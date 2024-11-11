package com.synchroteam.synchroteam;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.synchroteam.beans.Site;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.AddNewJobDialog;
import com.synchroteam.fragment.AttachmentListSiteDetailsFragment;
import com.synchroteam.fragment.SiteDetailFragment;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DateChecker;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.TabPageIndicator;

import java.util.Calendar;

public class SiteDetail extends AppCompatActivity {

    // --------------
    private TabPageIndicator indicator;

    private int siteId, clientId;

    private ActionBar actionBar;
    private ViewPager viewPager;

    private String[] CONTENT = null;

    private String clientName;

    /**
     * The data access object.
     */
    private Dao dataAccessObject;

    /**
     * The dao.
     */
    private Dao dao;

    private Site site;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_client_detail);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        String title = getIntent().getStringExtra(KEYS.SiteDetails.NAME_SITE);
        SpannableString titleSpannable = new SpannableString(title);
        titleSpannable.setSpan(
                new TypefaceSpan(this.getResources().getString(
                        R.string.fontName_hevelicaLight)), 0,
                titleSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // actionBar.setTitle(titleSpannable);
        actionBar.setTitle(isLGDevice() ? title : titleSpannable);

        siteId = getIntent().getIntExtra(KEYS.SiteDetails.ID_SITE, -1);
        clientId = getIntent().getIntExtra(KEYS.SiteDetails.ID_CLIENT, -1);
        clientName = getIntent().getStringExtra(KEYS.SiteDetails.CLIENT_NAME);

        CONTENT = new String[]{getString(R.string.textDescriptionLable),
                getString(R.string.textAttachmentLableTv)};
        indicator = (TabPageIndicator) findViewById(R.id.indicator);
        viewPager = (ViewPager) findViewById(R.id.pagerClientSection);

        viewPager.setAdapter(new SiteDetailTabAdapter(
                getSupportFragmentManager()));
        indicator.setViewPager(viewPager);

        // initialize the object
        dataAccessObject = DaoManager.getInstance();
        dao = DaoManager.getInstance();

        try {
            site = dataAccessObject.getSiteDetail(siteId);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        //New changes
        DateChecker.checkDateAndNavigate(this, dataAccessObject);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub

        MenuInflater inflater = getMenuInflater();

        // set the condition to show the add job or not
        int flag = dataAccessObject.getAddIntervFlag();

        // int flagActionInformation = dao.getCustomFieldsSite(siteId).size();
        int flagActionInformation = dao.getCFForSite(siteId).size();

        int flagEquipmentListing = dataAccessObject.getEquipementsForSiteCount(
                -1, siteId);

        if (flag != 1 && flagActionInformation == 0
                && flagEquipmentListing == 0) {

            // nothing to display

        } else if (flagActionInformation == 0 && flagEquipmentListing == 0) {
            // Add job
            inflater.inflate(
                    R.menu.site_detail_menu_without_equipmentlisting_actioninformation,
                    menu);
        } else if (flag != 1 && flagActionInformation == 0) {
            // Equipment Listing
            inflater.inflate(
                    R.menu.site_detail_menu_without_add_job_action_information,
                    menu);
        } else if (flag != 1 && flagEquipmentListing == 0) {
            // Action information
            inflater.inflate(
                    R.menu.site_detail_menu_without_addjob_equipmentlisting,
                    menu);
        } else if (flag != 1) {
            // Action information and equipment listing
            inflater.inflate(R.menu.site_detail_menu_without_add_job, menu);
        } else if (flagActionInformation == 0) {
            // Add job and equipment listing
            inflater.inflate(
                    R.menu.site_detail_menu_without_action_information, menu);
        } else if (flagEquipmentListing == 0) {
            // Add job and Action information
            inflater.inflate(R.menu.site_detail_menu_without_equipmentlisting,
                    menu);
        } else {
            // show all
            inflater.inflate(R.menu.site_detail_menu, menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub

        switch (item.getItemId()) {
            case R.id.action_equipment_listing:
                Intent equipmentIntent = new Intent(SiteDetail.this,
                        EquipmentListCLientDetail.class);
//v51.0.8
                equipmentIntent.putExtra(KEYS.NewJob.CLIENT_ID, clientId);
                equipmentIntent.putExtra(KEYS.NewJob.SITE_ID, siteId);
                equipmentIntent.putExtra(KEYS.NewJob.SITE_NAME, site.getNmSite());
                equipmentIntent.putExtra(KEYS.NewJob.CLIENT_NAME, clientName);

                equipmentIntent.putExtra(KEYS.ClientDetial.ITEM_SELECTION_ENABLED,
                        false);
                SiteDetail.this.startActivity(equipmentIntent);

                break;

            case R.id.action_startjob:
//                Calendar cal1 = Calendar.getInstance();
//                int milisecond1 = ((cal1.get(Calendar.HOUR_OF_DAY) * 3600000) + (cal1
//                        .get(Calendar.MINUTE) * 60000));
//
//                int milisecondMaxtime1 = ((23 * 3600000) + (40 * 60000));
//                if (milisecond1 <= milisecondMaxtime1) {
//                    int equipmentId = -1;
//                    String equipmentName = "";
//                    AddNewJobDialog addNewJobDialog = AddNewJobDialog.newInstance(clientId, siteId, equipmentId,
//                            clientName, site.getNmSite(), equipmentName, new AddNewJobDialog.TakeBackActionListener() {
//                                @Override
//                                public void doOnConfirmClick() {
//
//                                }
//
//                                @Override
//                                public void doOnCancelClick() {
//
//                                }
//                            });
//                    addNewJobDialog.show(getSupportFragmentManager(), "");
//
//                } else {
//
//                    DialogUtils.showInfoDialog(SiteDetail.this,
//                            SiteDetail.this.getString(R.string.textNoJobCreate));
//
//                }

                int equipmentId = -1;
                String equipmentName = "";
                AddNewJobDialog addNewJobDialog = AddNewJobDialog.newInstance(clientId, siteId, equipmentId,
                        clientName, site.getNmSite(), equipmentName, new AddNewJobDialog.TakeBackActionListener() {
                            @Override
                            public void doOnConfirmClick() {

                            }

                            @Override
                            public void doOnCancelClick() {

                            }
                        });
                addNewJobDialog.show(getSupportFragmentManager(), "");

                break;
            case R.id.action_addjob:

//                Calendar cal = Calendar.getInstance();
//                int milisecond = ((cal.get(Calendar.HOUR_OF_DAY) * 3600000) + (cal
//                        .get(Calendar.MINUTE) * 60000));
//
//                int milisecondMaxtime = ((23 * 3600000) + (40 * 60000));
//                if (milisecond <= milisecondMaxtime) {
//                    Intent addJobIntent = new Intent(SiteDetail.this,
//                            AddNewJob.class);
//
//                    addJobIntent.putExtra(KEYS.NewJob.CLIENT_ID, clientId);
//                    addJobIntent.putExtra(KEYS.NewJob.SITE_ID, siteId);
//                    addJobIntent.putExtra(KEYS.NewJob.CLIENT_NAME, clientName);
//                    addJobIntent.putExtra(KEYS.NewJob.SITE_NAME, site.getNmSite());
//
//                    SiteDetail.this.startActivity(addJobIntent);
//
//                } else {
//
//                    DialogUtils.showInfoDialog(SiteDetail.this,
//                            SiteDetail.this.getString(R.string.textNoJobCreate));
//
//                }

                Intent addJobIntent = new Intent(SiteDetail.this,
                        AddNewJob.class);

                addJobIntent.putExtra(KEYS.NewJob.CLIENT_ID, clientId);
                addJobIntent.putExtra(KEYS.NewJob.SITE_ID, siteId);
                addJobIntent.putExtra(KEYS.NewJob.CLIENT_NAME, clientName);
                if (site.getRefCustomer().length()>0){
                    addJobIntent.putExtra(KEYS.NewJob.SITE_NAME, site.getNmSite()+" ("+site.getRefCustomer()+")");
                }else {
                    addJobIntent.putExtra(KEYS.NewJob.SITE_NAME, site.getNmSite());
                }
                SiteDetail.this.startActivity(addJobIntent);

                break;

            case R.id.action_information:
                Intent customIntent = new Intent(SiteDetail.this,
                        CustomFieldSite.class);
                customIntent.putExtra(KEYS.SiteDetails.ID_SITE, siteId);
                startActivity(customIntent);
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isLGDevice() {
        return (Build.MANUFACTURER.contains("LG") || Build.MODEL.contains("LG"));
    }

    private class SiteDetailTabAdapter extends FragmentPagerAdapter {

        public SiteDetailTabAdapter(FragmentManager fm) {
            super(fm);
            // TODO Auto-generated constructor stub
        }

        @Override
        public Fragment getItem(int arg0) {

            Fragment currentFragment = null;
            if (arg0 == 0) {

                Bundle bundle = new Bundle();
                bundle.putInt(KEYS.ClientDetial.ID_CLIENT, clientId);
                bundle.putInt(KEYS.SiteDetails.ID_SITE, siteId);
                bundle.putString(KEYS.ClientDetial.CLIENT_NAME, clientName);
                currentFragment = new SiteDetailFragment();
                currentFragment.setArguments(bundle);

            } else if (arg0 == 1) {
                Bundle bundle = new Bundle();
                bundle.putInt(KEYS.ClientDetial.ID_CLIENT, clientId);
                bundle.putInt(KEYS.SiteDetails.ID_SITE, siteId);
                currentFragment = new AttachmentListSiteDetailsFragment();
                currentFragment.setArguments(bundle);
            }

            return currentFragment;

        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length].toUpperCase();
        }

    }

}
