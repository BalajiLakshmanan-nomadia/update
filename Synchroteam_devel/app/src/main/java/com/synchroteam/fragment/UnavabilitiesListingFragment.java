package com.synchroteam.fragment;

import androidx.annotation.NonNull;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synchroteam.beans.UpdateDataBaseEvent;
import com.synchroteam.beans.UpdateUiOnSync;
import com.synchroteam.fragmenthelper.UnavabilitiesFragmentHelper;
import com.synchroteam.utils.Logger;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc
/**
 * The Class UnavabilitiesListingFragment.
 */
public class UnavabilitiesListingFragment extends BaseFragment {

	/** The current jobs fragment helper. */
	private UnavabilitiesFragmentHelper unavabilitiesFragmentHelper;

	/** The list update listner. */
	private ListUpdateListener mListUpdateListener;

	//private static final String TAG = "UnavabilitiesListingFragment";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Logger.log("onResume Unavbil ", "onCreateView");

		unavabilitiesFragmentHelper = new UnavabilitiesFragmentHelper(
				getSyncroTeamBaseActivity(), this);
		View view = unavabilitiesFragmentHelper.inflateLayout(inflater,
				container);
		;
		try {
			EventBus.getDefault().registerSticky(this);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.printException(e);
		}

		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synchroteam.fragment.FragmentInterface#doOnBackPressed()
	 */
	@Override
	public boolean doOnBackPressed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		unavabilitiesFragmentHelper.doOnResume();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synchroteam.fragment.FragmentInterface#getFragmentTag()
	 */
	@Override
	public String getFragmentTag() {
		// TODO Auto-generated method stub
		return this.getTag();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synchroteam.fragment.FragmentInterface#doOnSync()
	 */
	@Override
	public void doOnSync() {
		// TODO Auto-generated method stub
		unavabilitiesFragmentHelper.doOnSyncronize();
		EventBus.getDefault().post(new UpdateUiOnSync());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityResult(int, int,
	 * android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		unavabilitiesFragmentHelper.onReturnToActivity(requestCode);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(@androidx.annotation.NonNull Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mListUpdateListener = (ListUpdateListener) activity;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synchroteam.fragment.FragmentInterface#listUpdate()
	 */
	public void listUpdate() {

		mListUpdateListener.onListUpdate();
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

	/**
	 * Function is called when UpdateDataBaseEvent Event is fired through Event
	 * Bus this is normally called when user perform any operation which updates
	 * the database of current job listing. .
	 *
	 * @param updateDataBaseEvent
	 *            the update data base event
	 */
	public void onEvent(UpdateDataBaseEvent updateDataBaseEvent) {

		Logger.log("onResume Unavbil ", "onEvent");

		unavabilitiesFragmentHelper.refreshList();
	}

}
