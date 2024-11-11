package com.synchroteam.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.synchroteam.TypefaceLibrary.Button;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.CConectionsBeans;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.beans.GlobalTaxInvoiceList;
import com.synchroteam.beans.Invoice_Quotation_Beans;
import com.synchroteam.beans.Invoice_Quotation_Items_Beans;
import com.synchroteam.beans.Quotation_Items_Beans;
import com.synchroteam.beans.UpdateDataBaseEvent;
import com.synchroteam.beans.UpdateUiAfterSync;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.AppUpdateDialog;
import com.synchroteam.dialogs.AuthenticationErrorDialog;
import com.synchroteam.dialogs.ConvertToInvoiceDialog;
import com.synchroteam.dialogs.DeleteInvoiceQuotationDialog;
import com.synchroteam.dialogs.ErrorDialog;
import com.synchroteam.dialogs.GlobalDiscountDialog;
import com.synchroteam.dialogs.InvoicePaymentTransactionResultDialog;
import com.synchroteam.dialogs.InvoiceQuotationPaynowConfirmDialog;
import com.synchroteam.dialogs.MultipleDeviceNotSupported;
import com.synchroteam.dialogs.SynchronizationErrorDialog;
import com.synchroteam.dialogs.TaxListDialog;
import com.synchroteam.events.AddInvoiceQuotationEvent;
import com.synchroteam.events.UpdateInvoiceQuotationTaxEvent;
import com.synchroteam.listadapters.InvoiceQuotationListAdapter.RefreshInvoiceQutationList;
import com.synchroteam.listadapters.InvoiceQuotationListAdapterNew;
import com.synchroteam.listadapters.TaxNameListAdapterRv;
import com.synchroteam.retrofit.ApiInterface;
import com.synchroteam.retrofit.Apiclient;
import com.synchroteam.retrofit.models.paymentservice.PaymentServiceModel;
import com.synchroteam.synchroteam.AddInvoiceQuotationNew;
import com.synchroteam.synchroteam.SiteList;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.technicalsupport.JobDetails;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.CreditCardUtils;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.SharedPref;
import com.synchroteam.utils.SynchroteamUitls;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;
import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.synchroteam.utils.CreditCardUtils.EXTRA_PAYMENT_SUCCESS;

/**
 * This class holds the view for create/update invoice or quotation and shows details of invoice/quotation.
 *
 * @author Trident
 */
public class InvoiceQuotationFragmentNew extends Fragment implements
        OnClickListener, RefreshInvoiceQutationList {

    // ....................................INSTANCE...VARIABLE...STARTS...HERE.....................................

    private RelativeLayout relHeaderView;
    private TextView txtTitle;
    private android.widget.TextView txtViewPdf;
    private android.widget.TextView txtDelete;
    private FloatingActionsMenu famAddInvoiceQuotation;
    private FloatingActionButton fabAddInvoice, fabAddQuotation;
    private ListView listViewInvoiceQuotation;
    private InvoiceQuotationListAdapterNew invoiceQuotationListAdapter;
    private TextView txtSubTotal, txtTaxValue, taxLbl, txtTotal, txtBalance, labelBal, txtLblTotal,
            txtLblDiscount, txtGlobalDiscount,txt_draft_title;
    private LinearLayout linPdf;
    private Button btnPayNow, btn_cnv_invoice,btn_save_new_invoice;
    private RelativeLayout relEmptyView;
    private RecyclerView rvListTaxName, RvListTaxValue;
    FloatingActionButton fabAddItem;

    private Dao dao;
    private ArrayList<Invoice_Quotation_Items_Beans> invoiceQuotationList;
    private RelativeLayout relTotalContainer;
    private Bundle bundleInvoice;
    private Invoice_Quotation_Beans invoiceQuotation;
    private String invoiceQuotationId;
    private int invoiceQuotationNumber = 0;
    private GestionAcces gestionAcces;
    private int flCreateUpdateInvoiceQuotation, order;
    private JobDetails jobDetails;
    private boolean isInvoice;
    private double invoicePaymentBalanceAmount = 0.00f;
    private String currencyType;
    private ProgressDialog progressDSynch;

    private ArrayList<CConectionsBeans> cConectionsBeansArrayList;

    private static final String KEY_CURRENCY = "currency";
    private static final String KEY_STRIPE_PUBLISHABLE_KEY = "stripe_publishable_key";

    private static final String TAG = "InvoiceQuotationFragment";
    private String pdfPublicLink;
    private ArrayList<GlobalTaxInvoiceList> globalTaxInvoiceLists;

    private TaxNameListAdapterRv taxNameListAdapterRv;
    private RecyclerView rvTaxListName;
    private LinearLayout linTaxList;
    private LinearLayout linTaxItem;
    private LinearLayout linGlobalDiscountItem;

    private LinearLayoutManager linearLayoutManager;

    int noAfterDecimal = 2;
    String formatDecimal = "%.2f";
    private int idClient = 0;
    private String idInterv;

    private boolean flInvoiceStrict,enableEdit;

    // ************************************INSTANCE...VARIABLE...ENDS...HERE***************************************

    // ....................................LIFECYCLE...METHOD...STARTS...HERE.......................................
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.layout_invoice_quotation_fragment_updated, container, false);
        initiateView(view);

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

        linearLayoutManager = new LinearLayoutManager(getActivity());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        jobDetails = (JobDetails) getActivity();

        getInvoiceOrQuotaion();

        flCreateUpdateInvoiceQuotation = gestionAcces
                .getFlCreateUpdateInvoiceQuotation();

        flInvoiceStrict = gestionAcces.isFlInvoiceStrict();

        setTitleAndDeleteBtn();

