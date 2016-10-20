package com.nokia.Models;

/**
 * Created by rkonda on 10/18/2016.
 */
public class AjaxRequestHook {

    String user_id;
    String repo;

    AjaxRequestHook()
    {

    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    AjaxRequestHook(String user_id, String repo)
    {
        this.user_id=user_id;
        this.repo=repo;
    }

    @Override
    public String toString() {
        return "AjaxRequestHook{" +
                "user_id='" + user_id + '\'' +
                ", repo='" + repo + '\'' +
                '}';
    }
}
