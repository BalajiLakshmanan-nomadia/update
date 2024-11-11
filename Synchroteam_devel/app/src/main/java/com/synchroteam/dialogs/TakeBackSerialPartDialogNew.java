package com.synchroteam.dialogs;


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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Trident
 */

public class TakeBackSerialPartDialogNew extends DialogFragment implements View.OnClickListener {
    private static final String TAG = TakeBackSerialPartDialogNew.class.getSimpleName();
    private TextView txtPartFound;
    private TextView txtPartOkay;
    private TextView txtPartNotFound;
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
    private Calendar calendar;

    private String idIntervention;
    private String dateUsed;
    private String serialNumber;

    private static final String ID_INTERVENTION = "id_interv";
    private static final String SERIAL = "serial";
    ArrayList<String> idStocks;
    int clientID = -1;
    private static TakeBackSerialPartDialogNew.TakeBackActionListener takeBackActionListener;
    private TextView txtSearchPart;
    private TextView txtSearchCategory;

    private int idPiecePart = -1;
    private int idCategoryPiece = -1;
    private String cdProduit;
    private String partName;
    private String nameCategory;
    private boolean isNewSerial = false;

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

    public static TakeBackSerialPartDialogNew newInstance(String id_interv, TakeBackSerialPartDialogNew.TakeBackActionListener listener,
                                                          String serialNumber) {

        TakeBackSerialPartDialogNew fragment = new TakeBackSerialPartDialogNew();
        takeBackActionListener = listener;
        Bundle args = new Bundle();
        args.putString(ID_INTERVENTION, id_interv);
        args.putString(SERIAL, serialNumber);
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
        partRepairStatus = KEYS.RepairStatusParts.KEY_RERAIR_STATUS_OK;
        idStocks = new ArrayList<>();

        initializeUI(view);
        initAutoCompleteClients(view);

        idIntervention = getArguments().getString(ID_INTERVENTION);
        serialNumber = getArguments().getString(SERIAL);

        relPartStatus.setVisibility(View.GONE);
        txtSearchPart.setEnabled(false);
        txtSearchCategory.setEnabled(false);
        txtHintPartCate.setVisibility(View.GONE);

//        idStocks = dataAccessObject.getStockNameID();
//        dataAccessObject.getAllDepotSNDemoNEw();

        return view;
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


    /**
     * For enabling and disabling the confirm button
     */
    private void enableDisableConfirmButton() {


        if (isConfirmEnable) {
            txtConfirm.setEnabled(true);
            txtConfirm.setBackgroundDrawable(getActivity().getResources().
                    getDrawable(R.drawable.txt_found_bg));
        } else {
            txtConfirm.setEnabled(false);
            txtConfirm.setBackgroundDrawable(getActivity().getResources().
                    getDrawable(R.drawable.boxframe_confirm_button_inactive));
        }

    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getActivity();

    }


    private void initializeUI(View view) {

        cal = Calendar.getInstance();

        txtPartFound = (TextView) view.findViewById(R.id.txtPartFound);
        txtPartOkay = (TextView) view.findViewById(R.id.txtPartOkay);
        txtPartNotFound = (TextView) view.findViewById(R.id.txtPartNotFound);
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
        txtConfirm.setEnabled(false);

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
                if (!TextUtils.isEmpty(edtSearchSerialNos.getText()) &&
                        edtSearchSerialNos.getText().toString().trim().length() > 0) {
                    new CheckSerialPartAsyncTask(getActivity(), edtSearchSerialNos.getText().toString()).execute();
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


    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RequestCode.REQUEST_CODE_TEXT_BARCODE) {
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

            //new changes check against all depots
//            for (int i = 0; i < idStocks.size(); i++) {
//                serialNumbersBeans = dataAccessObject.getAllDepotSerialNumbers(idStocks.get(i), idPieceSerial);
//                if (serialNumbersBeans != null)
//                    break;
//            }


            clientID = dataAccessObject.hasClientID(idIntervention);
            Logger.log(TAG, "idCLIENT depot is :" + dataAccessObject.getDepotClientId(idPieceSerial));


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

    /**
     * Async task for checking if the serial number entered is valid or not.
     */
    private class AddTBSerialPartAsyncTask extends AsyncTaskCoroutine<Void, Boolean> {

        private Context context;
        private String idPieceSerial;

        public AddTBSerialPartAsyncTask(Context context, String idPieceSerial) {
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


            calendar = Calendar.getInstance();
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            dateUsed = sdf.format(calendar.getTime());

            boolean isPieceInserted = false;

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



//                    if (partRepairStatus.equalsIgnoreCase(KEYS.RepairStatusParts.KEY_RERAIR_STATUS_OK)) {
//                        boolean checkStatus = dataAccessObject.updatePieceSerialTakeBackSP(null,
//                                null, serialNumbersBeans.getIdSerialNumber(), partRepairStatus);
//                    } else {
//                        boolean updateStatus = dataAccessObject.updatePieceSerialTakeBackStatus(serialNumbersBeans.getIdSerialNumber(), partRepairStatus);
//                    }

            }


            return pieceAvail_T_Sortie;
        }

        @Override
        public void onPostExecute(Boolean resultMessage) {
            super.onPostExecute(resultMessage);
//            if (resultMessage) {
//                Toast.makeText(context, R.string.toast_demo, Toast.LENGTH_SHORT).show();
//            }
            takeBackActionListener.doOnConfirmClick();
            dismiss();
        }
    }


    private void savePrevValuesInPref(InventorySerialNumbersBeans serialNumbersBeans, boolean isNewSerial, String serialno) {
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


    private boolean checkPartInReprisePiece() {
        boolean hasIdPiece = dataAccessObject.checkReprisePieceTakeBackPart(serialNumbersBeans.getIdPiece(), idIntervention);

        if (hasIdPiece) {
            Logger.log(TAG, "Check in T_REPRISE_PIECE Part has already been inserted.DO UPDATE");
        } else {
            Logger.log(TAG, "Check in T_REPRISE_PIECE Part has already not been inserted.DO INSERT");
        }


        return hasIdPiece;
    }

}