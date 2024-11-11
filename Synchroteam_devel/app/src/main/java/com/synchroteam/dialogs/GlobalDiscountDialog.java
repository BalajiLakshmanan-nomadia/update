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
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.RadioButton;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.beans.GlobalTaxInvoiceList;
import com.synchroteam.dao.Dao;
import com.synchroteam.events.UpdateInvoiceQuotationTaxEvent;
import com.synchroteam.listadapters.TaxListAdapterRv;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DecimalInputFilter;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.Logger;

import java.util.ArrayList;
import java.util.Locale;

import de.greenrobot.event.EventBus;

/**
 * Dialog fragment for showing list of Tax items.<p></p>
 * Created by Trident
 */
public class GlobalDiscountDialog extends DialogFragment {

    //--------------------------------INSTANCE...VARIABLES...STARTS--------------------------------------

    private RecyclerView rvTaxList;
    private LinearLayoutManager linearLayoutManager;
    private TaxListAdapterRv taxListAdapterRv;


    private Dao dataAccessObject;
    TextView txtSave, txtCancel;
    RadioGroup radioGroupListValues;
    RadioButton radioBtn1;
    RadioButton radioBtn2;
    boolean isPercentage = true;
    /**
     * The gt.
     */
    private GestionAcces gt;
    int noAfterDecimal = 2;
    String formatDecimal = "%.2f";
    InputFilter filter;
    private EditText edtDiscount;

    private static final String TAG = GlobalDiscountDialog.class.getSimpleName();
    String invoiceQuotationId;

    double invoiceTotalAmount;
    double invoiceSubTotal;
    GlobalSelectionListener listener;

    private static GlobalDiscountListener globalDiscountListener;

    /**
     * The Interface EnterDialogInterface.
     */
    public interface GlobalDiscountListener {

        /**
         * Do on confirm click.
         */
        public void doOnConfirmClick(String disValue, boolean disOption, Double totalWithDiscount);

        /**
         * Do on cancel click.
         */
        public void doOnCancelClick();

    }

    /**
     * List of shared block.
     */
    public static String INVOICE_ID = "invoice_id";

    //--------------------------------INSTANCE...VARIABLES...STARTS--------------------------------------

    //---------------------------------PUBLIC...METHODS...STARTS----------------------------------------

    public static GlobalDiscountDialog getInstance(String invoiceQuotationId, GlobalDiscountListener listener) {
        GlobalDiscountDialog dialog = new GlobalDiscountDialog();
        Bundle bundle = new Bundle();
        bundle.putString(INVOICE_ID, invoiceQuotationId);
        dialog.setArguments(bundle);
        globalDiscountListener = listener;
        return dialog;
    }

    //---------------------------------PUBLIC...METHODS...ENDS------------------------------------------

    //--------------------------------LIFECYCLE...METHODS...STARTS--------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_global_discount, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeView(view);

        dataAccessObject = DaoManager.getInstance();

        gt = dataAccessObject.getAcces();

        try {
            noAfterDecimal = gt.getNumDecimals();
            formatDecimal = "%." + noAfterDecimal + "f";
        } catch (Exception e) {
            noAfterDecimal = 2;
            formatDecimal = "%." + noAfterDecimal + "f";
        }


        getArgumentData();

        setCancelable(false);

        invoiceTotalAmount = dataAccessObject.getTotalTaxValueInvoice(invoiceQuotationId);
        invoiceSubTotal = dataAccessObject.getSubTotalTaxInvoice(invoiceQuotationId);
        final double invoiceTaxTotalCheck = dataAccessObject.getTotalTaxInvoice(invoiceQuotationId); //new changes V53

        if (noAfterDecimal > 0) {
//            edtDiscount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(
//                    10, noAfterDecimal)});
            edtDiscount.setFilters(new InputFilter[]{new DecimalInputFilter(10, noAfterDecimal,Integer.MAX_VALUE)});
        }else{
            edtDiscount.setInputType(InputType.TYPE_CLASS_NUMBER );
        }

//        else {
//            edtDiscount.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(10)});
//        }
//
        edtDiscount.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                //V52 new changes
//                Double maxVal = invoiceTotalAmount;

