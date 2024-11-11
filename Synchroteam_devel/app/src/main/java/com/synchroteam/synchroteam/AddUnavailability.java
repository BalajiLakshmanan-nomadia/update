package com.synchroteam.synchroteam;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.synchroteam.dao.Dao;
import com.synchroteam.fragment.UnavailabilityScheduleFragment;
import com.synchroteam.fragment.UnavailabilityStartFragment;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DateChecker;
import com.synchroteam.utils.TabPageIndicator;

/**
 * @author Trident
 *         <p>
 *         This class is used to add unavailability from
 *         UnavailabilitiesFragment
 */
public class AddUnavailability extends AppCompatActivity implements
        OnPageChangeListener {

    // ............................UI...ELEMENTS...STARTS...HERE.....................................
    private ActionBar actionBar;
    private TabPageIndicator indicator;
    private ViewPager unavailabilityPager;

    private UnavailabilityScheduleFragment scheduleFragment;
    private UnavailabilityStartFragment startFragment;

    private Dao dao;
    private SharedPreferences issuesPositionPref;
    private Editor edit;

    private String[] tabs;

    // private static final String TAG = "AddUnavailability";

    // ****************************UI...ELEMENTS...STARTS...HERE*************************************
    // ...........................LIFECYCLE...METHOD...STARTS...HERE..................................
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actionBar = getSupportActionBar();
        setContentView(R.layout.activity_add_unavailability);
        initializeUI();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //New changes
        DateChecker.checkDateAndNavigate(this, dao);

    }

    @Override
    protected void onStop() {
        super.onStop();
        edit.putString("pos", null);
        edit.putString("pos_for_start_fragment", null);
        edit.commit();
    }

    // ****************************LIFECYCLE...METHOD...STARTS...HERE*********************************

    // ............................OVERRIDDEN...METHOD...STARTS...HERE...........................................
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // ****************************OVERRIDDEN...METHOD...ENDS...HERE******************************************
    // .............................LOCAL...METHODS...STARTS...HERE................................................
    private void initializeUI() {
        tabs = new String[]{this.getString(R.string.schedule_title),
                this.getString(R.string.txt_start_activity)};
        unavailabilityPager = (ViewPager) findViewById(R.id.pagerUnavailability);
        unavailabilityPager.setAdapter(new UnavailabilityPagerAdapter(
                getSupportFragmentManager()));
        unavailabilityPager.setOnPageChangeListener(this);

        indicator = (TabPageIndicator) findViewById(R.id.indicator);
//		indicator.setVisibility(View.VISIBLE);
        indicator.setVisibility(View.GONE);
        indicator.setViewPager(unavailabilityPager);

        dao = DaoManager.getInstance();

        issuesPositionPref = getSharedPreferences("issues_pref",
                Context.MODE_PRIVATE);

        edit = issuesPositionPref.edit();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getString(R.string.txt_add_unavailability));
        actionBar.setHomeButtonEnabled(true);

        indicator.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

    }

    public void setStartFragIssuePos(int pos) {
//        startFragment.setIssuePosition(pos);
    }

    public void setScheduleFragIssuePos(int pos) {
        scheduleFragment.setIssuePosition(pos);
    }

    // *****************************LOCAL...METHODS...ENDS...HERE**********************************************

    // ...............................LISTENER...STARTS...HERE.................................................
    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int position) {
        actionBar.setSelectedNavigationItem(position);
    }

    // ******************************LISTENER...ENDS...HERE**********************************************************

    // ..............................ADAPTER...CLASS...STARTS...HERE.............................................
    private class UnavailabilityPagerAdapter extends FragmentStatePagerAdapter {

        public UnavailabilityPagerAdapter(FragmentManager fm) {
            super(fm);
            // TODO Auto-generated constructor stub
        }

        @Override
        public Fragment getItem(int index) {
            switch (index) {
                case 0:
                    scheduleFragment = new UnavailabilityScheduleFragment();
                    return scheduleFragment;
//                case 1:
//                    startFragment = new UnavailabilityStartFragment();
//                    return startFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position % tabs.length];
        }

    }

    // ******************************ADAPTER...CLASS...ENDS...HERE************************************************
}
