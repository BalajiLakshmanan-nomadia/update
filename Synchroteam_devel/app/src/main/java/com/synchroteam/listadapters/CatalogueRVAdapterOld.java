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
import android.os.Bundle;
import android.os.SystemClock;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.beans.InventoryDialogSerialNumber;
import com.synchroteam.beans.InventorySerialNumbersBeans;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.DeletePartsDialog;
import com.synchroteam.fragment.CatalougeJobDetailFragment;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.technicalsupport.JobDetails;
import com.synchroteam.utils.AccentInsensitive;
import com.synchroteam.scanner.CodeScannerActivity;
import com.synchroteam.utils.DecimalDigitsInputFilter;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.RequestCode;
import com.synchroteam.utils.ViewDrawnEventCatcher;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class CatalogueRVAdapterOld extends RecyclerView.Adapter<CatalogueRVAdapterOld.ViewHolderCatalogue> {
    /**
     * The activity.
     */
    private static CatalougeJobDetailFragment catalougeJobDetailFragment;

    /**
     * The deadline job beans.
     */
    private List<HashMap<String, String>> attachmentBeans;

    /**
     * The layout inflater.
     */
    private LayoutInflater layoutInflater;

    /**
     * The data access object.
     */
    private static Dao dataAccessObject;

    /**
     * The id intervention.
     */
    private static String idIntervention;

    /**
     * The job details.
     */
    private static JobDetails jobDetails;

    /**
     * Gestion access
     */
    private GestionAcces gestionAcces;

    /**
     * flag to show/hide the price of the elament
     */
    private int flMobPrice;

    /**
     * flag for serializable parts.
     */
    private int flSerializable;

    private ArrayList<InventoryDialogSerialNumber> listArrayFrom;

    int totalWidth;

    private long mLastClickTime = 0;

    private static final String TAG = "CatalogueRVAdapter";

    // ----NEW CHANGES----------------------------------
    static ArrayList<InventoryDialogSerialNumber> listSelected = new ArrayList<>();
    // static ArrayList<InventoryDialogSerialNumber> unSelectedList = new
    // ArrayList<>();
    static Typeface typeFace;
    public static RelativeLayout relSerialConatainer;
    static View dialogView;
    static ScrollView scrollContainer;
    static CatalogueRVAdapterOld.SerialNumberAdapter adapter;
    static Calendar calendar;
    static SimpleDateFormat sdf;
    private static String dateUsed;
    private User user;
    private String idUserStock;
    private LayoutInflater inflater;
    int checkSerialLay = 0;

    public CatalogueRVAdapterOld(JobDetails jobDetails,
                                 CatalougeJobDetailFragment catalougeJobDetailFragment,
                                 ArrayList<HashMap<String, String>> attachmentBeans,
                                 Dao dataAccessObject, GestionAcces gestionAcces,
                                 String idInterventiion) {
        inflater = LayoutInflater.from(jobDetails);
        this.catalougeJobDetailFragment = catalougeJobDetailFragment;
        this.attachmentBeans = attachmentBeans;
        this.dataAccessObject = dataAccessObject;
        this.idIntervention = idInterventiion;
        layoutInflater = (LayoutInflater) catalougeJobDetailFragment
                .getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.jobDetails = jobDetails;
        this.gestionAcces = gestionAcces;
        flMobPrice = gestionAcces.getFlMobPrice();

        user = dataAccessObject.getUser();
        idUserStock = user.getIdStock();
    }

    @Override
    public CatalogueRVAdapterOld.ViewHolderCatalogue onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_catalouge_item_listview_new, parent, false);
        ViewHolderCatalogue holder = new ViewHolderCatalogue(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(final CatalogueRVAdapterOld.ViewHolderCatalogue viewHolderCatalogue, final int position) {
        final HashMap<String, String> hashMap = attachmentBeans.get(position);
        final ViewHolderCatalogue viewHolder = viewHolderCatalogue;

        //new changes to track the added serial parts
        String serialSortie = hashMap.get(KEYS.JObDetail.SERIAL_PART_SORTIE);


        viewHolder.categoryDataTv.setText(hashMap.get(KEYS.JObDetail.NM_CAT)
                + " > " + hashMap.get(KEYS.JObDetail.NM_PIECE));
        // viewHolder.itemNameDataTv.setText(hashMap.get(KEYS.JObDetail.NM_PIECE));
        String dataQantitity = hashMap.get(KEYS.JObDetail.QTE);


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
            viewHolder.dataQantitiyEt.setText(dataQantitity);
            viewHolder.txtQtyBelow.setText(dataQantitity);
        } else {
            viewHolder.dataQantitiyEt.setText(tempArr[0]);
            viewHolder.txtQtyBelow.setText(tempArr[0]);
        }
        viewHolder.costTv.setText(BigDecimal.valueOf(Double.parseDouble(hashMap.get(KEYS.JObDetail.PRICE_TOTAL_ITEM))).setScale(2, RoundingMode.HALF_UP)
                + " " + hashMap.get(KEYS.JObDetail.DEVICE));

        flSerializable = Integer.parseInt(hashMap
                .get(KEYS.JObDetail.FLSERIALIZABLE));

        /* New change */
        /*
         * When the flag is 1, hide the price for all parts & services or else
         * show the price.
         */
        if (flMobPrice == 1) {
            viewHolder.txtPriceLabel.setVisibility(View.GONE);
            viewHolder.costTv.setVisibility(View.GONE);

            viewHolder.dataQantitiyEt.setVisibility(View.INVISIBLE);
            viewHolder.txtQtyBelow.setVisibility(View.VISIBLE);

            // viewHolder.currencyTv.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.txtQtyBelow.setVisibility(View.GONE);
            viewHolder.dataQantitiyEt.setVisibility(View.VISIBLE);
        }

        if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {
            viewHolder.dataQantitiyEt.setEnabled(true);
            viewHolder.txtQtyBelow.setEnabled(true);
            viewHolder.removeImageIcon.setEnabled(true);
        } else {
            viewHolder.dataQantitiyEt.setEnabled(false);
            viewHolder.txtQtyBelow.setEnabled(false);
            viewHolder.removeImageIcon.setEnabled(false);
        }

        viewHolder.dataQantitiyEt.setTag(flSerializable);
        viewHolder.dataQantitiyEt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub

                // TODO Auto-generated method stub
                int serializable = (int) viewHolder.dataQantitiyEt.getTag();

                if (serializable == 0) {

                    AlertDialog.Builder adb = new AlertDialog.Builder(
                            jobDetails);

                    View alertDialogView = layoutInflater.inflate(
                            R.layout.dialogaddqantity, null);
                    adb.setView(alertDialogView);
                    final EditText commentEt = (EditText) alertDialogView
                            .findViewById(R.id.commentaire);
                    String previousCount = ((TextView) v).getText().toString();
                    commentEt.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});
                    commentEt.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before,
                                                  int count) {
                            if (start == 4
                                    && !commentEt.getText().toString().contains(".")) {
                                String data = commentEt.getText().toString();
                                commentEt.setText(data.substring(0, 4));
                                commentEt.setSelection(4);
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
                    commentEt.setText(previousCount);

                    // all the text is selected
                    commentEt.setSelectAllOnFocus(true);
                    // for request to focus on editText and keyboard ups
                    commentEt.requestFocus();
                    commentEt.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager keyboard = (InputMethodManager) catalougeJobDetailFragment
                                    .getActivity().getSystemService(
                                            Context.INPUT_METHOD_SERVICE);
                            keyboard.showSoftInput(commentEt, 0);
                        }
                    }, 20);

                    adb.setNeutralButton(R.string.modifier,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    String cost = viewHolder.costTv.getText()
                                            .toString();
                                    cost = cost.substring(0, cost.indexOf(" "));
                                    double previousValue = Double
                                            .parseDouble(cost);

                                    String pieces = null;
                                    try {

                                        if ((!TextUtils.isEmpty(commentEt
                                                .getText().toString()))
                                                && (commentEt.getText()
                                                .length() > 1)
                                                && (commentEt.getText()
                                                .toString()
                                                .endsWith("."))) {

                                            pieces = commentEt.getText()
                                                    .toString()
                                                    .replace(".", "");
                                        }

                                        pieces = Double.parseDouble(commentEt
                                                .getText().toString()) + "";
                                    } catch (NumberFormatException numberFormatException) {
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
                                        Toast.makeText(jobDetails,
                                                R.string.addQuantity,
                                                Toast.LENGTH_LONG).show();
                                    } else {
                                        String[] tb = dataAccessObject
                                                .getNbreSorPieByIdPieAndIdInter(
                                                        hashMap.get(KEYS.JObDetail.ID_ITEM),
                                                        idIntervention);
                                        // qt.setText(String.valueOf(tb[0]));

                                        int valDepot = Integer.parseInt(tb[1]);

                                        dataAccessObject.majQuantite(hashMap
                                                        .get(KEYS.JObDetail.ID_ITEM),
                                                idIntervention, pieces + "",
                                                valDepot,null);

                                        double itemCostTotal = Double.parseDouble(hashMap
                                                .get(KEYS.JObDetail.PRIX))
                                                * Double.parseDouble(pieces);

                                        BigDecimal bdItemCostTotal = new BigDecimal(Double.parseDouble(hashMap
                                                .get(KEYS.JObDetail.PRIX))).multiply(new BigDecimal(Double.parseDouble(pieces)));

//                                        itemCostTotal = arrondi(itemCostTotal);
                                        itemCostTotal = bdItemCostTotal.doubleValue();

                                        CatalogueRVAdapterOld.this.attachmentBeans.get(position)
                                                .put(KEYS.JObDetail.QTE,
                                                        pieces + "");

                                        CatalogueRVAdapterOld.this
                                                .attachmentBeans.get(position)
                                                .put(KEYS.JObDetail.PRICE_TOTAL_ITEM,
                                                        itemCostTotal + "");
                                        catalougeJobDetailFragment
                                                .onQantityChange(previousValue,
                                                        itemCostTotal);
                                        notifyDataSetChanged();
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
                }
            }
        });

        viewHolder.txtQtyBelow.setTag(flSerializable);
        viewHolder.txtQtyBelow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int serializable = (int) viewHolder.txtQtyBelow.getTag();

                if (serializable == 0) {

                    AlertDialog.Builder adb = new AlertDialog.Builder(
                            jobDetails);

                    View alertDialogView = layoutInflater.inflate(
                            R.layout.dialogaddqantity, null);
                    adb.setView(alertDialogView);
                    final EditText commentEt = (EditText) alertDialogView
                            .findViewById(R.id.commentaire);
                    commentEt.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(5, 2)});
                    commentEt.addTextChangedListener(new TextWatcher() {

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before,
                                                  int count) {
                            if (start == 4
                                    && !commentEt.getText().toString().contains(".")) {
                                String data = commentEt.getText().toString();
                                commentEt.setText(data.substring(0, 4));
                                commentEt.setSelection(4);
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
                    String previousCount = ((TextView) v).getText().toString();
                    commentEt.setText(previousCount);

                    // all the text is selected
                    commentEt.setSelectAllOnFocus(true);
                    // for request to focus on editText and keyboard ups
                    commentEt.requestFocus();
                    commentEt.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InputMethodManager keyboard = (InputMethodManager) catalougeJobDetailFragment
                                    .getActivity().getSystemService(
                                            Context.INPUT_METHOD_SERVICE);
                            keyboard.showSoftInput(commentEt, 0);
                        }
                    }, 20);

                    adb.setNeutralButton(R.string.modifier,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    String cost = viewHolder.costTv.getText()
                                            .toString();
                                    cost = cost.substring(0, cost.indexOf(" "));
                                    double previousValue = Double
                                            .parseDouble(cost);

                                    String pieces = null;
                                    try {

                                        if ((!TextUtils.isEmpty(commentEt
                                                .getText().toString()))
                                                && (commentEt.getText()
                                                .length() > 1)
                                                && (commentEt.getText()
                                                .toString()
                                                .endsWith("."))) {

                                            pieces = commentEt.getText()
                                                    .toString()
                                                    .replace(".", "");
                                        }

                                        pieces = Double.parseDouble(commentEt
                                                .getText().toString()) + "";
                                    } catch (NumberFormatException numberFormatException) {
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
                                        Toast.makeText(jobDetails,
                                                R.string.addQuantity,
                                                Toast.LENGTH_LONG).show();
                                    } else {
                                        String[] tb = dataAccessObject
                                                .getNbreSorPieByIdPieAndIdInter(
                                                        hashMap.get(KEYS.JObDetail.ID_ITEM),
                                                        idIntervention);
                                        // qt.setText(String.valueOf(tb[0]));

                                        int valDepot = Integer.parseInt(tb[1]);

                                        dataAccessObject.majQuantite(hashMap
                                                        .get(KEYS.JObDetail.ID_ITEM),
                                                idIntervention, pieces + "",
                                                valDepot,null);

                                        double itemCostTotal = Double.parseDouble(hashMap
                                                .get(KEYS.JObDetail.PRIX))
                                                * Double.parseDouble(pieces);

                                        BigDecimal bdItemCostTotal = new BigDecimal(Double.parseDouble(hashMap
                                                .get(KEYS.JObDetail.PRIX))).multiply(new BigDecimal(Double.parseDouble(pieces)));

//                                        itemCostTotal = arrondi(itemCostTotal);
                                        itemCostTotal = bdItemCostTotal.doubleValue();

                                        CatalogueRVAdapterOld.this.attachmentBeans.get(position)
                                                .put(KEYS.JObDetail.QTE,
                                                        pieces + "");

                                        CatalogueRVAdapterOld.this
                                                .attachmentBeans.get(position)
                                                .put(KEYS.JObDetail.PRICE_TOTAL_ITEM,
                                                        itemCostTotal + "");
                                        catalougeJobDetailFragment
                                                .onQantityChange(previousValue,
                                                        itemCostTotal);
                                        notifyDataSetChanged();
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
                }
            }
        });

        ArrayList<InventorySerialNumbersBeans> inventorySerialNos = dataAccessObject
                .getPartsSerialNumbers(idUserStock,
                        Integer.parseInt(hashMap.get(KEYS.JObDetail.ID_ITEM)));
        listArrayFrom = new ArrayList<InventoryDialogSerialNumber>();

        ArrayList<String> addedSerialList = null;
        if (serialSortie != null && serialSortie.trim().length() > 0) {
            if (flSerializable != 0) {
                if (serialSortie.trim().contains(",")) {
                    addedSerialList = new ArrayList<String>(Arrays.asList(serialSortie.split(",")));
                } else {
                    addedSerialList = new ArrayList<>();
                    addedSerialList.add(serialSortie);
                }
            }
        }


        for (int i = 0; i < inventorySerialNos.size(); i++) {
            InventoryDialogSerialNumber h = new InventoryDialogSerialNumber();
            boolean isSelected = dataAccessObject.checkSelectedPiece(
                    hashMap.get(KEYS.JObDetail.ID_ITEM), idIntervention,
                    idUserStock, inventorySerialNos.get(i).getIdSerialNumber());

            h.setId(i + 1);
            h.setName(inventorySerialNos.get(i).getSerialNumber());
            h.setIdPieceSerial(inventorySerialNos.get(i).getIdSerialNumber());
            h.setSelected(isSelected);
            listArrayFrom.add(h);

        }

        viewHolder.txtSelectIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ArrayList<InventoryDialogSerialNumber> serialNoVal = (ArrayList<InventoryDialogSerialNumber>) v
                        .getTag();

                // preventing double click.
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }

                mLastClickTime = SystemClock.elapsedRealtime();

                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {
                    listSelected = new ArrayList<InventoryDialogSerialNumber>();
                    // unSelectedList = new
                    // ArrayList<InventoryDialogSerialNumber>();
                    CatalogueRVAdapterOld.SerialNumberDialog dialog = CatalogueRVAdapterOld.SerialNumberDialog.getInstance(hashMap
                            .get(KEYS.JObDetail.ID_ITEM));
                    dialog.show(jobDetails.getSupportFragmentManager(), "");
                }
            }
        });

        Bundle bundleSerial = new Bundle();
        bundleSerial.putInt(KEYS.InventoryListValues.IS_SERIALIZABLE,
                flSerializable);
        bundleSerial.putSerializable(KEYS.Catalouge.SERIAL_NUMBER,
                listArrayFrom);
        viewHolder.relSerialNumbers.setTag(bundleSerial);
        ViewDrawnEventCatcher.runJustBeforeBeingDrawn(
                viewHolder.relSerialNumbers, new Runnable() {

                    @Override
                    public void run() {

                        Bundle b = (Bundle) viewHolder.relSerialNumbers
                                .getTag();
                        int serializable = b
                                .getInt(KEYS.InventoryListValues.IS_SERIALIZABLE);
                        ArrayList<InventoryDialogSerialNumber> serialNoList = (ArrayList<InventoryDialogSerialNumber>) b
                                .getSerializable(KEYS.Catalouge.SERIAL_NUMBER);
                        if (serializable == 0) {
                            viewHolder.relSerializable.setVisibility(View.GONE);
                        } else {

                            generateTextViewForRow(serialNoList,
                                    viewHolder.relSerialNumbers);
                            if (checkSerialLay == 0) {
                                viewHolder.relSerializable.setVisibility(View.GONE);
                            } else {
                                viewHolder.relSerializable
                                        .setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });

        Bundle bundle = new Bundle();
        bundle.putInt(KEYS.InventoryListValues.IS_SERIALIZABLE, flSerializable);
        bundle.putSerializable(KEYS.Catalouge.SERIAL_NUMBER, listArrayFrom);
        viewHolder.removeImageIcon.setTag(bundle);
        viewHolder.removeImageIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                // TODO Auto-generated method stub

                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {
                    DeletePartsDialog deletePartsDialog = new DeletePartsDialog(
                            jobDetails,
                            new DeletePartsDialog.DeletePartsInterface() {

                                @Override
                                public void doOnYesClick() {
                                    // TODO Auto-generated method stub
                                    Bundle b = (Bundle) v.getTag();
                                    int serializable = b
                                            .getInt(KEYS.InventoryListValues.IS_SERIALIZABLE);
                                    ArrayList<InventoryDialogSerialNumber> serialNoList = (ArrayList<InventoryDialogSerialNumber>) b
                                            .getSerializable(KEYS.Catalouge.SERIAL_NUMBER);

                                    dataAccessObject.deleteSaisiePiece(
                                            hashMap.get(KEYS.JObDetail.ID_ITEM),
                                            idIntervention);

                                    if (serializable == 1) {
                                        assert serialNoList != null;
                                        for (int i = 0; i < serialNoList.size(); i++) {
                                            if (serialNoList.get(i)
                                                    .isSelected()) {
                                                String idPieceSerial = serialNoList
                                                        .get(i)
                                                        .getIdPieceSerial();
//                                                dataAccessObject
//                                                        .updatePieceSerial("",
//                                                                null,
//                                                                idPieceSerial);
                                                //new changes
                                                dataAccessObject
                                                        .updatePieceSerial(null,
                                                                null,
                                                                idPieceSerial);
                                            }
                                        }
                                    }

                                    catalougeJobDetailFragment.onItemRemoved();
                                }

                                @Override
                                public void doOnNoClick() {

                                }
                            });

                    deletePartsDialog.show();
                }

            }
        });


    }

    @Override
    public int getItemCount() {
        return attachmentBeans.size();
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

    /**
     * @param listSel <p>
     *                method to generate textview dynamically and add it to the
     *                relative layout. If the textview have the size to fit into
     *                first row, it will added or else will added to the next row.
     *                </p>
     */
    private void generateTextViewForRow(
            final ArrayList<InventoryDialogSerialNumber> listSel,
            final RelativeLayout relSerialConatainer) {

        checkSerialLay = 0;
        relSerialConatainer.removeAllViews();

        for (int i = 0; i < listSel.size(); i++) {

            TextView tv = new TextView(jobDetails);
            Typeface typeFaceAvenir = Typeface.createFromAsset(
                    jobDetails.getAssets(),
                    jobDetails.getResources().getString(
                            R.string.fontName_avenir));
            if (listSel.get(i).isSelected()) {
                tv.setText(listSel.get(i).getName());
                tv.setBackgroundDrawable(jobDetails.getResources().getDrawable(
                        R.drawable.boxframe_serial_not_text));

                tv.setId(i + 1);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, jobDetails
                        .getResources().getDimension(R.dimen.textSizeDaysTv));
                tv.setSingleLine(true);
                tv.setPadding(10, 5, 10, 5);
                tv.setTypeface(typeFaceAvenir);
                checkSerialLay = 1;
            }
            relSerialConatainer.setVisibility(View.INVISIBLE);
            relSerialConatainer.addView(tv);
        }

        relSerialConatainer.post(new Runnable() {
            @Override
            public void run() {
                int totalWidth = relSerialConatainer.getWidth();
                int rightPadding = relSerialConatainer.getPaddingRight();

                // loop through each text view, and set its layout
                // params
                for (int i = 0; i < listSel.size(); i++) {
                    if (listSel.get(i).isSelected()) {
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
                                    relSerialConatainer.getChildAt(i - 1)
                                            .getId());
                        }
                        child.setLayoutParams(params);
                        totalWidth = totalWidth - child.getWidth() - 10;
                    }
                }
                relSerialConatainer.setVisibility(View.VISIBLE);
                relSerialConatainer.requestLayout();

            }
        });


    }

    /**
     * View holder class for sorting options row.
     */
    class ViewHolderCatalogue extends RecyclerView.ViewHolder {
        /**
         * The left strip iv.
         */
        TextView removeImageIcon;

        /**
         * The job name tv.
         */
        TextView categoryDataTv;

        /**
         * Price label
         */
        TextView txtPriceLabel;

        /** The date or time tv. */
        // TextView itemNameDataTv;

        /** The year tv. */
        // TextView dataQantitiyTv;

        /**
         * The job priority tv.
         */
        TextView dataQantitiyEt;

        /**
         * Qty.
         */
        TextView txtQtyBelow;

        /**
         * The client name tv.
         */
        TextView costTv;

        /** The currency tv. */
        // TextView currencyTv;

        /**
         * Serial number container
         */
        RelativeLayout relSerialNumbers;

        /**
         * Select serial number icon
         */
        android.widget.TextView txtSelectIcon;

        RelativeLayout relSerializable;

        /**
         * The job time status container.
         */
        Typeface mTypeface;

        public ViewHolderCatalogue(View itemView) {
            super(itemView);

            categoryDataTv = (TextView) itemView
                    .findViewById(R.id.categoryDataTv);
            txtPriceLabel = (TextView) itemView
                    .findViewById(R.id.txtPriceLabel);
            removeImageIcon = (TextView) itemView
                    .findViewById(R.id.removeImageIcon);
            // itemNameDataTv = (TextView) itemView
            // .findViewById(R.id.itemNameDataTv);

            dataQantitiyEt = (TextView) itemView
                    .findViewById(R.id.dataQantitiyEt);
            txtQtyBelow = (TextView) itemView
                    .findViewById(R.id.txt_qty_left);
            costTv = (TextView) itemView
                    .findViewById(R.id.costTv);
            // currencyTv = (TextView) itemView
            // .findViewById(R.id.currencyTv);

            txtSelectIcon = (android.widget.TextView) itemView
                    .findViewById(R.id.txtSelectSerialNo);

            relSerialNumbers = (RelativeLayout) itemView
                    .findViewById(R.id.relSerialContainer);

            relSerializable = (RelativeLayout) itemView
                    .findViewById(R.id.relSerialNo);

            Typeface typeFace = Typeface.createFromAsset(
                    jobDetails.getAssets(),
                    jobDetails.getString(R.string.fontName_fontAwesome));
            txtSelectIcon.setTypeface(typeFace);


            itemView.setOnClickListener(mClickListener);
        }

        /**
         * Click listener for item view.
         */
        View.OnClickListener mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        };

    }


    // -------------------------------------------NEW...CHANGE-----------------------------------------------------

    /*
     * Serial number dialog
     */
    @SuppressLint("ValidFragment")
    public static class SerialNumberDialog extends DialogFragment {
        private static final String KEY_ID_PIECE = "id_piece";
        EditText edtSearch;
        //        String idPiece;
        ArrayList<InventoryDialogSerialNumber> items;
//        int oldCount;

        public static CatalogueRVAdapterOld.SerialNumberDialog getInstance(String idPiece) {
            Bundle bundle = new Bundle();
            bundle.putString(KEY_ID_PIECE, idPiece);
            CatalogueRVAdapterOld.SerialNumberDialog dialog = new CatalogueRVAdapterOld.SerialNumberDialog();
            dialog.setArguments(bundle);
            return dialog;
        }

//        public SerialNumberDialog(String idPiece) {
//            this.idPiece = idPiece;
//        }

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
            typeFace = Typeface.createFromAsset(getActivity().getAssets(),
                    getActivity().getString(R.string.fontName_fontAwesome));
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

            User user = dataAccessObject.getUser();
            String idUserStock = user.getIdStock();
            String idPiece = getArguments().getString(KEY_ID_PIECE);

            ArrayList<InventorySerialNumbersBeans> inventorySerialNos = dataAccessObject
                    .getDepotSerialNumbers(idUserStock,
                            Integer.parseInt(idPiece));

            items = new ArrayList<InventoryDialogSerialNumber>();
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
                if (TextUtils.isEmpty(inventorySerialNos.get(i).getIdInterv())
                        || inventorySerialNos.get(i).getIdInterv()
                        .equalsIgnoreCase(idIntervention)) {
                    items.add(h);

                    if (isSelected) {
                        listSelected.add(h);
                    }
//                    oldCount = listSelected.size();
                    // else
                    // unSelectedList.add(h);
                }
            }

            // for (int i = 0; i < listArrayFrom.size(); i++) {
            // if (listArrayFrom.get(i).isSelected()) {
            // listSelected.add(listArrayFrom.get(i));
            // }
            // }

            adapter = new CatalogueRVAdapterOld.SerialNumberAdapter(getActivity(), items, idPiece);
            listView.setAdapter(adapter);

            android.widget.TextView txtSpinnerIcon = (android.widget.TextView) dialogView
                    .findViewById(R.id.txtSearchIcon);
            android.widget.TextView txtBarcodeIcon = (android.widget.TextView) dialogView
                    .findViewById(R.id.txtBarcodeIcon);
            TextView txtClose = (TextView) dialogView
                    .findViewById(R.id.txtClose);
            TextView txtConfirm = (TextView) dialogView
                    .findViewById(R.id.txtConfirm);

            txtSpinnerIcon.setTypeface(typeFace);
            txtBarcodeIcon.setTypeface(typeFace);

            edtSearch = (EditText) dialogView
                    .findViewById(R.id.edtSearchSerialNos);

            edtSearch.addTextChangedListener(watcher);

            txtClose.setOnClickListener(clickListener);
            // txtConfirm.setOnClickListener(clickListener);

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
        View.OnClickListener clickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.txtClose:
