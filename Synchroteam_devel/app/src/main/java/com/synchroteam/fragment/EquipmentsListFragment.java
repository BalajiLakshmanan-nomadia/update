package com.synchroteam.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synchroteam.beans.UpdateUiOnSync;
import com.synchroteam.fragmenthelper.EquipmentsListFragmentHelperNew;
import com.synchroteam.synchroteam.SyncoteamNavigationActivity;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc
/**
 * The Fragment Class to Show DiscrptionJobDetail Fragments.
 * 
 * @author ${Ideavate Solution}
 */
public class EquipmentsListFragment extends Fragment {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */

	/** The discription job detail fragment helper. */
	EquipmentsListFragmentHelperNew clientSectionFragmentHelper;

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

		clientSectionFragmentHelper = new EquipmentsListFragmentHelperNew(
				(SyncoteamNavigationActivity) getActivity());
		EventBus.getDefault().register(this);

		return clientSectionFragmentHelper.inflateLayout(inflater, container);

	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		EventBus.getDefault().unregister(this);
		super.onDestroyView();
	}
	
	/**
	 * **
	 * Function is called UpdateUiOnSync Event is fired through Event bus this is normally called when user is some other module and perform sync operation
	 * than this event is fired by that module through Event bus to update the Equipment  listing.
	 *
	 * @param updateUiOnSync the update ui on sync
	 */
	
	public void  onEvent(UpdateUiOnSync updateUiOnSync){
		clientSectionFragmentHelper.doOnSyncronize();
		

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

}
