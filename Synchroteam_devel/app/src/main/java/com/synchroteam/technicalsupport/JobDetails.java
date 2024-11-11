package com.synchroteam.technicalsupport;

import static com.synchroteam.synchroteam.NewJob.MY_PERMISSIONS_REQUEST_LOCATION;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TypefaceSpan;
import android.util.Log;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.Conge;
import com.synchroteam.beans.Families;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.beans.Item;
import com.synchroteam.beans.JobIncidentLogModel;
import com.synchroteam.beans.JobIncidentModel;
import com.synchroteam.beans.LocationPoints;
import com.synchroteam.beans.NotificationItem;
import com.synchroteam.beans.TravelActivity;
import com.synchroteam.beans.UnavailabilityBeans;
import com.synchroteam.beans.UpdateDataBaseEvent;
import com.synchroteam.beans.UpdateJobDetailUi;
import com.synchroteam.beans.UpdateUiAfterSync;
import com.synchroteam.beans.UpdateUiOnSync;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.AppUpdateDialog;
import com.synchroteam.dialogs.AuthenticationErrorDialog;
import com.synchroteam.dialogs.ClockInTimeOutDialog;
import com.synchroteam.dialogs.ErrorDialog;
import com.synchroteam.dialogs.JobPauseFinishDialog;
import com.synchroteam.dialogs.JobStartResumeDialog;
import com.synchroteam.dialogs.MultipleDeviceNotSupported;
import com.synchroteam.dialogs.RescheduleIntervenDialog;
import com.synchroteam.dialogs.StartJobActivityDialog;
import com.synchroteam.dialogs.SynchronizationErrorDialog;
import com.synchroteam.events.ActivityUpdateEvent;
import com.synchroteam.events.ClockInTimeOutEvent;
import com.synchroteam.events.CloseTextDialogEvent;
import com.synchroteam.events.DrivingModeUpdateEvent;
import com.synchroteam.events.JobPauseFinishEvent;
import com.synchroteam.events.JobStartResumeEvent;
import com.synchroteam.events.SaveTextDialogEvent;
import com.synchroteam.events.TravelStopEvent;
import com.synchroteam.fragment.CatalougeJobDetailFragment;
import com.synchroteam.fragment.DiscrptionJobDetailFragment;
import com.synchroteam.fragment.InvoicingFragmentNew;
import com.synchroteam.fragmenthelper.CurrentJobsFragmentHelperNew;
import com.synchroteam.mvp.view.ReportsFragment;
import com.synchroteam.observers.ObservableObject;
import com.synchroteam.retrofit.ApiClientNew;
import com.synchroteam.retrofit.ApiInterface;
import com.synchroteam.retrofit.models.NotificationService.EventNotiRequest;
import com.synchroteam.retrofit.models.NotificationService.EventNotiResult;
import com.synchroteam.retrofit.models.NotificationService.jsonInfo;
import com.synchroteam.roomDB.RoomDBSingleTone;
import com.synchroteam.roomDB.entity.LocationCoordinates;
import com.synchroteam.roomDB.entity.NotificationEventModels;
import com.synchroteam.synchroteam.AddDuplicateNewJob;
import com.synchroteam.synchroteam.CommonInterface;
import com.synchroteam.synchroteam.NewJob;
import com.synchroteam.synchroteam.SyncroTeamApplication;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.tracking.DaoTracking;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.ClockInOutUtil;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DaoTrackingManager;
import com.synchroteam.utils.DateChecker;
import com.synchroteam.utils.DateFormatUtils;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.LocationUtils;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.MyCountDownTimer;
import com.synchroteam.utils.SharedPref;
import com.synchroteam.utils.SynchroteamUitls;
import com.synchroteam.utils.TabPageIndicator;
import com.synchroteam.utils.TwoLevelCircularProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;
import java.util.TimeZone;
import java.util.Timer;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;

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
public class JobDetails extends AppCompatActivity implements CommonInterface, Observer, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    /**
     * The action bar.
     */
    private ActionBar actionBar;

    /**
     * The job details view pager.
     */
    private ViewPager jobDetailsViewPager;

    /**
     * The stop ib.
     */
//    private ImageButton startJobIb, pauseIb, stopIb;

    /**
     * The data access object.
     */
    private Dao dataAccessObject;
    private DaoTracking daoTracking;

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
     * The view switcher.
     */
//    private ViewSwitcher viewSwitcher;

    /**
     * The is format24.
     */
    private boolean isFormat24;
    /**
     * The status.
     */
    private int status;

    /**
     * The chronometer.
     */
//    private Chronometer chronometer;

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
    public int mJour, mMois, mAnnee, mH1, mH2, mMin1, mMin2;

    public int mEndJour, mEndMois, mEndAnnee;

    /**
     * The alert dialog view2.
     */
    private View alertDialogView4, alertDialogView2;

    /**
     * The is additional information avaliable.
     */
    private boolean isAdditionalInformationAvaliable = false;

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
     * The user.
     */
    private User user;

    private ImageView imgArrowLeft, imgArrowRight;

    private TabPageIndicator indicator;

    private LinearLayout linDone;


    private long mStartBtnClickTime;
    private long mSPauseBtnClickTime;
    private long mStopBtnClickTime;

    private String dtCreated;


    private Resources wrappedResources;

    public boolean isTextDialogOpened;

    private static final String TAG = JobDetails.class.getSimpleName();


    // New functionality
    TextView txtClockMode, txtStopJobView;
    android.widget.TextView txtJobStatus;
    private RelativeLayout layBottomBar;
    private boolean isActive;
