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
import com.eclipsesource.v8.NodeJS;
import com.eclipsesource.v8.Releasable;
import com.eclipsesource.v8.V8Object;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.ark.core.Crypto;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import static java.lang.Math.round;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bitcoinj.core.ECKey;

/**
 *
 * @author Mastadon
 */
public class AccountService {

    private static Delegate getDelegate(String api) {
        String response = NetworkService.getFromPeer(api);

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
        String response = NetworkService.getFromPeer("/api/accounts?address=" + address);

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
        String response = NetworkService.getFromPeer("/api/accounts?address=" + address);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> respMap = new HashMap<String, Object>();

        try {
            // convert JSON string to Map
            respMap = mapper.readValue(response, new TypeReference<Map<String, Object>>() {
            });
            //Gson g = new Gson();
            if (!(Boolean)respMap.get("success"))
                return new Account(address, address, null, 0.0);
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
            ECKey addressKey = Crypto.getKeys(passphrase);
            String address = Crypto.getAddress(addressKey);
            account = getFullAccount(address);

        } catch (Exception ex) {
            Logger.getLogger(AccountService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Throwable t) {
            Logger.getLogger(AccountService.class.getName()).log(Level.SEVERE, null, t);
        }

        return account;

    }
    
    public static List<Delegate> getVotedDelegates(String address){
        String response = NetworkService.getFromPeer("/api/accounts/delegates/?address="+address);
        System.out.println(response);
        
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
        String response = NetworkService.getFromPeer("/api/transactions?orderBy=timestamp:desc&limit=" + limit + "&recipientId=" + address + "&senderId=" + address);
        System.out.println(response);
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

    public String getDateCurrentTimeZone(long timestamp) {
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        } catch (Exception e) {
        }
        return "";
    }

    private static String readFileAsString(String filePath) throws java.io.IOException {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(
                new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }

    private static void executeJSFunction(V8Object object, String name, Object... params) {
        Object result = object.executeJSFunction(name, params);
        if (result instanceof Releasable) {
            ((Releasable) result).release();
        }
    }

    private static void runMessageLoop(NodeJS nodeJS) {
        while (nodeJS.isRunning()) {
            nodeJS.handleMessage();
        }
    }
    

}
