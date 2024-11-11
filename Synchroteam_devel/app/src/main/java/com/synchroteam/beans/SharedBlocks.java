package com.synchroteam.beans;

import java.io.Serializable;

/**
 * POJO class for sorting options in all jobs screen.
 * Created by Trident on 5/23/2016.
 */
public class SharedBlocks implements Serializable{

    private int idBlock;
    private String blockName;
    private int idEquip;
    private int obligatoire;
    private boolean isSelected = false;

    public String getBlockName() {
        return blockName;
    }

    public void setBlockName(String blockName) {
        this.blockName = blockName;
    }

    public boolean getSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getIdBlock() {
        return idBlock;
    }

    public void setIdBlock(int idBlock) {
        this.idBlock = idBlock;
    }

    public int getIdEquip() {
        return idEquip;
    }

    public void setIdEquip(int idEquip) {
        this.idEquip = idEquip;
    }

    public int getObligatoire() {
        return obligatoire;
    }

    public void setObligatoire(int obligatoire) {
        this.obligatoire = obligatoire;
    }
}
