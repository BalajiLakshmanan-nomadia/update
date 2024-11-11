package com.synchroteam.beans;

/**
 * This class holds the id & value of tax rate for invoicing
 * 
 * @author Trident
 *
 */
public class TaxRates {

	private String idTaxRate;
	private String valTaxRate;
	private String valTaxName;

	public String getIdTaxRate() {
		return idTaxRate;
	}

	public void setIdTaxRate(String idTaxRate) {
		this.idTaxRate = idTaxRate;
	}

	public String getValTaxRate() {
		return valTaxRate;
	}

	public void setValTaxRate(String valTaxRate) {
		this.valTaxRate = valTaxRate;
	}

	public String getValTaxName() {
		return valTaxName;
	}

	public void setValTaxName(String valTaxName) {
		this.valTaxName = valTaxName;
	}
}
