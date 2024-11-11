package com.synchroteam.listadapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.Categorie;
import com.synchroteam.beans.CategoryAndPartsInterface;
import com.synchroteam.catalouge.CatalougeSubCategotyUpdated;
import com.synchroteam.dao.Dao;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AccentInsensitive;
import com.synchroteam.utils.Logger;

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc

/**
 * Adapter Class to for Category List. created for future purpose
 *
 * @author Ideavate.solution
 */
public class CategoryAdapter extends BaseAdapter {

    /**
     * The activity.
     */
    private Activity items;

    /**
     * The current jobs beans.
     */
    private List<CategoryAndPartsInterface> categories;

    /**
     * The orignal list.
     */
    private List<CategoryAndPartsInterface> orignalList;

    /**
     * The is user searching.
     */
    private boolean isUserSearching = false;

    /**
     * The layout inflater.
     */
    private LayoutInflater layoutInflater;

    /**
     * The id intervention.
     */
    private String idIntervention;

    /**
     * The data access object.
     */
    private Dao dataAccessObject;

    /**
     * The val depot.
     */
    private int valDepot = 0;

    CatalougeSubCategotyUpdated catalougeSubCategotyUpdated;

    // private static final String TAG = "CategoryAdapter";

    /**
     * Instantiates a new current jobs list adapter.
     *
     * @param activity         the activity
     * @param categories       the categories
     * @param dataAccessObject the data access object
     * @param idIntervention   the id intervention
     */
    public CategoryAdapter(Activity activity,
                           List<CategoryAndPartsInterface> categories, Dao dataAccessObject,
                           String idIntervention) {
        // TODO Auto-generated constructor stub
        this.items = activity;
        this.categories = categories;
        orignalList = new ArrayList<CategoryAndPartsInterface>();
        this.dataAccessObject = dataAccessObject;
        orignalList.addAll(categories);

        this.idIntervention = idIntervention;
        layoutInflater = (LayoutInflater) items
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.catalougeSubCategotyUpdated = (CatalougeSubCategotyUpdated) activity;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return categories.size();
    }

    public int getArrayCount() {
        return categories.size();
    }


    /**
     * The Class ViewHolder.
     */
    private class ViewHolder {


        android.widget.TextView textView;
        LinearLayout linear_category;

        public ViewHolder(View rowView) {
            textView=  rowView.findViewById(R.id.itemCategoryTv);
            linear_category = (LinearLayout) rowView.findViewById(R.id.linear_category);
        }
    }


    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return categories.get(position);
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
        CategoryAndPartsInterface categoryAndPartsInterface = categories.get(position);

        Categorie categorie = (Categorie) categoryAndPartsInterface;

