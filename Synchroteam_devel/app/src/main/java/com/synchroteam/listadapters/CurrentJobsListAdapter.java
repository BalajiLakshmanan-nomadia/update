package com.synchroteam.listadapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.SwipeLayout.DragEdge;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.dao.Dao;
import com.synchroteam.fragment.BaseFragment;
import com.synchroteam.fragmenthelper.CurrentJobsFragmentHelper;
import com.synchroteam.fragmenthelper.CurrentJobsFragmentHelper.RefreshListener;
import com.synchroteam.synchroteam.UpdateUnavailability;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.technicalsupport.JobDetails;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

/**
 * Adapter Class to infalate current job list.
 *
 * @author Ideavate.solution
 */
public class CurrentJobsListAdapter extends BaseAdapter implements
        RefreshListener {

    /**
     * The activity.
     */
    private Activity activity;

    /**
     * The current jobs beans.
     */
    private TreeMap<String, ArrayList<HashMap<String, String>>> currentJobList;

    /**
     * The key set current job.
     */
    private ArrayList<String> keySetCurrentJob;

    /**
     * The layout inflater.
     */
    private LayoutInflater layoutInflater;

    /**
     * The base fragment.
     */
    private BaseFragment baseFragment;

    /**
     * The old date pattern.
     */
    private SimpleDateFormat oldDatePattern;

    /**
     * The header date format.
     */
    private SimpleDateFormat headerDateFormat;

    private String started, suspended, scheduled;

    private int startedTextColor, suspendedTextColor, scheduledTextColor;

    // private static final String TAG = "CurrentJobsListAdapter";

    private Dao dataAccessObject;

    CurrentJobsFragmentHelper frag;

	/*
     * Instantiates a new current jobs list adapter.
	 * 
	 * @param activity the activity
	 * 
	 * @param currentJobList the current job list
	 */

    /**
     * Instantiates a new current jobs list adapter .
     *
     * @param activity       the activity
     * @param currentJobList the current job list
     * @param baseFragment   the base fragment
     */
    @SuppressLint("SimpleDateFormat")
    public CurrentJobsListAdapter(Activity activity,
                                  TreeMap<String, ArrayList<HashMap<String, String>>> currentJobList,
                                  BaseFragment baseFragment, CurrentJobsFragmentHelper frag) {
        this.activity = activity;
        this.currentJobList = currentJobList;
        this.baseFragment = baseFragment;
        Set<String> strings = currentJobList.keySet();
        keySetCurrentJob = new ArrayList<>(strings);
        headerDateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        oldDatePattern = new SimpleDateFormat("yyyy-MM-dd");
        layoutInflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        dataAccessObject = DaoManager.getInstance();

        started = activity.getString(R.string.textStarted);
        suspended = activity.getString(R.string.textSuspended);
        scheduled = activity.getString(R.string.textScheduled);

        startedTextColor = activity.getResources().getColor(
                R.color.textColorGreen);
        suspendedTextColor = activity.getResources().getColor(
                R.color.textColorOrange);
        scheduledTextColor = activity.getResources().getColor(
                R.color.textColorBlack);

        this.frag = frag;

    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        Logger.log("Current JobList getCount", keySetCurrentJob.size() + "");
        return keySetCurrentJob.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public ArrayList<HashMap<String, String>> getItem(int position) {
        return currentJobList.get(keySetCurrentJob.get(position));
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @SuppressWarnings("deprecation")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // /** The left strip iv. */
        ImageView leftStripIv;
        //
        // /** The job status iv. */
        TextView jobStatusIv;
        //
        // /** The job name tv. */
        TextView jobNameTv;
        //
        // /** The date or time tv. */
        TextView dateOrTimeTv;

        //
        // /** The job priority tv. */
        TextView jobPriorityTv;
        //
        // /** The client name tv. */
        TextView clientNameTv;
        TextView startTimeTv;
        // /** The job time status container. */
        RelativeLayout jobTimeStatusContainer;
        TextView dateDetailCurrentJobsTv;
        ImageView imgDividerLine;

        View view = layoutInflater.inflate(
                R.layout.layout_current_jobs_list_item, parent,false);
        LinearLayout containerJobList = (LinearLayout) view
                .findViewById(R.id.containerLinearlayout);

        dateDetailCurrentJobsTv = (TextView) view
                .findViewById(R.id.dateDetailCurrentJobsTv);
        imgDividerLine = (ImageView) view.findViewById(R.id.imgDateDivider);
        dateDetailCurrentJobsTv.setOnClickListener(null);
        ArrayList<HashMap<String, String>> currentJobHashmapList = getItem(position);

        dateDetailCurrentJobsTv.setVisibility(View.VISIBLE);
        imgDividerLine.setVisibility(View.VISIBLE);
        try {
            dateDetailCurrentJobsTv
                    .setText(headerDateFormat.format(oldDatePattern
                            .parse(keySetCurrentJob.get(position))));
        } catch (ParseException e) {
            Logger.printException(e);
        }

        Iterator<HashMap<String, String>> iterator = currentJobHashmapList
                .iterator();
        while (iterator.hasNext()) {

            HashMap<String, String> currentJobHashMap = iterator.next();
            View subItem;
            if (currentJobHashMap.get(KEYS.CurrentJobs.IS_CURRENT_JOB).equals(
                    KEYS.CurrentJobs.TRUE)) {

                subItem = layoutInflater.inflate(
                        R.layout.layout_current_job_item, parent,false);

                subItem.setTag(currentJobHashMap);
                leftStripIv = (ImageView) subItem
                        .findViewById(R.id.stripColorIv);
                jobStatusIv = (TextView) subItem.findViewById(R.id.jobStatusTv);

                jobNameTv = (TextView) subItem.findViewById(R.id.jobNameTv);
                dateOrTimeTv = (TextView) subItem
                        .findViewById(R.id.dateOrTimeTv);
                // yearTv = (TextView) view.findViewById(R.id.yearTv);
                jobPriorityTv = (TextView) subItem
                        .findViewById(R.id.jobPriorityTv);
                clientNameTv = (TextView) subItem
                        .findViewById(R.id.clientNameTv);
                startTimeTv = (TextView) subItem.findViewById(R.id.startTime);
                jobTimeStatusContainer = (RelativeLayout) subItem
                        .findViewById(R.id.jobTimeStatusRl);

                jobNameTv.setText(currentJobHashMap.get(KEYS.CurrentJobs.TYPE));
                clientNameTv.setText(currentJobHashMap
                        .get(KEYS.CurrentJobs.NOM_CLIENT_INTERV));

                int status = Integer.parseInt(currentJobHashMap
                        .get(KEYS.CurrentJobs.CD_STATUS));
                jobPriorityTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,
                        0);

                switch (status) {
                    case KEYS.CurrentJobs.JOB_NOT_STARTED2:
                    case KEYS.CurrentJobs.JOB_NOT_STARTED1:
                        leftStripIv
                                .setBackgroundResource(R.color.blackStripBackgroundCurrentJobs);
                        jobStatusIv.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.schedule_icon, 0, 0, 0);
                        jobStatusIv.setText(scheduled);
                        jobStatusIv.setTextColor(scheduledTextColor);
                        jobTimeStatusContainer
                                .setBackgroundResource(R.color.completedOrNotStartedJobsBackgroundCurrentJobs);
                        https:
                        // www.google.co.in/?gfe_rd=cr&ei=5wNzVPjCIpGGvASn94DgAg&gws_rd=ssl#q=perspective

                        dateOrTimeTv.setText(currentJobHashMap
                                .get(KEYS.CurrentJobs.DATE_TO_SHOW) + " ");
                        // yearTv.setVisibility(View.VISIBLE);
                        // yearTv.setText(currentJobHashmap.get(KEYS.CurrentJobs.YEAR_TO_SHOW));
                        Logger.log(">>>>>", currentJobHashMap
                                .get(KEYS.CurrentJobs.TIME_TO_SHOW));
                        startTimeTv.setText(currentJobHashMap
                                .get(KEYS.CurrentJobs.TIME_TO_SHOW));
                        break;

                    case KEYS.CurrentJobs.JOB__STARTED:
                        leftStripIv
                                .setBackgroundResource(R.color.greenStripBackgroundCurrentJobs);
                        jobStatusIv.setVisibility(View.VISIBLE);
                        jobStatusIv.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.started_btn, 0, 0, 0);
                        jobStatusIv.setText(started);
                        jobStatusIv.setTextColor(startedTextColor);
                        jobTimeStatusContainer
                                .setBackgroundResource(R.color.startedJobsBackgroundCurrentJobs);
                        dateOrTimeTv.setText(currentJobHashMap
                                .get(KEYS.CurrentJobs.DATE_TO_SHOW) + " ");
                        // yearTv.setText(currentJobHashmap.get(KEYS.CurrentJobs.YEAR_TO_SHOW));

                        startTimeTv.setText(currentJobHashMap
                                .get(KEYS.CurrentJobs.TIME_TO_SHOW));
                        break;
                    case KEYS.CurrentJobs.JOB__SUSPENDED:
                        leftStripIv
                                .setBackgroundResource(R.color.orangeStripBackgroundCurrentJobs);
                        jobStatusIv.setVisibility(View.VISIBLE);
                        jobTimeStatusContainer
                                .setBackgroundResource(R.color.suspendedJobsBackgroundCurrentJobs);
                        jobStatusIv.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.suspended_btn, 0, 0, 0);
                        jobStatusIv.setText(suspended);
                        jobStatusIv.setTextColor(suspendedTextColor);
                        if (!TextUtils.isEmpty(currentJobHashMap
                                .get(KEYS.CurrentJobs.DATE_TO_SHOW))) {
                            dateOrTimeTv.setText(currentJobHashMap
                                    .get(KEYS.CurrentJobs.DATE_TO_SHOW) + " ");
                        }

                        startTimeTv.setText(currentJobHashMap
                                .get(KEYS.CurrentJobs.TIME_TO_SHOW));
                        break;

                    default:
                        break;
                }

                int jobPrority = Integer.parseInt(currentJobHashMap
                        .get(KEYS.CurrentJobs.PRIORITY));

                switch (jobPrority) {
                    case KEYS.CurrentJobs.PRIORITY_HIGH:
                        jobPriorityTv.setText(activity.getResources().getString(
                                R.string.textHighPriorityTv).toUpperCase());
                        jobPriorityTv.setTextColor(activity.getResources()
                                .getColor(R.color.textHighPriorityCurrentJobs));

                        break;
                    case KEYS.CurrentJobs.PRIORITY_NORMAL:
                        jobPriorityTv.setText(activity.getResources().getString(
                                R.string.textAveragePriorityTv).toUpperCase());
                        jobPriorityTv.setTextColor(activity.getResources()
                                .getColor(R.color.textNormalPrioriyCurrentJobs));

                        // jobPriorityTv.setTextColor(R.color.textColo)

                        break;
                    case KEYS.CurrentJobs.PRIORITY_LOW:
                        jobPriorityTv.setText(activity.getResources().getString(
                                R.string.textLowPriorityTv).toUpperCase());
                        jobPriorityTv.setTextColor(activity.getResources()
                                .getColor(R.color.textLowPrioriyCurrentJobs));

                        break;
                    default:
                        break;
                }

                subItem.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        @SuppressWarnings("unchecked")
                        HashMap<String, String> currentJobHashMap = (HashMap<String, String>) v
                                .getTag();

                        if (!TextUtils.isEmpty(currentJobHashMap
                                .get(KEYS.CurrentJobs.DISPO))) {
                            if (!currentJobHashMap.get(KEYS.CurrentJobs.DISPO)
                                    .equals(KEYS.CurrentJobs.TRUE)) {
                                Bundle jobDetailBundle = new Bundle();

                                jobDetailBundle.putString(KEYS.CurrentJobs.ID,
                                        currentJobHashMap
                                                .get(KEYS.CurrentJobs.ID));

                                jobDetailBundle.putInt(
                                        KEYS.CurrentJobs.CD_STATUS,
                                        Integer.parseInt(currentJobHashMap
                                                .get(KEYS.CurrentJobs.CD_STATUS)));
                                jobDetailBundle.putString(
                                        KEYS.CurrentJobs.ID_USER,
                                        currentJobHashMap
                                                .get(KEYS.CurrentJobs.ID_USER));

                                jobDetailBundle
                                        .putString(
                                                KEYS.CurrentJobs.ADR_GLOBAL,
                                                currentJobHashMap
                                                        .get(KEYS.CurrentJobs.ADR_GLOBAL));

                                jobDetailBundle
                                        .putString(
                                                KEYS.CurrentJobs.ID_MODEL,
                                                currentJobHashMap
                                                        .get(KEYS.CurrentJobs.ID_MODEL));
                                jobDetailBundle.putString(KEYS.CurrentJobs.LAT,
                                        currentJobHashMap
                                                .get(KEYS.CurrentJobs.LAT));
                                jobDetailBundle.putString(KEYS.CurrentJobs.LON,
                                        currentJobHashMap
                                                .get(KEYS.CurrentJobs.LON));

                                jobDetailBundle.putString(
                                        KEYS.CurrentJobs.NOMSITE,
                                        currentJobHashMap.get("nomSite"));
                                jobDetailBundle
                                        .putString(
                                                KEYS.CurrentJobs.NOMEQUIPMENT,
                                                currentJobHashMap
                                                        .get(KEYS.CurrentJobs.NOMEQUIPMENT));
                                jobDetailBundle.putInt(KEYS.CurrentJobs.IDSITE,
                                        Integer.valueOf(currentJobHashMap
                                                .get(KEYS.CurrentJobs.IDSITE)));
                                jobDetailBundle.putInt(
                                        KEYS.CurrentJobs.IDCLIENT,
                                        Integer.valueOf(currentJobHashMap
                                                .get(KEYS.CurrentJobs.IDCLIENT)));
                                jobDetailBundle.putInt(
                                        KEYS.CurrentJobs.IDEQUIPMENT,
                                        Integer.valueOf(currentJobHashMap
                                                .get(KEYS.CurrentJobs.IDEQUIPMENT)));

                                jobDetailBundle.putString(
                                        KEYS.CurrentJobs.TYPE,
                                        currentJobHashMap
                                                .get(KEYS.CurrentJobs.TYPE));

                                jobDetailBundle.putString(
                                        KEYS.CurrentJobs.DT_CREATED,
                                        currentJobHashMap
                                                .get(KEYS.CurrentJobs.DT_CREATED));
                                jobDetailBundle.putBoolean(KEYS.CurrentJobs.IDSTARTJOB,false);

                                // closeDb();
                                Intent jobDetailIntent = new Intent(activity,
                                        JobDetails.class);
                                jobDetailIntent.putExtras(jobDetailBundle);
                                baseFragment.startActivity(jobDetailIntent);
                            }

                        }
                    }
                });
            } else {
                subItem = layoutInflater.inflate(
                        R.layout.layout_unavabilities_list_item, parent,false);
                SwipeLayout swipeLayout = (SwipeLayout) subItem
                        .findViewById(R.id.unavailabilitySwipeLayout);

                // set show mode.
                swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

                TextView nameUnavvabilityTv = (TextView) subItem
                        .findViewById(R.id.nameUnavability);
                TextView stopUnavailability = (TextView) subItem
                        .findViewById(R.id.txtStopUnavailability);

                stopUnavailability.setTag(currentJobHashMap);
                nameUnavvabilityTv.setText(currentJobHashMap
                        .get(KEYS.Unavabilities.TYPE));

                /** NEW CHANGES **/
                LinearLayout linUnavailabilityContainer = (LinearLayout) subItem
                        .findViewById(R.id.linUnavailabilityContainer);
                linUnavailabilityContainer.setTag(currentJobHashMap);

                GradientDrawable gd = (GradientDrawable) linUnavailabilityContainer
                        .getBackground();
                gd.setColor(Color.parseColor("#"
                        + currentJobHashMap.get(KEYS.Unavabilities.IMG)));
                gd.invalidateSelf();

                /** NEW CHANGES **/

                String selectedDate = currentJobHashMap
                        .get(KEYS.Unavabilities.SELECTED_DATE);
                String endDate = currentJobHashMap
                        .get(KEYS.Unavabilities.END_DATE);

                if (endDate == null) {
                    // swipeLayout.setBottomViewIds(0,
                    // R.id.bottom_wrapper_right,
                    // 0, 0);
                    swipeLayout.setSwipeEnabled(true);
                    swipeLayout.setDragEdge(DragEdge.Left);
                    swipeLayout.setDragEdge(DragEdge.Right);
                } else {
                    // swipeLayout.setBottomViewIds(0, 0, 0, 0);
                    swipeLayout.setSwipeEnabled(false);
                }

                stopUnavailability.setOnClickListener(new OnClickListener() {

                    @SuppressLint("SimpleDateFormat")
                    @Override
                    public void onClick(View v) {

                        @SuppressWarnings("unchecked")
                        HashMap<String, String> currentJobHashMap = (HashMap<String, String>) v
                                .getTag();
                        Calendar cal = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat(
                                "yyyy-MM-dd HH:mm:ss.SSS");
                        String currentDateStr = sdf.format(cal.getTime());

                        new endUnavailabilityAsync().execute(currentJobHashMap
                                        .get(KEYS.Unavabilities.UNAVAILABILITY_ID),
                                currentDateStr);
                    }
                });
                linUnavailabilityContainer
                        .setOnClickListener(new OnClickListener() {

                            @SuppressLint("NewApi")
                            @Override
                            public void onClick(View v) {

                                // ...........DIALOG...FOR...UNAVAILABILITY...DETAILS...STARTS............
                                @SuppressWarnings("unchecked")
                                HashMap<String, String> currentJobHashMap = (HashMap<String, String>) v
                                        .getTag();

                                String startEndTime = currentJobHashMap
                                        .get(KEYS.Unavabilities.PLAN_TIME_START_END);

								/*
                                 * Logic to find out the fourth - symbol in the
								 * date string.
								 */
//								int counter = 0;
//								int pos = 0;
//								for (int i = 0; i < startEndTime.length(); i++) {
//									if (startEndTime.charAt(i) == '-') {
//										counter++;
//										if (counter == 3) {
//											pos = i;
//										}
//									}
//								}
//
//								String startDateTime = startEndTime.substring(
//										0, pos);
//								String endDateTime = startEndTime
//										.substring(pos + 2);

                                String startDateTime = startEndTime.substring(0, startEndTime.indexOf("-"));
                                String endDateTime = startEndTime.substring(startEndTime.indexOf("-") + 2);

                                String startDate = startDateTime.substring(0,
                                        startDateTime.indexOf(" "));
                                String startTime = startDateTime
                                        .substring(startDateTime.indexOf(" ") + 1);

                                String endDate = null;
                                String endTime = null;

                                if (!StringUtils.isEmptyString(endDateTime)) {
                                    endDate = endDateTime.substring(0,
                                            endDateTime.indexOf(" ") + 1);
                                    endTime = endDateTime.substring(endDateTime
                                            .indexOf(" ") + 1);

                                }

                                String planStartDate = currentJobHashMap
                                        .get(KEYS.Unavabilities.PLAN_START_DATE_TIME);
                                String planEndDate = currentJobHashMap
                                        .get(KEYS.Unavabilities.PLAN_END_DATE_TIME);


                                    Intent intentUpdateUnavail = new Intent();

                                    Bundle bundle = new Bundle();
                                    intentUpdateUnavail.setClass(activity,
                                            UpdateUnavailability.class);
                                    bundle.putString(
                                            "id",
                                            currentJobHashMap
                                                    .get(KEYS.Unavabilities.UNAVAILABILITY_ID));
                                    bundle.putString("color_code",
                                            currentJobHashMap
                                                    .get(KEYS.Unavabilities.IMG));
                                    bundle.putString(KEYS.Unavabilities.TYPE,
                                            currentJobHashMap
                                                    .get(KEYS.Unavabilities.TYPE));
                                    bundle.putString("start_date", startDate);
                                    bundle.putString("start_time", startTime);
                                    bundle.putString("end_date", endDate);
                                    bundle.putString("end_time", endTime);
                                    bundle.putString("start_date_time",
                                            planStartDate);
                                    bundle.putString("end_date_time", planEndDate);
                                    bundle.putString(
                                            "description",
                                            currentJobHashMap
                                                    .get(KEYS.Unavabilities.CLTVILLE));

                                    bundle.putString(KEYS.Unavabilities.ID_USER, currentJobHashMap.get(KEYS.Unavabilities.ID_USER));
                                    bundle.putString(KEYS.Unavabilities.ID_GROUPE, currentJobHashMap.get(KEYS.Unavabilities.ID_GROUPE));
                                    intentUpdateUnavail.putExtras(bundle);
                                    activity.startActivity(intentUpdateUnavail);

                            }
                        });

                TextView startTimeUnavabilityTv = (TextView) subItem
                        .findViewById(R.id.startTimeUnavabilityTv);
                TextView endTimeUnavabilityTv = (TextView) subItem
                        .findViewById(R.id.EndTimeUnavabilityTv);

                android.widget.TextView startIcon = (android.widget.TextView) subItem
                        .findViewById(R.id.txtStartTimeIcon);
                android.widget.TextView endIcon = (android.widget.TextView) subItem
                        .findViewById(R.id.txtEndTimeIcon);

                Typeface typeFace = Typeface.createFromAsset(
                        activity.getAssets(), "fonts/fontawesome-webfont.ttf");
                startIcon.setTypeface(typeFace);
                endIcon.setTypeface(typeFace);

                if (TextUtils.isEmpty(endDate)) {
                    endIcon.setVisibility(View.GONE);
                }
                String startEndTime = currentJobHashMap
                        .get(KEYS.Unavabilities.PLAN_TIME_START_END);
                /*
				 * Logic to find out the fourth - symbol in the date string.
				 */
//				int counter = 0;
//				int pos = 0;
//				for (int i = 0; i < startEndTime.length(); i++) {
//					if (startEndTime.charAt(i) == '-') {
//						counter++;
//						if (counter == 3) {
//							pos = i;
//						}
//					}
//				}

//				String startTime = startEndTime.substring(0, pos);
//				String endTime = startEndTime.substring(pos + 1);

                String startTime = startEndTime.substring(0, startEndTime.indexOf("-"));
                String endTime = startEndTime.substring(startEndTime.indexOf("-") + 2);

                startTimeUnavabilityTv.setText(startTime);
                endTimeUnavabilityTv.setText(endTime);
                final TextView discriptionUnavabilityTv = (TextView) subItem
                        .findViewById(R.id.discriptionUnavabilityTv);
                discriptionUnavabilityTv.setText(currentJobHashMap
                        .get(KEYS.Unavabilities.CLTVILLE));

                if (!TextUtils.isEmpty(endDate)) {

                    SimpleDateFormat formatter = new SimpleDateFormat(
                            "dd/MM/yyyy HH:mm:ss");
                    try {
                        Date dateSel = formatter.parse(selectedDate);
                        Date dateEnd = formatter.parse(endDate);

                        if (dateEnd.compareTo(dateSel) < 0) {
                            subItem.setVisibility(View.GONE);
                            dateDetailCurrentJobsTv.setVisibility(View.GONE);
                            imgDividerLine.setVisibility(View.GONE);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }

            }

            containerJobList.addView(subItem);

        }

        return view;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.BaseAdapter#notifyDataSetChanged()
     */
    @Override
    public void notifyDataSetChanged() {
        try {
            keySetCurrentJob.clear();
            keySetCurrentJob.addAll(currentJobList.keySet());
        } catch (Exception e) {
            Logger.printException(e);
        }
        super.notifyDataSetChanged();
    }

    /**
     * The Class SaveNewJobDataAsyncTask.
     */
    private class endUnavailabilityAsync extends
            AsyncTaskCoroutine<String, Boolean> {

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        public void onPreExecute() {
            super.onPreExecute();

            DialogUtils.showProgressDialog(activity,
                    activity.getString(R.string.textWaitLable),
                    activity.getString(R.string.textSavingAddJobData), false);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @SuppressLint("SimpleDateFormat")
        @Override
        public Boolean doInBackground(String... params) {

            boolean drp = dataAccessObject.updateUnavailabilityEndDate(
                    params[0], params[1]);
            return drp;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        public void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            DialogUtils.dismissProgressDialog();

            boolean drp = result.booleanValue();
            if (drp) {
                Toast.makeText(activity,
                        activity.getString(R.string.unavailability_stopped),
                        Toast.LENGTH_SHORT).show();
                frag.refreshList();
            } else
                Toast.makeText(activity, R.string.msg_error, Toast.LENGTH_SHORT)
                        .show();
        }

    }

    @Override
    public void refreshList() {

    }

}
