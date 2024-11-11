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

import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.AddNewJobDialog;
import com.synchroteam.fragment.AttachmentListClientDetailsFragment;
import com.synchroteam.fragment.ClientDetailFragment;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DateChecker;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.TabPageIndicator;

import java.util.Calendar;

public class ClientDetail extends AppCompatActivity {

    private TabPageIndicator indicator;

    private int clientId;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_client_detail);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        String title = getIntent()
                .getStringExtra(KEYS.ClientDetial.CLIENT_NAME);
        String ref = getIntent().getStringExtra(KEYS.ClientDetial.REF_CUSTOMER);

        try {
            SpannableString titleSpannable = new SpannableString(title);

            titleSpannable.setSpan(
                    new TypefaceSpan(this.getResources().getString(
                            R.string.fontName_hevelicaLight)), 0,
                    titleSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            actionBar.setTitle(isLGDevice() ? title : titleSpannable);

        } catch (Exception e) {
            actionBar.setTitle("");
        }
        clientId = getIntent().getIntExtra(KEYS.ClientDetial.ID_CLIENT, -1);
        if (getIntent().getStringExtra(KEYS.ClientDetial.REF_CUSTOMER) != null && getIntent().getStringExtra(KEYS.ClientDetial.REF_CUSTOMER).length()>0){
            clientName = getIntent().getStringExtra(KEYS.ClientDetial.CLIENT_NAME)+" ("+
                    getIntent().getStringExtra(KEYS.ClientDetial.REF_CUSTOMER)+")";
        }else {
            clientName = getIntent().getStringExtra(KEYS.ClientDetial.CLIENT_NAME);
        }

        //actionBar.setTitle(titleSpannable);

        CONTENT = new String[]{getString(R.string.textDescriptionLable),
                getString(R.string.textAttachmentLableTv)};
        indicator = (TabPageIndicator) findViewById(R.id.indicator);
        viewPager = (ViewPager) findViewById(R.id.pagerClientSection);

        viewPager.setAdapter(new ClientDetailTabAdapter(
                getSupportFragmentManager()));
        indicator.setViewPager(viewPager);

        // initialize the object
        dataAccessObject = DaoManager.getInstance();
        dao = DaoManager.getInstance();

    }

    @Override
    protected void onResume() {
        super.onResume();

        //New changes
        DateChecker.checkDateAndNavigate(this, dataAccessObject);

    }

    public boolean isLGDevice() {
        return (Build.MANUFACTURER.contains("LG") || Build.MODEL.contains("LG"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        MenuInflater inflater = getMenuInflater();
        // inflater.inflate(R.menu.client_detail_menu, menu);

        // set the condition to show the add job or not
        int flag = dataAccessObject.getAddIntervFlag();

        //action information
        int flagActionInformation = 0;
//		flagActionInformation = dao.getCustomFieldsClients(clientId).size(); 
        flagActionInformation = dao.getCFForClientDetail(clientId).size();

        //site listing
        int flagSiteListing = 0;
        flagSiteListing = dataAccessObject.getSiteCount(clientId);

        //equipment listing
        int flagEquipmentListing = 0;
        flagEquipmentListing = dataAccessObject.getEquipementsForSiteCount(clientId,
                -1);


        if (flag != 1 && flagActionInformation == 0 && flagSiteListing == 0 && flagEquipmentListing == 0) {
            //nothing to display

        } else if (flagActionInformation == 0 && flagSiteListing == 0 && flagEquipmentListing == 0) {
            //Add job
            inflater.inflate(R.menu.client_detail_menu_with_addjob, menu);
        } else if (flag != 1 && flagActionInformation == 0 && flagSiteListing == 0) {
            //Equipment list show
            inflater.inflate(R.menu.client_detail_menu_with_equipmentlisting, menu);
        } else if (flag != 1 && flagSiteListing == 0 && flagEquipmentListing == 0) {
            //Action information
            inflater.inflate(R.menu.client_detail_menu_with_actioninformation, menu);
        } else if (flag != 1 && flagActionInformation == 0 && flagEquipmentListing == 0) {
            //Site Listing
            inflater.inflate(R.menu.client_detail_menu_with_sitelisting, menu);
        } else if (flag != 1 && flagActionInformation == 0) {
            //SiteListing & Equipment Listing
            inflater.inflate(
                    R.menu.client_detail_menu_without_add_job_action_information, menu);
        } else if (flagSiteListing == 0 && flagEquipmentListing == 0) {
            // Add job & Action Information
            inflater.inflate(
                    R.menu.client_detail_menu_without_sitelisting_equipmentlisting, menu);
        } else if (flagActionInformation == 0 && flagEquipmentListing == 0) {
            //Add job and Site Listing
            inflater.inflate(
                    R.menu.client_detail_menu_without_equipmentlisting_actioninformation, menu);
        } else if (flagActionInformation == 0 && flagSiteListing == 0) {
            //Add job and Equipment Listing
            inflater.inflate(
                    R.menu.client_detail_menu_without_sitelisting_actioninformation, menu);
        } else if (flag != 1 && flagEquipmentListing == 0) {
            //Action information and Site Listing
            inflater.inflate(
                    R.menu.client_detail_menu_without_addjob_equipmentlising, menu);
        } else if (flag != 1 && flagSiteListing == 0) {
            //Action information and Equipment Listing
            inflater.inflate(
                    R.menu.client_detail_menu_without_addjob_sitelisting, menu);
        } else if (flag != 1) {
            //Action information , Site Listing and Equipment Listing
            inflater.inflate(R.menu.client_detail_menu_without_add_job, menu);
        } else if (flagSiteListing == 0) {
            //Add Job , Action information and Equipment Listing
            inflater.inflate(
                    R.menu.client_detail_menu_without_site_listing, menu);
        } else if (flagEquipmentListing == 0) {
            //Add job , Action information and Site Listing
            inflater.inflate(
                    R.menu.client_detail_menu_without_equipment_listing, menu);

        } else if (flagActionInformation == 0) {
            //Add job , Site Listing and Equipment Listing
            inflater.inflate(
                    R.menu.client_detail_menu_without_action_information, menu);

        } else {
            //show all
            inflater.inflate(R.menu.client_detail_menu, menu);
        }


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub

        switch (item.getItemId()) {
            case R.id.action_addjob:


//                Calendar cal = Calendar.getInstance();
//                int milisecond = ((cal.get(Calendar.HOUR_OF_DAY) * 3600000) + (cal
//                        .get(Calendar.MINUTE) * 60000));
//
//                int milisecondMaxtime = ((23 * 3600000) + (40 * 60000));
//                if (milisecond <= milisecondMaxtime) {
//
//                    Intent intent = new Intent(ClientDetail.this, AddNewJob.class);
//
//                    intent.putExtra(KEYS.NewJob.CLIENT_ID, clientId);
//                    intent.putExtra(KEYS.NewJob.SITE_ID, -1);
//                    intent.putExtra(KEYS.NewJob.CLIENT_NAME, clientName);
//                    intent.putExtra(KEYS.NewJob.SITE_NAME, "");
//
//                    ClientDetail.this.startActivity(intent);
//
//                } else {
//
//                    DialogUtils.showInfoDialog(
//                            ClientDetail.this,
//                            ClientDetail.this
//                                    .getString(R.string.textNoJobCreate));
//
//                }

                Intent intent = new Intent(ClientDetail.this, AddNewJob.class);

                intent.putExtra(KEYS.NewJob.CLIENT_ID, clientId);
                intent.putExtra(KEYS.NewJob.SITE_ID, -1);
                intent.putExtra(KEYS.NewJob.CLIENT_NAME, clientName);
                intent.putExtra(KEYS.NewJob.SITE_NAME, "");

                ClientDetail.this.startActivity(intent);

                break;

            case R.id.action_startjob:
                Calendar cal1 = Calendar.getInstance();
                int milisecond1 = ((cal1.get(Calendar.HOUR_OF_DAY) * 3600000) + (cal1
                        .get(Calendar.MINUTE) * 60000));

                int milisecondMaxtime1 = ((23 * 3600000) + (40 * 60000));
                int siteId = -1;
                int equipmentId = -1;
                String siteName = "";
                String equipmentName = "";

//                if (milisecond1 <= milisecondMaxtime1) {
//                    AddNewJobDialog addNewJobDialog = AddNewJobDialog.newInstance(clientId, siteId, equipmentId,
//                            clientName, siteName, equipmentName, new AddNewJobDialog.TakeBackActionListener() {
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
//                    DialogUtils.showInfoDialog(ClientDetail.this,
//                            ClientDetail.this.getString(R.string.textNoJobCreate));
//
//                }

                AddNewJobDialog addNewJobDialog = AddNewJobDialog.newInstance(clientId, siteId, equipmentId,
                        clientName, siteName, equipmentName, new AddNewJobDialog.TakeBackActionListener() {
                            @Override
                            public void doOnConfirmClick() {

                            }

                            @Override
                            public void doOnCancelClick() {

                            }
                        });
                addNewJobDialog.show(getSupportFragmentManager(), "");

                break;
            case R.id.action_site_list:

                Intent siteIntent = new Intent(ClientDetail.this,
                        SiteListingClientDetails.class);

                siteIntent.putExtra(KEYS.NewJob.CLIENT_ID, clientId);
                siteIntent.putExtra(KEYS.NewJob.CLIENT_NAME, clientName);

                siteIntent
                        .putExtra(KEYS.ClientDetial.ITEM_SELECTION_ENABLED, false);

                ClientDetail.this.startActivity(siteIntent);

                break;

            case R.id.action_equipment_listing:
                Intent equipmentIntent = new Intent(ClientDetail.this,
                        EquipmentListCLientDetail.class);

                equipmentIntent.putExtra(KEYS.NewJob.CLIENT_ID, clientId);
                equipmentIntent.putExtra(KEYS.NewJob.CLIENT_NAME, clientName);
                equipmentIntent.putExtra(KEYS.NewJob.SITE_ID, -1);
                equipmentIntent.putExtra(KEYS.ClientDetial.ITEM_SELECTION_ENABLED,
                        false);

                ClientDetail.this.startActivity(equipmentIntent);

                break;

            case R.id.action_information:
                Intent customIntent = new Intent(ClientDetail.this,
                        CustomFieldClient.class);
                customIntent.putExtra(KEYS.ClientDetial.ID_CLIENT, clientId);
                startActivity(customIntent);

                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ClientDetailTabAdapter extends FragmentPagerAdapter {

        public ClientDetailTabAdapter(FragmentManager fm) {
            super(fm);
            // TODO Auto-generated constructor stub
        }

        @Override
        public Fragment getItem(int arg0) {

            Fragment currentFragment = null;
            if (arg0 == 0) {

                Bundle bundle = new Bundle();
                bundle.putInt(KEYS.ClientDetial.ID_CLIENT, clientId);

                currentFragment = new ClientDetailFragment();
                currentFragment.setArguments(bundle);

            } else if (arg0 == 1) {
                Bundle bundle = new Bundle();
                bundle.putInt(KEYS.ClientDetial.ID_CLIENT, clientId);
                currentFragment = new AttachmentListClientDetailsFragment();
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
