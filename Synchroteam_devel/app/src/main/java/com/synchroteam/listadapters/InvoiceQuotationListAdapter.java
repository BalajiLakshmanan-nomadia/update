package com.synchroteam.listadapters;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.Invoice_Quotation_Items_Beans;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.DeleteInvoiceQuotationItemDialog;
import com.synchroteam.events.UpdateInvoiceQuotationTaxEvent;
import com.synchroteam.fragment.InvoiceQuotationFragment;
import com.synchroteam.synchroteam.UpdateInvoiceQuotation;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.technicalsupport.JobDetails;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.KEYS;

import de.greenrobot.event.EventBus;

/**
 * This adapter holds the view for list item of a single invoice/quotation.
 *
 * @author Trident
 */
public class InvoiceQuotationListAdapter extends ArrayAdapter<String> {

    private final Context context;
    private ArrayList<Invoice_Quotation_Items_Beans> invoiceQuotationList;
    private String strTax, strDiscount, strQty;
    // private static final String TAG = "InvoiceQuotationListAdapter";
    private int flag, idClient, idSite;
    private String idInterv;
    private Dao dao;
    private int flCreateUpdate;
    InvoiceQuotationFragment listFragment;

    private String currencyType;

    // int pos;

    public InvoiceQuotationListAdapter(Context context,
                                       InvoiceQuotationFragment listFragment,
                                       ArrayList<Invoice_Quotation_Items_Beans> invoiceQuotationList,
                                       int flag, String idInterv, int idClient, int idSite,
                                       int flCreateUpdate, String currencyType) {
        super(context, R.layout.rowview_invoice_quotation);
        this.context = context;
        this.listFragment = listFragment;
        this.invoiceQuotationList = invoiceQuotationList;
        this.flag = flag;
        this.idInterv = idInterv;
        this.idClient = idClient;
        this.idSite = idSite;
        this.flCreateUpdate = flCreateUpdate;
        this.currencyType = currencyType;
        dao = DaoManager.getInstance();
    }

    @Override
    public int getCount() {
        return invoiceQuotationList.size();
    }

