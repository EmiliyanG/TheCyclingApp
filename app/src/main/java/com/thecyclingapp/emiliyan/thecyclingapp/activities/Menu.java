package com.thecyclingapp.emiliyan.thecyclingapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.thecyclingapp.emiliyan.thecyclingapp.R;
import com.thecyclingapp.emiliyan.thecyclingapp.activities.Cycle;
import com.thecyclingapp.emiliyan.thecyclingapp.activities.LogInActivity;
import com.thecyclingapp.emiliyan.thecyclingapp.activities.ProfileActivity;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.SaveVariables;

/**
 * Created by Emiliyan on 2/19/2016.
 */
public class Menu implements AdapterView.OnItemClickListener {

    private String[] menuItems;
    private Activity myActivity;
    private ImageButton  menuBtn;
    private DrawerLayout drawerLayout;
    private ListView listView;
    private String username,profile,cycle,logOut,find,feed;


    public void setUpMenu(Activity activity, ImageButton btn, final DrawerLayout drawerLayout, final ListView listView,String username){
        myActivity = activity;
        this.menuBtn= btn;
        this.username= username;
        this.drawerLayout = drawerLayout;
        this.listView = listView;
        profile = activity.getResources().getString(R.string.profile);
        cycle = activity.getResources().getString(R.string.cycle);
        logOut = activity.getResources().getString(R.string.log_out);
        find = activity.getResources().getString(R.string.find);
        feed = activity.getResources().getString(R.string.news_feed);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            private boolean isDrawerOpen= false;
            @Override
            public void onClick(View v) {
                //Opens the Drawer
                if(isDrawerOpen){
                    drawerLayout.closeDrawer(listView);
                    isDrawerOpen = false;
                }else{
                    drawerLayout.openDrawer(listView);
                    isDrawerOpen = true;
                }
            }
        });
        menuItems = myActivity.getResources().getStringArray(R.array.menu_items);
        listView.setAdapter(new ArrayAdapter<String>(myActivity,R.layout.menu_items_layout,menuItems));

        listView.setOnItemClickListener(this);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if(menuItems[position].equals(profile)) startProfileActivity();
        else if(menuItems[position].equals(cycle))startCycleActivity();
        else if(menuItems[position].equals(find)) startFindActivity();
        else if(menuItems[position].equals(feed)) startFeedActivity();
        else if(menuItems[position].equals(logOut)) {
            SaveVariables.saveString(myActivity,SaveVariables.USERNAME,SaveVariables.DEFAULT_VALUE_STRING);
            startLogInActivity();
        }
        else Toast.makeText(myActivity, menuItems[position] + " was selected ", Toast.LENGTH_LONG).show();

    }




    public DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }

    public ImageButton getMenuBtn() {
        return menuBtn;
    }

    public ListView getListView() {
        return listView;
    }

    private void startActivity(Intent intent){
        intent.putExtra(LogInActivity.USERNAME,username);
        myActivity.startActivity(intent);
        myActivity.finish();
    }

    private void startProfileActivity(){startActivity(new Intent(myActivity, ProfileActivity.class));}
    private void startCycleActivity(){startActivity(new Intent(myActivity, Cycle.class));}
    private void startLogInActivity(){startActivity(new Intent(myActivity,LogInActivity.class));}
    private void startFindActivity(){startActivity(new Intent(myActivity,FindActivity.class));}
    private void startFeedActivity() {startActivity(new Intent(myActivity,FeedActivity.class));}
}
