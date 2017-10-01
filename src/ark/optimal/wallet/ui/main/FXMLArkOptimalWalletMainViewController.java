/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui.main;

import ark.optimal.wallet.pojo.Account;
import ark.optimal.wallet.ui.FXMLAccountViewController;
import ark.optimal.wallet.ui.FXMLDelegatesViewController;
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
    private AnchorPane sideAnchor;
    @FXML
    private JFXButton btnAccounts;
    @FXML
    private JFXButton btnDelegateView;
    @FXML
    private AnchorPane holderPane;

    private AnchorPane delegatesview, accountsview;
    @FXML
    private JFXButton goHomeBtn;
    @FXML
    private JFXButton settingsBtn;
    @FXML
    private JFXButton saveWalletBtn;
    @FXML
    private JFXButton loadWalletBtn;
    @FXML
    private JFXButton exportWalletBtn;

    public FXMLAccountViewController getAccountViewController() {
        return accountViewController;
    }

    public void setAccountViewController(FXMLAccountViewController accountViewController) {
        this.accountViewController = accountViewController;
    }

    public FXMLDelegatesViewController getDelegatesViewController() {
        return delegatesViewController;
    }

    public void setDelegatesViewController(FXMLDelegatesViewController delegatesViewController) {
        this.delegatesViewController = delegatesViewController;
    }
    @FXML
    private JFXButton btnHome;

    private FXMLHomeViewController homeViewController;
    private FXMLAccountViewController accountViewController;
    private FXMLDelegatesViewController delegatesViewController;

    public FXMLHomeViewController getHomeViewController() {
        return homeViewController;
    }

    public void setHomeViewController(FXMLHomeViewController homeViewController) {
        this.homeViewController = homeViewController;
    }

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
    private void onSettings(ActionEvent event) {
        homeViewController.showSettings();
    }

    private void createPages() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ark/optimal/wallet/ui/FXMLDelegatesView.fxml"));
            delegatesview = fxmlLoader.load();
            delegatesViewController = (FXMLDelegatesViewController) fxmlLoader.getController();
            delegatesViewController.setMainViewControler(this);
            fxmlLoader = new FXMLLoader(getClass().getResource("/ark/optimal/wallet/ui/FXMLAccountView.fxml"));
            accountsview = fxmlLoader.load();
            accountViewController = (FXMLAccountViewController) fxmlLoader.getController();
            accountViewController.setMainViewControler(this);

            //set up default node on page load
            setNode(accountsview);
        } catch (IOException ex) {
            Logger.getLogger(FXMLArkOptimalWalletMainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void setNode(Node node) {
        holderPane.getChildren().clear();
        holderPane.getChildren().add((Node) node);

        FadeTransition ft = new FadeTransition(Duration.millis(1000));
        ft.setNode(node);
        ft.setFromValue(0.1);
        ft.setToValue(1);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);
        ft.play();
    }

    @FXML
    private void onHome(ActionEvent event) {
        homeViewController.viewHome();
    }
    public void clearAccountsMenu(){
        accountViewController.clearAccountsMenu();
    }

    public void runImportAccount() {
        accountViewController.runImportAccount();
    }

    public void runCreateAccount() {
        accountViewController.runCreateAccount();
    }

    public void runWatchAccount() {
        accountViewController.runWatchAccount();
    }

    public void selectAccount(Account account) {
        accountViewController.selectAccount(account);
    }

    public void addToUserAccountsMenu(Account account) {
        accountViewController.addToUserAccountsMenu(account);
    }

    public void addToWatchAccountsMenu(Account account) {
        accountViewController.addToWatchAccountsMenu(account);
    }

    @FXML
    private void onViewHome(ActionEvent event) {
        homeViewController.viewHome();
    }
    public void viewHome(){
        homeViewController.viewHome();
    }

    @FXML
    private void onSaveWallet(ActionEvent event) {
        homeViewController.saveWallet();
    }

    @FXML
    private void onLoadWallet(ActionEvent event) {
        homeViewController.loadWallet();
    }

    @FXML
    private void onExportWallet(ActionEvent event) {
        homeViewController.exportWallet();
    }

    public void removeAccountFromHome(Account account) {
        homeViewController.removeAccount(account);
    }

}
