package com.roomorama.caldroid;

import java.util.ArrayList;

import android.app.Service;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.synchroteam3.R;


// TODO: Auto-generated Javadoc
/**
 * Customize the weekday gridview.
 */
public class WeekdayArrayAdapter extends BaseAdapter {
	
	/** The text color. */
	public static int textColor = Color.WHITE;
      
      /** The layout inflater. */
      private LayoutInflater layoutInflater;
      
      /** The weeks. */
      private ArrayList<String> weeks;
	
	/**
	 * Instantiates a new weekday array adapter.
	 *
	 * @param context the context
	 * @param objects the objects
	 */
	public WeekdayArrayAdapter(Context context,
			 ArrayList<String> objects) {
		this.weeks=objects;
		layoutInflater=(LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
	}

	// To prevent cell highlighted when clicked
	/* (non-Javadoc)
	 * @see android.widget.BaseAdapter#areAllItemsEnabled()
	 */
	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	/* (non-Javadoc)
	 * @see android.widget.BaseAdapter#isEnabled(int)
	 */
	@Override
	public boolean isEnabled(int position) {
		return false;
	}

	// Set color to gray and text size to 12sp
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// To customize text size and color
		View view = layoutInflater.inflate(R.layout.layout_weeks_calander, null);

		
		TextView textView=(TextView) view.findViewById(R.id.calanderweekday);
		
		
		// Set content
		String item = (String) getItem(position);

		// Show smaller text if the size of the text is 4 or more in some
		// locale
		if (item.length() <= 3) {
			textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
		} else {
			textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
		}

		textView.setTextColor(textColor);
		textView.setText(item);
		textView.setGravity(Gravity.CENTER);
		return view;
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return weeks.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return weeks.get(arg0);
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

}
