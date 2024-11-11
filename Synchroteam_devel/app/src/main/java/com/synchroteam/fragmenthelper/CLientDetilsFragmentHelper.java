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
import android.widget.SimpleAdapter;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.Client;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.CallingOptionDialog;
import com.synchroteam.fragment.ClientDetailFragment;
import com.synchroteam.synchroteam.ClientDetail;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
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
 * The Class DiscriptionJobDetailFragmentHelper is to inflate and control all
 * the actions performed in job description screen. Created For Future Purpose
 *
 * @author $Ideavate Solution
 */
public class CLientDetilsFragmentHelper implements HelperInterface {

    private int clientId;

    /**
     * The data access object.
     */
    private Dao dataAccessObject;

    /**
     * The view.
     */
    private View view;

    private ClientDetail syncoteamNavigationActivity;

    /**
     * The gestion acces.
     */
    private GestionAcces gestionAcces;

    private ProgressBar progresBar;

    private TextView clientNameDataTv, addressDataTv, dataContactInformationTv,
            jobPhoneDataTv, clientEmailDataTv, jobMobileDataTv;

    private ImageView phoneIconIv, locationSiteIv, cloudClientIconIv,
            mobileIconIv, emailClientIconIv;

    private LinearLayout containerJobInformation, containerClientEmail,
            containerPhone, containerMobile;
    private ImageView dividerLinePhone, dividerLineMobile;

    private Client client;

    private PhoneNumberUtil phoneFormatUtil = null;

    private ClientDetailFragment clientDetailFragment;

    private String callPhone = "";

    /**
     * Instantiates a new client detial fragment helper
     *
     * @param clientId
     * @param clientDetailFragment
     */
    public CLientDetilsFragmentHelper(ClientDetail syncoteamNavigationActivity,
                                      int clientId, ClientDetailFragment clientDetailFragment) {
        // TODO Auto-generated constructor stub

        this.dataAccessObject = DaoManager.getInstance();
        this.gestionAcces = dataAccessObject.getAcces();
        this.clientId = clientId;
        this.syncoteamNavigationActivity = syncoteamNavigationActivity;
        this.clientDetailFragment = clientDetailFragment;

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
        View view = inflater.inflate(
                R.layout.layout_client_discription_fragment, null);
        initiateView(view);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            if (phoneFormatUtil == null) {
                phoneFormatUtil = PhoneNumberUtil.createInstance(syncoteamNavigationActivity);
            }

        new FetchCleintDetailsAsyncTask().execute(clientId);

        this.view = view;
        logicForShowPublicLink();
        return view;
    }

    /**
     * Sets the data to view.
     *
     * @param v the new data to view
     */

