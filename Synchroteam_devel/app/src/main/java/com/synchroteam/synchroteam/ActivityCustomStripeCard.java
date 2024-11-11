package com.synchroteam.synchroteam;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;

import com.stripe.android.model.Card;
import com.stripe.android.model.ConfirmPaymentIntentParams;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputListener;
import com.stripe.android.view.CardInputWidget;
import com.synchroteam.TypefaceLibrary.Button;
import com.synchroteam.beans.CConectionsBeans;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.InvoicePaymentTransactionResultDialog;
import com.synchroteam.retrofit.ApiInterface;
import com.synchroteam.retrofit.Apiclient;
import com.synchroteam.retrofit.models.paymentservice.PaymentServiceModel;
import com.synchroteam.retrofit.models.paymentservice.PaymentServiceModelBefore;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.CreditCardUtils;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.SharedPref;
import com.synchroteam.utils.SynchroteamUitls;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.synchroteam.utils.CreditCardUtils.EXTRA_BALANCE_AMOUNT;
import static com.synchroteam.utils.CreditCardUtils.EXTRA_BALANCE_CURRENCY_TYPE;
import static com.synchroteam.utils.CreditCardUtils.EXTRA_KEY_INVOICE_ID;
import static com.synchroteam.utils.CreditCardUtils.EXTRA_KEY_STRIPE_PUBLISHABLE_KEY;
import static com.synchroteam.utils.CreditCardUtils.EXTRA_KEY_STRIPE_TOKEN;
import static com.synchroteam.utils.CreditCardUtils.EXTRA_PAYMENT_INTENT_ID;
import static com.synchroteam.utils.CreditCardUtils.EXTRA_PAYMENT_RESULT_VALUE;
import static com.synchroteam.utils.CreditCardUtils.EXTRA_PAYMENT_SUCCESS;


/**
 * Created by Trident on 2/6/17.
 */

public class ActivityCustomStripeCard extends AppCompatActivity {

    // code by trident solutions
    private String amountToPay;
    private String currencyType;
    private String stripePublishableKey;

    private EditText edtCardHolderName, edtCardHolderEmail;
    private Button btnPayNow;
    String invoiceQuotationId;
    private ArrayList<CConectionsBeans> cConectionsBeansArrayList;
    private Dao dao;

    // stripe input widget
    private CardInputWidget mCardInputWidget;
    Stripe stripe;
    ProgressDialog progressBar;

