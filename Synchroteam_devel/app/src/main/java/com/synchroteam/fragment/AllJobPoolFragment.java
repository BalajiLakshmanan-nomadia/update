package com.synchroteam.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synchroteam.baseactivity.SyncroTeamBaseActivity;
import com.synchroteam.beans.UpdateDataBaseEvent;
import com.synchroteam.beans.UpdateUiOnSync;
import com.synchroteam.dialogs.AllJobsSortingDialog;
import com.synchroteam.fragmenthelper.AllJobPoolFragmentHelper;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.RequestCode;

import de.greenrobot.event.EventBus;

/**
 * This fragment shows the job pool for an user.<p></p>
 * Created by Trident.
 */
public class AllJobPoolFragment extends BaseFragment implements AllJobsSortingDialog.SortingListener {

    //------------------------------------INSTANCE...VARIABLES...DECLARATION...STARTS------------------------

    /**
     * Helper class
     */
    AllJobPoolFragmentHelper allJobsFragmentHelper;

    /**
     * listener to update list
     */
    ListUpdateListener mListUpdateListener;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 124;

//------------------------------------INSTANCE...VARIABLES...DECLARATION...ENDS--------------------------

//-------------------------------------LIFECYCLE...METHODS...STARTS--------------------------------------


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        allJobsFragmentHelper = new AllJobPoolFragmentHelper(getSyncroTeamBaseActivity(), this,
                this);

        View view = allJobsFragmentHelper.inflateLayout(inflater, container);

        try {
            EventBus.getDefault().registerSticky(this);
        } catch (Exception e) {
            Logger.printException(e);
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        SyncroTeamBaseActivity activity;

        if (context instanceof SyncroTeamBaseActivity) {
            activity = (SyncroTeamBaseActivity) context;
            mListUpdateListener = (ListUpdateListener) activity;
        }

    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onStop() {
        super.onStop();
        allJobsFragmentHelper.onStop();
    }

//-------------------------------------LIFECYCLE...METHODS...ENDS----------------------------------------

//-------------------------------------OVERRIDDEN...METHODS...STARTS--------------------------------------


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCode.REQUEST_CODE_TEXT_BARCODE) {
            Logger.output("FRAG", "activity result 1");
            if (resultCode == Activity.RESULT_OK) {
                Logger.output("FRAG", "activity result 1");
                allJobsFragmentHelper.onReturnToFragment(requestCode, resultCode, data);
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_GPS_SETTINGS) {
            allJobsFragmentHelper.onReturnToFragment(requestCode, resultCode, null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    allJobsFragmentHelper.callingLocationFunctionalities();
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
    public boolean doOnBackPressed() {
        return false;
    }

    @Override
    public String getFragmentTag() {
        return this.getTag();
    }

    @Override
    public void doOnSync() {
        allJobsFragmentHelper.doOnSyncronize();
        EventBus.getDefault().post(new UpdateUiOnSync());
    }

    @Override
    public void listUpdate() {
        mListUpdateListener.onListUpdate();
    }

    @Override
    public void onSortingChose(int options, String optionName, String jobNumber) {
        allJobsFragmentHelper.sortJobList(options, optionName, jobNumber);
    }

//-------------------------------------OVERRIDDEN...METHODS...ENDS----------------------------------------

//------------------------------------LOCAL...METHODS...STARTS--------------------------------------------

    /**
     * On event.
     *
     * @param updateDataBaseEvent the update data base event
     */
    public void onEvent(UpdateDataBaseEvent updateDataBaseEvent) {
        allJobsFragmentHelper.updateDatabase();
    }

    //------------------------------------LOCAL...METHODS...ENDS----------------------------------------------

}
