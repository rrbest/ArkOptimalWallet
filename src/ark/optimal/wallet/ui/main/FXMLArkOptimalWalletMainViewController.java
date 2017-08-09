/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui.main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToolbar;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author Mastadon
 */
public class FXMLArkOptimalWalletMainViewController implements Initializable {

    @FXML
    private JFXToolbar toolBar;
    @FXML
    private HBox toolBarRight;
    @FXML
    private Label lblMenu;
    @FXML
    private AnchorPane sideAnchor;
    @FXML
    private JFXButton btnAccounts;
    @FXML
    private JFXButton btnDelegateView;
    @FXML
    private JFXButton btnProxyVoting;
    @FXML
    private JFXButton btnSettings;
    @FXML
    private AnchorPane holderPane;

    private AnchorPane delegatesview, accountsview, proxyvotingview, settingsview;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        createPages();
    }    


    @FXML
    private void onAccounts(ActionEvent event) {
       setNode(accountsview);

    }

    @FXML
    private void onDelegateView(ActionEvent event) {
        setNode(delegatesview);
    }

    @FXML
    private void onProxyVotingView(ActionEvent event) {
    }

    @FXML
    private void onSettings(ActionEvent event) {
    }
    
    private void createPages() {
        try {
            delegatesview = FXMLLoader.load(getClass().getResource("/ark/optimal/wallet/ui/FXMLDelegateView.fxml"));
            accountsview = FXMLLoader.load(getClass().getResource("/ark/optimal/wallet/ui/FXMLAccountView.fxml"));
            //set up default node on page load
            setNode(accountsview);
        } catch (IOException ex) {
            Logger.getLogger(FXMLArkOptimalWalletMainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    private void updateDelegateView(String name){
        
    }
    
    private void setNode(Node node) {
        holderPane.getChildren().clear();
        holderPane.getChildren().add((Node) node);

        FadeTransition ft = new FadeTransition(Duration.millis(1500));
        ft.setNode(node);
        ft.setFromValue(0.1);
        ft.setToValue(1);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);
        ft.play();
    }
}
