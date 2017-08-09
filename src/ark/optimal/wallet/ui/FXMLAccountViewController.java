/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import ark.optimal.wallet.pojo.Account;
import ark.optimal.wallet.pojo.Delegate;
import ark.optimal.wallet.pojo.Transaction;
import ark.optimal.wallet.qrcode.QRCodeGenerator;
import ark.optimal.wallet.services.accountservices.AccountService;
import ark.optimal.wallet.services.storageservices.StorageService;
import ark.optimal.wallet.services.xchangeservices.XChangeServices;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import java.awt.image.BufferedImage;
import java.io.IOException;
import static java.lang.Math.round;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * FXML Controller class
 *
 * @author Mastadon
 */
public class FXMLAccountViewController implements Initializable {

    @FXML
    private AnchorPane holderAnchor;
    @FXML
    private JFXButton accountBalanceExchangeValue;
    @FXML
    private JFXButton accountAddress;
    @FXML
    private Tooltip accountAddressTooltip;
    @FXML
    private ImageView qrcodeImageView;

    private QRCodeGenerator qrCodeGenerator;
    @FXML
    private JFXDrawer accountsDrawer;
    @FXML
    private JFXHamburger accountsBurger;
    @FXML
    private Label accountName;
    @FXML
    private Label accountBalance;
    @FXML
    private JFXButton btnTransactions;
    @FXML
    private JFXButton btnVotes;
    @FXML
    private JFXButton btnExchange;
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
    @FXML
    private JFXButton btnSendArk;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // TODO
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLAccountsViewMenu.fxml"));
            VBox vb = loader.load();
            FXMLAccountsViewMenuController menuController = loader.getController();
            menuController.setAccountViewController(this);

            accountsDrawer.setSidePane(vb);

