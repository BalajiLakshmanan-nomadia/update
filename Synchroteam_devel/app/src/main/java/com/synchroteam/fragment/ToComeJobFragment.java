package com.synchroteam.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synchroteam.beans.UpdateDataBaseEvent;
import com.synchroteam.beans.UpdateUiOnSync;
import com.synchroteam.dialogs.AllJobsSortingDialog;
import com.synchroteam.fragmenthelper.ToComeJobFragmentHelperNew;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.RequestCode;

import de.greenrobot.event.EventBus;
// TODO: Auto-generated Javadoc
/**
 * This class is responsible to show to come fragment.
 * @author Ambesh.Kukreja
 *
 */
public class ToComeJobFragment extends BaseFragment implements AllJobsSortingDialog.SortingListener{

	/** The to come fragment helper. */
	ToComeJobFragmentHelperNew toComeFragmentHelper;
	
	/** The list update listner. */
	private ListUpdateListener mListUpdateListener;

	public static final int MY_PERMISSIONS_REQUEST_LOCATION = 124;
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		toComeFragmentHelper=new ToComeJobFragmentHelperNew(getSyncroTeamBaseActivity(),this,this);
		View view=toComeFragmentHelper.inflateLayout(inflater, container);
		try{
			EventBus.getDefault().registerSticky(this);
			}
			catch(Exception e){
				Logger.printException(e);
			}
		return view;
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
		toComeFragmentHelper.doOnSyncronize();
		EventBus.getDefault().post(new UpdateUiOnSync());
	}
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == RequestCode.REQUEST_CODE_TEXT_BARCODE) {
			Logger.output("FRAG","activity result 1");
			if (resultCode == Activity.RESULT_OK) {
				Logger.output("FRAG","activity result 1");
				toComeFragmentHelper.onReturnToFragment(requestCode,resultCode, data);
			}
		}
		else if (requestCode == RequestCode.REQUEST_CODE_GPS_SETTINGS){
			toComeFragmentHelper.onReturnToFragment(requestCode,resultCode,null);
		}
	

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



/* (non-Javadoc)
 * @see com.synchroteam.fragment.FragmentInterface#listUpdate()
 */
public void listUpdate(){
	
	mListUpdateListener.onListUpdate();
}

/**
 * On event.
 *
 * @param updateDataBaseEvent the update data base event
 */
public void  onEvent(UpdateDataBaseEvent updateDataBaseEvent){
	Logger.log("toComeFragmentHelper", "OnEvent Called");
	toComeFragmentHelper.updateDatabase();
}





/* (non-Javadoc)
 * @see android.support.v4.app.Fragment#onDestroy()
 */
@Override
public void onDestroy() {
	// TODO Auto-generated method stub
	EventBus.getDefault().unregister(this);	
	super.onDestroy();
}

	@Override
	public void onSortingChose(int options, String optionName, String jobNumber) {
		toComeFragmentHelper.sortJobList(options,optionName,jobNumber);

	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_LOCATION: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    toComeFragmentHelper.callingLocationFunctionalities();
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
}
