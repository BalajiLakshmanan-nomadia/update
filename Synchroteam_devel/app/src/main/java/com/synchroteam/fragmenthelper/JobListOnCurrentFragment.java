package com.synchroteam.fragmenthelper;

import static android.content.Context.LOCATION_SERVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.caldroidx.CaldroidFragment;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.CommonJobBean;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.beans.UpdateUiOnSync;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.AuthenticationErrorDialog;
import com.synchroteam.dialogs.ErrorDialog;
import com.synchroteam.dialogs.SynchronizationErrorDialog;
import com.synchroteam.fragment.BaseFragment;
import com.synchroteam.fragment.CalendarFragment;
import com.synchroteam.fragment.FragmentInterface;
import com.synchroteam.listadapters.AllJobsListAdapter;
import com.synchroteam.synchroteam.CommonInterface;
import com.synchroteam.synchroteam.JobsListOnCurrentDate;
import com.synchroteam.synchroteam.NewJob;
import com.synchroteam.synchroteam.SyncroTeamApplication;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.tracking.TrackingParameterService;
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

public class JobListOnCurrentFragment extends Fragment implements CommonInterface, FragmentInterface, View.OnClickListener{

    ListView pinnedSectionListView;

    /**
     * The months of year.
     */
    JobsListOnCurrentDate.MonthsOfYear[] monthsOfYear;

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

    @Override
    public Activity getActivityInstance() {
        return null;
    }

    @Override
    public void updateUi() {
        new FetchJobsOnCurrentDateAsyncTask().execute();
    }

    @Override
    public void updateUiOnTrakingStatusChange(boolean isRunning) {

    }

    @Override
    public boolean doOnBackPressed() {
        layBottomBar.setVisibility(View.GONE);
        calendarFragment.getChildFragmentManager().popBackStackImmediate();
        return true;
    }

    @Override
    public String getFragmentTag() {
        return null;
    }

    @Override
    public void doOnSync() {

    }

