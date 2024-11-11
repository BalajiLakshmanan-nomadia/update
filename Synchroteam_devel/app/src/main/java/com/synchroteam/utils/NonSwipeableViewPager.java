package com.synchroteam.utils;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Custom viewpager to restrict the swipe action between the pages.
 * 
 * @author Trident
 *
 */
public class NonSwipeableViewPager extends ViewPager {

	public NonSwipeableViewPager(Context context) {
		super(context);
	}

	public NonSwipeableViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		// returning false will restrict the viewpager to swipe between pages
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		return false;
	}
}
