package com.synchroteam.fragmenthelper;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synchroteam.baseactivity.SyncroTeamBaseActivity;
import com.synchroteam.synchroteam3.R;
// TODO: Auto-generated Javadoc
/***
 * This Class is responsible to handle all functionality of Reports Screen.
 * 1.To show Reports from Ultra lite Db.
 * 2.To filter data According to Tabs
 * created for future purpose
 * 
 * @author Ideavate.solution
 */
public class ReportsFragmentHelper implements HelperInterface {

/** The syncro team base activity. */
SyncroTeamBaseActivity syncroTeamBaseActivity;
	
	/**
	 * Instantiates a new reports fragment helper.
	 *
	 * @param syncroTeamBaseActivity the syncro team base activity
	 */
	public ReportsFragmentHelper(SyncroTeamBaseActivity syncroTeamBaseActivity) {
		// TODO Auto-generated constructor stub
	
	 this.syncroTeamBaseActivity=syncroTeamBaseActivity;
	}
	
	/* (non-Javadoc)
	 * @see com.synchroteam.fragmenthelper.HelperInterface#inflateLayout(android.view.LayoutInflater, android.view.ViewGroup)
	 */
	@Override
	public View inflateLayout(LayoutInflater inflater, ViewGroup container) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.layout_reports_details, container,
				false);
		initiateView(view);
		return view;
	}

	/* (non-Javadoc)
	 * @see com.synchroteam.fragmenthelper.HelperInterface#initiateView(android.view.View)
	 */
	@Override
	public void initiateView(View v) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.synchroteam.fragmenthelper.HelperInterface#doOnSyncronize()
	 */
	@Override
	public void doOnSyncronize() {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.synchroteam.fragmenthelper.HelperInterface#onReturnToActivity(int)
	 */
	@Override
	public void onReturnToActivity(int requestCode) {
		// TODO Auto-generated method stub
		
	}


}
