package com.synchroteam.fragmenthelper;

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
import android.text.TextUtils;
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
import com.synchroteam.fragment.CustomJobPoolDetailsFragment;
import com.synchroteam.fragment.DescriptionJobPoolDetailsFragment;
import com.synchroteam.fragment.JobPoolAttachmentListFragment;
import com.synchroteam.synchroteam.JobPoolDetails;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.CirclePageIndicator;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DateTimeUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;

import java.text.DateFormat;
import java.text.ParseException;
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
public class DiscriptionJobPoolDetailFragmentHelper implements HelperInterface {

    /**
     * The job details.
     */
    private JobPoolDetails jobPoolDetails;

    /**
     * The id interv.
     */
    private String idInterv;


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

    /**
     * The data access object.
     */
    private Dao dataAccessObject;

    private TextView txtDateJob, txtTimeJob, txtDuration;

    /**
     * Font awesome icons tc.
     */
    private android.widget.TextView txtTimeIcon, txtDateIcon,
            txtPlannedIcon, txtLockIcon;


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

    /**
     * The dateMeeting
     */
    private String dateMeeting;

    /**
     * The date pref.
     */
    private String datePref;

    /**
     * The id job window.
     */
    private String idJobWindow;


    /**
     * The gestion acces.
     */
    private GestionAcces gestionAcces;

    private String publicLinkIntervention, publicLinkClient;

    private CirclePageIndicator mIndicator;

    private ViewPager viewPager;

    private DescriptionJobPoolDetailsFragment fragmentDetails;
    private CustomJobPoolDetailsFragment fragmentCustomDetails;

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
     * @param jobPoolDetails the job details
     * @param idInterv       the id interv
     * @param nomSite        the nom site
     * @param nomEquipement  the nom equipement
     * @param idSite         the id site
     * @param lat            the lat
     * @param lon            the lon
     * @param adr_global     the adr_global
     * @param dateMeeting    the date meeting
     * @param idUser         the id user
     */
    public DiscriptionJobPoolDetailFragmentHelper(JobPoolDetails jobPoolDetails,
                                                  String idInterv, String nomSite, String nomEquipement, int idSite,
                                                  String lat, String lon, String adr_global, String dateMeeting,
                                                  String idUser, int idClient, int idEquipement, int status, String datePref, String idJobWindow) {

        this.jobPoolDetails = jobPoolDetails;
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
        this.dateMeeting = dateMeeting;
        this.idJobWindow = idJobWindow;
        this.datePref = datePref;

    }

