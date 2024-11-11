package com.synchroteam.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.synchroteam.beans.UnavailabilityBeans;
import com.synchroteam.beans.UpdateDataBaseEvent;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.listadapters.ActivityDialogListAdapter;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.tracking.DaoTracking;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DialogUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import de.greenrobot.event.EventBus;


public class StartActivityDialog extends DialogFragment implements View.OnClickListener, ActivityDialogListAdapter.SelectActivityListener {

    private static final String TAG = StartActivityDialog.class.getSimpleName();

    private Context context;
    private RelativeLayout relActivityType, relHeaderView, relFrom, relTo;
    private TextView txtTypeHint, txtFromHint, txtToHint, txtType, txtStartDate,
            txtEndDate, txtSave, txtBack;
    private TextView txtTitle;
    private ListView lvActivity;
    private EditText etDescription;
    private ArrayList<UnavailabilityBeans> unavailabilitiesList;
    private LinearLayout linStartActivity, layClose;
    private ProgressBar progressActivitiesList;
    private Dao dataAccessObject;
    private DaoTracking daoTracking;
    private Calendar mCalendar;
    private boolean isStartDateClicked;
    private String pos;
    private Date fromDate, toDate;
    private SimpleDateFormat sdf;
    private String startDate;
    private String endDate;
    private String fromDateDB, endDateDB;
    private String currentDateFormat = "yyyy-MM-dd HH:mm:ss";
    private boolean isActivityTypeList = false;
    private boolean timeTrackerEnabled = false;
    private CharSequence text;

    public static StartActivityDialog newInstance() {
        StartActivityDialog fragment = new StartActivityDialog();
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_start_activity, container);
        initializeUI(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
    }

    @Override
    public void onPause() {
        super.onPause();
        text = etDescription.getText()+" ";
        etDescription.setText(text);
    }


    private void initializeUI(View view) {
        dataAccessObject = DaoManager.getInstance();
        timeTrackerEnabled = isClockInAvailable();
        relActivityType = (RelativeLayout) view.findViewById(R.id.rel_activity_type);
        relHeaderView = (RelativeLayout) view.findViewById(R.id.rel_header_view);
        relFrom = (RelativeLayout) view.findViewById(R.id.rel_from);
        relTo = (RelativeLayout) view.findViewById(R.id.rel_to);
        txtTitle = (TextView) view.findViewById(R.id.txt_dialog_title);
        txtTypeHint = (TextView) view.findViewById(R.id.txt_type_hint);
        txtFromHint = (TextView) view.findViewById(R.id.txt_from_hint);
        txtType = (TextView) view.findViewById(R.id.txt_activity_type);
        txtStartDate = (TextView) view.findViewById(R.id.txt_start_date);
        txtEndDate = (TextView) view.findViewById(R.id.txt_end_date);
        txtToHint = (TextView) view.findViewById(R.id.txt_to_hint);
        layClose = (LinearLayout) view.findViewById(R.id.lay_close);
        lvActivity = (ListView) view.findViewById(R.id.lv_activity);
        txtSave = (TextView) view.findViewById(R.id.txt_save);
        txtBack = (TextView) view.findViewById(R.id.txt_back);
        etDescription = (EditText) view.findViewById(R.id.et_description);
        linStartActivity = (LinearLayout) view.findViewById(R.id.lin_start_activity);
        progressActivitiesList = (ProgressBar) view.findViewById(R.id.progress_activity_list);
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        etDescription.setHint(getString(R.string.textDescriptionLable) + " (" + getString(R.string.txt_optional) + ")");
        layClose.setOnClickListener(this);
        relActivityType.setOnClickListener(this);
        relFrom.setOnClickListener(this);
        relTo.setOnClickListener(this);
        txtSave.setOnClickListener(this);
        txtBack.setOnClickListener(this);

        //for enabling web link click
        etDescription.setMovementMethod(LinkMovementMethod.getInstance());
        etDescription.addTextChangedListener(edtDesTextWatcher);
    }

