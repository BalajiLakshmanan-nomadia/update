package com.synchroteam.synchroteam;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.core.content.ContextCompat;
import androidx.multidex.BuildConfig;
import androidx.multidex.MultiDexApplication;


import com.google.firebase.crashlytics.FirebaseCrashlytics;

import com.synchroteam.utils.AutoSyncBraodcastReciver;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.NotificationSyncBraodcastReciver;
import com.synchroteam.utils.SharedPref;

import java.io.File;

//import io.fabric.sdk.android.Fabric;

//v51.0.2
//import io.realm.Realm;
//import io.realm.RealmConfiguration;

/**
 * The Class SyncroTeamApplication.
 *
 * @author Ideavate.solution
 */
public class SyncroTeamApplication extends MultiDexApplication {

    /**
     * The syncoteam navigation activity.
     */
    private CommonInterface syncoteamNavigationActivity;

    /**
     * The is application in fore ground.
     */
    private boolean isApplicationInForeGround;

    /**
     * The alarm intent.
     */
    private Intent alarmIntent;

    /**
     * The is locale changed.
     */
    private boolean isLocaleChanged;

    /**
     * The syncro team application.
     */
    private static SyncroTeamApplication syncroTeamApplication;

    /*
     * (non-Javadoc)
     *
     * @see android.app.Application#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();

//        File dexOutputDir = codeCacheDir;
//        dexOutputDir.setReadOnly();

        //TODO Uncomment this while sending build
//        Fabric.with(this, new Crashlytics());
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG);

//v51.0.2
        //initiate real object
        // Realm.init(this);
        //configure realm object
//        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
//                .name("synchroteam.realm")
//                .schemaVersion(0)
//                .deleteRealmIfMigrationNeeded()
//                .build();
//        Realm.setDefaultConfiguration(realmConfig);

        alarmIntent = new Intent(this, AutoSyncBraodcastReciver.class);
        syncroTeamApplication = this;
        isLocaleChanged = false;

        SharedPref.setIsEmptyValue(false, this);

        // register the sync receiver for notification.
        IntentFilter autoSyncFilter = new IntentFilter();
        autoSyncFilter.addAction(KEYS.AUTO_SYNC_BROADCAST);
        registerReceiver(new NotificationSyncBraodcastReciver(), autoSyncFilter,RECEIVER_EXPORTED );
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterReceiver(new NotificationSyncBraodcastReciver());
    }

    /**
     * Sets that syncroteam app is in foreground.
     *
     * @param isSyncroteamActivityLive the new syncroteam app live
     */
    public void setSyncroteamAppLive(boolean isSyncroteamActivityLive) {
        if (this != null)
            this.isApplicationInForeGround = isSyncroteamActivityLive;
    }

    /**
     * checks that syncroteam app is in foreground.
     *
     * @return the syncroteam app live
     */
    public boolean getSyncroteamAppLive() {

        return isApplicationInForeGround;
    }

    /**
     * Sets the syncroteam activity instance.
     *
     * @param syncoteamNavigationActivity the new syncroteam activity instance
     */
    public void setSyncroteamActivityInstance(
            CommonInterface syncoteamNavigationActivity) {

        this.syncoteamNavigationActivity = syncoteamNavigationActivity;
    }

    /**
     * Gets the syncroteam activity instance.
     *
     * @return the syncroteam activity instance
     */
    public CommonInterface getSyncroteamActivityInstance() {

        return syncoteamNavigationActivity;
    }

    /**
     * Gets the auto sync intent.
     *
     * @return the auto sync intent
     */
    public Intent getAutoSyncIntent() {
        return alarmIntent;

    }

    /**
     * Gets the app context.
     *
     * @return the app context
     */
    public static SyncroTeamApplication getAppContext() {

        return syncroTeamApplication;
    }

    /**
     * Sets the checks if is locale changed.
     *
     * @param isLocaleChanged the new checks if is locale changed
     */
    public void setIsLocaleChanged(boolean isLocaleChanged) {
        this.isLocaleChanged = isLocaleChanged;
    }

    /**
     * Gets the checks if is locale chenged.
     *
     * @return the checks if is locale chenged
     */
    public boolean getIsLocaleChenged() {

        return isLocaleChanged;
    }

}
