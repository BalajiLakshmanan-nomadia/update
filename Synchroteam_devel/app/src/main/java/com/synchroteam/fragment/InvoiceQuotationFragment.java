package com.synchroteam.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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
import com.synchroteam.beans.UpdateDataBaseEvent;
import com.synchroteam.beans.UpdateUiAfterSync;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.AppUpdateDialog;
import com.synchroteam.dialogs.AuthenticationErrorDialog;
import com.synchroteam.dialogs.DeleteInvoiceQuotationDialog;
import com.synchroteam.dialogs.ErrorDialog;
import com.synchroteam.dialogs.InvoicePaymentTransactionResultDialog;
import com.synchroteam.dialogs.InvoiceQuotationPaynowConfirmDialog;
import com.synchroteam.dialogs.MultipleDeviceNotSupported;
import com.synchroteam.dialogs.SynchronizationErrorDialog;
import com.synchroteam.events.AddInvoiceQuotationEvent;
import com.synchroteam.listadapters.InvoiceQuotationListAdapter;
import com.synchroteam.listadapters.InvoiceQuotationListAdapter.RefreshInvoiceQutationList;
import com.synchroteam.retrofit.ApiInterface;
import com.synchroteam.retrofit.Apiclient;
import com.synchroteam.retrofit.models.paymentservice.PaymentServiceModel;
import com.synchroteam.synchroteam.AddInvoiceQuotation;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.technicalsupport.JobDetails;
import com.synchroteam.utils.CreditCardUtils;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.SharedPref;
import com.synchroteam.utils.SynchroteamUitls;

import net.i2p.android.ext.floatingactionbutton.FloatingActionButton;
import net.i2p.android.ext.floatingactionbutton.FloatingActionsMenu;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import de.greenrobot.event.EventBus;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * This class holds the view for create/update invoice or quotation and shows details of invoice/quotation.
 *
 * @author Trident
 */
