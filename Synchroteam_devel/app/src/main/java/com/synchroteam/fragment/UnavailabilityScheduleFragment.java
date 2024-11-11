package com.synchroteam.fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.UnavailabilityBeans;
import com.synchroteam.beans.UpdateDataBaseEvent;
import com.synchroteam.dao.Dao;
import com.synchroteam.listadapters.IssuesListAdapterAddUnavailabilityRc;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DateTimePicker;
import com.synchroteam.utils.DialogUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.greenrobot.event.EventBus;


@SuppressLint({"NewApi", "SimpleDateFormat"})
public class UnavailabilityScheduleFragment extends Fragment implements
        OnClickListener {

    // .............................UI...ELEMENTS...STARTS...HERE................................................

    private RecyclerView unavailabilityListView;
    private EditText edtIssueDescription;
    private TextView txtCreate, txtStartDate, txtEndDate, txtStartTime,
            txtEndTime;
    private LinearLayout linStartDate, linEndDate;
    private Date fromDate, toDate;
    private ArrayList<UnavailabilityBeans> unavailabilitiesList;

    private Dao dataAccessObject;
    // private SharedPreferences issuesPositionPref;
    private String pos;
    private String notesDescription;
    private SimpleDateFormat sdf;

    private String fromDateDB, endDateDB;

    private String idInterv, idUser;

    private String currentDateFormat = "yyyy-MM-dd HH:mm:ss";

    //--------NEW DATE TIME PICKER PARAMS--------------
    private Calendar mCalendar;
    private boolean isStartDateClicked;
    private static final int ONE_MINUTE_IN_MILLIS = 60000;
    //--------NEW DATE TIME PICKER PARAMS--------------

    private static final String TAG = "UnavailabilityScheduleFragment";

    // *****************************UI...ELEMENTS...ENDS...HERE**************************************************

    // ...............................LIFECYCLE...METHOD...STARTS...HERE.........................................
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_unavailability,
                container, false);
        initializeUI(view);
        return view;
    }

    // *********************************LIFECYCLE...METHOD...ENDS...HERE*******************************************

    // ...................................LOCAL...METHOD...STARTS...HERE.............................................
    private void initializeUI(View view) {
        unavailabilityListView = (RecyclerView) view
                .findViewById(R.id.unavailabilityListView);
        edtIssueDescription = (EditText) view
                .findViewById(R.id.edtIssueDescription);
        txtCreate = (TextView) view.findViewById(R.id.txtCreate);

        txtStartDate = (TextView) view.findViewById(R.id.txtStartDate);
        txtStartTime = (TextView) view.findViewById(R.id.txtStartTimE);
        txtEndTime = (TextView) view.findViewById(R.id.txtEndTime);
        txtEndDate = (TextView) view.findViewById(R.id.txtEndDate);
        linStartDate = (LinearLayout) view.findViewById(R.id.linStartDate);
        linEndDate = (LinearLayout) view.findViewById(R.id.linEndDate);
        dataAccessObject = DaoManager.getInstance();

        unavailabilitiesList = new ArrayList<UnavailabilityBeans>();
        unavailabilitiesList = (ArrayList<UnavailabilityBeans>) dataAccessObject
                .getUnavailabilities();
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

        unavailabilityListView.setLayoutManager(llm);
        unavailabilityListView
                .setAdapter(new IssuesListAdapterAddUnavailabilityRc(
                        getActivity(), unavailabilitiesList, false));

        // issuesPositionPref =
        // getActivity().getSharedPreferences("issues_pref",
        // Context.MODE_PRIVATE);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        idInterv = dataAccessObject.getStartedJobId();
        idUser = " ";

        txtCreate.setOnClickListener(this);
        linStartDate.setOnClickListener(this);
        linEndDate.setOnClickListener(this);

    }

    @SuppressLint({"SimpleDateFormat", "InflateParams"})
    private void showDateTimeDialg(final int type) {
        // Create the dialog
        final Dialog mDateTimeDialog = new Dialog(getActivity());
        // Inflate the root layout
        final RelativeLayout mDateTimeDialogView = (RelativeLayout) getActivity()
                .getLayoutInflater().inflate(R.layout.date_time_dialog, null);
        // Grab widget instance
        final DateTimePicker mDateTimePicker = (DateTimePicker) mDateTimeDialogView
                .findViewById(R.id.DateTimePicker);

        // mDateTimePicker.setMinimumDate();

        // Check is system is set to use 24h time (this doesn't seem to work as
        // expected though)
        final String timeS = android.provider.Settings.System.getString(
                getActivity().getContentResolver(),
                android.provider.Settings.System.TIME_12_24);
        final boolean is24h = !(timeS == null || timeS.equals("12"));

        // Update demo TextViews when the "OK" button is clicked
        ((Button) mDateTimeDialogView.findViewById(R.id.SetDateTime))
                .setOnClickListener(new OnClickListener() {

                    @SuppressLint({"InflateParams", "SimpleDateFormat"})
                    public void onClick(View v) {
                        mDateTimePicker.clearFocus();
                        // TODO Auto-generated method stub

                        if (type == 0) {
                            SimpleDateFormat formatMonth = new SimpleDateFormat(
                                    "MMM");
                            fromDate = mDateTimePicker.getCurrentDate();
                            fromDateDB = sdf.format(fromDate);

                            //TODO ***********NEW CHANGES***************
                            String startDateTime = null;
                            String startDate = null;
                            String startTime = null;

                            try {
                                startDateTime = dataAccessObject.getDateWithRequiredPresettedPattern(fromDateDB, currentDateFormat);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            startDate = startDateTime.substring(0, startDateTime.indexOf("/"));
                            startTime = startDateTime.substring(startDateTime.indexOf("/") + 1);

                            txtStartDate.setText(startDate);
                            txtStartTime.setText(startTime);

                            //TODO ***********NEW CHANGES***************

//							txtStartDate.setText(formatMonth
//									.format(mDateTimePicker.getCurrentDate())
//									+ " "
//									+ (mDateTimePicker
//											.get(Calendar.DAY_OF_MONTH))
//									+ " "
//									+ mDateTimePicker.get(Calendar.YEAR));
//							if (mDateTimePicker.is24HourView()) {
//								txtStartTime.setText(String.format("%02d",
//										mDateTimePicker
//												.get(Calendar.HOUR_OF_DAY))
//										+ ":"
//										+ String.format("%02d", mDateTimePicker
//												.get(Calendar.MINUTE)) + ",");
//							} else {
//								txtStartTime.setText(String.format("%02d",
//										mDateTimePicker.get(Calendar.HOUR))
//										+ ":"
//										+ String.format("%02d", mDateTimePicker
//												.get(Calendar.MINUTE))
//										+ " "
//										+ (mDateTimePicker.get(Calendar.AM_PM) == Calendar.AM ? "AM"
//												: "PM") + ",");
//							}
                        } else if (type == 1) {
                            toDate = mDateTimePicker.getCurrentDate();
                            endDateDB = sdf.format(toDate);

                            SimpleDateFormat formatMonth = new SimpleDateFormat(
                                    "MMM");

                            //TODO ***********NEW CHANGES***************
                            String endDateTime = null;
                            String endDate = null;
                            String endTime = null;

                            try {
                                endDateTime = dataAccessObject.getDateWithRequiredPresettedPattern(endDateDB, currentDateFormat);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            endDate = endDateTime.substring(0, endDateTime.indexOf("/"));
                            endTime = endDateTime.substring(endDateTime.indexOf("/") + 1);

                            txtEndDate.setText(endDate);
                            txtEndTime.setText(endTime);

                            //TODO ***********NEW CHANGES***************

//							txtEndDate.setText(formatMonth
//									.format(mDateTimePicker.getCurrentDate())
//									+ " "
//									+ (mDateTimePicker
//											.get(Calendar.DAY_OF_MONTH))
//									+ " "
//									+ mDateTimePicker.get(Calendar.YEAR));
//							if (mDateTimePicker.is24HourView()) {
//								txtEndTime.setText(String.format("%02d",
//										mDateTimePicker
//												.get(Calendar.HOUR_OF_DAY))
//										+ ":"
//										+ String.format("%02d", mDateTimePicker
//												.get(Calendar.MINUTE)) + ",");
//							} else {
//								txtEndTime.setText(String.format("%02d",
//										mDateTimePicker.get(Calendar.HOUR))
//										+ ":"
//										+ String.format("%02d", mDateTimePicker
//												.get(Calendar.MINUTE))
//										+ " "
//										+ (mDateTimePicker.get(Calendar.AM_PM) == Calendar.AM ? "AM"
//												: "PM") + ",");
//							}
                        }
                        if (toDate != null && fromDate != null) {
                            int comparedResult = toDate.compareTo(fromDate);
                            if (comparedResult == -1 || comparedResult == 0) {
                                txtEndDate.setText("");
                                txtEndTime.setText("");
                                toDate = null;
                                Toast.makeText(
                                        getActivity(),
                                        getActivity().getString(
                                                R.string.higher_to_date),
                                        Toast.LENGTH_LONG).show();
                            }

                        }

                        // if (fromDate != null) {
                        // int comparedResultDate = fromDate
                        // .compareTo(currentDate);
                        // if (comparedResultDate == -1
                        // || comparedResultDate == 0) {
                        // txtStartDate.setText("");
                        // txtStartTime.setText("");
                        // fromDate = null;
                        // Toast.makeText(
                        // getActivity(),
                        // getActivity().getString(
                        // R.string.lesser_from_date),
                        // Toast.LENGTH_LONG).show();
                        // }
                        // }

                        mDateTimeDialog.dismiss();

                    }
                });

        // Cancel the dialog when the "Cancel" button is clicked
        ((Button) mDateTimeDialogView.findViewById(R.id.CancelDialog))
                .setOnClickListener(new OnClickListener() {

                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        mDateTimeDialog.cancel();
                    }
                });

        // Reset Date and Time pickers when the "Reset" button is clicked
        ((Button) mDateTimeDialogView.findViewById(R.id.ResetDateTime))
                .setOnClickListener(new OnClickListener() {

                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        mDateTimePicker.reset();
                    }
                });

        // Setup TimePicker
        mDateTimePicker.setIs24HourView(is24h);
        // No title on the dialog window
        mDateTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // Set the dialog content view
        mDateTimeDialog.setContentView(mDateTimeDialogView);
        // Display the dialog
        mDateTimeDialog.show();

    }

    /**
     * <p>
     * Check whether the from date is lesser than current date or not.
     * </p>
     * <p>
     * If it is lesser than current date, stop the job which is currently
     * running. Else don't stop the job.
     * </p>
     */
    private void checkAndStopCurrentJob() {
        Calendar cal = Calendar.getInstance();
        String currentDateStr = sdf.format(cal.getTime());

        try {
            Date currentDate = sdf.parse(currentDateStr);
            Date fromDate = sdf.parse(fromDateDB);
            if (currentDate.compareTo(fromDate) > 0) {
                if (dataAccessObject.updateStatutInterv(4, idInterv))
                    dataAccessObject.setTimeClotIntervention(idInterv, idUser
                            + "", "");
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void setIssuePosition(int position) {
        pos = String.valueOf(position);
    }

    // **********************************LOCAL...METHOD...ENDS...HERE****************************************************

    // ...................................LISTENER...STARTS...HERE........................................................
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtCreate:
                // pos = issuesPositionPref.getString("pos", null);

                notesDescription = edtIssueDescription.getText().toString();

                if (pos == null || TextUtils.isEmpty(fromDateDB)
                        || TextUtils.isEmpty(endDateDB)) {
                    if (pos == null) {
                        Toast.makeText(getActivity(),
                                getActivity().getString(R.string.select_type),
                                Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(fromDateDB)) {
                        Toast.makeText(getActivity(),
                                getActivity().getString(R.string.enter_start_date),
                                Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(endDateDB)) {
                        Toast.makeText(getActivity(),
                                getActivity().getString(R.string.enter_end_date),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    new addUnavailabilityInDBAsync().execute();
                }
                break;
            case R.id.linStartDate:
//			showDateTimeDialg(0);
                isStartDateClicked = true;
                showDatePicker();
                break;
            case R.id.linEndDate:
                isStartDateClicked = false;
                if (TextUtils.isEmpty(fromDateDB)) {
                    Toast.makeText(getActivity(),
                            getActivity().getString(R.string.enter_start_date),
                            Toast.LENGTH_SHORT).show();
                } else {
                    showDatePicker();
                }
                break;
            default:
                break;
        }
    }

    // ...................................LISTENER...ENDS...HERE........................................................

    // .....................................ASYNC...CLASS...STARTS...HERE...............................................

    /**
     * The Class SaveNewJobDataAsyncTask.
     */
    private class addUnavailabilityInDBAsync extends
            AsyncTaskCoroutine<String, Boolean> {

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        public void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            DialogUtils.showProgressDialog(getActivity(), getActivity()
                    .getString(R.string.textWaitLable), getActivity()
                    .getString(R.string.textSavingAddJobData), false);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        public Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub

            boolean drp = dataAccessObject.addUnavailability(null,
                    unavailabilitiesList.get(Integer.parseInt(pos))
                            .getUnavailabilityID(), 0, fromDateDB, endDateDB,
                    notesDescription);
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

            DialogUtils.dismissProgressDialog();

            boolean drp = result.booleanValue();
            if (drp) {
                Toast.makeText(getActivity(),
                        getActivity().getString(R.string.unavailability_added),
                        Toast.LENGTH_SHORT).show();

//				checkAndStopCurrentJob();
                EventBus.getDefault().post(new UpdateDataBaseEvent());
                getActivity().finish();
            } else
                Toast.makeText(getActivity(), R.string.msg_error,
                        Toast.LENGTH_SHORT).show();
        }

    }
    // *************************************ASYNC...CLASS...ENDS...HERE*************************************************

    //************************************NEW...DATE...TIME...PICKER***************************************************

    /**
     * Initialize the date picker and sets date set listener for it.
     */
    private void showDatePicker() {

        mCalendar = Calendar.getInstance();
        int mYear = mCalendar.get(Calendar.YEAR);
        int mMonth = mCalendar.get(Calendar.MONTH);
        int mDay = mCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), mDateSetListener, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    /**
     * Initialize the time picker and sets time set listener for it.
     */
    private void showTimePicker() {

//        if (!isStartDateClicked) {
//            long t = mCalendar.getTimeInMillis();
//            Date newDate = new Date(t + (5 * ONE_MINUTE_IN_MILLIS));
//            mCalendar.setTime(newDate);
//        }

//        final Calendar calendar = Calendar.getInstance();
        int mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = mCalendar.get(Calendar.MINUTE);

        //check if system has 24h format or not
        boolean is24h = DateFormat.is24HourFormat(getActivity());

        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), mTimeSetListener, mHour, mMinute, is24h);
        timePickerDialog.show();
    }

    /**
     * Date set listener for date picker.
     */
    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (view.isShown()) {
                mCalendar.set(year, monthOfYear, dayOfMonth,
                        mCalendar.get(Calendar.HOUR_OF_DAY),
                        mCalendar.get(Calendar.MINUTE));
                if (isStartDateClicked) {
                    updateStartDate();
                } else {
                    updateEndDate();
                }
            }
        }
    };

    /**
     * Time set listener for time picker.
     */
    private TimePickerDialog.OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCalendar.set(mCalendar.get(Calendar.YEAR),
                    mCalendar.get(Calendar.MONTH),
                    mCalendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
            if (isStartDateClicked) {
                updateStartTime();
            } else {
                updateEndTime();
            }
        }
    };

    /**
     * Updates start date value
     */
    private void updateStartDate() {

        fromDate = mCalendar.getTime();
        fromDateDB = sdf.format(fromDate);

        String startDateTime = null;
        String startDate;
        String startTime;

        try {
            startDateTime = dataAccessObject.getDateWithRequiredPresettedPattern(fromDateDB, currentDateFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        startDate = startDateTime.substring(0, startDateTime.indexOf("/"));

        txtStartDate.setText(startDate);
        showTimePicker();
        updateStartTime();
    }

    /**
     * Updates start time value
     */
    private void updateStartTime() {

        fromDate = mCalendar.getTime();
        fromDateDB = sdf.format(fromDate);

        boolean is24h = DateFormat.is24HourFormat(getActivity());

        if (is24h) {

            String m24hTime = String.format(Locale.US, "%02d",
                    mCalendar
                            .get(Calendar.HOUR_OF_DAY))
                    + ":"
                    + String.format(Locale.US, "%02d", mCalendar
                    .get(Calendar.MINUTE));
            txtStartTime.setText(m24hTime);
        } else {
            String mNormalTime = String.format(Locale.US, "%02d",
                    mCalendar.get(Calendar.HOUR))
                    + ":"
                    + String.format(Locale.US, "%02d", mCalendar
                    .get(Calendar.MINUTE))
                    + " "
                    + (mCalendar.get(Calendar.AM_PM) == Calendar.AM ? "AM"
                    : "PM");
            txtStartTime.setText(mNormalTime);
        }

        validateEndDate();

    }

    /**
     * Updates end date value
     */
    private void updateEndDate() {

        toDate = mCalendar.getTime();
        endDateDB = sdf.format(toDate);

        String endDateTime = null;
        String endDate;
        String endTime;

        try {
            endDateTime = dataAccessObject.getDateWithRequiredPresettedPattern(endDateDB, currentDateFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        endDate = endDateTime.substring(0, endDateTime.indexOf("/"));

        txtEndDate.setText(endDate);
        showTimePicker();
        updateEndTime();

    }

    /**
     * Updates end time value
     */
    private void updateEndTime() {

        toDate = mCalendar.getTime();
        endDateDB = sdf.format(toDate);

        boolean is24h = DateFormat.is24HourFormat(getActivity());

        if (is24h) {

            String m24hTime = String.format(Locale.US, "%02d",
                    mCalendar
                            .get(Calendar.HOUR_OF_DAY))
                    + ":"
                    + String.format(Locale.US, "%02d", mCalendar
                    .get(Calendar.MINUTE));
            txtEndTime.setText(m24hTime);
        } else {
            String mNormalTime = String.format(Locale.US, "%02d",
                    mCalendar.get(Calendar.HOUR))
                    + ":"
                    + String.format(Locale.US, "%02d", mCalendar
                    .get(Calendar.MINUTE))
                    + " "
                    + (mCalendar.get(Calendar.AM_PM) == Calendar.AM ? "AM"
                    : "PM");
            txtEndTime.setText(mNormalTime);
        }

        validateEndDate();
    }

    /**
     * check if end date is higher than start date or not.
     */
    private void validateEndDate() {
        if (toDate != null && fromDate != null) {
            int comparedResult = toDate.compareTo(fromDate);
            if (comparedResult == -1 || comparedResult == 0) {
                txtEndDate.setText("");
                txtEndTime.setText("");
                toDate = null;
                Toast.makeText(
                        getActivity(),
                        getActivity().getString(
                                R.string.higher_to_date),
                        Toast.LENGTH_LONG).show();
            }

        }
    }
    //************************************NEW...DATE...TIME...PICKER***************************************************

}
