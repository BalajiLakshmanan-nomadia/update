package com.synchroteam.listadapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.ImageView;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.CatalougePieces;
import com.synchroteam.dao.Dao;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.KEYS;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc

/**
 * Adapter Class to for Catalouge List. created for future purpose
 *
 * @author Ideavate.solution
 */
public class PiecesListAdapter extends BaseAdapter {

    /** The activity. */

    /**
     * The deadline job beans.
     */
    private ArrayList<CatalougePieces> piecesList;

    /**
     * The orignal list.
     */
    private ArrayList<CatalougePieces> orignalList;

    /**
     * The layout inflater.
     */
    private LayoutInflater layoutInflater;

    /**
     * The data access object.
     */
    private Dao dataAccessObject;

    /**
     * The id intervention.
     */
    private String idIntervention;

    /**
     * The val depot.
     */
    private int valDepot = 0;

    //private static final String TAG = "PiecesListAdapter";

    /**
     * Instantiates a new deadline jobs list adapter.
     *
     * @param activity         the activity
     * @param piecesList       the pieces list
     * @param dataAccessObject the data access object
     * @param idInterventiion  the id interventiion
     */

    public PiecesListAdapter(Activity activity,
                             ArrayList<CatalougePieces> piecesList, Dao dataAccessObject,
                             String idInterventiion) {
        // TODO Auto-generated constructor stub

        this.dataAccessObject = dataAccessObject;
        this.idIntervention = idInterventiion;
        this.piecesList = piecesList;
        orignalList = new ArrayList<CatalougePieces>();
        orignalList.addAll(piecesList);
        layoutInflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        // TODO Auto-generated method stub

        return piecesList.size();

    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public CatalougePieces getItem(int position) {
        // TODO Auto-generated method stub

        return piecesList.get(position);

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
        CatalougePieces pice = getItem(position);

        if(convertView==null) {
            convertView = layoutInflater.inflate(R.layout.layout_items_sublistitem,
                    parent, false);

            TextView itemTv = (TextView) convertView.findViewById(R.id.itemTv);
            ImageView addItemTv = (ImageView) convertView
                    .findViewById(R.id.addItemIv);

            String numberOfPices = pice.getNumber_of_pices();

            Log.e("Selected Items:  ", "" + numberOfPices);

            if (numberOfPices.equals("0") || numberOfPices.equals("0.00")) {
                addItemTv.setImageResource(R.drawable.addcell);
                addItemTv.setOnClickListener(clickListener);

                //
                Bundle bundle = new Bundle();
                bundle.putString(KEYS.Items.PIECE_ID, pice.getId());
                bundle.putString(KEYS.Items.PRIX, pice.getPrix());
                bundle.putString(KEYS.Items.NOM_PIECE, pice.getNom_piece());

                bundle.putInt(KEYS.Items.CHILDNUMBER, position);
                bundle.putBoolean(KEYS.Items.IS_ITEM_SELECTED, false);

                addItemTv.setTag(bundle);
                addItemTv.setId(position);

            } else {
                addItemTv.setImageResource(R.drawable.item_selected_icon);
                addItemTv.setOnClickListener(clickListener);

                Bundle bundle = new Bundle();
                bundle.putString(KEYS.Items.PIECE_ID, pice.getId());
                bundle.putString(KEYS.Items.PRIX, pice.getPrix());
                bundle.putString(KEYS.Items.NOM_PIECE, pice.getNom_piece());

                bundle.putInt(KEYS.Items.CHILDNUMBER, position);
                bundle.putBoolean(KEYS.Items.IS_ITEM_SELECTED, true);

                addItemTv.setTag(bundle);
                addItemTv.setId(position);
            }

            itemTv.setText(pice.getNom_piece());
        }
        return convertView;
    }

    /**
     * The click listener.
     */
    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            // TODO Auto-generated method stub
            Bundle bundle = (Bundle) v.getTag();
            int index = v.getId();

            boolean isItemSelected = bundle
                    .getBoolean(KEYS.Items.IS_ITEM_SELECTED);
            if (isItemSelected) {

                ((ImageView) v).setImageResource(R.drawable.addcell);

                Log.e("Selected positions:  ", "" + index + piecesList.size());
                CatalougePieces pice = piecesList.get(index);
                pice.setNumber_of_pices("0");
                piecesList.set(index, pice);

                bundle.putBoolean(KEYS.Items.IS_ITEM_SELECTED, false);

                v.setTag(bundle);

                String[] tb = dataAccessObject.getNbreSorPieByIdPieAndIdInter(
                        bundle.getString(KEYS.Items.PIECE_ID), idIntervention);
                //todo CHECK IF IT IS OKAY WHEN WE PASS NUL TO THE SERIAL_SORTIE
                if (tb != null) {

                    // qt.setText(String.valueOf(tb[0]));

                    valDepot = Integer.parseInt(tb[1]);

                    dataAccessObject.majQuantite(
                            bundle.getString(KEYS.Items.PIECE_ID),
                            idIntervention, String.valueOf("0"), valDepot, null);

                } else
                    dataAccessObject.insertSortiePiece(idIntervention,
                            bundle.getString(KEYS.Items.PIECE_ID),
                            String.valueOf("0"), valDepot, null);

                valDepot = 0;

            } else {

                Log.e("Selected positions:  ", "" + index);

                CatalougePieces pice = piecesList.get(index);
                pice.setNumber_of_pices("1");
                piecesList.set(index, pice);

                ((ImageView) v).setImageResource(R.drawable.item_selected_icon);

                bundle.putBoolean(KEYS.Items.IS_ITEM_SELECTED, true);

                v.setTag(bundle);
                // items.setItem(new ItemHolder(bundle
                // .getString(KEYS.Items.PIECE_ID), bundle
                // .getString(KEYS.Items.PRIX), bundle
                // .getString(KEYS.Items.NOM_PIECE)));
                String[] tb = dataAccessObject.getNbreSorPieByIdPieAndIdInter(
                        bundle.getString(KEYS.Items.PIECE_ID), idIntervention);
                if (tb != null) {

                    // qt.setText(String.valueOf(tb[0]));

                    valDepot = Integer.parseInt(tb[1]);

                    dataAccessObject.majQuantite(
                            bundle.getString(KEYS.Items.PIECE_ID),
                            idIntervention, String.valueOf("1"), valDepot, null);

                } else
                    dataAccessObject.insertSortiePiece(idIntervention,
                            bundle.getString(KEYS.Items.PIECE_ID),
                            String.valueOf("1"), valDepot, null);

                valDepot = 0;

            }

        }
    };

    /**
     * Check qte.
     *
     * @param qte the qte
     * @return true, if successful
     */
    public boolean checkQte(String qte) {
        if (Float.parseFloat((qte)) >= 10000 || qte.length() > 7)
            return false;
        return true;
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
                piecesList.clear();
                piecesList.addAll((ArrayList<CatalougePieces>) results.values);

                notifyDataSetChanged();

            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                // TODO Auto-generated method stub
                FilterResults results = new FilterResults();
                ArrayList<CatalougePieces> aux = new ArrayList<CatalougePieces>();

                // El prefijo tiene que ser mayor que 0 y existir
                if (constraint != null && constraint.toString().length() > 0) {
                    for (CatalougePieces piece : orignalList) {

                        String nom = piece.getNom_piece();
                        if (constraint != null && constraint.length() > 0)
                            if ((nom.toLowerCase().contains(constraint))) {

                                aux.add(piece);

                            }

                    }
                    results.values = aux;
                    results.count = aux.size();

                } else {
                    synchronized (piecesList) {

                        results.values = orignalList;
                        results.count = orignalList.size();
                    }
                }
                return results;

            }
        };

        return filter;

    }

    /**
     * Gets the pices count.
     *
     * @return the pices count
     */
    public int getPicesCount() {
        return piecesList.size();
    }
}
