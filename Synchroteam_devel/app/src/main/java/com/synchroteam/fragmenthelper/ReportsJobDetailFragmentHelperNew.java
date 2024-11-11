package com.synchroteam.fragmenthelper;

import static com.synchroteam.utils.ImageUtil.getSavedPhoto;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.Families;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.beans.Item;
import com.synchroteam.beans.Photo_Pda;
import com.synchroteam.beans.SharedBlocks;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.AttachmentDialogNew;
import com.synchroteam.dialogs.SharedBlockListDialog;
import com.synchroteam.events.ReportFamilyUpdateEvent;
import com.synchroteam.events.ReportViewUpdateEvent;
import com.synchroteam.events.VerifyReportEvent;
import com.synchroteam.fragment.ReportsJobDetailFragment;
import com.synchroteam.listadapters.ReportsExpandableListAdapterNew;
import com.synchroteam.synchroteam.EditReportImage;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.technicalsupport.JobDetails;
import com.synchroteam.technicalsupport.SignatureFacture;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.CommonUtils;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.MaterialDesignUtils;
import com.synchroteam.utils.RequestCode;
import com.synchroteam.utils.TouchImageView;
import com.synchroteam.utils.imageutils.FileUtilsNew;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import androidx.core.content.ContextCompat;
import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc

/**
 * This class is responsible for inflating and controlling actions of reports
 * screen shown in Job Details.
 * <p/>
 * created for future purpose
 *
 * @author Ideavate.solution
 */
public class ReportsJobDetailFragmentHelperNew implements HelperInterface, View.OnTouchListener {

    /**
     * The job details.
     */
    JobDetails jobDetails;

    /**
     * The deadline excedded list view.
     */
    ListView reportsCategoryListView;

    /**
     * The add attachment.
     */
    ImageView addAttachment;

    /**
     * The id model.
     */
    private int idModel;

    /**
     * The id intervention.
     */
    private String idIntervention;

    /**
     * The cd_statut.
     */
    private int cd_statut;

    /**
     * The id_user.
     */
    private int id_user;

    /**
     * The data access operator.
     */
    private Dao dataAccessOperator;

    /**
     * The expandable list view.
     */
    private ExpandableListView expandableListView;

    /**
     * The reports job detail fragment.
     */
    private ReportsJobDetailFragment reportsJobDetailFragment;

    /**
     * The user.
     */
    private User user;

    /**
     * The attachment list view.
     */
    private LinearLayout attachmentLinearView;

    /**
     * The list commentaire.
     */
    public String[] listCommentaire;

    /**
     * The photo_ pdas.
     */
    private ArrayList<Photo_Pda> photo_Pdas;

    /**
     * The add attachment iv.
     */
    private ImageView addAttachmentIv;

    /**
     * The factory.
     */
    private LayoutInflater inflater;

    /**
     * The mysignature data iv.
     */
    private ImageView arrorBtn, customerSignatureDataTv, mysignatureDataIv;

    /**
     * The bitmap.
     */
    private Bitmap bitmap;

    /**
     * The captured path.
     */
    private String capturedPath;

    /**
     * The extention.
     */
    private String extention;

    /**
     * The sign.
     */
    private String sign;

    /**
     * The reports expandable list adapter.
     */
    private ReportsExpandableListAdapterNew reportsExpandableListAdapter;

    /**
     * The list item.
     */
    private ArrayList<Families> listItem;

    /**
     * The last expanded group position.
     */
    private int lastExpandedGroupPosition = -1;

    /**
     * The footer view.
     */
    private View footerView;

    /**
     * The footer view.
     */
    private View footerViewNew;

    /**
     * The device width.
     */
    private int deviceWidth;
    private int resisingWidth, resisingHeight;

    private TextView textSignatureLabelTv, textMySignatureLabelTv;

    private RelativeLayout relAttachmentContainer;
    private RelativeLayout RelLayCusSign, RelLayTechSign;
    int lastClickedPosition = 0;

    private Item itemChanged;

    private HashMap<Integer, Item> itemFamily;

    /**
     * Add Shared block container.
     */
    FrameLayout flAddSharedBlock;

    /**
     * List of shared block
     */
    private ArrayList<SharedBlocks> listSharedBlock;

    private boolean hadSharedBlock = false;

    private String dtCreated;

    //--------------------PINCH...ZOOM...VARIABLES--------

    // These matrices will be used to move and zoom image
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    // We can be in one of these 3 states
    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    int mode = NONE;

    // Remember some things for zooming
    PointF start = new PointF();
    PointF mid = new PointF();
    float oldDist = 1f;
    String savedItemClicked;
    private int groupIndexSend;
    private int childIndexSend;

    private int index = 1;
    private int reportItemCount;
    ProgressBar progressBar;
    //--------------------PINCH...ZOOM...VARIABLES--------

    private String TAG = "ReportsJobDetailFragmentHelper";

    /**
     * The map.
     *
     * @param jobDetails
     *            the job details
     * @param idModel
     *            the id model
     * @param idIntervention
     *            the id intervention
     * @param cd_statut
     *            the cd_statut
     * @param id_user
     *            the id_user
     * @param idClient
     *            the id client
     * @param idSite
     *            the id site
     * @param idEquipement
     *            the id equipement
     * @param reportsJobDetailFragment
     *            the reports job detail fragment
     */

    /**
     * Instantiates a new reports job detail fragment helper.
     *
     * @param jobDetails               the job details
     * @param idModel                  the id model
     * @param idIntervention           the id intervention
     * @param cd_statut                the cd_statut
     * @param id_user                  the id_user
     * @param reportsJobDetailFragment the reports job detail fragment
     */


    public ReportsJobDetailFragmentHelperNew(JobDetails jobDetails, int idModel,
                                             String idIntervention, int cd_statut, int id_user, String dtCreated,
                                             ReportsJobDetailFragment reportsJobDetailFragment) {

        this.jobDetails = jobDetails;
        this.idIntervention = idIntervention;
        this.cd_statut = cd_statut;
        this.id_user = id_user;
        this.dtCreated = dtCreated;

        this.idModel = idModel;
        dataAccessOperator = DaoManager.getInstance();

        this.reportsJobDetailFragment = reportsJobDetailFragment;
        this.sign = dataAccessOperator.checkSignature(idIntervention);

        DisplayMetrics displaymetrics = new DisplayMetrics();
        jobDetails.getWindowManager().getDefaultDisplay()
                .getMetrics(displaymetrics);

        deviceWidth = displaymetrics.widthPixels;

        resisingWidth = (deviceWidth * 80) / 100;
        resisingHeight = (displaymetrics.heightPixels * 80) / 100;
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
        this.inflater = inflater;
        View view = inflater.inflate(R.layout.layouts_reports_job_detail, null);

        expandableListView = (ExpandableListView) view
                .findViewById(R.id.reportsGeneralListView);

        progressBar = (ProgressBar) view
                .findViewById(R.id.progress);

        expandableListView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

        expandableListView.setOnGroupCollapseListener(onGroupCollapseListener);
        expandableListView.setOnGroupExpandListener(onGroupExpandListener);
        expandableListView.setOnGroupClickListener(onGroupClickListener);
        footerView = inflater.inflate(R.layout.layout_reports_footer_view, null);
        footerViewNew = inflater.inflate(R.layout.layout_footerview_items_list, null, false);


        RelLayCusSign = footerView.findViewById(R.id.rel_lay_cus_sig);
        RelLayTechSign = footerView.findViewById(R.id.rel_lay_tech_sig);
        GestionAcces gs = dataAccessOperator.getAcces();
        if (gs.getFlagSignCust() == 0) {
            RelLayCusSign.setVisibility(View.GONE);
        } else {
            RelLayCusSign.setVisibility(View.VISIBLE);
        }
        if (gs.getFlagSignTech() == 0) {
            RelLayTechSign.setVisibility(View.GONE);
        } else {
            RelLayTechSign.setVisibility(View.VISIBLE);
        }
        footerView.findViewById(R.id.customaerSignatureContainer)
                .setOnClickListener(onClickListener);
        footerView.findViewById(R.id.containerMysignature).setOnClickListener(
                onClickListener);

        android.widget.TextView txtViewReport = (android.widget.TextView) footerView.findViewById(R.id.txt_view_report);
        Typeface typeface = Typeface.createFromAsset(jobDetails.getAssets(), jobDetails.getString(R.string.fontName_fontAwesome));
        txtViewReport.setTypeface(typeface);

        txtViewReport.setOnClickListener(onClickListener);

        textSignatureLabelTv = (TextView) footerView
                .findViewById(R.id.textSignatureLabelTv);
        textMySignatureLabelTv = (TextView) footerView
                .findViewById(R.id.textMySignatureLabelTv);

        // retrieve cmt from T_INTERVENTIONS table
        String cmt = dataAccessOperator.getSignClient(idIntervention);

        if ((cmt != null) && (!TextUtils.isEmpty(cmt))) {
            textSignatureLabelTv.setText(cmt);
            textSignatureLabelTv.setVisibility(View.VISIBLE);
        }

        String cmt1 = dataAccessOperator.getSignUser(idIntervention);

        if ((cmt1 != null) && (!TextUtils.isEmpty(cmt1))) {
            textMySignatureLabelTv.setText(cmt1);
            textMySignatureLabelTv.setVisibility(View.VISIBLE);
        }

        addAttachmentIv = (ImageView) footerView
                .findViewById(R.id.addAttachmentIconIv);

        if (jobDetails.getUpdatedValueOfStatus() != KEYS.JObDetail.JOB__STARTED) {

            addAttachmentIv.setEnabled(false);
            addAttachmentIv.setImageResource(R.drawable.add_icon_disable);

        } else {
            addAttachmentIv.setEnabled(true);
            addAttachmentIv.setImageResource(R.drawable.add_icon);
        }

        addAttachmentIv.setOnClickListener(onClickListener);

        attachmentLinearView = (LinearLayout) footerView
                .findViewById(R.id.attachmentListView);

        customerSignatureDataTv = (ImageView) footerView
                .findViewById(R.id.customerSignatureDataTv);
        mysignatureDataIv = (ImageView) footerView
                .findViewById(R.id.mysignatureDataIv);

        arrorBtn = (ImageView) footerView
                .findViewById(R.id.arrowButtonAttachIv);
        arrorBtn.setTag(Boolean.valueOf(false));

        relAttachmentContainer = (RelativeLayout) footerView
                .findViewById(R.id.relAttachmentContainer);
        relAttachmentContainer.setTag(Boolean.valueOf(false));

        relAttachmentContainer.setOnClickListener(onClickListener);
// arrorBtn.setOnClickListener(onClickListener);

        //Shared block layout
        initializeSharedBlock();

        expandableListView.addFooterView(footerView);

//        affichFamille();
        new AttachReportAsyncTask(this).execute();
        getSharedBlock();
        remplir();
        showAndSaveSignature();

        return view;

    }


