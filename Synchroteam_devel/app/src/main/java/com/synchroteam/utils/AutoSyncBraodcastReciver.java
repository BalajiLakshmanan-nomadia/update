package com.synchroteam.utils;

import static android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import com.synchroteam.tracking.TrackingParameterService;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.events.AutoSyncViewUpdateEvent;
import com.synchroteam.synchroteam.CommonInterface;
import com.synchroteam.synchroteam.SyncroTeamApplication;
import com.synchroteam.synchroteam3.R;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc

/**
 * The Class AutoSyncBraodcastReciver which perform auto syncing operation. *
 * created for future use
 *
 * @author Ideavate.solution
 */
public class AutoSyncBraodcastReciver extends BroadcastReceiver {

    /**
     * The alarm manager.
     */
    private AlarmManager alarmManager;

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
//        pi = PendingIntent.getService(context, 0, new Intent(context,
//                TrackingService.class), PendingIntent.FLAG_UPDATE_CURRENT);
//
//        pi1 = PendingIntent.getService(context, 0, new Intent(context,
//                        TrackingServiceFrequency.class),
//                PendingIntent.FLAG_UPDATE_CURRENT);
        pITrackParams = initializePendingIntent();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        Logger.log("Broadcast Reciver ", "called");

        alarmManager = (AlarmManager) arg0
                .getSystemService(Context.ALARM_SERVICE);
        if (currentapiVersion >= android.os.Build.VERSION_CODES.KITKAT) {
            int interval = arg1.getIntExtra(
                    KEYS.SyncronizationSettings.SYNC_INTERVAL, 30);


            PendingIntent operation;

            //Behaviour changes supporting android 12
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                // Create a PendingIntent using FLAG_IMMUTABLE
                operation = PendingIntent.getBroadcast(arg0, 0, arg1,
                        PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
            } else {

                operation = PendingIntent.getBroadcast(arg0, 0, arg1,
                        PendingIntent.FLAG_UPDATE_CURRENT);
            }
//
//            PendingIntent operation = PendingIntent.getBroadcast(arg0, 0, arg1,
//                    PendingIntent.FLAG_UPDATE_CURRENT);

//            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                    SystemClock.elapsedRealtime() + interval * 60 * 1000,
//                    operation);

            // Android 14 changes
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            SystemClock.elapsedRealtime() + interval * 60 * 1000,
                            operation);
                } else {
                    arg0.startActivity(new Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM));

                }
            }else

            //android 'O' changes
            if (Build.VERSION.SDK_INT >= 23) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + interval * 60 * 1000,
                        operation);
            } else if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + interval * 60 * 1000,
                        operation);
            } else {
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + interval * 60 * 1000,
                        operation);
            }

        }
        startSyncing();

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
                    Logger.log("AutoSyncBroadcastReceiver","uto SYnc Braodcast Called");
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
                            myMessage.obj = "StopAutoSync";
                        else if (exception.indexOf("4005") != -1)
                            myMessage.obj = "StopAutoSync";
                        else if (exception.indexOf("4101") != -1)
                            myMessage.obj = "StopAutoSync";
                        else if (exception.indexOf("4001") != -1)
                            myMessage.obj = "StopAutoSync";
                        else if (exception.indexOf("4000") != -1)
                            myMessage.obj = "StopAutoSync";
                        else if (exception.indexOf("4002") != -1)
                            myMessage.obj = "StopAutoSync";
                        else if (exception.indexOf("4100") != -1)
                            myMessage.obj = "StopAutoSync";
                        else if (exception.indexOf("4003") != -1)
                            myMessage.obj = "4003";
                        else
                            myMessage.obj = "Error";
                    } else
                        myMessage.obj = "Error";

                    SynchroteamUitls
                            .logInFile("LastSync falised Through Auto Synchronization");
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
            } else if (statusCode.equalsIgnoreCase("StopAutoSync")) {
                removeAlarmManagerToAutoSyncroniseData();
                EventBus.getDefault().post(new AutoSyncViewUpdateEvent());
            }

        }
    };

    public void removeAlarmManagerToAutoSyncroniseData() {

        Intent intent = ((SyncroTeamApplication) context.getApplicationContext())
                .getAutoSyncIntent();

        PendingIntent alarmIntent;

        //Behaviour changes supporting android 12
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // Create a PendingIntent using FLAG_IMMUTABLE
            alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        } else {

            alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

//        PendingIntent alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(alarmIntent);

        SharedPref.setIsSyncOn(false, context.getApplicationContext());
        SharedPref.setSyncInterval(
                Integer.parseInt(context.getResources().getString(
                        R.string.hintDefaultTimeInterval)), context);
    }

    /**
     * Intializing the pending intent for the tracking service
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

}