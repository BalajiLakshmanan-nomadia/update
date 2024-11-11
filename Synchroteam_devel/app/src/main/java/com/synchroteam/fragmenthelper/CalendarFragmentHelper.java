package com.synchroteam.fragmenthelper;

import hirondelle.date4j.DateTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.caldroidx.CaldroidFragment;
import com.caldroidx.CaldroidListener;
import com.caldroidx.CalendarHelper;
import com.synchroteam.beans.DateTimeAlongWithJobsCountBean;
import com.synchroteam.baseactivity.SyncroTeamBaseActivity;
import com.synchroteam.beans.CalendarAllJobBeans;
import com.synchroteam.beans.EnableSynchronizationAddJobEvent;
import com.synchroteam.beans.JobsCount;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.fragment.CalendarFragment;
import com.synchroteam.fragment.CustomCalanderFragment;
import com.synchroteam.synchroteam.JobsListOnCurrentDate;
import com.synchroteam.synchroteam.SyncoteamNavigationActivity;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc

/***
 *
 * This class is responsible to handle all the functionality of Calendar screen.
 * 1.To inflate layout of custom calendar 2.To control the navigation inside
 * calendar 3. To Show Status of Job on Calendar.
 *
 *
 * @author $Ideavate Solution
 *
 */
public class CalendarFragmentHelper implements HelperInterface {

    public static final String TAG = "CalendarFragmentHelper";
    /**
     * The syncro team base activity.
     */
    private final SyncroTeamBaseActivity syncroTeamBaseActivity;

    /**
     * The calendar fragment.
     */
    private final CalendarFragment calendarFragment;

    /**
     * The caldroid fragment.
     */
    private CustomCalanderFragment caldroidFragment;
    // private ArrayList<DateTimeAlongWithJobsCountBean>
    // dateTimeAlongWithJobsCountBeans;
    /**
     * The data access object.
     */
    private final Dao dataAccessObject;

    /**
     * The user.
     */
    private final User user;

    LinearLayout statusImageContainer;

    RelativeLayout layoutTitleBar;

    RelativeLayout layBottomBar;

    /**
     * Instantiates a new calendar fragment helper.
     *
     * @param syncroTeamBaseActivity the syncro team base activity
     * @param calendarFragment       the calendar fragment
     */
    public CalendarFragmentHelper(
            SyncroTeamBaseActivity syncroTeamBaseActivity,
            CalendarFragment calendarFragment,RelativeLayout layoutTitleBar,RelativeLayout layBottomBar) {
        // TODO Auto-generated constructor stub

            this.layoutTitleBar = layoutTitleBar;
            this.layBottomBar = layBottomBar;

        this.syncroTeamBaseActivity = syncroTeamBaseActivity;
        this.calendarFragment = calendarFragment;
        dataAccessObject = DaoManager.getInstance();
        user = dataAccessObject.getUser();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.synchroteam.fragmenthelper.HelperInterface#inflateLayout(android.
     * view.LayoutInflater, android.view.ViewGroup)
     */
    @Override
    public View inflateLayout(LayoutInflater inflater, ViewGroup container) {
        // TODO Auto-generated method stub
        View view = inflater
                .inflate(R.layout.layout_calendar, container, false);
        statusImageContainer = view.findViewById(R.id.statusImageContainer);
        initiateView(view);
        return view;

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.synchroteam.fragmenthelper.HelperInterface#initiateView(android.view
     * .View)
     */
    @Override
    public void initiateView(View v) {
        // TODO Auto-generated method stub
        setCalanderFragment();

    }

