package com.synchroteam.dialogs;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.beans.GlobalTaxInvoiceList;
import com.synchroteam.beans.Invoice_Quotation_Items_Beans;
import com.synchroteam.beans.TaxRates;
import com.synchroteam.dao.Dao;
import com.synchroteam.events.UpdateInvoiceQuotationTaxEvent;
import com.synchroteam.listadapters.TaxListAdapterRv;
import com.synchroteam.smoothcheckbox.SmoothCheckBox;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.HintAdapter;
import com.synchroteam.utils.HintSpinner;
import com.synchroteam.utils.Logger;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Dialog fragment for showing list of Tax items.<p></p>
 * Created by Trident
 */
public class TaxListDialog extends DialogFragment implements View.OnClickListener {

    //--------------------------------INSTANCE...VARIABLES...STARTS--------------------------------------

    private RecyclerView rvTaxList;
    private LinearLayoutManager linearLayoutManager;
    private TaxListAdapterRv taxListAdapterRv;
    private Dao dataAccessObject;
    private Spinner spinnerTax;

    TextView txtCancel;

    private ArrayList<GlobalTaxInvoiceList> globalTaxInvoiceLists;
    private ArrayList<TaxRates> taxRateList;
    private ArrayList<String> taxRates;

    ArrayAdapter<String> taxAdapter;

    TaxRates selectedItem = null;
    private static final String TAG = TaxListDialog.class.getSimpleName();
    String invoiceQuotationId;

    double invoiceTaxTotal;
    double invoiceSubTotal;

    private FloatingActionButton addTaxItem;
    SmoothCheckBox chk_compound_tax;
    boolean isCompoundChecked = false;
    int noAfterDecimal = 2;

    /**
     * List of shared block.
     */
    public static String INVOICE_ID = "invoice_id";

    //--------------------------------INSTANCE...VARIABLES...STARTS--------------------------------------

    //---------------------------------PUBLIC...METHODS...STARTS----------------------------------------

    public static TaxListDialog getInstance(String invoiceQuotationId) {
        TaxListDialog dialog = new TaxListDialog();
        Bundle bundle = new Bundle();
        bundle.putString(INVOICE_ID, invoiceQuotationId);
        dialog.setArguments(bundle);
        return dialog;
    }

    //---------------------------------PUBLIC...METHODS...ENDS------------------------------------------

    //--------------------------------LIFECYCLE...METHODS...STARTS--------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_tax_item_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeView(view);

        dataAccessObject = DaoManager.getInstance();

        GestionAcces gestionAcces = dataAccessObject.getAcces();
        try {
            noAfterDecimal = gestionAcces.getNumDecimals();
        } catch (Exception e) {
            noAfterDecimal = 2;
        }

        txtCancel.setOnClickListener(clickListener);

        getArgumentData();

        setCancelable(false);

        invoiceTaxTotal = dataAccessObject.getTotalTaxInvoice(invoiceQuotationId);
        invoiceSubTotal = dataAccessObject.getSubTotalTaxInvoice(invoiceQuotationId);


        getListData();

        taxRateList = (ArrayList<TaxRates>) dataAccessObject.getTaxRates();

        updatingTaxList();

