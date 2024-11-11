package com.synchroteam.dialogs;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.AllJobsSorting;
import com.synchroteam.dao.Dao;
import com.synchroteam.fragmenthelper.CurrentJobsFragmentHelperNew;
import com.synchroteam.listadapters.SortingListAdapter;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.SharedPref;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.TimeZone;

//TODO need to retain the scrolled position in recycler view after notifydatasetChanged() called.
//TODO need to close the keyboard -  If the user is selected the near by job option, then he selects any other option keyboard is showing.
//TODO hide the divider for last item.

/**
 * Dialog fragment for showing sorting options in AllJobs class.<p></p>
 * Created by Trident on 5/20/2016.
 */
public class AllJobsSortingDialog extends DialogFragment {

    //--------------------------------INSTANCE...VARIABLES...STARTS--------------------------------------

    private ListView rvSortingOptions;
    private TextView txtConfirm;
    private TextView txtClose;
    private LinearLayoutManager mLinearLayoutManager;
    private SortingListAdapter mSortingRVAdapter;

    private int mSortingOption;
    private String mOptionName;
    private String mJobNumber;
    private String[] arrSortingOptions;
    private ArrayList<AllJobsSorting> listSortingOptions;
    private static Dao dataAccessObject;
    private boolean isCurrentJobs;
    private boolean isJobPool = false;
    private int mScrollY;

    private static final String KEY_JOB_SORTING = "job_sorting";
    private static final String KEY_SELECTED_DATE = "selected_date";
    private static final String KEY_IS_CURRENT_JOBS = "is_current_jobs";
    private static final String KEY_IS_JOB_POOL = "is_job_pool";

    private static final String TAG = AllJobsSortingDialog.class.getSimpleName();

    public static Fragment fragment;

    //--------------------------------INSTANCE...VARIABLES...STARTS--------------------------------------

    //---------------------------------PUBLIC...METHODS...STARTS----------------------------------------

    public static AllJobsSortingDialog getInstance(int mJobSorting, String selectedDate,
                                                   boolean isCurrentJob, boolean isJobPool, Fragment frag) {
        dataAccessObject = DaoManager.getInstance();
        fragment = frag;
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_JOB_SORTING, mJobSorting);
        bundle.putString(KEY_SELECTED_DATE, selectedDate);
        bundle.putBoolean(KEY_IS_CURRENT_JOBS, isCurrentJob);
        bundle.putBoolean(KEY_IS_JOB_POOL, isJobPool);

