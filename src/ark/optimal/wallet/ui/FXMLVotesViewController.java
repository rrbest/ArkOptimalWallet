/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import ark.optimal.wallet.pojo.Account;
import ark.optimal.wallet.pojo.Delegate;
import ark.optimal.wallet.services.accountservices.AccountService;
import ark.optimal.wallet.services.storageservices.StorageService;
import ark.optimal.wallet.ui.main.HostServicesProvider;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * FXML Controller class
 *
 * @author Mastadon
 */
public class FXMLVotesViewController implements Initializable {

    private FXMLAccountViewController accountViewController;

    private Account account;

    @FXML
    private TableView<VoteItem> masterVotedDelegateTable;
    @FXML
    private TableColumn<VoteItem, String> masterDelegateName;
    @FXML
    private TableColumn<VoteItem, Integer> masterDelegateRank;
    @FXML
    private TableColumn<VoteItem, Hyperlink> masterAddress;
    @FXML
    private TableColumn<VoteItem, Integer> masterVotes;
    @FXML
    private TableColumn<VoteItem, Double> masterPayoutPercentage;
    @FXML
    private TableView<VoteItem> SubWalletsVotedDelegateTable;
    @FXML
    private TableColumn<VoteItem, String> subWalletDelegateName;
    @FXML
    private TableColumn<VoteItem, Integer> subWalletDelegateRank;
    @FXML
    private TableColumn<VoteItem, Hyperlink> subWalletAddress;
    @FXML
    private TableColumn<VoteItem, Integer> subWalletVotes;
    @FXML
    private TableColumn<VoteItem, Double> subWalletPayoutPercentage;
    @FXML
    private TableColumn<VoteItem, Integer> masterExcludedVotes;
    @FXML
    private TableColumn<VoteItem, Integer> subWalletExcludedVotes;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        masterVotedDelegateTable.setPlaceholder(new Label(" "));

        masterDelegateName.setCellValueFactory(new PropertyValueFactory<VoteItem, String>("delegateName"));
        masterDelegateName.setCellFactory(new FXMLVotesViewController.ColumnFormatter<VoteItem, String>());

        masterDelegateRank.setCellValueFactory(new PropertyValueFactory<VoteItem, Integer>("delegateRank"));
        masterDelegateRank.setCellFactory(new FXMLVotesViewController.ColumnFormatter<VoteItem, Integer>());

        masterVotes.setCellValueFactory(new PropertyValueFactory<VoteItem, Integer>("votes"));
        masterVotes.setCellFactory(new FXMLVotesViewController.ColumnFormatter<VoteItem, Integer>());

        masterPayoutPercentage.setCellValueFactory(new PropertyValueFactory<VoteItem, Double>("payoutpercentage"));
        masterPayoutPercentage.setCellFactory(new FXMLVotesViewController.ColumnFormatter<VoteItem, Double>());

        masterExcludedVotes.setCellValueFactory(new PropertyValueFactory<VoteItem, Integer>("excludedVotes"));
        masterExcludedVotes.setCellFactory(new FXMLVotesViewController.ColumnFormatter<VoteItem, Integer>());

        masterAddress.setCellValueFactory(new PropertyValueFactory<VoteItem, Hyperlink>("address_link"));
        masterAddress.setCellFactory(new FXMLVotesViewController.HyperlinkCell());

        SubWalletsVotedDelegateTable.setPlaceholder(new Label(" "));

        subWalletDelegateName.setCellValueFactory(new PropertyValueFactory<VoteItem, String>("delegateName"));
        subWalletDelegateName.setCellFactory(new FXMLVotesViewController.ColumnFormatter<VoteItem, String>());

        subWalletDelegateRank.setCellValueFactory(new PropertyValueFactory<VoteItem, Integer>("delegateRank"));
        subWalletDelegateRank.setCellFactory(new FXMLVotesViewController.ColumnFormatter<VoteItem, Integer>());

        subWalletVotes.setCellValueFactory(new PropertyValueFactory<VoteItem, Integer>("votes"));
        subWalletVotes.setCellFactory(new FXMLVotesViewController.ColumnFormatter<VoteItem, Integer>());

