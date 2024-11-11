package com.synchroteam.listadapters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.TreeMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.FragmentActivity;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.dialogs.JobDetailPopupDialog;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.technicalsupport.JobDetails;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;

// TODO: Auto-generated Javadoc
/**
 * Adapter Class to for Deadline Jobs List.
 */
@SuppressLint("SimpleDateFormat")
public class AllJobsListAdapter extends BaseAdapter {

	/** The activity. */
	private FragmentActivity activity;

	/** The deadline job beans. */
	private TreeMap<String, ArrayList<HashMap<String, String>>> allJobList;

	/** The key set all job list. */
	private ArrayList<String> keySetAllJobList;

	/** The layout inflater. */
	private final LayoutInflater layoutInflater;

	

	/** The old date pattern. */
	private SimpleDateFormat headerDateFormat,oldDatePattern;

	private String started;

	private String suspended;

	private String scheduled;

	private String completed;
	private int startedTextColor;

	private int suspendedTextColor;

	private int scheduledTextColor;

	private int completedTextColor;
	private Context context;

	/**
	 * Instantiates a new deadline jobs list adapter.
	 *
	 * @param activity            the activity
	 * @param allJobList the all job list
	 */
	public AllJobsListAdapter(FragmentActivity activity,
			TreeMap<String, ArrayList<HashMap<String, String>>> allJobList) {
		// TODO Auto-generated constructor stub
		this.activity = activity;
		this.context=activity;
		this.allJobList = allJobList;
		headerDateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");
		oldDatePattern = new SimpleDateFormat("yyyy-MM-dd");
		layoutInflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		keySetAllJobList = new ArrayList<String>(allJobList.keySet());
		
		
		
		started=activity.getString(R.string.textStarted);
		suspended=activity.getString(R.string.textSuspended);
		scheduled=activity.getString(R.string.textScheduled);
		completed=activity.getString(R.string.textCompleted);
		
		startedTextColor=activity.getResources().getColor(R.color.textColorGreen);
		suspendedTextColor=activity.getResources().getColor(R.color.textColorOrange);
		scheduledTextColor=activity.getResources().getColor(R.color.textColorBlack);
		completedTextColor=activity.getResources().getColor(R.color.textColorLightBlue);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return keySetAllJobList.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public ArrayList<HashMap<String, String>> getItem(int position) {
		// TODO Auto-generated method stub
		return allJobList.get(keySetAllJobList.get(position));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// /** The left strip iv. */
		ImageView leftStripIv;
		//
		// /** The job status iv. */
		TextView jobStatusIv;
		//
		// /** The job name tv. */
		TextView jobNameTv;
		//
		// /** The date or time tv. */
		TextView dateOrTimeTv;

		android.widget.TextView txtLockIc;

		 TextView customerAddress;
		//
		//

		//
		// /** The job priority tv. */
		TextView jobPriorityTv;
		//
		// /** The client name tv. */
		TextView clientNameTv;
		TextView startTimeTv;
		// /** The job time status container. */
		RelativeLayout jobTimeStatusContainer;
		TextView dateDetailCurrentJobsTv;

		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.layout_all_job_list_item, parent, false);
			LinearLayout containerJobList = (LinearLayout) convertView
					.findViewById(R.id.containerLinearlayout);

			dateDetailCurrentJobsTv = (TextView) convertView
					.findViewById(R.id.dateDetailCurrentJobsTv);

			ArrayList<HashMap<String, String>> currentJobHashmapList = getItem(position);

			dateDetailCurrentJobsTv.setVisibility(View.VISIBLE);
			try {
				dateDetailCurrentJobsTv.setText(headerDateFormat.format(Objects.requireNonNull(oldDatePattern.parse(keySetAllJobList.get(position)))));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				Logger.printException(e);
			}

			for (HashMap<String, String> stringStringHashMap : currentJobHashmapList) {
				View subItem = layoutInflater.inflate(R.layout.layout_all_job_item, parent, false);
				HashMap<String, String> currentJobHashMap = stringStringHashMap;
				subItem.setTag(currentJobHashMap);
				leftStripIv = (ImageView) subItem.findViewById(R.id.stripColorIv);
				jobStatusIv = (TextView) subItem.findViewById(R.id.jobStatusTv);

				jobNameTv = (TextView) subItem.findViewById(R.id.jobNameTv);
				dateOrTimeTv = (TextView) subItem.findViewById(R.id.dateOrTimeTv);
				// yearTv = (TextView) view.findViewById(R.id.yearTv);
				jobPriorityTv = (TextView) subItem.findViewById(R.id.jobPriorityTv);
				clientNameTv = (TextView) subItem.findViewById(R.id.clientNameTv);
				startTimeTv = (TextView) subItem.findViewById(R.id.startTime);
				jobTimeStatusContainer = (RelativeLayout) subItem
						.findViewById(R.id.jobTimeStatusRl);
				txtLockIc = (android.widget.TextView) subItem.findViewById(R.id.txt_ic_lock);
				customerAddress = subItem.findViewById(R.id.customer_addressTv);

				jobNameTv.setText(currentJobHashMap.get(KEYS.CurrentJobs.TYPE));
				customerAddress.setText(currentJobHashMap.get(KEYS.CurrentJobs.ADR_GLOBAL));
				clientNameTv.setText(currentJobHashMap
						.get(KEYS.CurrentJobs.NOM_CLIENT_INTERV));

				int status = Integer.parseInt(currentJobHashMap
						.get(KEYS.CurrentJobs.CD_STATUS));
				jobPriorityTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);


				Typeface typeface = Typeface.createFromAsset(activity.getAssets(),
						activity.getString(R.string.fontName_fontAwesome));
				txtLockIc.setTypeface(typeface);

				switch (status) {
					case KEYS.CurrentJobs.JOB_NOT_STARTED2:
					case KEYS.CurrentJobs.JOB_NOT_STARTED1:
						leftStripIv
								.setBackgroundResource(R.color.blackStripBackgroundCurrentJobs);
						jobStatusIv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.schedule_icon, 0, 0, 0);
						jobStatusIv.setText(scheduled);
						jobStatusIv.setTextColor(scheduledTextColor);
						jobTimeStatusContainer
								.setBackgroundResource(R.color.completedOrNotStartedJobsBackgroundCurrentJobs);


						dateOrTimeTv.setText(currentJobHashMap
								.get(KEYS.CurrentJobs.DATE_TO_SHOW) + " ");
						// yearTv.setVisibility(View.VISIBLE);
						// yearTv.setText(currentJobHashmap.get(KEYS.CurrentJobs.YEAR_TO_SHOW));
						Logger.log(">>>>>",
								currentJobHashMap.get(KEYS.CurrentJobs.TIME_TO_SHOW));
						startTimeTv.setText(currentJobHashMap
								.get(KEYS.CurrentJobs.TIME_TO_SHOW));
						break;

					case KEYS.CurrentJobs.JOB__STARTED:
						leftStripIv
								.setBackgroundResource(R.color.greenStripBackgroundCurrentJobs);
						jobStatusIv.setVisibility(View.VISIBLE);
						jobStatusIv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.started_btn, 0, 0, 0);
						jobStatusIv.setText(started);
						jobStatusIv.setTextColor(startedTextColor);
						jobTimeStatusContainer
								.setBackgroundResource(R.color.startedJobsBackgroundCurrentJobs);
						dateOrTimeTv.setText(currentJobHashMap
								.get(KEYS.CurrentJobs.DATE_TO_SHOW) + " ");
						// yearTv.setText(currentJobHashmap.get(KEYS.CurrentJobs.YEAR_TO_SHOW));