    String clientSecret;
    String paymentIntentId;
    String apiKeyPublic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_stripe_card);

        Bundle args = savedInstanceState != null ? savedInstanceState : getIntent().getExtras();
        checkParams(args);

        dao = DaoManager.getInstance();
        initializeUI();
    }

    @Override
    protected void onResume() {
        super.onResume();

        setKeyboardVisibility(true);
    }

    private void checkParams(Bundle bundle) {
        if (bundle == null) {
            return;
        }

        amountToPay = bundle.getString(EXTRA_BALANCE_AMOUNT);
        currencyType = bundle.getString(EXTRA_BALANCE_CURRENCY_TYPE);
        stripePublishableKey = bundle.getString(EXTRA_KEY_STRIPE_PUBLISHABLE_KEY);
        invoiceQuotationId = bundle.getString(EXTRA_KEY_INVOICE_ID);

        if (currencyType == null)
            currencyType = "";
    }


    public void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
        checkParams(inState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_BALANCE_AMOUNT, amountToPay);
        outState.putString(EXTRA_BALANCE_CURRENCY_TYPE, currencyType);
        outState.putString(EXTRA_KEY_STRIPE_PUBLISHABLE_KEY, stripePublishableKey);
        outState.putString(EXTRA_KEY_INVOICE_ID, stripePublishableKey);
    }

    private void initializeUI() {
        Typeface typeface = Typeface.createFromAsset(getAssets(),
                "fonts/12_AVENIR_45_BOOK__.TTF");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        com.synchroteam.TypefaceLibrary.TextView txtPaymentAmount = (com.synchroteam.TypefaceLibrary.TextView) findViewById(R.id.payment_amount);

        ((TextView) findViewById(R.id.btn_pay_now)).setTypeface(typeface);
        ImageView back = (ImageView) toolbar.findViewById(R.id.back);
        back.setOnClickListener(FinishActivityListener);

        txtPaymentAmount.setText(currencyType + " " + toCurrencyFormat(Float.parseFloat(amountToPay)));

        mCardInputWidget = (CardInputWidget) findViewById(R.id.card_input_widget);
        edtCardHolderName = (EditText) findViewById(R.id.edtCardHolderName);
        edtCardHolderEmail = (EditText) findViewById(R.id.edtCardHolderEmail);
        btnPayNow = (Button) findViewById(R.id.btn_pay_now);

        edtCardHolderName.addTextChangedListener(textWatcher);
        edtCardHolderEmail.addTextChangedListener(textWatcher);

        cConectionsBeansArrayList = dao.getDataFromCConnection();

        mCardInputWidget.setPostalCodeEnabled(false);

        setButtonEnable(false);

        mCardInputWidget.setCardInputListener(new CardInputListener() {
            @Override
            public void onFocusChange(@NotNull FocusField focusField) {
                validateCardDetails();
            }

            @Override
            public void onCardComplete() {
                validateCardDetails();
            }

            @Override
            public void onExpirationComplete() {
                validateCardDetails();
            }

            @Override
            public void onCvcComplete() {
                validateCardDetails();
            }
        });

//        mCardInputWidget.setCardInputListener(new CardInputListener() {
//            @Override
//            public void onFocusChange(String focusField) {
//                validateCardDetails();
//            }
//
//            @Override
//            public void onCardComplete() {
//                validateCardDetails();
//            }
//
//            @Override
//            public void onExpirationComplete() {
//                validateCardDetails();
//            }
//
//            @Override
//            public void onCvcComplete() {
//                validateCardDetails();
//            }
//        });

        btnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideSoftKeyboard(ActivityCustomStripeCard.this);

                Card card = mCardInputWidget.getCard();
                if (card != null && edtCardHolderName.getText().toString().length() > 0 && edtCardHolderEmail.getText().toString().length() > 0) {
                    if (validateEmail(edtCardHolderEmail.getText().toString())) {

//                        card.setName(edtCardHolderName.getText().toString());
                        card.toBuilder().name(edtCardHolderName.getText().toString());
                        onDonePayment(card);

                    } else {
                        Toast.makeText(ActivityCustomStripeCard.this, getString(R.string.enter_valid_email), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ActivityCustomStripeCard.this, getString(R.string.text_enter_all_values), Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void setButtonEnable(Boolean toEnable) {
        if (toEnable) {
            btnPayNow.setBackgroundColor(getResources().getColor(R.color.pay_now_bg));
            btnPayNow.setEnabled(true);
        } else {
            btnPayNow.setBackgroundColor(getResources().getColor(R.color.backgoundBorderColor));
            btnPayNow.setEnabled(false);
        }
    }

    private void onDonePayment(Card card) {

        hideSoftKeyboard(this);

        if (SynchroteamUitls.isNetworkAvailable(this)) {
            if (card.validateCard()) {
                try {

                    int idCustomer = 0;
                    String accessToken = null;
                    try {
                        JSONObject jsonObject = new JSONObject(cConectionsBeansArrayList.get(0).getJsonAuth());
                        idCustomer = jsonObject.getInt("IdCustomer");

                        JSONObject stripeAuthObject = new JSONObject(String.valueOf(jsonObject.getJSONObject("stripeOAuthToken")));
                        accessToken = stripeAuthObject.getString("StripeUserId");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String currencyCode = dao.getDeviseCustomerCurrencyCode();
                    int amountInCents = (int) (Float.parseFloat(amountToPay) * 100);
                    hitServiceBeforePayment(accessToken, idCustomer, amountInCents, currencyCode, "", edtCardHolderEmail.getText().toString()
                            , null, invoiceQuotationId, "true", card);
//
                } catch (Exception e) {
                    e.printStackTrace();
                    finish();
                }
            } else {
                // Do not continue token creation.
                Toast.makeText(this, getString(R.string.enter_valid_card), Toast.LENGTH_SHORT).show();
            }
        } else {
            SynchroteamUitls.showToastMessage(this);
        }

    }


    private void hitServiceBeforePayment(String token, int idCustomer, final int amountInCents,
                                         final String currency, String ccToken, String ccEmail, String idInvoive,
                                         final String idRemoteInvoice, String addPayment, Card card) {
        final ProgressDialog progressDialog = ProgressDialog.show(this,
                getString(R.string.textPleaseWaitLable),
                getString(R.string.chargement), true, false);

        ApiInterface apiService =
                Apiclient.getClient().create(ApiInterface.class);

        String url = SharedPref.getUrlStripeServer(this);


        Call<PaymentServiceModelBefore> call = apiService.paymentServiceBefore(url, token, idCustomer, amountInCents,
                currency, ccToken, ccEmail, idInvoive, idRemoteInvoice, addPayment);

        call.enqueue(new Callback<PaymentServiceModelBefore>() {
            @Override
            public void onResponse(Call<PaymentServiceModelBefore> call, Response<PaymentServiceModelBefore> response) {

                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();

                String status = response.body().getStatus();
                String msg = response.body().getMsg();

                if (status.trim().equals("1")) {
                    clientSecret = response.body().getClientSecret();
                    paymentIntentId = response.body().getPaymentIntentId();
                    apiKeyPublic = response.body().getApiKeyPublic();

                    Logger.log("TAG", "paymentIntentId msg values is ====>" + paymentIntentId);
                    Logger.log("TAG", "paymentIntentId clientSecret values is ====>" + paymentIntentId);
                    Logger.log("TAG", "paymentIntentId apiKeyPublic values is ====>" + apiKeyPublic);

                    try {
                        checkingCardWithStripe(getApplicationContext(), card, apiKeyPublic, clientSecret);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    finish();
                }
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<PaymentServiceModelBefore> call, Throwable t) {
                Logger.log("Payment", "Retrofit failure :" + t);

                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();

            }

        });

    }


    private void checkingCardWithStripe(Context mContext, Card card, String apiKeyPublic, String clientSecret) throws Exception {

        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);//you can cancel it by pressing back button
        progressBar.setMessage(getString(R.string.textPleaseWaitLable));
        progressBar.show();

        stripe = new Stripe(mContext, apiKeyPublic);

        PaymentMethodCreateParams params = mCardInputWidget.getPaymentMethodCreateParams();
        if (params != null) {
            ConfirmPaymentIntentParams confirmParams = ConfirmPaymentIntentParams
                    .createWithPaymentMethodCreateParams(params, clientSecret);
            stripe.confirmPayment(this, confirmParams);
        }

//        stripe.createToken(
//                card,
//                new TokenCallback() {
//                    public void onSuccess(Token token) {
//
//                        progressBar.hide();
//
//                        // Send token to your server
//                        Intent intent = new Intent();
//                        intent.putExtra(EXTRA_KEY_STRIPE_TOKEN, token.getId());
//                        intent.putExtra(EXTRA_BALANCE_AMOUNT, amountToPay);
//                        intent.putExtra(CreditCardUtils.EXTRA_CC_EMAIL, edtCardHolderEmail.getText().toString());
//
//                        setResult(RESULT_OK, intent);
//                        finish();
//                    }
//
//                    public void onError(Exception error) {
//
//                        progressBar.hide();
//
//                        // Show localized error message
//                        Toast.makeText(getApplicationContext(),
//                                error.getLocalizedMessage(),
//                                Toast.LENGTH_LONG
//                        ).show();
//                    }
//                }
//        );

    }


    // from the link above
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks whether a hardware keyboard is available
        if (newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {

            RelativeLayout parent = (RelativeLayout) findViewById(R.id.parent);
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) parent.getLayoutParams();
            layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, 0);
            parent.setLayoutParams(layoutParams);

        }
    }

    private void setKeyboardVisibility(boolean visible) {
        if (!visible) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(edtCardHolderEmail.getWindowToken(), 0);
        } else {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    @Override
    public void onBackPressed() {
        this.finish();
    }

    View.OnClickListener FinishActivityListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };


    // hides keyboard when the user touches outside editText
    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null)
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static boolean validateEmail(String emailStr) {
        final Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    private String toCurrencyFormat(float amountf) {
        return String.format(Locale.US, "%.2f", amountf);
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            validateCardDetails();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private void validateCardDetails() {
        Card card = mCardInputWidget.getCard();
        if (card != null && edtCardHolderName.getText().length() > 0 && edtCardHolderEmail.getText().length() > 0) {
            setButtonEnable(true);
        } else {
            setButtonEnable(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result of stripe.confirmPayment
        stripe.onPaymentResult(requestCode, data, new ApiResultCallback<PaymentIntentResult>() {
            @Override
            public void onSuccess(@NotNull PaymentIntentResult paymentIntentResult) {
                if (progressBar != null && progressBar.isShowing())
                    progressBar.dismiss();

                Logger.log("TAG", "paymentIntentId PAYMENT SUCCESS ");
                Intent intent = new Intent();
                intent.putExtra(EXTRA_KEY_STRIPE_TOKEN, "");
                intent.putExtra(EXTRA_BALANCE_AMOUNT, amountToPay);
                intent.putExtra(CreditCardUtils.EXTRA_CC_EMAIL, edtCardHolderEmail.getText().toString());
                intent.putExtra(EXTRA_PAYMENT_INTENT_ID, paymentIntentId);

                if (paymentIntentResult.getIntent().getStatus().name() != null &&
                        paymentIntentResult.getIntent().getStatus().name().equalsIgnoreCase("succeeded")) {
                    intent.putExtra(EXTRA_PAYMENT_SUCCESS, true);
                    intent.putExtra(EXTRA_PAYMENT_RESULT_VALUE, paymentIntentResult.getIntent().getStatus().name());
                    setResult(RESULT_OK, intent);
                    finish();
                }else{
                    intent.putExtra(EXTRA_PAYMENT_SUCCESS, false);
                    intent.putExtra(EXTRA_PAYMENT_RESULT_VALUE, paymentIntentResult.getFailureMessage());
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }



            @Override
            public void onError(@NotNull Exception e) {
                if (progressBar != null && progressBar.isShowing())
                    progressBar.dismiss();

                Logger.log("TAG", "paymentIntentId PAYMENT FAILURE ");
                Intent intent = new Intent();

                intent.putExtra(EXTRA_PAYMENT_RESULT_VALUE, e.getMessage());
                intent.putExtra(EXTRA_PAYMENT_SUCCESS, false);
                setResult(RESULT_OK, intent);
                finish();

//                Toast.makeText(ActivityCustomStripeCard.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

}