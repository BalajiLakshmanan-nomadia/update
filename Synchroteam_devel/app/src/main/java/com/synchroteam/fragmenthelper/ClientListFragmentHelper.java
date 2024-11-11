package com.synchroteam.fragmenthelper;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.sap.ultralitejni17.ULjException;
import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.beans.Client;
import com.synchroteam.beans.EnableSynchronizationAddJobEvent;
import com.synchroteam.dao.Dao;
import com.synchroteam.listadapters.ClientSectionListAdapter;
import com.synchroteam.synchroteam.ClientDetail;
import com.synchroteam.synchroteam.SyncoteamNavigationActivity;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.SharedPref;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc
/**
 * The Class ClientListFragmentHelper is to inflate and control all the actions
 * performed in Client listing screen
 * 
 * @author $Ideavate Solution
 */
public class ClientListFragmentHelper implements HelperInterface {

	private Dao dataAccessObject;

	private ListView cientListView;

	private SyncoteamNavigationActivity syncoteamNavigationActivity;

	private ProgressBar progressBar;

	private ClientSectionListAdapter clientSectionListAdapter;

	private View footerView;

	private ArrayList<Client> arrayList;

	private int index;
	private int clientCount;
private 	String s1;
	private EditText searchViewEt;
	private Filter filter;
	private Timer timerSearch;
	private LinearLayout searchContanerClientList;

	private boolean isUserSearching = false;

	private static final String TAG = ClientListFragmentHelper.class
			.getSimpleName();

	/**
	 * Instantiates a new ClientListFragmentHelper.
	 */
	public ClientListFragmentHelper(
			SyncoteamNavigationActivity syncoteamNavigationActivity) {
		// TODO Auto-generated constructor stub

		dataAccessObject = DaoManager.getInstance();
		this.syncoteamNavigationActivity = syncoteamNavigationActivity;
		index = 1;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.synchroteam.fragmenthelper.HelperInterface#inflateLayout(android.
	 * view.LayoutInflater, android.view.ViewGroup)
	 */

	@Override
	public View inflateLayout(final LayoutInflater inflater, ViewGroup container) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.layout_section_client, null);

		cientListView = (ListView) view.findViewById(R.id.cientListView);
		searchViewEt = (EditText) view.findViewById(R.id.searchViewEt);
		cientListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub

				Intent intent = new Intent(syncoteamNavigationActivity,
						ClientDetail.class);

				Client client = arrayList.get(position);

				intent.putExtra(KEYS.ClientDetial.ID_CLIENT,
						client.getIdClient());
				intent.putExtra(KEYS.ClientDetial.CLIENT_NAME,
						client.getNmClient());

				searchViewEt.setText("");
				arrayList.clear();
				clientSectionListAdapter.notifyDataSetChanged();
				syncoteamNavigationActivity.startActivity(intent);


			}

		});
		progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
		searchContanerClientList = (LinearLayout) view
				.findViewById(R.id.searchContanerClientList);
		footerView = ((LayoutInflater) syncoteamNavigationActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.layout_footerview_items_list, null, false);



		index = 1;

		fetchDataFromDataBase();

