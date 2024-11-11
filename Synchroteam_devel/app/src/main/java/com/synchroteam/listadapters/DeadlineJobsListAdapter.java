package com.synchroteam.listadapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.fragment.BaseFragment;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.technicalsupport.JobDetails;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;

// TODO: Auto-generated Javadoc

/**
 * Adapter Class to for Deadline Jobs List.
 */
public class DeadlineJobsListAdapter extends BaseAdapter {

    /**
     * The activity.
     */
    private Activity activity;

    /**
     * The deadline job beans.
     */
    private TreeMap<String, ArrayList<HashMap<String, String>>> dedlineExceededJobList;

    /**
     * The layout inflater.
     */
    private LayoutInflater layoutInflater;

    /**
     * The key set deadline exceeded job.
     */
    private ArrayList<String> keySetDeadlineExceededJob;

    /**
     * The base fragment.
     */
    private BaseFragment baseFragment;

    /**
     * The old date pattern.
     */
    private SimpleDateFormat headerDateFormat, oldDatePattern;


    private String started, suspended, scheduled;

    private int startedTextColor, suspendedTextColor, scheduledTextColor;


    /**
     * Instantiates a new deadline jobs list adapter.
     *
     * @param activity               the activity
     * @param dedlineExceededJobList the dedline exceeded job list
     * @param baseFragment           the base fragment
     */
    public DeadlineJobsListAdapter(
            Activity activity,
            TreeMap<String, ArrayList<HashMap<String, String>>> dedlineExceededJobList,
            BaseFragment baseFragment) {
        // TODO Auto-generated constructor stub
        this.activity = activity;
        this.dedlineExceededJobList = dedlineExceededJobList;
        keySetDeadlineExceededJob = new ArrayList<String>(
                dedlineExceededJobList.keySet());
        this.baseFragment = baseFragment;
        headerDateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        oldDatePattern = new SimpleDateFormat("yyyy-MM-dd");
        layoutInflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        started = activity.getString(R.string.textStarted);
        suspended = activity.getString(R.string.textSuspended);
        scheduled = activity.getString(R.string.textScheduled);

        startedTextColor = activity.getResources().getColor(R.color.textColorGreen);
        suspendedTextColor = activity.getResources().getColor(R.color.textColorOrange);
        scheduledTextColor = activity.getResources().getColor(R.color.textColorBlack);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return keySetDeadlineExceededJob.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public ArrayList<HashMap<String, String>> getItem(int position) {
        // TODO Auto-generated method stub
        return dedlineExceededJobList.get(keySetDeadlineExceededJob
                .get(position));
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
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
        //

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
        if (convertView == null) {
            convertView = layoutInflater.inflate(
                    R.layout.layout_deadline_jobs_list_item, parent, false);
            LinearLayout containerJobList = (LinearLayout) convertView
                    .findViewById(R.id.containerLinearlayout);

            dateDetailCurrentJobsTv = (TextView) convertView
                    .findViewById(R.id.dateDetailCurrentJobsTv);

            ArrayList<HashMap<String, String>> currentJobHashmapList = getItem(position);

            dateDetailCurrentJobsTv.setVisibility(View.VISIBLE);
            try {
                dateDetailCurrentJobsTv.setText(headerDateFormat
                        .format(oldDatePattern.parse(keySetDeadlineExceededJob
                                .get(position))));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                Logger.printException(e);
            }

            Iterator<HashMap<String, String>> iterator = currentJobHashmapList
                    .iterator();
            while (iterator.hasNext()) {
                View subItem = layoutInflater.inflate(
                        R.layout.layout_dedline_excceded_item, parent, false);
                HashMap<String, String> currentJobHashMap = iterator.next();
                subItem.setTag(currentJobHashMap);
                leftStripIv = (ImageView) subItem.findViewById(R.id.stripColorIv);
                jobStatusIv = (TextView) subItem.findViewById(R.id.jobStatusTv);

                jobNameTv = (TextView) subItem.findViewById(R.id.jobNameTv);
                dateOrTimeTv = (TextView) subItem.findViewById(R.id.dateOrTimeTv);
                // yearTv = (TextView) view.findViewById(R.id.yearTv);
                jobPriorityTv = (TextView) subItem.findViewById(R.id.jobPriorityTv);
                clientNameTv = (TextView) subItem.findViewById(R.id.clientNameTv);
                startTimeTv = (TextView) subItem.findViewById(R.id.startTime);
                jobTimeStatusContainer = (RelativeLayout) subItem
                        .findViewById(R.id.jobTimeStatusRl);

                jobNameTv.setText(currentJobHashMap.get(KEYS.CurrentJobs.TYPE));
                clientNameTv.setText(currentJobHashMap
                        .get(KEYS.CurrentJobs.NOM_CLIENT_INTERV));

                int status = Integer.parseInt(currentJobHashMap
                        .get(KEYS.CurrentJobs.CD_STATUS));
                jobPriorityTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                switch (status) {
                    case KEYS.CurrentJobs.JOB_NOT_STARTED2:
                    case KEYS.CurrentJobs.JOB_NOT_STARTED1:
                        leftStripIv
                                .setBackgroundResource(R.color.blackStripBackgroundCurrentJobs);
                        jobStatusIv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.schedule_icon, 0, 0, 0);
                        jobStatusIv.setText(scheduled);
                        jobStatusIv.setTextColor(scheduledTextColor);
                        jobTimeStatusContainer
                                .setBackgroundResource(R.color.completedOrNotStartedJobsBackgroundCurrentJobs);


                        dateOrTimeTv.setText(currentJobHashMap
                                .get(KEYS.CurrentJobs.DATE_TO_SHOW) + " ");
                        // yearTv.setVisibility(View.VISIBLE);
                        // yearTv.setText(currentJobHashmap.get(KEYS.CurrentJobs.YEAR_TO_SHOW));
                        Logger.log(">>>>>",
                                currentJobHashMap.get(KEYS.CurrentJobs.TIME_TO_SHOW));
                        startTimeTv.setText(currentJobHashMap
                                .get(KEYS.CurrentJobs.TIME_TO_SHOW));
                        break;

                    case KEYS.CurrentJobs.JOB__STARTED:
                        leftStripIv
                                .setBackgroundResource(R.color.greenStripBackgroundCurrentJobs);
                        jobStatusIv.setVisibility(View.VISIBLE);
                        jobStatusIv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.started_btn, 0, 0, 0);
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
                        jobStatusIv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.suspended_btn, 0, 0, 0);
                        jobStatusIv.setText(suspended);
                        jobStatusIv.setTextColor(suspendedTextColor);
                        if (!TextUtils.isEmpty(currentJobHashMap
                                .get(KEYS.CurrentJobs.DATE_TO_SHOW))) {
                            dateOrTimeTv.setText(currentJobHashMap
                                    .get(KEYS.CurrentJobs.DATE_TO_SHOW) + " ");
                        }

                        // yearTv.setText(currentJobHashmap.get(KEYS.CurrentJobs.YEAR_TO_SHOW));
                        startTimeTv.setText(currentJobHashMap
                                .get(KEYS.CurrentJobs.TIME_TO_SHOW));
                        break;

                    default:
                        break;
                }

                int prority = Integer.parseInt(currentJobHashMap
                        .get(KEYS.CurrentJobs.PRIORITY));

                switch (prority) {
                    case KEYS.CurrentJobs.PRIORITY_HIGH:
                        jobPriorityTv.setText(activity.getResources().getString(
                                R.string.textHighPriorityTv).toUpperCase());
                        jobPriorityTv.setTextColor(activity.getResources().getColor(
                                R.color.textHighPriorityCurrentJobs));

                        break;
                    case KEYS.CurrentJobs.PRIORITY_NORMAL:
                        jobPriorityTv.setText(activity.getResources().getString(
                                R.string.textAveragePriorityTv).toUpperCase());
                        jobPriorityTv.setTextColor(activity.getResources().getColor(
                                R.color.textNormalPrioriyCurrentJobs));

                        // jobPriorityTv.setTextColor(R.color.textColo)

                        break;
                    case KEYS.CurrentJobs.PRIORITY_LOW:
                        jobPriorityTv.setText(activity.getResources().getString(
                                R.string.textLowPriorityTv).toUpperCase());
                        jobPriorityTv.setTextColor(activity.getResources().getColor(
                                R.color.textLowPrioriyCurrentJobs));

                        break;
                    default:
                        break;
                }

                subItem.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        @SuppressWarnings("unchecked")
                        HashMap<String, String> map = (HashMap<String, String>) v
                                .getTag();

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
                                bundle.putBoolean(KEYS.CurrentJobs.IDSTARTJOB, false);

                                // closeDb();
                                Intent jobDetailIntent = new Intent(activity,
                                        JobDetails.class);
                                jobDetailIntent.putExtras(bundle);
                                baseFragment.startActivity(jobDetailIntent);
                            }
                        }
                    }
                });

                containerJobList.addView(subItem);

            }
        }
        return convertView;

    }

    /* (non-Javadoc)
     * @see android.widget.BaseAdapter#notifyDataSetChanged()
     */
    @Override
    public void notifyDataSetChanged() {
        // TODO Auto-generated method stub
        try {
            keySetDeadlineExceededJob.clear();
            keySetDeadlineExceededJob.addAll(dedlineExceededJobList.keySet());
        } catch (Exception e) {
            Logger.printException(e);
        }
        super.notifyDataSetChanged();

        super.notifyDataSetChanged();
    }
}
