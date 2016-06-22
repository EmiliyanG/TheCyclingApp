package com.thecyclingapp.emiliyan.thecyclingapp.extras;

import android.app.Activity;
import android.gesture.Prediction;

import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.Key;
import com.google.maps.android.PolyUtil;
import com.thecyclingapp.emiliyan.thecyclingapp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Emiliyan on 2/19/2016.
 */
public class GoogleQueries {

    static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
    static final JsonFactory JSON_FACTORY = new JacksonFactory();
    private static final int RADIUS = 5000;
    public static List<String> autocomplete(Activity activity, String input) {
        List<String> resultList = new ArrayList<String>();

        try {

            HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                    @Override
                    public void initialize(HttpRequest request) {
                        request.setParser(new JsonObjectParser(JSON_FACTORY));
                    }
                }
            );
            System.out.println("input for query " + input);
            GenericUrl url = new GenericUrl(activity.getResources().getString(R.string.google_autocompletion_api_query_path));
            url.put("input", input);
            url.put("key", activity.getResources().getString(R.string.google_places_key));
            url.put("sensor",false);
            System.out.println(url.toString());

            HttpRequest request = requestFactory.buildGetRequest(url);
            HttpResponse httpResponse = request.execute();
            PlacesResult directionsResult = httpResponse.parseAs(PlacesResult.class);
            System.out.println("Status: " + httpResponse.getStatusCode() + " status message: " + httpResponse.getStatusMessage());
            List<Prediction> predictions = directionsResult.predictions;
            System.out.println("prediction size from googleQuery "+ predictions.size());
            for (Prediction prediction : predictions) {
                resultList.add(prediction.description);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return resultList;
    }



    //method should be used on a separate thread (AsynqTask )
    public static List<LatLng> sendDirectionsQuery(Activity activity, LatLng start, String end){
        List<LatLng> strictPath = new ArrayList<LatLng>();
        try {
            HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                @Override
                public void initialize(com.google.api.client.http.HttpRequest request) throws IOException {
                    request.setParser(new JsonObjectParser(JSON_FACTORY));
                }
            });

            //build request
            HttpRequest request = requestFactory.buildGetRequest(GoogleQueries.getGoogleDirectionsQuery(activity,start, end));

            //get response
            HttpResponse httpResponse = request.execute();

            //parse response and convert it to java objects
            DirectionsResult directionsResult = httpResponse.parseAs(DirectionsResult.class);

            //by default Directions API always returns a single route if such exists
            //each route consists of multiple legs(sub-routes)
            //get all legs for the given route
            List<Leg> legs = directionsResult.routes.get(0).legs;

            for(Leg leg:legs){
                //for each leg get all steps
                List<Step> steps = leg.steps;
                for(int i = 0;i<steps.size();i++){
                    //get the polyline for each step and decode it
                    //after that append it to the overall path
                    strictPath.addAll( PolyUtil.decode(steps.get(i).polyline.points));
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return strictPath;
    }

    //generate google Directions API query based on given route start and end points
    //mode set to bicycling
    //avoid highways
    public static GenericUrl getGoogleDirectionsQuery(Activity activity,LatLng startPoint, String endPoint){

        GenericUrl url = new GenericUrl(activity.getResources().getString(R.string.directions_api_query_path));
        url.set("origin", ""+startPoint.latitude+","+startPoint.longitude);
        url.set("destination", ""+endPoint);
        url.set("avoid", "highways");
        url.set("mode", "bicycling");

        url.set("sensor", true);
        url.set("key", activity.getResources().getString(R.string.google_directions_key));
        return url;
    }



    //method should be used on a separate thread (AsynqTask )
    public static PlacesList sendPlacesQuery(Activity activity, LatLng location){
        PlacesList placesList= null;
        try {
            HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                @Override
                public void initialize(com.google.api.client.http.HttpRequest request) throws IOException {
                    request.setParser(new JsonObjectParser(JSON_FACTORY));
                }
            });
            //build request
            HttpRequest request = requestFactory.buildGetRequest(getGooglePlacesQuery(activity, location));
            //get response
            HttpResponse httpResponse = request.execute();

            //parse response and convert it to java objects
            placesList = httpResponse.parseAs(PlacesList.class);

        }catch(Exception e){
            e.printStackTrace();
        }
        return placesList;
    }

    private static GenericUrl getGooglePlacesQuery(Activity activity, LatLng location) {
        GenericUrl url = new GenericUrl(activity.getResources().getString(R.string.google_places_api_query_path));
        url.set("location", location.latitude + "," + location.longitude);
        url.put("radius", RADIUS);
        url.put("type","bicycle_store");
        url.put("sensor", true);
        url.set("key", activity.getResources().getString(R.string.google_directions_key));
        return url;
    }





    /*
    * These classes are used to convert data returned from Google Directions API query to
    * actual Java classes
    *
    * */
    public static class DirectionsResult {
        @Key("routes")
        public List<Route> routes;
    }

    public static class Route {
        @Key("legs")
        public List<Leg> legs;
    }

    public static class Leg {
        @Key("steps")
        public List<Step> steps;
    }

    public static class Step {
        @Key("polyline")
        public Polyline polyline;
    }
    public static class Polyline {
        @Key("points")
        public String points;
    }

    /*
    * Google autocompletion
    * */
    public static class PlacesResult {

        @Key("predictions")
        public List<Prediction> predictions;

    }

    public static class Prediction {
        @Key("description")
        public String description;

        @Key("id")
        public String id;

    }
    /*
    * Google places
    * */
    public static class PlacesList {
        @Key("status")
        public String status;

        @Key("results")
        public List<Place> results;

    }

    public static class Place {
        @Key("id")
        public String id;
        @Key("name")
        public String name;
        @Key("reference")
        public String reference;
        @Key("geometry")
        public Geometry geometry;
        @Override
        public String toString() {
            return name + " - " + id + " - " + reference;
        }
    }

    public static class Location{
        @Key("lat")
        public double lat;
        @Key("lng")
        public double lng;
        public LatLng getLatLng(){
            return new LatLng(lat,lng);
        }
    }
    public static class Geometry{
        @Key("location")
        public Location location;
    }
}
