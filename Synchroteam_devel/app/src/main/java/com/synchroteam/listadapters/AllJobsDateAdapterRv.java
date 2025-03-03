package com.synchroteam.listadapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.CommonJobBean;
import com.synchroteam.dialogs.JobDetailPopupDialog;
import com.synchroteam.listeners.RvEmptyListener;
import com.synchroteam.synchroteam.JobPoolDetails;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.technicalsupport.JobDetails;
import com.synchroteam.utils.AccentInsensitive;
import com.synchroteam.utils.DateTimeUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.TreeMap;

//import static io.fabric.sdk.android.services.network.HttpRequest.get;

/**
 * Adapter class for showing all jobs with date.
 * </p>
 * Created by Trident on 11/8/2016.
 */

public class AllJobsDateAdapterRv extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private boolean isHeaderShown;
    private static final int HEADER_POSITION = 0;
    private int incrementValue;

    private FragmentActivity activity;
    private RvEmptyListener emptyListListener;
    private ArrayList<HashMap<String, String>> allJobList;
    private ArrayList<HashMap<String, String>> originalJobList;
    private int listIndex;
    private int baseCount = 20;
    private SimpleDateFormat headerDateFormat, oldDatePattern;
    private String started;
    private String suspended;
    private String scheduled;
    private String completed;
    private int startedTextColor;
    private int suspendedTextColor;
    private int scheduledTextColor;
    private int completedTextColor;
    private boolean isJobPool = false;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    private String currentDateFormat = "yyyy-MM-dd HH:mm:ss";

    DateFormat dateFormatJobPoolList;

    /**
     * Normal item view holder
     */
    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

        public ImageView leftStripIv;
        public TextView jobStatusIv;
        public TextView jobNameTv;
        public TextView customerAddress;
        public ImageView priorityImage;
        public TextView dateOrTimeTv;
        public TextView jobPriorityTv;
        public TextView clientNameTv;
        public TextView startTimeTv;
        public RelativeLayout jobTimeStatusContainer, jobViewRl, jobPoolRl;
        public TextView dateDetailCurrentJobsTv;
        public android.widget.TextView txtLockIc, txtLockIc_New, txtTimeIcon, txtDateIcon, txtDateMeetIcon;
        LinearLayout linMeetDate;
        RelativeLayout relPrefDate;
        TextView txtTimelabel, txtTimeSlot, txtDatePref, txtDateMeet;

        public ItemViewHolder(View view) {
            super(view);

            dateDetailCurrentJobsTv = (TextView) view.findViewById(R.id.dateDetailCurrentJobsTv);
            leftStripIv = (ImageView) view.findViewById(R.id.stripColorIv);
            jobStatusIv = (TextView) view.findViewById(R.id.jobStatusTv);
            jobNameTv = (TextView) view.findViewById(R.id.jobNameTv);
            customerAddress = view.findViewById(R.id.customer_addressTv);
            priorityImage = view.findViewById(R.id.priorityImage);
            dateOrTimeTv = (TextView) view.findViewById(R.id.dateOrTimeTv);
            jobPriorityTv = (TextView) view.findViewById(R.id.jobPriorityTv);
            clientNameTv = (TextView) view.findViewById(R.id.clientNameTv);
            startTimeTv = (TextView) view.findViewById(R.id.startTime);
            jobTimeStatusContainer = (RelativeLayout) view.findViewById(R.id.jobTimeStatusRl);
            jobViewRl = (RelativeLayout) view.findViewById(R.id.jobViewRl);
            jobPoolRl = (RelativeLayout) view.findViewById(R.id.jobPoolRl);
            txtTimeIcon = (android.widget.TextView) view.findViewById(R.id.txtTimeIcon);
            txtDateIcon = (android.widget.TextView) view.findViewById(R.id.txtDateIcon);
            txtLockIc = (android.widget.TextView) view.findViewById(R.id.txt_ic_lock);
            txtLockIc_New = (android.widget.TextView) view.findViewById(R.id.txt_ic_lock_new);
            txtDateMeetIcon = (android.widget.TextView) view.findViewById(R.id.txtDateMeetIcon);

            linMeetDate = (LinearLayout) view.findViewById(R.id.linMeetDate);
            relPrefDate = (RelativeLayout) view.findViewById(R.id.relPrefDate);
            txtTimelabel = (TextView) view.findViewById(R.id.txtTimelabel);
            txtTimeSlot = (TextView) view.findViewById(R.id.txtTimeSlot);
            txtDatePref = (TextView) view.findViewById(R.id.txtDatePref);
            txtDateMeet = (TextView) view.findViewById(R.id.txtDateMeet);

            Typeface typeface = Typeface.createFromAsset(activity.getAssets(), activity.getString(R.string.fontName_fontAwesome));
            txtLockIc.setTypeface(typeface);
            txtDateIcon.setTypeface(typeface);
            txtTimeIcon.setTypeface(typeface);
            txtLockIc_New.setTypeface(typeface);
            txtDateMeetIcon.setTypeface(typeface);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                HashMap<String, String> map = getItem(getAdapterPosition());
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

                        if (isJobPool) {

                            bundle.putString(KEYS.CurrentJobs.DATE_PREF,
                                    map.get(KEYS.CurrentJobs.DATE_PREF));
                            bundle.putString(KEYS.CurrentJobs.ID_JOB_WINDOW,
                                    map.get(KEYS.CurrentJobs.ID_JOB_WINDOW));


                            Intent jobDetailIntent = new Intent(activity,
                                    JobPoolDetails.class);
                            jobDetailIntent.putExtras(bundle);
                            activity.startActivity(jobDetailIntent);
                        } else {
                            Intent jobDetailIntent = new Intent(activity,
                                    JobDetails.class);
                            jobDetailIntent.putExtras(bundle);
                            activity.startActivity(jobDetailIntent);
                        }
                    }
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                HashMap<String, String> map = getItem(getAdapterPosition());
                if (map != null)
                    if (!TextUtils.isEmpty(map.get(KEYS.CurrentJobs.DISPO))) {
                        if (!Objects.equals(map.get(KEYS.CurrentJobs.DISPO), KEYS.CurrentJobs.TRUE)) {
                            Bundle bundle = new Bundle();

                            String[] numInterv = Objects.requireNonNull(map.get(KEYS.CurrentJobs.TYPE))
                                    .split("-");
                            String nmInterv = "";
                            if (numInterv != null && numInterv.length == 2)
                                nmInterv = numInterv[0].substring(1);
                            else
                                nmInterv = "0";

                            Logger.log(">>>>>", "job id is in nav act" + map.get(KEYS.CurrentJobs.ID));
                            Logger.log(">>>>>", "job idUser is in nav act" + map.get(KEYS.CurrentJobs.ID_USER));


                            bundle.putString(KEYS.CurrentJobs.ID,
                                    map.get(KEYS.CurrentJobs.ID));

                            bundle.putInt(KEYS.CurrentJobs.CD_STATUS, Integer
                                    .parseInt(map.get(KEYS.CurrentJobs.CD_STATUS)));
                            bundle.putString(KEYS.CurrentJobs.ID_USER,
                                    map.get(KEYS.CurrentJobs.ID_USER));
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
                            bundle.putString(KEYS.CurrentJobs.DATEMEETING,
                                    map.get(KEYS.CurrentJobs.DATEMEETING));

                            bundle.putString(KEYS.CurrentJobs.FROM_WHERE,
                                    KEYS.CurrentJobs.SYNCROTEAMNAVIGATIONACTIVITY);
                            bundle.putBoolean(KEYS.CurrentJobs.IDSTARTJOB, false);


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

                            bundle.putString(KEYS.CurrentJobs.TYPE,
                                    map.get(KEYS.CurrentJobs.TYPE));

                            bundle.putString(KEYS.CurrentJobs.NOM_CLIENT_INTERV,
                                    map.get(KEYS.CurrentJobs.NOM_CLIENT_INTERV));

                            bundle.putString(KEYS.CurrentJobs.MOBILE_CONTACT,
                                    map.get(KEYS.CurrentJobs.MOBILE_CONTACT));

                            bundle.putString(KEYS.CurrentJobs.ID, map.get(KEYS.CurrentJobs.ID));



                            JobDetailPopupDialog jobDetailPopupDialog = JobDetailPopupDialog.newInstance(bundle,
                                    new JobDetailPopupDialog.JobDetailsNavigationInterface() {
                                        @Override
                                        public void open() {
                                            if (isJobPool) {

                                                bundle.putString(KEYS.CurrentJobs.DATE_PREF,
                                                        map.get(KEYS.CurrentJobs.DATE_PREF));
                                                bundle.putString(KEYS.CurrentJobs.ID_JOB_WINDOW,
                                                        map.get(KEYS.CurrentJobs.ID_JOB_WINDOW));


                                                Intent jobDetailIntent = new Intent(activity,
                                                        JobPoolDetails.class);
                                                jobDetailIntent.putExtras(bundle);
                                                activity.startActivity(jobDetailIntent);
                                            } else {
                                                Intent jobDetailIntent = new Intent(activity,
                                                        JobDetails.class);
                                                jobDetailIntent.putExtras(bundle);
                                                activity.startActivity(jobDetailIntent);
                                            }
                                        }

                                        @Override
                                        public void close() {
                                        }
                                    });
                            jobDetailPopupDialog.show(activity.getSupportFragmentManager(),"");
                        }
                    }
            }
            return true;
        }
    }

    /**
     * Header view holder.
     */
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        public View mPlaceHolder;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            mPlaceHolder = itemView.findViewById(R.id.placeholder);
        }
    }

    /**
     * Footer view holder
     */
    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout linFooterView;

        public FooterViewHolder(View itemView) {
            super(itemView);
            linFooterView = (LinearLayout) itemView.findViewById(R.id.footer_layout);
        }
    }

    public AllJobsDateAdapterRv(FragmentActivity activity, RvEmptyListener emptyListListener,
                                ArrayList<HashMap<String, String>> allJobList, boolean isJobPool) {
        this.activity = activity;
        this.emptyListListener = emptyListListener;
        this.allJobList = allJobList;
        this.isJobPool = isJobPool;

        setDuplicateList();

        headerDateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        oldDatePattern = new SimpleDateFormat("yyyy-MM-dd");

        started = activity.getString(R.string.textStarted);
        suspended = activity.getString(R.string.textSuspended);
        scheduled = activity.getString(R.string.textScheduled);
        completed = activity.getString(R.string.textCompleted);

        startedTextColor = ContextCompat.getColor(activity, R.color.textColorGreen);
        suspendedTextColor = ContextCompat.getColor(activity, R.color.textColorOrange);
        scheduledTextColor = ContextCompat.getColor(activity, R.color.textColorBlack);
        completedTextColor = ContextCompat.getColor(activity, R.color.textColorLightBlue);

    }


    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.rowview_all_job_with_date, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(activity).inflate(R.layout.all_jobs_header, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(activity).inflate(R.layout.layout_footerview_items_list, parent, false);
            return new FooterViewHolder(view);
        }
        throw new RuntimeException("No view found");
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setClickable(true);
        if (holder instanceof ItemViewHolder) {
            if (position != RecyclerView.NO_POSITION) {
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

                HashMap<String, String> hmAllJobs = getItem(position);

                String hasHeader = hmAllJobs.get(KEYS.CurrentJobs.HAS_HEADER);
                if (hasHeader.equalsIgnoreCase("true")) {
                    itemViewHolder.dateDetailCurrentJobsTv.setVisibility(View.VISIBLE);
                    try {
                        itemViewHolder.dateDetailCurrentJobsTv.setText(headerDateFormat.format(oldDatePattern.parse(hmAllJobs.get(KEYS.CurrentJobs.ALL_JOB_HEADER))));
                    } catch (ParseException e) {
                        Logger.printException(e);
                    }
                } else {
                    itemViewHolder.dateDetailCurrentJobsTv.setVisibility(View.GONE);
                }

                itemViewHolder.jobNameTv.setText(hmAllJobs.get(KEYS.CurrentJobs.TYPE));
                itemViewHolder.customerAddress.setText(hmAllJobs.get(KEYS.CurrentJobs.ADR_GLOBAL));
                if (hmAllJobs.get(KEYS.CurrentJobs.NOMSITE).length()>0){
                    itemViewHolder.clientNameTv.setText(hmAllJobs
                            .get(KEYS.CurrentJobs.NOM_CLIENT_INTERV)+": "+hmAllJobs.get(KEYS.CurrentJobs.NOMSITE));
                }else {
                    itemViewHolder.clientNameTv.setText(hmAllJobs
                            .get(KEYS.CurrentJobs.NOM_CLIENT_INTERV));
                }

                int status = Integer.parseInt(hmAllJobs
                        .get(KEYS.CurrentJobs.CD_STATUS));
                itemViewHolder.jobPriorityTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                switch (status) {
                    case KEYS.CurrentJobs.JOB_NOT_STARTED2:
                    case KEYS.CurrentJobs.JOB_NOT_STARTED1:
                        itemViewHolder.leftStripIv
                                .setBackgroundResource(R.color.blackStripBackgroundCurrentJobs);
                        itemViewHolder.jobStatusIv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.schedule_icon, 0, 0, 0);
                        itemViewHolder.jobStatusIv.setText(scheduled);
                        itemViewHolder.jobStatusIv.setTextColor(scheduledTextColor);
                        itemViewHolder.jobTimeStatusContainer
                                .setBackgroundResource(R.color.completedOrNotStartedJobsBackgroundCurrentJobs);


                        itemViewHolder.dateOrTimeTv.setText(hmAllJobs
                                .get(KEYS.CurrentJobs.DATE_TO_SHOW) + " ");
                        // yearTv.setVisibility(View.VISIBLE);
                        // yearTv.setText(currentJobHashmap.get(KEYS.CurrentJobs.YEAR_TO_SHOW));
                        Logger.log(">>>>>",
                                hmAllJobs.get(KEYS.CurrentJobs.TIME_TO_SHOW));
                        itemViewHolder.startTimeTv.setText(hmAllJobs
                                .get(KEYS.CurrentJobs.TIME_TO_SHOW));
                        break;

                    case KEYS.CurrentJobs.JOB__STARTED:
                        itemViewHolder.leftStripIv
                                .setBackgroundResource(R.color.greenStripBackgroundCurrentJobs);
                        itemViewHolder.jobStatusIv.setVisibility(View.VISIBLE);
                        itemViewHolder.jobStatusIv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.started_btn, 0, 0, 0);
                        itemViewHolder.jobStatusIv.setText(started);
                        itemViewHolder.jobStatusIv.setTextColor(startedTextColor);
                        itemViewHolder.jobTimeStatusContainer
                                .setBackgroundResource(R.color.startedJobsBackgroundCurrentJobs);
                        itemViewHolder.dateOrTimeTv.setText(hmAllJobs
                                .get(KEYS.CurrentJobs.DATE_TO_SHOW) + " ");
                        // yearTv.setText(currentJobHashmap.get(KEYS.CurrentJobs.YEAR_TO_SHOW));

                        itemViewHolder.startTimeTv.setText(hmAllJobs
                                .get(KEYS.CurrentJobs.TIME_TO_SHOW));
                        break;
                    case KEYS.CurrentJobs.JOB__SUSPENDED:
                        itemViewHolder.leftStripIv
                                .setBackgroundResource(R.color.orangeStripBackgroundCurrentJobs);
                        itemViewHolder.jobStatusIv.setVisibility(View.VISIBLE);
                        itemViewHolder.jobTimeStatusContainer
                                .setBackgroundResource(R.color.suspendedJobsBackgroundCurrentJobs);
                        itemViewHolder.jobStatusIv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.suspended_btn, 0, 0, 0);
                        itemViewHolder.jobStatusIv.setText(suspended);
                        itemViewHolder.jobStatusIv.setTextColor(suspendedTextColor);
                        if (!TextUtils.isEmpty(hmAllJobs
                                .get(KEYS.CurrentJobs.DATE_TO_SHOW))) {
                            itemViewHolder.dateOrTimeTv.setText(hmAllJobs
                                    .get(KEYS.CurrentJobs.DATE_TO_SHOW) + " ");
                        }

                        // yearTv.setText(currentJobHashmap.get(KEYS.CurrentJobs.YEAR_TO_SHOW));
                        itemViewHolder.startTimeTv.setText(hmAllJobs
                                .get(KEYS.CurrentJobs.TIME_TO_SHOW));
                        break;
                    case KEYS.CurrentJobs.JOB__COMPLETE:
                    case KEYS.CurrentJobs.JOB__VALIDATED:
                        itemViewHolder.leftStripIv
                                .setBackgroundResource(R.color.colorBlueStripReportsList);

                        itemViewHolder.jobTimeStatusContainer
                                .setBackgroundResource(R.color.colorBluejobTimeStatusRlReoprtsList);
                        itemViewHolder.jobStatusIv.setVisibility(View.VISIBLE);
                        itemViewHolder.jobStatusIv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.completed_job_ind, 0, 0, 0);
                        itemViewHolder.jobStatusIv.setText(completed);
                        itemViewHolder.jobStatusIv.setTextColor(completedTextColor);

                        itemViewHolder.jobNameTv.setText(hmAllJobs.get(KEYS.CurrentJobs.TYPE));
                        itemViewHolder.customerAddress.setText(hmAllJobs.get(KEYS.CurrentJobs.ADR_GLOBAL));
                        if (hmAllJobs.get(KEYS.CurrentJobs.NOMSITE).length()>0){
                            itemViewHolder.clientNameTv.setText(hmAllJobs
                                    .get(KEYS.CurrentJobs.NOM_CLIENT_INTERV)+": "+hmAllJobs.get(KEYS.CurrentJobs.NOMSITE));
                        }else {
                            itemViewHolder.clientNameTv.setText(hmAllJobs
                                    .get(KEYS.CurrentJobs.NOM_CLIENT_INTERV));
                        }
                        itemViewHolder.dateOrTimeTv.setText(hmAllJobs
                                .get(KEYS.CurrentJobs.DATE_TO_SHOW) + " ");

                        itemViewHolder.startTimeTv.setText(hmAllJobs
                                .get(KEYS.CurrentJobs.TIME_TO_SHOW));
                        break;

                    default:
                        break;
                }

                int prority = Integer.parseInt(hmAllJobs
                        .get(KEYS.CurrentJobs.PRIORITY));

                switch (prority) {
                    case KEYS.CurrentJobs.PRIORITY_HIGH:
                        itemViewHolder.jobPriorityTv.setText(activity.getResources().getString(
                                R.string.textHighPriorityTv).toUpperCase());
                        itemViewHolder.jobPriorityTv.setTextColor(ContextCompat.getColor(activity, R.color.textHighPriorityCurrentJobs));
                        itemViewHolder.priorityImage.setVisibility(View.VISIBLE);

                        break;
                    case KEYS.CurrentJobs.PRIORITY_NORMAL:
                        itemViewHolder.jobPriorityTv.setText(activity.getResources().getString(
                                R.string.textAveragePriorityTv).toUpperCase());
                        itemViewHolder.jobPriorityTv.setTextColor(ContextCompat.getColor(activity, R.color.textNormalPrioriyCurrentJobs));
                        itemViewHolder.priorityImage.setVisibility(View.GONE);
                        // jobPriorityTv.setTextColor(R.color.textColo)

                        break;
                    case KEYS.CurrentJobs.PRIORITY_LOW:
                        itemViewHolder.jobPriorityTv.setText(activity.getResources().getString(
                                R.string.textLowPriorityTv).toUpperCase());
                        itemViewHolder.jobPriorityTv.setTextColor(ContextCompat.getColor(activity, R.color.textLowPrioriyCurrentJobs));
                        itemViewHolder.priorityImage.setVisibility(View.GONE);
                        break;
                    default:
                        break;
                }

                String dtMeeting = hmAllJobs.get(KEYS.CurrentJobs.DATEMEETING);
                if (!TextUtils.isEmpty(dtMeeting) && !dtMeeting.equalsIgnoreCase("null")) {
                    itemViewHolder.txtLockIc.setVisibility(View.VISIBLE);
                } else {
                    itemViewHolder.txtLockIc.setVisibility(View.GONE);
                }

                if (isJobPool) {
                    //lock icon visibility
                    if (!TextUtils.isEmpty(dtMeeting) && !dtMeeting.equalsIgnoreCase("null")) {
                        itemViewHolder.txtLockIc_New.setVisibility(View.VISIBLE);
                    } else {
                        itemViewHolder.txtLockIc_New.setVisibility(View.GONE);
                    }

                    itemViewHolder.txtLockIc.setVisibility(View.GONE);
                    itemViewHolder.jobPoolRl.setVisibility(View.VISIBLE);
                    itemViewHolder.jobViewRl.setVisibility(View.GONE);
                    itemViewHolder.jobTimeStatusContainer
                            .setBackgroundResource(R.color.white);
                    itemViewHolder.leftStripIv
                            .setBackgroundResource(R.color.blackStripBackgroundCurrentJobs);
                    itemViewHolder.txtLockIc.setVisibility(View.GONE);

                    jobPoolLogic(hmAllJobs, itemViewHolder);

                } else {
                    itemViewHolder.jobPoolRl.setVisibility(View.GONE);
                    itemViewHolder.jobViewRl.setVisibility(View.VISIBLE);
                }


            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    private void jobPoolLogic(HashMap<String, String> hmAllJobs, ItemViewHolder itemViewHolder) {
        if (isJobPool) {
            String isJobDatePref = hmAllJobs.get(KEYS.CurrentJobs.DATE_PREF);
            int idJobWindow = Integer.parseInt(hmAllJobs.get(KEYS.CurrentJobs.ID_JOB_WINDOW));
            String dtMeeting = hmAllJobs.get(KEYS.CurrentJobs.DATEMEETING);


            String dateToshow = null;
            String dateToshowRequired = " ";
            String startTimeRequired = " ";
            String endTimeRequired = " ";

            String timeLabel = activity.getResources().getString(
                    R.string.textPrefTimeLable);

            if (!TextUtils.isEmpty(dtMeeting) && !dtMeeting.equalsIgnoreCase("null")) {
                String[] dateTopass = dtMeeting.split(" ");
                dateToshow = dateTopass[0];
//                    dateToshowRequired = getDateWithRequiredPattern(dateToshow,
//                            "yyyy-MM-dd", "dd/MM/yy");

                dateToshowRequired = DateTimeUtils.getDateWithRequiredPattern(dateToshow,"yyyy-MM-dd",
                        DateFormat.getDateInstance(DateFormat.LONG));

                itemViewHolder.relPrefDate.setVisibility(View.VISIBLE);
                itemViewHolder.linMeetDate.setVisibility(View.GONE);

                if (dateToshowRequired != null && dateToshowRequired.trim().length() > 0)
                    itemViewHolder.txtDatePref.setText(dateToshowRequired);

                if (dateTopass[1] != null && dateTopass[1].length() > 0 && !dateTopass[1].startsWith("00:00")) {
//                        startTimeRequired = getDateWithRequiredPattern(dateTopass[1],
//                                "HH:mm:ss", "hh:mm a");
                    startTimeRequired = DateTimeUtils.getTimeWithRequiredFormatPattern(dateTopass[1],"hh:mm a",android.text.format.DateFormat
                            .getTimeFormat(activity));
                    timeLabel = activity.getResources().getString(
                            R.string.label_time_job_pool);

                    itemViewHolder.txtLockIc_New.setVisibility(View.VISIBLE);
                    itemViewHolder.linMeetDate.setVisibility(View.VISIBLE);
                    itemViewHolder.relPrefDate.setVisibility(View.GONE);
                    itemViewHolder.txtDateMeet.setText(dateToshowRequired);
                    itemViewHolder.txtTimelabel.setText(timeLabel);
                    if (startTimeRequired != null && startTimeRequired.trim().length() > 0)
                        startTimeRequired = DateTimeUtils.getTimeWithRequiredFormatPattern(dateTopass[1],"HH:mm:ss",android.text.format.DateFormat
                                .getTimeFormat(activity));
                        itemViewHolder.txtTimeSlot.setText(startTimeRequired);

                } else {
                    itemViewHolder.txtLockIc_New.setVisibility(View.GONE);
                }

            } else {

                itemViewHolder.txtLockIc_New.setVisibility(View.GONE);
                itemViewHolder.relPrefDate.setVisibility(View.VISIBLE);
                itemViewHolder.linMeetDate.setVisibility(View.GONE);

                if (isJobDatePref != null && !TextUtils.isEmpty(isJobDatePref) &&
                        !isJobDatePref.equalsIgnoreCase("null")) {
                    String[] dateTopass = isJobDatePref.split(" ");
                    dateToshow = dateTopass[0];
                    dateToshowRequired = DateTimeUtils.getDateWithRequiredPattern(dateToshow,"yyyy-MM-dd",DateFormat.getDateInstance(DateFormat.LONG));

//                        dateToshowRequired = getDateWithRequiredPattern(dateToshow,
//                                "yyyy-MM-dd", "dd/MM/yy");

                    itemViewHolder.relPrefDate.setVisibility(View.VISIBLE);
                    itemViewHolder.linMeetDate.setVisibility(View.GONE);
                    if (dateToshowRequired != null && dateToshowRequired.trim().length() > 0)
                        itemViewHolder.txtDatePref.setText(dateToshowRequired);

                    if (dateTopass[1] != null && dateTopass[1].length() > 0 && !dateTopass[1].startsWith("00:00")) {
//                            startTimeRequired = getDateWithRequiredPattern(dateTopass[1],
//                                    "HH:mm:ss", "hh:mm a");

                        startTimeRequired = DateTimeUtils.getTimeWithRequiredFormatPattern(dateTopass[1],"hh:mm a",android.text.format.DateFormat
                                .getTimeFormat(activity));

                        itemViewHolder.txtTimelabel.setText(timeLabel);
                        if (startTimeRequired != null && startTimeRequired.trim().length() > 0)
                            itemViewHolder.txtTimeSlot.setText(startTimeRequired);
                    }
                }

                if (idJobWindow > 0) {
                    startTimeRequired = hmAllJobs.get(KEYS.CurrentJobs.ID_JOB_WINDOW_START_TIME);
                    startTimeRequired = DateTimeUtils.getTimeWithRequiredFormatPattern(startTimeRequired,"hh:mm a",android.text.format.DateFormat
                            .getTimeFormat(activity));
                    endTimeRequired = hmAllJobs.get(KEYS.CurrentJobs.ID_JOB_WINDOW_END_TIME);
                    endTimeRequired = DateTimeUtils.getTimeWithRequiredFormatPattern(endTimeRequired,"hh:mm a",android.text.format.DateFormat
                            .getTimeFormat(activity));

                    itemViewHolder.txtTimelabel.setText(timeLabel);

                    if (startTimeRequired != null && startTimeRequired.trim().length() > 0 &&
                            endTimeRequired != null && endTimeRequired.trim().length() > 0) {
                        itemViewHolder.txtTimeSlot.setText(startTimeRequired + "\n" + endTimeRequired);
                    }
                }
            }

            if (startTimeRequired == null || startTimeRequired.trim().length() == 0) {
                itemViewHolder.txtTimeSlot.setVisibility(View.INVISIBLE);
                itemViewHolder.txtTimeIcon.setVisibility(View.INVISIBLE);
                itemViewHolder.txtTimelabel.setVisibility(View.INVISIBLE);
            } else {
                itemViewHolder.txtTimeSlot.setVisibility(View.VISIBLE);
                itemViewHolder.txtTimeIcon.setVisibility(View.VISIBLE);
                itemViewHolder.txtTimelabel.setVisibility(View.VISIBLE);
            }

            if (dateToshowRequired == null || dateToshowRequired.trim().length() == 0) {
                itemViewHolder.txtDateIcon.setVisibility(View.INVISIBLE);
                itemViewHolder.txtDateMeet.setVisibility(View.INVISIBLE);
                itemViewHolder.txtDateMeetIcon.setVisibility(View.INVISIBLE);
                itemViewHolder.txtDatePref.setVisibility(View.INVISIBLE);
                itemViewHolder.relPrefDate.setVisibility(View.INVISIBLE);
                itemViewHolder.linMeetDate.setVisibility(View.INVISIBLE);
            } else {
                itemViewHolder.txtDateIcon.setVisibility(View.VISIBLE);
                itemViewHolder.txtDateMeetIcon.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(dtMeeting) && !dtMeeting.equalsIgnoreCase("null")) {
                    itemViewHolder.linMeetDate.setVisibility(View.VISIBLE);
                    itemViewHolder.txtDateMeet.setVisibility(View.VISIBLE);
                    itemViewHolder.relPrefDate.setVisibility(View.GONE);
                    itemViewHolder.txtDatePref.setVisibility(View.GONE);
                } else {
                    itemViewHolder.relPrefDate.setVisibility(View.VISIBLE);
                    itemViewHolder.txtDatePref.setVisibility(View.VISIBLE);
                    itemViewHolder.linMeetDate.setVisibility(View.GONE);
                    itemViewHolder.txtDateMeet.setVisibility(View.GONE);
                }
            }
            Logger.log("TAG", "JOB POOL TIME START" + startTimeRequired);
            Logger.log("TAG", "JOB POOL TIME END" + endTimeRequired);
            Logger.log("TAG", "JOB POOL DATE " + dateToshowRequired);

        }
    }

    /**
     * Gets the date with required pattern for calander.
     *
     * @param date                the date
     * @param datePattern         the date pattern
     * @param requiredDatePattern the required date pattern
     * @return the date with requiredpattern for calander
     * @throws ParseException the parse exception
     */
    private synchronized String getDateWithRequiredPattern(String date,
                                                           String datePattern, String requiredDatePattern)
            throws ParseException {

        String requiredDate = null;

        SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
        SimpleDateFormat formatterOut = new SimpleDateFormat(requiredDatePattern);

        try {

            Date outputDate = formatter.parse(date);
            requiredDate = formatterOut.format(outputDate);
            System.out.println(date);
            System.out.println();

        } catch (ParseException e) {
            e.printStackTrace();
            requiredDate = date;
        }

        return requiredDate;
    }

    @Override
    public int getItemCount() {
        int count = listIndex * baseCount;
        if (count < allJobList.size()) {
            return count + incrementValue + 1;
        } else {
            return allJobList.size() + incrementValue;
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        } else if (isPositionFooter(position)) {
            return TYPE_FOOTER;
        }
        return TYPE_ITEM;
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

    private HashMap<String, String> getItem(int position) {
        return allJobList.get(position - incrementValue);
    }

    private boolean isPositionHeader(int position) {
        if (isHeaderShown) {
            return position == HEADER_POSITION;
        } else {
            return false;
        }
    }

    private boolean isPositionFooter(int position) {
        int count = listIndex * baseCount;
        if (count < allJobList.size()) {
            return position == count + incrementValue;
        } else {
            return false;
        }
    }

    public void setHeaderShown(boolean isHeaderShown) {
        this.isHeaderShown = isHeaderShown;
    }

    public boolean isHeaderShown() {
        return isHeaderShown;
    }

    public void setIncrementValue(int incrementValue) {
        this.incrementValue = incrementValue;
    }

    /**
     * Adds duplicate list for searching.
     */
    public void setDuplicateList() {
        originalJobList = new ArrayList<>();
        originalJobList.addAll(allJobList);
    }

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
                ArrayList<HashMap<String, String>> list = (ArrayList<HashMap<String, String>>) results.values;
                allJobList.addAll(list);

                emptyListListener.onEmptyList(list);

                notifyDataSetChanged();
            }
        };
        return filter;
    }

}
