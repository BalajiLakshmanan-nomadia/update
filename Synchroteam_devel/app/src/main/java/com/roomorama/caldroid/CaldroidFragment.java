package com.roomorama.caldroid;

import hirondelle.date4j.DateTime;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.antonyt.infiniteviewpager.InfinitePagerAdapter;
import com.antonyt.infiniteviewpager.InfiniteViewPager;
import com.synchroteam.beans.DateTimeAlongWithJobsCountBean;
import com.synchroteam.synchroteam3.R;



// TODO: Auto-generated Javadoc
/**
 * Caldroid is a fragment that display calendar with dates in a month. Caldroid
 * can be used as embedded fragment, or as dialog fragment. <br/>
 * <br/>
 * Caldroid fragment includes 4 main parts:<br/>
 * <br/>
 * 1) Month title view: show the month and year (e.g MARCH, 2013) <br/>
 * <br/>
 * 2) Navigation arrows: to navigate to next month or previous month <br/>
 * <br/>
 * 3) Weekday gridview: contains only 1 row and 7 columns. To display
 * "SUN, MON, TUE, WED, THU, FRI, SAT" <br/>
 * <br/>
 * 4) An infinite view pager that allow user to swipe left/right to change
 * month. This library is taken from
 * https://github.com/antonyt/InfiniteViewPager <br/>
 * <br/>
 * This infinite view pager recycles 4 fragment, each fragment contains a grid
 * view with 7 columns to display the dates in month. Whenever user swipes
 * different screen, the date grid views are updated. <br/>
 * <br/>
 * Caldroid fragment supports setting min/max date, selecting dates in a range,
 * setting disabled dates, highlighting today. It includes convenient methods to
 * work with date and string, enable or disable the navigation arrows. User can
 * also swipe left/right to change months.<br/>
 * <br/>
 * Caldroid code is simple and clean partly because of powerful JODA DateTime
 * library!
 * 
 * @author thomasdao
 * 
 */

@SuppressLint("DefaultLocale")
public class CaldroidFragment extends DialogFragment {

	/** The tag. */
	public String TAG = "CaldroidFragment";

	/** Weekday conventions. */
	public static int SUNDAY = 1;

	/** The monday. */
	public static int MONDAY = 2;

	/** The tuesday. */
	public static int TUESDAY = 3;

	/** The wednesday. */
	public static int WEDNESDAY = 4;

	/** The thursday. */
	public static int THURSDAY = 5;

	/** The friday. */
	public static int FRIDAY = 6;

	/** The saturday. */
	public static int SATURDAY = 7;

	/** Flags to display month. */
	private static final int MONTH_YEAR_FLAG = DateUtils.FORMAT_SHOW_DATE
			| DateUtils.FORMAT_NO_MONTH_DAY | DateUtils.FORMAT_SHOW_YEAR;

	/** First day of month time. */
	private Time firstMonthTime = new Time();

	/** Reuse formatter to print "MMMM yyyy" format. */
	private final StringBuilder monthYearStringBuilder = new StringBuilder(50);

	/** The month year formatter. */
	private Formatter monthYearFormatter = new Formatter(
			monthYearStringBuilder, Locale.getDefault());

	/** To customize the selected background drawable and text color. */
	public static int selectedBackgroundDrawable = -1;

	/** The selected text color. */
	public static int selectedTextColor = Color.BLACK;

	/** The Constant NUMBER_OF_PAGES. */
	public final static int NUMBER_OF_PAGES = 4;

	/** To customize the disabled background drawable and text color. */
	public static int disabledBackgroundDrawable = -1;

	/** The disabled text color. */
	public static int disabledTextColor = Color.GRAY;

	/** The selected dates with jobs. */
	private HashMap<DateTime, DateTimeAlongWithJobsCountBean> selectedDatesWithJobs;

	/** Caldroid view components. */
	private ImageButton leftArrowButton;

	/** The right arrow button. */
	private ImageButton rightArrowButton;

	/** The month title text view. */
	private TextView monthTitleTextView;

	/** The weekday grid view. */
	private GridView weekdayGridView;

	/** The date view pager. */
	private InfiniteViewPager dateViewPager;

	/** The page change listener. */
	private DatePageChangeListener pageChangeListener;

	/** The fragments. */
	private ArrayList<DateGridFragment> fragments;

    /** The current calaroid grid adapter. */
    protected CaldroidGridAdapter currentCalaroidGridAdapter;

    /** The container weeks. */
    private RelativeLayout containerWeeks;

	/** Initial params key. */
	public final static String DIALOG_TITLE = "dialogTitle";

	/** The Constant MONTH. */
	public final static String MONTH = "month";

	/** The Constant YEAR. */
	public final static String YEAR = "year";

	/** The Constant SHOW_NAVIGATION_ARROWS. */
	public final static String SHOW_NAVIGATION_ARROWS = "showNavigationArrows";

	/** The Constant DISABLE_DATES. */
	public final static String DISABLE_DATES = "disableDates";

	/** The Constant SELECTED_DATES_WITH_JOB. */
	public final static String SELECTED_DATES_WITH_JOB = "selectedDateWithJob";

	/** The Constant SELECTED_DATES. */
	public final static String SELECTED_DATES = "selectedDates";

	/** The Constant MIN_DATE. */
	public final static String MIN_DATE = "minDate";

	/** The Constant MAX_DATE. */
	public final static String MAX_DATE = "maxDate";

	/** The Constant ENABLE_SWIPE. */
	public final static String ENABLE_SWIPE = "enableSwipe";

	/** The Constant START_DAY_OF_WEEK. */
	public final static String START_DAY_OF_WEEK = "startDayOfWeek";

	/** The Constant SIX_WEEKS_IN_CALENDAR. */
	public final static String SIX_WEEKS_IN_CALENDAR = "sixWeeksInCalendar";

	/** The Constant ENABLE_CLICK_ON_DISABLED_DATES. */
	public final static String ENABLE_CLICK_ON_DISABLED_DATES = "enableClickOnDisabledDates";

	/** For internal use. */
	public final static String _MIN_DATE_TIME = "_minDateTime";

	/** The Constant _MAX_DATE_TIME. */
	public final static String _MAX_DATE_TIME = "_maxDateTime";

