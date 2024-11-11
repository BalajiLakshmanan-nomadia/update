package com.synchroteam.beans;

import java.util.Vector;

/**
 * Bean class for families in Reports.
 * Created by Trident on 7/27/2016.
 */
public class Families {

    private int idFamily;
    private String nameFamily;
    private int isSharedBlock;
    private int iteration;
    private int iterationCount;
    private int position;
    private int min;
    private int max;
    private int obligatoire;
    private int idEquip;
    private Vector<Item> items;

    public Vector<Item> getItems() {
        return items;
    }

    public void setItems(Vector<Item> items) {
        this.items = items;
    }

    public int getIteration() {
        return iteration;
    }

    public void setIteration(int iteration) {
        this.iteration = iteration;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getIdFamily() {
        return idFamily;
    }

    public void setIdFamily(int idFamily) {
        this.idFamily = idFamily;
    }

    public String getNameFamily() {
        return nameFamily;
    }

    public void setNameFamily(String nameFamily) {
        this.nameFamily = nameFamily;
    }

    public int getIsSharedBlock() {
        return isSharedBlock;
    }

    public void setIsSharedBlock(int isSharedBlock) {
        this.isSharedBlock = isSharedBlock;
    }


    public int getIterationCount() {
        return iterationCount;
    }

    public void setIterationCount(int iterationCount) {
        this.iterationCount = iterationCount;
    }

    public int getObligatoire() {
        return obligatoire;
    }

    public void setObligatoire(int obligatoire) {
        this.obligatoire = obligatoire;
    }


    public int getIdEquip() {
        return idEquip;
    }

    public void setIdEquip(int idEquip) {
        this.idEquip = idEquip;
    }
}
