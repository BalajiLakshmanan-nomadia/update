package com.synchroteam.beans;

// TODO: Auto-generated Javadoc
/**
 * The Class JobsCount which stores the job count on particular date which is used in calander.
 * @author Ideavate Solution
 */
public class JobsCount {
 
 /** The started job. */
 private int startedJob;
 
 /** The suspended job. */
 private int suspendedJob;
 
 /** The scheduled job. */
 private int scheduledJob;
 
 /** The completed job. */
 private int completedJob;
 
 
 
 /**
  * Gets the started job.
  *
  * @return the started job
  */
 public int getStartedJob() {
	return startedJob;
}

/**
 * Sets the started job.
 *
 * @param startedJob the new started job
 */
public void setStartedJob(int startedJob) {
	this.startedJob = startedJob;
}

/**
 * Gets the suspended job.
 *
 * @return the suspended job
 */
public int getSuspendedJob() {
	return suspendedJob;
}

/**
 * Sets the suspended job.
 *
 * @param suspendedJob the new suspended job
 */
public void setSuspendedJob(int suspendedJob) {
	this.suspendedJob = suspendedJob;
}

/**
 * Gets the scheduled job.
 *
 * @return the scheduled job
 */
public int getScheduledJob() {
	return scheduledJob;
}

/**
 * Sets the scheduled job.
 *
 * @param scheduledJob the new scheduled job
 */
public void setScheduledJob(int scheduledJob) {
	this.scheduledJob = scheduledJob;
}

/**
 * Gets the completed job.
 *
 * @return the completed job
 */
public int getCompletedJob() {
	return completedJob;
}

/**
 * Sets the completed job.
 *
 * @param completedJob the new completed job
 */
public void setCompletedJob(int completedJob) {
	this.completedJob = completedJob;
}

 
}
