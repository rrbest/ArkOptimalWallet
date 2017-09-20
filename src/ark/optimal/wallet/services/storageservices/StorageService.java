/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.services.storageservices;

import ark.optimal.wallet.pojo.Account;
import ark.optimal.wallet.pojo.Delegate;
import ark.optimal.wallet.services.accountservices.AccountService;
import com.google.gson.Gson;
import io.ark.core.Crypto;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import static java.util.Collections.list;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
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

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    private StorageService() {
        this.wallet = new Wallet();

    }

    public static StorageService getInstance() {
        if (instance == null) {
            instance = new StorageService();
            ScheduledExecutorService executor
                    = Executors.newScheduledThreadPool(5, new ThreadFactory() {
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
                    = Executors.newScheduledThreadPool(3, new ThreadFactory() {
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

            executor2.scheduleAtFixedRate(periodicTask2, 1, 16, TimeUnit.SECONDS);
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

    public void addAccountToUserAccounts(Account account) {
        this.wallet.getUserAccounts().put(account.getAddress(), account);
    }

    public void addAccountToSubAccounts(Account account) {
        this.wallet.getSubAccounts().put(account.getAddress(), account);
    }

    public void addAccountToWatchAccounts(Account account) {
        this.wallet.getWatchAccounts().put(account.getAddress(), account);
    }

    public void addDelegate(Delegate d) {
        this.wallet.getDelegates().put(d.getUsername(), d);
    }

    public void saveWallet() {
        Gson gson = new Gson();
        try {
            String jsonobject = gson.toJson(this.wallet);
            System.out.println(jsonobject);
            FileWriter writer = new FileWriter("wallet.json");
            writer.write(jsonobject);
            writer.close();

        } catch (IOException ex) {
            Logger.getLogger(StorageService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public Wallet loadWallet() {
        Gson gson = new Gson();
        try {
            Wallet wallet = gson.fromJson(new FileReader("wallet.json"), Wallet.class);
            if (wallet != null) {
                this.wallet = wallet;
            }
        } catch (FileNotFoundException ex) {
            return this.wallet;

        }
        return this.wallet;

    }

    public void updateWallet() {
        for (String address : this.wallet.getUserAccounts().keySet()) {
            Account account = this.wallet.getUserAccounts().get(address);
            String accountName = account.getUsername();
            Map<String, Account> subAccounts = account.getSubAccounts();
            account = AccountService.getFullAccount(address);
            if(account == null){
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
                this.wallet.getSubAccounts().put(subAccount.getAddress(), subAccount);

            }
            this.wallet.getUserAccounts().put(address, account);

        }
        for (String address : this.wallet.getWatchAccounts().keySet()) {
            Account account = this.wallet.getWatchAccounts().get(address);
            String accountName = account.getUsername();
            account = AccountService.getFullAccount(address);
            if(account == null){
                break;
            }
            account.setUsername(accountName);
            this.wallet.getWatchAccounts().put(address, account);

        }
        Iterator<String> iter = this.wallet.getDelegates().keySet().iterator();
        while(iter.hasNext()){// (String delegateName : this.wallet.getDelegates().keySet()) {
            String delegateName = iter.next();
            Delegate delegate = this.wallet.getDelegates().get(delegateName);
            Delegate newDelegate = AccountService.getDelegateByUsername(delegateName);
            if(newDelegate == null){
                break;
            }
            newDelegate.setExlcudedPercentage(delegate.getExlcudedPercentage());
            newDelegate.setPayoutFrequency(delegate.getPayoutFrequency());
            newDelegate.setPayoutPercentage(delegate.getPayoutPercentage());
            newDelegate.setPoolPercentage(delegate.getPoolPercentage());
            newDelegate.setMinPayout(delegate.getMinPayout());
            this.wallet.getDelegates().put(delegateName, newDelegate);
        }

    }

}
