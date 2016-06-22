package com.thecyclingapp.emiliyan.thecyclingapp.extras;

import android.app.Activity;

import com.thecyclingapp.emiliyan.thecyclingapp.R;

/**
 * Created by Emiliyan on 3/29/2016.
 */
public class RegisterResult {
    public static final int UNKNOWN_ERROR = 0;
    public static final int SUCCESS = 1;
    public static final int DUPLICATE_USERNAME = 2;

    public static final int INVALID_USERNAME_INPUT = 4; // invalid username- user input contains invalid character
    public static final int INVALID_PASSWORD_INPUT = 5; // invalid password - user input contains invalid character
    public static final int INVALID_EMAIL = 6; // invalid email input - invalid character or sequence of characters
    public static final int RETYPED_PASSWORD_NO_MATCH = 7;//retyped password for register activity does not match
    public static final int INVALID_FIRST_NAME = 8;
    public static final int INVALID_LAST_NAME =9;
    public static final int NO_INTERNET = 10;

    public static String getWarningText(Activity activity, int logInResult) {


        String warning;
        switch (logInResult) {
            case SUCCESS:
                warning = activity.getResources().getString(R.string.SUCCESS);
                break;
            case DUPLICATE_USERNAME:
                warning = activity.getResources().getString(R.string.DUPLICATE_USERNAME);
                break;

            case INVALID_USERNAME_INPUT:
                warning = activity.getResources().getString(R.string.INVALID_USERNAME_INPUT);
                break;
            case INVALID_PASSWORD_INPUT:
                warning = activity.getResources().getString(R.string.INVALID_PASSWORD_INPUT);
                break;
            case INVALID_EMAIL:
                warning = activity.getResources().getString(R.string.INVALID_EMAIL);
                break;
            case RETYPED_PASSWORD_NO_MATCH:
                warning = activity.getResources().getString(R.string.RETYPED_PASSWORD_NO_MATCH);
                break;
            case INVALID_FIRST_NAME:
                warning = activity.getResources().getString(R.string.INVALID_FIRST_NAME);
                break;
            case INVALID_LAST_NAME:
                warning = activity.getResources().getString(R.string.INVALID_LAST_NAME);
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
