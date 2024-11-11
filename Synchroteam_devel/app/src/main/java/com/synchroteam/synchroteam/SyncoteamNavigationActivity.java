package com.synchroteam.synchroteam;

import static android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.synchroteam.JobDispatcher.ScheduledJobService;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.baseactivity.SyncroTeamBaseActivity;
import com.synchroteam.beans.Conge;
import com.synchroteam.beans.EnableSynchronizationAddJobEvent;
import com.synchroteam.beans.Families;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.beans.Item;
import com.synchroteam.beans.JobDetailsModel;
import com.synchroteam.beans.LocationPoints;
import com.synchroteam.beans.SelectTodayDate;
import com.synchroteam.beans.TravelActivity;
import com.synchroteam.beans.UpdateDataBaseEvent;
import com.synchroteam.beans.UpdateJobDetailUi;
import com.synchroteam.beans.UpdateTrackingIndicator;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.ActivityModeDialog;
import com.synchroteam.dialogs.AppUpdateDialog;
import com.synchroteam.dialogs.AuthenticationErrorDialog;
import com.synchroteam.dialogs.ClockInTimeOutDialog;
import com.synchroteam.dialogs.ClockJobTravelActDialog;
import com.synchroteam.dialogs.DrivingModeDialogUpdated;
import com.synchroteam.dialogs.ErrorDialog;
import com.synchroteam.dialogs.MultipleDeviceNotSupported;
import com.synchroteam.dialogs.StartActivityDialog;
import com.synchroteam.dialogs.StartJobActivityDialog;
import com.synchroteam.dialogs.SynchronizationErrorDialog;
import com.synchroteam.events.ActivityUpdateEvent;
import com.synchroteam.events.ClockInTimeOutEvent;
import com.synchroteam.events.ClockedOutEvent;
import com.synchroteam.events.DrivingModeUpdateEvent;
import com.synchroteam.events.JobPauseFinishEvent;
import com.synchroteam.events.JobTypeDialogEvent;
import com.synchroteam.events.RefreshListEvent;
import com.synchroteam.events.TravelStopEvent;
import com.synchroteam.evernote.NotificationSyncJob;
import com.synchroteam.evernote.TrackSyncJob;
import com.synchroteam.fragment.AllJobPoolFragment;
import com.synchroteam.fragment.AllJobsFragment;
import com.synchroteam.fragment.AttachmentListFragment;
import com.synchroteam.fragment.BaseFragment;
import com.synchroteam.fragment.BaseReportFragment;
import com.synchroteam.fragment.CalendarFragment;
import com.synchroteam.fragment.ClientSectionFragment;
import com.synchroteam.fragment.CurrentJobFragment;
import com.synchroteam.fragment.DeadlineExceededFragment;
import com.synchroteam.fragment.InventoryFragment;
import com.synchroteam.fragment.LastSyncFragment;
import com.synchroteam.fragment.LegalInformationFragment;
import com.synchroteam.fragment.MessagesFragment;
import com.synchroteam.fragment.ToComeJobFragment;
import com.synchroteam.fragment.UnavabilitiesListingFragment;
import com.synchroteam.observers.ObservableObject;
import com.synchroteam.retrofit.ApiClientNew;
import com.synchroteam.retrofit.ApiInterface;
import com.synchroteam.retrofit.Apiclient;
import com.synchroteam.retrofit.models.NotificationService.EventNotiRequest;
import com.synchroteam.retrofit.models.NotificationService.EventNotiResult;
import com.synchroteam.retrofit.models.NotificationService.jsonInfo;
import com.synchroteam.retrofit.models.syncservice.SyncService;
import com.synchroteam.roomDB.RoomDBSingleTone;
import com.synchroteam.roomDB.entity.NotificationEventModels;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.technicalsupport.JobDetails;
import com.synchroteam.tracking.DaoTracking;
import com.synchroteam.tracking.TrackingParameterService;
import com.synchroteam.tracking2.LocationService;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.ClockInOutUtil;
import com.synchroteam.utils.CommonUtils;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DaoTrackingManager;
import com.synchroteam.utils.DateChecker;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.LocationUtils;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.SharedPref;
import com.synchroteam.utils.SynchroteamUitls;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.TimeZone;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * The Class SyncoteamNavigationActivity extends SyncroteamBaseActivity this
 * class is responsible to inflate Navigation Drawer and Ui and perform Actions
 * on Navigation Screen.
 *
 * @author Ideavate.solution
 */
public class SyncoteamNavigationActivity extends SyncroTeamBaseActivity implements Observer {


    //test commit
    
    /**
     * The drawer layout.
     */
    private DrawerLayout drawerLayout;

    /**
     * The drawer close and open.
     */
    private ImageButton drawerCloseAndOpen;

    /**
     * The reports.
     */
    private RelativeLayout currentJobs, toComeJobs, deadLineExceededJobs,
            messages, reports, allJobs, legalInformation, logout, calendarListItem,
            unavabilitiesListItem, clientListItem, inventoryListItem, jobPool;

    private LinearLayout trackingListItem;

    private TextView txtTrackingLabelTv;

    /**
     * The seleted_fragment_position.
     */
    private int seleted_fragment_position = 0;

    /**
     * The list item layout.
     */
    private RelativeLayout listItemLayout;

    /**
     * The title screen tv.
     */
    private TextView titleScreenTv;

    /**
     * The last syncronization settings iv.
     */
    private LinearLayout lastSyncronizationSettingsLl;

    /**
     * The syncronization icon iv.
     */
    private ImageView syncronizationIconIv, addJobBtn;

    /**
     * The last list item selected id.
     */
    private int lastListItemSelectedId;

    /**
     * The data access object.
     */
    private Dao dataAccessObject;

    /**
     * The progress d synch.
     */
    private ProgressDialog progressDSynch;

    /**
     * The alarm manager.
     */
    private AlarmManager alarmManager;

    /**
     * The user.
     */
    private User user;

    /**
     * The flag.
     */
    private int flag;
    /**
     * The current job count.
     */
    private TextView dedlineExceededCount, toComeCount, currentJobCount,
            messagesCount, reportsCount, allJobCount, unavabilityCount, jobPoolCount;

    /**
     * The tracking on off tb.
     */
    private RadioButton trackingOnOffTb;

    /**
     * The dao tracking.
     */
    private DaoTracking daoTracking;

    /**
     * The pi.
     */
    private PendingIntent pi, pi1, pITrackParams;

    /**
     * The date last syncronization tv.
     */
    private TextView dateLastSyncronizationTv;

    /**
     * The syncro team application.
     */
    private SyncroTeamApplication syncroTeamApplication;

    /**
     * The location manager.
     */
    private LocationManager locationManager;

    /**
     * The tracking indicator iv.
     */
    private ImageView trackingIndicatorIv;

    private RelativeLayout attachmentListItem;
    private NotificationManager mNotificationManager;

    private RelativeLayout relTitle;

    private InventoryFragment inventoryFragment;
    private SharedPreferences pref;

    // private boolean isLoggedInToday;
    private String isDebugEnabled;

    private static final String TAG = "SyncoteamNavigationActivity";

    // v48 new functionality
    private TextView txtClockMode, txtActivityStopView;
    private android.widget.TextView txtStartActivity;
    private RelativeLayout layBottomBar;
    String idIntervention = null;
    int idUser;
    String jobNumber;
    private long mStartBtnClickTime; // preventing double click
    private boolean isActive = false;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 124;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION_TRACKING = 154;

    private boolean executeOnResume = true;

    private ArrayList<LocationPoints> locationList;
    // private Realm realm;

    /**
     * The current date.
     */
    private Date todayDate;

    // A reference to the service used to get Tracking location updates.
    private LocationService mServiceTracking = null;


    // Tracks the bound state of the service.
    private boolean mBoundTracking = false;
    // Tracks the bound state of the service.
    private boolean mBoundTravel = false;
    private int mCountTodayJob, mCountUpComing, mCountLate, mCountReport;



    public final int REQUEST_CHECK_SETTINGS_TRACKING = 20001;

    /**
     * The key.
     */
    private static String KEY = "c2dmPref";

    /**
     * The registration key.
     */
    private static String REGISTRATION_KEY = "registrationKey";
    List<NotificationEventModels> l;


    /*
     * (non-Javadoc)
     *
     * @see
     * com.synchroteam.baseactivity.SyncroTeamBaseActivity#onCreate(android.
     * os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("location", "location", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }


        Logger.log("Synchroteam Navigation", "onCreate Called");

        initialiseParameters();
        initialiseView();
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        todayDate = calendar.getTime();


        lastListItemSelectedId = R.id.currentJobsListItem;
        EventBus.getDefault().post(new SelectTodayDate());

        alarmManager = (AlarmManager) this
                .getSystemService(Context.ALARM_SERVICE);

        onListUpdate();

        mNotificationManager = (NotificationManager) this
                .getSystemService(this.NOTIFICATION_SERVICE);

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                registerToPushServerNew();
            }
        });



        if (SharedPref.getIsSyncOn(this)) {
            setAlarmManagerToAutoSyncroniseData();
        }

        //new changes
        if (checkLinkValuesFromPref()) {
            syncAfterADay();
        } else {
            if (SyncoteamNavigationActivity.this != null &&
                    !SyncoteamNavigationActivity.this.isFinishing())
                showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));
        }


        updatedClockInOutDialog();

        checkNetworkAndGPS();
        onNewIntent(getIntent());


        TravelActivity travelActivity = dataAccessObject.isDrivingStarted();

        if (travelActivity != null) {
            //new changes
            checkAndEnableTravelActivity(travelActivity);
        }


//        try {
//            scheduleJobEvernote();
//        } catch (Exception e) {
//
//        }
//
        try {
            scheduleJob(SyncoteamNavigationActivity.this);
        } catch (Exception e) {

        }

        IntentFilter dataUpdateIntent = new IntentFilter();
        dataUpdateIntent.addAction(Intent.ACTION_DATE_CHANGED);
        registerReceiver(dateUpdateReceiver,dataUpdateIntent, RECEIVER_EXPORTED);
    }


    private class GetRealmDataAsyncTask extends AsyncTaskCoroutine<Void, Boolean> {

        @Override
        public Boolean doInBackground(Void... params) {

            boolean result = false;

            List<NotificationEventModels> results = new ArrayList<>();
            results = RoomDBSingleTone.instance(SyncoteamNavigationActivity.this).roomDao().
                    getAllNotificationEventModels();

            if (results != null && results.size() > 0)
                result = true;
            else
                result = false;
            Log.e("TAG", "NOTIFICATION VALUES ARE ==>" + results.size());

            return result;
        }

        @Override
        public void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {

                Constraints constraints = new Constraints.Builder()
                        // The Worker needs Network connectivity
                       // .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();

                PeriodicWorkRequest request =
                        // Executes MyWorker every 60 seconds
                        new PeriodicWorkRequest.Builder(ScheduledJobService.class, 60, TimeUnit.SECONDS)
                                .setConstraints(constraints)
                                .build();

                WorkManager.getInstance(SyncoteamNavigationActivity.this)
                        .enqueueUniquePeriodicWork("CheckNetUpdateDb", ExistingPeriodicWorkPolicy.REPLACE, request);

            } else {
               Logger.log(TAG, "NOTIFICATION SERVICE OFF ");
           //     WorkManager.getInstance(SyncoteamNavigationActivity.this).cancelUniqueWork("CheckNetUpdateDb");
            }
        }
    }


//    private void scheduleJobEvernote() {
//
//        PeriodicWorkRequest request =
//                // Executes MyWorker every 5 minutes
//                new PeriodicWorkRequest.Builder(ScheduledJobService.class, 300, TimeUnit.SECONDS)
//                        .build();
//
//        WorkManager.getInstance(SyncoteamNavigationActivity.this)
//                .enqueueUniquePeriodicWork(TrackSyncJob.TAG, ExistingPeriodicWorkPolicy.REPLACE, request);
//
//        WorkManager.getInstance(SyncoteamNavigationActivity.this)
//                .enqueueUniquePeriodicWork(NotificationSyncJob.TAG, ExistingPeriodicWorkPolicy.REPLACE, request);
//
//    }

    private void cancelAllJobsEvernote(String tag) {
        WorkManager.getInstance(SyncoteamNavigationActivity.this).cancelAllWorkByTag(tag);
    }

    private void cancelAllJobsEvernote() {

        WorkManager.getInstance(SyncoteamNavigationActivity.this).cancelAllWorkByTag(TrackSyncJob.TAG);
        WorkManager.getInstance(SyncoteamNavigationActivity.this).cancelAllWorkByTag(NotificationSyncJob.TAG);

    }


    /**
     * calculates the distance between all the points stored
     *
     * @return
     */
    private double calculateTotalDistance() {
        double totDistance = 0.00;
        for (int i = 0; i + 1 < locationList.size(); i++) {
            LocationPoints loc1 = locationList.get(i);
            LocationPoints loc2 = locationList.get(i + 1);
            totDistance += LocationUtils.distanceBetweenTwoPoint(loc1.getLatitude(), loc1.getLongitude(), loc2.getLatitude(), loc2.getLongitude());
        }
        //for kms
        totDistance = totDistance / 1000;

        return totDistance;
    }

