package com.synchroteam.beans;


/**
 * Model class for the calculating total clock in and out
 *
 * @Author Dharmalingam.R
 * @Company Trident
 */

public class TotalClockInOutBean {
    String startDateTime;
    String endDateTime;
    String startTime;
    String endTime;


    public TotalClockInOutBean(String startDateTime, String endDateTime, String startTime, String endTime) {
        this.endDateTime = endDateTime;
        this.startDateTime = startDateTime;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
