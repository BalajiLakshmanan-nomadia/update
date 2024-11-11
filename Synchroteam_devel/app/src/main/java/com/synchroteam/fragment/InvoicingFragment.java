package com.synchroteam.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.CirclePageIndicator;
import com.synchroteam.utils.KEYS;

/**
 * This class holds the view for Parts & Service and Invoice/Quotation fragment.
 * We can navigate from one fragment to other by using viewpager.
 * 
 * @author Trident
 *
 */
public class InvoicingFragment extends Fragment {

	// .................................INSTANCE...VARIABLE...STARTS...HERE......................................

	private ViewPager invoicingViewPager;
	private CirclePageIndicator mIndicator;

	private String idIntervention;
	private int status, idUser, idClient, idSite;

	private static final int NO_OF_PAGES = 2;

	// *********************************INSTANCE...VARIABLE...ENDS...HERE****************************************

	// ..................................LIFECYCLE...METHOD...STARTS...HERE.......................................
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.layout_fragment_invoicing,
				container, false);
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
		invoicingViewPager.setAdapter(new ViewPagerAdapter(getActivity()
				.getSupportFragmentManager()));
		mIndicator.setViewPager(invoicingViewPager);

		createCircleIndicator();

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

	private class ViewPagerAdapter extends FragmentPagerAdapter {

		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int index) {
			Fragment currentFragment = null;
			switch (index) {
			case 0:
				Bundle bundle = new Bundle();

				bundle.putString("id_interv", idIntervention);
				bundle.putInt("cd_statut", status);
				bundle.putInt("id_user", idUser);
				currentFragment = new CatalougeJobDetailFragment();
				currentFragment.setArguments(bundle);
				break;
			case 1:
				Bundle bundleInvoice = new Bundle();

				bundleInvoice.putString("id_interv", idIntervention);
				bundleInvoice.putInt("id_client", idClient);
				bundleInvoice.putInt("id_site", idSite);
				currentFragment = new InvoiceQuotationFragment();
				currentFragment.setArguments(bundleInvoice);
				break;
			}
			return currentFragment;
		}

		@Override
		public int getCount() {
			return NO_OF_PAGES;
		}

	}

	// *************************************VIEWPAGER...ADAPTER...ENDS...HERE*******************************************

}
