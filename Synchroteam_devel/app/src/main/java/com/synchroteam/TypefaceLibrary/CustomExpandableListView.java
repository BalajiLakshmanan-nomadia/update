package com.synchroteam.TypefaceLibrary;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;

// TODO: Auto-generated Javadoc
/**
 * The Class CustomExpandableListView.
 */
public class CustomExpandableListView extends ExpandableListView {

	/**
	 * Instantiates a new custom expandable list view.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public CustomExpandableListView(Context context, AttributeSet attrs) {
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
//		int height = 0;
//		for (int i = 0; i < getChildCount(); i++) {
//			View child = getChildAt(i);
//			child.measure(widthMeasureSpec,
//					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//			int h = child.getMeasuredHeight();
////			if (h > height){
////				
////			}
//				height = height+h;
//		}
//
//		heightMeasureSpec = MeasureSpec.makeMeasureSpec(height,
//				MeasureSpec.EXACTLY);

		
		ListAdapter adapter = this.getAdapter();
		int listviewHeight = 0;
//		this.measure(MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED,
//				MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0,
//				MeasureSpec.UNSPECIFIED));
		listviewHeight = this.getMeasuredHeight() * adapter.getCount()
				+ (adapter.getCount() * this.getDividerHeight());
		
		
		heightMeasureSpec = MeasureSpec.makeMeasureSpec(listviewHeight,
				MeasureSpec.EXACTLY);
		
		
		
		
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	

	

}
