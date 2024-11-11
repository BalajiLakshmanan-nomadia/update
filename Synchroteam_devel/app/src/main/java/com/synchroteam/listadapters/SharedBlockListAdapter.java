package com.synchroteam.listadapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.AllJobsSorting;
import com.synchroteam.beans.SharedBlocks;
import com.synchroteam.dialogs.SharedBlockListDialog;
import com.synchroteam.smoothcheckbox.SmoothCheckBox;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AccentInsensitive;
import com.synchroteam.utils.MaterialDesignUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class for showing shared block list.
 * Created by Trident on 7/19/2016.
 */
public class SharedBlockListAdapter extends ArrayAdapter<AllJobsSorting> implements Filterable{

    private LayoutInflater inflater;
    private Context context;
    private ArrayList<SharedBlocks> listSharedBlocks;
    private ArrayList<SharedBlocks> listOrginalSB;
    private SharedBlockListDialog dialog;
    private boolean isSearching;

    public SharedBlockListAdapter(Context context, SharedBlockListDialog dialog, ArrayList<SharedBlocks> listSharedBlocks) {
        super(context,R.layout.rowview_shared_blocks);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.listSharedBlocks = listSharedBlocks;
        this.dialog = dialog;
    }

    @Override
    public int getCount() {
        return listSharedBlocks.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolderSorting holder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.rowview_shared_blocks, parent, false);
            holder = new ViewHolderSorting(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolderSorting) convertView.getTag();
        }

        final SharedBlocks sharedBlocks = listSharedBlocks.get(position);
        holder.txtSharedBlock.setText(sharedBlocks.getBlockName());

        holder.mRelParent.setTag(position);
        holder.mRelParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                for (int i = 0; i < listSharedBlocks.size(); i++) {
                    SharedBlocks blocks = listSharedBlocks.get(i);
                    if (i == pos) {
                        blocks.setSelected(true);
                    } else {
                        blocks.setSelected(false);
                    }
                }
                    dialog.setSB(listSharedBlocks.get(pos));
                notifyDataSetChanged();
            }
        });


        holder.scbSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.mRelParent.performClick();
            }
        });

        if (sharedBlocks.getSelected()) {
            //don't animate the checkbox while searching
            if (isSearching) {
                holder.scbSelect.setChecked(true, false);
                isSearching = false;
            }else {
                holder.scbSelect.setChecked(true, true);
            }
        }else {
            holder.scbSelect.setChecked(false);
        }

        if (position != 0 && position == getCount()-1){
            holder.mViewDivider.setVisibility(View.GONE);
        }else{
            holder.mViewDivider.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    /**
     * View holder class for sorting options row.
     */
    class ViewHolderSorting {
        RelativeLayout mRelParent;
        TextView txtSharedBlock;
        View mViewDivider;
        SmoothCheckBox scbSelect;

        public ViewHolderSorting(View itemView) {
            mRelParent = (RelativeLayout) itemView.findViewById(R.id.rel_serial_parent);
            txtSharedBlock = (TextView) itemView.findViewById(R.id.txt_shared_block);
            scbSelect = (SmoothCheckBox)itemView.findViewById(R.id.scb_shared_block);
            mViewDivider = itemView.findViewById(R.id.view_divider_line);

            MaterialDesignUtils.getInstance(context).setRippleEffect(mRelParent);
        }

    }

    /**
     * Filter for shared block
     * @return filter
     */
    @Override
    public Filter getFilter() {
        Filter filterSb = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                isSearching = true;
                FilterResults results = new FilterResults();
                List<SharedBlocks> filteredSBList = new ArrayList<>();

                //saves the original list
                if (listOrginalSB == null){
                    listOrginalSB = new ArrayList<>(listSharedBlocks);
                }

                if (constraint == null || constraint.length() == 0){
                    results.count = listOrginalSB.size();
                    results.values = listOrginalSB;
                }else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < listOrginalSB.size(); i++) {
                        String data = listOrginalSB.get(i).getBlockName().toLowerCase();

                        String asciiSearchString = AccentInsensitive.removeAccentCase(data);
                        String asciiConstraint = AccentInsensitive.removeAccentCase(constraint.toString());

                        if (asciiSearchString.contains(asciiConstraint)){
                            filteredSBList.add(listOrginalSB.get(i));
                        }
                    }
                    results.count = filteredSBList.size();
                    results.values = filteredSBList;
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listSharedBlocks = (ArrayList<SharedBlocks>) results.values;
                notifyDataSetChanged();
            }
        };
        return filterSb;
    }
}
