package com.synchroteam.listadapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.Conge;
import com.synchroteam.beans.LocationPoints;
import com.synchroteam.beans.TravelActivity;
import com.synchroteam.beans.UpdateDataBaseEvent;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.JobDetailPopupDialog;
import com.synchroteam.dialogs.UpdateActivityDialog;
import com.synchroteam.events.DrivingModeUpdateEvent;
import com.synchroteam.events.TravelStopEvent;
import com.synchroteam.fragmenthelper.CurrentJobsFragmentHelperNew;
import com.synchroteam.listeners.RvEmptyListener;
import com.synchroteam.roomDB.RoomDBSingleTone;
import com.synchroteam.roomDB.entity.LocationCoordinates;
import com.synchroteam.synchroteam.NewJob;
import com.synchroteam.synchroteam.SyncoteamNavigationActivity;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.technicalsupport.JobDetails;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.LocationUtils;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.SharedPref;
import com.synchroteam.utils.SynchroteamUitls;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Vector;

import de.greenrobot.event.EventBus;

/**
 * This adapter class inflates the view for current jobs while "Sort by none" is selected for the sorting option.
 * Created by Trident on 12/30/2016.
 */

public class CurrentJobsAdapterRv extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private boolean isHeaderShown;
    private static final int HEADER_POSITION = 0;
    private int incrementValue;

    private FragmentActivity activity;
    private RvEmptyListener emptyListListener;
    private ArrayList<HashMap<String, String>> currentJobList;
    private CurrentJobsFragmentHelperNew helper;
    private Dao dataAccessObject;

    private SimpleDateFormat headerDateFormat, oldDatePattern;
    private String started;
    private String suspended;
    private String scheduled;
    private int startedTextColor;
    private int suspendedTextColor;
    private int scheduledTextColor;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_IS_JOB = 1;
    private static final int TYPE_IS_ACTIVITY = 2;
    private static final int TYPE_FOOTER = 3;

    private int listIndex;
    private int baseCount = 20;

    public CurrentJobsAdapterRv(FragmentActivity activity, RvEmptyListener emptyListListener,
                                ArrayList<HashMap<String, String>> currentJobList, CurrentJobsFragmentHelperNew helper) {
        this.activity = activity;
        this.emptyListListener = emptyListListener;
        this.currentJobList = currentJobList;
        this.helper = helper;
        headerDateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        oldDatePattern = new SimpleDateFormat("yyyy-MM-dd");
        dataAccessObject = DaoManager.getInstance();

        started = activity.getString(R.string.textStarted);
        suspended = activity.getString(R.string.textSuspended);
        scheduled = activity.getString(R.string.textScheduled);

        startedTextColor = ContextCompat.getColor(activity, R.color.textColorGreen);
        suspendedTextColor = ContextCompat.getColor(activity, R.color.textColorOrange);
        scheduledTextColor = ContextCompat.getColor(activity, R.color.textColorBlack);
    }

    /**
     * Normal item view holder
     */
    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

        public ImageView leftStripIv;
        public TextView jobStatusIv;
        public TextView jobNameTv;
        public TextView customerAddress;
        public TextView dateOrTimeTv;
        public TextView jobPriorityTv;
        public ImageView priorityImage;
        public TextView clientNameTv;
        public TextView startTimeTv;
        public RelativeLayout jobTimeStatusContainer;
        public TextView dateDetailCurrentJobsTv;
        public android.widget.TextView txtLockIc;


        public ItemViewHolder(View view) {
            super(view);

            dateDetailCurrentJobsTv = (TextView) view.findViewById(R.id.dateDetailCurrentJobsTv);
            leftStripIv = (ImageView) view.findViewById(R.id.stripColorIv);
            jobStatusIv = (TextView) view.findViewById(R.id.jobStatusTv);
            jobNameTv = (TextView) view.findViewById(R.id.jobNameTv);
            customerAddress = view.findViewById(R.id.customer_addressTv);
            dateOrTimeTv = (TextView) view.findViewById(R.id.dateOrTimeTv);
            jobPriorityTv = (TextView) view.findViewById(R.id.jobPriorityTv);
            priorityImage = view.findViewById(R.id.priorityImage);
            clientNameTv = (TextView) view.findViewById(R.id.clientNameTv);
            startTimeTv = (TextView) view.findViewById(R.id.startTime);
            jobTimeStatusContainer = (RelativeLayout) view.findViewById(R.id.jobTimeStatusRl);
            txtLockIc = (android.widget.TextView) view.findViewById(R.id.txt_ic_lock);

            Typeface typeface = Typeface.createFromAsset(activity.getAssets(), activity.getString(R.string.fontName_fontAwesome));
            txtLockIc.setTypeface(typeface);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
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



                            JobDetailPopupDialog jobDetailPopupDialog = JobDetailPopupDialog.newInstance(bundle,
                                    new JobDetailPopupDialog.JobDetailsNavigationInterface() {
                                        @Override
                                        public void open() {
                                            Intent jobDetailIntent = new Intent(activity,
                                                    JobDetails.class);
                                            jobDetailIntent.putExtras(bundle);
                                            activity.startActivity(jobDetailIntent);
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

    public class ActivityViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        View parentView;
        SwipeLayout swipeLayout;
        TextView nameUnavvabilityTv;
        TextView stopUnavailability;
        LinearLayout linUnavailabilityContainer;
        TextView startTimeUnavabilityTv;
        TextView endTimeUnavabilityTv;
        android.widget.TextView startIcon;
        android.widget.TextView endIcon;
        TextView discriptionUnavabilityTv;

        public ActivityViewHolder(View itemView) {
            super(itemView);
            parentView = itemView;
            swipeLayout = (SwipeLayout) itemView
                    .findViewById(R.id.unavailabilitySwipeLayout);
            nameUnavvabilityTv = (TextView) itemView
                    .findViewById(R.id.nameUnavability);
            stopUnavailability = (TextView) itemView
                    .findViewById(R.id.txtStopUnavailability);
            linUnavailabilityContainer = (LinearLayout) itemView
                    .findViewById(R.id.linUnavailabilityContainer);
            startTimeUnavabilityTv = (TextView) itemView
                    .findViewById(R.id.startTimeUnavabilityTv);
            endTimeUnavabilityTv = (TextView) itemView
                    .findViewById(R.id.EndTimeUnavabilityTv);

            startIcon = (android.widget.TextView) itemView
                    .findViewById(R.id.txtStartTimeIcon);
            endIcon = (android.widget.TextView) itemView
                    .findViewById(R.id.txtEndTimeIcon);
            discriptionUnavabilityTv = (TextView) itemView
                    .findViewById(R.id.discriptionUnavabilityTv);


            Typeface typeFace = Typeface.createFromAsset(
                    activity.getAssets(), "fonts/fontawesome-webfont.ttf");
            startIcon.setTypeface(typeFace);
            endIcon.setTypeface(typeFace);

            // set show mode.
            swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

            stopUnavailability.setOnClickListener(this);
            linUnavailabilityContainer.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                HashMap<String, String> currentJobHashMap = getItem(getAdapterPosition());
                if (currentJobHashMap != null)
                    switch (view.getId()) {
                        case R.id.txtStopUnavailability:
                            @SuppressWarnings("unchecked")
                            Calendar cal = Calendar.getInstance();
                            SimpleDateFormat sdf = new SimpleDateFormat(
                                    "yyyy-MM-dd HH:mm:ss.SSS");
                            String currentDateStr = sdf.format(cal.getTime());

                            new endUnavailabilityAsync().execute(currentJobHashMap
                                            .get(KEYS.Unavabilities.UNAVAILABILITY_ID),
                                    currentDateStr);
                            break;

                        case R.id.linUnavailabilityContainer:
                            @SuppressWarnings("unchecked")

                            String startEndTime = currentJobHashMap
                                    .get(KEYS.Unavabilities.PLAN_TIME_START_END);
                            String planStartDate = currentJobHashMap
                                    .get(KEYS.Unavabilities.PLAN_START_DATE_TIME);
                            String planEndDate = currentJobHashMap
                                    .get(KEYS.Unavabilities.PLAN_END_DATE_TIME);

                            if (startEndTime != null) {
                                String startDateTime = startEndTime.substring(0, startEndTime.indexOf("-"));
                                String endDateTime = startEndTime.substring(startEndTime.indexOf("-") + 2);

//                    String startDate = startDateTime.substring(0,
//                            startDateTime.indexOf(" "));
//                    String startTime = startDateTime
//                            .substring(startDateTime.indexOf(" ") + 1);
//
//                    String endDate = null;
//                    String endTime = null;
//
//                    if (!StringUtils.isEmptyString(endDateTime)) {
//                        endDate = endDateTime.substring(0,
//                                endDateTime.indexOf(" ") + 1);
//                        endTime = endDateTime.substring(endDateTime
//                                .indexOf(" ") + 1);
//
//                    }
//                    Intent intentUpdateUnavail = new Intent();
//
//                    Bundle bundle = new Bundle();
//                    intentUpdateUnavail.setClass(activity,
//                            UpdateUnavailability.class);
//                    bundle.putString(
//                            "id",
//                            currentJobHashMap
//                                    .get(KEYS.Unavabilities.UNAVAILABILITY_ID));
//                    bundle.putString("color_code",
//                            currentJobHashMap
//                                    .get(KEYS.Unavabilities.IMG));
//                    bundle.putString(KEYS.Unavabilities.TYPE,
//                            currentJobHashMap
//                                    .get(KEYS.Unavabilities.TYPE));
//                    bundle.putString("start_date", startDate);
//                    bundle.putString("start_time", startTime);
//                    bundle.putString("end_date", endDate);
//                    bundle.putString("end_time", endTime);
//                    bundle.putString("start_date_time",
//                            planStartDate);
//                    bundle.putString("end_date_time", planEndDate);
//                    bundle.putString(
//                            "description",
//                            currentJobHashMap
//                                    .get(KEYS.Unavabilities.CLTVILLE));
//
//                    bundle.putString(KEYS.Unavabilities.ID_USER, currentJobHashMap.get(KEYS.Unavabilities.ID_USER));
//                    bundle.putString(KEYS.Unavabilities.ID_GROUPE, currentJobHashMap.get(KEYS.Unavabilities.ID_GROUPE));
//                    intentUpdateUnavail.putExtras(bundle);
//                    activity.startActivity(intentUpdateUnavail);
                                if (endDateTime == null || endDateTime.trim().length() == 0) {
                                    if (activity != null) {
                                        ((SyncoteamNavigationActivity) activity).openUnavailabilityActivity();
                                    }
                                } else {
                                    if (activity != null) {
                                        UpdateActivityDialog.newInstance(currentJobHashMap
                                                                .get(KEYS.Unavabilities.UNAVAILABILITY_ID), currentJobHashMap
                                                                .get(KEYS.Unavabilities.TYPE), startDateTime, endDateTime,
                                                        planStartDate, planEndDate, currentJobHashMap
                                                                .get(KEYS.Unavabilities.CLTVILLE),
                                                        currentJobHashMap.get(KEYS.Unavabilities.ID_USER),
                                                        currentJobHashMap.get(KEYS.Unavabilities.FL_UNAVAILABLE),
                                                        currentJobHashMap.get(KEYS.Unavabilities.FL_PAYABLE),
                                                        currentJobHashMap.get(KEYS.Unavabilities.ID_TYPE_CONGE))
                                                .show(((SyncoteamNavigationActivity) activity).getSupportFragmentManager(), "");
                                    }
                                }
                            }
                            break;
                    }
            }
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

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_IS_JOB) {
            View view = LayoutInflater.from(activity).inflate(R.layout.rowview_all_job_with_date, parent, false);
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_IS_ACTIVITY) {
            View view = LayoutInflater.from(activity).inflate(R.layout.layout_unavabilities_list_item, parent, false);
            return new ActivityViewHolder(view);
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
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            holder.itemView.setClickable(true);
            if (position != RecyclerView.NO_POSITION) {
                HashMap<String, String> hmCurrentJobs = getItem(position);
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

                if (hmCurrentJobs != null) {

                    String hasHeader = hmCurrentJobs.get(KEYS.CurrentJobs.HAS_HEADER);
                    if (hasHeader != null)
                        if (hasHeader.equalsIgnoreCase("true")) {
                            itemViewHolder.dateDetailCurrentJobsTv.setVisibility(View.VISIBLE);
                            try {
                                itemViewHolder.dateDetailCurrentJobsTv.setText(headerDateFormat.
                                        format(Objects.requireNonNull(oldDatePattern.parse(Objects.requireNonNull
                                                (hmCurrentJobs.get(KEYS.CurrentJobs.ALL_JOB_HEADER))))));
                            } catch (ParseException e) {
                                Logger.printException(e);
                            }
                        } else {
                            itemViewHolder.dateDetailCurrentJobsTv.setVisibility(View.GONE);
                        }

                    itemViewHolder.jobNameTv.setText(hmCurrentJobs.get(KEYS.CurrentJobs.TYPE));
                    itemViewHolder.customerAddress.setText(hmCurrentJobs.get(KEYS.CurrentJobs.ADR_GLOBAL));
                    if (hmCurrentJobs.get(KEYS.CurrentJobs.NOMSITE).length()>0){
                        itemViewHolder.clientNameTv.setText(hmCurrentJobs
                                .get(KEYS.CurrentJobs.NOM_CLIENT_INTERV)+": "+hmCurrentJobs.get(KEYS.CurrentJobs.NOMSITE));
                    }else {
                        itemViewHolder.clientNameTv.setText(hmCurrentJobs
                                .get(KEYS.CurrentJobs.NOM_CLIENT_INTERV));
                    }

                    int status = Integer.parseInt(Objects.requireNonNull(hmCurrentJobs
                            .get(KEYS.CurrentJobs.CD_STATUS)));
                    itemViewHolder.jobPriorityTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                    DateFormat dateFormatCurrentJobList;
                    dateFormatCurrentJobList = DateFormat.getDateInstance(DateFormat.LONG);

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

                            itemViewHolder.dateOrTimeTv.setText(hmCurrentJobs
                                        .get(KEYS.CurrentJobs.DATE_TO_SHOW) + " ");
                            // yearTv.setVisibility(View.VISIBLE);
                            // yearTv.setText(currentJobHashmap.get(KEYS.CurrentJobs.YEAR_TO_SHOW));
                            Logger.log(">>>>>",
                                    hmCurrentJobs.get(KEYS.CurrentJobs.TIME_TO_SHOW));
                            itemViewHolder.startTimeTv.setText(hmCurrentJobs
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

                            itemViewHolder.dateOrTimeTv.setText(hmCurrentJobs
                                        .get(KEYS.CurrentJobs.DATE_TO_SHOW) + " ");
                            // yearTv.setText(currentJobHashmap.get(KEYS.CurrentJobs.YEAR_TO_SHOW));

                            itemViewHolder.startTimeTv.setText(hmCurrentJobs
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
                            if (!TextUtils.isEmpty(hmCurrentJobs
                                    .get(KEYS.CurrentJobs.DATE_TO_SHOW))) {
                                itemViewHolder.dateOrTimeTv.setText(hmCurrentJobs
                                            .get(KEYS.CurrentJobs.DATE_TO_SHOW) + " ");
                            }

                            // yearTv.setText(currentJobHashmap.get(KEYS.CurrentJobs.YEAR_TO_SHOW));
                            itemViewHolder.startTimeTv.setText(hmCurrentJobs
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

                            itemViewHolder.jobNameTv.setText(hmCurrentJobs.get(KEYS.CurrentJobs.TYPE));
                            itemViewHolder.customerAddress.setText(hmCurrentJobs.get(KEYS.CurrentJobs.ADR_GLOBAL));
                            if (hmCurrentJobs.get(KEYS.CurrentJobs.NOMSITE).length()>0){
                                itemViewHolder.clientNameTv.setText(hmCurrentJobs
                                        .get(KEYS.CurrentJobs.NOM_CLIENT_INTERV)+": "+hmCurrentJobs.get(KEYS.CurrentJobs.NOMSITE));
                            }else {
                                itemViewHolder.clientNameTv.setText(hmCurrentJobs
                                        .get(KEYS.CurrentJobs.NOM_CLIENT_INTERV));
                            }
                            itemViewHolder.dateOrTimeTv.setText(hmCurrentJobs
                                        .get(KEYS.CurrentJobs.DATE_TO_SHOW) + " ");

                            itemViewHolder.startTimeTv.setText(hmCurrentJobs
                                    .get(KEYS.CurrentJobs.TIME_TO_SHOW));
                            break;

                        default:
                            break;
                    }

                    int prority = Integer.parseInt(Objects.requireNonNull(hmCurrentJobs
                            .get(KEYS.CurrentJobs.PRIORITY)));

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

                    String dtMeeting = hmCurrentJobs.get(KEYS.CurrentJobs.DATEMEETING);
                    if (dtMeeting != null)
                        if (!dtMeeting.equalsIgnoreCase("null")) {
                            itemViewHolder.txtLockIc.setVisibility(View.VISIBLE);
                        } else {
                            itemViewHolder.txtLockIc.setVisibility(View.GONE);
                        }
                }
            }
        } else if (holder instanceof ActivityViewHolder) {
            holder.itemView.setClickable(true);
            if (position != RecyclerView.NO_POSITION) {
                HashMap<String, String> hmCurrentJobs = getItem(position);

                ActivityViewHolder activityViewHolder = (ActivityViewHolder) holder;
                if (hmCurrentJobs != null) {
                    activityViewHolder.nameUnavvabilityTv.setText(hmCurrentJobs
                            .get(KEYS.Unavabilities.TYPE));

                    GradientDrawable gd = (GradientDrawable) activityViewHolder.linUnavailabilityContainer
                            .getBackground();
                    gd.setColor(Color.parseColor("#"
                            + hmCurrentJobs.get(KEYS.Unavabilities.IMG)));
                    gd.invalidateSelf();

                    String selectedDate = hmCurrentJobs
                            .get(KEYS.Unavabilities.SELECTED_DATE);
                    String endDate = hmCurrentJobs
                            .get(KEYS.Unavabilities.END_DATE);

                    if (endDate == null) {
                        // swipeLayout.setBottomViewIds(0,
                        // R.id.bottom_wrapper_right,
                        // 0, 0);
                        activityViewHolder.swipeLayout.setSwipeEnabled(true);
                        activityViewHolder.swipeLayout.setDragEdge(SwipeLayout.DragEdge.Left);
                        activityViewHolder.swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
                    } else {
                        // swipeLayout.setBottomViewIds(0, 0, 0, 0);
                        activityViewHolder.swipeLayout.setSwipeEnabled(false);
                    }

                    if (TextUtils.isEmpty(endDate)) {
                        activityViewHolder.endIcon.setVisibility(View.GONE);
                    } else {
                        activityViewHolder.endIcon.setVisibility(View.GONE);
                    }


                    String startEndTime = hmCurrentJobs
                            .get(KEYS.Unavabilities.PLAN_TIME_START_END);

                    if (startEndTime != null) {
                        String startTime = startEndTime.substring(0, startEndTime.indexOf("-"));
                        String endTime = startEndTime.substring(startEndTime.indexOf("-") + 2);

                        activityViewHolder.startTimeUnavabilityTv.setText(startTime);
                        activityViewHolder.endTimeUnavabilityTv.setText(endTime);

                        if (endTime == null || endTime.trim().length() == 0) {
                            activityViewHolder.endTimeUnavabilityTv.setText(activity.getResources().getString(R.string.txt_Activity_in_progress));
                            activityViewHolder.endTimeUnavabilityTv.setTextColor(activity.getResources().getColor(R.color.red_color));
                        } else {
                            activityViewHolder.endTimeUnavabilityTv.setText(endTime);
                            activityViewHolder.endIcon.setVisibility(View.VISIBLE);
                            activityViewHolder.endTimeUnavabilityTv.setTextColor(activity.getResources().getColor(R.color.unavailability_start_end_text_color));
                        }
                    }
                    String desc = Objects.requireNonNull(hmCurrentJobs.get(KEYS.Unavabilities.CLTVILLE)).trim();

                    if (desc != null && desc.length() > 0) {
                        activityViewHolder.discriptionUnavabilityTv.setVisibility(View.VISIBLE);
                        activityViewHolder.discriptionUnavabilityTv.setText(desc);
                    } else {
                        activityViewHolder.discriptionUnavabilityTv.setVisibility(View.GONE);
                    }

                    //this check was done while iterating the list
//            if (!TextUtils.isEmpty(endDate)) {
//
//                SimpleDateFormat formatter = new SimpleDateFormat(
//                        "dd/MM/yyyy HH:mm:ss");
//                try {
//                    Date dateSel = formatter.parse(selectedDate);
//                    Date dateEnd = formatter.parse(endDate);
//
//                    if (dateEnd.compareTo(dateSel) < 0) {
//                        activityViewHolder.parentView.setVisibility(View.GONE);
//                    }
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            }
                }
            }
        }
    }


    @Override
    public int getItemCount() {
//        return currentJobList.size() + incrementValue + 1;

        int count = listIndex * baseCount;
        if (count < currentJobList.size()) {
            return count + incrementValue + 1;
        } else {
            return currentJobList.size() + incrementValue;
        }
    }

    /**
     * Sets the index position.
     *
     * @param index the new index position
     */
    public void setIndexPosition(int index) {
        this.listIndex = index;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        } else if (isPositionFooter(position)) {
            return TYPE_FOOTER;
        } else if (isJobItem(position)) {
            return TYPE_IS_JOB;
        } else {
            return TYPE_IS_ACTIVITY;
        }
    }

    private boolean isPositionHeader(int position) {
        if (isHeaderShown) {
            return position == HEADER_POSITION;
        } else {
            return false;
        }
    }

    private boolean isPositionFooter(int position) {
//        int count = currentJobList.size() + incrementValue;
//        if (position == count) {
//            return true;
//        } else {
//            return false;
//        }

        int count = listIndex * baseCount;
        if (count < currentJobList.size()) {
            return position == count + incrementValue+ 1;
        } else {
            return false;
        }
    }

    private boolean isJobItem(int postion) {
        return Objects.equals(currentJobList.get(postion - incrementValue).get(KEYS.CurrentJobs.IS_CURRENT_JOB),
                KEYS.CurrentJobs.TRUE);
    }

    private HashMap<String, String> getItem(int position) {
//        if (currentJobList != null && currentJobList.size() > 0 &&
//                (position - incrementValue) < currentJobList.size())


        if (currentJobList != null && currentJobList.size() > 0 &&
                (position - incrementValue) < currentJobList.size())
            return currentJobList.get(position - incrementValue);
        else
            return null;
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

            boolean drp = false;

            TravelActivity travelActivity = dataAccessObject.isDrivingStarted();

            Conge unAvailabilityDetail = checkUnAvailabilityStarted();
            if (unAvailabilityDetail != null) {
                drp = dataAccessObject.updateUnavailabilityEndDate(
                        params[0], params[1]);
            } else if (travelActivity != null) {
//                drp = updateEndDateofTravelActivities(); // old code
                 Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
                    String currentDateStr = sdf.format(cal.getTime());

                    List<LocationCoordinates> locations = new ArrayList<>();
                    locations = RoomDBSingleTone.instance(activity)
                            .LocationCoordinatesDao().getAllLocationCoordinatesModels();
                    ArrayList<LocationPoints> locationList = new ArrayList<>();
                    double lat = 0.00;
                    double lon = 0.00;
                    double distTravelled = 0.00;
                    if (locations.size() > 0) {
                        for (LocationCoordinates loc : locations) {
                            LocationPoints loc1 = new LocationPoints(loc.getLatitude(), loc.getLongitude());
                            locationList.add(loc1);
                        }
                        lat = locationList.get(locationList.size() - 1).getLatitude();
                        lon = locationList.get(locationList.size() - 1).getLongitude();
                        double distanceTravelled = calculateTotalDistance(locationList);
                        distTravelled = distanceTravelled;
                    }

                    drp = dataAccessObject.updateStopLatLonAndFinishDriving(
                            travelActivity.getIdTravel(), currentDateStr, lat, lon, distTravelled);

                RoomDBSingleTone.instance(activity).LocationCoordinatesDao().deteteAllLocationCoordinatesModels();
                List<LocationCoordinates> l = new ArrayList<>();

                if (drp){
                    SharedPref.setDistanceEnabled(false, activity);
                    //new changes v50
                    SharedPref.setIsTravelRunning(false, activity);
                    SharedPref.setRunningTravelName("", activity);
                    SharedPref.setIsTravelLastEntry(false, activity);
                    SharedPref.setTravelLocationData("", activity);

                    EventBus.getDefault().post(new TravelStopEvent());
                }

            }
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


//                EventBus.getDefault().post(new UpdateDataBaseEvent());
                EventBus.getDefault().post(new DrivingModeUpdateEvent());


//                helper.refreshList();

                if (dataAccessObject.checkSynchronisation(4) == 1) {
                    if (SynchroteamUitls.isNetworkAvailable(activity)) {
                        ((SyncoteamNavigationActivity) activity).synch();
                    } else {
                        helper.refreshList();
                    }

                }


            } else
                Toast.makeText(activity, R.string.msg_error, Toast.LENGTH_SHORT)
                        .show();
        }

    }

    /**
     * calculates the distance between all the points stored
     *
     * @return
     */
    private double calculateTotalDistance(ArrayList<LocationPoints> locationList) {

        double totDistance = 0.00;
        if (locationList != null && locationList.size() > 1) {
            for (int i = 0; i + 1 < locationList.size(); i++) {
                LocationPoints loc1 = locationList.get(i);
                LocationPoints loc2 = locationList.get(i + 1);
//                totDistance += LocationUtils.distanceBetweenTwoPoint(loc1.getLatitude(), loc1.getLongitude(), loc2.getLatitude(), loc2.getLongitude());
                totDistance += LocationUtils.distanceBetweenTwoPointAndroid(loc1.getLatitude(), loc1.getLongitude(), loc2.getLatitude(), loc2.getLongitude());
            }
            //for kms
//            totDistance = totDistance / 1000;

        }
        return totDistance;
    }

    private boolean updateEndDateofTravelActivities() {
        boolean result = false;
        TravelActivity travelActivity = dataAccessObject.isDrivingStarted();
        if (travelActivity != null) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
            String currentDateStr = sdf.format(cal.getTime());

            new Thread(new Runnable() {
                @Override
                public void run() {

                    List<LocationCoordinates> locations = new ArrayList<>();
                    locations = RoomDBSingleTone.instance(activity)
                            .LocationCoordinatesDao().getAllLocationCoordinatesModels();
                    ArrayList<LocationPoints> locationList = new ArrayList<>();
                    double lat = 0.00;
                    double lon = 0.00;
                    double distTravelled = 0.00;
                    if (locations.size() > 0) {
                        for (LocationCoordinates loc : locations) {
                            LocationPoints loc1 = new LocationPoints(loc.getLatitude(), loc.getLongitude());
                            locationList.add(loc1);
                        }
                        lat = locationList.get(locationList.size() - 1).getLatitude();
                        lon = locationList.get(locationList.size() - 1).getLongitude();
                        double distanceTravelled = calculateTotalDistance(locationList);
                        distTravelled = distanceTravelled;
                    }

                    boolean result = dataAccessObject.updateStopLatLonAndFinishDriving(
                            travelActivity.getIdTravel(), currentDateStr, lat, lon, distTravelled);

                    RoomDBSingleTone.instance(activity).LocationCoordinatesDao().deteteAllLocationCoordinatesModels();
                    List<LocationCoordinates> l = new ArrayList<>();

                    if (result) {

                        SharedPref.setDistanceEnabled(false, activity);
                        //new changes v50
                        SharedPref.setIsTravelRunning(false, activity);
                        SharedPref.setRunningTravelName("", activity);
                        SharedPref.setIsTravelLastEntry(false, activity);
                        SharedPref.setTravelLocationData("", activity);

                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                EventBus.getDefault().post(new DrivingModeUpdateEvent());
                                EventBus.getDefault().post(new TravelStopEvent());
                            }
                        });

                    }
                }
            }).start();
        }
        return result;
    }

