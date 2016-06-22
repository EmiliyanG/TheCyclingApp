package com.thecyclingapp.emiliyan.thecyclingapp.extras;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Emiliyan Grigorov
 * Date: 2 April 2016
 * Purpose: detect left, right swipe to show or hide side menu for list item
 */
public class SwipeDetector implements View.OnTouchListener {

    private static final int MAX_DISTANCE = UsersListUtils.SIDE_MENU_MAX_WIDTH;
    private float downX,distance;
    private int newWidth = 0;
    private int currentWidth = 0;

    private LinearLayout sideMenu;
    private boolean sideMenuOpen = false;
    private UsersListUtils utils;

    public SwipeDetector(LinearLayout sideMenu, UsersListUtils utils){
        this.sideMenu = sideMenu;
        this.utils = utils;
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {//ACTION_DOWN = 0
                downX = event.getX();
                currentWidth = v.getWidth();
                return true;
            }
            case MotionEvent.ACTION_MOVE: {//ACTION_MOVE = 2

                distance = downX - event.getX();
                newWidth = (int) Math.abs(distance);


                //horizontal swipe detection only
                if (distance < 0) {//swipe left to right
                    newWidth = 0;
                }

                if (distance > 0) {//swipe right to left

                    if(newWidth>MAX_DISTANCE){
                        sideMenuOpen = true;
                        newWidth= MAX_DISTANCE;
                    }
                }

                resizeSideMenu(newWidth);
                return true; // allow other events like Click to be processed
            }

            case MotionEvent.ACTION_UP:{
                if(distance==0) v.performClick();//if click was performed
                sideMenuLeftHalfOpen();//check if side menu was left not fully open
                distance = 0;
            }
            case MotionEvent.ACTION_CANCEL:{
                distance = 0;
                sideMenuLeftHalfOpen();//check if side menu was left not fully open
            }

        }
        return false;
    }

    private boolean sideMenuLeftHalfOpen(){
        if(newWidth < MAX_DISTANCE){//If side menu is not fully extended
            resizeSideMenu(0);//hide it
            sideMenuOpen = false;
            return true;//consume event so that no click is detected
        }else{
            utils.newSideMenuOpened(sideMenu);
        }
        return false;
    }

    private void resizeSideMenu(int w){
        currentWidth = w;
        sideMenu.getLayoutParams().width = w;
        sideMenu.requestLayout();
    }
}