    @Override
    public int getPosition(String item) {
        // TODO Auto-generated method stub
        return super.getPosition(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater mInflater = ((Activity) context).getLayoutInflater();
            convertView = mInflater.inflate(R.layout.rowview_invoice_quotation,
                    parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        // holder.position = position;
        strDiscount = Double.toString(invoiceQuotationList.get(position)
                .getDiscount());
        strTax = Double.toString(invoiceQuotationList.get(position).getTax());

        strQty = Double.toString(invoiceQuotationList.get(position)
                .getQuantity());
        String strItem = invoiceQuotationList.get(position).getItem();
        if (TextUtils.isEmpty(strItem)) {
            strItem = "";
        }
        holder.txtItem.setText(strItem + " "
                + invoiceQuotationList.get(position).getDescription());
        holder.txtItemTotal.setText(String.format(Locale.US, "%.2f", invoiceQuotationList
                .get(position).getTotal()) + " " + currencyType);

        // holder.txtQty.setText(strQty.substring(0, strQty.indexOf(".")));
        holder.txtQty.setText(strQty);
        holder.txtUnitPrice.setText(BigDecimal.valueOf(invoiceQuotationList.get(
                position).getUnitPrice()).toPlainString() + " " + currencyType);
        if (!strDiscount.equalsIgnoreCase("0.0"))
            holder.txtDiscount.setText(strDiscount + "%");
        else
            holder.txtDiscount.setText("-");
        if (!strTax.equalsIgnoreCase("0.0"))
            holder.txtTAx.setText(strTax + "%");
        else
            holder.txtTAx.setText("-");

        if (flCreateUpdate == 0) {
            holder.txtDeleteItem.setVisibility(View.GONE);
            convertView.setOnClickListener(null);
        } else {
            holder.txtDeleteItem.setVisibility(View.VISIBLE);

            holder.linParent.setTag(position);
            holder.linParent.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (((JobDetails) context).getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {
                        // final ViewHolder holder = (ViewHolder) v.getTag();

                        // String itemDesc =
                        // holder.txtItem.getText().toString();
                        // String item = itemDesc.substring(0,
                        // itemDesc.indexOf(" "));
                        // String desc =
                        // itemDesc.substring(itemDesc.indexOf(" ") + 1);

                        // if (!holder.txtDiscount.getText().toString()
                        // .equalsIgnoreCase("-")) {
                        // discountPercent = holder.txtDiscount.getText()
                        // .toString();
                        // discount = discountPercent.substring(0,
                        // discountPercent.indexOf("%"));
                        // }
                        // String taxPercent =
                        // holder.txtTAx.getText().toString();
                        // String tax = null;
                        // if (!taxPercent.equalsIgnoreCase("-")) {
                        // tax = taxPercent;
                        // }

                        int pos = (int) v.getTag();
                        String item = invoiceQuotationList.get(pos).getItem();
                        String desc = invoiceQuotationList.get(pos)
                                .getDescription();

                        String discount = null;
                        String discountStr = Double.toString(invoiceQuotationList.get(pos)
                                .getDiscount());
                        if (!discountStr.equalsIgnoreCase("0.0")) {
                            discount = discountStr;
                        }

                        String taxPercent = Double.toString(invoiceQuotationList
                                .get(pos).getTax());
                        String tax = null;
                        if (!taxPercent.equalsIgnoreCase("0.0")) {
                            tax = taxPercent;
                        }

                        Intent intentUpdateItem = new Intent();
                        intentUpdateItem.putExtra("itemId",
                                invoiceQuotationList.get(pos).getId());
                        intentUpdateItem.putExtra("id_interv", idInterv);
                        intentUpdateItem.putExtra("id_client", idClient);
                        intentUpdateItem.putExtra("id_site", idSite);
                        intentUpdateItem.putExtra("item", item);
                        intentUpdateItem.putExtra("description", desc);
                        intentUpdateItem.putExtra("unitPrice", Double.toString(invoiceQuotationList.get(pos)
                                .getUnitPrice()));
                        intentUpdateItem.putExtra("qty", Double.toString(invoiceQuotationList.get(pos)
                                .getQuantity()));
                        intentUpdateItem.putExtra("discount", discount);
                        intentUpdateItem.putExtra("tax", tax);
                        intentUpdateItem.putExtra("total", String.format(Locale.US,
                                "%.2f", invoiceQuotationList.get(pos)
                                        .getTotalWIthTax()));
                        intentUpdateItem.putExtra("description_item", invoiceQuotationList.get(pos)
                                .getDescriptionItem());

                        intentUpdateItem.setClass(context,
                                UpdateInvoiceQuotation.class);
                        context.startActivity(intentUpdateItem);
                    }
                }
            });
        }

        holder.txtDeleteItem.setTag(position);
        holder.txtDeleteItem.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final int pos = (int) v.getTag();
                if (((JobDetails) context).getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {
                    DeleteInvoiceQuotationItemDialog deleteIQDialog = new DeleteInvoiceQuotationItemDialog(
                            context,
                            new DeleteInvoiceQuotationItemDialog.DeleteInvoiceQuotationInterface() {

                                @Override
                                public void doOnYesClick() {
                                    dao.deleteInvoiceOrQuotationItem(invoiceQuotationList
                                            .get(pos).getId());
                                    listFragment.updateValuesDB();
                                    listFragment.getIQList();
//                                    EventBus.getDefault().post(new UpdateInvoiceQuotationTaxEvent());
//									listFragment.refreshList();
                                }

                                @Override
                                public void doOnNoClick() {
                                    // TODO Auto-generated method stub

                                }
                            });

                    deleteIQDialog.show();
                }
            }
        });

        return convertView;
    }

    // ..........................................VIEWHOLDER...CLASS...STARTS...HERE..............................

    static class ViewHolder {
        TextView txtItem, txtItemTotal, txtQty, txtUnitPrice, txtDiscount,
                txtTAx, txtDeleteItem;
        LinearLayout linParent;

        // int position;

        public ViewHolder(View rowView) {
            linParent = (LinearLayout) rowView
                    .findViewById(R.id.linParentLayout);
            txtItem = (TextView) rowView.findViewById(R.id.txt_item);
            txtItemTotal = (TextView) rowView.findViewById(R.id.txtItemTotal);
            txtQty = (TextView) rowView.findViewById(R.id.txt_qty);
            txtUnitPrice = (TextView) rowView.findViewById(R.id.txt_unit_price);
            txtDiscount = (TextView) rowView.findViewById(R.id.txt_discount);
            txtTAx = (TextView) rowView.findViewById(R.id.txt_tax);
            txtDeleteItem = (TextView) rowView.findViewById(R.id.txtDeleteItem);
        }
    }

    // ******************************************VIEWHOLDER...CLASS...ENDS...HERE********************************

    public interface RefreshInvoiceQutationList {
        void getIQList();
    }
}