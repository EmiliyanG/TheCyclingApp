package com.thecyclingapp.emiliyan.thecyclingapp;


import android.util.Log;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.thecyclingapp.emiliyan.thecyclingapp.dbTables.User;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.LogInResult;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.Passwords;
import com.thecyclingapp.emiliyan.thecyclingapp.extras.RegisterResult;
import com.thecyclingapp.emiliyan.thecyclingapp.dbTables.Statistics;
import com.thecyclingapp.emiliyan.thecyclingapp.dbTables.Workout;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;


/**
 * Created by Emiliyan Grigorov on 3/20/2015.
 *
 * Purpose: connect to database
 *
 * user manual :D : - create your own method
 *                  - make session (   private Session makeSession()  )
 *                  - make connection(   private Connection makeConnection()  )
 *                  - prepare statement and execute it
 *                  - get result set and use the data for your own purpose
 *                  - close connection !!  (   private void closeConnection(Connection conn)   )
 *                  - close session !!    (   private void closeSession(Session session)   )
 *
 *
 *
 */
public class MyConnection {

    private final static String USERNAME = "b3039447";
    private final static String PASSWORD = "bge_123_bge";
    /*----------------------------------------------------*/

    private final static int L_PORT=5656;
    private final static String HOST="linux.cs.ncl.ac.uk";
    private final static String R_HOST="homepages.cs.ncl.ac.uk";
    private final static int R_PORT=3306;
    private final static String DB_USERNAME = "b3039447";
    private final static String DB_PASSWORD = "=JoysMen";
    private final static String URL = "jdbc:mysql://localhost:"+L_PORT+"/b3039447";
    private final static String DRIVER_NAME="com.mysql.jdbc.Driver";



    private Session makeSession() {
        Session session = null;
        try{
            //Set StrictHostKeyChecking property to no to avoid UnknownHostKey issue
            Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");

            JSch jsch = new JSch();
            //set campus username and password
            session=jsch.getSession(USERNAME, HOST, 22);
            session.setPassword(PASSWORD);
            session.setConfig(config);
            //connect to campus
            session.connect();
            session.setPortForwardingL(L_PORT, R_HOST, R_PORT);
        }catch(Exception e){
            System.out.println("problem with session");
            closeSession(session);
        }
        return session;

    }
    private void closeSession(Session session){
        if(session !=null && session.isConnected()){
            session.disconnect();
        }
    }

    private Connection makeConnection(){
        Connection conn = null;
        try{
            Class.forName(DRIVER_NAME).newInstance();
            conn = DriverManager.getConnection(URL, DB_USERNAME, DB_PASSWORD);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("problem with connection");
        }
        return conn;
    }

