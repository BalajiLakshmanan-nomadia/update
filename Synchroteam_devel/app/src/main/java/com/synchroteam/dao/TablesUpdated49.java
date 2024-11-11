package com.synchroteam.dao;


/**
 * This class contains alter & drop commands for script version 4.3<br></br><br></br>
 * If an user updates from 4.2 to 4.3, this commands will be executed.
 * <p>
 * Created by Trident
 */
public class TablesUpdated49 {


    private String alter_T_PIECE_SERIALS_1;
    private String alter_T_PIECE_SERIALS_2;
    private String alter_T_PIECE_SERIALS_3;
    private String alter_T_PIECE_SERIALS_4;
    private String alter_T_PIECE_SERIALS_5;

    private String alter_T_SORTIE_PIECE_1;
    private String alter_T_SORTIE_PIECE_2;
    private String alter_T_SORTIE_PIECE_3;
    private String alter_T_SORTIE_PIECE_4;
    private String alter_T_SORTIE_PIECE_5;
    private String alter_T_SORTIE_PIECE_6;

    /**
     * create table T_REPRISE_PIECE
     */
    private String t_reprise_piece;

    private String insert_T_REPRISE_PIECE;


    /**
     * Instantiate upgraded table
     */
    public TablesUpdated49() {

        alter_T_SORTIE_PIECE_1 = "ALTER TABLE T_SORTIE_PIECE ADD SERIAL_REPRISE VARCHAR(256) NULL";
        alter_T_SORTIE_PIECE_2 = "ALTER TABLE T_SORTIE_PIECE ADD QUANTITE_REPRISE INTEGER NULL DEFAULT 0";

        alter_T_PIECE_SERIALS_1 = "ALTER TABLE T_PIECE_SERIALS ADD STATUS VARCHAR(20) NULL DEFAULT 'ok'";
        alter_T_PIECE_SERIALS_2 = "ALTER TABLE T_PIECE_SERIALS ADD ID_CLIENT INTEGER NULL";
        alter_T_PIECE_SERIALS_3 = "ALTER TABLE T_PIECE_SERIALS ADD ID_SITE_CLIENT INTEGER NULL";
        alter_T_PIECE_SERIALS_4 = "ALTER TABLE T_PIECE_SERIALS ADD ID_EQUIPEMENT_CLIENT INTEGER NULL";
        alter_T_PIECE_SERIALS_5 = "ALTER TABLE T_PIECE_SERIALS ADD CONSTRAINT T_INTERVENTIONS " +
                "FOREIGN KEY ( ID_INTERVENTION ASC ) REFERENCES T_INTERVENTIONS ( ID_INTERVENTION )";

        t_reprise_piece = " CREATE TABLE T_REPRISE_PIECE (" +
                "ID_INTERVENTION VARCHAR(160) NOT NULL," +
                "ID_PIECE INTEGER NOT NULL," +
                "QUANTITE_REPRISE INTEGER NOT NULL," +
                "SERIAL_REPRISE VARCHAR(1000) NULL," +
                "DT_SUPPR datetime NULL," +
                "DT_MODIF datetime NOT NULL DEFAULT CURRENT TIMESTAMP," +
                "PRIMARY KEY ( ID_INTERVENTION ASC, ID_PIECE ASC )," +
                "FOREIGN KEY ( ID_PIECE ASC ) REFERENCES TREF_PIECES ( ID_PIECE )," +
                "FOREIGN KEY ( ID_INTERVENTION ASC ) REFERENCES T_INTERVENTIONS ( ID_INTERVENTION )" +
                ")";

        insert_T_REPRISE_PIECE = "INSERT INTO T_REPRISE_PIECE (ID_INTERVENTION,ID_PIECE,QUANTITE_REPRISE,SERIAL_REPRISE) " +
                "SELECT ID_INTERVENTION,ID_PIECE,QUANTITE_REPRISE,SERIAL_REPRISE FROM T_SORTIE_PIECE WHERE " +
                "T_SORTIE_PIECE.QUANTITE_REPRISE > 0";

        /* REMOVE COLUMNS RELATED TO REMOVING PARTS FROM T_SORTIE_PIECE */
        alter_T_SORTIE_PIECE_3 = "ALTER TABLE T_SORTIE_PIECE DROP QUANTITE_REPRISE";
        alter_T_SORTIE_PIECE_4 = "ALTER TABLE T_SORTIE_PIECE DROP SERIAL_REPRISE";
        alter_T_SORTIE_PIECE_5 = "ALTER TABLE T_SORTIE_PIECE ADD SERIAL_SORTIE VARCHAR(500) NULL";
        alter_T_SORTIE_PIECE_6 = "ALTER TABLE T_SORTIE_PIECE ALTER COLUMN SERIAL_SORTIE VARCHAR(1500) NULL";

    }

    public String getAlter_T_PIECE_SERIALS_1() {
        return alter_T_PIECE_SERIALS_1;
    }

    public String getAlter_T_PIECE_SERIALS_2() {
        return alter_T_PIECE_SERIALS_2;
    }

    public String getAlter_T_PIECE_SERIALS_3() {
        return alter_T_PIECE_SERIALS_3;
    }

    public String getAlter_T_PIECE_SERIALS_4() {
        return alter_T_PIECE_SERIALS_4;
    }

    public String getAlter_T_SORTIE_PIECE_1() {
        return alter_T_SORTIE_PIECE_1;
    }

    public String getAlter_T_SORTIE_PIECE_2() {
        return alter_T_SORTIE_PIECE_2;
    }

    public String getAlter_T_SORTIE_PIECE_3() {
        return alter_T_SORTIE_PIECE_3;
    }

    public String getAlter_T_SORTIE_PIECE_4() {
        return alter_T_SORTIE_PIECE_4;
    }

    public String getAlter_T_SORTIE_PIECE_5() {
        return alter_T_SORTIE_PIECE_5;
    }

    public String getT_reprise_piece() {
        return t_reprise_piece;
    }

    public String getInsert_T_REPRISE_PIECE() {
        return insert_T_REPRISE_PIECE;
    }

    public String getAlter_T_PIECE_SERIALS_5() {
        return alter_T_PIECE_SERIALS_5;
    }
    public String getAlter_T_SORTIE_PIECE_6() {
        return alter_T_SORTIE_PIECE_6;
    }

}
