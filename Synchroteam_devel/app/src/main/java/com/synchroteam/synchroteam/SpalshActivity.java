package com.synchroteam.synchroteam;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.synchroteam.retrofit.models.mobileAuth.AuthData;
import com.synchroteam.tracking.TrackingParameterService;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.tracking.DaoTracking;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DaoTrackingManager;
import com.synchroteam.utils.DateFormatUtils;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.SharedPref;
import com.synchroteam.utils.SynchroteamUitls;

import java.io.File;

//import android.os.Environment;

/**
 * This Activity is Responsible for Inflating on this Splash Screen.
 *
 * @author Ideavate.solution
 */
public class SpalshActivity extends AppCompatActivity implements CommonInterface {

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    // code of previous developer
//    /**
//     * The progress db.
//     */
//    ProgressDialog progressDB;

    /**
     * The data access object.
     */
    private Dao dataAccessObject;

    /**
     * The data acess object tracking.
     */
    private DaoTracking dataAcessObjectTracking;

    /**
     * The pi.
     */
    private PendingIntent pi, pi1, pITrackParams;


    // private static final String TAG = "SpalshActivity";

    // code of previous developer

    // Declare the launcher at the top of your Activity/Fragment:
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // FCM SDK (and your app) can post notifications.
                    Logger.log("NOTIFICATION","POST NOTIFICATION IS GRANTED");
                    pITrackParams = initializePendingIntent();

                    splashScreen();
                } else {
                    // TODO: Inform user that that your app will not show notifications.
                    Toast.makeText(this,"Notification permission is denied",Toast.LENGTH_SHORT).show();
                    pITrackParams = initializePendingIntent();

                    splashScreen();
                }
            });

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_splash);
        SynchroteamUitls.setDiasableNetworkBroadcastReciver(this);
        askNotificationPermission();// post notification permission

