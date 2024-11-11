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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synchroteam.beans.SelectTodayDate;
import com.synchroteam.beans.UpdateDataBaseEvent;
import com.synchroteam.beans.UpdateUiOnSync;
import com.synchroteam.dialogs.AllJobsSortingDialog;
import com.synchroteam.events.ActivityUpdateEvent;
import com.synchroteam.fragmenthelper.CurrentJobsFragmentHelperNew;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.RequestCode;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc

/***
 * The Fragment Class to Show Current Jobs Fragments..
 *
 * @author ${Ideavate Solution}
 *
 */
public class CurrentJobFragment extends BaseFragment implements AllJobsSortingDialog.SortingListener {

    /**
     * The current jobs fragment helper.
     */
    private CurrentJobsFragmentHelperNew currentJobsFragmentHelper;

    /**
     * The list update listner.
     */
    private ListUpdateListener mListUpdateListener;

    //private static final String TAG = "CurrentJobFragment";

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 124;

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
        currentJobsFragmentHelper = new CurrentJobsFragmentHelperNew(
                getSyncroTeamBaseActivity(), this, this);

        Logger.log("CurrentJobFragment", "onCreateView");
        EventBus.getDefault().registerSticky(this);

        return currentJobsFragmentHelper.inflateLayout(inflater, container);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.synchroteam.fragment.FragmentInterface#doOnBackPressed()
     */
    @Override
    public boolean doOnBackPressed() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.synchroteam.fragment.FragmentInterface#getFragmentTag()
     */
    @Override
    public String getFragmentTag() {
        return this.getTag();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.synchroteam.fragment.FragmentInterface#doOnSync()
     */
    @Override
    public void doOnSync() {
        currentJobsFragmentHelper.doOnSyncronize();
        listUpdate();
        currentJobsFragmentHelper.doOnResume();

    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onAttach(android.app.Activity)
     */
    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        Logger.log("Current JobFragment", "onAttach");
        super.onAttach(activity);
        mListUpdateListener = (ListUpdateListener) activity;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.synchroteam.fragment.FragmentInterface#listUpdate()
     */
    public void listUpdate() {

        mListUpdateListener.onListUpdate();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCode.REQUEST_CODE_TEXT_BARCODE) {
            Logger.output("FRAG", "activity result 1");
            if (resultCode == Activity.RESULT_OK) {
                Logger.output("FRAG", "activity result 1");
                currentJobsFragmentHelper.onReturnToFragment(requestCode, resultCode, data);
            }
        } else if (requestCode == RequestCode.REQUEST_CODE_GPS_SETTINGS) {
            currentJobsFragmentHelper.onReturnToFragment(requestCode, resultCode, null);
        }


    }

    /**
     * Function is called when UpdateDataBaseEvent Event is fired through Event
     * Bus this is normally called when user perform any operation which updates
     * the database of current job listing. .
     *
     * @param updateDataBaseEvent the update data base event
     */
    public void onEvent(UpdateDataBaseEvent updateDataBaseEvent) {
        Log.e("EVENT","THE EVENT IS CALLED >>>>>>  UpdateDataBaseEvent");
        currentJobsFragmentHelper.refreshList();
    }

    public void onEvent(ActivityUpdateEvent event) {
        Log.e("EVENT","THE EVENT IS CALLED >>>>>>  ActivityUpdateEvent");
        currentJobsFragmentHelper.refreshList();

    }


    /**
     * Function is called when SlectTodayDate Event is fired through Event Bus
     * this is normally called when user selects any other menu from Drawer
     * layout and return back to current job screen when this function is called
     * it select the today date.
     *
     * @param selectTodayDate the select today date
     */
    public void onEvent(SelectTodayDate selectTodayDate) {
        Log.e("EVENT","THE EVENT IS CALLED >>>>>>  SelectTodayDate");
        currentJobsFragmentHelper.selectTodayDate();
        currentJobsFragmentHelper.doOnResume();
//		currentJobsFragmentHelper.refreshList();
    }

    /**
     * ** Function is called UpdateUiOnSync Event is fired through Event bus
     * this is normally called when user is some other module and perform sync
     * operation than this event is fired by that module through Event bus to
     * update current Job listing.
     *
     * @param updateUiOnSync the update ui on sync
     */

    public void onEvent(UpdateUiOnSync updateUiOnSync) {
        currentJobsFragmentHelper.doOnSyncronize();
        listUpdate();
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

    @Override
    public void onSortingChose(int options, String optionName, String jobNumber) {
        currentJobsFragmentHelper.sortJobList(options, optionName, jobNumber);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    currentJobsFragmentHelper.callingLocationFunctionalities();
                }
                return;
            }
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
            ActivityCompat.requestPermissions(getSyncroTeamBaseActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }


}
