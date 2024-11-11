package com.synchroteam.beans;

public class Invoice_Quotation_Items_Beans {

    private String id;
    private String item;
    private String description;
    private double unitPrice;
    private double quantity;
    private double tax;
    private int order;
    private String dateCreation;
    private double discount;
    private double total;
    private double taxValue;
    private double totalWIthTax;
    private String descriptionItem;
    private String taxName;
    private boolean isPercent;
    private String idTaxRate;

    public String getDescriptionItem() {
        return descriptionItem;
    }

    public void setDescriptionItem(String descriptionItem) {
        this.descriptionItem = descriptionItem;
    }

    public String getIdTaxRate() {
        return idTaxRate;
    }

    public void setIdTaxRate(String idTaxRate) {
        this.idTaxRate = idTaxRate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getTaxValue() {
        return taxValue;
    }

    public void setTaxValue(double taxValue) {
        this.taxValue = taxValue;
    }

    public double getTotalWIthTax() {
        return totalWIthTax;
    }

    public void setTotalWIthTax(double totalWIthTax) {
        this.totalWIthTax = totalWIthTax;
    }

    public String getTaxName() {
        return taxName;
    }

    public void setTaxName(String taxName) {
        this.taxName = taxName;
    }

    public boolean isPercent() {
        return isPercent;
    }

    public void setPercent(boolean percent) {
        isPercent = percent;
    }
}