	/** The Constant _BACKGROUND_FOR_DATETIME_MAP. */
	public final static String _BACKGROUND_FOR_DATETIME_MAP = "_backgroundForDateTimeMap";

	/** The Constant _TEXT_COLOR_FOR_DATETIME_MAP. */
	public final static String _TEXT_COLOR_FOR_DATETIME_MAP = "_textColorForDateTimeMap";

	/** Initial data. */
	protected String dialogTitle;

	/** The month. */
	protected int month = -1;

	/** The year. */
	protected int year = -1;

	/** The disable dates. */
	protected ArrayList<DateTime> disableDates = new ArrayList<DateTime>();

	/** The selected dates. */
	protected ArrayList<DateTime> selectedDates = new ArrayList<DateTime>();

	/** The min date time. */
	protected DateTime minDateTime;

	/** The max date time. */
	protected DateTime maxDateTime;

	/** The date in months list. */
	protected ArrayList<DateTimeAlongWithJobsCountBean> dateInMonthsList;

	/** caldroidData belongs to Caldroid. */
	protected HashMap<String, Object> caldroidData = new HashMap<String, Object>();

	/** extraData belongs to client. */
	protected HashMap<String, Object> extraData = new HashMap<String, Object>();

	/** backgroundForDateMap holds background resource for each date. */
	protected HashMap<DateTime, Integer> backgroundForDateTimeMap = new HashMap<DateTime, Integer>();

	/** textColorForDateMap holds color for text for each date. */
	protected HashMap<DateTime, Integer> textColorForDateTimeMap = new HashMap<DateTime, Integer>();;

	/** First column of calendar is Sunday. */
	protected int startDayOfWeek = SUNDAY;

	/**
	 * A calendar height is not fixed, it may have 5 or 6 rows. Set fitAllMonths
	 * to true so that the calendar will always have 6 rows
	 */
	private boolean sixWeeksInCalendar = true;

	/** datePagerAdapters hold 4 adapters, meant to be reused. */
	protected ArrayList<CaldroidGridAdapter> datePagerAdapters = new ArrayList<CaldroidGridAdapter>();

	/** To control the navigation. */
	protected boolean enableSwipe = true;

	/** The show navigation arrows. */
	protected boolean showNavigationArrows = true;

	/** The enable click on disabled dates. */
	protected boolean enableClickOnDisabledDates = false;

	/** dateItemClickListener is fired when user click on the date cell. */
	private OnItemClickListener dateItemClickListener;

	/** dateItemLongClickListener is fired when user does a longclick on the date cell. */
	private OnItemLongClickListener dateItemLongClickListener;

	/** caldroidListener inform library client of the event happens inside Caldroid. */
	private CaldroidListener caldroidListener;

	/**
	 * Gets the caldroid listener.
	 *
	 * @return the caldroid listener
	 */
	public CaldroidListener getCaldroidListener() {
		return caldroidListener;
	}

	/**
	 * Meant to be subclassed. User who wants to provide custom view, need to
	 * provide custom adapter here
	 *
	 * @param month the month
	 * @param year the year
	 * @return the new dates grid adapter
	 */
	public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {
		return new CaldroidGridAdapter(getActivity(), month, year,
				getCaldroidData(), extraData);

	}

	/**
	 * Sets the selected dates with job.
	 *
	 * @param selectedDatesWithJobs the selected dates with jobs
	 */
	public void setSelectedDatesWithJob(HashMap<DateTime, DateTimeAlongWithJobsCountBean> selectedDatesWithJobs){
		this.selectedDatesWithJobs=selectedDatesWithJobs;
	}

	/**
	 * For client to customize the weekDayGridView.
	 *
	 * @return the weekday grid view
	 */
	public GridView getWeekdayGridView() {
		return weekdayGridView;
	}

	/**
	 * For client to access array of rotating fragments.
	 *
	 * @return the fragments
	 */
	public ArrayList<DateGridFragment> getFragments() {
		return fragments;
	}

	/**
	 * To let user customize the navigation buttons.
	 *
	 * @return the left arrow button
	 */
	public ImageButton getLeftArrowButton() {
		return leftArrowButton;
	}

	/**
	 * Gets the right arrow button.
	 *
	 * @return the right arrow button
	 */
	public ImageButton getRightArrowButton() {
		return rightArrowButton;
	}

	/**
	 * To let client customize month title textview.
	 *
	 * @return the month title text view
	 */
	public TextView getMonthTitleTextView() {
		return monthTitleTextView;
	}

	/**
	 * Sets the month title text view.
	 *
	 * @param monthTitleTextView the new month title text view
	 */
	public void setMonthTitleTextView(TextView monthTitleTextView) {
		this.monthTitleTextView = monthTitleTextView;
	}

	/**
	 * Get 4 adapters of the date grid views. Useful to set custom data and
	 * refresh date grid view
	 *
	 * @return the date pager adapters
	 */
	public ArrayList<CaldroidGridAdapter> getDatePagerAdapters() {
		return datePagerAdapters;
	}

	/**
	 * caldroidData return data belong to Caldroid.
	 *
	 * @return the caldroid data
	 */
	public HashMap<String, Object> getCaldroidData() {
		caldroidData.clear();
		caldroidData.put(DISABLE_DATES, disableDates);
		caldroidData.put(SELECTED_DATES, selectedDates);
		caldroidData.put(_MIN_DATE_TIME, minDateTime);
		caldroidData.put(_MAX_DATE_TIME, maxDateTime);
		caldroidData.put(START_DAY_OF_WEEK, Integer.valueOf(startDayOfWeek));
		caldroidData.put(SIX_WEEKS_IN_CALENDAR,
				Boolean.valueOf(sixWeeksInCalendar));
		caldroidData.put(SELECTED_DATES_WITH_JOB, this.selectedDatesWithJobs);

		// For internal use
		caldroidData
				.put(_BACKGROUND_FOR_DATETIME_MAP, backgroundForDateTimeMap);
		caldroidData.put(_TEXT_COLOR_FOR_DATETIME_MAP, textColorForDateTimeMap);

		return caldroidData;
	}

