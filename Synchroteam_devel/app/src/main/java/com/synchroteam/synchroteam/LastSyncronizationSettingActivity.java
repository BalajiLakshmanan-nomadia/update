package com.synchroteam.synchroteam;

import static android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TypefaceSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.synchroteam.tracking.TrackingParameterService;
import com.synchroteam.TypefaceLibrary.Button;
import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.RadioButton;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.beans.UpdateUiOnSync;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.AuthenticationErrorDialog;
import com.synchroteam.dialogs.ErrorDialog;
import com.synchroteam.dialogs.SynchronizationErrorDialog;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DateChecker;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.SharedPref;
import com.synchroteam.utils.SynchroteamUitls;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc

/**
 * This Activity is Responsible for Inflating And performing actions on this
 * Synchronization Setting Screen.
 *
 * @author Ideavate.solution
 */
public class LastSyncronizationSettingActivity extends AppCompatActivity
        implements CommonInterface {

	/*
     * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    /**
     * The islast sync on.
     */
    private boolean islastSyncOn;

    /**
     * The islast sync on.
     */
    private boolean isPushSyncOn;

    /**
     * The action bar.
     */
    private ActionBar actionBar;

    /**
     * The alarm manager.
     */
    private AlarmManager alarmManager;

    /**
     * The alarm intent.
     */
    private PendingIntent alarmIntent;

    /**
     * The pi.
     */
    private PendingIntent pi, pi1,pITrackParams;

    /**
     * The sync time et.
     */
    private EditText syncTimeEt;

    /**
     * The button.
     */
    private Button button;

    /**
     * The progress d synch.
     */
    private ProgressDialog progressDSynch;

    /**
     * The dao.
     */
    private Dao dao;

    private User user;

    /**
     * The gt.
     */
    private GestionAcces gt;

    private RadioGroup rdoGrpAutoSync;

    private RadioButton rdoBtnAutoSync, rdoBtnPushSync;

    private LinearLayout linPeriodicity;

    private ImageView imgPeriodicityDivider;

    private static final String TAG = "LastSyncronizationSettingActivity";

    /*
     * (non-Javadoc)
     *
     * @see android.support.v7.app.AppCompatActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        actionBar = getSupportActionBar();

        String title = this.getResources().getString(
                R.string.textSyncronizationTv).toUpperCase();
        SpannableString titleSpannable = new SpannableString(title);
        titleSpannable.setSpan(
                new TypefaceSpan(this.getResources().getString(
                        R.string.fontName_hevelicaLight)), 0,
                titleSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // actionBar.setTitle(titleSpannable);
        actionBar.setTitle(isLGDevice() ? title : titleSpannable);

        alarmManager = (AlarmManager) this
                .getSystemService(Context.ALARM_SERVICE);
        setContentView(R.layout.layout_last_syncronization);
        final ImageButton imageButton = (ImageButton) findViewById(R.id.onOffLastSyncronization);
        syncTimeEt = (EditText) findViewById(R.id.dataPreiodTv);
        dao = DaoManager.getInstance();
//        pi = PendingIntent.getService(LastSyncronizationSettingActivity.this,
//                0, new Intent(LastSyncronizationSettingActivity.this,
//                        TrackingService.class),
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        pi1 = PendingIntent.getService(LastSyncronizationSettingActivity.this,
//                0, new Intent(LastSyncronizationSettingActivity.this,
//                        TrackingServiceFrequency.class),
//                PendingIntent.FLAG_UPDATE_CURRENT);
        pITrackParams=initializePendingIntent();

        button = (Button) findViewById(R.id.saveButton);

        rdoGrpAutoSync = (RadioGroup) findViewById(R.id.rdoGrpSync);
        rdoBtnAutoSync = (RadioButton) findViewById(R.id.rdoBtnAutoSync);
        rdoBtnPushSync = (RadioButton) findViewById(R.id.rdoBtnPushSync);
        linPeriodicity = (LinearLayout) findViewById(R.id.linPeriodicity);
        imgPeriodicityDivider = (ImageView) findViewById(R.id.imgPeriodictyDivider);

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (!islastSyncOn && !isPushSyncOn) {
                    removeAlarmManagerToAutoSyncroniseData();
                    setPushSyncOff();
                    finish();
                } else if (islastSyncOn) {
                    if (validateDate()) {
                        setAlarmManagerToAutoSyncroniseData();
                        setPushSyncOff();
                        finish();
                    }
                } else {
                    setPushSyncOn();
                    removeAlarmManagerToAutoSyncroniseData();
                    finish();
                }

                // if (islastSyncOn) {
                // if (validateDate()) {
                // setAlarmManagerToAutoSyncroniseData();
                // finish();
                // }
                // } else {
                // removeAlarmManagerToAutoSyncroniseData();
                // finish();
                // }
                // if (isPushSyncOn) {
                // setPushSyncOn();
                // finish();
                // } else {
                // setPushSyncOff();
                // finish();
                // }
            }
        });

        syncTimeEt.setText(SharedPref.getSyncInterval(this) + "");

        imageButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!islastSyncOn && !isPushSyncOn) {
                    imageButton
                            .setImageResource(R.drawable.traking_on_btn_tracking_detail);
                    enableRadioGroup();
                    if (rdoBtnAutoSync.isChecked()) {
                        showPeriodicity();
                        islastSyncOn = true;
                        isPushSyncOn = false;
                    } else {
                        islastSyncOn = false;
                        isPushSyncOn = true;
                    }

                } else {
                    imageButton
                            .setImageResource(R.drawable.traking_off_btn_tracking_detail);
                    disableRadioGroup();
                    hidePeriodicity();
                    islastSyncOn = false;
                    isPushSyncOn = false;
                }
            }
        });

        rdoGrpAutoSync
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        RadioButton rdoBtn = (RadioButton) findViewById(checkedId);
                        String strChecked = rdoBtn.getText().toString();
                        if (strChecked
                                .equalsIgnoreCase(getString(R.string.txt_auto_sync))) {
                            islastSyncOn = true;
                            isPushSyncOn = false;
                            showPeriodicity();
                        } else {
                            islastSyncOn = false;
                            isPushSyncOn = true;
                            hidePeriodicity();
                        }
                    }
                });

        if (SharedPref.getIsSyncOn(this)) {
            imageButton
                    .setImageResource(R.drawable.traking_on_btn_tracking_detail);
            enableRadioGroup();
            rdoBtnAutoSync.setChecked(true);
            showPeriodicity();
        } else if (SharedPref.getIsPushSyncOn(this)) {
            imageButton
                    .setImageResource(R.drawable.traking_on_btn_tracking_detail);
            enableRadioGroup();
            hidePeriodicity();
            rdoBtnPushSync.setChecked(true);
        } else {
            imageButton
                    .setImageResource(R.drawable.traking_off_btn_tracking_detail);
            disableRadioGroup();
            hidePeriodicity();
        }

        islastSyncOn = SharedPref.getIsSyncOn(this);
        isPushSyncOn = SharedPref.getIsPushSyncOn(this);
    }

    public boolean isLGDevice() {
        return (Build.MANUFACTURER.contains("LG") || Build.MODEL.contains("LG"));
    }


    /**
     * Intializin the pending intent for the tracking service
     *
     * @return Pending intent
     */
    private PendingIntent initializePendingIntent() {
        Intent trackingParamsIntent = new Intent(LastSyncronizationSettingActivity.this,
                TrackingParameterService.class);
        trackingParamsIntent.putExtra("from_pending_intent", true);

        PendingIntent pendingIntent;
        //Behaviour changes supporting android 12
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // Create a PendingIntent using FLAG_IMMUTABLE
            pendingIntent= PendingIntent.getService(LastSyncronizationSettingActivity.this,
                    0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT|
                            PendingIntent.FLAG_IMMUTABLE);
        } else {

            pendingIntent= PendingIntent.getService(LastSyncronizationSettingActivity.this,
                    0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return pendingIntent;
//        return PendingIntent.getService(LastSyncronizationSettingActivity.this,
//                0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    }


    /**
     * For canceling the Tracking service when conditions are met
     */
    private void cancelTrackingAlarm() {
        stopService(new Intent(LastSyncronizationSettingActivity.this, TrackingParameterService.class));
        AlarmManager am2 = (AlarmManager) LastSyncronizationSettingActivity.this
                .getSystemService(Context.ALARM_SERVICE);
        am2.cancel(pITrackParams);
    }


    /**
     * Validate date to check if proper date is used.
     *
     * @return true, if successful
     */
    protected boolean validateDate() {
        // TODO Auto-generated method stub

        String interval = syncTimeEt.getText().toString();
        boolean isDataValid = true;

        if (TextUtils.isEmpty(interval)) {
            DialogUtils.showInfoDialog(
                    this,
                    this.getResources().getString(
                            R.string.textEmptyTimeInterval));

            isDataValid = false;
        } else if (!TextUtils.isDigitsOnly(interval)) {
            DialogUtils.showInfoDialog(
                    this,
                    this.getResources().getString(
                            R.string.textCharcterTimeInterval));
            isDataValid = false;
        } else if (Integer.parseInt(interval) < Integer.parseInt(getResources()
                .getString(R.string.textMinimumTimeInterval))) {
            DialogUtils.showInfoDialog(
                    this,
                    this.getResources().getString(
                            R.string.textValueLessThanMinimumValue));
            isDataValid = false;
        }

        return isDataValid;
    }

    /**
     * Removes the alarm manager to auto syncronise data.
     */
    protected void removeAlarmManagerToAutoSyncroniseData() {
        // TODO Auto-generated method stub

        Intent intent = ((SyncroTeamApplication) getApplication())
                .getAutoSyncIntent();
//        alarmIntent = PendingIntent.getBroadcast(this, 0, intent,
//                PendingIntent.FLAG_UPDATE_CURRENT);

        //Behaviour changes supporting android 12
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // Create a PendingIntent using FLAG_IMMUTABLE
            alarmIntent = PendingIntent.getBroadcast(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_IMMUTABLE);
        } else {

            alarmIntent = PendingIntent.getBroadcast(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        alarmManager.cancel(alarmIntent);
        gt = dao.getAcces();

        SharedPref.setIsSyncOn(false, this);
        SharedPref.setSyncInterval(
                Integer.parseInt(this.getResources().getString(
                        R.string.hintDefaultTimeInterval)), this);

    }

    /**
     * Sets the alarm manager to auto syncronise data.
     */
    @SuppressLint("NewApi")
    protected void setAlarmManagerToAutoSyncroniseData() {

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        Intent intent = ((SyncroTeamApplication) getApplication())
                .getAutoSyncIntent();

        String stringInterval = syncTimeEt.getText().toString();
        int interval = Integer.parseInt(stringInterval);

        intent.putExtra(KEYS.SyncronizationSettings.SYNC_INTERVAL, interval);

//        alarmIntent = PendingIntent.getBroadcast(this, 0, intent,
//                PendingIntent.FLAG_UPDATE_CURRENT);

        //Behaviour changes supporting android 12
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // Create a PendingIntent using FLAG_IMMUTABLE
            alarmIntent = PendingIntent.getBroadcast(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_IMMUTABLE);
        } else {

            alarmIntent = PendingIntent.getBroadcast(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        alarmManager.cancel(alarmIntent);

        if (currentapiVersion < android.os.Build.VERSION_CODES.KITKAT) {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + interval * 60 * 1000,
                    interval * 60 * 1000, alarmIntent);

        } else {

            alarmManager.cancel(alarmIntent);
//            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                    SystemClock.elapsedRealtime() + interval * 60 * 1000,
//                    alarmIntent);

            // Android 14 changes
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

        SharedPref.setIsSyncOn(true, this);
        SharedPref.setSyncInterval(interval, this);

    }

    private void setPushSyncOn() {
        SharedPref.setIsPushSyncOn(true, this);
    }

    private void setPushSyncOff() {
        SharedPref.setIsPushSyncOn(false, this);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_job_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.action_refresh:

                synch();

                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Synch.
     */
    private void synch() {
        if (SynchroteamUitls
                .isNetworkAvailable(LastSyncronizationSettingActivity.this)) {
            progressDSynch = ProgressDialog.show(
                    LastSyncronizationSettingActivity.this,
                    getString(R.string.textPleaseWaitLable),
                    getString(R.string.msg_synch), true, false);

            Thread syncDataBaseWithServer = new Thread((new Runnable() {
                @Override
                public void run() {

                    Message myMessage = new Message();
                    try {
                        user = dao.getUser();
                        dao.sync(user.getLogin(), user.getPwd());
                        gt = dao.getAcces();
                        if (gt != null && gt.getOptionTaracking() == 0) {
//                            stopService(new Intent(
//                                    LastSyncronizationSettingActivity.this,
//                                    TrackingService.class));
//                            stopService(new Intent(
//                                    LastSyncronizationSettingActivity.this,
//                                    TrackingServiceFrequency.class));
//                            AlarmManager am = (AlarmManager) LastSyncronizationSettingActivity.this
//                                    .getSystemService(Context.ALARM_SERVICE);
//                            am.cancel(pi);
//                            AlarmManager am1 = (AlarmManager) LastSyncronizationSettingActivity.this
//                                    .getSystemService(Context.ALARM_SERVICE);
//                            am1.cancel(pi1);
                            cancelTrackingAlarm();
                        }
                        myMessage.obj = "ok";
                        handler.sendMessage(myMessage);
                    } catch (Exception ex) {
                        String exception = ex.getMessage();
                        if (exception != null) {
                            if (exception.indexOf("4001") != -1) {
                                myMessage.obj = "4001";
                            } else if (exception.indexOf("4006") != -1) {
                                myMessage.obj = "4006";
                            } else if (exception.indexOf("4000") != -1) {
                                myMessage.obj = "4000";
                            }else if (exception.indexOf("4101") != -1) {
                                myMessage.obj = "4101";
                            } else if (exception.indexOf("4005") != -1) {
                                myMessage.obj = "4005";
                            }else if (exception.indexOf("4003") != -1) {
                                myMessage.obj = "4003";
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

            syncDataBaseWithServer.start();
        } else {
            SynchroteamUitls
                    .showToastMessage(LastSyncronizationSettingActivity.this);
        }

    }

    /**
     * The handler.
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            String erreur = (String) msg.obj;
            if (erreur.equals("ok")) {
                Toast.makeText(LastSyncronizationSettingActivity.this,
                        getString(R.string.msg_synch_ok), Toast.LENGTH_LONG)
                        .show();

            } else if (erreur.equals("4001")||erreur.equals("4000")) {
                showAuthErrDialog();
            } else if (erreur.equals("4006")) {
                Toast.makeText(LastSyncronizationSettingActivity.this,
                        getString(R.string.msg_synch_error_4),
                        Toast.LENGTH_LONG).show();
            } else if (erreur.equals("4101")) {
                Toast.makeText(LastSyncronizationSettingActivity.this,
                        getString(R.string.msg_synch_error_2),
                        Toast.LENGTH_LONG).show();
            } else if (erreur.equals("4005")) {
                Toast.makeText(LastSyncronizationSettingActivity.this,
                        getString(R.string.msg_synch_error_3),
                        Toast.LENGTH_LONG).show();
            } else if (erreur.equals("4003")) {
                showErrMsgDialog(getString(R.string.msg_sync_error_4003));
            }else {
//                Toast.makeText(LastSyncronizationSettingActivity.this,
//                               getString(R.string.msg_synch_error_new), Toast.LENGTH_LONG)
//                        .show();
                showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));
            }
        }
    };

    /**
     * Show error dialog
     */
    protected void showErrMsgDialog(String errMsg) {

        ErrorDialog errDialog = new ErrorDialog(LastSyncronizationSettingActivity.this, errMsg);

        errDialog.show();
    }
    /**
     * For showing the synchronization failure messages
     */
    protected void showSyncFailureMsgDialog(String syncFailureMsg) {

        SynchronizationErrorDialog synchronizationErrorDialog = new SynchronizationErrorDialog
                (LastSyncronizationSettingActivity.this, syncFailureMsg, new SynchronizationErrorDialog
                        .SynchronizationErrorInterface() {
                    @Override
                    public void doOnOkayClick() {
                        //perform any action
                    }
                });

        synchronizationErrorDialog.show();
    }

    /**
     * Show authentication error dialog
     */
    protected void showAuthErrDialog() {

        AuthenticationErrorDialog authenticationErrorDialog = new AuthenticationErrorDialog(
                LastSyncronizationSettingActivity.this, user.getLogin());

        authenticationErrorDialog.show();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.synchroteam.synchroteam.CommonInterface#getActivityInstance()
     */
    @Override
    public Activity getActivityInstance() {
        // TODO Auto-generated method stub
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onResume()
     */
    protected void onResume() {
        super.onResume();

        //New changes
        DateChecker.checkDateAndNavigate(this, dao);


        ((SyncroTeamApplication) getApplicationContext())
                .setSyncroteamAppLive(true);
        ((SyncroTeamApplication) getApplicationContext())
                .setSyncroteamActivityInstance(this);

    }

    ;

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onPause()
     */
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
        EventBus.getDefault().post(new UpdateUiOnSync());
    }

    @Override
    public void updateUiOnTrakingStatusChange(boolean isRunning) {

    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        setResult(RESULT_OK);

        super.onDestroy();
    }

    private void disableRadioGroup() {
        for (int i = 0; i < rdoGrpAutoSync.getChildCount(); i++) {
            rdoGrpAutoSync.getChildAt(i).setEnabled(false);
        }
    }

    private void enableRadioGroup() {
        for (int i = 0; i < rdoGrpAutoSync.getChildCount(); i++) {
            rdoGrpAutoSync.getChildAt(i).setEnabled(true);
        }
    }

    /**
     * Shows the periodicity edittext when autosync is on.
     */
    private void showPeriodicity() {
        linPeriodicity.setVisibility(View.VISIBLE);
        imgPeriodicityDivider.setVisibility(View.VISIBLE);
    }

    /**
     * Hides the periodicity edittext when autosync is on.
     */
    private void hidePeriodicity() {
        linPeriodicity.setVisibility(View.INVISIBLE);
        imgPeriodicityDivider.setVisibility(View.INVISIBLE);
    }
}
