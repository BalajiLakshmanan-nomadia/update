package com.synchroteam.retrofit.models.JobPoolService;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JobPoolResult {

    @SerializedName("status")
    @Expose
    private Integer result;

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }
}
