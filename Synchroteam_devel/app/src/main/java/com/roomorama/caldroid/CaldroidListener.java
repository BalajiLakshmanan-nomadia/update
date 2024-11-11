package com.roomorama.caldroid;

import java.util.Date;

import android.view.View;

import com.synchroteam.beans.DateTimeAlongWithJobsCountBean;

// TODO: Auto-generated Javadoc
/**
 * CaldroidListener inform when user clicks on a valid date (not within disabled
 * dates, and valid between min/max dates)
 * 
 * The method onChangeMonth is optional, user can always override this to listen
 * to month change event.
 *
 * @author thomasdao
 */
public abstract class CaldroidListener {
	
	/**
	 * Inform client user has clicked on a date.
	 *
	 * @param date the date
	 * @param view the view
	 */
	public abstract void onSelectDate(DateTimeAlongWithJobsCountBean date, View view);

	
	/**
	 * Inform client user has long clicked on a date.
	 *
	 * @param date the date
	 * @param view the view
	 */
	public void onLongClickDate(Date date, View view) {
		// Do nothing
	}

	
	/**
	 * Inform client that calendar has changed month.
	 *
	 * @param month the month
	 * @param year the year
	 */
	public void onChangeMonth(int month, int year) {
		// Do nothing
	};

	
	/**
	 * Inform client that CaldroidFragment view has been created and views are
	 * no longer null. Useful for customization of button and text views
	 */
	public void onCaldroidViewCreated() {
		// Do nothing
	}
}
