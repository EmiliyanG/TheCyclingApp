package com.thecyclingapp.emiliyan.thecyclingapp.dbTables;

/**
 * Created by Emiliyan on 3/29/2016.
 */
public class User {
    private String userId, firstName, lastName;
    public User(){

    }
    public User(String userId,String firstName,String lastName){
        this.userId =userId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void setUserId(String userId) {this.userId = userId;}
    public void setFirstName(String firstName) {this.firstName = firstName;}
    public void setLastName(String lastName) {this.lastName = lastName;}

    public String getUserId() {return userId;}
    public String getFirstName() {return firstName;}
    public String getLastName() {return lastName;}
    public String toString(){
        return firstName+" "+lastName;
    }
}
