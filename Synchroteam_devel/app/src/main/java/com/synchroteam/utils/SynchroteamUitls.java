package com.synchroteam.utils;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.view.Gravity;
import android.widget.Toast;

import com.synchroteam.synchroteam.AddDuplicateNewJob;
import com.synchroteam.synchroteam.Login_Syncroteam;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.tracking.MobileSwichOffSwitchOnBroadcastReciver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

// TODO: Auto-generated Javadoc

/**
 * The Class SynchroteamUitls.
 */
public class SynchroteamUitls {

    /**
     * The Constant UPDATE_INTERVAL_IN_MILLISECONDS.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10;

    /**
     * The Constant PRIORITY_HIGH_ACCURACY.
     */
    public static final int PRIORITY_HIGH_ACCURACY = 0;

    /**
     * The Constant FAST_INTERVAL_CEILING_IN_MILLISECONDS.
     */
    public static final long FAST_INTERVAL_CEILING_IN_MILLISECONDS = 0;

    private static final String URL_GOOGLE_MAPS = "market://details?id=com.google.android.apps.maps";
    /**
     * Checks if is network available.
     *
     * @return true, if is network available
     */

    public static int GOOGLE_PLAY_SERVICE_CONNECTION_REQUEST_CODE = 1;

    /**
     * Checks if is network available.
     *
     * @param ctx the ctx
     * @return true, if is network available
     */
    public static boolean isNetworkAvailable(Context ctx) {
        boolean isNetwokAvailable = false;
        ConnectivityManager connectionManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectionManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectionManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo ethernetInfo = connectionManager
                .getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);

        if (wifiInfo != null && wifiInfo.isConnected()) {
            isNetwokAvailable = true;
        } else if (mobileInfo != null && mobileInfo.isConnected()) {
            isNetwokAvailable = true;
        }
//        else if (ethernetInfo != null && ethernetInfo.isConnected()) {
//            //new changes for chromebooks
//            isNetwokAvailable = true;
//        }
        else {

            isNetwokAvailable = false;
        }
        return isNetwokAvailable;
    }

    /**
     * Show toast message.
     *
     * @param ctx the ctx
     */
    public static void showToastMessage(Context ctx) {

        Toast toast = Toast
                .makeText(
                        ctx,
                        ctx.getResources().getString(
                                R.string.textNewtworkNorAvaliable),
                        Toast.LENGTH_SHORT);

        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

    }


    public static void setEnableNetworkBroadcastReciver(Context context) {

        ComponentName receiver = new ComponentName(context, NetworkBroadCastReciver.class);

        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

    }

    public static void setDiasableNetworkBroadcastReciver(Context context) {

        ComponentName receiver = new ComponentName(context, NetworkBroadCastReciver.class);

        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

    }


    public static void setDiasableMobileOnOffBroadcastReciver(Context context) {

        ComponentName receiver = new ComponentName(context, MobileSwichOffSwitchOnBroadcastReciver.class);

        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

    }


    public static void setEnableMobileOnOffBroadcastReciver(Context context) {

        ComponentName receiver = new ComponentName(context, MobileSwichOffSwitchOnBroadcastReciver.class);

        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

    }

    public static void logInFile(String msg) {

//        try {
//            File file = new File(Environment.getExternalStorageDirectory() + "/" + "SynchroteanLogs.txt");
////        FileOutputStream  outputStream = new FileOutputStream(file);
//            Calendar cal = Calendar.getInstance();
//            msg = msg + " " + cal.getTime().toString() + "\n";
//            FileWriter fileWritter = new FileWriter(file, true);
//            BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
//            bufferWritter.append(msg);
//            bufferWritter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    public static boolean isGoogleMapInstalled(Context context) {

        try {

            ApplicationInfo info = context.getPackageManager().getApplicationInfo("com.google.android.apps.maps", 0);

            return info.enabled;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


    public static void showGoogleMapsDialog(final Context context) {

        new AlertDialog.Builder(context)
                .setTitle(
                        context
                                .getString(R.string.textEnableGoogleMap))
                .setMessage(
                        context
                                .getString(R.string.textEnableGoogleMapText))
                .setPositiveButton(
                        context.getString(R.string.textOkLable).toUpperCase(),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // show system settings
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(URL_GOOGLE_MAPS));
                                context.startActivity(intent);

                                //Finish the activity so they can't circumvent the check

                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(
                        context
                                .getString(R.string.textDontAllowTracking),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }

    public static void ISTokonExpiryGotoLogin(Context Login) {
       // Toast.makeText(Login, Login.getString(R.string.tokon_expiry), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Login, Login_Syncroteam.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        Login.startActivity(intent);
        ((Activity)Login).finish();

    }
}

	

