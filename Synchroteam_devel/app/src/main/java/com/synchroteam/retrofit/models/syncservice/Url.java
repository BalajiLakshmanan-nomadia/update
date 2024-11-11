package com.synchroteam.retrofit.models.syncservice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Url {

    @SerializedName("url_stripe")
    @Expose
    private String urlStripe;
    @SerializedName("url_event_listener")
    @Expose
    private String urlEventListener;
    @SerializedName("url_image_listener")
    @Expose
    private String urlImageListener;

    @SerializedName("url_pool_listener")
    @Expose
    private String urlPoolListener;

    public String getUrlPoolListener() {
        return urlPoolListener;
    }

    public void setUrlPoolListener(String urlPoolListener) {
        this.urlPoolListener = urlPoolListener;
    }
    @SerializedName("url_mobile_auth")
    @Expose
    private String urlMobileAuth;
    public String getUrlMobileAuth() {
        return urlMobileAuth;
    }

    public void setUrlMobileAuth(String urlMobileAuth) {
        this.urlMobileAuth = urlMobileAuth;
    }

    @SerializedName("url_base")
    @Expose
    private String url_base;


    public String getUrlStripe() {
        return urlStripe;
    }

    public void setUrlStripe(String urlStripe) {
        this.urlStripe = urlStripe;
    }

    public String getUrlEventListener() {
        return urlEventListener;
    }

    public void setUrlEventListener(String urlEventListener) {
        this.urlEventListener = urlEventListener;
    }

    public String getUrlImageListener() {
        return urlImageListener;
    }

    public void setUrlImageListener(String urlImageListener) {
        this.urlImageListener = urlImageListener;
    }

    public String getUrl_base() {
        return url_base;
    }

    public void setUrl_base(String url_base) {
        this.url_base = url_base;
    }
}
