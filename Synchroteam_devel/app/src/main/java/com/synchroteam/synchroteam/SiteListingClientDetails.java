package com.synchroteam.synchroteam;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.TypefaceSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView.OnEditorActionListener;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.Site;
import com.synchroteam.dao.Dao;
import com.synchroteam.listadapters.SiteListAdapter;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;

import java.util.ArrayList;

public class SiteListingClientDetails extends AppCompatActivity {

	/** The client list lv. */
	private ListView siteListLv;

	/** The sites. */
	private ArrayList<Site> sites;

	/** The data accessobject. */
	private Dao dataAccessobject;

	/** The client list adapter. */
	private SiteListAdapter clientListAdapter;

	/** The search view et. */
	private EditText searchViewEt;

	/** The client id. */
	private int clientId;

	/** The cancel tv. */
	private TextView cancelTv;

	/** The site count. */
	private int siteCount;

	/** The footer view. */
	private View footerView;

	/** The index. */
	private int index = 1;

	/** The filter. */
	private Filter filter;

	private ActionBar actionBar;
	private String siteName, clientName;
	private boolean isItemSelectionEnabled;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.layout_newjob_site_dialog);
		siteListLv = (ListView) findViewById(R.id.siteListLv);
		clientId = getIntent().getExtras().getInt(KEYS.NewJob.CLIENT_ID);
		siteName = getIntent().getExtras().getString(KEYS.NewJob.SITE_NAME);
		clientName = getIntent().getExtras().getString(KEYS.NewJob.CLIENT_NAME);
		actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);

		String title = this.getResources().getString(
				R.string.textSiteListingLable);
		SpannableString titleSpannable = new SpannableString(title);
		titleSpannable.setSpan(
				new TypefaceSpan(this.getResources().getString(
						R.string.fontName_hevelicaLight)), 0,
				titleSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

		// actionBar.setTitle(titleSpannable);
		actionBar.setTitle(isLGDevice() ? title : titleSpannable);

		isItemSelectionEnabled = getIntent().getBooleanExtra(
				KEYS.ClientDetial.ITEM_SELECTION_ENABLED, true);
		searchViewEt = (EditText) findViewById(R.id.searchViewEt);
		findViewById(R.id.searchContanerSiteList).setVisibility(View.GONE);
		searchViewEt.addTextChangedListener(textWatcher);
		if (isItemSelectionEnabled) {

			siteListLv.setOnItemClickListener(onItemClickListener);
		}

		dataAccessobject = DaoManager.getInstance();
		cancelTv = (TextView) findViewById(R.id.cancelTv);
		siteCount = dataAccessobject.getSiteCount(clientId);

		if (siteCount > 20) {
			footerView = ((LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
					.inflate(R.layout.layout_footerview_items_list, null, false);
			siteListLv.addFooterView(footerView);
		}
		cancelTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					InputMethodManager inputManager = (InputMethodManager) SiteListingClientDetails.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					if (SiteListingClientDetails.this.getWindow()
							.getCurrentFocus() != null) {
						inputManager.hideSoftInputFromWindow(
								SiteListingClientDetails.this.getWindow()
										.getCurrentFocus().getWindowToken(), 0);
					}

				} catch (Exception e) {
					Logger.printException(e);
				}

				setResult(RESULT_CANCELED);
				finish();

			}
		});

		searchViewEt.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(android.widget.TextView v,
					int actionId, KeyEvent event) {
				// TODO Auto-generated method stub
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					// bundle.putSerializable(KEYS.NewJob.CLIENT_DETAILS, clt);
					bundle.putInt(KEYS.NewJob.SITE_ID, -1);
					bundle.putString(KEYS.NewJob.ADDRESS, "");
					bundle.putString(KEYS.NewJob.COMPLY_ADDRESS, "");
					bundle.putString(KEYS.NewJob.RUE, "");
					bundle.putString(KEYS.NewJob.SITE_NAME, v.getText()
							.toString());
					bundle.putString(KEYS.NewJob.VILLE, "");
					bundle.putString(KEYS.NewJob.GPSX, "");
					bundle.putString(KEYS.NewJob.GPSY, "");
					intent.putExtras(bundle);
					// intent.putExtra(KEYS.NewJob.CLIENT_INFORMATION, bundle);
					SiteListingClientDetails.this.setResult(RESULT_OK, intent);
					finish();

				}

				return false;
			}
		});

		new FetchSiteAsyncTask().execute();

		// set OnItemClickListener of siteList
		siteListLv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Site site = sites.get(position);

				Intent intent = new Intent(SiteListingClientDetails.this,
						SiteDetail.class);
				intent.putExtra(KEYS.SiteDetails.ID_SITE, site.getIdSite());
				intent.putExtra(KEYS.SiteDetails.ID_CLIENT, clientId);
				intent.putExtra(KEYS.SiteDetails.NAME_SITE, site.getNmSite());
				intent.putExtra(KEYS.SiteDetails.CLIENT_NAME, clientName);
				startActivity(intent);

			}
		});

	}

	@Override
	protected void onDestroy() {
		DialogUtils.dismissProgressDialog();
		super.onDestroy();
	}

	public boolean isLGDevice() {
		return (Build.MANUFACTURER.contains("LG") || Build.MODEL.contains("LG"));
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();

			return true;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Creates the and fill data to list view.
	 */
	private void createAndFillDataToListView() {
		// TODO Auto-generated method stub

		if (clientListAdapter == null) {
			clientListAdapter = new SiteListAdapter(this, sites,
					dataAccessobject);
			clientListAdapter.setIndexPosition(index);

		} else {
			clientListAdapter.setIndexPosition(index);
		}

		siteListLv.setAdapter(clientListAdapter);
		filter = clientListAdapter.getFilter();

		siteListLv.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				int ctadapter = clientListAdapter.getCount();

				if (footerView != null && footerView.isShown()) {

					index++;
					clientListAdapter.setIndexPosition(index);

					try {
						Thread.sleep(900);
					} catch (InterruptedException e) {
						Logger.printException(e);
					}
					clientListAdapter.notifyDataSetChanged();
				}

				if (ctadapter >= siteCount)
					if (footerView != null) {
						siteListLv.removeFooterView(footerView);
						clientListAdapter.notifyDataSetChanged();
					}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});

	}

	/** The on item click listener. */
	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Site clt = (Site) clientListAdapter.getItem(arg2);

			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			// bundle.putSerializable(KEYS.NewJob.CLIENT_DETAILS, clt);
			bundle.putInt(KEYS.NewJob.SITE_ID, clt.getIdSite());
			bundle.putString(KEYS.NewJob.ADDRESS, clt.getAdresse());
			bundle.putString(KEYS.NewJob.COMPLY_ADDRESS, clt.getAdresseCompl());
			bundle.putString(KEYS.NewJob.RUE, clt.getRueSite());
			bundle.putString(KEYS.NewJob.SITE_NAME, clt.getNmSite());
			bundle.putString(KEYS.NewJob.VILLE, clt.getVilleSite());
			bundle.putString(KEYS.NewJob.GPSX, clt.getGpsX());
			bundle.putString(KEYS.NewJob.GPSY, clt.getGpsY());
			bundle.putString(KEYS.NewJob.CP, clt.getCPSite());
			bundle.putString(KEYS.NewJob.PAYS, clt.getPaysSite());
			intent.putExtras(bundle);
			// intent.putExtra(KEYS.NewJob.CLIENT_INFORMATION, bundle);
			SiteListingClientDetails.this.setResult(RESULT_OK, intent);
			finish();

			// if (gt.getFlPageSites() == 1)
			// initAutoCompleteSites();
			// initAutoCompleteEquipements();
		}

	};
	/** The text watcher. */
	TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			if (!TextUtils.isEmpty(s.toString())) {
				if (footerView != null
						&& (footerView.getVisibility() == View.VISIBLE))

					footerView.setVisibility(View.GONE);

			} else {
				if (footerView != null
						&& (footerView.getVisibility() == View.GONE))
					footerView.setVisibility(View.VISIBLE);
			}

			if (filter != null)
				filter.filter(s.toString());

		}
	};

	/**
	 * The Class FetchSiteAsyncTask.
	 */
	private class FetchSiteAsyncTask extends AsyncTaskCoroutine<Void, Boolean> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		public void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			DialogUtils.showProgressDialog(SiteListingClientDetails.this,
					SiteListingClientDetails.this
							.getString(R.string.textWaitLable),
					SiteListingClientDetails.this
							.getString(R.string.textClientFetchDialog), false);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		public Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			if (sites != null) {

				sites.clear();

			} else {

				sites = new ArrayList<Site>();

			}
			try {
				/*
				 * sites.addAll(dataAccessobject.getSitesForClient(clientId,
				 * index, siteCount));
				 */
				sites.addAll(dataAccessobject.getSitesForClient(clientId));
				return true;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Logger.printException(e);
				return false;
			}

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		public void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				if (result.booleanValue()) {

					createAndFillDataToListView();

				} else {
					DialogUtils
							.showInfoDialog(
									SiteListingClientDetails.this,
									SiteListingClientDetails.this
											.getString(R.string.textAlertLable)+"!",
									SiteListingClientDetails.this
											.getString(R.string.textSiteNotFetchedDialog));
				}
			} catch (Exception e) {

				DialogUtils.showInfoDialog(SiteListingClientDetails.this,
						SiteListingClientDetails.this
								.getString(R.string.textAlertLable)+"!",
						SiteListingClientDetails.this
								.getString(R.string.textSiteNotFetchedDialog));
			} finally {

				DialogUtils.dismissProgressDialog();
			}

		}

	}

}
