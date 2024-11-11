package com.synchroteam.synchroteam;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.synchroteam.listadapters.IssuesListAdapterUpdateUnavailabilityRc;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DateChecker;
import com.synchroteam.utils.DateTimePicker;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;

import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.greenrobot.event.EventBus;


/**
 * @author trident
 *         <p/>
 *         This class is used to update the values of unavailability which is
 *         already created
 */
public class UpdateUnavailability extends AppCompatActivity implements
        OnClickListener {

    // .............................UI...ELEMENTS...STARTS...HERE................................................

    private RecyclerView unavailabilityListView;
    private EditText edtIssueDescription;
    private TextView txtUpdate, txtStartDate, txtEndDate, txtStartTime,
            txtEndTime;
    private LinearLayout linStartDate, linEndDate;
    private Date fromDate, toDate;
    private Date currentDate;
    private ArrayList<UnavailabilityBeans> unavailabilitiesList;
    private ArrayList<UnavailabilityBeans> modifiedUnavailList;
    private Dao dataAccessObject;
    // private SharedPreferences issuesPositionPref;
    private String pos;
    private String notesDescription;
    private SimpleDateFormat sdf;

    private String fromDateDB, endDateDB;
    private String unavailabilityID, startDate, startTime, endDate, endTime,
            startDateTime, endDateTime, idInterv, idUser, flUnavailable, flPayable;
    private ActionBar actionBar;

    private String fromDateStr, toDateStr;

    private String idActivityUser;
    private String idGroupe;

    private Bundle bundle;
    // private Editor edit;

    private static final String dateFormat1 = "mm/dd/yyyy";
    private static final String dateFormat2 = "dd/mm/yyyy";
    private static final String dateFormat3 = "yyyy/mm/dd";

    private String currentDateFormat = "yyyy-MM-dd HH:mm:ss";

    //--------NEW DATE TIME PICKER PARAMS--------------
    private Calendar mCalendar;
    private boolean isStartDateClicked;
    private static final int ONE_MINUTE_IN_MILLIS = 60000;
    //--------NEW DATE TIME PICKER PARAMS--------------


    private static final String TAG = "UpdateUnavailability";

    // *****************************UI...ELEMENTS...ENDS...HERE**************************************************

    // ...............................LIFECYCLE...METHOD...STARTS...HERE.........................................

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_update_unavailability);
        initializeUI();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //New changes
        DateChecker.checkDateAndNavigate(this, dataAccessObject);

    }


    @Override
    protected void onDestroy() {
        DialogUtils.dismissProgressDialog();
        super.onDestroy();
    }

    // @Override
    // protected void onStop() {
    // super.onStop();
    // edit.putString("pos", null);
    // edit.commit();
    // }

    // *********************************LIFECYCLE...METHOD...ENDS...HERE*******************************************

    // ............................OVERRIDDEN...METHOD...STARTS...HERE...........................................
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // ****************************OVERRIDDEN...METHOD...ENDS...HERE******************************************

    // ...................................LOCAL...METHOD...STARTS...HERE.............................................
    @SuppressLint("SimpleDateFormat")
    private void initializeUI() {
        unavailabilityListView = (RecyclerView) findViewById(R.id.unavailabilityListView);
        LinearLayoutManager llm=new LinearLayoutManager(UpdateUnavailability.this,LinearLayoutManager.HORIZONTAL,false);
        unavailabilityListView.setLayoutManager(llm);
        edtIssueDescription = (EditText) findViewById(R.id.edtIssueDescription);
        txtUpdate = (TextView) findViewById(R.id.txtCreate);

        txtStartDate = (TextView) findViewById(R.id.txtStartDate);
        txtStartTime = (TextView) findViewById(R.id.txtStartTimE);
        txtEndTime = (TextView) findViewById(R.id.txtEndTime);
        txtEndDate = (TextView) findViewById(R.id.txtEndDate);
        linStartDate = (LinearLayout) findViewById(R.id.linStartDate);
        linEndDate = (LinearLayout) findViewById(R.id.linEndDate);
        dataAccessObject = DaoManager.getInstance();

        actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setHomeButtonEnabled(true);

        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        bundle = getIntent().getExtras();

        unavailabilityID = bundle.getString("id");
        startDate = bundle.getString("start_date");
        startTime = bundle.getString("start_time");
        endDate = bundle.getString("end_date");
        endTime = bundle.getString("end_time");

        // startDate = startDate.substring(0, startDate.indexOf(",")) + ""
        // + startDate.substring(startDate.indexOf(",") + 1);

        txtStartDate.setText(startDate);
        txtStartTime.setText(startTime);

        if (endDate != null && endTime != null) {
            // endDate = endDate.substring(0, endDate.indexOf(",")) + ""
            // + endDate.substring(endDate.indexOf(",") + 1);
            txtEndDate.setText(endDate);
            txtEndTime.setText(endTime);
        }
        edtIssueDescription.setText(bundle.getString("description"));
        edtIssueDescription
                .setSelection(edtIssueDescription.getText().length());
        startDateTime = bundle.getString("start_date_time");
        endDateTime = bundle.getString("end_date_time");

        fromDateDB = startDateTime;
        endDateDB = endDateTime;
        fromDateStr = startDateTime;
        toDateStr = endDateTime;

		/*
         * new change... To set the selected unavailabilty to first position...
		 * iterate the original list and put the selected issue to first
		 * position.
		 */
        String type = bundle.getString(KEYS.Unavabilities.TYPE);

        idActivityUser = bundle.getString(KEYS.Unavabilities.ID_USER);
        idGroupe = bundle.getString(KEYS.Unavabilities.ID_GROUPE);
        flUnavailable = bundle.getString(KEYS.Unavabilities.FL_UNAVAILABLE);
        flPayable = bundle.getString(KEYS.Unavabilities.FL_PAYABLE);

        unavailabilitiesList = new ArrayList<UnavailabilityBeans>();
        modifiedUnavailList = new ArrayList<UnavailabilityBeans>();
        unavailabilitiesList = (ArrayList<UnavailabilityBeans>) dataAccessObject
                .getSimilarUnavailabilities(Integer.parseInt(flUnavailable),Integer.parseInt(flPayable));

        Logger.output(TAG, "idActivityUser : " + idActivityUser + "idGroupe : " + idGroupe);

        for (int i = 0; i < unavailabilitiesList.size(); i++) {
            if (type.equalsIgnoreCase(unavailabilitiesList.get(i)
                    .getUnavailabilityName())) {
                UnavailabilityBeans unavailabilityBeans = new UnavailabilityBeans();
                unavailabilityBeans.setCustomerID(unavailabilitiesList.get(i)
                        .getCustomerID());
                unavailabilityBeans
                        .setUnavailabilityColorCode(unavailabilitiesList.get(i)
                                .getUnavailabilityColorCode());
                unavailabilityBeans.setUnavailabilityID(unavailabilitiesList
                        .get(i).getUnavailabilityID());
                unavailabilityBeans.setUnavailabilityName(unavailabilitiesList
                        .get(i).getUnavailabilityName());
                modifiedUnavailList.add(0, unavailabilityBeans);
            } else {
                UnavailabilityBeans unavailabilityBeans = new UnavailabilityBeans();
                unavailabilityBeans.setCustomerID(unavailabilitiesList.get(i)
                        .getCustomerID());
                unavailabilityBeans
                        .setUnavailabilityColorCode(unavailabilitiesList.get(i)
                                .getUnavailabilityColorCode());
                unavailabilityBeans.setUnavailabilityID(unavailabilitiesList
                        .get(i).getUnavailabilityID());
                unavailabilityBeans.setUnavailabilityName(unavailabilitiesList
                        .get(i).getUnavailabilityName());
                modifiedUnavailList.add(unavailabilityBeans);
            }

        }

        unavailabilityListView
                .setAdapter(new IssuesListAdapterUpdateUnavailabilityRc(this,
                        modifiedUnavailList, type, idActivityUser));
        // unavailabilityListView.setAdapter(new IssuesListAdapter(this,
        // modifiedUnavailList, false, bundle.getString("color_code")));

        // issuesPositionPref = this.getSharedPreferences("issues_pref",
        // Context.MODE_PRIVATE);
        // edit = issuesPositionPref.edit();

        idInterv = dataAccessObject.getStartedJobId();

        txtUpdate.setOnClickListener(this);
//        linStartDate.setOnClickListener(this);
//        linEndDate.setOnClickListener(this);

        if (TextUtils.isEmpty(toDateStr)) {
            linStartDate.setClickable(false);
        }

        restrictUpdate();

    }

    @SuppressLint({"SimpleDateFormat", "InflateParams"})
    private void showDateTimeDialg(final int type) {
        // Create the dialog
        final Dialog mDateTimeDialog = new Dialog(this);
        // Inflate the root layout
        final RelativeLayout mDateTimeDialogView = (RelativeLayout) this
                .getLayoutInflater().inflate(R.layout.date_time_dialog, null);
        // Grab widget instance
        final DateTimePicker mDateTimePicker = (DateTimePicker) mDateTimeDialogView
                .findViewById(R.id.DateTimePicker);

        // mDateTimePicker.setMinimumDate();

        // Check is system is set to use 24h time (this doesn't seem to work as
        // expected though)
        final String timeS = android.provider.Settings.System.getString(
                this.getContentResolver(),
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

//                            txtStartDate.setText((mDateTimePicker
//                                    .get(Calendar.DAY_OF_MONTH))
//                                    + " "
//                                    + formatMonth.format(mDateTimePicker
//                                    .getCurrentDate())
//                                    + " "
//                                    + mDateTimePicker.get(Calendar.YEAR));
//
//
//                            if (mDateTimePicker.is24HourView()) {
//                                txtStartTime.setText(String.format("%02d",
//                                        mDateTimePicker
//                                                .get(Calendar.HOUR_OF_DAY))
//                                        + ":"
//                                        + String.format("%02d", mDateTimePicker
//                                        .get(Calendar.MINUTE)));
//                            } else {
//                                txtStartTime.setText(String.format("%02d",
//                                        mDateTimePicker.get(Calendar.HOUR))
//                                        + ":"
//                                        + String.format("%02d", mDateTimePicker
//                                        .get(Calendar.MINUTE))
//                                        + " "
//                                        + (mDateTimePicker.get(Calendar.AM_PM) == Calendar.AM ? "AM"
//                                        : "PM"));
//                            }
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

//                            txtEndDate.setText((mDateTimePicker
//                                    .get(Calendar.DAY_OF_MONTH))
//                                    + " "
//                                    + formatMonth.format(mDateTimePicker
//                                    .getCurrentDate())
//                                    + " "
//                                    + mDateTimePicker.get(Calendar.YEAR));
//
//
//                            if (mDateTimePicker.is24HourView()) {
//                                txtEndTime.setText(String.format("%02d",
//                                        mDateTimePicker
//                                                .get(Calendar.HOUR_OF_DAY))
//                                        + ":"
//                                        + String.format("%02d", mDateTimePicker
//                                        .get(Calendar.MINUTE)));
//                            } else {
//                                txtEndTime.setText(String.format("%02d",
//                                        mDateTimePicker.get(Calendar.HOUR))
//                                        + ":"
//                                        + String.format("%02d", mDateTimePicker
//                                        .get(Calendar.MINUTE))
//                                        + " "
//                                        + (mDateTimePicker.get(Calendar.AM_PM) == Calendar.AM ? "AM"
//                                        : "PM"));
//                            }
                        }
                        if (fromDate != null)
                            fromDateStr = sdf.format(fromDate);
                        if (toDate != null)
                            toDateStr = sdf.format(toDate);

                        Date fromD, toD;
                        try {
                            fromD = sdf.parse(fromDateStr);
                            toD = sdf.parse(toDateStr);

                            int comparedResult = toD.compareTo(fromD);
                            if (comparedResult == -1 || comparedResult == 0) {
                                txtEndDate.setText("");
                                txtEndTime.setText("");
                                endDateDB = null;
                                toD = null;
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.higher_to_date),
                                        Toast.LENGTH_LONG).show();
                            }

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

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
     * To get the system's date format.
     *
     * @param context
     * @return system date format in string
     */
    public static String getDateFormat(Context context) {
        // 25/12/2013
        Calendar testDate = Calendar.getInstance();
        testDate.set(Calendar.YEAR, 2013);
        testDate.set(Calendar.MONTH, Calendar.DECEMBER);
        testDate.set(Calendar.DAY_OF_MONTH, 25);

        Format format = android.text.format.DateFormat.getDateFormat(context);
        String testDateFormat = format.format(testDate.getTime());
        String[] parts = testDateFormat.split("/");
        StringBuilder sb = new StringBuilder();
        for (String s : parts) {
            if (s.equals("25")) {
                sb.append("dd/");
            }
            if (s.equals("12")) {
                sb.append("MM/");
            }
            if (s.equals("2013")) {
                sb.append("yyyy/");
            }
        }
        return sb.toString().substring(0, sb.toString().length() - 1);
    }

    public void setIssuePosition(int position) {
        pos = String.valueOf(position);
    }

    /**
     * Restrict the user to update the unavailability.<br></br> <br></br>
     * Restrict - if ID_GROUPE is null and ID_USER IS null (OR) if ID_GROUPE is not null and ID_USER IS  null<br></br> <br></br>
     * Allow - if ID_GROUPE is null and ID_USER IS not null
     */
    private void restrictUpdate() {
        if (idActivityUser.equalsIgnoreCase("0")) {
            actionBar.setTitle(getString(R.string.txt_activity));
            txtUpdate.setVisibility(View.GONE);
            linStartDate.setEnabled(false);
            linEndDate.setEnabled(false);
            edtIssueDescription.setEnabled(false);
        } else {
            actionBar.setTitle(getString(R.string.update_unavailability_title));
            txtUpdate.setVisibility(View.VISIBLE);
            linStartDate.setEnabled(true);
            linEndDate.setEnabled(true);
            edtIssueDescription.setEnabled(true);
        }
    }

    // **********************************LOCAL...METHOD...ENDS...HERE****************************************************

    // ...................................LISTENER...STARTS...HERE........................................................
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtCreate:
                // pos = issuesPositionPref.getString("pos", null);
                notesDescription = edtIssueDescription.getText().toString();

                if (TextUtils.isEmpty(fromDateDB) || TextUtils.isEmpty(endDateDB)) {
                    if (!TextUtils.isEmpty(toDateStr)) {
                        if (TextUtils.isEmpty(fromDateDB)) {
                            Toast.makeText(this,
                                    getString(R.string.enter_start_date),
                                    Toast.LENGTH_SHORT).show();
                        } else if (TextUtils.isEmpty(endDateDB)) {
                            Toast.makeText(this,
                                    getString(R.string.enter_end_date),
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (TextUtils.isEmpty(fromDateDB)) {
                            Toast.makeText(this,
                                    getString(R.string.enter_start_date),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            new UpdateUnavailabilityInDBAsync().execute();
                        }
                    }
                } else {
                    new UpdateUnavailabilityInDBAsync().execute();
                }
                break;
//            case R.id.linStartDate:
//                currentDate = new Date();
////                showDateTimeDialg(0);
//                isStartDateClicked = true;
//                showDatePicker();
//                break;
//            case R.id.linEndDate:
////                showDateTimeDialg(1);
//                isStartDateClicked = false;
//                showDatePicker();
//                break;
        }
    }

    // ...................................LISTENER...ENDS...HERE........................................................

    // .....................................ASYNC...CLASS...STARTS...HERE...............................................


    private class UpdateUnavailabilityInDBAsync extends
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

            DialogUtils.showProgressDialog(UpdateUnavailability.this,
                    getString(R.string.textWaitLable),
                    getString(R.string.textSavingAddJobData), false);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        public Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub

            boolean drp = dataAccessObject.updateUnavailability(
                    unavailabilityID,
                    modifiedUnavailList.get(Integer.parseInt(pos))
                            .getUnavailabilityID(), fromDateDB,
                    notesDescription, endDateDB);
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
                Toast.makeText(UpdateUnavailability.this,
                        getString(R.string.unavailability_added),
                        Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new UpdateDataBaseEvent());
                finish();
                /*
                 * change the status of the currently running job to
				 * pause/suspend it
				 */
//				if (toDateStr != null) {
//					Calendar cal = Calendar.getInstance();
//					String currentDateStr = sdf.format(cal.getTime());
//
//					try {
//						Date currentDate = sdf.parse(currentDateStr);
//						Date fromDate = sdf.parse(fromDateDB);
//						Date endDate = sdf.parse(endDateDB);
//
//						if (currentDate.compareTo(fromDate) > 0
//								&& currentDate.compareTo(endDate) < 0) {
//							if (dataAccessObject
//									.updateStatutInterv(4, idInterv))
//								dataAccessObject.setTimeClotIntervention(
//										idInterv, idActivityUser + "");
//						}
//					} catch (ParseException e) {
//						e.printStackTrace();
//					}
//				}
            } else
                Toast.makeText(UpdateUnavailability.this, R.string.msg_error,
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

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, mDateSetListener, mYear, mMonth, mDay);
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
        boolean is24h = DateFormat.is24HourFormat(this);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, mTimeSetListener, mHour, mMinute, is24h);
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

        boolean is24h = DateFormat.is24HourFormat(this);

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

//check if end date is higher than start date or not.
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

        boolean is24h = DateFormat.is24HourFormat(this);

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

        //check if end date is higher than start date or not.
        validateEndDate();
    }

    private void validateEndDate() {
        if (fromDate != null)
            fromDateStr = sdf.format(fromDate);
        if (toDate != null)
            toDateStr = sdf.format(toDate);

        Date fromD, toD;
        try {
            fromD = sdf.parse(fromDateStr);
            toD = sdf.parse(toDateStr);

            int comparedResult = toD.compareTo(fromD);
            if (comparedResult == -1 || comparedResult == 0) {
                txtEndDate.setText("");
                txtEndTime.setText("");
                endDateDB = null;
                Toast.makeText(getApplicationContext(),
                        getString(R.string.higher_to_date),
                        Toast.LENGTH_LONG).show();
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    //************************************NEW...DATE...TIME...PICKER***************************************************
}
