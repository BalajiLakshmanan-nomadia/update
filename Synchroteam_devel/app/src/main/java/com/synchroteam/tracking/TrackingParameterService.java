package com.synchroteam.tracking;

import static android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.synchroteam.beans.UpdateTrackingIndicator;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.synchroteam.SyncoteamNavigationActivity;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DaoTrackingManager;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.SharedPref;
import com.synchroteam.utils.SynchroteamUitls;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc

/**
 * This class used to get the location of the user for the distance which given
 * by the user as frequency.
 *
 * @author Trident.
 */
public class TrackingParameterService extends Service {

    /**
     * The dao.
     */
    private DaoTracking dao;

    /**
     * The dao2.
     */
    private Dao dao2;

    /**
     * The compteur sync.
     */
    private int compteurSync = 0;

    /**
     * The old tp.
     */
    TrackPoint oldTp = null;

    /**
     * The timer.
     */
    private Timer timer = new Timer();


    private static final String TAG = TrackingParameterService.class.getSimpleName();


    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            10000;

    /**
     * The name of the channel for notifications.
     */
    private static final String CHANNEL_ID = "Tracking Parameter";

    /**
     * The identifier for the notification displayed for the foreground service.
     */
    private static final int NOTIFICATION_ID = 1003;

    /**
     * Used to check whether the bound activity has really gone away and not unbound as part of an
     * orientation change. We create a foreground service notification only if the former takes
     * place.
     */
    private boolean mChangingConfiguration = false;


    private NotificationManager mNotificationManager;

    /**
     * Location request object.
     */
    private LocationRequest mLocationRequest;

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Callback for changes in location.
     */
    private LocationCallback mLocationCallback;


    private Handler mServiceHandler;

    // Binder given to clients
    private final IBinder mBinder = new TrackingParamBinder();


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if (intent == null) {
            startServiceTracking();
            return START_STICKY;
        } else {
            boolean startedFromPI = intent.getBooleanExtra("from_pending_intent",
                    false);

            // We got here from the pending intent.
            if (startedFromPI && !isServiceRunning(this)) {
                Logger.log(TAG, "Service started");
                startServiceTracking();
            }
            return START_STICKY;
        }

//        else if (!isServiceRunning(this)) {
//            startServiceTracking();
//        }

//        return START_NOT_STICKY;

    }


    @Override
    public IBinder onBind(Intent intent) {

        Logger.log(TAG, "in onBind()");
        if (!mChangingConfiguration && SharedPref.getIsTrackcingRunning(this))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                stopForeground(true);
            }

        checkTrackAndStart();

