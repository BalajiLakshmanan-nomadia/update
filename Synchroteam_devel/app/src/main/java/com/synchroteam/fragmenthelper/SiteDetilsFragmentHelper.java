package com.synchroteam.fragmenthelper;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.Site;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.CallingOptionDialog;
import com.synchroteam.dialogs.ErrorDialog;
import com.synchroteam.fragment.SiteDetailFragment;
import com.synchroteam.synchroteam.ClientDetail;
import com.synchroteam.synchroteam.SiteDetail;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.PackageInstallationUtil;
import com.synchroteam.utils.SynchroteamUitls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

// TODO: Auto-generated Javadoc

/**
 * The Class SiteDetilsFragmentHelper is to inflate and control all the actions
 * performed in site description screen.
 *
 * @author Trident.
 */
public class SiteDetilsFragmentHelper implements HelperInterface {

    private int siteId, clientId;

    /**
     * The data access object.
     */
    private Dao dataAccessObject;

    /**
     * The view.
     */
    private View view;

    private SiteDetail syncoteamNavigationActivity;

    private TextView clientNameDataTv, dataSitelableTv, addressDataTv,
            clientEmailDataTv, jobPhoneDataTv, jobMobileDataTv, dataContactInformationTv;
    private ImageView locationSiteIv;
    private ImageView dividerLineMobile;

    private Site site;

    private ScrollView containerClientDetials;
    private ProgressBar progresBar;

    private ImageView cloudClientIconIv, ivSitePublicLink, emailClientIconIv, phoneIconIv, mobileIconIv;
    private LinearLayout containerJobInformation, containerClientEmail,
            containerPhone, containerMobile;

    private String clientName;

    private PhoneNumberUtil phoneFormatUtil = null;

    private String callPhone = "";
    SiteDetailFragment siteDetailFragment;

