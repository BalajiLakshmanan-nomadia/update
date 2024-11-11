package com.synchroteam.retrofit.models.JobPoolService;

/**
 * Created by Trident on 6/21/2017.
 */

public class JobPoolScheduleRequest {
    private int idUser;
    private String idJob;
    private String dtStart;
    private String dtEnd;


    public JobPoolScheduleRequest(int idUser, String idJob, String dtStart, String dtEnd) {
        this.idUser = idUser;
        this.idJob = idJob;
        this.dtStart = dtStart;
        this.dtEnd = dtEnd;

    }
}
