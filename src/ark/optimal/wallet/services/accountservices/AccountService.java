/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.services.accountservices;

import ark.optimal.wallet.pojo.Account;
import ark.optimal.wallet.pojo.Delegate;
import ark.optimal.wallet.pojo.Transaction;
import ark.optimal.wallet.services.networkservices.NetworkService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.ark.core.Crypto;
import java.io.IOException;
import static java.lang.Math.round;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bitcoinj.core.ECKey;

/**
 *
 * @author Mastadon
 */
public class AccountService {

    private static Delegate getDelegate(String api) {
        String response = NetworkService.getFromPeer(api, 1);

        if (response == null) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> respMap = new HashMap<String, Object>();

        try {
            // convert JSON string to Map
            respMap = mapper.readValue(response, new TypeReference<Map<String, Object>>() {
            });
            //Gson g = new Gson();
            Map delegateMap = (Map) respMap.get("delegate");
            //Double pr = (delegateMap.get("productivity") instanceof Integer) ? (Double) ((Integer) delegateMap.get("productivity") * 1.0) : (Double) delegateMap.get("productivity"); 
            if (delegateMap == null) {
                return null;
            }
            Double vote = Double.parseDouble((String) delegateMap.get("vote")) / 100000000;
            int v = vote.intValue();
            Delegate d = new Delegate((String) delegateMap.get("username"),
                    (String) delegateMap.get("address"),
                    (String) delegateMap.get("publicKey"),
                    v,
                    (Integer) delegateMap.get("producedblocks"),
                    (Integer) delegateMap.get("missedblocks"),
                    (Integer) delegateMap.get("rate"),
                    (delegateMap.get("approval") instanceof Integer) ? (Double) ((Integer) delegateMap.get("approval") * 1.0) : (Double) delegateMap.get("approval"),
                    (delegateMap.get("productivity") instanceof Integer) ? (Double) ((Integer) delegateMap.get("productivity") * 1.0) : (Double) delegateMap.get("productivity"));
            //Delegate d = g.fromJson((String)respMap.get("delegate"), Delegate.class);

            return d;
        } catch (IOException ex) {
            Logger.getLogger(AccountService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;

    }

    public static Delegate getDelegateByPublicKey(String publicKey) {
        String api = "/api/delegates/get/?publicKey=" + publicKey;
        return getDelegate(api);

    }

    public static Delegate getDelegateByUsername(String username) {
        String api = "/api/delegates/get/?username=" + username;
        return getDelegate(api);
    }

    public static Account getAccount(String address) {
        String response = NetworkService.getFromPeer("/api/accounts?address=" + address, 1);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> respMap = new HashMap<String, Object>();

        try {
            // convert JSON string to Map
            respMap = mapper.readValue(response, new TypeReference<Map<String, Object>>() {
            });
            //Gson g = new Gson();
            Map accountMap = (Map) respMap.get("account");
            Double balance = round((Double.parseDouble((String) accountMap.get("balance")) / 100000000) * 100.0) / 100.0;

            Account a = new Account((String) accountMap.get("username"),
                    (String) accountMap.get("address"),
                    (String) accountMap.get("publicKey"),
                    balance);

            Delegate d = getDelegateByPublicKey(a.getPublicKey());
            if (d != null) {
                a.setUsername(d.getUsername());
            } else {
                a.setUsername(a.getAddress());
            }

            return a;
        } catch (IOException ex) {
            Logger.getLogger(AccountService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public static Account getFullAccount(String address) {
        String response = NetworkService.getFromPeer("/api/accounts?address=" + address, 1);

        if (response == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> respMap = new HashMap<String, Object>();

        try {
            // convert JSON string to Map
            respMap = mapper.readValue(response, new TypeReference<Map<String, Object>>() {
            });
            //Gson g = new Gson();
            if (!(Boolean) respMap.get("success")) {
                return new Account(address, address, null, 0.0);
            }
            Map accountMap = (Map) respMap.get("account");
            Double balance = round((Double.parseDouble((String) accountMap.get("balance")) / 100000000) * 100.0) / 100.0;

            Account a = new Account((String) accountMap.get("username"),
                    (String) accountMap.get("address"),
                    (String) accountMap.get("publicKey"),
                    balance);

            Delegate d = getDelegateByPublicKey(a.getPublicKey());
            if (d != null) {
                a.setUsername(d.getUsername());
            } else {
                a.setUsername(a.getAddress());
            }

            List<Transaction> transactions = getTransactions(a.getAddress(), 50);
            a.setTransactions(transactions);
            a.setVotedDelegates(getVotedDelegates(a.getAddress()));
            return a;
        } catch (IOException ex) {
            Logger.getLogger(AccountService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public static Account createAccount(String passphrase) {
        Account account = null;
        try {
            String address = getAddress(passphrase);
            account = getFullAccount(address);

        } catch (Exception ex) {
            Logger.getLogger(AccountService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Throwable t) {
            Logger.getLogger(AccountService.class.getName()).log(Level.SEVERE, null, t);
        }

        return account;

    }

    public static String getAddress(String passphrase) {
        ECKey addressKey = Crypto.getKeys(passphrase);
        String address = Crypto.getAddress(addressKey);
        return address;

    }

    public static List<Delegate> getVotedDelegates(String address) {
        String response = NetworkService.getFromPeer("/api/accounts/delegates/?address=" + address, 1);
        //System.out.println(response);
        if (response == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> respMap = new HashMap<String, Object>();
        List<Delegate> delegates = new ArrayList<Delegate>();
        try {
            // convert JSON string to Map
            respMap = mapper.readValue(response, new TypeReference<Map<String, Object>>() {
            });
            List<Map> dels = (List<Map>) respMap.get("delegates");
            for (Map delegateMap : dels) {
                Double vote = Double.parseDouble((String) delegateMap.get("vote")) / 100000000;
                int v = vote.intValue();
                Delegate d = new Delegate((String) delegateMap.get("username"),
                        (String) delegateMap.get("address"),
                        (String) delegateMap.get("publicKey"),
                        v,
                        (Integer) delegateMap.get("producedblocks"),
                        (Integer) delegateMap.get("missedblocks"),
                        (Integer) delegateMap.get("rate"),
                        (delegateMap.get("approval") instanceof Integer) ? (Double) ((Integer) delegateMap.get("approval") * 1.0) : (Double) delegateMap.get("approval"),
                        (delegateMap.get("productivity") instanceof Integer) ? (Double) ((Integer) delegateMap.get("productivity") * 1.0) : (Double) delegateMap.get("productivity"));

                delegates.add(d);
            }

        } catch (IOException ex) {
            Logger.getLogger(AccountService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return delegates;

    }

    public static List<Transaction> getTransactions(String address, int limit) {
        String response = NetworkService.getFromPeer("/api/transactions?orderBy=timestamp:desc&limit=" + limit + "&recipientId=" + address + "&senderId=" + address, 1);

        if (response == null) {
            return null;
        }
        //System.out.println(response);
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> respMap = new HashMap<String, Object>();
        List<Transaction> transactions = new ArrayList<Transaction>();
        try {
            // convert JSON string to Map
            respMap = mapper.readValue(response, new TypeReference<Map<String, Object>>() {
            });
            List<Map> trans = (List<Map>) respMap.get("transactions");
            for (Map t : trans) {
                String id = (String) t.get("id");
                Integer type = (Integer) t.get("type");
                Integer timestamp = (Integer) t.get("timestamp");
                Long amount = (t.get("amount") instanceof Integer) ? (Long) (((Integer) t.get("amount")).longValue()) : (Long) t.get("amount");
                Long fee = (t.get("fee") instanceof Integer) ? (Long) (((Integer) t.get("fee")).longValue()) : (Long) t.get("fee");
                String senderId = (String) t.get("senderId");
                String recipientId = (String) t.get("recipientId");
                String senderPublicKey = (String) t.get("senderPublicKey");
                Integer confirmations = (Integer) t.get("confirmations");
                String vendorField = (String) t.get("vendorField");
                String signature = (String) t.get("signature");
                String signSignature = (String) t.get("signSignature");
                String requesterPublicKey = (String) t.get("requesterPublicKey");
                String blockid = (String) t.get("blockid");

                String from = senderId;
                String to = recipientId;
                /*if (senderId != null) {
                    from = AccountService.getAccount(senderId).getUsername();
                }
                if (recipientId != null) {
                    to = AccountService.getAccount(recipientId).getUsername();
                }*/

                Transaction tr = new Transaction(id, timestamp, recipientId, senderId, amount, fee, type, vendorField, signature, signSignature, senderPublicKey, requesterPublicKey, confirmations, blockid, from, to);
                transactions.add(tr);

            }

        } catch (IOException ex) {
            Logger.getLogger(AccountService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return transactions;

    }

}
