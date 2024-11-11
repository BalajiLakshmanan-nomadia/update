package com.synchroteam.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;

import com.synchroteam.synchroteam.ActivityCustomStripeCard;
import com.synchroteam.utils.CreditCardUtils;
import com.synchroteam.TypefaceLibrary.Button;
import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DecimalDigitsInputFilter;

import java.util.Locale;

/**
 * Created by Trident.
 */

public class InvoiceQuotationPaynowConfirmDialog extends Dialog implements
        View.OnClickListener {

    Activity activity;
    private Fragment fragment;
    private Button btnCancel, btnConfirm;
    private TextView txtBalance, txtEditIcon;
    private EditText editTxtBalance;
    private boolean isEditTextClicked = true;
    private String currencyType;
    private double invoicePaymentBalanceAmount;
    final String decValidate = "[0-9]+([,.][0-9]{1,2})?";
    private Typeface tfFontAwsome, tfDefault;

    private String stripePublishableKey;
    String invoiceQuotationId;
    public InvoiceQuotationPaynowConfirmDialog(Activity activity, Fragment fragment,
                                               double invoicePaymentBalanceAmount, String currencyType,
                                               String stripePublishableKey,String invoiceQuotationId) {
        super(activity);
        this.activity = activity;
        this.fragment = fragment;
        this.currencyType = currencyType;
        this.invoicePaymentBalanceAmount = invoicePaymentBalanceAmount;
        this.stripePublishableKey = stripePublishableKey;
        this.invoiceQuotationId = invoiceQuotationId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_invoice_quotation_paynow_confirm);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        tfFontAwsome = Typeface.createFromAsset(activity.getAssets(),
                activity.getString(R.string.fontName_fontAwesome));
        tfDefault = Typeface.createFromAsset(activity.getAssets(),
                activity.getString(R.string.fontName_avenir));

        txtBalance = (TextView) findViewById(R.id.txtBalance);
        txtEditIcon = (TextView) findViewById(R.id.txtEditIcon);
        editTxtBalance = (EditText) findViewById(R.id.editTxtBalance);
        btnConfirm = (Button) findViewById(R.id.btn_confirm_pay);
        btnCancel = (Button) findViewById(R.id.btn_cancel_pay);
        ((TextView) findViewById(R.id.txt_credit_card)).setTypeface(tfFontAwsome);

        editTxtBalance.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(19, 2)});
        editTxtBalance.addTextChangedListener(amountToPayEdttxtWatcher);
        editTxtBalance.setVisibility(View.GONE);
        txtBalance.setVisibility(View.VISIBLE);

        btnConfirm.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        txtEditIcon.setOnClickListener(this);

        if (currencyType == null)
            currencyType = "";

        txtBalance.setText(toCurrencyFormat(invoicePaymentBalanceAmount) + " " + currencyType);
        doCompleteActionForTextButtonEdit(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm_pay:

                String textViewbalance = txtBalance.getText().toString();
                String amountToPay = textViewbalance.substring(0, textViewbalance.indexOf(" "));

                Intent intent = new Intent(activity, ActivityCustomStripeCard.class);
                intent.putExtra(CreditCardUtils.EXTRA_BALANCE_AMOUNT, amountToPay);
                intent.putExtra(CreditCardUtils.EXTRA_BALANCE_CURRENCY_TYPE, currencyType);
                intent.putExtra(CreditCardUtils.EXTRA_KEY_STRIPE_PUBLISHABLE_KEY, stripePublishableKey);
                intent.putExtra(CreditCardUtils.EXTRA_KEY_INVOICE_ID, invoiceQuotationId);
                fragment.startActivityForResult(intent, 2);


                dismiss();
                break;
            case R.id.btn_cancel_pay:
                dismiss();
                break;
            case R.id.txtEditIcon:
                if (isEditTextClicked) {

                    doCompleteActionForTextButtonEdit(true);

                    String balance = txtBalance.getText().toString();
                    try {
                        balance = balance.substring(0, balance.indexOf(" "));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    editTxtBalance.setText(balance.trim());
                    editTxtBalance.setSelection(editTxtBalance.getText().length());

                    txtBalance.setVisibility(View.GONE);
                    isEditTextClicked = false;
                    btnConfirm.setBackgroundColor(activity.getResources().getColor(R.color.confirm_button_disabled_clr));
                    btnConfirm.setEnabled(false);

                } else {

                    hideSoftKeyboard();

                    String balance = editTxtBalance.getText().toString().trim();
                    if (balance.length() > 0) {

                        try {

                            if (Float.parseFloat(balance) > 0 && Float.parseFloat(balance) <= invoicePaymentBalanceAmount) {

                                txtBalance.setVisibility(View.VISIBLE);
                                txtBalance.setText(balance + " " + currencyType);

                                doCompleteActionForTextButtonEdit(false);

                                editTxtBalance.setVisibility(View.GONE);
                                isEditTextClicked = true;
                                btnConfirm.setBackgroundColor(activity.getResources().getColor(R.color.pay_now_green));
                                btnConfirm.setEnabled(true);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
                break;

            default:
                break;
        }
    }


    TextWatcher amountToPayEdttxtWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            if (start == 18 && !editTxtBalance.getText().toString().contains(".")) {
                String data = editTxtBalance.getText().toString();
                editTxtBalance.setText(data.substring(0, 18));
                editTxtBalance.setSelection(18);
            }

            String balance = editTxtBalance.getText().toString().trim();
            if (!TextUtils.isEmpty(balance)) {
                try {
                    if (Float.parseFloat(balance) > 0 && Float.parseFloat(balance) <= invoicePaymentBalanceAmount && balance.matches(decValidate)) {
                        doValidationForTextButtonEdit(true);
                    } else {
                        doValidationForTextButtonEdit(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private String toCurrencyFormat(float amountf) {
        return String.format(Locale.US, "%.2f", amountf);
    }

    private String toCurrencyFormat(double amountf) {
        return String.format(Locale.US, "%.2f", amountf);
    }

    private void hideSoftKeyboard() {
        try {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getContext().getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doCompleteActionForTextButtonEdit(Boolean isActionDone) {
        if (isActionDone) {
            editTxtBalance.setVisibility(View.VISIBLE);
            txtEditIcon.setText(activity.getResources().getString(R.string.txt_ack_msg));
            txtEditIcon.setTypeface(tfDefault);
            txtEditIcon.setTextColor(activity.getResources().getColor(R.color.confirm_button_enabled_clr));
        } else {
            txtEditIcon.setText(activity.getResources().getString(R.string.fa_pencil));
            txtEditIcon.setTextColor(activity.getResources().getColor(R.color.red));
            txtEditIcon.setTypeface(tfFontAwsome);
        }
    }

    private void doValidationForTextButtonEdit(Boolean isValid) {
        if (isValid) {
            txtEditIcon.setText(activity.getResources().getString(R.string.txt_ack_msg));
            txtEditIcon.setTextColor(activity.getResources().getColor(R.color.confirm_button_enabled_clr));
            txtEditIcon.setEnabled(true);
        } else {
            txtEditIcon.setTextColor(activity.getResources().getColor(R.color.backgoundBorderColor));
            txtEditIcon.setEnabled(false);
        }
    }

}