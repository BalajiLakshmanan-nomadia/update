package com.synchroteam.events;

/**
 * Created by Trident.
 */

public class JobPauseFinishEvent {

    private String eventAction;

    public JobPauseFinishEvent(String eventAction) {
        this.eventAction = eventAction;
    }

    public String getEventAction() {
        return eventAction;
    }
}
