package com.thecyclingapp.emiliyan.thecyclingapp.activities;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.thecyclingapp.emiliyan.thecyclingapp.R;
import com.thecyclingapp.emiliyan.thecyclingapp.dbTables.User;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.SaveVariables;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.UsersListUtils;
import com.thecyclingapp.emiliyan.thecyclingapp.json.ResponseStatus;
import com.thecyclingapp.emiliyan.thecyclingapp.json.SearchForUsers;

import java.util.ArrayList;

public class FindPeopleActivity extends AppCompatActivity {
    private static final int DEFAULT_TITLE = -1;
    private LinearLayout mainContainer;
    private UsersListUtils listOfPeople;
    private User user;//current user
    private Menu myMenu = new Menu();
    private ProgressBar progressBar;
    private SearchForUsers searchForUsers;
    private EditText search;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_people);
        linkComponents();
    }

    private void linkComponents(){
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mainContainer = (LinearLayout) findViewById(R.id.mainContainer);
        title = (TextView) findViewById(R.id.title);

        search = (EditText) findViewById(R.id.searchOtherUsers);

        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //typed more than 4 characters in the search bar
                if(s.length()>2){
                    searchForUsers(s.toString());
                }else{
                    updateTitle(DEFAULT_TITLE,0);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }
            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub

            }
        });


        user = SaveVariables.getUser(this, SaveVariables.CURRENT_USER);//get current user object
        //set up the menu
        myMenu.setUpMenu(this, (ImageButton) findViewById(R.id.menuButton),
                (DrawerLayout) findViewById(R.id.drawerLayout),
                (ListView) findViewById(R.id.drawerList), user.getUserId());

        listOfPeople = new UsersListUtils(this, mainContainer) {
            @Override
            public void followeesLoaded(boolean hasMoreFollowees) {}
            @Override
            public void problemWithConnection() {}
        };


        searchForUsers = new SearchForUsers(this.getApplicationContext()) {
            @Override
            public void onResponseReceived(String response) {
                progressBar.setVisibility(View.GONE);
                int status = this.checkResponse(response);

                if(status == ResponseStatus.SUCCESS){
                    ArrayList<User> users= this.parseJSON(response);
                    mainContainer.removeAllViews();
                    listOfPeople.addUsers(users);
                    updateTitle(ResponseStatus.SUCCESS, users.size());
                }else{
                    updateTitle(status,0);
                }

            }
        };

    }

    private void updateTitle(int status,int count){

        switch (status){
            case ResponseStatus.SUCCESS:
                title.setText(this.getResources().getString(R.string.foundResults,count));
                break;
            case ResponseStatus.ZERO_ROWS_RETURNED:
                title.setText(this.getResources().getString(R.string.no_results));
                break;
            case ResponseStatus.NO_INTERNET:
                title.setText((this.getResources().getString(R.string.NO_INTERNET_CONNECTION)));
                break;
            case ResponseStatus.UNKNOWN_ERROR:
                title.setText((this.getResources().getString(R.string.unknown_error)));
                break;
            case DEFAULT_TITLE:
                title.setText((this.getResources().getString(R.string.find_people)));
                break;
        }



    }

    private void searchForUsers(String pattern){
        progressBar.setVisibility(View.VISIBLE);
        searchForUsers.searchForUsers(pattern);
    }


}