	/**
	 * Extra data is data belong to Client.
	 *
	 * @return the extra data
	 */
	public HashMap<String, Object> getExtraData() {
		return extraData;
	}

	/**
	 * Client can set custom data in this HashMap.
	 *
	 * @param extraData the extra data
	 */
	public void setExtraData(HashMap<String, Object> extraData) {
		this.extraData = extraData;
	}

	/**
	 * Set backgroundForDateMap.
	 *
	 * @param backgroundForDateMap the background for date map
	 */
	public void setBackgroundResourceForDates(
			HashMap<Date, Integer> backgroundForDateMap) {
		// Clear first
		backgroundForDateTimeMap.clear();

		if (backgroundForDateMap == null || backgroundForDateMap.size() == 0) {
			return;
		}

		for (Date date : backgroundForDateMap.keySet()) {
			Integer resource = backgroundForDateMap.get(date);
			DateTime dateTime = CalendarHelper.convertDateToDateTime(date).getDateTime();
			backgroundForDateTimeMap.put(dateTime, resource);
		}
	}

	/**
	 * Sets the background resource for date times.
	 *
	 * @param backgroundForDateTimeMap the background for date time map
	 */
	public void setBackgroundResourceForDateTimes(
			HashMap<DateTime, Integer> backgroundForDateTimeMap) {
		this.backgroundForDateTimeMap.putAll(backgroundForDateTimeMap);
	}

	/**
	 * Sets the background resource for date.
	 *
	 * @param backgroundRes the background res
	 * @param date the date
	 */
	public void setBackgroundResourceForDate(int backgroundRes, Date date) {
		DateTime dateTime = CalendarHelper.convertDateToDateTime(date).getDateTime();
		backgroundForDateTimeMap.put(dateTime, Integer.valueOf(backgroundRes));
	}

	/**
	 * Sets the background resource for date time.
	 *
	 * @param backgroundRes the background res
	 * @param dateTime the date time
	 */
	public void setBackgroundResourceForDateTime(int backgroundRes,
			DateTime dateTime) {
		backgroundForDateTimeMap.put(dateTime, Integer.valueOf(backgroundRes));
	}

	/**
	 * Set textColorForDateMap.
	 *
	 * @param textColorForDateMap the text color for date map
	 */
	public void setTextColorForDates(HashMap<Date, Integer> textColorForDateMap) {
		// Clear first
		textColorForDateTimeMap.clear();

		if (textColorForDateMap == null || textColorForDateMap.size() == 0) {
			return;
		}

		for (Date date : textColorForDateMap.keySet()) {
			Integer resource = textColorForDateMap.get(date);
			DateTime dateTime = CalendarHelper.convertDateToDateTime(date).getDateTime();
			textColorForDateTimeMap.put(dateTime, resource);
		}
	}

	/**
	 * Sets the text color for date times.
	 *
	 * @param textColorForDateTimeMap the text color for date time map
	 */
	public void setTextColorForDateTimes(
			HashMap<DateTime, Integer> textColorForDateTimeMap) {
		this.textColorForDateTimeMap.putAll(textColorForDateTimeMap);
	}

	/**
	 * Sets the text color for date.
	 *
	 * @param textColorRes the text color res
	 * @param date the date
	 */
	public void setTextColorForDate(int textColorRes, Date date) {
		DateTime dateTime = CalendarHelper.convertDateToDateTime(date).getDateTime();
		textColorForDateTimeMap.put(dateTime, Integer.valueOf(textColorRes));
	}

	/**
	 * Sets the text color for date time.
	 *
	 * @param textColorRes the text color res
	 * @param dateTime the date time
	 */
	public void setTextColorForDateTime(int textColorRes, DateTime dateTime) {
		textColorForDateTimeMap.put(dateTime, Integer.valueOf(textColorRes));
	}

	/**
	 * Get current saved sates of the Caldroid. Useful for handling rotation
	 *
	 * @return the saved states
	 */
	public Bundle getSavedStates() {
		Bundle bundle = new Bundle();
		bundle.putInt(MONTH, month);
		bundle.putInt(YEAR, year);

		if (dialogTitle != null) {
			bundle.putString(DIALOG_TITLE, dialogTitle);
		}

		if (selectedDates != null && selectedDates.size() > 0) {
			bundle.putStringArrayList(SELECTED_DATES,
					CalendarHelper.convertToStringList(selectedDates));
		}

		if (disableDates != null && disableDates.size() > 0) {
			bundle.putStringArrayList(DISABLE_DATES,
					CalendarHelper.convertToStringList(disableDates));
		}

		if (minDateTime != null) {
			bundle.putString(MIN_DATE, minDateTime.format("YYYY-MM-DD"));
		}

		if (maxDateTime != null) {
			bundle.putString(MAX_DATE, maxDateTime.format("YYYY-MM-DD"));
		}

		bundle.putBoolean(SHOW_NAVIGATION_ARROWS, showNavigationArrows);
		bundle.putBoolean(ENABLE_SWIPE, enableSwipe);
		bundle.putInt(START_DAY_OF_WEEK, startDayOfWeek);
		bundle.putBoolean(SIX_WEEKS_IN_CALENDAR, sixWeeksInCalendar);

		return bundle;
	}

	/**
	 * Save current state to bundle outState.
	 *
	 * @param outState the out state
	 * @param key the key
	 */
	public void saveStatesToKey(Bundle outState, String key) {
		outState.putBundle(key, getSavedStates());
	}

	/**
	 * Restore current states from savedInstanceState.
	 *
	 * @param savedInstanceState the saved instance state
	 * @param key the key
	 */
	public void restoreStatesFromKey(Bundle savedInstanceState, String key) {
		if (savedInstanceState != null && savedInstanceState.containsKey(key)) {
			Bundle caldroidSavedState = savedInstanceState.getBundle(key);
			setArguments(caldroidSavedState);
		}
	}

	/**
	 * Restore state for dialog.
	 *
	 * @param manager the manager
	 * @param savedInstanceState the saved instance state
	 * @param key the key
	 * @param dialogTag the dialog tag
	 */
	public void restoreDialogStatesFromKey(FragmentManager manager,
			Bundle savedInstanceState, String key, String dialogTag) {
		restoreStatesFromKey(savedInstanceState, key);

		CaldroidFragment existingDialog = (CaldroidFragment) manager
				.findFragmentByTag(dialogTag);
		if (existingDialog != null) {
			existingDialog.dismiss();
			show(manager, dialogTag);
		}
	}

