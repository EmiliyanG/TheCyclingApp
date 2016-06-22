package com.thecyclingapp.emiliyan.thecyclingapp.extras;

import android.location.Location;
import android.os.CountDownTimer;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.text.DecimalFormat;
import java.util.Date;

/**
 * Created by Emiliyan on 2/24/2016.
 */
public class SpeedTracker {
    private static final int TIMER_TICK = 1000;//used to set the timer's ticks in milliseconds
    private static final int DISPLAY_DEFAULT_SPEED = 5;
    //GPS tracker records different locations every time even if the device does not move
    //to avoid that anomaly chose radius tolerance from your location for which
    // the assumption is that device location does not change
    private static final double LOCATION_RADIUS_TOLERANCE = 3.0;//meter tolerance
    public static final String DEFAULT_SPEED = ".0";//if the device is not moving display this to screen
    public static final String DEFAULT_DISTANCE = ".0";
    private boolean recordSpeed = true;//while true fire a timer every second
    private Date date = new Date();
    private long currentDate=0l;
    private long lastDate =0l;
    private GoogleMap mMap;
    private TextView currentSpeed;
    private TextView distanceTextView;
    private double distance = 0.0;//the current covered distance by the cyclist
    private LatLng lastLocation;//store last location
    private DecimalFormat df = new DecimalFormat("###.0");
    private int displayDefaultSpeed = 0;
    private double speed = 0.0;
    private float[] results = new float[3];
    //used to determine average speed
    private double computedDistance;
    private double speedSum = 0.0;//calculate by for
    private int currentSpeedCount = 0;
    private double topSpeed = 0.0;


    //needs references to the GoogleMap object(used to determine the current location)
    // TextView currentSpeed to display speed updates
    // TextView distance to display covered distance updates
    public SpeedTracker(GoogleMap mMap, TextView currentSpeed, TextView distance){
        this.mMap = mMap;
        this.currentSpeed = currentSpeed;
        this.distanceTextView = distance;
    }

    public  void enableRecordingSpeed(){recordSpeed = true;}



    /*
    * given a set of doubles
    * speedSum = the sum of all elements of the set
    * currentSpeedCount = number of elements in the set
    * to increase performance keeping reference to individual elements is not needed
    *
    * */
    private void updateAverageSpeed(double speed){
        speedSum += speed;
        currentSpeedCount++;
        if(topSpeed<speed) topSpeed= speed;
    }
    public void stopRecordingSpeed(){
        recordSpeed=false;
        speedSum = 0.0;
        currentSpeedCount = 0;
        distance = 0.0;
    }
    public double getAverageSpeed(){
       return  speedSum / (double)currentSpeedCount;
    }
    public double getDistance(){
        return distance;
    }

    public void startRecordingSpeed(){

        if(recordSpeed){//check if recording speed is allowed
            new CountDownTimer(TIMER_TICK, TIMER_TICK) {//fire a timer every half second

                public void onTick(long millisUntilFinished) {}//method not used

                public void onFinish() {//when the timer finish


                    if(currentDate == 0l) currentDate = date.getTime();
                    Location location = mMap.getMyLocation();

                    LatLng currentLocation = new LatLng(location.getLatitude(),location.getLongitude());//convert my location to a LatLng object

                    if(lastLocation == null){
                        lastLocation = currentLocation;  //save initial location
                        lastDate = currentDate;
                    }

                    computedDistance = computeDistance(lastLocation,currentLocation);//compute the distance between the last and the current location

                    if(computedDistance>LOCATION_RADIUS_TOLERANCE) {//if the computed distance is bigger than a meter
                        distance += computedDistance;//update distance
                        //Velocity = distance/ time
                        //speed = computedDistance/ ((double)(currentDate-lastDate)/1000.0);
                        if(location.hasSpeed()) speed = location.getSpeed();
                        updateAverageSpeed(speed);
                        currentSpeed.setText(df.format(speed));//speed in meters per second
                        displayDefaultSpeed = 0;
                        lastLocation = currentLocation;//update last location
                        lastDate = currentDate;
                    }else{//if the computed distance is smaller than a meter i assume that the cyclist's location does not change
                        if(displayDefaultSpeed==DISPLAY_DEFAULT_SPEED) currentSpeed.setText(DEFAULT_SPEED);
                        displayDefaultSpeed++;
                    }

                    distanceTextView.setText(df.format(distance));


                    startRecordingSpeed();//use recursion until recording speed is disabled
                }
            }.start();
        }
    }

    public void pauseRecordingSpeed(){recordSpeed=false;}


    public double computeDistance(LatLng from, LatLng to){
        Location.distanceBetween(from.latitude,from.longitude,to.latitude,to.longitude,results);
       return results[0];//compute distance in meters
    }

}
