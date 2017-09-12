/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import ark.optimal.wallet.pojo.Account;
import ark.optimal.wallet.services.storageservices.StorageService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListCell;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.IOException;
import java.net.URL;
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
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author Mastadon
 */
public class FXMLAccountsViewMenuController implements Initializable {

    @FXML
    private JFXButton btnImportAccount;
    @FXML
    private JFXButton btnWatchAccount;
    @FXML
    private JFXButton btnCreateAccount;
    @FXML
    private JFXListView<AccountItem> myAcountsListView;
    @FXML
    private JFXListView<AccountItem> watchAcountsListView;

    @FXML
    private Tooltip delegateAddressTooltip1;
    @FXML
    private Tooltip delegateAddressTooltip;
    @FXML
    private Tooltip delegateAddressTooltip2;

    public void setAccountViewController(FXMLAccountViewController accountViewController) {
        this.accountViewController = accountViewController;
    }

    private FXMLAccountViewController accountViewController;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        myAcountsListView.setEditable(true);
        myAcountsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        myAcountsListView.setCellFactory(myAcountsListView -> new JFXListCell<AccountItem>() {
            public void updateItem(AccountItem account, boolean empty) {
                super.updateItem(account, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    FontAwesomeIconView accountImage = new FontAwesomeIconView();
                    accountImage.setGlyphName("USER");
                    accountImage.setFill(Paint.valueOf("#404bb6"));
                    accountImage.setSize("16px");
                    setText(account.getUsername());
                    setGraphic(accountImage);
                    setPrefWidth(USE_PREF_SIZE);
                }
            }

        });

        myAcountsListView.setPrefHeight(myAcountsListView.getItems().size() * 40);

        watchAcountsListView.setEditable(true);
        watchAcountsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        watchAcountsListView.setCellFactory(watchAcountsListView -> new JFXListCell<AccountItem>() {
            public void updateItem(AccountItem account, boolean empty) {
                super.updateItem(account, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    FontAwesomeIconView accountImage = new FontAwesomeIconView();
                    accountImage.setGlyphName("USER");
                    accountImage.setFill(Paint.valueOf("#404bb6"));
                    accountImage.setSize("16px");
                    setText(account.getUsername());
                    setGraphic(accountImage);
                    setPrefWidth(USE_PREF_SIZE);

                }
            }

        });

        watchAcountsListView.setPrefHeight(watchAcountsListView.getItems().size() * 40);

    }

    public void runCreateAccount() {
        onCreateAccount(null);
    }

    public void runWatchAccount() {
        onWatchAccount(null);
    }

    public void runImportAccount() {
        onImportAccount(null);
    }

    @FXML
    private void onImportAccount(ActionEvent event) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLImportAccount.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            ((FXMLImportAccountController) fxmlLoader.getController()).setAccountMenuController(this);
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

    @FXML
    private void onWatchAccount(ActionEvent event) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLWatchAccount.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            ((FXMLWatchAccountController) fxmlLoader.getController()).setAccountMenuController(this);
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

    @FXML
    private void onCreateAccount(ActionEvent event) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLCreateAccount.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            ((FXMLCreateAccountController) fxmlLoader.getController()).setAccountMenuController(this);
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

    public void updateMyAccounts(Account account) {
        addToMyAccounts(account);
        accountViewController.selectAccount(account);
    }

    public void updateAccountInfo(Account account) {

    }

    public void updateWatchAccounts(Account account) {
        addToWatchAccounts(account);
        accountViewController.selectAccount(account);
    }

    private void addToMyAccounts(Account account) {
        if (!StorageService.getInstance().getWallet().getUserAccounts().containsKey(account.getAddress())) {
            StorageService.getInstance().addAccountToUserAccounts(account);
            myAcountsListView.getItems().add(new AccountItem(account.getUsername(), account.getAddress()));
        }

        myAcountsListView.requestFocus();
        myAcountsListView.getSelectionModel().select(new AccountItem(account.getUsername(), account.getAddress()));
        //myAcountsListView.getSelectionModel().select(0);
        myAcountsListView.refresh();
        myAcountsListView.setPrefHeight(myAcountsListView.getItems().size() * 40);
        watchAcountsListView.getSelectionModel().select(-1);
    }

    private void addToWatchAccounts(Account account) {
        if (!StorageService.getInstance().getWallet().getWatchAccounts().containsKey(account.getAddress())) {
            StorageService.getInstance().addAccountToWatchAccounts(account);
            watchAcountsListView.getItems().add(new AccountItem(account.getUsername(), account.getAddress()));
        }

        watchAcountsListView.requestFocus();
        watchAcountsListView.getSelectionModel().select(new AccountItem(account.getUsername(), account.getAddress()));
        //myAcountsListView.getSelectionModel().select(0);
        watchAcountsListView.setPrefHeight(watchAcountsListView.getItems().size() * 40);

        myAcountsListView.getSelectionModel().select(-1);

    }

    public void selectAccountItem(Account account) {
        myAcountsListView.requestFocus();
        myAcountsListView.getSelectionModel().select(new AccountItem(account.getUsername(), account.getAddress()));
        //myAcountsListView.getSelectionModel().select(0);
        myAcountsListView.refresh();
        myAcountsListView.setPrefHeight(myAcountsListView.getItems().size() * 40);
        watchAcountsListView.getSelectionModel().select(-1);
    }

    @FXML
    private void handleWatchAccountMouseClick(MouseEvent event) {
        if (event.getClickCount() == 2) {
            //Use ListView's getSelected Item
            System.out.println("YO YO YO");
            //use this to do whatever you want to. Open Link etc.
        }

        System.out.println(watchAcountsListView.getSelectionModel().getSelectedItem().getAddress());
        String address = watchAcountsListView.getSelectionModel().getSelectedItem().getAddress();
        Account account = StorageService.getInstance().getWallet().getWatchAccounts().get(address);
        accountViewController.selectAccount(account);
    }

    @FXML
    private void handleUserAccountMouseClick(MouseEvent event) {

        if (event.isControlDown()) {
            //Use ListView's getSelected Item
            System.out.println("YO YO YO");
            myAcountsListView.edit(myAcountsListView.getEditingIndex());

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLEditAccountName.fxml"));
                Parent root1 = (Parent) fxmlLoader.load();
                FXMLEditAccountNameController editctrlr = (FXMLEditAccountNameController) fxmlLoader.getController();
                editctrlr.setAccountMenuController(this);
                editctrlr.setAccount(myAcountsListView.getSelectionModel().getSelectedItem());
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
        if (event.isControlDown()) {
            System.out.println(myAcountsListView.getSelectionModel().getSelectedItem().getAddress());
        }
        String address = myAcountsListView.getSelectionModel().getSelectedItem().getAddress();
        Account account = StorageService.getInstance().getWallet().getUserAccounts().get(address);
        accountViewController.selectAccount(account);

    }

    public void addToUserAccountsMenu(Account account) {
        myAcountsListView.getItems().add(new AccountItem(account.getUsername(), account.getAddress()));

    }

    public void addToWatchAccountsMenu(Account account) {
        watchAcountsListView.getItems().add(new AccountItem(account.getUsername(), account.getAddress()));

    }

    void clearAccountsMenu() {
        myAcountsListView.getItems().clear();
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
