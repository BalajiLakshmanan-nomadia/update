package com.synchroteam.dialogs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.synchroteam.beans.Conge;
import com.synchroteam.beans.LocationPoints;
import com.synchroteam.beans.UnavailabilityBeans;
import com.synchroteam.beans.UpdateDataBaseEvent;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.events.DrivingModeUpdateEvent;
import com.synchroteam.roomDB.RoomDBSingleTone;
import com.synchroteam.roomDB.entity.LocationCoordinates;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.tracking2.LocationService;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.ClockInOutUtil;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DaoTrackingManager;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.LocationUtils;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.SharedPref;
import com.synchroteam.utils.SynchroteamUitls;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.Vector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import de.greenrobot.event.EventBus;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.RECEIVER_EXPORTED;
import static com.synchroteam.utils.LocationUtils.stringKMorMi;


/**
 * Created by Trident
 */

public class DrivingModeDialogUpdated extends DialogFragment implements View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private ProgressDialog progressDSynch;
    private static final String TAG = ClockJobTravelActDialog.class.getSimpleName();
    private TextView txtDriving, txtTripInKm, txtOK, txtUndo, txtArrived;
    private LinearLayout layDrivingConfirmation, layDrivingArrived, layClose;
    //    private Typeface typeFaceEuros;
    Context context;
    private Dao dataAccessObject;
    private String idInterv, idUser;
    private User user;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
    private Calendar cal;
    private String drivingUniqueID = null;
    private int drivingTypeID;
    private Intent serviceIntent;
    private Handler autoStartHandler;
    private Runnable runnableAutoStart;

    private ArrayList<LocationPoints> locationList;
    private static final String TRAVEL_ID = "travel_id";
    private static final String TYPE_ID = "type_id";
    private static final String TRAVEL_NAME = "travel_name";
    DecimalFormat numberFormat;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 1245;

    boolean isAlreadyStarted = false;
    String travelActivityName = "";

    private long mStartBtnClickTime;
    double currentLat = 0.00;
    double currentLong = 0.00;
    boolean activityCreatedDB = false;
    boolean isPayable = false;
    double distValue = 0.00;

    /**
     * The m location request.
     */
    private LocationRequest mLocationRequest;

    /**
     * The location client.
     */
    private GoogleApiClient locationClient;

    /**
     * The location.
     */
    Location location;
    /**
     * The my timer.
     */
    private Timer myTimer;

    /**
     * The location manager.
     */
    protected LocationManager locationManager;

    String unit ;

    public static DrivingModeDialogUpdated newInstance(String travelId, int idType, String travelName) {

        DrivingModeDialogUpdated fragment = new DrivingModeDialogUpdated();
        Bundle args = new Bundle();
        args.putString(TRAVEL_ID, travelId);
        args.putInt(TYPE_ID, idType);
        args.putString(TRAVEL_NAME, travelName);
        fragment.setArguments(args);

        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_driving_mode, container);

        context = getActivity();

        numberFormat = new DecimalFormat();

        initializeLocationListeners();

        initializeUI(view);


        Logger.log(TAG, "on create view called");


        return view;
    }


    private void initializeLocationListeners() {
        // locationClient = new LocationClient(this, this, this);
        locationClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        mLocationRequest = LocationRequest.create();
        distValue = 0.00;
        /*
         * Set the update interval
         */
        mLocationRequest
                .setInterval(SynchroteamUitls.UPDATE_INTERVAL_IN_MILLISECONDS);

        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
        mLocationRequest
                .setFastestInterval(SynchroteamUitls.FAST_INTERVAL_CEILING_IN_MILLISECONDS);
    }

