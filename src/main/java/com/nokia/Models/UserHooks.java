package com.nokia.Models;

/**
 * Created by rkonda on 10/18/2016.
 */
public class UserHooks {

    private int user_id;
    private int chat_thread_id;
    private String reponame;
    private String project;
    private String hook_id;


    public String getHook_id() {
        return hook_id;
    }

    public void setHook_id(String hook_id) {
        this.hook_id = hook_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getChat_thread_id() {
        return chat_thread_id;
    }

    public void setChat_thread_id(int chat_thread_id) {
        this.chat_thread_id = chat_thread_id;
    }

    public String getReponame() {
        return reponame;
    }

    public void setReponame(String reponame) {
        this.reponame = reponame;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    @Override
    public String toString() {
        return "UserHooks{" +
                "user_id=" + user_id +
                ", chat_thread_id=" + chat_thread_id +
                ", reponame='" + reponame + '\'' +
                ", project='" + project + '\'' +
                ", hook_id='" + hook_id + '\'' +
                '}';
    }

}
