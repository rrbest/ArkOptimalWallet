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
import java.util.HashMap;
import java.util.Map;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
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
    private JFXButton btnCreateAccount;
    @FXML
    private JFXListView<AccountItem> myAcountsListView;
    @FXML
    private JFXListView<AccountItem> subAcountsListView;

    @FXML
    private Tooltip delegateAddressTooltip1;
    @FXML
    private Tooltip delegateAddressTooltip;
    @FXML
    private Tooltip delegateAddressTooltip2;
    @FXML
    private JFXButton btnManageSubWallets;
    @FXML
    private JFXButton sendToMaster;

    public void setAccountViewController(FXMLAccountViewController accountViewController) {
        this.accountViewController = accountViewController;
    }

    private FXMLAccountViewController accountViewController;

    private Map<String, Account> masterAccounts;
    private Map<String, Account> subAccounts;
    private VBox accountsParentVBox;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        masterAccounts = new HashMap<String, Account>();
        subAccounts = new HashMap<String, Account>();

        accountsParentVBox = (VBox) myAcountsListView.getParent();
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
                    accountImage.setGlyphName("BANK");
                    accountImage.setFill(Paint.valueOf("#404bb6"));
                    accountImage.setSize("16px");
                    setText(account.getUsername());
                    setGraphic(accountImage);
                    setPrefWidth(USE_PREF_SIZE);
                }
            }

        });

        myAcountsListView.setPrefHeight(myAcountsListView.getItems().size() * 40);

        subAcountsListView.setEditable(true);
        subAcountsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        subAcountsListView.setCellFactory(watchAcountsListView -> new JFXListCell<AccountItem>() {
            public void updateItem(AccountItem account, boolean empty) {
                super.updateItem(account, empty);
                if (empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    FontAwesomeIconView accountImage = new FontAwesomeIconView();
                    accountImage.setGlyphName("BANK");
                    accountImage.setFill(Paint.valueOf("#03A9F4"));
                    accountImage.setSize("13px");
                    setText(account.getUsername());
                    setGraphic(accountImage);
                    setPrefWidth(USE_PREF_SIZE);

                }
            }

        });

        subAcountsListView.setPrefHeight(subAcountsListView.getItems().size() * 40);
        
        accountsParentVBox.setPrefHeight(myAcountsListView.getItems().size() * 40 + subAcountsListView.getItems().size() * 40 + 100);
        ((AnchorPane)accountsParentVBox.getParent()).setPrefHeight(accountsParentVBox.getPrefHeight() + 50);
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
            StorageService.getInstance().addAccountToUserAccounts(account, true);
        }
        if(!masterAccounts.containsKey(account.getAddress())){
            masterAccounts.put(account.getAddress(), account);
            myAcountsListView.getItems().add(new AccountItem(account.getUsername(), account.getAddress()));
        }
        
        myAcountsListView.requestFocus();
        myAcountsListView.getSelectionModel().select(new AccountItem(account.getUsername(), account.getAddress()));
        myAcountsListView.refresh();
        myAcountsListView.setPrefHeight(myAcountsListView.getItems().size() * 40);
        accountsParentVBox.setPrefHeight(myAcountsListView.getItems().size() * 40 + subAcountsListView.getItems().size() * 40 + 100);
        subAcountsListView.getSelectionModel().select(-1);
    }

    private void viewSubAccounts(Account account) {
        subAcountsListView.getItems().clear();
        if (account.getSubAccounts().size() == 0) {
            //    sendToMaster.setVisible(false);
        } else {
            for (String delegateName : account.getSubAccounts().keySet()) {
                Account sub = account.getSubAccounts().get(delegateName);
                sub.setUsername(account.getUsername() + " (" + delegateName + ")");
                subAcountsListView.getItems().add(new AccountItem(sub.getUsername(), sub.getAddress()));
                //StorageService.getInstance().addAccountToSubAccounts(sub);
                subAccounts.put(sub.getAddress(), sub);
            }
            //     sendToMaster.setVisible(true);
        }
        subAcountsListView.setPrefHeight(subAcountsListView.getItems().size() * 40);
        accountsParentVBox.setPrefHeight(myAcountsListView.getItems().size() * 40 + subAcountsListView.getItems().size() * 40 + 100);
        ((AnchorPane)accountsParentVBox.getParent()).setPrefHeight(accountsParentVBox.getPrefHeight() + 100);

    }

    private void addToWatchAccounts(Account account) {
        if (!StorageService.getInstance().getWallet().getWatchAccounts().containsKey(account.getAddress())) {
            StorageService.getInstance().addAccountToWatchAccounts(account);
            subAcountsListView.getItems().add(new AccountItem(account.getUsername(), account.getAddress()));
        }

        subAcountsListView.requestFocus();
        subAcountsListView.getSelectionModel().select(new AccountItem(account.getUsername(), account.getAddress()));
        //myAcountsListView.getSelectionModel().select(0);
        subAcountsListView.setPrefHeight(subAcountsListView.getItems().size() * 40);

        myAcountsListView.getSelectionModel().select(-1);

    }

    public void selectAccountItem(Account account) {
        myAcountsListView.requestFocus();
        myAcountsListView.getSelectionModel().select(new AccountItem(account.getUsername(), account.getAddress()));
        //myAcountsListView.getSelectionModel().select(0);
        myAcountsListView.refresh();
        myAcountsListView.setPrefHeight(myAcountsListView.getItems().size() * 40);
        viewSubAccounts(account);
        subAcountsListView.getSelectionModel().select(-1);
    }

    public void selectSubAccountItem(Account account) {
        subAcountsListView.requestFocus();
        subAcountsListView.getSelectionModel().select(new AccountItem(account.getUsername(), account.getAddress()));
        //myAcountsListView.getSelectionModel().select(0);
        subAcountsListView.refresh();
        subAcountsListView.setPrefHeight(subAcountsListView.getItems().size() * 40);
        myAcountsListView.getSelectionModel().select(-1);
    }

    private void handleWatchAccountMouseClick(MouseEvent event) {
        if (event.isControlDown()) {
            subAcountsListView.edit(subAcountsListView.getEditingIndex());

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLEditAccountName.fxml"));
                Parent root1 = (Parent) fxmlLoader.load();
                FXMLEditAccountNameController editctrlr = (FXMLEditAccountNameController) fxmlLoader.getController();
                editctrlr.setAccountMenuController(this);
                editctrlr.setAccount(subAcountsListView.getSelectionModel().getSelectedItem());
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

        String address = subAcountsListView.getSelectionModel().getSelectedItem().getAddress();
        Account account = StorageService.getInstance().getWallet().getWatchAccounts().get(address);
        accountViewController.selectAccount(account);
    }

    @FXML
    private void handleUserAccountMouseClick(MouseEvent event) {

        if (event.isControlDown()) {
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
        AccountItem accountItem = myAcountsListView.getSelectionModel().getSelectedItem();
        if(accountItem == null){ // no account item selected (clicked)
            return;
        }
        String address = myAcountsListView.getSelectionModel().getSelectedItem().getAddress();
        Account account = StorageService.getInstance().getWallet().getUserAccounts().get(address);
        accountViewController.selectAccount(account);

    }

    public void addToUserAccountsMenu(Account account) {
        myAcountsListView.getItems().add(new AccountItem(account.getUsername(), account.getAddress()));

    }

    public void addToWatchAccountsMenu(Account account) {
        subAcountsListView.getItems().add(new AccountItem(account.getUsername(), account.getAddress()));

    }

    void clearAccountsMenu() {
        myAcountsListView.getItems().clear();
    }

    @FXML
    private void onManageSubWallets(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLSubWalletManagerView.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            FXMLSubWalletManagerViewController subwalletManager = ((FXMLSubWalletManagerViewController) fxmlLoader.getController());
            subwalletManager.setAccountMenuController(this);
            AccountItem ai = myAcountsListView.getSelectionModel().getSelectedItem();
            Account account = null;
            if (ai != null) {
                account = StorageService.getInstance().getWallet().getUserAccounts().get(ai.getAddress());
            }
            subwalletManager.selectMasterAccount(account);
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
    private void handleSubAccountMouseClick(MouseEvent event) {
        String address = subAcountsListView.getSelectionModel().getSelectedItem().getAddress();
        Account subAccount = StorageService.getInstance().getWallet().getSubAccounts().get(address);
        //Account subAccount = subAccounts.get(address);
        accountViewController.selectSubAccount(subAccount);

    }

    @FXML
    private void onSendToMaster(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLSendToMasterView.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            FXMLSendToMasterViewController sendController = ((FXMLSendToMasterViewController) fxmlLoader.getController());
            sendController.setAccountViewMenuController(this);
            String address = null;
            Account account = null;
            AccountItem accountItem = myAcountsListView.getSelectionModel().getSelectedItem();
            if (accountItem != null) {
                address = accountItem.getAddress();
                account = StorageService.getInstance().getWallet().getUserAccounts().get(address);
            } else {
                accountItem = subAcountsListView.getSelectionModel().getSelectedItem();
                address = accountItem.getAddress();
                //account = subAccounts.get(address);
                //account = StorageService.getInstance().getWallet().getUserAccounts().get(account.getMasterAccountAddress());
                Account sub = StorageService.getInstance().getWallet().getSubAccounts().get(address);
                account = StorageService.getInstance().getWallet().getUserAccounts().get(sub.getMasterAccountAddress());
            }

            sendController.updateSubWalletsTable(account);
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
