package com.synchroteam.listadapters;

import android.app.Activity;
import android.app.Service;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.synchroteam.beans.Equipement;
import com.synchroteam.dao.Dao;
import com.synchroteam.synchroteam.EquipmentList;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AccentInsensitive;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc

/**
 * The Class EquipmentsListAdapter. author Ideavate solution
 */
public class EquipmentsListAdapter extends BaseAdapter {

    /**
     * The equipements.
     */
    private ArrayList<Equipement> equipements;

    /**
     * The orignal list.
     */
    private ArrayList<Equipement> orignalList;

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

    private Activity activity;

    /**
     * Instantiates a new equipments list adapter.
     *
     * @param activity         the activity
     * @param equipements      the equipements
     * @param dataAccessObject the data access object
     */
    public EquipmentsListAdapter(Activity activity,
                                 ArrayList<Equipement> equipements, Dao dataAccessObject) {
        // TODO Auto-generated constructor stub

        this.equipements = equipements;
        orignalList = new ArrayList<Equipement>();
        orignalList.addAll(equipements);

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
        TextView equipmentNameTv;

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

        if (count < equipements.size()) {

            return count;
        } else {
            return equipements.size();
        }
        // } else {
        // return equipements.size();
        // }
    }

    public int getArrayCount() {
        return equipements.size();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return equipements.get(position);
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
        Equipement client = (Equipement) getItem(position);

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(
                    R.layout.list_item_autocomplete, null);

            viewHolder.equipmentNameTv = (TextView) convertView
                    .findViewById(R.id.dataNameTv);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (client.getRefCustomer().length()>0){
            viewHolder.equipmentNameTv.setText(client.getNmEquipement()+" ("+client.getRefCustomer()+")");
        }else {
            viewHolder.equipmentNameTv.setText(client.getNmEquipement());
        }
        return convertView;
    }

    /**
     * Adds the equipment to orignal list.
     *
     * @param equipements the equipements
     */
    public void addEquipmentToOrignalList(ArrayList<Equipement> equipements) {
        orignalList.clear();
        orignalList.addAll(equipements);
    }

    /**
     * Sets the index position.
     *
     * @param index the new index position
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
                equipements.clear();
                equipements.addAll((ArrayList<Equipement>) results.values);

                if (activity instanceof EquipmentList) {
                    EquipmentList equipmentList = ((EquipmentList) activity);
                    if (equipements.size() > 0) {
                        if (equipmentList != null) {
                            equipmentList.showFooterView();
                        }
                    } else {
                        if (equipmentList != null) {
                            equipmentList.hideFooterView();
                        }

                    }
                }
                notifyDataSetChanged();

            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                // TODO Auto-generated method stub
                FilterResults results = new FilterResults();
                ArrayList<Equipement> aux = new ArrayList<Equipement>();

                // El prefijo tiene que ser mayor que 0 y existir
                if (constraint != null && constraint.toString().length() > 0) {
                    isUserSearching = true;
                    for (Equipement equipement : orignalList) {

                        String nom = equipement.getNmEquipement();

                        // remove all the accented characters before search.
                        String asciiSearchString = AccentInsensitive
                                .removeAccentCase(nom);
                        String asciiConstraint = AccentInsensitive
                                .removeAccentCase(constraint.toString());
                        if (asciiConstraint != null && asciiConstraint.length() > 0)

                            if ((asciiSearchString.toLowerCase().contains(asciiConstraint.toString()
                                                                                  .toLowerCase()))) {

                                aux.add(equipement);

                            }

                    }
                    results.values = aux;
                    results.count = aux.size();

                } else {
                    synchronized (equipements) {
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
