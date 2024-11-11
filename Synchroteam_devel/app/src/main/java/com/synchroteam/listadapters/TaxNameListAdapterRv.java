package com.synchroteam.listadapters;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.beans.GlobalTaxInvoiceList;
import com.synchroteam.dao.Dao;
import com.synchroteam.fragment.InvoiceQuotationFragmentNew;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DaoManager;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * Adapter class for showing all tax names
 * </p>
 * Created by Trident
 */

public class TaxNameListAdapterRv extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Activity activity;
    private ArrayList<GlobalTaxInvoiceList> taxInvoiceLists;
    private String currencyType;
    private InvoiceQuotationFragmentNew fragment;
    int noAfterDecimal = 2;
    private Dao dao;
    /**
     * Normal item view holder
     */
    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public TextView txtTaxItem;
        public TextView txtTaxItemValue;

        public ItemViewHolder(View view) {
            super(view);

            txtTaxItem = (TextView) view.findViewById(R.id.txtTaxItem);
            txtTaxItemValue = (TextView) view.findViewById(R.id.txtTaxItemValue);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                fragment.showTaxListDialog();
            }
        }
    }


    public TaxNameListAdapterRv(Activity activity,
                                ArrayList<GlobalTaxInvoiceList> taxInvoiceLists, String currencyType, InvoiceQuotationFragmentNew fragment) {
        this.activity = activity;
        this.taxInvoiceLists = taxInvoiceLists;
        this.currencyType = currencyType;
        this.fragment = fragment;
        dao = DaoManager.getInstance();
        GestionAcces gestionAcces = dao.getAcces();
        try {
            noAfterDecimal = gestionAcces.getNumDecimals();
        } catch (Exception e) {
            noAfterDecimal = 2;
        }
    }


    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.rowview_tax_name_value_item, parent,
                false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        holder.itemView.setClickable(true);
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            String taxName=taxInvoiceLists.get(position).getTaxName();
            if(taxName==null)
                taxName=activity.getResources().getString(R.string.txt_tax_label);
            BigDecimal taxDecimal = new BigDecimal(taxInvoiceLists.get(position).getTax()).
                    setScale(noAfterDecimal, RoundingMode.HALF_UP);

            itemViewHolder.txtTaxItem.setText(taxName + "(" +
                    taxDecimal + "%)"
            );
            BigDecimal decimal = new BigDecimal(taxInvoiceLists.get(position).getTaxValue()).
                    setScale(noAfterDecimal, RoundingMode.HALF_UP);
            itemViewHolder.txtTaxItemValue.setText("" +  decimal + currencyType);
        }
    }

    @Override
    public int getItemCount() {
        return taxInvoiceLists.size();

    }


}
