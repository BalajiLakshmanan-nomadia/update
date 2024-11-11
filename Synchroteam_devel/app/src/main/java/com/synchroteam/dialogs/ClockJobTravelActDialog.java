package com.synchroteam.dialogs;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.synchroteam.beans.Conge;
import com.synchroteam.beans.JobDetailsModel;
import com.synchroteam.beans.UnavailabilityBeans;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.events.ActivityUpdateEvent;
import com.synchroteam.events.ClockedOutEvent;
import com.synchroteam.synchroteam.SyncoteamNavigationActivity;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.ClockInOutUtil;
import com.synchroteam.utils.Commons;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;

/**
 * Created by Trident
 */

public class ClockJobTravelActDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = ClockJobTravelActDialog.class.getSimpleName();
    private TextView txtClockInOut, txtJob, txtDrive, txtActivity;
    private LinearLayout layClose;
    Context context;
    private Dao dataAccessObject;
    private User user;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
    private Calendar cal;
    private static final String KEY_SHOW_CLOCK_IN_OUT = "clockInOut";
    boolean isClockInOut;
    private Chronometer timerDurationTime;
    private Typeface typeFaceAvenirMedium;
    private static final String FORMAT = "%02d:%02d:%02d";
    private String idInterv;

    LinearLayout linClockInOut;

    public static ClockJobTravelActDialog newInstance(boolean clockInOut) {

        ClockJobTravelActDialog fragment = new ClockJobTravelActDialog();
        Bundle args = new Bundle();
        args.putBoolean(KEY_SHOW_CLOCK_IN_OUT, clockInOut);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_start_job_activity, container);

        dataAccessObject = DaoManager.getInstance();

        user = dataAccessObject.getUser();

        idInterv = dataAccessObject.getStartedJobId();

        getArgumentValues();

        initializeUI(view);

        updateViewForClockInOut();

        totalClockInForToday();


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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();

        setCustomTypeface();

    }

    private void totalClockInForToday() {
        String filterDate = dataAccessObject.getTotalClockExactDate();

        long totalClockIn = dataAccessObject.getTotalClockIn(filterDate);
        showCurrentTimer(totalClockIn);
        Conge vectCongeClockIn = checkClockedIn();
        if (vectCongeClockIn != null) {
            runTimerHandler(totalClockIn);
        }

    }

    private void runTimerHandler(final long currentTime) {
        final long[] millSecToAdd = {currentTime};

        final Handler updateHandler = new Handler();

        Runnable runnable = new Runnable() {

            public void run() {
                millSecToAdd[0] += 1000;


                timerDurationTime.setText(parseTime(millSecToAdd[0]));
                updateHandler.postDelayed(this, 1000); // determines the execution interval
            }

        };

        updateHandler.postDelayed(runnable, 1000);
    }


    public String parseTime(long milliseconds) {
        return String.format(FORMAT,
                TimeUnit.MILLISECONDS.toHours(milliseconds),
                TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(milliseconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(milliseconds)));
    }

    private void showCurrentTimer(long previousTime) {

        int seconds = (int) (previousTime / 1000);

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

            if (hours == 0) {
                timerDurationTime.setText(" 00:" + minutesString + ":" + secondsString);
            } else if (hours < 10) {
                hoursString = "0" + hours;
                timerDurationTime.setText(" " + hoursString + ":" + minutesString + ":" + secondsString);
            } else {
                hoursString = hours + "";
                timerDurationTime.setText(" " + hoursString + ":" + minutesString + ":" + secondsString);
            }

        } catch (Exception e) {
            Logger.printException(e);

        }

    }

    private void getArgumentValues() {
        isClockInOut = getArguments().getBoolean(KEY_SHOW_CLOCK_IN_OUT);
    }

    private void updateViewForClockInOut() {
        if (isClockInOut) {
            txtJob.setVisibility(View.GONE);
            txtDrive.setVisibility(View.GONE);
            txtActivity.setVisibility(View.GONE);
        } else {
            txtJob.setVisibility(View.VISIBLE);
            txtDrive.setVisibility(View.VISIBLE);
            txtActivity.setVisibility(View.VISIBLE);
        }
    }

    private void initializeUI(View view) {

        cal = Calendar.getInstance();

        timerDurationTime = (Chronometer) view.findViewById(R.id.timer_duration_time);
        timerDurationTime.setTypeface(typeFaceAvenirMedium);
        timerDurationTime.setText(" 00:00:00 ");
        txtClockInOut = (TextView) view.findViewById(R.id.txt_clock_in_out);
        linClockInOut = (LinearLayout) view.findViewById(R.id.lin_clock_in_out);
        txtJob = (TextView) view.findViewById(R.id.txt_job);
        txtDrive = (TextView) view.findViewById(R.id.txt_drive);
        txtActivity = (TextView) view.findViewById(R.id.txt_activity);
        layClose = (LinearLayout) view.findViewById(R.id.lay_close);
        txtClockInOut.setOnClickListener(this);
        txtJob.setOnClickListener(this);
        txtDrive.setOnClickListener(this);
        txtActivity.setOnClickListener(this);
        linClockInOut.setOnClickListener(this);
        layClose.setOnClickListener(this);

    }

    private void settingClockInOutButtonLayout(boolean isClockModeAvailable) {
        if (isClockModeAvailable) {
//            settingDialogHeight(true);
            txtClockInOut.setVisibility(View.VISIBLE);
            linClockInOut.setVisibility(View.VISIBLE);
//            txtDrive.setVisibility(View.VISIBLE);
            if (!isClockInOut) {
                txtDrive.setVisibility(View.VISIBLE);
            }
            checkClockModeAndSettingLayout();
        } else {
            txtClockInOut.setVisibility(View.GONE);
            linClockInOut.setVisibility(View.GONE);
            txtDrive.setVisibility(View.VISIBLE);
//            settingDialogHeight(false);
        }
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
        if (user != null)
            settingClockInOutButtonLayout(dataAccessObject.checkIsClockInAvailable(user.getId()));
    }

    private void checkClockModeAndSettingLayout() {
        // checking Clocked In or Out
        Conge vectCongeClockIn = checkClockedIn();
        if (vectCongeClockIn != null) {
            txtClockInOut.setText(getActivity().getResources().getString(R.string.txt_clock_out));
        } else {
            txtClockInOut.setText(getActivity().getResources().getString(R.string.txt_clock_in));
        }


    }

    /**
     * for refreshing the current jobs list
     */
    private void refreshCurrentJobsList() {

        // update UI in Synchroteam navigation Activity
        EventBus.getDefault().post(new ActivityUpdateEvent());
    }

    private void settingDialogHeight(boolean isclockModeLargeDialog) {
        if (isclockModeLargeDialog) {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int screenHeight = metrics.heightPixels;
            int screenWidth = metrics.widthPixels;
            int dialogHeight = 0, dialogWidth = 0;
//            dialogHeight = (int) (screenHeight * Double.parseDouble(this.getResources().getString(R.string.clock_in_out_height)));
//            dialogWidth = (int) (screenWidth * Double.parseDouble(this.getResources().getString(R.string.clock_in_out_width)));
            if (isClockInOut) {
                dialogHeight = (int) (screenHeight * Double.parseDouble(this.getResources().getString(R.string.driving_height)));
                dialogWidth = (int) (screenWidth * Double.parseDouble(this.getResources().getString(R.string.driving_width)));
            } else {
                dialogHeight = (int) (screenHeight * Double.parseDouble(this.getResources().getString(R.string.clock_in_out_height)));
                dialogWidth = (int) (screenWidth * Double.parseDouble(this.getResources().getString(R.string.clock_in_out_width)));
            }

            getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
        } else {
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int screenHeight = metrics.heightPixels;
            int screenWidth = metrics.widthPixels;
            int dialogHeight = 0, dialogWidth = 0;
            dialogHeight = (int) (screenHeight * Double.parseDouble(this.getResources().getString(R.string.clock_in_out_height_small)));
            dialogWidth = (int) (screenWidth * Double.parseDouble(this.getResources().getString(R.string.clock_in_out_width)));
            getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
        }
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
        return dialog;
    }

    private void showToastMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lay_close:
                dismiss();
                break;
            case R.id.txt_clock_in_out:

                Conge vectCongeClockIn = checkClockedIn();
                if (vectCongeClockIn != null) {
                    // clocked in, So we do clocked out
                    if (finishClockedInMode()) {

                        updateInterventionToSuspendState();

                        checkClockModeAndSettingLayout();

//                        refreshCurrentJobsList();

                        /** cancel the inactivity alarm */
                        ClockInOutUtil.cancelAlarmForTimeOut(getActivity());
//                        showToastMessage(getActivity().getResources().getString(R.string.txt_clock_out));


                        //new changes
                        dismiss();
                    }
                } else {
                    cal = Calendar.getInstance();
                    String currentDateStr = sdf.format(cal.getTime());
                    // new clocked in entry to T_CONGE
                    UnavailabilityBeans clockInActivity = dataAccessObject.getClockInActivity();
                    String uniqueID = dataAccessObject.addUnavailabilityAndReturnID(null, clockInActivity.getUnavailabilityID(), 0, currentDateStr, null, "");
                    if (uniqueID != null) {
                        checkClockModeAndSettingLayout();
                        refreshCurrentJobsList();
                        ClockInOutUtil.saveLastClockedInTime(getActivity());
                        ClockInOutUtil.startAlarmForTimeOut(getActivity());
//                        showToastMessage(getActivity().getResources().getString(R.string.txt_clock_in));

                        //new changes
                        dismiss();

                    } else {
                        showToastMessage(getActivity().getResources().getString(R.string.msg_error));
                    }
                }
                break;
            case R.id.lin_clock_in_out:
                txtClockInOut.performClick();
                break;
            case R.id.txt_job:
                onStartJobDialogClicked();
