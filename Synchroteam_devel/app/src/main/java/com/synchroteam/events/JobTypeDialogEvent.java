package com.synchroteam.events;


/**
 * @Desc
 * @Author Dharmalingam.R
 * @Company Trident Solution & Created on 06 Feb 2018
 */

public class JobTypeDialogEvent {

    private int eventAction;
    private boolean hasMultipleJobs;

    public JobTypeDialogEvent(int eventAction, boolean hasMultipleJobs) {
        this.eventAction = eventAction;
        this.hasMultipleJobs = hasMultipleJobs;
    }

    public int getEventAction() {
        return eventAction;
    }

    public boolean getEventHasMultipleJobs() {
        return hasMultipleJobs;
    }
}
