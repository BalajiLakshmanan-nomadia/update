package com.synchroteam.beans;

/**
 * @author Trident
 *
 *         This class holds the getter & setter for the types of unavailability
 */
public class UnavailabilityBeans {

	private int unavailabilityID;
	private String unavailabilityColorCode;
	private String unavailabilityName;
	private int customerID;

	public String getUnavailabilityName() {
		return unavailabilityName;
	}

	public void setUnavailabilityName(String unavailabilityName) {
		this.unavailabilityName = unavailabilityName;
	}



	public int getUnavailabilityID() {
		return unavailabilityID;
	}

	public void setUnavailabilityID(int unavailabilityID) {
		this.unavailabilityID = unavailabilityID;
	}

	public String getUnavailabilityColorCode() {
		return unavailabilityColorCode;
	}

	public void setUnavailabilityColorCode(String unavailabilityColorCode) {
		this.unavailabilityColorCode = unavailabilityColorCode;
	}

	public int getCustomerID() {
		return customerID;
	}

	public void setCustomerID(int customerID) {
		this.customerID = customerID;
	}

}
