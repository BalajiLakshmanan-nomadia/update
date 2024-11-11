package com.synchroteam.beans;

/**
 * <p>
 * Beans class to hold the values for Pending request list in Inventory Details
 * page.
 * </p>
 * 
 * @author Trident.
 *
 */
public class PendingRequestBeans {
	private String idStock;
	private String idStockDest;
	private int qty;
	private String idPieceDemande;
	private int flUrgent;

	public int getFlUrgent() {
		return flUrgent;
	}

	public void setFlUrgent(int flUrgent) {
		this.flUrgent = flUrgent;
	}

	public String getIdStockDest() {
		return idStockDest;
	}

	public void setIdStockDest(String idStockDest) {
		this.idStockDest = idStockDest;
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

	public int getQty() {
		return qty;
	}

	public void setQty(int qty) {
		this.qty = qty;
	}

}