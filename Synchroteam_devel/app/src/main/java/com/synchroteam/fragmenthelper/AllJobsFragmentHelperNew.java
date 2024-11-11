package com.synchroteam.fragmenthelper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.baseactivity.SyncroTeamBaseActivity;
import com.synchroteam.beans.CommonJobBean;
import com.synchroteam.beans.CommonListBean;
import com.synchroteam.beans.CurrentJobDataBean;
import com.synchroteam.beans.CurrentJobDataBeanNew;
import com.synchroteam.beans.EnableSynchronizationAddJobEvent;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.AllJobsSortingDialog;
import com.synchroteam.fragment.AllJobsFragment;
import com.synchroteam.fragment.BaseFragment;
import com.synchroteam.listadapters.AllJobsDateAdapterRv;
import com.synchroteam.listadapters.AllJobsSortingAdapterRv;
import com.synchroteam.listeners.RvEmptyListener;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.scanner.CodeScannerActivity;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.PlayServicesUtil;
import com.synchroteam.utils.RequestCode;
import com.synchroteam.utils.SharedPref;
import com.synchroteam.utils.SynchroteamUitls;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TreeMap;
import java.util.Vector;

import de.greenrobot.event.EventBus;


/**
 * Helper class to show the list of all jobs in the fragment.<p></p>
 * Created by Trident on 5/18/2016.
 */
public class AllJobsFragmentHelperNew implements HelperInterface, RvEmptyListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //------------------------------------INSTANCE...VARIABLES...DECLARATION...STARTS------------------------

    private RecyclerView rvAllJobs;
    private LinearLayoutManager linearLayoutManager;
    private TextView txtEmptyList;
    private LinearLayout linSearchSort;
    private com.synchroteam.TypefaceLibrary.TextView txtSortBy;
    private TextView txtIcSort;
    private TextView txtIcSearch;
    private TextView txtIcBarcode;
    private EditText edSearchJobs;
    private AllJobsSortingDialog dialogSorting;

    private FragmentManager fragmentManager;
    private SyncroTeamBaseActivity syncroTeamBaseActivity;
    private Dao dataAccessObject;
    private TreeMap<String, ArrayList<HashMap<String, String>>> allJobListbyDate;

    private AllJobsDateAdapterRv allJobsListAdapter;
    private AllJobsSortingAdapterRv mAllJobsSortingAdapter;

    private ArrayList<HashMap<String, String>> allJobArrayList;
    private ArrayList<HashMap<String, String>> allJobList;
    private Filter mAllJobFilter;
    private Filter mJobSortingFilter;

    private BaseFragment baseFragment;
    private User user;
    private ProgressBar progressBarDeadlineJob;
    private DateFormat dateFormatCurrentJobList;
    private Format timeFormatCurrentJobList;

    private String mSortingName;
    private int mSortingOption;
    private boolean isDateAdapter;
    private boolean isSortAdapter;

    private Timer myTimer;
    protected LocationManager locationManager;
    private LocationRequest mLocationRequest;
    private GoogleApiClient locationClient;
    private boolean isLocationClientConnected = false;
    private AllJobsFragment allJobsFragment;

    private int index;
    private int searchIndex;
    private boolean isUserSearching = false;
    private boolean isDbUpdated = false;

    private boolean isLoading;
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    private Dao dataAceesObject;
    private Date dateSelected;
    DateFormat dateFormat;
    //-----QUICK---RETURN---LISTVIEW---PARAMS-----

//    private int mCachedVerticalScrollRange;
//    private int mQuickReturnHeight;

//    private static final int STATE_ONSCREEN = 0;
//    private static final int STATE_OFFSCREEN = 1;
//    private static final int STATE_RETURNING = 2;
//    private int mState = STATE_ONSCREEN;
//    private int mScrollY;
//    private int mMinRawY = 0;
//    private int rawY;

