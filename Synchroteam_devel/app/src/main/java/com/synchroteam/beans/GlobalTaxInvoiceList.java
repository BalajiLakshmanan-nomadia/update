package com.synchroteam.beans;


public class GlobalTaxInvoiceList {


    private double tax;

    private double taxValue;

    private String taxName;

    private boolean hasCompound;

    private String idTaxRate;

    private String idRemote;

    public GlobalTaxInvoiceList(double tax, double taxValue, String taxName, boolean hasCompound,
                                String idTaxRate, String idRemote) {
        this.tax = tax;
        this.taxValue = taxValue;
        this.taxName = taxName;
        this.hasCompound = hasCompound;
        this.idTaxRate = idTaxRate;
        this.idRemote=idRemote;

    }

    public String getIdRemote() {
        return idRemote;
    }

    public void setIdRemote(String idRemote) {
        this.idRemote = idRemote;
    }

    public String getIdTaxRate() {
        return idTaxRate;
    }

    public void setIdTaxRate(String idTaxRate) {
        this.idTaxRate = idTaxRate;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getTaxValue() {
        return taxValue;
    }

    public void setTaxValue(double taxValue) {
        this.taxValue = taxValue;
    }

    public String getTaxName() {
        return taxName;
    }

    public void setTaxName(String taxName) {
        this.taxName = taxName;
    }

    public boolean isHasCompound() {
        return hasCompound;
    }

    public void setHasCompound(boolean hasCompound) {
        this.hasCompound = hasCompound;
    }
}
