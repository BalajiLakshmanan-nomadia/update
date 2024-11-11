package com.synchroteam.beans;

// TODO: Auto-generated Javadoc

/**
 * The Class CurrentJobsBean to hold data of Current Jobs.
 */
public class CommonJobBean extends CommonListBean {

    /**
     * The id.
     */
    private String id;

    /**
     * The id user.
     */
    private int idUser;

    /**
     * The desc.
     */
    private String desc;

    /**
     * The priorite.
     */
    private int priorite;

    /**
     * The dt_deb_prev.
     */
    private String dt_deb_prev;

    /**
     * The dt_fin_prev.
     */
    private String dt_fin_prev;

    /**
     * The adr_interv_rue.
     */
    private String adr_interv_rue;

    /**
     * The adr_interv_cp.
     */
    private String adr_interv_cp;

    /**
     * The adr_interv_ville.
     */
    private String adr_interv_ville;

    /**
     * The adr_interv_pays.
     */
    private String adr_interv_pays;

    /**
     * The cd_status_interv.
     */
    private int cd_status_interv;

    /**
     * The nom_client_interv.
     */
    private String nom_client_interv;

    /**
     * The nom_contact.
     */
    private String nom_contact;

    /**
     * The tel_contact.
     */
    private String tel_contact;

    /**
     * The nom_site_interv.
     */
    private String nom_site_interv;

    /**
     * The type_ interv.
     */
    private String type_Interv;

    /**
     * The id_model_rapport.
     */
    private int id_model_rapport;

//    /**
//     * The nom_model_rapport.
//     */
//    private String nom_model_rapport;

    /**
     * The lat.
     */
    private String lat;

    /**
     * The lon.
     */
    private String lon;

    /**
     * The adr_interv_globale.
     */
    private String adr_interv_globale;

    /**
     * The adr_interv_complement.
     */
    private String adr_interv_complement;

    /**
     * The no_interv.
     */
    private int no_interv;

    /**
     * The nom_client_sign.
     */
    private String nom_client_sign;

    /**
     * The nom_tech_sign.
     */
    private String nom_tech_sign;

    /**
     * The nom_facture_sign.
     */
    private String nom_facture_sign;

    /**
     * The nom_equipement.
     */
    private String nom_equipement;

    /**
     * The id site.
     */
    private int idSite;

    /**
     * The id client.
     */
    private int idClient;

    /**
     * The id equipement.
     */
    private int idEquipement;

    /**
     * The mobile contact.
     */
    private String mobileContact;

    /**
     * The dt_meeting.
     */
    private String dt_meeting;

    /**
     * The ref_customer.
     */
    private String ref_customer;

    /**
     * The Id_interv_mere.
     */
    private String Id_interv_mere;

    /**
     * The dt_deb_real.
     */
    private String dt_deb_real;

    /**
     * The dt_fin_real.
     */
    private String dt_fin_real;

    /**
     * The header date.
     */
    private String headerDate;

    /**
     * Date created
     */
    private String dtCreated;

    /**
     * Date pref.
     */
    private String datePref;

    /**
     * The Id job window.
     */
    private int idJobWindow;

    /**
     * The job pool.
     */
    private boolean isJobPool;
public CommonJobBean()
{
    super(false,"");


}

