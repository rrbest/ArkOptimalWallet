/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import ark.optimal.wallet.pojo.Account;
import ark.optimal.wallet.pojo.Transaction;
import ark.optimal.wallet.services.storageservices.StorageService;
import ark.optimal.wallet.ui.main.HostServicesProvider;
import com.google.common.collect.HashBiMap;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * FXML Controller class
 *
 * @author Mastadon
 */
public class FXMLTransactionsViewController implements Initializable {

    @FXML
    private TableView<TransactionItem> transactionsTable;
    @FXML
    private TableColumn<TransactionItem, Hyperlink> _transactionid;
    @FXML
    private TableColumn<TransactionItem, Integer> _transactionconfirmations;
    @FXML
    private TableColumn<TransactionItem, DateTime> _transactiondate;
    @FXML
    private TableColumn<TransactionItem, String> _transactiontype;
    @FXML
    private TableColumn<TransactionItem, Double> __transactiontotal;
    @FXML
    private TableColumn<TransactionItem, String> __transactionfrom;
    @FXML
    private TableColumn<TransactionItem, String> __transactionto;
    @FXML
    private TableColumn<TransactionItem, String> __transactionSmartBridge;

    private FXMLAccountViewController accountViewController;

    private Account account;
    private Map<String, TransactionItem> transactionsItems;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        transactionsItems = new HashMap<String, TransactionItem>();
        _transactionid.setCellValueFactory(new PropertyValueFactory<TransactionItem, Hyperlink>("id_link"));
        _transactionid.setCellFactory(new FXMLTransactionsViewController.HyperlinkCell());

        _transactionconfirmations.setCellValueFactory(new PropertyValueFactory<TransactionItem, Integer>("confirmations"));
        _transactionconfirmations.setCellFactory(new FXMLTransactionsViewController.ColumnFormatter<TransactionItem, Integer>());

        _transactiondate.setCellValueFactory(new PropertyValueFactory<TransactionItem, DateTime>("date"));
        _transactiondate.setCellFactory(new FXMLTransactionsViewController.ColumnFormatter<TransactionItem, DateTime>());

        _transactiontype.setCellValueFactory(new PropertyValueFactory<TransactionItem, String>("type"));
        _transactiontype.setCellFactory(new FXMLTransactionsViewController.ColumnFormatter<TransactionItem, String>());

        __transactiontotal.setCellValueFactory(new PropertyValueFactory<TransactionItem, Double>("amount"));
        __transactiontotal.setCellFactory(new FXMLTransactionsViewController.ColumnFormatter<TransactionItem, Double>());

        __transactionfrom.setCellValueFactory(new PropertyValueFactory<TransactionItem, String>("from"));
        __transactionfrom.setCellFactory(new FXMLTransactionsViewController.ColumnFormatter<TransactionItem, String>());

        __transactionto.setCellValueFactory(new PropertyValueFactory<TransactionItem, String>("to"));
        __transactionto.setCellFactory(new FXMLTransactionsViewController.ColumnFormatter<TransactionItem, String>());

        __transactionSmartBridge.setCellValueFactory(new PropertyValueFactory<TransactionItem, String>("smartBridge"));
        __transactionSmartBridge.setCellFactory(new FXMLTransactionsViewController.ColumnFormatter<TransactionItem, String>());

