package com.synchroteam.TypefaceLibrary;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;

// TODO: Auto-generated Javadoc
/**
 * The Class CustomListView.
 */
public class CustomListView extends ListView {

	/**
	 * Instantiates a new custom list view.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public CustomListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * @see android.widget.ListView#onMeasure(int, int)
	 */
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// HACK! TAKE THAT ANDROID!
		
			// Calculate entire height by providing a very large height hint.
			// But do not use the highest 2 bits of this integer; those are
			// reserved for the MeasureSpec mode.
			int expandSpec = MeasureSpec.makeMeasureSpec(
					Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, expandSpec);

			ViewGroup.LayoutParams params = getLayoutParams();
			params.height = getMeasuredHeight();
		
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	
}
