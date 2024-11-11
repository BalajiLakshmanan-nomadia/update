package com.synchroteam.listadapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.UnavailabilityBeans;
import com.synchroteam.synchroteam.UpdateUnavailability;
import com.synchroteam.synchroteam3.R;

import java.util.ArrayList;

/**
 * @author Trident
 * 
 *         Adapter for issues list in UpdateUnavailability Class
 *
 */
public class IssuesListAdapterUpdateUnavailability extends ArrayAdapter<String> {
	private final Context context;
	private ArrayList<UnavailabilityBeans> unavailabilityList;
	private boolean isNotFirstTime;
	private String selectedPos;
	private ViewHolder viewHolder;
	private int colorCodePos;
	private String idUser;

	// private static final String TAG = "IssuesListAdapter";

	public IssuesListAdapterUpdateUnavailability(Context context,
			ArrayList<UnavailabilityBeans> unavailabilityList,
			String selectedType, String idUser) {
		super(context, R.layout.rowview_unavailability_issue);
		this.context = context;
		this.unavailabilityList = unavailabilityList;
		this.idUser = idUser;

		for (int i = 0; i < unavailabilityList.size(); i++) {
			if (selectedType.equalsIgnoreCase(unavailabilityList.get(i)
					.getUnavailabilityName())) {
				colorCodePos = i;
				((UpdateUnavailability) context).setIssuePosition(colorCodePos);
			}
		}
	}

	@Override
	public int getCount() {
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

		if (colorCodePos == position) {
			viewHolder.imgSelectTick.setVisibility(View.VISIBLE);
		} else {
			viewHolder.imgSelectTick.setVisibility(View.INVISIBLE);
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

		if (isNotFirstTime && selectedPos != null) {
			int selPos = Integer.parseInt(selectedPos);
			if (selPos == position)
				viewHolder.imgSelectTick.setVisibility(View.VISIBLE);
			else
				viewHolder.imgSelectTick.setVisibility(View.INVISIBLE);
		}

		viewHolder.viewIssue.setTag(position);
		viewHolder.viewIssue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int pos = (Integer) v.getTag();
				viewHolder.imgSelectTick.setVisibility(View.VISIBLE);
				selectedPos = String.valueOf(pos);
				isNotFirstTime = true;
				notifyDataSetChanged();
				((UpdateUnavailability) context).setIssuePosition(pos);

			}
		});

		if (idUser.equalsIgnoreCase("0")){
			viewHolder.viewIssue.setEnabled(false);
		}else{
			viewHolder.viewIssue.setEnabled(true);
		}

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
