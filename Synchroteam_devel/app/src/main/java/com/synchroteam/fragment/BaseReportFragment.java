package com.synchroteam.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synchroteam.fragmenthelper.BaseReportFragmentHelper;

// TODO: Auto-generated Javadoc
/**
 * The Fragment Class to Show Reports Fragments.
 * @author ${Ideavate Solution}
 */
public class BaseReportFragment extends BaseFragment {

	/** The base report fragment helper. */
	private BaseReportFragmentHelper baseReportFragmentHelper;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		baseReportFragmentHelper=new BaseReportFragmentHelper(this,getSyncroTeamBaseActivity());
		
		return baseReportFragmentHelper.inflateLayout(inflater, container);
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
		baseReportFragmentHelper.doOnSyncronize();
	}


	/* (non-Javadoc)
	 * @see com.synchroteam.fragment.FragmentInterface#listUpdate()
	 */
	@Override
	public void listUpdate() {
		// TODO Auto-generated method stub
		
	}






	
}