    /**
     * The on group collapse listener.
     */
    OnGroupCollapseListener onGroupCollapseListener = new OnGroupCollapseListener() {

        @Override
        public void onGroupCollapse(int groupPosition) {

            try {
                InputMethodManager inputManager = (InputMethodManager) jobDetails
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (jobDetails.getWindow().getCurrentFocus() != null) {
                    inputManager.hideSoftInputFromWindow(jobDetails.getWindow()
                            .getCurrentFocus().getWindowToken(), 0);
                }

            } catch (Exception e) {
                Logger.printException(e);
            }

        }
    };

    /*
     * (non-Javadoc)
     *
     * @see
     * com.synchroteam.fragmenthelper.HelperInterface#initiateView(android.view
     * .View)
     */
    @Override
    public void initiateView(View v) {

    }


    /**
     * The on click listener.
     */
    OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.addAttachmentIconIv) {

                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {

                    if (ContextCompat.checkSelfPermission(jobDetails, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED ||
                            ContextCompat.checkSelfPermission(jobDetails, Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(jobDetails, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        reportsJobDetailFragment.callingPermissionCamera();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                            if (ContextCompat.checkSelfPermission(jobDetails, Manifest.permission.READ_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED ||
                                    ContextCompat.checkSelfPermission(jobDetails, Manifest.permission.CAMERA)
                                            != PackageManager.PERMISSION_GRANTED) {
                                reportsJobDetailFragment.callingPermissionCamera();
                            } else {
                                openCameraAfterPermissionGranted();
                            }

                        } else {
                            reportsJobDetailFragment.callingPermissionCamera();
                        }

                    } else {
                        openCameraAfterPermissionGranted();
                    }

//                    AttachmentDialog attachmentDialog = new AttachmentDialog(
//                            jobDetails, reportsJobDetailFragment,
//                            ReportsJobDetailFragmentHelper.this);
//                    attachmentDialog.show();
                }

            } else if (id == R.id.relAttachmentContainer) {

                Boolean isArrowUp = (Boolean) v.getTag();
                if (isArrowUp.booleanValue()) {
                    attachmentLinearView.setVisibility(View.GONE);
                    v.setTag(Boolean.valueOf(false));
                    arrorBtn.setImageResource(R.drawable.arrow_down);
                } else {

                    attachmentLinearView.setVisibility(View.VISIBLE);
                    if ((lastExpandedGroupPosition != -1) && expandableListView != null) {

                        expandableListView.collapseGroup(lastExpandedGroupPosition);
                    }
//                    expandableListView.collapseGroup(lastExpandedGroupPosition);
                    v.setTag(Boolean.valueOf(true));
                    arrorBtn.setImageResource(R.drawable.arrow_up);
                }
            } else if (id == R.id.customaerSignatureContainer) {
//                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED
//                        || jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__COMPLETE) {
//                    openCustomerSignatureDialog();
//                }

                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {
                    openCustomerSignatureDialog();
                }

            } else if (id == R.id.containerMysignature) {
//                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED
//                        || jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__COMPLETE) {
//                    openMySignatureDialog();
//                }

                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {
                    openMySignatureDialog();
                }

            } else if (id == R.id.lin_add_shared_block) {
                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {
                    getSharedBlock();
                    SharedBlockListDialog dialog = SharedBlockListDialog.getInstance(listSharedBlock);
                    dialog.setTargetFragment(reportsJobDetailFragment, 100);
                    dialog.show(jobDetails.getSupportFragmentManager(), "shared block");
                }
            } else if (id == R.id.txt_view_report) {
                EventBus.getDefault().post(new VerifyReportEvent());
            } else {
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
        // reportsExpandableListAdapter.notifyDataSetChanged();
        arrorBtn.setTag(Boolean.valueOf(false));
        relAttachmentContainer.setTag(Boolean.valueOf(false));
        arrorBtn.setImageResource(R.drawable.arrow_down);
        attachmentLinearView.setVisibility(View.GONE);
//        affichFamille();
        new AttachReportAsyncTask(this).execute();
        getSharedBlock();
        remplir();

    }

    public void doOnSyncronizeNew(int groupPosition) {
        // reportsExpandableListAdapter.notifyDataSetChanged();
        arrorBtn.setTag(Boolean.valueOf(false));
        relAttachmentContainer.setTag(Boolean.valueOf(false));
        arrorBtn.setImageResource(R.drawable.arrow_down);
        attachmentLinearView.setVisibility(View.GONE);
//        affichFamille();
        new AttachReportAsyncTask(this,
                groupPosition, true).execute();
        getSharedBlock();
        remplir();

    }

    /**
     * Open dialog to enter technician signature.
     */
    protected void openMySignatureDialog() {

        Bundle bundle = new Bundle();
        bundle.putString("id_interv", idIntervention);
        bundle.putInt("id_user", id_user);
        bundle.putString("sign", "user");
        Intent i = new Intent(jobDetails, SignatureFacture.class);
        i.putExtras(bundle);
        reportsJobDetailFragment.startActivityForResult(i,
                RequestCode.REQUEST_CODE_SIGNATURE_FACTURE);

    }

    /**
     * Open dialog to enter customer signature.
     */
    protected void openCustomerSignatureDialog() {
        Bundle bundle = new Bundle();
        bundle.putString("id_interv", idIntervention);
        bundle.putInt("id_user", id_user);
        bundle.putString("sign", "client");
        Intent i = new Intent(jobDetails, SignatureFacture.class);
        i.putExtras(bundle);
        reportsJobDetailFragment.startActivityForResult(i,
                RequestCode.REQUEST_CODE_SIGNATURE_FACTURE);
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
     * Fetches all the categories along with reports items in that category from
     * database and make a ArrayList of category and create or update adapter
     * with the updated list.
     */
    public void affichFamille() {
        if (listItem == null) {

            listItem = new ArrayList<Families>();
        } else {
            listItem.clear();
        }

//        Vector<Famille> vect = dataAccessOperator.getAllFamille(idModel,
//                idIntervention);
//        Vector<Famille> vect = dataAccessOperator.getAllFamilyWithSB(idModel, idIntervention);
//        Vector<Families> vect = dataAccessOperator.getAllFamiliesWithSB(idModel, idIntervention);
        Vector<Families> vect = dataAccessOperator.getSBCategory(idIntervention);

        // Collections.sort(vect, new Comparator<Famille>() {
        // public int compare(Famille c1, Famille c2) {
        // return c1.getNomFamille().compareTo(c2.getNomFamille());
        // }
        // });

        Enumeration<Families> en = vect.elements();

        while (en.hasMoreElements()) {
            Families fm = en.nextElement();

            HashMap<Integer, Item> items = dataAccessOperator.getAllItem(
                    idIntervention, fm.getIdFamily(), fm.getIteration());

//            try {
//                dataAccessOperator.getItems(fm.getIdFamily());
//            } catch (ULjException e) {
//                e.printStackTrace();
//            }
            //
            //
            Vector<Item> vectorItem = cleanListItem(items);

            fm.setItems(vectorItem);

//            if (fm.getItems() != null) {
//                Logger.log("ReportsJobDetail", "family : " + fm.getNameFamily());
//                for (int i = 0; i < fm.getItems().size(); i++) {
//                    Logger.log("ReportsJobDetail", vectorItem.size() + ""
//                            + fm.getItems().get(i).getNomItem());
//                }
//            }

            if (fm.getIsSharedBlock() == 1) {
                hadSharedBlock = true;
            }

            listItem.add(fm);
        }

        if (reportsExpandableListAdapter == null) {

            if (listItem != null && listItem.size() > 0) {
                reportsExpandableListAdapter = new ReportsExpandableListAdapterNew(
                        jobDetails, listItem, dataAccessOperator, id_user, idModel,
                        cd_statut, idIntervention, this, reportsJobDetailFragment,
                        deviceWidth);
            }

            expandableListView.setAdapter(reportsExpandableListAdapter);

        } else {
            reportsExpandableListAdapter.notifyDataSetChanged();

        }

    }

    /**
     * Perform some action when this control comes form onActiviyResult.
     *
     * @param requestCode the request code
     * @param data        the data
     */
    public void onReturnToFragment(int requestCode, Intent data) {

        if (requestCode == RequestCode.REQUEST_CODE_ATTACHMENTS_BARCODE) {
            String code = data.getStringExtra("SCAN_RESULT_CODE");
            String capturedPath = data.getStringExtra("CAPTURED_PATH");
            Bitmap bitmap = null;
            byte[] check = getSavedPhoto(capturedPath,resisingWidth,resisingHeight);

            if (check != null && check.length > 0) {
                bitmap = BitmapFactory.decodeByteArray(check, 0, check.length);
                if (bitmap != null && bitmap.getWidth() > bitmap.getHeight()) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                }
            }else{
                Resources res = jobDetails.getResources();
                Drawable drawable = res.getDrawable(R.drawable.barcode);
                bitmap = ((BitmapDrawable) drawable).getBitmap();
            }
            enregistrerScan(bitmap, code, requestCode);

        } else if (requestCode == RequestCode.REQUEST_CODE_ATTACHMENTS_CAMERA) {

            enregistrerPhotoCapturer(requestCode);

        } else if (requestCode == RequestCode.REQUEST_CODE_ATTACHMENTS_LIBRARY) {

//            Uri selectedImage = data.getData();
//            enregistrerPhoto(selectedImage, requestCode);
            handleImageRequest(data, requestCode);
        } else if (requestCode == RequestCode.REQUEST_CODE_SIGNATURE_FACTURE) {
            sign = dataAccessOperator.checkSignature(idIntervention);

            showAndSaveSignature();

        } else if (requestCode == RequestCode.REQUEST_CODE_SIGNATURE_DIALOG_ITEM) {
            refreshItem(data.getExtras().getInt("groupIndex"), data.getExtras().getInt("childIndex"), 0);

        } else if (requestCode == RequestCode.REQUEST_CODE_CHILDVIEW_CAMERA) {
            enregistrerPhotoCapturer(requestCode);
        } else if (requestCode == RequestCode.REQUEST_CODE_CHILDVIEW_LIBRARY) {

//            Uri selectedImage = data.getData();
//
//            enregistrerPhoto(selectedImage, requestCode);

            handleImageRequest(data, requestCode);

        } else if (requestCode == RequestCode.REQUEST_CODE_CHILDVIEW_BARCODE) {
//            String code = data.getStringExtra("SCAN_RESULT_CODE");
//
//            Resources res = jobDetails.getResources();
//            Drawable drawable = res.getDrawable(R.drawable.barcode);
//            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

            Log.e("CHECK","BARCODE CHECK>>>>>>>>>>>");

            //new changes add scanner to photo field
            int def_screenWidth = jobDetails.getResources().getDisplayMetrics().widthPixels;
            int def_screenHeight = jobDetails.getResources().getDisplayMetrics().heightPixels;

            String code = data.getStringExtra("SCAN_RESULT_CODE");
            byte[] check = data.getByteArrayExtra("imagebytearray");
            int screenHeight = data.getIntExtra("sizeheight", def_screenHeight);
            int screenWidth = data.getIntExtra("sizewidth", def_screenWidth);


            Resources res = jobDetails.getResources();
            Drawable drawable = res.getDrawable(R.drawable.barcode);

            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            if (check != null && check.length > 0) {
                bitmap = BitmapFactory.decodeByteArray(check, 0, check.length);
                if (bitmap != null && bitmap.getWidth() > bitmap.getHeight()) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(90);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                }

            }

            enregistrerScan(bitmap, code, requestCode);

//            enregistrerScan(bitmap, code, requestCode);

        }

        /*
         * New changes.... For adding barcode to text
         */
        else if (requestCode == RequestCode.REQUEST_CODE_TEXT_BARCODE) {
            String code = data.getStringExtra("SCAN_RESULT_CODE");
            Log.e("CHECK","BARCODE CHECK>>>>>>>>>>>2");
            int groupIndex = getGroupIndexSend();
            int childIndex = getChildIndexSend();
            Item tempItems = listItem.get(groupIndex).getItems()
                    .get(childIndex);
            dataAccessOperator.updateValue(code, tempItems.getComItem(),
                    tempItems.getIdItem(), idIntervention, 0,
                    tempItems.getFlReserve(), tempItems.getNomItem(), tempItems.getIteration());
            refreshItem(getGroupIndexSend(), getChildIndexSend(), 0);
        }

        /*
         * New changes.... For adding barcode to text
         */
        else if (requestCode == RequestCode.REQUEST_CODE_NUMERIC_BARCODE) {
            String code = data.getStringExtra("SCAN_RESULT_CODE");
            Log.e("CHECK","BARCODE CHECK>>>>>>>>>>>3");
            int groupIndex = getGroupIndexSend();
            int childIndex = getChildIndexSend();
            if (isNumeric(code)) {
                Item tempItems = listItem.get(groupIndex).getItems()
                        .get(childIndex);
                dataAccessOperator.updateValue(code, tempItems.getComItem(),
                        tempItems.getIdItem(), idIntervention, 0,
                        tempItems.getFlReserve(), tempItems.getNomItem(), tempItems.getIteration());
            } else {
                Toast.makeText(jobDetails,
                        jobDetails.getString(R.string.barcode_non_numeric),
                        Toast.LENGTH_SHORT).show();
            }
            refreshItem(getGroupIndexSend(), getChildIndexSend(), 0);
        }

    }

    /**
     * Method to check whether the value from barcode is numeric or text
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        NumberFormat formatter = NumberFormat.getInstance();
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(str, pos);
        return str.length() == pos.getIndex();
    }

    /**
     * Fetches The Images and attachment Discription To be shown on Attachments.
     */
    public void remplir() {

        Vector<Photo_Pda> vec = dataAccessOperator
                .getAllPhotoByIdIntervention(idIntervention);
        int nbrePhotos = vec.size();
        user = dataAccessOperator.getUser();

        Enumeration<Photo_Pda> en = vec.elements();

        if (photo_Pdas != null) {
            photo_Pdas.clear();
        } else {
            photo_Pdas = new ArrayList<Photo_Pda>();
        }

        // listCommentaire = new String[vec.size()];

        // photo = new byte[vec.size()][];

        while (en.hasMoreElements()) {
            Photo_Pda pp = en.nextElement();
            photo_Pdas.add(pp);

            // photo[i]=pp.getPhoto();
            // listCommentaire[i]=pp.getCommentaire();

        }

        Logger.log("Photo Pdas", photo_Pdas.size() + "");

        resetAttachmentListView();

        for (int j = 0; j < photo_Pdas.size(); j++) {
            if (!photo_Pdas.get(j).getCommentaire().startsWith("PIC_"))
                attachmentLinearView.addView(getView(j));
        }

        attachmentLinearView.invalidate();


        if (user != null && user.getId() != id_user || nbrePhotos >= 18) {
            addAttachmentIv.setEnabled(false);
        } else {
            addAttachmentIv.setEnabled(true);
        }

        if (photo_Pdas.size() == 0) {
            if (attachmentLinearView.getVisibility() == View.VISIBLE)
                attachmentLinearView.setVisibility(View.GONE);

            arrorBtn.setTag(Boolean.valueOf(false));
            relAttachmentContainer.setTag(Boolean.valueOf(false));
            arrorBtn.setImageResource(R.drawable.arrow_down);
        }

    }

    // /** The on item long click listener. */

    /**
     * Delete the attachment.
     *
     * @param photo_Pda the photo_ pda
     */
    protected void deleteAttachment(final Photo_Pda photo_Pda) {
        AlertDialog.Builder adbC = new AlertDialog.Builder(jobDetails);
        adbC.setMessage(R.string.txt_confirm);
        adbC.setPositiveButton(R.string.textYesLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dataAccessOperator.deletePhoto(photo_Pda.getIdPhoto());
                        remplir();
                    }
                });
        adbC.setNegativeButton(R.string.textNoLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        adbC.show();
    }

    /**
     * insert the Barcode scanned to DB And update AttachmentList.
     *
     * @param bm   the bm
     * @param code the code
     */
    @SuppressLint("SimpleDateFormat")
    public void enregistrerScan(Bitmap bm, String code, int requestCode) {

//        try {
//
//            bitmap = bm;
//            fixImageSize(bitmap);
//
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//            String idPhoto1 = sdf.format(new Date());
//            String idPhoto = id_user + "_" + idPhoto1;
//
//            if (requestCode == RequestCode.REQUEST_CODE_CHILDVIEW_BARCODE) {
//                int groupIndex = getGroupIndexSend();
//                int childIndex = getChildIndexSend();
//
//                Item tempItems = listItem.get(groupIndex).getItems()
//                        .get(childIndex);
//
//                String cmtr = "PIC_" + tempItems.getIdItem();
//
//                dataAccessOperator.insertPhoto(idIntervention,
//                        jobDetails.getString(R.string.barcodeFilePath), cmtr,
//                        "jpg", tempItems.getIteration());
//
//                dataAccessOperator.updateValue("1", tempItems.getComItem(),
//                        tempItems.getIdItem(), idIntervention, 0,
//                        tempItems.getFlReserve(), tempItems.getNomItem(), tempItems.getIteration());
//
//                refreshItem(getGroupIndexSend(), getChildIndexSend(), 0);
//            } else if (requestCode == RequestCode.REQUEST_CODE_TEXT_BARCODE) {
//                int groupIndex = getGroupIndexSend();
//                int childIndex = getChildIndexSend();
//                Item tempItems = listItem.get(groupIndex).getItems()
//                        .get(childIndex);
//                dataAccessOperator.updateValue(code, tempItems.getComItem(),
//                        tempItems.getIdItem(), idIntervention, 0,
//                        tempItems.getFlReserve(), tempItems.getNomItem(), tempItems.getIteration());
//                refreshItem(getGroupIndexSend(), getChildIndexSend(), 0);
//            } else if (requestCode == RequestCode.REQUEST_CODE_NUMERIC_BARCODE) {
//                int groupIndex = getGroupIndexSend();
//                int childIndex = getChildIndexSend();
//                Item tempItems = listItem.get(groupIndex).getItems()
//                        .get(childIndex);
//                dataAccessOperator.updateValue(code, tempItems.getComItem(),
//                        tempItems.getIdItem(), idIntervention, 0,
//                        tempItems.getFlReserve(), tempItems.getNomItem(), tempItems.getIteration());
//                refreshItem(getGroupIndexSend(), getChildIndexSend(), 0);
//            } else {
//                dataAccessOperator.insertPhoto(idIntervention,
//                        jobDetails.getString(R.string.barcodeFilePath), code,
//                        "jpg", 0);
//                deleteTempImage();
//                remplir();
//
//                showAttachmentList();
//
//            }
//
//        } catch (Exception e) {
//            Logger.printException(e);
//            e.printStackTrace();
//        }

        try {

            bitmap = bm;
            fixImageSize(bitmap);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String idPhoto1 = sdf.format(new Date());
            String idPhoto = id_user + "_" + idPhoto1;

            if (requestCode == RequestCode.REQUEST_CODE_CHILDVIEW_BARCODE) {
                int groupIndex = getGroupIndexSend();
                int childIndex = getChildIndexSend();

                Item tempItems = listItem.get(groupIndex).getItems()
                        .get(childIndex);

                String cmtr = "PIC_" + tempItems.getIdItem();

                dataAccessOperator.insertPhoto(idIntervention,
                        jobDetails.getString(R.string.barcodeFilePath), code,
                        "jpg", tempItems.getIteration());

                dataAccessOperator.updateValue("1", tempItems.getComItem(),
                        tempItems.getIdItem(), idIntervention, 0,
                        tempItems.getFlReserve(), tempItems.getNomItem(), tempItems.getIteration());

                refreshItem(getGroupIndexSend(), getChildIndexSend(), 0);
            } else if (requestCode == RequestCode.REQUEST_CODE_TEXT_BARCODE) {
                int groupIndex = getGroupIndexSend();
                int childIndex = getChildIndexSend();
                Item tempItems = listItem.get(groupIndex).getItems()
                        .get(childIndex);
                dataAccessOperator.updateValue(code, tempItems.getComItem(),
                        tempItems.getIdItem(), idIntervention, 0,
                        tempItems.getFlReserve(), tempItems.getNomItem(), tempItems.getIteration());
                refreshItem(getGroupIndexSend(), getChildIndexSend(), 0);
            } else if (requestCode == RequestCode.REQUEST_CODE_NUMERIC_BARCODE) {
                int groupIndex = getGroupIndexSend();
                int childIndex = getChildIndexSend();
                Item tempItems = listItem.get(groupIndex).getItems()
                        .get(childIndex);
                dataAccessOperator.updateValue(code, tempItems.getComItem(),
                        tempItems.getIdItem(), idIntervention, 0,
                        tempItems.getFlReserve(), tempItems.getNomItem(), tempItems.getIteration());
                refreshItem(getGroupIndexSend(), getChildIndexSend(), 0);
            } else {
                dataAccessOperator.insertPhoto(idIntervention,
                        jobDetails.getString(R.string.barcodeFilePath), code,
                        "jpg", 0);
                deleteTempImage();
                remplir();

                showAttachmentList();

            }

        } catch (Exception e) {
            Logger.printException(e);
            e.printStackTrace();
        }
    }

    /**
     * Resize the bitmap and save it to Db. Previous Developer Code
     *
     * @param bitmap the bitmap
     */
    public void fixImageSize(Bitmap bitmap) {
        int origWidth = bitmap.getWidth();
        int origHeight = bitmap.getHeight();
        if (origWidth > 1024 || origHeight > 1024) {
            Bitmap bmap = resizeImageToDb(bitmap);
            saveBitmap(bmap);
        } else
            saveBitmap(bitmap);
    }

    /**
     * Perform the action of resizing the bitmap . Previous Developer Code
     *
     * @param bitmap the bitmap
     * @return the bitmap
     */
    public Bitmap resizeImageToDb(Bitmap bitmap) {
        int origWidth = bitmap.getWidth();
        int origHeight = bitmap.getHeight();
        int newWidth = 1024;
        int newHeight = 1024;
        float scaleWidth;
        float scaleHeight;
        float scaleFactor;
        Matrix matrix;

        scaleWidth = (float) newWidth / origWidth;
        scaleHeight = (float) newHeight / origHeight;
        scaleFactor = Math.min(scaleWidth, scaleHeight);

        newWidth = Math.round(origWidth * scaleFactor);
        newHeight = Math.round(origHeight * scaleFactor);

        matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    /**
     * Save bitmap to Database. Previous Developer Code
     *
     * @param bitmap the bitmap
     */
    public void saveBitmap(Bitmap bitmap) {
        FileOutputStream fos = null;
        try {
            File f = new File(jobDetails.getString(R.string.barcodeFilePath));
            String name = f.getAbsolutePath();
            fos = new FileOutputStream(name);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            fos.flush();
            fos.close();
        } catch (Exception e) {
            Logger.printException(e);

        }
    }

    /**
     * Delete temp image. Previous Developer Code
     */
    public void deleteTempImage() {

        File f = new File(jobDetails.getString(R.string.barcodeFilePath));
        if (f.exists())
            f.delete();
    }

    /**
     * Save the image taken from camera to database and update attachment list.
     * Previous Developer Code
     */
    @SuppressLint("SimpleDateFormat")
    public void enregistrerPhotoCapturer(int requestCode) {
        try {
            restorePreferences();

            File capturedFile = new File(capturedPath);

            String fileName = capturedFile.getName();
            // CommonUtils.rotateFileImageIfNecessary(capturedFile);

            extention = fileName.substring(fileName.lastIndexOf(".") + 1);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String idPhoto1 = sdf.format(new Date());
            String idPhoto = id_user + "_" + idPhoto1;

            if (requestCode == RequestCode.REQUEST_CODE_CHILDVIEW_CAMERA) {
                int groupIndex = groupIndexSend;
                int childIndex = childIndexSend;

                Item tempItems = listItem.get(groupIndex).getItems()
                        .get(childIndex);

                String cmtr = "PIC_" + tempItems.getIdItem();

                dataAccessOperator.insertPhoto(idIntervention,
                        capturedPath, cmtr, extention, tempItems.getIteration());

                dataAccessOperator.updateValue("1", tempItems.getComItem(),
                        tempItems.getIdItem(), idIntervention, 0,
                        tempItems.getFlReserve(), tempItems.getNomItem(), tempItems.getIteration());

                refreshItem(groupIndex, childIndex, 0);

            } else if (requestCode == RequestCode.REQUEST_CODE_ATTACHMENTS_CAMERA) {
                String cmtr = "";
                dataAccessOperator.insertPhoto(idIntervention,
                        capturedPath, cmtr, extention, 0);

                remplir();
                showAttachmentList();
            }

        } catch (Exception e) {
            Logger.printException(e);
        }
    }

    /**
     * Restore preferences. Previous Developer Code
     */
    private void restorePreferences() {
        SharedPreferences settings = jobDetails
                .getPreferences(Context.MODE_PRIVATE);
        capturedPath = settings.getString("fileUriPath", "");
    }

    /**
     * Save the Images taken from gallary and save it to Db and update the
     * attachment list.
     *
     * @param selectedImage the selected image
     */
    public void enregistrerPhoto(Uri selectedImage, int requestCode) {
        try {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = jobDetails.getContentResolver().query(
                    selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            if (cursor.getCount() == 0) {
                Toast.makeText(jobDetails.getApplicationContext(),
                        R.string.msg_error_photo, Toast.LENGTH_SHORT).show();
            } else {
                // byteArray = null;
                // cursor.moveToFirst();
                //
                // int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                // cursor.close();

                /*
                 * previous code
                 */
                // String capturedPath = CommonUtils
                // .getRealPathOfGalleryPhotosFromURI(selectedImage,
                // jobDetails);

                /*
                 * new changes.
                 */
                // String capturedPath =
                // CommonUtils.getPathOfImage(selectedImage,
                // jobDetails);
                String capturedPath = CommonUtils.getPath(jobDetails,
                        selectedImage);

                File capturedFile = new File(capturedPath);

                // CommonUtils.rotateFileImageIfNecessary(capturedFile);

                String fileName = capturedFile.getName();
                extention = fileName.substring(fileName.lastIndexOf(".") + 1);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                String idPhoto1 = sdf.format(new Date());
                String idPhoto = id_user + "_" + idPhoto1;

                if (requestCode == RequestCode.REQUEST_CODE_ATTACHMENTS_LIBRARY) {

                    String cmtr = "";
                    dataAccessOperator.insertPhoto(idIntervention,
                            capturedPath, cmtr, extention, 0);

                    remplir();
                    showAttachmentList();

                } else if (requestCode == RequestCode.REQUEST_CODE_CHILDVIEW_LIBRARY) {

                    int groupIndex = getGroupIndexSend();
                    int childIndex = getChildIndexSend();

                    Item tempItems = listItem.get(groupIndex).getItems()
                            .get(childIndex);

                    String cmtr = "PIC_" + tempItems.getIdItem();

                    dataAccessOperator.insertPhoto(idIntervention,
                            capturedPath, cmtr, extention, tempItems.getIteration());

                    dataAccessOperator.updateValue("1", tempItems.getComItem(),
                            tempItems.getIdItem(), idIntervention, 0,
                            tempItems.getFlReserve(), tempItems.getNomItem(), tempItems.getIteration());

                    refreshItem(getGroupIndexSend(), getChildIndexSend(), 0);

                }
            }
        } catch (Exception e) {
            Logger.printException(e);
            e.printStackTrace();
        }
    }


    public void rotateFileImageIfNecessary(File imageFile) {

        try {
            String filePath = imageFile.getAbsolutePath();

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 1);
            int rotateAngle = 0;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotateAngle = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotateAngle = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotateAngle = 90;
                    break;
                default:
                    break;
            }

            Bitmap mybitmap = null;
            try {
                if (rotateAngle != 0) {
                    Bitmap orgBitmap = BitmapFactory.decodeFile(filePath);
                    Matrix matrix = new Matrix();
                    matrix.setRotate(rotateAngle);
                    mybitmap = Bitmap.createBitmap(orgBitmap, 0, 0,
                            orgBitmap.getWidth(), orgBitmap.getHeight(),
                            matrix, true);
                }
            } catch (OutOfMemoryError error) {
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = 4;
                Bitmap orgBitmap = BitmapFactory.decodeFile(filePath, o2);

                if (rotateAngle != 0) {
                    Matrix matrix = new Matrix();
                    matrix.setRotate(rotateAngle);
                    mybitmap = Bitmap.createBitmap(orgBitmap, 0, 0,
                            orgBitmap.getWidth(), orgBitmap.getHeight(),
                            matrix, true);
                }
                error.printStackTrace();
            }
            if (mybitmap == null || rotateAngle == 0)
                return;

            try {
                FileOutputStream fOut = new FileOutputStream(filePath);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                mybitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                bos.writeTo(fOut);
                bos.close();
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleImageRequest(final Intent data, int requestCode) {

        Uri selectedImage;
        String queryImageUrl = "";

        try {

            if (data != null && data.getData() != null) {

                selectedImage = data.getData();
                queryImageUrl = selectedImage.getPath();
                queryImageUrl = FileUtilsNew.compressImageFile(jobDetails, queryImageUrl,
                        false, selectedImage);

                Log.e("TAG", "IMAGE VALUES IS ===>" + queryImageUrl);

//                File capturedFile = new File(queryImageUrl);

                String capturedPath = queryImageUrl;

                File capturedFile = new File(capturedPath);

//                rotateFileImageIfNecessary(capturedFile);

                String fileName = capturedFile.getName();
                extention = fileName.substring(fileName.lastIndexOf(".") + 1);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                String idPhoto1 = sdf.format(new Date());
                String idPhoto = id_user + "_" + idPhoto1;

                if (requestCode == RequestCode.REQUEST_CODE_ATTACHMENTS_LIBRARY) {

                    String cmtr = "";
                    dataAccessOperator.insertPhoto(idIntervention,
                            capturedPath, cmtr, extention, 0);

                    remplir();
                    showAttachmentList();

                } else if (requestCode == RequestCode.REQUEST_CODE_CHILDVIEW_LIBRARY) {

                    int groupIndex = getGroupIndexSend();
                    int childIndex = getChildIndexSend();

                    Item tempItems = listItem.get(groupIndex).getItems()
                            .get(childIndex);

                    String cmtr = "PIC_" + tempItems.getIdItem();

                    dataAccessOperator.insertPhoto(idIntervention,
                            capturedPath, cmtr, extention, tempItems.getIteration());

                    dataAccessOperator.updateValue("1", tempItems.getComItem(),
                            tempItems.getIdItem(), idIntervention, 0,
                            tempItems.getFlReserve(), tempItems.getNomItem(), tempItems.getIteration());

                    refreshItem(getGroupIndexSend(), getChildIndexSend(), 0);

                }


            } else {

                Toast.makeText(jobDetails,
                        R.string.msg_error_photo, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Logger.printException(e);
        }


    }


    /**
     * Gets the real path from uri.
     *
     * @param context    the context
     * @param contentUri the content uri
     * @return the real path from uri
     */
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null,
                    null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Show and save signature to Db.
     */
    private void showAndSaveSignature() {

        if (sign.equals("SIGN_DONE")) {
            byte[] retour = dataAccessOperator.getPhotoById(idIntervention,
                    "SIGN_USER");
            if (retour != null) {
//                Bitmap bitmap = BitmapFactory.decodeByteArray(retour, 0,
//                        retour.length);
//
//                mysignatureDataIv.setImageBitmap(bitmap);

                //new changes
                Glide.with(jobDetails)
                        .load(retour)
                        .asBitmap()
                        .override(250, 250)
                        .fitCenter()
                        .placeholder(R.drawable.library_iicon)
                        .into(mysignatureDataIv);

            }

            byte[] retour1 = dataAccessOperator.getPhotoById(idIntervention,
                    "SIGN_CLIENT");
            if (retour1 != null) {
//                Bitmap bitmap = BitmapFactory.decodeByteArray(retour1, 0,
//                        retour1.length);
//                customerSignatureDataTv.setImageBitmap(bitmap);

                //new changes
                Glide.with(jobDetails)
                        .load(retour1)
                        .asBitmap()
                        .override(250, 250)
                        .fitCenter()
                        .placeholder(R.drawable.library_iicon)
                        .into(customerSignatureDataTv);

            }
            String cmt1 = dataAccessOperator.getSignUser(idIntervention);

            if ((cmt1 != null) && (!TextUtils.isEmpty(cmt1))) {
                textMySignatureLabelTv.setText(cmt1);
                textMySignatureLabelTv.setVisibility(View.VISIBLE);
            }

            // retrieve cmt from T_INTERVENTIONS table
            String cmt = dataAccessOperator.getSignClient(idIntervention);

            if ((cmt != null) && (!TextUtils.isEmpty(cmt))) {
                textSignatureLabelTv.setText(cmt);
                textSignatureLabelTv.setVisibility(View.VISIBLE);
            }

        } else {

            if (sign.equals("SIGN_USER")) {

                byte[] retour = dataAccessOperator.getPhotoById(idIntervention,
                        "SIGN_USER");
                if (retour != null) {

//                    Bitmap bitmap = BitmapFactory.decodeByteArray(retour, 0,
//                            retour.length);
//
//                    mysignatureDataIv.setImageBitmap(bitmap);

                    //new changes
                    Glide.with(jobDetails)
                            .load(retour)
                            .asBitmap()
                            .override(250, 250)
                            .fitCenter()
                            .placeholder(R.drawable.library_iicon)
                            .into(mysignatureDataIv);
                }

                String cmt1 = dataAccessOperator.getSignUser(idIntervention);

                if ((cmt1 != null) && (!TextUtils.isEmpty(cmt1))) {
                    textMySignatureLabelTv.setText(cmt1);
                    textMySignatureLabelTv.setVisibility(View.VISIBLE);
                }
            }

            if (sign.equals("SIGN_CLIENT")) {
                byte[] retour1 = dataAccessOperator.getPhotoById(
                        idIntervention, "SIGN_CLIENT");
                if (retour1 != null) {
//                    Bitmap bitmap = BitmapFactory.decodeByteArray(retour1, 0,
//                            retour1.length);
//                    customerSignatureDataTv.setImageBitmap(bitmap);

                    //new changes
                    Glide.with(jobDetails)
                            .load(retour1)
                            .asBitmap()
                            .override(250, 250)
                            .fitCenter()
                            .placeholder(R.drawable.library_iicon)
                            .into(customerSignatureDataTv);

                }

                // retrieve cmt from T_INTERVENTIONS table
                String cmt = dataAccessOperator.getSignClient(idIntervention);

                if ((cmt != null) && (!TextUtils.isEmpty(cmt))) {
                    textSignatureLabelTv.setText(cmt);
                    textSignatureLabelTv.setVisibility(View.VISIBLE);
                }

            }

        }

    }

    /**
     * The on group expand listener.
     */
    OnGroupExpandListener onGroupExpandListener = new OnGroupExpandListener() {

        @Override
        public void onGroupExpand(int groupPosition) {

            if ((lastExpandedGroupPosition != -1)
                    && (groupPosition != lastExpandedGroupPosition)) {

                expandableListView.collapseGroup(lastExpandedGroupPosition);
            }
            lastExpandedGroupPosition = groupPosition;
            if (attachmentLinearView.getVisibility() == View.VISIBLE) {
                arrorBtn.performClick();
                relAttachmentContainer.performClick();
            }

        }
    };

    OnGroupClickListener onGroupClickListener = new OnGroupClickListener() {

        @Override
        public boolean onGroupClick(ExpandableListView parent, View v,
                                    int groupPosition, long id) {

            Boolean shouldExpand = (!expandableListView
                    .isGroupExpanded(groupPosition));
            expandableListView.collapseGroup(lastClickedPosition);

            if (shouldExpand) {
                // generateExpandableList();
                expandableListView.expandGroup(groupPosition);
                expandableListView.setSelectionFromTop(groupPosition, 0);
            }
            lastClickedPosition = groupPosition;
            return true;

        }
    };

    /**
     * called to fetch a fresh set of data from database this function is called
     * after every inout is performed and adapter is notifyied with new Data.
     *
     * @param groupPosition the group position
     */
    public void refreshItem(int groupPosition, int childPosition, int isStatusChanged) {

        synchronized (this) {
            Logger.log("ReportsDetailFragmentHelper>>>>>>>>>",
                    "Refresh Item Called");

            ItemsAsyncTask itemsAsyncTask = new ItemsAsyncTask();

            itemsAsyncTask.execute(groupPosition, childPosition, isStatusChanged);

            //post the update event to trigger update in report view
            EventBus.getDefault().post(new ReportFamilyUpdateEvent(groupPosition));
        }

        //new changes for hiding the keyboard v48
        if (jobDetails != null) {
            jobDetails.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            jobDetails.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }

    }

    /**
     * The Class ItemsAsyncTask.
     */
    private class ItemsAsyncTask extends AsyncTaskCoroutine<Integer, Boolean> {

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        /**
         * The group position.
         */
        int groupPosition;

        /**
         * Child position
         */
        int childPosition;

        /**
         * Status changed = 1 else 0.
         */
        int isStatusChanged;

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        public void onPreExecute() {
            super.onPreExecute();
            Logger.log("onPreExecute>>>", "Called");

            DialogUtils.showProgressDialog(jobDetails,
                    jobDetails.getString(R.string.textSaving),
                    jobDetails.getString(R.string.textPleaseWaitLable), false);

        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        public Boolean doInBackground(Integer... params) {
            groupPosition = params[0].intValue();
            childPosition = params[1].intValue();
            isStatusChanged = params[2].intValue();

            try {
                Logger.log("doInBackground>>>", "Called");
                Families famille = listItem.get(groupPosition);
                HashMap<Integer, Item> items = dataAccessOperator.getAllItem(
                        idIntervention, famille.getIdFamily(), famille.getIteration());

                Vector<Item> vectorItem = famille.getItems();
                vectorItem.clear();

                Vector<Item> newvectorItem = cleanListItem(items);

                vectorItem.addAll(newvectorItem);

                Logger.log("Size of Items ", items.size() + "");

                listItem.set(groupPosition, famille);

                itemChanged = vectorItem.get(childPosition);
                itemFamily = items;

                return true;

            } catch (Exception e) {
                Logger.printException(e);
                return false;
            }
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        public void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            Logger.log("onPostExecute>>>", "Called");
            if (result.booleanValue()) {

                // expandableListView.setAdapter(reportsExpandableListAdapter);
                //delete the signature for all the case except if status is changed.
                if (isStatusChanged == 0) {
                    checkSignCond(itemChanged, itemFamily);
                }

                try {
                    jobDetails.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (reportsExpandableListAdapter != null)
                                reportsExpandableListAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (Exception e) {

                }
//                reportsExpandableListAdapter.notifyDataSetChanged();

                // expandableListView.expandGroup(groupPosition);
                // ((BaseExpandableListAdapter) expandableListView
                // .getExpandableListAdapter()).notifyDataSetChanged();

            }

            try {
            } finally {
                DialogUtils.dismissProgressDialog();

            }
        }

    }

    // TODO Check if the changed item is type 7(signature) or not.
    // TODO If it is not signature, then check if it has a condition item or not.
    // TODO If it has a condition item, check the condition type is 7 or not.
    // TODO If it is not 7 then delete the all the signatures.
    private void checkSignCond(Item item, HashMap<Integer, Item> map) {
        if (item.getIdTypeItem() != 7) {
            if (item.getCond() == 0) {
                deleteSignIfModified();
            } else {
                Item itemCond = map.get(item.getCond());
                if (itemCond.getIdTypeItem() != 7) {
                    deleteSignIfModified();
                }
            }
        }
    }

    /**
     * Clean list item.
     *
     * @param map the map
     * @return the vector
     */
    public Vector<Item> cleanListItem(HashMap<Integer, Item> map) {
        Vector<Item> vect = new Vector<Item>();
        Iterator<Item> iter = map.values().iterator();
        while (iter.hasNext()) {
            Item item1 = (Item) iter.next();
            item1 = listeItems(map, item1);
            if (item1 != null)
                vect.add(item1);
        }
        Collections.sort(vect);
        return vect;
    }

    /**
     * Liste items.
     *
     * @param map  the map
     * @param item the item
     * @return the item
     */
    public Item listeItems(HashMap<Integer, Item> map, Item item) {

        if (item.getCond() == 0) {
            return item;
        } else {
            Item itemP = map.get(item.getCond());
            if (itemP != null) {
                if (RecursiveCompare(item, item, itemP, map)) {
                    return item;
                } else
                    return null;
            } else {
                return null;
            }
        }

    }

    /**
     * Recursive compare.
     *
     * @param itemR the item r
     * @param itemF the item f
     * @param itemP the item p
     * @param map   the map
     * @return the boolean
     */
    public Boolean RecursiveCompare(Item itemR, Item itemF, Item itemP,
                                    HashMap<Integer, Item> map) {

        try {
            if (CompareValues(itemF, itemP)) {

                if (itemP.getCond() != 0) {

                    return RecursiveCompare(itemR, itemP, map.get(itemP.getCond()),
                            map);
                } else
                    return true;
            } else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Compare values.
     *
     * @param itemF the item f
     * @param itemP the item p
     * @return the boolean
     */
    public Boolean CompareValues(Item itemF, Item itemP) throws Exception {
        Boolean drp = false;

        if (itemP.getValItem() == 1 || itemP.getIdTypeItem() == 7
                || itemP.getIdTypeItem() == 8 || itemP.getIdTypeItem() == 9) {
            String operator = "", value = "";
            Date dF, dP;

            switch (itemP.getIdTypeItem()) {
                case 3:
                    operator = itemF.getVal_cond().substring(0, 1);
                    value = itemF.getVal_cond().substring(1);
                    dF = getDateF(value);
                    dP = getDateF(itemP.getValeurNet());

                    if (operator.equals("=")) {
                        if (dF.getTime() == dP.getTime())
                            drp = true;
                    } else if (operator.equals("<")) {
                        if (dF.getTime() > dP.getTime())
                            drp = true;
                    } else if (operator.equals(">")) {
                        if (dF.getTime() < dP.getTime())
                            drp = true;
                    }

                    break;
                case 4:
                    operator = itemF.getVal_cond().substring(0, 1);
                    value = itemF.getVal_cond().substring(1);
                    dF = getTimes(value);
                    dP = getTimes(itemP.getValeurNet());

                    if (operator.equals("=")) {
                        if (dF.getTime() == dP.getTime())
                            drp = true;
                    } else if (operator.equals("<")) {
                        if (dF.getTime() > dP.getTime())
                            drp = true;
                    } else if (operator.equals(">")) {
                        if (dF.getTime() < dP.getTime())
                            drp = true;
                    }

                    break;
                case 5:
                    operator = itemF.getVal_cond().substring(0, 1);
                    Double val = Double.parseDouble(itemF.getVal_cond()
                            .substring(1));

                    Double valp;
                    try {
                        valp = Double.parseDouble(itemP.getValeurNet());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        valp = 0.00;

                    }

                    if (operator.equals("=")) {

                        if (val.equals(valp))
                            drp = true;
                    } else if (operator.equals("<")) {
                        if (val > valp)
                            drp = true;
                    } else if (operator.equals(">")) {
                        if (val < valp)
                            drp = true;
                    }

                    break;
                case 6:
                    break;
                case 7:
                    if (itemF.getVal_cond().equals("1")) {
                        if (itemP.getValeurNet().equals("1"))
                            drp = true;
                    } else if (itemF.getVal_cond().equals("2")) {
                        if (itemP.getValeurNet().equals("2")
                                || itemP.getValeurNet().equals(""))
                            drp = true;
                    }

                    break;
                case 8:
                    if (itemF.getVal_cond().equals("1")) {
                        if (itemP.getValeurNet().equals("1"))
                            drp = true;
                    } else if (itemF.getVal_cond().equals("2")) {
                        if (itemP.getValeurNet().equals("2")
                                || itemP.getValeurNet().equals(""))
                            drp = true;
                    }

                    break;
                case 9:
                    if (itemF.getVal_cond().equals("1")) {
                        if (!itemP.getValeurNet().equals(""))
                            drp = true;
                    } else if (itemF.getVal_cond().equals("2")) {
                        if (itemP.getValeurNet().equals("2")
                                || itemP.getValeurNet().equals(""))
                            drp = true;
                    }

                    break;

                case 11:
                    operator = itemF.getVal_cond().substring(0, 1);
                    value = itemF.getVal_cond().substring(1);
                    dF = getTimes(value);
                    dP = getTimes(itemP.getValeurNet());

                    if (operator.equals("=")) {
                        if (dF.getTime() == dP.getTime())
                            drp = true;
                    } else if (operator.equals("<")) {
                        if (dF.getTime() > dP.getTime())
                            drp = true;
                    } else if (operator.equals(">")) {
                        if (dF.getTime() < dP.getTime())
                            drp = true;
                    }

                    break;

                default:
//                    if (itemF.getVal_cond().contains("@@@")) {
//
//                    } else if (itemF.getVal_cond().equals(itemP.getValeurNet()))
//                        drp = true;

                    String[] test = itemF.getVal_cond().split("@@@");
                    if (test != null && test.length > 0) {
                        for (int i = 0; i < test.length; i++) {
//                            if (itemP.getValeurNet().contains("@@@")) {
                            String[] test1 = itemP.getValeurNet().split("@@@");
                            if (test1 != null && test1.length > 0) {
                                for (int j = 0; j < test1.length; j++) {
                                    if (test[i].trim().equals(test1[j].trim())) {
                                        drp = true;
                                        break;
                                    }
                                }
//                                }
                            } else if (test[i].trim().equals(itemP.getValeurNet().trim())) {
                                drp = true;
                                break;
                            }
                        }
                    }

                    break;

            }
        }
        return drp;
    }

    /**
     * Gets the date f.
     *
     * @param mDate the m date
     * @return the date f
     */
    public Date getDateF(String mDate) {

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        Date date;
        try {
            date = df.parse(mDate + " 00:00:00.000");
            return date;
        } catch (ParseException e) {
            Logger.printException(e);
            return new Date();
        }
    }

    /**
     * Gets the times.
     *
     * @param mDate the m date
     * @return the times
     */
    public Date getTimes(String mDate) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");
        Date date;
        try {
            date = df.parse("01/01/0001 " + mDate + ":00.000");
            return date;
        } catch (ParseException e) {
            Logger.printException(e);
            return new Date();
        }
    }

    /**
     * Called When Job is started it notifies the ReportsExpandableListAdapter
     * so that all the input fields become enabled.
     */
    public void doOnJobStartStop() {
        try {
            jobDetails.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(reportsExpandableListAdapter!=null)
                    reportsExpandableListAdapter.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {

        }
//        reportsExpandableListAdapter.notifyDataSetChanged();

        if (jobDetails.getUpdatedValueOfStatus() != KEYS.JObDetail.JOB__STARTED) {

            addAttachmentIv.setEnabled(false);
            addAttachmentIv.setImageResource(R.drawable.add_icon_disable);

        } else {
            addAttachmentIv.setEnabled(true);
            addAttachmentIv.setImageResource(R.drawable.add_icon);
        }

    }

    /**
     * Gets the expandable list view.
     *
     * @return the expandable list view
     */
    public ExpandableListView getExpandableListView() {
        return expandableListView;
    }

    /**
     * Reset Attachment ListView and clear all the data.
     */
    public void resetAttachmentListView() {

        attachmentLinearView.removeAllViews();

    }

    /**
     * Removes the footer view.
     */
    public void removeFooterView() {
        // expandableListView.removeFooterView(footerView);
        //
        // footerView = null;
    }

    /**
     * Add view.
     *
     * @param position the position
     * @return the view
     */
    public View getView(final int position) {

        View row = inflater.inflate(R.layout.layout_attachement_list_tem, null,
                false);

        final int pos = position;
        RelativeLayout containerAttachment = (RelativeLayout) row
                .findViewById(R.id.containerAttachment);
        ImageView removeAttachmentIconIv = (ImageView) row
                .findViewById(R.id.removeAttachmentIconIv);

        final Photo_Pda photo_Pda = photo_Pdas.get(position);
        final byte[] mPhoto = photo_Pda.getPhoto();

//        Bitmap bitmap;
//        BitmapFactory.Options opt = new BitmapFactory.Options();
//        opt.inDither = true;
//        opt.inPreferredConfig = Bitmap.Config.RGB_565;
//        bitmap = BitmapFactory.decodeByteArray(mPhoto, 0, mPhoto.length, opt);
        ImageView img = (ImageView) row.findViewById(R.id.attachmentIv);

        // bmd = new BitmapDrawable(resizeBitmap(bitmap,newwidth,newheight));

//        img.setImageBitmap(bitmap);

        //new changes
        Glide.with(jobDetails)
                .load(mPhoto)
                .asBitmap()
                .override(200, 200)
                .fitCenter()
                .placeholder(R.drawable.library_iicon)
                .into(img);

        img.setTag(position);
        img.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(jobDetails, EditReportImage.class);
                intent.putExtra(KEYS.CurrentJobs.ID, idIntervention);
                intent.putExtra(KEYS.Photos.KEY_ID_PHOTO, photo_Pda.getIdPhoto());
                intent.putExtra(KEYS.EditImage.KEY_ATTACHEMENT_IMAGE_ID, KEYS.EditImage.KEY_IMAGE_FROM_REPORTS_ATTACHMENT);
                jobDetails.startActivity(intent);

//                showImg(v);

            }
        });
        // img.setImageDrawable(bmd);

        final TextView label = (TextView) row
                .findViewById(R.id.attachmentCommentEt);
        label.setText(photo_Pda.getCommentaire());

        photo_Pda.setPosition(position);
        removeAttachmentIconIv.setTag(photo_Pda);
        containerAttachment.setTag(photo_Pda);

        containerAttachment.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {
                    Photo_Pda photo_Pda = (Photo_Pda) v.getTag();
                    modifyComment(photo_Pda.getIdPhoto(),
                            photo_Pda.getCommentaire(), photo_Pda.getPhoto(),
                            pos, ((RelativeLayout) v));

                }
            }
        });
        containerAttachment.setLongClickable(true);

        removeAttachmentIconIv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Logger.log("SetOnClickListner", "Called");
                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {
                    Photo_Pda photo_Pda = (Photo_Pda) v.getTag();
                    deleteAttachment(photo_Pda, photo_Pda.getPosition());
                }

            }
        });

        /*
         * containerAttachment.setOnLongClickListener(new OnLongClickListener()
         * {
         *
         * @Override public boolean onLongClick(View v) { method stub
         * Logger.log("SetOnClickListner", "Called"); if
         * (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED)
         * { Photo_Pda photo_Pda = (Photo_Pda) v.getTag();
         * deleteAttachment(photo_Pda, photo_Pda.getPosition()); }
         *
         * return false;
         *
         * } });
         */

        return row;
    }

    /**
     * Show img.
     *
     * @param v the v
     */
    public void showImg(View v) {

        final Dialog nagDialog = new Dialog(jobDetails,
                android.R.style.Theme_Translucent_NoTitleBar);
        nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        nagDialog.setCancelable(true);
        nagDialog.setContentView(R.layout.item_image);
        int position = (Integer) v.getTag();

        ImageView btnClose = (ImageView) nagDialog.findViewById(R.id.closeBtn);
        TouchImageView ivPreview = (TouchImageView) nagDialog
                .findViewById(R.id.imageItem);
        byte[] bytes = photo_Pdas.get(position).getPhoto();
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        Logger.log("ImageWidth Height ",
                bitmap.getWidth() + " " + bitmap.getHeight());

        ivPreview.setImageBitmap(bitmap);

        btnClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (nagDialog != null)
                    nagDialog.dismiss();
            }
        });

