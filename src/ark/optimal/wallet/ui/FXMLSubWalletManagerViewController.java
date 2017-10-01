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
import ark.optimal.wallet.services.optimizationservices.OptimizationService;
import ark.optimal.wallet.services.storageservices.StorageService;
import ark.optimal.wallet.ui.main.HostServicesProvider;
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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
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
    private JFXButton selectAll;
    @FXML
    private JFXButton voteBtn;
    @FXML
    private JFXButton sendToMasterBtn;

    @FXML
    private TableColumn<SubWalletItem, Double> _payoutPercentage;

    private Map<String, SubWalletItem> subWalletsMap;
    private Account selectedAccount;
    @FXML
    private TableColumn<SubWalletItem, Boolean> voted;
    @FXML
    private TableColumn<SubWalletItem, Integer> _delegateTotalVotes;
    @FXML
    private TableColumn<SubWalletItem, Integer> _delegateExcludedVotes;
    @FXML
    private JFXButton optimizeBtn;
    @FXML
    private JFXButton removeSelected;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        subWalletsTable.setPlaceholder(new Label("Master Wallet has no Sub Wallets"));
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

        accounts.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observableValue, Object oldAccount, Object newAccount) {
                //newAccount = (Account)newAccount;
                selectMasterAccount((Account) newAccount);
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

        CheckBox selectAllCB = new CheckBox();
        selectAllCB.setUserData(this._delegateChecked);
        selectAllCB.setOnAction(handleSelectAllCheckbox());
        this._delegateChecked.setGraphic(selectAllCB);

        voted.setCellValueFactory(new PropertyValueFactory<SubWalletItem, Boolean>("voted"));
        voted.setCellFactory(p -> {
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
                        checkBox.setDisable(true);
                    }
                }
            };

            tableCell.setAlignment(Pos.CENTER);
            tableCell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

            return tableCell;
        });

        voted.setEditable(false);

        _delegateTotalVotes.setCellValueFactory(new PropertyValueFactory<SubWalletItem, Integer>("delegateTotalVotes"));
        _delegateExcludedVotes.setCellValueFactory(new PropertyValueFactory<SubWalletItem, Integer>("delegateExcludedVotes"));

    }

    void setAccountMenuController(FXMLAccountsViewMenuController accountsViewMenuController) {
        this.accountsViewMenuController = accountsViewMenuController;
    }

    @FXML
    private void onCreateImportSubWallets(ActionEvent event) {

        Account account = accounts.getValue();

        if (account == null) {

            new AlertController().alertUser("Please select master wallet");
            return;
        }
        if (masterPassphrase.getText() == null || masterPassphrase.getText().equals("")) {
            new AlertController().alertUser("Please enter master passphrase");
            return;
        }
        String senderByPassphrase = AccountService.getAddress(masterPassphrase.getText());
        if (!senderByPassphrase.equals(account.getAddress())) {
            new AlertController().alertUser("Passphrase is not corresponding to account");
            return;
        }

        List<Transaction> transactions = new ArrayList<Transaction>();
        List<TransactionItem> transactionItems = new ArrayList<TransactionItem>();

        Map subAccounts = account.getSubAccounts();
        for (SubWalletItem si : selectedSubWallets) {
            TransactionItem ti = new TransactionItem("---", account.getUsername(), account.getUsername() + "(" + si.getDelegateName() + ")", 0.0, 0.0, "SubWallet created/imported");

            // if (!subAccounts.containsKey(si.getDelegateName())) {
            Account sub = AccountService.createAccount(masterPassphrase.getText() + " " + si.getDelegateName());
            sub.setUsername(account.getUsername() + "(" + si.getDelegateName() + ")");
            Delegate delegate = StorageService.getInstance().getWallet().getDelegates().get(si.getDelegateName());
            if (sub.getVotedDelegates().size() == 0) {
                Transaction tx = TransactionService.createTransaction(account.getAddress(), sub.getAddress(), 2, "send to sub wallet to vote", masterPassphrase.getText());
                transactions.add(tx);
                ti = new TransactionItem(tx.getId(), account.getUsername(), sub.getUsername(), 2.0, 0.1, "send to sub wallet to vote");
                //sendToSubwallet(account, sub, delegate);
            }
            sub.setMasterAccountAddress(account.getAddress());
            subAccounts.put(delegate.getUsername(), sub);
            StorageService.getInstance().addAccountToUserAccounts(account, true);
            StorageService.getInstance().addAccountToSubAccounts(sub);

            //  }
            transactionItems.add(ti);

        }
        StorageService.getInstance().addAccountToUserAccounts(account, true);
        this.accountsViewMenuController.selectAccountItem(account);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLSubWalletTransactionsView.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            FXMLSubWalletTransactionsViewController createSubWalletViewController = ((FXMLSubWalletTransactionsViewController) fxmlLoader.getController());
            //createSubWalletViewController.setAccountSubWalletManagerController(this);
            createSubWalletViewController.setTransactions(transactions);
            createSubWalletViewController.setTransactionItems(transactionItems);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("C");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(FXMLSubWalletManagerViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

        //closeWindow();
    }

    public void sendToSubwallet(Account account, Account sub, Delegate delegate) {
        Transaction tx = TransactionService.createTransaction(account.getAddress(), sub.getAddress(), 2, "send to sub wallet to vote", masterPassphrase.getText());
        TransactionService.broadcastTransaction(tx);
    }

    private void onCreateImportSubWalletsCancel(ActionEvent event) {
        closeWindow();
    }

    @FXML
    private void onSearchDelegate(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            searchDelegate();
        }

    }
    private void searchDelegate() {
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
            si.setChecked(Boolean.FALSE);
            si.setVoted(Boolean.FALSE);
            si.setDelegateTotalVotes(d.getVote());
            si.setDelegateExcludedVotes(new Double(d.getVote() * d.getExlcudedPercentage() / 100.0).intValue());

            subWalletsTable.getItems().add(si);
            subWalletsMap.put(d.getUsername(), si);
            StorageService.getInstance().addDelegate(d, true);

        }
        subWalletsTable.getSelectionModel().clearSelection();
        subWalletsTable.requestFocus();
        subWalletsTable.getSelectionModel().select(si);
        subWalletsTable.scrollTo(si);
        delegateNameOrPublicKey.setText("");
    }

    @FXML
    private void onSearch(ActionEvent event) {
        searchDelegate();
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
        if (this.selectedAccount != null) {
            accountsViewMenuController.selectAccountItem(this.selectedAccount);
        }
        closeWindow();
    }

    private void selectAll(Boolean select) {
        selectedSubWallets.clear();
        if (select) {
            for (SubWalletItem si : subWalletsTable.getItems()) {
                si.setChecked(Boolean.TRUE);
                selectedSubWallets.add(si);
            }
        } else {
            for (SubWalletItem si : subWalletsTable.getItems()) {
                si.setChecked(Boolean.FALSE);
            }
        }
        subWalletsTable.refresh();
    }

    private EventHandler<ActionEvent> handleSelectAllCheckbox() {

        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CheckBox cb = (CheckBox) event.getSource();
                TableColumn column = (TableColumn) cb.getUserData();
                if (cb.isSelected()) {
                    selectAll(true);
                } else {
                    selectAll(false);
                }

            }
        };
    }

    @FXML
    private void onSubWalletVote(ActionEvent event) {

        Account account = accounts.getValue();

        if (account == null) {

            new AlertController().alertUser("Please select master wallet");
            return;
        }
        if (masterPassphrase.getText() == null || masterPassphrase.getText().equals("")) {
            new AlertController().alertUser("Please enter master passphrase");
            return;
        }
        String senderByPassphrase = AccountService.getAddress(masterPassphrase.getText());
        if (!senderByPassphrase.equals(account.getAddress())) {
            new AlertController().alertUser("Passphrase is not corresponding to account");
            return;
        }

        List<Transaction> transactions = new ArrayList<Transaction>();
        List<TransactionItem> transactionItems = new ArrayList<TransactionItem>();

        Map<String, Account> subAccounts = account.getSubAccounts();
        for (SubWalletItem si : selectedSubWallets) {
            Account sub = subAccounts.get(si.getDelegateName());
            Delegate delegate = StorageService.getInstance().getWallet().getDelegates().get(si.getDelegateName());
            TransactionItem ti = new TransactionItem("---", sub.getUsername(), sub.getUsername(), 0.0, 0.0, "Already voted");

            if (sub.getVotedDelegates().size() == 0) {
                Transaction tx = TransactionService.createVote(sub.getAddress(), si.getDelegateName(), masterPassphrase.getText() + " " + si.getDelegateName(), false);
                transactions.add(tx);
                ti = new TransactionItem(tx.getId(), sub.getUsername(), sub.getUsername(), 0.0, 1.0, "vote to delegate");
            }
            transactionItems.add(ti);

        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLSubWalletVotesView.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            FXMLSubWalletVotesViewController subWalletVotesViewController = ((FXMLSubWalletVotesViewController) fxmlLoader.getController());
            //createSubWalletViewController.setAccountSubWalletManagerController(this);
            subWalletVotesViewController.setTransactions(transactions);
            subWalletVotesViewController.setTransactionItems(transactionItems);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("C");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(FXMLSubWalletManagerViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void onSendToMaster(ActionEvent event) {

        Account account = accounts.getValue();

        if (account == null) {

            new AlertController().alertUser("Please select master wallet");
            return;
        }
        if (masterPassphrase.getText() == null || masterPassphrase.getText().equals("")) {
            new AlertController().alertUser("Please enter master passphrase");
            return;
        }
        String senderByPassphrase = AccountService.getAddress(masterPassphrase.getText());
        if (!senderByPassphrase.equals(account.getAddress())) {
            new AlertController().alertUser("Passphrase is not corresponding to account");
            return;
        }

        List<Transaction> transactions = new ArrayList<Transaction>();
        List<TransactionItem> transactionItems = new ArrayList<TransactionItem>();

        Map<String, Account> subAccounts = account.getSubAccounts();
        for (SubWalletItem si : selectedSubWallets) {
            Account sub = subAccounts.get(si.getDelegateName());
            Delegate delegate = StorageService.getInstance().getWallet().getDelegates().get(si.getDelegateName());
            TransactionItem ti = new TransactionItem("---", sub.getAddress(), sub.getAddress(), 0.0, 0.0, "Insufficient Balance to transfer");

            if (sub.getBalance() > 1.1) {
                Integer amount = sub.getBalance().intValue();
                Transaction tx = TransactionService.createTransaction(sub.getAddress(), account.getAddress(), amount, "send to Master", masterPassphrase.getText() + " " + si.getDelegateName());
                transactions.add(tx);
                ti = new TransactionItem(tx.getId(), sub.getUsername(), account.getUsername(), amount.doubleValue(), 0.1, "send to Master");

            }
            transactionItems.add(ti);

        }

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLSubWalletTransactionsView.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            FXMLSubWalletTransactionsViewController createSubWalletViewController = ((FXMLSubWalletTransactionsViewController) fxmlLoader.getController());
            //createSubWalletViewController.setAccountSubWalletManagerController(this);
            createSubWalletViewController.setTransactions(transactions);
            createSubWalletViewController.setTransactionItems(transactionItems);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("C");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(FXMLSubWalletManagerViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    void selectMasterAccount(Account account) {
        subWalletsTable.getItems().clear();
        subWalletsMap.clear();
        selectedSubWallets.clear();
        if (account == null) {
            return;
        }
        this.selectedAccount = account;
        for (Map.Entry<String, Account> entry : account.getSubAccounts().entrySet()) {
            Boolean checked = false;
            String delegateName = entry.getKey();
            Account subAccount = entry.getValue();
            Delegate delegate = StorageService.getInstance().getWallet().getDelegates().get(delegateName);
            SubWalletItem si = new SubWalletItem(delegateName, delegate.getRate(), subAccount.getUsername(), subAccount.getBalance().intValue(), delegate.getPayoutPercentage());
            if (entry.getValue().getVotedDelegates().size() > 0) {
                checked = true;
                this.selectedSubWallets.add(si);
            }
            si.setChecked(checked);
            si.setVoted(checked);
            si.setDelegateTotalVotes(delegate.getVote());
            si.setDelegateExcludedVotes(new Double(delegate.getVote() * delegate.getExlcudedPercentage() / 100.0).intValue());
            subWalletsTable.getItems().add(si);
            subWalletsMap.put(delegate.getUsername(), si);

        }
        subWalletsTable.refresh();
        accounts.getSelectionModel().select(account);

    }

    @FXML
    private void onOptimize(ActionEvent event) {

        Account account = accounts.getValue();

        if (account == null) {

            new AlertController().alertUser("Please select master wallet");
            return;
        }
        if (masterPassphrase.getText() == null || masterPassphrase.getText().equals("")) {
            new AlertController().alertUser("Please enter master passphrase");
            return;
        }
        String senderByPassphrase = AccountService.getAddress(masterPassphrase.getText());
        if (!senderByPassphrase.equals(account.getAddress())) {
            new AlertController().alertUser("Passphrase is not corresponding to account");
            return;
        }

        try {
            runOptimization(selectedAccount, masterPassphrase.getText());

        } catch (Exception ex) {
            Logger.getLogger(FXMLSubWalletManagerViewController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void runOptimization(Account account, String passphrase) {
        int walletsVotes = 0;
        for (String delegateName : account.getSubAccounts().keySet()) {
            Account subaccount = account.getSubAccounts().get(delegateName);
            subaccount = AccountService.getAccount(subaccount.getAddress());
            Double walletVotes = subaccount.getBalance() - 1;
            if (walletVotes != null && walletVotes > 0) {
                walletsVotes += walletVotes.intValue();
            }

        }
        Account acc = AccountService.getAccount(account.getAddress());
        walletsVotes += acc.getBalance().intValue();
        //walletsVotes = new Double((masterWalletPercentage/100.0) * walletsVotes).intValue();
        List<Delegate> selectedDelegates = new ArrayList<Delegate>();
        Map<String, Account> subAccounts = account.getSubAccounts();
        for (SubWalletItem si : selectedSubWallets) {
            Account sub = subAccounts.get(si.getDelegateName());
            Delegate delegate = StorageService.getInstance().getWallet().getDelegates().get(si.getDelegateName());
            selectedDelegates.add(delegate);
        }
        Map<String, Double> votes = OptimizationService.runConvexOptimizattion(walletsVotes, selectedDelegates);
        runOptimizationReport(account, passphrase, votes);
        return;
    }

    private void runOptimizationReport(Account account, String passphrase, Map<String, Double> votes) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLOptimizationReportView.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            FXMLOptimizationReportViewController optReportController = (FXMLOptimizationReportViewController) fxmlLoader.getController();
            optReportController.setSubWalletManagerController(this);
            optReportController.createReport(account, passphrase, votes);
            //updateVoteController.setDelegateName(_delegatestable.getSelectionModel().getSelectedItem().getUsername());
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("C");
            stage.setScene(new Scene(root1));
            stage.show();

        } catch (IOException ex) {
            Logger.getLogger(FXMLSubWalletManagerViewController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void executeOptimizationTrades(Account account, String passphrase, Map<String, Double> votes) {

        // send new votes to subwallets
        try {
            List<Transaction> transactions = new ArrayList<Transaction>();
            List<TransactionItem> transactionItems = new ArrayList<TransactionItem>();

            Map<String, Account> subAccounts = account.getSubAccounts();
            for (SubWalletItem si : selectedSubWallets) {
                Account sub = subAccounts.get(si.getDelegateName());
                TransactionItem ti = new TransactionItem("---", account.getUsername(), sub.getUsername(), 0.0, 0.0, "Zero Votes/Arks to transfer");
                Double vote = votes.get(si.getDelegateName());

                if (vote >= 1) {
                    Transaction tx = TransactionService.createTransaction(account.getAddress(), sub.getAddress(), vote.longValue(), "send opt vote to SubWallet", masterPassphrase.getText());
                    transactions.add(tx);
                    ti = new TransactionItem(tx.getId(), account.getUsername(), sub.getUsername(), vote, 0.1, "send to Subwallet");

                }
                transactionItems.add(ti);

            }

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLSubWalletTransactionsView.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            FXMLSubWalletTransactionsViewController createSubWalletViewController = ((FXMLSubWalletTransactionsViewController) fxmlLoader.getController());
            //createSubWalletViewController.setAccountSubWalletManagerController(this);
            createSubWalletViewController.setTransactions(transactions);
            createSubWalletViewController.setTransactionItems(transactionItems);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("C");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(FXMLAccountsViewMenuController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(FXMLDelegatesViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void onRemoveSelected(ActionEvent event) {
        Account master = this.selectedAccount;
        Map<String, Account> subs = master.getSubAccounts();
        for (SubWalletItem si : selectedSubWallets) {
            String delegateName = si.getDelegateName();
            subs.remove(delegateName);
            subWalletsTable.getItems().remove(si);
            subWalletsMap.remove(delegateName);
        }
        master.setSubAccounts(subs);
        StorageService.getInstance().addAccountToUserAccounts(master, true);

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
                            try {
                                HostServicesProvider.getInstance().getHostServices().showDocument("https://explorer.ark.io/");
                            } catch (Exception ex) {
                                Logger.getLogger(FXMLOptimizationReportViewController.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        });
                    }

                }
            };
            return cell;
        }
    }

}
