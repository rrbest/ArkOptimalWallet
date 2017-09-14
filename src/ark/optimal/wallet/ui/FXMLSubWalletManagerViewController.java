/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import ark.optimal.wallet.pojo.Account;
import ark.optimal.wallet.pojo.Delegate;
import ark.optimal.wallet.services.accountservices.AccountService;
import ark.optimal.wallet.services.storageservices.StorageService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import io.ark.core.Transaction;
import io.ark.core.TransactionService;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
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
    private JFXButton createImportSubWalletsCancel;
    @FXML
    private JFXComboBox<Account> accounts;
    @FXML
    private JFXTextField delegateNameOrPublicKey;
    @FXML
    private JFXButton delegateSearch;
    
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
    
    private Map<String, Delegate> delegatesMap;
    private List<Delegate> selectedDelegates;
    @FXML
    private TableView<Delegate> delegatesTable;
    @FXML
    private TableColumn<Delegate, Boolean> _delegateChecked;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        delegatesMap = new HashMap<String, Delegate>();
        selectedDelegates = new ArrayList<Delegate>();
        ObservableList<Account> userAccounts = FXCollections.observableArrayList();
         userAccounts.addAll(StorageService.getInstance().getWallet().getUserAccounts().values());
         accounts.setItems(userAccounts);
         //accounts.getSelectionModel().selectFirst();
         
         
         accounts.setCellFactory(new Callback<ListView<Account>,ListCell<Account>>(){
            public ListCell<Account> call(ListView<Account> l){
                return new ListCell<Account>(){
                    @Override
                    protected void updateItem(Account item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            setText(item.getUsername());
                        }
                    }
                } ;
            }
        });
        //selected value showed in combo box
        accounts.setConverter(new StringConverter<Account>() {
              public String toString(Account account) {
                if (account == null){
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
        
        // intialize from storage 
        
        for(Delegate delegate : StorageService.getInstance().getWallet().getDelegates().values()){
            delegatesTable.getItems().add(delegate);
            delegatesMap.put(delegate.getUsername(), delegate);
            
        }
        delegatesTable.refresh();
        
        
         
    }    

    void setAccountMenuController(FXMLAccountsViewMenuController accountsViewMenuController) {
        this.accountsViewMenuController = accountsViewMenuController;
    }

    @FXML
    private void onCreateImportSubWallets(ActionEvent event) {
        Account account = accounts.getValue();
        Map subAccounts = account.getSubAccounts();
        for (Delegate d : selectedDelegates) {
            if (!subAccounts.containsKey(d.getUsername())) {
                Account a = AccountService.createAccount(masterPassphrase.getText() + " " + d.getUsername());
                if (a.getVotedDelegates().size() == 0) {
                    Transaction tx = TransactionService.createTransaction(account.getAddress(), a.getAddress(), 2, "send to sub wallet to vote", masterPassphrase.getText());
                    String response = TransactionService.PostTransaction(tx);
                    try {
                        int counter = 0;
                        while (!response.contains("success") && ++counter <= 10) {
                            System.out.println("wait for successful transaction");
                            response = TransactionService.PostTransaction(tx);
                        }

                        tx = TransactionService.createVote(a.getAddress(), d.getUsername(), masterPassphrase.getText() + " " + d.getUsername(), false);
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
                } else {
                    
                }
                a.setMasterAccount(account);
                subAccounts.put(d.getUsername(), a);
                StorageService.getInstance().addAccountToSubAccounts(a);

            }
            System.out.println(d.getUsername());
        }
        StorageService.getInstance().addAccountToUserAccounts(account);
        this.accountsViewMenuController.selectAccountItem(account);
        closeWindow();
    }

    @FXML
    private void onCreateImportSubWalletsCancel(ActionEvent event) {
        closeWindow();
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
            delegatesTable.getItems().add(d);
            delegatesMap.put(d.getUsername(), d);
            StorageService.getInstance().addDelegate(d);

        }


        delegatesTable.requestFocus();
        delegatesTable.getSelectionModel().select(d);
    }
    private void closeWindow(){
        Stage stage = (Stage) masterPassphrase.getScene().getWindow();
        stage.close();
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
}
