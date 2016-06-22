package com.thecyclingapp.emiliyan.thecyclingapp.extras;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.thecyclingapp.emiliyan.thecyclingapp.R;
import com.thecyclingapp.emiliyan.thecyclingapp.dbTables.Workout;

/**
 * Created by Emiliyan on 4/19/2016.
 */
public class ViewGenerator {
    private Context context;
    private int background, custom_dark, custom_medium, custom_light, custom_lighter, custom_lightest;
    protected static final int GRAPH_HEIGHT = 400, //used to determine graph height columns from graph may have bigger height
            GRAPH_COLUMN_WIDTH = 160,
            GRAPH_LINE_WIDTH = 8,
            SIDE = 160,
            TITLE_SIZE = 20,
            GRAPH_TEXT_SIZE = 12;
    private LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT
            , LinearLayout.LayoutParams.WRAP_CONTENT);

    public ViewGenerator(Context c){
        this.context = c;
        setColors();
    }
    private void setColors() {
        background = ContextCompat.getColor(context, R.color.background);
        custom_dark = ContextCompat.getColor(context, R.color.custom_dark);
        custom_medium = ContextCompat.getColor(context, R.color.custom_medium);
        custom_light = ContextCompat.getColor(context, R.color.custom_light);
        custom_lighter = ContextCompat.getColor(context, R.color.custom_lighter);
        custom_lightest = ContextCompat.getColor(context, R.color.custom_lightest);
    }

    public LinearLayout generateWorkoutView(Workout workout) {

        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        llp.topMargin = 20;

        LinearLayout mainContainer = generateLayout(null, llp, LinearLayout.VERTICAL);//main layout
        mainContainer.setPadding(0, 0, 0, 20);

        LinearLayout.LayoutParams topParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        topParams.setMargins(6, 20, 6, 0);//left, top,right,bottom
        LinearLayout top = generateLayout(mainContainer, topParams, LinearLayout.HORIZONTAL);// top layout contains thumbnail, topVeritcal
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(SIDE, SIDE);
        ImageView thumbnail = new ImageView(context);
        thumbnail.setBackgroundColor(background);
        thumbnail.setLayoutParams(imageParams);
        top.addView(thumbnail);

        LinearLayout.LayoutParams topVerticalParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        topVerticalParams.setMargins(6, 0, 6, 0);//left, top,right,bottom
        LinearLayout topVertical = generateLayout(top, topVerticalParams, LinearLayout.VERTICAL);// contains title, hours ago
        generateTextView(topVertical, TextFormat.formatWorkoutViewTitle(workout.getUserId()), TITLE_SIZE);//add title
        generateTextView(topVertical, TextFormat.formatTimeAgo(workout.getDate()), TITLE_SIZE);//add time ago

        LinearLayout.LayoutParams graphParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        graphParams.setMargins(6, 20, 6, 0);//left,top,right,bottom
        LinearLayout graph = generateLayout(mainContainer, graphParams, LinearLayout.HORIZONTAL);//graph
        mainContainer.setBackgroundColor(custom_lighter);
        graph.setGravity(Gravity.BOTTOM);

        generateGraphColumn(graph, GRAPH_HEIGHT, GRAPH_LINE_WIDTH, background, null, 0);//left line
        generateGraphColumn(graph, getAvrgSpeedGraphColumnHeight(workout.getAverageSpeed()), GRAPH_COLUMN_WIDTH, custom_light, "average speed", 0);//average speed column
        generateGraphColumn(graph, getTimeGraphColumnHeight(workout.getTime()), GRAPH_COLUMN_WIDTH, custom_dark, "time", 0);//time
        generateGraphColumn(graph, getDistanceGraphColumnHeight(workout.getDistance()), GRAPH_COLUMN_WIDTH, custom_medium, "distance", 0);// distance

        generateGraphColumn(mainContainer, GRAPH_LINE_WIDTH, LinearLayout.LayoutParams.WRAP_CONTENT, background, null, 6);//graph bottom line

        LinearLayout.LayoutParams extraInfoParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        extraInfoParams.setMargins(0, 6, 0, 6);//left,top,right,bottom
        LinearLayout extraInfo = generateLayout(mainContainer,extraInfoParams,LinearLayout.VERTICAL);

        generateTextView(extraInfo,TextFormat.formatWorkoutViewAvgSpeed(workout.getAverageSpeed()),TITLE_SIZE);//generate average speed textView
        generateTextView(extraInfo,TextFormat.formatWorkoutViewTime(workout.getTime()),TITLE_SIZE);//generate time textView
        generateTextView(extraInfo,TextFormat.formatWorkoutViewDistance(workout.getDistance()),TITLE_SIZE);//generate distance textView
        extraInfo.setVisibility(View.GONE);

        mainContainer.setOnClickListener(new ClickListener(extraInfo) {
            @Override
            public void onClick(View v) {
                if(extraInfo.getVisibility()==View.VISIBLE) this.extraInfo.setVisibility(View.GONE);
                else this.extraInfo.setVisibility(View.VISIBLE);
            }
        });



        return mainContainer;
    }

    public LinearLayout generateLayout(LinearLayout parent, LinearLayout.LayoutParams lp, int orientation) {

        LinearLayout temp = new LinearLayout(context);//create layout
        if (parent != null) parent.addView(temp);//add it to parent
        temp.setLayoutParams(lp); //add layout parameters
        temp.setOrientation(orientation);// set orientation
        return temp;
    }

    //set column text null if you use you want to use the column as a graph line
    public LinearLayout generateGraphColumn(LinearLayout parent, int height, int width, int color, String columnText, int leftMargin) {

        LinearLayout.LayoutParams columnParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        columnParams.gravity = Gravity.BOTTOM;
        columnParams.setMargins(leftMargin, 0, 10, 0);//left,top,right,bottom

        LinearLayout column = generateLayout(parent, columnParams, LinearLayout.VERTICAL);
        column.setGravity(Gravity.CENTER_HORIZONTAL);
        if (columnText != null) generateTextView(column, columnText, GRAPH_TEXT_SIZE);

        View temp = new View(context);
        temp.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        temp.setBackgroundColor(color);

        column.addView(temp);
        return column;
    }


    //generate textView set layout parameters and add it to parent layout
    //set text as well
    public TextView generateTextView(LinearLayout textViewParent, String text, float textSize) {
        TextView temp = new TextView(context);
        temp.setLayoutParams(textViewParams);
        temp.setText(text);
        temp.setTextSize(textSize);
        temp.setTextColor(background);
        textViewParent.addView(temp);
        return temp;
    }

    /*
    * used to determine Average speed column's height for Profile Activity
    * */
    public int getAvrgSpeedGraphColumnHeight(double averageSpeed){
        //assumptions max average speed 100 kmh
        //even if average speed is bigger than 100 kmh the graph layout for profile activity should still look good
        //used to display a graphical representation of average speed in m/s: 100 kmh = 27 m/s * 15 = 405
        int index = GRAPH_HEIGHT / 26;
        int avrgSpeed = (int)( averageSpeed+0.5);
        return avrgSpeed * index;
    }

    /*
    * used to determine Time column's height for Profile Activity
    * */
    public int getTimeGraphColumnHeight(long time){
        long sixHours = 21600000, twelveHours = 43200000;

        int x = 8;
        //reduce the height of the column to fit the graph
        //future updates add achievements if true
        if(sixHours < time) x= 4;
        if(twelveHours < time) x= 2;

        int index = GRAPH_HEIGHT / x;
        time = time/1000; //convert milliseconds to seconds
        return ((int)time) / index;
    }

    /*
    * used to determine distance column's height for Profile Activity
    * */
    public int getDistanceGraphColumnHeight(double distance){
        int fortyKm = 40000; //in meters
        int x = 4;
        if( ((int) distance) > fortyKm) x = 2;

        int index = GRAPH_HEIGHT / x;


        return ((int) distance) / index;
    }

    private abstract class ClickListener implements View.OnClickListener{
        protected LinearLayout extraInfo;
        public ClickListener(LinearLayout extraInfo){
            this.extraInfo =extraInfo;
        }
    }
}
