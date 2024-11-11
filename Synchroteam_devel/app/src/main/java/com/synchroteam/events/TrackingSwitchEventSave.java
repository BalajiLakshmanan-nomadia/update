package com.synchroteam.events;

/**
 * This class will subscribe for tracking toggle in navigation drawer list and if it's switched between on/off the event will be transferred to the tracking fragment to on/off th switch.
 * Created by Trident on 10/25/2016.
 */

public class TrackingSwitchEventSave {

    public boolean isTrackingOn;

    /**
     * Constructor
     *
     * @param isTrackingOn : boolean value for switch
     */
    public TrackingSwitchEventSave(boolean isTrackingOn) {
        this.isTrackingOn = isTrackingOn;
    }
}
