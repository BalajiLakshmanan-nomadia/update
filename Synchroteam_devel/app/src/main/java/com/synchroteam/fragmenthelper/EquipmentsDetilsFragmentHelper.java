package com.synchroteam.fragmenthelper;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sap.ultralitejni17.ULjException;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.Site;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.synchroteam.ClientDetail;
import com.synchroteam.synchroteam.EquipmentDetials;
import com.synchroteam.synchroteam.SiteDetail;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;

// TODO: Auto-generated Javadoc

/**
 * The Class EquipmentsDetilsFragmentHelper is to inflate and control all the
 * actions performed in equipments description screen.
 *
 * @author Trident
 */
public class EquipmentsDetilsFragmentHelper implements HelperInterface {

    /**
     * The data access object.
     */
    private Dao dataAccessObject;

    /**
     * The view.
     */
    private EquipmentDetials syncoteamNavigationActivity;

    private TextView equipmentDataTv, dataSitelableTv, clientNameDataTv;
    private int clientId, siteId, equipId;
    private String clientName, siteName, equipmentName, publicLink;

    private Site site = null;

    private ImageView cloudClientIconIv, ivEequipmentPublicLink, cloudSiteIconIv;

    private LinearLayout containerJobInformation;

    /**
     * Instantiates a new client detial fragment helper
     *
     * @param clientId
     */
    public EquipmentsDetilsFragmentHelper(
            EquipmentDetials syncoteamNavigationActivity, int clientId,
            int siteId, String clientName, String siteName, String equipmentName, String publicLink, int equipId) {
        // TODO Auto-generated constructor stub

        this.dataAccessObject = DaoManager.getInstance();
        this.clientId = clientId;
        this.siteId = siteId;
        this.equipId = equipId;
        this.syncoteamNavigationActivity = syncoteamNavigationActivity;
        this.clientName = clientName;
        this.equipmentName = equipmentName;
        this.siteName = siteName;
        this.publicLink = publicLink;
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
        View view = inflater.inflate(R.layout.layout_equipment_detail, null);
        initiateView(view);

        new FetchSiteDetialAsyncTask().execute();
        logicForShowPublicLink();
        return view;
    }

    /**
     * Sets the data to view.
     *
     * @param
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
        clientNameDataTv = (TextView) v.findViewById(R.id.clientNameDataTv);
        dataSitelableTv = (TextView) v.findViewById(R.id.dataSitelableTv);
        equipmentDataTv = (TextView) v.findViewById(R.id.equipmentDataTv);

        dataSitelableTv.setText(siteName);
        equipmentDataTv.setText(equipmentName);
        clientNameDataTv.setText(clientName);

        cloudClientIconIv = (ImageView) v.findViewById(R.id.cloudClientIconIv);
        ivEequipmentPublicLink = (ImageView) v.findViewById(R.id.equipmentPublicLinkIv);


        if(publicLink==null||publicLink.trim().length()==0) {
            publicLink = dataAccessObject.getEquipementsDetailLink(clientId, siteId, equipId);
        }
        if (publicLink != null && !TextUtils.isEmpty(publicLink)) {
            ivEequipmentPublicLink.setVisibility(View.VISIBLE);
        } else {
            ivEequipmentPublicLink.setVisibility(View.GONE);
        }


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

        ivEequipmentPublicLink.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isSubContr = isSubContractor();
                if (!isSubContr)
                openLinkInBrowser(publicLink);
            }
        });
        cloudSiteIconIv = (ImageView) v.findViewById(R.id.cloudSiteIconIv);
        cloudSiteIconIv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(syncoteamNavigationActivity,
                        SiteDetail.class);
                intent.putExtra(KEYS.SiteDetails.ID_SITE, siteId);
                intent.putExtra(KEYS.SiteDetails.ID_CLIENT, clientId);
                intent.putExtra(KEYS.SiteDetails.NAME_SITE, siteName);
                intent.putExtra(KEYS.SiteDetails.CLIENT_NAME, clientName);
                syncoteamNavigationActivity.startActivity(intent);

            }
        });

        containerJobInformation = (LinearLayout) v
                .findViewById(R.id.containerJobInformation);

        Logger.log("", "check site name " + siteName);

        if (siteName != null && !TextUtils.isEmpty(siteName.trim())) {
            containerJobInformation.setVisibility(View.VISIBLE);
        } else {
            containerJobInformation.setVisibility(View.GONE);
        }

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

    private class FetchSiteDetialAsyncTask extends
            AsyncTaskCoroutine<Void, Boolean> {

        @Override
        public void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

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

            if (result.booleanValue()) {
                /*
                 * dataSitelableTv.setText(site.getNmSite());
                 * addressDataTv.setText(site.getAdresse());
                 * clientNameDataTv.setText(clientName);
                 */

                // dataSitelableTv.setText(site.getNmSite());
                equipmentDataTv.setText(equipmentName);
                clientNameDataTv.setText(clientName);

                if (site.getNmSite() != null
                        && !TextUtils.isEmpty(site.getNmSite().trim())) {
                    siteName = site.getNmSite();
                    dataSitelableTv.setText(site.getNmSite());
                    containerJobInformation.setVisibility(View.VISIBLE);
                } else {
                    siteName = site.getNmSite();
                    dataSitelableTv.setText(site.getNmSite());
                    containerJobInformation.setVisibility(View.GONE);
                }

            } else {

            }

        }
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
    private void logicForShowPublicLink() {
        //hide'show logic
        boolean isSubContr=isSubContractor();
        if(!isSubContr)
            ivEequipmentPublicLink.setVisibility(View.VISIBLE);
        else
            ivEequipmentPublicLink.setVisibility(View.GONE);
    }
    /**
     * To check if the technician is subcontractor
     *
     * @return result
     */
    private boolean isSubContractor() {
        boolean result = false;
        User user = dataAccessObject.getUser();

        int idProfil = user.getIdProfil();
        int flSubContractor = user.getFlSubContractor();
        if (idProfil == 4 && flSubContractor == 1) {
            result = true;
        }
        return result;
    }
}
