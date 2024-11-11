package com.synchroteam.dao;

/**
 * This class contains alter & drop commands for script version 4.1<br></br><br></br>
 * If an user updates from 4.1 to 4.2, this commands will be executed.
 * <p>
 * Created by Trident
 */
public class TablesUpdated42 {


    private String tref_modele_famille_1;
    private String tref_modele_famille_2;
    private String tref_modele_famille_3;

    private String tref_modele_item_1;
    private String t_saisie_rapport_1;
    private String t_saisie_rapport_2;
    private String t_saisie_rapport_3;

    private String t_intervention;

    private String t_photos_pda;

    private String t_piece_demande;

    private String tref_modele_bloc;

    private String t_saisie_bloc;

    /**
     * Instantiate upgraded table
     */
    public TablesUpdated42() {

        //--------------------------------------------QUERIES...STARTS...HERE---------------------------------------

        tref_modele_famille_1 = "ALTER TABLE TREF_MODELE_FAMILLE ADD FL_SHARED BIT NOT NULL DEFAULT 0";
        tref_modele_famille_2 = "ALTER TABLE TREF_MODELE_FAMILLE ADD FL_TABLE BIT NOT NULL DEFAULT 0";
        tref_modele_famille_3 = "ALTER TABLE TREF_MODELE_FAMILLE ADD FL_PUBLIE BIT NOT NULL DEFAULT 0";

        tref_modele_item_1 = "ALTER TABLE TREF_MODELE_ITEM ADD DT_CREATED TIMESTAMP NOT NULL DEFAULT CURRENT TIMESTAMP";

        t_saisie_rapport_1 = "ALTER TABLE T_SAISIE_RAPPORT ADD NM_MODELE_ITEM VARCHAR(1024) NULL";
        t_saisie_rapport_2 = "ALTER TABLE T_SAISIE_RAPPORT ADD ITERATION SMALLINT NOT NULL DEFAULT 0";
        t_saisie_rapport_3 = "ALTER TABLE T_SAISIE_RAPPORT ALTER PRIMARY KEY ( ID_INTERVENTION ASC, ID_MODELE_ITEM ASC, ITERATION ASC  )";

        t_intervention = "ALTER TABLE DBA.T_INTERVENTIONS ADD DT_CREATE TIMESTAMP NOT NULL DEFAULT '1900-01-01 00:00:00.000000'";

        t_photos_pda = "ALTER TABLE DBA.T_PHOTOS_PDA ADD ITERATION INTEGER NOT NULL DEFAULT 0";

        t_piece_demande = "ALTER TABLE DBA.T_PIECE_DEMANDE ADD FL_URGENT INTEGER NULL DEFAULT 0";

        tref_modele_bloc = "CREATE TABLE TREF_MODELE_BLOC ("
                + "ID_CUSTOMER INTEGER NOT NULL,"
                + "ID_MODELE_RAPPORT INTEGER NOT NULL,"
                + "ID_MODELE_FAMILLE INTEGER NOT NULL,"
                + "POS SMALLINT NOT NULL DEFAULT 0,"
                + "MIN SMALLINT NOT NULL DEFAULT 0,"
                + "MAX SMALLINT NOT NULL DEFAULT 0,"
                + "FL_EQUIP BIT NOT NULL DEFAULT 0,"
                + "DT_SUPPR TIMESTAMP NULL,"
                + "PRIMARY KEY ( ID_MODELE_RAPPORT ASC, ID_MODELE_FAMILLE ASC ))";

        t_saisie_bloc = "CREATE TABLE T_SAISIE_BLOC ("
                + "ID_CUSTOMER INTEGER NOT NULL,"
                + "ID_MODELE_RAPPORT INTEGER NOT NULL,"
                + "ID_MODELE_FAMILLE INTEGER NOT NULL,"
                + "ID_INTERVENTION VARCHAR(160) NOT NULL,"
                + "ID_EQUIPEMENT_CLIENT INTEGER NOT NULL DEFAULT 0,"
                + "ITERATION SMALLINT NOT NULL DEFAULT 0,"
                + "NM_MODELE_FAMILLE VARCHAR(1024) NULL,"
                + "OBLIGATOIRE BIT NOT NULL DEFAULT 1,"
                + "POS SMALLINT NOT NULL DEFAULT 0,"
                + "DT_CREATED TIMESTAMP NOT NULL DEFAULT CURRENT TIMESTAMP,"
                + "PRIMARY KEY ( ID_MODELE_FAMILLE ASC, ID_INTERVENTION ASC, ITERATION ASC ))";

        //--------------------------------------------QUERIES...ENDS...HERE-----------------------------------------

    }

    public String getTref_modele_famille_1() {
        return tref_modele_famille_1;
    }

    public String getTref_modele_famille_2() {
        return tref_modele_famille_2;
    }

    public String getTref_modele_famille_3() {
        return tref_modele_famille_3;
    }

    public String getTref_modele_item_1() {
        return tref_modele_item_1;
    }

    public String getT_saisie_rapport_1() {
        return t_saisie_rapport_1;
    }

    public String getT_saisie_rapport_2() {
        return t_saisie_rapport_2;
    }

    public String getT_saisie_rapport_3() {
        return t_saisie_rapport_3;
    }

    public String getT_intervention() {
        return t_intervention;
    }

    public String getTref_modele_bloc() {
        return tref_modele_bloc;
    }

    public String getT_photos_pda() {
        return t_photos_pda;
    }

    public String getT_piece_demande() {
        return t_piece_demande;
    }

    public String getT_saisie_bloc() {
        return t_saisie_bloc;
    }
}
