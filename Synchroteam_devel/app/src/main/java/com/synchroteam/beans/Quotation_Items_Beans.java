package com.synchroteam.beans;

public class Quotation_Items_Beans {

    private String REF_LIGNE;
    private String DESCR_LIGNE;
    private double PRIX_UNITAIRE;
    private double QUANTITE;
    private double VAL_TAXRATE;
    private int ORDRE;
    private double DISCOUNT;
    private double TOTAL_HT;
    private double VAL_TVA;
    private double TOTAL_TTC;
    private String ID_REMOTE_FACTURE;
    private String DESCRIPTION;
    private boolean DISCOUNTPERCENT;
    private String ID_REMOTE;

    public Quotation_Items_Beans(String REF_LIGNE, String DESCR_LIGNE, double PRIX_UNITAIRE,
                                 double QUANTITE, double VAL_TAXRATE, int ORDRE,
                                 double DISCOUNT, double TOTAL_HT, double VAL_TVA,
                                 double TOTAL_TTC, String ID_REMOTE_FACTURE,
                                 String DESCRIPTION, boolean DISCOUNTPERCENT,
                                 String ID_REMOTE) {
        this.REF_LIGNE = REF_LIGNE;
        this.DESCR_LIGNE = DESCR_LIGNE;
        this.PRIX_UNITAIRE = PRIX_UNITAIRE;
        this.QUANTITE = QUANTITE;
        this.VAL_TAXRATE = VAL_TAXRATE;
        this.ORDRE = ORDRE;
        this.DISCOUNT = DISCOUNT;
        this.TOTAL_HT = TOTAL_HT;
        this.VAL_TVA = VAL_TVA;
        this.TOTAL_TTC = TOTAL_TTC;
        this.ID_REMOTE_FACTURE = ID_REMOTE_FACTURE;
        this.DESCRIPTION = DESCRIPTION;
        this.DISCOUNTPERCENT = DISCOUNTPERCENT;
        this.ID_REMOTE = ID_REMOTE;
    }

    public String getREF_LIGNE() {
        return REF_LIGNE;
    }

    public String getDESCR_LIGNE() {
        return DESCR_LIGNE;
    }

    public double getPRIX_UNITAIRE() {
        return PRIX_UNITAIRE;
    }

    public double getQUANTITE() {
        return QUANTITE;
    }

    public double getVAL_TAXRATE() {
        return VAL_TAXRATE;
    }

    public int getORDRE() {
        return ORDRE;
    }

    public double getDISCOUNT() {
        return DISCOUNT;
    }

    public double getTOTAL_HT() {
        return TOTAL_HT;
    }

    public double getVAL_TVA() {
        return VAL_TVA;
    }

    public double getTOTAL_TTC() {
        return TOTAL_TTC;
    }

    public String getID_REMOTE_FACTURE() {
        return ID_REMOTE_FACTURE;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public boolean isDISCOUNTPERCENT() {
        return DISCOUNTPERCENT;
    }

    public String getID_REMOTE() {
        return ID_REMOTE;
    }


}
