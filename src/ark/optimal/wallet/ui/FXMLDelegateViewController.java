/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import ark.optimal.wallet.pojo.Delegate;
import ark.optimal.wallet.services.accountservices.AccountService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * FXML Controller class
 *
 * @author Mastadon
 */
public class FXMLDelegateViewController implements Initializable {

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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        delegatesMap = new HashMap<String, Delegate>();
        _delegatestable.getSelectionModel().selectedItemProperty().addListener(
                (
                        ObservableValue<? extends Delegate> observable,
                        Delegate oldValue,
                        Delegate newValue) -> {
                    if (newValue == null) {
                        return;
                    }
                    updateDelegateView(newValue);

                });

        _delegatename.setCellValueFactory(new PropertyValueFactory<Delegate, String>("username"));
        _votes.setCellValueFactory(new PropertyValueFactory<Delegate, Integer>("vote"));
        _rank.setCellValueFactory(new PropertyValueFactory<Delegate, Integer>("rate"));
        _approval.setCellValueFactory(new PropertyValueFactory<Delegate, Double>("approval"));
        _productivity.setCellValueFactory(new PropertyValueFactory<Delegate, Double>("productivity"));

    }


    @FXML
    private void onSearch(ActionEvent event) {
        String n = delegateNameOrPublicKey.getText();
        Delegate d = null;
        if (delegatesMap.containsKey(n)){
            d = delegatesMap.get(n);
            
        }else{
            d = AccountService.getDelegateByUsername(n);
            d.setMinPayout(100.0);
            d.setPayoutFrequency(0.25);
            d.setPayoutPercentage(70.0);
            d.setPoolPercentage(90.0);
            delegatesMap.put(d.getUsername(), d);
            _delegatestable.getItems().add(d);
            
        }
        if (d == null) {
            return;
        }
        updateDelegateView(d);
         
        _delegatestable.requestFocus();
        _delegatestable.getSelectionModel().select(d);
        
        

            
            
           // _delegatestable.getFocusModel().focus(0);
           //for (int i = 0; i < _delegatestable.getItems().size(); i++) {
           //     if (_delegatestable.getItems().get(i).getUsername() == d.getUsername()) {
           //         _delegatestable.requestFocus();
           //         _delegatestable.getSelectionModel().select(d);
           //     }
           // }
        
        

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
    private void toClipboard(String c){
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
            updateVoteController.setDelegateName(_delegatestable.getSelectionModel().getSelectedItem().getUsername());
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

}