    /**
     * check if the time tracker is enabled or not
     *
     * @return
     */
    private boolean isClockInAvailable() {
        User user = dataAccessObject.getUser();
        Boolean isClockInAvailable = dataAccessObject.checkIsClockInAvailable(user.getId());
        return isClockInAvailable;
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
        dialogHeight = (int) (screenHeight * Double.parseDouble(this.getResources().getString(R.string.dialog_start_activity_height)));
        dialogWidth = (int) (screenWidth * Double.parseDouble(this.getResources().getString(R.string.clock_in_out_width)));
        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rel_activity_type:
                new GetUnavailabilitiesAsync().execute();
                break;
            case R.id.rel_from:
                isStartDateClicked = true;
                showDatePicker();
                break;
            case R.id.rel_to:
                isStartDateClicked = false;
                if (TextUtils.isEmpty(fromDateDB)) {
                    Toast.makeText(getActivity(),
                            getActivity().getString(R.string.enter_start_date),
                            Toast.LENGTH_SHORT).show();
                } else {
                    showDatePicker();
                }
                break;
            case R.id.txt_save:
                if (pos == null || TextUtils.isEmpty(txtStartDate.getText().toString().trim())
                        || TextUtils.isEmpty(txtEndDate.getText().toString().trim())) {
                    if (pos == null) {
                        Toast.makeText(getActivity(),
                                getActivity().getString(R.string.select_type),
                                Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(txtStartDate.getText().toString().trim())) {
                        Toast.makeText(getActivity(),
                                getActivity().getString(R.string.enter_start_date),
                                Toast.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(txtEndDate.getText().toString().trim())) {
                        Toast.makeText(getActivity(),
                                getActivity().getString(R.string.enter_end_date),
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    new addUnavailabilityInDBAsync().execute(etDescription.getText().toString());
                }
                break;
            case R.id.lay_close:
                if (!isActivityTypeList) {
                    dismiss();
                } else {
                    lvActivity.setVisibility(View.GONE);
                    linStartActivity.setVisibility(View.VISIBLE);
                    txtBack.setVisibility(View.GONE);
                    isActivityTypeList = false;
                    initialView();
                }
                break;
            case R.id.txt_back:
                lvActivity.setVisibility(View.GONE);
                linStartActivity.setVisibility(View.VISIBLE);
                txtBack.setVisibility(View.GONE);

                break;
        }
    }

    @Override
    public void setIssuePosition(int position) {
        pos = String.valueOf(position);
        txtBack.setVisibility(View.GONE);
        txtTypeHint.setVisibility(View.GONE);
        txtType.setText(unavailabilitiesList.get(position).getUnavailabilityName());
        txtType.setVisibility(View.VISIBLE);
        lvActivity.setVisibility(View.GONE);
        linStartActivity.setVisibility(View.VISIBLE);
        initialView();
    }

    private void initialView() {
        relHeaderView.setBackgroundColor(getResources().getColor(R.color.white));
        txtTitle.setTextColor(getResources().getColor(R.color.black));
        layClose.setBackgroundResource(R.drawable.bg_yellow_circle);
    }

    private void activityListTypeShown() {
        relHeaderView.setBackgroundColor(getResources().getColor(R.color.white));
        txtTitle.setTextColor(getResources().getColor(R.color.black));
        layClose.setBackgroundResource(R.drawable.bg_yellow_circle);
    }

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
        txtFromHint.setVisibility(View.GONE);
        txtStartDate.setVisibility(View.VISIBLE);
        txtStartDate.setSelected(true);

        fromDate = mCalendar.getTime();
        fromDateDB = sdf.format(fromDate);

        String startDateTime = null;

        try {
            startDateTime = dataAccessObject.getDateWithRequiredPresettedPattern(fromDateDB, currentDateFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        startDate = startDateTime.substring(0, startDateTime.indexOf("/"));

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
            txtStartDate.setText(startDate + " " + m24hTime);
        } else {
            String mNormalTime = String.format(Locale.US, "%02d",
                    mCalendar.get(Calendar.HOUR))
                    + ":"
                    + String.format(Locale.US, "%02d", mCalendar
                    .get(Calendar.MINUTE))
                    + " "
                    + (mCalendar.get(Calendar.AM_PM) == Calendar.AM ? "AM"
                    : "PM");
            txtStartDate.setText(startDate + " " + mNormalTime);
        }

        validateEndDate();

    }

    /**
     * Updates end date value
     */
    private void updateEndDate() {
        txtToHint.setVisibility(View.GONE);
        txtEndDate.setVisibility(View.VISIBLE);
        txtEndDate.setSelected(true);

        toDate = mCalendar.getTime();
        endDateDB = sdf.format(toDate);

        String endDateTime = null;

        try {
            endDateTime = dataAccessObject.getDateWithRequiredPresettedPattern(endDateDB, currentDateFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        endDate = endDateTime.substring(0, endDateTime.indexOf("/"));

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
            txtEndDate.setText(endDate + " " + m24hTime);
        } else {
            String mNormalTime = String.format(Locale.US, "%02d",
                    mCalendar.get(Calendar.HOUR))
                    + ":"
                    + String.format(Locale.US, "%02d", mCalendar
                    .get(Calendar.MINUTE))
                    + " "
                    + (mCalendar.get(Calendar.AM_PM) == Calendar.AM ? "AM"
                    : "PM");
            txtEndDate.setText(endDate + " " + mNormalTime);
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
                toDate = null;
                Toast.makeText(
                        getActivity(),
                        getActivity().getString(
                                R.string.higher_to_date),
                        Toast.LENGTH_LONG).show();
            }

        }
    }

    /**
     * Get list of unavailabilities
     */
    private class GetUnavailabilitiesAsync extends AsyncTaskCoroutine<Void, Void> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            linStartActivity.setVisibility(View.GONE);
            progressActivitiesList.setVisibility(View.VISIBLE);
        }

        @Override
        public Void doInBackground(Void... voids) {
            unavailabilitiesList = new ArrayList<>();
            if (timeTrackerEnabled) {
                unavailabilitiesList = dataAccessObject
                        .getUnavailabilities();
            }else{
                unavailabilitiesList = dataAccessObject
                        .getUnavailabilitiesWhenTimeTrackerDisabled();
            }

            return null;
        }

        @Override
        public void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            lvActivity.setAdapter(new ActivityDialogListAdapter(
                    getActivity(), unavailabilitiesList, null, StartActivityDialog.this));
//            txtBack.setVisibility(View.VISIBLE);
            isActivityTypeList = true;
            activityListTypeShown();
            progressActivitiesList.setVisibility(View.GONE);
            lvActivity.setVisibility(View.VISIBLE);
        }

    }

