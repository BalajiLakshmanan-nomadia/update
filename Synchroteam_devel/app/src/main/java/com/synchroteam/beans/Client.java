package com.synchroteam.beans;


// TODO: Auto-generated Javadoc

/**
 * The Class Client holds the data of clients required in NewJobs.
 */
public class Client {

    /**
     * The id client.
     */
    private int idClient;

    /**
     * The nm client.
     */
    private String nmClient;

    /**
     * The rue client.
     */
    private String rueClient;

    /**
     * The ville client.
     */
    private String villeClient;
    /**
     * The ville client.
     */
    private String add_client_pays;
    private String add_client_cp;
    /**
     * The adresse global client.
     */
    private String adresseGlobalClient;

    /**
     * The adresse compl client.
     */
    private String adresseComplClient;

    /**
     * The gps x.
     */
    private String gpsX;

    /**
     * The gps y.
     */
    private String gpsY;


    private String nom_contact_client;

    private String telephoneClient;
    private String publicLink;

    private String emailContactClient;

    public String getEmailContactClient() {
        return emailContactClient;
    }


    public void setEmailContactClient(String emailContactClient) {
        this.emailContactClient = emailContactClient;
    }


    public String getMobileContactClient() {
        return mobileContactClient;
    }


    public void setMobileContactClient(String mobileContactClient) {
        this.mobileContactClient = mobileContactClient;
    }

    private String mobileContactClient;


    private String preNomContactClient;

    public String getAdd_client_prov() {
        return add_client_prov;
    }

    public void setAdd_client_prov(String add_client_prov) {
        this.add_client_prov = add_client_prov;
    }

    private String add_client_prov;

    public String getPreNomContactClient() {
        return preNomContactClient;
    }


    public void setPreNomContactClient(String preNomContactClient) {
        this.preNomContactClient = preNomContactClient;
    }

    // REF_CUSTOMER

    private String ref_customer;

    public String getRef_customer() {
        return ref_customer;
    }

    public void setRef_customer(String ref_customer) {
        this.ref_customer = ref_customer;
    }

    /**
     * Instantiates a new client.
     */


    public Client() {
        super();
        // TODO Auto-generated constructor stub
    }


    /**
     * Instantiates a new client.
     *
     * @param idClient      the id client
     * @param nmClient      the nm client
     * @param rueClient     the rue client
     * @param villeClient   the ville client
     * @param adresseGlobal the adresse global
     * @param adresseCompl  the adresse compl
     * @param gpsX          the gps x
     * @param gpsY          the gps y
     */
    public Client(int idClient, String nmClient, String rueClient,
                  String villeClient, String adresseGlobal,
                  String adresseCompl, String gpsX, String gpsY,String ref_customer) {
        super();
        this.idClient = idClient;
        this.nmClient = nmClient;
        this.rueClient = rueClient;
        this.villeClient = villeClient;
        this.adresseGlobalClient = adresseGlobal;
        this.adresseComplClient = adresseCompl;
        this.gpsX = gpsX;
        this.gpsY = gpsY;
        this.ref_customer = ref_customer;

    }

    /**
     * Instantiates a new client.
     *
     * @param idClient      the id client
     * @param nmClient      the nm client
     * @param rueClient     the rue client
     * @param villeClient   the ville client
     * @param adresseGlobal the adresse global
     * @param adresseCompl  the adresse compl
     * @param gpsX          the gps x
     * @param gpsY          the gps y
     */
    public Client(int idClient, String nmClient, String rueClient,
                  String villeClient, String adresseGlobal,
                  String adresseCompl, String gpsX, String gpsY) {
        super();
        this.idClient = idClient;
        this.nmClient = nmClient;
        this.rueClient = rueClient;
        this.villeClient = villeClient;
        this.adresseGlobalClient = adresseGlobal;
        this.adresseComplClient = adresseCompl;
        this.gpsX = gpsX;
        this.gpsY = gpsY;
    }


