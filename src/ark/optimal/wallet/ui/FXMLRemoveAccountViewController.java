/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import ark.optimal.wallet.pojo.Account;
import ark.optimal.wallet.services.storageservices.StorageService;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Mastadon
 */
public class FXMLRemoveAccountViewController implements Initializable {

    @FXML
    private Label removeLabel;
    @FXML
    private JFXButton removeBtn;
    @FXML
    private JFXButton cancel;
    private Account account;
    private FXMLAccountsViewMenuController accountMenuController;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void onRemoveAccount(ActionEvent event) {
        if (account == null) {
            return;
        }
        StorageService.getInstance().removeAccount(account);
        accountMenuController.removeAccount(account);
        closeWindow();
    }

    @FXML
    private void onCancel(ActionEvent event) {
        closeWindow();
    }

    void setAccountMenuController(FXMLAccountsViewMenuController menuController) {
        this.accountMenuController = menuController;
    }

    void setAccount(Account account) {
        this.account = account;
        removeLabel.setText("Remove Account " + account.getAddress());
    }

    private void closeWindow() {
        Stage stage = (Stage) removeBtn.getScene().getWindow();
        stage.close();
    }

}
