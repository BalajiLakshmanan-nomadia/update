package com.synchroteam.fragmenthelper;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.synchroteam.baseactivity.SyncroTeamBaseActivity;
import com.synchroteam.beans.ReportsJobBean;
import com.synchroteam.dao.Dao;
import com.synchroteam.fragment.BaseFragment;
import com.synchroteam.listadapters.ReportsByDateJobsListAdapter;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.technicalsupport.JobDetails;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;

// TODO: Auto-generated Javadoc

/**
 * The Class is responsible to inflate and perform various actions in Reports Fragment when date tab is selected.
 * <p>
 * created for future purpose
 *
 * @author Ideavate.solution
 */
public class ReportsWithDateFragmentHelper implements HelperInterface {

    /**
     * The syncro team base activity.
     */
    private SyncroTeamBaseActivity syncroTeamBaseActivity;

    /**
     * The selected date.
     */
    private Date selectedDate;

    /**
     * The list view.
     */
    private ListView listView;

    /**
     * The reports by date jobs list adapter.
     */
    private ReportsByDateJobsListAdapter reportsByDateJobsListAdapter;

    /**
     * The complete jobs list list.
     */
    private ArrayList<HashMap<String, String>> completeJobsListList;

    /**
     * The data access object.
     */
    private Dao dataAccessObject;


    /**
     * The base fragment.
     */
    private BaseFragment baseFragment;


    /**
     * The date format.
     */
    private DateFormat dateFormat;

    /**
     * The format.
     */
    private Format format;

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
     * The months of year.
     */
    MonthsOfYear[] monthsOfYear;

    /**
     * Instantiates a new reports with date fragment helper.
     *
     * @param syncroTeamBaseActivity the syncro team base activity
     * @param baseFragment           the base fragment
     */
    public ReportsWithDateFragmentHelper(
            SyncroTeamBaseActivity syncroTeamBaseActivity, BaseFragment baseFragment) {
        // TODO Auto-generated constructor stub

        this.syncroTeamBaseActivity = syncroTeamBaseActivity;
        this.dataAccessObject = DaoManager.getInstance();

//		 monthsOfYear = MonthsOfYear.values();
        this.baseFragment = baseFragment;

    }


    /* (non-Javadoc)
     * @see com.synchroteam.fragmenthelper.HelperInterface#inflateLayout(android.view.LayoutInflater, android.view.ViewGroup)
     */
    @Override
    public View inflateLayout(LayoutInflater inflater, ViewGroup container) {
        // TODO Auto-generated method stub
        View view = inflater.inflate(R.layout.layout_reports_list_with_date, null);
        dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
        format = android.text.format.DateFormat.getTimeFormat(syncroTeamBaseActivity);
        initiateView(view);
        return view;
    }