                Double maxVal = invoiceSubTotal + invoiceTaxTotalCheck; //new changes V53
                String discountVal = edtDiscount.getText().toString();
                int disLength = 4;

                if (maxVal != null && maxVal >= 0) {

                    String maxCount = "" + maxVal;
                    disLength = maxCount.trim().length();

                    if (discountVal.isEmpty() || discountVal.trim().length() == 0) return;
                    else if (discountVal.startsWith(".")) {
                        discountVal = "0";
                        edtDiscount.setText(discountVal);
                        edtDiscount.setSelection(discountVal.length());
                    } else if (discountVal.startsWith(".")) {
                        discountVal = "0.";
                        edtDiscount.setText(discountVal);
                        edtDiscount.setSelection(discountVal.length());
                    }

                    if (isPercentage) {
                        if (Double.parseDouble(discountVal) > 100) {
                            edtDiscount.setText(s.subSequence(0, s.length() - 1));
                            edtDiscount.setSelection(edtDiscount.getText().length());
                        }
                    }
//                    else {
//                        if (Double.parseDouble(discountVal) >= maxVal ) {  //+1
//                            edtDiscount.setText(s.subSequence(0, s.length() - 1));
//                            edtDiscount.setSelection(edtDiscount.getText().length());
//                            edtDiscount.setText(s.toString());
//                        }
//                    }

                } else {
                    if (s.length() > 0) {
                        if (isPercentage) {
                            if (Double.parseDouble(discountVal) > 100) {
                                edtDiscount.setText(s.subSequence(0, s.length() - 1));
                                edtDiscount.setSelection(edtDiscount.getText().length());
                            }
                        } else {

                            edtDiscount.setText(s.subSequence(0, s.length() - 1));
                            edtDiscount
                                    .setSelection(edtDiscount.getText().length());
                        }
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        radioGroupListValues
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group,
                                                 int checkedId) {
                        String itemSelected = "";
                        if (checkedId == R.id.radioBtnOne) {
                            isPercentage = false;
                            if (!TextUtils.isEmpty(edtDiscount.getText().toString().trim())) {
                                updateDiscountValue(isPercentage);
                                calculateTotal();
                            }
                        } else if (checkedId == R.id.radioBtnTwo) {
                            isPercentage = true;
                            if (!TextUtils.isEmpty(edtDiscount.getText().toString().trim())) {
                                updateDiscountValue(isPercentage);
                                calculateTotal();
                            }
                        }

                    }

                });

    }

    private void updateDiscountValue(boolean isPercentage) {
        Double maxVal = invoiceTotalAmount;
        String discountVal = edtDiscount.getText().toString();

        if (maxVal != null && maxVal > 0) {

            if (discountVal != null && discountVal.trim().length() > 0)
                if (isPercentage) {
                    if (Double.parseDouble(discountVal) > 100) {
                        edtDiscount.setText("0");
                        edtDiscount
                                .setSelection(edtDiscount.getText().length());
                        edtDiscount.setText(String.format(Locale.US, formatDecimal, Double.parseDouble("0")));
                    } else {
                        edtDiscount.setText(String.format(Locale.US, formatDecimal, Double.parseDouble(discountVal)));
                    }

                } else {
                    if (Double.parseDouble(discountVal) > maxVal) {
                        edtDiscount.setText(discountVal);
                        edtDiscount.setSelection(edtDiscount.getText().length());
                        edtDiscount.setText(String.format(Locale.US, formatDecimal, Double.parseDouble(discountVal)));
                    } else {
                        edtDiscount.setText(String.format(Locale.US, formatDecimal, Double.parseDouble(discountVal)));
                    }
                }
        } else {
            if (!isPercentage) {
                edtDiscount.setText(discountVal);
                edtDiscount.setText(String.format(Locale.US, formatDecimal, Double.parseDouble(discountVal)));
            } else {
                if (Double.parseDouble(discountVal) > 100) {
                    edtDiscount.setText("0");
                    edtDiscount
                            .setSelection(edtDiscount.getText().length());
                    edtDiscount.setText(String.format(Locale.US, formatDecimal, Double.parseDouble("0")));
                } else {
                    edtDiscount.setText(String.format(Locale.US, formatDecimal, Double.parseDouble(discountVal)));
                }
            }
        }
    }


    private void getArgumentData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            invoiceQuotationId = getArguments().getString(INVOICE_ID);
        }

        isPercentage = dataAccessObject.getGlobalDiscountOptionInvoice(invoiceQuotationId);
        double disValue = dataAccessObject.getGlobalDiscountValueInvoice(invoiceQuotationId);

        edtDiscount.setText(String.format(Locale.US, formatDecimal, disValue));

