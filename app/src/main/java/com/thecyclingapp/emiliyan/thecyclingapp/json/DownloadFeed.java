package com.thecyclingapp.emiliyan.thecyclingapp.json;

import android.content.Context;

import com.google.api.client.util.Key;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Emiliyan on 4/19/2016.
 */


public abstract class DownloadFeed extends ServerConnection {

    private static final String DOWNLOAD_FEED_URL = "http://thecyclingapp.co.uk/downloadFeed.php";
    private static final String PASSWORD = "simple";
    private String username;

    protected DownloadFeed(Context c) {
        super(c);
    }



    @Override
    public abstract void onResponseReceived(String response);



    public int checkResponse(String response) {
        System.out.println("message came from DownloadFeed checkresponse> " + response);
        if(response.equals(NO_INTERNET)) return ResponseStatus.NO_INTERNET;
        else if(response.contains(QUERY_RETURNED_ZERO_ROWS)) return ResponseStatus.ZERO_ROWS_RETURNED;
        else if(response==null || response.length() == 0) return ResponseStatus.UNKNOWN_ERROR;
        else return ResponseStatus.SUCCESS;
    }

    public List<SingleFeed> parseJSON(String response){

        Feed feed = new Gson().fromJson(response, Feed.class);
        return feed.feed;
    }

    public void downloadFeed(String username,int limit, int offset){
        this.username = username;
        Map<String,String> params = new HashMap<>();
        params.put("permission",PASSWORD);
        params.put("username",username);
        params.put("limit",""+limit);
        params.put("offset",""+offset);
        sendPost(DOWNLOAD_FEED_URL, params);
    }

    public static class Feed{
        @Key("feed")
        public List<SingleFeed> feed;
        public void setFeed(List<SingleFeed> feed){
            this.feed = feed;
        }
    }

    public static class SingleFeed{
        @Key("username")
        public String username;
        @Key("time")
        public long time;
        @Key("avgSpeed")
        public double avgSpeed;
        @Key("distance")
        public double distance;
        @Key("date")
        public long date;
        @Key("firstname")
        public String firstname;
        @Key("lastname")
        public String lastname;


        public void setUsername(String username){
            this.username = username;
        }
        public void setTime(long time){
            this.time = time;
        }

    }




}
