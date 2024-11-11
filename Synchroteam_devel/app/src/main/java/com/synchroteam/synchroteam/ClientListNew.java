package com.synchroteam.synchroteam;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.Client;
import com.synchroteam.dao.Dao;
import com.synchroteam.listadapters.ClientListAdapterNew;
import com.synchroteam.listadapters.ClientListAdapterNewUpdated;
import com.synchroteam.listeners.RvEmptyListener;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DateChecker;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.SynchroteamUitls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// TODO: Auto-generated Javadoc

/**
 * The Class ClientList used to show client list on add Job.
 * 
 * @author $Ideavate Solution
 */
public class ClientListNew extends Activity implements ConnectionCallbacks,
		OnConnectionFailedListener , RvEmptyListener {

	/** The client list lv. */
	private ListView  nearestClientListLv;
	RecyclerView clientListLv;

	/** The clients. */
	private ArrayList<Client> clients;

	/** The clients. */
	private ArrayList<Client> clientsOriginal;

	/** The data accessobject. */
	private Dao dataAccessobject;

	/** The client list adapter. */
	private ClientListAdapterNewUpdated clientListAdapter;

	/** The search view et. */
	private EditText searchViewEt;

	/** The cancel tv. */
	private TextView cancelTv;

	/** The client count. */
	private int clientCount;

	/** The footer view. */
	private View footerView;

	/** The index. */
	private int index = 1;

	/** The location manager. */
	protected LocationManager locationManager;

	/** The filter. */
	private Filter filter;

	/** The nearest client client btn. */
	private TextView nearestClientClientBtn;

	/** The client text. */
	private String nearestClienttext, clientText;

	/** The my timer. */
	private Timer myTimer;

	/** The ids. */
	private int ids = 0;

	/** The search container. */
	private LinearLayout searchContainer;

	/** The adapter. */
	private CustomBaseAdapter adapter;

	/** The m location request. */
	private LocationRequest mLocationRequest;

	/** The location client. */
	private GoogleApiClient locationClient;

	/** The is location client connected. */
	private boolean isLocationClientConnected = false;

	private String km;

	private boolean isUserSearching = false;
	private boolean isUserScrolled = false;

	/** The search count. */
	private int searchCount;
	private int listOffset = 1;
	private int searchOffset = 1;
	private String searchText;
    private ProgressBar progressBar;


	public static final int MY_PERMISSIONS_REQUEST_LOCATION = 124;
	private int lastVisibleItem, totalItemCount;
	LinearLayoutManager linearLayoutManager;
	private boolean isLoading;
	private int visibleThreshold = 2;
	private String searchTextSize;



	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.layout_newjob_client_dialog);
		clientListLv = (RecyclerView) findViewById(R.id.clientListLv);
		linearLayoutManager = new LinearLayoutManager(this);
		DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(clientListLv.getContext(),
				linearLayoutManager.getOrientation());
		clientListLv.addItemDecoration(dividerItemDecoration);
		clientListLv.setLayoutManager(linearLayoutManager);
		nearestClientListLv = (ListView) findViewById(R.id.nearestClientListLv);
		nearestClientListLv.setOnItemClickListener(nearsetItemClickListner);
		searchViewEt = (EditText) findViewById(R.id.searchViewEt);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
