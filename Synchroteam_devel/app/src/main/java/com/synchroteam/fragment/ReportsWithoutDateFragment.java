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
import com.synchroteam.fragmenthelper.ReportsWithoutDateFragmentHelperNew;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.RequestCode;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc

/**
 * The Class is used to show a nested fragment in Reports Fragment when All is selected.
 * created for future use
 */
public class ReportsWithoutDateFragment extends BaseFragment implements AllJobsSortingDialog.SortingListener {


    /**
     * Instantiates a new reports without date fragment.
     */
    public ReportsWithoutDateFragment() {
        // TODO Auto-generated constructor stub
    }

    /**
     * The reports without date fragment helper.
     */
    ReportsWithoutDateFragmentHelperNew reportsWithoutDateFragmentHelper;


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 124;

    /* (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Logger.log("ReportsWithoutDateFragment", "onCreateView");


        // TODO Auto-generated method stub
        reportsWithoutDateFragmentHelper = new ReportsWithoutDateFragmentHelperNew(
                getSyncroTeamBaseActivity(), this, this);
        View view = reportsWithoutDateFragmentHelper.inflateLayout(inflater, container);
        EventBus.getDefault().registerSticky(this);
        return view;
    }


    /* (non-Javadoc)
     * @see android.support.v4.app.Fragment#onResume()
     */
    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        Logger.log("ReportsWithoutDateFragment", "onResume");

        super.onResume();


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
        return null;
    }


    /* (non-Javadoc)
     * @see com.synchroteam.fragment.FragmentInterface#doOnSync()
     */
    @Override
    public void doOnSync() {
        // TODO Auto-generated method stub
        reportsWithoutDateFragmentHelper.doOnSyncronize();
        EventBus.getDefault().post(new UpdateUiOnSync());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == RequestCode.REQUEST_CODE_TEXT_BARCODE) {
            Logger.output("FRAG", "activity result 1");
            if (resultCode == Activity.RESULT_OK) {
                Logger.output("FRAG", "activity result 1");
                reportsWithoutDateFragmentHelper.onReturnToFragment(requestCode, resultCode, data);
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_GPS_SETTINGS) {
            reportsWithoutDateFragmentHelper.onReturnToFragment(requestCode, resultCode, null);
        }


    }

    /* (non-Javadoc)
     * @see com.synchroteam.fragment.FragmentInterface#listUpdate()
     */
    @Override
    public void listUpdate() {
    }

    /**
     * On event.
     *
     * @param updateDataBaseEvent the update data base event
     */
    public void onEvent(UpdateDataBaseEvent updateDataBaseEvent) {
        reportsWithoutDateFragmentHelper.updateDatabase();

    }


    /* (non-Javadoc)
     * @see android.support.v4.app.Fragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onSortingChose(int options, String optionName, String jobNumber) {
        reportsWithoutDateFragmentHelper.sortJobList(options, optionName, jobNumber);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    reportsWithoutDateFragmentHelper.callingLocationFunctionalities();
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
