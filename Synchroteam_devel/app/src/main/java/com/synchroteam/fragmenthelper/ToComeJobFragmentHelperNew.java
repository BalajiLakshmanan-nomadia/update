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
import com.synchroteam.beans.EnableSynchronizationAddJobEvent;
import com.synchroteam.beans.ReportsJobBean;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.AllJobsSortingDialog;
import com.synchroteam.fragment.BaseFragment;
import com.synchroteam.fragment.ToComeJobFragment;
import com.synchroteam.listadapters.AllJobsDateAdapterRv;
import com.synchroteam.listadapters.AllJobsSortingAdapterRv;
import com.synchroteam.listadapters.ToComeJobsListAdapter;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Timer;
import java.util.TreeMap;
import java.util.Vector;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc

/***
 * This class is Responsible for inflating and Performing Actions on To Come
 * Jobs fragment.
 * <p>
 * created for future purpose
 *
 * @author Ideavate.solution
 */
@SuppressLint("SimpleDateFormat")
public class ToComeJobFragmentHelperNew implements HelperInterface, RvEmptyListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    SyncroTeamBaseActivity syncroTeamBaseActivity;
    protected ProgressDialog progressDSynch;
    private Dao dataAceesObject;
    private TreeMap<String, ArrayList<HashMap<String, String>>> toComeJobList;
    private ToComeJobsListAdapter toComeJobsListAdapter;
    private FullMonths[] fullMonths;
    private DateFormat dateFormat;
    private Format format;
    private BaseFragment baseFragment;
    private ProgressBar progressBarToComeJob;

    private RecyclerView rvUpcomingJobs;
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
    private AllJobsDateAdapterRv allJobsListAdapter;
    private AllJobsSortingAdapterRv mAllJobsSortingAdapter;

    private ArrayList<HashMap<String, String>> upcomingJobArrayList;
    private ArrayList<HashMap<String, String>> upcomingJobList;
    private Filter mUpcomingJobFilter;
    private Filter mJobSortingFilter;

    private String mSortingName;
    private int mSortingOption;
    private boolean isDateAdapter;
    private boolean isSortAdapter;

    private Timer myTimer;
    protected LocationManager locationManager;
    private LocationRequest mLocationRequest;
    private GoogleApiClient locationClient;
    private boolean isLocationClientConnected = false;
    private ToComeJobFragment toComeJobFragment;

    private int index;
    private int searchIndex;
    private boolean isUserSearching = false;
    private boolean isDbUpdated = false;

    private boolean isLoading;
    private int visibleThreshold = 2;
    private int lastVisibleItem, totalItemCount;

    /**
     * The Enum MonthsOfYear.
     */
    public enum MonthsOfYear {

        /**
         * The jan.
         */
        JAN,
        /**
         * The feb.
         */
        FEB,
        /**
         * The mar.
         */
        MAR,
        /**
         * The apr.
         */
        APR,
        /**
         * The may.
         */
        MAY,
        /**
         * The jun.
         */
        JUN,
        /**
         * The jul.
         */
        JUL,
        /**
         * The aug.
         */
        AUG,
        /**
         * The sep.
         */
        SEP,
        /**
         * The oct.
         */
        OCT,
        /**
         * The nov.
         */
        NOV,
        /**
         * The dec.
         */
        DEC;
    }

    /**
     * The Enum FullMonths.
     */
    public enum FullMonths {

        /**
         * The januaruy.
         */
        January,
        /**
         * The febuary.
         */
        Febuary,
        /**
         * The march.
         */
        March,
        /**
         * The april.
         */
        April,
        /**
         * The may.
         */
        May,
        /**
         * The june.
         */
        June,
        /**
         * The july.
         */
        July,
        /**
         * The august.
         */
        August,
        /**
         * The september.
         */
        September,
        /**
         * The october.
         */
        October,
        /**
         * The november.
         */
        November,
        /**
         * The december.
         */
        December;
    }

    /**
     * The months of year.
     */
    MonthsOfYear[] monthsOfYear;

    private static final String TAG = ToComeJobFragmentHelperNew.class.getSimpleName();

    /**
     * Instantiates a new to come fragment helper.
     *
     * @param syncroTeamBaseActivity the syncro team base activity
     * @param baseFragment           the base fragment
     */
    public ToComeJobFragmentHelperNew(
            SyncroTeamBaseActivity syncroTeamBaseActivity, ToComeJobFragment toComeJobFragment,
            BaseFragment baseFragment) {
        // TODO Auto-generated constructor stub

        this.syncroTeamBaseActivity = syncroTeamBaseActivity;
        this.toComeJobFragment = toComeJobFragment;
        dataAceesObject = DaoManager.getInstance();

        monthsOfYear = MonthsOfYear.values();
        fullMonths = FullMonths.values();
        this.baseFragment = baseFragment;
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
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.layout_tocome_new, container, false);

        SharedPref.setSortedJobNumber("", syncroTeamBaseActivity);
        SharedPref.setSortingOption(0, syncroTeamBaseActivity);

        initiateView(view);
        dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
        progressBarToComeJob = (ProgressBar) view
                .findViewById(R.id.progressBarToComeJob);
        format = android.text.format.DateFormat
                .getTimeFormat(syncroTeamBaseActivity);



        if (mSortingOption == KEYS.AllJobSortingOptions.SORT_BY_NEARBY_JOB) {
            String mSortedJobNumber = SharedPref.getSortedJobNumber(syncroTeamBaseActivity);
            HashMap<String, String> hmLatLong = dataAceesObject.getLatLongAllJobs(mSortedJobNumber);
            if (hmLatLong.size() > 0) {
                new FetchToComeJobFromDb().execute("" + mSortingOption, hmLatLong.get(KEYS.CurrentJobs.LAT), hmLatLong.get(KEYS.CurrentJobs.LON));
            } else {
                Toast.makeText(syncroTeamBaseActivity.getApplicationContext(), R.string.txt_jobno_mismatch, Toast.LENGTH_LONG).show();
            }
        } else if (mSortingOption != KEYS.AllJobSortingOptions.SORT_BY_NEARBY) {
            new FetchToComeJobFromDb().execute("" + mSortingOption, "", "");
        }

        return view;
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
        rvUpcomingJobs = (RecyclerView) v.findViewById(R.id.rv_upcoming_jobs);
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
        dialogSorting = AllJobsSortingDialog.getInstance(KEYS.AllJobSortingOptions.UPCOMING_JOBS_SORTING,
                null, false,false,baseFragment);
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

    /**
     * The item click listener.
     */

    /*
     * (non-Javadoc)
     *
     * @see com.synchroteam.fragmenthelper.HelperInterface#doOnSyncronize()
     */
    @Override
    public void doOnSyncronize() {
        if (mSortingOption == KEYS.AllJobSortingOptions.SORT_BY_NEARBY) {
            geocoder();
        } else if (mSortingOption == KEYS.AllJobSortingOptions.SORT_BY_NEARBY_JOB) {
            String mSortedJobNumber = SharedPref.getSortedJobNumber(syncroTeamBaseActivity);
            HashMap<String, String> hmLatLong = dataAceesObject.getLatLongAllJobs(mSortedJobNumber);
            if (hmLatLong.size() > 0) {
                new FetchToComeJobFromDb().execute("" + mSortingOption, hmLatLong.get(KEYS.CurrentJobs.LAT), hmLatLong.get(KEYS.CurrentJobs.LON));
            } else {
                Toast.makeText(syncroTeamBaseActivity.getApplicationContext(), R.string.txt_jobno_mismatch, Toast.LENGTH_LONG).show();
            }
        } else {
            new FetchToComeJobFromDb().execute("" + mSortingOption, "", "");
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

    @Override
    public void onEmptyList(ArrayList<HashMap<String, String>> list) {
        setEmptyViewForRv(list);
    }

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
    private void getUpcomingJobsByDate() {
        if (toComeJobList != null) {
            toComeJobList.clear();
        } else {
            toComeJobList = new TreeMap<>(
                    new Comparator<String>() {

                        @Override
                        public int compare(String lhs, String rhs) {
                            // TODO Auto-generated method stub
                            return -lhs.compareTo(rhs);
                        }
                    });
        }

        if (upcomingJobArrayList != null) {
            upcomingJobArrayList.clear();
        } else {
            upcomingJobArrayList = new ArrayList<>();
        }

        HashMap<String, String> map;

        Vector<CommonJobBean> vect;

        vect = dataAceesObject.getAllInterventionNext();

        Enumeration<CommonJobBean> en = vect.elements();

        while (en.hasMoreElements()) {
            CommonJobBean interv = en.nextElement();
            int st = interv.getCd_status_interv();
            map = new HashMap<String, String>();

            String numInterv = interv.getType_Interv();

            map.put(KEYS.CurrentJobs.DISPO, "false");
            if (!TextUtils.isEmpty(interv.getRef_customer()))
                numInterv = "#" + interv.getRef_customer() + " - "
                        + interv.getType_Interv();
            else if (interv.getNo_interv() != 0)
                numInterv = "#" + interv.getNo_interv() + " - "
                        + interv.getType_Interv();

            map.put(KEYS.CurrentJobs.ID, interv.getId());
            map.put(KEYS.CurrentJobs.CD_STATUS,
                    String.valueOf(interv.getCd_status_interv()));
            map.put(KEYS.CurrentJobs.ALL_JOB_HEADER, interv.getHeaderDate());
            map.put(KEYS.CurrentJobs.ID_USER,
                    String.valueOf(interv.getIdUser()));

            map.put(KEYS.CurrentJobs.ID_MODEL,
                    String.valueOf(interv.getId_model_rapport()));
//            map.put(KEYS.CurrentJobs.JOB_NUMBER, numInterv);
            map.put(KEYS.CurrentJobs.TYPE, numInterv);

            map.put(KEYS.CurrentJobs.PRIORITY, interv.getPriorite() + "");

            if (!TextUtils.isEmpty(interv.getNom_site_interv()))
                map.put(KEYS.CurrentJobs.CLTVILLE, interv.getNom_site_interv()
                        + " - " + interv.getAdr_interv_ville());
            else
                map.put(KEYS.CurrentJobs.CLTVILLE,
                        interv.getNom_client_interv() + " - "
                                + interv.getAdr_interv_ville());

            map.put(KEYS.CurrentJobs.LAT, interv.getLat());
            map.put(KEYS.CurrentJobs.LON, interv.getLon());
            map.put(KEYS.CurrentJobs.ADR_GLOBAL,
                    interv.getAdr_interv_globale());


            map.put(KEYS.CurrentJobs.NOMSITE, interv.getNom_site_interv());
            map.put(KEYS.CurrentJobs.NOMEQUIPMENT, interv.getNom_equipement());
            map.put(KEYS.CurrentJobs.IDSITE, String.valueOf(interv.getIdSite()));
            map.put(KEYS.CurrentJobs.IDCLIENT,
                    String.valueOf(interv.getIdClient()));
            map.put(KEYS.CurrentJobs.IDEQUIPMENT,
                    String.valueOf(interv.getIdEquipement()));

            map.put(KEYS.CurrentJobs.DESC,
                    String.valueOf(interv.getDesc()));

            map.put(KEYS.CurrentJobs.TELCEL,
                    interv.getTel_contact());

            map.put(KEYS.CurrentJobs.MOBILE_CONTACT,
                    interv.getMobileContact());

            map.put(KEYS.CurrentJobs.NOM_CLIENT_INTERV,
                    interv.getNom_client_interv());

            map.put(KEYS.CurrentJobs.ADR_VILLE,
                    String.valueOf(interv.getAdr_interv_ville()));

            //adding custom fields...
            map.put(KEYS.CurrentJobs.CF_INTERVENTION, dataAceesObject.getAllCFInterv(interv.getId()));
            map.put(KEYS.CurrentJobs.CF_SITE, dataAceesObject.getAllCFSite(interv.getIdSite()));
            map.put(KEYS.CurrentJobs.CF_CLIENT, dataAceesObject.getAllCFClient(interv.getIdClient()));
            map.put(KEYS.CurrentJobs.CF_EQUIPMENT, dataAceesObject.getAllCFEquip(interv.getIdEquipement()));

            map.put(KEYS.CurrentJobs.DATEMEETING,
                    String.valueOf(interv.getDt_meeting()));

            Date date;

            date = getDateFromDbFormat(interv.getDt_deb_prev());

            String dateToshow = null;
            if (st == KEYS.CurrentJobs.JOB__STARTED) {
                dateToshow = dataAceesObject.getJobStartTime(interv.getId());
                if (!TextUtils.isEmpty(dateToshow)) {
                    String[] dateTopass = dateToshow.split("/");

                    map.put(KEYS.CurrentJobs.DATE_TO_SHOW, dateTopass[0]);

                    map.put(KEYS.CurrentJobs.TIME_TO_SHOW, dateTopass[1]);
                }

            } else if (st == KEYS.CurrentJobs.JOB__SUSPENDED) {

                dateToshow = dataAceesObject
                        .getJobSuspendedTime(interv.getId());
                if (!TextUtils.isEmpty(dateToshow)) {

                    String[] dateTopass = dateToshow.split("/");

                    map.put(KEYS.CurrentJobs.DATE_TO_SHOW, dateTopass[0]);

                    map.put(KEYS.CurrentJobs.TIME_TO_SHOW, dateTopass[1]);
                }

            } else {

                map.put(KEYS.CurrentJobs.DATE_TO_SHOW, dateFormat.format(date)
                        + "");

                map.put(KEYS.CurrentJobs.TIME_TO_SHOW, format.format(date));
            }

            if (toComeJobList.containsKey(map
                    .get(KEYS.CurrentJobs.ALL_JOB_HEADER))) {
                toComeJobList.get(map.get(KEYS.CurrentJobs.ALL_JOB_HEADER))
                        .add(map);

                map.put(KEYS.CurrentJobs.HAS_HEADER, "false");
                upcomingJobArrayList.add(map);
            } else {
                toComeJobList.put(map.get(KEYS.CurrentJobs.ALL_JOB_HEADER),
                        new ArrayList<HashMap<String, String>>());
                toComeJobList.get(map.get(KEYS.CurrentJobs.ALL_JOB_HEADER))
                        .add(map);

                map.put(KEYS.CurrentJobs.HAS_HEADER, "true");
                upcomingJobArrayList.add(map);
            }
        }

        /*
         * SimpleAdapter adapter = new SimpleAdapter (this.getBaseContext(),
         * listItem, R.layout.list_item_interv, new String[] {"img1", "type",
         * "cltVille","plan", "img2"}, new int[] {R.id.img1, R.id.text1 ,
         * R.id.text2 , R.id.text3 ,R.id.img2});
         */

    }

    /**
     * Creates the adapter and inflate data in list view.
     */
    private void getUpcomingJobs(final int options, final String latitude, final String longitude) {

        if (upcomingJobList != null) {
            upcomingJobList.clear();
        } else {
            upcomingJobList = new ArrayList<>();
        }

        HashMap<String, String> map;

        Vector<CommonJobBean> vect;


        if (options == KEYS.AllJobSortingOptions.SORT_BY_NEARBY || options == KEYS.AllJobSortingOptions.SORT_BY_NEARBY_JOB) {
            vect = dataAceesObject.getUpcomingJobsByLocation(latitude, longitude);

            //if two or more jobs have the same lat & long, sort them according to the job number.
            Collections.sort(vect, new Comparator<CommonJobBean>() {
                @Override
                public int compare(CommonJobBean lhs, CommonJobBean rhs) {
                    return getSortingResultforNearby(lhs, lhs);
                }
            });

        } else {
            vect = dataAceesObject.getAllInterventionNext();
            Collections.sort(vect, new Comparator<CommonJobBean>() {
                @Override
                public int compare(CommonJobBean lhs, CommonJobBean rhs) {
                    switch (options) {
                        case KEYS.AllJobSortingOptions.SORT_BY_CLIENT:
                            return getSortingResultforClient(lhs, rhs);
                        case KEYS.AllJobSortingOptions.SORT_BY_SITE:
                            return getSortingResultforSite(lhs, rhs);
                        case KEYS.AllJobSortingOptions.SORT_BY_EQUIPMENT:
                            return getSortingResultforEquipment(lhs, rhs);
                        case KEYS.AllJobSortingOptions.SORT_BY_TOWN:
                            return getSortingResultforTown(lhs, rhs);
                        case KEYS.AllJobSortingOptions.SORT_BY_PRIORITY:
                            return getSortingResultForPriority(lhs, rhs);
                        default:
                            return getSortingResultforClient(lhs, rhs);
                    }
                }
            });
        }

        Enumeration<CommonJobBean> en = vect.elements();

        while (en.hasMoreElements()) {
            CommonJobBean interv = en.nextElement();
            int st = interv.getCd_status_interv();
            map = new HashMap<String, String>();

            String numInterv = interv.getType_Interv();

            map.put(KEYS.CurrentJobs.DISPO, "false");
            if (!TextUtils.isEmpty(interv.getRef_customer()))
                numInterv = "#" + interv.getRef_customer() + " - "
                        + interv.getType_Interv();
            else if (interv.getNo_interv() != 0)
                numInterv = "#" + interv.getNo_interv() + " - "
                        + interv.getType_Interv();

            map.put(KEYS.CurrentJobs.ID, interv.getId());
            map.put(KEYS.CurrentJobs.CD_STATUS,
                    String.valueOf(interv.getCd_status_interv()));
            map.put(KEYS.CurrentJobs.ALL_JOB_HEADER, interv.getHeaderDate());
            map.put(KEYS.CurrentJobs.ID_USER,
                    String.valueOf(interv.getIdUser()));

            map.put(KEYS.CurrentJobs.ID_MODEL,
                    String.valueOf(interv.getId_model_rapport()));
//            map.put(KEYS.CurrentJobs.JOB_NUMBER, numInterv);
            map.put(KEYS.CurrentJobs.TYPE, numInterv);
            map.put(KEYS.CurrentJobs.PRIORITY, interv.getPriorite() + "");

            if (!TextUtils.isEmpty(interv.getNom_site_interv()))
                map.put(KEYS.CurrentJobs.CLTVILLE, interv.getNom_site_interv()
                        + " - " + interv.getAdr_interv_ville());
            else
                map.put(KEYS.CurrentJobs.CLTVILLE,
                        interv.getNom_client_interv() + " - "
                                + interv.getAdr_interv_ville());

            map.put(KEYS.CurrentJobs.LAT, interv.getLat());
            map.put(KEYS.CurrentJobs.LON, interv.getLon());
            map.put(KEYS.CurrentJobs.ADR_GLOBAL,
                    interv.getAdr_interv_globale());

            map.put(KEYS.CurrentJobs.NOMSITE, interv.getNom_site_interv());
            map.put(KEYS.CurrentJobs.NOMEQUIPMENT, interv.getNom_equipement());
            map.put(KEYS.CurrentJobs.IDSITE, String.valueOf(interv.getIdSite()));
            map.put(KEYS.CurrentJobs.IDCLIENT,
                    String.valueOf(interv.getIdClient()));
            map.put(KEYS.CurrentJobs.IDEQUIPMENT,
                    String.valueOf(interv.getIdEquipement()));

            map.put(KEYS.CurrentJobs.NOM_CLIENT_INTERV,
                    interv.getNom_client_interv());

            map.put(KEYS.CurrentJobs.DESC,
                    String.valueOf(interv.getDesc()));

            map.put(KEYS.CurrentJobs.TELCEL,
                    interv.getTel_contact());

            map.put(KEYS.CurrentJobs.MOBILE_CONTACT,
                    interv.getMobileContact());

            map.put(KEYS.CurrentJobs.ADR_VILLE,
                    String.valueOf(interv.getAdr_interv_ville()));

            //adding custom fields...
            map.put(KEYS.CurrentJobs.CF_INTERVENTION, dataAceesObject.getAllCFInterv(interv.getId()));
            map.put(KEYS.CurrentJobs.CF_SITE, dataAceesObject.getAllCFSite(interv.getIdSite()));
            map.put(KEYS.CurrentJobs.CF_CLIENT, dataAceesObject.getAllCFClient(interv.getIdClient()));
            map.put(KEYS.CurrentJobs.CF_EQUIPMENT, dataAceesObject.getAllCFEquip(interv.getIdEquipement()));

            map.put(KEYS.CurrentJobs.DATEMEETING,
                    String.valueOf(interv.getDt_meeting()));

            Date date;

            date = getDateFromDbFormat(interv.getDt_deb_prev());

            String dateToshow = null;
            if (st == KEYS.CurrentJobs.JOB__STARTED) {
                dateToshow = dataAceesObject.getJobStartTime(interv.getId());
                if (!TextUtils.isEmpty(dateToshow)) {
                    String[] dateTopass = dateToshow.split("/");

                    map.put(KEYS.CurrentJobs.DATE_TO_SHOW, dateTopass[0]);

                    map.put(KEYS.CurrentJobs.TIME_TO_SHOW, dateTopass[1]);
                }

            } else if (st == KEYS.CurrentJobs.JOB__SUSPENDED) {

                dateToshow = dataAceesObject
                        .getJobSuspendedTime(interv.getId());
                if (!TextUtils.isEmpty(dateToshow)) {

                    String[] dateTopass = dateToshow.split("/");

                    map.put(KEYS.CurrentJobs.DATE_TO_SHOW, dateTopass[0]);

                    map.put(KEYS.CurrentJobs.TIME_TO_SHOW, dateTopass[1]);
                }

            } else {

                map.put(KEYS.CurrentJobs.DATE_TO_SHOW, dateFormat.format(date)
                        + "");

                map.put(KEYS.CurrentJobs.TIME_TO_SHOW, format.format(date));
            }

            upcomingJobList.add(map);
        }

        /*
         * SimpleAdapter adapter = new SimpleAdapter (this.getBaseContext(),
         * listItem, R.layout.list_item_interv, new String[] {"img1", "type",
         * "cltVille","plan", "img2"}, new int[] {R.id.img1, R.id.text1 ,
         * R.id.text2 , R.id.text3 ,R.id.img2});
         */

    }


    /**
     * Set the to Come list adapter to toCome list view.
     */
    private void setToComeListAdapter() {

        if (allJobsListAdapter == null || !isDateAdapter) {
            allJobsListAdapter = new AllJobsDateAdapterRv(syncroTeamBaseActivity, this,
                    upcomingJobArrayList, false);
//            rvAllJobs.addFooterView(footerView);
            index = 1;
            searchIndex = 1;

            allJobsListAdapter.setIndexPosition(index);
            rvUpcomingJobs.setLayoutManager(linearLayoutManager);
            rvUpcomingJobs.setAdapter(allJobsListAdapter);


        } else {
            if (isDbUpdated) {
                allJobsListAdapter.setDuplicateList();
                isDbUpdated = false;
            }
            allJobsListAdapter.notifyDataSetChanged();
        }

        setEmptyViewForRv(upcomingJobArrayList);

        resetSearchField();

        if (mUpcomingJobFilter == null) {
            mUpcomingJobFilter = allJobsListAdapter.getAllJobFilter();
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
            rvUpcomingJobs.setVisibility(View.VISIBLE);
            txtEmptyList.setVisibility(View.GONE);
        } else {
            rvUpcomingJobs.setVisibility(View.GONE);
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
            HashMap<String, String> hmLatLong = dataAceesObject.getLatLongAllJobs(mSortedJobNumber);
            if (hmLatLong.size() > 0) {
                new FetchToComeJobFromDb().execute("" + mSortingOption, hmLatLong.get(KEYS.CurrentJobs.LAT), hmLatLong.get(KEYS.CurrentJobs.LON));
            } else {
                Toast.makeText(syncroTeamBaseActivity.getApplicationContext(), R.string.txt_jobno_mismatch, Toast.LENGTH_LONG).show();
            }
        } else {
            Logger.output(TAG, "others");
            new FetchToComeJobFromDb().execute("" + mSortingOption, "", "");
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
            HashMap<String, String> hmLatLong = dataAceesObject.getLatLongAllJobs(jobNumber);
            new FetchToComeJobFromDb().execute("" + mSortingOption, hmLatLong.get(KEYS.CurrentJobs.LAT), hmLatLong.get(KEYS.CurrentJobs.LON));
        } else {
            txtSortBy.setText(optionName);
            SharedPref.setSortingOption(options, syncroTeamBaseActivity);
            new FetchToComeJobFromDb().execute("" + mSortingOption, "", "");
        }
    }

    /**
     * Set the list adapter to deadlineExccedeListView.
     */
    private void setSortingListAdapter() {
        if (mAllJobsSortingAdapter == null || !isSortAdapter) {
            mAllJobsSortingAdapter = new AllJobsSortingAdapterRv(syncroTeamBaseActivity, this,
                    upcomingJobList, false);
            index = 1;
            searchIndex = 1;

            mAllJobsSortingAdapter.setIndexPosition(index);
            rvUpcomingJobs.setLayoutManager(linearLayoutManager);
            rvUpcomingJobs.setAdapter(mAllJobsSortingAdapter);



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

        setEmptyViewForRv(upcomingJobList);

        resetSearchField();

        if (mJobSortingFilter == null) {
            mJobSortingFilter = mAllJobsSortingAdapter.getJobFilter();
            mUpcomingJobFilter = null;
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
                        toComeJobFragment.callingPermissionLocation();
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
                        toComeJobFragment.startActivityForResult(intent, RequestCode.REQUEST_CODE_GPS_SETTINGS);
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

            new FetchToComeJobFromDb().execute(mSortingOption + "",
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
        }else{
            return 0;
        }
    }

    /**
     * Fetches the up comming jobs from DB.
     *
     * @author Ideavate.Solution
     */

    private class FetchToComeJobFromDb extends AsyncTaskCoroutine<String, Void> {

        private int option;
        private String mLatitude;
        private String mLongitude;

        @Override
        public void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            rvUpcomingJobs.setVisibility(View.GONE);
            progressBarToComeJob.setVisibility(View.VISIBLE);

        }

        @Override
        public Void doInBackground(String... params) {
            option = Integer.parseInt(params[0]);
            mLatitude = params[1];
            mLongitude = params[2];

            if (option == KEYS.AllJobSortingOptions.SORT_BY_DATE) {
                getUpcomingJobsByDate();
            } else {
                getUpcomingJobs(mSortingOption, mLatitude, mLongitude);
            }
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            progressBarToComeJob.setVisibility(View.GONE);
            rvUpcomingJobs.setVisibility(View.VISIBLE);
            if (mSortingOption == KEYS.AllJobSortingOptions.SORT_BY_DATE) {
                setToComeListAdapter();
            } else {
                setSortingListAdapter();
            }
            rvUpcomingJobs.addOnScrollListener(mScrollListener);

            EventBus.getDefault().post(new EnableSynchronizationAddJobEvent());

        }

    }

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
                    toComeJobFragment.startActivityForResult(intent,
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

                mUpcomingJobFilter.filter(s.toString());
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