//                        CatalogueRVAdapter.this.notifyDataSetChanged();
//                        Logger.output(TAG, "old : " + oldCount + " new : " + listSelected.size());
//                        if (oldCount != listSelected.size()) {
                        catalougeJobDetailFragment.onItemRemoved();
//                        }
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
    /*
     * Adapter for listview in the serial number dialog.
     */
    public static class SerialNumberAdapter extends BaseAdapter implements
            Filterable {

        List<InventoryDialogSerialNumber> arrayList;
        List<InventoryDialogSerialNumber> mOriginalValues; // Original Values
        LayoutInflater inflater;
        String idPiece;

        public SerialNumberAdapter(Context context,
                                   ArrayList<InventoryDialogSerialNumber> arrayList, String idPiece) {
            this.arrayList = arrayList;
            inflater = LayoutInflater.from(context);
            this.idPiece = idPiece;

            // generate the textview for first time
            generateTextView(listSelected);
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

        private class ViewHolderAdapter {
            TextView textView;
            android.widget.TextView txtSelectIcon;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            CatalogueRVAdapterOld.SerialNumberAdapter.ViewHolderAdapter holder = null;

            if (convertView == null) {

                holder = new CatalogueRVAdapterOld.SerialNumberAdapter.ViewHolderAdapter();
                convertView = inflater.inflate(
                        R.layout.alert_dialog_listview_search_subview, null);
                holder.textView = (TextView) convertView
                        .findViewById(R.id.alertTextView);
                holder.txtSelectIcon = (android.widget.TextView) convertView
                        .findViewById(R.id.txtItemSelect);

                holder.txtSelectIcon.setTypeface(typeFace);

                convertView.setTag(holder);
            } else {
                holder = (CatalogueRVAdapterOld.SerialNumberAdapter.ViewHolderAdapter) convertView.getTag();
            }

            InventoryDialogSerialNumber data = arrayList.get(position);

            holder.textView.setText(data.getName());
            if (data.isSelected()) {
                holder.txtSelectIcon.setVisibility(View.VISIBLE);
            } else {
                holder.txtSelectIcon.setVisibility(View.GONE);
            }

            holder.textView.setTag(position);
            holder.textView.setOnClickListener(new View.OnClickListener() {
                @SuppressWarnings("deprecation")
                public void onClick(View v) {
                    int pos = (int) v.getTag();
                    InventoryDialogSerialNumber data = arrayList.get(pos);

                    data.setSelected(!data.isSelected());

                    if (data.isSelected()) {
                        listSelected.add(data);

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

                        if (listSelected.size() > 0) {
                            listSelected.remove(removePos);
                        }

                        dataAccessObject.updatePieceSerial(null, null,
                                data.getIdPieceSerial());
                    }

                    String[] tb = dataAccessObject
                            .getNbreSorPieByIdPieAndIdInter(idPiece,
                                    idIntervention);
                    int valDepot = 0;
                    if (tb != null) {

                        valDepot = Integer.parseInt(tb[1]);

                        dataAccessObject.majQuantite(idPiece, idIntervention,
                                String.valueOf(listSelected.size()), valDepot,null);

                    } else {
                        dataAccessObject.insertSortiePiece(idIntervention,
                                idPiece, String.valueOf(listSelected.size()),
                                valDepot,null);
                    }

                    notifyDataSetChanged();

                    // generate the textview after selected
                    generateTextView(listSelected);

                }
            });

            holder.txtSelectIcon.setTag(holder);
            holder.txtSelectIcon.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    CatalogueRVAdapterOld.SerialNumberAdapter.ViewHolderAdapter holder = (CatalogueRVAdapterOld.SerialNumberAdapter.ViewHolderAdapter) v.getTag();
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

            TextView tv = new TextView(jobDetails);
            Typeface typeFaceAvenir = Typeface.createFromAsset(
                    jobDetails.getAssets(),
                    jobDetails.getResources().getString(
                            R.string.fontName_avenir));
            tv.setText(listSel.get(i).getName());
            tv.setBackgroundDrawable(jobDetails.getResources().getDrawable(
                    R.drawable.boxframe_serial_not_text));

            tv.setId(i + 1);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, jobDetails
                    .getResources().getDimension(R.dimen.textSizeDaysTv));
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

}
