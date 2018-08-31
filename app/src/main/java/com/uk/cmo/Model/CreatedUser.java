package com.uk.cmo.Model;

/**
 * Created by usman on 10-02-2018.
 */

public class CreatedUser {

    String uid;
    String full_name,user_name;
    String token;
    boolean accountsetup;
    boolean legit;   //getters and setter will be added for this once admin signup is included



    public CreatedUser() {

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public CreatedUser(String full_name, String user_name,String uid, boolean accountsetup) {
        this.full_name = full_name;
        this.user_name = user_name;
        this.uid = uid;
        this.accountsetup = accountsetup;
    }

    public boolean isLegit() {
        return legit;
    }

    public void setLegit(boolean legit) {
        this.legit = legit;
    }


    public boolean isAccountsetup() {
        return accountsetup;
    }

    public void setAccountsetup(boolean accountsetup) {
        this.accountsetup = accountsetup;
    }
    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getUser_name() {
        return user_name;
    }

}