//    @Override
//    public void show(FragmentManager manager, String tag) {
//        try {
//            FragmentTransaction ft = manager.beginTransaction();
//            ft.add(this, tag).addToBackStack(null);
//            ft.commitAllowingStateLoss();
//        } catch (IllegalStateException e) {
//            com.synchroteam.utils.Logger.log(TAG, "IllegalStateException ----->" + e);
//        }
//    }

    @Override
    public void onStart() {
        super.onStart();

        /*
         * Connect the client. Don't re-start any requests here; instead, wait
         * for onResume()
         */
        locationClient.connect();

        //new changes
        try {
            getActivity().registerReceiver(broadcastReceiver,
                    new IntentFilter(LocationService.BROADCAST_ACTION), RECEIVER_EXPORTED);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return (keyCode == KeyEvent.KEYCODE_BACK);
            }
        });
        return dialog;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_ok:
                if (SystemClock.elapsedRealtime() - mStartBtnClickTime < 1500) {
                    return;
                }
                mStartBtnClickTime = SystemClock.elapsedRealtime();

                if (!checkDrivingPermission()) {
                    callingPermissionLocation();
                } else {
                    SharedPref.setIsTravelLastEntry(false, getActivity());
                    geocoder(view);
                }

                break;
            case R.id.txt_undo:

                if (autoStartHandler != null && runnableAutoStart != null)
                    autoStartHandler.removeCallbacks(runnableAutoStart);
                ClockJobTravelActDialog.newInstance(false).show(getActivity().getSupportFragmentManager(), "");
                dismiss();
                break;
            case R.id.txt_arrived:

                SharedPref.setIsTravelLastEntry(true, getActivity());
                stopLocationService();

                break;
            case R.id.lay_close:
                dismiss();
                break;
        }
    }

    private void getLatLongAndAddInDB() {

        startDrivingAndCalcDistance();
    }

    private void createAndInsertValue(double currentLat, double currentLong) {
        SharedPref.setIsTravelLastEntry(false, getActivity());
        DialogUtils.dismissProgressDialog();
        if (!CheckClockInOutModeActivity(isPayable)) {
            AddActivityInsertLatLongInDB();
        }
    }

    /**
     * clock in/out change when the driving is payable/non-payable
     * Payable means clocked in
     * Non Payable means clocked out
     *
     * @param isPayableDriving
     */
    private boolean CheckClockInOutModeActivity(boolean isPayableDriving) {

        boolean errorClockedIn = false;
        if (dataAccessObject.checkIsClockInAvailable(user.getId())) {
            if (isPayableDriving) {
                /*
                 * Payable driving
                 * Perform clock in if its clocked out
                 * Checking clock in
                 */
                Conge vectorCongeClockIn = checkClockedIn();
                if (vectorCongeClockIn != null) {
                    // Already clocked in. Do nothing
                    errorClockedIn = false;
                } else {
                    /*
                     * Clocked out, do clock in for the payable driving, New logic subtract 1
                     * min from current time for clock in time and update event.
                     * */
                    cal = Calendar.getInstance();
                    cal.add(Calendar.SECOND, -1);
                    String currentDateStr = sdf.format(cal.getTime());
                    // new clocked in entry to T_CONGE
                    UnavailabilityBeans clockInActivity = dataAccessObject.getClockInActivity();
                    String uniqueID = dataAccessObject.addUnavailabilityAndReturnID(null,
                            clockInActivity.getUnavailabilityID(), 0, currentDateStr, null, "");
                    if (uniqueID != null) {
                        showToastMessage(getActivity().getResources().getString(R.string.txt_clock_in));
                        EventBus.getDefault().post(new DrivingModeUpdateEvent());
                    } else {
                        errorClockedIn = true;
                        showToastMessage(getActivity().getResources().getString(R.string.msg_error));
                    }

                }
            } else {
                /*
                 * Non payable Driving
                 * Perform clock out if its already clocked in
                 * Checking clock in
                 */
                Conge vectorCongeClockIn = checkClockedIn();
                if (vectorCongeClockIn != null) {
                    /*
                     * Clocked in, do clock out for the non payable driving, New logic add 1
                     *  min from current time for clock out time and update event
                     * */
                    if (finishClockedInMode()) {
                        showToastMessage(getActivity().getResources().getString(R.string.txt_clock_out));
                        EventBus.getDefault().post(new DrivingModeUpdateEvent());
                    }

                }
            }
        }

        return errorClockedIn;
    }


    private void AddActivityInsertLatLongInDB() {

        //create driving activity in database and insert values
        new AddLatLongAndDrivingInDBAsync().execute(currentLat, currentLong);
    }

    private boolean checkDrivingPermission() {
        boolean result;
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            result = false;
        } else {
            result = true;
        }
        return result;
    }


    private void initializeUI(View view) {
        dataAccessObject = DaoManager.getInstance();
        cal = Calendar.getInstance();
        idInterv = dataAccessObject.getStartedJobId();
        assert getArguments() != null;
        drivingUniqueID = getArguments().getString(TRAVEL_ID);
        idUser = " ";
        user = dataAccessObject.getUser();

        locationList = new ArrayList<>();
        txtDriving = (TextView) view.findViewById(R.id.txt_driving);
        txtTripInKm = (TextView) view.findViewById(R.id.txt_trip_in_km);
        txtOK = (TextView) view.findViewById(R.id.txt_ok);
        txtUndo = (TextView) view.findViewById(R.id.txt_undo);
        txtArrived = (TextView) view.findViewById(R.id.txt_arrived);
        layDrivingConfirmation = (LinearLayout) view.findViewById(R.id.lay_driving_confirmation);
        layDrivingArrived = (LinearLayout) view.findViewById(R.id.lay_driving_arrived);
        layClose = (LinearLayout) view.findViewById(R.id.lay_close);

        drivingTypeID = getArguments().getInt(TYPE_ID);
        txtDriving.setText(getArguments().getString(TRAVEL_NAME));

        //check if the activity is payable or not.
        isPayable = dataAccessObject.checkActivityOrDrivingIsPayable(drivingTypeID);

        //new changes 51
        if (getArguments().getString(TRAVEL_NAME) != null && getArguments().getString(TRAVEL_NAME).length() > 0) {
            travelActivityName = getArguments().getString(TRAVEL_NAME);
            SharedPref.setRunningTravelName(travelActivityName, getActivity());
        }

        if (drivingUniqueID != null && drivingUniqueID.length() > 0)
            SharedPref.setDistanceEnabled(true, getActivity());

        SharedPref.setIsTravelLastEntry(false, getActivity());

        txtOK.setOnClickListener(this);
        txtUndo.setOnClickListener(this);
        txtArrived.setOnClickListener(this);
        layClose.setOnClickListener(this);


        //get the previously saved distance if the distance tracking is enabled
        if (SharedPref.getDistanceEnabled(getActivity())) {

            //new changes v50
            SharedPref.setIsTravelRunning(true, getActivity());

            //v51.0.2
            new Thread(new Runnable() {
                @Override
                public void run() {

                    List<LocationCoordinates> locations = new ArrayList<>();
                    locations = RoomDBSingleTone.instance(getActivity())
                            .LocationCoordinatesDao().getAllLocationCoordinatesModels();
                    if (locations.size() > 0) {
                        for (LocationCoordinates loc : locations) {
                            LocationPoints loc1 = new LocationPoints(loc.getLatitude(), loc.getLongitude());
                            locationList.add(loc1);
                            currentLat = loc.getLatitude();
                            currentLong = loc.getLongitude();
                        }
                        final double distanceTravelled = calculateTotalDistance();
                        if (getActivity() != null && !getActivity().isFinishing())
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    initializeDecimalFormat();

                                    String calDist = "0.00";
                                    try {
                                        calDist = numberFormat.format(distanceTravelled);
                                    } catch (Exception e) {
                                        calDist = "" + distanceTravelled;
                                    }

                                    unit = stringKMorMi();
                                    txtTripInKm.setText(getActivity().getString(R.string.txt_trip) + ": " + calDist + " "+unit);

                                }
                            });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                unit = stringKMorMi();
                                txtTripInKm.setText(getActivity().getString(R.string.txt_trip) + ": " + "0.00 "+ unit);
                            }
                        });
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showArrivedLayout(true);
                            isAlreadyStarted = true;
                            startDrivingAndCalcDistance();
                        }
                    });
                }
            }).start();
        } else {
            unit = stringKMorMi();
            txtTripInKm.setText(getActivity().getString(R.string.txt_trip) + ": " + "0.00 "+unit);
            SharedPref.setTravelLocationData("", getActivity());
            showArrivedLayout(false);
            SharedPref.setIsTravelRunning(false, getActivity());
        }
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (getActivity() != null) {
                boolean isFirstEntry = intent.getBooleanExtra(LocationService.KEY_IS_FIRST_ENTRY, false);
//                boolean isLastEntry = intent.getBooleanExtra(CalcDistanceService.KEY_IS_LAST_ENTRY, false);
                //new changes
                boolean isLastEntry = SharedPref.getIsTravelLastEntry(getActivity());

                double lat = intent.getDoubleExtra(LocationService.KEY_LAT, 0.0);
                double lon = intent.getDoubleExtra(LocationService.KEY_LON, 0.0);
                distValue = intent.getDoubleExtra(LocationService.KEY_DIST, 0.0);
                if (lat != 0)
                    currentLat = lat;
                if (lon != 0)
                    currentLong = lon;

//                Logger.log(TAG, "Getting current Lat Long distance ===>" + distValue);
//                Logger.log(TAG, "Getting current Lat Long===>" + currentLat + "," + currentLong);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initializeDecimalFormat();

                        String calDist = "0.00";
                        try {
                            calDist = numberFormat.format(distValue);
                        } catch (Exception e) {
                            calDist = "" + distValue;
                        }
                        if (calDist != null && calDist.trim().length() > 0)
                            unit = stringKMorMi();
                            txtTripInKm.setText(getActivity().getString(R.string.txt_trip) + ": " + calDist + " "+unit);
                    }
                });


                //add lat & lon to the local arraylist
                LocationPoints locPoints = new LocationPoints(lat, lon);
                boolean isSameLoc = false;

                if (locationList != null && locationList.size() > 1) {
                    isSameLoc = LocationUtils.isSameLocation(locationList.get(locationList.size() - 1).getLatitude(),
                            locationList.get(locationList.size() - 1).getLongitude(),
                            locPoints.getLatitude(), locPoints.getLongitude());
                }
                if (!isSameLoc && locationList != null) {
                    locationList.add(locPoints);
                    //add the values to realm db, in case if the user closes the app while in driving mode,
                    //we will fetch the previous location coordinates from the db and started from the point
                    final LocationCoordinates location = new LocationCoordinates();
                    location.setLatitude(lat);
                    location.setLongitude(lon);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            RoomDBSingleTone.instance(getActivity()).LocationCoordinatesDao().insertAll(location);
                            List<LocationCoordinates> l = new ArrayList();

                        }
                    }).start();
                }

           //     val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                //call the method to calculate the distance for all the saved points
                double distanceTravelled = distValue;
                //save the value in preference to continue the distance calculation when the user closes
                //and reopen the app
                SharedPref.setDistanceEnabled(true, getActivity());
                currentLat = lat;
                currentLong = lon;

                if (!activityCreatedDB) {
                    activityCreatedDB = true;
                    Logger.log("TAG", "GETTING THE FIRST LAT AND LONG VALUES===>"
                            + currentLat + "," + currentLong);
                    new AddStartLatLonAsync().execute(currentLat, currentLong);
                }

                if (isLastEntry) {
                    //clear the realm db
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            RoomDBSingleTone.instance(getActivity()).LocationCoordinatesDao().deteteAllLocationCoordinatesModels();
                            List<LocationCoordinates> l = new ArrayList<>();
                        }
                    }).start();

                    if (distanceTravelled == 0 || distanceTravelled < distValue)
                        distanceTravelled = distValue;

                    Logger.log("TAG", "GETTING TOTAL DISTANCE VALUE IS ===>"
                            + distanceTravelled);
                    //start the async to save the values to ultralite db
                    new EndDrivingAsync(getActivity()).execute(currentLat, currentLong, distanceTravelled);

                }

                initializeDecimalFormat();

                String calDist = "0.00";
                try {
                    calDist = numberFormat.format(distValue);
                } catch (Exception e) {
                    calDist = "" + distValue;
                }

                unit = stringKMorMi();
                txtTripInKm.setText(getActivity().getString(R.string.txt_trip) + ": " + calDist + " "+unit);
                //old code
