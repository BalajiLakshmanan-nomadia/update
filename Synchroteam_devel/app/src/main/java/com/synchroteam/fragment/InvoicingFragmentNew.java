package com.synchroteam.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synchroteam.beans.Invoice_Quotation_Beans;
import com.synchroteam.dao.Dao;
import com.synchroteam.events.AddInvoiceQuotationEvent;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.CirclePageIndicator;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.KEYS;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * This class holds the view for Parts & Service and Invoice/Quotation fragment.
 * We can navigate from one fragment to other by using viewpager.
 *
 * @author Trident
 */
public class InvoicingFragmentNew extends Fragment {

    // .................................INSTANCE...VARIABLE...STARTS...HERE......................................

    private ViewPager invoicingViewPager;
    private CirclePageIndicator mIndicator;

    private String idIntervention;
    private int status, idUser, idClient, idSite;
    private ArrayList<String> invoiceIdList;
    ArrayList<Invoice_Quotation_Beans> invoiceList;

    private static int NO_OF_PAGES = 1;
    private Dao dao;
    private ViewPagerAdapter pagerAdapter;

    // *********************************INSTANCE...VARIABLE...ENDS...HERE****************************************

    // ..................................LIFECYCLE...METHOD...STARTS...HERE.......................................
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_invoicing,
                container, false);
        dao = DaoManager.getInstance();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);
        initiateView(view);
        return view;
    }

    // **********************************LIFECYCLE...METHODS...ENDS...HERE******************************************

    // ...................................LOCAL...METHOD...STARTS...HERE..............................................

    /**
     * Initiate the UI elements here.
     *
     * @param view
     */
    private void initiateView(View view) {
        invoicingViewPager = (ViewPager) view
                .findViewById(R.id.inviocingViewPager);
        mIndicator = (CirclePageIndicator) view
                .findViewById(R.id.circlePageIndicator);

        Bundle bundle = getArguments();
        idIntervention = bundle.getString("id_interv");
        idUser = bundle.getInt("id_user");
        status = bundle.getInt("cd_statut");
        idClient = bundle.getInt("idClient");
        idSite = bundle.getInt(KEYS.JObDetail.IDSITE);
        getInvoiceOrQuotaionList();
        invoiceIdList = new ArrayList<>();


        updateAdapter();

    }

    private void updateAdapter() {
        if (invoiceList != null && invoiceList.size() > 0) {
            NO_OF_PAGES = invoiceList.size() + 1;
        } else {
            NO_OF_PAGES = 1;
        }
        pagerAdapter = new ViewPagerAdapter(getActivity()
                .getSupportFragmentManager(), invoiceList, NO_OF_PAGES);
        invoicingViewPager.setAdapter(pagerAdapter);
        mIndicator.setViewPager(invoicingViewPager);
        invoicingViewPager.setOffscreenPageLimit(NO_OF_PAGES);

        createCircleIndicator();
    }

    /**
     * gets the invoice or quotation for current job.
     */
    private void getInvoiceOrQuotaionList() {
        invoiceList = new ArrayList<>();
        invoiceList = dao.getInvoiceQuotationDetailsList(idIntervention);

    }

    /**
     * On event.
     *
     * @param addInvoiceQuotationEvent :
     */
    public void onEvent(AddInvoiceQuotationEvent addInvoiceQuotationEvent) {
        int currentItem = 0;
        getInvoiceOrQuotaionList();
        if (invoiceList != null && invoiceList.size() > 0) {
            if (addInvoiceQuotationEvent.inventoryId != null &&
                    addInvoiceQuotationEvent.inventoryId.trim().length() > 1) {
                for (int i = 0; i < invoiceList.size(); i++) {
                    if (invoiceList.get(i).getId().trim().equals(addInvoiceQuotationEvent.inventoryId.trim())) {
                        currentItem = i;
                        break;
                    }
                }
            }
        }
        updateAdapter();
        if (invoicingViewPager.getAdapter() != null) {
            invoicingViewPager.getAdapter().notifyDataSetChanged();
            invoicingViewPager.setCurrentItem(currentItem);
        }
    }

    /**
     * set the parameters to create the circle page indicator
     */
    private void createCircleIndicator() {
        final float density = getActivity().getResources().getDisplayMetrics().density;
        mIndicator.setFillColor(ContextCompat.getColor(getActivity(), R.color.actionbarColor));
        mIndicator.setPageColor(ContextCompat.getColor(getActivity(), R.color.status_selector_color));
        mIndicator.setBackgroundColor(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mIndicator.setStrokeWidth(0);
        mIndicator.setPadding(10, 10, 10, 10);
        // sets the radius(size) of the indicator
        mIndicator.setRadius(5 * density);
    }

    // ***********************************LOCAL...METHOD...ENDS...HERE************************************************

    // .....................................VIEWPAGER...ADAPTER...STARTS...HERE........................................

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        ArrayList<Invoice_Quotation_Beans> invoiceList;
        int noOfPages;

        public ViewPagerAdapter(FragmentManager fm, ArrayList<Invoice_Quotation_Beans> invoiceList, int noOfPages) {
            super(fm);
            this.invoiceList = invoiceList;
            this.noOfPages = noOfPages;
        }

        public void updateInvoicePager(ArrayList<Invoice_Quotation_Beans> invoiceList, int noOfPages) {
            this.invoiceList = invoiceList;
            this.noOfPages = noOfPages;
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int index) {

            if (index == NO_OF_PAGES - 1) {
                Fragment currentFragment = null;

                Bundle bundleInvoice = new Bundle();

                bundleInvoice.putString("id_interv", idIntervention);
                bundleInvoice.putInt("id_client", idClient);
                bundleInvoice.putInt("id_site", idSite);
                currentFragment = new InvoiceQuotationFragment();
                currentFragment.setArguments(bundleInvoice);

                return currentFragment;
            } else {
                Fragment currentFragment = null;

                Bundle bundleInvoice = new Bundle();

                bundleInvoice.putString("id_interv", idIntervention);
                bundleInvoice.putInt("id_client", idClient);
                bundleInvoice.putInt("id_site", idSite);
                bundleInvoice.putSerializable("invoice_list", invoiceList.get(index));
                currentFragment = new InvoiceQuotationFragmentNew();
                currentFragment.setArguments(bundleInvoice);

                return currentFragment;
            }

        }

        @Override
        public int getCount() {
            return noOfPages;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
    }

    // *************************************VIEWPAGER...ADAPTER...ENDS...HERE*******************************************

}
