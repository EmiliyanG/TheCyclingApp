package com.thecyclingapp.emiliyan.thecyclingapp.dbTables;

import android.app.Activity;
import android.os.AsyncTask;

import com.thecyclingapp.emiliyan.thecyclingapp.MyConnection;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.SaveVariables;
import com.thecyclingapp.emiliyan.thecyclingapp.json.DownloadStatistics;
import com.thecyclingapp.emiliyan.thecyclingapp.json.InsertStatistics;
import com.thecyclingapp.emiliyan.thecyclingapp.json.ResponseStatus;

/**
 * Created by Emiliyan on 3/31/2016.
 */
public abstract class StatisticsUtils {

    private Statistics myStatistics;



    public abstract void onStatisticsDownloaded(Statistics statistics);


    public void updateWeeklyStatistics(Activity activity,double averageSpeed ,double distance,long time){
        myStatistics.updateWeeklyStatistics(activity,averageSpeed,distance,time);
    }


    public void uploadStatistics(Activity activity){
        SaveVariables.saveStatistics(activity, myStatistics);
        InsertStatistics is = new InsertStatistics(activity.getApplicationContext()) {
            @Override
            public void onResponseReceived(String response) {
                //check for internet connection failure
            }
        };
        is.insertStatistics(myStatistics);


    }
    public void downloadStatistics(Activity activity, final String username){
        if(myStatistics == null){
            DownloadStatistics ds = new DownloadStatistics(activity.getApplicationContext()) {
                @Override
                public void onResponseReceived(String response) {
                    int status = this.checkStatus(response);
                    if(status== ResponseStatus.SUCCESS){
                        Statistics statistics = this.parseJSON(response);
                        myStatistics = statistics;
                        onStatisticsDownloaded(statistics);
                    }
                    else if(status == ResponseStatus.ZERO_ROWS_RETURNED){

                        Statistics statistics = new Statistics(username,0.0,0.0,0.0,0.0,0l,0l,0l,0l);
                        myStatistics = statistics;
                        onStatisticsDownloaded(statistics);

                    }


                }
            };
            ds.sendDownloadQuery(username);


        }
        else onStatisticsDownloaded(myStatistics);
    }



//    /*connection to database should be done on a separate thread*/
//    private class GetStatistics extends AsyncTask<String,Void,Statistics> {
//        String username;
//        @Override
//        protected Statistics doInBackground(String... params) {
//            username = params[0];
//            MyConnection conn = new MyConnection();
//            return conn.getStatisctics(username);
//        }
//        @Override
//        protected void onPostExecute(Statistics status) {
//            super.onPostExecute(status);
//            myStatistics = status;
//            onStatisticsDownloaded(status);
//        }
//    }
//
//    private class UpdateStatistics extends AsyncTask<Statistics,Void,Boolean>{
//        @Override
//        protected Boolean doInBackground(Statistics... params) {
//            Statistics statistics = params[0];
//            MyConnection conn = new MyConnection();
//            return conn.updateStatisticsForUser(statistics);
//        }
//        @Override
//        protected void onPostExecute(Boolean status) {
//            super.onPostExecute(status);
//        }
//    }
}
