/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui.main;

import ark.optimal.wallet.pojo.Account;
import ark.optimal.wallet.services.storageservices.StorageService;
import ark.optimal.wallet.services.storageservices.Wallet;
import ark.optimal.wallet.ui.AlertController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import static javafx.scene.layout.Region.USE_PREF_SIZE;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author Mastadon
 */
public class FXMLHomeViewController implements Initializable {

    @FXML
    private Label myaccountstitle;
    @FXML
    private JFXListView<AccountItem> homeAccounts;
    @FXML
    private JFXButton btnCreateAccount;
    @FXML
    private Tooltip delegateAddressTooltip1;
    @FXML
    private JFXButton btnImportAccount;
    @FXML
    private Tooltip delegateAddressTooltip;

    private FXMLArkOptimalWalletMainViewController mainController;
    private AnchorPane homeview, mainview;
    private ArkOptimalWallet arkWalletApp;
    @FXML
    private JFXButton goHomeBtn;
    @FXML
    private AnchorPane settings;
    @FXML
    private JFXButton settingsBtn;
    @FXML
    private JFXButton saveWalletBtn;
    @FXML
    private JFXButton loadWalletBtn;
    @FXML
    private JFXButton exportWalletBtn;
    @FXML
    private JFXButton removeMasterWalletBtn;

