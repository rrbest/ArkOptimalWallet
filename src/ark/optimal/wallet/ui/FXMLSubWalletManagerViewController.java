/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import ark.optimal.wallet.pojo.Account;
import ark.optimal.wallet.pojo.Delegate;
import ark.optimal.wallet.services.accountservices.AccountService;
import ark.optimal.wallet.services.networkservices.NetworkService;
import ark.optimal.wallet.services.storageservices.StorageService;
import com.google.gson.Gson;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import io.ark.core.Transaction;
import io.ark.core.TransactionService;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author Mastadon
 */
public class FXMLSubWalletManagerViewController implements Initializable {

    private FXMLAccountsViewMenuController accountsViewMenuController;
    @FXML
    private JFXTextField masterPassphrase;
    @FXML
    private JFXButton createImportSubWallets;
    @FXML
    private JFXComboBox<Account> accounts;
    @FXML
    private JFXTextField delegateNameOrPublicKey;
    @FXML
    private JFXButton delegateSearch;

    @FXML
    private TableColumn<SubWalletItem, String> _delegatename;
    @FXML
    private TableColumn<SubWalletItem, Integer> _rank;
    @FXML
    private TableColumn<SubWalletItem, Integer> _votes;

    private List<SubWalletItem> selectedSubWallets;
    @FXML
    private TableView<SubWalletItem> subWalletsTable;
    @FXML
    private TableColumn<SubWalletItem, Boolean> _delegateChecked;
    @FXML
    private JFXButton close;
    @FXML
    private TableColumn<SubWalletItem, Hyperlink> _subwallet;
    @FXML
    private JFXButton selectAll;
    @FXML
    private JFXButton voteBtn;
    @FXML
    private JFXButton sendToMasterBtn;

    @FXML
    private TableColumn<SubWalletItem, Double> _payoutPercentage;

    private Map<String, SubWalletItem> subWalletsMap;
    private Account selectedAccount;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        subWalletsMap = new HashMap<String, SubWalletItem>();
        selectedSubWallets = new ArrayList<SubWalletItem>();
        ObservableList<Account> userAccounts = FXCollections.observableArrayList();
        userAccounts.addAll(StorageService.getInstance().getWallet().getUserAccounts().values());
        accounts.setItems(userAccounts);
        //accounts.getSelectionModel().selectFirst();

        accounts.setCellFactory(new Callback<ListView<Account>, ListCell<Account>>() {
            public ListCell<Account> call(ListView<Account> l) {
                return new ListCell<Account>() {
                    @Override
                    protected void updateItem(Account item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            setText(item.getUsername());
                        }
                    }
                };
            }
        });
        //selected value showed in combo box
        accounts.setConverter(new StringConverter<Account>() {
            public String toString(Account account) {
                if (account == null) {
                    return null;
                } else {
                    return account.getUsername();
                }
            }

            @Override
            public Account fromString(String username) {
                return null;
            }
        });

        _delegatename.setCellValueFactory(new PropertyValueFactory<SubWalletItem, String>("delegateName"));
        _votes.setCellValueFactory(new PropertyValueFactory<SubWalletItem, Integer>("votes"));
        _rank.setCellValueFactory(new PropertyValueFactory<SubWalletItem, Integer>("delegateRank"));
        _payoutPercentage.setCellValueFactory(new PropertyValueFactory<SubWalletItem, Double>("payoutPercentage"));

        _subwallet.setCellValueFactory(new PropertyValueFactory<SubWalletItem, Hyperlink>("address_link"));
        _subwallet.setCellFactory(new FXMLSubWalletManagerViewController.HyperlinkCell());

        _delegateChecked.setCellValueFactory(new PropertyValueFactory<SubWalletItem, Boolean>("checked"));

        _delegateChecked.setCellFactory(p -> {
            CheckBox checkBox = new CheckBox();
            TableCell<SubWalletItem, Boolean> tableCell = new TableCell<SubWalletItem, Boolean>() {

                @Override
                protected void updateItem(Boolean item, boolean empty) {

                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        setGraphic(checkBox);
                        checkBox.setSelected(item);
                    }
                }
            };

            checkBox.addEventFilter(MouseEvent.MOUSE_PRESSED, event
                    -> validate(checkBox, (SubWalletItem) tableCell.getTableRow().getItem(), event));

            checkBox.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.SPACE) {
                    validate(checkBox, (SubWalletItem) tableCell.getTableRow().getItem(), event);
                }
            });

            tableCell.setAlignment(Pos.CENTER);
            tableCell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

            return tableCell;
        });

        _delegateChecked.setEditable(true);

        // intialize from storage 
