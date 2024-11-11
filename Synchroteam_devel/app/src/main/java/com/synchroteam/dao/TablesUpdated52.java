package com.synchroteam.dao;


/**
 * This class contains alter & drop commands for script version 51<br></br><br></br>
 * If an user updates from 49 to 51, this commands will be executed.
 * <p>
 * Created by Trident
 */
public class TablesUpdated52 {


    private String alter_TREF_GESTION_ACCES_1;
    private String alter_TREF_GESTION_ACCES_2;
    private String alter_TREF_GESTION_ACCES_3;

    private String alter_T_USERS_1;
    private String alter_T_USERS_2;
    private String alter_T_USERS_3;


    private String alter_TREF_PIECES_1;
    private String alter_TREF_PIECES_2;

    private String alter_T_LIGNES_FACTURE_1;
    private String alter_T_LIGNES_FACTURE_2;
    private String alter_T_LIGNES_FACTURE_3;
    private String alter_T_LIGNES_FACTURE_4;
    private String alter_T_LIGNES_FACTURE_5;
    private String alter_T_LIGNES_FACTURE_6;
    private String alter_T_LIGNES_FACTURE_7;
    private String alter_T_LIGNES_FACTURE_8;

    private String alter_T_TAXRATES_1;

    private String alter_TREF_TYPE_INTERVENTION_1;

    private String alter_T_CUSTOMERS_1;

    private String alter_T_FACTURES_1;

    /**
     * create table T_GROUPES_USERS
     */
    private String t_groups_user;

    /**
     * create table TREF_TAGS
     */
    private String t_tref_tags;

    /**
     * create table T_FACTURE_TAX
     */
    private String t_facture_tax;

    public TablesUpdated52() {

        t_groups_user = "CREATE TABLE T_GROUPES_USERS (" +
                "ID_GROUPE INTEGER NOT NULL DEFAULT AUTOINCREMENT," +
                "NOM_GROUPE varchar(1024) NOT NULL," +
                "DESCRIPTION_GROUPE VARCHAR(2048) NULL," +
                "PRIMARY KEY ( ID_GROUPE ASC ))";

        t_tref_tags = "CREATE TABLE TREF_TAGS (" +
                "ID_REF_TAGS INTEGER NOT NULL DEFAULT AUTOINCREMENT," +
                "REF_TABLE VARCHAR(512) NULL," +
                "REF_ID INTEGER NULL," +
                "TYPE_TAGS VARCHAR(512) NULL," +
                "LIST_TAGS long VARCHAR NULL," +
                "PRIMARY KEY ( ID_REF_TAGS ASC ))";

        t_facture_tax = "CREATE TABLE T_FACTURE_TAX (" +
                "ID_TAXRATE INTEGER NULL DEFAULT NULL," +
                "FL_COMPOUND BIT NOT NULL DEFAULT 0," +
                "VAL_TAXRATE DECIMAL(7, 5) NOT NULL DEFAULT 0," +
                "VAL_TVA DECIMAL(20, 4) NOT NULL DEFAULT 0," +
                "ID_REMOTE VARCHAR(512) NOT NULL DEFAULT newid()," +
                "ID_REMOTE_FACTURE varchar(512) NULL," +
                "DT_SUPPR TIMESTAMP NULL," +
                "PRIMARY KEY ( ID_REMOTE  ))";


        alter_TREF_GESTION_ACCES_1 = "ALTER TABLE TREF_GESTION_ACCES ADD FL_SIGN_CUST INTEGER NOT NULL DEFAULT 1";
        alter_TREF_GESTION_ACCES_2 = "ALTER TABLE TREF_GESTION_ACCES ADD FL_SIGN_TECH INTEGER NOT NULL DEFAULT 1";
        alter_TREF_GESTION_ACCES_3 = "ALTER TABLE TREF_GESTION_ACCES ADD NUM_DECIMALS INTEGER NOT NULL DEFAULT 2";

        alter_T_USERS_1 = "ALTER TABLE T_USERS ADD AUTH_TOKEN UNIQUEIDENTIFIER NULL";
        alter_T_USERS_2 = "ALTER TABLE T_USERS ADD AUTH_EXPIRY TIMESTAMP NULL";
        alter_T_USERS_3 = "ALTER TABLE T_USERS DROP PWD_USER";

        alter_TREF_TYPE_INTERVENTION_1 = "ALTER TABLE TREF_TYPE_INTERVENTION ADD DEFAULT_PRIORITY INTEGER NULL";
        alter_T_CUSTOMERS_1 = "ALTER TABLE T_CUSTOMERS ADD UNIT_DISTANCE varchar(20) NOT NULL DEFAULT 'km'";

        alter_TREF_PIECES_1 = "ALTER TABLE TREF_PIECES ADD DESCRIPTION VARCHAR(16000) NULL";
        alter_TREF_PIECES_2 = "ALTER TABLE TREF_PIECES ADD TYPE VARCHAR(40) NOT NULL DEFAULT 'part'";

        alter_T_FACTURES_1 = "ALTER TABLE T_FACTURES ADD NUM VARCHAR(1020) NULL DEFAULT NULL";

        alter_T_LIGNES_FACTURE_1 = "ALTER TABLE T_LIGNES_FACTURE ADD ID_TAXRATE INTEGER NULL DEFAULT NULL";
        alter_T_LIGNES_FACTURE_2 = "ALTER TABLE T_LIGNES_FACTURE ALTER VAL_TAXRATE DECIMAL(7,5) NOT NULL DEFAULT '0'";
        alter_T_LIGNES_FACTURE_3 = "ALTER TABLE T_LIGNES_FACTURE ADD ID_PIECE INTEGER NULL DEFAULT NULL";
        alter_T_LIGNES_FACTURE_4 = "ALTER TABLE T_LIGNES_FACTURE ADD DESCRIPTION VARCHAR(16000) DEFAULT NULL";
        alter_T_LIGNES_FACTURE_5 = "ALTER TABLE T_LIGNES_FACTURE ADD ID_INTERVENTION VARCHAR(160) NULL DEFAULT NULL";
        alter_T_LIGNES_FACTURE_6 = "ALTER TABLE T_LIGNES_FACTURE ALTER DISCOUNT DECIMAL(20, 4)";
        alter_T_LIGNES_FACTURE_7 = "ALTER TABLE T_LIGNES_FACTURE ADD DISCOUNTPERCENT BIT NOT NULL DEFAULT 1";
        alter_T_LIGNES_FACTURE_8 = "ALTER TABLE T_LIGNES_FACTURE ALTER PRIX_UNITAIRE DECIMAL(14, 4) NOT NULL DEFAULT 0";

        alter_T_TAXRATES_1 = "ALTER TABLE T_TAXRATES ALTER VAL_TAXRATE DECIMAL(7,5) NOT NULL";

    }

