package com.thecyclingapp.emiliyan.thecyclingapp.extras;

/**
 * Created by Emiliyan on 3/30/2016.
 */
public class TextFormat {

    /*
    * format average speed to 2 decimal places
    * */
    public static String formatAverageSpeed(double avgSpeed){
        return String.format("%1$,.2f m/s", avgSpeed);
    }
    /*
    * format milliseconds to hours:minutes:seconds
    * */
    public static String formatTime(long time){
        return ""+DateTimeUtils.formatDuration(time)+ " hours";
    }
    /*
    * format distance to 2 decimal places
    * */
    public static String formatDistance(double distance){
        return String.format("%1$,.2f meters",distance);
    }
    /*
    * compute approximately how long ago this date occurred
    * return minutes if more than a minute
    *        hours if more than an hour
    *        days if more than a day
    *        month if more than a month
    *        year if more than a year
    * */
    public static String formatTimeAgo(long date){
        return DateTimeUtils.computeDuration(date);
    }
    /*methods below are specific for profile activity workout view */
    public static String formatWorkoutViewTitle(String username){
        return username +" just finished his ride";
    }

    public static String formatWorkoutViewTime(long time){
        return "Time: "+ formatTime(time);
    }
    public static String formatWorkoutViewAvgSpeed(double avgSpeed){
        return "Average speed: "+ formatAverageSpeed(avgSpeed);
    }
    public static String formatWorkoutViewDistance(double distance){
        return "Distance: "+ formatDistance(distance);
    }
}
