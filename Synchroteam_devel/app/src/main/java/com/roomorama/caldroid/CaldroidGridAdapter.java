package com.roomorama.caldroid;

import hirondelle.date4j.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.synchroteam.beans.DateTimeAlongWithJobsCountBean;
import com.synchroteam.synchroteam3.R;



// TODO: Auto-generated Javadoc
/**
 * The CaldroidGridAdapter provides customized view for the dates gridview.
 *
 * @author thomasdao
 */
public class CaldroidGridAdapter extends BaseAdapter {

	/** The datetime list. */
	protected ArrayList<DateTimeAlongWithJobsCountBean> datetimeList;

	/** The month. */
	protected int month;

	/** The year. */
	protected int year;

	/** The context. */
	protected Context context;

	/** The disable dates. */
	protected ArrayList<DateTime> disableDates;

	/** The selected dates. */
	protected ArrayList<DateTime> selectedDates;

	// Use internally, to make the search for date faster instead of using
	// indexOf methods on ArrayList
	/** The disable dates map. */
	protected HashMap<DateTime, Integer> disableDatesMap = new HashMap<DateTime, Integer>();

	/** The selected dates map. */
	protected HashMap<DateTime, Integer> selectedDatesMap = new HashMap<DateTime, Integer>();

    /** The date with jobs. */
    protected HashMap<DateTime, DateTimeAlongWithJobsCountBean> dateWithJobs;

	/** The min date time. */
	protected DateTime minDateTime;

	/** The max date time. */
	protected DateTime maxDateTime;

	/** The today. */
	protected DateTime today;

	/** The start day of week. */
	protected int startDayOfWeek;

	/** The six weeks in calendar. */
	protected boolean sixWeeksInCalendar;

	/** The resources. */
	protected Resources resources;

	/** caldroidData belongs to Caldroid. */
	protected HashMap<String, Object> caldroidData;

	/** extraData belongs to client. */
	protected HashMap<String, Object> extraData;

	/**
	 * Sets the adapter date time.
	 *
	 * @param dateTime the new adapter date time
	 */
	public void setAdapterDateTime(DateTime dateTime) {
		this.month = dateTime.getMonth();
		this.year = dateTime.getYear();
		this.datetimeList = CalendarHelper.getFullWeeks(this.month, this.year,
				startDayOfWeek, sixWeeksInCalendar);
	}

	// GETTERS AND SETTERS
	/**
	 * Gets the datetime list.
	 *
	 * @return the datetime list
	 */
	public ArrayList<DateTimeAlongWithJobsCountBean> getDatetimeList() {
		return datetimeList;
	}

	/**
	 * Gets the min date time.
	 *
	 * @return the min date time
	 */
	public DateTime getMinDateTime() {
		return minDateTime;
	}

	/**
	 * Sets the min date time.
	 *
	 * @param minDateTime the new min date time
	 */
	public void setMinDateTime(DateTime minDateTime) {
		this.minDateTime = minDateTime;
	}

	/**
	 * Gets the max date time.
	 *
	 * @return the max date time
	 */
	public DateTime getMaxDateTime() {
		return maxDateTime;
	}

	/**
	 * Sets the max date time.
	 *
	 * @param maxDateTime the new max date time
	 */
	public void setMaxDateTime(DateTime maxDateTime) {
		this.maxDateTime = maxDateTime;
	}

	/**
	 * Gets the disable dates.
	 *
	 * @return the disable dates
	 */
	public ArrayList<DateTime> getDisableDates() {
		return disableDates;
	}

	/**
	 * Sets the disable dates.
	 *
	 * @param disableDates the new disable dates
	 */
	public void setDisableDates(ArrayList<DateTime> disableDates) {
		this.disableDates = disableDates;
	}

	/**
	 * Gets the selected dates.
	 *
	 * @return the selected dates
	 */
	public ArrayList<DateTime> getSelectedDates() {
		return selectedDates;
	}

