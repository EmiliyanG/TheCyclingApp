package com.thecyclingapp.emiliyan.thecyclingapp.json;

import android.content.Context;

import com.google.api.client.util.Key;
import com.google.gson.Gson;
import com.thecyclingapp.emiliyan.thecyclingapp.dbTables.Workout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Emiliyan on 4/17/2016.
 */
public abstract class DownloadWorkouts extends ServerConnection {
    private static final String DOWNLOAD_WORKOUTS_URL = "http://thecyclingapp.co.uk/downloadWorkouts.php";
    private static final String PASSWORD = "simple";
    private String username;
    protected DownloadWorkouts(Context c) {
        super(c);
    }

    @Override
    public abstract void onResponseReceived(String response);



    public int checkResponse(String response) {
        System.out.println("message came from DownloadWorkouts checkresponse> " + response);
        if(response.equals(NO_INTERNET)) return ResponseStatus.NO_INTERNET;
        else if(response.contains(QUERY_RETURNED_ZERO_ROWS)) return ResponseStatus.ZERO_ROWS_RETURNED;
        else if(response==null || response.length() == 0) return ResponseStatus.UNKNOWN_ERROR;
        else return ResponseStatus.SUCCESS;
    }
    public ArrayList<Workout> parseJSON(String response){
        ArrayList<Workout> temp = null;



        Workouts workouts = new Gson().fromJson(response, Workouts.class);

        if(workouts.workouts!=null && workouts.workouts.size() > 0){//if JSON to Workouts.class successful and there is only one match from query result
            temp = new ArrayList<>();
            for(DownloadedWorkout dw: workouts.workouts){
                temp.add(new Workout(dw.avgSpeed,dw.distance,dw.time,dw.date,username));
            }
        }

        return temp;

    }


    public void downloadWorkouts(String username,int limit, int offset, long date){
        this.username = username;
        Map<String,String> params = new HashMap<String, String>();
        params.put("permission",PASSWORD);
        params.put("username",username);
        params.put("limit",""+limit);
        params.put("offset",""+offset);
        params.put("date",""+date);
        sendPost(DOWNLOAD_WORKOUTS_URL, params);
    }

    public static class Workouts{
        @Key("workouts")
        public List<DownloadedWorkout> workouts;
    }
    public static class DownloadedWorkout {

        @Key("user_id")
        public String username;
        @Key("time")
        public long time;
        @Key("avgSpeed")
        public double avgSpeed;
        @Key("distance")
        public double distance;
        @Key("date")
        public long date;
    }
}
