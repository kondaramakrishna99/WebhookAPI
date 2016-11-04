package com.nokia.Models;

/*
This model respresents the table authorize_table in database
Each user has an access token.
 */
public class UserToken {

    private String user_id;
    private String access_token;
    private String username;
    private String project;
    private String scope;

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public UserToken() {

    }

    public UserToken(String user_id, String access_token) {
        this.user_id = user_id;
        this.access_token = access_token;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
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
                "user_id='" + user_id + '\'' +
                ", access_token='" + access_token + '\'' +
                ", username='" + username + '\'' +
                ", project='" + project + '\'' +
                ", scope='" + scope + '\'' +
                '}';
    }
}