    private void closeConnection(Connection conn) throws SQLException {
        if(conn != null && !conn.isClosed()){
            conn.close();
        }
    }

//    public void testConnection(){
//        Connection conn = null;
//        Session session= null;
//        session = makeSession();
//        conn = makeConnection();
//        System.out.println("you managed to connect to database successfully");
//        try{
//            closeConnection(conn);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        closeSession(session);
//    }
//
//


//    //return true if password and username pair is correct and exists in the database
//    public int checkUsernameAndPassword(String username, String password, User user) {
//
//        Session session;
//        Connection conn;
//        Passwords passwords = new Passwords();
//
//        try{
//            session = makeSession();
//            conn = makeConnection();
//            PreparedStatement mySt = conn.prepareStatement("SELECT * FROM user WHERE username=?");
//
//            mySt.setString(1, username);/*replace '?' in the query with username; note parameters count start from 1 !!*/
//
//            ResultSet myRs = mySt.executeQuery();/*execute query and save result set*/
//
//            int count= 0;//count successful matches
//            String dbPassword = "",
//                    dbSalt = "",
//                    firstName = "",
//                    lastName = "";
//
//                /*loop through all matches */
//            while(myRs.next()) {//for username and password to be valid there should be only 1 matching row
//                count++;
//                dbPassword = myRs.getString(2);
//                dbSalt = myRs.getString(3);
//                firstName = myRs.getString(5);
//                lastName = myRs.getString(6);
//            }
//
//            if(count == 1){//if there is only 1 match in the database proceed
//                boolean passwordMatch = passwords.checkPassword(password,dbPassword,dbSalt);
//
//                if(passwordMatch){
//                    user.setUserId(username);
//                    user.setFirstName(firstName);
//                    user.setLastName(lastName);
//                    return LogInResult.SUCCESS;
//                }
//                else return LogInResult.VALID_USER_PASSWORD_WRONG;
//            }
//
//            if(count == 0) return LogInResult.NO_SUCH_USER_FOUND;// if username is not found
//
//            closeConnection(conn);
//            closeSession(session);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//        return LogInResult.UNKNOWN_ERROR;
//    }

//    public int addUser(String username, String password, String salt, String email,String firstNameText,String lastNameText){
//        Connection conn = null;
//        Session session= null;
//        Boolean success = false;
//        try{
//            session = makeSession();
//            conn = makeConnection();
//
//                /*make MySQL query*/
//            PreparedStatement mySt = conn.prepareStatement("INSERT INTO user (username, password, salt, email, firstName, lastName) VALUES (?, ?, ?, ?, ?, ?)");
//                /*replace the first '?' in the query with usernameInput
//                * note parameters count start from 1 !!*/
//            mySt.setString(1, username);
//                /*replace the second '?' in the query with passwordInput*/
//            mySt.setString(2, password);
//            mySt.setString(3, salt);
//            mySt.setString(4, email);
//            mySt.setString(5, firstNameText);
//            mySt.setString(6, lastNameText);
//            System.out.println("prepare statement to execute!");
//                /*execute query and save result set*/
//            mySt.executeUpdate();
//            /* execute query */
//            System.out.println("user added!");
//            success=true;
//
//            closeConnection(conn);
//            closeSession(session);
//        }catch (MySQLIntegrityConstraintViolationException e){
//            return RegisterResult.DUPLICATE_USERNAME;
//        }
//        catch(Exception e){
//
//            e.printStackTrace();
//            System.out.println("Error occured while adding user to database !!");
//            return RegisterResult.UNKNOWN_ERROR;
//        }
//        return RegisterResult.SUCCESS;
//    }


//    /*
//    * Save a workout in database
//    * */
//    public boolean saveWorkout(double averageSpeed,double distance, long time, long date, String userId){
//        Connection conn = null;
//        Session session= null;
//        Boolean success = false;
//        try{
//            session = makeSession();
//            conn = makeConnection();
//
//                /*make MySQL query*/
//            PreparedStatement mySt = conn.prepareStatement("INSERT INTO workout (average_speed, time, distance, user_id, date) VALUES (?, ?, ?, ?, ?)");
//
//            mySt.setDouble(1, averageSpeed);/*replace the first '?' in the query with average_speed; note parameters count start from 1 !!*/
//            mySt.setLong(2, time);
//            mySt.setDouble(3, distance);
//            mySt.setString(4, userId);
//            mySt.setLong(5, date);
//
//            System.out.println("prepare statement to execute!");
//                /*execute query and save result set*/
//            mySt.executeUpdate();
//            /* execute query */
//            System.out.println("update complete!");
//            success=true;
//                /*otherwise there is no match or there is more than 1 match and duplicate rows*/
//            closeConnection(conn);
//            closeSession(session);
//
//        }catch(Exception e){
//            System.out.println("Error with updating workouts set up !!");
//        }
//        return success;
//    }


