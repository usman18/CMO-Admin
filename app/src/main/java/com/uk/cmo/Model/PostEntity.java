package com.uk.cmo.Model;

/**
 * Created by usman on 24-02-2018.
 */

public class PostEntity {
    private String post_id;
    private String post_uri;
    private String user_name;
    private String user_pp;
    private String timestamp;
    private String description;

    public PostEntity() {
    }

    public String getPost_id() {
        return post_id;
    }

    public void setPost_id(String post_id) {
        this.post_id = post_id;
    }

    public String getPost_uri() {
        return post_uri;
    }

    public void setPost_uri(String post_uri) {
        this.post_uri = post_uri;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_pp() {
        return user_pp;
    }

    public void setUser_pp(String user_pp) {
        this.user_pp = user_pp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