    /* (non-Javadoc)
     * @see com.synchroteam.fragmenthelper.HelperInterface#initiateView(android.view.View)
     */
    @Override
    public void initiateView(View v) {
        // TODO Auto-generated method stub

        listView = (ListView) v.findViewById(R.id.reportsWithDateListView);


        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                @SuppressWarnings("unchecked")
                HashMap<String, String> map = (HashMap<String, String>) listView.getItemAtPosition(arg2);
                if (!TextUtils.isEmpty(map.get(KEYS.CurrentJobs.DISPO))) {
                    if (!map.get(KEYS.CurrentJobs.DISPO).equals(KEYS.CurrentJobs.TRUE)) {
                        Bundle bundle = new Bundle();
                        String[] numInterv = map.get(KEYS.CurrentJobs.TYPE)
                                .split("-");
                        String nmInterv = "";
                        if (numInterv.length == 2)
                            nmInterv = numInterv[0].substring(1);
                        else
                            nmInterv = "0";

                        bundle.putString(KEYS.CurrentJobs.ID,
                                map.get(KEYS.CurrentJobs.ID));

                        bundle.putInt(KEYS.CurrentJobs.CD_STATUS, Integer
                                .parseInt(map.get(KEYS.CurrentJobs.CD_STATUS)));
                        bundle.putString(KEYS.CurrentJobs.ID_USER,
                                map.get(KEYS.CurrentJobs.ID_USER));
                        bundle.putString(KEYS.CurrentJobs.CONTACT,
                                map.get(KEYS.CurrentJobs.CONTACT));
                        bundle.putString(KEYS.CurrentJobs.TEL,
                                map.get(KEYS.CurrentJobs.TEL));
                        bundle.putString(KEYS.CurrentJobs.ADR_GLOBAL,
                                map.get(KEYS.CurrentJobs.ADR_GLOBAL));
                        bundle.putString(KEYS.CurrentJobs.ADR_COMPLEMENT,
                                map.get(KEYS.CurrentJobs.ADR_COMPLEMENT));
                        bundle.putString(KEYS.CurrentJobs.DESC,
                                map.get(KEYS.CurrentJobs.DESC));
                        bundle.putString(KEYS.CurrentJobs.ID_MODEL,
                                map.get(KEYS.CurrentJobs.ID_MODEL));
                        bundle.putString(KEYS.CurrentJobs.LAT,
                                map.get(KEYS.CurrentJobs.LAT));
                        bundle.putString(KEYS.CurrentJobs.LON,
                                map.get(KEYS.CurrentJobs.LON));
                        bundle.putString(KEYS.CurrentJobs.MDATE1,
                                map.get(KEYS.CurrentJobs.MDATE1));
                        bundle.putString(KEYS.CurrentJobs.MDATE2,
                                map.get(KEYS.CurrentJobs.MDATE2));
                        bundle.putString("NumIntevType", nmInterv);
                        bundle.putString(KEYS.CurrentJobs.NOMSITE,
                                map.get("nomSite"));
                        bundle.putString(KEYS.CurrentJobs.NOMEQUIPMENT,
                                map.get(KEYS.CurrentJobs.NOMEQUIPMENT));
                        bundle.putInt(KEYS.CurrentJobs.IDSITE, Integer
                                .valueOf(map.get(KEYS.CurrentJobs.IDSITE)));
                        bundle.putInt(KEYS.CurrentJobs.IDCLIENT, Integer
                                .valueOf(map.get(KEYS.CurrentJobs.IDCLIENT)));
                        bundle.putInt(KEYS.CurrentJobs.IDEQUIPMENT, Integer
                                .valueOf(map.get(KEYS.CurrentJobs.IDEQUIPMENT)));
                        bundle.putString(KEYS.CurrentJobs.TELCEL,
                                map.get(KEYS.CurrentJobs.TELCEL));
                        bundle.putString(KEYS.CurrentJobs.DATEMEETING,
                                map.get(KEYS.CurrentJobs.DATEMEETING));
                        bundle.putString(KEYS.CurrentJobs.TYPE,
                                map.get(KEYS.CurrentJobs.TYPE));

                        bundle.putString(KEYS.CurrentJobs.FROM_WHERE,
                                KEYS.CurrentJobs.SYNCROTEAMNAVIGATIONACTIVITY);
                        bundle.putBoolean(KEYS.CurrentJobs.IDSTARTJOB,false);
                        // closeDb();
                        Intent jobDetailIntent = new Intent(syncroTeamBaseActivity,
                                JobDetails.class);
                        jobDetailIntent.putExtras(bundle);
                        syncroTeamBaseActivity.startActivity(jobDetailIntent);
                    }
                }

            }


        });


    }

    /* (non-Javadoc)
     * @see com.synchroteam.fragmenthelper.HelperInterface#doOnSyncronize()
     */
    @Override
    public void doOnSyncronize() {
        // TODO Auto-generated method stub
        createAdapterAndInflateDatainListView();
        baseFragment.listUpdate();
    }

    /* (non-Javadoc)
     * @see com.synchroteam.fragmenthelper.HelperInterface#onReturnToActivity(int)
     */
    @Override
    public void onReturnToActivity(int requestCode) {
        // TODO Auto-generated method stub


    }

    /**
     * On date selected.
     *
     * @param date the date
     */
    public void onDateSelected(Date date) {
        selectedDate = date;
        createAdapterAndInflateDatainListView();


    }


    /**
     * Update ui.
     */
    public void updateUi() {
        dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
        format = android.text.format.DateFormat.getTimeFormat(syncroTeamBaseActivity);
        createAdapterAndInflateDatainListView();
    }

    /**
     * Creates the adapter and inflate datain list view.
     */
    private void createAdapterAndInflateDatainListView() {
        if (completeJobsListList != null) {
            completeJobsListList.clear();
        } else {
            completeJobsListList = new ArrayList<HashMap<String, String>>();
        }

        HashMap<String, String> map;

        Vector<ReportsJobBean> vect = new Vector<ReportsJobBean>();


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        vect = dataAccessObject.getAllInterventionCompletedDate(simpleDateFormat.format(selectedDate));

        Enumeration<ReportsJobBean> en = vect.elements();

        while (en.hasMoreElements()) {

            ReportsJobBean interv = en.nextElement();
            int st = interv.getCd_status_interv();

            String numInterv = interv.getType_Interv();
            map = new HashMap<String, String>();
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
            map.put(KEYS.CurrentJobs.ID_USER,
                    String.valueOf(interv.getIdUser()));
            map.put(KEYS.CurrentJobs.CONTACT, interv.getNom_contact());
            map.put(KEYS.CurrentJobs.TEL, interv.getTel_contact());

            map.put(KEYS.CurrentJobs.ADR_VILLE, interv.getAdr_interv_ville());
            map.put(KEYS.CurrentJobs.ADR_CP, interv.getAdr_interv_cp());
            map.put(KEYS.CurrentJobs.ADR_PAYS, interv.getAdr_interv_pays());
            map.put(KEYS.CurrentJobs.ADR_RUE, interv.getAdr_interv_rue());
            map.put(KEYS.CurrentJobs.ADR_GLOBAL, interv.getAdr_interv_globale());
            map.put(KEYS.CurrentJobs.ADR_COMPLEMENT,
                    interv.getAdr_interv_complement());
            map.put(KEYS.CurrentJobs.NOM_CLIENT_INTERV,
                    interv.getNom_client_interv());

            map.put(KEYS.CurrentJobs.DESC,
                    String.valueOf(interv.getDesc()));

            map.put(KEYS.CurrentJobs.TELCEL,
                    interv.getTel_contact());

            map.put(KEYS.CurrentJobs.MOBILE_CONTACT,
                    interv.getMobileContact());

            map.put(KEYS.CurrentJobs.DATE_CREATED_REAL, interv.getDt_deb_real());
            map.put(KEYS.CurrentJobs.DESC, interv.getDesc());
            map.put(KEYS.CurrentJobs.ID_MODEL,
                    String.valueOf(interv.getId_model_rapport()));
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

            map.put(KEYS.CurrentJobs.NOMSITE, interv.getNom_site_interv());
            map.put(KEYS.CurrentJobs.NOMEQUIPMENT, interv.getNom_equipement());
            map.put(KEYS.CurrentJobs.IDSITE, String.valueOf(interv.getIdSite()));
            map.put(KEYS.CurrentJobs.IDCLIENT,
                    String.valueOf(interv.getIdClient()));
            map.put(KEYS.CurrentJobs.IDEQUIPMENT,
                    String.valueOf(interv.getIdEquipement()));
            map.put(KEYS.CurrentJobs.TELCEL, interv.getMobileContact());

            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
            SimpleDateFormat formatDay = new SimpleDateFormat("E");
            DateFormat df1 = DateFormat.getTimeInstance(DateFormat.SHORT);

            Date date, time1, time2;
            if (interv.getCd_status_interv() == 5) {

                date = getDateFromDbFormat(interv.getDt_deb_real());

                time1 = getDateFromDbFormat(interv.getDt_deb_real());
                time2 = getDateFromDbFormat(interv.getDt_fin_real());
            } else {
                date = getDateFromDbFormat(interv.getDt_deb_prev());
                time1 = getDateFromDbFormat(interv.getDt_deb_prev());
                time2 = getDateFromDbFormat(interv.getDt_fin_prev());
            }


//			calendarForJobs.setTime(time2);
//
//			map.put(KEYS.CurrentJobs.MONTH_TO_SHOW,
//					fullMonths[calendarForJobs.get(Calendar.MONTH)]
//							+ "");
//			map.put(KEYS.CurrentJobs.YEAR_TO_SHOW,
//					calendarForJobs.get(Calendar.YEAR) + "");
            map.put(KEYS.CurrentJobs.DATE_TO_SHOW,
                    dateFormat.format(time2) + "");
//			String minute;
//			if (calendarForJobs.get(Calendar.MINUTE) < 10) {
//				minute = "0" + calendarForJobs.get(Calendar.MINUTE);
//			} else {
//				minute = "" + calendarForJobs.get(Calendar.MINUTE);
//			}
//			String hour;
//			if (calendarForJobs.get(Calendar.HOUR_OF_DAY) < 10) {
//				hour = "0" + calendarForJobs.get(Calendar.HOUR_OF_DAY);
//			} else {
//				hour = "" + calendarForJobs.get(Calendar.HOUR_OF_DAY);
//			}


            map.put(KEYS.CurrentJobs.TIME_TO_SHOW, format.format(time2));


            map.put(KEYS.CurrentJobs.PLAN,
                    formatDay.format(date) + " " + df.format(date) + "  -  "
                            + df1.format(time1) + " > " + df1.format(time2));

            map.put(KEYS.CurrentJobs.MDATE1, interv.getDt_deb_prev());
            map.put(KEYS.CurrentJobs.MDATE2, interv.getDt_fin_prev());
            if (!TextUtils.isEmpty(interv.getDt_meeting())) {

                Date dateMeeting = getDateFromDbFormat(interv.getDt_meeting());
                Date timeMeeting = getDateFromDbFormat(interv.getDt_meeting());
                map.put(KEYS.CurrentJobs.DATEMEETING,
                        formatDay.format(dateMeeting) + " "
                                + df.format(dateMeeting) + " "
                                + df1.format(timeMeeting));
            } else
                map.put(KEYS.CurrentJobs.DATEMEETING, "");

            map.put(KEYS.CurrentJobs.JOB_TYPE, st + "");

            completeJobsListList.add(map);

        }

		/*
         * SimpleAdapter adapter = new SimpleAdapter (this.getBaseContext(),
		 * listItem, R.layout.list_item_interv, new String[] {"img1", "type",
		 * "cltVille","plan", "img2"}, new int[] {R.id.img1, R.id.text1 ,
		 * R.id.text2 , R.id.text3 ,R.id.img2});
		 */
        if (reportsByDateJobsListAdapter == null) {
            reportsByDateJobsListAdapter = new ReportsByDateJobsListAdapter(
                    syncroTeamBaseActivity, completeJobsListList);
            listView.setAdapter(reportsByDateJobsListAdapter);
        } else {
            reportsByDateJobsListAdapter.notifyDataSetChanged();
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
            Logger.printException(e);
            return new Date();
        }
    }


}
