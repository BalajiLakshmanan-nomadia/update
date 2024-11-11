package com.synchroteam.dao;


/**
 * This class contains alter & drop commands for script version 51<br></br><br></br>
 * If an user updates from 49 to 51, this commands will be executed.
 * <p>
 * Created by Trident
 */
public class TablesUpdated51 {


    private String alter_T_INTERVENTIONS_1;
    private String alter_T_INTERVENTIONS_2;
    private String alter_T_INTERVENTIONS_3;


    /**
     * create table T_JOB_WINDOWS
     */
    private String t_job_windows;



    /**
     * Instantiate upgraded table
     */
    public TablesUpdated51() {

        alter_T_INTERVENTIONS_1 = "ALTER TABLE T_INTERVENTIONS ADD FL_POOL BIT NOT NULL DEFAULT 0";
        alter_T_INTERVENTIONS_2 = "ALTER TABLE T_INTERVENTIONS ADD DT_PREF datetime NULL";
        alter_T_INTERVENTIONS_3 = "ALTER TABLE T_INTERVENTIONS ADD ID_JOB_WINDOW INTEGER NULL";



        t_job_windows = " CREATE TABLE T_JOB_WINDOWS (" +
                "ID_JOB_WINDOW INTEGER NOT NULL DEFAULT AUTOINCREMENT," +
                "LABEL VARCHAR(50) NULL," +
                "TIME_START TIME NULL," +
                "TIME_END TIME NULL," +
                "PRIMARY KEY ( ID_JOB_WINDOW ASC )" +
                ")";

    }

    public String getAlter_T_INTERVENTIONS_1() {
        return alter_T_INTERVENTIONS_1;
    }

    public String getAlter_T_INTERVENTIONS_2() {
        return alter_T_INTERVENTIONS_2;
    }

    public String getAlter_T_INTERVENTIONS_3() {
        return alter_T_INTERVENTIONS_3;
    }

    public String getT_job_windows() {
        return t_job_windows;
    }
}
