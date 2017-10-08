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
import ark.optimal.wallet.services.xchangeservices.XChangeServices;
import ark.optimal.wallet.ui.main.FXMLArkOptimalWalletMainViewController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import io.ark.core.Transaction;
import io.ark.core.TransactionService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.spongycastle.jcajce.provider.asymmetric.dsa.DSASigner;

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
    private Label lbl_delegatename;
    @FXML
    private Label lbl_delegateapproval;
    @FXML
    private Label lbl_delegateproductivity;
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
    private JFXButton removeSelectedDelegate;
    @FXML
    private JFXTextField delegatePayoutPercentage;
    @FXML
    private JFXButton updateSelected;
    @FXML
    private TableColumn<Delegate, Double> _payoutPercentage;
    @FXML
    private TableColumn<Delegate, Integer> _excludedVotes;
    @FXML
    private JFXButton optimizeBtn;
    @FXML
    private JFXButton loadDelegates;
    @FXML
    private JFXTextField delegateExcludedVotes;
    private FXMLArkOptimalWalletMainViewController mainViewController;

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
        _delegatestable.addEventFilter(KeyEvent.KEY_RELEASED, (event) -> {
            if (new KeyCodeCombination(KeyCode.A, KeyCombination.SHORTCUT_DOWN).match(event)) {
                event.consume();
                updateDelegateView(_delegatestable.getSelectionModel().getSelectedItem());
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

        _delegatestable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        _delegatename.setCellValueFactory(new PropertyValueFactory<Delegate, String>("username"));
        _votes.setCellValueFactory(new PropertyValueFactory<Delegate, Integer>("vote"));
        _rank.setCellValueFactory(new PropertyValueFactory<Delegate, Integer>("rate"));
        _payoutPercentage.setCellValueFactory(new PropertyValueFactory<Delegate, Double>("payoutPercentage"));
        _excludedVotes.setCellValueFactory(new PropertyValueFactory<Delegate, Integer>("excludedVotes"));
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

        CheckBox selectAllCB = new CheckBox();
        selectAllCB.setUserData(this._delegateChecked);
        selectAllCB.setOnAction(handleSelectAllCheckbox());
        this._delegateChecked.setGraphic(selectAllCB);

        // intialize from storage 
        for (Delegate delegate : StorageService.getInstance().getWallet().getDelegates().values()) {
            delegate.setChecked(Boolean.FALSE);
            _delegatestable.getItems().add(delegate);
            delegatesMap.put(delegate.getUsername(), delegate);
        }
        _delegatestable.refresh();

        Timeline updateTimeline = new Timeline(new KeyFrame(Duration.minutes(1), new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    refreshDelegates();
                } catch (Exception ex) {
                    Logger.getLogger(FXMLTransactionsViewController.class.getName()).log(Level.WARNING, null, ex);
                    Thread.currentThread().interrupt();
                }
            }
        }));
        updateTimeline.setCycleCount(Timeline.INDEFINITE);
        updateTimeline.play();

    }

    @FXML
    private void onSearch(ActionEvent event) {
        _delegatestable.getSelectionModel().clearSelection();
        String n = delegateNameOrPublicKey.getText();
        searchDelegate(n);
    }

    private Delegate searchDelegate(String n) {
        n = n.replaceAll("\\s+", "");
        delegateNameOrPublicKey.setText("");
        Delegate d = null;
        if (delegatesMap.containsKey(n)) {
            d = delegatesMap.get(n);

        } else {
            d = StorageService.getInstance().getWallet().getDelegates().get(n);
            if (d == null) {
                d = AccountService.getDelegateByUsername(n);
                if (d == null) {
                    new AlertController().alertUser("Delegate doesn't exist");
                    return d;
                }

            }
            _delegatestable.getItems().add(d);
            delegatesMap.put(d.getUsername(), d);
            StorageService.getInstance().addDelegate(d, true);

        }

        updateDelegateView(d);
        _delegatestable.requestFocus();
        _delegatestable.getSelectionModel().select(d);
        _delegatestable.scrollTo(d);
        delegateNameOrPublicKey.setText("");
        return d;
    }

    private void updateDelegateView(Delegate d) {
        Set<Delegate> selection = new HashSet<Delegate>(_delegatestable.getSelectionModel().getSelectedItems());
        if (selection.size() == 0) {
            return;
        }
        Iterator<Delegate> itr = selection.iterator();
        Delegate delegate = itr.next();
        String delegatePPercentageStr = delegate.getPayoutPercentage().toString();
        String delegateExcludedStr = delegate.getExcludedVotes().toString();
        delegatePayoutPercentage.setText(delegatePPercentageStr);
        delegateExcludedVotes.setText(delegateExcludedStr);

        if (selection.size() > 1) {
            _votefordelegate.setDisable(true);
            while (itr.hasNext()) {
                Delegate del = itr.next();
                if (!delegatePPercentageStr.equals(del.getPayoutPercentage().toString())) {
                    delegatePayoutPercentage.setText("");
                }
                if (!delegateExcludedStr.equals(del.getExcludedVotes().toString())) {
                    delegateExcludedVotes.setText("");
                }

            }
            lbl_delegatename.setText("");
            lbl_delegateapproval.setText("");
            lbl_delegateproductivity.setText("");
            lbl_delegateproducedblocks.setText("");
            lbl_delegatemissedblocks.setText("");
            delegateAddress.setText("");
            delegateAddressTooltip.setText("");
            delegatepublickey.setText("");
            delegatePublicKeyTooltip.setText("");
            return;
        }

        _votefordelegate.setDisable(false);
        lbl_delegatename.setText(d.getUsername());
        lbl_delegateapproval.setText(d.getApproval().toString() + "\tApproval");
        lbl_delegateproductivity.setText(String.format("%.2f%%", d.getProductivity()) + "\tProductivity");
        lbl_delegateproducedblocks.setText(d.getProducedblocks().toString() + "\tBlocks");
        lbl_delegatemissedblocks.setText(d.getMissedblocks().toString() + "\tBlocks");
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

    private void selectAll(Boolean select) {
        selectedDelegates.clear();
        if (select) {
            for (Delegate d : _delegatestable.getItems()) {
                d.setChecked(Boolean.TRUE);
                selectedDelegates.add(d);
            }
        } else {
            for (Delegate d : _delegatestable.getItems()) {
                d.setChecked(Boolean.FALSE);
            }
        }
        _delegatestable.refresh();
    }

    private EventHandler<ActionEvent> handleSelectAllCheckbox() {

        return new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                CheckBox cb = (CheckBox) event.getSource();
                TableColumn column = (TableColumn) cb.getUserData();
                if (cb.isSelected()) {
                    selectAll(true);
                } else {
                    selectAll(false);
                }

            }
        };
    }

    @FXML
    private void onRemoveSelectedDelegate(ActionEvent event) {
        event.consume();
        for (Delegate delegate : selectedDelegates) {
            delegatesMap.remove(delegate.getUsername());
            _delegatestable.getItems().remove(delegate);
            //StorageService.getInstance().getWallet().getDelegates().remove(delegate.getUsername());

        }
        _delegatestable.refresh();

    }

    @FXML
    private void PayoutPrecentageReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            updatePayoutPercentage();
        }

    }

    private void updatePayoutPercentage() {
        Set<Delegate> selection = new HashSet<Delegate>(_delegatestable.getSelectionModel().getSelectedItems());
        Iterator<Delegate> itr = selection.iterator();
        while (itr.hasNext()) {
            Delegate d = itr.next();
            d.setPayoutPercentage(new Double(delegatePayoutPercentage.getText()));
            Delegate dirtyDelegate = StorageService.getInstance().getWallet().getDelegates().get(d.getUsername());
            dirtyDelegate.setPayoutPercentage(d.getPayoutPercentage());
            StorageService.getInstance().addDelegate(dirtyDelegate, true);
        }
        _delegatestable.refresh();
    }

    @FXML
    private void excludedPercentageKeyReleased(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            updateExcludedPercentage();
        }
    }

    private void updateExcludedPercentage() {
        Set<Delegate> selection = new HashSet<Delegate>(_delegatestable.getSelectionModel().getSelectedItems());
        Iterator<Delegate> itr = selection.iterator();
        while (itr.hasNext()) {
            Delegate d = itr.next();
            d.setExcludedVotes(new Integer(delegateExcludedVotes.getText()).intValue());
            Delegate dirtyDelegate = StorageService.getInstance().getWallet().getDelegates().get(d.getUsername());
            dirtyDelegate.setExcludedVotes(d.getExcludedVotes());
            StorageService.getInstance().addDelegate(dirtyDelegate, true);

        }
        _delegatestable.refresh();

    }

    @FXML
    private void onUpdateSelected(ActionEvent event) {
        updatePayoutPercentage();
        updateExcludedPercentage();

    }

    @FXML
    private void onSearchDelegate(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            _delegatestable.getSelectionModel().clearSelection();
            String n = delegateNameOrPublicKey.getText();
            searchDelegate(n);
        }

    }

    @FXML
    private void onOptimize(ActionEvent event) {

        if(this.selectedDelegates.size() < 2){
           new AlertController().alertUser("Please select two or more delegates to run optimization");
           return;
        }
        
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLOptimizationSetupView.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            FXMLOptimizationSetupViewController optimizationSetupViewController = (FXMLOptimizationSetupViewController) fxmlLoader.getController();
            optimizationSetupViewController.setDelegateViewController(this);
            optimizationSetupViewController.setSelectedDelegates(this.selectedDelegates);
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

    public void refreshDelegates() {
        if (this.delegatesMap.size() == 0) {
            return;
        }
        for (Delegate d : this._delegatestable.getItems()) {
            Delegate updatedDelegate = StorageService.getInstance().getWallet().getDelegates().get(d.getUsername());
            d.setRate(updatedDelegate.getRate());
            d.setVote(updatedDelegate.getVote());
        }
        this._delegatestable.refresh();
    }

    @FXML
    private void onLoadDelegates(ActionEvent event) {

        try {
            Path path = Paths.get(StorageService.getInstance().getWalletFilePath());
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Delegates File");
            fileChooser.setInitialDirectory(path.getParent().toFile());
            fileChooser.setInitialFileName(path.getFileName().toString());

            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excel File", "*.xlsx"));
            File selectedFile = fileChooser.showOpenDialog(this.mainViewController.getHomeViewController().getAppController().getMainStage());
            if (selectedFile == null) {
                new AlertController().alertUser("Please open delegate file");
                return;
            }

            FileInputStream excelFile = new FileInputStream(selectedFile);
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();
            iterator.next(); // pass header row
            while (iterator.hasNext()) {

                Row currentRow = iterator.next();
                Iterator<Cell> cellIterator = currentRow.iterator();

                Cell delegateCell = cellIterator.next();
                String delegateName = delegateCell.getStringCellValue();
                Cell payoutpctCell = cellIterator.next();
                Double payoutpct = payoutpctCell.getNumericCellValue();
                Cell excludedVotesCell = cellIterator.next();
                Double excludedVotes = excludedVotesCell.getNumericCellValue();
                Delegate delegate = searchDelegate(delegateName);
                if(delegate == null){
                    continue;
                }
                delegate.setPayoutPercentage(payoutpct);
                delegate.setExcludedVotes(excludedVotes.intValue());
                _delegatestable.getSelectionModel().select(delegate);
            }
        } catch (FileNotFoundException e) {
            new AlertController().alertUser("Please make sure to select the delegate XLSX file");
        } catch (IOException e) {
            new AlertController().alertUser("Please make sure the delegate XLSX file has correct data types \n some delegate data might have been loaded");
        } catch (Exception e) {
            new AlertController().alertUser("Please make sure the delegate XLSX file has correct data types \n some delegate data might have been loaded");
        } finally {
            _delegatestable.refresh();
        }
       

    }

    public void setMainViewControler(FXMLArkOptimalWalletMainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }
    private class DelegateData {

        private String name;
        private Double payoutPercentage;
        private Integer excludedVotes;

        public DelegateData(String name, Double payoutPercentage, Integer excludedVotes) {
            this.name = name;
            this.payoutPercentage = payoutPercentage;
            this.excludedVotes = excludedVotes;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getPayoutPercentage() {
            return payoutPercentage;
        }

        public void setPayoutPercentage(Double payoutPercentage) {
            this.payoutPercentage = payoutPercentage;
        }

        public Integer getExcludedVotes() {
            return excludedVotes;
        }

        public void setExcludedVotes(Integer excludedVotes) {
            this.excludedVotes = excludedVotes;
        }
        
    }
}
