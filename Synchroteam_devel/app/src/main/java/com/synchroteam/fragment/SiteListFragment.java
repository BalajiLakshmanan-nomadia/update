package com.synchroteam.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synchroteam.beans.UpdateUiOnSync;
import com.synchroteam.fragmenthelper.SiteListFragmentHelper;
import com.synchroteam.fragmenthelper.SiteListFragmentHelperNew;
import com.synchroteam.synchroteam.SyncoteamNavigationActivity;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc
/**
 * The Fragment Class to Show SiteListFragment.
 * 
 * @author ${Ideavate Solution}
 */
public class SiteListFragment extends Fragment {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */

	/** The discription job detail fragment helper. */
	SiteListFragmentHelperNew siteListFragmentHelper;

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

		siteListFragmentHelper = new SiteListFragmentHelperNew(
				(SyncoteamNavigationActivity) getActivity());
		
		EventBus.getDefault().register(this);
		return siteListFragmentHelper.inflateLayout(inflater, container);

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
		EventBus.getDefault().unregister(this);
		super.onDestroy();

	}



	/**
	 * **
	 * Function is called UpdateUiOnSync Event is fired through Event bus this is normally called when user is some other module and perform sync operation
	 * than this event is fired by that module through Event bus to update the sites listing.
	 *
	 * @param updateUiOnSync the update ui on sync
	 */
	
	public void  onEvent(UpdateUiOnSync updateUiOnSync){
		siteListFragmentHelper.doOnSyncronize();
		

	}
	
	


}
