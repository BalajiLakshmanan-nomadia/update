package com.synchroteam.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.synchroteam.events.JobStartResumeEvent;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.ClockInOutUtil;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;

import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;

/**
 * Created by Trident
 */

public class JobStartResumeDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = JobStartResumeDialog.class.getSimpleName();
    private static final String KEY_JOB_ACTION_NAME = "job_action_name";
    private static final String KEY_JOB_NUMBER = "job_number";
    private static final String KEY_JOB_TIMER = "job_timer";
    private static final long AUTO_START_ACTIVITY = 30000;

    private TextView txtJobActionName, txtOK, txtUndo, txtJobNumber;
    private Chronometer timerDurationTime;
    private Typeface typeFaceAvenirMedium;
    Handler autoStartHandler;
    Runnable runnableAutoStart;
    LinearLayout layClose;

    Context context;

    public static JobStartResumeDialog newInstance(String jobAction, String jobNumber, long startResumeJobTimer) {

        JobStartResumeDialog fragment = new JobStartResumeDialog();
        Bundle args = new Bundle();
        args.putString(KEY_JOB_NUMBER, jobNumber);
        args.putString(KEY_JOB_ACTION_NAME, jobAction);
        args.putLong(KEY_JOB_TIMER, startResumeJobTimer);
        fragment.setArguments(args);

        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_job_start_resume, container);

        context = getActivity();

        setCustomTypeface();

        initializeUI(view);

