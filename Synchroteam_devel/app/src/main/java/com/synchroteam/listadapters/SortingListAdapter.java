package com.synchroteam.listadapters;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.AllJobsSorting;
import com.synchroteam.dialogs.AllJobsSortingDialog;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.SharedPref;

import java.util.ArrayList;

/**
 * Adapter class for showing sorting options.
 * Created by Trident on 5/20/2016.
 */
public class SortingListAdapter extends ArrayAdapter<AllJobsSorting> {

    private LayoutInflater inflater;
    private Context context;
    private ArrayList<AllJobsSorting> listSortingOptions;
    private AllJobsSortingDialog dialog;
    private boolean isCurrentJobSorting;

    public SortingListAdapter(Context context, AllJobsSortingDialog dialog, ArrayList<AllJobsSorting> listSortingOptions, boolean isCurrentJobSorting) {
        super(context, R.layout.rowview_dialog_sorting);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.listSortingOptions = listSortingOptions;
        this.dialog = dialog;
        this.isCurrentJobSorting = isCurrentJobSorting;
    }

    @Override
    public int getCount() {
        return listSortingOptions.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderSorting holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.rowview_dialog_sorting, parent, false);
            holder = new ViewHolderSorting(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderSorting) convertView.getTag();
        }

        final AllJobsSorting allJobsSorting = listSortingOptions.get(position);
        holder.txtSortingOption.setText(allJobsSorting.getSortingName());
        if (allJobsSorting.getSelected()) {
            holder.txtIcSelect.setVisibility(View.VISIBLE);

            if ((position == KEYS.AllJobSortingOptions.SORT_BY_NEARBY_JOB && !isCurrentJobSorting) || (position == KEYS.CurrentJobsSorting.SORT_BY_NEARBY_JOB && isCurrentJobSorting)) {
                String mJobNumber = SharedPref.getSortedJobNumber(context);
                holder.edtJobNumber.setVisibility(View.VISIBLE);
                holder.edtJobNumber.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        dialog.setSortingValues(position, allJobsSorting.getSortingName(), s.toString());
                        SharedPref.setSortedJobNumber(s.toString(), context);
                    }
                });
                if (!TextUtils.isEmpty(mJobNumber) || !mJobNumber.equalsIgnoreCase("0")) {
                    holder.edtJobNumber.setText(mJobNumber);
                    dialog.setSortingValues(position, listSortingOptions.get(position).getSortingName(), mJobNumber);
                } else {
                    holder.edtJobNumber.setText("");
                    dialog.setSortingValues(position, listSortingOptions.get(position).getSortingName(), "");
                }


            } else {
                holder.edtJobNumber.setVisibility(View.GONE);
                dialog.setSortingValues(position, listSortingOptions.get(position).getSortingName(), "");
            }
        } else {
            holder.txtIcSelect.setVisibility(View.GONE);
            holder.edtJobNumber.setVisibility(View.GONE);
        }

        holder.mRelParent.setTag(position);
        holder.mRelParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                for (int i = 0; i < listSortingOptions.size(); i++) {
                    AllJobsSorting sorting = listSortingOptions.get(i);
                    if (i == pos) {
                        sorting.setSelected(true);
                    } else {
                        sorting.setSelected(false);
                    }
                }

                if (pos != KEYS.AllJobSortingOptions.SORT_BY_NEARBY_JOB) {
                    dialog.setSortingValues(pos, listSortingOptions.get(pos).getSortingName(), "");
                }
                notifyDataSetChanged();
            }
        });

        if (position == getCount() - 1) {
            holder.mViewDivider.setVisibility(View.GONE);
        } else {
            holder.mViewDivider.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    /**
     * View holder class for sorting options row.
     */
    class ViewHolderSorting {
        RelativeLayout mRelParent;
        TextView txtSortingOption;
        EditText edtJobNumber;
        View mViewDivider;

        android.widget.TextView txtIcSelect;
        Typeface mTypeface;

        public ViewHolderSorting(View itemView) {
            mRelParent = (RelativeLayout) itemView.findViewById(R.id.rel_serial_parent);
            txtSortingOption = (TextView) itemView.findViewById(R.id.txt_sorting_option);
            edtJobNumber = (EditText) itemView.findViewById(R.id.ed_soting_job_number);
            txtIcSelect = (android.widget.TextView) itemView.findViewById(R.id.txt_item_select);
            mViewDivider = (View) itemView.findViewById(R.id.view_divider_line);

            mTypeface = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.fontName_fontAwesome));
            txtIcSelect.setTypeface(mTypeface);

        }
    }

}