//		searchViewEt.addTextChangedListener(textWatcher);
//		clientListLv.setOnClickListener(onItemClickListener);

		dataAccessobject = DaoManager.getInstance();
		cancelTv = (TextView) findViewById(R.id.cancelTv);
		km = ClientListNew.this.getString(R.string.km);
		nearestClienttext = this.getString(R.string.textNearestClientNewJob).toUpperCase();
		clientText = this.getString(R.string.textJobCustomerLableTv);
		findViewById(R.id.clearData).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				searchViewEt.setText("");

				try {
					InputMethodManager inputManager = (InputMethodManager) ClientListNew.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					if (ClientListNew.this.getWindow().getCurrentFocus() != null) {
						inputManager.hideSoftInputFromWindow(
								ClientListNew.this.getWindow().getCurrentFocus()
										.getWindowToken(), 0);
					}

				} catch (Exception e) {
					Logger.printException(e);
				}

			}
		});
		searchContainer = (LinearLayout) findViewById(R.id.searchContanerClientList);
		nearestClientClientBtn = (TextView) findViewById(R.id.nearestClientClientBtn);
		clientCount = dataAccessobject.getClientsCount();
		if (clientCount > 20) {
			footerView = ((LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
					.inflate(R.layout.layout_footerview_items_list, null, false);
//			clientListLv.addFooterView(footerView);
		}
		cancelTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setResult(RESULT_CANCELED);
				finish();

			}
		});

		nearestClientClientBtn.setTag(Boolean.valueOf(false));

		nearestClientClientBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				boolean isNearestClient = ((Boolean) arg0.getTag())
						.booleanValue();

				if (isNearestClient) {
					nearestClientClientBtn.setText(nearestClienttext);
					arg0.setTag(Boolean.valueOf(false));
					searchContainer.setVisibility(View.VISIBLE);

//					clientListLv.addFooterView(footerView);

					if (clients.isEmpty()) {
						new FetchClientsAsyncTaskUpdated().execute();
					} else {
						createAndFillDataToListView();
					}
				} else {

					geocoder(arg0);

				}

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
					bundle.putInt(KEYS.NewJob.CLIENT_ID, -1);
					bundle.putString(KEYS.NewJob.GLOBAL_ADDRESS, "");
					bundle.putString(KEYS.NewJob.COMPLY_ADDRESS, "");
					bundle.putString(KEYS.NewJob.RUE, "");
					bundle.putString(KEYS.NewJob.CLIENT_NAME, v.getText()
							.toString());
					bundle.putString(KEYS.NewJob.VILLE, "");
					bundle.putString(KEYS.NewJob.GPSX, "");
					bundle.putString(KEYS.NewJob.GPSY, "");
					bundle.putBoolean("isNewClient",true);
					intent.putExtras(bundle);
					// intent.putExtra(KEYS.NewJob.CLIENT_INFORMATION, bundle);
					ClientListNew.this.setResult(RESULT_OK, intent);
					finish();

				}

				return false;
			}
		});

		// locationClient = new LocationClient(this, this, this);
		locationClient = new GoogleApiClient.Builder(this)
				.addApi(LocationServices.API).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).build();

		mLocationRequest = LocationRequest.create();

		/*
		 * Set the update interval
		 */
		mLocationRequest
				.setInterval(SynchroteamUitls.UPDATE_INTERVAL_IN_MILLISECONDS);

		// Use high accuracy
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

		// Set the interval ceiling to one minute
		mLocationRequest
				.setFastestInterval(SynchroteamUitls.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

//		new FetchClientsAsyncTask().execute();

		fetchDataFromDataBase();

	}

	/**
	 * Fetches value from db
	 */
	private void fetchDataFromDataBase() {
		new FetchClientsAsyncTaskUpdated().execute();
	}

	@Override
	protected void onResume() {
		super.onResume();

		//New changes
		DateChecker.checkDateAndNavigate(this, dataAccessobject);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	public void onStop() {

		// After disconnect() is called, the client is considered "dead".
		locationClient.disconnect();

		super.onStop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStart()
	 */
	@Override
	public void onStart() {

		super.onStart();

		/*
		 * Connect the client. Don't re-start any requests here; instead, wait
		 * for onResume()
		 */
		locationClient.connect();

	}

	/**
	 * Creates the and fill data to list view.
	 */
	private void createAndFillDataToListView() {
		// TODO Auto-generated method stub

		if (clientListAdapter == null) {
			clientListAdapter = new ClientListAdapterNewUpdated(this, clients,
					dataAccessobject);
            clientListLv.setAdapter(clientListAdapter);

		} else {
            clientListAdapter.notifyDataSetChanged();
		}


		if (clientListLv.getVisibility() == View.GONE) {
			clientListLv.setVisibility(View.VISIBLE);
			nearestClientListLv.setVisibility(View.GONE);
		}

//        nearestClientListLv.setOnScrollListener(new OnScrollListener() {
//
//			@Override
//			public void onScrollStateChanged(AbsListView view, int scrollState) {
//				// TODO Auto-generated method stub
//				// int ctadapter = clientListAdapter.getCount();
//
//				if (footerView != null && footerView.isShown()) {
//
//					index++;
//					clientListAdapter.setIndexPosition(index);
//
//					try {
//						Thread.sleep(900);
//					} catch (InterruptedException e) {
//						Logger.printException(e);
//					}
//					clientListAdapter.notifyDataSetChanged();
//				}
//
//			}
//
//			@Override
//			public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
//				int ctadapter = clientListAdapter.getCount();
//
//				if (!isUserSearching) {
//					if (ctadapter >= clientCount) {
//						if (footerView != null)
//							clientListLv.removeFooterView(footerView);
//					}
//				} else {
//					if (ctadapter >= clientListAdapter.getArrayCount()) {
//						hideFooterView();
//					} else {
//						showFooterView();
//					}
//				}
//			}
//		});

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

	/** The on item click listener. */
//	OnItemClickListener onItemClickListener = new OnItemClickListener() {
//
//		@Override
//		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
//				long arg3) {
//
//			Client clt = (Client) clientListAdapter.getItem(arg2);
//			Intent intent = new Intent();
//			Bundle bundle = new Bundle();
//			bundle.putBoolean(KEYS.NewJob.ISNEARESTCLIENTSELECTED, false);
//			bundle.putInt(KEYS.NewJob.CLIENT_ID, clt.getIdClient());
//			bundle.putString(KEYS.NewJob.GLOBAL_ADDRESS,
//					clt.getAdresseGlobalClient());
//			bundle.putString(KEYS.NewJob.COMPLY_ADDRESS,
//					clt.getAdresseComplClient());
//			bundle.putString(KEYS.NewJob.RUE, clt.getRueClient());
//			if(clt.getRef_customer().length()>0){
//				bundle.putString(KEYS.NewJob.CLIENT_NAME, clt.getNmClient()+" ("+clt.getRef_customer()+")");
//			}else {
//				bundle.putString(KEYS.NewJob.CLIENT_NAME, clt.getNmClient());
//			}
//			bundle.putString(KEYS.NewJob.VILLE, clt.getVilleClient());
//			bundle.putString(KEYS.NewJob.GPSX, clt.getGpsX());
//			bundle.putString(KEYS.NewJob.GPSY, clt.getGpsY());
//			intent.putExtras(bundle);
//
//			ClientListNew.this.setResult(RESULT_OK, intent);
//			finish();
//
//		}
//
//	};

	/** The nearset item click listner. */
	OnItemClickListener nearsetItemClickListner = new OnItemClickListener() {

		@SuppressWarnings("unchecked")
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub

			HashMap<String, String> map = (HashMap<String, String>) adapter
					.getItem(arg2);
			Intent intent = new Intent();
			intent = new Intent();
			intent.putExtra(KEYS.NewJob.ISNEARESTCLIENTSELECTED, true);
			intent.putExtra("ObjectType", map.get("ObjectType"));
			intent.putExtra("id", map.get("id"));
			intent.putExtra("nom", map.get("nom"));
			intent.putExtra("nmClient", map.get("nmClient"));
			intent.putExtra("IdClient", map.get("IdClient"));
			intent.putExtra("AdrGlobale", map.get("AdrGlobale"));
			intent.putExtra("AdrComplement", map.get("AdrComplement"));
			intent.putExtra("Latitude", map.get("Latitude"));
			intent.putExtra("Longitude", map.get("Longitude"));
			intent.putExtra("RefCustomer",map.get("RefCustomer"));

			setResult(RESULT_OK, intent);
			finish();

		}
	};

	@Override
	public void onEmptyList(ArrayList<HashMap<String, String>> list) {

	}

	/** The text watcher. to watch test edit in edit text and filter client list */
//	TextWatcher textWatcher = new TextWatcher() {
//
//		@Override
//		public void onTextChanged(CharSequence s, int start, int before,
//				int count) {
//			// TODO Auto-generated method stub
//
//		}
//
//		@Override
//		public void beforeTextChanged(CharSequence s, int start, int count,
//				int after) {
//			// TODO Auto-generated method stub
//
//		}
//
//		@Override
//		public void afterTextChanged(Editable s) {
//
//
//			if (!TextUtils.isEmpty(s.toString())) {
//				isUserSearching = true;
//			} else {
//				isUserSearching = false;
//			}
//
//			if (filter != null)
//				filter.filter(s.toString());
//
//		}
//	};

	/**
	 * The Class FetchClientsAsyncTask.
	 */
	private class FetchClientsAsyncTask extends AsyncTaskCoroutine<Void, Boolean> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		public void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			DialogUtils.showProgressDialog(ClientListNew.this,
					ClientListNew.this.getString(R.string.textWaitLable),
					ClientListNew.this.getString(R.string.textClientFetchDialog),
					false);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		public Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			if (clients != null) {

				clients.clear();

			} else {

				clients = new ArrayList<Client>();

			}
			try {
				clients.addAll(dataAccessobject.getClients());
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
									ClientListNew.this,
									ClientListNew.this
											.getString(R.string.textAlertLable)+"!",
									ClientListNew.this
											.getString(R.string.textClientNotFetchedDialog)+"!");
				}
			} catch (Exception e) {

				DialogUtils.showInfoDialog(ClientListNew.this, ClientListNew.this
						.getString(R.string.textAlertLable)+"!", ClientListNew.this
						.getString(R.string.textClientNotFetchedDialog)+"!");
			} finally {

				DialogUtils.dismissProgressDialog();
			}

		}

	}

	/**
	 * code of previous developer.
	 * 
	 * @param v
	 *            the v
	 */
	public void geocoder(View v) {

		if (servicesConnected() && isLocationClientConnected) {

			boolean isGPSEnabled = false;
			boolean isNetworkEnabled = false;

			try {
				locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

				isGPSEnabled = locationManager
						.isProviderEnabled(LocationManager.GPS_PROVIDER);

				isNetworkEnabled = Build.FINGERPRINT.contains("generic") ? true : locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


				if (!isGPSEnabled && !isNetworkEnabled) {
					showSettingsAlert();
				}

				else {

					if (ActivityCompat.checkSelfPermission(ClientListNew.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
							&& ActivityCompat.checkSelfPermission(ClientListNew.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
						callingPermissionLocation();
					} else {
						callingLocationFunctionalities();
					}



				}

			} catch (Exception e) {
				Logger.printException(e);
			}

		}

		ids = v.getId();

	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_LOCATION: {
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					callingLocationFunctionalities();
				} else {
					// permission denied, boo! Disable the
					// functionality that depends on this permission.
				}
				return;
			}
			// other 'case' lines to check for other
			// permissions this app might request.
		}
	}

	public void callingPermissionLocation() {

		if (ActivityCompat.shouldShowRequestPermissionRationale(ClientListNew.this,
				Manifest.permission.ACCESS_COARSE_LOCATION)) {
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ClientListNew.this);
			alertBuilder.setCancelable(true);
			alertBuilder.setTitle(getString(R.string.app_name));
			alertBuilder.setMessage(getString(R.string.str_location_permission));
			alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
				public void onClick(DialogInterface dialog, int which) {
					ActivityCompat.requestPermissions(ClientListNew.this,
							new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
									Manifest.permission.ACCESS_COARSE_LOCATION},
							MY_PERMISSIONS_REQUEST_LOCATION);
				}
			});
			AlertDialog alert = alertBuilder.create();
			alert.show();
		} else {
			// No explanation needed; request the permission
			ActivityCompat.requestPermissions(ClientListNew.this,
					new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
							Manifest.permission.ACCESS_COARSE_LOCATION},
					MY_PERMISSIONS_REQUEST_LOCATION);
		}
	}

	@SuppressLint("MissingPermission")
	private void callingLocationFunctionalities() {
		Toast.makeText(ClientListNew.this,
				getString(R.string.gps_lancer), Toast.LENGTH_SHORT)
				.show();

		// locationClient.requestLocationUpdates(mLocationRequest,
		// locationListener);
		LocationServices.FusedLocationApi.requestLocationUpdates(
				locationClient, mLocationRequest, locationListener);

		final Toast tag = Toast.makeText(ClientListNew.this,
				getString(R.string.gps_delai), Toast.LENGTH_SHORT);
		if (myTimer != null) {
			myTimer.cancel();
			myTimer = new Timer();
		} else
			myTimer = new Timer();

		myTimer.schedule(new java.util.TimerTask() {
			@Override
			public void run() {
				DialogUtils.dismissProgressDialog();
				stopUsingGPS();
				tag.show();
			}
		}, 50000);

		DialogUtils.showProgressDialog(this, ClientListNew.this
						.getString(R.string.textPleaseWaitLable),
				ClientListNew.this
						.getString(R.string.textFetchingLocation),
				false);
	}

	/**
	 * Stop using gps.
	 */
	public void stopUsingGPS() {

		if ((locationClient != null) && (locationClient.isConnected())) {
			// locationClient.removeLocationUpdates(locationListener);
			LocationServices.FusedLocationApi.removeLocationUpdates(
					locationClient, locationListener);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.location.LocationListener#onLocationChanged(android.location.
	 * Location)
	 */

	/** The location listener. */
	LocationListener locationListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub

			myTimer.cancel();

			stopUsingGPS();
			switch (ids) {

			case R.id.nearestClientClientBtn:
				DialogUtils.dismissProgressDialog();

				new FetchNearestClientAsyncTask().execute(
						location.getLatitude() + "", location.getLongitude()
								+ "");

				break;
			}
		}
	};

	/**
	 * Show settings alert.
	 */
	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setTitle(getString(R.string.textAlertLable)+"!");
		alertDialog.setMessage(getString(R.string.textEnableLocationService));
		alertDialog.setPositiveButton(R.string.textYesLable,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(intent);
					}
				});

		alertDialog.setNegativeButton(R.string.textNoLable,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		alertDialog.show();
	}

	/**
	 * The Class CustomBaseAdapter to inflate list of nearest client.
	 */
	public class CustomBaseAdapter extends BaseAdapter {

		/** The context. */
		Context context;
		// List<RowItem> rowItems;
		/** The list item. */
		ArrayList<HashMap<String, String>> listItem;// = new
													// ArrayList<HashMap<String,
													// String>>();
		/** The orignal list. */
		ArrayList<HashMap<String, String>> orignalList;

		/**
		 * Instantiates a new custom base adapter.
		 * 
		 * @param context
		 *            the context
		 * @param listItem
		 *            the list item
		 */
		public CustomBaseAdapter(Context context,
				ArrayList<HashMap<String, String>> listItem) {
			this.context = context;
			this.listItem = listItem;
			orignalList = new ArrayList<HashMap<String, String>>();
			this.orignalList.addAll(listItem);
		}

		/* private view holder class */
		/**
		 * The Class ViewHolder.
		 */
		private class ViewHolder {

			/** The txt title. */
			TextView txtTitle;

			/** The txt desc. */
			TextView txtDesc;

			/** Distance lable */
			TextView txtDistanceLable;

			/** The txt distance. */
			TextView txtDistance;

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;

			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = mInflater.inflate(
						R.layout.layout_clientlist_listitem, null);
				holder = new ViewHolder();
				holder.txtTitle = (TextView) convertView
						.findViewById(R.id.clientNameTv);
				holder.txtDesc = (TextView) convertView
						.findViewById(R.id.clientAddressTv);
				holder.txtDistance = (TextView) convertView
						.findViewById(R.id.clientDistanceTv);
				holder.txtDistanceLable = (TextView) convertView.findViewById(R.id.distanceTv);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			HashMap<String, String> map = extracted(position);

			// holder.txtTitle.setText(map.get("nom")+"--"+map.get("nomClient"));

			String type = map.get("ObjectType");
			String nom = map.get("nom");
			String refCostomer = map.get("RefCustomer");
			if (type.equals("Site")) {
				nom = map.get("nmClient") + " : (" + map.get("nom") + ")";
			}
			if(refCostomer.length()>0){
				holder.txtTitle.setText(nom+" ("+refCostomer+")");
			}else {
				holder.txtTitle.setText(nom);
			}

			holder.txtDesc.setText(map.get("AdrGlobale"));

			double newKB = Math
					.round(Double.parseDouble(map.get("distanceKm")) * 100.0) / 100.0;

			holder.txtDistanceLable.setText(getString(R.string.textDistanceNearestClient)+":");
			holder.txtDistance.setText(newKB + " " + km);

			return convertView;
		}

		/**
		 * Extracted.
		 * 
		 * @param position
		 *            the position
		 * @return the hash map
		 */
		@SuppressWarnings("unchecked")
		private HashMap<String, String> extracted(int position) {
			return (HashMap<String, String>) getItem(position);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			return listItem.size();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int position) {
			return listItem.get(position);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			return listItem.indexOf(getItem(position));
		}

	}

	/**
	 * The Class FetchNearestClientAsyncTask.
	 */
	private class FetchNearestClientAsyncTask extends
			AsyncTaskCoroutine<String, Boolean> {

		/** The nearest clint list. */
		private ArrayList<HashMap<String, String>> nearestClintList;

		/**
		 * Instantiates a new fetch nearest client async task.
		 */
		public FetchNearestClientAsyncTask() {
			// TODO Auto-generated constructor stub

			nearestClintList = new ArrayList<HashMap<String, String>>();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		public void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			DialogUtils.showProgressDialog(ClientListNew.this, ClientListNew.this
					.getString(R.string.textWaitLable), ClientListNew.this
					.getString(R.string.textProgressDialogFetchNearestClient),
					false);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		public Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub

			try {
				nearestClintList.clear();
				nearestClintList.addAll(dataAccessobject.getAllSt(params[0],
						params[1]));
				if (nearestClintList.size() == 0) {

					return false;
				} else {
					return true;
				}
			} catch (Exception e) {
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
//					clientListLv.removeFooterView(footerView);
					if (adapter == null) {
						adapter = new CustomBaseAdapter(ClientListNew.this,
								nearestClintList);
						nearestClientListLv.setAdapter(adapter);

					}

					else {
						adapter.notifyDataSetChanged();

					}

					nearestClientListLv.setVisibility(View.VISIBLE);
					clientListLv.setVisibility(View.GONE);
//					clientListLv.removeFooterView(footerView);

					nearestClientClientBtn.setText(clientText);
					nearestClientClientBtn.setTag(Boolean.valueOf(true));
					searchContainer.setVisibility(View.GONE);

				} else {
					DialogUtils
							.showInfoDialog(
									ClientListNew.this,
									ClientListNew.this
											.getString(R.string.textAlertLable)+"!",
									ClientListNew.this
											.getString(R.string.textNearestClientNotFetchedDialog));
				}
			} finally {
				DialogUtils.dismissProgressDialog();

			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		DialogUtils.dismissProgressDialog();
		try {
			InputMethodManager inputManager = (InputMethodManager) ClientListNew.this
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (ClientListNew.this.getWindow().getCurrentFocus() != null) {
				inputManager.hideSoftInputFromWindow(ClientListNew.this
						.getWindow().getCurrentFocus().getWindowToken(), 0);
			}

		} catch (Exception e) {
			Logger.printException(e);
		}

		super.onDestroy();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.android.gms.common.GooglePlayServicesClient.
	 * OnConnectionFailedListener
	 * #onConnectionFailed(com.google.android.gms.common.ConnectionResult)
	 */
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks
	 * #onConnected(android.os.Bundle)
	 */
	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		isLocationClientConnected = true;
	}

	/**
	 * Verify that Google Play services is available before making a request.
	 * 
	 * @return true if Google Play services is available, otherwise false
	 */
	private boolean servicesConnected() {

		// Check that Google Play services is available
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);

		// If Google Play services is available
		if (ConnectionResult.SUCCESS == resultCode) {
			// In debug mode, log the status

			// Continue
			return true;
			// Google Play services was not available for some reason
		} else {
			// Display an error dialog
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode,
					this, 0);
			dialog.show();

			return false;
		}
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		// TODO Auto-generated method stub

	}



	private RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
		@Override
		public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
			super.onScrollStateChanged(recyclerView, newState);
			View currentFocus = ClientListNew.this.getCurrentFocus();
			if (currentFocus != null){
				InputMethodManager imm = (InputMethodManager) ClientListNew.this.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(recyclerView.getWindowToken(),0);
			}

		}

		@Override
		public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
			super.onScrolled(recyclerView, dx, dy);

			totalItemCount = linearLayoutManager.getItemCount();
			lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

			if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)){
				isLoading = true;
			}
			 searchTextSize = searchViewEt.getText().toString().trim();