//    private boolean updateEndDateofTravelActivities() {
//        boolean result = false;
//        TravelActivity travelActivity = dataAccessObject.isDrivingStarted();
//        if (travelActivity != null) {
//            Calendar cal = Calendar.getInstance();
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
//            String currentDateStr = sdf.format(cal.getTime());
//            String locationData = SharedPref.getTravelLocationData(activity);
//            double lat = 0, lon = 0, distTravelled = 0;
//            if (locationData != null && locationData.trim().length() > 1) {
//                try {
//                    JSONObject jsonObject = new JSONObject(locationData);
//                    lat = jsonObject.getDouble("Location_lat");
//                    lon = jsonObject.getDouble("Location_long");
//                    distTravelled = jsonObject.getDouble("Location_dist");
//
//                    Logger.log("TAG", "Location distance calc is ===?" + lat + " ," + lon + ", " + distTravelled);
//
//                    result = dataAccessObject.updateStopLatLonAndFinishDriving(
//                            travelActivity.getIdTravel(), currentDateStr, lat, lon, distTravelled);
//
//                    if (result) {
//
//                        SharedPref.setDistanceEnabled(false, activity);
//                        //new changes v50
//                        SharedPref.setIsTravelRunning(false, activity);
//                        SharedPref.setRunningTravelName("", activity);
//                        SharedPref.setIsTravelLastEntry(false, activity);
//                        SharedPref.setTravelLocationData("", activity);
//
//                        activity.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                EventBus.getDefault().post(new DrivingModeUpdateEvent());
//                                EventBus.getDefault().post(new TravelStopEvent());
//                            }
//                        });
//
//
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                RoomDBSingleTone.instance(activity).LocationCoordinatesDao().deteteAllLocationCoordinatesModels();
//                                List<LocationCoordinates> l = new ArrayList<>();
//                                l = RoomDBSingleTone.instance(activity).LocationCoordinatesDao().getAllLocationCoordinatesModels();
//                                Log.e("taf", l.toString());
//                            }
//                        }).start();
//
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    result = false;
//                }
//            }
//
//        }
//        return result;
//    }

    public boolean updateEndDateOfStartedActivities() {

        boolean result = false;
        Vector<Conge> vectConge = new Vector<Conge>();
        vectConge = dataAccessObject.getCongeNewExceptClockInActivity();
        Enumeration<Conge> enindisp = vectConge.elements();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String currentDate = sdf.format(cal.getTime());
        while (enindisp.hasMoreElements()) {
            Conge indisp = enindisp.nextElement();
            if (indisp.getDtFin() == null) {
                result = dataAccessObject.updateUnavailabilityEndDate(
                        indisp.getIdConge(), currentDate);

                //todo new 50 changes
            }
        }
        return result;
    }

    public Conge checkUnAvailabilityStarted() {
        Conge indisp;
        Vector<Conge> vectConge = new Vector<Conge>();
        vectConge = dataAccessObject.getCongeNewExceptClockInActivity();
        Enumeration<Conge> enindisp = vectConge.elements();
        while (enindisp.hasMoreElements()) {
            indisp = enindisp.nextElement();
            if (indisp.getDtFin() == null) {
                return indisp;
            }
        }
        return null;
    }
}
