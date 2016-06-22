package com.thecyclingapp.emiliyan.thecyclingapp.activities;

import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.thecyclingapp.emiliyan.thecyclingapp.R;
import com.thecyclingapp.emiliyan.thecyclingapp.dbTables.User;
import com.thecyclingapp.emiliyan.thecyclingapp.dbTables.Workout;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.SaveVariables;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.ViewGenerator;
import com.thecyclingapp.emiliyan.thecyclingapp.json.DownloadFeed;
import com.thecyclingapp.emiliyan.thecyclingapp.json.ResponseStatus;

import java.util.Date;
import java.util.List;

public class FeedActivity extends AppCompatActivity {
    private Activity myActivity = this;
    private static final int FEED_DOWNLOAD_LIMIT = 10;
    private User user;
    private Menu myMenu = new Menu();
    private Button loadMore;
    private LinearLayout scrollLayout;
    private ProgressBar loadFeedProgressBar;
    private boolean hasMoreFeed = true;
    private ViewGenerator viewGenerator;
    private int currentOffset = 0;
    private long currentDate;
    private DownloadFeed downloadFeed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        user = SaveVariables.getUser(this, SaveVariables.CURRENT_USER);

        //set up the menu
        myMenu.setUpMenu(this, (ImageButton) findViewById(R.id.menuButton),
                (DrawerLayout) findViewById(R.id.drawerLayout),
                (ListView) findViewById(R.id.drawerList), user.getUserId());
        linkComponents();
        downloadFeed(user.getUserId());
    }






    private void linkComponents(){
        viewGenerator = new ViewGenerator(this.getApplicationContext());
        scrollLayout = (LinearLayout) findViewById(R.id.scrollLayout);
        loadMore = (Button) findViewById(R.id.loadMore);
        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFeed(user.getUserId());
            }
        });
        loadFeedProgressBar = (ProgressBar) findViewById(R.id.loadProgressBar);
        currentDate = new Date().getTime();
        downloadFeed = new DownloadFeed(this) {
            @Override
            public void onResponseReceived(String response) {
                int status = this.checkResponse(response);

                hideLoadProgressBar();
                if(status== ResponseStatus.SUCCESS){
                    List<SingleFeed> feeds = this.parseJSON(response);


                    for(SingleFeed uw: feeds){

                        scrollLayout.addView(viewGenerator.generateWorkoutView(new Workout(uw.avgSpeed,uw.distance,uw.time,uw.date,uw.username)));

                    }
                    currentOffset += FEED_DOWNLOAD_LIMIT;//remember position for next download


                }else if(status== ResponseStatus.ZERO_ROWS_RETURNED){
                    hasMoreFeed = false;
                }else if(status==ResponseStatus.NO_INTERNET){
                    Toast.makeText(myActivity, "No internet connection", Toast.LENGTH_LONG).show();//display message on screen
                }
                showLoadMoreBtn();
            }
        };
    }
    /*hide button by removing it from its parent
        * method used to ensure that load button stays always on the bottom*/
    private void hideLoadMoreBtn(){

        if(loadMore.getParent()!= null){
            scrollLayout.removeView(loadMore);
            loadFeedProgressBar.setVisibility(View.VISIBLE);
        }
    }
    /*show the button on the bottom of the activity */
    private void showLoadMoreBtn(){
        if(loadMore.getParent() == null){
            scrollLayout.addView(loadMore);
            if(loadFeedProgressBar.getParent()==null) scrollLayout.addView(loadFeedProgressBar);
        }
    }
    private void hideLoadProgressBar(){
        loadFeedProgressBar.setVisibility(View.GONE);
        if(loadFeedProgressBar.getParent() != null){
            scrollLayout.removeView(loadFeedProgressBar);
        }

    }


    private void downloadFeed(String username) {

        if(hasMoreFeed){
            hideLoadMoreBtn();

            downloadFeed.downloadFeed(username,FEED_DOWNLOAD_LIMIT,currentOffset);
        }
        else{
            Toast.makeText(this,"Nothing else to show.",Toast.LENGTH_LONG).show();//display message on screen
        }

    }




}