    /**
     * Instantiates a new current jobs bean.
     *
     * @param id                    the id
     * @param desc                  the desc
     * @param priorite              the priorite
     * @param dt_deb_prev           the dt_deb_prev
     * @param dt_fin_prev           the dt_fin_prev
     * @param adr_interv_rue        the adr_interv_rue
     * @param adr_interv_cp         the adr_interv_cp
     * @param adr_interv_ville      the adr_interv_ville
     * @param adr_interv_pays       the adr_interv_pays
     * @param cd_status_interv      the cd_status_interv
     * @param nom_client_interv     the nom_client_interv
     * @param nom_contact           the nom_contact
     * @param tel_contact           the tel_contact
     * @param nom_site_interv       the nom_site_interv
     * @param type_Interv           the type_ interv
     * @param id_model_rapport      the id_model_rapport
     * @param idUser                the id user
     * @param lat                   the lat
     * @param lon                   the lon
     * @param adr_interv_globale    the adr_interv_globale
     * @param adr_interv_complement the adr_interv_complement
     * @param no_interv             the no_interv
     * @param nom_client_sign       the nom_client_sign
     * @param nom_tech_sign         the nom_tech_sign
     * @param nom_facture_sign      the nom_facture_sign
     * @param nom_equipement        the nom_equipement
     * @param idClient              the id client
     * @param idSite                the id site
     * @param idEquipement          the id equipement
     * @param mobileContact         the mobile contact
     * @param dt_meeting            the dt_meeting
     * @param ref_customer          the ref_customer
     * @param Id_interv_mere        the id_interv_mere
     * @param headerDate            the header date
     */
//    public CommonJobBean(String id, String desc, int priorite,
//                         String dt_deb_prev, String dt_fin_prev, String adr_interv_rue,
//                         String adr_interv_cp, String adr_interv_ville,
//                         String adr_interv_pays, int cd_status_interv,
//                         String nom_client_interv, String nom_contact, String tel_contact,
//                         String nom_site_interv, String type_Interv, int id_model_rapport,
//                         String nom_model_rapport, int idUser, String lat, String lon,
//                         String adr_interv_globale, String adr_interv_complement,
//                         int no_interv, String nom_client_sign, String nom_tech_sign,
//                         String nom_facture_sign, String nom_equipement, int idClient,
//                         int idSite, int idEquipement, String mobileContact,
//                         String dt_meeting, String ref_customer, String Id_interv_mere,
//                         String headerDate, String dtCreated) {
//
//
//        super(true, headerDate);
//        this.id = id;
//        this.idUser = idUser;
//        this.desc = desc;
//        this.priorite = priorite;
//        this.dt_deb_prev = dt_deb_prev;
//        this.dt_fin_prev = dt_fin_prev;
//        this.adr_interv_rue = adr_interv_rue;
//        this.adr_interv_cp = adr_interv_cp;
//        this.adr_interv_ville = adr_interv_ville;
//        this.adr_interv_pays = adr_interv_pays;
//        this.cd_status_interv = cd_status_interv;
//        this.nom_client_interv = nom_client_interv;
//        this.nom_contact = nom_contact;
//        this.tel_contact = tel_contact;
//        this.nom_site_interv = nom_site_interv;
//        this.type_Interv = type_Interv;
//        this.id_model_rapport = id_model_rapport;
////        this.nom_model_rapport = nom_model_rapport;
//        this.lat = lat;
//        this.lon = lon;
//
//        this.adr_interv_globale = adr_interv_globale;
//        this.adr_interv_complement = adr_interv_complement;
//        this.no_interv = no_interv;
//
//        this.nom_client_sign = nom_client_sign;
//        this.nom_tech_sign = nom_tech_sign;
//        this.nom_facture_sign = nom_facture_sign;
//        this.nom_equipement = nom_equipement;
//        this.idSite = idSite;
//        this.idClient = idClient;
//        this.idEquipement = idEquipement;
//        this.mobileContact = mobileContact;
//        this.dt_meeting = dt_meeting;
//        this.ref_customer = ref_customer;
//        this.Id_interv_mere = Id_interv_mere;
//        this.headerDate = headerDate;
//
//        this.dtCreated = dtCreated;
//    }

    //constructor after removing the TREF_MODELE_REPORT table

