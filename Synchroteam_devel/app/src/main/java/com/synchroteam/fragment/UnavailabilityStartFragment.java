package com.synchroteam.fragment;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.Conge;
import com.synchroteam.beans.UnavailabilityBeans;
import com.synchroteam.beans.UpdateDataBaseEvent;
import com.synchroteam.dao.Dao;
import com.synchroteam.listadapters.IssuesListAdapterAddUnavailabilityRc;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DialogUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Vector;

import de.greenrobot.event.EventBus;


@SuppressLint("NewApi")
public class UnavailabilityStartFragment extends Fragment implements
		OnClickListener {

	// .............................UI...ELEMENTS...STARTS...HERE................................................

	private RecyclerView unavailabilityListView;
	private EditText edtIssueDescription;
	private TextView txtCreate;

	private ArrayList<UnavailabilityBeans> unavailabilitiesList;

	private Dao dataAccessObject;

	// private SharedPreferences issuesPositionPref;
	private String pos;
	private String notesDescription;
	SimpleDateFormat sdf;
	private Calendar cal;

	private String idInterv, idUser;

	// private static final String TAG = "UnavailabilityStartFragment";

	// ...............................LIFECYCLE...METHOD...STARTS...HERE.........................................
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_create_unavailability,
				container, false);
		initializeUI(view);
		return view;
	}

	// *********************************LIFECYCLE...METHOD...ENDS...HERE*******************************************

	// ...................................LOCAL...METHOD...STARTS...HERE.............................................
	private void initializeUI(View view) {
		unavailabilityListView = (RecyclerView) view
				.findViewById(R.id.unavailabilityListView);
		LinearLayoutManager llm=new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL,false);
		unavailabilityListView.setLayoutManager(llm);
		edtIssueDescription = (EditText) view
				.findViewById(R.id.edtIssueDescription);
		txtCreate = (TextView) view.findViewById(R.id.txtCreate);
		dataAccessObject = DaoManager.getInstance();

		unavailabilitiesList = new ArrayList<UnavailabilityBeans>();
		unavailabilitiesList = (ArrayList<UnavailabilityBeans>) dataAccessObject
				.getUnavailabilities();

		// issuesPositionPref =
		// getActivity().getSharedPreferences("issues_pref",
		// Context.MODE_PRIVATE);

		cal = Calendar.getInstance();

		idInterv = dataAccessObject.getStartedJobId();
		idUser = " ";

		unavailabilityListView
				.setAdapter(new IssuesListAdapterAddUnavailabilityRc(
						getActivity(), unavailabilitiesList, true));
		txtCreate.setOnClickListener(this);

	}

	/**
	 * Check for the unavailability which does not have an end date (i.e already
	 * started) and update the end date of that unavailability to current date.
	 */
	private void updateEndDateOfPreviousActivity() {

		Vector<Conge> vectConge = new Vector<Conge>();
		vectConge = dataAccessObject.getConge();
		Enumeration<Conge> enindisp = vectConge.elements();
		String currentDate = sdf.format(cal.getTime());
		while (enindisp.hasMoreElements()) {
			Conge indisp = enindisp.nextElement();
			if (indisp.getDtFin() == null) {
				dataAccessObject.updateUnavailabilityEndDate(
						indisp.getIdConge(), currentDate);
			}
		}
	}

	public void setIssuePosition(int position) {
		pos = String.valueOf(position);
	}

	// **********************************LOCAL...METHOD...ENDS...HERE****************************************************

	// ...................................LISTENER...STARTS...HERE........................................................
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.txtCreate:
			// pos = issuesPositionPref.getString("pos_for_start_fragment",
			// null);

			notesDescription = edtIssueDescription.getText().toString();

			if (pos == null) {
				Toast.makeText(getActivity(),
						getActivity().getString(R.string.select_type),
						Toast.LENGTH_SHORT).show();
			} else {
				new addUnavailabilityInDBAsync().execute();
			}
			break;

		}
	}

	// ...................................LISTENER...ENDS...HERE........................................................

	// ......................................ASYNC...CLASS...STARTS...HERE..............................................

	/**
	 * The Class SaveNewJobDataAsyncTask.
	 */
	private class addUnavailabilityInDBAsync extends
			AsyncTaskCoroutine<String, Boolean> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		public void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			DialogUtils.showProgressDialog(getActivity(),
					getString(R.string.textWaitLable),
					getString(R.string.textSavingAddJobData), false);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@SuppressLint("SimpleDateFormat")
		@Override
		public Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			String currentDateStr = sdf.format(cal.getTime());

			updateEndDateOfPreviousActivity();

			boolean drp = dataAccessObject.addUnavailability(null,
					unavailabilitiesList.get(Integer.parseInt(pos))
							.getUnavailabilityID(), 0, currentDateStr, null,
					notesDescription);
			return drp;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		public void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			DialogUtils.dismissProgressDialog();

			boolean drp = result.booleanValue();
			if (drp) {
				Toast.makeText(getActivity(),
						getActivity().getString(R.string.unavailability_added),
						Toast.LENGTH_SHORT).show();

				if (dataAccessObject.updateStatutInterv(4, idInterv))
					dataAccessObject.setTimeClotIntervention(idInterv, idUser
							+ "","");

				EventBus.getDefault().post(new UpdateDataBaseEvent());
				getActivity().finish();
			} else
				Toast.makeText(getActivity(), R.string.msg_error,
						Toast.LENGTH_SHORT).show();
		}

	}

	// *************************************************ASYNC...CLASS...ENDS...HERE****************************************
}
