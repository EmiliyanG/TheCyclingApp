package com.thecyclingapp.emiliyan.thecyclingapp.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.thecyclingapp.emiliyan.thecyclingapp.dbTables.Statistics;
import com.thecyclingapp.emiliyan.thecyclingapp.dbTables.StatisticsUtils;
import com.thecyclingapp.emiliyan.thecyclingapp.dbTables.User;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.DateTimeUtils;
import com.thecyclingapp.emiliyan.thecyclingapp.MyConnection;
import com.thecyclingapp.emiliyan.thecyclingapp.R;
import com.thecyclingapp.emiliyan.thecyclingapp.dbTables.Workout;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.SaveVariables;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.TextFormat;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.UsersListUtils;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.ViewGenerator;
import com.thecyclingapp.emiliyan.thecyclingapp.json.DownloadWorkouts;
import com.thecyclingapp.emiliyan.thecyclingapp.json.ResponseStatus;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    protected static final int WORKOUT_DOWNLOAD_LIMIT = 5;
    private User user;
    private User followee;
    private int currentOffset = 0;
    private boolean gotFirstWorkout = false;
    private long date= 0l;
    private boolean hasMoreWorkouts = true;
    private TextView averageSpeed, totalSpeed, distanceThisWeek, totalDistance,
            hoursThisWeek, totalHours, userName;

    private Button loadMore;
    private String username;
    private TextView title;
    private LinearLayout scrollLayout;
    private ProgressBar loadWorkoutsProgressBar;
    private Activity myActivity = this;
    private Menu myMenu = new Menu();
    private StatisticsUtils statisticsUtils;
    private long currentDate;
    private ViewGenerator viewGenerator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        linkComponents();
        displayStatistics();

        //set up the menu
        myMenu.setUpMenu(this, (ImageButton) findViewById(R.id.menuButton),
                (DrawerLayout) findViewById(R.id.drawerLayout),
                (ListView) findViewById(R.id.drawerList), user.getUserId());
        downloadWorkouts(username);
    }


    private void linkComponents() {
        title = (TextView) findViewById(R.id.title);
        updateUserAndUsername();

        averageSpeed = (TextView) findViewById(R.id.averageSpeed);
        totalSpeed = (TextView) findViewById(R.id.totalSpeed);
        distanceThisWeek = (TextView) findViewById(R.id.distanceThisWeek);
        totalDistance = (TextView) findViewById(R.id.totalDistance);
        hoursThisWeek = (TextView) findViewById(R.id.hoursThisWeek);
        totalHours = (TextView) findViewById(R.id.totalHours);
        userName = (TextView) findViewById(R.id.userName);
        scrollLayout = (LinearLayout) findViewById(R.id.scrollLayout);
        loadMore = (Button) findViewById(R.id.loadMore);
        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadWorkouts(username);
            }
        });
        loadWorkoutsProgressBar = (ProgressBar) findViewById(R.id.loadWorkoutsProgressBar);
        currentDate = new Date().getTime();
        if(user.getUserId().equals(username)) userName.setText(user.toString());
        else userName.setText(followee.toString());
        viewGenerator = new ViewGenerator(this.getApplicationContext());
        statisticsUtils = new StatisticsUtils() {
            @Override
            public void onStatisticsDownloaded(Statistics statistics) {
                if(statistics!= null) displayStatisticsForUser(statistics);//if statistics are null there was a problem with connection
            }
        };
    }
    private void updateUserAndUsername(){
        user = SaveVariables.getUser(this, SaveVariables.CURRENT_USER);
        followee = SaveVariables.getUser(this,SaveVariables.FOLLOWEE_USER);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            username = extras.getString(LogInActivity.USERNAME); // retrieve the data using keyName
            if(username == null){

                username = extras.getString(UsersListUtils.SELECTED_USER);
                title.setText(followee.getFirstName()+"'s profile");

            }else{
                title.setText("Your profile");
            }

            Log.d("username correct", username);
        }
    }

    private void displayStatistics(){
        Statistics statistics = SaveVariables.getSavedStatistic(this);
        if(statistics!= null && statistics.getUserId().equals(username)){//if there are saved statistics which belong to current user
            displayStatisticsForUser(statistics);
        }else statisticsUtils.downloadStatistics(this,username);//else download statistics from database once downloaded abstract method onStatisticsDownloaded will be called

    }


    private void displayStatisticsForUser(Statistics statistics){
        //this week
        averageSpeed.setText(TextFormat.formatAverageSpeed(statistics.getWeeklyAvgSpeed()));
        distanceThisWeek.setText(TextFormat.formatDistance(statistics.getWeeklyDistance()));
        hoursThisWeek.setText(TextFormat.formatTime(statistics.getWeeklyTime()));

        //total
        totalSpeed.setText(TextFormat.formatAverageSpeed(statistics.getTotalAvgSpeed()));
        totalDistance.setText(TextFormat.formatDistance(statistics.getTotalDistance()));
        totalHours.setText(TextFormat.formatTime(statistics.getTotalTime()));
    }



    /*hide button by removing it from its parent
    * method used to ensure that load button stays always on the bottom*/
    private void hideLoadMoreBtn(){

        if(loadMore.getParent()!= null){
            scrollLayout.removeView(loadMore);
            loadWorkoutsProgressBar.setVisibility(View.VISIBLE);
        }
    }
    /*show the button on the bottom of the activity */
    private void showLoadMoreBtn(){
        if(loadMore.getParent() == null){
            scrollLayout.addView(loadMore);
            if(loadWorkoutsProgressBar.getParent()==null) scrollLayout.addView(loadWorkoutsProgressBar);
        }
    }
    private void hideLoadWorkoutsProgressBar(){
        loadWorkoutsProgressBar.setVisibility(View.GONE);
        if(loadWorkoutsProgressBar.getParent() != null){
            scrollLayout.removeView(loadWorkoutsProgressBar);
        }

    }

    private void downloadWorkouts(String username) {
        if(hasMoreWorkouts){
            hideLoadMoreBtn();
            DownloadWorkouts dw = new DownloadWorkouts(this.getApplicationContext()) {
                @Override
                public void onResponseReceived(String response) {
                    int status = this.checkResponse(response);
                    hideLoadWorkoutsProgressBar();
                    if(status== ResponseStatus.SUCCESS){
                        ArrayList<Workout> workouts = this.parseJSON(response);

                        for(Workout temp: workouts){
                            scrollLayout.addView(viewGenerator.generateWorkoutView(temp));
                        }
                        currentOffset += WORKOUT_DOWNLOAD_LIMIT;//remember position for next download


                    }else if(status== ResponseStatus.ZERO_ROWS_RETURNED){


                        hasMoreWorkouts = false;

                    }else if(status==ResponseStatus.NO_INTERNET){
                        Toast.makeText(myActivity,"No internet connection",Toast.LENGTH_LONG).show();//display message on screen

                    }
                    showLoadMoreBtn();
                }
            };

            dw.downloadWorkouts(username,WORKOUT_DOWNLOAD_LIMIT,currentOffset,currentDate);
        }
        else{
            Toast.makeText(myActivity,"You don't have other workouts",Toast.LENGTH_LONG).show();//display message on screen
        }

    }

}



