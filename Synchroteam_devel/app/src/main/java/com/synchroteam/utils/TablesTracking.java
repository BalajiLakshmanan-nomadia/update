package com.synchroteam.utils;

public class TablesTracking {
	
	/** The t_activite. */
	private String t_activite;
	
	/** The t_suivi. */
	private String t_suivi;
	
	/** The t_session_gps. */
	private String t_session_gps;
	
	/** The t_param_customer. */
	private String t_param_customer;
	
	
	
	
	/**
	 * Instantiates a new tables tracking.
	 */
	public TablesTracking() {
		super();
		
		t_activite="CREATE TABLE GPS_ACTIVITE ("
				  +"USER_ID integer NOT NULL,"
				  +"TYPE_ACTIVITE varchar(20) NOT NULL,"
				  +"HORODATAGE timestamp NOT NULL,"
				  +"DT_MODIF timestamp NULL,"
				  +"DT_SUPPR timestamp NULL,"
				  +"SAISIE varchar(30) NULL,"
				  +"PRIMARY KEY ( USER_ID ASC, TYPE_ACTIVITE ASC, HORODATAGE ASC ) WITH MAX HASH SIZE 4)";
		
		t_suivi="CREATE TABLE GPS_SUIVI ("
				  +"USER_ID integer NOT NULL,"
				  +"LATITUDE varchar(15) NOT NULL,"
				  +"LONGITUDE varchar(15) NOT NULL,"
				  +"VITESSE varchar(20) NOT NULL,"
				  +"HORODATAGE timestamp NOT NULL,"
				  +"DT_MODIF timestamp NULL,"
				  +"DT_SUPPR timestamp NULL,"
				  +"PRIMARY KEY ( USER_ID ASC, HORODATAGE ASC ) WITH MAX HASH SIZE 4)";
		
		t_session_gps="CREATE TABLE T_SESSION_GPS ("
			      +"NOM varchar(20) NOT NULL,"
			      +"VALEUR varchar(20) NOT NULL DEFAULT '0',"
			      +"PRIMARY KEY ( NOM ASC ) WITH MAX HASH SIZE 4,SYNCHRONIZE OFF)";
		
		
	/*	t_param_customer="CREATE TABLE TREF_PARAMETRES_CUSTOMER ("
				  +"ID_CUSTOMER integer NOT NULL,"
				  +"NOM_OPTION varchar(50) NOT NULL,"
				  +"VALEUR_OPTION varchar(50) NOT NULL,"
				  +"DT_MODIF timestamp NULL,"
				  +"DT_SUPPR timestamp NULL,"
				  +"PRIMARY KEY ( ID_CUSTOMER ASC, NOM_OPTION ASC ) WITH MAX HASH SIZE 4)"; */
	}
	
	/**
	 * Gets the t_activite.
	 *
	 * @return the t_activite
	 */
	public String getT_activite() {
		return t_activite;
	}
	
	/**
	 * Gets the t_suivi.
	 *
	 * @return the t_suivi
	 */
	public String getT_suivi() {
		return t_suivi;
	}
	
	/**
	 * Gets the t_session_gps.
	 *
	 * @return the t_session_gps
	 */
	public String getT_session_gps() {
		return t_session_gps;
	}
	
	/**
	 * Gets the t_param_customer.
	 *
	 * @return the t_param_customer
	 */
	public String getT_param_customer() {
		return t_param_customer;
	}
	
	

}
