package com.synchroteam.synchroteam;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TypefaceSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Chronometer;
import android.widget.Chronometer.OnChronometerTickListener;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TimePicker;
import android.widget.Toast;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.Conge;
import com.synchroteam.beans.Families;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.beans.Item;
import com.synchroteam.beans.UpdateDataBaseEvent;
import com.synchroteam.beans.UpdateJobDetailUi;
import com.synchroteam.beans.UpdateUiAfterSync;
import com.synchroteam.beans.UpdateUiOnSync;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.AppUpdateDialog;
import com.synchroteam.dialogs.AuthenticationErrorDialog;
import com.synchroteam.dialogs.ErrorDialog;
import com.synchroteam.dialogs.JobPauseFinishDialog;
import com.synchroteam.dialogs.JobStartResumeDialog;
import com.synchroteam.dialogs.MultipleDeviceNotSupported;
import com.synchroteam.dialogs.StartJobActivityDialog;
import com.synchroteam.dialogs.SynchronizationErrorDialog;
import com.synchroteam.fragment.DiscrptionJobPoolFragment;
import com.synchroteam.observers.ObservableObject;
import com.synchroteam.retrofit.ApiClientNew;
import com.synchroteam.retrofit.ApiInterface;
import com.synchroteam.retrofit.models.JobPoolService.JobPoolRequest;
import com.synchroteam.retrofit.models.JobPoolService.JobPoolResult;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DateChecker;
import com.synchroteam.utils.DateFormatUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.SharedPref;
import com.synchroteam.utils.SynchroteamUitls;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import de.greenrobot.event.EventBus;
//import io.realm.Realm;
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
public class JobPoolDetails extends AppCompatActivity implements CommonInterface, Observer {

    /**
     * The action bar.
     */
    private ActionBar actionBar;

    /**
     * The job details view pager.
     */
    private ViewPager jobDetailsViewPager;


    /**
     * The data access object.
     */
    private Dao dataAccessObject;

    /**
     * The id intervention.
     */
    private String idIntervention;

    /**
     * The id user.
     */
    private int idUser;

    /**
     * The progress d synch.
     */
    private ProgressDialog progressDSynch;

    /**
     * The id model.
     */
    private String idModel;


    /**
     * The is format24.
     */
    private boolean isFormat24;
    /**
     * The status.
     */
    private int status;


    /**
     * The previous time.
     */
    private long previousTime;

    /**
     * The nom site.
     */
    private String nomSite;

    /**
     * The nom equipement.
     */
    private String nomEquipement;

    /**
     * The id site.
     */
    private int idSite;

    /**
     * The id client.
     */
    private int idClient;

    /**
     * The id equipement.
     */
    private int idEquipement;

    /**
     * The close activity.
     */
    private boolean closeActivity = false;

    /**
     * The factory.
     */
    private LayoutInflater factory;

    /**
     * The alert dialog view1.
     */
    private View alertDialogView1;

    /**
     * The m min2.
     */
    private int mJour, mMois, mAnnee, mH1, mH2, mMin1, mMin2;

    /**
     * The alert dialog view2.
     */
    private View alertDialogView4, alertDialogView2;


    /**
     * The menu.
     */
    private Menu menu;

    /**
     * The content.
     */
    private String[] CONTENT = null;

    /**
     * The gestion acces.
     */
    private GestionAcces gestionAcces;

    /**
     * The adr_global.
     */
    private String lat, lon, adr_global;

    /**
     * The duree.
     */
    private Date duree;

    /**
     * Theindicator user.
     */
    private User user;


    private long mStartBtnClickTime;

    private String dtCreated, dtPref, idJobWindow, dtMeeting;


    public boolean isTextDialogOpened;

    private static final String TAG = JobPoolDetails.class.getSimpleName();

    private boolean isActive;

 //   private Realm realm;

    boolean isStartJob = false;

    private int flCreateUpdateInvoiceQuotation;
    int countPager = 3;

