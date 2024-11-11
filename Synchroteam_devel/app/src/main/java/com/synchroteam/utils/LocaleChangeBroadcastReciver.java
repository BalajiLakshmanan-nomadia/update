package com.synchroteam.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.synchroteam.synchroteam.SyncroTeamApplication;

// TODO: Auto-generated Javadoc
/**
 * The Class LocaleChangeBroadcastReciver.
 */
public class LocaleChangeBroadcastReciver extends BroadcastReceiver {

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		
		((SyncroTeamApplication)arg0.getApplicationContext()).setIsLocaleChanged(true);
		
	}

}
