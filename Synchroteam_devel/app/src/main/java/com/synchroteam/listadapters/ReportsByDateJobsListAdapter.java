package com.synchroteam.listadapters;

import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.KEYS;

// TODO: Auto-generated Javadoc
/**
 * Adapter Class to for Reports in Jobs detail List.
 * created for future purpose
 * 
 * @author Ideavate.solution
 */
public class ReportsByDateJobsListAdapter extends BaseAdapter {

	
	/** The activity. */
	private  Activity activity;
	
	/** The deadline job beans. */
	private List<HashMap<String, String>> jobsCompletedList;
	
	/** The layout inflater. */
	private LayoutInflater layoutInflater;
	

	
	/**
	 * Instantiates a new deadline jobs list adapter.
	 *
	 * @param activity the activity
	 * @param jobCompletedList the dedline exceeded job list
	 */
	public ReportsByDateJobsListAdapter(Activity activity,List<HashMap<String, String>> jobCompletedList) {
		// TODO Auto-generated constructor stub
		this.activity=activity;
		this.jobsCompletedList=jobCompletedList;
		 layoutInflater= (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 
				
		 
	}
	
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return jobsCompletedList.size();
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public HashMap<String, String> getItem(int position) {
		// TODO Auto-generated method stub
		return jobsCompletedList.get(position);
	}

	/* (non-Javadoc)
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/**
	 * The Class ViewHolder.
	 */
	private class ViewHolder {
		
		/** The left strip iv. */
		ImageView leftStripIv;
		
		
		
		/** The job name tv. */
		TextView jobNameTv;
		
		/** The date or time tv. */
		TextView dateOrTimeTv;
		
		/** The year tv. */
//		TextView yearTv;
		
		/** The job priority tv. */
		TextView jobPriorityTv;
		
		/** The client name tv. */
		TextView clientNameTv;
		
		/** The start time tv. */
		TextView startTimeTv;
		/** The job time status container. */
		RelativeLayout jobTimeStatusContainer;
	}
	
	/* (non-Javadoc)
	 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		HashMap<String, String> currentJobHashmap = getItem(position);

		ViewHolder holder = null;
		if (convertView == null) {
			convertView = layoutInflater.inflate(
					R.layout.layout_reports_list_item, parent,false);
			holder = new ViewHolder();
			holder.leftStripIv = (ImageView) convertView
					.findViewById(R.id.stripColorIv);
			

			holder.jobNameTv = (TextView) convertView
					.findViewById(R.id.jobNameTv);
			holder.dateOrTimeTv = (TextView) convertView
					.findViewById(R.id.dateOrTimeTv);
//			holder.yearTv = (TextView) convertView.findViewById(R.id.yearTv);
			holder.jobPriorityTv = (TextView) convertView
					.findViewById(R.id.jobPriorityTv);
			holder.clientNameTv = (TextView) convertView
					.findViewById(R.id.clientNameTv);
			holder.startTimeTv=(TextView) convertView.findViewById(R.id.startTv);
			holder.jobTimeStatusContainer = (RelativeLayout) convertView
					.findViewById(R.id.jobTimeStatusRl);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		
		
		
//		
//		String dateToShow=null;
//		if(currentJobHashmap.get(KEYS.CurrentJobs.DATE_TO_SHOW).length()!=1){
//			dateToShow=currentJobHashmap.get(KEYS.CurrentJobs.DATE_TO_SHOW);
//		}
//		else{
//			dateToShow="0"+currentJobHashmap.get(KEYS.CurrentJobs.DATE_TO_SHOW);
//		}
		
		
		holder.jobNameTv.setText(currentJobHashmap.get(KEYS.CurrentJobs.TYPE));
		holder.clientNameTv.setText(currentJobHashmap
				.get(KEYS.CurrentJobs.NOM_CLIENT_INTERV));
		
		
		if (!TextUtils.isEmpty(currentJobHashmap
				.get(KEYS.CurrentJobs.DATE_TO_SHOW))) {
			holder.dateOrTimeTv.setText(currentJobHashmap
					.get(KEYS.CurrentJobs.DATE_TO_SHOW)
					+ " ");
		}

//		holder.yearTv.setText(currentJobHashmap
//				.get(KEYS.CurrentJobs.YEAR_TO_SHOW));
		holder.startTimeTv.setText(currentJobHashmap
				.get(KEYS.CurrentJobs.TIME_TO_SHOW));

		holder.leftStripIv
				.setBackgroundResource(R.color.colorBlueStripReportsList);
		holder.jobTimeStatusContainer
				.setBackgroundResource(R.color.colorBluejobTimeStatusRlReoprtsList);
		

		int prority = Integer.parseInt(currentJobHashmap
				.get(KEYS.CurrentJobs.PRIORITY));

		switch (prority) {
		case KEYS.CurrentJobs.PRIORITY_HIGH:
			holder.jobPriorityTv.setText(activity.getResources().getString(
					R.string.textHighPriorityTv).toUpperCase());
			holder.jobPriorityTv.setTextColor(activity.getResources().getColor(R.color.textHighPriorityCurrentJobs));
		
			break;
		case KEYS.CurrentJobs.PRIORITY_NORMAL:
			holder.jobPriorityTv.setText(activity.getResources().getString(
					R.string.textAveragePriorityTv).toUpperCase());
			holder.jobPriorityTv.setTextColor(activity.getResources().getColor(R.color.textNormalPrioriyCurrentJobs));

//			holder.jobPriorityTv.setTextColor(R.color.textColo)
			
			break;
		case KEYS.CurrentJobs.PRIORITY_LOW:
			holder.jobPriorityTv.setText(activity.getResources().getString(
					R.string.textLowPriorityTv).toUpperCase());
			holder.jobPriorityTv.setTextColor(activity.getResources().getColor(R.color.textLowPrioriyCurrentJobs));

			break;
		default:
			break;
		}
		
		return convertView;
	}

	
	
}
