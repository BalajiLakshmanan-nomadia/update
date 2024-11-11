package com.synchroteam.mvp.view;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synchroteam.events.VerifyReportEvent;
import com.synchroteam.fragment.ReportsJobDetailFragment;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.CirclePageIndicator;
import com.synchroteam.utils.KEYS;

import de.greenrobot.event.EventBus;

/**
 * This class holds the view pager for showing reports screen & report view screen
 * </p>
 * Created by Trident on 11/18/2016.
 */

public class ReportsFragment extends Fragment {

    private ViewPager vpReport;
    private CirclePageIndicator cpIndicator;

    private int idModel;
    private int idUser;
    private int status;
    private String idJob;
    private String dtCreated;

    private static final int NO_OF_PAGES = 2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_report, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initiateView(view);
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    /**
     * Event for navigating to report view fragment
     *
     * @param verifyReportEvent
     */
    public void onEvent(VerifyReportEvent verifyReportEvent) {
        vpReport.setCurrentItem(1);
    }

    private void initiateView(View view) {
        vpReport = (ViewPager) view.findViewById(R.id.vp_report);
        cpIndicator = (CirclePageIndicator) view.findViewById(R.id.cp_indicator);

        getValues();
        configureCpIndicator();


        vpReport.setAdapter(new ReportPagerAdapter(getActivity().getSupportFragmentManager() ));
        cpIndicator.setViewPager(vpReport);

    }

    private void getValues() {
        idModel = getArguments().getInt(KEYS.CurrentJobs.ID_MODEL);
        status = getArguments().getInt(KEYS.CurrentJobs.CD_STATUS);
        idUser = getArguments().getInt(KEYS.CurrentJobs.ID_USER);
        idJob = getArguments().getString(KEYS.CurrentJobs.ID);
        dtCreated = getArguments().getString(KEYS.CurrentJobs.DT_CREATED);
    }

    private void configureCpIndicator() {
        final float density = getActivity().getResources().getDisplayMetrics().density;
        cpIndicator.setFillColor(ContextCompat.getColor(getActivity(), R.color.actionbarColor));
        cpIndicator.setPageColor(ContextCompat.getColor(getActivity(), R.color.status_selector_color));
        cpIndicator.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        cpIndicator.setStrokeWidth(0);
        cpIndicator.setPadding(10, 10, 10, 10);
        cpIndicator.setRadius(5 * density);
    }

    private class ReportPagerAdapter extends FragmentStatePagerAdapter {

        public ReportPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    Bundle bundle = new Bundle();
                    bundle.putInt(KEYS.CurrentJobs.ID_MODEL, idModel);
                    bundle.putInt(KEYS.CurrentJobs.CD_STATUS, status);
                    bundle.putInt(KEYS.CurrentJobs.ID_USER, idUser);
                    bundle.putString(KEYS.CurrentJobs.ID, idJob);
                    bundle.putString(KEYS.CurrentJobs.DT_CREATED, dtCreated);
                    ReportsJobDetailFragment reportFragment = new ReportsJobDetailFragment();
                    reportFragment.setArguments(bundle);
                    return reportFragment;
                case 1:
                    Bundle bundleView = new Bundle();
                    bundleView.putString(KEYS.CurrentJobs.DT_CREATED, dtCreated);
                    bundleView.putString(KEYS.CurrentJobs.ID, idJob);
                    ReportViewFragmentNew reportViewFragment = ReportViewFragmentNew.getInstance();
                    reportViewFragment.setArguments(bundleView);
                    return reportViewFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return NO_OF_PAGES;
        }
    }
}
