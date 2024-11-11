package com.synchroteam.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.dao.Dao;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.CommonUtils;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DateFormatUtils;
import com.synchroteam.utils.Logger;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

/**
 * Created by Trident on 23/8/17.
 */

public class RescheduleIntervenDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = RescheduleIntervenDialog.class.getSimpleName();

    private TextView txtOk;
    private LinearLayout layClose;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");

    private EditText edtEndDate, edtStartDate, edtStartTime, edtEndTime;
    private Date mFinalStartTimeDate, mFinalTimeEndDate;
    /**
     * The alert dialog view2.
     */
    private View alertDialogView, alertDialogView2;

    /**
     * The is format24.
     */
    private static boolean isFormat24;

    /**
     * The duree.
     */
    private Date duree;

    /**
     * The m min m.
     */
    private int mJour, mMois, mAnnee, mEndJour, mEndMois, mEndAnnee, mH1, mH2, mMin1, mMin2;

    private DateFormat df;

    Date myStartDate, mDefultMinOneMin;

    Context jobDetails;
    View view;
    private Dao dataAccessObject;
    String intervention;

    private static final String INTERVENTION = "interven_id";

    private static TakeBackActionListener takeBackActionListener;

    /**
     * The Interface EnterDialogInterface.
     */
    public interface TakeBackActionListener {

        /**
         * Do on confirm click.
         */
        public void doOnConfirmClick(int mJour, int mMois, int mAnnee, int mEndJour, int mEndMois, int mEndAnnee,
                                     int mH1, int mH2, int mMin1, int mMin2);

        /**
         * Do on cancel click.
         */
        public void doOnCancelClick();

    }

    public static RescheduleIntervenDialog newInstance(String intervention, TakeBackActionListener listener) {

        RescheduleIntervenDialog fragment = new RescheduleIntervenDialog();
        takeBackActionListener = listener;
        Bundle args = new Bundle();
        args.putString(INTERVENTION, intervention);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_reschedule_intervention, container);
        jobDetails = getActivity();
        dataAccessObject = DaoManager.getInstance();
        initializeUI(view);
        return view;
    }

    private void initializeUI(View view) {
        df = DateFormat.getDateInstance(DateFormat.LONG);

        edtEndDate = view.findViewById(R.id.dateDebutfin);
        edtStartDate = view.findViewById(R.id.dateDebut);
        edtStartTime = view.findViewById(R.id.heurDebut);
        edtEndTime = view.findViewById(R.id.heurFin);


        view.findViewById(R.id.cancelBtn).setOnClickListener(this);
        view.findViewById(R.id.setBtn).setOnClickListener(this);

        edtEndDate.setOnClickListener(this);
        edtStartDate.setOnClickListener(this);
        edtStartTime.setOnClickListener(this);
        edtEndTime.setOnClickListener(this);


        mDefultMinOneMin = new Date();
        try {
            mDefultMinOneMin = simpleDateFormat.parse("00:01");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        isFormat24 = android.text.format.DateFormat.is24HourFormat(jobDetails);

        intervention = getArguments().getString(INTERVENTION);


        try {
            duree = dataAccessObject.getJobDuration(intervention);
        } catch (Exception e) {
            try {
                duree = simpleDateFormat.parse("02:00");
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }
        initDates();

        Date date = new Date(mAnnee, mMois, mJour, mH1, mMin1);
        Date date2 = new Date(mEndAnnee, mEndMois, mEndJour, mH2, mMin2);


        Format writeFormat = android.text.format.DateFormat.getTimeFormat(jobDetails);

        edtEndDate.setText(df.format(date2));
        edtStartTime.setText(writeFormat.format(date));

        edtStartDate.setText(df.format(date));
        edtEndTime.setText(writeFormat.format(date2));


    }

    /**
     * sets the height of the dialog dynamically based on orientation.
     */
    @Override
    public void onResume() {
        super.onResume();
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


    public void initDates() {
        // Date date1=getDateFromDbFormat(mDate1);
        String[] jobDates = dataAccessObject.getInterventionTime(intervention);

        Logger.log(TAG, "Reschedule values :====>" + jobDates[0] + " : " + jobDates[1]);
        Date date1 = new Date();
        Date startDate = new Date();
        Date endDate = new Date();

        Date date2 = new Date();
        Date date3 = new Date();

        if (jobDates != null && jobDates.length > 1) {
            Date start = new Date();
            Date end = new Date();
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                startDate = sdf.parse(jobDates[0]);
                endDate = sdf.parse(jobDates[1]);

                mAnnee = startDate.getYear();
                mMois = startDate.getMonth();
                mJour = startDate.getDate();
                mH1 = startDate.getHours();
                mMin1 = startDate.getMinutes();

                mEndAnnee = endDate.getYear();
                mEndMois = endDate.getMonth();
                mEndJour = endDate.getDate();
                mH2 = endDate.getHours();
                mMin2 = endDate.getMinutes();

                date2 = new Date(mAnnee, mMois, mJour, mH1, mMin1);
                date3 = new Date(mEndAnnee, mEndMois, mEndJour, mH2, mMin2);

            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

        String dbDate = null;


        if (date1.compareTo(date2) < 0) {
            date1 = date2;
        }

        Calendar calander = Calendar.getInstance();
        calander.setTime(date1);
        // Date date2=getDateFromDbFormat(mDate2);
        mAnnee = date1.getYear();
        mMois = date1.getMonth();
        mJour = date1.getDate();
        mH1 = date1.getHours();
        mMin1 = date1.getMinutes();

        String startHourString = null;
        String startMinuteString = null;
        if (mH1 < 10) {
            startHourString = "0" + mH1;
        } else {
            startHourString = mH1 + "";
        }

        if (mMin1 < 10) {
            startMinuteString = "0" + mMin1;
        } else {
            startMinuteString = "" + mMin1;
        }

        String hd = startHourString + ":" + startMinuteString;

        Date dated = getDateFromStrHour(hd);
        int hour = dated.getHours();
        int min = dated.getMinutes();
        int milliseconds = ((dated.getHours() + duree.getHours()) * 3600000)
                + ((dated.getMinutes() + duree.getMinutes()) * 60000);

        if (milliseconds > 85500000) {
            hour = 23;
            min = 55;
        } else {
            min = ((milliseconds / (60000)) % 60);
            hour = ((milliseconds / (3600000)) % 24);
        }

        // calander.add(Calendar.HOUR_OF_DAY,2);


//        if (date1.compareTo(endDate) > 0) {
//            date1 = startDate;
//        }

        if (date1.compareTo(date3) < 0) {
            mEndAnnee = date3.getYear();
            mEndMois = date3.getMonth();
            mEndJour = date3.getDate();
            mH2 = date3.getHours();
            mMin2 = date3.getMinutes();
        } else {
            mEndAnnee = date1.getYear();
            mEndMois = date1.getMonth();
            mEndJour = date1.getDate();
            mH2 = hour;
            mMin2 = min;
        }


        mFinalTimeEndDate = new Date(mEndAnnee, mEndMois, mEndJour, mH2
                + duree.getHours(), mMin2
                + duree.getMinutes());
    }


    /**
     * ** This method restrict date picker dialog so user can not choose any
     * future date.
     *
     * @param datePicker the date picker
     */
    public void restrictPreviousDateChoice(DatePicker datePicker,
                                           final android.widget.TextView txtFullDate) {
        try {
            final Calendar calendar = Calendar.getInstance(Locale.getDefault());
            final int minYear = calendar.get(Calendar.YEAR);
            final int minMonth = calendar.get(Calendar.MONTH);
            final int minDay = calendar.get(Calendar.DAY_OF_MONTH);

            txtFullDate.setText(DateFormatUtils.getDateString(minDay, minMonth,
                    minYear));

            datePicker.init(minYear, minMonth, minDay,
                    new DatePicker.OnDateChangedListener() {
                        public void onDateChanged(DatePicker view, int year,
                                                  int month, int day) {
                            Calendar newDate = Calendar.getInstance(Locale
                                    .getDefault());
                            newDate.set(year, month, day);

                            if (calendar.after(newDate)) {

                                view.init(minYear, minMonth, minDay, this);
                            }

                            txtFullDate.setText(DateFormatUtils.getDateString(
                                    day, month, year));
                        }
                    });
        } catch (Exception e) {
            Logger.printException(e);
        }
    }

    public void showDate() {

        LayoutInflater factory = LayoutInflater.from(jobDetails);
        alertDialogView2 = factory.inflate(R.layout.show_date_picker, null);
        final DatePicker datePicker = (DatePicker) alertDialogView2
                .findViewById(R.id.start_date);
        android.widget.TextView txtFullDate = (android.widget.TextView) alertDialogView2
                .findViewById(R.id.txt_full_date);

        //Hide the full date view in picker, if it is above or from lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            txtFullDate.setVisibility(View.GONE);
        }
        restrictPreviousDateChoice(datePicker, txtFullDate);
        if (!edtStartDate.getText().toString().equals("")) {
            Date date = getDateFromStrDate(edtStartDate.getText().toString());
            datePicker.updateDate(date.getYear() + 1900, date.getMonth(),
                    date.getDate());
        }
        AlertDialog.Builder adb = new AlertDialog.Builder(jobDetails);
        adb.setView(alertDialogView2);
        adb.setTitle(R.string.textDateSmallLable);

        adb.setIcon(R.drawable.cal_icon);
        adb.setPositiveButton(jobDetails.getString(R.string.ok).toUpperCase(),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        mJour = datePicker.getDayOfMonth();
                        mMois = datePicker.getMonth();
                        mAnnee = datePicker.getYear() - 1900;
                        DateFormat df = DateFormat
                                .getDateInstance(DateFormat.LONG);
                        Date date = new Date(mAnnee, mMois, mJour);

                        edtStartDate.setText(df.format(date));

                        if (!TextUtils.isEmpty(edtStartTime.getText().toString()) ||
                                !TextUtils.isEmpty(edtEndTime.getText().toString()) ||
                                !TextUtils.isEmpty(edtEndDate.getText().toString())) {
                            Calendar currentCalendar = CommonUtils.calendar();
                            Date mCurrentDate = CommonUtils.date();
                            int hour = currentCalendar.get(Calendar.HOUR_OF_DAY);
                            int minute = currentCalendar.get(Calendar.MINUTE);
                            Date mDateStart = new Date(datePicker.getYear() - 1900, mMois, mJour);
                            if (TextUtils.isEmpty(edtStartTime.getText().toString())) {
                                mDateStart.setHours(hour);
                                mDateStart.setMinutes(minute);

                            } else {
                                Date stMin = getDateFromStrHour(edtStartTime.getText().toString());
                                mDateStart.setHours(stMin.getHours());
                                mDateStart.setMinutes(stMin.getMinutes());
                                if (mCurrentDate.compareTo(mDateStart) > 0) {
                                    mDateStart.setHours(hour);
                                    mDateStart.setMinutes(minute);
                                } else {
                                    mDateStart.setHours(stMin.getHours());
                                    mDateStart.setMinutes(stMin.getMinutes());
                                }

                            }
                            Date stSelTime = CommonUtils.calendar().getTime();
                            stSelTime.setHours(mDateStart.getHours());
                            stSelTime.setMinutes(mDateStart.getMinutes());

                            mFinalStartTimeDate = getDateFromStrDate(edtStartDate.getText().toString());
                            mFinalStartTimeDate.setHours(mDateStart.getHours());
                            mFinalStartTimeDate.setMinutes(mDateStart.getMinutes());

                            Date endDate = getDateFromStrDate(edtEndDate.getText().toString());

                            if (endDate.compareTo(mFinalStartTimeDate) <= 0) {
                                mFinalTimeEndDate = new Date(mAnnee, mMois, mJour, mDateStart.getHours()
                                        + duree.getHours(), mDateStart.getMinutes()
                                        + duree.getMinutes());

                            } else {
                                mFinalTimeEndDate = new Date(mEndAnnee, mEndMois, mEndJour, mDateStart.getHours()
                                        + duree.getHours(), mDateStart.getMinutes()
                                        + duree.getMinutes());
                            }


                            mEndJour = mFinalTimeEndDate.getDate();
                            mEndAnnee = mFinalTimeEndDate.getYear();
                            mEndMois = mFinalTimeEndDate.getMonth();
                            mH2 = mFinalTimeEndDate.getHours();
                            mMin2 = mFinalTimeEndDate.getMinutes();
                            mH1 = mFinalStartTimeDate.getHours();
                            mMin1 = mFinalStartTimeDate.getMinutes();
                            edtEndTime.setText(updateTo24HresFormat(mFinalTimeEndDate.getHours(), mFinalTimeEndDate.getMinutes()));
                            edtEndDate.setText(df.format(mFinalTimeEndDate));
                            edtStartTime.setText(updateTo24HresFormat(mFinalStartTimeDate.getHours(), mFinalStartTimeDate.getMinutes()));
                        }

                    }
                });

        adb.setNegativeButton(R.string.textCancelLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        adb.show();
    }

    @SuppressWarnings("deprecation")
    public void showTime1() {

        if (!edtStartDate.getText().toString().equals("")) {


            LayoutInflater factory = LayoutInflater.from(jobDetails);
            alertDialogView = factory.inflate(R.layout.show_time_picker,
                    null);
            TimePicker timePicker = (TimePicker) alertDialogView
                    .findViewById(R.id.StartTime);
            timePicker.setIs24HourView(isFormat24);
            if (TextUtils.isEmpty(edtStartTime.getText().toString())) {
                mH1 = timePicker.getCurrentHour();
                mMin1 = timePicker.getCurrentMinute();
            } else {
                Date datez = getDateFromStrHour(edtStartTime.getText().toString());
                mH1 = datez.getHours();
                mMin1 = datez.getMinutes();
            }
            timePicker.setCurrentHour(mH1);
            timePicker.setCurrentMinute(mMin1);
            AlertDialog.Builder adb = new AlertDialog.Builder(jobDetails);
            adb.setView(alertDialogView);
            adb.setTitle(R.string.textStartTimeLable);
            adb.setIcon(R.drawable.time_icon);
            adb.setPositiveButton(jobDetails.getString(R.string.ok).toUpperCase(),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            TimePicker timePicker = (TimePicker) alertDialogView
                                    .findViewById(R.id.StartTime);
                            timePicker.setIs24HourView(isFormat24);

                            mH1 = timePicker.getCurrentHour();
                            mMin1 = timePicker.getCurrentMinute();
                            Date currentTime = Calendar.getInstance(Locale.getDefault()).getTime();
                            Date stSelTime = Calendar.getInstance(Locale.getDefault()).getTime();
                            stSelTime.setHours(mH1);
                            stSelTime.setMinutes(mMin1);
                            Date SelectDte = getDateFromStrDate(edtStartDate.getText().toString());
                            if (currentTime.compareTo(SelectDte) > 0) {
                                if (currentTime.compareTo(stSelTime) > 0) {
                                    mH1 = currentTime.getHours();
                                    mMin1 = currentTime.getMinutes();
                                } else {
                                    mH1 = timePicker.getCurrentHour();
                                    mMin1 = timePicker.getCurrentMinute();
                                }
                            } else {
                                mH1 = timePicker.getCurrentHour();
                                mMin1 = timePicker.getCurrentMinute();
                            }
                            mFinalTimeEndDate = new Date(mAnnee, mMois, mJour, mH1
                                    + duree.getHours(), mMin1
                                    + duree.getMinutes());
                            mFinalStartTimeDate = getDateFromStrDate(CommonUtils.convertEditTextToString(edtStartDate));
                            mEndJour = mFinalTimeEndDate.getDate();
                            mEndMois = mFinalTimeEndDate.getMonth();
                            mEndAnnee = mFinalTimeEndDate.getYear();
                            mH2 = mFinalTimeEndDate.getHours();
                            mMin2 = mFinalTimeEndDate.getMinutes();
                            mFinalStartTimeDate.setHours(mH1);
                            mFinalStartTimeDate.setMinutes(mMin1);

                            edtEndTime.setText(updateTo24HresFormat(mFinalTimeEndDate.getHours(), mFinalTimeEndDate.getMinutes()));
                            edtEndDate.setText(df.format(mFinalTimeEndDate));
                            edtStartTime.setText(updateTo24HresFormat(mFinalStartTimeDate.getHours(), mFinalStartTimeDate.getMinutes()));

                        }
                    });

            adb.setNegativeButton(R.string.textCancelLable,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,
                                            int which) {
                        }
                    });

            adb.show();

        } else {
            Toast.makeText(jobDetails, R.string.msg_date, Toast.LENGTH_SHORT).show();
        }
    }

    public void showDateEnd() {
        if (!TextUtils.isEmpty(edtStartTime.getText().toString())) {


            myStartDate = getDateFromStrDate(CommonUtils.convertEditTextToString(edtStartDate));
            Date select = getDateFromStrHour(CommonUtils.convertEditTextToString(edtStartTime));
            myStartDate.setHours(select.getHours());
            myStartDate.setMinutes(select.getMinutes());


            final EditText edtStartDate = view.findViewById(R.id.heurDebut);
            if (!TextUtils.isEmpty(edtStartDate.getText().toString())) {
                if (!TextUtils.isEmpty(edtStartTime.getText().toString())) {


                    LayoutInflater factory = LayoutInflater.from(jobDetails);
                    alertDialogView2 = factory.inflate(R.layout.show_date_picker, null);
                    final DatePicker datePicker = (DatePicker) alertDialogView2
                            .findViewById(R.id.start_date);
                    android.widget.TextView txtFullDate = (android.widget.TextView) alertDialogView2
                            .findViewById(R.id.txt_full_date);
                    Calendar cal1 = Calendar.getInstance();
                    cal1.add(Calendar.DAY_OF_MONTH, 1);
                    Date date = getDateFromStrDate(edtEndDate.getText().toString());
                    datePicker.setMinDate(java.lang.System.currentTimeMillis() - 1000);
                    if (!edtEndDate.getText().toString().equals("")) {
                        datePicker.updateDate(date.getYear() + 1900,
                                date.getMonth(),
                                date.getDate());
                    }
                    AlertDialog.Builder adb = new AlertDialog.Builder(jobDetails);
                    adb.setView(alertDialogView2);
                    adb.setTitle(R.string.textDateSmallLable);
                    adb.setIcon(R.drawable.cal_icon);
                    adb.setPositiveButton(jobDetails.getString(R.string.ok).toUpperCase(),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    mEndJour = datePicker.getDayOfMonth();
                                    mEndMois = datePicker.getMonth();
                                    mEndAnnee = datePicker.getYear() - 1900;
                                    Date date = new Date(mEndAnnee, mEndMois, mEndJour);
                                    edtEndDate.setText(df.format(date));

                                    if (!TextUtils.isEmpty(edtEndDate.getText().toString())) {
                                        Date endDate = getDateFromStrDate(edtEndDate.getText().toString());
                                        datePicker.updateDate(endDate.getYear() + 1900, endDate.getMonth(),
                                                endDate.getDate());

                                        if (endDate.compareTo(mFinalTimeEndDate) > 0) {
                                            edtEndDate.setText(df.format(endDate));
                                            if (!TextUtils.isEmpty(CommonUtils.convertEditTextToString(edtEndTime))) {
                                                Date endHrsSEc = getDateFromStrHour(CommonUtils.convertEditTextToString(edtEndTime));
                                                mH2 = endHrsSEc.getHours();
                                                mMin2 = endHrsSEc.getMinutes();
                                            } else {
                                                mH2 = mFinalTimeEndDate.getHours();
                                                mMin2 = mFinalTimeEndDate.getMinutes();
                                            }
                                        } else {
                                            mFinalTimeEndDate = new Date(mAnnee, mMois, mJour, mH1
                                                    + mDefultMinOneMin.getHours(), mMin1
                                                    + mDefultMinOneMin.getMinutes());
                                            edtEndDate.setText(df.format(mFinalTimeEndDate));
                                            edtEndTime.setText("");
//                                                edtEndTime.setText(updateTo24HresFormat(mFinalTimeEndDate.getHours(),
//                                                        mFinalTimeEndDate.getMinutes()));
                                            mEndJour = mFinalTimeEndDate.getDate();
                                            mEndMois = mFinalTimeEndDate.getMonth();
                                            mEndAnnee = mFinalTimeEndDate.getYear();
                                            mH2 = mFinalTimeEndDate.getHours();
                                            mMin2 = mFinalTimeEndDate.getMinutes();
                                        }
                                    }

                                }
                            });
                    adb.setNegativeButton(R.string.textCancelLable,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                    adb.show();

                } else {

                    Toast.makeText(jobDetails, R.string.msg_endDate, Toast.LENGTH_SHORT)
                            .show();

                }
            } else {
                Toast.makeText(jobDetails, R.string.msg_date, Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(jobDetails, R.string.msg_date, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("deprecation")
    public void showTime2() {
        if (!edtStartDate.getText().toString().equals("")) {
            EditText heurDebutEdit = (EditText) view.findViewById(R.id.heurDebut);

            if (!heurDebutEdit.getText().toString().equals("")) {
                if (!TextUtils.isEmpty(edtEndDate.getText().toString())) {
                    // new func
                    EditText etM = (EditText) view.findViewById(R.id.heurMeeting);

                    LayoutInflater factory = LayoutInflater.from(jobDetails);
                    alertDialogView = factory.inflate(R.layout.show_time_picker,
                            null);

                    TimePicker timePicker = (TimePicker) alertDialogView
                            .findViewById(R.id.StartTime);
                    timePicker.setIs24HourView(isFormat24);
                    Date datez = getDateFromStrHour(heurDebutEdit.getText()
                            .toString());
                    mH1 = datez.getHours();
                    mMin1 = datez.getMinutes();

                    if (TextUtils.isEmpty(CommonUtils.convertEditTextToString(edtEndTime))) {
                        Date getedtEndtime = new Date(mAnnee, mMois, mJour, mH1
                                + duree.getHours(), mMin1
                                + duree.getMinutes());
                        timePicker.setCurrentMinute(getedtEndtime.getMinutes());
                        timePicker.setCurrentHour(getedtEndtime.getHours());
                    } else {
                        Date getEndTime = getDateFromStrHour(CommonUtils.convertEditTextToString(edtEndTime));
                        timePicker.setCurrentMinute(getEndTime.getMinutes());
                        timePicker.setCurrentHour(getEndTime.getHours());
                    }
                    AlertDialog.Builder adb = new AlertDialog.Builder(jobDetails);
                    adb.setView(alertDialogView);
                    adb.setTitle(R.string.textEndTimeLable);
                    adb.setIcon(R.drawable.time_icon);
                    adb.setPositiveButton(jobDetails.getString(R.string.ok).toUpperCase(),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    TimePicker timePicker = (TimePicker) alertDialogView
                                            .findViewById(R.id.StartTime);
                                    timePicker.setIs24HourView(isFormat24);

                                    mH2 = timePicker.getCurrentHour();
                                    mMin2 = timePicker.getCurrentMinute();
                                    Date myEndDate = new Date();
                                    Date mDateEnd = new Date();
                                    myEndDate = getDateFromStrDate(CommonUtils.convertEditTextToString(edtEndDate));
                                    myEndDate.setMinutes(mMin2);
                                    myEndDate.setHours(mH2);
                                    Date currentint = getDateFromStrHour(CommonUtils.convertEditTextToString(edtStartTime));
                                    myEndDate = getDateFromStrDate(CommonUtils.convertEditTextToString(edtEndDate));
                                    myEndDate.setMinutes(mMin2);
                                    myEndDate.setHours(mH2);

                                    //todo check crash mFinalStartTimeDate
                                    if (mFinalStartTimeDate == null)
                                        mFinalStartTimeDate = new Date(mAnnee, mMois, mJour, mH1, mMin1);

                                    mDateEnd.setTime(mFinalStartTimeDate.getTime());
                                    mDateEnd.setHours(currentint.getHours() + mDefultMinOneMin.getHours());
                                    mDateEnd.setMinutes(currentint.getMinutes() + mDefultMinOneMin.getMinutes());
                                    int totalhour = mDateEnd.getHours();
                                    int totalhmin = mDateEnd.getMinutes();
                                    if (mDateEnd.compareTo(myEndDate) > 0) {
                                        mH2 = totalhour;
                                        mMin2 = totalhmin;
                                        edtEndTime.setText(updateTo24HresFormat(totalhour, totalhmin));
                                    } else {
                                        mH2 = timePicker.getCurrentHour();
                                        mMin2 = timePicker.getCurrentMinute();
                                        edtEndTime.setText(updateTo24HresFormat(mH2, mMin2));
                                    }


                                }
                            });

                    adb.setNegativeButton(R.string.textCancelLable,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                }
                            });

                    adb.show();
//                    }

                } else {
                    Toast.makeText(jobDetails, R.string.msg_endDate, Toast.LENGTH_SHORT)
                            .show();
                }
            } else {

                Toast.makeText(jobDetails, R.string.msg_endtime, Toast.LENGTH_SHORT)
                        .show();
            }

        } else {

            Toast.makeText(jobDetails, R.string.msg_date, Toast.LENGTH_SHORT).show();
        }

    }


    private String updateTo24HresFormat(int heur, int min) {
        String updatedTime = "";
        String dateFiMin = "";
        String dateFiHeur = "";
        if (heur < 10) {
            dateFiHeur = "0" + heur;
        } else {
            dateFiHeur = "" + heur;
        }
        if (min < 10) {
            dateFiMin = "0" + min;
        } else {
            dateFiMin = "" + min;
        }

        updatedTime = dateFiHeur + ":" + dateFiMin;
        return updatedTime;
    }


    /**
     * Gets the Date from a String literal.
     *
     * @param mDate the m date
     * @return the date from str hour
     */
    public Date getDateFromStrHour(String mDate) {
        // DateFormat df=DateFormat.getTimeInstance(DateFormat.SHORT);
        Date date;
        try {
            date = simpleDateFormat.parse(mDate);
            return date;
        } catch (ParseException e) {
            Logger.printException(e);
            return new Date();
        }
    }

    /**
     * Gets the date from String literal.
     *
     * @param mDate the m date
     * @return the date from str date
     */
    public Date getDateFromStrDate(String mDate) {
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
        Date date;
        try {
            date = df.parse(mDate);
            return date;
        } catch (ParseException e) {
            Logger.printException(e);
            return new Date();
        }
    }


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dateDebut:
                showDate();
                break;

            case R.id.heurDebut:
                showTime1();
                break;
            case R.id.dateDebutfin:
                showDateEnd();
                break;
            case R.id.heurFin:
                showTime2();
                break;
            case R.id.cancelBtn:
                takeBackActionListener.doOnCancelClick();
                dismiss();
                break;
            case R.id.setBtn:
                Logger.log(TAG, "Reschedule values Start date:===>" + mAnnee + " : " + mMois + " : " + mJour
                        + " : " + mH1 + " : " + mMin1);

                Logger.log(TAG, "Reschedule values End date:===>" + mEndAnnee + " : " + mEndMois + " : " + mEndJour
                        + " : " + mH2 + " : " + mMin2);
                takeBackActionListener.doOnConfirmClick(mJour, mMois, mAnnee, mEndJour, mEndMois, mEndAnnee, mH1, mH2, mMin1, mMin2);
                dismiss();
                break;
        }
    }
}