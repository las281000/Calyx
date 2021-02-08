package com.example.calyx;


public class User  {
    private String login;
    private String password;
    private String userKey;


    public User(String login, String password, String userKey){
        this.login = login;
        this.password = password;
        this.userKey = userKey;
    }

    public  String getLogin(){
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }
}
