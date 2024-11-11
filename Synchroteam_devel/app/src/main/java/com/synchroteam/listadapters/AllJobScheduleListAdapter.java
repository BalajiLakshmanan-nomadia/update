package com.synchroteam.listadapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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
import com.synchroteam.synchroteam3.R;
import com.synchroteam.technicalsupport.JobDetails;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;

// TODO: Auto-generated Javadoc

/**
 * Adapter Class to for Deadline Jobs List.
 */
@SuppressLint("SimpleDateFormat")
public class AllJobScheduleListAdapter extends BaseAdapter {

    /**
     * The activity.
     */
    private Activity activity;

    /**
     * The deadline job beans.
     */
    private TreeMap<String, ArrayList<HashMap<String, String>>> allJobList;

    /**
     * The key set all job list.
     */
    private ArrayList<String> keySetAllJobList;

    /**
     * The layout inflater.
     */
    private LayoutInflater layoutInflater;


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

    TextView clientNameTv, txtStartTime, txtEndTime;
    TextView jobNameTv;
    android.widget.TextView txt_ic_lock;

    LinearLayout linContainerJob;

    TextView dateDetailCurrentJobsTv;


    /**
     * Instantiates a new deadline jobs list adapter.
     *
     * @param activity   the activity
     * @param allJobList the all job list
     */
    public AllJobScheduleListAdapter(Activity activity,
                                     TreeMap<String, ArrayList<HashMap<String, String>>> allJobList) {
        // TODO Auto-generated constructor stub
        this.activity = activity;
        this.allJobList = allJobList;
        headerDateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        oldDatePattern = new SimpleDateFormat("yyyy-MM-dd");
        layoutInflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        keySetAllJobList = new ArrayList<String>(allJobList.keySet());


        started = activity.getString(R.string.textStarted);
        suspended = activity.getString(R.string.textSuspended);
        scheduled = activity.getString(R.string.textScheduled);
        completed = activity.getString(R.string.textCompleted);

        startedTextColor = activity.getResources().getColor(R.color.textColorGreen);
        suspendedTextColor = activity.getResources().getColor(R.color.textColorOrange);
        scheduledTextColor = activity.getResources().getColor(R.color.textColorBlack);
        completedTextColor = activity.getResources().getColor(R.color.textColorLightBlue);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return keySetAllJobList.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public ArrayList<HashMap<String, String>> getItem(int position) {
        // TODO Auto-generated method stub
        return allJobList.get(keySetAllJobList.get(position));
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

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.layout_all_job_list_item, parent, false);
            LinearLayout containerJobList = (LinearLayout) convertView
                    .findViewById(R.id.containerLinearlayout);

            dateDetailCurrentJobsTv = (TextView) convertView
                    .findViewById(R.id.dateDetailCurrentJobsTv);

            ArrayList<HashMap<String, String>> currentJobHashmapList = getItem(position);


            dateDetailCurrentJobsTv.setVisibility(View.GONE);

            Iterator<HashMap<String, String>> iterator = currentJobHashmapList
                    .iterator();
            while (iterator.hasNext()) {
                View subItem = layoutInflater.inflate(R.layout.layout_all_job_pool_listitem,
                        parent, false);
                HashMap<String, String> currentJobHashMap = iterator.next();
                subItem.setTag(currentJobHashMap);

                jobNameTv = (TextView) subItem.findViewById(R.id.jobNameTv);
                clientNameTv = (TextView) subItem.findViewById(R.id.clientNameTv);
                txtStartTime = (TextView) subItem.findViewById(R.id.txtStartTime);
                txtEndTime = (TextView) subItem.findViewById(R.id.txtEndTime);

                linContainerJob = (LinearLayout) subItem.findViewById(R.id.linContainerJob);

                txt_ic_lock = (android.widget.TextView) subItem.findViewById(R.id.txt_ic_lock);

                jobNameTv.setText(currentJobHashMap.get(KEYS.CurrentJobs.TYPE_NEW));


                int status = Integer.parseInt(currentJobHashMap
                        .get(KEYS.CurrentJobs.CD_STATUS));


                Logger.log(">>>>>", ">>>>>" +
                        currentJobHashMap.get(KEYS.CurrentJobs.ADR_RUE));
                Logger.log(">>>>>", ">>>>>" +
                        currentJobHashMap.get(KEYS.CurrentJobs.ADR_GLOBAL));

                String address = currentJobHashMap.get(KEYS.CurrentJobs.ADR_VILLE);
                if (address != null && address.trim().length() > 0) {
                    clientNameTv.setText(address);
                } else {
                    clientNameTv.setText("-");
                }

                Typeface typeface = Typeface.createFromAsset(activity.getAssets(),
                        activity.getString(R.string.fontName_fontAwesome));
                txt_ic_lock.setTypeface(typeface);

                switch (status) {
                    case KEYS.CurrentJobs.JOB_NOT_STARTED2:
                    case KEYS.CurrentJobs.JOB_NOT_STARTED1:
                        linContainerJob
                                .setBackgroundResource(R.drawable.boxframe_textview_layout_not_started);
                        String startTime = formatDateFromOnetoAnother(currentJobHashMap
                                .get(KEYS.CurrentJobs.MDATE1), "yyyy-MM-dd HH:mm:ss.SSS", "hh:mm a");
                        String endTime = formatDateFromOnetoAnother(currentJobHashMap
                                .get(KEYS.CurrentJobs.MDATE2), "yyyy-MM-dd HH:mm:ss.SSS", "hh:mm a");

                        txtStartTime.setText(startTime);
                        txtEndTime.setText(endTime);

                        break;

                    case KEYS.CurrentJobs.JOB__STARTED:
                        linContainerJob
                                .setBackgroundResource(R.drawable.boxframe_textview_layout_started);
                        String time = formatDateFromOnetoAnother(currentJobHashMap
                                .get(KEYS.CurrentJobs.TIME_TO_SHOW), "HH:mm", "hh:mm a");
                        txtStartTime.setText(time);
                        txtEndTime.setVisibility(View.GONE);
                        break;
                    case KEYS.CurrentJobs.JOB__SUSPENDED:

                        linContainerJob
                                .setBackgroundResource(R.drawable.boxframe_textview_layout);
                        String time1 = formatDateFromOnetoAnother(currentJobHashMap
                                .get(KEYS.CurrentJobs.TIME_TO_SHOW), "HH:mm", "hh:mm a");
                        txtStartTime.setText(time1);
                        txtEndTime.setVisibility(View.GONE);

                        break;
                    case KEYS.CurrentJobs.JOB__COMPLETE:

                        linContainerJob
                                .setBackgroundResource(R.drawable.boxframe_textview_layout_complete);
                        txtStartTime.setText(currentJobHashMap
                                .get(KEYS.CurrentJobs.TIME_TO_SHOW));
                        txtEndTime.setVisibility(View.GONE);
                        break;

                    default:
                        break;
                }

                int prority = Integer.parseInt(currentJobHashMap
                        .get(KEYS.CurrentJobs.PRIORITY));


                String dtMeeting = currentJobHashMap.get(KEYS.CurrentJobs.DATEMEETING);
                if (dtMeeting != null && !TextUtils.isEmpty(dtMeeting) && !dtMeeting.equalsIgnoreCase("null")) {
                    txt_ic_lock.setVisibility(View.VISIBLE);
                } else {
                    txt_ic_lock.setVisibility(View.GONE);
                }


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
            keySetAllJobList.clear();
            keySetAllJobList.addAll(allJobList.keySet());
        } catch (Exception e) {
            Logger.printException(e);
        }

        super.notifyDataSetChanged();
    }

    public String formatDateFromOnetoAnother(String dateStr, String givenformat, String resultformat) {

        String result = "";
        SimpleDateFormat sdf;
        SimpleDateFormat sdf1;

        try {
            sdf = new SimpleDateFormat(givenformat);
            sdf1 = new SimpleDateFormat(resultformat);
            result = sdf1.format(sdf.parse(dateStr));
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
