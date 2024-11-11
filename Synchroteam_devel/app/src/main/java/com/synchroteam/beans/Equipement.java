package com.synchroteam.beans;

// TODO: Auto-generated Javadoc
/**
 * The Class Equipement to hold data related to equipments needed in NewJob
 * Screen.
 * 
 * @author Ideavate Solution
 */
public class Equipement {

	/** The id equipement. */
	private int idEquipement;

	/** The nm equipement. */
	private String nmEquipement;

	private int mEquipementSiteId;

	private String refCustomer;

	public String getRefCustomer() {
		return refCustomer;
	}

	public int getmEquipementSiteId() {
		return mEquipementSiteId;
	}

	public void setmEquipementSiteId(int mEquipementSiteId) {
		this.mEquipementSiteId = mEquipementSiteId;
	}

	/**
	 * Instantiates a new equipement.
	 * 
	 * @param idEquipement
	 *            the id equipement
	 * @param nmEquipement
	 *            the nm equipement
	 */
	public Equipement(int idEquipement, String nmEquipement,
			int mEquipementSiteId) {
		super();
		this.idEquipement = idEquipement;
		this.nmEquipement = nmEquipement;
		this.mEquipementSiteId = mEquipementSiteId;
	}

	public Equipement(int idEquipement, String nmEquipement,
					  int mEquipementSiteId,String refCustomer) {
		super();
		this.idEquipement = idEquipement;
		this.nmEquipement = nmEquipement;
		this.mEquipementSiteId = mEquipementSiteId;
		this.refCustomer = refCustomer;

	}

	public Equipement(int idEquipement, String nmEquipement) {
		super();
		this.idEquipement = idEquipement;
		this.nmEquipement = nmEquipement;
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
	 * Gets the nm equipement.
	 * 
	 * @return the nm equipement
	 */
	public String getNmEquipement() {
		return nmEquipement;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return nmEquipement;
	}
}
