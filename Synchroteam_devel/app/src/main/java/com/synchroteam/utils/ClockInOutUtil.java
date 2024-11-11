package com.synchroteam.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.synchroteam.dao.Dao;
import com.synchroteam.receiver.ClockInTimeOutReceiver;
import com.synchroteam.synchroteam.SyncoteamNavigationActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;
import static android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM;

/**
 * Created by Trident on 10/10/2017.
 */

public class ClockInOutUtil {

    /**
     * starts the alarm to check the inactivity for 15 minutes interval
     */
    public static void startAlarmForTimeOut(Context mContext) {
        Dao dao = DaoManager.getInstance();
        String minutes = dao.getAutoClockOutTime();
        Long clockOutTime = null;
        if (minutes != null && minutes.length() > 0) {
            clockOutTime = Long.parseLong(minutes) * 60 * 1000;
            Logger.log("ClockInOutUtil", "Auto clockOut Time in min is--->" + minutes);
            Logger.log("ClockInOutUtil", "Auto clockOut Time is--->" + clockOutTime);

            Intent alarmIntent = new Intent(mContext, ClockInTimeOutReceiver.class);

            PendingIntent pendingIntent;

            //Behaviour changes supporting android 12
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                // Create a PendingIntent using FLAG_IMMUTABLE
                pendingIntent = PendingIntent.getBroadcast(mContext,
                        1001, alarmIntent, 0| PendingIntent.FLAG_IMMUTABLE);
            } else {

                pendingIntent = PendingIntent.getBroadcast(mContext,
                        1001, alarmIntent, 0);
            }

//            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,
//                    1001, alarmIntent, 0);

            AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
//        alarmManager.set(AlarmManager.RTC_WAKEUP,
//                         System.currentTimeMillis() + (Commons.CLOCK_IN_TIME_OUT_SECS * 1000),
//                         pendingIntent);

//            alarmManager.set(AlarmManager.RTC_WAKEUP,
//                    System.currentTimeMillis() + clockOutTime,
//                    pendingIntent);

            // Android 14 changes

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                            System.currentTimeMillis() + clockOutTime,
                            pendingIntent);
                } else {
                    mContext.startActivity(new Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM));

                }
            }else
                //android 'O' changes
            if (Build.VERSION.SDK_INT >= 23) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + clockOutTime,
                        pendingIntent);
            } else if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + clockOutTime,
                        pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + clockOutTime,
                        pendingIntent);
            }
            SharedPref.setTimeOutEnabled(true, mContext);
        }
    }

    /**
     * cancels the alarm for inactivity timeout
     *
     * @param mContext
     */
    public static void cancelAlarmForTimeOut(Context mContext) {
        Intent alarmIntent = new Intent(mContext, ClockInTimeOutReceiver.class);

        PendingIntent pendingIntent;
        //Behaviour changes supporting android 12
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // Create a PendingIntent using FLAG_IMMUTABLE
            pendingIntent = PendingIntent.getBroadcast(mContext,
                    1001, alarmIntent,  PendingIntent.FLAG_IMMUTABLE);
        } else {

            pendingIntent = PendingIntent.getBroadcast(mContext,
                    1001, alarmIntent, 0);
        }
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,
//                1001, alarmIntent, 0);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        SharedPref.setTimeOutEnabled(false, mContext);
    }

    /**
     * saves the last clocked in time
     *
     * @param mContext
     */
    public static void saveLastClockedInTime(Context mContext) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Calendar cal = Calendar.getInstance();
        String currentDate = sdf.format(cal.getTime());
        if (currentDate != null && mContext != null) {
            SharedPref.setClockedInTime(currentDate, mContext);
        }
    }
}
