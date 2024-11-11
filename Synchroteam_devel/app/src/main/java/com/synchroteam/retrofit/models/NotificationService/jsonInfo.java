package com.synchroteam.retrofit.models.NotificationService;

/**
 * Created by Trident on 6/21/2017.
 */

public class jsonInfo {
    private int idClient;
    private int idSite;
    private int idEquipement;
    private int idJobType;
    private int idTech;
    private String startedScheduledDateTime;
    private String completedScheduledDateTime;
    private String startedRealisedDateTime;
    private String completedRealisedDateTime;

    public int getIdTech() {
        return idTech;
    }

    public void setIdTech(int idTech) {
        this.idTech = idTech;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public void setIdSite(int idSite) {
        this.idSite = idSite;
    }

    public void setIdEquipement(int idEquipement) {
        this.idEquipement = idEquipement;
    }

    public void setIdJobType(int idJobType) {
        this.idJobType = idJobType;
    }

    public void setStartedScheduledDateTime(String startedScheduledDateTime) {
        this.startedScheduledDateTime = startedScheduledDateTime;
    }

    public void setCompletedScheduledDateTime(String completedScheduledDateTime) {
        this.completedScheduledDateTime = completedScheduledDateTime;
    }

    public void setStartedRealisedDateTime(String startedRealisedDateTime) {
        this.startedRealisedDateTime = startedRealisedDateTime;
    }

    public void setCompletedRealisedDateTime(String completedRealisedDateTime) {
        this.completedRealisedDateTime = completedRealisedDateTime;
    }

    public int getIdClient() {
        return idClient;
    }

    public int getIdSite() {
        return idSite;
    }

    public int getIdEquipement() {
        return idEquipement;
    }

    public int getIdJobType() {
        return idJobType;
    }

    public String getStartedScheduledDateTime() {
        return startedScheduledDateTime;
    }

    public String getCompletedScheduledDateTime() {
        return completedScheduledDateTime;
    }

    public String getStartedRealisedDateTime() {
        return startedRealisedDateTime;
    }

    public String getCompletedRealisedDateTime() {
        return completedRealisedDateTime;
    }
}
