package com.synchroteam.dao;

/**
 * Created by user on 25/4/17.
 */

public class TablesUpdated45 {

    private String t_payments;
    private String t_payments_log;
    private String c_connections;

    private String alter_TREF_PIECES_PRIX_PIECE;

    private String alter_T_LIGNES_FACTURE_PRIX_UNITAIRE;
    private String alter_T_LIGNES_FACTURE_TOTAL_HT;
    private String alter_T_LIGNES_FACTURE_VAL_TVA;
    private String alter_T_LIGNES_FACTURE_TOTAL_TTC;

    private String alter_T_FACTURES_TOTAL_HT;
    private String alter_T_FACTURES_VAL_TVA;
    private String alter_T_FACTURES_TOTAL_TTC;

    private String alter_T_PAYMENTS_RECEIVED;
    private String alter_T_PAYMENTS_REFUNDED;

    private String alter_T_PAYMENTS_LOG_AMOUNT;

    public TablesUpdated45() {

        t_payments = "CREATE TABLE T_PAYMENTS (" +
                "ID_PAYMENT UNIQUEIDENTIFIER NOT NULL DEFAULT newid()," +
                "ID_REMOTE VARCHAR(512) NOT NULL," +
                "MODE VARCHAR(40) NULL," +
                "RECEIVED DECIMAL(13,2) NOT NULL DEFAULT 0," +
                "REFUNDED DECIMAL(13,2) NOT NULL DEFAULT 0," +
                "CHARGE_ID VARCHAR(1020) NULL," +
                "NOTE VARCHAR(1020) NULL," +
                "DT_CREATED datetime NOT NULL," +
                "CONSTRAINT PK_T_PAYMENTS PRIMARY KEY ( ID_PAYMENT ASC )" +
                ")";

        t_payments_log = "CREATE TABLE T_PAYMENTS_LOG (" +
                "ID_PAYMENT_LOG UNIQUEIDENTIFIER NOT NULL DEFAULT newid()," +
                "ID_PAYMENT UNIQUEIDENTIFIER NOT NULL," +
                "ID_REMOTE VARCHAR(512) NOT NULL," +
                "AMOUNT DECIMAL(13,2) NOT NULL DEFAULT 0," +
                "DT_CREATED datetime NOT NULL," +
                "CONSTRAINT PK_T_PAYMENTS_LOG PRIMARY KEY ( ID_PAYMENT_LOG ASC )" +
                ")";

        c_connections = "CREATE TABLE C_CONNECTIONS (" +
                "ID_CONNECTION INTEGER NOT NULL DEFAULT AUTOINCREMENT," +
                "JSON_AUTH VARCHAR(8192) NULL," +
                "JSON_SETTINGS VARCHAR(8192) NULL," +
                "FL_ACTIVE BIT NOT NULL DEFAULT 0," +
                "PRIMARY KEY ( ID_CONNECTION ASC )" +
                ")";

        // ****** new changes *******

        alter_TREF_PIECES_PRIX_PIECE = "ALTER TABLE TREF_PIECES ALTER PRIX_PIECE DECIMAL(12,2) NULL";

        alter_T_LIGNES_FACTURE_PRIX_UNITAIRE = "ALTER TABLE T_LIGNES_FACTURE ALTER PRIX_UNITAIRE DECIMAL(12,2) NOT NULL DEFAULT 0";
        alter_T_LIGNES_FACTURE_TOTAL_HT = "ALTER TABLE T_LIGNES_FACTURE ALTER TOTAL_HT DECIMAL(20,4) NOT NULL DEFAULT 0";
        alter_T_LIGNES_FACTURE_VAL_TVA = "ALTER TABLE T_LIGNES_FACTURE ALTER VAL_TVA DECIMAL(20,4) NOT NULL DEFAULT 0";
        alter_T_LIGNES_FACTURE_TOTAL_TTC = "ALTER TABLE T_LIGNES_FACTURE ALTER TOTAL_TTC DECIMAL(20,4) NOT NULL DEFAULT 0";

        alter_T_FACTURES_TOTAL_HT = "ALTER TABLE T_FACTURES ALTER TOTAL_HT DECIMAL(20,4) NOT NULL DEFAULT 0";
        alter_T_FACTURES_VAL_TVA = "ALTER TABLE T_FACTURES ALTER VAL_TVA DECIMAL(20,4) NOT NULL DEFAULT 0";
        alter_T_FACTURES_TOTAL_TTC = "ALTER TABLE T_FACTURES ALTER TOTAL_TTC DECIMAL(20,4) NOT NULL DEFAULT 0";

        alter_T_PAYMENTS_RECEIVED = "ALTER TABLE T_PAYMENTS ALTER RECEIVED DECIMAL(18,2) NOT NULL DEFAULT 0";
        alter_T_PAYMENTS_REFUNDED = "ALTER TABLE T_PAYMENTS ALTER REFUNDED DECIMAL(18,2) NOT NULL DEFAULT 0";

        alter_T_PAYMENTS_LOG_AMOUNT = "ALTER TABLE T_PAYMENTS_LOG ALTER AMOUNT DECIMAL(18,2) NOT NULL DEFAULT 0";

    }

    public String getT_payments() {
        return t_payments;
    }

    public String getT_payments_log() {
        return t_payments_log;
    }

    public String getC_connections() {
        return c_connections;
    }

    public String getAlter_TREF_PIECES_PRIX_PIECE() {
        return alter_TREF_PIECES_PRIX_PIECE;
    }

    public String getAlter_T_LIGNES_FACTURE_PRIX_UNITAIRE() {
        return alter_T_LIGNES_FACTURE_PRIX_UNITAIRE;
    }

    public String getAlter_T_LIGNES_FACTURE_TOTAL_HT() {
        return alter_T_LIGNES_FACTURE_TOTAL_HT;
    }

    public String getAlter_T_LIGNES_FACTURE_VAL_TVA() {
        return alter_T_LIGNES_FACTURE_VAL_TVA;
    }

    public String getAlter_T_LIGNES_FACTURE_TOTAL_TTC() {
        return alter_T_LIGNES_FACTURE_TOTAL_TTC;
    }

    public String getAlter_T_FACTURES_TOTAL_HT() {
        return alter_T_FACTURES_TOTAL_HT;
    }

    public String getAlter_T_FACTURES_VAL_TVA() {
        return alter_T_FACTURES_VAL_TVA;
    }

    public String getAlter_T_FACTURES_TOTAL_TTC() {
        return alter_T_FACTURES_TOTAL_TTC;
    }

    public String getAlter_T_PAYMENTS_RECEIVED() {
        return alter_T_PAYMENTS_RECEIVED;
    }

    public String getAlter_T_PAYMENTS_REFUNDED() {
        return alter_T_PAYMENTS_REFUNDED;
    }

    public String getAlter_T_PAYMENTS_LOG_AMOUNT() {
        return alter_T_PAYMENTS_LOG_AMOUNT;
    }

}