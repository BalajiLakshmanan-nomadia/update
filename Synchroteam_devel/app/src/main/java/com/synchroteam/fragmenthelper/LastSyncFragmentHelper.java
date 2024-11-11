package com.synchroteam.fragmenthelper;

import static android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.synchroteam.tracking.TrackingParameterService;
import com.synchroteam.TypefaceLibrary.Button;
import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.RadioButton;
import com.synchroteam.baseactivity.SyncroTeamBaseActivity;
import com.synchroteam.beans.EnableSynchronizationAddJobEvent;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.fragment.BaseFragment;
import com.synchroteam.synchroteam.SyncroTeamApplication;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.CommonUtils;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.MaterialDesignUtils;
import com.synchroteam.utils.SharedPref;

import java.util.Calendar;

import de.greenrobot.event.EventBus;

/**
 * Helper class for inflating the view for last sync. The technician can able to enable the push sync or auto sync from this screen.
 * <p>
 * Created by Trident on 10/24/2016.
 */

public class LastSyncFragmentHelper implements HelperInterface {

    private View view;
    private EditText syncTimeEt;
    private ImageButton imageButton;
    private Button button;
    private ProgressDialog progressDSynch;
    private RadioGroup rdoGrpAutoSync;
    private RadioButton rdoBtnAutoSync, rdoBtnPushSync;
    private LinearLayout linPeriodicity;
    private ImageView imgPeriodicityDivider;

    private Dao dao;
    private User user;
    private GestionAcces gt;
    private SyncroTeamBaseActivity syncroTeamBaseActivity;
    private BaseFragment baseFragment;
    private boolean islastSyncOn;
    private boolean isPushSyncOn;
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    private PendingIntent pi, pi1,pITrackParams;

    private static final String TAG = LastSyncFragmentHelper.class.getSimpleName();

    public LastSyncFragmentHelper(SyncroTeamBaseActivity syncroTeamBaseActivity, BaseFragment baseFragment) {
        this.syncroTeamBaseActivity = syncroTeamBaseActivity;
        this.baseFragment = baseFragment;
        dao = DaoManager.getInstance();
        alarmManager = (AlarmManager) syncroTeamBaseActivity.getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public View inflateLayout(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.layout_last_syncronization, container, false);
        initiateView(view);
        return view;
    }

    @Override
    public void initiateView(View v) {
        imageButton = (ImageButton) v.findViewById(R.id.onOffLastSyncronization);
        syncTimeEt = (EditText) v.findViewById(R.id.dataPreiodTv);
        button = (Button) v.findViewById(R.id.saveButton);
        rdoGrpAutoSync = (RadioGroup) v.findViewById(R.id.rdoGrpSync);
        rdoBtnAutoSync = (RadioButton) v.findViewById(R.id.rdoBtnAutoSync);
        rdoBtnPushSync = (RadioButton) v.findViewById(R.id.rdoBtnPushSync);
        linPeriodicity = (LinearLayout) v.findViewById(R.id.linPeriodicity);
        imgPeriodicityDivider = (ImageView) v.findViewById(R.id.imgPeriodictyDivider);

        button.setOnClickListener(mOnClickListener);
        imageButton.setOnClickListener(mOnClickListener);
        rdoGrpAutoSync.setOnCheckedChangeListener(mOnCheckedChangeListener);

        setPendingIntents();

        setViewForSyncs();

        islastSyncOn = SharedPref.getIsSyncOn(syncroTeamBaseActivity);
        isPushSyncOn = SharedPref.getIsPushSyncOn(syncroTeamBaseActivity);

        EventBus.getDefault().post(new EnableSynchronizationAddJobEvent());

    }

