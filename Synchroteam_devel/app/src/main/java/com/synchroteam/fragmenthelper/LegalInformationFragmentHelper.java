package com.synchroteam.fragmenthelper;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.synchroteam.baseactivity.SyncroTeamBaseActivity;
import com.synchroteam.beans.EnableSynchronizationAddJobEvent;
import com.synchroteam.fragment.BaseFragment;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DoubleClickListener;
import com.synchroteam.utils.SharedPref;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc
/***
 *
 * This class is responsible to handle all the functionality of Calendar screen.
 * 1.To inflate layout of custom calendar 2.To control the navigation inside
 * calendar 3. To Show Status of Job on Calendar. created for future purpose
 *
 * @author Ideavate.solution
 *
 */
public class LegalInformationFragmentHelper implements HelperInterface {

	/** The syncro team base activity. */
	SyncroTeamBaseActivity syncroTeamBaseActivity;

	// private static final String TAG = "LegalInformationFragmentHelper";

	/**
	 * Instantiates a new calendar fragment helper.
	 *
	 * @param syncroTeamBaseActivity
	 *            the syncro team base activity
	 * @param baseFragment
	 *            the base fragment
	 */
	public LegalInformationFragmentHelper(
			SyncroTeamBaseActivity syncroTeamBaseActivity,
			BaseFragment baseFragment) {
		// TODO Auto-generated constructor stub

		this.syncroTeamBaseActivity = syncroTeamBaseActivity;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.synchroteam.fragmenthelper.HelperInterface#inflateLayout(android.
	 * view.LayoutInflater, android.view.ViewGroup)
	 */
	@Override
	public View inflateLayout(LayoutInflater inflater, ViewGroup container) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.layout_legalinformation,
				container, false);

		// view.setClickable(false);
		/*
		 * new change. when double click made on the screen, debug mode gets
		 * enable for synchronization. Hence a value stored to shared preference
		 * for debugging.
		 */
		view.setOnClickListener(new DoubleClickListener() {

			@Override
			public void onSingleClick(View v) {
				// single click
			}

			@Override
			public void onDoubleClick(View v) {
				savePreferences();
			}
		});
		initiateView(view);
		EventBus.getDefault().post(new EnableSynchronizationAddJobEvent());
		return view;

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.synchroteam.fragmenthelper.HelperInterface#initiateView(android.view
	 * .View)
	 */
	@Override
	public void initiateView(View v) {

		TextView textVersionNameLabelTv = (TextView) v
				.findViewById(R.id.textVersionNameLabelTv);

		PackageInfo pinfo = null;
		try {
			pinfo = syncroTeamBaseActivity.getPackageManager().getPackageInfo(
					syncroTeamBaseActivity.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String versionName = pinfo.versionName;
		textVersionNameLabelTv.setText(syncroTeamBaseActivity
				.getString(R.string.txt_version)
				+" Android V"
				+ "  "
				+ versionName);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.synchroteam.fragmenthelper.HelperInterface#doOnSyncronize()
	 */
	@Override
	public void doOnSyncronize() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.synchroteam.fragmenthelper.HelperInterface#onReturnToActivity(int)
	 */
	@Override
	public void onReturnToActivity(int requestCode) {
		// TODO Auto-generated method stub

	}

	/**
	 * Save preferences.
	 *
	 */
	// private void savePreferences() {
	// // We need an Editor object to make preference changes.
	// SharedPreferences settings = PreferenceManager
	// .getDefaultSharedPreferences(syncroTeamBaseActivity);
	// SharedPreferences.Editor editor = settings.edit();
	//
	// String isDebugEnabled = settings.getString(KEYS.DEBUG_TAG, "");
	//
	// if (TextUtils.isEmpty(isDebugEnabled)
	// || isDebugEnabled.equalsIgnoreCase("false")) {
	// editor.putString(KEYS.DEBUG_TAG, "true");
	// Toast.makeText(
	// syncroTeamBaseActivity,
	// syncroTeamBaseActivity
	// .getString(R.string.txt_debug_enabled),
	// Toast.LENGTH_SHORT).show();
	// } else if (isDebugEnabled.equalsIgnoreCase("true")) {
	// editor.putString(KEYS.DEBUG_TAG, "false");
	// Toast.makeText(
	// syncroTeamBaseActivity,
	// syncroTeamBaseActivity
	// .getString(R.string.txt_debug_disabled),
	// Toast.LENGTH_SHORT).show();
	// }
	// // Commit the edits!
	// editor.commit();
	//
	// }

	/**
	 * Save preferences.
	 *
	 */
	private void savePreferences() {
		boolean isDebugEnabled = SharedPref
				.getDebugEnabled(syncroTeamBaseActivity);

		if (!isDebugEnabled) {
			SharedPref.setDebugEnableSync(true, syncroTeamBaseActivity);
			Toast.makeText(
					syncroTeamBaseActivity,
					syncroTeamBaseActivity
							.getString(R.string.txt_debug_enabled),
					Toast.LENGTH_SHORT).show();
		} else {
			SharedPref.setDebugEnableSync(false, syncroTeamBaseActivity);
			Toast.makeText(
					syncroTeamBaseActivity,
					syncroTeamBaseActivity
							.getString(R.string.txt_debug_disabled),
					Toast.LENGTH_SHORT).show();
		}

	}
}
