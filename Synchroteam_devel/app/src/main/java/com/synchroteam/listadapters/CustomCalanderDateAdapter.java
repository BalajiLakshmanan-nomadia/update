package com.synchroteam.listadapters;


import hirondelle.date4j.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Service;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.caldroidx.CaldroidGridAdapter;
import com.synchroteam.beans.DateTimeAlongWithJobsCountBean;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.CalanderDateTextView;

// TODO: Auto-generated Javadoc
/**
 * The Class CustomCalanderDateAdapter.
 */
public class CustomCalanderDateAdapter extends CaldroidGridAdapter {

	/** The layout inflater. */
	private LayoutInflater layoutInflater;
	
	/** The color code completed job. */
	private int colorCodeStartedJob,colorCodeSuspendedJob,colorCodeScheduledJob,colorCodeCompletedJob;


	/**
	 * Instantiates a new custom calander date adapter.
	 *
	 * @param context the context
	 * @param month the month
	 * @param year the year
	 * @param caldroidData the caldroid data
	 * @param extraData the extra data
	 */
	public CustomCalanderDateAdapter(Context context, int month, int year,
									 Map<String, Object> caldroidData,
									 Map<String, Object> extraData) {
		super(context, month, year, caldroidData, extraData);
		// TODO Auto-generated constructor stub
	layoutInflater=(LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
	
	colorCodeStartedJob=context.getResources().getColor(R.color.lineColorStartedJob);
	colorCodeSuspendedJob=context.getResources().getColor(R.color.lineColorSuspendedJob);
	colorCodeScheduledJob=context.getResources().getColor(R.color.lineColorScheduledJob);
	colorCodeCompletedJob=context.getResources().getColor(R.color.lineColorCompletedJob);

	
	}
	
	/**
	 * The Class ViewHolder.
	 */
	private class ViewHolder {
		
		/** The left strip iv. */
	CalanderDateTextView calanderDateTextView;
	
	}
	
	/**
	 * Gets the dat time list.
	 *
	 * @return the dat time list
	 */
	public ArrayList<DateTimeAlongWithJobsCountBean> getDatTimeList(){
		return getDatetimeList();
	}
	

	/* (non-Javadoc)
	 * @see com.roomorama.caldroid.CaldroidGridAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if(convertView==null){
			convertView=layoutInflater.inflate(R.layout.layout_custom_calander_view, parent,false);
			viewHolder=new ViewHolder();
			viewHolder.calanderDateTextView=(CalanderDateTextView) convertView.findViewById(R.id.dateTv);
			
			convertView.setTag(viewHolder);
		
		}
		else{
			viewHolder=(ViewHolder) convertView.getTag();
		} 
		DateTimeAlongWithJobsCountBean dateTimeAlongWithJobsCountBean=super.datetimeList.get(position);
		DateTime dateTime=dateTimeAlongWithJobsCountBean.getDateTime();
		Resources resources = context.getResources();

		// Set color of the dates in previous / next month
		if(dateTime.equals(getToday())){
			viewHolder.calanderDateTextView.setBackgroundResource(R.color.backgroundCurrentDate);
		}
		else if (dateTime.getMonth() != month) {
			viewHolder.calanderDateTextView.setTextColor(resources
					.getColor(R.color.caldroid_darker_gray));
			viewHolder.calanderDateTextView.setBackgroundResource(R.color.backgroundDateOfOtherMonth);
			
		}
		else{
			viewHolder.calanderDateTextView.setTextColor(resources
					.getColor(R.color.caldroid_black));
			viewHolder.calanderDateTextView.setBackgroundResource(R.color.backgroundDateOfCurrentMonth);
		}
		viewHolder.calanderDateTextView.setText(dateTime.getDay()+"");
		
		
		
		
		
		
		
		if (dateWithJobs != null) {
			// Get background resource for the dateTime
			DateTimeAlongWithJobsCountBean dateAlongWithJobsCountBean = dateWithJobs.get(dateTime);

			// Set it
			if (dateAlongWithJobsCountBean != null) {
				viewHolder.calanderDateTextView.setStartedJob(dateAlongWithJobsCountBean.getStartJobCount(),colorCodeStartedJob);
				viewHolder.calanderDateTextView.setSuspendedjob(dateAlongWithJobsCountBean.getSuspendedJobCount(),colorCodeSuspendedJob);
				viewHolder.calanderDateTextView.setScheduledJob(dateAlongWithJobsCountBean.getScheduledJobCount(),colorCodeScheduledJob);
				viewHolder.calanderDateTextView.setCompletedJob(dateAlongWithJobsCountBean.getCompletedJobCount(),colorCodeCompletedJob);
				
				
//				Logger.log("calanderDateTextView Adapter Data ", dateAlongWithJobsCountBean.getDateTime()+"  "
//				+dateAlongWithJobsCountBean.getStartJobCount()+" "+dateAlongWithJobsCountBean.getScheduledJobCount()
//				+" "+dateAlongWithJobsCountBean.getCompletedJobCount()+" "+dateAlongWithJobsCountBean.getSuspendedJobCount());
			}
			else{
				viewHolder.calanderDateTextView.setStartedJob(0,colorCodeStartedJob);
				viewHolder.calanderDateTextView.setSuspendedjob(0,colorCodeSuspendedJob);
				viewHolder.calanderDateTextView.setScheduledJob(0,colorCodeScheduledJob);
				viewHolder.calanderDateTextView.setCompletedJob(0,colorCodeCompletedJob);
			}
			
		}
		else{
			viewHolder.calanderDateTextView.setStartedJob(0,colorCodeStartedJob);
			viewHolder.calanderDateTextView.setSuspendedjob(0,colorCodeSuspendedJob);
			viewHolder.calanderDateTextView.setScheduledJob(0,colorCodeScheduledJob);
			viewHolder.calanderDateTextView.setCompletedJob(0,colorCodeCompletedJob);
		}
		
		
		
		
//		Logger.log(
//				"Date And Various Job Count",
//				dateTime.getDay() + " job " + dateTimeAlongWithJobsCountBean.getStartJobCount() + " "
//						+ dateTimeAlongWithJobsCountBean.getSuspendedJobCount() + ""
//						+ dateTimeAlongWithJobsCountBean.getCompletedJobCount() + ""
//						+ dateTimeAlongWithJobsCountBean.getScheduledJobCount());
//		
		
		return convertView;
	}
	
	
	
	
	
	
	
	
}
