package com.synchroteam.beans;

// TODO: Auto-generated Javadoc
/**
 * As per our understanding it was developed to hold data related to message screen
 * The Class Message_oper.
 * @author ${Previous Developer}
 */
public class Message_oper {
	
	/** The id. */
	private int id;
	
	/** The titre. */
	private String titre;
	
	/** The corp. */
	private String corp;
	
	/** The priorite. */
	private int priorite;
	
	/** The etat. */
	private int etat;
	
	/**
	 * Instantiates a new message_oper.
	 *
	 * @param id the id
	 * @param titre the titre
	 * @param corp the corp
	 * @param priorite the priorite
	 * @param etat the etat
	 * @return the discription
	 */
	
	public Message_oper(int id, String titre, String corp, int priorite, int etat) {
		super();
		this.id=id;
		this.titre = titre;
		this.corp = corp;
		this.priorite = priorite;
		this.etat= etat;
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
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Gets the titre.
	 *
	 * @return the titre
	 */
	public String getTitre() {
		return titre;
	}
	
	/**
	 * Sets the titre.
	 *
	 * @param titre the new titre
	 */
	public void setTitre(String titre) {
		this.titre = titre;
	}
	
	/**
	 * Gets the corp.
	 *
	 * @return the corp
	 */
	public String getCorp() {
		return corp;
	}
	
	/**
	 * Sets the corp.
	 *
	 * @param corp the new corp
	 */
	public void setCorp(String corp) {
		this.corp = corp;
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
	 * Gets the etat.
	 *
	 * @return the etat
	 */
	public int getEtat() {
		return etat;
	}
	
	/**
	 * Sets the etat.
	 *
	 * @param etat the new etat
	 */
	public void setEtat(int etat) {
		this.etat = etat;
	}

	
}
