package com.synchroteam.beans;

import java.io.Serializable;

/**
 * POJO class for sorting options in all jobs screen.
 * Created by Trident on 5/23/2016.
 */
public class AllJobsSorting  {
    private String sortingName;
    private boolean isSelected;

    public String getSortingName() {
        return sortingName;
    }

    public void setSortingName(String sortingName) {
        this.sortingName = sortingName;
    }

    public boolean getSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
