/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import ark.optimal.wallet.pojo.Delegate;
import ark.optimal.wallet.services.optimizationservices.OptimizationService;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import org.apache.commons.lang3.StringUtils;
/**
 * FXML Controller class
 *
 * @author Mastadon
 */
public class FXMLOptimizationSetupViewController implements Initializable {

    @FXML
    private JFXButton optimize;
    @FXML
    private JFXButton optimizationCancel;
    @FXML
    private JFXTextField scenarioMasterVotes;
    @FXML
    private JFXTextField scenarioName;
    
    private List<Delegate> selectedDelegates;
    private FXMLDelegatesViewController delegateViewController;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        this.scenarioName.setText("Scenario1");
        this.scenarioMasterVotes.setText("1000");
         
    }

    @FXML
    private void onOptimize(ActionEvent event) {
        if (scenarioName.getText() == null || scenarioMasterVotes.getText() == null){
            new AlertController().alertUser("Please enter scenario name and Votes/Arks");
            return;
        }
        if(! StringUtils.isNumeric(scenarioMasterVotes.getText())){
            new AlertController().alertUser("Please enter numeric value for scenario Votes/Arks");
            return;
        }
        Integer scenarioValue = new Double(scenarioMasterVotes.getText()).intValue();

        Map<String, Double> votes = OptimizationService.runConvexOptimizattion(scenarioValue , selectedDelegates);
        runOptimizationReport(votes);
        
        closeWindow();
    }
    private void runOptimizationReport(Map<String, Double> votes) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLOptimizationReportView.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            FXMLOptimizationReportViewController optReportController = (FXMLOptimizationReportViewController) fxmlLoader.getController();
            optReportController.createReport(scenarioName.getText(), votes);
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

    @FXML
    private void onOptimizationCancel(ActionEvent event) {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) optimize.getScene().getWindow();
        stage.close();
    }

    public void setDelegateViewController(FXMLDelegatesViewController delegateViewController) {
        this.delegateViewController = delegateViewController;
    }

    public void setSelectedDelegates(List<Delegate> selectedDelegates) {
        this.selectedDelegates = selectedDelegates;
    }
    
}
