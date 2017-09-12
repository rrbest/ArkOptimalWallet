/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.ui;

import ark.optimal.wallet.pojo.Account;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

/**
 *
 * @author Mastadon
 */
public class AccountVotedDelegateTreeItem extends AnchorPane {

    private Account account;
    private FXMLVotedDelegateViewController votedDelegateViewController;
    
    public AccountVotedDelegateTreeItem(Label accountName) {
        super();
        accountName.setFont(new Font(17.0));
        this.getChildren().add(accountName);

    }
    
    public AccountVotedDelegateTreeItem(Account account) {
        super();
        this.account = account;
        
        try {

            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FXMLVotedDelegateView.fxml"));
            AnchorPane votespane = (AnchorPane) fxmlLoader.load();
            if (account.getVotedDelegates().size() > 0){
            
                votedDelegateViewController = (FXMLVotedDelegateViewController) fxmlLoader.getController();
                votedDelegateViewController.setVotedDelegate(new DelegateItem(account.getVotedDelegates().get(0)));
            }
            this.getChildren().addAll(votespane.getChildren());
            
        } catch (IOException ex) {
            Logger.getLogger(AccountVotedDelegateTreeItem.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
