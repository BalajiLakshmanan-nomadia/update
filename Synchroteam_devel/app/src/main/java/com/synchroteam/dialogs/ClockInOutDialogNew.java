package com.synchroteam.dialogs;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.synchroteam.beans.Conge;
import com.synchroteam.beans.UnavailabilityBeans;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.events.ActivityUpdateEvent;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.ClockInOutUtil;
import com.synchroteam.utils.DaoManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;

import de.greenrobot.event.EventBus;

/**
 * Created by Trident
 */

public class ClockInOutDialogNew extends DialogFragment implements View.OnClickListener {
    private static final String TAG = ClockInOutDialogNew.class.getSimpleName();
    private TextView txtClockInOut;
    private LinearLayout layClose;
    Context context;
    private Dao dataAccessObject;
    private User user;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
    private Calendar cal;
    private static final String KEY_SHOW_CLOCK_IN_OUT = "clockInOut";
    boolean isClockInOut;

    public static ClockInOutDialogNew newInstance(boolean clockInOut) {

        ClockInOutDialogNew fragment = new ClockInOutDialogNew();
        Bundle args = new Bundle();
        args.putBoolean(KEY_SHOW_CLOCK_IN_OUT, clockInOut);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout_dialog_clock_in_out, container);

        dataAccessObject = DaoManager.getInstance();

        user = dataAccessObject.getUser();

        getArgumentValues();

        initializeUI(view);

        totalClockInForToday();

        return view;
    }

    /**
     * For getting the total clock in for the day
     */
    private void totalClockInForToday() {

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();

    }

    private void getArgumentValues() {
        isClockInOut = getArguments().getBoolean(KEY_SHOW_CLOCK_IN_OUT);
    }


    private void initializeUI(View view) {

        cal = Calendar.getInstance();

        txtClockInOut = (TextView) view.findViewById(R.id.txt_clock_in_out);

        layClose = (LinearLayout) view.findViewById(R.id.lay_close);
        txtClockInOut.setOnClickListener(this);

        layClose.setOnClickListener(this);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onResume() {
        super.onResume();
        checkClockModeAndSettingLayout();
    }


    private void checkClockModeAndSettingLayout() {
        // checking Clocked In or Out
        Conge vectCongeClockIn = checkClockedIn();
        if (vectCongeClockIn != null) {
            txtClockInOut.setText(getActivity().getResources().getString(R.string.txt_clock_out));
        } else {
            txtClockInOut.setText(getActivity().getResources().getString(R.string.txt_clock_in));
        }
        // update UI in Synchroteam navigation Activity
        EventBus.getDefault().post(new ActivityUpdateEvent());
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
                        checkClockModeAndSettingLayout();
                        ClockInOutUtil.cancelAlarmForTimeOut(getActivity());
                        dismiss();
                    }
                } else {
                    String currentDateStr = sdf.format(cal.getTime());
                    // new clocked in entry to T_CONGE
                    UnavailabilityBeans clockInActivity = dataAccessObject.getClockInActivity();
                    String uniqueID = dataAccessObject.addUnavailabilityAndReturnID(null, clockInActivity.getUnavailabilityID(), 0, currentDateStr, null, "");
                    if (uniqueID != null) {
                        checkClockModeAndSettingLayout();
                        ClockInOutUtil.saveLastClockedInTime(getActivity());
                        ClockInOutUtil.startAlarmForTimeOut(getActivity());
                        dismiss();

                    } else {
                        showToastMessage(getActivity().getResources().getString(R.string.msg_error));
                    }
                }
                break;
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