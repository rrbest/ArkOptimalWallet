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
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import io.ark.core.Transaction;
import io.ark.core.TransactionService;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * FXML Controller class
 *
 * @author Mastadon
 */
public class FXMLSendToMasterViewController implements Initializable {

    @FXML
    private Label sendToMasteLabel;
    @FXML
    private JFXButton sendToMaster;
    @FXML
    private JFXButton sendToMasterCancel;
    @FXML
    private TableView<SendToMasterItem> subwalletsTable;
    @FXML
    private TableColumn<SendToMasterItem, Hyperlink> subwalletAddress;
    @FXML
    private TableColumn<SendToMasterItem, Integer> votes;

    private FXMLAccountsViewMenuController accountViewMenuController;
    @FXML
    private JFXTextField passphrase;
    private Account account;

    public void setAccountViewMenuController(FXMLAccountsViewMenuController accountViewMenuController) {
        this.accountViewMenuController = accountViewMenuController;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        votes.setCellValueFactory(new PropertyValueFactory<SendToMasterItem, Integer>("votes"));
        votes.setCellFactory(new FXMLSendToMasterViewController.ColumnFormatter<SendToMasterItem, Integer>());

        subwalletAddress.setCellValueFactory(new PropertyValueFactory<SendToMasterItem, Hyperlink>("subwalletAddress_link"));
        subwalletAddress.setCellFactory(new FXMLSendToMasterViewController.HyperlinkCell());

    }

    public void updateSubWalletsTable(Account account) {
        sendToMasteLabel.setText("Send To Master - " + account.getUsername());
        this.account = account;
        subwalletsTable.getItems().clear();
        for (Account sub : account.getSubAccounts().values()) {
            String subUsername = sub.getUsername();
            Delegate d = sub.getVotedDelegates().get(0);
            SendToMasterItem oi = new SendToMasterItem(sub.getUsername(), sub.getBalance().intValue());
            subwalletsTable.getItems().add(oi);
        }
    }

    @FXML
    private void onSendToMaster(ActionEvent event) {
        for (Map.Entry<String, Account> entry : this.account.getSubAccounts().entrySet()) {
            Account subAccount = entry.getValue();
            Delegate d = subAccount.getVotedDelegates().get(0);
            account.getSubAccounts().put(entry.getKey(), subAccount);
            if (subAccount.getBalance() > 1.2) {
                Transaction tx = TransactionService.createTransaction(subAccount.getAddress(), account.getAddress(), new Long(subAccount.getBalance().intValue() - 1), "User sent SubWallet to Master", passphrase.getText() + " " + d.getUsername());
                TransactionService.PostTransaction(tx);
            }
        }
        closeWindow();
    }

    @FXML
    private void onSendToMasterCancel(ActionEvent event) {
        closeWindow();

    }

    private void closeWindow() {
        Stage stage = (Stage) sendToMaster.getScene().getWindow();
        stage.close();
    }

    private class HyperlinkCell implements Callback<TableColumn<SendToMasterItem, Hyperlink>, TableCell<SendToMasterItem, Hyperlink>> {

        @Override
        public TableCell<SendToMasterItem, Hyperlink> call(TableColumn<SendToMasterItem, Hyperlink> arg) {
            TableCell<SendToMasterItem, Hyperlink> cell = new TableCell<SendToMasterItem, Hyperlink>() {
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
                                Logger.getLogger(FXMLSendToMasterViewController.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(FXMLSendToMasterViewController.class.getName()).log(Level.SEVERE, null, ex);
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