	/**
	 * Get current virtual position of the month being viewed.
	 *
	 * @return the current virtual position
	 */
	public int getCurrentVirtualPosition() {
		int currentPage = dateViewPager.getCurrentItem();
		return pageChangeListener.getCurrent(currentPage);
	}

	/**
	 * Move calendar to the specified date.
	 *
	 * @param date the date
	 */
	public void moveToDate(Date date) {
		moveToDateTime(CalendarHelper.convertDateToDateTime(date).getDateTime());
	}

	/**
	 * Move calendar to specified dateTime, with animation.
	 *
	 * @param dateTime the date time
	 */
	public void moveToDateTime(DateTime dateTime) {

		DateTime firstOfMonth = new DateTime(year, month, 1, 0, 0, 0, 0);
		DateTime lastOfMonth = firstOfMonth.getEndOfMonth();

		// To create a swipe effect
		// Do nothing if the dateTime is in current month

		// Calendar swipe left when dateTime is in the past
		if (dateTime.lt(firstOfMonth)) {
			// Get next month of dateTime. When swipe left, month will
			// decrease
			DateTime firstDayNextMonth = dateTime.plus(0, 1, 0, 0, 0, 0, 0,
					DateTime.DayOverflow.LastDay);

			// Refresh adapters
			pageChangeListener.setCurrentDateTime(firstDayNextMonth);
			int currentItem = dateViewPager.getCurrentItem();
			pageChangeListener.refreshAdapters(currentItem);

			// Swipe left
			dateViewPager.setCurrentItem(currentItem - 1);
		}

		// Calendar swipe right when dateTime is in the future
		else if (dateTime.gt(lastOfMonth)) {
			// Get last month of dateTime. When swipe right, the month will
			// increase
			DateTime firstDayLastMonth = dateTime.minus(0, 1, 0, 0, 0, 0, 0,
					DateTime.DayOverflow.LastDay);

			// Refresh adapters
			pageChangeListener.setCurrentDateTime(firstDayLastMonth);
			int currentItem = dateViewPager.getCurrentItem();
			pageChangeListener.refreshAdapters(currentItem);

			// Swipe right
			dateViewPager.setCurrentItem(currentItem + 1);
		}

	}

	/**
	 * Set month and year for the calendar. This is to avoid naive
	 * implementation of manipulating month and year. All dates within same
	 * month/year give same result
	 *
	 * @param date the new calendar date
	 */
	public void setCalendarDate(Date date) {
		setCalendarDateTime(CalendarHelper.convertDateToDateTime(date).getDateTime());
	}

	/**
	 * Sets the calendar date time.
	 *
	 * @param dateTime the new calendar date time
	 */
	public void setCalendarDateTime(DateTime dateTime) {
		month = dateTime.getMonth();
		year = dateTime.getYear();

		// Notify listener
		if (caldroidListener != null) {
			caldroidListener.onChangeMonth(month, year);
		}

		refreshView();
	}

	/**
	 * Set calendar to previous month.
	 */
	public void prevMonth() {
		dateViewPager.setCurrentItem(pageChangeListener.getCurrentPage() - 1);
	}

	/**
	 * Set calendar to next month.
	 */
	public void nextMonth() {
		dateViewPager.setCurrentItem(pageChangeListener.getCurrentPage() + 1);
	}

	/**
	 * Clear all disable dates. Notice this does not refresh the calendar, need
	 * to explicitly call refreshView()
	 */
	public void clearDisableDates() {
		disableDates.clear();
	}

	/**
	 * Set disableDates from ArrayList of Date.
	 *
	 * @param disableDateList the new disable dates
	 */
	public void setDisableDates(ArrayList<Date> disableDateList) {
		disableDates.clear();
		if (disableDateList == null || disableDateList.size() == 0) {
			return;
		}

		for (Date date : disableDateList) {
			DateTime dateTime = CalendarHelper.convertDateToDateTime(date).getDateTime();
			disableDates.add(dateTime);
		}

	}

	/**
	 * Set disableDates from ArrayList of String. By default, the date formatter
	 * is yyyy-MM-dd. For e.g 2013-12-24
	 *
	 * @param disableDateStrings the new disable dates from string
	 */
	public void setDisableDatesFromString(ArrayList<String> disableDateStrings) {
		setDisableDatesFromString(disableDateStrings, null);
	}

	/**
	 * Set disableDates from ArrayList of String with custom date format. For
	 * example, if the date string is 06-Jan-2013, use date format dd-MMM-yyyy.
	 * This method will refresh the calendar, it's not necessary to call
	 * refreshView()
	 *
	 * @param disableDateStrings the disable date strings
	 * @param dateFormat the date format
	 */
	public void setDisableDatesFromString(ArrayList<String> disableDateStrings,
			String dateFormat) {
		disableDates.clear();
		if (disableDateStrings == null) {
			return;
		}

		for (String dateString : disableDateStrings) {
			DateTime dateTime = CalendarHelper.getDateTimeFromString(
					dateString, dateFormat).getDateTime();
			disableDates.add(dateTime);
		}
	}

	/**
	 * To clear selectedDates. This method does not refresh view, need to
	 * explicitly call refreshView()
	 */
	public void clearSelectedDates() {
		selectedDates.clear();
	}

	/**
	 * Select the dates from fromDate to toDate. By default the background color
	 * is holo_blue_light, and the text color is black. You can customize the
	 * background by changing CaldroidFragment.selectedBackgroundDrawable, and
	 * change the text color CaldroidFragment.selectedTextColor before call this
	 * method. This method does not refresh view, need to call refreshView()
	 *
	 * @param fromDate the from date
	 * @param toDate the to date
	 */
	public void setSelectedDates(Date fromDate, Date toDate) {
		// Ensure fromDate is before toDate
		if (fromDate == null || toDate == null || fromDate.after(toDate)) {
			return;
		}

		selectedDates.clear();

		DateTime fromDateTime = CalendarHelper.convertDateToDateTime(fromDate).getDateTime();
		DateTime toDateTime = CalendarHelper.convertDateToDateTime(toDate).getDateTime();

		DateTime dateTime = fromDateTime;
		while (dateTime.lt(toDateTime)) {
			selectedDates.add(dateTime);
			dateTime = dateTime.plusDays(1);
		}
		selectedDates.add(toDateTime);
	}

