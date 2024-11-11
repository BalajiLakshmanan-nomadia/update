package com.synchroteam.beans;

public class InvoicingCatalogSubCategoryBeans {

	/** id of sub category */
	private String id;

	/** name of item */
	private String itemName;

	/** name of sub category */
	private String nameSubcategory;

	/** price of sub category */
	private String priceSubcategory;

	/** Id of the corresponding tax rate */
	private String idTaxRate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getNameSubcategory() {
		return nameSubcategory;
	}

	public void setNameSubcategory(String nameSubcategory) {
		this.nameSubcategory = nameSubcategory;
	}

	public String getPriceSubcategory() {
		return priceSubcategory;
	}

	public void setPriceSubcategory(String priceSubcategory) {
		this.priceSubcategory = priceSubcategory;
	}

	public String getIdTaxRate() {
		return idTaxRate;
	}

	public void setIdTaxRate(String idTaxRate) {
		this.idTaxRate = idTaxRate;
	}

}
