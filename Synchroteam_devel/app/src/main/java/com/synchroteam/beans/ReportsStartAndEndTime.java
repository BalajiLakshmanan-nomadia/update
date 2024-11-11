package com.synchroteam.beans;

// TODO: Auto-generated Javadoc
/**
 * This class was created to store start and end time which are to be shown on reports detail
 * The Class ReportsStartAndEndTime .
 * @author ${Ideavate Solution}
 */
public class ReportsStartAndEndTime {
	

	/** The start date. */
	private String startDate;
	
	/** The end date. */
	private String endDate;
	
	/** The start time. */
	private String startTime;
	

	/** The end time. */
	private String endTime;
	

	/**
	 * Instantiates a new reports start and end time.
	 *
	 * @param startDate the start date
	 * @param endDate the end date
	 * @param startTime the start time
	 * @param endTime the end time
	 */
	public ReportsStartAndEndTime(String startDate,String endDate,String startTime,String endTime) {
		// TODO Auto-generated constructor stub
		
		this.startDate=startDate;
		this.endDate=endDate;
		this.startTime=startTime;
		this.endTime=endTime;
	}
	
	
	

	/**
	 * Gets the start date.
	 *
	 * @return the startTime
	 */
	public String getStartDate() {
		return startDate;
	}

	/**
	 * Gets the end date.
	 *
	 * @return the endDate
	 */
	public String getEndDate() {
		return endDate;
	}
	
	/**
	 * Gets the start time.
	 *
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}




	/**
	 * Gets the end time.
	 *
	 * @return the endTime
	 */
	public String getEndTime() {
		return endTime;
	}
	
}
