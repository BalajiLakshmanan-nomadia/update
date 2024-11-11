package com.synchroteam.events;

/**
 * Created by Trident.
 */

public class TrackingParamsActionEvent {

    private boolean eventStart;

    public TrackingParamsActionEvent(boolean eventAction) {
        this.eventStart = eventStart;
    }

    public boolean getEventAction() {
        return eventStart;
    }
}
