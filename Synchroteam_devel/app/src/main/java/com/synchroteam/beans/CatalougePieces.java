package com.synchroteam.beans;

// TODO: Auto-generated Javadoc
/**
 * This class is used to store and retrive parts data.
 *
 * @author Ideavate.Solution 
 */
public class CatalougePieces implements CategoryAndPartsInterface {

	/** The id. */
	private String id;
	
	/** The nom_piece. */
	private String nom_piece;
	
	/** The prix. */
	private String prix;
	
	/** The number_of_pices. */
	private String number_of_pices;

	

	/**
	 * Instantiates a new catalouge pieces.
	 *
	 * @param id the id
	 * @param nom_piece the nom_piece
	 * @param prix the prix
	 * @param number_of_pices the number_of_pices
	 */
	public CatalougePieces(String id, String nom_piece, String prix,
			String number_of_pices) {
		// TODO Auto-generated constructor stub
		this.id=id;
		this.nom_piece=nom_piece;
		this.prix=prix;
		this.number_of_pices=number_of_pices;
				
				
	}

	/* (non-Javadoc)
	 * @see com.synchroteam.beans.CategoryAndPartsInterface#isCategory()
	 */
	@Override
	public boolean isCategory() {
		// TODO Auto-generated method stub
		return false;
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
	 * Gets the nom_piece.
	 *
	 * @return the nom_piece
	 */
	public String getNom_piece() {
		return nom_piece;
	}

	/**
	 * Gets the prix.
	 *
	 * @return the prix
	 */
	public String getPrix() {
		return prix;
	}

	/**
	 * Gets the number_of_pices.
	 *
	 * @return the number_of_pices
	 */
	public String getNumber_of_pices() {
		return number_of_pices;
	}
	
	public void setNumber_of_pices(String number_of_pices) {
		this.number_of_pices = number_of_pices;
	}
	
}
