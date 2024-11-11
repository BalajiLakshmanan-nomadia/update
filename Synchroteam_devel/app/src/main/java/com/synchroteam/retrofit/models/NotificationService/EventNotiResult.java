package com.synchroteam.retrofit.models.NotificationService;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventNotiResult {

    @SerializedName("Result")
    @Expose
    private Integer result;

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

}