//v51.0.2
    // private Realm realm;

    boolean isStartJob = false;
    private Context context;
    private int flCreateUpdateInvoiceQuotation;
    int countPager = 3;

    RelativeLayout rel_incident, rel_incident_view;
    ImageView img_view_incident, img_pause_incident;
    TextView txt_incident_time;
    TwoLevelCircularProgressBar progress_incident;
    MyCountDownTimer incidentTimeCounter;
    private static final String FORMAT = "%02d:%02d:%02d";
    public Handler incidentHandler;
    public Runnable runnableIncident;

    TextView txt_start_timer, txt_resolved_timer, txt_start_limit, txt_resolved_limit,
            txt_freeze_timer, txt_unfreeze_timer, txt_start, txt_submit_freeze, txt_resolved;
    EditText edt_commentaire;
    LinearLayout lin_incident_action, lin_freeze_view;
    RelativeLayout rel_close;
    JobIncidentModel incidentModel;
    long incidentTimeInterval = 0;

    ArrayList<NotificationItem> notiList;

    /**
     * The location request.
     */
    private LocationRequest locationRequest;

    private LocationManager locationManager;

    /**
     * The my timer.
     */
    private Timer myTimer;
    /**
     * The location client.
     */
    private GoogleApiClient locationClient;

    Double latitude;
    Double longitude;
    String gpsEvent;
    private Calendar cal;
    List<NotificationEventModels> l;


    /*
     * (non-Javadoc)
     *
     * @see android.support.v7.app.AppCompatActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getApplicationContext();
        factory = LayoutInflater.from(context);
        // actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        setContentView(R.layout.layout_job_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
//v51.0.2
        //   realm = Realm.getDefaultInstance();

        dataAccessObject = DaoManager.getInstance();
        daoTracking = DaoTrackingManager.getInstance();
//        dao = DaoManager.getInstance();

        txtClockMode = (TextView) findViewById(R.id.txt_clock_mode);
        txtJobStatus = (android.widget.TextView) findViewById(R.id.txtJobStatus);
        txtStopJobView = (TextView) findViewById(R.id.txt_stopview);
        layBottomBar = (RelativeLayout) findViewById(R.id.layout_start_job);

        //views for the job incident
        rel_incident = (RelativeLayout) findViewById(R.id.rel_incident);
        img_view_incident = (ImageView) findViewById(R.id.img_view_incident);
        img_pause_incident = (ImageView) findViewById(R.id.img_pause_incident);
        txt_incident_time = (TextView) findViewById(R.id.txt_incident_time);
        progress_incident = (TwoLevelCircularProgressBar) findViewById(R.id.progress_incident);
        rel_incident_view = (RelativeLayout) findViewById(R.id.rel_incident_view);

        txt_start_timer = (TextView) findViewById(R.id.txt_start_timer);
        txt_resolved = (TextView) findViewById(R.id.txt_resolved);
        txt_resolved_timer = (TextView) findViewById(R.id.txt_resolved_timer);
        txt_start_limit = (TextView) findViewById(R.id.txt_start_limit);
        txt_resolved_limit = (TextView) findViewById(R.id.txt_resolved_limit);

        txt_freeze_timer = (TextView) findViewById(R.id.txt_freeze_timer);
        txt_unfreeze_timer = (TextView) findViewById(R.id.txt_unfreeze_timer);
        txt_start = (TextView) findViewById(R.id.txt_start);
        edt_commentaire = (EditText) findViewById(R.id.txt_commentaire);
        txt_submit_freeze = (TextView) findViewById(R.id.txt_submit_freeze);


        lin_incident_action = (LinearLayout) findViewById(R.id.lin_incident_action);
        lin_freeze_view = (LinearLayout) findViewById(R.id.lin_freeze_view);
        rel_close = (RelativeLayout) findViewById(R.id.rel_close);

        txt_submit_freeze.setOnClickListener(onClickListener);
        txt_freeze_timer.setOnClickListener(onClickListener);
        txt_unfreeze_timer.setOnClickListener(onClickListener);
        txt_start.setOnClickListener(onClickListener);
        rel_close.setOnClickListener(onClickListener);
        rel_incident.setOnClickListener(onClickListener);
        txt_resolved.setOnClickListener(onClickListener);

        cal = Calendar.getInstance();

        initializingForLocation();

        getRequiredData();

        logicForIncidentReport();

        if (gestionAcces != null && gestionAcces.getFlCreateUpdateInvoiceQuotation() > 0) {
            flCreateUpdateInvoiceQuotation = gestionAcces.getFlCreateUpdateInvoiceQuotation();
        }

        String title = getIntent().getStringExtra(KEYS.CurrentJobs.TYPE);
        if (title != null && title.length() > 0) {
            SpannableString titleSpannable = new SpannableString(title);
            titleSpannable.setSpan(
                    new TypefaceSpan(this.getResources().getString(
                            R.string.fontName_hevelicaLight)), 0,
                    titleSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            // actionBar.setTitle(titleSpannable);
//		actionBar.setTitle(isLGDevice() ? title : titleSpannable);
            actionBar.setTitle(null);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_action_back);

            android.widget.TextView txtTitle = (android.widget.TextView) findViewById(R.id.toolbar_title);
            txtTitle.setText(isLGDevice() ? title : titleSpannable);
        }

        jobDetailsViewPager = (ViewPager) findViewById(R.id.pagerJobDetails);


//        startJobIb = (ImageButton) findViewById(R.id.startJobIb);
//        pauseIb = (ImageButton) findViewById(R.id.pauseIb);
//        stopIb = (ImageButton) findViewById(R.id.stopIb);
//        stopIb.setBackgroundDrawable(null);
//        pauseIb.setBackgroundDrawable(null);
//        viewSwitcher = (ViewSwitcher) findViewById(R.id.containerBottomTimerFl);
//        chronometer = (Chronometer) findViewById(R.id.timerTv);
//        chronometer
//                .setOnChronometerTickListener(chronometerTickListenerOnStartOrRestart);
//        startJobIb.setOnClickListener(onClickListener);
//        pauseIb.setOnClickListener(onClickListener);
//        stopIb.setOnClickListener(onClickListener);


        txtClockMode.setOnClickListener(onClickListener);
        txtJobStatus.setOnClickListener(onClickListener);
        txtStopJobView.setOnClickListener(onClickListener);

        isFormat24 = android.text.format.DateFormat.is24HourFormat(this);


        if (gestionAcces != null && flCreateUpdateInvoiceQuotation == 0) {
            CONTENT = new String[]{this.getString(R.string.textDescriptionLable).toUpperCase(),
                    this.getString(R.string.textTabsReports).toUpperCase(),
                    this.getString(R.string.textTabParts).toUpperCase(),
                    this.getString(R.string.textAttachmentLableTv).toUpperCase()};
            countPager = 3;
        } else {
            CONTENT = new String[]{this.getString(R.string.textDescriptionLable).toUpperCase(),
                    this.getString(R.string.textTabsReports).toUpperCase(),
                    this.getString(R.string.textTabParts).toUpperCase(),
                    this.getString(R.string.textTabsCatalouge).toUpperCase(),
                    this.getString(R.string.textAttachmentLableTv).toUpperCase()};
            countPager = 4;
        }

        jobDetailsViewPager.setOffscreenPageLimit(5);
        jobDetailsViewPager.setAdapter(new JobDetailsPagerAdapter(
                getSupportFragmentManager(), countPager));
        jobDetailsViewPager.setOnPageChangeListener(simpleOnPageChangeListener);

        // addTabs(actionBar);
        indicator = (TabPageIndicator) findViewById(R.id.indicator);
        indicator.setVisibility(View.VISIBLE);
        indicator.setViewPager(jobDetailsViewPager);
        indicator.setTabSize(countPager);


//        Typeface typeFace = Typeface.createFromAsset(getAssets(),
//                "fonts/eurostile.TTF");
//        chronometer.setTypeface(typeFace);

        indicator.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                if (arg0 != 0) {

                    if (((idUser + "").equals(user.getId() + "")) && ((status == KEYS.JObDetail.JOB__SUSPENDED)
                            || (status == KEYS.JObDetail.JOB_NOT_STARTED1)
                            || (status == KEYS.JObDetail.JOB_NOT_STARTED2))) {

                        DialogUtils
                                .showInfoDialog(
                                        JobDetails.this,
                                        JobDetails.this
                                                .getString(R.string.textAlertLable) + "!",
                                        JobDetails.this
                                                .getString(R.string.textToastPleaseStartJob));

                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        //old code 50
//        dataAccessObject = DaoManager.getInstance();
////        dao = DaoManager.getInstance();
//        getRequiredData();

        // google doc item 149
        int idStatus = dataAccessObject.getStatus(idIntervention);


        if (idStatus != -1) {
            status = idStatus;
        }

        //new changes
        linDone = (LinearLayout) findViewById(R.id.lin_done_container);
//        final LinearLayout linRootView = (LinearLayout) findViewById(R.id.rootLayout);
//        linRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                int heightDiff = linRootView.getRootView().getHeight() - linRootView.getHeight();
//
//                DisplayMetrics metrics = getResources().getDisplayMetrics();
//                int screenHeight = metrics.heightPixels;
//
//                if (heightDiff > screenHeight/3){
//                    onShowKeyboard();
//                }else {
//                    onHideKeyboard();
//                }
//            }
//        });

        if (isStartJob) {
            startJobAutomatically();
        }
    }

    private void logicForIncidentReport() {
        Logger.log("startJobAutomatically", " " + status);
        incidentModel = dataAccessObject.getJobIncident(idIntervention);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                handler.postDelayed(this, 11000);
                progress_incident.setProgressValue(0);
                updateProgressIncident();
            }
        }, 0);


        if (incidentModel != null) {
            rel_incident_view.setVisibility(View.GONE);
            rel_incident.setVisibility(View.VISIBLE);
            hasIncident(incidentModel);
        } else {
            rel_incident_view.setVisibility(View.GONE);
            rel_incident.setVisibility(View.GONE);
        }
    }

    private void stopProgressTimerIncident() {
        if (incidentHandler != null && runnableIncident != null)
            incidentHandler.removeCallbacks(runnableIncident);
    }

    private void hasIncident(JobIncidentModel incidentModel) {

        incidentTimeInterval = 0;

        incidentTimeInterval = timerValueCalculation();

        if (incidentModel.isFlFrozen()) {
            showCurrentTimerNew(incidentTimeInterval);
            freezeTimerValue();
        } else {
            if (!incidentModel.getStatus().equalsIgnoreCase("resolve")) {
                runTimerHandler(incidentTimeInterval);
            } else {
                showCurrentTimerNew(incidentTimeInterval);
            }
            updateProgressColor(incidentTimeInterval, incidentModel);
        }


        if (incidentModel.getStatus().equalsIgnoreCase("own") ||
                incidentModel.getStatus().equalsIgnoreCase("declare")) {
            long value = incidentTimeInterval;
            if (value < 0) {
                value = -value;
            }
            txt_start_timer.setText(showTimerValue(value));
            txt_unfreeze_timer.setVisibility(View.GONE);
        } else if (incidentModel.getStatus().equalsIgnoreCase("freeze")) {
            txt_unfreeze_timer.setVisibility(View.VISIBLE);
            txt_freeze_timer.setVisibility(View.GONE);
            txt_start.setVisibility(View.GONE);
        }

        if (incidentModel.isFlFrozen()) {
            txt_unfreeze_timer.setVisibility(View.VISIBLE);
            txt_start.setVisibility(View.GONE);
            txt_resolved.setVisibility(View.GONE);
            txt_freeze_timer.setVisibility(View.GONE);
        } else {
            txt_unfreeze_timer.setVisibility(View.GONE);
            if (incidentModel.getStatus().equalsIgnoreCase("own") ||
                    incidentModel.getStatus().equalsIgnoreCase("declare")) {
                txt_start.setVisibility(View.VISIBLE);
                txt_resolved.setVisibility(View.GONE);
            } else if (incidentModel.getStatus().equalsIgnoreCase("start")) {
                txt_resolved.setVisibility(View.VISIBLE);
                txt_start.setVisibility(View.GONE);
            }
            txt_freeze_timer.setVisibility(View.VISIBLE);
        }

        if (incidentModel.getStatus().equalsIgnoreCase("resolve")) {
            lin_incident_action.setVisibility(View.GONE);
            lin_freeze_view.setVisibility(View.GONE);
        }


        txt_start_limit.setText(showTimerValue(incidentModel.getDelayStart() * 60000));
        txt_resolved_limit.setText(showTimerValue(incidentModel.getDelayResolve() * 60000));
    }

    private long timerValueCalculation() {

        JobIncidentModel incidentModel = dataAccessObject.getJobIncident(idIntervention);

        int resolveInt = (incidentModel.getStatus().equalsIgnoreCase("own") ||
                incidentModel.getStatus().equalsIgnoreCase("declare")) ?
                0 : incidentModel.getFreezeResolve();

        JobIncidentLogModel incidentLogModel = dataAccessObject.getJobIncidentLog(incidentModel.getIdIntervention(),
                incidentModel.getIdIncident(), "freeze");

        Date lastFreezeDate = new Date();

        if (incidentLogModel != null) {
            lastFreezeDate = getDateIncidentUTC(incidentLogModel.getActionDate());
        }

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(lastFreezeDate);
        cal2.set(Calendar.HOUR_OF_DAY, lastFreezeDate.getHours());
        cal2.set(Calendar.MINUTE, lastFreezeDate.getMinutes());

        Date dtDeclare = getDateIncidentUTC(incidentModel.getDtDeclare());

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(dtDeclare);
        cal1.set(Calendar.HOUR_OF_DAY, dtDeclare.getHours());
        cal1.set(Calendar.MINUTE, dtDeclare.getMinutes());

        Date incidentSubstituteDate = incidentModel.isFlFrozen() ? lastFreezeDate : new Date();

        Calendar cal3 = Calendar.getInstance();
        cal3.setTime(incidentSubstituteDate);
        cal3.set(Calendar.HOUR_OF_DAY, lastFreezeDate.getHours());
        cal3.set(Calendar.MINUTE, lastFreezeDate.getMinutes());


//        txt_start_timer.setText(showTimerValue(incidentModel.getDelayStart() * 60000));
//        txt_resolved_timer.setText(showTimerValue(incidentModel.getDelayResolve() * 60000));

        if (!incidentModel.getStatus().equalsIgnoreCase("own") ||
                incidentModel.getStatus().equalsIgnoreCase("declare")) {
            Date dtStart = getDateIncidentUTC(incidentModel.getDtStart());
            long startTimer = dtStart.getTime() - dtDeclare.getTime() - ((incidentModel.getFreezeStart() * 60000));

            long value = startTimer;
            if (value < 0) {
                value = -value;
            }
            txt_start_timer.setText(showTimerValue(value));
        }


        if (incidentModel.getStatus().equalsIgnoreCase("resolve")) {
            incidentSubstituteDate = getDateIncidentUTC(incidentModel.getDtResolve());
        }

        long sum = incidentSubstituteDate.getTime() - dtDeclare.getTime();

// new changes v53
        long incidentTimeInterval = (sum - (incidentModel.getFreezeStart() * 60000)) - (resolveInt * 60000);

        if (incidentModel.getStatus().equalsIgnoreCase("resolve")) {
            long value = incidentTimeInterval;
            if (value < 0) {
                value = -value;
            }
            txt_resolved_timer.setText(showTimerValue(value));
        }

        return incidentTimeInterval;
    }

    private void freezeTimerValue() {

        txt_incident_time.setTextColor(getResources().getColor(R.color.white));
        progress_incident.setProgressColor(getResources().getColor(R.color.white));

        progress_incident.setVisibility(View.GONE);
        img_view_incident.setVisibility(View.GONE);

        img_pause_incident.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_incident_24dp));
        img_pause_incident.setVisibility(View.VISIBLE);
        txt_incident_time.setTextColor(getResources().getColor(R.color.freeze_txt_color));
        rel_incident.setBackgroundColor(getResources().getColor(R.color.freeze_bg_color));
        txt_resolved_timer.setTextColor(getResources().getColor(R.color.freeze_txt_color));
        txt_resolved_timer.setText(txt_start_timer.getText().toString());

    }


    private void updateProgressColor(long incidentTimeInterval, JobIncidentModel incidentModel) {
        long delayStartOrResolve = (incidentModel.getStatus().equalsIgnoreCase("own") ||
                incidentModel.getStatus().equalsIgnoreCase("declare")) ?
                (incidentModel.getDelayStart() * 60000) : (incidentModel.getDelayResolve() * 60000);

        long percentValue = (delayStartOrResolve / 100) * 80;

        rel_incident.setBackgroundColor(getResources().getColor(R.color.black));

        String status = dataAccessObject.getJobIncidentStatus(incidentModel.getIdIncident());

        if (status.equalsIgnoreCase("resolve")) {
            progress_incident.setVisibility(View.GONE);
            img_pause_incident.setVisibility(View.VISIBLE);
            img_view_incident.setVisibility(View.GONE);
        } else {
            progress_incident.setVisibility(View.VISIBLE);
            img_pause_incident.setVisibility(View.GONE);
            img_view_incident.setVisibility(View.VISIBLE);
        }

        if (incidentModel.getStatus().equalsIgnoreCase("resolve"))
            img_pause_incident.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_white_24dp));

        if (incidentTimeInterval < percentValue) {
            //white
            txt_incident_time.setTextColor(getResources().getColor(R.color.textColorGreen));
            progress_incident.setProgressColor(getResources().getColor(R.color.textColorGreen));
            if (status.equalsIgnoreCase("own") ||
                    status.equalsIgnoreCase("declare")) {
                txt_start_timer.setTextColor(getResources().getColor(R.color.textColorGreen));
            }

            if (status.equalsIgnoreCase("start")) {
                txt_resolved_timer.setTextColor(getResources().getColor(R.color.textColorGreen));
            }

            if (status.equalsIgnoreCase("resolve")) {
                img_pause_incident.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_white_24dp));
                txt_resolved_timer.setTextColor(getResources().getColor(R.color.textColorGreen));
            }

        } else if ((incidentTimeInterval > percentValue) && incidentTimeInterval < delayStartOrResolve) {
            //yellow
            txt_incident_time.setTextColor(getResources().getColor(R.color.incident_yellow_color));
            progress_incident.setProgressColor(getResources().getColor(R.color.incident_yellow_color));

            if (status.equalsIgnoreCase("own") ||
                    status.equalsIgnoreCase("declare")) {
                txt_start_timer.setTextColor(getResources().getColor(R.color.incident_yellow_color));
            }

            if (status.equalsIgnoreCase("start")) {
                txt_resolved_timer.setTextColor(getResources().getColor(R.color.incident_yellow_color));
            }


            if (status.equalsIgnoreCase("resolve")) {
                img_pause_incident.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_yellow_24dp));
                txt_resolved_timer.setTextColor(getResources().getColor(R.color.incident_yellow_color));
            }
        } else if (incidentTimeInterval > delayStartOrResolve) {
            //red
            txt_incident_time.setTextColor(getResources().getColor(R.color.red));
            progress_incident.setProgressColor(getResources().getColor(R.color.red));
            if (status.equalsIgnoreCase("own") ||
                    status.equalsIgnoreCase("declare")) {
                txt_start_timer.setTextColor(getResources().getColor(R.color.red));
            }
            if (status.equalsIgnoreCase("start")) {
//                txt_resolved_timer.setTextColor(getResources().getColor(R.color.red));
                txt_resolved_timer.setTextColor(getResources().getColor(R.color.black));
            }
            if (status.equalsIgnoreCase("resolve")) {
                img_pause_incident.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_red_24dp));
                txt_resolved_timer.setTextColor(getResources().getColor(R.color.red));
            }

        }
    }

    private void runTimerHandler(final long currentTime) {
        final long[] millSecToAdd = {currentTime};

        incidentHandler = new Handler();

        runnableIncident = new Runnable() {

            public void run() {
                millSecToAdd[0] += 1000;

                showCurrentTimerNew(millSecToAdd[0]);

//                txt_incident_time.setText(parseTime(millSecToAdd[0]));
                incidentHandler.postDelayed(this, 1000); // determines the execution interval
            }

        };

        incidentHandler.postDelayed(runnableIncident, 1000);
    }


    public String parseTime(long milliseconds) {

        showCurrentTimerNew(milliseconds);

        return String.format(FORMAT,
                TimeUnit.MILLISECONDS.toHours(milliseconds),
                TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(milliseconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(milliseconds)));

    }

    private void showCurrentTimerNew(long previousTime) {

        incidentTimeInterval = previousTime;

        if (previousTime < 0) {
            previousTime = -previousTime;
        }

        int seconds = (int) (previousTime / 1000);

        try {
            int hours = seconds / 3600;
            int minutes = (seconds / 60) - (hours * 60);
            seconds = seconds - (hours * 3600) - (minutes * 60);
            String minutesString = null;
            String hoursString = null;
            String secondsString = null;

            if (minutes < 10) {
                minutesString = "0" + minutes;
            } else {
                minutesString = minutes + "";
            }

            if (seconds < 10) {
                secondsString = "0" + seconds;
            } else {
                secondsString = seconds + "";
            }

            String timeValue = "";
            if (hours == 0) {
                timeValue = " 00h " + minutesString;
                txt_incident_time.setText(" 00h " + minutesString + ":" + secondsString);
            } else if (hours < 10) {
                hoursString = "0" + hours + "h";
                timeValue = " " + hoursString + " " + minutesString;
                txt_incident_time.setText(" " + hoursString + " " + minutesString + ":" + secondsString);
            } else {
                hoursString = hours + "h";
                timeValue = " " + hoursString + " " + minutesString;
                txt_incident_time.setText(" " + hoursString + " " + minutesString + ":" + secondsString);
            }

            if (incidentModel.getStatus().equalsIgnoreCase("own") ||
                    incidentModel.getStatus().equalsIgnoreCase("declare")) {
                txt_start_timer.setText(timeValue);
            }

            if (incidentModel.getStatus().equalsIgnoreCase("start")) {
//                txt_resolved_timer.setText(timeValue);
                txt_resolved_timer.setText(" -");
            }

            updateProgressColor(incidentTimeInterval, incidentModel);

        } catch (Exception e) {
            Logger.printException(e);

        }

    }

    private String showTimerValue(long previousTime) {

        String result = "";
        int seconds = (int) (previousTime / 1000);

        try {
            int hours = seconds / 3600;
            int minutes = (seconds / 60) - (hours * 60);
            seconds = seconds - (hours * 3600) - (minutes * 60);
            String minutesString = null;
            String hoursString = null;
            String secondsString = null;

            if (minutes < 10) {
                minutesString = "0" + minutes;
            } else {
                minutesString = minutes + "";
            }

            if (seconds < 10) {
                secondsString = "0" + seconds;
            } else {
                secondsString = seconds + "";
            }

            if (hours == 0) {
//                result = (" 00h:" + minutesString + ":" + secondsString);
                result = (" 00h " + minutesString);
            } else if (hours < 10) {
                hoursString = "0" + hours + "h";
//                result = (" " + hoursString + ":" + minutesString + ":" + secondsString);
                result = (" " + hoursString + " " + minutesString);
            } else {
                hoursString = hours + "h";
//                result = (" " + hoursString + ":" + minutesString + ":" + secondsString);
                result = (" " + hoursString + " " + minutesString);
            }

        } catch (Exception e) {
            Logger.printException(e);
            result = "";
        }


        return result;
    }

    private void updateProgressIncident() {

        final boolean[] start = {false};

        CountDownTimer mCountDownTimer;
        final int[] i = {0};

        progress_incident.setProgressValue(i[0]);
        final long millSec = 10000;


        incidentTimeCounter = new MyCountDownTimer(millSec, 1000) {

            public void onTick(long millisUntilFinished) {

                i[0]++;
//                Logger.log("startJobAutomatically", " " + i[0]);
                int value = (int) ((int) i[0] * 100 / (millSec / 1000));
                if (value > 100) {
                    value = 0;
                    i[0] = 0;
                }
                progress_incident.setProgressValue(value);

            }

            public void onFinish() {

                progress_incident.setProgressValue(100);

//                this.setMillisInFuture(millSec); // here we change the millisInFuture of our timer
//                this.start();

            }
        }.start();
    }

    private void logicForSubmitFreezeIncident() {

        int freezeStartOrResolveValue = 0;

        if (edt_commentaire.getText() != null && edt_commentaire.getText().toString().length() > 0) {

            String actionComment = edt_commentaire.getText().toString();

            String status = dataAccessObject.getJobIncidentStatus(incidentModel.getIdIncident());

            boolean isSuccess = dataAccessObject.insertTIncidentLogValues(incidentModel.getIdIncident(),
                    incidentModel.getIdCustomer(), user.getId(), incidentModel.getIdIntervention(),
                    "freeze", actionComment, 0);


            if (isSuccess) {

//                //seperate logic
//                int freezeValue = 0;
//                if (status.equalsIgnoreCase("own")) {
//                    freezeValue = incidentModel.getFreezeStart();
//                } else {
//                    freezeValue = incidentModel.getFreezeResolve();
//                }
//                freezeStartOrResolveValue = freezeStartOrResolveValue + freezeValue;
//new changes v53 freeze timer issue fixes

                boolean result = dataAccessObject.updateTIncidentForFreezeOrUnFreeze(true,
                        incidentModel.getIdIncident(), status, freezeStartOrResolveValue);

                if (result) {
                    Logger.log(TAG, "JOB_INCIDENT  T_INCIDENTS & T_INCIDENTS_LOG insert success");
                    edt_commentaire.setText("");
                    stopProgressTimerIncident();

                    freezeTimerValue();
                    txt_start.setVisibility(View.GONE);
                    txt_resolved.setVisibility(View.GONE);
                    txt_freeze_timer.setVisibility(View.GONE);
                    txt_unfreeze_timer.setVisibility(View.VISIBLE);

                    lin_incident_action.setVisibility(View.VISIBLE);
                    lin_freeze_view.setVisibility(View.GONE);

                    incidentModel = dataAccessObject.getJobIncident(idIntervention);

                    //synch
                    synch();

                } else {
                    Logger.log(TAG, "JOB_INCIDENT  T_INCIDENTS & T_INCIDENTS_LOG insert success");
                }
            } else {
                Logger.log(TAG, "JOB_INCIDENT  T_INCIDENTS_LOG insert failure");
            }

        } else {
            Toast.makeText(this, R.string.enterReason, Toast.LENGTH_SHORT).show();
        }
    }

    private void logicForStartIncident() {

        boolean isSuccess = dataAccessObject.insertTIncidentLogValues(incidentModel.getIdIncident(),
                incidentModel.getIdCustomer(), user.getId(), incidentModel.getIdIntervention(),
                "start", "", 0);

        if (isSuccess) {
            boolean result = dataAccessObject.updateTIncidentForStartOrResolve(true,
                    incidentModel.getIdIncident(), "start");
            if (result) {
                Logger.log(TAG, "JOB_INCIDENT  T_INCIDENTS & T_INCIDENTS_LOG insert success");
                txt_resolved.setVisibility(View.VISIBLE);
                txt_start.setVisibility(View.GONE);
                txt_start_timer.setTextColor(getResources().getColor(R.color.black));
                incidentModel = dataAccessObject.getJobIncident(idIntervention);
                incidentTimeInterval = timerValueCalculation();
                //synch
                synch();
            } else {
                Logger.log(TAG, "JOB_INCIDENT  T_INCIDENTS & T_INCIDENTS_LOG insert success");
            }
        } else {
            Logger.log(TAG, "JOB_INCIDENT  T_INCIDENTS_LOG insert failure");
        }
    }

    private void logicForResolvedIncident() {

        boolean isSuccess = dataAccessObject.insertTIncidentLogValues(incidentModel.getIdIncident(),
                incidentModel.getIdCustomer(), user.getId(), incidentModel.getIdIntervention(),
                "resolve", "", 0);

        if (isSuccess) {
            boolean result = dataAccessObject.updateTIncidentForStartOrResolve(true,
                    incidentModel.getIdIncident(), "resolve");
            if (result) {
                Logger.log(TAG, "JOB_INCIDENT  T_INCIDENTS & T_INCIDENTS_LOG insert success");
                lin_freeze_view.setVisibility(View.GONE);
                lin_incident_action.setVisibility(View.GONE);
                stopProgressTimerIncident();
                updateProgressColor(incidentTimeInterval, incidentModel);
//                txt_resolved_timer.setTextColor(getResources().getColor(R.color.black));
//new changes v53 freeze timer issue fixes

                long value = incidentTimeInterval;
                if (value < 0) {
                    value = -value;
                }
                txt_resolved_timer.setText(showTimerValue(value));

                incidentModel = dataAccessObject.getJobIncident(idIntervention);
                //synch
                synch();

            } else {
                Logger.log(TAG, "JOB_INCIDENT  T_INCIDENTS & T_INCIDENTS_LOG insert success");
            }
        } else {
            Logger.log(TAG, "JOB_INCIDENT  T_INCIDENTS_LOG insert failure");
        }
    }

    private void logicForUnfreezeIncident() {

        JobIncidentLogModel incidentLogModel = dataAccessObject.getJobIncidentLog(incidentModel.getIdIntervention(),
                incidentModel.getIdIncident(), "freeze");

        if (incidentLogModel != null) {

            Date actionDate = new Date();

            if (incidentLogModel != null) {
                actionDate = getDateIncidentUTC(incidentLogModel.getActionDate());
                Logger.log(TAG, "JOB INCIDENT VALUE actionDate value is====>" + actionDate);
            }

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(actionDate);
            cal2.set(Calendar.HOUR_OF_DAY, actionDate.getHours());
            cal2.set(Calendar.MINUTE, actionDate.getMinutes());


            long actionDuration = new Date().getTime() - actionDate.getTime();
            int actionParticularDuration = (int) (actionDuration / 1000) / 60;

            if (incidentModel.getStatus().equalsIgnoreCase("own") ||
                    incidentModel.getStatus().equalsIgnoreCase("declare")) {
                actionDuration = actionDuration + (incidentModel.getFreezeStart() * 60000);
            } else if (incidentModel.getStatus().equalsIgnoreCase("start")) {
//                actionDuration = actionDuration + (incidentModel.getFreezeStart() * 60000) +
//                        (incidentModel.getFreezeResolve() * 60000);
//new changes v53 freeze timer issue fixes

                actionDuration = actionDuration + (incidentModel.getFreezeResolve() * 60000);
            }

            int actionDurationMinutes = (int) (actionDuration / 1000) / 60;

            Logger.log(TAG, "JOB INCIDENT VALUE actionDurationMinutes value is====>" + actionDurationMinutes);

            String status = dataAccessObject.getJobIncidentStatus(incidentModel.getIdIncident());

            boolean isSuccess = dataAccessObject.insertTIncidentLogValues(incidentModel.getIdIncident(),
                    incidentModel.getIdCustomer(), user.getId(), incidentModel.getIdIntervention(),
                    "unfreeze", "", actionParticularDuration);


            if (isSuccess) {
//new changes v53 freeze timer issue fixes
                //seperate logic
//                int freezeValue = 0;
//                if (status.equalsIgnoreCase("own")||status.equalsIgnoreCase("declare")) {
//                    freezeValue = incidentModel.getFreezeStart();
//                } else {
//                    freezeValue = incidentModel.getFreezeResolve();
//                }
//                actionDurationMinutes = actionDurationMinutes + freezeValue;

                boolean result = dataAccessObject.updateTIncidentForFreezeOrUnFreeze(false,
                        incidentModel.getIdIncident(), status, actionDurationMinutes);

                if (result) {
                    Logger.log(TAG, "JOB_INCIDENT  T_INCIDENTS & T_INCIDENTS_LOG insert success");

                    txt_start.setVisibility(View.GONE);
                    txt_freeze_timer.setVisibility(View.GONE);
                    txt_unfreeze_timer.setVisibility(View.VISIBLE);
                    txt_resolved_timer.setText(" -");
                    txt_resolved_timer.setTextColor(getResources().getColor(R.color.black));

                    long incidentTimeInterval = timerValueCalculation();
                    runTimerHandler(incidentTimeInterval);
                    updateProgressColor(incidentTimeInterval, incidentModel);

                    if (dataAccessObject.getJobIncidentStatus(incidentModel.getIdIncident()).equalsIgnoreCase("own") ||
                            dataAccessObject.getJobIncidentStatus(incidentModel.getIdIncident()).equalsIgnoreCase("declare")) {
                        txt_start.setVisibility(View.VISIBLE);
                        txt_resolved.setVisibility(View.GONE);
                    } else {
                        txt_start.setVisibility(View.GONE);
                        txt_resolved.setVisibility(View.VISIBLE);
                    }
                    txt_freeze_timer.setVisibility(View.VISIBLE);
                    txt_unfreeze_timer.setVisibility(View.GONE);

                    incidentModel = dataAccessObject.getJobIncident(idIntervention);

                    //synch
                    synch();

                } else {
                    Logger.log(TAG, "JOB_INCIDENT  T_INCIDENTS & T_INCIDENTS_LOG insert success");
                }
            } else {
                Logger.log(TAG, "JOB_INCIDENT  T_INCIDENTS_LOG insert failure");
            }

        }
    }


    private void startJobAutomatically() {
        Logger.log("startJobAutomatically", " " + status);
        Logger.log("startJobAutomatically", "job intervention id is :" + idIntervention);

//        String idIntervOld = dataAccessObject.getStartedJobId();
//        if (idIntervOld != null && idIntervOld.trim().length() > 0)
//            if (dataAccessObject.updateStatutInterv(4, idIntervOld))
//                dataAccessObject.setTimeClotIntervention(idIntervOld, idUser + "", "");

        new StartJobAsyncTask(JobDetails.this).execute();

//        EventBus.getDefault().post(new JobStartResumeEvent(KEYS.JobDetails.KEY_START_JOB));


        /** cancel the inactivity alarm */
        ClockInOutUtil.cancelAlarmForTimeOut(JobDetails.this);
    }

    /**
     * Checking Job status and setting the UI for Job
     *
     * @param status - Status of the Job
     */
    private void checkJobStatusAndSettingUI(int status, boolean isActivityStarted, String activityName) {
        if (!isActivityStarted) {
            if (status == KEYS.JObDetail.JOB_NOT_STARTED1 || status == KEYS.JObDetail.JOB_NOT_STARTED2) {
//                txtJobStatus.setText(getResources().getString(R.string.tt_start));
                txtJobStatus.setText(getResources().getString(R.string.textStartLable));
                txtStopJobView.setVisibility(View.VISIBLE);
                txtStopJobView.setText(getString(R.string.fa_play));
            } else if (status == KEYS.JObDetail.JOB__SUSPENDED) {
                txtJobStatus.setText(getResources().getString(R.string.txt_resume));
                txtStopJobView.setVisibility(View.VISIBLE);
                txtStopJobView.setText(getString(R.string.fa_play));
            } else if (status == KEYS.JObDetail.JOB__STARTED) {
                txtJobStatus.setText(getJobNumber());
                txtStopJobView.setVisibility(View.VISIBLE);
                txtStopJobView.setText(getString(R.string.fa_stop));
            } else if (status == KEYS.JObDetail.JOB__COMPLETE) {
                txtJobStatus.setText(getJobNumber());
                txtStopJobView.setVisibility(View.INVISIBLE);
            } else {
                txtJobStatus.setText(getJobNumber());
                txtStopJobView.setVisibility(View.INVISIBLE);
            }
        } else {
            txtJobStatus.setText(activityName);
            txtStopJobView.setVisibility(View.VISIBLE);
        }
    }

    public void checkClockModeAndSettingLayout(boolean isClockIn) {
        if (isClockIn) {
            txtClockMode.setBackgroundColor(getResources().getColor(R.color.green_color));
        } else {
            txtClockMode.setBackgroundColor(getResources().getColor(R.color.red_color));
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

        duree = dataAccessObject.getJobDuration(idIntervention);

        user = dataAccessObject.getUser();

        isStartJob = bundle.getBoolean(KEYS.CurrentJobs.IDSTARTJOB, false);

        if (gestionAcces != null && gestionAcces.getFlCreateUpdateInvoiceQuotation() > 0) {
            flCreateUpdateInvoiceQuotation = gestionAcces.getFlCreateUpdateInvoiceQuotation();
        }


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

        if (haveCF) {
            isAdditionalInformationAvaliable = true;
        }

        if (user != null) {
            if (!((idUser + "").equals(user.getId() + ""))) {
//            startJobIb.setVisibility(View.GONE);
//            viewSwitcher.setVisibility(View.GONE);
//            chronometer.setVisibility(View.GONE);
                layBottomBar.setVisibility(View.GONE);
                status = KEYS.CurrentJobs.JOB__COMPLETE;
            } else {
                if (status == KEYS.JObDetail.JOB__STARTED) {

//                viewSwitcher.setDisplayedChild(1);
                    assert idIntervention != null;
                    String dateString = dataAccessObject
                            .getJobResumedTimeInJobDetails(idIntervention);
                    if (dateString != null) {
//                        SimpleDateFormat dateFormat = new SimpleDateFormat(
//                                "yyyy-MM-dd HH:mm:ss", Locale.US);
//                        try {
//                            long timeWhenJobWasStarted = dateFormat.parse(dateString)
//                                    .getTime();
//                            long previousTimeToBeSubtracted = dataAccessObject
//                                    .suspendedTimeDiffrence(idIntervention);
//
//                            startTimer(timeWhenJobWasStarted
//                                    - previousTimeToBeSubtracted);
//                        } catch (ParseException e) {
//                            Logger.printException(e);
//                        }

                        //V54 changes
                        startTimer(getjobStartStopTimeValue());
                    }
                }
                if (status == KEYS.JObDetail.JOB__SUSPENDED) {

//                chronometer.setOnChronometerTickListener(null);
//                chronometer
//                        .setOnChronometerTickListener(onChronometerTickListenerSuspend);

//                    //v53 changes
//                    previousTime = dataAccessObject
//                            .suspendedTimeDiffrence(idIntervention);

                    //v54 changes
                    previousTime = getjobStartStopTimeValue();


//                showCurrentTimer(previousTime);

//                viewSwitcher.setDisplayedChild(1);
//                pauseIb.setImageResource(R.drawable.play_btn);
//                stopIb.setVisibility(View.INVISIBLE);
                }

                if ((status == KEYS.JObDetail.JOB__COMPLETE)
                        || (status == KEYS.JObDetail.DEDLINE_EXCEEDED)) {

//                startJobIb.setVisibility(View.GONE);
//                viewSwitcher.setVisibility(View.GONE);
//                chronometer.setVisibility(View.GONE);

                }
            }
        }

        //checking time
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        String s = sdf.format(System.currentTimeMillis());


//        String startTime = dataAccessObject.getStartTimeInJobDetails(idIntervention);
//
//        if (startTime != null) {
//            long checkTime = dataAccessObject.getJobSuspendTime(s, startTime);
//
//            if (checkTime <= 0) {
//                try {
//                    Date finalDate = sdf.parse(startTime);
//                    Calendar cal = Calendar.getInstance();
//                    cal.setTime(finalDate);
//                    cal.add(Calendar.MINUTE, 1);
//                    s = sdf.format(cal.getTime());
//                } catch (ParseException e) {
//                    s = sdf.format(System.currentTimeMillis());
//                    e.printStackTrace();
//                }
//            }
//        }

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
            case R.id.home:
                break;
            case R.id.action_decline:
                if (status == KEYS.JObDetail.JOB_NOT_STARTED1
                        || status == KEYS.JObDetail.JOB_NOT_STARTED2) {
                    rejeterIntervention();
                }
                break;

//            case R.id.action_job_more:
//                if (status == KEYS.JObDetail.JOB_NOT_STARTED1
//                        || status == KEYS.JObDetail.JOB_NOT_STARTED2) {
//                    RescheduleJobMenuDialog mBottomsheet = new RescheduleJobMenuDialog();
//                    mBottomsheet.show(this.getSupportFragmentManager(), mBottomsheet.getTag());
//                }
//                break;

            case R.id.action_reschedule:
                if (status == KEYS.JObDetail.JOB_NOT_STARTED1
                        || status == KEYS.JObDetail.JOB_NOT_STARTED2) {
//                    replanifIntervention();
                    replanifInterventionNew();
                }
                break;
            case R.id.action_duplication:

//                Calendar cal = Calendar.getInstance();
//                int milisecond = ((cal.get(Calendar.HOUR_OF_DAY) * 3600000) + (cal
//                        .get(Calendar.MINUTE) * 60000));
//
//                int milisecondMaxtime = ((23 * 3600000) + (40 * 60000));
//                if (milisecond <= milisecondMaxtime) {
//
//                    Intent intent = new Intent(JobDetails.this,
//                            AddDuplicateNewJob.class);
//
//                    intent.putExtra(KEYS.ClientDetial.SITE_NAME, nomSite);
//                    intent.putExtra(KEYS.ClientDetial.EQUIPMENTS_NAME,
//                            nomEquipement);
//                    intent.putExtra(KEYS.JObDetail.ID, idIntervention);
//
//                    startActivity(intent);
//
//                } else {
//
//                    DialogUtils.showInfoDialog(JobDetails.this,
//                            JobDetails.this.getString(R.string.textNoJobCreate));
//
//                }

                //Time restriction has been removed
                Intent intent = new Intent(JobDetails.this,
                        AddDuplicateNewJob.class);

                intent.putExtra(KEYS.ClientDetial.SITE_NAME, nomSite);
                intent.putExtra(KEYS.ClientDetial.EQUIPMENTS_NAME,
                        nomEquipement);
                intent.putExtra(KEYS.JObDetail.ID, idIntervention);

                startActivity(intent);

                // duplicationInterv();
                break;
            case android.R.id.home:
                if (!isTextDialogOpened) {
                    NavUtils.navigateUpFromSameTask(this);
                } else {
                    EventBus.getDefault().post(new CloseTextDialogEvent());
                }
                break;
            case R.id.action_save:
                EventBus.getDefault().post(new SaveTextDialogEvent());
                break;
            default:
                break;
        }
//        }else {
////            isTextDialogOpened = false;
//            EventBus.getDefault().post(new CloseTextDialogEvent());
//        }
        return true;
    }

    public int getStatus() {
        return status;
    }

    /**
     * Rejeter intervention.
     */
    public void rejeterIntervention() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.txt_confirm)
                .setMessage(R.string.txt_confirm)
                .setCancelable(false)
                .setPositiveButton(R.string.textYesLable,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dataAccessObject
                                        .rejectIntervention(idIntervention);

////                                //v54 Changes
//                                notiList = new ArrayList<>();
//                                NotificationItem item;
//                                item = new NotificationItem(idIntervention, -1);

//                                notiList.add(item);

                                notificationEventList(idIntervention, -1);

                                setResult(35);
                                closeActivity = true;
                                synch();
                            }
                        })
                .setNegativeButton(R.string.textNoLable,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
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

        if (status == KEYS.JObDetail.JOB__COMPLETE) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.job_discription_menu, menu);

