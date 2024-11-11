package com.synchroteam.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synchroteam.beans.UpdateUiOnSync;
import com.synchroteam.fragmenthelper.LegalInformationFragmentHelper;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc
/***
 *  The Fragment Class to Show Current Legal Information Fragments.
 * @author ${Ideavate Solution}
 *
 */
public class LegalInformationFragment extends BaseFragment {
	
	/** The reports fragment helper. */
	LegalInformationFragmentHelper legalInformationFragmentHelper;
	
	/** The list update listner. */
	private ListUpdateListener mListUpdateListener;

/* (non-Javadoc)
 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
 */
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	legalInformationFragmentHelper=new LegalInformationFragmentHelper(getSyncroTeamBaseActivity(),this);
	return legalInformationFragmentHelper.inflateLayout(inflater, container);
}

/* (non-Javadoc)
 * @see com.synchroteam.fragment.FragmentInterface#doOnBackPressed()
 */
@Override
public boolean doOnBackPressed() {
	// TODO Auto-generated method stub
	return false;
}

/* (non-Javadoc)
 * @see com.synchroteam.fragment.FragmentInterface#getFragmentTag()
 */
@Override
public String getFragmentTag() {
	// TODO Auto-generated method stub
	return this.getTag();
}

/* (non-Javadoc)
 * @see com.synchroteam.fragment.FragmentInterface#doOnSync()
 */
@Override
public void doOnSync() {
	// TODO Auto-generated method stub
EventBus.getDefault().post(new UpdateUiOnSync());
}

/* (non-Javadoc)
 * @see com.synchroteam.fragment.FragmentInterface#listUpdate()
 */
@Override
public void listUpdate() {
	// TODO Auto-generated method stub
	mListUpdateListener.onListUpdate();
}

/* (non-Javadoc)
 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
 */
@Override
public void onAttach(Activity activity) {
	// TODO Auto-generated method stub
	super.onAttach(activity);
	mListUpdateListener =(ListUpdateListener)activity;
}


}


