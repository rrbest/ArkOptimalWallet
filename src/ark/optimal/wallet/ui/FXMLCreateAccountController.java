/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import ark.optimal.wallet.pojo.Account;
import ark.optimal.wallet.services.accountservices.AccountService;
import ark.optimal.wallet.services.storageservices.StorageService;
import com.eclipsesource.v8.JavaCallback;
import com.eclipsesource.v8.NodeJS;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Function;
import com.eclipsesource.v8.V8Object;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import io.ark.core.Crypto;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Mastadon
 */
public class FXMLCreateAccountController implements Initializable {

    @FXML
    private JFXTextField accountPassphrase;
    @FXML
    private JFXButton accountCreateAccountCancel;
    @FXML
    private JFXButton accountCreateAccount;
    @FXML
    private JFXTextField accountPassphraseLabel;

    private FXMLAccountsViewMenuController accountMenuController;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        String passphrase = Crypto.generatePassphrase();
        accountPassphraseLabel.setText(passphrase);
    }


    @FXML
    private void accountCreateAccountCancel(ActionEvent event) {
        closeWindow();
    }

    @FXML
    private void OnCreateAccount(ActionEvent event) {
        Account account = StorageService.getInstance().checkIfAccountExistByPassphrase(accountPassphrase.getText());
        if (account == null){
            account = AccountService.createAccount(accountPassphrase.getText());
        }
        accountMenuController.updateMyAccounts(account);
        closeWindow();
    }

    public void setAccountMenuController(FXMLAccountsViewMenuController accountMenuController) {
        this.accountMenuController = accountMenuController;
    }

     private void closeWindow(){
        Stage stage = (Stage) accountPassphrase.getScene().getWindow();
        stage.close();
    }
}
