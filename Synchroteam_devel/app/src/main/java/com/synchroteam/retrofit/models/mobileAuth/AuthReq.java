package com.synchroteam.retrofit.models.mobileAuth;

public class AuthReq {

    private String domain,login,password;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUserId() {
        return login;
    }

    public void setUserId(String userId) {
        this.login = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
