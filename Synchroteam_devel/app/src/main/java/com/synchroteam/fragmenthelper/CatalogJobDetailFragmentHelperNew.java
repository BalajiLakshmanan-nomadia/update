package com.synchroteam.fragmenthelper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.beans.SortiePiece;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.TakeBackSerialPartDialogNew;
import com.synchroteam.fragment.CatalougeJobDetailFragment;
import com.synchroteam.listadapters.CatalogueRVAdapterNew;
import com.synchroteam.listadapters.TakeBackSerializedPartRVAdapter;
import com.synchroteam.synchroteam.PartsAndServicesListNew;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.technicalsupport.JobDetails;
import com.synchroteam.technicalsupport.SignatureFacture;
import com.synchroteam.utils.CommonUtils;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.RequestCode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Vector;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * The Class CatalogJobDetailFragmentHelper is responsible to inflate and
 * control various actions on catalog screen. created for future use
 */
public class CatalogJobDetailFragmentHelperNew implements HelperInterface {

    /**
     * The job details.
     */
    private JobDetails jobDetails;

    /**
     * The catalouge list.
     */
    private RecyclerView catalougeList;

    /**
     * The catalouge list.
     */
    private RecyclerView takeBackSeializedPartList;

    /**
     * Label of price
     */
    private TextView txtPriceLabel;

    /**
     * total price
     */
    private TextView txtTotalPrice;


    /**
     * The add item container.
     */
    private TextView addItemContainer;

    /**
     * The take back serialized part container.
     */
    private TextView takeBackSerializedPart;

    /**
     * The devise.
     */
    private String devise;

    /**
     * The data access object.
     */
    private Dao dataAccessObject;

    /**
     * The id_interv.
     */
    private String id_interv;

    /**
     * The id_user.
     */
    private int id_user;

    /**
     * The catalouge job detail fragment.
     */
    private CatalougeJobDetailFragment catalougeJobDetailFragment;

    /**
     * The list item.
     */
    private ArrayList<HashMap<String, String>> listItem;

    /**
     * The list item.
     */
    private ArrayList<HashMap<String, String>> listItemTakeBackSP;

    /**
     * The catalouge list adapter.
     */
    private CatalogueRVAdapterNew catalougeListAdapter;

    /**
     * The catalouge list adapter.
     */
    private TakeBackSerializedPartRVAdapter takeBackSerializedPartRVAdapter;

    /**
     * The total.
     */
    private double total;

    /**
     * The customer signature data tv.
     */
    private ImageView customerSignatureDataTv;


    /**
     * The decimal format.
     */
    private DecimalFormat decimalFormat;

    /**
     * Gestion access
     */
    private GestionAcces gestionAcces;

    /* Flag to show/hide the price of parts & services */
    private int flMobPrice;

    private static final String TAG = "CatalogJobDetailFragmentHelper";

    /**
     * Instantiates a new catalog job detail fragment helper.
     *
     * @param jobDetails                 the job details
     * @param id_interv                  the id_interv
     * @param cd_statut                  the cd_statut
     * @param id_user                    the id_user
     * @param catalougeJobDetailFragment the catalouge job detail fragment
     */
    public CatalogJobDetailFragmentHelperNew(JobDetails jobDetails,
                                             String id_interv, int cd_statut, int id_user,
                                             CatalougeJobDetailFragment catalougeJobDetailFragment) {

        this.jobDetails = jobDetails;
        this.dataAccessObject = DaoManager.getInstance();
        this.id_interv = id_interv;

        this.id_user = id_user;
        this.catalougeJobDetailFragment = catalougeJobDetailFragment;

        this.decimalFormat = new DecimalFormat("#0.00");
        this.gestionAcces = dataAccessObject.getAcces();

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.synchroteam.fragmenthelper.HelperInterface#inflateLayout(android.
     * view.LayoutInflater, android.view.ViewGroup)
     */
    @Override
    public View inflateLayout(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.layout_cataloge_job_detail_new_updated,
                null, false);

        init();
        initiateView(view);
        affichPiece();
        takeBackSerialPartData();

