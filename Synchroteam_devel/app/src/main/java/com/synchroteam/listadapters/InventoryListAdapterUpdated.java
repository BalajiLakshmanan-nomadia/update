package com.synchroteam.listadapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;

import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.beans.InventoryDialogSerialNumber;
import com.synchroteam.beans.InventoryItemBeans;
import com.synchroteam.beans.InventorySerialNumbersBeans;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.fragmenthelper.InventoryFragmentHelperNew;
import com.synchroteam.scanner.CodeScannerActivity;
import com.synchroteam.synchroteam.InventoryDetails;
import com.synchroteam.synchroteam.PartsAndServicesListNew;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AccentInsensitive;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.RequestCode;
import com.synchroteam.utils.ScannerBar;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class InventoryListAdapterUpdated extends ArrayAdapter<String> {

    private static Context context;
    private ArrayList<InventoryItemBeans> inventoryArraryList;
    private static Dao dataAccessObject;
    private static String idIntervention;
    private static String dateUsed;
    private int valDepot = 0;
    private boolean isInvoice, isFiltered;
    private static final String TAG = "InventoryListAdapter";

    static ArrayList<InventoryDialogSerialNumber> listSelected = new ArrayList<>();
    // static ArrayList<InventoryDialogSerialNumber> unSelectedList = new
    // ArrayList<>();
    static Typeface typeFace;
    public static RelativeLayout relSerialConatainer;
    static View dialogView;
    static ScrollView scrollContainer;
    static SerialNumberAdapter adapter;

    static Calendar calendar;
    static SimpleDateFormat sdf;

    private long mLastClickTime = 0;

    private static boolean changesDoneInParts = false;

    InventoryFragmentHelperNew inventoryFragment;

    /**
     * The index.
     */
    private int listIndex;

    /**
     * The base count.
     */
    private int baseCount = 20;

    /**
     * The is user searching.
     */
    private boolean isUserSearching = false;

    public boolean isCategoryFiltered = false;

    private GestionAcces access;

    private boolean isPartsAndServices;

    private LayoutInflater mInflater;

    private String currencyType;

    boolean fromParts = false;

    public InventoryListAdapterUpdated(Context context, int resource,
                                       ArrayList<InventoryItemBeans> inventoryArrayList,
                                       Dao dataAccessObject, String idInterventiion, boolean isInvoice, boolean isPartsAndServices,
                                       InventoryFragmentHelperNew inventoryFragment) {
        super(context, resource);
        this.dataAccessObject = dataAccessObject;
        this.idIntervention = idInterventiion;
        this.context = context;
        this.inventoryArraryList = inventoryArrayList;
        this.isInvoice = isInvoice;
        this.isPartsAndServices = isPartsAndServices;
        this.inventoryFragment = inventoryFragment;


        typeFace = Typeface.createFromAsset(context.getAssets(),
                context.getString(R.string.fontName_fontAwesome));

        mInflater = ((Activity) context).getLayoutInflater();

        currencyType = dataAccessObject.getDeviseCustomer();
    }


    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return inventoryArraryList.size();
//        return inventoryArraryList.size() > 0 ? inventoryArraryList.size() : 0;
    }


    /**
     * Sets the index position.
     *
     * @param index the new index position
     */
    public void setIndexPosition(int index) {

        this.listIndex = index;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.rowview_inventory_item,
                    parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        try {

            InventoryItemBeans pice = inventoryArraryList.get(position);
            String noOfPieces = null;
            if (dataAccessObject != null) {
                noOfPieces = dataAccessObject.getNumberOfPices(inventoryArraryList
                        .get(position).getIdPiece(), idIntervention);
            }

            if (inventoryFragment == null) {
                fromParts = true;
                if (pice.getIsTracked() == 0) {
                    holder.linQtyContainer.setVisibility(View.GONE);
                } else {
                    holder.linQtyContainer.setVisibility(View.VISIBLE);
                }

                if (!isInvoice) {

                    holder.linItemBg.setOnClickListener(clickListener);

                    Bundle bundle = new Bundle();
                    bundle.putString(KEYS.Items.PIECE_ID, pice.getIdPiece());
                    bundle.putInt(KEYS.InventoryListValues.IS_SERIALIZABLE,
                            pice.getIsSerializable());
                    bundle.putString(KEYS.Items.NUMBEROFPICES, noOfPieces);
                    bundle.putString("description_item", pice.getDescription());

                    holder.linItemBg.setTag(bundle);
                    holder.linItemBg.setId(position);

                    if (noOfPieces.equals("0") || noOfPieces.equals("0.00") || noOfPieces.equals("0.0")) {
                        bundle.putBoolean(KEYS.Items.IS_ITEM_SELECTED, false);
                        holder.linItemBg
                                .setBackgroundResource(R.drawable.boxframe_textview_layout);
                    } else {
                        bundle.putBoolean(KEYS.Items.IS_ITEM_SELECTED, true);
                        holder.linItemBg
                                .setBackgroundResource(R.drawable.boxframe_textview_green_layout);
                    }

                } else {

                    holder.linItemBg.setOnClickListener(invoiceListener);

                    Bundle bundle = new Bundle();
                    bundle.putString(KEYS.InventoryListValues.REFERENCE_NAME,
                            pice.getReference());
                    bundle.putString(KEYS.InventoryListValues.CATEGORY_NAME,
                            pice.getCategory());
                    bundle.putString(KEYS.InventoryListValues.PARTS_SERVICES_NAME,
                            pice.getPartsService());
                    bundle.putString(KEYS.InventoryListValues.PRICE_VALUE,
                            String.valueOf(pice.getCostOfItem()));
                    bundle.putString(KEYS.InventoryListValues.TAX_RATE,
                            dataAccessObject.getTaxById(pice.getIdTaxRate()));
                    bundle.putString("description_item", pice.getDescription());

                    holder.linItemBg.setTag(bundle);
                    holder.linItemBg.setId(position);
                }
            } else {
                fromParts = false;

                holder.linItemBg.setOnClickListener(convertViewListener);

                Bundle bundle = new Bundle();
                bundle.putString(KEYS.InventoryListValues.ID_PIECE,
                        pice.getIdPiece());
                bundle.putString(KEYS.InventoryListValues.REFERENCE_NAME,
                        pice.getReference());
                bundle.putString(KEYS.InventoryListValues.CATEGORY_NAME,
                        pice.getCategory());
                bundle.putString(KEYS.InventoryListValues.PARTS_SERVICES_NAME,
                        pice.getPartsService());
                bundle.putString(KEYS.InventoryListValues.PRICE_VALUE,
                        String.valueOf(pice.getCostOfItem()));
                bundle.putInt(KEYS.InventoryListValues.IS_SERIALIZABLE,
                        pice.getIsSerializable());
                bundle.putString(KEYS.InventoryListValues.QTY,
                        pice.getNoOfPieces());
                bundle.putString("description_item", pice.getDescription());

                holder.linItemBg.setTag(bundle);

            }

            if (inventoryArraryList != null && inventoryArraryList.size() > 0) {
                String reference = inventoryArraryList.get(position).getReference();
                Logger.log(TAG, "INVENTORY LIST ADAPTER NEW REFERENCE VALUE IS:====>" + reference);
                String category = inventoryArraryList.get(position).getCategory();

                String itemCategory = category.replace("|", " > ");
                holder.txtCategory.setText(itemCategory);
                holder.txtPartsService.setText(inventoryArraryList.get(position)
                        .getPartsService());
                holder.txtCost.setText(BigDecimal.valueOf(inventoryArraryList.get(position)
                        .getCostOfItem()).toPlainString() + " " + currencyType);

                holder.txtQty.setText(String.valueOf(inventoryArraryList.get(position)
                        .getNoOfPieces()));

                access = dataAccessObject.getAcces();
                if (access.getFlMobPrice() == 1) {
                    holder.txtCost.setVisibility(View.INVISIBLE);
                } else {
                    holder.txtCost.setVisibility(View.VISIBLE);
                }

                if (!TextUtils.isEmpty(reference)) {
                    Logger.log(TAG, "INVENTORY LIST ADAPTER NEW REFERENCE VALUE INSIDE IF IS:====>" + inventoryArraryList.get(position)
                            .getReference());
                    holder.txtReference.setText(inventoryArraryList.get(position)
                            .getReference());
                } else {
                    holder.txtReference.setText("-");
                }

                if (pice.getIsSerializable() == 0) {
                    holder.txtSerializableIcon.setVisibility(View.GONE);
                    holder.txtSerializable.setVisibility(View.GONE);
                    // holder.txtSerializableIcon.setTextColor(context.getResources()
                    // .getColor(R.color.invoice_label_color));
                    // holder.txtSerializable.setTextColor(context.getResources()
                    // .getColor(R.color.invoice_label_color));
                } else {
                    holder.txtSerializableIcon.setVisibility(View.VISIBLE);
                    holder.txtSerializable.setVisibility(View.VISIBLE);
                    // holder.txtSerializableIcon.setTextColor(context.getResources()
                    // .getColor(R.color.serializable_color));
                    // holder.txtSerializable.setTextColor(context.getResources()
                    // .getColor(R.color.serializable_color));
                }
            }
        } catch (Exception e) {
            Toast.makeText(context, "adapter " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            notifyDataSetChanged();

        }
        return convertView;
    }

    /**
     * Add a parts & service item
     *
     * @param bundle
     * @param v
     * @param index
     */
    private void addItem(Bundle bundle, View v, int index, String qty) {

        InventoryItemBeans pice = inventoryArraryList.get(index);
        pice.setNoOfPieces(qty);
        inventoryArraryList.set(index, pice);

        bundle.putBoolean(KEYS.Items.IS_ITEM_SELECTED, false);

        v.setTag(bundle);

        String[] tb = dataAccessObject.getNbreSorPieByIdPieAndIdInter(
                bundle.getString(KEYS.Items.PIECE_ID), idIntervention);
        if (tb != null) {

            // qt.setText(String.valueOf(tb[0]));

            valDepot = Integer.parseInt(tb[1]);

            dataAccessObject.majQuantite(bundle.getString(KEYS.Items.PIECE_ID),
                    idIntervention, String.valueOf(qty), valDepot, null);

        } else
            dataAccessObject.insertSortiePiece(idIntervention,
                    bundle.getString(KEYS.Items.PIECE_ID), String.valueOf(qty),
                    valDepot, null);

        valDepot = 0;

        ((PartsAndServicesListNew) context).closeList();
    }


    /**
     * The click listener.
     */
    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(final View v) {

            // preventing double click.
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return;
            }

            mLastClickTime = SystemClock.elapsedRealtime();

            final Bundle bundle = (Bundle) v.getTag();
            final int index = v.getId();

            boolean isItemSelected = bundle
                    .getBoolean(KEYS.Items.IS_ITEM_SELECTED);
            int isSerializable = bundle
                    .getInt(KEYS.InventoryListValues.IS_SERIALIZABLE);

            // if the item is non-serializable
            if (isSerializable == 0) {
                if (isItemSelected) {
//					addItem(bundle, v, index, "0");

                    AlertDialog.Builder adb = new AlertDialog.Builder(
                            context);

                    View alertDialogView = mInflater.inflate(
                            R.layout.dialogaddqantity, null);
                    adb.setView(alertDialogView);
                    final EditText commentEt = (EditText) alertDialogView
                            .findViewById(R.id.commentaire);

                    String dataQantitity = bundle.getString(KEYS.Items.NUMBEROFPICES);
                    String tempArr[] = null;

                    try {
                        tempArr = dataQantitity.split("\\.");
                    } catch (Exception e) {
                        tempArr[0] = dataQantitity;
                        tempArr[1] = "0";
                        e.printStackTrace();
                    }
                    int roundQantitity = Integer.parseInt(tempArr[1]);

                    if (roundQantitity > 0) {
                        commentEt.setText(dataQantitity);
                    } else {
                        commentEt.setText(tempArr[0]);
                    }

                    // all the text is selected
                    commentEt.setSelectAllOnFocus(true);
                    // for request to focus on editText and keyboard ups
                    commentEt.requestFocus();
                    commentEt.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager keyboard = (InputMethodManager) context.getSystemService(
                                    Context.INPUT_METHOD_SERVICE);
                            keyboard.showSoftInput(commentEt, 0);
                        }
                    }, 20);

                    adb.setNeutralButton(R.string.modifier,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String pieces;
                                    try {
                                        pieces = Double.parseDouble(commentEt
                                                .getText().toString()) + "";
                                    } catch (Exception e) {
                                        pieces = "0.0";
                                    }
                                    if (commentEt.getText().length() == 0
                                            || commentEt.getText().toString()
                                            .equals("0")
                                            || commentEt.getText().toString()
                                            .equals("0.0")
                                            || commentEt.getText().toString()
                                            .equals("")
                                            || !checkQte(commentEt.getText()
                                            .toString())) {
                                        Toast.makeText(context,
                                                R.string.addQuantity,
                                                Toast.LENGTH_LONG).show();
                                    } else {
                                        addItem(bundle, v, index, pieces);
                                    }
                                }
                            });

                    adb.setNegativeButton(R.string.textCancelLable,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                    // remplir();
                                }
                            });

                    adb.show();

                } else {
                    addItem(bundle, v, index, "1");
                }


            }
            // show a dialog for the serializable parts.
            else {

                User user = dataAccessObject.getUser();
                String idPiece = bundle.getString(KEYS.Items.PIECE_ID);
                // serial no array sorted ny serial number
                String idUserStock = user.getIdStock();
                ArrayList<InventorySerialNumbersBeans> inventorySerialNos = dataAccessObject
                        .getDepotSerialNumbers(idUserStock,
                                Integer.parseInt(idPiece));

                ArrayList<InventoryDialogSerialNumber> items = new ArrayList<InventoryDialogSerialNumber>();
                ArrayList<String> serialList = dataAccessObject.getTakeBackPieceSerialList(idPiece, idIntervention);
                for (int i = 0; i < inventorySerialNos.size(); i++) {
                    InventoryDialogSerialNumber h = new InventoryDialogSerialNumber();
                    h.setId(i + 1);
                    h.setName(inventorySerialNos.get(i).getSerialNumber());
                    h.setIdPieceSerial(inventorySerialNos.get(i)
                            .getIdSerialNumber());
                    boolean isSelected = dataAccessObject.checkSelectedPiece(
                            idPiece, idIntervention, idUserStock,
                            inventorySerialNos.get(i).getIdSerialNumber());
                    h.setSelected(isSelected);

                    if (TextUtils.isEmpty(inventorySerialNos.get(i)
                            .getIdInterv())
                            || inventorySerialNos.get(i).getIdInterv()
                            .equalsIgnoreCase(idIntervention) || inventorySerialNos.get(i).getDateUsed() == null) {

                        //check if the serial no is taken back in the same job
                        if (serialList != null && serialList.size() > 0) {
                            int serialPresent = -1;
                            for (int j = 0; j < serialList.size(); j++) {
                                if (inventorySerialNos.get(i).getSerialNumber().trim().equals(serialList.get(j))) {
                                    serialPresent = 1;
                                }
                            }
                            if (serialPresent == -1)
                                items.add(h);

                        } else {
                            items.add(h);
                        }


                    }
                }

//                ArrayList<InventorySerialNumbersBeans> list = new ArrayList<>();
//                list = dataAccessObject
//                        .getPartsSerialNumbers(idPiece);
//
//                if (items.size() > 0||list.size()>0) {
//                    SerialNumberDialog dialog = SerialNumberDialog.getInstance(idPiece, items);
//                    dialog.show(((AppCompatActivity) context)
//                            .getSupportFragmentManager(), "");
//                    listSelected = new ArrayList<InventoryDialogSerialNumber>();
//                } else {
//                    Toast.makeText(getContext(),
//                            getContext().getString(R.string.txt_no_parts),
//                            Toast.LENGTH_SHORT).show();
//
//                }

                ArrayList<InventorySerialNumbersBeans> list = new ArrayList<>();

                boolean takeFrom = false;
                if (access.getFlTakeFrom() == 1) {
                    takeFrom = true;
                } else {
                    takeFrom = false;
                }
                ArrayList<InventorySerialNumbersBeans> listnew = new ArrayList<>();
                if (fromParts && takeFrom) {
                    list = dataAccessObject
                            .getPartsSerialNumbers(idPiece);
                    for(int i=0;i<list.size();i++){
                        if(list.get(i).getIdInterv()==null &&
                                list.get(i).getDateUsed()==null){
                            listnew.add(list.get(i));

                        }else if(list.get(i).getIdInterv().equals(idIntervention) &&
                                list.get(i).getDateUsed()!=null){
                            listnew.add(list.get(i));

                        }
                    }
                }

                if (items.size() > 0) {
                    SerialNumberDialog dialog = SerialNumberDialog.getInstance(idPiece,
                            items, fromParts, takeFrom);
                    dialog.show(((AppCompatActivity) context)
                            .getSupportFragmentManager(), "");
                    listSelected = new ArrayList<InventoryDialogSerialNumber>();
                } else if (fromParts && listnew != null && listnew.size() > 0) {
                    SerialNumberDialog dialog = SerialNumberDialog.getInstance(idPiece,
                            items, fromParts, takeFrom);
                    dialog.show(((AppCompatActivity) context)
                            .getSupportFragmentManager(), "");
                    listSelected = new ArrayList<InventoryDialogSerialNumber>();
                } else {

                    Toast.makeText(getContext(),
                            getContext().getString(R.string.txt_no_parts),
                            Toast.LENGTH_SHORT).show();

                }

                // unSelectedList = new
                // ArrayList<InventoryDialogSerialNumber>();
            }
        }
    };

    private OnClickListener convertViewListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            Bundle bundle = (Bundle) v.getTag();

            Intent intent = new Intent(context,
                    InventoryDetails.class);
            intent.putExtra(KEYS.InventoryListValues.ID_PIECE,
                    bundle.getString(KEYS.InventoryListValues.ID_PIECE));
            intent.putExtra(KEYS.InventoryListValues.REFERENCE_NAME,
                    bundle.getString(KEYS.InventoryListValues.REFERENCE_NAME));
            intent.putExtra(KEYS.InventoryListValues.CATEGORY_NAME,
                    bundle.getString(KEYS.InventoryListValues.CATEGORY_NAME));
            intent.putExtra(
                    KEYS.InventoryListValues.PARTS_SERVICES_NAME,
                    bundle.getString(KEYS.InventoryListValues.PARTS_SERVICES_NAME));
            intent.putExtra(KEYS.InventoryListValues.PRICE_VALUE, String
                    .valueOf(bundle
                            .getString(KEYS.InventoryListValues.PRICE_VALUE)));
            intent.putExtra(KEYS.InventoryListValues.IS_SERIALIZABLE,
                    bundle.getInt(KEYS.InventoryListValues.IS_SERIALIZABLE));
            intent.putExtra(KEYS.InventoryListValues.QTY, bundle.getString(KEYS.InventoryListValues.QTY));
            intent.putExtra("description_item", bundle.getString("description_item"));

            context.startActivity(intent);
        }
    };

    private OnClickListener invoiceListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            Bundle bundle = (Bundle) v.getTag();

            Intent intent = ((PartsAndServicesListNew) context).getIntent();
            intent.putExtra(KEYS.InventoryListValues.REFERENCE_NAME,
                    bundle.getString(KEYS.InventoryListValues.REFERENCE_NAME));
            intent.putExtra(KEYS.InventoryListValues.CATEGORY_NAME,
                    bundle.getString(KEYS.InventoryListValues.CATEGORY_NAME));
            intent.putExtra(
                    KEYS.InventoryListValues.PARTS_SERVICES_NAME,
                    bundle.getString(KEYS.InventoryListValues.PARTS_SERVICES_NAME));

            intent.putExtra(KEYS.InventoryListValues.TAX_RATE,
                    bundle.getString(KEYS.InventoryListValues.TAX_RATE));
            intent.putExtra("description_item", bundle.getString("description_item"));

