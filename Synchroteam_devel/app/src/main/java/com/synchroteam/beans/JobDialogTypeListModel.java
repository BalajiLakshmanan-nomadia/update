package com.synchroteam.beans;


/**
 * @Desc
 * @Author Dharmalingam.R
 */

public class JobDialogTypeListModel {
    String jobType;
    boolean hasMultipleJobs;
    int jobTypeName;

    public int getJobTypeName() {
        return jobTypeName;
    }

    public void setJobTypeName(int jobTypeName) {
        this.jobTypeName = jobTypeName;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public boolean isHasMultipleJobs() {
        return hasMultipleJobs;
    }

    public void setHasMultipleJobs(boolean hasMultipleJobs) {
        this.hasMultipleJobs = hasMultipleJobs;
    }
}
