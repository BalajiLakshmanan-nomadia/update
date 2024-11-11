package com.synchroteam.synchroteam;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings.System;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TypefaceSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.synchroteam.tracking.TrackingParameterService;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.beans.ReportsStartAndEndTime;
import com.synchroteam.beans.UpdateUiOnSync;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.SynchronizationErrorDialog;
import com.synchroteam.listadapters.ReportsDetailListAdapter;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.ReportsItems;
import com.synchroteam.utils.SharedPref;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc
/**
 * This Activity is Responsible for Inflating And performing actions on this
 * Reports Detail Screen. created for future purpose
 * 
 * @author Ideavate.solution
 */
public class ReportsDetails extends AppCompatActivity implements
		CommonInterface {

	/** The reports items. */
	private ArrayList<ReportsItems> reportsItems;

	/** The finish date tv. */
	private TextView startTimeTv, finishedTimeTv, spentTimeTv, startDateTv,
			finishDateTv;

	/** The id intervention. */
	private String idIntervention;

	/** The id model. */
	private int idModel;

	/** The data access object. */
	private Dao dataAccessObject;

	/** The finish date. */
	private String startTime, finishedTime, spentTime, startDate, finishDate;

	/** The reports detail list adapter. */
	private ReportsDetailListAdapter reportsDetailListAdapter;

	/** The reports detail lv. */
	private ListView reportsDetailLv;

	/** The action bar. */
	private ActionBar actionBar;

	/** The pi. */
	private PendingIntent pi, pi1,pITrackParams;

	/** The progress d synch. */
	private ProgressDialog progressDSynch;

	/** The user. */
	private User user;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_reports_details);

		actionBar = getSupportActionBar();

		Bundle bundle = getIntent().getExtras();
		this.idModel = Integer.parseInt(bundle
				.getString(KEYS.ReportsDetails.ID_MODEL));
		this.idIntervention = bundle.getString(KEYS.ReportsDetails.ID);
		dataAccessObject = DaoManager.getInstance();
		user = dataAccessObject.getUser();

		String title = bundle.getString(KEYS.ReportsDetails.TYPE);
		SpannableString titleSpannable = new SpannableString(title);
		titleSpannable.setSpan(
				new TypefaceSpan(this.getResources().getString(
						R.string.fontName_hevelicaLight)), 0,
				titleSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		// actionBar.setTitle(titleSpannable);

		actionBar.setTitle(isLGDevice() ? title : titleSpannable);

        pITrackParams=initializePendingIntent();

		initialiseView();

		createAndFillDatainUi();

	}

	/**
	 * Intializin the pending intent for the tracking service
	 *
	 * @return Pending intent
	 */
	private PendingIntent initializePendingIntent() {
		Intent trackingParamsIntent = new Intent(ReportsDetails.this,
				TrackingParameterService.class);
		trackingParamsIntent.putExtra("from_pending_intent", true);

		PendingIntent pendingIntent;
		//Behaviour changes supporting android 12
		if (android.os.Build.VERSION.SDK_INT >= 23) {
			// Create a PendingIntent using FLAG_IMMUTABLE
			pendingIntent= PendingIntent.getService(ReportsDetails.this,
					0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT|
							PendingIntent.FLAG_IMMUTABLE);
		} else {

			pendingIntent= PendingIntent.getService(ReportsDetails.this,
					0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		}

		return pendingIntent;

//		return PendingIntent.getService(ReportsDetails.this,
//				0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT);

	}


	/**
	 * For canceling the Tracking service when conditions are met
	 */
	private void cancelTrackingAlarm() {
		stopService(new Intent(ReportsDetails.this, TrackingParameterService.class));
		AlarmManager am2 = (AlarmManager) ReportsDetails.this
				.getSystemService(Context.ALARM_SERVICE);
		am2.cancel(pITrackParams);
	}


	public boolean isLGDevice() {
		return (Build.MANUFACTURER.contains("LG") || Build.MODEL.contains("LG"));
	}

	/**
	 * Fill date and time data.
	 */
	private void fillDateAndTimeData() {
		// TODO Auto-generated method stub
		startTimeTv.setText(startTime);
		finishedTimeTv.setText(finishedTime);
		startDateTv.setText(startDate);
		finishDateTv.setText(finishDate);
		spentTimeTv.setText(spentTime);
	}

	/**
	 * Gets the required data.
	 *
	 * @return the required data
	 */
	private void getRequiredData() {
		// TODO Auto-generated method stub

		ReportsStartAndEndTime reportsStartAndEndTime = dataAccessObject
				.getStartAndEndDateForJob(idIntervention);
		if (reportsStartAndEndTime != null) {
			startDate = reportsStartAndEndTime.getStartDate();
			finishDate = reportsStartAndEndTime.getEndDate();
			startTime = reportsStartAndEndTime.getStartTime();
			finishedTime = reportsStartAndEndTime.getEndTime();

		}

		long timeElapsed = dataAccessObject
				.suspendedTimeDiffrence(idIntervention);
		int seconds = (int) (timeElapsed / 1000);

		// Date date=new Date(timeElapsed);

		int hours = seconds / 3600;
		int minutes = (seconds / 60) - (hours * 60);
		seconds = seconds - (hours * 3600) - (minutes * 60);
		String minutesString = null;
		String hoursString = null;
		if (minutes < 10) {
			minutesString = "0" + minutes;
		} else {
			minutesString = minutes + "";
		}
		if (hours < 10) {
			hoursString = "0" + hours;
		} else {
			hoursString = hours + "";
		}

		spentTime = hoursString + ":" + minutesString + "h";

	}

	/**
	 * Initialise view.
	 */
	private void initialiseView() {
		// TODO Auto-generated method stub

		startTimeTv = (TextView) findViewById(R.id.startTimeTv);
		finishedTimeTv = (TextView) findViewById(R.id.finishedTimeTv);
		spentTimeTv = (TextView) findViewById(R.id.spentTimeTv);
		startDateTv = (TextView) findViewById(R.id.startDateTv);
		finishDateTv = (TextView) findViewById(R.id.finishDateTv);
		reportsDetailLv = (ListView) findViewById(R.id.reportsDetailLv);

	}

	/**
	 * Creates the and fill datain ui.
	 */
	private void createAndFillDatainUi() {
		// TODO Auto-generated method stub

		if (reportsItems != null) {
			reportsItems.clear();
		} else {
			reportsItems = new ArrayList<ReportsItems>();
		}

		reportsItems = dataAccessObject.getAllCategoriesOfJob(idModel,
				idIntervention,0, null);

		// Collections.sort(reportsItems, new Comparator<ReportsItems>() {
		//
		// @Override
		// public int compare(ReportsItems reportIten, ReportsItems arg1) {
		// // TODO Auto-generated method stub
		// return 0;
		// }
		// });

		if (reportsDetailListAdapter != null) {
			reportsDetailListAdapter.notifyDataSetChanged();

		} else {
			reportsDetailListAdapter = new ReportsDetailListAdapter(this,
					reportsItems);
		}

		reportsDetailLv.setAdapter(reportsDetailListAdapter);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synchroteam.synchroteam.CommonInterface#getActivityInstance()
	 */
	@Override
	public Activity getActivityInstance() {
		// TODO Auto-generated method stub
		return this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	protected void onResume() {
		super.onResume();
		((SyncroTeamApplication) getApplicationContext())
				.setSyncroteamAppLive(true);

		((SyncroTeamApplication) getApplicationContext())
				.setSyncroteamActivityInstance(this);

		getRequiredData();
		fillDateAndTimeData();

		String dateFormatString = System.getString(this.getContentResolver(),
				System.DATE_FORMAT);
		String timeFormatString = System.getString(this.getContentResolver(),
				System.TIME_12_24);
		if (!(TextUtils.isEmpty(dateFormatString))
				&& (!TextUtils.isEmpty(timeFormatString))) {
			if (!dateFormatString.equals(SharedPref.getDateFormat(this))) {

				reportsDetailListAdapter.notifyDataSetChanged();

				SharedPref.setDateFormat(this);
				SharedPref.setTimeFormat(this);

			}

			else if (!timeFormatString.equals(SharedPref.getTimeFormat(this))) {

				reportsDetailListAdapter.notifyDataSetChanged();
				SharedPref.setDateFormat(this);
				SharedPref.setTimeFormat(this);

			}

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		((SyncroTeamApplication) getApplicationContext())
				.setSyncroteamAppLive(false);
		((SyncroTeamApplication) getApplicationContext())
				.setSyncroteamActivityInstance(null);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synchroteam.synchroteam.CommonInterface#updateUi()
	 */
	@Override
	public void updateUi() {
		// TODO Auto-generated method stub
		EventBus.getDefault().post(new UpdateUiOnSync());
	}

	@Override
	public void updateUiOnTrakingStatusChange(boolean isRunning) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.reports_detail_menu, menu);

		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_refresh:

			syncronise();

			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * Syncronise with database.
	 */
	protected void syncronise() {
		// TODO Auto-generated method stub

		progressDSynch = ProgressDialog.show(this,
				getString(R.string.textPleaseWaitLable),
				getString(R.string.msg_synch), true, false);

		Thread syncDbWithServer = new Thread((new Runnable() {
			@Override
			public void run() {

				Message myMessage = new Message();
				try {
					dataAccessObject.sync(user.getLogin(), user.getPwd());

					GestionAcces gt = dataAccessObject.getAcces();
					if (gt != null && gt.getOptionTaracking() == 0) {
//						stopService(new Intent(ReportsDetails.this,
//								TrackingService.class));
//						AlarmManager am = (AlarmManager) ReportsDetails.this
//								.getSystemService(Context.ALARM_SERVICE);
//						am.cancel(pi);
//
//						stopService(new Intent(ReportsDetails.this,
//								TrackingServiceFrequency.class));
//						AlarmManager am1 = (AlarmManager) ReportsDetails.this
//								.getSystemService(Context.ALARM_SERVICE);
//						am1.cancel(pi1);

						cancelTrackingAlarm();
					}

					myMessage.obj = "ok";
					handler.sendMessage(myMessage);
				} catch (Exception ex) {
					String exception = ex.getMessage();
					if (exception != null) {
						if (exception.indexOf("4006") != -1)
							myMessage.obj = "4006";
						else if (exception.indexOf("4101") != -1)
							myMessage.obj = "4101";
						else if (exception.indexOf("4005") != -1)
							myMessage.obj = "4005";
						else
							myMessage.obj = "Error";
					} else
						myMessage.obj = "Error";
					handler.sendMessage(myMessage);
				} finally {
					if (progressDSynch != null && progressDSynch.isShowing())
						progressDSynch.dismiss();
				}
			}
		}));
		syncDbWithServer.start();
	}

	/** The handler. */
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			String erreur = (String) msg.obj;
			if (erreur.equals("ok")) {
				Toast.makeText(ReportsDetails.this,
						getString(R.string.msg_synch_ok), Toast.LENGTH_LONG)
						.show();

			} else if (erreur.equals("4006")) {
				Toast.makeText(ReportsDetails.this,
						getString(R.string.msg_synch_error_4),
						Toast.LENGTH_LONG).show();
			} else if (erreur.equals("4101")) {
				Toast.makeText(ReportsDetails.this,
						getString(R.string.msg_synch_error_2),
						Toast.LENGTH_LONG).show();
			} else if (erreur.equals("4005")) {
				Toast.makeText(ReportsDetails.this,
						getString(R.string.msg_synch_error_3),
						Toast.LENGTH_LONG).show();
			} else {
//				Toast.makeText(ReportsDetails.this,
//							   getString(R.string.msg_synch_error_new), Toast.LENGTH_LONG)
//						.show();
                showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));
			}
		}
	};

    /**
     * For showing the synchronization failure messages
     */
    protected void showSyncFailureMsgDialog(String syncFailureMsg) {

        SynchronizationErrorDialog synchronizationErrorDialog = new SynchronizationErrorDialog
                (ReportsDetails.this, syncFailureMsg, new SynchronizationErrorDialog
                        .SynchronizationErrorInterface() {
                    @Override
                    public void doOnOkayClick() {
                        //perform any action
                    }
                });

        synchronizationErrorDialog.show();
    }

}
