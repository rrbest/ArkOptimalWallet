/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.ark.core;

import ark.optimal.wallet.pojo.Delegate;
import ark.optimal.wallet.services.accountservices.AccountService;
import ark.optimal.wallet.services.networkservices.NetworkService;
import com.google.common.io.BaseEncoding;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Mastadon
 */
public class TransactionService {

    public static Transaction createTransaction(String senderId, String recipientId, long amount, String vendorField, String passphrase){
    
        byte type = 0;
        long fee = 10000000;
        amount = amount * 100000000;
        Transaction tx = new Transaction(recipientId, amount, fee, type, vendorField);
        tx.setSenderId(senderId);
        int timestamp = Slot.getTime(null);
        tx.setTimestamp(timestamp);
        tx.sign(passphrase);
        tx.setId(Crypto.getId(tx));
        return tx;
    }
    public static Transaction createVote(String senderId, String delegateName, String passphrase, boolean unvote){
        byte type = 3;
        long fee = 100000000;
        long amount = 0;
        Delegate d = AccountService.getDelegateByUsername(delegateName);
        String recipientId = senderId;
        Transaction tx = new Transaction(recipientId, amount, fee, type, null);
        tx.setSenderId(senderId);
        int timestamp = Slot.getTime(null);
        tx.setTimestamp(timestamp);
        List<String> votes = Arrays.asList((unvote ? "-" : "+") + d.getPublicKey());
        Asset asset = new Asset();
        asset.setVotes(votes);
        tx.setAsset(asset);
        tx.sign(passphrase);
        tx.setId(Crypto.getId(tx));
        return tx;
    }
    public static void sign(Transaction t,String passphrase){
        t.sign(passphrase);
    }
    
    public static String PostTransaction(Transaction transaction) {
        List<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(transaction);
        Transactions txs = new Transactions(transactions);
        String json = new Gson().toJson(txs);
        String response = "";
        for (int i = 0; i<10; i++){
            response = NetworkService.postToPeer3("/peer/transactions", json);
        }
        
        return response;
    }
    private static class Transactions {
        private List<Transaction> transactions;

        public Transactions(List<Transaction> transactions) {
            this.transactions = transactions;
        }
        
    }
}
