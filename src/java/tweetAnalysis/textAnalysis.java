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
      getTweetList();
      
    }
    public static List<String[]> getTweetList(){
      Path currentRelativePath = Paths.get("");
      String s = currentRelativePath.toAbsolutePath().toString();
      String file = s+"/web/resources/smrt_tweet_data.txt";
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
