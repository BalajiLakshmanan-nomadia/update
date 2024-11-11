package com.synchroteam.utils;

import com.synchroteam.dao.Dao;
import com.synchroteam.synchroteam.SyncroTeamApplication;

// TODO: Auto-generated Javadoc
/**
 * The Class DaoManager.
 */
public class DaoManager {

	/** The m instance. */
	private static Dao mInstance = null;

	private DaoManager() {

	}

	/**
	 * Gets the single instance of DaoManager.
	 *
	 * @return single instance of DaoManager
	 */
	public static Dao getInstance() {
		if (mInstance == null)
			synchronized (Dao.class) {
				if (mInstance == null) {
					mInstance = new Dao(SyncroTeamApplication.getAppContext());
				}
			}
		return mInstance;
	}

	/**
	 * Reset dao.
	 */
	public static void resetDao() {
		mInstance = null;
	}
}