//        ivPreview.setOnTouchListener(this);

        nagDialog.show();
    }

    /**
     * Modify comment.
     *
     * @param idPh  the id ph
     * @param cmtr  the cmtr
     * @param photo the photo
     * @param pos   the pos
     * @param view  the view
     */
    protected void modifyComment(final String idPh, final String cmtr,
                                 byte[] photo, final int pos, final RelativeLayout view) {

        AlertDialog.Builder adb = new AlertDialog.Builder(jobDetails);

        final TextView textView = (TextView) view.getChildAt(3);
        View alertDialogView = inflater.inflate(R.layout.dialogphoto, null);
        adb.setView(alertDialogView);
        final EditText commentEt = (EditText) alertDialogView
                .findViewById(R.id.commentaire);

        commentEt.setText(cmtr);
        if ((jobDetails).getUpdatedValueOfStatus() == 3) {
            adb.setNeutralButton(R.string.modifier,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            String cm = String.valueOf(commentEt.getText());
                            Logger.log("Text view", "cm : " + cm);
                            dataAccessOperator.modifierPhotoById(idPh, cm);
                            Photo_Pda photo_Pda = (Photo_Pda) view.getTag();
                            photo_Pda.setString(cm);
                            view.setTag(photo_Pda);

                            textView.setText(cm);
                            CommonUtils.hideKeyboard(jobDetails, commentEt);
                            // remplir();

                        }
                    });
        }
        adb.setNegativeButton(R.string.textCancelLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // remplir();
                    }
                });

        adb.show();

    }

    /**
     * Delete attachment.
     *
     * @param photo_Pda the photo_ pda
     * @param pos       the pos
     */
    protected void deleteAttachment(final Photo_Pda photo_Pda, final int pos) {
        AlertDialog.Builder adbC = new AlertDialog.Builder(jobDetails);

        adbC.setMessage(R.string.txt_confirm);
        adbC.setPositiveButton(R.string.textYesLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (pos <= photo_Pdas.size() - 1) {
                            dataAccessOperator.deletePhoto(photo_Pda.getIdPhoto());
                            photo_Pdas.remove(pos);
                            attachmentLinearView.removeAllViews();
                            remplir();
                        }
                    }
                });
        adbC.setNegativeButton(R.string.textNoLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        adbC.show();
    }

    /**
     * Show attachment list.
     */
    private void showAttachmentList() {
        if (attachmentLinearView.getVisibility() == View.GONE)
            attachmentLinearView.setVisibility(View.VISIBLE);

        expandableListView.collapseGroup(lastExpandedGroupPosition);
        arrorBtn.setTag(Boolean.valueOf(true));
        relAttachmentContainer.setTag(Boolean.valueOf(true));
        arrorBtn.setImageResource(R.drawable.arrow_up);

    }

    /**
     * Deletes the customer & user signature, if any changes made to reports.
     */
    private void deleteSignIfModified() {
        GestionAcces gt = dataAccessOperator.getAcces();
        if (gt.getFlSectionDelSign() == 1) {
            DeleteSignaturesAsync deleteSignaturesAsync = new DeleteSignaturesAsync();
            deleteSignaturesAsync.execute();
        }
    }


    public int getGroupIndexSend() {
        return groupIndexSend;
    }

    public void setGroupIndexSend(int groupIndexSend) {
        this.groupIndexSend = groupIndexSend;
    }


    public int getChildIndexSend() {
        return childIndexSend;
    }

    public void setChildIndexSend(int childIndexSend) {
        this.childIndexSend = childIndexSend;
    }

    /**
     * Async task to delete all the signatures in reports screen.
     */
    private class DeleteSignaturesAsync extends AsyncTaskCoroutine<Void, Void> {

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            DialogUtils.showProgressDialog(jobDetails,
                    jobDetails.getString(R.string.textSaving),
                    jobDetails.getString(R.string.textPleaseWaitLable), false);
        }

        @Override
        public Void doInBackground(Void... params) {
            dataAccessOperator.deleteSignature(idIntervention, "SIGN_CLIENT");
            dataAccessOperator.deleteSignature(idIntervention, "SIGN_USER");

            //delete signature inside the reports
            for (int i = 0; i < listItem.size(); i++) {
                Families famille = listItem.get(i);
                Vector<Item> vectorItem = famille.getItems();
                for (int j = 0; j < vectorItem.size(); j++) {
                    Item item = vectorItem.get(j);
                    if (item.getIdTypeItem() == 7) {
                        String type = "SIGN_" + item.getIdItem();
                        dataAccessOperator.deleteSignature(idIntervention, type);
                        dataAccessOperator.updateValue("", item.getComItem(), item.getIdItem(), idIntervention, 0,
                                item.getFlReserve(), item.getNomItem(), item.getIteration());
                    }
                }
            }

            return null;
        }

        @Override
        public void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mysignatureDataIv.setImageBitmap(null);

            customerSignatureDataTv.setImageBitmap(null);

            DialogUtils.dismissProgressDialog();
        }
    }

    //----------------------------PINCH...AND...ZOOM...FUNCTIONALITY...FOR...IMAGEVIEW-----------------------------

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        // TODO Auto-generated method stub

        ImageView view = (ImageView) v;
        dumpEvent(event);

        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                Log.d(TAG, "mode=DRAG");
                mode = DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                oldDist = spacing(event);
                Log.d(TAG, "oldDist=" + oldDist);
                if (oldDist > 10f) {
                    savedMatrix.set(matrix);
                    midPoint(mid, event);
                    mode = ZOOM;
                    Log.d(TAG, "mode=ZOOM");
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                Log.d(TAG, "mode=NONE");
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == DRAG) {
                    // ...
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY()
                            - start.y);
                } else if (mode == ZOOM) {
                    float newDist = spacing(event);
                    Log.d(TAG, "newDist=" + newDist);
                    if (newDist > 10f) {
                        matrix.set(savedMatrix);
                        float scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix);
        return true;
    }

    private void dumpEvent(MotionEvent event) {
        String names[] = {"DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE",
                "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?"};
        StringBuilder sb = new StringBuilder();
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        sb.append("event ACTION_").append(names[actionCode]);
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN
                || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(
                    action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
            sb.append(")");
        }
        sb.append("[");
        for (int i = 0; i < event.getPointerCount(); i++) {
            sb.append("#").append(i);
            sb.append("(pid ").append(event.getPointerId(i));
            sb.append(")=").append((int) event.getX(i));
            sb.append(",").append((int) event.getY(i));
            if (i + 1 < event.getPointerCount())
                sb.append(";");
        }
        sb.append("]");
        Log.d(TAG, sb.toString());
    }

    /**
     * Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    //----------------------------PINCH...AND...ZOOM...FUNCTIONALITY...FOR...IMAGEVIEW-----------------------------

    //----------------------------SHARED...BLOCK...FUNCTIONALITY---------------------------------------------------

    /**
     * Initialize shared block views.
     */
    private void initializeSharedBlock() {
        flAddSharedBlock = (FrameLayout) footerView.findViewById(R.id.lin_add_shared_block);
        LinearLayout linInnerView = (LinearLayout) footerView.findViewById(R.id.lin_inner_view);
        android.widget.TextView txtAddIcon = (android.widget.TextView) footerView.findViewById(R.id.txt_add_icon);

        Typeface typeface = Typeface.createFromAsset(jobDetails.getAssets(), jobDetails.getString(R.string.fontName_material_icon));
        txtAddIcon.setTypeface(typeface);

        MaterialDesignUtils.getInstance(jobDetails).setRippleEffect(linInnerView);

        flAddSharedBlock.setOnClickListener(onClickListener);
    }

    /**
     * Invokes when a shared block is selected.
     *
     * @param sharedBlocks
     */
    public void onSharedBlockSelected(SharedBlocks sharedBlocks) {
        //will invoke when a shared block is selected
        int position = dataAccessOperator.getLastPosition(idModel, idIntervention) + 1;
        //TODO need to change the obligatoire in future.
        if (sharedBlocks != null) {
            boolean isAdded = dataAccessOperator.addSharedBlock(id_user, idModel, sharedBlocks.getIdBlock(), idIntervention, 0, 0, sharedBlocks.getBlockName(), 1, position);
            if (isAdded) {
                Toast.makeText(jobDetails, jobDetails.getString(R.string.txt_ack_msg), Toast.LENGTH_SHORT).show();
                doOnSyncronize();
                EventBus.getDefault().post(new ReportViewUpdateEvent());
            } else {
                Toast.makeText(jobDetails, jobDetails.getString(R.string.msg_error), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * gets list of shared blocks which aren't already present in the report.
     *
     * @return list of shared blocks
     */
    private void getSharedBlock() {
        listSharedBlock = new ArrayList<>();
//        ArrayList<SharedBlocks> listAllSB =new ArrayList<>();
        ArrayList<SharedBlocks> listAllSB = dataAccessOperator.getSBList(idIntervention);

        if (listAllSB != null && listAllSB.size() > 0) {
            for (int i = 0; i < listAllSB.size(); i++) {
                boolean isFamilyAdded = false;
                SharedBlocks sb = listAllSB.get(i);
                if (listItem != null && listItem.size() > 0)
                    for (int j = 0; j < listItem.size(); j++) {
                        if (sb.getIdBlock() == listItem.get(j).getIdFamily()) {
                            isFamilyAdded = true;
                            break;
                        }
                    }
                if (!isFamilyAdded) {
                    listSharedBlock.add(sb);
                }
            }
        }
        setAddSBVisibility();
    }

    /**
     * Sets visibility for add shared block container.
     */
    private void setAddSBVisibility() {
        //if the report doesn't have any single shared block, don't show the add sb container.
        //likewise, if all the shared blocks are added to the report, don't show the add sb container.
        if (listSharedBlock.size() == 0) {
            flAddSharedBlock.setVisibility(View.GONE);
        } else {
            flAddSharedBlock.setVisibility(View.VISIBLE);
        }
    }

    //----------------------------SHARED...BLOCK...FUNCTIONALITY---------------------------------------------------

    /**
     * Saves value for multiple data and refreshes the list
     *
     * @param data
     */
    public void saveMultipleData(Bundle data) {
        Item items = data.getParcelable(KEYS.MultipleTextData.KEY_ITEM);
        String value = data.getString(KEYS.MultipleTextData.KEY_VALUE);
        int conf = data.getInt(KEYS.MultipleTextData.KEY_CONF);
        int childIndex = data.getInt(KEYS.MultipleTextData.KEY_CHILD_INDEX);
        int groupIndex = data.getInt(KEYS.MultipleTextData.KEY_GROUP_INDEX);

        dataAccessOperator.updateValue(value,
                items.getComItem(),
                items.getIdItem(), idIntervention,
                conf, items.getFlReserve(), items.getNomItem(), items.getIteration());

        refreshItem(groupIndex, childIndex, 0);
    }

    public void openCameraAfterPermissionGranted() {
        AttachmentDialogNew attachmentDialog = new AttachmentDialogNew(
                jobDetails, reportsJobDetailFragment,
                ReportsJobDetailFragmentHelperNew.this);
//        attachmentDialog.prendrePhoto();
        attachmentDialog.show();
    }


    /**
     * The Class SaveNewJobDataAsyncTask.
     */
    private class AttachReportAsyncTask extends
            AsyncTaskCoroutine<String, Boolean> {

        ReportsJobDetailFragmentHelperNew reportsJobDetailFragmentHelper;
        int groupPos = 0;
        boolean refresh = false;

        public AttachReportAsyncTask(ReportsJobDetailFragmentHelperNew
                                             reportsJobDetailFragmentHelper) {
            this.reportsJobDetailFragmentHelper = reportsJobDetailFragmentHelper;
        }

        public AttachReportAsyncTask(ReportsJobDetailFragmentHelperNew reportsJobDetailFragmentHelper,
                                     int groupPosition, boolean refresh) {
            this.reportsJobDetailFragmentHelper = reportsJobDetailFragmentHelper;
            this.groupPos = groupPosition;
            this.refresh = refresh;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        public void onPreExecute() {
            super.onPreExecute();

            DialogUtils
                    .showProgressDialog(jobDetails, jobDetails
                            .getString(R.string.textWaitLable), jobDetails
                            .getString(R.string.chargement), false);


            if (listItem == null) {

                listItem = new ArrayList<Families>();
            } else {
//                listItem.clear();
            }

            listItem = new ArrayList<Families>();
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        public Boolean doInBackground(String... params) {

            Vector<Families> vect = dataAccessOperator.getSBCategory(idIntervention);

            Logger.log("TAG", "VIEW CHANGE ITEM COUNT===>" + vect.size());

            Enumeration<Families> en = vect.elements();

            while (en.hasMoreElements()) {
                Families fm = en.nextElement();

                HashMap<Integer, Item> items = dataAccessOperator.getAllItem(
                        idIntervention, fm.getIdFamily(), fm.getIteration());


                Vector<Item> vectorItem = cleanListItem(items);

                fm.setItems(vectorItem);

                if (fm.getIsSharedBlock() == 1) {
                    hadSharedBlock = true;
                }

                listItem.add(fm);
            }


            return true;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        public void onPostExecute(Boolean result) {
            super.onPostExecute(result);


            reportItemCount = listItem.size();

            if (reportsExpandableListAdapter == null) {

                if (listItem != null) {
                    reportsExpandableListAdapter = new ReportsExpandableListAdapterNew(
                            jobDetails, listItem, dataAccessOperator, id_user, idModel,
                            cd_statut, idIntervention, reportsJobDetailFragmentHelper,
                            reportsJobDetailFragment,
                            deviceWidth);
                }

//                reportsExpandableListAdapter = new ReportsExpandableListAdapterNew(
//                        jobDetails, listItem, dataAccessOperator, id_user, idModel,
//                        cd_statut, idIntervention, reportsJobDetailFragmentHelper,
//                        reportsJobDetailFragment,
//                        deviceWidth);

                expandableListView.setAdapter(reportsExpandableListAdapter);

                if (reportsExpandableListAdapter != null) {
                    reportsExpandableListAdapter.setIndexPosition(index);
                }

                try {
                    Thread.sleep(900);
                } catch (Exception e) {
                    Logger.printException(e);
                }

            } else {
                if (listItem != null) {
                    reportsExpandableListAdapter = new ReportsExpandableListAdapterNew(
                            jobDetails, listItem, dataAccessOperator, id_user, idModel,
                            cd_statut, idIntervention, reportsJobDetailFragmentHelper,
                            reportsJobDetailFragment,
                            deviceWidth);
                }

//                reportsExpandableListAdapter = new ReportsExpandableListAdapterNew(
//                        jobDetails, listItem, dataAccessOperator, id_user, idModel,
//                        cd_statut, idIntervention, reportsJobDetailFragmentHelper,
//                        reportsJobDetailFragment,
//                        deviceWidth);
                if (reportsExpandableListAdapter != null)
                    expandableListView.setAdapter(reportsExpandableListAdapter);
                if (refresh && groupPos < listItem.size() - 1)
                    expandableListView.setSelectedGroup(groupPos);

                if (reportsExpandableListAdapter != null) {
                    try {
                        jobDetails.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                reportsExpandableListAdapter.setIndexPosition(index);
                                reportsExpandableListAdapter.notifyDataSetChanged();
                            }
                        });
                    } catch (Exception e) {
                    }

                    reportsExpandableListAdapter.notifyDataSetChanged();
                }

            }

            if (reportItemCount > 15)
                expandableListView.addFooterView(footerViewNew);


            DialogUtils.dismissProgressDialog();

            updateScrollViewList();
        }
    }

    private void updateScrollViewList() {
        expandableListView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view,
                                             int scrollState) {


                if (footerViewNew != null && footerViewNew.isShown()) {
                    index++;
                    if (reportsExpandableListAdapter != null) {
                        reportsExpandableListAdapter.setIndexPosition(index);

                        try {
                            Thread.sleep(900);
                        } catch (InterruptedException e) {
                            Logger.printException(e);
                        }
                        reportsExpandableListAdapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onScroll(AbsListView arg0, int arg1, int arg2,
                                 int arg3) {
                if (reportsExpandableListAdapter != null) {
                    int ctadapter = reportsExpandableListAdapter.getGroupCount();

                    if (ctadapter >= reportItemCount) {
//                    progressBar.setVisibility(View.GONE);
                        hideFooterViewNew();
                    } else {
//                    progressBar.setVisibility(View.VISIBLE);
                        showFooterViewNew();
                    }
                }
            }
        });
    }

    public void hideFooterViewNew() {
        if (footerViewNew != null) {
            if (footerViewNew.getVisibility() == View.VISIBLE) {
                footerViewNew.setVisibility(View.INVISIBLE);
            }
        }

    }

    public void showFooterViewNew() {
        if (footerViewNew != null) {
            if (footerViewNew.getVisibility() == View.INVISIBLE) {
                footerViewNew.setVisibility(View.VISIBLE);
            }
        }
    }
}