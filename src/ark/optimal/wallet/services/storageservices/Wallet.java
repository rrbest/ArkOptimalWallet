/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.services.storageservices;

import ark.optimal.wallet.pojo.Account;
import ark.optimal.wallet.pojo.Delegate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Mastadon
 */
public class Wallet {

    private Map <String, Account> userAccounts;
    private Map <String, Account> watchAccounts;
    private Map <String, Account> subAccounts;
    private Map <String, Delegate> delegates;
    public Wallet() {
        userAccounts = new ConcurrentHashMap<String, Account>();
        watchAccounts = new ConcurrentHashMap<String, Account>();
        subAccounts = new ConcurrentHashMap<String, Account>(); 
        delegates = new ConcurrentHashMap<String, Delegate>();
    }

   public Map<String, Account> getSubAccounts() {
        return subAccounts;
    }

    public void setSubAccounts(Map<String, Account> subAccounts) {
        this.subAccounts = subAccounts;
    }
    
   

    public Map<String, Account> getUserAccounts() {
        return userAccounts;
    }

    public void setUserAccounts(Map<String, Account> userAccounts) {
        this.userAccounts = userAccounts;
    }

    public Map<String, Account> getWatchAccounts() {
        return watchAccounts;
    }

    public void setWatchAccounts(Map<String, Account> watchAccounts) {
        this.watchAccounts = watchAccounts;
    }

    public Map<String, Delegate> getDelegates() {
        return delegates;
    }

    public void setDelegates(Map<String, Delegate> delegates) {
        this.delegates = delegates;
    }

    
}