//			if (searchTextSize.length() > 0){
//				updateClientList();
//			}else {
//
//			}
			updateClientList();



		}
	};


	/**
	 * Updates the client list.
	 */
	private void updateClientList() {
//		progressBar.setVisibility(View.VISIBLE);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				try {
					if (!isUserSearching) {
						listOffset = listOffset + 25;
						ArrayList<Client> clientList = dataAccessobject.getClientsByOffset(listOffset);
						clients.addAll(clientList);
						clients.addAll(clientList);
					} else {
						searchOffset = searchOffset + 25;
						clients.addAll(dataAccessobject.getClientsSearch(searchOffset, searchText));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				clientListAdapter.notifyDataSetChanged();
//				progressBar.setVisibility(View.GONE);
			}
		},900);

//        new FetchClients().execute();
	}

	/**
	 * Async task for fetching list.
	 */
	private class FetchClientsAsyncTaskUpdated extends AsyncTaskCoroutine<Void, Void> {

		public FetchClientsAsyncTaskUpdated() {

			if (clients != null) {
				clients.clear();
			} else {
				clients = new ArrayList<>();
			}

			if (clientsOriginal != null) {
				clientsOriginal.clear();
			} else {
				clientsOriginal = new ArrayList<>();
			}
		}

		@Override
		public void onPreExecute() {
			super.onPreExecute();

			searchViewEt.setText("");
//			progressBar.setVisibility(View.VISIBLE);

			clientListLv.setVisibility(View.GONE);


		}

		@Override
		public Void doInBackground(Void... params) {
			try {
				ArrayList<Client> listClients = dataAccessobject.getClientsByOffset(listOffset);
				clients.addAll(listClients);
				clientsOriginal.addAll(listClients);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		@Override
		public void onPostExecute(Void result) {
			super.onPostExecute(result);

			clientCount = dataAccessobject.getCustomerCount();

			Logger.log("oNpostEceuteClientListing", clientCount + "");
//			progressBar.setVisibility(View.GONE);

			clientListLv.setVisibility(View.VISIBLE);



			if (clientListAdapter == null) {
				clientListAdapter = new ClientListAdapterNewUpdated(ClientListNew.this, clients,
						dataAccessobject);
				clientListLv.setAdapter(clientListAdapter);

			} else {
				clientListAdapter.notifyDataSetChanged();
			}
//			clientListLv.setAdapter(clientListAdapter);
//			clientListAdapter.notifyDataSetChanged();



			searchViewEt.addTextChangedListener(textWatcher);
			clientListLv.addOnScrollListener(mOnScrollListener);

		}

	}

	/**
	 * The text watcher. to watch test edit in edit text and filter client list
	 */
	TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
								  int count) {

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
									  int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {
			if (!TextUtils.isEmpty(s.toString())) {

				isUserSearching = true;
				searchOffset = 1;
				clients.clear();
				searchText = s.toString();

				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						try {
							clients.clear();
							searchCount = dataAccessobject.getCustomerSearchCount(searchText);
							clients.addAll(dataAccessobject.getClientsSearch(searchOffset, searchText));
							if(clients.size()>20){
								showFooterView();
							}else{
								hideFooterView();
							}
							clientListAdapter.notifyDataSetChanged();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, 100);

			} else {
				isUserSearching = false;
				searchText = "";
				clients.clear();
				clients.addAll(clientsOriginal);
				clientListAdapter.notifyDataSetChanged();
			}
		}
	};
}