    /*
     * (non-Javadoc)
     *
     * @see android.support.v7.app.AppCompatActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        factory = LayoutInflater.from(getApplicationContext());

        setContentView(R.layout.layout_job__pool_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

       // realm = Realm.getDefaultInstance();

        dataAccessObject = DaoManager.getInstance();

        getRequiredData();

        flCreateUpdateInvoiceQuotation = gestionAcces
                .getFlCreateUpdateInvoiceQuotation();

        String title = getIntent().getStringExtra(KEYS.CurrentJobs.TYPE);
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

        jobDetailsViewPager = (ViewPager) findViewById(R.id.pagerJobDetails);

        isFormat24 = android.text.format.DateFormat.is24HourFormat(this);

        countPager = 1;

        jobDetailsViewPager.setAdapter(new JobDetailsPagerAdapter(
                getSupportFragmentManager(), countPager));
        jobDetailsViewPager.setOnPageChangeListener(simpleOnPageChangeListener);

        // google doc item 149
        int idStatus = dataAccessObject.getStatus(idIntervention);


        if (idStatus != -1) {
            status = idStatus;
        }

    }


    public boolean isLGDevice() {
        return (Build.MANUFACTURER.contains("LG") || Build.MODEL.contains("LG"));
    }

    /**
     * Gets the required data.
     *
     * @return the required data
     */
    private void getRequiredData() {
        Bundle bundle = this.getIntent().getExtras();

        idUser = Integer.parseInt(bundle.getString("idUser"));
        idIntervention = bundle.getString("id");
        status = bundle.getInt(KEYS.CurrentJobs.CD_STATUS);
        nomSite = bundle.getString(KEYS.JObDetail.NOMSITE);
        nomEquipement = bundle.getString(KEYS.JObDetail.NOMEQUIPMENT);

        idSite = bundle.getInt(KEYS.JObDetail.IDSITE);
        idClient = bundle.getInt(KEYS.JObDetail.IDCLIENT);
        idEquipement = bundle.getInt("idEquipement");
        idModel = bundle.getString(KEYS.CurrentJobs.ID_MODEL);
        gestionAcces = dataAccessObject.getAcces();
        lat = bundle.getString(KEYS.JObDetail.LAT);
        lon = bundle.getString(KEYS.JObDetail.LON);
        adr_global = bundle.getString(KEYS.JObDetail.ADR_GLOBAL);

        dtCreated = bundle.getString(KEYS.JObDetail.DT_CREATED);
        dtMeeting = bundle.getString(KEYS.CurrentJobs.DATEMEETING);
        dtPref = bundle.getString(KEYS.CurrentJobs.DATE_PREF);
        idJobWindow = bundle.getString(KEYS.CurrentJobs.ID_JOB_WINDOW);

        duree = dataAccessObject.getJobDuration(idIntervention);

        user = dataAccessObject.getUser();

        isStartJob = bundle.getBoolean(KEYS.CurrentJobs.IDSTARTJOB, false);

        flCreateUpdateInvoiceQuotation = gestionAcces.getFlCreateUpdateInvoiceQuotation();

        // int count = dataAccessObject.getCustomFields(idIntervention,
        // idClient,
        // idSite, idEquipement).size();

        boolean haveCF;
        if (dataAccessObject.getCFForIntervention(idIntervention).size() != 0
                || dataAccessObject.getCFForClient(idClient).size() != 0
                || dataAccessObject.getCFForSite(idSite).size() != 0
                || dataAccessObject.getCFForEquip(idEquipement).size() != 0) {
            haveCF = true;
        } else {
            haveCF = false;
        }


        if (user != null) {
            if (!((idUser + "").equals(user.getId() + ""))) {

                status = KEYS.CurrentJobs.JOB__COMPLETE;
            } else {
                if (status == KEYS.JObDetail.JOB__STARTED) {


                    String dateString = dataAccessObject
                            .getJobResumedTimeInJobDetails(idIntervention);
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss");
                    try {
                        long timeWhenJobWasStarted = dateFormat.parse(dateString)
                                .getTime();
                        long previousTimeToBeSubtracted = dataAccessObject
                                .suspendedTimeDiffrence(idIntervention);

                        startTimer(timeWhenJobWasStarted
                                - previousTimeToBeSubtracted);

                    } catch (ParseException e) {
                        Logger.printException(e);
                    }
                }
                if (status == KEYS.JObDetail.JOB__SUSPENDED) {

                    previousTime = dataAccessObject
                            .suspendedTimeDiffrence(idIntervention);

                }

                if ((status == KEYS.JObDetail.JOB__COMPLETE)
                        || (status == KEYS.JObDetail.DEDLINE_EXCEEDED)) {

                }
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if (!isTextDialogOpened) {
        switch (item.getItemId()) {


            case R.id.action_schedule:

                if (SynchroteamUitls.isNetworkAvailable(JobPoolDetails.this)) {

                    try {
                        hitServiceForReserve(idIntervention, user.getId());
                    } catch (Exception e) {

                    }
                }

//                navigateToScheduleActivity();

                break;

            case android.R.id.home:

                finish();
                break;


            default:
                break;
        }

        return true;
    }

    private void navigateToScheduleActivity() {
        Bundle bundle = new Bundle();
        bundle.putString(KEYS.CurrentJobs.DATEMEETING, dtMeeting);
        bundle.putString(KEYS.CurrentJobs.DATE_PREF, dtPref);
        bundle.putString(KEYS.CurrentJobs.ID_JOB_WINDOW, idJobWindow);
        bundle.putString(KEYS.CurrentJobs.ID, idIntervention);
        //todo hitService and navigate

        Intent jobDetailIntent = new Intent(JobPoolDetails.this,
                JobPoolSchedule.class);
        jobDetailIntent.putExtras(bundle);
        startActivity(jobDetailIntent);
    }

    private void hitServiceForReserve(String idIntervention, int id) throws Exception{

        //show progress
        if (progressDSynch == null && !isFinishing())
            progressDSynch = ProgressDialog.show(JobPoolDetails.this,
                    getString(R.string.textPleaseWaitLable),
                    getString(R.string.chargement), true, false);
        else if (progressDSynch != null && !progressDSynch.isShowing())
            progressDSynch.show();

        String authUserName = dataAccessObject.getUserDomain() + "_" + user.getLogin();
        String authPassword = user.getPwd();
        ApiInterface apiService = ApiClientNew.createService(ApiInterface.class, authUserName, authPassword);

        String url = SharedPref.getJobPoolUrlServer(JobPoolDetails.this) + "/reserve";


        JobPoolRequest jobPoolRequest = new JobPoolRequest(id, idIntervention);
        Logger.log("TAG", "JobPoolRequest jobPoolRequest--->" + jobPoolRequest.toString());

        Call<JobPoolResult> call = apiService.scheduleReserveService(url, jobPoolRequest);
        call.enqueue(new Callback<JobPoolResult>() {
            @Override
            public void onResponse(Call<JobPoolResult> call, Response<JobPoolResult> response) {


                if (response.isSuccessful()) {
                    Logger.log("JobPoolResult", "JobPoolResult success :" + response.body());
                    int status = response.body().getResult();

                    progressDSynch.dismiss();
                    if (status == 1) {
                        navigateToScheduleActivity();
                        Logger.log("JobPoolResult", "JobPoolResult success :" + status);
                    } else {
                        Logger.log("JobPoolResult", "JobPoolResult failure :" + status);

                        showErrMsgDialog(getString(R.string.msg_reserve_job_pool_error));
                    }
                } else {
                    Logger.log("JobPoolResult", "JobPoolResult failure :" + response);
                    progressDSynch.dismiss();
                    showErrorDialog(getString(R.string.msg_error));
                }
            }

            @Override
            public void onFailure(Call<JobPoolResult> call, Throwable t) {
                Logger.log("JobPoolResult", "JobPoolResult Exception :" + t);
                showErrorDialog(getString(R.string.msg_error));
            }

        });

    }

    private void showErrorDialog(String string) {

        ErrorDialog errDialog = new ErrorDialog(JobPoolDetails.this, string);

        errDialog.show();
    }

    /**
     * Show error dialog
     */
    protected void showErrMsgDialog(String errMsg) {

        ErrorDialog errDialog = new ErrorDialog(JobPoolDetails.this, errMsg);

        errDialog.show();
    }

    public int getStatus() {
        return status;
    }


    /**
     * Show current timer.
     *
     * @param previousTime the previous time
     */
    private void showCurrentTimer(long previousTime) {

        int seconds = (int) (previousTime / 1000);

        // Date date=new Date(timeElapsed);

//        chronometer.setVisibility(View.VISIBLE);
//        startJobIb.setVisibility(View.GONE);
        try {

            int hours = seconds / 3600;
            int minutes = (seconds / 60) - (hours * 60);
            seconds = seconds - (hours * 3600) - (minutes * 60);
            String minutesString = null;
            String hoursString = null;
            if (minutes < 10) {
                minutesString = "0" + minutes;
            } else {
                minutesString = minutes + "";
            }
            if (hours < 10) {
                hoursString = "0" + hours;
            } else {
                hoursString = hours + "";
            }

//            chronometer.setText(hoursString + "h : " + minutesString + "m");

        } catch (Exception e) {
            Logger.printException(e);

        }

    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.job_pool_discription_menu, menu);

        this.menu = menu;

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * The simple on page change listener.
     */
    private ViewPager.SimpleOnPageChangeListener
            simpleOnPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        public void onPageSelected(int position) {

            actionBar.setSelectedNavigationItem(position);

        }

    };

    /**
     * The Class JobDetailsPagerAdapter.
     */
    private class JobDetailsPagerAdapter extends FragmentStatePagerAdapter {

        int count;

        /**
         * Instantiates a new job details pager adapter.
         *
         * @param fm the fm
         */
        public JobDetailsPagerAdapter(FragmentManager fm, int count) {
            super(fm);
            this.count = count;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
         */
        @Override
        public Fragment getItem(int arg0) {
            Fragment currentFragment = null;
            if (arg0 == 0) {
                Bundle bundle = new Bundle();
                bundle.putString(KEYS.JObDetail.ID, idIntervention);

                bundle.putString(KEYS.JObDetail.NOMSITE, nomSite);
                bundle.putString(KEYS.JObDetail.NOMEQUIPMENT, nomEquipement);
                bundle.putString(KEYS.JObDetail.LAT, lat);
                bundle.putString(KEYS.JObDetail.LON, lon);
                bundle.putString(KEYS.JObDetail.ADR_GLOBAL, adr_global);
                bundle.putString(KEYS.JObDetail.ID_USER, idUser + "");
                bundle.putInt(KEYS.JObDetail.IDSITE, idSite);
                bundle.putInt("cd_statut", status);

                bundle.putString("id_interv", idIntervention);
                bundle.putInt("idClient", idClient);
                bundle.putInt("idSiteCus", idSite);
                bundle.putInt("idEquipement", idEquipement);
                bundle.putString(KEYS.CurrentJobs.DATE_PREF, dtPref);
                bundle.putString(KEYS.CurrentJobs.ID_JOB_WINDOW, idJobWindow);
                bundle.putString(KEYS.CurrentJobs.DATEMEETING, dtMeeting);


                currentFragment = new DiscrptionJobPoolFragment();
                currentFragment.setArguments(bundle);
            }

            return currentFragment;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.support.v4.view.PagerAdapter#getCount()
         */
        @Override
        public int getCount() {
            return count;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.support.v4.view.PagerAdapter#getPageTitle(int)
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length].toUpperCase();
        }

    }

    /**
     * The on click listener.
     */
    OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();


        }

    };

    public boolean checkInterventionAndGettingIdInterv() {
        int idUserTemp = dataAccessObject.getUser().getId();
        String idInterventionTemp = dataAccessObject.getStartedInterventionIDToPauseStop(idUserTemp);
        return idInterventionTemp != null;
    }

    /**
     * open the activity / job in dialogs according to the status
     */
    private void openActivityJobDialogs() {
        //preventing double click
        if (SystemClock.elapsedRealtime() - mStartBtnClickTime < 1000) {
            return;
        }
        mStartBtnClickTime = SystemClock.elapsedRealtime();

        //new logic

//        Conge unAvailabilityDetail = checkUnAvailabilityStarted();
//
//        if (unAvailabilityDetail != null) {
//
//            ActivityModeDialog.newInstance(unAvailabilityDetail.getNomTypeConge(), unAvailabilityDetail.getIdTypeConge(), unAvailabilityDetail.getIdConge(), true, unAvailabilityDetail.getDtDebut()).show(getSupportFragmentManager(), "");
//
//        }
//        else {

        Logger.log("status", " " + status);
        Logger.log(TAG, "job intervention id is :" + idIntervention);

        if (status == KEYS.JObDetail.JOB_NOT_STARTED1 || status == KEYS.JObDetail.JOB_NOT_STARTED2) {
            String dateString = dataAccessObject
                    .getJobResumedTimeInJobDetails(idIntervention);
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");

            long startJobTimer = 0;
            try {
                long timeWhenJobWasStarted = dateFormat.parse(dateString)
                        .getTime();
                long previousTimeToBeSubtracted = dataAccessObject
                        .suspendedTimeDiffrence(idIntervention);

                startJobTimer = timeWhenJobWasStarted
                        - previousTimeToBeSubtracted;
            } catch (Exception e) {
                Logger.printException(e);
            }
            JobStartResumeDialog.newInstance(KEYS.JobDetails.KEY_START_JOB, getJobNumber(), startJobTimer).show(getSupportFragmentManager(), "");
        }

        if (status == KEYS.JObDetail.JOB__STARTED) {

            String dateString = dataAccessObject
                    .getJobResumedTimeInJobDetails(idIntervention);
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");

            long startJobTimer = 0;
            try {
                long timeWhenJobWasStarted = dateFormat.parse(dateString)
                        .getTime();
                long previousTimeToBeSubtracted = dataAccessObject
                        .suspendedTimeDiffrence(idIntervention);

                startJobTimer = timeWhenJobWasStarted
                        - previousTimeToBeSubtracted;
            } catch (ParseException e) {
                Logger.printException(e);
            }

            JobPauseFinishDialog.newInstance(getJobNumber(), startJobTimer, isAllMandatoryFieldsAreFilled()).show(getSupportFragmentManager(), "");
        }

        if (status == KEYS.JObDetail.JOB__SUSPENDED) {

//                chronometer.setOnChronometerTickListener(null);
//                chronometer
//                        .setOnChronometerTickListener(onChronometerTickListenerSuspend);

            previousTime = dataAccessObject
                    .suspendedTimeDiffrence(idIntervention);

            showCurrentTimer(previousTime);

            JobStartResumeDialog.newInstance(KEYS.JobDetails.KEY_RESUME_JOB, getJobNumber(), previousTime).show(getSupportFragmentManager(), "");

        }
//        }

    }

    /**
     * open the activity / job / clock in dialogs according to the status
     */
    private void openActivityJobClockDialogs() {
        //preventing double click
        if (SystemClock.elapsedRealtime() - mStartBtnClickTime < 1000) {
            return;
        }
        mStartBtnClickTime = SystemClock.elapsedRealtime();

//        Conge unAvailabilityDetail = checkUnAvailabilityStarted();
//        if (unAvailabilityDetail != null) {
//
//            ActivityModeDialog.newInstance(unAvailabilityDetail.getNomTypeConge(), unAvailabilityDetail.getIdTypeConge(), unAvailabilityDetail.getIdConge(), true, unAvailabilityDetail.getDtDebut()).show(getSupportFragmentManager(), "");
//
//        } else

        Conge unAvailabilityDetail = checkUnAvailabilityStarted();

        //new changes 50
//        TravelActivity travelActivity = dataAccessObject.isDrivingStarted();

        if (unAvailabilityDetail != null || checkInterventionAndGettingIdInterv()) {

            Logger.log("status", " " + status);

            if (status == KEYS.JObDetail.JOB_NOT_STARTED1 || status == KEYS.JObDetail.JOB_NOT_STARTED2) {
                String dateString = dataAccessObject
                        .getJobResumedTimeInJobDetails(idIntervention);
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");

                long startJobTimer = 0;
                try {
                    long timeWhenJobWasStarted = dateFormat.parse(dateString)
                            .getTime();
                    long previousTimeToBeSubtracted = dataAccessObject
                            .suspendedTimeDiffrence(idIntervention);

                    startJobTimer = timeWhenJobWasStarted
                            - previousTimeToBeSubtracted;
                } catch (Exception e) {
                    Logger.printException(e);
                }
                JobStartResumeDialog.newInstance(KEYS.JobDetails.KEY_START_JOB, getJobNumber(), startJobTimer).show(getSupportFragmentManager(), "");
            }

            if (status == KEYS.JObDetail.JOB__STARTED) {

                String dateString = dataAccessObject
                        .getJobResumedTimeInJobDetails(idIntervention);
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss");

                long startJobTimer = 0;
                try {
                    long timeWhenJobWasStarted = dateFormat.parse(dateString)
                            .getTime();
                    long previousTimeToBeSubtracted = dataAccessObject
                            .suspendedTimeDiffrence(idIntervention);

                    startJobTimer = timeWhenJobWasStarted
                            - previousTimeToBeSubtracted;
                } catch (ParseException e) {
                    Logger.printException(e);
                }

                JobPauseFinishDialog.newInstance(getJobNumber(), startJobTimer, isAllMandatoryFieldsAreFilled()).show(getSupportFragmentManager(), "");
            }

            if (status == KEYS.JObDetail.JOB__SUSPENDED) {

//                chronometer.setOnChronometerTickListener(null);
//                chronometer
//                        .setOnChronometerTickListener(onChronometerTickListenerSuspend);

                previousTime = dataAccessObject
                        .suspendedTimeDiffrence(idIntervention);

                showCurrentTimer(previousTime);

                JobStartResumeDialog.newInstance(KEYS.JobDetails.KEY_RESUME_JOB, getJobNumber(), previousTime).show(getSupportFragmentManager(), "");

            }
        } else {
            StartJobActivityDialog.newInstance(true).show(getSupportFragmentManager(), "");
        }
    }

    /**
     * Checking Activity is started
     *
     * @return
     */
    public Conge checkUnAvailabilityStarted() {
        Conge indisp;
        Vector<Conge> vectConge = new Vector<Conge>();
        vectConge = dataAccessObject.getCongeNewExceptClockInActivity();
        Enumeration<Conge> enindisp = vectConge.elements();
        while (enindisp.hasMoreElements()) {
            indisp = enindisp.nextElement();
            if (indisp.getDtFin() == null) {
                return indisp;
            }
        }
        return null;
    }

    public void updateEndDateOfStartedActivities() {
        Vector<Conge> vectConge = new Vector<Conge>();
        vectConge = dataAccessObject.getCongeNewExceptClockInActivity();
        Enumeration<Conge> enindisp = vectConge.elements();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String currentDate = sdf.format(cal.getTime());
        while (enindisp.hasMoreElements()) {
            Conge indisp = enindisp.nextElement();
            if (indisp.getDtFin() == null) {
                dataAccessObject.updateUnavailabilityEndDate(
                        indisp.getIdConge(), currentDate);

                //todo new 50 changes
            }
        }
    }


    /**
     * Start job.
     *
     * @param isFirstTIme the is first t ime
     */
    @SuppressLint("SimpleDateFormat")
    protected void startJob(boolean isFirstTIme) {

        if (!isFirstTIme) {
            // Update DT_FIN_PREV when Resume the job for old line (for pause state)
            String dt_Fin = dataAccessObject.getDtFinForRestartJobAndStopJob(idIntervention);
            dataAccessObject.updateDtFinPrev(dt_Fin, idIntervention);
        }

        String id = dataAccessObject.getIdIntervStatus(idIntervention, idUser);

        if (id != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String s = sdf.format(System.currentTimeMillis());
            if (dataAccessObject.updateStatutInterv(4, id)) {
                dataAccessObject.setTimeClotInterv(s);
            }
        }

        long currentTime = 0;
        if (isFirstTIme) {
            if (dataAccessObject.updateStatutInterv(3, idIntervention)) {
                String tempId = dataAccessObject.getUniqueId();
                currentTime = dataAccessObject.setTimeStartInterv(idIntervention,
                        idUser, status,tempId);
                startTimer(currentTime);
            }
        } else {
            if (dataAccessObject.updateStatutInterv(3, idIntervention)) {
                currentTime = dataAccessObject.setTimeStartIntervResume(idIntervention,
                        idUser, status);
                resumeTimer();
            }
        }


//        menu.removeItem(R.id.action_job_more);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                menu.removeItem(R.id.action_reschedule);
                menu.removeItem(R.id.action_decline);
            }
        });