    public CommonJobBean(String id, String desc, int priorite,
                         String dt_deb_prev, String dt_fin_prev,

                         String adr_interv_rue,
                         String adr_interv_cp, String adr_interv_ville,
                         String adr_interv_pays, int cd_status_interv,
                         String nom_client_interv, String nom_contact,
                         String tel_contact,
                         String nom_site_interv, String type_Interv, int id_model_rapport,
                         int idUser, String lat, String lon,
                         String adr_interv_globale, String adr_interv_complement,
                         int no_interv, String nom_client_sign, String nom_tech_sign,
                         String nom_facture_sign, String nom_equipement, int idClient,
                         int idSite, int idEquipement, String mobileContact,
                         String dt_meeting, String ref_customer, String Id_interv_mere,
                         String headerDate, String dtCreated) {


        super(true, headerDate);
        this.id = id;
        this.idUser = idUser;
        this.desc = desc;
        this.priorite = priorite;
        this.dt_deb_prev = dt_deb_prev;
        this.dt_fin_prev = dt_fin_prev;
        this.adr_interv_rue = adr_interv_rue;
        this.adr_interv_cp = adr_interv_cp;
        this.adr_interv_ville = adr_interv_ville;
        this.adr_interv_pays = adr_interv_pays;
        this.cd_status_interv = cd_status_interv;
        this.nom_client_interv = nom_client_interv;
        this.nom_contact = nom_contact;
        this.tel_contact = tel_contact;
        this.nom_site_interv = nom_site_interv;
        this.type_Interv = type_Interv;
        this.id_model_rapport = id_model_rapport;
//        this.nom_model_rapport = "";
        this.lat = lat;
        this.lon = lon;

        this.adr_interv_globale = adr_interv_globale;
        this.adr_interv_complement = adr_interv_complement;
        this.no_interv = no_interv;

        this.nom_client_sign = nom_client_sign;
        this.nom_tech_sign = nom_tech_sign;
        this.nom_facture_sign = nom_facture_sign;
        this.nom_equipement = nom_equipement;
        this.idSite = idSite;
        this.idClient = idClient;
        this.idEquipement = idEquipement;
        this.mobileContact = mobileContact;
        this.dt_meeting = dt_meeting;
        this.ref_customer = ref_customer;
        this.Id_interv_mere = Id_interv_mere;
        this.headerDate = headerDate;

        this.dtCreated = dtCreated;

        //for job Pool logic
        this.isJobPool = false;
        this.idJobWindow = 0;
        this.datePref = " ";

    }

    /**
     * Gets the date preference..
     *
     * @return the datePref
     */
    public String getDatePref() {
        return datePref;
    }

    /**
     * Sets the date preference.
     *
     * @return the datePref
     */
    public void setDatePref(String datePref) {
        this.datePref = datePref;
    }

    /**
     * Gets the job window.
     *
     * @return job window
     */
    public int getIdJobWindow() {
        return idJobWindow;
    }

    /**
     * Sets the job window.
     *
     * @param  idJobWindow
     */
    public void setIdJobWindow(int idJobWindow) {
        this.idJobWindow = idJobWindow;
    }

    /**
     * Gets the jobpool.
     *
     * @return the job pool
     */
    public boolean isJobPool() {
        return isJobPool;
    }

    /**
     * Sets the job pool.
     *
     * @param  jobPool
     */
    public void setJobPool(boolean jobPool) {
        isJobPool = jobPool;
    }

    /**
     * Gets the lat.
     *
     * @return the lat
     */
    public String getLat() {
        return lat;
    }


    /**
     * Sets the lat.
     *
     * @param lat the new lat
     */
    public void setLat(String lat) {
        this.lat = lat;
    }


    /**
     * Gets the lon.
     *
     * @return the lon
     */
    public String getLon() {
        return lon;
    }


    /**
     * Sets the lon.
     *
     * @param lon the new lon
     */
    public void setLon(String lon) {
        this.lon = lon;
    }


    /**
     * Gets the id user.
     *
     * @return the id user
     */
    public int getIdUser() {
        return idUser;
    }

    /**
     * Sets the id user.
     *
     * @param idUser the new id user
     */
    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

//    /**
//     * Gets the nom_model_rapport.
//     *
//     * @return the nom_model_rapport
//     */
//    public String getNom_model_rapport() {
//        return nom_model_rapport;
//    }
//
//    /**
//     * Sets the nom_model_rapport.
//     *
//     * @param nomModelRapport the new nom_model_rapport
//     */
//    public void setNom_model_rapport(String nomModelRapport) {
//        nom_model_rapport = nomModelRapport;
//    }

