package com.synchroteam.retrofit.models.JobPoolService;

/**
 * Created by Trident on 6/21/2017.
 */

public class JobPoolRequest {
    private int idUser;
    private String idJob;


    public int getIdUser() {
        return idUser;
    }

    public String getIdJob() {
        return idJob;
    }

    public JobPoolRequest(int idUser, String idJob) {
        this.idUser = idUser;
        this.idJob = idJob;

    }
}
