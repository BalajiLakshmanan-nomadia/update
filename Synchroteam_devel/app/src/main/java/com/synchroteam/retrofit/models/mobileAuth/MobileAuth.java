package com.synchroteam.retrofit.models.mobileAuth;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.synchroteam.dao.Dao;
import com.synchroteam.retrofit.models.syncservice.Debug;

public class MobileAuth {
    @SerializedName("result")
    @Expose
    private int result;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("data")
    @Expose
    private Data data;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }



}
