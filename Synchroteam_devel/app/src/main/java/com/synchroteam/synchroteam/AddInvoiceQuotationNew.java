package com.synchroteam.synchroteam;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.RadioButton;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.beans.Invoice_Quotation_Beans;
import com.synchroteam.beans.Invoice_Quotation_Items_Beans;
import com.synchroteam.beans.TaxRates;
import com.synchroteam.beans.UpdateUiOnSync;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.EnterDescriptionDialog;
import com.synchroteam.events.AddInvoiceQuotationEvent;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.CommonUtils;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DateChecker;
import com.synchroteam.utils.DecimalDigitsInputFilter;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.RequestCode;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.regex.Pattern;

import de.greenrobot.event.EventBus;

/**
 * This class used to add a new invoice/quotation and update into DB
 *
 * @author Trident
 */
public class AddInvoiceQuotationNew extends AppCompatActivity implements
        OnFocusChangeListener, OnClickListener, CommonInterface {

    // .................................INSTANCE...VARIABLE...STARTS...HERE..........................................
    // private ArrayList<String> mOriginalValues, mOriginalValuesDesc;
    private ActionBar actionBar;
    private EditText edtUnitPrice, edtQty, edtDiscount;
    private TextView txtTotal;
    private LinearLayout linItemContainer;
//    private TextView txtNavigate;

    private Spinner spinnerTax;
    private com.synchroteam.TypefaceLibrary.TextView txtCategroy, txtReference,
            txtName, txtAdd, txtCancel, txtLblRefCat;
    private Dao dataAccessObject;
    private Invoice_Quotation_Beans invoiceQuotation;
    private String idInterv;
    private int flag;
    private Integer idSite, idClient;
    private double unitPrice, qty, discount, total, tax, taxValue, totalWithTax,
            totalWithDiscount;
    private String invoiceQuotationId;
    // private ArrayList<InvoicingCatalogCategoryBeans> catList;
    // private ArrayList<InvoicingCatalogSubCategoryBeans> subCatList;
    // private ArrayList<String> descriptionList, priceValues, itemNames,
    // taxRates, idTaxRates;
    private ArrayList<TaxRates> taxRateList;
    private ArrayList<String> taxRates;
    private static final String zeroTotal = "0.00";
    private int order;
    ArrayAdapter<String> taxAdapter;
    private String category;
    private String currencyType;


    RadioGroup radioGroupListValues;
    RadioButton radioBtn1;
    RadioButton radioBtn2;
    LinearLayout linReferenceContainer;
    boolean isPercentage = true;
    public String srtReference;
    public String descripItem;
    TextView txtExpand, txtExpandPart;
    /**
     * The gt.
     */
    private GestionAcces gt;
    int noAfterDecimal = 2;
    String formatDecimal = "%.2f";
    InputFilter filter;
    private boolean flInvoiceStrict;
//	private static final String TAG = AddInvoiceQuotation.class.getName();

    // *********************************INSTANCE...VARIABLE...ENDS...HERE********************************************

    // ................................LIFECYCLE...METHOD...STARTS...HERE............................................
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_activity_add_invoice_quotation_new);
        initiateView();
        txtLblRefCat.setText(getString(R.string.txt_cat_label) + " & " + getString(R.string.txt_ref_label));

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //New changes
        DateChecker.checkDateAndNavigate(this, dataAccessObject);

        ((SyncroTeamApplication) getApplicationContext())
                .setSyncroteamAppLive(true);
        ((SyncroTeamApplication) getApplicationContext())
                .setSyncroteamActivityInstance(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ((SyncroTeamApplication) getApplicationContext())
                .setSyncroteamAppLive(false);
        ((SyncroTeamApplication) getApplicationContext())
                .setSyncroteamActivityInstance(null);

    }

    @Override
    protected void onDestroy() {
        DialogUtils.dismissProgressDialog();
        super.onDestroy();
    }

    // *********************************LIFECYCLE...METHOD...ENDS...HERE*********************************************

    // ..................................OVERRIDDEN...METHODS...STARTS...HERE.........................................

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RequestCode.REQUEST_CODE_OPEN_ITEM) {
            if (resultCode == Activity.RESULT_OK) {
                category = data
                        .getStringExtra(KEYS.InventoryListValues.CATEGORY_NAME);
                String replacedText = category.replace("|", " > ");

                //v51 code
//                txtCategroy.setText(replacedText);
//                txtName.setText(data
//                        .getStringExtra(KEYS.InventoryListValues.PARTS_SERVICES_NAME));
//                txtReference
//                        .setText(data
//                                .getStringExtra(KEYS.InventoryListValues.REFERENCE_NAME));

                //v52 Updates
                String descriptionItem = data
                        .getStringExtra("description_item");
                descripItem = descriptionItem;

                txtName.setText(data
                        .getStringExtra(KEYS.InventoryListValues.PARTS_SERVICES_NAME));
                txtName.post(new Runnable() {

                    @Override
                    public void run() {
                        if (txtName.getLineCount() > 1) {
                            txtExpandPart.setVisibility(View.VISIBLE);
                            txtName.setSingleLine(true);
                            txtName.setMaxLines(1);
                        } else {
                            txtExpandPart.setVisibility(View.GONE);
                            txtName.setMaxLines(Integer.MAX_VALUE);
                        }
                    }
                });

                if (descriptionItem != null && descriptionItem.length() > 0) {
                    txtReference.setText(descriptionItem);

                    txtReference.setTypeface(txtReference.getTypeface(), Typeface.ITALIC);
                    linReferenceContainer.setVisibility(View.VISIBLE);


                    txtReference.post(new Runnable() {

                        @Override
                        public void run() {
                            if (txtReference.getLineCount() > 1) {
                                txtExpand.setVisibility(View.VISIBLE);
                                txtReference.setSingleLine(true);
                            } else {
                                txtExpand.setVisibility(View.GONE);
                            }
                        }
                    });

                } else {
//                    linReferenceContainer.setVisibility(View.GONE);

                    txtReference.setHint(getString(R.string.enterDescription));

                    txtReference.setTypeface(txtReference.getTypeface(), Typeface.ITALIC);
                    linReferenceContainer.setVisibility(View.VISIBLE);

                    Handler handler = new Handler();
                    txtReference.post(new Runnable() {

                        @Override
                        public void run() {
                            if (txtReference.getLineCount() > 1) {
                                txtExpand.setVisibility(View.VISIBLE);
                                txtReference.setSingleLine(true);
                                txtReference.setMaxLines(1);
                            } else {
                                txtExpand.setVisibility(View.GONE);
                            }
                        }
                    });
                }

                String ref = data
                        .getStringExtra(KEYS.InventoryListValues.REFERENCE_NAME);

                if (ref != null && ref.trim().length() > 0)
                    txtCategroy.setText(replacedText + " : " + ref);
                else
                    txtCategroy.setText(replacedText);

                srtReference = ref;

                BigDecimal bdPriceValue = new BigDecimal(Double.parseDouble(data.getStringExtra(KEYS.InventoryListValues.PRICE_VALUE))).
                        setScale(noAfterDecimal, RoundingMode.HALF_UP);
                edtUnitPrice.setText(bdPriceValue.toPlainString());

                edtUnitPrice.setSelection(edtUnitPrice.getText().toString()
                        .length());

//                int spinnerPosition = taxAdapter.getPosition(data
//                        .getStringExtra(KEYS.InventoryListValues.TAX_RATE)
//                        + "%");

                //check for the default tax selection
//                String[] tax_ref = data.getStringArrayExtra(KEYS.InventoryListValues.TAX_RATE);
//                if (tax_ref != null) {
//                    BigDecimal taxDefVal = new BigDecimal(Arrays.toString(tax_ref)).
//                            setScale(noAfterDecimal, RoundingMode.HALF_UP);
//                    int spinnerPosition = taxAdapter.getPosition(taxDefVal.toPlainString().trim() + "%");
//
//                    spinnerTax.setSelection(spinnerPosition);
//
//                }

                String tax_ref = data.getStringExtra(KEYS.InventoryListValues.TAX_RATE);

                if (tax_ref != null) {
                    BigDecimal taxDefVal = new BigDecimal(tax_ref).
                            setScale(noAfterDecimal, RoundingMode.HALF_UP);
                    int spinnerPosition = taxAdapter.getPosition(taxDefVal + "%");

                    spinnerTax.setSelection(spinnerPosition);

                }
            }
        }
    }

    @Override
    public Activity getActivityInstance() {
        return this;
    }

    @Override
    public void updateUi() {
        EventBus.getDefault().post(new UpdateUiOnSync());
    }

    @Override
    public void updateUiOnTrakingStatusChange(boolean isRunning) {

    }

    // **********************************OVERRIDDEN...METHODS...ENDS...HERE*******************************************

    // ...................................LOCAL...METHOD...STARTS...HERE..............................................
    private void initiateView() {
        actionBar = getSupportActionBar();
        getVauesForDB();

        String mTitle = getResources().getString(R.string.textAddItem);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(mTitle.toUpperCase());
        actionBar.setHomeButtonEnabled(true);

        linItemContainer = (LinearLayout) findViewById(R.id.linItemContainer);
//        txtNavigate = (TextView) findViewById(R.id.txt_navigate);
        txtTotal = (com.synchroteam.TypefaceLibrary.TextView) findViewById(R.id.txtTotalValue);

        edtUnitPrice = (EditText) findViewById(R.id.edtInvoiceUnitPrice);
        edtQty = (EditText) findViewById(R.id.edtInvoiceQty);
        edtDiscount = (EditText) findViewById(R.id.edtInvoiceDiscount);
        // txtDescription = (AutoCompleteTextView)
        // findViewById(R.id.edtInvoiceDescription);
        spinnerTax = (Spinner) findViewById(R.id.edtInvoiceTax);
        txtExpand = (TextView) findViewById(R.id.txtExpand);
        txtExpandPart = (TextView) findViewById(R.id.txtExpandPart);
        Typeface typeface = Typeface.createFromAsset(
                getAssets(), getString(R.string.fontName_fontAwesome));

        txtExpand.setTypeface(typeface);
        txtExpandPart.setTypeface(typeface);

        txtCategroy = (com.synchroteam.TypefaceLibrary.TextView) findViewById(R.id.txt_category);
        txtReference = (com.synchroteam.TypefaceLibrary.TextView) findViewById(R.id.txt_reference);
        txtName = (com.synchroteam.TypefaceLibrary.TextView) findViewById(R.id.txt_parts_service);

        txtAdd = (com.synchroteam.TypefaceLibrary.TextView) findViewById(R.id.txtAdd);
        txtCancel = (com.synchroteam.TypefaceLibrary.TextView) findViewById(R.id.txtCancel);

        radioGroupListValues = (RadioGroup) findViewById(R.id.discountOptions);
        radioBtn1 = (RadioButton) findViewById(R.id.radioBtnOne);
        radioBtn2 = (RadioButton) findViewById(R.id.radioBtnTwo);
        radioBtn1.setText(getString(R.string.txt_ext_amt_lbl) + " (-)");
        radioBtn2.setText(getString(R.string.txt_perc_lbl) + " (%)");

        linReferenceContainer = (LinearLayout) findViewById(R.id.linReferenceContainer);

        txtLblRefCat = (com.synchroteam.TypefaceLibrary.TextView) findViewById(R.id.txtLblRefCat);

        com.synchroteam.TypefaceLibrary.TextView txtTotalLabel = (com.synchroteam.TypefaceLibrary.TextView) findViewById(R.id.txtTotalLabel);
        txtTotalLabel.setText(getString(R.string.txt_total_label) + " : ");

        dataAccessObject = DaoManager.getInstance();

        idClient = dataAccessObject.hasClientID(idInterv);
        gt = dataAccessObject.getAcces();
        flInvoiceStrict=gt.isFlInvoiceStrict();

        try {
            noAfterDecimal = gt.getNumDecimals();
            formatDecimal = "%." + noAfterDecimal + "f";
        } catch (Exception e) {
            noAfterDecimal = 2;
            formatDecimal = "%." + noAfterDecimal + "f";
        }

        //new changes
//        invoiceQuotation = dataAccessObject
//                .getInvoiceQuotationDetails(idInterv);
//        invoiceQuotationId = invoiceQuotation.getId();

        filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; ++i) {
                    if (!Pattern.compile("[1234567890]*").matcher(String.valueOf(source.charAt(i))).matches()) {
                        return "";
                    }
                }

                return null;
            }
        };

        if (noAfterDecimal > 0) {
            edtUnitPrice.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(11, noAfterDecimal)});
        } else {
            edtUnitPrice.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(11)});
        }

        edtQty.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});

        edtUnitPrice.setOnFocusChangeListener(this);
        edtQty.setOnFocusChangeListener(this);
        edtDiscount.setOnFocusChangeListener(this);
        txtAdd.setOnClickListener(this);
        txtCancel.setOnClickListener(this);
        linItemContainer.setOnClickListener(this);
