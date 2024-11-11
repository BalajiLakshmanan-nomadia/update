package com.synchroteam.fragmenthelper;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.Description;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.fragment.CustomDetailsFragment;
import com.synchroteam.fragment.DescriptionDetailsFragment;
import com.synchroteam.fragment.JobAttachmentListFragment;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.technicalsupport.JobDetails;
import com.synchroteam.utils.CirclePageIndicator;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * The Class DiscriptionJobDetailFragmentHelper is to inflate and control all
 * the actions performed in job description screen. Created For Future Purpose
 *
 * @author $Ideavate Solution
 */
public class DiscriptionJobDetailFragmentHelper implements HelperInterface {

    /**
     * The job details.
     */
    private JobDetails jobDetails;

    /**
     * The id interv.
     */
    private String idInterv;

    /** The cd status. */
    // private String cdStatus;

    /**
     * The nom site.
     */
    private String nomSite;

    /**
     * The nom equipement.
     */
    private String nomEquipement;

    /**
     * The status.
     */
    private int status;

    /**
     * The linear.
     */
    private View linear;

    /** The id site. */
    // private int idSite;

    /**
     * The data access object.
     */
    private Dao dataAccessObject;

    /**
     * The planned tv.
     */
    private TextView dateAndTimeTv, startTv, endTv, plannedTv;

    /**
     * Font awesome icons tc.
     */
    private android.widget.TextView txtDateic, txtStartic, txtEndic,
            txtPlannedic;

    /** The txt equipement. */
    // private TextView txtClient, txtAdresse, txtType, txtModele, txtNumInterv,
    // txtSite, txtEquipement, txtJobManager;

    /** The technician tv. */
    // private TextView phoneNumberTv, mobileNumberTv, technicianTv,
    // additionalAddressDataTv;

    /**
     * The description.
     */
    private Description description;

    /**
     * The alert dialog view3.
     */
    private View alertDialogView3;

    /**
     * The adr_globale.
     */
    private String lat = "0", lon = "0", adr_globale;

    /** The message icon iv. */
    // private ImageView phoneIconIv, messageIconIv;

    /** The conatainer additional address. */
    // private LinearLayout conatainerAdditionalAddress, containerClientEmail,
    // containerCustomerName, containerCustomerSite, containerAddress,
    // containerNumber;

    /** The txtcontact. */
    // private TextView txtcontact, clientEmailDataTv;

    /** The txt description. */
    // private TextView txtDescription;

    /**
     * The ma list view perso.
     */
    private ListView maListViewPerso;

    /**
     * The user.
     */
    private User user;

    /**
     * The id user.
     */
    private String idUser;

    /**
     * The view.
     */
    private View view;

    // private ImageView emailClientIconIv;

    /**
     * The gestion acces.
     */
    private GestionAcces gestionAcces;

    /**
     * The mobile icon iv.
     */
    // private ImageView mobileIconIv;

    private String publicLinkIntervention, publicLinkClient;

    private CirclePageIndicator mIndicator;

    private ViewPager viewPager;

    private DescriptionDetailsFragment fragmentDetails;
    private CustomDetailsFragment fragmentCustomDetails;

    // private static final String TAG = "DiscriptionJobDetailFragmentHelper";

    /**
     * The id equipement.
     */
    private int idClient, idSite, idEquipement;

    /**
     * to check if custom field is present or not.
     */
    private boolean haveCF;

    /**
     * to check if attachments are present or not.
     */
    private boolean haveAttachment;

    /**
     * No of page.
     */
    private int pageCount;

    private LinearLayout linDate, linDuration;

    /**
     * Instantiates a new discription job detail fragment helper.
     *
     * @param jobDetails    the job details
     * @param idInterv      the id interv
     * @param nomSite       the nom site
     * @param nomEquipement the nom equipement
     * @param idSite        the id site
     * @param lat           the lat
     * @param lon           the lon
     * @param adr_global    the adr_global
     * @param dateMeeting   the date meeting
     * @param idUser        the id user
     */
    public DiscriptionJobDetailFragmentHelper(JobDetails jobDetails,
                                              String idInterv, String nomSite, String nomEquipement, int idSite,
                                              String lat, String lon, String adr_global, String dateMeeting,
                                              String idUser, int idClient, int idEquipement, int status) {

        this.jobDetails = jobDetails;
        this.idInterv = idInterv;

        this.nomSite = nomSite;
        this.nomEquipement = nomEquipement;

        this.dataAccessObject = DaoManager.getInstance();
        this.gestionAcces = dataAccessObject.getAcces();
        this.lat = lat;
        this.lon = lon;
        this.adr_globale = adr_global;

        this.idUser = idUser;

        this.idClient = idClient;
        this.idSite = idSite;
        this.idEquipement = idEquipement;
        this.status = status;

    }

