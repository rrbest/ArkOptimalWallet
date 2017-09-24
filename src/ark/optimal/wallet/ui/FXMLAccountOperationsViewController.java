/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import ark.optimal.wallet.pojo.Account;
import ark.optimal.wallet.services.storageservices.StorageService;
import ark.optimal.wallet.ui.main.HostServicesProvider;
import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author Mastadon
 */
public class FXMLAccountOperationsViewController implements Initializable {

    @FXML
    private JFXButton openExplorerBtn;
    @FXML
    private JFXButton removeBtn;
    @FXML
    private Label accountAddressLabel;

    private Account account;
    private FXMLAccountViewController accountViewController;
    @FXML
    private JFXButton editLabelBtn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

    }

    @FXML
    private void onOpenExplorer(ActionEvent event) {
        try {
            HostServicesProvider.getInstance().getHostServices().showDocument("https://explorer.ark.io/address/" + account.getAddress());
        } catch (Exception ex) {
            Logger.getLogger(FXMLAccountViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void onRemoveAccount(ActionEvent event) {
        
          try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLRemoveAccountView.fxml"));
                Parent root1 = (Parent) fxmlLoader.load();
                FXMLRemoveAccountViewController removeController = (FXMLRemoveAccountViewController) fxmlLoader.getController();
                removeController.setAccountMenuController(this.accountViewController.getMenuController());
                removeController.setAccount(this.account);
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setTitle("C");
                stage.setScene(new Scene(root1));
                stage.show();
            } catch (IOException ex) {
                Logger.getLogger(FXMLAccountsViewMenuController.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

    void setAccountViewController(FXMLAccountViewController accountViewController) {
        this.accountViewController = accountViewController;
    }

    void setAccount(Account account) {
        this.account = account;
        this.accountAddressLabel.setText(account.getAddress());
        if(StorageService.getInstance().getWallet().getUserAccounts().get(account.getAddress()) == null){
            editLabelBtn.setVisible(false);
        }else{
            editLabelBtn.setVisible(true);
        }
    }

    @FXML
    private void onEditLabel(ActionEvent event) {
        try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLEditAccountName.fxml"));
                Parent root1 = (Parent) fxmlLoader.load();
                FXMLEditAccountNameController editctrlr = (FXMLEditAccountNameController) fxmlLoader.getController();
                editctrlr.setAccountMenuController(this.accountViewController.getMenuController());
                editctrlr.setAccount(this.account);
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.setTitle("C");
                stage.setScene(new Scene(root1));
                stage.show();
            } catch (IOException ex) {
                Logger.getLogger(FXMLAccountsViewMenuController.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

}
