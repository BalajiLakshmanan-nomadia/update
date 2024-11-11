package com.synchroteam.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.synchroteam.events.ActivityUpdateEvent;
import com.synchroteam.observers.ObservableObject;

import de.greenrobot.event.EventBus;

/**
 * Created by Trident on 10/9/2017.
 */

public class ClockInTimeOutReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ObservableObject.getInstance().updateValue(intent);
        // update UI in Synchroteam navigation Activity
        EventBus.getDefault().post(new ActivityUpdateEvent());
    }
}
