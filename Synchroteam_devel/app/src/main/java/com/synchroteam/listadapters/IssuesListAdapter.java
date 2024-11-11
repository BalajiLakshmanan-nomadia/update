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

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.UnavailabilityBeans;
import com.synchroteam.synchroteam3.R;

/**
 * @author Trident
 * 
 *         Adapter for issues list in CreateUnavailability Class
 *
 */
public class IssuesListAdapter extends ArrayAdapter<String> {
	private final Context context;
	private int pos;
	private ArrayList<UnavailabilityBeans> unavailabilityList;
	private SharedPreferences issuesPositionPref, issuesPositionPref1;
	private Editor edit;
	private String colorCode;

	// private static final String TAG = "IssuesListAdapter";

	private boolean isStartFragment;

	public IssuesListAdapter(Context context,
			ArrayList<UnavailabilityBeans> unavailabilityList,
			boolean isStartFragment, String colorCode) {
		super(context, R.layout.rowview_unavailability_issue);
		this.context = context;
		this.unavailabilityList = unavailabilityList;
		this.isStartFragment = isStartFragment;
		this.colorCode = colorCode;

		issuesPositionPref = context.getSharedPreferences("issues_pref",
				Context.MODE_PRIVATE);
		edit = issuesPositionPref.edit();
		
		for (int i = 0; i < unavailabilityList.size(); i++) {
			if (colorCode.equalsIgnoreCase(unavailabilityList.get(i)
					.getUnavailabilityColorCode())) {
				edit.putString("pos", i + "");
				edit.commit();
			}
		}

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return unavailabilityList.size();
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;

		if (convertView == null) {
			LayoutInflater mInflater = ((Activity) context).getLayoutInflater();
			convertView = mInflater.inflate(
					R.layout.rowview_unavailability_issue, parent, false);
			viewHolder = new ViewHolder(convertView);

			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.positionSelected = position;

		issuesPositionPref1 = context.getSharedPreferences("issues_pref",
				Context.MODE_PRIVATE);
		String savedPosition;
		if (!isStartFragment)
			savedPosition = issuesPositionPref1.getString("pos", null);
		else
			savedPosition = issuesPositionPref1.getString(
					"pos_for_start_fragment", null);

		if (savedPosition != null) {
			pos = Integer.parseInt(savedPosition);
			if (position == pos) {
				viewHolder.imgSelectTick.setVisibility(View.VISIBLE);
			} else {
				viewHolder.imgSelectTick.setVisibility(View.INVISIBLE);
			}
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

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final ViewHolder viewHolder = (ViewHolder) v.getTag();
				viewHolder.imgSelectTick.setVisibility(View.VISIBLE);

				notifyDataSetChanged();

				if (!isStartFragment) {
					edit.putString("pos", viewHolder.positionSelected + "");
				} else {
					edit.putString("pos_for_start_fragment",
							viewHolder.positionSelected + "");
				}
				edit.commit();

			}
		});
		return convertView;
	}

	// ...........................VIEWHOLDER...CLASS...STARTS...HERE...........................................
	static class ViewHolder {
		View viewIssue;
		TextView txtIssueName;
		ImageView imgSelectTick;
		int positionSelected;

		public ViewHolder(View rowView) {
			viewIssue = (View) rowView.findViewById(R.id.viewIssueCircle);
			txtIssueName = (TextView) rowView.findViewById(R.id.txtIssueName);
			imgSelectTick = (ImageView) rowView
					.findViewById(R.id.imgSelectTick);
		}

	}

	// ****************************VIEWHOLDER...CLASS...ENDS...HERE*********************************************
}
