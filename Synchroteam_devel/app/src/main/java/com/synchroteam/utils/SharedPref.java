package com.synchroteam.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.provider.Settings.System;

import com.synchroteam.synchroteam3.R;

import java.text.Format;
import java.util.Date;

// TODO: Auto-generated Javadoc

/**
 * The Class SharedPref. * created for future use
 *
 * @author Ideavate.solution
 */
public class SharedPref {

    /**
     * The Constant SP_NAME_SYNCRONIZATION_SETTINGS.
     */
    private static final String SP_NAME_SYNCRONIZATION_SETTINGS = "syncroteam_sycronization_settings";

    /**
     * The Constant SP_NAME_CURRENT_JOBS.
     */
    private static final String SP_NAME_CURRENT_JOBS = "current_jobs";

    /**
     * The Constant SP_NAME_DATE_AND_TIME_FORMAT.
     */
    private static final String SP_NAME_DATE_AND_TIME_FORMAT = "sp_name_date_and_time_format";

    /**
     * The Constant SP_NAME_DATABASE_CREATED.
     */
    private static final String SP_NAME_DATABASE_CREATED = "database_created";

    /**
     * The Constant SP_NAME_TRACKING_SETTINGS.
     */
    private static final String SP_NAME_TRACKING_SETTINGS = "tracking_settings";


    private static final String SP_NAME_ENABLE_DIABLE_SYNCRONIZATION = "enable_disable_syncronization";

    private static final String SP_APP_UPDATION = "sp_app_updation";

    /**
     * The Constant IS_SYNC_ON.
     */
    private static final String IS_SYNC_ON = "is_sync_on";

    /**
     * The Constant IS_PUSH_SYNC_ON.
     */
    private static final String IS_PUSH_SYNC_ON = "is_push_sync_on";

    /**
     * The Constant IS_TRACKING_ON_OFF.
     */
    private static final String IS_TRACKING_ON_OFF = "is_tracking_on_off";

    /**
     * The Constant TRACKING_INDICATOR_ON_OFF.
     */
    private static final String TRACKING_INDICATOR_ON_OFF = "tracking_indicator_on_off";

    /**
     * The Constant TRAVEL.
     */
    private static final String TRAVEL_INDICATOR_ON_OFF = "travel_indicatior_on_off";

    /**
     * The name of the current running TRAVEL activity.
     */
    private static final String TRAVEL_NAME_CURRENT = "current_travel_name";

    private static final String TRAVEL_LOC_DATA = "travel_location_data";

    /**
     * The Constant IS_DATABASE_CREATED.
     */
    static final String IS_DATABASE_CREATED = "is_database_created";

    /**
     * The Constant TIME_SYNCRONISED.
     */
    private static final String TIME_SYNCRONISED = "time_syncronised";

    /**
     * The Constant SYNC_INTERVAL.
     */
    private static final String SYNC_INTERVAL = "sync_interval";

    /**
     * The Constant DATE_FORMAT.
     */
    private static final String DATE_FORMAT = "date_format";

    /**
     * The Constant TIME_FORMAT.
     */
    private static final String TIME_FORMAT = "time_format";

    /**
     * The Constant SP_TAKE_BACK_PART
     */
    private static final String SP_TAKE_BACK_PART = "synchro_take_back_part";
    private static final String SP_TAKE_BACK_PART_SERIAL_ID = "synchro_TBPart_SerialId";

    /**
     * The Constant CURRENT_JOBS_COUNT.
     */
    private static final String CURRENT_JOBS_COUNT = "current_jobs_count";

    private static final String IS_CLIENT_SECTION_SELECTED = "is_cleint_section_selected";

    private static final String IS_CLIENT_FETCHED = "is_client_fetched";

    private static final String IS_SITES_FETCHED = "is_site_fetched";

    private static final String IS_EQUIPMENT_FETCHED = "is_equipment_fetched";

    private static final String SHOULD_RESTART_TRACKING = "should_restart_tracking";

    private static final String IS_NOT_LOGGED_IN_TODAY = "is_not_logged_in_today";

    private static final String FILTERED_CATEGORY_PARTS_SERVICES = "filtered_category_parts_services";

    private static final String IS_STOCK_SELECTED = "filtered_type_parts_services";

    private static final String IS_DEBUG_ENABLED = "is_debug_enabled";

