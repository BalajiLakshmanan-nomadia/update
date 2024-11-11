package com.synchroteam.dialogs;

import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Chronometer;
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
import com.synchroteam.beans.UnavailabilityBeans;
import com.synchroteam.beans.UpdateDataBaseEvent;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.events.ActivityUpdateEvent;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.tracking.DaoTracking;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.ClockInOutUtil;
import com.synchroteam.utils.Commons;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DaoTrackingManager;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.SynchroteamUitls;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Timer;
import java.util.Vector;

import de.greenrobot.event.EventBus;

/**
 * Created by user on 8/8/17.
 */

public class ActivityModeDialog extends DialogFragment implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = ClockJobTravelActDialog.class.getSimpleName();
    private static final String ACTIVITY_NAME = "activity_name";
    private static final String ACTIVITY_ID = "activity_id";
    private static final String KEY_ACTIVITY_UNIQUE_ID = "activity_unique_id";
    private static final String KEY_IS_ACTIVITY_STARTED = "is_activity_started";
    private static final String KEY_ACTIVITY_START_TIME = "activity_start_time";

    private TextView txtActivityName, txtOK, txtUndo, txtDone;
    private Chronometer timerDurationTime;
    private LinearLayout layActivityConfirmation, layActivityDone, layClose;
    private Typeface typeFaceAvenirMedium;
    Handler autoStartHandler;
    Runnable runnableAutoStart;
    private ProgressDialog progressDSynch;
    Context context;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
    private Calendar cal;

    private String idInterv, idUser;
    private Dao dataAccessObject;
    private DaoTracking daoTracking;
    private String unavailabilityID = null;
    //    private CurrentJobsFragmentHelperNew currentJobsHelper;
    private User user;
    private long mStartBtnClickTime;
    private static final long AUTO_START_ACTIVITY = 30000;

    /**
     * The location client.
     */
    private GoogleApiClient locationClient;

    /**
     * The location request.
     */
    private LocationRequest locationRequest;

    private LocationManager locationManager;

    /**
     * The my timer.
     */
    private Timer myTimer;

    Double latitude;
    Double longitude;
    String idActivityRemote;
    String gpsEvent;

    int ids = 0;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 124;

    public static ActivityModeDialog newInstance(String unAvailabilityName, int unAvailabilityID, String unAvailabilityUniqueID, boolean isActivityStarted, String activityStartTime) {

        ActivityModeDialog fragment = new ActivityModeDialog();
        Bundle args = new Bundle();
        args.putString(ACTIVITY_NAME, unAvailabilityName);
        args.putInt(ACTIVITY_ID, unAvailabilityID);
        args.putString(KEY_ACTIVITY_UNIQUE_ID, unAvailabilityUniqueID);
        args.putBoolean(KEY_IS_ACTIVITY_STARTED, isActivityStarted);
        args.putString(KEY_ACTIVITY_START_TIME, activityStartTime);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        initializingForLocation();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_unavailability_mode, container);

        context = this.getActivity();
        setCustomTypeface();

        initializeUI(view);

        initializeObj();