//                txtTripInKm.setText(getActivity().getString(R.string.txt_trip) + ": " + distanceTravelled
//                                            + " km");
            }
        }
    };

    /**
     * Async class to update the start lat & lon of the driving activity
     */
    private class AddStartLatLonAsync extends AsyncTaskCoroutine<Double, Boolean> {

        @Override
        public Boolean doInBackground(Double... doubles) {
            double startLat = doubles[0];
            double startLon = doubles[1];
            boolean result = dataAccessObject.updateStartLatLon(drivingUniqueID, startLat, startLon);
            return result;
        }

    }

    /**
     * Initializing decimal Format
     *
     * @return
     */
    private void initializeDecimalFormat() {

        numberFormat = new DecimalFormat();
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        DecimalFormatSymbols sym = DecimalFormatSymbols.getInstance();
        sym.setDecimalSeparator('.');
        numberFormat.setDecimalFormatSymbols(sym);
    }

    /**
     * calculates the distance between all the points stored
     *
     * @return
     */
    private double calculateTotalDistance() {

        double totDistance = 0.00;
        if (locationList != null && locationList.size() > 1) {
            Logger.log(TAG, "DISTANCE_CALC LIST SIZE ====>" + locationList.size());
            for (int i = 0; i + 1 < locationList.size(); i++) {
                LocationPoints loc1 = locationList.get(i);
                LocationPoints loc2 = locationList.get(i + 1);
//                totDistance += LocationUtils.distanceBetweenTwoPoint(loc1.getLatitude(), loc1.getLongitude(), loc2.getLatitude(), loc2.getLongitude());
                totDistance += LocationUtils.distanceBetweenTwoPointAndroid(loc1.getLatitude(), loc1.getLongitude(), loc2.getLatitude(), loc2.getLongitude());
            }
            //for kms

//            totDistance = totDistance / 1000;

            Logger.log(TAG, "DISTANCE_CALC TOTAL NEW====>" + totDistance);
        }
        return totDistance;
    }


    public void stopCalcDistanceService() {
        if (isAdded()) {
            if (broadcastReceiver != null) {
                try {
                    getActivity().unregisterReceiver(broadcastReceiver);
                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.log(TAG, "Receiver already unregistered");
                    Logger.printException(e);
                }
            }
        }
    }

    @Override
    public void onStop() {
        // After disconnect() is called, the client is considered "dead".
        locationClient.disconnect();
        stopCalcDistanceService();
        super.onStop();

    }

    @Override
    public void onDismiss(@NotNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (autoStartHandler != null && runnableAutoStart != null)
            autoStartHandler.removeCallbacks(runnableAutoStart);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * sets the height of the dialog dynamically based on orientation.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (broadcastReceiver == null) {
            //new changes
            try {
                getActivity().registerReceiver(broadcastReceiver,
                        new IntentFilter(LocationService.BROADCAST_ACTION), RECEIVER_EXPORTED);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initializeDecimalFormat();

                String calDist = "0.00";
                try {
                    calDist = numberFormat.format(distValue);
                } catch (Exception e) {
                    calDist = "" + distValue;
                }
                if (calDist != null && calDist.trim().length() > 0)
                    unit = stringKMorMi();
                txtTripInKm.setText(getActivity().getString(R.string.txt_trip) + ": " + calDist + " "+unit);
            }
        });

        Logger.log(TAG, "on resume view called");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NotNull String[] permissions, @NotNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    //new changes
