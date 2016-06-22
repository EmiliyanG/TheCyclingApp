package com.thecyclingapp.emiliyan.thecyclingapp.dbTables;

import android.app.Activity;

import com.thecyclingapp.emiliyan.thecyclingapp.extras.DateTimeUtils;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.SaveVariables;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Emiliyan on 3/29/2016.
 */
public class Statistics implements Serializable{
    private static final String SAVED_DATE = "lastSavedDate";
    private String userId;
    private double weeklyAvgSpeedSum,
            weeklyDistance,
            totalAvgSpeedSum,
            totalDistance;

    private long weeklyAvgSpeedCount,
            weeklyTime,
            totalAvgSpeedCount,
            totalTime;

    //constructor initialises private variables
    public Statistics(String userId,
                      double weeklyAvgSpeedSum,
                      double weeklyDistance,
                      double totalAvgSpeedSum,
                      double totalDistance,
                      long weeklyAvgSpeedCount,
                      long weeklyTime,
                      long totalAvgSpeedCount,
                      long totalTime){
        this.userId = userId;
        this.weeklyAvgSpeedSum =weeklyAvgSpeedSum;
        this.weeklyDistance =weeklyDistance;
        this.totalAvgSpeedSum =totalAvgSpeedSum;
        this.totalDistance =totalDistance;
        this.weeklyAvgSpeedCount= weeklyAvgSpeedCount;
        this.weeklyTime =weeklyTime;
        this.totalAvgSpeedCount =totalAvgSpeedCount;
        this.totalTime =totalTime;

    }
    //default constructor to be used if statistic is saved for the first time
    public Statistics(){
    }

    //add statistics from a single workout
    //any computations relevant to the statistics are handled here
    //external computations should be avoided in order to preserve of consistency
    //weekly statistics should be reset if it is a new week
    //total statistics are always updated
    public void updateWeeklyStatistics(Activity activity,
                                       double avgSpeed,
                                       double distance,
                                       long time){

        //weekly statistics should be reset every week
        long savedDate = SaveVariables.getLong(activity,SAVED_DATE);//check for saved date state

        //if there is indeed a saved date and it is the same week
        if(savedDate!= SaveVariables.DEFAULT_VALUE_LONG && DateTimeUtils.sameWeek(savedDate)){
            //add statistics to previous records
            this.weeklyAvgSpeedSum += avgSpeed;
            this.weeklyDistance += distance;
            this.weeklyAvgSpeedCount++;
            this.weeklyTime += time;

        }else{ //else if it is a new week or no date was previously saved
            SaveVariables.saveLong(activity,SAVED_DATE,new Date().getTime());//save new date state
            //ignore any previous records for weekly statistics
            this.weeklyAvgSpeedSum = avgSpeed;
            this.weeklyDistance = distance;
            this.weeklyAvgSpeedCount = 1;
            this.weeklyTime = time;
        }
        //total statistics should never be ignored
        totalAvgSpeedSum += avgSpeed;
        totalAvgSpeedCount ++;
        totalDistance += distance;
        totalTime += time;
    }


    //calculate weekly average speed
    public double getWeeklyAvgSpeed(){return weeklyAvgSpeedSum /(double)weeklyAvgSpeedCount;}
    //calculate total average speed
    public double getTotalAvgSpeed(){return totalAvgSpeedSum / (double)totalAvgSpeedCount;}

    /*getters*/
    public double getWeeklyAvgSpeedSum() {return weeklyAvgSpeedSum;}
    public String getUserId() {return userId;}
    public double getWeeklyDistance() {return weeklyDistance;}
    public double getTotalAvgSpeedSum() {return totalAvgSpeedSum;}
    public double getTotalDistance() {return totalDistance;}
    public long getWeeklyAvgSpeedCount() {return weeklyAvgSpeedCount;}
    public long getWeeklyTime() {return weeklyTime;}
    public long getTotalAvgSpeedCount() {return totalAvgSpeedCount;}
    public long getTotalTime() {return totalTime;}

    public String toString(){
        return userId+" "+weeklyAvgSpeedSum+" "+
                weeklyDistance+" "+
                totalAvgSpeedSum+" "+
                totalDistance+" "+
                weeklyAvgSpeedCount+" "+
                weeklyTime+" "+
                totalAvgSpeedCount+" "+
                totalTime;
    }
}
