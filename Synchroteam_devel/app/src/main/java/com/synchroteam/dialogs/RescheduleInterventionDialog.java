package com.synchroteam.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.CommonUtils;
import com.synchroteam.utils.DateFormatUtils;
import com.synchroteam.utils.Logger;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

// TODO: Auto-generated Javadoc

/**
 * The Class AttachmentDialog class to show and manage all the actions in
 * attachment dialog.
 *
 * @author ${Ideavate Solution}
 */
@SuppressLint("SimpleDateFormat")
public class RescheduleInterventionDialog extends Dialog implements View.OnClickListener {

    /**
     * The job details.
     */
    private Activity jobDetails;
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


    /**
     * Instantiates a new attachment dialog.
     *
     * @param jobDetails the job details
     * @param duree
     */
    public RescheduleInterventionDialog(Activity jobDetails, Date duree) {
        super(jobDetails, android.R.style.Theme_Translucent_NoTitleBar);
        setCancelable(false);
        setContentView(R.layout.dialog_reschedule_intervention);
        this.getWindow().setGravity(Gravity.CENTER);

        this.jobDetails = jobDetails;
        this.duree = duree;
        df = DateFormat.getDateInstance(DateFormat.LONG);

        edtEndDate = findViewById(R.id.dateDebutfin);
        edtStartDate = findViewById(R.id.dateDebut);
        edtStartTime = findViewById(R.id.heurDebut);
        edtEndTime = findViewById(R.id.heurFin);
        initDates();

//        findViewById(R.id.cancelBtn).setOnClickListener(this);
//        findViewById(R.id.setBtn).setOnClickListener(this);

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


//        setCancelable(false);

    }

    public void initDates() {
        // Date date1=getDateFromDbFormat(mDate1);
        Date date1 = new Date();
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
        mH2 = hour;
        mMin2 = min;
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

    @SuppressWarnings("deprecation")
    public void showDate(View v) {

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
                            mFinalTimeEndDate = new Date(mAnnee, mMois, mJour, mDateStart.getHours()
                                    + duree.getHours(), mDateStart.getMinutes()
                                    + duree.getMinutes());

                            mFinalStartTimeDate = getDateFromStrDate(edtStartDate.getText().toString());
                            mFinalStartTimeDate.setHours(mDateStart.getHours());
                            mFinalStartTimeDate.setMinutes(mDateStart.getMinutes());
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
    public void showTime1(View v) {

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

    public void showDateEnd(View v) {
        if (!TextUtils.isEmpty(edtStartTime.getText().toString())) {


            myStartDate = getDateFromStrDate(CommonUtils.convertEditTextToString(edtStartDate));
            Date select = getDateFromStrHour(CommonUtils.convertEditTextToString(edtStartTime));
            myStartDate.setHours(select.getHours());
            myStartDate.setMinutes(select.getMinutes());


            final EditText edtStartDate = findViewById(R.id.heurDebut);
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
    public void showTime2(View v) {
        if (!edtStartDate.getText().toString().equals("")) {
            EditText heurDebutEdit = (EditText) findViewById(R.id.heurDebut);

            if (!heurDebutEdit.getText().toString().equals("")) {
                if (!TextUtils.isEmpty(edtEndDate.getText().toString())) {
                    // new func
                    EditText etM = (EditText) findViewById(R.id.heurMeeting);

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
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.dateDebut) {

        } else if (id == R.id.heurDebut) {

        } else if (id == R.id.dateDebutfin) {

        } else if (id == R.id.heurFin) {

        } else if (id == R.id.cancelBtn) {
            dismiss();
        } else if (id == R.id.setBtn) {
            dismiss();
        }
    }
}
