package com.synchroteam.retrofit.models.mobileAuth;

public class ChangePasswordModel {
    private String domain, login, oldPassword, newPassword, newPassword2;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConformPassword() {
        return newPassword2;
    }

    public void setConformPassword(String conformPassword) {
        this.newPassword2 = conformPassword;
    }


}
