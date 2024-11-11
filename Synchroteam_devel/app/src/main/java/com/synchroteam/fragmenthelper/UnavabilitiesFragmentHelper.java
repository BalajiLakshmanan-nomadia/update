package com.synchroteam.fragmenthelper;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.baseactivity.SyncroTeamBaseActivity;
import com.synchroteam.beans.Conge;
import com.synchroteam.beans.EnableSynchronizationAddJobEvent;
import com.synchroteam.dao.Dao;
import com.synchroteam.fragment.BaseFragment;
import com.synchroteam.listadapters.UnavabilitiesListAdapterNewDuplicate;
import com.synchroteam.synchroteam.AddUnavailability;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.KEYS;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc

/**
 * The Class UnavabilitiesFragmentHelper.
 */
public class UnavabilitiesFragmentHelper implements HelperInterface,
        OnClickListener {

    /**
     * The syncro team base activity.
     */
    private SyncroTeamBaseActivity syncroTeamBaseActivity;

    /**
     * The data acees object.
     */
    private Dao dataAceesObject;

    /**
     * The base fragment.
     */
    private BaseFragment baseFragment;

    /**
     * The calendar for jobs.
     */
    private Calendar calendarForJobs;

    /**
     * The unavabilities list view.
     */
    private ListView unavabilitiesListView;

    /**
     * The unavbilities list.
     */
    private ArrayList<HashMap<String, String>> unavbilitiesList;

    /**
     * The unavabilities list adapter.
     */
//    private UnavabilitiesListAdapter unavabilitiesListAdapter;
    private UnavabilitiesListAdapterNewDuplicate unavabilitiesListAdapter;

    /**
     * The date format current job list.
     */
    // private DateFormat dateFormatCurrentJobList;
    private SimpleDateFormat dateFormatCurrentJobList;

    /**
     * The format.
     */
    private Format timeFormatCurrentJobList;

    private ProgressBar progressBarUnavabilities;

    private TextView txtAddUnavailability;

    private View footerView;
    private static final String dateFormat1 = "mm/dd/yyyy";
    private static final String dateFormat2 = "dd/mm/yyyy";
    private static final String dateFormat3 = "yyyy/mm/dd";

    private String currentDateFormat = "yyyy-MM-dd HH:mm:ss";

    // private static final String TAG = "UnavabilitiesFragmentHelper";

    /**
     * Instantiates a new unavabilities fragment helper.
     *
     * @param syncroTeamBaseActivity the syncro team base activity
     * @param baseFragment           the base fragment
     */
    public UnavabilitiesFragmentHelper(
            SyncroTeamBaseActivity syncroTeamBaseActivity,
            BaseFragment baseFragment) {
        // TODO Auto-generated constructor stub

        this.syncroTeamBaseActivity = syncroTeamBaseActivity;
        dataAceesObject = DaoManager.getInstance();
        this.baseFragment = baseFragment;

    }

    public void doOnResume() {

        refreshList();

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
        View view = inflater.inflate(R.layout.layout_unavabilities, container,
                false);
        initiateView(view);

        // dateFormatCurrentJobList = DateFormat
        // .getDateInstance(DateFormat.MEDIUM);
        dateFormatCurrentJobList = new SimpleDateFormat("dd-MMM-yyyy");
        timeFormatCurrentJobList = android.text.format.DateFormat
                .getTimeFormat(syncroTeamBaseActivity);
        progressBarUnavabilities = (ProgressBar) view
                .findViewById(R.id.progressBarUnavabilities);
        new FetchUnavabilitiesAsyncTask().execute();

        return view;
    }

    /**
     * Creates the adapter and inflate data in list view.
     */
    private ArrayList<HashMap<String, String>> createAdapterAndInflateDataInListView() {
        // TODO Auto-generated method stub

        ArrayList<HashMap<String, String>> listData = new ArrayList<HashMap<String, String>>();

        HashMap<String, String> map;

        Vector<Conge> vectConge = new Vector<Conge>();
        vectConge = dataAceesObject.getConge();
        Enumeration<Conge> enindisp = vectConge.elements();

        while (enindisp.hasMoreElements()) {
            Conge indisp = enindisp.nextElement();
            map = new HashMap<String, String>();
            map.put(KEYS.Unavabilities.UNAVAILABILITY_ID, indisp.getIdConge());

            map.put(KEYS.Unavabilities.TYPE, indisp.getNomTypeConge());
            map.put(KEYS.Unavabilities.ID_TYPE_CONGE, ""+indisp.getIdTypeConge());

            map.put(KEYS.Unavabilities.CLTVILLE, indisp.getNote());
            if (indisp.isHeader()) {
                map.put(KEYS.Unavabilities.IS_HEADER, KEYS.Unavabilities.TRUE);
                map.put(KEYS.Unavabilities.DT_DEBUT, indisp.getDtDebut());
            } else {
                map.put(KEYS.Unavabilities.IS_HEADER, KEYS.Unavabilities.FALSE);

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                SimpleDateFormat updateFormat = new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss.SSS");

                Date dateD, dateF = null;
                dateD = getDateFromDbFormat(indisp.getDtDebut());

                String activityStartDate = null;
                try {
                    activityStartDate = getDateWithRequiredPresettedPattern(indisp.getDtDebut(), currentDateFormat);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

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

                map.put(KEYS.Unavabilities.UNAVAILABILITY_ID,
                        indisp.getIdConge());
                map.put(KEYS.Unavabilities.TYPE, indisp.getNomTypeConge());
                map.put(KEYS.Unavabilities.IMG, indisp.getCouleurConge());

                map.put(KEYS.Unavabilities.ID_USER, indisp.getIdUser());
                map.put(KEYS.Unavabilities.ID_GROUPE, indisp.getIdGroupe());
                map.put(KEYS.Unavabilities.FL_UNAVAILABLE, String.valueOf(indisp.getFlUnavailable()));
                map.put(KEYS.Unavabilities.FL_PAYABLE, String.valueOf(indisp.getFlPayable()));
            }

            listData.add(map);
        }

        return listData;
    }

    /**
     * sets the adapter to unavabilities list view
     */

    private void setUnavabilityListAdapter() {

        if (unavabilitiesListAdapter != null) {
            unavabilitiesListAdapter.notifyDataSetChanged();
        } else {
            unavabilitiesListAdapter = new UnavabilitiesListAdapterNewDuplicate(
                    syncroTeamBaseActivity, this, unavbilitiesList);
            unavabilitiesListView.addFooterView(footerView);
            unavabilitiesListView.setAdapter(unavabilitiesListAdapter);
            Log.e("unavailability","unavailability list size"+unavabilitiesListAdapter.getCount());
        }

        baseFragment.listUpdate();
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
        // TODO Auto-generated method stub
        unavabilitiesListView = (ListView) v
                .findViewById(R.id.unavablilitiesLv);

        txtAddUnavailability = (TextView) v
                .findViewById(R.id.txtAddUnavailability);

        footerView = ((LayoutInflater) syncroTeamBaseActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.empty_view_item, null, false);
        txtAddUnavailability.setOnClickListener(this);

    }

    /*
     * (non-Javadoc)
     *
     * @see com.synchroteam.fragmenthelper.HelperInterface#doOnSyncronize()
     */
    @Override
    public void doOnSyncronize() {
        // TODO Auto-generated method stub
        new FetchUnavabilitiesAsyncTask().execute();

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.synchroteam.fragmenthelper.HelperInterface#onReturnToActivity(int)
     */
    @Override
    public void onReturnToActivity(int requestCode) {
        // TODO Auto-generated method stub

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
            assert requestDate != null;
            requiredDate = dateFormat.format(requestDate);
            Format format = android.text.format.DateFormat
                    .getTimeFormat(syncroTeamBaseActivity);
            requiredDate = requiredDate + " " + format.format(requestDate);
            // System.out.println("requiredDate" + requiredDate);
        }
        return requiredDate;
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
     * Refresh list.
     */
    public void refreshList() {
        // TODO Auto-generated method stub

        // dateFormatCurrentJobList = DateFormat
        // .getDateInstance(DateFormat.MEDIUM);
        if (unavabilitiesListAdapter != null) {
            unavabilitiesListAdapter.notifyDataSetChanged();
        }

        // if
        // (getDateFormat(syncroTeamBaseActivity).equalsIgnoreCase(dateFormat1))
        // {
        // dateFormatCurrentJobList = new SimpleDateFormat("MMM-dd-yyyy");
        // } else if (getDateFormat(syncroTeamBaseActivity).equalsIgnoreCase(
        // dateFormat2)) {
        // dateFormatCurrentJobList = new SimpleDateFormat("dd-MMM-yyyy");
        // } else if (getDateFormat(syncroTeamBaseActivity).equalsIgnoreCase(
        // dateFormat3)) {
        // dateFormatCurrentJobList = new SimpleDateFormat("yyyy-MMM-dd");
        // }

        dateFormatCurrentJobList = new SimpleDateFormat("dd-MMM-yyyy");
        timeFormatCurrentJobList = android.text.format.DateFormat
                .getTimeFormat(syncroTeamBaseActivity);
        new FetchUnavabilitiesAsyncTask().execute();

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

    /**
     * Updates the list after datas fetched from database.
     *
     * @param listData
     */
    private void updateList(ArrayList<HashMap<String, String>> listData) {
        if (unavbilitiesList == null) {
            unavbilitiesList = new ArrayList<HashMap<String, String>>();
        } else {
            unavbilitiesList.clear();
        }

        unavbilitiesList.addAll(listData);
    }

    /**
     * Fetches the unavabilities from the database in background thread.
     */

    private class FetchUnavabilitiesAsyncTask extends
            AsyncTaskCoroutine<Void, ArrayList<HashMap<String, String>>> {

        @Override
        public void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            progressBarUnavabilities.setVisibility(View.VISIBLE);
            unavabilitiesListView.setVisibility(View.GONE);
        }

        @Override
        public ArrayList<HashMap<String, String>> doInBackground(
                Void... params) {
            // TODO Auto-generated method stub

            return createAdapterAndInflateDataInListView();
        }

        @Override
        public void onPostExecute(ArrayList<HashMap<String, String>> result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            updateList(result);
            progressBarUnavabilities.setVisibility(View.GONE);
            unavabilitiesListView.setVisibility(View.VISIBLE);
            setUnavabilityListAdapter();
            EventBus.getDefault().post(new EnableSynchronizationAddJobEvent());
        }

    }

    // ......................................LISTENER...STARTS...HERE...............................
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtAddUnavailability:
                Intent intentCreateUnavailability = new Intent(
                        syncroTeamBaseActivity, AddUnavailability.class);
                syncroTeamBaseActivity.startActivity(intentCreateUnavailability);
                break;

        }
    }

    // **************************************LISTENER...ENDS...HERE*******************************

}
