package com.synchroteam.beans;

// TODO: Auto-generated Javadoc

/**
 * Previous developer class as per our understanding its store data regarding leaves of particular technician.
 * The Class Conge .
 *
 * @author Ideavate.Solution
 */
public class Conge extends CommonListBean {

    /**
     * The id conge.
     */
    private String idConge;

    /**
     * The id type conge.
     */
    private int idTypeConge;

    /**
     * The dt debut.
     */
    private String dtDebut;

    /**
     * The dt fin.
     */
    private String dtFin;

    /**
     * The note.
     */
    private String note;

    /**
     * The nom type conge.
     */
    private String nomTypeConge;

    /**
     * The couleur conge.
     */
    private String couleurConge;

    /**
     * The is header.
     */
    private boolean isHeader;

    /**
     * The header date.
     */
    private String headerDate;

    /**
     * User id.
     */
    private String idUser;

    /**
     * Group id.
     */
    private String idGroupe;

    private int flUnavailable;

    private int flPayable;


    /**
     * Instantiates a new conge.
     *
     * @param idConge      the id conge
     * @param idTypeConge  the id type conge
     * @param dtDebut      the dt debut
     * @param dtFin        the dt fin
     * @param note         the note
     * @param nomTypeConge the nom type conge
     * @param couleurConge the couleur conge
     * @param header       the header
     */
    public Conge(String idConge, int idTypeConge, String dtDebut,
                 String dtFin, String note, String nomTypeConge,
                 String couleurConge, String header, String idUser, String idGroupe,
                 int flUnavailable, int flPayable) {
        super(false, header);
        this.idConge = idConge;
        this.idTypeConge = idTypeConge;
        this.dtDebut = dtDebut;
        this.dtFin = dtFin;
        this.note = note;
        this.nomTypeConge = nomTypeConge;
        this.couleurConge = couleurConge;
        this.headerDate = header;
        this.idUser = idUser;
        this.idGroupe = idGroupe;
        this.flUnavailable = flUnavailable;
        this.flPayable = flPayable;
    }

//	/**
//	 * Instantiates a new conge.
//	 *
//	 * @param isHeader the is header
//	 * @param dtDebut the dt debut
//	 */
//	public Conge(boolean isHeader,String dtDebut){
//		super(false);
//	this.dtDebut=dtDebut;
//	this.setHeader(isHeader);
//}


    /**
     * Gets the header.
     *
     * @return the header
     */
    public String getHeader() {

        return headerDate;
    }

    /**
     * Gets the id conge.
     *
     * @return the id conge
     */
    public String getIdConge() {
        return idConge;
    }

    /**
     * Sets the id conge.
     *
     * @param idConge the new id conge
     */
    public void setIdConge(String idConge) {
        this.idConge = idConge;
    }

    /**
     * Gets the id type conge.
     *
     * @return the id type conge
     */
    public int getIdTypeConge() {
        return idTypeConge;
    }

    /**
     * Sets the id type conge.
     *
     * @param idTypeConge the new id type conge
     */
    public void setIdTypeConge(int idTypeConge) {
        this.idTypeConge = idTypeConge;
    }

    /**
     * Gets the dt debut.
     *
     * @return the dt debut
     */
    public String getDtDebut() {
        return dtDebut;
    }

    /**
     * Sets the dt debut.
     *
     * @param dtDebut the new dt debut
     */
    public void setDtDebut(String dtDebut) {
        this.dtDebut = dtDebut;
    }

    /**
     * Gets the dt fin.
     *
     * @return the dt fin
     */
    public String getDtFin() {
        return dtFin;
    }

    /**
     * Sets the dt fin.
     *
     * @param dtFin the new dt fin
     */
    public void setDtFin(String dtFin) {
        this.dtFin = dtFin;
    }

    /**
     * Gets the note.
     *
     * @return the note
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets the note.
     *
     * @param note the new note
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Gets the nom type conge.
     *
     * @return the nom type conge
     */
    public String getNomTypeConge() {
        return nomTypeConge;
    }

    /**
     * Sets the nom type conge.
     *
     * @param nomTypeConge the new nom type conge
     */
    public void setNomTypeConge(String nomTypeConge) {
        this.nomTypeConge = nomTypeConge;
    }

    /**
     * Gets the couleur conge.
     *
     * @return the couleur conge
     */
    public String getCouleurConge() {
        return couleurConge;
    }

    /**
     * Sets the couleur conge.
     *
     * @param couleurConge the new couleur conge
     */
    public void setCouleurConge(String couleurConge) {
        this.couleurConge = couleurConge;
    }

    /**
     * Checks if is header.
     *
     * @return true, if is header
     */
    public boolean isHeader() {
        return isHeader;
    }

    /**
     * Sets the header.
     *
     * @param isHeader the new header
     */
    public void setHeader(boolean isHeader) {
        this.isHeader = isHeader;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdGroupe() {
        return idGroupe;
    }

    public void setIdGroupe(String idGroupe) {
        this.idGroupe = idGroupe;
    }

    public int getFlUnavailable() {
        return flUnavailable;
    }

    public int getFlPayable() {
        return flPayable;
    }
}
