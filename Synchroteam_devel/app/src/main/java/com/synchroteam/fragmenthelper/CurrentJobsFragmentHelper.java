package com.synchroteam.fragmenthelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.baseactivity.SyncroTeamBaseActivity;
import com.synchroteam.beans.CommonJobBean;
import com.synchroteam.beans.CommonListBean;
import com.synchroteam.beans.Conge;
import com.synchroteam.beans.CurrentJobDataBean;
import com.synchroteam.beans.EnableSynchronizationAddJobEvent;
import com.synchroteam.beans.UpdateDataBaseEvent;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.fragment.BaseFragment;
import com.synchroteam.listadapters.CurrentJobsListAdapter;
import com.synchroteam.synchroteam.CreateUnavailability;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.MyGallery;
import com.synchroteam.utils.SharedPref;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.Vector;

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
public class CurrentJobsFragmentHelper implements HelperInterface,
        OnClickListener {

    /**
     * The syncro team base activity.
     */
    SyncroTeamBaseActivity syncroTeamBaseActivity;

    /** The saturday date tv. */

    /**
     * The current jobs lv.
     */
    ListView currentJobsLv;

    /**
     * The data acees object.
     */
    private Dao dataAceesObject;

    /**
     * The current date.
     */
    private Date todayDate;

    /**
     * The date selected.
     */
    private Date dateSelected;

    /** The filtered array list. */
    // private ArrayList<HashMap<String, String>> filteredArrayList;

    /**
     * The current job list.
     */
    private TreeMap<String, ArrayList<HashMap<String, String>>> currentJobList;

    /**
     * The current jobs list adapter.
     */
    private CurrentJobsListAdapter currentJobsListAdapter;

    /**
     * The view.
     */
    private View view;

    /**
     * The base fragment.
     */
    private BaseFragment baseFragment;

    /**
     * The current job date picker.
     */
    @SuppressWarnings("deprecation")
    private MyGallery currentJobWeekRibbionGallary;

    /**
     * The selected position.
     */
    private int todayDatePositionWeekRibbon;

    /**
     * The lower limit.
     */
    private int lowerLimitOfAllowedSelectionWeekRibbon;

    /**
     * The upper limit.
     */
    private int upperLimitofAllowedSelectionWeekRibbon;

    /**
     * The dates.
     */
    private ArrayList<Date> datesToBeShownOnGallary;

    /**
     * The gallary adapter.
     */
    private GallaryAdapter gallaryAdapterWeekRibbon;

    /**
     * The previous position selected.
     */
    private int previousPositionSelected;

    /**
     * The date format.
     */
    private DateFormat dateFormatCurrentJobList;

    // private DateFormat dateFormatUnavabilities;
    private SimpleDateFormat dateFormatUnavabilities;
    /**
     * The format.
     */
    private Format timeFormatCurrentJobList;

    /**
     * The user.
     */
    private User user;

    /**
     * The job count.
     */
    private int jobCount;

    private boolean isItemClicked = false;

    private TextView txtStartActivity;

    private View footerView;

    private static final String dateFormat1 = "mm/dd/yyyy";
    private static final String dateFormat2 = "dd/mm/yyyy";
    private static final String dateFormat3 = "yyyy/mm/dd";

    private String currentDateFormat = "yyyy-MM-dd HH:mm:ss";

    private static final String TAG = "CurrentJobsFragmentHelper";

    /**
     * Instantiates a new current jobs fragment helper.
     */


    /**
     * The dateFormatWeekRibbon.
     */
    private DateFormat dateFormatWeekRibbon;

    /**
     * Instantiates a new current jobs fragment helper.
     *
     * @param syncroTeamBaseActivity the syncro team base activity
     * @param baseFragment           the base fragment
     */
    public CurrentJobsFragmentHelper(
            SyncroTeamBaseActivity syncroTeamBaseActivity,
            BaseFragment baseFragment) {

        this.syncroTeamBaseActivity = syncroTeamBaseActivity;
        dataAceesObject = DaoManager.getInstance();
        this.baseFragment = baseFragment;

        dateFormatWeekRibbon = new SimpleDateFormat("yyyy-MM-dd");
        user = dataAceesObject.getUser();

    }

    public CurrentJobsFragmentHelper() {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.synchroteam.fragmenthelper.HelperInterface#inflateLayout(android.
     * view.LayoutInflater, android.view.ViewGroup)
     */
    @Override
    public View inflateLayout(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.layout_current_jobs, container, false);

        initiateView(view);
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        todayDate = calendar.getTime();

        dateSelected = todayDate;

        dateFormatCurrentJobList = DateFormat.getDateInstance(DateFormat.LONG);
        // dateFormatUnavabilities =
        // DateFormat.getDateInstance(DateFormat.MEDIUM);

        // dateFormatUnavabilities = new SimpleDateFormat("MMM dd, yyyy");
        timeFormatCurrentJobList = android.text.format.DateFormat
                .getTimeFormat(syncroTeamBaseActivity);

        setDatesOfGallary(true);

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
            // System.out.println("requiredDate" + requiredDate);
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
                        Logger.log("CurrentJobFragmentHelper", position + "");
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
                            Logger.log("CurrentJobFragmentHelper",
                                    "on Item Selected " + arg3 + " Selected "
                                            + arg0.getSelectedItemId());
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
    private void createAdapterAndInflateDataInListView() {
        if (currentJobList != null) {
            currentJobList.clear();
        } else {
            currentJobList = null;
            currentJobList = new TreeMap<String, ArrayList<HashMap<String, String>>>(
                    new Comparator<String>() {

                        @Override
                        public int compare(String lhs, String rhs) {
                            return lhs.compareTo(rhs);
                        }
                    });
        }

        HashMap<String, String> currentJobHashMap;

        ArrayList<CommonListBean> currentJobBeanVector = new ArrayList<>();

        // currentJobBeanVector =
        // dataAceesObject.getAllInterventionNow(dateSelected,user.getId());

        CurrentJobDataBean currentJobDataBean = dataAceesObject
                .getCurrentJobs(dateSelected, user.getId());
        currentJobBeanVector = currentJobDataBean.getCommonJobDataBean();
        jobCount = currentJobDataBean.getCurrrntJobCount();

        for(int i=0;i<currentJobBeanVector.size();i++){

                CommonListBean commonListBean = currentJobBeanVector.get(i);
                if (commonListBean.getIsJobBean()) {

                    CommonJobBean currentJobsBean = (CommonJobBean) commonListBean;
                    int jobStatus = currentJobsBean.getCd_status_interv();
                    if (jobStatus != KEYS.CurrentJobs.JOB__COMPLETE) {

                        String jobNumber = currentJobsBean.getType_Interv();

                        currentJobHashMap = new HashMap<>();
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

                        // currentJobHashMap.put(KEYS.CurrentJobs.ADR_GLOBAL,
                        // currentJobsBean.getAdr_interv_globale());

                        // currentJobHashMap.put(KEYS.CurrentJobs.DATE_CREATED_REAL,
                        // currentJobsBean.getDt_deb_prev());

                        currentJobHashMap.put(KEYS.CurrentJobs.ID_MODEL, String
                                .valueOf(currentJobsBean.getId_model_rapport()));
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

                        currentJobHashMap.put(KEYS.CurrentJobs.DT_CREATED,
                                String.valueOf(currentJobsBean.getDtCreated()));

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

                        } else {
                            currentJobList.put(currentJobHashMap
                                            .get(KEYS.CurrentJobs.ALL_JOB_HEADER),
                                    new ArrayList<HashMap<String, String>>());
                            currentJobList.get(
                                    currentJobHashMap
                                            .get(KEYS.CurrentJobs.ALL_JOB_HEADER))
                                    .add(currentJobHashMap);
                        }
                    }

                } else {
                    Conge indisp = (Conge) commonListBean;

                    HashMap<String, String> map = new HashMap<String, String>();

                    SimpleDateFormat sdf = new SimpleDateFormat(
                            "dd/MM/yyyy HH:mm:ss");
                    SimpleDateFormat updateFormat = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss.SSS");

                    Date dateD, dateF;
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
//					map.put(KEYS.Unavabilities.PLAN_TIME_START_END,
//							dateFormatUnavabilities.format(dateD) + " "
//									+ timeFormatCurrentJobList.format(dateD)
//									+ " - "
//									+ dateFormatUnavabilities.format(dateF)
//									+ " "
//									+ timeFormatCurrentJobList.format(dateF));
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

                    if (currentJobList.containsKey(map
                            .get(KEYS.CurrentJobs.ALL_JOB_HEADER))) {
                        currentJobList
                                .get(map.get(KEYS.CurrentJobs.ALL_JOB_HEADER)).add(
                                map);

                    } else {
                        currentJobList.put(
                                map.get(KEYS.CurrentJobs.ALL_JOB_HEADER),
                                new ArrayList<HashMap<String, String>>());
                        currentJobList
                                .get(map.get(KEYS.CurrentJobs.ALL_JOB_HEADER)).add(
                                map);
                    }
                }

            }

//        Enumeration<CommonListBean> en = currentJobBeanVector.elements();
//        while (en.hasMoreElements()) {
//
//            CommonListBean commonListBean = en.nextElement();
//            if (commonListBean.getIsJobBean()) {
//
//                CommonJobBean currentJobsBean = (CommonJobBean) commonListBean;
//                int jobStatus = currentJobsBean.getCd_status_interv();
//                if (jobStatus != KEYS.CurrentJobs.JOB__COMPLETE) {
//
//                    String jobNumber = currentJobsBean.getType_Interv();
//
//                    currentJobHashMap = new HashMap<>();
//                    currentJobHashMap.put(KEYS.CurrentJobs.DISPO, "false");
//
//                    if (!TextUtils.isEmpty(currentJobsBean.getRef_customer()))
//                        jobNumber = "#" + currentJobsBean.getRef_customer()
//                                + " - " + currentJobsBean.getType_Interv();
//                    else if (currentJobsBean.getNo_interv() != 0)
//                        jobNumber = "#" + currentJobsBean.getNo_interv()
//                                + " - " + currentJobsBean.getType_Interv();
//
//                    currentJobHashMap.put(KEYS.CurrentJobs.ALL_JOB_HEADER,
//                            currentJobsBean.getHeaderDate());
//                    currentJobHashMap.put(KEYS.CurrentJobs.IS_CURRENT_JOB,
//                            KEYS.CurrentJobs.TRUE);
//                    currentJobHashMap.put(KEYS.CurrentJobs.ID,
//                            currentJobsBean.getId());
//                    currentJobHashMap.put(KEYS.CurrentJobs.CD_STATUS, String
//                            .valueOf(currentJobsBean.getCd_status_interv()));
//                    currentJobHashMap.put(KEYS.CurrentJobs.ID_USER,
//                            String.valueOf(currentJobsBean.getIdUser()));
//
//                    // put nom_client_interv name
//                    currentJobHashMap.put(KEYS.CurrentJobs.NOM_CLIENT_INTERV,
//                            currentJobsBean.getNom_client_interv());
//
//                    // currentJobHashMap.put(KEYS.CurrentJobs.ADR_GLOBAL,
//                    // currentJobsBean.getAdr_interv_globale());
//
//                    // currentJobHashMap.put(KEYS.CurrentJobs.DATE_CREATED_REAL,
//                    // currentJobsBean.getDt_deb_prev());
//
//                    currentJobHashMap.put(KEYS.CurrentJobs.ID_MODEL, String
//                            .valueOf(currentJobsBean.getId_model_rapport()));
//                    currentJobHashMap.put(KEYS.CurrentJobs.TYPE, jobNumber);
//                    currentJobHashMap.put(KEYS.CurrentJobs.PRIORITY,
//                            currentJobsBean.getPriorite() + "");
//
//                    currentJobHashMap.put(KEYS.CurrentJobs.LAT,
//                            currentJobsBean.getLat());
//                    currentJobHashMap.put(KEYS.CurrentJobs.LON,
//                            currentJobsBean.getLon());
//
//                    currentJobHashMap.put(KEYS.CurrentJobs.NOMSITE,
//                            currentJobsBean.getNom_site_interv());
//                    currentJobHashMap.put(KEYS.CurrentJobs.NOMEQUIPMENT,
//                            currentJobsBean.getNom_equipement());
//                    currentJobHashMap.put(KEYS.CurrentJobs.IDSITE,
//                            String.valueOf(currentJobsBean.getIdSite()));
//                    currentJobHashMap.put(KEYS.CurrentJobs.IDCLIENT,
//                            String.valueOf(currentJobsBean.getIdClient()));
//                    currentJobHashMap.put(KEYS.CurrentJobs.IDEQUIPMENT,
//                            String.valueOf(currentJobsBean.getIdEquipement()));
//
//                    currentJobHashMap.put(KEYS.CurrentJobs.DT_CREATED,
//                            String.valueOf(currentJobsBean.getDtCreated()));
//
//                    Date date = getDateFromDbFormat(currentJobsBean
//                            .getDt_deb_prev());
//
//                    // currentJobHashMap.put(KEYS.CurrentJobs.DT_DEBUT_PREV,
//                    // currentJobsBean.getDt_deb_prev());
//
//                    String dateToshow = null;
//
//                    if (jobStatus == KEYS.CurrentJobs.JOB__STARTED) {
//
//                        if (user.getId() != currentJobsBean.getIdUser()) {
//                            try {
//                                dateToshow = dataAceesObject
//                                        .getDateWithRequiredPresettedPattern(date);
//                            } catch (ParseException e) {
//                                Logger.printException(e);
//                            }
//                            if (!TextUtils.isEmpty(dateToshow)) {
//
//                                String[] dateTopass = dateToshow.split("/");
//
//                                currentJobHashMap.put(
//                                        KEYS.CurrentJobs.DATE_TO_SHOW,
//                                        dateTopass[0]);
//
//                                currentJobHashMap.put(
//                                        KEYS.CurrentJobs.TIME_TO_SHOW,
//                                        dateTopass[1]);
//                            }
//                        } else {
//
//                            dateToshow = dataAceesObject
//                                    .getJobStartTime(currentJobsBean.getId());
//                            if (!TextUtils.isEmpty(dateToshow)) {
//
//                                String[] dateTopass = dateToshow.split("/");
//
//                                currentJobHashMap.put(
//                                        KEYS.CurrentJobs.DATE_TO_SHOW,
//                                        dateTopass[0]);
//
//                                currentJobHashMap.put(
//                                        KEYS.CurrentJobs.TIME_TO_SHOW,
//                                        dateTopass[1]);
//                            }
//
//                        }
//
//                    } else if (jobStatus == KEYS.CurrentJobs.JOB__SUSPENDED) {
//
//                        if (user.getId() != currentJobsBean.getIdUser()) {
//
//                            if (!TextUtils.isEmpty(dateToshow)) {
//
//                                currentJobHashMap.put(
//                                        KEYS.CurrentJobs.DATE_TO_SHOW, "");
//
//                                currentJobHashMap.put(
//                                        KEYS.CurrentJobs.TIME_TO_SHOW, "");
//                            }
//                        } else {
//
//                            dateToshow = dataAceesObject
//                                    .getJobSuspendedTime(currentJobsBean
//                                            .getId());
//                            if (!TextUtils.isEmpty(dateToshow)) {
//
//                                String[] dateTopass = dateToshow.split("/");
//
//                                currentJobHashMap.put(
//                                        KEYS.CurrentJobs.DATE_TO_SHOW,
//                                        dateTopass[0]);
//
//                                currentJobHashMap.put(
//                                        KEYS.CurrentJobs.TIME_TO_SHOW,
//                                        dateTopass[1]);
//                            }
//                        }
//
//                    } else {
//
//                        currentJobHashMap.put(KEYS.CurrentJobs.DATE_TO_SHOW,
//                                dateFormatCurrentJobList.format(date) + "");
//
//                        currentJobHashMap.put(KEYS.CurrentJobs.TIME_TO_SHOW,
//                                timeFormatCurrentJobList.format(date));
//                    }
//
//                    if (currentJobList.containsKey(currentJobHashMap
//                            .get(KEYS.CurrentJobs.ALL_JOB_HEADER))) {
//                        currentJobList.get(
//                                currentJobHashMap
//                                        .get(KEYS.CurrentJobs.ALL_JOB_HEADER))
//                                .add(currentJobHashMap);
//
//                    } else {
//                        currentJobList.put(currentJobHashMap
//                                        .get(KEYS.CurrentJobs.ALL_JOB_HEADER),
//                                new ArrayList<HashMap<String, String>>());
//                        currentJobList.get(
//                                currentJobHashMap
//                                        .get(KEYS.CurrentJobs.ALL_JOB_HEADER))
//                                .add(currentJobHashMap);
//                    }
//                }
//
//            } else {
//                Conge indisp = (Conge) commonListBean;
//
//                HashMap<String, String> map = new HashMap<String, String>();
//
//                SimpleDateFormat sdf = new SimpleDateFormat(
//                        "dd/MM/yyyy HH:mm:ss");
//                SimpleDateFormat updateFormat = new SimpleDateFormat(
//                        "yyyy-MM-dd HH:mm:ss.SSS");
//
//                Date dateD, dateF;
//                String activityStartDate = null;
//                try {
//                    activityStartDate = getDateWithRequiredPresettedPattern(indisp.getDtDebut(), currentDateFormat);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//
//                dateD = getDateFromDbFormat(indisp.getDtDebut());
//                if (indisp.getDtFin() != null) {
//                    dateF = getDateFromDbFormat(indisp.getDtFin());
//
//                    String activityEndDate = null;
//                    try {
//                        activityEndDate = getDateWithRequiredPresettedPattern(indisp.getDtFin(), currentDateFormat);
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
////					map.put(KEYS.Unavabilities.PLAN_TIME_START_END,
////							dateFormatUnavabilities.format(dateD) + " "
////									+ timeFormatCurrentJobList.format(dateD)
////									+ " - "
////									+ dateFormatUnavabilities.format(dateF)
////									+ " "
////									+ timeFormatCurrentJobList.format(dateF));
//                    map.put(KEYS.Unavabilities.PLAN_TIME_START_END,
//                            activityStartDate
//                                    + " - "
//                                    + activityEndDate);
//                    map.put(KEYS.Unavabilities.END_DATE, sdf.format(dateF));
//
//                    map.put(KEYS.Unavabilities.PLAN_START_DATE_TIME,
//                            updateFormat.format(dateD));
//                    map.put(KEYS.Unavabilities.PLAN_END_DATE_TIME,
//                            updateFormat.format(dateF));
//                } else {
//                    map.put(KEYS.Unavabilities.PLAN_TIME_START_END,
//                            activityStartDate
//                                    + " - " + " ");
//                    map.put(KEYS.Unavabilities.PLAN_START_DATE_TIME,
//                            updateFormat.format(dateD));
//                }
//                map.put(KEYS.Unavabilities.TYPE, indisp.getNomTypeConge());
//
//                map.put(KEYS.Unavabilities.UNAVAILABILITY_ID,
//                        indisp.getIdConge());
//                map.put(KEYS.Unavabilities.CLTVILLE, indisp.getNote());
//                map.put(KEYS.CurrentJobs.ALL_JOB_HEADER, indisp.getHeaderDate());
//
//                map.put(KEYS.CurrentJobs.IS_CURRENT_JOB, KEYS.CurrentJobs.FALSE);
//
//                map.put(KEYS.Unavabilities.IMG, indisp.getCouleurConge());
//
//                map.put(KEYS.Unavabilities.SELECTED_DATE,
//                        sdf.format(dateSelected));
//
//                map.put(KEYS.Unavabilities.ID_USER, indisp.getIdUser());
//
//                map.put(KEYS.Unavabilities.ID_GROUPE, indisp.getIdGroupe());
//
//                if (currentJobList.containsKey(map
//                        .get(KEYS.CurrentJobs.ALL_JOB_HEADER))) {
//                    currentJobList
//                            .get(map.get(KEYS.CurrentJobs.ALL_JOB_HEADER)).add(
//                            map);
//
//                } else {
//                    currentJobList.put(
//                            map.get(KEYS.CurrentJobs.ALL_JOB_HEADER),
//                            new ArrayList<HashMap<String, String>>());
//                    currentJobList
//                            .get(map.get(KEYS.CurrentJobs.ALL_JOB_HEADER)).add(
//                            map);
//                }
//            }
//
//        }

        setCurrentJobAdapter();

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

        if (currentJobList.isEmpty()) {
            view.findViewById(R.id.dateDetailContainer)
                    .setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.dateDetailContainer).setVisibility(
                    View.VISIBLE);
        }

        if (currentJobsListAdapter == null) {
            currentJobsListAdapter = new CurrentJobsListAdapter(
                    syncroTeamBaseActivity, currentJobList, baseFragment, this);
            currentJobsLv.addFooterView(footerView);
            currentJobsLv.setAdapter(currentJobsListAdapter);

        } else {
            currentJobsListAdapter.notifyDataSetChanged();
        }

    }

	/*
     * (non-Javadoc)
	 * 
	 * @see
	 * com.synchroteam.fragmenthelper.HelperInterface#initiateView(android.view
	 * .View)
	 */

    @Override
    public void initiateView(View v) {

        currentJobsLv = (ListView) v.findViewById(R.id.currentJobLv);
        txtStartActivity = (TextView) v.findViewById(R.id.txtStartActivity);
        footerView = ((LayoutInflater) syncroTeamBaseActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.empty_view_item, null, false);

        txtStartActivity.setOnClickListener(this);
        // currentJobsLv.setOnItemClickListener(itemClickListener);
        currentJobWeekRibbionGallary = (MyGallery) v
                .findViewById(R.id.currentJobDatePicker);

    }

    /** The item click listener. */
	/*
	 * OnItemClickListener itemClickListener = new OnItemClickListener() {
	 * 
	 * @Override public void onItemClick(AdapterView<?> arg0, View arg1, int
	 * arg2, long arg3) {
	 * 
	 *  Intent jobIntent = new Intent();
	 * jobIntent.setClass(syncroTeamBaseActivity, JobDetails.class);
	 * syncroTeamBaseActivity.startActivity(jobIntent);
	 * 
	 * } };
	 */

    /**
     * Sets the date and filter list by fetching current jobs on the date
     * selected.
     *
     * @param view                   the view
     * @param positionOfDateSelected the position
     */
    protected void setDateAndFilterList(View view, int positionOfDateSelected) {

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

            createAdapterAndInflateDataInListView();

            if (baseFragment.isResumed()) {
                baseFragment.listUpdate();
            }

        }

        EventBus.getDefault().post(new EnableSynchronizationAddJobEvent());
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
        setDatesOfGallary(false);

        createAdapterAndInflateDataInListView();

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

        Logger.log("Current Job Fragment Helper", "updateDatabase");
        setDatesOfGallary(false);

        createAdapterAndInflateDataInListView();
        baseFragment.listUpdate();

    }

    /**
     * Perform following action when this fragment is resumed. 1.Check the date
     * and time format of device and change(Manually or by changing locale) the
     * value of dateFormatCurrentJobList,timeFormatCurrentJobList formats so
     * that while updating Ui the format of date and time on current job list
     * get updated.
     */

    public void doOnResume() {
        CurrentJobDataBean currentJobDataBean = dataAceesObject
                .getCurrentJobs(dateSelected, user.getId());

        jobCount = currentJobDataBean.getCurrrntJobCount();
        SharedPref.setCurrentJobsCount(jobCount + "",
                syncroTeamBaseActivity);
        String dateFormatString = System
                .getString(syncroTeamBaseActivity.getContentResolver(),
                        System.DATE_FORMAT);
        String timeFormatString = System.getString(
                syncroTeamBaseActivity.getContentResolver(), System.TIME_12_24);

        dateFormatUnavabilities = new SimpleDateFormat("dd-MMM-yyyy");

        // if
        // (getDateFormat(syncroTeamBaseActivity).equalsIgnoreCase(dateFormat1))
        // {
        // dateFormatUnavabilities = new SimpleDateFormat("MMM-dd-yyyy");
        // } else if (getDateFormat(syncroTeamBaseActivity).equalsIgnoreCase(
        // dateFormat2)) {
        // dateFormatUnavabilities = new SimpleDateFormat("dd-MMM-yyyy");
        // } else if (getDateFormat(syncroTeamBaseActivity).equalsIgnoreCase(
        // dateFormat3)) {
        // dateFormatUnavabilities = new SimpleDateFormat("yyyy-MMM-dd");
        // }

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

        refreshList();

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

    // ..............................LISTENER...STARTS...HERE..............................................................

    // ......ONCLICK...LISTENER...STARTS...HERE.....
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtStartActivity:
                Intent intentCreateUnavailability = new Intent(
                        syncroTeamBaseActivity, CreateUnavailability.class);
                syncroTeamBaseActivity.startActivity(intentCreateUnavailability);
                break;

        }
    }

    // ******ONCLICK...LISTENER...ENDS...HERE*********

    // *****************************************LISTENER...ENDS...HERE*******************************************************

    // ...................................INTERFACE...STARTS...HERE........................................................

    public interface RefreshListener {
        void refreshList();
    }
    // ************************************INTERFACE...ENDS...HERE**********************************************************
}
