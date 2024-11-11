package com.synchroteam.listadapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.SwipeLayout.DragEdge;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.UpdateActivityDialog;
import com.synchroteam.fragmenthelper.UnavabilitiesFragmentHelper;
import com.synchroteam.synchroteam.SyncoteamNavigationActivity;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

// TODO: Auto-generated Javadoc

/**
 * Adapter Class to for Deadline Jobs List.
 */
/*
 new class added to resolve headerviewlist adapter issue(library), removed PinnedSectionListAdapter
 */
public class UnavabilitiesListAdapterNewDuplicate extends BaseAdapter
         {

    /**
     * The deadline job beans.
     */
    private List<HashMap<String, String>> unavabilitiesList;

    /**
     * The layout inflater.
     */
    private LayoutInflater layoutInflater;

    private Activity activity;

    private String startTime, endTime;

    private Dao dataAccessObject;

    private UnavabilitiesFragmentHelper frag;

    // private static final String TAG = "UnavabilitiesListAdapter";

    /**
     * Instantiates a new deadline jobs list adapter.
     *
     * @param activity          the activity
     * @param unavabilitiesList the unavabilities list
     */
    public UnavabilitiesListAdapterNewDuplicate(Activity activity,
                                                UnavabilitiesFragmentHelper frag,
                                                List<HashMap<String, String>> unavabilitiesList) {
        // TODO Auto-generated constructor stub
        this.activity = activity;
        this.unavabilitiesList = unavabilitiesList;
        layoutInflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.frag = frag;

        dataAccessObject = DaoManager.getInstance();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return unavabilitiesList.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public HashMap<String, String> getItem(int position) {
        // TODO Auto-generated method stub
        return unavabilitiesList.get(position);
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

        final HashMap<String, String> unavabilityHashMap = getItem(position);
        View view = convertView;
        // ViewHolder holder = null;
        // if (convertView == null) {
        // convertView = layoutInflater.inflate(
        // R.layout.layout_deadline_jobs_list_item, null);
        // holder = new ViewHolder();

        if (Objects.equals(unavabilityHashMap.get(KEYS.Unavabilities.IS_HEADER), KEYS.Unavabilities.TRUE)) {
            view = layoutInflater.inflate(
                    R.layout.layout__header_alljob_listitem, parent,false);
            TextView dateDetails = (TextView) view
                    .findViewById(R.id.dateDetailCurrentJobsTv);
            dateDetails.setText(unavabilityHashMap
                    .get(KEYS.Unavabilities.DT_DEBUT));

        } else {
            view = layoutInflater.inflate(
                    R.layout.layout_unavabilities_list_item, parent,false);

            SwipeLayout swipeLayout = (SwipeLayout) view
                    .findViewById(R.id.unavailabilitySwipeLayout);

            // set show mode.
            swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

            LinearLayout linUnavailabilityContainer = (LinearLayout) view
                    .findViewById(R.id.linUnavailabilityContainer);

            linUnavailabilityContainer
                    .setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            String startEndTime = unavabilityHashMap
                                    .get(KEYS.Unavabilities.PLAN_TIME_START_END);

                            /*
                             * Logic to find out the fourth - symbol in the date
                             * string.
                             */
//                            int counter = 0;
//                            int pos = 0;
//                            for (int i = 0; i < startEndTime.length(); i++) {
//                                if (startEndTime.charAt(i) == '-') {
//                                    counter++;
//                                    if (counter == 3) {
//                                        pos = i;
//                                    }
//                                }
//                            }
//
//                            String startDateTime = startEndTime.substring(0,
//                                    pos);
//                            String endDateTime = startEndTime
//                                    .substring(pos + 2);

                            assert startEndTime != null;
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

                            String planStartDate = unavabilityHashMap
                                    .get(KEYS.Unavabilities.PLAN_START_DATE_TIME);
                            String planEndDate = unavabilityHashMap
                                    .get(KEYS.Unavabilities.PLAN_END_DATE_TIME);
//                            Intent intentUpdateUnavail = new Intent();
//                            Bundle bundle = new Bundle();
//                            intentUpdateUnavail.setClass(activity,
//                                    UpdateUnavailability.class);
//                            bundle.putString("id", unavabilityHashMap
//                                    .get(KEYS.Unavabilities.UNAVAILABILITY_ID));
//                            bundle.putString("color_code", unavabilityHashMap
//                                    .get(KEYS.Unavabilities.IMG));
//                            bundle.putString(KEYS.Unavabilities.TYPE,
//                                    unavabilityHashMap
//                                            .get(KEYS.Unavabilities.TYPE));
//                            bundle.putString("start_date", startDate);
//                            bundle.putString("start_time", startTime);
//                            bundle.putString("end_date", endDate);
//                            bundle.putString("end_time", endTime);
//                            bundle.putString("start_date_time", planStartDate);
//                            bundle.putString("end_date_time", planEndDate);
//                            bundle.putString("description", unavabilityHashMap
//                                    .get(KEYS.Unavabilities.CLTVILLE));
//
//                            bundle.putString(KEYS.Unavabilities.ID_USER, unavabilityHashMap.get(KEYS.Unavabilities.ID_USER));
//                            bundle.putString(KEYS.Unavabilities.ID_GROUPE, unavabilityHashMap.get(KEYS.Unavabilities.ID_GROUPE));
//                            bundle.putString(KEYS.Unavabilities.FL_UNAVAILABLE, unavabilityHashMap.get(KEYS.Unavabilities.FL_UNAVAILABLE));
//                            bundle.putString(KEYS.Unavabilities.FL_PAYABLE, unavabilityHashMap.get(KEYS.Unavabilities.FL_PAYABLE));
//                            intentUpdateUnavail.putExtras(bundle);
//                            activity.startActivity(intentUpdateUnavail);

                            if (endTime == null || endTime.trim().length() == 0) {

                                if (activity != null) {
                                    ((SyncoteamNavigationActivity) activity).openUnavailabilityActivity();
                                }

                            } else {
                                UpdateActivityDialog.newInstance(unavabilityHashMap
                                                .get(KEYS.Unavabilities.UNAVAILABILITY_ID), unavabilityHashMap
                                                .get(KEYS.Unavabilities.TYPE), startDateTime, endDateTime,
                                        planStartDate, planEndDate, unavabilityHashMap
                                                .get(KEYS.Unavabilities.CLTVILLE),
                                        unavabilityHashMap.get(KEYS.Unavabilities.ID_USER),
                                        unavabilityHashMap.get(KEYS.Unavabilities.FL_UNAVAILABLE),
                                        unavabilityHashMap.get(KEYS.Unavabilities.FL_PAYABLE),
                                        unavabilityHashMap.get(KEYS.Unavabilities.ID_TYPE_CONGE))
                                        .show(((SyncoteamNavigationActivity) activity).getSupportFragmentManager(), "");
                            }

                        }
                    });

            TextView nameUnavvabilityTv = (TextView) view
                    .findViewById(R.id.nameUnavability);
            TextView stopUnavailability = (TextView) view
                    .findViewById(R.id.txtStopUnavailability);

            nameUnavvabilityTv.setText(unavabilityHashMap
                    .get(KEYS.Unavabilities.TYPE));

            /** NEW CHANGES **/

            GradientDrawable gd = (GradientDrawable) linUnavailabilityContainer
                    .getBackground();
            gd.setColor(Color.parseColor("#"
                    + unavabilityHashMap.get(KEYS.Unavabilities.IMG)));
            gd.invalidateSelf();

            // linUnavailabilityContainer.getBackground().setColorFilter(
            // Color.parseColor("#"
            // + unavabilityHashMap.get(KEYS.Unavabilities.IMG)),
            // PorterDuff.Mode.DARKEN);
            /** NEW CHANGES **/

            TextView startTimeUnavabilityTv = (TextView) view
                    .findViewById(R.id.startTimeUnavabilityTv);
            TextView endTimeUnavabilityTv = (TextView) view
                    .findViewById(R.id.EndTimeUnavabilityTv);

            android.widget.TextView startIcon = (android.widget.TextView) view
                    .findViewById(R.id.txtStartTimeIcon);
            android.widget.TextView endIcon = (android.widget.TextView) view
                    .findViewById(R.id.txtEndTimeIcon);

            Typeface typeFace = Typeface.createFromAsset(activity.getAssets(),
                    "fonts/fontawesome-webfont.ttf");
            startIcon.setTypeface(typeFace);
            endIcon.setTypeface(typeFace);

            String startEndTime = unavabilityHashMap
                    .get(KEYS.Unavabilities.PLAN_TIME_START_END);

            String endDate = unavabilityHashMap
                    .get(KEYS.Unavabilities.END_DATE);

            if (TextUtils.isEmpty(endDate)) {
                endIcon.setVisibility(View.GONE);
            } else {
                endIcon.setVisibility(View.VISIBLE);
            }

            /*
             * Logic to find out the fourth - symbol in the date string.
             */
//            int counter = 0;
//            int pos = 0;
//            for (int i = 0; i < startEndTime.length(); i++) {
//                if (startEndTime.charAt(i) == '-') {
//                    counter++;
//                    if (counter == 3) {
//                        pos = i;
//                    }
//                }
//            }
//
//            startTime = startEndTime.substring(0, pos);
//            endTime = startEndTime.substring(pos + 1);

            assert startEndTime != null;
            startTime = startEndTime.substring(0, startEndTime.indexOf("-"));
            endTime = startEndTime.substring(startEndTime.indexOf("-") + 2);

            startTimeUnavabilityTv.setText(startTime);
            if (endTime == null || endTime.trim().length() == 0) {
                //todo add text in progress
                endTimeUnavabilityTv.setText(activity.getResources().getString(R.string.txt_Activity_in_progress));
                endTimeUnavabilityTv.setTextColor(activity.getResources().getColor(R.color.red_color));
            } else {
                endTimeUnavabilityTv.setText(endTime);
                endTimeUnavabilityTv.setTextColor(activity.getResources().getColor(R.color.unavailability_start_end_text_color));
                endIcon.setVisibility(View.VISIBLE);
            }
            final TextView discriptionUnavabilityTv = (TextView) view
                    .findViewById(R.id.discriptionUnavabilityTv);
            String desc = unavabilityHashMap
                    .get(KEYS.Unavabilities.CLTVILLE);

            //new changes desc
            if (desc != null && desc.length() > 0) {
                discriptionUnavabilityTv.setVisibility(View.VISIBLE);
                discriptionUnavabilityTv.setText(unavabilityHashMap
                        .get(KEYS.Unavabilities.CLTVILLE));
            } else {
                discriptionUnavabilityTv.setText(unavabilityHashMap
                        .get(KEYS.Unavabilities.CLTVILLE));
                discriptionUnavabilityTv.setVisibility(View.GONE);
            }


            if (endDate == null) {
                // swipeLayout
                // .setBottomViewIds(0, R.id.bottom_wrapper_right, 0, 0);
                swipeLayout.setSwipeEnabled(true);
                swipeLayout.setDragEdge(DragEdge.Left);
                swipeLayout.setDragEdge(DragEdge.Right);

            } else {
                // swipeLayout.setBottomViewIds(0, 0, 0, 0);
                swipeLayout.setSwipeEnabled(false);
            }

            stopUnavailability.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss.SSS");
                    String currentDateStr = sdf.format(cal.getTime());

                    new endUnavailabilityAsync().execute(unavabilityHashMap
                                    .get(KEYS.Unavabilities.UNAVAILABILITY_ID),
                            currentDateStr);
                }
            });

        }

        return view;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.BaseAdapter#getViewTypeCount()
     */
    @Override
    public int getViewTypeCount() {
        // TODO Auto-generated method stub
        return 2;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.BaseAdapter#getItemViewType(int)
     */
    @Override
    public int getItemViewType(int position) {
        // TODO Auto-generated method stub
        int type;
        if (Objects.equals(unavabilitiesList.get(position).get(KEYS.Unavabilities.IS_HEADER), KEYS.Unavabilities.TRUE)) {
            type = KEYS.Unavabilities.TYPE_SECTIONAL_HEADER;
        } else {
            type = KEYS.Unavabilities.TYPE_ITEM_HEADER;
        }

        return type;
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
            // TODO Auto-generated method stub
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
            // TODO Auto-generated method stub

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
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            DialogUtils.dismissProgressDialog();

            boolean drp = result.booleanValue();
            if (drp) {
                Toast.makeText(activity,
                        activity.getString(R.string.unavailability_stopped),
                        Toast.LENGTH_SHORT).show();
                frag.doOnResume();
            } else {
                Toast.makeText(activity, R.string.msg_error, Toast.LENGTH_SHORT)
                        .show();
            }
        }

    }

}
