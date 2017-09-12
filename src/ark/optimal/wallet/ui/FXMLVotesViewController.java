/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import ark.optimal.wallet.pojo.Account;
import ark.optimal.wallet.pojo.Delegate;
import ark.optimal.wallet.services.accountservices.AccountService;
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
    private TableColumn<VoteItem, String> delegateName;
    @FXML
    private TableColumn<VoteItem, Integer> delegateRank;
    @FXML
    private TableColumn<VoteItem, Hyperlink> subwalletAddress;
    @FXML
    private TableColumn<VoteItem, Integer> votes;
    @FXML
    private TableColumn<VoteItem, Double> payoutpercentage;
    @FXML
    private TableColumn<VoteItem, Double> minpayout;
    @FXML
    private TableView<VoteItem> votedDelegatesTable;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        delegateName.setCellValueFactory(new PropertyValueFactory<VoteItem, String>("delegateName"));
        delegateName.setCellFactory(new FXMLVotesViewController.ColumnFormatter<VoteItem, String>());

        delegateRank.setCellValueFactory(new PropertyValueFactory<VoteItem, Integer>("delegateRank"));
        delegateRank.setCellFactory(new FXMLVotesViewController.ColumnFormatter<VoteItem, Integer>());

        votes.setCellValueFactory(new PropertyValueFactory<VoteItem, Integer>("votes"));
        votes.setCellFactory(new FXMLVotesViewController.ColumnFormatter<VoteItem, Integer>());

        payoutpercentage.setCellValueFactory(new PropertyValueFactory<VoteItem, Double>("payoutpercentage"));
        payoutpercentage.setCellFactory(new FXMLVotesViewController.ColumnFormatter<VoteItem, Double>());

        minpayout.setCellValueFactory(new PropertyValueFactory<VoteItem, Double>("minpayout"));
        minpayout.setCellFactory(new FXMLVotesViewController.ColumnFormatter<VoteItem, Double>());

        subwalletAddress.setCellValueFactory(new PropertyValueFactory<VoteItem, Hyperlink>("subwalletAddress_link"));
        subwalletAddress.setCellFactory(new FXMLVotesViewController.HyperlinkCell());

    }

    public void setAccountsViewController(FXMLAccountViewController accountViewController) {
        this.accountViewController = accountViewController;
    }

    public void viewVotes(Account account) {
        votedDelegatesTable.getItems().clear();
       
        for (String delegateName : account.getSubAccounts().keySet()) {
            Delegate d = AccountService.getDelegateByUsername(delegateName);
            Account sub = account.getSubAccounts().get(delegateName);
            if (sub.getVotedDelegates().size()  == 0)
            {   
                sub = AccountService.getFullAccount(sub.getAddress());
            }
            sub.setUsername(account.getUsername() + "(" + delegateName + ")");
            
            d.setChecked(Boolean.TRUE);
            if (sub.getVotedDelegates().size()  > 0)
                sub.getVotedDelegates().set(0, d);
            VoteItem vi = new VoteItem(delegateName, d.getRate(), sub.getUsername(), sub.getBalance().intValue(), 80.0, 100.0);
            votedDelegatesTable.getItems().add(vi);
            
            
            
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
                                u = new URI(item.getText());
                                java.awt.Desktop.getDesktop().browse(u);
                            } catch (URISyntaxException ex) {
                                Logger.getLogger(FXMLAccountViewController.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
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
