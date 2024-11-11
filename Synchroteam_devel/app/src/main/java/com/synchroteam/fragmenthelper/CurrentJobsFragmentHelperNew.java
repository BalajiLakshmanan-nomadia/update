package com.synchroteam.fragmenthelper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.provider.Settings.System;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.baseactivity.SyncroTeamBaseActivity;
import com.synchroteam.beans.CommonJobBean;
import com.synchroteam.beans.CommonListBean;
import com.synchroteam.beans.Conge;
import com.synchroteam.beans.CurrentJobArrayListBean;
import com.synchroteam.beans.CurrentJobDataBean;
import com.synchroteam.beans.EnableSynchronizationAddJobEvent;
import com.synchroteam.beans.UpdateDataBaseEvent;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.AllJobsSortingDialog;
import com.synchroteam.events.RefreshListEvent;
import com.synchroteam.fragment.BaseFragment;
import com.synchroteam.fragment.CurrentJobFragment;
import com.synchroteam.listadapters.AllJobsDateAdapterRv;
import com.synchroteam.listadapters.AllJobsSortingAdapterRv;
import com.synchroteam.listadapters.CurrentJobsAdapterRv;
import com.synchroteam.listeners.RvEmptyListener;
import com.synchroteam.scanner.CodeScannerActivity;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DateFormatUtils;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.MyGallery;
import com.synchroteam.utils.PlayServicesUtil;
import com.synchroteam.utils.QuickReturnBehaviorForCurrentJobs;
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
import java.util.Timer;
import java.util.TreeMap;
import java.util.Vector;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import de.greenrobot.event.EventBus;

/**
 * This class is responsible to handle all the functionality of Current Job
 * screen 1.To inflate layout of current Jobs Screen 2.To fetch Data from Ultra
 * Lite Db and show it in list view.
 *
 * @author ${Ideavate Solution}
 */
@SuppressWarnings("deprecation")
@SuppressLint("SimpleDateFormat")
public class CurrentJobsFragmentHelperNew implements HelperInterface,
        RvEmptyListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    SyncroTeamBaseActivity syncroTeamBaseActivity;
    private ProgressBar pbCurrentJobs;
    private RecyclerView rvCurrentJobs;
    private LinearLayoutManager linearLayoutManager;
    private android.widget.TextView txtEmptyList;
    private LinearLayout linSearchSort;
    private com.synchroteam.TypefaceLibrary.TextView txtSortBy;
    private android.widget.TextView txtIcSort;
    private android.widget.TextView txtIcSearch;
    private android.widget.TextView txtIcBarcode;
    private EditText edSearchJobs;
    private AllJobsSortingDialog dialogSorting;
    private FragmentManager fragmentManager;

    private CurrentJobFragment currentJobFragment;
    private CurrentJobsAdapterRv currentJobsAdapterRv;
    private Dao dataAceesObject;
    private Date todayDate;
    private Date dateSelected;
    private TreeMap<String, ArrayList<HashMap<String, String>>> currentJobList;

    private AllJobsDateAdapterRv allJobsListAdapter;
    private AllJobsSortingAdapterRv mAllJobsSortingAdapter;

    private ArrayList<HashMap<String, String>> currentJobsByNoSorting;
    private ArrayList<HashMap<String, String>> currentJobsByDateSorting;
    private ArrayList<HashMap<String, String>> currentJobsByOtherSorting;
    private Filter mCurrentJobDateFilter;
    private Filter mCurrentJobSortingFilter;

    private View view;
    private BaseFragment baseFragment;
    private MyGallery currentJobWeekRibbionGallary;
    private int todayDatePositionWeekRibbon;
    private int lowerLimitOfAllowedSelectionWeekRibbon;
    private int upperLimitofAllowedSelectionWeekRibbon;
    private ArrayList<Date> datesToBeShownOnGallary;
    private GallaryAdapter gallaryAdapterWeekRibbon;
    private int previousPositionSelected;
    private DateFormat dateFormatCurrentJobList;
    private SimpleDateFormat dateFormatUnavabilities;
    private Format timeFormatCurrentJobList;
    private User user;
    private int jobCount;
    private boolean isItemClicked = true;
    private static final String dateFormat1 = "mm/dd/yyyy";
    private static final String dateFormat2 = "dd/mm/yyyy";
    private static final String dateFormat3 = "yyyy/mm/dd";

    private String mSortingName;
    private int mSortingOption = 0;
    private boolean isAdapter;
    private boolean isDateAdapter;
    private boolean isSortAdapter;

    private String currentDateFormat = "yyyy-MM-dd HH:mm:ss";

    private Timer myTimer;
    protected LocationManager locationManager;
    private LocationRequest mLocationRequest;
    private GoogleApiClient locationClient;
    private boolean isLocationClientConnected = false;

    private int index;
    private int searchIndex;
    private boolean isUserSearching = false;
    private boolean isDbUpdated = false;

    private boolean isLoading;
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;

    private boolean isBarcodeRead;
    public static int isUserClickedSortByDate = 0;

    private static final String TAG = CurrentJobsFragmentHelperNew.class.getSimpleName();

    private DateFormat dateFormatWeekRibbon;

    ArrayList<CommonListBean> todaysJobList;
    ArrayList<CommonListBean> todaysActivityList;
    ArrayList<CommonListBean> allTodaysJobList;

    private int offset =1;

    ArrayList<CommonListBean> currentJobBeanArrayList;

    /**
     * Instantiates a new current jobs fragment helper.
     *
     * @param syncroTeamBaseActivity the syncro team base activity
     * @param baseFragment           the base fragment
     */
    public CurrentJobsFragmentHelperNew(
            SyncroTeamBaseActivity syncroTeamBaseActivity, CurrentJobFragment currentJobFragment,
            BaseFragment baseFragment) {

        this.syncroTeamBaseActivity = syncroTeamBaseActivity;
        this.currentJobFragment = currentJobFragment;
        this.baseFragment = baseFragment;
        dateFormatWeekRibbon = new SimpleDateFormat("yyyy-MM-dd");
        dataAceesObject = DaoManager.getInstance();
        user = dataAceesObject.getUser();
        todaysJobList = new ArrayList<>();
        todaysActivityList = new ArrayList<>();
        allTodaysJobList = new ArrayList<>();

    }


    @Override
    public View inflateLayout(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.layout_current_jobs_new, container, false);

        SharedPref.setSortedJobNumber("", syncroTeamBaseActivity);
        SharedPref.setSortingOption(0, syncroTeamBaseActivity);


        initiateView(view);

        Calendar calendar = Calendar.getInstance();
        todayDate = calendar.getTime();

        dateSelected = todayDate;

        dateFormatCurrentJobList = DateFormat.getDateInstance(DateFormat.LONG);
        timeFormatCurrentJobList = android.text.format.DateFormat
                .getTimeFormat(syncroTeamBaseActivity);

        setDatesOfGallary(true);

        new FetchCurrentJobs(true, true).execute("" + mSortingOption, "", "");
