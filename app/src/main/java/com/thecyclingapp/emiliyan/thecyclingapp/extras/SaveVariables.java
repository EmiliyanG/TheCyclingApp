package com.thecyclingapp.emiliyan.thecyclingapp.extras;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.thecyclingapp.emiliyan.thecyclingapp.dbTables.Statistics;
import com.thecyclingapp.emiliyan.thecyclingapp.dbTables.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Emiliyan on 2/24/2016.
 *
 * Purpose save a single variable of the type: int, long, String
 * Make use of SharedPreferences class > variables will be available even after application is restarted
 */
public class SaveVariables {
    private static final String PREFS_NAME  = "theCyclingApp";
    public static final int DEFAULT_VALUE_INT = 0; //value to return if int does not exist
    public static final String DEFAULT_VALUE_STRING = null;
    public static final long DEFAULT_VALUE_LONG = 0l;
    public static final String USERNAME = "username";
    public static final String CURRENT_USER = "user";
    public static final String FOLLOWEE_USER = "followeeUser";
    private static final String SEPARATOR = "#";
    /*file names*/
    private final static String FILE_NAME_SAVED_STATISTICS = "saved_statistics.txt";

    public static void saveString(Activity activity,String key, String myString){
        SharedPreferences.Editor editor = startSharedPreferencesSession(activity);
        editor.putString(key, myString);
        closeSharedPreferencesSession(editor);
    }
    public static void saveInt(Activity activity,String key, int myInt){
        SharedPreferences.Editor editor = startSharedPreferencesSession(activity);
        editor.putInt(key, myInt);
        closeSharedPreferencesSession(editor);
    }
    public  static void saveLong(Activity activity,String key, long myLong){
        SharedPreferences.Editor editor = startSharedPreferencesSession(activity);
        editor.putLong(key, myLong);
        closeSharedPreferencesSession(editor);
    }

    /*
    * Save user object SharedPreferences class
    * SharedPreferences saves key value pairs
    * The advantage of using sharedPreferences over saving raw files to the internal or external storage is faster performance
    * Because user objects are very simple containing only 3 private variables using ObjectOutputStream() would be inefficient
    * private fields of User are concatenated and converted into a single String
    * concatenation format userId + SEPARATOR + firstName + SEPARATOR + lastName
    * the key parameter is used to identify the saved string
     */
    public static void saveUser(Activity activity, String key, User user){


        if(user!=null){
            String userVariables= user.getUserId()+SEPARATOR+user.getFirstName()+SEPARATOR+user.getLastName();
            SharedPreferences.Editor editor = startSharedPreferencesSession(activity);
            editor.putString(key, userVariables);
            closeSharedPreferencesSession(editor);
        }else{
            Log.d("SharedPreferences","the passed object of type User is null");
        }


    }
    /*
    * return null if no user was previously saved
    * */
    public static User getUser(Activity activity, String key){
        String savedInstance = getSharedPreferences(activity).getString(key, DEFAULT_VALUE_STRING);
        if(savedInstance== null) return null;
        String[] variables = savedInstance.split(SEPARATOR);//split string into username, first name and last name
        return new User(variables[0],variables[1],variables[2]);//return User object
    }

    public static int getInt(Activity activity, String key){
        return getSharedPreferences(activity).getInt(key, DEFAULT_VALUE_INT);
    }
    public static String getString(Activity activity, String key){
        return getSharedPreferences(activity).getString(key, DEFAULT_VALUE_STRING);
    }
    public static long getLong(Activity activity, String key){
        return getSharedPreferences(activity).getLong(key, DEFAULT_VALUE_LONG);
    }



    private static SharedPreferences getSharedPreferences(Activity activity){
        Context ctx   = activity.getApplicationContext();
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static SharedPreferences.Editor startSharedPreferencesSession(Activity activity){
        return getSharedPreferences(activity).edit();
    }
    private static void closeSharedPreferencesSession(SharedPreferences.Editor editor){
        editor.apply();
    }
    public static Statistics getSavedStatistic(Activity activity){
        Statistics statistic = null;
        try{
            ObjectInputStream ois = new ObjectInputStream(activity.openFileInput(FILE_NAME_SAVED_STATISTICS));//open input stream
            statistic = (Statistics) ois.readObject();//get saved modules
            //close stream
            ois.close();
        } catch (Exception e) {
            //File not found
            Log.d("statistics>>",e.getMessage());
        }
        return statistic;
    }


    /*
    * Get saved Statistics object from internal device memory
    * returns null if no Statistics were previously saved
    * */
    //save Statistics object to internal device memory
    //returns true if save was successful
    public static boolean saveStatistics(Activity activity,Statistics statistic){
        try{
            FileOutputStream fos = activity.openFileOutput(FILE_NAME_SAVED_STATISTICS, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fos);
            out.writeObject(statistic);
            out.flush();
            fos.close();
        }catch(Exception e) {
            Log.d("statistics>>", e.getMessage());
            return false;
        }
        return true;
    }







}
