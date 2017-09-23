/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.services.xchangeservices;

import ark.optimal.wallet.pojo.Delegate;
import ark.optimal.wallet.services.accountservices.AccountService;
import ark.optimal.wallet.services.networkservices.NetworkService;
import ark.optimal.wallet.services.storageservices.StorageService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Mastadon
 */
public class XChangeServices {
    private ArkMarketData arkMarketdata;

    private static XChangeServices instance = null;
    
    private XChangeServices() {
        
    }
    public static XChangeServices getInstance() {
        if (instance == null) {
            instance = new XChangeServices();
            ScheduledExecutorService executor
                    = Executors.newScheduledThreadPool(2, new ThreadFactory() {
                        public Thread newThread(Runnable r) {
                            Thread t = Executors.defaultThreadFactory().newThread(r);
                            t.setDaemon(true);
                            return t;
                        }
                    });

            Runnable periodicTask = new Runnable() {
                public void run() {

                   XChangeServices.getInstance().getArkMarketData("USD");
                }
            };
            executor.scheduleAtFixedRate(periodicTask, 0, 8, TimeUnit.SECONDS);
        }
        return instance;
    }
   
    private synchronized void getArkMarketData(String currency) {
        try {
            String response = getArkMarketDataFromCoinMarketCap(currency);
            if (response == null) {
                return;
            }

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> respMap = new HashMap<String, Object>();

            // convert JSON string to Map
            respMap = mapper.readValue(response, new TypeReference<Map<String, Object>>() {
            });

            Double price_usd = Double.parseDouble((String) respMap.get("price_usd"));
            Double market_cap_usd = Double.parseDouble((String) respMap.get("market_cap_usd"));
            Double percent_change_1h = Double.parseDouble((String) respMap.get("percent_change_1h"));
            Double percent_change_24h = Double.parseDouble((String) respMap.get("percent_change_24h"));
            Double percent_change_7d = Double.parseDouble((String) respMap.get("percent_change_7d"));
            
             arkMarketdata = new ArkMarketData();
             arkMarketdata.setPrice_usd(price_usd);
             arkMarketdata.setPercent_change_1h(percent_change_1h);
             arkMarketdata.setPercent_change_24h(percent_change_24h);
             arkMarketdata.setPercent_change_7d(percent_change_7d);
             arkMarketdata.setMarket_cap_usd(market_cap_usd);
             
        } catch (IOException ex) {
            Logger.getLogger(AccountService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return;
    }

    private String getArkMarketDataFromCoinMarketCap(String currency) {
        String urlString = "https://api.coinmarketcap.com/v1/ticker/Ark/?convert=" + currency;

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
            String ret = StringUtils.substringBetween(response.toString(), "[", "]");
            return ret;

        } catch (MalformedURLException ex) {
            Logger.getLogger(NetworkService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NetworkService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public Double getPrice()
    {
        if(arkMarketdata == null){
            getArkMarketData("USD");
        }
        return arkMarketdata.getPrice_usd();
    }
    public Double getPercentageChange1H()
    {
        if(arkMarketdata == null){
            getArkMarketData("USD");
        }
        return arkMarketdata.getPercent_change_1h();
    }
    public Double getPercentageChange24H()
    {
        if(arkMarketdata == null){
            getArkMarketData("USD");
        }
        return arkMarketdata.getPercent_change_24h();
    }
    public Double getPercentageChange7D()
    {
        if(arkMarketdata == null){
            getArkMarketData("USD");
        }
        return arkMarketdata.getPercent_change_7d();
    }
    public Double getMarkCapUSD()
    {
        if(arkMarketdata == null){
            getArkMarketData("USD");
        }
        return arkMarketdata.getMarket_cap_usd();
    }

    public class ArkMarketData {

        private String id;
        private String name;
        private String symbol;
        private String rank;
        private Double price_usd;
        private Double price_btc;
        private Double market_cap_usd;
        private Double percent_change_1h;
        private Double percent_change_24h;
        private Double percent_change_7d;
        private Long last_updated;

        public ArkMarketData() {
        }

        public ArkMarketData(String id, String name, String symbol, String rank, Double price_usd, Double price_btc, Double market_cap_usd, Double percent_change_1h, Double percent_change_24h, Double percent_change_7d, Long last_updated) {
            this.id = id;
            this.name = name;
            this.symbol = symbol;
            this.rank = rank;
            this.price_usd = price_usd;
            this.price_btc = price_btc;
            this.market_cap_usd = market_cap_usd;
            this.percent_change_1h = percent_change_1h;
            this.percent_change_24h = percent_change_24h;
            this.percent_change_7d = percent_change_7d;
            this.last_updated = last_updated;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getRank() {
            return rank;
        }

        public void setRank(String rank) {
            this.rank = rank;
        }

        public Double getPrice_usd() {
            return price_usd;
        }

        public void setPrice_usd(Double price_usd) {
            this.price_usd = price_usd;
        }

        public Double getPrice_btc() {
            return price_btc;
        }

        public void setPrice_btc(Double price_btc) {
            this.price_btc = price_btc;
        }

        public Double getMarket_cap_usd() {
            return market_cap_usd;
        }

        public void setMarket_cap_usd(Double market_cap_usd) {
            this.market_cap_usd = market_cap_usd;
        }

        public Double getPercent_change_1h() {
            return percent_change_1h;
        }

        public void setPercent_change_1h(Double percent_change_1h) {
            this.percent_change_1h = percent_change_1h;
        }

        public Double getPercent_change_24h() {
            return percent_change_24h;
        }

        public void setPercent_change_24h(Double percent_change_24h) {
            this.percent_change_24h = percent_change_24h;
        }

        public Double getPercent_change_7d() {
            return percent_change_7d;
        }

        public void setPercent_change_7d(Double percent_change_7d) {
            this.percent_change_7d = percent_change_7d;
        }

        public Long getLast_updated() {
            return last_updated;
        }

        public void setLast_updated(Long last_updated) {
            this.last_updated = last_updated;
        }

    }
}
