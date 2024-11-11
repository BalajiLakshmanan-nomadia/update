package com.synchroteam.fragmenthelper;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.synchroteam.baseactivity.SyncroTeamBaseActivity;
import com.synchroteam.beans.CommonJobBean;
import com.synchroteam.beans.EnableSynchronizationAddJobEvent;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.fragment.BaseFragment;
import com.synchroteam.listadapters.DeadlineJobsListAdapter;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc
/**
 * This class is responsible to handle all the functionality of Deadline
 * Exceeded screen. 1.To inflate layout of Deadline Exceeded Jobs Screen 2.To
 * fetch Data from Ultra Lite Db and show it in list view.
 * 
 * @author $Ideavate Solution
 * 
 */
@SuppressLint("SimpleDateFormat")
public class DeadlineExceededFragmentHelper implements HelperInterface {

	/** The syncro team base activity. */
	private SyncroTeamBaseActivity syncroTeamBaseActivity;

	/** The deadline excedded list view. */
	private ListView deadlineExceddedListView;

	/** The progress d synch. */
	protected ProgressDialog progressDSynch;

	/** The full months. */
	private FullMonths[] fullMonths;

	/** The data acees object. */
	private Dao dataAceesObject;

	/** The dedline exceeded list. */
	private TreeMap<String, ArrayList<HashMap<String, String>>> dedlineExceededList;

	/** The deadline jobs list adapter. */
	private DeadlineJobsListAdapter deadlineJobsListAdapter;

	/** The calendar for jobs. */
	private Calendar calendarForJobs;

	/** The base fragment. */
	private BaseFragment baseFragment;

	/** The date format. */
	private DateFormat dateFormat;

	/** The format. */
	private Format format;

	/**
	 * The Enum FullMonths.
	 */
	private User user;

	private ProgressBar progressBarDeadlineJob;

	/**
	 * The Enum FullMonths.
	 */
	public enum FullMonths {

		/** The januaruy. */
		January,
		/** The febuary. */
		Febuary,
		/** The march. */
		March,
		/** The april. */
		April,
		/** The may. */
		May,
		/** The june. */
		June,
		/** The july. */
		July,
		/** The august. */
		August,
		/** The september. */
		September,
		/** The october. */
		October,
		/** The november. */
		November,
		/** The december. */
		December;
	}

	/**
	 * The Enum MonthsOfYear.
	 */
	public enum MonthsOfYear {

		/** The jan. */
		JAN,
		/** The feb. */
		FEB,
		/** The mar. */
		MAR,
		/** The apr. */
		APR,
		/** The may. */
		MAY,
		/** The jun. */
		JUN,
		/** The jul. */
		JUL,
		/** The aug. */
		AUG,
		/** The sep. */
		SEP,
		/** The oct. */
		OCT,
		/** The nov. */
		NOV,
		/** The dec. */
		DEC;
	}

	/** The months of year. */
	MonthsOfYear[] monthsOfYear;

