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
public class FXMLWatchAccountController implements Initializable {

    @FXML
    private JFXTextField accountAddress;
    @FXML
    private JFXButton AccountCancel;

    private FXMLAccountsViewMenuController accountMenuController;
    @FXML
    private JFXButton accountWatchAccount;


    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void onWatchAccount(ActionEvent event) {
        
        Account account = StorageService.getInstance().checkIfAccountExistByAddress(accountAddress.getText());
        if (account == null){
            account = AccountService.getFullAccount(accountAddress.getText());
        }
        accountMenuController.updateWatchAccounts(account);
        closeWindow();
    }

    @FXML
    private void accountWatchCancel(ActionEvent event) {
        closeWindow();
    }
    
    public void setAccountMenuController(FXMLAccountsViewMenuController accountMenuController) {
        this.accountMenuController = accountMenuController;
    }
    
    private void closeWindow() {
        Stage stage = (Stage) accountAddress.getScene().getWindow();
        stage.close();
    }


}
