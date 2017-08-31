/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import ark.optimal.wallet.pojo.Account;
import ark.optimal.wallet.pojo.Delegate;
import ark.optimal.wallet.services.accountservices.AccountService;
import ark.optimal.wallet.services.optimizationservices.OptimizationService;
import ark.optimal.wallet.services.storageservices.StorageService;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import cern.colt.matrix.tdouble.DoubleFactory1D;
import cern.colt.matrix.tdouble.DoubleFactory2D;
import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.joptimizer.exception.JOptimizerException;
import com.joptimizer.functions.ConvexMultivariateRealFunction;
import com.joptimizer.functions.LinearMultivariateRealFunction;
import com.joptimizer.optimizers.JOptimizer;
import com.joptimizer.optimizers.LPOptimizationRequest;
import com.joptimizer.optimizers.LPPrimalDualMethod;
import com.joptimizer.optimizers.OptimizationRequest;
import de.xypron.jcobyla.Calcfc;
import de.xypron.jcobyla.Cobyla;
import de.xypron.jcobyla.CobylaExitStatus;
import io.ark.core.Transaction;
import io.ark.core.TransactionService;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.apache.commons.math3.analysis.MultivariateFunction;

/**
 * FXML Controller class
 *
 * @author Mastadon
 */
public class FXMLDelegatesViewController implements Initializable {

    @FXML
    private AnchorPane holderAnchor;
    @FXML
    private TableView<Delegate> _delegatestable;
    @FXML
    private TableColumn<Delegate, String> _delegatename;
    @FXML
    private TableColumn<Delegate, Integer> _rank;
    @FXML
    private TableColumn<Delegate, Integer> _votes;
    @FXML
    private TableColumn<Delegate, Double> _approval;
    @FXML
    private TableColumn<Delegate, Double> _productivity;
    @FXML
    private Label lbl_delegatename;
    @FXML
    private Label lbl_delegateapproval;
    @FXML
    private Label lbl_delegateproductivity;
    @FXML
    private Label lbl_delegatepoolpercentage;
    @FXML
    private JFXButton _votefordelegate;
    @FXML
    private JFXButton _delegatesearch;
    private Label lbl_delegateAddress;
    private Label lbl_delegatepublickey;
    @FXML
    private Label lbl_delegateproducedblocks;
    @FXML
    private Label lbl_delegatemissedblocks;

    private Map<String, Delegate> delegatesMap;
    @FXML
    private Label lbl_delegatepayoutfrequency;
    @FXML
    private Label lbl_delegateminpayout;
    @FXML
    private Label lbl_delegatepayoutpercentage;
    @FXML
    private JFXTextField delegateNameOrPublicKey;
    @FXML
    private JFXButton delegateAddress;
    @FXML
    private JFXButton delegatepublickey;
    @FXML
    private Tooltip delegateAddressTooltip;
    @FXML
    private Tooltip delegatePublicKeyTooltip;
    @FXML
    private TableColumn<Delegate, Boolean> _delegateChecked;