//        pi = PendingIntent.getService(this, 0, new Intent(this,
//                TrackingService.class), PendingIntent.FLAG_UPDATE_CURRENT);
//        pi1 = PendingIntent.getService(this, 0, new Intent(this,
//                        TrackingServiceFrequency.class),
//                PendingIntent.FLAG_UPDATE_CURRENT);



        // File file;
        // try {
        // file = new File(Environment.getExternalStorageDirectory(),
        // "SynchroteanLogs.txt");
        //
        // if (!file.exists())
        // file.createNewFile();
        // Logger.log("Path of the file :  ", "" + file.getAbsolutePath());
        //
        // } catch (IOException e) {
        // e.printStackTrace();
        // }

    }

    private void askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                    PackageManager.PERMISSION_GRANTED) {
                Logger.log("NOTIFICATION","POST NOTIFICATION IS GRANTED");
                pITrackParams = initializePendingIntent();

                splashScreen();

            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("Alert");
                alertBuilder.setMessage(getString(R.string.notification_permission));
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        pITrackParams = initializePendingIntent();

                        splashScreen();
                    }
                });
                alertBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }else {
            pITrackParams = initializePendingIntent();

            splashScreen();
        }
    }



    /**
     * Intializin the pending intent for the tracking service
     *
     * @return Pending intent
     */
    private PendingIntent initializePendingIntent() {
        Intent trackingParamsIntent = new Intent(SpalshActivity.this,
                TrackingParameterService.class);
        trackingParamsIntent.putExtra("from_pending_intent", true);

        PendingIntent pendingIntent;
        //Behaviour changes supporting android 12
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // Create a PendingIntent using FLAG_IMMUTABLE
            pendingIntent= PendingIntent.getService(SpalshActivity.this,
                    0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT|
                            PendingIntent.FLAG_IMMUTABLE);
        } else {

            pendingIntent= PendingIntent.getService(SpalshActivity.this,
                    0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return pendingIntent;

//        return PendingIntent.getService(SpalshActivity.this,
//                0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    }


    /**
     * For canceling the Tracking service when conditions are met
     */
    private void cancelTrackingAlarm() {
        stopService(new Intent(SpalshActivity.this, TrackingParameterService.class));
        AlarmManager am2 = (AlarmManager) SpalshActivity.this
                .getSystemService(Context.ALARM_SERVICE);
        am2.cancel(pITrackParams);
    }

    /**
     * show Splash screen.
     */
    public void splashScreen() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        Logger.log("Device Density", "" + metrics.density);

        try {
            linkToDatabase();
        } catch (Exception e) {
            dataAccessObject = null;
            dataAcessObjectTracking = null;

            Logger.printException(e);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        try {
//            if (!isFinishing() && progressDB != null && progressDB.isShowing()) {
//                progressDB.dismiss();
//            }
//        } catch (Exception e) {
//
//        } finally {
//            progressDB = null;
//        }
    }

    /**
     * connect to to database.
     * <p/>
     * previous developer code
     */
    protected void linkToDatabase() {

        dataAccessObject = new Dao();
        dataAcessObjectTracking = new DaoTracking();
        new LinkAsyncTask(this).execute();

//        final ProgressDialog[] progressDB = new ProgressDialog[1];
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if (!isFinishing()) {
//                    progressDB[0] = ProgressDialog.show(getApplicationContext(),
//                            getString(R.string.textPleaseWaitLable),
//                            getString(R.string.msg_chargement), true, false);
//                }
//
//            }
//        });


//        Thread linkToServer = new Thread((new Runnable() {
//            @Override
//            public void run() {
//
//                if (dataAccessObject != null) {
//                    dataAccessObject.connectDatabase(SpalshActivity.this);
//                    dataAcessObjectTracking.connectDatabase(SpalshActivity.this);
////                dataAccessObject.removeInterventionFK();
//
//                    Message myMessage = new Message();
//                    myMessage.obj = true;
//
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
////                            try {
////                                if (!isFinishing() && progressDB[0] != null && progressDB[0].isShowing()) {
////                                    progressDB[0].dismiss();
////                                }
////                            } catch (Exception e) {
////
////                            } finally {
////
////                            }
//                        }
//                    });
//                    handler.sendMessage(myMessage);
//                }
//            }
//        }));
//        linkToServer.start();

    }

    /**
     * The handler.
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            boolean etat = (Boolean) msg.obj;
            if (etat) {
                enterApp();
            }
        }
    };

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Entrance Point of app decides whether to show login screen or app
     * dashboard.
     * <p/>
     * code of perevious developer
     */
    protected void enterApp() {

        //if the user updated from script version 41 to 42,
        // instead of deleting all datas, alter the table to prevent the long time sync for first time.

        if (dataAccessObject.getUserScript().equals(Dao.script) ||
                SharedPref.getScriptUpdated(this)) {

            User user = dataAccessObject.getUser();

            if (!dataAccessObject.checkAuthTokenLogin() && user != null) {
                AuthData getAuthandExpriryToken = dataAccessObject.getUserToken();
                if (getAuthandExpriryToken != null && !TextUtils.isEmpty(user.getPwd())) {

                    boolean isExpiry = DateFormatUtils.getFormattedDateFromAPIDateBase(getAuthandExpriryToken.getExpiry());
                    if (isExpiry) {
                        Intent i;
                        closeDb();

                        // if(domain.contains("dalkia"))
                        // i = new Intent(SpalshActivity.this, Login_Syncroteam.class);
                        // else
                        SharedPref.setIsNotLoggedInToday(false, SpalshActivity.this);
                        i = new Intent(SpalshActivity.this,
                                SyncoteamNavigationActivity.class);
                        // i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(i);

                    } else {
                        Intent i = new Intent(SpalshActivity.this,
                                Login_Syncroteam.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                    }

                } else {
                    Intent i = new Intent(SpalshActivity.this,
                            Login_Syncroteam.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
//                if (user.getPwd().equals("")) {
//                    Intent i = new Intent(SpalshActivity.this,
//                            Login_Syncroteam.class);
//                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(i);
//                } else {
//                    Intent i;
//                    closeDb();
//
//                    // if(domain.contains("dalkia"))
//                    // i = new Intent(SpalshActivity.this, Login_Syncroteam.class);
//                    // else
//                    SharedPref.setIsNotLoggedInToday(false, SpalshActivity.this);
//                    i = new Intent(SpalshActivity.this,
//                            SyncoteamNavigationActivity.class);
//                    // i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                    startActivity(i);
//                }
            } else {

                if (user != null)
                    dataAccessObject.clearPassword(user.getLogin());

                closeDb();

                //cancelling Tracking service
                cancelTrackingAlarm();

                SharedPref.setIsNotLoggedInToday(true, SpalshActivity.this);
                SharedPref.setDebugEnableSync(false, SpalshActivity.this);


                Intent i = new Intent(SpalshActivity.this,
                        Login_Syncroteam.class);
                // i.putExtra(KEYS.JObDetail.IS_LOGGED_IN_TODAY, false);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        } else {
            try {

                stopTracking();
                stopAutoSync();
                deleteDbTables();

                closeDb();

                clearApplicationData();
                Log.e("TAG", "- Database Deleted -");
                Intent i = new Intent(SpalshActivity.this,
                        Login_Syncroteam.class);
                /** ......................New Changes............. */
                /* To restart the app after wiping all the data */

                int mPendingIntentId = 123456;


                PendingIntent mPendingIntent;

                //Behaviour changes supporting android 12
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    // Create a PendingIntent using FLAG_IMMUTABLE
                    mPendingIntent = PendingIntent.getActivity(this,
                            mPendingIntentId, i, PendingIntent.FLAG_CANCEL_CURRENT
                                    |PendingIntent.FLAG_IMMUTABLE);
                } else {

                    mPendingIntent = PendingIntent.getActivity(this,
                            mPendingIntentId, i, PendingIntent.FLAG_CANCEL_CURRENT);
                }

//                PendingIntent mPendingIntent = PendingIntent.getActivity(this,
//                        mPendingIntentId, i, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager) this
                        .getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100,
                        mPendingIntent);
                System.exit(0);
                /** ......................New Changes............. */

                // i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                // startActivity(i);
            } catch (Exception e) {
                Logger.printException(e);
            }

        }

        SpalshActivity.this.finish();

    }


    private void stopAutoSync() {
        AlarmManager alarmManager = (AlarmManager) this
                .getSystemService(Context.ALARM_SERVICE);


        PendingIntent operation;
        //Behaviour changes supporting android 12
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // Create a PendingIntent using FLAG_IMMUTABLE
            operation = PendingIntent.getBroadcast(
                    SpalshActivity.this, 0,
                    ((SyncroTeamApplication) getApplication()).getAutoSyncIntent(),
                    PendingIntent.FLAG_UPDATE_CURRENT|  PendingIntent.FLAG_IMMUTABLE);
        } else {

            operation = PendingIntent.getBroadcast(
                    SpalshActivity.this, 0,
                    ((SyncroTeamApplication) getApplication()).getAutoSyncIntent(),
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

//        PendingIntent operation = PendingIntent.getBroadcast(
//                SpalshActivity.this, 0,
//                ((SyncroTeamApplication) getApplication()).getAutoSyncIntent(),
//                PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(operation);

    }

    /**
     * Stop tracking.
     */
    public void stopTracking() {
        Logger.log("Splash Activity", "Stop tracking is called");

        dataAcessObjectTracking.setSessiondata("tracking", "off");
        // ---------------------------------------------------------
        SharedPref.setPreviousValueOfTracking(SpalshActivity.this, false);
        SharedPref.setIsTrackingRunning(false, SpalshActivity.this);

//        stopService(new Intent(this, TrackingService.class));
//        AlarmManager am = (AlarmManager) this
//                .getSystemService(Context.ALARM_SERVICE);
//        am.cancel(pi);
//
//        stopService(new Intent(this, TrackingServiceFrequency.class));
//        AlarmManager am1 = (AlarmManager) this
//                .getSystemService(Context.ALARM_SERVICE);
//        am1.cancel(pi1);

        cancelTrackingAlarm();
    }

    /**
     * Clear application data. Previous Developer Code
     */
    private void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());

        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));
                    Log.i("TAG",
                            "**************** File /data/data/APP_PACKAGE/" + s
                                    + " DELETED *******************");
                }
            }
        }
    }

    /**
     * Delete all app directories.
     * <p/>
     * Previous Developer Code
     *
     * @param file the file
     * @return true, if successful
     */
    private boolean deleteDir(File file) {
        if (file != null && file.isDirectory()) {
            String[] children = file.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(file, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return file.delete();
    }

    /**
     * Close database and reset the Data Access Object.
     */
    private void closeDb() {

        dataAccessObject.closeDatabase();
        dataAcessObjectTracking.closeDatabase();

        DaoManager.resetDao();
        DaoTrackingManager.resetDaoTracking();

        dataAcessObjectTracking = null;
        dataAccessObject = null;
    }

    private void deleteDbTables() {

        dataAccessObject.deleteAllTables(this);
        dataAcessObjectTracking.deleteTrackingTables(this);

    }

    /*
     * (non-Javadoc)
     *
     * @see com.synchroteam.synchroteam.CommonInterface#getActivityInstance()
     */
    @Override
    public Activity getActivityInstance() {
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onResume()
     */
    protected void onResume() {
        super.onResume();
        ((SyncroTeamApplication) getApplicationContext())
                .setSyncroteamAppLive(true);
        ((SyncroTeamApplication) getApplicationContext())
                .setSyncroteamActivityInstance(this);

    }


    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        ((SyncroTeamApplication) getApplicationContext())
                .setSyncroteamAppLive(false);
        ((SyncroTeamApplication) getApplicationContext())
                .setSyncroteamActivityInstance(null);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.synchroteam.synchroteam.CommonInterface#updateUi()
     */
    @Override
    public void updateUi() {

    }

    @Override
    public void updateUiOnTrakingStatusChange(boolean isRunning) {

    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onBackPressed()
     */
    @Override
    public void onBackPressed() {

//        try {
//            if (!isFinishing() && progressDB != null && progressDB.isShowing()) {
//                progressDB.dismiss();
//            }
//        } catch (Exception e) {
//
//        } finally {
//            progressDB = null;
//        }

        super.onBackPressed();
    }

    // /* (non-Javadoc)
    // * @see android.app.Activity#onBackPressed()
    // */
    // public void onBackPressed() {
    // super.onBackPressed();
    // dataAccessObject.closeDatabase();
    // dataAcessObjectTracking.closeDatabase();
    // dataAcessObjectTracking = null;
    // dataAccessObject = null;
    // };

    /**
     * The Class for linking to the database.
     */
    private class LinkAsyncTask extends
            AsyncTaskCoroutine<String, Boolean> {

        Context context;


        public LinkAsyncTask(Context context) {
            this.context = context;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        public void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();


            DialogUtils.showProgressDialog((Activity) context,
                    context.getString(R.string.textWaitLable),
                    context.getString(R.string.msg_chargement),
                    false);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])sc
         */
        @Override
        public Boolean doInBackground(String... params) {

            dataAccessObject.connectDatabase(SpalshActivity.this);
            dataAcessObjectTracking.connectDatabase(SpalshActivity.this);

            return true;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        public void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            DialogUtils.dismissProgressDialog();
            if (result) {
                enterApp();
            }

        }

    }
}
