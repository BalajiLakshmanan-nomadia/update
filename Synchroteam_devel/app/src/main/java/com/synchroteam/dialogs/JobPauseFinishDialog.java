package com.synchroteam.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.synchroteam.events.JobPauseFinishEvent;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.ClockInOutUtil;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;

import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;

/**
 * Created by user on 23/8/17.
 */

public class JobPauseFinishDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = JobPauseFinishDialog.class.getSimpleName();
    private static final String KEY_JOB_NUMBER = "job_number";
    private static final String KEY_JOB_TIMER = "job_timer";
    private static final String KEY_JOB_IS_MANDATORY_FIELDS_FILLED = "job_is_mandatory_fields_are_filled";

    private TextView txtPause, txtFinish, txtJobNumber;
    private Chronometer timerDurationTime;
    private LinearLayout layClose, layAlertMandatoryNotFilled;
    private Typeface typeFaceAvenirMedium;
    Context context;

    Handler updateHandler;
    Runnable runnable;

    public static JobPauseFinishDialog newInstance(String jobNumber, long jobTimer, boolean isMandatoryFieldsAreFilled) {

        JobPauseFinishDialog fragment = new JobPauseFinishDialog();
        Bundle args = new Bundle();
        args.putString(KEY_JOB_NUMBER, jobNumber);
        args.putLong(KEY_JOB_TIMER, jobTimer);
        args.putBoolean(KEY_JOB_IS_MANDATORY_FIELDS_FILLED, isMandatoryFieldsAreFilled);
        fragment.setArguments(args);

        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_job_pause_finish, container);

        context = getActivity();

        setCustomTypeface();

        initializeUI(view);

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

    private void initializeUI(View view) {

        txtJobNumber = (TextView) view.findViewById(R.id.txt_job_number);
        timerDurationTime = (Chronometer) view.findViewById(R.id.timer_duration_time);
        txtPause = (TextView) view.findViewById(R.id.txt_pause);
        txtFinish = (TextView) view.findViewById(R.id.txt_finish);
        layClose = (LinearLayout) view.findViewById(R.id.lay_close);
        layAlertMandatoryNotFilled = (LinearLayout) view.findViewById(R.id.lay_alert_mandatory_not_filled);

        settingUIToFinishJob(getArguments().getBoolean(KEY_JOB_IS_MANDATORY_FIELDS_FILLED));

        timerDurationTime.setTypeface(typeFaceAvenirMedium);
//        timerDurationTime.setText(getActivity().getResources().getString(R.string.textDurationLableTv) + ": 0 min");
        timerDurationTime.setText(getActivity().getResources().getString(R.string.textDurationLableTv) + "00:00:00");
        txtJobNumber.setText(getArguments().getString(KEY_JOB_NUMBER));

//        startTimer(getArguments().getLong(KEY_JOB_TIMER));
//        timerDurationTime.setOnChronometerTickListener(chronometerTickListenerOnStartOrContinue);

        timerDurationTime.setText(getActivity().getResources().
                getString(R.string.textDurationLableTv) + "  " +
                parseTimeNew(getArguments().getLong(KEY_JOB_TIMER)));
        runTimerHandler(getArguments().getLong(KEY_JOB_TIMER));

        txtPause.setOnClickListener(this);
        txtFinish.setOnClickListener(this);
        layClose.setOnClickListener(this);
    }

    private void settingUIToFinishJob(boolean isMandatoryFieldsCompleted) {
        if (isMandatoryFieldsCompleted) {
            txtFinish.setEnabled(true);
            txtFinish.setTextColor(getActivity().getResources().getColor(R.color.black));
            layAlertMandatoryNotFilled.setVisibility(View.GONE);
        } else {
            txtFinish.setEnabled(false);
            txtFinish.setTextColor(getActivity().getResources().getColor(R.color.text_light));
            layAlertMandatoryNotFilled.setVisibility(View.VISIBLE);
        }
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
        dialogHeight = (int) (screenHeight * Double.parseDouble(this.getResources().getString(R.string.dialog_job_pause_height)));
        dialogWidth = (int) (screenWidth * Double.parseDouble(this.getResources().getString(R.string.dialog_job_pause_width)));
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
        dialog.setCancelable(true);
        return dialog;
    }

    private void showToastMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_pause:
                if (updateHandler != null && runnable != null)
                    updateHandler.removeCallbacks(runnable);
                EventBus.getDefault().post(new JobPauseFinishEvent(KEYS.JobDetails.KEY_PAUSE_JOB));
                /** start timer for inactivity */
                ClockInOutUtil.saveLastClockedInTime(getActivity());
                ClockInOutUtil.startAlarmForTimeOut(getActivity());
                dismiss();
                break;
            case R.id.txt_finish:
                if (updateHandler != null && runnable != null)
                    updateHandler.removeCallbacks(runnable);

                EventBus.getDefault().post(new JobPauseFinishEvent(KEYS.JobDetails.KEY_FINISH_JOB));
                /** start timer for inactivity */
                ClockInOutUtil.saveLastClockedInTime(getActivity());
                ClockInOutUtil.startAlarmForTimeOut(getActivity());

                dismiss();
                break;

            case R.id.lay_close:
                if (updateHandler != null && runnable != null)
                    updateHandler.removeCallbacks(runnable);
                dismiss();
                break;
        }
    }

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


    private void runTimerHandler(final long currentTime) {
        final long[] millSecToAdd = {currentTime};

        updateHandler = new Handler();

        runnable = new Runnable() {

            public void run() {
                millSecToAdd[0] += 1000;

                timerDurationTime.setText(parseTimeNew(millSecToAdd[0]));
                timerDurationTime.setText(getActivity().getResources().
                        getString(R.string.textDurationLableTv) + "  " +
                        parseTimeNew(millSecToAdd[0]));

                updateHandler.postDelayed(this, 1000); // determines the execution interval
            }

        };

        updateHandler.postDelayed(runnable, 1000);
    }


    /**
     * The chronometer tick listener on start or continue.
     */
    Chronometer.OnChronometerTickListener chronometerTickListenerOnStartOrContinue = new Chronometer.OnChronometerTickListener() {

        @Override
        public void onChronometerTick(Chronometer chronometer) {

            long timeElapsed = System.currentTimeMillis()
                    - chronometer.getBase();

            Logger.log("TAG", "JOB CHECK TIME START END PARSED" +
                    " TIME VALUES DIALOG CHRONO ====>" + parseTimeNew(timeElapsed));

            int seconds = (int) (timeElapsed / 1000);

            try {

                int hours = seconds / 3600;
                int minutes = (seconds / 60) - (hours * 60);
                seconds = seconds - (hours * 3600) - (minutes * 60);
                String minutesString = null;
                String hoursString = null;
                String secString = null;


                if (minutes < 10) {
                    minutesString = "0" + minutes;
                } else {
                    minutesString = minutes + "";
                }

                if (seconds < 10) {
                    secString = "0" + seconds;
                } else {
                    secString = seconds + "";
                }

//                minutesString = minutes + "";

//                if (hours == 0) {
//                    chronometer.setText(getActivity().getResources().getString(R.string.textDurationLableTv) + " 00:" + minutesString + ":" + minutesString);
//                } else if (hours < 10) {
//                    hoursString = "0" + hours;
//                    chronometer.setText(getActivity().getResources().getString(R.string.textDurationLableTv) + " " + hoursString + ":" + minutesString+ ":" + minutesString );
//                } else {
//                    hoursString = hours + "";
//                    chronometer.setText(getActivity().getResources().getString(R.string.textDurationLableTv) + " " + hoursString + ":" + minutesString + ":" + minutesString);
//                }

                //new changes
                if (hours == 0) {
                    chronometer.setText(getActivity().getResources().getString(R.string.textDurationLableTv) + " 00:" + minutesString + ":" + secString);
                } else if (hours < 10) {
                    hoursString = "0" + hours;
                    chronometer.setText(getActivity().getResources().getString(R.string.textDurationLableTv) + " " + hoursString + ":" + minutesString + ":" + secString);
                } else {
                    hoursString = hours + "";
                    chronometer.setText(getActivity().getResources().getString(R.string.textDurationLableTv) + " " + hoursString + ":" + minutesString + ":" + secString);
                }


            } catch (Exception e) {
                Logger.printException(e);

            }

        }
    };

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (updateHandler != null)
            updateHandler.removeCallbacks(runnable);
    }

    public String parseTimeNew(long milliseconds) {

        final String FORMAT = "%02d:%02d:%02d";

        return String.format(FORMAT,
                TimeUnit.MILLISECONDS.toHours(milliseconds),
                TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(milliseconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(milliseconds)));

    }
}