    private List<Delegate> selectedDelegates;
    @FXML
    private JFXButton optimizationBtn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        delegatesMap = new HashMap<String, Delegate>();
        selectedDelegates = new ArrayList<Delegate>();
        _delegatestable.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Delegate>() {

            public void changed(
                    ObservableValue<? extends Delegate> observable,
                    Delegate oldValue,
                    Delegate newValue) {
                if (newValue == null) {
                    return;
                }
                updateDelegateView(newValue);
            }

        });
        _delegatestable.setRowFactory(new Callback<TableView<Delegate>, TableRow<Delegate>>() {
            @Override
            public TableRow<Delegate> call(TableView<Delegate> tableView2) {
                final TableRow<Delegate> row = new TableRow<>();
                row.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        final int index = row.getIndex();
                        if (index >= _delegatestable.getItems().size()) {
                            _delegatestable.getSelectionModel().clearSelection();
                            event.consume();
                        }
                    }
                });
                return row;
            }
        });

        _delegatename.setCellValueFactory(new PropertyValueFactory<Delegate, String>("username"));
        _votes.setCellValueFactory(new PropertyValueFactory<Delegate, Integer>("vote"));
        _rank.setCellValueFactory(new PropertyValueFactory<Delegate, Integer>("rate"));
        _approval.setCellValueFactory(new PropertyValueFactory<Delegate, Double>("approval"));
        _productivity.setCellValueFactory(new PropertyValueFactory<Delegate, Double>("productivity"));
        _delegateChecked.setCellValueFactory(new PropertyValueFactory<Delegate, Boolean>("checked"));

        _delegateChecked.setCellFactory(p -> {
            CheckBox checkBox = new CheckBox();
            TableCell<Delegate, Boolean> tableCell = new TableCell<Delegate, Boolean>() {

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
                    -> validate(checkBox, (Delegate) tableCell.getTableRow().getItem(), event));

            checkBox.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.SPACE) {
                    validate(checkBox, (Delegate) tableCell.getTableRow().getItem(), event);
                }
            });

            tableCell.setAlignment(Pos.CENTER);
            tableCell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

            return tableCell;
        });

        _delegateChecked.setEditable(true);

    }

    @FXML
    private void onSearch(ActionEvent event) {
        String n = delegateNameOrPublicKey.getText();
        Delegate d = null;
        if (delegatesMap.containsKey(n)) {
            d = delegatesMap.get(n);

        } else {
            d = AccountService.getDelegateByUsername(n);
            if (d == null) {
                return;
            }
            d.setMinPayout(100.0);
            d.setPayoutFrequency(0.25);
            d.setPayoutPercentage(80.0);
            d.setPoolPercentage(90.0);
            _delegatestable.getItems().add(d);
            delegatesMap.put(d.getUsername(), d);
            StorageService.getInstance().addDelegate(d);

        }

        updateDelegateView(d);

        _delegatestable.requestFocus();
        _delegatestable.getSelectionModel().select(d);

    }

    private void updateDelegateView(Delegate d) {
        lbl_delegatename.setText(d.getUsername());
        lbl_delegateapproval.setText(d.getApproval().toString());
        lbl_delegateproductivity.setText(String.format("%.2f%%", d.getProductivity()) + " Productivity");
        lbl_delegateproducedblocks.setText(d.getProducedblocks().toString() + " Blocks");
        lbl_delegatemissedblocks.setText(d.getMissedblocks().toString() + " Blocks");
        lbl_delegatepoolpercentage.setText(String.format("%.0f%%", d.getPoolPercentage()));
        lbl_delegatepayoutfrequency.setText(d.getPayoutFrequency().toString());
        lbl_delegateminpayout.setText(d.getMinPayout().toString());
        lbl_delegatepayoutpercentage.setText(d.getPayoutPercentage().toString());

        delegateAddress.setText(d.getAddress());
        delegateAddressTooltip.setText(d.getAddress());
        delegatepublickey.setText(d.getPublicKey());
        delegatePublicKeyTooltip.setText(d.getPublicKey());

    }

    @FXML
    private void onCopyAddress(ActionEvent event) {
        System.out.println(delegateAddress.getText());
        toClipboard(delegateAddress.getText());
    }

    @FXML
    private void onCopyPublicKey(ActionEvent event) {
        System.out.println(delegatepublickey.getText());
        toClipboard(delegatepublickey.getText());
    }

    private void toClipboard(String c) {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(c);
        clipboard.setContent(content);
    }

    @FXML
    private void onVoteForDelegate(ActionEvent event) {

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLUpdateVoteView.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            FXMLUpdateVoteViewController updateVoteController = (FXMLUpdateVoteViewController) fxmlLoader.getController();
            updateVoteController.setDelegateViewController(this);
            //updateVoteController.setDelegateName(_delegatestable.getSelectionModel().getSelectedItem().getUsername());
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

    private void validate(CheckBox checkBox, Delegate delegate, Event event) {
        event.consume();
        checkBox.setSelected(!checkBox.isSelected());
        delegate.setChecked(checkBox.isSelected());
        System.out.println(delegate.getUsername() + " selected : " + checkBox.isSelected());

        if (checkBox.isSelected()) {
            selectedDelegates.add(delegate);
        } else {
            selectedDelegates.remove(delegate);
        }

    }

    public void executeOptimizationTrades(Account account, String passphrase, Map<String, Double> votes) {

        // send all subwallets balance to master wallet  
        int walletsVotes = 0;
        for (String delegateName : account.getSubAccounts().keySet()) {
            Account subaccount = account.getSubAccounts().get(delegateName);
            subaccount = AccountService.getAccount(subaccount.getAddress());
            Double walletVotes = subaccount.getBalance() - 1;
            if (walletVotes != null && walletVotes > 0) {
                Transaction tx = TransactionService.createTransaction(subaccount.getAddress(), account.getAddress(), walletVotes.longValue(), "send to master wallet", passphrase + " " + delegateName);
                TransactionService.PostTransaction(tx);
                walletsVotes += walletVotes.intValue();
            }

        }
        // create new subwallets

        createSubWallets(account, passphrase);

        // send new votes to subwallets
        try {
            TimeUnit.SECONDS.sleep(2);
            Account acc = AccountService.getAccount(account.getAddress());
            while (acc.getBalance() < walletsVotes - 2) {
                System.out.println("Wait for Confirmation of transactions");
                System.out.println("subwallets votes = " + walletsVotes);
                System.out.println("master wallet balance  = " + acc.getBalance());
                acc = AccountService.getAccount(account.getAddress());

            }
            for (Delegate delegate : selectedDelegates) {
                Account subaccount = account.getSubAccounts().get(delegate.getUsername());
                Double vote = votes.get(delegate.getUsername());
                if (vote >= 1) {
                    Transaction tx = TransactionService.createTransaction(account.getAddress(), subaccount.getAddress(), vote.longValue(), "send to sub wallet", passphrase);
                    TransactionService.PostTransaction(tx);
                }
            }

        } catch (InterruptedException ex) {
            Logger.getLogger(FXMLDelegatesViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void optimize(Account account, String passphrase) {
        runOptimization(account, passphrase);
    }

    // creates subwallets and vote for corresponding delegates
    private void createSubWallets(Account account, String passphrase) {

        Map subAccounts = account.getSubAccounts();
        for (Delegate d : selectedDelegates) {
            if (!subAccounts.containsKey(d.getUsername())) {
                Account a = AccountService.createAccount(passphrase + " " + d.getUsername());
                if (a.getVotedDelegates().size() == 0) {
                    Transaction tx = TransactionService.createTransaction(account.getAddress(), a.getAddress(), 2, "send to sub wallet to vote", passphrase);
                    String response = TransactionService.PostTransaction(tx);
                    try {
                        int counter = 0;
                        while (!response.contains("success") && ++counter <= 10) {
                            System.out.println("wait for successful transaction");
                            response = TransactionService.PostTransaction(tx);
                        }

                        tx = TransactionService.createVote(a.getAddress(), d.getUsername(), passphrase + " " + d.getUsername(), false);
                        response = TransactionService.PostTransaction(tx);
                        counter = 0;
                        while (!response.contains("success") && ++counter <= 10) {
                            System.out.println("wait for successful transaction");
                            response = TransactionService.PostTransaction(tx);
                        }
                        a.getVotedDelegates().add(d);

                    } catch (Exception ex) {
                        Logger.getLogger(FXMLDelegatesViewController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                subAccounts.put(d.getUsername(), a);
                StorageService.getInstance().addAccountToUserAccounts(a);

            }
            System.out.println(d.getUsername());
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

        // test
        //walletsVotes = 2000000;
        Map<String, Double> votes = OptimizationService.runConvexOptimizattion(walletsVotes, selectedDelegates);
        runOptimizationReport(account, passphrase, votes);
        return;

        /*int walletsVotes = 0;
        for (String delegateName : account.getSubAccounts().keySet()) {
            Account subaccount = account.getSubAccounts().get(delegateName);
            subaccount = AccountService.getAccount(subaccount.getAddress());
            Double walletVotes = subaccount.getBalance();
            if (walletVotes != null && walletVotes > 3) {
                Transaction tx = TransactionService.createTransaction(subaccount.getAddress(), account.getAddress(), walletVotes.longValue() - 2, "send to master wallet", passphrase + " " + delegateName);
                TransactionService.PostTransaction(tx);
                walletsVotes += walletVotes.intValue();
            }

        }
        try {
            TimeUnit.SECONDS.sleep(2);
            Account acc = AccountService.getAccount(account.getAddress());
            while (acc.getBalance() < walletsVotes) {
                System.out.println("Wait for Confirmation of transactions");
                acc = AccountService.getAccount(account.getAddress());

            }
            walletsVotes = acc.getBalance().intValue() - 2;
            walletsVotes = 162786;
            //Map<String, Double> votes = createLP(walletsVotes, delegates);
            Map<String, Double> votes = createJOModel(walletsVotes, selectedDelegates);
            for (Delegate delegate : selectedDelegates) {
                Account subaccount = account.getSubAccounts().get(delegate.getUsername());
                Double vote = votes.get(delegate.getUsername());
                Transaction tx = TransactionService.createTransaction(account.getAddress(), subaccount.getAddress(), vote.longValue(), "send to sub wallet", passphrase);
                TransactionService.PostTransaction(tx);

            }

        } catch (InterruptedException ex) {
            Logger.getLogger(FXMLDelegatesViewController.class.getName()).log(Level.SEVERE, null, ex);
        }
         */
    }

    private void runOptimizationReport(Account account, String passphrase, Map<String, Double> votes) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLOptimizationReportView.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            FXMLOptimizationReportViewController optReportController = (FXMLOptimizationReportViewController) fxmlLoader.getController();
            optReportController.setDelegateViewController(this);
            optReportController.updateReport(account, passphrase, votes);
            //updateVoteController.setDelegateName(_delegatestable.getSelectionModel().getSelectedItem().getUsername());
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

    @FXML
    private void onRunOptimization(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLOptimizationSetupView.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            FXMLOptimizationSetupViewController optSetupViewController = (FXMLOptimizationSetupViewController) fxmlLoader.getController();
            optSetupViewController.setDelegateViewController(this);
            //updateVoteController.setDelegateName(_delegatestable.getSelectionModel().getSelectedItem().getUsername());
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
