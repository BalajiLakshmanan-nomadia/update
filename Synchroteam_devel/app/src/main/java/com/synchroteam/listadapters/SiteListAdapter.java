package com.synchroteam.listadapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.synchroteam.beans.Site;
import com.synchroteam.dao.Dao;
import com.synchroteam.synchroteam.SiteList;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AccentInsensitive;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class SiteListAdapter. author Ideavate.solution
 */
@SuppressLint("DefaultLocale")
public class SiteListAdapter extends BaseAdapter {

	/** The sites. */
	private ArrayList<Site> sites;

	/** The orignal list. */
	private ArrayList<Site> orignalList;

	/** The layout inflater. */
	private LayoutInflater layoutInflater;

	/** The index. */
	private int index;

	/** The base count. */
	private int baseCount = 20;

	/** The is user searching. */
	private boolean isUserSearching = false;

	Activity activity;

	/**
	 * Instantiates a new site list adapter.
	 *
	 * @param activity
	 *            the activity
	 * @param sites
	 *            the sites
	 * @param dataAccessObject
	 *            the data access object
	 */
	public SiteListAdapter(Activity activity, ArrayList<Site> sites,
			Dao dataAccessObject) {
		// TODO Auto-generated constructor stub

		this.sites = sites;
		orignalList = new ArrayList<Site>();
		orignalList.addAll(sites);

		layoutInflater = (LayoutInflater) activity
				.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
	}

	/**
	 * The Class ViewHolder.
	 */
	private class ViewHolder {

		/** The client name tv. */
		TextView siteNameTv;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		// if (!isUserSearching) {
		int count = index * baseCount;

		if (count < sites.size()) {

			return count;
		} else {
			return sites.size();
		}
		// } else {
		// return sites.size();
		// }
	}

	public int getArrayCount() {
		return sites.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return sites.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		Site client = (Site) getItem(position);

		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = layoutInflater.inflate(
					R.layout.list_item_autocomplete, parent, false);

			viewHolder.siteNameTv = (TextView) convertView
					.findViewById(R.id.dataNameTv);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (client.getRefCustomer().length()>0){
			viewHolder.siteNameTv.setText(client.getNmSite()+" ("+client.getRefCustomer()+")");
		}else {
			viewHolder.siteNameTv.setText(client.getNmSite());
		}
		return convertView;
	}

	/**
	 * Adds the site to orignal list.
	 *
	 * @param sites
	 *            the sites
	 */
	public void addSiteToOrignalList(ArrayList<Site> sites) {

		orignalList.clear();
		orignalList.addAll(sites);

	}

	/**
	 * Sets the index position.
	 *
	 * @param index
	 *            the new index position
	 */
	public void setIndexPosition(int index) {
		// TODO Auto-generated method stub
		this.index = index;
	}

	/**
	 * Gets the filter.
	 *
	 * @return the filter
	 */
	public Filter getFilter() {

		Filter filter = new Filter() {

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				// TODO Auto-generated method stub
				sites.clear();
				sites.addAll((ArrayList<Site>) results.values);
				notifyDataSetChanged();
			}

			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				// TODO Auto-generated method stub
				FilterResults results = new FilterResults();
				ArrayList<Site> aux = new ArrayList<Site>();

				// El prefijo tiene que ser mayor que 0 y existir
				if (constraint != null && constraint.toString().length() > 0) {
					isUserSearching = true;
					for (Site site : orignalList) {

						String nom = site.getNmSite();

						// remove all the accented characters before search.
						String asciiSearchString = AccentInsensitive
								.removeAccentCase(nom);
						String asciiConstraint = AccentInsensitive
								.removeAccentCase(constraint.toString());

						if ((asciiSearchString.toLowerCase()
								.contains(asciiConstraint.toString()
										.toLowerCase()))) {

							aux.add(site);

						}

					}
					results.values = aux;
					results.count = aux.size();

				} else {
					synchronized (sites) {
						isUserSearching = false;
						results.values = orignalList;
						results.count = orignalList.size();
					}
				}
				return results;

			}
		};

		return filter;

	}
}