    /**
     * Gets the id_model_rapport.
     *
     * @return the id_model_rapport
     */
    public int getId_model_rapport() {
        return id_model_rapport;
    }

    /**
     * Sets the id_model_rapport.
     *
     * @param idModelRapport the new id_model_rapport
     */
    public void setId_model_rapport(int idModelRapport) {
        id_model_rapport = idModelRapport;
    }

    /**
     * Gets the type_ interv.
     *
     * @return the type_ interv
     */
    public String getType_Interv() {
        return type_Interv;
    }

    /**
     * Sets the type_ interv.
     *
     * @param typeInterv the new type_ interv
     */
    public void setType_Interv(String typeInterv) {
        type_Interv = typeInterv;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the desc.
     *
     * @return the desc
     */
    public String getDesc() {

        return desc;
    }

    /**
     * Sets the desc.
     *
     * @param desc the new desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * Gets the priorite.
     *
     * @return the priorite
     */
    public int getPriorite() {
        return priorite;
    }

    /**
     * Sets the priorite.
     *
     * @param priorite the new priorite
     */
    public void setPriorite(int priorite) {
        this.priorite = priorite;
    }

    /**
     * Gets the adr_interv_rue.
     *
     * @return the adr_interv_rue
     */
    public String getAdr_interv_rue() {
        return adr_interv_rue;
    }

    /**
     * Sets the adr_interv_rue.
     *
     * @param adrIntervRue the new adr_interv_rue
     */
    public void setAdr_interv_rue(String adrIntervRue) {
        adr_interv_rue = adrIntervRue;
    }

    /**
     * Gets the adr_interv_cp.
     *
     * @return the adr_interv_cp
     */
    public String getAdr_interv_cp() {
        return adr_interv_cp;
    }

    /**
     * Sets the adr_interv_cp.
     *
     * @param adrIntervCp the new adr_interv_cp
     */
    public void setAdr_interv_cp(String adrIntervCp) {
        adr_interv_cp = adrIntervCp;
    }

    /**
     * Gets the adr_interv_ville.
     *
     * @return the adr_interv_ville
     */
    public String getAdr_interv_ville() {
        return adr_interv_ville;
    }

    /**
     * Sets the adr_interv_ville.
     *
     * @param adrIntervVille the new adr_interv_ville
     */
    public void setAdr_interv_ville(String adrIntervVille) {
        adr_interv_ville = adrIntervVille;
    }

    /**
     * Gets the adr_interv_pays.
     *
     * @return the adr_interv_pays
     */
    public String getAdr_interv_pays() {
        return adr_interv_pays;
    }

    /**
     * Sets the adr_interv_pays.
     *
     * @param adrIntervPays the new adr_interv_pays
     */
    public void setAdr_interv_pays(String adrIntervPays) {
        adr_interv_pays = adrIntervPays;
    }

    /**
     * Gets the cd_status_interv.
     *
     * @return the cd_status_interv
     */
    public int getCd_status_interv() {
        return cd_status_interv;
    }

    /**
     * Sets the cd_status_interv.
     *
     * @param cdStatusInterv the new cd_status_interv
     */
    public void setCd_status_interv(int cdStatusInterv) {
        cd_status_interv = cdStatusInterv;
    }

    /**
     * Gets the nom_client_interv.
     *
     * @return the nom_client_interv
     */
    public String getNom_client_interv() {
        return nom_client_interv;
    }

    /**
     * Sets the nom_client_interv.
     *
     * @param nomClientInterv the new nom_client_interv
     */
    public void setNom_client_interv(String nomClientInterv) {
        nom_client_interv = nomClientInterv;
    }

    /**
     * Gets the nom_contact.
     *
     * @return the nom_contact
     */
    public String getNom_contact() {
        return nom_contact;
    }

    /**
     * Sets the nom_contact.
     *
     * @param nomContact the new nom_contact
     */
    public void setNom_contact(String nomContact) {
        nom_contact = nomContact;
    }

    /**
     * Gets the tel_contact.
     *
     * @return the tel_contact
     */
    public String getTel_contact() {
        return tel_contact;
    }

    /**
     * Sets the tel_contact.
     *
     * @param telContact the new tel_contact
     */
    public void setTel_contact(String telContact) {
        tel_contact = telContact;
    }

    /**
     * Sets the nom_site_interv.
     *
     * @param nomSiteInterv the new nom_site_interv
     */
    public void setNom_site_interv(String nomSiteInterv) {
        nom_site_interv = nomSiteInterv;
    }

    /**
     * Gets the dt_deb_prev.
     *
     * @return the dt_deb_prev
     */
    public String getDt_deb_prev() {
        return dt_deb_prev;
    }

    /**
     * Sets the dt_deb_prev.
     *
     * @param dtDebPrev the new dt_deb_prev
     */
    public void setDt_deb_prev(String dtDebPrev) {
        dt_deb_prev = dtDebPrev;
    }

    /**
     * Gets the dt_fin_prev.
     *
     * @return the dt_fin_prev
     */
    public String getDt_fin_prev() {
        return dt_fin_prev;
    }

    /**
     * Sets the dt_fin_prev.
     *
     * @param dtFinPrev the new dt_fin_prev
     */
    public void setDt_fin_prev(String dtFinPrev) {
        dt_fin_prev = dtFinPrev;
    }

    /**
     * Gets the nom_site_interv.
     *
     * @return the nom_site_interv
     */
    public String getNom_site_interv() {
        return nom_site_interv;
    }

    /**
     * Gets the adr_interv_globale.
     *
     * @return the adr_interv_globale
     */
    public String getAdr_interv_globale() {
        return adr_interv_globale;
    }

    /**
     * Sets the adr_interv_globale.
     *
     * @param adrIntervGlobale the new adr_interv_globale
     */
    public void setAdr_interv_globale(String adrIntervGlobale) {
        adr_interv_globale = adrIntervGlobale;
    }


    /**
     * Gets the adr_interv_complement.
     *
     * @return the adr_interv_complement
     */
    public String getAdr_interv_complement() {
        return adr_interv_complement;
    }

    /**
     * Sets the adr_interv_complement.
     *
     * @param adrIntervComplement the new adr_interv_complement
     */
    public void setAdr_interv_complement(String adrIntervComplement) {
        adr_interv_complement = adrIntervComplement;
    }

    /**
     * Gets the no_interv.
     *
     * @return the no_interv
     */
    public int getNo_interv() {
        return no_interv;
    }

    /**
     * Sets the no_interv.
     *
     * @param noInterv the new no_interv
     */
    public void setNo_interv(int noInterv) {
        no_interv = noInterv;
    }


    /**
     * Gets the nom_client_sign.
     *
     * @return the nom_client_sign
     */
    public String getNom_client_sign() {
        return nom_client_sign;
    }


    /**
     * Sets the nom_client_sign.
     *
     * @param nom_client_sign the new nom_client_sign
     */
    public void setNom_client_sign(String nom_client_sign) {
        this.nom_client_sign = nom_client_sign;
    }


    /**
     * Gets the nom_tech_sign.
     *
     * @return the nom_tech_sign
     */
    public String getNom_tech_sign() {
        return nom_tech_sign;
    }


    /**
     * Sets the nom_tech_sign.
     *
     * @param nom_tech_sign the new nom_tech_sign
     */
    public void setNom_tech_sign(String nom_tech_sign) {
        this.nom_tech_sign = nom_tech_sign;
    }


    /**
     * Gets the nom_facture_sign.
     *
     * @return the nom_facture_sign
     */
    public String getNom_facture_sign() {
        return nom_facture_sign;
    }


    /**
     * Sets the nom_facture_sign.
     *
     * @param nom_facture_sign the new nom_facture_sign
     */
    public void setNom_facture_sign(String nom_facture_sign) {
        this.nom_facture_sign = nom_facture_sign;
    }


    /**
     * Gets the nom_equipement.
     *
     * @return the nom_equipement
     */
    public String getNom_equipement() {
        return nom_equipement;
    }


    /**
     * Sets the nom_equipement.
     *
     * @param nom_equipement the new nom_equipement
     */
    public void setNom_equipement(String nom_equipement) {
        this.nom_equipement = nom_equipement;
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
     * Sets the id site.
     *
     * @param idSite the new id site
     */
    public void setIdSite(int idSite) {
        this.idSite = idSite;
    }


    /**
     * Gets the id client.
     *
     * @return the id client
     */
    public int getIdClient() {
        return idClient;
    }


    /**
     * Sets the id client.
     *
     * @param idClient the new id client
     */
    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }


    /**
     * Gets the id equipement.
     *
     * @return the id equipement
     */
    public int getIdEquipement() {
        return idEquipement;
    }


    /**
     * Sets the id equipement.
     *
     * @param idEquipement the new id equipement
     */
    public void setIdEquipement(int idEquipement) {
        this.idEquipement = idEquipement;
    }


    /**
     * Gets the mobile contact.
     *
     * @return the mobile contact
     */
    public String getMobileContact() {
        return mobileContact;
    }


    /**
     * Sets the mobile contact.
     *
     * @param mobileContact the new mobile contact
     */
    public void setMobileContact(String mobileContact) {
        this.mobileContact = mobileContact;
    }


    /**
     * Gets the dt_meeting.
     *
     * @return the dt_meeting
     */
    public String getDt_meeting() {
        return dt_meeting;
    }


    /**
     * Sets the dt_meeting.
     *
     * @param dt_meeting the new dt_meeting
     */
    public void setDt_meeting(String dt_meeting) {
        this.dt_meeting = dt_meeting;
    }


    /**
     * Gets the ref_customer.
     *
     * @return the ref_customer
     */
    public String getRef_customer() {
        return ref_customer;
    }


    /**
     * Sets the ref_customer.
     *
     * @param ref_customer the new ref_customer
     */
    public void setRef_customer(String ref_customer) {
        this.ref_customer = ref_customer;
    }

    /**
     * Gets the id_interv_mere.
     *
     * @return the id_interv_mere
     */
    public String getId_interv_mere() {
        return Id_interv_mere;
    }


    /**
     * Sets the id_interv_mere.
     *
     * @param id_interv_mere the new id_interv_mere
     */
    public void setId_interv_mere(String id_interv_mere) {
        Id_interv_mere = id_interv_mere;
    }


    /**
     * Gets the dt_deb_real.
     *
     * @return the dt_deb_real
     */
    public String getDt_deb_real() {
        return dt_deb_real;
    }


    /**
     * Sets the dt_deb_real.
     *
     * @param dt_deb_real the new dt_deb_real
     */
    public void setDt_deb_real(String dt_deb_real) {
        this.dt_deb_real = dt_deb_real;
    }


    /**
     * Gets the dt_fin_real.
     *
     * @return the dt_fin_real
     */
    public String getDt_fin_real() {
        return dt_fin_real;
    }


    /**
     * Sets the dt_fin_real.
     *
     * @param dt_fin_real the new dt_fin_real
     */
    public void setDt_fin_real(String dt_fin_real) {
        this.dt_fin_real = dt_fin_real;
    }

    /**
     * Gets the header date.
     *
     * @return the header date
     */
    public String getHeaderDate() {
        return headerDate;
    }

    public void setHeaderDate(String headerDate) {
        this.headerDate = headerDate;
    }

    public void setDtCreated(String dtCreated) {
        this.dtCreated = dtCreated;
    }

    /**
     * Gets date created
     *
     * @return dtCreated
     */
    public String getDtCreated() {
        return dtCreated;
    }
}
