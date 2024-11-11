package com.synchroteam.dialogs;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.synchroteam.beans.InventorySerialNumbersBeans;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.synchroteam.CategoryPartList;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.scanner.CodeScannerActivity;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.CommonUtils;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.RequestCode;
import com.synchroteam.utils.SharedPref;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Trident
 */

public class UpdateTBSerialPartDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG = UpdateTBSerialPartDialog.class.getSimpleName();
    private TextView txtPartFound;
    private TextView txtPartNotFound;
    private TextView txtPartOkay;
    private TextView txtCheckSerial;
    private TextView txtPartStatusOk;
    private TextView txtPartStatusNeedsRepair;
    private TextView txtPartStatusObselete;
    private TextView txtConfirm;
    private TextView txtClose;
    private TextView txtHintPartCate;
    private RelativeLayout relPartStatus;
    private EditText edtSearchSerialNos;
    private LinearLayout linScanCcontainer;

    Context context;
    private Dao dataAccessObject;
    private User user;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
    private Calendar cal;
    private boolean isConfirmEnable;
    private boolean isPartFound;
    private String partRepairStatus;
    private String idUserStock;
    InventorySerialNumbersBeans serialNumbersBeans;
    InventorySerialNumbersBeans serialNumbersBeansOld;
    private Calendar calendar;

    private String idIntervention;
    private String dateUsed;
    private String serialNumberOld;
    private boolean isNew = true;
    private static final String ID_INTERVENTION = "id_interv";
    private static final String ID_PIECE = "id_piece";
    private static final String SERIAL = "serial";
    int clientID = -1;
    boolean fromObsolete = false;
    private int idPiece;
    private TextView txtSearchPart;
    private TextView txtSearchCategory;

    private int idPiecePart = -1;
    private int idCategoryPiece = -1;
    private String cdProduit;
    private String partName;
    private String nameCategory;
    private boolean isNewSerial = false;

    private static UpdateTBSerialPartDialog.TakeBackActionListener takeBackActionListener;

    /**
     * The Interface EnterDialogInterface.
     */
    public interface TakeBackActionListener {

        /**
         * Do on confirm click.
         */
        public void doOnConfirmClick();

        /**
         * Do on cancel click.
         */
        public void doOnCancelClick();

    }

    public static UpdateTBSerialPartDialog newInstance(String id_interv, TakeBackActionListener listener,
                                                       String serialNumber, int idPiece) {

        UpdateTBSerialPartDialog fragment = new UpdateTBSerialPartDialog();
        takeBackActionListener = listener;
        Bundle args = new Bundle();
        args.putString(ID_INTERVENTION, id_interv);
        args.putString(SERIAL, serialNumber);
        args.putInt(ID_PIECE, idPiece);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_take_back_serial_part, container);

        dataAccessObject = DaoManager.getInstance();

        user = dataAccessObject.getUser();
        idUserStock = user.getIdStock();
        isConfirmEnable = false;
//        partRepairStatus = KEYS.RepairStatusParts.KEY_RERAIR_STATUS_OK;


        initializeUI(view);
        initAutoCompleteClients(view);

        if (getArguments().getString(ID_INTERVENTION) != null)
            idIntervention = getArguments().getString(ID_INTERVENTION);
        serialNumberOld = getArguments().getString(SERIAL);
        idPiece = getArguments().getInt(ID_PIECE);

        relPartStatus.setVisibility(View.GONE);
        txtSearchPart.setEnabled(false);
        txtSearchCategory.setEnabled(false);

        if (serialNumberOld != null && serialNumberOld.trim().length() > 0) {
            relPartStatus.setVisibility(View.VISIBLE);
            edtSearchSerialNos.setText(serialNumberOld);
            edtSearchSerialNos.setSelection(serialNumberOld.length());
            isConfirmEnable = true;
            isPartFound = true;
            isNew = false;
            String statusName = dataAccessObject.getStatusForSerial(serialNumberOld, idPiece);
            Logger.log(TAG, "STATUS NAME TB :+" + statusName);
            serialNumbersBeansOld = dataAccessObject.getAllDepotSerialNumbersNew(idUserStock, serialNumberOld);
            serialNumbersBeans = dataAccessObject.getAllDepotSerialNumbersNew(idUserStock, serialNumberOld);
            String[] partCat = dataAccessObject.getPartCatNameForSerial(serialNumbersBeans.getIdPiece());
            if (partCat != null && partCat.length > 0) {
                partName = partCat[0];
                nameCategory = partCat[1];
                txtSearchCategory.setText(nameCategory);
                txtSearchPart.setText(partName);
                txtHintPartCate.setVisibility(View.GONE);
            }

            if (statusName != null && statusName.length() > 0) {

                if (statusName.trim().equalsIgnoreCase(KEYS.RepairStatusParts.KEY_RERAIR_STATUS_OK)) {
                    partRepairStatus = KEYS.RepairStatusParts.KEY_RERAIR_STATUS_OK;
                    updateRepairStatusView();
                } else if (statusName.trim().equalsIgnoreCase(KEYS.RepairStatusParts.KEY_RERAIR_STATUS_NEEDS_REPAIR)) {
                    partRepairStatus = KEYS.RepairStatusParts.KEY_RERAIR_STATUS_NEEDS_REPAIR;
                    updateRepairStatusView();
                } else if (statusName.trim().equalsIgnoreCase(KEYS.RepairStatusParts.KEY_RERAIR_STATUS_OBSELETE)) {
                    partRepairStatus = KEYS.RepairStatusParts.KEY_RERAIR_STATUS_OBSELETE;
                    updateRepairStatusView();
                }
            }
            updatePartStatus();
            enableDisableConfirmButton();
        }


