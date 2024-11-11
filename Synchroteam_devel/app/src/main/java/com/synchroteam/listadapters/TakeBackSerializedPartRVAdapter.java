package com.synchroteam.listadapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.beans.InventoryDialogSerialNumber;
import com.synchroteam.beans.InventorySerialNumbersBeans;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.DeletePartsDialog;
import com.synchroteam.dialogs.UpdateTBSerialPartDialog;
import com.synchroteam.fragment.CatalougeJobDetailFragment;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.technicalsupport.JobDetails;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.SharedPref;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Adapter class for take back serialized parts
 * Created by Trident on 5/20/2016.
 */
public class TakeBackSerializedPartRVAdapter extends RecyclerView.Adapter<TakeBackSerializedPartRVAdapter.ViewHolderCatalogue> {

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

    private static final String TAG = "TakeBackSerializedPartRVAdapter";

    // ----NEW CHANGES----------------------------------
    static ArrayList<InventoryDialogSerialNumber> listSelected = new ArrayList<>();
    // static ArrayList<InventoryDialogSerialNumber> unSelectedList = new
    // ArrayList<>();
    static Typeface typeFace;
    public static RelativeLayout relSerialConatainer;
    static View dialogView;
    static ScrollView scrollContainer;

    static Calendar calendar;
    static SimpleDateFormat sdf;
    private static String dateUsed;
    private User user;
    private String idUserStock;
    private LayoutInflater inflater;

