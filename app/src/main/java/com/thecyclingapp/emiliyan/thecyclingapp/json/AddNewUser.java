package com.thecyclingapp.emiliyan.thecyclingapp.json;

import android.content.Context;

import com.google.gson.Gson;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.LogInResult;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.Passwords;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.RegisterResult;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Emiliyan on 4/16/2016.
 */
public abstract class AddNewUser extends ServerConnection {
    private static final String INSERT_USER_SUCCESSFUL = "success";
    private static final String PASSWORD = "simple";
    private static final String ADD_NEW_USER_URL = "http://thecyclingapp.co.uk/InsertUser.php";

    protected AddNewUser(Context c) {
        super(c);
    }

    public int checkResponse(String response) {
        System.out.println("message came from AddNewUser checkresponse"+response);
        if(response.equals(NO_INTERNET)) return RegisterResult.NO_INTERNET;
        else if(response.contains(DUPLICATE_ENTRY)) return RegisterResult.DUPLICATE_USERNAME;
        else if(response.equals(INSERT_USER_SUCCESSFUL)) return RegisterResult.SUCCESS;
        else return RegisterResult.UNKNOWN_ERROR;
    }

    @Override
    public abstract void onResponseReceived(String response);

    public void sendUsernameCheckQuery(String username, String password, String email,String firstName,String lastName){
        Passwords passwordUtil = new Passwords();
        String salt = passwordUtil.generateRandomSalt();//generate salt
        password = passwordUtil.encryptPassword(password,salt);//encrypt password with the salt


        Map<String,String> params = new HashMap<String, String>();
        params.put("permission",PASSWORD);
        params.put("username",username);
        params.put("password",password);
        params.put("salt",salt);
        params.put("email",email);
        params.put("firstname",firstName);
        params.put("lastname",lastName);
        sendPost(ADD_NEW_USER_URL, params);
    }
}