//            intent.putExtra("Price_check",
//                    bundle.getString(KEYS.InventoryListValues.PRICE_VALUE));

            intent.putExtra(KEYS.InventoryListValues.PRICE_VALUE, String
                    .valueOf(bundle
                            .getString(KEYS.InventoryListValues.PRICE_VALUE)));

            ((PartsAndServicesListNew) context).setResult(Activity.RESULT_OK,
                    intent);
            ((PartsAndServicesListNew) context).finish();


        }
    };

    static class ViewHolder {
        TextView txtReference, txtCategory, txtPartsService, txtCost,
                txtSerializable, txtQty;
        LinearLayout linItemBg;
        android.widget.TextView txtSerializableIcon;
        LinearLayout linQtyContainer;

        public ViewHolder(View view) {
            txtReference = (TextView) view.findViewById(R.id.txt_reference);
            txtCategory = (TextView) view.findViewById(R.id.txt_category);
            txtPartsService = (TextView) view
                    .findViewById(R.id.txt_parts_service);
            txtCost = (TextView) view.findViewById(R.id.txtItemCost);
            linItemBg = (LinearLayout) view.findViewById(R.id.linItemBg);
            txtSerializableIcon = (android.widget.TextView) view
                    .findViewById(R.id.txtSerializableIcon);
            txtSerializable = (TextView) view
                    .findViewById(R.id.txtSerializable);
            txtQty = (TextView) view.findViewById(R.id.txt_qty_piece);
            linQtyContainer = (LinearLayout) view.findViewById(R.id.lin_qty_container);

            txtSerializableIcon.setTypeface(typeFace);
        }
    }

    /*
     * Serial number dialog
     */

    @SuppressLint("ValidFragment")
    public static class SerialNumberDialog extends DialogFragment {
        private static final String KEY_ID_PIECE = "id_piece";
        private static final String KEY_ITEMS = "items";
        private static final String KEY_PARTS = "parts";
        private static final String KEY_TAKE_FROM = "take_from";
        EditText edtSearch;
//        String idPiece;
//        ArrayList<InventoryDialogSerialNumber> items;


        public static SerialNumberDialog getInstance(
                String idPiece, ArrayList<InventoryDialogSerialNumber> items,
                boolean fromParts, boolean takeFrom) {
            Bundle bundle = new Bundle();
            bundle.putString(KEY_ID_PIECE, idPiece);
            bundle.putSerializable(KEY_ITEMS, items);
            bundle.putBoolean(KEY_PARTS, fromParts);
            bundle.putBoolean(KEY_TAKE_FROM, takeFrom);
            SerialNumberDialog dialog = new SerialNumberDialog();
            dialog.setArguments(bundle);
            return dialog;
        }

        /*
         * when dismissed invoke the listener to notify that some items are
         * selected.
         */
        @Override
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
        }

        /*
         * sets the height of the dialog to 1/7th of the total screen height.
         */
        @Override
        public void onResume() {
            super.onResume();
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int screenHeight = metrics.heightPixels;
            int dialogHeight = (int) (screenHeight * 0.7);
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.WRAP_CONTENT, dialogHeight);
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Dialog dialog = super.onCreateDialog(savedInstanceState);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(Color.TRANSPARENT));
            WindowManager.LayoutParams wmlp = dialog.getWindow()
                    .getAttributes();
            wmlp.gravity = Gravity.CENTER;
            return dialog;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            dialogView = inflater.inflate(
                    R.layout.dialog_serial_number_spinner, container, false);
            initializeUI();
            setCancelable(false);
            return dialogView;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode,
                                     Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == Activity.RESULT_OK) {
                edtSearch.setText(data.getStringExtra("SCAN_RESULT_CODE"));
                edtSearch.setSelection(edtSearch.getText().length());
            }
        }

        /*
         * Initiates the views
         */
        private void initializeUI() {
            final ListView listView = (ListView) dialogView
                    .findViewById(R.id.listSerialNos);
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            listView.setFastScrollEnabled(false);

            scrollContainer = (ScrollView) dialogView
                    .findViewById(R.id.scrollContainer);

            // ArrayList<InventoryDialogSerialNumber> items = new
            // ArrayList<InventoryDialogSerialNumber>();
            // String[] sno = { "121212", "12121", "121221", "343434" };

            User user = dataAccessObject.getUser();

            // serial no array sorted ny serial number
            String idUserStock = user.getIdStock();
            String idPiece = getArguments().getString(KEY_ID_PIECE);
            boolean fromParts = getArguments().getBoolean(KEY_PARTS);
            boolean takeFrom = getArguments().getBoolean(KEY_TAKE_FROM);

            if (fromParts && takeFrom) {
                ArrayList<InventoryDialogSerialNumber> list = new ArrayList<>();

                ArrayList<InventorySerialNumbersBeans> serialNos = dataAccessObject
                        .getPartsSerialNumbers(idPiece);

                for (int i = 0; i < serialNos.size(); i++) {
                    InventoryDialogSerialNumber h = new InventoryDialogSerialNumber();
                    boolean isSelected = dataAccessObject.checkSelectedPiece(
                            idPiece, idIntervention, idUserStock, serialNos.get(i)
                                    .getIdSerialNumber());
                    h.setId(i + 1);
                    h.setName(serialNos.get(i).getSerialNumber());
                    h.setName(serialNos.get(i).getSerialNumber());
                    h.setIdPieceSerial(serialNos.get(i).getIdSerialNumber());
                    h.setStockId(serialNos.get(i).getIdStock());
                    h.setIdPiece(serialNos.get(i).getIdPiece());
                    h.setSelected(isSelected);
                    String nameStock = dataAccessObject.getStockName(serialNos.get(i).getIdStock()); // to get deponame
                    if (!serialNos.get(i).getIdStock().equalsIgnoreCase(idUserStock)) {
                        h.setStockName(nameStock);
                    }

                    if(serialNos.get(i).getIdInterv()==null &&
                            serialNos.get(i).getDateUsed()==null){
                        list.add(h);
                        if (isSelected) {
                            listSelected.add(h);
                        }
                    }else if(serialNos.get(i).getIdInterv().equals(idIntervention) &&
                            serialNos.get(i).getDateUsed()!=null){
                        list.add(h);
                        if (isSelected) {
                            listSelected.add(h);
                        }
                    }
//                    list.add(h);
//                    if (isSelected) {
//                        listSelected.add(h);
//                    }



//                h.setIdPieceSerial(serialNos.get(i).getIdSerialNumber());
//                h.setSelected(isSelected);
//                if (isSelected) {
//                    listSelected.add(h);
//                }
                }

                adapter = new SerialNumberAdapter(getActivity(), list, idPiece, fromParts, takeFrom);
                listView.setAdapter(adapter);
            } else {
                //v53 code
                ArrayList<InventoryDialogSerialNumber> items = (ArrayList<InventoryDialogSerialNumber>)
                        getArguments().getSerializable(KEY_ITEMS);

                // serial no array sorted by date used
                ArrayList<InventorySerialNumbersBeans> serialNos = dataAccessObject
                        .getPartsSerialNumbers(idUserStock,
                                Integer.parseInt(idPiece));
                for (int i = 0; i < serialNos.size(); i++) {
                    InventoryDialogSerialNumber h = new InventoryDialogSerialNumber();
                    boolean isSelected = dataAccessObject.checkSelectedPiece(
                            idPiece, idIntervention, idUserStock, serialNos.get(i)
                                    .getIdSerialNumber());
                    h.setId(i + 1);
                    h.setName(serialNos.get(i).getSerialNumber());
                    h.setIdPieceSerial(serialNos.get(i).getIdSerialNumber());
                    h.setStockId(serialNos.get(i).getIdStock());
                    h.setIdPiece(serialNos.get(i).getIdPiece());
                    h.setSelected(isSelected);
                    if (isSelected) {
                        listSelected.add(h);
                    }
                }

                adapter = new SerialNumberAdapter(getActivity(), items, idPiece, fromParts, takeFrom);
                listView.setAdapter(adapter);
            }
            android.widget.TextView txtSpinnerIcon = (android.widget.TextView) dialogView
                    .findViewById(R.id.txtSearchIcon);
            android.widget.TextView txtBarcodeIcon = (android.widget.TextView) dialogView
                    .findViewById(R.id.txtBarcodeIcon);
            TextView txtClose = (TextView) dialogView
                    .findViewById(R.id.txtClose);

            txtSpinnerIcon.setTypeface(typeFace);
            txtBarcodeIcon.setTypeface(typeFace);

            edtSearch = (EditText) dialogView
                    .findViewById(R.id.edtSearchSerialNos);

            edtSearch.addTextChangedListener(watcher);

            txtClose.setOnClickListener(clickListener);

            txtBarcodeIcon.setOnClickListener(clickListener);
        }

        /*
         * Text watcher for edit text
         */
        TextWatcher watcher = new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        /*
         * click listener
         */
        OnClickListener clickListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.txtClose:
                        if (changesDoneInParts) {
                            ((PartsAndServicesListNew) context).closeList();
                            changesDoneInParts = false;
                        }
                        dismiss();
                        break;
                    case R.id.txtBarcodeIcon:
                        Intent intent = new Intent(getActivity(), CodeScannerActivity.class);
                        startActivityForResult(intent,
                                RequestCode.REQUEST_CODE_TEXT_BARCODE);
                        break;
                    default:
                        break;
                }
            }
        };
    }

    // *********************************************LIST...VIEW...ADAPTER*******************************************************
    public static class SerialNumberAdapter extends BaseAdapter implements
            Filterable {

        List<InventoryDialogSerialNumber> arrayList;
        List<InventoryDialogSerialNumber> mOriginalValues; // Original Values
        LayoutInflater inflater;
        String idPiece;
        String idUserStock;
        boolean fromParts;
        boolean takeFrom;
        private ArrayList<InventoryDialogSerialNumber> listSelectedSerial;

        public SerialNumberAdapter(Context context,
                                   List<InventoryDialogSerialNumber> arrayList,
                                   String idPiece, boolean fromParts, boolean takeFrom) {
            this.arrayList = arrayList;
            this.idPiece = idPiece;
            inflater = LayoutInflater.from(context);
            // generate the textview for first time
            generateTextView(listSelected);

            User user = dataAccessObject.getUser();
            // serial no array sorted ny serial number
            idUserStock = user.getIdStock();
            this.fromParts = fromParts;
            this.takeFrom = takeFrom;
            listSelectedSerial = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolderSerial {
            TextView textView;
            android.widget.TextView txtSelectIcon;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            ViewHolderSerial holder = null;

            if (convertView == null) {

                holder = new ViewHolderSerial();
                convertView = inflater.inflate(
                        R.layout.alert_dialog_listview_search_subview, null);
                holder.textView = (TextView) convertView
                        .findViewById(R.id.alertTextView);
                holder.txtSelectIcon = (android.widget.TextView) convertView
                        .findViewById(R.id.txtItemSelect);

                holder.txtSelectIcon.setTypeface(typeFace);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolderSerial) convertView.getTag();
            }

            InventoryDialogSerialNumber data = arrayList.get(position);

//            holder.textView.setText(data.getName());

            if (data.getStockName() != null && data.getStockName().trim().length() > 0)
                if (data.getStockId().equals(idUserStock))
                    holder.textView.setText(data.getName());
                else
                    holder.textView.setText(data.getName() + " (" + data.getStockName() + ")");
            else
                holder.textView.setText(data.getName());

            if (data.isSelected()) {
                holder.txtSelectIcon.setVisibility(View.VISIBLE);
            } else {
                holder.txtSelectIcon.setVisibility(View.GONE);
            }

            holder.textView.setTag(position);
            holder.textView.setOnClickListener(new OnClickListener() {
                @SuppressWarnings("deprecation")
                public void onClick(View v) {
                    int pos = (int) v.getTag();
                    InventoryDialogSerialNumber data = arrayList.get(pos);

                    data.setSelected(!data.isSelected());

                    if (data.isSelected()) {
                        listSelected.add(data);

                        if (fromParts && takeFrom)
                            if (!data.getStockId().equals(idUserStock)) {
                                listSelectedSerial.add(data);
                                AddInventoryPiece addPiece = new AddInventoryPiece(listSelectedSerial, idPiece);
                                addPiece.execute();
                            }

                        calendar = Calendar.getInstance();
                        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                        dateUsed = sdf.format(calendar.getTime());
                        dataAccessObject.updatePieceSerial(idIntervention,
                                dateUsed, data.getIdPieceSerial());
                    } else {
                        int removePos = 0;
                        for (int i = 0; i < listSelected.size(); i++) {

                            if (data.getIdPieceSerial().equalsIgnoreCase(
                                    listSelected.get(i).getIdPieceSerial())) {
                                removePos = i;
                            }
                        }

                        listSelected.remove(removePos);

                        //v54 changes
                        if (fromParts && takeFrom & listSelectedSerial.size() > 0)
                            listSelectedSerial.remove(removePos);

//                        dataAccessObject.updatePieceSerial("", null,
//                                data.getIdPieceSerial());

                        //new changes
                        dataAccessObject.updatePieceSerial(null, null,
                                data.getIdPieceSerial());
                    }

                    String[] tb = dataAccessObject
                            .getNbreSorPieByIdPieAndIdInter(idPiece,
                                    idIntervention);
                    int valDepot = 0;
                    String serialSortie = "";
                    if (tb != null) {

                        valDepot = Integer.parseInt(tb[1]);
                        for (int i = 0; i < listSelected.size(); i++) {
                            if (i == 0) {
                                serialSortie = listSelected.get(i).getName();
                            } else {
                                serialSortie = serialSortie + "," + listSelected.get(i).getName();
                            }
                        }
                        dataAccessObject.majQuantite(idPiece, idIntervention,
                                String.valueOf(listSelected.size()), valDepot, serialSortie);

                    } else {
                        for (int i = 0; i < listSelected.size(); i++) {
                            if (i == 0) {
                                serialSortie = listSelected.get(i).getName();
                            } else {
                                serialSortie = serialSortie + "," + listSelected.get(i).getName();
                            }
                        }
                        dataAccessObject.insertSortiePiece(idIntervention,
                                idPiece, String.valueOf(listSelected.size()),
                                valDepot, serialSortie);
                    }

                    notifyDataSetChanged();

                    // generate the textview after selected
                    generateTextView(listSelected);

                    changesDoneInParts = true;

                }
            });

            holder.txtSelectIcon.setTag(holder);
            holder.txtSelectIcon.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    ViewHolderSerial holder = (ViewHolderSerial) v.getTag();
                    holder.textView.performClick();
                }
            });

            return convertView;
        }

        @SuppressLint("DefaultLocale")
        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint,
                                              FilterResults results) {

                    arrayList = (List<InventoryDialogSerialNumber>) results.values; // has
                    // the
                    // filtered
                    // values
                    notifyDataSetChanged(); // notifies the data with new
                    // filtered values
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults(); // Holds the
                    // results
                    // of a
                    // filtering
                    // operation
                    // in values
                    List<InventoryDialogSerialNumber> FilteredArrList = new ArrayList<InventoryDialogSerialNumber>();

                    if (mOriginalValues == null) {
                        mOriginalValues = new ArrayList<InventoryDialogSerialNumber>(
                                arrayList); // saves the original data in
                        // mOriginalValues
                    }

                    /********
                     *
                     * If constraint(CharSequence that is received) is null
                     * returns the mOriginalValues(Original) values else does
                     * the Filtering and returns FilteredArrList(Filtered)
                     *
                     ********/
                    if (constraint == null || constraint.length() == 0) {

                        // set the Original result to return
                        results.count = mOriginalValues.size();
                        results.values = mOriginalValues;
                    } else {
                        constraint = constraint.toString().toLowerCase();
                        for (int i = 0; i < mOriginalValues.size(); i++) {
                            String data = mOriginalValues.get(i).getName();

                            // remove all the accented characters before search.
                            String asciiSearchString = AccentInsensitive
                                    .removeAccentCase(data);
                            String asciiConstraint = AccentInsensitive
                                    .removeAccentCase(constraint.toString());
                            if (asciiConstraint != null && asciiConstraint.length() > 0)
                                if (asciiSearchString.toLowerCase().contains(
                                        asciiConstraint.toString())) {
                                    FilteredArrList.add(mOriginalValues.get(i));
                                }
                        }
                        // set the Filtered result to return
                        results.count = FilteredArrList.size();
                        results.values = FilteredArrList;
                    }
                    return results;
                }
            };
            return filter;
        }
    }

    /**
     * @param listSel <p>
     *                method to generate textview dynamically and add it to the
     *                relative layout. If the textview have the size to fit into
     *                first row, it will added or else will added to the next row.
     *                </p>
     */
    private static void generateTextView(
            final ArrayList<InventoryDialogSerialNumber> listSel) {

        relSerialConatainer = (RelativeLayout) dialogView
                .findViewById(R.id.relSerialContainer);
        relSerialConatainer.removeAllViews();

        for (int i = 0; i < listSel.size(); i++) {

            TextView tv = new TextView(context);
            Typeface typeFaceAvenir = Typeface.createFromAsset(
                    context.getAssets(),
                    context.getResources().getString(R.string.fontName_avenir));
            tv.setText(listSel.get(i).getName());
            tv.setBackgroundDrawable(context.getResources().getDrawable(
                    R.drawable.boxframe_serial_not_text));

            tv.setId(i + 1);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.getResources()
                    .getDimension(R.dimen.textSizeDaysTv));
            tv.setSingleLine(true);
            tv.setPadding(10, 5, 10, 5);
            tv.setTypeface(typeFaceAvenir);
            relSerialConatainer.setVisibility(View.INVISIBLE);
            relSerialConatainer.addView(tv);
        }

        relSerialConatainer.post(new Runnable() {
            @Override
            public void run() {
                int totalWidth = relSerialConatainer.getWidth();

                // loop through each text view, and set its layout
                // params
                for (int i = 0; i < listSel.size(); i++) {
                    View child = relSerialConatainer.getChildAt(i);
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
                                    relSerialConatainer.getChildAt(i - 1)
                                            .getId());
                            params.addRule(RelativeLayout.ALIGN_BOTTOM,
                                    relSerialConatainer.getChildAt(i - 1)
                                            .getId());
                        }
                    } else {
                        // place it in the next row.
                        totalWidth = relSerialConatainer.getWidth();
                        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                        params.addRule(RelativeLayout.BELOW,
                                relSerialConatainer.getChildAt(i - 1).getId());
                    }
                    child.setLayoutParams(params);
                    totalWidth = totalWidth - child.getWidth() - 10;
                }
                relSerialConatainer.setVisibility(View.VISIBLE);
                relSerialConatainer.requestLayout();

                // scroll down the scroll view after adding the text views.
                scrollContainer.post(new Runnable() {

                    @Override
                    public void run() {
                        scrollContainer.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }
        });

        if (adapter != null)
            adapter.notifyDataSetChanged();

    }

    /**
     * Check qte.
     *
     * @param qte the qte
     * @return true, if successful
     */
    public boolean checkQte(String qte) {
        try {
            if (Float.parseFloat((qte)) >= 10000 || qte.length() > 7)
                return false;
            return true;
        } catch (Exception e) {
            return false;
        }

    }


    private static class AddInventoryPiece extends AsyncTaskCoroutine<String, Boolean> {

        ArrayList<InventoryDialogSerialNumber> serialList;
        String idPiece;

        public AddInventoryPiece(ArrayList<InventoryDialogSerialNumber> serialList, String idPiece) {
            this.serialList = serialList;
            this.idPiece = idPiece;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressLint("SimpleDateFormat")
        @Override
        public Boolean doInBackground(String... params) {

            calendar = Calendar.getInstance();
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            dateUsed = sdf.format(calendar.getTime());
            String currentTime = sdf.format(calendar.getTime());
            User user = dataAccessObject.getUser();
            // serial no array sorted ny serial number
            String idUserStock = user.getIdStock();
            boolean drp = false;
            for (int i = 0; i < serialList.size(); i++) {
                drp = dataAccessObject.addInventoryTransfer(
                        serialList.get(i).getStockId(),
                        idUserStock,
                        serialList.get(i).getIdPiece(), user.getId(),
                        1,
                        1, 0, currentTime);
            }
            return drp;
        }

        @Override
        public void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            DialogUtils.dismissProgressDialog();
            boolean drp = result.booleanValue();
            if (drp) {
                UpdateSerialNumberId updateSerial = new UpdateSerialNumberId(serialList);
                updateSerial.execute();
            } else
                Toast.makeText(context.getApplicationContext(),
                        context.getString(R.string.msg_error),
                        Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Async task to update the serial number's stock id before an item is
     * transfered/returned.
     */
    private static class UpdateSerialNumberId extends AsyncTaskCoroutine<String, Boolean> {

        private final ArrayList<InventoryDialogSerialNumber> serialList;

        public UpdateSerialNumberId(ArrayList<InventoryDialogSerialNumber> serialList) {
            this.serialList = serialList;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
        }

        @SuppressLint("SimpleDateFormat")
        @Override
        public Boolean doInBackground(String... params) {
            boolean isChanged = true;
            List<InventoryDialogSerialNumber> serialNoList;
            String idStock;
            serialNoList = serialList;

            User user = dataAccessObject.getUser();
            // serial no array sorted ny serial number
            String idUserStock = user.getIdStock();
            for (int i = 0; i < serialNoList.size(); i++) {
                boolean drp = dataAccessObject
                        .updateSerialNoId(idUserStock, serialNoList.get(i).getName(),
                                serialNoList.get(i).getIdPiece(),serialNoList.get(i).getIdPieceSerial());
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

                Toast.makeText(context.getApplicationContext(),
                        context.getString(R.string.txt_ack_msg),
                        Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(context.getApplicationContext(),
                        context.getString(R.string.msg_error),
                        Toast.LENGTH_SHORT).show();
        }
    }


}
