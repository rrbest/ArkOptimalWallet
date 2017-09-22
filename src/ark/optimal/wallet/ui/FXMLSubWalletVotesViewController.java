/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import ark.optimal.wallet.ui.main.HostServicesProvider;
import com.jfoenix.controls.JFXButton;
import io.ark.core.Transaction;
import io.ark.core.TransactionService;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * FXML Controller class
 *
 * @author Mastadon
 */
public class FXMLSubWalletVotesViewController implements Initializable {

    @FXML
    private TableView<TransactionItem> transactionsTable;
    @FXML
    private TableColumn<TransactionItem, Hyperlink> tid;
    @FXML
    private TableColumn<TransactionItem, Hyperlink> from;
    @FXML
    private TableColumn<TransactionItem, Hyperlink> to;
    @FXML
    private TableColumn<TransactionItem, Double> fee;
    
    @FXML
    private JFXButton executeTransactions;
    @FXML
    private JFXButton cancelTransactions;

    private List<Transaction> transactions;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        tid.setCellValueFactory(new PropertyValueFactory<TransactionItem, Hyperlink>("id_link"));
        tid.setCellFactory(new FXMLSubWalletVotesViewController.HyperlinkCell());
        
        fee.setCellValueFactory(new PropertyValueFactory<TransactionItem, Double>("fee"));
        fee.setCellFactory(new FXMLSubWalletVotesViewController.ColumnFormatter<TransactionItem, Double>());

        from.setCellValueFactory(new PropertyValueFactory<TransactionItem, Hyperlink>("from_link"));
        from.setCellFactory(new FXMLSubWalletVotesViewController.HyperlinkCell());

        to.setCellValueFactory(new PropertyValueFactory<TransactionItem, Hyperlink>("to_link"));
        to.setCellFactory(new FXMLSubWalletVotesViewController.HyperlinkCell());
        
        transactionsTable.setPlaceholder(new Label("No Wallets to be created"));

    }
    public void setTransactions(List<Transaction> transactions){
        this.transactions = transactions;
    }
    public void setTransactionItems(List<TransactionItem> transactionItems){
        transactionsTable.getItems().clear();
        if (transactionItems == null){
            return;
        }
        transactionsTable.getItems().addAll(transactionItems);
        transactionsTable.refresh();
    }

    @FXML
    private void onExecuteTransactions(ActionEvent event) {
        for (Transaction tx: this.transactions){
           TransactionService.broadcastTransaction(tx);
        }
        closeWindow();
        new AlertController().successMessage("Transactions sent. Please allow time to be confirmed!");

    }

    @FXML
    private void onCancelTransactions(ActionEvent event) {
        closeWindow();
    }
    
     private void closeWindow(){
        Stage stage = (Stage) executeTransactions.getScene().getWindow();
        stage.close();
    }
    private class HyperlinkCell implements Callback<TableColumn<TransactionItem, Hyperlink>, TableCell<TransactionItem, Hyperlink>> {

        @Override
        public TableCell<TransactionItem, Hyperlink> call(TableColumn<TransactionItem, Hyperlink> arg) {
            TableCell<TransactionItem, Hyperlink> cell = new TableCell<TransactionItem, Hyperlink>() {
                @Override
                protected void updateItem(Hyperlink item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setGraphic(null);
                    } else {

                        setGraphic(item);

                        item.setOnAction(t -> {
                            try {
                                HostServicesProvider.getInstance().getHostServices().showDocument("https://explorer.ark.io/tx/" + item.getText());
                             } catch (Exception ex) {
                                Logger.getLogger(FXMLAccountViewController.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        });
                    }

                }
            };
            return cell;
        }
    }

    private class ColumnFormatter<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

        private final DateTimeFormatter formatter = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm");

        public ColumnFormatter() {
            super();
        }

        @Override
        public TableCell<S, T> call(TableColumn<S, T> arg0) {
            return new TableCell<S, T>() {
                @Override
                protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setGraphic(null);
                    } else {
                        String val = item.toString();
                        if (item instanceof DateTime) {
                            DateTime ld = (DateTime) item;
                            val = formatter.print(ld);

                        }
                        if (item instanceof Hyperlink) {

                        }
                        setGraphic(new Label(val));
                        setAlignment(Pos.CENTER);
                    }
                }
            };
        }
    }
}
