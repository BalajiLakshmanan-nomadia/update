package com.synchroteam.beans;

// TODO: Auto-generated Javadoc
/**
 * The Class CommonListBean is used to diffrentiate between beans of Current Job and conge and is used in Current Job Listing.
 * @author Ideavate.Solution 
 */
public  class CommonListBean {

	/** The is job bean. */
	private boolean isJobBean;

	/** The header date. */
	private String headerDate;
	
	/**
	 * Instantiates a new common list bean.
	 *
	 * @param isJobBean the is job bean
	 * @param headerDate the header date
	 */
	public CommonListBean(boolean isJobBean,String headerDate) {
		// TODO Auto-generated constructor stub

		this.isJobBean = isJobBean;
		this.headerDate=headerDate;

	}

	 /**
 	 * Gets the checks if is job bean.
 	 *
 	 * @return the checks if is job bean
 	 */
 	public boolean getIsJobBean(){
		 
		 return isJobBean;
	 }
	 
 	/**
 	 * Gets the header date.
 	 *
 	 * @return the header date
 	 */
 	public String getHeaderDate(){
		 
		 return headerDate;
	 }

}
