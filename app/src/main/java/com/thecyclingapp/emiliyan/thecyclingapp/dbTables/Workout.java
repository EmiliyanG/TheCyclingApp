package com.thecyclingapp.emiliyan.thecyclingapp.dbTables;

import java.util.Date;

/**
 * Created by Emiliyan on 3/25/2016.
 */
public class Workout {

    private double averageSpeed;
    private double distance;
    private long time;
    private long date;
    private String userId;

    public Workout(double averageSpeed ,double distance,long time,long date,String userId){
        this.averageSpeed =averageSpeed;
        this.distance= distance;
        this.time = time;
        this.date = date;
        this.userId = userId;
    }



    public double getAverageSpeed() {return averageSpeed;}



    public double getDistance() {return distance;}
    public void setDistance(double distance){this.distance = distance;}

    public long getTime() {return time;}
    public void setTime(long time){this.time = time;}


    public long getDate() {
        return date;
    }

    public String getUserId() {
        return userId;
    }
}
