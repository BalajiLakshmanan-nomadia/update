package com.synchroteam.dao;


/**
 * This class contains alter & drop commands for script version 54<br></br><br></br>
 * If an user updates from 52 to 54, this commands will be executed.
 * <p>
 * Created by Trident
 */
public class TablesUpdated54 {


    private String alter_T_TEMPS_INTERV_1;
    private String alter_T_TEMPS_INTERV_2;
    private String alter_T_INTERVENTIONS_1;
    private String alter_T_CLIENTS_1;
    private String alter_T_SITES_CLIENTS_1;



    public TablesUpdated54() {
        //ALTER TABLE "T_CLIENTS" ADD "ADR_CLIENT_PROVINCE" varchar(1024) NULL;
        //ALTER TABLE "T_SITES_CLIENTS" ADD "ADR_SITE_PROVINCE" varchar(1024) NULL;
        alter_T_INTERVENTIONS_1 = "ALTER TABLE T_INTERVENTIONS ADD ADR_INTERV_PROVINCE VARCHAR(1024) NULL";
        alter_T_CLIENTS_1 = "ALTER TABLE T_CLIENTS ADD ADR_CLIENT_PROVINCE VARCHAR(1024) NULL";
        alter_T_SITES_CLIENTS_1 = "ALTER TABLE T_SITES_CLIENTS ADD ADR_SITE_PROVINCE VARCHAR(1024) NULL";
        alter_T_TEMPS_INTERV_1 = "ALTER TABLE T_TEMPS_INTERV ADD FL_SCHEDULED INTEGER NOT NULL";
        alter_T_TEMPS_INTERV_2 = "ALTER TABLE T_TEMPS_INTERV ADD FL_AUXILIARY INTEGER NOT NULL";


    }

    public String getAlter_T_TEMPS_INTERV_1() {
        return alter_T_TEMPS_INTERV_1;
    }

    public String getAlter_T_TEMPS_INTERV_2() {
        return alter_T_TEMPS_INTERV_2;
    }

    public String getAlter_T_INTERVENTIONS_1() {
        return alter_T_INTERVENTIONS_1;
    }

    public String getAlter_T_CLIENTS_1() {
        return alter_T_CLIENTS_1;
    }

    public String getAlter_T_SITES_CLIENTS_1() {
        return alter_T_SITES_CLIENTS_1;
    }
}
