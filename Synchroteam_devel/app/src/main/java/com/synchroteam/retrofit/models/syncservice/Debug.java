package com.synchroteam.retrofit.models.syncservice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Debug {

    @SerializedName("port")
    @Expose
    private Integer port;
    @SerializedName("server")
    @Expose
    private String server;
    @SerializedName("ssl")
    @Expose
    private Boolean ssl;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public Boolean getSsl() {
        return ssl;
    }

    public void setSsl(Boolean ssl) {
        this.ssl = ssl;
    }

}