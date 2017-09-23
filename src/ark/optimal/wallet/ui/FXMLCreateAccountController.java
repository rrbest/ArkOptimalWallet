/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import ark.optimal.wallet.pojo.Account;
import ark.optimal.wallet.services.accountservices.AccountService;
import ark.optimal.wallet.services.storageservices.StorageService;
import ark.optimal.wallet.ui.main.FXMLHomeViewController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import io.ark.core.Crypto;
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
public class FXMLCreateAccountController implements Initializable {

    @FXML
    private JFXTextField accountPassphrase;
    @FXML
    private JFXTextField accountPassphraseLabel;

    private FXMLAccountsViewMenuController accountMenuController;
    @FXML
    private JFXButton accountCreateAccount;
    @FXML
    private JFXButton accountCreateAccountCancel;
    private FXMLHomeViewController homeViewController;

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

    public void setAccountMenuController(FXMLAccountsViewMenuController accountMenuController) {
        this.accountMenuController = accountMenuController;
    }

     private void closeWindow(){
        Stage stage = (Stage) accountPassphrase.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void onCreateAccount(ActionEvent event) {
        Account account = StorageService.getInstance().checkIfAccountExistByPassphrase(accountPassphrase.getText());
        if (account == null){
            account = AccountService.createAccount(accountPassphrase.getText());
        }
        if(accountMenuController != null){
            accountMenuController.updateMyAccounts(account);
        }else if(homeViewController!= null){
            homeViewController.updateMyAccounts(account);
        }
        
        closeWindow();
    }

    public void setHomeViewController(FXMLHomeViewController homeViewController) {
        this.homeViewController = homeViewController;
    }

}
