package com.synchroteam.synchroteam;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.CommonJobBean;
import com.synchroteam.beans.UpdateDataBaseEvent;
import com.synchroteam.beans.UpdateUiOnSync;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.AppUpdateDialog;
import com.synchroteam.dialogs.AuthenticationErrorDialog;
import com.synchroteam.dialogs.ErrorDialog;
import com.synchroteam.dialogs.MultipleDeviceNotSupported;
import com.synchroteam.dialogs.SynchronizationErrorDialog;
import com.synchroteam.listadapters.AllJobScheduleListAdapter;
import com.synchroteam.observers.ObservableObject;
import com.synchroteam.retrofit.ApiClientNew;
import com.synchroteam.retrofit.ApiInterface;
import com.synchroteam.retrofit.models.JobPoolService.JobPoolRequest;
import com.synchroteam.retrofit.models.JobPoolService.JobPoolResult;
import com.synchroteam.retrofit.models.JobPoolService.JobPoolScheduleRequest;
import com.synchroteam.synchroteam3.R;

import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DateTimeUtils;
import com.synchroteam.utils.DialogResponseInterface;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.SharedPref;
import com.synchroteam.utils.SynchroteamUitls;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// TODO: Auto-generated Javadoc

/**
 * The Class to Inflate and perform Actions On Job Detail Screen. * created for
 * future use
 *
 * @author Ideavate.solution
 */
@SuppressLint("SimpleDateFormat")
public class JobPoolSchedule extends AppCompatActivity implements CommonInterface, Observer, View.OnClickListener {

    /**
     * The action bar.
     */
    private ActionBar actionBar;


    android.widget.TextView txtStartDateIcon, txtEndDateIcon, txtStartTimeIcon, txtEndTimeIcon;

    private TextView txtStartDate, txtEndDate, txtStartTime, txtEndTime, txtprefSlotLabel, txtValidate, txtCancel, empty_text;

    private LinearLayout dateContainer, endDateContainer, startTime_container, endTime_container;

    private ListView jobListLv;


    /**
     * The all job list.
     */
    private TreeMap<String, ArrayList<HashMap<String, String>>> allJobList;

    /**
     * The data acees object.
     */
    private Dao dataAceesObject;

    /**
     * The date.
     */
    private String date;

    /**
     * The footer view.
     */
    private View footerView;

    /**
     * The progress d synch.
     */
    private ProgressDialog progressDSynch;

    /**
     * The user.
     */
    private User user;

    /**
     * The all jobs list adapter.
     */
    private AllJobScheduleListAdapter allJobsListAdapter;

    /**
     * The date format.
     */
    private DateFormat dateFormat;

    /**
     * The format.
     */
    private Format format;

    private String idIntervention, dtPref, idJobWindow, dtMeeting;


    String dateToshowRequired = " ";
    String startTimeRequired = " ";
    String endTimeRequired = " ";

    Date duree;

    int flag = 3;

    String jobDefaultHours, txtEndTimeStrl;


    private int mYear, mMonth, mDay, mStartHour = -1, mStartMinute = -1, mEndHour = -1, mEndMin;
    private DatePickerDialog datePickerDialog;
    Calendar startTimeCal = Calendar.getInstance();
    Calendar endTimeCal = Calendar.getInstance();
    Calendar dateCal = Calendar.getInstance();
    private Date mDate, StartTimemDate, EndTimemDate;
    SimpleDateFormat dateFormatSdf = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat timeFormatSdf = new SimpleDateFormat("HH:mm");
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat dateTimeFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
    private Date slot_StartTime, slot_EndTime;

    private static final String TAG = JobPoolSchedule.class.getSimpleName();
    String dtStart = "";
    String dtEnd = "";
    String dateStartTime = "";
    String dateNew = "";
    boolean isDatePresent = false;
    int hours, mins;

    Date jobTypeDate;
    String jobTypeDateStr;