    /*
     * (non-Javadoc)
     *
     * @see
     * com.synchroteam.fragmenthelper.HelperInterface#initiateView(android.view
     * .View)
     */
    @Override
    public void initiateView(View v) {
        // TODO Auto-generated method stub

        progresBar = (ProgressBar) v.findViewById(R.id.progresBar);
        clientNameDataTv = (TextView) v.findViewById(R.id.clientNameDataTv);
        addressDataTv = (TextView) v.findViewById(R.id.addressDataTv);
        dataContactInformationTv = (TextView) v
                .findViewById(R.id.dataContactInformationTv);
        jobPhoneDataTv = (TextView) v.findViewById(R.id.jobPhoneDataTv);
        phoneIconIv = (ImageView) v.findViewById(R.id.phoneIconIv);
        locationSiteIv = (ImageView) v.findViewById(R.id.locationSiteIv);
        cloudClientIconIv = (ImageView) v.findViewById(R.id.cloudClientIconIv);

        jobMobileDataTv = (TextView) v.findViewById(R.id.jobMobileDataTv);
        clientEmailDataTv = (TextView) v.findViewById(R.id.clientEmailDataTv);

        phoneIconIv.setOnClickListener(onClickListener);
        locationSiteIv.setOnClickListener(onClickListener);
        cloudClientIconIv.setOnClickListener(onClickListener);

        mobileIconIv = (ImageView) v.findViewById(R.id.mobileIconIv);
        emailClientIconIv = (ImageView) v.findViewById(R.id.emailClientIconIv);

        mobileIconIv.setOnClickListener(onClickListener);
        emailClientIconIv.setOnClickListener(onClickListener);

        containerJobInformation = (LinearLayout) v
                .findViewById(R.id.containerJobInformation);
        containerClientEmail = (LinearLayout) v
                .findViewById(R.id.containerClientEmail);
        containerPhone = (LinearLayout) v.findViewById(R.id.containerPhone);
        containerMobile = (LinearLayout) v.findViewById(R.id.containerMobile);
        dividerLinePhone = (ImageView) v.findViewById(R.id.dividerLinePhone);
        dividerLineMobile = (ImageView) v.findViewById(R.id.dividerLineMobile);


        containerPhone.setOnClickListener(onClickListener);
        containerMobile.setOnClickListener(onClickListener);
        containerClientEmail.setOnClickListener(onClickListener);
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
     * The on click listener.
     */
    OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.cloudClientIconIv:
                    if (client != null) {
                        boolean isSubContr = isSubContractor();
                        if (!isSubContr)
                            openLinkInBrowser(client.getPublicLink());
                    }
                    break;
                case R.id.locationSiteIv:
                    openMaps();
                    break;
                case R.id.phoneIconIv:
                case R.id.containerPhone:
                    if (client != null) {
                        if (!TextUtils.isEmpty(client.getTelephoeClient())) {
                            String phoneNo = client.getTelephoeClient().trim();
                            callPhone = phoneNo;
                            boolean installed = PackageInstallationUtil.whatsAppInstalledOrNot("com.whatsapp",syncoteamNavigationActivity);
                            if (installed) {
                                CallingOptionDialog callingOptionDialog = new CallingOptionDialog(syncoteamNavigationActivity, phoneNo);
                                callingOptionDialog.show();
                            } else {
                                if (ContextCompat.checkSelfPermission(syncoteamNavigationActivity, Manifest.permission.CALL_PHONE)
                                        != PackageManager.PERMISSION_GRANTED) {

                                    clientDetailFragment.callingPermissionPhone();
                                } else {
                                    callingMethod();
                                }
                            }
                        }
                    }
                    break;

                case R.id.emailClientIconIv:

                case R.id.containerClientEmail:
                    if (client != null) {
                        if (!TextUtils.isEmpty(client.getEmailContactClient())) {
                            Intent email = new Intent(Intent.ACTION_SEND);
                            String emailAddress = client.getEmailContactClient().trim();
                            email.putExtra(Intent.EXTRA_EMAIL,
                                    new String[]{emailAddress});
                            // email.putExtra(Intent.EXTRA_SUBJECT, "subject");
                            // email.putExtra(Intent.EXTRA_TEXT, "message");
                            email.setType("message/rfc822");
                            syncoteamNavigationActivity.startActivity(Intent
                                    .createChooser(email, "Choose an Email client :"));
                        }
                    }
                    break;

                case R.id.mobileIconIv:

                case R.id.containerMobile:
                    if (client != null) {
                        if (!TextUtils.isEmpty(client.getMobileContactClient())) {
                            String mobileNo = client.getMobileContactClient().trim();
//					Intent callIntent = new Intent(Intent.ACTION_CALL);
//					callIntent.setData(Uri.parse("tel:" + mobileNo));
//					syncoteamNavigationActivity.startActivity(callIntent);
                            callPhone = mobileNo;
                            boolean installed = PackageInstallationUtil.whatsAppInstalledOrNot("com.whatsapp",syncoteamNavigationActivity);
                            if (installed) {
                                CallingOptionDialog callingOptionDialog = new CallingOptionDialog(syncoteamNavigationActivity, mobileNo);
                                callingOptionDialog.show();
                            } else {
                                if (ContextCompat.checkSelfPermission(syncoteamNavigationActivity, Manifest.permission.CALL_PHONE)
                                        != PackageManager.PERMISSION_GRANTED) {

                                    clientDetailFragment.callingPermissionPhone();
                                } else {
                                    callingMethod();
                                }
                            }
                        }
                    }
                    break;


                default:
                    break;
            }

        }
    };


    @SuppressLint("MissingPermission")
    public void callingMethod() {
        String phoneNo = callPhone;
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNo));
        syncoteamNavigationActivity.startActivity(callIntent);
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
     * Open maps.
     */
    public void openMaps() {

        if (isIntentAvailable(syncoteamNavigationActivity,
                "android.intent.action.navigon.START_PUBLIC")) {
            AlertDialog alert = new myCustomAlertDialog(
                    syncoteamNavigationActivity);
            alert.show();
        } else {

            if (!SynchroteamUitls.isGoogleMapInstalled(syncoteamNavigationActivity)) {
                SynchroteamUitls.showGoogleMapsDialog(syncoteamNavigationActivity);

                return;
            }

            try {
                if ((!TextUtils.isEmpty(client.getGpsX()))
                        && (!TextUtils.isEmpty(client.getGpsY()))) {

                    Intent myIntent = new Intent(
                            android.content.Intent.ACTION_VIEW,
                            Uri.parse("geo:0,0?q=" + client.getGpsY() + ","
                                    + client.getGpsX() + "&z=20"));
                    syncoteamNavigationActivity.startActivity(myIntent);
                } else {
                    Intent myIntent = new Intent(
                            android.content.Intent.ACTION_VIEW,
                            Uri.parse("geo:0,0?q="
                                    + client.getAdresseGlobalClient()
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
                            if ((!TextUtils.isEmpty(client.getGpsX()))
                                    && (!TextUtils.isEmpty(client.getGpsY()))) {
                                syncoteamNavigationActivity
                                        .startActivity(getMapsIntent(
                                                syncoteamNavigationActivity,
                                                "geo:0,0?q=" + client.getGpsX()
                                                        + ","
                                                        + client.getGpsY()
                                                        + "&z=16"));
                            } else {
                                syncoteamNavigationActivity
                                        .startActivity(getMapsIntent(
                                                syncoteamNavigationActivity,
                                                "geo:0,0?q="
                                                        + client.getAdresseGlobalClient()
                                                        + "&z=16"));
                            }

                        } catch (Exception e) {
                            Logger.printException(e);
                        } finally {
                            dismiss();
                        }
                    } else {
                        try {
                            if ((!TextUtils.isEmpty(client.getGpsX()))
                                    && (!TextUtils.isEmpty(client.getGpsY()))) {
                                String INTENT_EXTRA_KEY_LATITUDE = "latitude";
                                String INTENT_EXTRA_KEY_LONGITUDE = "longitude";
                                Intent myIntent = new Intent(
                                        "android.intent.action.navigon.START_PUBLIC");
                                myIntent.putExtra(INTENT_EXTRA_KEY_LATITUDE,
                                        Float.valueOf(client.getGpsX()));
                                myIntent.putExtra(INTENT_EXTRA_KEY_LONGITUDE,
                                        Float.valueOf(client.getGpsY()));
                                syncoteamNavigationActivity
                                        .startActivity(myIntent);
                            } else {
                                String INTENT_EXTRA_KEY_FREE_TEXT_ADDRESS = "free_text_address";
                                Intent myIntent = new Intent(
                                        "android.intent.action.navigon.START_PUBLIC");
                                myIntent.putExtra(
                                        INTENT_EXTRA_KEY_FREE_TEXT_ADDRESS,
                                        client.getAdresseGlobalClient());
                                syncoteamNavigationActivity
                                        .startActivity(myIntent);
                            }
                            //dismiss();
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

    private class FetchCleintDetailsAsyncTask extends
            AsyncTaskCoroutine<Integer, Boolean> {

        @Override
        public void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @Override
        public Boolean doInBackground(Integer... params) {
            // TODO Auto-generated method stub

            try {
                client = dataAccessObject.getClientDetails(clientId);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Logger.printException(e);

            }

            if (client != null) {
                return Boolean.valueOf(true);
            } else {
                return Boolean.valueOf(false);
            }

        }

        @Override
        public void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            if (result.booleanValue()) {
                if(client.getRef_customer().length()>0){
                    clientNameDataTv.setText(client.getNmClient()+" ("+client.getRef_customer()+")");
                }else {
                    clientNameDataTv.setText(client.getNmClient());
                }
                addressDataTv.setText(client.getAdresseGlobalClient());

                if (!TextUtils.isEmpty(client.getPreNomContactClient())
                        || !TextUtils.isEmpty(client.getNomContactClient()))
                    dataContactInformationTv.setText(client
                            .getPreNomContactClient()
                            + " "
                            + client.getNomContactClient());
                else
                    containerJobInformation.setVisibility(View.GONE);

                if (!TextUtils.isEmpty(client.getTelephoeClient()))
                    jobPhoneDataTv.setText(formatPhoneNumber(client.getTelephoeClient()));
                else {
                    containerPhone.setVisibility(View.GONE);
                    dividerLinePhone.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(client.getMobileContactClient()))
                    jobMobileDataTv.setText(formatPhoneNumber(client.getMobileContactClient()));
                else {
                    containerMobile.setVisibility(View.GONE);
                    dividerLineMobile.setVisibility(View.GONE);
                }

                if (!TextUtils.isEmpty(client.getEmailContactClient()))
                    clientEmailDataTv.setText(client.getEmailContactClient());
                else
                    containerClientEmail.setVisibility(View.GONE);

                if (!TextUtils.isEmpty(client.getPublicLink()))
                    cloudClientIconIv.setVisibility(View.VISIBLE);
                else
                    cloudClientIconIv.setVisibility(View.GONE);

            } else {

            }
            logicForShowPublicLink();
        }

    }

    private void logicForShowPublicLink() {
        //hide'show logic
        boolean isSubContr=isSubContractor();
        if(!isSubContr)
            cloudClientIconIv.setVisibility(View.VISIBLE);
        else
            cloudClientIconIv.setVisibility(View.GONE);
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
//					updatedPhoneNo=numberProto.toString();
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
