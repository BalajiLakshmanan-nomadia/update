package com.synchroteam.tracking;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;

import com.synchroteam.utils.DaoTrackingManager;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.SharedPref;

import java.util.Calendar;

// TODO: Auto-generated Javadoc

/**
 * The Class MobileSwichOffSwitchOnBroadcastReciver.
 */
public class MobileSwichOffSwitchOnBroadcastReciver extends BroadcastReceiver {

    /**
     * The dao tracking.
     */
    private DaoTracking daoTracking;

    /**
     * The context.
     */
    private Context context;

    /**
     * The pi.
     */
    private PendingIntent pi, pi1, pITrackParams;

    /*
     * (non-Javadoc)
     *
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
     * android.content.Intent)
     */
    @Override
    public void onReceive(Context arg0, Intent arg1) {
        // TODO Auto-generated method stub

        Logger.log("MobileSwichOffSwitchOnBroadcastReciver", arg1.getAction());
        daoTracking = DaoTrackingManager.getInstance();

        this.context = arg0;
//        pi = PendingIntent.getService(context, 0, new Intent(context,
//                TrackingService.class), PendingIntent.FLAG_UPDATE_CURRENT);
//        pi1 = PendingIntent.getService(context, 0, new Intent(context,
//                        TrackingServiceFrequency.class),
//                PendingIntent.FLAG_UPDATE_CURRENT);
        pITrackParams = initializePendingIntent();

        if (arg1.getAction().equals(Intent.ACTION_SHUTDOWN)) {
            if (!daoTracking.getSessiondata("tracking").equals("off")) {
                stopTrackingLogout();
            }
        } else if (arg1.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            startTrackingAsPerPreviousSettings();

        }

    }

    /**
     * Start tracking as per previous settings.
     */
    private void startTrackingAsPerPreviousSettings() {
        // TODO Auto-generated method stub
        if (daoTracking.getSessiondata("tracking").equals("off")) {
            if (SharedPref.getPreviousValueOfTracking(context)) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    startTracking();
                }
            }

        }
    }

    /**
     * Start tracking.
     */
    private void startTracking() {
        // TODO Auto-generated method stub
        daoTracking.setSessiondata("tracking", "on");
        // ---------------------------------------------------------
        String h1 = daoTracking.getSessiondata("heur1");
        if (h1.trim().length() > 0) {
            String hh1 = h1.substring(0, 2);
            String mm1 = h1.substring(3, 5);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hh1));
            calendar.set(Calendar.MINUTE, Integer.valueOf(mm1));
            calendar.set(Calendar.SECOND, 0);
            AlarmManager am = (AlarmManager) context
                    .getSystemService(Context.ALARM_SERVICE);
            SharedPref.setPreviousValueOfTracking(context, true);
            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pi);
            triggeringTrackingAtSpecificTime(calendar, pITrackParams);
        }
        if (daoTracking.getSessiondata("tracking").equals("on")) {
//            context.stopService(new Intent(context, TrackingService.class));
//            context.startService(new Intent(context, TrackingService.class));
//
//            context.stopService(new Intent(context,
//                    TrackingServiceFrequency.class));
//            context.startService(new Intent(context,
//                    TrackingServiceFrequency.class));

            try {
                context.stopService(new Intent(context, TrackingParameterService.class));


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(new Intent(context, TrackingParameterService.class));
                }else{
                    context.startService(new Intent(context, TrackingParameterService.class));
                }
            } catch (Exception e) {

            }

        }
    }

    /**
     * Stop tracking.
     */
    public void stopTrackingLogout() {
        daoTracking.setSessiondata("tracking", "off");
        // ---------------------------------------------------------

//        context.stopService(new Intent(context, TrackingService.class));
//        AlarmManager am = (AlarmManager) context
//                .getSystemService(Context.ALARM_SERVICE);
//        am.cancel(pi);
//
//        context.stopService(new Intent(context, TrackingServiceFrequency.class));
//        AlarmManager am1 = (AlarmManager) context
//                .getSystemService(Context.ALARM_SERVICE);
//        am1.cancel(pi1);

        cancelTrackingAlarm();
    }

    /**
     * Intializin the pending intent for the tracking service
     *
     * @return Pending intent
     */
    private PendingIntent initializePendingIntent() {
        Intent trackingParamsIntent = new Intent(context,
                TrackingParameterService.class);
        trackingParamsIntent.putExtra("from_pending_intent", true);


        PendingIntent pendingIntent;
        //Behaviour changes supporting android 12
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // Create a PendingIntent using FLAG_IMMUTABLE
            pendingIntent = PendingIntent.getService(context,
                    0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT|
                            PendingIntent.FLAG_IMMUTABLE);
        } else {

            pendingIntent = PendingIntent.getService(context,
                    0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return pendingIntent;
//        return PendingIntent.getService(context,
//                0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    }


    /**
     * For canceling the Tracking service when conditions are met
     */
    private void cancelTrackingAlarm() {
        context.stopService(new Intent(context, TrackingParameterService.class));
        AlarmManager am2 = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        am2.cancel(pITrackParams);
    }

    /**
     * Trigger pending intent at a later time
     *
     * @param cal
     * @param pITrackParams
     */
    private void triggeringTrackingAtSpecificTime(Calendar cal, PendingIntent pITrackParams) {
        AlarmManager trackingParamsAlarm = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        trackingParamsAlarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pITrackParams);

    }
}
