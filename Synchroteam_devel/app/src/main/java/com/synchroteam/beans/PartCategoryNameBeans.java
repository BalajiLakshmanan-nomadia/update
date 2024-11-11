package com.synchroteam.beans;

public class PartCategoryNameBeans {


    private int idPiece;
    private int idCategory;
    private String cdProduit;
    private String partName;
    private String nameCategory;

    public PartCategoryNameBeans(int idCategory, String nameCategory) {
        this.idCategory = idCategory;
        this.nameCategory = nameCategory;
    }

    public PartCategoryNameBeans(int idPiece, int idCategory, String partName, String cdProduit,String nameCategory) {
        this.idPiece = idPiece;
        this.idCategory = idCategory;
        this.partName = partName;
        this.cdProduit = cdProduit;
        this.nameCategory = nameCategory;
    }


    public int getIdPiece() {
        return idPiece;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public String getCdProduit() {
        return cdProduit;
    }

    public String getPartName() {
        return partName;
    }

    public String getNameCategory() {
        return nameCategory;
    }


}
