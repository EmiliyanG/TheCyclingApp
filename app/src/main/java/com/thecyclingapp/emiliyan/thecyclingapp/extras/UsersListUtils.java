package com.thecyclingapp.emiliyan.thecyclingapp.extras;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.thecyclingapp.emiliyan.thecyclingapp.MyConnection;
import com.thecyclingapp.emiliyan.thecyclingapp.R;
import com.thecyclingapp.emiliyan.thecyclingapp.activities.ProfileActivity;
import com.thecyclingapp.emiliyan.thecyclingapp.dbTables.User;
import java.util.ArrayList;

/**
 * Created by Emiliyan on 4/1/2016.
 *
 */
public abstract class UsersListUtils {
    public static final int LIMIT = 20;
    public static final int SIDE_MENU_MAX_WIDTH = 260;
    public static final String SELECTED_USER = "selectedUser";

    private LinearLayout mainContainer;
    private Activity activity;
    private LinearLayout lastMenu = null;

    private int background, custom_dark, custom_medium, custom_light, custom_lighter, custom_lightest;
    private boolean hasMoreFollowees = true;

    public UsersListUtils(Activity activity, LinearLayout container){
        this.activity = activity;
        this.mainContainer = container;

        setColors();
    }

    public void newSideMenuOpened(LinearLayout newMenu){
        if(lastMenu!= null && lastMenu!= newMenu){//close last menu
            lastMenu.getLayoutParams().width = 0;
            lastMenu.requestLayout();
        }
        lastMenu = newMenu;
    }


    private void setColors() {
        background = ContextCompat.getColor(activity, R.color.background);
        custom_dark = ContextCompat.getColor(activity, R.color.custom_dark);
        custom_medium = ContextCompat.getColor(activity, R.color.custom_medium);
        custom_light = ContextCompat.getColor(activity, R.color.custom_light);
        custom_lighter = ContextCompat.getColor(activity, R.color.custom_lighter);
        custom_lightest = ContextCompat.getColor(activity, R.color.custom_lightest);
    }
    public void addUsers(ArrayList<User> users){
        if(users.size() == 0) hasMoreFollowees = false;
        for(User user: users) addUser(user);

    }

    public void addUser(User user){
        FrameLayout container = generateFrameLayout();//create container
        container.addView(generateTextView(user.toString()));// add title

        LinearLayout sideMenu = generateSideMenu();//create side menu layout
        Button viewProfile = generateButton("view profile");
        sideMenu.addView(viewProfile);//add button

        container.addView(sideMenu);//add side menu to container
        //FrameLayout container, LinearLayout sideMenu, Button viewProfile,User user

        ListItem listItem = new ListItem(container, sideMenu, viewProfile, user);

        container.setOnTouchListener(new SwipeDetector(sideMenu, this));

        mainContainer.addView(container);

        ClickListener listener = new ClickListener(listItem);
        container.setOnClickListener(listener);
        viewProfile.setOnClickListener(listener);
    }
    private void startProfileActivity(User user){
        SaveVariables.saveUser(activity, SaveVariables.FOLLOWEE_USER, user);
        Intent intent = new Intent(activity, ProfileActivity.class);
        intent.putExtra(SELECTED_USER, user.getUserId());
        activity.startActivity(intent);
    }

    private TextView generateTextView(String text){
        TextView textView = new TextView(activity);
        textView.setText(text);
        FrameLayout.LayoutParams tvlp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        tvlp.gravity = Gravity.CENTER_VERTICAL;
        textView.setLayoutParams(tvlp);
        textView.setTextSize(20);
        textView.setTextColor(background);
        return textView;
    }
    private LinearLayout generateSideMenu(){
        LinearLayout sideMenu = new LinearLayout(activity);
        FrameLayout.LayoutParams slp = new FrameLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);//width, height
        slp.gravity= Gravity.RIGHT;
        sideMenu.setLayoutParams(slp);

        sideMenu.setOrientation(LinearLayout.HORIZONTAL);
        return  sideMenu;
    }


    private FrameLayout generateFrameLayout(){
        FrameLayout frameLayout = new FrameLayout(activity);
        LinearLayout.LayoutParams flp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);//width, height
        flp.setMargins(0, 6, 0, 0);//left,top,right,bottom
        frameLayout.setLayoutParams(flp);
        frameLayout.setPadding(6, 0, 0, 0);
        frameLayout.setBackgroundColor(custom_lighter);
        return frameLayout;
    }

    private Button generateButton(String buttonText){
        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(SIDE_MENU_MAX_WIDTH, ViewGroup.LayoutParams.WRAP_CONTENT);//width, height
        Button btn = new Button(activity);
        btn.setText(buttonText);
        btn.setLayoutParams(blp);
        btn.setBackgroundColor(custom_light);
        btn.setTextColor(background);
        return btn;
    }

    /**
     * return false if no more followees in database
     * return true if query was sent and waits for an answer
     */
    public boolean loadFolloweesList(User user, int limit, int offset){

        if(hasMoreFollowees){
            new LoadFriendsList(user,limit,offset).execute();
            return true;
        }else {
            followeesLoaded(hasMoreFollowees);
            return false;
        }
    }

    public abstract void followeesLoaded(boolean hasMoreFollowees);
    public abstract void problemWithConnection();

    private class ListItem{
        private FrameLayout container;
        private LinearLayout sideMenu;
        private Button viewProfile;
        private User user;
        public ListItem(FrameLayout container, LinearLayout sideMenu, Button viewProfile,User user){
            this.container = container;
            this.viewProfile = viewProfile;
            this.sideMenu = sideMenu;
            this.user = user;
        }
        public FrameLayout getContainer() {return container;}
        public LinearLayout getSideMenu() {return sideMenu;}
        public Button getViewProfile() {return viewProfile;}
        public User getMyUser() {return user;}
    }



    /*
    * Load workouts from database
    * */
    private class LoadFriendsList extends AsyncTask<Void, Void, ArrayList<User>> {

        private User user;
        private int limit;
        private int offset;
        public LoadFriendsList(User user, int limit, int offset){
            this.user = user;
            this.limit = limit;
            this.offset = offset;
        }

        @Override
        protected ArrayList<User> doInBackground(Void... params) {

            MyConnection conn = new MyConnection();
            return conn.downloadFolloweesList(user, limit, offset);
        }

        @Override
        protected void onPostExecute(ArrayList<User> followeesList) {
            super.onPostExecute(followeesList);
            if(followeesList!= null){
                if(followeesList.size()>0){
                    addUsers(followeesList);
                    if(followeesList.size()<LIMIT ){
                        hasMoreFollowees= false;
                    }

                }else {
                    hasMoreFollowees = false;
                }

                followeesLoaded(hasMoreFollowees);
            }else{
                problemWithConnection();
                Toast.makeText(activity, "No internet connection", Toast.LENGTH_LONG).show();//display message on screen
            }


        }
    }
    private class ClickListener implements View.OnClickListener{
        ListItem li;
        public ClickListener(ListItem li){
            this.li = li;
        }

        @Override
        public void onClick(View v) {
            startProfileActivity(li.getMyUser());
        }
    }
}