        setHintAndAdapterForSpinner(taxRateList, spinnerTax, getActivity().getResources().getString(R.string.txt_select_tax_label));


    }

    public void taxValueCalculation(boolean isCompound, double tax, String id_remote) {
        invoiceTaxTotal = dataAccessObject.getTotalTaxInvoice(invoiceQuotationId);
        invoiceSubTotal = dataAccessObject.getSubTotalTaxInvoice(invoiceQuotationId);

        double globalDiscount = updateInvoiceTotal();
        invoiceSubTotal = invoiceSubTotal - globalDiscount;

        double taxSubtotal = invoiceSubTotal;
        double totalTax = invoiceSubTotal + invoiceTaxTotal;
        double taxValue = (invoiceSubTotal * tax) / 100;
        if (isCompound) {
            taxSubtotal = taxSubtotal + invoiceTaxTotal;
        }
        taxValue = (taxSubtotal * tax) / 100;

        new UpdateGlobalTaxInvoiceOrQuotationItem(isCompound, taxValue, id_remote).execute();

    }

    private double updateInvoiceTotal() {

        double updatedTaxAmount = 0.0;
        double globalDiscountAmount = 0;
        boolean globalDiscountIsPercent = true;

        globalDiscountAmount = dataAccessObject.getGlobalDiscountValueInvoice(invoiceQuotationId);
        globalDiscountIsPercent = dataAccessObject.getGlobalDiscountOptionInvoice(invoiceQuotationId);

        Double totalWithDiscount = 0.0;

        Double maxVal = invoiceSubTotal;
        String discountVal = "" + globalDiscountAmount;

        if (maxVal != null && maxVal >= 0) {

            if (discountVal != null && discountVal.trim().length() > 0)
                if (globalDiscountIsPercent) {
                    if (Double.parseDouble(discountVal) > 100) {
                        discountVal = "100";
                    }
                } else {
                    if (Double.parseDouble(discountVal) > maxVal) {
                        discountVal = "0";
                    }
                }
        } else {
            if (!globalDiscountIsPercent) {
                discountVal = "0";
            }
        }

        Double discountValue = Double.parseDouble(discountVal);
        double totalWithoutDisc = 0.0;

        if (globalDiscountIsPercent) {
            updatedTaxAmount = (invoiceSubTotal * discountValue) / 100;
        } else {
            updatedTaxAmount = discountValue;
        }

        return updatedTaxAmount;
    }

    private void updatingTaxList() {
        for (int i = 0; i < taxRateList.size(); i++) {
            for (int j = 0; j < globalTaxInvoiceLists.size(); j++) {
                if (globalTaxInvoiceLists.get(j).getIdTaxRate().trim().equalsIgnoreCase(
                        taxRateList.get(i).getIdTaxRate())) {
                    taxRateList.remove(i);
                    break;
                }
            }
        }

        if (globalTaxInvoiceLists != null && globalTaxInvoiceLists.size() > 0)
            rvTaxList.setVisibility(View.VISIBLE);
        else
            rvTaxList.setVisibility(View.INVISIBLE);
    }

    private void getArgumentData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            invoiceQuotationId = getArguments().getString(INVOICE_ID);
        }
    }


    private void setHintAndAdapterForSpinner(List<TaxRates> userArray, Spinner spinner, String hintText) {
        //adapter for spinner with hint
        HintAdapter<TaxRates> hintAdapter = new HintAdapter<TaxRates>(getActivity(),
                R.layout.layout_spinner_row, hintText, userArray) {
            @Override
            protected View getCustomView(int position, View convertView, ViewGroup parent) {
                TaxRates item = getItem(position);

                View view = inflateLayout(parent, false);
                BigDecimal taxDecimal = new BigDecimal(item.getValTaxRate()).
                        setScale(noAfterDecimal, RoundingMode.HALF_UP);
                ((android.widget.TextView) view.findViewById(R.id.txtSpinner)).
                        setText(item.getValTaxName() + " (" + taxDecimal + "%)");
                return view;
            }
        };

        HintSpinner<TaxRates> hintSpinner = new HintSpinner<TaxRates>(spinner,
                hintAdapter,
                new HintSpinner.Callback<TaxRates>() {
                    @Override
                    public void onItemSelected(int position, TaxRates itemAtPosition) {
                        selectedItem = itemAtPosition;

                    }
                });

        hintSpinner.init();
    }

    private void initializeView(View view) {
        rvTaxList = (RecyclerView) view.findViewById(R.id.rv_tax_list);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        txtCancel = (TextView) view.findViewById(R.id.txtCancel);
        spinnerTax = (Spinner) view.findViewById(R.id.spinnerTaxItem);
        addTaxItem = (FloatingActionButton) view.findViewById(R.id.addTaxItem);
        chk_compound_tax = (SmoothCheckBox) view.findViewById(R.id.chk_compound_tax);


        chk_compound_tax.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                isCompoundChecked = isChecked;
            }
        });

        addTaxItem.setOnClickListener(this);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag).addToBackStack(null);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            com.synchroteam.utils.Logger.log("TAG", "IllegalStateException ----->" + e);
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(false);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        return dialog;
    }


    //--------------------------------LIFECYCLE...METHODS...STARTS--------------------------------------

    //--------------------------------LOCAL...METHODS...STARTS------------------------------------------

    /**
     * Add list of sorting options data to the array list.
     */
    private void getListData() {
        globalTaxInvoiceLists = new ArrayList<>();
        globalTaxInvoiceLists = dataAccessObject.getGlobalTaxInvoice(invoiceQuotationId);

        if (globalTaxInvoiceLists != null && globalTaxInvoiceLists.size() > 0) {
            setListAdapter();
        }
    }

    /**
     * Sets the adapter.
     */
    private void setListAdapter() {
        taxListAdapterRv = new TaxListAdapterRv(getActivity(),
                globalTaxInvoiceLists, "", this);
        rvTaxList.setLayoutManager(linearLayoutManager);
        rvTaxList.setAdapter(taxListAdapterRv);

    }


    private void sendSelectedList() {
//        SelectionListener listener = (SelectionListener) getTargetFragment();
//        listener.onSelectTaxItem();
        dismiss();
    }


    //--------------------------------LOCAL...METHODS...STARTS------------------------------------------

    //------------------------------------LISTENER...METHODS...STARTS-----------------------------------

    /**
     * Click listener
     */
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.txtCancel:
                    sendSelectedList();
                    break;
            }
        }
    };

    public void deleteGlobalTaxItem(String idRemote) {
        new DeleteGlobalTaxInvoiceOrQuotationItem(idRemote).execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addTaxItem:
                if (selectedItem != null) {
                    addGlobalTaxCalu(selectedItem, isCompoundChecked);
                    chk_compound_tax.setChecked(false);
                }
                break;
        }
    }

    private void addGlobalTaxCalu(TaxRates selectedItem, boolean isCompoundChecked) {
        invoiceTaxTotal = dataAccessObject.getTotalTaxInvoice(invoiceQuotationId);
        invoiceSubTotal = dataAccessObject.getSubTotalTaxInvoice(invoiceQuotationId);

        double taxRate = Double.parseDouble(selectedItem.getValTaxRate());

        double globalDiscount = updateInvoiceTotal();
        invoiceSubTotal = invoiceSubTotal - globalDiscount;

        double taxSubtotal = invoiceSubTotal;
        double totalTax = invoiceSubTotal + invoiceTaxTotal;

        double taxValue = (invoiceSubTotal * taxRate) / 100;
        if (this.isCompoundChecked) {
            taxSubtotal = taxSubtotal + invoiceTaxTotal;
        }
        taxValue = (taxSubtotal * taxRate) / 100;


        new AddGlobalTaxInvoiceOrQuotationItem(this.selectedItem.getIdTaxRate(), isCompoundChecked,
                Double.parseDouble(this.selectedItem.getValTaxRate()), taxValue, invoiceQuotationId).execute();
    }


    //------------------------------------LISTENER...METHODS...ENDS-------------------------------------


    //--------------------------------INTERFACE...STARTS------------------------------------------------

    public interface SelectionListener {
        void onSelectTaxItem();
    }
    //--------------------------------INTERFACE...ENDS--------------------------------------------------


    // .......................................ASYNC...CLASS...STARTS...HERE..............................................

    /**
     * Async class to update the previous values of invoice or quotation
     */
    private class UpdateInvoiceOrQuotation extends
            AsyncTaskCoroutine<String, Boolean> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressLint("SimpleDateFormat")
        @Override
        public Boolean doInBackground(String... params) {

            ArrayList<Invoice_Quotation_Items_Beans> invoiceQuotationList = dataAccessObject
                    .getInvoiceQuotationItemValues(invoiceQuotationId);

            ArrayList<GlobalTaxInvoiceList> globalTaxInvoiceLists = dataAccessObject.
                    getGlobalTaxInvoice(invoiceQuotationId);

            double totalWithDiscountRate = 0;
            double taxAmount = 0;
            double totalWithTaxAmt = 0;
            for (int i = 0; i < invoiceQuotationList.size(); i++) {
                double total = invoiceQuotationList.get(i).getTotal();
                double taxAmount1 = invoiceQuotationList.get(i).getTaxValue();
                double totWithTax = invoiceQuotationList.get(i)
                        .getTotalWIthTax();

                totalWithDiscountRate = totalWithDiscountRate + total;
                taxAmount = taxAmount + taxAmount1;
                totalWithTaxAmt = totalWithTaxAmt + totWithTax;
            }


            double globalTaxAmount = 0;
            for (int i = 0; i < globalTaxInvoiceLists.size(); i++) {
                double taxValueAmount = globalTaxInvoiceLists.get(i).getTaxValue();
                globalTaxAmount = globalTaxAmount + taxValueAmount;
            }
            taxAmount = taxAmount + globalTaxAmount;
            totalWithTaxAmt = totalWithTaxAmt + globalTaxAmount;


            boolean drp = dataAccessObject.updateInvoiceOrQuotation(
                    invoiceQuotationId, totalWithDiscountRate, taxAmount,
                    totalWithTaxAmt, false);
            return drp;
        }

        @Override
        public void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            DialogUtils.dismissProgressDialog();
            boolean drp = result.booleanValue();
            if (drp) {
                Toast.makeText(getActivity(),
                        getResources().getString(R.string.txt_ack_msg),
                        Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new UpdateInvoiceQuotationTaxEvent());

            } else
                Toast.makeText(getActivity(),
                        getResources().getString(R.string.msg_error),
                        Toast.LENGTH_SHORT).show();
        }
    }


    private class UpdateGlobalTaxInvoiceOrQuotationItem extends
            AsyncTaskCoroutine<String, Boolean> {

        boolean isCompound;
        double taxValue;
        String id_remote;

        public UpdateGlobalTaxInvoiceOrQuotationItem(boolean isCompound, double taxValue,
                                                     String id_remote) {
            this.isCompound = isCompound;
            this.taxValue = taxValue;
            this.id_remote = id_remote;
        }


        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        public Boolean doInBackground(String... params) {
            Logger.log(TAG, "UPDATED TOTAL CALCULATION VALUES ISCOMPOUND=====>" + isCompound);
            Logger.log(TAG, "UPDATED TOTAL CALCULATION VALUES TAX VALUE====>" + taxValue);
            boolean drp = dataAccessObject.updateGlobalTaxInvoice(isCompound, id_remote, taxValue);
            return drp;

        }

        @Override
        public void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            DialogUtils.dismissProgressDialog();
            boolean drp = result.booleanValue();
            if (drp) {
                new UpdateInvoiceOrQuotation().execute();
            } else
                Toast.makeText(getActivity(),
                        getResources().getString(R.string.msg_error),
                        Toast.LENGTH_SHORT).show();
        }
    }

    private class DeleteGlobalTaxInvoiceOrQuotationItem extends
            AsyncTaskCoroutine<String, Boolean> {

        boolean isCompound;
        double taxValue;
        String id_remote;

        public DeleteGlobalTaxInvoiceOrQuotationItem(String id_remote) {
            this.id_remote = id_remote;
        }


        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        public Boolean doInBackground(String... params) {


            boolean drp = dataAccessObject.deleteGlobalTaxInvoice(id_remote);
            return drp;

        }

        @Override
        public void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            DialogUtils.dismissProgressDialog();
            boolean drp = result.booleanValue();
            if (drp) {
                updateTaxDropDownList();
                new UpdateInvoiceOrQuotation().execute();
                if (taxListAdapterRv != null)
                    taxListAdapterRv.notifyDataSetChanged();
            } else
                Toast.makeText(getActivity(),
                        getResources().getString(R.string.msg_error),
                        Toast.LENGTH_SHORT).show();
        }
    }

    private class AddGlobalTaxInvoiceOrQuotationItem extends
            AsyncTaskCoroutine<String, Boolean> {

        String idTaxRate;
        boolean isCompoundChecked;
        double taxRate, taxValue;
        String invoiceQuotationId;

        public AddGlobalTaxInvoiceOrQuotationItem(String idTaxRate, boolean isCompoundChecked,
                                                  double taxRate, double taxValue,
                                                  String invoiceQuotationId) {
            this.idTaxRate = idTaxRate;
            this.isCompoundChecked = isCompoundChecked;
            this.taxRate = taxRate;
            this.taxValue = taxValue;
            this.invoiceQuotationId = invoiceQuotationId;
        }


        @Override
        public void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        public Boolean doInBackground(String... params) {


            boolean drp = dataAccessObject.addGlobalTaxInvoice(
                    idTaxRate, isCompoundChecked, taxRate, taxValue, invoiceQuotationId);

            return drp;

        }

        @Override
        public void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            DialogUtils.dismissProgressDialog();
            boolean drp = result.booleanValue();
            if (drp) {
                selectedItem = null;
                updateTaxDropDownList();
                new UpdateInvoiceOrQuotation().execute();
            } else
                Toast.makeText(getActivity(),
                        getResources().getString(R.string.msg_error),
                        Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTaxDropDownList() {
        getListData();
        taxRateList = (ArrayList<TaxRates>) dataAccessObject.getTaxRates();
        updatingTaxList();
        setHintAndAdapterForSpinner(taxRateList, spinnerTax,
                getActivity().getResources().getString(R.string.txt_select_tax_label));
    }

}
