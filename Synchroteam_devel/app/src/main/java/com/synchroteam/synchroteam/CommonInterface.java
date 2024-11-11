package com.synchroteam.synchroteam;

import android.app.Activity;

// TODO: Auto-generated Javadoc
/**
 * The Interface CommonInterface.
 */
public interface CommonInterface {

	/**
	 * Gets the activity instance.
	 *
	 * @return the activity instance
	 */
	public Activity getActivityInstance();
	
	/**
	 * Update ui.
	 */
	public void updateUi();

	public void updateUiOnTrakingStatusChange(boolean isRunning);
}
