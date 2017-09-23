/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui.main;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author Mastadon
 */
public class FXMLHomeItemViewController implements Initializable {

    @FXML
    private Label accountNameLabel;
    @FXML
    private Label accountBalanceLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    void setAccountDetails(String username, double balance) {
        accountNameLabel.setText(username);
        accountBalanceLabel.setText("Ѧ" + Math.round(balance * 100.0) / 100.0);
    }
    
}