        return view;
    }

    /**
     * Inits the data of catalouge.
     */
    private void init() {
        devise = dataAccessObject.getDeviseCustomer();
        Logger.log(TAG, "Curreny code is :" + devise);

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.synchroteam.fragmenthelper.HelperInterface#initiateView(android.view
     * .View)
     */
    @Override
    public void initiateView(View view) {
        catalougeList = (RecyclerView) view.findViewById(R.id.catalougeList);
        takeBackSeializedPartList = (RecyclerView) view.findViewById(R.id.takeBackSeializedPartList);
        txtPriceLabel = (TextView) view.findViewById(R.id.txtPriceLabel);
        txtTotalPrice = (TextView) view.findViewById(R.id.totalTv);

        addItemContainer = (TextView) view
                .findViewById(R.id.addItemContainer);

        takeBackSerializedPart = (TextView) view
                .findViewById(R.id.takeBackSerializedPart);

        customerSignatureDataTv = (ImageView) view
                .findViewById(R.id.customerSignatureDataTv);

        Typeface typeFace = Typeface.createFromAsset(
                jobDetails.getAssets(),
                jobDetails.getResources().getString(
                        R.string.fontName_fontAwesome));

        // customerSignatureDataTv.setOnClickListener(onClickListener);
        view.findViewById(R.id.containerSignature).setOnClickListener(
                onClickListener);


        addItemContainer.setOnClickListener(onClickListener);
        takeBackSerializedPart.setOnClickListener(onClickListener);

        if (dataAccessObject.checkSignaturefacture(id_interv, "SIGN_FACTURE") == 1) {

            byte[] retour = dataAccessObject.getPhotoById(id_interv,
                    "SIGN_FACTURE");
            if (retour != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(retour, 0,
                        retour.length);
                customerSignatureDataTv.setImageBitmap(bitmap);
            }
        }

    }

    /**
     * The on click listener.
     */
    OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.addItemContainer:
                    // Boolean drp = dataAccessObject.getNiveauCategorie();
                    if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {

                        // Intent i = new Intent(jobDetails,
                        // CatalougeSubCategory.class);
                        //
                        // i.putExtra(KEYS.Catalouge.ID_INTER, id_interv);
                        // i.putExtra(KEYS.Catalouge.NOM_CAT, "");
                        // i.putExtra(KEYS.Catalouge.INDEX, 0);
                        //
                        // catalougeJobDetailFragment.startActivityForResult(i,
                        // RequestCode.REQUEST_CODE_OPEN_ITEM);

                        //search new implemented
                        Intent intentPartsServicesList = new Intent(jobDetails,
                                PartsAndServicesListNew.class);
//                        Intent intentPartsServicesList = new Intent(jobDetails,
//                                PartsAndServicesList.class);
                        intentPartsServicesList.putExtra(KEYS.Catalouge.ID_INTER,
                                id_interv);
                        intentPartsServicesList.putExtra(KEYS.Catalouge.IS_PARTS_AND_SERVICES, true);
                        // jobDetails.startActivity(intentPartsServicesList);
                        catalougeJobDetailFragment.startActivityForResult(
                                intentPartsServicesList,
                                RequestCode.REQUEST_CODE_OPEN_ITEM);

                    }

                    break;

                case R.id.takeBackSerializedPart:

                    if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {

                        TakeBackSerialPartDialogNew takeBackSerialPartDialogNew = TakeBackSerialPartDialogNew.newInstance(id_interv, new TakeBackSerialPartDialogNew.TakeBackActionListener() {
                            @Override
                            public void doOnConfirmClick() {
                                takeBackSerialPartData();
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
                        }, "");
                        takeBackSerialPartDialogNew.show(catalougeJobDetailFragment.getFragmentManager(), "");
                    }

                    break;

                case R.id.containerSignature:
                    if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {

                        Bundle bundle = new Bundle();
                        bundle.putString("id_interv", id_interv);
                        bundle.putInt("id_user", id_user);
                        bundle.putString("sign", "facture");
                        Intent i = new Intent(jobDetails, SignatureFacture.class);
                        i.putExtras(bundle);
                        catalougeJobDetailFragment.startActivityForResult(i,
                                RequestCode.REQUEST_CODE_SIGNATURE_FACTURE);
                    }

                    break;
            }

        }
    };

    /*
     * (non-Javadoc)
     *
     * @see com.synchroteam.fragmenthelper.HelperInterface#doOnSyncronize()
     */
    @Override
    public void doOnSyncronize() {
        affichPiece();
        takeBackSerialPartData();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.synchroteam.fragmenthelper.HelperInterface#onReturnToActivity(int)
     */
    @Override
    public void onReturnToActivity(int requestCode) {

    }


    public void takeBackSerialPartData() {

        if (listItemTakeBackSP != null) {
            listItemTakeBackSP.clear();
        } else {
            listItemTakeBackSP = new ArrayList<HashMap<String, String>>();
        }

//        dataAccessObject.takeBackSPChecking(id_interv);
//        dataAccessObject.checkTest();

        HashMap<String, String> map;
        Vector<SortiePiece> vectTakeBackSP = dataAccessObject.getReprisePieceTakeBackSP(id_interv);
        User user = dataAccessObject.getUser();

        Enumeration<SortiePiece> enTakeBackSP = vectTakeBackSP.elements();
        while (enTakeBackSP.hasMoreElements()) {
            SortiePiece p = enTakeBackSP.nextElement();


            //new changes
            if (p.getQty_reprise() == 1) {
                map = new HashMap<String, String>();
                map.put(KEYS.JObDetail.QUANTITY_REPRISE, String.valueOf(1));
                map.put(KEYS.JObDetail.SERIAL_REPRISE, p.getSerialReprise());
                map.put(KEYS.JObDetail.ID_ITEM, String.valueOf(p.getId()));
                map.put(KEYS.JObDetail.NM_CAT, p.getNomCat());
                map.put(KEYS.JObDetail.NM_PIECE, p.getNom());
                map.put(KEYS.JObDetail.QTE, String.valueOf(p.getQte()));
                map.put(KEYS.JObDetail.PRIX, String.valueOf(p.getPrix()));
                map.put(KEYS.JObDetail.FLSERIALIZABLE,
                        String.valueOf(p.getFlSerializable()));
                map.put(KEYS.JObDetail.FLTRACKABLE,
                        String.valueOf(p.getFlTrackable()));
                map.put(KEYS.JObDetail.FLFACTURABLE,
                        String.valueOf(p.getFlFacturable()));
                map.put(KEYS.JObDetail.DEVICE, devise);
                double totalPrice = p.getQty_reprise() * p.getPrix();
                String price = CommonUtils.roundDoubleValue(totalPrice, 2) + "";
                map.put(KEYS.JObDetail.PRICE_TOTAL_ITEM, price);
                String statusName = dataAccessObject.getStatusForSerial(p.getSerialReprise(), p.getId());
                //new changes
                if (statusName == null) {
                    statusName = KEYS.RepairStatusParts.KEY_RERAIR_STATUS_OBSELETE;
                }
                Logger.log(TAG, "STATUS NAME TB :" + statusName);
                map.put(KEYS.JObDetail.STATUS, statusName);
                listItemTakeBackSP.add(map);
            } else if (p.getQty_reprise() > 1) {
                String[] serRepList = p.getSerialReprise().split(",");
                if (serRepList != null && serRepList.length > 0)
                    for (int i = 0; i < serRepList.length; i++) {
                        map = new HashMap<String, String>();
                        map.put(KEYS.JObDetail.ID_ITEM, String.valueOf(p.getId()));
                        map.put(KEYS.JObDetail.NM_CAT, p.getNomCat());
                        map.put(KEYS.JObDetail.NM_PIECE, p.getNom());
                        map.put(KEYS.JObDetail.QTE, String.valueOf(p.getQte()));
                        map.put(KEYS.JObDetail.PRIX, String.valueOf(p.getPrix()));
                        map.put(KEYS.JObDetail.FLSERIALIZABLE,
                                String.valueOf(p.getFlSerializable()));
                        map.put(KEYS.JObDetail.FLTRACKABLE,
                                String.valueOf(p.getFlTrackable()));
                        map.put(KEYS.JObDetail.FLFACTURABLE,
                                String.valueOf(p.getFlFacturable()));
                        map.put(KEYS.JObDetail.DEVICE, devise);
                        double totalPrice = p.getQty_reprise() * p.getPrix();
                        String price = CommonUtils.roundDoubleValue(totalPrice, 2) + "";
                        map.put(KEYS.JObDetail.PRICE_TOTAL_ITEM, price);
                        map.put(KEYS.JObDetail.QUANTITY_REPRISE, String.valueOf(1));
                        map.put(KEYS.JObDetail.SERIAL_REPRISE, serRepList[i]);
                        String statusName = dataAccessObject.getStatusForSerial(serRepList[i], p.getId());
                        //new changes
                        if (statusName == null) {
                            statusName = KEYS.RepairStatusParts.KEY_RERAIR_STATUS_OBSELETE;
                        }
                        Logger.log(TAG, "STATUS NAME TB :+" + statusName);
                        map.put(KEYS.JObDetail.STATUS, statusName);
                        listItemTakeBackSP.add(map);
                    }
            }


            //new changes
//            map.put(KEYS.JObDetail.QUANTITY_REPRISE, String.valueOf(p.getQty_reprise()));
//            map.put(KEYS.JObDetail.SERIAL_REPRISE, p.getSerialReprise());

            //todo confirm price calculation
//            total += p.getPrix() * p.getQte();
//
//            //new changes
//            if (p.getQty_reprise() == 1) {
//                map.put(KEYS.JObDetail.QUANTITY_REPRISE, String.valueOf(1));
//                map.put(KEYS.JObDetail.SERIAL_REPRISE, p.getSerialReprise());
//                listItemTakeBackSP.add(map);
//            } else if (p.getQty_reprise() > 1) {
//                String[] serRepList = p.getSerialReprise().split(",");
//                if (serRepList != null && serRepList.length > 0)
//                    for (int i = 0; i < serRepList.length; i++) {
//                        map.put(KEYS.JObDetail.QUANTITY_REPRISE, String.valueOf(1));
//                        map.put(KEYS.JObDetail.SERIAL_REPRISE, serRepList[i]);
//                        listItemTakeBackSP.add(map);
//                    }
//            }

        }

        if (takeBackSerializedPartRVAdapter == null) {
            //todo change later
            takeBackSerializedPartRVAdapter = new TakeBackSerializedPartRVAdapter(jobDetails,
                    catalougeJobDetailFragment, listItemTakeBackSP, dataAccessObject,
                    gestionAcces, id_interv);

            setTakeBackSerializedPartAdapter();
        } else {
            //todo change later
//            takeBackSerializedPartRVAdapter = new TakeBackSerializedPartRVAdapter(jobDetails,
//                    catalougeJobDetailFragment, listItemTakeBackSP, dataAccessObject,
//                    gestionAcces, id_interv);
//
//            setTakeBackSerializedPartAdapter();
            takeBackSerializedPartRVAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Extract all the catalouse items from and initialise the adapter and list
     * view to show that data.
     */
    public void affichPiece() {
        total = 0;

        if (listItem != null) {
            listItem.clear();
        } else {
            listItem = new ArrayList<HashMap<String, String>>();
        }


        HashMap<String, String> map;

        Vector<SortiePiece> vect = dataAccessObject.getSortiepiece(id_interv);

        Enumeration<SortiePiece> en = vect.elements();

        while (en.hasMoreElements()) {
            SortiePiece p = en.nextElement();

            map = new HashMap<String, String>();
            map.put(KEYS.JObDetail.ID_ITEM, String.valueOf(p.getId()));
            map.put(KEYS.JObDetail.NM_CAT, p.getNomCat());
            map.put(KEYS.JObDetail.NM_PIECE, p.getNom());
            map.put(KEYS.JObDetail.QTE, String.valueOf(p.getQte()));
            map.put(KEYS.JObDetail.PRIX, String.valueOf(p.getPrix()));
            map.put(KEYS.JObDetail.FLSERIALIZABLE,
                    String.valueOf(p.getFlSerializable()));

            map.put(KEYS.JObDetail.FLTRACKABLE,
                    String.valueOf(p.getFlTrackable()));
            map.put(KEYS.JObDetail.SERIAL_PART_SORTIE, p.getSerialSortie());
            map.put(KEYS.JObDetail.FLFACTURABLE,
                    String.valueOf(p.getFlFacturable()));
            map.put(KEYS.JObDetail.DEVICE, devise);
            double totalPrice = p.getPrix() * p.getQte();

            String price = CommonUtils.roundDoubleValue(totalPrice, 2) + "";

            map.put(KEYS.JObDetail.PRICE_TOTAL_ITEM, price);

            total += p.getPrix() * p.getQte();

            String quantity = String.valueOf(p.getQte());
            int isSerial = p.getFlSerializable();
            if (quantity.equals("0.0") || quantity.equals("0.00")
                    || quantity.equals("0")) {
                // add only for non serial parts
                try {
                    if (p.getFlSerializable() == 0)
                        listItem.add(map);
                } catch (Exception e) {

                }

            } else {
                listItem.add(map);
            }
        }


        txtTotalPrice.setText(jobDetails.getString(R.string.txt_total_label) + " : " + BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP) + " "
                + devise);

        /* New change */
        /*
         * show/hide the total by flag
         */
        flMobPrice = gestionAcces.getFlMobPrice();
        if (flMobPrice == 1) {
            txtTotalPrice.setVisibility(View.INVISIBLE);
            txtPriceLabel.setVisibility(View.INVISIBLE);
        }


        if (catalougeListAdapter == null) {
            catalougeListAdapter = new CatalogueRVAdapterNew(jobDetails,
                    catalougeJobDetailFragment, listItem, dataAccessObject,
                    gestionAcces, id_interv);

            catalougeList.setLayoutManager(new LinearLayoutManager(jobDetails));
            catalougeList.setAdapter(catalougeListAdapter);
            catalougeList.setHasFixedSize(true);
            catalougeList.setNestedScrollingEnabled(true);


        } else {
            catalougeListAdapter.notifyDataSetChanged();
        }


    }

    private void setTakeBackSerializedPartAdapter() {

        takeBackSeializedPartList.setLayoutManager(new LinearLayoutManager(jobDetails));
        takeBackSeializedPartList.setAdapter(takeBackSerializedPartRVAdapter);
        takeBackSeializedPartList.setHasFixedSize(true);
        takeBackSeializedPartList.setNestedScrollingEnabled(true);
    }

    /**
     * On return to fragment.
     *
     * @param requestCode the request code
     * @param data        the data
     */
    public void onReturnToFragment(int requestCode, Intent data) {

        if (requestCode == RequestCode.REQUEST_CODE_OPEN_ITEM) {
            affichPiece();
            takeBackSerialPartData();
            //TODO new changes
            deleteSignIfModified();

        } else if (requestCode == RequestCode.REQUEST_CODE_SIGNATURE_FACTURE) {
            showCustomerSignature();
        }

    }

    /**
     * add a new item to parts & services table (T_SORTIE_TABLE) by fetching
     * values from bar code scanner class.
     *
     * @param data
     */
    // private void addItemByBarcode(Intent data) {
    // String reference = data.getStringExtra("SCAN_RESULT_CODE");
    // ArrayList<String> arrIdPiece = dataAccessObject
    // .getIdForReference(reference);
    // if (arrIdPiece.size() != 0) {
    // if (arrIdPiece.size() == 1) {
    // String idPiece = arrIdPiece.get(0);
    // String[] arr = dataAccessObject.getNbreSorPieByIdPieAndIdInter(
    // idPiece, id_interv);
    // if (arr == null) {
    // dataAccessObject.insertSortiePiece(id_interv, idPiece,
    // String.valueOf("1"), 0);
    // affichPiece();
    // } else {
    // // do some operation here
    // }
    // } else {
    // // navigate to the list
    // Intent intentPartsServicesList = new Intent(jobDetails,
    // PartsAndServicesList.class);
    // jobDetails.startActivity(intentPartsServicesList);
    // }
    // } else {
    // Toast.makeText(jobDetails,
    // jobDetails.getResources().getString(R.string.txt_no_match),
    // Toast.LENGTH_SHORT).show();
    // }
    // }

    /**
     * Show customer signature.
     */
    private void showCustomerSignature() {
        byte[] retour = dataAccessObject
                .getPhotoById(id_interv, "SIGN_FACTURE");
        if (retour != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(retour, 0,
                    retour.length);
            customerSignatureDataTv.setImageBitmap(bitmap);
        }
    }

    /**
     * Deletes the user signature, if any changes made to reports.
     */
    private void deleteSignIfModified() {
        GestionAcces gt = dataAccessObject.getAcces();
        if (gt.getFlSectionDelSign() == 1) {
            dataAccessObject.deleteSignature(id_interv, "SIGN_FACTURE");

            customerSignatureDataTv.setImageBitmap(null);

        }
    }

    /**
     * Called when quantity is changed.
     *
     * @param oldValue the old value
     * @param newValue the new value
     */
    public void onQantityChange(double oldValue, double newValue) {

        BigDecimal bdTotal = new BigDecimal(total);
        BigDecimal bdOldValue = new BigDecimal(oldValue);
        BigDecimal bdNewValue = new BigDecimal(newValue);

        total = total - oldValue;
        total = total + newValue;

        bdTotal = bdTotal.subtract(bdOldValue);
        bdTotal = bdTotal.add(bdNewValue);

//        bdTotal = BigDecimal.valueOf(arrondi(Double.parseDouble(bdTotal.toPlainString())));

        Logger.output(TAG, "old : " + oldValue + " new : " + newValue);


        txtTotalPrice.setText(jobDetails.getString(R.string.txt_total_label) + " : "
                + bdTotal.setScale(2, RoundingMode.HALF_UP) + " " + devise);

        //TODO new changes
//		if(oldValue != newValue)
//        deleteSignIfModified();

    }

    /**
     * Called When item is removed.
     */
    public void onItemRemoved() {
        affichPiece();


        //TODO new changes
//        deleteSignIfModified();

    }

    /**
     * Called When item is removed.
     */
    public void onItemRemovedTB() {

        takeBackSerialPartData();
        //TODO new changes
        deleteSignIfModified();

    }

    /**
     * Do on job start stop.
     */
    public void doOnJobStartStop() {
        catalougeListAdapter.notifyDataSetChanged();

        //todo change later
        takeBackSerializedPartRVAdapter.notifyDataSetChanged();
    }

}
