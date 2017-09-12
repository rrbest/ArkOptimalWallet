/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import io.ark.core.Transaction;
import io.ark.core.TransactionService;
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
        Transaction tx = TransactionService.createTransaction(this.senderAddress,destinationAddress.getText(), new Long(amountArk.getText()), smartBridge.getText(), passphrase.getText());
        TransactionService.PostTransaction(tx);
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
