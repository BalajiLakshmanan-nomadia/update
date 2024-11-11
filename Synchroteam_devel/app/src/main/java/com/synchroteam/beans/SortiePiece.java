package com.synchroteam.beans;

// TODO: Auto-generated Javadoc

/**
 * The Class SortiePiece.
 *
 * @author ${Previous developer}
 */
public class SortiePiece {

    /**
     * The id.
     */
    private int id;

    /**
     * The nom.
     */
    private String nom;

    /**
     * The prix.
     */
    private double prix;

    /**
     * The nom cat.
     */
    private String nomCat;

    /**
     * The qte.
     */
    private double qte;

    /**
     * The fl facturable.
     */
    private int flFacturable;

    /**
     * The fl serializable.
     */
    private int flSerializable;

    /**
     * The selected serial for take back.
     */
    private String serialReprise;

    /**
     * The selected serial for part added.
     */
    private String serialSortie;

    private int qty_reprise;

    /**
     * The fl trackable.
     */
    private int flTrackable;

    /**
     * Instantiates a new sortie piece.
     *
     * @param id           the id
     * @param nom          the nom
     * @param prix         the prix
     * @param qte          the qte
     * @param nomCat       the nom cat
     * @param flFacturable the fl facturable
     */
    public SortiePiece(int id, String nom, double prix, int flSerializable,
                       double qte, String nomCat, int flFacturable,
                       String serialReprise, int qty_reprise,String serialSortie,int flTrackable) {
        super();
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.flSerializable = flSerializable;
        this.qte = qte;
        this.nomCat = nomCat;
        this.flFacturable = flFacturable;
        this.serialReprise = serialReprise;
        this.qty_reprise = qty_reprise;
        this.serialSortie = serialSortie;
        this.flTrackable=flTrackable;
    }

    public int getFlTrackable() {
        return flTrackable;
    }

    public String getSerialReprise() {
        return serialReprise;
    }

    public String getSerialSortie() {
        return serialSortie;
    }


    public int getQty_reprise() {
        return qty_reprise;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the nom.
     *
     * @return the nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * Gets the prix.
     *
     * @return the prix
     */
    public double getPrix() {
        return prix;
    }

    /**
     * Gets the qte.
     *
     * @return the qte
     */
    public double getQte() {
        return qte;
    }

    /**
     * Gets the nom cat.
     *
     * @return the nom cat
     */
    public String getNomCat() {
        return nomCat;
    }

    /**
     * Gets the fl facturable.
     *
     * @return the fl facturable
     */
    public int getFlFacturable() {
        return flFacturable;
    }

    /**
     * Gets the fl serializable.
     *
     * @return the fl serializable
     */
    public int getFlSerializable() {
        return flSerializable;
    }

}
