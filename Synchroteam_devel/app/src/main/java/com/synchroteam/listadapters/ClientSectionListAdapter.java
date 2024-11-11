package com.synchroteam.listadapters;

import android.app.Activity;
import android.app.Service;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.synchroteam.beans.Client;
import com.synchroteam.dao.Dao;
import com.synchroteam.fragmenthelper.ClientListFragmentHelper;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AccentInsensitive;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc

/**
 * The Class ClientListAdapter.
 */
public class ClientSectionListAdapter extends BaseAdapter {

    /** The activity. */

    /**
     * The clients.
     */
    private ArrayList<Client> clients;

    /**
     * The orignal list.
     */
    private ArrayList<Client> orignalList;

    /**
     * The layout inflater.
     */
    private LayoutInflater layoutInflater;

    /**
     * The index.
     */
    private int index;

    /**
     * The base count.
     */
    private int baseCount = 20;

//	private String notAvaliable;

    /**
     * The is user searching.
     */
    private boolean isUserSearching = false;

    private ClientListFragmentHelper fragment;

    /**
     * Instantiates a new client list adapter.
     *
     * @param activity         the activity
     * @param clients          the clients
     * @param dataAccessObject the data access object
     */
    public ClientSectionListAdapter(Activity activity,
                                    ArrayList<Client> clients, Dao dataAccessObject,
                                    ClientListFragmentHelper fragment) {
        // TODO Auto-generated constructor stub

        this.clients = clients;
        this.fragment = fragment;
        orignalList = new ArrayList<Client>();
//		notAvaliable = activity.getString(R.string.textNotAvalableLable);
        orignalList.addAll(clients);
        layoutInflater = (LayoutInflater) activity
                .getSystemService(Service.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * The Class ViewHolder.
     */
    private class ViewHolder {

        /**
         * The client name tv.
         */
        TextView clientNameTv;

        TextView clientPlaceTv;

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

//        if (count < clients.size()) {
//
//            return count;
//        } else {
//            return clients.size();
//        }
        // } else {
         return clients.size();
        // }

    }

    public int getArrayCount() {
        return clients.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return clients.get(position);
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
        Client client = (Client) getItem(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(
                    R.layout.layout_section_client_list, parent,false);

            viewHolder.clientNameTv = (TextView) convertView
                    .findViewById(R.id.clientNameTv);
            viewHolder.clientPlaceTv = (TextView) convertView
                    .findViewById(R.id.clientPlaceTv);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.clientNameTv.setText(client.getNmClient());
        viewHolder.clientPlaceTv.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(client.getVilleClient())) {

            viewHolder.clientPlaceTv.setText(client.getVilleClient());
        } else {
//			viewHolder.clientPlaceTv.setText(notAvaliable);
            viewHolder.clientPlaceTv.setVisibility(View.GONE);
        }

        return convertView;
    }

    /**
     * Sets the index position.
     *
     * @param index the new index position
     */
    public void setIndexPosition(int index) {

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
                clients.clear();
                clients.addAll((ArrayList<Client>) results.values);

                if (clients.size() > 0) {
                    if (fragment != null) {
                        fragment.showFooterView();
                    }
                } else {
                    if (fragment != null) {
                        fragment.hideFooterView();
                    }

                }
                notifyDataSetChanged();

            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                // TODO Auto-generated method stub
                FilterResults results = new FilterResults();
                ArrayList<Client> aux = new ArrayList<Client>();

                // El prefijo tiene que ser mayor que 0 y existir
                if (constraint != null && constraint.toString().length() > 0) {
                    isUserSearching = true;
                    for (Client client : orignalList) {

                        String searchString = client.getNmClient()
                                + client.getVilleClient();

                        // remove all the accented characters before search.
                        String asciiSearchString = AccentInsensitive
                                .removeAccentCase(searchString);
                        String asciiConstraint = AccentInsensitive
                                .removeAccentCase(constraint.toString());

                        if (asciiConstraint != null && asciiConstraint.length() > 0)
                            if (asciiSearchString.toLowerCase().contains(
                                    asciiConstraint.toLowerCase())) {

                                aux.add(client);

                            }

                    }
                    results.values = aux;
                    results.count = aux.size();

                } else {
                    synchronized (clients) {
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
