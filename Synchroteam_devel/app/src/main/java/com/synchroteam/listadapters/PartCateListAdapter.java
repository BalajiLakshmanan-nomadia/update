package com.synchroteam.listadapters;

import android.app.Activity;
import android.app.Service;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.synchroteam.beans.PartCategoryNameBeans;
import com.synchroteam.dao.Dao;
import com.synchroteam.synchroteam.CategoryPartList;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AccentInsensitive;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc

/**
 * The Class ClientListAdapter.
 */
public class PartCateListAdapter extends BaseAdapter {

    /** The activity. */

    /**
     * The clients.
     */
    private ArrayList<PartCategoryNameBeans> clients;

    /**
     * The orignal list.
     */
    private ArrayList<PartCategoryNameBeans> orignalList;

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

    /**
     * The is user searching.
     */
    private boolean isUserSearching = false;

    private CategoryPartList clientList;

    boolean isPart;
    int idCategoryPiece;

    /**
     * Instantiates a new client list adapter.
     *
     * @param activity         the activity
     * @param clients          the clients
     * @param dataAccessObject the data access object
     * @param idCategoryPiece
     */
    public PartCateListAdapter(Activity activity, ArrayList<PartCategoryNameBeans> clients,
                               Dao dataAccessObject, boolean isPart, int idCategoryPiece) {
        // TODO Auto-generated constructor stub

        this.clients = clients;
        this.isPart = isPart;
        this.idCategoryPiece = idCategoryPiece;
        clientList = (CategoryPartList) activity;
        orignalList = new ArrayList<PartCategoryNameBeans>();

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
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder;
        PartCategoryNameBeans nameBeans = (PartCategoryNameBeans) getItem(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(
                    R.layout.list_item_autocomplete, parent,false);

            viewHolder.clientNameTv = (TextView) convertView
                    .findViewById(R.id.dataNameTv);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (isPart) {

            if (idCategoryPiece <= 0) {
                viewHolder.clientNameTv.setText(nameBeans.getNameCategory() + " > " + nameBeans.getPartName()
                        + " (" + nameBeans.getCdProduit() + ")");
            } else {
                viewHolder.clientNameTv.setText(nameBeans.getPartName() + " (" + nameBeans.getCdProduit() + ")");
            }
        } else {
            viewHolder.clientNameTv.setText(nameBeans.getNameCategory());
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
                clients.addAll((ArrayList<PartCategoryNameBeans>) results.values);

                if (clients.size() > 0) {
                    if (clientList != null) {
                        clientList.showFooterView();
                    }
                } else {
                    if (clientList != null) {
                        clientList.hideFooterView();
                    }

                }
                notifyDataSetChanged();

            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                // TODO Auto-generated method stub
                FilterResults results = new FilterResults();
                ArrayList<PartCategoryNameBeans> aux = new ArrayList<PartCategoryNameBeans>();

                // El prefijo tiene que ser mayor que 0 y existir
                if (constraint != null && constraint.toString().length() > 0) {
                    isUserSearching = true;
                    for (PartCategoryNameBeans client : orignalList) {
                        String nom;
                        if (isPart)
                            nom = client.getPartName();
                        else
                            nom = client.getNameCategory();

                        // remove all the accented characters before search.
                        String asciiSearchString = AccentInsensitive
                                .removeAccentCase(nom);
                        String asciiConstraint = AccentInsensitive
                                .removeAccentCase(constraint.toString());

                        if ((asciiSearchString.toLowerCase()
                                .contains(asciiConstraint.toString()
                                        .toLowerCase()))) {

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
