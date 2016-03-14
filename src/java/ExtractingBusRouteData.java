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

public class ExtractingBusRouteData {

    private final String USER_AGENT = "Mozilla/5.0";

    public static void main(String[] args) throws Exception, FileNotFoundException {

        ExtractingBusRouteData http = new ExtractingBusRouteData();

        PrintWriter printer = new PrintWriter(new File("web\\resources\\BusRoutes.csv"));
        printer.write("ServiceNo,Operator,Direction,StopSequence,BusStopCode,Distance,WD_FirstBus,WD_LastBus,SAT_FirstBus,SAT_LastBus,SUN_FirstBus,SUN_LastBus\n");
        int index = 0;
        String jsonData = http.sendGet(0).toString();
        JSONObject obj = new JSONObject(jsonData);
        JSONArray array = obj.getJSONArray("value");
        while (array.length() > 0) {
            for (int i = 0; i < array.length(); i++) {

                JSONObject p = (JSONObject) array.get(i);
                String ServiceNo = p.getString("ServiceNo");
                String Operator = p.getString("Operator");
                Object Direction = p.get("Direction");
                Object StopSequence = p.get("StopSequence");
                Object BusStopCode = p.get("BusStopCode");
                Object Distance = p.get("Distance");
                Object WD_FirstBus = p.get("WD_FirstBus");
                Object WD_LastBus = p.get("WD_LastBus");
                Object SAT_FirstBus = p.get("SAT_FirstBus");
                Object SAT_LastBus = p.get("SAT_LastBus");
                Object SUN_FirstBus = p.get("SUN_FirstBus");
                Object SUN_LastBus = p.get("SUN_LastBus");

                printer.write(ServiceNo + "," + Operator + "," + Direction + "," + StopSequence + "," + BusStopCode + "," + Distance + "," + WD_FirstBus + "," + WD_LastBus + "," + SAT_FirstBus + "," + SAT_LastBus + "," + SUN_FirstBus + "," + SUN_LastBus + "\n");
            }
            index += 50;
            jsonData = http.sendGet(index).toString();
            obj = new JSONObject(jsonData);
            array = obj.getJSONArray("value");
            printer.flush();

        }
    }
//            JSONObject jsonObj = jsonMainArr.getJSONObject(i);

//            System.out.println(jsonObj.getString("Name"));
//System.out.println(obj.toString());
//        System.out.println("value: " + obj.getJsonString("value"));
//        System.out.println("RoadName: " + obj.getString("RoadName"));
//        System.out.println("value: " + obj.getJSONObject("value"));
// HTTP GET request
    private StringBuffer sendGet(int dataIndex) throws Exception {

        String url = "http://datamall2.mytransport.sg/ltaodataservice/BusRoutes?$skip=" + dataIndex;

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
