/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ark.optimal.wallet.services.networkservices;

import io.ark.core.Slot;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

/**
 *
 * @author Mastadon
 */
public class NetworkService {

    private static Network Mainnet;
    private static Network Devnet;

    static {
        if (Mainnet == null) {
            Mainnet = new Network(
                    "6e84d08bd299ed97c212c886c98a57e36545c8f5d645ca7eeae63a8bd62d8988",
                    "mainnet",
                    4001,
                    0x17,
                    new ArrayList<String>(Arrays.asList("5.39.9.240:4001",
                            "5.39.9.241:4001",
                            "5.39.9.242:4001",
                            "5.39.9.243:4001",
                            "5.39.9.244:4001",
                            "5.39.9.250:4001",
                            "5.39.9.251:4001",
                            "5.39.9.252:4001",
                            "5.39.9.253:4001",
                            "5.39.9.254:4001",
                            "5.39.9.255:4001",
                            "5.39.53.48:4001",
                            "5.39.53.49:4001",
                            "5.39.53.50:4001",
                            "5.39.53.51:4001"//,
                            //"5.39.53.52:4001",
                            //"5.39.53.53:4001",
                            //"5.39.53.54:4001",
                            //"5.39.53.55:4001",
                            //"37.59.129.160:4001",
                            //"37.59.129.161:4001",
                            //"37.59.129.162:4001",
                            //"37.59.129.163:4001",
                            //"37.59.129.164:4001",
                            //"37.59.129.165:4001",
                            //"37.59.129.166:4001",
                            //"37.59.129.167:4001",
                            //"37.59.129.168:4001",
                            //"37.59.129.169:4001",
                            //"37.59.129.170:4001",
                            //"37.59.129.171:4001",
                            //"37.59.129.172:4001",
                            //"37.59.129.173:4001",
                            //"37.59.129.174:4001",
                           // "37.59.129.175:4001",
                           //"193.70.72.80:4001",
                           // "193.70.72.81:4001",
                           // "193.70.72.82:4001",
                           // "193.70.72.83:4001",
                           // "193.70.72.84:4001",
                           // "193.70.72.85:4001",
                           //  "193.70.72.86:4001",
                           // "193.70.72.87:4001",
                           // "193.70.72.88:4001",
                           // "193.70.72.89:4001",
                           // "193.70.72.90:4001"
                            )));

            Devnet = new Network(
                    "578e820911f24e039733b45e4882b73e301f813a0d2c31330dafda84534ffa23",
                    "devnet",
                    4002,
                    0x1e,
                    new ArrayList<String>(Arrays.asList("167.114.29.51:4002",
                            "167.114.29.52:4002",
                            "167.114.29.53:4002",
                            "167.114.29.54:4002",
                            "167.114.29.55:4002")));

        }

    }

    public static String getFromPeer(String api, int trial) {
        String peer_ip = getRandomSeed(Mainnet);
        System.out.println(peer_ip);
        String urlString = "http://" + peer_ip + api;
        HttpURLConnection con = null;
        Boolean success = false;
        try {
            URL url = new URL(urlString);
            con = (HttpURLConnection) url.openConnection();
            con.setConnectTimeout(10000);
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
          //  System.out.println("Sending get request : " + url);
         //   System.out.println("Response code : " + responseCode);

            // Reading response from input Stream
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String output;
            StringBuffer response = new StringBuffer();

            while ((output = in.readLine()) != null) {
                response.append(output);
            }
            in.close();
            success = true;

            //printing result from response
          //  System.out.println(response.toString());

            return response.toString();

        } catch (MalformedURLException ex) {
            Logger.getLogger(NetworkService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NetworkService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (con != null) {
                con.disconnect();
            }if(!success){
                getFromPeer(api, ++trial);
            }
        }
        return null;
    }

    public static String postToPeer(String api, String generatedJSONString) {
        String peer_ip = getRandomSeed(Mainnet);
        String urlString = "http://" + peer_ip + api;

        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoInput(true);
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("port", "1");
            con.setRequestProperty("nethash", Mainnet.getNethash());
            con.connect();

            DataOutputStream output = null;
            DataInputStream input = null;
            output = new DataOutputStream(con.getOutputStream());

            /*Construct the POST data.*/
            String content = generatedJSONString;
            System.out.println(content);
            /* Send the request data.*/
            output.writeBytes(content);
            output.flush();
            output.close();

            /* Get response data.*/
            String response = null;
            input = new DataInputStream(con.getInputStream());
            while (null != ((response = input.readLine()))) {
                System.out.println(response);
                input.close();
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(NetworkService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NetworkService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static String postToPeer2(String api, String generatedJSONString) {

        String peer_ip = getRandomSeed(Mainnet);
        String urlString = "http://" + peer_ip + api;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(urlString);
        post.setHeader("Content-Type", "application/json");
        post.setHeader("version", "0.1.0");
        post.setHeader("port", "1");
        post.setHeader("nethash", Mainnet.getNethash());

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("task", "savemodel"));
        params.add(new BasicNameValuePair("code", generatedJSONString));

        System.out.println(generatedJSONString);
        CloseableHttpResponse response = null;
        Scanner in = null;
        try {
            post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            response = httpClient.execute(post);
            // System.out.println(response.getStatusLine());
            HttpEntity entity = response.getEntity();
            in = new Scanner(entity.getContent());
            while (in.hasNext()) {
                System.out.println(in.next());

            }
            EntityUtils.consume(entity);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(NetworkService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NetworkService.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            in.close();
            try {
                response.close();
            } catch (IOException ex) {
                Logger.getLogger(NetworkService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        /*String peer_ip = getRandomSeed(Mainnet);
        String urlString = "http://" + peer_ip + api;

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(urlString);
        StringEntity entity = new StringEntity(generatedJSONString);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");

        CloseableHttpResponse response = client.execute(httpPost);
        assertThat(response.getStatusLine().getStatusCode(), equalTo(200));
        client.close();*/
        return null;
    }

    public static String postToPeer3(String api, String generatedJSONString) {

        String resp = null;
        String peer_ip = getRandomSeed(Mainnet);
        String urlString = "http://" + peer_ip + api;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(urlString);
        post.setHeader("Content-Type", "application/json");
        post.setHeader("Accept", "application/json");
        post.setHeader("version", "0.1.0");
        post.setHeader("port", "1");
        post.setHeader("nethash", Mainnet.getNethash());
        Scanner in = null;
        String json = "{\"id\":1,\"name\":\"John\"}";
        try {
            StringEntity entity = new StringEntity(generatedJSONString);
            post.setEntity(entity);
            CloseableHttpResponse response = httpClient.execute(post);
            System.out.println(response.getStatusLine().getStatusCode());

            HttpEntity entity2 = response.getEntity();
            in = new Scanner(entity2.getContent());
            while (in.hasNext()) {
                resp = in.next();
                System.out.println(resp);

            }
            EntityUtils.consume(entity2);
            return resp;

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(NetworkService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NetworkService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static String getRandomSeed(Network network) {
        int random = randInt(0, network.getPeerseed().size() - 1);
        return network.getPeerseed().get(random);
    }

    private static int randInt(int min, int max) {

        Random rand = new Random();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        rand.setSeed(timestamp.getTime());
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

}
