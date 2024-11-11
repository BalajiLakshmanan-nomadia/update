package com.synchroteam.utils;

import android.app.Activity;
import android.content.pm.PackageManager;

public class PackageInstallationUtil {

    public static boolean whatsAppInstalledOrNot(String uri, Activity activity) {
        PackageManager pm = activity.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        }
        catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }

}
