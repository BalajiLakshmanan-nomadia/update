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
import com.synchroteam.dialogs.DeletePartsDialog;
import com.synchroteam.dialogs.TaxListDialog;
import com.synchroteam.smoothcheckbox.SmoothCheckBox;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DaoManager;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

/**
 * Adapter class for showing all Selected Tax names
 * </p>
 * Created by Trident
 */

public class TaxListAdapterRv extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Activity activity;
    private ArrayList<GlobalTaxInvoiceList> taxInvoiceLists;
    String currencyType;
    TaxListDialog taxListDialog;
    private Dao dao;
    int noAfterDecimal = 2;
    /**
     * Normal item view holder
     */
    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public TextView txtTaxItem, txtDeleteItem;
        SmoothCheckBox checkBox;


        public ItemViewHolder(View view) {
            super(view);

            txtTaxItem = (TextView) view.findViewById(R.id.txt_tax_item);
            checkBox = (SmoothCheckBox) view.findViewById(R.id.chk_compound_tax);
            txtDeleteItem = (TextView) view.findViewById(R.id.txtDeleteItem);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {


            }
        }
    }


    public TaxListAdapterRv(Activity activity,
                            ArrayList<GlobalTaxInvoiceList> taxInvoiceLists, String currencyType,
                            TaxListDialog taxListDialog) {
        this.activity = activity;
        this.taxInvoiceLists = taxInvoiceLists;
        this.currencyType = currencyType;
        this.taxListDialog = taxListDialog;
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
        View view = LayoutInflater.from(activity).inflate(R.layout.row_selected_global_tax_list, parent,
                false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        holder.itemView.setClickable(true);
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

            String taxName = taxInvoiceLists.get(position).getTaxName();
            if (taxName == null)
                taxName = activity.getResources().getString(R.string.txt_tax_label);
            BigDecimal taxDecimal = new BigDecimal(taxInvoiceLists.get(position).getTax()).
                    setScale(noAfterDecimal, RoundingMode.HALF_UP);

            itemViewHolder.txtTaxItem.setText(taxName + "(" +
                    taxDecimal + "%)"
            );

            if (taxInvoiceLists.get(position).isHasCompound()) {
                itemViewHolder.checkBox.setChecked(true, true);
            }
            itemViewHolder.checkBox.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                    taxListDialog.taxValueCalculation(isChecked, taxInvoiceLists.get(position).getTax(),
                            taxInvoiceLists.get(position).getIdRemote());

                }
            });

            itemViewHolder.txtDeleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    DeletePartsDialog deletePartsDialog = new DeletePartsDialog(
                            activity,
                            new DeletePartsDialog.DeletePartsInterface() {

                                @Override
                                public void doOnYesClick() {

                                    taxListDialog.deleteGlobalTaxItem(taxInvoiceLists.get(position).getIdRemote());
                                }

                                @Override
                                public void doOnNoClick() {

                                }
                            });

                    deletePartsDialog.show();


                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return taxInvoiceLists.size();

    }


}
