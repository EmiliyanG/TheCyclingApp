package com.thecyclingapp.emiliyan.thecyclingapp.json;

import android.content.Context;

import com.thecyclingapp.emiliyan.thecyclingapp.dbTables.Statistics;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.RegisterResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Emiliyan on 4/17/2016.
 */
public abstract class InsertStatistics extends ServerConnection {
    private static final String PASSWORD = "simple";
    private static final String UPLOAD_STATISTICS_URL ="http://thecyclingapp.co.uk/insertStatistics.php";
    private static final String UPLOAD_STATISTIC_SUCCESSFUL = "success";

    protected InsertStatistics(Context c) {
        super(c);
    }

    @Override
    public abstract void onResponseReceived(String response);


    public int checkResponse(String response) {
        System.out.println("message came from UploadStatistic checkresponse> " + response);
        if(response.equals(NO_INTERNET)) return ResponseStatus.NO_INTERNET;
        else if(response.contains(DUPLICATE_ENTRY)) return ResponseStatus.DUPLICATE_ENTRY;
        else if(response.equals(UPLOAD_STATISTIC_SUCCESSFUL)) return ResponseStatus.SUCCESS;
        else return ResponseStatus.UNKNOWN_ERROR;
    }



    public void insertStatistics(Statistics st){

        Map<String,String> params = new HashMap<String, String>();
        params.put("permission",PASSWORD);
        params.put("username",st.getUserId());
        params.put("weekly_avg_speed_sum",""+st.getWeeklyAvgSpeedSum());
        params.put("weekly_avg_speed_count",""+st.getWeeklyAvgSpeedCount());
        params.put("weekly_distance",""+st.getWeeklyDistance());
        params.put("weekly_time",""+st.getWeeklyTime());
        params.put("total_avg_speed_sum",""+st.getTotalAvgSpeedSum());
        params.put("total_avg_speed_count",""+st.getTotalAvgSpeedCount());
        params.put("total_distance",""+st.getTotalDistance());
        params.put("total_time",""+st.getTotalTime());
        sendPost(UPLOAD_STATISTICS_URL, params);
    }

}