//        else {
//            stopForeground(true);
//        }
        mChangingConfiguration = false;
        return mBinder;
    }

    private void checkTrackAndStart() {
        if (!mChangingConfiguration && SharedPref.getIsTrackcingRunning(this)) {
            if (isServiceRunning(this)) {
                Logger.log(TAG, "PARAM_TRACKING Service is running");

                //new changes
                serviceLogic();

            } else {
                Logger.log(TAG, "PARAM_TRACKING Service is not running");
                Logger.log(TAG, "PARAM_TRACKING Start service again");
                startServiceTracking();
            }
        }
    }

    @Override
    public void onRebind(Intent intent) {

        Logger.log(TAG, "in onRebind()");

        if (!mChangingConfiguration && SharedPref.getIsTrackcingRunning(this))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                stopForeground(true);
            }

        checkTrackAndStart();

        mChangingConfiguration = false;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Logger.log(TAG, "Last client unbound from service");

        if (!mChangingConfiguration && SharedPref.getIsTrackcingRunning(this))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    startForeground(NOTIFICATION_ID, getNotification(), FOREGROUND_SERVICE_TYPE_LOCATION);
                }
            }


        checkTrackAndStart();
        return true; // Ensures onRebind() is called when a client re-binds.
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Service#onCreate()
     */
    public void onCreate() {
        super.onCreate();
        Logger.log(TAG, "onCreate");

        dao = DaoTrackingManager.getInstance();
        dao2 = DaoManager.getInstance();
        isTrackingActive();
//        startServiceTracking();

//        //changes for android 'O'
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            showNotification();
//        } else {
//            showNotification();
//        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForeground(NOTIFICATION_ID, getNotification());
//        }

    }

    private void isTrackingActive() {
        if (dao.getSessiondata("tracking").equals("on")) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (SharedPref.getIsTrackcingRunning(this)) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            startForeground(NOTIFICATION_ID, getNotification(),FOREGROUND_SERVICE_TYPE_LOCATION);
                        }
                    }
                    checkTrackAndStart();
                } else {
                    startServiceTracking();
                }

            } else {
                Logger.log(TAG, "Runtime permission disabled");
            }
        }
    }


    @Override
    public void onDestroy() {
//        mServiceHandler.removeCallbacksAndMessages(null);
    }

    private void showNotification() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, getNotification(),FOREGROUND_SERVICE_TYPE_LOCATION);
        }
    }

    /**
     * Returns the {@link NotificationCompat} used as part of the foreground service.
     */
    private Notification getNotification() {

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);

            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }

        String stop = "stop";

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, SyncoteamNavigationActivity.class);
        intent.putExtra(KEYS.Notification.KEY_FROM_TRACKING, true);
        intent.putExtra(KEYS.Notification.KEY_FROM_TRAVEL, false);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent;

        //Behaviour changes supporting android 12
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // Create a PendingIntent using FLAG_IMMUTABLE
            pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_IMMUTABLE);
        } else {

            pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
//                PendingIntent.FLAG_UPDATE_CURRENT);


        PendingIntent servicePendingIntent;

        //Behaviour changes supporting android 12
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // Create a PendingIntent using FLAG_IMMUTABLE
            servicePendingIntent = PendingIntent.getBroadcast(
                    this, 1, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
        } else {

            servicePendingIntent = PendingIntent.getBroadcast(
                    this, 1, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);

        }

//        PendingIntent servicePendingIntent = PendingIntent.getBroadcast(
//                this, 1, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);

        // Create the persistent notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "TrackingFrequency")
                .setContentTitle(getString(R.string.notification_text_tracking))
                .setContentText(getString(R.string.notification_sub_text_tracking))
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.icon_notif);
//                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon_notif));

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);


        // Set the Channel ID for Android O.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID); // Channel ID
        }

        return builder.build();
    }


    private void removeNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true);
        }
