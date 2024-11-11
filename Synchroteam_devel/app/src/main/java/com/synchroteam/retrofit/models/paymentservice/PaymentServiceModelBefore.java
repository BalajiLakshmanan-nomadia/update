package com.synchroteam.retrofit.models.paymentservice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Trident on 5/5/17.
 */

public class PaymentServiceModelBefore {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("msg")
    @Expose
    private String msg;

    @SerializedName("apiKeyPublic")
    @Expose
    private String apiKeyPublic;

    @SerializedName("paymentIntentId")
    @Expose
    private String paymentIntentId;

    @SerializedName("clientSecret")
    @Expose
    private String clientSecret;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getApiKeyPublic() {
        return apiKeyPublic;
    }

    public void setApiKeyPublic(String apiKeyPublic) {
        this.apiKeyPublic = apiKeyPublic;
    }

    public String getPaymentIntentId() {
        return paymentIntentId;
    }

    public void setPaymentIntentId(String paymentIntentId) {
        this.paymentIntentId = paymentIntentId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}