//        for (Delegate delegate : StorageService.getInstance().getWallet().getDelegates().values()) {
//            SubWalletItem si = new SubWalletItem(delegate.getUsername(), delegate.getRate(), "", null, delegate.getPayoutPercentage());
//            subWalletsTable.getItems().add(si);
//            subWalletsMap.put(delegate.getUsername(), si);
//
//        }
//        subWalletsTable.refresh();
    }

    void setAccountMenuController(FXMLAccountsViewMenuController accountsViewMenuController) {
        this.accountsViewMenuController = accountsViewMenuController;
    }

    @FXML
    private void onCreateImportSubWallets(ActionEvent event) {

        if (accounts.getValue() == null) {

            new AlertController().alertUser("Please select master wallet");
            return;
        }
        if (masterPassphrase.getText() == null || masterPassphrase.getText().equals("")) {
            new AlertController().alertUser("Please enter master passphrase");
            return;
        }

        Account account = accounts.getValue();
        Map subAccounts = account.getSubAccounts();
        for (SubWalletItem si : selectedSubWallets) {
            if (!subAccounts.containsKey(si.getDelegateName())) {
                Account sub = AccountService.createAccount(masterPassphrase.getText() + " " + si.getDelegateName());
                Delegate delegate = StorageService.getInstance().getWallet().getDelegates().get(si.getDelegateName());
                if (sub.getVotedDelegates().size() == 0) {
                    sendToSubwallet(account, sub, delegate);
                }
                sub.setMasterAccountAddress(account.getAddress());
                subAccounts.put(delegate.getUsername(), sub);
                StorageService.getInstance().addAccountToUserAccounts(account);

                StorageService.getInstance().addAccountToSubAccounts(sub);

            }
        }
        StorageService.getInstance().addAccountToUserAccounts(account);
        this.accountsViewMenuController.selectAccountItem(account);
        closeWindow();
    }

    public void sendToSubwallet(Account account, Account sub, Delegate delegate) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> {
            try {
                Transaction tx = TransactionService.createTransaction(account.getAddress(), sub.getAddress(), 2, "send to sub wallet to vote", masterPassphrase.getText());
                TransactionService.broadcastTransaction(tx);

//                Thread.sleep(8000); // wait to confirm
//                Account newSub = null;
//                int counter = 0;
//                while (newSub == null && ++counter <= 10) {
//                    newSub = AccountService.getFullAccount(sub.getAddress());
//                }
//                if (newSub == null) {
//                    return;
//                }
//
//                if (newSub.getBalance() > 1) {
//                    tx = TransactionService.createVote(sub.getAddress(), delegate.getUsername(), masterPassphrase.getText() + " " + delegate.getUsername(), false);
//                    TransactionService.broadcastTransaction(tx);
//                    counter = 0;
//                    newSub = null;
//                    while (newSub == null && ++counter <= 10) {
//                        newSub = AccountService.getFullAccount(sub.getAddress());
//                    }
//                    if (newSub == null) {
//                        return;
//                    }
//                    if (newSub.getVotedDelegates().size() > 0) {
//                        sub.getVotedDelegates().add(delegate);
//                    }
//                }
            } catch (Exception ex) {
                Logger.getLogger(FXMLDelegatesViewController.class.getName()).log(Level.SEVERE, null, ex);
            }

        });

    }

    private void onCreateImportSubWalletsCancel(ActionEvent event
    ) {
        closeWindow();
    }

    @FXML
    private void onSearch(ActionEvent event) {
        String n = delegateNameOrPublicKey.getText();
        SubWalletItem si = null;
        if (subWalletsMap.containsKey(n)) {
            si = subWalletsMap.get(n);

        } else {
            Delegate d = StorageService.getInstance().getWallet().getDelegates().get(n);
            if (d == null) {
                d = AccountService.getDelegateByUsername(n);
                if (d == null) {
                    new AlertController().alertUser("Delegate doesn't exist");
                    return;
                }
            }
            String subWalletName = this.selectedAccount.getUsername() + "(" + d.getUsername() + ")";
            si = new SubWalletItem(d.getUsername(), d.getRate(), subWalletName, 0, d.getPayoutPercentage());
            si.setChecked(Boolean.TRUE);
            subWalletsTable.getItems().add(si);
            subWalletsMap.put(d.getUsername(), si);
            StorageService.getInstance().addDelegate(d);

        }

        subWalletsTable.requestFocus();
        subWalletsTable.getSelectionModel().select(si);
    }

    private void closeWindow() {
        Stage stage = (Stage) masterPassphrase.getScene().getWindow();
        stage.close();
    }

    private void validate(CheckBox checkBox, SubWalletItem subwalletItem, Event event) {
        event.consume();
        checkBox.setSelected(!checkBox.isSelected());
        subwalletItem.setChecked(checkBox.isSelected());

        if (checkBox.isSelected()) {
            selectedSubWallets.add(subwalletItem);
        } else {
            selectedSubWallets.remove(subwalletItem);
        }
    }

    @FXML
    private void onClose(ActionEvent event) {
        closeWindow();
    }

    @FXML
    private void onSelectAll(ActionEvent event) {
        selectedSubWallets.clear();
        if (selectAll.getText().equals("Select All")) {
            selectAll.setText("De-Select All");
            for (SubWalletItem si : subWalletsTable.getItems()) {
                si.setChecked(Boolean.TRUE);
                selectedSubWallets.add(si);
            }
        } else {
            selectAll.setText("Select All");
            for (SubWalletItem si : subWalletsTable.getItems()) {
                si.setChecked(Boolean.FALSE);
            }
        }
        subWalletsTable.refresh();
    }

    @FXML
    private void onSubWalletVote(ActionEvent event) {
    }

    @FXML
    private void onSendToMaster(ActionEvent event) {
    }

    void selectMasterAccount(Account account) {
        if (account == null) {
            return;
        }
        this.selectedAccount = account;
        accounts.getSelectionModel().select(account);
        for (Map.Entry<String, Account> entry : account.getSubAccounts().entrySet()) {
            Boolean checked = false;
            String delegateName = entry.getKey();
            Account subAccount = entry.getValue();
            if (entry.getValue().getVotedDelegates().size() > 0) {
                checked = true;
            }
            Delegate delegate = StorageService.getInstance().getWallet().getDelegates().get(delegateName);
            SubWalletItem si = new SubWalletItem(delegateName, delegate.getRate(), subAccount.getUsername(), subAccount.getBalance().intValue(), delegate.getPayoutPercentage());
            si.setChecked(checked);
            subWalletsTable.getItems().add(si);
            subWalletsMap.put(delegate.getUsername(), si);

        }

        subWalletsTable.refresh();
    }

    private class HyperlinkCell implements Callback<TableColumn<SubWalletItem, Hyperlink>, TableCell<SubWalletItem, Hyperlink>> {

        @Override
        public TableCell<SubWalletItem, Hyperlink> call(TableColumn<SubWalletItem, Hyperlink> arg) {
            TableCell<SubWalletItem, Hyperlink> cell = new TableCell<SubWalletItem, Hyperlink>() {
                @Override
                protected void updateItem(Hyperlink item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setGraphic(null);
                    } else {

                        setGraphic(item);

                        item.setOnAction(t -> {
                            URI u;
                            try {
                                u = new URI(item.getText());
                                java.awt.Desktop.getDesktop().browse(u);
                            } catch (URISyntaxException ex) {
                                Logger.getLogger(FXMLAccountViewController.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(FXMLAccountViewController.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        });
                    }

                }
            };
            return cell;
        }
    }

}