        AllJobsSortingDialog allJobsSortingDialog = new AllJobsSortingDialog();
        allJobsSortingDialog.setArguments(bundle);
        return allJobsSortingDialog;
    }

    //---------------------------------PUBBLIC...METHODS...ENDS------------------------------------------

    //--------------------------------LIFECYCLE...METHODS...STARTS--------------------------------------

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_all_jobs_sorting, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvSortingOptions = (ListView) view.findViewById(R.id.lv_sorting_options);
        txtConfirm = (TextView) view.findViewById(R.id.txtConfirm);
        txtClose = (TextView) view.findViewById(R.id.txtClose);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());

        txtConfirm.setOnClickListener(clickListener);
        txtClose.setOnClickListener(clickListener);

        setCancelable(false);

        getListData();

        setListAdapter();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams wmlp = dialog.getWindow()
                    .getAttributes();
            wmlp.gravity = Gravity.CENTER;
        }
        return dialog;
    }

    /**
     * sets the height of the dialog to 1/7th of the total screen height.
     */
    @Override
    public void onResume() {
        super.onResume();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenHeight = metrics.heightPixels;
        int dialogHeight = (int) (screenHeight * 0.7);
        if (getDialog().getWindow() != null)
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.WRAP_CONTENT, dialogHeight);
    }
    //--------------------------------LIFECYCLE...METHODS...STARTS--------------------------------------

    //--------------------------------LOCAL...METHODS...STARTS------------------------------------------

    /**
     * Add list of sorting options data to the array list.
     */
    private void getListData() {
        assert getArguments() != null;
        isCurrentJobs = getArguments().getBoolean(KEY_IS_CURRENT_JOBS);
        isJobPool = getArguments().getBoolean(KEY_IS_JOB_POOL);
        if (isCurrentJobs) {
            arrSortingOptions = new String[]{getString(R.string.txt_no_sorting), getString(R.string.txt_date_sorting),
                    getString(R.string.txt_customer_sorting),
                    getString(R.string.txt_site_sorting),
                    getString(R.string.txt_equipment_sorting),
                    getString(R.string.txt_nearby_sorting),
                    getString(R.string.txt_nearby_job_sorting),
                    getString(R.string.txt_town_sorting),
                    getString(R.string.txt_priority_sorting)};
        } else {
            if (isJobPool) {
                arrSortingOptions = new String[]{
                        getString(R.string.txt_date_sorting),
                        getString(R.string.txt_customer_sorting),
                        getString(R.string.txt_site_sorting),
                        getString(R.string.txt_equipment_sorting),
                        getString(R.string.txt_nearby_sorting),
                        getString(R.string.txt_nearby_job_sorting),
                        getString(R.string.txt_town_sorting),
                        getString(R.string.txt_priority_sorting),
                        getString(R.string.txt_no_sorting)};
            } else {
                arrSortingOptions = new String[]{getString(R.string.txt_date_sorting),
                        getString(R.string.txt_customer_sorting),
                        getString(R.string.txt_site_sorting),
                        getString(R.string.txt_equipment_sorting),
                        getString(R.string.txt_nearby_sorting),
                        getString(R.string.txt_nearby_job_sorting),
                        getString(R.string.txt_town_sorting),
                        getString(R.string.txt_priority_sorting)};
            }
        }


        listSortingOptions = new ArrayList<>();
        for (int i = 0; i < arrSortingOptions.length; i++) {
            AllJobsSorting allJobsSorting = new AllJobsSorting();
            allJobsSorting.setSortingName(arrSortingOptions[i]);
            int sortOption = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                assert getActivity() != null;
                sortOption = SharedPref.getSortingOption(getActivity());
            }
//            if (isJobPool) {
//                if (sortOption == 8) {
//                    sortOption = 0;
//                } else if (sortOption == 0) {
//                    sortOption = 8;
//                } else {
//                    sortOption += 1;
//                }
//            }
            if (i == sortOption) {
                allJobsSorting.setSelected(true);
            } else {
                allJobsSorting.setSelected(false);
            }
            listSortingOptions.add(allJobsSorting);
        }
    }

    /**
     * Sets the adapter.
     */
    private void setListAdapter() {
        mSortingRVAdapter = new SortingListAdapter(getActivity(), this, listSortingOptions, isCurrentJobs);
        rvSortingOptions.setAdapter(mSortingRVAdapter);
    }

    /**
     * Sends selected sorting option and job number (only if "Sort Nearby job" selected) to All job fragment.
     */
    private void sendSortingOption() {
        SortingListener listener = (SortingListener) fragment;


        if (listener == null)
            listener = (SortingListener) getTargetFragment();

        if (mSortingOption == KEYS.CurrentJobsSorting.SORT_BY_DATE) {
            CurrentJobsFragmentHelperNew.isUserClickedSortByDate = 1;
        } else if (mSortingOption == KEYS.CurrentJobsSorting.SORT_BY_NONE) {
            CurrentJobsFragmentHelperNew.isUserClickedSortByDate = 0;
        } else {
            CurrentJobsFragmentHelperNew.isUserClickedSortByDate = 2;
        }

        if ((mSortingOption == KEYS.AllJobSortingOptions.SORT_BY_NEARBY_JOB && !isCurrentJobs) ||
                (mSortingOption == KEYS.CurrentJobsSorting.SORT_BY_NEARBY_JOB && isCurrentJobs)) {
            if (!TextUtils.isEmpty(mJobNumber)) {
                if (checkJobNumber(mJobNumber)) {
                    assert listener != null;
                    listener.onSortingChose(mSortingOption, mOptionName, mJobNumber);
                    dismiss();
                } else {
                    Toast.makeText(getContext(), R.string.txt_jobno_mismatch, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getContext(), R.string.alert_job_number, Toast.LENGTH_SHORT).show();
            }
        } else {
            assert listener != null;
            listener.onSortingChose(mSortingOption, mOptionName, mJobNumber);
            dismiss();
        }
    }

    /**
     * set sorting values from adapter.
     *
     * @param option    : selected option
     * @param name      : name of sorting
     * @param jobNumber : job number (for near by job option)
     */
    public void setSortingValues(int option, String name, String jobNumber) {
//        if (isJobPool) {
//            if (option == 0) {
//                mSortingOption = 8;
//            } else {
//                mSortingOption = option - 1;
//            }
//        } else {
//            mSortingOption = option;
//        }

        mSortingOption = option;
        mOptionName = name;
        mJobNumber = jobNumber;
    }

    /**
     * Checks if the job number is valid or not.
     *
     * @param jobNumber : job number
     * @return true if exists
     */
    private boolean checkJobNumber(String jobNumber) {
        assert getArguments() != null;
        int mJobSorting = getArguments().getInt(KEY_JOB_SORTING);
        switch (mJobSorting) {
            case KEYS.AllJobSortingOptions.ALL_JOBS_SORTING:
                HashMap<String, String> latLongAllJobs = dataAccessObject.getLatLongAllJobs(jobNumber);
                if (latLongAllJobs.size() > 0) {
                    return true;
                } else {
                    return false;
                }
            case KEYS.AllJobSortingOptions.LATE_JOBS_SORTING:
                HashMap<String, String> latLongLateJobs = dataAccessObject.getLatLongLateJobs(jobNumber, dataAccessObject.getUser().getId());
                if (latLongLateJobs.size() > 0) {
                    return true;
                } else {
                    return false;
                }
            case KEYS.AllJobSortingOptions.UPCOMING_JOBS_SORTING:
                HashMap<String, String> latLongUpcomingJobs = dataAccessObject.getLatLongUpcomingJobs(jobNumber);
                if (latLongUpcomingJobs.size() > 0) {
                    return true;
                } else {
                    return false;
                }
            case KEYS.AllJobSortingOptions.REPORT_SORTING:
                String selectedDate = getArguments().getString(KEY_SELECTED_DATE);
                HashMap<String, String> latLongCompletedJobs = dataAccessObject.getLatLongCompletedJobs(jobNumber, selectedDate);
                if (latLongCompletedJobs.size() > 0) {
                    return true;
                } else {
                    return false;
                }
            case KEYS.AllJobSortingOptions.REPORT_SORTING_WITH_DATE:
                String reportSelectedDate = getArguments().getString(KEY_SELECTED_DATE);
                HashMap<String, String> latLongCompletedJobsWithDate = dataAccessObject.getLatLongCompletedJobsByDate(jobNumber, reportSelectedDate);
                if (latLongCompletedJobsWithDate.size() > 0) {
                    return true;
                } else {
                    return false;
                }
            case KEYS.AllJobSortingOptions.CURRENT_JOBS_SORTING:
                Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
                Date todayDate = calendar.getTime();
                HashMap<String, String> latLongCurrentJobs = dataAccessObject.checkLatLongCurrentJobs(todayDate, dataAccessObject.getUser().getId(), jobNumber);
                if (latLongCurrentJobs.size() > 0) {
                    return true;
                } else {
                    return false;
                }

            default:
                HashMap<String, String> hmLatLong = dataAccessObject.getLatLongAllJobs(jobNumber);
                if (hmLatLong.size() > 0) {
                    return true;
                } else {
                    return false;
                }
        }
    }

    //--------------------------------LOCAL...METHODS...STARTS------------------------------------------

    //------------------------------------LISTENER...METHODS...STARTS-----------------------------------

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.txtConfirm:
                    sendSortingOption();
                    break;
                case R.id.txtClose:
                    dismiss();
                    break;
            }
        }
    };

    //------------------------------------LISTENER...METHODS...ENDS-------------------------------------

    //--------------------------------INTERFACE...STARTS------------------------------------------------

    public interface SortingListener {
        void onSortingChose(int options, String optionName, String jobNumber);
    }
    //--------------------------------INTERFACE...ENDS--------------------------------------------------


}