//            menu.removeItem(R.id.action_job_more);
            menu.removeItem(R.id.action_reschedule);
            menu.removeItem(R.id.action_decline);

            if (dataAccessObject.getAddIntervFlag() != 1) {
                menu.removeItem(R.id.action_duplication);

            }
            if (!isAdditionalInformationAvaliable) {
                menu.removeItem(R.id.action_information);
            }
            this.menu = menu;

            return super.onCreateOptionsMenu(menu);
        }

        if (!((idUser + "").equals(user.getId() + ""))) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.job_discription_menu, menu);

//            menu.removeItem(R.id.action_job_more);
            menu.removeItem(R.id.action_reschedule);
            menu.removeItem(R.id.action_decline);

            if (dataAccessObject.getAddIntervFlag() != 1) {
                menu.removeItem(R.id.action_duplication);

            }
            if (!isAdditionalInformationAvaliable) {
                menu.removeItem(R.id.action_information);
            }
            this.menu = menu;

            return super.onCreateOptionsMenu(menu);

        }

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.job_discription_menu, menu);

        if (gestionAcces != null && gestionAcces.getOptionReplanif() != 1) {

            menu.removeItem(R.id.action_reschedule);
        }
        if (gestionAcces != null && gestionAcces.getOptionReject() != 1) {
            menu.removeItem(R.id.action_decline);
        }

        if (dataAccessObject.getAddIntervFlag() != 1) {
            menu.removeItem(R.id.action_duplication);
        }

        this.menu = menu;
        if (!isAdditionalInformationAvaliable) {
            menu.removeItem(R.id.action_information);
        }

        if ((status != KEYS.JObDetail.JOB_NOT_STARTED1)
                && (status != KEYS.JObDetail.JOB_NOT_STARTED2)) {
            menu.removeItem(R.id.action_reschedule);
            menu.removeItem(R.id.action_decline);
//            menu.removeItem(R.id.action_job_more);
        }

        String meetingDate = dataAccessObject.getDateMeetingForIntervention(idIntervention);
        if (meetingDate != null && meetingDate.trim().length() > 0) {
            menu.removeItem(R.id.action_reschedule);
        }

        // int flagActionInformation = dao.getCustomFields(idIntervention,
        // idClient, idSite, idEquipement).size();
        boolean haveCF;
        if (dataAccessObject.getCFForIntervention(idIntervention).size() != 0
                || dataAccessObject.getCFForClient(idClient).size() != 0
                || dataAccessObject.getCFForSite(idSite).size() != 0
                || dataAccessObject.getCFForEquip(idEquipement).size() != 0) {
            haveCF = true;
        } else {
            haveCF = false;
        }

        if (!haveCF)
            menu.removeItem(R.id.action_information);

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

        ;

    };

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

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

                currentFragment = new DiscrptionJobDetailFragment();
                currentFragment.setArguments(bundle);
            } else if (arg0 == 1) {

                Bundle bundle = new Bundle();
                int a = Integer.parseInt(idModel);
                bundle.putInt(KEYS.CurrentJobs.ID_MODEL, a);
                bundle.putString(KEYS.CurrentJobs.ID, idIntervention);
                bundle.putInt(KEYS.CurrentJobs.CD_STATUS, status);
                bundle.putInt(KEYS.CurrentJobs.ID_USER, idUser);
                bundle.putString(KEYS.JObDetail.DT_CREATED, dtCreated);
                currentFragment = new ReportsFragment();
                currentFragment.setArguments(bundle);
            } else if (arg0 == 2) {

                Bundle bundle = new Bundle();

                bundle.putString("id_interv", idIntervention);
                bundle.putInt("cd_statut", status);
                bundle.putInt("id_user", idUser);
                bundle.putInt("idClient", idClient);
                bundle.putInt(KEYS.JObDetail.IDSITE, idSite);
                //new changes

//                currentFragment = new InvoicingFragment();
//                currentFragment.setArguments(bundle);

                currentFragment = new CatalougeJobDetailFragment();
                currentFragment.setArguments(bundle);

            } else if (arg0 == 3) {

                Bundle bundle = new Bundle();

                bundle.putString("id_interv", idIntervention);
                bundle.putInt("cd_statut", status);
                bundle.putInt("id_user", idUser);
                bundle.putInt("idClient", idClient);
                bundle.putInt(KEYS.JObDetail.IDSITE, idSite);
                currentFragment = new InvoicingFragmentNew();
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

            if (id == R.id.txt_stopview) {
                txtJobStatus.performClick();
            } else if (id == R.id.txtJobStatus) {
                openActivityJobDialogs();
            } else if (id == R.id.txt_clock_mode) {
                openActivityJobClockDialogs();
            } else if (id == R.id.rel_incident) { //new changes V53
                rel_incident_view.setVisibility(View.VISIBLE);
                if (incidentModel.getStatus().equalsIgnoreCase("resolve")) {
                    lin_incident_action.setVisibility(View.GONE);
                    lin_freeze_view.setVisibility(View.GONE);
                } else {
                    lin_incident_action.setVisibility(View.VISIBLE);
                    lin_freeze_view.setVisibility(View.GONE);
                }
            } else if (id == R.id.txt_freeze_timer) {
                lin_incident_action.setVisibility(View.GONE);
                lin_freeze_view.setVisibility(View.VISIBLE);
                edt_commentaire.setText("");
            } else if (id == R.id.txt_unfreeze_timer) {
                logicForUnfreezeIncident();
            } else if (id == R.id.txt_start) {
                logicForStartIncident();
            } else if (id == R.id.txt_resolved) {
                logicForResolvedIncident();
            } else if (id == R.id.rel_close) {  // new changes V53
                if (incidentModel.getStatus().equalsIgnoreCase("resolve")) {
                    lin_incident_action.setVisibility(View.GONE);
                    lin_freeze_view.setVisibility(View.GONE);
                } else {
                    lin_incident_action.setVisibility(View.VISIBLE);
                    lin_freeze_view.setVisibility(View.GONE);
                    edt_commentaire.setText("");
                }
                rel_incident_view.setVisibility(View.GONE);
            } else if (id == R.id.txt_submit_freeze) {
                logicForSubmitFreezeIncident();
            }

        }

    };


    public boolean checkInterventionAndGettingIdInterv() {
        int idUserTemp = dataAccessObject.getUser().getId();
        String idInterventionTemp = dataAccessObject.getStartedInterventionIDToPauseStop(idUserTemp);
        return idInterventionTemp != null;
    }

    public String parseTimeNew(long milliseconds) {

        final String FORMAT = "%02d:%02d:%02d";

        return String.format(FORMAT,
                TimeUnit.MILLISECONDS.toHours(milliseconds),
                TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(
                        TimeUnit.MILLISECONDS.toHours(milliseconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliseconds) - TimeUnit.MINUTES.toSeconds(
                        TimeUnit.MILLISECONDS.toMinutes(milliseconds)));

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


        if (status == KEYS.JObDetail.JOB_NOT_STARTED1 || status == KEYS.JObDetail.JOB_NOT_STARTED2) {
            String dateString = dataAccessObject
                    .getJobResumedTimeInJobDetails(idIntervention);
            Logger.log(TAG, "JOB CHECK TIME START END  DEBUT_TIME====>" + dateString);

            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss", Locale.US);

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


//            String dateStart = dataAccessObject
//                    .getJobResumedTimeInJobDetails(idIntervention);
//            String dateFinal = dataAccessObject.getJobFinalTimeInJobDetails(idIntervention);
//
//            Long timeDiff = dataAccessObject.getJobSuspendTime(dateFinal,
//                    dateStart);
//            Logger.log(TAG, "JOB CHECK TIME START END  DT_FIN====>" + dateFinal);
//            Logger.log(TAG, "JOB CHECK TIME START END  DEBUT_TIME====>" + dateStart);
//            startJobTimer=startJobTimer-timeDiff;

            JobStartResumeDialog.newInstance(KEYS.JobDetails.KEY_START_JOB, getJobNumber(), 0).show(getSupportFragmentManager(), "");
        }

        if (status == KEYS.JObDetail.JOB__STARTED) {

//            String dateString = dataAccessObject
//                    .getJobResumedTimeInJobDetails(idIntervention);
//            SimpleDateFormat dateFormat = new SimpleDateFormat(
//                    "yyyy-MM-dd HH:mm:ss", Locale.US);
//            Logger.log(TAG, "JOB CHECK TIME START END  DEBUT_TIME====>" + dateString);
//
//            long startJobTimer = 0;
//            try {
//                long timeWhenJobWasStarted = dateFormat.parse(dateString)
//                        .getTime();
//
//                long previousTimeToBeSubtracted = dataAccessObject
//                        .suspendedTimeDiffrence(idIntervention);
//
//                startJobTimer = timeWhenJobWasStarted
//                        - previousTimeToBeSubtracted;
//
//
//            } catch (Exception e) {
//                Logger.printException(e);
//            }
//            Logger.log(TAG, "JOB CHECK TIME START END  DEBUT_TIME====>" + dateString);
            //V54 Changes
            long timeUpdatedValue = getjobStartStopTimeValue();

            JobPauseFinishDialog.newInstance(getJobNumber(), timeUpdatedValue, isAllMandatoryFieldsAreFilled()).show(getSupportFragmentManager(), "");

            //todo check and uncomment
//            new IsMandatoryFilledAsyncTask(this, status, timeUpdatedValue).execute();

        }

        if (status == KEYS.JObDetail.JOB__SUSPENDED) {

//                chronometer.setOnChronometerTickListener(null);
//                chronometer
//                        .setOnChronometerTickListener(onChronometerTickListenerSuspend);

            //V53 Changes
//            String dateStart = dataAccessObject
//                    .getJobResumedTimeInJobDetails(idIntervention);
//            String dateFinal = dataAccessObject.getJobFinalTimeInJobDetails(idIntervention);
//
//            Long timeDiff = dataAccessObject.getJobSuspendTime(dateFinal,
//                    dateStart);
//            Logger.log(TAG, "JOB CHECK TIME START END  DT_FIN====>" + dateFinal);
//            Logger.log(TAG, "JOB CHECK TIME START END  DEBUT_TIME====>" + dateStart);
//
//            previousTime = dataAccessObject
//                    .suspendedTimeDiffrence(idIntervention);
//
//            previousTime = previousTime - timeDiff;

            previousTime = getjobStartStopTimeValue();

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
                        "yyyy-MM-dd HH:mm:ss", Locale.US);

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

//                String dateString = dataAccessObject
//                        .getJobResumedTimeInJobDetails(idIntervention);
//                SimpleDateFormat dateFormat = new SimpleDateFormat(
//                        "yyyy-MM-dd HH:mm:ss", Locale.US);
//
//                long startJobTimer = 0;
//                try {
//                    long timeWhenJobWasStarted = dateFormat.parse(dateString)
//                            .getTime();
//                    long previousTimeToBeSubtracted = dataAccessObject
//                            .suspendedTimeDiffrence(idIntervention);
//
//                    startJobTimer = timeWhenJobWasStarted
//                            - previousTimeToBeSubtracted;
//                } catch (ParseException e) {
//                    Logger.printException(e);
//                }

                //V54 Changes
                long timeUpdatedValue = getjobStartStopTimeValue();


                JobPauseFinishDialog.newInstance(getJobNumber(), timeUpdatedValue, isAllMandatoryFieldsAreFilled()).show(getSupportFragmentManager(), "");

                //todo check and uncomment
//                new IsMandatoryFilledAsyncTask(this, status, timeUpdatedValue);

            }

            if (status == KEYS.JObDetail.JOB__SUSPENDED) {

//                chronometer.setOnChronometerTickListener(null);
//                chronometer
//                        .setOnChronometerTickListener(onChronometerTickListenerSuspend);

//                previousTime = dataAccessObject
//                        .suspendedTimeDiffrence(idIntervention);

                previousTime = getjobStartStopTimeValue();

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


    // ---------------------------------------------V_45--CHANGES--------------------------------------------------

    /**
     * Check for the unavailability which does not have an end date (i.e already
     * started) and update the end date of that unavailability to current date.
     */
    private void updateEndDateOfPreviousActivity() {

        Vector<Conge> vectConge = new Vector<Conge>();
        vectConge = dataAccessObject.getConge();
        Enumeration<Conge> enindisp = vectConge.elements();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        String currentDate = sdf.format(cal.getTime());
        while (enindisp.hasMoreElements()) {
            Conge indisp = enindisp.nextElement();
            if (indisp.getDtFin() == null) {
                dataAccessObject.updateUnavailabilityEndDate(
                        indisp.getIdConge(), currentDate);
            }
        }
    }

    private void updateEndDateOfScheduleddActivity() {
        Vector<Conge> vectConge = new Vector<Conge>();
        vectConge = dataAccessObject.getConge();
        Enumeration<Conge> enindisp = vectConge.elements();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        String currentDateStr = sdf.format(cal.getTime());

        while (enindisp.hasMoreElements()) {
            Conge indisp = enindisp.nextElement();

            try {
                Date currentDate = sdf.parse(currentDateStr);
                Date fromDate = sdf.parse(indisp.getDtDebut());
                Date endDate = sdf.parse(indisp.getDtFin());

                if (currentDate.compareTo(fromDate) > 0
                        && currentDate.compareTo(endDate) < 0) {
                    dataAccessObject.updateUnavailabilityEndDate(
                            indisp.getIdConge(), currentDateStr);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }


    // ---------------------------------------------v_45--CHANGES--------------------------------------------------

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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
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
                        idUser, status, tempId);
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
                if (menu != null) {
                    menu.removeItem(R.id.action_reschedule);
                    menu.removeItem(R.id.action_decline);
                }
            }
        });


//        updateEndDateOfPreviousActivity();

//		updateEndDateOfScheduleddActivity();

        //todo new 50 changes

        //for stopping the previous started activities
        updateEndDateOfStartedActivities();
        updateEndDateofTravelActivities();

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
//            String dateString = dataAccessObject
//                    .getJobResumedTimeInJobDetails(idIntervention);
//            SimpleDateFormat dateFormat = new SimpleDateFormat(
//                    "yyyy-MM-dd HH:mm:ss", Locale.US);
//            try {
//                long timeWhenJobWasStarted = dateFormat.parse(dateString)
//                        .getTime();
//                long previousTimeToBeSubtracted = dataAccessObject
//                        .suspendedTimeDiffrence(idIntervention);
//
//                startTimer(timeWhenJobWasStarted
//                        - previousTimeToBeSubtracted);
//            } catch (ParseException e) {
//                Logger.printException(e);
//            }

            startTimer(getjobStartStopTimeValue());

        }
    }

    private long getjobStartStopTimeValue() {
        //V54 Changes
        long timeUpdatedValue = 0;

        try {

            ArrayList<Long> list = dataAccessObject
                    .jobTimeDiffrence(idIntervention);
            Collections.reverse(list);
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    Logger.log(TAG, "JOB DETAIL TIME DT_FIN DT_DEBUT" +
                            " VALUE VALUE====>" + parseTimeNew(list.get(i)));

                    timeUpdatedValue = timeUpdatedValue + list.get(i);

                }
            }

        } catch (Exception e) {
            Logger.printException(e);
        }

        Logger.log("TAG", "JOB CHECK TIME START END PARSED" +
                " TIME VALUES IS====>" + parseTimeNew(timeUpdatedValue));

        return timeUpdatedValue;

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
            Toast.makeText(JobDetails.this, R.string.msg_saisie_oblig,
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
                    "yyyy-MM-dd HH:mm:ss", Locale.US);
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

        if (SynchroteamUitls.isNetworkAvailable(JobDetails.this)) {
            if (progressDSynch == null)
                progressDSynch = ProgressDialog.show(JobDetails.this,
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

                        //Logic for hitting notification after sync
                        if (notiList != null && notiList.size() > 0)
                            for (int i = 0; i < notiList.size(); i++)
                                notificationEventList(notiList.get(i).getIdIntervention(),
                                        notiList.get(i).getStatus());

                        l = new ArrayList<>();
                        l = RoomDBSingleTone.instance(JobDetails.this).roomDao().getAllNotificationEventModels();
                        Log.e("taf", "the list is >>>>  "+ l.size());

                        if (l != null && l.size()>0){
                            for (int i=0;i<l.size();i++){
                                JSONObject jsonObj = new JSONObject(l.get(i).value);

                                hitNotificationEventService(jsonObj.getString("IdJob"),jsonObj.getInt("IdCustomer"),jsonObj.getInt("JobStatus"),
                                        l.get(i).id);
                                Log.e("JSON","THE JSON IS >>>>>" + jsonObj.getString("IdCustomer"));

                            }
                        }

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

            //save in local DB
            if (notiList != null && notiList.size() > 0)
                for (int i = 0; i < notiList.size(); i++)
                    notificationEventList(notiList.get(i).getIdIntervention(),
                            notiList.get(i).getStatus());

            EventBus.getDefault().post(new UpdateDataBaseEvent());
            if (closeActivity) {
                finish();
            } else {
                SynchroteamUitls.showToastMessage(JobDetails.this);
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
                Toast.makeText(JobDetails.this,
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
                Toast.makeText(JobDetails.this,
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
                if (!JobDetails.this.isFinishing()) {
                    showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));
                }
                /*updated code*/
            }

        }
    };


    /**
     * Show error dialog
     */
    protected void showErrMsgDialog(String errMsg) {

        ErrorDialog errDialog = new ErrorDialog(JobDetails.this, errMsg);

        errDialog.show();
    }

    /**
     * For showing the synchronization failure messages
     */
    protected void showSyncFailureMsgDialog(String syncFailureMsg) {

        if (!JobDetails.this.isFinishing()) {
            SynchronizationErrorDialog synchronizationErrorDialog = new SynchronizationErrorDialog
                    (JobDetails.this, syncFailureMsg, new SynchronizationErrorDialog
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
                JobDetails.this, user.getLogin());

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
                                    JobDetails.this.finish();
                                }

                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // v48 changes
        // checking if activity is started
//        Conge unAvailabilityDetail = checkUnAvailabilityStarted();
//        if (unAvailabilityDetail != null) {
//            checkJobStatusAndSettingUI(0, true, unAvailabilityDetail.getNomTypeConge());
//        } else {
//            checkJobStatusAndSettingUI(status, false, null);
//        }


        Log.e("Job Details", "Onresume is  called");

        new OnResumeAsynTask().execute();
//        // show / hide the clock in out when the user have clock in out function
//        checkClockModeAvailabilityAndSettingLayout(dataAccessObject.checkIsClockInAvailable(user.getId()));
//
//        //--------------------------------------- v48 ---------------------------------------------
//        //new changes
//        checkJobStatusAndSettingUI(status, false, null);
//
//
//        //New changes
//        DateChecker.checkDateAndNavigate(JobDetails.this, dataAccessObject); // this changed into getApplicationContext()
//
//        if (!JobDetails.this.isFinishing() && context != null) {
//
//            ((SyncroTeamApplication) context)
//                    .setSyncroteamAppLive(true);
//            ((SyncroTeamApplication) context)
//                    .setSyncroteamActivityInstance(this);
//        }
//        // String deviceDateFormat=getDateFormat(this);
//        // String deviceTimeFormat=getTimeFormat(this);
//
//
//        String dateFormatString = Settings.System.getString(
//                this.getContentResolver(),
//                Settings.System.DATE_FORMAT);
//        String timeFormatString = Settings.System.getString(
//                this.getContentResolver(),
//                Settings.System.TIME_12_24);
//
//        if ((!TextUtils.isEmpty(dateFormatString))
//                && (!TextUtils.isEmpty(timeFormatString))) {
//            if (!dateFormatString.equals(SharedPref.getDateFormat(this))) {
//                SharedPref.setDateFormat(this);
//                SharedPref.setTimeFormat(this);
//                EventBus.getDefault().post(new UpdateJobDetailUi());
//
//            } else if (!timeFormatString.equals(SharedPref.getTimeFormat(this))) {
//
//                SharedPref.setDateFormat(this);
//                SharedPref.setTimeFormat(this);
//                EventBus.getDefault().post(new UpdateJobDetailUi());
//
//            }
//
//        }

    }

    public void checkClockModeAvailabilityAndSettingLayout(boolean isClockModeAvailable) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isClockModeAvailable) {
                    txtClockMode.setVisibility(View.VISIBLE);
                    // check clocked in or not
                    Conge vectCongeClockIn = checkClockedIn();
                    if (vectCongeClockIn != null) {
                        txtClockMode.setBackgroundColor(getResources().getColor(R.color.green_color));
                    } else {
                        txtClockMode.setBackgroundColor(getResources().getColor(R.color.red_color));
                    }
                } else {
                    txtClockMode.setVisibility(View.GONE);
                }
            }
        });
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
        if (JobDetails.this != null && !JobDetails.this.isFinishing() && context != null) {  //&& (SyncroTeamApplication) getApplicationContext() != null
            ((SyncroTeamApplication) context)
                    .setSyncroteamAppLive(false);
            ((SyncroTeamApplication) context)
                    .setSyncroteamActivityInstance(null);
        }
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
     * Replanif intervention.
     */
    @SuppressWarnings("deprecation")
    public void replanifIntervention() {
        initDates();
        factory = LayoutInflater.from(getApplicationContext());
        alertDialogView1 = factory.inflate(R.layout.replanifenetre, null);

        TextView tv = (TextView) alertDialogView1.findViewById(R.id.repDate);

        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
        // Date date=getDateFromDbFormat(mDate1);
        Date date = new Date(mAnnee, mMois, mJour, mH1, mMin1);
        tv.setText(df.format(date));
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showDatePicker();
            }

        });
        tv = (TextView) alertDialogView1.findViewById(R.id.repHeurDeb);
        Format writeFormat = android.text.format.DateFormat.getTimeFormat(this);
        // date=getDateFromDbFormat(mDate1);
        tv.setText(writeFormat.format(date));
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showTimePickerDeb();
            }

        });
        tv = (TextView) alertDialogView1.findViewById(R.id.repHeurFin);

        Date date2 = new Date(mAnnee, mMois, mJour, mH2, mMin2);
        tv.setText(writeFormat.format(date2));

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showTimePickerFin();
            }

        });
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setView(alertDialogView1);
        adb.setTitle(R.string.replanification);
        adb.setPositiveButton(getString(R.string.ok).toUpperCase(),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        //v49 code
//                        dataAccessObject.replanifInterv(idIntervention,
//                                getDbDate(1), getDbDate(2));
//                        setResult(35);
//                        closeActivity = true;
//                        synch();


                        // EventBus.getDefault().post(new
                        // UpdateDataBaseEvent());


                        //new changes

                        new RescheduleJobNotifyAsyncTask(JobDetails.this).execute();

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
     * Inits the dates.
     */
    @SuppressWarnings("deprecation")
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
     * Gets the date from db format.
     *
     * @param mDate the m date
     * @return the date from db format
     */
    public Date getDateFromDbFormat(String mDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
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
                                .getTimeFormat(JobDetails.this);
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
                                .getTimeFormat(JobDetails.this);
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        String dbDate;
        if (n == 1) {
            Date date = new Date(mAnnee, mMois, mJour, mH1, mMin1);
            dbDate = sdf.format(date);
        } else {
            Date date = new Date(mEndAnnee, mEndMois, mEndJour, mH2, mMin2);
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
        SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm", Locale.US);
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
                        .getTimeFormat(JobDetails.this);
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
                    .getTimeFormat(JobDetails.this);
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
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS", Locale.US);
        Date date;
        try {
            date = df.parse(mDate + " 00:00:00.000");
            return date;
        } catch (ParseException e) {
            Logger.printException(e);
            return new Date();
        }
    }

    public Date getDateIncident(String mDate) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        Date date;
        try {
            date = df.parse(mDate + " 00:00:00.000");
            return date;
        } catch (ParseException e) {
            Logger.printException(e);
            return new Date();
        }
    }

    //new changes v53 to get ACTION DATE as UTC time
    public Date getDateIncidentUTC(String mDate) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
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
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS", Locale.US);
        Date date;
        try {
            date = df.parse("01/01/0001 " + mDate + ":00.000");
            return date;
        } catch (ParseException e) {
            Logger.printException(e);
            return new Date();
        }
    }

    @Override
    public void onBackPressed() {
        if (!isTextDialogOpened) {
            super.onBackPressed();
            finish();
        } else {
            EventBus.getDefault().post(new CloseTextDialogEvent());
        }
    }

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
        EventBus.getDefault().register(this);
        ObservableObject.getInstance().addObserver(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive = false;
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        ObservableObject.getInstance().deleteObserver(this);
        super.onDestroy();
        if (progressDSynch != null
                && progressDSynch.isShowing())
            progressDSynch.dismiss();

        if (incidentTimeCounter != null) {
            incidentTimeCounter.cancel();
            incidentTimeCounter = null;
        }

    }

    // Event bus to start/resume JOB from Dialog
    public void onEvent(JobStartResumeEvent event) {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);

        if (event.getEventAction().equals(KEYS.JobDetails.KEY_START_JOB)) {

            //old code
//            if (dataAccessObject.checkIsClockInAvailable(user.getId())) {
//                // checking Clocked In or Out
//                startJobClockInOut(cal, sdf);
//            } else {
//                startJob(true);
//                status = KEYS.CurrentJobs.JOB__STARTED;
//                EventBus.getDefault().post(new UpdateJobDetailUi());
//            }

            new StartJobAsyncTask(JobDetails.this).execute();

        } else if (event.getEventAction().equals(KEYS.JobDetails.KEY_RESUME_JOB)) {


            new ResumeJobAsyncTask(JobDetails.this).execute();

            //old code
//            if (dataAccessObject.checkIsClockInAvailable(user.getId())) {
//                // checking Clocked In or Out
//                resumeJobClockInOut(cal, sdf);
//            } else {
//                startJob(false);
//                status = KEYS.CurrentJobs.JOB__STARTED;
//            }
        } else {
            Logger.log("jobDetails", "invalid action");

            checkJobStatusAndSettingUI(status, false, null);

            // show or hide the clock in out when the user have clock in out function
            checkClockModeAvailabilityAndSettingLayout(dataAccessObject.checkIsClockInAvailable(user.getId()));
        }


    }

    private void resumeJobClockInOut(Calendar cal, SimpleDateFormat sdf) {
        Conge vectCongeClockIn = checkClockedIn();
        if (vectCongeClockIn != null) {
            // clocked in
            startJob(false);
            status = KEYS.CurrentJobs.JOB__STARTED;
        } else {

            /* Clocked out, do clock in for the job,New logic subtract 1 min from current
            time for clock in time*/
            cal.add(Calendar.SECOND, -1);
            String currentDateStr = sdf.format(cal.getTime());
            // new clocked in entry to T_CONGE
            UnavailabilityBeans clockInActivity = dataAccessObject.getClockInActivity();
            String uniqueID = dataAccessObject.addUnavailabilityAndReturnID(null, clockInActivity.getUnavailabilityID(), 0, currentDateStr, null, "");
            if (uniqueID != null) {
                showToastMessage(getResources().getString(R.string.txt_clock_in));
                startJob(false);
                status = KEYS.CurrentJobs.JOB__STARTED;
            } else {
                showToastMessage(getResources().getString(R.string.msg_error));
            }
        }
    }

    private void startJobClockInOut(Calendar cal, SimpleDateFormat sdf) {
        Conge vectCongeClockIn = checkClockedIn();
        if (vectCongeClockIn != null) {
            // clocked in
            startJob(true);
            status = KEYS.CurrentJobs.JOB__STARTED;
            EventBus.getDefault().post(new UpdateJobDetailUi());
        } else {

            /* Clocked out, do clock in for the job,New logic subtract 1 min from current
            time for clock in time*/
            cal.add(Calendar.SECOND, -1);
            String currentDateStr = sdf.format(cal.getTime());

            // new clocked in entry to T_CONGE
            UnavailabilityBeans clockInActivity = dataAccessObject.getClockInActivity();
            String uniqueID = dataAccessObject.addUnavailabilityAndReturnID(null, clockInActivity.getUnavailabilityID(), 0, currentDateStr, null, "");
            if (uniqueID != null) {
                showToastMessage(getResources().getString(R.string.txt_clock_in));
                startJob(true);
                status = KEYS.CurrentJobs.JOB__STARTED;
                EventBus.getDefault().post(new UpdateJobDetailUi());
            } else {
                showToastMessage(getResources().getString(R.string.msg_error));
            }
        }
    }

    // Event bus to pause/stop JOB from Dialog
    public void onEvent(JobPauseFinishEvent event) {

        if (event.getEventAction().equals(KEYS.JobDetails.KEY_PAUSE_JOB)) {
//            pauseJob();
            new PauseJobAsyncTask(JobDetails.this).execute();

        } else if (event.getEventAction().equals(KEYS.JobDetails.KEY_FINISH_JOB)) {

            /*
             * new changes
             */
            AlertDialog.Builder builder = new AlertDialog.Builder(
                    JobDetails.this);
            builder.setMessage(R.string.txt_stop_job)
                    .setCancelable(false)
                    .setPositiveButton(R.string.textYesLable,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
//                                    stopJob();
                                    if (JobDetails.this != null)
                                        new StopJobAsyncTask(JobDetails.this).execute();
                                    dialog.dismiss();
                                }
                            })
                    .setNegativeButton(R.string.textNoLable,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();

//            stopJob();

        } else {
            Logger.log("jobDetails", "invalid action");
        }
        checkJobStatusAndSettingUI(status, false, null);

        // show / hide the clock in out when the user have clock in out function
        checkClockModeAvailabilityAndSettingLayout(dataAccessObject.checkIsClockInAvailable(user.getId()));
    }

    public void onEvent(ActivityUpdateEvent event) {

        // checking if activity is started
//        Conge unAvailabilityDetail = checkUnAvailabilityStarted();
//        if (unAvailabilityDetail != null) {
//            checkJobStatusAndSettingUI(0, true, unAvailabilityDetail.getNomTypeConge());
//        } else {
//            checkJobStatusAndSettingUI(status, false, null);
//        }

        //new changes
        checkJobStatusAndSettingUI(status, false, null);


        // show / hide the clock in out when the user have clock in out function
        checkClockModeAvailabilityAndSettingLayout(dataAccessObject.checkIsClockInAvailable(user.getId()));
    }

    private void showToastMessage(final String message) {
        //run on main thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(JobDetails.this, message, Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * this method will fires when the inactivity timeout happens. Fired from synchroteam navigation
     * activity
     *
     * @param event
     */
    public void onEvent(ClockInTimeOutEvent event) {
        ClockInTimeOutDialog.newInstance().show(getSupportFragmentManager(), "");
        boolean isClockModeAvailable = dataAccessObject.checkIsClockInAvailable(user.getId());
        checkClockModeAvailabilityAndSettingLayout(isClockModeAvailable);
    }


    /**
     * This method will triggers for clock in time out
     *
     * @param observable
     * @param o
     */
    @Override
    public void update(Observable observable, Object o) {
//        boolean isClockModeAvailable = dataAccessObject.checkIsClockInAvailable(user.getId());
//        if (isClockModeAvailable) {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
//            Calendar cal = Calendar.getInstance();
//            Vector<Conge> vectConge = dataAccessObject.getClockIn();
//            Enumeration<Conge> enindisp = vectConge.elements();
//            String currentDate = sdf.format(cal.getTime());
//            if (isActive) {
//                // check clocked in or not
//                Conge vectCongeClockIn = checkClockedIn();
//                if (vectCongeClockIn != null) {
//                    ClockInTimeOutDialog.newInstance().show(getSupportFragmentManager(), "");
//                }
//            }
//            while (enindisp.hasMoreElements()) {
//                Conge indisp = enindisp.nextElement();
//                if (indisp.getDtFin() == null) {
//                    dataAccessObject.updateClockedOutEndTime(indisp.getIdConge(), currentDate);
//                }
//            }
//            if (isActive)
//                checkClockModeAvailabilityAndSettingLayout(isClockModeAvailable);
    }


    // ---------------------------------------------NEW--CHANGES--------------------------------------------------

    /**
     * In samsung note 3, while selecting a date in date picker leads to a crash in reports screen (device language - french).
     * To overcome this, we override the getResources() in activity to resolve it.
     *
     * @return Resources object
     */
//    @Override
//    public Resources getResources() {
//        if (wrappedResources == null) {
//            wrappedResources = wrapResourcesFixDateDialogCrash(super.getResources());
//        }
//        return wrappedResources;
//    }
//
//    public Resources wrapResourcesFixDateDialogCrash(Resources r) {
//        return new Resources(r.getAssets(), r.getDisplayMetrics(), r.getConfiguration()) {
//            @NonNull
//            @Override
//            public String getString(int id, Object... formatArgs) throws NotFoundException {
//                try {
//                    return super.getString(id, formatArgs);
//                } catch (IllegalFormatConversionException ifce) {
//                    Log.i("DatePickerDialogFix", "IllegalFormatConversionException Fixed!", ifce);
//                    String template = super.getString(id);
//                    template = template.replaceAll("%" + ifce.getConversion(), "%s");
//                    return String.format(getConfiguration().locale, template, formatArgs);
//                }
//            }
//        };
//    }


//    protected void onShowKeyboard() {
//        viewSwitcher.setVisibility(View.GONE);
//        chronometer.setVisibility(View.GONE);
//        linDone.setVisibility(View.VISIBLE);
//
//    }
//
//    protected void onHideKeyboard() {
//        viewSwitcher.setVisibility(View.VISIBLE);
//        chronometer.setVisibility(View.VISIBLE);
//        linDone.setVisibility(View.GONE);
//    }

    // ---------------------------------------------NEW--CHANGES--V48--LOCAL---METHODS------------------------------------------------
    private void callUpdateJobDetailUiEvent() {
        //run on main thread
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new UpdateJobDetailUi());
            }
        });
    }

    /**
     * Start job.
     *
     * @param isFirstTIme the is first t ime
     */
    @SuppressLint("SimpleDateFormat")
    protected synchronized void startJobUpdated(boolean isFirstTIme) {

//        String id = dataAccessObject.getIdIntervStatus(idIntervention, idUser);
//        if(id!=null)
//            stopJobOld(id);

        if (!isFirstTIme) {
            // Update DT_FIN_PREV when Resume the job for old line (for pause state)
            String dt_Fin = dataAccessObject.getDtFinForRestartJobAndStopJob(idIntervention);
            dataAccessObject.updateDtFinPrev(dt_Fin, idIntervention);
        }

//        int id = dataAccessObject.getMainTechnicianNew(idIntervention);
//        Logger.log("TAG", "TECHNECIAN NAME AFTER JOB START====>"
//                + dataAccessObject.getNameTechnecian(id));

        long currentTime = 0;
        if (isFirstTIme) {
            if (dataAccessObject.updateStatutInterv(3, idIntervention)) {
                String tempId = dataAccessObject.getUniqueId();
                currentTime = dataAccessObject.setTimeStartInterv(idIntervention,
                        idUser, status, tempId);
                startTimer(currentTime);

//                dataAccessObject.updateTechName(idUser, tempId);
//                dataAccessObject.testDtFinPrev(idIntervention);

            }
        } else {
            if (dataAccessObject.updateStatutInterv(3, idIntervention)) {
                currentTime = dataAccessObject.setTimeStartIntervResume(idIntervention,
                        idUser, status);
                resumeTimer();
            }
        }

        //for stopping the previous started activities
        updateEndDateOfStartedActivities();
        updateEndDateofTravelActivities();

    }

    private synchronized void stopJobOld(String id) {

        if (id != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            String s = sdf.format(System.currentTimeMillis());
            if (dataAccessObject.updateStatutInterv(4, id)) {
                dataAccessObject.setTimeClotInterv(s);
            }
        }
    }

    /**
     * calculates the distance between all the points stored
     *
     * @return
     */
    private double calculateTotalDistance(ArrayList<LocationPoints> locationList) {

        double totDistance = 0.00;
        if (locationList != null && locationList.size() > 1) {
            for (int i = 0; i + 1 < locationList.size(); i++) {
                LocationPoints loc1 = locationList.get(i);
                LocationPoints loc2 = locationList.get(i + 1);
//                totDistance += LocationUtils.distanceBetweenTwoPoint(loc1.getLatitude(), loc1.getLongitude(), loc2.getLatitude(), loc2.getLongitude());
                totDistance += LocationUtils.distanceBetweenTwoPointAndroid(loc1.getLatitude(), loc1.getLongitude(), loc2.getLatitude(), loc2.getLongitude());
            }
            //for kms
//            totDistance = totDistance / 1000;

        }
        return totDistance;
    }

    private boolean updateEndDateofTravelActivities() {
        boolean result = false;
        TravelActivity travelActivity = dataAccessObject.isDrivingStarted();
        if (travelActivity != null) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
            String currentDateStr = sdf.format(cal.getTime());

            new Thread(new Runnable() {
                @Override
                public void run() {

                    List<LocationCoordinates> locations = new ArrayList<>();
                    locations = RoomDBSingleTone.instance(JobDetails.this)
                            .LocationCoordinatesDao().getAllLocationCoordinatesModels();
                    ArrayList<LocationPoints> locationList = new ArrayList<>();
                    double lat = 0.00;
                    double lon = 0.00;
                    double distTravelled = 0.00;
                    if (locations.size() > 0) {
                        for (LocationCoordinates loc : locations) {
                            LocationPoints loc1 = new LocationPoints(loc.getLatitude(), loc.getLongitude());
                            locationList.add(loc1);
                        }
                        lat = locationList.get(locationList.size() - 1).getLatitude();
                        lon = locationList.get(locationList.size() - 1).getLongitude();
                        double distanceTravelled = calculateTotalDistance(locationList);
                        distTravelled = distanceTravelled;
                    }

                    boolean result = dataAccessObject.updateStopLatLonAndFinishDriving(
                            travelActivity.getIdTravel(), currentDateStr, lat, lon, distTravelled);

                    RoomDBSingleTone.instance(JobDetails.this).LocationCoordinatesDao().deteteAllLocationCoordinatesModels();
                    List<LocationCoordinates> l = new ArrayList<>();

                    if (result) {

                        SharedPref.setDistanceEnabled(false, JobDetails.this);
                        //new changes v50
                        SharedPref.setIsTravelRunning(false, JobDetails.this);
                        SharedPref.setRunningTravelName("", JobDetails.this);
                        SharedPref.setIsTravelLastEntry(false, JobDetails.this);
                        SharedPref.setTravelLocationData("", JobDetails.this);

                        JobDetails.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                EventBus.getDefault().post(new DrivingModeUpdateEvent());
                                EventBus.getDefault().post(new TravelStopEvent());
                            }
                        });

                    }
                }
            }).start();
        }
        return result;
    }

