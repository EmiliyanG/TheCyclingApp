package com.thecyclingapp.emiliyan.thecyclingapp.activities;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.thecyclingapp.emiliyan.thecyclingapp.dbTables.Statistics;
import com.thecyclingapp.emiliyan.thecyclingapp.dbTables.StatisticsUtils;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.GoogleQueries;
import com.thecyclingapp.emiliyan.thecyclingapp.MyConnection;
import com.thecyclingapp.emiliyan.thecyclingapp.R;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.SaveVariables;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.SpeedTracker;
import com.thecyclingapp.emiliyan.thecyclingapp.dbTables.Workout;
import com.thecyclingapp.emiliyan.thecyclingapp.json.UploadWorkout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Cycle extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;//built-in mechanisms to handle certain errors
    private final static int TIMER_COUNT_DOWN= 4000;
    private static final LatLng NEWCASTLE = new LatLng(54.9667, -1.6000);

    private String username;
    private SpeedTracker speedTracker;

//    private Statistics myStatistics;
    private Activity myActivity = this;

    public static final String TAG = Cycle.class.getSimpleName();

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private GoogleApiClient mGoogleApiClient;

    private LocationRequest mLocationRequest;

    private Menu myMenu = new Menu();
    private EditText search;// the search bar
    private boolean predictionChosen = false;//used for autocompletion(if prediction chosen do not make any more prediction queries)
    private boolean locationIsSet = false;
    private LatLng currentLocation;
    private Polyline lastRoute;//stores reference to the last route drawn on the map
    private Button startPause;
    private RelativeLayout topDisplay;
    private Chronometer myChronometer;
    private LinearLayout topSearch,scrollLayout;
    private FrameLayout pausedLayout,countDownDisplay;
    private static long currentChronometerTime = 0l;
    private TextView currentSpeed,distance,countDown;
    private DecimalFormat df = new DecimalFormat("###.0");
    private int background, custom_dark, custom_medium, custom_light, custom_lighter, custom_lightest;
    private StatisticsUtils statisticsUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cycle);
        setColors();
        setUpMapIfNeeded();

        Bundle extras = getIntent().getExtras();
        if(extras != null) username = extras.getString(LogInActivity.USERNAME); // retrieve the data using keyName

        //set up the menu
        myMenu.setUpMenu(this, (ImageButton) findViewById(R.id.menuButton),
                (DrawerLayout) findViewById(R.id.drawerLayout),
                (ListView) findViewById(R.id.drawerList), username);

        linkUIElements();

    }



    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();


    }


    private void linkUIElements(){

        //lint to UI elements
        search = (EditText) findViewById(R.id.searchBar);
        scrollLayout = (LinearLayout) findViewById(R.id.scrollViewLayout);
        startPause = (Button) findViewById(R.id.startPause);
        topDisplay = (RelativeLayout) findViewById(R.id.top_display);
        myChronometer = (Chronometer) findViewById(R.id.chronometer);
        countDownDisplay = (FrameLayout) findViewById(R.id.countDownDisplay);
        topSearch = (LinearLayout) findViewById(R.id.top_search);
        pausedLayout = (FrameLayout) findViewById(R.id.pausedLayout);
        countDown = (TextView) findViewById(R.id.countDown);
        currentSpeed = (TextView) findViewById(R.id.currentSpeed);
        distance = (TextView) findViewById(R.id.distance);
        speedTracker = new SpeedTracker(mMap, currentSpeed, distance);

        final Button btnContinue = (Button) findViewById(R.id.pausedLayoutContinue);
        final Button btnStop = (Button) findViewById(R.id.pausedLayoutStop);



        //add listeners

        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                scrollLayout.removeAllViews();
                //typed more than 4 characters in the search bar
                if(s.length()>4&&!predictionChosen){
                    new ShowPredictions().execute(s.toString());

                }

                if(s.length()<=4){
                    predictionChosen = false;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }
            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });


        startPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (topDisplay.getVisibility() == View.GONE) {//start cycling route was selected
                    if (predictionChosen) {
                        showCyclingDisplay();
                    } else {
                        Toast.makeText(myActivity, " please select route first ", Toast.LENGTH_LONG).show();
                    }

                } else {//pause cycling
                    pauseCycling();
                }
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showCyclingDisplay();

            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopCycling();//save workout in database

                topSearch.setVisibility(View.VISIBLE);
                pausedLayout.setVisibility(View.GONE);
                startPause.setText(getResources().getString(R.string.cycle_start_button));
                currentChronometerTime=0l;
                speedTracker.stopRecordingSpeed();
                distance.setText("0");
                currentSpeed.setText(SpeedTracker.DEFAULT_SPEED);


            }
        });

        statisticsUtils = new StatisticsUtils() {
            @Override
            public void onStatisticsDownloaded(Statistics statistics) {

                System.out.println("Statistics: "+ statistics.toString());
            }//statistics are not needed in this activity
        };
    }
    public static long getCurrentChronometerTime(){
        return currentChronometerTime;
    }
    /*
    *
    * hide search bar; hide paused layout; start count down wait till count down is finished and
    * show top display
    *
    *
    * */
    private void showCyclingDisplay(){
        statisticsUtils.downloadStatistics(this,username); //get statistics for this user so that whenever user finishes his/her workout statistics will be updated
        speedTracker.enableRecordingSpeed();
        topSearch.setVisibility(View.GONE);
        pausedLayout.setVisibility(View.GONE);
        countDownDisplay.setVisibility(View.VISIBLE);
        startPause.setText(getResources().getString(R.string.cycle_pause_button));
        new CountDownTimer(TIMER_COUNT_DOWN, 1000) {//fire a timer

            public void onTick(long millisUntilFinished) {
                String secondsRemaining = "" + millisUntilFinished / 1000;
                countDown.setText(secondsRemaining);
            }

            public void onFinish() {
                countDownDisplay.setVisibility(View.GONE);
                topDisplay.setVisibility(View.VISIBLE);//make it visible

                if(currentChronometerTime == 0l){//if user starts cycling now
                    myChronometer.setBase(SystemClock.elapsedRealtime());//start timer from 00:00

                    distance.setText(SpeedTracker.DEFAULT_DISTANCE);//reset distance
                }else{

                    myChronometer.setBase(SystemClock.elapsedRealtime() - currentChronometerTime);//start timer from the last saved time
                }

                myChronometer.start();//start the chronometer
                speedTracker.startRecordingSpeed();
            }
        }.start();
    }
    private void pauseCycling(){
        speedTracker.pauseRecordingSpeed();
        startPause.setText(getResources().getString(R.string.cycle_start_button));//update button to START
        currentChronometerTime = SystemClock.elapsedRealtime() - myChronometer.getBase();//store current Chronometer's time
        myChronometer.stop();//stop chronometer
        topDisplay.setVisibility(View.GONE);//hide display
        pausedLayout.setVisibility(View.VISIBLE);//show paused view
    }

    private void stopCycling(){
        pauseCycling();
        double averageSpeed =  speedTracker.getAverageSpeed();
        double distance = speedTracker.getDistance();
        long time =  currentChronometerTime;
        currentChronometerTime = 0l;//reset chronometer time
        long date = new Date().getTime();
        speedTracker.stopRecordingSpeed();
        String userId = username;
        saveWorkout(averageSpeed, distance, time, date, userId);
    }


    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This should only be called once and when we are sure that mMap is not null.
     */

    private void setUpMap() {
        showMyLocation();
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NEWCASTLE, 14.9f));//zoom to initial point,which will be updated by currentLocation

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                /* hide keyboard */
                removeSearchBarFocus();
            }
        });
    }


    private void showMyLocation(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mMap.setPadding(0,200,0,0);
        mMap.setMyLocationEnabled(true);//enable location
        //mMap.getUiSettings(). setMyLocationButtonEnabled(false);//hide default map location button
        mLocationRequest = LocationRequest.create();// Create the LocationRequest object
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//high accuracy requires more power and time
        mLocationRequest.setInterval(4000);        // 10 seconds, in milliseconds
        mLocationRequest.setFastestInterval(1000); // 1 second, in milliseconds
    }

    private void handleNewLocation(Location location) {
       if(!locationIsSet){
           Log.d(TAG, location.toString());
           double currentLatitude = location.getLatitude();
           double currentLongitude = location.getLongitude();
           currentLocation = new LatLng(currentLatitude,currentLongitude);
           mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
           locationIsSet = true;
       }
        Log.d(TAG, location.toString());

    }

    public TextView generateTextView(String text){
        final TextView rowTextView = new TextView(this);
        rowTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                predictionChosen = true;
                String address = rowTextView.getText().toString();
                search.setText(address);
                scrollLayout.removeAllViews();
                Location myLocation = mMap.getMyLocation();
                currentLocation = new LatLng(myLocation.getLatitude(),myLocation.getLongitude());
                new DirectionsFetcher().execute(address);
            }
        });
        rowTextView.setPadding(15, 15, 15, 15);//set padding
        rowTextView.setBackgroundColor(custom_lightest);//set background color
        rowTextView.setText(text);// set text
        rowTextView.setTextColor(custom_medium);
        return rowTextView;
    }
    private void setColors() {
        background = ContextCompat.getColor(this, R.color.background);
        custom_dark = ContextCompat.getColor(this, R.color.custom_dark);
        custom_medium = ContextCompat.getColor(this, R.color.custom_medium);
        custom_light = ContextCompat.getColor(this, R.color.custom_light);
        custom_lighter = ContextCompat.getColor(this, R.color.custom_lighter);
        custom_lightest = ContextCompat.getColor(this, R.color.custom_lightest);
    }

    public void drawRoute(List<LatLng> listOfGeopoints){
        if(lastRoute!= null){
            lastRoute.remove();
        }

        PolylineOptions myOpt = new PolylineOptions()
                .addAll(listOfGeopoints)
                .visible(true)
                .color(Color.BLUE)
                .width(8f);
        lastRoute = mMap.addPolyline(myOpt);//update last Route to the current route
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point : listOfGeopoints) builder.include(point);//get all points of polyline to compute bounds
        LatLngBounds bounds = builder.build();//compute bounds for the polyline
        int padding = 20; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,padding);//use bounds to focus camera on the polyline

        mMap.animateCamera(cu, 2000, null);

        removeSearchBarFocus();/* hide keyboard */

    }



    /*
    *  I am only going to request location updates when the last location is not known.
    * */
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        else {
            handleNewLocation(location);
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);// Start an Activity that tries to resolve the error
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }
    //every time user is moving around with their phone
    //the location APIs are uptading the location silently in the background
    //and that method is called
    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }



    private void removeSearchBarFocus(){

        if(search.isFocused()){//if search bar is focused
            search.clearFocus();//clear focus
            /*hide keyboard*/
            ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE))
                    .toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
        }
    }

    private void saveWorkout(double averageSpeed ,double distance,long time,long date,String userId){
        statisticsUtils.updateWeeklyStatistics(this,averageSpeed,distance,time);
        statisticsUtils.uploadStatistics(this);
        UploadWorkout uw = new UploadWorkout(this.getApplicationContext()) {
            @Override
            public void onResponseReceived(String response) {
                checkResponse(response);
            }
        };
        uw.uploadWorkoutQuery(userId,time,averageSpeed,distance,date);
    }



    private class DirectionsFetcher extends AsyncTask<String, Integer, String> {

        private List<LatLng> strictPath = new ArrayList<LatLng>();

        protected String doInBackground(String... params) {


            //send directions API query
            //get path for start and end route points in the form of List<LatLng>
            strictPath = GoogleQueries.sendDirectionsQuery(myActivity,currentLocation,params[0]);
            return null;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            drawRoute(strictPath);
        }
    }


    private class ShowPredictions extends AsyncTask<String, Integer, Void> {
        List<String> predictions = new ArrayList<String>();

        protected Void doInBackground(String... params) {

            try{
                Log.d("show predictions", "send query to google ====================================");
                predictions = GoogleQueries.autocomplete(myActivity, params[0]);
                Log.d("show predictions", "results"+predictions.size()+" ====================================");
                for(String prediction: predictions){
                    Log.d("show predictions", prediction);
                }
            }catch(Exception e){
                Log.d("show predictions error", e.getMessage());
                e.printStackTrace();
            }
            return null;

        }
        protected void onPostExecute(Void result) {

            for(String prediction: predictions){
                // add the textview to the linearlayout
                scrollLayout.addView(generateTextView(prediction));
            }
        }
    }



//    private class ConnectToDatabase extends AsyncTask<Workout,Void,Boolean>{
//
//        @Override
//        protected Boolean doInBackground(Workout... params) {
//
//            Workout workout = params[0];
//
//
//            MyConnection conn = new MyConnection();
//            return conn.saveWorkout(workout.getAverageSpeed(),workout.getDistance(),
//                    workout.getTime(),workout.getDate(),workout.getUserId());
//        }
//
//        @Override
//        protected void onPostExecute(Boolean status) {
//            super.onPostExecute(status);
//        }
//    }

}