	/**
	 * Sets the selected dates.
	 *
	 * @param selectedDates the new selected dates
	 */
	public void setSelectedDates(ArrayList<DateTime> selectedDates) {
		this.selectedDates = selectedDates;
	}

	/**
	 * Gets the caldroid data.
	 *
	 * @return the caldroid data
	 */
	public HashMap<String, Object> getCaldroidData() {
		return caldroidData;
	}

	/**
	 * Sets the caldroid data.
	 *
	 * @param caldroidData the caldroid data
	 */
	public void setCaldroidData(HashMap<String, Object> caldroidData) {
		this.caldroidData = caldroidData;

		// Reset parameters
		populateFromCaldroidData();
	}

	/**
	 * Gets the extra data.
	 *
	 * @return the extra data
	 */
	public HashMap<String, Object> getExtraData() {
		return extraData;
	}

	/**
	 * Sets the extra data.
	 *
	 * @param extraData the extra data
	 */
	public void setExtraData(HashMap<String, Object> extraData) {
		this.extraData = extraData;
	}

	/**
	 * Constructor.
	 *
	 * @param context the context
	 * @param month the month
	 * @param year the year
	 * @param caldroidData the caldroid data
	 * @param extraData the extra data
	 */
	public CaldroidGridAdapter(Context context, int month, int year,
			HashMap<String, Object> caldroidData,
			HashMap<String, Object> extraData) {
		super();
		this.month = month;
		this.year = year;
		this.context = context;
		this.caldroidData = caldroidData;
		this.extraData = extraData;
		this.resources = context.getResources();

		// Get data from caldroidData
		populateFromCaldroidData();
	}

	/**
	 * Retrieve internal parameters from caldroid data.
	 */
	@SuppressWarnings("unchecked")
	private void populateFromCaldroidData() {
		disableDates = (ArrayList<DateTime>) caldroidData
				.get(CaldroidFragment.DISABLE_DATES);
		if (disableDates != null) {
			disableDatesMap.clear();
			for (DateTime dateTime : disableDates) {
				disableDatesMap.put(dateTime, 1);
			}
		}

		selectedDates = (ArrayList<DateTime>) caldroidData
				.get(CaldroidFragment.SELECTED_DATES);
		if (selectedDates != null) {
			selectedDatesMap.clear();
			for (DateTime dateTime : selectedDates) {
				selectedDatesMap.put(dateTime, 1);
			}
		}

		minDateTime = (DateTime) caldroidData
				.get(CaldroidFragment._MIN_DATE_TIME);
		maxDateTime = (DateTime) caldroidData
				.get(CaldroidFragment._MAX_DATE_TIME);
		startDayOfWeek = (Integer) caldroidData
				.get(CaldroidFragment.START_DAY_OF_WEEK);
		sixWeeksInCalendar = (Boolean) caldroidData
				.get(CaldroidFragment.SIX_WEEKS_IN_CALENDAR);
		dateWithJobs=(HashMap<DateTime, DateTimeAlongWithJobsCountBean>) caldroidData.get(CaldroidFragment.SELECTED_DATES_WITH_JOB);

		this.datetimeList = CalendarHelper.getFullWeeks(this.month, this.year,
				startDayOfWeek, sixWeeksInCalendar);
	}

	/**
	 * Gets the today.
	 *
	 * @return the today
	 */
	protected DateTime getToday() {
		if (today == null) {
			today = CalendarHelper.convertDateToDateTime(new Date()).getDateTime();
		}

		return today;
	}

