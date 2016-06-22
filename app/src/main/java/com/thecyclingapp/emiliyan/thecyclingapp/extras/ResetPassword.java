package com.thecyclingapp.emiliyan.thecyclingapp.extras;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.thecyclingapp.emiliyan.thecyclingapp.R;
import com.thecyclingapp.emiliyan.thecyclingapp.activities.LogInActivity;

public class ResetPassword extends ActionBarActivity {
    private Button reset;
    private Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        linkComponents();
    }

    public void startLogInActivity() {
        Intent intent = new Intent(this, LogInActivity.class);
        startActivity(intent);
    }

    private void linkComponents(){
        reset = (Button) findViewById(R.id.reset);
        cancel = (Button) findViewById(R.id.cancel);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SendEmail().execute();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLogInActivity();
                finish();
            }
        });
    }


    public class SendEmail extends AsyncTask<String, Integer, Void> {
        private boolean messageSent = false;


        @Override
        protected Void doInBackground(String... params) {


            return null;
        }

        protected void onPostExecute(Void s) {
            super.onPostExecute(s);
            if( messageSent ) System.out.println("Check your mail");
        }

    }
}