    public TakeBackSerializedPartRVAdapter(JobDetails jobDetails,
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

    @NotNull
    @Override
    public TakeBackSerializedPartRVAdapter.ViewHolderCatalogue onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_take_back_part_item, parent, false);
        ViewHolderCatalogue holder = new ViewHolderCatalogue(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final TakeBackSerializedPartRVAdapter.ViewHolderCatalogue viewHolder, final int position) {
        final HashMap<String, String> hashMap = attachmentBeans.get(position);

        String dataQantitity = hashMap.get(KEYS.JObDetail.QTE);

        String dataQantitityReprise = hashMap.get(KEYS.JObDetail.QUANTITY_REPRISE);
        final String serialReprise = hashMap.get(KEYS.JObDetail.SERIAL_REPRISE);
        String idPiece = hashMap.get(KEYS.JObDetail.ID_ITEM);
        String statusName = hashMap.get(KEYS.JObDetail.STATUS);


        String tempArr[] = null;

        try {
            tempArr = dataQantitity.split("\\.");
        } catch (Exception e) {
            tempArr[0] = dataQantitity;
            tempArr[1] = "0";
            e.printStackTrace();
        }

        viewHolder.categoryDataTv.setText(hashMap.get(KEYS.JObDetail.NM_CAT)
                + " > " + hashMap.get(KEYS.JObDetail.NM_PIECE));
        // viewHolder.itemNameDataTv.setText(hashMap.get(KEYS.JObDetail.NM_PIECE));


//        int roundQantitity = Integer.parseInt(tempArr[1]);
//
//        if (roundQantitity > 0) {
//            viewHolder.dataQantitiyEt.setText(dataQantitity);
//            viewHolder.txtQtyBelow.setText(dataQantitity);
//        } else {
//            viewHolder.dataQantitiyEt.setText(tempArr[0]);
//            viewHolder.txtQtyBelow.setText(tempArr[0]);
//        }


        viewHolder.dataQantitiyEt.setText(dataQantitityReprise);
        viewHolder.txtQtyBelow.setText(dataQantitityReprise);

        viewHolder.txtPriceLabel.setVisibility(View.INVISIBLE);
        viewHolder.txtQtyBelow.setVisibility(View.INVISIBLE);


//        String statusName = dataAccessObject.getStatusForSerial(idUserStock, serialReprise);
        if (statusName != null && statusName.length() > 0) {

            if (statusName.trim().equalsIgnoreCase(KEYS.RepairStatusParts.KEY_RERAIR_STATUS_OK)) {
                viewHolder.txtQtyLabel.setText(R.string.textOkLable);
            } else if (statusName.trim().equalsIgnoreCase(KEYS.RepairStatusParts.KEY_RERAIR_STATUS_NEEDS_REPAIR)) {
                viewHolder.txtQtyLabel.setText(R.string.txt_needs_repair);
            } else if (statusName.trim().equalsIgnoreCase(KEYS.RepairStatusParts.KEY_RERAIR_STATUS_OBSELETE)) {
                viewHolder.txtQtyLabel.setText(R.string.txt_obselete);
            }
            viewHolder.txtQtyLabel.setVisibility(View.VISIBLE);
//            viewHolder.txtPriceLabel.setVisibility(View.VISIBLE);
//            viewHolder.costTv.setVisibility(View.VISIBLE);
        } else {
            viewHolder.txtQtyLabel.setText(R.string.txt_obselete);
            viewHolder.txtQtyLabel.setVisibility(View.VISIBLE);

        }

        //old code
//        viewHolder.costTv.setText(BigDecimal.valueOf(Double.parseDouble(hashMap.get(KEYS.JObDetail.PRICE_TOTAL_ITEM))).setScale(2, RoundingMode.HALF_UP)
//                + " " + hashMap.get(KEYS.JObDetail.DEVICE));

        flSerializable = Integer.parseInt(hashMap
                .get(KEYS.JObDetail.FLSERIALIZABLE));

        viewHolder.txtItemLabel.setText(R.string.txt_label_part_removed);


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
        viewHolder.txtQtyBelow.setTag(flSerializable);
        viewHolder.takeBackSerial.setText(serialReprise);
        viewHolder.relSerializable.setVisibility(View.VISIBLE);
        viewHolder.takeBackSerial.setVisibility(View.VISIBLE);

        viewHolder.dataQantitiyEt.setVisibility(View.INVISIBLE);
        viewHolder.txtQtyBelow.setVisibility(View.GONE);
        viewHolder.costTv.setVisibility(View.GONE);

        InventorySerialNumbersBeans inventorySerialNo = dataAccessObject.getAllDepotSerialNumbersNew(idUserStock, serialReprise);
        listArrayFrom = new ArrayList<InventoryDialogSerialNumber>();
        if (inventorySerialNo != null) {
            InventoryDialogSerialNumber h = new InventoryDialogSerialNumber();
            h.setId(1);
            h.setName(inventorySerialNo.getSerialNumber());
            h.setIdPieceSerial(inventorySerialNo.getIdSerialNumber());
            h.setSelected(true);
            listArrayFrom.add(h);

        }


//        ArrayList<InventorySerialNumbersBeans> inventorySerialNos = dataAccessObject
//                .getPartsSerialNumbersTakeBack(idUserStock,
//                        Integer.parseInt(idPiece), serialReprise);
//        listArrayFrom = new ArrayList<InventoryDialogSerialNumber>();
//        for (int i = 0; i < inventorySerialNos.size(); i++) {
//            InventoryDialogSerialNumber h = new InventoryDialogSerialNumber();
//            h.setId(i + 1);
//            h.setName(inventorySerialNos.get(i).getSerialNumber());
//            h.setIdPieceSerial(inventorySerialNos.get(i).getIdSerialNumber());
//            h.setSelected(true);
//            listArrayFrom.add(h);
//        }


//        Bundle bundleSerial = new Bundle();
//        bundleSerial.putInt(KEYS.InventoryListValues.IS_SERIALIZABLE,
//                flSerializable);
//        bundleSerial.putSerializable(KEYS.Catalouge.SERIAL_NUMBER,
//                listArrayFrom);
//        viewHolder.relSerialNumbers.setTag(bundleSerial);
//        ViewDrawnEventCatcher.runJustBeforeBeingDrawn(
//                viewHolder.relSerialNumbers, new Runnable() {
//
//                    @Override
//                    public void run() {
//
//                        Bundle b = (Bundle) viewHolder.relSerialNumbers
//                                .getTag();
//                        int serializable = b
//                                .getInt(KEYS.InventoryListValues.IS_SERIALIZABLE);
//                        ArrayList<InventoryDialogSerialNumber> serialNoList = (ArrayList<InventoryDialogSerialNumber>) b
//                                .getSerializable(KEYS.Catalouge.SERIAL_NUMBER);
//                        if (serializable == 0) {
//                            viewHolder.relSerializable.setVisibility(View.GONE);
//                        } else {
//                            viewHolder.relSerializable
//                                    .setVisibility(View.VISIBLE);
//                            generateTextViewForRow(serialNoList,
//                                    viewHolder.relSerialNumbers);
//
//                        }
//                    }
//                });


        Bundle bundle = new Bundle();
        bundle.putInt(KEYS.InventoryListValues.IS_SERIALIZABLE, flSerializable);
        bundle.putString(KEYS.InventoryListValues.ID_PIECE, idPiece);
        bundle.putSerializable(KEYS.Catalouge.SERIAL_NUMBER, listArrayFrom);
        viewHolder.removeImageIcon.setTag(bundle);

        viewHolder.txtSelectIcon.setTag(bundle);

        viewHolder.txtSelectIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = (Bundle) v.getTag();
                int serializable = b
                        .getInt(KEYS.InventoryListValues.IS_SERIALIZABLE);
                int idPiece = Integer.parseInt(b.getString(KEYS.InventoryListValues.ID_PIECE));
                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {
                    if (serializable == 1) {
                        UpdateTBSerialPartDialog updateTBSerialPartDialog = UpdateTBSerialPartDialog.newInstance(idIntervention, new UpdateTBSerialPartDialog.TakeBackActionListener() {
                            @Override
                            public void doOnConfirmClick() {
                                catalougeJobDetailFragment.onItemRemovedTB();
                                if (jobDetails != null) {
                                    jobDetails.getWindow().setSoftInputMode(
                                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                                    jobDetails.getWindow().setSoftInputMode(
                                            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                                }
                            }

                            @Override
                            public void doOnCancelClick() {
                                if (jobDetails != null) {
                                    jobDetails.getWindow().setSoftInputMode(
                                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                                    jobDetails.getWindow().setSoftInputMode(
                                            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                                }
                            }
                        }, serialReprise, idPiece);
                        updateTBSerialPartDialog.show(catalougeJobDetailFragment.getFragmentManager(), "");
                    }
                }
            }
        });

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
                                    if (serializable == 1) {
                                        for (int i = 0; i < serialNoList.size(); i++) {
                                            if (serialNoList.get(i)
                                                    .isSelected()) {
                                                String idPieceSerial = serialNoList
                                                        .get(i)
                                                        .getIdPieceSerial();
                                                calendar = Calendar.getInstance();
                                                sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                                                dateUsed = sdf.format(calendar.getTime());

                                                boolean isNewSerial = isNewSerial(idPieceSerial);
                                                InventorySerialNumbersBeans beans = null;
                                                if (!isNewSerial)
                                                    beans = restorePrevValuesFromPref(idPieceSerial);

                                                if (isNewSerial || beans != null) {

                                                    if (!isNewSerial) {
                                                        dataAccessObject
                                                                .removeReprisePieceSerialTB(beans.getIdInterv(),
                                                                        beans.getDateUsed(), beans.getStatusName(),
                                                                        beans.getIdStock(), idPieceSerial);
                                                    } else {
                                                        boolean checkStatus = dataAccessObject.updatePieceSerialTakeBackSP(null,
                                                                null, idPieceSerial, KEYS.RepairStatusParts.KEY_RERAIR_STATUS_OBSELETE,
                                                                idUserStock);
//                                                        dataAccessObject.deleteTPieceSerial(hashMap.get(KEYS.JObDetail.ID_ITEM),
//                                                                idPieceSerial);
                                                    }
                                                    if (jobDetails != null)
                                                        SharedPref.removeTakeBackPartSharedPref(jobDetails, idPieceSerial);

                                                } else {
                                                    dataAccessObject
                                                            .removeReprisePieceSerialTB(idIntervention,
                                                                    dateUsed, "ok", idUserStock,
                                                                    idPieceSerial);
                                                }
                                            }
                                        }
                                    }


                                    //new changes
                                    String[] qtySerial = dataAccessObject
                                            .getQtySerialRepPieceByIdPieAndIdInter(hashMap.get(KEYS.JObDetail.ID_ITEM),
                                                    idIntervention);
                                    int quantity = 1;
                                    String serialReprise = null;

                                    if (qtySerial != null) {

                                        quantity = Integer.parseInt(qtySerial[0]);
                                        serialReprise = qtySerial[1];

                                    }
                                    if (quantity == 1) {
                                        dataAccessObject.deleteReprisePiece(
                                                hashMap.get(KEYS.JObDetail.ID_ITEM),
                                                idIntervention);
                                    } else {
                                        int updatedQty = quantity - 1;
                                        String[] serRepriseList = serialReprise.split(",");
                                        String updatedSerialReprise = "";
                                        for (int i = 0; i < serRepriseList.length; i++) {
                                            if (!serRepriseList[i].equals(serialNoList.get(0).getName())) {
                                                if (updatedSerialReprise.trim().length() == 0) {
                                                    updatedSerialReprise = updatedSerialReprise + "" +
                                                            serRepriseList[i];
                                                } else {
                                                    updatedSerialReprise = updatedSerialReprise + "," +
                                                            serRepriseList[i];
                                                }
                                            }
                                        }

                                        Logger.log(TAG, "TBSP updated qty is :" + updatedQty);
                                        Logger.log(TAG, "TBSP updated serial reprise is :" + updatedSerialReprise);
                                        dataAccessObject.updateReprisePieceTakeBack(idIntervention, hashMap.get(KEYS.JObDetail.ID_ITEM)
                                                , updatedQty, updatedSerialReprise);
                                    }
                                    catalougeJobDetailFragment.onItemRemovedTB();
                                }

                                @Override
                                public void doOnNoClick() {

                                }
                            });

                    deletePartsDialog.show();
                }

            }
        });


        boolean enableDeleteUpdate = dataAccessObject.isEditForSerial(serialReprise, Integer.parseInt(idPiece));
        if (enableDeleteUpdate) {
            viewHolder.removeImageIcon.setVisibility(View.VISIBLE);
            viewHolder.txtSelectIcon.setVisibility(View.VISIBLE);
        } else {
            viewHolder.removeImageIcon.setVisibility(View.GONE);
            viewHolder.txtSelectIcon.setVisibility(View.GONE);
        }

    }

    private InventorySerialNumbersBeans restorePrevValuesFromPref(String key) {
        InventorySerialNumbersBeans beans = null;
        if (jobDetails != null) {
            String jsonString = SharedPref.getTakeBackPartSharedPref(jobDetails, key);

            Logger.log(TAG, "TAKE_BACK_PART  Key  :" + key);
            Logger.log(TAG, "TAKE_BACK_PART  Values  :" + jsonString);

            if (jsonString != null && jsonString.length() > 0) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonString);
                    beans = new InventorySerialNumbersBeans();
                    beans.setIdStock(jsonObj.getString("idStock"));
                    beans.setSerialNumber(jsonObj.getString("serialNumber"));
                    beans.setStatusName(jsonObj.getString("statusName"));
                    beans.setIdInterv(jsonObj.getString("idInterv"));
                    beans.setDateUsed(jsonObj.getString("dateUsed"));
                    beans.setIdPiece(Integer.parseInt(jsonObj.getString("idPiece")));

                    //update values to database

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                beans = null;
            }
        }

        return beans;
    }

    private boolean isNewSerial(String key) {
        boolean isNew = false;
        if (jobDetails != null) {
            String jsonString = SharedPref.getTakeBackPartSharedPref(jobDetails, key);

            Logger.log(TAG, "TAKE_BACK_PART  Key  :" + key);
            Logger.log(TAG, "TAKE_BACK_PART  Values  :" + jsonString);

            if (jsonString != null && jsonString.length() > 0) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonString);

                    isNew = jsonObj.getBoolean("isNew");
                    //update values to database

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return isNew;
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

    @Override
    public int getItemCount() {
        return attachmentBeans.size();
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

        TextView takeBackSerial;

        /**
         * Qty.
         */
        TextView txtQtyBelow;

        TextView txtQtyLabel;

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

        TextView txtItemLabel;

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
            takeBackSerial = (TextView) itemView
                    .findViewById(R.id.takeBackSerial);
            txtQtyBelow = (TextView) itemView
                    .findViewById(R.id.txt_qty_left);
            costTv = (TextView) itemView
                    .findViewById(R.id.costTv);

            txtItemLabel = (TextView) itemView
                    .findViewById(R.id.txtItemLabel);

            // currencyTv = (TextView) itemView
            // .findViewById(R.id.currencyTv);

            txtSelectIcon = (android.widget.TextView) itemView
                    .findViewById(R.id.txtSelectSerialNo);

            relSerialNumbers = (RelativeLayout) itemView
                    .findViewById(R.id.relSerialContainer);

            relSerializable = (RelativeLayout) itemView
                    .findViewById(R.id.relSerialNo);

            txtQtyLabel = (TextView) itemView
                    .findViewById(R.id.txtQtyLabel);

            Typeface typeFace = Typeface.createFromAsset(
                    jobDetails.getAssets(),
                    jobDetails.getString(R.string.fontName_fontAwesome));
            txtSelectIcon.setTypeface(typeFace);

//            txtSelectIcon.setVisibility(View.GONE);

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


    }

}