    private static final String IS_TRACKING_DEBUG_ENABLED = "is_tracking_debug_enabled";

    private static final String IS_SCRIPT_UPDATED_FROM_40_TO_41 = "is_script_updated";

    private static final String ALL_JOB_SORTING_OPTION = "all_job_sorting_option";

    private static final String SORTED_JOB_NUMBER = "job_number_sorted";

    private static final String PERMISSION_CAMERA = "camera_permission";

    private static final String SYNC_DEBUG_PORT = "sync_debug_port";
    private static final String SYNC_DEBUG_SERVER = "sync_debug_server";
    private static final String SYNC_DEBUG_SSL = "sync_debug_ssl";

    private static final String SYNC_STD_PORT = "sync_std_port";
    private static final String SYNC_STD_SERVER = "sync_std_server";
    private static final String SYNC_STD_SSL = "sync_std_ssl";
    private static final String IS_TIME_OUT_ENABLED = "is_timeout_enabled";
    private static final String CLOCKED_IN_TIME = "clocked_in_time";
    private static final String DISTANCE_TRAVELLED = "distance_travelled";
    private static final String IS_DRIVING_ENABLED = "is_driving";
    private static final String SYNC_URL_STRIPE_SERVER = "url_stripe";
    private static final String SYNC_NOTI_URL_SERVER = "url_event_listener";
    private static final String IS_PREVIOUS_TIME_OUT = "is_previous_timeout";
    private static final String SYNC_URL_BASE_SERVER = "url_base";
    private static final String SYNC_JOB_POOL_URL_SERVER = "url_pool_listener";
    private static final String SYNC_MOBILE_AUTH_SERVER = "mob_auth_server";

    private static final String IS_EMPTY_VALUE = "emptyvalue";
    private static final String IS_LOGIN_USER = "is_login_user";

    private static final String LOGIN_USER_SETT = "login_user_settings";

    /**
     * Sets if sync is on.
     *
     * @param is_sync_on the is_sync_on
     * @param context    the context
     */
    public static void setIsSyncOn(boolean is_sync_on, Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(IS_SYNC_ON, is_sync_on);
        editor.commit();

    }

    /**
     * Return a boolean indicating if sync is on.
     *
     * @param context the context
     * @return the checks if is sync on
     */
    public static boolean getIsSyncOn(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);