    public DiscriptionJobDetailFragmentHelper() {
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
        View view = inflater.inflate(R.layout.layout_discription_jobdetails,
                null);

        initiateView(view);
        setDataToView(view);
        this.view = view;
        return view;
    }

    /**
     * Sets the data to view.
     *
     * @param view the new data to view
     */
    private void setDataToView(View view) {

        description = dataAccessObject.getInterventionById(idInterv);
        user = dataAccessObject.getUser();

        seDateInDateAndTime();
        // jobTypeTv.setText(description.getTypeIntervention());
        // jobInformationTv.setText(description.getDescriptionIntervention());
        // if(!TextUtils.isEmpty(nomEquipement)){
        // equipmentTv.setText(nomEquipement);
        // }
        // else{
        // containerEquipments.setVisibility(View.GONE);
        // }

        if (description != null) {
            Logger.log("PRe Nom", user.getPrenom());
            Logger.log("DIscriptionJobDetail>>>>", description.getNomClient());
        }

        // if(!dateMeeting.equals(null) && !dateMeeting.equals("")){
        //
        //
        // LinearLayout lnM=(LinearLayout)
        // view.findViewById(R.id.containerMeeting);
        //
        // TextView txtM=(TextView) view.findViewById(R.id.jobMeetingDataTv);
        // view.findViewById(R.id.imageViewMeeting).setVisibility(View.GONE);
        // lnM.setVisibility(0);
        //
        // txtM.setText(dateMeeting);
        // }

        // clientCorporationTv.setText(description.getNomClient());
        // clientAddressTv.setText(description.getAdresseGlobale());
        // clientNameTv.setText(description.getNomContact());
        //

    }

