package com.synchroteam.synchroteam;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.TextView.OnEditorActionListener;

import com.sap.ultralitejni17.ULjException;
import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.Equipement;
import com.synchroteam.dao.Dao;
import com.synchroteam.listadapters.EquipmentsListAdapter;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DateChecker;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class EquipmentList to show equipments list on new job screen.
 *
 * @author $Ideavate Solution
 */
public class EquipmentList extends Activity {

	/** The client list lv. */
	private ListView equipmentListLv;

	/** The equipements. */
	private ArrayList<Equipement> equipements;

	/** The data accessobject. */
	private Dao dataAccessobject;

	/** The equipments list adapter. */
	private EquipmentsListAdapter equipmentsListAdapter;

	/** The search view et. */
	private EditText searchViewEt;

	/** The site id. */
	private int clientId, siteId;

	/** The cancel tv. */
	private TextView cancelTv;

	/** The equipment count. */
	private int equipmentCount;

	/** The footer view. */
	private View footerView;

	/** The index. */
	private int index = 1;

	/** The filter. */
	private Filter filter;

	private boolean isSelectionEnabled;

	private boolean isUserSearching = false;

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

		setContentView(R.layout.layout_newjob_equipment_dialog);
		equipmentListLv = (ListView) findViewById(R.id.equipmentListLv);
		searchViewEt = (EditText) findViewById(R.id.searchViewEt);
		searchViewEt.addTextChangedListener(textWatcher);
		dataAccessobject = DaoManager.getInstance();
		clientId = getIntent().getExtras().getInt(KEYS.NewJob.CLIENT_ID);
		siteId = getIntent().getExtras().getInt(KEYS.NewJob.SITE_ID);
		isSelectionEnabled = getIntent().getBooleanExtra(
				KEYS.ClientDetial.ITEM_SELECTION_ENABLED, true);
		if (isSelectionEnabled) {
			equipmentListLv.setOnItemClickListener(onItemClickListener);
		}
		cancelTv = (TextView) findViewById(R.id.cancelTv);
		equipmentCount = dataAccessobject.getEquipementsForSiteCount(clientId,
				siteId);
		if (equipmentCount > 20) {
			footerView = ((LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
					.inflate(R.layout.layout_footerview_items_list, null, false);
			equipmentListLv.addFooterView(footerView);
		}

		cancelTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					InputMethodManager inputManager = (InputMethodManager) EquipmentList.this
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					if (EquipmentList.this.getWindow().getCurrentFocus() != null) {
						inputManager.hideSoftInputFromWindow(
								EquipmentList.this.getWindow()
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
					bundle.putInt(KEYS.NewJob.EQUIPMENTS_ID, -1);
					bundle.putString(KEYS.NewJob.EQUIPMENTS_NAME, v.getText()
							.toString());

					intent.putExtras(bundle);
					// intent.putExtra(KEYS.NewJob.CLIENT_INFORMATION, bundle);
					EquipmentList.this.setResult(RESULT_OK, intent);
					finish();

				}

				return false;
			}
		});

