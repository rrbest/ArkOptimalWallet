/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import com.jfoenix.controls.JFXButton;
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
public class FXMLSendArkConfirmationViewController implements Initializable {

    @FXML
    private Label sendArkTitle;
    @FXML
    private JFXButton send;
    @FXML
    private JFXButton accountCreateAccountCancel;
    @FXML
    private Label transactionIDLabel;
    @FXML
    private Label transactionFromLabel;
    @FXML
    private Label transactionToLabel;
    @FXML
    private Label transactionAmountLabel;
    @FXML
    private Label transactionFeeLabel;

    private Transaction tx;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void onSend(ActionEvent event) {
        TransactionService.broadcastTransaction(this.tx);
        new AlertController().successMessage("Transaction Sent. Please give it time to be confirmed");
        closeWindow();;
    }

    @FXML
    private void onSendArkCancel(ActionEvent event) {
        closeWindow();
    }

    void setTransaction(Transaction tx) {
        this.tx = tx;
        transactionIDLabel.setText(tx.getId());
        transactionFromLabel.setText(tx.getSenderId());
        transactionToLabel.setText(tx.getRecipientId());
        transactionAmountLabel.setText(new Double(tx.getAmount()/100000000.0).toString());
        transactionFeeLabel.setText(new Double(tx.getFee()/100000000.0).toString());
        
    }

    private void closeWindow() {
        Stage stage = (Stage) send.getScene().getWindow();
        stage.close();
    }

}