    public String getAlter_TREF_GESTION_ACCES_1() {
        return alter_TREF_GESTION_ACCES_1;
    }

    public void setAlter_TREF_GESTION_ACCES_1(String alter_TREF_GESTION_ACCES_1) {
        this.alter_TREF_GESTION_ACCES_1 = alter_TREF_GESTION_ACCES_1;
    }

    public String getAlter_TREF_GESTION_ACCES_2() {
        return alter_TREF_GESTION_ACCES_2;
    }

    public void setAlter_TREF_GESTION_ACCES_2(String alter_TREF_GESTION_ACCES_2) {
        this.alter_TREF_GESTION_ACCES_2 = alter_TREF_GESTION_ACCES_2;
    }

    public String getAlter_T_USERS_1() {
        return alter_T_USERS_1;
    }

    public void setAlter_T_USERS_1(String alter_T_USERS_1) {
        this.alter_T_USERS_1 = alter_T_USERS_1;
    }

    public String getAlter_T_USERS_2() {
        return alter_T_USERS_2;
    }

    public void setAlter_T_USERS_2(String alter_T_USERS_2) {
        this.alter_T_USERS_2 = alter_T_USERS_2;
    }

    public String getAlter_T_USERS_3() {
        return alter_T_USERS_3;
    }

    public void setAlter_T_USERS_3(String alter_T_USERS_3) {
        this.alter_T_USERS_3 = alter_T_USERS_3;
    }

    public String getAlter_TREF_PIECES_1() {
        return alter_TREF_PIECES_1;
    }

    public void setAlter_TREF_PIECES_1(String alter_TREF_PIECES_1) {
        this.alter_TREF_PIECES_1 = alter_TREF_PIECES_1;
    }

    public String getAlter_TREF_PIECES_2() {
        return alter_TREF_PIECES_2;
    }

    public void setAlter_TREF_PIECES_2(String alter_TREF_PIECES_2) {
        this.alter_TREF_PIECES_2 = alter_TREF_PIECES_2;
    }

    public String getAlter_T_LIGNES_FACTURE_1() {
        return alter_T_LIGNES_FACTURE_1;
    }

    public void setAlter_T_LIGNES_FACTURE_1(String alter_T_LIGNES_FACTURE_1) {
        this.alter_T_LIGNES_FACTURE_1 = alter_T_LIGNES_FACTURE_1;
    }

