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
import com.synchroteam.beans.EnableSynchronizationAddJobEvent;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.AllJobsSortingDialog;
import com.synchroteam.fragment.AllJobPoolFragment;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;
import java.util.TreeMap;

import de.greenrobot.event.EventBus;


/**
 * Helper class to show the list of all jobs in the fragment.<p></p>
 * Created by Trident on 5/18/2016.
 */
public class AllJobPoolFragmentHelper implements HelperInterface, RvEmptyListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    //------------------------------------INSTANCE...VARIABLES...DECLARATION...STARTS------------------------

    private RecyclerView rvAllJobPool;
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
    private TreeMap<String, ArrayList<HashMap<String, String>>> allJobListbyDateNew;

    private AllJobsDateAdapterRv allJobPoolListAdapter;
    private AllJobsSortingAdapterRv mAllJobPoolSortingAdapter;

    private ArrayList<HashMap<String, String>> allJobArrayList;
    private ArrayList<HashMap<String, String>> allJobArrayListNew;
    private ArrayList<HashMap<String, String>> allJobList;
    private ArrayList<HashMap<String, String>> allJobListNew;
    private ArrayList<HashMap<String, String>> allJobListSearch;

    private Filter mAllJobFilter;
    private Filter mJobSortingFilter;

    private BaseFragment baseFragment;
    private User user;
    private ProgressBar progressBarDeadlineJob;
    private DateFormat dateFormatCurrentJobList;
    private Format timeFormatCurrentJobList;

    private String mSortingName;
    private int mSortingOption = 8;
    private boolean isDateAdapter;
    private boolean isSortAdapter;

    private Timer myTimer;
    protected LocationManager locationManager;
    private LocationRequest mLocationRequest;
    private GoogleApiClient locationClient;
    private boolean isLocationClientConnected = false;
    private AllJobPoolFragment allJobPoolFragment;

    private int index;
    private int searchIndex;
    private boolean isUserSearching = false;
    private boolean isDbUpdated = false;
    private String searchText;
    private String searchTextSize;

    private boolean isLoading;
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;
    /**
     * The current date format.
     */
    private String currentDateFormat = "yyyy-MM-dd HH:mm:ss.SSS";

    private int jobPoolOffset = 1;
    private int jobPoolListCount;
    private int mCount = 20;
    private int optionNew;

    // footerView
    LinearLayout footerView;
    Boolean onScroll = false;

    private static final String TAG = AllJobPoolFragmentHelper.class.getSimpleName();
    private ArrayList<HashMap<String, String>> jobList;

    private ArrayList<HashMap<String, String>> originalJobList;

    //------------------------------------INSTANCE...VARIABLES...DECLARATION...ENDS--------------------------

    /**
     * Instantiates a new all job fragment helper object.
     *
     * @param syncroTeamBaseActivity : base activity
     * @param baseFragment           : base fragment
     */
    public AllJobPoolFragmentHelper(SyncroTeamBaseActivity syncroTeamBaseActivity, AllJobPoolFragment allJobPoolFragment, BaseFragment baseFragment) {
        this.syncroTeamBaseActivity = syncroTeamBaseActivity;
        this.allJobPoolFragment = allJobPoolFragment;
        dataAccessObject = DaoManager.getInstance();
        this.baseFragment = baseFragment;
        user = dataAccessObject.getUser();
    }

    //-------------------------------------OVERRIDDEN...METHODS...STARTS--------------------------------------

    @Override
    public View inflateLayout(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.layout_all_job_pool,
                container, false);

        SharedPref.setSortedJobNumber("", syncroTeamBaseActivity);
        SharedPref.setSortingOption(8, syncroTeamBaseActivity);

        initiateView(view);
        progressBarDeadlineJob = (ProgressBar) view
                .findViewById(R.id.progressBarDeadlineJob);

        dateFormatCurrentJobList = DateFormat.getDateInstance(DateFormat.LONG);
        timeFormatCurrentJobList = android.text.format.DateFormat
                .getTimeFormat(syncroTeamBaseActivity);

        if (mSortingOption == KEYS.AllJobSortingOptions.SORT_BY_NEARBY_JOB) {
            String mSortedJobNumber = SharedPref.getSortedJobNumber(syncroTeamBaseActivity);
            HashMap<String, String> hmLatLong = dataAccessObject.getLatLongAllJobs(mSortedJobNumber);
            if (hmLatLong.size() > 0) {
                new FetchAllJobPool().execute("" + mSortingOption, hmLatLong.get(KEYS.CurrentJobs.LAT), hmLatLong.get(KEYS.CurrentJobs.LON));
            } else {
                Toast.makeText(syncroTeamBaseActivity.getApplicationContext(), R.string.txt_jobno_mismatch, Toast.LENGTH_LONG).show();
            }
        } else if (mSortingOption != KEYS.AllJobSortingOptions.SORT_BY_NEARBY) {
            new FetchAllJobPool().execute("" + mSortingOption, "", "");
        }

        return view;
    }


    @Override
    public void initiateView(View v) {
        rvAllJobPool = (RecyclerView) v.findViewById(R.id.rv_all_job_pool);
        linearLayoutManager = new LinearLayoutManager(syncroTeamBaseActivity);
        txtEmptyList = (TextView) v.findViewById(R.id.empty_text);
        linSearchSort = (LinearLayout) v.findViewById(R.id.lin_search_sort);
        RelativeLayout relSort = (RelativeLayout) v.findViewById(R.id.rel_sort);
        txtSortBy = (com.synchroteam.TypefaceLibrary.TextView) v.findViewById(R.id.txt_sort_by);
        txtIcSort = (TextView) v.findViewById(R.id.ic_sort);
        txtIcSearch = (TextView) v.findViewById(R.id.ic_search);
        txtIcBarcode = (TextView) v.findViewById(R.id.ic_barcode);
        edSearchJobs = (EditText) v.findViewById(R.id.ed_search_all_jobs);

        // footerView
        footerView = v.findViewById(R.id.footer_layout);

        mSortingOption = SharedPref.getSortingOption(syncroTeamBaseActivity);

        fragmentManager = syncroTeamBaseActivity.getSupportFragmentManager();
        dialogSorting = AllJobsSortingDialog.getInstance(KEYS.AllJobSortingOptions.ALL_JOBS_SORTING,
                null, false, true, baseFragment);
        dialogSorting.setTargetFragment(baseFragment, 300);

        index = 1;
        searchIndex = 1;
        jobList = new ArrayList<>();
        originalJobList = new ArrayList<>();

        String[] arrSortingOptions = {syncroTeamBaseActivity.getString(R.string.txt_date_sorting),
                syncroTeamBaseActivity.getString(R.string.txt_customer_sorting),
                syncroTeamBaseActivity.getString(R.string.txt_site_sorting),
                syncroTeamBaseActivity.getString(R.string.txt_equipment_sorting),
                syncroTeamBaseActivity.getString(R.string.txt_nearby_sorting),
                syncroTeamBaseActivity.getString(R.string.txt_nearby_job_sorting),
                syncroTeamBaseActivity.getString(R.string.txt_town_sorting),
                syncroTeamBaseActivity.getString(R.string.txt_priority_sorting),
                syncroTeamBaseActivity.getString(R.string.txt_no_sorting)};
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
                new FetchAllJobPool().execute("" + mSortingOption, hmLatLong.get(KEYS.CurrentJobs.LAT), hmLatLong.get(KEYS.CurrentJobs.LON));
            } else {
                Toast.makeText(syncroTeamBaseActivity.getApplicationContext(), R.string.txt_jobno_mismatch, Toast.LENGTH_LONG).show();
            }
        } else {
            new FetchAllJobPool().execute("" + mSortingOption, "", "");
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
     * Gets the date with required pattern for calander.
     *
     * @param date                the date
     * @param datePattern         the date pattern
     * @param requiredDatePattern the required date pattern
     * @return the date with requiredpattern for calander
     * @throws ParseException the parse exception
     */
    private synchronized String getDateWithRequiredPattern(String date,
                                                           String datePattern, String requiredDatePattern)
            throws ParseException {

        String requiredDate = null;

        SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
        SimpleDateFormat formatterOut = new SimpleDateFormat(requiredDatePattern);

        try {

            Date outputDate = formatter.parse(date);
            requiredDate = formatterOut.format(outputDate);
            System.out.println(date);
            System.out.println();

        } catch (ParseException e) {
            e.printStackTrace();
            requiredDate = date;
        }

        return requiredDate;
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
        if (allJobListbyDateNew != null) {
            allJobListbyDateNew.clear();
        } else {
            allJobListbyDateNew = new TreeMap<>(
                    new Comparator<String>() {

                        @Override
                        public int compare(String lhs, String rhs) {
                            return lhs.compareTo(rhs);
                        }
                    });
        }

        if (allJobArrayListNew != null) {
            allJobArrayListNew.clear();
        } else {
            allJobArrayListNew = new ArrayList<>();
        }

        HashMap<String, String> allJobHashMap;

        ArrayList<CommonListBean> allJobBeanVector;

        jobPoolListCount = Integer.parseInt(dataAccessObject.getToJobPoolCount());

//        CurrentJobDataBean allJobsDataBean = dataAccessObject.getAllInterventionByDateNew();
        CurrentJobDataBean allJobsDataBean = dataAccessObject.getAllInterventionByDateNewWithOffset(jobPoolOffset);
        allJobBeanVector = allJobsDataBean.getCommonJobDataBean();

        for (int i = 0; i < allJobBeanVector.size(); i++) {
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
            allJobHashMap.put(KEYS.CurrentJobs.ADR_GLOBAL,
                    allJobsBean.getAdr_interv_globale());

            allJobHashMap.put(KEYS.CurrentJobs.DESC, allJobsBean.getDesc());
            allJobHashMap.put(KEYS.CurrentJobs.ADR_COMPLEMENT,
                    allJobsBean.getAdr_interv_complement());
            allJobHashMap.put(KEYS.CurrentJobs.MOBILE_CONTACT,
                    String.valueOf(allJobsBean.getMobileContact()));


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

            //adding custom fields...
            allJobHashMap.put(KEYS.CurrentJobs.CF_INTERVENTION, dataAccessObject.getAllCFInterv(allJobsBean.getId()));
            allJobHashMap.put(KEYS.CurrentJobs.CF_SITE, dataAccessObject.getAllCFSite(allJobsBean.getIdSite()));
            allJobHashMap.put(KEYS.CurrentJobs.CF_CLIENT, dataAccessObject.getAllCFClient(allJobsBean.getIdClient()));
            allJobHashMap.put(KEYS.CurrentJobs.CF_EQUIPMENT, dataAccessObject.getAllCFEquip(allJobsBean.getIdEquipement()));

            allJobHashMap.put(KEYS.CurrentJobs.DATEMEETING,
                    String.valueOf(allJobsBean.getDt_meeting()));

            Date date = getDateFromDbFormat(allJobsBean
                    .getDt_deb_prev());

            boolean isJobPool = allJobsBean.isJobPool();

            if (allJobsBean.isJobPool()) {
                allJobHashMap.put(KEYS.CurrentJobs.IS_JOB_POOL,
                        "True");
            } else {
                allJobHashMap.put(KEYS.CurrentJobs.IS_JOB_POOL,
                        "True");
            }
            if (isJobPool) {
                String isJobDatePref = allJobsBean.getDatePref();
                int idJobWindow = allJobsBean.getIdJobWindow();
                allJobHashMap.put(KEYS.CurrentJobs.DATE_PREF, isJobDatePref);
                allJobHashMap.put(KEYS.CurrentJobs.ID_JOB_WINDOW, String.valueOf(idJobWindow));
                if (idJobWindow > 0) {
                    String startTime = dataAccessObject.startTimeJobWindow(idJobWindow);
                    String endTime = dataAccessObject.endTimeJobWindow(idJobWindow);
                    allJobHashMap.put(KEYS.CurrentJobs.ID_JOB_WINDOW_START_TIME, startTime);
                    allJobHashMap.put(KEYS.CurrentJobs.ID_JOB_WINDOW_END_TIME, endTime);
                }
            }

            String DTMEETING = dataAccessObject.getDTMEETINGList(allJobsBean.getId());
            Logger.log(TAG, "DT_MEETING VALUE IS ===>" + DTMEETING);

            String dateToshow = null;

            if (jobStatus <= KEYS.CurrentJobs.JOB_NOT_STARTED2) {

                allJobHashMap.put(KEYS.CurrentJobs.DATE_TO_SHOW,
                        dateFormatCurrentJobList.format(date) + "");

                allJobHashMap.put(KEYS.CurrentJobs.TIME_TO_SHOW,
                        timeFormatCurrentJobList.format(date));
            }

            if (allJobListbyDateNew.containsKey(allJobHashMap
                    .get(KEYS.CurrentJobs.ALL_JOB_HEADER))) {
                allJobListbyDateNew.get(
                                allJobHashMap
                                        .get(KEYS.CurrentJobs.ALL_JOB_HEADER))
                        .add(allJobHashMap);

                allJobHashMap.put(KEYS.CurrentJobs.HAS_HEADER, "false");
                allJobArrayListNew.add(allJobHashMap);
            } else {
                allJobListbyDateNew.put(allJobHashMap
                                .get(KEYS.CurrentJobs.ALL_JOB_HEADER),
                        new ArrayList<HashMap<String, String>>());
                allJobListbyDateNew.get(
                                allJobHashMap
                                        .get(KEYS.CurrentJobs.ALL_JOB_HEADER))
                        .add(allJobHashMap);

//                allJobHashMap.put(KEYS.CurrentJobs.HAS_HEADER, "true");

                allJobHashMap.put(KEYS.CurrentJobs.HAS_HEADER, "false");
                allJobArrayListNew.add(allJobHashMap);

            }
        }

        allJobArrayList.addAll(allJobArrayListNew);
        allJobListbyDate.putAll(allJobListbyDateNew);

        Log.e("ARRAYLIST", " THE ARRAYLIST DATE SORTING SIZE IS >>>>>" + allJobArrayList.size());
    }


    /**
     * Sort list by selected option and set adapter
     *
     * @param options : selected option
     */
    public void getListForSorting(final int options, String latitude, String longitude) {
        if (allJobListNew != null) {
            allJobListNew.clear();
        } else {
            allJobListNew = new ArrayList<>();
        }

        HashMap<String, String> allJobHashMap;

        ArrayList<CommonListBean> allJobBeanVector = null;

        CurrentJobDataBean allJobsDataBean;

        if (options == KEYS.AllJobSortingOptions.SORT_BY_NEARBY || options == KEYS.AllJobSortingOptions.SORT_BY_NEARBY_JOB) {
//            if (!TextUtils.isEmpty(latitude) && !TextUtils.isEmpty(longitude)) {
            allJobsDataBean = dataAccessObject.getAllInterventionNearByNewWithOffset(latitude, longitude, jobPoolOffset);
            allJobBeanVector = allJobsDataBean.getCommonJobDataBean();

        } else if (options == KEYS.AllJobSortingOptions.SORT_BY_NONE) {
            allJobsDataBean = dataAccessObject.getAllInterventionJobPoolWithOffset(jobPoolOffset);
            allJobBeanVector = allJobsDataBean.getCommonJobDataBean();
        } else {
            allJobsDataBean = dataAccessObject.getAllInterventionNewWithOffset(jobPoolOffset);
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

        for (int i = 0; i < allJobBeanVector.size(); i++) {

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
            allJobHashMap.put(KEYS.CurrentJobs.ADR_GLOBAL,
                    allJobsBean.getAdr_interv_globale());

            allJobHashMap.put(KEYS.CurrentJobs.DESC, allJobsBean.getDesc());
            allJobHashMap.put(KEYS.CurrentJobs.ADR_COMPLEMENT,
                    allJobsBean.getAdr_interv_complement());
            allJobHashMap.put(KEYS.CurrentJobs.MOBILE_CONTACT,
                    String.valueOf(allJobsBean.getMobileContact()));

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

            //adding custom fields...
            allJobHashMap.put(KEYS.CurrentJobs.CF_INTERVENTION, dataAccessObject.getAllCFInterv(allJobsBean.getId()));
            allJobHashMap.put(KEYS.CurrentJobs.CF_SITE, dataAccessObject.getAllCFSite(allJobsBean.getIdSite()));
            allJobHashMap.put(KEYS.CurrentJobs.CF_CLIENT, dataAccessObject.getAllCFClient(allJobsBean.getIdClient()));
            allJobHashMap.put(KEYS.CurrentJobs.CF_EQUIPMENT, dataAccessObject.getAllCFEquip(allJobsBean.getIdEquipement()));

            allJobHashMap.put(KEYS.CurrentJobs.DATEMEETING,
                    String.valueOf(allJobsBean.getDt_meeting()));

            Date date = getDateFromDbFormat(allJobsBean
                    .getDt_deb_prev());
            boolean isJobPool = allJobsBean.isJobPool();

            if (allJobsBean.isJobPool()) {
                allJobHashMap.put(KEYS.CurrentJobs.IS_JOB_POOL,
                        "True");
            } else {
                allJobHashMap.put(KEYS.CurrentJobs.IS_JOB_POOL,
                        "True");
            }
            if (isJobPool) {
                String isJobDatePref = allJobsBean.getDatePref();
                int idJobWindow = allJobsBean.getIdJobWindow();
                allJobHashMap.put(KEYS.CurrentJobs.DATE_PREF, isJobDatePref);
                allJobHashMap.put(KEYS.CurrentJobs.ID_JOB_WINDOW, String.valueOf(idJobWindow));
                if (idJobWindow > 0) {
                    String startTime = dataAccessObject.startTimeJobWindow(idJobWindow);
                    String endTime = dataAccessObject.endTimeJobWindow(idJobWindow);
                    allJobHashMap.put(KEYS.CurrentJobs.ID_JOB_WINDOW_START_TIME, startTime);
                    allJobHashMap.put(KEYS.CurrentJobs.ID_JOB_WINDOW_END_TIME, endTime);
                }
            }

            String dateToshow = null;

            if (jobStatus <= KEYS.CurrentJobs.JOB_NOT_STARTED2) {

                allJobHashMap.put(KEYS.CurrentJobs.DATE_TO_SHOW,
                        dateFormatCurrentJobList.format(date) + "");

                allJobHashMap.put(KEYS.CurrentJobs.TIME_TO_SHOW,
                        timeFormatCurrentJobList.format(date));
            }

            allJobListNew.add(allJobHashMap);

        }

        allJobList.addAll(allJobListNew);

        Log.e("ARRAYLIST", " THE ARRAYLIST SIZE IS >>>>>" + allJobList.size());

    }

    /**
     * Set the list adapter to deadlineExccedeListView.
     */
    private void setAllJobsListAdapter() {
        if (allJobPoolListAdapter == null || !isDateAdapter) {
            allJobPoolListAdapter = new AllJobsDateAdapterRv(syncroTeamBaseActivity, this,
                    allJobArrayList, true);
            index = 1;
            searchIndex = 1;

            allJobPoolListAdapter.setIndexPosition(index);

            rvAllJobPool.setLayoutManager(linearLayoutManager);
            rvAllJobPool.setAdapter(allJobPoolListAdapter);

        } else {

                if (isDbUpdated) {
                    mAllJobPoolSortingAdapter.setDuplicateList();
                    isDbUpdated = false;
                }


            allJobPoolListAdapter.notifyDataSetChanged();
        }

            setEmptyViewForRv(allJobArrayList);

        if (!isUserSearching){
            resetSearchField();
        }


        isDateAdapter = true;
        isSortAdapter = false;

        //check if header is already shown, if shown then shw the header for this adapter also
        if (mAllJobPoolSortingAdapter != null) {
            boolean isHeaderShown = mAllJobPoolSortingAdapter.isHeaderShown();
            if (isHeaderShown) {
                allJobPoolListAdapter.setHeaderShown(true);
                allJobPoolListAdapter.setIncrementValue(1);
                allJobPoolListAdapter.notifyDataSetChanged();
            } else {
                allJobPoolListAdapter.setHeaderShown(false);
                allJobPoolListAdapter.setIncrementValue(0);
                allJobPoolListAdapter.notifyDataSetChanged();
            }
//            allJobPoolListAdapter.setHeaderShown(false);
//            allJobPoolListAdapter.setIncrementValue(0);
//            allJobPoolListAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Sets empty view for recycler view.
     *
     * @param arrayList
     */
    private void setEmptyViewForRv(ArrayList<HashMap<String, String>> arrayList) {
        if (arrayList.size() > 0) {
            rvAllJobPool.setVisibility(View.VISIBLE);
            txtEmptyList.setVisibility(View.GONE);
        } else {
            rvAllJobPool.setVisibility(View.GONE);
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
            if (mDate != null && mDate.trim().length() > 0)
                date = sdf.parse(mDate);
            else
                return new Date();
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
                new FetchAllJobPool().execute("" + mSortingOption, hmLatLong.get(KEYS.CurrentJobs.LAT), hmLatLong.get(KEYS.CurrentJobs.LON));
            } else {
                Toast.makeText(syncroTeamBaseActivity.getApplicationContext(), R.string.txt_jobno_mismatch, Toast.LENGTH_LONG).show();
            }
        } else {
            Logger.output(TAG, "others");
            new FetchAllJobPool().execute("" + mSortingOption, "", "");
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
            new FetchAllJobPool().execute("" + mSortingOption, hmLatLong.get(KEYS.CurrentJobs.LAT), hmLatLong.get(KEYS.CurrentJobs.LON));
        } else {
            txtSortBy.setText(optionName);
            SharedPref.setSortingOption(options, syncroTeamBaseActivity);
            new FetchAllJobPool().execute("" + mSortingOption, "", "");
        }
    }

    /**
     * Set the list adapter to deadlineExccedeListView.
     */
    private void setSortingListAdapter() {
        Log.e("ALLJOB","ALLJOB LIST SIZE>>>>>>"+allJobList.size());

        /**
//         * Found the root cause of the issue #1295693795 and fixed it.
//         * TODO - Have to do optimization -[Set Adapter once and update the adapter at every other instance]
//         */
//        mAllJobPoolSortingAdapter = new AllJobsSortingAdapterRv(syncroTeamBaseActivity, this,
//                allJobList, true);
//        index = 1;
//        searchIndex = 1;
//
//        mAllJobPoolSortingAdapter.setIndexPosition(index);
//        rvAllJobPool.setLayoutManager(linearLayoutManager);
//        rvAllJobPool.setAdapter(mAllJobPoolSortingAdapter);

        if (mAllJobPoolSortingAdapter == null || !isSortAdapter) {
            mAllJobPoolSortingAdapter = new AllJobsSortingAdapterRv(syncroTeamBaseActivity, this,
                    allJobList, true);
            index = 1;
            searchIndex = 1;

            mAllJobPoolSortingAdapter.setIndexPosition(index);
            rvAllJobPool.setLayoutManager(linearLayoutManager);
            rvAllJobPool.setAdapter(mAllJobPoolSortingAdapter);
            Log.e("NAMESq", " IF BLOCK >>>>>");

        } else {
            if (isDbUpdated) {
                Log.e("NAMESq", " ELSE BLOCK IF>>>>>");
                mAllJobPoolSortingAdapter.setDuplicateList();
                isDbUpdated = false;
            }else{
                mAllJobPoolSortingAdapter.updateList(allJobList);
                Log.e("NAMESq", " ELSE BLOCK ELSE>>>>>");
            }

            mAllJobPoolSortingAdapter.notifyDataSetChanged();
            Log.e("NAMESq", " ELSE BLOCK >>>>>");
        }

        setEmptyViewForRv(allJobList);

        if (!isUserSearching){
            resetSearchField();
        }


        isSortAdapter = true;
        isDateAdapter = false;

        //check if header is already shown, if shown then shw the header for this adapter also
        if (allJobPoolListAdapter != null) {
            boolean isHeaderShown = allJobPoolListAdapter.isHeaderShown();
            if (isHeaderShown) {
                mAllJobPoolSortingAdapter.setHeaderShown(true);
                mAllJobPoolSortingAdapter.setIncrementValue(1);
                mAllJobPoolSortingAdapter.notifyDataSetChanged();
            } else {
                mAllJobPoolSortingAdapter.setHeaderShown(false);
                mAllJobPoolSortingAdapter.setIncrementValue(0);
                mAllJobPoolSortingAdapter.notifyDataSetChanged();
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
                        allJobPoolFragment.callingPermissionLocation();
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
                        allJobPoolFragment.startActivityForResult(intent, RequestCode.REQUEST_CODE_GPS_SETTINGS);
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

            new FetchAllJobPool().execute(mSortingOption + "",
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
        int result = rhsB.getPriorite() - lhsB.getPriorite();

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
//        } else {
//            return 0;
//        }
//    }
    private int getSortingResultforNearby(CommonJobBean lhsB, CommonJobBean rhsB) {

        if (lhsB.getLat() != null && rhsB.getLat() != null && lhsB.getLon() != null && rhsB.getLon() != null) {
            int resultLat = lhsB.getLat().compareToIgnoreCase(rhsB.getLat());
            int resultLon = lhsB.getLon().compareToIgnoreCase(rhsB.getLon());
            if (resultLat == 0 && resultLon == 0) {
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
                return 0;
            }
        } else {
            return 0;
        }

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

    private class FetchAllJobPool extends AsyncTaskCoroutine<String, Void> {

        private int option;
        private String mLatitude;
        private String mLongitude;

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            rvAllJobPool.setVisibility(View.GONE);
            progressBarDeadlineJob.setVisibility(View.VISIBLE);
            jobPoolOffset = 1;
            Log.e("CHECK","EXCUTING FOR 1ST TIME");
        }

        @Override
        public Void doInBackground(String... params) {
            option = Integer.parseInt(params[0]);
            mLatitude = params[1];
            mLongitude = params[2];


            if (option == KEYS.AllJobSortingOptions.SORT_BY_DATE) {

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

                createAdapterAndInflateDataInListView();
            } else {

                if (allJobList != null) {
                    allJobList.clear();
                } else {
                    allJobList = new ArrayList<>();
                }

                getListForSorting(mSortingOption, mLatitude, mLongitude);
            }
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressBarDeadlineJob.setVisibility(View.GONE);
            rvAllJobPool.setVisibility(View.VISIBLE);
            isUserSearching = false;

            if (mSortingOption == KEYS.AllJobSortingOptions.SORT_BY_DATE) {
                setAllJobsListAdapter();
                edSearchJobs.addTextChangedListener(mWatcher);
                rvAllJobPool.addOnScrollListener(mScrollListener);
            } else {
                setSortingListAdapter();
                edSearchJobs.addTextChangedListener(mWatcher);
                rvAllJobPool.addOnScrollListener(mScrollListener);
            }

            optionNew = option;
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
                    dialogSorting = AllJobsSortingDialog.getInstance(KEYS.AllJobSortingOptions.ALL_JOBS_SORTING,
                            null, false, true, baseFragment);
                    dialogSorting.setTargetFragment(baseFragment, 300);
                    dialogSorting.show(fragmentManager, "sorting");
                    break;
                case R.id.ic_barcode:
                    Intent intent = new Intent(syncroTeamBaseActivity,
                            CodeScannerActivity.class);
                    allJobPoolFragment.startActivityForResult(intent,
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

            isUserSearching = s.toString().trim().length() != 0;
            searchText = s.toString().trim();

            if (searchText.length() > 0) {
                allJobList = new ArrayList<>();
                jobPoolOffset = 1;
                new FetchAllJobPoolInSearch(searchText,true).execute();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {



//            if (!TextUtils.isEmpty(s.toString())) {
//
//                isUserSearching = true;
//                jobPoolOffset = 1;
//                jobList.clear();
//                searchText = s.toString();
//
//                new Handler().postDelayed(new Runnable() {
//                    @SuppressLint("NotifyDataSetChanged")
//                    @Override
//                    public void run() {
//                        try {
//                            jobList.clear();
//                            int count =dataAccessObject.getJobPoolSearchWithCount(searchText);
//                            //todo replicate this
//                            jobList.addAll(getTheResultList(dataAccessObject.getJobPoolSearchWithOffset(jobPoolOffset, searchText)));
//                            mAllJobPoolSortingAdapter.notifyDataSetChanged();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, 10);
//
//            } else {
//                isUserSearching = false;
//                searchText = "";
//                jobList.clear();
//                jobList.addAll(originalJobList);
//            }
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

            Log.e("CHECK","THE RESULT IS"+(!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)));
            Log.e("CHECK","THE RESULT IS"+( totalItemCount +" "+ lastVisibleItem +" "+ visibleThreshold));
            if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                isLoading = true;

                searchTextSize = edSearchJobs.getText().toString().trim();
                if(searchTextSize.length()>0){
                    new FetchAllJobPoolInSearchOnScroll(searchTextSize).execute();
                }else {
                    new FetchAllJobPoolOnScroll().execute();
                }
            }
        }
    };
    private Collection<? extends HashMap<String, String>> getTheResultList(ArrayList<CommonJobBean> list) {


        HashMap<String, String> allJobHashMap;

        ArrayList<CommonListBean> allJobBeanVector = null;
        CurrentJobDataBean allJobsDataBean;

        for (int i = 0; i < allJobBeanVector.size(); i++) {

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
            allJobHashMap.put(KEYS.CurrentJobs.ADR_GLOBAL,
                    allJobsBean.getAdr_interv_globale());

            allJobHashMap.put(KEYS.CurrentJobs.DESC, allJobsBean.getDesc());
            allJobHashMap.put(KEYS.CurrentJobs.ADR_COMPLEMENT,
                    allJobsBean.getAdr_interv_complement());
            allJobHashMap.put(KEYS.CurrentJobs.MOBILE_CONTACT,
                    String.valueOf(allJobsBean.getMobileContact()));
            allJobHashMap.put(KEYS.CurrentJobs.CONTACT,
                    allJobsBean.getNom_contact());

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

            //adding custom fields...
            allJobHashMap.put(KEYS.CurrentJobs.CF_INTERVENTION, dataAccessObject.getAllCFInterv(allJobsBean.getId()));
            allJobHashMap.put(KEYS.CurrentJobs.CF_SITE, dataAccessObject.getAllCFSite(allJobsBean.getIdSite()));
            allJobHashMap.put(KEYS.CurrentJobs.CF_CLIENT, dataAccessObject.getAllCFClient(allJobsBean.getIdClient()));
            allJobHashMap.put(KEYS.CurrentJobs.CF_EQUIPMENT, dataAccessObject.getAllCFEquip(allJobsBean.getIdEquipement()));

            allJobHashMap.put(KEYS.CurrentJobs.DATEMEETING,
                    String.valueOf(allJobsBean.getDt_meeting()));

            Date date = getDateFromDbFormat(allJobsBean
                    .getDt_deb_prev());
            boolean isJobPool = allJobsBean.isJobPool();

            if (allJobsBean.isJobPool()) {
                allJobHashMap.put(KEYS.CurrentJobs.IS_JOB_POOL,
                        "True");
            } else {
                allJobHashMap.put(KEYS.CurrentJobs.IS_JOB_POOL,
                        "True");
            }
            if (isJobPool) {
                String isJobDatePref = allJobsBean.getDatePref();
                int idJobWindow = allJobsBean.getIdJobWindow();
                allJobHashMap.put(KEYS.CurrentJobs.DATE_PREF, isJobDatePref);
                allJobHashMap.put(KEYS.CurrentJobs.ID_JOB_WINDOW, String.valueOf(idJobWindow));
                if (idJobWindow > 0) {
                    String startTime = dataAccessObject.startTimeJobWindow(idJobWindow);
                    String endTime = dataAccessObject.endTimeJobWindow(idJobWindow);
                    allJobHashMap.put(KEYS.CurrentJobs.ID_JOB_WINDOW_START_TIME, startTime);
                    allJobHashMap.put(KEYS.CurrentJobs.ID_JOB_WINDOW_END_TIME, endTime);
                }
            }

            String dateToshow = null;

            if (jobStatus <= KEYS.CurrentJobs.JOB_NOT_STARTED2) {

                allJobHashMap.put(KEYS.CurrentJobs.DATE_TO_SHOW,
                        dateFormatCurrentJobList.format(date) + "");

                allJobHashMap.put(KEYS.CurrentJobs.TIME_TO_SHOW,
                        timeFormatCurrentJobList.format(date));
            }

            allJobListNew.add(allJobHashMap);

        }

        allJobList.addAll(allJobListNew);

        return allJobList;
    }

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
                        allJobPoolListAdapter.setIndexPosition(index);
                    } else {
                        allJobPoolListAdapter.setIndexPosition(searchIndex);
                    }
                    allJobPoolListAdapter.notifyDataSetChanged();
                } else {
                    if (!isUserSearching) {
                        mAllJobPoolSortingAdapter.setIndexPosition(index);
                    } else {
                        mAllJobPoolSortingAdapter.setIndexPosition(searchIndex);
                    }
                    mAllJobPoolSortingAdapter.notifyDataSetChanged();
                }
                isLoading = false;
            }
        }, 900);

    }

    private class FetchAllJobPoolOnScroll extends AsyncTaskCoroutine<String, Void> {

        private int option;
        private String mLatitude;
        private String mLongitude;

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            footerView.setVisibility(View.VISIBLE);
            jobPoolOffset = jobPoolOffset + mCount;
        }

        @Override
        public Void doInBackground(String... params) {
            if (optionNew == KEYS.AllJobSortingOptions.SORT_BY_DATE) {
                createAdapterAndInflateDataInListView();
            } else {
                getListForSorting(mSortingOption, mLatitude, mLongitude);
            }
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            super.onPostExecute(result);
            footerView.setVisibility(View.GONE);

            if (!isUserSearching) {
                index++;
            } else {
                searchIndex++;
            }
            if (isDateAdapter) {
                if (!isUserSearching) {
                    allJobPoolListAdapter.setIndexPosition(index);
                } else {
                    allJobPoolListAdapter.setIndexPosition(searchIndex);
                }
                allJobPoolListAdapter.notifyDataSetChanged();
                Log.e("Scrollb", " THE FetchAllJobPoolOnScroll 1 >>>>>" + allJobList.size());
            } else {
                if (!isUserSearching) {
                    mAllJobPoolSortingAdapter.setIndexPosition(index);
                } else {
                    mAllJobPoolSortingAdapter.setIndexPosition(searchIndex);
                }
                mAllJobPoolSortingAdapter.updateList(allJobList);
                mAllJobPoolSortingAdapter.notifyDataSetChanged();
                Log.e("Scrollb", " THE FetchAllJobPoolOnScroll 2 >>>>>" + allJobList.size());
            }
            isLoading = false;

        }

    }
    //------------------------------------------LISTENER---ENDS---------------------------------------------

    /***
     * Async task to fetch all the jobs.
     * in search field
     * @author Trident
     */

    private class FetchAllJobPoolInSearch extends AsyncTaskCoroutine<String, Void> {
        String searchText;


        public FetchAllJobPoolInSearch(String searchText, Boolean clear) {
            this.searchText = searchText;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            progressBarDeadlineJob.setVisibility(View.VISIBLE);
            if (allJobList != null) {
                allJobList.clear();
            } else {
                allJobList = new ArrayList<>();
            }
        }

        @Override
        public Void doInBackground(String... params) {
            getSearchList(searchText,true);
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            super.onPostExecute(result);
            setSortingListAdapter();
            progressBarDeadlineJob.setVisibility(View.GONE);


        }
    }

    /**
     * Sort list by search text and set adapter
     *
     * @param  : search text
     */
    public synchronized void getSearchList(String searchText, Boolean clearJobList) {

        if (allJobListSearch != null) {
            allJobListSearch.clear();
        } else {
            allJobListSearch = new ArrayList<>();
        }
        if (clearJobList)
            if (allJobList != null) {
                allJobList.clear();
            } else {
                allJobList = new ArrayList<>();
            }

        Log.e("karan", " THE Search searchtext" + searchText);
        Log.e("karan", " THE Search jobPoolOffset" + jobPoolOffset);




        HashMap<String, String> allJobHashMap;

        ArrayList<CommonListBean> allJobBeanVector = null;

        CurrentJobDataBean allJobsDataBean;

        allJobBeanVector = dataAccessObject.getJobPoolSearchWithOffset(jobPoolOffset,searchText);
//        allJobBeanVector = allJobsDataBean.getCommonJobDataBean();

        for (int i = 0; i < allJobBeanVector.size(); i++) {

            CommonListBean commonListBean = allJobBeanVector.get(i);
            CommonJobBean allJobsBean = (CommonJobBean) commonListBean;
            int jobStatus = allJobsBean.getCd_status_interv();

            String jobNumber = allJobsBean.getType_Interv();

            allJobHashMap = new HashMap<>();
            allJobHashMap.put(KEYS.CurrentJobs.DISPO, "false");

            if (!TextUtils.isEmpty(allJobsBean.getRef_customer())) {
                jobNumber = "#" + allJobsBean.getRef_customer()
                        + " - " + allJobsBean.getType_Interv();
                allJobHashMap.put(KEYS.CurrentJobs.REF_CUSTOMER,
                        allJobsBean.getRef_customer());
//                allJobHashMap.put(KEYS.CurrentJobs.JOB_NUMBER, allJobsBean.getRef_customer());
            } else if (allJobsBean.getNo_interv() != 0) {
                jobNumber = "#" + allJobsBean.getNo_interv()
                        + " - " + allJobsBean.getType_Interv();
                allJobHashMap.put(KEYS.CurrentJobs.N0_INTERV,
                        String.valueOf(allJobsBean.getNo_interv()));
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
            allJobHashMap.put(KEYS.CurrentJobs.ADR_GLOBAL,
                    allJobsBean.getAdr_interv_globale());

            allJobHashMap.put(KEYS.CurrentJobs.DESC, allJobsBean.getDesc());
            allJobHashMap.put(KEYS.CurrentJobs.ADR_COMPLEMENT,
                    allJobsBean.getAdr_interv_complement());
            allJobHashMap.put(KEYS.CurrentJobs.MOBILE_CONTACT,
                    String.valueOf(allJobsBean.getMobileContact()));

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

            //adding custom fields...
            allJobHashMap.put(KEYS.CurrentJobs.CF_INTERVENTION, dataAccessObject.getAllCFInterv(allJobsBean.getId()));
            allJobHashMap.put(KEYS.CurrentJobs.CF_SITE, dataAccessObject.getAllCFSite(allJobsBean.getIdSite()));
            allJobHashMap.put(KEYS.CurrentJobs.CF_CLIENT, dataAccessObject.getAllCFClient(allJobsBean.getIdClient()));
            allJobHashMap.put(KEYS.CurrentJobs.CF_EQUIPMENT, dataAccessObject.getAllCFEquip(allJobsBean.getIdEquipement()));

            allJobHashMap.put(KEYS.CurrentJobs.DATEMEETING,
                    String.valueOf(allJobsBean.getDt_meeting()));

            Date date = getDateFromDbFormat(allJobsBean
                    .getDt_deb_prev());
            boolean isJobPool = allJobsBean.isJobPool();

            if (allJobsBean.isJobPool()) {
                allJobHashMap.put(KEYS.CurrentJobs.IS_JOB_POOL,
                        "True");
            } else {
                allJobHashMap.put(KEYS.CurrentJobs.IS_JOB_POOL,
                        "True");
            }
            if (isJobPool) {
                String isJobDatePref = allJobsBean.getDatePref();
                int idJobWindow = allJobsBean.getIdJobWindow();
                allJobHashMap.put(KEYS.CurrentJobs.DATE_PREF, isJobDatePref);
                allJobHashMap.put(KEYS.CurrentJobs.ID_JOB_WINDOW, String.valueOf(idJobWindow));
                if (idJobWindow > 0) {
                    String startTime = dataAccessObject.startTimeJobWindow(idJobWindow);
                    String endTime = dataAccessObject.endTimeJobWindow(idJobWindow);
                    allJobHashMap.put(KEYS.CurrentJobs.ID_JOB_WINDOW_START_TIME, startTime);
                    allJobHashMap.put(KEYS.CurrentJobs.ID_JOB_WINDOW_END_TIME, endTime);
                }
            }

            if (jobStatus <= KEYS.CurrentJobs.JOB_NOT_STARTED2) {

                allJobHashMap.put(KEYS.CurrentJobs.DATE_TO_SHOW,
                        dateFormatCurrentJobList.format(date) + "");

                allJobHashMap.put(KEYS.CurrentJobs.TIME_TO_SHOW,
                        timeFormatCurrentJobList.format(date));
            }


            allJobListSearch.add(allJobHashMap);
            Log.e("NAMES", " THE Search jobNumber >>>>>" + jobNumber);
        }

        allJobList.addAll(allJobListSearch);
        Log.e("karan", " THE Search getSearchList allJobListSearch SIZE IS 3>>>>>" + allJobListSearch.size());
        Log.e("karan", " THE Search getSearchList allJobList SIZE IS 4>>>>>" + allJobList.size());

//        removeDuplicates();
    }
//    public synchronized void removeDuplicates() {
////        HashSet<HashMap<String, String>> set = new HashSet<>(allJobListSearch);
////        allJobListSearch.clear();
////        allJobListSearch.addAll(set);
//        allJobListSearch = new ArrayList<>(new HashSet<>(allJobListSearch));
//        Log.e("ARRAYLIST", " THE Search ARRAYLIST SIZE IS 3 >>>>>" + allJobListSearch.size());
//    }


    private class FetchAllJobPoolInSearchOnScroll extends AsyncTaskCoroutine<String, Void> {
        String searchText;

        public FetchAllJobPoolInSearchOnScroll(String searchText) {
            this.searchText = searchText;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            footerView.setVisibility(View.VISIBLE);
            jobPoolOffset = jobPoolOffset + mCount;
        }

        @Override
        public Void doInBackground(String... params) {
                getSearchList(searchText,false);
               return null;
        }

        @Override
        public void onPostExecute(Void result) {
            super.onPostExecute(result);
            footerView.setVisibility(View.GONE);
            if (!isUserSearching) {
                index++;
            } else {
                searchIndex++;
            }
            if (isDateAdapter) {
                if (!isUserSearching) {
                    allJobPoolListAdapter.setIndexPosition(index);
                } else {
                    allJobPoolListAdapter.setIndexPosition(searchIndex);
                }
                allJobPoolListAdapter.notifyDataSetChanged();
                Log.e("Scrolla", " THE allJobPoolListAdapter 1 >>>>>" + allJobList.size());
            } else {
                if (!isUserSearching) {
                    mAllJobPoolSortingAdapter.setIndexPosition(index);
                } else {
                    mAllJobPoolSortingAdapter.setIndexPosition(searchIndex);
                }
                mAllJobPoolSortingAdapter.updateList(allJobList);
                mAllJobPoolSortingAdapter.notifyDataSetChanged();
                Log.e("Scrolla", " THE allJobPoolListAdapter 2 >>>>>" + allJobList.size());

            }
            isLoading = false;
        }

    }


}




