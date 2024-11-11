package com.synchroteam.tracking;

import android.location.Location;


// TODO: Auto-generated Javadoc
/**
 * The Class TrackPoint.
 *
 * @author Previous Developer
 * The Class TrackPoint.
 */
public class TrackPoint {
	
	/** The location. */
	private Location location;
	
	/** The time stamp. */
	private long timeStamp;
	
	
	
	/**
	 * Instantiates a new track point.
	 */
	public TrackPoint() {
		super();
		// TODO Auto-generated constructor stub
	}
	

	/**
	 * Instantiates a new track point.
	 *
	 * @param location the location
	 * @param timeStamp the time stamp
	 */
	public TrackPoint(Location location, long timeStamp) {
		super();
		this.location = location;
		this.timeStamp = timeStamp;
	}


	/**
	 * Gets the location.
	 *
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Sets the location.
	 *
	 * @param location the new location
	 */
	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * Gets the time stamp.
	 *
	 * @return the time stamp
	 */
	public long getTimeStamp() {
		return timeStamp;
	}
	
	/**
	 * Sets the time stamp.
	 *
	 * @param timeStamp the new time stamp
	 */
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
}
