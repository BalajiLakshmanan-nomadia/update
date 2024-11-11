package com.synchroteam.baseactivity;

import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.synchroteam.beans.SelectTodayDate;
import com.synchroteam.fragment.BaseFragment;
import com.synchroteam.fragment.FragmentInterface;
import com.synchroteam.synchroteam.CommonInterface;
import com.synchroteam.synchroteam.SyncroTeamApplication;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.Logger;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc

/**
 * Base Activity of Activity That manages that Navigation Through Navigation
 * Drawer.
 *
 * @author Ambesh.Kukreja
 */
public abstract class SyncroTeamBaseActivity extends FragmentActivity implements
        CommonInterface, BaseFragment.ListUpdateListener {

	/** The log tag. */
	private String LOG_TAG = "SyncroTeamBaseActivity";

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_syncroteambaseactivity);


	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		boolean isBackPressedHandled = false;
		String fragmentTag = null;
		try {
			FragmentInterface fragment = getCurrentFragment();
			if (fragment != null) {
				isBackPressedHandled = fragment.doOnBackPressed();
				fragmentTag = fragment.getFragmentTag();
			} else {
				fragmentTag = "CurrentJobFragment";
			}

		} catch (Exception e) {
			Logger.printException(e); //
		}

		if (!isBackPressedHandled) {
			quitFromAppWhenAllFragmentsRemoved(fragmentTag);
		}
	}

	/**
	 * This function checks removes all the fragments from the app and quit from
	 * application if user is on Current Job Fragment.
	 * 
	 * @param fragmentTag
	 *            the fragment tag
	 */
	private void quitFromAppWhenAllFragmentsRemoved(String fragmentTag) {
		FragmentManager localFragmentManager = getSupportFragmentManager();
		int i = localFragmentManager.getBackStackEntryCount();

		/***********************/

		/***********************/

		Logger.log(LOG_TAG + " Fragment Tag", fragmentTag + "");
		if (i <= 1) {
			if (fragmentTag.equals("CurrentJobFragment")) {
				finish();
			} else {

				EventBus.getDefault().post(new SelectTodayDate());
				removeAllFragments();

			}

			return;
		}

	}

	/**
	 * Removes the all fragments.
	 */
	protected void removeAllFragments() {
		// TODO Auto-generated method stub

		try{

			int fragmentCount = getSupportFragmentManager()
					.getBackStackEntryCount();

			for (int index = 0; index < fragmentCount; index++) {
				getSupportFragmentManager().popBackStackImmediate();
			}
		}catch (Exception e){

		}

//		int fragmentCount = getSupportFragmentManager()
//				.getBackStackEntryCount();
//
//		for (int index = 0; index < fragmentCount; index++) {
//			getSupportFragmentManager().popBackStackImmediate();
//		}

	}

	/**
	 * * Get Active Fragment From fragment Stack.
	 * 
	 * @return Fragment
	 */
	public BaseFragment getCurrentFragment() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		int backEnrtyCount = fragmentManager.getBackStackEntryCount();
		Logger.log(LOG_TAG, fragmentManager.getBackStackEntryCount() + "");
		if (backEnrtyCount == 0) {
			return null;
		}
		String fragmentTag = fragmentManager.getBackStackEntryAt(
				backEnrtyCount - 1).getName();
		Fragment currentFragment = getSupportFragmentManager()
				.findFragmentByTag(fragmentTag);
		return (BaseFragment) currentFragment;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	protected void onResume() {
		super.onResume();
		((SyncroTeamApplication) getApplicationContext())
				.setSyncroteamAppLive(true);
		((SyncroTeamApplication) getApplicationContext())
				.setSyncroteamActivityInstance(this);

	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		((SyncroTeamApplication) getApplicationContext())
				.setSyncroteamAppLive(false);
		((SyncroTeamApplication) getApplicationContext())
				.setSyncroteamActivityInstance(null);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synchroteam.synchroteam.CommonInterface#getActivityInstance()
	 */
	@Override
	public Activity getActivityInstance() {
		// TODO Auto-generated method stub
		return this;
	}

}
