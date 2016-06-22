package com.thecyclingapp.emiliyan.thecyclingapp.activities;

import android.app.Activity;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.thecyclingapp.emiliyan.thecyclingapp.R;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.GoogleQueries;

import java.util.ArrayList;
import java.util.List;

public class PlacesActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String STATUS_OK = "OK";/*indicates that no errors occurred; the place was successfully detected and at least one result was returned.*/
    private static final String STATUS_ZERO_RESULTS = "ZERO_RESULTS";/*indicates that the search was successful but returned no results. This may occur if the search was passed a latlng in a remote location.*/
    private static final String STATUS_OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";/*indicates that you are over your quota*/

    private Activity myActivity= this;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;//built-in mechanisms to handle certain errors
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private boolean displayPlaces = true;
    private GoogleApiClient mGoogleApiClient;
    private static final LatLng NEWCASTLE = new LatLng(54.9667, -1.6000);
    private String username;
    private LocationRequest mLocationRequest;
    public static final String TAG = Cycle.class.getSimpleName();
    private Menu myMenu = new Menu();
    private LatLng currentLocation;
    private LinearLayout loading;
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        Bundle extras = getIntent().getExtras();

        setUpMapIfNeeded();
        linkComponents();
        if(extras != null) username = extras.getString(LogInActivity.USERNAME); // retrieve the data using keyName

        //set up the menu
        myMenu.setUpMenu(this, (ImageButton) findViewById(R.id.menuButton),
                (DrawerLayout) findViewById(R.id.drawerLayout),
                (ListView) findViewById(R.id.drawerList), username);
    }


    private void linkComponents(){
        loading = (LinearLayout) findViewById(R.id.loading);
        title = (TextView) findViewById(R.id.title);
        mMap.setPadding(0,60,0,0);
    }

    private void displayStatus(GoogleQueries.PlacesList placesList){

        if(placesList!= null){
            displayPlaces = false;
            System.out.println(placesList.status);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for(GoogleQueries.Place place: placesList.results){
                Log.d("place", place.name);
                LatLng point = place.geometry.location.getLatLng();
                builder.include(point);
                mMap.addMarker(new MarkerOptions()
                        .position(point)
                        .title(place.name));
            }

            //compute bounds for the polyline
            LatLngBounds bounds = builder.build();
            focusMap(bounds);
        }else{
            System.out.println("No internet connection while loading places from PlacesActivity AsyncTask");
        }
        loading.setVisibility(View.GONE);
        updateTitle(placesList);
    }

    /*
    * STATUS_OK
    * STATUS_ZERO_RESULTS
    * STATUS_OVER_QUERY_LIMIT
    *
    * */
    private void updateTitle(GoogleQueries.PlacesList placesList){
        String titleText;
        if(placesList==null)titleText= getResources().getString(R.string.NO_INTERNET_CONNECTION);
        else{
            if(placesList.status.equals(STATUS_OK)) titleText = "Found "+placesList.results.size()+ " places around you";
            else if(placesList.status.equals(STATUS_ZERO_RESULTS)) titleText = getResources().getString(R.string.STATUS_ZERO_RESULTS);
            else titleText = getResources().getString(R.string.STATUS_OVER_QUERY_LIMIT);
        }
        title.setText(titleText);

    }




    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
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
        //zoom to initial point,which will be updated by currentLocation
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NEWCASTLE, 14.9f));

    }


    private void showMyLocation(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //enable location
        mMap.setMyLocationEnabled(true);
        //hide default map location button
        //mMap.getUiSettings(). setMyLocationButtonEnabled(false);


        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)//high accuracy requires more power and time
                .setInterval(20 * 1000)        // 20 seconds, in milliseconds
                .setFastestInterval(10 * 1000); // 1 second, in milliseconds
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        currentLocation = new LatLng(currentLatitude,currentLongitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        showNearByPlaces(currentLocation);
    }
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

    public void focusMap(LatLngBounds bounds){
        int padding = 20; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,padding);//use bounds to focus camera on the polyline
        mMap.animateCamera(cu, 2000, null);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    public void showNearByPlaces(LatLng location){
       if(displayPlaces) {
           new ShowPredictions().execute(location);

       }
    }
    private class ShowPredictions extends AsyncTask<LatLng, Integer, Void> {
        GoogleQueries.PlacesList placesList = null;

        protected Void doInBackground(LatLng... params) {

            try{
                Log.d("show predictions", "send query to google ====================================");
                placesList = GoogleQueries.sendPlacesQuery(myActivity, params[0]);
                Log.d("show predictions", "results"+placesList.results.size()+" ====================================");

            }catch(Exception e){
                Log.d("google places error", " "+e.getMessage());
                e.printStackTrace();
            }
            return null;

        }
        protected void onPostExecute(Void result) {
            displayStatus(placesList);
        }
    }
}
