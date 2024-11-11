package com.synchroteam.fragmenthelper;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.beans.SortiePiece;
import com.synchroteam.dao.Dao;
import com.synchroteam.fragment.CatalougeJobDetailFragment;
import com.synchroteam.listadapters.CatalougeListAdapter;
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

/**
 * The Class CatalogJobDetailFragmentHelper is responsible to inflate and
 * control various actions on catalog screen. created for future use
 */
public class CatalogJobDetailFragmentHelper implements HelperInterface {

    /**
     * The job details.
     */
    private JobDetails jobDetails;

    /**
     * The catalouge list.
     */
    private ListView catalougeList;

    /**
     * Label of price
     */
    private TextView txtPriceLabel;

    /**
     * The footer view.
     */
    private View footerView;

    /**
     * The add item container.
     */
    private LinearLayout addItemContainer;

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
     * The catalouge list adapter.
     */
    private CatalougeListAdapter catalougeListAdapter;

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
    public CatalogJobDetailFragmentHelper(JobDetails jobDetails,
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
        View view = inflater.inflate(R.layout.layout_cataloge_job_detail_new,
                null, false);
        footerView = inflater.inflate(R.layout.layout_footer_catalougelistitem,
                null);

        init();
        initiateView(view);
        affichPiece();

        return view;
    }

    /**
     * Inits the data of catalouge.
     */
    private void init() {
        devise = dataAccessObject.getDeviseCustomer();

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
        catalougeList = (ListView) view.findViewById(R.id.catalougeList);
        txtPriceLabel = (TextView) view.findViewById(R.id.txtPriceLabel);

        catalougeList.addFooterView(footerView);

        addItemContainer = (LinearLayout) footerView
                .findViewById(R.id.addItemContainer);

        customerSignatureDataTv = (ImageView) footerView
                .findViewById(R.id.customerSignatureDataTv);

        Typeface typeFace = Typeface.createFromAsset(
                jobDetails.getAssets(),
                jobDetails.getResources().getString(
                        R.string.fontName_fontAwesome));

        // customerSignatureDataTv.setOnClickListener(onClickListener);
        footerView.findViewById(R.id.containerSignature).setOnClickListener(
                onClickListener);

        addItemContainer.setOnClickListener(onClickListener);

        if (dataAccessObject.checkSignaturefacture(id_interv, "SIGN_FACTURE") == 1) {

            byte[] retour = dataAccessObject.getPhotoById(id_interv,
                    "SIGN_FACTURE");
            if (retour != null) {
//                Bitmap bitmap = BitmapFactory.decodeByteArray(retour, 0,
//                        retour.length);
//                customerSignatureDataTv.setImageBitmap(bitmap);

                //new changes
                Glide.with(jobDetails)
                        .load(retour)
                        .asBitmap()
                        .override(200, 200)
                        .fitCenter()
                        .placeholder(R.drawable.library_iicon)
                        .into(customerSignatureDataTv);

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
//                        Intent intentPartsServicesList = new Intent(jobDetails,
//                                PartsAndServicesList.class);
                        Intent intentPartsServicesList = new Intent(jobDetails,
                                PartsAndServicesListNew.class);
                        intentPartsServicesList.putExtra(KEYS.Catalouge.ID_INTER,
                                id_interv);
                        intentPartsServicesList.putExtra(KEYS.Catalouge.IS_PARTS_AND_SERVICES, true);
                        // jobDetails.startActivity(intentPartsServicesList);
                        catalougeJobDetailFragment.startActivityForResult(
                                intentPartsServicesList,
                                RequestCode.REQUEST_CODE_OPEN_ITEM);

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

            map.put(KEYS.JObDetail.FLFACTURABLE,
                    String.valueOf(p.getFlFacturable()));
            map.put(KEYS.JObDetail.DEVICE, devise);
            double totalPrice = p.getPrix() * p.getQte();

            String price = CommonUtils.roundDoubleValue(totalPrice, 2) + "";

            map.put(KEYS.JObDetail.PRICE_TOTAL_ITEM, price);

            total += p.getPrix() * p.getQte();

            String quantity = String.valueOf(p.getQte());
            if (quantity.equals("0.0") || quantity.equals("0.00")
                    || quantity.equals("0")) {
                // nothing to do
            } else {
                listItem.add(map);
            }
        }

        TextView tv = (TextView) footerView.findViewById(R.id.totalTv);
        tv.setText(jobDetails.getString(R.string.txt_total_label) + " : " + BigDecimal.valueOf(total).setScale(2, RoundingMode.HALF_UP) + " "
                + devise);

		/* New change */
        /*
         * show/hide the total by flag
		 */
        flMobPrice = gestionAcces.getFlMobPrice();
        if (flMobPrice == 1) {
            tv.setVisibility(View.INVISIBLE);
            txtPriceLabel.setVisibility(View.INVISIBLE);
        }


        if (catalougeListAdapter == null) {
            catalougeListAdapter = new CatalougeListAdapter(jobDetails,
                    catalougeJobDetailFragment, listItem, dataAccessObject,
                    gestionAcces, id_interv);

            catalougeList.setAdapter(catalougeListAdapter);

        } else {
            catalougeListAdapter.notifyDataSetChanged();
        }

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

//            Bitmap bitmap = BitmapFactory.decodeByteArray(retour, 0,
//                    retour.length);
//            customerSignatureDataTv.setImageBitmap(bitmap);

            //new changes
            Glide.with(jobDetails)
                    .load(retour)
                    .asBitmap()
                    .override(200, 200)
                    .fitCenter()
                    .placeholder(R.drawable.library_iicon)
                    .into(customerSignatureDataTv);
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

        TextView tv = (TextView) footerView.findViewById(R.id.totalTv);

        tv.setText(jobDetails.getString(R.string.txt_total_label) + " : "
                + bdTotal.setScale(2, RoundingMode.HALF_UP) + " " + devise);

        //TODO new changes
//		if(oldValue != newValue)
        deleteSignIfModified();

    }

    /**
     * Called When item is removed.
     */
    public void onItemRemoved() {
        affichPiece();

        //TODO new changes
        deleteSignIfModified();

    }

    /**
     * Do on job start stop.
     */
    public void doOnJobStartStop() {
        catalougeListAdapter.notifyDataSetChanged();
    }

}
