package com.synchroteam.listadapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAutoCompleteAdapter1 extends ArrayAdapter<String> {

    private ArrayList<String> fullList;
    private ArrayList<String> mOriginalValues;
    private int viewResourceId;
//	private static final String TAG = "CustomAutoCompleteAdapter";

    public CustomAutoCompleteAdapter1(Context context, int viewResourceId,
                                      int textViewResourceId, ArrayList<String> objects) {
        super(context, viewResourceId, textViewResourceId, objects);
        this.viewResourceId = viewResourceId;
        fullList = objects;
        mOriginalValues = objects;
    }

    @Override
    public int getCount() {
        return mOriginalValues.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(viewResourceId, parent,false);
        }
        TextView productLabel = (TextView) v.findViewById(android.R.id.text1);
        if (productLabel != null) {
            productLabel.setText(mOriginalValues.get(position));
        }
        return v;
    }

    @Override
    public Filter getFilter() {
        return textFilter;
    }

    @SuppressLint("DefaultLocale")
    Filter textFilter = new Filter() {

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            mOriginalValues = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }

        @SuppressLint("DefaultLocale")
        @Override
        protected FilterResults performFiltering(CharSequence charSearch) {
            FilterResults results = new FilterResults();
            if (charSearch == null || charSearch.length() == 0) {
                results.values = fullList;
                results.count = fullList.size();
            } else {
                ArrayList<String> filteredItem = new ArrayList<String>();

                final ArrayList<String> list = fullList;
                /*
				 * We'll go through all the location and see if they contain the
				 * supplied string/character
				 */
                for (int i = 0; i < list.size(); i++) {
                    String c = list.get(i);
                    if (charSearch != null && charSearch.length() > 0)
                        if (c.toLowerCase().contains(
                                charSearch.toString().toLowerCase())) {

                            // if `contains` == true then add it
                            // to our filtered list

                            filteredItem.add(c);
                        }
                }

                // Finally set the filtered values and size/count
                results.values = filteredItem;
                results.count = filteredItem.size();

            }
            return results;
        }
    };
}