//

		SharedPref.setIsClientFetched(syncoteamNavigationActivity, false);

		return view;
	}

	private void fetchDataFromDataBase() {
		// TODO Auto-generated method stub
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			new FetchClientsAsyncTask().execute();
		else
		new FetchClientsAsyncTask().execute();

		//
		//

	}

	private class FetchClientsAsyncTask extends AsyncTaskCoroutine<Void, Void> {

		public FetchClientsAsyncTask() {

			// TODO Auto-generated constructor stub
			if (arrayList != null) {
				arrayList.clear();
			} else {
				arrayList = new ArrayList<Client>();
			}
		}

		@Override
		public void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();

			searchViewEt.setText("");
			progressBar.setVisibility(View.VISIBLE);
			cientListView.setVisibility(View.GONE);
			searchContanerClientList.setVisibility(View.GONE);

		}

		@Override
		public Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				if (isUserSearching)
				{if (!TextUtils.isEmpty(s1)) {
						isUserSearching = true;
					} else {
						isUserSearching = false;
					}
					filter.filter(s1);
				}else
				{
					arrayList.clear();
					arrayList.addAll(dataAccessObject.getClients());
					Logger.output(TAG,"client : " + arrayList.size());
				}

			} catch (Exception e) {
								e.printStackTrace();
			}
			return null;
		}
		@Override
		public void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);

			clientCount = arrayList.size();

			Logger.log("oNpostEceuteClientListing", clientCount + "");
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					progressBar.setVisibility(View.GONE);
					searchContanerClientList.setVisibility(View.VISIBLE);
				}
			},500);

			cientListView.setVisibility(View.VISIBLE);
			//searchViewEt.addTextChangedListener(textWatcher);
			if (clientCount > 20) {

				cientListView.addFooterView(footerView);
			}


			if (clientSectionListAdapter == null) {
				clientSectionListAdapter = new ClientSectionListAdapter(
						syncoteamNavigationActivity, arrayList,
						dataAccessObject, ClientListFragmentHelper.this);
				clientSectionListAdapter.setIndexPosition(1);
				cientListView.setAdapter(clientSectionListAdapter);

			} else {

				clientSectionListAdapter.notifyDataSetChanged();

			}

			if (filter == null) {

				searchViewEt.addTextChangedListener(textWatcher);
				filter = clientSectionListAdapter.getFilter();

			}

			cientListView.setOnScrollListener(new OnScrollListener() {

				@Override
				public void onScrollStateChanged(AbsListView view,
						int scrollState) {
					// TODO Auto-generated method stub
//					int ctadapter = clientSectionListAdapter.getCount();

					Logger.log(TAG, "footer view : " + footerView.isShown());

					if (footerView != null && footerView.isShown()) {

						index++;
						clientSectionListAdapter.setIndexPosition(index);

						try {
							Thread.sleep(900);
						} catch (InterruptedException e) {
							Logger.printException(e);
						}
						clientSectionListAdapter.notifyDataSetChanged();
					}
//					if (!isUserSearching) {
//						if (ctadapter >= clientCount) {
//							cientListView.removeFooterView(footerView);
//						}
//					} else {
//						if (ctadapter >= clientSectionListAdapter
//								.getArrayCount()) {
//							hideFooterView();
//						} else {
//							showFooterView();
//						}
//					}

				}

				@Override
				public void onScroll(AbsListView arg0, int arg1, int arg2,
						int arg3) {
					int ctadapter = clientSectionListAdapter.getCount();

					if (!isUserSearching) {
						if (ctadapter >= clientCount) {
							cientListView.removeFooterView(footerView);
							clientSectionListAdapter.notifyDataSetChanged();
						}
					} else {
						if (ctadapter >= clientSectionListAdapter
								.getArrayCount()) {
							hideFooterView();
						} else {
							showFooterView();
						}
					}
				}
			});

			SharedPref.setIsClientFetched(syncoteamNavigationActivity, true);

			EventBus.getDefault().post(new EnableSynchronizationAddJobEvent());
		}

	}

	/** The text watcher. to watch test edit in edit text and filter client list */
	TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {


		}

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			// if (!TextUtils.isEmpty(s.toString())) {
			// if (footerView != null
			// && (footerView.getVisibility() == View.VISIBLE))
			//
			// footerView.setVisibility(View.GONE);
			//
			// } else {
			// if (footerView != null
			// && (footerView.getVisibility() == View.GONE))
			// footerView.setVisibility(View.VISIBLE);
			// }
			 s1=s.toString().trim();

			if (!TextUtils.isEmpty(s1)) {
				isUserSearching = true;
			} else {
				isUserSearching = false;
			}
			timerSearch = new Timer();
			timerSearch.schedule(
					new TimerTask() {
						@Override
						public void run() {
							filter.filter(s1);
						}
	}, 500);
//
//		//	searchViewEt.addTextChangedListener(this);

		}
	};

	@Override
	public void initiateView(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doOnSyncronize() {
		// TODO Auto-generated method stub
		fetchDataFromDataBase();
	}

	@Override
	public void onReturnToActivity(int requestCode) {
		// TODO Auto-generated method stub

	}

	public void hideFooterView() {
		if (footerView != null) {
			if (footerView.getVisibility() == View.VISIBLE) {
				footerView.setVisibility(View.INVISIBLE);
			}
		}

	}

	public void showFooterView() {
		if (footerView != null) {
			if (footerView.getVisibility() == View.INVISIBLE) {
				footerView.setVisibility(View.VISIBLE);
			}
		}
	}

}
