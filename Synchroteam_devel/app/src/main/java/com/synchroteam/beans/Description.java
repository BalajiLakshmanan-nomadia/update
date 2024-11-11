package com.synchroteam.beans;

public class Description {
    private String nomClient;
    private String adresseRue;
    private String adresseCp;
    private String adresseVille;
    private String adressePays;
    private String nomContact;
    private String telContact;
    private String dateDebutIntervention;
    private String dateFinIntervention;
    private String typeIntervention;
//    private String modeleIntervention;
    private String descriptionIntervention;
    private String adresseGlobale;
    private String adresseComplement;
    private int noInterv;
    private String contactMobile;
    private String RefCustomer;
    private String publicLinkInterv;
    private String publicLinkClient;
    private String preNomContact;
    private String emailContact;
    private int idClient;

    public String getEmailContact() {
        return emailContact;
    }


    public void setEmailContact(String emailContact) {
        this.emailContact = emailContact;
    }


    public String getPreNomContact() {
        return preNomContact;
    }


    public void setPreNomContact(String preNomContact) {
        this.preNomContact = preNomContact;
    }


    public Description(String nomClient, String adresseRue, String adresseCp,
                       String adresseVille, String adressePays, String nomContact,
                       String telContact, String dateDebutIntervention,
                       String dateFinIntervention, String typeIntervention,
                       String descriptionIntervention, String adresseGlobale,
                       String adresseComplement, int noInterv, String contactMobile,
                       String RefCustomer, String publicLinkInterv, String preNomContact, String emailContact, String publicLinkClient) {
        super();
        this.nomClient = nomClient;
        this.adresseRue = adresseRue;
        this.adresseCp = adresseCp;
        this.adresseVille = adresseVille;
        this.adressePays = adressePays;
        this.nomContact = nomContact;
        this.telContact = telContact;
        this.dateDebutIntervention = dateDebutIntervention;
        this.typeIntervention = typeIntervention;
        this.dateFinIntervention = dateFinIntervention;
//        this.modeleIntervention = modeleIntervention;
        this.descriptionIntervention = descriptionIntervention;
        this.adresseGlobale = adresseGlobale;
        this.adresseComplement = adresseComplement;
        this.noInterv = noInterv;
        this.contactMobile = contactMobile;
        this.RefCustomer = RefCustomer;
        this.publicLinkInterv = publicLinkInterv;
        this.preNomContact = preNomContact;
        this.emailContact = emailContact;
        this.publicLinkClient = publicLinkClient;

    }
    public Description(String nomClient, String adresseRue, String adresseCp,
                       String adresseVille, String adressePays, String nomContact,
                       String telContact, String dateDebutIntervention,
                       String dateFinIntervention, String typeIntervention,
                       String descriptionIntervention, String adresseGlobale,
                       String adresseComplement, int noInterv, String contactMobile,
                       String RefCustomer, String publicLinkInterv, String preNomContact, String emailContact, String publicLinkClient,
                       int idClient) {
        super();
        this.nomClient = nomClient;
        this.adresseRue = adresseRue;
        this.adresseCp = adresseCp;
        this.adresseVille = adresseVille;
        this.adressePays = adressePays;
        this.nomContact = nomContact;
        this.telContact = telContact;
        this.dateDebutIntervention = dateDebutIntervention;
        this.typeIntervention = typeIntervention;
        this.dateFinIntervention = dateFinIntervention;
//        this.modeleIntervention = modeleIntervention;
        this.descriptionIntervention = descriptionIntervention;
        this.adresseGlobale = adresseGlobale;
        this.adresseComplement = adresseComplement;
        this.noInterv = noInterv;
        this.contactMobile = contactMobile;
        this.RefCustomer = RefCustomer;
        this.publicLinkInterv = publicLinkInterv;
        this.preNomContact = preNomContact;
        this.emailContact = emailContact;
        this.publicLinkClient = publicLinkClient;
        this.idClient = idClient;

    }


    public Description(String publicLinkInterv, String publicLinkClient) {
        this.publicLinkInterv = publicLinkInterv;
        this.publicLinkClient = publicLinkClient;

    }
    public int getIdClient(){
        return idClient;
    }

    public String getNomClient() {
        return nomClient;
    }

    public String getAdresseRue() {
        return adresseRue;
    }

    public String getAdresseCp() {
        return adresseCp;
    }

    public String getAdresseVille() {
        return adresseVille;
    }

    public String getAdressePays() {
        return adressePays;
    }

    public String getNomContact() {
        return nomContact;
    }

    public String getTelContact() {
        return telContact;
    }

    public String getDateDebutIntervention() {
        return dateDebutIntervention;
    }

    public String getTypeIntervention() {
        return typeIntervention;
    }

    public String getDateFinIntervention() {
        return dateFinIntervention;
    }

//    public String getModeleIntervention() {
//        return modeleIntervention;
//    }

    public String getDescriptionIntervention() {
        return descriptionIntervention;
    }

    public String getAdresseGlobale() {
        return adresseGlobale;
    }

    public String getAdresseComplement() {
        return adresseComplement;
    }

    public int getNoInterv() {
        return noInterv;
    }

    public String getContactMobile() {
        return contactMobile;
    }

    public String getRefCustomer() {
        return RefCustomer;
    }

    public String getPublicLinkInterv() {
        return publicLinkInterv;
    }

    public String getPublicLinkClient() {
        return publicLinkClient;
    }

    public void setPublicLinkIntervention(String publicLinkInterv) {
        this.publicLinkInterv = publicLinkInterv;
    }


    public void setPublicLinkClient(String publicLinkClient) {
        this.publicLinkClient = publicLinkClient;
    }
}
