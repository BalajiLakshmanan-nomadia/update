package com.synchroteam.beans;

// TODO: Auto-generated Javadoc

/**
 * This class is used by the NewJob Screen to show Sites The Class Site.
 *
 * @author ${Previous Developer}
 */
public class Site {

    /**
     * The id site.
     */
    private int idSite;

    /**
     * The nm site.
     */
    private String nmSite;
    /**
     * The nm site.
     */
    private String add_site_province;

    /**
     * The rue site.
     */
    private String rueSite;

    /**
     * The ville site.
     */
    private String villeSite;

    /**
     * The adresse.
     */
    private String adresse;

    /**
     * The adresse compl.
     */
    private String adresseCompl;

    /**
     * The gps x.
     */
    private String gpsX;

    /**
     * The gps y.
     */
    private String gpsY;

    private String telephoneContactSite;

    private String mobileContactSite;

    private String emailContactSite;

    private String preNomContactSite;

    private String nomContactSite;

    private int idClient;

    private String publicLink;
    /**
     * The cp.
     */
    private String cp = "";

    private String refCustomer;


    public String getAdd_site_province() {
        return add_site_province;
    }

    public void setAdd_site_province(String add_site_province) {
        this.add_site_province = add_site_province;
    }

    /**
     * The pays.
     */
    private String pays = "";

    public String getRefCustomer() {
        return refCustomer;
    }

    /*
     * public Site(int idSite, String nmSite, String rueSite, String villeSite)
     * { super(); this.idSite = idSite; this.nmSite = nmSite; this.rueSite =
     * rueSite; this.villeSite = villeSite; }
     */

    /**
     * Instantiates a new site.
     *
     * @param idSite       the id site
     * @param nmSite       the nm site
     * @param rueSite      the rue site
     * @param villeSite    the ville site
     * @param adresse      the adresse
     * @param adresseCompl the adresse compl
     * @param gpsX         the gps x
     * @param gpsY         the gps y
     */
    public Site(int idSite, String nmSite, String rueSite, String villeSite,
                String adresse, String adresseCompl, String gpsX, String gpsY,
                String telephoneContactSite,
                String preNomContactSite, String nomContactSite, String emailContactSite, String
                        publicLink, String mobileContactSite, String cp, String pays,String add_site_province) {
        super();
        this.idSite = idSite;
        this.nmSite = nmSite;
        this.rueSite = rueSite;
        this.villeSite = villeSite;
        this.adresse = adresse;
        this.adresseCompl = adresseCompl;
        this.gpsX = gpsX;
        this.gpsY = gpsY;
        this.telephoneContactSite = telephoneContactSite;
        this.preNomContactSite = preNomContactSite;
        this.nomContactSite = nomContactSite;
        this.emailContactSite = emailContactSite;
        this.publicLink = publicLink;
        this.mobileContactSite = mobileContactSite;
        this.cp = cp;
        this.pays = pays;
        this.add_site_province =add_site_province;
    }
// with ref customer
    public Site(int idSite, String nmSite, String rueSite, String villeSite,
                String adresse, String adresseCompl, String gpsX, String gpsY,
                String telephoneContactSite,
                String preNomContactSite, String nomContactSite, String emailContactSite, String
                        publicLink, String mobileContactSite, String cp, String pays,String add_site_province,String refCustomer) {
        super();
        this.idSite = idSite;
        this.nmSite = nmSite;
        this.rueSite = rueSite;
        this.villeSite = villeSite;
        this.adresse = adresse;
        this.adresseCompl = adresseCompl;
        this.gpsX = gpsX;
        this.gpsY = gpsY;
        this.telephoneContactSite = telephoneContactSite;
        this.preNomContactSite = preNomContactSite;
        this.nomContactSite = nomContactSite;
        this.emailContactSite = emailContactSite;
        this.publicLink = publicLink;
        this.mobileContactSite = mobileContactSite;
        this.cp = cp;
        this.pays = pays;
        this.add_site_province =add_site_province;
        this.refCustomer=refCustomer;
    }

    public Site(int idSite, String nmSite, String rueSite,
                  String villeSite, String adresse, String adresseCompl, String gpsX,
                  String gpsY, String cp, String pays) {
        super();
        this.idSite = idSite;
        this.nmSite = nmSite;
        this.rueSite = rueSite;
        this.villeSite = villeSite;
        this.adresse = adresse;
        this.adresseCompl = adresseCompl;
        this.gpsX = gpsX;
        this.gpsY = gpsY;
        this.cp = cp;
        this.pays = pays;
    }

