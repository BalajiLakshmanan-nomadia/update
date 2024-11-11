package com.synchroteam.listadapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.technicalsupport.JobDetails;
import com.synchroteam.utils.AccentInsensitive;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeMap;

// TODO: Auto-generated Javadoc

/**
 * Adapter Class to for Deadline Jobs List.
 */
@SuppressLint("SimpleDateFormat")
public class AllJobsDateAdapter extends ArrayAdapter<HashMap<String, String>> {

    /**
     * The activity.
     */
    private Activity activity;

    /**
     * The deadline job beans.
     */
    private ArrayList<HashMap<String, String>> allJobList;

    /**
     * The deadline job beans.
     */
    private ArrayList<HashMap<String, String>> originalJobList;

    /**
     * The layout inflater.
     */
    private LayoutInflater layoutInflater;

    /**
     * The index.
     */
    private int listIndex;

    /**
     * The base count.
     */
    private int baseCount = 20;


    /**
     * The old date pattern.
     */
    private SimpleDateFormat headerDateFormat, oldDatePattern;

    private String started;

    private String suspended;

    private String scheduled;

    private String completed;
    private int startedTextColor;

    private int suspendedTextColor;

    private int scheduledTextColor;

    private int completedTextColor;

    /**
     * Instantiates all jobs list adapter.
     *
     * @param activity   the activity
     * @param allJobList the all job list
     */
    public AllJobsDateAdapter(Activity activity,
                              ArrayList<HashMap<String, String>> allJobList) {
        super(activity, R.layout.layout_all_job_list_item, allJobList);
        this.activity = activity;
        this.allJobList = allJobList;

        setDuplicateList();

        headerDateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        oldDatePattern = new SimpleDateFormat("yyyy-MM-dd");
        layoutInflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        started = activity.getString(R.string.textStarted);
        suspended = activity.getString(R.string.textSuspended);
        scheduled = activity.getString(R.string.textScheduled);
        completed = activity.getString(R.string.textCompleted);

        startedTextColor = activity.getResources().getColor(R.color.textColorGreen);
        suspendedTextColor = activity.getResources().getColor(R.color.textColorOrange);
        scheduledTextColor = activity.getResources().getColor(R.color.textColorBlack);
        completedTextColor = activity.getResources().getColor(R.color.textColorLightBlue);
    }

    /**
     * Adds duplicate list for searching.
     */
    public void setDuplicateList() {
        originalJobList = new ArrayList<>();
        originalJobList.addAll(allJobList);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        int count = listIndex * baseCount;
        if (count < allJobList.size()) {
            return count;
        } else {
            return allJobList.size();
        }
    }

    /**
     * Gets count of array.
     *
     * @return size
     */
    public int getArrayCount() {
        return allJobList.size();
    }

    /**
     * Sets the index position.
     *
     * @param index the new index position
     */
    public void setIndexPosition(int index) {
        this.listIndex = index;
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
            convertView = layoutInflater.inflate(R.layout.rowview_all_job_with_date,parent,false);
            HashMap<String, String> currentJobHashMap = allJobList.get(position);
            dateDetailCurrentJobsTv = (TextView) convertView
                    .findViewById(R.id.dateDetailCurrentJobsTv);

            String hasHeader = currentJobHashMap.get(KEYS.CurrentJobs.HAS_HEADER);

            if (hasHeader.equalsIgnoreCase("true")) {
                dateDetailCurrentJobsTv.setVisibility(View.VISIBLE);
                try {
                    dateDetailCurrentJobsTv.setText(headerDateFormat.format(oldDatePattern.parse(currentJobHashMap.get(KEYS.CurrentJobs.ALL_JOB_HEADER))));
                } catch (ParseException e) {
                    Logger.printException(e);
                }
            } else {
                dateDetailCurrentJobsTv.setVisibility(View.GONE);
            }

            convertView.setTag(currentJobHashMap);
            leftStripIv = (ImageView) convertView.findViewById(R.id.stripColorIv);
            jobStatusIv = (TextView) convertView.findViewById(R.id.jobStatusTv);

            jobNameTv = (TextView) convertView.findViewById(R.id.jobNameTv);
            dateOrTimeTv = (TextView) convertView.findViewById(R.id.dateOrTimeTv);
            // yearTv = (TextView) view.findViewById(R.id.yearTv);
            jobPriorityTv = (TextView) convertView.findViewById(R.id.jobPriorityTv);
            clientNameTv = (TextView) convertView.findViewById(R.id.clientNameTv);
            startTimeTv = (TextView) convertView.findViewById(R.id.startTime);
            jobTimeStatusContainer = (RelativeLayout) convertView
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
                case KEYS.CurrentJobs.JOB__COMPLETE:
                case KEYS.CurrentJobs.JOB__VALIDATED:
                    leftStripIv
                            .setBackgroundResource(R.color.colorBlueStripReportsList);

                    jobTimeStatusContainer
                            .setBackgroundResource(R.color.colorBluejobTimeStatusRlReoprtsList);
                    jobStatusIv.setVisibility(View.VISIBLE);
                    jobStatusIv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.completed_job_ind, 0, 0, 0);
                    jobStatusIv.setText(completed);
                    jobStatusIv.setTextColor(completedTextColor);