//        txtNavigate.setOnClickListener(this);
        // txtBarcodeIcon.setOnClickListener(this);

        edtUnitPrice.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (start == 10
                        && !edtUnitPrice.getText().toString().contains(".")) {
                    String price = edtUnitPrice.getText().toString();
                    edtUnitPrice.setText(price.substring(0, 10));
                    edtUnitPrice.setSelection(10);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String priceVal = edtUnitPrice.getText().toString();
//                if (priceVal.isEmpty() || priceVal.trim().length() == 0) {
//                    edtDiscount.setText("");
//                }

//                edtDiscount.setText("");

                updateDiscountValue(isPercentage);

            }
        });

        edtQty.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (start == 4 && !edtQty.getText().toString().contains(".")) {
                    String qty = edtQty.getText().toString();
                    edtQty.setText(qty.substring(0, 4));
                    edtQty.setSelection(4);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String quantityVal = edtQty.getText().toString();
//                if (quantityVal.isEmpty() || quantityVal.trim().length() == 0) {
//                    edtDiscount.setText("");
//                }

//                edtDiscount.setText("");

                updateDiscountValue(isPercentage);
            }
        });

//        if (noAfterDecimal == 0)
//            edtDiscount.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4)});
//
//        edtDiscount.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before,
//                                      int count) {
//                String discountVal = edtDiscount.getText().toString();
//
//                if (discountVal.isEmpty() || discountVal.trim().length() == 0) return;
//                else if (discountVal.startsWith(".") && noAfterDecimal == 0) {
//                    discountVal = "0";
//                    edtDiscount.setText(discountVal);
//                    edtDiscount.setSelection(discountVal.length());
//                } else if (discountVal.startsWith(".")) {
//                    discountVal = "0.";
//                    edtDiscount.setText(discountVal);
//                    edtDiscount.setSelection(discountVal.length());
//                }
//
//                if (!discountVal.equals("") && !discountVal.contains("%")) {
//                    double num = Double.parseDouble(discountVal);
//                    if (num >= 101) {
//                        edtDiscount.setText(s.subSequence(0, s.length() - 1));
//                        edtDiscount
//                                .setSelection(edtDiscount.getText().length());
//                    }
//                }
//
//                if (start >= 2) {
//
//                    if (noAfterDecimal > 0) {
//                        edtDiscount
//                                .setFilters(new InputFilter[]{new DecimalDigitsInputFilter(
//                                        4, noAfterDecimal)});
//                    } else {
//                        edtDiscount.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(4)});
//                    }
//                } else {
//                    if (noAfterDecimal > 0) {
//                        edtDiscount
//                                .setFilters(new InputFilter[]{new DecimalDigitsInputFilter(
//                                        3, noAfterDecimal)});
//                    } else {
//                        edtDiscount.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(3)});
//                    }
//                }
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count,
//                                          int after) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });


        if (noAfterDecimal > 0) {
            edtDiscount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(
                    10, noAfterDecimal)});
        } else {
            edtDiscount.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(10)});
        }

        edtDiscount.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                //V52 new changes
                String quantityVal = edtQty.getText().toString();
                String priceVal = edtUnitPrice.getText().toString();
                String discountVal = edtDiscount.getText().toString();
                int disLength = 4;
                if (quantityVal != null && quantityVal.trim().length() > 0 &&
                        priceVal != null && priceVal.trim().length() > 0) {

                    double maxVal = Double.parseDouble(edtQty.getText().toString()) *
                            Double.parseDouble(edtUnitPrice.getText().toString());
                    String maxCount = "" + maxVal;
                    disLength = maxCount.trim().length();

                    if (discountVal.isEmpty() || discountVal.trim().length() == 0) return;
                    else if (discountVal.startsWith(".") && noAfterDecimal == 0) {
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
                            edtDiscount
                                    .setSelection(edtDiscount.getText().length());
                        }
                    } else {
                        if (Double.parseDouble(discountVal) >= maxVal + 1) {
                            edtDiscount.setText(s.subSequence(0, s.length() - 1));
                            edtDiscount
                                    .setSelection(edtDiscount.getText().length());
                        }
                    }

                } else {

//                    if (s.length() > 0) {
//                        edtDiscount.setText(s.subSequence(0, s.length() - 1));
//                        edtDiscount
//                                .setSelection(edtDiscount.getText().length());
//                    }

                    if (discountVal.startsWith(".") && noAfterDecimal == 0) {
                        discountVal = "0";
                        edtDiscount.setText(discountVal);
                        edtDiscount.setSelection(discountVal.length());
                    } else if (discountVal.startsWith(".")) {
                        discountVal = "0.";
                        edtDiscount.setText(discountVal);
                        edtDiscount.setSelection(discountVal.length());
                    }
                    if (s.length() > 0) {
                        if (isPercentage) {
                            if (Double.parseDouble(discountVal) > 100) {
                                edtDiscount.setText(s.subSequence(0, s.length() - 1));
                                edtDiscount
                                        .setSelection(edtDiscount.getText().length());
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

        spinnerTax.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                calculateTotal();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        // setCatalogAdapter();
        setSpinnerTaxAdapter();

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


//        Handler handler = new Handler();
        txtReference.post(new Runnable() {

            @Override
            public void run() {
                if (txtReference.getLineCount() > 1) {
                    txtExpand.setVisibility(View.VISIBLE);
                    txtReference.setSingleLine(true);
                } else {
                    txtExpand.setVisibility(View.GONE);
                }
            }
        });

        txtExpand.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (txtReference.getLineCount() > 1) {
                    txtReference.setSingleLine(true);
                    txtExpand.setText(getString(R.string.fa_expand));
                } else {
                    txtReference.setSingleLine(false);
                    txtExpand.setText(getString(R.string.fa_compress));
                }
            }
        });

        txtName.post(new Runnable() {

            @Override
            public void run() {
                if (txtName.getLineCount() > 1) {
                    txtExpandPart.setVisibility(View.VISIBLE);
                    txtName.setSingleLine(true);
                    txtName.setMaxLines(1);
                } else {
                    txtExpandPart.setVisibility(View.GONE);
                    txtName.setMaxLines(Integer.MAX_VALUE);
                }
            }
        });

        txtExpandPart.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (txtName.getLineCount() > 1) {
                    txtName.setSingleLine(true);
                    txtExpandPart.setText(getString(R.string.fa_expand));
                } else {
                    txtName.setSingleLine(false);
                    txtExpandPart.setText(getString(R.string.fa_compress));
                }
            }
        });

        txtReference.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String desc = "";
                desc = txtReference.getText().toString();
                editDescription(desc);
            }
        });
    }

    private void updateDiscountValue(boolean isPercentage) {
        String quantityVal = edtQty.getText().toString();
        String priceVal = edtUnitPrice.getText().toString();
        String discountVal = edtDiscount.getText().toString();

        if (quantityVal != null && quantityVal.trim().length() > 0 &&
                priceVal != null && priceVal.trim().length() > 0) {
            if (quantityVal.startsWith(".")) {
                quantityVal = "0.";
                edtQty.setText(quantityVal);
                edtQty.setSelection(quantityVal.length());
                edtQty.setText(String.format(Locale.US, "%.2f",Double.parseDouble(quantityVal) ));
            }
            if (discountVal.startsWith(".")) {
                discountVal = "0.";
                edtDiscount.setText(discountVal);
                edtDiscount.setSelection(discountVal.length());
            }
            if (priceVal.startsWith(".")) {
                priceVal = "0.";
                edtUnitPrice.setText(priceVal);
                edtUnitPrice.setSelection(priceVal.length());
            }
            if (priceVal.startsWith(".")) {
                priceVal = "0.";
                edtUnitPrice.setText(priceVal);
                edtUnitPrice.setSelection(priceVal.length());
            }
            double maxVal = Double.parseDouble(edtQty.getText().toString()) *
                    Double.parseDouble(edtUnitPrice.getText().toString());

            if (discountVal != null && discountVal.trim().length() > 0)
                if (isPercentage) {
                    if (Double.parseDouble(discountVal) > 100) {
                        edtDiscount.setText("100");
                        edtDiscount
                                .setSelection(edtDiscount.getText().length());
                    }
                } else {
                    if (Double.parseDouble(discountVal) > maxVal) {
                        edtDiscount.setText("0");
                        edtDiscount
                                .setSelection(edtDiscount.getText().length());
                    }
                }


        } else {
            if (!isPercentage) {
                edtDiscount.setText("0");
                edtDiscount.setSelection(edtDiscount.getText().length());
            }
        }
    }

    public void editDescription(String desc) {
        EnterDescriptionDialog enterCommentDialog = new EnterDescriptionDialog(
                this,
                new EnterDescriptionDialog.EnterDialogInterface() {

                    @Override
                    public void doOnModifyClick(String data) {
                        if (data != null) {
                            descripItem = data;
                            if (data.trim().length() > 0)
                                txtReference.setText(data);
                            else {
                                txtReference.setHint(getString(R.string.enterDescription));
                            }

                            txtReference.setTypeface(txtReference.getTypeface(), Typeface.ITALIC);
                            linReferenceContainer.setVisibility(View.VISIBLE);
                            txtReference.post(new Runnable() {

                                @Override
                                public void run() {
                                    if (txtReference.getLineCount() > 1) {
                                        txtExpand.setVisibility(View.VISIBLE);
                                        txtReference.setSingleLine(true);
                                        txtReference.setMaxLines(1);
                                    } else {
                                        txtExpand.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void doOnCancelClick() {

                    }
                }, getString(R.string.enterDescription).toUpperCase(), desc
                , true);

        enterCommentDialog.show();
    }

    private void getVauesForDB() {
        idInterv = getIntent().getExtras().getString("id_interv");
        idClient = getIntent().getExtras().getInt("id_client");
        idSite = getIntent().getExtras().getInt("id_site");


        order = getIntent().getExtras().getInt("order");
        flag = getIntent().getExtras().getInt("flag");
        currencyType = getIntent().getExtras().getString("currency_type");
        invoiceQuotation = (Invoice_Quotation_Beans) getIntent().getExtras().getSerializable("invoice_list");
        assert invoiceQuotation != null;
        invoiceQuotationId = invoiceQuotation.getId();
    }

    /**
     * <p>
     * Used for previous version...no need in this.
     * </p>
     * Set the values for the description AutoCompleteTextview and set listener
     * for it
     */
    // @SuppressLint("ClickableViewAccessibility")
    // private void setCatalogAdapter() {
    // catList = (ArrayList<InvoicingCatalogCategoryBeans>) dataAccessObject
    // .getCatalogCategory();
    // descriptionList = new ArrayList<String>();
    // priceValues = new ArrayList<String>();
    // itemNames = new ArrayList<String>();
    // idTaxRates = new ArrayList<String>();
    // for (int i = 0; i < catList.size(); i++) {
    // subCatList = (ArrayList<InvoicingCatalogSubCategoryBeans>)
    // dataAccessObject
    // .getSubcategoryByID(String.valueOf(catList.get(i)
    // .getIdCategory()));
    // for (int j = 0; j < subCatList.size(); j++) {
    // descriptionList.add(catList.get(i).getNameCategory().toString()
    // + " > " + subCatList.get(j).getNameSubcategory());
    // priceValues.add(subCatList.get(j).getPriceSubcategory());
    // idTaxRates.add(subCatList.get(j).getIdTaxRate());
    // if (subCatList.get(j).getItemName() != null) {
    // itemNames.add(subCatList.get(j).getItemName());
    // } else {
    // itemNames.add("");
    // }
    // }
    //
    // }
    // }

    /**
     * <p>
     * set the values for Tax spinner. To set a hint for the spinner, we set a
     * dummy value as "TAX" and hide that in the spinner dropdown.
     * </p>
     * <p>
     * Also we have set the custom font for the spinner values.
     * </p>
     */
    private void setSpinnerTaxAdapter() {
        taxRateList = (ArrayList<TaxRates>) dataAccessObject.getTaxRates();
        taxRates = new ArrayList<String>();
        taxRates.add(getResources().getString(R.string.txt_tax_label).toUpperCase());
        for (int i = 0; i < taxRateList.size(); i++) {

            BigDecimal taxDecimal = new BigDecimal(taxRateList.get(i).getValTaxRate()).
                    setScale(noAfterDecimal, RoundingMode.HALF_UP);

            taxRates.add(String.valueOf(taxDecimal)
                    + "%");

//            taxRates.add(String.valueOf(taxRateList.get(i).getValTaxRate())
//                    + "%");

        }
        taxAdapter = new ArrayAdapter<String>(this,
                R.layout.layout_custom_adapter_spinner, taxRates) {

            Typeface font = Typeface
                    .createFromAsset(
                            getContext().getAssets(),
                            getApplicationContext().getString(
                                    R.string.fontName_avenir));

            @NotNull
            @Override
            public View getView(int position, View convertView, @NotNull ViewGroup parent) {
                TextView view = (TextView) super.getView(position, convertView,
                        parent);
                view.setTypeface(font);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView,
                                        @NotNull ViewGroup parent) {
                View v = null;
                // If this is the initial dummy entry, make it hidden
                if (position == 0) {
                    TextView tv = new TextView(getContext());
                    tv.setHeight(0);
                    tv.setVisibility(View.GONE);
                    v = tv;
                } else {
                    // Pass convertView as null to prevent reuse of special case
                    // views
                    v = super.getDropDownView(position, null, parent);
                    ((TextView) v).setTypeface(font);
                }

                // Hide scroll bar because it appears sometimes unnecessarily,
                // this does not prevent scrolling
                parent.setVerticalScrollBarEnabled(false);
                return v;
            }
        };

        spinnerTax.setAdapter(taxAdapter);
    }

    /**
     * to calculate the total value after entering the quantity & amount price
     */
    private void calculateTotal() {
        if (!TextUtils.isEmpty(edtUnitPrice.getText().toString())
                && !TextUtils.isEmpty(edtQty.getText().toString())) {

            String taxValueStr = spinnerTax.getSelectedItem().toString();

            double quantity = Double.parseDouble(edtQty.getText().toString());
            double amount = Double.parseDouble(edtUnitPrice.getText().toString());

            double totalVal = amount * quantity;

//            totalWithDiscount = totalVal - (totalVal * discount) / 100;

            edtQty.setText(String.format(Locale.US, "%.2f", quantity));

            //v52 changes
            if (isPercentage)
                totalWithDiscount = totalVal - (totalVal * discount) / 100;
            else
                totalWithDiscount = totalVal - discount;

            totalWithTax = totalWithDiscount + taxValue;

            if (TextUtils.isEmpty(edtDiscount.getText().toString())
                    && taxValueStr.equalsIgnoreCase(getResources().getString(
                    R.string.txt_tax_label).toUpperCase())) {
                txtTotal.setText(String.format(Locale.US, formatDecimal, totalVal) + " " + currencyType);
            } else {
                if (!TextUtils.isEmpty(edtDiscount.getText().toString())
                        && taxValueStr.equalsIgnoreCase(getResources()
                        .getString(R.string.txt_tax_label).toUpperCase())) {
                    String discountStr = edtDiscount.getText().toString();

                    //v51 code
//                    double discount;
//
//                    if (discountStr.contains("%"))
//                        discount = Double.parseDouble(discountStr.substring(0,
//                                discountStr.indexOf("%")));
//                    else
//                        discount = Double.parseDouble(discountStr);
//                    double totalWithDiscount = totalVal - (totalVal * discount)
//                            / 100;

                    //V52 changes
                    double totalWithDiscount = 0;
                    double discount = 0;
                    if (isPercentage) {
                        if (discountStr.contains("%"))
                            discount = Double.parseDouble(discountStr.substring(0,
                                    discountStr.indexOf("%")));
                        else
                            discount = Double.parseDouble(discountStr);

                        totalWithDiscount = totalVal - (totalVal * discount)
                                / 100;
                    } else {
                        discount = Double.parseDouble(discountStr);
                        totalWithDiscount = totalVal - discount;
                    }

                    discount = CommonUtils.roundDoubleValue(discount, noAfterDecimal);
                    edtDiscount.setText(String.format(Locale.US, formatDecimal, discount));
                    txtTotal.setText(String.format(Locale.US, formatDecimal, totalWithDiscount) + " " + currencyType);
                } else if (TextUtils.isEmpty(edtDiscount.getText().toString())
                        && !taxValueStr.equalsIgnoreCase(getResources()
                        .getString(R.string.txt_tax_label).toUpperCase())) {
                    double totalAmount = CommonUtils.roundDoubleValue(totalVal, noAfterDecimal);
                    double taxPercent = Double.parseDouble(taxValueStr.substring(
                            0, taxValueStr.indexOf("%")));
                    double taxAmount = (totalVal * taxPercent)
                            / 100;
                    taxAmount = CommonUtils.roundDoubleValue(taxAmount, noAfterDecimal);
                    double totalWithTax = totalAmount + taxAmount;
                    txtTotal.setText(String.format(Locale.US, formatDecimal, totalWithTax) + " " + currencyType);
                } else if (!TextUtils.isEmpty(edtDiscount.getText().toString())
                        && !taxValueStr.equalsIgnoreCase(getResources()
                        .getString(R.string.txt_tax_label).toUpperCase())
                        && (edtDiscount.getText().toString().lastIndexOf("%") != 0)) {
                    String discountStr = edtDiscount.getText().toString();

                    double discount;

//                    if (discountStr.contains("%"))
//                        discount = Double.parseDouble(discountStr.substring(0,
//                                discountStr.indexOf("%")));
//                    else
//                        discount = Double.parseDouble(discountStr);

                    //V52 changes

                    if (isPercentage) {
                        if (discountStr.contains("%"))
                            discount = Double.parseDouble(discountStr.substring(0,
                                    discountStr.indexOf("%")));
                        else
                            discount = Double.parseDouble(discountStr);

                        totalWithDiscount = totalVal - (totalVal * discount)
                                / 100;
                    } else {
                        discount = Double.parseDouble(discountStr);
                        totalWithDiscount = totalVal - discount;
                    }

                    double taxPercent = Double.parseDouble(taxValueStr.substring(
                            0, taxValueStr.indexOf("%")));

//                    double totalWithDiscount = totalVal - (totalVal * discount)
//                            / 100;

                    double taxAmount = (totalWithDiscount * taxPercent) / 100;

                    totalWithDiscount = CommonUtils.roundDoubleValue(totalWithDiscount, noAfterDecimal);

                    taxAmount = CommonUtils.roundDoubleValue(taxAmount, noAfterDecimal);
                    discount = CommonUtils.roundDoubleValue(discount, noAfterDecimal);

                    double totalWithTax = totalWithDiscount + taxAmount;
                    edtDiscount.setText(String.format(Locale.US, formatDecimal, discount));
                    txtTotal.setText(String.format(Locale.US, formatDecimal, totalWithTax) + " " + currencyType);
                } else if (!taxValueStr.equalsIgnoreCase(getResources()
                        .getString(R.string.txt_tax_label).toUpperCase())
                        && (edtDiscount.getText().toString().lastIndexOf("%") == 0)) {

                    edtDiscount.setText("");
                    double totalAmount = CommonUtils.roundDoubleValue(totalVal, noAfterDecimal);
                    double taxPercent = Double.parseDouble(taxValueStr.substring(
                            0, taxValueStr.indexOf("%")));
                    double taxAmount = (totalVal * taxPercent)
                            / 100;
                    taxAmount = CommonUtils.roundDoubleValue(taxAmount, noAfterDecimal);
                    double totalWithTax = totalAmount + taxAmount;
                    txtTotal.setText(String.format(Locale.US, formatDecimal, totalWithTax) + " " + currencyType);
                }
            }

        } else {
            double defValue = 0.00;
            txtTotal.setText(String.format(Locale.US, formatDecimal, defValue) + " " + currencyType);
        }
    }

    // ***********************************LOCAL...METHODS...ENDS...HERE***********************************************

    // ....................................LISTENER...STARTS...HERE....................................................

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.edtInvoiceUnitPrice:
                calculateTotal();
                String unitPrice = edtUnitPrice.getText().toString();
                if (!TextUtils.isEmpty(unitPrice)) {
                    if (!hasFocus
                            && (unitPrice.lastIndexOf(".") == unitPrice.length() - 1)) {
//                        edtUnitPrice.setText(unitPrice.substring(0,
//                                unitPrice.indexOf("."))
//                                + ".00");
//                        edtUnitPrice.setText(String.format(Locale.US, formatDecimal, unitPrice));

                        BigDecimal bdPriceValue = new BigDecimal(Double.parseDouble(unitPrice.substring(0,
                                unitPrice.indexOf("."))
                                + ".00")).
                                setScale(noAfterDecimal, RoundingMode.HALF_UP);
                        edtUnitPrice.setText(bdPriceValue.toString());
                    }
                }
            case R.id.edtInvoiceQty:
                calculateTotal();
                String qty = edtQty.getText().toString();
                if (!TextUtils.isEmpty(qty)) {
                    if (!hasFocus && (qty.lastIndexOf(".") == qty.length() - 1)) {
//                        edtQty.setText(qty.substring(0, qty.indexOf(".")) + ".00");
//                        edtQty.setText(String.format(Locale.US, formatDecimal, qty));
                        BigDecimal bdQtyValue = new BigDecimal(Double.parseDouble(qty.substring(0,
                                qty.indexOf("."))
                                + ".00")).
                                setScale(noAfterDecimal, RoundingMode.HALF_UP);
                        edtQty.setText(bdQtyValue.toString());
                        edtQty.setText(String.format(Locale.US, "%.2f", Double.parseDouble(bdQtyValue.toString())));
                    }
                }
            case R.id.edtInvoiceDiscount:
                calculateTotal();
                String discount = edtDiscount.getText().toString();
                if (!TextUtils.isEmpty(discount)) {
//                    if (!hasFocus
//                            && (discount.lastIndexOf(".") == discount.length() - 1)) {
//                        edtDiscount.setText(discount.substring(0,
//                                discount.indexOf("."))
//                                + ".00%");
//                    } else if (!hasFocus && !discount.contains(".")
//                            && !discount.contains("%")) {
//                        edtDiscount.setText(discount + "%");
//                    } else if (!hasFocus
//                            && (discount.lastIndexOf(".") != discount.length() - 1)
//                            && !discount.contains("%")) {
//                        edtDiscount.setText(discount + "%");
//                    }
                }
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtAdd:
                String desc = category;
                String price = edtUnitPrice.getText().toString();
                String quty = edtQty.getText().toString();
                if (desc == null || price.matches("") || quty.matches("")) {
                    if (desc == null)
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.txt_empty_desc),
                                Toast.LENGTH_SHORT).show();
                    else if (price.matches(""))
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.txt_empty_price),
                                Toast.LENGTH_SHORT).show();
                    else if (quty.matches(""))
                        Toast.makeText(getApplicationContext(),
                                getResources().getString(R.string.txt_validate_qty),
                                Toast.LENGTH_SHORT).show();
                } else {
                    String discountStr = "";
                    unitPrice = Double.parseDouble(edtUnitPrice.getText().toString());
                    qty = Double.parseDouble(edtQty.getText().toString());
                    Double.parseDouble(edtUnitPrice.getText().toString());
                    if (!edtDiscount.getText().toString().matches("")
                            && edtDiscount.getText().toString().lastIndexOf("%") != 0) {
                        discountStr = edtDiscount.getText().toString();
                        if (discountStr.contains("%"))
                            discount = Double.parseDouble(discountStr.substring(0,
                                    discountStr.indexOf("%")));
                        else
                            discount = Double.parseDouble(discountStr);
                    }
                    if (!spinnerTax
                            .getSelectedItem()
                            .toString()
                            .equalsIgnoreCase(
                                    getResources().getString(R.string.txt_tax_label).toUpperCase())) {
                        String taxValueStr = spinnerTax.getSelectedItem()
                                .toString();
                        tax = Double.valueOf(taxValueStr.substring(0,
                                taxValueStr.indexOf("%")));
                    }

                    total = unitPrice * qty;
//                    totalWithDiscount = total - (total * discount) / 100;
                    if (isPercentage) {
                        totalWithDiscount = total - (total * discount)
                                / 100;
                    } else {
                        if (discount > 0) {
                            discount = Double.parseDouble(discountStr);
                        }
                        totalWithDiscount = total - discount;

                    }

                    taxValue = (totalWithDiscount * tax) / 100;

                    totalWithDiscount = CommonUtils.roundDoubleValue(totalWithDiscount, noAfterDecimal);
                    taxValue = CommonUtils.roundDoubleValue(taxValue, noAfterDecimal);

                    totalWithTax = totalWithDiscount + taxValue;
                    txtTotal.setText(String.format(Locale.US, formatDecimal, totalWithTax) + " " + currencyType);
                    if (TextUtils.isEmpty(invoiceQuotationId)) {
                        invoiceQuotationId = dataAccessObject.getUniqueId();
                        new addInvoiceOrQuotation().execute();
                    } else {
                        new updateInvoiceOrQuotation().execute();
                    }
                }
                break;
            case R.id.linItemContainer:
                //search new implemented
                Intent partsIntent = new Intent(this, PartsAndServicesListNew.class);
//                Intent partsIntent = new Intent(this, PartsAndServicesList.class);
                partsIntent.putExtra(KEYS.Catalouge.ID_INTER, idInterv);
                partsIntent.putExtra(KEYS.Catalouge.IS_INVOICE, true);
                partsIntent.putExtra(KEYS.Catalouge.IS_PARTS_AND_SERVICES, false);
                startActivityForResult(partsIntent,
                        RequestCode.REQUEST_CODE_OPEN_ITEM);
                break;
//            case R.id.txt_navigate:
//                //search new implemented
//                Intent intent = new Intent(this, PartsAndServicesListNew.class);
////                Intent intent = new Intent(this, PartsAndServicesList.class);
//                intent.putExtra(KEYS.Catalouge.ID_INTER, idInterv);
//                intent.putExtra(KEYS.Catalouge.IS_INVOICE, true);
//                intent.putExtra(KEYS.Catalouge.IS_PARTS_AND_SERVICES, false);
//                startActivityForResult(intent, RequestCode.REQUEST_CODE_OPEN_ITEM);
//                break;
            case R.id.txtCancel:
                finish();
                break;
            default:
                break;
        }
    }

    // **************************************LISTENER...ENDS...HERE******************************************************

    // .......................................ASYNC...CLASS...STARTS...HERE..............................................

    /**
     * Async class to add a new invoice or quotation
     */
    private class addInvoiceOrQuotation extends
            AsyncTaskCoroutine<String, Boolean> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressLint("SimpleDateFormat")
        @Override
        public Boolean doInBackground(String... params) {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss.SSS");

            boolean drp = dataAccessObject.addInvoiceOrQuotation(idInterv,
                    flag, sdf.format(cal.getTime()), idClient, idSite,
                    totalWithDiscount, taxValue, totalWithTax, invoiceQuotationId,flInvoiceStrict);
            return drp;
        }

        @Override
        public void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            DialogUtils.dismissProgressDialog();
            boolean drp = result.booleanValue();
            if (drp) {
                new AddInvoiceOrQuotationItem().execute();
            } else
                Toast.makeText(getApplicationContext(),
                        getString(R.string.msg_error), Toast.LENGTH_SHORT)
                        .show();
        }
    }

    /**
     * Async class to update the previous values of invoice or quotation
     */
    private class updateInvoiceOrQuotation extends
            AsyncTaskCoroutine<String, Boolean> {
        private double totalWithDiscountRate, taxAmount, totalWithTaxAmt;

        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressLint("SimpleDateFormat")
        @Override
        public Boolean doInBackground(String... params) {
//            double subTotal = totalWithDiscount
//                    + invoiceQuotation.getTotalWithoutTax();
//            double prevTaxValue = taxValue + invoiceQuotation.getTax();
//            double prevTotal = totalWithTax + invoiceQuotation.getTotalWithTax();
//            boolean drp = dataAccessObject
//                    .updateInvoiceOrQuotation(invoiceQuotation.getId(),
//                            subTotal, prevTaxValue, prevTotal, true);

            ArrayList<Invoice_Quotation_Items_Beans> invoiceQuotationList =
                    (ArrayList<Invoice_Quotation_Items_Beans>) dataAccessObject
                            .getInvoiceQuotationItemValues(invoiceQuotationId);

            for (int i = 0; i < invoiceQuotationList.size(); i++) {
                double total = invoiceQuotationList.get(i).getTotal();
                double taxAmount1 = invoiceQuotationList.get(i).getTaxValue();
                double totWithTax = invoiceQuotationList.get(i)
                        .getTotalWIthTax();

                totalWithDiscountRate = totalWithDiscountRate + total;
                taxAmount = taxAmount + taxAmount1;
                totalWithTaxAmt = totalWithTaxAmt + totWithTax;
            }

            double subTotal = totalWithDiscount + totalWithDiscountRate;
            double prevTaxValue = taxValue + taxAmount;
            double prevTotal = totalWithTax + totalWithTaxAmt;

            boolean drp = dataAccessObject
                    .updateInvoiceOrQuotation(invoiceQuotation.getId(),
                            subTotal, prevTaxValue, prevTotal, true);


            return drp;
        }

        @Override
        public void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            DialogUtils.dismissProgressDialog();
            boolean drp = result.booleanValue();
            if (drp) {
                new AddInvoiceOrQuotationItem().execute();
            } else
                Toast.makeText(getApplicationContext(),
                        getString(R.string.msg_error), Toast.LENGTH_SHORT)
                        .show();
        }
    }


    /**
     * Async class to add items in invoice or quotation
     */
    private class AddInvoiceOrQuotationItem extends
            AsyncTaskCoroutine<String, Boolean> {

        String sItemName;
        String sReference;

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            sItemName = txtName.getText().toString();
            sReference = srtReference;
        }

        @Override
        public Boolean doInBackground(String... params) {
            String desc = category + " > " + sItemName;
            String item = sReference;
            if (invoiceQuotationId == null) {
                invoiceQuotation = dataAccessObject
                        .getInvoiceQuotationDetailsNew(idInterv, invoiceQuotationId);
                invoiceQuotationId = invoiceQuotation.getId();
                order = 0;
            } else {
                order = order + 1;
            }
            /*
             * SQL doesn't support apostrophe ("'"). So replace the apostrophe
             * ("'") with ("\'").
             */
            if (item != null)
                if (item.contains("'")) {
                    item = item.replace("'", "''");
                }
            if (desc != null)
                if (desc.contains("'")) {
                    desc = desc.replace("'", "''");
                }

            boolean drp = dataAccessObject.addInvoiceOrQuotationItem(item,
                    desc, unitPrice, qty, tax, order, discount, totalWithDiscount,
                    taxValue, totalWithTax, invoiceQuotationId, descripItem, isPercentage);
            return drp;

        }

        @Override
        public void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            DialogUtils.dismissProgressDialog();
            boolean drp = result.booleanValue();
            if (drp) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.txt_ack_msg), Toast.LENGTH_SHORT)
                        .show();
                EventBus.getDefault().post(new AddInvoiceQuotationEvent(invoiceQuotationId, false));

                finish();
            } else
                Toast.makeText(getApplicationContext(),
                        getString(R.string.msg_error), Toast.LENGTH_SHORT)
                        .show();
        }
    }

    // **********************************ASYNC...CLASS...ENDS...HERE********************************************

    // public class CustomAutoCompleteAdapter extends ArrayAdapter<String> {
    //
    // private ArrayList<String> fullList;
    // private int viewResourceId;
    //
    // // private static final String TAG = "CustomAutoCompleteAdapter";
    //
    // public CustomAutoCompleteAdapter(Context context, int viewResourceId,
    // int textViewResourceId, ArrayList<String> objects) {
    // super(context, viewResourceId, textViewResourceId, objects);
    // this.viewResourceId = viewResourceId;
    // fullList = objects;
    // mOriginalValues = objects;
    // }
    //
    // @Override
    // public int getCount() {
    // return mOriginalValues.size();
    // }
    //
    // @Override
    // public View getView(int position, View convertView, ViewGroup parent) {
    // View v = convertView;
    // if (v == null) {
    // LayoutInflater vi = (LayoutInflater) getContext()
    // .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    // v = vi.inflate(viewResourceId, null);
    // }
    // TextView productLabel = (TextView) v
    // .findViewById(android.R.id.text1);
    // if (productLabel != null) {
    // if (!TextUtils.isEmpty(mOriginalValues.get(position))) {
    // productLabel.setText(mOriginalValues.get(position));
    // } else {
    // productLabel.setHeight(0);
    // productLabel.setVisibility(View.GONE);
    // }
    //
    // }
    // return v;
    // }
    //
    // @Override
    // public Filter getFilter() {
    // return textFilter;
    // }
    //
    // @SuppressLint("DefaultLocale")
    // Filter textFilter = new Filter() {
    //
    // @SuppressWarnings("unchecked")
    // @Override
    // protected void publishResults(CharSequence constraint,
    // FilterResults results) {
    // mOriginalValues = (ArrayList<String>) results.values;
    // notifyDataSetChanged();
    // }
    //
    // @SuppressLint("DefaultLocale")
    // @Override
    // protected FilterResults performFiltering(CharSequence charSearch) {
    // FilterResults results = new FilterResults();
    // if (charSearch == null || charSearch.length() == 0) {
    // results.values = fullList;
    // results.count = fullList.size();
    // } else {
    // ArrayList<String> filteredItem = new ArrayList<String>();
    //
    // final ArrayList<String> list = fullList;
    // /*
    // * We'll go through all the location and see if they contain
    // * the supplied string/character
    // */
    // for (int i = 0; i < list.size(); i++) {
    // String c = list.get(i);
    // if (c.toLowerCase().contains(
    // charSearch.toString().toLowerCase())) {
    //
    // // if `contains` == true then add it
    // // to our filtered list
    //
    // filteredItem.add(c);
    // }
    // }
    //
    // // Finally set the filtered values and size/count
    // results.values = filteredItem;
    // results.count = filteredItem.size();
    //
    // }
    // return results;
    // }
    // };
    // }
    //
    // public class CustomAutoCompleteAdapterDesc extends ArrayAdapter<String> {
    //
    // private ArrayList<String> fullList;
    // private int viewResourceId;
    //
    // // private static final String TAG = "CustomAutoCompleteAdapter";
    //
    // public CustomAutoCompleteAdapterDesc(Context context,
    // int viewResourceId, int textViewResourceId,
    // ArrayList<String> objects) {
    // super(context, viewResourceId, textViewResourceId, objects);
    // this.viewResourceId = viewResourceId;
    // fullList = objects;
    // mOriginalValuesDesc = objects;
    // }
    //
    // @Override
    // public int getCount() {
    // return mOriginalValuesDesc.size();
    // }
    //
    // @Override
    // public View getView(int position, View convertView, ViewGroup parent) {
    // View v = convertView;
    // if (v == null) {
    // LayoutInflater vi = (LayoutInflater) getContext()
    // .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    // v = vi.inflate(viewResourceId, null);
    // }
    // TextView productLabel = (TextView) v
    // .findViewById(android.R.id.text1);
    // if (productLabel != null) {
    // productLabel.setText(mOriginalValuesDesc.get(position));
    // }
    // return v;
    // }
    //
    // @Override
    // public Filter getFilter() {
    // return textFilter;
    // }
    //
    // @SuppressLint("DefaultLocale")
    // Filter textFilter = new Filter() {
    //
    // @SuppressWarnings("unchecked")
    // @Override
    // protected void publishResults(CharSequence constraint,
    // FilterResults results) {
    // mOriginalValuesDesc = (ArrayList<String>) results.values;
    // notifyDataSetChanged();
    // }
    //
    // @SuppressLint("DefaultLocale")
    // @Override
    // protected FilterResults performFiltering(CharSequence charSearch) {
    // FilterResults results = new FilterResults();
    // if (charSearch == null || charSearch.length() == 0) {
    // results.values = fullList;
    // results.count = fullList.size();
    // } else {
    // ArrayList<String> filteredItem = new ArrayList<String>();
    //
    // final ArrayList<String> list = fullList;
    // /*
    // * We'll go through all the location and see if they contain
    // * the supplied string/character
    // */
    // for (int i = 0; i < list.size(); i++) {
    // String c = list.get(i);
    // if (c.toLowerCase().contains(
    // charSearch.toString().toLowerCase())) {
    //
    // // if `contains` == true then add it
    // // to our filtered list
    //
    // filteredItem.add(c);
    // }
    // }
    //
    // // Finally set the filtered values and size/count
    // results.values = filteredItem;
    // results.count = filteredItem.size();
    //
    // }
    // return results;
    // }
    // };
    // }
}
