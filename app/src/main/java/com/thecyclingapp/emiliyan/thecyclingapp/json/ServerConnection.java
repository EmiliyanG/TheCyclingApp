package com.thecyclingapp.emiliyan.thecyclingapp.json;
import android.content.Context;
import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Emiliyan on 4/16/2016.
 * Purpose: The class makes use of the Volley library to send a HTTP POST request to php file
 * The php file located at server host http://thecyclingapp.co.uk makes MySQL database manipulations
 * and return JSON response corresponding to the result from the MySQL query
 *
 * The internet connections made are thread safe as the class StringRequest already uses AsyncTask
 * the method onResponseReceived is called when the AsyncTask is executed
 * the class is general and should not be used directly!
 * Additional classes are provided in the package to extend the functionality of the class and deal with
 * requests/responses to specific php files
 *
 */
public abstract class ServerConnection {
    /*
    * NO_INTERNET is passed to onResponseReceived(...) if there is no internet connection available
    *
    * */
    public static final String NO_INTERNET = "noInternet";
    public static final String DUPLICATE_ENTRY = "Duplicate entry";

    /*
    * QUERY_RETURNED_ZERO_ROWS is returned
    * by the server and passed to onResponseReceived(...) if the MySQL query was successfully executed
    * however no results were obtained
    * */
    public static final String QUERY_RETURNED_ZERO_ROWS = "no rows";




    private static final String showUrl = "http://thecyclingapp.co.uk/showUsers.php";

    private RequestQueue requestQueue;

    protected ServerConnection(Context c) {
        requestQueue = Volley.newRequestQueue(c);
    }
    /*
    * method is called when the http POST request is executed and a response is received
    *
    * */
    public abstract void onResponseReceived(String response);

    /*
    * the method sends http POST request
    * the url should point to a specific php file location located on host http://thecyclingapp.co.uk
    * params should always be provided as each php file is protected by a password with identifier "permission"
    * params are added to the body of the post method and are encoded for additional security
    * any request to the php files without the specified "permission" will be denied due to security reasons
    * */
    protected void sendPost(String url,final Map<String,String> params){
        System.out.println("send post");
        StringRequest sr = new StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        onResponseReceived(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(error instanceof NoConnectionError) {
                            onResponseReceived(NO_INTERNET);
                        }else{
                            error.printStackTrace();
                        }


                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        requestQueue.add(sr);
    }
}
