package com.synchroteam.tracking;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.synchroteam.dao.Dao;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DaoTrackingManager;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.SharedPref;

// TODO: Auto-generated Javadoc

/**
 * The Class UpdateApplicationBroadCastReciver gets called when the application
 * gets updated created for future use
 *
 * @author Ideavate.solution
 */
public class UpdateApplicationBroadCastReciver extends BroadcastReceiver {

    private DaoTracking daoTracking;

    private Dao dataAccessObject;
    private Context context;

    private PendingIntent pi, pi1, pITrackParams;

    /*
     * (non-Javadoc)
     *
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
     * android.content.Intent)
     */
    @Override
    public void onReceive(Context arg0, Intent arg1) {

        this.context = arg0;
        if (arg0 == null) {
            return;
        }
        if (arg1 == null) {
            return;
        }

        if (arg1.getData().getSchemeSpecificPart()
                .equals(context.getPackageName())) { // Restart services.

            System.out
                    .println("Update Application V3.6 **********************");

            /****
             * Should be called when user upgrades the application and should be
             * called when databasre script is updated.
             */

            /**
             * Since the tracking is stopeed we have to change the tracking
             * presets.
             */

            daoTracking = DaoTrackingManager.getInstance();
            dataAccessObject = DaoManager.getInstance();
            if (dataAccessObject.getUserScript().equals(Dao.script)) {

                if (!dataAccessObject.getUserScript().equals("ForceDelete")) {

                    Logger.log("UpdateApplicationBroadCastReciver",
                            "SharedPref");
                    pITrackParams = initializePendingIntent();

                    if (daoTracking.getSessiondata("tracking").equals("on")) {
                        daoTracking.setSessiondata("tracking", "off");
                        // ---------------------------------------------------------
                        SharedPref.setPreviousValueOfTracking(context, false);
                        SharedPref.setIsTrackingRunning(false, context);
//                        context.stopService(new Intent(context,
//                                TrackingService.class));
//                        AlarmManager alarm = (AlarmManager) context
//                                .getSystemService(Context.ALARM_SERVICE);
//                        alarm.cancel(pi);
//
//                        context.stopService(new Intent(context,
//                                TrackingServiceFrequency.class));
//                        AlarmManager alarm1 = (AlarmManager) context
//                                .getSystemService(Context.ALARM_SERVICE);
//                        alarm1.cancel(pi1);

                        cancelTrackingAlarm();
                    }

                }

            }

            ActivityManager am = (ActivityManager) arg0
                    .getSystemService(Activity.ACTIVITY_SERVICE);
            am.killBackgroundProcesses(arg0.getPackageName());
            android.os.Process.killProcess(android.os.Process.myPid());

        }

    }

    /**
     * Intializin the pending intent for the tracking service
     *
     * @return Pending intent
     */
    private PendingIntent initializePendingIntent() {
        Intent trackingParamsIntent = new Intent(this.context,
                TrackingParameterService.class);
        trackingParamsIntent.putExtra("from_pending_intent", true);

        PendingIntent pendingIntent;

        //Behaviour changes supporting android 12
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // Create a PendingIntent using FLAG_IMMUTABLE
            pendingIntent = PendingIntent.getService(this.context,
                    0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT|
                            PendingIntent.FLAG_IMMUTABLE);
        } else {

            pendingIntent = PendingIntent.getService(this.context,
                    0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return pendingIntent;
//        return PendingIntent.getService(this.context,
//                0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    }


    /**
     * For canceling the Tracking service when conditions are met
     */
    private void cancelTrackingAlarm() {
        context.stopService(new Intent(this.context, TrackingParameterService.class));
        AlarmManager am2 = (AlarmManager) this.context
                .getSystemService(Context.ALARM_SERVICE);
        am2.cancel(pITrackParams);
    }
}
