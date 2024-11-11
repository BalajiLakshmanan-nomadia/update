package com.synchroteam.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.util.LocaleData;
import android.icu.util.ULocale;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.LocaleList;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import com.synchroteam.synchroteam3.R;

import static android.content.Context.LOCATION_SERVICE;

import java.util.Locale;

/**
 * Created by Trident on 10/31/2017.
 */

public class LocationUtils {

    public static double distanceBetweenTwoPoint(double srcLat, double srcLng, double desLat, double desLng) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(desLat - srcLat);
        double dLng = Math.toRadians(desLng - srcLng);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(srcLat))
                * Math.cos(Math.toRadians(desLat)) * Math.sin(dLng / 2)
                * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        double meterConversion = 1609;

        return (int) (dist * meterConversion);
    }

    public static double distanceBetweenTwoPointAndroid(double srcLat, double srcLng, double desLat, double desLng) {

        double actualDistance = 0;

//        LatLng latLngA = new LatLng(srcLat,srcLng);
//        LatLng latLngB = new LatLng(desLat,desLng);
        Location locationA = new Location("point A");
        locationA.setLatitude(srcLat);
        locationA.setLongitude(srcLng);
        Location locationB = new Location("point B");
        locationB.setLatitude(desLat);
        locationB.setLongitude(desLng);

        double distanceInMeter = locationA.distanceTo(locationB);

//        Locale locale = Locale.forLanguageTag(Locale.getDefault().getCountry());
        Locale locale = Locale.getDefault();
        Log.e("LOCALE","THE LOCALE IS >>>>>>>"+ locale);

        String countryCode = Locale.getDefault().getCountry();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            LocaleData.MeasurementSystem system = LocaleData.getMeasurementSystem(ULocale.forLocale(locale));

            if(system.equals(LocaleData.MeasurementSystem.SI)){
                actualDistance = distanceInMeter/1000;
            }else if(system.equals(LocaleData.MeasurementSystem.UK)){
                actualDistance = distanceInMeter/1000;
            }else if(system.equals(LocaleData.MeasurementSystem.US)){
                actualDistance = distanceInMeter/1609;
            }
        }else {
            switch (countryCode) {
                case "US":
                case "LR":
                case "MM":
                    actualDistance = distanceInMeter / 1609;
                    break;
                default:
                    actualDistance = distanceInMeter / 1000;
                    break;
            }
        }
        return actualDistance;

//        return locationA.distanceTo(locationB);
    }

    public static boolean isSameLocation(double srcLat, double srcLng, double desLat, double desLng) {

//        LatLng latLngA = new LatLng(srcLat,srcLng);
//        LatLng latLngB = new LatLng(desLat,desLng);
        Location locationA = new Location("point A");
        locationA.setLatitude(srcLat);
        locationA.setLongitude(srcLng);
        Location locationB = new Location("point B");
        locationB.setLatitude(desLat);
        locationB.setLongitude(desLng);
        if (locationB.distanceTo(locationA) < 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if the location serivce is turned or not
     *
     * @param context
     * @return
     */
    public static boolean checkLocationServices(Activity context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        if (servicesConnected(context)) {
            boolean isNetEnabled = Build.FINGERPRINT.contains("generic") ? true : locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    || isNetEnabled) {
                return true;
            } else {
                showLocationSettingDialog(context);
            }
        }
        return false;
    }

    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private static boolean servicesConnected(Activity context) {

        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(context);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status

            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode,
                    context, 0);
            dialog.show();

            return false;
        }
    }

    /**
     * Show location setting dialog.
     */
    protected static void showLocationSettingDialog(final Activity context) {
        // TODO Auto-generated method stub
        new AlertDialog.Builder(context)
                .setTitle(
                        context
                                .getString(R.string.textEnableLocationServiceTracking))
                .setMessage(
                        context
                                .getString(R.string.textEnableLocationServiceText))
                .setPositiveButton(
                        context.getString(R.string.textOkLable).toUpperCase(),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // show system settings
                                context.startActivity(new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(
                        context
                                .getString(R.string.textDontAllowTracking),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }

    public static String stringKMorMi(){

        String unit = null;
        Locale locale = Locale.getDefault();

        String countryCode = Locale.getDefault().getCountry();
        Log.e("UNIT","THE COUNTRY IS>>>>>>>>"+countryCode);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            LocaleData.MeasurementSystem system = LocaleData.getMeasurementSystem(ULocale.forLocale(locale));

            if(system.equals(LocaleData.MeasurementSystem.US)){
                unit = "mi";
            }else if(system.equals(LocaleData.MeasurementSystem.UK)){
                unit = "km";
            }else if(system.equals(LocaleData.MeasurementSystem.SI)){
                unit = "km";
            }
        }else {
            switch (countryCode){
                case "US":
                case "LR":
                case "MM":
                    unit = "mi";
                    break;
                default:unit = "km";
                    break;
            }
        }
        Log.e("UNIT","THE UNIT METHOD STRING IS>>>>>>>>>>"+unit);
        return unit;
    }
}
