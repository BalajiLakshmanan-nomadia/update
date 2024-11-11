package com.synchroteam.beans;

import hirondelle.date4j.DateTime;

// TODO: Auto-generated Javadoc
/**
 * The Class DateTimeAlongWithJobsCountBean.
 */
public class DateTimeAlongWithJobsCountBean {
	
	/** The date time. */
	private DateTime dateTime;
	
	/** The start job count. */
	private int startJobCount;
	
	/** The suspended job count. */
	private int suspendedJobCount;
	
	/** The scheduled job count. */
	private int scheduledJobCount;

	/** The completed job count. */
	private int completedJobCount;

	/**
	 * Instantiates a new date time along with jobs count bean.
	 *
	 * @param dateTime the date time
	 * @param startJobCount the start job count
	 * @param suspendedJobCount the suspended job count
	 * @param scheduledJobCount the scheduled job count
	 * @param completedJobCount the completed job count
	 */
	public DateTimeAlongWithJobsCountBean(DateTime dateTime, int startJobCount,
			int suspendedJobCount, int scheduledJobCount, int completedJobCount) {

		this.dateTime = dateTime;
		this.startJobCount = startJobCount;
		this.suspendedJobCount = suspendedJobCount;
		this.scheduledJobCount = scheduledJobCount;
		this.completedJobCount = completedJobCount;
	}

	/**
	 * Gets the date time.
	 *
	 * @return the date time
	 */
	public DateTime getDateTime() {
		return dateTime;
	}

	/**
	 * Sets the date time.
	 *
	 * @param dateTime the new date time
	 */
	public void setDateTime(DateTime dateTime) {
		this.dateTime = dateTime;
	}

	/**
	 * Gets the start job count.
	 *
	 * @return the start job count
	 */
	public int getStartJobCount() {
		return startJobCount;
	}

	/**
	 * Sets the start job count.
	 *
	 * @param startJobCount the new start job count
	 */
	public void setStartJobCount(int startJobCount) {
		this.startJobCount = startJobCount;
	}

	/**
	 * Gets the suspended job count.
	 *
	 * @return the suspended job count
	 */
	public int getSuspendedJobCount() {
		return suspendedJobCount;
	}

	/**
	 * Sets the suspended job count.
	 *
	 * @param suspendedJobCount the new suspended job count
	 */
	public void setSuspendedJobCount(int suspendedJobCount) {
		this.suspendedJobCount = suspendedJobCount;
	}

	/**
	 * Gets the scheduled job count.
	 *
	 * @return the scheduled job count
	 */
	public int getScheduledJobCount() {
		return scheduledJobCount;
	}

	/**
	 * Sets the scheduled job count.
	 *
	 * @param scheduledJobCount the new scheduled job count
	 */
	public void setScheduledJobCount(int scheduledJobCount) {
		this.scheduledJobCount = scheduledJobCount;
	}

	/**
	 * Gets the completed job count.
	 *
	 * @return the completed job count
	 */
	public int getCompletedJobCount() {
		return completedJobCount;
	}

	/**
	 * Sets the completed job count.
	 *
	 * @param completedJobCount the new completed job count
	 */
	public void setCompletedJobCount(int completedJobCount) {
		this.completedJobCount = completedJobCount;
	}

	
	/**
	 * Gets the total jobs.
	 *
	 * @return the total jobs
	 */
	public int getTotalJobs(){
		return startJobCount+suspendedJobCount+scheduledJobCount+completedJobCount;
	}
}