						startTimeTv.setText(currentJobHashMap
								.get(KEYS.CurrentJobs.TIME_TO_SHOW));
						break;
					case KEYS.CurrentJobs.JOB__SUSPENDED:
						leftStripIv
								.setBackgroundResource(R.color.orangeStripBackgroundCurrentJobs);
						jobStatusIv.setVisibility(View.VISIBLE);
						jobTimeStatusContainer
								.setBackgroundResource(R.color.suspendedJobsBackgroundCurrentJobs);
						jobStatusIv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.suspended_btn, 0, 0, 0);
						jobStatusIv.setText(suspended);
						jobStatusIv.setTextColor(suspendedTextColor);
						if (!TextUtils.isEmpty(currentJobHashMap
								.get(KEYS.CurrentJobs.DATE_TO_SHOW))) {
							dateOrTimeTv.setText(currentJobHashMap
									.get(KEYS.CurrentJobs.DATE_TO_SHOW) + " ");
						}

						// yearTv.setText(currentJobHashmap.get(KEYS.CurrentJobs.YEAR_TO_SHOW));
						startTimeTv.setText(currentJobHashMap
								.get(KEYS.CurrentJobs.TIME_TO_SHOW));
						break;
					case KEYS.CurrentJobs.JOB__COMPLETE:
						leftStripIv
								.setBackgroundResource(R.color.colorBlueStripReportsList);

						jobTimeStatusContainer
								.setBackgroundResource(R.color.colorBluejobTimeStatusRlReoprtsList);
						jobStatusIv.setVisibility(View.VISIBLE);
						jobStatusIv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.completed_job_ind, 0, 0, 0);
						jobStatusIv.setText(completed);
						jobStatusIv.setTextColor(completedTextColor);

						jobNameTv.setText(currentJobHashMap.get(KEYS.CurrentJobs.TYPE));
						clientNameTv.setText(currentJobHashMap
								.get(KEYS.CurrentJobs.NOM_CLIENT_INTERV));
						dateOrTimeTv.setText(currentJobHashMap
								.get(KEYS.CurrentJobs.DATE_TO_SHOW) + " ");

						startTimeTv.setText(currentJobHashMap
								.get(KEYS.CurrentJobs.TIME_TO_SHOW));
						break;

					default:
						break;
				}

				int prority = Integer.parseInt(currentJobHashMap
						.get(KEYS.CurrentJobs.PRIORITY));

				switch (prority) {
					case KEYS.CurrentJobs.PRIORITY_HIGH:
						jobPriorityTv.setText(activity.getResources().getString(
								R.string.textHighPriorityTv).toUpperCase());
						jobPriorityTv.setTextColor(activity.getResources().getColor(
								R.color.textHighPriorityCurrentJobs));

						break;
					case KEYS.CurrentJobs.PRIORITY_NORMAL:
						jobPriorityTv.setText(activity.getResources().getString(
								R.string.textAveragePriorityTv).toUpperCase());
						jobPriorityTv.setTextColor(activity.getResources().getColor(
								R.color.textNormalPrioriyCurrentJobs));

						// jobPriorityTv.setTextColor(R.color.textColo)

						break;
					case KEYS.CurrentJobs.PRIORITY_LOW:
						jobPriorityTv.setText(activity.getResources().getString(
								R.string.textLowPriorityTv).toUpperCase());
						jobPriorityTv.setTextColor(activity.getResources().getColor(
								R.color.textLowPrioriyCurrentJobs));

						break;
					default:
						break;
				}

				String dtMeeting = currentJobHashMap.get(KEYS.CurrentJobs.DATEMEETING);
				if (!TextUtils.isEmpty(dtMeeting) && !Objects.requireNonNull(dtMeeting).equalsIgnoreCase("null")) {
					txtLockIc.setVisibility(View.VISIBLE);
				} else {
					txtLockIc.setVisibility(View.GONE);
				}

				subItem.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						@SuppressWarnings("unchecked")
						HashMap<String, String> map = (HashMap<String, String>) v
								.getTag();

						if (!TextUtils.isEmpty(map.get(KEYS.CurrentJobs.DISPO))) {
							if (!Objects.equals(map.get(KEYS.CurrentJobs.DISPO), KEYS.CurrentJobs.TRUE)) {
								Bundle bundle = new Bundle();
								String[] numInterv = Objects.requireNonNull(map.get(KEYS.CurrentJobs.TYPE))
										.split("-");
								String nmInterv = "";
								if (numInterv.length == 2)
									nmInterv = numInterv[0].substring(1);
								else
									nmInterv = "0";

								bundle.putString(KEYS.CurrentJobs.ID,
										map.get(KEYS.CurrentJobs.ID));

								bundle.putInt(KEYS.CurrentJobs.CD_STATUS, Integer
										.parseInt(map.get(KEYS.CurrentJobs.CD_STATUS)));
								bundle.putString(KEYS.CurrentJobs.ID_USER,
										map.get(KEYS.CurrentJobs.ID_USER));
								bundle.putString(KEYS.CurrentJobs.CONTACT,
										map.get(KEYS.CurrentJobs.CONTACT));
								bundle.putString(KEYS.CurrentJobs.TEL,
										map.get(KEYS.CurrentJobs.TEL));
								bundle.putString(KEYS.CurrentJobs.ADR_GLOBAL,
										map.get(KEYS.CurrentJobs.ADR_GLOBAL));
								bundle.putString(KEYS.CurrentJobs.ADR_COMPLEMENT,
										map.get(KEYS.CurrentJobs.ADR_COMPLEMENT));
								bundle.putString(KEYS.CurrentJobs.DESC,
										map.get(KEYS.CurrentJobs.DESC));
								bundle.putString(KEYS.CurrentJobs.ID_MODEL,
										map.get(KEYS.CurrentJobs.ID_MODEL));
								bundle.putString(KEYS.CurrentJobs.LAT,
										map.get(KEYS.CurrentJobs.LAT));
								bundle.putString(KEYS.CurrentJobs.LON,
										map.get(KEYS.CurrentJobs.LON));
								bundle.putString(KEYS.CurrentJobs.MDATE1,
										map.get(KEYS.CurrentJobs.MDATE1));
								bundle.putString(KEYS.CurrentJobs.MDATE2,
										map.get(KEYS.CurrentJobs.MDATE2));
								bundle.putString("NumIntevType", nmInterv);
								bundle.putString(KEYS.CurrentJobs.NOMSITE,
										map.get("nomSite"));
								bundle.putString(KEYS.CurrentJobs.NOMEQUIPMENT,
										map.get(KEYS.CurrentJobs.NOMEQUIPMENT));
								bundle.putInt(KEYS.CurrentJobs.IDSITE, Integer
										.parseInt(map.get(KEYS.CurrentJobs.IDSITE)));
								bundle.putInt(KEYS.CurrentJobs.IDCLIENT, Integer
										.parseInt(map.get(KEYS.CurrentJobs.IDCLIENT)));
								bundle.putInt(KEYS.CurrentJobs.IDEQUIPMENT, Integer
										.parseInt(map.get(KEYS.CurrentJobs.IDEQUIPMENT)));
								bundle.putString(KEYS.CurrentJobs.TELCEL,
										map.get(KEYS.CurrentJobs.TELCEL));
								bundle.putString(KEYS.CurrentJobs.DATEMEETING,
										map.get(KEYS.CurrentJobs.DATEMEETING));
								bundle.putString(KEYS.CurrentJobs.TYPE,
										map.get(KEYS.CurrentJobs.TYPE));

								bundle.putString(KEYS.CurrentJobs.FROM_WHERE,
										KEYS.CurrentJobs.SYNCROTEAMNAVIGATIONACTIVITY);
								bundle.putBoolean(KEYS.CurrentJobs.IDSTARTJOB, false);
								// closeDb();
								Intent jobDetailIntent = new Intent(activity,
										JobDetails.class);
								jobDetailIntent.putExtras(bundle);
								activity.startActivity(jobDetailIntent);
							}
						}

					}
				});

				subItem.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						{
							HashMap<String, String> map = (HashMap<String, String>) v
									.getTag();
							if (map != null)
								if (!TextUtils.isEmpty(map.get(KEYS.CurrentJobs.DISPO))) {
									if (!Objects.equals(map.get(KEYS.CurrentJobs.DISPO), KEYS.CurrentJobs.TRUE)) {
										Bundle bundle = new Bundle();

										String[] numInterv = Objects.requireNonNull(map.get(KEYS.CurrentJobs.TYPE))
												.split("-");
										String nmInterv = "";
										if (numInterv != null && numInterv.length == 2)
											nmInterv = numInterv[0].substring(1);
										else
											nmInterv = "0";

										Logger.log(">>>>>", "job id is in nav act" + map.get(KEYS.CurrentJobs.ID));
										Logger.log(">>>>>", "job idUser is in nav act" + map.get(KEYS.CurrentJobs.ID_USER));


										bundle.putString(KEYS.CurrentJobs.ID,
												map.get(KEYS.CurrentJobs.ID));

										bundle.putInt(KEYS.CurrentJobs.CD_STATUS, Integer
												.parseInt(map.get(KEYS.CurrentJobs.CD_STATUS)));
										bundle.putString(KEYS.CurrentJobs.ID_USER,
												map.get(KEYS.CurrentJobs.ID_USER));
										bundle.putString(KEYS.CurrentJobs.ID_MODEL,
												map.get(KEYS.CurrentJobs.ID_MODEL));
										bundle.putString(KEYS.CurrentJobs.LAT,
												map.get(KEYS.CurrentJobs.LAT));
										bundle.putString(KEYS.CurrentJobs.LON,
												map.get(KEYS.CurrentJobs.LON));
										bundle.putString(KEYS.CurrentJobs.MDATE1,
												map.get(KEYS.CurrentJobs.MDATE1));
										bundle.putString(KEYS.CurrentJobs.MDATE2,
												map.get(KEYS.CurrentJobs.MDATE2));
										bundle.putString("NumIntevType", nmInterv);
										bundle.putString(KEYS.CurrentJobs.DATEMEETING,
												map.get(KEYS.CurrentJobs.DATEMEETING));

										bundle.putString(KEYS.CurrentJobs.FROM_WHERE,
												KEYS.CurrentJobs.SYNCROTEAMNAVIGATIONACTIVITY);
										bundle.putBoolean(KEYS.CurrentJobs.IDSTARTJOB, false);


										bundle.putString(KEYS.CurrentJobs.CONTACT,
												map.get(KEYS.CurrentJobs.CONTACT));
										bundle.putString(KEYS.CurrentJobs.TEL,
												map.get(KEYS.CurrentJobs.TEL));
										bundle.putString(KEYS.CurrentJobs.ADR_GLOBAL,
												map.get(KEYS.CurrentJobs.ADR_GLOBAL));
										bundle.putString(KEYS.CurrentJobs.ADR_COMPLEMENT,
												map.get(KEYS.CurrentJobs.ADR_COMPLEMENT));
										bundle.putString(KEYS.CurrentJobs.DESC,
												map.get(KEYS.CurrentJobs.DESC));

										bundle.putString(KEYS.CurrentJobs.NOMSITE,
												map.get("nomSite"));
										bundle.putString(KEYS.CurrentJobs.NOMEQUIPMENT,
												map.get(KEYS.CurrentJobs.NOMEQUIPMENT));
										bundle.putInt(KEYS.CurrentJobs.IDSITE, Integer
												.valueOf(map.get(KEYS.CurrentJobs.IDSITE)));
										bundle.putInt(KEYS.CurrentJobs.IDCLIENT, Integer
												.valueOf(map.get(KEYS.CurrentJobs.IDCLIENT)));
										bundle.putInt(KEYS.CurrentJobs.IDEQUIPMENT, Integer
												.valueOf(map.get(KEYS.CurrentJobs.IDEQUIPMENT)));
										bundle.putString(KEYS.CurrentJobs.TELCEL,
												map.get(KEYS.CurrentJobs.TELCEL));

										bundle.putString(KEYS.CurrentJobs.TYPE,
												map.get(KEYS.CurrentJobs.TYPE));

										bundle.putString(KEYS.CurrentJobs.NOM_CLIENT_INTERV,
												map.get(KEYS.CurrentJobs.NOM_CLIENT_INTERV));

										bundle.putString(KEYS.CurrentJobs.MOBILE_CONTACT,
												map.get(KEYS.CurrentJobs.MOBILE_CONTACT));

										bundle.putString(KEYS.CurrentJobs.ID, map.get(KEYS.CurrentJobs.ID));

										JobDetailPopupDialog jobDetailPopupDialog = JobDetailPopupDialog.newInstance(bundle,
												new JobDetailPopupDialog.JobDetailsNavigationInterface() {
													@Override
													public void open() {
													}

													@Override
													public void close() {
													}
												});
										jobDetailPopupDialog.show(activity.getSupportFragmentManager(),"");
									}
								}

							return true;
						}					}
				});


				containerJobList.addView(subItem);
			}

		}
		return convertView;

		}

	/* (non-Javadoc)
	 * @see android.widget.BaseAdapter#notifyDataSetChanged()
	 */
	@Override
	public void notifyDataSetChanged() {
		// TODO Auto-generated method stub
		try {
			keySetAllJobList.clear();
			keySetAllJobList.addAll(allJobList.keySet());
		} catch (Exception e) {
			e.printStackTrace();
			Logger.printException(e);
		}

		super.notifyDataSetChanged();
	}

}