        return sp.getBoolean(IS_SYNC_ON, false);

    }

    /**
     * Sets if sync is on.
     *
     * @param context the context
     */
    public static void setIsPushSyncOn(boolean is_push_sync_on, Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(IS_PUSH_SYNC_ON, is_push_sync_on);
        editor.commit();

    }

    /**
     * Return a boolean indicating if sync is on.
     *
     * @param context the context
     * @return the checks if is sync on
     */
    public static boolean getIsPushSyncOn(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);

        return sp.getBoolean(IS_PUSH_SYNC_ON, false);

    }

    /**
     * Sets the time syncronised.
     *
     * @param time_syncronised the time_syncronised
     * @param context          the context
     */
    public static void settimeSyncronised(long time_syncronised, Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putLong(TIME_SYNCRONISED, time_syncronised);
        editor.commit();

    }

    /**
     * Gets the last Syncronised time.
     *
     * @param context the context
     * @return the time syncronised
     */
    public static String gettimeSyncronised(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);

        Date date = new Date(sp.getLong(TIME_SYNCRONISED,
                java.lang.System.currentTimeMillis()));
        Format timeFormat = android.text.format.DateFormat
                .getTimeFormat(context);
        Format dateFormat = android.text.format.DateFormat
                .getDateFormat(context);

        return dateFormat.format(date) + " " + timeFormat.format(date);

    }

    /**
     * Sets the sync interval.
     *
     * @param syncInterval the sync interval
     * @param context      the context
     */
    public static void setSyncInterval(int syncInterval, Context context) {

        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putInt(SYNC_INTERVAL, syncInterval);
        editor.commit();

    }

    /**
     * Gets the sync interval.
     *
     * @param context the context
     * @return the sync interval
     */
    public static int getSyncInterval(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);

        return sp.getInt(
                SYNC_INTERVAL,
                Integer.parseInt(context.getResources().getString(
                        R.string.hintDefaultTimeInterval)));

    }

    /**
     * Sets the current jobs count.
     *
     * @param count   the count
     * @param context the context
     */
    public static void setCurrentJobsCount(String count, Context context) {

        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_CURRENT_JOBS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(CURRENT_JOBS_COUNT, count);
        editor.commit();

    }

    /**
     * Gets the sync interval.
     *
     * @param context the context
     * @return the sync interval
     */
    public static String getCurrentJobsCount(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_CURRENT_JOBS, Context.MODE_PRIVATE);

        return sp.getString(CURRENT_JOBS_COUNT, "0");

    }

    /**
     * Sets the database created.
     *
     * @param created the created
     * @param context the context
     */
    public static void setDatabaseCreated(boolean created, Context context) {

        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_DATABASE_CREATED, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(IS_DATABASE_CREATED, created);
        editor.commit();

    }

    /**
     * Gets the database created.
     *
     * @param context the context
     * @return the database created
     */
    public static boolean getDatabaseCreated(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_DATABASE_CREATED, Context.MODE_PRIVATE);

        return sp.getBoolean(IS_DATABASE_CREATED, false);
    }

    /**
     * Clear data.
     *
     * @param context the context
     */
    public static void clearData(Context context) {
        Editor syncSeetingEditor = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE).edit();
        syncSeetingEditor.clear();
        syncSeetingEditor.commit();
        Editor current_jobsEditor = context.getSharedPreferences(
                SP_NAME_CURRENT_JOBS, Context.MODE_PRIVATE).edit();
        current_jobsEditor.clear();
        current_jobsEditor.commit();

        Editor database_createdEditor = context.getSharedPreferences(
                SP_NAME_DATABASE_CREATED, Context.MODE_PRIVATE).edit();
        database_createdEditor.clear();
        database_createdEditor.commit();
        Editor tracing_settingEditor = context.getSharedPreferences(
                SP_NAME_TRACKING_SETTINGS, Context.MODE_PRIVATE).edit();
        tracing_settingEditor.clear();
        tracing_settingEditor.commit();

        Editor tack_back_settingEditor = context.getSharedPreferences(
                SP_TAKE_BACK_PART, Context.MODE_PRIVATE).edit();
        tack_back_settingEditor.clear();
        tack_back_settingEditor.commit();

        Editor tack_back_serial_settingEditor = context.getSharedPreferences(
                SP_TAKE_BACK_PART_SERIAL_ID, Context.MODE_PRIVATE).edit();
        tack_back_serial_settingEditor.clear();
        tack_back_serial_settingEditor.commit();
    }

    /**
     * Sets the previous value of tracking.
     *
     * @param context      the context
     * @param isTrackingOn the is tracking on
     */
    public static void setPreviousValueOfTracking(Context context,
                                                  boolean isTrackingOn) {

        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_TRACKING_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(IS_TRACKING_ON_OFF, isTrackingOn);
        editor.commit();

    }

    /**
     * Gets the previous value of tracking.
     *
     * @param context the context
     * @return the previous value of tracking
     */
    public static boolean getPreviousValueOfTracking(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_TRACKING_SETTINGS, Context.MODE_PRIVATE);

        return sp.getBoolean(IS_TRACKING_ON_OFF, false);

    }

    /**
     * Sets the date format.
     *
     * @param context the new date format
     */
    public static void setDateFormat(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_DATE_AND_TIME_FORMAT, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(DATE_FORMAT, System.getString(
                context.getContentResolver(), System.DATE_FORMAT));
        editor.commit();

    }

    /**
     * Sets the time format.
     *
     * @param context the new time format
     */
    public static void setTimeFormat(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_DATE_AND_TIME_FORMAT, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(TIME_FORMAT, System.getString(
                context.getContentResolver(), System.TIME_12_24));
        editor.commit();
    }

    /**
     * Gets the date format.
     *
     * @param context the context
     * @return the date format
     */
    public static String getDateFormat(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_DATE_AND_TIME_FORMAT, Context.MODE_PRIVATE);
        return sp.getString(DATE_FORMAT, System.getString(
                context.getContentResolver(), System.DATE_FORMAT));
    }

    /**
     * Gets the time format.
     *
     * @param context the context
     * @return the time format
     */
    public static String getTimeFormat(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_DATE_AND_TIME_FORMAT, Context.MODE_PRIVATE);
        return sp.getString(TIME_FORMAT, System.getString(
                context.getContentResolver(), System.TIME_12_24));
    }

    /**
     * Sets the is tracking running.
     *
     * @param isTrackingRunning the is tracking running
     * @param context           the context
     */
    public static void setIsTrackingRunning(boolean isTrackingRunning,
                                            Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_TRACKING_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(TRACKING_INDICATOR_ON_OFF, isTrackingRunning);
        editor.commit();

    }

    /**
     * Gets the checks if is trackcing running.
     *
     * @param context the context
     * @return the checks if is trackcing running
     */
    public static boolean getIsTrackcingRunning(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_TRACKING_SETTINGS, Context.MODE_PRIVATE);
        return sp.getBoolean(TRACKING_INDICATOR_ON_OFF, false);

    }

    public static void setIsClientSectionOpen(Activity activity,
                                              boolean isClientSectionSelected) {

        SharedPreferences sp = activity.getSharedPreferences(
                SP_NAME_ENABLE_DIABLE_SYNCRONIZATION, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(IS_CLIENT_SECTION_SELECTED, isClientSectionSelected);
        editor.commit();

    }

    public static boolean getIsCLientSectionOpen(Activity activity) {

        SharedPreferences sp = activity.getSharedPreferences(
                SP_NAME_ENABLE_DIABLE_SYNCRONIZATION, Context.MODE_PRIVATE);

        return sp.getBoolean(IS_CLIENT_SECTION_SELECTED, false);

    }

    public static void setIsClientFetched(Activity activity,
                                          boolean isCLientFetched) {

        SharedPreferences sp = activity.getSharedPreferences(
                SP_NAME_ENABLE_DIABLE_SYNCRONIZATION, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(IS_CLIENT_FETCHED, isCLientFetched);
        editor.commit();

    }

    public static void setIsSiteFetched(Activity activity, boolean isSiteFetched) {

        SharedPreferences sp = activity.getSharedPreferences(
                SP_NAME_ENABLE_DIABLE_SYNCRONIZATION, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(IS_SITES_FETCHED, isSiteFetched);
        editor.commit();

    }

    public static void setIsEquipmentFetched(Activity activity,
                                             boolean isEquipmentFetched) {

        SharedPreferences sp = activity.getSharedPreferences(
                SP_NAME_ENABLE_DIABLE_SYNCRONIZATION, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(IS_SITES_FETCHED, isEquipmentFetched);
        editor.commit();

    }

    public static boolean getIsClientSiteEquipmentFetched(Activity activity) {
        SharedPreferences sp = activity.getSharedPreferences(
                SP_NAME_ENABLE_DIABLE_SYNCRONIZATION, Context.MODE_PRIVATE);

        return ((sp.getBoolean(IS_CLIENT_FETCHED, true))
                && (sp.getBoolean(IS_SITES_FETCHED, true)) && (sp.getBoolean(
                IS_EQUIPMENT_FETCHED, true)));

    }

    public static void setShouldRestartTracking(Context context,
                                                boolean shouldRestartTracking) {

        SharedPreferences sp = context.getSharedPreferences(SP_APP_UPDATION,
                Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(SHOULD_RESTART_TRACKING, shouldRestartTracking);
        editor.commit();
    }

    public static boolean getShouldRestartTracking(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SP_APP_UPDATION,
                Context.MODE_PRIVATE);

        return sp.getBoolean(SHOULD_RESTART_TRACKING, false);

    }

    /**
     * Sets if the logged in is today or not.
     *
     * @param is_not_logged_in_today
     * @param context
     */
    public static void setIsNotLoggedInToday(boolean is_not_logged_in_today,
                                             Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(IS_NOT_LOGGED_IN_TODAY, is_not_logged_in_today);
        editor.commit();

    }

    /**
     * Gets if the logged in is today or not.
     *
     * @param context
     * @return
     */
    public static boolean getIsNotLoggedInToday(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);

        return sp.getBoolean(IS_NOT_LOGGED_IN_TODAY, false);

    }

    /**
     * Sets if the logged in is today or not.
     *
     * @param is_stock_selected
     * @param context
     */
    public static void setIsStockSelected(boolean is_stock_selected,
                                          Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_CURRENT_JOBS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(IS_STOCK_SELECTED, is_stock_selected);
        editor.commit();

    }

    /**
     * Gets if the logged in is today or not.
     *
     * @param context
     * @return
     */
    public static boolean getIsStockSelected(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_CURRENT_JOBS, Context.MODE_PRIVATE);

        return sp.getBoolean(IS_STOCK_SELECTED, false);

    }

    /**
     * Sets the filtered category in parts and services list.
     *
     * @param category
     * @param context
     */
    public static void setFilteredCategoryPartsServices(String category,
                                                        Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_CURRENT_JOBS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(FILTERED_CATEGORY_PARTS_SERVICES, category);
        editor.commit();

    }

    /**
     * Gets the filtered category.
     *
     * @param context
     * @return
     */
    public static String getFilteredCategoryPartsServices(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_CURRENT_JOBS, Context.MODE_PRIVATE);
        return sp.getString(FILTERED_CATEGORY_PARTS_SERVICES, "");
    }

    /**
     * Enable/Disable debug in sync.
     *
     * @param context
     */
    public static void setDebugEnableSync(boolean isDebugEnabled,
                                          Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(IS_DEBUG_ENABLED, isDebugEnabled);
        editor.commit();

    }

    /**
     * Gets debug enabled/disabled status.
     *
     * @param context
     * @return
     */
    public static boolean getDebugEnabled(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        return sp.getBoolean(IS_DEBUG_ENABLED, false);
    }


    /**
     * Enable/Disable Tracking Debug mode
     *
     * @param isTrackingDebugEnabled
     * @param context
     */
    public static void setTrackingDebugEnable(boolean isTrackingDebugEnabled, Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(IS_TRACKING_DEBUG_ENABLED, isTrackingDebugEnabled);
        editor.commit();

    }


    /**
     * Gets tracking debug enabled/disabled status
     *
     * @param context
     * @return
     */
    public static boolean getTrackingDebugEnabled(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        return sp.getBoolean(IS_TRACKING_DEBUG_ENABLED, false);
    }

    /**
     * Sets true if script is updated from 40 to 41.
     *
     * @param context
     */
    public static void setScriptUpdated(boolean isScriptUpdated,
                                        Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(IS_SCRIPT_UPDATED_FROM_40_TO_41, isScriptUpdated);
        editor.commit();

    }

    /**
     * Returns true if script is updated from 40 to 41.
     *
     * @param context
     * @return
     */
    public static boolean getScriptUpdated(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        return sp.getBoolean(IS_SCRIPT_UPDATED_FROM_40_TO_41, false);
    }

    /**
     * Sets sorting option selected.
     *
     * @param sortingOption : option selected
     * @param context       : context
     */
    public static void setSortingOption(int sortingOption,
                                        Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_CURRENT_JOBS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putInt(ALL_JOB_SORTING_OPTION, sortingOption);
        editor.commit();

    }

    /**
     * Gets sorting option
     *
     * @param context : context
     * @return sorting option
     */
    public static int getSortingOption(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_CURRENT_JOBS, Context.MODE_PRIVATE);
        return sp.getInt(ALL_JOB_SORTING_OPTION, KEYS.AllJobSortingOptions.SORT_BY_DATE);
    }

    /**
     * Sets job number for sorting.
     *
     * @param jobNumber
     * @param context
     */
    public static void setSortedJobNumber(String jobNumber,
                                          Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_CURRENT_JOBS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(SORTED_JOB_NUMBER, jobNumber);
        editor.commit();

    }

    /**
     * Gets sorting option
     *
     * @param context : context
     * @return sorting option
     */
    public static String getSortedJobNumber(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_CURRENT_JOBS, Context.MODE_PRIVATE);
        return sp.getString(SORTED_JOB_NUMBER, "0");
    }

    /**
     * Sets true if camera permission was granted
     *
     * @param context
     */
    public static void setPermissionCamera(boolean isGranted,
                                           Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_CURRENT_JOBS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(PERMISSION_CAMERA, isGranted);
        editor.commit();

    }

    /**
     * Returns true if camera permission was granted
     *
     * @param context
     * @return
     */
    public static boolean getPermissionCamera(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_CURRENT_JOBS, Context.MODE_PRIVATE);
        return sp.getBoolean(PERMISSION_CAMERA, false);
    }


    public static void setSyncDebugSsl(boolean debugSSL,
                                       Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(SYNC_DEBUG_SSL, debugSSL);
        editor.commit();

    }

    public static boolean getSyncDebugSsl(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        return sp.getBoolean(SYNC_DEBUG_SSL, false);
    }

    public static void setSyncDebugPort(int debugPort,
                                        Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putInt(SYNC_DEBUG_PORT, debugPort);
        editor.commit();

    }

    public static int getSyncDebugPort(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        return sp.getInt(SYNC_DEBUG_PORT, 0);
    }

    public static void setSyncDebugServer(String debugServer,
                                          Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(SYNC_DEBUG_SERVER, debugServer);
        editor.commit();

    }

    public static String getSyncDebugServer(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        return sp.getString(SYNC_DEBUG_SERVER, null);
    }

    public static void setSyncStdSsl(boolean stdSSL,
                                     Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(SYNC_STD_SSL, stdSSL);
        editor.commit();

    }

    public static boolean getSyncStdSsl(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        return sp.getBoolean(SYNC_STD_SSL, false);
    }

    public static void setSyncStdPort(int stdPort,
                                      Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putInt(SYNC_STD_PORT, stdPort);
        editor.commit();

    }

    public static int getSyncStdPort(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        return sp.getInt(SYNC_STD_PORT, 0);
    }

    public static void setSyncStdServer(String stdServer,
                                        Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(SYNC_STD_SERVER, stdServer);
        editor.commit();

    }

    public static String getSyncStdServer(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        return sp.getString(SYNC_STD_SERVER, null);
    }

    public static void setTimeOutEnabled(boolean isTimeOutEnabled,
                                         Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(IS_TIME_OUT_ENABLED, isTimeOutEnabled);
        editor.commit();
    }

    public static boolean getTimeOutEnabled(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        return sp.getBoolean(IS_TIME_OUT_ENABLED, false);
    }

    public static void setClockedInTime(String clockedInTime,
                                        Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(CLOCKED_IN_TIME, clockedInTime);
        editor.commit();
    }

    public static String getClockedInTime(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        return sp.getString(CLOCKED_IN_TIME, null);
    }

