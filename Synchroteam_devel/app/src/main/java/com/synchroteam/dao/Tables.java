package com.synchroteam.dao;

// TODO: Auto-generated Javadoc
// Tables 3.3

/**
 * The Class Tables.
 */
public class
Tables {

    /**
     * The t_attrib_messsages_oper.
     */
    private String t_attrib_messsages_oper;

    /**
     * The t_attrib_modules.
     */
    private String t_attrib_modules;

    /**
     * The t_clients.
     */
    private String t_clients;

    /**
     * The t_customers.
     */
    private String t_customers;

    /**
     * The t_equipements_clients.
     */
    private String t_equipements_clients;

    /**
     * The t_interventions.
     */
    private String t_interventions;

    /**
     * The t_messages_operateur.
     */
    private String t_messages_operateur;

    /**
     * The t_photo_pda.
     */
    private String t_photo_pda;

    /**
     * The t_saisie_rapport.
     */
    private String t_saisie_rapport;

    /**
     * The t_sites_client.
     */
    private String t_sites_client;

    /**
     * The t_sortie_piece.
     */
    private String t_sortie_piece;

    /**
     * The t_temps_interv.
     */
    private String t_temps_interv;

    /**
     * The t_users.
     */
    private String t_users;

    /** The t_val_custom_fields. */
    // private String t_val_custom_fields;

    /**
     * The tref_categorie_piece.
     */
    private String tref_categorie_piece;

    /**
     * The tref_commentaires_fils.
     */
//    private String tref_commentaires_fils;

    /**
     * The tref_commentaires_pere.
     */
//    private String tref_commentaires_pere;

    /**
     * The tref_modele_famille.
     */
    private String tref_modele_famille;

    /**
     * The tref_modele_item.
     */
    private String tref_modele_item;

    /**
     * The tref_modele_rapport.
     */
    private String tref_modele_rapport;

    /**
     * The tref_modele_value_item.
     */
    private String tref_modele_value_item;

    /**
     * The tref_modules.
     */
    private String tref_modules;

    /**
     * The tref_pieces.
     */
    private String tref_pieces;

    /**
     * The tref_statut_intervention.
     */
    private String tref_statut_intervention;

    /**
     * The tref_traductions.
     */
    private String tref_traductions;

    /**
     * The tref_traduction_cust.
     */
    private String tref_traduction_cust;

    /**
     * The tref_type_intervention.
     */
    private String tref_type_intervention;

    /** The tref_custom_fields. */
    // private String tref_custom_fields;

    /**
     * The tref_gestion_acces.
     */
    private String tref_gestion_acces;

    /**
     * The tref_typint_rapport.
     */
    private String tref_typint_rapport;

    /**
     * The t_session.
     */
    private String t_session;

    /** The tdalkia_liste_operation. */
    // private String tdalkia_liste_operation;

    /** The tdalkia_bt_fis. */
    // private String tdalkia_bt_fis;

    /** The tdalkia_refus. */
    // private String tdalkia_refus;

    /**
     * The t_conge.
     */
    private String t_conge;

    /**
     * The tref_type_conge.
     */
    private String tref_type_conge;

    /**
     * The t_clients_index.
     */
    private String t_clients_index;

    /**
     * The t_clients_index.
     */
    private String t_clients_index_search;

    /**
     * The t_sites_clients_index.
     */
    private String t_sites_clients_index;

    /**
     * The t_sites_clients_index.
     */
    private String t_sites_clients_index_search;

    /**
     * The t_equipment_client_index.
     */
    private String t_equipment_client_index;



    /**
     * The t_intervention_dt_deb_previndex.
     */
    private String t_intervention_dt_deb_previndex;

    /**
     * The t_intervention_dt_fin_realindex.
     */
    private String t_intervention_dt_fin_realindex;

    /**
     * The t_attrib_messages_oer_fl_lu_index.
     */
    private String t_attrib_messages_oer_fl_lu_index;

    /**
     * The t_attrib_messages_oer_dt_modif_index.
     */
    private String t_attrib_messages_oer_dt_modif_index;

    /**
     * Attachment table
     */
    private String t_attachment;

    /**
     * Invoice/Quotation
     */
    private String t_factures;

    /**
     * Invoice/Quotation items
     */
    private String t_lignes_facture;

    /**
     * tax rate for invoice/quotation items
     */
    private String t_taxrates;

    /**
     * Stock table
     */
    private String t_stocks;

    /**
     * Serial number for stock items
     */
    private String t_piece_Serials;

    /**
     * pennding request table
     */
    private String t_piece_demande;

    /**
     * table to maintatain a copy of shared block in tref_modele_bloc table
     */
    private String t_saisie_bloc;

    /**
     * shared blocks table
     */
    private String tref_modele_bloc;

    /**
     * stock pieces table
     */
    private String t_stock_pieces;

    /**
     * Payments table - Store each payment received
     */
    private String t_payments;

    /**
     * Payments log table - Log of activity related to payments / refunds
     */
    private String t_payments_log;

    /**
     * c_connections - Nouvelle Table
     */
    private String c_connections;

    /**
     * alter table T_STOCK_PIECES
     */
    private String alter_T_STOCK_PIECES;


    /**
     * create table T_REPRISE_PIECE
     */
    private String t_reprise_piece;

    /**
     * create table T_REPRISE_PIECE
     */
    private String alter_T_PIECE_SERIALS;

    /**
     * create table T_JOB_WINDOWS
     */
    private String t_job_windows;


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

    /**
     * create table T_INCIDENTS
     */
    private String t_incidents;
    /**
     * create table T_INCIDENTS_LOG
     */
    private String t_incidents_log;

    /**
     * create table GPS_STEPS
     */
    private String gps_steps;
    // gps_steps index
    private String gps_steps_index;

    public String getGps_steps_index(){ return gps_steps_index; }

    public String getT_clients_index_search() {
        return t_clients_index_search;
    }

    public String getT_sites_clients_index_search() {
        return t_sites_clients_index_search;
    }

    /**
     * Instantiates a new tables.
     */
    public Tables() {
        super();
        t_session = "CREATE TABLE t_session_nosync ( "
                + "ID VARCHAR(80) NOT NULL," + "STRT VARCHAR(80) ,"
                + "HOST VARCHAR(80) ," + "PORT VARCHAR(80) ,"
                + "SCRIPT VARCHAR(80) ," + "DRPSYNCH INTEGER , "
                + "LNG VARCHAR(80) ," + "LAST_SYNCH TIMESTAMP NULL ,"
                + "DOMAIN VARCHAR(80) ,"
                + "PRIMARY KEY (ID ASC),SYNCHRONIZE OFF ) ";

        t_attrib_messsages_oper = "CREATE TABLE T_ATTRIB_MESSAGES_OPER ("
                + "ID_MESSAGE INTEGER NOT NULL," + "ID_USER INTEGER NOT NULL,"
                + "FL_LU INTEGER NOT NULL DEFAULT 0,"
                + "DT_MODIF TIMESTAMP NULL," + "DT_SUPPR TIMESTAMP NULL,"
                + "PRIMARY KEY ( ID_MESSAGE ASC, ID_USER ASC ))";

        t_attrib_modules = "CREATE TABLE T_ATTRIB_MODULES ("
                + "ID_CUSTOMER INTEGER NOT NULL,"
                + "ID_MODULE INTEGER NOT NULL," + "DT_MODIF TIMESTAMP NULL,"
                + "DT_SUPPR TIMESTAMP NULL,"
                + "PRIMARY KEY ( ID_CUSTOMER ASC, ID_MODULE ASC ))";

        t_clients = "CREATE TABLE T_CLIENTS ("
                + "ID_CLIENT INTEGER NOT NULL DEFAULT AUTOINCREMENT,"
                + "ID_CUSTOMER INTEGER NOT NULL,"
                + "NM_CLIENT VARCHAR(2048) NOT NULL,"
                + "ADR_CLIENT_RUE VARCHAR(1024) NULL,"
                + "ADR_CLIENT_CP VARCHAR(1024) NULL,"
                + "ADR_CLIENT_VILLE VARCHAR(1024) NULL,"
                + "ADR_CLIENT_PAYS VARCHAR(1024) NULL,"
                + "NOM_CONTACT_CLIENT VARCHAR(1024) NULL,"
                + "TEL_CONTACT_CLIENT VARCHAR(1024) NULL,"
                + "GPS_POSX_CLIENT VARCHAR(60) NULL,"
                + "GPS_POSY_CLIENT VARCHAR(60) NULL,"
                + "DT_MODIF TIMESTAMP NULL,"
                + "DT_SUPPR TIMESTAMP NULL,"
                // "REF_CUSTOMER VARCHAR(30) NULL,"+
                // "COMMENTAIRE_CUSTOMER VARCHAR(120) NULL,"+
                + "ADR_CLIENT_GLOBALE VARCHAR(2048) NULL,"
                + "ADR_CLIENT_COMPLEMENT VARCHAR(1020) NULL,"
                + "PUBLIC_LINK VARCHAR(1060) NULL,"
                + "PRENOM_CONTACT_CLIENT VARCHAR(256),"
                + "EMAIL_CONTACT_CLIENT VARCHAR(256),"
                + "MOBILE_CONTACT_CLIENT VARCHAR(256),"
                + "JSON_CF long VARCHAR,"
                + "ADR_CLIENT_PROVINCE VARCHAR(1024),"
                + "REF_CUSTOMER VARCHAR(1024) NULL, "
                + "PRIMARY KEY ( ID_CLIENT ASC ))";

        t_customers = "CREATE TABLE T_CUSTOMERS ("
                + "ID_CUSTOMER INTEGER NOT NULL DEFAULT AUTOINCREMENT,"
                + "NM_SOCIETE VARCHAR(1024) NOT NULL,"
                + "DT_CREATE_SERVICE TIMESTAMP NOT NULL DEFAULT CURRENT TIMESTAMP,"
                + "DT_FIN_SERVICE TIMESTAMP NULL,"
                + "NB_LICENCES INTEGER NOT NULL DEFAULT 0,"
                + "CD_LANGUE VARCHAR(64) NOT NULL DEFAULT 'FR',"
                + "NM_DEVISE VARCHAR(16) NOT NULL DEFAULT '€',"
                + "FL_HORS_PROGRAMME INTEGER NOT NULL DEFAULT 0,"
                + "FL_SYNC_DEBUT INTEGER NOT NULL DEFAULT 1,"
                + "FL_SYNC_REPRENDRE INTEGER NOT NULL DEFAULT 1,"
                + "FL_SYNC_FIN INTEGER NOT NULL DEFAULT 0,"
                + "FL_SYNC_SUSPENDRE INTEGER NOT NULL DEFAULT 0,"
                + "NM_FILE_PDF VARCHAR(120) NOT NULL DEFAULT 'pdf.aspx',"
                + "NM_FILE_EXCEL VARCHAR(200) NOT NULL DEFAULT 'noteFinanciere.aspx',"
                + "NM_STYLE_CSS VARCHAR(80) NOT NULL DEFAULT 'default.css',"
                + "DT_MODIF TIMESTAMP NULL," + "DT_SUPPR TIMESTAMP NULL," +
                "JSON_SETTINGS VARCHAR(8192) NULL,"
                + "UNIT_DISTANCE varchar(20) NOT NULL DEFAULT 'km'," +
                // "FL_EVAL INTEGER NULL DEFAULT 1,"+
                // "ID_RESP_COMMERCIAL INTEGER NULL DEFAULT 1,"+
                // "ID_RESELLER INTEGER NULL,"+
                // "FL_CLUF INTEGER NULL,"+
                "PRIMARY KEY ( ID_CUSTOMER ASC ))";

        t_equipements_clients = "CREATE TABLE T_EQUIPEMENTS_CLIENTS ("
                + "ID_EQUIPEMENT_CLIENT INTEGER NOT NULL DEFAULT AUTOINCREMENT UNIQUE,"
                + "ID_SITE_CLIENT INTEGER NULL,"
                + "ID_CLIENT INTEGER NOT NULL,"
                + "ID_CUSTOMER INTEGER NOT NULL,"
                + "NM_EQUIPEMENT VARCHAR(2048) NOT NULL,"
                + "GPS_POSX_EQUIPEMENT VARCHAR(60) NULL,"
                + "GPS_POSY_EQUIPEMENT VARCHAR(60) NULL,"
                +
                // "REF_CUSTOMER VARCHAR(120) NULL,"+
                // "COMMENTAIRES_CUSTOMER VARCHAR(480) NULL,"+
                "DT_MODIF TIMESTAMP NULL," + "DT_SUPPR TIMESTAMP NULL,"
                + "JSON_CF long VARCHAR,"
                + "PUBLIC_LINK VARCHAR(1024) NULL,"
                + "REF_CUSTOMER VARCHAR(1024) NULL, "
                + "PRIMARY KEY ( ID_EQUIPEMENT_CLIENT ASC ))";

        t_interventions = "CREATE TABLE T_INTERVENTIONS ("
                + "ID_INTERVENTION VARCHAR(160) NOT NULL,"
                + "ID_CUSTOMER INTEGER NOT NULL,"
                + "ID_TYPE_INTERVENTION INTEGER NOT NULL,"
                + "ID_CLIENT INTEGER NULL,"
                + "ID_USER INTEGER NULL,"
                + "DESCR_INTERVENTION VARCHAR(8192) NOT NULL,"
                + "PRIORITE_INTERVENTION INTEGER NOT NULL DEFAULT 1,"
                + "DT_DEB_PREV TIMESTAMP NULL,"
                + "DT_FIN_PREV TIMESTAMP NULL,"
                + "DT_DEB_REAL TIMESTAMP NULL,"
                + "DT_FIN_REAL TIMESTAMP NULL,"
                + "ADR_INTERV_RUE VARCHAR(1024) NULL,"
                + "ADR_INTERV_CP VARCHAR(1024) NULL,"
                + "ADR_INTERV_VILLE VARCHAR(1024) NULL,"
                + "ADR_INTERV_PAYS VARCHAR(1024) NULL,"
                + "GPS_POSX_INTERV VARCHAR(1024) NULL,"
                + "GPS_POSY_INTERV VARCHAR(1024) NULL,"
                + "SIGN_CLIENT VARCHAR(8000) NULL,"
                + "SIGN_USER VARCHAR(8000) NULL,"
                + "TXT_COMMENTAIRE_INTERV VARCHAR(2048) NULL,"
                + "CD_STATUT_INTERV INTEGER NOT NULL,"
                + "ID_USER_VALIDATION INTEGER NULL,"
                + "DT_VALIDATION TIMESTAMP NULL,"
                + "DT_MODIF TIMESTAMP NULL,"
                + "DT_SUPPR TIMESTAMP NULL,"
                + "NOM_CLIENT_INTERV VARCHAR(2048) NULL,"
                + "ID_MODELE_RAPPORT INTEGER NULL,"
                +
                // "ID_OCCURENCE_TASK INTEGER NULL,"+
                "REF_CUSTOMER VARCHAR(1024) NULL," + "ID_SITE INTEGER NULL,"
                + "ID_EQUIPEMENT INTEGER NULL,"
                + "NOM_CONTACT VARCHAR(1024) NULL,"
                + "TEL_CONTACT VARCHAR(1024) NULL,"
                + "NOM_SITE_INTERV VARCHAR(2048) NULL,"
                + "NOM_EQUIPEMENT_INTERV VARCHAR(2048) NULL,"
                + "ADR_INTERV_GLOBALE VARCHAR(2048) NULL,"
                + "ADR_INTERV_COMPLEMENT VARCHAR(1024) NULL,"
                + "NO_INT_CUST INTEGER NULL,"
                + "ID_USER_LAST_MODIF INTEGER NULL DEFAULT 0,"
                + "NM_CLIENT_SIGN  VARCHAR(1020) NULL,"
                + "NM_TECH_SIGN  VARCHAR(1020) NULL,"
                + "NM_FACTURE_SIGN  VARCHAR(1020) NULL,"
                + "MOBILE_CONTACT  VARCHAR(1024) NULL,"
                + "DT_MEETING TIMESTAMP NULL,"
                + "ID_INTERVENTION_MERE VARCHAR(160) NULL,"
                + "PUBLIC_LINK VARCHAR(1060) NULL,"
                + "PRENOM_CONTACT VARCHAR(256),"
                + "EMAIL_CONTACT VARCHAR(256),"
                // "ID_CREATEUR INTEGER NULL,"+
                + "JSON_CF long VARCHAR,"
                + "DT_CREATE TIMESTAMP NOT NULL DEFAULT '1900-01-01 00:00:00.000000',"
                + "FL_POOL BIT NOT NULL DEFAULT 0,"
                + "DT_PREF datetime NULL,"
                + "ID_JOB_WINDOW INTEGER NULL,"
                +"ADR_INTERV_PROVINCE VARCHAR(1024) NULL,"
                + " PRIMARY KEY  ( ID_INTERVENTION ASC ))";

        t_messages_operateur = "CREATE TABLE T_MESSAGES_OPERATEUR("
                + "ID_MESSAGE INTEGER NOT NULL DEFAULT AUTOINCREMENT,"
                + "TITRE_MESSAGE VARCHAR(1024) NULL,"
                + "CORP_MESSAGE VARCHAR(4096) NULL,"
                + "PRIORITE_MESSAGE INTEGER NULL DEFAULT 2,"
                + "DT_SUPPR TIMESTAMP NULL," + "DT_MODIF TIMESTAMP NULL,"
                + "PRIMARY KEY ( ID_MESSAGE ASC ))";

        t_photo_pda = "CREATE TABLE T_PHOTOS_PDA("
                + "ID_PHOTO_PDA VARCHAR(160) NOT NULL,"
                + "ID_INTERVENTION VARCHAR(160) NOT NULL,"
                + "PHOTO_PDA LONG BINARY,"
                + "COMMENTAIRE_PHOTO_PDA VARCHAR(2048) NULL,"
                + "DT_MODIF TIMESTAMP NULL," + "DT_SUPPR TIMESTAMP NULL,"
                + "EXTENSION VARCHAR(20) NULL,"
                + "ITERATION INTEGER NOT NULL DEFAULT 0,"
                + "PRIMARY KEY ( ID_PHOTO_PDA ASC ))";

        t_saisie_rapport = "CREATE TABLE T_SAISIE_RAPPORT("
                + "ID_INTERVENTION VARCHAR(160) NOT NULL,"
                + "ID_MODELE_ITEM INTEGER NOT NULL,"
                + "VALEUR_ITEM VARCHAR(2048) NOT NULL,"
                + "COMMENTAIRE_ITEM VARCHAR(12288) NULL,"
                + "DT_MODIF TIMESTAMP NULL," + "DT_SUPPR TIMESTAMP NULL,"
                + "FL_RESERVE INTEGER NULL DEFAULT 0,"
                + "NM_MODELE_ITEM VARCHAR(1024) NULL,"
                + "ITERATION SMALLINT NOT NULL DEFAULT 0,"
                + "PRIMARY KEY ( ID_INTERVENTION ASC, ID_MODELE_ITEM ASC, ITERATION ASC ))";
//				+ "PRIMARY KEY ( ID_INTERVENTION ASC, ID_MODELE_ITEM ASC ))";

        t_sites_client = "CREATE TABLE T_SITES_CLIENTS("
                + "ID_SITE_CLIENT INTEGER NOT NULL DEFAULT AUTOINCREMENT UNIQUE,"
                + "ID_CLIENT INTEGER NOT NULL,"
                + "ID_CUSTOMER INTEGER NOT NULL,"
                + "NM_SITE VARCHAR(2048) NOT NULL,"
                + "ADR_SITE_RUE VARCHAR(1024) NULL,"
                + "ADR_SITE_CP VARCHAR(1024) NULL,"
                + "ADR_SITE_VILLE VARCHAR(1024) NULL,"
                + "ADR_SITE_PAYS VARCHAR(1024) NULL,"
                + "NM_CONTACT_SITE VARCHAR(1024) NULL,"
                + "TEL_CONTACT_SITE VARCHAR(1024) NULL,"
                + "GPS_POSX_SITE VARCHAR(60) NULL,"
                + "GPS_POSY_SITE VARCHAR(60) NULL,"
                + "DT_MODIF TIMESTAMP NULL," + "DT_SUPPR TIMESTAMP NULL,"
                + "ADR_SITE_GLOBALE VARCHAR(2048) NULL,"
                + "ADR_SITE_COMPLEMENT VARCHAR(1020) NULL,"
                + "PRENOM_CONTACT_SITE VARCHAR(256),"
                + "EMAIL_CONTACT_SITE VARCHAR(256)," + "JSON_CF long VARCHAR,"
                + "PUBLIC_LINK VARCHAR(1024) NULL,"
                + "MOBILE_CONTACT_SITE VARCHAR(1024) NULL,"
                + "ADR_SITE_PROVINCE VARCHAR(1024) NULL,"
                + "REF_CUSTOMER VARCHAR(1024) NULL, "
                + "PRIMARY KEY ( ID_SITE_CLIENT ASC ))";
        // "REF_CUSTOMER VARCHAR(120) NULL,"+
        // "COMMENTAIRES_CUSTOMER VARCHAR(500) NULL,"+



        /*
           New changed added SERIAL_SORTIE for tracking the serial parts installed for a job
        */
        t_sortie_piece = "CREATE TABLE T_SORTIE_PIECE ("
                + "ID_INTERVENTION VARCHAR(160) NOT NULL,"
                + "ID_PIECE INTEGER NOT NULL,"
                + "QUANTITE_SORTIE DECIMAL(6,2) NOT NULL,"
                + "FL_FACTURABLE INTEGER NOT NULL DEFAULT 0,"
                + "DT_SUPPR datetime NULL," + "DT_MODIF datetime NULL,"
                + "SERIAL_SORTIE VARCHAR(1500) NULL,"
                + "PRIMARY KEY ( ID_INTERVENTION ASC ,ID_PIECE ASC )"
                + ")";

        //old changes
//        t_sortie_piece = "CREATE TABLE T_SORTIE_PIECE ("
//                + "ID_INTERVENTION VARCHAR(160) NOT NULL,"
//                + "ID_PIECE INTEGER NOT NULL,"
//                + "QUANTITE_SORTIE DECIMAL(6,2) NOT NULL,"
//                + "FL_FACTURABLE INTEGER NOT NULL DEFAULT 0,"
//                + "DT_SUPPR datetime NULL," + "DT_MODIF datetime NULL,"
//                + "PRIMARY KEY ( ID_INTERVENTION ASC ,ID_PIECE ASC ))";

        // -----------------------------------------------------------------------------------------------------------------------------------

        t_temps_interv = "CREATE TABLE T_TEMPS_INTERV ("
                + "ID_TEMPS_INTERV VARCHAR(160) NOT NULL,"
                + "ID_INTERVENTION VARCHAR(160) NOT NULL,"
                + "ID_USER INTEGER NOT NULL,"
                + "DT_DEPART TIMESTAMP NULL,"
                + "DT_DEBUT TIMESTAMP NULL,"
                + "DT_FIN TIMESTAMP NULL,"
                + "DT_RETOUR TIMESTAMP NULL,"
                + "DT_MODIF TIMESTAMP NULL,"
                + "DT_SUPPR TIMESTAMP NULL,"
                + "DT_FIN_PREV TIMESTAMP NULL,"
                + "FL_SCHEDULED INTEGER NOT NULL,"
                + "FL_AUXILIARY INTEGER NOT NULL,"
                + "PRIMARY KEY ( ID_TEMPS_INTERV ASC ),"
                + " FOREIGN KEY T_TEMPS_INTERV_to_T_INTERVENTIONS (ID_INTERVENTION) REFERENCES T_INTERVENTIONS (ID_INTERVENTION)) ";

        t_users = "CREATE TABLE T_USERS ("
                + "ID_USER INTEGER NOT NULL DEFAULT AUTOINCREMENT,"
                + "ID_CUSTOMER INTEGER NULL," + "ID_PROFIL INTEGER NULL,"
                + "LOGIN_USER VARCHAR(1024) NOT NULL,"
                + "NOM_USER VARCHAR(1024) NOT NULL,"
                + "PRENOM_USER VARCHAR(1024) NULL,"
                //   + "PWD_USER VARCHAR(1024) NOT NULL,"//v52.0.0 client cmd
                + "CD_LANGUE_USER VARCHAR(8) NULL,"
                + "FL_GPS INTEGER NOT NULL DEFAULT 0,"
                + "FL_CODE_BARRE INTEGER NOT NULL DEFAULT 0,"
                + "DT_VERIF_PWD TIMESTAMP NULL,"
                + "GPS_POSX_USER VARCHAR(60) NULL,"
                + "GPS_POSY_USER VARCHAR(60) NULL,"
                + "ID_TYPE_PDA INTEGER NOT NULL DEFAULT 0,"
                + "ID_PUSH_IOS VARCHAR(400) NULL,"
                + "ID_PUSH_ANDROID VARCHAR(800) NULL,"
                + "DT_MODIF TIMESTAMP NULL,"
                + "DT_SUPPR TIMESTAMP NULL,"
                + "ID_STOCK UNIQUEIDENTIFIER NULL,"
                // "NUMERO_TELEPHONE VARCHAR(15) NULL,"+
                + "JSON_CF long VARCHAR,"
                + "FL_TT BIT NOT NULL DEFAULT 0,"
                + "APP_VERSION VARCHAR(40) NULL,"
                + "AUTH_TOKEN UNIQUEIDENTIFIER NULL,"
                + "AUTH_EXPIRY TIMESTAMP NULL,"
                + "FL_SUBCONTRACTOR BIT NOT NULL DEFAULT 0,"
                + "FL_GPS_TRACKED BIT NOT NULL DEFAULT 0, "
                + "PRIMARY KEY ( ID_USER ASC ))";

        // t_val_custom_fields = "CREATE TABLE T_VAL_CUSTOM_FIELDS ("
        // + "ID_CUSTOM_FIELD INTEGER NOT NULL,"
        // + "ID_ELEMENT_CONCERNE VARCHAR(160) NOT NULL,"
        // + "VAL_CUSTOM_FIELD VARCHAR(2048) NULL,"
        // + "DT_MODIF TIMESTAMP NULL," + "DT_SUPPR TIMESTAMP NULL,"
        // + "PRIMARY KEY (ID_CUSTOM_FIELD ASC,ID_ELEMENT_CONCERNE ASC ))";

        tref_categorie_piece = "CREATE TABLE TREF_CATEGORIE_PIECE ("
                + "ID_CATEGORIE_PIECE INTEGER NOT NULL DEFAULT AUTOINCREMENT,"
                + "ID_CUSTOMER INTEGER NOT NULL,"
                + "NM_CATEGORIE_PIECE VARCHAR(1024) NOT NULL,"
                + "DT_MODIF TIMESTAMP NULL," + "DT_SUPPR TIMESTAMP NULL,"
                + "PRIMARY KEY ( ID_CATEGORIE_PIECE ASC ))";

        /*tref_commentaires_fils = "CREATE TABLE TREF_COMMENTAIRES_FILS("
                + "ID_COMMENTAIRE_FILS INTEGER NOT NULL DEFAULT AUTOINCREMENT,"
                + "ID_COMMENTAIRE_PERE INTEGER NOT NULL,"
                + "NM_COMMENTAIRE_FILS VARCHAR(200) NOT NULL,"
                + "DT_MODIF TIMESTAMP NULL," + "DT_SUPPR TIMESTAMP NULL,"
                + "PRIMARY KEY ( ID_COMMENTAIRE_FILS ASC ))";

        tref_commentaires_pere = "CREATE TABLE TREF_COMMENTAIRES_PERE("
                + "ID_COMMENTAIRE_PERE INTEGER NOT NULL DEFAULT AUTOINCREMENT,"
                + "ID_CUSTOMER INTEGER NOT NULL,"
                + "NM_COMMENTAIRE_PERE VARCHAR(200) NOT NULL,"
                + "DT_MODIF TIMESTAMP NULL," + "DT_SUPPR TIMESTAMP NULL,"
                + "PRIMARY KEY ( ID_COMMENTAIRE_PERE ASC ))";*/

        // -----------------------------------------------------------------------------------------------------
        tref_modele_famille = "CREATE TABLE TREF_MODELE_FAMILLE("
                + "ID_MODELE_FAMILLE INTEGER NOT NULL DEFAULT AUTOINCREMENT,"
                + "ID_MODELE_RAPPORT INTEGER NOT NULL,"
                + "NM_MODELE_FAMILLE VARCHAR(1024) NOT NULL,"
                + "DT_MODIF TIMESTAMP NULL," + "DT_SUPPR TIMESTAMP NULL,"
                + "TRIE_CONDITION_FAMILLE INTEGER NULL,"
                + "FL_RAPPORT_AUX integer NULL, "
                + "FL_SHARED BIT NOT NULL DEFAULT 0, "
                + "FL_TABLE BIT NOT NULL DEFAULT 0, "
                + "FL_PUBLIE BIT NOT NULL DEFAULT 0, "
                + "PRIMARY KEY ( ID_MODELE_FAMILLE ASC ))";

        tref_modele_item = "CREATE TABLE TREF_MODELE_ITEM("
                + "ID_MODELE_ITEM INTEGER NOT NULL DEFAULT AUTOINCREMENT,"
                + "ID_MODELE_FAMILLE INTEGER NOT NULL,"
                + "ID_TYPE_ITEM INTEGER NOT NULL DEFAULT 0,"
                + "NM_MODELE_ITEM VARCHAR(1024) NOT NULL,"
                + "DT_MODIF TIMESTAMP NULL," + "DT_SUPPR TIMESTAMP NULL,"
                + "OBLIGATOIRE INTEGER NOT NULL DEFAULT 0,"
                + "CONDITIONNE_PAR INTEGER NULL DEFAULT 0,"
                + "VALEUR_CONDITION VARCHAR(2048) NULL DEFAULT '0',"
                + "TRIE_CONDITION_ITEM INTEGER NULL,"
                + "VALEUR_DEFAUT VARCHAR(2048) NULL,"
                + "IMAGE LONG BINARY NULL,"
                + "DT_CREATED TIMESTAMP NOT NULL DEFAULT CURRENT TIMESTAMP,"
                + "FL_PRIVATE INTEGER NULL DEFAULT 0,"
                + "PRIMARY KEY ( ID_MODELE_ITEM ASC ))";

        tref_modele_rapport = "CREATE TABLE TREF_MODELE_RAPPORT("
                + "ID_MODELE_RAPPORT INTEGER NOT NULL DEFAULT AUTOINCREMENT,"
                + "ID_CUSTOMER INTEGER NOT NULL,"
                + "NM_MODELE_RAPPORT VARCHAR(1024) NOT NULL,"
                + "FL_PUBLIE INTEGER NOT NULL DEFAULT 0,"
                + "DT_MODIF TIMESTAMP NULL," + "DT_SUPPR TIMESTAMP NULL,"
                + "FL_DEFAULT INTEGER NOT NULL DEFAULT 0,"
                + "PRIMARY KEY ( ID_MODELE_RAPPORT ASC ))";

        tref_modele_value_item = "CREATE TABLE TREF_MODELE_VALUE_ITEM("
                + "ID_MODELE_ITEM INTEGER NOT NULL,"
                + "NM_VALUE_ITEM VARCHAR(2048) NOT NULL,"
                + "DT_MODIF TIMESTAMP NULL," + "DT_SUPPR TIMESTAMP NULL,"
                + "PRIMARY KEY ( ID_MODELE_ITEM ASC, NM_VALUE_ITEM ASC ))";

        tref_modules = "CREATE TABLE TREF_MODULES("
                + "ID_MODULE INTEGER NOT NULL,"
                + "NM_MODULE VARCHAR(100) NOT NULL,"
                + "DT_MODIF TIMESTAMP NULL," + "DT_SUPPR TIMESTAMP NULL,"
                + "PRIMARY KEY ( ID_MODULE ASC ))";

        tref_pieces = "CREATE TABLE TREF_PIECES("
                + "ID_PIECE INTEGER NOT NULL DEFAULT AUTOINCREMENT,"
                + "ID_CATEGORIE_PIECE INTEGER NOT NULL,"
                + "NM_PIECE VARCHAR(1024) NOT NULL,"
                + "CD_PRODUIT VARCHAR(1024) NULL,"
                + "PRIX_PIECE DECIMAL(12,2) NULL,"
                + "DT_MODIF TIMESTAMP NULL,"
                + "DT_SUPPR TIMESTAMP NULL,"
                + "ID_TAXRATE INTEGER NULL,"
                + "FL_TRACK_STOCK bit NOT NULL DEFAULT 0,"
                + "FL_SERIALIZABLE bit NOT NULL DEFAULT 0,"
                + "MIN_QUANTITY INTEGER NOT NULL DEFAULT 0,"
                + "QUANTITY INTEGER NOT NULL DEFAULT 0,"
                + "DESCRIPTION VARCHAR(16000) NULL,"
                + "TYPE VARCHAR(40) NOT NULL DEFAULT 'part',"
                + "PRIMARY KEY ( ID_PIECE ASC ))";

        tref_statut_intervention = "CREATE TABLE TREF_STATUT_INTERVENTION("
                + "CD_STATUT_INTERV INTEGER NOT NULL,"
                + "NM_STATUT_INTERVENTION VARCHAR(512) NOT NULL,"
                + "DT_MODIF TIMESTAMP NULL," + "DT_SUPPR TIMESTAMP NULL,"
                + "PRIMARY KEY ( CD_STATUT_INTERV ASC ))";

        tref_traductions = "CREATE TABLE TREF_TRADUCTIONS("
                + "CD_TRADUCTION VARCHAR(120) NOT NULL,"
                + "FL_PDA_TRAD INTEGER NOT NULL DEFAULT 0,"
                + "FR VARCHAR(1000) NOT NULL," + "EN VARCHAR(1000) NOT NULL,"
                + "DT_MODIF TIMESTAMP NULL," + "DT_SUPPR TIMESTAMP NULL,"
                + "PRIMARY KEY ( CD_TRADUCTION ASC ))";

        tref_traduction_cust = "CREATE TABLE TREF_TRADUCTIONS_CUST ("
                + "CD_TRADUCTION VARCHAR(120) NOT NULL,"
                + "ID_CUSTOMER INTEGER NOT NULL,"
                + "FR_CUST VARCHAR(1000) NULL," + "EN_CUST VARCHAR(1000) NULL,"
                + "DT_MODIF TIMESTAMP NULL," + "DT_SUPPR TIMESTAMP NULL,"
                + "PRIMARY KEY ( CD_TRADUCTION ASC, ID_CUSTOMER ASC ))";

        tref_type_intervention = "CREATE TABLE TREF_TYPE_INTERVENTION("
                + "ID_TYPE_INTERVENTION INTEGER NOT NULL DEFAULT AUTOINCREMENT,"
                + "ID_CUSTOMER INTEGER NOT NULL,"
                + "NM_TYPE_INTERVENTION VARCHAR(1024) NOT NULL,"
                + "DT_MODIF TIMESTAMP NULL," + "DT_SUPPR TIMESTAMP NULL,"
                + "FL_DEFAULT INTEGER NOT NULL DEFAULT 0,"
                + "HR_DUREE_INTERVENTION TIME NULL DEFAULT '02:00:00',"
                + "DEFAULT_PRIORITY INTEGER NULL,"
                + "PRIMARY KEY ( ID_TYPE_INTERVENTION ASC ))";

        // tref_custom_fields = "CREATE TABLE TREF_CUSTOM_FIELDS ("
        // + "ID_CUSTOM_FIELD INTEGER NOT NULL DEFAULT AUTOINCREMENT,"
        // + "ID_CUSTOMER INTEGER NOT NULL,"
        // + "NM_TABLE VARCHAR(512) NOT NULL,"
        // + "NM_CUSTOMS_FIELD VARCHAR(1024) NULL,"
        // + "NUM_ORDER INTEGER NULL,"
        // + "DT_MODIF TIMESTAMP NULL,"
        // + "DT_SUPPR TIMESTAMP NULL,"
        // +
        // "CONSTRAINT PK_TREF_TYPE_INTERVENTION PRIMARY KEY ( ID_CUSTOM_FIELD ASC ))";

        tref_gestion_acces = "CREATE TABLE TREF_GESTION_ACCES ("
                + "ID_CUSTOMER INTEGER NOT NULL,"
                + "FL_OPTION_TRACKING INTEGER NULL DEFAULT 0,"
                + "FL_OPTION_HELPSURFING INTEGER NULL DEFAULT 0,"
                + "FL_REPLANIFE_INTERV INTEGER NULL DEFAULT 0,"
                + "FL_DECLINER_INTERV INTEGER NULL DEFAULT 0,"
                + "FL_COMMENTS_REPORT INTEGER NULL DEFAULT 0,"
                + "FL_MANDATORY_DESCRIPTION INTEGER NULL DEFAULT 1,"
                + "FL_FORCE_REPORT_TEMPLATE INTEGER NULL DEFAULT 1,"
                + "FL_PAGE_SITES INTEGER NULL DEFAULT 1,"
                + "FL_CREATE_UPDATE_INVOICE_QUOTATION INTEGER NULL DEFAULT 0,"
                + "FL_MOB_PRICE INTEGER NULL DEFAULT 0,"
                + "FL_LIST_CUSTOMERS INTEGER NULL DEFAULT 0,"
                + "FL_SECTION_STOCK INTEGER NULL DEFAULT 0,"
                + "FL_SECTION_DELSIGN INTEGER NULL DEFAULT 0,"
                + "FL_STOCK_TAKEFROM INTEGER NULL DEFAULT 0,"
                + "FL_STOCK_SENDTO INTEGER NULL DEFAULT 0,"
                + "FL_STOCK_REQUESTFROM INTEGER NULL DEFAULT 0,"
                + "FL_TIME_TRACKING INTEGER NULL DEFAULT 0,"
                + "FL_SIGN_TECH INTEGER NOT NULL DEFAULT 1,"
                + "FL_SIGN_CUST INTEGER NOT NULL DEFAULT 1,"
                + "NUM_DECIMALS INTEGER NOT NULL DEFAULT 2,"
                + "FL_INV_STRICT BIT NOT NULL DEFAULT 0,"
                +"FL_MOB_LIVE_PICTURE_ONLY INTEGER NOT NULL DEFAULT 0, "
                + "PRIMARY KEY ( ID_CUSTOMER ASC ))";

        tref_typint_rapport = "CREATE TABLE TREF_TYPINT_RAPPORT ("
                + "ID_TYPE_INTERVENTION INTEGER NOT NULL,"
                + "ID_MODELE_RAPPORT INTEGER NOT NULL, "
                + "PRIMARY KEY ( ID_TYPE_INTERVENTION ))";

        // tdalkia_liste_operation = "CREATE TABLE TDALKIA_LISTE_OPERATIONS ("
        // + "ID_INTERVENTION VARCHAR(160) NOT NULL,"
        // + "OPERATION_INDENT INTEGER NULL,"
        // + "OPERATION_DESCRIPTION VARCHAR(1020) NULL,"
        // + "FL_DONE INTEGER NULL DEFAULT 0,"
        // + "OPERATION_CODE_HYGIENE_SECURITE VARCHAR(1020) NULL,"
        // + "OPERATION_MANDATORY_FL INTEGER NULL,"
        // + "PRIMARY KEY ( ID_INTERVENTION , OPERATION_DESCRIPTION ))";

        // tdalkia_bt_fis = "CREATE TABLE TDALKIA_BT_FIS ("
        // + "ID_INTERVENTION VARCHAR(160) NOT NULL, "
        // + "COMMENTAIRE_BT VARCHAR(1020) NULL, "
        // + "SEARCH_DESC_INSTALLATION VARCHAR(1020) NULL, "
        // + "TECHNICIENS_AFFECTATION VARCHAR(2048) NULL, "
        // + "FIS_No VARCHAR(80) NULL, " + "VIP VARCHAR(80) NULL, "
        // + "Delay DECIMAL(30,6) NULL, "
        // + "Reject_Cause VARCHAR(80) NULL, "
        //
        // + "Cause_Description_ML VARCHAR(1000) NULL, "
        // + "Sub_Cause_Description_ML VARCHAR(1000) NULL, "
        //
        // + "Sales_Meter_No VARCHAR(80) NULL, "
        // + "statutFIS VARCHAR(12) NULL, "
        // + "DescRefus VARCHAR(1020) NULL, "
        // + "DateRefus TIMESTAMP NULL, " + "DT_MODIF TIMESTAMP NULL,"
        // + "DT_SUPPR TIMESTAMP NULL,"
        //
        // + "Sales_Meter_Description VARCHAR(1000) NULL,"
        // + "Sales_Meter_Address_1 VARCHAR(200) NULL,"
        // + "Sales_Meter_Address_2 VARCHAR(200) NULL,"
        // + "Sales_Meter_Post_Code VARCHAR(50) NULL,"
        // + "Sales_Meter_City VARCHAR(50) NULL,"
        // + "FIS_Comments VARCHAR(1000) NULL,"
        // + "Alarm_Description_ML VARCHAR(1000) NULL,"
        // + "Building_Name VARCHAR(200) NULL,"
        // + "Building_Adress_1 VARCHAR(200) NULL,"
        // + "Building_Address_2 VARCHAR(200) NULL,"
        // + "Building_Post_code VARCHAR(200) NULL,"
        // + "Building_City VARCHAR(200) NULL,"
        // + "Contact_Name VARCHAR(200) NULL,"
        // + "Contact_VIP VARCHAR(1000) NULL,"
        // + "Contact_Phone_No VARCHAR(120) NULL,"
        // + "PRIMARY KEY ( ID_INTERVENTION ))";

        // tdalkia_refus = "CREATE TABLE TDALKIA_REFUS ("
        // + "CODE_REFUS VARCHAR(200) NOT NULL,"
        // + "DESCIPTION_REFUS VARCHAR(1000) NULL,"
        // + "DT_SUPPR TIMESTAMP NULL," + "DT_MODIF TIMESTAMP NULL,"
        // + "PRIMARY KEY ( CODE_REFUS ASC ))";

        tref_type_conge = "CREATE TABLE TREF_TYPE_CONGE ("
                + "ID_TYPE_CONGE INTEGER NOT NULL DEFAULT AUTOINCREMENT,"
                + "NOM_TYPE_CONGE VARCHAR(1024) NULL,"
                + "COULEUR_CONGE VARCHAR(64) NULL,"
                + "DT_SUPPR TIMESTAMP NULL,"
                + "ID_CUSTOMER INTEGER NULL,"
                + "FL_UNAVAILABLE BIT NOT NULL DEFAULT 0,"
                + "FL_DRIVING BIT NOT NULL DEFAULT 0,"
                + "FL_CLOCK BIT NOT NULL DEFAULT 0,"
                + "FL_PAYABLE BIT NOT NULL DEFAULT 0,"
                + "PRIMARY KEY ( ID_TYPE_CONGE ASC ))";

        t_conge = "CREATE TABLE T_CONGE ("
                + "ID_REMOTE VARCHAR(512) NOT NULL DEFAULT newid(),"
                + "ID_TYPE_CONGE INTEGER NULL," + "ID_USER INTEGER NULL,"
                + "DT_DEBUT TIMESTAMP NULL," + "DT_FIN TIMESTAMP NULL,"
                + "DT_SUPPR TIMESTAMP NULL," + "NOTES VARCHAR(2048) NOT NULL,"
                + "ID_GROUPE INTEGER NULL,"
                + "LAT_START DECIMAL(10,6) NULL,"
                + "LNG_START DECIMAL(10,6) NULL,"
                + "LAT_END DECIMAL(10,6) NULL,"
                + "LNG_END DECIMAL(10,6) NULL,"
                + "DISTANCE INTEGER NULL,"
                + "PRIMARY KEY ( ID_REMOTE ASC ))";

        t_attachment = "CREATE TABLE T_ATTACHMENTS ("
                + "ID_CUSTOMER INTEGER NOT NULL,"
                + "ID_ATTACHMENT INTEGER NOT NULL DEFAULT AUTOINCREMENT,"
                + "TOKEN_ATTACHMENT VARCHAR(128) NOT NULL DEFAULT newid(),"
                + "NM_ATTACHMENT VARCHAR(512) NULL,"
                + "SIZE_ATTACHMENT INTEGER NULL,"
                + "URL_ATTACHMENT VARCHAR(1024) NULL,"
                + "DT_MODIF TIMESTAMP NULL," + "DT_SUPPR TIMESTAMP NULL,"
                + "ID_INTERVENTION VARCHAR(40) NULL,"
                + "ID_CLIENT INTEGER NULL," + "ID_SITE_CLIENT INTEGER NULL,"
                + "ID_EQUIPEMENT_CLIENT INTEGER NULL,"
                + "PRIMARY KEY (ID_ATTACHMENT ASC ))";

        t_factures = "CREATE TABLE T_FACTURES ("
                + "ID_CUSTOMER INTEGER NOT NULL,"
                + "ID_INTERVENTION VARCHAR(160) NULL,"
                + "NO_INT_FACTURE_DEVIS INTEGER NULL,"
                + "FL_FACTURE INTEGER NOT NULL,"
                + "DT_CREATE TIMESTAMP NOT NULL,"
                + "CD_STATUT INTEGER NOT NULL DEFAULT 0,"
                + "ID_CLIENT INTEGER NULL," + "ID_SITE INTEGER NULL,"
                + "TOTAL_HT DECIMAL(20,4) NOT NULL DEFAULT 0,"
                + "VAL_TVA DECIMAL(20,4) NOT NULL DEFAULT 0,"
                + "TOTAL_TTC DECIMAL(20,4) NOT NULL DEFAULT 0,"
                + "ID_REMOTE VARCHAR(512) NOT NULL DEFAULT newid(),"
                + "NUM VARCHAR(1020) NULL DEFAULT NULL,"
                + "DISCOUNT DECIMAL(20, 4) NOT NULL DEFAULT 0,"
                + "DISCOUNTPERCENT BIT NOT NULL DEFAULT 1,"
                + "DESCRIPTION VARCHAR(16000) NULL,"
                + "FL_INV_STRICT BIT NOT NULL DEFAULT 0,"
                + "PRIMARY KEY (  ID_REMOTE ASC ))";

        t_lignes_facture = "CREATE TABLE T_LIGNES_FACTURE ("
                + "REF_LIGNE VARCHAR(1024) NULL,"
                + "DESCR_LIGNE VARCHAR(1020) NOT NULL,"
                + "PRIX_UNITAIRE DECIMAL(14,4) NOT NULL DEFAULT 0,"
                + "QUANTITE DECIMAL(6,2) NOT NULL DEFAULT 0,"
                + "VAL_TAXRATE DECIMAL(7,5) NOT NULL DEFAULT 0,"
                + "ORDRE INTEGER NULL," + "DT_SUPPR TIMESTAMP NULL,"
                + "DISCOUNT DECIMAL(20,4) NOT NULL DEFAULT 0,"
                + "TOTAL_HT DECIMAL(20,4) NOT NULL DEFAULT 0,"
                + "VAL_TVA DECIMAL(20,4) NOT NULL DEFAULT 0,"
                + "TOTAL_TTC DECIMAL(20,4) NOT NULL DEFAULT 0,"
                + "ID_REMOTE VARCHAR(512) NOT NULL DEFAULT newid(),"
                + "ID_REMOTE_FACTURE VARCHAR(512) NULL,"
                + "ID_TAXRATE INTEGER NULL DEFAULT NULL,"
                + "ID_PIECE INTEGER NULL DEFAULT NULL,"
                + "DESCRIPTION VARCHAR(16000) DEFAULT NULL,"
                + "ID_INTERVENTION VARCHAR(160) NULL DEFAULT NULL,"
                + "DISCOUNTPERCENT BIT NOT NULL DEFAULT 1,"
                + "PRIMARY KEY (  ID_REMOTE ASC ))";

        t_taxrates = "CREATE TABLE T_TAXRATES ("
                + "ID_TAXRATE INTEGER NOT NULL DEFAULT AUTOINCREMENT, "
                + "ID_CUSTOMER INTEGER NOT NULL, "
                + "NM_TAXRATE VARCHAR(1024) NOT NULL, "
                + "VAL_TAXRATE DECIMAL(7,5) NOT NULL, "
                + "IS_DEFAULT_TAXRATE INTEGER NOT NULL DEFAULT 0, "
                + "DT_SUPPR TIMESTAMP NULL, "
                + "PRIMARY KEY ( ID_TAXRATE ASC ) )";

        t_stocks = "CREATE TABLE T_STOCKS ("
                + "ID_STOCK UNIQUEIDENTIFIER NOT NULL DEFAULT newid(),"
                + "NM_STOCK VARCHAR(1024) NULL,"
                + "FL_DEFAULT BIT NOT NULL DEFAULT 0,"
                + "FL_PROVIDER BIT NOT NULL DEFAULT 0,"
                + "PRIMARY KEY ( ID_STOCK ASC ))";

        //new changes
        t_piece_Serials = "CREATE TABLE T_PIECE_SERIALS ("
                + "ID_PIECE_SERIAL UNIQUEIDENTIFIER NOT NULL DEFAULT newid(),"
                + "ID_STOCK UNIQUEIDENTIFIER NOT NULL,"
                + "ID_PIECE INTEGER NOT NULL," + "SERIAL VARCHAR(1024) NULL,"
                + "ID_INTERVENTION VARCHAR(160) NULL,"
                + "DT_USED TIMESTAMP NULL,"
                + "STATUS VARCHAR(20) NOT NULL DEFAULT 'ok',"
                + "ID_CLIENT INTEGER NULL,"
                + "ID_SITE_CLIENT INTEGER NULL,"
                + "ID_EQUIPEMENT_CLIENT INTEGER NULL,"
                + "PRIMARY KEY ( ID_PIECE_SERIAL ASC )"
                + ")";

//        //old changes
//        t_piece_Serials = "CREATE TABLE T_PIECE_SERIALS ("
//                + "ID_PIECE_SERIAL UNIQUEIDENTIFIER NOT NULL DEFAULT newid(),"
//                + "ID_STOCK UNIQUEIDENTIFIER NOT NULL,"
//                + "ID_PIECE INTEGER NOT NULL," + "SERIAL VARCHAR(1024) NULL,"
//                + "ID_INTERVENTION VARCHAR(160) NULL,"
//                + "DT_USED TIMESTAMP NULL,"
//                + "PRIMARY KEY ( ID_PIECE_SERIAL ASC ))";


        t_piece_demande = "CREATE TABLE T_PIECE_DEMANDE ("
                + "ID_PIECE_DEMANDE UNIQUEIDENTIFIER NOT NULL DEFAULT newid(),"
                + "ID_STOCK_SOURCE UNIQUEIDENTIFIER NOT NULL,"
                + "ID_STOCK_DESTINATION UNIQUEIDENTIFIER NOT NULL,"
                + "ID_PIECE INTEGER NOT NULL," + "ID_USER INTEGER NOT NULL,"
                + "QUANTITY INTEGER NOT NULL DEFAULT 0,"
                + "FL_TRANSFER BIT NOT NULL DEFAULT 0,"
                + "DT_CREATED TIMESTAMP NULL," + "DT_COMPLETED TIMESTAMP NULL,"
                + "FL_URGENT INTEGER NULL DEFAULT 0,"
                + "PRIMARY KEY ( ID_PIECE_DEMANDE ASC ))";

        t_clients_index = "CREATE INDEX T_CLIENT_INDEX ON T_CLIENTS (NM_CLIENT)";
        t_sites_clients_index = "CREATE INDEX T_SITES_CLIENTS_INDEX ON T_SITES_CLIENTS (NM_SITE)";
        t_equipment_client_index = "CREATE INDEX T_EQUIPEMENTS_CLIENTS_INDEX ON T_EQUIPEMENTS_CLIENTS (NM_EQUIPEMENT)";

        t_clients_index_search="CREATE INDEX T_CLIENT_INDEX_SEARCH ON T_CLIENTS (NM_CLIENT,ADR_CLIENT_VILLE,ADR_CLIENT_CP)";
        t_sites_clients_index = "CREATE INDEX T_SITES_CLIENTS_INDEX_SEARCH ON T_SITES_CLIENTS (NM_SITE,ADR_SITE_VILLE,ADR_SITE_CP)";



        //
        t_intervention_dt_deb_previndex = "CREATE INDEX T_INTERVENTION_DT_DEBUT_INDEX ON T_INTERVENTIONS (DT_DEB_PREV)";
        //
        t_intervention_dt_fin_realindex = "CREATE INDEX T_INTERVENTION_DT_FIN_INDEX ON T_INTERVENTIONS (DT_FIN_REAL)";

        t_attrib_messages_oer_fl_lu_index = "CREATE INDEX T_ATTRIB_MESSAGES_ORER_FL_LU_INDEX ON T_ATTRIB_MESSAGES_OPER (FL_LU)";
        //
        t_attrib_messages_oer_dt_modif_index = "CREATE INDEX T_ATTRIB_MESSAGES_ORER_DT_MODIF_INDEX ON T_ATTRIB_MESSAGES_OPER (DT_MODIF)";

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


        t_stock_pieces = "CREATE TABLE T_STOCK_PIECES (" +
                "ID_STOCK_PIECE UNIQUEIDENTIFIER NOT NULL DEFAULT newid()," +
                "ID_STOCK UNIQUEIDENTIFIER NOT NULL," +
                "ID_PIECE INTEGER NOT NULL," +
                "QUANTITY INTEGER NOT NULL DEFAULT 0," +
                "PRIMARY KEY ( ID_STOCK_PIECE ASC ))";

        t_payments = "CREATE TABLE T_PAYMENTS (" +
                "ID_PAYMENT UNIQUEIDENTIFIER NOT NULL DEFAULT newid()," +
                "ID_REMOTE VARCHAR(512) NOT NULL," +
                "MODE VARCHAR(40) NULL," +
                "RECEIVED DECIMAL(18,2) NOT NULL DEFAULT 0," +
                "REFUNDED DECIMAL(18,2) NOT NULL DEFAULT 0," +
                "CHARGE_ID VARCHAR(1020) NULL," +
                "NOTE VARCHAR(1020) NULL," +
                "DT_CREATED datetime NOT NULL," +
                "CONSTRAINT PK_T_PAYMENTS PRIMARY KEY ( ID_PAYMENT ASC )" +
                ")";

        t_payments_log = "CREATE TABLE T_PAYMENTS_LOG (" +
                "ID_PAYMENT_LOG UNIQUEIDENTIFIER NOT NULL DEFAULT newid()," +
                "ID_PAYMENT UNIQUEIDENTIFIER NOT NULL," +
                "ID_REMOTE VARCHAR(512) NOT NULL," +
                "AMOUNT DECIMAL(18,2) NOT NULL DEFAULT 0," +
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

        alter_T_STOCK_PIECES = "ALTER TABLE T_STOCK_PIECES ADD CONSTRAINT TREF_PIECES NOT NULL " +
                "FOREIGN KEY ( ID_PIECE ASC ) " +
                "REFERENCES TREF_PIECES ( ID_PIECE )";


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

        alter_T_PIECE_SERIALS = "ALTER TABLE T_PIECE_SERIALS ADD CONSTRAINT T_INTERVENTIONS " +
                "FOREIGN KEY ( ID_INTERVENTION ASC ) REFERENCES T_INTERVENTIONS ( ID_INTERVENTION )";

        t_job_windows = " CREATE TABLE T_JOB_WINDOWS (" +
                "ID_JOB_WINDOW INTEGER NOT NULL DEFAULT AUTOINCREMENT," +
                "LABEL VARCHAR(50) NULL," +
                "TIME_START TIME NULL," +
                "TIME_END TIME NULL," +
                "PRIMARY KEY ( ID_JOB_WINDOW ASC )" +
                ")";

        // ----------------------------------------V52_CHANGES--------------------------------------

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

        // ----------------------------------------V52_CHANGES--------------------------------------

        // ----------------------------------------V53_CHANGES--------------------------------------

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

        // ----------------------------------------V53_CHANGES--------------------------------------
        //-----------------------------------------v55_changes--------------------------------------

//        gps_steps = "CREATE TABLE GPS_STEPS("
//                +"ID_CUSTOMER INT NOT NULL,"
//                +"ID_ACTIVITY INTEGER NULL,"
//                +"ID_JOB VARCHAR(40) NULL,"
//                +"ID_USER INT NOT NULL,"
//                +"HORODATAGE TIMESTAMP NOT NULL,"
//                +"LAT VARCHAR(15) NOT NULL,"
//                +"LNG VARCHAR(15) NOT NULL,"
//                +"TYPE VARCHAR(20) NOT NULL,"
//                +"EVENT VARCHAR(20) NOT NULL,"
//                +"DATE_INDEX INT NOT NULL,"
//                +"PRIMARY KEY (ID_USER ASC,HORODATAGE ASC))";
//
//        gps_steps_index = "CREATE INDEX GPS_STEPS_ID_CUSTOMER_DATE_INDEX ON GPS_STEPS (ID_CUSTOMER,DATE_INDEX)";
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

    /**
     * Gets the t_session.
     *
     * @return the t_session
     */
    public String getT_session() {
        return t_session;
    }

    /**
     * Sets the t_session.
     *
     * @param tSession the new t_session
     */
    public void setT_session(String tSession) {
        t_session = tSession;
    }

    /**
     * Gets the t_session.
     *
     * @return the t_session
     */
    public String getAlter_T_PIECE_SERIALS() {
        return alter_T_PIECE_SERIALS;
    }

    /**
     * Gets the t_reprise_piece.
     *
     * @return the t_reprise_piece
     */
    public String getT_REPRISE_PIECE() {
        return t_reprise_piece;
    }


    /**
     * Gets the t_job_windows.
     *
     * @return the t_job_windows
     */
    public String getT_JOB_WINDOWS() {
        return t_job_windows;
    }


    /**
     * Gets the t_attrib_messsages_oper.
     *
     * @return the t_attrib_messsages_oper
     */
    public String getT_attrib_messsages_oper() {
        return t_attrib_messsages_oper;
    }

    /**
     * Gets the t_attrib_modules.
     *
     * @return the t_attrib_modules
     */
    public String getT_attrib_modules() {
        return t_attrib_modules;
    }

    /**
     * Gets the t_clients.
     *
     * @return the t_clients
     */
    public String getT_clients() {
        return t_clients;
    }

    /**
     * Gets the t_customers.
     *
     * @return the t_customers
     */
    public String getT_customers() {
        return t_customers;
    }

    /**
     * Gets the t_equipements_clients.
     *
     * @return the t_equipements_clients
     */
    public String getT_equipements_clients() {
        return t_equipements_clients;
    }

    /**
     * Gets the t_interventions.
     *
     * @return the t_interventions
     */
    public String getT_interventions() {
        return t_interventions;
    }

    /**
     * Gets the t_messages_operateur.
     *
     * @return the t_messages_operateur
     */
    public String getT_messages_operateur() {
        return t_messages_operateur;
    }

    /**
     * Gets the t_photo_pda.
     *
     * @return the t_photo_pda
     */
    public String getT_photo_pda() {
        return t_photo_pda;
    }

    /**
     * Gets the t_saisie_rapport.
     *
     * @return the t_saisie_rapport
     */
    public String getT_saisie_rapport() {
        return t_saisie_rapport;
    }

    /**
     * Gets the t_sites_client.
     *
     * @return the t_sites_client
     */
    public String getT_sites_client() {
        return t_sites_client;
    }

    /**
     * Gets the t_sortie_piece.
     *
     * @return the t_sortie_piece
     */
    public String getT_sortie_piece() {
        return t_sortie_piece;
    }

    /**
     * Gets the t_temps_interv.
     *
     * @return the t_temps_interv
     */
    public String getT_temps_interv() {
        return t_temps_interv;
    }

    /**
     * Gets the t_users.
     *
     * @return the t_users
     */
    public String getT_users() {
        return t_users;
    }

    /**
     * Gets the t_val_custom_fields.
     *
     * @return the t_val_custom_fields
     */
    // public String getT_val_custom_fields() {
    // return t_val_custom_fields;
    // }

    /**
     * Gets the tref_categorie_piece.
     *
     * @return the tref_categorie_piece
     */
    public String getTref_categorie_piece() {
        return tref_categorie_piece;
    }

    /**
     * Gets the tref_commentaires_fils.
     *
     * @return the tref_commentaires_fils
     */
//    public String getTref_commentaires_fils() {
//        return tref_commentaires_fils;
//    }

    /**
     * Gets the tref_commentaires_pere.
     *
     * @return the tref_commentaires_pere
     */
//    public String getTref_commentaires_pere() {
//        return tref_commentaires_pere;
//    }

    /**
     * Gets the tref_modele_famille.
     *
     * @return the tref_modele_famille
     */
    public String getTref_modele_famille() {
        return tref_modele_famille;
    }

    /**
     * Gets the tref_modele_item.
     *
     * @return the tref_modele_item
     */
    public String getTref_modele_item() {
        return tref_modele_item;
    }

    /**
     * Gets the tref_modele_rapport.
     *
     * @return the tref_modele_rapport
     */
    public String getTref_modele_rapport() {
        return tref_modele_rapport;
    }

    /**
     * Gets the tref_modele_value_item.
     *
     * @return the tref_modele_value_item
     */
    public String getTref_modele_value_item() {
        return tref_modele_value_item;
    }

    /**
     * Gets the tref_modules.
     *
     * @return the tref_modules
     */
    public String getTref_modules() {
        return tref_modules;
    }

    /**
     * Gets the tref_pieces.
     *
     * @return the tref_pieces
     */
    public String getTref_pieces() {
        return tref_pieces;
    }

    /**
     * Gets the tref_statut_intervention.
     *
     * @return the tref_statut_intervention
     */
    public String getTref_statut_intervention() {
        return tref_statut_intervention;
    }

    /**
     * Gets the tref_traductions.
     *
     * @return the tref_traductions
     */
    public String getTref_traductions() {
        return tref_traductions;
    }

    /**
     * Gets the tref_traduction_cust.
     *
     * @return the tref_traduction_cust
     */
    public String getTref_traduction_cust() {
        return tref_traduction_cust;
    }

    /**
     * Gets the tref_type_intervention.
     *
     * @return the tref_type_intervention
     */
    public String getTref_type_intervention() {
        return tref_type_intervention;
    }

    /**
     * Gets the tref_custom_fields.
     *
     * @return the tref_custom_fields
     */
    // public String getTref_custom_fields() {
    // return tref_custom_fields;
    // }

    /**
     * Gets the tref_gestion_acces.
     *
     * @return the tref_gestion_acces
     */
    public String getTref_gestion_acces() {
        return tref_gestion_acces;
    }

    /**
     * Gets the tref_typint_rapport.
     *
     * @return the tref_typint_rapport
     */
    public String getTref_typint_rapport() {
        return tref_typint_rapport;
    }

    /**
     * Gets the tdalkia_liste_operation.
     *
     * @return the tdalkia_liste_operation
     */
    // public String getTdalkia_liste_operation() {
    // return tdalkia_liste_operation;
    // }

    /**
     * Gets the tdalkia_bt_fis.
     *
     * @return the tdalkia_bt_fis
     */
    // public String getTdalkia_bt_fis() {
    // return tdalkia_bt_fis;
    // }

    /**
     * Gets the tdalkia_refus.
     *
     * @return the tdalkia_refus
     */
    // public String getTdalkia_refus() {
    // return tdalkia_refus;
    // }

    /**
     * Gets the t_conge.
     *
     * @return the t_conge
     */
    public String getT_conge() {
        return t_conge;
    }

    /**
     * Gets the tref_type_conge.
     *
     * @return the tref_type_conge
     */
    public String getTref_type_conge() {
        return tref_type_conge;
    }

    /**
     * Gets the t_clients_index.
     *
     * @return the t_clients_index
     */
    public String getT_clients_index() {
        return t_clients_index;
    }

    /**
     * Gets the t_sites_clients_index.
     *
     * @return the t_sites_clients_index
     */
    public String getT_sites_clients_index() {
        return t_sites_clients_index;
    }

    /**
     * Gets the t_equipment_client_index.
     *
     * @return the t_equipment_client_index
     */
    public String getT_equipment_client_index() {
        return t_equipment_client_index;
    }

    /**
     * Gets the t_intervention_dt_deb_previndex.
     *
     * @return the t_intervention_dt_deb_previndex
     */
    public String getT_intervention_dt_deb_previndex() {
        return t_intervention_dt_deb_previndex;
    }

    /**
     * Gets the t_intervention_dt_fin_realindex.
     *
     * @return the t_intervention_dt_fin_realindex
     */
    public String getT_intervention_dt_fin_realindex() {
        return t_intervention_dt_fin_realindex;
    }

    /**
     * Gets the t_attrib_messages_oer_fl_lu_index.
     *
     * @return the t_attrib_messages_oer_fl_lu_index
     */
    public String getT_attrib_messages_oer_fl_lu_index() {
        return t_attrib_messages_oer_fl_lu_index;
    }

    //
    //

    /**
     * Gets the t_attrib_messages_oer_dt_modif_index.
     *
     * @return the t_attrib_messages_oer_dt_modif_index
     */
    public String getT_attrib_messages_oer_dt_modif_index() {
        return t_attrib_messages_oer_dt_modif_index;
    }

    public String getT_Attachment() {
        return t_attachment;
    }

    public String getT_Factures() {
        return t_factures;
    }

    public String getT_Lignes_Facture() {
        return t_lignes_facture;
    }

    public String getT_Taxrates() {
        return t_taxrates;
    }

    public String getT_Stocks() {
        return t_stocks;
    }

    public String getT_Piece_Serials() {
        return t_piece_Serials;
    }

    public String getT_Piece_Demande() {
        return t_piece_demande;
    }

    /**
     * Gets tref_modele_bloc table
     *
     * @return tref_modele_bloc
     */
    public String getTref_modele_bloc() {
        return tref_modele_bloc;
    }

    /**
     * gets t_saisie_bloc table
     *
     * @return t_saisie_bloc
     */
    public String getT_saisie_bloc() {
        return t_saisie_bloc;
    }

    public String getT_stock_pieces() {
        return t_stock_pieces;
    }

    /*
     * public String getTref_pieces_index() { return tref_pieces_index; }
     *
     * public void setTref_pieces_index(String tref_pieces_index) {
     * this.tref_pieces_index = tref_pieces_index; }
     *
     * public String getTref_categorie_piece_index() { return
     * tref_categorie_piece_index; }
     *
     * public void setTref_categorie_piece_index(String
     * tref_categorie_piece_index) { this.tref_categorie_piece_index =
     * tref_categorie_piece_index; }
     */

    // version 45 payments tables updated


    public String getT_payments() {
        return t_payments;
    }

    public String getT_payments_log() {
        return t_payments_log;
    }

    public String getC_connections() {
        return c_connections;
    }

    public String getAlter_T_STOCK_PIECES() {
        return alter_T_STOCK_PIECES;
    }

    public String getT_incidents() {
        return t_incidents;
    }

    public String getT_incidents_log() {
        return t_incidents_log;
    }

    // v55

    public String getGps_steps() {
        return gps_steps;
    }

    public void setGps_steps(String gps_steps) {
        this.gps_steps = gps_steps;
    }
}