    //SELECT * FROM   workout WHERE  user_id = "Em" ORDER  BY date  LIMIT  10 OFFSET 0;
    /*
    * parameters:
    * username - used to to find all workouts for this user
    * limit - used to limit the result arraylist of workouts to a given number
    * offset - used to point a starting point from from the results
     *
     * offset and limit are useful because in practise cyclist will have at least 1 workout a day
     * after a year or 2 the result set can grow up to 700 rows for example
     * meaning that by pulling all rows every time user wants to see them will be inefficient and time consuming
     * better approach would be to pull for example 10 rows in a scroll view at a time and load more rows on scrolling down
     * offset is like a index in array starting from 0 to the size of the array - 1
    * */
//    public ArrayList<Workout> getWorkouts(String username,int limit, int offset, long date){
//        Connection conn = null;
//        Session session= null;
//        ArrayList<Workout> workouts = null;
//        try{
//            session = makeSession();
//            conn = makeConnection();
//
//            PreparedStatement mySt;
//            if(date == 0l){
//              /*make MySQL query*/
//                mySt = conn.prepareStatement("SELECT * FROM workout WHERE user_id =? ORDER BY date DESC LIMIT ? OFFSET ?");
//                mySt.setString(1, username);/*replace the first '?' in the query with username; note parameters count start from 1 !!*/
//                mySt.setInt(2, limit);
//                mySt.setInt(3, offset);
//                System.out.println("prepare statement to retrieve workouts from database!");
//            }else{
//                /*make MySQL query*/
//                mySt = conn.prepareStatement("SELECT * FROM workout WHERE user_id =? && date<=? ORDER BY date DESC LIMIT ? OFFSET ?");
//                mySt.setString(1, username);/*replace the first '?' in the query with username; note parameters count start from 1 !!*/
//                mySt.setLong(2, date);
//                mySt.setInt(3, limit);
//                mySt.setInt(4, offset);
//                System.out.println("prepare statement to retrieve workouts from database!");
//            }
//
//            ResultSet myRs = mySt.executeQuery();/*execute query and save result set*/
//
//            workouts = new ArrayList<>();
//            while(myRs.next()) {
//                double avrgSpeed = myRs.getDouble(1);
//                long time = myRs.getLong(2);
//                double distance = myRs.getDouble(3);
//                long currentDate = myRs.getLong(5);
//                Workout workout = new Workout(avrgSpeed,distance,time,currentDate,username);
//                workouts.add(workout);
//            }
//
//
//                /*otherwise there is no match or there is more than 1 match and duplicate rows*/
//            closeConnection(conn);
//            closeSession(session);
//        }catch(Exception e){
//            System.out.println("Error with PIN set up !!");
//        }
//        return workouts;
//    }


//    public Statistics getStatisctics(String username){
//        Connection conn = null;
//        Session session= null;
//        Statistics statistics = null;
//        try{
//            session = makeSession();
//            conn = makeConnection();
//
//              /*make MySQL query*/
//            PreparedStatement mySt = conn.prepareStatement("SELECT * FROM user_statistics WHERE user_id =? ");
//            mySt.setString(1, username);/*replace the first '?' in the query with username; note parameters count start from 1 !!*/
//
//            ResultSet myRs = mySt.executeQuery();/*execute query and save result set*/
//
//
//            if(myRs.next()) {//myRs should always return a single row because of the uniqueness property for username
//                //in the user_statistics table 1 column is the username
//                //column count starts from 1
//                double weeklyAvgSpeedSum = myRs.getDouble(2),
//                        weeklyDistance = myRs.getDouble(4),
//                        totalAvgSpeedSum = myRs.getDouble(6),
//                        totalDistance = myRs.getDouble(8);
//
//                long weeklyAvgSpeedCount = myRs.getLong(3),
//                        weeklyTime = myRs.getLong(5),
//                        totalAvgSpeedCount = myRs.getLong(7),
//                        totalTime = myRs.getLong(9);
//
//                statistics = new Statistics(username,weeklyAvgSpeedSum,weeklyDistance,totalAvgSpeedSum,totalDistance,
//                         weeklyAvgSpeedCount,weeklyTime,totalAvgSpeedCount,totalTime);
//
//            }else {/*otherwise there is no match; set default values for doubles and longs*/
//                statistics = new Statistics(username,DEFAULT_DOUBLE,DEFAULT_DOUBLE,DEFAULT_DOUBLE,DEFAULT_DOUBLE,
//                        DEFAULT_LONG,DEFAULT_LONG,DEFAULT_LONG,DEFAULT_LONG);
//                insertStatisticsForUser(conn, username);
//            }
//
//            closeConnection(conn);
//            closeSession(session);
//        }catch(Exception e){
//            System.out.println("Error with PIN set up !!");
//        }
//        return statistics;
//    }
//

//    private boolean insertStatisticsForUser(Connection conn, String username) throws SQLException{
//
//        PreparedStatement mySt = conn.prepareStatement(
//                "INSERT INTO user_statistics (user_id, weekly_avg_speed_sum," +
//                " weekly_avg_speed_count, weekly_distance," +
//                " weekly_time, total_avg_speed_sum, total_avg_speed_count," +
//                " total_distance, total_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
//        mySt.setString(1, username);
//        mySt.setDouble(2, DEFAULT_DOUBLE);
//        mySt.setLong(3, DEFAULT_LONG);
//        mySt.setDouble(4, DEFAULT_DOUBLE);
//        mySt.setLong(5, DEFAULT_LONG);
//        mySt.setDouble(6, DEFAULT_DOUBLE);
//        mySt.setLong(7, DEFAULT_LONG);
//        mySt.setDouble(8, DEFAULT_DOUBLE);
//        mySt.setLong(9, DEFAULT_LONG);
//
//        int result = mySt.executeUpdate();//return number of updated rows
//        Log.d("create statistics row","update succeeds");
//        return result == 1;
//    }

//    public boolean updateStatisticsForUser(Statistics statistics){
//        Connection conn = null;
//        Session session= null;
//        int result = 0;
//        try{
//            session = makeSession();
//            conn = makeConnection();
//
//              /*make MySQL query*/
//            PreparedStatement mySt = conn.prepareStatement("UPDATE user_statistics SET weekly_avg_speed_sum=?, weekly_avg_speed_count=?, weekly_distance=?, weekly_time=?," +
//                                                            " total_avg_speed_sum=?, total_avg_speed_count=?, total_distance=?, total_time=? WHERE user_id=?");
//
//            mySt.setDouble(1,statistics.getWeeklyAvgSpeedSum());/*replace the first '?' in the query with double; note parameters count start from 1 !!*/
//            mySt.setLong(2, statistics.getWeeklyAvgSpeedCount());
//            mySt.setDouble(3,statistics.getWeeklyDistance());
//            mySt.setLong(4,statistics.getWeeklyTime());
//            mySt.setDouble(5, statistics.getTotalAvgSpeedSum());
//            mySt.setLong(6, statistics.getTotalAvgSpeedCount());
//            mySt.setDouble(7,statistics.getTotalDistance());
//            mySt.setLong(8,statistics.getTotalTime());
//            mySt.setString(9,statistics.getUserId());
//            result = mySt.executeUpdate();//return number of updated rows
//            Log.d("create statistics row","update succeeds");
//
//
//            closeConnection(conn);
//            closeSession(session);
//        }catch(Exception e){
//            System.out.println("Error with PIN set up !!");
//        }
//        return result == 1;
//    }

