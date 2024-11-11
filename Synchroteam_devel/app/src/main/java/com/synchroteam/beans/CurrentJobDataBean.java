package com.synchroteam.beans;

import java.util.ArrayList;
import java.util.Vector;

// TODO: Auto-generated Javadoc
/**
 * The Class CurrentJobDataBean is used to store data of Jobs in Current Job Screen.
 */
public class CurrentJobDataBean {
	
	/** The current jobs beans. */
	private ArrayList<CommonListBean> currentJobsBeans;
	
	/** The current job count. */
	private int currentJobCount;

	/**
	 * Instantiates a new current job data bean.
	 *
	 * @param currentJobsBeans the current jobs beans
	 * @param currentJobCount the current job count
	 */
	public CurrentJobDataBean(ArrayList<CommonListBean> currentJobsBeans,
							  int currentJobCount) {

		this.currentJobsBeans = currentJobsBeans;
		this.currentJobCount = currentJobCount;
	}
 
	/**
	 * Gets the common job data bean.
	 *
	 * @return the common job data bean
	 */
	public ArrayList<CommonListBean> getCommonJobDataBean(){
		return currentJobsBeans;
	}
	
	
	/**
	 * Gets the currrnt job count.
	 *
	 * @return the currrnt job count
	 */
	public int getCurrrntJobCount(){
		return currentJobCount;
	}
	
	
}
