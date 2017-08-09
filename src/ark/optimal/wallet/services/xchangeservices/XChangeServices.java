/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.services.xchangeservices;

import ark.optimal.wallet.pojo.Delegate;
import ark.optimal.wallet.services.accountservices.AccountService;
import ark.optimal.wallet.services.networkservices.NetworkService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Mastadon
 */
public class XChangeServices {
    
    public static double getPrice(String currency){
        String response = getCryptonator(currency);

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> respMap = new HashMap<String, Object>();

        try {
            // convert JSON string to Map
            respMap = mapper.readValue(response, new TypeReference<Map<String, Object>>() {
            });
            //Gson g = new Gson();
            Map cyptonatorMap = (Map) respMap.get("ticker");
            //Double pr = (delegateMap.get("productivity") instanceof Integer) ? (Double) ((Integer) delegateMap.get("productivity") * 1.0) : (Double) delegateMap.get("productivity"); 
            Double price = Double.parseDouble((String) cyptonatorMap.get("price"));
            
            return price;
        } catch (IOException ex) {
            Logger.getLogger(AccountService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 0.5;

    }
    
    private static String getCryptonator(String currency){
        String urlString = "https://api.cryptonator.com/api/ticker/ark-" + currency;

        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            System.out.println("Sending get request : " + url);
            System.out.println("Response code : " + responseCode);

            // Reading response from input Stream
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String output;
            StringBuffer response = new StringBuffer();

            while ((output = in.readLine()) != null) {
                response.append(output);
            }
            in.close();

            //printing result from response
            System.out.println(response.toString());
            
            return response.toString();

        } catch (MalformedURLException ex) {
            Logger.getLogger(NetworkService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NetworkService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
