/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.services.storageservices;

import ark.optimal.wallet.pojo.Account;
import ark.optimal.wallet.services.accountservices.AccountService;
import static ark.optimal.wallet.services.accountservices.AccountService.getAccount;
import io.ark.core.Crypto;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bitcoinj.core.ECKey;

/**
 *
 * @author Mastadon
 */
public class StorageService {
    private static StorageService instance = null;
    private Map <String, Account> userAccounts;
    private Map <String, Account> watchAccounts;

    public Map<String, Account> getUserAccounts() {
        return userAccounts;
    }

    private StorageService() {
        userAccounts = new HashMap<String, Account>();
        watchAccounts = new HashMap<String, Account>();
        
    }

    public static StorageService getInstance() {
      if(instance == null) {
         instance = new StorageService();
      }
      return instance;
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
    
    public Account checkIfAccountExistByPassphrase(String passphrase){
        
        Account account = null;
        try {
            ECKey addressKey = Crypto.getKeys(passphrase);
            String address = Crypto.getAddress(addressKey);
            account = userAccounts.get(address);
            if (account == null)
                account = watchAccounts.get(address);
            
        } catch (Exception ex) {
            Logger.getLogger(AccountService.class.getName()).log(Level.SEVERE, null, ex);
        } catch(Throwable t){
            Logger.getLogger(AccountService.class.getName()).log(Level.SEVERE, null, t);
        }
        return account;
    }
    public Account checkIfAccountExistByAddress(String address){
        
        Account account = null;
        try {
            account = userAccounts.get(address);
            if (account == null)
                account = watchAccounts.get(address);
            
        } catch (Exception ex) {
            Logger.getLogger(AccountService.class.getName()).log(Level.SEVERE, null, ex);
        } catch(Throwable t){
            Logger.getLogger(AccountService.class.getName()).log(Level.SEVERE, null, t);
        }
        return account;
    }
    public void addAccountToUserAccounts(Account account){
        userAccounts.put(account.getAddress(), account);
    }
    public void addAccountToWatchAccounts(Account account){
        watchAccounts.put(account.getAddress(), account);
    }
    
}