    //with refCustomer
    public Site(int idSite, String nmSite, String rueSite,
                String villeSite, String adresse, String adresseCompl, String gpsX,
                String gpsY, String cp, String pays,String refCustomer) {
        super();
        this.idSite = idSite;
        this.nmSite = nmSite;
        this.rueSite = rueSite;
        this.villeSite = villeSite;
        this.adresse = adresse;
        this.adresseCompl = adresseCompl;
        this.gpsX = gpsX;
        this.gpsY = gpsY;
        this.cp = cp;
        this.pays = pays;
        this.refCustomer = refCustomer;
    }

    public Site(int idSite, int idClient, String nmSite, String rueSite,
                String villeSite, String adresse, String adresseCompl, String gpsX,
                String gpsY, String cp, String pays) {
        super();
        this.idSite = idSite;
        this.idClient = idClient;
        this.nmSite = nmSite;
        this.rueSite = rueSite;
        this.villeSite = villeSite;
        this.adresse = adresse;
        this.adresseCompl = adresseCompl;
        this.gpsX = gpsX;
        this.gpsY = gpsY;
        this.cp = cp;
        this.pays = pays;
    }


    /**
     * Instantiates a new site.
     */
    public Site() {
        super();
    }

    /**
     * Gets the id site.
     *
     * @return the id site
     */
    public int getIdSite() {
        return idSite;
    }

    /**
     * Gets the nm site.
     *
     * @return the nm site
     */
    public String getNmSite() {
        return nmSite;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return nmSite;
    }

    /**
     * Gets the rue site.
     *
     * @return the rue site
     */
    public String getRueSite() {
        return rueSite;
    }

    /**
     * Gets the cp site.
     *
     * @return the cp site
     */
    public String getCPSite() {
        return cp;
    }

    /**
     * Gets the pays site.
     *
     * @return the pays site
     */
    public String getPaysSite() {
        return pays;
    }

    /**
     * Gets the ville site.
     *
     * @return the ville site
     */
    public String getVilleSite() {
        return villeSite;
    }

    /**
     * Gets the adresse.
     *
     * @return the adresse
     */
    public String getAdresse() {
        return adresse;
    }

    /**
     * Sets the adresse.
     *
     * @param adresse the new adresse
     */
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    /**
     * Gets the adresse compl.
     *
     * @return the adresse compl
     */
    public String getAdresseCompl() {
        return adresseCompl;
    }

    /**
     * Sets the adresse compl.
     *
     * @param adresseCompl the new adresse compl
     */
    public void setAdresseCompl(String adresseCompl) {
        this.adresseCompl = adresseCompl;
    }

    /**
     * Sets the id site.
     *
     * @param idSite the new id site
     */
    public void setIdSite(int idSite) {
        this.idSite = idSite;
    }

    /**
     * Sets the nm site.
     *
     * @param nmSite the new nm site
     */
    public void setNmSite(String nmSite) {
        this.nmSite = nmSite;
    }

    /**
     * Sets the rue site.
     *
     * @param rueSite the new rue site
     */
    public void setRueSite(String rueSite) {
        this.rueSite = rueSite;
    }

    /**
     * Sets the ville site.
     *
     * @param villeSite the new ville site
     */
    public void setVilleSite(String villeSite) {
        this.villeSite = villeSite;
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
     * Sets the gps x.
     *
     * @param gpsX the new gps x
     */
    public void setGpsX(String gpsX) {
        this.gpsX = gpsX;
    }

    /**
     * Gets the gps y.
     *
     * @return the gps y
     */
    public String getGpsY() {
        return gpsY;
    }

    /**
     * Sets the gps y.
     *
     * @param gpsY the new gps y
     */
    public void setGpsY(String gpsY) {
        this.gpsY = gpsY;
    }

    public String getTelephoneContactSite() {
        return telephoneContactSite;
    }

    public void setTelephoneContactSite(String telephoneContactSite) {
        this.telephoneContactSite = telephoneContactSite;
    }

    public String getMobileContactSite() {
        return mobileContactSite;
    }

    public void setMobileContactSite(String mobileContactSite) {
        this.mobileContactSite = mobileContactSite;
    }

    public String getEmailContactSite() {
        return emailContactSite;
    }

    public void setEmailContactSite(String emailContactSite) {
        this.emailContactSite = emailContactSite;
    }

    public String getPreNomContactSite() {
        return preNomContactSite;
    }

    public void setPreNomContactSite(String preNomContactSite) {
        this.preNomContactSite = preNomContactSite;
    }

    public String getNomContactSite() {
        return nomContactSite;
    }

    public void setNomContactSite(String nomContactSite) {
        this.nomContactSite = nomContactSite;
    }

    /**
     * Gets public link for site
     *
     * @return
     */
    public String getPublicLink() {
        return publicLink;
    }
}
