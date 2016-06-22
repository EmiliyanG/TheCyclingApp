package com.thecyclingapp.emiliyan.thecyclingapp.json;

import android.content.Context;


import com.thecyclingapp.emiliyan.thecyclingapp.extras.RegisterResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Emiliyan on 4/16/2016.
 */
public abstract class UploadWorkout extends ServerConnection{
    private static final String PASSWORD = "simple";
    private static final String UPLOAD_WORKOUT_URL ="http://thecyclingapp.co.uk/uploadWorkout.php";

    private static final String UPLOAD_WORKOUT_SUCCESSFUL = "success";

    protected UploadWorkout(Context c) {
        super(c);
    }

    @Override
    public abstract void onResponseReceived(String response);

    public int checkResponse(String response) {
        System.out.println("message came from UploadWorkout checkresponse> "+response);
        if(response.equals(NO_INTERNET)) return ResponseStatus.NO_INTERNET;
        else if(response.contains(DUPLICATE_ENTRY)) return ResponseStatus.DUPLICATE_ENTRY;
        else if(response.equals(UPLOAD_WORKOUT_SUCCESSFUL)) return RegisterResult.SUCCESS;
        else return RegisterResult.UNKNOWN_ERROR;
    }

    public void uploadWorkoutQuery(String username, long time, double avgSpeed,double distance,long date){

        Map<String,String> params = new HashMap<String, String>();
        params.put("permission",PASSWORD);
        params.put("username",username);
        params.put("time",""+time);
        params.put("avgSpeed",""+avgSpeed);
        params.put("distance",""+distance);
        params.put("date",""+date);

        sendPost(UPLOAD_WORKOUT_URL, params);
    }
}
