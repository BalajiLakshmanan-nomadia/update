package com.synchroteam.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synchroteam.beans.UpdateUiAfterSync;
import com.synchroteam.fragmenthelper.JobAttachmentsListItemFragmentHelper;
import com.synchroteam.utils.KEYS;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc

/**
 * The Fragment Class to Show DiscrptionJobDetail Fragments.
 * 
 * @author ${Ideavate Solution}
 */
public class JobPoolAttachmentListFragment extends BaseFragment {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */

	/** The discription job detail fragment helper. */
	private JobAttachmentsListItemFragmentHelper attachmentsListItemFragmentHelper;

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

		// it is coming from JobDetail page
		attachmentsListItemFragmentHelper = new JobAttachmentsListItemFragmentHelper(
				getActivity(), bundle.getString(KEYS.CurrentJobs.ID),
				bundle.getInt(KEYS.CurrentJobs.CD_STATUS),
				bundle.getInt(KEYS.CurrentJobs.IDCLIENT),
				bundle.getInt(KEYS.CurrentJobs.IDSITE),
				bundle.getInt(KEYS.CurrentJobs.IDEQUIPMENT));

		EventBus.getDefault().register(this);
		return attachmentsListItemFragmentHelper.inflateLayout(inflater,
				container);

	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		EventBus.getDefault().unregister(this);
		super.onDestroyView();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onDestroy()
	 */
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}

	/**
	 * ** Function is called UpdateUiOnSync Event is fired through Event bus
	 * this is normally called when user is some other module and perform sync
	 * operation than this event is fired by that module through Event bus to
	 * update the current client listing.
	 * 
	 * @param updateUiOnSync
	 *            the update ui on sync
	 */

	public void onEvent(UpdateUiAfterSync updateUiOnSync) {
		attachmentsListItemFragmentHelper.doOnSyncronize();

	}

	@Override
	public boolean doOnBackPressed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getFragmentTag() {
		// TODO Auto-generated method stub
		return this.getTag();
	}

	@Override
	public void doOnSync() {
		// TODO Auto-generated method stub

	}

	@Override
	public void listUpdate() {
		// TODO Auto-generated method stub

	}

}
