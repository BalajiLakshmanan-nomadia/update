package com.synchroteam.utils;

import com.synchroteam.synchroteam.SyncroTeamApplication;
import com.synchroteam.tracking.DaoTracking;

// TODO: Auto-generated Javadoc
/**
 * The Class DaoTrackingManager.
 */
public class DaoTrackingManager {

	/** The m instance. */
	private static DaoTracking mInstance = null;

	private DaoTrackingManager() {

	}

	/**
	 * Gets the single instance of DaoTrackingManager.
	 * 
	 * @return single instance of DaoTrackingManager
	 */
	public static DaoTracking getInstance() {
		if (mInstance == null)
			synchronized (DaoTracking.class) {
				if (mInstance == null) {
					mInstance = new DaoTracking(
							SyncroTeamApplication.getAppContext());
				}
			}
		return mInstance;
	}

	/**
	 * Reset dao tracking.
	 */
	public static void resetDaoTracking() {
		mInstance = null;
	}
}