//    private TranslateAnimation anim;

    //-----QUICK---RETURN---LISTVIEW---PARAMS-----

    private static final String TAG = AllJobsFragmentHelperNew.class.getSimpleName();
    private ArrayList<ArrayList<CommonJobBean>> allReportCurrUPcomingLate;
    private ArrayList<CommonJobBean> AllCollection;
    //------------------------------------INSTANCE...VARIABLES...DECLARATION...ENDS--------------------------

    /**
     * Instantiates a new all job fragment helper object.
     *
     * @param syncroTeamBaseActivity : base activity
     * @param baseFragment           : base fragment
     */
    public AllJobsFragmentHelperNew(SyncroTeamBaseActivity syncroTeamBaseActivity, AllJobsFragment allJobsFragment, BaseFragment baseFragment) {
        this.syncroTeamBaseActivity = syncroTeamBaseActivity;
        this.allJobsFragment = allJobsFragment;
        dataAccessObject = DaoManager.getInstance();
        this.baseFragment = baseFragment;
        user = dataAccessObject.getUser();
    }

    //-------------------------------------OVERRIDDEN...METHODS...STARTS--------------------------------------

    @Override
    public View inflateLayout(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.layout_all_jobs_new,
                container, false);
        dataAceesObject = DaoManager.getInstance();
        SharedPref.setSortedJobNumber("", syncroTeamBaseActivity);
        SharedPref.setSortingOption(0, syncroTeamBaseActivity);
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        initiateView(view);
        allReportCurrUPcomingLate = new ArrayList<>();
        AllCollection = new ArrayList<>();
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        dateSelected = calendar.getTime();


        progressBarDeadlineJob = (ProgressBar) view
                .findViewById(R.id.progressBarDeadlineJob);
        dateFormatCurrentJobList = DateFormat.getDateInstance(DateFormat.LONG);
        timeFormatCurrentJobList = android.text.format.DateFormat
                .getTimeFormat(syncroTeamBaseActivity);

        if (mSortingOption == KEYS.AllJobSortingOptions.SORT_BY_NEARBY_JOB) {
            String mSortedJobNumber = SharedPref.getSortedJobNumber(syncroTeamBaseActivity);
            HashMap<String, String> hmLatLong = dataAccessObject.getLatLongAllJobs(mSortedJobNumber);
            if (hmLatLong.size() > 0) {
                new FetchAllJobs().execute("" + mSortingOption, hmLatLong.get(KEYS.CurrentJobs.LAT), hmLatLong.get(KEYS.CurrentJobs.LON));
            } else {
                Toast.makeText(syncroTeamBaseActivity.getApplicationContext(), R.string.txt_jobno_mismatch, Toast.LENGTH_LONG).show();
            }
        } else if (mSortingOption != KEYS.AllJobSortingOptions.SORT_BY_NEARBY) {
            new FetchAllJobs().execute("" + mSortingOption, "", "");
        }

        return view;
    }


    @Override
    public void initiateView(View v) {
        rvAllJobs = (RecyclerView) v.findViewById(R.id.rv_all_jobs);
        linearLayoutManager = new LinearLayoutManager(syncroTeamBaseActivity);
        txtEmptyList = (TextView) v.findViewById(R.id.empty_text);
        linSearchSort = (LinearLayout) v.findViewById(R.id.lin_search_sort);
        RelativeLayout relSort = (RelativeLayout) v.findViewById(R.id.rel_sort);
        txtSortBy = (com.synchroteam.TypefaceLibrary.TextView) v.findViewById(R.id.txt_sort_by);
        txtIcSort = (TextView) v.findViewById(R.id.ic_sort);
        txtIcSearch = (TextView) v.findViewById(R.id.ic_search);
        txtIcBarcode = (TextView) v.findViewById(R.id.ic_barcode);
        edSearchJobs = (EditText) v.findViewById(R.id.ed_search_all_jobs);

        mSortingOption = SharedPref.getSortingOption(syncroTeamBaseActivity);

        fragmentManager = syncroTeamBaseActivity.getSupportFragmentManager();
        dialogSorting = AllJobsSortingDialog.getInstance(KEYS.AllJobSortingOptions.ALL_JOBS_SORTING,
                null, false, true, baseFragment);
        dialogSorting.setTargetFragment(baseFragment, 300);

        index = 1;
        searchIndex = 1;

        String[] arrSortingOptions = {syncroTeamBaseActivity.getString(R.string.txt_date_sorting),
                syncroTeamBaseActivity.getString(R.string.txt_customer_sorting),
                syncroTeamBaseActivity.getString(R.string.txt_site_sorting),
                syncroTeamBaseActivity.getString(R.string.txt_equipment_sorting),
                syncroTeamBaseActivity.getString(R.string.txt_nearby_sorting),
                syncroTeamBaseActivity.getString(R.string.txt_nearby_job_sorting),
                syncroTeamBaseActivity.getString(R.string.txt_town_sorting)};
        txtSortBy.setText(arrSortingOptions[mSortingOption]);

        relSort.setOnClickListener(clickListener);
        txtIcBarcode.setOnClickListener(clickListener);
        edSearchJobs.setOnFocusChangeListener(mFocusChangeListener);

        setTypeFaceFont();

        setLocationServices();

        locationClient.connect();

        animateFirstTime();

    }

    /**
     * Hide the container for the first time after view renders.
     */
    private void animateFirstTime() {
        linSearchSort.post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    ViewPropertyAnimator animator = linSearchSort.animate()
                            .translationY(-linSearchSort.getHeight())
                            .setInterpolator(new FastOutSlowInInterpolator())
                            .setDuration(200);
                    animator.start();
                } else {
                    Animation slideUp = AnimationUtils.loadAnimation(syncroTeamBaseActivity, R.anim.slide_up);
                    linSearchSort.startAnimation(slideUp);
                }
            }
        });
    }

    @Override
    public void doOnSyncronize() {
        if (mSortingOption == KEYS.AllJobSortingOptions.SORT_BY_NEARBY) {
            geocoder();
        } else if (mSortingOption == KEYS.AllJobSortingOptions.SORT_BY_NEARBY_JOB) {
            String mSortedJobNumber = SharedPref.getSortedJobNumber(syncroTeamBaseActivity);
            HashMap<String, String> hmLatLong = dataAccessObject.getLatLongAllJobs(mSortedJobNumber);
            if (hmLatLong.size() > 0) {
                new FetchAllJobs().execute("" + mSortingOption, hmLatLong.get(KEYS.CurrentJobs.LAT), hmLatLong.get(KEYS.CurrentJobs.LON));
            } else {
                Toast.makeText(syncroTeamBaseActivity.getApplicationContext(), R.string.txt_jobno_mismatch, Toast.LENGTH_LONG).show();
            }
        } else {
            new FetchAllJobs().execute("" + mSortingOption, "", "");
        }
    }

    @Override
    public void onReturnToActivity(int requestCode) {
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        isLocationClientConnected = true;
        if (mSortingOption == KEYS.AllJobSortingOptions.SORT_BY_NEARBY) {
            geocoder();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        isLocationClientConnected = false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        isLocationClientConnected = false;
    }

    //-------------------------------------OVERRIDDEN...METHODS...ENDS----------------------------------------

    //--------------------------------------LOCAL...METHODS...STARTS------------------------------------------

    /**
     * Calls on fragment stop.
     */
    public void onStop() {
        locationClient.disconnect();
    }

    /**
     * Calls from All jobs fragment
     */
    public void onReturnToFragment(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCode.REQUEST_CODE_TEXT_BARCODE) {
            edSearchJobs.setText(data.getStringExtra("SCAN_RESULT_CODE"));
            edSearchJobs.setSelection(edSearchJobs.getText().toString()
                    .length());
        } else if (requestCode == RequestCode.REQUEST_CODE_GPS_SETTINGS) {
            if ((locationClient != null) && (locationClient.isConnected())) {
                geocoder();
            } else if (locationClient != null) {
                locationClient.connect();
            }

        }

    }

    /**
     * Sets font awesome font to text views.
     */
    private void setTypeFaceFont() {
        Typeface tfFontAwesome = Typeface.createFromAsset(syncroTeamBaseActivity.getAssets(), syncroTeamBaseActivity.getString(R.string.fontName_fontAwesome));
        Typeface tfMaterial = Typeface.createFromAsset(syncroTeamBaseActivity.getAssets(), syncroTeamBaseActivity.getString(R.string.fontName_material_icon));

        txtIcSort.setTypeface(tfMaterial);
        txtIcSearch.setTypeface(tfMaterial);
        txtIcBarcode.setTypeface(tfFontAwesome);

    }

    /**
     * Creates the adapter and inflate data in list view.
     */
    private void createAdapterAndInflateDataInListView() {
        if (allJobListbyDate != null) {
            allJobListbyDate.clear();
        } else {
            allJobListbyDate = new TreeMap<>(
                    new Comparator<String>() {

                        @Override
                        public int compare(String lhs, String rhs) {
                            return lhs.compareTo(rhs);
                        }
                    });
        }

        if (allJobArrayList != null) {
            allJobArrayList.clear();
        } else {
            allJobArrayList = new ArrayList<>();
        }

        HashMap<String, String> allJobHashMap;


        for (int i = 0; i < AllCollection.size(); i++) {
            CommonJobBean allJobsBean = AllCollection.get(i);
            int jobStatus = allJobsBean.getCd_status_interv();

            String jobNumber = allJobsBean.getType_Interv();

            allJobHashMap = new HashMap<>();

            allJobHashMap.put(KEYS.CurrentJobs.DISPO, "false");

            if (!TextUtils.isEmpty(allJobsBean.getRef_customer())) {
                jobNumber = "#" + allJobsBean.getRef_customer()
                        + " - " + allJobsBean.getType_Interv();
//                allJobHashMap.put(KEYS.CurrentJobs.JOB_NUMBER, allJobsBean.getRef_customer());
            } else if (allJobsBean.getNo_interv() != 0) {
                jobNumber = "#" + allJobsBean.getNo_interv()
                        + " - " + allJobsBean.getType_Interv();
//                allJobHashMap.put(KEYS.CurrentJobs.JOB_NUMBER, "" + allJobsBean.getNo_interv());
            }
            allJobHashMap.put(KEYS.CurrentJobs.ALL_JOB_HEADER,
                    allJobsBean.getHeaderDate());
            allJobHashMap.put(KEYS.CurrentJobs.IS_CURRENT_JOB,
                    KEYS.CurrentJobs.TRUE);
            allJobHashMap.put(KEYS.CurrentJobs.ID,
                    allJobsBean.getId());
            allJobHashMap.put(KEYS.CurrentJobs.CD_STATUS, String
                    .valueOf(allJobsBean.getCd_status_interv()));
            allJobHashMap.put(KEYS.CurrentJobs.ID_USER,
                    String.valueOf(allJobsBean.getIdUser()));

            // put nom_client_interv name
            allJobHashMap.put(KEYS.CurrentJobs.NOM_CLIENT_INTERV,
                    allJobsBean.getNom_client_interv());

            allJobHashMap.put(KEYS.CurrentJobs.DESC,
                    String.valueOf(allJobsBean.getDesc()));

            allJobHashMap.put(KEYS.CurrentJobs.TELCEL,
                    allJobsBean.getTel_contact());

            allJobHashMap.put(KEYS.CurrentJobs.MOBILE_CONTACT,
                    allJobsBean.getMobileContact());

            allJobHashMap.put(KEYS.CurrentJobs.ID_MODEL, String
                    .valueOf(allJobsBean.getId_model_rapport()));
            allJobHashMap.put(KEYS.CurrentJobs.TYPE, jobNumber);
            allJobHashMap.put(KEYS.CurrentJobs.PRIORITY,
                    allJobsBean.getPriorite() + "");

            allJobHashMap.put(KEYS.CurrentJobs.LAT,
                    allJobsBean.getLat());
            allJobHashMap.put(KEYS.CurrentJobs.LON,
                    allJobsBean.getLon());

            allJobHashMap.put(KEYS.CurrentJobs.NOMSITE,
                    allJobsBean.getNom_site_interv());
            allJobHashMap.put(KEYS.CurrentJobs.NOMEQUIPMENT,
                    allJobsBean.getNom_equipement());
            allJobHashMap.put(KEYS.CurrentJobs.IDSITE,
                    String.valueOf(allJobsBean.getIdSite()));
            allJobHashMap.put(KEYS.CurrentJobs.IDCLIENT,
                    String.valueOf(allJobsBean.getIdClient()));
            allJobHashMap.put(KEYS.CurrentJobs.IDEQUIPMENT,
                    String.valueOf(allJobsBean.getIdEquipement()));
            allJobHashMap.put(KEYS.CurrentJobs.ADR_VILLE,
                    String.valueOf(allJobsBean.getAdr_interv_ville()));
            allJobHashMap.put(KEYS.CurrentJobs.ADR_GLOBAL,
                    String.valueOf(allJobsBean.getAdr_interv_globale()));

            allJobHashMap.put(KEYS.CurrentJobs.DESC, allJobsBean.getDesc());
            allJobHashMap.put(KEYS.CurrentJobs.ADR_COMPLEMENT,
                    allJobsBean.getAdr_interv_complement());
            allJobHashMap.put(KEYS.CurrentJobs.MOBILE_CONTACT,
                    String.valueOf(allJobsBean.getMobileContact()));

            //adding custom fields...
            allJobHashMap.put(KEYS.CurrentJobs.CF_INTERVENTION, dataAccessObject.getAllCFInterv(allJobsBean.getId()));
            allJobHashMap.put(KEYS.CurrentJobs.CF_SITE, dataAccessObject.getAllCFSite(allJobsBean.getIdSite()));
            allJobHashMap.put(KEYS.CurrentJobs.CF_CLIENT, dataAccessObject.getAllCFClient(allJobsBean.getIdClient()));
            allJobHashMap.put(KEYS.CurrentJobs.CF_EQUIPMENT, dataAccessObject.getAllCFEquip(allJobsBean.getIdEquipement()));

            allJobHashMap.put(KEYS.CurrentJobs.DATEMEETING,
                    String.valueOf(allJobsBean.getDt_meeting()));

            Date date = getDateFromDbFormat(allJobsBean
                    .getDt_deb_prev());

            String dateToshow = null;

            if (jobStatus == KEYS.CurrentJobs.JOB__STARTED) {

                if (user.getId() != allJobsBean.getIdUser()) {
                    try {
                        dateToshow = dataAccessObject
                                .getDateWithRequiredPresettedPattern(date);
                    } catch (ParseException e) {
                        Logger.printException(e);
                    }
                    if (!TextUtils.isEmpty(dateToshow)) {

                        String[] dateTopass = dateToshow.split("/");

                        allJobHashMap.put(
                                KEYS.CurrentJobs.DATE_TO_SHOW,
                                dateTopass[0]);

                        allJobHashMap.put(
                                KEYS.CurrentJobs.TIME_TO_SHOW,
                                dateTopass[1]);
                    }
                } else {

                    dateToshow = dataAccessObject
                            .getJobStartTime(allJobsBean.getId());
                    if (!TextUtils.isEmpty(dateToshow)) {

                        String[] dateTopass = dateToshow.split("/");

                        allJobHashMap.put(
                                KEYS.CurrentJobs.DATE_TO_SHOW,
                                dateTopass[0]);

                        allJobHashMap.put(
                                KEYS.CurrentJobs.TIME_TO_SHOW,
                                dateTopass[1]);
                    }

                }

            } else if (jobStatus == KEYS.CurrentJobs.JOB__SUSPENDED) {

                if (user.getId() != allJobsBean.getIdUser()) {

                    if (!TextUtils.isEmpty(dateToshow)) {

                        allJobHashMap.put(
                                KEYS.CurrentJobs.DATE_TO_SHOW, "");

                        allJobHashMap.put(
                                KEYS.CurrentJobs.TIME_TO_SHOW, "");
                    }
                } else {

                    dateToshow = dataAccessObject
                            .getJobSuspendedTime(allJobsBean
                                    .getId());
                    if (!TextUtils.isEmpty(dateToshow)) {

                        String[] dateTopass = dateToshow.split("/");

                        allJobHashMap.put(
                                KEYS.CurrentJobs.DATE_TO_SHOW,
                                dateTopass[0]);

                        allJobHashMap.put(
                                KEYS.CurrentJobs.TIME_TO_SHOW,
                                dateTopass[1]);
                    }
                }

            } else {

                allJobHashMap.put(KEYS.CurrentJobs.DATE_TO_SHOW,
                        dateFormatCurrentJobList.format(date) + "");

                allJobHashMap.put(KEYS.CurrentJobs.TIME_TO_SHOW,
                        timeFormatCurrentJobList.format(date));
            }

            if (allJobListbyDate.containsKey(allJobHashMap
                    .get(KEYS.CurrentJobs.ALL_JOB_HEADER))) {
                allJobListbyDate.get(
                        allJobHashMap
                                .get(KEYS.CurrentJobs.ALL_JOB_HEADER))
                        .add(allJobHashMap);

                allJobHashMap.put(KEYS.CurrentJobs.HAS_HEADER, "false");
                allJobArrayList.add(allJobHashMap);
            } else {
                allJobListbyDate.put(allJobHashMap
                                .get(KEYS.CurrentJobs.ALL_JOB_HEADER),
                        new ArrayList<HashMap<String, String>>());
                allJobListbyDate.get(
                        allJobHashMap
                                .get(KEYS.CurrentJobs.ALL_JOB_HEADER))
                        .add(allJobHashMap);

                allJobHashMap.put(KEYS.CurrentJobs.HAS_HEADER, "true");
                allJobArrayList.add(allJobHashMap);
            }

        }

    }

    public void getListForSortingDev503(final int options, String latitude, String longitude) {
        if (allJobList != null) {
            allJobList.clear();
        } else {
            allJobList = new ArrayList<>();
        }

        HashMap<String, String> allJobHashMap;

        ArrayList<CommonListBean> allJobBeanVector = null;

        CurrentJobDataBean allJobsDataBean;

        if (options == KEYS.AllJobSortingOptions.SORT_BY_NEARBY || options == KEYS.AllJobSortingOptions.SORT_BY_NEARBY_JOB) {
//            if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {
            //dev 51.0.3
//                allJobsDataBean = dataAccessObject.getAllInterventionNearByUpdated(latitude, longitude,user.getId());
            allJobsDataBean = dataAccessObject.getAllInterventionNearBy(latitude, longitude);
            allJobBeanVector = allJobsDataBean.getCommonJobDataBean();

            //if two or more jobs have the same lat & long, sort them according to the job number.
            Collections.sort(allJobBeanVector, new Comparator<CommonListBean>() {
                @Override
                public int compare(CommonListBean lhs, CommonListBean rhs) {
                    CommonJobBean lhsB = (CommonJobBean) lhs;
                    CommonJobBean rhsB = (CommonJobBean) rhs;
                    return getSortingResultforNearby(lhsB, rhsB);
                }
            });
//            }
        } else {
            allJobsDataBean = dataAccessObject.getAllIntervention();
            Log.e("siz", AllCollection.size() + "");
            //dev 51.0.3
//            allJobsDataBean = dataAccessObject.getAllInterventionUpdated(user.getId());
            allJobBeanVector = allJobsDataBean.getCommonJobDataBean();
            Collections.sort(allJobBeanVector, new Comparator<CommonListBean>() {
                @Override
                public int compare(CommonListBean lhs, CommonListBean rhs) {
                    CommonJobBean lhsB = (CommonJobBean) lhs;
                    CommonJobBean rhsB = (CommonJobBean) rhs;
                    switch (options) {
                        case KEYS.AllJobSortingOptions.SORT_BY_CLIENT:
                            return getSortingResultforClient(lhsB, rhsB);
                        case KEYS.AllJobSortingOptions.SORT_BY_SITE:
                            return getSortingResultforSite(lhsB, rhsB);
                        case KEYS.AllJobSortingOptions.SORT_BY_EQUIPMENT:
                            return getSortingResultforEquipment(lhsB, rhsB);
                        case KEYS.AllJobSortingOptions.SORT_BY_TOWN:
                            return getSortingResultforTown(lhsB, rhsB);
                        case KEYS.AllJobSortingOptions.SORT_BY_PRIORITY:
                            return getSortingResultForPriority(lhsB, rhsB);
                        default:
                            return getSortingResultforClient(lhsB, rhsB);
                    }
                }
            });
        }

        for(int i=0;i<allJobBeanVector.size();i++){

                CommonListBean commonListBean = allJobBeanVector.get(i);
                CommonJobBean allJobsBean = (CommonJobBean) commonListBean;
                int jobStatus = allJobsBean.getCd_status_interv();

                String jobNumber = allJobsBean.getType_Interv();

                allJobHashMap = new HashMap<>();
                allJobHashMap.put(KEYS.CurrentJobs.DISPO, "false");

                if (!TextUtils.isEmpty(allJobsBean.getRef_customer())) {
                    jobNumber = "#" + allJobsBean.getRef_customer()
                            + " - " + allJobsBean.getType_Interv();
//                allJobHashMap.put(KEYS.CurrentJobs.JOB_NUMBER, allJobsBean.getRef_customer());
                } else if (allJobsBean.getNo_interv() != 0) {
                    jobNumber = "#" + allJobsBean.getNo_interv()
                            + " - " + allJobsBean.getType_Interv();
//                allJobHashMap.put(KEYS.CurrentJobs.JOB_NUMBER, "" + allJobsBean.getNo_interv());
                }
                allJobHashMap.put(KEYS.CurrentJobs.ALL_JOB_HEADER,
                        allJobsBean.getHeaderDate());
                allJobHashMap.put(KEYS.CurrentJobs.IS_CURRENT_JOB,
                        KEYS.CurrentJobs.TRUE);
                allJobHashMap.put(KEYS.CurrentJobs.ID,
                        allJobsBean.getId());
                allJobHashMap.put(KEYS.CurrentJobs.CD_STATUS, String
                        .valueOf(allJobsBean.getCd_status_interv()));
                allJobHashMap.put(KEYS.CurrentJobs.ID_USER,
                        String.valueOf(allJobsBean.getIdUser()));

                // put nom_client_interv name
                allJobHashMap.put(KEYS.CurrentJobs.NOM_CLIENT_INTERV,
                        allJobsBean.getNom_client_interv());

            allJobHashMap.put(KEYS.CurrentJobs.DESC,
                    String.valueOf(allJobsBean.getDesc()));

            allJobHashMap.put(KEYS.CurrentJobs.TELCEL,
                    allJobsBean.getTel_contact());

            allJobHashMap.put(KEYS.CurrentJobs.MOBILE_CONTACT,
                    allJobsBean.getMobileContact());

                allJobHashMap.put(KEYS.CurrentJobs.ID_MODEL, String
                        .valueOf(allJobsBean.getId_model_rapport()));
                allJobHashMap.put(KEYS.CurrentJobs.TYPE, jobNumber);
                allJobHashMap.put(KEYS.CurrentJobs.PRIORITY,
                        allJobsBean.getPriorite() + "");

                allJobHashMap.put(KEYS.CurrentJobs.LAT,
                        allJobsBean.getLat());
                allJobHashMap.put(KEYS.CurrentJobs.LON,
                        allJobsBean.getLon());
                Logger.output(TAG, "lat " + allJobsBean.getLat() + " Lon " + allJobsBean.getLon());

                allJobHashMap.put(KEYS.CurrentJobs.NOMSITE,
                        allJobsBean.getNom_site_interv());
                allJobHashMap.put(KEYS.CurrentJobs.NOMEQUIPMENT,
                        allJobsBean.getNom_equipement());
                allJobHashMap.put(KEYS.CurrentJobs.IDSITE,
                        String.valueOf(allJobsBean.getIdSite()));
                allJobHashMap.put(KEYS.CurrentJobs.IDCLIENT,
                        String.valueOf(allJobsBean.getIdClient()));
                allJobHashMap.put(KEYS.CurrentJobs.IDEQUIPMENT,
                        String.valueOf(allJobsBean.getIdEquipement()));
                allJobHashMap.put(KEYS.CurrentJobs.ADR_VILLE,
                        String.valueOf(allJobsBean.getAdr_interv_ville()));
                allJobHashMap.put(KEYS.CurrentJobs.ADR_GLOBAL,
                        String.valueOf(allJobsBean.getAdr_interv_globale()));

            allJobHashMap.put(KEYS.CurrentJobs.DESC, allJobsBean.getDesc());
            allJobHashMap.put(KEYS.CurrentJobs.ADR_COMPLEMENT,
                    allJobsBean.getAdr_interv_complement());
            allJobHashMap.put(KEYS.CurrentJobs.MOBILE_CONTACT,
                    String.valueOf(allJobsBean.getMobileContact()));

                //adding custom fields...
                allJobHashMap.put(KEYS.CurrentJobs.CF_INTERVENTION, dataAccessObject.getAllCFInterv(allJobsBean.getId()));
                allJobHashMap.put(KEYS.CurrentJobs.CF_SITE, dataAccessObject.getAllCFSite(allJobsBean.getIdSite()));
                allJobHashMap.put(KEYS.CurrentJobs.CF_CLIENT, dataAccessObject.getAllCFClient(allJobsBean.getIdClient()));
                allJobHashMap.put(KEYS.CurrentJobs.CF_EQUIPMENT, dataAccessObject.getAllCFEquip(allJobsBean.getIdEquipement()));

                allJobHashMap.put(KEYS.CurrentJobs.DATEMEETING,
                        String.valueOf(allJobsBean.getDt_meeting()));

                Date date = getDateFromDbFormat(allJobsBean
                        .getDt_deb_prev());

                String dateToshow = null;

                if (jobStatus == KEYS.CurrentJobs.JOB__STARTED) {

                    if (user.getId() != allJobsBean.getIdUser()) {
                        try {
                            dateToshow = dataAccessObject
                                    .getDateWithRequiredPresettedPattern(date);
                        } catch (ParseException e) {
                            Logger.printException(e);
                        }
                        if (!TextUtils.isEmpty(dateToshow)) {

                            String[] dateTopass = dateToshow.split("/");

                            allJobHashMap.put(
                                    KEYS.CurrentJobs.DATE_TO_SHOW,
                                    dateTopass[0]);

                            allJobHashMap.put(
                                    KEYS.CurrentJobs.TIME_TO_SHOW,
                                    dateTopass[1]);
                        }
                    } else {

                        dateToshow = dataAccessObject
                                .getJobStartTime(allJobsBean.getId());
                        if (!TextUtils.isEmpty(dateToshow)) {

                            String[] dateTopass = dateToshow.split("/");

                            allJobHashMap.put(
                                    KEYS.CurrentJobs.DATE_TO_SHOW,
                                    dateTopass[0]);

                            allJobHashMap.put(
                                    KEYS.CurrentJobs.TIME_TO_SHOW,
                                    dateTopass[1]);
                        }

                    }

                } else if (jobStatus == KEYS.CurrentJobs.JOB__SUSPENDED) {

                    if (user.getId() != allJobsBean.getIdUser()) {

                        if (!TextUtils.isEmpty(dateToshow)) {

                            allJobHashMap.put(
                                    KEYS.CurrentJobs.DATE_TO_SHOW, "");

                            allJobHashMap.put(
                                    KEYS.CurrentJobs.TIME_TO_SHOW, "");
                        }
                    } else {

                        dateToshow = dataAccessObject
                                .getJobSuspendedTime(allJobsBean
                                        .getId());
                        if (!TextUtils.isEmpty(dateToshow)) {

                            String[] dateTopass = dateToshow.split("/");

                            allJobHashMap.put(
                                    KEYS.CurrentJobs.DATE_TO_SHOW,
                                    dateTopass[0]);

                            allJobHashMap.put(
                                    KEYS.CurrentJobs.TIME_TO_SHOW,
                                    dateTopass[1]);
                        }
                    }

                } else {

                    allJobHashMap.put(KEYS.CurrentJobs.DATE_TO_SHOW,
                            dateFormatCurrentJobList.format(date) + "");

                    allJobHashMap.put(KEYS.CurrentJobs.TIME_TO_SHOW,
                            timeFormatCurrentJobList.format(date));
                }

                allJobList.add(allJobHashMap);

            }

//        Enumeration<CommonListBean> en = allJobBeanVector.elements();
//        while (en.hasMoreElements()) {
//
//            CommonListBean commonListBean = en.nextElement();
//            CommonJobBean allJobsBean = (CommonJobBean) commonListBean;
//            int jobStatus = allJobsBean.getCd_status_interv();
//
//            String jobNumber = allJobsBean.getType_Interv();
//
//            allJobHashMap = new HashMap<>();
//            allJobHashMap.put(KEYS.CurrentJobs.DISPO, "false");
//
//            if (!TextUtils.isEmpty(allJobsBean.getRef_customer())) {
//                jobNumber = "#" + allJobsBean.getRef_customer()
//                        + " - " + allJobsBean.getType_Interv();
////                allJobHashMap.put(KEYS.CurrentJobs.JOB_NUMBER, allJobsBean.getRef_customer());
//            } else if (allJobsBean.getNo_interv() != 0) {
//                jobNumber = "#" + allJobsBean.getNo_interv()
//                        + " - " + allJobsBean.getType_Interv();
////                allJobHashMap.put(KEYS.CurrentJobs.JOB_NUMBER, "" + allJobsBean.getNo_interv());
//            }
//            allJobHashMap.put(KEYS.CurrentJobs.ALL_JOB_HEADER,
//                    allJobsBean.getHeaderDate());
//            allJobHashMap.put(KEYS.CurrentJobs.IS_CURRENT_JOB,
//                    KEYS.CurrentJobs.TRUE);
//            allJobHashMap.put(KEYS.CurrentJobs.ID,
//                    allJobsBean.getId());
//            allJobHashMap.put(KEYS.CurrentJobs.CD_STATUS, String
//                    .valueOf(allJobsBean.getCd_status_interv()));
//            allJobHashMap.put(KEYS.CurrentJobs.ID_USER,
//                    String.valueOf(allJobsBean.getIdUser()));
//
//            // put nom_client_interv name
//            allJobHashMap.put(KEYS.CurrentJobs.NOM_CLIENT_INTERV,
//                    allJobsBean.getNom_client_interv());
//
//
//            allJobHashMap.put(KEYS.CurrentJobs.ID_MODEL, String
//                    .valueOf(allJobsBean.getId_model_rapport()));
//            allJobHashMap.put(KEYS.CurrentJobs.TYPE, jobNumber);
//            allJobHashMap.put(KEYS.CurrentJobs.PRIORITY,
//                    allJobsBean.getPriorite() + "");
//
//            allJobHashMap.put(KEYS.CurrentJobs.LAT,
//                    allJobsBean.getLat());
//            allJobHashMap.put(KEYS.CurrentJobs.LON,
//                    allJobsBean.getLon());
//            Logger.output(TAG, "lat " + allJobsBean.getLat() + " Lon " + allJobsBean.getLon());
//
//            allJobHashMap.put(KEYS.CurrentJobs.NOMSITE,
//                    allJobsBean.getNom_site_interv());
//            allJobHashMap.put(KEYS.CurrentJobs.NOMEQUIPMENT,
//                    allJobsBean.getNom_equipement());
//            allJobHashMap.put(KEYS.CurrentJobs.IDSITE,
//                    String.valueOf(allJobsBean.getIdSite()));
//            allJobHashMap.put(KEYS.CurrentJobs.IDCLIENT,
//                    String.valueOf(allJobsBean.getIdClient()));
//            allJobHashMap.put(KEYS.CurrentJobs.IDEQUIPMENT,
//                    String.valueOf(allJobsBean.getIdEquipement()));
//            allJobHashMap.put(KEYS.CurrentJobs.ADR_VILLE,
//                    String.valueOf(allJobsBean.getAdr_interv_ville()));
//
//            //adding custom fields...
//            allJobHashMap.put(KEYS.CurrentJobs.CF_INTERVENTION, dataAccessObject.getAllCFInterv(allJobsBean.getId()));
//            allJobHashMap.put(KEYS.CurrentJobs.CF_SITE, dataAccessObject.getAllCFSite(allJobsBean.getIdSite()));
//            allJobHashMap.put(KEYS.CurrentJobs.CF_CLIENT, dataAccessObject.getAllCFClient(allJobsBean.getIdClient()));
//            allJobHashMap.put(KEYS.CurrentJobs.CF_EQUIPMENT, dataAccessObject.getAllCFEquip(allJobsBean.getIdEquipement()));
//
//            allJobHashMap.put(KEYS.CurrentJobs.DATEMEETING,
//                    String.valueOf(allJobsBean.getDt_meeting()));
//
//            Date date = getDateFromDbFormat(allJobsBean
//                    .getDt_deb_prev());
//
//            String dateToshow = null;
//
//            if (jobStatus == KEYS.CurrentJobs.JOB__STARTED) {
//
//                if (user.getId() != allJobsBean.getIdUser()) {
//                    try {
//                        dateToshow = dataAccessObject
//                                .getDateWithRequiredPresettedPattern(date);
//                    } catch (ParseException e) {
//                        Logger.printException(e);
//                    }
//                    if (!TextUtils.isEmpty(dateToshow)) {
//
//                        String[] dateTopass = dateToshow.split("/");
//
//                        allJobHashMap.put(
//                                KEYS.CurrentJobs.DATE_TO_SHOW,
//                                dateTopass[0]);
//
//                        allJobHashMap.put(
//                                KEYS.CurrentJobs.TIME_TO_SHOW,
//                                dateTopass[1]);
//                    }
//                } else {
//
//                    dateToshow = dataAccessObject
//                            .getJobStartTime(allJobsBean.getId());
//                    if (!TextUtils.isEmpty(dateToshow)) {
//
//                        String[] dateTopass = dateToshow.split("/");
//
//                        allJobHashMap.put(
//                                KEYS.CurrentJobs.DATE_TO_SHOW,
//                                dateTopass[0]);
//
//                        allJobHashMap.put(
//                                KEYS.CurrentJobs.TIME_TO_SHOW,
//                                dateTopass[1]);
//                    }
//
//                }
//
//            } else if (jobStatus == KEYS.CurrentJobs.JOB__SUSPENDED) {
//
//                if (user.getId() != allJobsBean.getIdUser()) {
//
//                    if (!TextUtils.isEmpty(dateToshow)) {
//
//                        allJobHashMap.put(
//                                KEYS.CurrentJobs.DATE_TO_SHOW, "");
//
//                        allJobHashMap.put(
//                                KEYS.CurrentJobs.TIME_TO_SHOW, "");
//                    }
//                } else {
//
//                    dateToshow = dataAccessObject
//                            .getJobSuspendedTime(allJobsBean
//                                    .getId());
//                    if (!TextUtils.isEmpty(dateToshow)) {
//
//                        String[] dateTopass = dateToshow.split("/");
//
//                        allJobHashMap.put(
//                                KEYS.CurrentJobs.DATE_TO_SHOW,
//                                dateTopass[0]);
//
//                        allJobHashMap.put(
//                                KEYS.CurrentJobs.TIME_TO_SHOW,
//                                dateTopass[1]);
//                    }
//                }
//
//            } else {
//
//                allJobHashMap.put(KEYS.CurrentJobs.DATE_TO_SHOW,
//                        dateFormatCurrentJobList.format(date) + "");
//
//                allJobHashMap.put(KEYS.CurrentJobs.TIME_TO_SHOW,
//                        timeFormatCurrentJobList.format(date));
//            }
//
//            allJobList.add(allJobHashMap);
//
//        }

    }

    /**
     * Set the list adapter to deadlineExccedeListView.
     */
    private void setAllJobsListAdapter() {
        if (allJobsListAdapter == null || !isDateAdapter) {
            allJobsListAdapter = new AllJobsDateAdapterRv(syncroTeamBaseActivity, this,
                    allJobArrayList, false);
//            rvAllJobs.addFooterView(footerView);
            index = 1;
            searchIndex = 1;

            allJobsListAdapter.setIndexPosition(index);
            rvAllJobs.setLayoutManager(linearLayoutManager);
            rvAllJobs.setAdapter(allJobsListAdapter);


        } else {
            if (isDbUpdated) {
                allJobsListAdapter.setDuplicateList();
                isDbUpdated = false;
            }
            allJobsListAdapter.notifyDataSetChanged();
        }

        setEmptyViewForRv(allJobArrayList);

        resetSearchField();

        if (mAllJobFilter == null) {
            mAllJobFilter = allJobsListAdapter.getAllJobFilter();
            mJobSortingFilter = null;
            edSearchJobs.addTextChangedListener(mWatcher);
        }

//        rvAllJobs.setItemCount(allJobsListAdapter.getArrayCount());

        isDateAdapter = true;
        isSortAdapter = false;

        //check if header is already shown, if shown then shw the header for this adapter also
        if (mAllJobsSortingAdapter != null) {
            boolean isHeaderShown = mAllJobsSortingAdapter.isHeaderShown();
            if (isHeaderShown) {
                allJobsListAdapter.setHeaderShown(true);
                allJobsListAdapter.setIncrementValue(1);
                allJobsListAdapter.notifyDataSetChanged();
            } else {
                allJobsListAdapter.setHeaderShown(false);
                allJobsListAdapter.setIncrementValue(0);
                allJobsListAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Sets empty view for recycler view.
     *
     * @param arrayList
     */
    private void setEmptyViewForRv(ArrayList<HashMap<String, String>> arrayList) {
        if (arrayList.size() > 0) {
            rvAllJobs.setVisibility(View.VISIBLE);
            txtEmptyList.setVisibility(View.GONE);
        } else {
            rvAllJobs.setVisibility(View.GONE);
            txtEmptyList.setVisibility(View.VISIBLE);
        }
    }

    private void resetSearchField() {
        String mSearchText = edSearchJobs.getText().toString();
        if (!TextUtils.isEmpty(mSearchText)) {
            edSearchJobs.setText("");
            edSearchJobs.clearFocus();
        }
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
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    /**
     * Update database.
     */
    public void updateDatabase() {
        isDbUpdated = true;
        if (mSortingOption == KEYS.AllJobSortingOptions.SORT_BY_NEARBY) {
            Logger.output(TAG, "Nearby");
            if ((locationClient != null) && (locationClient.isConnected())) {
                geocoder();
            } else if (locationClient != null) {
                locationClient.connect();
            }
        } else if (mSortingOption == KEYS.AllJobSortingOptions.SORT_BY_NEARBY_JOB) {
            Logger.output(TAG, "Nearby job");
            String mSortedJobNumber = SharedPref.getSortedJobNumber(syncroTeamBaseActivity);
            HashMap<String, String> hmLatLong = dataAccessObject.getLatLongAllJobs(mSortedJobNumber);
            if (hmLatLong.size() > 0) {
                new FetchAllJobs().execute("" + mSortingOption, hmLatLong.get(KEYS.CurrentJobs.LAT), hmLatLong.get(KEYS.CurrentJobs.LON));
            } else {
                Toast.makeText(syncroTeamBaseActivity.getApplicationContext(), R.string.txt_jobno_mismatch, Toast.LENGTH_LONG).show();
            }
        } else {
            Logger.output(TAG, "others");
            new FetchAllJobs().execute("" + mSortingOption, "", "");
        }

        baseFragment.listUpdate();

    }


    /**
     * Sort job list by selected order.
     *
     * @param options    : selected option
     * @param optionName : name of sorting
     * @param jobNumber  : job number (for near by job option)
     */
    public void sortJobList(int options, String optionName, String jobNumber) {
        mSortingName = optionName;
        mSortingOption = options;

        if (mSortingOption == KEYS.AllJobSortingOptions.SORT_BY_NEARBY) {
            if ((locationClient != null) && (locationClient.isConnected())) {
                geocoder();
            } else if (locationClient != null) {
                locationClient.connect();
            }
        } else if (mSortingOption == KEYS.AllJobSortingOptions.SORT_BY_NEARBY_JOB) {
            txtSortBy.setText(optionName);
            SharedPref.setSortingOption(options, syncroTeamBaseActivity);
            HashMap<String, String> hmLatLong = dataAccessObject.getLatLongAllJobs(jobNumber);
            new FetchAllJobs().execute("" + mSortingOption, hmLatLong.get(KEYS.CurrentJobs.LAT), hmLatLong.get(KEYS.CurrentJobs.LON));
        } else {
            txtSortBy.setText(optionName);
            SharedPref.setSortingOption(options, syncroTeamBaseActivity);
            new FetchAllJobs().execute("" + mSortingOption, "", "");
        }
    }

    /**
     * Set the list adapter to deadlineExccedeListView.
     */
    private void setSortingListAdapter() {
        if (mAllJobsSortingAdapter == null || !isSortAdapter) {
            mAllJobsSortingAdapter = new AllJobsSortingAdapterRv(syncroTeamBaseActivity, this,
                    allJobList, false);
            index = 1;
            searchIndex = 1;

            mAllJobsSortingAdapter.setIndexPosition(index);
            rvAllJobs.setLayoutManager(linearLayoutManager);
            rvAllJobs.setAdapter(mAllJobsSortingAdapter);



        } else {
//            if (!isDbUpdated) {
//                index = 1;
//                searchIndex = 1;
//                mAllJobsSortingAdapter.setIndexPosition(index);
//            } else {
//                isDbUpdated = false;
//            }
            if (isDbUpdated) {
                mAllJobsSortingAdapter.setDuplicateList();
                isDbUpdated = false;
            }
            mAllJobsSortingAdapter.notifyDataSetChanged();
        }

        setEmptyViewForRv(allJobList);

        resetSearchField();

        if (mJobSortingFilter == null) {
            mJobSortingFilter = mAllJobsSortingAdapter.getJobFilter();
            mAllJobFilter = null;
            edSearchJobs.addTextChangedListener(mWatcher);
        }

//        rvAllJobs.setItemCount(mAllJobsSortingAdapter.getArrayCount());

        isSortAdapter = true;
        isDateAdapter = false;

        //check if header is already shown, if shown then shw the header for this adapter also
        if (allJobsListAdapter != null) {
            boolean isHeaderShown = allJobsListAdapter.isHeaderShown();
            if (isHeaderShown) {
                mAllJobsSortingAdapter.setHeaderShown(true);
                mAllJobsSortingAdapter.setIncrementValue(1);
                mAllJobsSortingAdapter.notifyDataSetChanged();
            } else {
                mAllJobsSortingAdapter.setHeaderShown(false);
                mAllJobsSortingAdapter.setIncrementValue(0);
                mAllJobsSortingAdapter.notifyDataSetChanged();
            }
        }

    }

    /**
     * code of previous developer.
     */
    public void geocoder() {

        boolean servicesConnected = PlayServicesUtil.checkPlayServices(syncroTeamBaseActivity);

        if (servicesConnected && isLocationClientConnected) {

            boolean isGPSEnabled;
            boolean isNetworkEnabled;

            try {
                locationManager = (LocationManager) syncroTeamBaseActivity.getSystemService(Context.LOCATION_SERVICE);

                isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                isNetworkEnabled = Build.FINGERPRINT.contains("generic") ? true : locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


                if (!isGPSEnabled && !isNetworkEnabled) {
                    showSettingsAlert();
                } else {
                    if (ActivityCompat.checkSelfPermission(syncroTeamBaseActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(syncroTeamBaseActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        allJobsFragment.callingPermissionLocation();
                    } else {
                        callingLocationFunctionalities();
                    }

                }

            } catch (Exception e) {
                Logger.printException(e);
            }

        }

    }


    @SuppressLint("MissingPermission")
    public void callingLocationFunctionalities() {

        if (!TextUtils.isEmpty(mSortingName)) {
            txtSortBy.setText(mSortingName);
        }
        SharedPref.setSortingOption(mSortingOption, syncroTeamBaseActivity);

        Toast.makeText(syncroTeamBaseActivity,
                syncroTeamBaseActivity.getString(R.string.gps_lancer), Toast.LENGTH_SHORT)
                .show();

        // locationClient.requestLocationUpdates(mLocationRequest,
        // locationListener);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                locationClient, mLocationRequest, locationListener);

        final Toast tag = Toast.makeText(syncroTeamBaseActivity,
                syncroTeamBaseActivity.getString(R.string.gps_delai), Toast.LENGTH_SHORT);
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

        DialogUtils.showProgressDialog(syncroTeamBaseActivity, syncroTeamBaseActivity
                        .getString(R.string.textPleaseWaitLable),
                syncroTeamBaseActivity
                        .getString(R.string.textFetchingLocation),
                false);
    }

    /**
     * Show settings alert.
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(syncroTeamBaseActivity);
        alertDialog.setTitle(syncroTeamBaseActivity.getString(R.string.textAlertLable) + "!");
        alertDialog.setMessage(syncroTeamBaseActivity.getString(R.string.textEnableLocationService));
        alertDialog.setPositiveButton(R.string.textYesLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        txtSortBy.setText(mSortingName);
                        SharedPref.setSortingOption(mSortingOption, syncroTeamBaseActivity);
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        syncroTeamBaseActivity.startActivity(intent);
                        allJobsFragment.startActivityForResult(intent, RequestCode.REQUEST_CODE_GPS_SETTINGS);
                    }
                });

        alertDialog.setNegativeButton(R.string.textNoLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        linSearchSort.setVisibility(View.VISIBLE);
                    }
                });
        alertDialog.show();
    }

    /**
     * The location listener.
     */
    LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {

            Logger.output(TAG, "listener called");

            myTimer.cancel();

            stopUsingGPS();
            DialogUtils.dismissProgressDialog();

            new FetchAllJobs().execute(mSortingOption + "",
                    location.getLatitude() + "", location.getLongitude()
                            + "");

        }
    };

    /**
     * Stop using gps.
     */
    public void stopUsingGPS() {

        if ((locationClient != null) && (locationClient.isConnected())) {
            // locationClient.removeLocationUpdates(locationListener);
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    locationClient, locationListener);
        }
    }

    private void setLocationServices() {
        locationClient = new GoogleApiClient.Builder(syncroTeamBaseActivity)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        mLocationRequest = LocationRequest.create();

        /*
         * Set the update interval
         */
        mLocationRequest
                .setInterval(SynchroteamUitls.UPDATE_INTERVAL_IN_MILLISECONDS);

        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
        mLocationRequest
                .setFastestInterval(SynchroteamUitls.FAST_INTERVAL_CEILING_IN_MILLISECONDS);
    }

//    public void hideFooterView() {
//        if (footerView != null) {
//            if (footerView.getVisibility() == View.VISIBLE) {
//                footerView.setVisibility(View.INVISIBLE);
//            }
//        }
//
//    }
//
//    public void showFooterView() {
//        if (footerView != null) {
//            if (footerView.getVisibility() == View.INVISIBLE) {
//                footerView.setVisibility(View.VISIBLE);
//            }
//        }
//    }

    /**
     * Compares two client name for sorting. If two client name is same, then comparison will be carried out with job numbers.
     *
     * @param lhsB
     * @param rhsB
     * @return result for comparison
     */
    private int getSortingResultforClient(CommonJobBean lhsB, CommonJobBean rhsB) {
        int result = lhsB.getNom_client_interv().compareToIgnoreCase(rhsB.getNom_client_interv());

        if (result == 0) {
            String mLhsNo = "0";
            String mRhsNo = "0";
            if (!TextUtils.isEmpty(lhsB.getRef_customer())) {
                mLhsNo = lhsB.getRef_customer();
            } else if (lhsB.getNo_interv() != 0) {
                mLhsNo = "" + lhsB.getNo_interv();
            }

            if (!TextUtils.isEmpty(rhsB.getRef_customer())) {
                mRhsNo = rhsB.getRef_customer();
            } else if (rhsB.getNo_interv() != 0) {
                mRhsNo = "" + rhsB.getNo_interv();
            }
            return mLhsNo.compareToIgnoreCase(mRhsNo);
        } else {
            return result;
        }
    }

    /**
     * Compares two site name for sorting. If two site name is same, then comparison will be carried out with job numbers.
     *
     * @param lhsB
     * @param rhsB
     * @return result for comparison
     */

    private int getSortingResultforSite(CommonJobBean lhsB, CommonJobBean rhsB) {
        int result = lhsB.getNom_site_interv().compareToIgnoreCase(rhsB.getNom_site_interv());

        if (result == 0) {
            String mLhsNo = "0";
            String mRhsNo = "0";
            if (!TextUtils.isEmpty(lhsB.getRef_customer())) {
                mLhsNo = lhsB.getRef_customer();
            } else if (lhsB.getNo_interv() != 0) {
                mLhsNo = "" + lhsB.getNo_interv();
            }

            if (!TextUtils.isEmpty(rhsB.getRef_customer())) {
                mRhsNo = rhsB.getRef_customer();
            } else if (rhsB.getNo_interv() != 0) {
                mRhsNo = "" + rhsB.getNo_interv();
            }
            return mLhsNo.compareToIgnoreCase(mRhsNo);
        } else {
            return result;
        }
    }

    /**
     * Compares two equipment name for sorting. If two equipment name is same, then comparison will be carried out with job numbers.
     *
     * @param lhsB
     * @param rhsB
     * @return result for comparison
     */
    private int getSortingResultforEquipment(CommonJobBean lhsB, CommonJobBean rhsB) {
        int result = lhsB.getNom_equipement().compareToIgnoreCase(rhsB.getNom_equipement());

        if (result == 0) {
            String mLhsNo = "0";
            String mRhsNo = "0";
            if (!TextUtils.isEmpty(lhsB.getRef_customer())) {
                mLhsNo = lhsB.getRef_customer();
            } else if (lhsB.getNo_interv() != 0) {
                mLhsNo = "" + lhsB.getNo_interv();
            }

            if (!TextUtils.isEmpty(rhsB.getRef_customer())) {
                mRhsNo = rhsB.getRef_customer();
            } else if (rhsB.getNo_interv() != 0) {
                mRhsNo = "" + rhsB.getNo_interv();
            }
            return mLhsNo.compareToIgnoreCase(mRhsNo);
        } else {
            return result;
        }
    }

    /**
     * Compares two priority. If two priority is same,
     * then comparison will be carried out with job numbers.
     *
     * @param lhsB
     * @param rhsB
     * @return result for comparison
     */
    private int getSortingResultForPriority(CommonJobBean lhsB, CommonJobBean rhsB) {
//        int result = ("" + lhsB.getPriorite()).compareToIgnoreCase("" + rhsB.getPriorite());
        int result = ("" + rhsB.getPriorite()).compareToIgnoreCase("" + lhsB.getPriorite());

        if (result == 0) {
            String mLhsNo = "0";
            String mRhsNo = "0";
            if (!TextUtils.isEmpty("" + lhsB.getPriorite())) {
                mLhsNo = "" + lhsB.getPriorite();
            } else if (lhsB.getNo_interv() != 0) {
                mLhsNo = "" + lhsB.getNo_interv();
            }

            if (!TextUtils.isEmpty("" + rhsB.getPriorite())) {
                mRhsNo = "" + rhsB.getPriorite();
            } else if (rhsB.getNo_interv() != 0) {
                mRhsNo = "" + rhsB.getNo_interv();
            }
            return mLhsNo.compareToIgnoreCase(mRhsNo);
//            return mRhsNo.compareToIgnoreCase(mLhsNo);
        } else {
            return result;
        }
    }

    /**
     * Compares two town name for sorting. If two town name is same, then comparison will be carried out with job numbers.
     *
     * @param lhsB
     * @param rhsB
     * @return result for comparison
     */
    private int getSortingResultforTown(CommonJobBean lhsB, CommonJobBean rhsB) {
        int result = lhsB.getAdr_interv_ville().compareToIgnoreCase(rhsB.getAdr_interv_ville());

        if (result == 0) {
            String mLhsNo = "0";
            String mRhsNo = "0";
            if (!TextUtils.isEmpty(lhsB.getRef_customer())) {
                mLhsNo = lhsB.getRef_customer();
            } else if (lhsB.getNo_interv() != 0) {
                mLhsNo = "" + lhsB.getNo_interv();
            }

            if (!TextUtils.isEmpty(rhsB.getRef_customer())) {
                mRhsNo = rhsB.getRef_customer();
            } else if (rhsB.getNo_interv() != 0) {
                mRhsNo = "" + rhsB.getNo_interv();
            }
            return mLhsNo.compareToIgnoreCase(mRhsNo);
        } else {
            return result;
        }
    }

    /**
     * Compares two location for sorting. If two locations are same, then comparison will be carried out with job numbers.
     *
     * @param lhsB
     * @param rhsB
     * @return result for comparison
     */
//    private int getSortingResultforNearby(CommonJobBean lhsB, CommonJobBean rhsB) {
//        int resultLat = lhsB.getLat().compareToIgnoreCase(rhsB.getLat());
//        int resultLon = lhsB.getLon().compareToIgnoreCase(rhsB.getLon());
//        if (resultLat == 0 && resultLon == 0) {
//            String mLhsNo = "0";
//            String mRhsNo = "0";
//            if (!TextUtils.isEmpty(lhsB.getRef_customer())) {
//                mLhsNo = lhsB.getRef_customer();
//            } else if (lhsB.getNo_interv() != 0) {
//                mLhsNo = "" + lhsB.getNo_interv();
//            }
//
//            if (!TextUtils.isEmpty(rhsB.getRef_customer())) {
//                mRhsNo = rhsB.getRef_customer();
//            } else if (rhsB.getNo_interv() != 0) {
//                mRhsNo = "" + rhsB.getNo_interv();
//            }
//            return mLhsNo.compareToIgnoreCase(mRhsNo);
//        }
//        return 1;
//    }
    private int getSortingResultforNearby(CommonJobBean lhsB, CommonJobBean rhsB) {
        if (lhsB.getLat() != null && rhsB.getLat() != null && lhsB.getLon() != null && rhsB.getLon() != null) {
            int resultLat = lhsB.getLat().compareToIgnoreCase(rhsB.getLat());
            int resultLon = lhsB.getLon().compareToIgnoreCase(rhsB.getLon());
            if (resultLat == 0 && resultLon == 0) {
                String mLhsNo = "0";
                String mRhsNo = "0";
//
//                if (lhsB.getRef_customer() != null && !TextUtils.isEmpty(lhsB.getRef_customer())) {
//                    mLhsNo = lhsB.getRef_customer();
//                } else if (lhsB.getNo_interv() != 0) {
//                    mLhsNo = "" + lhsB.getNo_interv();
//                }
//
//                if (rhsB.getRef_customer() != null && !TextUtils.isEmpty(rhsB.getRef_customer())) {
//                    mRhsNo = rhsB.getRef_customer();
//                }
//                Log.e(TAG,"EXCEPTION INTERVN NO====>"+rhsB.getNo_interv());
//                 if (rhsB.getNo_interv() != 0) {
//                    mRhsNo = "" + rhsB.getNo_interv();
//                }

                if (lhsB.getRef_customer() != null && !TextUtils.isEmpty(lhsB.getRef_customer())
                        && rhsB.getRef_customer() != null && !TextUtils.isEmpty(rhsB.getRef_customer())) {
                    mLhsNo = lhsB.getRef_customer();
                    mRhsNo = rhsB.getRef_customer();
                    return mLhsNo.compareToIgnoreCase(mRhsNo);
                } else if (lhsB.getNo_interv() != 0 && rhsB.getNo_interv() != 0) {
                    mLhsNo = "" + lhsB.getNo_interv();
                    mRhsNo = "" + rhsB.getNo_interv();
                    int returnValue = 0;
                    if (lhsB.getNo_interv() < rhsB.getNo_interv()) {
                        returnValue= 1;
                    } else {
                        returnValue= 0;
                    }

//                    try {
//                        Logger.log(TAG,"VALUE CHECK TEST =====>"+mLhsNo+""+mRhsNo);
//                        return Integer.valueOf(mLhsNo).compareTo(Integer.valueOf(mRhsNo));
//                    } catch (Exception e) {
//                        return 0;
//                    }


                    return returnValue;

                } else {
                    return 0;
                }

//                try {
//
//                } catch (Exception e) {
//                    return 0;
//                }
            } else {
                return 0;
            }
        } else {
            return 0;
        }
//        if(lhsB.getLat()!=null&&rhsB.getLat()!=null){
//        int resultLat = lhsB.getLat().compareToIgnoreCase(rhsB.getLat());
//        int resultLon = lhsB.getLon().compareToIgnoreCase(rhsB.getLon());
//        if (resultLat == 0 && resultLon == 0) {
//            String mLhsNo = "0";
//            String mRhsNo = "0";
//            if (!TextUtils.isEmpty(lhsB.getRef_customer())) {
//                mLhsNo = lhsB.getRef_customer();
//            } else if (lhsB.getNo_interv() != 0) {
//                mLhsNo = "" + lhsB.getNo_interv();
//            }
//            if (!TextUtils.isEmpty(rhsB.getRef_customer())) {
//                mRhsNo = rhsB.getRef_customer();
//            } else if (rhsB.getNo_interv() != 0) {
//                mRhsNo = "" + rhsB.getNo_interv();
//            }
//            return mLhsNo.compareToIgnoreCase(mRhsNo);
//        }else{
//            return 0;
//        }
//        return 1;
    }

    @Override
    public void onEmptyList(ArrayList<HashMap<String, String>> list) {
        setEmptyViewForRv(list);
    }
    //--------------------------------------LOCAL...METHODS...ENDS--------------------------------------------

    //--------------------------------------INNER...CLASS...STARTS--------------------------------------------

    /***
     * Async task to fetch all the jobs.
     *
     * @author Trident
     */

    private class FetchAllJobs extends AsyncTaskCoroutine<String, Void> {

        private int option;
        private String mLatitude;
        private String mLongitude;

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            rvAllJobs.setVisibility(View.GONE);
            progressBarDeadlineJob.setVisibility(View.VISIBLE);
        }

        @Override
        public Void doInBackground(String... params) {

            allReportCurrUPcomingLate.clear();
            AllCollection.clear();

//            CurrentJobDataBean currentJobDataBean = dataAceesObject
//                    .getTodaysJobs(dateSelected, user.getId(),
//                            KEYS.CurrentJobsSorting.SORT_BY_DATE);

            //deve 51.0.3
            CurrentJobDataBeanNew currentJobDataBean = dataAceesObject.getCurrentJobsForSortingNew(dateSelected, user.getId());//  .getCurrentJobs(dateSelected, user.getId());
            //current job dev v51.0.3
            ArrayList<CommonJobBean> currentJobBeanVector = new ArrayList<>(currentJobDataBean.getCommonJobDataBean());
            int jobCount = currentJobDataBean.getCurrrntJobCount();
            //upcoming job dev v51.0.3
            ArrayList<CommonJobBean> upcomingJob = new ArrayList<>(dataAceesObject.getAllInterventionNext());
            ArrayList<CommonJobBean> lateJob = new ArrayList<>(dataAceesObject.getLateJobs(user.getId()));

            Calendar calendarReport = Calendar.getInstance();
            calendarReport.setTime(new Date());
            calendarReport.add(Calendar.DATE, -30);
            String lastDate = dateFormat.format(calendarReport.getTime());
            AllCollection.clear();
            ArrayList<CommonJobBean> reportJob = new ArrayList<>(dataAceesObject.getAllInterventionCompletedNewNew(lastDate));
            allReportCurrUPcomingLate.add(reportJob);
            allReportCurrUPcomingLate.add(currentJobBeanVector);
            allReportCurrUPcomingLate.add(upcomingJob);
            allReportCurrUPcomingLate.add(lateJob);

            for (int i = 0; i < allReportCurrUPcomingLate.size(); i++) {
                for (int j = 0; j < allReportCurrUPcomingLate.get(i).size(); j++) {
                    AllCollection.add(allReportCurrUPcomingLate.get(i).get(j));
                }
            }
            Collections.sort(AllCollection, new Comparator<CommonJobBean>() {

                @Override
                public int compare(CommonJobBean o1, CommonJobBean o2) {
                    int result = -1;

                    try {
                        result = dateFormat.parse(o1.getHeaderDate()).compareTo(dateFormat.parse(o2.getHeaderDate()));
                    } catch (ParseException ex) {
                        ex.printStackTrace();
                    }
                    return result;
                }


            });
            option = Integer.parseInt(params[0]);
            mLatitude = params[1];
            mLongitude = params[2];

            if (option == KEYS.AllJobSortingOptions.SORT_BY_DATE) {
                createAdapterAndInflateDataInListView();
            } else {
                // getListForSorting(mSortingOption, mLatitude, mLongitude);
                getListForSortingDev503(mSortingOption, mLatitude, mLongitude);
            }
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressBarDeadlineJob.setVisibility(View.GONE);
            rvAllJobs.setVisibility(View.VISIBLE);

            if (mSortingOption == KEYS.AllJobSortingOptions.SORT_BY_DATE) {
                setAllJobsListAdapter();
            } else {
                setSortingListAdapter();
            }
            rvAllJobs.addOnScrollListener(mScrollListener);

            EventBus.getDefault().post(new EnableSynchronizationAddJobEvent());
        }

    }

    //--------------------------------------INNER...CLASS...ENDS----------------------------------------------

    //------------------------------------------LISTENER---STARTS---------------------------------------------


    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            switch (v.getId()) {
                case R.id.rel_sort:
                    dialogSorting.show(fragmentManager, "sorting");
                    break;
                case R.id.ic_barcode:
                    Intent intent = new Intent(syncroTeamBaseActivity,
                            CodeScannerActivity.class);
                    allJobsFragment.startActivityForResult(intent,
                            RequestCode.REQUEST_CODE_TEXT_BARCODE);
                    break;
            }
        }
    };

    /**
     * watcher.
     */
    TextWatcher mWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            isUserSearching = s.toString().length() != 0;
            searchIndex = 1;
            if (isDateAdapter) {
                if (!isUserSearching) {
                    allJobsListAdapter.setIndexPosition(index);
                } else {
                    allJobsListAdapter.setIndexPosition(searchIndex);
                }

                mAllJobFilter.filter(s.toString());
            } else {
                if (!isUserSearching) {
                    mAllJobsSortingAdapter.setIndexPosition(index);
                } else {
                    mAllJobsSortingAdapter.setIndexPosition(searchIndex);
                }

                mJobSortingFilter.filter(s.toString());
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * Focus change listener.
     */
    private EditText.OnFocusChangeListener mFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                edSearchJobs.setHint("");
            } else {
                edSearchJobs.setHint(syncroTeamBaseActivity.getString(R.string.txt_search_hint));
            }
        }
    };


    /**
     * Scroll listener for recycler view.
     * To dismissing keyboard while scrolling the recycler view up or down.
     * To integrate Footer view while loading items.
     */
    private RecyclerView.OnScrollListener mScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);

            //dismissing soft input keyboard while scrolling
            View currentFocus = syncroTeamBaseActivity.getCurrentFocus();
            if (currentFocus != null) {
                InputMethodManager imm = (InputMethodManager) syncroTeamBaseActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(recyclerView.getWindowToken(), 0);
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            totalItemCount = linearLayoutManager.getItemCount();
            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                isLoading = true;
                loadMore();
            }
        }
    };

    /**
     * Loading more data after swipe to the end
     */
    private void loadMore() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isUserSearching) {
                    index++;
                } else {
                    searchIndex++;
                }
                if (isDateAdapter) {
                    if (!isUserSearching) {
                        allJobsListAdapter.setIndexPosition(index);
                    } else {
                        allJobsListAdapter.setIndexPosition(searchIndex);
                    }
                    allJobsListAdapter.notifyDataSetChanged();
                } else {
                    if (!isUserSearching) {
                        mAllJobsSortingAdapter.setIndexPosition(index);
                    } else {
                        mAllJobsSortingAdapter.setIndexPosition(searchIndex);
                    }
                    mAllJobsSortingAdapter.notifyDataSetChanged();
                }
                isLoading = false;
            }
        }, 900);

    }

    //------------------------------------------LISTENER---ENDS---------------------------------------------
}