//        new FetchCurrentJobs(false, true).execute("" + mSortingOption, "", "");

        Logger.log("TAG", "Calling the fetch current job query 1");
        return view;
    }

    /**
     * Gets the date with required pattern.
     *
     * @param date        the date
     * @param datePattern the date pattern
     * @return requiredDate
     * @throws ParseException the parse exception
     */

    private String getDateWithRequiredPresettedPattern(String date,
                                                       String datePattern) throws ParseException {
        String requiredDate = "";
        if (!TextUtils.isEmpty(date)) {
            SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
            Date requestDate = formatter.parse(date);

            DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
            requiredDate = dateFormat.format(requestDate);
            Format format = android.text.format.DateFormat
                    .getTimeFormat(syncroTeamBaseActivity);
            requiredDate = requiredDate + " " + format.format(requestDate);

        }
        return requiredDate;
    }

    /**
     * Sets the dates of gallary.
     *
     * @param isCurrentJobToBeSelected the new dates of gallary
     */
    private void setDatesOfGallary(final boolean isCurrentJobToBeSelected) {

        if (datesToBeShownOnGallary == null) {
            datesToBeShownOnGallary = new ArrayList<Date>();
        } else {
            datesToBeShownOnGallary.clear();
        }
        if (datesToBeShownOnGallary != null)
            datesToBeShownOnGallary.addAll(dataAceesObject.getAllDates());

        for (int i = 0; i < datesToBeShownOnGallary.size(); i++) {
            try {

                int j = datesToBeShownOnGallary.get(i).compareTo(
                        dateFormatWeekRibbon.parse(dateFormatWeekRibbon
                                .format(todayDate)));
                if (j == 0) {
                    todayDatePositionWeekRibbon = i;
                }
            } catch (ParseException e) {
                Logger.printException(e);
            }

        }
        lowerLimitOfAllowedSelectionWeekRibbon = todayDatePositionWeekRibbon - 3;
        upperLimitofAllowedSelectionWeekRibbon = todayDatePositionWeekRibbon + 3;

        if (gallaryAdapterWeekRibbon == null) {
            gallaryAdapterWeekRibbon = new GallaryAdapter(
                    syncroTeamBaseActivity, datesToBeShownOnGallary);
            currentJobWeekRibbionGallary.setAdapter(gallaryAdapterWeekRibbon);

        } else {
            gallaryAdapterWeekRibbon.notifyDataSetChanged();
        }

        if (isCurrentJobToBeSelected) {
            currentJobWeekRibbionGallary
                    .setSelection(todayDatePositionWeekRibbon);
            previousPositionSelected = todayDatePositionWeekRibbon;
        } else {
            currentJobWeekRibbionGallary.setSelection(previousPositionSelected);
        }

        currentJobWeekRibbionGallary.setCallbackDuringFling(false);
        currentJobWeekRibbionGallary
                .setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        isItemClicked = true;
                        setDateAndFilterList(view, position);

                    }

                });

        currentJobWeekRibbionGallary
                .setOnItemSelectedListener(new OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                        if (arg1 != null) {
                            if (!isItemClicked) {
                                setDateAndFilterList(arg1, arg1.getId());
                            } else {
                                isItemClicked = false;
                            }

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {

                    }

                });

    }

    /**
     * Fetches the current jobs from the data base based on the value of
     * dateSelected object creates the list of current jobs fetch and create or
     * notify the currentJobsListAdapter with the fresh data.
     */
    @SuppressWarnings("deprecation")
    private void getCurrentJobsWithNoSorting() {

        if (currentJobList != null) {
            currentJobList.clear();
        } else {
            currentJobList = null;
            currentJobList = new TreeMap<>(
                    new Comparator<String>() {

                        @Override
                        public int compare(String lhs, String rhs) {
                            return lhs.compareTo(rhs);
                        }
                    });
        }

        if (currentJobsByNoSorting != null) {
            currentJobsByNoSorting.clear();
        } else {
            currentJobsByNoSorting = new ArrayList<>();
        }

        HashMap<String, String> currentJobHashMap;

        ArrayList<CommonListBean> currentJobBeanVector = new ArrayList<>();

        if (user != null) {

//            CurrentJobArrayListBean currentJobDataBean = dataAceesObject
//                    .getCurrentJobValues(dateSelected, user.getId(),true,KEYS.CurrentJobsSorting.SORT_BY_NONE);
            CurrentJobArrayListBean currentJobDataBean = dataAceesObject
                    .getTodaysJobs(dateSelected, user.getId(),KEYS.CurrentJobsSorting.SORT_BY_NONE);

            currentJobBeanArrayList = currentJobDataBean.getCommonJobDataBean();

            // ArrayList<CommonListBean>
            for (int i = 0; i < currentJobBeanArrayList.size(); i++) {
                CommonListBean commonListBean = currentJobBeanArrayList.get(i);

                if (commonListBean.getIsJobBean()) {

                    CommonJobBean currentJobsBean = (CommonJobBean) commonListBean;
                    Log.e("ArrayCheck","id intervention is--->"+currentJobsBean.getId());
                    int jobStatus = currentJobsBean.getCd_status_interv();
                    if (jobStatus != KEYS.CurrentJobs.JOB__COMPLETE) {

                        String jobNumber = currentJobsBean.getType_Interv();

                        currentJobHashMap = new HashMap<String, String>();
                        currentJobHashMap.put(KEYS.CurrentJobs.DISPO, "false");

                        if (!TextUtils.isEmpty(currentJobsBean.getRef_customer()))
                            jobNumber = "#" + currentJobsBean.getRef_customer()
                                    + " - " + currentJobsBean.getType_Interv();
                        else if (currentJobsBean.getNo_interv() != 0)
                            jobNumber = "#" + currentJobsBean.getNo_interv()
                                    + " - " + currentJobsBean.getType_Interv();

                        currentJobHashMap.put(KEYS.CurrentJobs.ALL_JOB_HEADER,
                                currentJobsBean.getHeaderDate());
                        currentJobHashMap.put(KEYS.CurrentJobs.IS_CURRENT_JOB,
                                KEYS.CurrentJobs.TRUE);
                        currentJobHashMap.put(KEYS.CurrentJobs.ID,
                                currentJobsBean.getId());
                        currentJobHashMap.put(KEYS.CurrentJobs.CD_STATUS, String
                                .valueOf(currentJobsBean.getCd_status_interv()));
                        currentJobHashMap.put(KEYS.CurrentJobs.ID_USER,
                                String.valueOf(currentJobsBean.getIdUser()));

                        // put nom_client_interv name
                        currentJobHashMap.put(KEYS.CurrentJobs.NOM_CLIENT_INTERV,
                                currentJobsBean.getNom_client_interv());

                        currentJobHashMap.put(KEYS.CurrentJobs.DESC,
                                String.valueOf(currentJobsBean.getDesc()));

                        currentJobHashMap.put(KEYS.CurrentJobs.TELCEL,
                                currentJobsBean.getTel_contact());

                        currentJobHashMap.put(KEYS.CurrentJobs.MOBILE_CONTACT,
                                currentJobsBean.getMobileContact());

                         currentJobHashMap.put(KEYS.CurrentJobs.ADR_GLOBAL,
                         currentJobsBean.getAdr_interv_globale());

                        currentJobHashMap.put(KEYS.CurrentJobs.ADR_COMPLEMENT,
                                currentJobsBean.getAdr_interv_complement());

                        // currentJobHashMap.put(KEYS.CurrentJobs.DATE_CREATED_REAL,
                        // currentJobsBean.getDt_deb_prev());

                        currentJobHashMap.put(KEYS.CurrentJobs.ID_MODEL, String
                                .valueOf(currentJobsBean.getId_model_rapport()));
                        currentJobHashMap.put(KEYS.CurrentJobs.TYPE, jobNumber);
                        currentJobHashMap.put(KEYS.CurrentJobs.DESC, currentJobsBean.getDesc());
                        currentJobHashMap.put(KEYS.CurrentJobs.PRIORITY,
                                currentJobsBean.getPriorite() + "");

                        currentJobHashMap.put(KEYS.CurrentJobs.LAT,
                                currentJobsBean.getLat());
                        currentJobHashMap.put(KEYS.CurrentJobs.LON,
                                currentJobsBean.getLon());

                        currentJobHashMap.put(KEYS.CurrentJobs.NOMSITE,
                                currentJobsBean.getNom_site_interv());
                        currentJobHashMap.put(KEYS.CurrentJobs.NOMEQUIPMENT,
                                currentJobsBean.getNom_equipement());
                        currentJobHashMap.put(KEYS.CurrentJobs.IDSITE,
                                String.valueOf(currentJobsBean.getIdSite()));
                        currentJobHashMap.put(KEYS.CurrentJobs.IDCLIENT,
                                String.valueOf(currentJobsBean.getIdClient()));
                        currentJobHashMap.put(KEYS.CurrentJobs.IDEQUIPMENT,
                                String.valueOf(currentJobsBean.getIdEquipement()));

                        currentJobHashMap.put(KEYS.CurrentJobs.MOBILE_CONTACT,
                                String.valueOf(currentJobsBean.getMobileContact()));



                        //add ectra for date n sort

                        currentJobHashMap.put(KEYS.CurrentJobs.DT_CREATED,
                                currentJobsBean.getDtCreated());

                        currentJobHashMap.put(KEYS.CurrentJobs.DATEMEETING,
                                String.valueOf(currentJobsBean.getDt_meeting()));

                        Date date = getDateFromDbFormat(currentJobsBean
                                .getDt_deb_prev());

                        // currentJobHashMap.put(KEYS.CurrentJobs.DT_DEBUT_PREV,
                        // currentJobsBean.getDt_deb_prev());

                        String dateToshow = null;

                        if (jobStatus == KEYS.CurrentJobs.JOB__STARTED) {

                            if (user.getId() != currentJobsBean.getIdUser()) {
                                try {
                                    dateToshow = dataAceesObject
                                            .getDateWithRequiredPresettedPattern(date);
                                } catch (ParseException e) {
                                    Logger.printException(e);
                                }
                                if (!TextUtils.isEmpty(dateToshow)) {

                                    String[] dateTopass = dateToshow.split("/");

                                    currentJobHashMap.put(
                                            KEYS.CurrentJobs.DATE_TO_SHOW,
                                            dateTopass[0]);

                                    currentJobHashMap.put(
                                            KEYS.CurrentJobs.TIME_TO_SHOW,
                                            dateTopass[1]);
                                }
                            } else {

                                dateToshow = dataAceesObject
                                        .getJobStartTime(currentJobsBean.getId());
                                if (!TextUtils.isEmpty(dateToshow)) {

                                    String[] dateTopass = dateToshow.split("/");

                                    currentJobHashMap.put(
                                            KEYS.CurrentJobs.DATE_TO_SHOW,
                                            dateTopass[0]);

                                    currentJobHashMap.put(
                                            KEYS.CurrentJobs.TIME_TO_SHOW,
                                            dateTopass[1]);
                                }

                            }

                        } else if (jobStatus == KEYS.CurrentJobs.JOB__SUSPENDED) {

                            if (user.getId() != currentJobsBean.getIdUser()) {

                                if (!TextUtils.isEmpty(dateToshow)) {

                                    currentJobHashMap.put(
                                            KEYS.CurrentJobs.DATE_TO_SHOW, "");

                                    currentJobHashMap.put(
                                            KEYS.CurrentJobs.TIME_TO_SHOW, "");
                                }
                            } else {

                                dateToshow = dataAceesObject
                                        .getJobSuspendedTime(currentJobsBean
                                                .getId());
                                if (!TextUtils.isEmpty(dateToshow)) {

                                    String[] dateTopass = dateToshow.split("/");

                                    currentJobHashMap.put(
                                            KEYS.CurrentJobs.DATE_TO_SHOW,
                                            dateTopass[0]);

                                    currentJobHashMap.put(
                                            KEYS.CurrentJobs.TIME_TO_SHOW,
                                            dateTopass[1]);
                                }
                            }

                        } else {

                            currentJobHashMap.put(KEYS.CurrentJobs.DATE_TO_SHOW,
                                    dateFormatCurrentJobList.format(date) + "");

                            currentJobHashMap.put(KEYS.CurrentJobs.TIME_TO_SHOW,
                                    timeFormatCurrentJobList.format(date));
                        }

                        //dont show for sort by none
                        if (currentJobList.containsKey(currentJobHashMap
                                .get(KEYS.CurrentJobs.ALL_JOB_HEADER))) {
                            currentJobList.get(
                                            currentJobHashMap
                                                    .get(KEYS.CurrentJobs.ALL_JOB_HEADER))
                                    .add(currentJobHashMap);

                            currentJobHashMap.put(KEYS.CurrentJobs.HAS_HEADER, "false");
                            currentJobsByNoSorting.add(currentJobHashMap);
                        } else {
                            currentJobList.put(currentJobHashMap
                                            .get(KEYS.CurrentJobs.ALL_JOB_HEADER),
                                    new ArrayList<HashMap<String, String>>());
                            currentJobList.get(
                                            currentJobHashMap
                                                    .get(KEYS.CurrentJobs.ALL_JOB_HEADER))
                                    .add(currentJobHashMap);

                            currentJobHashMap.put(KEYS.CurrentJobs.HAS_HEADER, "true");
                            currentJobsByNoSorting.add(currentJobHashMap);
                        }
                    }

                } else {
                    Conge indisp = (Conge) commonListBean;

                    HashMap<String, String> map = new HashMap<String, String>();

                    SimpleDateFormat sdf = new SimpleDateFormat(
                            "dd/MM/yyyy HH:mm:ss");
                    SimpleDateFormat updateFormat = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss.SSS");

                    Date dateD, dateF = null;
                    String activityStartDate = null;
                    try {
                        activityStartDate = getDateWithRequiredPresettedPattern(indisp.getDtDebut(), currentDateFormat);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    dateD = getDateFromDbFormat(indisp.getDtDebut());
                    if (indisp.getDtFin() != null) {
                        dateF = getDateFromDbFormat(indisp.getDtFin());

                        String activityEndDate = null;
                        try {
                            activityEndDate = getDateWithRequiredPresettedPattern(indisp.getDtFin(), currentDateFormat);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        map.put(KEYS.Unavabilities.PLAN_TIME_START_END,
                                activityStartDate
                                        + " - "
                                        + activityEndDate);
                        map.put(KEYS.Unavabilities.END_DATE, sdf.format(dateF));

                        map.put(KEYS.Unavabilities.PLAN_START_DATE_TIME,
                                updateFormat.format(dateD));
                        map.put(KEYS.Unavabilities.PLAN_END_DATE_TIME,
                                updateFormat.format(dateF));
                    } else {
                        map.put(KEYS.Unavabilities.PLAN_TIME_START_END,
                                activityStartDate
                                        + " - " + " ");
                        map.put(KEYS.Unavabilities.PLAN_START_DATE_TIME,
                                updateFormat.format(dateD));
                    }
                    map.put(KEYS.Unavabilities.TYPE, indisp.getNomTypeConge());
                    map.put(KEYS.Unavabilities.ID_TYPE_CONGE, "" + indisp.getIdTypeConge());
                    map.put(KEYS.Unavabilities.UNAVAILABILITY_ID,
                            indisp.getIdConge());
                    map.put(KEYS.Unavabilities.CLTVILLE, indisp.getNote());
                    map.put(KEYS.CurrentJobs.ALL_JOB_HEADER, indisp.getHeaderDate());

                    map.put(KEYS.CurrentJobs.IS_CURRENT_JOB, KEYS.CurrentJobs.FALSE);

                    map.put(KEYS.Unavabilities.IMG, indisp.getCouleurConge());

                    map.put(KEYS.Unavabilities.SELECTED_DATE,
                            sdf.format(dateSelected));

                    map.put(KEYS.Unavabilities.ID_USER, indisp.getIdUser());

                    map.put(KEYS.Unavabilities.ID_GROUPE, indisp.getIdGroupe());

                    map.put(KEYS.Unavabilities.FL_PAYABLE, String.valueOf(indisp.getFlPayable()));

                    map.put(KEYS.Unavabilities.FL_UNAVAILABLE, String.valueOf(indisp.getFlUnavailable()));

                    if (currentJobList.containsKey(map
                            .get(KEYS.CurrentJobs.ALL_JOB_HEADER))) {
                        currentJobList
                                .get(map.get(KEYS.CurrentJobs.ALL_JOB_HEADER)).add(
                                        map);
                        map.put(KEYS.CurrentJobs.HAS_HEADER, "false");

                        //add only the activities which have no end date or end before the selected date
                        if (dateF != null) {

                            SimpleDateFormat dateAlone = new SimpleDateFormat(
                                    "dd/MM/yyyy");
                            String startingDate = dateAlone.format(dateF);
                            String startingDate1 = dateAlone.format(dateD);
                            String endDate = dateAlone.format(dateSelected);

                            if(startingDate.compareTo(endDate)>=0 && startingDate1.compareTo(endDate)<=0) {
                                currentJobsByNoSorting.add(map);
                            }
//                            if (dateF.compareTo(dateSelected) >= 0) {
//                                currentJobsByNoSorting.add(map);
//                            }
                        } else {
                            currentJobsByNoSorting.add(map);
                        }
                    } else {
                        currentJobList.put(
                                map.get(KEYS.CurrentJobs.ALL_JOB_HEADER),
                                new ArrayList<HashMap<String, String>>());
                        currentJobList
                                .get(map.get(KEYS.CurrentJobs.ALL_JOB_HEADER)).add(
                                        map);

                        map.put(KEYS.CurrentJobs.HAS_HEADER, "true");

                        //add only the activities which have no end date or end before the selected date
                        if (dateF != null) {

                            SimpleDateFormat dateAlone = new SimpleDateFormat(
                                    "dd/MM/yyyy");
                            String startingDate = dateAlone.format(dateF);
                            String startingDate1 = dateAlone.format(dateD);
                            String endDate = dateAlone.format(dateSelected);

                            if(startingDate.compareTo(endDate)>=0 && startingDate1.compareTo(endDate)<=0) {
                                currentJobsByNoSorting.add(map);
                            }
//                            if (dateF.compareTo(dateSelected) >= 0) {
//                                currentJobsByNoSorting.add(map);
//                            }
                        } else {
                            currentJobsByNoSorting.add(map);
                        }
                    }
                }

            }


        }
    }

    /**
     * Fetches the current jobs from the data base based on the value of
     * dateSelected object creates the list of current jobs fetch and create or
     * notify the currentJobsListAdapter with the fresh data.
     */
    @SuppressWarnings("deprecation")
    private void getCurrentJobsByDate() {
        if (currentJobList != null) {
            currentJobList.clear();

            currentJobsAdapterRv.notifyDataSetChanged();

//            currentJobsAdapterRv.notifyDataSetChanged();
        } else {
            currentJobList = null;
            currentJobList = new TreeMap<>(
                    new Comparator<String>() {

                        @Override
                        public int compare(String lhs, String rhs) {
                            return lhs.compareTo(rhs);
                        }
                    });
        }

        if (currentJobsByDateSorting != null) {
            currentJobsByDateSorting.clear();
        } else {
            currentJobsByDateSorting = new ArrayList<>();
        }


        HashMap<String, String> currentJobHashMap;

        ArrayList<CommonListBean> currentJobBeanVector;


        CurrentJobArrayListBean currentJobDataBean = dataAceesObject
                .getCurrentJobValuesWithSorting(dateSelected, user.getId());
        currentJobBeanArrayList = currentJobDataBean.getCommonJobDataBean();

        ArrayList<CommonListBean> activityList = new ArrayList<CommonListBean>();
        activityList = dataAceesObject.getTodaysActivityList(dateSelected);

        for (int i = 0; i < activityList.size(); i++) {
            currentJobBeanArrayList.add(activityList.get(i));
        }

        jobCount = currentJobDataBean.getCurrrntJobCount();

        for (int i = 0; i < currentJobBeanArrayList.size(); i++) {
            CommonListBean commonListBean = currentJobBeanArrayList.get(i);
            if (commonListBean.getIsJobBean()) {

                CommonJobBean currentJobsBean = (CommonJobBean) commonListBean;
                int jobStatus = currentJobsBean.getCd_status_interv();
                if (jobStatus != KEYS.CurrentJobs.JOB__COMPLETE) {

                    String jobNumber = currentJobsBean.getType_Interv();

                    currentJobHashMap = new HashMap<String, String>();
                    currentJobHashMap.put(KEYS.CurrentJobs.DISPO, "false");

                    if (!TextUtils.isEmpty(currentJobsBean.getRef_customer()))
                        jobNumber = "#" + currentJobsBean.getRef_customer()
                                + " - " + currentJobsBean.getType_Interv();
                    else if (currentJobsBean.getNo_interv() != 0)
                        jobNumber = "#" + currentJobsBean.getNo_interv()
                                + " - " + currentJobsBean.getType_Interv();

                    currentJobHashMap.put(KEYS.CurrentJobs.ALL_JOB_HEADER,
                            currentJobsBean.getHeaderDate());
                    currentJobHashMap.put(KEYS.CurrentJobs.IS_CURRENT_JOB,
                            KEYS.CurrentJobs.TRUE);
                    currentJobHashMap.put(KEYS.CurrentJobs.ID,
                            currentJobsBean.getId());
                    currentJobHashMap.put(KEYS.CurrentJobs.CD_STATUS, String
                            .valueOf(currentJobsBean.getCd_status_interv()));
                    currentJobHashMap.put(KEYS.CurrentJobs.ID_USER,
                            String.valueOf(currentJobsBean.getIdUser()));

                    // put nom_client_interv name
                    currentJobHashMap.put(KEYS.CurrentJobs.NOM_CLIENT_INTERV,
                            currentJobsBean.getNom_client_interv());

                    currentJobHashMap.put(KEYS.CurrentJobs.DESC,
                            String.valueOf(currentJobsBean.getDesc()));

                    currentJobHashMap.put(KEYS.CurrentJobs.TELCEL,
                            currentJobsBean.getTel_contact());

                    currentJobHashMap.put(KEYS.CurrentJobs.MOBILE_CONTACT,
                            currentJobsBean.getMobileContact());

                     currentJobHashMap.put(KEYS.CurrentJobs.ADR_GLOBAL,
                     currentJobsBean.getAdr_interv_globale());

                    currentJobHashMap.put(KEYS.CurrentJobs.ADR_COMPLEMENT,
                            currentJobsBean.getAdr_interv_complement());

                    currentJobHashMap.put(KEYS.CurrentJobs.MOBILE_CONTACT,
                            String.valueOf(currentJobsBean.getMobileContact()));


                    // currentJobHashMap.put(KEYS.CurrentJobs.DATE_CREATED_REAL,
                    // currentJobsBean.getDt_deb_prev());

                    currentJobHashMap.put(KEYS.CurrentJobs.ID_MODEL, String
                            .valueOf(currentJobsBean.getId_model_rapport()));
//                    currentJobHashMap.put(KEYS.CurrentJobs.JOB_NUMBER, jobNumber);
                    currentJobHashMap.put(KEYS.CurrentJobs.TYPE, jobNumber);

                    currentJobHashMap.put(KEYS.CurrentJobs.PRIORITY,
                            currentJobsBean.getPriorite() + "");

                    currentJobHashMap.put(KEYS.CurrentJobs.LAT,
                            currentJobsBean.getLat());
                    currentJobHashMap.put(KEYS.CurrentJobs.LON,
                            currentJobsBean.getLon());

                    currentJobHashMap.put(KEYS.CurrentJobs.NOMSITE,
                            currentJobsBean.getNom_site_interv());
                    currentJobHashMap.put(KEYS.CurrentJobs.NOMEQUIPMENT,
                            currentJobsBean.getNom_equipement());
                    currentJobHashMap.put(KEYS.CurrentJobs.IDSITE,
                            String.valueOf(currentJobsBean.getIdSite()));
                    currentJobHashMap.put(KEYS.CurrentJobs.IDCLIENT,
                            String.valueOf(currentJobsBean.getIdClient()));
                    currentJobHashMap.put(KEYS.CurrentJobs.IDEQUIPMENT,
                            String.valueOf(currentJobsBean.getIdEquipement()));
                    currentJobHashMap.put(KEYS.CurrentJobs.ADR_VILLE,
                            String.valueOf(currentJobsBean.getAdr_interv_ville()));

                    //adding custom fields...
                    currentJobHashMap.put(KEYS.CurrentJobs.CF_INTERVENTION, dataAceesObject.getAllCFInterv(currentJobsBean.getId()));
                    currentJobHashMap.put(KEYS.CurrentJobs.CF_SITE, dataAceesObject.getAllCFSite(currentJobsBean.getIdSite()));
                    currentJobHashMap.put(KEYS.CurrentJobs.CF_CLIENT, dataAceesObject.getAllCFClient(currentJobsBean.getIdClient()));
                    currentJobHashMap.put(KEYS.CurrentJobs.CF_EQUIPMENT, dataAceesObject.getAllCFEquip(currentJobsBean.getIdEquipement()));

                    currentJobHashMap.put(KEYS.CurrentJobs.DT_CREATED,
                            String.valueOf(currentJobsBean.getDtCreated()));

                    currentJobHashMap.put(KEYS.CurrentJobs.DATEMEETING,
                            String.valueOf(currentJobsBean.getDt_meeting()));

                    Date date = getDateFromDbFormat(currentJobsBean
                            .getDt_deb_prev());

                    // currentJobHashMap.put(KEYS.CurrentJobs.DT_DEBUT_PREV,
                    // currentJobsBean.getDt_deb_prev());

                    String dateToshow = null;

                    if (jobStatus == KEYS.CurrentJobs.JOB__STARTED) {

                        if (user.getId() != currentJobsBean.getIdUser()) {
                            try {
                                dateToshow = dataAceesObject
                                        .getDateWithRequiredPresettedPattern(date);
                            } catch (ParseException e) {
                                Logger.printException(e);
                            }
                            if (!TextUtils.isEmpty(dateToshow)) {

                                String[] dateTopass = dateToshow.split("/");

                                currentJobHashMap.put(
                                        KEYS.CurrentJobs.DATE_TO_SHOW,
                                        dateTopass[0]);

                                currentJobHashMap.put(
                                        KEYS.CurrentJobs.TIME_TO_SHOW,
                                        dateTopass[1]);
                            }
                        } else {

                            dateToshow = dataAceesObject
                                    .getJobStartTime(currentJobsBean.getId());
                            if (!TextUtils.isEmpty(dateToshow)) {

                                String[] dateTopass = dateToshow.split("/");

                                currentJobHashMap.put(
                                        KEYS.CurrentJobs.DATE_TO_SHOW,
                                        dateTopass[0]);

                                currentJobHashMap.put(
                                        KEYS.CurrentJobs.TIME_TO_SHOW,
                                        dateTopass[1]);
                            }

                        }

                    } else if (jobStatus == KEYS.CurrentJobs.JOB__SUSPENDED) {

                        if (user.getId() != currentJobsBean.getIdUser()) {

                            if (!TextUtils.isEmpty(dateToshow)) {

                                currentJobHashMap.put(
                                        KEYS.CurrentJobs.DATE_TO_SHOW, "");

                                currentJobHashMap.put(
                                        KEYS.CurrentJobs.TIME_TO_SHOW, "");
                            }
                        } else {

                            dateToshow = dataAceesObject
                                    .getJobSuspendedTime(currentJobsBean
                                            .getId());
                            if (!TextUtils.isEmpty(dateToshow)) {

                                String[] dateTopass = dateToshow.split("/");

                                currentJobHashMap.put(
                                        KEYS.CurrentJobs.DATE_TO_SHOW,
                                        dateTopass[0]);

                                currentJobHashMap.put(
                                        KEYS.CurrentJobs.TIME_TO_SHOW,
                                        dateTopass[1]);
                            }
                        }

                    } else {

                        currentJobHashMap.put(KEYS.CurrentJobs.DATE_TO_SHOW,
                                dateFormatCurrentJobList.format(date) + "");

                        currentJobHashMap.put(KEYS.CurrentJobs.TIME_TO_SHOW,
                                timeFormatCurrentJobList.format(date));
                    }

                    if (currentJobList.containsKey(currentJobHashMap
                            .get(KEYS.CurrentJobs.ALL_JOB_HEADER))) {
                        currentJobList.get(
                                        currentJobHashMap
                                                .get(KEYS.CurrentJobs.ALL_JOB_HEADER))
                                .add(currentJobHashMap);

                        currentJobHashMap.put(KEYS.CurrentJobs.HAS_HEADER, "false");
                        currentJobsByDateSorting.add(currentJobHashMap);
                    } else {
                        currentJobList.put(currentJobHashMap
                                        .get(KEYS.CurrentJobs.ALL_JOB_HEADER),
                                new ArrayList<HashMap<String, String>>());
                        currentJobList.get(
                                        currentJobHashMap
                                                .get(KEYS.CurrentJobs.ALL_JOB_HEADER))
                                .add(currentJobHashMap);

                        currentJobHashMap.put(KEYS.CurrentJobs.HAS_HEADER, "true");
                        currentJobsByDateSorting.add(currentJobHashMap);
                    }
                }

            }

        }
    }

    /**
     * Fetches the current jobs from the data base based on the value of
     * dateSelected object creates the list of current jobs fetch and create or
     * notify the currentJobsListAdapter with the fresh data.
     */
    @SuppressWarnings("deprecation")
    private void getCurrentJobsBySorting(final int options, final String latitude, final String longitude) {

        if (currentJobsByOtherSorting != null) {
            currentJobsByOtherSorting.clear();
        } else {
            currentJobsByOtherSorting = new ArrayList<>();
        }


        HashMap<String, String> currentJobHashMap;

        ArrayList<CommonListBean> currentJobBeanVector;

        if (options == KEYS.CurrentJobsSorting.SORT_BY_NEARBY || options == KEYS.CurrentJobsSorting.SORT_BY_NEARBY_JOB) {
            CurrentJobArrayListBean currentJobDataBean = dataAceesObject
                    .getCurrentJobsArrayListNearBy(dateSelected, user.getId(), latitude, longitude);
            currentJobBeanArrayList = currentJobDataBean.getCommonJobDataBean();
            jobCount = currentJobDataBean.getCurrrntJobCount();

            //if two or more jobs have the same lat & long, sort them according to the job number.
            Collections.sort(currentJobBeanArrayList, new Comparator<CommonListBean>() {
                @Override
                public int compare(CommonListBean lhs, CommonListBean rhs) {
                    return getSortingResultforNearby(lhs, rhs);
                }
            });

        } else {

            CurrentJobArrayListBean currentJobDataBean = dataAceesObject
                    .getCurrentJobValuesWithSorting(dateSelected, user.getId());
            currentJobBeanArrayList = currentJobDataBean.getCommonJobDataBean();
            jobCount = currentJobDataBean.getCurrrntJobCount();
            Collections.sort(currentJobBeanArrayList, new Comparator<CommonListBean>() {
                @Override
                public int compare(CommonListBean lhs, CommonListBean rhs) {
                    switch (options) {
                        case KEYS.CurrentJobsSorting.SORT_BY_CLIENT:
                            return getSortingResultforClient(lhs, rhs);
                        case KEYS.CurrentJobsSorting.SORT_BY_SITE:
                            return getSortingResultforSite(lhs, rhs);
                        case KEYS.CurrentJobsSorting.SORT_BY_EQUIPMENT:
                            return getSortingResultforEquipment(lhs, rhs);
                        case KEYS.CurrentJobsSorting.SORT_BY_TOWN:
                            return getSortingResultforTown(lhs, rhs);
                        case KEYS.CurrentJobsSorting.SORT_BY_PRIORITY:
                            return getSortingResultForPriority(lhs, rhs);
                        default:
                            return getSortingResultforClient(lhs, rhs);
                    }
                }
            });
        }

        ArrayList<CommonListBean> activityList = new ArrayList<CommonListBean>();
        activityList = dataAceesObject.getTodaysActivityList(dateSelected);

        for (int i = 0; i < activityList.size(); i++) {
            currentJobBeanArrayList.add(activityList.get(i));
        }

        for (int i = 0; i < currentJobBeanArrayList.size(); i++) {
            CommonListBean commonListBean = currentJobBeanArrayList.get(i);
            if (commonListBean.getIsJobBean()) {

                CommonJobBean currentJobsBean = (CommonJobBean) commonListBean;
                int jobStatus = currentJobsBean.getCd_status_interv();
                if (jobStatus != KEYS.CurrentJobs.JOB__COMPLETE) {

                    String jobNumber = currentJobsBean.getType_Interv();

                    currentJobHashMap = new HashMap<String, String>();
                    currentJobHashMap.put(KEYS.CurrentJobs.DISPO, "false");

                    if (!TextUtils.isEmpty(currentJobsBean.getRef_customer()))
                        jobNumber = "#" + currentJobsBean.getRef_customer()
                                + " - " + currentJobsBean.getType_Interv();
                    else if (currentJobsBean.getNo_interv() != 0)
                        jobNumber = "#" + currentJobsBean.getNo_interv()
                                + " - " + currentJobsBean.getType_Interv();

                    currentJobHashMap.put(KEYS.CurrentJobs.ALL_JOB_HEADER,
                            currentJobsBean.getHeaderDate());
                    currentJobHashMap.put(KEYS.CurrentJobs.IS_CURRENT_JOB,
                            KEYS.CurrentJobs.TRUE);
                    currentJobHashMap.put(KEYS.CurrentJobs.ID,
                            currentJobsBean.getId());
                    currentJobHashMap.put(KEYS.CurrentJobs.CD_STATUS, String
                            .valueOf(currentJobsBean.getCd_status_interv()));
                    currentJobHashMap.put(KEYS.CurrentJobs.ID_USER,
                            String.valueOf(currentJobsBean.getIdUser()));

                    // put nom_client_interv name
                    currentJobHashMap.put(KEYS.CurrentJobs.NOM_CLIENT_INTERV,
                            currentJobsBean.getNom_client_interv());

                    currentJobHashMap.put(KEYS.CurrentJobs.DESC,
                            String.valueOf(currentJobsBean.getDesc()));

                    currentJobHashMap.put(KEYS.CurrentJobs.TELCEL,
                            currentJobsBean.getTel_contact());

                    currentJobHashMap.put(KEYS.CurrentJobs.MOBILE_CONTACT,
                            currentJobsBean.getMobileContact());

                     currentJobHashMap.put(KEYS.CurrentJobs.ADR_GLOBAL,
                     currentJobsBean.getAdr_interv_globale());

                    currentJobHashMap.put(KEYS.CurrentJobs.ADR_COMPLEMENT,
                            currentJobsBean.getAdr_interv_complement());

                    currentJobHashMap.put(KEYS.CurrentJobs.MOBILE_CONTACT,
                            String.valueOf(currentJobsBean.getMobileContact()));


                    // currentJobHashMap.put(KEYS.CurrentJobs.DATE_CREATED_REAL,
                    // currentJobsBean.getDt_deb_prev());

                    currentJobHashMap.put(KEYS.CurrentJobs.ID_MODEL, String
                            .valueOf(currentJobsBean.getId_model_rapport()));
//                    currentJobHashMap.put(KEYS.CurrentJobs.JOB_NUMBER, jobNumber);
                    currentJobHashMap.put(KEYS.CurrentJobs.TYPE, jobNumber);
                    currentJobHashMap.put(KEYS.CurrentJobs.PRIORITY,
                            currentJobsBean.getPriorite() + "");

                    currentJobHashMap.put(KEYS.CurrentJobs.LAT,
                            currentJobsBean.getLat());
                    currentJobHashMap.put(KEYS.CurrentJobs.LON,
                            currentJobsBean.getLon());

                    currentJobHashMap.put(KEYS.CurrentJobs.NOMSITE,
                            currentJobsBean.getNom_site_interv());
                    currentJobHashMap.put(KEYS.CurrentJobs.NOMEQUIPMENT,
                            currentJobsBean.getNom_equipement());
                    currentJobHashMap.put(KEYS.CurrentJobs.IDSITE,
                            String.valueOf(currentJobsBean.getIdSite()));
                    currentJobHashMap.put(KEYS.CurrentJobs.IDCLIENT,
                            String.valueOf(currentJobsBean.getIdClient()));
                    currentJobHashMap.put(KEYS.CurrentJobs.IDEQUIPMENT,
                            String.valueOf(currentJobsBean.getIdEquipement()));
                    currentJobHashMap.put(KEYS.CurrentJobs.ADR_VILLE,
                            String.valueOf(currentJobsBean.getAdr_interv_ville()));

                    //adding custom fields...
                    currentJobHashMap.put(KEYS.CurrentJobs.CF_INTERVENTION, dataAceesObject.getAllCFInterv(currentJobsBean.getId()));
                    currentJobHashMap.put(KEYS.CurrentJobs.CF_SITE, dataAceesObject.getAllCFSite(currentJobsBean.getIdSite()));
                    currentJobHashMap.put(KEYS.CurrentJobs.CF_CLIENT, dataAceesObject.getAllCFClient(currentJobsBean.getIdClient()));
                    currentJobHashMap.put(KEYS.CurrentJobs.CF_EQUIPMENT, dataAceesObject.getAllCFEquip(currentJobsBean.getIdEquipement()));

                    currentJobHashMap.put(KEYS.CurrentJobs.DT_CREATED,
                            String.valueOf(currentJobsBean.getDtCreated()));

                    currentJobHashMap.put(KEYS.CurrentJobs.DATEMEETING,
                            String.valueOf(currentJobsBean.getDt_meeting()));

                    Date date = getDateFromDbFormat(currentJobsBean
                            .getDt_deb_prev());

                    // currentJobHashMap.put(KEYS.CurrentJobs.DT_DEBUT_PREV,
                    // currentJobsBean.getDt_deb_prev());

                    String dateToshow = null;

                    if (jobStatus == KEYS.CurrentJobs.JOB__STARTED) {

                        if (user.getId() != currentJobsBean.getIdUser()) {
                            try {
                                dateToshow = dataAceesObject
                                        .getDateWithRequiredPresettedPattern(date);
                            } catch (ParseException e) {
                                Logger.printException(e);
                            }
                            if (!TextUtils.isEmpty(dateToshow)) {

                                String[] dateTopass = dateToshow.split("/");

                                currentJobHashMap.put(
                                        KEYS.CurrentJobs.DATE_TO_SHOW,
                                        dateTopass[0]);

                                currentJobHashMap.put(
                                        KEYS.CurrentJobs.TIME_TO_SHOW,
                                        dateTopass[1]);
                            }
                        } else {

                            dateToshow = dataAceesObject
                                    .getJobStartTime(currentJobsBean.getId());
                            if (!TextUtils.isEmpty(dateToshow)) {

                                String[] dateTopass = dateToshow.split("/");

                                currentJobHashMap.put(
                                        KEYS.CurrentJobs.DATE_TO_SHOW,
                                        dateTopass[0]);

                                currentJobHashMap.put(
                                        KEYS.CurrentJobs.TIME_TO_SHOW,
                                        dateTopass[1]);
                            }

                        }

                    } else if (jobStatus == KEYS.CurrentJobs.JOB__SUSPENDED) {

                        if (user.getId() != currentJobsBean.getIdUser()) {

                            if (!TextUtils.isEmpty(dateToshow)) {

                                currentJobHashMap.put(
                                        KEYS.CurrentJobs.DATE_TO_SHOW, "");

                                currentJobHashMap.put(
                                        KEYS.CurrentJobs.TIME_TO_SHOW, "");
                            }
                        } else {

                            dateToshow = dataAceesObject
                                    .getJobSuspendedTime(currentJobsBean
                                            .getId());
                            if (!TextUtils.isEmpty(dateToshow)) {

                                String[] dateTopass = dateToshow.split("/");

                                currentJobHashMap.put(
                                        KEYS.CurrentJobs.DATE_TO_SHOW,
                                        dateTopass[0]);

                                currentJobHashMap.put(
                                        KEYS.CurrentJobs.TIME_TO_SHOW,
                                        dateTopass[1]);
                            }
                        }

                    } else {

                        currentJobHashMap.put(KEYS.CurrentJobs.DATE_TO_SHOW,
                                dateFormatCurrentJobList.format(date) + "");

                        currentJobHashMap.put(KEYS.CurrentJobs.TIME_TO_SHOW,
                                timeFormatCurrentJobList.format(date));
                    }

                    currentJobsByOtherSorting.add(currentJobHashMap);
                }

            }

        }

    }

    /**
     * Sets the current job adapter.
     */
    public void setCurrentJobAdapter() {

        if (dateFormatWeekRibbon.format(dateSelected).equals(
                dateFormatWeekRibbon.format(todayDate))) {

            SharedPref.setCurrentJobsCount(jobCount + "",
                    syncroTeamBaseActivity);

        }

        if (currentJobsByNoSorting.isEmpty()) {
            view.findViewById(R.id.dateDetailContainer)
                    .setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.dateDetailContainer).setVisibility(
                    View.VISIBLE);
        }

        Logger.log(TAG, "CURRENT JOBS SIZE IS====>" + currentJobsByNoSorting.size());

        if (currentJobsAdapterRv == null || !isAdapter) {
            currentJobsAdapterRv = new CurrentJobsAdapterRv(syncroTeamBaseActivity, this, currentJobsByNoSorting, this);
            index = 1;
            searchIndex = 1;
            currentJobsAdapterRv.setIndexPosition(index);
            rvCurrentJobs.setLayoutManager(linearLayoutManager);
            rvCurrentJobs.setAdapter(currentJobsAdapterRv);

        } else {
            currentJobsAdapterRv.notifyDataSetChanged();
        }

        setEmptyViewForRv(currentJobsByNoSorting);

        isSortAdapter = false;
        isAdapter = true;
        isDateAdapter = false;

        resetSearchField();

    }

    /**
     * Set the to Come list adapter to toCome list view.
     */
    private void setCurrentJobByDateAdapter() {

//        edSearchJobs.setEnabled(true);
//        txtIcBarcode.setEnabled(true);

        if (allJobsListAdapter == null || !isDateAdapter) {
            allJobsListAdapter = new AllJobsDateAdapterRv(syncroTeamBaseActivity, this,
                    currentJobsByDateSorting, false);
//            rvAllJobs.addFooterView(footerView);
            index = 1;
            searchIndex = 1;

            allJobsListAdapter.setIndexPosition(index);
            rvCurrentJobs.setLayoutManager(linearLayoutManager);
            rvCurrentJobs.setAdapter(allJobsListAdapter);


        } else {
            if (isDbUpdated) {
                allJobsListAdapter.setDuplicateList();
                isDbUpdated = false;
            }
            allJobsListAdapter.notifyDataSetChanged();
        }

        setEmptyViewForRv(currentJobsByDateSorting);

        isDateAdapter = true;
        isAdapter = false;
        isSortAdapter = false;

        resetSearchField();

        if (mCurrentJobDateFilter == null) {
            mCurrentJobDateFilter = allJobsListAdapter.getAllJobFilter();
            mCurrentJobSortingFilter = null;
            edSearchJobs.addTextChangedListener(mWatcher);
        } else {
            mCurrentJobDateFilter = allJobsListAdapter.getAllJobFilter();
            mCurrentJobSortingFilter = null;
            edSearchJobs.addTextChangedListener(mWatcher);
        }
//        rvAllJobs.setItemCount(allJobsListAdapter.getArrayCount());

        //check if header is already shown, if shown then shw the header for this adapter also
        if (currentJobsAdapterRv != null || mAllJobsSortingAdapter != null) {
            boolean isHeaderShown;
            if (currentJobsAdapterRv != null) {
                isHeaderShown = currentJobsAdapterRv.isHeaderShown();
            } else {
                isHeaderShown = mAllJobsSortingAdapter.isHeaderShown();
            }

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
     * Set the list adapter to deadlineExccedeListView.
     */
    private void setCurrentJobBySortingAdapter() {

//        edSearchJobs.setEnabled(true);
//        txtIcBarcode.setEnabled(true);

        if (mAllJobsSortingAdapter == null || !isSortAdapter) {
            mAllJobsSortingAdapter = new AllJobsSortingAdapterRv(syncroTeamBaseActivity, this,
                    currentJobsByOtherSorting, false);
            index = 1;
            searchIndex = 1;

            mAllJobsSortingAdapter.setIndexPosition(index);
            rvCurrentJobs.setLayoutManager(linearLayoutManager);
            rvCurrentJobs.setAdapter(mAllJobsSortingAdapter);


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

        setEmptyViewForRv(currentJobsByOtherSorting);

        resetSearchField();

        if (mCurrentJobSortingFilter == null) {
            if (mAllJobsSortingAdapter != null) {
                mCurrentJobSortingFilter = mAllJobsSortingAdapter.getJobFilter();
                mCurrentJobDateFilter = null;
                edSearchJobs.addTextChangedListener(mWatcher);
            }
        } else {
            if (allJobsListAdapter != null) {
                mCurrentJobDateFilter = allJobsListAdapter.getAllJobFilter();
                mCurrentJobSortingFilter = null;
                edSearchJobs.addTextChangedListener(mWatcher);
            }
        }

//        rvAllJobs.setItemCount(mAllJobsSortingAdapter.getArrayCount());

        isSortAdapter = true;
        isAdapter = false;
        isDateAdapter = false;

        //check if header is already shown, if shown then shw the header for this adapter also
        if (currentJobsAdapterRv != null || allJobsListAdapter != null) {
            boolean isHeaderShown;
            if (currentJobsAdapterRv != null) {
                isHeaderShown = currentJobsAdapterRv.isHeaderShown();
            } else {
                isHeaderShown = allJobsListAdapter.isHeaderShown();
            }
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
     * Sets empty view for recycler view.
     *
     * @param arrayList
     */
    private void setEmptyViewForRv(ArrayList<HashMap<String, String>> arrayList) {
        if (arrayList.size() > 0) {
            rvCurrentJobs.setVisibility(View.VISIBLE);
            txtEmptyList.setVisibility(View.GONE);
        } else {
            rvCurrentJobs.setVisibility(View.GONE);
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

    @Override
    public void initiateView(View v) {
        pbCurrentJobs = (ProgressBar) v
                .findViewById(R.id.pb_current_jobs);
        rvCurrentJobs = (RecyclerView) v.findViewById(R.id.rv_current_jobs);
        linearLayoutManager = new LinearLayoutManager(syncroTeamBaseActivity);

        txtEmptyList = (android.widget.TextView) v.findViewById(R.id.empty_text);
        linSearchSort = (LinearLayout) v.findViewById(R.id.lin_search_sort);
        RelativeLayout relSort = (RelativeLayout) v.findViewById(R.id.rel_sort);
        txtSortBy = (com.synchroteam.TypefaceLibrary.TextView) v.findViewById(R.id.txt_sort_by);
        txtIcSort = (android.widget.TextView) v.findViewById(R.id.ic_sort);
        txtIcSearch = (android.widget.TextView) v.findViewById(R.id.ic_search);
        txtIcBarcode = (android.widget.TextView) v.findViewById(R.id.ic_barcode);
        edSearchJobs = (EditText) v.findViewById(R.id.ed_search_all_jobs);

        fragmentManager = syncroTeamBaseActivity.getSupportFragmentManager();
        dialogSorting = AllJobsSortingDialog.getInstance(KEYS.AllJobSortingOptions.CURRENT_JOBS_SORTING,
                null, true, false, currentJobFragment);
        dialogSorting.setTargetFragment(baseFragment, 300);

        String[] arrSortingOptions = {syncroTeamBaseActivity.getString(R.string.txt_no_sorting), syncroTeamBaseActivity.getString(R.string.txt_date_sorting),
                syncroTeamBaseActivity.getString(R.string.txt_customer_sorting),
                syncroTeamBaseActivity.getString(R.string.txt_site_sorting),
                syncroTeamBaseActivity.getString(R.string.txt_equipment_sorting),
                syncroTeamBaseActivity.getString(R.string.txt_nearby_sorting),
                syncroTeamBaseActivity.getString(R.string.txt_nearby_job_sorting),
                syncroTeamBaseActivity.getString(R.string.txt_town_sorting)};
        txtSortBy.setText(arrSortingOptions[mSortingOption]);

        // currentJobsLv.setOnItemClickListener(itemClickListener);
        currentJobWeekRibbionGallary = (MyGallery) v
                .findViewById(R.id.currentJobDatePicker);

        relSort.setOnClickListener(clickListener);
        txtIcBarcode.setOnClickListener(clickListener);

        edSearchJobs.setOnFocusChangeListener(mFocusChangeListener);

        edSearchJobs.setKeyImeChangeListener(new EditText.KeyImeChange() {
            @Override
            public void onKeyIme(int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    if (edSearchJobs.getText().length() == 0) {
                        Logger.log("Keycode: " + keyCode, " Event : " + event);

                        if (isUserClickedSortByDate == 1) {
                            sortJobList(KEYS.CurrentJobsSorting.SORT_BY_DATE, syncroTeamBaseActivity.getString(R.string.txt_date_sorting), "");
                        } else if (isUserClickedSortByDate == 0) {
                            sortJobListNone(KEYS.CurrentJobsSorting.SORT_BY_NONE, syncroTeamBaseActivity.getString(R.string.txt_no_sorting), "");
                        }
                        edSearchJobs.clearFocus();
                    }
                }
            }
        });

        setTypeFaceFont();

        setLocationServices();

        locationClient.connect();

        animateFirstTime();

    }

    /**
     * Sets the date and filter list by fetching current jobs on the date
     * selected.
     *
     * @param view                   the view
     * @param positionOfDateSelected the position
     */
    protected void setDateAndFilterList(View view, int positionOfDateSelected) {

        int result = dateSelected.compareTo((Date) view.getTag());

        if (result != 0) {
            dateSelected = (Date) view.getTag();
            if (positionOfDateSelected < lowerLimitOfAllowedSelectionWeekRibbon) {

                currentJobWeekRibbionGallary
                        .setSelection(positionOfDateSelected
                                + (lowerLimitOfAllowedSelectionWeekRibbon - positionOfDateSelected));
            } else if (positionOfDateSelected > upperLimitofAllowedSelectionWeekRibbon) {

                currentJobWeekRibbionGallary
                        .setSelection(positionOfDateSelected
                                - (positionOfDateSelected - upperLimitofAllowedSelectionWeekRibbon));
            } else {
                previousPositionSelected = positionOfDateSelected;
                if (mSortingOption == KEYS.CurrentJobsSorting.SORT_BY_NEARBY) {
                    if ((locationClient != null) && (locationClient.isConnected())) {
                        geocoder();
                    } else if (locationClient != null) {
                        locationClient.connect();
                    }
                } else if (mSortingOption == KEYS.CurrentJobsSorting.SORT_BY_NEARBY_JOB) {
                    String mSortedJobNumber = SharedPref.getSortedJobNumber(syncroTeamBaseActivity);
                    HashMap<String, String> hmLatLong = dataAceesObject.checkLatLongCurrentJobs(dateSelected, user.getId(), mSortedJobNumber);
                    if (hmLatLong.size() > 0) {
                        Logger.log("TAG", "Calling the fetch current job query 2");
                        new FetchCurrentJobs(false, true).execute("" + mSortingOption, hmLatLong.get(KEYS.CurrentJobs.LAT), hmLatLong.get(KEYS.CurrentJobs.LON));
                    } else {
                        currentJobsByOtherSorting = new ArrayList<>();
                        setEmptyViewForRv(currentJobsByOtherSorting);
                        Toast.makeText(syncroTeamBaseActivity.getApplicationContext(), R.string.txt_jobno_mismatch, Toast.LENGTH_LONG).show();
                    }
                } else if (mSortingOption != KEYS.CurrentJobsSorting.SORT_BY_NEARBY) {
                    Logger.log("TAG", "Calling the fetch current job query 3");
                    new FetchCurrentJobs(false, true).execute("" + mSortingOption, "", "");
                }

                if (baseFragment.isResumed()) {
                    baseFragment.listUpdate();
                }
            }

            EventBus.getDefault().post(new EnableSynchronizationAddJobEvent());
        }

    }

    /**
     * Gets the date from db format.
     *
     * @param dateInStringToBeParsed the m date
     * @return the date from db format
     */
    public Date getDateFromDbFormat(String dateInStringToBeParsed) {
        SimpleDateFormat dateInDatabaseFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss.SSS");
        Date date;
        try {
            date = dateInDatabaseFormat.parse(dateInStringToBeParsed);
            return date;
        } catch (ParseException e) {
            Logger.printException(e);
            return new Date();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.synchroteam.fragmenthelper.HelperInterface#doOnSyncronize()
     */
    @Override
    public void doOnSyncronize() {

        Logger.log(TAG, "DO ON SYNCHRONIZE METHOD CALLED");
        setDatesOfGallary(false);

        if (mSortingOption == KEYS.CurrentJobsSorting.SORT_BY_NEARBY) {
            geocoder();
        } else if (mSortingOption == KEYS.CurrentJobsSorting.SORT_BY_NEARBY_JOB) {
            String mSortedJobNumber = SharedPref.getSortedJobNumber(syncroTeamBaseActivity);
            HashMap<String, String> hmLatLong = dataAceesObject.getLatLongAllJobs(mSortedJobNumber);
            if (hmLatLong.size() > 0) {
                Logger.log("TAG", "Calling the fetch current job query 4");
                new FetchCurrentJobs(false, true).execute("" + mSortingOption, hmLatLong.get(KEYS.CurrentJobs.LAT), hmLatLong.get(KEYS.CurrentJobs.LON));
            } else {
                Toast.makeText(syncroTeamBaseActivity.getApplicationContext(), R.string.txt_jobno_mismatch, Toast.LENGTH_LONG).show();
            }
        } else {
            resetSearchField();
            if (edSearchJobs.getText().length() == 0) {
                edSearchJobs.clearFocus();
                if (isUserClickedSortByDate == 1) {
                    Logger.log(TAG, "DO ON SYNCHRONIZE METHOD QUERY CALLED");
                    sortJobList(KEYS.CurrentJobsSorting.SORT_BY_DATE, syncroTeamBaseActivity.getString(R.string.txt_date_sorting), "");
                } else if (isUserClickedSortByDate == 0) {
                    sortJobListNone(KEYS.CurrentJobsSorting.SORT_BY_NONE, syncroTeamBaseActivity.getString(R.string.txt_no_sorting), "");
                }
            }


            try {
                if (currentJobsAdapterRv != null) {
                    currentJobsAdapterRv.setHeaderShown(false);
                    currentJobsAdapterRv.setIncrementValue(0);
                }
                QuickReturnBehaviorForCurrentJobs.mIsFirstSwipeUp = true;
                QuickReturnBehaviorForCurrentJobs.mDySinceDirectionChange = 0;
                animateFirstTime();
            } catch (Exception e) {

            }
        }

    }

    /**
     * Calls on fragment stop.
     */
    public void onStop() {
        locationClient.disconnect();
    }

    /**
     * Calls from Current jobs fragment
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
     * Hide the container for the first time after view renders.
     */
    public void animateFirstTime() {
        linSearchSort.post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    ViewPropertyAnimator animator = linSearchSort.animate()
                            .translationY(-linSearchSort.getHeight())
                            .setInterpolator(new FastOutSlowInInterpolator())
                            .setDuration(50);
                    animator.start();
                } else {
                    Animation slideUp = AnimationUtils.loadAnimation(syncroTeamBaseActivity, R.anim.slide_up);
                    linSearchSort.startAnimation(slideUp);
                }
            }
        });
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
                        currentJobFragment.callingPermissionLocation();
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
                        currentJobFragment.startActivityForResult(intent, RequestCode.REQUEST_CODE_GPS_SETTINGS);
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
     * Sort job list by selected order.
     *
     * @param options    : selected option
     * @param optionName : name of sorting
     * @param jobNumber  : job number (for near by job option)
     */
    public void sortJobList(int options, String optionName, String jobNumber) {

        boolean isUpdateQuery = true;
        mSortingName = optionName;
        mSortingOption = options;

        if (mSortingOption == KEYS.CurrentJobsSorting.SORT_BY_NONE) {
            if (currentJobsAdapterRv != null) {
                currentJobsAdapterRv.setHeaderShown(false);
                currentJobsAdapterRv.setIncrementValue(0);
            }
            QuickReturnBehaviorForCurrentJobs.mIsFirstSwipeUp = true;
            QuickReturnBehaviorForCurrentJobs.mDySinceDirectionChange = 0;
            animateFirstTime();

            txtSortBy.setText(optionName);

            edSearchJobs.clearFocus();

            Logger.log("TAG", "Calling the fetch current job query 5");
            new FetchCurrentJobs(false, isUpdateQuery).execute("" + mSortingOption, "", "");

        } else if (mSortingOption == KEYS.CurrentJobsSorting.SORT_BY_NEARBY) {
            if ((locationClient != null) && (locationClient.isConnected())) {
                geocoder();
            } else if (locationClient != null) {
                locationClient.connect();
            }
        } else if (mSortingOption == KEYS.CurrentJobsSorting.SORT_BY_NEARBY_JOB) {
            txtSortBy.setText(optionName);
            HashMap<String, String> hmLatLong = dataAceesObject.checkLatLongCurrentJobs(dateSelected, user.getId(), jobNumber);
            Logger.log("TAG", "Calling the fetch current job query 6");
            new FetchCurrentJobs(false, isUpdateQuery).execute("" + mSortingOption, hmLatLong.get(KEYS.CurrentJobs.LAT), hmLatLong.get(KEYS.CurrentJobs.LON));
        } else {
            txtSortBy.setText(optionName);
            Logger.log("TAG", "Calling the fetch current job query 7");
            new FetchCurrentJobs(false, isUpdateQuery).execute("" + mSortingOption, "", "");
        }

        SharedPref.setSortingOption(options, syncroTeamBaseActivity);

    }

    /**
     * Sort jobs list at none category with no animation
     *
     * @param options
     * @param optionName
     * @param jobNumber
     */
    public void sortJobListNone(int options, String optionName, String jobNumber) {
        mSortingName = optionName;
        mSortingOption = options;

        if (mSortingOption == KEYS.CurrentJobsSorting.SORT_BY_NONE) {
            if (currentJobsAdapterRv != null) {
                currentJobsAdapterRv.setHeaderShown(true);
                currentJobsAdapterRv.setIncrementValue(1);
            }

            QuickReturnBehaviorForCurrentJobs.mIsFirstSwipeUp = true;
            QuickReturnBehaviorForCurrentJobs.mDySinceDirectionChange = 0;

//            animateFirstTime();

            txtSortBy.setText(optionName);

            edSearchJobs.clearFocus();
            Logger.log("TAG", "Calling the fetch current job query 8");
            new FetchCurrentJobs(false, false).execute("" + mSortingOption, "", "");


        }

        SharedPref.setSortingOption(options, syncroTeamBaseActivity);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.synchroteam.fragmenthelper.HelperInterface#onReturnToActivity(int)
     */

    /**
     * Refresh The List when any change in database are made.
     */
    public void refreshList() {

        Logger.log("Current Job Fragment Helper", "REFRESHING LIST CALLED");


        setDatesOfGallary(false);

        if (mSortingOption == KEYS.CurrentJobsSorting.SORT_BY_NEARBY_JOB) {
            String mSortedJobNumber = SharedPref.getSortedJobNumber(syncroTeamBaseActivity);
            HashMap<String, String> hmLatLong = dataAceesObject.checkLatLongCurrentJobs(dateSelected, user.getId(), mSortedJobNumber);
            if (hmLatLong.size() > 0) {
                Logger.log("TAG", "Calling the fetch current job query 9");
                new FetchCurrentJobs(false, true).execute("" + mSortingOption, hmLatLong.get(KEYS.CurrentJobs.LAT), hmLatLong.get(KEYS.CurrentJobs.LON));
            } else {
                Toast.makeText(syncroTeamBaseActivity.getApplicationContext(), R.string.txt_jobno_mismatch, Toast.LENGTH_LONG).show();
            }
        } else if (mSortingOption != KEYS.CurrentJobsSorting.SORT_BY_NEARBY) {

            resetSearchField();

            //make changes here
            if (edSearchJobs.getText().length() == 0) {
                edSearchJobs.clearFocus();
                if (isUserClickedSortByDate == 1) {
                    sortJobList(KEYS.CurrentJobsSorting.SORT_BY_DATE,
                            syncroTeamBaseActivity.getString(R.string.txt_date_sorting), "");
                } else if (isUserClickedSortByDate == 0) {
                    sortJobListNone(KEYS.CurrentJobsSorting.SORT_BY_NONE,
                            syncroTeamBaseActivity.getString(R.string.txt_no_sorting), "");
                }
            }

            try {
                if (currentJobsAdapterRv != null) {
                    currentJobsAdapterRv.setHeaderShown(false);
                    currentJobsAdapterRv.setIncrementValue(0);
                }
                QuickReturnBehaviorForCurrentJobs.mIsFirstSwipeUp = true;
                QuickReturnBehaviorForCurrentJobs.mDySinceDirectionChange = 0;
                animateFirstTime();
            } catch (Exception e) {

            }

//            linSearchSort.setVisibility(View.VISIBLE);
        }

        baseFragment.listUpdate();

    }

    public void onEvent(RefreshListEvent event) {
        if (syncroTeamBaseActivity != null)
            syncroTeamBaseActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (currentJobsAdapterRv != null)
                        currentJobsAdapterRv.notifyDataSetChanged();
                }
            });
    }


    /**
     * Perform following action when this fragment is resumed. 1.Check the date
     * and time format of device and change(Manually or by changing locale) the
     * value of dateFormatCurrentJobList,timeFormatCurrentJobList formats so
     * that while updating Ui the format of date and time on current job list
     * get updated.
     */
    public void doOnResume() {

        String dateFormatString = System
                .getString(syncroTeamBaseActivity.getContentResolver(),
                        System.DATE_FORMAT);
        String timeFormatString = System.getString(
                syncroTeamBaseActivity.getContentResolver(), System.TIME_12_24);

        dateFormatUnavabilities = new SimpleDateFormat("dd-MMM-yyyy");


        if (!(TextUtils.isEmpty(dateFormatString))
                && (!TextUtils.isEmpty(timeFormatString))) {
            if (!dateFormatString.equals(SharedPref
                    .getDateFormat(syncroTeamBaseActivity))) {

                dateFormatCurrentJobList = DateFormat
                        .getDateInstance(DateFormat.LONG);
                // dateFormatUnavabilities = DateFormat
                // .getDateInstance(DateFormat.MEDIUM);

                // dateFormatUnavabilities = new
                // SimpleDateFormat("MMMM dd, yyyy");
                timeFormatCurrentJobList = android.text.format.DateFormat
                        .getTimeFormat(syncroTeamBaseActivity);

                SharedPref.setDateFormat(syncroTeamBaseActivity);
                SharedPref.setTimeFormat(syncroTeamBaseActivity);
                EventBus.getDefault().post(new UpdateDataBaseEvent());

            } else if (!timeFormatString.equals(SharedPref
                    .getTimeFormat(syncroTeamBaseActivity))) {
                dateFormatCurrentJobList = DateFormat
                        .getDateInstance(DateFormat.LONG);
                // dateFormatUnavabilities = DateFormat
                // .getDateInstance(DateFormat.MEDIUM);
                // dateFormatUnavabilities = new
                // SimpleDateFormat("MMMM dd, yyyy");
                timeFormatCurrentJobList = android.text.format.DateFormat
                        .getTimeFormat(syncroTeamBaseActivity);

                SharedPref.setDateFormat(syncroTeamBaseActivity);
                SharedPref.setTimeFormat(syncroTeamBaseActivity);
                EventBus.getDefault().post(new UpdateDataBaseEvent());

            }

        }

        Calendar calendar = Calendar.getInstance();
        if (!DateFormatUtils.isSameDay(todayDate,  calendar.getTime())) {
            todayDate = calendar.getTime();
            dateSelected = todayDate;
            refreshList();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        isLocationClientConnected = true;
        if (mSortingOption == KEYS.CurrentJobsSorting.SORT_BY_NEARBY) {
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

    @Override
    public void onEmptyList(ArrayList<HashMap<String, String>> list) {
        setEmptyViewForRv(list);
    }

    /**
     * V 1.1 The Class GallaryAdapter inflates the data of
     * currentJobWeekRibbionGallary and change the background color of date
     * representing todays date.
     */
    @SuppressLint("DefaultLocale")
    private class GallaryAdapter extends BaseAdapter {

        /**
         * The dates.
         */
        private ArrayList<Date> dates;

        /** The context. */

        /**
         * The simple date format.
         */
        private SimpleDateFormat simpleDateFormat;

        /**
         * The layout inflater.
         */
        private LayoutInflater layoutInflater;

        /**
         * The white color refrence.
         */
        private int whiteColorRefrence;

        /**
         * The gray color refrence.
         */
        private int grayColorRefrence;

        /**
         * The orange color refrence.
         */
        private int orangeColorRefrence;

        /**
         * The Class ViewHolder.
         */
        private class ViewHolder {

            /**
             * The day lable tv.
             */
            TextView dayLableTv;

            /**
             * The date lable tv.
             */
            TextView dateLableTv;

            /**
             * The container week ribbon date.
             */
            LinearLayout containerWeekRibbonDate;

        }

        /**
         * Instantiates a new gallary adapter.
         *
         * @param context the context
         * @param dates   the dates
         */
        public GallaryAdapter(Context context, ArrayList<Date> dates) {

            this.dates = dates;
            layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            simpleDateFormat = new SimpleDateFormat("dd/EEE");
            whiteColorRefrence = syncroTeamBaseActivity.getResources()
                    .getColor(R.color.white);
            grayColorRefrence = syncroTeamBaseActivity.getResources().getColor(
                    R.color.grayDay);
            orangeColorRefrence = syncroTeamBaseActivity.getResources()
                    .getColor(R.color.orangeStripBackgroundCurrentJobs);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount() {

            return dates.size();
        }

        /*
         * (non-Javadoc)
         *
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public Object getItem(int arg0) {
            return dates.get(arg0);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.widget.Adapter#getItemId(int)
         */
        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.widget.Adapter#getView(int, android.view.View,
         * android.view.ViewGroup)
         */
        @Override
        public View getView(int arg0, View convertView, ViewGroup arg2) {
            Date date = (Date) getItem(arg0);
            ViewHolder viewHolder;
            String dateString = simpleDateFormat.format(date);
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = layoutInflater.inflate(
                        R.layout.layout_current_job_galary_item, null);
                viewHolder.dayLableTv = (TextView) convertView
                        .findViewById(R.id.dayslableTv);
                viewHolder.dateLableTv = (TextView) convertView
                        .findViewById(R.id.datelableTv);
                viewHolder.containerWeekRibbonDate = (LinearLayout) convertView
                        .findViewById(R.id.containerWeekRibbonDate);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            int itemId = (int) getItemId(arg0);
            if ((itemId >= lowerLimitOfAllowedSelectionWeekRibbon)
                    && (itemId <= upperLimitofAllowedSelectionWeekRibbon)) {
                viewHolder.dayLableTv.setTextColor(whiteColorRefrence);
            } else {
                viewHolder.dayLableTv.setTextColor(grayColorRefrence);
            }
            if (itemId == todayDatePositionWeekRibbon) {
                viewHolder.containerWeekRibbonDate
                        .setBackgroundColor(orangeColorRefrence);

            }

            String[] lables = dateString.split("/");
            viewHolder.dayLableTv.setText(lables[1].toUpperCase() + "");
            viewHolder.dateLableTv.setText(lables[0] + "");
            convertView.setTag(date);

            convertView.setId((int) getItemId(arg0));

            return convertView;
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.synchroteam.fragmenthelper.HelperInterface#onReturnToActivity(int)
     */
    @Override
    public void onReturnToActivity(int requestCode) {

    }

    /**
     * Selts the gallry to todays date.
     */
    public void selectTodayDate() {
        currentJobWeekRibbionGallary.setSelection(todayDatePositionWeekRibbon);
    }

    /**
     * To get the system's date format.
     *
     * @param context
     * @return current date format in string
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

    private int getSortingResultForDate(CommonListBean lhsB, CommonListBean rhsB) {
        CommonJobBean lhs = (CommonJobBean) lhsB;
        CommonJobBean rhs = (CommonJobBean) rhsB;

        Logger.log(TAG, "COMPARE TO DATES==>" + lhs.getDt_deb_prev());
        Logger.log(TAG, "COMPARE TO DATES==>" + rhs.getDt_deb_prev());
        int result = 0;
        String pattern = "yyyy-mm-dd HH:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);

        try {

            int comparedResult = formatter.parse(lhs.getDt_deb_prev()).compareTo
                    (formatter.parse(rhs.getDt_deb_prev()));
            Logger.log(TAG, "COMPARE TO VALUES==>" + comparedResult);

        } catch (Exception e) {

        }

        if (result == 0) {
            String mLhsNo = "0";
            String mRhsNo = "0";
            if (!TextUtils.isEmpty(lhs.getRef_customer())) {
                mLhsNo = lhs.getRef_customer();
            } else if (lhs.getNo_interv() != 0) {
                mLhsNo = "" + lhs.getNo_interv();
            }

            if (!TextUtils.isEmpty(rhs.getRef_customer())) {
                mRhsNo = rhs.getRef_customer();
            } else if (rhs.getNo_interv() != 0) {
                mRhsNo = "" + rhs.getNo_interv();
            }
            return mLhsNo.compareToIgnoreCase(mRhsNo);
        } else {
            return result;
        }

    }

    /**
     * Compares two client name for sorting. If two client name is same, then comparison will be carried out with job numbers.
     *
     * @param lhsB
     * @param rhsB
     * @return result for comparison
     */
    private int getSortingResultforClient(CommonListBean lhsB, CommonListBean rhsB) {
        CommonJobBean lhs = (CommonJobBean) lhsB;
        CommonJobBean rhs = (CommonJobBean) rhsB;

        int result = lhs.getNom_client_interv().compareToIgnoreCase(rhs.getNom_client_interv());

        if (result == 0) {
            String mLhsNo = "0";
            String mRhsNo = "0";
            if (!TextUtils.isEmpty(lhs.getRef_customer())) {
                mLhsNo = lhs.getRef_customer();
            } else if (lhs.getNo_interv() != 0) {
                mLhsNo = "" + lhs.getNo_interv();
            }

            if (!TextUtils.isEmpty(rhs.getRef_customer())) {
                mRhsNo = rhs.getRef_customer();
            } else if (rhs.getNo_interv() != 0) {
                mRhsNo = "" + rhs.getNo_interv();
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
    private int getSortingResultforSite(CommonListBean lhsB, CommonListBean rhsB) {
        CommonJobBean lhs = (CommonJobBean) lhsB;
        CommonJobBean rhs = (CommonJobBean) rhsB;

        int result = lhs.getNom_site_interv().compareToIgnoreCase(rhs.getNom_site_interv());

        if (result == 0) {
            String mLhsNo = "0";
            String mRhsNo = "0";
            if (!TextUtils.isEmpty(lhs.getRef_customer())) {
                mLhsNo = lhs.getRef_customer();
            } else if (lhs.getNo_interv() != 0) {
                mLhsNo = "" + lhs.getNo_interv();
            }

            if (!TextUtils.isEmpty(rhs.getRef_customer())) {
                mRhsNo = rhs.getRef_customer();
            } else if (rhs.getNo_interv() != 0) {
                mRhsNo = "" + rhs.getNo_interv();
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
    private int getSortingResultforEquipment(CommonListBean lhsB, CommonListBean rhsB) {
        CommonJobBean lhs = (CommonJobBean) lhsB;
        CommonJobBean rhs = (CommonJobBean) rhsB;

        int result = lhs.getNom_equipement().compareToIgnoreCase(rhs.getNom_equipement());

        if (result == 0) {
            String mLhsNo = "0";
            String mRhsNo = "0";
            if (!TextUtils.isEmpty(lhs.getRef_customer())) {
                mLhsNo = lhs.getRef_customer();
            } else if (lhs.getNo_interv() != 0) {
                mLhsNo = "" + lhs.getNo_interv();
            }

            if (!TextUtils.isEmpty(rhs.getRef_customer())) {
                mRhsNo = rhs.getRef_customer();
            } else if (rhs.getNo_interv() != 0) {
                mRhsNo = "" + rhs.getNo_interv();
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
     * @param lhs
     * @param rhs
     * @return result for comparison
     */
    private int getSortingResultForPriority(CommonListBean lhs, CommonListBean rhs) {
        CommonJobBean lhsB = (CommonJobBean) lhs;
        CommonJobBean rhsB = (CommonJobBean) rhs;
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
    private int getSortingResultforTown(CommonListBean lhsB, CommonListBean rhsB) {
        CommonJobBean lhs = (CommonJobBean) lhsB;
        CommonJobBean rhs = (CommonJobBean) rhsB;
        int result = lhs.getAdr_interv_ville().compareToIgnoreCase(rhs.getAdr_interv_ville());

        if (result == 0) {
            String mLhsNo = "0";
            String mRhsNo = "0";
            if (!TextUtils.isEmpty(lhs.getRef_customer())) {
                mLhsNo = lhs.getRef_customer();
            } else if (lhs.getNo_interv() != 0) {
                mLhsNo = "" + lhs.getNo_interv();
            }

            if (!TextUtils.isEmpty(rhs.getRef_customer())) {
                mRhsNo = rhs.getRef_customer();
            } else if (rhs.getNo_interv() != 0) {
                mRhsNo = "" + rhs.getNo_interv();
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
    private int getSortingResultforNearby(CommonListBean lhsB, CommonListBean rhsB) {

        CommonJobBean lhs = (CommonJobBean) lhsB;
        CommonJobBean rhs = (CommonJobBean) rhsB;
        if (lhs.getLat() != null && rhs.getLat() != null && lhs.getLon() != null && rhs.getLon() != null) {
            int resultLat = lhs.getLat().compareToIgnoreCase(rhs.getLat());
            int resultLon = lhs.getLon().compareToIgnoreCase(rhs.getLon());
            if (resultLat == 0 && resultLon == 0) {
                String mLhsNo = "0";
                String mRhsNo = "0";
                if (!TextUtils.isEmpty(lhs.getRef_customer())) {
                    mLhsNo = lhs.getRef_customer();
                } else if (lhs.getNo_interv() != 0) {
                    mLhsNo = "" + lhs.getNo_interv();
                }

                if (!TextUtils.isEmpty(rhs.getRef_customer())) {
                    mRhsNo = rhs.getRef_customer();
                } else if (rhs.getNo_interv() != 0) {
                    mRhsNo = "" + rhs.getNo_interv();
                }
                return mLhsNo.compareToIgnoreCase(mRhsNo);
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }

    /***
     * Async task to fetch the deadline exceeded jobs.
     *
     * @author Ideavate.solutions
     */

    private class FetchCurrentJobs extends AsyncTaskCoroutine<String, Void> {

        private int option;
        private String mLatitude = "";
        private String mLongitude = "";
        private boolean setCurrentDate = false;
        private boolean updateQuery = false;
        ProgressDialog progress;

        public FetchCurrentJobs(boolean setCurrentDate, boolean updateQuery) {
            this.setCurrentDate = setCurrentDate;
            this.updateQuery = updateQuery;

            Logger.log(TAG, "Update query calling statuts==>" + updateQuery);
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();

            if (progress == null) {
                progress = ProgressDialog.show(syncroTeamBaseActivity,
                        syncroTeamBaseActivity.getString(R.string.textPleaseWaitLable),
                        syncroTeamBaseActivity.getString(R.string.chargement), true, false);
                progress.show();
            } else if (progress != null && !progress.isShowing()) {
                progress.show();
            }

            rvCurrentJobs.setVisibility(View.GONE);
//            pbCurrentJobs.setVisibility(View.VISIBLE);

            updateListBeforeDisplay(option);

            if (currentJobsByNoSorting != null) {
                currentJobsByNoSorting.clear();
            } else {
                currentJobsByNoSorting = new ArrayList<>();
            }

        }

        @Override
        public Void doInBackground(String... params) {
            option = Integer.parseInt(params[0]);
            mLatitude = params[1];
            mLongitude = params[2];

   //         getTodayJobsActivityList(dateSelected, user.getId(),
   //                 option,mLatitude,mLongitude);

            if (option == KEYS.CurrentJobsSorting.SORT_BY_NONE) {
                getCurrentJobsWithNoSorting();
            } else if (option == KEYS.CurrentJobsSorting.SORT_BY_DATE) {
                getCurrentJobsByDate();
            } else {
                getCurrentJobsBySorting(mSortingOption, mLatitude, mLongitude);
            }

            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            super.onPostExecute(result);

//            pbCurrentJobs.setVisibility(View.GONE);
            rvCurrentJobs.setVisibility(View.VISIBLE);

            if (progress != null && progress.isShowing())
                progress.dismiss();

            Logger.log("TAG", "Finished the fetch current job query");

            if (mSortingOption == KEYS.CurrentJobsSorting.SORT_BY_NONE) {
                setCurrentJobAdapter();
            } else if (mSortingOption == KEYS.CurrentJobsSorting.SORT_BY_DATE) {
                setCurrentJobByDateAdapter();
            } else {
                setCurrentJobBySortingAdapter();
            }
            rvCurrentJobs.addOnScrollListener(mScrollListener);
            if (setCurrentDate) {
                selectTodayDate();
            }

            EventBus.getDefault().post(new EnableSynchronizationAddJobEvent());

        }

    }

    private void updateListBeforeDisplay(int option) {

//        if (currentJobList != null) {
//            currentJobList.clear();
//        } else {
//            currentJobList = null;
//            currentJobList = new TreeMap<>(
//                    new Comparator<String>() {
//
//                        @Override
//                        public int compare(String lhs, String rhs) {
//                            return lhs.compareTo(rhs);
//                        }
//                    });
//        }
//        if (currentJobsByNoSorting != null) {
//            currentJobsByNoSorting.clear();
//        } else {
//            currentJobsByNoSorting = new ArrayList<>();
//        }
//
//        if (currentJobsByDateSorting != null) {
//            currentJobsByDateSorting.clear();
//        } else {
//            currentJobsByDateSorting = new ArrayList<>();
//        }
//
//        if (currentJobsByOtherSorting != null) {
//            currentJobsByOtherSorting.clear();
//        } else {
//            currentJobsByOtherSorting = new ArrayList<>();
//        }

        if (option == KEYS.CurrentJobsSorting.SORT_BY_NONE) {
            if (currentJobList != null) {
                currentJobList.clear();
            } else {
                currentJobList = null;
                currentJobList = new TreeMap<>(
                        new Comparator<String>() {

                            @Override
                            public int compare(String lhs, String rhs) {
                                return lhs.compareTo(rhs);
                            }
                        });
            }
            if (currentJobsByNoSorting != null) {
                currentJobsByNoSorting.clear();
            } else {
                currentJobsByNoSorting = new ArrayList<>();
            }

        } else if (option == KEYS.CurrentJobsSorting.SORT_BY_DATE) {

            if (currentJobList != null) {
                currentJobList.clear();
                currentJobsAdapterRv.notifyDataSetChanged();
            } else {
                currentJobList = null;
                currentJobList = new TreeMap<>(
                        new Comparator<String>() {

                            @Override
                            public int compare(String lhs, String rhs) {
                                return lhs.compareTo(rhs);
                            }
                        });
            }

            if (currentJobsByDateSorting != null) {
                currentJobsByDateSorting.clear();
            } else {
                currentJobsByDateSorting = new ArrayList<>();
            }

        } else {

            if (currentJobsByOtherSorting != null) {
                currentJobsByOtherSorting.clear();
            } else {
                currentJobsByOtherSorting = new ArrayList<>();
            }
        }

    }


    /**
     * Method for getting the todays job list and activity list
     *
     * @param dateSelected
     * @param id
     * @param sortByValue
     * @param mLatitude
     * @param mLongitude
     */
    private void getTodayJobsActivityList(Date dateSelected, int id, int sortByValue,
                                          String mLatitude, String mLongitude) {

        allTodaysJobList = new ArrayList<>();

        todaysJobList = dataAceesObject.getTodaysJobList(dateSelected, id);
        if (sortByValue == 0) {
            todaysActivityList = dataAceesObject.getTodaysActivityList(dateSelected);
        }

        allTodaysJobList.addAll(todaysJobList);
        allTodaysJobList.addAll(todaysActivityList);

        int numberOfJob = allTodaysJobList.size();

        //sorting the header date
        Collections.sort(allTodaysJobList, new Comparator<CommonListBean>() {

            @Override
            public int compare(CommonListBean lhs, CommonListBean rhs) {
                return lhs.getHeaderDate().compareTo(rhs.getHeaderDate());
            }
        });


        CurrentJobDataBean todaysJobBean = null;
        ArrayList<CommonListBean> commonListBeanList = new ArrayList<>();


        if (mSortingOption == KEYS.CurrentJobsSorting.SORT_BY_NONE) {

            todaysJobBean = new CurrentJobDataBean(allTodaysJobList, numberOfJob);

        } else if (mSortingOption == KEYS.CurrentJobsSorting.SORT_BY_NEARBY_JOB ||
                mSortingOption == KEYS.CurrentJobsSorting.SORT_BY_NEARBY) {

            //todo logic
            todaysJobBean = dataAceesObject.getTodaysJobSortNearBy(dateSelected, id,
                    mLatitude, mLongitude);

        } else {

            //include only today job without activity
            todaysJobBean = new CurrentJobDataBean(
                    todaysJobList, numberOfJob);

            //performing sorting
            Collections.sort(commonListBeanList, new Comparator<CommonListBean>() {
                @Override
                public int compare(CommonListBean lhs, CommonListBean rhs) {
                    switch (sortByValue) {
                        case KEYS.CurrentJobsSorting.SORT_BY_CLIENT:
                            return getSortingResultforClient(lhs, rhs);
                        case KEYS.CurrentJobsSorting.SORT_BY_SITE:
                            return getSortingResultforSite(lhs, rhs);
                        case KEYS.CurrentJobsSorting.SORT_BY_EQUIPMENT:
                            return getSortingResultforEquipment(lhs, rhs);
                        case KEYS.CurrentJobsSorting.SORT_BY_TOWN:
                            return getSortingResultforTown(lhs, rhs);
                        case KEYS.CurrentJobsSorting.SORT_BY_PRIORITY:
                            return getSortingResultForPriority(lhs, rhs);
                        default:
                            return getSortingResultforClient(lhs, rhs);
                    }
                }
            });

        }


        commonListBeanList = todaysJobBean.getCommonJobDataBean();
        jobCount = todaysJobBean.getCurrrntJobCount();

        addValuesToHashMap(commonListBeanList, sortByValue);


    }

    public Conge checkUnAvailabilityStarted() {
        Conge indisp;
        Vector<Conge> vectConge = new Vector<Conge>();
        vectConge = dataAceesObject.getCongeNewExceptClockInActivity();
        Enumeration<Conge> enindisp = vectConge.elements();
        while (enindisp.hasMoreElements()) {
            indisp = enindisp.nextElement();
            if (indisp.getDtFin() == null) {
                return indisp;
            }
        }
        return null;
    }

    public Conge check() {
        Conge indisp;
        Vector<Conge> vectConge = new Vector<Conge>();
        vectConge = dataAceesObject.getConge();
        Enumeration<Conge> enindisp = vectConge.elements();
        while (enindisp.hasMoreElements()) {
            indisp = enindisp.nextElement();
            if (indisp.getDtFin() == null) {
                return indisp;
            }
        }
        return null;
    }


    // ******ONCLICK...LISTENER...ENDS...HERE*********

    // *****************************************LISTENER...ENDS...HERE*******************************************************

    // ...................................INTERFACE...STARTS...HERE........................................................

    public interface RefreshListener {
        void refreshList();
    }
    // ************************************INTERFACE...ENDS...HERE**********************************************************

    //------------------------------------------LISTENER---STARTS---------------------------------------------

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
            Logger.log("TAG", "Calling the fetch current job query 10");
            new FetchCurrentJobs(false, true).execute(mSortingOption + "",
                    location.getLatitude() + "", location.getLongitude()
                            + "");

        }
    };

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            switch (v.getId()) {
                case R.id.rel_sort:
                    dialogSorting = AllJobsSortingDialog.getInstance(KEYS.AllJobSortingOptions.CURRENT_JOBS_SORTING,
                            null, true, false, currentJobFragment);
                    dialogSorting.setTargetFragment(baseFragment, 300);
                    dialogSorting.show(fragmentManager, "sorting");
                    break;
                case R.id.ic_barcode:
                    //if user tries to read the barcode while sort by none is selected. sort the list by date then do the further process.
                    if (mSortingOption == KEYS.CurrentJobsSorting.SORT_BY_NONE) {
                        sortJobList(KEYS.CurrentJobsSorting.SORT_BY_DATE,
                                syncroTeamBaseActivity.getString(R.string.txt_date_sorting), "");
                    }

                    Intent intent = new Intent(syncroTeamBaseActivity,
                            CodeScannerActivity.class);
                    currentJobFragment.startActivityForResult(intent,
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

                if (mCurrentJobDateFilter != null) {
                    mCurrentJobDateFilter.filter(s.toString());
                }
                
            } else if (isSortAdapter) {
                if (!isUserSearching) {
                    mAllJobsSortingAdapter.setIndexPosition(index);
                } else {
                    mAllJobsSortingAdapter.setIndexPosition(searchIndex);
                }

                if (mCurrentJobSortingFilter != null) {
                    mCurrentJobSortingFilter.filter(s.toString());
                }
            }

            //todo check logic later
//            else if (isAdapter) {
//                if (!isUserSearching) {
//                    currentJobsAdapterRv.setIndexPosition(index);
//                } else {
//                    currentJobsAdapterRv.setIndexPosition(searchIndex);
//                }
//
//            }

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
            if (mSortingOption == KEYS.CurrentJobsSorting.SORT_BY_NONE) {
                if (hasFocus) {
                    sortJobList(KEYS.CurrentJobsSorting.SORT_BY_DATE, syncroTeamBaseActivity.getString(R.string.txt_date_sorting), "");
                } else {
                    edSearchJobs.setHint(syncroTeamBaseActivity.getString(R.string.txt_search_hint));
                }
            } else {
                if (hasFocus) {
                    edSearchJobs.setHint("");
                } else {
                    edSearchJobs.setHint(syncroTeamBaseActivity.getString(R.string.txt_search_hint));
                }
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

    //------------------------------------------LISTENER---ENDS---------------------------------------------

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
                } else if (isSortAdapter) {
                    if (!isUserSearching) {
                        mAllJobsSortingAdapter.setIndexPosition(index);
                    } else {
                        mAllJobsSortingAdapter.setIndexPosition(searchIndex);
                    }
                    mAllJobsSortingAdapter.notifyDataSetChanged();
                } else if (isAdapter) {
                    if (!isUserSearching) {
                        currentJobsAdapterRv.setIndexPosition(index);
                    } else {
                        currentJobsAdapterRv.setIndexPosition(searchIndex);
                    }
                    currentJobsAdapterRv.notifyDataSetChanged();
                }
                isLoading = false;
            }
        }, 900);

    }


    public void addValuesToHashMap(ArrayList<CommonListBean> commonListBeanList, int sortByValue) {

        HashMap<String, String> todaysJobHashMap;

        for (int i = 0; i < commonListBeanList.size(); i++) {
            CommonListBean commonListBean = commonListBeanList.get(i);

            if (commonListBean.getIsJobBean()) {
                CommonJobBean currentJobsBean = (CommonJobBean) commonListBean;
                int jobStatus = currentJobsBean.getCd_status_interv();
                if (jobStatus != KEYS.CurrentJobs.JOB__COMPLETE) {

                    String jobNumber = currentJobsBean.getType_Interv();

                    todaysJobHashMap = new HashMap<String, String>();
                    todaysJobHashMap.put(KEYS.CurrentJobs.DISPO, "false");

                    if (!TextUtils.isEmpty(currentJobsBean.getRef_customer()))
                        jobNumber = "#" + currentJobsBean.getRef_customer()
                                + " - " + currentJobsBean.getType_Interv();
                    else if (currentJobsBean.getNo_interv() != 0)
                        jobNumber = "#" + currentJobsBean.getNo_interv()
                                + " - " + currentJobsBean.getType_Interv();

                    todaysJobHashMap.put(KEYS.CurrentJobs.ALL_JOB_HEADER,
                            currentJobsBean.getHeaderDate());
                    todaysJobHashMap.put(KEYS.CurrentJobs.IS_CURRENT_JOB,
                            KEYS.CurrentJobs.TRUE);
                    todaysJobHashMap.put(KEYS.CurrentJobs.ID,
                            currentJobsBean.getId());
                    todaysJobHashMap.put(KEYS.CurrentJobs.CD_STATUS, String
                            .valueOf(currentJobsBean.getCd_status_interv()));
                    todaysJobHashMap.put(KEYS.CurrentJobs.ID_USER,
                            String.valueOf(currentJobsBean.getIdUser()));

                    // put nom_client_interv name
                    todaysJobHashMap.put(KEYS.CurrentJobs.NOM_CLIENT_INTERV,
                            currentJobsBean.getNom_client_interv());

                    todaysJobHashMap.put(KEYS.CurrentJobs.DESC,
                            String.valueOf(currentJobsBean.getDesc()));

                    todaysJobHashMap.put(KEYS.CurrentJobs.TELCEL,
                            currentJobsBean.getTel_contact());

                    todaysJobHashMap.put(KEYS.CurrentJobs.MOBILE_CONTACT,
                            currentJobsBean.getMobileContact());

                    todaysJobHashMap.put(KEYS.CurrentJobs.ID_MODEL, String
                            .valueOf(currentJobsBean.getId_model_rapport()));
                    todaysJobHashMap.put(KEYS.CurrentJobs.TYPE, jobNumber);
                    todaysJobHashMap.put(KEYS.CurrentJobs.PRIORITY,
                            currentJobsBean.getPriorite() + "");

                    todaysJobHashMap.put(KEYS.CurrentJobs.LAT,
                            currentJobsBean.getLat());
                    todaysJobHashMap.put(KEYS.CurrentJobs.LON,
                            currentJobsBean.getLon());

                    todaysJobHashMap.put(KEYS.CurrentJobs.NOMSITE,
                            currentJobsBean.getNom_site_interv());
                    todaysJobHashMap.put(KEYS.CurrentJobs.NOMEQUIPMENT,
                            currentJobsBean.getNom_equipement());
                    todaysJobHashMap.put(KEYS.CurrentJobs.IDSITE,
                            String.valueOf(currentJobsBean.getIdSite()));
                    todaysJobHashMap.put(KEYS.CurrentJobs.IDCLIENT,
                            String.valueOf(currentJobsBean.getIdClient()));
                    todaysJobHashMap.put(KEYS.CurrentJobs.IDEQUIPMENT,
                            String.valueOf(currentJobsBean.getIdEquipement()));

                    //add custom values if option is not none
                    if (sortByValue != KEYS.CurrentJobsSorting.SORT_BY_NONE) {

                        todaysJobHashMap.put(KEYS.CurrentJobs.ADR_VILLE,
                                String.valueOf(currentJobsBean.getAdr_interv_ville()));

                        todaysJobHashMap.put(KEYS.CurrentJobs.CF_INTERVENTION, dataAceesObject.getAllCFInterv(currentJobsBean.getId()));
                        todaysJobHashMap.put(KEYS.CurrentJobs.CF_SITE, dataAceesObject.getAllCFSite(currentJobsBean.getIdSite()));
                        todaysJobHashMap.put(KEYS.CurrentJobs.CF_CLIENT, dataAceesObject.getAllCFClient(currentJobsBean.getIdClient()));
                        todaysJobHashMap.put(KEYS.CurrentJobs.CF_EQUIPMENT, dataAceesObject.getAllCFEquip(currentJobsBean.getIdEquipement()));

                    }

                    todaysJobHashMap.put(KEYS.CurrentJobs.DT_CREATED,
                            currentJobsBean.getDtCreated());

                    todaysJobHashMap.put(KEYS.CurrentJobs.DATEMEETING,
                            String.valueOf(currentJobsBean.getDt_meeting()));

                    Date date = getDateFromDbFormat(currentJobsBean
                            .getDt_deb_prev());

                    String dateToshow = null;

                    if (jobStatus == KEYS.CurrentJobs.JOB__STARTED) {

                        if (user.getId() != currentJobsBean.getIdUser()) {
                            try {
                                dateToshow = dataAceesObject
                                        .getDateWithRequiredPresettedPattern(date);
                            } catch (ParseException e) {
                                Logger.printException(e);
                            }
                            if (!TextUtils.isEmpty(dateToshow)) {

                                String[] dateTopass = dateToshow.split("/");

                                todaysJobHashMap.put(
                                        KEYS.CurrentJobs.DATE_TO_SHOW,
                                        dateTopass[0]);

                                todaysJobHashMap.put(
                                        KEYS.CurrentJobs.TIME_TO_SHOW,
                                        dateTopass[1]);
                            }
                        } else {

                            dateToshow = dataAceesObject
                                    .getJobStartTime(currentJobsBean.getId());
                            if (!TextUtils.isEmpty(dateToshow)) {

                                String[] dateTopass = dateToshow.split("/");

                                todaysJobHashMap.put(
                                        KEYS.CurrentJobs.DATE_TO_SHOW,
                                        dateTopass[0]);

                                todaysJobHashMap.put(
                                        KEYS.CurrentJobs.TIME_TO_SHOW,
                                        dateTopass[1]);
                            }

                        }

                    } else if (jobStatus == KEYS.CurrentJobs.JOB__SUSPENDED) {

                        if (user.getId() != currentJobsBean.getIdUser()) {

                            if (!TextUtils.isEmpty(dateToshow)) {

                                todaysJobHashMap.put(
                                        KEYS.CurrentJobs.DATE_TO_SHOW, "");

                                todaysJobHashMap.put(
                                        KEYS.CurrentJobs.TIME_TO_SHOW, "");
                            }
                        } else {

                            dateToshow = dataAceesObject
                                    .getJobSuspendedTime(currentJobsBean
                                            .getId());
                            if (!TextUtils.isEmpty(dateToshow)) {

                                String[] dateTopass = dateToshow.split("/");

                                todaysJobHashMap.put(
                                        KEYS.CurrentJobs.DATE_TO_SHOW,
                                        dateTopass[0]);

                                todaysJobHashMap.put(
                                        KEYS.CurrentJobs.TIME_TO_SHOW,
                                        dateTopass[1]);
                            }
                        }

                    } else {

                        todaysJobHashMap.put(KEYS.CurrentJobs.DATE_TO_SHOW,
                                dateFormatCurrentJobList.format(date) + "");

                        todaysJobHashMap.put(KEYS.CurrentJobs.TIME_TO_SHOW,
                                timeFormatCurrentJobList.format(date));
                    }


                    if (sortByValue == KEYS.CurrentJobsSorting.SORT_BY_NONE ||
                            sortByValue == KEYS.CurrentJobsSorting.SORT_BY_DATE) {
                        if (currentJobList.containsKey(todaysJobHashMap
                                .get(KEYS.CurrentJobs.ALL_JOB_HEADER))) {
                            currentJobList.get(
                                            todaysJobHashMap
                                                    .get(KEYS.CurrentJobs.ALL_JOB_HEADER))
                                    .add(todaysJobHashMap);

                            todaysJobHashMap.put(KEYS.CurrentJobs.HAS_HEADER, "false");

                            if (sortByValue == KEYS.CurrentJobsSorting.SORT_BY_NONE)
                                currentJobsByNoSorting.add(todaysJobHashMap);
                            else
                                currentJobsByDateSorting.add(todaysJobHashMap);
                        } else {
                            currentJobList.put(todaysJobHashMap
                                            .get(KEYS.CurrentJobs.ALL_JOB_HEADER),
                                    new ArrayList<HashMap<String, String>>());
                            currentJobList.get(
                                            todaysJobHashMap
                                                    .get(KEYS.CurrentJobs.ALL_JOB_HEADER))
                                    .add(todaysJobHashMap);

                            todaysJobHashMap.put(KEYS.CurrentJobs.HAS_HEADER, "true");

                            if (sortByValue == KEYS.CurrentJobsSorting.SORT_BY_NONE)
                                currentJobsByNoSorting.add(todaysJobHashMap);
                            else
                                currentJobsByDateSorting.add(todaysJobHashMap);
                        }
                    } else {
                        currentJobsByOtherSorting.add(todaysJobHashMap);
                    }

                }

            } else {
                if (sortByValue == KEYS.CurrentJobsSorting.SORT_BY_NONE) {
                    Conge indisp = (Conge) commonListBean;

                    HashMap<String, String> map = new HashMap<String, String>();

                    SimpleDateFormat sdf = new SimpleDateFormat(
                            "dd/MM/yyyy HH:mm:ss");
                    SimpleDateFormat updateFormat = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss.SSS");

                    Date dateD, dateF = null;
                    String activityStartDate = null;
                    try {
                        activityStartDate = getDateWithRequiredPresettedPattern(indisp.getDtDebut(), currentDateFormat);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    dateD = getDateFromDbFormat(indisp.getDtDebut());
                    if (indisp.getDtFin() != null) {
                        dateF = getDateFromDbFormat(indisp.getDtFin());

                        String activityEndDate = null;
                        try {
                            activityEndDate = getDateWithRequiredPresettedPattern(indisp.getDtFin(), currentDateFormat);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        map.put(KEYS.Unavabilities.PLAN_TIME_START_END,
                                activityStartDate
                                        + " - "
                                        + activityEndDate);
                        map.put(KEYS.Unavabilities.END_DATE, sdf.format(dateF));

                        map.put(KEYS.Unavabilities.PLAN_START_DATE_TIME,
                                updateFormat.format(dateD));
                        map.put(KEYS.Unavabilities.PLAN_END_DATE_TIME,
                                updateFormat.format(dateF));
                    } else {
                        map.put(KEYS.Unavabilities.PLAN_TIME_START_END,
                                activityStartDate
                                        + " - " + " ");
                        map.put(KEYS.Unavabilities.PLAN_START_DATE_TIME,
                                updateFormat.format(dateD));
                    }
                    map.put(KEYS.Unavabilities.TYPE, indisp.getNomTypeConge());
                    map.put(KEYS.Unavabilities.ID_TYPE_CONGE, "" + indisp.getIdTypeConge());
                    map.put(KEYS.Unavabilities.UNAVAILABILITY_ID,
                            indisp.getIdConge());
                    map.put(KEYS.Unavabilities.CLTVILLE, indisp.getNote());
                    map.put(KEYS.CurrentJobs.ALL_JOB_HEADER, indisp.getHeaderDate());

                    map.put(KEYS.CurrentJobs.IS_CURRENT_JOB, KEYS.CurrentJobs.FALSE);

                    map.put(KEYS.Unavabilities.IMG, indisp.getCouleurConge());

                    map.put(KEYS.Unavabilities.SELECTED_DATE,
                            sdf.format(dateSelected));

                    map.put(KEYS.Unavabilities.ID_USER, indisp.getIdUser());

                    map.put(KEYS.Unavabilities.ID_GROUPE, indisp.getIdGroupe());

                    map.put(KEYS.Unavabilities.FL_PAYABLE, String.valueOf(indisp.getFlPayable()));

                    map.put(KEYS.Unavabilities.FL_UNAVAILABLE, String.valueOf(indisp.getFlUnavailable()));

                    if (currentJobList.containsKey(map
                            .get(KEYS.CurrentJobs.ALL_JOB_HEADER))) {
                        currentJobList
                                .get(map.get(KEYS.CurrentJobs.ALL_JOB_HEADER)).add(
                                        map);
                        map.put(KEYS.CurrentJobs.HAS_HEADER, "false");

                        //add only the activities which have no end date or end before the selected date
                        if (dateF != null) {

                            SimpleDateFormat dateAlone = new SimpleDateFormat(
                                    "dd/MM/yyyy");
                            String startingDate = dateAlone.format(dateF);
                            String endDate = dateAlone.format(dateSelected);

                            if(startingDate.compareTo(endDate)>=0) {
                                currentJobsByNoSorting.add(map);
                            }

//                            if (dateF.compareTo(dateSelected) >= 0) {
//                                currentJobsByNoSorting.add(map);
//                            }
                        } else {
                            currentJobsByNoSorting.add(map);
                        }
                    } else {
                        currentJobList.put(
                                map.get(KEYS.CurrentJobs.ALL_JOB_HEADER),
                                new ArrayList<HashMap<String, String>>());
                        currentJobList
                                .get(map.get(KEYS.CurrentJobs.ALL_JOB_HEADER)).add(
                                        map);

                        map.put(KEYS.CurrentJobs.HAS_HEADER, "true");

                        //add only the activities which have no end date or end before the selected date
                        if (dateF != null) {

                            SimpleDateFormat dateAlone = new SimpleDateFormat(
                                    "dd/MM/yyyy");
                            String startingDate = dateAlone.format(dateF);
                            String endDate = dateAlone.format(dateSelected);

                            if(startingDate.compareTo(endDate)>=0) {
                                currentJobsByNoSorting.add(map);
                            }

//                            if (dateF.compareTo(dateSelected) >= 0) {
//                                currentJobsByNoSorting.add(map);
//                            }
                        } else {
                            currentJobsByNoSorting.add(map);
                        }
                    }

                }
            }

        }

    }


}
