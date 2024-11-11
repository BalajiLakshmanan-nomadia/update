package com.synchroteam.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synchroteam.beans.UpdateUiOnSync;
import com.synchroteam.fragmenthelper.ClientSectionFragmentHelper;
import com.synchroteam.synchroteam.SyncoteamNavigationActivity;
import com.synchroteam.synchroteam3.R;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc
/**
 * The Fragment Class to Show ClientSectionFragment Fragments.
 * 
 * @author ${Ideavate Solution}
 */
public class ClientSectionFragment extends BaseFragment {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */

	
	private ClientSectionFragmentHelper clientSectionFragmentHelper;
	private ListUpdateListener mListUpdateListener;
	public static final int MY_PERMISSIONS_REQUEST_LOCATION = 124;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		clientSectionFragmentHelper = new ClientSectionFragmentHelper(
				(SyncoteamNavigationActivity) getActivity(), this);
		
		clientSectionFragmentHelper.intialiseLocationClient();
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
		clientSectionFragmentHelper.connectToLocationClient();
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		
clientSectionFragmentHelper.disconnectToLocationClient();
		super.onStop();
		
	}
	
	
	
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

	
		

		return clientSectionFragmentHelper.inflateLayout(inflater, container);

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

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
	 */
	@Override
		public void onAttach(Activity activity) {
			// TODO Auto-generated method stub
		
			super.onAttach(activity);
			mListUpdateListener =(ListUpdateListener)activity;
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
		listUpdate();
		EventBus.getDefault().post(new UpdateUiOnSync());
		
	}


	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_LOCATION: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    clientSectionFragmentHelper.callingLocationFunctionalities();
				} else {
					// permission denied, boo! Disable the
					// functionality that depends on this permission.
				}
				return;
			}
			// other 'case' lines to check for other
			// permissions this app might request.
		}
	}

	public void callingPermissionLocation() {

		if (ActivityCompat.shouldShowRequestPermissionRationale(getSyncroTeamBaseActivity(),
				Manifest.permission.ACCESS_COARSE_LOCATION)) {
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getSyncroTeamBaseActivity());
			alertBuilder.setCancelable(true);
			alertBuilder.setTitle(getString(R.string.app_name));
			alertBuilder.setMessage(getString(R.string.str_location_permission));
			alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
				public void onClick(DialogInterface dialog, int which) {
					ActivityCompat.requestPermissions(getSyncroTeamBaseActivity(),
							new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
									Manifest.permission.ACCESS_COARSE_LOCATION},
							MY_PERMISSIONS_REQUEST_LOCATION);
				}
			});
			AlertDialog alert = alertBuilder.create();
			alert.show();
		} else {
			// No explanation needed; request the permission
			ActivityCompat.requestPermissions(getSyncroTeamBaseActivity(),
					new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
							Manifest.permission.ACCESS_COARSE_LOCATION},
					MY_PERMISSIONS_REQUEST_LOCATION);
		}
	}
	

	
	
	
	
	@Override
	public void listUpdate() {
		// TODO Auto-generated method stub
	
		
		mListUpdateListener.onListUpdate();
	}

}
