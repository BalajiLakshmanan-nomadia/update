package com.synchroteam.events;

/**
 * Created by Trident
 */

public class JobStartResumeEvent {

    private String eventAction;

    public JobStartResumeEvent(String eventAction) {
        this.eventAction = eventAction;
    }

    public String getEventAction() {
        return eventAction;
    }
}