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
import com.synchroteam.fragmenthelper.ReportsWithDateFragmentHelperNew;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DateEvent;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.RequestCode;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc

/**
 * The Fragment Class to Show a nested fragment when by date Tab is selected in
 * Report fragment. created for future use
 */
public class ReportsWithDateFragment extends BaseFragment implements AllJobsSortingDialog.SortingListener {

    /**
     * The reports with date fragment helper.
     */
    private ReportsWithDateFragmentHelperNew reportsWithDateFragmentHelper;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 124;

    /**
     * Instantiates a new reports with date fragment.
     */
    public ReportsWithDateFragment() {
        // TODO Auto-generated constructor stub
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
        reportsWithDateFragmentHelper = new ReportsWithDateFragmentHelperNew(
                getSyncroTeamBaseActivity(), this, this);

        View view = reportsWithDateFragmentHelper.inflateLayout(inflater, container);
        EventBus.getDefault().registerSticky(this);

        return view;
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
     * @see com.synchroteam.fragment.FragmentInterface#getFragmentTag()
     */
    @Override
    public String getFragmentTag() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.synchroteam.fragment.FragmentInterface#doOnSync()
     */
    @Override
    public void doOnSync() {
        // TODO Auto-generated method stub
        reportsWithDateFragmentHelper.doOnSyncronize();
        EventBus.getDefault().post(new UpdateUiOnSync());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == RequestCode.REQUEST_CODE_TEXT_BARCODE) {
            Logger.output("FRAG", "activity result 1");
            if (resultCode == Activity.RESULT_OK) {
                Logger.output("FRAG", "activity result 1");
                reportsWithDateFragmentHelper.onReturnToFragment(requestCode, resultCode, data);
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_GPS_SETTINGS) {
            reportsWithDateFragmentHelper.onReturnToFragment(requestCode, resultCode, null);
        }


    }


    /*
     * (non-Javadoc)
     *
     * @see com.synchroteam.fragment.FragmentInterface#listUpdate()
     */
    @Override
    public void listUpdate() {
        // TODO Auto-generated method stub

    }

    /**
     * On event.
     *
     * @param event the event
     */
    public void onEvent(DateEvent event) {

        reportsWithDateFragmentHelper.onDateSelected(event.getDateSelected());

    }

    /**
     * On event.
     *
     * @param updateDataBaseEvent the update data base event
     */
    public void onEvent(UpdateDataBaseEvent updateDataBaseEvent) {
        reportsWithDateFragmentHelper.updateDatabase();

    }

    /* (non-Javadoc)
     * @see android.support.v4.app.Fragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        EventBus.getDefault().unregister(this);
        Logger.log("onDestroy ", "ReportsWithDateFragment");
        super.onDestroy();
    }

    @Override
    public void onSortingChose(int options, String optionName, String jobNumber) {
        reportsWithDateFragmentHelper.sortJobList(options, optionName, jobNumber);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    reportsWithDateFragmentHelper.callingLocationFunctionalities();
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