    /**
     * Sets the calander fragment.
     */
    private void setCalanderFragment() {
        // TODO Auto-generated method stub
        caldroidFragment = new CustomCalanderFragment();
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putBoolean(CaldroidFragment.ENABLE_SWIPE, true);
        args.putBoolean(CaldroidFragment.SIX_WEEKS_IN_CALENDAR, true);

        Logger.output(TAG, "locale : " + Locale.getDefault().getLanguage());
        //for france, the starting day should be mmonday
        if (Locale.getDefault().getLanguage().contentEquals("fr")) {
            args.putInt(CaldroidFragment.START_DAY_OF_WEEK,
                    CaldroidFragment.MONDAY);
        }

        // Uncomment this to customize startDayOfWeek
        // args.putInt(CaldroidFragment.START_DAY_OF_WEEK,
        // CaldroidFragment.TUESDAY); // Tuesday
        caldroidFragment.setArguments(args);

        calendarFragment
                .getChildFragmentManager()
                .beginTransaction()
                .add(R.id.calanderViewContainer, caldroidFragment,
                        caldroidFragment.getClass().getSimpleName())
                .addToBackStack(caldroidFragment.getClass().getSimpleName())
                .commit();


    }

    /**
     * Gets the jobs of current month.
     *
     * @return the jobs of current month
     */
    private void getJobsOfCurrentMonth() {
        // TODO Auto-generated method stub

        new CreateArrayListJobCount().execute();

    }

