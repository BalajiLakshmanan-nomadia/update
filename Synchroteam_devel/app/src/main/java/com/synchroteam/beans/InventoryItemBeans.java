package com.synchroteam.beans;

/**
 * This model class holds the values for inventory list items.
 *
 * @author Trident
 */
public class InventoryItemBeans {

    private String idPiece;
    private String reference;
    private String category;
    private String partsService;
    private int isSerializable;
    private double costOfItem;
    private String noOfPieces;
    private String taxRate;
    private int idTaxRate;
    private int isTracked;
    private String description;

    public InventoryItemBeans() {
    }

    public InventoryItemBeans(String idPiece, String reference, String category,
                              String partsService, int isSerializable, double costOfItem,
                              String noOfPieces, int idTaxRate, int isTracked,String description) {
        this.idPiece = idPiece;
        this.reference = reference;
        this.category = category;
        this.partsService = partsService;
        this.isSerializable = isSerializable;
        this.costOfItem = costOfItem;
        this.noOfPieces = noOfPieces;
        this.idTaxRate = idTaxRate;
        this.isTracked = isTracked;
        this.description=description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIsTracked() {
        return isTracked;
    }

    public void setIsTracked(int isTracked) {
        this.isTracked = isTracked;
    }

    public int getIdTaxRate() {
        return idTaxRate;
    }

    public void setIdTaxRate(int idTaxRate) {
        this.idTaxRate = idTaxRate;
    }

    public String getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(String taxRate) {
        this.taxRate = taxRate;
    }

    public String getNoOfPieces() {
        return noOfPieces;
    }

    public void setNoOfPieces(String noOfPieces) {
        this.noOfPieces = noOfPieces;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPartsService() {
        return partsService;
    }

    public void setPartsService(String partsService) {
        this.partsService = partsService;
    }

    public int getIsSerializable() {
        return isSerializable;
    }

    public void setIsSerializable(int isSerializable) {
        this.isSerializable = isSerializable;
    }

    public double getCostOfItem() {
        return costOfItem;
    }

    public void setCostOfItem(double costOfItem) {
        this.costOfItem = costOfItem;
    }

    public String getIdPiece() {
        return idPiece;
    }

    public void setIdPiece(String idPiece) {
        this.idPiece = idPiece;
    }

}
