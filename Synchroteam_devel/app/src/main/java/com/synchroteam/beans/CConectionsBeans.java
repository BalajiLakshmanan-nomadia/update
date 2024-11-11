package com.synchroteam.beans;

/**
 * Created by Trident on 5/5/17.
 */

public class CConectionsBeans {

    private int idConnection;
    private String jsonAuth;
    private String jsonSettings;
    private Boolean fl_active;


    public int getIdConnection() {
        return idConnection;
    }

    public void setIdConnection(int idConnection) {
        this.idConnection = idConnection;
    }

    public String getJsonAuth() {
        return jsonAuth;
    }

    public void setJsonAuth(String jsonAuth) {
        this.jsonAuth = jsonAuth;
    }

    public String getJsonSettings() {
        return jsonSettings;
    }

    public void setJsonSettings(String jsonSettings) {
        this.jsonSettings = jsonSettings;
    }

    public Boolean getFl_active() {
        return fl_active;
    }

    public void setFl_active(Boolean fl_active) {
        this.fl_active = fl_active;
    }
}
