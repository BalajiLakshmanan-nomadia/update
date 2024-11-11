package com.synchroteam.events;

/**
 * This event used to update the items in the family in ReportView when the report was updated by the user.
 * Created by Trident on 12/9/2016.
 */

public class ReportFamilyUpdateEvent {

    public int familyPosition;

    /**
     * Constructor
     *
     * @param familyPosition : Position of the family in report
     */
    public ReportFamilyUpdateEvent(int familyPosition) {
        this.familyPosition = familyPosition;
    }

}
