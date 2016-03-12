/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiQueueAnalysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import org.json.JSONObject;

/**
 *
 * @author GYX
 */
public class taxiQueue {
    

    public taxiQueue() {
    }
    
//    public static void main(String[] arg){
//        String address = "WestGate";
//        try{
//            String[] latlon = getLongtitudeLatitute("Singapore "+address, google_Key);
//            System.out.println("lat = "+latlon[0]+", lon = "+latlon[1]);
//        }
//        catch(Exception ex){
//            ex.printStackTrace();
//        }
//        
//    }
    public void writeTaxiQueueCSV(Date date){
        Path currentRelativePath = Paths.get("");
      String s = currentRelativePath.toAbsolutePath().toString();
      String file = s+"/web/resources/smrt_tweet_data.txt";
      
    }
    
    public String[] getLongtitudeLatitute(String address) throws MalformedURLException, ProtocolException, IOException{
        //address = "Singapore "+address;
        String key = "AIzaSyC7-wRn9K-fHU2xggxHdGU0M3JMllsumhM";
        String urlAddress = address.replace(" ", "+");
        URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=Singapore" + urlAddress + "&key=" + key + "");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        conn.setRequestMethod("GET");
        conn.setAllowUserInteraction(false);
        conn.setDoOutput(true);
        conn.setRequestProperty("Accept", "application/json");
        
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                            + conn.getResponseCode());
	}        
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        
        String temp = "";
        String jsonString = "";
                
        while((temp = br.readLine()) != null){
            jsonString += temp;
        }
        conn.disconnect();
        //System.out.println("Json String = "+jsonString);
        JSONObject obj = new JSONObject(jsonString);
        //System.out.println(obj.getJSONArray("results"));
        JSONObject resultsElement = obj.getJSONArray("results").getJSONObject(0);
        JSONObject geometry = resultsElement.getJSONObject("geometry");
        JSONObject location = geometry.getJSONObject("location");
        String lng = location.get("lng").toString();
        String lat = location.get("lat").toString();
        String [] lngLat = new String[2];
        lngLat[0] = lat;
        lngLat[1] = lng;
        
        return lngLat;
    }
    
}