    /*
     * (non-Javadoc)
     *
     * @see com.synchroteam.fragmenthelper.HelperInterface#doOnSyncronize()
     */
    @Override
    public void doOnSyncronize() {
        // TODO Auto-generated method stub
        new CreateArrayListJobCount().execute();

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.synchroteam.fragmenthelper.HelperInterface#onReturnToActivity(int)
     */
    @Override
    public void onReturnToActivity(int requestCode) {
        // TODO Auto-generated method stub

    }

    /**
     * Convert to string list.
     *
     * @param dateTimes the date times
     * @return the array list
     */
    public static ArrayList<String> convertToStringList(ArrayList<DateTimeAlongWithJobsCountBean> dateTimes) {
        ArrayList<String> list = new ArrayList<>();
        String date;
//        for (DateTimeAlongWithJobsCountBean dateTime : dateTimes) {
//            date = dateTime.getDateTime().format("YYYY-MM-DD", Locale.US);
//            list.add(date);
//        }
        try {
            // Create iterator to loop through each book in list.
            for (Iterator<DateTimeAlongWithJobsCountBean> jobsCountBeanIterator = dateTimes.iterator();
                 jobsCountBeanIterator.hasNext(); ) {
                // Get next book.
                DateTimeAlongWithJobsCountBean dateTimeAlongWithJobsCountBean = jobsCountBeanIterator.next();
                String dateNew = dateTimeAlongWithJobsCountBean.getDateTime().format("YYYY-MM-DD", Locale.US);
                list.add(dateNew);
            }
        } catch (Exception e) {

        }
        return list;
    }

    /**
     * On destroy view.
     */
    public void onDestroyView() {
        calendarFragment.getChildFragmentManager().beginTransaction()
                .remove(caldroidFragment);

    }

    /**
     * The Class CreateArrayListJobCount.
     */
    @SuppressLint("StaticFieldLeak")
    private class CreateArrayListJobCount extends AsyncTaskCoroutine<Void, Void> {

        /**
         * The date time along with job count hash map.
         */
        private HashMap<DateTime, DateTimeAlongWithJobsCountBean> dateTimeAlongWithJobCountHashMap;

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        public void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            dateTimeAlongWithJobCountHashMap = new HashMap<DateTime, DateTimeAlongWithJobsCountBean>();
            DialogUtils.showProgressDialog(syncroTeamBaseActivity,
                    syncroTeamBaseActivity.getString(R.string.textWaitLable),
                    syncroTeamBaseActivity.getString(R.string.textFetchJob),
                    false);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        public Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            ArrayList<DateTimeAlongWithJobsCountBean> dateTimeAlongWithJobsCountBeans = caldroidFragment
                    .getCurrentDateInMonthArrayList();
            // ArrayList<String> stringDates =
            // convertToStringList(caldroidFragment.getCurrentDateInMonthArrayList());
            if (dateTimeAlongWithJobsCountBeans != null && dateTimeAlongWithJobsCountBeans.size() > 0) {

                ArrayList<String> stringDates = convertToStringList(dateTimeAlongWithJobsCountBeans);

                // caldroidFragment.(backgroundForDateTimeMap)

                ArrayList<String> mnthList = new ArrayList<String>();

                int indexCurrentDate = 0;
                if ((stringDates.size() % 2) == 0) {
                    indexCurrentDate = stringDates.size() / 2;
                } else {
                    indexCurrentDate = (stringDates.size() + 1) / 2;
                }

                String[] tempPrev = stringDates.get(0).split("-");
                String[] tempNext = stringDates.get((stringDates.size() - 1))
                        .split("-");
                String[] tempCurrent = stringDates.get(indexCurrentDate).split("-");
                mnthList.add(tempPrev[1]);
                mnthList.add(tempCurrent[1]);
                mnthList.add(tempNext[1]);

                // get the all types of jobs in a list
                ArrayList<CalendarAllJobBeans> calendarAllJobBeansList = new ArrayList<CalendarAllJobBeans>();
                calendarAllJobBeansList.clear();
                calendarAllJobBeansList.addAll(dataAccessObject
                        .getCountsOfVariousJobForMonth(mnthList));

                // calculate the current date
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                String currentDate = dateFormat.format(new Date());

                for (int i = 0; i < stringDates.size(); i++) {

                    JobsCount jobsCount = new JobsCount();

                    // set the started job count
                    jobsCount.setStartedJob(dataAccessObject.getStartedJobCount(
                            stringDates.get(i), user.getId(), currentDate));

                    // set the suspended job count
                    jobsCount
                            .setSuspendedJob(dataAccessObject.getSuspendedJobCount(
                                    stringDates.get(i), user.getId()));

                    // set the variables
                    int completedJobsCount = 0, scheduledJobsCounts = 0;

                    // calculate the complete job count for every date
                    for (int k = 0; k < calendarAllJobBeansList.size(); k++) {
                        if (stringDates.get(i).equals(
                                calendarAllJobBeansList.get(k).getDT_FIN_REAL())
                                && calendarAllJobBeansList.get(k)
                                .getCD_STATUT_INTERV() == KEYS.CurrentJobs.JOB__COMPLETE) {
                            completedJobsCount = completedJobsCount + 1;
                        }
                    }

                    // set the completed job count
                    jobsCount.setCompletedJob(completedJobsCount);

                    // calculate the schedule job count for every date
                    for (int k = 0; k < calendarAllJobBeansList.size(); k++) {

                        // scheduled
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                        Date startDate = null, endDate = null, compareDate = null;
                        try {
                            startDate = sdf.parse(calendarAllJobBeansList.get(k)
                                    .getDT_DEB_PREV());
                            endDate = sdf.parse(calendarAllJobBeansList.get(k)
                                    .getDT_FIN_PREV());
                            compareDate = sdf.parse(stringDates.get(i));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                            // TODO: handle exception
                            Logger.printException(e);
                        }

                        if (((startDate.compareTo(compareDate) == 0)
                                || (endDate.compareTo(compareDate) == 0) || ((compareDate
                                .compareTo(startDate) > 0) && (compareDate
                                .compareTo(endDate) < 0)))
                                && (calendarAllJobBeansList.get(k)
                                .getCD_STATUT_INTERV() <= 2)) {

                            scheduledJobsCounts = scheduledJobsCounts + 1;

                        }

                    }
                    // set the scheduled job count
                    jobsCount.setScheduledJob(scheduledJobsCounts);

                    dateTimeAlongWithJobCountHashMap.put(
                            dateTimeAlongWithJobsCountBeans.get(i).getDateTime(),
                            new DateTimeAlongWithJobsCountBean(
                                    dateTimeAlongWithJobsCountBeans.get(i)
                                            .getDateTime(), jobsCount
                                    .getStartedJob(), jobsCount
                                    .getSuspendedJob(), jobsCount
                                    .getScheduledJob(), jobsCount
                                    .getCompletedJob()));

                }

                /*
                 * for (int i = 0; i < stringDates.size(); i++) { JobsCount
                 * jobsCount = dataAccessObject.getCountsOfVariousJobs(
                 * stringDates.get(i), user.getId());
                 *
                 * dateTimeAlongWithJobCountHashMap.put(
                 * dateTimeAlongWithJobsCountBeans.get(i).getDateTime(), new
                 * DateTimeAlongWithJobsCountBean(
                 * dateTimeAlongWithJobsCountBeans.get(i) .getDateTime(), jobsCount
                 * .getStartedJob(), jobsCount .getSuspendedJob(), jobsCount
                 * .getScheduledJob(), jobsCount .getCompletedJob()));
                 *
                 * }
                 */

                caldroidFragment.setSelectedDatesWithJob(dateTimeAlongWithJobCountHashMap);

            }
            return null;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        public void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            DialogUtils.dismissProgressDialog();
            caldroidFragment.refreshView();
            EventBus.getDefault().post(new EnableSynchronizationAddJobEvent());

        }

    }

