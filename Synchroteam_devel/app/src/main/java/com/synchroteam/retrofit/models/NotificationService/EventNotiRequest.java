package com.synchroteam.retrofit.models.NotificationService;

/**
 * Created by Trident on 6/21/2017.
 */

public class EventNotiRequest {
    private int IdCustomer;
    private String IdJob;
    private int JobStatus;
    private String EventOrigin;
    private String timestamp;
    jsonInfo jsonInfo;

    private int idClient;
    private int idSite;
    private int idEquipement;
    private int idJobType;
    private int idTech;
    private String startedScheduledDateTime;
    private String completedScheduledDateTime;
    private String startedRealisedDateTime;
    private String completedRealisedDateTime;

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public int getIdSite() {
        return idSite;
    }

    public void setIdSite(int idSite) {
        this.idSite = idSite;
    }

    public int getIdEquipement() {
        return idEquipement;
    }

    public void setIdEquipement(int idEquipement) {
        this.idEquipement = idEquipement;
    }

    public int getIdJobType() {
        return idJobType;
    }

    public void setIdJobType(int idJobType) {
        this.idJobType = idJobType;
    }

    public int getIdTech() {
        return idTech;
    }

    public void setIdTech(int idTech) {
        this.idTech = idTech;
    }

    public String getStartedScheduledDateTime() {
        return startedScheduledDateTime;
    }

    public void setStartedScheduledDateTime(String startedScheduledDateTime) {
        this.startedScheduledDateTime = startedScheduledDateTime;
    }

    public String getCompletedScheduledDateTime() {
        return completedScheduledDateTime;
    }

    public void setCompletedScheduledDateTime(String completedScheduledDateTime) {
        this.completedScheduledDateTime = completedScheduledDateTime;
    }

    public String getStartedRealisedDateTime() {
        return startedRealisedDateTime;
    }

    public void setStartedRealisedDateTime(String startedRealisedDateTime) {
        this.startedRealisedDateTime = startedRealisedDateTime;
    }

    public String getCompletedRealisedDateTime() {
        return completedRealisedDateTime;
    }

    public void setCompletedRealisedDateTime(String completedRealisedDateTime) {
        this.completedRealisedDateTime = completedRealisedDateTime;
    }

    public int getIdCustomer() {
        return IdCustomer;
    }

    public String getIdJob() {
        return IdJob;
    }

    public int getJobStatus() {
        return JobStatus;
    }

    public String getEventOrigin() {
        return EventOrigin;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public EventNotiRequest(int IdCustomer, String IdJob, int JobStatus,
                            String EventOrigin, String timestamp,jsonInfo jsonInfo) {
        this.IdCustomer = IdCustomer;
        this.IdJob = IdJob;
        this.JobStatus = JobStatus;
        this.EventOrigin = EventOrigin;
        this.timestamp = timestamp;
        this.jsonInfo=jsonInfo;
    }

    public EventNotiRequest(int idCustomer, String idJob, int jobStatus, String eventOrigin, String timestamp, int idClient, int idSite, int idEquipement, int idJobType, int idTech, String startedScheduledDateTime, String completedScheduledDateTime, String startedRealisedDateTime, String completedRealisedDateTime) {
        IdCustomer = idCustomer;
        IdJob = idJob;
        JobStatus = jobStatus;
        EventOrigin = eventOrigin;
        this.timestamp = timestamp;
        this.idClient = idClient;
        this.idSite = idSite;
        this.idEquipement = idEquipement;
        this.idJobType = idJobType;
        this.idTech = idTech;
        this.startedScheduledDateTime = startedScheduledDateTime;
        this.completedScheduledDateTime = completedScheduledDateTime;
        this.startedRealisedDateTime = startedRealisedDateTime;
        this.completedRealisedDateTime = completedRealisedDateTime;
    }
}