//        initializingForLocation();// for GPS_STEPS

        // activity is already started and comes from minimize
        if (getArguments().getBoolean(KEY_IS_ACTIVITY_STARTED)) {
            showActivityStartsLayout(true);

            String activityStartTime = getArguments().getString(KEY_ACTIVITY_START_TIME);
            long totalActivityTime = System.currentTimeMillis();
            try {
                totalActivityTime = sdf.parse(activityStartTime).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            startTimer(totalActivityTime);

        } else {
//            autoActivityStart();
        }

        ids = view.getId();
        return view;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag).addToBackStack(null);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            com.synchroteam.utils.Logger.log(TAG, "IllegalStateException ----->" + e);
        }
    }

    private void autoActivityStart() {
        autoStartHandler = new Handler();
        runnableAutoStart = new Runnable() {
            @Override
            public void run() {
                boolean isPayable = dataAccessObject.checkActivityOrDrivingIsPayable(getArguments().getInt(ACTIVITY_ID));
                CheckInOutClockModeAndStartActivity(isPayable);
            }
        };
        autoStartHandler.postDelayed(runnableAutoStart, AUTO_START_ACTIVITY);
    }

    private void initializeUI(View view) {

        dataAccessObject = DaoManager.getInstance();
        daoTracking = DaoTrackingManager.getInstance();
        user = dataAccessObject.getUser();

        unavailabilityID = (getArguments().getString(KEY_ACTIVITY_UNIQUE_ID));

        txtActivityName = (TextView) view.findViewById(R.id.txt_activity_name);
        timerDurationTime = (Chronometer) view.findViewById(R.id.timer_duration_time);
        txtOK = (TextView) view.findViewById(R.id.txt_ok);
        txtUndo = (TextView) view.findViewById(R.id.txt_undo);
        txtDone = (TextView) view.findViewById(R.id.txt_done);
        layActivityConfirmation = (LinearLayout) view.findViewById(R.id.lay_activity_confirmation);
        layActivityDone = (LinearLayout) view.findViewById(R.id.lay_activity_done);
        layClose = (LinearLayout) view.findViewById(R.id.lay_close);

        timerDurationTime.setTypeface(typeFaceAvenirMedium);
//        timerDurationTime.setText(getActivity().getResources().getString(R.string.textDurationLableTv) + ": 0 min");
        timerDurationTime.setText(" 00:00:00 ");
        timerDurationTime.setOnChronometerTickListener(chronometerTickListenerOnStartOrContinue);

        txtOK.setOnClickListener(this);
        txtUndo.setOnClickListener(this);
        txtDone.setOnClickListener(this);
        layClose.setOnClickListener(this);

        showActivityStartsLayout(false);

        txtActivityName.setText(getArguments().getString(ACTIVITY_NAME));

        Logger.log(TAG, "unavailabilityID activity name is===>" + unavailabilityID);
    }

    private void initializeObj() {
        cal = Calendar.getInstance();
        idInterv = dataAccessObject.getStartedJobId();
        idUser = " ";
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
//        settingDialogHeight();
    }

    private void settingDialogHeight() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenHeight = metrics.heightPixels;
        int screenWidth = metrics.widthPixels;
        int dialogHeight = 0, dialogWidth = 0;
        dialogHeight = (int) (screenHeight * Double.parseDouble(this.getResources().getString(R.string.driving_height)));
        dialogWidth = (int) (screenWidth * Double.parseDouble(this.getResources().getString(R.string.driving_width)));
        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
    }

    private void setCustomTypeface() {
        typeFaceAvenirMedium = Typeface.createFromAsset(context.getAssets(),
                getString(R.string.fontName_avenir_medium));
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return (keyCode == KeyEvent.KEYCODE_BACK);
            }
        });
        return dialog;
    }

    private void showToastMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_ok:

                idActivityRemote = dataAccessObject.getUniqueId();
                // insert into Gps_steps if tracking ennabled
                if (daoTracking.getSessiondata("tracking").equals("on")) {
                    gpsEvent = "start";
                    geocoder();
                }
                if (dataAccessObject.checkSynchronisation(1) == 1) {
                    startActivityLogicSync();

                } else {
                    if (SystemClock.elapsedRealtime() - mStartBtnClickTime < 1500) {
                        return;
                    }
                    mStartBtnClickTime = SystemClock.elapsedRealtime();
                    if (autoStartHandler != null && runnableAutoStart != null)
                        autoStartHandler.removeCallbacks(runnableAutoStart);
                    boolean isPayable = dataAccessObject.checkActivityOrDrivingIsPayable(getArguments().getInt(ACTIVITY_ID));
                    CheckInOutClockModeAndStartActivity(isPayable);
                }
//                 else
//                    specialSynch();

                break;
            case R.id.txt_undo:
                if (autoStartHandler != null && runnableAutoStart != null)
                    autoStartHandler.removeCallbacks(runnableAutoStart);
                DrivingOrActivityListDialog.newInstance(Commons.IS_DRIVING).show(getActivity().getSupportFragmentManager(), "");
                dismiss();
                break;
            case R.id.txt_done:

                // insering into Gps_steps if tracking enabled
                if (daoTracking.getSessiondata("tracking").equals("on")) {
                    gpsEvent = "stop";
                    geocoder();
                }
                if (dataAccessObject.checkSynchronisation(4) == 1) {
//                    synchStop();
                    stopActivityLogicSync();
                } else {
                    stopTimer();
                    Calendar cal = Calendar.getInstance();
                    String currentDateStr = sdf.format(cal.getTime());
                    new ActivityModeDialog.EndUnavailabilityAsync(getActivity()).execute(unavailabilityID, currentDateStr);
                }
