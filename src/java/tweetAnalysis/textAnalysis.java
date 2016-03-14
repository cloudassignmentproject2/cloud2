/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author YX
 */
package tweetAnalysis;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.*;
import java.util.*;
import java.nio.file.*;
public class textAnalysis {

    public textAnalysis() {
    }
    
    
    public static void main(String[] args)
    {
      List<String[]> list = getTweetList();
      System.out.println("List length = "+list.size());
      
    }
    
    private static void downloadUsingStream(String urlStr, String file) throws IOException{
        URL url = new URL(urlStr);
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count=0;
        while((count = bis.read(buffer,0,1024)) != -1)
        {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
    }
    
    public static List<String[]> getTweetList(){
      Path currentRelativePath = Paths.get("");
      String url = "http://ec2-54-169-123-186.ap-southeast-1.compute.amazonaws.com/ckan/dataset/d114a6d2-b27c-447f-80b0-f0ee843357f6/resource/9e4c0961-c4e9-402e-bad7-a9d6ac947bfe/download/smrt-tweet-data.txt";
      String s = currentRelativePath.toAbsolutePath().toString();
      System.out.println("current Path = "+s);
    try {
           downloadUsingStream(url, s+"/smrt_tweet_data.txt");
        } catch (IOException e) {
            e.printStackTrace();
       }
      
      File test = new File(s+"/smrt_tweet_data.txt");
      System.out.println("File length = "+ test.length());
      String file = s+"/smrt_tweet_data.txt";
      String[] compare = {
          "breakdown"
          ,"stuck"
          ,"long"
//          ,"stop"
          
      };
      Date date = new Date();
      List<String[]> tweets = filterTweets(file, compare, date);
//      for(int i=0; i < tweets.size(); i++){
//          String[] tweet = tweets.get(i);
//          System.out.println("Message:"+tweet[0]+", Date:"+tweet[1]);
//      }
        return tweets;
    }
    public static List<String[]> filterTweets(String file, String[] compare, Date date){
        List<String[]> tweets = new ArrayList<String[]>();
        try{
        tweets = Files.lines(Paths.get(file),StandardCharsets.ISO_8859_1)
        //Long count = Files.lines(Paths.get(file),StandardCharsets.ISO_8859_1)
        //.parallel()
        .map(s -> getMessageAndDate(s))
        //.filter(d -> filterDate(d[1],date))
        .filter(a -> filterCheck(a[0],compare))
        .collect(Collectors.toList());

      }
      catch(Exception e){
        e.printStackTrace();
      }
        return tweets;
    }
    public static Boolean filterDate(String d, Date date){
        boolean sameDay = false;
        //Converting the milli sec to Date object
        Date messageDate = new Date(Long.parseLong(d));
        //Getting the dow mon dd hh
        String mDate = messageDate.toString().split(":")[0];
        String cDate = date.toString().split(":")[0];
        if(mDate.equals(cDate)){
            sameDay = true;
        }
        return sameDay;
    }
    
    public static Boolean filterCheck(String a, String[] compare){
        boolean exist = false;
        for(int i=0; i < compare.length; i++){
            if(a.contains(compare[i])){
                exist = true;
            }
        }
        return exist;
    }
    
    public static String[] getMessageAndDate(String s){
        //System.out.println("getMessageAndDate");
        String[] messageAndDate = new String[2];
        String[] original = s.split(",\"");
        String message = original[0].split(":")[1];
        //System.out.println("message = "+message);
        String date = original[2].split(":")[1];
        messageAndDate[0] = message;
        messageAndDate[1] = date;
        return messageAndDate;
    }

}
