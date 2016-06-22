package com.thecyclingapp.emiliyan.thecyclingapp.json;

import android.content.Context;

import com.google.api.client.util.Key;
import com.google.gson.Gson;
import com.thecyclingapp.emiliyan.thecyclingapp.dbTables.Statistics;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.LogInResult;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.Passwords;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Emiliyan on 4/17/2016.
 */
public abstract class DownloadStatistics extends ServerConnection {
    private static final String PASSWORD = "simple";
    private static final String DOWNLOAD_STATISTICS_URL = "http://thecyclingapp.co.uk/downloadStatistics.php";


    protected DownloadStatistics(Context c) {
        super(c);
    }

    @Override
    public abstract void onResponseReceived(String response);




    //return true if password and username pair is correct and exists in the database
    public int checkStatus(String response) {
        System.out.println(response);
        if(response.equals(NO_INTERNET)) return ResponseStatus.NO_INTERNET;
        else if(response==null||response.length()==0)return ResponseStatus.UNKNOWN_ERROR;

        else if(response.equals(QUERY_RETURNED_ZERO_ROWS)) return ResponseStatus.ZERO_ROWS_RETURNED;
        else return ResponseStatus.SUCCESS;
    }

    public Statistics parseJSON(String response){

        DownloadedStatistics ds = new Gson().fromJson(response, DownloadedStatistics.class);
        return new Statistics(ds.user_id,ds.weekly_avg_speed_sum,ds.weekly_distance, ds.total_avg_speed_sum,ds.total_distance,ds.weekly_avg_speed_count,
                ds.weekly_time,ds.total_avg_speed_count,ds.total_time);
    }




    public void sendDownloadQuery(String username){
        Map<String,String> params = new HashMap<String, String>();
        params.put("permission",PASSWORD);
        params.put("username",username);
        sendPost(DOWNLOAD_STATISTICS_URL, params);
    }


    /**
     * Created by Emiliyan on 4/16/2016.
     */


    public static class DownloadedStatistics {
        @Key("user_id")
        public String user_id;
        @Key("weekly_avg_speed_sum")
        public double weekly_avg_speed_sum;
        @Key("weekly_avg_speed_count")
        public long weekly_avg_speed_count;
        @Key("weekly_distance")
        public double weekly_distance;
        @Key("weekly_time")
        public long weekly_time;

        @Key("total_avg_speed_sum")
        public double total_avg_speed_sum;
        @Key("total_avg_speed_count")
        public long total_avg_speed_count;
        @Key("total_distance")
        public double total_distance;
        @Key("total_time")
        public long total_time;

    }

}
