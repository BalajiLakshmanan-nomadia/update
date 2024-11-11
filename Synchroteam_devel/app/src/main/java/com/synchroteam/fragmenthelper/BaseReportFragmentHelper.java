package com.synchroteam.fragmenthelper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.TextView;

import com.synchroteam.TypefaceLibrary.Button;
import com.synchroteam.baseactivity.SyncroTeamBaseActivity;
import com.synchroteam.beans.EnableSynchronizationAddJobEvent;
import com.synchroteam.fragment.BaseFragment;
import com.synchroteam.fragment.ReportsWithDateFragment;
import com.synchroteam.fragment.ReportsWithoutDateFragment;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DateEvent;
import com.synchroteam.utils.DateFormatUtils;

import java.util.Calendar;
import java.util.Date;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc
/**
 * The Class BaseReportFragmentHelper is responsible to inflate and control
 * various actions on Base Reports Fragment. created for future use
 * 
 * @author Ideavate Solution
 */
public class BaseReportFragmentHelper implements HelperInterface {

	/** The syncro team base activity. */
	private BaseFragment baseFragment;

	/** The syncro team base activity. */
	private SyncroTeamBaseActivity syncroTeamBaseActivity;

	/** The pager reports list. */
	private ViewPager pagerReportsList;

	/** The tab with name bt. */
	private Button tabWithoutDateBt, tabWithDateBt;

	/** The tab selector by date tv. */
	private TextView tabSelectorAllTv, tabSelectorByDateTv;

	/** The reports page adapter. */
	private ReportsPageAdapter reportsPageAdapter;

	/** The fragment manager. */
	private FragmentManager fragmentManager;

	/** The factory. */
	private LayoutInflater factory;

	/**
	 * Instantiates a new base report fragment helper.
	 *
	 * @param baseFragment
	 *            the base fragment
	 * @param syncroTeamBaseActivity
	 *            the syncro team base activity
	 */
	public BaseReportFragmentHelper(BaseFragment baseFragment,
			SyncroTeamBaseActivity syncroTeamBaseActivity) {
		// TODO Auto-generated constructor stub
		this.syncroTeamBaseActivity = syncroTeamBaseActivity;
		this.baseFragment = baseFragment;
		factory = LayoutInflater.from(syncroTeamBaseActivity);

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
		View view = inflater.inflate(R.layout.layout_report, null);
		pagerReportsList = (ViewPager) view.findViewById(R.id.pagerReports);
		fragmentManager = baseFragment.getChildFragmentManager();
		if (reportsPageAdapter == null) {
			reportsPageAdapter = new ReportsPageAdapter(fragmentManager);
		}

		pagerReportsList.setAdapter(reportsPageAdapter);
		pagerReportsList.setOnPageChangeListener(simpleOnPageChangeListener);

		tabWithoutDateBt = (Button) view
				.findViewById(R.id.tab_reports_without_date);
		tabWithDateBt = (Button) view.findViewById(R.id.tab_reports_with_date);
		tabSelectorAllTv = (TextView) view
				.findViewById(R.id.all_repots_tab_selector);
		tabSelectorByDateTv = (TextView) view
				.findViewById(R.id.bydate_repots_tab_selector);
		tabWithoutDateBt.setOnClickListener(onClickListener);
		tabWithDateBt.setOnClickListener(onClickListener);
		EventBus.getDefault().post(new EnableSynchronizationAddJobEvent());
		return view;
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
		// TODO Auto-generated method stub

	}

