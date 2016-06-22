package com.thecyclingapp.emiliyan.thecyclingapp.json;

import android.content.Context;
import com.google.api.client.util.Key;
import com.google.gson.Gson;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.LogInResult;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.Passwords;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Emiliyan on 4/16/2016.
 */
public abstract class VerifyUserLogIn extends ServerConnection {
    private static final String PASSWORD = "simple";
    private static final String CHECK_PASS_URL = "http://thecyclingapp.co.uk/checkUserPass.php";

    public VerifyUserLogIn(Context c) {
        super(c);
    }


    @Override
    public abstract void onResponseReceived(String response);


    //return true if password and username pair is correct and exists in the database
    public int checkUsernameAndPassword(String response,String password, com.thecyclingapp.emiliyan.thecyclingapp.dbTables.User user) {
        System.out.println(response);
        if(response.equals(NO_INTERNET)) return LogInResult.NO_INTERNET;

        if(response==null||response.length()==0){
            return LogInResult.UNKNOWN_ERROR;
        }
        if(response.equals(QUERY_RETURNED_ZERO_ROWS)){
            return LogInResult.NO_SUCH_USER_FOUND;// if username is not found
        }
        //convert the json string back to object
        Users users = new Gson().fromJson(response, Users.class);
        if(users.users!=null && users.users.size() == 1){//if JSON to Users.class successful and there is only one match from query result
            User match = users.users.get(0);
            Passwords passwords = new Passwords();
            boolean passwordMatch = passwords.checkPassword(password, match.password, match.salt);
            if(passwordMatch){
                user.setUserId(match.username);
                user.setFirstName(match.firstname);
                user.setLastName(match.lastname);
                return LogInResult.SUCCESS;
            }
            else return LogInResult.VALID_USER_PASSWORD_WRONG;

        }else{
            return LogInResult.NO_SUCH_USER_FOUND;// if username is not found
        }
    }


    public void sendUsernameCheckQuery(String username){
        Map<String,String> params = new HashMap<String, String>();
        params.put("permission",PASSWORD);
        params.put("username",username);
        sendPost(CHECK_PASS_URL, params);
    }


    /**
     * Created by Emiliyan on 4/16/2016.
     */

    public static class Users{
        @Key("users")
        public List<User> users;
    }
    public static class User {
        @Key("username")
        public String username;
        @Key("password")
        public String password;
        @Key("salt")
        public String salt;
        @Key("email")
        public String email;
        @Key("firstname")
        public String firstname;
        @Key("lastname")
        public String lastname;
        @Override
        public String toString() {
            return firstname + " " + lastname;
        }
    }
}
