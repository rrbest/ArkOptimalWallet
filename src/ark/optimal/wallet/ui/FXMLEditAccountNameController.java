/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import ark.optimal.wallet.pojo.Account;
import ark.optimal.wallet.services.storageservices.StorageService;
import ark.optimal.wallet.ui.FXMLAccountsViewMenuController.AccountItem;
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
public class FXMLEditAccountNameController implements Initializable {

    @FXML
    private JFXTextField accountLabel;
    @FXML
    private JFXButton btnAccountLabel;
    @FXML
    private JFXButton AccountLabelCancel;

    private FXMLAccountsViewMenuController accountMenuController;

    private Account account;

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
    private void onChangeAccountLabel(ActionEvent event) {
        Account account = StorageService.getInstance().checkIfAccountExistByAddress(this.account.getAddress());
        account.setUsername(accountLabel.getText());
        this.account.setUsername(accountLabel.getText());
        if (StorageService.getInstance().getWallet().getUserAccounts().get(account.getAddress()) != null) {
            accountMenuController.updateMyAccounts(account);
        } 
        
        closeWindow();
    }

    @FXML
    private void onAccountLabelCancel(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) accountLabel.getScene().getWindow();
        stage.close();
    }

    public void setAccount(AccountItem accountItem) {
        Account account = StorageService.getInstance().getWallet().getUserAccounts().get(accountItem.getAddress());
        accountLabel.setText(account.getUsername());
        this.account = account;
    }

    void setAccount(Account account) {
        accountLabel.setText(account.getUsername());
        this.account = account;
    }
}
