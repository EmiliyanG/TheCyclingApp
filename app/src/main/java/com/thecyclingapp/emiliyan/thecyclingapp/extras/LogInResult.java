package com.thecyclingapp.emiliyan.thecyclingapp.extras;

import android.app.Activity;

import com.thecyclingapp.emiliyan.thecyclingapp.R;

/**
 * Created by Emiliyan on 3/21/2016.
 *
 * class contains constants to determine the possible results from a user log in interaction with the app
 *
 */
public class LogInResult {

    public static final int UNKNOWN_ERROR = 0;
    public static final int SUCCESS = 1; //password and username are valid and present in database
    public static final int VALID_USER_PASSWORD_WRONG = 2; //user exists in database, however password does not match
    public static final int NO_SUCH_USER_FOUND = 3; // no such user was found
    public static final int INVALID_USERNAME_INPUT = 4; // invalid username- user input contains invalid character
    public static final int INVALID_PASSWORD_INPUT = 5; // invalid password - user input contains invalid character
    public static final int NO_INTERNET = 6;

    public static String getWarningText(Activity activity, int logInResult) {


        String warning;
        switch (logInResult) {
            case SUCCESS:
                warning = activity.getResources().getString(R.string.SUCCESS);
                break;
            case VALID_USER_PASSWORD_WRONG:
                warning = activity.getResources().getString(R.string.VALID_USER_PASSWORD_WRONG);
                break;
            case NO_SUCH_USER_FOUND:
                warning = activity.getResources().getString(R.string.NO_SUCH_USER_FOUND);
                break;
            case INVALID_USERNAME_INPUT:
                warning = activity.getResources().getString(R.string.INVALID_USERNAME_INPUT);
                break;
            case INVALID_PASSWORD_INPUT:
                warning = activity.getResources().getString(R.string.INVALID_PASSWORD_INPUT);
                break;
            case NO_INTERNET:
                warning = activity.getResources().getString(R.string.NO_INTERNET_CONNECTION);
                break;

            default:
                warning = "Unknown error occured!";
                break;
        }
        return warning;
    }
}
