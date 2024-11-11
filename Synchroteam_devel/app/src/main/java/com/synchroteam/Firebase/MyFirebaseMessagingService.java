package com.synchroteam.Firebase;

import static androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.synchroteam.dao.Dao;
import com.synchroteam.synchroteam.SyncoteamNavigationActivity;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private static final String TAG = "MyFirebaseMsgService";

    /**
     * The key.
     */
    private static String KEY = "c2dmPref";

    /**
     * The registration key.
     */
    private static String REGISTRATION_KEY = "registrationKey";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());
        String message = "";

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            Map<String, String> messageArray = remoteMessage.getData();
            if (messageArray != null && messageArray.size() > 0) {
                message = messageArray.get("message");
                handleMessage(message);
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());

            if (remoteMessage.getNotification().getBody() != null &&
                    remoteMessage.getNotification().getBody().trim().length() > 0) {
                message = remoteMessage.getNotification().getBody().trim();
                handleMessage(message);

            }
        }


    }


    /**
     * Handle message.
     *
     * @param message the intent
     */
    private void handleMessage(String message) {

        if (message != null && message.trim().length() > 0) {
            // send the broadcast to auto sync receiver
            sendBroadCast();
            generateCustomNotification(message);
        }
    }

    /**
     * sends the broadcast intent to push sync receiver.
     */
    private void sendBroadCast() {
        Intent broadCastIntent = new Intent();
        broadCastIntent.setAction(KEYS.AUTO_SYNC_BROADCAST);
        sendBroadcast(broadCastIntent);
    }


//    /**
//     * Called if InstanceID token is updated.
//     */
//    @Override
//    public void onNewToken(String token) {
//        Log.d(TAG, "Refreshed token: " + token);
//        sendRegistrationToServer(token);
//    }

    /**
     * Send Registration token to the server
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        Dao dao = DaoManager.getInstance();
        Logger.log(TAG, "Registration token is =======>" + token);
        if (!TextUtils.isEmpty(token)) {
            dao.setIdPushAndroid(token);
            SharedPreferences.Editor editor = getSharedPreferences(KEY,
                    Context.MODE_PRIVATE).edit();
            editor.putString(REGISTRATION_KEY, token);
            editor.commit();
        }
    }


//    /**
//     * Create and show a simple notification containing the received FCM message.
//     *
//     * @param message FCM message body received.
//     */
//    private void generateNotification(String message) {
//        int icon = R.drawable.icon_notif;
//        long when = System.currentTimeMillis();
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        String title = getString(R.string.app_name);
//        Intent notificationIntent;
//        notificationIntent = new Intent(this,
//                SyncoteamNavigationActivity.class);
//
//        Bundle bundle = new Bundle();
//        bundle.putString("notification", "yes");
//        notificationIntent.putExtras(bundle);
//
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
//                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent intent = PendingIntent.getActivity(this, 0,
//                notificationIntent, 0);
//
//        Notification notification = null;
//
//
//        //android 'O' changes
//        if (Build.VERSION.SDK_INT >= 26) {
//
//            NotificationChannel channel = new NotificationChannel("Noti_channel", "Notification",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//            channel.setDescription("");
//            notificationManager.createNotificationChannel(channel);
//
//            // Create the persistent notification
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Noti_channel")
//                    .setContentTitle(title)
//                    .setContentText(message)
//                    .setWhen(when)
//                    .setContentIntent(intent)
//                    .setTicker(message)
//                    .setSmallIcon(R.drawable.icon_notif);
////                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon_notif));
//            notification = builder.build();
//        } else {
//            // Notification notification = new Notification(icon, message, when);
//            notification = new NotificationCompat.Builder(this)
//                    .setSmallIcon(R.drawable.icon_notif)
////                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon_notif))
//                    .setTicker(message).setContentTitle(title)
//                    .setContentText(message).setWhen(when).setContentIntent(intent)
//                    .build();
//        }
//
//
//        notification.defaults = Notification.DEFAULT_ALL;
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//
//        notificationManager.notify(0, notification);
//    }

    /**
     * Create and show a simple notification containing the received FCM message.
     * V54 changes
     *
     * @param message FCM message body received.
     */
    private void generateNotification(String message) {
        int icon = R.drawable.icon_notif;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        String title = getString(R.string.app_name);
        Intent notificationIntent;
        notificationIntent = new Intent(this,
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
            intent = PendingIntent.getActivity(this, 0,
                    notificationIntent,  PendingIntent.FLAG_IMMUTABLE);
        } else {

            intent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);
        }

