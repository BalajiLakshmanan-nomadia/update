package com.synchroteam.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.core.content.IntentCompat;

import com.synchroteam.beans.Conge;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.synchroteam.SpalshActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;

/**
 * In every activity's onResume, we check if the user is logged in the current date, if not we navigate the user to the login screen.
 * We do this check because, if the user minimized the app and then he reopens the app on the next day, then we have to logout the user.
 * Created by Trident on 8/17/2016.
 */
public class DateChecker {


    public static void checkDateAndNavigate(Context context, Dao dao) {
        if (context != null && dao != null) {
            //v52 changes
//            if (!dao.checkDateLogin()) {
            if (dao.checkAuthTokenLogin()) {

//                Conge vectCongeClockIn = checkClockedIn(dao);
//                if (finishClockedInMode(dao)) {
//                    String idInterv = dao.getStartedJobId();
//                    if (idInterv != null && idInterv.length() > 0) {
//                        if (dao.updateStatutInterv(4, idInterv))
//                            dao.setTimeClotIntervention(idInterv, "");
//                    }
//                }
                User user = dao.getUser();
                if(user!=null) {
                    dao.clearPassword(user.getLogin());
                    Intent intent = new Intent(context, SpalshActivity.class);
//                intent.setFlags(IntentCompat.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    //sdk 26 changes
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                }
            }
        }
    }


    private static Conge checkClockedIn(Dao dao) {

        Conge indisp;
        Vector<Conge> vectConge = dao.getClockIn();
        Enumeration<Conge> enindisp = vectConge.elements();
        while (enindisp.hasMoreElements()) {
            indisp = enindisp.nextElement();
            if (indisp.getDtFin() == null) {
                return indisp;
            }
        }
        return null;
    }

    /**
     * Finish clock in to Clocked out
     */
    private static boolean finishClockedInMode(Dao dao) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        boolean clockedOut = false;
        Vector<Conge> vectConge = dao.getClockIn();
        Enumeration<Conge> enindisp = vectConge.elements();
        Calendar cal = Calendar.getInstance();
        String currentDate = sdf.format(cal.getTime());
        while (enindisp.hasMoreElements()) {
            Conge indisp = enindisp.nextElement();
            if (indisp.getDtFin() == null) {
                clockedOut = dao.updateClockedOutEndTime(indisp.getIdConge(), currentDate);
            }
        }
        return clockedOut;
    }
}