        subWalletPayoutPercentage.setCellValueFactory(new PropertyValueFactory<VoteItem, Double>("payoutpercentage"));
        subWalletPayoutPercentage.setCellFactory(new FXMLVotesViewController.ColumnFormatter<VoteItem, Double>());

        subWalletExcludedVotes.setCellValueFactory(new PropertyValueFactory<VoteItem, Integer>("excludedVotes"));
        subWalletExcludedVotes.setCellFactory(new FXMLVotesViewController.ColumnFormatter<VoteItem, Integer>());

        subWalletAddress.setCellValueFactory(new PropertyValueFactory<VoteItem, Hyperlink>("address_link"));
        subWalletAddress.setCellFactory(new FXMLVotesViewController.HyperlinkCell());

    }

    public void setAccountsViewController(FXMLAccountViewController accountViewController) {
        this.accountViewController = accountViewController;
    }

    public void viewVotes(Account account) {
        masterVotedDelegateTable.getItems().clear();
        if (account.getVotedDelegates().size() > 0) {
            Delegate d = account.getVotedDelegates().get(0);
            Delegate delegate = StorageService.getInstance().getWallet().getDelegates().get(d.getUsername());
            if (delegate != null) {
                d = delegate;
            } else {
                StorageService.getInstance().addDelegate(d, true);
            }
            VoteItem vi = new VoteItem(d.getUsername(), d.getRate(), account.getUsername(), account.getBalance().intValue(), d.getPayoutPercentage(), d.getExcludedVotes());
            masterVotedDelegateTable.getItems().add(vi);
        }
        SubWalletsVotedDelegateTable.getItems().clear();

        for (String delegateName : account.getSubAccounts().keySet()) {
            Delegate delegate = StorageService.getInstance().getWallet().getDelegates().get(delegateName);
            Account sub = account.getSubAccounts().get(delegateName);
            if (sub.getVotedDelegates().size() == 0) {
                sub = AccountService.getFullAccount(sub.getAddress());
            }
            sub.setUsername(account.getUsername() + "(" + delegateName + ")");

            //d.setChecked(Boolean.TRUE);
            if (sub.getVotedDelegates().size() > 0) {
                if (delegate != null) {
                    sub.getVotedDelegates().set(0, delegate);
                }
                delegate = sub.getVotedDelegates().get(0);
                VoteItem vi = new VoteItem(delegateName, delegate.getRate(), sub.getUsername(), sub.getBalance().intValue(), delegate.getPayoutPercentage(), delegate.getExcludedVotes());
                SubWalletsVotedDelegateTable.getItems().add(vi);

            }

        }
    }

    private class HyperlinkCell implements Callback<TableColumn<VoteItem, Hyperlink>, TableCell<VoteItem, Hyperlink>> {

        @Override
        public TableCell<VoteItem, Hyperlink> call(TableColumn<VoteItem, Hyperlink> arg) {
            TableCell<VoteItem, Hyperlink> cell = new TableCell<VoteItem, Hyperlink>() {
                @Override
                protected void updateItem(Hyperlink item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setGraphic(null);
                    } else {

                        setGraphic(item);

                        item.setOnAction(t -> {
                            URI u;
                            try {
                                HostServicesProvider.getInstance().getHostServices().showDocument("https://explorer.ark.io/");
                            } catch (Exception ex) {
                                Logger.getLogger(FXMLAccountViewController.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        });
                    }

                }
            };
            return cell;
        }
    }

    private class ColumnFormatter<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

        private final DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm");

        public ColumnFormatter() {
            super();
        }

        @Override
        public TableCell<S, T> call(TableColumn<S, T> arg0) {
            return new TableCell<S, T>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setGraphic(null);
                    } else {
                        String val = item.toString();
                        if (item instanceof DateTime) {
                            DateTime ld = (DateTime) item;
                            val = formatter.print(ld);

                        }
                        if (item instanceof Hyperlink) {

                        }
                        setGraphic(new Label(val));
                        setAlignment(Pos.CENTER);
                    }
                }
            };
        }
    }

}
