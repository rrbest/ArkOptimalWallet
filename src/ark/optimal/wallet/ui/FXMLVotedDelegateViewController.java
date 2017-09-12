/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

/**
 * FXML Controller class
 *
 * @author Mastadon
 */
public class FXMLVotedDelegateViewController implements Initializable {

    @FXML
    private TableView<DelegateItem> votedDelegateTable;
    @FXML
    private TableColumn<DelegateItem, Boolean> votedDelegateCheck;
    @FXML
    private TableColumn<DelegateItem, Integer> delegateRank;
    @FXML
    private TableColumn<DelegateItem, String> delegateName;
    @FXML
    private TableColumn<DelegateItem, Hyperlink> delegateAddress;
    @FXML
    private TableColumn<DelegateItem, Double> delegateApproval;
    @FXML
    private TableColumn<DelegateItem, Double> delegateProductivity;
    @FXML
    private JFXButton votebtn;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

        votedDelegateTable.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<DelegateItem>() {

            public void changed(
                    ObservableValue<? extends DelegateItem> observable,
                    DelegateItem oldValue,
                    DelegateItem newValue) {
                if (newValue == null) {
                    return;
                }
            }

        });
        votedDelegateTable.setRowFactory(new Callback<TableView<DelegateItem>, TableRow<DelegateItem>>() {
            @Override
            public TableRow<DelegateItem> call(TableView<DelegateItem> tableView2) {
                final TableRow<DelegateItem> row = new TableRow<>();
                row.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        final int index = row.getIndex();
                        if (index >= votedDelegateTable.getItems().size()) {
                            votedDelegateTable.getSelectionModel().clearSelection();
                            event.consume();
                        }
                    }
                });
                return row;
            }
        });

        delegateName.setCellValueFactory(new PropertyValueFactory<DelegateItem, String>("username"));
        delegateRank.setCellValueFactory(new PropertyValueFactory<DelegateItem, Integer>("rate"));
        delegateApproval.setCellValueFactory(new PropertyValueFactory<DelegateItem, Double>("approval"));
        delegateProductivity.setCellValueFactory(new PropertyValueFactory<DelegateItem, Double>("productivity"));
        votedDelegateCheck.setCellValueFactory(new PropertyValueFactory<DelegateItem, Boolean>("checked"));

        delegateAddress.setCellValueFactory(new PropertyValueFactory<DelegateItem, Hyperlink>("address"));
        delegateAddress.setCellFactory(new FXMLVotedDelegateViewController.HyperlinkCell());

        votedDelegateCheck.setCellFactory(p -> {
            CheckBox checkBox = new CheckBox();
            TableCell<DelegateItem, Boolean> tableCell = new TableCell<DelegateItem, Boolean>() {

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
                    -> validate(checkBox, (DelegateItem) tableCell.getTableRow().getItem(), event));

            checkBox.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.SPACE) {
                    validate(checkBox, (DelegateItem) tableCell.getTableRow().getItem(), event);
                }
            });

            tableCell.setAlignment(Pos.CENTER);
            tableCell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

            return tableCell;
        });

        votedDelegateCheck.setEditable(true);
    }

    private class HyperlinkCell implements Callback<TableColumn<DelegateItem, Hyperlink>, TableCell<DelegateItem, Hyperlink>> {

        @Override
        public TableCell<DelegateItem, Hyperlink> call(TableColumn<DelegateItem, Hyperlink> arg) {
            TableCell<DelegateItem, Hyperlink> cell = new TableCell<DelegateItem, Hyperlink>() {
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
                                u = new URI(item.getText());
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

    private void validate(CheckBox checkBox, DelegateItem delegate, Event event) {
        event.consume();
        checkBox.setSelected(!checkBox.isSelected());
        delegate.setChecked(checkBox.isSelected());
        System.out.println(delegate.getUsername() + " selected : " + checkBox.isSelected());

    }

    public void setVotedDelegate(DelegateItem delegate) {
        votedDelegateTable.getItems().clear();
        votedDelegateTable.getItems().add(delegate);
        votedDelegateTable.requestFocus();
        votedDelegateTable.getSelectionModel().select(delegate);

    }
}