	/**
	 * Convenient method to select dates from String.
	 *
	 * @param fromDateString the from date string
	 * @param toDateString the to date string
	 * @param dateFormat the date format
	 * @throws ParseException the parse exception
	 */
	public void setSelectedDateStrings(String fromDateString,
			String toDateString, String dateFormat) throws ParseException {

		Date fromDate = CalendarHelper.getDateFromString(fromDateString,
				dateFormat);
		Date toDate = CalendarHelper
				.getDateFromString(toDateString, dateFormat);
		setSelectedDates(fromDate, toDate);
	}

	/**
	 * Check if the navigation arrow is shown.
	 *
	 * @return true, if is show navigation arrows
	 */
	public boolean isShowNavigationArrows() {
		return showNavigationArrows;
	}

	/**
	 * Show or hide the navigation arrows.
	 *
	 * @param showNavigationArrows the new show navigation arrows
	 */
	public void setShowNavigationArrows(boolean showNavigationArrows) {
		this.showNavigationArrows = showNavigationArrows;
		if (showNavigationArrows) {
			leftArrowButton.setVisibility(View.VISIBLE);
			rightArrowButton.setVisibility(View.VISIBLE);
		} else {
			leftArrowButton.setVisibility(View.INVISIBLE);
			rightArrowButton.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * Enable / Disable swipe to navigate different months.
	 *
	 * @return true, if is enable swipe
	 */
	public boolean isEnableSwipe() {
		return enableSwipe;
	}

	/**
	 * Sets the enable swipe.
	 *
	 * @param enableSwipe the new enable swipe
	 */
	public void setEnableSwipe(boolean enableSwipe) {
		this.enableSwipe = enableSwipe;
		dateViewPager.setEnabled(enableSwipe);
	}

	/**
	 * Set min date. This method does not refresh view
	 *
	 * @param minDate the new min date
	 */
	public void setMinDate(Date minDate) {
		if (minDate == null) {
			minDateTime = null;
		} else {
			minDateTime = CalendarHelper.convertDateToDateTime(minDate).getDateTime();
		}
	}

	/**
	 * Checks if is six weeks in calendar.
	 *
	 * @return true, if is six weeks in calendar
	 */
	public boolean isSixWeeksInCalendar() {
		return sixWeeksInCalendar;
	}

	/**
	 * Sets the six weeks in calendar.
	 *
	 * @param sixWeeksInCalendar the new six weeks in calendar
	 */
	public void setSixWeeksInCalendar(boolean sixWeeksInCalendar) {
		this.sixWeeksInCalendar = sixWeeksInCalendar;
		dateViewPager.setSixWeeksInCalendar(sixWeeksInCalendar);
	}

	/**
	 * Convenient method to set min date from String. If dateFormat is null,
	 * default format is yyyy-MM-dd
	 *
	 * @param minDateString the min date string
	 * @param dateFormat the date format
	 */
	public void setMinDateFromString(String minDateString, String dateFormat) {
		if (minDateString == null) {
			setMinDate(null);
		} else {
			minDateTime = CalendarHelper.getDateTimeFromString(minDateString,
					dateFormat).getDateTime();
		}
	}

	/**
	 * Set max date. This method does not refresh view
	 *
	 * @param maxDate the new max date
	 */
	public void setMaxDate(Date maxDate) {
		if (maxDate == null) {
			maxDateTime = null;
		} else {
			maxDateTime = CalendarHelper.convertDateToDateTime(maxDate).getDateTime();
		}
	}

	/**
	 * Convenient method to set max date from String. If dateFormat is null,
	 * default format is yyyy-MM-dd
	 *
	 * @param maxDateString the max date string
	 * @param dateFormat the date format
	 */
	public void setMaxDateFromString(String maxDateString, String dateFormat) {
		if (maxDateString == null) {
			setMaxDate(null);
		} else {
			maxDateTime = CalendarHelper.getDateTimeFromString(maxDateString,
					dateFormat).getDateTime();
		}
	}

	/**
	 * Set caldroid listener when user click on a date.
	 *
	 * @param caldroidListener the new caldroid listener
	 */
	public void setCaldroidListener(CaldroidListener caldroidListener) {
		this.caldroidListener = caldroidListener;
	}

	/**
	 * Gets the current date in month array list.
	 *
	 * @return the current date in month array list
	 */
	public ArrayList<DateTimeAlongWithJobsCountBean> getCurrentDateInMonthArrayList(){
		return dateInMonthsList;
	}

	/**
	 * Callback to listener when date is valid (not disable, not outside of
	 * min/max date).
	 *
	 * @return the date item click listener
	 */
	private OnItemClickListener getDateItemClickListener() {
		if (dateItemClickListener == null) {
			dateItemClickListener = new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					DateTimeAlongWithJobsCountBean dateTime = dateInMonthsList.get(position);

					if (caldroidListener != null) {
						if (!enableClickOnDisabledDates) {
							if ((minDateTime != null && dateTime.getDateTime().
									lt(minDateTime))
									|| (maxDateTime != null && dateTime.getDateTime()
											.gt(maxDateTime))
									|| (disableDates != null && disableDates
											.indexOf(dateTime) != -1)) {
								return;
							}
						}


						caldroidListener.onSelectDate(dateTime, view);
					}
				}
			};
		}

		return dateItemClickListener;
	}