    /**
     * Instantiates a new client detial fragment helper
     *
     * @param
     */
    public SiteDetilsFragmentHelper(SiteDetail syncoteamNavigationActivity,
                                    int clientid, int siteId, String clientName, SiteDetailFragment siteDetailFragment) {
        // TODO Auto-generated constructor stub

        this.dataAccessObject = DaoManager.getInstance();
        this.clientId = clientid;
        this.siteId = siteId;
        this.syncoteamNavigationActivity = syncoteamNavigationActivity;
        this.clientName = clientName;
        this.siteDetailFragment = siteDetailFragment;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            if (phoneFormatUtil == null) {
                phoneFormatUtil = PhoneNumberUtil.createInstance(syncoteamNavigationActivity);
            }
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
        View view = inflater.inflate(R.layout.layout_site_detail, null);
        initiateView(view);

        this.view = view;
        return view;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.synchroteam.fragmenthelper.HelperInterface#doOnSyncronize()
     */
    @Override
    public void doOnSyncronize() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.synchroteam.fragmenthelper.HelperInterface#onReturnToActivity(int)
     */
    @Override
    public void onReturnToActivity(int requestCode) {
        // TODO Auto-generated method stub

    }

    /**
     * Open maps.
     */
    public void openMaps() {

        if (isIntentAvailable(syncoteamNavigationActivity,
                "android.intent.action.navigon.START_PUBLIC")) {
            AlertDialog alert = new myCustomAlertDialog(
                    syncoteamNavigationActivity);
            alert.show();
        } else {

            if (!SynchroteamUitls
                    .isGoogleMapInstalled(syncoteamNavigationActivity)) {
                SynchroteamUitls
                        .showGoogleMapsDialog(syncoteamNavigationActivity);

                return;
            }
            try {
                if ((!TextUtils.isEmpty(site.getGpsX()))
                        && (!TextUtils.isEmpty(site.getGpsY()))) {
                    Intent myIntent = new Intent(
                            android.content.Intent.ACTION_VIEW,
                            Uri.parse("geo:0,0?q=" + site.getGpsY() + ","
                                    + site.getGpsX() + "&z=20"));
                    syncoteamNavigationActivity.startActivity(myIntent);
                } else {
                    Intent myIntent = new Intent(
                            android.content.Intent.ACTION_VIEW,
                            Uri.parse("geo:0,0?q=" + site.getAdresse()
                                    + "&z=20"));
                    syncoteamNavigationActivity.startActivity(myIntent);
                }
            } catch (Exception e) {
                Logger.printException(e);
            }
        }

    }

    /**
     * The Class myCustomAlertDialog.
     */
    private class myCustomAlertDialog extends AlertDialog {

        /**
         * Instantiates a new my custom alert dialog.
         *
         * @param context the context
         */
        protected myCustomAlertDialog(Context context) {
            super(context);

            LayoutInflater factory = LayoutInflater.from(context);
            View alertDialogView3 = factory.inflate(R.layout.nav_list, null);

            ListView maListViewPerso = (ListView) alertDialogView3
                    .findViewById(R.id.list_view_navigation);
            maListViewPerso.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> a, View v, int position,
                                        long id) {

                    if (position == 0) {
                        try {
                            if ((!TextUtils.isEmpty(site.getGpsX()))
                                    && (!TextUtils.isEmpty(site.getGpsY()))) {
                                syncoteamNavigationActivity
                                        .startActivity(getMapsIntent(
                                                syncoteamNavigationActivity,
                                                "geo:0,0?q=" + site.getGpsX()
                                                        + "," + site.getGpsY()
                                                        + "&z=16"));
                            } else {
                                syncoteamNavigationActivity
                                        .startActivity(getMapsIntent(
                                                syncoteamNavigationActivity,
                                                "geo:0,0?q="
                                                        + site.getAdresse()
                                                        + "&z=16"));
                            }

                        } catch (Exception e) {
                            Logger.printException(e);
                        } finally {

                            dismiss();
                        }

                    } else {
                        try {
                            if ((!TextUtils.isEmpty(site.getGpsX()))
                                    && (!TextUtils.isEmpty(site.getGpsY()))) {
                                String INTENT_EXTRA_KEY_LATITUDE = "latitude";
                                String INTENT_EXTRA_KEY_LONGITUDE = "longitude";
                                Intent myIntent = new Intent(
                                        "android.intent.action.navigon.START_PUBLIC");
                                myIntent.putExtra(INTENT_EXTRA_KEY_LATITUDE,
                                        Float.valueOf(site.getGpsX()));
                                myIntent.putExtra(INTENT_EXTRA_KEY_LONGITUDE,
                                        Float.valueOf(site.getGpsY()));
                                syncoteamNavigationActivity
                                        .startActivity(myIntent);
                            } else {
                                String INTENT_EXTRA_KEY_FREE_TEXT_ADDRESS = "free_text_address";
                                Intent myIntent = new Intent(
                                        "android.intent.action.navigon.START_PUBLIC");
                                myIntent.putExtra(
                                        INTENT_EXTRA_KEY_FREE_TEXT_ADDRESS,
                                        site.getAdresse());
                                syncoteamNavigationActivity
                                        .startActivity(myIntent);
                            }

                        } catch (Exception e) {
                            Logger.printException(e);
                        } finally {
                            dismiss();
                        }
                    }
                }
            });
            ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
            HashMap<String, String> map;

            map = new HashMap<String, String>();
            map.put("item", "Google Maps");
            map.put("img", String.valueOf(R.drawable.maps));
            listItem.add(map);

            map = new HashMap<String, String>();
            map.put("item", "Navigon");
            map.put("img", String.valueOf(R.drawable.navig));
            listItem.add(map);

            SimpleAdapter adapter = new SimpleAdapter(
                    alertDialogView3.getContext(), listItem,
                    R.layout.nav_list_item, new String[]{"img", "item"},
                    new int[]{R.id.imgNav, R.id.textNav});

