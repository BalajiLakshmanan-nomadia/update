package com.synchroteam.pushNotification;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// TODO: Auto-generated Javadoc
/**
 * The Class GCMReceiver.
 * author Previous Developer
 */
public class GCMReceiver extends BroadcastReceiver {

	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		   GCMService.runIntentInService(context, intent);
	       setResult(Activity.RESULT_OK, null, null);
	 }
}
