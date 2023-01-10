/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gridergui;

/**
 *
 * @author user
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class WebClient {    
    public static String sendHTTP(String sURL, String sJSon, HashMap<String, String> headers) throws IOException{
        if (sURL.substring(0, 5).equalsIgnoreCase("https")){
            return httpsPostJSon(sURL, sJSon, headers);
        } else {
            return httpPostJSon(sURL, sJSon, headers);
        }        
    }
    
    private static String httpsPostJSon(String sURL, String sJSon, HashMap<String, String> headers) throws MalformedURLException, IOException {
        HttpsURLConnection conn = null;
        StringBuilder lsResponse = new StringBuilder();
        URL url = null;

        //Open network IO
        url = new URL(sURL);

        //opens a connection, then sends POST & set HTTP header nicely
        conn = (HttpsURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");

        if(headers != null){
            Set<Map.Entry<String, String>> entrySet = headers.entrySet();

            for(Map.Entry<String, String> entry : entrySet) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }

        }

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
        bw.write(sJSon);
        bw.flush();
        bw.close();

        if (!(conn.getResponseCode() == HttpsURLConnection.HTTP_CREATED ||
                conn.getResponseCode() == HttpsURLConnection.HTTP_OK)) {
            System.setProperty("store.error.info", String.valueOf(conn.getResponseCode()));
            System.out.println(lsResponse);
            return null;
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream())));

        String output;
        while ((output = br.readLine()) != null) {
            lsResponse.append(output);
        }
        conn.disconnect();

        return lsResponse.toString();
    }
    
    private static String httpPostJSon(String sURL, String sJSon, HashMap<String, String> headers) throws MalformedURLException, IOException{
        HttpURLConnection conn = null;
        StringBuilder lsResponse = new StringBuilder();
        URL url = null;
        
        //Open network IO 
        url = new URL(sURL);

        //opens a connection, then sends POST & set HTTP header nicely
        conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        //conn.setRequestProperty("Content-Type", "application/json");
        
        if(headers != null){
            Set<Map.Entry<String, String>> entrySet = headers.entrySet();

            for(Map.Entry<String, String> entry : entrySet) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }

        }
        
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
        bw.write(sJSon);
        bw.flush();
        bw.close();

        BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

        String output;
        while ((output = br.readLine()) != null) {
                lsResponse.append(output);
        }
        conn.disconnect(); 

        return lsResponse.toString();
    }
    
    public static JSONObject RequestClientToken(String fsProductID, String fsClientID, String fsUserIDxx){
        String url = "https://restgk.guanzongroup.com.ph/x-api/v1.0/auth/token_request.php";
        
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        headers.put("g-api-id", fsProductID);
        headers.put("g-api-client", fsClientID);
        headers.put("g-api-user", fsUserIDxx);
        
        JSONObject loJSON = new JSONObject();
        
        try {
            String response = WebClient.sendHTTP(url, "", (HashMap<String, String>) headers);
            
            if(response == null){
                JSONObject loErr = new JSONObject();
                loErr.put("code", 500);
                loErr.put("message", "Server has no response.");
                
                loJSON.put("result", "error");
                loJSON.put("error", loErr);
                
                return loJSON;
            } else {
                JSONParser loParser = new JSONParser();
                return (JSONObject) loParser.parse(response);
            }
        } catch (IOException | ParseException ex) {
            JSONObject loErr = new JSONObject();
            loErr.put("code", 0);
            loErr.put("message", ex.getMessage());

            loJSON.put("result", "error");
            loJSON.put("error", loErr);

            return loJSON;
        }
    }
    
    public static JSONObject RequestAccessToken(String fsClientKy){
        String url = "https://restgk.guanzongroup.com.ph/x-api/v1.0/auth/access_request.php";
        
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Content-Type", "application/json");
        headers.put("g-client-key", fsClientKy);
        
        JSONObject loJSON = new JSONObject();
        
        try {
            String response = WebClient.sendHTTP(url, "", (HashMap<String, String>) headers);
            
            if(response == null){
                JSONObject loErr = new JSONObject();
                loErr.put("code", 500);
                loErr.put("message", "Server has no response.");
                
                loJSON.put("result", "error");
                loJSON.put("error", loErr);
                
                return loJSON;
            } else {
                JSONParser loParser = new JSONParser();
                return (JSONObject) loParser.parse(response);
            }
        } catch (IOException | ParseException ex) {
            JSONObject loErr = new JSONObject();
            loErr.put("code", 0);
            loErr.put("message", ex.getMessage());

            loJSON.put("result", "error");
            loJSON.put("error", loErr);

            return loJSON;
        }
    }
}
