package com.synchroteam.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synchroteam.beans.UpdateUiOnSync;
import com.synchroteam.events.AutoSyncViewUpdateEvent;
import com.synchroteam.fragmenthelper.LastSyncFragmentHelper;

import de.greenrobot.event.EventBus;

/**
 * Fragment for setting the auto synchronization or push synchronization feature.
 * <p>
 * Created by Trident on 10/24/2016.
 */

public class LastSyncFragment extends BaseFragment {

    private ListUpdateListener listUpdateListener;
    LastSyncFragmentHelper lastSyncFragmentHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        registerEventBus();

        lastSyncFragmentHelper = new LastSyncFragmentHelper(getSyncroTeamBaseActivity(), this);
        return lastSyncFragmentHelper.inflateLayout(inflater, container);
    }

    private void registerEventBus() {
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listUpdateListener = (ListUpdateListener) context;
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
        EventBus.getDefault().post(new UpdateUiOnSync());
    }

    @Override
    public void listUpdate() {
        listUpdateListener.onListUpdate();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * Event bus to update UI for Auto sync
     *
     * @param event
     */
    public void onEvent(AutoSyncViewUpdateEvent event) {
        lastSyncFragmentHelper.setViewForSyncs();
    }

}