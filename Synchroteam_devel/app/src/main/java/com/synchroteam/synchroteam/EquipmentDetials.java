package com.synchroteam.synchroteam;

import java.util.Calendar;

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
import com.synchroteam.fragment.AttachmentListEquipmentDetailsFragment;
import com.synchroteam.fragment.EquipmentDetailFragment;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DateChecker;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.TabPageIndicator;

public class EquipmentDetials extends AppCompatActivity {

    private TabPageIndicator indicator;

    private ActionBar actionBar;
    private ViewPager viewPager;

    private String[] CONTENT = null;

    private int clientId, siteId, equipmentId;
    private String clientName, siteName, equipmentName, publicLink;

    /**
     * The dao.
     */
    private Dao dao;

    /**
     * The data access object.
     */
    private Dao dataAccessObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_client_detail);
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            clientId = bundle.getInt(KEYS.EquipmentDetail.ID_CLIENT);
            siteId = bundle.getInt(KEYS.EquipmentDetail.ID_SITE);
            equipmentId = bundle.getInt(KEYS.EquipmentDetail.EQUIPMENTS_ID);
            clientName = bundle.getString(KEYS.EquipmentDetail.CLIENT_NAME);
            siteName = bundle.getString(KEYS.EquipmentDetail.SITE_NAME);

            equipmentName = bundle
                    .getString(KEYS.EquipmentDetail.EQUIPMENTS_NAME);
            publicLink = bundle
                    .getString(KEYS.EquipmentDetail.PUBLIC_LINK);
        }

        String title = equipmentName;

        try {
            SpannableString titleSpannable = new SpannableString(equipmentName);

            titleSpannable
                    .setSpan(
                            new TypefaceSpan(this.getResources().getString(
                                    R.string.fontName_hevelicaLight)), 0,
                            titleSpannable.length(),
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            // actionBar.setTitle(titleSpannable);
            actionBar.setTitle(isLGDevice() ? title : titleSpannable);
        } catch (Exception e) {
            Logger.printException(e);
            actionBar.setTitle("");
        }

        CONTENT = new String[]{getString(R.string.textDescriptionLable),
                getString(R.string.textAttachmentLableTv)};
        indicator = (TabPageIndicator) findViewById(R.id.indicator);
        viewPager = (ViewPager) findViewById(R.id.pagerClientSection);

        viewPager.setAdapter(new EquipmentsDetailTabAdapter(
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
        // inflater.inflate(R.menu.equipment_detail_menu, menu);

        // set the condition to show the add job or not
        int flag = dataAccessObject.getAddIntervFlag();

        // int flagActionInformation = dao.getCustomFieldEquipment(equipmentId)
        // .size();
        int flagActionInformation = dao.getCFForEquip(equipmentId).size();

        if (flag != 1 && flagActionInformation == 0) {
            // nothing to display
        } else if (flagActionInformation == 0) {
            inflater.inflate(
                    R.menu.equipment_detail_menu_without_action_information,
                    menu); // remove action information

        } else if (flag != 1) {
            inflater.inflate(R.menu.equipment_detail_menu_without_add_job, menu); // remove
            // add
            // job

        } else {
            inflater.inflate(R.menu.equipment_detail_menu, menu); // include
            // both
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
//                    Intent addJobIntent = new Intent(EquipmentDetials.this,
//                            AddNewJob.class);
//
//                    addJobIntent.putExtra(KEYS.NewJob.CLIENT_ID, clientId);
//                    addJobIntent.putExtra(KEYS.NewJob.SITE_ID, siteId);
//                    addJobIntent.putExtra(KEYS.NewJob.EQUIPMENTS_ID, equipmentId);
//                    addJobIntent.putExtra(KEYS.NewJob.CLIENT_NAME, clientName);
//                    addJobIntent.putExtra(KEYS.NewJob.SITE_NAME, siteName);
//                    addJobIntent.putExtra(KEYS.NewJob.EQUIPMENTS_NAME,
//                            equipmentName);
//
//                    EquipmentDetials.this.startActivity(addJobIntent);
//
//                } else {
//
//                    DialogUtils.showInfoDialog(EquipmentDetials.this,
//                            EquipmentDetials.this
//                                    .getString(R.string.textNoJobCreate));
//
//                }

                Intent addJobIntent = new Intent(EquipmentDetials.this,
                        AddNewJob.class);

                addJobIntent.putExtra(KEYS.NewJob.CLIENT_ID, clientId);
                addJobIntent.putExtra(KEYS.NewJob.SITE_ID, siteId);
                addJobIntent.putExtra(KEYS.NewJob.EQUIPMENTS_ID, equipmentId);
                addJobIntent.putExtra(KEYS.NewJob.CLIENT_NAME, clientName);
                addJobIntent.putExtra(KEYS.NewJob.SITE_NAME, siteName);
                addJobIntent.putExtra(KEYS.NewJob.EQUIPMENTS_NAME,
                        equipmentName);

                EquipmentDetials.this.startActivity(addJobIntent);
                break;

            case R.id.action_startjob:
//                Calendar cal1 = Calendar.getInstance();
//                int milisecond1 = ((cal1.get(Calendar.HOUR_OF_DAY) * 3600000) + (cal1
//                        .get(Calendar.MINUTE) * 60000));
//
//                int milisecondMaxtime1 = ((23 * 3600000) + (40 * 60000));
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
//                    DialogUtils.showInfoDialog(EquipmentDetials.this,
//                            EquipmentDetials.this.getString(R.string.textNoJobCreate));
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

            case R.id.action_information:
                Intent customIntent = new Intent(EquipmentDetials.this,
                        CustomFieldEquipment.class);
                customIntent.putExtra(KEYS.EquipmentDetail.EQUIPMENTS_ID,
                        equipmentId);
                startActivity(customIntent);

                break;

            case android.R.id.home:
                finish();

                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class EquipmentsDetailTabAdapter extends FragmentPagerAdapter {

        public EquipmentsDetailTabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {

            Fragment currentFragment = null;
            if (arg0 == 0) {

                Bundle bundle = new Bundle();
                bundle.putInt(KEYS.ClientDetial.ID_CLIENT, clientId);
                bundle.putInt(KEYS.EquipmentDetail.ID_SITE, siteId);
                bundle.putString(KEYS.EquipmentDetail.CLIENT_NAME, clientName);
                bundle.putString(KEYS.EquipmentDetail.SITE_NAME, siteName);
                bundle.putString(KEYS.EquipmentDetail.EQUIPMENTS_NAME,
                        equipmentName);
                bundle.putString(KEYS.EquipmentDetail.PUBLIC_LINK,
                        publicLink);
                bundle.putInt(KEYS.EquipmentDetail.EQUIPMENTS_ID, equipmentId);
                currentFragment = new EquipmentDetailFragment();
                currentFragment.setArguments(bundle);

            } else if (arg0 == 1) {
                Bundle bundle = new Bundle();
                bundle.putInt(KEYS.EquipmentDetail.EQUIPMENTS_ID, equipmentId);
                currentFragment = new AttachmentListEquipmentDetailsFragment();
                currentFragment.setArguments(bundle);
            }

            return currentFragment;

        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return CONTENT[position % CONTENT.length].toUpperCase();
        }

    }

}