//        PendingIntent intent = PendingIntent.getActivity(this, 0,
//                notificationIntent, 0);

        Notification notification = null;
        //android 'O' changes
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel("Noti_channel", "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("");
            notificationManager.createNotificationChannel(channel);
            // Create the persistent notification
//            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Noti_channel")
//                    .setContentTitle(title)
//                    .setContentText(message)
//                    .setWhen(when)
//                    .setContentIntent(intent)
//                    .setTicker(message)
//                    .setSmallIcon(icon);
//            notification = builder.build();
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_notif);
            notification = new NotificationCompat.Builder(this, "Noti_channel")
                    .setSmallIcon(icon)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setLargeIcon(largeIcon)
                    .setStyle(new NotificationCompat.BigPictureStyle()
                            .bigPicture(largeIcon)
                            .bigLargeIcon(null))
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(message))
                    .build();
        } else {
            // Notification notification = new Notification(icon, message, when);
//            notification = new NotificationCompat.Builder(this)
//                    .setSmallIcon(icon).setTicker(message).setContentTitle(title)
//                    .setContentText(message).setWhen(when).setContentIntent(intent)
//                    .build();
            Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_notif);
//            notification = new NotificationCompat.Builder(this, "Noti_channel")
            notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(icon)
                    .setLargeIcon(largeIcon)
                    .setContentTitle(title)
                    .setContentText(message).build();
        }
//
        notification.defaults = Notification.DEFAULT_ALL;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >=
                android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.notif_black : R.drawable.icon_notif_new;
    }


    /**
     * Create and show a simple notification containing the received FCM message.
     * V54 changes
     *
     * @param message FCM message body received.
     */
    private void generateCustomNotification(String message) {
        int icon = R.drawable.notif_black;
        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);
        String title = getString(R.string.app_name);
        Intent notificationIntent;
        notificationIntent = new Intent(this,
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
            intent = PendingIntent.getActivity(this, 0,
                    notificationIntent,  PendingIntent.FLAG_IMMUTABLE);
        } else {

            intent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);
        }

        Notification notification = null;

        @SuppressLint("RemoteViewLayout") RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_item);
        notificationLayout.setTextViewText(R.id.txt_time,
                DateUtils.formatDateTime(this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME));
        notificationLayout.setTextViewText(R.id.tv_msg, message);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_notif);

         //android 'O' changes
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel("Noti_channel", "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("");
            notificationManager.createNotificationChannel(channel);
            notification = new NotificationCompat.Builder(this, "Noti_channel")
                    .setSmallIcon(icon)
                    .setColorized(true)
                    .setColor(getResources().getColor(R.color.orange))
                    .setLargeIcon(largeIcon)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(notificationLayout)
                    .setContentIntent(intent)
                    .build();
        } else {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                notification = new NotificationCompat.Builder(this)
                        .setSmallIcon(icon)
                        .setColor(getResources().getColor(R.color.orange))
                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                        .setCustomContentView(notificationLayout)
                        .setLargeIcon(largeIcon)
                        .setContentIntent(intent)
                        .build();


            }else{
                notification = new NotificationCompat.Builder(this)
                        .setSmallIcon(icon)
                        .setLargeIcon(largeIcon)
                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                        .setCustomContentView(notificationLayout)
                        .setContentIntent(intent)
                        .build();
            }

        }


        notification.defaults = Notification.DEFAULT_ALL;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0, notification);
    }
}