package com.synchroteam.synchroteam;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.synchroteam.TypefaceLibrary.CheckBox;
import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.RadioButton;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.beans.InventoryDialogSerialNumber;
import com.synchroteam.beans.InventorySerialNumbersBeans;
import com.synchroteam.beans.InventorySingleItemBeans;
import com.synchroteam.beans.InventoryStocksBeans;
import com.synchroteam.beans.PendingRequestBeans;
import com.synchroteam.beans.UpdateUiOnSync;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.listadapters.InventoryItemAdapter;
import com.synchroteam.listadapters.SpinnerAdapter;
import com.synchroteam.synchroteam.RestockSerialNumberDialog.RestockSerialNumberListener;
import com.synchroteam.synchroteam.ReturnSerialNumbersDialog.ReturnSerialNumbersListener;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DateChecker;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.greenrobot.event.EventBus;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

public class InventoryDetails extends AppCompatActivity implements
        CommonInterface {

    // ___________INSTANCE__VARIBALES___________

    private ActionBar actionBar;
    private StickyListHeadersListView stickyList;
    private InventoryItemAdapter mAdapter;
    private ArrayList<InventorySingleItemBeans> inventoryItemArray;
    private Typeface typeface;

    /*******
     * HEADER...ELEMENTS
     *********/
    private View headerView;
    private TextView txtReference, txtCategory, txtPartsService, txtPrice, txtQty,
            txtSerializable, txtRestockSave, txtRestockCancel, txtReturnSave,
            txtReturnCancel;
    private android.widget.TextView txtSerializableIcon, txtRestockIcon,
            txtReturnIcon, txtSpinnerDepotIcon, txtSpinnerToIcon;
    private LinearLayout linPriceContainer;
    private View dividerPrice;
    private Spinner spinnerRestockFrom, spinnerReturnTo;
    private RestockSerialNumberDialog restockSerialNos;
    private ReturnSerialNumbersDialog returnSerialNos;
    private EditText edtRestockQty, edtReturnQty;
    private FrameLayout frameRestockSerialView;
    private LinearLayout linReturnSerialView;
    private RadioGroup rdoGrpAction;
    private RadioButton rdoTransfer;
    private RadioButton rdoRequest;
    private RelativeLayout relRestockSerialContainer, relReturnSerialContainer;
    private CheckBox chkBxUrgent;
    private LinearLayout linRestockContainer;
    private LinearLayout linReturnContainer;

    private int isSerializable;
    private String qty;
    private Dao dao;
    private GestionAcces access;
    private ArrayList<InventoryStocksBeans> inventoryStockArray;
    private ArrayList<InventorySerialNumbersBeans> inventorySerialNos;
    private User user;
    private int idUser;
    private String idUserStock;
    private String idPiece;

    private ArrayList<String> restockFromList, returnToList;
    List<String> restockSerialNoList, returnSerialNoList;
    List<String> restockSerialPieceNoList, returnSerialPieceNoList;

    private List<InventoryDialogSerialNumber> listArrayFrom, listArrayTo;
    private ArrayList<PendingRequestBeans> pendingRequestList;
    private boolean isReturnSaved;

    private Calendar calendar;
    private SimpleDateFormat sdf;

    /*******
     * HEADER...ELEMENTS
     *********/

    // ___________INSTANCE__VARIBALES___________

    private static final String TAG = InventoryDetails.class.getName();

    // ******************************************OVERRIDE...METHODS...STARTS...HERE****************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_details);
        initiateUi();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        ((SyncroTeamApplication) getApplicationContext())
                .setSyncroteamAppLive(false);
        ((SyncroTeamApplication) getApplicationContext())
                .setSyncroteamActivityInstance(null);

    }

    @Override
    protected void onResume() {
        super.onResume();

        //New changes
        DateChecker.checkDateAndNavigate(this, dao);

        ((SyncroTeamApplication) getApplicationContext())
                .setSyncroteamAppLive(true);
        ((SyncroTeamApplication) getApplicationContext())
                .setSyncroteamActivityInstance(this);
    }

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
    // ******************************************OVERRIDE...METHODS...ENDS...HERE******************************************

    // ******************************************LOCAL...METHODS...STARTS...HERE*******************************************

    /**
     * Initialize the UI elements.
     */
    @SuppressLint({"InflateParams", "SimpleDateFormat"})
    private void initiateUi() {
        setActionBar();
        headerView = getLayoutInflater().inflate(
                R.layout.inventory_list_header, null);
        stickyList = (StickyListHeadersListView) findViewById(R.id.stickyInventoryList);
        stickyList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        typeface = Typeface.createFromAsset(getAssets(), getResources()
                .getString(R.string.fontName_fontAwesome));

        calendar = Calendar.getInstance();
        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        dao = DaoManager.getInstance();
        access = dao.getAcces();
        user = dao.getUser();
        idUser = user.getId();
        idUserStock = user.getIdStock();

        initializeHeaderUi(headerView);

        restockSerialNoList = new ArrayList<String>();
        returnSerialNoList = new ArrayList<String>();
        restockSerialPieceNoList = new ArrayList<>();
        returnSerialPieceNoList = new ArrayList<>();

        setValues();
        setSpinnerAdapter();
        setFontForText(typeface);
        stickyList.addHeaderView(headerView);
        stickyList.setAreHeadersSticky(true);
        stickyList.setDivider(null);
        stickyList.setDividerHeight(0);
        stickyList.setOnItemClickListener(null);

        txtSpinnerDepotIcon.setOnClickListener(clickListener);
        txtSpinnerToIcon.setOnClickListener(clickListener);
        rdoGrpAction.setOnCheckedChangeListener(checkChangedListener);
        spinnerRestockFrom.setOnItemSelectedListener(itemSelectedListener);
        spinnerReturnTo.setOnItemSelectedListener(itemSelectedListener);
        spinnerRestockFrom.setOnTouchListener(touchListener);
        txtSpinnerDepotIcon.setOnTouchListener(touchListener);
        edtRestockQty.setOnFocusChangeListener(focusListener);
        edtReturnQty.setOnFocusChangeListener(focusListener);

        setListener();
        setListAdapter();

    }

    /**
     * Initialize the header view elements in the list view.
     *
     * @param headerView
     */
    private void initializeHeaderUi(View headerView) {
        txtRestockIcon = (android.widget.TextView) headerView
                .findViewById(R.id.txRestockIcon);
        txtReference = (TextView) headerView.findViewById(R.id.txt_reference);
        txtCategory = (TextView) headerView.findViewById(R.id.txt_category);
        txtPartsService = (TextView) headerView
                .findViewById(R.id.txt_parts_service);
        txtPrice = (TextView) headerView.findViewById(R.id.txt_price);
        txtQty = (TextView) headerView.findViewById(R.id.txt_qty);
        txtSerializable = (TextView) headerView
                .findViewById(R.id.txtSerializable);

        edtRestockQty = (EditText) headerView
                .findViewById(R.id.edtAddRestockQty);
        edtReturnQty = (EditText) headerView.findViewById(R.id.edtReturnQty);

        txtRestockSave = (TextView) headerView
                .findViewById(R.id.txtRestockSave);
        txtRestockCancel = (TextView) headerView
                .findViewById(R.id.txtRestockCancel);
        txtReturnSave = (TextView) headerView.findViewById(R.id.txtReturnSave);
        txtReturnCancel = (TextView) headerView
                .findViewById(R.id.txtReturnCancel);

        spinnerRestockFrom = (Spinner) headerView
                .findViewById(R.id.spinnerFrom);

        restockSerialNos = (RestockSerialNumberDialog) headerView
                .findViewById(R.id.spinnerRestockSerial);
        spinnerReturnTo = (Spinner) headerView.findViewById(R.id.spinnerSendTo);
        returnSerialNos = (ReturnSerialNumbersDialog) headerView
                .findViewById(R.id.spinnerReturnSerial);

        txtSerializableIcon = (android.widget.TextView) headerView
                .findViewById(R.id.txtSerializableIcon);
        txtRestockIcon = (android.widget.TextView) headerView
                .findViewById(R.id.txRestockIcon);
        txtReturnIcon = (android.widget.TextView) headerView
                .findViewById(R.id.txtReturnIcon);
        txtSpinnerDepotIcon = (android.widget.TextView) headerView
                .findViewById(R.id.txtSpinnerDepotIcon);
        txtSpinnerToIcon = (android.widget.TextView) headerView
                .findViewById(R.id.txtSpinnerSendIcon);

        linPriceContainer = (LinearLayout) headerView.findViewById(R.id.lin_price_container);
        dividerPrice = (View) headerView.findViewById(R.id.view_price_divider);

        frameRestockSerialView = (FrameLayout) headerView
                .findViewById(R.id.frameRestockSerialView);
        linReturnSerialView = (LinearLayout) headerView
                .findViewById(R.id.linContainer2);

        rdoGrpAction = (RadioGroup) headerView.findViewById(R.id.rdoGrpAction);
        rdoTransfer = (RadioButton) headerView.findViewById(R.id.rdoTransfer);
        rdoRequest = (RadioButton) headerView.findViewById(R.id.rdoRequest);

        relRestockSerialContainer = (RelativeLayout) headerView
                .findViewById(R.id.relRestockSerialContainer);
        relReturnSerialContainer = (RelativeLayout) headerView
                .findViewById(R.id.relReturnSerialContainer);

        linRestockContainer = (LinearLayout) headerView.findViewById(R.id.linRestockContainer);
        linReturnContainer = (LinearLayout) headerView.findViewById(R.id.linMovementContainer);

        chkBxUrgent = (CheckBox) headerView.findViewById(R.id.chkbx_fl_urgent);

    }

    /**
     * Set type face for the font awesome text views.
     *
     * @param typeFace
     */
    private void setFontForText(Typeface typeFace) {
        txtSerializableIcon.setTypeface(typeface);
        txtRestockIcon.setTypeface(typeface);
        txtReturnIcon.setTypeface(typeface);
        txtSpinnerDepotIcon.setTypeface(typeface);
        txtSpinnerToIcon.setTypeface(typeface);
    }

    private void setListener() {
        txtSpinnerDepotIcon.setOnClickListener(clickListener);
        txtSpinnerToIcon.setOnClickListener(clickListener);
        rdoGrpAction.setOnCheckedChangeListener(checkChangedListener);
        spinnerRestockFrom.setOnItemSelectedListener(itemSelectedListener);
        spinnerReturnTo.setOnItemSelectedListener(itemSelectedListener);
        relRestockSerialContainer.setOnClickListener(clickListener);
        relReturnSerialContainer.setOnClickListener(clickListener);
        txtRestockSave.setOnClickListener(clickListener);
        txtRestockCancel.setOnClickListener(clickListener);
        txtReturnSave.setOnClickListener(clickListener);
        txtReturnCancel.setOnClickListener(clickListener);
    }

    /**
     * Hides soft input keyboard.
     */
    private void hideSoftKeyboard() {
        // Check if no view has focus:
        View view = InventoryDetails.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * Get the values from intent and set to the text views.
     */
    private void setValues() {
        idPiece = getIntent().getExtras().getString(
                KEYS.InventoryListValues.ID_PIECE);

        String category = getIntent().getExtras().getString(
                KEYS.InventoryListValues.CATEGORY_NAME);

        String itemCategory = category.replace("|", " > ");

        txtCategory.setText(itemCategory);
        txtPartsService.setText(getIntent().getExtras().getString(
                KEYS.InventoryListValues.PARTS_SERVICES_NAME));

        BigDecimal bdPriceValue = new BigDecimal(getIntent().getExtras().getString(
                KEYS.InventoryListValues.PRICE_VALUE)).setScale(2, RoundingMode.HALF_UP);
        txtPrice.setText(bdPriceValue.toPlainString());

        isSerializable = getIntent().getExtras().getInt(
                KEYS.InventoryListValues.IS_SERIALIZABLE);
        String reference = getIntent().getExtras().getString(
                KEYS.InventoryListValues.REFERENCE_NAME);
        txtQty.setText(getIntent().getExtras().getString(
                KEYS.InventoryListValues.QTY));

        if (!TextUtils.isEmpty(reference)) {
            txtReference.setText(reference);
        } else {
            txtReference.setText("-");
        }
        /*
         * if the value is not serializable hide the serial number edit text.
         */
        if (isSerializable == 0) {
            txtSerializable.setVisibility(View.INVISIBLE);
            txtSerializableIcon.setVisibility(View.INVISIBLE);
            // txtSerializable.setTextColor(getResources().getColor(
            // R.color.invoice_label_color));
            // txtSerializableIcon.setTextColor(getResources().getColor(
            // R.color.invoice_label_color));
            frameRestockSerialView.setVisibility(View.GONE);
            linReturnSerialView.setVisibility(View.GONE);
        }

        /**
         * If the fl mob price is turned on, don't show the price
         */
        if (access != null && access.getFlMobPrice() == 1) {
            linPriceContainer.setVisibility(View.GONE);
            dividerPrice.setVisibility(View.GONE);
        } else {
            linPriceContainer.setVisibility(View.VISIBLE);
            dividerPrice.setVisibility(View.VISIBLE);
        }

        if (access != null) {
            if (access.getFlTakeFrom() == 1) {
                rdoTransfer.setVisibility(View.VISIBLE);
            } else {
                rdoTransfer.setVisibility(View.GONE);
            }

            if (access != null && access.getFlRequestFrom() == 1) {
                rdoRequest.setVisibility(View.VISIBLE);
                if (access.getFlTakeFrom() == 0) {
                    rdoRequest.setChecked(true);
                }
            } else {
                rdoRequest.setVisibility(View.GONE);
            }

            if (access != null && access.getFlTakeFrom() == 0 && access.getFlRequestFrom() == 0) {
                linRestockContainer.setVisibility(View.GONE);
            } else {
                linRestockContainer.setVisibility(View.VISIBLE);
            }
        }


        if (access != null && access.getFlSendTo() == 1) {
            linReturnContainer.setVisibility(View.VISIBLE);
        } else {
            linReturnContainer.setVisibility(View.GONE);
        }


    }

    /**
     * Initiate action bar.
     */
    private void setActionBar() {
        actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(
                R.string.txt_inventory_label).toUpperCase());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    /**
     * Set the values for list view.
     */
    public void setListAdapter() {
        pendingRequestList = dao.getPendingRequestList(Integer
                .parseInt(idPiece));
        inventoryItemArray = new ArrayList<InventorySingleItemBeans>();
        for (int i = 0; i < pendingRequestList.size(); i++) {
            InventorySingleItemBeans inventoryItem = new InventorySingleItemBeans();

            inventoryItem.setIdPiece(idPiece);
            inventoryItem.setIdStock(pendingRequestList.get(i).getIdStock());
            inventoryItem.setIdStockDestination(pendingRequestList.get(i)
                    .getIdStockDest());
            inventoryItem.setIdPieceDemande(pendingRequestList.get(i)
                    .getIdPieceDemande());
            inventoryItem.setUser(user.getNom());

            String nameStock = dao.getStockName(pendingRequestList.get(i)
                    .getIdStock());
            inventoryItem.setDepot(nameStock);

            String action = null;
            if (idUserStock.equalsIgnoreCase(pendingRequestList.get(i)
                    .getIdStock())) {
                action = getResources().getString(R.string.txt_send_to_label);
            } else {
                action = getResources().getString(R.string.txt_take_from_label);
            }

            inventoryItem.setAction(action);
            inventoryItem.setQty(String.valueOf(pendingRequestList.get(i)
                    .getQty()));
            inventoryItem.setFlUrgent(String.valueOf(pendingRequestList.get(i)
                    .getFlUrgent()));
            inventoryItem.setIsSerializable(getIntent().getExtras().getInt(
                    KEYS.InventoryListValues.IS_SERIALIZABLE));
            inventoryItemArray.add(inventoryItem);
        }

        mAdapter = new InventoryItemAdapter(this, android.R.id.text1,
                stickyList, inventoryItemArray, idUser);
        stickyList.setAdapter(mAdapter);

    }

    /**
     * Set the values for From and To spinner.
     */
    public void setSpinnerAdapter() {

        inventoryStockArray = dao.getInventoryStockList(idUserStock);

        restockFromList = new ArrayList<String>();
        returnToList = new ArrayList<String>();

        restockFromList.add(getResources().getString(R.string.txt_from_label));
        returnToList.add(getResources().getString(R.string.txt_send_to_label));

        for (int i = 0; i < inventoryStockArray.size(); i++) {

            String valueRestock = inventoryStockArray.get(i).getNameStock();
            String valueReturn = inventoryStockArray.get(i).getNameStock();

            // if the piece is serializable, add the quantity with the depot
            // name (Only for restock).
            if (isSerializable == 1) {

                String qtyRestock = String
                        .valueOf(getQunatityOfPiece(inventoryStockArray.get(i)
                                .getIdStock()));

                valueRestock += " - " + qtyRestock;

                if (!qtyRestock.equalsIgnoreCase("0")) {
                    restockFromList.add(valueRestock);
                }

            } else {
                restockFromList.add(valueRestock);
            }

            returnToList.add(valueReturn);

        }

        spinnerRestockFrom.setAdapter(new SpinnerAdapter(this,
                R.layout.layout_custom_adapter_spinner, restockFromList));

        spinnerReturnTo.setAdapter(new SpinnerAdapter(this,
                R.layout.layout_custom_adapter_spinner, returnToList));

    }

    /**
     * Gets the quantity of piece in a given depot (Only for serializable).
     *
     * @param idStock
     * @return qty of the piece
     */

    private int getQunatityOfPiece(String idStock) {
        int qty = 0;
        ArrayList<InventorySerialNumbersBeans> serialNoList = dao
                .getDepotSerialNumbers(idStock, Integer.parseInt(idPiece));
        for (int i = 0; i < serialNoList.size(); i++) {
            if (TextUtils.isEmpty(serialNoList.get(i).getIdInterv())) {
                qty++;
            }
        }
        return qty;
    }

    /**
     * Load the list for Restock serial number dialog view.
     *
     * @param position
     */
    private void loadRestockSerial(int position) {
        clearRestockSerialNumbers();
        if (!spinnerRestockFrom
                .getSelectedItem()
                .toString()
                .equalsIgnoreCase(
                        getResources().getString(R.string.txt_from_label))) {
            inventorySerialNos = dao.getDepotSerialNumbers(inventoryStockArray
                    .get(position).getIdStock(), Integer.parseInt(idPiece));

            listArrayFrom = new ArrayList<InventoryDialogSerialNumber>();

            for (int i = 0; i < inventorySerialNos.size(); i++) {
                InventoryDialogSerialNumber h = new InventoryDialogSerialNumber();
                h.setId(i + 1);
                h.setName(inventorySerialNos.get(i).getSerialNumber());
                h.setSelected(false);

                //new changes
                h.setIdPieceSerial(inventorySerialNos.get(i).getIdSerialNumber());
                h.setStockId(inventorySerialNos.get(i).getIdStock());
                h.setIdPiece(inventorySerialNos.get(i).getIdPiece());

                if (TextUtils.isEmpty(inventorySerialNos.get(i).getIdInterv())
                        || inventorySerialNos.get(i).getDateUsed() == null) {
                    listArrayFrom.add(h);
                }


            }

            restockSerialNos.setItems(listArrayFrom,
                    getResources().getString(R.string.txt_serializable), -1,
                    new RestockSerialNumberListener() {

                        @Override
                        public void onItemsSelected(List<String> items,
                                                    List<String> itemsPieceSerial) {
                            restockSerialNoList = items;
                            restockSerialPieceNoList = itemsPieceSerial;

                            if (items.size() > 0) {
                                restockSerialNos.setVisibility(View.INVISIBLE);
                                relRestockSerialContainer
                                        .setVisibility(View.VISIBLE);
                                generateTextView(items,
                                        relRestockSerialContainer);
                            } else {
                                clearRestockSerialNumbers();
                            }

                            /*
                             * if the user doesn't enter any qty, then set the
                             * size of list to it. Or else leave as it is.
                             */
                            // if (TextUtils.isEmpty(edtRestockQty.getText()
                            // .toString())) {
                            // edtRestockQty.setText("" + items.size());
                            // edtRestockQty.setSelection(edtRestockQty
                            // .getText().toString().length());
                            // }
                            if (items.size() > 0) {
                                edtRestockQty.setText("" + items.size());
                                edtRestockQty.setSelection(edtRestockQty
                                        .getText().toString().length());
                            } else {
                                edtRestockQty.setText("");
                            }
                        }


                    });
        } else {
            restockSerialNos.setItems(null,
                    getResources().getString(R.string.txt_serializable), -1,
                    new RestockSerialNumberListener() {

                        @Override
                        public void onItemsSelected(List<String> items, List<String> itemsPieceSerial) {
                        }
                    });
        }
    }

    /**
     * Load the list for Return serial number dialog view.
     */
    private void loadReturnSerial() {
        clearReturnSerialNumbers();

        if (!spinnerReturnTo
                .getSelectedItem()
                .toString()
                .equalsIgnoreCase(
                        getResources().getString(R.string.txt_send_to_label))) {
            inventorySerialNos = dao.getDepotSerialNumbers(idUserStock,
                    Integer.parseInt(idPiece));

            listArrayTo = new ArrayList<InventoryDialogSerialNumber>();
            for (int i = 0; i < inventorySerialNos.size(); i++) {
                InventoryDialogSerialNumber h = new InventoryDialogSerialNumber();
                h.setId(i + 1);
                h.setName(inventorySerialNos.get(i).getSerialNumber());
                h.setSelected(false);

                //new changes
                h.setIdPieceSerial(inventorySerialNos.get(i).getIdSerialNumber());
                h.setStockId(inventorySerialNos.get(i).getIdStock());
                h.setIdPiece(inventorySerialNos.get(i).getIdPiece());

                if (TextUtils.isEmpty(inventorySerialNos.get(i).getIdInterv())
                        || inventorySerialNos.get(i).getDateUsed() == null) {
                    listArrayTo.add(h);
                }
            }

            returnSerialNos.setItems(listArrayTo,
                    getResources().getString(R.string.txt_serializable), -1,
                    new ReturnSerialNumbersListener() {

                        @Override
                        public void onItemsSelected(List<String> items,List<String> itemsPieceSerial) {

                            returnSerialNoList = items;
                            returnSerialPieceNoList=itemsPieceSerial;

                            if (items.size() > 0) {
                                returnSerialNos.setVisibility(View.INVISIBLE);
                                relReturnSerialContainer
                                        .setVisibility(View.VISIBLE);
                                generateTextView(items,
                                        relReturnSerialContainer);
                            } else {
                                clearReturnSerialNumbers();
                            }

                            /*
                             * if the user doesn't enter any qty, then set the
                             * size of list to it. Or else leave as it is.
                             */

                            if (items.size() > 0) {
                                edtReturnQty.setText("" + items.size());
                                edtReturnQty.setSelection(edtReturnQty
                                        .getText().toString().length());
                            } else {
                                edtReturnQty.setText("");
                            }

                        }
                    });
        } else {
            returnSerialNos.setItems(null,
                    getResources().getString(R.string.txt_serializable), -1,
                    new ReturnSerialNumbersListener() {

                        @Override
                        public void onItemsSelected(List<String> items,List<String> itemsPieceSerial) {
                        }
                    });
        }
    }

    /**
     * @param listSel , textViewContainer
     *                <p>
     *                method to generate textview dynamically and add it to the
     *                relative layout. If the textview have the size to fit into
     *                first row, it will added or else will added to the next row.
     *                </p>
     */
    @SuppressWarnings("deprecation")
    private void generateTextView(final List<String> listSel,
                                  final RelativeLayout textViewContainer) {

        textViewContainer.removeAllViews();

        for (int i = 0; i < listSel.size(); i++) {

            TextView tv = new TextView(this);
            Typeface typeFaceAvenir = Typeface.createFromAsset(getAssets(),
                    getResources().getString(R.string.fontName_avenir));
            tv.setText(listSel.get(i));
            tv.setBackgroundDrawable(getResources().getDrawable(
                    R.drawable.boxframe_serial_not_text));

            tv.setId(i + 1);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
                    .getDimension(R.dimen.textSizeDaysTv));
            tv.setSingleLine(true);
            tv.setPadding(10, 5, 10, 5);
            tv.setTypeface(typeFaceAvenir);
            textViewContainer.setVisibility(View.INVISIBLE);
            textViewContainer.addView(tv);
        }

        textViewContainer.post(new Runnable() {
            @Override
            public void run() {
                int totalWidth = textViewContainer.getWidth();

                // loop through each text view, and set its layout
                // params
                for (int i = 0; i < listSel.size(); i++) {
                    View child = textViewContainer.getChildAt(i);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(10, 10, 0, 0);
                    // this text view can fit in the same row so
                    // lets place it relative to the previous one.
                    if (child.getWidth() < totalWidth) {

                        if (i > 0) { // i == 0 is in correct
                            // position
                            params.addRule(RelativeLayout.RIGHT_OF,
                                    textViewContainer.getChildAt(i - 1).getId());
                            params.addRule(RelativeLayout.ALIGN_BOTTOM,
                                    textViewContainer.getChildAt(i - 1).getId());
                        }
                    } else {
                        // place it in the next row.
                        totalWidth = textViewContainer.getWidth();
                        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        params.addRule(RelativeLayout.BELOW, textViewContainer
                                .getChildAt(i - 1).getId());
                    }
                    child.setLayoutParams(params);
                    totalWidth = totalWidth - child.getWidth() - 10;
                }
                textViewContainer.setVisibility(View.VISIBLE);
                textViewContainer.requestLayout();
            }
        });

    }

    /**
     * Clear all the selected serial numbers in Restock block
     */
    private void clearRestockSerialNumbers() {
        restockSerialNoList.clear();
        relRestockSerialContainer.removeAllViews();
        restockSerialNos.setVisibility(View.VISIBLE);
        relRestockSerialContainer.setVisibility(View.INVISIBLE);
        restockSerialNos.setText("");
        restockSerialNos.setHint(getResources().getString(
                R.string.txt_serial_nos_label));
    }

    /**
     * Checks the entered qty and selected qty are equal.
     *
     * @param edtQty
     * @param serialNoList
     * @return true - if the qty is same, false - if it's not same.
     */
    private boolean validateQty(EditText edtQty, List<String> serialNoList) {
        String qtyStr = edtQty.getText().toString();

        if (TextUtils.isEmpty(qtyStr) || qtyStr.equalsIgnoreCase("0")) {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.txt_validate_qty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (serialNoList.size() == 0) {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.txt_validate_serial_no),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else {
            int qty = Integer.parseInt(qtyStr);
            int listQty = serialNoList.size();
            if (qty == listQty) {
                return true;
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        getResources().getString(
                                R.string.txt_validate_serial_no),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        // if (!TextUtils.isEmpty(qtyStr) && !qtyStr.equalsIgnoreCase("0")) {
        // int qty = Integer.parseInt(qtyStr);
        // int listQty = serialNoList.size();
        // if (qty == listQty) {
        // return true;
        // } else {
        // return false;
        // }
        // } else {
        // return false;
        // }
    }

    /**
     * Clear all the selected serial numbers in Return block
     */
    private void clearReturnSerialNumbers() {
        returnSerialNoList.clear();
        relReturnSerialContainer.removeAllViews();
        returnSerialNos.setVisibility(View.VISIBLE);
        relReturnSerialContainer.setVisibility(View.INVISIBLE);
        returnSerialNos.setText("");
        returnSerialNos.setHint(getResources().getString(
                R.string.txt_serial_nos_label));
    }

    /**
     * Gets the depot id based on the name of depot selected in spinner.
     *
     * @param stockArray
     * @param spinner
     * @return idStock
     */
    private int getIdPosition(ArrayList<InventoryStocksBeans> stockArray,
                              Spinner spinner) {
        int pos = 0;
        String strDepotName = spinner.getSelectedItem().toString();

        if (isSerializable == 1 && strDepotName.contains("-")) {
            strDepotName = strDepotName.substring(0,
                    strDepotName.lastIndexOf("-") - 1);
//            strDepotName = strDepotName.substring(0,
//                    strDepotName.indexOf("-") - 1);
        }

        for (int i = 0; i < stockArray.size(); i++) {
            if (stockArray.get(i).getNameStock().equalsIgnoreCase(strDepotName)) {
                pos = i;
            }
        }
        return pos;
    }

    /**
     * Validate the values before entered into DB
     *
     * @param spinnerDepot
     * @param edtQty
     * @param serialNoList
     */
    private void validateValues(Spinner spinnerDepot, EditText edtQty,
                                List<String> serialNoList,List<String> serialPieceNoList,
                                boolean isReturn) {
        if (spinnerDepot.getSelectedItemPosition() != 0) {
            if (!TextUtils.isEmpty(edtQty.getText().toString())) {
                if ((isSerializable == 1 && rdoTransfer.isChecked())
                        || (isSerializable == 1 && isReturn)) {
                    if (validateQty(edtQty, serialNoList)) {

                        // TODO get the idStock of the currently selected depot
                        // in spinner

                        String idStockSource = inventoryStockArray
                                .get(getIdPosition(inventoryStockArray,
                                        spinnerDepot)).getIdStock();
                        // String idStockSource = inventoryStockArray.get(
                        // spinnerDepot.getSelectedItemPosition() - 1)
                        // .getIdStock();
                        String qty = edtQty.getText().toString();
                        String idStockDest = idUserStock;
                        String flTransfer;
                        flTransfer = "1";
                        String flUrgent = "0";
                        if (isReturnSaved) {
                            String[] params = {idStockDest, idStockSource,
                                    qty, flTransfer, flUrgent};
                            AddInventoryPiece addInventoryPiece = new AddInventoryPiece();
                            addInventoryPiece.execute(params);
                        } else {
                            String[] params = {idStockSource, idStockDest,
                                    qty, flTransfer, flUrgent};
                            AddInventoryPiece addInventoryPiece = new AddInventoryPiece();
                            addInventoryPiece.execute(params);
                        }
                    }
                } else {
                    String qtyStr = edtQty.getText().toString();
                    if (!qtyStr.equalsIgnoreCase("0")) {

                        // TODO get the idStock of the currently selected depot
                        // in spinner

                        String idStockSource = inventoryStockArray
                                .get(getIdPosition(inventoryStockArray,
                                        spinnerDepot)).getIdStock();
                        // String idStockSource = inventoryStockArray.get(
                        // spinnerDepot.getSelectedItemPosition() - 1)
                        // .getIdStock();
                        String qty = edtQty.getText().toString();
                        String idStockDest = idUserStock;
                        String flTransfer;
                        String flUrgent = "0";
                        if (!isReturnSaved) {
                            if (rdoTransfer.isChecked()) {
                                flTransfer = "1";
                            } else {
                                flTransfer = "0";

                                //urgent flag for request
                                if (chkBxUrgent.isChecked()) {
                                    flUrgent = "1";
                                } else {
                                    flUrgent = "0";
                                }
                            }
                            String[] params = {idStockSource, idStockDest,
                                    qty, flTransfer, flUrgent};
                            AddInventoryPiece addInventoryPiece = new AddInventoryPiece();
                            addInventoryPiece.execute(params);
                        } else {
                            flTransfer = "1";
                            String[] params = {idStockDest, idStockSource,
                                    qty, flTransfer, flUrgent};
                            AddInventoryPiece addInventoryPiece = new AddInventoryPiece();
                            addInventoryPiece.execute(params);
                        }

                    } else {
                        Toast.makeText(
                                getApplicationContext(),
                                getResources().getString(
                                        R.string.txt_validate_qty),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(),
                        getResources().getString(R.string.txt_validate_qty),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.txt_select_one_depot),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // ******************************************LOCAL...METHODS...ENDS...HERE*********************************************

    // *******************************************ASYNC...CLASS...STARTS...HERE*********************************************
    private class AddInventoryPiece extends AsyncTaskCoroutine<String, Boolean> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressLint("SimpleDateFormat")
        @Override
        public Boolean doInBackground(String... params) {


            String currentTime = sdf.format(calendar.getTime());
            boolean drp = dao.addInventoryTransfer(params[0], params[1],
                    Integer.parseInt(idPiece), idUser,
                    Integer.parseInt(params[2]), Integer.parseInt(params[3]), Integer.parseInt(params[4]),
                    currentTime);
            return drp;
        }

        @Override
        public void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            DialogUtils.dismissProgressDialog();
            boolean drp = result.booleanValue();
            if (drp) {
                if (isSerializable == 0) {
                    if (!isReturnSaved) {
                        spinnerRestockFrom.setSelection(0);
                        edtRestockQty.setText("");
                        clearRestockSerialNumbers();
                    } else {
                        spinnerReturnTo.setSelection(0);
                        edtReturnQty.setText("");
                        clearReturnSerialNumbers();
                    }
                    setListAdapter();
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.txt_ack_msg), Toast.LENGTH_SHORT)
                            .show();
                } else {
                    UpdateSerialNumberId updateSerial = new UpdateSerialNumberId();
                    updateSerial.execute();
                }
            } else
                Toast.makeText(getApplicationContext(),
                        getString(R.string.msg_error), Toast.LENGTH_SHORT)
                        .show();
        }
    }

    /**
     * Async task to update the serial number's stock id before an item is
     * transfered/returned.
     */
    private class UpdateSerialNumberId extends AsyncTaskCoroutine<String, Boolean> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressLint("SimpleDateFormat")
        @Override
        public Boolean doInBackground(String... params) {
            boolean isChanged = true;
            List<String> serialNoList;
            List<String> serialPieceNoList;
            String idStock;
            String idPieceSerial;
            if (!isReturnSaved) {
                serialNoList = restockSerialNoList;
                serialPieceNoList = restockSerialPieceNoList;
                idStock = idUserStock;
            } else {
                serialNoList = returnSerialNoList;
                serialPieceNoList = returnSerialPieceNoList;

                idStock = inventoryStockArray.get(
                        spinnerReturnTo.getSelectedItemPosition() - 1)
                        .getIdStock();

            }
            for (int i = 0; i < serialNoList.size(); i++) {
                boolean drp = dao
                        .updateSerialNoId(idStock, serialNoList.get(i),
                                Integer.parseInt(idPiece),serialPieceNoList.get(i));
                if (!drp)
                    isChanged = false;
            }
            return isChanged;
        }

        @Override
        public void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            DialogUtils.dismissProgressDialog();
            boolean drp = result.booleanValue();
            if (drp) {
                if (!isReturnSaved) {
                    spinnerRestockFrom.setSelection(0);
                    edtRestockQty.setText("");
                    clearRestockSerialNumbers();
                } else {
                    spinnerReturnTo.setSelection(0);
                    edtReturnQty.setText("");
                    clearReturnSerialNumbers();
                }
                setListAdapter();
                setSpinnerAdapter();
                Toast.makeText(getApplicationContext(),
                        getString(R.string.txt_ack_msg), Toast.LENGTH_SHORT)
                        .show();
            } else
                Toast.makeText(getApplicationContext(),
                        getString(R.string.msg_error), Toast.LENGTH_SHORT)
                        .show();
        }
    }

    // *******************************************ASYNC...CLASS...ENDS...HERE*********************************************

    // *******************************************LISTENER...STARTS...HERE**************************************************

    /**
     * Click listener.
     */
    OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.txtSpinnerDepotIcon:
                    spinnerRestockFrom.performClick();
                    break;
                case R.id.txtSpinnerSendIcon:
                    spinnerReturnTo.performClick();
                    break;
                case R.id.relReturnSerialContainer:
                    returnSerialNos.performClick();
                    break;
                case R.id.relRestockSerialContainer:
                    restockSerialNos.performClick();
                    break;
                case R.id.txtRestockSave:
                    hideSoftKeyboard();
                    isReturnSaved = false;
                    validateValues(spinnerRestockFrom, edtRestockQty,
                            restockSerialNoList,restockSerialPieceNoList, false);
                    break;
                case R.id.txtRestockCancel:
                    rdoTransfer.setChecked(true);
                    spinnerRestockFrom.setSelection(0);
                    edtRestockQty.setText("");
                    clearRestockSerialNumbers();
                    break;
                case R.id.txtReturnSave:
                    hideSoftKeyboard();
                    isReturnSaved = true;
                    validateValues(spinnerReturnTo, edtReturnQty,
                            returnSerialNoList,returnSerialPieceNoList, true);
                    break;
                case R.id.txtReturnCancel:
                    spinnerReturnTo.setSelection(0);
                    edtReturnQty.setText("");
                    clearReturnSerialNumbers();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * Check changed listener.
     */
    OnCheckedChangeListener checkChangedListener = new OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.rdoTransfer) {
                if (isSerializable != 0) {
                    frameRestockSerialView.setVisibility(View.VISIBLE);
                    edtRestockQty.clearFocus();
                }
                chkBxUrgent.setVisibility(View.GONE);
                chkBxUrgent.setChecked(false);
            } else {
                frameRestockSerialView.setVisibility(View.GONE);
                chkBxUrgent.setVisibility(View.VISIBLE);
                clearRestockSerialNumbers();
                edtRestockQty.setText("");
            }

        }
    };

    /**
     * Item selected listener.
     */
    OnItemSelectedListener itemSelectedListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {

            Spinner spinner = (Spinner) parent;
            switch (spinner.getId()) {
                case R.id.spinnerFrom:
                    loadRestockSerial(getIdPosition(inventoryStockArray,
                            spinnerRestockFrom));
                    break;
                case R.id.spinnerSendTo:
                    loadReturnSerial();
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    /**
     * Focus change listener.
     */
    OnFocusChangeListener focusListener = new OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            switch (v.getId()) {
                case R.id.edtAddRestockQty:
                    if (hasFocus && isSerializable == 1) {
                        if (rdoTransfer.isChecked()) {
                            edtRestockQty.clearFocus();
                            edtRestockQty.setCursorVisible(false);
                            restockSerialNos.performClick();
                        } else {
                            edtRestockQty.requestFocus();
                            edtRestockQty.setCursorVisible(true);
                        }
                    }
                    break;
                case R.id.edtReturnQty:
                    if (hasFocus && isSerializable == 1) {
                        edtReturnQty.clearFocus();
                        edtReturnQty.setCursorVisible(false);
                        returnSerialNos.performClick();
                    }
                    break;

            }
        }
    };

    /**
     * Touch listener.
     */
    OnTouchListener touchListener = new OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // shows a toast if there is no depot available for a piece.
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (restockFromList.size() == 1) {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.txt_no_parts),
                            Toast.LENGTH_SHORT).show();
                } else {
                    v.performClick();
                }
                return true;
            } else {
                return false;
            }

        }
    };
    // *******************************************LISTENER...ENDS...HERE**************************************************

}