    /**
     * Do on resume.
     */
    public void doOnResume() {

        getJobsOfCurrentMonth();
        caldroidFragment.setCaldroidListener(listener);

    }


    /**
     * The listener.
     */
    final CaldroidListener listener = new CaldroidListener() {
        @Override
        public void onSelectDate(DateTimeAlongWithJobsCountBean date, View view) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                    "yyyy-MM-dd", Locale.US);

            String selectedDate = simpleDateFormat.format(CalendarHelper
                    .convertDateTimeToDate(date.getDateTime()));

            statusImageContainer.setVisibility(View.GONE);
            Fragment fragment = new JobListOnCurrentFragment(selectedDate,calendarFragment,layoutTitleBar,layBottomBar);

            calendarFragment
                    .getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.calanderViewContainer, fragment,
                            fragment.getClass().getSimpleName())
                    .addToBackStack(fragment.getClass().getSimpleName())
                    .commit();

//            String selectedDate = simpleDateFormat.format(CalendarHelper
//                    .convertDateTimeToDate(date.getDateTime()));
//
//            Intent intent = new Intent(syncroTeamBaseActivity,
//                    JobsListOnCurrentDate.class);
//            intent.putExtra(KEYS.Calander.DATE_SELECTED, selectedDate);
//            calendarFragment.startActivity(intent);
        }

        @Override
        public void onLongClickDate(Date date, View view) {
            super.onLongClickDate(date, view);
        }

        @Override
        public void onCaldroidViewCreated() {
            super.onCaldroidViewCreated();
        }

        /** The h. */
        private final Handler h = new Handler();

        @Override
        public void onChangeMonth(int month, int year) {
            super.onChangeMonth(month, year);

            h.postDelayed(new Runnable() {

                @Override
                public void run() {

                    getJobsOfCurrentMonth();
                    caldroidFragment.refreshView();

                }
            }, 300);
        }
    };

    public ArrayList<Integer> calculatePrevCurrentNextMonths() {
        ArrayList<Integer> mnthList = new ArrayList<Integer>();

        Calendar cal = Calendar.getInstance();
        int currentMonth = cal.get(Calendar.MONTH) + 1;

        cal.add(Calendar.MONTH, 1);
        int nextMonth = cal.get(Calendar.MONTH) + 1;

        Calendar cal1 = Calendar.getInstance();
        cal1.add(Calendar.MONTH, -1);
        int prevMonth = cal1.get(Calendar.MONTH);

        if (currentMonth == 1 && prevMonth == 11) {
            prevMonth = prevMonth + 1;
        }

        mnthList.add(prevMonth);
        mnthList.add(currentMonth);
        mnthList.add(nextMonth);

        return mnthList;
    }

}