    public ArrayList<User> downloadFolloweesList(User user, int limit, int offset){
        Connection conn = null;
        Session session= null;
        ArrayList<User> followeesList = null;
        try{
            session = makeSession();
            conn = makeConnection();
            PreparedStatement mySt;
            /*make MySQL query*/
            mySt = conn.prepareStatement("SELECT " +
                    "f.followee_id,u.firstName,u.lastName " +
                    "FROM (followees f inner join user u on f.followee_id = u.username) " +
                    "WHERE f.user_id =? " +
                    "ORDER BY f.since DESC " +
                    "LIMIT ? " +
                    "OFFSET ?");

            mySt.setString(1, user.getUserId());/*replace the first '?' in the query with username; note parameters count start from 1 !!*/
            mySt.setInt(2, limit);
            mySt.setInt(3, offset);

            Log.d("followees","start downloading followees list!");
            ResultSet myRs = mySt.executeQuery();/*execute query and save result set*/
            followeesList = new ArrayList<>();

            String username,firstName,lastName;

            while(myRs.next()) {

                username = myRs.getString(1);
                firstName = myRs.getString(2);
                lastName =myRs.getString(3);
                User friend = new User(username,firstName,lastName);
                followeesList.add(friend);
            }

            closeConnection(conn);
            closeSession(session);
        }catch(Exception e){
            Log.e("friends list","trying to download friends list");
            e.printStackTrace();
        }
        return followeesList;
    }


}