//        edtDiscount.setText("" + disValue);


        if (isPercentage) {
            radioBtn2.setChecked(true);
        } else {
            radioBtn1.setChecked(true);
        }
    }


    private void initializeView(View view) {
        edtDiscount = (EditText) view.findViewById(R.id.edtInvoiceDiscount);
        radioGroupListValues = (RadioGroup) view.findViewById(R.id.discountOptions);
        radioBtn1 = (RadioButton) view.findViewById(R.id.radioBtnOne);
        radioBtn2 = (RadioButton) view.findViewById(R.id.radioBtnTwo);
        radioBtn1.setText(getString(R.string.txt_ext_amt_lbl) + " (-)");
        radioBtn2.setText(getString(R.string.txt_perc_lbl) + " (%)");
        txtSave = (com.synchroteam.TypefaceLibrary.TextView) view.findViewById(R.id.txtSave);
        txtCancel = (com.synchroteam.TypefaceLibrary.TextView) view.findViewById(R.id.txtCancel);

        txtSave.setOnClickListener(clickListener);
        txtCancel.setOnClickListener(clickListener);
        edtDiscount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {

                calculateTotal();

            }
        });
    }

    private void calculateTotal() {

    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag).addToBackStack(null);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            Logger.log("TAG", "IllegalStateException ----->" + e);
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


    private void sendSelectedList() {
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

                    globalDiscountListener.doOnCancelClick();
                    if (getActivity() != null) {
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    }
                    dismiss();
                    break;
                case R.id.txtSave:
                    double invoiceTotalAmount = dataAccessObject.getTotalTaxValueInvoice(invoiceQuotationId);
                    double invoiceSubTotal = dataAccessObject.getSubTotalTaxInvoice(invoiceQuotationId);
                    final double invoiceTaxTotalCheck = dataAccessObject.getTotalTaxInvoice(invoiceQuotationId); //new changes V53
                    Double maxVal = invoiceSubTotal + invoiceTaxTotalCheck;
                    if (edtDiscount.getText() != null && edtDiscount.getText().toString().trim().length() > 0) {
                        double enteredEdtDiscount = Double.parseDouble(edtDiscount.getText().toString());
                        if (enteredEdtDiscount > 0 && enteredEdtDiscount > maxVal && !isPercentage) {
                            Logger.log(TAG, "Exact amount is greater than the total invoice " + invoiceTotalAmount);
                        } else {
                            new UpdateInvoiceOrQuotation().execute();
                        }
                    }
                    break;
            }
        }
    };


    //------------------------------------LISTENER...METHODS...ENDS-------------------------------------


    //--------------------------------INTERFACE...STARTS------------------------------------------------

    public interface GlobalSelectionListener {
        void onSaveGlobalDiscountItem(String discountValue, boolean discountOption);
    }
    //--------------------------------INTERFACE...ENDS--------------------------------------------------


    // .......................................ASYNC...CLASS...STARTS...HERE..............................................

    /**
     * Async class to update the previous values of invoice or quotation
     */
    @SuppressLint("StaticFieldLeak")
    private class UpdateInvoiceOrQuotation extends
            AsyncTaskCoroutine<String, Boolean> {

        Double discAmt = 0.0;

        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressLint("SimpleDateFormat")
        @Override
        public Boolean doInBackground(String... params) {

            Double totalWithDiscount = 0.0;

            @SuppressLint("WrongThread") Double discountValue = Double.parseDouble(edtDiscount.getText().toString());

            double invoiceTaxTotal = dataAccessObject.getTotalTaxInvoice(invoiceQuotationId);
            double invoiceSubTotal = dataAccessObject.getSubTotalTaxInvoice(invoiceQuotationId);

            invoiceTotalAmount = invoiceSubTotal + invoiceTaxTotal;
//            invoiceTotalAmount = invoiceSubTotal;

            Logger.log(TAG, "GLOBAL DISCOUNT subtotal & Tax ===> " + invoiceTotalAmount);
            if (isPercentage) {
                totalWithDiscount = invoiceTotalAmount - (invoiceTotalAmount * discountValue) / 100;
                discAmt = (invoiceTotalAmount * discountValue) / 100;
            } else {
                totalWithDiscount = invoiceTotalAmount - discountValue;
                discAmt = discountValue;
            }

            Logger.log(TAG, "GLOBAL DISCOUNT global disc amt ===> " + discAmt);
//            invoiceTotalAmount = invoiceTotalAmount - totalWithDiscount;

            updateGlobalList(discAmt);


            boolean drp = dataAccessObject.updateInvoiceOrQuotationGlobalDisc(
                    invoiceQuotationId, discountValue, totalWithDiscount,
                    isPercentage);

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

                globalDiscountListener.doOnConfirmClick(edtDiscount.getText().toString(), isPercentage, discAmt);
                if (getActivity() != null) {
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }
                dismiss();

            } else
                Toast.makeText(getActivity(),
                        getResources().getString(R.string.msg_error),
                        Toast.LENGTH_SHORT).show();
        }
    }

    private void updateGlobalList(Double discAmt) {


        double globalDiscount = discAmt;

        double invoiceTaxTotal = dataAccessObject.getTotalTaxInvoice(invoiceQuotationId);
        double invoiceSubTotal = dataAccessObject.getSubTotalTaxInvoice(invoiceQuotationId);
        Logger.log(TAG, "GLOBAL DISCOUNT  invoiceSubTotal value ===> " + invoiceSubTotal);
        Logger.log(TAG, "GLOBAL DISCOUNT  invoiceTaxTotal value ===> " + invoiceTaxTotal);

        ArrayList<GlobalTaxInvoiceList> globalTaxInvoiceLists = new ArrayList<>();
        globalTaxInvoiceLists = dataAccessObject.getGlobalTaxInvoice(invoiceQuotationId);

        double globalTaxAmount = 0;
        if (globalTaxInvoiceLists != null && globalTaxInvoiceLists.size() > 0) {
            for (int i = 0; i < globalTaxInvoiceLists.size(); i++) {
                double taxSubtotal = invoiceSubTotal - globalDiscount;

                double totalTax = invoiceSubTotal + invoiceTaxTotal;
                double taxValue = (invoiceSubTotal * globalTaxInvoiceLists.get(i).getTax()) / 100;
                if (globalTaxInvoiceLists.get(i).isHasCompound()) {
                    taxSubtotal = taxSubtotal + invoiceTaxTotal;
                }
                taxValue = (taxSubtotal * globalTaxInvoiceLists.get(i).getTax()) / 100;

                globalTaxAmount = globalTaxAmount + taxValue;

                Logger.log(TAG, "GLOBAL DISCOUNT global tax value ===> " + taxValue);

                dataAccessObject.updateGlobalTaxInvoice(globalTaxInvoiceLists.get(i).isHasCompound(),
                        globalTaxInvoiceLists.get(i).getIdRemote(), taxValue);

            }
        }

        invoiceTaxTotal = invoiceTaxTotal + globalTaxAmount;
        double totalWithTaxAmt = invoiceTaxTotal + invoiceSubTotal;

        dataAccessObject.updateInvoiceOrQuotation(
                invoiceQuotationId, invoiceSubTotal, invoiceTaxTotal,
                totalWithTaxAmt, false);
    }


}
