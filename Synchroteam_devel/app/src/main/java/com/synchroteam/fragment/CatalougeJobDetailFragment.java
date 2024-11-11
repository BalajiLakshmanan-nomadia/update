package com.synchroteam.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synchroteam.beans.UpdateJobDetailUi;
import com.synchroteam.beans.UpdateUiAfterSync;
import com.synchroteam.fragmenthelper.CatalogJobDetailFragmentHelperNew;
import com.synchroteam.technicalsupport.JobDetails;
import com.synchroteam.utils.RequestCode;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc

/**
 * The Fragment Class to Catalouge Fragment.
 *
 * @author ${Ideavate Solution}
 */
public class CatalougeJobDetailFragment extends Fragment {

    /**
     * The catalog job detail fragment helper.
     */
//    private CatalogJobDetailFragmentHelper catalogJobDetailFragmentHelper;
    private CatalogJobDetailFragmentHelperNew catalogJobDetailFragmentHelper;

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

        Bundle bundle = getArguments();

        catalogJobDetailFragmentHelper = new CatalogJobDetailFragmentHelperNew(
                (JobDetails) getActivity(), bundle.getString("id_interv"),
                bundle.getInt("cd_statut"), bundle.getInt("id_user"), this);
        View view = catalogJobDetailFragmentHelper.inflateLayout(inflater,
                container);
        EventBus.getDefault().registerSticky(this);
        return view;

    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onActivityResult(int, int,
     * android.content.Intent)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == RequestCode.REQUEST_CODE_DRAW_SIGNATURE) {
            if (resultCode != Activity.RESULT_CANCELED) {

                catalogJobDetailFragmentHelper.onReturnToFragment(requestCode,
                        data);

            }

        } else {
            if (resultCode == Activity.RESULT_OK) {
                catalogJobDetailFragmentHelper.onReturnToFragment(requestCode,
                        data);
            }
        }

    }

    /**
     * On qantity change.
     *
     * @param oldValue the old value
     * @param newValue the new value
     */
    public void onQantityChange(double oldValue, double newValue) {

        catalogJobDetailFragmentHelper.onQantityChange(oldValue, newValue);
    }

    /**
     * On item removed.
     */
    public void onItemRemoved() {

        catalogJobDetailFragmentHelper.onItemRemoved();
    }

    /**
     * On item removed.
     */
    public void onItemRemovedTB() {

        catalogJobDetailFragmentHelper.onItemRemovedTB();
    }

    /**
     * On event.
     *
     * @param updateJobDetailUi the update job detail ui
     */
    public void onEvent(UpdateJobDetailUi updateJobDetailUi) {
        catalogJobDetailFragmentHelper.doOnJobStartStop();
    }

    public void onEvent(UpdateUiAfterSync udaUpdateUiAfterSync) {

        catalogJobDetailFragmentHelper.doOnSyncronize();

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

}
