package com.synchroteam.utils;

import android.view.View;
import android.view.View.OnClickListener;

/**
 * <p>
 * Listener class to catch the event for double click
 * </p>
 * 
 * @author Trident
 *
 */
public abstract class DoubleClickListener implements OnClickListener {

	private static final long DOUBLE_CLICK_TIME_DELTA = 300;// milliseconds

	long lastClickTime = 0;

	@Override
	public void onClick(View v) {
		long clickTime = System.currentTimeMillis();
		if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
			onDoubleClick(v);
		} else {
			onSingleClick(v);
		}
		lastClickTime = clickTime;
	}

	public abstract void onSingleClick(View v);

	public abstract void onDoubleClick(View v);
}