    /*
     * (non-Javadoc)
     *
     * @see android.support.v7.app.AppCompatActivity#onCreate(android.os.Bundle)
     */
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.layout_schedule_job_pool);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        dataAceesObject = DaoManager.getInstance();

        getRequiredData();


        String title = this.getResources().getString(R.string.planning);
        if (title != null && title.length() > 0) {
            SpannableString titleSpannable = new SpannableString(title);
            titleSpannable.setSpan(
                    new TypefaceSpan(this.getResources().getString(
                            R.string.fontName_hevelicaLight)), 0,
                    titleSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            actionBar.setTitle(null);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);

            android.widget.TextView txtTitle = (android.widget.TextView) findViewById(R.id.toolbar_title);
            txtTitle.setText(isLGDevice() ? title : titleSpannable);
        }

        initializeView();


    }

    private void initializeView() {
        jobListLv = (ListView) findViewById(R.id.jobListLv);


        user = dataAceesObject.getUser();
        Typeface typeface = Typeface.createFromAsset(getAssets(), getString(R.string.fontName_fontAwesome));

        txtStartDate = (TextView) findViewById(R.id.txtStartDate);
        txtEndDate = (TextView) findViewById(R.id.txtEndDate);
        txtStartTime = (TextView) findViewById(R.id.txtStartTime);
        txtEndTime = (TextView) findViewById(R.id.txtEndTime);
        txtprefSlotLabel = (TextView) findViewById(R.id.txtprefSlotLabel);
        txtValidate = (TextView) findViewById(R.id.txtValidate);
        txtCancel = (TextView) findViewById(R.id.txtCancel);
        empty_text = (TextView) findViewById(R.id.empty_text);

        txtStartDateIcon = (android.widget.TextView) findViewById(R.id.txtStartDateIcon);
        txtEndDateIcon = (android.widget.TextView) findViewById(R.id.txtEndDateIcon);
        txtStartTimeIcon = (android.widget.TextView) findViewById(R.id.txtStartTimeIcon);
        txtEndTimeIcon = (android.widget.TextView) findViewById(R.id.txtEndTimeIcon);

        dateContainer = findViewById(R.id.linContainer1);
        endDateContainer = findViewById(R.id.linContainer); //endDate
        endTime_container = findViewById(R.id.linContainer3);
        startTime_container = findViewById(R.id.linContainer2);

        txtStartDateIcon.setTypeface(typeface);
        txtEndDateIcon.setTypeface(typeface);
        txtStartTimeIcon.setTypeface(typeface);
        txtEndTimeIcon.setTypeface(typeface);


        footerView = ((LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.layout_footerview_items_list, null, false);

        jobListLv.addFooterView(footerView);


        //for showing the header
        String dateString = formatDateFromOnetoAnother(date, "yyyy-MM-dd", "MMMM yyyy");

        dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
        format = android.text.format.DateFormat.getTimeFormat(this);

        jobPoolDetailsLogic();

        new FetchJobsOnCurrentDateAsyncTask().execute();

        dateContainer.setOnClickListener(this);
        endDateContainer.setOnClickListener(this);
        startTime_container.setOnClickListener(this);
        endTime_container.setOnClickListener(this);
        txtCancel.setOnClickListener(this);
        txtValidate.setOnClickListener(this);
    }

    public String formatDateFromOnetoAnother(String dateStr, String givenformat, String resultformat) {

        String result = "";
        SimpleDateFormat sdf;
        SimpleDateFormat sdf1;

        try {
            sdf = new SimpleDateFormat(givenformat);
            sdf1 = new SimpleDateFormat(resultformat);
            result = sdf1.format(Objects.requireNonNull(sdf.parse(dateStr)));

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            sdf = null;
            sdf1 = null;
        }
        return result;
    }

    public boolean isLGDevice() {
        return (Build.MANUFACTURER.contains("LG") || Build.MODEL.contains("LG"));
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        rejectScheduleDialog();
    }

    /**
     * Gets the required data.
     *
     * @return the required data
     */
    private void getRequiredData() {
        Bundle bundle = this.getIntent().getExtras();
        dtMeeting = bundle.getString(KEYS.CurrentJobs.DATEMEETING);
        dtPref = bundle.getString(KEYS.CurrentJobs.DATE_PREF);
        idJobWindow = bundle.getString(KEYS.CurrentJobs.ID_JOB_WINDOW);
        idIntervention = bundle.getString(KEYS.CurrentJobs.ID);

        duree = dataAceesObject.getJobDuration(idIntervention);
        SimpleDateFormat jobDurationFormat = new SimpleDateFormat("HH:mm");
        jobDefaultHours = jobDurationFormat.format(duree);

        Log.e("bundle", "bundle_hours" + jobDefaultHours);
        Log.e("bundle", "dtPref" + dtPref);
        Log.e("bundle", "dtMeeting" + dtMeeting);


    }

    private void jobPoolDetailsLogic() {
        duree = dataAceesObject.getJobDuration(idIntervention);

        String isJobDatePref = dtPref;
        int idJobWindow = Integer.parseInt(this.idJobWindow);

        if (!TextUtils.isEmpty(dtMeeting) && !dtMeeting.equalsIgnoreCase("null")) {
            try {
                String[] dateTopass = dtMeeting.split(" ");
                date = dateTopass[0];

                dateToshowRequired = formatDateFromOnetoAnother(date,
                        "yyyy-MM-dd", "dd/MM/yy");

                if (dateToshowRequired != null && dateToshowRequired.trim().length() > 0) {
                    isDatePresent = true;
                }

                if (dateTopass[1] != null && dateTopass[1].length() > 0 && !dateTopass[1].startsWith("00:00")) {

                    startTimeRequired = formatDateFromOnetoAnother(dateTopass[1],
                            "HH:mm:ss", "hh:mm a");

                    if (startTimeRequired != null && startTimeRequired.trim().length() > 0) {

                    }
                } else {
                    //hide lock icon

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            if (isJobDatePref != null && !TextUtils.isEmpty(isJobDatePref) &&
                    !isJobDatePref.equalsIgnoreCase("null")) {
                try {
                    String[] dateTopass = isJobDatePref.split(" ");
                    date = dateTopass[0];
                    dateToshowRequired = formatDateFromOnetoAnother(date,
                            "yyyy-MM-dd", "dd/MM/yy");

                    if (dateTopass[1] != null && dateTopass[1].length() > 0 && !dateTopass[1].startsWith("00:00")) {
                        startTimeRequired = formatDateFromOnetoAnother(dateTopass[1],
                                "HH:mm:ss", "hh:mm a");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (idJobWindow > 0) {
                startTimeRequired = dataAceesObject.startTimeJobWindow(idJobWindow);
                endTimeRequired = dataAceesObject.endTimeJobWindow(idJobWindow);

                Log.e("startTimePrefSlot", "startTimePrefSlot: " + startTimeRequired);
                if (startTimeRequired != null && startTimeRequired.trim().length() > 0 &&
                        endTimeRequired != null && endTimeRequired.trim().length() > 0) {
                    txtprefSlotLabel.setVisibility(View.VISIBLE);
                    txtprefSlotLabel.setText(getResources().getString(R.string.textPrefTimeLable) + " : " +
                            startTimeRequired + " - " + endTimeRequired);

                } else {
                    txtprefSlotLabel.setVisibility(View.GONE);
                }
            }
        }

//        if (startTimeRequired != null && startTimeRequired.trim().length() > 0) {
//            String start = formatDateFromOnetoAnother(startTimeRequired,
//                    "hh:mm a", "HH:mm");
//            String[] startTimeTopass = start.split(":");
////            txtStartTime.setText(start);
//            if(startTimeTopass.length == 2 && !startTimeTopass[0].trim().isEmpty() && !startTimeTopass[1].trim().isEmpty()) {
//                try {
//                    setStartTime(Integer.parseInt(startTimeTopass[0].trim()),
//                            Integer.parseInt(startTimeTopass[1].trim()));
//                                    Log.e("JobPoolSchedule>>>>>>>>","Hour or minute string is empty." + startTimeTopass.length);
//
//                } catch (NumberFormatException e) {
//                    throw new RuntimeException(e);
//                }
//
//            }else {
//                Logger.log("JobPoolSchedule>>>>>>>>", "Hour or minute string is empty." + startTimeTopass.length);
//            }
//
//        }
        if (startTimeRequired != null && startTimeRequired.trim().length() > 0) {
            try {
                String start = formatDateFromOnetoAnother(startTimeRequired, "hh:mm a", "HH:mm");
                String[] startTimeTopass = start.split(":");

                // Check if startTimeTopass has the correct number of elements (should be 2 for hours and minutes)
                if (startTimeTopass.length == 2) {
                    // Safely parse the hour and minute
                    int hour = Integer.parseInt(startTimeTopass[0].trim());
                    int minute = Integer.parseInt(startTimeTopass[1].trim());

                    // Set the start time using the parsed values
                    setStartTime(hour, minute);
                    Log.e("JobPoolSchedule", "valid time format: " + start);
                } else {
                    Log.e("JobPoolSchedule", "Invalid time format: " + start);
                }
            } catch (NumberFormatException e) {
                Log.e("JobPoolSchedule", "Number format exception for time: " + startTimeRequired, e);
            } catch (Exception e) {
                Log.e("JobPoolSchedule", "Unexpected exception occurred: " + e.getMessage(), e);
            }
        }

        if (endTimeRequired != null && endTimeRequired.trim().length() > 0) {

//            txtEndTime.setText(endTimeRequired);
        }

        if (dateToshowRequired == null || dateToshowRequired.trim().length() == 0) {
            calendar = Calendar.getInstance();
            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);

            //set start time and end time(month value starts from 0)
            dateCal.set(mYear, mMonth - 1, mDay);

        } else {

//            String dateFromPrevAct = formatDateFromOnetoAnother(dateToshowRequired,
//                    "dd/MM/yy", "yyyy-MM-dd");
//
            String dateFromPrevAct = dateToshowRequired;

//            setDate(dateFromPrevAct);

            txtStartDate.setText(dateToshowRequired);
            Log.e("pref", "pref_date" + dateToshowRequired);
            setPrefSlotEndDateTime(dateToshowRequired, startTimeRequired);

            String dateArr[] = date.split("-");
            mYear = Integer.parseInt(dateArr[0].trim());
            mMonth = Integer.parseInt(dateArr[1].trim());
            mDay = Integer.parseInt(dateArr[2].trim());

            //set start time and end time(month value starts from 0)
            dateCal.set(mYear, mMonth - 1, mDay);
            startTimeCal.set(mYear, mMonth - 1, mDay);
            endTimeCal.set(mYear, mMonth - 1, mDay);

            try {
                mDate = dateFormatSdf.parse(date);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String slot_StartTimeStr = "";
        String slot_EndTimeStr = "";

        //logic for setting the slot
        if (startTimeRequired != null && startTimeRequired.trim().length() > 0 &&
                endTimeRequired != null && endTimeRequired.trim().length() > 0) {
            flag = 3;

            slot_StartTimeStr = formatDateFromOnetoAnother(startTimeRequired,
                    "hh:mm a", "HH:mm");
            slot_EndTimeStr = formatDateFromOnetoAnother(endTimeRequired,
                    "hh:mm a", "HH:mm");
            try {
                slot_StartTime = timeFormatSdf.parse(slot_StartTimeStr);
                slot_EndTime = timeFormatSdf.parse(slot_EndTimeStr);
                mStartHour = slot_StartTime.getHours();
                mStartMinute = slot_StartTime.getMinutes();

                //set start time date
                StartTimemDate = timeFormatSdf.parse(mStartHour + ":" + mStartMinute);


            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else if (startTimeRequired != null && startTimeRequired.trim().length() > 0) {
            flag = 2;
            slot_StartTimeStr = formatDateFromOnetoAnother(startTimeRequired,
                    "hh:mm a", "HH:mm");


            try {
                slot_StartTime = timeFormatSdf.parse(slot_StartTimeStr);
                mStartHour = slot_StartTime.getHours();
                mStartMinute = slot_StartTime.getMinutes();

                //set start time date
                StartTimemDate = timeFormatSdf.parse(mStartHour + ":" + mStartMinute);

                if (isDatePresent) {
                    startTimeCal.set(Calendar.HOUR_OF_DAY, mStartHour);
                    startTimeCal.set(Calendar.MINUTE, mStartMinute);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            flag = 1;
        }

        Logger.log("TAG", "JOB POOL TIME START" + startTimeRequired);
        Logger.log("TAG", "JOB POOL TIME END" + endTimeRequired);
        Logger.log("TAG", "JOB POOL DATE " + dateToshowRequired);


    }

    private void setPrefSlotDate(String dateFromPrevAct) {
        Calendar todayCalender = Calendar.getInstance();
        Date currentDate = todayCalender.getTime();
        String currentDateString = dateFormatSdf.format(currentDate);
        String currentDtStr = formatDateFromOnetoAnother(currentDateString, "yyyy-MM-dd", "dd/MM/yy");
        SimpleDateFormat dfDate = new SimpleDateFormat("dd/MM/yy");
        try {
            if (dfDate.parse(dateFromPrevAct).before(dfDate.parse(currentDtStr))) {
                txtStartDate.setText(currentDtStr);
            } else {
                txtStartDate.setText(dateFromPrevAct);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onResume()
     */
    protected void onResume() {
        super.onResume();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                rejectScheduleDialog();
                break;
        }
        return true;
    }


    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();

    }

    /*
     * (non-Javadoc)
     *
     * @see com.synchroteam.synchroteam.CommonInterface#getActivityInstance()
     */
    @Override
    public Activity getActivityInstance() {
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.synchroteam.synchroteam.CommonInterface#updateUi()
     */
    @Override
    public void updateUi() {
        EventBus.getDefault().post(new UpdateUiOnSync());
    }

    @Override
    public void updateUiOnTrakingStatusChange(boolean isRunning) {

    }


    @Override
    protected void onStart() {
        super.onStart();

//        EventBus.getDefault().register(this);
        ObservableObject.getInstance().addObserver(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

//        if (EventBus.getDefault().isRegistered(this))
//            EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        ObservableObject.getInstance().deleteObserver(this);
        super.onDestroy();

    }


    /**
     * This method will triggers for clock in time out
     *
     * @param observable
     * @param o
     */
    @Override
    public void update(Observable observable, Object o) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linContainer1:
                jobPoolDateLogic();
                break;
            case R.id.linContainer:
                if (txtStartDate.getText().toString().isEmpty()) {
                    Toast.makeText(JobPoolSchedule.this, "Enter start date first for scheduling a job", Toast.LENGTH_SHORT).show();
                } else if (txtStartDate.getText().length() > 0 && txtStartTime.getText().toString().isEmpty()) {
                    Toast.makeText(JobPoolSchedule.this, "Enter start time first for scheduling a job", Toast.LENGTH_SHORT).show();
                } else {
                    jobPoolEndDateLogic();
                }

                break;
            case R.id.linContainer2:
                if (!isDatePresent)
                    jobPoolStartTimeLogic();
                break;
            case R.id.linContainer3:
                jobPoolEndTimeLogic();
                break;
            case R.id.txtValidate:
                if (SynchroteamUitls.isNetworkAvailable(JobPoolSchedule.this)) {
                    String startDate = "";
                    String endDate = "";
                    setStartTimeFinal();
                    setEndTimeFinal();
                    if (dtStart != null && dtStart.trim().length() > 0 && dtEnd != null && dtEnd.trim().length() > 0) {

                        String startTime = txtStartDate.getText().toString() + " " +
                                txtStartTime.getText().toString();
                        String endTime = txtEndDate.getText().toString() + " " +
                                txtEndTime.getText().toString();
                        String dtStart = formatDateFromOnetoAnother(startTime,
                                "dd/MM/yy hh:mm a", "yyyy-MM-dd HH:mm:ss");
                        String dtEnd = formatDateFromOnetoAnother(endTime,
                                "dd/MM/yy hh:mm a", "yyyy-MM-dd HH:mm:ss");
                        Log.e(TAG, "Job pool Schedule startDate==>" + dtStart);
                        Log.e(TAG, "Job pool Schedule endDate==>" + dtEnd);
                        try {
                            hitServiceForSchedule(idIntervention, user.getId(), dtStart, dtEnd);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        showErrMsgDialog(getString(R.string.text_enter_all_values));
                    }
                } else {
                    showErrMsgDialog(getString(R.string.textNewtworkNorAvaliable));
                }
                break;

            case R.id.txtCancel:
                rejectScheduleDialog();
                break;
        }
    }

    /**
     * Rejeter intervention.
     */
    public void rejectScheduleDialog() {

        DialogUtils.showConfirmationDialog(JobPoolSchedule.this,
                this.getString(R.string.textAlertLable) + "!",
                this.getString(R.string.textNewJobEndConfirmation), false, 0,
                this.getString(R.string.textYesLable),
                this.getString(R.string.textNoLable),
                new DialogResponseInterface() {

                    @Override
                    public void doOnPositiveBtnClick(Activity arg0) {
                        // TODO Auto-generated method stub
                        if (SynchroteamUitls.isNetworkAvailable(JobPoolSchedule.this)) {
                            try {
                                hitServiceForRelease(idIntervention, user.getId());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void doOnNegativeBtnClick(Activity arg0) {
                        // TODO Auto-generated method stub

                    }
                });

    }

    private void hitServiceForSchedule(String idIntervention, int id, String startDate, String endDate) throws Exception {
        //show progress
        if (progressDSynch == null && !isFinishing())
            progressDSynch = ProgressDialog.show(JobPoolSchedule.this,
                    getString(R.string.textPleaseWaitLable),
                    getString(R.string.chargement), true, false);
        else if (progressDSynch != null && !progressDSynch.isShowing())
            progressDSynch.show();

        String authUserName = dataAceesObject.getUserDomain() + "_" + user.getLogin();
        String authPassword = user.getPwd();
        ApiInterface apiService = ApiClientNew.createService(ApiInterface.class, authUserName, authPassword);

        String url = SharedPref.getJobPoolUrlServer(JobPoolSchedule.this) + "/schedule";


        JobPoolScheduleRequest jobPoolRequest = new JobPoolScheduleRequest(id, idIntervention, startDate, endDate);
        Logger.log("TAG", "JobPoolRequest jobPoolRequest--->" + jobPoolRequest.toString());

        Call<JobPoolResult> call = apiService.scheduleJobPoolService(url, jobPoolRequest);
        call.enqueue(new Callback<JobPoolResult>() {
            @Override
            public void onResponse(Call<JobPoolResult> call, Response<JobPoolResult> response) {


                if (response.isSuccessful()) {
                    progressDSynch.dismiss();
                    int status = response.body().getResult();
                    if (status == 1) {
                        synch();
                        Logger.log("JobPoolResult", "JobPoolResult success :" + status);
                    } else {
                        Logger.log("JobPoolResult", "JobPoolResult failure :" + status);
                        showErrMsgDialog(getString(R.string.msg_schedule_error));
                    }
                } else {
                    progressDSynch.dismiss();
                    Logger.log("JobPoolResult", "JobPoolResult failure :" + response.errorBody());
                    showErrMsgDialog(getString(R.string.msg_error));
                }
            }

            @Override
            public void onFailure(Call<JobPoolResult> call, Throwable t) {
                Logger.log("JobPoolResult", "JobPoolResult Exception :" + t);
                progressDSynch.dismiss();
                showErrMsgDialog(getString(R.string.msg_error));
            }

        });

    }

    private void hitServiceForRelease(String idIntervention, int id) throws Exception {
        //show progress
        if (progressDSynch == null && !isFinishing())
            progressDSynch = ProgressDialog.show(JobPoolSchedule.this,
                    getString(R.string.textPleaseWaitLable),
                    getString(R.string.chargement), true, false);
        else if (progressDSynch != null && !progressDSynch.isShowing())
            progressDSynch.show();


        String authUserName = dataAceesObject.getUserDomain() + "_" + user.getLogin();
        String authPassword = user.getPwd();
        ApiInterface apiService = ApiClientNew.createService(ApiInterface.class, authUserName, authPassword);

        String url = SharedPref.getJobPoolUrlServer(JobPoolSchedule.this) + "/release";


        JobPoolRequest jobPoolRequest = new JobPoolRequest(id, idIntervention);
        Logger.log("TAG", "JobPoolRequest jobPoolRequest--->" + jobPoolRequest.toString());

        Call<JobPoolResult> call = apiService.scheduleReserveService(url, jobPoolRequest);
        call.enqueue(new Callback<JobPoolResult>() {
            @Override
            public void onResponse(Call<JobPoolResult> call, Response<JobPoolResult> response) {


                if (response.isSuccessful()) {
                    int status = response.body().getResult();

                    if (status == 1) {
                        progressDSynch.dismiss();
                        finish();
                        Logger.log("JobPoolResult", "JobPoolResult success :" + status);
                    } else {
                        progressDSynch.dismiss();
                        Logger.log("JobPoolResult", "JobPoolResult failure :" + status);
//                        showErrMsgDialog(getString(R.string.msg_error));
                        Toast.makeText(JobPoolSchedule.this, getString(R.string.msg_error), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    progressDSynch.dismiss();
                    Logger.log("JobPoolResult", "JobPoolResult failure :" + response);
                    showErrMsgDialog(getString(R.string.msg_error));
                }
            }

            @Override
            public void onFailure(Call<JobPoolResult> call, Throwable t) {
                Logger.log("JobPoolResult", "JobPoolResult Exception :" + t);
                progressDSynch.dismiss();
                showErrMsgDialog(getString(R.string.msg_error));
            }

        });

    }


    public void case3_AddDefaultTime(int hourOfDay, int minute) {

        try {
            Date updatedDate = timeFormatSdf.parse(hourOfDay + ":" + minute);
            if (updatedDate.getTime() <= slot_EndTime.getTime() &&
                    updatedDate.getTime() >= StartTimemDate.getTime()) {
                Log.e("bundle_", "bundle_  " + updatedDate);
            } else {

                //new changes
//                mEndHour = slot_EndTime.getHours();
//                mEndMin = slot_EndTime.getMinutes();

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void jobPoolEndTimeLogic() {

        if (txtStartDate.getText().toString().isEmpty()) {
            Toast.makeText(JobPoolSchedule.this, R.string.msg_date, Toast.LENGTH_SHORT).show();

        } else if (txtStartTime.getText().toString().isEmpty()) {
            Toast.makeText(JobPoolSchedule.this, R.string.msg_endtime, Toast.LENGTH_SHORT).show();

        } else {
            Calendar todayCalender = Calendar.getInstance();
            Date currentDate = todayCalender.getTime();

            String currentDateString = dateFormatSdf.format(currentDate);
            String currentDtStr = formatDateFromOnetoAnother(currentDateString, "yyyy-MM-dd", "dd/MM/yy");

            int currentHr = currentDate.getHours();
            int currentMins = currentDate.getMinutes();
            String endDate = txtEndDate.getText().toString();
            String startDate = txtStartDate.getText().toString();

            if (mEndHour == -1) {
                String addTime[] = jobDefaultHours.split(":");
                int addHour = Integer.parseInt(addTime[0]);
                int addMinutes = Integer.parseInt(addTime[1]);

                mEndHour = mStartHour + addHour;
                mEndMin = mStartMinute + addMinutes;


                if (mEndMin >= 60) {
                    mEndHour = mStartHour + 1;
                    mEndMin = mEndMin - 60;
                }

                if (mEndHour >= 24) {
                    mEndHour = 23;
                    mEndMin = 55;
                }

                if (flag == 3) {
                    case3_AddDefaultTime(mEndHour, mEndMin);
                }

//                if (mStartHour < 22) {
//                    mEndHour = mStartHour + 2;
//
//                } else {
//                    mEndHour = mStartHour;
//                }
//                mEndMin = mStartMinute;

            }
            SimpleDateFormat dfDate = new SimpleDateFormat("dd/MM/yy");
            if (jobTypeDate != null) {
                try {
                    if (dfDate.parse(endDate).before(dfDate.parse(currentDtStr))) {
                        Log.e("time ", "start time is before end time");
                        mEndHour = hours;
                        mEndMin = mins;
                        Log.e("time ", "mEndHour " + mEndHour);
                        Log.e("time ", "mEndMin " + mEndMin);
                    } else if (dfDate.parse(endDate).equals(dfDate.parse(jobTypeDateStr))) {
                        Log.e("time ", "jobTypeDateStr is equal current time");
                        mEndHour = hours;
                        mEndMin = mins;
                        Log.e("time ", "mEndHour " + mEndHour);
                        Log.e("time ", "mEndMin " + mEndMin);
                    } else if (dfDate.parse(endDate).equals(dfDate.parse(currentDtStr))) {
                        Log.e("time ", "start time is equal current time"); // current hr or 11:59
                        mEndHour = mStartHour;
                        mEndMin = mStartMinute + 1;
                        Log.e("time ", "mEndHour " + mEndHour);
                        Log.e("time ", "mEndMin " + mEndMin);
                    } else {
                        Log.e("time ", "start time is after end time");
                        mEndHour = hours;
                        mEndMin = mins;
                        Log.e("time ", "mEndHour " + mEndHour);
                        Log.e("time ", "mEndMin " + mEndMin);
                        Log.e("time ", "hour  " + hours);
                        Log.e("time ", "Min " + mins);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                mEndHour = currentDate.getHours();
                mEndMin = currentDate.getMinutes();
            }
            TimePickerDialog endTimerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            mEndHour = hourOfDay;
                            mEndMin = minute;


                            if (flag == 3) {
                                case3_endTime(hourOfDay, minute);
                            }
                            if (flag == 2) {
                                endTimeCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                endTimeCal.set(Calendar.MINUTE, minute);
                                mEndHour = hourOfDay;
                                mEndMin = minute;

                                if (endTimeCal.getTime().after(startTimeCal.getTime())) {
//                                    txtEndTime.setText(hourOfDay + ":" + minute);
                                    setEndTime(hourOfDay, minute);
                                } else {

                                    mEndHour = -1;
                                    mEndMin = 0;

                                    if (txtStartDate.getText().toString().equals(txtEndDate.getText().toString())) {
                                        setEndTime(mStartHour, mStartMinute + 1); //specific date and time v53 new changes
                                    } else {
                                        setEndTime(hourOfDay, minute);
                                    }

//                                    mEndHour = mStartHour;
//                                    mEndMin = mStartMinute;
////                                    txtEndTime.setText(timeFormatSdf.format(startTimeCal.getTime()));
//                                    setEndTime(startTimeCal.getTime().getHours(), startTimeCal.getTime().getMinutes());
                                }

                            }
                            if (flag == 1) {
                                case1_endTime(hourOfDay, minute);
                            }
                        }
                    }, mEndHour, mEndMin, false);

            endTimerDialog.show();
        }

    }

    private void jobPoolStartTimeLogic() {

        if (!txtStartDate.getText().toString().isEmpty()) {
            if (flag == 1 || flag == 3) {
                if (mStartHour == -1 && mStartMinute == -1) {
                    if (flag == 1) {
                        calendar = Calendar.getInstance();
                        mStartHour = calendar.get(Calendar.HOUR_OF_DAY);
                        mStartMinute = calendar.get(Calendar.MINUTE);
                    } else if (flag == 3) {
                        mStartHour = slot_StartTime.getHours();
                        mStartMinute = slot_StartTime.getMinutes();
                    }
                }

                TimePickerDialog startTimerDialog = new TimePickerDialog(this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                mStartHour = hourOfDay;
                                mStartMinute = minute;

                                if (flag == 1) {
                                    case1_startTime(hourOfDay, minute);
                                }
                                if (flag == 3) {
                                    case3_startTime(hourOfDay, minute);
                                }

                                if (txtEndTime.getText().toString().trim().length() == 0) {
                                    mEndHour = -1;
                                }
                            }
                        }, mStartHour, mStartMinute, false);
                startTimerDialog.show();
            } else {
                startTimeCal.set(Calendar.HOUR_OF_DAY, slot_StartTime.getHours());
                startTimeCal.set(Calendar.MINUTE, slot_StartTime.getMinutes());
                mStartHour = slot_StartTime.getHours();
                mStartMinute = slot_StartTime.getMinutes();

//                txtStartTime.setText(mStartHour + ":" + mStartMinute);


                setStartTime(mStartHour, mStartMinute);

            }
        } else {
            Toast.makeText(JobPoolSchedule.this, R.string.msg_date, Toast.LENGTH_SHORT).show();
        }
    }

    private void jobPoolDateLogic() {

        if (isDatePresent) {
            String dateArr[] = date.split("-");
            mYear = Integer.parseInt(dateArr[0].trim());
            mMonth = Integer.parseInt(dateArr[1].trim());
            mDay = Integer.parseInt(dateArr[2].trim());

            dateCal.set(mYear, mMonth - 1, mDay);
            mDate = dateCal.getTime();

//            txtDate.setText(dateFormatSdf.format(dateCal.getTime()));
//            setDate(dateFormatSdf.format(dateCal.getTime()));

            //disable click
            dateContainer.setEnabled(false);
            dateContainer.setClickable(false);


        } else {
            //enable click
            dateContainer.setEnabled(true);
            dateContainer.setClickable(true);

            mYear = calendar.get(Calendar.YEAR);
            mMonth = calendar.get(Calendar.MONTH);
            mDay = calendar.get(Calendar.DAY_OF_MONTH);


            if (date != null && date.trim().length() > 0) {
                String dob_var = (txtStartDate.getText().toString());
                Date dateObject = getDateFromStrDate(dob_var);
                String date = new SimpleDateFormat("dd/MM/yyyy").format(dateObject);
                String[] items1 = date.split("/");
                String d1 = items1[0];
                String m1 = items1[1];
                String y1 = items1[2];
                mDay = Integer.parseInt(d1);
                mMonth = Integer.parseInt(m1) - 1;
                mYear = Integer.parseInt(y1);

//                String dateArr[] = date.split("-");
//                mYear = Integer.parseInt(dateArr[0].trim());
//                mMonth = Integer.parseInt(dateArr[1].trim()) - 1;
//                mDay = Integer.parseInt(dateArr[2].trim());
            }

            Date myStartDate = getDateFromStrDate(dateToshowRequired);
            Date myStartDate1 = new Date();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            mYear = year;
                            mMonth = monthOfYear;
                            mDay = dayOfMonth;

                            dateCal.set(year, monthOfYear, dayOfMonth);
                            mDate = dateCal.getTime();

                            String dateNew = dateFormatSdf.format(dateCal.getTime());


//                            txtDate.setText(dateFormatSdf.format(dateCal.getTime()));
                            setDate(dateFormatSdf.format(dateCal.getTime()));


                            if (dateNew != null && dateNew.trim().length() > 0) {
                                date = dateNew;
                                new FetchJobsOnCurrentDateAsyncTask().execute();
                            }

                            Logger.log(TAG, "FLAG FOR TYPE ===>" + flag);

                            if (flag != 3)
                                checkStartTimeDateLogic(dateCal);

                        }
                    }, mYear, mMonth, mDay);
            if (myStartDate.before(myStartDate1)) {
                datePickerDialog.getDatePicker().setMinDate(myStartDate.getTime()); //pref slot date
                datePickerDialog.show();
                Log.e("pref", "pref slot before today: " + myStartDate + "\n" + myStartDate1);

            } else if (myStartDate.after(myStartDate1)) {
                datePickerDialog.getDatePicker().setMinDate(myStartDate1.getTime()); //pref slot date
                datePickerDialog.show();
                Log.e("pref", "pref slot after today: " + myStartDate + "\n" + myStartDate1);
            } else {
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        }
    }

    private void jobPoolEndDateLogic() {

//        if (isDatePresent ) {
//            String dateArr[] = dateNew.split("-");
//            mYear = Integer.parseInt(dateArr[0].trim());
//            mMonth = Integer.parseInt(dateArr[1].trim());
//            mDay = Integer.parseInt(dateArr[2].trim());
//
//            dateCal.set(mYear, mMonth - 1, mDay);
//            mDate = dateCal.getTime();
//          txtDate.setText(dateFormatSdf.format(dateCal.getTime()));
//
//            setEndDate(dateFormatSdf.format(dateCal.getTime()));
//
//            // disabled
//            endDateContainer.setEnabled(false);
//            endDateContainer.setClickable(false);
//
//        } else {
        //enable click
        endDateContainer.setEnabled(true);
        endDateContainer.setClickable(true);


        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        Date myStartDate = null;
        String startDateTxt = txtStartDate.getText().toString();
        Date startDate = getDateFromStrDate(startDateTxt);
        Date myStartDate1 = new Date();
        if (dateNew.equals("")) {

        } else {
            String dateArr[] = dateNew.split("-");
            mYear = Integer.parseInt(dateArr[0].trim());
            mMonth = Integer.parseInt(dateArr[1].trim()) - 1;
            mDay = Integer.parseInt(dateArr[2].trim());
            myStartDate = getDateFromStrDate(dateNew);
        }

        if (dateNew != null && dateNew.trim().length() > 0 && txtEndDate.getText().length() > 0) {
            String dob_var = (txtEndDate.getText().toString());
            Date dateObject = getDateFromStrDate(dob_var);
            String date = new SimpleDateFormat("dd/MM/yyyy").format(dateObject);
            String[] items1 = date.split("/");
            String d1 = items1[0];
            String m1 = items1[1];
            String y1 = items1[2];
            mDay = Integer.parseInt(d1);
            mMonth = Integer.parseInt(m1) - 1;
            mYear = Integer.parseInt(y1);
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;


                        dateCal.set(year, monthOfYear, dayOfMonth);
                        mDate = dateCal.getTime();

                        String dateNews = dateFormatSdf.format(dateCal.getTime());


                        setEndDate(dateFormatSdf.format(dateCal.getTime()));


                        if (dateNews != null && dateNews.trim().length() > 0) {
                            dateNew = dateNews;
//                                d=dateCal.getTime();
                            new FetchJobsOnCurrentDateAsyncTask().execute();
                        }

                        Logger.log(TAG, "FLAG FOR TYPE ===>" + flag);

                        if (flag != 3)
                            checkStartTimeDateLogic(dateCal);

                    }
                }, mYear, mMonth, mDay);
        if (myStartDate != null) {
            datePickerDialog.getDatePicker().setMinDate(startDate.getTime()); //job pool schedule date(default hours)
            datePickerDialog.show();
            Log.e("pref", "start date: " + startDate + "\n" + myStartDate1);
        } else {
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        }

//        }

    }

    /**
     * This method is used to get the date text from  startdate textview and set into datepicker directly
     */
    private void startDateClickListener() {

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); // Make sure user insert date into textview in this format.
        Date dateObject;
        try {
            String dob_var = (txtStartDate.getText().toString());
            dateObject = formatter.parse(dob_var);
            String date = new SimpleDateFormat("dd/MM/yyyy").format(dateObject);

            String[] items1 = date.split("/");
            String d1 = items1[0];
            String m1 = items1[1];
            String y1 = items1[2];
            mDay = Integer.parseInt(d1);
            mMonth = Integer.parseInt(m1);
            mYear = Integer.parseInt(y1);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            mYear = year;
                            mMonth = monthOfYear;
                            mDay = dayOfMonth;
                            String datee = (dayOfMonth) + "/" + (monthOfYear + 1) + "/" + (year);

                            txtStartDate.setText(datee);
                        }
                    }, mYear, mMonth - 1, mDay);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        } catch (java.text.ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i("Exception is: ", e.toString());
        }
    }

    private void checkStartTimeDateLogic(Calendar dateCal) {

        if (txtStartTime.getText().toString().trim().length() > 0) {

            Calendar current_time = Calendar.getInstance();
            int hour = current_time.get(Calendar.HOUR_OF_DAY);
            int min = current_time.get(Calendar.MINUTE);

            dateCal.set(Calendar.HOUR_OF_DAY, startTimeCal.getTime().getHours());
            dateCal.set(Calendar.MINUTE, startTimeCal.getTime().getMinutes());

            String currentDate_str = dateFormatSdf.format(current_time.getTime());
            String selectedDate_str = dateFormatSdf.format(dateCal.getTime());
            String startDate = txtStartDate.getText().toString();
            String endDate = txtEndDate.getText().toString();

            if (currentDate_str.equalsIgnoreCase(selectedDate_str)) {
                if (dateCal.before(current_time)) {
                    startTimeCal.set(Calendar.HOUR_OF_DAY, hour);
                    startTimeCal.set(Calendar.MINUTE, min);
                    mStartHour = hour;
                    mStartMinute = min;
//                    setStartTime(mStartHour, mStartMinute);

                    if (startTimeCal.after(endTimeCal) && startTimeRequired.equals("")) {
                        mEndHour = -1;
                        mEndMin = 0;
//                        txtEndTime.setText("checkStart");
                        setStartTime(mStartHour, mStartMinute);
                    }
                }
            } else if (!currentDate_str.equals(selectedDate_str) && startDate.equals(endDate)) {
                setEndTime(mStartHour, mStartMinute + 1);
            }
//                txtStartTime.setText(startTimeRequired);
//                setEndDate(dateFormatSdf.format(dateCal.getTime()));

        }
    }


    public void case1_startTime(int hourOfDay, int minute) {
        try {
            Calendar todayCalender = Calendar.getInstance();
            Date currentDate = todayCalender.getTime();
            Calendar current_time = Calendar.getInstance();
            int hour = current_time.get(Calendar.HOUR_OF_DAY);
            int min = current_time.get(Calendar.MINUTE);

            String currentDate_str = dateFormatSdf.format(currentDate);
            String selectedDate_str = dateFormatSdf.format(mDate);

            startTimeCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
            startTimeCal.set(Calendar.MINUTE, minute);
            mStartHour = hourOfDay;
            mStartMinute = minute;

            Date selected_hr = timeFormatSdf.parse(hourOfDay + ":" + minute);
            Date current_hr = timeFormatSdf.parse(hour + ":" + min);
            if (currentDate_str.equalsIgnoreCase(selectedDate_str)) {

                if (selected_hr.after(current_hr)) {
                    setStartTime(mStartHour, mStartMinute);
                } else {
                    if (mStartHour == 0 && mStartMinute == 0) {
                        mStartHour = 23;
                        mStartMinute = 55;

                        setStartTime(mStartHour, mStartMinute);
                    } else {
                        mStartHour = hour;
                        mStartMinute = min;
                        startTimeCal.set(Calendar.HOUR_OF_DAY, mStartHour);
                        startTimeCal.set(Calendar.MINUTE, mStartMinute);

                        setStartTime(current_time.getTime().getHours(), current_time.getTime().getMinutes());
                    }
                }
            } else {
                setStartTime(mStartHour, mStartMinute);
            }


            if (startTimeCal.after(endTimeCal)) {
                mEndHour = -1;
                mEndMin = 0;
                //TODO Update End Time
//                txtEndTime.setText("update time");
                String endDate = formatDateFromOnetoAnother(dateNew, "yyyy-MM-dd", "dd/MM/yy");
                txtEndTime.setText(formatDateFromOnetoAnother(txtEndTimeStrl, "HH:mm", "hh:mm a"));
                txtEndDate.setText(endDate);

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void case3_startTime(int hourOfDay, int minute) {
        try {
            StartTimemDate = timeFormatSdf.parse(hourOfDay + ":" + minute);
            if (StartTimemDate.getTime() >= slot_StartTime.getTime() && StartTimemDate.before(slot_EndTime)) {
//                txtStartTime.setText(mStartHour + ":" + mStartMinute);
                setStartTime(mStartHour, mStartMinute);


                //new changes
                txtprefSlotLabel.setTextColor(getResources().getColor(R.color.grayDate));
            } else {
                mStartHour = slot_StartTime.getHours();
                mStartMinute = slot_StartTime.getMinutes();
                setStartTime(slot_StartTime.getHours(), slot_StartTime.getMinutes());
                //set start time date
                StartTimemDate = timeFormatSdf.parse(mStartHour + ":" + mStartMinute);

                //new changes
                mStartHour = hourOfDay;
                mStartMinute = minute;
                setStartTime(mStartHour, mStartMinute);
                StartTimemDate = timeFormatSdf.parse(mStartHour + ":" + mStartMinute);
                txtprefSlotLabel.setTextColor(getResources().getColor(R.color.red));

            }

            if (EndTimemDate != null) {
                if (StartTimemDate.after(EndTimemDate)) {

                    if (StartTimemDate.before(slot_EndTime)) {
                        mStartHour = hourOfDay;
                        mStartMinute = minute;
                        setStartTime(mStartHour, mStartMinute);
                    }
                    mEndHour = -1;
                    mEndMin = 0;
//                    txtEndTime.setText(" case 3");
//                    String endDate = formatDateFromOnetoAnother(dateNew, "yyyy-MM-dd", "dd/MM/yy");
                    txtEndTime.setText(formatDateFromOnetoAnother(txtEndTimeStrl, "HH:mm", "hh:mm a"));
//                    txtEndDate.setText(endDate);
//                    setEndTime(mStartHour, mStartMinute + 1);
                }
                if (EndTimemDate.after(slot_EndTime)) {
                    txtprefSlotLabel.setTextColor(getResources().getColor(R.color.red));
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void case1_endTime(int hourOfDay, int minute) {
        endTimeCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        endTimeCal.set(Calendar.MINUTE, minute);
        mEndHour = hourOfDay;
        mEndMin = minute;
//        if (endTimeCal.getTimeInMillis() > startTimeCal.getTimeInMillis()) {  //TODO: end time greater than start time
////            txtEndTime.setText(hourOfDay + ":" + minute);
//            setEndTime(hourOfDay, minute);
//        }
        String startDate = txtStartDate.getText().toString();
        String endDate = txtEndDate.getText().toString();
        if (endTimeCal.after(startTimeCal)) {
            Log.e("time", "time after: ");
//          txtEndTime.setText("time after"); // PM
            setEndTime(hourOfDay, minute);

        } else if (endTimeCal.before(startTimeCal) && !startDate.equals(endDate)) {
            setEndTime(hourOfDay, minute);
        } else {
            mEndHour = -1;
            mEndMin = 0;
            Log.e("time", "time before: ");
            setEndTime(mStartHour, mStartMinute + 1);
//             txtEndTime.setText("time before"); // AM
//            txtEndTime.setText(timeFormatSdf.format(startTimeCal.getTime()));
//            setEndTime(hourOfDay, minute);
        }

    }

    public void case3_endTime(int hourOfDay, int minute) {
        endTimeCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        endTimeCal.set(Calendar.MINUTE, minute);

        try {
            EndTimemDate = timeFormatSdf.parse(hourOfDay + ":" + minute);
            if (EndTimemDate.getTime() <= slot_EndTime.getTime() &&
                    EndTimemDate.getTime() >= StartTimemDate.getTime()) {

                setEndTime(hourOfDay, minute);
                txtprefSlotLabel.setTextColor(getResources().getColor(R.color.grayDate));
            } else if (EndTimemDate.getTime() >= StartTimemDate.getTime()) {


                if (EndTimemDate.getTime() > slot_EndTime.getTime())
                    txtprefSlotLabel.setTextColor(getResources().getColor(R.color.red));

                setEndTime(hourOfDay, minute);


            } else {

                mEndHour = -1;
                mEndMin = 0;
//                txtEndTime.setText("test");
                String startTime = txtStartTime.getText().toString();
                String endTime = txtEndTime.getText().toString();
                if (txtStartDate.getText().toString().equals(txtEndDate.getText().toString()) &&
                        startTime.compareTo(endTime) > 0) {
                    setEndTime(mStartHour, mStartMinute + 1);
                } else if (txtStartDate.getText().toString().equals(txtEndDate.getText().toString()) &&
                        startTime.compareTo(endTime) < 0) {
                    setEndTime(mStartHour, mStartMinute + 1);
                } else if (txtStartDate.getText().toString().equals(txtEndDate.getText().toString()) &&
                        startTime.equals(endTime)) {
                    setEndTime(mStartHour, mStartMinute + 1);
                } else {
                    setEndTime(hourOfDay, minute);
                }


                //new changes
                if (StartTimemDate != null)
                    if (StartTimemDate.getTime() >= slot_StartTime.getTime() &&
                            StartTimemDate.before(slot_EndTime)) {
                        //new changes
                        txtprefSlotLabel.setTextColor(getResources().getColor(R.color.grayDate));
                    }

            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void setStartTime(int hourOfDay, int minute) {

        String strStartTime = hourOfDay + ":" + minute;
        txtStartTime.setText(formatDateFromOnetoAnother(strStartTime,
                "HH:mm", "hh:mm a"));
//        txtStartTime.setText(hourOfDay + ":" + minute);

        try {
            String startTime = txtStartDate.getText().toString() + " " +
                    txtStartTime.getText().toString();
            dtStart = formatDateFromOnetoAnother(startTime,
                    "dd/MM/yy hh:mm a", "yyyy-MM-dd HH:mm:ss");
            dateStartTime = formatDateFromOnetoAnother(startTime,
                    "dd/MM/yy hh:mm aa", "dd-MM-yyyy HH:mm:ss");
            Logger.log(TAG, "JobPoolSchedule finalStartTime :" + dtStart);

            setEndDateJobType();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setStartTimeFinal() {

        try {
            String startTime = txtStartDate.getText().toString() + " " +
                    txtStartTime.getText().toString();
            dtStart = formatDateFromOnetoAnother(startTime,
                    "dd/MM/yy hh:mm a", "yyyy-MM-dd HH:mm:ss");
            Logger.log(TAG, "JobPoolSchedule finalStartTime :" + dtStart);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setEndTimeFinal() {

//New Change : txtStartDate changed into txtEndDate for job pool end date schedule task
        try {
            String endTime = txtStartDate.getText().toString() + " " +
                    txtEndTime.getText().toString();
            dtEnd = formatDateFromOnetoAnother(endTime,
                    "dd/MM/yy hh:mm a", "yyyy-MM-dd HH:mm:ss");
            Logger.log(TAG, "JobPoolSchedule finalEndTime :" + dtEnd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setEndTime(int hourOfDay, int minute) {

        String strEndTime = hourOfDay + ":" + minute;

        txtEndTime.setText(formatDateFromOnetoAnother(strEndTime,
                "HH:mm", "hh:mm a"));

//        txtEndTime.setText(hourOfDay + ":" + minute);

//New Change : txtStartDate changed into txtEndDate for job pool end date schedule task
        try {
            String endTime = txtStartDate.getText().toString() + " " +
                    txtEndTime.getText().toString();
            dtEnd = formatDateFromOnetoAnother(endTime,
                    "dd/MM/yy hh:mm a", "yyyy-MM-dd HH:mm:ss");
            Logger.log(TAG, "JobPoolSchedule finalEndTime :" + dtEnd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDate(String datee) {

        this.date = datee;
//        txtStartDate.setText(formatDateFromOnetoAnother(date,
//                "yyyy-MM-dd", "dd/MM/yy"));

        String startDate = formatDateFromOnetoAnother(datee, "yyyy-MM-dd", "dd/MM/yy");

        String endDate = txtEndDate.getText().toString();
        Calendar todayCalender = Calendar.getInstance();
        Date currentDate = todayCalender.getTime();
        String currentDateString = dateFormatSdf.format(currentDate);
        String currentDateStr = formatDateFromOnetoAnother(currentDateString, "yyyy-MM-dd", "dd/MM/yy");

//        if (startDate.compareTo(endDate) > 0 && txtEndDate.getText().length() > 0) {
//            txtStartDate.setText(startDate);
//            txtEndDate.setText(startDate);
//        }  else if (startDate.compareTo(endDate) > 0 && txtEndDate.getText().length() == 0) {
//            Log.e("date_ ", "Date1 is after Date2 is zero");
//            txtStartDate.setText(startDate);
//            date = startDate;
//        } else if (startDate.compareTo(endDate) < 0 && dtPref == null) {
//            Log.e("date_ ", "Date1 is before Date2");
//            txtStartDate.setText(startDate);
//        } else if (startDate.compareTo(endDate) < 0 && dtPref != null) {
//            Log.e("date_ ", "Date1 is before Date2");
//            txtStartDate.setText(startDate);
//            txtEndDate.setText(endDate); // changed start date to end date
//        } else if (startDate.compareTo(endDate) == 0) {
//            Log.e("date_ ", "Date1 is equals Date2");
//            txtStartDate.setText(startDate);
//            setEndTime(mStartHour, mStartMinute + 1); // new changes v53
//        } else {
//            txtStartDate.setText(startDate);
//            date = startDate;
//        }

        SimpleDateFormat dfDate = new SimpleDateFormat("dd/MM/yy");
            try {
                if (dfDate.parse(startDate).before(dfDate.parse(endDate))) {
                    txtStartDate.setText(startDate);
                    txtEndDate.setText(endDate);
                } else if (dfDate.parse(startDate).after(dfDate.parse(endDate))) {
                    txtStartDate.setText(startDate);
                    txtEndDate.setText(startDate);
                } else if (dfDate.parse(startDate).equals(dfDate.parse(endDate))) {
                    txtStartDate.setText(startDate);
                    setEndTime(mStartHour, mStartMinute + 1);
                } else {
                    txtStartDate.setText(startDate);
                    date = startDate;
                }
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        txtStartDate.setText(startDate);
        date = startDate;

    }

    /**
     * To=his method is used to set end date text
     */
    public void setEndDate(String date) {

        this.date = date;

        String startDate = txtStartDate.getText().toString();
        String endDate = formatDateFromOnetoAnother(date, "yyyy-MM-dd", "dd/MM/yy");
        checkDates(startDate, endDate);
    }

    /**
     * This method is used to validate the end date is greater / lesser or equal
     *
     * @param dtStart
     * @param dtEnd
     */
    public void checkDates(String dtStart, String dtEnd) {
        SimpleDateFormat dfDate = new SimpleDateFormat("dd/MM/yy");
        String startTimeTxt = txtStartTime.getText().toString();
        String endTimeTxt = txtEndTime.getText().toString();

        try {
            if (dfDate.parse(dtStart).before(dfDate.parse(dtEnd))) {
                Log.e("date_ ", "start date is before end date");
                txtEndDate.setText(dtEnd);
            } else if (dfDate.parse(dtStart).equals(dfDate.parse(dtEnd)) && startTimeTxt.compareTo(endTimeTxt) > 0) {
                Log.e("date_ ", "start date is equal end date");
                txtEndDate.setText(dtEnd);
                setEndTime(mStartHour, mStartMinute + 1);
            } else if (dfDate.parse(dtStart).equals(dfDate.parse(dtEnd)) && startTimeTxt.compareTo(endTimeTxt) < 0) { // 1 pm < 3pm
                txtEndDate.setText(dtEnd);
                setEndTime(mStartHour, mStartMinute + 1);
            } else {
                Log.e("date_ ", "start date is after end date");
                txtEndDate.setText(dtStart);
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * New change: To set end date,end time based on job type from start time ---> job default hr schedule
     */

    public void setEndDateJobType() {

        String addTime[] = jobDefaultHours.split(":");
        int addHour = Integer.parseInt(addTime[0]);
        int addMinutes = Integer.parseInt(addTime[1]);
        long milliseconds = addHour * 60 * 60 * 1000; // hour


        long result = TimeUnit.HOURS.toMillis(addHour) + TimeUnit.MINUTES.toMillis(addMinutes);

        DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date d = null;
        try {
            d = format.parse(dateStartTime);
            jobTypeDate = format.parse(dateStartTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
//        assert d != null;
        d.setTime(d.getTime() + result);
        jobTypeDate.setTime(d.getTime() + result);

        dateNew = dateFormatSdf.format(d.getTime());
        jobTypeDateStr = dateFormatSdf.format(d.getTime());

        jobTypeDateStr = formatDateFromOnetoAnother(dateNew, "yyyy-MM-dd", "dd/MM/yy");
        String txtEndTimeStr = d.getHours() + ":" + d.getMinutes();
        txtEndTimeStrl = txtEndTimeStr;

        String startTimeTxt = txtStartTime.getText().toString();
        String endTimeTxt = txtEndTime.getText().toString();
        String startDateTxt = txtStartDate.getText().toString();
        String endDateTxt = txtEndDate.getText().toString();
        String endDate = formatDateFromOnetoAnother(dateNew, "yyyy-MM-dd", "dd/MM/yy");

        if (txtStartDate.getText().length() > 0 && txtStartTime.getText().length() > 0 && txtEndDate.getText().length() == 0) {
            txtEndDate.setText(endDate);
            txtEndTime.setText(formatDateFromOnetoAnother(txtEndTimeStr, "HH:mm", "hh:mm a"));
        } else if (startDateTxt.equals(endDateTxt) && startTimeTxt.compareTo(endTimeTxt) > 0) {  //start time is after end time
            Log.e("endTimeTxt", "eTxt" + startTimeTxt + "\n" + endTimeTxt);
            txtEndTime.setText(formatDateFromOnetoAnother(txtEndTimeStr, "HH:mm", "hh:mm a"));
            txtEndDate.setText(endDate);
        } else if (startDateTxt.equals(endDateTxt) && startTimeTxt.compareTo(endTimeTxt) < 0) {  //start time is before end time
            Log.e("endTimeTxt", "eTxt" + startTimeTxt + "\n" + endTimeTxt);
            txtEndTime.setText(formatDateFromOnetoAnother(txtEndTimeStr, "HH:mm", "hh:mm a"));
            txtEndDate.setText(endDate);
        }
        if (startTimeCal.after(endTimeCal)) {
            //time < 11
            txtEndTime.setText(formatDateFromOnetoAnother(txtEndTimeStr, "HH:mm", "hh:mm a"));
            txtEndDate.setText(endDate);
        } else {
            txtEndDate.setText(endDate);
            txtEndTime.setText(formatDateFromOnetoAnother(txtEndTimeStr, "HH:mm", "hh:mm a"));
        }
        //yuou
//        else if(txtStartTime.getText().toString().compareTo(txtEndTime.getText().toString())>0){
//            txtEndTime.setText("test");
//        }

//        else if (!txtEndDate.getText().toString().equals(jobTypeDateStr)) {
//            txtEndDate.setText(txtStartDate.getText().toString());
//            txtEndTime.setText("");
//        }
//

        hours = d.getHours();
        mins = d.getMinutes();
        Log.e("bundle_", "bundle_ " + d);
        Log.e("bundle_", "bundle_ " + result);
        Log.e("bundle_", "bundle_ " + dateNew);
        Log.e("bundle_", "bundle_hours " + d.getHours());
        Log.e("bundle_", "bundle_min" + d.getMinutes());
        Log.e("bundle_", "bundle_  " + dateStartTime);

    }
//------------------------------------------------------------------------//

    /**
     * update end time based on start time
     */
    public void updateEndTime() {

        String[] addTime = jobDefaultHours.split(":");
        int addHour = Integer.parseInt(addTime[0]);
        int addMinutes = Integer.parseInt(addTime[1]);
        long milliseconds = addHour * 60 * 60 * 1000; // hour


        long result = TimeUnit.HOURS.toMillis(addHour) + TimeUnit.MINUTES.toMillis(addMinutes);

        DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date d = null;
        Date jobTypeDate = null;
        try {
            d = format.parse(dateStartTime);
            jobTypeDate = format.parse(dateStartTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert d != null;
        d.setTime(d.getTime() + result);
        assert jobTypeDate != null;
        jobTypeDate.setTime(d.getTime() + result);
        String txtEndTimeStr = d.getHours() + ":" + d.getMinutes();
        txtEndTime.setText(formatDateFromOnetoAnother(txtEndTimeStr, "HH:mm", "hh:mm a"));
        Log.e("endTime", "endTime" + txtEndTimeStr);

    }

    //-------------------------------------------------------------------------//
    public void setPrefSlotEndDateTime(String startDate, String startTime) {

        String addTime[] = jobDefaultHours.split(":");
        int addHour = Integer.parseInt(addTime[0]);
        int addMinutes = Integer.parseInt(addTime[1]);

        long result = TimeUnit.HOURS.toMillis(addHour) + TimeUnit.MINUTES.toMillis(addMinutes);
        String startDateTime = startDate + " " + startTime;

        String dateStartTime = formatDateFromOnetoAnother(startDateTime,
                "dd/MM/yy hh:mm aa", "dd-MM-yyyy HH:mm:ss");
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date d = null;
        try {
            d = format.parse(dateStartTime);
            jobTypeDate = format.parse(dateStartTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (d != null) {
            d.setTime(d.getTime() + result);
            jobTypeDate.setTime(d.getTime() + result);
            dateNew = dateFormatSdf.format(d.getTime());

            String txtEndTimeStr = d.getHours() + ":" + d.getMinutes();
            jobTypeDateStr = formatDateFromOnetoAnother(dateNew, "yyyy-MM-dd", "dd/MM/yy");
            if (txtStartDate.getText().length() > 0 && txtStartTime.getText().length() > 0 && txtEndDate.getText().length() == 0) {
                String test = formatDateFromOnetoAnother(dateNew, "yyyy-MM-dd", "dd/MM/yy");
                txtEndDate.setText(test);
                txtEndTime.setText(formatDateFromOnetoAnother(txtEndTimeStr, "HH:mm", "hh:mm a"));
            } else if (txtStartDate.getText().toString().equals(txtEndDate.getText().toString())) {
                txtEndTime.setText("");
            }
            hours = d.getHours();
            mins = d.getMinutes();
        }
    }

//--------------------------------------------------------------------------//

    /**
     * The Class FetchJobsOnCurrentDateAsyncTask.
     */
    @SuppressLint("StaticFieldLeak")
    private class FetchJobsOnCurrentDateAsyncTask extends
            AsyncTaskCoroutine<Void, Boolean> {


        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        public void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            DialogUtils.showProgressDialog(JobPoolSchedule.this,
                    JobPoolSchedule.this
                            .getString(R.string.textWaitLable), "", false);

        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        public Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub

            return createAndInflateDbDataInList();
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        public void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            DialogUtils.dismissProgressDialog();

            createAndNotifyAdapter();

        }

    }

    /**
     * Creates the and inflate db data in list.
     *
     * @return true, if successful
     */
    private boolean createAndInflateDbDataInList() {
        // TODO Auto-generated method stub

        if (allJobList != null) {
            allJobList.clear();
        } else {
            allJobList = new TreeMap<String, ArrayList<HashMap<String, String>>>(
                    new Comparator<String>() {

                        @Override
                        public int compare(String lhs, String rhs) {
                            // TODO Auto-generated method stub
//                            return -lhs.compareTo(rhs);
                            return -rhs.compareTo(lhs);
                        }
                    });
        }

        HashMap<String, String> map = new HashMap<String, String>();

        Vector<CommonJobBean> vect = new Vector<CommonJobBean>();

        // vectConge = dataAceesObject.getConge();

        vect = dataAceesObject.getAllJobsOfParticularDateNew(date, user.getId());

        Enumeration<CommonJobBean> en = vect.elements();

        while (en.hasMoreElements()) {

            CommonJobBean interv = en.nextElement();
            int st = interv.getCd_status_interv();
            map = new HashMap<String, String>();

            String numInterv = interv.getType_Interv();

            map.put(KEYS.CurrentJobs.DISPO, "false");
            if (!TextUtils.isEmpty(interv.getRef_customer()))
                numInterv = "#" + interv.getRef_customer() + " - "
                        + interv.getType_Interv();
            else if (interv.getNo_interv() != 0)
                numInterv = "#" + interv.getNo_interv() + " - "
                        + interv.getType_Interv();

            map.put(KEYS.CurrentJobs.TYPE_NEW, interv.getType_Interv());
            map.put(KEYS.CurrentJobs.ID, interv.getId());
            map.put(KEYS.CurrentJobs.CD_STATUS,
                    String.valueOf(interv.getCd_status_interv()));
            map.put(KEYS.CurrentJobs.ALL_JOB_HEADER, interv.getHeaderDate());
            map.put(KEYS.CurrentJobs.ID_USER,
                    String.valueOf(interv.getIdUser()));
            map.put(KEYS.CurrentJobs.CONTACT, interv.getNom_contact());
            map.put(KEYS.CurrentJobs.TEL, interv.getTel_contact());

            map.put(KEYS.CurrentJobs.ADR_VILLE, interv.getAdr_interv_ville());
            map.put(KEYS.CurrentJobs.ADR_CP, interv.getAdr_interv_cp());
            map.put(KEYS.CurrentJobs.ADR_PAYS, interv.getAdr_interv_pays());
            map.put(KEYS.CurrentJobs.ADR_RUE, interv.getAdr_interv_rue());
            map.put(KEYS.CurrentJobs.ADR_GLOBAL, interv.getAdr_interv_globale());
            map.put(KEYS.CurrentJobs.ADR_COMPLEMENT,
                    interv.getAdr_interv_complement());
            map.put(KEYS.CurrentJobs.NOM_CLIENT_INTERV,
                    interv.getNom_client_interv());

            map.put(KEYS.CurrentJobs.DESC,
                    String.valueOf(interv.getDesc()));

            map.put(KEYS.CurrentJobs.TELCEL,
                    interv.getTel_contact());

            map.put(KEYS.CurrentJobs.MOBILE_CONTACT,
                    interv.getMobileContact());

            map.put(KEYS.CurrentJobs.DATE_CREATED_REAL, interv.getDt_deb_real());
            map.put(KEYS.CurrentJobs.DESC, interv.getDesc());
            map.put(KEYS.CurrentJobs.ID_MODEL,
                    String.valueOf(interv.getId_model_rapport()));
            map.put(KEYS.CurrentJobs.TYPE, numInterv);
            map.put(KEYS.CurrentJobs.PRIORITY, interv.getPriorite() + "");

            if (!TextUtils.isEmpty(interv.getNom_site_interv()))
                map.put(KEYS.CurrentJobs.CLTVILLE, interv.getNom_site_interv()
                        + " - " + interv.getAdr_interv_ville());
            else
                map.put(KEYS.CurrentJobs.CLTVILLE,
                        interv.getNom_client_interv() + " - "
                                + interv.getAdr_interv_ville());

            map.put(KEYS.CurrentJobs.LAT, interv.getLat());
            map.put(KEYS.CurrentJobs.LON, interv.getLon());

            map.put(KEYS.CurrentJobs.NOMSITE, interv.getNom_site_interv());
            map.put(KEYS.CurrentJobs.NOMEQUIPMENT, interv.getNom_equipement());
            map.put(KEYS.CurrentJobs.IDSITE, String.valueOf(interv.getIdSite()));
            map.put(KEYS.CurrentJobs.IDCLIENT,
                    String.valueOf(interv.getIdClient()));
            map.put(KEYS.CurrentJobs.IDEQUIPMENT,
                    String.valueOf(interv.getIdEquipement()));
            map.put(KEYS.CurrentJobs.TELCEL, interv.getMobileContact());


            Date date, time1, time2;
            if (interv.getCd_status_interv() == 5) {

                date = getDateFromDbFormat(interv.getDt_deb_real());

                time1 = getDateFromDbFormat(interv.getDt_deb_real());
                time2 = getDateFromDbFormat(interv.getDt_fin_real());
            } else {
                date = getDateFromDbFormat(interv.getDt_deb_prev());
                time1 = getDateFromDbFormat(interv.getDt_deb_prev());
                time2 = getDateFromDbFormat(interv.getDt_fin_prev());
            }

            String dateToshow = null;
            if (st == KEYS.CurrentJobs.JOB__STARTED) {

                if (user.getId() != interv.getIdUser()) {
                    try {
                        dateToshow = dataAceesObject
                                .getDateWithRequiredPresettedPattern(date);
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        Logger.printException(e);
                    }
                    if (!TextUtils.isEmpty(dateToshow)) {

                        String[] dateTopass = dateToshow.split("/");
                        // String[] dateToShow = dateTopass[0].split("-");

                        // map.put(KEYS.CurrentJobs.MONTH_TO_SHOW,
                        // dateToShow[0]);
                        map.put(KEYS.CurrentJobs.DATE_TO_SHOW, dateTopass[0]);
                        // map.put(KEYS.CurrentJobs.YEAR_TO_SHOW,
                        // dateToShow[2]);
                        map.put(KEYS.CurrentJobs.TIME_TO_SHOW, dateTopass[1]);
                    }
                } else {

                    dateToshow = dataAceesObject
                            .getJobStartTime(interv.getId());
                    if (!TextUtils.isEmpty(dateToshow)) {

                        String[] dateTopass = dateToshow.split("/");
                        // String[] dateToShow = dateTopass[0].split("-");

                        // map.put(KEYS.CurrentJobs.MONTH_TO_SHOW,
                        // dateToShow[0]);
                        map.put(KEYS.CurrentJobs.DATE_TO_SHOW, dateTopass[0]);
                        // map.put(KEYS.CurrentJobs.YEAR_TO_SHOW,
                        // dateToShow[2]);
                        map.put(KEYS.CurrentJobs.TIME_TO_SHOW, dateTopass[1]);
                    }
                }

            } else if (st == KEYS.CurrentJobs.JOB__SUSPENDED) {

                dateToshow = dataAceesObject
                        .getJobSuspendedTime(interv.getId());
                if (!TextUtils.isEmpty(dateToshow)) {

                    dateToshow = dataAceesObject.getJobSuspendedTime(interv
                            .getId());
                    if (!TextUtils.isEmpty(dateToshow)) {

                        String[] dateTopass = dateToshow.split("/");

                        map.put(KEYS.CurrentJobs.DATE_TO_SHOW, dateTopass[0]);

                        map.put(KEYS.CurrentJobs.TIME_TO_SHOW, dateTopass[1]);
                    }

                }

            } else if (st == KEYS.CurrentJobs.JOB__COMPLETE) {
                map.put(KEYS.CurrentJobs.DATE_TO_SHOW, dateFormat.format(time2)
                        + "");

                map.put(KEYS.CurrentJobs.TIME_TO_SHOW, format.format(time2));
            } else if ((st == KEYS.CurrentJobs.JOB_NOT_STARTED1)
                    || (st == KEYS.CurrentJobs.JOB_NOT_STARTED2)) {
                map.put(KEYS.CurrentJobs.DATE_TO_SHOW, dateFormat.format(date)
                        + "");

                map.put(KEYS.CurrentJobs.TIME_TO_SHOW, format.format(date));
            }

            // map.put(KEYS.CurrentJobs.PLAN, formatDay.format(date) + " "
            // + df.format(date) + "  -  " + df1.format(time1) + " > "
            // + df1.format(time2));

            map.put(KEYS.CurrentJobs.MDATE1, interv.getDt_deb_prev());
            map.put(KEYS.CurrentJobs.MDATE2, interv.getDt_fin_prev());
            if (!TextUtils.isEmpty(interv.getDt_meeting())) {
                map.put(KEYS.CurrentJobs.DATEMEETING, interv.getDt_meeting());
            } else
                map.put(KEYS.CurrentJobs.DATEMEETING, "");

            map.put(KEYS.CurrentJobs.JOB_TYPE, st + "");

            if (allJobList
                    .containsKey(map.get(KEYS.CurrentJobs.ALL_JOB_HEADER))) {
                allJobList.get(map.get(KEYS.CurrentJobs.ALL_JOB_HEADER)).add(
                        map);

            } else {
                allJobList.put(map.get(KEYS.CurrentJobs.ALL_JOB_HEADER),
                        new ArrayList<HashMap<String, String>>());
                allJobList.get(map.get(KEYS.CurrentJobs.ALL_JOB_HEADER)).add(
                        map);
            }

        }

        return true;

    }


    /**
     * Gets the date from db format.
     *
     * @param mDate the m date
     * @return the date from db format
     */
    public Date getDateFromDbFormat(String mDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date;
        try {
            date = sdf.parse(mDate);
            return date;
        } catch (Exception e) {
            Logger.printException(e);
            return new Date();
        }
    }

    /**
     * Creates the and notify adapter.
     */
    private void createAndNotifyAdapter() {

        if (allJobList.isEmpty()) {
            empty_text.setVisibility(View.VISIBLE);
            jobListLv.setVisibility(View.GONE);
            return;
        } else {
            empty_text.setVisibility(View.GONE);
            jobListLv.setVisibility(View.VISIBLE);
        }
        jobListLv.removeFooterView(footerView);
        /*
         * SimpleAdapter adapter = new SimpleAdapter (this.getBaseContext(),
         * listItem, R.layout.list_item_interv, new String[] {"img1", "type",
         * "cltVille","plan", "img2"}, new int[] {R.id.img1, R.id.text1 ,
         * R.id.text2 , R.id.text3 ,R.id.img2});
         */
        if (allJobsListAdapter == null) {
            allJobsListAdapter = new AllJobScheduleListAdapter(this, allJobList);
            jobListLv.setAdapter(allJobsListAdapter);
        } else {
            allJobsListAdapter.notifyDataSetChanged();
        }

//        jobListLv.removeFooterView(footerView);

        EventBus.getDefault().post(new UpdateUiOnSync());

    }


    /**
     * Synch.
     */
    public void synch() {

        if (SynchroteamUitls.isNetworkAvailable(JobPoolSchedule.this)) {
            if (progressDSynch == null)
                progressDSynch = ProgressDialog.show(JobPoolSchedule.this,
                        getString(R.string.textPleaseWaitLable),
                        getString(R.string.msg_synch), true, false);
            else if (progressDSynch != null && !progressDSynch.isShowing())
                progressDSynch.show();


            Thread syncDbToServer = new Thread((new Runnable() {

                @Override
                public void run() {

                    Message myMessage = new Message();
                    try {
                        User u = dataAceesObject.getUser();
                        dataAceesObject.sync(u.getLogin(), u.getPwd());
                        Logger.output(TAG, " finished sync");
                        Thread.sleep(1000);
                        myMessage.obj = "ok";

                        handler.sendMessage(myMessage);

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

                        handler.sendMessage(myMessage);

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
            finish();
        }
    }

    /**
     * The handler.
     */
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            String erreur = (String) msg.obj;
//            EventBus.getDefault().post(new UpdateDataBaseEvent());

            if (erreur.equals("ok")) {
                Toast.makeText(JobPoolSchedule.this,
                        getString(R.string.msg_synch_ok), Toast.LENGTH_LONG)
                        .show();
                //update the list on main page
                EventBus.getDefault().post(new UpdateDataBaseEvent());

                finish();

            } else if (erreur.equals("4001") || erreur.equals("4000")) {
                showAuthErrDialog();
            } else if (erreur.equals("4006")) {
                Toast.makeText(JobPoolSchedule.this,
                        getString(R.string.msg_synch_error_4),
                        Toast.LENGTH_LONG).show();
                finish();

            } else if (erreur.equals("4101")) {
                showMultipleDeviecDialog();
            } else if (erreur.equals("4005")) {
                showAppUpdatetDialog();
            } else if (erreur.equals("4003")) {
                showErrMsgDialog(getString(R.string.msg_sync_error_4003));
            } else {
//                previous code
//                Toast.makeText(JobPoolSchedule.this,
//                        getString(R.string.msg_synch_error_new), Toast.LENGTH_LONG)
//                        .show();
//
//                if (closeActivity) {
//                    finish();
//                }
                showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));
                /*updated code*/
            }

        }
    };


    /**
     * Show error dialog
     */
    protected void showErrMsgDialog(String errMsg) {

        ErrorDialog errDialog = new ErrorDialog(JobPoolSchedule.this, errMsg);

        errDialog.show();
    }

    /**
     * For showing the synchronization failure messages
     */
    protected void showSyncFailureMsgDialog(String syncFailureMsg) {

        if (!isFinishing()) {
            SynchronizationErrorDialog synchronizationErrorDialog = new SynchronizationErrorDialog
                    (JobPoolSchedule.this, syncFailureMsg, new SynchronizationErrorDialog
                            .SynchronizationErrorInterface() {
                        @Override
                        public void doOnOkayClick() {
                            //perform any action

                            finish();
                        }
                    });

            synchronizationErrorDialog.show();
        }
    }

    /**
     * Show authentication error dialog
     */
    protected void showAuthErrDialog() {

        AuthenticationErrorDialog authenticationErrorDialog = new AuthenticationErrorDialog(
                JobPoolSchedule.this, user.getLogin());

        authenticationErrorDialog.show();
    }

    /**
     * Show app update dialog
     */
    protected void showAppUpdatetDialog() {

        AppUpdateDialog appUpdateDialog = new AppUpdateDialog(this);

        appUpdateDialog.show();
    }


    /**
     * Show multiple user dialog
     */
    protected void showMultipleDeviecDialog() {

        MultipleDeviceNotSupported multipleDeviceDialog = new MultipleDeviceNotSupported(
                this,
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

    /***
     * Create a chooser of browsers to open the link
     *
     * @param link
     */
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

    public Date getDateFromStrDate(String mDate) {
//        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yy");
        Date date;
        try {
            date = df.parse(mDate);
            return date;
        } catch (ParseException e) {
            Logger.printException(e);
            return new Date();
        }
    }
}