//        autoJobStart();

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

    private void autoJobStart() {
        autoStartHandler = new Handler();
        runnableAutoStart = new Runnable() {
            @Override
            public void run() {
                doStartOrResumeJob();
            }
        };
        autoStartHandler.postDelayed(runnableAutoStart, AUTO_START_ACTIVITY);
    }

    private void doStartOrResumeJob() {
        if (getArguments().getString(KEY_JOB_ACTION_NAME).equals(KEYS.JobDetails.KEY_START_JOB)) {
            EventBus.getDefault().post(new JobStartResumeEvent(KEYS.JobDetails.KEY_START_JOB));
        } else if (getArguments().getString(KEY_JOB_ACTION_NAME).equals(KEYS.JobDetails.KEY_RESUME_JOB)) {
            EventBus.getDefault().post(new JobStartResumeEvent(KEYS.JobDetails.KEY_RESUME_JOB));
        }
        dismiss();
    }

    private void initializeUI(View view) {

        txtJobNumber = (TextView) view.findViewById(R.id.txt_job_number);
        txtJobActionName = (TextView) view.findViewById(R.id.txt_job_action_name);
        timerDurationTime = (Chronometer) view.findViewById(R.id.timer_duration_time);
        txtOK = (TextView) view.findViewById(R.id.txt_ok);
        txtUndo = (TextView) view.findViewById(R.id.txt_undo);
        layClose = (LinearLayout) view.findViewById(R.id.lay_close);

        timerDurationTime.setTypeface(typeFaceAvenirMedium);
//        timerDurationTime.setText(getActivity().getResources().getString(R.string.textDurationLableTv) + ": 0 min");
        timerDurationTime.setText(getActivity().getResources().getString(R.string.textDurationLableTv) + " 00:00:00");

        txtJobNumber.setText(getArguments().getString(KEY_JOB_NUMBER));
        //for marquee
        txtJobNumber.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        txtJobNumber.setSingleLine(true);
        txtJobNumber.setSelected(true);
        if (getArguments().getString(KEY_JOB_ACTION_NAME).equals(KEYS.JobDetails.KEY_START_JOB)) {
//            txtJobActionName.setText(getActivity().getResources().getString(R.string.tt_start));
            txtJobActionName.setText(getActivity().getResources().getString(R.string.textStartLable));
//            txtOK.setText(getActivity().getResources().getString(R.string.txt_dialog_activity_start));
            txtOK.setText(getActivity().getResources().getString(R.string.textStartLable).toUpperCase());

        } else if (getArguments().getString(KEY_JOB_ACTION_NAME).equals(KEYS.JobDetails.KEY_RESUME_JOB)) {
            txtJobActionName.setText(getActivity().getResources().getString(R.string.txt_resume));
            txtOK.setText(getActivity().getResources().getString(R.string.txt_resume));
        }

        showCurrentTimer(getArguments().getLong(KEY_JOB_TIMER));

//        timerDurationTime.setText(getActivity().getResources().getString(R.string.textDurationLableTv) + " "+
//                parseTimeNew(getArguments().getLong(KEY_JOB_TIMER)));

        timerDurationTime.setText(getActivity().getResources().
                getString(R.string.textDurationLableTv) + "  " +
                parseTimeNew(getArguments().getLong(KEY_JOB_TIMER)));

        txtOK.setOnClickListener(this);
        txtUndo.setOnClickListener(this);
        layClose.setOnClickListener(this);
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
        dialogHeight = (int) (screenHeight * Double.parseDouble(this.getResources().getString(R.string.dialog_job_details_height)));
        dialogWidth = (int) (screenWidth * Double.parseDouble(this.getResources().getString(R.string.dialog_job_details_width)));
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
            case R.id.txt_ok:
                if (autoStartHandler != null && runnableAutoStart != null)
                    autoStartHandler.removeCallbacks(runnableAutoStart);

                if (getArguments().getString(KEY_JOB_ACTION_NAME).equals(KEYS.JobDetails.KEY_START_JOB)) {
                    EventBus.getDefault().post(new JobStartResumeEvent(KEYS.JobDetails.KEY_START_JOB));
                } else if (getArguments().getString(KEY_JOB_ACTION_NAME).equals(KEYS.JobDetails.KEY_RESUME_JOB)) {
                    EventBus.getDefault().post(new JobStartResumeEvent(KEYS.JobDetails.KEY_RESUME_JOB));
                }
                /** cancel the inactivity alarm */
                ClockInOutUtil.cancelAlarmForTimeOut(getActivity());
                dismiss();
                break;
            case R.id.txt_undo:
                if (autoStartHandler != null && runnableAutoStart != null)
                    autoStartHandler.removeCallbacks(runnableAutoStart);
                dismiss();
                break;
            case R.id.lay_close:
                if (autoStartHandler != null && runnableAutoStart != null)
                    autoStartHandler.removeCallbacks(runnableAutoStart);
                dismiss();
                break;
        }
    }

    private void showCurrentTimer(long previousTime) {

        int seconds = (int) (previousTime / 1000);

        // Date date=new Date(timeElapsed);

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

//            minutesString = minutes + "";

//            if (hours == 0) {
////                timerDurationTime.setText(getActivity().getResources().getString(R.string.textDurationLableTv) + ": " + minutesString + " min");
//                timerDurationTime.setText(getActivity().getResources().getString(R.string.textDurationLableTv) + " 00:" + minutesString);
//            } else if (hours < 10) {
//                hoursString = "0" + hours;
////                timerDurationTime.setText(getActivity().getResources().getString(R.string.textDurationLableTv) + ": " + hoursString + " h " + minutesString + " min");
//                timerDurationTime.setText(getActivity().getResources().getString(R.string.textDurationLableTv) + " " + hoursString + ":" + minutesString);
//            } else {
//                hoursString = hours + "";
////                timerDurationTime.setText(getActivity().getResources().getString(R.string.textDurationLableTv) + ": " + hoursString + " h " + minutesString + " min");
//                timerDurationTime.setText(getActivity().getResources().getString(R.string.textDurationLableTv) + " " + hoursString + ":" + minutesString);
//            }

            //new changes
            if (hours == 0) {

                timerDurationTime.setText(getActivity().getResources().getString(R.string.textDurationLableTv) + " 00:" + minutesString + ":" + secString);
            } else if (hours < 10) {
                hoursString = "0" + hours;
                timerDurationTime.setText(getActivity().getResources().getString(R.string.textDurationLableTv) + " " + hoursString + ":" + minutesString + ":" + secString);
            } else {
                hoursString = hours + "";
                timerDurationTime.setText(getActivity().getResources().getString(R.string.textDurationLableTv) + " " + hoursString + ":" + minutesString + ":" + secString);
            }

            timerDurationTime.setText(getActivity().getResources().getString(R.string.textDurationLableTv) + "  " +
                    parseTimeNew(previousTime));

        } catch (Exception e) {
            Logger.printException(e);

        }

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (autoStartHandler != null)
            autoStartHandler.removeCallbacks(runnableAutoStart);
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