//                 else
//                    specialSynchStop();


                break;
            case R.id.lay_close:
                dismiss();
                break;
        }
    }

    private void insertIntoGpsSteps() {
        cal.add(Calendar.SECOND, -1);
        SimpleDateFormat dateFormat = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            dateFormat = new SimpleDateFormat("YYYYMMDD");
        }

        String currentDateStr = dateFormat.format(cal.getTime());
        int dateIndex = Integer.parseInt(currentDateStr);

        String lat = latitude+"";
        String lng = longitude+"";
        daoTracking.insertActivityInGpsSteps(lat,lng,gpsEvent,dateIndex,idActivityRemote);
    }

    private void geocoder() {
        {

            if (servicesConnected()) {

                boolean isGPSEnabled = false;
                boolean isNetworkEnabled = false;

                try {
                    locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

                    isGPSEnabled = locationManager
                            .isProviderEnabled(LocationManager.GPS_PROVIDER);

                    isNetworkEnabled = Build.FINGERPRINT.contains("generic") ? true : locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


                    if (!isGPSEnabled && !isNetworkEnabled) {
                        showSettingsAlert();
                    } else {


                        // locationClient.requestLocationUpdates(mLocationRequest,
                        // locationListener);

//                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                            callingPermissionLocation();
//                        } else {
                            callingLocationFunctionalities();
//                        }


                    }

                } catch (Exception e) {
                    Logger.printException(e);
                }

            }


        }
    }

    public void specialSynch() {
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

    public void specialSynchStop() {
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

    public void synchStart() {

        if (SynchroteamUitls.isNetworkAvailable(getActivity())) {
            if (progressDSynch == null)
                progressDSynch = ProgressDialog.show(getActivity(),
                        getString(R.string.textPleaseWaitLable),
                        getString(R.string.msg_synch), true, false);
            else if (progressDSynch != null && !progressDSynch.isShowing())
                progressDSynch.show();

            Logger.output(TAG, " thread started");

            Thread syncDbToServer = new Thread((new Runnable() {

                @Override
                public void run() {

                    Message myMessage = new Message();
                    try {
                        dataAccessObject.sync(user.getLogin(), user.getPwd());
                        Logger.output("activity mode dialog", " finished sync");
                        Thread.sleep(1000);
                        myMessage.obj = "ok";

                        jobActivityHandler.sendMessage(myMessage);

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

                        jobActivityHandler.sendMessage(myMessage);

                    } finally {
                        if (progressDSynch != null
                                && progressDSynch.isShowing())
                            progressDSynch.dismiss();

                    }

                }
            }));
            syncDbToServer.start();
        } else {
            if (progressDSynch != null
                    && progressDSynch.isShowing())
                progressDSynch.dismiss();

            if (!getActivity().isFinishing()) {
                SynchroteamUitls.showToastMessage(getActivity());
            }
        }
    }

    public void synchStop() {
        if (SynchroteamUitls.isNetworkAvailable(getActivity())) {
            if (progressDSynch == null)
                progressDSynch = ProgressDialog.show(getActivity(),
                        getString(R.string.textPleaseWaitLable),
                        getString(R.string.msg_synch), true, false);
            else if (progressDSynch != null && !progressDSynch.isShowing())
                progressDSynch.show();
            Logger.output(TAG, " thread started");
            Thread syncDbToServer = new Thread((new Runnable() {

                @Override
                public void run() {
                    Message myMessage = new Message();
                    try {
                        dataAccessObject.sync(user.getLogin(), user.getPwd());
                        Logger.output("activity mode dialog", " finished sync");
                        Thread.sleep(1000);
                        myMessage.obj = "ok";
                        jobActivityHandlerStop.sendMessage(myMessage);

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

                        jobActivityHandlerStop.sendMessage(myMessage);

                    } finally {
                        if (progressDSynch != null
                                && progressDSynch.isShowing())
                            progressDSynch.dismiss();

                    }

                }
            }));
            syncDbToServer.start();
        } else {
            if (progressDSynch != null
                    && progressDSynch.isShowing())
                progressDSynch.dismiss();

            if (!getActivity().isFinishing()) {
                SynchroteamUitls.showToastMessage(getActivity());
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler jobActivityHandler = new Handler() {
        public void handleMessage(Message msg) {
            String erreur = (String) msg.obj;
            if (erreur.equals("ok")) {

                afterStartLogic();
            } else {
                if (erreur.equals("4006")) {
                    showErrMsgDialog(getString(R.string.msg_synch_error_4));
                } else if (erreur.equals("4101")) {
                    showMultipleDeviecDialog();
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

    private void startActivityLogicSync() {

        if (progressDSynch == null)
            progressDSynch = ProgressDialog.show(getActivity(),
                    getString(R.string.textPleaseWaitLable),
                    getString(R.string.msg_synch), true, false);
        else if (progressDSynch != null && !progressDSynch.isShowing())
            progressDSynch.show();

        if (SystemClock.elapsedRealtime() - mStartBtnClickTime < 1500) {
            return;
        }
        mStartBtnClickTime = SystemClock.elapsedRealtime();
        if (autoStartHandler != null && runnableAutoStart != null)
            autoStartHandler.removeCallbacks(runnableAutoStart);
        boolean isPayable = dataAccessObject.checkActivityOrDrivingIsPayable(getArguments().getInt(ACTIVITY_ID));
        CheckInOutClockModeAndStartActivity(isPayable);
    }

    @SuppressLint("HandlerLeak")
    private Handler jobActivityHandlerStop = new Handler() {
        public void handleMessage(Message msg) {
            String erreur = (String) msg.obj;
            if (erreur.equals("ok")) {
                afterStopLogic();
            } else {
                if (erreur.equals("4006")) {
                    showErrMsgDialog(getString(R.string.msg_synch_error_4));
                } else if (erreur.equals("4101")) {
                    showMultipleDeviecDialog();
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

    private void stopActivityLogicSync() {

        if (!getActivity().isFinishing()) {
            if (progressDSynch == null)
                progressDSynch = ProgressDialog.show(getActivity(),
                        getString(R.string.textPleaseWaitLable),
                        getString(R.string.msg_synch), true, false);
            else if (progressDSynch != null && !progressDSynch.isShowing())
                progressDSynch.show();

            stopTimer();
            Calendar cal = Calendar.getInstance();
            String currentDateStr = sdf.format(cal.getTime());
            new ActivityModeDialog.EndUnavailabilityAsync(getActivity()).execute(unavailabilityID, currentDateStr);
        }
    }

    protected void showAppUpdatetDialog() {

        AppUpdateDialog appUpdateDialog = new AppUpdateDialog(getActivity());

        appUpdateDialog.show();
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

    protected void showMultipleDeviecDialog() {

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
                        } else {
                            openLinkInBrowser(getString(R.string.txtInfoEn));
                        }
                    }
                });
        multipleDeviceDialog.show();
    }

    protected void showErrMsgDialog(String errMsg) {

        ErrorDialog errDialog = new ErrorDialog(getActivity(), errMsg);

        errDialog.show();
    }

    private void showActivityStartsLayout(Boolean show) {
        if (show) {
            layActivityConfirmation.setVisibility(View.GONE);
            layActivityDone.setVisibility(View.VISIBLE);
        } else {
            layActivityConfirmation.setVisibility(View.VISIBLE);
            layActivityDone.setVisibility(View.GONE);
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

    private void showAndStartUnAvailability() {
        try {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showActivityStartsLayout(true);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private class AddUnavailabilityInDBAsync extends
            AsyncTaskCoroutine<String, Boolean> {

        @Override
        public void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            DialogUtils.showProgressDialog(getActivity(),
                    getString(R.string.textWaitLable),
                    getString(R.string.textSavingAddJobData), false);
        }

        @SuppressLint("SimpleDateFormat")
        @Override
        public Boolean doInBackground(String... params) {

            String currentDateStr = sdf.format(cal.getTime());


//            updateEndDateOfPreviousActivity();

            // last parameter is description notes
            unavailabilityID = dataAccessObject.addUnavailabilityAndReturnID(idActivityRemote,
                    getArguments().getInt(ACTIVITY_ID), 0, currentDateStr, null, "");
            return unavailabilityID != null;
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

//                EventBus.getDefault().post(new UpdateDataBaseEvent());

                EventBus.getDefault().post(new ActivityUpdateEvent());

                showAndStartUnAvailability();

                long currentTime = System.currentTimeMillis();
                startTimer(currentTime);
                /** cancel the inactivity alarm */
                ClockInOutUtil.cancelAlarmForTimeOut(getActivity());


                if (dataAccessObject.checkSynchronisation(1) == 1) {
                    if (SynchroteamUitls.isNetworkAvailable(getActivity())) {
                        synchStart();
                    } else {
                        afterStartLogic();
                    }

                } else {
                    afterStartLogic();
                }

            } else
                Toast.makeText(getActivity(), getActivity().getString(R.string.msg_error),
                        Toast.LENGTH_SHORT).show();
            afterStartLogic();
        }


    }

    private void afterStartLogic() {

        try {
            if (progressDSynch != null && progressDSynch.isShowing()) {
                progressDSynch.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // *************************************************ASYNC...CLASS...ENDS...HERE****************************************


    /**
     * Check for the unavailability which does not have an end date (i.e already
     * started) and update the end date of that unavailability to current date.
     */
    private void updateEndDateOfPreviousActivity() {

        Vector<Conge> vectConge = new Vector<Conge>();
        vectConge = dataAccessObject.getConge();
        Enumeration<Conge> enindisp = vectConge.elements();
        String currentDate = sdf.format(cal.getTime());
        while (enindisp.hasMoreElements()) {
            Conge indisp = enindisp.nextElement();
            if (indisp.getDtFin() == null) {
                dataAccessObject.updateUnavailabilityEndDate(
                        indisp.getIdConge(), currentDate);
            }
        }
    }


    private class EndUnavailabilityAsync extends
            AsyncTaskCoroutine<String, Boolean> {
        private Context mContext;

        public EndUnavailabilityAsync(Context mContext) {
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
                    getActivity().getString(R.string.textSavingAddJobData), false);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @SuppressLint("SimpleDateFormat")
        @Override
        public Boolean doInBackground(String... params) {
            boolean drp = false;
            if (params[0] != null) {
                drp = dataAccessObject.updateUnavailabilityEndDate(
                        params[0], params[1]);
            }
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

            if (!getActivity().isFinishing()) {
                DialogUtils.dismissProgressDialog();

                if (result) {
                    Toast.makeText(getActivity(),
                            getActivity().getString(R.string.unavailability_stopped),
                            Toast.LENGTH_SHORT).show();
//                currentJobsHelper.refreshList();

//                    EventBus.getDefault().post(new UpdateDataBaseEvent());
                    EventBus.getDefault().post(new ActivityUpdateEvent());


                    if (dataAccessObject.checkSynchronisation(4) == 1) {
                        if (SynchroteamUitls.isNetworkAvailable(getActivity())) {
                            synchStop();
                        } else {
                            afterStopLogic();
                        }

                    } else {
                        afterStopLogic();
                    }

                } else
                    Toast.makeText(getActivity(), getActivity().getString(R.string.msg_error), Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void afterStopLogic() {

//        if (getActivity()!= null && !getActivity().isFinishing()) {
        if (progressDSynch != null && progressDSynch.isShowing()) {
            progressDSynch.dismiss();
        }
        /** start timer for inactivity */
        ClockInOutUtil.saveLastClockedInTime(context);
        ClockInOutUtil.startAlarmForTimeOut(context);
        dismiss();
//        }

    }

//    private void closeDialogAfterSuccess() {
////        ClockJobTravelActDialog.newInstance().show(getActivity().getSupportFragmentManager(), "");
//        dismiss();
//    }

    private void startTimer(long currentTime) {
        timerDurationTime.setBase(currentTime);
        timerDurationTime.setVisibility(View.VISIBLE);
        timerDurationTime.start();

    }

    /**
     * Stop timer.
     */
    private void stopTimer() {
        timerDurationTime.stop();
    }


    /**
     * The chronometer tick listener on start or continue.
     */
    Chronometer.OnChronometerTickListener chronometerTickListenerOnStartOrContinue = new Chronometer.OnChronometerTickListener() {

        @Override
        public void onChronometerTick(Chronometer chronometer) {

            long timeElapsed = System.currentTimeMillis()
                    - chronometer.getBase();
            int seconds = (int) (timeElapsed / 1000);

            try {

                int hours = seconds / 3600;
                int minutes = (seconds / 60) - (hours * 60);
                seconds = seconds - (hours * 3600) - (minutes * 60);
                String minutesString = null;
                String hoursString = null;
                String secondsString = null;

                if (minutes < 10) {
                    minutesString = "0" + minutes;
                } else {
                    minutesString = minutes + "";
                }

                if (seconds < 10) {
                    secondsString = "0" + seconds;
                } else {
                    secondsString = seconds + "";
                }

//                minutesString = minutes + "";

                if (hours == 0) {
//                    chronometer.setText(getActivity().getResources().getString(R.string.textDurationLableTv) + ": " + minutesString + " min");
                    chronometer.setText(" 00:" + minutesString + ":" + secondsString);
                } else if (hours < 10) {
                    hoursString = "0" + hours;
                    chronometer.setText(" " + hoursString + ":" + minutesString + ":" + secondsString);
                } else {
                    hoursString = hours + "";
                    chronometer.setText(" " + hoursString + ":" + minutesString + ":" + secondsString);
                }

            } catch (Exception e) {
                Logger.printException(e);

            }

        }
    };

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (autoStartHandler != null && runnableAutoStart != null)
            autoStartHandler.removeCallbacks(runnableAutoStart);
    }

    /**
     * clock in/out change when the activity is payable/non-payable
     * Payable means clocked in
     * Non Payable means clocked out
     *
     * @param isPayableActivity
     */
    private void CheckInOutClockModeAndStartActivity(boolean isPayableActivity) {
        if (dataAccessObject.checkIsClockInAvailable(user.getId())) {
            if (isPayableActivity) {
                // payable activity - clocked in
                // checking Clocked In or Out
                Conge vectCongeClockIn = checkClockedIn();
                if (vectCongeClockIn != null) {
                    // clocked in
                    new ActivityModeDialog.AddUnavailabilityInDBAsync().execute();
                } else {
                    /* Clocked out, do clock in for the payable activity, New logic subtract 1
                    min from current time for clock in time*/
                    cal.add(Calendar.SECOND, -1);
                    String currentDateStr = sdf.format(cal.getTime());
                    // new clocked in entry to T_CONGE
                    UnavailabilityBeans clockInActivity = dataAccessObject.getClockInActivity();

                    String uniqueID = dataAccessObject.addUnavailabilityAndReturnID(idActivityRemote,
                            clockInActivity.getUnavailabilityID(), 0, currentDateStr, null, "");
                    if (uniqueID != null) {
                        showToastMessage(getActivity().getResources().getString(R.string.txt_clock_in));
                        new ActivityModeDialog.AddUnavailabilityInDBAsync().execute();
                    } else {
                        showToastMessage(getActivity().getResources().getString(R.string.msg_error));
                    }

                }
            } else {
                // non payable activity - clocked out
                // checking Clocked In or Out
                Conge vectCongeClockIn = checkClockedIn();
                if (vectCongeClockIn != null) {
                    /* Clocked in, do clock out for the non payable driving, New logic add 1
                    min from current time for clock out time*/
                    if (finishClockedInMode()) {
                        new ActivityModeDialog.AddUnavailabilityInDBAsync().execute();
                        showToastMessage(getActivity().getResources().getString(R.string.txt_clock_out));
                    }

                } else {
                    // clocked out
                    new ActivityModeDialog.AddUnavailabilityInDBAsync().execute();
                }
            }
        } else {
            new ActivityModeDialog.AddUnavailabilityInDBAsync().execute();
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

    private void initializingForLocation() {
        locationClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        locationClient.connect();

        locationRequest = LocationRequest.create();

        locationRequest
                .setInterval(SynchroteamUitls.UPDATE_INTERVAL_IN_MILLISECONDS);

        // Use high accuracy
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
        locationRequest
                .setFastestInterval(SynchroteamUitls.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

    }

    /**
     * The location listener.
     */
    LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {

            myTimer.cancel();
            DialogUtils.dismissProgressDialog();
                stopUsingGPS();
                latitude = location.getLatitude();
                longitude = location.getLongitude();

            insertIntoGpsSteps();

            }

    };

    private void callingLocationFunctionalities() {


        LocationServices.FusedLocationApi.requestLocationUpdates(
                locationClient, locationRequest, locationListener);

        final Toast tag = Toast.makeText(context,
                getString(R.string.gps_delai), Toast.LENGTH_SHORT);
        if (myTimer != null) {
            myTimer.cancel();
            myTimer = new Timer();
        } else
            myTimer = new Timer();

        myTimer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                DialogUtils.dismissProgressDialog();
                stopUsingGPS();
                tag.show();
            }
        }, 50000);

        DialogUtils
                .showProgressDialog(
                        (Activity) context,
                        context
                                .getString(R.string.textPleaseWaitLable),
                        context
                                .getString(R.string.textFetchingLocation),
                        false);
    }

    private void callingPermissionLocation() {

        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle(getString(R.string.app_name));
            alertBuilder.setMessage(getString(R.string.str_location_permission));
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(context);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status

            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode,
                    (Activity) context, 0);
            dialog.show();

            return false;
        }
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
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
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

}