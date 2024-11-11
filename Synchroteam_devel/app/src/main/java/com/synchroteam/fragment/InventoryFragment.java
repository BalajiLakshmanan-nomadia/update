package com.synchroteam.fragment;

import com.synchroteam.beans.UpdateDataBaseEvent;
import com.synchroteam.beans.UpdateUiOnSync;
import com.synchroteam.events.InventoryFragmentCatEvent;
import com.synchroteam.fragmenthelper.InventoryFragmentHelperNew;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.RequestCode;

import de.greenrobot.event.EventBus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class InventoryFragment extends BaseFragment {

    //    private InventoryFragmentHelper inventoryFragmentHelper;
    private InventoryFragmentHelperNew inventoryFragmentHelper;
    /**
     * The list update listner.
     */
    private ListUpdateListener mListUpdateListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        inventoryFragmentHelper = new InventoryFragmentHelperNew(getActivity(),
                this, this);
        try {
            EventBus.getDefault().registerSticky(this);
        } catch (Exception e) {
            Logger.printException(e);
        }

        return inventoryFragmentHelper.inflateLayout(inflater, container);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListUpdateListener = (ListUpdateListener) activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        inventoryFragmentHelper.doOnResume();
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
        inventoryFragmentHelper.doOnSyncronize();
        EventBus.getDefault().post(new UpdateUiOnSync());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.synchroteam.fragment.FragmentInterface#listUpdate()
     */
    public void listUpdate() {
        mListUpdateListener.onListUpdate();
    }

    /**
     *
     */
    public void onEvent(InventoryFragmentCatEvent updateDataBaseEvent) {

        Logger.log("onResume Unavbil ", "onEvent");

        inventoryFragmentHelper.updateListPartInventory(updateDataBaseEvent.data);
    }


    /**
     * Function is called when UpdateDataBaseEvent Event is fired through Event
     * Bus this is normally called when user perform any operation which updates
     * the database of current job listing.
     *
     * @param updateDataBaseEvent the update data base event
     */
    public void onEvent(UpdateDataBaseEvent updateDataBaseEvent) {

        Logger.log("onResume Unavbil ", "onEvent");

        inventoryFragmentHelper.refreshList();
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCode.REQUEST_CODE_TEXT_BARCODE) {
            if (resultCode == Activity.RESULT_OK) {
                inventoryFragmentHelper.onReturnToFragment(requestCode, data);
            }
        } else {
            if (resultCode == Activity.RESULT_OK) {
                inventoryFragmentHelper.onReturnToFragment(requestCode, data);
            }
        }

    }

}