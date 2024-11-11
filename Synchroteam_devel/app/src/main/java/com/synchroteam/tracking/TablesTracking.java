package com.synchroteam.tracking;

// TODO: Auto-generated Javadoc
/**
 * The Class TablesTracking.
 *
 * @author Previous Developer
 */
public class TablesTracking {
	
	/** The t_activite. */
	private String t_activite;
	
	/** The t_suivi. */
	private String t_suivi;
	
	/** The t_session_gps. */
	private String t_session_gps;
	
	/** The t_param_customer. */
	private String t_param_customer;

	/** The gps_steps. */
	private String gps_steps;

	/** The gps_steps_index. */
	private String gps_steps_index;
	
	
	
	
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

		// v55 new table
		gps_steps = "CREATE TABLE GPS_STEPS("
				+"ID_CUSTOMER INT NOT NULL,"
				+"ID_ACTIVITY INTEGER NULL,"
				+"ID_JOB VARCHAR(40) NULL,"
				+"ID_USER INT NOT NULL,"
				+"HORODATAGE TIMESTAMP NOT NULL,"
				+"LAT VARCHAR(15) NOT NULL,"
				+"LNG VARCHAR(15) NOT NULL,"
				+"TYPE VARCHAR(20) NOT NULL,"
				+"EVENT VARCHAR(20) NOT NULL,"
				+"DATE_INDEX INT NOT NULL,"
				+"ID_ACTIVITY_REMOTE VARCHAR(512) NULL,"
				+"PRIMARY KEY (ID_USER ASC,HORODATAGE ASC))";

		gps_steps_index = "CREATE INDEX GPS_STEPS_ID_CUSTOMER_DATE_INDEX ON GPS_STEPS (ID_CUSTOMER,DATE_INDEX)";
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

	/**
	 * Gets the gps_steps.
	 *
	 * @return the gps_steps
	 */
	public String getGps_steps() {
		return gps_steps;
	}

	/**
	 * Gets the gps_steps_index.
	 *
	 * @return the gps_steps_index
	 */
	public String getGps_steps_index() {
		return gps_steps_index;
	}
}