	/** The on click listener. */
	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			if (id == R.id.tab_reports_with_date) {
				if (pagerReportsList.getCurrentItem() != 1) {
					pagerReportsList.setCurrentItem(1);
					tabSelectorByDateTv
							.setBackgroundResource(R.color.colorOrangeTabBarReportsSelected);
					tabSelectorAllTv.setVisibility(View.GONE);
					tabSelectorByDateTv.setVisibility(View.VISIBLE);
					tabWithDateBt.setTextColor(syncroTeamBaseActivity
							.getResources().getColor(
									R.color.colorOrangeTabBarReportsSelected));
					tabWithoutDateBt.setTextColor(syncroTeamBaseActivity
							.getResources().getColor(
									R.color.colorWhiteTabBarReportsUnSelected));

				} else {
					showDatePicker();
				}

			} else if (id == R.id.tab_reports_without_date) {
				pagerReportsList.setCurrentItem(0);
				tabWithoutDateBt.setTextColor(syncroTeamBaseActivity
						.getResources().getColor(
								R.color.colorOrangeTabBarReportsSelected));
				tabWithDateBt.setTextColor(syncroTeamBaseActivity
						.getResources().getColor(
								R.color.colorWhiteTabBarReportsUnSelected));
				tabSelectorAllTv
						.setBackgroundResource(R.color.colorOrangeTabBarReportsSelected);
				tabSelectorAllTv.setVisibility(View.VISIBLE);
				tabSelectorByDateTv.setVisibility(View.GONE);
			}

		}

	};

	/**
	 * The Class ReportsPageAdapter is to initialise and inflate view of various
	 * Tabs under reports list.
	 */
	private class ReportsPageAdapter extends FragmentStatePagerAdapter {

		/**
		 * Instantiates a new reports page adapter.
		 * 
		 * @param fm
		 *            the fm
		 */
		public ReportsPageAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
		 */
		@Override
		public Fragment getItem(int arg0) {

			Fragment currentFragment = null;
			if (arg0 == 0) {
				currentFragment = new ReportsWithoutDateFragment();
			} else if (arg0 == 1) {
				currentFragment = new ReportsWithDateFragment();
			}

			return currentFragment;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.support.v4.view.PagerAdapter#getCount()
		 */
		@Override
		public int getCount() {

			return 2;
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

	/**
	 * Show date picker .
	 */
	private void showDatePicker() {
		// TODO Auto-generated method stub
		final View alertDialogView2 = factory.inflate(
				R.layout.show_date_picker, null);
		AlertDialog.Builder adb = new AlertDialog.Builder(
				syncroTeamBaseActivity);
		adb.setView(alertDialogView2);
		adb.setTitle(R.string.textDateSmallLable);

		final DatePicker datePicker = (DatePicker) alertDialogView2
				.findViewById(R.id.start_date);
		final android.widget.TextView txtFullDate = (android.widget.TextView) alertDialogView2
				.findViewById(R.id.txt_full_date);

		//Hide the full date view in picker, if it is above or from lollipop
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			txtFullDate.setVisibility(View.GONE);
		}

		adb.setIcon(R.drawable.cal_icon);
		adb.setPositiveButton(syncroTeamBaseActivity.getString(R.string.ok).toUpperCase(),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						int mJour = datePicker.getDayOfMonth();
						int mMois = datePicker.getMonth();
						int mAnnee = datePicker.getYear() - 1900;

						@SuppressWarnings("deprecation")
						Date date = new Date(mAnnee, mMois, mJour);

						EventBus.getDefault().post(new DateEvent(date));
						if (dialog != null)
							dialog.dismiss();

					}
				});

		adb.setNegativeButton(R.string.textCancelLable,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				});

		// new changes......
		// To show the date changed in a textview abbove the date pickeer.

		Calendar thisDay = Calendar.getInstance();

		txtFullDate.setText(DateFormatUtils.getDateString(
				thisDay.get(Calendar.DAY_OF_MONTH),
				thisDay.get(Calendar.MONTH), thisDay.get(Calendar.YEAR)));

		datePicker.init(thisDay.get(Calendar.YEAR),
				thisDay.get(Calendar.MONTH),
				thisDay.get(Calendar.DAY_OF_MONTH),
				new OnDateChangedListener() {
					@Override
					public void onDateChanged(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {

						txtFullDate.setText(DateFormatUtils.getDateString(
								dayOfMonth, monthOfYear, year));
					}
				});

		adb.show();

	}

	/** The simple on page change listener. */
	private ViewPager.SimpleOnPageChangeListener simpleOnPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
		public void onPageSelected(int position) {
			if (position == 1) {

				tabSelectorByDateTv
						.setBackgroundResource(R.color.colorOrangeTabBarReportsSelected);
				tabSelectorAllTv.setVisibility(View.GONE);
				tabSelectorByDateTv.setVisibility(View.VISIBLE);
				tabWithDateBt.setTextColor(syncroTeamBaseActivity
						.getResources().getColor(
								R.color.colorOrangeTabBarReportsSelected));
				tabWithoutDateBt.setTextColor(syncroTeamBaseActivity
						.getResources().getColor(
								R.color.colorWhiteTabBarReportsUnSelected));
				showDatePicker();

			} else if (position == 0) {
				tabWithoutDateBt.setTextColor(syncroTeamBaseActivity
						.getResources().getColor(
								R.color.colorOrangeTabBarReportsSelected));
				tabWithDateBt.setTextColor(syncroTeamBaseActivity
						.getResources().getColor(
								R.color.colorWhiteTabBarReportsUnSelected));
				tabSelectorAllTv
						.setBackgroundResource(R.color.colorOrangeTabBarReportsSelected);
				tabSelectorAllTv.setVisibility(View.VISIBLE);
				tabSelectorByDateTv.setVisibility(View.GONE);

			}

		};

	};

}
