package com.nokia.Models;

/*
This model respresents the table authorize_table in database
Each user has an access token.
 */
public class UserToken {

    private int user_id;
    private String access_token;

    public UserToken() {

    }

    public UserToken(int user_id, String access_token) {
        this.user_id = user_id;
        this.access_token = access_token;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }


    @Override
    public String toString() {
        return "UserToken{" +
                "user_id=" + user_id +
                ", access_token='" + access_token + '\'' +
                '}';
    }

}