            if (accountAddress.getText() != null && accountAddress.getText() != "") {
                qrCodeGenerator = new QRCodeGenerator();
                BufferedImage bufferedImage = qrCodeGenerator.generateQRCode(accountAddress.getText(), 128, 128);
                qrcodeImageView.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
            }
            HamburgerBackArrowBasicTransition burgerTask = new HamburgerBackArrowBasicTransition(accountsBurger);
            burgerTask.setRate(-1);
            accountsBurger.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> {
                burgerTask.setRate(burgerTask.getRate() * -1);
                burgerTask.play();
                if (accountsDrawer.isShown()) {
                    accountsDrawer.close();
                } else {
                    accountsDrawer.open();

                }
            });

            _transactionid.setCellValueFactory(new PropertyValueFactory<TransactionItem, Hyperlink>("id_link"));
            _transactionid.setCellFactory(new HyperlinkCell());

            _transactionconfirmations.setCellValueFactory(new PropertyValueFactory<TransactionItem, Integer>("confirmations"));
            _transactionconfirmations.setCellFactory(new ColumnFormatter<TransactionItem, Integer>());

            _transactiondate.setCellValueFactory(new PropertyValueFactory<TransactionItem, DateTime>("date"));
            _transactiondate.setCellFactory(new ColumnFormatter<TransactionItem, DateTime>());

            _transactiontype.setCellValueFactory(new PropertyValueFactory<TransactionItem, String>("type"));
            _transactiontype.setCellFactory(new ColumnFormatter<TransactionItem, String>());

            __transactiontotal.setCellValueFactory(new PropertyValueFactory<TransactionItem, Double>("amount"));
            __transactiontotal.setCellFactory(new ColumnFormatter<TransactionItem, Double>());

            __transactionfrom.setCellValueFactory(new PropertyValueFactory<TransactionItem, String>("from"));
            __transactionfrom.setCellFactory(new ColumnFormatter<TransactionItem, String>());

            __transactionto.setCellValueFactory(new PropertyValueFactory<TransactionItem, String>("to"));
            __transactionto.setCellFactory(new ColumnFormatter<TransactionItem, String>());

            __transactionSmartBridge.setCellValueFactory(new PropertyValueFactory<TransactionItem, String>("smartBridge"));
            __transactionSmartBridge.setCellFactory(new ColumnFormatter<TransactionItem, String>());

            /*transactionsTable.setRowFactory(param -> new TableRow<TransactionItem>() {
                @Override
                protected void updateItem(TransactionItem item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        disableProperty().bind(true);
                    }
                }
            });*/
        } catch (IOException ex) {
            Logger.getLogger(FXMLAccountViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void onCopyAddress(ActionEvent event) {
    }

    @FXML
    private void OnAccountBalanceExchangeValue(ActionEvent event) {
    }

    void selectAccount(Account account) {
        accountName.setText(account.getUsername());
        accountAddress.setText(account.getAddress());
        accountBalance.setText(accountBalance.getText().charAt(0) + account.getBalance().toString());
        accountBalanceExchangeValue.setText(NumberFormat.getCurrencyInstance().format(new Double(account.getBalance() * XChangeServices.getPrice("usd"))));
        updateTransactionsTable(account.getTransactions());
        addQRCode(account.getAddress());
        btnTransactions.setFocusTraversable(true);
    }

    @FXML
    private void onFetchTransactions(ActionEvent event) {
        List<Transaction> transactions = AccountService.getTransactions(accountAddress.getText(), 50);
        updateTransactionsTable(transactions);
    }

    private void updateTransactionsTable(List<Transaction> transactions) {

        if (transactions == null)
            return;
        transactionsTable.getItems().clear();

        for (Transaction t : transactions) {
            System.out.println(t.getId());
            System.out.println(t.getAmount());
            System.out.println(formatTransactionTimeStamp((long) t.getTimestamp()));
            DateTime dt = ConvertTransactionTimeStampToLocal(t.getTimestamp());
            Double amount = t.getAmount().doubleValue() / 100000000.0;
            String type = "";
            if (t.getType() == 0) {
                type = "Receive Ark";
            } else if (t.getType() == 3) {
                type = "Vote";
                amount = -1 * t.getFee().doubleValue() / 100000000.0;

            } else if (t.getType() == 2) {
                type = "Delegate Registration";
                amount = -1 * t.getFee().doubleValue() / 100000000.0;
            } else {
                type = "Send Ark";
                amount = -1 * amount;
            }
            String smartbridge = t.getVendorField();
            String from = t.getFrom();
            if (t.getSenderId() != null) {
                Account account = StorageService.getInstance().getUserAccounts().get(t.getSenderId());
                if (account != null) {
                    from = account.getUsername();
                } else {
                    account = StorageService.getInstance().getWatchAccounts().get(t.getSenderId());
                    if (account != null) {
                        from = account.getUsername();
                    }
                }

            }

            String to = t.getTo();
            if (t.getRecipientId() != null) {
                Account account = StorageService.getInstance().getUserAccounts().get(t.getRecipientId());
                if (account != null) {
                    to = account.getUsername();
                } else {
                    account = StorageService.getInstance().getWatchAccounts().get(t.getRecipientId());
                    if (account != null) {
                        to = account.getUsername();
                    }
                }

            }

            TransactionItem ti = new TransactionItem(t.getId(), t.getConfirmations(), dt, type, amount, from, to, smartbridge);
            transactionsTable.getItems().add(ti);

        }

        if (transactionsTable.getItems().size() * 40 > 450) {
            transactionsTable.setPrefHeight(transactionsTable.getItems().size() * 40);
        }
    }

    @FXML
    private void onFetchVotes(ActionEvent event) {
    }

    @FXML
    private void onExchange(ActionEvent event) {
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

    private void addQRCode(String address) {
        if (address != null && address != "") {
            qrCodeGenerator = new QRCodeGenerator();
            BufferedImage bufferedImage = qrCodeGenerator.generateQRCode(accountAddress.getText(), 128, 128);
            qrcodeImageView.setImage(SwingFXUtils.toFXImage(bufferedImage, null));
        }
    }

    @FXML
    private void onSendArk(ActionEvent event) {
        
        if (accountAddress.getText() == null)
            return;
        
         try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLSendArkView.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            FXMLSendArkViewController sendArkController = (FXMLSendArkViewController) fxmlLoader.getController();
            sendArkController.setAccountMenuController(this);
            sendArkController.setSenderAddress(accountAddress.getText());
            
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
                            URI u;
                            try {
                                u = new URI("https://explorer.ark.io/tx/" + item.getText());
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