    private void checkIfTrackingEnabled() {
        //realm = Realm.getDefaultInstance();
        locationList = new ArrayList<>();
        //new changes 51
        TravelActivity travelActivity = dataAccessObject.isDrivingStarted();

        if (travelActivity != null) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && isNetworkEnabled) {
                try {
                    showDialogDriving(travelActivity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        //check later
//        if (SharedPref.getDistanceEnabled(this)) {
//            SharedPref.setIsTravelRunning(true, this);
//
//            //get saved location points from db and add them to arraylist
//            realm.executeTransactionAsync(new Realm.Transaction() {
//                @Override
//                public void execute(Realm realm) {
//                    RealmResults<LocationCoordinates> locations = realm.where(LocationCoordinates.class).findAll();
//                    if (locations.size() > 0) {
//                        for (LocationCoordinates loc : locations) {
//                            LocationPoints loc1 = new LocationPoints(loc.getLatitude(), loc.getLongitude());
//                            locationList.add(loc1);
//                        }
//                        double distanceTravelled = calculateTotalDistance();
//                        //new changes 50 save loc data in pref
//                        if (locationList != null && locationList.size() > 0) {
//                            int lastItem = locationList.size() - 1;
//                            setTravelLocationDataToPref(locationList.get(lastItem).getLatitude(),
//                                    locationList.get(lastItem).getLongitude(), distanceTravelled);
//                        }
//                    }
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (isGPSEnabled && isNetworkEnabled)
//                                startDrivingAndCalcDistance(true);
//                        }
//                    });
//                }
//            });
//        }

    }

    private void startDrivingAndCalcDistance(boolean isAlreadyStarted) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            SharedPref.setIsTravelRunning(true, this);
            SharedPref.setIsTravelLastEntry(false, this);

            Intent serviceIntent = new Intent(this, LocationService.class);
            serviceIntent.putExtra("started", isAlreadyStarted);
            serviceIntent.setAction(LocationService.ACTION_START);

//            startService(serviceIntent);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            } else {
                startService(serviceIntent);
            }

          //  updateUiOnTrakingStatusChange();
        }
    }


    private void checkGpsOnOff() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(LocationRequest.create());
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
              //  if (mBoundTracking)
                 //   mServiceTracking.startServiceTracking();
            }
        });

        task.addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(SyncoteamNavigationActivity.this,
                                REQUEST_CHECK_SETTINGS_TRACKING);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();

        if (extras != null) {

            boolean fromTracking = extras.getBoolean(KEYS.Notification.KEY_FROM_TRACKING);
            boolean fromTravel = extras.getBoolean(KEYS.Notification.KEY_FROM_TRAVEL);

             if (fromTravel) {

                //new changes 51
                TravelActivity travelActivity = dataAccessObject.isDrivingStarted();

                if (travelActivity != null) {
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && isNetworkEnabled) {
                        try {
                            showDialogDriving(travelActivity);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }


            } else {

            }
        } else {
//            checkIfTrackingEnabled();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS_TRACKING:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Logger.log(TAG, "Location permission enabled");
                        if (mBoundTracking)
                        //    mServiceTracking.startServiceTracking();
                        break;
                    case Activity.RESULT_CANCELED:
                        Logger.log(TAG, "Gps permission is disabled enabled");
                        Logger.log(TAG, "Disable Tracking functionality");
                        if (mBoundTracking)
                   //         mServiceTracking.stopService();
                        break;
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void updatedClockInOutDialog() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user != null)
                    checkTimeOutInitially(dataAccessObject.checkIsClockInAvailable(user.getId()));
            }
        }, 10000);

    }

    public boolean isTrackingServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (TrackingParameterService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NotNull String[] permissions, @NotNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    TrackingFunc();
                } else {
                    boolean isTrackingServiceRunning = SharedPref.getIsTrackcingRunning(SyncoteamNavigationActivity.this);
                    if (isTrackingServiceRunning) {
                        stopTracking();
                    }
                    trackingOnOffTb.setTag(Boolean.valueOf(false));
                    trackingOnOffTb.setBackgroundResource(R.drawable.traking_off_btn);
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_LOCATION_TRACKING:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkGpsOnOff();
                } else {
                    Logger.log(TAG, "Runtime permission is disabled. Enable runtime permission");
                    boolean isTrackingServiceRunning = SharedPref.getIsTrackcingRunning(SyncoteamNavigationActivity.this);
                    if (isTrackingServiceRunning) {
                        stopTracking();
                    }
                    trackingOnOffTb.setTag(Boolean.valueOf(false));
                    trackingOnOffTb.setBackgroundResource(R.drawable.traking_off_btn);
                }
                return;

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void callingPermissionLocation() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(SyncoteamNavigationActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SyncoteamNavigationActivity.this);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle(getString(R.string.app_name));
            alertBuilder.setMessage(getString(R.string.str_location_permission));
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(SyncoteamNavigationActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(SyncoteamNavigationActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }

    }

    public void callingPermissionLocationTracking() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(SyncoteamNavigationActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(SyncoteamNavigationActivity.this);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle(getString(R.string.app_name));
            alertBuilder.setMessage(getString(R.string.str_location_permission));
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(SyncoteamNavigationActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION_TRACKING);
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(SyncoteamNavigationActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION_TRACKING);
        }
    }

    private void TrackingFunc() {
        startTracking();
        trackingOnOffTb.setTag(Boolean.valueOf(true));
        trackingOnOffTb.setBackgroundResource(R.drawable.traking_on_btn);
    }


    public void scheduleJob(Context context) {

        new GetRealmDataAsyncTask().execute();

    }


    public LocationService getServiceTrackingParams() {
        return mServiceTracking;
    }


    /**
     * Initialise parameters.
     */
    private void initialiseParameters() {
        executeOnResume = true;
         String domainName = "";
                dataAccessObject = DaoManager.getInstance();
                daoTracking = DaoTrackingManager.getInstance();
                user = dataAccessObject.getUser();
                try {
                    domainName = dataAccessObject.getUserDomain();
                } catch (Exception e) {
                    Logger.log("TAG", "Erroe" + e);
                }

        pITrackParams = initializePendingIntent();
        syncroTeamApplication = ((SyncroTeamApplication) getApplication());
        SharedPref.setDateFormat(this);
        SharedPref.setTimeFormat(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        EventBus.getDefault().register(this);
        ObservableObject.getInstance().addObserver(this);

        pref = PreferenceManager.getDefaultSharedPreferences(this);

        if (domainName != null && user != null) {
            FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
            if (user.getLogin() != null && domainName != null)
                crashlytics.setUserId("" + domainName + "_"
                        + user.getLogin());

        }

        if (user != null) {
            //new changes
            updateAppVersion(user.getId());
        } else {
            user = dataAccessObject.getUser();
        }

        if (user == null) {
            navigateToLoginActivity();
        }

    }

    private void navigateToLoginActivity() {
        Intent intent = new Intent(this, Login_Syncroteam.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void triggeringTrackingAtSpecificTime(Calendar cal, PendingIntent pITrackParams) {
        AlarmManager trackingParamsAlarm = (AlarmManager) SyncoteamNavigationActivity.this
                .getSystemService(Context.ALARM_SERVICE);
        trackingParamsAlarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pITrackParams);

    }

    private PendingIntent initializePendingIntent() {
        Intent trackingParamsIntent = new Intent(SyncoteamNavigationActivity.this,
                TrackingParameterService.class);
        trackingParamsIntent.putExtra("from_pending_intent", true);

        PendingIntent pendingIntent;
        //Behaviour changes supporting android 12
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // Create a PendingIntent using FLAG_IMMUTABLE
            pendingIntent = PendingIntent.getService(SyncoteamNavigationActivity.this,
                    0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT |
                            PendingIntent.FLAG_IMMUTABLE);
        } else {

            pendingIntent = PendingIntent.getService(SyncoteamNavigationActivity.this,
                    0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return pendingIntent;

//        return PendingIntent.getService(SyncoteamNavigationActivity.this,
//                0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    private void cancelTrackingAlarm() {
        stopService(new Intent(SyncoteamNavigationActivity.this, TrackingParameterService.class));
        AlarmManager am2 = (AlarmManager) SyncoteamNavigationActivity.this
                .getSystemService(Context.ALARM_SERVICE);
        am2.cancel(pITrackParams);
    }

    public void setLocale(String lang) {
        Locale myLocale;
        if (lang.equals("zh_CN")) {
            myLocale = Locale.SIMPLIFIED_CHINESE;
        } else if (lang.equals("zh_TW")) {
            myLocale = Locale.TRADITIONAL_CHINESE;
        } else {
            myLocale = new Locale(lang);
        }

        Locale.setDefault(myLocale);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    /**
     * For updating the App version for the user
     *
     * @param idUser
     */
    private void updateAppVersion(int idUser) {

        if (idUser != 0) {
            PackageInfo pinfo = null;
            try {
                pinfo = getPackageManager().getPackageInfo(
                        getPackageName(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            assert pinfo != null;
            String versionName = pinfo.versionName;

            Logger.log(TAG, "Synchroteam version ---->" + versionName);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    dataAccessObject.updateAppVersion(idUser, versionName);
                }
            });
        }
    }

    /**
     * Initialise view.
     */
    private void initialiseView() {

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerCloseAndOpen = (ImageButton) findViewById(R.id.iconNavigationDrawer);
        currentJobs = (RelativeLayout) findViewById(R.id.currentJobsListItem);

        currentJobCount = (TextView) findViewById(R.id.countCurrentJobsTv);
        toComeCount = (TextView) findViewById(R.id.countToComeTv);
        dedlineExceededCount = (TextView) findViewById(R.id.countDeadlineExceededtv);
        messagesCount = (TextView) findViewById(R.id.countMessageTv);
        reportsCount = (TextView) findViewById(R.id.countReportsTv);
        jobPoolCount = (TextView) findViewById(R.id.countJobPoolTv);
        allJobCount = (TextView) findViewById(R.id.countAllJobsTv);
        unavabilityCount = (TextView) findViewById(R.id.unAvabilitiesCountTv);
        calendarListItem = (RelativeLayout) findViewById(R.id.calendarListItem);
        unavabilitiesListItem = (RelativeLayout) findViewById(R.id.unavabilitiesListItem);
        trackingListItem = (LinearLayout) findViewById(R.id.trackingListItem);
        txtTrackingLabelTv = (TextView) findViewById(R.id.txtTrackingLabelTv);
        toComeJobs = (RelativeLayout) findViewById(R.id.toComeListItem);
        listItemLayout = (RelativeLayout) findViewById(R.id.lLout);
        deadLineExceededJobs = (RelativeLayout) findViewById(R.id.deadlineExceededListItem);
        messages = (RelativeLayout) findViewById(R.id.messageListItem);
        lastSyncronizationSettingsLl = (LinearLayout) findViewById(R.id.syncronizationListItem);
        reports = (RelativeLayout) findViewById(R.id.reportsListItem);
        jobPool = (RelativeLayout) findViewById(R.id.jobPoolListItem);
        legalInformation = (RelativeLayout) findViewById(R.id.legalInformationListItem);
        clientListItem = (RelativeLayout) findViewById(R.id.clientListItem);
        inventoryListItem = (RelativeLayout) findViewById(R.id.inventoryListItem);
        logout = (RelativeLayout) findViewById(R.id.logoutListItem);
        allJobs = (RelativeLayout) findViewById(R.id.allJobsListItem);

        // v48 new
        txtStartActivity = (android.widget.TextView) findViewById(R.id.txtStartActivity);
        txtClockMode = (TextView) findViewById(R.id.txt_clock_mode);
        txtActivityStopView = (TextView) findViewById(R.id.txt_stopview);
        layBottomBar = (RelativeLayout) findViewById(R.id.lay_bottom_bar);

        currentJobs
                .setBackgroundResource(R.color.grayNavigationDrawerListItemSelected);

        syncronizationIconIv = (ImageView) findViewById(R.id.syncroniseJobBtn);

        addJobBtn = (ImageView) findViewById(R.id.addJobBtn);
        // trackingDividerIv = (ImageView) findViewById(R.id.trackingDividerIv);
        trackingOnOffTb = (RadioButton) findViewById(R.id.trackingOnOffTb);
        attachmentListItem = (RelativeLayout) findViewById(R.id.attachmentListItem);
        attachmentListItem.setOnClickListener(onClickListener);

        dateLastSyncronizationTv = (TextView) findViewById(R.id.dateLastSyncronizationTv);
        trackingIndicatorIv = (ImageView) findViewById(R.id.trackingIndicatorIv);

        relTitle = (RelativeLayout) findViewById(R.id.layoutTitleBar);

        /* New changes */
        switchGestion();
        /* New changes */

        trackingOnOffTb.setOnClickListener(onClickListener);

        txtClockMode.setOnClickListener(onClickListener);
        currentJobs.setOnClickListener(onClickListener);
        jobPool.setOnClickListener(onClickListener);
        toComeJobs.setOnClickListener(onClickListener);
        deadLineExceededJobs.setOnClickListener(onClickListener);
        allJobs.setOnClickListener(onClickListener);
        attachmentListItem.setOnClickListener(onClickListener);
        titleScreenTv = (TextView) findViewById(R.id.titleCurrentScreen);
        drawerCloseAndOpen.setOnClickListener(onClickListener);
        messages.setOnClickListener(onClickListener);
        clientListItem.setOnClickListener(onClickListener);
        inventoryListItem.setOnClickListener(onClickListener);
        unavabilitiesListItem.setOnClickListener(onClickListener);
        lastSyncronizationSettingsLl.setOnClickListener(onClickListener);
//        trackingSettingsIv.setOnClickListener(onClickListener);
        reports.setOnClickListener(onClickListener);
        legalInformation.setOnClickListener(onClickListener);
        logout.setOnClickListener(onClickListener);
        calendarListItem.setOnClickListener(onClickListener);
        syncronizationIconIv.setOnClickListener(onClickListener);
        addJobBtn.setOnClickListener(onClickListener);
//        txtTrackingLabelTv.setOnClickListener(onClickListener);
 //       trackingListItem.setOnClickListener(onClickListener);

        txtStartActivity.setOnClickListener(onClickListener);
        txtActivityStopView.setOnClickListener(onClickListener);

        setTitleProperty(0);

        jobPool.setVisibility(View.VISIBLE);
    }

    public void onEvent(
            EnableSynchronizationAddJobEvent enableSynchronizationAddJobEvent) {

        if (SharedPref.getIsCLientSectionOpen(this)) {
            if (SharedPref.getIsClientSiteEquipmentFetched(this)) {

                addJobBtn.setEnabled(true);
                syncronizationIconIv.setEnabled(true);

            }

        } else {
            addJobBtn.setEnabled(true);
            syncronizationIconIv.setEnabled(true);
        }

    }

    /*
     * calendarListItem (non-Javadoc)
     *
     * @see com.synchroteam.baseactivity.SyncroTeamBaseActivity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();



        if (executeOnResume) {
            Logger.log("onResume", "Synchroteam Navigation Activity");
            OnResumeAsynTask onResumeAsynTask = new OnResumeAsynTask();
            onResumeAsynTask.execute();
//            onResumeLogic();
        }

        if ( lastListItemSelectedId == R.id.currentJobsListItem){
        EventBus.getDefault().post(new SelectTodayDate());
        }

        // // register the auto sync receiver
        // IntentFilter autoSyncFilter = new IntentFilter();
        // autoSyncFilter.addAction(KEYS.AUTO_SYNC_BROADCAST);
        // registerReceiver(autoSyncReceiver, autoSyncFilter);

    }

    public void onResumeLogic() {

        // v48 new changes
        checkNetworkAndGPS();
        settingJobActivityNameInBar();

        if (dataAccessObject != null && user != null) {

            // show / hide the clock in out when the user have clock in out function
            checkClockModeAvailabilityAndSettingLayout(dataAccessObject.checkIsClockInAvailable(user.getId()));
        }

        // --------------------------------------- v48 ---------------------------------------------

        // changes
        DateChecker.checkDateAndNavigate(this, dataAccessObject);

        if (SharedPref
                .getPreviousValueOfTracking(SyncoteamNavigationActivity.this)) {
            trackingOnOffTb.setTag(Boolean.valueOf(true));
            trackingOnOffTb.setBackgroundResource(R.drawable.traking_on_btn);

        } else {
            trackingOnOffTb.setTag(Boolean.valueOf(false));
            trackingOnOffTb.setBackgroundResource(R.drawable.traking_off_btn);

        }

        dateLastSyncronizationTv.setText(SharedPref
                .gettimeSyncronised(SyncoteamNavigationActivity.this));

        if (syncroTeamApplication.getIsLocaleChenged()) {
            setPreviousSettingsAfterDevicePresets();
            syncroTeamApplication.setIsLocaleChanged(false);
        }
        if (servicesConnected()) {
            if (SharedPref
                    .getIsTrackcingRunning(SyncoteamNavigationActivity.this)) {

                trackingIndicatorIv
                        .setImageResource(R.drawable.tracking_indicator_enabled);

//                trackingOnOffTb.setTag(Boolean.valueOf(true));
//                trackingOnOffTb.setBackgroundResource(R.drawable.traking_on_btn);

            } else {

                trackingIndicatorIv
                        .setImageResource(R.drawable.tracking_indicator_disabled);

//                boolean isTrackingServiceRunning = SharedPref.getIsTrackcingRunning(SyncoteamNavigationActivity.this);
//                if (isTrackingServiceRunning) {
//                    stopTracking();
//                }
//                trackingOnOffTb.setTag(Boolean.valueOf(false));
//                trackingOnOffTb.setBackgroundResource(R.drawable.traking_off_btn);
//
//                EventBus.getDefault().post(new TrackingSwitchEvent(false));

            }
        } else {
            trackingIndicatorIv
                    .setImageResource(R.drawable.tracking_indicator_disabled);

//            boolean isTrackingServiceRunning = SharedPref.getIsTrackcingRunning(SyncoteamNavigationActivity.this);
//            if (isTrackingServiceRunning) {
//                stopTracking();
//            }
//            trackingOnOffTb.setTag(Boolean.valueOf(false));
//            trackingOnOffTb.setBackgroundResource(R.drawable.traking_off_btn);
//            EventBus.getDefault().post(new TrackingSwitchEvent(false));

        }

    }


    private void checkNetworkAndGPS() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        isNetworkEnabled = Build.FINGERPRINT.contains("generic") ? true : locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showDialogDriving(TravelActivity travelActivity) throws Exception {

        // drivingModeDialog.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("driving_dialog");
        if (prev == null) {
//            SharedPref.setDistanceEnabled(true, this);

            // Create and show the dialog.
            DrivingModeDialogUpdated drivingModeDialog = DrivingModeDialogUpdated.newInstance(travelActivity.getIdTravel(), travelActivity.getIdType(), travelActivity.getTravelName());
            drivingModeDialog.show(getSupportFragmentManager(), "driving_dialog");

            //todo remove later Travel
//            TravelModeDialog drivingModeDialog = TravelModeDialog.newInstance(travelActivity.getIdTravel(), travelActivity.getIdType(), travelActivity.getTravelName());
//            drivingModeDialog.show(getSupportFragmentManager(), "driving_dialog");

        } else {

//            if (prev.isHidden())
//                ft.show(prev);
        }

    }

    private void settingJobActivityNameInBar() {
        TravelActivity travelActivity = dataAccessObject.isDrivingStarted();

//        checkAndEnableTravelActivity(travelActivity);

        // checking if activity is started
        Conge unAvailabilityDetail = checkUnAvailabilityStarted();
        if (unAvailabilityDetail != null) {
            setBottomLayoutJobActivityView(-1, null, true, false, unAvailabilityDetail.getNomTypeConge());
        } else if (travelActivity != null) {
            //new changes 51
            setBottomLayoutJobActivityView(-1, null, false, true, travelActivity.getTravelName());
        }

        //new logic
//        else if (checkInterventionAndGettingIdInterv()) {
//            // checking job is started and setting layout
//            Description description = dataAccessObject.getInterventionById(idIntervention);
//
//            if (!TextUtils.isEmpty(description.getRefCustomer())
//                    && !description.getRefCustomer().matches("^\\s*$"))
//                jobNumber = "" + description.getRefCustomer();
//            else if (description.getNoInterv() != 0)
//                jobNumber = "#" + description.getNoInterv();
//            setBottomLayoutJobActivityView(3, jobNumber, false, false, null);
//
//        }

        else {
            setBottomLayoutJobActivityView(-1, null, false, false, null);
        }
    }

    private void checkAndEnableTravelActivity(TravelActivity travelActivity) {
        if (travelActivity != null) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && isNetworkEnabled) {
                try {
                    showDialogDriving(travelActivity);
                    setBottomLayoutJobActivityView(-1, null, false, true, travelActivity.getTravelName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                DrivingModeDialog.newInstance(travelActivity.getIdTravel(), travelActivity.getIdType(), travelActivity.getTravelName()).show(getSupportFragmentManager(), "");
            } else {
                // no network provider is enabled
                if (!isGPSEnabled)
                    startDrivingAndCalcDistance(false);
                else
                    SynchroteamUitls.showToastMessage(this);

                setBottomLayoutJobActivityView(-1, null, false, true, getString(R.string.txt_travel));
            }
//            setBottomLayoutJobActivityView(-1, null, false, true, getString(R.string.txt_travel));
        }
    }

    // @Override
    // protected void onPause() {
    // super.onPause();
    //
    // // unregister the auto sync receiver
    // unregisterReceiver(autoSyncReceiver);
    // }

    /**
     * Open fragment based on drawer item selected.
     *
     * @param position position on navigation drawer
     * @param view     the v which is clicked
     */
    public void openFragment(int position, View view) {

        if (seleted_fragment_position == 0 && position == 0) {
            if (drawerLayout.isDrawerOpen(listItemLayout)) {
                drawerLayout.closeDrawer(listItemLayout);
            }
        } else if (seleted_fragment_position == position) {
            if (drawerLayout.isDrawerOpen(listItemLayout)) {
                drawerLayout.closeDrawer(listItemLayout);
            }
            return;
        } else {
            seleted_fragment_position = position;
            super.removeAllFragments();
        }

        findViewById(lastListItemSelectedId).setBackgroundResource(
                R.color.grayNavigationDrawerListItemUnSelected);
        findViewById(view.getId()).setBackgroundResource(
                R.color.grayNavigationDrawerListItemSelected);
        addJobBtn.setEnabled(false);
        syncronizationIconIv.setEnabled(false);
        lastListItemSelectedId = view.getId();
        switch (position) {
            case 0:
                SharedPref.setSortedJobNumber("", SyncoteamNavigationActivity.this);
                SharedPref.setSortingOption(0, SyncoteamNavigationActivity.this);

                addJobBtn.setEnabled(true);
                syncronizationIconIv.setEnabled(true);
                setTitleProperty(0);
                EventBus.getDefault().post(new SelectTodayDate());
                addButtonVisibility(View.VISIBLE, false);
                SharedPref.setIsClientSectionOpen(this, false);
                layBottomBar.setVisibility(View.VISIBLE);
                break;
            case 1:
//                throw new RuntimeException("Force crash!");
                ToComeJobFragment toComeFragment = new ToComeJobFragment();

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, toComeFragment,
                                toComeFragment.getClass().getSimpleName())
                        .addToBackStack(toComeFragment.getClass().getSimpleName())
                        .commit();

                setTitleProperty(1);
                addButtonVisibility(View.VISIBLE, false);
                SharedPref.setIsClientSectionOpen(this, false);
                layBottomBar.setVisibility(View.VISIBLE);

                break;

            case 3:
                BaseReportFragment baseReportFragment = new BaseReportFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, baseReportFragment,
                                baseReportFragment.getClass().getSimpleName())
                        .addToBackStack(
                                baseReportFragment.getClass().getSimpleName())
                        .commit();
                setTitleProperty(3);
                addButtonVisibility(View.GONE, false);
                SharedPref.setIsClientSectionOpen(this, false);
                layBottomBar.setVisibility(View.VISIBLE);
                break;

            case 2:
                DeadlineExceededFragment deadlineExceededFragment = new DeadlineExceededFragment();

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, deadlineExceededFragment,
                                deadlineExceededFragment.getClass().getSimpleName())
                        .addToBackStack(
                                deadlineExceededFragment.getClass().getSimpleName())
                        .commit();

                setTitleProperty(2);
                addButtonVisibility(View.VISIBLE, false);
                SharedPref.setIsClientSectionOpen(this, false);
                layBottomBar.setVisibility(View.VISIBLE);
                break;
            case 4:
                CalendarFragment calendarFragment = new CalendarFragment(relTitle,layBottomBar);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, calendarFragment,
                                calendarFragment.getClass().getSimpleName()
                        )
                        .addToBackStack(calendarFragment.getClass().getSimpleName())
                        .commit();

                setTitleProperty(4);
                addButtonVisibility(View.GONE, false);
                SharedPref.setIsClientSectionOpen(this, false);
                layBottomBar.setVisibility(View.GONE);
                break;
            case 5:
                MessagesFragment messagesFragment = new MessagesFragment();

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, messagesFragment,
                                messagesFragment.getClass().getSimpleName())
                        .addToBackStack(messagesFragment.getClass().getSimpleName())
                        .commit();
                setTitleProperty(5);
                addButtonVisibility(View.GONE, false);
                SharedPref.setIsClientSectionOpen(this, false);
                layBottomBar.setVisibility(View.GONE);
                break;
            case 6:
                LastSyncFragment lastSyncFragment = new LastSyncFragment();

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, lastSyncFragment,
                                lastSyncFragment.getClass().getSimpleName())
                        .addToBackStack(lastSyncFragment.getClass().getSimpleName())
                        .commit();
                setTitleProperty(6);
                addButtonVisibility(View.GONE, false);
                SharedPref.setIsClientSectionOpen(this, false);
                layBottomBar.setVisibility(View.GONE);
                break;
            case 8:

                LegalInformationFragment legalInformationFragment = new LegalInformationFragment();

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, legalInformationFragment,
                                legalInformationFragment.getClass().getSimpleName())
                        .addToBackStack(
                                legalInformationFragment.getClass().getSimpleName())
                        .commit();
                setTitleProperty(8);
                addButtonVisibility(View.GONE, false);
                SharedPref.setIsClientSectionOpen(this, false);
                layBottomBar.setVisibility(View.GONE);
                break;
            case 9:
                addJobBtn.setEnabled(true);
                UnavabilitiesListingFragment unavabilitiesListingFragment = new UnavabilitiesListingFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.content_frame,
                                unavabilitiesListingFragment,
                                unavabilitiesListingFragment.getClass()
                                        .getSimpleName())
                        .addToBackStack(
                                unavabilitiesListingFragment.getClass()
                                        .getSimpleName()).commit();
                setTitleProperty(9);
                addJobBtn.setVisibility(View.VISIBLE);
                addButtonVisibility(View.VISIBLE, true);
                SharedPref.setIsClientSectionOpen(this, false);
                layBottomBar.setVisibility(View.VISIBLE);
                break;

            case 10:
                ClientSectionFragment clientListingFragment = new ClientSectionFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, clientListingFragment,
                                clientListingFragment.getClass().getSimpleName())
                        .addToBackStack(
                                clientListingFragment.getClass().getSimpleName())
                        .commit();
                setTitleProperty(10);
                addJobBtn.setVisibility(View.GONE);
                SharedPref.setIsClientSectionOpen(this, true);
                layBottomBar.setVisibility(View.GONE);
                break;
            case 11:
                AttachmentListFragment attachmentListFragment = new AttachmentListFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, attachmentListFragment,
                                attachmentListFragment.getClass().getSimpleName())
                        .addToBackStack(
                                attachmentListFragment.getClass().getSimpleName())
                        .commit();
                setTitleProperty(11);
                addJobBtn.setVisibility(View.GONE);
                layBottomBar.setVisibility(View.GONE);
                break;

            case 12:
                inventoryFragment = new InventoryFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, inventoryFragment,
                                inventoryFragment.getClass().getSimpleName())
                        .addToBackStack(
                                inventoryFragment.getClass().getSimpleName())
                        .commit();
                setTitleProperty(12);
                addJobBtn.setVisibility(View.GONE);
                SharedPref.setIsClientSectionOpen(this, false);
                layBottomBar.setVisibility(View.GONE);
                break;

            case 13:
                AllJobsFragment allJobsFragment = new AllJobsFragment();

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, allJobsFragment,
                                allJobsFragment.getClass().getSimpleName())
                        .addToBackStack(
                                allJobsFragment.getClass().getSimpleName())
                        .commit();

                setTitleProperty(13);
                addButtonVisibility(View.VISIBLE, false);
                SharedPref.setIsClientSectionOpen(this, false);
                layBottomBar.setVisibility(View.VISIBLE);
                break;
            case 14:

                AllJobPoolFragment jobPoolFragment = new AllJobPoolFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, jobPoolFragment,
                                jobPoolFragment.getClass().getSimpleName())
                        .addToBackStack(
                                jobPoolFragment.getClass().getSimpleName())
                        .commit();
                setTitleProperty(14);
                addButtonVisibility(View.GONE, false);
                SharedPref.setIsClientSectionOpen(this, false);
                layBottomBar.setVisibility(View.GONE);
                break;
        }

    }

    /**
     * The on click listener.
     */
    OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            int id = v.getId();
            if (id == R.id.currentJobsListItem) {
                openFragment(0, v);
            } else if (id == R.id.toComeListItem) {
                openFragment(1, v);
            } else if (id == R.id.deadlineExceededListItem) {
                openFragment(2, v);
            } else if (id == R.id.iconNavigationDrawer) {
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                if (drawerLayout.isDrawerOpen(listItemLayout)) {
                    drawerLayout.closeDrawer(listItemLayout);
                } else {
                    drawerLayout.openDrawer(listItemLayout);
                }
            } else if (id == R.id.messageListItem) {
                openFragment(5, v);
            } else if (id == R.id.syncronizationListItem) {
//                Intent lastSyncronizationIntent = new Intent(
//                        SyncoteamNavigationActivity.this,
//                        LastSyncronizationSettingActivity.class);
//                if (drawerLayout.isDrawerOpen(listItemLayout)) {
//                    drawerLayout.closeDrawer(listItemLayout);
//                }
//                startActivity(lastSyncronizationIntent);
//                findViewById(lastListItemSelectedId).setBackgroundResource(
//                        R.color.grayNavigationDrawerListItemUnSelected);
//
//                lastListItemSelectedId = v.getId();
//                SharedPref.setIsClientSectionOpen(
//                        SyncoteamNavigationActivity.this, false);

                openFragment(6, v);

//            } else if (id == R.id.settingsTrackingIv
//                    || id == R.id.txtTrackingLabelTv) {

            } else if (id == R.id.trackingListItem) {
//                Intent trackingSettingsIntent = new Intent(
//                        SyncoteamNavigationActivity.this,
//                        ParametreTracking.class);
//                startActivity(trackingSettingsIntent);
//                findViewById(lastListItemSelectedId).setBackgroundResource(
//                        R.color.grayNavigationDrawerListItemUnSelected);
//                if (drawerLayout.isDrawerOpen(listItemLayout)) {
//                    drawerLayout.closeDrawer(listItemLayout);
//                }
//                SharedPref.setIsClientSectionOpen(
//                        SyncoteamNavigationActivity.this, false);
//                lastListItemSelectedId = v.getId();

           //     openFragment(7, v);

            } else if (id == R.id.reportsListItem) {
                openFragment(3, v);
            } else if (id == R.id.jobPoolListItem) {
                openFragment(14, v);
            } else if (id == R.id.legalInformationListItem) {
                openFragment(8, v);
            } else if (id == R.id.logoutListItem) {
                logout();
            } else if (id == R.id.syncroniseJobBtn) {
                // synchronize functionality
//                syncronise();

                //new changes
                syncronizeUpdated();


            } else if (id == R.id.addJobBtn) {

//                Calendar cal = Calendar.getInstance();
//                int milisecond = ((cal.get(Calendar.HOUR_OF_DAY) * 3600000) + (cal
//                        .get(Calendar.MINUTE) * 60000));
//
//                int milisecondMaxtime = ((23 * 3600000) + (40 * 60000));
//                if (milisecond <= milisecondMaxtime) {
//                    if (seleted_fragment_position == 9) {
////                        Intent intentCreateUnavailability = new Intent(
////                                SyncoteamNavigationActivity.this, AddUnavailability.class);
////                        startActivity(intentCreateUnavailability);
//                        StartActivityDialog.newInstance().show(getSupportFragmentManager(), "");
//                    } else {
//                        Intent addJob = new Intent(
//                                SyncoteamNavigationActivity.this, NewJob.class);
//                        SharedPref.setIsClientSectionOpen(
//                                SyncoteamNavigationActivity.this, false);
//                        startActivity(addJob);
//                    }
//                } else {
//
//                    DialogUtils.showInfoDialog(
//                            SyncoteamNavigationActivity.this,
//                            SyncoteamNavigationActivity.this
//                                    .getString(R.string.textNoJobCreate));
//
//                }

                if (seleted_fragment_position == 9) {
//                        Intent intentCreateUnavailability = new Intent(
//                                SyncoteamNavigationActivity.this, AddUnavailability.class);
//                        startActivity(intentCreateUnavailability);
                    StartActivityDialog.newInstance().show(getSupportFragmentManager(), "");
                } else {
                    Intent addJob = new Intent(
                            SyncoteamNavigationActivity.this, NewJob.class);
                    SharedPref.setIsClientSectionOpen(
                            SyncoteamNavigationActivity.this, false);
                    startActivity(addJob);
                }

            } else if (id == R.id.trackingOnOffTb) {

                Boolean tag = (Boolean) v.getTag();
                if (tag.booleanValue()) {

                    stopTracking();
                    trackingOnOffTb.setTag(Boolean.valueOf(false));
                    trackingOnOffTb
                            .setBackgroundResource(R.drawable.traking_off_btn);

                } else {

                    startTracking();
                    trackingOnOffTb.setTag(Boolean.valueOf(true));
                    trackingOnOffTb
                            .setBackgroundResource(R.drawable.traking_on_btn);
                }

            } else if (id == R.id.calendarListItem) {
                openFragment(4, v);

            } else if (id == R.id.unavabilitiesListItem) {

                openFragment(9, v);
            } else if (id == R.id.clientListItem) {
                openFragment(10, v);

            } else if (id == R.id.attachmentListItem) {
                openFragment(11, v);
            } else if (id == R.id.inventoryListItem) {
                openFragment(12, v);
            } else if (id == R.id.allJobsListItem) {
                openFragment(13, v);
            } else if (id == R.id.txtStartActivity) {
//                sampleLatLongCheck();
                openClockInDialogs(false);
            } else if (id == R.id.txt_stopview) {
                txtStartActivity.performClick();
            } else if (id == R.id.txt_clock_mode) {
                //show only clock in & Out
                openClockInDialogs(true);
            }

        }
    };


    /**
     * Opens the activity / job / clock in dialog according to the current status
     *
     * @param isClockIcon
     */
    private void openClockInDialogs(boolean isClockIcon) {
        if (SystemClock.elapsedRealtime() - mStartBtnClickTime < 1500) {
            return;
        }
        mStartBtnClickTime = SystemClock.elapsedRealtime();
        TravelActivity travelActivity = dataAccessObject.isDrivingStarted();

        Conge unAvailabilityDetail = checkUnAvailabilityStarted();
        if (unAvailabilityDetail != null) {
            ActivityModeDialog.newInstance(unAvailabilityDetail.getNomTypeConge(), unAvailabilityDetail.getIdTypeConge(), unAvailabilityDetail.getIdConge(), true, unAvailabilityDetail.getDtDebut()).show(getSupportFragmentManager(), "");
        } else if (travelActivity != null) {
            //new changes
            checkAndEnableTravelActivity(travelActivity);
        }

        //new logic hide job no for paused jobs
//        else if (checkInterventionAndGettingIdInterv()) {
//            // check job
//            String dateString = dataAccessObject
//                    .getJobResumedTimeInJobDetails(idIntervention);
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//
//            long startJobTimer = 0;
//            try {
//                long timeWhenJobWasStarted = dateFormat.parse(dateString)
//                        .getTime();
//                long previousTimeToBeSubtracted = dataAccessObject
//                        .suspendedTimeDiffrence(idIntervention);
//
//                startJobTimer = timeWhenJobWasStarted
//                        - previousTimeToBeSubtracted;
//            } catch (ParseException e) {
//                Logger.printException(e);
//            }
//
//            Description description = dataAccessObject.getInterventionById(idIntervention);
//
//            if (!TextUtils.isEmpty(description.getRefCustomer())
//                    && !description.getRefCustomer().matches("^\\s*$"))
//                jobNumber = "" + description.getRefCustomer();
//            else if (description.getNoInterv() != 0)
//                jobNumber = "#" + description.getNoInterv();
//
//            JobPauseFinishDialog.newInstance(getJobNumber(jobNumber), startJobTimer, isAllMandatoryFieldsAreFilled()).show(getSupportFragmentManager(), "");
//
//        }
        else {
            if (isClockIcon) {
                StartJobActivityDialog.newInstance(isClockIcon).show(getSupportFragmentManager(), "");
            } else {
                ClockJobTravelActDialog.newInstance(isClockIcon).show(getSupportFragmentManager(), "");
            }
        }
    }

    /**
     * To show/hide client and inventory item in navigation drawer.
     */
    private void switchGestion() {
        GestionAcces gt = dataAccessObject.getAcces();


        if (gt != null && gt.getFlListCustomers() == 0) {
            clientListItem.setVisibility(View.GONE);
        } else {
            clientListItem.setVisibility(View.VISIBLE);
        }

        if (gt != null && gt.getFlSectionStock() == 0) {
            inventoryListItem.setVisibility(View.GONE);
        } else {
            inventoryListItem.setVisibility(View.VISIBLE);
        }
    }

    public void openUnavailabilityActivity() {
        Conge unAvailabilityDetail = checkUnAvailabilityStarted();
        TravelActivity travelActivity = dataAccessObject.isDrivingStarted();

        if (unAvailabilityDetail != null) {
            ActivityModeDialog.newInstance(unAvailabilityDetail.getNomTypeConge(), unAvailabilityDetail.getIdTypeConge(), unAvailabilityDetail.getIdConge(), true, unAvailabilityDetail.getDtDebut()).show(getSupportFragmentManager(), "");
        } else if (travelActivity != null) {
            //new changes 51
            checkAndEnableTravelActivity(travelActivity);
        }

    }

    /**
     * Start tracking.
     */
    public void startTracking() {


        trackingOnOffTb.setTag(Boolean.valueOf(true));
        trackingOnOffTb.setBackgroundResource(R.drawable.traking_on_btn);

        daoTracking.setSessiondata("tracking", "on");
        SharedPref.setPreviousValueOfTracking(SyncoteamNavigationActivity.this, true);
        dataAccessObject.updateFL_GPS_TRACKED(idUser, true);


/*
        daoTracking.setSessiondata("tracking", "on");
        // ---------------------------------------------------------
        String h1 = daoTracking.getSessiondata("heur1");
        String hh1 = "08";
        String mm1 = "00";

        try {
            hh1 = h1.substring(0, 2);
            mm1 = h1.substring(3, 5);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hh1));
        calendar.set(Calendar.MINUTE, Integer.valueOf(mm1));
        calendar.set(Calendar.SECOND, 0);

        SharedPref.setPreviousValueOfTracking(SyncoteamNavigationActivity.this,
                true);

        triggeringTrackingAtSpecificTime(calendar, pITrackParams);

        if (daoTracking.getSessiondata("tracking").equals("on")) {


//            stopService(new Intent(this, TrackingService.class));
//            startService(new Intent(this, TrackingService.class));
//
//            stopService(new Intent(this, TrackingServiceFrequency.class));
//            startService(new Intent(this, TrackingServiceFrequency.class));

            scheduleJobEvernote();
            callingTrackingParamsFunc();

        }


 */
    }

    /**
     * Stop tracking.
     */
    public void stopTracking() {


        trackingOnOffTb.setTag(Boolean.valueOf(false));
        trackingOnOffTb.setBackgroundResource(R.drawable.traking_off_btn);


        daoTracking.setSessiondata("tracking", "off");
        SharedPref.setPreviousValueOfTracking(SyncoteamNavigationActivity.this,
                false);
        dataAccessObject.updateFL_GPS_TRACKED(idUser, false);

       /*
        daoTracking.setSessiondata("tracking", "off");

        cancelAllJobsEvernote(TrackSyncJob.TAG);
        // ---------------------------------------------------------
        SharedPref.setPreviousValueOfTracking(SyncoteamNavigationActivity.this,
                false);
//        stopService(new Intent(this, TrackingService.class));
//        AlarmManager am = (AlarmManager) this
//                .getSystemService(Context.ALARM_SERVICE);
//        am.cancel(pi);
//
//        stopService(new Intent(this, TrackingServiceFrequency.class));
//        AlarmManager am1 = (AlarmManager) this
//                .getSystemService(Context.ALARM_SERVICE);
//        am1.cancel(pi1);

        removeTrackingParamsFunc();

        cancelTrackingAlarm();
*/
    }


    /**
     * Sets the title of the title Bar based on current screen.
     *
     * @param i index of the item selected in Navigation Drawer
     */
    private void setTitleProperty(int i) {
        switch (i) {
            case 0:
                String mCurrentJobTitle = getString(R.string.textCurrentJobLable);
                titleScreenTv.setText(mCurrentJobTitle.toUpperCase());
                break;
            case 1:
                String mUpcomingTitle = getString(R.string.textToComeLable);
                titleScreenTv.setText(mUpcomingTitle.toUpperCase());
                break;
            case 2:
                String mDeadLineTitle = getString(R.string.textDedlineExceededLable);
                titleScreenTv.setText(mDeadLineTitle.toUpperCase());
                break;
            case 3:
                String mReportTitle = getString(R.string.textReportsLable);
                titleScreenTv.setText(mReportTitle.toUpperCase());
                break;
            case 4:
                String mCalendarTitle = getString(R.string.textCalanderlable);
                titleScreenTv.setText(mCalendarTitle.toUpperCase());
                break;
            case 5:
                String mMessagesTitle = getString(R.string.textMessagesLable);
                titleScreenTv.setText(mMessagesTitle.toUpperCase());
                break;
            case 6:
                String mLastSyncTitle = getString(R.string.textSyncronizationTv);
                titleScreenTv.setText(mLastSyncTitle.toUpperCase());
                break;
            case 7:
                String mTrackingParamsTitle = getString(R.string.textTrackingLableTvTracking);
                titleScreenTv.setText(mTrackingParamsTitle.toUpperCase());
                break;
            case 8:
                String mLegalInfoTitle = getString(R.string.textLegalInformationLable);
                titleScreenTv.setText(mLegalInfoTitle.toUpperCase());
                break;
            case 9:
                String mActivityTitle = getString(R.string.texUnavabilitiesLables);
                titleScreenTv.setText(mActivityTitle.toUpperCase());
                break;
            case 10:
                String mCustomerTitle = getString(R.string.textJobCustomerLableTv);
                titleScreenTv.setText(mCustomerTitle.toUpperCase());
                break;
            case 11:
                String mAttachmentTitle = getString(R.string.textAttachmentLableTv);
                titleScreenTv.setText(mAttachmentTitle.toUpperCase());
                break;
            case 12:
                String mInventoryTitle = getString(R.string.txt_inventory_label);
                titleScreenTv.setText(mInventoryTitle.toUpperCase());
                break;
            case 13:
                String mAllJobsTitle = getString(R.string.textAllJobLable);
                titleScreenTv.setText(mAllJobsTitle.toUpperCase());
                break;
            case 14:
                String mJobPoolTitle = getString(R.string.textJobPoolLable);
                titleScreenTv.setText(mJobPoolTitle.toUpperCase());
                break;
            default:
                break;
        }

        drawerLayout.closeDrawer(listItemLayout);
    }

    /**
     * To show progress dialog.
     */
    private void showProgressDialog() {
        if (!SyncoteamNavigationActivity.this.isFinishing()) {

            if (progressDSynch == null) {
                // progressDSynch = ProgressDialog.show(this,
                // getString(R.string.textPleaseWaitLable),
                // getString(R.string.msg_synch), true, false);
                progressDSynch = new ProgressDialog(
                        SyncoteamNavigationActivity.this);
                progressDSynch.setTitle(getString(R.string.textPleaseWaitLable));
                progressDSynch.setMessage(getString(R.string.msg_synch));
                progressDSynch.setIndeterminate(true);
                progressDSynch.setCancelable(false);
            }
            progressDSynch.show();
        }
    }


    /**
     * To dismiss progress dialog
     */
    private void dismissProgressDialog() {
        if (progressDSynch != null && progressDSynch.isShowing()) {
            progressDSynch.dismiss();
        }
    }

    /**
     * Syncronise with database.
     */
    protected void syncronise() {

        isDebugEnabled = pref.getString(KEYS.DEBUG_TAG, "");

        if (SynchroteamUitls
                .isNetworkAvailable(SyncoteamNavigationActivity.this)) {

            if (!isFinishing()) {
                showProgressDialog();
            }

            Thread syncDbToServer = new Thread((new Runnable() {
                @Override
                public void run() {

                    Message myMessage = new Message();
                    try {
                        dataAccessObject.sync(user.getLogin(), user.getPwd());

                        GestionAcces gt = dataAccessObject.getAcces();
                        if (gt != null && gt.getOptionTaracking() == 0) {
                            stopTracking();
                        }

                        Thread.sleep(1000);
                        myMessage.obj = "ok";

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        Logger.printException(ex);
                        String exception = ex.getMessage();
                        if (exception != null) {
                            if (!isDebugEnabled.equalsIgnoreCase("true")) {
                                if (exception.indexOf("4001") != -1) {
                                    myMessage.obj = "4001";
                                } else if (exception.indexOf("4000") != -1) {
                                    myMessage.obj = "4000";
                                } else if (exception.indexOf("4006") != -1) {
                                    myMessage.obj = "4006";
                                } else if (exception.indexOf("4101") != -1) {
                                    myMessage.obj = "4101";
                                } else if (exception.indexOf("4005") != -1) {
                                    myMessage.obj = "4005";
                                } else if (exception.indexOf("4003") != -1) {
                                    myMessage.obj = "4003";
                                } else {
                                    myMessage.obj = "Error";
                                }
                            } else {
                                myMessage.obj = exception;
                            }
                        } else
                            myMessage.obj = "Error";

                    } finally {
                        // progressDSynch.dismiss();
                        dismissProgressDialog();
                    }
                    synchroteamNavigationActivityHandler.sendMessage(myMessage);

                }
            }));
            syncDbToServer.start();
        } else {
            SynchroteamUitls.showToastMessage(SyncoteamNavigationActivity.this);
        }

    }


    protected void syncronizeUpdated() {

        isDebugEnabled = pref.getString(KEYS.DEBUG_TAG, "");

        if (SynchroteamUitls
                .isNetworkAvailable(SyncoteamNavigationActivity.this)) {

            new syncronizeAsyncTask().execute();

        } else {
            SynchroteamUitls.showToastMessage(SyncoteamNavigationActivity.this);
        }

    }


    // .......................................ASYNC...CLASS...STARTS...HERE..............................................

    /**
     * Async class to add a new invoice or quotation
     */
    private class syncronizeAsyncTask extends
            AsyncTaskCoroutine<String, String> {


        @Override
        public void onPreExecute() {
            super.onPreExecute();
            executeOnResume = false;
//            if (!isFinishing()) {
//                showProgressDialog();
//            }

            DialogUtils.showProgressDialog((Activity) SyncoteamNavigationActivity.this,
                    getString(R.string.textPleaseWaitLable),
                    getString(R.string.msg_synch),
                    false, true);
        }

        @Override
        public String doInBackground(String... params) {
            String result = "";
            try {
                dataAccessObject.sync(user.getLogin(), user.getPwd());

                GestionAcces gt = dataAccessObject.getAcces();
                if (gt != null && gt.getOptionTaracking() == 0) {
                    stopTracking();
                }

                l = new ArrayList<>();
                l = RoomDBSingleTone.instance(SyncoteamNavigationActivity.this).roomDao().getAllNotificationEventModels();
                Log.e("TRIDENT", "the list is >>>>  "+ l.size());

                if (l != null && l.size()>0){
                    for (int i=0;i<l.size();i++){
                        JSONObject jsonObj = new JSONObject(l.get(i).value);

                        hitNotificationEventService(jsonObj.getString("IdJob"),jsonObj.getInt("IdCustomer"),
                                jsonObj.getInt("JobStatus"),l.get(i).id);
                        Log.e("TRIDENT","THE JSON IS >>>>>" + jsonObj.getString("IdCustomer"));
                    }

                }



                result = "ok";

            } catch (Exception ex) {
                ex.printStackTrace();
                Logger.printException(ex);
                String exception = ex.getMessage();
                if (exception != null) {
                    if (!isDebugEnabled.equalsIgnoreCase("true")) {
                        if (exception.indexOf("4001") != -1) {
                            result = "4001";
                        } else if (exception.indexOf("4000") != -1) {
                            result = "4000";
                        } else if (exception.indexOf("4006") != -1) {
                            result = "4006";
                        } else if (exception.indexOf("4101") != -1) {
                            result = "4101";
                        } else if (exception.indexOf("4005") != -1) {
                            result = "4005";
                        } else if (exception.indexOf("4003") != -1) {
                            result = "4003";
                        } else {
                            result = "Error";
                        }
                    } else {
                        result = exception;
                    }
                } else {
                    result = "Error";
                }
            }
            return result;
        }

        @Override
        public void onPostExecute(String result) {
            super.onPostExecute(result);

            if (SyncoteamNavigationActivity.this != null && !isFinishing())
                DialogUtils.dismissProgressDialog();

            String erreur = result;

//            dataAccessObject.getTagsTest();

            if (erreur.equals("ok")) {
                // show / hide the clock in out when the user have clock in out function
                checkClockModeAvailabilityAndSettingLayout(dataAccessObject.checkIsClockInAvailable(user.getId()));

                Toast.makeText(SyncoteamNavigationActivity.this,
                        getString(R.string.msg_synch_ok), Toast.LENGTH_LONG)
                        .show();
                BaseFragment baseFragment = SyncoteamNavigationActivity.this
                        .getCurrentFragment();
                if (baseFragment == null) {
                    baseFragment = (BaseFragment) SyncoteamNavigationActivity.this
                            .getSupportFragmentManager().findFragmentByTag(
                                    "CurrentJobFragment");

                }

                if (baseFragment != null)
                    baseFragment.doOnSync();
                dateLastSyncronizationTv.setText(SharedPref
                        .gettimeSyncronised(SyncoteamNavigationActivity.this));

                switchGestion();

                //new changes
                try {
                    checkPrevCurrentTimeOutValue();
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.log(TAG, "AUTO_CLOCK_OUT Exception ------>" + e);
                }

            } else if (erreur.equals("4001") || erreur.equals("4000")) {
                if (SyncoteamNavigationActivity.this != null &&
                        !SyncoteamNavigationActivity.this.isFinishing())
                    showAuthErrDialog();
            } else if (erreur.equals("4003")) {
                if (SyncoteamNavigationActivity.this != null &&
                        !SyncoteamNavigationActivity.this.isFinishing())
                    showErrMsgDialog(getString(R.string.msg_sync_error_4003));
            } else if (erreur.equals("4006")) {
                if (SyncoteamNavigationActivity.this != null &&
                        !SyncoteamNavigationActivity.this.isFinishing())
                    showErrMsgDialog(getString(R.string.msg_synch_error_4));
            } else if (erreur.equals("4101")) {
                if (SyncoteamNavigationActivity.this != null &&
                        !SyncoteamNavigationActivity.this.isFinishing())
                    showMultipleDeviecDialog();
            } else if (erreur.equals("4005")) {
                if (SyncoteamNavigationActivity.this != null &&
                        !SyncoteamNavigationActivity.this.isFinishing())
                    showAppUpdatetDialog();
            } else {
                if (erreur.equals("Error")) {
                    if (SyncoteamNavigationActivity.this != null &&
                            !SyncoteamNavigationActivity.this.isFinishing())
                        showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));
                } else {
                    if (SyncoteamNavigationActivity.this != null &&
                            !SyncoteamNavigationActivity.this.isFinishing())
                        showErrMsgDialog(erreur);
                }
            }
            executeOnResume = true;

        }

    }

    private void checkPrevCurrentTimeOutValue() throws Exception {
        String currentAutoClockOut = dataAccessObject.getAutoClockOutTime();
        String prevAutoClockOut = SharedPref.getPreviousTimeOut(SyncoteamNavigationActivity.this);

        if (prevAutoClockOut != null && currentAutoClockOut != null) {
            Logger.log(TAG, "AUTO_CLOCK_OUT currentAutoClockOut ------>" + currentAutoClockOut);
            Logger.log(TAG, "AUTO_CLOCK_OUT prevAutoClockOut ------>" + prevAutoClockOut);

            if (currentAutoClockOut.trim().equals(prevAutoClockOut.trim())) {
                SharedPref.setPreviousTimeOut(currentAutoClockOut, SyncoteamNavigationActivity.this);
            } else {
                ClockInOutUtil.cancelAlarmForTimeOut(SyncoteamNavigationActivity.this);

                ClockInOutUtil.saveLastClockedInTime(SyncoteamNavigationActivity.this);
                ClockInOutUtil.startAlarmForTimeOut(SyncoteamNavigationActivity.this);
            }
        }

    }

    private class DeleteJobFromDatabase extends
            AsyncTaskCoroutine<String, String>{
        String jobId;
        public DeleteJobFromDatabase(String jobId){
            this.jobId = jobId;
        }
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            executeOnResume = false;
        }

        @Override
        public String doInBackground(String... params) {
            String result = "";
            Logger.log("TRIDENT", "DELETING THE JOB ID :" + jobId);
            RoomDBSingleTone.instance(SyncoteamNavigationActivity.this).roomDao().deteteIDNotificationEventModels(jobId);
            return result;
        }

        @Override
        public void onPostExecute(@Nullable String result) {
            super.onPostExecute(result);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.synchroteam.baseactivity.SyncroTeamBaseActivity#onBackPressed()
     */
    @Override
    public void onBackPressed() {

        setTitleProperty(0);
        seleted_fragment_position = 0;
        findViewById(lastListItemSelectedId).setBackgroundResource(
                R.color.grayNavigationDrawerListItemUnSelected);
        findViewById(R.id.currentJobsListItem).setBackgroundResource(
                R.color.grayNavigationDrawerListItemSelected);
        lastListItemSelectedId = R.id.currentJobsListItem;

        layBottomBar.setVisibility(View.VISIBLE);

        super.onBackPressed();
        //
        // int i = getSupportFragmentManager().getBackStackEntryCount();
        //
        //
        // if(i==0){
        // super.onBackPressed();
        // }else{

        getSupportFragmentManager().popBackStackImmediate();
        // }

    }

    /**
     * The handler. handleMessage() of this handler is called after user
     * manually perform Sync operation the hadler is responsible for calling
     * doOnSync() method of the fragment currently visble so that its data get
     * updated with the latest data of database.
     */
    @SuppressLint("HandlerLeak")
    private Handler synchroteamNavigationActivityHandler = new Handler() {
        public void handleMessage(Message msg) {
            String erreur = (String) msg.obj;
            if (erreur.equals("ok")) {
                // show / hide the clock in out when the user have clock in out function
                checkClockModeAvailabilityAndSettingLayout(dataAccessObject.checkIsClockInAvailable(user.getId()));

                Toast.makeText(SyncoteamNavigationActivity.this,
                        getString(R.string.msg_synch_ok), Toast.LENGTH_LONG)
                        .show();
                BaseFragment baseFragment = SyncoteamNavigationActivity.this
                        .getCurrentFragment();
                if (baseFragment == null) {
                    baseFragment = (BaseFragment) SyncoteamNavigationActivity.this
                            .getSupportFragmentManager().findFragmentByTag(
                                    "CurrentJobFragment");

                }

                if (baseFragment != null)
                    baseFragment.doOnSync();
                dateLastSyncronizationTv.setText(SharedPref
                        .gettimeSyncronised(SyncoteamNavigationActivity.this));

                switchGestion();

            } else if (erreur.equals("4001") || erreur.equals("4000")) {
                if (SyncoteamNavigationActivity.this != null &&
                        !SyncoteamNavigationActivity.this.isFinishing())
                    showAuthErrDialog();
            } else if (erreur.equals("4003")) {
                if (SyncoteamNavigationActivity.this != null &&
                        !SyncoteamNavigationActivity.this.isFinishing())
                    showErrMsgDialog(getString(R.string.msg_sync_error_4003));
            } else if (erreur.equals("4006")) {
                // Toast.makeText(SyncoteamNavigationActivity.this,
                // getString(R.string.msg_synch_error_4),
                // Toast.LENGTH_LONG).show();
                if (SyncoteamNavigationActivity.this != null &&
                        !SyncoteamNavigationActivity.this.isFinishing())
                    showErrMsgDialog(getString(R.string.msg_synch_error_4));
            } else if (erreur.equals("4101")) {
                // Toast.makeText(SyncoteamNavigationActivity.this,
                // getString(R.string.msg_synch_error_2),
                // Toast.LENGTH_LONG).show();
                if (SyncoteamNavigationActivity.this != null &&
                        !SyncoteamNavigationActivity.this.isFinishing())
                    showMultipleDeviecDialog();
            } else if (erreur.equals("4005")) {
                // Toast.makeText(SyncoteamNavigationActivity.this,
                // getString(R.string.msg_synch_error_3),
                // Toast.LENGTH_LONG).show();
                if (SyncoteamNavigationActivity.this != null &&
                        !SyncoteamNavigationActivity.this.isFinishing())
                    showAppUpdatetDialog();
            } else {
                if (erreur.equals("Error")) {
                    // Toast.makeText(SyncoteamNavigationActivity.this,
                    // getString(R.string.msg_synch_error_new),
                    // Toast.LENGTH_LONG).show();
                    if (SyncoteamNavigationActivity.this != null &&
                            !SyncoteamNavigationActivity.this.isFinishing())
                        showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));
                } else {
                    // Toast.makeText(SyncoteamNavigationActivity.this, erreur,
                    // Toast.LENGTH_LONG).show();
                    if (SyncoteamNavigationActivity.this != null &&
                            !SyncoteamNavigationActivity.this.isFinishing())
                        showErrMsgDialog(erreur);
                }
            }

        }
    };

    /**
     * Show app update dialog
     */
    protected void showAppUpdatetDialog() {

        AppUpdateDialog appUpdateDialog = new AppUpdateDialog(this);

        appUpdateDialog.show();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.synchroteam.synchroteam.CommonInterface#updateUi()
     */
    public void updateUi() {

        Logger.log("Synchroteam Navigation ", " updateUi called");

        new UpdateJobCountUI(true).execute();


        Toast.makeText(SyncoteamNavigationActivity.this,
                getString(R.string.msg_synch_ok), Toast.LENGTH_LONG).show();

        BaseFragment baseFragment = SyncoteamNavigationActivity.this
                .getCurrentFragment();

        if (baseFragment == null) {
            baseFragment = (BaseFragment) SyncoteamNavigationActivity.this
                    .getSupportFragmentManager().findFragmentByTag(
                            "CurrentJobFragment");
        }

        if (baseFragment != null)
            baseFragment.doOnSync();

        dateLastSyncronizationTv.setText(SharedPref
                .gettimeSyncronised(SyncoteamNavigationActivity.this));

    }

    public void updateUiOnTrakingStatusChange(boolean isRunning) {

        if (isRunning) {
            trackingIndicatorIv
                    .setImageResource(R.drawable.tracking_indicator_enabled);
        } else {
            trackingIndicatorIv
                    .setImageResource(R.drawable.tracking_indicator_disabled);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.synchroteam.baseactivity.SyncroTeamBaseActivity#getActivityInstance()
     */
    @Override
    public Activity getActivityInstance() {
        return this;
    }

    /**
     * Logout.
     */
    private void logout() {
        //dataAccessObject.resetDateLogin();
        dataAccessObject.clearPassword(user.getLogin());
        Intent intent = new Intent(this, Login_Syncroteam.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent autoSyncIntent = ((SyncroTeamApplication) SyncoteamNavigationActivity.this
                .getApplication()).getAutoSyncIntent();
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(
//                SyncoteamNavigationActivity.this, 0, autoSyncIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent pendingIntent;
        //Behaviour changes supporting android 12
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // Create a PendingIntent using FLAG_IMMUTABLE
            pendingIntent = PendingIntent.getBroadcast(
                    SyncoteamNavigationActivity.this, 0, autoSyncIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {

            pendingIntent = PendingIntent.getBroadcast(
                    SyncoteamNavigationActivity.this, 0, autoSyncIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        alarmManager.cancel(pendingIntent);

        mNotificationManager.cancel(1986);

        startActivity(intent);

        finish();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.synchroteam.fragment.BaseFragment.ListUpdateListener#onListUpdate()
     */
    @Override
    public synchronized void onListUpdate() {

        Logger.log("Synchroteam Navigation", "onListUpdate Called");

        if (dataAccessObject == null) {
            return;
        }

        new UpdateJobCountUI(false).execute();

    }

    /**
     * Adds the button visibility.
     *
     * @param visibility the visibility
     * @param showAdd
     */
    public void addButtonVisibility(int visibility, boolean showAdd) {
        if (flag != 1) {
            addJobBtn.setVisibility(View.GONE);
        } else {
            addJobBtn.setVisibility(visibility);
        }

        if (showAdd)
            addJobBtn.setVisibility(visibility);

    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.notif_black : R.drawable.icon_notif_new;
    }

    /**
     * Creat notification.
     */
    @SuppressWarnings("static-access")
    private void creatNotification() {

        int icon = R.drawable.icon_notif_new;

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
                    notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        } else {

            intent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);
        }
//        PendingIntent intent = PendingIntent.getActivity(this, 0,
//                notificationIntent, 0);
        Notification notification = null;

        @SuppressLint("RemoteViewLayout") RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.notification_item);
        notificationLayout.setTextViewText(R.id.txt_time,
                DateUtils.formatDateTime(this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME));
        notificationLayout.setTextViewText(R.id.tv_msg, "test \n test");

        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel("Noti_channel", "Notification",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("");
            notificationManager.createNotificationChannel(channel);

            notification = new NotificationCompat.Builder(this, "Noti_channel")
                    .setSmallIcon(getNotificationIcon())
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(notificationLayout)
                    .setContentIntent(intent)
                    .build();
        } else {

            notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(getNotificationIcon())
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(notificationLayout)
                    .setContentIntent(intent).build();
        }

        notification.defaults = Notification.DEFAULT_ALL;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        mNotificationManager.notify(1986, notification);
    }


    /**
     * Register to push server.
     */
    public void registerToPushServerNew() {

        try {
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.w("FirebaseMessaging", "Fetching FCM registration token failed", task.getException());
                                return;
                            }

                            // Get new FCM registration token
                            String token = task.getResult();
                            Log.e("newToken--", token);
                            if (!TextUtils.isEmpty(token)) {
                                dataAccessObject.setIdPushAndroid(token);
                                SharedPreferences.Editor editor = getSharedPreferences(KEY,
                                        Context.MODE_PRIVATE).edit();
                                editor.putString(REGISTRATION_KEY, token);
                                editor.apply();
                            }
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            Logger.printException(e);
        }
    }

    /**
     * Sets the alarm manager to auto syncronise data.
     */
    @SuppressLint("NewApi")
    protected void setAlarmManagerToAutoSyncroniseData() {

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        Intent intent = ((SyncroTeamApplication) getApplication())
                .getAutoSyncIntent();

        int interval = SharedPref.getSyncInterval(this);

        intent.putExtra(KEYS.SyncronizationSettings.SYNC_INTERVAL, interval);

//        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent,
//                PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent alarmIntent;
        //Behaviour changes supporting android 12
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // Create a PendingIntent using FLAG_IMMUTABLE
            alarmIntent = PendingIntent.getBroadcast(
                    SyncoteamNavigationActivity.this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        } else {

            alarmIntent = PendingIntent.getBroadcast(
                    SyncoteamNavigationActivity.this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        if (currentapiVersion < android.os.Build.VERSION_CODES.KITKAT) {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + interval * 60 * 1000,
                    interval * 60 * 1000, alarmIntent);

        } else {

//            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                    SystemClock.elapsedRealtime() + interval * 60 * 1000,
//                    alarmIntent);

            //Android 14 changes
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                            SystemClock.elapsedRealtime() + interval * 60 * 1000,
                            alarmIntent);
                } else {
                    this.startActivity(new Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM));

                }
            }else
            //android 'O' changes
            if (Build.VERSION.SDK_INT >= 23) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + interval * 60 * 1000,
                        alarmIntent);
            } else if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + interval * 60 * 1000,
                        alarmIntent);
            } else {
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + interval * 60 * 1000,
                        alarmIntent);
            }

        }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v4.app.FragmentActivity#onSaveInstanceState(android.os
     * .Bundle)
     */
    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("lastListItemSelectedId", lastListItemSelectedId);
        outState.putInt("seleted_fragment_position", seleted_fragment_position);

    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
     */
    @Override
    protected void onRestoreInstanceState(@NotNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            lastListItemSelectedId = savedInstanceState
                    .getInt("lastListItemSelectedId");
            seleted_fragment_position = savedInstanceState
                    .getInt("seleted_fragment_position");

        }

    }

    /**
     * Sets the previous settings after device presets.
     */
    private void setPreviousSettingsAfterDevicePresets() {
        currentJobs
                .setBackgroundResource(R.color.grayNavigationDrawerListItemUnSelected);

        findViewById(lastListItemSelectedId).setBackgroundResource(
                R.color.grayNavigationDrawerListItemSelected);

        switch (seleted_fragment_position) {
            case 0:

                setTitleProperty(0);
                addButtonVisibility(View.VISIBLE, false);
                break;
            case 1:

                setTitleProperty(1);
                addButtonVisibility(View.VISIBLE, false);
                break;

            case 3:

                setTitleProperty(3);
                addButtonVisibility(View.GONE, false);
                break;

            case 2:

                setTitleProperty(2);
                addButtonVisibility(View.VISIBLE, false);

                break;
            case 4:

                setTitleProperty(4);
                addButtonVisibility(View.GONE, false);

                break;
            case 5:

                setTitleProperty(5);
                addButtonVisibility(View.GONE, false);

                break;
            case 8:

                setTitleProperty(8);
                addButtonVisibility(View.GONE, false);
                break;
            case 9:

                setTitleProperty(9);
                addJobBtn.setVisibility(View.GONE);
                break;

            case 10:
                setTitleProperty(10);
                addJobBtn.setVisibility(View.GONE);
                break;
            case 11:
                setTitleProperty(11);
                addJobBtn.setVisibility(View.GONE);
                break;
            case 12:
                setTitleProperty(12);
                addJobBtn.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * On event.
     *
     * @param updateTrackingIndicator the update tracking indicator
     */
    public void onEvent(UpdateTrackingIndicator updateTrackingIndicator) {

        Logger.log("Tracking Indicator", "Updated");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Logger.log("Sychnroteam navigationActivity", "runOnUiThread");

                boolean isNetEnabled = Build.FINGERPRINT.contains("generic") ? true : locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

                if (SharedPref
                        .getIsTrackcingRunning(SyncoteamNavigationActivity.this)
                        && (locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER) || isNetEnabled)) {

                    trackingIndicatorIv
                            .setImageResource(R.drawable.tracking_indicator_enabled);


                } else {
                    trackingIndicatorIv
                            .setImageResource(R.drawable.tracking_indicator_disabled);

//                    boolean isTrackingServiceRunning = SharedPref.getIsTrackcingRunning(SyncoteamNavigationActivity.this);
//                    if (isTrackingServiceRunning) {
//                        stopTracking();
//                    }
//                    trackingOnOffTb.setTag(Boolean.valueOf(false));
//                    trackingOnOffTb.setBackgroundResource(R.drawable.traking_off_btn);

                }

            }
        });

    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        ObservableObject.getInstance().deleteObserver(this);
        dismissProgressDialog();
        super.onDestroy();
        unregisterReceiver(dateUpdateReceiver);
    }



    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status

            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode,
                    this, 0);
            dialog.show();

            return false;
        }
    }

    /**
     * Show multiple user dialog
     */
    protected void showMultipleDeviecDialog() {

        MultipleDeviceNotSupported multipleDeviceDialog = new MultipleDeviceNotSupported(
                SyncoteamNavigationActivity.this,
                new MultipleDeviceNotSupported.MultipleDeviceInterface() {

                    @Override
                    public void doOnOkClick() {
                    }

                    @Override
                    public void doOnLinkClick() {
                        if (Locale.getDefault().getLanguage()
                                .equalsIgnoreCase("fr")) {
                            openLinkInBrowser(getString(R.string.txtInfoFr));
                        } else if (Locale.getDefault().getLanguage()
                                .equalsIgnoreCase("es")) {
                            openLinkInBrowser(getString(R.string.txtInfoEs));
                        } else {
                            openLinkInBrowser(getString(R.string.txtInfoEn));
                        }
                    }
                });
        multipleDeviceDialog.show();
    }

    /**
     * Show error dialog
     */
    protected void showErrMsgDialog(String errMsg) {

        ErrorDialog errDialog = new ErrorDialog(
                SyncoteamNavigationActivity.this, errMsg);

        errDialog.show();
    }

    /**
     * For showing the synchronization failure messages
     */
    protected void showSyncFailureMsgDialog(String syncFailureMsg) {

        if (SyncoteamNavigationActivity.this != null && !SyncoteamNavigationActivity.this.isFinishing()) {

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SynchronizationErrorDialog synchronizationErrorDialog = new SynchronizationErrorDialog
                            (SyncoteamNavigationActivity.this, syncFailureMsg,
                                    new SynchronizationErrorDialog.SynchronizationErrorInterface() {
                                        @Override
                                        public void doOnOkayClick() {
                                            //perform any action
                                        }
                                    });

                    synchronizationErrorDialog.show();
                }
            });

        }

    }

    /**
     * Show authentication error dialog
     */
    protected void showAuthErrDialog() {

        AuthenticationErrorDialog authenticationErrorDialog = new AuthenticationErrorDialog(
                SyncoteamNavigationActivity.this, user.getLogin());

        authenticationErrorDialog.show();
    }

    /***
     * Create a chooser of browsers to open the link
     *
     * @param link
     */
    protected void openLinkInBrowser(String link) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(link));

        // Always use string resources for UI text. This says something like
        // "Share this photo with"
        String title = getString(R.string.titleBrowserSelection);
        // Create and start the chooser
        Intent chooser = Intent.createChooser(intent, title);
        startActivity(chooser);
    }

    /**
     * Sync only if the app was not logged in today.
     */
    private void syncAfterADay() {

        if (SharedPref.getIsNotLoggedInToday(this) || SharedPref.getScriptUpdated(this)) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    //new changes
                    syncronizeUpdated();

                }
            }, 1000);

            SharedPref.setIsNotLoggedInToday(false, this);
        }
    }

    /**
     * Hitting sync server service for next day to get the service link of domain
     * also calls when app migration
     *
     * @param domain
     */
    private void hitSyncServerForNextDay(final String domain) {

        progressDSynch = ProgressDialog.show(SyncoteamNavigationActivity.this,
                getString(R.string.textPleaseWaitLable),
                getString(R.string.msg_synch), true, false);

        ApiInterface apiService =
                Apiclient.getClient().create(ApiInterface.class);

        Call<SyncService> call = apiService.synchronizeServer(domain);
        call.enqueue(new Callback<SyncService>() {
            @Override
            public void onResponse(@NotNull Call<SyncService> call, @NotNull Response<SyncService> response) {

                try {

                    // Saving Server, port, ssl from Response to Shared Preferences
                    assert response.body() != null;
                    SharedPref.setSyncStdServer(response.body().getStd().getServer(), SyncoteamNavigationActivity.this);
                    SharedPref.setSyncStdPort(response.body().getStd().getPort(), SyncoteamNavigationActivity.this);
                    SharedPref.setSyncStdSsl(response.body().getStd().getSsl(), SyncoteamNavigationActivity.this);

                    SharedPref.setSyncDebugServer(response.body().getDebug().getServer(), SyncoteamNavigationActivity.this);
                    SharedPref.setSyncDebugPort(response.body().getDebug().getPort(), SyncoteamNavigationActivity.this);
                    SharedPref.setSyncDebugSsl(response.body().getDebug().getSsl(), SyncoteamNavigationActivity.this);

                    dismissProgressDialog();

                    syncAfterADay();

                } catch (Exception e) {
                    dismissProgressDialog();

                    if (checkLinkValuesFromPref())
                        syncAfterADay();
                    else {
                        if (SyncoteamNavigationActivity.this != null &&
                                !SyncoteamNavigationActivity.this.isFinishing())
                            showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));
                    }
                }

            }

            @Override
            public void onFailure(@NotNull Call<SyncService> call, @NotNull Throwable t) {
//                        Logger.log("SyncNextDay", "Retrofit failure :" + t);

                // dismiss dialog
                dismissProgressDialog();

                if (checkLinkValuesFromPref())
                    syncAfterADay();
                else {
                    if (SyncoteamNavigationActivity.this != null &&
                            !SyncoteamNavigationActivity.this.isFinishing())
                        showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));
                }
            }
        });
    }

    private Boolean checkLinkValuesFromPref() {
        return SharedPref.getSyncStdServer(SyncoteamNavigationActivity.this) != null && SharedPref.getSyncStdPort(SyncoteamNavigationActivity.this) != 0;
    }


    // new functionality v48

    public void checkClockModeAvailabilityAndSettingLayout(boolean isClockModeAvailable) {
        if (isClockModeAvailable) {
            txtClockMode.setVisibility(View.VISIBLE);
            // check clocked in or not
            Conge vectCongeClockIn = checkClockedIn();
            if (vectCongeClockIn != null) {
                txtClockMode.setBackgroundColor(getResources().getColor(R.color.green_color));
            } else {
                txtClockMode.setBackgroundColor(getResources().getColor(R.color.red_color));
            }
        } else {
            txtClockMode.setVisibility(View.GONE);
        }
    }

    public Conge checkUnAvailabilityStarted() {
        Conge indisp;
        Vector<Conge> vectConge = new Vector<Conge>();
        vectConge = dataAccessObject.getCongeNewExceptClockInActivity();
        Enumeration<Conge> enindisp = vectConge.elements();
        while (enindisp.hasMoreElements()) {
            indisp = enindisp.nextElement();
            if (indisp.getDtFin() == null) {
                return indisp;
            }
        }
        return null;
    }

    public Conge checkClockedIn() {
        Conge indisp;
        Vector<Conge> vectConge = dataAccessObject.getClockIn();
        Enumeration<Conge> enindisp = vectConge.elements();
        while (enindisp.hasMoreElements()) {
            indisp = enindisp.nextElement();
            if (indisp.getDtFin() == null) {
                return indisp;
            }
        }
        return null;
    }

    // event triggered from activity mode dialog
    public void onEvent(ActivityUpdateEvent event) {
        // checking if activity is started
        Conge unAvailabilityDetail = checkUnAvailabilityStarted();
        if (unAvailabilityDetail == null) {
            setBottomLayoutJobActivityView(-1, null, false, false, null);
        } else {
            setBottomLayoutJobActivityView(-1, null, true, false, unAvailabilityDetail.getNomTypeConge());
        }

        // Setting clock in out UI
        checkClockModeAvailabilityAndSettingLayout(dataAccessObject.checkIsClockInAvailable(user.getId()));

    }

    // event triggered from driving mode dialog
    public void onEvent(DrivingModeUpdateEvent event) {

        //new changes 51
        TravelActivity travelActivity = dataAccessObject.isDrivingStarted();
        if (travelActivity == null) {
            setBottomLayoutJobActivityView(-1, null, false, false, null);
        } else {
            setBottomLayoutJobActivityView(-1, null, false, true, travelActivity.getTravelName());
        }

        // Setting clock in out UI
        checkClockModeAvailabilityAndSettingLayout(dataAccessObject.checkIsClockInAvailable(user.getId()));

    }

    public void onEvent(JobTypeDialogEvent typeDialogEvent) {
        if (typeDialogEvent.getEventAction() != 0) {
            switch (typeDialogEvent.getEventAction()) {
                case KEYS.JobTypeDialog.KEY_TODAY_JOB:
                    if (currentJobs != null && typeDialogEvent.getEventHasMultipleJobs()) {
                        currentJobs.performClick();
                    } else {
                        JobDetailsModel model = dataAccessObject.getAllAvailableJobsToday(todayDate);
                        if (model != null) {
                            navigateToJobDetailActivity(model);
                        }
                    }
                    break;
                case KEYS.JobTypeDialog.KEY_UPCOMING_JOB:
                    if (toComeJobs != null && typeDialogEvent.getEventHasMultipleJobs()) {
                        toComeJobs.performClick();
                    } else {
                        JobDetailsModel model = dataAccessObject.getToComeJobDetails();
                        if (model != null) {
                            navigateToJobDetailActivity(model);
                        }
                    }
                    break;
                case KEYS.JobTypeDialog.KEY_LATE_JOB:
                    if (deadLineExceededJobs != null && typeDialogEvent.getEventHasMultipleJobs()) {
                        deadLineExceededJobs.performClick();
                    } else {
                        JobDetailsModel model = dataAccessObject.getDeadlineExceededJobDetails();
                        if (model != null) {
                            navigateToJobDetailActivity(model);
                        }
                    }
                    break;
            }
        }
    }


    public void navigateToJobDetailActivity(JobDetailsModel model) {
        Bundle bundle = new Bundle();
        String[] numInterv = model.getJobType()
                .split("-");
        String nmInterv = "";
        if (numInterv != null && numInterv.length == 2)
            nmInterv = numInterv[0].substring(1);
        else
            nmInterv = "0";

        String jobNo = null;

        if (!TextUtils.isEmpty(model.getRefCustomer()))
            jobNo = "#" + model.getRefCustomer()
                    + " - " + model.getJobType();
        else if (model.getJobNumber() != 0)
            jobNo = "#" + model.getJobNumber()
                    + " - " + model.getJobType();

        bundle.putString(KEYS.CurrentJobs.ID,
                model.getIdJob());

        bundle.putInt(KEYS.CurrentJobs.CD_STATUS, model.getJobStatus());
        bundle.putString(KEYS.CurrentJobs.ID_USER,
                String.valueOf(model.getJobUserId()));
        bundle.putString(KEYS.CurrentJobs.ID_MODEL,
                model.getIdModel());
        bundle.putString(KEYS.CurrentJobs.LAT,
                model.getLat());
        bundle.putString(KEYS.CurrentJobs.LON,
                model.getLon());
        bundle.putString("NumIntevType", nmInterv);
        bundle.putString(KEYS.CurrentJobs.NOMSITE,
                model.getSiteName());
        bundle.putString(KEYS.CurrentJobs.NOMEQUIPMENT,
                model.getEquipmentName());
        bundle.putInt(KEYS.CurrentJobs.IDSITE,
                model.getIdSite());
        bundle.putInt(KEYS.CurrentJobs.IDCLIENT, model.getIdEquipment());
        bundle.putInt(KEYS.CurrentJobs.IDEQUIPMENT, model.getIdEquipment());
        bundle.putString(KEYS.CurrentJobs.TYPE,
                jobNo);
        bundle.putBoolean(KEYS.CurrentJobs.IDSTARTJOB, false);
        Intent jobDetailIntent = new Intent(this,
                JobDetails.class);
        jobDetailIntent.putExtras(bundle);
        startActivity(jobDetailIntent);
    }

    public void onClickJobInClockInOutDialog() {
//        JobDetailsModel model = dataAccessObject.getDetailOfTodayPausedJob();

        //new logic
        JobDetailsModel model = dataAccessObject.getCurrentActiveJobs();
        if (model != null) {
            Bundle bundle = new Bundle();
            String[] numInterv = model.getJobType()
                    .split("-");
            String nmInterv = "";
            if (numInterv.length == 2)
                nmInterv = numInterv[0].substring(1);
            else
                nmInterv = "0";

            String jobNo = null;

            if (!TextUtils.isEmpty(model.getRefCustomer()))
                jobNo = "#" + model.getRefCustomer()
                        + " - " + model.getJobType();
            else if (model.getJobNumber() != 0)
                jobNo = "#" + model.getJobNumber()
                        + " - " + model.getJobType();

            bundle.putString(KEYS.CurrentJobs.ID,
                    model.getIdJob());

            bundle.putInt(KEYS.CurrentJobs.CD_STATUS, model.getJobStatus());
            bundle.putString(KEYS.CurrentJobs.ID_USER,
                    String.valueOf(model.getJobUserId()));
            bundle.putString(KEYS.CurrentJobs.ID_MODEL,
                    model.getIdModel());
            bundle.putString(KEYS.CurrentJobs.LAT,
                    model.getLat());
            bundle.putString(KEYS.CurrentJobs.LON,
                    model.getLon());
            bundle.putString("NumIntevType", nmInterv);
            bundle.putString(KEYS.CurrentJobs.NOMSITE,
                    model.getSiteName());
            bundle.putString(KEYS.CurrentJobs.NOMEQUIPMENT,
                    model.getEquipmentName());
            bundle.putInt(KEYS.CurrentJobs.IDSITE,
                    model.getIdSite());
            bundle.putInt(KEYS.CurrentJobs.IDCLIENT, model.getIdEquipment());
            bundle.putInt(KEYS.CurrentJobs.IDEQUIPMENT, model.getIdEquipment());
            bundle.putString(KEYS.CurrentJobs.TYPE,
                    jobNo);
            bundle.putBoolean(KEYS.CurrentJobs.IDSTARTJOB, false);

            Intent jobDetailIntent = new Intent(this,
                    JobDetails.class);
            jobDetailIntent.putExtras(bundle);
            startActivity(jobDetailIntent);
        } else {
            Toast.makeText(this, R.string.no_jobs_were_found, Toast.LENGTH_SHORT).show();
        }
    }

    // Event bus to pause/stop JOB from Dialog
    public void onEvent(JobPauseFinishEvent event) {
        //todo comment and check if the scenario is working correctly
//        if (event.getEventAction().equals(KEYS.JobDetails.KEY_PAUSE_JOB)) {
//            pauseJob(idIntervention, idUser);
//        } else if (event.getEventAction().equals(KEYS.JobDetails.KEY_FINISH_JOB)) {
//            stopJob(idIntervention, idUser);
//        } else {
//            Logger.log("jobDetails", "invalid action");
//        }
        settingJobActivityNameInBar();
    }

    public boolean checkInterventionAndGettingIdInterv() {
        idUser = dataAccessObject.getUser().getId();
        idIntervention = dataAccessObject.getStartedInterventionIDToPauseStop(idUser);
        return idIntervention != null;
    }

    public void setBottomLayoutJobActivityView(int jobStatus, String jobNumber, boolean isUnAvailabilityStarted, boolean isDrivingStarted, String unAvailabilityName) {
        if (jobStatus == KEYS.JObDetail.JOB__STARTED) {
            txtActivityStopView.setText(getString(R.string.fa_stop));
            txtStartActivity.setText(getJobNumber(jobNumber));
            txtActivityStopView.setVisibility(View.VISIBLE);
        } else if (isDrivingStarted) {
            txtActivityStopView.setText(getString(R.string.fa_stop));
            txtStartActivity.setText(unAvailabilityName);
            txtActivityStopView.setVisibility(View.VISIBLE);
        } else if (isUnAvailabilityStarted) {
            txtActivityStopView.setText(getString(R.string.fa_stop));
            txtStartActivity.setText(unAvailabilityName);
            txtActivityStopView.setVisibility(View.VISIBLE);
        } else {
//            txtStartActivity.setText(getResources().getString(R.string.tt_start));
            txtStartActivity.setText(getResources().getString(R.string.textStartLable));

//            txtActivityStopView.setVisibility(View.GONE);
            txtActivityStopView.setText(getString(R.string.fa_play));
            txtActivityStopView.setVisibility(View.VISIBLE);
        }
    }

    // Event bus removing the service
    public void onEvent(TravelStopEvent event) {

        EventBus.getDefault().post(new RefreshListEvent());
        BaseFragment baseFragment = SyncoteamNavigationActivity.this
                .getCurrentFragment();
        if (baseFragment != null && baseFragment instanceof CurrentJobFragment) {
            try {
                baseFragment.doOnSync();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //unregister dialog
        Intent serviceIntent = new Intent(this, LocationService.class);
        serviceIntent.setAction(LocationService.ACTION_STOP);

        startService(serviceIntent);
    }

    private String getJobNumber(String jobNumber) {
//        return getResources().getString(R.string.intervention) + " " + jobNumber;
        return jobNumber;
    }

    protected void pauseJob(String idIntervention, int idUser) {

        if (dataAccessObject.updateStatutInterv(4, idIntervention))
            dataAccessObject.setTimeClotIntervention(idIntervention, idUser + "", "");
        EventBus.getDefault().post(new UpdateJobDetailUi());
        if (dataAccessObject.checkSynchronisation(3) == 1)
            synch();
        else
            specialSynch();

    }

    public void onEvent(ClockedOutEvent event) {

//        String idInterv = dataAccessObject.getStartedJobId();
//        if (idInterv != null && idInterv.length() > 0) {
//
//            new SuspendInterInDBAsync(idInterv).execute();
//        }


        if (dataAccessObject.checkSynchronisation(3) == 1)
            synch();
        else
            specialSynch();

    }


    /**
     * Stop job.
     */
    protected void stopJob(String idIntervention, int idUser) {

        if (checkObligGlobale(idIntervention) == 1) {

            if (dataAccessObject.updateStatutInterv(5, idIntervention)) {
                dataAccessObject.setRealEndDate(idIntervention);
                dataAccessObject.setTimeClotInterventionForJobFinish(idIntervention, idUser + "");
            }

            if (dataAccessObject.checkSynchronisation(4) == 1) {

                synch();
            } else
                specialSynch();
        } else
            Toast.makeText(SyncoteamNavigationActivity.this, R.string.msg_saisie_oblig,
                    Toast.LENGTH_LONG).show();
    }

    /**
     * Check oblig globale. - Version 4.2 (Rapport v2)
     *
     * @return the int
     */
    public int checkObligGlobale(String idIntervention) {
        Vector<Families> vect = dataAccessObject.getSBCategory(idIntervention);
//        Vector<Families> vect = dataAccessObject.getAllFamiliesWithSB(Integer
//                .parseInt(idModel), idIntervention);
        Iterator<Families> iter = vect.iterator();
        while (iter.hasNext()) {
            Families fm = iter.next();
            if (CheckObligPartial(dataAccessObject.getAllItem(idIntervention,
                    fm.getIdFamily(), fm.getIteration())) == 0)
                return 0;
        }

        return 1;
    }

    /**
     * Duplicate method of checkObligGlobale
     * Check all the mandatory fields are filled
     *
     * @return
     */
    public boolean isAllMandatoryFieldsAreFilled() {
        Vector<Families> vect = dataAccessObject.getSBCategory(idIntervention);
//        Vector<Families> vect = dataAccessObject.getAllFamiliesWithSB(Integer
//                .parseInt(idModel), idIntervention);
        Iterator<Families> iter = vect.iterator();
        while (iter.hasNext()) {
            Families fm = iter.next();
            if (CheckObligPartial(dataAccessObject.getAllItem(idIntervention,
                    fm.getIdFamily(), fm.getIteration())) == 0)
                return false;
        }

        return true;
    }

    /**
     * Check oblig partial.
     *
     * @param map the map
     * @return the int
     */
    public int CheckObligPartial(HashMap<Integer, Item> map) {

        int drp = 0;

        Iterator<Item> iter = map.values().iterator();
        while (iter.hasNext()) {
            Item item1 = new Item(), item2, item3;
            item1 = iter.next();
            // Logger.log(TAG, "item1 : " + item1.getNomItem());

            if (item1.getCond() == 0) {
                if (item1.getOblig() == 1)
                    if (item1.getValItem() == 0) {
                        return 0;
                    }
                // // if ((item1.getValItem() == 0)
                // // && (item1.getIdTypeItem() != 7))// Code To be
                // // removed in V3.3
                // return 0;

            } else {
                item2 = new Item(item1.getCond(), item1.getVal_cond(),
                        item1.getValeurNet());

                // --------------PREVIOUS CODE--------------
                // while (item2.getCond() != 0) {
                //
                // item3 = map.get(item2.getCond());
                //
                // Logger.log(TAG, "item3 : " + item3.getNomItem());
                //
                // if (item2.getVal_cond().equals(item3.getValeurNet())) {
                // item2 = new Item(item3.getCond(), item3.getVal_cond(),
                // item3.getValeurNet());
                // drp = 1;
                // } else {
                // drp = 0;
                // item2.setCond(0);
                // }
                // }

                // -------------PREVIOUS CODE--------------

                // --------------NEW CHANGES--------------

                item3 = map.get(item2.getCond());
                if (RecursiveCompare(item2, item2, item3, map)) {
                    drp = 1;
                } else {
                    drp = 0;
                }
                // if (CompareValues(item2, item3)) {
                // if (item3.getCond() != 0) {
                // if (RecursiveCompare(item2, item3,
                // map.get(item3.getCond()), map)) {
                // drp = 1;
                // } else {
                // drp = 0;
                // }
                // }
                //
                // } else {
                // drp = 0;
                // }
                // --------------NEW CHANGES--------------

                if (drp == 1) {
                    if (item1.getOblig() == 1)
                        if (item1.getValItem() == 0) {
                            return 0;
                        }

                    drp = 0;
                }
            }

        }

        return 1;
    }

    /**
     * Recursive compare.
     *
     * @param itemR the item r
     * @param itemF the item f
     * @param itemP the item p
     * @param map   the map
     * @return the boolean
     */
    public Boolean RecursiveCompare(Item itemR, Item itemF, Item itemP,
                                    HashMap<Integer, Item> map) {

        try {
            if (CompareValues(itemF, itemP)) {

                if (itemP.getCond() != 0) {

                    return RecursiveCompare(itemR, itemP, map.get(itemP.getCond()),
                            map);
                } else
                    return true;
            } else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Compare values.
     *
     * @param itemF the item f
     * @param itemP the item p
     * @return the boolean
     */
    public Boolean CompareValues(Item itemF, Item itemP) throws Exception {
        Boolean drp = false;

        if (itemP.getValItem() == 1 || itemP.getIdTypeItem() == 7
                || itemP.getIdTypeItem() == 8 || itemP.getIdTypeItem() == 9) {
            String operator = "", value = "";
            Date dF, dP;

            switch (itemP.getIdTypeItem()) {
                case 3:
                    operator = itemF.getVal_cond().substring(0, 1);
                    value = itemF.getVal_cond().substring(1);
                    dF = getDateF(value);
                    dP = getDateF(itemP.getValeurNet());

                    if (operator.equals("=")) {
                        if (dF.getTime() == dP.getTime())
                            drp = true;
                    } else if (operator.equals("<")) {
                        if (dF.getTime() > dP.getTime())
                            drp = true;
                    } else if (operator.equals(">")) {
                        if (dF.getTime() < dP.getTime())
                            drp = true;
                    }

                    break;
                case 4:
                    operator = itemF.getVal_cond().substring(0, 1);
                    value = itemF.getVal_cond().substring(1);
                    dF = getTimes(value);
                    dP = getTimes(itemP.getValeurNet());

                    if (operator.equals("=")) {
                        if (dF.getTime() == dP.getTime())
                            drp = true;
                    } else if (operator.equals("<")) {
                        if (dF.getTime() > dP.getTime())
                            drp = true;
                    } else if (operator.equals(">")) {
                        if (dF.getTime() < dP.getTime())
                            drp = true;
                    }

                    break;
                case 5:
                    operator = itemF.getVal_cond().substring(0, 1);
                    Double val = Double.parseDouble(itemF.getVal_cond()
                            .substring(1));

                    Double valp;
                    try {
                        valp = Double.parseDouble(itemP.getValeurNet());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        valp = 0.00;

                    }

                    if (operator.equals("=")) {

                        if (val.equals(valp))
                            drp = true;
                    } else if (operator.equals("<")) {
                        if (val > valp)
                            drp = true;
                    } else if (operator.equals(">")) {
                        if (val < valp)
                            drp = true;
                    }

                    break;
                case 6:
                    break;
                case 7:
                    if (itemF.getVal_cond().equals("1")) {
                        if (itemP.getValeurNet().equals("1"))
                            drp = true;
                    } else if (itemF.getVal_cond().equals("2")) {
                        if (itemP.getValeurNet().equals("2")
                                || itemP.getValeurNet().equals(""))
                            drp = true;
                    }

                    break;
                case 8:
                    if (itemF.getVal_cond().equals("1")) {
                        if (itemP.getValeurNet().equals("1"))
                            drp = true;
                    } else if (itemF.getVal_cond().equals("2")) {
                        if (itemP.getValeurNet().equals("2")
                                || itemP.getValeurNet().equals(""))
                            drp = true;
                    }

                    break;
                case 9:
                    if (itemF.getVal_cond().equals("1")) {
                        if (!itemP.getValeurNet().equals(""))
                            drp = true;
                    } else if (itemF.getVal_cond().equals("2")) {
                        if (itemP.getValeurNet().equals("2")
                                || itemP.getValeurNet().equals(""))
                            drp = true;
                    }

                    break;

                default:
                    if (itemF.getVal_cond().equals(itemP.getValeurNet()))
                        drp = true;
                    break;
            }
        }
        return drp;
    }

    /**
     * Gets the date f.
     *
     * @param mDate the m date
     * @return the date f
     */
    public Date getDateF(String mDate) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS", Locale.US);
        Date date;
        try {
            date = df.parse(mDate + " 00:00:00.000");
            return date;
        } catch (ParseException e) {
            Logger.printException(e);
            return new Date();
        }
    }

    /**
     * Gets the times.
     *
     * @param mDate the m date
     * @return the times
     */
    public Date getTimes(String mDate) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS", Locale.US);
        Date date;
        try {
            date = df.parse("01/01/0001 " + mDate + ":00.000");
            return date;
        } catch (ParseException e) {
            Logger.printException(e);
            return new Date();
        }
    }


    // synch

    /**
     * Synch.
     */
    public void synch() {

        if (SynchroteamUitls.isNetworkAvailable(SyncoteamNavigationActivity.this)) {
            progressDSynch = ProgressDialog.show(SyncoteamNavigationActivity.this,
                    getString(R.string.textPleaseWaitLable),
                    getString(R.string.msg_synch), true, false);
            Logger.output(TAG, " thread started");

            Thread syncDbToServer = new Thread((new Runnable() {

                @Override
                public void run() {

                    Message myMessage = new Message();
                    try {
                        User u = dataAccessObject.getUser();
                        dataAccessObject.sync(u.getLogin(), u.getPwd());
                        Logger.output(TAG, " finished sync");
                        Thread.sleep(3000);
                        myMessage.obj = "ok";

                        handler.sendMessage(myMessage);

                    } catch (Exception ex) {
                        String exception = ex.getMessage();
                        Logger.printException(ex);
                        if (exception != null) {
                            if (exception.indexOf("4001") != -1) {
                                myMessage.obj = "4001";
                            } else if (exception.indexOf("4000") != -1) {
                                myMessage.obj = "4000";
                            } else if (exception.indexOf("4006") != -1) {
                                myMessage.obj = "4006";
                            } else if (exception.indexOf("4101") != -1) {
                                myMessage.obj = "4101";
                            } else if (exception.indexOf("4003") != -1) {
                                myMessage.obj = "4003";
                            } else if (exception.indexOf("4005") != -1) {
                                myMessage.obj = "4005";
                            } else {
                                myMessage.obj = "Error";
                            }
                        } else {
                            myMessage.obj = "Error";
                        }

                        handler.sendMessage(myMessage);

                    } finally {
                        if (progressDSynch != null
                                && progressDSynch.isShowing())
                            progressDSynch.dismiss();

                    }

                }
            }));
            syncDbToServer.start();
        } else {

            SynchroteamUitls.showToastMessage(SyncoteamNavigationActivity.this);

        }

    }

    /**
     * The handler.
     */
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            String erreur = (String) msg.obj;
            EventBus.getDefault().post(new UpdateDataBaseEvent());

            // show / hide the clock in out when the user have clock in out function
            checkClockModeAvailabilityAndSettingLayout(dataAccessObject.checkIsClockInAvailable(user.getId()));

            if (erreur.equals("ok")) {
                if (SyncoteamNavigationActivity.this != null &&
                        !SyncoteamNavigationActivity.this.isFinishing())
                    Toast.makeText(SyncoteamNavigationActivity.this,
                            getString(R.string.msg_synch_ok), Toast.LENGTH_LONG)
                            .show();

            } else if (erreur.equals("4001")) {
                if (SyncoteamNavigationActivity.this != null &&
                        !SyncoteamNavigationActivity.this.isFinishing())
                    showAuthErrDialog();
            } else if (erreur.equals("4000")) {
                if (SyncoteamNavigationActivity.this != null &&
                        !SyncoteamNavigationActivity.this.isFinishing())
                    showErrMsgDialog(getString(R.string.msg_synch_error_4));
            } else if (erreur.equals("4006")) {
                if (SyncoteamNavigationActivity.this != null &&
                        !SyncoteamNavigationActivity.this.isFinishing())
                    Toast.makeText(SyncoteamNavigationActivity.this,
                            getString(R.string.msg_synch_error_4),
                            Toast.LENGTH_LONG).show();

            } else if (erreur.equals("4101")) {
                if (SyncoteamNavigationActivity.this != null &&
                        !SyncoteamNavigationActivity.this.isFinishing())
                    showMultipleDeviecDialog();
            } else if (erreur.equals("4005")) {
                if (SyncoteamNavigationActivity.this != null &&
                        !SyncoteamNavigationActivity.this.isFinishing())
                    showAppUpdatetDialog();
            } else if (erreur.equals("4003")) {
                if (SyncoteamNavigationActivity.this != null &&
                        !SyncoteamNavigationActivity.this.isFinishing())
                    showErrMsgDialog(getString(R.string.msg_sync_error_4003));
            } else {
//                Toast.makeText(SyncoteamNavigationActivity.this,
//                        getString(R.string.msg_synch_error_new), Toast.LENGTH_LONG)
//                        .show();
                if (SyncoteamNavigationActivity.this != null &&
                        !SyncoteamNavigationActivity.this.isFinishing())
                    showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));

            }

        }
    };

    /**
     * Special synch.
     */
    public void specialSynch() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.ask_synch_msg)
                .setCancelable(false)
                .setPositiveButton(R.string.textYesLable,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                synch();
                            }
                        })
                .setNegativeButton(R.string.textNoLable,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();

                                EventBus.getDefault().post(
                                        new UpdateDataBaseEvent());

                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * This method will triggers for clock in time out
     *
     * @param observable
     * @param o
     */
    @Override
    public void update(Observable observable, Object o) {
        boolean isClockModeAvailable = dataAccessObject.checkIsClockInAvailable(user.getId());
        if (isClockModeAvailable) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
            Calendar cal = Calendar.getInstance();
            String currentDate = sdf.format(cal.getTime());

            clockOutAndUpdateUI(isClockModeAvailable, currentDate);
            //fires event for job details screen
            EventBus.getDefault().post(new ClockInTimeOutEvent());
        }
    }

    /**
     * updates clock out in db and update the ui
     *
     * @param isClockModeAvailable
     */
    private void clockOutAndUpdateUI(boolean isClockModeAvailable, String currentDate) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