        transactionsTable.setPlaceholder(new Label("No Transactions"));


    }

    public void setAccountsViewController(FXMLAccountViewController accountViewController) {
        this.accountViewController = accountViewController;
    }

    public void refreshTransactionsTable(Account account) {
        if (account == null) {
            account = this.account;
        }
        if (account == null){
            return;
        }
        Account updatedAccount = StorageService.getInstance().getWallet().getUserAccounts().get(account.getAddress());
        if (updatedAccount == null) {
            updatedAccount = StorageService.getInstance().getWallet().getSubAccounts().get(account.getAddress());
        }
        if (updatedAccount == null) {
            return;
        }
        List<Transaction> transactions = updatedAccount.getTransactions();
        if (transactions == null) {
            return;
        }
        for (Transaction t : transactions) {
            TransactionItem ti = transactionsItems.get(t.getId());
            if (ti != null) {
                ti.setConfirmations(t.getConfirmations());
            } else {
                ti = buildTranscationItem(t);
                transactionsTable.getItems().add(0,ti);
                transactionsItems.put(ti.getId(), ti);
            }

        }
        transactionsTable.sort();
        transactionsTable.refresh();
    }

    private TransactionItem buildTranscationItem(Transaction t) {
        DateTime dt = ConvertTransactionTimeStampToLocal(t.getTimestamp());
        Double fee = t.getFee().doubleValue() / 100000000.0;
        Double amount = t.getAmount().doubleValue() / 100000000.0;
        String type = "";
        if (t.getType() == 0) {
            if (account.getAddress().equals(t.getSenderId())) {
                type = "Send Ark";
                amount = -1 * (amount + fee);
            } else {
                type = "Receive Ark";
            }
        } else if (t.getType() == 3) {
            type = "Vote";
            amount = -1 * t.getFee().doubleValue() / 100000000.0;

        } else if (t.getType() == 2) {
            type = "Delegate Registration";
            amount = -1 * t.getFee().doubleValue() / 100000000.0;
        }

        String smartbridge = t.getVendorField();
        String from = t.getFrom();
        if (t.getSenderId() != null) {
            Account acc = StorageService.getInstance().getWallet().getUserAccounts().get(t.getSenderId());
            if (acc != null) {
                from = acc.getUsername();
            } else {
                acc = StorageService.getInstance().getWallet().getSubAccounts().get(t.getSenderId());
                if (acc != null) {
                    from = acc.getUsername();
                }
            }

        }

        String to = t.getTo();
        if (t.getRecipientId() != null) {
            Account acc = StorageService.getInstance().getWallet().getUserAccounts().get(t.getRecipientId());
            if (acc != null) {
                to = acc.getUsername();
            } else {
                acc = StorageService.getInstance().getWallet().getSubAccounts().get(t.getRecipientId());
                if (acc != null) {
                    to = acc.getUsername();
                }
            }

        }

        TransactionItem ti = new TransactionItem(t.getId(), t.getConfirmations(), dt, type, amount, from, to, smartbridge);
        return ti;
    }

    public void updateTransactionsTable(Account account) {
        this.account = account;
        transactionsTable.getItems().clear();
        transactionsItems.clear();
        if (account == null) {
            return;
        }
        List<Transaction> transactions = account.getTransactions();
        if (transactions == null) {
            return;
        }
        for (Transaction t : transactions) {
            TransactionItem ti = buildTranscationItem(t);
            transactionsTable.getItems().add(ti);
            transactionsItems.put(ti.getId(), ti);

        }

    }

    private String formatTransactionTimeStamp(long timestamp) {

        DateTime d = new DateTime(2017, 3, 21, 13, 0, 0, 0);
        long start = d.getMillis() / 1000;
        Timestamp date = new Timestamp((long) (timestamp + start) * 1000);
        System.out.println(date.toString());

        DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yy HH:mm:ss");
        String timestr = fmt.print((long) (timestamp + start) * 1000);
        System.out.println(timestr);

        return timestr;
    }

    private DateTime ConvertTransactionTimeStampToLocal(long timestamp) {

        DateTime d = new DateTime(2017, 3, 21, 13, 0, 0, 0);
        long start = d.getMillis() / 1000;
        Timestamp date = new Timestamp((long) (timestamp + start) * 1000);
        System.out.println(date.toString());
        DateTime dt = new LocalDateTime(date.getTime()).toDateTime(DateTimeZone.UTC);

        DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd/yy HH:mm:ss");
        String timestr = fmt.print((long) (timestamp + start) * 1000);
        System.out.println(timestr);

        return dt;
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
