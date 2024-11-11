package com.synchroteam.beans;

import java.io.Serializable;

public class InventoryDialogSerialNumber implements Serializable {

	private int id;
	private String idPieceSerial;
	private String name;
	private String dateUsed;
	private boolean isSelected;
	private String stockName;

	public int getIdPiece() {
		return idPiece;
	}

	public void setIdPiece(int idPiece) {
		this.idPiece = idPiece;
	}

	private int idPiece;

	public String getStockId() {
		return stockId;
	}

	public void setStockId(String stockId) {
		this.stockId = stockId;
	}

	private String stockId;
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return serial number of piece
	 */
	public String getIdPieceSerial() {
		return idPieceSerial;
	}
	/**
	 * @param idPieceSerial
	 *            the serial number of piece to be set
	 */
	public void setIdPieceSerial(String idPieceSerial) {
		this.idPieceSerial = idPieceSerial;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the isSelected
	 */
	public boolean isSelected() {
		return isSelected;
	}
	/**
	 * @param isSelected
	 *            the isSelected to set
	 */
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}
	/**
	 * @return date used
	 */
	public String getDateUsed() {
		return dateUsed;
	}
	/**
	 * Sets date used.
	 *
	 * @param dateUsed
	 */
	public void setDateUsed(String dateUsed) {
		this.dateUsed = dateUsed;
	}
	public String getStockName() {
		return stockName;
	}
	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

}