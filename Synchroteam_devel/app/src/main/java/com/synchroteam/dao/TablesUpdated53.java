package com.synchroteam.dao;


/**
 * This class contains alter & drop commands for script version 51<br></br><br></br>
 * If an user updates from 49 to 51, this commands will be executed.
 * <p>
 * Created by Trident
 */
public class TablesUpdated53 {


    private String alter_T_FACTURES_1;
    private String alter_T_FACTURES_2;
    private String alter_T_FACTURES_3;
    /**
     * create table T_INCIDENTS
     */
    private String t_incidents;
    /**
     * create table T_INCIDENTS_LOG
     */
    private String t_incidents_log;



    public TablesUpdated53() {
        alter_T_FACTURES_1 = "ALTER TABLE T_FACTURES ADD DISCOUNT DECIMAL(20, 4) NOT NULL DEFAULT 0";
        alter_T_FACTURES_2 = "ALTER TABLE T_FACTURES ADD DISCOUNTPERCENT BIT NOT NULL DEFAULT 1";
        alter_T_FACTURES_3 = "ALTER TABLE T_FACTURES ADD DESCRIPTION VARCHAR(16000) NULL";


        t_incidents = "CREATE TABLE T_INCIDENTS (" +
                " ID_INCIDENT UNIQUEIDENTIFIER NOT NULL DEFAULT newid()," +
                " ID_CUSTOMER INTEGER NOT NULL," +
                " ID_CONTRACT_SLA UNIQUEIDENTIFIER NULL," +
                " ID_INTERVENTION VARCHAR(40) NULL," +
                " DESCRIPTION VARCHAR(1024) NULL," +
                " DT_DECLARE TIMESTAMP NOT NULL DEFAULT CURRENT TIMESTAMP," +
                " DELAY_OWN INTEGER NULL," +
                " DELAY_START INTEGER NULL," +
                " DELAY_RESOLVE INTEGER NULL," +
                " FREEZE_START INTEGER NULL," +
                " FREEZE_RESOLVE INTEGER NULL," +
                " STATUS VARCHAR(20) NULL," +
                " FL_FROZEN BIT NULL DEFAULT 0," +
                " DT_OWN TIMESTAMP NULL," +
                " DT_START TIMESTAMP NULL," +
                " DT_RESOLVE TIMESTAMP NULL," +
                " DT_CREATE TIMESTAMP NOT NULL DEFAULT CURRENT TIMESTAMP," +
                " DT_MODIF TIMESTAMP NULL," +
                " DT_SUPPR TIMESTAMP NULL," +
                " ID_AUTO INTEGER NOT NULL DEFAULT 0," +
                " ID_CLIENT INTEGER NOT NULL," +
                " ID_SITE INTEGER NULL," +
                " ID_EQUIPMENT INTEGER NULL," +
                " PRIMARY KEY ( ID_INCIDENT ASC ))";

        t_incidents_log = "CREATE TABLE T_INCIDENTS_LOG (" +
                " ID_INCIDENT_LOG UNIQUEIDENTIFIER NOT NULL DEFAULT newid()," +
                " ID_INCIDENT UNIQUEIDENTIFIER NOT NULL," +
                " ID_CUSTOMER INTEGER NOT NULL," +
                " ID_INTERVENTION VARCHAR(40) NOT NULL," +
                " ID_USER INTEGER NOT NULL," +
                " ACTION VARCHAR(20) NOT NULL," +
                " ACTION_DATE TIMESTAMP NOT NULL," +
                " ACTION_COMMENT VARCHAR(255) NULL," +
                " DT_CREATE TIMESTAMP NOT NULL DEFAULT CURRENT TIMESTAMP," +
                " DT_MODIF TIMESTAMP NULL," +
                " DT_SUPPR TIMESTAMP NULL," +
                " ACTION_DURATION INTEGER NULL," +
                " PRIMARY KEY ( ID_INCIDENT_LOG ASC ))";

    }

    public String getT_incidents() {
        return t_incidents;
    }

    public void setT_incidents(String t_incidents) {
        this.t_incidents = t_incidents;
    }

    public String getT_incidents_log() {
        return t_incidents_log;
    }

    public void setT_incidents_log(String t_incidents_log) {
        this.t_incidents_log = t_incidents_log;
    }

    public String getAlter_T_FACTURES_1() {
        return alter_T_FACTURES_1;
    }

    public void setAlter_T_FACTURES_1(String alter_T_FACTURES_1) {
        this.alter_T_FACTURES_1 = alter_T_FACTURES_1;
    }

    public String getAlter_T_FACTURES_2() {
        return alter_T_FACTURES_2;
    }

    public void setAlter_T_FACTURES_2(String alter_T_FACTURES_2) {
        this.alter_T_FACTURES_2 = alter_T_FACTURES_2;
    }

    public String getAlter_T_FACTURES_3() {
        return alter_T_FACTURES_3;
    }

    public void setAlter_T_FACTURES_3(String alter_T_FACTURES_3) {
        this.alter_T_FACTURES_3 = alter_T_FACTURES_3;
    }

}
