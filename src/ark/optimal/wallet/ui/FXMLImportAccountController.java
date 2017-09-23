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
    private FXMLHomeViewController homeViewController;
    
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
        int counter = 0;
        while (account == null && ++counter<=10){
            account = AccountService.createAccount(accountPassphrase.getText());
        }
        if(account == null){
            new AlertController().alertUser("Network may be unavailable");
            return;
        }
        if(accountMenuController != null){
            accountMenuController.updateMyAccounts(account);
        }else if(homeViewController != null){
            homeViewController.updateMyAccounts(account);
        }
        
        
        closeWindow();
    }
    
    private void closeWindow(){
        Stage stage = (Stage) accountPassphrase.getScene().getWindow();
        stage.close();
    }

    public void setHomeViewController(FXMLHomeViewController homeViewController) {
        this.homeViewController = homeViewController;
    }

   
}