	/**
	 * Instantiates a new deadline exceeded fragment helper.
	 * 
	 * @param syncroTeamBaseActivity
	 *            the syncro team base activity
	 * @param baseFragment
	 *            the base fragment
	 */
	public DeadlineExceededFragmentHelper(
			SyncroTeamBaseActivity syncroTeamBaseActivity,
			BaseFragment baseFragment) {
		// TODO Auto-generated constructor stub

		this.syncroTeamBaseActivity = syncroTeamBaseActivity;
		dataAceesObject = DaoManager.getInstance();
		this.baseFragment = baseFragment;

		calendarForJobs = Calendar.getInstance();
		monthsOfYear = MonthsOfYear.values();
		fullMonths = FullMonths.values();
		user = dataAceesObject.getUser();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.synchroteam.fragmenthelper.HelperInterface#inflateLayout(android.
	 * view.LayoutInflater, android.view.ViewGroup)
	 */
	@Override
	public View inflateLayout(LayoutInflater inflater, ViewGroup container) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.layout_deadline_exceeded,
				container, false);
		initiateView(view);
		fullMonths = FullMonths.values();
		dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
		format = android.text.format.DateFormat
				.getTimeFormat(syncroTeamBaseActivity);
		progressBarDeadlineJob = (ProgressBar) view
				.findViewById(R.id.progressBarDeadlineJob);

		new FetchDeadlineExceededJobs().execute();

		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.synchroteam.fragmenthelper.HelperInterface#initiateView(android.view
	 * .View)
	 */
	@Override
	public void initiateView(View v) {
		// TODO Auto-generated method stub
		deadlineExceddedListView = (ListView) v
				.findViewById(R.id.deadlineJobLv);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synchroteam.fragmenthelper.HelperInterface#doOnSyncronize()
	 */
	@Override
	public void doOnSyncronize() {
		// TODO Auto-generated method stub
		new FetchDeadlineExceededJobs().execute();

	}

	/**
	 * Creates the adapter and inflate data in list view.
	 */
	private void createAdapterAndInflateDataInListView() {
		if (dedlineExceededList != null) {
			dedlineExceededList.clear();
		} else {
			dedlineExceededList = new TreeMap<String, ArrayList<HashMap<String, String>>>(
					new Comparator<String>() {

						@Override
						public int compare(String lhs, String rhs) {
							// TODO Auto-generated method stub
							return lhs.compareTo(rhs);
						}
					});
		}

		HashMap<String, String> map;

		Vector<CommonJobBean> vect = new Vector<CommonJobBean>();

		vect = dataAceesObject.getAllInterventionBefore(user.getId());

		Enumeration<CommonJobBean> en = vect.elements();

		while (en.hasMoreElements()) {
			CommonJobBean interv = en.nextElement();
			int st = interv.getCd_status_interv();
			map = new HashMap<String, String>();

			String numInterv = interv.getType_Interv();

			map.put(KEYS.CurrentJobs.DISPO, "false");
			if (!TextUtils.isEmpty(interv.getRef_customer()))
				numInterv = "#" + interv.getRef_customer() + " - "
						+ interv.getType_Interv();
			else if (interv.getNo_interv() != 0)
				numInterv = "#" + interv.getNo_interv() + " - "
						+ interv.getType_Interv();

			map.put(KEYS.CurrentJobs.ID, interv.getId());
			map.put(KEYS.CurrentJobs.CD_STATUS,
					String.valueOf(interv.getCd_status_interv()));
			map.put(KEYS.CurrentJobs.ALL_JOB_HEADER, interv.getHeaderDate());
			map.put(KEYS.CurrentJobs.ID_USER,
					String.valueOf(interv.getIdUser()));

			map.put(KEYS.CurrentJobs.ID_MODEL,
					String.valueOf(interv.getId_model_rapport()));
			map.put(KEYS.CurrentJobs.TYPE, numInterv);
			map.put(KEYS.CurrentJobs.PRIORITY, interv.getPriorite() + "");

			if (!TextUtils.isEmpty(interv.getNom_site_interv()))
				map.put(KEYS.CurrentJobs.CLTVILLE, interv.getNom_site_interv()
						+ " - " + interv.getAdr_interv_ville());
			else
				map.put(KEYS.CurrentJobs.CLTVILLE,
						interv.getNom_client_interv() + " - "
								+ interv.getAdr_interv_ville());

			map.put(KEYS.CurrentJobs.LAT, interv.getLat());
			map.put(KEYS.CurrentJobs.LON, interv.getLon());

			map.put(KEYS.CurrentJobs.NOMSITE, interv.getNom_site_interv());
			map.put(KEYS.CurrentJobs.NOMEQUIPMENT, interv.getNom_equipement());
			map.put(KEYS.CurrentJobs.IDSITE, String.valueOf(interv.getIdSite()));
			map.put(KEYS.CurrentJobs.IDCLIENT,
					String.valueOf(interv.getIdClient()));
			map.put(KEYS.CurrentJobs.IDEQUIPMENT,
					String.valueOf(interv.getIdEquipement()));
			
			map.put(KEYS.CurrentJobs.NOM_CLIENT_INTERV,
					interv.getNom_client_interv());

			map.put(KEYS.CurrentJobs.DESC,
					String.valueOf(interv.getDesc()));

			map.put(KEYS.CurrentJobs.TELCEL,
					interv.getTel_contact());

			map.put(KEYS.CurrentJobs.MOBILE_CONTACT,
					interv.getMobileContact());


			Date date;

			date = getDateFromDbFormat(interv.getDt_deb_prev());

			String dateToshow = null;
			if (st == KEYS.CurrentJobs.JOB__STARTED) {
				dateToshow = dataAceesObject.getJobStartTime(interv.getId());
				if (!TextUtils.isEmpty(dateToshow)) {
					String[] dateTopass = dateToshow.split("/");

					map.put(KEYS.CurrentJobs.DATE_TO_SHOW, dateTopass[0]);

					map.put(KEYS.CurrentJobs.TIME_TO_SHOW, dateTopass[1]);
				}

			} else if (st == KEYS.CurrentJobs.JOB__SUSPENDED) {

				dateToshow = dataAceesObject
						.getJobSuspendedTime(interv.getId());
				if (!TextUtils.isEmpty(dateToshow)) {

					String[] dateTopass = dateToshow.split("/");

					map.put(KEYS.CurrentJobs.DATE_TO_SHOW, dateTopass[0]);

					map.put(KEYS.CurrentJobs.TIME_TO_SHOW, dateTopass[1]);
				}

			} else {

				map.put(KEYS.CurrentJobs.DATE_TO_SHOW, dateFormat.format(date)
						+ "");

				map.put(KEYS.CurrentJobs.TIME_TO_SHOW, format.format(date));
			}

			if (dedlineExceededList.containsKey(map
					.get(KEYS.CurrentJobs.ALL_JOB_HEADER))) {
				dedlineExceededList.get(
						map.get(KEYS.CurrentJobs.ALL_JOB_HEADER)).add(map);

			} else {
				dedlineExceededList.put(
						map.get(KEYS.CurrentJobs.ALL_JOB_HEADER),
						new ArrayList<HashMap<String, String>>());
				dedlineExceededList.get(
						map.get(KEYS.CurrentJobs.ALL_JOB_HEADER)).add(map);
			}
		}

	}

	/**
	 * Set the list adapter to deadlineExccedeListView.
	 */
	private void setDeadlineJobsListAdapter() {
    Logger.log("Deadline Excceded helper ", " setDeadlineJobsListAdapter");
		if (deadlineJobsListAdapter == null) {
			deadlineJobsListAdapter = new DeadlineJobsListAdapter(
					syncroTeamBaseActivity, dedlineExceededList, baseFragment);
			deadlineExceddedListView.setAdapter(deadlineJobsListAdapter);
		} else {
			deadlineJobsListAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * Gets the date from db format.
	 * 
	 * @param mDate
	 *            the m date
	 * @return the date from db format
	 */
	public Date getDateFromDbFormat(String mDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date date;
		try {
			date = sdf.parse(mDate);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
			return new Date();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.synchroteam.fragmenthelper.HelperInterface#onReturnToActivity(int)
	 */
	@Override
	public void onReturnToActivity(int requestCode) {
		// TODO Auto-generated method stub

	}

	/**
	 * Update database.
	 */
	public void updateDatabase() {
		// TODO Auto-generated method stub
		dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
		format = android.text.format.DateFormat
				.getTimeFormat(syncroTeamBaseActivity);
		new FetchDeadlineExceededJobs().execute();

		baseFragment.listUpdate();

	}

	/***
	 * Async task to fetch the deadline exceeded jobs.
	 * 
	 * @author Ideavate.solutions
	 * 
	 */

	private class FetchDeadlineExceededJobs extends AsyncTaskCoroutine<Void, Void> {

		@Override
		public void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			deadlineExceddedListView.setVisibility(View.GONE);
			progressBarDeadlineJob.setVisibility(View.VISIBLE);

		}

		@Override
		public Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			createAdapterAndInflateDataInListView();

			return null;
		}

		@Override
		public void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			
			super.onPostExecute(result);
			progressBarDeadlineJob.setVisibility(View.GONE);
			deadlineExceddedListView.setVisibility(View.VISIBLE);
			setDeadlineJobsListAdapter();
			EventBus.getDefault().post(new EnableSynchronizationAddJobEvent());
			

		}

	}

}
