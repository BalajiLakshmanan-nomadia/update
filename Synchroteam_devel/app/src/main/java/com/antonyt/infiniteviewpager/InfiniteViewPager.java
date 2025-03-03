package com.antonyt.infiniteviewpager;

import java.util.ArrayList;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


import com.synchroteam.beans.DateTimeAlongWithJobsCountBean;


// TODO: Auto-generated Javadoc
/**
 * A {@link ViewPager} that allows pseudo-infinite paging with a wrap-around
 * effect. Should be used with an {@link InfinitePagerAdapter}.
 * 
 */
public class InfiniteViewPager extends ViewPager {

	// ******* Declaration *********
	/** The Constant OFFSET. */
	public static final int OFFSET = 1000;

	/** datesInMonth is required to calculate the height correctly. */
	private ArrayList<DateTimeAlongWithJobsCountBean> datesInMonth;

	/** Enable swipe. */
	private boolean enabled = true;

	/**
	 * A calendar height is not fixed, it may have 4, 5 or 6 rows. Set
	 * fitAllMonths to true so that the calendar will always have 6 rows
	 */
	private boolean sixWeeksInCalendar = false;

	/** Use internally to decide height of the calendar. */
	private int rowHeight = 0;

	// ******* Setter and getters *********
	/* (non-Javadoc)
	 * @see android.view.View#isEnabled()
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/* (non-Javadoc)
	 * @see android.view.View#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
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
	 * Gets the dates in month.
	 *
	 * @return the dates in month
	 */
	public ArrayList<DateTimeAlongWithJobsCountBean> getDatesInMonth() {
		return datesInMonth;
	}

	/**
	 * Sets the dates in month.
	 *
	 * @param datesInMonth the new dates in month
	 */
	public void setDatesInMonth(ArrayList<DateTimeAlongWithJobsCountBean> datesInMonth) {
		this.datesInMonth = datesInMonth;
	}

	/**
	 * Sets the six weeks in calendar.
	 *
	 * @param sixWeeksInCalendar the new six weeks in calendar
	 */
	public void setSixWeeksInCalendar(boolean sixWeeksInCalendar) {
		this.sixWeeksInCalendar = sixWeeksInCalendar;
		rowHeight = 0;
	}

	// ************** Constructors ********************
	/**
	 * Instantiates a new infinite view pager.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public InfiniteViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * Instantiates a new infinite view pager.
	 *
	 * @param context the context
	 */
	public InfiniteViewPager(Context context) {
		super(context);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.ViewPager#setAdapter(android.support.v4.view.PagerAdapter)
	 */
	@Override
	public void setAdapter(PagerAdapter adapter) {
		super.setAdapter(adapter);
		// offset first element so that we can scroll to the left
		setCurrentItem(OFFSET);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.ViewPager#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (enabled) {
			return super.onTouchEvent(event);
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.ViewPager#onInterceptTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if (enabled) {
			return super.onInterceptTouchEvent(event);
		}
		return false;
	}

	/**
	 * ViewPager does not respect "wrap_content". The code below tries to
	 * measure the height of the child and set the height of viewpager based on
	 * child height
	 * 
	 * It was customized from
	 * http://stackoverflow.com/questions/9313554/measuring-a-viewpager
	 * 
	 * Thanks Delyan for his brilliant code
	 *
	 * @param widthMeasureSpec the width measure spec
	 * @param heightMeasureSpec the height measure spec
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		// Calculate row height
		int rows = datesInMonth.size() / 7;

		boolean wrapHeight = MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST;

		int height = getMeasuredHeight();
		if (wrapHeight && rowHeight == 0) {
			/*
			 * The first super.onMeasure call made the pager take up all the
			 * available height. Since we really wanted to wrap it, we need to
			 * remeasure it. Luckily, after that call the first child is now
			 * available. So, we take the height from it.
			 */

			int width = getMeasuredWidth();

			// Use the previously measured width but simplify the calculations
			widthMeasureSpec = MeasureSpec.makeMeasureSpec(width,
					MeasureSpec.EXACTLY);

			/*
			 * If the pager actually has any children, take the first child's
			 * height and call that our own
			 */
			if (getChildCount() > 0) {
				View firstChild = getChildAt(0);

				/*
				 * The child was previously measured with exactly the full
				 * height. Allow it to wrap this time around.
				 */
				firstChild.measure(widthMeasureSpec, MeasureSpec
						.makeMeasureSpec(height, MeasureSpec.AT_MOST));

				height = firstChild.getMeasuredHeight();
				rowHeight = height / rows;
			}
		}

		// Calculate height of the calendar
		int calHeight = 0;

		// If fit 6 weeks, we need 6 rows
		if (sixWeeksInCalendar) {
			calHeight = rowHeight * 6;
		} else { // Otherwise we return correct number of rows
			calHeight = rowHeight * rows;
		}

		// Prevent small vertical scroll
		calHeight += 3;

		heightMeasureSpec = MeasureSpec.makeMeasureSpec(calHeight,
				MeasureSpec.EXACTLY);

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

}