	/**
	 * Callback to listener when date is valid (not disable, not outside of
	 * min/max date).
	 *
	 * @return the date item long click listener
	 */
	private OnItemLongClickListener getDateItemLongClickListener() {
		if (dateItemLongClickListener == null) {
			dateItemLongClickListener = new OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent,
						View view, int position, long id) {

					DateTimeAlongWithJobsCountBean dateTime = dateInMonthsList.get(position);

					if (caldroidListener != null) {
						if (!enableClickOnDisabledDates) {
							if ((minDateTime != null && dateTime.getDateTime()
									.lt(minDateTime))
									|| (maxDateTime != null && dateTime.getDateTime()
											.gt(maxDateTime))
									|| (disableDates != null && disableDates
											.indexOf(dateTime) != -1)) {
								return false;
							}
						}
						Date date = CalendarHelper
								.convertDateTimeToDate(dateTime);
						caldroidListener.onLongClickDate(date, view);
					}

					return true;
				}
			};
		}

		return dateItemLongClickListener;
	}

	/**
	 * Refresh month title text view when user swipe.
	 */
	protected void refreshMonthTitleTextView() {
		// Refresh title view
		firstMonthTime.year = year;
		firstMonthTime.month = month - 1;
		firstMonthTime.monthDay = 1;
		long millis = firstMonthTime.toMillis(true);

		// This is the method used by the platform Calendar app to get a
		// correctly localized month name for display on a wall calendar
		monthYearStringBuilder.setLength(0);
		String monthTitle = DateUtils.formatDateRange(getActivity(),
				monthYearFormatter, millis, millis, MONTH_YEAR_FLAG).toString();

		monthTitleTextView.setText(monthTitle);
	}

	/**
	 * Refresh view when parameter changes. You should always change all
	 * parameters first, then call this method.
	 */
	public void refreshView() {
		// If month and year is not yet initialized, refreshView doesn't do
		// anything
		if (month == -1 || year == -1) {
			return;
		}

		refreshMonthTitleTextView();

		// Refresh the date grid views
		for (CaldroidGridAdapter adapter : datePagerAdapters) {
			// Reset caldroid data
			adapter.setCaldroidData(getCaldroidData());

			// Reset extra data
			adapter.setExtraData(extraData);

			// Refresh view
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * Retrieve initial arguments to the fragment Data can include: month, year,
	 * dialogTitle, showNavigationArrows,(String) disableDates, selectedDates,
	 * minDate, maxDate.
	 */
	protected void retrieveInitialArgs() {
		// Get arguments
		Bundle args = getArguments();
		if (args != null) {
			// Get month, year
			month = args.getInt(MONTH, -1);
			year = args.getInt(YEAR, -1);
			dialogTitle = args.getString(DIALOG_TITLE);
			Dialog dialog = getDialog();
			if (dialog != null) {
				if (dialogTitle != null) {
					dialog.setTitle(dialogTitle);
				} else {
					// Don't display title bar if user did not supply
					// dialogTitle
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				}
			}

			// Get start day of Week. Default calendar first column is SUNDAY
			startDayOfWeek = args.getInt(START_DAY_OF_WEEK, 1);
			if (startDayOfWeek > 7) {
				startDayOfWeek = startDayOfWeek % 7;
			}

			// Should show arrow
			showNavigationArrows = args
					.getBoolean(SHOW_NAVIGATION_ARROWS, true);

			// Should enable swipe to change month
			enableSwipe = args.getBoolean(ENABLE_SWIPE, true);

			// Get sixWeeksInCalendar
			sixWeeksInCalendar = args.getBoolean(SIX_WEEKS_IN_CALENDAR, true);

			// Get clickable setting
			enableClickOnDisabledDates = args.getBoolean(
					ENABLE_CLICK_ON_DISABLED_DATES, false);

			// Get disable dates
			ArrayList<String> disableDateStrings = args
					.getStringArrayList(DISABLE_DATES);
			if (disableDateStrings != null && disableDateStrings.size() > 0) {
				disableDates.clear();
				for (String dateString : disableDateStrings) {
					DateTimeAlongWithJobsCountBean dt = CalendarHelper.getDateTimeFromString(
							dateString, "yyyy-MM-dd");
					disableDates.add(dt.getDateTime());
				}
			}

			// Get selected dates
			ArrayList<String> selectedDateStrings = args
					.getStringArrayList(SELECTED_DATES);
			if (selectedDateStrings != null && selectedDateStrings.size() > 0) {
				selectedDates.clear();
				for (String dateString : selectedDateStrings) {
					DateTimeAlongWithJobsCountBean dt = CalendarHelper.getDateTimeFromString(
							dateString, "yyyy-MM-dd");
					selectedDates.add(dt.getDateTime());
				}
			}

			// Get min date and max date
			String minDateTimeString = args.getString(MIN_DATE);
			if (minDateTimeString != null) {
				minDateTime = CalendarHelper.getDateTimeFromString(
						minDateTimeString, null).getDateTime();
			}

			String maxDateTimeString = args.getString(MAX_DATE);
			if (maxDateTimeString != null) {
				maxDateTime = CalendarHelper.getDateTimeFromString(
						maxDateTimeString, null).getDateTime();
			}

		}
		if (month == -1 || year == -1) {
			DateTime dateTime = DateTime.today(TimeZone.getDefault());
			month = dateTime.getMonth();
			year = dateTime.getYear();
		}
	}

	/**
	 * To support faster init.
	 *
	 * @param dialogTitle the dialog title
	 * @param month the month
	 * @param year the year
	 * @return the caldroid fragment
	 */
	public static CaldroidFragment newInstance(String dialogTitle, int month,
			int year) {
		CaldroidFragment f = new CaldroidFragment();

		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putString(DIALOG_TITLE, dialogTitle);
		args.putInt(MONTH, month);
		args.putInt(YEAR, year);

		f.setArguments(args);

		return f;
	}

	/**
	 * Below code fixed the issue viewpager disappears in dialog mode on
	 * orientation change
	 *
	 * Code taken from Andy Dennie and Zsombor Erdody-Nagy
	 * http://stackoverflow.com/questions/8235080/fragments-dialogfragment
	 * -and-screen-rotation
	 */
	@Override
	public void onDestroyView() {
		if (getDialog() != null && getRetainInstance()) {
			getDialog().setDismissMessage(null);
		}
		super.onDestroyView();
	}

	/**
	 * Setup view.
	 *
	 * @param inflater the inflater
	 * @param container the container
	 * @param savedInstanceState the saved instance state
	 * @return the view
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		retrieveInitialArgs();

		// To support keeping instance for dialog
		if (getDialog() != null) {
			setRetainInstance(true);
		}

		// Inflate layout
		View view = inflater.inflate(R.layout.calendar_view, container, false);

		// For the monthTitleTextView
		monthTitleTextView = (TextView) view
				.findViewById(R.id.calendar_month_year_textview);

		// For the left arrow button
		leftArrowButton = (ImageButton) view.findViewById(R.id.calendar_left_arrow);
		rightArrowButton = (ImageButton) view
				.findViewById(R.id.calendar_right_arrow);

		containerWeeks=(RelativeLayout) view.findViewById(R.id.calendar_title_view);

		// Navigate to previous month when user click
		leftArrowButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prevMonth();
			}
		});

		// Navigate to next month when user click
		rightArrowButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				nextMonth();
			}
		});

		// Show navigation arrows depend on initial arguments
		setShowNavigationArrows(showNavigationArrows);

		// For the weekday gridview ("SUN, MON, TUE, WED, THU, FRI, SAT")
		weekdayGridView = (GridView) view.findViewById(R.id.weekday_gridview);
		WeekdayArrayAdapter weekdaysAdapter = new WeekdayArrayAdapter(
				getActivity(),
				getDaysOfWeek());
		weekdayGridView.setAdapter(weekdaysAdapter);

		// Setup all the pages of date grid views. These pages are recycled
		setupDateGridPages(view);

		// Refresh view
		refreshView();

		// Inform client that all views are created and not null
		// Client should perform customization for buttons and textviews here
		if (caldroidListener != null) {
			caldroidListener.onCaldroidViewCreated();
		}

		return view;
	}




	/**
	 * Gets the continer weeks view.
	 *
	 * @return the continer weeks view
	 */
	public View getContinerWeeksView(){

		return containerWeeks;
	}



	/**
	 * Setup 4 pages contain date grid views. These pages are recycled to use
	 * memory efficient
	 *
	 * @param view the new up date grid pages
	 */
	private void setupDateGridPages(View view) {
		// Get current date time
		DateTime currentDateTime = new DateTime(year, month, 1, 0, 0, 0, 0);

		// Set to pageChangeListener
		pageChangeListener = new DatePageChangeListener();
		pageChangeListener.setCurrentDateTime(currentDateTime);

		// Setup adapters for the grid views
		// Current month
		CaldroidGridAdapter adapter0 = getNewDatesGridAdapter(
				currentDateTime.getMonth(), currentDateTime.getYear());

		// Setup dateInMonthsList
		dateInMonthsList = adapter0.getDatetimeList();
        currentCalaroidGridAdapter=adapter0;
		// Next month
		DateTime nextDateTime = currentDateTime.plus(0, 1, 0, 0, 0, 0, 0,
				DateTime.DayOverflow.LastDay);
		CaldroidGridAdapter adapter1 = getNewDatesGridAdapter(
				nextDateTime.getMonth(), nextDateTime.getYear());

		// Next 2 month
		DateTime next2DateTime = nextDateTime.plus(0, 1, 0, 0, 0, 0, 0,
				DateTime.DayOverflow.LastDay);
		CaldroidGridAdapter adapter2 = getNewDatesGridAdapter(
				next2DateTime.getMonth(), next2DateTime.getYear());

		// Previous month
		DateTime prevDateTime = currentDateTime.minus(0, 1, 0, 0, 0, 0, 0,
				DateTime.DayOverflow.LastDay);
		CaldroidGridAdapter adapter3 = getNewDatesGridAdapter(
				prevDateTime.getMonth(), prevDateTime.getYear());

		// Add to the array of adapters
		datePagerAdapters.add(adapter0);
		datePagerAdapters.add(adapter1);
		datePagerAdapters.add(adapter2);
		datePagerAdapters.add(adapter3);

		// Set adapters to the pageChangeListener so it can refresh the adapter
		// when page change
		pageChangeListener.setCaldroidGridAdapters(datePagerAdapters);

		// Setup InfiniteViewPager and InfinitePagerAdapter. The
		// InfinitePagerAdapter is responsible
		// for reuse the fragments
		dateViewPager = (InfiniteViewPager) view
				.findViewById(R.id.months_infinite_pager);

		// Set enable swipe
		dateViewPager.setEnabled(enableSwipe);

		// Set if viewpager wrap around particular month or all months (6 rows)
		dateViewPager.setSixWeeksInCalendar(sixWeeksInCalendar);

		// Set the numberOfDaysInMonth to dateViewPager so it can calculate the
		// height correctly
		dateViewPager.setDatesInMonth(dateInMonthsList);

		// MonthPagerAdapter actually provides 4 real fragments. The
		// InfinitePagerAdapter only recycles fragment provided by this
		// MonthPagerAdapter
		final MonthPagerAdapter pagerAdapter = new MonthPagerAdapter(
				getChildFragmentManager());

		// Provide initial data to the fragments, before they are attached to
		// view.
		fragments = pagerAdapter.getFragments();
		for (int i = 0; i < NUMBER_OF_PAGES; i++) {
			DateGridFragment dateGridFragment = fragments.get(i);
			CaldroidGridAdapter adapter = datePagerAdapters.get(i);
			dateGridFragment.setGridAdapter(adapter);
			dateGridFragment.setOnItemClickListener(getDateItemClickListener());
			dateGridFragment
					.setOnItemLongClickListener(getDateItemLongClickListener());
		}

		// Setup InfinitePagerAdapter to wrap around MonthPagerAdapter
		InfinitePagerAdapter infinitePagerAdapter = new InfinitePagerAdapter(
				pagerAdapter);

		// Use the infinitePagerAdapter to provide data for dateViewPager
		dateViewPager.setAdapter(infinitePagerAdapter);

		// Setup pageChangeListener
		dateViewPager.setOnPageChangeListener(pageChangeListener);
	}





	/**
	 * To display the week day title.
	 *
	 * @return "SUN, MON, TUE, WED, THU, FRI, SAT"
	 */
	private ArrayList<String> getDaysOfWeek() {
		ArrayList<String> list = new ArrayList<String>();

		SimpleDateFormat fmt = new SimpleDateFormat("EEE", Locale.getDefault());

		// 17 Feb 2013 is Sunday
		DateTime sunday = new DateTime(2013, 2, 17, 0, 0, 0, 0);
		DateTimeAlongWithJobsCountBean nextDay = new DateTimeAlongWithJobsCountBean(sunday.plusDays(startDayOfWeek - SUNDAY), 0, 0, 0, 0);

		for (int i = 0; i < 7; i++) {
			Date date = CalendarHelper.convertDateTimeToDate(nextDay);
			list.add(fmt.format(date).toUpperCase());
			DateTime dateTime=nextDay.getDateTime().plusDays(1);
			nextDay.setDateTime(dateTime);
		}

		return list;
	}

	/**
	 * DatePageChangeListener refresh the date grid views when user swipe the
	 * calendar.
	 *
	 * @author thomasdao
	 */
	public class DatePageChangeListener implements OnPageChangeListener {

		/** The current page. */
		private int currentPage = InfiniteViewPager.OFFSET;

		/** The current date time. */
		private DateTime currentDateTime;

		/** The caldroid grid adapters. */
		private ArrayList<CaldroidGridAdapter> caldroidGridAdapters;

		/**
		 * Return currentPage of the dateViewPager.
		 *
		 * @return the current page
		 */
		public int getCurrentPage() {
			return currentPage;
		}

		/**
		 * Sets the current page.
		 *
		 * @param currentPage the new current page
		 */
		public void setCurrentPage(int currentPage) {
			this.currentPage = currentPage;
		}

		/**
		 * Return currentDateTime of the selected page.
		 *
		 * @return the current date time
		 */
		public DateTime getCurrentDateTime() {
			return currentDateTime;
		}

		/**
		 * Sets the current date time.
		 *
		 * @param dateTime the new current date time
		 */
		public void setCurrentDateTime(DateTime dateTime) {
			this.currentDateTime = dateTime;
			setCalendarDateTime(currentDateTime);
		}

		/**
		 * Return 4 adapters.
		 *
		 * @return the caldroid grid adapters
		 */
		public ArrayList<CaldroidGridAdapter> getCaldroidGridAdapters() {
			return caldroidGridAdapters;
		}

		/**
		 * Sets the caldroid grid adapters.
		 *
		 * @param caldroidGridAdapters the new caldroid grid adapters
		 */
		public void setCaldroidGridAdapters(
				ArrayList<CaldroidGridAdapter> caldroidGridAdapters) {
			this.caldroidGridAdapters = caldroidGridAdapters;
		}

		/**
		 * Return virtual next position.
		 *
		 * @param position the position
		 * @return the next
		 */
		private int getNext(int position) {
			return (position + 1) % CaldroidFragment.NUMBER_OF_PAGES;
		}

		/**
		 * Return virtual previous position.
		 *
		 * @param position the position
		 * @return the previous
		 */
		private int getPrevious(int position) {
			return (position + 3) % CaldroidFragment.NUMBER_OF_PAGES;
		}

		/**
		 * Return virtual current position.
		 *
		 * @param position the position
		 * @return the current
		 */
		public int getCurrent(int position) {
			return position % CaldroidFragment.NUMBER_OF_PAGES;
		}

		/* (non-Javadoc)
		 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrollStateChanged(int)
		 */
		@Override
		public void onPageScrollStateChanged(int position) {
		}

		/* (non-Javadoc)
		 * @see android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrolled(int, float, int)
		 */
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		/**
		 * Refresh adapters.
		 *
		 * @param position the position
		 */
		public void refreshAdapters(int position) {
			// Get adapters to refresh
			CaldroidGridAdapter currentAdapter = caldroidGridAdapters
					.get(getCurrent(position));
			CaldroidGridAdapter prevAdapter = caldroidGridAdapters
					.get(getPrevious(position));
			CaldroidGridAdapter nextAdapter = caldroidGridAdapters
					.get(getNext(position));

			if (position == currentPage) {
				// Refresh current adapter

				currentAdapter.setAdapterDateTime(currentDateTime);
				currentAdapter.notifyDataSetChanged();

				// Refresh previous adapter
				prevAdapter.setAdapterDateTime(currentDateTime.minus(0, 1, 0,
						0, 0, 0, 0, DateTime.DayOverflow.LastDay));
				prevAdapter.notifyDataSetChanged();

				// Refresh next adapter
				nextAdapter.setAdapterDateTime(currentDateTime.plus(0, 1, 0, 0,
						0, 0, 0, DateTime.DayOverflow.LastDay));
				nextAdapter.notifyDataSetChanged();
			}
			// Detect if swipe right or swipe left
			// Swipe right
			else if (position > currentPage) {
				// Update current date time to next month
				currentDateTime = currentDateTime.plus(0, 1, 0, 0, 0, 0, 0,
						DateTime.DayOverflow.LastDay);

				// Refresh the adapter of next gridview
				nextAdapter.setAdapterDateTime(currentDateTime.plus(0, 1, 0, 0,
						0, 0, 0, DateTime.DayOverflow.LastDay));
				nextAdapter.notifyDataSetChanged();

			}
			// Swipe left
			else {
				// Update current date time to previous month
				currentDateTime = currentDateTime.minus(0, 1, 0, 0, 0, 0, 0,
						DateTime.DayOverflow.LastDay);

				// Refresh the adapter of previous gridview
				prevAdapter.setAdapterDateTime(currentDateTime.minus(0, 1, 0,
						0, 0, 0, 0, DateTime.DayOverflow.LastDay));
				prevAdapter.notifyDataSetChanged();
			}

			// Update current page
			currentPage = position;
		}

		/**
		 * Refresh the fragments.
		 *
		 * @param position the position
		 */
		@Override
		public void onPageSelected(int position) {
			refreshAdapters(position);

			// Update current date time of the selected page
			setCalendarDateTime(currentDateTime);

			// Update all the dates inside current month
			CaldroidGridAdapter currentAdapter = caldroidGridAdapters
					.get(position % CaldroidFragment.NUMBER_OF_PAGES);

			// Refresh dateInMonthsList
			dateInMonthsList.clear();
			dateInMonthsList.addAll(currentAdapter.getDatetimeList());
		}

	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.DialogFragment#onDetach()
	 */
	@Override
	public void onDetach() {
		try {
			Field childFragmentManager = Fragment.class
					.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);

		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}catch (Exception e){

		}
		super.onDetach();
	}
}