                    jobNameTv.setText(currentJobHashMap.get(KEYS.CurrentJobs.TYPE));
                    clientNameTv.setText(currentJobHashMap
                            .get(KEYS.CurrentJobs.NOM_CLIENT_INTERV));
                    dateOrTimeTv.setText(currentJobHashMap
                            .get(KEYS.CurrentJobs.DATE_TO_SHOW) + " ");

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

            convertView.setOnClickListener(new OnClickListener() {

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
                            activity.startActivity(jobDetailIntent);
                        }
                    }

                }
            });
        }
        return convertView;
    }

    /* (non-Javadoc)
     * @see android.widget.BaseAdapter#notifyDataSetChanged()
     */
//    @Override
//    public void notifyDataSetChanged() {
//        // TODO Auto-generated method stub
//        try {
//            allJobList.clear();
//            keySetAllJobList.addAll(allJobList.keySet());
//        } catch (Exception e) {
//            Logger.printException(e);
//        }
//
//        super.notifyDataSetChanged();
//    }

    public Filter getAllJobFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                ArrayList<HashMap<String, String>> tempList = new ArrayList<>();
                TreeMap<String, ArrayList<HashMap<String, String>>> tmJobList = new TreeMap<>();

                if (constraint != null && constraint.length() > 0) {
                    for (int i = 0; i < originalJobList.size(); i++) {

//                        ArrayList<String> tempKeyList = new ArrayList<>(originalJobList.keySet());
//                        ArrayList<HashMap<String, String>> tempSingleList = originalJobList.get(tempKeyList.get(i));

                        HashMap<String, String> hmJob = originalJobList.get(i);
                        String mSearchString = hmJob.get(KEYS.CurrentJobs.NOM_CLIENT_INTERV)
                                + hmJob.get(KEYS.CurrentJobs.NOMSITE)
                                + hmJob.get(KEYS.CurrentJobs.NOMEQUIPMENT)
                                + hmJob.get(KEYS.CurrentJobs.TYPE)
                                + hmJob.get(KEYS.CurrentJobs.ADR_VILLE)
                                + hmJob.get(KEYS.CurrentJobs.CF_INTERVENTION)
                                + hmJob.get(KEYS.CurrentJobs.CF_SITE)
                                + hmJob.get(KEYS.CurrentJobs.CF_CLIENT)
                                + hmJob.get(KEYS.CurrentJobs.CF_EQUIPMENT);

                        String mAsciiSearch = AccentInsensitive.removeAccentCase(mSearchString);
                        String mAsciiConstraint = AccentInsensitive.removeAccentCase(constraint.toString());
                        if (mAsciiConstraint != null && mAsciiConstraint.length() > 0)
                            if (mAsciiSearch.toLowerCase().contains(mAsciiConstraint.toLowerCase())) {
                                if (tmJobList.containsKey(hmJob
                                        .get(KEYS.CurrentJobs.ALL_JOB_HEADER))) {
                                    tmJobList.get(hmJob.get(KEYS.CurrentJobs.ALL_JOB_HEADER))
                                            .add(hmJob);
                                    hmJob.put(KEYS.CurrentJobs.HAS_HEADER, "false");
                                    tempList.add(hmJob);

                                } else {
                                    tmJobList.put(hmJob.get(KEYS.CurrentJobs.ALL_JOB_HEADER),
                                            new ArrayList<HashMap<String, String>>());
                                    tmJobList.get(hmJob.get(KEYS.CurrentJobs.ALL_JOB_HEADER))
                                            .add(hmJob);
                                    hmJob.put(KEYS.CurrentJobs.HAS_HEADER, "true");
                                    tempList.add(hmJob);
                                }

                            }
                    }
                    results.values = tempList;
                    results.count = tempList.size();
                } else {
                    synchronized (allJobList) {
                        results.values = originalJobList;
                        results.count = originalJobList.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                allJobList.clear();
                allJobList.addAll((Collection<? extends HashMap<String, String>>) results.values);
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}
