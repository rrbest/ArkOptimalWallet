/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Mastadon
 */
public class AlertController {

    public AlertController() {
    }
    
    public void alertUser(String message){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLAlertView.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            FXMLAlertViewController alertViewController = (FXMLAlertViewController) fxmlLoader.getController();
            alertViewController.setMessage(message);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("C");
            stage.setScene(new Scene(root1));
            stage.show();

        } catch (IOException ex) {
            Logger.getLogger(FXMLAccountsViewMenuController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void successMessage(String message){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLAlertSuccessView.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            FXMLAlertViewController alertViewController = (FXMLAlertViewController) fxmlLoader.getController();
            alertViewController.setMessage(message);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("C");
            stage.setScene(new Scene(root1));
            stage.show();

        } catch (IOException ex) {
            Logger.getLogger(FXMLAccountsViewMenuController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
