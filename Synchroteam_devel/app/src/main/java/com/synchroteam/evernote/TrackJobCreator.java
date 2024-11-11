package com.synchroteam.evernote;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


public class TrackJobCreator {

    @Nullable
    public Worker create(@NonNull String tag, @NonNull Context context, @NonNull WorkerParameters params) {
        switch (tag) {
            case TrackSyncJob.TAG:
                return new TrackSyncJob(context, params);
            case NotificationSyncJob.TAG:
                return new NotificationSyncJob(context, params);
            default:
                return null;
        }
    }
}