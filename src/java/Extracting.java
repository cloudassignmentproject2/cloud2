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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Extracting {

	private final String USER_AGENT = "Mozilla/5.0";

	public static void main(String[] args) throws Exception {

		Extracting http = new Extracting();

		System.out.println("Testing 1 - Send Http GET request");
		http.sendGet();
		

	}

	// HTTP GET request
	private void sendGet() throws Exception {

		String url = "http://datamall2.mytransport.sg/ltaodataservice/BusStops";
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		//add request header
		con.setRequestProperty("AccountKey", "0/4f2Df699ugo4NZdPHTBA==");
                con.setRequestProperty("UniqueUserId", "e06f7842-2355-4453-ba69-c8c74e3ab870");
                con.setRequestProperty("accept", "application/json");

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append("\n"+inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());

	}
	
	

}
