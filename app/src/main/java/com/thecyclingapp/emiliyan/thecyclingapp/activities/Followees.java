package com.thecyclingapp.emiliyan.thecyclingapp.activities;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.thecyclingapp.emiliyan.thecyclingapp.R;
import com.thecyclingapp.emiliyan.thecyclingapp.dbTables.User;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.SaveVariables;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.UsersListUtils;
/*
* Created by Emiliyan Grigorov
* Date 3 April 2016
*
*
* */
public class Followees extends AppCompatActivity {

    private int offset = 0;
    private LinearLayout mainContainer;
    private UsersListUtils friendsList;
    private User user;//current user
    private Menu myMenu = new Menu();
    private ProgressBar progressBar;
    private Button loadMore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followees);
        linkComponents();
        loadFolloweesList();
    }

    private void linkComponents(){
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mainContainer = (LinearLayout) findViewById(R.id.mainContainer);
        loadMore = (Button) findViewById(R.id.loadMore);
        loadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFolloweesList();
            }
        });


        user = SaveVariables.getUser(this, SaveVariables.CURRENT_USER);//get current user object
        //set up the menu
        myMenu.setUpMenu(this, (ImageButton) findViewById(R.id.menuButton),
                (DrawerLayout) findViewById(R.id.drawerLayout),
                (ListView) findViewById(R.id.drawerList), user.getUserId());

        friendsList = new UsersListUtils(this, mainContainer) {
            @Override
            public void followeesLoaded(boolean hasMoreFollowees) {//method called whenever followees are loaded
                showLoadMoreBtn();//show load more button
                if(!hasMoreFollowees) //if there are no more followees for current user
                    noMoreFollowees();//disable button, show message
            }
            @Override
            public void problemWithConnection() {showLoadMoreBtn();}
        };

    }

    private void loadFolloweesList(){
        hideLoadMoreBtn();
        boolean hasMoreFollowees = friendsList.loadFolloweesList(user,UsersListUtils.LIMIT,offset);
        if(hasMoreFollowees) offset+=UsersListUtils.LIMIT;
    }
    private void hideLoadMoreBtn(){

        if(loadMore.getParent()!=null) mainContainer.removeView(loadMore);
        progressBar.setVisibility(View.VISIBLE);

    }
    private void hideProgressbar(){
        progressBar.setVisibility(View.GONE);
        if(progressBar.getParent()!=null) mainContainer.removeView(progressBar);
    }

    private void showLoadMoreBtn(){
        hideProgressbar();
        if(loadMore.getParent()==null)mainContainer.addView(loadMore);
        if(progressBar.getParent()== null) mainContainer.addView(progressBar);
        loadMore.setVisibility(View.VISIBLE);
    }
    private void noMoreFollowees(){
        loadMore.setEnabled(false);
        loadMore.setText("You don't have other followees");
    }

}
