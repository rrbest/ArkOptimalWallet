/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import ark.optimal.wallet.services.accountservices.AccountService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import io.ark.core.Crypto;
import io.ark.core.Transaction;
import io.ark.core.TransactionService;
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
public class FXMLSendArkViewController implements Initializable {

    @FXML
    private JFXButton sendArkNext;
    @FXML
    private JFXButton accountCreateAccountCancel;
    @FXML
    private JFXTextField destinationAddress;
    @FXML
    private JFXTextField amountArk;
    @FXML
    private JFXTextField smartBridge;

    private FXMLAccountViewController accountViewController;
    @FXML
    private JFXTextField passphrase;
    @FXML
    private Label sendArkTitle;
    
    private String senderAddress;

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
        sendArkTitle.setText("Send Ark From "+ this.senderAddress);
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
    }    

    @FXML
    private void onSendArkNext(ActionEvent event) {
        
        String senderByPassphrase = AccountService.getAddress(passphrase.getText());
        if(!senderByPassphrase.equals(this.senderAddress))
        {
            new AlertController().alertUser("Passphrase is not corresponding to account");
            closeWindow();
            return;
        }
         try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLSendArkConfirmationView.fxml"));
                Parent root1 = (Parent) fxmlLoader.load();
                FXMLSendArkConfirmationViewController sendArkConfController = (FXMLSendArkConfirmationViewController) fxmlLoader.getController();
                Transaction tx = TransactionService.createTransaction(this.senderAddress,destinationAddress.getText(), new Long(amountArk.getText()), smartBridge.getText(), passphrase.getText());
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
    private void onSendArkCancel(ActionEvent event) {
        closeWindow();
    }

    void setAccountMenuController(FXMLAccountViewController accountViewController) {
        this.accountViewController = accountViewController;
    }
     private void closeWindow(){
        Stage stage = (Stage) sendArkNext.getScene().getWindow();
        stage.close();
    }
}