//        updateEndDateOfPreviousActivity();

//		updateEndDateOfScheduleddActivity();

        //todo new 50 changes

        //for stopping the previous started activities
        updateEndDateOfStartedActivities();

        startJobSynchonization(isFirstTIme);

    }

    private void startJobSynchonization(boolean isFirstTIme) {
        if (isFirstTIme) {

            if (dataAccessObject.checkSynchronisation(1) == 1) {
                synch();
            } else
                specialSynch();

        } else {

            if (dataAccessObject.checkSynchronisation(2) == 1) {
                synch();
            } else {
                specialSynch();
            }
        }


    }


    private void resumeTimer() {
        if (status == KEYS.JObDetail.JOB__STARTED) {
            String dateString = dataAccessObject
                    .getJobResumedTimeInJobDetails(idIntervention);
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            try {
                long timeWhenJobWasStarted = dateFormat.parse(dateString)
                        .getTime();
                long previousTimeToBeSubtracted = dataAccessObject
                        .suspendedTimeDiffrence(idIntervention);

                startTimer(timeWhenJobWasStarted
                        - previousTimeToBeSubtracted);
            } catch (ParseException e) {
                Logger.printException(e);
            }
        }
    }


    /**
     * Start timer.
     *
     * @param currentTime the current time
     */
    private void startTimer(long currentTime) {
//        chronometer.setBase(currentTime);
//        chronometer.setVisibility(View.VISIBLE);
//        startJobIb.setVisibility(View.GONE);
//        chronometer.start();

    }

    /**
     * Stop job.
     */
    protected void stopJob() {

        if (checkObligGlobale() == 1) {

            if (dataAccessObject.updateStatutInterv(5, idIntervention)) {
                dataAccessObject.setRealEndDate(idIntervention);
                dataAccessObject.setTimeClotInterventionForJobFinish(idIntervention, idUser + "");
            }
            status = KEYS.CurrentJobs.JOB__COMPLETE;
            // if (!this.isFinishing())
            // EventBus.getDefault().post(new UpdateDataBaseEvent());

//            dataAccessObject.testDtFinPrev(idIntervention);

            closeActivity = true;

            //TODO Remove after confirming the DT_FIN_PREV
//            dataAccessObject.forCheckingAllValues(idIntervention,idUser,true);

            if (dataAccessObject.checkSynchronisation(4) == 1) {

                synch();
            } else
                specialSynch();
        } else {
            Toast.makeText(JobPoolDetails.this, R.string.msg_saisie_oblig,
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Pause job.
     */
    protected void pauseJob() {
        if (status == KEYS.JObDetail.JOB__STARTED) {
            if (dataAccessObject.updateStatutInterv(4, idIntervention))
                dataAccessObject.setTimeClotIntervention(idIntervention, idUser + "", "");
            status = KEYS.CurrentJobs.JOB__SUSPENDED;
            // if (!this.isFinishing())
            // EventBus.getDefault().post(new UpdateDataBaseEvent());
            EventBus.getDefault().post(new UpdateJobDetailUi());
            closeActivity = true;
            if (dataAccessObject.checkSynchronisation(3) == 1)
                synch();
            else
                specialSynch();


//            pauseIb.setImageResource(R.drawable.play_btn);
//            stopIb.setVisibility(View.INVISIBLE);
//            stopTimer();

        } else if (status == KEYS.JObDetail.JOB__SUSPENDED) {
            status = KEYS.JObDetail.JOB__STARTED;
            EventBus.getDefault().post(new UpdateJobDetailUi());

            String dateString = dataAccessObject
                    .getJobResumedTimeInJobDetails(idIntervention);
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss");
            try {
                long timeWhenJobWasStarted = dateFormat.parse(dateString)
                        .getTime();
                long previousTimeToBeSubtracted = dataAccessObject
                        .suspendedTimeDiffrence(idIntervention);

                startTimer(timeWhenJobWasStarted - previousTimeToBeSubtracted);
            } catch (Exception e) {
                Logger.printException(e);
            }
            startJob(false);
//            pauseIb.setImageResource(R.drawable.ic_pause);
//            stopIb.setVisibility(View.VISIBLE);
        }

    }

    /**
     * Stop timer.
     */
    private void stopTimer() {
//        chronometer.stop();
    }

    /**
     * Synch.
     */
    public void synch() {

        if (SynchroteamUitls.isNetworkAvailable(JobPoolDetails.this)) {
            if (progressDSynch == null)
                progressDSynch = ProgressDialog.show(JobPoolDetails.this,
                        getString(R.string.textPleaseWaitLable),
                        getString(R.string.msg_synch), true, false);
            else if (progressDSynch != null && !progressDSynch.isShowing())
                progressDSynch.show();

            Logger.output(TAG, " thread started");

            Thread syncDbToServer = new Thread((new Runnable() {

                @Override
                public void run() {

                    Message myMessage = new Message();
                    try {
                        User u = dataAccessObject.getUser();
                        dataAccessObject.sync(u.getLogin(), u.getPwd());
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
            EventBus.getDefault().post(new UpdateDataBaseEvent());
            if (closeActivity) {
                finish();
            } else {
                SynchroteamUitls.showToastMessage(JobPoolDetails.this);
            }
        }
    }

    /**
     * The handler.
     */
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            String erreur = (String) msg.obj;
            EventBus.getDefault().post(new UpdateDataBaseEvent());

            if (erreur.equals("ok")) {
                Toast.makeText(JobPoolDetails.this,
                        getString(R.string.msg_synch_ok), Toast.LENGTH_LONG)
                        .show();
                if (closeActivity) {
                    finish();
                } else {
                    EventBus.getDefault().post(new UpdateUiAfterSync());
                }

            } else if (erreur.equals("4001") || erreur.equals("4000")) {
                showAuthErrDialog();
            } else if (erreur.equals("4006")) {
                Toast.makeText(JobPoolDetails.this,
                        getString(R.string.msg_synch_error_4),
                        Toast.LENGTH_LONG).show();
                if (closeActivity) {
                    finish();
                }

            } else if (erreur.equals("4101")) {
                showMultipleDeviecDialog();
            } else if (erreur.equals("4005")) {
                showAppUpdatetDialog();
            } else if (erreur.equals("4003")) {
                showErrMsgDialog(getString(R.string.msg_sync_error_4003));
            } else {
//                previous code
//                Toast.makeText(JobDetails.this,
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
     * For showing the synchronization failure messages
     */
    protected void showSyncFailureMsgDialog(String syncFailureMsg) {

        if (!isFinishing()) {
            SynchronizationErrorDialog synchronizationErrorDialog = new SynchronizationErrorDialog
                    (JobPoolDetails.this, syncFailureMsg, new SynchronizationErrorDialog
                            .SynchronizationErrorInterface() {
                        @Override
                        public void doOnOkayClick() {
                            //perform any action

                            if (closeActivity) {
                                finish();
                            }
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
                JobPoolDetails.this, user.getLogin());

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
                        } else {
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

    /**
     * Special synch.
     */
    public void specialSynch() {
        if (progressDSynch != null && progressDSynch.isShowing()) progressDSynch.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.ask_synch_msg)
                .setCancelable(false)
                .setPositiveButton(R.string.textYesLable,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                synch();
                            }
                        })
                .setNegativeButton(R.string.textNoLable,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();

                                EventBus.getDefault().post(
                                        new UpdateDataBaseEvent());
                                if (closeActivity) {
                                    JobPoolDetails.this.finish();
                                }

                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onResume()
     */
    protected void onResume() {
        super.onResume();


        //--------------------------------------- v48 -----------------------------------------------

        //New changes
        DateChecker.checkDateAndNavigate(this, dataAccessObject);

        ((SyncroTeamApplication) getApplicationContext())
                .setSyncroteamAppLive(true);
        ((SyncroTeamApplication) getApplicationContext())
                .setSyncroteamActivityInstance(this);

        // String deviceDateFormat=getDateFormat(this);
        // String deviceTimeFormat=getTimeFormat(this);

        String dateFormatString = android.provider.Settings.System.getString(
                this.getContentResolver(),
                android.provider.Settings.System.DATE_FORMAT);
        String timeFormatString = android.provider.Settings.System.getString(
                this.getContentResolver(),
                android.provider.Settings.System.TIME_12_24);

        if ((!TextUtils.isEmpty(dateFormatString))
                && (!TextUtils.isEmpty(timeFormatString))) {
            if (!dateFormatString.equals(SharedPref.getDateFormat(this))) {

                SharedPref.setDateFormat(this);
                SharedPref.setTimeFormat(this);
                EventBus.getDefault().post(new UpdateJobDetailUi());

            } else if (!timeFormatString.equals(SharedPref.getTimeFormat(this))) {

                SharedPref.setDateFormat(this);
                SharedPref.setTimeFormat(this);
                EventBus.getDefault().post(new UpdateJobDetailUi());

            }

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

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onPause()
     */
    @Override
    protected void onPause() {
        super.onPause();
        ((SyncroTeamApplication) getApplicationContext())
                .setSyncroteamAppLive(false);
        ((SyncroTeamApplication) getApplicationContext())
                .setSyncroteamActivityInstance(null);

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

    /**
     * The chronometer tick listener on start or restart.
     */
    OnChronometerTickListener chronometerTickListenerOnStartOrRestart = new OnChronometerTickListener() {

        @Override
        public void onChronometerTick(Chronometer chronometer) {

            long timeElapsed = System.currentTimeMillis()
                    - chronometer.getBase();
            int seconds = (int) (timeElapsed / 1000);

            // Date date=new Date(timeElapsed);

            try {

                int hours = seconds / 3600;
                int minutes = (seconds / 60) - (hours * 60);
                seconds = seconds - (hours * 3600) - (minutes * 60);
                String minutesString = null;
                String hoursString = null;
                if (minutes < 10) {
                    minutesString = "0" + minutes;
                } else {
                    minutesString = minutes + "";
                }
                if (hours < 10) {
                    hoursString = "0" + hours;
                } else {
                    hoursString = hours + "";
                }

                chronometer.setText(hoursString + "h : " + minutesString + "m");

            } catch (Exception e) {
                Logger.printException(e);

            }

        }
    };

    /**
     * The on chronometer tick listener suspend.
     */
    OnChronometerTickListener onChronometerTickListenerSuspend = new OnChronometerTickListener() {

        // private long timeElapsed;
        @Override
        public void onChronometerTick(Chronometer chronometer) {
            long timeElapsed;
            // if (isFirstCall) {
            // timeElapsed = previousTime;
            // isFirstCall = false;
            // } else {
            timeElapsed = (System.currentTimeMillis() - chronometer.getBase());
            // }

            int seconds = (int) (timeElapsed / 1000);

            // Date date=new Date(timeElapsed);

            try {

                int hours = seconds / 3600;
                int minutes = (seconds / 60) - (hours * 60);
                seconds = seconds - (hours * 3600) - (minutes * 60);
                String minutesString = null;
                String hoursString = null;
                if (minutes < 10) {
                    minutesString = "0" + minutes;
                } else {
                    minutesString = minutes + "";
                }
                if (hours < 10) {
                    hoursString = "0" + hours;
                } else {
                    hoursString = hours + "";
                }

                chronometer.setText(hoursString + "h : " + minutesString + "m");

            } catch (Exception e) {
                Logger.printException(e);

            }

        }
    };


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
        } catch (ParseException e) {
            Logger.printException(e);
            return new Date();
        }
    }

    /**
     * Show date picker.
     */
    @SuppressWarnings("deprecation")
    public void showDatePicker() {
        LayoutInflater factory = LayoutInflater.from(this);
        alertDialogView4 = factory.inflate(R.layout.show_date_picker, null);
        final DatePicker datePicker = (DatePicker) alertDialogView4
                .findViewById(R.id.start_date);
        android.widget.TextView txtFullDate = (android.widget.TextView) alertDialogView4
                .findViewById(R.id.txt_full_date);

        //Hide the full date view in picker, if it is above or from lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            txtFullDate.setVisibility(View.GONE);
        }

        restrictPreviousDateChoice(datePicker, txtFullDate);

        // Date date=getDateFromStrDate(et.getText().toString());
        Date date = new Date();
        datePicker.updateDate(date.getYear() + 1900, date.getMonth(),
                date.getDate());
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setView(alertDialogView4);
        adb.setTitle(R.string.textDateSmallLable);
        adb.setIcon(R.drawable.cal_icon);
        adb.setPositiveButton(getString(R.string.ok).toUpperCase(),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        mJour = datePicker.getDayOfMonth();
                        mMois = datePicker.getMonth();
                        mAnnee = datePicker.getYear() - 1900;
                        DateFormat df = DateFormat
                                .getDateInstance(DateFormat.LONG);
                        Date date = new Date(mAnnee, mMois, mJour);
                        TextView et = (TextView) alertDialogView1
                                .findViewById(R.id.repDate);
                        et.setText(df.format(date));

                    }
                });

        adb.setNegativeButton(R.string.textCancelLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        adb.show();
    }

    /**
     * Show time picker deb.
     */
    @SuppressWarnings("deprecation")
    public void showTimePickerDeb() {

        LayoutInflater factory = LayoutInflater.from(this);
        alertDialogView2 = factory.inflate(R.layout.show_time_picker, null);

        TimePicker timePicker = (TimePicker) alertDialogView2
                .findViewById(R.id.StartTime);
        timePicker.setOnTimeChangedListener(timeDebChangedListener);
        timePicker.setIs24HourView(isFormat24);
        TextView et = (TextView) alertDialogView1.findViewById(R.id.repHeurDeb);

        String hd = null;

        String startHourString = null;
        String startMinuteString = null;
        if (!TextUtils.isEmpty(et.getText().toString())) {
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

            hd = startHourString + ":" + startMinuteString;

        } else {
            hd = "";
        }

        if (!hd.equals("")) {
            Date date = getDateFromStrHour(hd);
            timePicker.setCurrentHour(date.getHours());
            timePicker.setCurrentMinute(date.getMinutes());
        }

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setView(alertDialogView2);
        adb.setTitle(R.string.textStartTimeLable);
        adb.setIcon(R.drawable.time_icon);
        adb.setPositiveButton(getString(R.string.ok).toUpperCase(),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        TimePicker timePicker = (TimePicker) alertDialogView2
                                .findViewById(R.id.StartTime);

                        mH1 = timePicker.getCurrentHour();
                        mMin1 = timePicker.getCurrentMinute();
                        Format format = android.text.format.DateFormat
                                .getTimeFormat(JobPoolDetails.this);
                        Date date = new Date(mAnnee, mMois, mJour, mH1, mMin1);
                        TextView et = (TextView) alertDialogView1
                                .findViewById(R.id.repHeurDeb);
                        et.setText(format.format(date));

                    }
                });

        adb.setNegativeButton(R.string.textCancelLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        adb.show();
    }

    /**
     * Show time picker fin.
     */
    @SuppressWarnings("deprecation")
    public void showTimePickerFin() {

        LayoutInflater factory = LayoutInflater.from(this);
        alertDialogView2 = factory.inflate(R.layout.show_time_picker, null);

        TimePicker timePicker = (TimePicker) alertDialogView2
                .findViewById(R.id.StartTime);
        timePicker.setOnTimeChangedListener(timeFinChangedListener);
        timePicker.setIs24HourView(isFormat24);
        TextView et = (TextView) alertDialogView1.findViewById(R.id.repHeurFin);

        String hd = null;
        String endHourString = null;
        String endMinuteString = null;
        if (!TextUtils.isEmpty(et.getText().toString())) {
            if (mH2 < 10) {
                endHourString = "0" + mH2;
            } else {
                endHourString = mH2 + "";
            }

            if (mMin2 < 10) {
                endMinuteString = "0" + mMin2;
            } else {
                endMinuteString = "" + mMin2;
            }

            hd = endHourString + ":" + endMinuteString;

        } else {
            hd = "";
        }

        if (!hd.equals("")) {
            Date date = getDateFromStrHour(hd);
            timePicker.setCurrentHour(date.getHours());
            timePicker.setCurrentMinute(date.getMinutes());
        }

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setView(alertDialogView2);

        adb.setTitle(R.string.textEndTimeLable);
        adb.setIcon(R.drawable.time_icon);
        adb.setPositiveButton(getString(R.string.ok).toUpperCase(),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        TimePicker timePicker = (TimePicker) alertDialogView2
                                .findViewById(R.id.StartTime);

                        mH2 = timePicker.getCurrentHour();
                        mMin2 = timePicker.getCurrentMinute();
                        Format format = android.text.format.DateFormat
                                .getTimeFormat(JobPoolDetails.this);
                        Date date = new Date(mAnnee, mMois, mJour, mH2, mMin2);
                        TextView et = (TextView) alertDialogView1
                                .findViewById(R.id.repHeurFin);
                        et.setText(format.format(date));

                    }
                });

        adb.setNegativeButton(R.string.textCancelLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        adb.show();
    }

    /**
     * Gets the db date.
     *
     * @param n the n
     * @return the db date
     */
    @SuppressWarnings("deprecation")
    public String getDbDate(int n) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String dbDate;
        if (n == 1) {
            Date date = new Date(mAnnee, mMois, mJour, mH1, mMin1);
            dbDate = sdf.format(date);
        } else {
            Date date = new Date(mAnnee, mMois, mJour, mH2, mMin2);
            dbDate = sdf.format(date);
        }
        return dbDate;
    }

    /**
     * Gets the date from str date.
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

    /**
     * Gets the date from str hour.
     *
     * @param mDate the m date
     * @return the date from str hour
     */
    public Date getDateFromStrHour(String mDate) {
        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
        Date date;
        try {
            date = displayFormat.parse(mDate);
            return date;
        } catch (ParseException e) {
            Logger.printException(e);
            return new Date();
        }
    }

    /**
     * The time fin changed listener.
     */
    private TimePicker.OnTimeChangedListener timeFinChangedListener = new TimePicker.OnTimeChangedListener() {

        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

            updateDisplayFin(view, hourOfDay, minute);
        }
    };

    /**
     * Update display fin.
     *
     * @param timePicker the time picker
     * @param hourOfDay  the hour of day
     * @param minute     the minute
     */
    @SuppressWarnings("deprecation")
    private void updateDisplayFin(TimePicker timePicker, int hourOfDay,
                                  int minute) {
        // start

        int nextMinute = 0;
        timePicker.setOnTimeChangedListener(mNullTimeChangedListener);

        if (minute >= 55 && minute <= 59)
            nextMinute = 55;
        else if (minute >= 50)
            nextMinute = 50;
        else if (minute >= 45)
            nextMinute = 45;
        else if (minute >= 40)
            nextMinute = 40;
        else if (minute >= 35)
            nextMinute = 35;
        else if (minute >= 30)
            nextMinute = 30;
        else if (minute >= 25)
            nextMinute = 25;
        else if (minute >= 20)
            nextMinute = 20;
        else if (minute >= 15)
            nextMinute = 15;
        else if (minute >= 10)
            nextMinute = 10;
        else if (minute >= 5)
            nextMinute = 05;
        else if (minute >= 0) {
            nextMinute = 0;
        } else {
            nextMinute = 0;
        }

        if ((minute - nextMinute) == 1) {
            if (minute >= 55 && minute <= 59)
                nextMinute = 00;
            else if (minute >= 50)
                nextMinute = 55;
            else if (minute >= 45)
                nextMinute = 50;

            else if (minute >= 40)
                nextMinute = 45;
            else if (minute >= 35)
                nextMinute = 40;
            else if (minute >= 30)
                nextMinute = 35;
            else if (minute >= 25)
                nextMinute = 30;
            else if (minute >= 20)
                nextMinute = 25;
            else if (minute >= 15)
                nextMinute = 20;
            else if (minute >= 10)
                nextMinute = 15;
            else if (minute >= 5)
                nextMinute = 10;
            else if (minute >= 0) {
                nextMinute = 5;
            } else {
                nextMinute = 5;
            }
        }

        TextView et = (TextView) alertDialogView1.findViewById(R.id.repHeurDeb);
        String hd = null;

        String startHourString = null;
        String startMinuteString = null;
        if (!TextUtils.isEmpty(et.getText().toString())) {
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

            hd = startHourString + ":" + startMinuteString;

        } else {
            hd = "";
        }
        if (!hd.equals("")) {

            Date dateDeb = getDateFromStrHour(hd);
            Date dateFin = getDateFromStrHour(hourOfDay + ":" + (minute

            ));

            if (dateDeb.compareTo(dateFin) >= 0) {
                // //***
                int mH1F = dateFin.getHours();
                int mMin1F = dateFin.getMinutes();
                Format format = android.text.format.DateFormat
                        .getTimeFormat(JobPoolDetails.this);
                Date date = new Date();

                /*
                 * EditText etM = (EditText) findViewById(R.id.heurMeeting); if
                 * (!etM.getText().toString().equals("")) {
                 *
                 * timePicker.setCurrentMinute(dateDeb.getMinutes() + 5);
                 * timePicker.setCurrentHour(dateDeb.getHours());
                 *
                 * } else
                 */
                if (mH1F - duree.getHours() > -1) {

                    TextView selectedDate = (TextView) alertDialogView1
                            .findViewById(R.id.repDate);

                    if (!selectedDate.getText().toString().equals("")) {

                        // set selected date and start time in cal1 objects
                        Date date1 = getDateFromStrDate(selectedDate.getText()
                                .toString());
                        Calendar cal1 = Calendar.getInstance();
                        cal1.setTime(date1);
                        cal1.set(Calendar.HOUR_OF_DAY, dateDeb.getHours());
                        cal1.set(Calendar.MINUTE, dateDeb.getMinutes());

                        // set current date and and end time in cal2 object
                        Date date2 = new Date();
                        Calendar cal2 = Calendar.getInstance();
                        cal2.setTime(date2);
                        cal2.set(Calendar.HOUR_OF_DAY, dateFin.getHours());
                        cal2.set(Calendar.MINUTE, dateFin.getMinutes());

                        if ((cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR))
                                && (cal1.get(Calendar.DAY_OF_MONTH) == cal2
                                .get(Calendar.DAY_OF_MONTH))
                                && (cal1.get(Calendar.MONTH) == cal2
                                .get(Calendar.MONTH))) {

                            int milliseconds1 = ((cal1
                                    .get(Calendar.HOUR_OF_DAY) * 3600000) + (cal1
                                    .get(Calendar.MINUTE) * 60000));

                            int milliseconds2 = ((cal2
                                    .get(Calendar.HOUR_OF_DAY) * 3600000) + (cal2
                                    .get(Calendar.MINUTE) * 60000));

                            // end time is equal to start time when date is = to
                            // today's date
                            if ((milliseconds1 == milliseconds2)
                                    || (milliseconds2 - milliseconds1 <= 900000)) {

                                date.setHours(dateDeb.getHours()
                                        + duree.getHours());
                                date.setMinutes(dateDeb.getMinutes()
                                        + duree.getMinutes());

                                Date currentDate = new Date();
                                if (date.getHours() <= currentDate.getHours()
                                        && date.getMinutes() <= currentDate
                                        .getMinutes()) {

                                    mH1 = date.getHours();
                                    mMin1 = date.getMinutes();
                                    et.setText(format.format(date));

                                    timePicker.setCurrentMinute(minute);
                                    timePicker.setCurrentHour(hourOfDay);

                                } else {

                                    // ////
                                    int hour = currentDate.getHours();
                                    int min = currentDate.getMinutes();

                                    int milliseconds = ((currentDate.getHours()) * 3600000)
                                            + ((currentDate.getMinutes() + 15) * 60000);

                                    if (milliseconds < 0) {
                                        hour = 0;
                                        min = 0;
                                    } else {
                                        min = ((milliseconds / (60000)) % 60);
                                        hour = ((milliseconds / (3600000)) % 24);
                                    }

                                    timePicker.setCurrentMinute(min);
                                    timePicker.setCurrentHour(hour);
                                }

                            } else {
                                date.setHours(mH1F - duree.getHours());
                                date.setMinutes(mMin1F - duree.getMinutes());
                                mH1 = date.getHours();
                                mMin1 = date.getMinutes();
                                et.setText(format.format(date));

                            }

                        } else {
                            date.setHours(mH1F - duree.getHours());
                            date.setMinutes(mMin1F - duree.getMinutes());
                            mH1 = date.getHours();
                            mMin1 = date.getMinutes();
                            et.setText(format.format(date));

                        }

                    }

                } else {

                    TextView selectedDate = (TextView) alertDialogView1
                            .findViewById(R.id.repDate);

                    if (!selectedDate.getText().toString().equals("")) {

                        // set selected date and start time in cal1 objects
                        Date date1 = getDateFromStrDate(selectedDate.getText()
                                .toString());
                        Calendar cal1 = Calendar.getInstance();
                        cal1.setTime(date1);
                        cal1.set(Calendar.HOUR_OF_DAY, dateFin.getHours());
                        cal1.set(Calendar.MINUTE, dateFin.getMinutes());

                        // today's date
                        Calendar cal2 = Calendar.getInstance();

                        if ((cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR))
                                && (cal1.get(Calendar.DAY_OF_MONTH) == cal2
                                .get(Calendar.DAY_OF_MONTH))
                                && (cal1.get(Calendar.MONTH) == cal2
                                .get(Calendar.MONTH))) {

                            int milliseconds1 = ((dateDeb.getHours() * 3600000) + (dateDeb
                                    .getMinutes() * 60000));

                            int milliseconds2 = ((dateFin.getHours() * 3600000) + (dateFin
                                    .getMinutes() * 60000));

                            if (milliseconds2 < milliseconds1) {

                                int milliseconds = ((dateDeb.getHours() + duree
                                        .getHours()) * 3600000)
                                        + ((dateDeb.getMinutes() + duree
                                        .getMinutes()) * 60000);

                                timePicker
                                        .setCurrentHour((milliseconds / (3600000)) % 24);
                                timePicker
                                        .setCurrentMinute((milliseconds / (60000)) % 60);

                            }

                        } else {
                            date.setHours(0);
                            date.setMinutes(0);
                            et.setText(format.format(date));
                            mH1 = date.getHours();
                            mMin1 = date.getMinutes();
                            if (dateFin.getHours() == 0
                                    && dateFin.getMinutes() == 0) {
                                // dateDeb.setTime(dateFin.getTime() - 60000 *
                                // 15 );
                                timePicker.setCurrentMinute(15);
                                timePicker.setCurrentHour(0);
                            }
                        }

                    }

                }

            } else {

                // when selected date is equal to today's date
                TextView selectedDate = (TextView) alertDialogView1
                        .findViewById(R.id.repDate);
                if (!selectedDate.getText().toString().equals("")) {

                    // set selected date and start time in cal1 objects
                    Date date1 = getDateFromStrDate(selectedDate.getText()
                            .toString());
                    Calendar cal1 = Calendar.getInstance();
                    cal1.setTime(date1);
                    cal1.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    cal1.set(Calendar.MINUTE, minute);

                    // today's date
                    Calendar cal2 = Calendar.getInstance();

                    if ((cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR))
                            && (cal1.get(Calendar.DAY_OF_MONTH) == cal2
                            .get(Calendar.DAY_OF_MONTH))
                            && (cal1.get(Calendar.MONTH) == cal2
                            .get(Calendar.MONTH))) {

                        int milliseconds1 = ((dateDeb.getHours() * 3600000) + (dateDeb
                                .getMinutes() * 60000));

                        int milliseconds2 = ((dateFin.getHours() * 3600000) + (dateFin
                                .getMinutes() * 60000));

                        if (milliseconds2 - milliseconds1 <= 900000) {

                            Date date = new Date();
                            int hour = date.getHours();
                            int min = date.getMinutes();
                            int milliseconds = ((date.getHours() * 3600000) + ((date
                                    .getMinutes() + 15)) * 60000);

                            if (milliseconds < 0) {
                                hour = 0;
                                min = 0;
                            } else {
                                min = ((milliseconds / (60000)) % 60);
                                hour = ((milliseconds / (3600000)) % 24);
                            }
                            timePicker.setCurrentHour(hour);
                            timePicker.setCurrentMinute(min);

                        }

                    }
                }
            }

        } else {

            // start time is not filled
            TextView selectedDate = (TextView) alertDialogView1
                    .findViewById(R.id.repDate);
            if (!selectedDate.getText().toString().equals("")) {

                // set selected date and start time in cal1 objects
                Date date1 = getDateFromStrDate(selectedDate.getText()
                        .toString());
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(date1);
                cal1.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal1.set(Calendar.MINUTE, minute);

                // today's date
                Calendar cal2 = Calendar.getInstance();

                // if selected date is equal to today's
                if ((cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR))
                        && (cal1.get(Calendar.DAY_OF_MONTH) == cal2
                        .get(Calendar.DAY_OF_MONTH))
                        && (cal1.get(Calendar.MONTH) == cal2
                        .get(Calendar.MONTH))) {

                    int milliseconds1 = ((cal1.get(Calendar.HOUR_OF_DAY) * 3600000) + (cal1
                            .get(Calendar.MINUTE) * 60000));

                    int milliseconds2 = ((cal2.get(Calendar.HOUR_OF_DAY) * 3600000) + (cal2
                            .get(Calendar.MINUTE) * 60000));

                    if (milliseconds1 < milliseconds2) {

                        // true when user select previous date
                        timePicker.setCurrentMinute(cal2.get(Calendar.MINUTE));
                        timePicker.setCurrentHour(cal2
                                .get(Calendar.HOUR_OF_DAY));
                    } else if (milliseconds1 == milliseconds2) {

                        // when user select present time
                        Date date = new Date();
                        int hour = date.getHours();
                        int min = date.getMinutes();
                        int milliseconds = ((date.getHours() * 3600000) + ((date
                                .getMinutes() + 15)) * 60000);

                        if (milliseconds < 0) {
                            hour = 0;
                            min = 0;
                        } else {
                            min = ((milliseconds / (60000)) % 60);
                            hour = ((milliseconds / (3600000)) % 24);
                        }
                        timePicker.setCurrentHour(hour);
                        timePicker.setCurrentMinute(min);

                    } else {

                        timePicker.setCurrentMinute(minute);
                        timePicker.setCurrentHour(hourOfDay);

                    }

                }
                // not equal to today's date
                else {

                    timePicker.setCurrentMinute(minute);
                    timePicker.setCurrentHour(hourOfDay);

                }

            }

        }

        timePicker.setOnTimeChangedListener(timeFinChangedListener);

        // end
        /*
         * int nextMinute = 0;
         * timePicker.setOnTimeChangedListener(mNullTimeChangedListener);
         *
         * if (minute >= 55 && minute <= 59) nextMinute = 55; else if (minute >=
         * 50) nextMinute = 50; else if (minute >= 45) nextMinute = 45; else if
         * (minute >= 40) nextMinute = 40; else if (minute >= 35) nextMinute =
         * 35; else if (minute >= 30) nextMinute = 30; else if (minute >= 25)
         * nextMinute = 25; else if (minute >= 20) nextMinute = 20; else if
         * (minute >= 15) nextMinute = 15; else if (minute >= 10) nextMinute =
         * 10; else if (minute >= 5) nextMinute = 05; else if (minute >= 0) {
         * nextMinute = 0; } else { nextMinute = 0; }
         *
         * if (minute - nextMinute == 1) { if (minute >= 55 && minute <= 59)
         * nextMinute = 00; else if (minute >= 50) nextMinute = 55; else if
         * (minute >= 45) nextMinute = 50;
         *
         * else if (minute >= 40) nextMinute = 45; else if (minute >= 35)
         * nextMinute = 40; else if (minute >= 30) nextMinute = 35; else if
         * (minute >= 25) nextMinute = 30; else if (minute >= 20) nextMinute =
         * 25; else if (minute >= 15) nextMinute = 20; else if (minute >= 10)
         * nextMinute = 15; else if (minute >= 5) nextMinute = 10; else if
         * (minute >= 0) { nextMinute = 5; } else { nextMinute = 5; } }
         *
         * TextView et = (TextView)
         * alertDialogView1.findViewById(R.id.repHeurDeb); String hd = null;
         *
         * String startHourString = null; String startMinuteString = null; if
         * (!TextUtils.isEmpty(et.getText().toString())) { if (mH1 < 10) {
         * startHourString = "0" + mH1; } else { startHourString = mH1 + ""; }
         *
         * if (mMin1 < 10) { startMinuteString = "0" + mMin1; } else {
         * startMinuteString = "" + mMin1; }
         *
         * hd = startHourString + ":" + startMinuteString;
         *
         * } else { hd = ""; }
         *
         * if (!hd.equals("")) {
         *
         * Date dateDeb = getDateFromStrHour(hd); Date dateFin =
         * getDateFromStrHour(hourOfDay + ":" + (nextMinute));
         *
         * if (dateDeb.compareTo(dateFin) >= 0) { // //*** int mH1F =
         * dateFin.getHours(); int mMin1F = dateFin.getMinutes(); Format format
         * = android.text.format.DateFormat .getTimeFormat(JobDetails.this);
         *
         * Date date = new Date();
         *
         * if (mH1F - duree.getHours() > -1) { date.setHours(mH1F -
         * duree.getHours()); date.setMinutes(mMin1F - duree.getMinutes()); mH1
         * = date.getHours(); mMin1 = date.getMinutes();
         * et.setText(format.format(date)); } else {
         *
         * date.setHours(0); date.setMinutes(0);
         * et.setText(format.format(date)); mH1 = date.getHours(); mMin1 =
         * date.getMinutes(); if (dateFin.getHours() == 0 &&
         * dateFin.getMinutes() == 0) { // dateDeb.setTime(dateFin.getTime() -
         * 60000 * 15 ); timePicker.setCurrentMinute(15);
         * timePicker.setCurrentHour(0); } }
         *
         * } else timePicker.setCurrentMinute(nextMinute); } else
         * timePicker.setCurrentMinute(nextMinute);
         *
         * timePicker.setOnTimeChangedListener(timeFinChangedListener);
         */
    }

    /**
     * The m null time changed listener.
     */
    private TimePicker.OnTimeChangedListener mNullTimeChangedListener = new TimePicker.OnTimeChangedListener() {

        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        }
    };

    /**
     * The time deb changed listener.
     */
    private TimePicker.OnTimeChangedListener timeDebChangedListener = new TimePicker.OnTimeChangedListener() {

        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
            updateDisplayDeb(view, hourOfDay, minute);
        }
    };

    /**
     * Update display deb.
     *
     * @param timePicker the time picker
     * @param hourOfDay  the hour of day
     * @param minute     the minute
     */
    @SuppressWarnings("deprecation")
    private void updateDisplayDeb(TimePicker timePicker, int hourOfDay,
                                  int minute) {
        // start

        int nextMinute = 0;
        timePicker.setOnTimeChangedListener(mNullTimeChangedListener);

        if (minute >= 55 && minute <= 59)
            nextMinute = 55;
        else if (minute >= 50)
            nextMinute = 50;
        else if (minute >= 45)
            nextMinute = 45;
        else if (minute >= 40)
            nextMinute = 40;
        else if (minute >= 35)
            nextMinute = 35;
        else if (minute >= 30)
            nextMinute = 30;
        else if (minute >= 25)
            nextMinute = 25;
        else if (minute >= 20)
            nextMinute = 20;
        else if (minute >= 15)
            nextMinute = 15;
        else if (minute >= 10)
            nextMinute = 10;
        else if (minute >= 5)
            nextMinute = 05;
        else if (minute >= 0) {
            nextMinute = 0;
        } else {
            nextMinute = 0;
        }

        if (minute - nextMinute == 1) {
            if (minute >= 55 && minute <= 59)
                nextMinute = 00;
            else if (minute >= 50)
                nextMinute = 55;
            else if (minute >= 45)
                nextMinute = 50;

            else if (minute >= 40)
                nextMinute = 45;
            else if (minute >= 35)
                nextMinute = 40;
            else if (minute >= 30)
                nextMinute = 35;
            else if (minute >= 25)
                nextMinute = 30;
            else if (minute >= 20)
                nextMinute = 25;
            else if (minute >= 15)
                nextMinute = 20;
            else if (minute >= 10)
                nextMinute = 15;
            else if (minute >= 5)
                nextMinute = 10;
            else if (minute >= 0) {
                nextMinute = 5;
            } else {
                nextMinute = 5;
            }
        }

        TextView et = (TextView) alertDialogView1.findViewById(R.id.repHeurFin);
        String hf = null;
        String endHourString = null;
        String endMinuteString = null;
        if (!TextUtils.isEmpty(et.getText().toString())) {
            if (mH2 < 10) {
                endHourString = "0" + mH2;
            } else {
                endHourString = mH2 + "";
            }

            if (mMin2 < 10) {
                endMinuteString = "0" + mMin2;
            } else {
                endMinuteString = "" + mMin2;
            }

            hf = endHourString + ":" + endMinuteString;

        } else {
            hf = "";
        }

        if (!hf.equals("")) {

            Date dateDeb = getDateFromStrHour(hourOfDay + ":" + (minute));
            Date dateFin = getDateFromStrHour(hf);

            Format format = android.text.format.DateFormat
                    .getTimeFormat(JobPoolDetails.this);
            Date date = new Date();

            if (dateDeb.compareTo(dateFin) >= 0) {

                // dateDeb.setTime(dateFin.getTime() - 60000 * 15 );
                // timePicker.setCurrentMinute(dateDeb.getMinutes());
                // timePicker.setCurrentHour(dateDeb.getHours());
                int mH1 = dateDeb.getHours();
                int mMin1 = dateDeb.getMinutes();
                // DateFormat df = DateFormat.getTimeInstance(DateFormat.SHORT);

                if (mH1 + duree.getHours() < 24) {

                    date.setHours(mH1 + duree.getHours());
                    date.setMinutes(mMin1 + duree.getMinutes());
                    mH2 = date.getHours();
                    mMin2 = date.getMinutes();
                    et.setText(format.format(date));

                } else {
                    // dateDeb.setTime(dateFin.getTime() - 60000 * 15 );
                    // timePicker.setCurrentMinute(dateDeb.getMinutes());
                    // timePicker.setCurrentHour(dateDeb.getHours());
                    date.setHours(23);
                    date.setMinutes(55);
                    mH2 = date.getHours();
                    mMin2 = date.getMinutes();
                    et.setText(format.format(date));
                    /*
                     * if (dateDeb.getHours() == 23 && dateDeb.getMinutes() ==
                     * 55) { // dateDeb.setTime(dateFin.getTime() - 60000 * 15
                     * ); timePicker.setCurrentMinute(40);
                     * timePicker.setCurrentHour(23); }
                     */

                    timePicker.setCurrentMinute(40);
                    timePicker.setCurrentHour(23);

                }

            } else {
                // ////
                // when selected date is equal to today's date
                TextView selectedDate = (TextView) alertDialogView1
                        .findViewById(R.id.repDate);
                if (!selectedDate.getText().toString().equals("")) {

                    // set selected date and start time in cal1 objects
                    Date date1 = getDateFromStrDate(selectedDate.getText()
                            .toString());
                    Calendar cal1 = Calendar.getInstance();
                    cal1.setTime(date1);
                    cal1.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    cal1.set(Calendar.MINUTE, minute);

                    // today's date
                    Calendar cal2 = Calendar.getInstance();

                    if ((cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR))
                            && (cal1.get(Calendar.DAY_OF_MONTH) == cal2
                            .get(Calendar.DAY_OF_MONTH))
                            && (cal1.get(Calendar.MONTH) == cal2
                            .get(Calendar.MONTH))) {

                        if (cal2.get(Calendar.HOUR_OF_DAY) >= hourOfDay
                                && cal2.get(Calendar.MINUTE) >= minute) {

                            /*
                             * date.setHours(23); date.setMinutes(55); mH2 =
                             * date.getHours(); mMin2 = date.getMinutes();
                             * et.setText(format.format(date)); if
                             * (dateDeb.getHours() == 23 && dateDeb.getMinutes()
                             * == 55) { // dateDeb.setTime(dateFin.getTime() -
                             * 60000 * // 15 ); timePicker.setCurrentMinute(40);
                             * timePicker.setCurrentHour(23); }
                             */

                            timePicker.setCurrentMinute(cal2
                                    .get(Calendar.MINUTE));
                            timePicker.setCurrentHour(cal2
                                    .get(Calendar.HOUR_OF_DAY));

                        } else {

                            int milliseconds1 = ((cal1
                                    .get(Calendar.HOUR_OF_DAY) * 3600000) + (cal1
                                    .get(Calendar.MINUTE) * 60000));

                            int milliseconds2 = ((23 * 3600000) + (40 * 60000));

                            int milliseconds3 = ((cal2
                                    .get(Calendar.HOUR_OF_DAY) * 3600000) + (cal2
                                    .get(Calendar.MINUTE) * 60000));

                            if (milliseconds1 > milliseconds2) {

                                int milliseconds = ((dateFin.getHours()) * 3600000)
                                        + ((dateFin.getMinutes() - 15) * 60000);

                                int hour = 0;
                                int min = 0;

                                if (milliseconds < 0) {
                                    hour = 0;
                                    min = 0;
                                } else {
                                    min = ((milliseconds / (60000)) % 60);
                                    hour = ((milliseconds / (3600000)) % 24);
                                }

                                timePicker.setCurrentMinute(min);
                                timePicker.setCurrentHour(hour);

                            } else if (milliseconds1 < milliseconds3) {

                                mH1 = date.getHours();
                                mMin1 = date.getMinutes();
                                date.setHours(mH1 + duree.getHours());
                                date.setMinutes(mMin1 + duree.getMinutes());
                                mH2 = date.getHours();
                                mMin2 = date.getMinutes();
                                et.setText(format.format(date));

                                timePicker.setCurrentMinute(mMin1);
                                timePicker.setCurrentHour(mH1);

                            } else {

                                mH1 = dateDeb.getHours();
                                mMin1 = dateDeb.getMinutes();
                                date.setHours(mH1 + duree.getHours());
                                date.setMinutes(mMin1 + duree.getMinutes());
                                mH2 = date.getHours();
                                mMin2 = date.getMinutes();
                                et.setText(format.format(date));

                                timePicker.setCurrentMinute(minute);
                                timePicker.setCurrentHour(hourOfDay);
                            }

                        }

                        // ///
                    } else {

                        mH1 = dateDeb.getHours();
                        mMin1 = dateDeb.getMinutes();
                        date.setHours(mH1 + duree.getHours());
                        date.setMinutes(mMin1 + duree.getMinutes());
                        mH2 = date.getHours();
                        mMin2 = date.getMinutes();
                        et.setText(format.format(date));

                        timePicker.setCurrentMinute(minute);
                        timePicker.setCurrentHour(hourOfDay);
                    }
                }
            }
        } else {

            // end time is not filled
            TextView selectedDate = (TextView) alertDialogView1
                    .findViewById(R.id.repDate);
            if (!selectedDate.getText().toString().equals("")) {

                // set selected date and start time in cal1 objects
                Date date1 = getDateFromStrDate(selectedDate.getText()
                        .toString());
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(date1);
                cal1.set(Calendar.HOUR_OF_DAY, hourOfDay);
                cal1.set(Calendar.MINUTE, minute);

                // today's date
                Calendar cal2 = Calendar.getInstance();

                // if selected date is equal to today's
                if ((cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR))
                        && (cal1.get(Calendar.DAY_OF_MONTH) == cal2
                        .get(Calendar.DAY_OF_MONTH))
                        && (cal1.get(Calendar.MONTH) == cal2
                        .get(Calendar.MONTH))) {

                    int milliseconds1 = ((cal1.get(Calendar.HOUR_OF_DAY) * 3600000) + (cal1
                            .get(Calendar.MINUTE) * 60000));

                    int milliseconds2 = ((cal2.get(Calendar.HOUR_OF_DAY) * 3600000) + (cal2
                            .get(Calendar.MINUTE) * 60000));

                    if (milliseconds1 <= milliseconds2) {

                        // true when user select past time or equal to present
                        // time
                        timePicker.setCurrentMinute(cal2.get(Calendar.MINUTE));
                        timePicker.setCurrentHour(cal2
                                .get(Calendar.HOUR_OF_DAY));
                    } else {

                        int hour = hourOfDay;
                        int min = minute;

                        int milliseconds = ((hour * 3600000) + min * 60000);

                        if (milliseconds > 85500000) {
                            // when user schedule job at 23.45 above
                            hour = 23;
                            min = 40;

                        }
                        timePicker.setCurrentMinute(min);
                        timePicker.setCurrentHour(hour);

                    }

                }
                // not equal to today's date
                else {

                    timePicker.setCurrentMinute(minute);
                    timePicker.setCurrentHour(hourOfDay);

                }

            }

        }

        timePicker.setOnTimeChangedListener(timeDebChangedListener);

        // end
        /*
         * int nextMinute = 0;
         * timePicker.setOnTimeChangedListener(mNullTimeChangedListener);
         *
         * if (minute >= 55 && minute <= 59) nextMinute = 55; else if (minute >=
         * 50) nextMinute = 50; else if (minute >= 45) nextMinute = 45; else if
         * (minute >= 40) nextMinute = 40; else if (minute >= 35) nextMinute =
         * 35; else if (minute >= 30) nextMinute = 30; else if (minute >= 25)
         * nextMinute = 25; else if (minute >= 20) nextMinute = 20; else if
         * (minute >= 15) nextMinute = 15; else if (minute >= 10) nextMinute =
         * 10; else if (minute >= 5) nextMinute = 05; else if (minute >= 0) {
         * nextMinute = 0; } else { nextMinute = 0; }
         *
         * if (minute - nextMinute == 1) { if (minute >= 55 && minute <= 59)
         * nextMinute = 00; else if (minute >= 50) nextMinute = 55; else if
         * (minute >= 45) nextMinute = 50;
         *
         * else if (minute >= 40) nextMinute = 45; else if (minute >= 35)
         * nextMinute = 40; else if (minute >= 30) nextMinute = 35; else if
         * (minute >= 25) nextMinute = 30; else if (minute >= 20) nextMinute =
         * 25; else if (minute >= 15) nextMinute = 20; else if (minute >= 10)
         * nextMinute = 15; else if (minute >= 5) nextMinute = 10; else if
         * (minute >= 0) { nextMinute = 5; } else { nextMinute = 5; } }
         *
         * TextView et = (TextView)
         * alertDialogView1.findViewById(R.id.repHeurFin); String hf = null;
         * String endHourString = null; String endMinuteString = null; if
         * (!TextUtils.isEmpty(et.getText().toString())) { if (mH2 < 10) {
         * endHourString = "0" + mH2; } else { endHourString = mH2 + ""; }
         *
         * if (mMin2 < 10) { endMinuteString = "0" + mMin2; } else {
         * endMinuteString = "" + mMin2; }
         *
         * hf = endHourString + ":" + endMinuteString;
         *
         * } else { hf = ""; }
         *
         * if (!hf.equals("")) {
         *
         * Date dateDeb = getDateFromStrHour(hourOfDay + ":" + (nextMinute));
         * Date dateFin = getDateFromStrHour(hf); if (dateDeb.compareTo(dateFin)
         * >= 0) {
         *
         * // dateDeb.setTime(dateFin.getTime() - 60000 * 15 ); //
         * timePicker.setCurrentMinute(dateDeb.getMinutes()); //
         * timePicker.setCurrentHour(dateDeb.getHours()); int mH1 =
         * dateDeb.getHours(); int mMin1 = dateDeb.getMinutes(); // DateFormat
         * df = DateFormat.getTimeInstance(DateFormat.SHORT); Format format =
         * android.text.format.DateFormat .getTimeFormat(JobDetails.this); Date
         * date = new Date(); if (mH1 + duree.getHours() < 24) {
         *
         * date.setHours(mH1 + duree.getHours()); date.setMinutes(mMin1 +
         * duree.getMinutes()); mH2 = date.getHours(); mMin2 =
         * date.getMinutes(); et.setText(format.format(date)); } else { //
         * dateDeb.setTime(dateFin.getTime() - 60000 * 15 ); //
         * timePicker.setCurrentMinute(dateDeb.getMinutes()); //
         * timePicker.setCurrentHour(dateDeb.getHours()); date.setHours(23);
         * date.setMinutes(55); mH2 = date.getHours(); mMin2 =
         * date.getMinutes(); et.setText(format.format(date)); if
         * (dateDeb.getHours() == 23 && dateDeb.getMinutes() == 55) { //
         * dateDeb.setTime(dateFin.getTime() - 60000 * 15 );
         * timePicker.setCurrentMinute(50); timePicker.setCurrentHour(23); } }
         *
         * } else timePicker.setCurrentMinute(nextMinute); } else
         * timePicker.setCurrentMinute(nextMinute);
         *
         * timePicker.setOnTimeChangedListener(timeDebChangedListener);
         */
    }

    /**
     * Gets the updated value of status.
     *
     * @return the updated value of status
     */
    public int getUpdatedValueOfStatus() {

        return status;
    }

    /**
     * Check oblig globale.
     *
     * @return the int
     */