//                JobListDialog.newInstance(Commons.IS_JOB).show(getActivity().getSupportFragmentManager(), "");

                break;
            case R.id.txt_drive:
                DrivingOrActivityListDialog.newInstance(Commons.IS_DRIVING).show(getActivity().getSupportFragmentManager(), "");
                dismiss();
                break;
            case R.id.txt_activity:
                DrivingOrActivityListDialog.newInstance(Commons.IS_ACTIVITY).show(getActivity().getSupportFragmentManager(), "");
                dismiss();
                break;
        }
    }

    /**
     * Update any running intervention to suspend state when clock out
     */
    public void updateInterventionToSuspendState() {

        if (idInterv != null && idInterv.length() > 0) {

            if (dataAccessObject.updateStatutInterv(4, idInterv))
                dataAccessObject.setTimeClotIntervention(idInterv, user.getId() + "", "");

            EventBus.getDefault().post(new ClockedOutEvent());

        } else {
            refreshCurrentJobsList();
        }
    }

    public void onStartJobDialogClicked() {

        JobDetailsModel model = dataAccessObject.getCurrentActiveJobs();
        if (model != null) {
            dismiss();
            ((SyncoteamNavigationActivity) getActivity()).navigateToJobDetailActivity(model);
        } else {
            JobListDialog.newInstance(Commons.IS_JOB).show(getFragmentManager(), "");
            dismiss();
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
        String currentDate = sdf.format(cal.getTime());
        while (enindisp.hasMoreElements()) {
            Conge indisp = enindisp.nextElement();
            if (indisp.getDtFin() == null) {
                clockedOut = dataAccessObject.updateClockedOutEndTime(indisp.getIdConge(), currentDate);
            }
        }
        return clockedOut;
    }


}