    /*
    *  The Class SaveNewJobDataAsyncTask.
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
                    .getString(R.string.chargement), false);
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
                    params[0]);

            Calendar cal = Calendar.getInstance();
            String currentDateStr = sdf.format(cal.getTime());
            // new clocked in entry to T_CONGE
            UnavailabilityBeans clockInActivity = dataAccessObject.getClockInActivity();


//            try {
//                Thread.sleep(600);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

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



            boolean drp = result.booleanValue();
            if (drp) {
                Toast.makeText(getActivity(),
                        getActivity().getString(R.string.unavailability_added),
                        Toast.LENGTH_SHORT).show();

//				checkAndStopCurrentJob();
                EventBus.getDefault().post(new UpdateDataBaseEvent());

                DialogUtils.dismissProgressDialog();
                StartActivityDialog.this.dismiss();

            } else {
                Toast.makeText(getActivity(), R.string.msg_error,
                        Toast.LENGTH_SHORT).show();
                DialogUtils.dismissProgressDialog();
            }
        }

    }


    /*text watcher for edit text to check if has any web links after typing certain word*/
    public TextWatcher edtDesTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            Linkify.addLinks(editable, Linkify.ALL);

            /**
             * for android 9 and above Linkify.All will not support phone number clickable for certain device
             * so the below code is to ensure the phone number is clickable
             */
            Linkify.addLinks(editable,
                    Patterns.PHONE,
                    "tel:",
                    Linkify.sPhoneNumberMatchFilter,
                    Linkify.sPhoneNumberTransformFilter
            );
        }
    };

}