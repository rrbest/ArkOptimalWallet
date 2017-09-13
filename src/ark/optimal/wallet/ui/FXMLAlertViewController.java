/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

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
public class FXMLAlertViewController implements Initializable {

    @FXML
    private Label message;
    @FXML
    private JFXButton ok;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
    }    

    @FXML
    private void onOk(ActionEvent event) {
        closeWindow();
    }
    
        private void closeWindow() {
        Stage stage = (Stage) ok.getScene().getWindow();
        stage.close();
    }

    void setMessage(String message) {
        this.message.setText(message);
    }
}
