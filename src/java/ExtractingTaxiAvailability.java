/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Benjamin
 */
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;
import org.json.JSONArray;
import org.json.JSONObject;

public class ExtractingTaxiAvailability {

    private final String USER_AGENT = "Mozilla/5.0";

    public static void main(String[] args) throws Exception, FileNotFoundException {

        
        populateTaxiData(1);
//        printer = new PrintWriter(new File("web\\resources\\Taxi_2.csv"));
//        printer.write("Latitude,Longitude\n");
//        index = 0;
//        jsonData = http.sendGet(0).toString();
//        obj = new JSONObject(jsonData);
//        array = obj.getJSONArray("value");
//        while (array.length() > 0) {
//            for (int i = 0; i < array.length(); i++) {
//
//                JSONObject p = (JSONObject) array.get(i);
//                Object Latitude = p.get("Latitude");
//                Object Longitude = p.get("Longitude");
//                printer.write(Latitude + "," + Longitude+ "\n");
//            }
//            index += 50;
//            jsonData = http.sendGet(index).toString();
//            obj = new JSONObject(jsonData);
//            array = obj.getJSONArray("value");
//            printer.flush();
//
//        }
        
        
    }
//            JSONObject jsonObj = jsonMainArr.getJSONObject(i);

//            System.out.println(jsonObj.getString("Name"));
//System.out.println(obj.toString());
//        System.out.println("value: " + obj.getJsonString("value"));
//        System.out.println("RoadName: " + obj.getString("RoadName"));
//        System.out.println("value: " + obj.getJSONObject("value"));
// HTTP GET request
    
    private static void populateTaxiData(int fileNo) throws Exception, FileNotFoundException {
        ExtractingTaxiAvailability http = new ExtractingTaxiAvailability();

        PrintWriter printer = new PrintWriter(new File("web\\resources\\Taxi_"+fileNo+".csv"));
        printer.write("Latitude,Longitude\n");
        int index = 0;
        String jsonData = http.sendGet(0).toString();
        JSONObject obj = new JSONObject(jsonData);
        JSONArray array = obj.getJSONArray("value");
        
        while (array.length() > 0) {
            for (int i = 0; i < array.length(); i++) {

                JSONObject p = (JSONObject) array.get(i);
                Object Latitude = p.get("Latitude");
                Object Longitude = p.get("Longitude");
                printer.write(Latitude + "," + Longitude+ "\n");
            }
            index += 50;
            jsonData = http.sendGet(index).toString();
            obj = new JSONObject(jsonData);
            array = obj.getJSONArray("value");
            printer.flush();

        }
        
    }
    
    private StringBuffer sendGet(int dataIndex) throws Exception {

        String url = "http://datamall2.mytransport.sg/ltaodataservice/TaxiAvailability?$skip=" + dataIndex;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("AccountKey", "0/4f2Df699ugo4NZdPHTBA==");
        con.setRequestProperty("UniqueUserId", "e06f7842-2355-4453-ba69-c8c74e3ab870");
        con.setRequestProperty("accept", "application/json");

        int responseCode = con.getResponseCode();
//        System.out.println("\nSending 'GET' request to URL : " + url);
//        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine + "\n");
        }
        in.close();
        return response;
        //print result
//		System.out.println(response.toString());

    }
}
