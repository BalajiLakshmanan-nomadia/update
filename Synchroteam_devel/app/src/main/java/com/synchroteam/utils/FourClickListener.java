package com.synchroteam.utils;

import android.view.View;
import android.view.View.OnClickListener;

public abstract class FourClickListener implements OnClickListener {

	private static final long FOUR_CLICK_TIME_DELTA = 1000;

	int noOfClicks = 0;
	long lastClickTime = 0;

	@Override
	public void onClick(View v) {
		long clickTime = System.currentTimeMillis();

		if (clickTime - lastClickTime < FOUR_CLICK_TIME_DELTA) {
			if (noOfClicks == 2) {
				onFourthClick(v);
				noOfClicks = 0;
			} else
				noOfClicks++;
		} else {
				noOfClicks = 0;
		}
		lastClickTime = clickTime;
	}

	public abstract void onFourthClick(View v);

}