//        txtLabel.setText(getActivity().getString(R.string.txt_item_label));

        getIQList();

        changeItemsVisibility();


        txtDelete.setOnClickListener(this);
        fabAddInvoice.setOnClickListener(this);
        fabAddQuotation.setOnClickListener(this);
        fabAddItem.setOnClickListener(this);
        // txtSubTotal.setText(String.valueOf(invoiceQuotation
        // .getTotalWithoutTax()));
        // txtTaxValue.setText(String.valueOf(invoiceQuotation.getTax()));
        // txtTotal.setText(String.valueOf(invoiceQuotation.getTotalWithTax()));

        strictInvoiceEnabled(invoiceQuotation.isStrictInvoice(),
                invoiceQuotation.getNumberOfIQ());
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        EventBus.getDefault().unregister(this);
        super.onDestroy();

    }

    // ************************************LIFECYCLE...METHOD...ENDS...HERE******************************************

    // .....................................LOCAL...METHODS...STARTS...HERE...........................................

    private void initiateView(View view) {
        relHeaderView = (RelativeLayout) view.findViewById(R.id.rel_header_view);
        txtTitle = (TextView) view.findViewById(R.id.txt_invoice_quotation_title);
        txtDelete = (android.widget.TextView) view.findViewById(R.id.txt_delete_invoice_quotation);
        famAddInvoiceQuotation = (FloatingActionsMenu) view
                .findViewById(R.id.addInvoiceQuotation);
        fabAddInvoice = (FloatingActionButton) view
                .findViewById(R.id.addInvoice);
        fabAddQuotation = (FloatingActionButton) view
                .findViewById(R.id.addQuotation);
        fabAddItem = (FloatingActionButton) view.findViewById(R.id.addItem);
        listViewInvoiceQuotation = (ListView) view
                .findViewById(R.id.invoiceQuotationList);
        relTotalContainer = (RelativeLayout) view
                .findViewById(R.id.relTotalContainer);
        txtSubTotal = (TextView) view.findViewById(R.id.txtSubtotal);
        txtTaxValue = (TextView) view.findViewById(R.id.txtTax);

        rvTaxListName = (RecyclerView) view.findViewById(R.id.rvTaxName);
        linTaxList = (LinearLayout) view.findViewById(R.id.linTaxList);
        linTaxItem = (LinearLayout) view.findViewById(R.id.linTaxItem);
        linGlobalDiscountItem = (LinearLayout) view.findViewById(R.id.linGlobalDiscountItem);

        taxLbl = (TextView) view.findViewById(R.id.txtLblTax);
        txtLblDiscount = (TextView) view.findViewById(R.id.txtLblDiscount);
        txtGlobalDiscount = (TextView) view.findViewById(R.id.txtGlobalDiscount);
        txt_draft_title = (TextView) view.findViewById(R.id.txt_draft_title);

        txtTotal = (TextView) view.findViewById(R.id.txtTotal);
        txtLblTotal = (TextView) view.findViewById(R.id.txtLblTotal);
        txtBalance = (TextView) view.findViewById(R.id.txtBalance);
        labelBal = (TextView) view.findViewById(R.id.labelBal);
        btnPayNow = (Button) view.findViewById(R.id.btn_pay_now);
        btn_cnv_invoice = (Button) view.findViewById(R.id.btn_cnv_invoice);
        btn_save_new_invoice = (Button) view.findViewById(R.id.btn_save_new_invoice);

//        txtLabel = (TextView) view.findViewById(R.id.txtLabel);
//        layoutBalance = (LinearLayout) view.findViewById(R.id.layoutBalance);
        linPdf = (LinearLayout) view.findViewById(R.id.linPdf);

        txtViewPdf = (android.widget.TextView) view.findViewById(R.id.txtViewPdf);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(),
                getActivity().getString(R.string.fontName_fontAwesome));
        txtViewPdf.setTypeface(typeface);

        android.widget.TextView txtEmptyList = (android.widget.TextView) view
                .findViewById(R.id.empty_text);
        relEmptyView = (RelativeLayout) view.findViewById(R.id.rel_empty_view);

        Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(),
                getActivity().getString(R.string.fontName_fontAwesome));
        txtDelete.setTypeface(typeFace);
        txtEmptyList.setTypeface(typeFace);

        txtLblDiscount.setText("- " + getActivity().getString(R.string.txt_discount_label));
        taxLbl.setText("+ " + getActivity().getString(R.string.add_txt_tax_label));
        dao = DaoManager.getInstance();
        gestionAcces = dao.getAcces();
        try {
            noAfterDecimal = gestionAcces.getNumDecimals();
            formatDecimal = "%." + noAfterDecimal + "f";
        } catch (Exception e) {
            noAfterDecimal = 2;
            formatDecimal = "%." + noAfterDecimal + "f";
        }
        bundleInvoice = getArguments();
        idInterv = bundleInvoice.getString("id_interv");
        currencyType = dao.getDeviseCustomer();

        idClient = bundleInvoice.getInt("id_client");

        setBalanceLayoutVisible(false);
        btnPayNow.setOnClickListener(this);
        btn_cnv_invoice.setOnClickListener(this);
        btn_save_new_invoice.setOnClickListener(this);
        linPdf.setOnClickListener(this);
        linTaxList.setOnClickListener(this);
        linTaxItem.setOnClickListener(this);
        linGlobalDiscountItem.setOnClickListener(this);

        txtTotal.setTypeface(txtTotal.getTypeface(), Typeface.BOLD);
        txtLblTotal.setTypeface(txtLblTotal.getTypeface(), Typeface.BOLD);
    }

    /**
     * gets the invoice or quotation for current job.
     */
    private void getInvoiceOrQuotaion() {
        invoiceQuotation = (Invoice_Quotation_Beans) bundleInvoice.getSerializable("invoice_list");
        invoiceQuotationId = invoiceQuotation.getId();
    }


    /**
     * Hide/Show functional elements according to id.
     */
    private void changeItemsVisibility() {
        if (invoiceQuotation.getId() != null) {
            fabAddItem.setVisibility(View.VISIBLE);
            relTotalContainer.setVisibility(View.VISIBLE);
            relHeaderView.setVisibility(View.VISIBLE);
            fabAddInvoice.setVisibility(View.GONE);
            fabAddQuotation.setVisibility(View.GONE);
        } else {
            relHeaderView.setVisibility(View.GONE);
            relTotalContainer.setVisibility(View.GONE);
            fabAddItem.setVisibility(View.GONE);
            famAddInvoiceQuotation.setVisibility(View.VISIBLE);
            fabAddInvoice.setVisibility(View.VISIBLE);
            fabAddQuotation.setVisibility(View.VISIBLE);
        }
        if (gestionAcces != null && flCreateUpdateInvoiceQuotation == 0) {
            fabAddInvoice.setVisibility(View.GONE);
            fabAddQuotation.setVisibility(View.GONE);
            fabAddItem.setVisibility(View.GONE);
            famAddInvoiceQuotation.setVisibility(View.GONE);
        }
    }

    /**
     * Gets the list of items in invoice/quotation.
     */
    public void getIQList() {
        if (invoiceQuotationList == null) {
            invoiceQuotationList = new ArrayList<>();
        } else {
            invoiceQuotationList.clear();
        }

        updateGlobalList();

        cConectionsBeansArrayList = dao.getDataFromCConnection();
        // this table contains only one value
        if (cConectionsBeansArrayList.size() > 0 && cConectionsBeansArrayList.get(0).getFl_active()) {
            int flag = invoiceQuotation.getFlag();
            if (flag == 1) {
                setBalanceLayoutVisible(true);
            } else {
                setBalanceLayoutVisible(false);
            }
        } else {
            setBalanceLayoutVisible(false);
        }

        invoiceQuotationList.addAll(dao
                .getInvoiceQuotationItemValues(invoiceQuotationId));

        invoicePaymentBalanceAmount = dao.getInvoicePaymentOfReceivedRefunded(invoiceQuotationId);

        if (invoiceQuotationListAdapter == null) {
            invoiceQuotationListAdapter = new InvoiceQuotationListAdapterNew(
                    getActivity(), this, invoiceQuotationList,
                    invoiceQuotation.getFlag(),
                    bundleInvoice.getString("id_interv"),
                    bundleInvoice.getInt("id_client"),
                    bundleInvoice.getInt("id_site"), flCreateUpdateInvoiceQuotation, currencyType);

            listViewInvoiceQuotation.setAdapter(invoiceQuotationListAdapter);
            listViewInvoiceQuotation.setEmptyView(relEmptyView);
        } else {
            invoiceQuotationListAdapter.notifyDataSetChanged();
        }


        setTotalValues();
    }

    private double updateInvoiceTotal() {

        double updatedTaxAmount = 0.0;
        double globalDiscountAmount = 0;
        boolean globalDiscountIsPercent = true;

        globalDiscountAmount = dao.getGlobalDiscountValueInvoice(invoiceQuotationId);
        globalDiscountIsPercent = dao.getGlobalDiscountOptionInvoice(invoiceQuotationId);
        double invoiceSubTotal = dao.getSubTotalTaxInvoice(invoiceQuotationId);
        double invoiceTaxTotal = dao.getTotalTaxInvoice(invoiceQuotationId);

        Double totalWithDiscount = 0.0;

        Double maxVal = invoiceSubTotal + invoiceTaxTotal;
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

    private void updateGlobalList() {
        double invoiceTaxTotal = dao.getTotalTaxInvoice(invoiceQuotationId);
        double invoiceSubTotal = dao.getSubTotalTaxInvoice(invoiceQuotationId);

        double globalDiscount = updateInvoiceTotal();


        ArrayList<GlobalTaxInvoiceList> globalTaxInvoiceLists = new ArrayList<>();
        globalTaxInvoiceLists = dao.getGlobalTaxInvoice(invoiceQuotationId);

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

                dao.updateGlobalTaxInvoice(globalTaxInvoiceLists.get(i).isHasCompound(),
                        globalTaxInvoiceLists.get(i).getIdRemote(), taxValue);

            }
        }

        invoiceTaxTotal = invoiceTaxTotal + globalTaxAmount;
        double totalWithTaxAmt = invoiceTaxTotal + invoiceSubTotal;

        dao.updateInvoiceOrQuotation(
                invoiceQuotationId, invoiceSubTotal, invoiceTaxTotal,
                totalWithTaxAmt, false);
    }

    /**
     * To get the list of tax(Global and part tax)
     */
    private void getTaxList() {
        globalTaxInvoiceLists = new ArrayList<>();
        globalTaxInvoiceLists = dao.getGlobalTaxInvoice(invoiceQuotationId);
    }

    private void setTotalValues() {

        getTaxList();

        double totalWithDiscountRate = 0.00f;
        double taxAmount = 0.00f;
        double totalWithTaxAmt = 0.00f;
//        invoiceQuotationList = dao
//                .getInvoiceQuotationItemValues(invoiceQuotationId);

        //v52 Global calc
        double globalTaxAmount = 0;

        double globalDiscountAmount = 0;
        boolean globalDiscountIsPercent = true;

        globalDiscountAmount = dao.getGlobalDiscountValueInvoice(invoiceQuotationId);
        globalDiscountIsPercent = dao.getGlobalDiscountOptionInvoice(invoiceQuotationId);

        if (globalTaxInvoiceLists != null && globalTaxInvoiceLists.size() > 0) {
            for (int i = 0; i < globalTaxInvoiceLists.size(); i++) {
                double taxValueAmount = globalTaxInvoiceLists.get(i).getTaxValue();
                globalTaxAmount = globalTaxAmount + taxValueAmount;
            }
        }

        ArrayList<GlobalTaxInvoiceList> taxItemInvoiceList = new ArrayList<>();

        if (invoiceQuotationList.size() > 0) {
            for (int i = 0; i < invoiceQuotationList.size(); i++) {
                double total = invoiceQuotationList.get(i).getTotal();
                double taxAmount1 = invoiceQuotationList.get(i).getTaxValue();
                double totWithTax = invoiceQuotationList.get(i)
                        .getTotalWIthTax();

                totalWithDiscountRate = totalWithDiscountRate
                        + total;
                taxAmount = taxAmount + taxAmount1;
                totalWithTaxAmt = totalWithTaxAmt + totWithTax;

                //v52 changes
                if (invoiceQuotationList.get(i).getTaxValue() != 0.00) {
                    taxItemInvoiceList.add(new GlobalTaxInvoiceList(invoiceQuotationList.get(i).getTax(),
                            invoiceQuotationList.get(i).getTaxValue(),
                            invoiceQuotationList.get(i).getTaxName(), false, invoiceQuotationList.get(i).getIdTaxRate(),
                            invoiceQuotationList.get(i).getId()));
                }

            }

            if (taxItemInvoiceList != null && taxItemInvoiceList.size() > 0) {
                globalTaxInvoiceLists.addAll(taxItemInvoiceList);
            }

            if (globalTaxInvoiceLists.size() > 0) {
                invoicePaymentBalanceAmount = totalWithTaxAmt + globalTaxAmount - invoicePaymentBalanceAmount;
            } else {
                invoicePaymentBalanceAmount = totalWithTaxAmt - invoicePaymentBalanceAmount;
            }

            if (invoicePaymentBalanceAmount > 0) {
                txtBalance.setText(toCurrencyFormat(invoicePaymentBalanceAmount) + " " + currencyType);
            } else {
                setBalanceLayoutVisible(false);
            }

            txtSubTotal.setText(String.format(Locale.US, formatDecimal, totalWithDiscountRate) + " " + currencyType);
            if (taxAmount == 0)
                txtTaxValue.setText(String.valueOf("-"));
            else
                txtTaxValue.setText(String.format(Locale.US, formatDecimal, taxAmount) + " " + currencyType);

            if (totalWithTaxAmt == 0)
                txtTotal.setText(String.valueOf("-"));
            else
                txtTotal.setText(String.format(Locale.US, formatDecimal, totalWithTaxAmt) + " " + currencyType);

            //v52 changes
            if (globalTaxInvoiceLists.size() > 0) {

                totalWithTaxAmt = totalWithTaxAmt + globalTaxAmount;

                setListAdapterName();
                taxLbl.setVisibility(View.GONE);
                txtTaxValue.setVisibility(View.GONE);
                if (totalWithTaxAmt == 0)
                    txtTotal.setText(String.valueOf("-"));
                else
                    txtTotal.setText(String.format(Locale.US, formatDecimal, totalWithTaxAmt) + " " + currencyType);

            } else {

                rvTaxListName.setVisibility(View.GONE);
                linTaxList.setVisibility(View.GONE);
                taxLbl.setVisibility(View.VISIBLE);
                txtTaxValue.setVisibility(View.VISIBLE);
            }

            updateDiscountValue(totalWithTaxAmt, globalDiscountIsPercent, globalDiscountAmount);


        } else {
            txtSubTotal.setText("-");
            txtTaxValue.setText(String.valueOf("-"));
            txtTotal.setText(String.valueOf("-"));
            txtGlobalDiscount.setText(String.valueOf("-"));
            rvTaxListName.setVisibility(View.GONE);
            linTaxList.setVisibility(View.GONE);
            setBalanceLayoutVisible(false);
        }


    }

    private void setDeleteTotalValues() {

        getTaxList();

        double totalWithDiscountRate = 0.00f;
        double taxAmount = 0.00f;
        double totalWithTaxAmt = 0.00f;
//        invoiceQuotationList = dao
//                .getInvoiceQuotationItemValues(invoiceQuotationId);

        //v52 Global calc
        double globalTaxAmount = 0;

        double globalDiscountAmount = 0.0;
        boolean globalDiscountIsPercent = true;

//        globalDiscountAmount = dao.getGlobalDiscountValueInvoice(invoiceQuotationId);
        globalDiscountIsPercent = dao.getGlobalDiscountOptionInvoice(invoiceQuotationId);

        if (globalTaxInvoiceLists != null && globalTaxInvoiceLists.size() > 0) {
            for (int i = 0; i < globalTaxInvoiceLists.size(); i++) {
                double taxValueAmount = globalTaxInvoiceLists.get(i).getTaxValue();
                globalTaxAmount = globalTaxAmount + taxValueAmount;
            }
        }

        ArrayList<GlobalTaxInvoiceList> taxItemInvoiceList = new ArrayList<>();

        if (invoiceQuotationList.size() > 0) {
            for (int i = 0; i < invoiceQuotationList.size(); i++) {
                double total = invoiceQuotationList.get(i).getTotal();
                double taxAmount1 = invoiceQuotationList.get(i).getTaxValue();
                double totWithTax = invoiceQuotationList.get(i)
                        .getTotalWIthTax();

                totalWithDiscountRate = totalWithDiscountRate
                        + total;
                taxAmount = taxAmount + taxAmount1;
                totalWithTaxAmt = totalWithTaxAmt + totWithTax;

                //v52 changes
                if (invoiceQuotationList.get(i).getTaxValue() != 0.00) {
                    taxItemInvoiceList.add(new GlobalTaxInvoiceList(invoiceQuotationList.get(i).getTax(),
                            invoiceQuotationList.get(i).getTaxValue(),
                            invoiceQuotationList.get(i).getTaxName(), false, invoiceQuotationList.get(i).getIdTaxRate(),
                            invoiceQuotationList.get(i).getId()));
                }

            }

            if (taxItemInvoiceList != null && taxItemInvoiceList.size() > 0) {
                globalTaxInvoiceLists.addAll(taxItemInvoiceList);
            }

            if (globalTaxInvoiceLists.size() > 0) {
                invoicePaymentBalanceAmount = totalWithTaxAmt + globalTaxAmount - invoicePaymentBalanceAmount;
            } else {
                invoicePaymentBalanceAmount = totalWithTaxAmt - invoicePaymentBalanceAmount;
            }

            if (invoicePaymentBalanceAmount > 0) {
                txtBalance.setText(toCurrencyFormat(invoicePaymentBalanceAmount) + " " + currencyType);
            } else {
                setBalanceLayoutVisible(false);
            }

            txtSubTotal.setText(String.format(Locale.US, formatDecimal, totalWithDiscountRate) + " " + currencyType);
            if (taxAmount == 0)
                txtTaxValue.setText(String.valueOf("-"));
            else
                txtTaxValue.setText(String.format(Locale.US, formatDecimal, taxAmount) + " " + currencyType);

            if (totalWithTaxAmt == 0)
                txtTotal.setText(String.valueOf("-"));
            else
                txtTotal.setText(String.format(Locale.US, formatDecimal, totalWithTaxAmt) + " " + currencyType);

            //v52 changes
            if (globalTaxInvoiceLists.size() > 0) {

                totalWithTaxAmt = totalWithTaxAmt + globalTaxAmount;

                setListAdapterName();
                taxLbl.setVisibility(View.GONE);
                txtTaxValue.setVisibility(View.GONE);
                if (totalWithTaxAmt == 0)
                    txtTotal.setText(String.valueOf("-"));
                else
                    txtTotal.setText(String.format(Locale.US, formatDecimal, totalWithTaxAmt) + " " + currencyType);

            } else {

                rvTaxListName.setVisibility(View.GONE);
                linTaxList.setVisibility(View.GONE);
                taxLbl.setVisibility(View.VISIBLE);
                txtTaxValue.setVisibility(View.VISIBLE);
            }

            updateDiscountValue(totalWithTaxAmt, globalDiscountIsPercent, globalDiscountAmount);


        } else {
            txtSubTotal.setText("-");
            txtTaxValue.setText(String.valueOf("-"));
            txtTotal.setText(String.valueOf("-"));
            txtGlobalDiscount.setText(String.valueOf("-"));
            rvTaxListName.setVisibility(View.GONE);
            linTaxList.setVisibility(View.GONE);
            setBalanceLayoutVisible(false);
        }


    }

    private void updateDiscountValue(double totalTaxAmt, boolean isPercentage, double globalDiscountAmount) {
        double invoiceTaxTotal = dao.getTotalTaxInvoice(invoiceQuotationId);
        double invoiceSubTotal = dao.getSubTotalTaxInvoice(invoiceQuotationId);

        double totalAmt = invoiceTaxTotal + invoiceSubTotal;


        Double maxVal = invoiceSubTotal + invoiceTaxTotal;
        String discountVal = "" + globalDiscountAmount;
        Double totalWithDiscount = 0.0;

        if (maxVal != null && maxVal >= 0) {

            if (discountVal != null && discountVal.trim().length() > 0)
                if (isPercentage) {
                    if (Double.parseDouble(discountVal) > 100) {
                        discountVal = "100";
                    }
                } else {
                    if (Double.parseDouble(discountVal) > maxVal) {
                        discountVal = "0";
                    }
                }
        } else {
            if (!isPercentage) {
                discountVal = "0";
            }
        }

        Double discountValue = Double.parseDouble(discountVal);
        double totalWithoutDisc = 0.0;

        if (isPercentage) {
            totalWithoutDisc = (invoiceSubTotal * discountValue) / 100;
        } else {
            totalWithoutDisc = discountValue;
        }

        totalTaxAmt = totalTaxAmt - totalWithoutDisc;

        boolean drp = dao.updateInvoiceOrQuotationGlobalDisc(
                invoiceQuotationId, discountValue, totalTaxAmt,
                isPercentage);


        if (totalTaxAmt == 0)
            txtTotal.setText(String.valueOf("-"));
        else
            txtTotal.setText(String.format(Locale.US, formatDecimal, totalTaxAmt) + " " + currencyType);


//        txtGlobalDiscount.setText(String.format(Locale.US, formatDecimal, totalWithoutDisc) + " " + currencyType);

        if (discountValue == 0) {
            txtGlobalDiscount.setText(String.valueOf("-"));
        } else {
            if (isPercentage)
                txtGlobalDiscount.setText(String.format(Locale.US, formatDecimal, discountValue) + " " + "%");
            else
                txtGlobalDiscount.setText(String.format(Locale.US, formatDecimal, discountValue) + " " + currencyType);
        }
    }

    /**
     * Deletes the item of an invoice/quotation.
     */
    private void deleteInvoiceQuotationItem() {
        for (int i = 0; i < invoiceQuotationList.size(); i++) {
            dao.deleteInvoiceOrQuotationItem(invoiceQuotationList
                    .get(i).getId());
        }
    }

    /**
     * Sets the title for invoice or quuotation.
     * And sets visibility for delte button.
     */
    private void setTitleAndDeleteBtn() {
        String title;
        if (invoiceQuotation.getFlag() == 0) {
            title = getActivity().getString(R.string.txt_label_quotation);
            btn_cnv_invoice.setVisibility(View.VISIBLE);
            if (invoiceQuotation.getNumberOfIQ() != 0) {
                title += " #" + invoiceQuotation.getNumberOfIQ();
                invoiceQuotationNumber = invoiceQuotation.getNumberOfIQ();
                txtDelete.setVisibility(View.GONE);
                linPdf.setVisibility(View.VISIBLE);
            }
            isInvoice = false;
        } else {
            title = getActivity().getString(R.string.txt_label_invoice);
            btn_cnv_invoice.setVisibility(View.GONE);
            if (invoiceQuotation.getNumberOfIQ() != 0) {
                title += " #" + invoiceQuotation.getNumberOfIQ();
                invoiceQuotationNumber = invoiceQuotation.getNumberOfIQ();
                txtDelete.setVisibility(View.GONE);
                linPdf.setVisibility(View.VISIBLE);
            }
            isInvoice = true;
        }
        txtTitle.setText(title);

//        if (invoiceQuotationNumber != 0) {
//            linPdf.setVisibility(View.VISIBLE);
//        } else {
//            linPdf.setVisibility(View.GONE);
//        }

    }

    /**
     * Hide/Show function for strict invoice true
     */
    private void strictInvoiceEnabled(boolean isEnabled, int invoiceQuotationNumber) {

        if (invoiceQuotation.isStrictInvoice() &&
                invoiceQuotation.getNumberOfIQ() > 0)
            enableEdit = false;
        else
            enableEdit = true;

        if (invoiceQuotation.getFlag() == 0) {
            enableEdit = true;
        }
        if (isEnabled && invoiceQuotationNumber > 0) {

            linPdf.setVisibility(View.VISIBLE);
            txt_draft_title.setVisibility(View.GONE);
            btn_save_new_invoice.setVisibility(View.GONE);
            fabAddItem.setVisibility(View.GONE);

            if (invoiceQuotation.getFlag() == 0)
                fabAddItem.setVisibility(View.VISIBLE);


        } else if (isEnabled && invoiceQuotationNumber <= 0) {

            linPdf.setVisibility(View.GONE);
            txt_draft_title.setVisibility(View.VISIBLE);
            btn_save_new_invoice.setVisibility(View.VISIBLE);
            fabAddItem.setVisibility(View.VISIBLE);
            btnPayNow.setVisibility(View.GONE);

        } else {
            txt_draft_title.setVisibility(View.VISIBLE);
            if (invoiceQuotationNumber > 0)
                txt_draft_title.setVisibility(View.GONE);

            btn_save_new_invoice.setVisibility(View.VISIBLE);
            fabAddItem.setVisibility(View.VISIBLE);

            if (invoiceQuotationNumber <= 0) {
                btnPayNow.setVisibility(View.GONE);
            } else {
                btn_save_new_invoice.setVisibility(View.GONE);
            }

        }

    }
    /**
     * Deletes the whole invoice/quotation.
     */
    private void deleteInvoiceQuotation() {
        dao.deleteInvoiceOrQuotation(invoiceQuotationId);
    }

    /**
     * Event listener for updating UI after sync
     *
     * @param updateUiAfterSync
     */
    public void onEvent(UpdateUiAfterSync updateUiAfterSync) {
        refreshList();
    }

    /**
     * Refreshes UI.
     */
    public void refreshList() {
        getInvoiceOrQuotaion();
        setTitleAndDeleteBtn();
        getIQList();
    }

    // *************************************LOCAL...METHODS...ENDS...HERE*********************************************

    // .....................................LISTENER...STARTS...HERE..................................................
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_delete_invoice_quotation:
                //TODO circular reveal for delete button.
                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {
                    DeleteInvoiceQuotationDialog deleteIQDialog = new DeleteInvoiceQuotationDialog(
                            jobDetails, isInvoice,
                            new DeleteInvoiceQuotationDialog.DeleteInvoiceQuotationInterface() {

                                @Override
                                public void doOnYesClick() {
                                    deleteInvoiceQuotationItem();
                                    deleteInvoiceQuotation();
                                    deleteGlobalTaxItems();

//                                    updateValuesDB();
                                    getInvoiceOrQuotaion();
                                    getIQList();
                                    changeItemsVisibility();
                                    strictInvoiceEnabled(invoiceQuotation.isStrictInvoice(),
                                            invoiceQuotation.getNumberOfIQ());
                                    EventBus.getDefault().post(new AddInvoiceQuotationEvent(" ", false));
                                    EventBus.getDefault().post(new UpdateInvoiceQuotationTaxEvent());

                                }

                                @Override
                                public void doOnNoClick() {

                                }
                            });

                    deleteIQDialog.show();
                }
                break;
            case R.id.addInvoice:
                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED && idClient > 0) {
                    Intent intentAddInvoice = new Intent();
                    intentAddInvoice.setClass(getActivity(),
                            AddInvoiceQuotationNew.class);
                    intentAddInvoice.putExtra("id_interv",
                            bundleInvoice.getString("id_interv"));
                    intentAddInvoice.putExtra("id_client",
                            bundleInvoice.getInt("id_client"));
                    intentAddInvoice.putExtra("id_site",
                            bundleInvoice.getInt("id_site"));
                    intentAddInvoice.putExtra("flag", 1);
                    intentAddInvoice.putExtra("invoice_list", invoiceQuotation);
                    intentAddInvoice.putExtra("currency_type", currencyType);
                    startActivity(intentAddInvoice);
                }
                famAddInvoiceQuotation.collapse();
                break;
            case R.id.addQuotation:
                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED && idClient > 0) {
                    Intent intentAddQuotation = new Intent();
                    intentAddQuotation.setClass(getActivity(),
                            AddInvoiceQuotationNew.class);
                    intentAddQuotation.putExtra("id_interv",
                            bundleInvoice.getString("id_interv"));
                    intentAddQuotation.putExtra("id_client",
                            bundleInvoice.getInt("id_client"));
                    intentAddQuotation.putExtra("id_site",
                            bundleInvoice.getInt("id_site"));
                    intentAddQuotation.putExtra("flag", 0);
                    intentAddQuotation.putExtra("invoice_list", invoiceQuotation);
                    intentAddQuotation.putExtra("currency_type", currencyType);
                    startActivity(intentAddQuotation);
                }
                famAddInvoiceQuotation.collapse();
                break;
            case R.id.addItem:
                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {
                    if (idClient > 0) {
                        int flag = invoiceQuotation.getFlag();
                        if (flag == 0) {
                            Intent intentAddQuotation1 = new Intent();
                            intentAddQuotation1.setClass(getActivity(),
                                    AddInvoiceQuotationNew.class);
                            intentAddQuotation1.putExtra("id_interv",
                                    bundleInvoice.getString("id_interv"));
                            intentAddQuotation1.putExtra("id_client",
                                    bundleInvoice.getInt("id_client"));
                            intentAddQuotation1.putExtra("id_site",
                                    bundleInvoice.getInt("id_site"));
                            intentAddQuotation1.putExtra("flag", 0);
                            if (invoiceQuotationList.size() > 0)
                                order = invoiceQuotationList.get(
                                        invoiceQuotationList.size() - 1).getOrder();
                            intentAddQuotation1.putExtra("invoice_list", invoiceQuotation);
                            intentAddQuotation1.putExtra("order", order);
                            intentAddQuotation1.putExtra("currency_type", currencyType);
                            startActivity(intentAddQuotation1);
                        } else {
                            Intent intentAddInvoice1 = new Intent();
                            intentAddInvoice1.setClass(getActivity(),
                                    AddInvoiceQuotationNew.class);
                            intentAddInvoice1.putExtra("id_interv",
                                    bundleInvoice.getString("id_interv"));
                            intentAddInvoice1.putExtra("id_client",
                                    bundleInvoice.getInt("id_client"));
                            intentAddInvoice1.putExtra("id_site",
                                    bundleInvoice.getInt("id_site"));
                            intentAddInvoice1.putExtra("flag", 1);
                            if (invoiceQuotationList.size() > 0)
                                order = invoiceQuotationList.get(
                                        invoiceQuotationList.size() - 1).getOrder();
                            intentAddInvoice1.putExtra("invoice_list", invoiceQuotation);
                            intentAddInvoice1.putExtra("order", order);
                            intentAddInvoice1.putExtra("currency_type", currencyType);
                            startActivity(intentAddInvoice1);
                        }
                    }else {
                        Toast.makeText(getActivity(), R.string.client_error_msg, Toast.LENGTH_SHORT)
                                .show();
                    }
                }

                break;

            case R.id.btn_pay_now:

                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {
                    String stripePublishableKey = null;
                    try {
                        JSONObject jsonAuthObject = new JSONObject(cConectionsBeansArrayList.get(0).getJsonSettings());
                        stripePublishableKey = jsonAuthObject.getString("apiKeyPublic");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    synch(stripePublishableKey, currencyType);
                }
                break;

            case R.id.btn_cnv_invoice:
                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {

                    ConvertToInvoiceDialog deleteIQDialog = new ConvertToInvoiceDialog(
                            jobDetails, isInvoice,
                            new ConvertToInvoiceDialog.ConvertToInvoiceDialogInterface() {

                                @Override
                                public void doOnYesClick() {

                                    new ConvertAddInvoiceOrQuotation().execute();
                                }

                                @Override
                                public void doOnNoClick() {

                                }
                            });

                    deleteIQDialog.show();
                }
                break;

            case R.id.linPdf:
//                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED||
//                        jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__COMPLETE)
                    generatePdfLogic();

                break;
            case R.id.linTaxList:
                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED)
                    showTaxListDialog();
            case R.id.linTaxItem:
                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED)
                    showTaxListDialog();
                break;
            case R.id.linGlobalDiscountItem:
                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED)
                    showGlobalListDialog();
                break;
            case R.id.btn_save_new_invoice:
                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {

                    if (dao.updateFacturesInvoice(invoiceQuotationId, -1)) {
                        int value = dao.getInvoiceNoFactures(invoiceQuotationId);
                        if (value == -1)
                            new SynchronizeAsyncTask(invoiceQuotationId).execute();
                        else
                            Toast.makeText(getActivity(), getString(R.string.msg_error), Toast.LENGTH_LONG)
                                    .show();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.msg_error), Toast.LENGTH_LONG)
                                .show();
                    }
                }

                break;


        }
    }

    private void deleteGlobalTaxItems() {
        dao.deleteGlobalTaxInvoice(invoiceQuotationId);
    }

    public void updateValuesDB() {
        ArrayList<Invoice_Quotation_Items_Beans> invoiceQuotationList = (ArrayList<Invoice_Quotation_Items_Beans>) dao
                .getInvoiceQuotationItemValues(invoiceQuotationId);
        double totalWithDiscountRate = 0, taxAmount = 0, totalWithTaxAmt = 0;
        for (int i = 0; i < invoiceQuotationList.size(); i++) {
            double total = invoiceQuotationList.get(i).getTotal();
            double taxAmount1 = invoiceQuotationList.get(i).getTaxValue();
            double totWithTax = invoiceQuotationList.get(i)
                    .getTotalWIthTax();

            totalWithDiscountRate = totalWithDiscountRate + total;
            taxAmount = taxAmount + taxAmount1;
            totalWithTaxAmt = totalWithTaxAmt + totWithTax;
        }
        boolean drp = dao.updateInvoiceOrQuotation(
                invoiceQuotation.getId(), totalWithDiscountRate, taxAmount,
                totalWithTaxAmt, true);
    }

    // *************************************LISTENER...ENDS...HERE*****************************************************


    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

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


            boolean paymentResult = data.getBooleanExtra(CreditCardUtils.EXTRA_PAYMENT_SUCCESS, false);
            String message = data.getStringExtra(CreditCardUtils.EXTRA_PAYMENT_RESULT_VALUE);

            if (paymentResult) {
                String ccToken = data.getStringExtra(CreditCardUtils.EXTRA_KEY_STRIPE_TOKEN);
                String amount = data.getStringExtra(CreditCardUtils.EXTRA_BALANCE_AMOUNT);
                String ccEmail = data.getStringExtra(CreditCardUtils.EXTRA_CC_EMAIL);
                String paymentId = data.getStringExtra(CreditCardUtils.EXTRA_PAYMENT_INTENT_ID);
                String currencyCode = dao.getDeviseCustomerCurrencyCode();
                int amountInCents = (int) (Float.parseFloat(amount) * 100);
                if (SharedPref.getUrlStripeServer(getActivity()) != null && SharedPref.getUrlStripeServer(getActivity()).length() > 0) {
//                hitPaymentService(accessToken, idCustomer, amountInCents, currencyCode, ccToken, ccEmail, null, invoiceQuotationId);

                    hitPaymentServiceUpdated(idCustomer, amountInCents, ccEmail,
                            null, invoiceQuotationId, paymentId);
                }
            } else {
                InvoicePaymentTransactionResultDialog dialog = new InvoicePaymentTransactionResultDialog(getActivity(),
                        "0", message, null);
                dialog.show(getActivity().getSupportFragmentManager(), "");
            }
        }
    }

    private void hitPaymentServiceUpdated(int idCustomer, int amountInCents, String ccEmail,
                                          String idInvoive, String idRemoteInvoice, String paymentId) {

        String amountOriginal = toCurrencyFormat(Float.parseFloat(String.valueOf(amountInCents)) / 100);

        String tPaymentUniqueId = dao.insertTPaymentsTable(idRemoteInvoice, amountOriginal, paymentId);
        dao.insertTPaymentsLog(tPaymentUniqueId, idRemoteInvoice, amountOriginal, paymentId);

        getIQList();


        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(),
                getString(R.string.textPleaseWaitLable),
                getString(R.string.chargement), true, false);

        ApiInterface apiService =
                Apiclient.getClient().create(ApiInterface.class);

        String url = SharedPref.getUrlStripeServer(getActivity());

        url = url + "/Confirm";

        Call<PaymentServiceModel> call = apiService.paymentServiceAfter(url, idCustomer, amountInCents,
                ccEmail, idInvoive, idRemoteInvoice, paymentId);

        call.enqueue(new Callback<PaymentServiceModel>() {
            @Override
            public void onResponse(Call<PaymentServiceModel> call, Response<PaymentServiceModel> response) {

                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();

                String status = response.body().getStatus();
                String msg = response.body().getMsg();


                if (status.equals("1")) {

                    getIQList();

                    InvoicePaymentTransactionResultDialog successDialog = new InvoicePaymentTransactionResultDialog(getActivity(), status, msg, amountOriginal + " " + currencyType);
                    successDialog.show(getActivity().getSupportFragmentManager(), "");

                } else {
                    InvoicePaymentTransactionResultDialog dialog = new InvoicePaymentTransactionResultDialog(getActivity(), status, msg, null);
                    dialog.show(getActivity().getSupportFragmentManager(), "");
                }

            }

            @Override
            public void onFailure(Call<PaymentServiceModel> call, Throwable t) {
                Logger.log("Payment", "Retrofit failure :" + t);

                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();

                InvoicePaymentTransactionResultDialog dialog = new InvoicePaymentTransactionResultDialog(getActivity(), "0", getString(R.string.msg_error), null);
                dialog.show(getActivity().getSupportFragmentManager(), "");
            }

        });


    }

    private void hitPaymentService(String token, int idCustomer, final int amountInCents,
                                   final String currency, String ccToken, String ccEmail, String idInvoive,
                                   final String idRemoteInvoice) {

        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(),
                getString(R.string.textPleaseWaitLable),
                getString(R.string.chargement), true, false);

        ApiInterface apiService =
                Apiclient.getClient().create(ApiInterface.class);

        String url = SharedPref.getUrlStripeServer(getActivity());

        Call<PaymentServiceModel> call = apiService.paymentService(url, token, idCustomer, amountInCents, currency, ccToken, ccEmail, idInvoive, idRemoteInvoice);
        call.enqueue(new Callback<PaymentServiceModel>() {
            @Override
            public void onResponse(Call<PaymentServiceModel> call, Response<PaymentServiceModel> response) {

                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();

                String status = response.body().getStatus();
                String msg = response.body().getMsg();
                String chargeId = response.body().getChargeId();

                if (status.equals("1")) {

                    String amountOriginal = toCurrencyFormat(Float.parseFloat(String.valueOf(amountInCents)) / 100);

                    String tPaymentUniqueId = dao.insertTPaymentsTable(idRemoteInvoice, amountOriginal, chargeId);
                    dao.insertTPaymentsLog(tPaymentUniqueId, idRemoteInvoice, amountOriginal, chargeId);

                    getIQList();

                    InvoicePaymentTransactionResultDialog successDialog = new InvoicePaymentTransactionResultDialog(getActivity(), status, msg, amountOriginal + " " + currencyType);
                    successDialog.show(getActivity().getSupportFragmentManager(), "");

                } else {
                    InvoicePaymentTransactionResultDialog dialog = new InvoicePaymentTransactionResultDialog(getActivity(), status, msg, null);
                    dialog.show(getActivity().getSupportFragmentManager(), "");
                }

            }

            @Override
            public void onFailure(Call<PaymentServiceModel> call, Throwable t) {
                Logger.log("Payment", "Retrofit failure :" + t);

                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();

                InvoicePaymentTransactionResultDialog dialog = new InvoicePaymentTransactionResultDialog(getActivity(), "0", getString(R.string.msg_error), null);
                dialog.show(getActivity().getSupportFragmentManager(), "");
            }

        });
    }

    private void setBalanceLayoutVisible(Boolean visible) {
        if (invoiceQuotation != null)
        if (visible) {
            if (invoiceQuotation.getNumberOfIQ() <= 0) {
                btnPayNow.setVisibility(View.GONE);
                Logger.log(TAG, "VALUE FOR BALANCE LAYOUT IS ===" + invoiceQuotation.getNumberOfIQ() + " " + visible);
            } else {
                btnPayNow.setVisibility(View.VISIBLE);
            }
            labelBal.setVisibility(View.VISIBLE);
            txtBalance.setVisibility(View.VISIBLE);
        } else {
            btnPayNow.setVisibility(View.GONE);
            labelBal.setVisibility(View.GONE);
            txtBalance.setVisibility(View.GONE);
        }
    }

    /**
     * Synch.
     */
    private void synch(final String stripePublishableKey, final String currencyType) {

        if (SynchroteamUitls.isNetworkAvailable(getActivity())) {
            progressDSynch = ProgressDialog.show(getActivity(),
                    getString(R.string.textPleaseWaitLable),
                    getString(R.string.msg_synch), true, false);
            Logger.output(TAG, " thread started");

            Thread syncDbToServer = new Thread((new Runnable() {

                @Override
                public void run() {

                    Message myMessage = new Message();
                    try {
                        User u = dao.getUser();
                        dao.sync(u.getLogin(), u.getPwd());
                        Logger.output(TAG, " finished sync");
                        Thread.sleep(3000);
                        myMessage.obj = "ok";

                        if (stripePublishableKey != null && currencyType != null) {

                            Bundle bundle = new Bundle();
                            bundle.putString(KEY_STRIPE_PUBLISHABLE_KEY, stripePublishableKey);
                            bundle.putString(KEY_CURRENCY, currencyType);
                            myMessage.setData(bundle);

                        }

                        handler.sendMessage(myMessage);

                    } catch (Exception ex) {
                        String exception = ex.getMessage();
                        Logger.printException(ex);
                        if (exception != null) {
                            if (exception.indexOf("4001") != -1) {
                                myMessage.obj = "4001";
                            } else if (exception.indexOf("4000") != -1) {
                                myMessage.obj = "4000";
                            } else if (exception.indexOf("4006") != -1) {
                                myMessage.obj = "4006";
                            } else if (exception.indexOf("4101") != -1) {
                                myMessage.obj = "4101";
                            } else if (exception.indexOf("4005") != -1) {
                                myMessage.obj = "4005";
                            } else if (exception.indexOf("4003") != -1) {
                                myMessage.obj = "4003";
                            } else {
                                myMessage.obj = "Error";
                            }
                        } else {
                            myMessage.obj = "Error";
                        }

                        handler.sendMessage(myMessage);

                    } finally {
                        if (progressDSynch != null
                                && progressDSynch.isShowing())
                            progressDSynch.dismiss();

                    }

                }
            }));
            syncDbToServer.start();
        } else {

            EventBus.getDefault().post(new UpdateDataBaseEvent());
            if (!getActivity().isFinishing()) {
                SynchroteamUitls.showToastMessage(getActivity());
            }
        }

    }

    /**
     * The handler.
     */
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            String erreur = (String) msg.obj;
            EventBus.getDefault().post(new UpdateDataBaseEvent());

            if (erreur.equals("ok")) {
                Toast.makeText(getActivity(),
                        getString(R.string.msg_synch_ok), Toast.LENGTH_LONG)
                        .show();

                Bundle bundle = msg.getData();

                if (bundle != null) {

                    // update the UI after Sync
                    onResume();

                    String stripePublishableKey = bundle.getString(KEY_STRIPE_PUBLISHABLE_KEY);
                    String currencyType = bundle.getString(KEY_CURRENCY);

                    InvoiceQuotationPaynowConfirmDialog confirmDialog = new InvoiceQuotationPaynowConfirmDialog(getActivity(),
                            InvoiceQuotationFragmentNew.this, invoicePaymentBalanceAmount, currencyType, stripePublishableKey, invoiceQuotationId);
                    confirmDialog.show();
                }

                EventBus.getDefault().post(new UpdateUiAfterSync());

//                jobPublicLink = dao.getInterventionById(idJob).getPublicLinkInterv();
//                String downloadLink = jobPublicLink + "/PDF";
//                openLinkInBrowser(downloadLink);

            } else if (erreur.equals("4001") || erreur.equals("4000")) {
                showAuthErrDialog();
            } else if (erreur.equals("4006")) {
                Toast.makeText(getActivity(),
                        getString(R.string.msg_synch_error_4),
                        Toast.LENGTH_LONG).show();
            } else if (erreur.equals("4101")) {
                showMultipleDeviecDialog();
            } else if (erreur.equals("4005")) {
                showAppUpdatetDialog();
            } else if (erreur.equals("4003")) {
                showErrMsgDialog(getString(R.string.msg_sync_error_4003));
            } else {
//                Toast.makeText(getActivity(),
//                        getString(R.string.msg_synch_error_new), Toast.LENGTH_LONG)
//                        .show();
                showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));
            }

        }
    };


    /**
     * Show error dialog
     */
    protected void showErrMsgDialog(String errMsg) {

        ErrorDialog errDialog = new ErrorDialog(getActivity(), errMsg);

        errDialog.show();
    }

    /**
     * For showing the synchronization failure messages
     */
    protected void showSyncFailureMsgDialog(String syncFailureMsg) {

        SynchronizationErrorDialog synchronizationErrorDialog = new SynchronizationErrorDialog
                (getActivity(), syncFailureMsg, new SynchronizationErrorDialog
                        .SynchronizationErrorInterface() {
                    @Override
                    public void doOnOkayClick() {
                        //perform any action
                    }
                });

        synchronizationErrorDialog.show();
    }


    /**
     * Show authentication error dialog
     */
    protected void showAuthErrDialog() {
        User user = dao.getUser();
        AuthenticationErrorDialog authenticationErrorDialog = new AuthenticationErrorDialog(
                getActivity(), user.getLogin());

        authenticationErrorDialog.show();
    }

    /**
     * Show app update dialog
     */
    protected void showAppUpdatetDialog() {

        AppUpdateDialog appUpdateDialog = new AppUpdateDialog(getActivity());

        appUpdateDialog.show();
    }


    /**
     * Show multiple user dialog
     */
    protected void showMultipleDeviecDialog() {

        MultipleDeviceNotSupported multipleDeviceDialog = new MultipleDeviceNotSupported(
                getActivity(),
                new MultipleDeviceNotSupported.MultipleDeviceInterface() {

                    @Override
                    public void doOnOkClick() {
                    }

                    @Override
                    public void doOnLinkClick() {
                        if (Locale.getDefault().getLanguage()
                                .equalsIgnoreCase("fr")) {
                            openLinkInBrowser(getString(R.string.txtInfoFr));
                        } else if (Locale.getDefault().getLanguage()
                                .equalsIgnoreCase("es")) {
                            openLinkInBrowser(getString(R.string.txtInfoEs));
                        }else {
                            openLinkInBrowser(getString(R.string.txtInfoEn));
                        }
                    }
                });
        multipleDeviceDialog.show();
    }


    /***
     * Create a chooser of browsers to open the link
     *
     * @param link
     */
    protected void openLinkInBrowser(String link) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(link));
        Bundle bundle = new Bundle();

        //For adding the header token
