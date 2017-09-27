/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import ark.optimal.wallet.pojo.Account;
import ark.optimal.wallet.services.accountservices.AccountService;
import ark.optimal.wallet.services.storageservices.StorageService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import io.ark.core.Transaction;
import io.ark.core.TransactionService;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author Mastadon
 */
public class FXMLUpdateVoteViewController implements Initializable {

    @FXML
    private JFXTextField passphrase;
    @FXML
    private JFXButton accountVoteNext;
    @FXML
    private JFXButton accountVoteCancel;

    private FXMLDelegatesViewController delegateViewController;
    @FXML
    private JFXTextField delegateName;

    @FXML
    private JFXComboBox<Account> accounts;
    @FXML
    private Label voteForLabel;

    public void setDelegateViewController(FXMLDelegatesViewController delegateViewController) {
        this.delegateViewController = delegateViewController;
    }

    public void setDelegateName(String dName) {
        delegateName.setText(dName);

    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        ObservableList<Account> userAccounts = FXCollections.observableArrayList();
        userAccounts.addAll(StorageService.getInstance().getWallet().getUserAccounts().values());
        accounts.setItems(userAccounts);

        accounts.setCellFactory(new Callback<ListView<Account>, ListCell<Account>>() {
            public ListCell<Account> call(ListView<Account> l) {
                return new ListCell<Account>() {
                    @Override
                    protected void updateItem(Account item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            setText(item.getUsername());
                        }
                    }
                };
            }
        });
        //selected value showed in combo box
        accounts.setConverter(new StringConverter<Account>() {
            public String toString(Account account) {
                if (account == null) {
                    return null;
                } else {
                    return account.getUsername();
                }
            }

            @Override
            public Account fromString(String username) {
                return null;
            }
        });

    }

    @FXML
    private void onAccountVoteNext(ActionEvent event) {
        Account account = accounts.getSelectionModel().getSelectedItem();
        //delegateViewController.optimize(account, passphrase.getText());

        if(account == null){
             new AlertController().alertUser("Please select master account");
             return;
        }
        if (passphrase.getText() == null || passphrase.getText().equals("")) {
            new AlertController().alertUser("Please enter master passphrase");
            closeWindow();
            return;
        }
        String senderByPassphrase = AccountService.getAddress(passphrase.getText());
        if (!senderByPassphrase.equals(account.getAddress())) {
            new AlertController().alertUser("Passphrase is not corresponding to account");
            closeWindow();
            return;
        }
        if (account.getVotedDelegates().size() > 0) {
            new AlertController().alertUser("Account Already voted.");
            closeWindow();
            return;
        }
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLSendArkConfirmationView.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            FXMLSendArkConfirmationViewController sendArkConfController = (FXMLSendArkConfirmationViewController) fxmlLoader.getController();
            Transaction tx = TransactionService.createVote(account.getAddress(), delegateName.getText(), passphrase.getText(), false);
            sendArkConfController.setTransaction(tx);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("C");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(FXMLAccountsViewMenuController.class.getName()).log(Level.SEVERE, null, ex);
        }
        closeWindow();

    }

    @FXML
    private void onAccountVoteCancel(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) passphrase.getScene().getWindow();
        stage.close();
    }
}
