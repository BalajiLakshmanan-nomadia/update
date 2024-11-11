package com.synchroteam.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synchroteam.beans.UpdateJobDetailUi;
import com.synchroteam.beans.UpdateUiAfterSync;
import com.synchroteam.fragmenthelper.DiscriptionJobPoolDetailFragmentHelper;
import com.synchroteam.synchroteam.JobPoolDetails;
import com.synchroteam.utils.KEYS;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc

/**
 * The Fragment Class to Show DiscrptionJobDetail Fragments.
 * 
 * @author ${Ideavate Solution}
 */
public class DiscrptionJobPoolFragment extends Fragment {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */

	/** The discription job detail fragment helper. */
	DiscriptionJobPoolDetailFragmentHelper discriptionJobDetailFragmentHelper;


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Bundle bundle = getArguments();

		discriptionJobDetailFragmentHelper = new DiscriptionJobPoolDetailFragmentHelper(
				(JobPoolDetails) getActivity(),
				bundle.getString(KEYS.JObDetail.ID),
				bundle.getString(KEYS.JObDetail.NOMSITE),
				bundle.getString(KEYS.JObDetail.NOMEQUIPMENT),
				bundle.getInt(KEYS.JObDetail.IDSITE),
				bundle.getString(KEYS.JObDetail.LAT),
				bundle.getString(KEYS.JObDetail.LON),
				bundle.getString(KEYS.JObDetail.ADR_GLOBAL),
				bundle.getString(KEYS.JObDetail.DATEMEETING),
				bundle.getString(KEYS.JObDetail.ID_USER),
				bundle.getInt(KEYS.JObDetail.IDCLIENT),
				bundle.getInt(KEYS.JObDetail.IDEQUIPMENT),
				bundle.getInt(KEYS.JObDetail.CD_STATUS),
                bundle.getString(KEYS.CurrentJobs.DATE_PREF),
                bundle.getString(KEYS.CurrentJobs.ID_JOB_WINDOW));

		EventBus.getDefault().register(this);

		return discriptionJobDetailFragmentHelper.inflateLayout(inflater,
				container);

	}

	/**
	 * On event.
	 *
	 * @param updateJobDetailUi
	 *            the update job detail ui
	 */
	public void onEvent(UpdateJobDetailUi updateJobDetailUi) {

		discriptionJobDetailFragmentHelper.updateUi();

	}

	/**
	 * On event.
	 *
	 * @param updateUiAfterSync
	 *            the update ui after sync
	 */
	public void onEvent(UpdateUiAfterSync updateUiAfterSync) {

		discriptionJobDetailFragmentHelper.updateUiAfterSync();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		EventBus.getDefault().unregister(this);
		super.onDestroy();

	}

}