		new FetchEquipmentAsyncTask().execute();
	}

	@Override
	protected void onResume() {
		super.onResume();

		//New changes
		DateChecker.checkDateAndNavigate(this, dataAccessobject);

	}

	@Override
	protected void onDestroy() {
		DialogUtils.dismissProgressDialog();
		super.onDestroy();
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

	/**
	 * Creates the and fill data to list view.
	 */
	private void createAndFillDataToListView() {
		// TODO Auto-generated method stub

		if (equipmentsListAdapter == null) {
			equipmentsListAdapter = new EquipmentsListAdapter(this,
					equipements, dataAccessobject);
			equipmentsListAdapter.setIndexPosition(index);
			equipmentListLv.setAdapter(equipmentsListAdapter);
		} else {
			equipmentsListAdapter.notifyDataSetChanged();
		}

		filter = equipmentsListAdapter.getFilter();

		equipmentListLv.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				// int ctadapter = equipmentsListAdapter.getCount();

				if (footerView != null && footerView.isShown()) {

					index++;
					equipmentsListAdapter.setIndexPosition(index);

					try {
						Thread.sleep(900);
					} catch (InterruptedException e) {
						Logger.printException(e);
					}
					equipmentsListAdapter.notifyDataSetChanged();
				}
				// if (!isUserSearching) {
				// if (ctadapter >= equipmentCount)
				// equipmentListLv.removeFooterView(footerView);
				// } else {
				// if (ctadapter >= equipmentsListAdapter.getArrayCount()) {
				// hideFooterView();
				// } else {
				// showFooterView();
				// }
				// }

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
								 int visibleItemCount, int totalItemCount) {
				int ctadapter = equipmentsListAdapter.getCount();

				if (!isUserSearching) {
					if (ctadapter >= equipmentCount)
						equipmentListLv.removeFooterView(footerView);
				} else {
					if (ctadapter >= equipmentsListAdapter.getArrayCount()) {
						hideFooterView();
					} else {
						showFooterView();
					}
				}

			}
		});

	}

	/** The on item click listener. */
	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
								long arg3) {
			Equipement clt = (Equipement) equipmentsListAdapter.getItem(arg2);
			// int idClient = clt.getIdClient();

			// // EditText et = (EditText) findViewById(R.id.addressEt);
			// if (clt.getAdresseGlobalClient().equals(""))
			// et.setText(" ");
			// else
			// et.setText(clt.getAdresseGlobalClient());
			//
			// // et = (EditText) findViewById(R.id.additionalAddressEt);
			//
			// if (.equals(""))
			// et.setText(" ");
			// else
			// et.setText(clt.getAdresseComplClient());
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			// bundle.putSerializable(KEYS.NewJob.CLIENT_DETAILS, clt);
			bundle.putInt(KEYS.NewJob.EQUIPMENTS_ID, clt.getIdEquipement());
			bundle.putString(KEYS.NewJob.EQUIPMENTS_NAME, clt.getNmEquipement());
			bundle.putInt(KEYS.NewJob.SITE_ID, clt.getmEquipementSiteId());
			intent.putExtras(bundle);
			// intent.putExtra(KEYS.NewJob.CLIENT_INFORMATION, bundle);
			EquipmentList.this.setResult(RESULT_OK, intent);
			finish();

			// if (gt.getFlPageSites() == 1)
			// initAutoCompleteSites();
			// initAutoCompleteEquipements();
		}

	};
	/**
	 * The text watcher.which watches text changes on edit text and perform
	 * equipment list filtering
	 */
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

			if (!TextUtils.isEmpty(s.toString())) {
				isUserSearching = true;
			} else {
				isUserSearching = false;
			}

			if (filter != null)
				filter.filter(s.toString());
		}
	};

	/**
	 * The Class FetchEquipmentAsyncTask.
	 */
	private class FetchEquipmentAsyncTask extends
			AsyncTaskCoroutine<Void, Boolean> {

		/*
		 * (non-Javadoc)
		 *
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		public void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			DialogUtils.showProgressDialog(EquipmentList.this,
					EquipmentList.this.getString(R.string.textWaitLable),
					EquipmentList.this
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
			if (equipements != null) {

				equipements.clear();

			} else {

				equipements = new ArrayList<Equipement>();

			}
			try {
				equipements.addAll(dataAccessobject.getEquipementsForSite(
						clientId, index, siteId, equipmentCount));
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
									EquipmentList.this,
									EquipmentList.this
											.getString(R.string.textAlertLable)+"!",
									EquipmentList.this
											.getString(R.string.textEquipmentNotFetchedDialog));
				}
			} catch (Exception e) {

				DialogUtils
						.showInfoDialog(
								EquipmentList.this,
								EquipmentList.this
										.getString(R.string.textAlertLable)+"!",
								EquipmentList.this
										.getString(R.string.textEquipmentNotFetchedDialog));
			} finally {

				DialogUtils.dismissProgressDialog();
			}

		}

	}

}