            maListViewPerso.setAdapter(adapter);
            setView(alertDialogView3);
            setTitle(R.string.naviguer_par);
            setIcon(android.R.drawable.ic_dialog_info);

        }

    }

    /**
     * Gets the maps intent.
     *
     * @param context the context
     * @param url     the url
     * @return the maps intent
     */
    public static Intent getMapsIntent(Context context, String url) {
        Intent videoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        final PackageManager pm = context.getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(videoIntent,
                0);
        for (int i = 0; i < activityList.size(); i++) {
            ResolveInfo app = activityList.get(i);
            if (app.activityInfo.name.contains("MapsActivity")) {
                videoIntent.setClassName(app.activityInfo.packageName,
                        app.activityInfo.name);
                return videoIntent;
            }
        }
        return videoIntent;
    }

    /**
     * Checks if is intent available.
     *
     * @param context the context
     * @param action  the action
     * @return true, if is intent available
     */
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /**
     * Fetches the client detail form data base and show data on UI.
     *
     * @author Ambesh.Kukreja
     */

    private class FetchSiteDetialAsyncTask extends
            AsyncTaskCoroutine<Void, Boolean> {

        @Override
        public void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            containerClientDetials.setVisibility(View.GONE);
            progresBar.setVisibility(View.VISIBLE);

        }

        @Override
        public Boolean doInBackground(Void... params) {
            // TODO Auto-generated method stub

            try {
                site = dataAccessObject.getSiteDetail(siteId);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                Logger.printException(e);
            }

            if (site != null) {

                return Boolean.valueOf(true);

            } else {
                return Boolean.valueOf(false);
            }

        }

        @Override
        public void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            containerClientDetials.setVisibility(View.VISIBLE);
            progresBar.setVisibility(View.GONE);

            if (result.booleanValue()) {
                if (site.getRefCustomer().length()>0){
                    dataSitelableTv.setText(site.getNmSite()+" ("+site.getRefCustomer()+")");
                }else {
                    dataSitelableTv.setText(site.getNmSite());
                }
                addressDataTv.setText(site.getAdresse());
                clientNameDataTv.setText(clientName);

                if (!TextUtils.isEmpty(site.getEmailContactSite()))
                    clientEmailDataTv.setText(site.getEmailContactSite());
                else {
                    containerClientEmail = (LinearLayout) view
                            .findViewById(R.id.containerClientEmail);
                    containerClientEmail.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(site.getTelephoneContactSite())) {

                    jobPhoneDataTv.setText(formatPhoneNumber(site.getTelephoneContactSite()));

                } else {
                    containerPhone = (LinearLayout) view
                            .findViewById(R.id.containerPhone);
                    containerPhone.setVisibility(View.GONE);
                    ImageView dividerLinePhone = (ImageView) view
                            .findViewById(R.id.dividerLinePhone);
                    dividerLinePhone.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(site.getMobileContactSite())) {
                    containerMobile = (LinearLayout) view
                            .findViewById(R.id.containerMobile);
                    containerMobile.setVisibility(View.VISIBLE);
                    jobMobileDataTv.setText(formatPhoneNumber(site.getMobileContactSite()));

                    dividerLineMobile.setVisibility(View.VISIBLE);
                } else {
                    containerMobile = (LinearLayout) view
                            .findViewById(R.id.containerMobile);
                    containerMobile.setVisibility(View.GONE);
                    ImageView dividerLineMobile = (ImageView) view
                            .findViewById(R.id.dividerLineMobile);
                    dividerLineMobile.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(site.getPreNomContactSite())
                        || !TextUtils.isEmpty(site.getPreNomContactSite()))
                    dataContactInformationTv.setText(site
                            .getPreNomContactSite()
                            + " "
                            + site.getNomContactSite());
                else {
                    containerJobInformation = (LinearLayout) view
                            .findViewById(R.id.containerJobInformation);
                    containerJobInformation.setVisibility(View.GONE);

                }

                if (!TextUtils.isEmpty(site.getPublicLink())) {
                    ivSitePublicLink.setVisibility(View.VISIBLE);
                } else {
                    ivSitePublicLink.setVisibility(View.GONE);
                }

            } else {

            }

            logicForShowPublicLink();
        }

    }

    public String formatPhoneNumber(String phoneNo) {
        String updatedPhoneNo = "";
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            updatedPhoneNo = phoneNo;
        } else {
            if (phoneNo.trim().startsWith("+") && !phoneNo.trim().contains(" ")) {
                if (phoneFormatUtil == null) {
                    phoneFormatUtil = PhoneNumberUtil.createInstance(syncoteamNavigationActivity);
                }
                try {
                    // phone must begin with '+'
                    Phonenumber.PhoneNumber numberProto = phoneFormatUtil.parse(phoneNo, "");
                    int countryCode = numberProto.getCountryCode();
                    long nationalNo = numberProto.getNationalNumber();


//                    updatedPhoneNo=numberProto.toString();
                    updatedPhoneNo = phoneFormatUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
                } catch (Exception e) {
                    updatedPhoneNo = phoneNo;
                }


            } else {
                updatedPhoneNo = phoneNo;
            }
        }
        return updatedPhoneNo;
    }

    @Override
    public void initiateView(View v) {

        // TODO Auto-generated method stub

        clientNameDataTv = (TextView) v.findViewById(R.id.clientNameDataTv);
        dataSitelableTv = (TextView) v.findViewById(R.id.dataSitelableTv);
        addressDataTv = (TextView) v.findViewById(R.id.addressDataTv);
        locationSiteIv = (ImageView) v.findViewById(R.id.locationSiteIv);
        containerClientDetials = (ScrollView) v
                .findViewById(R.id.containerClientDetials);
        progresBar = (ProgressBar) v.findViewById(R.id.progresBar);
        locationSiteIv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                openMaps();

            }
        });

        dataAccessObject = DaoManager.getInstance();

        cloudClientIconIv = (ImageView) v.findViewById(R.id.cloudClientIconIv);
        ivSitePublicLink = (ImageView) v.findViewById(R.id.sitePublicLinkIv);

        cloudClientIconIv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(syncoteamNavigationActivity,
                        ClientDetail.class);

                intent.putExtra(KEYS.ClientDetial.ID_CLIENT, clientId);
                intent.putExtra(KEYS.ClientDetial.CLIENT_NAME, clientName);

                syncoteamNavigationActivity.startActivity(intent);

            }
        });

        ivSitePublicLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (site != null) {
                    boolean isSubContr = isSubContractor();
                    if (!isSubContr)
                    openLinkInBrowser(site.getPublicLink());
                }
            }
        });
        clientEmailDataTv = (TextView) v.findViewById(R.id.clientEmailDataTv);
        jobPhoneDataTv = (TextView) v.findViewById(R.id.jobPhoneDataTv);
        jobMobileDataTv = (TextView) v.findViewById(R.id.jobMobileDataTv);
        dividerLineMobile = (ImageView) v.findViewById(R.id.dividerLineMobile);
        dataContactInformationTv = (TextView) v
                .findViewById(R.id.dataContactInformationTv);

        emailClientIconIv = (ImageView) v.findViewById(R.id.emailClientIconIv);
        emailClientIconIv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if ((site != null)
                        && (!TextUtils.isEmpty(site.getEmailContactSite()))) {
                    Intent email = new Intent(Intent.ACTION_SEND);
                    String emailAddress = site.getEmailContactSite().trim();
                    email.putExtra(Intent.EXTRA_EMAIL,
                            new String[]{emailAddress});
                    // email.putExtra(Intent.EXTRA_SUBJECT, "subject");
                    // email.putExtra(Intent.EXTRA_TEXT, "message");
                    email.setType("message/rfc822");
                    syncoteamNavigationActivity.startActivity(Intent
                            .createChooser(email, "Choose an Email client :"));
                }

            }
        });

        phoneIconIv = (ImageView) v.findViewById(R.id.phoneIconIv);
        phoneIconIv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if ((site != null)
                        && (!TextUtils.isEmpty(site.getTelephoneContactSite()))) {
                    if (validCellPhone(site.getTelephoneContactSite())) {
                        String mobileNo = site.getTelephoneContactSite().trim();
                        callPhone = mobileNo;
                        boolean installed = PackageInstallationUtil.whatsAppInstalledOrNot("com.whatsapp",syncoteamNavigationActivity);
                        if (installed) {
                            CallingOptionDialog callingOptionDialog = new CallingOptionDialog(syncoteamNavigationActivity, mobileNo);
                            callingOptionDialog.show();
                        }else {
                            if (ContextCompat.checkSelfPermission(syncoteamNavigationActivity, Manifest.permission.CALL_PHONE)
                                    != PackageManager.PERMISSION_GRANTED) {

                                siteDetailFragment.callingPermissionPhone();
                            } else {
                                callingMethod();
                            }
                        }

                    } else {
                        showErrMsgDialog(syncoteamNavigationActivity.getResources().getString(R.string
                                .error_invalid_phone));
                    }
                }

            }
        });

        mobileIconIv = (ImageView) v.findViewById(R.id.mobileIconIv);
        mobileIconIv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if ((site != null)
                        && (!TextUtils.isEmpty(site.getMobileContactSite()))) {
                    if (validCellPhone(site.getTelephoneContactSite())) {
                        String mobileNo = site.getMobileContactSite().trim();
//                        Intent callIntent = new Intent(Intent.ACTION_CALL);
//                        callIntent.setData(Uri.parse("tel:" + mobileNo));
//                        syncoteamNavigationActivity.startActivity(callIntent);
                        callPhone = mobileNo;
                        boolean installed = PackageInstallationUtil.whatsAppInstalledOrNot("com.whatsapp",syncoteamNavigationActivity);
                        if (installed) {
                            CallingOptionDialog callingOptionDialog = new CallingOptionDialog(syncoteamNavigationActivity, mobileNo);
                            callingOptionDialog.show();
                        }else {
                            if (ContextCompat.checkSelfPermission(syncoteamNavigationActivity, Manifest.permission.CALL_PHONE)
                                    != PackageManager.PERMISSION_GRANTED) {

                                siteDetailFragment.callingPermissionPhone();
                            } else {
                                callingMethod();
                            }
                        }

                    } else {
                        showErrMsgDialog(syncoteamNavigationActivity.getResources().getString(R.string
                                .error_invalid_phone));
                    }
                }

            }
        });


        containerClientEmail = (LinearLayout) v
                .findViewById(R.id.containerClientEmail);
        containerPhone = (LinearLayout) v.findViewById(R.id.containerPhone);
        containerMobile = (LinearLayout) v.findViewById(R.id.containerMobile);



        containerPhone.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if ((site != null)
                        && (!TextUtils.isEmpty(site.getTelephoneContactSite()))) {
                    if (validCellPhone(site.getTelephoneContactSite())) {
                        String mobileNo = site.getTelephoneContactSite().trim();
                        callPhone = mobileNo;
                        if (ContextCompat.checkSelfPermission(syncoteamNavigationActivity, Manifest.permission.CALL_PHONE)
                                != PackageManager.PERMISSION_GRANTED) {

                            siteDetailFragment.callingPermissionPhone();
                        } else {
                            callingMethod();
                        }

                    } else {
                        showErrMsgDialog(syncoteamNavigationActivity.getResources().getString(R.string
                                .error_invalid_phone));
                    }
                }

            }
        });

        containerMobile.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if ((site != null)
                        && (!TextUtils.isEmpty(site.getMobileContactSite()))) {
                    if (validCellPhone(site.getTelephoneContactSite())) {
                        String mobileNo = site.getMobileContactSite().trim();
                        callPhone = mobileNo;
                        if (ContextCompat.checkSelfPermission(syncoteamNavigationActivity, Manifest.permission.CALL_PHONE)
                                != PackageManager.PERMISSION_GRANTED) {

                            siteDetailFragment.callingPermissionPhone();
                        } else {
                            callingMethod();
                        }

                    } else {
                        showErrMsgDialog(syncoteamNavigationActivity.getResources().getString(R.string
                                .error_invalid_phone));
                    }
                }

            }
        });
        containerClientEmail.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if ((site != null)
                        && (!TextUtils.isEmpty(site.getEmailContactSite()))) {
                    Intent email = new Intent(Intent.ACTION_SEND);
                    String emailAddress = site.getEmailContactSite().trim();
                    email.putExtra(Intent.EXTRA_EMAIL,
                            new String[]{emailAddress});
                    email.setType("message/rfc822");
                    syncoteamNavigationActivity.startActivity(Intent
                            .createChooser(email, "Choose an Email client :"));
                }

            }
        });

        new FetchSiteDetialAsyncTask().execute();
    }

    private void logicForShowPublicLink() {
        //hide'show logic
        boolean isSubContr=isSubContractor();
        if(!isSubContr)
            ivSitePublicLink.setVisibility(View.VISIBLE);
        else
            ivSitePublicLink.setVisibility(View.GONE);
    }

    @SuppressLint("MissingPermission")
    public void callingMethod() {
        String phoneNo = callPhone;
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNo));
        syncoteamNavigationActivity.startActivity(callIntent);
    }

    /**
     * Show error dialog
     */
    protected void showErrMsgDialog(String errMsg) {

        ErrorDialog errDialog = new ErrorDialog(
                syncoteamNavigationActivity, errMsg);

        errDialog.show();
    }

    public boolean validCellPhone(String number) {
        return android.util.Patterns.PHONE.matcher(number).matches();
    }

    /***
     * Create a chooser of browsers to open the link
     *
     * @param link
     */
    protected void openLinkInBrowser(String link) {
        // TODO Auto-generated method stub
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(link));

        // Always use string resources for UI text. This says something like
        // "Share this photo with"
        String title = syncoteamNavigationActivity
                .getString(R.string.titleBrowserSelection);
        // Create and start the chooser
        Intent chooser = Intent.createChooser(intent, title);
        syncoteamNavigationActivity.startActivity(chooser);
    }

    /**
     * To check if the technician is subcontractor
     * @return result
     */
    private boolean isSubContractor() {
        boolean result=false;
        User user = dataAccessObject.getUser();

        int idProfil = user.getIdProfil();
        int flSubContractor=user.getFlSubContractor();
        if (idProfil == 4 && flSubContractor == 1) {
            result=true;
        }
        return result;
    }
}