    /**
     * Instantiates a new client.
     *
     * @param idClient      the id client
     * @param nmClient      the nm client
     * @param rueClient     the rue client
     * @param villeClient   the ville client
     * @param adresseGlobal the adresse global
     * @param adresseCompl  the adresse compl
     * @param gpsX          the gps x
     * @param gpsY          the gps y
     */
    public Client(int idClient, String nmClient, String rueClient,
                  String villeClient, String adresseGlobal, String adresseCompl,
                  String gpsX, String gpsY, String nom_contact_client,
                  String telephoneClient, String publicLink, String emailContactClient,
                  String mobileContactClient, String preNomContactClient, String add_client_prov,
                  String add_client_pays, String add_client_cp) {
        super();
        this.idClient = idClient;
        this.nmClient = nmClient;
        this.rueClient = rueClient;
        this.villeClient = villeClient;
        this.adresseGlobalClient = adresseGlobal;
        this.adresseComplClient = adresseCompl;
        this.gpsX = gpsX;
        this.gpsY = gpsY;
        this.nom_contact_client = nom_contact_client;
        this.telephoneClient = telephoneClient;
        this.publicLink = publicLink;
        this.emailContactClient = emailContactClient;
        this.mobileContactClient = mobileContactClient;
        this.preNomContactClient = preNomContactClient;
        this.add_client_prov = add_client_prov;
        this.add_client_pays = add_client_pays;
        this.add_client_cp = add_client_cp;
    }

    //with ref_customer

    public Client(int idClient, String nmClient, String rueClient,
                  String villeClient, String adresseGlobal, String adresseCompl,
                  String gpsX, String gpsY, String nom_contact_client,
                  String telephoneClient, String publicLink, String emailContactClient,
                  String mobileContactClient, String preNomContactClient, String add_client_prov,
                  String add_client_pays, String add_client_cp,String ref_customer) {
        super();
        this.idClient = idClient;
        this.nmClient = nmClient;
        this.rueClient = rueClient;
        this.villeClient = villeClient;
        this.adresseGlobalClient = adresseGlobal;
        this.adresseComplClient = adresseCompl;
        this.gpsX = gpsX;
        this.gpsY = gpsY;
        this.nom_contact_client = nom_contact_client;
        this.telephoneClient = telephoneClient;
        this.publicLink = publicLink;
        this.emailContactClient = emailContactClient;
        this.mobileContactClient = mobileContactClient;
        this.preNomContactClient = preNomContactClient;
        this.add_client_prov = add_client_prov;
        this.add_client_pays = add_client_pays;
        this.add_client_cp = add_client_cp;
        this.ref_customer = ref_customer;
    }

    public String getAdd_client_pays() {
        return add_client_pays;
    }

    public String getAdd_client_cp() {
        return add_client_cp;
    }

    /**
     * Gets the id client.
     *
     * @return the id client
     */
    public int getIdClient() {
        return idClient;
    }

    public String getTelephoeClient() {
        return telephoneClient;

    }

    public String getPublicLink() {

        return publicLink;
    }

    public String getNomContactClient() {

        return nom_contact_client;
    }

    /**
     * Gets the nm client.
     *
     * @return the nm client
     */
    public String getNmClient() {
        return nmClient;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return nmClient;
    }

    /**
     * Gets the rue client.
     *
     * @return the rue client
     */
    public String getRueClient() {
        return rueClient;
    }

    /**
     * Gets the ville client.
     *
     * @return the ville client
     */
    public String getVilleClient() {
        return villeClient;
    }

    /**
     * Gets the adresse global client.
     *
     * @return the adresse global client
     */
    public String getAdresseGlobalClient() {
        return adresseGlobalClient;
    }

    /**
     * Gets the adresse compl client.
     *
     * @return the adresse compl client
     */
    public String getAdresseComplClient() {
        return adresseComplClient;
    }

    /**
     * Gets the gps x.
     *
     * @return the gps x
     */
    public String getGpsX() {
        return gpsX;
    }

    /**
     * Gets the gps y.
     *
     * @return the gps y
     */
    public String getGpsY() {
        return gpsY;
    }


//	@Override
//	public int hashCode() {
//		return nmClient.charAt(0);
//		
//		/*if(TextUtils.isEmpty(nmClient)){
//			return super.hashCode();
//		}
//		else{
//			return nmClient.charAt(0);
//		}*/		
//	}
//	
//	
//	@Override
//	public boolean equals(Object o) {
//		
//		return nmClient.equals(o);
//	}
}
