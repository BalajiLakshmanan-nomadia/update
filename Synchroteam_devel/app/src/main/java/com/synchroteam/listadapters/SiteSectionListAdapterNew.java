package com.synchroteam.listadapters;

import android.app.Activity;
import android.app.Service;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.synchroteam.beans.Client_Site_Bean;
import com.synchroteam.synchroteam3.R;

import java.util.ArrayList;

/**
 * The Class ClientListAdapter.
 */
public class SiteSectionListAdapterNew extends BaseAdapter {

	/** The activity. */

	/** The clients. */
	private ArrayList<Client_Site_Bean> clients;

	/** The layout inflater. */
	private LayoutInflater layoutInflater;

	/**
	 * Instantiates a new client list adapter.
	 *
	 * @param activity
	 *            the activity
	 * @param clients
	 *            the clients
	 */
	public SiteSectionListAdapterNew(Activity activity,
									 ArrayList<Client_Site_Bean> clients) {
		this.clients = clients;
		layoutInflater = (LayoutInflater) activity
				.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * The Class ViewHolder.
	 */
	private class ViewHolder {

		/** The client name tv. */
		TextView clientNameTv;

		TextView clientPlaceTv;

		TextView siteNameTv;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
			return clients.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		return clients.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		Client_Site_Bean client_Site_Bean = (Client_Site_Bean) getItem(position);

		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = layoutInflater.inflate(
					R.layout.layout_section_site_list, parent,false);

			viewHolder.clientNameTv = (TextView) convertView
					.findViewById(R.id.clientNameTv);
			viewHolder.clientPlaceTv = (TextView) convertView
					.findViewById(R.id.clientPlaceTv);
			viewHolder.siteNameTv = (TextView) convertView
					.findViewById(R.id.siteNameTv);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.clientNameTv.setText(client_Site_Bean.getClientName());
		viewHolder.clientPlaceTv.setVisibility(View.VISIBLE);
		if (!TextUtils.isEmpty(client_Site_Bean.getVilleSite())) {
			viewHolder.clientPlaceTv.setText(client_Site_Bean.getVilleSite());
		} else {
			viewHolder.clientPlaceTv.setVisibility(View.GONE);

		}

		if (client_Site_Bean.getRefCustomer().length()>0){
			viewHolder.siteNameTv.setText(client_Site_Bean.getNmSite()+" ("+client_Site_Bean.getRefCustomer()+")");
		}else {
			viewHolder.siteNameTv.setText(client_Site_Bean.getNmSite());
		}
		return convertView;
	}

}
