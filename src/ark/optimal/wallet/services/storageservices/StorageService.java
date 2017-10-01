/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.services.storageservices;

import ark.optimal.wallet.pojo.Account;
import ark.optimal.wallet.pojo.Delegate;
import ark.optimal.wallet.services.accountservices.AccountService;
import ark.optimal.wallet.ui.main.Settings;
import com.google.gson.Gson;
import io.ark.core.Crypto;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bitcoinj.core.ECKey;

/**
 *
 * @author Mastadon
 */
public class StorageService {

    private static StorageService instance = null;
    private Wallet wallet;
    private String walletFilePath;
    private Map<String, Account> removableAccounts;
    private final String SETTINGSFILEPATH = System.getProperty("user.dir") + "/settings.json";
    

    public String getWalletFilePath() {
        return walletFilePath;
    }

    public void setWalletFilePath(String walletFilePath) {
        this.walletFilePath = walletFilePath;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    private StorageService() {
        this.wallet = new Wallet();
        removableAccounts = new HashMap<String, Account>();
        this.walletFilePath = System.getProperty("user.dir") + "/wallet.json";
        File f = new File(SETTINGSFILEPATH);
        if (f.exists() && !f.isDirectory()) {
            Gson gson = new Gson();
            try {
                Settings settings = gson.fromJson(new FileReader(SETTINGSFILEPATH), Settings.class);
                if (settings != null) {
                    this.walletFilePath = settings.getWalletPath();
                }
            } catch (Exception ex) {
                Logger.getLogger(AccountService.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            this.updateSettings();
        }
    }

    public static StorageService getInstance() {
        if (instance == null) {
            instance = new StorageService();
            ScheduledExecutorService executor
                    = Executors.newScheduledThreadPool(1, new ThreadFactory() {
                        public Thread newThread(Runnable r) {
                            Thread t = Executors.defaultThreadFactory().newThread(r);
                            t.setDaemon(true);
                            return t;
                        }
                    });

            Runnable periodicTask = new Runnable() {
                public void run() {

                    StorageService.getInstance().saveWallet();
                }
            };

            executor.scheduleAtFixedRate(periodicTask, 10, 30, TimeUnit.SECONDS);

            ScheduledExecutorService executor2
                    = Executors.newScheduledThreadPool(2, new ThreadFactory() {
                        public Thread newThread(Runnable r) {
                            Thread t = Executors.defaultThreadFactory().newThread(r);
                            t.setDaemon(true);
                            return t;
                        }
                    });

            Runnable periodicTask2 = new Runnable() {
                public void run() {
                    try {
                        StorageService.getInstance().updateWallet();
                    } catch (Exception ex) {
                        Logger.getLogger(StorageService.class.getName()).log(Level.WARNING, null, ex);
                        Thread.currentThread().interrupt();
                    }
                }
            };

            executor2.scheduleAtFixedRate(periodicTask2, 1, 8, TimeUnit.SECONDS);
        }
        return instance;
    }

    public Account checkIfAccountExistByPassphrase(String passphrase) {

        Account account = null;
        try {
            ECKey addressKey = Crypto.getKeys(passphrase);
            String address = Crypto.getAddress(addressKey);
            account = this.wallet.getUserAccounts().get(address);
            if (account == null) {
                account = this.wallet.getWatchAccounts().get(address);
            }

        } catch (Exception ex) {
            Logger.getLogger(AccountService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Throwable t) {
            Logger.getLogger(AccountService.class.getName()).log(Level.SEVERE, null, t);
        }
        return account;
    }

    public Account checkIfAccountExistByAddress(String address) {

        Account account = null;
        try {
            account = this.wallet.getUserAccounts().get(address);
            if (account == null) {
                account = this.wallet.getWatchAccounts().get(address);
            }

        } catch (Exception ex) {
            Logger.getLogger(AccountService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Throwable t) {
            Logger.getLogger(AccountService.class.getName()).log(Level.SEVERE, null, t);
        }
        return account;
    }

    public synchronized void addAccountToUserAccounts(Account account, boolean masterThread) {
        Account dirtyAccount = this.wallet.getUserAccounts().get(account.getAddress());
        if (!masterThread && dirtyAccount != null) {
            if (dirtyAccount.getSubAccounts().size() != account.getSubAccounts().size()) { // don't overwrite master thread changes
                return;
            }
        }
//        Map<String, Account> merged = new ConcurrentHashMap<String, Account>();
//        if (dirtyAccount != null && !masterThread) {
//            
//            Map<String, Account> subs = dirtyAccount.getSubAccounts();
//            merged.putAll(subs);
//        }
//        merged.putAll(account.getSubAccounts());
//        account.setSubAccounts(merged);
        this.wallet.getUserAccounts().put(account.getAddress(), account);
    }

    public synchronized void addAccountToSubAccounts(Account account) {
        this.wallet.getSubAccounts().put(account.getAddress(), account);
    }

    public void addAccountToWatchAccounts(Account account) {
        this.wallet.getWatchAccounts().put(account.getAddress(), account);
    }

    public synchronized void addDelegate(Delegate d, boolean masterThread) {
        if (!masterThread) {
            Delegate dirtyDelegate = this.wallet.getDelegates().get(d.getUsername());
            d.setExcludedVotes(dirtyDelegate.getExcludedVotes());
            d.setPayoutPercentage(dirtyDelegate.getPayoutPercentage());

        }

        this.wallet.getDelegates().put(d.getUsername(), d);
    }
    public synchronized void removeAccount(Account account){
        removableAccounts.put(account.getAddress(), account);
    }

    public void updateSettings() {
        Settings settings = new Settings(this.walletFilePath);
        Gson gson = new Gson();
        try {
            String jsonobject = gson.toJson(settings);
            System.out.println(jsonobject);
            FileWriter writer = new FileWriter(SETTINGSFILEPATH);
            writer.write(jsonobject);
            writer.close();

        } catch (IOException ex) {
            Logger.getLogger(StorageService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void saveWallet() {
        saveWallet(walletFilePath);
    }

    public void saveWallet(String filepath) {
        Gson gson = new Gson();
        try {
            String jsonobject = gson.toJson(this.wallet);
            System.out.println(jsonobject);
            FileWriter writer = new FileWriter(filepath);
            writer.write(jsonobject);
            writer.close();

        } catch (IOException ex) {
            Logger.getLogger(StorageService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Wallet loadWallet(){
        return this.loadWallet(walletFilePath);
    }
 
    public Wallet loadWallet(String filepath) {
        Gson gson = new Gson();
        try {
            Wallet wallet = gson.fromJson(new FileReader(filepath), Wallet.class);
            if (wallet != null) {
                this.wallet = wallet;
            }
        } catch (FileNotFoundException ex) {
            return this.wallet;
        } catch (Exception ex) {
            return this.wallet;
        }
        return this.wallet;

    }

    public void updateWallet() {
     try{
         
        
        Set<String> addresses = this.wallet.getUserAccounts().keySet(); // to avoid concurrentModification
        for (String address : addresses) {
            Account removableAccount = removableAccounts.get(address);
            if(removableAccount != null){
                this.wallet.getUserAccounts().remove(removableAccount.getAddress());
                this.removableAccounts.remove(removableAccount.getAddress());
                continue;
            }
            Account account = this.wallet.getUserAccounts().get(address);
            if(account == null){
                continue;
            }
            String accountName = account.getUsername();
            Map<String, Account> subAccounts = account.getSubAccounts();
            account = AccountService.getFullAccount(address);
            if (account == null) {
                break;
            }
            account.setUsername(accountName);
            for (Map.Entry<String, Account> entry : subAccounts.entrySet()) {
                String subAccountName = entry.getValue().getUsername();
                String masterAccountAddress = entry.getValue().getMasterAccountAddress();
                Account subAccount = AccountService.getFullAccount(entry.getValue().getAddress());
                subAccount.setUsername(subAccountName);
                subAccount.setMasterAccountAddress(masterAccountAddress);
                account.getSubAccounts().put(entry.getKey(), subAccount);
                this.addAccountToSubAccounts(subAccount);

            }
            this.addAccountToUserAccounts(account, false);

        }
        for (String address : this.wallet.getWatchAccounts().keySet()) {
            Account account = this.wallet.getWatchAccounts().get(address);
            String accountName = account.getUsername();
            account = AccountService.getFullAccount(address);
            if (account == null) {
                break;
            }
            account.setUsername(accountName);
            this.wallet.getWatchAccounts().put(address, account);

        }
        Set<String> names = this.wallet.getDelegates().keySet();
        for (String delegateName : names) {
            Delegate delegate = this.wallet.getDelegates().get(delegateName);
            Delegate newDelegate = AccountService.getDelegateByUsername(delegateName);
            if (newDelegate == null) {
                break;
            }
            //newDelegate.setExlcudedPercentage(delegate.getExlcudedPercentage());
            //newDelegate.setPayoutFrequency(delegate.getPayoutFrequency());
            //newDelegate.setPayoutPercentage(delegate.getPayoutPercentage());
            //newDelegate.setPoolPercentage(delegate.getPoolPercentage());
            //newDelegate.setMinPayout(delegate.getMinPayout());
            this.addDelegate(newDelegate, false);
        }
     }catch(ConcurrentModificationException ex){
         Logger.getLogger(StorageService.class.getName()).log(Level.FINE, null, ex);
     }

    }

    public Map<String,Account> getRemoveableAccounts() {
        return removableAccounts;
    }

}
