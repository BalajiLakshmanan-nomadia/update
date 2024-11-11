package com.synchroteam.evernote;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.synchroteam.tracking.DaoTracking;
import com.synchroteam.tracking.TrackingParameterService;
import com.synchroteam.utils.DaoTrackingManager;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.SharedPref;

public class TrackSyncJob extends Worker {
    public TrackSyncJob(
            @NonNull Context c,
            @NonNull WorkerParameters params) {
        super(c, params);
        context = c;
        workerParameters = params;
    }

    private Context context;
    private WorkerParameters workerParameters;

    public static final String TAG = "track_sync_tag";
    /**
     * The dao.
     */
    private DaoTracking dao;

    /**
     * Override this method to do your actual background processing.  This method is called on a
     * background thread - you are required to <b>synchronously</b> do your work and return the
     * {@link Result} from this method.  Once you return from this
     * method, the Worker is considered to have finished what its doing and will be destroyed.  If
     * you need to do your work asynchronously on a thread of your own choice, see
     * {@link ListenableWorker}.
     * <p>
     * A Worker has a well defined
     * <a href="https://d.android.com/reference/android/app/job/JobScheduler">execution window</a>
     * to finish its execution and return a {@link Result}.  After
     * this time has expired, the Worker will be signalled to stop.
     *
     * @return The {@link Result} of the computation; note that
     * dependent work will not execute if you use
     * {@link Result#failure()} or
     * {@link Result#failure(Data)}
     */
    @NonNull
    @Override
    public Result doWork() {
        dao = DaoTrackingManager.getInstance();
        Log.e(TAG, "Worker RUNNING");

        try {
            isTrackingActive();
        } catch (Exception e) {
            Logger.log(TAG, "EVERNOTE_JOB Exception occurred ====>" + e);
        }

        return Result.success();
    }

    private void isTrackingActive() {
        if (dao.getSessiondata("tracking").equals("on")) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (SharedPref.getIsTrackcingRunning(context)) {

                    boolean serviceRunning = isTrackingServiceRunning(context);
                    if (serviceRunning) {
                        Logger.log(TAG, "EVERNOTE_JOB service is running");
                    } else {
                        Logger.log(TAG, "EVERNOTE_JOB service is not running");
                    }

                } else {
                    Logger.log(TAG, "EVERNOTE_JOB Tracking is on but the service is not running. " +
                            "Change the pref value");
                }

            } else {
                Logger.log(TAG, "EVERNOTE_JOB Tracking is on but the service is not running. " +
                        "Runtime Permission is disabled");
            }

        } else {
            Logger.log(TAG, "EVERNOTE_JOB Tracking is disabled on the device");
        }

    }

    public boolean isTrackingServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (TrackingParameterService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}