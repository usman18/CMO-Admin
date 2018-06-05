package com.uk.cmo.Model;

/**
 * Created by usman on 10-02-2018.
 */

public class CreatedUser {
    String full_name,user_name;
    boolean accountsetup;
    boolean membersetup;
    boolean legit;   //getters and setter will be added for this once admin signup is included
    String token;


    public CreatedUser() {

    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public CreatedUser(String full_name, String user_name, boolean accountsetup, boolean membersetup) {
        this.full_name = full_name;
        this.user_name = user_name;
        this.accountsetup = accountsetup;
        this.membersetup=membersetup;
    }

    public boolean isLegit() {
        return legit;
    }

    public void setLegit(boolean legit) {
        this.legit = legit;
    }


    public boolean isMembersetup() {
        return membersetup;
    }

    public void setMembersetup(boolean membersetup) {
        this.membersetup = membersetup;
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