    public void setHomeview(AnchorPane homeview) {
        this.homeview = homeview;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Wallet wallet = StorageService.getInstance().loadWallet();
            // TODO
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLArkOptimalWalletMainView.fxml"));
            mainview = fxmlLoader.load();
            mainController = (FXMLArkOptimalWalletMainViewController) fxmlLoader.getController();
            mainController.setHomeViewController(this);

            homeAccounts.setEditable(true);
            homeAccounts.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            homeAccounts.setCellFactory(myAcountsListView -> new JFXListCell<FXMLHomeViewController.AccountItem>() {
                public void updateItem(FXMLHomeViewController.AccountItem account, boolean empty) {
                    super.updateItem(account, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        FontAwesomeIconView accountImage = new FontAwesomeIconView();
                        accountImage.setGlyphName("BANK");
                        accountImage.setFill(Paint.valueOf("#404bb6"));
                        accountImage.setSize("16px");
                        setText(account.getUsername());
                        setGraphic(accountImage);
                        setPrefWidth(USE_PREF_SIZE);
                    }
                }

            });
            if (wallet != null) {
                for (String address : wallet.getUserAccounts().keySet()) {
                    // Account account = AccountService.getFullAccount(address);
                    Account account = wallet.getUserAccounts().get(address);
                    //wallet.getUserAccounts().put(address, account);
                    addToMyAccounts(account);
                }
                for (String address : wallet.getWatchAccounts().keySet()) {
                    // Account account = AccountService.getFullAccount(address);
                    Account account = wallet.getUserAccounts().get(address);
                    //wallet.getUserAccounts().put(address, account);
                    addToWatchAccounts(account);
                }
                //StorageService.getInstance().updateWallet();

            }
            homeAccounts.setPrefHeight(homeAccounts.getItems().size() * 40);
            ((VBox) homeAccounts.getParent()).setPrefHeight(40 + homeAccounts.getItems().size() * 40);
        } catch (IOException ex) {
            Logger.getLogger(FXMLHomeViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void addToMyAccounts(Account account) {
        if (!StorageService.getInstance().getWallet().getUserAccounts().containsKey(account.getAddress())) {
            StorageService.getInstance().addAccountToUserAccounts(account, true);
        }
        homeAccounts.getItems().add(new FXMLHomeViewController.AccountItem(account.getUsername(), account.getAddress()));
        //homeAccounts.requestFocus();
        //homeAccounts.getSelectionModel().select(new FXMLHomeViewController.AccountItem(account.getUsername(), account.getAddress()));
        homeAccounts.refresh();
        homeAccounts.setPrefHeight(homeAccounts.getItems().size() * 40);
        ((VBox) homeAccounts.getParent()).setPrefHeight(40 + homeAccounts.getItems().size() * 40);

        mainController.addToUserAccountsMenu(account);
    }

    private void addToWatchAccounts(Account account) {
        mainController.addToWatchAccountsMenu(account);
    }

    @FXML
    private void onCreateAccount(ActionEvent event) {
        mainController.runCreateAccount();
        this.arkWalletApp.view(mainview);

    }

    @FXML
    private void onImportAccount(ActionEvent event) {
        mainController.runImportAccount();
        this.arkWalletApp.view(mainview);
    }

    private void onWatchAccount(ActionEvent event) {
        mainController.runWatchAccount();
        this.arkWalletApp.view(mainview);
    }

    public void viewHome() {
        homeAccounts.getItems().clear();
        for (String address : StorageService.getInstance().getWallet().getUserAccounts().keySet()) {
            Account account = StorageService.getInstance().getWallet().getUserAccounts().get(address);
            addToMyAccounts(account);
        }
        this.arkWalletApp.view(homeview);

    }

    public void setAppController(ArkOptimalWallet app) {
        this.arkWalletApp = app;
    }

    public ArkOptimalWallet getAppController() {
        return this.arkWalletApp;
    }

    @FXML
    private void handleUserAccountMouseClick(MouseEvent event) {
        String address = homeAccounts.getSelectionModel().getSelectedItem().getAddress();
        Account account = StorageService.getInstance().getWallet().getUserAccounts().get(address);
        this.arkWalletApp.view(mainview);
        mainController.selectAccount(account);
    }

    @FXML
    private void onViewHome(ActionEvent event) {
    }

    @FXML
    private void onSettings(ActionEvent event) {
        showSettings();

    }

    public void showSettings() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLSettingsView.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            FXMLSettingsViewController settingController = (FXMLSettingsViewController) fxmlLoader.getController();
            settingController.setHomeViewController(this);
            Stage stage = new Stage();
            settingController.setStage(stage);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("C");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(FXMLHomeViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void onSaveWallet(ActionEvent event) {
        saveWallet();
    }

    public void saveWallet() {
        StorageService.getInstance().saveWallet();
        new AlertController().successMessage("Wallet Saved :" + StorageService.getInstance().getWalletFilePath());
    }

    @FXML
    private void onLoadWallet(ActionEvent event) {
        loadWallet();
    }

    public void loadWallet() {
        Path path = Paths.get(StorageService.getInstance().getWalletFilePath());
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Wallet File");
        fileChooser.setInitialDirectory(path.getParent().toFile());
        fileChooser.setInitialFileName(path.getFileName().toString());

        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Json File", "*.json"));
        File selectedFile = fileChooser.showOpenDialog(arkWalletApp.getMainStage());
        if (selectedFile != null) {
            StorageService.getInstance().loadWallet(selectedFile.getAbsolutePath().toString());
        }
    }

    @FXML
    private void onRemoveMaster(ActionEvent event) {
    }

    @FXML
    private void onExportWallet(ActionEvent event) {
        exportWallet();
    }

    public void exportWallet() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLExportWalletView.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            FXMLExportWalletViewController exportController = (FXMLExportWalletViewController) fxmlLoader.getController();
            exportController.setHomeViewController(this);
            Stage stage = new Stage();
            exportController.setStage(stage);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("C");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(FXMLHomeViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    class AccountItem extends RecursiveTreeObject<AccountItem> {

        StringProperty username;
        StringProperty address;

        public AccountItem(String username, String address) {
            this.username = new SimpleStringProperty(username);
            this.address = new SimpleStringProperty(address);
        }

        public String getUsername() {
            return username.get();
        }

        public void setUsername(String username) {
            this.username.set(username);
        }

        public String getAddress() {
            return address.get();
        }

        public void setAddress(String address) {
            this.address.set(address);
        }

        @Override
        public boolean equals(Object obj) {
            boolean b = ((obj == null || this.address == null) ? this.address == null : ((AccountItem) obj).address.get().equals(this.address.get()));
            return b;
        }

    }

}
