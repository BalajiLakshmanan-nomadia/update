package com.synchroteam.listadapters;

import android.content.Context;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.AllJobsSorting;
import com.synchroteam.dialogs.AllJobsSortingDialog;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.SharedPref;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Adapter class for showing sorting options.
 * Created by Trident on 5/20/2016.
 */
public class SortingRVAdapter extends RecyclerView.Adapter<SortingRVAdapter.ViewHolderSorting> {

    private LayoutInflater inflater;
    private Context context;
    private ArrayList<AllJobsSorting> listSortingOptions;
    private AllJobsSortingDialog dialog;

    public SortingRVAdapter(Context context, AllJobsSortingDialog dialog, ArrayList<AllJobsSorting> listSortingOptions) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.listSortingOptions = listSortingOptions;
        this.dialog = dialog;
    }

    @NotNull
    @Override
    public SortingRVAdapter.ViewHolderSorting onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.rowview_dialog_sorting, parent, false);
        ViewHolderSorting holder = new ViewHolderSorting(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(SortingRVAdapter.ViewHolderSorting holder, final int position) {
        final AllJobsSorting allJobsSorting = listSortingOptions.get(position);
        holder.txtSortingOption.setText(allJobsSorting.getSortingName());
        if (allJobsSorting.getSelected()) {
            holder.txtIcSelect.setVisibility(View.VISIBLE);

            if (position == KEYS.AllJobSortingOptions.SORT_BY_NEARBY_JOB) {
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
                    }
                });
                if (!mJobNumber.equalsIgnoreCase("0")){
                    holder.edtJobNumber.setText(mJobNumber);
                }else{
                    holder.edtJobNumber.setText("");
                }
            }else {
                holder.edtJobNumber.setVisibility(View.GONE);
            }
        } else {
            holder.txtIcSelect.setVisibility(View.GONE);
            holder.edtJobNumber.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listSortingOptions.size();
    }

    /**
     * View holder class for sorting options row.
     */
    class ViewHolderSorting extends RecyclerView.ViewHolder {
        TextView txtSortingOption;
        EditText edtJobNumber;
        android.widget.TextView txtIcSelect;
        Typeface mTypeface;

        public ViewHolderSorting(View itemView) {
            super(itemView);
            txtSortingOption = (TextView) itemView.findViewById(R.id.txt_sorting_option);
            edtJobNumber = (EditText) itemView.findViewById(R.id.ed_soting_job_number);
            txtIcSelect = (android.widget.TextView) itemView.findViewById(R.id.txt_item_select);

            mTypeface = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.fontName_fontAwesome));
            txtIcSelect.setTypeface(mTypeface);

            itemView.setOnClickListener(mClickListener);
        }

        /**
         * Click listener for item view.
         */
        View.OnClickListener mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < listSortingOptions.size(); i++) {
                    AllJobsSorting sorting = listSortingOptions.get(i);
                    if (i == getAdapterPosition()) {
                        sorting.setSelected(true);
                    } else {
                        sorting.setSelected(false);
                    }
                }

                if (getAdapterPosition() != KEYS.AllJobSortingOptions.SORT_BY_NEARBY_JOB) {
                    dialog.setSortingValues(getAdapterPosition(), listSortingOptions.get(getAdapterPosition()).getSortingName(), "");
                }
                notifyDataSetChanged();
            }
        };

    }

}