    @Override
    public void listUpdate() {

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iconNavigationDrawer:
                layBottomBar.setVisibility(View.GONE);
                calendarFragment.getChildFragmentManager().popBackStackImmediate();
                break;
            case R.id.addJobBtn:
                getActivity().startActivity(new Intent(
                        getActivity(), NewJob.class));
                break;
            case R.id.syncroniseJobBtn:
                syncronise();
                break;

        }
    }

    protected void syncronise() {
        // TODO Auto-generated method stub

        if (SynchroteamUitls.isNetworkAvailable(getActivity())) {
            progressDSynch = ProgressDialog.show(getActivity(),
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
            SynchroteamUitls.showToastMessage(getActivity());
        }

    }

    private void cancelTrackingAlarm() {
        getActivity().stopService(new Intent(getActivity(), TrackingParameterService.class));
        AlarmManager am2 = (AlarmManager) getActivity()
                .getSystemService(Context.ALARM_SERVICE);
        am2.cancel(pITrackParams);
    }

    protected void showAuthErrDialog() {

        AuthenticationErrorDialog authenticationErrorDialog = new AuthenticationErrorDialog(
                getActivity(), user.getLogin());

        authenticationErrorDialog.show();
    }

    protected void showErrMsgDialog(String errMsg) {

        ErrorDialog errDialog = new ErrorDialog(getActivity(), errMsg);

        errDialog.show();
    }

    protected void showSyncFailureMsgDialog(String syncFailureMsg) {

        SynchronizationErrorDialog synchronizationErrorDialog = new SynchronizationErrorDialog
                (getActivity(), syncFailureMsg, new SynchronizationErrorDialog
                        .SynchronizationErrorInterface() {
                    @Override
                    public void doOnOkayClick() {
                        //perform any action
                    }
                });

        synchronizationErrorDialog.show();
    }

    @SuppressLint("HandlerLeak")
     Handler jobListOnCurrentDateHandler = new Handler() {
        public void handleMessage(Message msg) {
            String statusCode = (String) msg.obj;
            if (statusCode.equals("ok")) {
                isAddIconBeShownFlag = dataAceesObject.getAddIntervFlag();

                if (isAddIconBeShownFlag != 1) {
//                    menu.getItem(0).setVisible(false);
                    addJobBtn.setVisibility(View.GONE);
                } else {
//                    menu.getItem(0).setVisible(true);
                    addJobBtn.setVisibility(View.VISIBLE);
                }

                new FetchJobsOnCurrentDateAsyncTask().execute();

                Toast.makeText(getActivity(),
                                getString(R.string.msg_synch_ok), Toast.LENGTH_LONG)
                        .show();

                EventBus.getDefault().post(new UpdateUiOnSync());

            } else if (statusCode.equals("4001") || statusCode.equals("4000")) {
                showAuthErrDialog();
            } else if (statusCode.equals("4006")) {
                Toast.makeText(getActivity(),
                        getString(R.string.msg_synch_error_4),
                        Toast.LENGTH_LONG).show();
            } else if (statusCode.equals("4101")) {
                Toast.makeText(getActivity(),
                        getString(R.string.msg_synch_error_2),
                        Toast.LENGTH_LONG).show();
            } else if (statusCode.equals("4005")) {
                Toast.makeText(getActivity(),
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

    TextView titleCurrentScreen;

    RelativeLayout layoutTitleBar;

    RelativeLayout layBottomBar;

    ImageView iconNavigationDrawer,addJobBtn,syncroniseJobBtn;
     CalendarFragment calendarFragment;

    public JobListOnCurrentFragment(String date,
                                    CalendarFragment calendarFragment,RelativeLayout layoutTitleBar, RelativeLayout layBottomBar) {
        this.date = date;
        this.layoutTitleBar = layoutTitleBar;
        this.layBottomBar = layBottomBar;
        this.calendarFragment = calendarFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater
                .inflate(R.layout.layout_joblist, container, false);
        dataAceesObject = DaoManager.getInstance();

        user = dataAceesObject.getUser();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String dateString = formatDateFromOnetoAnother(date, "yyyy-MM-dd", "MMMM yyyy");

        Log.e("title",dateString);

        titleCurrentScreen = view.findViewById(R.id.titleCurrentScreen);
        titleCurrentScreen.setText(dateString.toUpperCase());

        pinnedSectionListView = (ListView) view.findViewById(R.id.jobListLv);
        iconNavigationDrawer = view.findViewById(R.id.iconNavigationDrawer);
        addJobBtn = view.findViewById(R.id.addJobBtn);
        syncroniseJobBtn = view.findViewById(R.id.syncroniseJobBtn);

        addJobBtn.setOnClickListener(this);
        iconNavigationDrawer.setOnClickListener(this);
        syncroniseJobBtn.setOnClickListener(this);

        pITrackParams = initializePendingIntent();

        monthsOfYear = JobsListOnCurrentDate.MonthsOfYear.values();

        footerView = ((LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
                R.layout.layout_footerview_items_list, null, false);

        pinnedSectionListView.addFooterView(footerView);

        isAddIconBeShownFlag = dataAceesObject.getAddIntervFlag();
    }


    private PendingIntent initializePendingIntent() {
        Intent trackingParamsIntent = new Intent(getActivity(),
                TrackingParameterService.class);
        trackingParamsIntent.putExtra("from_pending_intent", true);

        PendingIntent pendingIntent;
        //Behaviour changes supporting android 12
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // Create a PendingIntent using FLAG_IMMUTABLE
            pendingIntent= PendingIntent.getService(getActivity(),
                    0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT|
                            PendingIntent.FLAG_IMMUTABLE);
        } else {

            pendingIntent= PendingIntent.getService(getActivity(),
                    0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return pendingIntent;

//        return PendingIntent.getService(JobsListOnCurrentDate.this,
//                0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    }


    @Override
    public void onPause() {
        super.onPause();
        layoutTitleBar.setVisibility(View.VISIBLE);

        SyncroTeamApplication.getAppContext().setSyncroteamAppLive(false);
        SyncroTeamApplication.getAppContext().setSyncroteamActivityInstance(null);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DialogUtils.dismissProgressDialog();
    }

    @Override
    public void onResume() {
        super.onResume();
        layBottomBar.setVisibility(View.VISIBLE);
        layoutTitleBar.setVisibility(View.GONE);

        DateChecker.checkDateAndNavigate(getActivity(), dataAceesObject);

        SyncroTeamApplication.getAppContext().setSyncroteamAppLive(true);
        SyncroTeamApplication.getAppContext().setSyncroteamActivityInstance(this);

        dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
        format = android.text.format.DateFormat.getTimeFormat(getActivity());

        new FetchJobsOnCurrentDateAsyncTask().execute();
    }

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
            DialogUtils.showProgressDialog(getActivity(),
                    getActivity()
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

    private void createAndNotifyAdapter() {

        if (allJobList.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getActivity().getString(R.string.textNoJobAvaliable));
            builder.setCancelable(false);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    calendarFragment.getChildFragmentManager().popBackStackImmediate();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }



//        if (allJobList.isEmpty()) {
//            DialogUtils.showFinishDialog(getActivity(),
//                    getActivity().getString(R.string.textNoJobAvaliable));
//            return;
//
//        }

        /*
         * SimpleAdapter adapter = new SimpleAdapter (this.getBaseContext(),
         * listItem, R.layout.list_item_interv, new String[] {"img1", "type",
         * "cltVille","plan", "img2"}, new int[] {R.id.img1, R.id.text1 ,
         * R.id.text2 , R.id.text3 ,R.id.img2});
         */
        if (allJobsListAdapter == null) {
            allJobsListAdapter = new AllJobsListAdapter(getActivity(), allJobList);
            pinnedSectionListView.setAdapter(allJobsListAdapter);
        } else {
            allJobsListAdapter.notifyDataSetChanged();
        }

        pinnedSectionListView.removeFooterView(footerView);
        allJobsListAdapter.notifyDataSetChanged();

        EventBus.getDefault().post(new UpdateUiOnSync());

    }


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


}
