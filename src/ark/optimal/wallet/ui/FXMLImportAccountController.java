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
import com.jfoenix.controls.JFXTextField;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Mastadon
 */
public class FXMLImportAccountController implements Initializable {

    @FXML
    private JFXTextField accountPassphrase;

    private FXMLAccountsViewMenuController accountMenuController;
    @FXML
    private JFXButton accountImportAccount;
    @FXML
    private JFXButton accountImportAccountCancel;

    public void setAccountMenuController(FXMLAccountsViewMenuController accountMenuController) {
        this.accountMenuController = accountMenuController;
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    


    @FXML
    private void accountImportAccountCancel(ActionEvent event) {
        closeWindow();
    }

    @FXML
    private void OnImportAccount(ActionEvent event) {
        Account account = StorageService.getInstance().checkIfAccountExistByPassphrase(accountPassphrase.getText());
        if (account == null){
            account = AccountService.createAccount(accountPassphrase.getText());
        }
        accountMenuController.updateMyAccounts(account);
        closeWindow();
    }
    
    private void closeWindow(){
        Stage stage = (Stage) accountPassphrase.getScene().getWindow();
        stage.close();
    }

   
}