    public void setViewForSyncs() {

        syncTimeEt.setText(SharedPref.getSyncInterval(syncroTeamBaseActivity) + "");

        if (SharedPref.getIsSyncOn(syncroTeamBaseActivity)) {
            imageButton
                    .setImageResource(R.drawable.traking_on_btn_tracking_detail);
            enableRadioGroup();
            rdoBtnAutoSync.setChecked(true);
            showPeriodicity();
        } else if (SharedPref.getIsPushSyncOn(syncroTeamBaseActivity)) {
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
    }

    private void setPendingIntents() {
//        pi = PendingIntent.getService(syncroTeamBaseActivity,
//                0, new Intent(syncroTeamBaseActivity,
//                        TrackingService.class),
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        pi1 = PendingIntent.getService(syncroTeamBaseActivity,
//                0, new Intent(syncroTeamBaseActivity,
//                        TrackingServiceFrequency.class),
//                PendingIntent.FLAG_UPDATE_CURRENT);

        pITrackParams=initializePendingIntent();
    }

    /**
     * Intializin the pending intent for the tracking service
     *
     * @return Pending intent
     */
    private PendingIntent initializePendingIntent() {
        Intent trackingParamsIntent = new Intent(syncroTeamBaseActivity,
                TrackingParameterService.class);
        trackingParamsIntent.putExtra("from_pending_intent", true);

        PendingIntent pendingIntent;
        //Behaviour changes supporting android 12
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // Create a PendingIntent using FLAG_IMMUTABLE
            pendingIntent= PendingIntent.getService(syncroTeamBaseActivity,
                    0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT|
                            PendingIntent.FLAG_IMMUTABLE);
        } else {

            pendingIntent= PendingIntent.getService(syncroTeamBaseActivity,
                    0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return pendingIntent;
//        return PendingIntent.getService(syncroTeamBaseActivity,
//                0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    }


    /**
     * For canceling the Tracking service when conditions are met
     */
    private void cancelTrackingAlarm() {
        syncroTeamBaseActivity.stopService(new Intent(syncroTeamBaseActivity, TrackingParameterService.class));
        AlarmManager am2 = (AlarmManager) syncroTeamBaseActivity
                .getSystemService(Context.ALARM_SERVICE);
        am2.cancel(pITrackParams);
    }

    /**
     * Trigger pending intent at a later time
     *
     * @param cal
     * @param pITrackParams
     */
    private void triggeringTrackingAtSpecificTime(Calendar cal, PendingIntent pITrackParams) {
        AlarmManager trackingParamsAlarm = (AlarmManager) syncroTeamBaseActivity
                .getSystemService(Context.ALARM_SERVICE);
        trackingParamsAlarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pITrackParams);

    }

    @Override
    public void doOnSyncronize() {

    }

    @Override
    public void onReturnToActivity(int requestCode) {

    }

    /**
     * Sets the alarm manager to auto syncronise data.
     */
    @SuppressLint("NewApi")
    protected void setAlarmManagerToAutoSyncroniseData() {

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        Intent intent = ((SyncroTeamApplication) syncroTeamBaseActivity.getApplication())
                .getAutoSyncIntent();

        String stringInterval = syncTimeEt.getText().toString();
        int interval = Integer.parseInt(stringInterval);

        intent.putExtra(KEYS.SyncronizationSettings.SYNC_INTERVAL, interval);

//        alarmIntent = PendingIntent.getBroadcast(syncroTeamBaseActivity, 0, intent,
//                PendingIntent.FLAG_UPDATE_CURRENT);

        //Behaviour changes supporting android 12
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // Create a PendingIntent using FLAG_IMMUTABLE
            alarmIntent = PendingIntent.getBroadcast(syncroTeamBaseActivity, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_IMMUTABLE);
        } else {

            alarmIntent = PendingIntent.getBroadcast(syncroTeamBaseActivity, 0, intent,
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
                    syncroTeamBaseActivity.startActivity(new Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM));

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

        SharedPref.setIsSyncOn(true, syncroTeamBaseActivity);
        SharedPref.setSyncInterval(interval, syncroTeamBaseActivity);

    }

    /**
     * Removes the alarm manager to auto syncronise data.
     */
    public void removeAlarmManagerToAutoSyncroniseData() {
        // TODO Auto-generated method stub

        Intent intent = ((SyncroTeamApplication) syncroTeamBaseActivity.getApplication())
                .getAutoSyncIntent();
//        alarmIntent = PendingIntent.getBroadcast(syncroTeamBaseActivity, 0, intent,
//                PendingIntent.FLAG_UPDATE_CURRENT);

        //Behaviour changes supporting android 12
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // Create a PendingIntent using FLAG_IMMUTABLE
            alarmIntent = PendingIntent.getBroadcast(syncroTeamBaseActivity, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_IMMUTABLE);
        } else {

            alarmIntent = PendingIntent.getBroadcast(syncroTeamBaseActivity, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        }

        alarmManager.cancel(alarmIntent);
        gt = dao.getAcces();

        SharedPref.setIsSyncOn(false, syncroTeamBaseActivity);
        SharedPref.setSyncInterval(
                Integer.parseInt(syncroTeamBaseActivity.getResources().getString(
                        R.string.hintDefaultTimeInterval)), syncroTeamBaseActivity);

    }

    private void setPushSyncOn() {
        SharedPref.setIsPushSyncOn(true, syncroTeamBaseActivity);
    }

    private void setPushSyncOff() {
        SharedPref.setIsPushSyncOn(false, syncroTeamBaseActivity);
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
                    syncroTeamBaseActivity,
                    syncroTeamBaseActivity.getResources().getString(
                            R.string.textEmptyTimeInterval));

            isDataValid = false;
        } else if (!TextUtils.isDigitsOnly(interval)) {
            DialogUtils.showInfoDialog(
                    syncroTeamBaseActivity,
                    syncroTeamBaseActivity.getResources().getString(
                            R.string.textCharcterTimeInterval));
            isDataValid = false;
        } else if (Integer.parseInt(interval) < Integer.parseInt(syncroTeamBaseActivity.getResources()
                .getString(R.string.textMinimumTimeInterval))) {
            DialogUtils.showInfoDialog(
                    syncroTeamBaseActivity,
                    syncroTeamBaseActivity.getResources().getString(
                            R.string.textValueLessThanMinimumValue));
            isDataValid = false;
        }

        return isDataValid;
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

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.saveButton:
                    if (!islastSyncOn && !isPushSyncOn) {
                        removeAlarmManagerToAutoSyncroniseData();
                        setPushSyncOff();
                        MaterialDesignUtils.getInstance(syncroTeamBaseActivity).showSnackBar(view, syncroTeamBaseActivity.getString(R.string.txt_data_saved));
                    } else if (islastSyncOn) {
                        if (validateDate()) {
                            setAlarmManagerToAutoSyncroniseData();
                            setPushSyncOff();
                            MaterialDesignUtils.getInstance(syncroTeamBaseActivity).showSnackBar(view, syncroTeamBaseActivity.getString(R.string.txt_data_saved));
                        }
                    } else {
                        setPushSyncOn();
//                        removeAlarmManagerToAutoSyncroniseData();
                        MaterialDesignUtils.getInstance(syncroTeamBaseActivity).showSnackBar(view, syncroTeamBaseActivity.getString(R.string.txt_data_saved));
                    }

                    CommonUtils.hideKeyboard(syncroTeamBaseActivity);

                    break;

                case R.id.onOffLastSyncronization:
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
                    break;
            }
        }
    };

    private RadioGroup.OnCheckedChangeListener mOnCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
            RadioButton rdoBtn = (RadioButton) view.findViewById(checkedId);
            String strChecked = rdoBtn.getText().toString();
            if (strChecked
                    .equalsIgnoreCase(syncroTeamBaseActivity.getString(R.string.txt_auto_sync))) {
                islastSyncOn = true;
                isPushSyncOn = false;
                showPeriodicity();
            } else {
                islastSyncOn = false;
                isPushSyncOn = true;
                hidePeriodicity();
            }
        }
    };

}