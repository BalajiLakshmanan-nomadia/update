package com.synchroteam.beans;

public class JobIncidentModel {

    private String idIncident;
    private int idCustomer;
    private String idContract;
    private String idIntervention;
    private String description;
    private String dtDeclare;
    private int delayOwn;
    private int delayStart;
    private int delayResolve;
    private int freezeStart;
    private int freezeResolve;
    private String status;
    private boolean flFrozen;
    private String dtOwn;
    private String dtStart;
    private String dtResolve;
    private String dtCreate;
    private String dtModif;
    private String dtSuppr;
    private int idAuto;
    private int idClient;
    private int idSite;
    private int idEquipment;

    public JobIncidentModel(String idIncident, int idCustomer, String idContract, String idIntervention,
                            String description, String dtDeclare, int delayOwn, int delayStart,
                            int delayResolve, int freezeStart, int freezeResolve, String status,
                            boolean flFrozen, String dtOwn, String dtStart, String dtResolve, String dtCreate,
                            String dtModif, String dtSuppr, int idAuto, int idClient, int idSite, int idEquipment) {
        this.idIncident = idIncident;
        this.idCustomer = idCustomer;
        this.idContract = idContract;
        this.idIntervention = idIntervention;
        this.description = description;
        this.dtDeclare = dtDeclare;
        this.delayOwn = delayOwn;
        this.delayStart = delayStart;
        this.delayResolve = delayResolve;
        this.freezeStart = freezeStart;
        this.freezeResolve = freezeResolve;
        this.status = status;
        this.flFrozen = flFrozen;
        this.dtOwn = dtOwn;
        this.dtStart = dtStart;
        this.dtResolve = dtResolve;
        this.dtCreate = dtCreate;
        this.dtModif = dtModif;
        this.dtSuppr = dtSuppr;
        this.idAuto = idAuto;
        this.idClient = idClient;
        this.idSite = idSite;
        this.idEquipment = idEquipment;
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

    public String getIdContract() {
        return idContract;
    }

    public void setIdContract(String idContract) {
        this.idContract = idContract;
    }

    public String getIdIntervention() {
        return idIntervention;
    }

    public void setIdIntervention(String idIntervention) {
        this.idIntervention = idIntervention;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDtDeclare() {
        return dtDeclare;
    }

    public void setDtDeclare(String dtDeclare) {
        this.dtDeclare = dtDeclare;
    }

    public int getDelayOwn() {
        return delayOwn;
    }

    public void setDelayOwn(int delayOwn) {
        this.delayOwn = delayOwn;
    }

    public int getDelayStart() {
        return delayStart;
    }

    public void setDelayStart(int delayStart) {
        this.delayStart = delayStart;
    }

    public int getDelayResolve() {
        return delayResolve;
    }

    public void setDelayResolve(int delayResolve) {
        this.delayResolve = delayResolve;
    }

    public int getFreezeStart() {
        return freezeStart;
    }

    public void setFreezeStart(int freezeStart) {
        this.freezeStart = freezeStart;
    }

    public int getFreezeResolve() {
        return freezeResolve;
    }

    public void setFreezeResolve(int freezeResolve) {
        this.freezeResolve = freezeResolve;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isFlFrozen() {
        return flFrozen;
    }

    public void setFlFrozen(boolean flFrozen) {
        this.flFrozen = flFrozen;
    }

    public String getDtOwn() {
        return dtOwn;
    }

    public void setDtOwn(String dtOwn) {
        this.dtOwn = dtOwn;
    }

    public String getDtStart() {
        return dtStart;
    }

    public void setDtStart(String dtStart) {
        this.dtStart = dtStart;
    }

    public String getDtResolve() {
        return dtResolve;
    }

    public void setDtResolve(String dtResolve) {
        this.dtResolve = dtResolve;
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

    public int getIdAuto() {
        return idAuto;
    }

    public void setIdAuto(int idAuto) {
        this.idAuto = idAuto;
    }

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

    public int getIdEquipment() {
        return idEquipment;
    }

    public void setIdEquipment(int idEquipment) {
        this.idEquipment = idEquipment;
    }
}
