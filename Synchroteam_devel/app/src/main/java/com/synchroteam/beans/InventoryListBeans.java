package com.synchroteam.beans;

public class InventoryListBeans {

	/** id of piece */
	private String id;

	/** category id of the piece */
	private String idCategory;

	/** parts & services name */
	private String namePartsService;

	/** reference name of piece */
	private String namereference;

	/** price of piece */
	private String priceValue;

	/** flag for serializable */
	private String flagSerializable;

	/** no of pieces */
	private String noOfPiece;

	/** id tax rate */
	private String idTaxRate;

	/** category name */
	private String nameCategory;

	public String getNameCategory() {
		return nameCategory;
	}

	public void setNameCategory(String nameCategory) {
		this.nameCategory = nameCategory;
	}

	public String getIdTaxRate() {
		return idTaxRate;
	}

	public void setIdTaxRate(String idTaxRate) {
		this.idTaxRate = idTaxRate;
	}

	public String getNoOfPiece() {
		return noOfPiece;
	}

	public void setNoOfPiece(String noOfPiece) {
		this.noOfPiece = noOfPiece;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNamePartsService() {
		return namePartsService;
	}

	public void setNamePartsService(String namePartsService) {
		this.namePartsService = namePartsService;
	}

	public String getNamereference() {
		return namereference;
	}

	public void setNamereference(String namereference) {
		this.namereference = namereference;
	}

	public String getPriceValue() {
		return priceValue;
	}

	public void setPriceValue(String priceValue) {
		this.priceValue = priceValue;
	}

	public String getIdCategory() {
		return idCategory;
	}

	public void setIdCategory(String idCategory) {
		this.idCategory = idCategory;
	}

	public String getFlagSerializable() {
		return flagSerializable;
	}

	public void setFlagSerializable(String flagSerializable) {
		this.flagSerializable = flagSerializable;
	}

}
