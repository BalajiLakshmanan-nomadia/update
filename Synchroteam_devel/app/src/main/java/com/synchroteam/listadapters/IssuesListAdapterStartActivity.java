package com.synchroteam.listadapters;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.UnavailabilityBeans;
import com.synchroteam.synchroteam.CreateUnavailability;
import com.synchroteam.synchroteam3.R;

/**
 * @author Trident
 * 
 *         Adapter for issues list in CreateUnavailability Class
 *
 */
public class IssuesListAdapterStartActivity extends ArrayAdapter<String> {
	private final Context context;
	private ArrayList<UnavailabilityBeans> unavailabilityList;
	private ViewHolder viewHolder;
	private String selectedPos;

	// private static final String TAG = "IssuesListAdapter";

	public IssuesListAdapterStartActivity(Context context,
			ArrayList<UnavailabilityBeans> unavailabilityList) {
		super(context, R.layout.rowview_unavailability_issue);
		this.context = context;
		this.unavailabilityList = unavailabilityList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return unavailabilityList.size();
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater mInflater = ((Activity) context).getLayoutInflater();
			convertView = mInflater.inflate(
					R.layout.rowview_unavailability_issue, parent, false);
			viewHolder = new ViewHolder(convertView);

			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.txtIssueName.setText(unavailabilityList.get(position)
				.getUnavailabilityName());

		GradientDrawable gd = (GradientDrawable) viewHolder.viewIssue
				.getBackground();
		gd.setColor(Color
				.parseColor("#"
						+ unavailabilityList.get(position)
								.getUnavailabilityColorCode()));
		gd.invalidateSelf();

		if (selectedPos != null) {
			int selPos = Integer.parseInt(selectedPos);
			if (selPos == position)
				viewHolder.imgSelectTick.setVisibility(View.VISIBLE);
			else
				viewHolder.imgSelectTick.setVisibility(View.INVISIBLE);
		} else {
			viewHolder.imgSelectTick.setVisibility(View.INVISIBLE);
		}

		viewHolder.viewIssue.setTag(position);
		viewHolder.viewIssue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int pos = (Integer) v.getTag();
				viewHolder.imgSelectTick.setVisibility(View.VISIBLE);
				selectedPos = String.valueOf(pos);
				notifyDataSetChanged();

				((CreateUnavailability) context).setIssuePosition(pos);
			}
		});
		convertView.setOnClickListener(null);
		return convertView;
	}

	// ...........................VIEWHOLDER...CLASS...STARTS...HERE...........................................
	static class ViewHolder {
		View viewIssue;
		TextView txtIssueName;
		ImageView imgSelectTick;

		public ViewHolder(View rowView) {
			viewIssue = (View) rowView.findViewById(R.id.viewIssueCircle);
			txtIssueName = (TextView) rowView.findViewById(R.id.txtIssueName);
			imgSelectTick = (ImageView) rowView
					.findViewById(R.id.imgSelectTick);
		}

	}

	// ****************************VIEWHOLDER...CLASS...ENDS...HERE*********************************************
}
