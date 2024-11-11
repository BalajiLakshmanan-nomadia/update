package com.synchroteam.beans;

/**
 * The Class Item is used to store the data regarding each item of reports screen .
 */
public class NotificationItem {

    private int status;
    private String idIntervention;

    /**
     * Instantiates a new item.
     *
     * @param status        the status
     * @param idIntervention       the val cond
     */
    public NotificationItem(String idIntervention ,int status) {
        super();
        this.status = status;
        this.idIntervention = idIntervention;

    }

    public int getStatus() {
        return status;
    }

    public String getIdIntervention() {
        return idIntervention;
    }
}
