package com.synchroteam.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.synchroteam.beans.CommonListBean;
import com.synchroteam.beans.CurrentJobDataBean;
import com.synchroteam.beans.JobDialogTypeListModel;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.listadapters.JobDialogListAdapter;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.Commons;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.KEYS;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import static android.content.Context.LOCATION_SERVICE;


public class JobListDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = ClockJobTravelActDialog.class.getSimpleName();

    Context context;

    private LinearLayout layClose;
    private LinearLayout linProgress;
    private ImageView imgBack;
    private TextView txtActivity;
    private ListView listActivity;
    private ArrayList<JobDialogTypeListModel> jobTypeList;
    JobDialogTypeListModel typeListModel;
    private Dao dataAccessObject;
    protected LocationManager locationManager;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private int type;
    private static final String TYPE = "type";

    String deadlineExceededCount, toComeCount;
    User user;
    private Date dateSelected;
    /**
     * The current date.
     */
    private Date todayDate;


    public static JobListDialog newInstance(int type) {
        JobListDialog fragment = new JobListDialog();
        Bundle args = new Bundle();
        args.putInt(TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_activity_list, container);

        initializeUI(view);
        getJobTypeCount();
        setActivityListAdapter();

        return view;
    }

    private void getJobTypeCount() {
        dataAccessObject = DaoManager.getInstance();
        user = dataAccessObject.getUser();

        jobTypeList = new ArrayList<>();

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        todayDate = calendar.getTime();

        dateSelected = todayDate;


        deadlineExceededCount = "" + dataAccessObject.getDedlineExceededJobsCountExcludingAuxUser(user.getId());
        toComeCount = dataAccessObject.getToComeJobCountExcludingAuxUser(user.getId());

        ArrayList<CommonListBean> currentJobBeanVector = new ArrayList<>();

        CurrentJobDataBean currentJobDataBean = dataAccessObject
                .getCurrentJobsExcludingAuxUser(dateSelected, user.getId());
        currentJobBeanVector = currentJobDataBean.getCommonJobDataBean();
        int jobCount = currentJobDataBean.getCurrrntJobCount();

        if (jobCount > 0) {
            typeListModel = new JobDialogTypeListModel();
            typeListModel.setJobType(getString(R.string.textCurrentJobLable));
            typeListModel.setJobTypeName(KEYS.JobTypeDialog.KEY_TODAY_JOB);
            if (jobCount == 1) {
                typeListModel.setHasMultipleJobs(false);
            } else {
                typeListModel.setHasMultipleJobs(true);
            }
            jobTypeList.add(typeListModel);
        }

        if (Integer.parseInt(toComeCount) > 0) {
            typeListModel = new JobDialogTypeListModel();
            typeListModel.setJobType(getString(R.string.textToComeLable) + " " + getString(R.string
                    .interventions));
            typeListModel.setJobTypeName(KEYS.JobTypeDialog.KEY_UPCOMING_JOB);
            if (Integer.parseInt(toComeCount) == 1) {
                typeListModel.setHasMultipleJobs(false);
            } else {
                typeListModel.setHasMultipleJobs(true);
            }
            jobTypeList.add(typeListModel);
        }

        if (Integer.parseInt(deadlineExceededCount) > 0) {
            typeListModel = new JobDialogTypeListModel();
            typeListModel.setJobTypeName(KEYS.JobTypeDialog.KEY_LATE_JOB);
            typeListModel.setJobType(getString(R.string.textDedlineExceededLable) + " " + getString
                    (R.string.interventions));
            if (Integer.parseInt(deadlineExceededCount) == 1) {
                typeListModel.setHasMultipleJobs(false);
            } else {
                typeListModel.setHasMultipleJobs(true);
            }
            jobTypeList.add(typeListModel);
        }

        linProgress.setVisibility(View.GONE);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        checkNetworkAndGPS();
        type = getArguments().getInt(TYPE);
        switch (type) {
            case Commons.IS_JOB:
                txtActivity.setText(getString(R.string.interventions));
                break;
        }
    }

    private void checkNetworkAndGPS() {
        locationManager = (LocationManager) getActivity()
                .getSystemService(LOCATION_SERVICE);

        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        isNetworkEnabled = Build.FINGERPRINT.contains("generic") ? true : locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    }

    private void setActivityListAdapter() {
        if (jobTypeList != null && jobTypeList.size() > 0 && getActivity() != null)
            listActivity.setAdapter(new JobDialogListAdapter(getActivity(), jobTypeList, this));
        else {
            if (context != null)
                Toast.makeText(context, context.getResources().getString(R.string.no_jobs_were_found), Toast.LENGTH_SHORT).show();
        }
    }


    private void initializeUI(View view) {


        imgBack = (ImageView) view.findViewById(R.id.txt_back);
        txtActivity = (TextView) view.findViewById(R.id.txt_activity);
        listActivity = (ListView) view.findViewById(R.id.list_activity);
        layClose = (LinearLayout) view.findViewById(R.id.lay_close);
        linProgress = (LinearLayout) view.findViewById(R.id.linProgress);

        linProgress.setVisibility(View.VISIBLE);

        imgBack.setOnClickListener(this);
        layClose.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
            case R.id.txt_back:
                ClockJobTravelActDialog.newInstance(false).show(getActivity().getSupportFragmentManager(), "");
                dismiss();
                break;
            case R.id.lay_close:
                ClockJobTravelActDialog.newInstance(false).show(getActivity().getSupportFragmentManager(), "");
                dismiss();
                break;
        }
    }
}