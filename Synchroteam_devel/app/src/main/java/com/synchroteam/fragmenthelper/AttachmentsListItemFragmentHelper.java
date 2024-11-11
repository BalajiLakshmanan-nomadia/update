package com.synchroteam.fragmenthelper;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.synchroteam.beans.AttachmentsBeans;
import com.synchroteam.beans.EnableSynchronizationAddJobEvent;
import com.synchroteam.dao.Dao;
import com.synchroteam.listadapters.AttachmentListAdapter;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.Logger;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc
/**
 * The Class DiscriptionJobDetailFragmentHelper is to inflate and control all
 * the actions performed in job description screen. Created For Future Purpose
 * 
 * @author $Ideavate Solution
 */
public class AttachmentsListItemFragmentHelper implements HelperInterface {

	// private int clientId;

	/** The data access object. */
	private Dao dataAccessObject;

	/** The view. */
	private View view;

	private ListView attachmentListView;
	private TextView txtEmpty;
	private ProgressBar progressBar;
	private Activity syncoteamNavigationActivity;

	private ArrayList<AttachmentsBeans> attachmentsBeans;

	private AttachmentListAdapter attachmentListAdapter;

	private View footerView;
	private int index, attachmentCount;

	/**
	 * Instantiates a new client detial fragment helper
	 * 
	 * @param clientId
	 * 
	 */
	/*
	 * public AttachmentsListFragmentHelper(ClientDetail
	 * syncoteamNavigationActivity,int clientId) { // TODO Auto-generated
	 * constructor stub
	 * 
	 * 
	 * 
	 * this.dataAccessObject = DaoManager.getInstance();
	 * 
	 * this.clientId=clientId;
	 * this.syncoteamNavigationActivity=syncoteamNavigationActivity;
	 * 
	 * 
	 * 
	 * 
	 * }
	 */

	public AttachmentsListItemFragmentHelper(
			Activity syncoteamNavigationActivity) {
		// TODO Auto-generated constructor stub

		this.dataAccessObject = DaoManager.getInstance();

		// this.clientId = clientId;
		index = 1;
		this.syncoteamNavigationActivity = syncoteamNavigationActivity;

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
		View view = inflater.inflate(
				R.layout.layout_attachment_list_client_detail, null);
		footerView = inflater.inflate(R.layout.layout_footerview_items_list,
				null, false);
		initiateView(view);

		this.view = view;
		return view;
	}

	/**
	 * Sets the data to view.
	 * 
	 * @param view
	 *            the new data to view
	 */

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

		attachmentListView = (ListView) v.findViewById(R.id.attachmentListView);
		txtEmpty = (TextView) v.findViewById(R.id.empty_text);
		progressBar = (ProgressBar) v.findViewById(R.id.progressBar);

		new FetchAttachmentDataFromDb().execute();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.synchroteam.fragmenthelper.HelperInterface#doOnSyncronize()
	 */
	@Override
	public void doOnSyncronize() {
		// TODO Auto-generated method stub
		new FetchAttachmentDataFromDb().execute();
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

	/** The on click listener. */
	OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

		}
	};

	private class FetchAttachmentDataFromDb extends AsyncTaskCoroutine<Void, Void> {

		public FetchAttachmentDataFromDb() {
			// TODO Auto-generated constructor stub
			if (attachmentsBeans == null) {

				attachmentsBeans = new ArrayList<AttachmentsBeans>();
			} else {
				attachmentsBeans.clear();
			}

		}

		@Override
		public void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			progressBar.setVisibility(View.VISIBLE);
			attachmentListView.setVisibility(View.GONE);
		}

		@Override
		public Void doInBackground(Void... params) {
			// TODO Auto-generated method stub

			attachmentsBeans.addAll(dataAccessObject.getAttachmentList());

			return null;
		}

		@Override
		public void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			progressBar.setVisibility(View.GONE);

			attachmentListView.setVisibility(View.VISIBLE);
			attachmentCount = attachmentsBeans.size();
			if (attachmentCount > 20) {

				attachmentListView.addFooterView(footerView);
			}

			if (attachmentListAdapter == null) {
				attachmentListAdapter = new AttachmentListAdapter(
						syncoteamNavigationActivity, attachmentsBeans);
				attachmentListAdapter.setIndexPosition(index);
				attachmentListView.setAdapter(attachmentListAdapter);
				attachmentListView.setEmptyView(txtEmpty);

			} else {
				attachmentListAdapter.notifyDataSetChanged();
			}

//			attachmentCount = attachmentsBeans.size();
//			if (attachmentCount > 20) {
//
//				attachmentListView.addFooterView(footerView);
//			}

			attachmentListView.setOnScrollListener(new OnScrollListener() {

				@Override
				public void onScrollStateChanged(AbsListView view,
						int scrollState) {
					// TODO Auto-generated method stub
					int ctadapter = attachmentListAdapter.getCount();

					if (footerView.isShown()) {

						index++;
						attachmentListAdapter.setIndexPosition(index);

						try {
							Thread.sleep(900);
						} catch (InterruptedException e) {
							Logger.printException(e);
						}
						attachmentListAdapter.notifyDataSetChanged();
					}
					if (ctadapter >= attachmentCount) {
						attachmentListView.removeFooterView(footerView);
						attachmentListAdapter.notifyDataSetChanged();
					}
				}

				@Override
				public void onScroll(AbsListView arg0, int arg1, int arg2,
						int arg3) {
					// TODO Auto-generated method stub

				}
			});

			EventBus.getDefault().post(new EnableSynchronizationAddJobEvent());

		}

	}

}
