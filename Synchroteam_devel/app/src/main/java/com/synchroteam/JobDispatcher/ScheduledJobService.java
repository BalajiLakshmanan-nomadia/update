package com.synchroteam.JobDispatcher;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import androidx.annotation.NonNull;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.synchroteam.beans.NotifEventModels;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.evernote.NotificationSyncJob;
import com.synchroteam.retrofit.ApiClientNew;
import com.synchroteam.retrofit.ApiInterface;
import com.synchroteam.retrofit.models.NotificationService.EventNotiRequest;
import com.synchroteam.retrofit.models.NotificationService.EventNotiResult;
import com.synchroteam.retrofit.models.NotificationService.jsonInfo;
import com.synchroteam.roomDB.RoomDBSingleTone;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.Logger;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
//v51.0.2
//import io.realm.Realm;
//import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduledJobService extends Worker {
    public ScheduledJobService(
            @NonNull Context c,
            @NonNull WorkerParameters params) {
        super(c, params);
        context = c;
        workerParameters = params;
    }

    private Context context;
    private WorkerParameters workerParameters;

    private static final String TAG = ScheduledJobService.class.getSimpleName();
    ArrayList<NotifEventModels> list;

    /**
     * The dao.
     */
    private Dao dao;
    User user;

    private void logicNotificationJobStatus() {

        new GetRealmDataAsyncTask().execute();

    }

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
        list = new ArrayList<>();
        dao = DaoManager.getInstance();
        user = dao.getUser();

        Log.e(TAG, "Worker RUNNING");

        try {
            logicNotificationJobStatus();
        } catch (Exception e) {
            return Result.failure();
        }

        return Result.retry();
       // return Result.success();
    }

    @NonNull
    @Override
    public void onStopped() {
        Log.e(TAG, "Worker onStopped");

    }

    private class GetRealmDataAsyncTask extends AsyncTaskCoroutine<Void, Boolean> {

        @Override
        public Boolean doInBackground(Void... params) {

            boolean result = false;

            List<com.synchroteam.roomDB.entity.NotificationEventModels> results = new ArrayList<>();
            results = RoomDBSingleTone.instance(context).roomDao().getAllNotificationEventModels();
            for (com.synchroteam.roomDB.entity.NotificationEventModels information : results) {
                list.add(new NotifEventModels(information.getId(), information.getUrl(),
                        information.getValue(), false));
            }

            if (results != null && results.size() > 0)
                result = true;
            else
                Log.e("TAG", "NOTIFICATION VALUES ARE EMPTY");

            return result;
        }

        @Override
        public void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                if (list != null && list.size() > 0) {
                    callService(0);
                }
            } else {
             //   cancelAllJobs("CheckNetUpdateDb");

            }
        }
    }

    private void cancelAllJobs(String checkNetUpdateDb) {
        WorkManager.getInstance(context).cancelUniqueWork(checkNetUpdateDb);
    }

    private void callService(int pos) {
        try {
            if (pos < list.size())
                hitNotificationEventService(list.get(pos), pos);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized boolean hitNotificationEventService(NotifEventModels eventModels, int pos)
            throws Exception {
        final int[] currentPos = {pos};
        final boolean[] result = {false};
        if (user != null && user.getLogin() != null) {
            String authUserName = dao.getUserDomain() + "_" + user.getLogin();
            String authPassword = user.getPwd();
            ApiInterface apiService = ApiClientNew.createService(ApiInterface.class, authUserName, authPassword);

            final String id = eventModels.getId();
            String url = eventModels.getUrl();
            EventNotiRequest eventNotiRequest = null;
            try {
                JSONObject jsonObj = new JSONObject(eventModels.getValue());

                Logger.log(TAG, "NOTIFICATION  OFF "+jsonObj.toString());

                JSONObject infoJson=jsonObj.getJSONObject("jsonInfo");

                Logger.log(TAG, "NOTIFICATION  OFF "+infoJson.toString());
                jsonInfo infoObject =new jsonInfo();
                int idJobType = infoJson.getInt("idJobType");

                infoObject.setIdJobType(idJobType);
                infoObject.setIdTech(infoJson.getInt("idTech"));

                if (infoJson.has("idClient")&&
                        !infoJson.isNull("idClient"))
                    infoObject.setIdClient(infoJson.getInt("idClient"));

                if (infoJson.has("idSite")&&!infoJson.isNull("idSite"))
                    infoObject.setIdSite(infoJson.getInt("idSite"));

                if (!infoJson.isNull("idEquipment")&&!infoJson.isNull("idEquipment"))
                    infoObject.setIdEquipement(infoJson.getInt("idEquipment"));

                if (infoJson.has("startedScheduledDateTime")&&!infoJson.isNull("startedScheduledDateTime")&&
                        infoJson.getString("startedScheduledDateTime") .length() > 0)
                    infoObject.setStartedScheduledDateTime(infoJson.getString("startedScheduledDateTime") );

                if (infoJson.has("completedScheduledDateTime")&&!infoJson.isNull("completedScheduledDateTime")&&
                        infoJson.getString("completedScheduledDateTime") .length() > 0)
                    infoObject.setCompletedScheduledDateTime(infoJson.getString("completedScheduledDateTime"));

                if (infoJson.has("startedRealisedDateTime")&&!infoJson.isNull("startedRealisedDateTime")&&
                        infoJson.getString("startedRealisedDateTime").length() > 0)
                    infoObject.setStartedRealisedDateTime(infoJson.getString("startedRealisedDateTime"));

                if (infoJson.has("completedRealisedDateTime")&&!infoJson.isNull("completedRealisedDateTime")&&
                        infoJson.getString("completedRealisedDateTime").length() > 0)
                    infoObject.setCompletedRealisedDateTime(infoJson.getString("completedRealisedDateTime"));

                    // old
//                eventNotiRequest = new EventNotiRequest(jsonObj.getInt("IdCustomer"),
//                        jsonObj.getString("IdJob"),
//                        jsonObj.getInt("JobStatus"),
//                        "tech",
//                        jsonObj.getString("timestamp"),infoObject);

                // new change  - for notification
                eventNotiRequest = new EventNotiRequest(jsonObj.getInt("IdCustomer"),
                        jsonObj.getString("IdJob"),
                        jsonObj.getInt("JobStatus"),
                        "tech",
                        jsonObj.getString("timestamp"),infoObject);

            } catch (Exception e) {
                Logger.printException(e);
            }

            if (eventNotiRequest != null) {

                Call<EventNotiResult> call = apiService.notificationEventService(url, eventNotiRequest);

                call.enqueue(new Callback<EventNotiResult>() {
                    @Override
                    public void onResponse(Call<EventNotiResult> call, Response<EventNotiResult> response) {

                        if (response.isSuccessful()) {
                            currentPos[0]++;
                            int status = response.body().getResult();
                            if (status == 1) {
                                result[0] = true;
                                Logger.log(TAG, "NOTIFICATION STATUS RESULT ====>" + status);
                                deleteFormDB(id);
                            } else {
                                result[0] = true;
                                Logger.log(TAG, "NOTIFICATION STATUS RESULT FAILURE====>" + status);
                            }
                        } else {
                            result[0] = false;
                        }
                        callService(currentPos[0]);
                    }

                    @Override
                    public void onFailure(Call<EventNotiResult> call, Throwable t) {
                        result[0] = false;
                        currentPos[0]++;
                        callService(currentPos[0]);
                    }

                });
            }

        }


        return result[0];
    }

    public synchronized void deleteFormDB(final String idNotif) {

        try {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    RoomDBSingleTone.instance(context).roomDao().
                            deteteIDNotificationEventModels(idNotif);
                }
            }).start();

        } catch (Exception e) {
            Logger.printException(e);
        }

    }
}