//    public int checkObligGlobale() {
//        Vector<Famille> vect = dataAccessObject.getAllFamille(Integer
//                .parseInt(idModel));
//        Iterator<Famille> iter = vect.iterator();
//        while (iter.hasNext()) {
//            Famille fm = iter.next();
//            if (CheckObligPartial(dataAccessObject.getAllItem(idIntervention,
//                    fm.getID())) == 0)
//                return 0;
//        }
//
//        return 1;
//    }

    /**
     * Check oblig globale. - Version 4.2 (Rapport v2)
     *
     * @return the int
     */
    public int checkObligGlobale() {
        Vector<Families> vect = dataAccessObject.getSBCategory(idIntervention);
//        Vector<Families> vect = dataAccessObject.getAllFamiliesWithSB(Integer
//                .parseInt(idModel), idIntervention);
        Iterator<Families> iter = vect.iterator();
        while (iter.hasNext()) {
            Families fm = iter.next();
            if (CheckObligPartial(dataAccessObject.getAllItem(idIntervention,
                    fm.getIdFamily(), fm.getIteration())) == 0)
                return 0;
        }

        return 1;
    }

    /**
     * Duplicate method of checkObligGlobale
     * Check all the mandatory fields are filled
     *
     * @return
     */
    public boolean isAllMandatoryFieldsAreFilled() {
        Vector<Families> vect = dataAccessObject.getSBCategory(idIntervention);
//        Vector<Families> vect = dataAccessObject.getAllFamiliesWithSB(Integer
//                .parseInt(idModel), idIntervention);
        Iterator<Families> iter = vect.iterator();
        while (iter.hasNext()) {
            Families fm = iter.next();
            if (CheckObligPartial(dataAccessObject.getAllItem(idIntervention,
                    fm.getIdFamily(), fm.getIteration())) == 0)
                return false;
        }

        return true;
    }

    /**
     * Check oblig partial.
     *
     * @param map the map
     * @return the int
     */
    public int CheckObligPartial(HashMap<Integer, Item> map) {

        int drp = 0;

        Iterator<Item> iter = map.values().iterator();
        while (iter.hasNext()) {
            Item item1 = new Item(), item2, item3;
            item1 = iter.next();
            // Logger.log(TAG, "item1 : " + item1.getNomItem());

            if (item1.getCond() == 0) {
                if (item1.getOblig() == 1)
                    if (item1.getValItem() == 0) {
                        return 0;
                    }
                // // if ((item1.getValItem() == 0)
                // // && (item1.getIdTypeItem() != 7))// Code To be
                // // removed in V3.3
                // return 0;

            } else {
                item2 = new Item(item1.getCond(), item1.getVal_cond(),
                        item1.getValeurNet());

                // --------------PREVIOUS CODE--------------
                // while (item2.getCond() != 0) {
                //
                // item3 = map.get(item2.getCond());
                //
                // Logger.log(TAG, "item3 : " + item3.getNomItem());
                //
                // if (item2.getVal_cond().equals(item3.getValeurNet())) {
                // item2 = new Item(item3.getCond(), item3.getVal_cond(),
                // item3.getValeurNet());
                // drp = 1;
                // } else {
                // drp = 0;
                // item2.setCond(0);
                // }
                // }

                // -------------PREVIOUS CODE--------------

                // --------------NEW CHANGES--------------

                item3 = map.get(item2.getCond());
                if (RecursiveCompare(item2, item2, item3, map)) {
                    drp = 1;
                } else {
                    drp = 0;
                }
                // if (CompareValues(item2, item3)) {
                // if (item3.getCond() != 0) {
                // if (RecursiveCompare(item2, item3,
                // map.get(item3.getCond()), map)) {
                // drp = 1;
                // } else {
                // drp = 0;
                // }
                // }
                //
                // } else {
                // drp = 0;
                // }
                // --------------NEW CHANGES--------------

                if (drp == 1) {
                    if (item1.getOblig() == 1)
                        if (item1.getValItem() == 0) {
                            return 0;
                        }

                    drp = 0;
                }
            }

        }

        return 1;
    }

    /**
     * Duplication interv.
     */
    @SuppressWarnings("deprecation")
