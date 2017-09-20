/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Mastadon
 */
public class Account {

    private String username;
    private String address;
    private String publicKey;
    private Double balance;

    private List<Transaction> transactions;
    private List<Delegate> votedDelegates;

    private Map<String, Account> subAccounts;
    private String masterAccountAddress;

    public String getMasterAccountAddress() {
        return masterAccountAddress;
    }

    public void setMasterAccountAddress(String masterAccountAddress) {
        this.masterAccountAddress = masterAccountAddress;
    }

    public List<Delegate> getVotedDelegates() {
        return votedDelegates;
    }

    public void setSubAccounts(Map<String, Account> subAccounts) {
        this.subAccounts = subAccounts;
    }

    public Map<String, Account> getSubAccounts() {
        return subAccounts;
    }

    public void setVotedDelegates(List<Delegate> votedDelegates) {
        this.votedDelegates = votedDelegates;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Account(String username, String address, String publicKey, Double balance) {
        this.username = username;
        this.address = address;
        this.publicKey = publicKey;
        this.balance = balance;
        subAccounts = new ConcurrentHashMap<String, Account>();
        votedDelegates = new ArrayList<Delegate>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

}