//    public static void setDistanceTravelled(String distance,
//                                            Context context) {
//        SharedPreferences sp = context.getSharedPreferences(
//                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
//        Editor editor = sp.edit();
//        editor.putString(DISTANCE_TRAVELLED, distance);
//        editor.commit();
//
//    }

//    public static String getDistanceTravelled(Context context) {
//        SharedPreferences sp = context.getSharedPreferences(
//                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
//        return sp.getString(DISTANCE_TRAVELLED, null);
//    }

    public static void setDistanceEnabled(boolean isEnabled,
                                          Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(IS_DRIVING_ENABLED, isEnabled);
        editor.commit();

    }

    public static boolean getDistanceEnabled(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        return sp.getBoolean(IS_DRIVING_ENABLED, false);
    }


    /**
     * Sets take back part detail.
     *
     * @param key     : serial piece id
     * @param value   : serial parts details in json
     * @param context : context
     */
    public static void setTakeBackPartSharedPref(String key, String value,
                                                 Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_TAKE_BACK_PART, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.commit();

    }

    /**
     * Gets take back part detail
     *
     * @param context : context
     * @return serial parts details in json
     */
    public static String getTakeBackPartSharedPref(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_TAKE_BACK_PART, Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    /**
     * Remove take back part detail
     *
     * @param context : context
     * @return serial parts details in json
     */
    public static void removeTakeBackPartSharedPref(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_TAKE_BACK_PART, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        editor.commit();
    }

    public static void setUrlStripeServer(String urlStripe,
                                          Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(SYNC_URL_STRIPE_SERVER, urlStripe);
        editor.commit();

    }

    public static String getUrlStripeServer(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        return sp.getString(SYNC_URL_STRIPE_SERVER, null);
    }

    public static void setNotiUrlServer(String notiUrl,
                                        Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(SYNC_NOTI_URL_SERVER, notiUrl);
        editor.commit();

    }

    public static String getNotiUrlServer(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        return sp.getString(SYNC_NOTI_URL_SERVER, null);
    }


    public static void setJobPoolUrlServer(String jobPoolUrl,
                                           Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(SYNC_JOB_POOL_URL_SERVER, jobPoolUrl);
        editor.commit();

    }

    public static String getJobPoolUrlServer(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        return sp.getString(SYNC_JOB_POOL_URL_SERVER, null);
    }

    public static void setPreviousTimeOut(String prevTimeOut,
                                          Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(IS_PREVIOUS_TIME_OUT, prevTimeOut);
        editor.commit();
    }

    public static String getPreviousTimeOut(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        return sp.getString(IS_PREVIOUS_TIME_OUT, null);
    }

    /**
     * Sets the is travel running.
     *
     * @param isTravelRunning the is travel running
     * @param context         the context
     */
    public static void setIsTravelRunning(boolean isTravelRunning,
                                          Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_TRACKING_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(TRAVEL_INDICATOR_ON_OFF, isTravelRunning);
        editor.commit();

    }

    /**
     * Gets the checks if is travel running.
     *
     * @param context the context
     * @return the checks if is travel running
     */
    public static boolean getIsTravelRunning(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_TRACKING_SETTINGS, Context.MODE_PRIVATE);
        return sp.getBoolean(TRAVEL_INDICATOR_ON_OFF, false);

    }

    public static void setRunningTravelName(String travelName,
                                            Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_TRACKING_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(TRAVEL_NAME_CURRENT, travelName);
        editor.commit();
    }

    public static String getRunningTravelName(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_TRACKING_SETTINGS, Context.MODE_PRIVATE);
        return sp.getString(TRAVEL_NAME_CURRENT, "");
    }


    /**
     * Sets the is travel entry.
     *
     * @param isTravelRunning the is travel running
     * @param context         the context
     */
    public static void setIsTravelLastEntry(boolean isTravelRunning,
                                            Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_TRACKING_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(TRAVEL_INDICATOR_ON_OFF, isTravelRunning);
        editor.commit();

    }

    /**
     * Gets the checks if is travel running.
     *
     * @param context the context
     * @return the checks if is travel running
     */
    public static boolean getIsTravelLastEntry(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_TRACKING_SETTINGS, Context.MODE_PRIVATE);
        return sp.getBoolean(TRAVEL_INDICATOR_ON_OFF, false);

    }


    public static void setTravelLocationData(String travelName,
                                             Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_TRACKING_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(TRAVEL_LOC_DATA, travelName);
        editor.commit();
    }

    public static String getTravelLocationData(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_TRACKING_SETTINGS, Context.MODE_PRIVATE);
        return sp.getString(TRAVEL_LOC_DATA, "");
    }

    public static String getUrlMobileAuth(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        return sp.getString(SYNC_MOBILE_AUTH_SERVER, "");
    }

    public static void setUrlMobileAuth(String urlbase,
                                        Context context) {
            SharedPreferences sp = context.getSharedPreferences(
                    SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
            Editor editor = sp.edit();
            editor.putString(SYNC_MOBILE_AUTH_SERVER, urlbase);
            editor.commit();
    }

    public static void setUrlBaseServer(String urlbase,
                                        Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(SYNC_URL_BASE_SERVER, urlbase);
        editor.commit();

    }

    public static String getUrlBaseServer(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_SYNCRONIZATION_SETTINGS, Context.MODE_PRIVATE);
        return sp.getString(SYNC_URL_BASE_SERVER, null);
    }

    /**
     * Sets the is travel entry.
     *
     * @param isTravelRunning the is travel running
     * @param context         the context
     */
    public static void setIsEmptyValue(boolean isTravelRunning,
                                       Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_TRACKING_SETTINGS, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putBoolean(IS_EMPTY_VALUE, isTravelRunning);
        editor.commit();

    }

    /**
     * Gets the checks if is travel running.
     *
     * @param context the context
     * @return the checks if is travel running
     */
    public static boolean getIsSetIsEmptyValue(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                SP_NAME_TRACKING_SETTINGS, Context.MODE_PRIVATE);
        return sp.getBoolean(IS_EMPTY_VALUE, false);

    }

    public static void setLoginUser(String userName,
                                    Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                LOGIN_USER_SETT, Context.MODE_PRIVATE);
        Editor editor = sp.edit();
        editor.putString(IS_LOGIN_USER, userName);
        editor.commit();
    }

    public static String getLoginUSer(Context context) {
        SharedPreferences sp = context.getSharedPreferences(
                LOGIN_USER_SETT, Context.MODE_PRIVATE);
        return sp.getString(IS_LOGIN_USER, null);
    }
}
