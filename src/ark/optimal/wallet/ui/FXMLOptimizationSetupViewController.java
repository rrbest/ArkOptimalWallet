/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import ark.optimal.wallet.pojo.Account;
import ark.optimal.wallet.services.storageservices.StorageService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author Mastadon
 */
public class FXMLOptimizationSetupViewController implements Initializable {

    @FXML
    private JFXTextField passphrase;
    @FXML
    private JFXButton optimize;
    @FXML
    private JFXButton optimizationCancel;
    @FXML
    private JFXComboBox<Account> accounts;

    private FXMLSubWalletManagerViewController subWalletManagerViewController;
    @FXML
    private JFXTextField masterPercentageOptimization;
   

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        ObservableList<Account> userAccounts = FXCollections.observableArrayList();
         userAccounts.addAll(StorageService.getInstance().getWallet().getUserAccounts().values());
         accounts.setItems(userAccounts);
         
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
         
    }

    @FXML
    private void onOptimize(ActionEvent event) {
        Account account = accounts.getSelectionModel().getSelectedItem();
        if (account == null || masterPercentageOptimization.getText() == null){
            new AlertController().alertUser("Please complete optimization form");
            return;
        }
       // delegateViewController.optimize(account, passphrase.getText(), new Double(masterPercentageOptimization.getText()));
        closeWindow();
    }

    @FXML
    private void onOptimizationCancel(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) optimize.getScene().getWindow();
        stage.close();
    }
}