//        AuthData getAuthandExpriryToken = dao.getUserToken();
//        if(getAuthandExpriryToken!=null)
//        bundle.putString("Authorization", getAuthandExpriryToken.getAuth());
//        intent.putExtra(Browser.EXTRA_HEADERS, bundle);

        // "Share this photo with"
        String title = getString(R.string.titleBrowserSelection);
        // Create and start the chooser
        Intent chooser = Intent.createChooser(intent, title);
        startActivity(chooser);
    }

    private String toCurrencyFormat(float amountf) {
        return String.format(Locale.US, formatDecimal, amountf);
    }

    private String toCurrencyFormat(double amountf) {
        return String.format(Locale.US, formatDecimal, amountf);
    }


    /**
     * For generating the pdf for invoices/quotation
     */
    private void generatePdfLogic() {

        synchPdf();
    }

    /**
     * On event.
     *
     * @param event :
     */
    public void onEvent(UpdateInvoiceQuotationTaxEvent event) {
        updatedCalculation();
    }


    private void getUpdatedInvoiceOrQuotation() {
        ArrayList<Invoice_Quotation_Beans> invoiceList = new ArrayList<>();
        invoiceList = dao.getInvoiceQuotationDetailsList(bundleInvoice.getString("id_interv"));
        if (invoiceList != null && invoiceList.size() > 0) {
            if (invoiceQuotationId != null &&
                    invoiceQuotationId.length() > 1) {
                for (int i = 0; i < invoiceList.size(); i++) {
                    if (invoiceList.get(i).getId().trim().equals(invoiceQuotationId.trim())) {
                        invoiceQuotationNumber = invoiceList.get(i).getNumberOfIQ();
                        break;
                    }
                }
            }
        }
    }

    /**
     * Synch.
     */
    private void synchPdf() {

        if (SynchroteamUitls.isNetworkAvailable(getActivity())) {
            progressDSynch = ProgressDialog.show(getActivity(),
                    getString(R.string.textPleaseWaitLable),
                    getString(R.string.msg_synch), true, false);
            Logger.output(TAG, " thread started");

            Thread syncDbToServer = new Thread((new Runnable() {

                @Override
                public void run() {

                    Message myMessage = new Message();
                    try {
                        User u = dao.getUser();
                        dao.sync(u.getLogin(), u.getPwd());
                        Logger.output(TAG, " finished sync");
                        Thread.sleep(3000);
                        myMessage.obj = "ok";

                        handlerPdf.sendMessage(myMessage);

                    } catch (Exception ex) {
                        String exception = ex.getMessage();
                        Logger.printException(ex);
                        if (exception != null) {
                            if (exception.indexOf("4001") != -1) {
                                myMessage.obj = "4001";
                            } else if (exception.indexOf("4000") != -1) {
                                myMessage.obj = "4000";
                            } else if (exception.indexOf("4006") != -1) {
                                myMessage.obj = "4006";
                            } else if (exception.indexOf("4101") != -1) {
                                myMessage.obj = "4101";
                            } else if (exception.indexOf("4005") != -1) {
                                myMessage.obj = "4005";
                            } else if (exception.indexOf("4003") != -1) {
                                myMessage.obj = "4003";
                            } else {
                                myMessage.obj = "Error";
                            }
                        } else {
                            myMessage.obj = "Error";
                        }

                        handlerPdf.sendMessage(myMessage);

                    } finally {
                        if (progressDSynch != null
                                && progressDSynch.isShowing())
                            progressDSynch.dismiss();

                    }

                }
            }));
            syncDbToServer.start();
        } else {

            EventBus.getDefault().post(new UpdateDataBaseEvent());
            if (!getActivity().isFinishing()) {
                SynchroteamUitls.showToastMessage(getActivity());
            }
        }

    }

    /**
     * The handler.
     */
    private Handler handlerPdf = new Handler() {
        public void handleMessage(Message msg) {
            String erreur = (String) msg.obj;
            EventBus.getDefault().post(new UpdateDataBaseEvent());

            if (erreur.equals("ok")) {
                Toast.makeText(getActivity(),
                        getString(R.string.msg_synch_ok), Toast.LENGTH_LONG)
                        .show();


                EventBus.getDefault().post(new UpdateUiAfterSync());

                getUpdatedInvoiceOrQuotation();

                if (invoiceQuotationNumber != 0) {

                    if (SharedPref.getUrlBaseServer(getActivity()) != null &&
                            SharedPref.getUrlBaseServer(getActivity()).length() > 0) {
                        pdfPublicLink = SharedPref.getUrlBaseServer(getActivity());
                        String domain = dao.getUserDomain();
                        pdfPublicLink = pdfPublicLink.replaceFirst("xxxxx", domain);
                        Logger.log(TAG, "pdfPublicLink =====>" + pdfPublicLink);


                        pdfPublicLink = pdfPublicLink + "/Invoices/PublicInvoice/" + invoiceQuotationNumber + "/" + invoiceQuotationId;
                        String downloadLink = pdfPublicLink + "/PDF";
                        openLinkInBrowser(downloadLink);

                    }

                    //update UI after synch
                    EventBus.getDefault().post(new AddInvoiceQuotationEvent(invoiceQuotationId, true));

                }

            } else if (erreur.equals("4001") || erreur.equals("4000")) {
                showAuthErrDialog();
            } else if (erreur.equals("4006")) {
                Toast.makeText(getActivity(),
                        getString(R.string.msg_synch_error_4),
                        Toast.LENGTH_LONG).show();
            } else if (erreur.equals("4101")) {
                showMultipleDeviecDialog();
            } else if (erreur.equals("4005")) {
                showAppUpdatetDialog();
            } else if (erreur.equals("4003")) {
                showErrMsgDialog(getString(R.string.msg_sync_error_4003));
            } else {
//                Toast.makeText(getActivity(),
//                        getString(R.string.msg_synch_error_new), Toast.LENGTH_LONG)
//                        .show();
                showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));
            }

        }
    };

    // -----------------------------------V52_CHANGES-------------------------------------------------

    public void showTaxListDialog() {
        if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {
            if (enableEdit) {
                TaxListDialog taxListDialog = TaxListDialog.getInstance(invoiceQuotationId);
                taxListDialog.show(getFragmentManager(), "");
            }
        }
    }

    public void showGlobalListDialog() {
        if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {
            if (enableEdit) {
                GlobalDiscountDialog globalDiscountDialog = GlobalDiscountDialog.getInstance(invoiceQuotationId,
                        new GlobalDiscountDialog.GlobalDiscountListener() {
                            @Override
                            public void doOnConfirmClick(String disValue, boolean disOption, Double totalWithDiscount) {
                                Double totalWithDiscountRate = Double.parseDouble(disValue);


//                            txtGlobalDiscount.setText(String.format(Locale.US, formatDecimal, totalWithDiscount)
//                                    + " " + currencyType);

                                if (disOption)
                                    txtGlobalDiscount.setText(String.format(Locale.US, formatDecimal, totalWithDiscountRate) + " " + "%");
                                else
                                    txtGlobalDiscount.setText(String.format(Locale.US, formatDecimal, totalWithDiscountRate) + " " + currencyType);

                                updateGlobalList();
                                setTotalValues();

                            }

                            @Override
                            public void doOnCancelClick() {

                            }
                        });
                globalDiscountDialog.show(getFragmentManager(), "");
            }
        }
    }

    private void updatedCalculation() {

        setTotalValues();
    }

    // hides keyboard when the user touches outside editText
    public void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if (activity.getCurrentFocus() != null)
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
    }

    /**
     * Sets the adapter.
     */
    private void setListAdapterName() {
        Collections.reverse(globalTaxInvoiceLists);
        if (globalTaxInvoiceLists.size() == 2) {
            ViewGroup.LayoutParams params = linTaxList.getLayoutParams();
            params.height = dpToPx(60);
            linTaxList.setLayoutParams(params);
        } else if (globalTaxInvoiceLists.size() == 1) {
            ViewGroup.LayoutParams params = linTaxList.getLayoutParams();
            params.height = dpToPx(30);
            linTaxList.setLayoutParams(params);
        }

        taxNameListAdapterRv = new TaxNameListAdapterRv(getActivity(), globalTaxInvoiceLists, currencyType, this);
        rvTaxListName.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvTaxListName.setAdapter(taxNameListAdapterRv);
        rvTaxListName.setVisibility(View.VISIBLE);
        linTaxList.setVisibility(View.VISIBLE);

    }


    public int dpToPx(int dp) {
        float density = getActivity().getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

    public void deleteGlobalDiscount(int size) {

        if (size == 0) {
            new UpdateInvoiceOrQuotation().execute();
        }


    }


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

            Double discountValue = 0.0;

            double invoiceTaxTotal = dao.getTotalTaxInvoice(invoiceQuotationId);
            double invoiceSubTotal = dao.getSubTotalTaxInvoice(invoiceQuotationId);

            double invoiceTotalAmount = invoiceSubTotal + invoiceTaxTotal;

            totalWithDiscount = invoiceTotalAmount - discountValue;
            Double discAmt = discountValue;

            Logger.log(TAG, "GLOBAL DISCOUNT global disc amt ===> " + discAmt);
//            invoiceTotalAmount = invoiceTotalAmount - totalWithDiscount;

//            updateGlobalList(discAmt);


            boolean drp = dao.updateInvoiceOrQuotationGlobalDiscNew(
                    invoiceQuotationId, discountValue, totalWithDiscount);

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


                updateGlobalList();
                setTotalValues();

                if (getActivity() != null) {
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                }


            } else
                Toast.makeText(getActivity(),
                        getResources().getString(R.string.msg_error),
                        Toast.LENGTH_SHORT).show();
        }
    }

    // -----------------------------------V52_CHANGES-------------------------------------------------


    // .......................................ASYNC...CLASS...STARTS...HERE..............................................

    /**
     * Async class to add a new invoice or quotation
     */
    private class ConvertAddInvoiceOrQuotation extends
            AsyncTaskCoroutine<String, Boolean> {
        String uniqueId = "";

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            DialogUtils.showProgressDialog(getActivity(),
                    getActivity().getString(R.string.textWaitLable),
                    getActivity().getString(R.string.chargement),
                    false);
        }

        @SuppressLint("SimpleDateFormat")
        @Override
        public Boolean doInBackground(String... params) {
            uniqueId = dao.getUniqueId();
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss.SSS");


            boolean drp = dao.duplicationInvoiceOrQuotation(idInterv,
                    1, sdf.format(cal.getTime()), invoiceQuotationId, uniqueId, flInvoiceStrict);

            return drp;
        }

        @Override
        public void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            DialogUtils.dismissProgressDialog();
            boolean drp = result.booleanValue();
            if (drp) {
                new AddInvoiceOrQuotationItem(uniqueId).execute();
            } else
                Toast.makeText(getActivity(),
                        getString(R.string.msg_error), Toast.LENGTH_SHORT)
                        .show();
        }
    }


    /**
     * Async class to add items in invoice or quotation
     */
    private class AddInvoiceOrQuotationItem extends
            AsyncTaskCoroutine<String, Boolean> {

        String uniqueId;


        public AddInvoiceOrQuotationItem(String uniqueId) {
            this.uniqueId = uniqueId;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            DialogUtils.showProgressDialog(getActivity(),
                    getActivity().getString(R.string.textWaitLable),
                    getActivity().getString(R.string.chargement),
                    false);
        }

        @Override
        public Boolean doInBackground(String... params) {

            ArrayList<Quotation_Items_Beans> quotationItemsBeans = dao.getQuotationItemValues(invoiceQuotationId);
            ArrayList<GlobalTaxInvoiceList> globalTaxInvoiceLists = dao.getGlobalTaxInvoice(invoiceQuotationId);
            boolean drp = false;
            if (quotationItemsBeans != null && quotationItemsBeans.size() > 0) {
                Logger.log("TAG",
                        "INVOICE DUPLICATION VALUES quotationItemsBeans size===>" + quotationItemsBeans.size());
                for (int i = 0; i < quotationItemsBeans.size(); i++) {
                    drp = dao.addQuotationItem(quotationItemsBeans.get(i), uniqueId);
                }
            }


            if (globalTaxInvoiceLists != null && globalTaxInvoiceLists.size() > 0) {
                Logger.log("TAG",
                        "INVOICE DUPLICATION VALUES globalTaxInvoiceLists size===>" + globalTaxInvoiceLists.size());
                for (int i = 0; i < globalTaxInvoiceLists.size(); i++) {
                    drp = dao.addGlobalTaxInvoice(globalTaxInvoiceLists.get(i).getIdTaxRate(),
                            globalTaxInvoiceLists.get(i).isHasCompound(), globalTaxInvoiceLists.get(i).getTax(),
                            globalTaxInvoiceLists.get(i).getTaxValue(), uniqueId);
                }
            }

            return drp;

        }

        @Override
        public void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            DialogUtils.dismissProgressDialog();
            boolean drp = result.booleanValue();
            if (drp) {
                Toast.makeText(getActivity(),
                        getString(R.string.txt_ack_msg), Toast.LENGTH_SHORT)
                        .show();
                EventBus.getDefault().post(new AddInvoiceQuotationEvent(uniqueId, false));

            } else
                Toast.makeText(getActivity(),
                        getString(R.string.msg_error), Toast.LENGTH_SHORT)
                        .show();
        }
    }

    /**
     * Async class for performing Synch.
     */
    private class SynchronizeAsyncTask extends AsyncTaskCoroutine<Void, String> {

        String invoiceQuotationId;

        public SynchronizeAsyncTask(String invoiceQuotationId) {
            this.invoiceQuotationId = invoiceQuotationId;

        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            if (getActivity() != null && !getActivity().isFinishing()) {
                if (progressDSynch == null)
                    progressDSynch = ProgressDialog.show(getActivity(),
                            jobDetails.getString(R.string.textPleaseWaitLable),
                            jobDetails.getString(R.string.msg_synch), true, false);
                else if (progressDSynch != null && !progressDSynch.isShowing())
                    progressDSynch.show();
            }
        }

        @Override
        public String doInBackground(Void... voids) {

            String resultMessage = "Ok";
            if (SynchroteamUitls.isNetworkAvailable(getActivity())) {

                User u = dao.getUser();
                try {
                    dao.sync(u.getLogin(), u.getPwd());
                    Logger.output(TAG, " finished sync");

                    resultMessage = "ok";
                    Thread.sleep(1000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    String exception = ex.getMessage();
                    Logger.printException(ex);
                    if (exception != null) {
                        if (exception.indexOf("4001") != -1) {
                            resultMessage = "4001";
                        } else if (exception.indexOf("4000") != -1) {
                            resultMessage = "4000";
                        } else if (exception.indexOf("4006") != -1) {
                            resultMessage = "4006";
                        } else if (exception.indexOf("4101") != -1) {
                            resultMessage = "4101";
                        } else if (exception.indexOf("4005") != -1) {
                            resultMessage = "4005";
                        } else if (exception.indexOf("4003") != -1) {
                            resultMessage = "4003";
                        } else {
                            resultMessage = "Error";
                        }
                    } else {
                        resultMessage = "Error";
                    }
                }

            }


            return resultMessage;
        }

        @Override
        public void onPostExecute(String resultMessage) {
            super.onPostExecute(resultMessage);
            if (jobDetails != null) {

                if (getActivity() != null && !getActivity().isFinishing()) {
                    //dismiss progress bar
                    if (progressDSynch != null
                            && progressDSynch.isShowing())
                        progressDSynch.dismiss();
                }

                if (resultMessage.equals("offline") || resultMessage.equals("ok")) {

                    if (resultMessage.equals("ok"))
                        Toast.makeText(getActivity(),
                                        getString(R.string.msg_synch_ok), Toast.LENGTH_LONG)
                                .show();


//                    EventBus.getDefault().post(new UpdateUiAfterSync());
//                    getUpdatedInvoiceOrQuotation();
                    EventBus.getDefault().post(new AddInvoiceQuotationEvent(invoiceQuotationId, false));

                } else if (resultMessage.equals("4001") || resultMessage.equals("4000")) {
                    showAuthErrDialog();
                } else if (resultMessage.equals("4006")) {
                    Toast.makeText(getActivity(),
                            getString(R.string.msg_synch_error_4),
                            Toast.LENGTH_LONG).show();
                    getActivity().finish();

                } else if (resultMessage.equals("4101")) {
                    showMultipleDeviecDialog();
                } else if (resultMessage.equals("4005")) {
                    showAppUpdatetDialog();
                } else if (resultMessage.equals("4003")) {
                    showErrMsgDialog(getString(R.string.msg_sync_error_4003));
                } else {
                    if (!getActivity().isFinishing()) {
                        showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));
                    }
                }


            }
        }
    }

    // .......................................ASYNC...CLASS...ENDS...HERE..............................................
}