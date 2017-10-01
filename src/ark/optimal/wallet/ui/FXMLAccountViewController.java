/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import ark.optimal.wallet.pojo.Account;
import ark.optimal.wallet.qrcode.QRCodeGenerator;
import ark.optimal.wallet.services.accountservices.AccountService;
import ark.optimal.wallet.services.storageservices.StorageService;
import ark.optimal.wallet.services.xchangeservices.XChangeServices;
import ark.optimal.wallet.ui.main.FXMLArkOptimalWalletMainViewController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.transitions.hamburger.HamburgerBackArrowBasicTransition;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.ScrollEvent;

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
    private JFXButton btnSendArk;
    @FXML
    private AnchorPane accountHolderPane;

    private AnchorPane transactionsView, votesView;
    private FXMLTransactionsViewController transactionsViewController;
    private FXMLVotesViewController votesViewController;
    private FXMLAccountsViewMenuController menuController;

    private FXMLAccountOperationsViewController accountOpController;
    private Account account;
    @FXML
    private Label copyToCliboardLabel;
    @FXML
    private JFXButton AccountOperationMenuBtn;
    @FXML
    private JFXDrawer accountOperationsDrawer;
    private FXMLArkOptimalWalletMainViewController mainViewController;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // TODO
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLAccountsViewMenu.fxml"));
            ScrollPane sp = loader.load();
            sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

            menuController = loader.getController();
            menuController.setAccountViewController(this);

            accountsDrawer.setSidePane(sp);

            loader = new FXMLLoader(getClass().getResource("FXMLAccountOperationsView.fxml"));
            AnchorPane ap = loader.load();
            accountOpController = (FXMLAccountOperationsViewController) loader.getController();
            accountOpController.setAccountViewController(this);

            accountOperationsDrawer.setSidePane(ap);

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
                    accountsDrawer.setVisible(false);
                } else {
                    accountsDrawer.open();
                    accountsDrawer.setVisible(true);

                }
            });

            createPages();

            Timeline updateTimeline = new Timeline(new KeyFrame(Duration.seconds(8), new EventHandler<ActionEvent>() {

                @Override
                public void handle(ActionEvent event) {
                    try {
                        refreshAccount();
                    } catch (Exception ex) {
                        Logger.getLogger(FXMLTransactionsViewController.class.getName()).log(Level.WARNING, null, ex);
                        Thread.currentThread().interrupt();
                    }
                }
            }));
            updateTimeline.setCycleCount(Timeline.INDEFINITE);
            updateTimeline.play();

        } catch (IOException ex) {
            Logger.getLogger(FXMLAccountViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void refreshAccount() {
        if (this.account == null) {
            return;
        }
        Account updateAccount = StorageService.getInstance().getWallet().getUserAccounts().get(this.account.getAddress());
        if (updateAccount == null) {
            updateAccount = StorageService.getInstance().getWallet().getSubAccounts().get(this.account.getAddress());
        }
        if (updateAccount == null) {
            return;
        }
        this.accountBalance.setText(this.accountBalance.getText().charAt(0) + updateAccount.getBalance().toString());
        this.accountBalanceExchangeValue.setText(NumberFormat.getCurrencyInstance().format(new Double(updateAccount.getBalance() * XChangeServices.getInstance().getPrice())));
        transactionsViewController.refreshTransactionsTable(account);

    }

    public FXMLAccountsViewMenuController getMenuController() {
        return menuController;
    }

    @FXML
    private void onCopyAddress(ActionEvent event) {
        try {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(accountAddress.getText());
            clipboard.setContent(content);
            copyToCliboardLabel.setVisible(true);

            FadeTransition fadeOut = new FadeTransition(Duration.millis(2000));
            fadeOut.setNode(copyToCliboardLabel);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setCycleCount(1);
            fadeOut.setAutoReverse(false);
            //copyToCliboardLabel.setVisible(false);
            fadeOut.playFromStart();

            //TimeUnit.SECONDS.sleep(10);
            //copyToCliboardLabel.setVisible(false);
        } catch (Exception ex) {
            Logger.getLogger(FXMLAccountViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void OnAccountBalanceExchangeValue(ActionEvent event) {
    }

    public void selectAccount(Account account) {
        //Map<String, Account> subs = account.getSubAccounts();
        //String name = account.getUsername();
        //account = AccountService.getFullAccount(account.getAddress());
        //account.setSubAccounts(subs);
        //account.setUsername(name);
        this.account = account;
        accountName.setText(account.getUsername());
        accountAddress.setText(account.getAddress());
        accountBalance.setText(accountBalance.getText().charAt(0) + account.getBalance().toString());
        accountBalanceExchangeValue.setText(NumberFormat.getCurrencyInstance().format(new Double(account.getBalance() * XChangeServices.getInstance().getPrice())));
        transactionsViewController.updateTransactionsTable(account);
        addQRCode(account.getAddress());
        setNode(transactionsView);
        menuController.selectAccountItem(account);
        StorageService.getInstance().addAccountToUserAccounts(account, true);
        btnTransactions.requestFocus();
        closeAccountOperationsDrawer();

    }

    public void selectSubAccount(Account subAccount) {
        //String name = subAccount.getUsername();
        //subAccount = AccountService.getFullAccount(subAccount.getAddress());
        //subAccount.setUsername(name);
        this.account = subAccount;
        accountName.setText(subAccount.getUsername());
        accountAddress.setText(subAccount.getAddress());
        accountBalance.setText(accountBalance.getText().charAt(0) + subAccount.getBalance().toString());
        accountBalanceExchangeValue.setText(NumberFormat.getCurrencyInstance().format(new Double(subAccount.getBalance() * XChangeServices.getInstance().getPrice())));
        transactionsViewController.updateTransactionsTable(subAccount);
        addQRCode(subAccount.getAddress());
        setNode(transactionsView);
        menuController.selectSubAccountItem(subAccount);
        btnTransactions.requestFocus();
    }

    @FXML
    private void onFetchTransactions(ActionEvent event) {
        //Account account = AccountService.getFullAccount(accountAddress.getText());
        //List<Transaction> transactions = AccountService.getTransactions(accountAddress.getText(), 50);
        Account account = StorageService.getInstance().getWallet().getUserAccounts().get(accountAddress.getText());
        if (account == null) {
            String masterAccountAddress = this.account.getMasterAccountAddress();
            if (masterAccountAddress != null) {
                /*   Account masterAccount = StorageService.getInstance().getWallet().getUserAccounts().get(masterAccountAddress);
                List<Account> subs = (List<Account>) masterAccount.getSubAccounts().values();
                for (Account sub : subs){
                    if(sub.getAddress() == this.account.getAddress()){
                        account = sub;
                        break;
                    }
                        
                }
                 */
                account = StorageService.getInstance().getWallet().getSubAccounts().get(accountAddress.getText());
            }
        }
        if (account == null) {
            account = AccountService.getFullAccount(accountAddress.getText());
        }
        transactionsViewController.updateTransactionsTable(account);
        btnTransactions.requestFocus();
        setNode(transactionsView);
        closeAccountOperationsDrawer();

    }

    private void setNode(Node node) {
        accountHolderPane.getChildren().clear();
        accountHolderPane.getChildren().add((Node) node);

        FadeTransition ft = new FadeTransition(Duration.millis(500));
        ft.setNode(node);
        ft.setFromValue(0.1);
        ft.setToValue(1);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);
        ft.play();
    }

    private void createPages() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLTransactionsView.fxml"));
            transactionsView = fxmlLoader.load();
            transactionsViewController = (FXMLTransactionsViewController) fxmlLoader.getController();
            transactionsViewController.setAccountsViewController(this);

            fxmlLoader = new FXMLLoader(getClass().getResource("FXMLVotesView.fxml"));
            votesView = fxmlLoader.load();
            votesViewController = (FXMLVotesViewController) fxmlLoader.getController();
            votesViewController.setAccountsViewController(this);
            //set up default node on page load
            setNode(transactionsView);
        } catch (IOException ex) {
            Logger.getLogger(FXMLArkOptimalWalletMainViewController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @FXML
    private void onFetchVotes(ActionEvent event) {
        btnVotes.requestFocus();
        votesViewController.viewVotes(this.account);
        setNode(votesView);
        closeAccountOperationsDrawer();
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

        if (accountAddress.getText() == null) {
            return;
        }

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
        closeAccountOperationsDrawer();
    }

    public void runImportAccount() {
        menuController.runImportAccount();

    }

    public void runCreateAccount() {
        menuController.runCreateAccount();

    }

    public void runWatchAccount() {
        menuController.runWatchAccount();

    }

    public void addToUserAccountsMenu(Account account) {
        menuController.addToUserAccountsMenu(account);
    }

    public void addToWatchAccountsMenu(Account account) {
        menuController.addToWatchAccountsMenu(account);
    }

    public void clearAccountsMenu() {
        menuController.clearAccountsMenu();
    }

    @FXML
    private void onAccountOperationMenu(ActionEvent event) {
        if (!accountOperationsDrawer.isShown()) {
            accountOperationsDrawer.open();
            accountOperationsDrawer.setVisible(true);

        } else {
            accountOperationsDrawer.close();
            accountOperationsDrawer.setVisible(false);

        }

        accountOpController.setAccount(this.account);

    }

    public void closeAccountOperationsDrawer() {
        accountOperationsDrawer.close();
        accountOperationsDrawer.setVisible(false);
    }

    void viewHome() {
        this.mainViewController.viewHome();
    }

    public void setMainViewControler(FXMLArkOptimalWalletMainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    void removeAccountFromHome(Account account) {
        mainViewController.removeAccountFromHome(account);
    }

}
