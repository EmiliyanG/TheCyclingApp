package com.thecyclingapp.emiliyan.thecyclingapp.activities;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.thecyclingapp.emiliyan.thecyclingapp.R;
import com.thecyclingapp.emiliyan.thecyclingapp.dbTables.User;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.SaveVariables;

public class FindActivity extends AppCompatActivity {

    private User user;
    private Menu myMenu = new Menu();
    private LinearLayout friends,findPeople,findPlaces;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        updateUser();
        //set up the menu
        myMenu.setUpMenu(this, (ImageButton) findViewById(R.id.menuButton),
                (DrawerLayout) findViewById(R.id.drawerLayout),
                (ListView) findViewById(R.id.drawerList), user.getUserId());
        linkComponents();
    }



    private void linkComponents(){
        findPeople = (LinearLayout) findViewById(R.id.findPeople);
        findPlaces = (LinearLayout) findViewById(R.id.findPlaces);
        friends = (LinearLayout) findViewById(R.id.friends);
        findPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFindPeopleActivity();
            }
        });
        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFriendsActivity();
            }
        });
        findPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlacesActivity();
            }
        });
    }
    private void updateUser(){
        user = SaveVariables.getUser(this, SaveVariables.CURRENT_USER);
    }




    private void startMyActivity(Intent intent){
        intent.putExtra(LogInActivity.USERNAME,user.getUserId());
        startActivity(intent);
    }
    private void startPlacesActivity(){startMyActivity(new Intent(this, PlacesActivity.class));}
    private void startFriendsActivity(){startMyActivity(new Intent(this, Followees.class));}
    private void startFindPeopleActivity(){startMyActivity(new Intent(this, FindPeopleActivity.class));}
}