//    private void updateEndDateofTravelActivities() {
//        TravelActivity travelActivity = dataAccessObject.isDrivingStarted();
//        if (travelActivity != null) {
//            Calendar cal = Calendar.getInstance();
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS",Locale.US);
//            String currentDateStr = sdf.format(cal.getTime());
//            String locationData = SharedPref.getTravelLocationData(this);
//            double lat = 0, lon = 0, distTravelled = 0;
//            if (locationData != null && locationData.trim().length() > 1) {
//                try {
//                    JSONObject jsonObject = new JSONObject(locationData);
//                    lat = jsonObject.getDouble("Location_lat");
//                    lon = jsonObject.getDouble("Location_long");
//                    distTravelled = jsonObject.getDouble("Location_dist");
//
//                    Logger.log(TAG, "Location distance calc is ===?" + lat + " ," + lon + ", " + distTravelled);
//
//                    boolean drp = dataAccessObject.updateStopLatLonAndFinishDriving(
//                            travelActivity.getIdTravel(), currentDateStr, lat, lon, distTravelled);
//
//                    if (drp) {
//
//                        SharedPref.setDistanceEnabled(false, this);
//                        //new changes v50
//                        SharedPref.setIsTravelRunning(false, this);
//                        SharedPref.setRunningTravelName("", this);
//                        SharedPref.setIsTravelLastEntry(false, this);
//                        SharedPref.setTravelLocationData("", this);
//
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                EventBus.getDefault().post(new DrivingModeUpdateEvent());
//                                EventBus.getDefault().post(new TravelStopEvent());
//                            }
//                        });
//
//
//                        //clear the realm db
//                        //pendiinf
////                        Realm realm = Realm.getDefaultInstance();
////                        realm.executeTransactionAsync(new Realm.Transaction() {
////                            @Override
////                            public void execute(Realm realm) {
////                                RealmResults<LocationCoordinates> locations = realm.where(LocationCoordinates.class).findAll();
////                                locations.deleteAllFromRealm();
////                            }
////                        });
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                RoomDBSingleTone.instance(JobDetails.this).LocationCoordinatesDao().deteteAllLocationCoordinatesModels();
//                                List<LocationCoordinates> l = new ArrayList<>();
//                                l = RoomDBSingleTone.instance(JobDetails.this).LocationCoordinatesDao().getAllLocationCoordinatesModels();
//                                Log.e("taf", l.toString());
//                            }
//                        }).start();
//
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
//    }

    public void updateEndDateOfStartedActivities() {
        Vector<Conge> vectConge = new Vector<Conge>();
        vectConge = dataAccessObject.getCongeNewExceptClockInActivity();
        Enumeration<Conge> enindisp = vectConge.elements();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
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
     * Special synch.
     */
    public void specialSynchonizeUpdated() {
        if (!JobDetails.this.isFinishing()) {
            if (progressDSynch != null && progressDSynch.isShowing()) progressDSynch.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.ask_synch_msg)
                    .setCancelable(false)
                    .setPositiveButton(R.string.textYesLable,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    new SynchronizeAsyncTask(JobDetails.this, false).execute();
                                    dialog.dismiss();
                                }
                            })
                    .setNegativeButton(R.string.textNoLable,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    dialog.cancel();

                                    EventBus.getDefault().post(
                                            new UpdateDataBaseEvent());
                                    if (closeActivity) {
                                        JobDetails.this.finish();
                                    }

                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void checkJobStatusAndSettingUIAfterSync(int status, boolean isActivityStarted,
                                                     String activityName, String jobNumber) {
        if (!isActivityStarted) {
            if (status == KEYS.JObDetail.JOB_NOT_STARTED1 || status == KEYS.JObDetail.JOB_NOT_STARTED2) {
                txtJobStatus.setText(getResources().getString(R.string.textStartLable));
                txtStopJobView.setVisibility(View.VISIBLE);
                txtStopJobView.setText(getString(R.string.fa_play));
            } else if (status == KEYS.JObDetail.JOB__SUSPENDED) {
                txtJobStatus.setText(getResources().getString(R.string.txt_resume));
                txtStopJobView.setVisibility(View.VISIBLE);
                txtStopJobView.setText(getString(R.string.fa_play));
            } else if (status == KEYS.JObDetail.JOB__STARTED) {
                txtJobStatus.setText(jobNumber);
                txtStopJobView.setVisibility(View.VISIBLE);
                txtStopJobView.setText(getString(R.string.fa_stop));
            } else if (status == KEYS.JObDetail.JOB__COMPLETE) {
                txtJobStatus.setText(jobNumber);
                txtStopJobView.setVisibility(View.INVISIBLE);
            } else {
                txtJobStatus.setText(jobNumber);
                txtStopJobView.setVisibility(View.INVISIBLE);
            }
        } else {
            txtJobStatus.setText(activityName);
            txtStopJobView.setVisibility(View.VISIBLE);
        }
    }

    // ---------------------------------------------NEW--CHANGES--V48--LOCAL---METHODS------------------------------------------------


    // ---------------------------------------------NEW--CHANGES--V48--ASYNC---TASK------------------------------------------------


    /**
     * Async class for performing Synch.
     */
    private class SynchronizeAsyncTask extends AsyncTaskCoroutine<Void, String> {
        private final Context jobDetails;
        boolean isClockModeAvailable;
        Conge vectCongeClockIn;
        String jobNumber = "";
        boolean updateJobDetailEvent = false;

        public SynchronizeAsyncTask(Context jobDetails, boolean updateJobDetailEvent) {
            this.jobDetails = jobDetails;
            this.updateJobDetailEvent = updateJobDetailEvent;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            if (JobDetails.this != null && !JobDetails.this.isFinishing()) {
                if (progressDSynch == null)
                    progressDSynch = ProgressDialog.show(JobDetails.this,
                            jobDetails.getString(R.string.textPleaseWaitLable),
                            jobDetails.getString(R.string.msg_synch), true, false);
                else if (progressDSynch != null && !progressDSynch.isShowing())
                    progressDSynch.show();
            }
        }

        @Override
        public String doInBackground(Void... voids) {

            String resultMessage = "Ok";
            if (SynchroteamUitls.isNetworkAvailable(JobDetails.this)) {

                User u = dataAccessObject.getUser();
                try {
                    dataAccessObject.sync(u.getLogin(), u.getPwd());
                    Logger.output(TAG, " finished sync");

                    //Logic for hitting notification after sync
                    if (notiList != null && notiList.size() > 0)
                        for (int i = 0; i < notiList.size(); i++)
                            notificationEventList(notiList.get(i).getIdIntervention(),
                                    notiList.get(i).getStatus());

                    l = new ArrayList<>();
                    l = RoomDBSingleTone.instance(JobDetails.this).roomDao().getAllNotificationEventModels();
                    Log.e("TRIDENT", "the list is >>>>  "+ l.size());

                    if (l != null && l.size()>0){
                        for (int i=0;i<l.size();i++){
                            JSONObject jsonObj = new JSONObject(l.get(i).value);

                            hitNotificationEventService(jsonObj.getString("IdJob"),jsonObj.getInt("IdCustomer"),jsonObj.getInt("JobStatus"),l.get(i).id);
                            Log.e("TRIDENT","THE JSON IS >>>>>" + jsonObj.getString("IdCustomer"));

                        }
                    }


                    resultMessage = "ok";
                    Thread.sleep(1000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    String exception = ex.getMessage();
                    Logger.printException(ex);
                    if (exception != null) {
                        if (exception.indexOf("4001") != -1) {
                            resultMessage = "4001";
                        } else if (exception.indexOf("4000") != -1) {
                            resultMessage = "4000";
                        } else if (exception.indexOf("4006") != -1) {
                            resultMessage = "4006";
                        } else if (exception.indexOf("4101") != -1) {
                            resultMessage = "4101";
                        } else if (exception.indexOf("4005") != -1) {
                            resultMessage = "4005";
                        } else if (exception.indexOf("4003") != -1) {
                            resultMessage = "4003";
                        } else {
                            resultMessage = "Error";
                        }
                    } else {
                        resultMessage = "Error";
                    }
                }

            } else {

                //save in database
                if (notiList != null && notiList.size() > 0)
                    for (int i = 0; i < notiList.size(); i++)
                        notificationEventList(notiList.get(i).getIdIntervention(),
                                notiList.get(i).getStatus());
                resultMessage = "offline";
            }

            isClockModeAvailable = dataAccessObject.checkIsClockInAvailable(user.getId());
            vectCongeClockIn = checkClockedIn();
            jobNumber = getJobNumber();


            return resultMessage;
        }

        @Override
        public void onPostExecute(String resultMessage) {
            super.onPostExecute(resultMessage);
            if (jobDetails != null) {

                //show job number if exists and show or hide the clock in out when the user have clock in out function

                if (jobNumber != null && jobNumber.trim().length() > 0) {
                    checkJobStatusAndSettingUIAfterSync(status, false, null, jobNumber);
                } else {
                    checkJobStatusAndSettingUI(status, false, null);
                }
                if (isClockModeAvailable) {
                    txtClockMode.setVisibility(View.VISIBLE);
                    // check clocked in or not
                    if (vectCongeClockIn != null) {
                        txtClockMode.setBackgroundColor(getResources().getColor(R.color.green_color));
                    } else {
                        txtClockMode.setBackgroundColor(getResources().getColor(R.color.red_color));
                    }
                } else {
                    txtClockMode.setVisibility(View.GONE);
                }

                if (updateJobDetailEvent)
                    EventBus.getDefault().post(new UpdateJobDetailUi());

                if (resultMessage.equals("offline") || resultMessage.equals("ok")) {
                    EventBus.getDefault().post(new UpdateDataBaseEvent());
                    if (resultMessage.equals("ok"))
                        Toast.makeText(JobDetails.this,
                                        getString(R.string.msg_synch_ok), Toast.LENGTH_LONG)
                                .show();

                    if (closeActivity) {
                        finish();
                    } else {
                        if (resultMessage.equals("offline")) {
                            SynchroteamUitls.showToastMessage(JobDetails.this);
                        } else {
                            EventBus.getDefault().post(new UpdateUiAfterSync());
                        }
                    }

                } else if (resultMessage.equals("4001") || resultMessage.equals("4000")) {
                    showAuthErrDialog();
                } else if (resultMessage.equals("4006")) {
                    Toast.makeText(JobDetails.this,
                            getString(R.string.msg_synch_error_4),
                            Toast.LENGTH_LONG).show();
                    if (closeActivity) {
                        finish();
                    }

                } else if (resultMessage.equals("4101")) {
                    showMultipleDeviecDialog();
                } else if (resultMessage.equals("4005")) {
                    showAppUpdatetDialog();
                } else if (resultMessage.equals("4003")) {
                    showErrMsgDialog(getString(R.string.msg_sync_error_4003));
                } else {
                    if (!JobDetails.this.isFinishing()) {
                        showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));
                    }
                }
                if (JobDetails.this != null && !JobDetails.this.isFinishing()) {
                    //dismiss progress bar
                    if (progressDSynch != null
                            && progressDSynch.isShowing())
                        progressDSynch.dismiss();
                }
            }
        }
    }

    /**
     * Async task for starting the intervention and to perform Dao operations.
     * user WeakReference for avoiding the memory leaks
     */
    private class StartJobAsyncTask extends AsyncTaskCoroutine<Void, String> {

        private final Context jobDetails;
        private final Calendar cal;
        private final SimpleDateFormat sdf;
        private boolean updateJobDetailEvent = true;

        public StartJobAsyncTask(Context jobDetails) {
            this.jobDetails = jobDetails;
            cal = Calendar.getInstance();
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
            String idIntervOld = dataAccessObject.getStartedJobId();
            if (idIntervOld != null && idIntervOld.trim().length() > 0) {
                if (dataAccessObject.updateStatutInterv(4, idIntervOld))
                    dataAccessObject.setTimeClotIntervention(idIntervOld, idUser + "", "");
            }

        }


        @Override
        public void onPreExecute() {
            super.onPreExecute();
            if (progressDSynch == null)
                progressDSynch = ProgressDialog.show(JobDetails.this,
                        jobDetails.getString(R.string.textPleaseWaitLable),
                        jobDetails.getString(R.string.msg_synch), true, false);
//            else if (progressDSynch != null && !progressDSynch.isShowing())
            else
                progressDSynch.show();


        }

        @Override
        public String doInBackground(Void... voids) {

            String resultMessage = "0";

            if (dataAccessObject != null && user != null) {
                if (dataAccessObject.checkIsClockInAvailable(user.getId())) {
                    // checking Clocked In or Out
                    Conge vectCongeClockIn = checkClockedIn();
                    if (vectCongeClockIn != null) {
                        // clocked in
//                        showToastMessage(getResources().getString(R.string.txt_clock_in));
//                        stopJobOld();
                        startJobUpdated(true);
                        status = KEYS.CurrentJobs.JOB__STARTED;
                        updateJobDetailEvent = true;
//                        callUpdateJobDetailUiEvent();

                    } else {

                        /* Clocked out, do clock in for the job,New logic subtract 1 min from current
                         time for clock in time*/
                        cal.add(Calendar.SECOND, -1);
                        String currentDateStr = sdf.format(cal.getTime());

                        // new clocked in entry to T_CONGE
                        UnavailabilityBeans clockInActivity = dataAccessObject.getClockInActivity();

                        Logger.log(TAG, "CHECKFINAL TIME STARTTIME VALUE====>" + currentDateStr);
                        String uniqueID = dataAccessObject.addUnavailabilityAndReturnID(null,
                                clockInActivity.getUnavailabilityID(), 0, currentDateStr,
                                null, "");

                        if (uniqueID != null) {
//                            showToastMessage(getResources().getString(R.string.txt_clock_in));

                            startJobUpdated(true);
                            status = KEYS.CurrentJobs.JOB__STARTED;
                            //run on main thread
//                            callUpdateJobDetailUiEvent();
                            updateJobDetailEvent = true;

                        } else {
//                            showToastMessage(getResources().getString(R.string.msg_error));
                            updateJobDetailEvent = false;
                        }
                    }
                } else {
//                    stopJobOld();
                    startJobUpdated(true);
                    status = KEYS.CurrentJobs.JOB__STARTED;
//                    callUpdateJobDetailUiEvent();
                    updateJobDetailEvent = true;
                }
            }

            assert dataAccessObject != null;
            resultMessage = "" + dataAccessObject.checkSynchronisation(2);

            /*new Changes 50 When the job is added successfully ,
             Check if there is internet connection and generate notification
             service if not save in local DB*/
            //v51.0.2
            //    realm = Realm.getDefaultInstance();


//            notificationEventList(idIntervention, status);

            //v54 Changes
            notiList = new ArrayList<>();
            NotificationItem item;
            item = new NotificationItem(idIntervention, status);
            notiList.add(item);

            return resultMessage;
        }

        @Override
        public void onPostExecute(String resultMessage) {
            super.onPostExecute(resultMessage);

            //Insert into GPS_STEPS_INTERVENTION
            if (daoTracking.getSessiondata("tracking").equals("on")) {
                gpsEvent = "start";
                geocoder();
            }


            if (jobDetails != null) {


                ClockInOutUtil.cancelAlarmForTimeOut(jobDetails);

                if (menu != null) {
                    menu.removeItem(R.id.action_reschedule);
                    menu.removeItem(R.id.action_decline);
                }

//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        menu.removeItem(R.id.action_reschedule);
//                        menu.removeItem(R.id.action_decline);
//                    }
//                });

                if (resultMessage.trim().length() > 0 && resultMessage.trim().equals("1")) {
                    new SynchronizeAsyncTask(JobDetails.this, updateJobDetailEvent).execute();
                } else {
                    if (!JobDetails.this.isFinishing()) {

                        specialSynchonizeUpdated();
                    }
                    checkJobStatusAndSettingUI(status, false, null);

                    // show / hide the clock in out when the user have clock in out function
                    checkClockModeAvailabilityAndSettingLayout(dataAccessObject.checkIsClockInAvailable(user.getId()));
                }

//                EventBus.getDefault().post(new AddInvoiceQuotationEvent());

                Logger.log(TAG, "START JOB NOTIFICAITON---->" + status);


            }

        }

    }


    /**
     * Async task for resuming the intervention and to perform Dao operations.
     * user WeakReference for avoiding the memory leaks
     */
    private class ResumeJobAsyncTask extends AsyncTaskCoroutine<Void, String> {

        private final Context jobDetails;
        private Calendar cal;
        private SimpleDateFormat sdf;

        public ResumeJobAsyncTask(JobDetails jobDetails) {
            this.jobDetails = jobDetails;
            cal = Calendar.getInstance();
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
            String idIntervOld = dataAccessObject.getStartedJobId();
            if (idIntervOld != null && idIntervOld.trim().length() > 0) {
                if (dataAccessObject.updateStatutInterv(4, idIntervOld))
                    dataAccessObject.setTimeClotIntervention(idIntervOld, idUser + "", "");
            }
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            if (progressDSynch == null) {
                progressDSynch = ProgressDialog.show(jobDetails,
                        jobDetails.getString(R.string.textPleaseWaitLable),
                        jobDetails.getString(R.string.msg_synch), true, false);
            } else if (progressDSynch != null && !progressDSynch.isShowing()) {
                progressDSynch.show();

            }
        }

        @Override
        public String doInBackground(Void... voids) {

            String resultMessage = "0";
            if (dataAccessObject.checkIsClockInAvailable(user.getId())) {
                // checking Clocked In or Out
                Conge vectCongeClockIn = checkClockedIn();
                if (vectCongeClockIn != null) {
                    // clocked in
                    startJobUpdated(false);
                    status = KEYS.CurrentJobs.JOB__STARTED;

                    //v54 Changes


                } else {

                    /* Clocked out, do clock in for the job,New logic subtract 1 min from current
                    time for clock in time*/
                    cal.add(Calendar.SECOND, -1);
                    String currentDateStr = sdf.format(cal.getTime());
                    // new clocked in entry to T_CONGE
                    UnavailabilityBeans clockInActivity = dataAccessObject.getClockInActivity();
                    String uniqueID = dataAccessObject.addUnavailabilityAndReturnID(null, clockInActivity.getUnavailabilityID(), 0, currentDateStr, null, "");
                    if (uniqueID != null) {
//                        showToastMessage(getResources().getString(R.string.txt_clock_in));
                        startJobUpdated(false);
                        status = KEYS.CurrentJobs.JOB__STARTED;
                    } else {
//                        showToastMessage(getResources().getString(R.string.msg_error));
                    }
                }

            } else {
                startJobUpdated(false);
                status = KEYS.CurrentJobs.JOB__STARTED;
            }

            resultMessage = "" + dataAccessObject.checkSynchronisation(1);



//            notiList = new ArrayList<>();
//            NotificationItem item;
//            item = new NotificationItem(idIntervention, status);
//            notiList.add(item);
//            Logger.log("TRIDENT_Test", "doInBackground onResume  :" + notiList.size());





            return resultMessage;
        }

        @Override
        public void onPostExecute(String resultMessage) {
            super.onPostExecute(resultMessage);

            if (daoTracking.getSessiondata("tracking").equals("on")) {
                gpsEvent = "resume";
                geocoder();
            }
            if (jobDetails != null) {

                if (resultMessage.trim().length() > 0 && resultMessage.trim().equals("1")) {
                    new SynchronizeAsyncTask(JobDetails.this, false).execute();
                } else {
                    if (!JobDetails.this.isFinishing()) {

                        specialSynchonizeUpdated();
                    }
                    checkJobStatusAndSettingUI(status, false, null);
                    // show / hide the clock in out when the user have clock in out function
                    checkClockModeAvailabilityAndSettingLayout(dataAccessObject.checkIsClockInAvailable(user.getId()));
                }

                if (menu != null) {
                    menu.removeItem(R.id.action_reschedule);
                    menu.removeItem(R.id.action_decline);
                }
            }
        }
    }


    /**
     * Async task for starting the intervention and to perform Dao operations.
     * user WeakReference for avoiding the memory leaks
     */
    private class PauseJobAsyncTask extends AsyncTaskCoroutine<Void, String> {

        private final Context jobDetails;
        private String syncTypeMessage = "0";

        public PauseJobAsyncTask(Context jobDetails) {
            this.jobDetails = jobDetails;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            if (progressDSynch == null)
                progressDSynch = ProgressDialog.show(jobDetails,
                        jobDetails.getString(R.string.textPleaseWaitLable),
                        jobDetails.getString(R.string.msg_synch), true, false);
            else if (progressDSynch != null && !progressDSynch.isShowing())
                progressDSynch.show();
        }

        @Override
        public String doInBackground(Void... voids) {
            String interType = "";
            if (status == KEYS.JObDetail.JOB__STARTED) {
                interType = "" + status;
                if (dataAccessObject.updateStatutInterv(4, idIntervention))
                    dataAccessObject.setTimeClotIntervention(idIntervention, idUser + "", "");
                status = KEYS.CurrentJobs.JOB__SUSPENDED;

                closeActivity = true;
                syncTypeMessage = "" + dataAccessObject.checkSynchronisation(3);

//                notificationEventList(idIntervention, status);

                //v54 Changes
                notiList = new ArrayList<>();
                NotificationItem item;
                item = new NotificationItem(idIntervention, status);
                notiList.add(item);
                Log.e("PauseJobAsyncTask>>>","PauseJobAsyncTask" +"  "+ notiList);


            } else if (status == KEYS.JObDetail.JOB__SUSPENDED) {
                interType = "" + status;
                status = KEYS.JObDetail.JOB__STARTED;


//                notiList = new ArrayList<>();
//                NotificationItem item;
//                item = new NotificationItem(idIntervention, status);
//                notiList.add(item);

//                String dateString = dataAccessObject
//                        .getJobResumedTimeInJobDetails(idIntervention);
//                SimpleDateFormat dateFormat = new SimpleDateFormat(
//                        "yyyy-MM-dd HH:mm:ss", Locale.US);
//                try {
//                    long timeWhenJobWasStarted = Objects.requireNonNull(dateFormat.parse(dateString))
//                            .getTime();
//                    long previousTimeToBeSubtracted = dataAccessObject
//                            .suspendedTimeDiffrence(idIntervention);
//
//                    startTimer(timeWhenJobWasStarted - previousTimeToBeSubtracted);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    Logger.printException(e);
//                }

                startTimer(getjobStartStopTimeValue());
            }
            return interType;
        }

        @Override
        public void onPostExecute(String resultMessage) {
            super.onPostExecute(resultMessage);
            if (daoTracking.getSessiondata("tracking").equals("on")) {
                gpsEvent = "pause";
                geocoder();
            }
            int numberParseMsg = 0;
            if (jobDetails != null) {
                try {
                    numberParseMsg = Integer.parseInt(resultMessage.toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (numberParseMsg == KEYS.JObDetail.JOB__STARTED) {
                    if (syncTypeMessage.trim().length() > 0 && syncTypeMessage.trim().equals("1")) {
                        new SynchronizeAsyncTask(JobDetails.this, true).execute();
                    } else {
                        if (!JobDetails.this.isFinishing()) {

                            specialSynchonizeUpdated();
                        }
                        checkJobStatusAndSettingUI(status, false, null);
                        // show / hide the clock in out when the user have clock in out function
                        checkClockModeAvailabilityAndSettingLayout(dataAccessObject.checkIsClockInAvailable(user.getId()));
                    }

                } else if (numberParseMsg == KEYS.JObDetail.JOB__SUSPENDED) {
                    new ResumeJobAsyncTask(JobDetails.this).execute();
                }

            }
        }
    }


    /**
     * Async task for starting the intervention and to perform Dao operations.
     * user WeakReference for avoiding the memory leaks
     */
    private class StopJobAsyncTask extends AsyncTaskCoroutine<Void, String> {

        private final Context jobDetails;
        private String syncTypeMessage = "0";

        public StopJobAsyncTask(Context jobDetails) {
            this.jobDetails = jobDetails;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            if (progressDSynch == null)
                progressDSynch = ProgressDialog.show(JobDetails.this,
                        jobDetails.getString(R.string.textPleaseWaitLable),
                        jobDetails.getString(R.string.msg_synch), true, false);
            else if (progressDSynch != null && !progressDSynch.isShowing())
                progressDSynch.show();
        }

        @Override
        public String doInBackground(Void... voids) {

            String jobMandatoryFieldCompleted = "yes";
            if (checkObligGlobale() == 1) {

                if (dataAccessObject.updateStatutInterv(5, idIntervention)) {

                    dataAccessObject.setRealEndDate(idIntervention);
                    dataAccessObject.setTimeClotInterventionForJobFinish(idIntervention, idUser + "");
                    //remove the take back part items if available in shared preference
                    ArrayList<String> serialList = dataAccessObject.getAllTakeBackPieceSerialList(idIntervention);
                    if (serialList != null && serialList.size() > 0) {
                        for (int i = 0; i < serialList.size(); i++) {
                            String key = dataAccessObject.getIdForSerial(serialList.get(i).trim());
                            if (jobDetails != null)
                                SharedPref.removeTakeBackPartSharedPref(jobDetails, key);
                        }
                    }
                }
                status = KEYS.CurrentJobs.JOB__COMPLETE;

                closeActivity = true;
                syncTypeMessage = "" + dataAccessObject.checkSynchronisation(4);

            } else {
                jobMandatoryFieldCompleted = "no";

            }

            if (jobMandatoryFieldCompleted.equals("yes")) {
                /*new Changes 50 When the job is added successfully ,
             Check if there is internet connection and generate notification
             service if not save in local DB*/
                // realm = Realm.getDefaultInstance();
                //v51.0.2

//                notificationEventList(idIntervention, status);

                //v54 Changes
                notiList = new ArrayList<>();
                NotificationItem item;
                item = new NotificationItem(idIntervention, status);
                notiList.add(item);
                Log.e("StopJobAsyncTask>>>","notification" + notiList);
            }


            return jobMandatoryFieldCompleted;
        }

        @Override
        public void onPostExecute(String resultMessage) {
            super.onPostExecute(resultMessage);
            if (daoTracking.getSessiondata("tracking").equals("on")) {
                gpsEvent = "stop";
                geocoder();
            }
            if (jobDetails != null) {
                if (resultMessage.equals("yes")) {

                    if (syncTypeMessage.trim().length() > 0 && syncTypeMessage.trim().equals("1")) {
                        new SynchronizeAsyncTask(JobDetails.this, false).execute();
                    } else {
                        if (!JobDetails.this.isFinishing()) {

                            specialSynchonizeUpdated();
                        }
                        checkJobStatusAndSettingUI(status, false, null);
                        // show / hide the clock in out when the user have clock in out function
                        checkClockModeAvailabilityAndSettingLayout(dataAccessObject.checkIsClockInAvailable(user.getId()));
                    }

                    Logger.log(TAG, "STOP JOB NOTIFICAITON---->" + status);


                } else if (resultMessage.equals("no")) {
                    if (progressDSynch != null && progressDSynch.isShowing())
                        progressDSynch.dismiss();
                    Toast.makeText(JobDetails.this, R.string.msg_saisie_oblig,
                            Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    /**
     * Async task for rescheduling the intervention and to perform Dao operations.
     * user WeakReference for avoiding the memory leaks
     */
    private class RescheduleJobNotifyAsyncTask extends AsyncTaskCoroutine<Void, Boolean> {

        private Context jobDetails;
        private Calendar cal;
        private SimpleDateFormat sdf;
        private boolean updateJobDetailEvent = true;

        public RescheduleJobNotifyAsyncTask(Context jobDetails) {
            this.jobDetails = jobDetails;
            cal = Calendar.getInstance();
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        }


        @Override
        public void onPreExecute() {
            super.onPreExecute();
            if (progressDSynch == null)
                progressDSynch = ProgressDialog.show(jobDetails,
                        jobDetails.getString(R.string.textPleaseWaitLable),
                        jobDetails.getString(R.string.msg_synch), true, false);
            else if (progressDSynch != null && !progressDSynch.isShowing())
                progressDSynch.show();


        }

        @Override
        public Boolean doInBackground(Void... voids) {

            boolean resultMessage = false;

            if (dataAccessObject != null) {

                resultMessage = dataAccessObject.replanifInterv(idIntervention,
                        getDbDate(1), getDbDate(2));
            }


            /*new Changes 50 When the job is rescheduled successfully ,
             Check if there is internet connection and generate notification
             service if not save in local DB*/
            //v51.0.2
            //    realm = Realm.getDefaultInstance();
            if (resultMessage) {
//                notificationEventList(idIntervention, 1);

                //v54 Changes
                notiList = new ArrayList<>();
                NotificationItem item;
                item = new NotificationItem(idIntervention, status);
                notiList.add(item);
            }

//            notificationEventList(idIntervention,status);

            return resultMessage;
        }

        @Override
        public void onPostExecute(Boolean resultMessage) {
            super.onPostExecute(resultMessage);


            if (jobDetails != null) {

                //setResult(35);
                closeActivity = true;
                synch();

                Logger.log(TAG, "RESCHEDULE JOB NOTIFICAITON---->" + status);

            } else {
                if (progressDSynch != null && progressDSynch.isShowing())
                    progressDSynch.dismiss();
            }

        }

    }

    // ---------------------------------------------NEW--CHANGES--V48--ASYNC---TASK------------------------------------------------

    // ---------------------------------------------NEW--CHANGES--V49--NOTIFICATION---TASK------------------------------------------------

    private void notificationEventList(String idJob, int jobStatus) {
        if (SynchroteamUitls.isNetworkAvailable(JobDetails.this)) {
            hitNotificationEventService(idJob, dataAccessObject.getIdCustomer(), jobStatus, idJob);
        } else {
            String notificationId = dataAccessObject.getUniqueId();
            String values = getJsonRequestValues(idJob, dataAccessObject.getIdCustomer(), jobStatus);
            com.synchroteam.roomDB.entity.NotificationEventModels eventModels = new com.synchroteam.roomDB.entity.NotificationEventModels();
            eventModels.setId(notificationId);
            eventModels.setUrl(SharedPref.getNotiUrlServer(JobDetails.this));
            eventModels.setValue(values);
            saveNotiAlertInLocalDB(eventModels);
        }
    }

    private void hitNotificationEventService(String idJobCreated, int idCustomer, int jobStatus,String idLocalDb){
//        ApiInterface apiService =
//                Apiclient.getClient().create(ApiInterface.class);

        String authUserName = dataAccessObject.getUserDomain() + "_" + user.getLogin();
        String authPassword = user.getPwd();
        ApiInterface apiService = ApiClientNew.createService(ApiInterface.class, authUserName, authPassword);

        String url = SharedPref.getNotiUrlServer(JobDetails.this);
        Logger.log("TRIDENT_Test", "EventNotiResult url--->" + url);



        String currentDateUtc = getCurrentUtcDate();

        Logger.log("TRIDENT_Test", "EventNotiResult currentDateUtc--->" + currentDateUtc);

        jsonInfo infoObject = new jsonInfo();
        int idJobType = dataAccessObject.getTypeIntervention(idJobCreated);
        ArrayList<String> list = dataAccessObject.getInterventionTimeDetails(idJobCreated);
        infoObject.setIdJobType(idJobType);
        infoObject.setIdTech(user.getId());

        if (idClient >= 1)
            infoObject.setIdClient(idClient);

        if (idSite >= 1)
            infoObject.setIdSite(idSite);

        if (idEquipement >= 1)
            infoObject.setIdEquipement(idEquipement);

        if (list.get(0) != null && list.get(0).length() > 0)
            infoObject.setStartedScheduledDateTime(list.get(0));

        if (list.get(1) != null && list.get(1).length() > 0)
            infoObject.setCompletedScheduledDateTime(list.get(1));

        if (list.get(2) != null && list.get(2).length() > 0)
            infoObject.setStartedRealisedDateTime(list.get(2));

        if (list.get(3) != null && list.get(3).length() > 0)
            infoObject.setCompletedRealisedDateTime(list.get(3));



        EventNotiRequest eventNotiRequest = new EventNotiRequest(idCustomer, idJobCreated,
                jobStatus, "tech", currentDateUtc, infoObject);

        Gson gson = new Gson();
        String json = gson.toJson(eventNotiRequest);
        Logger.log("TRIDENT_Test", "SERVICE CALL REQUEST URL--->" + url);
        Logger.log("TRIDENT_Test", "SERVICE CALL REQUEST JSON--->" + json);

        Logger.log("TRIDENT_Test", "EventNotiResult EventNotiRequest--->" + eventNotiRequest.toString());

        Call<EventNotiResult> call = apiService.notificationEventService(url, eventNotiRequest);
        call.enqueue(new Callback<EventNotiResult>() {
            @Override
            public void onResponse(Call<EventNotiResult> call, Response<EventNotiResult> response) {


                if (response.isSuccessful()) {
                    int status = response.body().getResult();
                    Logger.log("TRIDENT_Test", "SERVICE CALL RESULT --->" + response);
                    if (status == 1) {
                        Logger.log("TRIDENT_Test", "EventNotiResult success :" + status);
                    } else {
                        Logger.log("TRIDENT_Test", "EventNotiResult failure :" + status);
                    }
                } else {
                    Logger.log("TRIDENT_Test", "EventNotiResult failure :" + response);
                }
            }

            @Override
            public void onFailure(Call<EventNotiResult> call, Throwable t) {
                Logger.log("TRIDENT_Test", "EventNotiResult Exception :" + t);

            }

        });
    }


    private String getJsonRequestValues(String idJobCreated, int idCustomer, int JobStatus) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();

            jsonObject.put("IdCustomer", idCustomer);
            jsonObject.put("IdJob", idJobCreated);
            jsonObject.put("JobStatus", JobStatus);
            jsonObject.put("EventOrigin", "tech");
            jsonObject.put("timestamp", getCurrentUtcDate());
            jsonObject.put("jsonInfo", getJsonInfObject(idJobCreated));
            Logger.log("TAG", "JSON NOTIFICATION VALUES===>" + jsonObject.toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private JSONObject getJsonInfObject(String idJobCreated) {
        JSONObject jsonInfo = new JSONObject();
        int idJobType = dataAccessObject.getTypeIntervention(idJobCreated);
        ArrayList<String> list = dataAccessObject.getInterventionTimeDetails(idJobCreated);
        ArrayList<Integer> clientDetails = dataAccessObject.getInterventionClientDetails(idJobCreated);

        try {
            if (clientDetails.get(0) != null && clientDetails.get(0) >= 1)
                jsonInfo.put("idClient", clientDetails.get(0));
            else
                jsonInfo.put("idClient", null);

            if (clientDetails.get(1) != null && clientDetails.get(1) >= 1)
                jsonInfo.put("idSite", clientDetails.get(1));
            else
                jsonInfo.put("idSite", null);
            if (clientDetails.get(2) != null && clientDetails.get(2) >= 1)
                jsonInfo.put("idEquipment", clientDetails.get(2));
            else
                jsonInfo.put("idEquipment", null);

            jsonInfo.put("idJobType", idJobType);
            jsonInfo.put("idTech", idUser);

            if (list.get(0) != null && list.get(0).length() > 0)
                jsonInfo.put("startedScheduledDateTime", list.get(0));
            else
                jsonInfo.put("startedScheduledDateTime", null);
            if (list.get(1) != null && list.get(1).length() > 0)
                jsonInfo.put("completedScheduledDateTime", list.get(1));
            else
                jsonInfo.put("completedScheduledDateTime", null);
            if (list.get(2) != null && list.get(2).length() > 0)
                jsonInfo.put("startedRealisedDateTime", list.get(2));
            else
                jsonInfo.put("startedRealisedDateTime", null);
            if (list.get(3) != null && list.get(3).length() > 0)
                jsonInfo.put("completedRealisedDateTime", list.get(3));
            else
                jsonInfo.put("completedRealisedDateTime", null);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonInfo;
    }

    public String getCurrentUtcDate() {
        String currentDate;
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        currentDate = dateFormat.format(date);
        return currentDate;
    }

    public void saveNotiAlertInLocalDB(final com.synchroteam.roomDB.entity.NotificationEventModels notifModel) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    RoomDBSingleTone.instance(JobDetails.this).roomDao().insertAll(notifModel);
                    List<NotificationEventModels> l = new ArrayList<>();
                    l = RoomDBSingleTone.instance(JobDetails.this).roomDao().getAllNotificationEventModels();
                    Log.e("taf", l.toString());
                }
            }).start();

        } catch (Exception e) {
            Logger.printException(e);
        }
    }


    // ---------------------------------------------NEW--CHANGES--V48--NOTIFICATION---TASK------------------------------------------------


    /**
     * Replanif intervention.
     */
    @SuppressWarnings("deprecation")
    public void replanifInterventionNew() {

        RescheduleIntervenDialog.newInstance(idIntervention, new RescheduleIntervenDialog.TakeBackActionListener() {


            @Override
            public void doOnConfirmClick(int mJour, int mMois, int mAnnee,
                                         int mEndJour, int mEndMois, int mEndAnnee,
                                         int mH1, int mH2, int mMin1, int mMin2) {
                JobDetails.this.mJour = mJour;
                JobDetails.this.mMois = mMois;
                JobDetails.this.mAnnee = mAnnee;
                JobDetails.this.mEndJour = mEndJour;
                JobDetails.this.mEndMois = mEndMois;
                JobDetails.this.mEndAnnee = mEndAnnee;
                JobDetails.this.mH1 = mH1;
                JobDetails.this.mH2 = mH2;
                JobDetails.this.mMin1 = mMin1;
                JobDetails.this.mMin2 = mMin2;

                new RescheduleJobNotifyAsyncTask(JobDetails.this).execute();

            }

            @Override
            public void doOnCancelClick() {

            }
        }).show(getSupportFragmentManager(), "");
    }


    //V54 changes
    private class IsMandatoryFilledAsyncTask extends AsyncTaskCoroutine<Void, Boolean> {

        private Context jobDetails;
        private Calendar cal;
        private SimpleDateFormat sdf;
        int status;
        long timeUpdatedValue;

        public IsMandatoryFilledAsyncTask(Context jobDetails, int status, long timeUpdatedValue) {
            this.jobDetails = jobDetails;
            this.status = status;
            this.timeUpdatedValue = timeUpdatedValue;
        }


        @Override
        public void onPreExecute() {
            super.onPreExecute();
            if (progressDSynch == null)
                progressDSynch = ProgressDialog.show(jobDetails,
                        jobDetails.getString(R.string.textPleaseWaitLable),
                        jobDetails.getString(R.string.msg_synch), true, false);
            else if (progressDSynch != null && !progressDSynch.isShowing())
                progressDSynch.show();


        }

        @Override
        public Boolean doInBackground(Void... voids) {

            boolean resultMessage = true;

            Vector<Families> vect = dataAccessObject.getSBCategory(idIntervention);

            Iterator<Families> iter = vect.iterator();
            while (iter.hasNext()) {
                Families fm = iter.next();
                if (CheckObligPartial(dataAccessObject.getAllItem(idIntervention,
                        fm.getIdFamily(), fm.getIteration())) == 0) {
                    resultMessage = false;
                    return resultMessage;
                }
            }

            return resultMessage;
        }

        @Override
        public void onPostExecute(Boolean resultMessage) {
            super.onPostExecute(resultMessage);

            if (progressDSynch != null && progressDSynch.isShowing())
                progressDSynch.dismiss();

            if (status == KEYS.JObDetail.JOB__STARTED)
                JobPauseFinishDialog.newInstance(getJobNumber(), timeUpdatedValue, resultMessage).show(getSupportFragmentManager(), "");

        }

    }


//    private void notificationEvent(String idJob, int jobStatus) {
//        if (SynchroteamUitls.isNetworkAvailable(JobDetails.this)) {
//            try {
//                hitNotificationEventService(idJob, dataAccessObject.getIdCustomer(), jobStatus);
//            } catch (Exception e) {
//
//            }
//        } else {
//            String notificationId = dataAccessObject.getUniqueId();
//            String values = getJsonRequestValues(idJob, dataAccessObject.getIdCustomer(), jobStatus);
//            com.synchroteam.roomDB.entity.NotificationEventModels eventModels = new com.synchroteam.roomDB.entity.NotificationEventModels();
//            eventModels.setId(notificationId);
//            eventModels.setUrl(SharedPref.getNotiUrlServer(JobDetails.this));
//            eventModels.setValue(values);
//            saveNotiAlertInLocalDB(eventModels);
//        }
//    }

    public class OnResumeAsynTask extends AsyncTaskCoroutine<Void, Void> {


        @Override
        public void onPreExecute() {
            super.onPreExecute();
            DialogUtils.showProgressDialog(JobDetails.this,
                    JobDetails.this.getString(R.string.textWaitLable),
                    JobDetails.this.getString(R.string.textProgressDialogFetchNearestClient), false);

            //new changes
            checkJobStatusAndSettingUI(status, false, null);

        }

        @Override
        public Void doInBackground(Void... voids) {

            // show / hide the clock in out when the user have clock in out function
            checkClockModeAvailabilityAndSettingLayout(dataAccessObject.checkIsClockInAvailable(user.getId()));

            //--------------------------------------- v48 ---------------------------------------------

            //New changes
            DateChecker.checkDateAndNavigate(JobDetails.this, dataAccessObject); // this changed into getApplicationContext()

            return null;
        }

        @Override
        public void onPostExecute(Void unused) {

            if (!JobDetails.this.isFinishing() && context != null) {

                ((SyncroTeamApplication) context)
                        .setSyncroteamAppLive(true);
                ((SyncroTeamApplication) context)
                        .setSyncroteamActivityInstance(JobDetails.this);
            }


            String dateFormatString = Settings.System.getString(
                    JobDetails.this.getContentResolver(),
                    Settings.System.DATE_FORMAT);
            String timeFormatString = Settings.System.getString(
                    JobDetails.this.getContentResolver(),
                    Settings.System.TIME_12_24);

            if ((!TextUtils.isEmpty(dateFormatString))
                    && (!TextUtils.isEmpty(timeFormatString))) {
                if (!dateFormatString.equals(SharedPref.getDateFormat(JobDetails.this))) {
                    SharedPref.setDateFormat(JobDetails.this);
                    SharedPref.setTimeFormat(JobDetails.this);
                    EventBus.getDefault().post(new UpdateJobDetailUi());

                } else if (!timeFormatString.equals(SharedPref.getTimeFormat(JobDetails.this))) {

                    SharedPref.setDateFormat(JobDetails.this);
                    SharedPref.setTimeFormat(JobDetails.this);
                    EventBus.getDefault().post(new UpdateJobDetailUi());

                }

            }

            DialogUtils.dismissProgressDialog();

        }

    }

    private void geocoder() {

        if (servicesConnected()) {
            boolean isGPSEnabled = false;
            boolean isNetworkEnabled = false;

            try {
                locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

                isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                isNetworkEnabled = Build.FINGERPRINT.contains("generic") ? true : locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


                if (!isGPSEnabled && !isNetworkEnabled) {
                    showSettingsAlert();
                } else {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        callingPermissionLocation();
                    } else {
                        callingLocationFunctionalities();
                    }
                }

            } catch (Exception e) {
                Logger.printException(e);
            }
        }
    }

    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(context);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status

            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode,
                    (Activity) context, 0);
            dialog.show();

            return false;
        }
    }

    private void callingPermissionLocation() {

        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle(getString(R.string.app_name));
            alertBuilder.setMessage(getString(R.string.str_location_permission));
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(getActivityInstance(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions((Activity) this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    private void callingLocationFunctionalities() {


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                locationClient, locationRequest, locationListener);

        final Toast tag = Toast.makeText(this,
                getString(R.string.gps_delai), Toast.LENGTH_SHORT);
        if (myTimer != null) {
            myTimer.cancel();
            myTimer = new Timer();
        } else
            myTimer = new Timer();

        myTimer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                DialogUtils.dismissProgressDialog();
                stopUsingGPS();
                tag.show();
            }
        }, 50000);

        DialogUtils
                .showProgressDialog(
                        this,
                        this
                                .getString(R.string.textPleaseWaitLable),
                        this
                                .getString(R.string.textFetchingLocation),
                        false);
    }
    public void stopUsingGPS() {

        if ((locationClient != null) && (locationClient.isConnected())) {
            // locationClient.removeLocationUpdates(locationListener);
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    locationClient, locationListener);
        }
    }
    /**
     * The location listener.
     */
    LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {


            stopUsingGPS();
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            insertIntoGpsStepsIntervention();

            myTimer.cancel();
            DialogUtils.dismissProgressDialog();
        }

    };

    private void insertIntoGpsStepsIntervention() {
        cal.add(Calendar.SECOND, -1);
        SimpleDateFormat dateFormat = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            dateFormat = new SimpleDateFormat("YYYYMMDD");
        }

        String currentDateStr = dateFormat.format(cal.getTime());
        int dateIndex = Integer.parseInt(currentDateStr);

        String lat = latitude+"";
        String lng = longitude+"";
        daoTracking.insertJobInGpsSteps(idIntervention,lat,lng,gpsEvent,dateIndex);
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getString(R.string.textAlertLable) + "!");
        alertDialog.setMessage(getString(R.string.textEnableLocationService));
        alertDialog.setPositiveButton(R.string.textYesLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                        callingLocationFunctionalities();
                    }
                });

        alertDialog.setNegativeButton(R.string.textNoLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }
    private void initializingForLocation() {
        locationClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        locationClient.connect();

        locationRequest = LocationRequest.create();

        locationRequest
                .setInterval(SynchroteamUitls.UPDATE_INTERVAL_IN_MILLISECONDS);

        // Use high accuracy
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
        locationRequest
                .setFastestInterval(SynchroteamUitls.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

    }

}