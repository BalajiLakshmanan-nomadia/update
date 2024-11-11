package com.synchroteam.fragmenthelper;

import java.util.Timer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.fragment.BaseFragment;
import com.synchroteam.fragment.ClientListFragment;
import com.synchroteam.fragment.ClientSectionFragment;
import com.synchroteam.fragment.EquipmentsListFragment;
import com.synchroteam.fragment.SiteListFragment;
import com.synchroteam.synchroteam.NearestClientSite;
import com.synchroteam.synchroteam.NearestClientSiteJavaClass;
import com.synchroteam.synchroteam.NearestSite;
import com.synchroteam.synchroteam.SyncoteamNavigationActivity;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.SynchroteamUitls;
import com.synchroteam.utils.TabPageIndicator;
import com.synchroteam.utils.TabPageIndicator.OnTabClickedListener;

// TODO: Auto-generated Javadoc

/**
 * The Class ClientSectionFragmentHelper is to inflate and control all the
 * actions performed in Cleint Section tab seprated screen.
 *
 * @author $Ideavate Solution
 */
public class ClientSectionFragmentHelper implements HelperInterface,
        ConnectionCallbacks, OnConnectionFailedListener {

    private ViewPager pagerClientSection;

    private String[] CONTENT = null;

    private SyncoteamNavigationActivity syncoteamNavigationActivity;
    private BaseFragment fragment;
    private TextView nearestClientClientBtn;

    /**
     * The m location request.
     */
    private LocationRequest mLocationRequest;

    /**
     * The location client.
     */
    private GoogleApiClient locationClient;

    /**
     * The location manager.
     */
    protected LocationManager locationManager;
    /**
     * The my timer.
     */
    private Timer myTimer;

    /**
     * The is location client connected.
     */
    private boolean isLocationClientConnected = false;

    private ViewAnimator viewAnimator;

    private View view;

    private Resources res;

    private TabPageIndicator tabPageIndicator;

    private ListView listView;

    /**
     * Instantiates a new client section fragment helper
     *
     * @param syncoteamNavigationActivity the syncoteamNavigationActivity
     * @param fragment                    the fragment
     */
    public ClientSectionFragmentHelper(
            SyncoteamNavigationActivity syncoteamNavigationActivity,
            BaseFragment fragment) {
        // TODO Auto-generated constructor stub

        this.syncoteamNavigationActivity = syncoteamNavigationActivity;
        this.fragment = fragment;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.synchroteam.fragmenthelper.HelperInterface#inflateLayout(android.
     * view.LayoutInflater, android.view.ViewGroup)
     */
    @Override
    public View inflateLayout(LayoutInflater inflater, ViewGroup container) {
        // TODO Auto-generated method stub
        view = inflater.inflate(R.layout.layout_client_section, null);

        viewAnimator = (ViewAnimator) view
                .findViewById(R.id.viewAnimatorClientSection);
        listView = (ListView) view.findViewById(R.id.clientListLv);

        pagerClientSection = (ViewPager) view
                .findViewById(R.id.pagerClientSection);

        nearestClientClientBtn = (TextView) view
                .findViewById(R.id.nearestClientClientBtn);

        nearestClientClientBtn.setOnClickListener(onClickListener);

        nearestClientClientBtn
                .setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            v.performClick();
                        }
                    }
                });

        pagerClientSection.setAdapter(new ClientSectionAdapter(fragment
                .getChildFragmentManager()));

        pagerClientSection.setOffscreenPageLimit(3);

        CONTENT = new String[]{
                syncoteamNavigationActivity
                        .getString(R.string.textJobCustomerLableTv).toUpperCase(),
                syncoteamNavigationActivity
                        .getString(R.string.textSiteLable).toUpperCase(),
                syncoteamNavigationActivity
                        .getString(R.string.textEquipmentLable).toUpperCase()};
        tabPageIndicator = (TabPageIndicator) view.findViewById(R.id.indicator);
        tabPageIndicator.setViewPager(pagerClientSection);
        tabPageIndicator.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // TODO Auto-generated method stub
                try {
                    InputMethodManager inputManager = (InputMethodManager) syncoteamNavigationActivity
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (syncoteamNavigationActivity.getWindow()
                            .getCurrentFocus() != null) {
                        inputManager.hideSoftInputFromWindow(
                                syncoteamNavigationActivity.getWindow()
                                        .getCurrentFocus().getWindowToken(), 0);
                    }

                    if (position == 0) {
                        nearestClientClientBtn.setText(syncoteamNavigationActivity
                                .getString(R.string.textNearestClientNewJob).toUpperCase());
                        nearestClientClientBtn.setVisibility(View.VISIBLE);
                    } else if (position == 1) {
                        nearestClientClientBtn.setText(syncoteamNavigationActivity
                                .getString(R.string.textNearestSitesNewJob).toUpperCase());
                        nearestClientClientBtn.setVisibility(View.VISIBLE);
                    } else {
                        nearestClientClientBtn.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    Logger.printException(e);
                }

            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub
                Log.e("onPageScrolled", "Called*******");

            }

            @SuppressLint("LongLogTag")
            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub
                Log.e("onPageScrollStateChanged", "Called*******");

            }
        });

        tabPageIndicator.setOnTabClicked(new OnTabClickedListener() {

            @Override
            public void onTabClicked(View view, int selectedPosition) {

                if (nearestClientClientBtn
                        .getText()
                        .toString()
                        .equalsIgnoreCase(syncoteamNavigationActivity
                                .getString(R.string.textJobCustomerLableTv).toUpperCase())) {
                    // if text is Client then Animate the view
                    nearestClientClientBtn.setText(syncoteamNavigationActivity
                            .getString(R.string.textNearestClientNewJob).toUpperCase());
                    viewAnimator.showPrevious();
                    // tabPageIndicator.selectedTab();

                } else if (nearestClientClientBtn
                        .getText()
                        .toString()
                        .equalsIgnoreCase(syncoteamNavigationActivity
                                .getString(R.string.textSiteLable).toUpperCase())) {
                    // if text is Client then Animate the view

                    nearestClientClientBtn.setText(syncoteamNavigationActivity
                            .getString(R.string.textNearestSitesNewJob).toUpperCase());
                    viewAnimator.showPrevious();
                    // tabPageIndicator.selectedTab();
                }

                if (selectedPosition == 0) {
                    nearestClientClientBtn.setText(syncoteamNavigationActivity
                            .getString(R.string.textNearestClientNewJob).toUpperCase());
                    nearestClientClientBtn.setVisibility(View.VISIBLE);
                } else if (selectedPosition == 1) {
                    nearestClientClientBtn.setText(syncoteamNavigationActivity
                            .getString(R.string.textNearestSitesNewJob).toUpperCase());
                    nearestClientClientBtn.setVisibility(View.VISIBLE);
                } else {
                    nearestClientClientBtn.setVisibility(View.GONE);
                }

            }
        });

        return view;
    }

    /**
     * On click listner
     */
    OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            if (nearestClientClientBtn
                    .getText()
                    .toString()
                    .equalsIgnoreCase(syncoteamNavigationActivity
                            .getString(R.string.textNearestClientNewJob).toUpperCase())
                    || nearestClientClientBtn
                    .getText()
                    .toString()
                    .equalsIgnoreCase(syncoteamNavigationActivity
                            .getString(R.string.textNearestSitesNewJob).toUpperCase())) {
                // Text is NearByClients or NearBySites
                geocoder(v);

            } else {

                // Text is Client
                if (nearestClientClientBtn
                        .getText()
                        .toString()
                        .equalsIgnoreCase(syncoteamNavigationActivity
                                .getString(R.string.textJobCustomerLableTv).toUpperCase())) {
                    nearestClientClientBtn.setText(syncoteamNavigationActivity
                            .getString(R.string.textNearestClientNewJob).toUpperCase());
                    viewAnimator.showPrevious();
                    // tabPageIndicator.selectedTab();
                } else if (nearestClientClientBtn
                        .getText()
                        .toString()
                        .equalsIgnoreCase(syncoteamNavigationActivity
                                .getString(R.string.textSiteLable).toUpperCase())) {
                    nearestClientClientBtn.setText(syncoteamNavigationActivity
                            .getString(R.string.textNearestSitesNewJob).toUpperCase());
                    viewAnimator.showPrevious();
                    // tabPageIndicator.selectedTab();
                }

            }

        }
    };

    /**
     * Fetches the current location using latest google play Location Service
     * and after that opens the nearest client detail screen.
     *
     * @param v the v
     */
    public void geocoder(View v) {

        if (servicesConnected() && isLocationClientConnected) {

            boolean isGPSEnabled = false;
            boolean isNetworkEnabled = false;

            try {
                locationManager = (LocationManager) syncoteamNavigationActivity
                        .getSystemService(Context.LOCATION_SERVICE);

                isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                isNetworkEnabled = Build.FINGERPRINT.contains("generic") ? true : locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


                if (!isGPSEnabled && !isNetworkEnabled) {
                    showSettingsAlert();
                } else {

                    if (ActivityCompat.checkSelfPermission(syncoteamNavigationActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(syncoteamNavigationActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ((ClientSectionFragment) fragment).callingPermissionLocation();
                    } else {
                        callingLocationFunctionalities();
                    }


                }

            } catch (Exception e) {
                Logger.printException(e);
            }

        }

    }

    @SuppressLint("MissingPermission")
    public void callingLocationFunctionalities() {

        Toast.makeText(
                syncoteamNavigationActivity,
                syncoteamNavigationActivity
                        .getString(R.string.gps_lancer),
                Toast.LENGTH_SHORT).show();

        // locationClient.requestLocationUpdates(mLocationRequest,
        // locationListener);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                locationClient, mLocationRequest, locationListener);
        final Toast tag = Toast.makeText(
                syncoteamNavigationActivity,
                syncoteamNavigationActivity
                        .getString(R.string.gps_delai),
                Toast.LENGTH_SHORT);
        if (myTimer != null) {
            myTimer.cancel();
            myTimer = new Timer();
        } else
            myTimer = new Timer();

        myTimer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                try {
                } finally {
                    DialogUtils.dismissProgressDialog();
                }
                stopUsingGPS();
                tag.show();
            }
        }, 50000);

        DialogUtils.showProgressDialog(syncoteamNavigationActivity,
                syncoteamNavigationActivity
                        .getString(R.string.textPleaseWaitLable),
                syncoteamNavigationActivity
                        .getString(R.string.textFetchingLocation),
                false);

    }

    /**
     * Stop using gps i.e stops the updates of location.
     */
    public void stopUsingGPS() {

        if ((locationClient != null) && (locationClient.isConnected())) {
            // locationClient.removeLocationUpdates(locationListener);
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    locationClient, locationListener);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.location.LocationListener#onLocationChanged(android.location.
     * Location)
     */

    /**
     * The location listener.
     */
    LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub

            myTimer.cancel();

            stopUsingGPS();

            try {
            } finally {
                DialogUtils.dismissProgressDialog();
            }

            // DialogUtils.dismissProgressDialog();
            Intent i = new Intent(syncoteamNavigationActivity,
                    NearestClientSite.class);

            Bundle bundle = new Bundle();
            if (location != null) {

                bundle.putString("Latitude",
                        String.valueOf(location.getLatitude()));
                bundle.putString("Longitude",
                        String.valueOf(location.getLongitude()));
                i.putExtras(bundle);
                // syncoteamNavigationActivity.startActivity(i);

                // set Nearest Client within the view
                // new
                // NearestClientSiteJavaClass().onCreateView(syncoteamNavigationActivity,
                // listView, String.valueOf(location.getLatitude()),
                // String.valueOf(location.getLongitude()));
                if (nearestClientClientBtn
                        .getText()
                        .toString()
                        .equalsIgnoreCase(syncoteamNavigationActivity
                                .getString(R.string.textNearestClientNewJob).toUpperCase())) {

                    new NearestClientSiteJavaClass().onCreateView(
                            syncoteamNavigationActivity, listView,
                            String.valueOf(location.getLatitude()),
                            String.valueOf(location.getLongitude()));
                    nearestClientClientBtn.setText(syncoteamNavigationActivity
                            .getString(R.string.textJobCustomerLableTv).toUpperCase());
                    // tabPageIndicator.unSelectedAllTab();
                    viewAnimator.showNext();
                } else if (nearestClientClientBtn
                        .getText()
                        .toString()
                        .equalsIgnoreCase(syncoteamNavigationActivity
                                .getString(R.string.textNearestSitesNewJob).toUpperCase())) {

                    new NearestSite().onCreateView(syncoteamNavigationActivity,
                            listView, String.valueOf(location.getLatitude()),
                            String.valueOf(location.getLongitude()));
                    nearestClientClientBtn.setText(syncoteamNavigationActivity
                            .getString(R.string.textSiteLable).toUpperCase());
                    // tabPageIndicator.unSelectedAllTab();
                    viewAnimator.showNext();
                }
            } else {
                DialogUtils.showInfoDialog(syncoteamNavigationActivity,
                        syncoteamNavigationActivity
                                .getString(R.string.locationNotAvaliableLable));

            }

        }
    };

    /**
     * Show settings alert which asks you to enable your location services.
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                syncoteamNavigationActivity);
        alertDialog.setTitle(syncoteamNavigationActivity
                .getString(R.string.textAlertLable) + "!");
        alertDialog.setMessage(syncoteamNavigationActivity
                .getString(R.string.textEnableLocationService));
        alertDialog.setPositiveButton(R.string.textYesLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        syncoteamNavigationActivity.startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton(R.string.textNoLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(syncoteamNavigationActivity);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status

            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode,
                    syncoteamNavigationActivity, 0);
            dialog.show();

            return false;
        }
    }

    /**
     * THis is the adapter for view pager its responsible for inflating
     * client,site and equipment listing fragment into this view Pager
     *
     * @author Ideavate.Solution
     */

    private class ClientSectionAdapter extends FragmentStatePagerAdapter {

        /**
         * Instantiates a new job details pager adapter.
         *
         * @param fm the fm
         */
        public ClientSectionAdapter(FragmentManager fm) {
            super(fm);
            // TODO Auto-generated constructor stub
        }

        /*
         * (non-Javadoc)
         *
         * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
         */
        @Override
        public Fragment getItem(int arg0) {
            // TODO Auto-generated method stub
            Fragment currentFragment = null;
            if (arg0 == 0) {
                currentFragment = new ClientListFragment();

            } else if (arg0 == 1) {
                currentFragment = new SiteListFragment();
            } else if (arg0 == 2) {

                currentFragment = new EquipmentsListFragment();

            }

            return currentFragment;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.support.v4.view.PagerAdapter#getCount()
         */
        @Override
        public int getCount() {
            return 3;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.support.v4.view.PagerAdapter#getPageTitle(int)
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length].toUpperCase();
        }

    }

    @Override
    public void initiateView(View v) {
        // TODO Auto-generated method stub

    }

    @Override
    public void doOnSyncronize() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onReturnToActivity(int requestCode) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onConnected(Bundle arg0) {
        // TODO Auto-generated method stub
        isLocationClientConnected = true;

    }

    /**
     * Initialising the Settings to use the new location services this function
     * is responsible to initialise the object of LocationClient,LocationRequest
     * and setting various parametes for location request object.
     */
    public void intialiseLocationClient() {
        // TODO Auto-generated method stub
        // locationClient = new LocationClient(syncoteamNavigationActivity,
        // this,
        // this);
        locationClient = new GoogleApiClient.Builder(
                syncoteamNavigationActivity).addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mLocationRequest = LocationRequest.create();

        /*
         * Set the update interval
         */
        mLocationRequest
                .setInterval(SynchroteamUitls.UPDATE_INTERVAL_IN_MILLISECONDS);

        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
        mLocationRequest
                .setFastestInterval(SynchroteamUitls.FAST_INTERVAL_CEILING_IN_MILLISECONDS);
    }

    /**
     * This fuction is called to connect to location services of Google Play
     * service by connecting through location client
     */
    public void connectToLocationClient() {
        locationClient.connect();

    }

    /**
     * This function is called to Disconnect the locationclient from location
     * Api of Google Play services.
     */
    public void disconnectToLocationClient() {

        locationClient.disconnect();

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        // TODO Auto-generated method stub

    }

}
