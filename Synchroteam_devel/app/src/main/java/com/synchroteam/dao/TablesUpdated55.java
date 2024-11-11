package com.synchroteam.dao;
/**
 * This class contains alter & drop commands for script version 54<br></br><br></br>
 * If an user updates from 54 to 55, this commands will be executed.
 * <p>
 * Created by Trident
 */
public class TablesUpdated55 {

        private String alter_TREF_GESTION_ACCES;
        private String alter_TREF_GESTION_ACCES_1;
        private String alter_T_FACTURES;
        private String alter_T_FACTURES_1;
        private String alter_T_USERS;

        private String alter_T_USERS_1;

        private String gps_steps;
        private String gps_steps_index;

    private String alter_T_CLIENTS;
    private String alter_T_SITES_CLIENTS;
    private String alter_T_EQUIPEMENTS_CLIENTS;

        public TablesUpdated55() {

            alter_TREF_GESTION_ACCES = "ALTER TABLE TREF_GESTION_ACCES ADD FL_INV_STRICT BIT NOT NULL DEFAULT 0";
            alter_TREF_GESTION_ACCES_1 = "ALTER TABLE TREF_GESTION_ACCES ADD FL_MOB_LIVE_PICTURE_ONLY INTEGER NOT NULL DEFAULT 0";
            alter_T_FACTURES = "ALTER TABLE T_FACTURES ADD FL_INV_STRICT BIT NOT NULL DEFAULT 0";
//        alter_T_FACTURES_1 = "ALTER TABLE T_FACTURES ALTER COLUMN NO_INT_FACTURE_DEVIS VARCHAR(50) NOT NULL";
            alter_T_FACTURES_1 = "ALTER TABLE T_FACTURES ALTER COLUMN NO_INT_FACTURE_DEVIS INTEGER NOT NULL";
            alter_T_USERS = "ALTER TABLE T_USERS ADD FL_SUBCONTRACTOR BIT NOT NULL DEFAULT 0";

            alter_T_USERS_1 ="ALTER TABLE T_USERS ADD FL_GPS_TRACKED BIT NOT NULL DEFAULT 0";

            alter_T_CLIENTS ="ALTER TABLE T_CLIENTS ADD REF_CUSTOMER VARCHAR(1024) NULL";
            alter_T_SITES_CLIENTS = "ALTER TABLE T_SITES_CLIENTS ADD REF_CUSTOMER VARCHAR(1024) NULL";
            alter_T_EQUIPEMENTS_CLIENTS="ALTER TABLE T_EQUIPEMENTS_CLIENTS ADD REF_CUSTOMER VARCHAR(1024) NULL";
            
//            gps_steps = "CREATE TABLE GPS_STEPS("
//                    +"ID_CUSTOMER INT NOT NULL,"
//                    +"ID_ACTIVITY INTEGER NULL,"
//                    +"ID_JOB VARCHAR(40) NULL,"
//                    +"ID_USER INT NOT NULL,"
//                    +"HORODATAGE TIMESTAMP NOT NULL,"
//                    +"LAT VARCHAR(15) NOT NULL,"
//                    +"LNG VARCHAR(15) NOT NULL,"
//                    +"TYPE VARCHAR(20) NOT NULL,"
//                    +"EVENT VARCHAR(20) NOT NULL,"
//                    +"DATE_INDEX INT NOT NULL,"
//                    +"PRIMARY KEY (ID_USER ASC,HORODATAGE ASC))";
//
//
//            gps_steps_index ="CREATE INDEX GPS_STEPS_ID_CUSTOMER_DATE_INDEX ON GPS_STEPS (ID_CUSTOMER,DATE_INDEX)";
        }

        public String getAlter_TREF_GESTION_ACCES() {
            return alter_TREF_GESTION_ACCES;
        }

    public String getAlter_TREF_GESTION_ACCES_1() {
        return alter_TREF_GESTION_ACCES_1;
    }

    public String getAlter_T_FACTURES() {
            return alter_T_FACTURES;
        }

        public String getAlter_T_FACTURES_1() {
            return alter_T_FACTURES_1;
        }

        public String getAlter_T_USERS() {
            return alter_T_USERS;
        }

        public String getAlter_T_USERS_1(){ return alter_T_USERS_1; }

        public String getGps_steps(){ return  gps_steps;}

        public String getGps_steps_index(){ return  gps_steps_index;}

    public String getAlter_T_CLIENTS() {
        return alter_T_CLIENTS;
    }

    public String getAlter_T_SITES_CLIENTS() {
        return alter_T_SITES_CLIENTS;
    }

    public String getAlter_T_EQUIPEMENTS_CLIENTS() {
        return alter_T_EQUIPEMENTS_CLIENTS;
    }
}

