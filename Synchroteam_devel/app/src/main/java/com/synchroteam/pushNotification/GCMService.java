package com.synchroteam.pushNotification;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import androidx.core.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.synchroteam.dao.Dao;
import com.synchroteam.synchroteam.SyncoteamNavigationActivity;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;

/**
 * The Class GCMService. author Previous Developer
 */
public class GCMService extends IntentService {

    /**
     * The s wake lock.
     */
    private static PowerManager.WakeLock sWakeLock;

    /**
     * The Constant LOCK.
     */
    private static final Object LOCK = GCMService.class;

    /**
     * The key.
     */
    private static String KEY = "c2dmPref";

    /**
     * The registration key.
     */
    private static String REGISTRATION_KEY = "registrationKey";

    // private static final String TAG = "GCMService";

    /**
     * Instantiates a new GCM service.
     */
    public GCMService() {
        super("GCMService");
    }

    /**
     * Instantiates a new GCM service.
     *
     * @param name the name
     */
    public GCMService(String name) {
        super(name);
    }

    /**
     * Run intent in service.
     *
     * @param context the context
     * @param intent  the intent
     */
    static void runIntentInService(Context context, Intent intent) {
        synchronized (LOCK) {
            if (sWakeLock == null) {
                PowerManager pm = (PowerManager) context
                        .getSystemService(Context.POWER_SERVICE);
                sWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                        ":my_wakelock");
            }
        }
        sWakeLock.acquire();
        intent.setClassName(context, GCMService.class.getName());

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//          // context.getApplicationContext().startForeground(intent);
//        } else {
//            context.startService(intent);
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        }else{
            context.startService(intent);
        }
    }

    private void buildNotificationForeground() {
        PendingIntent broadcastIntent;
        //Behaviour changes supporting android 12
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // Create a PendingIntent using FLAG_IMMUTABLE
            broadcastIntent = PendingIntent.getActivity(
                    this, 0, new Intent(),
                    PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        } else {

            broadcastIntent = PendingIntent.getActivity(
                    this, 0, new Intent(),
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }


        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel("GCM Notification", "Notification", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }

        // Create the persistent notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "GCM Notification")
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.notification_text_gcm))
                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.drawable.icon_notif);
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon_notif));


        Notification notification = builder.build();

        startForeground(1005, notification);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.IntentService#onHandleIntent(android.content.Intent)
     */
    @Override
    public final void onHandleIntent(Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            try {
                buildNotificationForeground();
            } catch (Exception e) {
                Logger.log("GCM SERVICE", "Error while starting foreground service");
            }
        }


        try {
            String action = intent.getAction();
            if (action.equals("com.google.android.c2dm.intent.REGISTRATION")) {
                handleRegistration(this, intent);
            } else if (action.equals("com.google.android.c2dm.intent.RECEIVE")) {
                handleMessage(this, intent);
            }
        } finally {
            synchronized (LOCK) {
                if (sWakeLock != null && sWakeLock.isHeld())
                    sWakeLock.release();
            }
        }
    }

    /**
     * Handle registration.
     *
     * @param context the context
     * @param intent  the intent
     */
    private void handleRegistration(Context context, Intent intent) {
        Dao dao = DaoManager.getInstance();
        String registration = intent.getStringExtra("registration_id");
        if (intent.getStringExtra("error") != null) {
            Log.e("c2dm", "registration failed");
            String error = intent.getStringExtra("error");

            if (!TextUtils.isEmpty(error)) {
                if (error.equals("SERVICE_NOT_AVAILABLE")) {
                    Logger.log("c2dm", "SERVICE_NOT_AVAILABLE");
                } else if (error.equals("ACCOUNT_MISSING")) {
                    Logger.log("c2dm", "ACCOUNT_MISSING");
                } else if (error.equals("AUTHENTICATION_FAILED")) {
                    Logger.log("c2dm", "AUTHENTICATION_FAILED");
                } else if (error.equals("TOO_MANY_REGISTRATIONS")) {
                    Logger.log("c2dm", "TOO_MANY_REGISTRATIONS");
                } else if (error.equals("INVALID_SENDER")) {
                     Logger.log("c2dm", "INVALID_SENDER");
                } else if (error.equals("PHONE_REGISTRATION_ERROR")) {
                    Logger.log("c2dm", "PHONE_REGISTRATION_ERROR");
                }
            }
        } else if (!TextUtils.isEmpty(intent.getStringExtra("unregistered"))) {
            Logger.log("c2dm", "unregistered");

        } else if (!TextUtils.isEmpty(registration)) {
            dao.setIdPushAndroid(registration);
            Editor editor = context.getSharedPreferences(KEY,
                    Context.MODE_PRIVATE).edit();
            editor.putString(REGISTRATION_KEY, registration);
            editor.commit();
        }

    }

    /**
     * Handle message.
     *
     * @param context the context
     * @param intent  the intent
     */
    private void handleMessage(Context context, Intent intent) {


//         send the broadcast to auto sync receiver
        sendBroadCast();

        String message = intent.getStringExtra("message");
        generateNotification(this, message);

    }

    /**
     * Generate notification.
     *
     * @param context the context
     * @param message the message
     */
    @SuppressWarnings("deprecation")
    private void generateNotification(Context context, String message) {
        int icon = R.drawable.icon_notif;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        String title = context.getString(R.string.app_name);
        Intent notificationIntent;

        // if(domain.contains("dalkia"))
        // notificationIntent = new Intent(context, AccueilDalkia.class);
        // else
        notificationIntent = new Intent(context,
                SyncoteamNavigationActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("notification", "yes");
        notificationIntent.putExtras(bundle);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent;

        //Behaviour changes supporting android 12
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // Create a PendingIntent using FLAG_IMMUTABLE
            intent = PendingIntent.getActivity(context, 0,
                    notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        } else {

            intent = PendingIntent.getActivity(context, 0,
                    notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        }



        Notification notification = null;


        //android 'O' changes
        if (Build.VERSION.SDK_INT >= 26) {

            NotificationChannel channel = new NotificationChannel("Noti_channel", "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("");
            notificationManager.createNotificationChannel(channel);

            // Create the persistent notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Noti_channel")
                    .setContentTitle(title)
                    .setContentText(message)
                    .setWhen(when)
                    .setContentIntent(intent)
                    .setTicker(message)
                    .setSmallIcon(icon);
            notification = builder.build();
        } else {
            // Notification notification = new Notification(icon, message, when);
            notification = new NotificationCompat.Builder(context)
                    .setSmallIcon(icon).setTicker(message).setContentTitle(title)
                    .setContentText(message).setWhen(when).setContentIntent(intent)
                    .build();
        }


        notification.defaults = Notification.DEFAULT_ALL;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        // notification.setLatestEventInfo(context, title, message, intent);

        notificationManager.notify(0, notification);
    }

    /**
     * sends the broadcast intent to push sync receiver.
     */
    private void sendBroadCast() {
        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(KEYS.AUTO_SYNC_BROADCAST);
        sendBroadcast(broadCastIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            stopForeground(true);
    }
}
