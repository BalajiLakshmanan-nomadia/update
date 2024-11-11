package com.synchroteam.TypefaceLibrary;

import com.synchroteam.synchroteam3.R;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;
import android.widget.TextView;

public class FATextView extends TextView {

	/**
	 * Instantiates the font awesome textview
	 * 
	 * @param context
	 */
	public FATextView(Context context) {
		super(context);
		FontManager.getInstance().setFont(this,
				context.getString(R.string.fontName_fontAwesome));
	}

	public FATextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		if (isInEditMode())
			return;

		FontManager.getInstance().setFont(this, attrs);

	}

	/** The list view. */
	private ListView listView;

	/**
	 * Sets the font.
	 * 
	 * @param fontPath
	 *            the new font
	 */
	public void setFont(String fontPath) {
		FontManager.getInstance().setFont(this, fontPath);
	}

	/**
	 * Sets the font.
	 * 
	 * @param resId
	 *            the new font
	 */
	public void setFont(int resId) {
		String fontPath = getContext().getString(resId);
		setFont(fontPath);
	}

	/**
	 * Set the listView for which this textView is drawn in listView' row and
	 * wanna gain scroll against list scroll.
	 * 
	 * @param listView
	 *            the new list view for scroll enable
	 */
	public void setListViewForScrollEnable(ListView listView) {
		this.listView = listView;
		setVerticalScrollBarEnabled(true);
		setMovementMethod(ScrollingMovementMethod.getInstance());
	}

	/**
	 * Sets the scroll enable.
	 */
	public void setScrollEnable() {
		setVerticalScrollBarEnabled(true);
		setMovementMethod(ScrollingMovementMethod.getInstance());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#dispatchTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		boolean ret;
		ret = super.dispatchTouchEvent(event);
		if (ret && listView != null)
			listView.requestDisallowInterceptTouchEvent(true);
		return ret;
	}
}