//        dataAccessObject.getAllDepotSNDemoNEw();

        return view;
    }

    /**
     * inits the list of clients.
     */
    public void initAutoCompleteClients(View view) {

        txtSearchPart = (TextView) view.findViewById(R.id.txtSearchPart);
        txtSearchCategory = (TextView) view.findViewById(R.id.txtSearchCategory);


        txtSearchPart.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryPartList.class);
                intent.putExtra(KEYS.Catalouge.IS_PARTS, true);
                intent.putExtra(KEYS.PartCategoryName.KEY_ID_CATEGORY, idCategoryPiece);


                startActivityForResult(intent,
                        RequestCode.REQUEST_CODE_SEARCH_PART);

            }
        });

        txtSearchCategory.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CategoryPartList.class);
                intent.putExtra(KEYS.Catalouge.IS_PARTS, false);
                intent.putExtra(KEYS.PartCategoryName.KEY_ID_CATEGORY, idCategoryPiece);
                startActivityForResult(intent,
                        RequestCode.REQUEST_CODE_CATEGORY_SEARCH);

            }
        });


    }

    private void updatePartStatus() {
        relPartStatus.setVisibility(View.VISIBLE);
        if (isPartFound) {
            txtPartFound.setVisibility(View.VISIBLE);
            txtPartNotFound.setVisibility(View.GONE);
            txtPartOkay.setVisibility(View.GONE);
            txtHintPartCate.setVisibility(View.GONE);
        } else {
            txtPartFound.setVisibility(View.GONE);
            txtPartNotFound.setVisibility(View.VISIBLE);
            txtPartOkay.setVisibility(View.GONE);
            txtHintPartCate.setVisibility(View.VISIBLE);
        }
    }

    /**
     * For enabling and disabling the confirm button
     */
    private void enableDisableConfirmButton() {


        if (isConfirmEnable)
            txtConfirm.setBackgroundDrawable(getActivity().getResources().
                    getDrawable(R.drawable.txt_found_bg));
        else
            txtConfirm.setBackgroundDrawable(getActivity().getResources().
                    getDrawable(R.drawable.boxframe_confirm_button_inactive));


    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();

    }


    private void initializeUI(View view) {

        cal = Calendar.getInstance();

        txtPartFound = (TextView) view.findViewById(R.id.txtPartFound);
        txtPartNotFound = (TextView) view.findViewById(R.id.txtPartNotFound);
        txtPartOkay = (TextView) view.findViewById(R.id.txtPartOkay);
        txtCheckSerial = (TextView) view.findViewById(R.id.txtCheckSerial);
        txtPartStatusOk = (TextView) view.findViewById(R.id.txtPartStatusOk);
        txtPartStatusNeedsRepair = (TextView) view.findViewById(R.id.txtPartStatusNeedsRepair);
        txtPartStatusObselete = (TextView) view.findViewById(R.id.txtPartStatusObselete);
        txtConfirm = (TextView) view.findViewById(R.id.txtConfirm);
        txtClose = (TextView) view.findViewById(R.id.txtClose);
        txtHintPartCate = (TextView) view.findViewById(R.id.txtHintPartCate);
        relPartStatus = (RelativeLayout) view.findViewById(R.id.relPartStatus);
        edtSearchSerialNos = (EditText) view.findViewById(R.id.edtSearchSerialNos);
        linScanCcontainer = (LinearLayout) view.findViewById(R.id.linScanCcontainer);

        linScanCcontainer.setOnClickListener(this);
        txtConfirm.setOnClickListener(this);
        txtClose.setOnClickListener(this);
        txtCheckSerial.setOnClickListener(this);
        txtPartStatusOk.setOnClickListener(this);
        txtPartStatusNeedsRepair.setOnClickListener(this);
        txtPartStatusObselete.setOnClickListener(this);
        edtSearchSerialNos.addTextChangedListener(textWatcher);

        edtSearchSerialNos.setEnabled(true);
        edtSearchSerialNos.setFocusable(true);

        CommonUtils.showKeyboard(getActivity(), edtSearchSerialNos);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(false);
        return dialog;
    }

    private void showToastMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            relPartStatus.setVisibility(View.GONE);
            isConfirmEnable = false;
            isPartFound = false;
            txtSearchPart.setText("");
            txtSearchCategory.setText("");
            idPiecePart = -1;
            cdProduit = "";
            partName = "";
            idCategoryPiece = -1;
            nameCategory = "";
            enableDisableConfirmButton();
            txtSearchPart.setEnabled(false);
            txtSearchCategory.setEnabled(false);
            txtHintPartCate.setVisibility(View.GONE);
        }
    };


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.linScanCcontainer:
                Intent intent = new Intent(getActivity(), CodeScannerActivity.class);
                startActivityForResult(intent,
                        RequestCode.REQUEST_CODE_TEXT_BARCODE);

                break;
            case R.id.txtConfirm:
                if (!TextUtils.isEmpty(edtSearchSerialNos.getText()) &&
                        edtSearchSerialNos.getText().toString().trim().length() > 0) {
                    if (isPartFound && isConfirmEnable) {
                        CommonUtils.hideKeyboard(getActivity(), edtSearchSerialNos);
                        if (isNewSerial) {
                            if (idPiecePart > 0 && idCategoryPiece > 0) {
                                RemoveSerialOldPart();
                                RemoveUpdateTRepricePiece(serialNumbersBeansOld);
                                AddSerialAndUpdate();
                            }
                        } else {
                            new AddTBSerialPartAsyncTask(getActivity(), edtSearchSerialNos.getText().toString()).execute();
                        }
                    }
                }
                break;
            case R.id.txtClose:
                CommonUtils.hideKeyboard(getActivity(), edtSearchSerialNos);
                takeBackActionListener.doOnCancelClick();
                dismiss();

                break;
            case R.id.txtCheckSerial:

                if (!TextUtils.isEmpty(edtSearchSerialNos.getText()) && edtSearchSerialNos.getText().toString().trim().length() > 0) {

                    if (edtSearchSerialNos.getText().toString().trim().equals(serialNumberOld.trim())) {
                        serialNumbersBeans = dataAccessObject.getAllDepotSerialNumbersNew(idUserStock, serialNumberOld);
                        if (serialNumbersBeans != null) {
                            isPartFound = true;
                            isConfirmEnable = true;
                            String[] partCat = dataAccessObject.getPartCatNameForSerial(serialNumbersBeans.getIdPiece());
                            if (partCat != null && partCat.length > 0) {
                                partName = partCat[0];
                                nameCategory = partCat[1];
                                txtSearchCategory.setText(nameCategory);
                                txtSearchPart.setText(partName);
                                txtHintPartCate.setVisibility(View.GONE);
                            }
                        } else {
                            isPartFound = false;
                            isConfirmEnable = false;
                        }
                        enableDisableConfirmButton();
                        updatePartStatus();
                    } else {
                        new CheckSerialPartAsyncTask(getActivity(), edtSearchSerialNos.getText().toString()).execute();
                    }
                }
                break;
            case R.id.txtPartStatusOk:
                partRepairStatus = KEYS.RepairStatusParts.KEY_RERAIR_STATUS_OK;
                updateRepairStatusView();
                break;
            case R.id.txtPartStatusNeedsRepair:
                partRepairStatus = KEYS.RepairStatusParts.KEY_RERAIR_STATUS_NEEDS_REPAIR;
                updateRepairStatusView();
                break;
            case R.id.txtPartStatusObselete:
                partRepairStatus = KEYS.RepairStatusParts.KEY_RERAIR_STATUS_OBSELETE;
                updateRepairStatusView();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == RequestCode.REQUEST_CODE_TEXT_BARCODE) {
            edtSearchSerialNos.setText(data.getStringExtra("SCAN_RESULT_CODE"));
            edtSearchSerialNos.setSelection(edtSearchSerialNos.getText().length());
        } else if (requestCode == RequestCode.REQUEST_CODE_SEARCH_PART
                && (resultCode == RESULT_OK)) {
            Bundle bundle = data.getExtras();
            idPiecePart = bundle.getInt(KEYS.PartCategoryName.KEY_ID_PIECE);
            cdProduit = bundle.getString(KEYS.PartCategoryName.KEY_CD_PRODUIT);
            partName = bundle.getString(KEYS.PartCategoryName.KEY_PART_NAME);
            idCategoryPiece = bundle.getInt(KEYS.PartCategoryName.KEY_ID_CATEGORY);
            nameCategory = bundle.getString(KEYS.PartCategoryName.KEY_CATEGORY_NAME);

            txtSearchCategory.setText(nameCategory);
            txtSearchPart.setText(partName);
            isNewSerial = true;
            checkAddNewSerial();

        } else if (requestCode == RequestCode.REQUEST_CODE_CATEGORY_SEARCH
                && (resultCode == RESULT_OK)) {

            Bundle bundle = data.getExtras();
            int newCateId = bundle.getInt(KEYS.PartCategoryName.KEY_ID_CATEGORY);
            nameCategory = bundle.getString(KEYS.PartCategoryName.KEY_CATEGORY_NAME);
            if (idCategoryPiece <= 0) {
                idCategoryPiece = bundle.getInt(KEYS.PartCategoryName.KEY_ID_CATEGORY);
            } else if (idCategoryPiece == newCateId) {
                idCategoryPiece = bundle.getInt(KEYS.PartCategoryName.KEY_ID_CATEGORY);
            } else {
                txtSearchPart.setText("");
                idPiecePart = -1;
                cdProduit = "";
                partName = "";
                idCategoryPiece = newCateId;
            }
            txtSearchCategory.setText(nameCategory);
            isNewSerial = true;
            checkAddNewSerial();

        }

        txtSearchPart.setEnabled(true);
        txtSearchCategory.setEnabled(true);
    }


    private void checkAddNewSerial() {
        if (idPiecePart == -1 || idCategoryPiece == -1) {
            isConfirmEnable = false;
            txtSearchPart.setEnabled(true);
            txtSearchCategory.setEnabled(true);

            isPartFound = false;
            enableDisableConfirmButton();
            updatePartStatus();


        } else {

            isConfirmEnable = true;
            txtSearchPart.setEnabled(false);
            txtSearchCategory.setEnabled(false);
            enableDisableConfirmButton();
            isPartFound = true;

            txtPartFound.setVisibility(View.GONE);
            txtPartNotFound.setVisibility(View.GONE);
            txtPartOkay.setVisibility(View.VISIBLE);
        }


    }

    private void AddSerialAndUpdate() {
        int idCustomer = dataAccessObject.getIdCustomer();

        String serialId = dataAccessObject.insertNewSerialPart(idUserStock, clientID, null,
                null, edtSearchSerialNos.getText().toString(),
                idPiecePart, partRepairStatus, idCustomer);

        Logger.log(TAG, "NEWSERIAL insert bool valew :" + serialId);

        if (serialId.length() >= 0) {

            savePrevValuesInPref(serialNumbersBeans, true, serialId);

            boolean pieceAvail_T_Sortie = dataAccessObject.checkReprisePieceTakeBackPart(idPiecePart, idIntervention);

            boolean isPieceInserted = false;
            if (!pieceAvail_T_Sortie) {
                isPieceInserted = dataAccessObject.insertReprisePieceTakeBack(idIntervention, "" + idPiecePart
                        , 1, edtSearchSerialNos.getText().toString(), dateUsed);

            } else {
                String[] qtySerial = dataAccessObject
                        .getQtySerialRepPieceByIdPieAndIdInter(String.valueOf(idPiecePart),
                                idIntervention);
                int quantity = 1;
                String serialReprise = null;

                if (qtySerial != null) {

                    quantity = Integer.parseInt(qtySerial[0]);
                    serialReprise = qtySerial[1];

                    int updatedQty = quantity + 1;
                    String updatedSerialReprise = serialReprise + "," + edtSearchSerialNos.getText().toString().trim();

                    Logger.log(TAG, "TBSP updated qty is :" + updatedQty);
                    Logger.log(TAG, "TBSP updated serial reprise is :" + updatedSerialReprise);

                    isPieceInserted = dataAccessObject.updateReprisePieceTakeBack(idIntervention, "" + idPiecePart
                            , updatedQty, updatedSerialReprise);
                }
            }
            Logger.log(TAG, "NEWSERIAL insert isPieceInserted bool valew :" + isPieceInserted);
        }


        takeBackActionListener.doOnConfirmClick();
        dismiss();


    }

    /**
     * Updating the status view based on the selected repair status
     */
    private void updateRepairStatusView() {

        if (partRepairStatus.trim().equals(KEYS.RepairStatusParts.KEY_RERAIR_STATUS_OK)) {
            txtPartStatusOk.setBackgroundDrawable(getActivity().getResources().
                    getDrawable(R.drawable.part_ok_status_sel_bg));

            txtPartStatusNeedsRepair.setBackgroundDrawable(getActivity().getResources().
                    getDrawable(R.drawable.part_needrepair_status_bg));
            txtPartStatusObselete.setBackgroundDrawable(getActivity().getResources().
                    getDrawable(R.drawable.part_obselete_status_bg));
        } else if (partRepairStatus.trim().equals(KEYS.RepairStatusParts.KEY_RERAIR_STATUS_NEEDS_REPAIR)) {
            txtPartStatusNeedsRepair.setBackgroundDrawable(getActivity().getResources().
                    getDrawable(R.drawable.part_needrepair_status_sel_bg));

            txtPartStatusOk.setBackgroundDrawable(getActivity().getResources().
                    getDrawable(R.drawable.part_ok_status_bg));
            txtPartStatusObselete.setBackgroundDrawable(getActivity().getResources().
                    getDrawable(R.drawable.part_obselete_status_bg));
        } else if (partRepairStatus.trim().equals(KEYS.RepairStatusParts.KEY_RERAIR_STATUS_OBSELETE)) {
            txtPartStatusObselete.setBackgroundDrawable(getActivity().getResources().
                    getDrawable(R.drawable.part_obselete_status_sel_bg));

            txtPartStatusOk.setBackgroundDrawable(getActivity().getResources().
                    getDrawable(R.drawable.part_ok_status_bg));
            txtPartStatusNeedsRepair.setBackgroundDrawable(getActivity().getResources().
                    getDrawable(R.drawable.part_needrepair_status_bg));
        }

    }

    /**
     * Async task for checking if the serial number entered is valid or not.
     */
    private class CheckSerialPartAsyncTask extends AsyncTaskCoroutine<Void, Boolean> {

        private Context context;
        private String idPieceSerial;

        public CheckSerialPartAsyncTask(Context context, String idPieceSerial) {
            this.context = context;
            this.idPieceSerial = idPieceSerial;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            isConfirmEnable = false;
            relPartStatus.setVisibility(View.GONE);
            enableDisableConfirmButton();
        }

        @Override
        public Boolean doInBackground(Void... voids) {
            isPartFound = false;

            clientID = dataAccessObject.hasClientID(idIntervention);
            serialNumbersBeans = dataAccessObject.getAllDepotSerialNumbers(idUserStock, idPieceSerial, clientID, idIntervention);

            if (serialNumbersBeans != null) {
                isPartFound = true;
                String[] partCat = dataAccessObject.getPartCatNameForSerial(serialNumbersBeans.getIdPiece());
                if (partCat != null && partCat.length > 0) {
                    partName = partCat[0];
                    nameCategory = partCat[1];
                }
            } else {
                isPartFound = false;
            }

            return isPartFound;
        }

        @Override
        public void onPostExecute(Boolean resultMessage) {
            super.onPostExecute(resultMessage);

            if (isPartFound) {
                isConfirmEnable = true;
                txtSearchPart.setEnabled(false);
                txtSearchCategory.setEnabled(false);
                txtHintPartCate.setVisibility(View.GONE);
            } else {
                isConfirmEnable = false;
                txtSearchPart.setEnabled(true);
                txtSearchCategory.setEnabled(true);
                txtHintPartCate.setVisibility(View.VISIBLE);
            }
            enableDisableConfirmButton();
            updatePartStatus();
            txtSearchCategory.setText(nameCategory);
            txtSearchPart.setText(partName);

        }
    }

    private void savePrevValuesInPref(InventorySerialNumbersBeans serialNumbersBeans, boolean isNewSerial,
                                      String serialno) {
        String idSerialNo = "";
        String jsonString = "";
        if (!isNewSerial) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("idInterv", serialNumbersBeans.getIdInterv());
                jsonObject.put("dateUsed", serialNumbersBeans.getDateUsed());
                if (serialNumbersBeans.getStatusName() != null)
                    jsonObject.put("statusName", serialNumbersBeans.getStatusName());
                else
                    jsonObject.put("statusName", "ok");
                jsonObject.put("serialNumber", serialNumbersBeans.getSerialNumber());
                jsonObject.put("idStock", serialNumbersBeans.getIdStock());
                jsonObject.put("idPiece", "" + serialNumbersBeans.getIdPiece());
                jsonObject.put("isNew", false);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            idSerialNo = serialNumbersBeans.getIdSerialNumber();
            jsonString = jsonObject.toString();
        } else {

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("isNew", true);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            idSerialNo = serialno;
            jsonString = jsonObject.toString();
        }
        Logger.log(TAG, "TAKE_BACK_PART  Key  :" + idSerialNo);
        Logger.log(TAG, "TAKE_BACK_PART  Values  :" + jsonString);

        if (getActivity() != null) {
            SharedPref.setTakeBackPartSharedPref(idSerialNo, jsonString, getActivity());
        }

    }


    /**
     * Async task for checking if the serial number entered is valid or not.
     */
    private class AddTBSerialPartAsyncTask extends AsyncTaskCoroutine<Void, Boolean> {

        private Context context;
        private String serialNumberNew;

        public AddTBSerialPartAsyncTask(Context context, String serialNumberNew) {
            this.context = context;
            this.serialNumberNew = serialNumberNew;
        }

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            isConfirmEnable = false;
            relPartStatus.setVisibility(View.GONE);
            enableDisableConfirmButton();
        }

        @Override
        public Boolean doInBackground(Void... voids) {
            isPartFound = false;


            calendar = Calendar.getInstance();
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            dateUsed = sdf.format(calendar.getTime());

            boolean isPieceInserted = false;


            if (serialNumbersBeansOld.getIdPiece() == serialNumbersBeans.getIdPiece()) {

                if (serialNumberNew.trim().equals(serialNumberOld.trim())) {
                    Logger.log(TAG, "Update new part serial partRepairStatus: " + partRepairStatus);


//                    if (fromObsolete) {
//                        boolean checkStatus = dataAccessObject.insertPieceSerialTakeBackSP(idIntervention,
//                                null, serialNumbersBeans.getIdSerialNumber(), partRepairStatus, idUserStock, clientID, serialNumbersBeans);
//                        fromObsolete = false;
//
//                    } else {
//                        boolean checkStatus = dataAccessObject.updatePieceSerialTakeBackSP(idIntervention,
//                                null, serialNumbersBeans.getIdSerialNumber(), partRepairStatus, idUserStock);
//                    }

                    boolean checkStatus = dataAccessObject.updatePieceSerialTakeBackSP(null,
                            null, serialNumbersBeans.getIdSerialNumber(), partRepairStatus, idUserStock);
                    Logger.log(TAG, "Updated new part serial partRepairStatus check: " + checkStatus);

                } else {
                    RemoveSerialOldPart();

                    savePrevValuesInPref(serialNumbersBeans, false, serialNumbersBeans.getIdSerialNumber());
                    dataAccessObject.updatePieceSerialTakeBackSP(null,
                            null, serialNumbersBeans.getIdSerialNumber(), partRepairStatus, idUserStock);

                    UpdateTRepricePieceSerial(serialNumbersBeans);
                }

            } else {

                RemoveSerialOldPart();
                RemoveUpdateTRepricePiece(serialNumbersBeansOld);

                AddNewSerialInReprisePiece(isPieceInserted);

            }

            return true;
        }

        @Override
        public void onPostExecute(Boolean resultMessage) {
            super.onPostExecute(resultMessage);


            takeBackActionListener.doOnConfirmClick();
            dismiss();
        }
    }

    private void AddNewSerialInReprisePiece(boolean isPieceInserted) {

        boolean pieceAvail_T_Sortie = checkPartInReprisePiece();

        if (!pieceAvail_T_Sortie) {

            isPieceInserted = dataAccessObject.insertReprisePieceTakeBack(idIntervention, String.valueOf(serialNumbersBeans.getIdPiece())
                    , 1, serialNumbersBeans.getSerialNumber(), dateUsed);

        } else {

            String[] qtySerial = dataAccessObject
                    .getQtySerialRepPieceByIdPieAndIdInter(String.valueOf(serialNumbersBeans.getIdPiece()),
                            idIntervention);
            int quantity = 1;
            String serialReprise = null;

            if (qtySerial != null) {

                quantity = Integer.parseInt(qtySerial[0]);
                serialReprise = qtySerial[1];

                int updatedQty = quantity + 1;
                String updatedSerialReprise = serialReprise + "," + serialNumbersBeans.getSerialNumber();

                Logger.log(TAG, "TBSP updated qty is :" + updatedQty);
                Logger.log(TAG, "TBSP updated serial reprise is :" + updatedSerialReprise);

                isPieceInserted = dataAccessObject.updateReprisePieceTakeBack(idIntervention, String.valueOf(serialNumbersBeans.getIdPiece())
                        , updatedQty, updatedSerialReprise);
            }
        }

        if (isPieceInserted) {
            savePrevValuesInPref(serialNumbersBeans, false, serialNumbersBeans.getIdSerialNumber());

            boolean checkStatus = dataAccessObject.updatePieceSerialTakeBackSP(null,
                    null, serialNumbersBeans.getIdSerialNumber(), partRepairStatus, idUserStock);
        }

    }

    private boolean checkPartInReprisePiece() {

        boolean hasIdPiece = dataAccessObject.checkReprisePieceTakeBackPart(serialNumbersBeans.getIdPiece(), idIntervention);

        if (hasIdPiece) {
            Logger.log(TAG, "Check in T_REPRISE_PIECE Part has already been inserted.DO UPDATE");
        } else {
            Logger.log(TAG, "Check in T_REPRISE_PIECE Part has already been inserted.DO INSERT");
        }


        return hasIdPiece;
    }

    private void RemoveSerialOldPart() {

        if (serialNumbersBeansOld != null) {
            String idPieceSerial = serialNumbersBeansOld.getIdSerialNumber();
            calendar = Calendar.getInstance();
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            dateUsed = sdf.format(calendar.getTime());
            Logger.log(TAG, "Remove old part serial : " + serialNumbersBeansOld.getSerialNumber());

            boolean newSerial = isNewSerial(idPieceSerial);
            InventorySerialNumbersBeans beans = null;
            if (!newSerial)
                beans = restorePrevValuesFromPref(idPieceSerial);

            if (newSerial || beans != null) {

//                if (fromObsolete) {
//                    dataAccessObject.insertPieceSerialTakeBackSP(beans.getIdInterv(),
//                            beans.getDateUsed(), beans.getIdSerialNumber(), beans.getStatusName(), beans.getIdStock(), clientID, beans);
//                    fromObsolete = false;
//                } else {
//                    dataAccessObject
//                            .removeReprisePieceSerialTB(beans.getIdInterv(),
//                                    beans.getDateUsed(), beans.getStatusName(),
//                                    beans.getIdStock(), idPieceSerial);
//                }


                if (!newSerial) {
                    dataAccessObject
                            .removeReprisePieceSerialTB(beans.getIdInterv(),
                                    beans.getDateUsed(), beans.getStatusName(),
                                    beans.getIdStock(), idPieceSerial);
                } else {
                    boolean checkStatus = dataAccessObject.updatePieceSerialTakeBackSP(null,
                            null, idPieceSerial, KEYS.RepairStatusParts.KEY_RERAIR_STATUS_OBSELETE,
                            idUserStock);
//                    dataAccessObject.deleteTPieceSerial(String.valueOf(serialNumbersBeansOld.getIdPiece()),
//                            idPieceSerial);
                }

                if (getActivity() != null)
                    SharedPref.removeTakeBackPartSharedPref(getActivity(), idPieceSerial);

            } else {
                dataAccessObject
                        .removeReprisePieceSerialTB(null,
                                dateUsed, "ok", idUserStock,
                                idPieceSerial);
            }

        }

    }

    private boolean isNewSerial(String key) {
        boolean isNew = false;
        if (getActivity() != null) {
            String jsonString = SharedPref.getTakeBackPartSharedPref(getActivity(), key);

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

    private InventorySerialNumbersBeans restorePrevValuesFromPref(String key) {
        InventorySerialNumbersBeans beans = null;
        if (getActivity() != null) {
            String jsonString = SharedPref.getTakeBackPartSharedPref(getActivity(), key);

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


                } catch (JSONException e) {
                    e.printStackTrace();
                    beans = null;
                }
            } else {
                beans = null;
            }
        }

        return beans;
    }

    private void RemoveUpdateTRepricePiece(InventorySerialNumbersBeans inventorySerialNo) {
        //new changes
        String[] qtySerial = dataAccessObject
                .getQtySerialRepPieceByIdPieAndIdInter(String.valueOf(inventorySerialNo.getIdPiece()),
                        idIntervention);
        int quantity = 1;
        String serialReprise = null;

        if (qtySerial != null) {

            quantity = Integer.parseInt(qtySerial[0]);
            serialReprise = qtySerial[1];
            Logger.log(TAG, "Original TRepricePiece Serial no is " + serialReprise);
            Logger.log(TAG, "Original TRepricePiece quantity no is " + quantity);
        }
        if (quantity == 1) {
            Logger.log(TAG, "Delete reprise table ");

            dataAccessObject.deleteReprisePiece(
                    String.valueOf(inventorySerialNo.getIdPiece()),
                    idIntervention);

        } else {
            int updatedQty = quantity - 1;
            String[] serRepriseList = serialReprise.split(",");
            String updatedSerialReprise = "";
            for (int i = 0; i < serRepriseList.length; i++) {
                if (!serRepriseList[i].equals(serialNumberOld)) {
                    if (updatedSerialReprise.trim().length() == 0) {
                        updatedSerialReprise = updatedSerialReprise + "" +
                                serRepriseList[i];
                    } else {
                        updatedSerialReprise = updatedSerialReprise + "," +
                                serRepriseList[i];
                    }
                }
            }

            Logger.log(TAG, "Update TRepricePiece Serial no is " + updatedSerialReprise);
            Logger.log(TAG, "Update TRepricePiece quantity no is " + updatedQty);

            dataAccessObject.updateReprisePieceTakeBack(idIntervention, String.valueOf(inventorySerialNo.getIdPiece())
                    , updatedQty, updatedSerialReprise);

        }
    }

    private void UpdateTRepricePieceSerial(InventorySerialNumbersBeans inventorySerialNo) {
        //new changes
        String[] qtySerial = dataAccessObject
                .getQtySerialRepPieceByIdPieAndIdInter(String.valueOf(inventorySerialNo.getIdPiece()),
                        idIntervention);
        int quantity = 1;
        String serialReprise = null;

        if (qtySerial != null) {

            quantity = Integer.parseInt(qtySerial[0]);
            serialReprise = qtySerial[1];

        }
        String updatedSerialReprise = "";
        int updatedQty = 1;
        if (quantity == 1) {
            updatedSerialReprise = inventorySerialNo.getSerialNumber();
            updatedQty = 1;
        } else {
            updatedQty = quantity;
            String[] serRepriseList = serialReprise.split(",");
            updatedSerialReprise = "";
            for (int i = 0; i < serRepriseList.length; i++) {
                if (!serRepriseList[i].equals(serialNumberOld)) {
                    if (updatedSerialReprise.trim().length() == 0) {
                        updatedSerialReprise = updatedSerialReprise + "" +
                                serRepriseList[i];
                    } else {
                        updatedSerialReprise = updatedSerialReprise + "," +
                                serRepriseList[i];
                    }
                }
            }
            updatedSerialReprise = updatedSerialReprise + "," + inventorySerialNo.getSerialNumber();


        }
        Logger.log(TAG, "Update TRepricePiece Serial no is " + updatedSerialReprise);
        Logger.log(TAG, "Update TRepricePiece quantity no is " + updatedQty);

        dataAccessObject.updateReprisePieceTakeBackSerial(idIntervention, String.valueOf(inventorySerialNo.getIdPiece())
                , updatedSerialReprise);
    }


}