	/**
	 * Sets the custom resources.
	 *
	 * @param dateTime the date time
	 * @param backgroundView the background view
	 * @param textView the text view
	 */
	@SuppressWarnings("unchecked")
	protected void setCustomResources(DateTime dateTime, View backgroundView,
			TextView textView) {
		// Set custom background resource
		HashMap<DateTime, Integer> backgroundForDateTimeMap = (HashMap<DateTime, Integer>) caldroidData
				.get(CaldroidFragment._BACKGROUND_FOR_DATETIME_MAP);
		if (backgroundForDateTimeMap != null) {
			// Get background resource for the dateTime
			Integer backgroundResource = backgroundForDateTimeMap.get(dateTime);

			// Set it
			if (backgroundResource != null) {
				backgroundView.setBackgroundResource(backgroundResource
						.intValue());
			}
		}

		// Set custom text color
		HashMap<DateTime, Integer> textColorForDateTimeMap = (HashMap<DateTime, Integer>) caldroidData
				.get(CaldroidFragment._TEXT_COLOR_FOR_DATETIME_MAP);
		if (textColorForDateTimeMap != null) {
			// Get textColor for the dateTime
			Integer textColorResource = textColorForDateTimeMap.get(dateTime);

			// Set it
			if (textColorResource != null) {
				textView.setTextColor(resources.getColor(textColorResource
						.intValue()));
			}
		}
	}

	/**
	 * Customize colors of text and background based on states of the cell
	 * (disabled, active, selected, etc)
	 *
	 * To be used only in getView method.
	 *
	 * @param position the position
	 * @param cellView the cell view
	 */
	protected void customizeTextView(int position, TextView cellView) {
		cellView.setTextColor(Color.BLACK);

		// Get dateTime of this cell
		DateTimeAlongWithJobsCountBean dateTime = this.datetimeList.get(position);

		// Set color of the dates in previous / next month
		if (dateTime.getDateTime().getMonth() != month) {
			cellView.setTextColor(resources
					.getColor(R.color.caldroid_darker_gray));
		}

		boolean shouldResetDiabledView = false;
		boolean shouldResetSelectedView = false;

		// Customize for disabled dates and date outside min/max dates
		if ((minDateTime != null && dateTime.getDateTime().lt(minDateTime))
				|| (maxDateTime != null && dateTime.getDateTime().gt(maxDateTime))
				|| (disableDates != null && disableDatesMap
						.containsKey(dateTime.getDateTime()))) {

			cellView.setTextColor(CaldroidFragment.disabledTextColor);
			if (CaldroidFragment.disabledBackgroundDrawable == -1) {
				cellView.setBackgroundResource(R.drawable.disable_cell);
			} else {
				cellView.setBackgroundResource(CaldroidFragment.disabledBackgroundDrawable);
			}

			if (dateTime.getDateTime().equals(getToday())) {
				cellView.setBackgroundResource(R.drawable.red_border_gray_bg);
			}
		} else {
			shouldResetDiabledView = true;
		}

		// Customize for selected dates
		if (selectedDates != null && selectedDatesMap.containsKey(dateTime.getDateTime())) {
			if (CaldroidFragment.selectedBackgroundDrawable != -1) {
				cellView.setBackgroundResource(CaldroidFragment.selectedBackgroundDrawable);
			} else {
				cellView.setBackgroundColor(resources
						.getColor(R.color.caldroid_sky_blue));
			}

			cellView.setTextColor(CaldroidFragment.selectedTextColor);
		} else {
			shouldResetSelectedView = true;
		}

		if (shouldResetDiabledView && shouldResetSelectedView) {
			// Customize for today
			if (dateTime.getDateTime().equals(getToday())) {
				cellView.setBackgroundResource(R.drawable.red_border);
			} else {
				cellView.setBackgroundResource(R.drawable.cell_bg);
			}
		}

		cellView.setText("" + dateTime.getDateTime().getDay());

		// Set custom color if required
		setCustomResources(dateTime.getDateTime(), cellView, cellView);
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.datetimeList.size();
	}



	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TextView cellView = (TextView) convertView;

		// For reuse
		if (convertView == null) {
			cellView = (TextView) inflater.inflate(R.layout.date_cell, null);
		}

		customizeTextView(position, cellView);

		return cellView;
	}

}
