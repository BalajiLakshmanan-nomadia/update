package com.synchroteam.retrofit.models.mobileAuth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.synchroteam.retrofit.models.syncservice.Debug;

public class Data {
    @SerializedName("authToken")
    @Expose
    private String authToken;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getAuthExpiry() {
        return authExpiry;
    }

    public void setAuthExpiry(String authExpiry) {
        this.authExpiry = authExpiry;
    }

    @SerializedName("authExpiry")
    @Expose
    private String authExpiry;
}
