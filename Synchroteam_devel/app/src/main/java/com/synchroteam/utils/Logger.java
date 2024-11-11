package com.synchroteam.utils;

import android.util.Log;

/**
 * Logger class of the application to show Logs. * created for future use
 *
 * @author Ideavate.solution
 */
public class Logger {

    /**
     * Log.
     *
     * @param tag     the tag
     * @param message the message
     */
    public static void log(String tag, String message) {

        Log.e(tag, message);

    }

    /**
     * Log.
     *
     * @param tag     the tag
     * @param message the message
     */
    public static void output(String tag, String message) {

        Log.e(tag, message);

    }

    /**
     * Prints the exception.
     *
     * @param e the e
     */
    public static void printException(Exception e) {
        e.printStackTrace();
//        Crashlytics.logException(e);

    }



}
