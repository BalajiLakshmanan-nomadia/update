package com.synchroteam.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Gallery;

/**
 * Custom Gallary View 
 */

public class MyGallery extends Gallery {

	/****
	 * Intialise the New MyGallry View.
	 * @param context
	 * @param attrs
	 */
	public MyGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	private boolean isFling = false;
/***
 * Get the boolean value of fling
 * @return
 */
	public boolean getIsFling() {
		return isFling;
	}
/***
 * Set the boolean valu of fling
 * @param isFling
 */
	public void setFling(boolean isFling) {
		this.isFling = isFling;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		Logger.log("MyGallery>>>>", "onFling");
		isFling = super.onFling(e1, e2, velocityX, velocityY);
		return isFling;

		// return false;
	}

	
	
	
}