//        Calendar cal = Calendar.getInstance();

        Vector<Conge> vectConge = dataAccessObject.getClockIn();
        Enumeration<Conge> enindisp = vectConge.elements();
//        String currentDate = sdf.format(cal.getTime());

        //dismiss other dialogs and show clock out dialog
        if (isActive) {
            // check clocked in or not
            if (vectConge.size() > 0) {
                CommonUtils.dismissAllDialogs(getSupportFragmentManager());
                ClockInTimeOutDialog.newInstance().show(getSupportFragmentManager(), "");
            }
        }
        //update clock out time
        while (enindisp.hasMoreElements()) {
            Conge indisp = enindisp.nextElement();
            if (indisp.getDtFin() == null) {

                suspendCurrentInterIfActive(currentDate);

                dataAccessObject.updateClockedOutEndTime(indisp.getIdConge(), currentDate);
            }
        }
        //change clock in icon
        if (isActive) {
            checkClockModeAvailabilityAndSettingLayout(isClockModeAvailable);
        }
    }

    private void suspendCurrentInterIfActive(String currentDate) {
        String idInterv = dataAccessObject.getStartedJobId();
        if (idInterv != null && idInterv.length() > 0) {
            if (dataAccessObject.updateStatutInterv(4, idInterv))
                dataAccessObject.setTimeClotIntervention(idInterv, user.getId() + "", currentDate);

            if (isActive)
                EventBus.getDefault().post(new ClockedOutEvent());
        }
    }

    /**
     * checks if the user have been inactive
     *
     * @param isClockModeAvailable
     */
    private void checkTimeOutInitially(boolean isClockModeAvailable) {
        Conge vectCongeClockIn = checkClockedIn();
        Logger.log(TAG, "AUTO_CLOCK_OUT prevAutoClockOut ------>" + SharedPref.getTimeOutEnabled(this));
        boolean isTimeOutEnabled = SharedPref.getTimeOutEnabled(this);
        if (isTimeOutEnabled && vectCongeClockIn != null) {
            //get clocked in time from preference and check if it has exceeded the clock out time.

            String clockedInTime = SharedPref.getClockedInTime(this);
            if (clockedInTime != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
                try {
                    Date clockedInDate = sdf.parse(clockedInTime);

                    Logger.log("ClockInOutUtil", "Auto clockOut Time CURRENT DATE--->" +
                            new Date().toString());
                    assert clockedInDate != null;
                    Logger.log("ClockInOutUtil", "Auto clockOut Time CLOCKED DATE--->" +
                            clockedInDate.toString());

                    long diff = new Date().getTime() - clockedInDate.getTime();
                    long diffInMins = TimeUnit.MILLISECONDS.toMinutes(diff);
                    Dao dao = DaoManager.getInstance();
                    String minutes = dao.getAutoClockOutTime();
                    if (minutes != null && minutes.length() > 0) {
                        Long autoClockOutMin = Long.parseLong(minutes);

                        Logger.log("ClockInOutUtil", "Auto clockOut Time in min is--->" + minutes);
                        Logger.log("ClockInOutUtil", "Auto clockOut Time dialog remaining time " +
                                "is--->" +
                                diffInMins);

                        if (diffInMins > autoClockOutMin) {
                            Date updatedCLockOutDate = addMinutesToDate(autoClockOutMin, clockedInDate);
                            SimpleDateFormat sdfCLockOutDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
                            String clockOutDate = sdfCLockOutDate.format(updatedCLockOutDate.getTime());
                            clockOutAndUpdateUI(isClockModeAvailable, clockOutDate);
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * Add clock out minutes to the saved date and return the updated clock out time to save in database
     *
     * @param minutes
     * @param beforeTime
     * @return
     */
    private Date addMinutesToDate(long minutes, Date beforeTime) {
        final long ONE_MINUTE_IN_MILLIS = 60000;

        long curTimeInMs = beforeTime.getTime();
        Date afterAddingMins = new Date(curTimeInMs + (minutes * ONE_MINUTE_IN_MILLIS));
        Logger.log(TAG, "updated clock out min is :" + afterAddingMins);

        return afterAddingMins;
    }


    private class UpdateJobCountUI extends AsyncTaskCoroutine<Void, Void> {

        private int msgCount, unavbCount;
        private String countJobPool = "0";
        private String toComeJobCnt = "0";

        boolean isUpdateUI = false;

        GestionAcces gt;


        public UpdateJobCountUI(boolean isUpdateUI) {
            this.isUpdateUI = isUpdateUI;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();

            if (progressDSynch == null) {
                progressDSynch = ProgressDialog.show(SyncoteamNavigationActivity.this,
                        getString(R.string.textPleaseWaitLable),
                        getString(R.string.chargement), true, false);
                progressDSynch.show();
            }else if (progressDSynch != null && !progressDSynch.isShowing()) {
                progressDSynch.show();
            }

        }


        @Override
        public Void doInBackground(Void... params) {

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DATE, -30);
            String lastDate = dateFormat.format(calendar.getTime());

            Calendar calendarx = Calendar.getInstance(TimeZone.getDefault());
            Date dateSelected = calendarx.getTime();

            if (user != null) {

                if (!isUpdateUI) {
                    gt = dataAccessObject.getAcces();
                }

                mCountTodayJob =dataAccessObject.getTodaysJobCount(dateSelected,user.getId(),true);

                mCountLate = dataAccessObject.getLateJobCount(user.getId());

                toComeJobCnt = dataAccessObject.getToComeJobCount();

                mCountUpComing = Integer.parseInt(toComeJobCnt);

                msgCount = dataAccessObject.getNbreMsg();

                flag = dataAccessObject.getAddIntervFlag();

                mCountReport = Integer.parseInt(dataAccessObject.getReportsCountNew(lastDate));

                unavbCount = dataAccessObject.getUnavabilityCounts();

                countJobPool = dataAccessObject.getToJobPoolCount();

            }


            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (progressDSynch != null
                    && progressDSynch.isShowing())
                progressDSynch.dismiss();

            if (!isUpdateUI) {
                //setting the visibility of the add job button
                if (seleted_fragment_position < 4 ||
                        seleted_fragment_position == 9 ||
                        seleted_fragment_position == 13) {
                    if (flag != 1) {
                        addJobBtn.setVisibility(View.GONE);
                    } else {
                        addJobBtn.setVisibility(View.VISIBLE);
                    }
                } else {
                    addJobBtn.setVisibility(View.GONE);
                }

                if (seleted_fragment_position == 9)
                    addJobBtn.setVisibility(View.VISIBLE);

                if (gt != null && gt.getOptionTaracking() == 0) {
                    trackingListItem.setVisibility(View.GONE);
                    trackingOnOffTb.setTag(Boolean.valueOf(false));
                    trackingOnOffTb.setBackgroundResource(R.drawable.traking_off_btn);
                } else {
                    trackingListItem.setVisibility(View.VISIBLE);
                }

                //setting the visibility of job pool
                if (countJobPool != null && countJobPool.trim().length() > 0) {
                    if (Integer.parseInt(countJobPool) <= 0) {
                        jobPool.setVisibility(View.GONE);
                    } else {
                        jobPool.setVisibility(View.VISIBLE);
                    }
                    jobPoolCount.setText(countJobPool + "");
                } else {
                    jobPool.setVisibility(View.GONE);
                }
            }

            //updating the count values
            messagesCount.setText(msgCount + "");
            currentJobCount.setText(mCountTodayJob + "");
            dedlineExceededCount.setText(mCountLate + "");
            toComeCount.setText(mCountUpComing + "");
            unavabilityCount.setText(unavbCount + "");
            reportsCount.setText(mCountReport + "");
            allJobCount.setText(mCountTodayJob + mCountUpComing + mCountLate + mCountReport + "");

        }

    }

    public class OnResumeAsynTask extends AsyncTaskCoroutine<Void, Void> {

        TravelActivity travelActivity;
        Boolean checkIsClockInAvailableState;

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            DialogUtils.showProgressDialog(SyncoteamNavigationActivity.this,
                    SyncoteamNavigationActivity.this.getString(R.string.textWaitLable),
                    SyncoteamNavigationActivity.this.getString(R.string.textProgressDialogFetchNearestClient), false);
            checkNetworkAndGPS();

        }

        @Override
        public Void doInBackground(Void... voids) {

            if (dataAccessObject != null && user != null) {

                // show / hide the clock in out when the user have clock in out function
                checkIsClockInAvailableState = dataAccessObject.checkIsClockInAvailable(user.getId());
            }

            // --------------------------------------- v48 ---------------------------------------------

            // changes
            DateChecker.checkDateAndNavigate(SyncoteamNavigationActivity.this, dataAccessObject);
            travelActivity = dataAccessObject.isDrivingStarted();

            return null;
        }

        @Override
        public void onPostExecute(Void unused) {
            super.onPostExecute(unused);

            checkClockModeAvailabilityAndSettingLayout(checkIsClockInAvailableState);


            Conge unAvailabilityDetail = checkUnAvailabilityStarted();
            if (unAvailabilityDetail != null) {
                setBottomLayoutJobActivityView(-1, null, true, false, unAvailabilityDetail.getNomTypeConge());
            } else if (travelActivity != null) {
                //new changes 51
                setBottomLayoutJobActivityView(-1, null, false, true, travelActivity.getTravelName());
            } else {
                setBottomLayoutJobActivityView(-1, null, false, false, null);
            }

            if (SharedPref
                    .getPreviousValueOfTracking(SyncoteamNavigationActivity.this)) {
                trackingOnOffTb.setTag(Boolean.valueOf(true));
                trackingOnOffTb.setBackgroundResource(R.drawable.traking_on_btn);
                daoTracking.setSessiondata("tracking", "on");
                dataAccessObject.updateFL_GPS_TRACKED(idUser, true);


            } else {
                trackingOnOffTb.setTag(Boolean.valueOf(false));
                trackingOnOffTb.setBackgroundResource(R.drawable.traking_off_btn);
                daoTracking.setSessiondata("tracking", "off");
                dataAccessObject.updateFL_GPS_TRACKED(idUser, false);


            }

            dateLastSyncronizationTv.setText(SharedPref
                    .gettimeSyncronised(SyncoteamNavigationActivity.this));

            if (syncroTeamApplication.getIsLocaleChenged()) {
                setPreviousSettingsAfterDevicePresets();
                syncroTeamApplication.setIsLocaleChanged(false);
            }
            if (servicesConnected()) {
                if (SharedPref
                        .getIsTrackcingRunning(SyncoteamNavigationActivity.this)) {

                    trackingIndicatorIv
                            .setImageResource(R.drawable.tracking_indicator_enabled);

                } else {

                    trackingIndicatorIv
                            .setImageResource(R.drawable.tracking_indicator_disabled);
                }
            } else {
                trackingIndicatorIv
                        .setImageResource(R.drawable.tracking_indicator_disabled);

            }

            DialogUtils.dismissProgressDialog();

        }

    }

    BroadcastReceiver dateUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            EventBus.getDefault().post(new SelectTodayDate());
        }
    };

    void hitNotificationEventService(String idJobCreated, int idCustomer, int jobStatus,String idLocalDb) {
//        ApiInterface apiService =
//                Apiclient.getClient().create(ApiInterface.class);

        String authUserName = dataAccessObject.getUserDomain() + "_" + user.getLogin();
        String authPassword = user.getPwd();
        ApiInterface apiService = ApiClientNew.createService(ApiInterface.class, authUserName, authPassword);

        String url = SharedPref.getNotiUrlServer(this);



        String currentDateUtc = getCurrentUtcDate();

        Logger.log("TRIDENT","SERVICE NOTIFICAITON RESUEST====>"+url);

        jsonInfo infoObject = new jsonInfo();
        int idJobType = dataAccessObject.getTypeIntervention(idJobCreated);
        ArrayList<String> list = dataAccessObject.getInterventionTimeDetails(idJobCreated);
        ArrayList<Integer> clientDetails = dataAccessObject.getInterventionClientDetails(idJobCreated);
        infoObject.setIdJobType(idJobType);
        infoObject.setIdTech(user.getId());

        if (clientDetails.get(0) != null && clientDetails.get(0) >= 1)
            infoObject.setIdClient(clientDetails.get(0));

        if (clientDetails.get(1) != null && clientDetails.get(1) >= 1)
            infoObject.setIdSite(clientDetails.get(1));

        if (clientDetails.get(2) != null && clientDetails.get(2) >= 1)
            infoObject.setIdEquipement(clientDetails.get(2));

        if (list.get(0) != null && list.get(0).length() > 0)
            infoObject.setStartedScheduledDateTime(list.get(0));

        if (list.get(1) != null && list.get(1).length() > 0)
            infoObject.setCompletedScheduledDateTime(list.get(1));

        if (list.get(2) != null && list.get(2).length() > 0)
            infoObject.setStartedRealisedDateTime(list.get(2));

        if (list.get(3) != null && list.get(3).length() > 0)
            infoObject.setCompletedRealisedDateTime(list.get(3));


        Logger.log("TRIDENT", "EventNotiResult currentDateUtc--->" + currentDateUtc);
        // old
//        EventNotiRequest eventNotiRequest = new EventNotiRequest(idCustomer, idJobCreated,
//                jobStatus, "tech", currentDateUtc, infoObject);

        // new changes - for notification
        EventNotiRequest eventNotiRequest = new EventNotiRequest(idCustomer, idJobCreated,
                jobStatus, "tech", currentDateUtc,clientDetails.get(0),clientDetails.get(1),
                clientDetails.get(2),idJobType,user.getId(),
                list.get(0),list.get(1),list.get(2),list.get(3));
        Logger.log("TRIDENT", "EventNotiResult EventNotiRequest--->" + eventNotiRequest.toString());

        Gson gson = new Gson();
        String json = gson.toJson(eventNotiRequest);
        Logger.log("TRIDENT", "SERVICE CALL REQUEST URL--->" + url);
        Logger.log("TRIDENT", "SERVICE CALL REQUEST JSON--->" + json);


        Call<EventNotiResult> call = apiService.notificationEventService(url, eventNotiRequest);
        call.enqueue(new Callback<EventNotiResult>() {
            @Override
            public void onResponse(Call<EventNotiResult> call, Response<EventNotiResult> response) {


                if (response.isSuccessful()) {
                    int status = response.body().getResult();

                    Logger.log("TRIDENT", "SERVICE CALL RESULT --->" + response);
                    Logger.log("TRIDENT", "EventNotiResult RESPONSE VALUE :" + response);
                    Logger.log("TRIDENT", "EventNotiResult RESPONSE VALUE status:" + status);

                    if (status == 1) {
                        Logger.log("TRIDENT", "EventNotiResult success :" + status);
                        new DeleteJobFromDatabase(idLocalDb).execute();
                    } else {
                        Logger.log("TRIDENT", "EventNotiResult failure :" + status);
                    }
                } else {
                    Logger.log("TRIDENT", "EventNotiResult failure :" + response);
                }
            }

            @Override
            public void onFailure(Call<EventNotiResult> call, Throwable t) {
                Logger.log("TRIDENT", "EventNotiResult Exception :" + t);

            }

        });
    }

    public String getCurrentUtcDate() {
        String currentDate;
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        currentDate = dateFormat.format(date);
        return currentDate;
    }


}