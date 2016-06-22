package com.thecyclingapp.emiliyan.thecyclingapp.extras;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by Emiliyan on 3/26/2016.
 */
public class DateTimeUtils {

    private static final String[] DAYS = {"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};
    private static final String DAY_OF_WEEK = "EE";//patern "u" is not recognised for some reason
    private static final String SAME_DAY_IN_YEAR = "D";
    private static final String YEAR = "yyyy";
    public static final long[] time = {TimeUnit.DAYS.toMillis(365),//year
            TimeUnit.DAYS.toMillis(30),//month
            TimeUnit.DAYS.toMillis(1),//day
            TimeUnit.HOURS.toMillis(1),//hour
            TimeUnit.MINUTES.toMillis(1),//minute
            TimeUnit.SECONDS.toMillis(1)};//second
    public static final String[] timeString = {"year","month","day","hour","minute","second"};

    //computation is not accurate as there are not only 30 days in every month for example
    // the point of that method is to show approximate time
    public static String computeDuration(long duration) {
        String s;
        long currentDate = new Date().getTime();
        duration= currentDate-duration;

        for(int i=0; i < time.length; i++) {
            long current = time[i];
            long temp = duration/current;
            if(temp>0) {

                if(temp>1) s = ""+temp+" "+timeString[i]+"s ago";
                else s = ""+temp+" "+timeString[i]+" ago";
                return s;
            }
        }
        return "0 seconds ago";

    }



    /*
     * check if given date is within current week
     * get Monday date for current week and date's week
     * check the day number for both Monday dates
     * hours minutes and seconds are ignored
     * method works based on the fact there is only 1 Monday in a week
     * alternative solution would be to use joda time however external library should be imported
     * duplicate classes (with jasypt library) appear after importing library
     * third alternative is using Calendar class for calculation, however
     * calendar class has unexpected and unreliable behaviour
     * example where calendar class breaks december 28th 2015 is monday, 1st January 2016 is friday
     * both dates are within the same week however calendar class would return false
     *
     * method would break if two dates are both Mondays and have the same day number in a year, however are in different years
     * repetition of days happens once every 6 years which relatively big time
     * therefore checking same year eliminates that problem (adds additional cost to performance)
     *
     * */
    public static boolean sameWeek(long date){
        long todaysDate = new Date().getTime();
        long thisMonday = getMondayDate(todaysDate);
        long monday = getMondayDate(date);
        return sameDayInYear(thisMonday,monday) && sameYear(thisMonday, monday);
    }

    /* convert date to a Monday date
     * */
    public static long getMondayDate(long date){
        SimpleDateFormat sdf = new SimpleDateFormat(DAY_OF_WEEK);
        String dayText = sdf.format(date);
        int day =0;//range for int: 0-6
        //compute how many days ago was Monday: e.g. if today is Friday(5th day) monday should be 5 - 4
        for(int i = 0;i<DAYS.length;i++){
            if(dayText.equalsIgnoreCase(DAYS[i])){
                day = i;
            }
        }
        return date - TimeUnit.DAYS.toMillis(day);//get passed days in milliseconds and subtract from current date to get Monday
    }




    //check if day number matches
    private static boolean sameDayInYear(long todaysDate,long lastDate){
        SimpleDateFormat sdf = new SimpleDateFormat(SAME_DAY_IN_YEAR);
        return sdf.format(todaysDate).equals(sdf.format(lastDate));
    }
    //check if two dates are within the same year
    private static boolean sameYear(long todaysDate,long lastDate){
        SimpleDateFormat sdf = new SimpleDateFormat(YEAR);
        return sdf.format(todaysDate).equals(sdf.format(lastDate));
    }

    /*
    * Author: Bohemian
    * Open source code taken from: http://stackoverflow.com/questions/9027317/how-to-convert-milliseconds-to-hhmmss-format
    * answered Jan 27 '12 at 0:11
    *
    * Date link accessed: 26 March 2016
    * */
    public static String formatDuration(long millis) {
       return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
    }


}
