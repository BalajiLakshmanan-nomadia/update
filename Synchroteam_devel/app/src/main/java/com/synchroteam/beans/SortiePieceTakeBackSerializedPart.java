package com.synchroteam.beans;

// TODO: Auto-generated Javadoc

/**
 * The Class SortiePiece.
 *
 * @author ${Previous developer}
 */
public class SortiePieceTakeBackSerializedPart {

    /**
     * The id.
     */
    private int id_tref_piece;

    /**
     * The nom.
     */
    private String name;

    /**
     * The prix.
     */
    private double price;


    /**
     * The name category cat.
     */
    private String nameCategory;


    /**
     * The fl facturable.
     */
    private int flFacturable;

    /**
     * The fl serializable.
     */
    private int flSerializable;

    private String serial;

    /**
     * The qte.
     */
    private double quantity_sortie;

    private int quantity_reprise;

    private int id_piece_sortie;


    /**
     * Instantiates a new sortie piece.
     *
     * @param id_piece_sortie  the id
     * @param serial           take back serial no
     * @param quantity_sortie  the quantity_sortie
     * @param quantity_reprise the quantity_reprise
     * @param flFacturable     the fl facturable
     */
    public SortiePieceTakeBackSerializedPart(int id_piece_sortie, String serial,
                                             double quantity_sortie, int quantity_reprise, int flFacturable) {
        super();
        this.id_piece_sortie = id_piece_sortie;
        this.serial = serial;
        this.quantity_sortie = quantity_sortie;
        this.quantity_reprise = quantity_reprise;
        this.flFacturable = flFacturable;

    }

    public String getNameCategory() {
        return nameCategory;
    }

    public int getId_tref_piece() {
        return id_tref_piece;
    }

    public void setId_tref_piece(int id_tref_piece) {
        this.id_tref_piece = id_tref_piece;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getFlSerializable() {
        return flSerializable;
    }

    public void setFlSerializable(int flSerializable) {
        this.flSerializable = flSerializable;
    }

    public int getFlFacturable() {
        return flFacturable;
    }

    public String getSerial() {
        return serial;
    }

    public double getQuantity_sortie() {
        return quantity_sortie;
    }

    public int getQuantity_reprise() {
        return quantity_reprise;
    }

    public int getId_piece_sortie() {
        return id_piece_sortie;
    }
}
