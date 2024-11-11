package com.synchroteam.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.synchroteam.beans.UpdateUiOnSync;
import com.synchroteam.fragmenthelper.CalendarFragmentHelper;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc
/***
 * The Fragment Class to Show Calander Fragments.
 * 
 * @author ${Ideavate Solution}
 * 
 */
public class CalendarFragment extends BaseFragment {

	/** The calendar fragment helper. */
	CalendarFragmentHelper calendarFragmentHelper;

	/** The list update listner. */
	private ListUpdateListener mListUpdateListener;

	RelativeLayout layoutTitleBar;

	RelativeLayout layBottomBar;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */

	public CalendarFragment(RelativeLayout layoutTitleBar,RelativeLayout layBottomBar ) {
		this.layoutTitleBar = layoutTitleBar;
		this.layBottomBar = layBottomBar;

	}

	public CalendarFragment( ) {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		calendarFragmentHelper = new CalendarFragmentHelper(
				getSyncroTeamBaseActivity(), this,layoutTitleBar,layBottomBar);
		return calendarFragmentHelper.inflateLayout(inflater, container);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		mListUpdateListener = (ListUpdateListener) activity;
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
		calendarFragmentHelper.doOnSyncronize();
		EventBus.getDefault().post(new UpdateUiOnSync());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synchroteam.fragment.FragmentInterface#listUpdate()
	 */
	@Override
	public void listUpdate() {
		// TODO Auto-generated method stub
		mListUpdateListener.onListUpdate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onResume()
	 */
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		calendarFragmentHelper.doOnResume();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onDestroyView()
	 */
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		calendarFragmentHelper.onDestroyView();
	}

}
