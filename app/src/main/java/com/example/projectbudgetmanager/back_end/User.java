package com.example.projectbudgetmanager.back_end;

public class User {
    private String name;
    private String pass;
    private String Token;


    public User(String username, String pass) {
        name = username;
        this.pass = pass;
    }

    public String getUsername() {
        return name;
    }

    public void setUsername(String username) {
        name = username;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        pass = pass;
    }


    public String getToken() {
        return Token;
    }

    public void setToken(String token) {
        Token = token;
    }
}
