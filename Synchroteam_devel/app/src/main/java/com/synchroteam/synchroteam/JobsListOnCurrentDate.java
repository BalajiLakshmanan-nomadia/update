package com.synchroteam.synchroteam;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.synchroteam.tracking.TrackingParameterService;
import com.synchroteam.beans.CommonJobBean;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.beans.UpdateUiOnSync;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.AuthenticationErrorDialog;
import com.synchroteam.dialogs.ErrorDialog;
import com.synchroteam.dialogs.SynchronizationErrorDialog;
import com.synchroteam.listadapters.AllJobsListAdapter;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DateChecker;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.SynchroteamUitls;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.Vector;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc

/**
 * The Class JobsListOnCurrentDate.
 */
public class JobsListOnCurrentDate extends AppCompatActivity implements
        CommonInterface {

    /**
     * The pinned section list view.
     */
    ListView pinnedSectionListView;

    /**
     * The months of year.
     */
    MonthsOfYear[] monthsOfYear;

    /**
     * The action bar.
     */
    private ActionBar actionBar;

    /**
     * The all job list.
     */
    private TreeMap<String, ArrayList<HashMap<String, String>>> allJobList;

    /**
     * The data acees object.
     */
    private Dao dataAceesObject;

    /**
     * The date.
     */
    private String date;

    /**
     * The footer view.
     */
    private View footerView;

    /**
     * The progress d synch.
     */
    private ProgressDialog progressDSynch;

    /**
     * The user.
     */
    private User user;

    /**
     * The all jobs list adapter.
     */
    private AllJobsListAdapter allJobsListAdapter;

    /**
     * The pi.
     */
    private PendingIntent pi, pi1, pITrackParams;

    /**
     * The flag.
     */
    private int isAddIconBeShownFlag;

    /**
     * The menu.
     */
    private Menu menu;

    /**
     * The date format.
     */
    private DateFormat dateFormat;

    /**
     * The format.
     */
    private Format format;

    /** TAG */
    // private static final String TAG = "JobsListOnCurrentDate";

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

    /*
     * (non-Javadoc)
     *
     * @see android.support.v7.app.AppCompatActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_joblist);
        actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        actionBar.setTitle("");

        pinnedSectionListView = (ListView) findViewById(R.id.jobListLv);
        dataAceesObject = DaoManager.getInstance();

        user = dataAceesObject.getUser();

//        pi = PendingIntent.getService(this, 0, new Intent(this,
//                                                          TrackingService.class), PendingIntent.FLAG_UPDATE_CURRENT);
//        pi1 = PendingIntent.getService(this, 0, new Intent(this,
//                                                           TrackingServiceFrequency.class),
//                                       PendingIntent.FLAG_UPDATE_CURRENT);

        pITrackParams = initializePendingIntent();

        monthsOfYear = MonthsOfYear.values();

        footerView = ((LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.layout_footerview_items_list, null, false);

        pinnedSectionListView.addFooterView(footerView);

        date = getIntent().getStringExtra(KEYS.Calander.DATE_SELECTED);

        isAddIconBeShownFlag = dataAceesObject.getAddIntervFlag();

        //for showing the header
        String dateString = formatDateFromOnetoAnother(date, "yyyy-MM-dd", "MMMM yyyy");
        actionBar.setTitle(dateString.toUpperCase());

    }

    public String formatDateFromOnetoAnother(String date, String givenformat, String resultformat) {

        String result = "";
        SimpleDateFormat sdf;
        SimpleDateFormat sdf1;

        try {
            sdf = new SimpleDateFormat(givenformat);
            sdf1 = new SimpleDateFormat(resultformat);
            result = sdf1.format(sdf.parse(date));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            sdf = null;
            sdf1 = null;
        }
        return result;
    }

    /**
     * Intializin the pending intent for the tracking service
     *
     * @return Pending intent
     */
    private PendingIntent initializePendingIntent() {
        Intent trackingParamsIntent = new Intent(JobsListOnCurrentDate.this,
                TrackingParameterService.class);
        trackingParamsIntent.putExtra("from_pending_intent", true);

        PendingIntent pendingIntent;
        //Behaviour changes supporting android 12
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // Create a PendingIntent using FLAG_IMMUTABLE
            pendingIntent= PendingIntent.getService(JobsListOnCurrentDate.this,
                    0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT|
                            PendingIntent.FLAG_IMMUTABLE);
        } else {

            pendingIntent= PendingIntent.getService(JobsListOnCurrentDate.this,
                    0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return pendingIntent;

//        return PendingIntent.getService(JobsListOnCurrentDate.this,
//                0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    }


    /**
     * For canceling the Tracking service when conditions are met
     */
    private void cancelTrackingAlarm() {
        stopService(new Intent(JobsListOnCurrentDate.this, TrackingParameterService.class));
        AlarmManager am2 = (AlarmManager) JobsListOnCurrentDate.this
                .getSystemService(Context.ALARM_SERVICE);
        am2.cancel(pITrackParams);
    }


    @Override
    protected void onDestroy() {
        DialogUtils.dismissProgressDialog();
        super.onDestroy();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.calander_job_list_menu, menu);
        if (isAddIconBeShownFlag != 1) {
            menu.getItem(0).setVisible(false);
        }
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub

        switch (item.getItemId()) {
            case R.id.action_add_job:

//                Calendar cal = Calendar.getInstance();
//                int milisecond = ((cal.get(Calendar.HOUR_OF_DAY) * 3600000) + (cal
//                        .get(Calendar.MINUTE) * 60000));
//
//                int milisecondMaxtime = ((23 * 3600000) + (40 * 60000));
//                if (milisecond <= milisecondMaxtime) {
//
//                    JobsListOnCurrentDate.this.startActivity(new Intent(
//                            JobsListOnCurrentDate.this, NewJob.class));
//
//                } else {
//
//                    DialogUtils.showInfoDialog(JobsListOnCurrentDate.this,
//                            JobsListOnCurrentDate.this
//                                    .getString(R.string.textNoJobCreate));
//
//                }

                JobsListOnCurrentDate.this.startActivity(new Intent(
                        JobsListOnCurrentDate.this, NewJob.class));

                break;
            case R.id.action_sync:
                syncronise();

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onResume()
     */
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub

        super.onResume();

        //New changes
        DateChecker.checkDateAndNavigate(this, dataAceesObject);

        ((SyncroTeamApplication) getApplicationContext())
                .setSyncroteamAppLive(true);
        ((SyncroTeamApplication) getApplicationContext())
                .setSyncroteamActivityInstance(this);
        dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
        format = android.text.format.DateFormat.getTimeFormat(this);

        new FetchJobsOnCurrentDateAsyncTask().execute();

    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        ((SyncroTeamApplication) getApplicationContext())
                .setSyncroteamAppLive(false);
        ((SyncroTeamApplication) getApplicationContext())
                .setSyncroteamActivityInstance(null);

    }

    /**
     * Creates the and inflate db data in list.
     *
     * @return true, if successful
     */
    private boolean createAndInflateDbDataInList() {
        // TODO Auto-generated method stub

        if (allJobList != null) {
            allJobList.clear();
        } else {
            allJobList = new TreeMap<String, ArrayList<HashMap<String, String>>>(
                    new Comparator<String>() {

                        @Override
                        public int compare(String lhs, String rhs) {
                            // TODO Auto-generated method stub
                            return -lhs.compareTo(rhs);
                        }
                    });
        }

        HashMap<String, String> map = new HashMap<String, String>();

        Vector<CommonJobBean> vect = new Vector<CommonJobBean>();

        // vectConge = dataAceesObject.getConge();

        vect = dataAceesObject.getAllJobsOfParticularDate(date, user.getId());

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
            String dateToshow = null;
            if (st == KEYS.CurrentJobs.JOB__STARTED) {

                if (user.getId() != interv.getIdUser()) {
                    try {
                        dateToshow = dataAceesObject
                                .getDateWithRequiredPresettedPattern(date);
                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        Logger.printException(e);
                    }
                    if (!TextUtils.isEmpty(dateToshow)) {

                        assert dateToshow != null;
                        String[] dateTopass = dateToshow.split("/");
                        // String[] dateToShow = dateTopass[0].split("-");

                        // map.put(KEYS.CurrentJobs.MONTH_TO_SHOW,
                        // dateToShow[0]);
                        map.put(KEYS.CurrentJobs.DATE_TO_SHOW, dateTopass[0]);
                        // map.put(KEYS.CurrentJobs.YEAR_TO_SHOW,
                        // dateToShow[2]);
                        map.put(KEYS.CurrentJobs.TIME_TO_SHOW, dateTopass[1]);
                    }
                } else {

                    dateToshow = dataAceesObject
                            .getJobStartTime(interv.getId());
                    if (!TextUtils.isEmpty(dateToshow)) {

                        String[] dateTopass = dateToshow.split("/");
                        // String[] dateToShow = dateTopass[0].split("-");

                        // map.put(KEYS.CurrentJobs.MONTH_TO_SHOW,
                        // dateToShow[0]);
                        map.put(KEYS.CurrentJobs.DATE_TO_SHOW, dateTopass[0]);
                        // map.put(KEYS.CurrentJobs.YEAR_TO_SHOW,
                        // dateToShow[2]);
                        map.put(KEYS.CurrentJobs.TIME_TO_SHOW, dateTopass[1]);
                    }
                }

            } else if (st == KEYS.CurrentJobs.JOB__SUSPENDED) {

                dateToshow = dataAceesObject
                        .getJobSuspendedTime(interv.getId());
                if (!TextUtils.isEmpty(dateToshow)) {

                    dateToshow = dataAceesObject.getJobSuspendedTime(interv
                            .getId());
                    if (!TextUtils.isEmpty(dateToshow)) {

                        String[] dateTopass = dateToshow.split("/");

                        map.put(KEYS.CurrentJobs.DATE_TO_SHOW, dateTopass[0]);

                        map.put(KEYS.CurrentJobs.TIME_TO_SHOW, dateTopass[1]);
                    }

                }

            } else if (st == KEYS.CurrentJobs.JOB__COMPLETE) {
                map.put(KEYS.CurrentJobs.DATE_TO_SHOW, dateFormat.format(time2)
                        + "");

                map.put(KEYS.CurrentJobs.TIME_TO_SHOW, format.format(time2));
            } else if ((st == KEYS.CurrentJobs.JOB_NOT_STARTED1)
                    || (st == KEYS.CurrentJobs.JOB_NOT_STARTED2)) {
                map.put(KEYS.CurrentJobs.DATE_TO_SHOW, dateFormat.format(date)
                        + "");

                map.put(KEYS.CurrentJobs.TIME_TO_SHOW, format.format(date));
            }

            // map.put(KEYS.CurrentJobs.PLAN, formatDay.format(date) + " "
            // + df.format(date) + "  -  " + df1.format(time1) + " > "
            // + df1.format(time2));

            map.put(KEYS.CurrentJobs.MDATE1, interv.getDt_deb_prev());
            map.put(KEYS.CurrentJobs.MDATE2, interv.getDt_fin_prev());
            if (!TextUtils.isEmpty(interv.getDt_meeting())) {

                // Date dateMeeting = getDateFromDbFormat(interv
                // .getDt_meeting());
                // Date timeMeeting = getDateFromDbFormat(interv
                // .getDt_meeting());
                // map.put(KEYS.CurrentJobs.DATEMEETING,
                // formatDay.format(dateMeeting) + " "
                // + df.format(dateMeeting) + " "
                // + df1.format(timeMeeting));
            } else
                map.put(KEYS.CurrentJobs.DATEMEETING, "");

            map.put(KEYS.CurrentJobs.JOB_TYPE, st + "");

            if (allJobList
                    .containsKey(map.get(KEYS.CurrentJobs.ALL_JOB_HEADER))) {
                Objects.requireNonNull(allJobList.get(map.get(KEYS.CurrentJobs.ALL_JOB_HEADER))).add(
                        map);

            } else {
                allJobList.put(map.get(KEYS.CurrentJobs.ALL_JOB_HEADER),
                        new ArrayList<HashMap<String, String>>());
                Objects.requireNonNull(allJobList.get(map.get(KEYS.CurrentJobs.ALL_JOB_HEADER))).add(
                        map);
            }

        }

        return true;

    }

    /**
     * Creates the and notify adapter.
     */
    private void createAndNotifyAdapter() {

        if (allJobList.isEmpty()) {
            DialogUtils.showFinishDialog(this,
                    this.getString(R.string.textNoJobAvaliable));
            return;

        }

        /*
         * SimpleAdapter adapter = new SimpleAdapter (this.getBaseContext(),
         * listItem, R.layout.list_item_interv, new String[] {"img1", "type",
         * "cltVille","plan", "img2"}, new int[] {R.id.img1, R.id.text1 ,
         * R.id.text2 , R.id.text3 ,R.id.img2});
         */
        if (allJobsListAdapter == null) {
            allJobsListAdapter = new AllJobsListAdapter(this, allJobList);
            pinnedSectionListView.setAdapter(allJobsListAdapter);
        } else {
            allJobsListAdapter.notifyDataSetChanged();
        }

        pinnedSectionListView.removeFooterView(footerView);
        allJobsListAdapter.notifyDataSetChanged();

        EventBus.getDefault().post(new UpdateUiOnSync());

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
        } catch (Exception e) {
            Logger.printException(e);
            return new Date();
        }
    }

    /**
     * The Class FetchJobsOnCurrentDateAsyncTask.
     */
    private class FetchJobsOnCurrentDateAsyncTask extends
            AsyncTaskCoroutine<Void, Boolean> {

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        public void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            DialogUtils.showProgressDialog(JobsListOnCurrentDate.this,
                    JobsListOnCurrentDate.this
                            .getString(R.string.textWaitLable), "", false);

        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        public Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub
            return createAndInflateDbDataInList();
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        public void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            DialogUtils.dismissProgressDialog();

            createAndNotifyAdapter();

        }

    }

    /**
     * Syncronise with database.
     */
    protected void syncronise() {
        // TODO Auto-generated method stub

        if (SynchroteamUitls.isNetworkAvailable(JobsListOnCurrentDate.this)) {
            progressDSynch = ProgressDialog.show(this,
                    getString(R.string.textPleaseWaitLable),
                    getString(R.string.msg_synch), true, false);

            Thread syncDatabaseWithServer = new Thread((new Runnable() {
                @Override
                public void run() {

                    Message myMessage = new Message();
                    try {
                        dataAceesObject.sync(user.getLogin(), user.getPwd());

                        GestionAcces gt = dataAceesObject.getAcces();
                        if (gt != null && gt.getOptionTaracking() == 0) {
//                            stopService(new Intent(JobsListOnCurrentDate.this,
//                                                   TrackingService.class));
//                            stopService(new Intent(JobsListOnCurrentDate.this,
//                                                   TrackingServiceFrequency.class));
//                            AlarmManager am = (AlarmManager) JobsListOnCurrentDate.this
//                                    .getSystemService(Context.ALARM_SERVICE);
//                            am.cancel(pi);
//
//                            AlarmManager am1 = (AlarmManager) JobsListOnCurrentDate.this
//                                    .getSystemService(Context.ALARM_SERVICE);
//                            am1.cancel(pi1);

                            cancelTrackingAlarm();
                        }

                        myMessage.obj = "ok";
                        jobListOnCurrentDateHandler.sendMessage(myMessage);
                    } catch (Exception ex) {
                        String exception = ex.getMessage();
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
                        jobListOnCurrentDateHandler.sendMessage(myMessage);
                    } finally {
                        if (progressDSynch != null
                                && progressDSynch.isShowing())
                            progressDSynch.dismiss();
                    }
                }
            }));

            syncDatabaseWithServer.start();
        } else {
            SynchroteamUitls.showToastMessage(JobsListOnCurrentDate.this);
        }

    }

    /**
     * The handler.
     */
    @SuppressLint("HandlerLeak")
    private Handler jobListOnCurrentDateHandler = new Handler() {
        public void handleMessage(Message msg) {
            String statusCode = (String) msg.obj;
            if (statusCode.equals("ok")) {
                isAddIconBeShownFlag = dataAceesObject.getAddIntervFlag();

                if (isAddIconBeShownFlag != 1) {
                    menu.getItem(0).setVisible(false);
                } else {
                    menu.getItem(0).setVisible(true);
                }

                new FetchJobsOnCurrentDateAsyncTask().execute();

                Toast.makeText(JobsListOnCurrentDate.this,
                        getString(R.string.msg_synch_ok), Toast.LENGTH_LONG)
                        .show();

                EventBus.getDefault().post(new UpdateUiOnSync());

            } else if (statusCode.equals("4001") || statusCode.equals("4000")) {
                showAuthErrDialog();
            } else if (statusCode.equals("4006")) {
                Toast.makeText(JobsListOnCurrentDate.this,
                        getString(R.string.msg_synch_error_4),
                        Toast.LENGTH_LONG).show();
            } else if (statusCode.equals("4101")) {
                Toast.makeText(JobsListOnCurrentDate.this,
                        getString(R.string.msg_synch_error_2),
                        Toast.LENGTH_LONG).show();
            } else if (statusCode.equals("4005")) {
                Toast.makeText(JobsListOnCurrentDate.this,
                        getString(R.string.msg_synch_error_3),
                        Toast.LENGTH_LONG).show();
            } else if (statusCode.equals("4003")) {
                showErrMsgDialog(getString(R.string.msg_sync_error_4003));
            } else {
//                Toast.makeText(JobsListOnCurrentDate.this,
//                               getString(R.string.msg_synch_error_new), Toast.LENGTH_LONG)
//                        .show();
                showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));
            }
        }
    };

    /**
     * Show error dialog
     */
    protected void showErrMsgDialog(String errMsg) {

        ErrorDialog errDialog = new ErrorDialog(JobsListOnCurrentDate.this, errMsg);

        errDialog.show();
    }

    /**
     * For showing the synchronization failure messages
     */
    protected void showSyncFailureMsgDialog(String syncFailureMsg) {

        SynchronizationErrorDialog synchronizationErrorDialog = new SynchronizationErrorDialog
                (JobsListOnCurrentDate.this, syncFailureMsg, new SynchronizationErrorDialog
                        .SynchronizationErrorInterface() {
                    @Override
                    public void doOnOkayClick() {
                        //perform any action
                    }
                });

        synchronizationErrorDialog.show();
    }

    /**
     * Show authentication error dialog
     */
    protected void showAuthErrDialog() {

        AuthenticationErrorDialog authenticationErrorDialog = new AuthenticationErrorDialog(
                JobsListOnCurrentDate.this, user.getLogin());

        authenticationErrorDialog.show();
    }


    /*
     * (non-Javadoc)
     *
     * @see com.synchroteam.synchroteam.CommonInterface#getActivityInstance()
     */
    @Override
    public Activity getActivityInstance() {
        // TODO Auto-generated method stub
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.synchroteam.synchroteam.CommonInterface#updateUi()
     */
    @Override
    public void updateUi() {
        // TODO Auto-generated method stub
        new FetchJobsOnCurrentDateAsyncTask().execute();
    }
    @Override
    public void updateUiOnTrakingStatusChange(boolean isRunning) {

    }
}
