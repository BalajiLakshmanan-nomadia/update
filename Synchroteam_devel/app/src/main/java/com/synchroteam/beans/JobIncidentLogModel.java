package com.synchroteam.beans;

public class JobIncidentLogModel {

    private String idIncidentLog;
    private String idIncident;
    private int idCustomer;
    private String idIntervention;
    private int idUser;
    private String action;
    private String actionDate;
    private String actionComment;
    private int actionDuration;
    private String dtCreate;
    private String dtModif;
    private String dtSuppr;

    public JobIncidentLogModel(String idIncidentLog, String idIncident, int idCustomer,
                               String idIntervention, int idUser, String action, String actionDate,
                               String actionComment, int actionDuration) {
        this.idIncidentLog = idIncidentLog;
        this.idIncident = idIncident;
        this.idCustomer = idCustomer;
        this.idIntervention = idIntervention;
        this.idUser = idUser;
        this.action = action;
        this.actionDate = actionDate;
        this.actionComment = actionComment;
        this.actionDuration = actionDuration;
        this.dtCreate = dtCreate;
        this.dtModif = dtModif;
        this.dtSuppr = dtSuppr;
    }

    public String getIdIncidentLog() {
        return idIncidentLog;
    }

    public void setIdIncidentLog(String idIncidentLog) {
        this.idIncidentLog = idIncidentLog;
    }

    public String getIdIncident() {
        return idIncident;
    }

    public void setIdIncident(String idIncident) {
        this.idIncident = idIncident;
    }

    public int getIdCustomer() {
        return idCustomer;
    }

    public void setIdCustomer(int idCustomer) {
        this.idCustomer = idCustomer;
    }

    public String getIdIntervention() {
        return idIntervention;
    }

    public void setIdIntervention(String idIntervention) {
        this.idIntervention = idIntervention;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActionDate() {
        return actionDate;
    }

    public void setActionDate(String actionDate) {
        this.actionDate = actionDate;
    }

    public String getActionComment() {
        return actionComment;
    }

    public void setActionComment(String actionComment) {
        this.actionComment = actionComment;
    }

    public int getActionDuration() {
        return actionDuration;
    }

    public void setActionDuration(int actionDuration) {
        this.actionDuration = actionDuration;
    }

    public String getDtCreate() {
        return dtCreate;
    }

    public void setDtCreate(String dtCreate) {
        this.dtCreate = dtCreate;
    }

    public String getDtModif() {
        return dtModif;
    }

    public void setDtModif(String dtModif) {
        this.dtModif = dtModif;
    }

    public String getDtSuppr() {
        return dtSuppr;
    }

    public void setDtSuppr(String dtSuppr) {
        this.dtSuppr = dtSuppr;
    }
}
