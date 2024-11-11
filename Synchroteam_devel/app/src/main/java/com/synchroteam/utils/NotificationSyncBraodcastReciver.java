package com.synchroteam.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.synchroteam.tracking.TrackingParameterService;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.synchroteam.CommonInterface;
import com.synchroteam.synchroteam.SyncroTeamApplication;
import com.synchroteam.synchroteam3.R;

// TODO: Auto-generated Javadoc

/**
 * The Class NotificationSyncBraodcastReciver which perform auto syncing
 * operation when a notification is received. *
 *
 * @author Trident
 */
public class NotificationSyncBraodcastReciver extends BroadcastReceiver {

    /** The alarm manager. */
    // private AlarmManager alarmManager;

    /**
     * The data access object.
     */
    private Dao dataAccessObject;

    /**
     * The user.
     */
    private User user;

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
    @SuppressLint("NewApi")
    @Override
    public void onReceive(Context arg0, Intent arg1) {
        // TODO Auto-generated method stub
        dataAccessObject = DaoManager.getInstance();
        user = dataAccessObject.getUser();
        this.context = arg0;
//		pi = PendingIntent.getService(context, 0, new Intent(context,
//				TrackingService.class), PendingIntent.FLAG_UPDATE_CURRENT);
//
//		pi1 = PendingIntent.getService(context, 0, new Intent(context,
//				TrackingServiceFrequency.class),
//				PendingIntent.FLAG_UPDATE_CURRENT);

        pITrackParams = initializePendingIntent();

        // int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        Logger.log("Broadcast Reciver ", "called");

        // alarmManager = (AlarmManager) arg0
        // .getSystemService(Context.ALARM_SERVICE);
        // if (currentapiVersion >= android.os.Build.VERSION_CODES.KITKAT) {
        // int interval = arg1.getIntExtra(
        // KEYS.SyncronizationSettings.SYNC_INTERVAL, 30);
        //
        // PendingIntent operation = PendingIntent.getBroadcast(arg0, 0, arg1,
        // PendingIntent.FLAG_UPDATE_CURRENT);
        //
        // alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
        // SystemClock.elapsedRealtime() + interval * 60 * 1000,
        // operation);
        //
        // }
        if (SharedPref.getIsPushSyncOn(context)) {
            startSyncing();
        }

    }

    /**
     * Start syncing.
     */
    private void startSyncing() {
        // TODO Auto-generated method stub
        if (((SyncroTeamApplication) context.getApplicationContext())
                .getSyncroteamAppLive()) {

            SyncroTeamApplication app = (SyncroTeamApplication) context
                    .getApplicationContext();
            CommonInterface commonInterface = app
                    .getSyncroteamActivityInstance();
            Activity activity = commonInterface.getActivityInstance();
            DialogUtils.showProgressDialog(activity,
                    activity.getString(R.string.textPleaseWaitLable),
                    activity.getString(R.string.msg_synch), false);

        }

        Thread syncDbToServer = new Thread((new Runnable() {
            @Override
            public void run() {

                Message myMessage = new Message();
                try {
                    dataAccessObject.sync(user.getLogin(), user.getPwd());
                    SynchroteamUitls.logInFile("Auto SYnc Braodcast Called");
                    GestionAcces gt = dataAccessObject.getAcces();
                    if (gt != null && gt.getOptionTaracking() == 0) {
//                        context.stopService(new Intent(context,
//                                TrackingService.class));
//                        AlarmManager am = (AlarmManager) context
//                                .getSystemService(Context.ALARM_SERVICE);
//                        am.cancel(pi);
//
//                        context.stopService(new Intent(context,
//                                TrackingServiceFrequency.class));
//                        AlarmManager am1 = (AlarmManager) context
//                                .getSystemService(Context.ALARM_SERVICE);
//                        am1.cancel(pi1);

                        cancelTrackingAlarm();
                    }
                    SynchroteamUitls
                            .logInFile("LastSync Logged Through Auto Synchronization");
                    myMessage.obj = "ok";
                    autoSyncBroadCastReciverHandler.sendMessage(myMessage);
                } catch (Exception ex) {
                    String exception = ex.getMessage();
                    if (exception != null) {
                        if (exception.indexOf("4006") != -1)
                            myMessage.obj = "4006";
                        else if (exception.indexOf("4101") != -1)
                            myMessage.obj = "4101";
                        else if (exception.indexOf("4005") != -1)
                            myMessage.obj = "4005";
                        else
                            myMessage.obj = "Error";
                    } else
                        myMessage.obj = "Error";
                    SynchroteamUitls
                            .logInFile("LastSync falised Through push sync Auto Synchronization");
                    autoSyncBroadCastReciverHandler.sendMessage(myMessage);
                } finally {
                    DialogUtils.dismissProgressDialog();

                }

            }
        }));
        syncDbToServer.start();
    }

    /**
     * The handler.
     */
    @SuppressLint("HandlerLeak")
    private Handler autoSyncBroadCastReciverHandler = new Handler() {
        public void handleMessage(Message msg) {
            String statusCode = (String) msg.obj;
            if (statusCode.equals("ok")) {

                if (((SyncroTeamApplication) context.getApplicationContext())
                        .getSyncroteamAppLive()) {

                    ((SyncroTeamApplication) context.getApplicationContext())
                            .getSyncroteamActivityInstance().updateUi();

                }
            }

        }
    };

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
            pendingIntent= PendingIntent.getService(context,
                    0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT|
                            PendingIntent.FLAG_IMMUTABLE);
        } else {

            pendingIntent=  PendingIntent.getService(context,
                    0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

//        return PendingIntent.getService(context,
//                0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        return pendingIntent;

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


}