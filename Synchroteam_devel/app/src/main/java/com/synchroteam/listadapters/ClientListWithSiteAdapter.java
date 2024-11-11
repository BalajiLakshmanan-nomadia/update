package com.synchroteam.listadapters;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.synchroteam.beans.Client;
import com.synchroteam.dao.Dao;
import com.synchroteam.fragmenthelper.ClientListFH;
import com.synchroteam.synchroteam.ClientDetail;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AccentInsensitive;
import com.synchroteam.utils.KEYS;

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc

/**
 * The Class ClientListAdapter.
 */
public class ClientListWithSiteAdapter extends BaseAdapter implements Filterable {
    private FilterDate filterDate;
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

    private ClientListFH fragment;
    private Activity activity;

    /**
     * Instantiates a new client list adapter.
     *
     * @param activity         the activity
     * @param clients          the clients
     * @param dataAccessObject the data access object
     */
    public ClientListWithSiteAdapter(Activity activity,
                                     ArrayList<Client> clients, Dao dataAccessObject,
                                     ClientListFH fragment) {
        // TODO Auto-generated constructor stub

        this.clients = clients;
        this.fragment = fragment;
        orignalList = new ArrayList<Client>();
        this.activity = activity;
        orignalList.addAll(clients);
//		notAvaliable = activity.getString(R.string.textNotAvalableLable);
        layoutInflater = (LayoutInflater) activity
                .getSystemService(Service.LAYOUT_INFLATER_SERVICE);
        filterDate = new FilterDate(ClientListWithSiteAdapter.this, this.clients);
    }

    @Override
    public Filter getFilter() {
        return filterDate;
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

        if (count < clients.size()) {

            return count;
        } else {
            return clients.size();
        }
        // } else {
        // return clients.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder viewHolder;
        Client client_Site_Bean = (Client) getItem(position);

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

        viewHolder.clientNameTv.setText(client_Site_Bean.getNmClient());
        viewHolder.clientPlaceTv.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(client_Site_Bean.getVilleClient())) {
            viewHolder.clientPlaceTv.setText(client_Site_Bean.getVilleClient());
        } else {
//			viewHolder.clientPlaceTv.setText(notAvaliable);
            viewHolder.clientPlaceTv.setVisibility(View.GONE);

        }
        viewHolder.siteNameTv.setVisibility(View.GONE);

        // viewHolder.siteNameTv.setText(client_Site_Bean.getNmSite());
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Client client_Site_Bean = clients.get(position);

                Intent intent = new Intent(activity,
                        ClientDetail.class);
                intent.putExtra(KEYS.ClientDetial.ID_CLIENT,
                        client_Site_Bean.getIdClient());
                intent.putExtra(KEYS.ClientDetial.CLIENT_NAME,
                        client_Site_Bean.getNmClient());

                activity.startActivity(intent);
            }
        });

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
//    public Filter getFilter() {
//
//        Filter filter = new Filter() {
//
//            @SuppressWarnings("unchecked")
//            @Override
//            protected void publishResults(CharSequence constraint,
//                                          FilterResults results) {
//                // TODO Auto-generated method stub
//                clients.clear();
//                clients.addAll((ArrayList<Client>) results.values);
//
//                if (clients.size() > 0) {
//                    if (fragment != null) {
//                        fragment.showFooterView();
//                    }
//                } else {
//                    if (fragment != null) {
//                        fragment.hideFooterView();
//                    }
//
//                }
//
//                notifyDataSetChanged();
//
//            }
//
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                // TODO Auto-generated method stub
//                FilterResults results = new FilterResults();
//                ArrayList<Client> aux = new ArrayList<Client>();
//
//                // El prefijo tiene que ser mayor que 0 y existir
//                if (constraint != null && constraint.toString().length() > 0) {
//                    isUserSearching = true;
//                    for (Client client : orignalList) {
//
//                        String searchString = client.getNmClient()
//                                 + client.getVilleClient();
//
//                        // remove all the accented characters before search.
//                        String asciiSearchString = AccentInsensitive
//                                .removeAccentCase(searchString);
//                        String asciiConstraint = AccentInsensitive
//                                .removeAccentCase(constraint.toString());
//                        if (asciiConstraint != null && asciiConstraint.length() > 0)
//                            if (asciiSearchString.toLowerCase().contains(
//                                    asciiConstraint.toLowerCase())) {
//
//                                aux.add(client);
//
//                            }
//
//                    }
//                    results.values = aux;
//                    results.count = aux.size();
//
//                } else {
//                    synchronized (clients) {
//                        isUserSearching = false;
//                        results.values = orignalList;
//                        results.count = orignalList.size();
//                    }
//                }
//                return results;
//
//            }
//        };
//
//        return filter;
//
//    }
    private class FilterDate extends Filter {
        List<Client> arrayListOld = new ArrayList<>();
        List<Client> arrayListNew;
        ClientListWithSiteAdapter localRecyclerHis;

        public FilterDate(ClientListWithSiteAdapter recyclerShowHisCategoryList, List<Client> list) {
            localRecyclerHis = recyclerShowHisCategoryList;
            arrayListOld = list;
        }

        @Override
        protected FilterResults performFiltering(CharSequence str) {
            FilterResults results = new FilterResults();


            if (str != null && str.length() > 0) {
                isUserSearching = true;
                arrayListNew = new ArrayList<>();
                for (Client client : arrayListOld) {

                    String searchString = client.getNmClient()
                            + client.getVilleClient();

                    // remove all the accented characters before search.
                    String asciiSearchString = AccentInsensitive
                            .removeAccentCase(searchString);
                    String asciiConstraint = AccentInsensitive
                            .removeAccentCase(str.toString());
                    if (asciiConstraint != null && asciiConstraint.length() > 0)
                        if (asciiSearchString.toLowerCase().contains(
                                asciiConstraint.toLowerCase())) {

                            arrayListNew.add(client);

                        }

                }
                results.count = arrayListNew.size();
                results.values = arrayListNew;
            } else {
                arrayListNew.clear();
                arrayListNew.addAll(arrayListOld);
                results.count = arrayListNew.size();
                results.values = arrayListNew;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            localRecyclerHis.clients = (ArrayList<Client>) filterResults.values;
            try {
                if (fragment != null) {
                    fragment.showFooterView();
                } else {
                    if (fragment != null) {
                        fragment.hideFooterView();
                    }

                }
                notifyDataSetChanged();

            } catch (Exception r) {
                localRecyclerHis.clients = (ArrayList<Client>) arrayListOld;
                if (localRecyclerHis.clients.size() > 0) {
                    if (fragment != null) {
                        fragment.showFooterView();
                    }
                } else {
                    if (fragment != null) {
                        fragment.hideFooterView();
                    }

                }
//
                notifyDataSetChanged();
            }
        }
    }
}