//                    callingService();

                } else {
                    // permission denied
                }
                return;
            }
        }
    }


    private void showToastMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    protected void showErrMsgDialog(String errMsg) {
        ErrorDialog errDialog = new ErrorDialog(getActivity(), errMsg);
        errDialog.show();
    }

    protected void showAppUpdatetDialog() {
        AppUpdateDialog appUpdateDialog = new AppUpdateDialog(getActivity());
        appUpdateDialog.show();
    }

    protected void showMultipleDeviceDialog() {

        MultipleDeviceNotSupported multipleDeviceDialog = new MultipleDeviceNotSupported(
                getActivity(),
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
                        }else {
                            openLinkInBrowser(getString(R.string.txtInfoEn));
                        }
                    }
                });
        multipleDeviceDialog.show();
    }

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

    public void specialSynchStop() {
        if (getActivity() != null && !getActivity().isFinishing() && getDialog() != null)
            if (progressDSynch != null && progressDSynch.isShowing()) progressDSynch.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.ask_synch_msg)
                .setCancelable(false)
                .setPositiveButton(R.string.textYesLable,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                synchStop();
                            }
                        })
                .setNegativeButton(R.string.textNoLable,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();


                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void synchStop() {
//x
        if (SynchroteamUitls.isNetworkAvailable(getActivity())) {
            if (progressDSynch == null)
                progressDSynch = ProgressDialog.show(getActivity(),
                        getString(R.string.textPleaseWaitLable),
                        getString(R.string.msg_synch), true, false);
            else if (progressDSynch != null && !progressDSynch.isShowing())
                progressDSynch.show();

            Logger.output(TAG, " SYNCH ENABLED CALLING STOP");

            Thread syncDbToServer = new Thread((new Runnable() {

                @Override
                public void run() {

                    Message myMessage = new Message();
                    try {

                        dataAccessObject.sync(user.getLogin(), user.getPwd());
                        Logger.output("activity mode dialog", " finished sync");
                        Thread.sleep(1000);
                        myMessage.obj = "ok";

                        jobTravelHandlerStop.sendMessage(myMessage);

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
                            } else if (exception.indexOf("4005") != -1) {
                                myMessage.obj = "4005";
                            } else if (exception.indexOf("4003") != -1) {
                                myMessage.obj = "4003";
                            } else {
                                myMessage.obj = "Error";
                            }
                        } else {
                            myMessage.obj = "Error";
                        }

                        jobTravelHandlerStop.sendMessage(myMessage);

                    } finally {
                        if (getActivity() != null && !getActivity().isFinishing() && getDialog() != null)
                            if (progressDSynch != null
                                    && progressDSynch.isShowing())
                                progressDSynch.dismiss();
                            dismiss();

                    }

                }
            }));
            syncDbToServer.start();
        } else {
            if (getActivity() != null && !getActivity().isFinishing() && getDialog() != null)
                if (progressDSynch != null
                        && progressDSynch.isShowing())
                    progressDSynch.dismiss();

            dismiss();
            SynchroteamUitls.showToastMessage(getActivity());

        }
    }

    protected void showSyncFailureMsgDialog(String syncFailureMsg) {

        SynchronizationErrorDialog synchronizationErrorDialog = new SynchronizationErrorDialog
                (getActivity(), syncFailureMsg, new SynchronizationErrorDialog.SynchronizationErrorInterface() {
                    @Override
                    public void doOnOkayClick() {
                        //perform any action
                    }
                });

        synchronizationErrorDialog.show();
    }

    private void stopLocationService() {
        if (getActivity() != null && serviceIntent != null && !getActivity().isFinishing()) {
            serviceIntent.setAction(LocationService.ACTION_STOP);
            getActivity().startService(serviceIntent);

        }
    }

    private void showArrivedLayout(Boolean show) {
        if (show) {
            layDrivingConfirmation.setVisibility(View.GONE);
            layDrivingArrived.setVisibility(View.VISIBLE);
        } else {
            layDrivingConfirmation.setVisibility(View.VISIBLE);
            layDrivingArrived.setVisibility(View.GONE);
        }
    }

    private void startDrivingAndCalcDistance() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            callingPermissionLocation();
        } else {
            callingService();
        }
    }

    private void callingService() {
        SharedPref.setIsTravelRunning(true, getActivity());
        SharedPref.setIsTravelLastEntry(false, getActivity());

        serviceIntent = new Intent(getActivity(), LocationService.class);
        serviceIntent.putExtra("started", isAlreadyStarted);
        serviceIntent.setAction(LocationService.ACTION_START);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getActivity().startForegroundService(serviceIntent);
        } else {
            getActivity().startService(serviceIntent);
        }

    }

    public void callingPermissionLocation() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle(getString(R.string.app_name));
            alertBuilder.setMessage(getString(R.string.str_location_permission));
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    public void specialSynch() {
        if (getActivity() != null && !getActivity().isFinishing() && getDialog() != null)
            if (progressDSynch != null && progressDSynch.isShowing()) progressDSynch.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.ask_synch_msg)
                .setCancelable(false)
                .setPositiveButton(R.string.textYesLable,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                synchStart();
                            }
                        })
                .setNegativeButton(R.string.textNoLable,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();


                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void synchStart() {

        if (SynchroteamUitls.isNetworkAvailable(getActivity())) {
            if (progressDSynch == null)
                progressDSynch = ProgressDialog.show(getActivity(),
                        getString(R.string.textPleaseWaitLable),
                        getString(R.string.msg_synch), true, false);
            else if (progressDSynch != null && !progressDSynch.isShowing())
                progressDSynch.show();

            Logger.output(TAG, " SYNCH ENABLED CALLING START");

            Thread syncDbToServer = new Thread((new Runnable() {

                @Override
                public void run() {

                    Message myMessage = new Message();
                    try {

                        dataAccessObject.sync(user.getLogin(), user.getPwd());
                        Logger.output("travel mode dialog", " finished sync");
                        Thread.sleep(1000);
                        myMessage.obj = "ok";

                        jobTravelHandler.sendMessage(myMessage);

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
                            } else if (exception.indexOf("4005") != -1) {
                                myMessage.obj = "4005";
                            } else if (exception.indexOf("4003") != -1) {
                                myMessage.obj = "4003";
                            } else {
                                myMessage.obj = "Error";
                            }
                        } else {
                            myMessage.obj = "Error";
                        }

                        jobTravelHandler.sendMessage(myMessage);

                    } finally {
                        if (getActivity() != null && !getActivity().isFinishing() && getDialog() != null)
                            if (progressDSynch != null
                                    && progressDSynch.isShowing())
                                progressDSynch.dismiss();

                    }

                }
            }));
            syncDbToServer.start();
        } else {
            if (getActivity() != null && !getActivity().isFinishing() && getDialog() != null) {
                if (progressDSynch != null && progressDSynch.isShowing())
                    progressDSynch.dismiss();

                SynchroteamUitls.showToastMessage(getActivity());
            }

        }
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

    /**
     * Finish clock in to Clocked out
     */
    private boolean finishClockedInMode() {
        boolean clockedOut = false;
        Vector<Conge> vectConge = dataAccessObject.getClockIn();
        Enumeration<Conge> enindisp = vectConge.elements();
        cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 1);
        String currentDate = sdf.format(cal.getTime());
        while (enindisp.hasMoreElements()) {
            Conge indisp = enindisp.nextElement();
            if (indisp.getDtFin() == null) {
                clockedOut = dataAccessObject.updateClockedOutEndTime(indisp.getIdConge(), currentDate);
            }
        }
        return clockedOut;
    }

    @SuppressLint("HandlerLeak")
    private Handler jobTravelHandlerStop = new Handler() {
        public void handleMessage(Message msg) {
            String erreur = (String) msg.obj;
            if (erreur.equals("ok")) {
                if (getActivity() != null && !getActivity().isFinishing() && getDialog() != null)
                    if (progressDSynch != null && progressDSynch.isShowing())
                        progressDSynch.dismiss();

                afterStopLogic();

            } else {
                if (erreur.equals("4006")) {
                    showErrMsgDialog(getString(R.string.msg_synch_error_4));
                } else if (erreur.equals("4101")) {
                    showMultipleDeviceDialog();
                } else if (erreur.equals("4005")) {
                    showAppUpdatetDialog();
                } else if (erreur.equals("4003")) {
                    showErrMsgDialog(getString(R.string.msg_sync_error_4003));
                } else if (erreur.equals("4000") || erreur.equals("4001")) {
                    showErrMsgDialog(getString(R.string.msg_error_auth));
                } else {
                    if (erreur.equals("Error")) {
                        showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));
                    } else {
                        showErrMsgDialog(erreur);
                    }
                }
            }
        }
    };


    @SuppressLint("HandlerLeak")
    private Handler jobTravelHandler = new Handler() {
        public void handleMessage(Message msg) {
            String erreur = (String) msg.obj;
            if (erreur.equals("ok")) {
                //after starting the service
                afterStartLogic();
            } else {
                if (erreur.equals("4006")) {
                    showErrMsgDialog(getString(R.string.msg_synch_error_4));
                } else if (erreur.equals("4101")) {
                    showMultipleDeviceDialog();
                } else if (erreur.equals("4005")) {
                    showAppUpdatetDialog();
                } else if (erreur.equals("4003")) {
                    showErrMsgDialog(getString(R.string.msg_sync_error_4003));
                } else if (erreur.equals("4000") || erreur.equals("4001")) {
                    showErrMsgDialog(getString(R.string.msg_error_auth));
                } else {
                    if (erreur.equals("Error")) {
                        showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));
                    } else {
                        showErrMsgDialog(erreur);

                    }
                }
            }
        }
    };

    private void afterStartLogic() {
//        if (progressDSynch != null && progressDSynch.isShowing())
//            progressDSynch.dismiss();

        if (getActivity() != null && !getActivity().isFinishing() && getDialog() != null) {
            try {
                if (progressDSynch != null && progressDSynch.isShowing()) {
                    progressDSynch.dismiss();
                }
            } catch (Exception e) {

            } finally {
                progressDSynch = null;
            }
        }
    }

    public void afterStopLogic() {

        if (getActivity() != null && !getActivity().isFinishing() && getDialog() != null) {
            try {
                if (progressDSynch != null && progressDSynch.isShowing()) {
                    progressDSynch.dismiss();
                }
            } catch (Exception e) {

            } finally {
                progressDSynch = null;
            }

            /** start timer for inactivity */
            ClockInOutUtil.saveLastClockedInTime(getActivity());
            ClockInOutUtil.startAlarmForTimeOut(getActivity());
            EventBus.getDefault().post(new UpdateDataBaseEvent());
            EventBus.getDefault().post(new DrivingModeUpdateEvent());
            dismiss();
        }

//        if (progressDSynch != null && progressDSynch.isShowing())
//            progressDSynch.dismiss();


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {

                // Start an Activity that tries to resolve the error
                connectionResult
                        .startResolutionForResult(
                                getActivity(),
                                SynchroteamUitls.GOOGLE_PLAY_SERVICE_CONNECTION_REQUEST_CODE);

                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */

            } catch (IntentSender.SendIntentException e) {

                // Log the error
                Logger.printException(e);
            }
        } else {

            // If no resolution is available, display a dialog to the user with
            // the error.

        }
    }

    /**
     * code of previous developer.
     *
     * @param v the v
     */
    public void geocoder(View v) {

        if (servicesConnected()) {

            boolean isGPSEnabled = false;
            boolean isNetworkEnabled = false;

            try {
                locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

                isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                isNetworkEnabled = Build.FINGERPRINT.contains("generic") ? true : locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


                if (!isGPSEnabled && !isNetworkEnabled) {
                    showSettingsAlert();
                } else {

                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        callingPermissionLocation();
                    } else {
                        activityCreatedDB = true;
                        callingLocationFunctionalities();
                        getLatLongAndAddInDB();
                    }

                }

            } catch (Exception e) {
                Logger.printException(e);
            }

        }
    }

    @SuppressLint("MissingPermission")
    private void callingLocationFunctionalities() {

        LocationServices.FusedLocationApi.requestLocationUpdates(
                locationClient, mLocationRequest, locationListener);

        final Toast tag = Toast.makeText(getActivity(),
                getString(R.string.gps_delai), Toast.LENGTH_SHORT);
        if (myTimer != null) {
            myTimer.cancel();
            myTimer = new Timer();
        } else
            myTimer = new Timer();

        myTimer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                activityCreatedDB = false;
                if (currentLat > 0 && currentLong > 0) {
                    activityCreatedDB = true;
                    Logger.log("TAG", "GETTING THE FIRST LAT AND LONG VALUES===>"
                            + currentLat + "," + currentLong);
                    createAndInsertValue(currentLat, currentLong);
                } else {
                    activityCreatedDB = false;
                    currentLat = 0.00;
                    currentLong = 0.00;
                    Logger.log("TAG", "GETTING THE FIRST LAT AND LONG VALUES===>"
                            + currentLat + "," + currentLong);
                    createAndInsertValue(currentLat, currentLong);
                }
                DialogUtils.dismissProgressDialog();
                stopUsingGPS();
                tag.show();
            }
        }, 50000);

        DialogUtils
                .showProgressDialog(
                        getActivity(),
                        getActivity()
                                .getString(R.string.textPleaseWaitLable),
                        getActivity()
                                .getString(R.string.textFetchingLocation),
                        false);
    }

    /**
     * Stop using gps.
     */
    public void stopUsingGPS() {

        if ((locationClient != null) && (locationClient.isConnected())) {
            // locationClient.removeLocationUpdates(locationListener);
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    locationClient, locationListener);
        }
    }

    /**
     * The location listener.
     */
    LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location loc) {

            myTimer.cancel();
            location = loc;
            currentLat = location.getLatitude();
            currentLong = location.getLongitude();

//            DialogUtils.dismissProgressDialog();
//            stopUsingGPS();

            if (location != null) {
                DialogUtils.dismissProgressDialog();
                Logger.log("TAG", "GETTING THE FIRST LAT AND LONG VALUES===>"
                        + currentLat + "," + currentLong);
                createAndInsertValue(currentLat, currentLong);
                stopUsingGPS();
            }
        }
    };

    /**
     * Show settings alert.
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(getString(R.string.textAlertLable) + "!");
        alertDialog.setMessage(getString(R.string.textEnableLocationService));
        alertDialog.setPositiveButton(R.string.textYesLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton(R.string.textNoLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }


    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getActivity());

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status

            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode,
                    getActivity(), 0);
            dialog.show();

            return false;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class EndDrivingAsync extends
            AsyncTaskCoroutine<Double, Boolean> {
        private final Context mContext;

        public EndDrivingAsync(Context mContext) {
            this.mContext = mContext;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        public void onPreExecute() {
            super.onPreExecute();

            DialogUtils.showProgressDialog(getActivity(),
                    getActivity().getString(R.string.textWaitLable),
                    getActivity().getString(R.string.chargement), false);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @SuppressLint("SimpleDateFormat")
        @Override
        public Boolean doInBackground(Double... params) {
            boolean drp = false;
            cal = Calendar.getInstance();
            String currentDateStr = sdf.format(cal.getTime());

            drp = dataAccessObject.updateStopLatLonAndFinishDriving(
                    drivingUniqueID, currentDateStr, params[0], params[1], params[2]);


            if (drivingUniqueID != null && drivingUniqueID.length() > 0) {
                SimpleDateFormat dateFormat = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    dateFormat = new SimpleDateFormat("YYYYMMDD");
                }

                String currentDateIndex = dateFormat.format(cal.getTime());
                int dateIndex = Integer.parseInt(currentDateIndex);

                String lat = params[0]+"";
                String lng = params[1]+"";
                DaoTrackingManager.getInstance().insertActivityInGpsSteps(lat,lng,"stop",dateIndex, drivingUniqueID);
            }
            activityCreatedDB = false;

            return drp;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        public void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (!getActivity().isFinishing())
                DialogUtils.dismissProgressDialog();

            if (result) {
                Toast.makeText(getActivity(),
                        getActivity().getString(R.string.unavailability_stopped),
                        Toast.LENGTH_SHORT).show();
                SharedPref.setDistanceEnabled(false, getActivity());

                //new changes v50
                SharedPref.setIsTravelRunning(false, getActivity());
                SharedPref.setRunningTravelName("", getActivity());
                SharedPref.setIsTravelLastEntry(false, getActivity());
                SharedPref.setTravelLocationData("", getActivity());

                stopCalcDistanceService();

                checkSyncAndPerformActionStop();


            } else
                Toast.makeText(getActivity(), getActivity().getString(R.string.msg_error), Toast.LENGTH_SHORT).show();
        }

    }


    private class AddLatLongAndDrivingInDBAsync extends
            AsyncTaskCoroutine<Double, Boolean> {

        @Override
        public void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            DialogUtils.showProgressDialog(getActivity(),
                    getString(R.string.textWaitLable),
                    getString(R.string.chargement), false);
        }

        @SuppressLint("SimpleDateFormat")
        @Override
        public Boolean doInBackground(Double... doubles) {
            boolean result = false;
            double startLat = doubles[0];
            double startLon = doubles[1];
            cal = Calendar.getInstance();
            String currentDateStr = sdf.format(cal.getTime());

//            //Creating the activity in the Database
//            drivingUniqueID = dataAccessObject.addDrivingActivityAndReturnID(drivingTypeID,
//                    currentDateStr, null, "");
//
//            //Adding the lat & long values in the database
//            if (drivingUniqueID != null && drivingUniqueID.trim().length() > 0)
//                result = dataAccessObject.updateStartLatLon(drivingUniqueID, startLat, startLon);

            drivingUniqueID = dataAccessObject.addDrivingActivityLatLongAndReturnID(drivingTypeID,
                    currentDateStr, null, "", startLat, startLon);

            activityCreatedDB = true;

            if (drivingUniqueID != null && drivingUniqueID.length() > 0) {
                result = true;

                SimpleDateFormat dateFormat = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    dateFormat = new SimpleDateFormat("YYYYMMDD");
                }

                String currentDateIndex = dateFormat.format(cal.getTime());
                int dateIndex = Integer.parseInt(currentDateIndex);

                String lat = startLat+"";
                String lng = startLon+"";
                DaoTrackingManager.getInstance().insertActivityInGpsSteps(lat,lng,"start",dateIndex, drivingUniqueID);
            }

            return result;
        }

        @Override
        public void onPostExecute(Boolean result) {

            super.onPostExecute(result);

            DialogUtils.dismissProgressDialog();

            boolean drp = result.booleanValue();
            if (drp) {
                Toast.makeText(getActivity(),
                        getActivity().getString(R.string.unavailability_added),
                        Toast.LENGTH_SHORT).show();

                if (dataAccessObject.updateStatutInterv(4, idInterv))
                    dataAccessObject.setTimeClotIntervention(idInterv, idUser + "", "");

                /*
                 *  Update the events
                 */
                EventBus.getDefault().post(new UpdateDataBaseEvent());
                EventBus.getDefault().post(new DrivingModeUpdateEvent());
                isAlreadyStarted = false;

                /*
                 *  Cancel the inactivity alarm
                 */
                ClockInOutUtil.cancelAlarmForTimeOut(getActivity());

                showArrivedLayout(true);

                SharedPref.setDistanceEnabled(true, getActivity());

                checkSyncAndPerformAction();

            } else
                Toast.makeText(getActivity(), getActivity().getString(R.string.msg_error),
                        Toast.LENGTH_SHORT).show();
        }

    }

    private void checkSyncAndPerformAction() {
        if (dataAccessObject.checkSynchronisation(1) == 1) {
            if (SynchroteamUitls.isNetworkAvailable(getActivity())) {
                synchStart();
            } else {
                afterStartLogic();
            }
        } else {
            afterStartLogic();
        }
    }

    private void checkSyncAndPerformActionStop() {
        if (dataAccessObject.checkSynchronisation(4) == 1) {
            if (SynchroteamUitls.isNetworkAvailable(getActivity())) {
                synchStop();
            } else {
                afterStopLogic();
            }

        } else {
            afterStopLogic();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        broadcastReceiver.getDebugUnregister();
    }
}