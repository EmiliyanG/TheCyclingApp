package com.thecyclingapp.emiliyan.thecyclingapp.json;

import android.content.Context;

import com.google.api.client.util.Key;
import com.google.gson.Gson;
import com.thecyclingapp.emiliyan.thecyclingapp.dbTables.User;
import com.thecyclingapp.emiliyan.thecyclingapp.dbTables.Workout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Emiliyan on 4/17/2016.
 */
public abstract class SearchForUsers extends ServerConnection {


    private static final String SEARCH_FOR_USERS_URL = "http://thecyclingapp.co.uk/searchName.php";
    private static final String PASSWORD = "simple";

    protected SearchForUsers(Context c) {
        super(c);
    }

    @Override
    public abstract void onResponseReceived(String response);

    public int checkResponse(String response) {
        if(response.equals(NO_INTERNET)) return ResponseStatus.NO_INTERNET;
        else if(response.contains(QUERY_RETURNED_ZERO_ROWS)) return ResponseStatus.ZERO_ROWS_RETURNED;
        else if(response==null || response.length() == 0) return ResponseStatus.UNKNOWN_ERROR;
        else return ResponseStatus.SUCCESS;
    }



    public ArrayList<User> parseJSON(String response){
        ArrayList<User> temp = null;



        Users users = new Gson().fromJson(response, Users.class);

        if(users.users!=null && users.users.size() > 0){//if JSON to Workouts.class successful and there is only one match from query result
            temp = new ArrayList<>();
            for(DownloadedUsers du: users.users){
                temp.add(new User(du.username,du.firstname,du.lastname));
            }
        }

        return temp;

    }


    public void searchForUsers(String pattern){

        Map<String,String> params = new HashMap<String, String>();
        params.put("permission",PASSWORD);
        params.put("pattern",pattern);
        sendPost(SEARCH_FOR_USERS_URL, params);
    }

    public static class Users{
        @Key("users")
        public List<DownloadedUsers> users;
    }
    public static class DownloadedUsers {

        @Key("user_id")
        public String username;
        @Key("firstname")
        public String firstname;
        @Key("lastname")
        public String lastname;
    }





}
