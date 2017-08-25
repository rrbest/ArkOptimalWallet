/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import ark.optimal.wallet.pojo.Account;
import ark.optimal.wallet.pojo.Delegate;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeView;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Mastadon
 */
public class FXMLVotesViewController implements Initializable {

    private FXMLAccountViewController accountViewController;
    @FXML
    private TreeView<AccountVotedDelegateTreeItem> accountsTree;

    private Account account;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

    }

    public void setAccountsViewController(FXMLAccountViewController accountViewController) {
        this.accountViewController = accountViewController;
    }

    public void viewVotes(Account account) {
        TreeItem<AccountVotedDelegateTreeItem> root = new TreeItem<AccountVotedDelegateTreeItem>(new AccountVotedDelegateTreeItem(new Label(account.getUsername() + " - Master Wallet")));
        TreeItem<AccountVotedDelegateTreeItem> node = new TreeItem<AccountVotedDelegateTreeItem>(new AccountVotedDelegateTreeItem(account));
        root.getChildren().add(node);

        for (String delegateName : account.getSubAccounts().keySet()) {
            Account sub = account.getSubAccounts().get(delegateName);
            sub.setUsername(account.getUsername() + "(" + delegateName + ")");
            if (sub.getVotedDelegates().size() > 0) {
                sub.getVotedDelegates().get(0).setChecked(Boolean.TRUE);

            }
            TreeItem<AccountVotedDelegateTreeItem> root2 = new TreeItem<AccountVotedDelegateTreeItem>(new AccountVotedDelegateTreeItem(new Label(sub.getUsername() + " - Sub Wallet")));
            TreeItem<AccountVotedDelegateTreeItem> node2 = new TreeItem<AccountVotedDelegateTreeItem>(new AccountVotedDelegateTreeItem(sub));
            root2.getChildren().add(node2);
            root.getChildren().add(root2);

        }

        /*Account child1 = new Account("mastadon (natalie)", "AQ9JFb5CdUsQt5KiAUTsyx5ZNFiiXpmBFe", "", 100.0);
        Delegate d = new Delegate("natalie", "AG78yF1fZRcj43yTXmePHvHQrsWWDDokNk", "", 977900, 0, 0, 40, 0.76, 99.98);
        d.setChecked(Boolean.TRUE);
        List<Delegate> ds = new ArrayList<Delegate>();
        ds.add(d);
        child1.setVotedDelegates(ds);
        TreeItem<AccountVotedDelegateTreeItem> root2 = new TreeItem<AccountVotedDelegateTreeItem>(new AccountVotedDelegateTreeItem(new Label(child1.getUsername() + " - Sub Wallet")));
        TreeItem<AccountVotedDelegateTreeItem> node2 = new TreeItem<AccountVotedDelegateTreeItem>(new AccountVotedDelegateTreeItem(child1));
        root2.getChildren().add(node2);
        root.getChildren().add(root2);*/

        /* set tree root */
        accountsTree.setRoot(root);
    }

}