//        else {
//            stopForeground(true);
//        }
    }

    /**
     * Startservice.
     */
    public void startServiceTracking() {
        dao = DaoTrackingManager.getInstance();
        dao2 = DaoManager.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return; //TODO: start worker when tracking 2.0 for Android 12+
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(new Intent(getApplicationContext(),
                    TrackingParameterService.class));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(NOTIFICATION_ID, getNotification(),FOREGROUND_SERVICE_TYPE_LOCATION);
            }
        } else {
            startService(new Intent(getApplicationContext(), TrackingParameterService.class));
        }
        serviceLogic();

    }

    /**
     * Stopservice.
     */
    @SuppressWarnings("static-access")
    public void stopService() {

        SharedPref.setIsTrackingRunning(false, this);
        EventBus.getDefault().post(new UpdateTrackingIndicator());
//        EventBus.getDefault().post(new TrackingSwitchEventSaveNew(false));
        removeNotification();
        removeLocationUpdates();

    }

    /**
     * Save location.
     *
     * @param location the location
     */
    public void saveLocation(Location location) {

        Double latitude = location.getLatitude();
        Double longitude = location.getLongitude();
        Logger.log(TAG, "<<<<<Location points got from Tracking Service Frequency class >>>>" + latitude + " , " + longitude);
        TrackPoint newTp = new TrackPoint(location, new Date().getTime());
        String vitesse;

        if (location.hasSpeed()) {
            NumberFormat format = DecimalFormat.getInstance();
            format.setMinimumFractionDigits(0);
            format.setMaximumFractionDigits(4);
            format.setGroupingUsed(false);
            vitesse = format.format(location.getSpeed());
            SynchroteamUitls.logInFile("Has Speed " + vitesse);
        } else {
            vitesse = "0.0";
            SynchroteamUitls.logInFile("no Speed " + vitesse);
        }

        boolean valuesSaved = dao.saveCoordinate(dao2.getUser().getId(), vitesse, latitude, longitude);
        Logger.log(TAG, "Location points while saving to Database result=======>" + valuesSaved);
        oldTp = newTp;

    }

    /**
     * Sets the location request parameters.
     */
    private void createLocationRequest() {
        String f = dao.getSessiondata("frequence");

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
//        mLocationRequest.setSmallestDisplacement(10);
        mLocationRequest.setMaxWaitTime(UPDATE_INTERVAL_IN_MILLISECONDS * 5);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (f != null && f.length() > 0) {
            mLocationRequest.setSmallestDisplacement(Integer.valueOf(f));
        } else {
            mLocationRequest.setSmallestDisplacement(0);
        }

    }


    private void initializingForLocation() {

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Log.e(TAG, "location callback");
                saveLocation(locationResult.getLastLocation());
            }

        };

        createLocationRequest();

        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mServiceHandler = new Handler(handlerThread.getLooper());
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Android O requires a Notification Channel.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.app_name);

            // Create the channel for the notification
            NotificationChannel mChannel =
                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);

            // Set the Notification Channel for the Notification Manager.
            mNotificationManager.createNotificationChannel(mChannel);
        }

    }


    private void getLastLocation() {
        try {
            mFusedLocationClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                Log.e(TAG, "Get last location");
                                saveLocation(task.getResult());
                            } else {
                                Log.w(TAG, "Failed to get location.");
                            }
                        }
                    });

        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission." + unlikely);
        }
    }


    /**
     * Makes a request for location updates.
     */
    public void requestLocationUpdates() {
        Logger.log(TAG, "Requesting location updates");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        } else {

            try {
                mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                        mLocationCallback, Looper.getMainLooper());
            } catch (SecurityException unlikely) {
                Log.e(TAG, "Lost location permission. Could not request updates. " + unlikely);
            }
        }

    }

    /**
     * Removes location updates.
     */
    public void removeLocationUpdates() {
        Logger.log(TAG, "Removing location updates");
        try {
            if (mFusedLocationClient != null)
                mFusedLocationClient.removeLocationUpdates(mLocationCallback);
            if (timer != null) {
                timer.cancel();
            }
            stopSelf();
        } catch (SecurityException unlikely) {
            Log.e(TAG, "Lost location permission. Could not remove updates. " + unlikely);
        }


    }

    /**
     * Returns true if this is a foreground service.
     *
     * @param context The {@link Context}.
     */
    public boolean serviceIsRunningInForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    return true;
                }
            }
        }
        return false;


    }

    public boolean isServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (getClass().getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @SuppressLint("MissingPermission")
    private void serviceLogic() {
        // TODO Auto-generated method stub

        final String p = dao.getSessiondata("periode");

        Calendar cal = Calendar.getInstance();
        int d = cal.get(Calendar.DAY_OF_WEEK);
        String jours = dao.getSessiondata("jours");
        String[] tabJours = new String[7];
        tabJours = jours.split("-");

        String h2 = dao.getSessiondata("heur2");
        String hh2 = "";
        String mm2 = "";
        if (h2 != null && h2.length() >= 5) {
            hh2 = h2.substring(0, 2);
            mm2 = h2.substring(3, 5);
        }

        String startTime = dao.getSessiondata("heur1");
        String startHour = "";
        String startMinute = "";
        if (startTime != null && startTime.length() >= 5) {
            startHour = startTime.substring(0, 2);
            startMinute = startTime.substring(3, 5);
        }
        @SuppressWarnings("deprecation") final Date startDate = new Date(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),
                Integer.valueOf(startHour), Integer.valueOf(startMinute));

        @SuppressWarnings("deprecation") final Date endDate = new Date(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),
                Integer.valueOf(hh2), Integer.valueOf(mm2));
        int he = cal.get(Calendar.HOUR_OF_DAY);
        int me = cal.get(Calendar.MINUTE);
        @SuppressWarnings("deprecation")
        Date currentD = new Date(cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), he, me);

        if ((Integer.valueOf(tabJours[0]) == d
                || Integer.valueOf(tabJours[1]) == d
                || Integer.valueOf(tabJours[2]) == d
                || Integer.valueOf(tabJours[3]) == d
                || Integer.valueOf(tabJours[4]) == d
                || Integer.valueOf(tabJours[5]) == d || Integer
                .valueOf(tabJours[6]) == d)
                && (currentD.before(endDate))
                && ((currentD.after(startDate)) || (currentD.equals(startDate)))) {

            dao.setSessiondata("tracking", "on");

            initializingForLocation();

            requestLocationUpdates();

            dao.saveActiviteTracking(dao2.getUser().getId(), "START");

            //cancel old timer and initiate new one
            if (timer != null) {
                timer.cancel();
            }
            timer = new Timer();
            timerForSyncDB(endDate, startDate, p);

            SharedPref.setIsTrackingRunning(true, this);
            EventBus.getDefault().post(new UpdateTrackingIndicator());

        } else {
//            stopSelf();
            stopService();
        }
    }

    private void timerForSyncDB(final Date endDate, final Date startDate, final String p) {

        if (timer != null) {
            timer.scheduleAtFixedRate(new TimerTask() {

                @SuppressWarnings("deprecation")
                public void run() {

                    Calendar calendar = Calendar.getInstance();
                    int h = calendar.get(Calendar.HOUR_OF_DAY);
                    int m = calendar.get(Calendar.MINUTE);
                    Date currentDate = new Date(calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH), calendar
                            .get(Calendar.DAY_OF_MONTH), h, m);

                    if ((currentDate.before(endDate))
                            && ((currentDate.after(startDate)) || (currentDate
                            .equals(startDate)))) {
                        if (SharedPref.getIsTrackcingRunning(TrackingParameterService.this)) {
                            if (compteurSync >= Integer.valueOf(p)) {

                                getLastLocation();

                                syncDatabase();
                                compteurSync = 0;
                            }
                            compteurSync++;
                            Logger.log("Inner Process", "" + compteurSync);
                        } else {
                            if (timer != null)
                                timer.cancel();
                            dao.saveActiviteTracking(dao2.getUser().getId(), "STOP");
                            stopService();
                        }

                    } else {
                        dao.saveActiviteTracking(dao2.getUser().getId(), "STOP");
                        stopService();
                    }
                }

            }, 0, 60 * 1000);
        }

    }

    private void syncDatabase() {

        final User u = dao2.getUser();
        try {
            if (SynchroteamUitls
                    .isNetworkAvailable(TrackingParameterService.this)) {
                Logger.log(TAG, "Location points syncing to Database =======>");

                boolean syncResult = dao.sync(u.getLogin(), u.getPwd(),
                        dao2.getUserDomain(), Dao.script);

                Logger.log(TAG, "Location points while syncing to Database result=======>" + syncResult);

                SynchroteamUitls
                        .setDiasableNetworkBroadcastReciver(TrackingParameterService.this);

            } else {
                SynchroteamUitls
                        .setEnableNetworkBroadcastReciver(TrackingParameterService.this);

            }

        } catch (Exception e1) {
            Logger.printException(e1);
            SynchroteamUitls
                    .logInFile("Sync failed time due to no Network "
                            + " Sync failed due to Ultarlite db error: "
                            + e1.getMessage());

        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        if (SharedPref.getIsTrackcingRunning(this)) {
            Intent restartService = new Intent(getApplicationContext(),
                    this.getClass());
            restartService.setPackage(getPackageName());

            PendingIntent restartServicePI;

            //Behaviour changes supporting android 12
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                // Create a PendingIntent using FLAG_IMMUTABLE
                restartServicePI = PendingIntent.getService(
                        getApplicationContext(), 1, restartService,
                        PendingIntent.FLAG_ONE_SHOT|
                PendingIntent.FLAG_IMMUTABLE);
            } else {

                restartServicePI = PendingIntent.getService(
                        getApplicationContext(), 1, restartService,
                        PendingIntent.FLAG_ONE_SHOT);
            }

//            PendingIntent restartServicePI = PendingIntent.getService(
//                    getApplicationContext(), 1, restartService,
//                    PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            alarmService.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 1000, restartServicePI);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class TrackingParamBinder extends Binder {
        public TrackingParameterService getService() {
            // Return this instance of LocalService so clients can call public methods
            return TrackingParameterService.this;
        }
    }

}