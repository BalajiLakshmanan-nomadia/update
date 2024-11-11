package com.synchroteam.retrofit.models.syncservice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SyncService {

    @SerializedName("debug")
    @Expose
    private Debug debug;
    @SerializedName("std_v2")
    @Expose
    private Std std;
    @SerializedName("url")
    @Expose
    private Url url;

    public Debug getDebug() {
        return debug;
    }

    public void setDebug(Debug debug) {
        this.debug = debug;
    }

    public Std getStd() {
        return std;
    }

    public void setStd(Std std) {
        this.std = std;
    }

    public Url getUrl() {
        return url;
    }

    public void setUrl(Url url) {
        this.url = url;
    }

}