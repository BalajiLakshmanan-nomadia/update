package com.synchroteam.beans;

// TODO: Auto-generated Javadoc

import com.synchroteam.utils.StringUtils;

/**
 * Photo_Pda is used to store data related to attachments.
 * The Class Photo_Pda.
 * @author ${Previous developer}
 */
public class Photo_Pda {

	/** The id photo. */
	private String idPhoto;

	/** The id intervention. */
	private String idIntervention;

	/** The commentaire. */
	private String commentaire;

	/** The photo. */
	private byte[] photo;

	/** The position. */
	private int position;

	/** The extention. */
	private String extention;

    /** File path */
    private String filePath;



	/**
	 * Instantiates a new photo_ pda.
	 *
	 * @param idPhoto the id photo
	 * @param idIntervention the id intervention
	 * @param commentaire the commentaire
	 * @param photo the photo
	 */
	public Photo_Pda(String idPhoto, String idIntervention, String commentaire, byte[] photo) {
		super();
		this.idPhoto = idPhoto;
		this.idIntervention = idIntervention;
		this.commentaire = commentaire;
		this.photo = photo;
	}


	/**
	 * Instantiates a new photo_ pda.
	 *
	 * @param idPhoto the id photo
	 * @param commentaire the commentaire
	 * @param photo the photo
	 * @param extention the extention
	 * @param filePath the file path
	 */
	public Photo_Pda(String idPhoto,String commentaire, byte[] photo,String extention) {
		super();
		this.idPhoto = idPhoto;
		this.extention=extention;
		this.commentaire = commentaire;
		this.photo = photo;

	}

    /**
	 * Instantiates a new photo_ pda.
	 *
	 * @param idPhoto the id photo
	 * @param commentaire the commentaire
	 * @param photo the photo
	 * @param extention the extention
	 * @param filePath the file path
	 */
	public Photo_Pda(String idPhoto,String commentaire, String filePath,byte[] photo, String extention) {
		super();
		this.idPhoto = idPhoto;
		this.extention=extention;
		this.commentaire = commentaire;
		this.filePath = filePath;
		this.photo = photo;
	}

	/**
	 * Gets the extention.
	 *
	 * @return the extention
	 */
	public String getExtention(){
		return extention;
	}

	/**
	 * Gets the id intervention.
	 *
	 * @return the id intervention
	 */
	public String getIdIntervention() {
		return idIntervention;
	}

	/**
	 * Gets the id photo.
	 *
	 * @return the id photo
	 */
	public String getIdPhoto() {
		return idPhoto;
	}

	/**
	 * Gets the commentaire.
	 *
	 * @return the commentaire
	 */
	public String getCommentaire() {
		return commentaire;
	}

	/**
	 * Gets the photo.
	 *
	 * @return the photo
	 */
	public byte[] getPhoto() {
		return photo;
	}



	/**
	 * Sets the string.
	 *
	 * @param newComment the new string
	 */
	public void setString(String newComment){
		commentaire=newComment;
	}


	/**
	 * Sets the position.
	 *
	 * @param position the new position
	 */
	public void setPosition(int position){
		this.position=position;
	}

	/**
	 * Gets the position.
	 *
	 * @return the position
	 */
	public int getPosition(){
		return position;
	}

    /**
     * Gets file path
     * @return
     */
    public String getFilePath() {
        return filePath;
    }

}
