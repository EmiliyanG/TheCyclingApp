package com.thecyclingapp.emiliyan.thecyclingapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.thecyclingapp.emiliyan.thecyclingapp.dbTables.User;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.LogInResult;
import com.thecyclingapp.emiliyan.thecyclingapp.MyConnection;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.Passwords;
import com.thecyclingapp.emiliyan.thecyclingapp.R;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.RegisterResult;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.SaveVariables;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.Validator;
import com.thecyclingapp.emiliyan.thecyclingapp.json.AddNewUser;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {

    private static final int WARNING_DURATION = 10000;//in milliseconds

    private Button cancel;
    private Button register;
    private EditText username,password,typePasswordAgain,email,firstName, lastName;

    private TextView warning;
    private CountDownTimer timer;
    private Activity myActivity = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        linkComponents();
    }





    public void startCyclingActivity() {
        Intent intent = new Intent(this, Cycle.class);
        startActivity(intent);
    }

    public void startLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

    private void linkComponents(){
        cancel = (Button) findViewById(R.id.cancel);
        register = (Button) findViewById(R.id.register);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        typePasswordAgain = (EditText) findViewById(R.id.typePasswordAgain);
        email = (EditText) findViewById(R.id.email);
        warning = (TextView) findViewById(R.id.warning);
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogInActivity();
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameText = username.getText().toString();
                String passwordText = password.getText().toString();
                String typePasswordAgainText = typePasswordAgain.getText().toString();
                String emailText = email.getText().toString();
                String firstNameText = firstName.getText().toString();
                String lastNameText = lastName.getText().toString();
                boolean checkUsername = Validator.validateUsername(usernameText);
                boolean checkPassword = Validator.validatePassword(passwordText);
                boolean checkTypePassAgain = passwordText.equals(typePasswordAgainText);
                boolean checkEmail = Validator.validateEmail(emailText);
                boolean validateFirstName = Validator.validateName(firstNameText);
                boolean validateLastName = Validator.validateName(lastNameText);

                ArrayList<Integer> errors = new ArrayList<Integer>();

                if(!checkUsername)errors.add(RegisterResult.INVALID_USERNAME_INPUT);
                if(!checkPassword) errors.add(RegisterResult.INVALID_PASSWORD_INPUT);
                if(!checkTypePassAgain)errors.add(RegisterResult.RETYPED_PASSWORD_NO_MATCH);
                if(!checkEmail)errors.add(RegisterResult.INVALID_EMAIL);
                if(!validateFirstName) errors.add(RegisterResult.INVALID_FIRST_NAME);
                if(!validateLastName) errors.add(RegisterResult.INVALID_LAST_NAME);

                if(errors.size()==0){
                    addUser(usernameText, passwordText, emailText,firstNameText,lastNameText);
                    System.out.println("registration successful");
                }else{
                    String errorsText = "";
                   for(int error:errors){
                       errorsText+= LogInResult.getWarningText(myActivity, error)+"\n";
                   }
                    fireWarningTimer(errorsText);
                }
            }
        });
    }

    private void showWarning(int logInResult){
        password.setText("");
        typePasswordAgain.setText("");
        fireWarningTimer(RegisterResult.getWarningText(this, logInResult));
    }

    /*
    * show warning for WARNING_DURATION amount of time
    * */
    private void fireWarningTimer(String warningText){

        warning.setText(warningText);
        warning.setVisibility(View.VISIBLE);
        if(timer != null) timer.cancel();//if there is active timer already - cancel it

        timer = new CountDownTimer(WARNING_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                warning.setVisibility(View.GONE);
            }
        }.start();//start timer
    }

    private void addUser(final String username, String password, String email, final String firstNameText, final String lastNameText){
        AddNewUser addNewUser = new AddNewUser(this.getApplicationContext()) {
            @Override
            public void onResponseReceived(String response) {

                int status = checkResponse(response);
                System.out.println(response);
                if(status == RegisterResult.SUCCESS){

                    showWarning(RegisterResult.SUCCESS);
                    User user = new User();
                    user.setLastName(lastNameText);
                    user.setFirstName(firstNameText);
                    user.setUserId(username);
                    SaveVariables.saveString(myActivity, SaveVariables.USERNAME, user.getUserId());
                    SaveVariables.saveUser(myActivity, SaveVariables.CURRENT_USER, user);
                    startCyclingActivity();//if registering was successful
                }
                else showWarning(status);
            }
        };
        addNewUser.sendUsernameCheckQuery(username, password, email, firstNameText, lastNameText);

    }


//    private class ConnectToDatabase extends AsyncTask<String,Void,Integer> {
//
//        @Override
//        protected Integer doInBackground(String... params) {
//            String username = params[0];
//            String password = params[1];
//            String email = params[2];
//            String firstNameText = params[3];
//            String lastNameText = params[4];
//            Passwords passwordUtil = new Passwords();
//            String salt = passwordUtil.generateRandomSalt();
//            password = passwordUtil.encryptPassword(password,salt);
//
//            MyConnection conn = new MyConnection();
//            return conn.addUser(username, password,salt, email,firstNameText,lastNameText);
//        }
//
//        @Override
//        protected void onPostExecute(Integer status) {
//            super.onPostExecute(status);
//
//
//
//
//
//        }
//    }
}