        if (convertView == null) {

            convertView = layoutInflater.inflate(
                    R.layout.layout_items_main_listitem, parent,false);

            viewHolder = new ViewHolder(convertView);

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.textView.setText(categorie.getNomcat());

        viewHolder.linear_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                catalougeSubCategotyUpdated.setClickAdapterCheck(categoryAndPartsInterface);
            }
        });

        return convertView;
    }

    // }
    // else{
    // CatalougePieces pice = (CatalougePieces)categoryAndPartsInterface;
    //
    // View view = layoutInflater.inflate(R.layout.layout_items_sublistitem,
    // null);
    // TextView itemTv = (TextView) view.findViewById(R.id.itemTv);
    // ImageView addItemTv = (ImageView) view.findViewById(R.id.addItemIv);
    // String numberOfPices = pice.getNumber_of_pices();
    // if (numberOfPices.equals("0") || numberOfPices.equals("0.00")) {
    // addItemTv.setImageResource(R.drawable.addcell);
    // addItemTv.setOnClickListener(clickListener);
    // //
    // Bundle bundle = new Bundle();
    // bundle.putString(KEYS.Items.PIECE_ID, pice.getId());
    // bundle.putString(KEYS.Items.PRIX, pice.getPrix());
    // bundle.putString(KEYS.Items.NOM_PIECE,
    // pice.getNom_piece());
    //
    //
    // bundle.putBoolean(KEYS.Items.IS_ITEM_SELECTED, false);
    //
    // addItemTv.setTag(bundle);
    // addItemTv.setId(arg0);
    // } else {
    // addItemTv.setImageResource(R.drawable.item_selected_icon);
    // addItemTv.setOnClickListener(clickListener);
    //
    // Bundle bundle = new Bundle();
    // bundle.putString(KEYS.Items.PIECE_ID, pice.getId());
    // bundle.putString(KEYS.Items.PRIX, pice.getPrix());
    // bundle.putString(KEYS.Items.NOM_PIECE,
    // pice.getNom_piece());
    //
    //
    // bundle.putBoolean(KEYS.Items.IS_ITEM_SELECTED, true);
    //
    // addItemTv.setTag(bundle);
    // addItemTv.setId(arg0);
    // }
    // itemTv.setText(pice.getNom_piece());
    //
    // return view;
    // }


    /** The click listener. */
    // private OnClickListener clickListener=new OnClickListener() {
    //
    // @Override
    // public void onClick(View v) {
    // // TODO Auto-generated method stub
    //
    // // TODO Auto-generated method stub
    // Bundle bundle = (Bundle) v.getTag();
    // int index = v.getId();
    // boolean isItemSelected = bundle
    // .getBoolean(KEYS.Items.IS_ITEM_SELECTED);
    // if (isItemSelected) {
    //
    // ((ImageView) v).setImageResource(R.drawable.addcell);
    //
    // bundle.putBoolean(KEYS.Items.IS_ITEM_SELECTED, false);
    //
    // v.setTag(bundle);
    //
    //
    // if (dataAccessObject.getNbreSorPieByIdPieAndIdInter(
    // bundle
    // .getString(KEYS.Items.PIECE_ID), idIntervention) != null) {
    // String[] tb = dataAccessObject.getNbreSorPieByIdPieAndIdInter(
    // bundle
    // .getString(KEYS.Items.PIECE_ID), idIntervention);
    // // qt.setText(String.valueOf(tb[0]));
    //
    // valDepot = Integer.parseInt(tb[1]);
    //
    // dataAccessObject.majQuantite(bundle
    // .getString(KEYS.Items.PIECE_ID),
    // idIntervention, String.valueOf("0"), valDepot);
    //
    // } else
    // dataAccessObject.insertSortiePiece(idIntervention,
    // bundle
    // .getString(KEYS.Items.PIECE_ID), String.valueOf("0"), valDepot);
    //
    // valDepot = 0;
    //
    //
    // CatalougePieces pice = (CatalougePieces) categories.get(index);
    // pice.setNumber_of_pices("0");
    // categories.set(index, pice);
    //
    //
    // }
    // else {
    //
    // ((ImageView) v).setImageResource(R.drawable.item_selected_icon);
    //
    // bundle.putBoolean(KEYS.Items.IS_ITEM_SELECTED, true);
    //
    // v.setTag(bundle);
    // // items.setItem(new ItemHolder(bundle
    // // .getString(KEYS.Items.PIECE_ID), bundle
    // // .getString(KEYS.Items.PRIX), bundle
    // // .getString(KEYS.Items.NOM_PIECE)));
    //
    //
    //
    //
    // if (dataAccessObject.getNbreSorPieByIdPieAndIdInter(
    // bundle
    // .getString(KEYS.Items.PIECE_ID), idIntervention) != null) {
    // String[] tb = dataAccessObject.getNbreSorPieByIdPieAndIdInter(
    // bundle
    // .getString(KEYS.Items.PIECE_ID), idIntervention);
    // // qt.setText(String.valueOf(tb[0]));
    //
    // valDepot = Integer.parseInt(tb[1]);
    //
    // dataAccessObject.majQuantite(bundle
    // .getString(KEYS.Items.PIECE_ID),
    // idIntervention, String.valueOf("1"), valDepot);
    //
    // } else
    // dataAccessObject.insertSortiePiece(idIntervention,
    // bundle
    // .getString(KEYS.Items.PIECE_ID), String.valueOf("1"), valDepot);
    //
    // valDepot = 0;
    //
    //
    // }
    //
    // CatalougePieces pice = (CatalougePieces) categories.get(index);
    // pice.setNumber_of_pices("1");
    // categories.set(index, pice);
    //
    // }
    // };

    /**
     * Gets the group count.
     *
     * @return the group count
     */
    public int getGroupCount() {

        return categories.size();
    }

    /**
     * Sets the index position.
     *
     * @param index the new index position
     */
    public void setIndexPosition(int index) {

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
                categories.clear();
                categories.addAll((ArrayList<Categorie>) results.values);

                if (categories.size() > 0) {
                    if (items != null) {
                        ((CatalougeSubCategotyUpdated) items).showFooterView();
                    }
                } else {
                    if (items != null) {
                        ((CatalougeSubCategotyUpdated) items).hideFooterView();
                    }

                }
                notifyDataSetChanged();

            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                // TODO Auto-generated method stub
                FilterResults results = new FilterResults();
                ArrayList<CategoryAndPartsInterface> aux = new ArrayList<CategoryAndPartsInterface>();

                // El prefijo tiene que ser mayor que 0 y existir
                if (constraint != null && constraint.toString().length() > 0) {
                    // isUserSearching = true;
                    for (CategoryAndPartsInterface categoryAndPartsInterface : orignalList) {

                        Categorie piece = (Categorie) categoryAndPartsInterface;
                        String nom = piece.getNomcat();

                        // remove all the accented characters before search.
                        String asciiSearchString = AccentInsensitive
                                .removeAccentCase(nom);
                        String asciiConstraint = AccentInsensitive
                                .removeAccentCase(constraint.toString());

                        if ((asciiSearchString.toLowerCase()
                                .contains(asciiConstraint))) {

                            aux.add(piece);

                        }

                    }

                    // aux.addAll(dataAccessObject.getFiltredPices(constraint.toString()
                    // ,idIntervention));

                    results.values = aux;
                    results.count = aux.size();

                } else {
                    synchronized (categories) {
                        // isUserSearching = false;
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