    public String getAlter_T_LIGNES_FACTURE_2() {
        return alter_T_LIGNES_FACTURE_2;
    }

    public void setAlter_T_LIGNES_FACTURE_2(String alter_T_LIGNES_FACTURE_2) {
        this.alter_T_LIGNES_FACTURE_2 = alter_T_LIGNES_FACTURE_2;
    }

    public String getAlter_T_LIGNES_FACTURE_3() {
        return alter_T_LIGNES_FACTURE_3;
    }

    public void setAlter_T_LIGNES_FACTURE_3(String alter_T_LIGNES_FACTURE_3) {
        this.alter_T_LIGNES_FACTURE_3 = alter_T_LIGNES_FACTURE_3;
    }

    public String getAlter_T_LIGNES_FACTURE_4() {
        return alter_T_LIGNES_FACTURE_4;
    }

    public void setAlter_T_LIGNES_FACTURE_4(String alter_T_LIGNES_FACTURE_4) {
        this.alter_T_LIGNES_FACTURE_4 = alter_T_LIGNES_FACTURE_4;
    }

    public String getAlter_T_LIGNES_FACTURE_5() {
        return alter_T_LIGNES_FACTURE_5;
    }

    public void setAlter_T_LIGNES_FACTURE_5(String alter_T_LIGNES_FACTURE_5) {
        this.alter_T_LIGNES_FACTURE_5 = alter_T_LIGNES_FACTURE_5;
    }

    public String getAlter_T_LIGNES_FACTURE_6() {
        return alter_T_LIGNES_FACTURE_6;
    }

    public void setAlter_T_LIGNES_FACTURE_6(String alter_T_LIGNES_FACTURE_6) {
        this.alter_T_LIGNES_FACTURE_6 = alter_T_LIGNES_FACTURE_6;
    }

    public String getAlter_T_LIGNES_FACTURE_7() {
        return alter_T_LIGNES_FACTURE_7;
    }

    public void setAlter_T_LIGNES_FACTURE_7(String alter_T_LIGNES_FACTURE_7) {
        this.alter_T_LIGNES_FACTURE_7 = alter_T_LIGNES_FACTURE_7;
    }

    public String getAlter_T_TAXRATES_1() {
        return alter_T_TAXRATES_1;
    }

    public void setAlter_T_TAXRATES_1(String alter_T_TAXRATES_1) {
        this.alter_T_TAXRATES_1 = alter_T_TAXRATES_1;
    }

    public String getAlter_TREF_TYPE_INTERVENTION_1() {
        return alter_TREF_TYPE_INTERVENTION_1;
    }

    public void setAlter_TREF_TYPE_INTERVENTION_1(String alter_TREF_TYPE_INTERVENTION_1) {
        this.alter_TREF_TYPE_INTERVENTION_1 = alter_TREF_TYPE_INTERVENTION_1;
    }

    public String getAlter_T_CUSTOMERS_1() {
        return alter_T_CUSTOMERS_1;
    }

    public void setAlter_T_CUSTOMERS_1(String alter_T_CUSTOMERS_1) {
        this.alter_T_CUSTOMERS_1 = alter_T_CUSTOMERS_1;
    }

    public String getAlter_T_FACTURES_1() {
        return alter_T_FACTURES_1;
    }

    public void setAlter_T_FACTURES_1(String alter_T_FACTURES_1) {
        this.alter_T_FACTURES_1 = alter_T_FACTURES_1;
    }

    public String getT_groups_user() {
        return t_groups_user;
    }

    public void setT_groups_user(String t_groups_user) {
        this.t_groups_user = t_groups_user;
    }

    public String getT_tref_tags() {
        return t_tref_tags;
    }

    public void setT_tref_tags(String t_tref_tags) {
        this.t_tref_tags = t_tref_tags;
    }

    public String getT_facture_tax() {
        return t_facture_tax;
    }

    public void setT_facture_tax(String t_facture_tax) {
        this.t_facture_tax = t_facture_tax;
    }

    public String getAlter_TREF_GESTION_ACCES_3() {
        return alter_TREF_GESTION_ACCES_3;
    }

    public void setAlter_TREF_GESTION_ACCES_3(String alter_TREF_GESTION_ACCES_3) {
        this.alter_TREF_GESTION_ACCES_3 = alter_TREF_GESTION_ACCES_3;
    }

    public String getAlter_T_LIGNES_FACTURE_8() {
        return alter_T_LIGNES_FACTURE_8;
    }

    public void setAlter_T_LIGNES_FACTURE_8(String alter_T_LIGNES_FACTURE_8) {
        this.alter_T_LIGNES_FACTURE_8 = alter_T_LIGNES_FACTURE_8;
    }
}
