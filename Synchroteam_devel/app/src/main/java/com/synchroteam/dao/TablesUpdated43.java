package com.synchroteam.dao;


/**
 * This class contains alter & drop commands for script version 4.3<br></br><br></br>
 * If an user updates from 4.2 to 4.3, this commands will be executed.
 * <p>
 * Created by Trident
 */
public class TablesUpdated43 {

    private String tref_gestion_acces_1;
    private String tref_gestion_acces_2;
    private String tref_gestion_acces_3;

    private String t_stock_pieces;

    private String tref_modele_item;

    /**
     * Instantiate upgraded table
     */
    public TablesUpdated43(){

        tref_gestion_acces_1 = "ALTER TABLE TREF_GESTION_ACCES ADD FL_STOCK_TAKEFROM INTEGER NULL DEFAULT 0";
        tref_gestion_acces_2 = "ALTER TABLE TREF_GESTION_ACCES ADD FL_STOCK_SENDTO INTEGER NULL DEFAULT 0";
        tref_gestion_acces_3 = "ALTER TABLE TREF_GESTION_ACCES ADD FL_STOCK_REQUESTFROM INTEGER NULL DEFAULT 0";

        t_stock_pieces = "CREATE TABLE T_STOCK_PIECES (" +
                "ID_STOCK_PIECE UNIQUEIDENTIFIER NOT NULL DEFAULT newid()," +
                "ID_STOCK UNIQUEIDENTIFIER NOT NULL," +
                "ID_PIECE INTEGER NOT NULL," +
                "QUANTITY INTEGER NOT NULL DEFAULT 0," +
                "PRIMARY KEY ( ID_STOCK_PIECE ASC ))";

        tref_modele_item = "ALTER TABLE TREF_MODELE_ITEM ADD FL_PRIVATE INTEGER NULL DEFAULT 0";

    }

    public String getTref_gestion_acces_1() {
        return tref_gestion_acces_1;
    }

    public String getTref_gestion_acces_2() {
        return tref_gestion_acces_2;
    }

    public String getTref_gestion_acces_3() {
        return tref_gestion_acces_3;
    }

    public String getT_stock_pieces() {
        return t_stock_pieces;
    }

    public String getTref_modele_item() {
        return tref_modele_item;
    }
}
