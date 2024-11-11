package com.synchroteam.utils;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.synchroteam.tracking.TrackingParameterService;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.tracking.DaoTracking;

public class NetworkBroadCastReciver extends BroadcastReceiver {
    private PendingIntent pi, pi1, pITrackParams;
    private Context context2;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        context2 = context;
//        pi = PendingIntent.getService(context2, 0, new Intent(context2,
//                TrackingService.class), PendingIntent.FLAG_UPDATE_CURRENT);
//        pi1 = PendingIntent.getService(context2, 0, new Intent(context2,
//                        TrackingServiceFrequency.class),
//                PendingIntent.FLAG_UPDATE_CURRENT);
        pITrackParams = initializePendingIntent();

        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        SynchroteamUitls.setDiasableNetworkBroadcastReciver(context);
        if (wifi.isAvailable() || mobile.isAvailable()) {
            // Do something

            final Dao dao = DaoManager.getInstance();
            final DaoTracking daoTracking = DaoTrackingManager.getInstance();
            final User user = dao.getUser();

            Thread syncDbToServer = new Thread((new Runnable() {
                @Override
                public void run() {

                    try {

                        daoTracking.sync(user.getLogin(), user.getPwd(),
                                dao.getUserDomain(), Dao.script);

                        SynchroteamUitls
                                .logInFile("First Sync failed time due to no Network");

                        Thread.sleep(1000);

                    } catch (Exception ex) {
                        Logger.printException(ex);

                    }

                }
            }));
            syncDbToServer.start();

        }

    }

    /**
     * Intializin the pending intent for the tracking service
     *
     * @return Pending intent
     */
    private PendingIntent initializePendingIntent() {
        Intent trackingParamsIntent = new Intent(context2,
                TrackingParameterService.class);
        trackingParamsIntent.putExtra("from_pending_intent", true);

        PendingIntent pendingIntent;

        //Behaviour changes supporting android 12
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // Create a PendingIntent using FLAG_IMMUTABLE
            pendingIntent = PendingIntent.getService(context2,
                    0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT|
                            PendingIntent.FLAG_IMMUTABLE);
        } else {

            pendingIntent = PendingIntent.getService(context2,
                    0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return pendingIntent;

//        return PendingIntent.getService(context2,
//                0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

}