//    public void duplicationInterv() {
//        initDates();
//        factory = LayoutInflater.from(getApplicationContext());
//        alertDialogView1 = factory.inflate(R.layout.replanifenetre, null);
//
//        TextView tv = (TextView) alertDialogView1.findViewById(R.id.repDate);
//
//        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
//        // Date date=getDateFromDbFormat(mDate1);
//
//        Date date = new Date(mAnnee, mMois, mJour, mH1, mMin1);
//
//        tv.setText(df.format(date));
//        tv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                showDatePicker();
//            }
//
//        });
//        tv = (TextView) alertDialogView1.findViewById(R.id.repHeurDeb);
//        // df=DateFormat.getTimeInstance(SimpleDateFormat.SHORT);
//        Format writeFormat = android.text.format.DateFormat.getTimeFormat(this);
//        // date=getDateFromDbFormat(mDate1);
//        tv.setText(writeFormat.format(date));
//        tv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                showTimePickerDeb();
//            }
//
//        });
//        tv = (TextView) alertDialogView1.findViewById(R.id.repHeurFin);
//        // df=DateFormat.getTimeInstance(SimpleDateFormat.SHORT);
//
//        Date date2 = new Date(mAnnee, mMois, mJour, mH2, mMin2);
//
//        tv.setText(writeFormat.format(date2));
//
//        tv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
//                showTimePickerFin();
//            }
//
//        });
//        AlertDialog.Builder adb = new AlertDialog.Builder(this);
//        adb.setView(alertDialogView1);
//        adb.setTitle(R.string.textCopyIcon);
//        adb.setPositiveButton(getString(R.string.ok).toUpperCase(),
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//						/*
//						 * dataAccessObject.duplication(idIntervention,
//						 * getDbDate(1), getDbDate(2));
//						 */
//
//                        // if (!JobDetails.this.isFinishing())
//                        // EventBus.getDefault().post(new
//                        // UpdateDataBaseEvent());
//                        setResult(35);
//                        closeActivity = true;
//
//                        synch();
//
//                    }
//                });
//
//        adb.setNegativeButton(R.string.textCancelLable,
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                });
//        adb.show();
//
//    }

    /**
     * ** This method restrict date picker dialog so user can not choose any
     * future date.
     *
     * @param datePicker the date picker
     */
    public void restrictPreviousDateChoice(DatePicker datePicker,
                                           final android.widget.TextView txtFullDate) {
        try {
            // Field mDatePickerField = dpd.getClass().getDeclaredField(
            // "mDatePicker");
            // mDatePickerField.setAccessible(true);
            // DatePicker datePicker = (DatePicker) mDatePickerField.get(dpd);

            final Calendar calendar = Calendar.getInstance(Locale.getDefault());
            final int minYear = calendar.get(Calendar.YEAR);
            final int minMonth = calendar.get(Calendar.MONTH);
            final int minDay = calendar.get(Calendar.DAY_OF_MONTH);

            txtFullDate.setText(DateFormatUtils.getDateString(minDay, minMonth,
                    minYear));

            datePicker.init(minYear, minMonth, minDay,
                    new OnDateChangedListener() {
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


    // ---------------------------------------------NEW--CHANGES--------------------------------------------------

    /**
     * Recursive compare.
     *
     * @param itemR the item r
     * @param itemF the item f
     * @param itemP the item p
     * @param map   the map
     * @return the boolean
     */
    public Boolean RecursiveCompare(Item itemR, Item itemF, Item itemP,
                                    HashMap<Integer, Item> map) {

        try {
            if (CompareValues(itemF, itemP)) {

                if (itemP.getCond() != 0) {

                    return RecursiveCompare(itemR, itemP, map.get(itemP.getCond()),
                            map);
                } else
                    return true;
            } else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Compare values.
     *
     * @param itemF the item f
     * @param itemP the item p
     * @return the boolean
     */
    public Boolean CompareValues(Item itemF, Item itemP) throws Exception {
        Boolean drp = false;

        if (itemP.getValItem() == 1 || itemP.getIdTypeItem() == 7
                || itemP.getIdTypeItem() == 8 || itemP.getIdTypeItem() == 9) {
            String operator = "", value = "";
            Date dF, dP;

            switch (itemP.getIdTypeItem()) {
                case 3:
                    operator = itemF.getVal_cond().substring(0, 1);
                    value = itemF.getVal_cond().substring(1);
                    dF = getDateF(value);
                    dP = getDateF(itemP.getValeurNet());

                    if (operator.equals("=")) {
                        if (dF.getTime() == dP.getTime())
                            drp = true;
                    } else if (operator.equals("<")) {
                        if (dF.getTime() > dP.getTime())
                            drp = true;
                    } else if (operator.equals(">")) {
                        if (dF.getTime() < dP.getTime())
                            drp = true;
                    }

                    break;
                case 4:
                    operator = itemF.getVal_cond().substring(0, 1);
                    value = itemF.getVal_cond().substring(1);
                    dF = getTimes(value);
                    dP = getTimes(itemP.getValeurNet());

                    if (operator.equals("=")) {
                        if (dF.getTime() == dP.getTime())
                            drp = true;
                    } else if (operator.equals("<")) {
                        if (dF.getTime() > dP.getTime())
                            drp = true;
                    } else if (operator.equals(">")) {
                        if (dF.getTime() < dP.getTime())
                            drp = true;
                    }

                    break;
                case 5:
                    operator = itemF.getVal_cond().substring(0, 1);
                    Double val = Double.parseDouble(itemF.getVal_cond()
                            .substring(1));

                    Double valp;
                    try {
                        valp = Double.parseDouble(itemP.getValeurNet());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        valp = 0.00;

                    }

                    if (operator.equals("=")) {

                        if (val.equals(valp))
                            drp = true;
                    } else if (operator.equals("<")) {
                        if (val > valp)
                            drp = true;
                    } else if (operator.equals(">")) {
                        if (val < valp)
                            drp = true;
                    }

                    break;
                case 6:
                    break;
                case 7:
                    if (itemF.getVal_cond().equals("1")) {
                        if (itemP.getValeurNet().equals("1"))
                            drp = true;
                    } else if (itemF.getVal_cond().equals("2")) {
                        if (itemP.getValeurNet().equals("2")
                                || itemP.getValeurNet().equals(""))
                            drp = true;
                    }

                    break;
                case 8:
                    if (itemF.getVal_cond().equals("1")) {
                        if (itemP.getValeurNet().equals("1"))
                            drp = true;
                    } else if (itemF.getVal_cond().equals("2")) {
                        if (itemP.getValeurNet().equals("2")
                                || itemP.getValeurNet().equals(""))
                            drp = true;
                    }

                    break;
                case 9:
                    if (itemF.getVal_cond().equals("1")) {
                        if (!itemP.getValeurNet().equals(""))
                            drp = true;
                    } else if (itemF.getVal_cond().equals("2")) {
                        if (itemP.getValeurNet().equals("2")
                                || itemP.getValeurNet().equals(""))
                            drp = true;
                    }

                    break;

//                default:
//                    if (itemF.getVal_cond().equals(itemP.getValeurNet()))
//                        drp = true;
//                    break;
                case 11:
                    operator = itemF.getVal_cond().substring(0, 1);
                    value = itemF.getVal_cond().substring(1);
                    dF = getTimes(value);
                    dP = getTimes(itemP.getValeurNet());

                    if (operator.equals("=")) {
                        if (dF.getTime() == dP.getTime())
                            drp = true;
                    } else if (operator.equals("<")) {
                        if (dF.getTime() > dP.getTime())
                            drp = true;
                    } else if (operator.equals(">")) {
                        if (dF.getTime() < dP.getTime())
                            drp = true;
                    }

                    break;

                default:
//                    if (itemF.getVal_cond().contains("@@@")) {
//
//                    } else if (itemF.getVal_cond().equals(itemP.getValeurNet()))
//                        drp = true;

                    String[] test = itemF.getVal_cond().split("@@@");
                    if (test != null && test.length > 0) {
                        for (int i = 0; i < test.length; i++) {
//                            if (itemP.getValeurNet().contains("@@@")) {
                            String[] test1 = itemP.getValeurNet().split("@@@");
                            if (test1 != null && test1.length > 0) {
                                for (int j = 0; j < test1.length; j++) {
                                    if (test[i].trim().equals(test1[j].trim())) {
                                        drp = true;
                                        break;
                                    }
                                }
//                                }
                            } else if (test[i].trim().equals(itemP.getValeurNet().trim())) {
                                drp = true;
                                break;
                            }
                        }
                    }

                    break;
            }
        }
        return drp;
    }

    /**
     * Gets the date f.
     *
     * @param mDate the m date
     * @return the date f
     */
    public Date getDateF(String mDate) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        Date date;
        try {
            date = df.parse(mDate + " 00:00:00.000");
            return date;
        } catch (ParseException e) {
            Logger.printException(e);
            return new Date();
        }
    }

    /**
     * Gets the times.
     *
     * @param mDate the m date
     * @return the times
     */
    public Date getTimes(String mDate) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        Date date;
        try {
            date = df.parse("01/01/0001 " + mDate + ":00.000");
            return date;
        } catch (ParseException e) {
            Logger.printException(e);
            return new Date();
        }
    }

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
////        if (!isTextDialogOpened) {
////            super.onBackPressed();
////            finish();
////        } else {
////            EventBus.getDefault().post(new CloseTextDialogEvent());
////        }
//    }

    private String getJobNumber() {

//        String jobNumber = getIntent().getStringExtra(KEYS.CurrentJobs.TYPE);
        //new changes

        String jobNumber = dataAccessObject.getJobNumber(idIntervention);

        if (jobNumber == null || jobNumber.trim().length() == 0) {
            jobNumber = getIntent().getStringExtra(KEYS.CurrentJobs.TYPE);
            if (jobNumber != null) {
                if (jobNumber.contains("-")) {
                    String[] job = jobNumber.split("-");
                    jobNumber = job[0];
                }
            } else {
                jobNumber = "";
            }

        }

//        return getResources().getString(R.string.intervention) + " " + jobNumber;
        //new changes
        return jobNumber;
    }


    @Override
    protected void onStart() {
        super.onStart();
        isActive = true;
//        EventBus.getDefault().register(this);
        ObservableObject.getInstance().addObserver(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive = false;
//        if (EventBus.getDefault().isRegistered(this))
//            EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        ObservableObject.getInstance().deleteObserver(this);
        super.onDestroy();
        if (progressDSynch != null
                && progressDSynch.isShowing())
            progressDSynch.dismiss();

    }


    private void showToastMessage(final String message) {
        //run on main thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(JobPoolDetails.this, message, Toast.LENGTH_SHORT).show();
            }
        });

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


}