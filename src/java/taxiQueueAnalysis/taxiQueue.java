/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiQueueAnalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import org.json.JSONObject;

/**
 *
 * @author GYX
 */
public class taxiQueue {
    

    public taxiQueue() {
    }
    
    public static void main(String[] arg){
        try{
            getTaxiQueueList();
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
        
    }
    public static ArrayList<String> getTaxiQueueList() throws FileNotFoundException, IOException{
      ArrayList<String> taxiQueueList = new ArrayList<String>();
        Path currentRelativePath = Paths.get("");
      String path = currentRelativePath.toAbsolutePath().toString();
      String file = path+"/web/resources/techcq-query-results.csv";
      HashMap<Integer, String[]> taxiQs = new HashMap<Integer, String[]>();
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
      Files.lines(Paths.get(file))
        //Long count = Files.lines(Paths.get(file),StandardCharsets.ISO_8859_1)
        //.parallel()
        .forEach(s -> {
            String[] row = s.split(",");
            if(!row[1].equals("taxi_stand_id")){
                //System.out.println(s);
                int taxiID = Integer.parseInt(row[1]);
                if(taxiQs.containsKey(taxiID)){
                    String[] rowInMap = taxiQs.get(taxiID);
                    try {
                    Date date = formatter.parse(row[6].replaceAll("\"", ""));
                    Date dateInMap = formatter.parse(rowInMap[6].replaceAll("\"", ""));
                        if(date.after(dateInMap)){
                            taxiQs.replace(taxiID, row);
                        }
                    } catch (ParseException e) {
                            e.printStackTrace();
                    }
                }
                else{
                    taxiQs.put(taxiID, row);
                }
            }
            
        });
      //PrintWriter printer = new PrintWriter(new File("web\\resources\\taxiQueue.csv"));
      //printer.write("taxi_stand_id,taxi_stand_name,taxi_num,people_num,lat,lon\n");
        
      for (int key : taxiQs.keySet()) {
          String[] queueInfo = taxiQs.get(key);
          String id = queueInfo[1];
          String name = queueInfo[2];
          String taxiNum = queueInfo[5];
          String peopleNum = queueInfo[4];
          String[] latlon = getLongtitudeLatitute(name);
          String listItem = id+","+name+","+taxiNum+","+peopleNum+","+latlon[0]+","+latlon[1];
          System.out.println(id+","+name+","+taxiNum+","+peopleNum+","+latlon[0]+","+latlon[1]);
          taxiQueueList.add(listItem);
        }
      
      return taxiQueueList;
      
    }
    
    public static String[] getLongtitudeLatitute(String address) throws MalformedURLException, ProtocolException, IOException{
        //address = "Singapore "+address;
        String key = "AIzaSyC7-wRn9K-fHU2xggxHdGU0M3JMllsumhM";
        String urlAddress = address.replace(" ", "+");
        URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=Singapore+" + urlAddress + "&key=" + key + "");
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




