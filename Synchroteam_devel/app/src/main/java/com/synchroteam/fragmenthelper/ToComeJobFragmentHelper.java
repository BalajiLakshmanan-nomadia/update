package com.synchroteam.fragmenthelper;

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
import com.synchroteam.dao.Dao;
import com.synchroteam.fragment.BaseFragment;
import com.synchroteam.listadapters.ToComeJobsListAdapter;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.KEYS;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc

/***
 * This class is Responsible for inflating and Performing Actions on To Come
 * Jobs fragment.
 * 
 * created for future purpose
 * 
 * @author Ideavate.solution
 * 
 */
@SuppressLint("SimpleDateFormat")
public class ToComeJobFragmentHelper implements HelperInterface {

	/** The syncro team base activity. */
	SyncroTeamBaseActivity syncroTeamBaseActivity;

	/** The to come job list view. */
	ListView toComeJobListView;

	/** The progress d synch. */
	protected ProgressDialog progressDSynch;

	/** The data acees object. */
	private Dao dataAceesObject;

	/** The to come job list. */
	private TreeMap<String, ArrayList<HashMap<String, String>>> toComeJobList;

	/** The to come jobs list adapter. */
	private ToComeJobsListAdapter toComeJobsListAdapter;

	/** The calendar for jobs. */

	/** The full months. */
	private FullMonths[] fullMonths;

	/** The date format. */
	private DateFormat dateFormat;

	/** The format. */
	private Format format;

	/** The base fragment. */
	private BaseFragment baseFragment;

	private ProgressBar progressBarToComeJob;

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

	/** The months of year. */
	MonthsOfYear[] monthsOfYear;

	/**
	 * Instantiates a new to come fragment helper.
	 * 
	 * @param syncroTeamBaseActivity
	 *            the syncro team base activity
	 * @param baseFragment
	 *            the base fragment
	 */
	public ToComeJobFragmentHelper(
			SyncroTeamBaseActivity syncroTeamBaseActivity,
			BaseFragment baseFragment) {
		// TODO Auto-generated constructor stub

		this.syncroTeamBaseActivity = syncroTeamBaseActivity;
		dataAceesObject = DaoManager.getInstance();

		monthsOfYear = MonthsOfYear.values();
		fullMonths = FullMonths.values();
		this.baseFragment = baseFragment;
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
		View view = inflater.inflate(R.layout.layout_tocome, container, false);
		initiateView(view);
		dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
		progressBarToComeJob = (ProgressBar) view
				.findViewById(R.id.progressBarToComeJob);
		format = android.text.format.DateFormat
				.getTimeFormat(syncroTeamBaseActivity);
		new FetchToComeJobFromDb().execute();
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
		toComeJobListView = (ListView) v.findViewById(R.id.toComeJobLv);

	}

	/** The item click listener. */

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synchroteam.fragmenthelper.HelperInterface#doOnSyncronize()
	 */
	@Override
	public void doOnSyncronize() {
		// TODO Auto-generated method stub
		new FetchToComeJobFromDb().execute();

	}

	/**
	 * Creates the adapter and inflate data in list view.
	 */
	private void createAdapterAndInflateDataInListView() {
		if (toComeJobList != null) {
			toComeJobList.clear();
		} else {
			toComeJobList = new TreeMap<String, ArrayList<HashMap<String, String>>>(
					new Comparator<String>() {

						@Override
						public int compare(String lhs, String rhs) {
							// TODO Auto-generated method stub
							return -lhs.compareTo(rhs);
						}
					});
		}

		HashMap<String, String> map;

		Vector<CommonJobBean> vect = new Vector<CommonJobBean>();

		vect = dataAceesObject.getAllInterventionNext();

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

			if (toComeJobList.containsKey(map
					.get(KEYS.CurrentJobs.ALL_JOB_HEADER))) {
				toComeJobList.get(map.get(KEYS.CurrentJobs.ALL_JOB_HEADER))
						.add(map);

			} else {
				toComeJobList.put(map.get(KEYS.CurrentJobs.ALL_JOB_HEADER),
						new ArrayList<HashMap<String, String>>());
				toComeJobList.get(map.get(KEYS.CurrentJobs.ALL_JOB_HEADER))
						.add(map);
			}
		}

		/*
		 * SimpleAdapter adapter = new SimpleAdapter (this.getBaseContext(),
		 * listItem, R.layout.list_item_interv, new String[] {"img1", "type",
		 * "cltVille","plan", "img2"}, new int[] {R.id.img1, R.id.text1 ,
		 * R.id.text2 , R.id.text3 ,R.id.img2});
		 */

	}

	/**
	 * Set the to Come list adapter to toCome list view.
	 */
	private void setToComeListAdapter() {

		if (toComeJobsListAdapter == null) {
			toComeJobsListAdapter = new ToComeJobsListAdapter(
					syncroTeamBaseActivity, toComeJobList, baseFragment);
			toComeJobListView.setAdapter(toComeJobsListAdapter);
		} else {
			toComeJobsListAdapter.notifyDataSetChanged();
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

	/**
	 * Update database.
	 */
	public void updateDatabase() {
		// TODO Auto-generated method stub

		new FetchToComeJobFromDb().execute();

		baseFragment.listUpdate();

	}

	/**
	 * 
	 * Fetches the up comming jobs from DB.
	 * 
	 * @author Ideavate.Solution
	 * 
	 */

	private class FetchToComeJobFromDb extends AsyncTaskCoroutine<Void, Void> {

		@Override
		public void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			toComeJobListView.setVisibility(View.GONE);
			progressBarToComeJob.setVisibility(View.VISIBLE);

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
			progressBarToComeJob.setVisibility(View.GONE);
			toComeJobListView.setVisibility(View.VISIBLE);
			setToComeListAdapter();
			EventBus.getDefault().post(new EnableSynchronizationAddJobEvent());

		}

	}

	@Override
	public void onReturnToActivity(int requestCode) {
		// TODO Auto-generated method stub

	}

}