public class InvoiceQuotationFragment extends Fragment implements
        OnClickListener, RefreshInvoiceQutationList {

    // ....................................INSTANCE...VARIABLE...STARTS...HERE.....................................

    private RelativeLayout relHeaderView;
    private TextView txtTitle;
    private android.widget.TextView txtDelete;
    private FloatingActionsMenu famAddInvoiceQuotation;
    private FloatingActionButton fabAddInvoice, fabAddQuotation, fabAddItem;
    private ListView listViewInvoiceQuotation;
    private InvoiceQuotationListAdapter invoiceQuotationListAdapter;
    private TextView txtSubTotal, txtTaxValue, txtTotal, txtLabel, txtBalance, labelBal;
    private LinearLayout layoutBalance;
    private Button btnPayNow;
    private RelativeLayout relEmptyView;

    private Dao dao;
    private ArrayList<Invoice_Quotation_Items_Beans> invoiceQuotationList;
    private RelativeLayout relTotalContainer;
    private Bundle bundleInvoice;
    private Invoice_Quotation_Beans invoiceQuotation;
    private String invoiceQuotationId;
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
    private int idClient = 0;

    // ************************************INSTANCE...VARIABLE...ENDS...HERE***************************************

    // ....................................LIFECYCLE...METHOD...STARTS...HERE.......................................
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(
                R.layout.layout_invoice_quotation_fragment, container, false);
        initiateView(view);

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        jobDetails = (JobDetails) getActivity();

        getInvoiceOrQuotaion();

        flCreateUpdateInvoiceQuotation = gestionAcces
                .getFlCreateUpdateInvoiceQuotation();

        setTitleAndDeleteBtn();

        txtLabel.setText(getActivity().getString(R.string.txt_item_label));

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
        txtTotal = (TextView) view.findViewById(R.id.txtTotal);
        txtBalance = (TextView) view.findViewById(R.id.txtBalance);
        labelBal = (TextView) view.findViewById(R.id.labelBal);
        btnPayNow = (Button) view.findViewById(R.id.btn_pay_now);
        txtLabel = (TextView) view.findViewById(R.id.txtLabel);
        layoutBalance = (LinearLayout) view.findViewById(R.id.layoutBalance);


        android.widget.TextView txtEmptyList = (android.widget.TextView) view
                .findViewById(R.id.empty_text);
        relEmptyView = (RelativeLayout) view.findViewById(R.id.rel_empty_view);

        Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(),
                getActivity().getString(R.string.fontName_fontAwesome));
        txtDelete.setTypeface(typeFace);
        txtEmptyList.setTypeface(typeFace);

        dao = DaoManager.getInstance();
        gestionAcces = dao.getAcces();
        bundleInvoice = getArguments();

        idClient = bundleInvoice.getInt("id_client");
        currencyType = dao.getDeviseCustomer();

        setBalanceLayoutVisible(false);
        btnPayNow.setOnClickListener(this);
    }

    /**
     * gets the invoice or quotation for current job.
     */
    private void getInvoiceOrQuotaion() {
//        invoiceQuotation = dao.getInvoiceQuotationDetails(bundleInvoice
//                .getString("id_interv"));
        invoiceQuotation = new Invoice_Quotation_Beans();
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
            invoiceQuotationListAdapter = new InvoiceQuotationListAdapter(
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

    private void setTotalValues() {
        double totalWithDiscountRate = 0.00f;
        double taxAmount = 0.00f;
        double totalWithTaxAmt = 0.00f;
//        invoiceQuotationList = dao
//                .getInvoiceQuotationItemValues(invoiceQuotationId);

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
            }

            invoicePaymentBalanceAmount = totalWithTaxAmt - invoicePaymentBalanceAmount;
            if (invoicePaymentBalanceAmount > 0) {
                txtBalance.setText(toCurrencyFormat(invoicePaymentBalanceAmount) + " " + currencyType);
            } else {
                setBalanceLayoutVisible(false);
            }

            txtSubTotal.setText(String.format(Locale.US, "%.2f", totalWithDiscountRate) + " " + currencyType);
            txtTaxValue.setText(String.format(Locale.US, "%.2f", taxAmount) + " " + currencyType);
            txtTotal.setText(String.format(Locale.US, "%.2f", totalWithTaxAmt) + " " + currencyType);
        } else {
            txtSubTotal.setText("-");
            txtTaxValue.setText(String.valueOf("-"));
            txtTotal.setText(String.valueOf("-"));

            setBalanceLayoutVisible(false);
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
            if (invoiceQuotation.getNumberOfIQ() != 0) {
                title += " #" + invoiceQuotation.getNumberOfIQ();
                txtDelete.setVisibility(View.GONE);
            }
            isInvoice = false;
        } else {
            title = getActivity().getString(R.string.txt_label_invoice);
            if (invoiceQuotation.getNumberOfIQ() != 0) {
                title += " #" + invoiceQuotation.getNumberOfIQ();
                txtDelete.setVisibility(View.GONE);
            }
            isInvoice = true;
        }
        txtTitle.setText(title);
    }

    /**
     * Deletes the whole invoice/quotation.
     */
    private void deleteInvoiceQuotation() {
        dao.deleteInvoiceOrQuotation(invoiceQuotationId);
    }

    private void deleteGlobalTaxItems() {
        dao.deleteGlobalTaxInvoice(invoiceQuotationId);
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

                                    EventBus.getDefault().post(new AddInvoiceQuotationEvent(" ", false));

                                }

                                @Override
                                public void doOnNoClick() {

                                }
                            });

                    deleteIQDialog.show();
                }
                break;
            case R.id.addInvoice:
                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {
                    if (idClient > 0) {
                        Intent intentAddInvoice = new Intent();
                        intentAddInvoice.setClass(getActivity(),
                                AddInvoiceQuotation.class);
                        intentAddInvoice.putExtra("id_interv",
                                bundleInvoice.getString("id_interv"));
                        intentAddInvoice.putExtra("id_client",
                                bundleInvoice.getInt("id_client"));
                        intentAddInvoice.putExtra("id_site",
                                bundleInvoice.getInt("id_site"));
                        intentAddInvoice.putExtra("flag", 1);
                        intentAddInvoice.putExtra("currency_type", currencyType);
                        startActivity(intentAddInvoice);
                    }else {
                        Toast.makeText(getActivity(), R.string.client_error_msg, Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                famAddInvoiceQuotation.collapse();
                break;
            case R.id.addQuotation:
                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {
                    if (idClient > 0) {
                        Intent intentAddQuotation = new Intent();
                        intentAddQuotation.setClass(getActivity(),
                                AddInvoiceQuotation.class);
                        intentAddQuotation.putExtra("id_interv",
                                bundleInvoice.getString("id_interv"));
                        intentAddQuotation.putExtra("id_client",
                                bundleInvoice.getInt("id_client"));
                        intentAddQuotation.putExtra("id_site",
                                bundleInvoice.getInt("id_site"));
                        intentAddQuotation.putExtra("flag", 0);
                        intentAddQuotation.putExtra("currency_type", currencyType);
                        startActivity(intentAddQuotation);
                    }else {
                        Toast.makeText(getActivity(), R.string.client_error_msg, Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                famAddInvoiceQuotation.collapse();
                break;
            case R.id.addItem:
                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {
                    int flag = invoiceQuotation.getFlag();
                    if (idClient > 0) {
                        if (flag == 0) {
                            Intent intentAddQuotation1 = new Intent();
                            intentAddQuotation1.setClass(getActivity(),
                                    AddInvoiceQuotation.class);
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
                            intentAddQuotation1.putExtra("order", order);
                            intentAddQuotation1.putExtra("currency_type", currencyType);
                            startActivity(intentAddQuotation1);
                        } else {
                            Intent intentAddInvoice1 = new Intent();
                            intentAddInvoice1.setClass(getActivity(),
                                    AddInvoiceQuotation.class);
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
        }
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

            String ccToken = data.getStringExtra(CreditCardUtils.EXTRA_KEY_STRIPE_TOKEN);
            String amount = data.getStringExtra(CreditCardUtils.EXTRA_BALANCE_AMOUNT);
            String ccEmail = data.getStringExtra(CreditCardUtils.EXTRA_CC_EMAIL);
            String paymentId = data.getStringExtra(CreditCardUtils.EXTRA_PAYMENT_INTENT_ID);
            String currencyCode = dao.getDeviseCustomerCurrencyCode();

            boolean paymentResult = data.getBooleanExtra(CreditCardUtils.EXTRA_PAYMENT_SUCCESS, false);
            int amountInCents = (int) (Float.parseFloat(amount) * 100);

            if (paymentResult) {
                if (SharedPref.getUrlStripeServer(getActivity()) != null && SharedPref.getUrlStripeServer(getActivity()).length() > 0) {
//                hitPaymentService(accessToken, idCustomer, amountInCents, currencyCode, ccToken, ccEmail, null, invoiceQuotationId);

//                    hitPaymentServiceUpdated(idCustomer, amountInCents, ccEmail,
//                            null, invoiceQuotationId, paymentId);

                    InvoicePaymentTransactionResultDialog successDialog = new InvoicePaymentTransactionResultDialog(getActivity(),
                            "1", "Success", "10 " + currencyType);
                    successDialog.show(getActivity().getSupportFragmentManager(), "");

                }
            } else {
                InvoicePaymentTransactionResultDialog dialog = new InvoicePaymentTransactionResultDialog(getActivity(), "0", "Failure", null);
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

    private void hitPaymentService(String token, int idCustomer, final int amountInCents, final String currency, String ccToken, String ccEmail, String idInvoive, final String idRemoteInvoice) {

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
            layoutBalance.setVisibility(View.VISIBLE);
            btnPayNow.setVisibility(View.VISIBLE);
            labelBal.setVisibility(View.VISIBLE);
            txtBalance.setVisibility(View.VISIBLE);
        } else {
            layoutBalance.setVisibility(View.GONE);
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

                    InvoiceQuotationPaynowConfirmDialog confirmDialog = new InvoiceQuotationPaynowConfirmDialog(getActivity(), InvoiceQuotationFragment.this,
                            invoicePaymentBalanceAmount, currencyType, stripePublishableKey, invoiceQuotationId);
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

        // Always use string resources for UI text. This says something like
        // "Share this photo with"
        String title = getString(R.string.titleBrowserSelection);
        // Create and start the chooser
        Intent chooser = Intent.createChooser(intent, title);
        startActivity(chooser);
    }

    private String toCurrencyFormat(float amountf) {
        return String.format(Locale.US, "%.2f", amountf);
    }

    private String toCurrencyFormat(double amountf) {
        return String.format(Locale.US, "%.2f", amountf);
    }
}