package com.synchroteam.catalouge;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.synchroteam.beans.Categorie;
import com.synchroteam.beans.CategoryAndPartsInterface;
import com.synchroteam.dao.Dao;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AccentInsensitive;

import java.util.ArrayList;
import java.util.List;

// TODO: Auto-generated Javadoc

/**
 * Adapter Class to for Category List. created for future purpose
 *
 * @author Ideavate.solution
 */
public class CategoryItemsAdapter extends ArrayAdapter<String> {

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

    /**
     * The index.
     */
    private int index;

    private int baseCount = 20;

    CatalougeSubCategotyUpdated catalougeSubCategory;


    public CategoryItemsAdapter(Activity activity,int resources,
                                ArrayList<CategoryAndPartsInterface> categories, Dao dataAccessObject,
                                String idIntervention) {
        super(activity,resources);
        // TODO Auto-generated constructor stub
        this.items = activity;
        this.categories = categories;
        orignalList = new ArrayList<CategoryAndPartsInterface>();
        this.dataAccessObject = dataAccessObject;
        orignalList.addAll(categories);

        this.idIntervention = idIntervention;
        layoutInflater = (LayoutInflater) items
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.catalougeSubCategory = (CatalougeSubCategotyUpdated) activity;
    }

    /**
     * The Class ViewHolder.
     */
    private class ViewHolder {


        TextView textView;
        LinearLayout linear_category;

        public ViewHolder(View rowView) {
            textView=  rowView.findViewById(R.id.itemCategoryTv);
            linear_category = (LinearLayout) rowView.findViewById(R.id.linear_category);
        }
    }


    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {

//        int count = index * baseCount;
//
//        if (count < categories.size()) {
//
//            return count;
//        } else {
//            return categories.size();
//        }

        return categories.size();

    }


    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public String getItem(int position) {
        // TODO Auto-generated method stub
        return String.valueOf(categories.get(position));
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

                catalougeSubCategory.setClickAdapterCheck(categoryAndPartsInterface);
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
                        ((CatalougeSubCategory) items).showFooterView();
                    }
                } else {
                    if (items != null) {
                        ((CatalougeSubCategory) items).hideFooterView();
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
