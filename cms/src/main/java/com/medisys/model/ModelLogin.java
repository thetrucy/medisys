package com.medisys.model;

public class ModelLogin {

    private String id;
    private String password;

    public ModelLogin(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public ModelLogin() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}