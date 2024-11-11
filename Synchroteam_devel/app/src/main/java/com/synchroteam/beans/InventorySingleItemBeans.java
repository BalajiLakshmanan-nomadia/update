package com.synchroteam.beans;

public class InventorySingleItemBeans {

	private String idStock;
	private String idStockDestination;
	private String idPiece;
	private String user;
	private String qty;
	private String depot;
	private String action;
	private int isSerializable;
	private String idPieceDemande;
	private String flUrgent;

	public String getFlUrgent() {
		return flUrgent;
	}

	public void setFlUrgent(String flUrgent) {
		this.flUrgent = flUrgent;
	}

	public String getIdStockDestination() {
		return idStockDestination;
	}

	public void setIdStockDestination(String idStockDestination) {
		this.idStockDestination = idStockDestination;
	}

	public String getIdPieceDemande() {
		return idPieceDemande;
	}

	public void setIdPieceDemande(String idPieceDemande) {
		this.idPieceDemande = idPieceDemande;
	}

	public String getIdStock() {
		return idStock;
	}

	public void setIdStock(String idStock) {
		this.idStock = idStock;
	}

	public String getIdPiece() {
		return idPiece;
	}

	public void setIdPiece(String idPiece) {
		this.idPiece = idPiece;
	}

	public int getIsSerializable() {
		return isSerializable;
	}

	public void setIsSerializable(int isSerializable) {
		this.isSerializable = isSerializable;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getQty() {
		return qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}

	public String getDepot() {
		return depot;
	}

	public void setDepot(String depot) {
		this.depot = depot;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

}
