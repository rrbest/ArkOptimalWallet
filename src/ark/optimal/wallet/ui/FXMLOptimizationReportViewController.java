/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import ark.optimal.wallet.pojo.Account;
import ark.optimal.wallet.pojo.Delegate;
import ark.optimal.wallet.services.storageservices.StorageService;
import ark.optimal.wallet.ui.main.HostServicesProvider;
import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
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
public class FXMLOptimizationReportViewController implements Initializable {

    @FXML
    private JFXButton executeTrades;
    @FXML
    private JFXButton closeReport;
    @FXML
    private TableView<OptimizationReportItem> votedDelegatesTable;
    @FXML
    private TableColumn<OptimizationReportItem, String> delegateName;
    @FXML
    private TableColumn<OptimizationReportItem, Integer> delegateRank;
    @FXML
    private TableColumn<OptimizationReportItem, Hyperlink> subwalletAddress;
    @FXML
    private TableColumn<OptimizationReportItem, Integer> votes;
    @FXML
    private TableColumn<OptimizationReportItem, Double> payout;

    private FXMLSubWalletManagerViewController subWalletManagerController;

    public void setSubWalletManagerController(FXMLSubWalletManagerViewController subWalletManagerController) {
        this.subWalletManagerController = subWalletManagerController;
    }
    @FXML
    private Label optReportTitle;
    
    private Account account;
    private String passphrase;
    private Map<String, Double> optVotes;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        delegateName.setCellValueFactory(new PropertyValueFactory<OptimizationReportItem, String>("delegateName"));
        delegateName.setCellFactory(new FXMLOptimizationReportViewController.ColumnFormatter<OptimizationReportItem, String>());

        delegateRank.setCellValueFactory(new PropertyValueFactory<OptimizationReportItem, Integer>("delegateRank"));
        delegateRank.setCellFactory(new FXMLOptimizationReportViewController.ColumnFormatter<OptimizationReportItem, Integer>());

        votes.setCellValueFactory(new PropertyValueFactory<OptimizationReportItem, Integer>("votes"));
        votes.setCellFactory(new FXMLOptimizationReportViewController.ColumnFormatter<OptimizationReportItem, Integer>());

        subwalletAddress.setCellValueFactory(new PropertyValueFactory<OptimizationReportItem, Hyperlink>("subwalletAddress_link"));
        subwalletAddress.setCellFactory(new FXMLOptimizationReportViewController.HyperlinkCell());

        payout.setCellValueFactory(new PropertyValueFactory<OptimizationReportItem, Double>("payout"));
        payout.setCellFactory(new FXMLOptimizationReportViewController.ColumnFormatter<OptimizationReportItem, Double>());

    }

    @FXML
    private void onExecuteTrades(ActionEvent event) {
       List<OptimizationReportItem> trades = votedDelegatesTable.getItems();
       subWalletManagerController.executeOptimizationTrades(account, passphrase, optVotes);
       closeWindow();
    }

    @FXML
    private void onCloseReport(ActionEvent event) {
        closeWindow();

    }

    private void closeWindow() {
        Stage stage = (Stage) closeReport.getScene().getWindow();
        stage.close();
    }

    public void updateReport(Account account,String passphrase ,Map<String, Double> votes) {
        this.passphrase = passphrase;
        this.optVotes = votes;
        this.account = account;
        double balance = votes.values().stream().mapToDouble(Double::doubleValue).sum();
        optReportTitle.setText("Optimization Report - "+ account.getUsername() + " / Ѧ" + balance);
        votedDelegatesTable.getItems().clear();
        for (String delegateName : votes.keySet()) {
            String subUsername = account.getUsername() + "(" + delegateName + ")";
            Delegate d = StorageService.getInstance().getWallet().getDelegates().get(delegateName);
            double includedvotes = 100 * ( d.getVote() * (1 - d.getExlcudedPercentage()/100.0));
            double payout = 422 * d.getPayoutPercentage() * votes.get(delegateName) / (includedvotes);
            OptimizationReportItem oi = new OptimizationReportItem(delegateName, d.getRate(), subUsername, votes.get(delegateName).intValue(), payout);
            votedDelegatesTable.getItems().add(oi);
        }

    }

    private class HyperlinkCell implements Callback<TableColumn<OptimizationReportItem, Hyperlink>, TableCell<OptimizationReportItem, Hyperlink>> {

        @Override
        public TableCell<OptimizationReportItem, Hyperlink> call(TableColumn<OptimizationReportItem, Hyperlink> arg) {
            TableCell<OptimizationReportItem, Hyperlink> cell = new TableCell<OptimizationReportItem, Hyperlink>() {
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