    public DiscriptionJobPoolDetailFragmentHelper() {
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
        View view = inflater.inflate(R.layout.layout_description_job_pool,
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

        jobPoolDetailsLogic();


        if (description != null) {
            Logger.log("PRe Nom", user.getPrenom());
            Logger.log("DIscriptionJobDetail>>>>", description.getNomClient());
        }

    }

    private void jobPoolDetailsLogic() {
        Date duree = dataAccessObject.getJobDuration(idInterv);
        SimpleDateFormat jobDurationFormat = new SimpleDateFormat("HH:mm");
        String strDate = jobDurationFormat.format(duree);
        txtDuration.setText(strDate);

//        TextView txtDateJob, txtTimeJob ;
//        android.widget.TextView txtTimeIcon, txtDateIcon;


        String isJobDatePref = datePref;
        int idJobWindow = Integer.parseInt(this.idJobWindow);
        String dtMeeting = dateMeeting;


        String dateToshow = null;
        String dateToshowRequired = " ";
        String startTimeRequired = " ";
        String endTimeRequired = " ";

        String timeLabel = jobPoolDetails.getResources().getString(
                R.string.textPrefTimeLable);
        String dateLabel = jobPoolDetails.getResources().getString(
                R.string.textPrefDateLable);
        if (!TextUtils.isEmpty(dtMeeting) && !dtMeeting.equalsIgnoreCase("null")) {
            String[] dateTopass = dtMeeting.split(" ");
            dateToshow = dateTopass[0];
//                dateToshowRequired = getDateWithRequiredPattern(dateToshow,
//                        "yyyy-MM-dd", "dd/MM/yy");

            dateToshowRequired = DateTimeUtils.getDateWithRequiredPattern(dateToshow,
                    "yyyy-MM-dd", DateFormat.getDateInstance(DateFormat.LONG));

            if (dateToshowRequired != null && dateToshowRequired.trim().length() > 0)
                txtDateJob.setText(dateToshowRequired);

            if (dateTopass[1] != null && dateTopass[1].length() > 0 && !dateTopass[1].startsWith("00:00")) {
//                    startTimeRequired = getDateWithRequiredPattern(dateTopass[1],
//                            "HH:mm:ss", "hh:mm a");
                startTimeRequired = DateTimeUtils.getTimeWithRequiredFormatPattern(dateTopass[1],
                        "HH:mm:ss", android.text.format.DateFormat.getTimeFormat(jobPoolDetails));
                timeLabel = jobPoolDetails.getResources().getString(
                        R.string.label_time_job_pool);


                txtDateJob.setText(dateToshowRequired);

                if (startTimeRequired != null && startTimeRequired.trim().length() > 0) {
                    txtTimeJob.setText(timeLabel + " : " + startTimeRequired);
                    dateLabel = "";
                }

                txtLockIcon.setVisibility(View.VISIBLE);
            } else {
                //hide lock icon
                txtLockIcon.setVisibility(View.GONE);
            }

        } else {

            if (isJobDatePref != null && !TextUtils.isEmpty(isJobDatePref) &&
                    !isJobDatePref.equalsIgnoreCase("null")) {
                txtLockIcon.setVisibility(View.GONE);
                String[] dateTopass = isJobDatePref.split(" ");
                dateToshow = dateTopass[0];
//                    dateToshowRequired = getDateWithRequiredPattern(dateToshow,
//                            "yyyy-MM-dd", "dd/MM/yy");

                dateToshowRequired = DateTimeUtils.getDateWithRequiredPattern(dateToshow,
                        "yyyy-MM-dd", DateFormat.getDateInstance(DateFormat.LONG));

                if (dateToshowRequired != null && dateToshowRequired.trim().length() > 0)
                    txtDateJob.setText(dateLabel + " : " + dateToshowRequired);

                if (dateTopass[1] != null && dateTopass[1].length() > 0 && !dateTopass[1].startsWith("00:00")) {
//                        startTimeRequired = getDateWithRequiredPattern(dateTopass[1],
//                                "HH:mm:ss", "hh:mm a");

                    startTimeRequired = DateTimeUtils.getTimeWithRequiredFormatPattern(dateTopass[1],
                            "HH:mm:ss", android.text.format.DateFormat.getTimeFormat(jobPoolDetails));

                    txtDateJob.setText(dateLabel + " : " + dateToshowRequired);

                    if (startTimeRequired != null && startTimeRequired.trim().length() > 0) {
                        txtTimeJob.setText(timeLabel + " : " + startTimeRequired);
                        dateLabel = "";
                    }
                }
            }

            if (idJobWindow > 0) {
                startTimeRequired = dataAccessObject.startTimeJobWindow(idJobWindow);
                endTimeRequired = dataAccessObject.endTimeJobWindow(idJobWindow);


                if (startTimeRequired != null && startTimeRequired.trim().length() > 0 &&
                        endTimeRequired != null && endTimeRequired.trim().length() > 0) {
                    txtTimeJob.setText(timeLabel + " : " + startTimeRequired + " - " + endTimeRequired);
                }
            }
        }

        if (startTimeRequired == null || startTimeRequired.trim().length() == 0) {
            txtTimeJob.setVisibility(View.INVISIBLE);
            txtTimeIcon.setVisibility(View.INVISIBLE);

        }

        if (dateToshowRequired == null || dateToshowRequired.trim().length() == 0) {
            txtDateIcon.setVisibility(View.INVISIBLE);
            txtDateJob.setVisibility(View.INVISIBLE);
        }
        Logger.log("TAG", "JOB POOL TIME START" + startTimeRequired);
        Logger.log("TAG", "JOB POOL TIME END" + endTimeRequired);
        Logger.log("TAG", "JOB POOL DATE " + dateToshowRequired);


    }


    /**
     * Gets the date with required pattern for calander.
     *
     * @param date                the date
     * @param datePattern         the date pattern
     * @param requiredDatePattern the required date pattern
     * @return the date with requiredpattern for calander
     * @throws ParseException the parse exception
     */
    private synchronized String getDateWithRequiredPattern(String date,
                                                           String datePattern, String requiredDatePattern)
            throws ParseException {

        String requiredDate = null;

        SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
        SimpleDateFormat formatterOut = new SimpleDateFormat(requiredDatePattern);

        try {

            Date outputDate = formatter.parse(date);
            requiredDate = formatterOut.format(outputDate);
            System.out.println(date);
            System.out.println();

        } catch (ParseException e) {
            e.printStackTrace();
            requiredDate = date;
        }

        return requiredDate;
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

        txtDateJob = (TextView) v.findViewById(R.id.txtDateJob);
        txtDateJob.setSelected(true);
        txtTimeJob = (TextView) v.findViewById(R.id.txtTimeJob);
        txtDuration = (TextView) v.findViewById(R.id.txtDuration);


        txtTimeIcon = (android.widget.TextView) v.findViewById(R.id.txtTimeIcon);
        txtDateIcon = (android.widget.TextView) v
                .findViewById(R.id.txtDateIcon);
        txtPlannedIcon = (android.widget.TextView) v.findViewById(R.id.txtPlannedIcon);
        txtLockIcon = (android.widget.TextView) v.findViewById(R.id.txtLockIcon);

        linDate = (LinearLayout) v.findViewById(R.id.lin_date);
        linDuration = (LinearLayout) v.findViewById(R.id.lin_duration);

        Typeface fontAwesome = Typeface.createFromAsset(jobPoolDetails.getAssets(),
                jobPoolDetails.getString(R.string.fontName_fontAwesome));

        txtTimeIcon.setTypeface(fontAwesome);
        txtDateIcon.setTypeface(fontAwesome);
        txtPlannedIcon.setTypeface(fontAwesome);
        txtLockIcon.setTypeface(fontAwesome);

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
        viewPager.setAdapter(new ViewPagerAdapter(jobPoolDetails
                .getSupportFragmentManager()));


        mIndicator.setViewPager(viewPager);
        final float density = jobPoolDetails.getResources().getDisplayMetrics().density;
        mIndicator.setFillColor(ContextCompat.getColor(jobPoolDetails, R.color.actionbarColor));
        mIndicator.setPageColor(ContextCompat.getColor(jobPoolDetails, R.color.status_selector_color));
        mIndicator.setBackgroundColor(ContextCompat.getColor(jobPoolDetails, android.R.color.transparent));
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
                    fragmentDetails = new DescriptionJobPoolDetailsFragment();
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

                        fragmentCustomDetails = new CustomJobPoolDetailsFragment();
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
                        JobPoolAttachmentListFragment fragmentAttach = new JobPoolAttachmentListFragment();
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
                        JobPoolAttachmentListFragment fragmentAttach = new JobPoolAttachmentListFragment();
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
