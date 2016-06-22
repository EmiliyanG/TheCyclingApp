package com.thecyclingapp.emiliyan.thecyclingapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.thecyclingapp.emiliyan.thecyclingapp.json.ServerConnection;
import com.thecyclingapp.emiliyan.thecyclingapp.dbTables.User;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.LogInResult;
import com.thecyclingapp.emiliyan.thecyclingapp.R;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.ResetPassword;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.SaveVariables;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.Validator;
import com.thecyclingapp.emiliyan.thecyclingapp.json.VerifyUserLogIn;


public class LogInActivity extends AppCompatActivity {
    private static final int WARNING_DURATION = 5000;
    protected static final String USERNAME = "username";
    protected static final String USER = "currentUser";
    private Typeface reglo;
    private EditText logInUsername;
    private EditText logInPassword;
    private TextView logInWarning;
    private TextView haveAccount;
    private Button logInBtn;
    private TextView forgottenPassword;
    private CountDownTimer timer;
    private Activity myActivity = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        String username = SaveVariables.getString(this,SaveVariables.USERNAME);
        if(username!= SaveVariables.DEFAULT_VALUE_STRING) startCyclingActivity(username);

        linkComponents();

    }

    public void startCyclingActivity(String username) {
        Intent intent = new Intent(this, Cycle.class);
        intent.putExtra(USERNAME,username);
        startActivity(intent);
    }

    public void startRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
    public void startResetPasswordActivity() {
        Intent intent = new Intent(this, ResetPassword.class);
        startActivity(intent);
    }

    private void linkComponents(){
       reglo = Typeface.createFromAsset(getAssets(), "fonts/reglo-bold.otf");
        logInUsername = (EditText) findViewById(R.id.logInUsername);
        logInPassword = (EditText) findViewById(R.id.logInPassword);
        logInWarning = (TextView) findViewById(R.id.logInWarning);
        haveAccount = (TextView) findViewById(R.id.haveAccount);
        logInBtn = (Button) findViewById(R.id.logInBtn);
        forgottenPassword = (TextView) findViewById(R.id.forgottenPassword);

        setTypeFace(forgottenPassword);
        setTypeFace(haveAccount);
        setTypeFace(logInUsername);
        setTypeFace(logInPassword);
        setTypeFace(logInWarning);
        setTypeFace(logInBtn);


        haveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegisterActivity();
            }
        });

        forgottenPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startResetPasswordActivity();
            }
        });

        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = logInUsername.getText().toString();
                String password = logInPassword.getText().toString();


                //if password and username match regular expressions
                boolean validPassword = Validator.validatePassword(password);
                boolean validUsername = Validator.validateUsername(username);


                if (validPassword && validUsername) { //if user input for password and username contains valid characters
                    checkDetailsFromDatabase(username, password);
                } else if (!validPassword) {//if password contains invalid character
                    showWarning(LogInResult.INVALID_PASSWORD_INPUT);
                } else {//else if username or both password and username contain invalid characters
                    showWarning(LogInResult.INVALID_USERNAME_INPUT);
                }
            }
        });
    }

    /*
    * set warning text and show it on screen
    * use predefined LogInResult constants as parameter input
    * */
    private void showWarning(int logInResult){
        logInPassword.setText("");
        fireWarningTimer( LogInResult.getWarningText(this,logInResult) );
    }

    /*
    * show warning for WARNING_DURATION amount of time
    * */
    private void fireWarningTimer(String warning){

        logInWarning.setText(warning);
        logInWarning.setVisibility(View.VISIBLE);
        if(timer != null) timer.cancel();//if there is active timer already - cancel it

        timer = new CountDownTimer(WARNING_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                logInWarning.setVisibility(View.GONE);
            }
        }.start();//start timer
    }


    //buttons and editTexts extend textView
    private void setTypeFace(TextView textView){
        textView.setTypeface(reglo);
    }

    private void checkDetailsFromDatabase(String username,final String password){
        final User user = new User();
        VerifyUserLogIn sc = new VerifyUserLogIn(this.getApplicationContext()) {
            @Override
            public void onResponseReceived(String response) {
                int status = checkUsernameAndPassword(response,password,user);

                if(status == LogInResult.SUCCESS){
                    System.out.println(status);
                    SaveVariables.saveString(myActivity,SaveVariables.USERNAME,user.getUserId());
                    SaveVariables.saveUser(myActivity,SaveVariables.CURRENT_USER,user);
                    startCyclingActivity(user.getUserId());//if log in was successful
                }
                else{
                    showWarning(status);//show log in status
                }
            }
        };
        sc.sendUsernameCheckQuery(username);


    }
//    private class ConnectToDatabase extends AsyncTask<String,Void,Integer>{
//        String username;
//
//        @Override
//        protected Integer doInBackground(String... params) {
//
//            username = params[0];
//            String password = params[1];
//
//            MyConnection conn = new MyConnection();
//            return conn.checkUsernameAndPassword(username, password,user);
//        }
//
//        @Override
//        protected void onPostExecute(Integer status) {
//            super.onPostExecute(status);
//            showWarning(status);//show log in status
//
//            if(status.equals(LogInResult.SUCCESS)){
//                SaveVariables.saveString(myActivity,SaveVariables.USERNAME,username);
//                SaveVariables.saveUser(myActivity,SaveVariables.CURRENT_USER,user);
//                startCyclingActivity(username);//if log in was successful
//            }
//        }
//    }
}