    /**
     * Se date in date and time.
     */
    @SuppressLint("SimpleDateFormat")
    private void seDateInDateAndTime() {

        SimpleDateFormat dateFormat;
        DateFormat dateFormatLong;

        try {
            dateFormatLong = DateFormat.getDateInstance(DateFormat.LONG);
            dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
            Date dateDebut = dateFormat.parse(description
                    .getDateDebutIntervention());
            Date dateFin = dateFormat.parse(description
                    .getDateFinIntervention());
            Date startDate = df1.parse(description
                    .getDateDebutIntervention());
            Date endDate = df1.parse(description
                    .getDateFinIntervention());
            Format format = android.text.format.DateFormat
                    .getDateFormat(jobDetails);
            Format timeFormat = android.text.format.DateFormat
                    .getTimeFormat(jobDetails);

            if(startDate.equals(endDate)) {
                dateAndTimeTv.setText(dateFormatLong.format(dateDebut));
//                dateAndTimeTv.setText(format.format(dateDebut));
                startTv.setText(timeFormat.format(dateDebut));
                endTv.setText(timeFormat.format(dateFin));

                int seconds = (int) ((dateFin.getTime() - dateDebut.getTime()) / 1000);

                int hours = seconds / 3600;
                int minutes = (seconds / 60) - (hours * 60);
                seconds = seconds - (hours * 3600) - (minutes * 60);
                String minutesString = null;
                String hoursString = null;
                if (minutes < 10) {
                    minutesString = "0" + minutes;
                } else {
                    minutesString = minutes + "";
                }
                if (hours < 10) {
                    hoursString = "0" + hours;
                } else {
                    hoursString = hours + "";
                }
                plannedTv.setText(hoursString + ":" + minutesString);
            }
            else{
                linDate.setVisibility(View.GONE);
                linDuration.setVisibility(View.GONE);
                startTv.setText(format.format(dateDebut) +" "+timeFormat.format( dateDebut));
                endTv.setText(format.format(dateFin) +" "+ timeFormat.format(dateFin));
            }
        } catch (Exception e) {
            Logger.printException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.synchroteam.fragmenthelper.HelperInterface#initiateView(android.view
     * .View)
     */
    @Override
    public void initiateView(View v) {

        TextView textView1 = (TextView) v.findViewById(R.id.textView2);
        TextView textView2 = (TextView) v.findViewById(R.id.textView3);
        TextView textView3 = (TextView) v.findViewById(R.id.textView4);
        TextView textView4 = (TextView) v.findViewById(R.id.textView5);

        textView4.setSelected(true);

        dateAndTimeTv = (TextView) v.findViewById(R.id.dateAndTimeTv);
        dateAndTimeTv.setSelected(true);
        startTv = (TextView) v.findViewById(R.id.startTimeTv);
        endTv = (TextView) v.findViewById(R.id.endTImeTv);
        plannedTv = (TextView) v.findViewById(R.id.plannedTv);

        txtDateic = (android.widget.TextView) v.findViewById(R.id.txtDateIcon);
        txtStartic = (android.widget.TextView) v
                .findViewById(R.id.txtStartIcon);
        txtEndic = (android.widget.TextView) v.findViewById(R.id.txtEndIcon);
        txtPlannedic = (android.widget.TextView) v
                .findViewById(R.id.txtPlannedIcon);
        linDate = (LinearLayout) v.findViewById(R.id.lin_date);
        linDuration = (LinearLayout) v.findViewById(R.id.lin_duration);

        Typeface fontAwesome = Typeface.createFromAsset(jobDetails.getAssets(),
                jobDetails.getString(R.string.fontName_fontAwesome));
        txtDateic.setTypeface(fontAwesome);
        txtStartic.setTypeface(fontAwesome);
        txtEndic.setTypeface(fontAwesome);
        txtPlannedic.setTypeface(fontAwesome);

        mIndicator = (CirclePageIndicator) v
                .findViewById(R.id.circlePageIndicator);

        // Check if custom field & attachment are present and change the page
        // counts accordingly.
        if (dataAccessObject.getCFForIntervention(idInterv).size() != 0
                || dataAccessObject.getCFForClient(idClient).size() != 0
                || dataAccessObject.getCFForSite(idSite).size() != 0
                || dataAccessObject.getCFForEquip(idEquipement).size() != 0) {
            haveCF = true;
//			pageCount = 3;
        } else {
            haveCF = false;
//			pageCount = 2;
        }

        if (dataAccessObject.getJobAttachmentList(idInterv).size() != 0
                || dataAccessObject.getClientAttachmentList(idClient).size() != 0
                || dataAccessObject.getSiteAttachmentList(idSite).size() != 0
                || dataAccessObject.getEquipmentAttachmentList(idEquipement)
                .size() != 0) {
            haveAttachment = true;
        } else {
            haveAttachment = false;
        }

        if (haveCF && haveAttachment) {
            pageCount = 3;
            mIndicator.setVisibility(View.VISIBLE);
        } else if (haveCF || haveAttachment) {
            pageCount = 2;
            mIndicator.setVisibility(View.VISIBLE);
        } else {
            pageCount = 1;
            mIndicator.setVisibility(View.GONE);
        }

        viewPager = (ViewPager) v.findViewById(R.id.viewPager);
        viewPager.setAdapter(new ViewPagerAdapter(jobDetails
                .getSupportFragmentManager()));


        mIndicator.setViewPager(viewPager);
        final float density = jobDetails.getResources().getDisplayMetrics().density;
        mIndicator.setFillColor(ContextCompat.getColor(jobDetails, R.color.actionbarColor));
        mIndicator.setPageColor(ContextCompat.getColor(jobDetails, R.color.status_selector_color));
        mIndicator.setBackgroundColor(ContextCompat.getColor(jobDetails, android.R.color.transparent));
        mIndicator.setStrokeWidth(0);
        mIndicator.setPadding(10, 10, 10, 10);
        // sets the radius(size) of the indicator
        mIndicator.setRadius(5 * density);

    }

    /*
     * (non-Javadoc)
     *
     * @see com.synchroteam.fragmenthelper.HelperInterface#doOnSyncronize()
     */
    @Override
    public void doOnSyncronize() {

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.synchroteam.fragmenthelper.HelperInterface#onReturnToActivity(int)
     */
    @Override
    public void onReturnToActivity(int requestCode) {

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
     * Fill nav list.
     */
    public void fillNavList() {

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
     * Update ui.
     */
    public void updateUi() {
        Logger.log("Dessss", "update ui");
        setDataToView(view);
    }

    /**
     * Update ui.
     */
    public void updateUiAfterSync() {
        gestionAcces = dataAccessObject.getAcces();
        if (gestionAcces != null && gestionAcces.getOptionHelpSurfing() == 0) {
            view.findViewById(R.id.locationIconIv).setVisibility(View.GONE);
            view.findViewById(R.id.locationSiteIv).setVisibility(View.GONE);

        } else {
            view.findViewById(R.id.locationIconIv).setVisibility(View.VISIBLE);
            view.findViewById(R.id.locationSiteIv).setVisibility(View.VISIBLE);

        }

        Description descriptionPl = dataAccessObject
                .getPublicLinksById(idInterv);
        description.setPublicLinkIntervention(descriptionPl
                .getPublicLinkInterv());
        description.setPublicLinkClient(descriptionPl.getPublicLinkClient());

    }

    // .....................................VIEW...PAGER...ADAPTER.................................................
    private class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Object instantiateItem(View container, int position) {
            return super.instantiateItem(container, position);
        }

        @Override
        public Fragment getItem(int index) {
            switch (index) {
                case 0:
                    Bundle bundleDetails = new Bundle();
                    bundleDetails.putString("id_interv", idInterv);
                    bundleDetails.putString("site_name", nomSite);
                    bundleDetails.putString("equp_name", nomEquipement);
                    bundleDetails.putString("lat", lat);
                    bundleDetails.putString("lon", lon);
                    bundleDetails.putString("id_user", idUser);
                    bundleDetails.putInt("idSite", idSite);
                    bundleDetails.putInt("idEquipement", idEquipement);
                    fragmentDetails = new DescriptionDetailsFragment();
                    fragmentDetails.setArguments(bundleDetails);
                    return fragmentDetails;
                case 1:
                    // int count = dataAccessObject.getJsonForIntervention(idInterv)
                    // .size();
                    if (haveCF) {
                        Bundle bundle = new Bundle();
                        bundle.putString("id_interv", idInterv);
                        bundle.putInt("idClient", idClient);
                        bundle.putInt("idSite", idSite);
                        bundle.putInt("idEquipement", idEquipement);

                        fragmentCustomDetails = new CustomDetailsFragment();
                        fragmentCustomDetails.setArguments(bundle);
                        return fragmentCustomDetails;
                    } else if (haveAttachment) {
                        Bundle bundleAttach = new Bundle();

                        bundleAttach.putString(KEYS.CurrentJobs.ID, idInterv);
                        bundleAttach.putInt(KEYS.CurrentJobs.CD_STATUS, status);
                        bundleAttach.putInt(KEYS.CurrentJobs.IDCLIENT, idClient);
                        bundleAttach.putInt(KEYS.CurrentJobs.IDSITE, idSite);
                        bundleAttach.putInt(KEYS.CurrentJobs.IDEQUIPMENT,
                                idEquipement);
                        JobAttachmentListFragment fragmentAttach = new JobAttachmentListFragment();
                        fragmentAttach.setArguments(bundleAttach);
                        return fragmentAttach;
                    }

                case 2:
                    if (haveAttachment) {
                        Bundle bundleAttach = new Bundle();

                        bundleAttach.putString(KEYS.CurrentJobs.ID, idInterv);
                        bundleAttach.putInt(KEYS.CurrentJobs.CD_STATUS, status);
                        bundleAttach.putInt(KEYS.CurrentJobs.IDCLIENT, idClient);
                        bundleAttach.putInt(KEYS.CurrentJobs.IDSITE, idSite);
                        bundleAttach.putInt(KEYS.CurrentJobs.IDEQUIPMENT, idEquipement);
                        JobAttachmentListFragment fragmentAttach = new JobAttachmentListFragment();
                        fragmentAttach.setArguments(bundleAttach);
                        return fragmentAttach;
                    }

            }
            return null;
        }

        @Override
        public int getCount() {
            // int count = dataAccessObject.getJsonForIntervention(idInterv)
            // .size();
            return pageCount;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

    }
}
