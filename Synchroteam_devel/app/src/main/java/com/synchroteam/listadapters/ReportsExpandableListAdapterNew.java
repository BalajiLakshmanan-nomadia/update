package com.synchroteam.listadapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.SwipeLayout.DragEdge;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.synchroteam.HourMinuteCustomView.HourMinSelectDialog;
import com.synchroteam.TypefaceLibrary.Button;
import com.synchroteam.TypefaceLibrary.CheckBox;
import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.RadioButton;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.Families;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.beans.Item;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.AttachmentDialogNew;
import com.synchroteam.dialogs.EnterCommentDialog;
import com.synchroteam.dialogs.EnterNumericDataDialog;
import com.synchroteam.dialogs.ReportMultipleTextiItemDialog;
import com.synchroteam.dialogs.ReportTextItemDialog;
import com.synchroteam.dialogs.SelectValueFromListDialog;
import com.synchroteam.events.ReportViewUpdateEvent;
import com.synchroteam.fragment.ReportsJobDetailFragment;
import com.synchroteam.fragmenthelper.ReportsJobDetailFragmentHelperNew;
import com.synchroteam.scanner.CodeScannerActivity;
import com.synchroteam.synchroteam.EditReportImage;
import com.synchroteam.synchroteam.ReportsMultiSelectDialogNew;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.technicalsupport.JobDetails;
import com.synchroteam.utils.DateFormatUtils;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.RequestCode;
import com.synchroteam.utils.ScannerBar;
import com.synchroteam.utils.SynchroteamUitls;
import com.synchroteam.utils.TouchImageView;

import java.io.IOException;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Timer;
import java.util.Vector;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import de.greenrobot.event.EventBus;

/**
 * The Class ReportsExpandableListAdapter. author Ideavate.solution
 */
@SuppressLint("SimpleDateFormat")
public class ReportsExpandableListAdapterNew extends BaseExpandableListAdapter
        implements ConnectionCallbacks, OnConnectionFailedListener {

    /**
     * The job details.
     */
    private JobDetails jobDetails;

    /**
     * The familles.
     */
    private ArrayList<Families> familles;

    /**
     * The data access object.
     */
    private Dao dataAccessObject;

    /**
     * The gt.
     */
    private GestionAcces gt;

    /**
     * The id user.
     */
    private int idUser;

    /**
     * id model report
     */
    private int idReportModel;

    /**
     * The cd_statut.
     */
    private int cd_statut;

    /**
     * The id interv.
     */
    private String idInterv;

    /**
     * The reports job detail fragment helper.
     */
    private ReportsJobDetailFragmentHelperNew reportsJobDetailFragmentHelper;

    /**
     * The french time format.
     */
    private SimpleDateFormat frenchDateFormat, frenchTimeFormat;

    /**
     * The reports job detail fragment.
     */
    private ReportsJobDetailFragment reportsJobDetailFragment;

    /**
     * The opt.
     */
    private BitmapFactory.Options opt;

    /**
     * The image resources.
     */
    private int imageResources[] = {R.drawable.nostatus, R.drawable.resolved,

            R.drawable.complaint_noright, R.drawable.complaint};

    private EnterNumericDataDialog enterDataDialog;

    private static final String TAG = "ReportsExpandableListAdapter";


    private GoogleApiClient locationClient;
    private LocationRequest mLocationRequest;
    protected LocationManager locationManager;
    private Timer myTimer;
    int ids = 0;
    /**
     * The rue.
     */
    private String rue = "";

    /**
     * The ville.
     */
    private String ville = "";

    /**
     * The Gps x.
     */
    private String GpsX = "";

    /**
     * The Gps y.
     */
    private String GpsY = "";

    private Item items;
    int grpIndex = 0;
    int chdIndex = 0;

    /**
     * The index.
     */
    private int index;

    /**
     * The base count.
     */
    private int baseCount = 15;

    private long mStartBtnClickTime;

    /**
     * Instantiates a new reports expandable list adapter.
     *
     * @param jobDetails                     the job details
     * @param familles                       the familles
     * @param dataAccesssObject              the data accesss object
     * @param userId                         the user id
     * @param cd_status                      the cd_status
     * @param idInterv                       the id interv
     * @param reportsJobDetailFragmentHelper the reports job detail fragment helper
     * @param reportsJobDetailFragment       the reports job detail fragment
     * @param deviceWidth                    the device width
     */

    @SuppressLint("SimpleDateFormat")
    public ReportsExpandableListAdapterNew(JobDetails jobDetails,
                                           ArrayList<Families> familles, Dao dataAccesssObject, int userId, int idReportModel,
                                           int cd_status, String idInterv,
                                           ReportsJobDetailFragmentHelperNew reportsJobDetailFragmentHelper,
                                           ReportsJobDetailFragment reportsJobDetailFragment, int deviceWidth) {
        this.jobDetails = jobDetails;
        this.familles = familles;
        this.dataAccessObject = dataAccesssObject;
        this.gt = dataAccesssObject.getAcces();

        this.idUser = userId;
        this.idReportModel = idReportModel;
        this.cd_statut = cd_status;
        this.idInterv = idInterv;
        this.reportsJobDetailFragmentHelper = reportsJobDetailFragmentHelper;
        this.reportsJobDetailFragment = reportsJobDetailFragment;

        frenchDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        frenchTimeFormat = new SimpleDateFormat("HH:mm");
        opt = new BitmapFactory.Options();
        opt.inDither = true;
        opt.inPreferredConfig = Bitmap.Config.RGB_565;

        // locationClient = new LocationClient(jobDetails, this, this);
        locationClient = new GoogleApiClient.Builder(jobDetails)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mLocationRequest = LocationRequest.create();
        locationClient.connect();

        /*
         * Set the update interval
         */
        mLocationRequest
                .setInterval(SynchroteamUitls.UPDATE_INTERVAL_IN_MILLISECONDS);

        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
        mLocationRequest
                .setFastestInterval(SynchroteamUitls.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

        // Drawable
        // drawable=jobDetails.getResources().getDrawable(R.drawable.default_image);

//        DialogUtils
//                .showProgressDialog(jobDetails, jobDetails
//                        .getString(R.string.textWaitLable), jobDetails
//                        .getString(R.string.chargement), false);

    }


    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

//         Item items = (Item)getChild(groupPosition, childPosition);
        Item items = ((Item) getChild(groupPosition, childPosition));

        LayoutInflater layoutInflater = (LayoutInflater) jobDetails
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = convertView;

        Typeface fontAwesome = Typeface.createFromAsset(jobDetails.getAssets(),
                jobDetails.getString(R.string.fontName_fontAwesome));

        if (items != null && items.getIdTypeItem() >= 0) {

            Logger.log("getChildView", "comment " + items.getComItem());
            items.setGroupPosition(groupPosition);
            final int groupIndex = groupPosition;
            final int childIndex = childPosition;

            // if (gt.getFlCommentsRapport() == 0 && user.getId() == idUser) {

            int itemType = items.getIdTypeItem();

            if (itemType == 0) {

                view = layoutInflater.inflate(R.layout.layout_datatype_conform, parent, false);
                android.widget.TextView txtImageAvail = (android.widget.TextView) view
                        .findViewById(R.id.txtImageAvail);
                View lineImageAvail = (View) view.findViewById(R.id.viewImageAvail);

                txtImageAvail.setTypeface(fontAwesome);
                final byte[] photo = items.getImage();

                // ....................SWIPE...ACTION...STARTS..............................
                SwipeLayout swipeLayout = (SwipeLayout) view
                        .findViewById(R.id.swipeLayoutDataConform);

                // set show mode.
                swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

                if ((photo != null) && (photo.length != 0)) {
                    // swipeLayout.setBottomViewIds(R.id.bottom_wrapper_left,
                    // R.id.bottom_wrapper_right, 0, 0);
//				swipeLayout.setDragEdge(DragEdge.Left);
//				swipeLayout.setDragEdge(DragEdge.Right);

                    swipeLayout.addDrag(DragEdge.Left, swipeLayout.findViewById(R.id.bottom_wrapper_left));
                    swipeLayout.addDrag(DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_right));

                    txtImageAvail.setVisibility(View.VISIBLE);
                    lineImageAvail.setVisibility(View.VISIBLE);
                } else {
                    // swipeLayout
                    // // .setBottomViewIds(0, R.id.bottom_wrapper_right, 0, 0);
//				swipeLayout.setDragEdge(DragEdge.Right);

                    swipeLayout.addDrag(DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_right));

                    txtImageAvail.setVisibility(View.GONE);
                    lineImageAvail.setVisibility(View.GONE);

                }

                // ***********************SWIPE...ACTION...ENDS******************************
                final TextView reportsConformationItemName = (TextView) view
                        .findViewById(R.id.fieldName);
                final android.widget.TextView txtExpand = (android.widget.TextView) view
                        .findViewById(R.id.txtExpand);
                Typeface typeface = Typeface.createFromAsset(
                        jobDetails.getAssets(), jobDetails.getResources()
                                .getString(R.string.fontName_fontAwesome));

                reportsConformationItemName.setText(items.getNomItem());

                // -------------------NEW CHANGES--------------

                txtExpand.setTypeface(typeface);

                Handler handler = new Handler();
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (reportsConformationItemName.getLineCount() > 1) {
                            txtExpand.setVisibility(View.VISIBLE);
                            reportsConformationItemName.setSingleLine(true);
                        } else {
                            txtExpand.setVisibility(View.GONE);
                        }
                    }
                });

                txtExpand.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (reportsConformationItemName.getLineCount() > 1) {
                            reportsConformationItemName.setSingleLine(true);
                            txtExpand.setText(jobDetails
                                    .getString(R.string.fa_expand));
                        } else {
                            reportsConformationItemName.setSingleLine(false);
                            txtExpand.setText(jobDetails
                                    .getString(R.string.fa_compress));
                        }
                    }
                });

                // -------------------NEW CHANGES--------------

                final RadioGroup radioGroupCompliance = (RadioGroup) view
                        .findViewById(R.id.dataConfirm);
                final RadioButton radioBtnNotVerified = (RadioButton) view
                        .findViewById(R.id.radioBtnNotVerified);
                final RadioButton radioBtnCompliant = (RadioButton) view
                        .findViewById(R.id.radioBtnCompliant);
                final RadioButton radioBtnNotCompliant = (RadioButton) view
                        .findViewById(R.id.radioBtnNotCompliant);

                final int conf = 0;
                final String code;
                String valItem = getValueFormat(items.getValeurNet(),
                        items.getIdTypeItem());
                if (items.getOblig() != 0) {

                    if ((valItem.equals(jobDetails
                            .getString(R.string.non_renseigne)))
                            || (valItem.equalsIgnoreCase("0"))) {

                        reportsConformationItemName
                                .setTextColor(jobDetails
                                        .getResources()
                                        .getColor(
                                                R.color.textManadatoryFieldsReportsJobDetail));

                    } else {
                        reportsConformationItemName.setTextColor(jobDetails
                                .getResources().getColor(
                                        R.color.text_category_title_color));
                    }
                }

                if (valItem.equals(jobDetails
                        .getString(R.string.textComplaintLable))) {
                    radioBtnCompliant.setChecked(true);
                    radioBtnNotCompliant.setChecked(false);
                    radioBtnNotVerified.setChecked(false);
                    code = "1";
                } else if (valItem.equals(jobDetails
                        .getString(R.string.textNotComplaintLable))) {
                    radioBtnNotCompliant.setChecked(true);
                    radioBtnCompliant.setChecked(false);
                    radioBtnNotVerified.setChecked(false);
                    code = "2";
                } else {
                    radioBtnNotVerified.setChecked(true);
                    radioBtnNotCompliant.setChecked(false);
                    radioBtnCompliant.setChecked(false);
                    code = "0";
                }
                if (jobDetails.getUpdatedValueOfStatus() != KEYS.JObDetail.JOB__STARTED) {
                    if (radioGroupCompliance != null) {
                        radioBtnCompliant.setEnabled(false);
                        radioBtnNotCompliant.setEnabled(false);
                        radioBtnNotVerified.setEnabled(false);
                    }

                } else {
                    if (radioGroupCompliance != null) {
                        radioBtnCompliant.setEnabled(true);
                        radioBtnNotCompliant.setEnabled(true);
                        radioBtnNotVerified.setEnabled(true);
                    }

                }

                assert radioGroupCompliance != null;
                radioGroupCompliance
                        .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                            @Override
                            public void onCheckedChanged(RadioGroup group,
                                                         int checkedId) {
                                String val = "";
                                if (checkedId == R.id.radioBtnCompliant) {
                                    val = "1";
                                } else if (checkedId == R.id.radioBtnNotCompliant) {
                                    val = "2";
                                }

                                if (!TextUtils.isEmpty(code)) {
                                    if (!code.equals(val)) {
                                        dataAccessObject.updateValue(val,
                                                items.getComItem(),
                                                items.getIdItem(), idInterv, conf,
                                                items.getFlReserve(), items.getNomItem(), items.getIteration());
                                        items.setValeurNet(val);
                                        reportsJobDetailFragmentHelper
                                                .refreshItem(groupIndex, childIndex, 0);

                                    }
                                }
                            }
                        });

                ImageView imgph = (ImageView) view.findViewById(R.id.itemImage);

                if ((photo != null) && (photo.length != 0)) {

//                    final Bitmap btImag = BitmapFactory.decodeByteArray(photo, 0,
//                            photo.length, opt);
//
//                    // BitmapDrawable bmd = new BitmapDrawable(resiseBitmap(imgph,
//                    // btImag));
//                    imgph.setImageBitmap(btImag);

                    //new changes
                    Glide.with(jobDetails)
                            .load(photo)
                            .asBitmap()
                            .override(200, 200)
                            .fitCenter()
                            .placeholder(R.drawable.library_iicon)
                            .into(imgph);


                    imgph.setVisibility(View.VISIBLE);

                    imgph.setOnClickListener(new OnClickListener() {
                        public void onClick(final View v) {

                            DisplayMetrics displaymetrics = new DisplayMetrics();
                            jobDetails.getWindowManager().getDefaultDisplay()
                                    .getMetrics(displaymetrics);

                            // Bitmap bm2 = dataAccessObject.resizeBitmap(btImag ,
                            // displaymetrics.widthPixels ,
                            // displaymetrics.heightPixels);

//                            Bitmap resized = Bitmap.createScaledBitmap(btImag,
//                                    displaymetrics.widthPixels,
//                                    displaymetrics.heightPixels, true);
//                            showImg(v, btImag);

                            //new changes
                            Glide.with(jobDetails)
                                    .load(photo)
                                    .asBitmap()
                                    .fitCenter()
                                    .placeholder(R.drawable.library_iicon)
                                    .into(new SimpleTarget<Bitmap>(displaymetrics.widthPixels, displaymetrics.heightPixels) {
                                        @Override
                                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                            showImg(v, resource);
                                        }
                                    });


                        }
                    });

                } else {
                    imgph.setVisibility(View.GONE);
                }

                android.widget.TextView commentIv = (android.widget.TextView) view
                        .findViewById(R.id.commentIv);
                commentIv.setTypeface(fontAwesome);
                if (gt != null && gt.getFlCommentsRapport() == 0) {
                    commentIv.setVisibility(View.GONE);

                }

                final boolean isWriteable = jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED;

                commentIv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        EnterCommentDialog enterCommentDialog = new EnterCommentDialog(
                                jobDetails,
                                new EnterCommentDialog.EnterDialogInterface() {

                                    @Override
                                    public void doOnModifyClick(String data) {

                                        String valItem = getValueFormat(
                                                items.getValeurNet(),
                                                items.getIdTypeItem());
                                        String code;
                                        if (valItem.equals(jobDetails
                                                .getString(R.string.textComplaintLable))) {

                                            code = "1";
                                        } else if (valItem.equals(jobDetails
                                                .getString(R.string.textNotComplaintLable))) {

                                            code = "2";
                                        } else {

                                            code = "0";
                                        }

                                        dataAccessObject.updateValue(code, data,
                                                items.getIdItem(), idInterv, conf,
                                                items.getFlReserve(), items.getNomItem(), items.getIteration());
                                        reportsJobDetailFragmentHelper
                                                .refreshItem(groupIndex, childIndex, 0);
                                    }

                                    @Override
                                    public void doOnCancelClick() {
                                        hideKeyboard();
                                    }
                                }, jobDetails.getString(R.string.enterComment).toUpperCase(),
                                items.getComItem(), isWriteable);

                        enterCommentDialog.show();

                    }
                });

                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED || jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__COMPLETE) {
                    if (commentIv != null) {
                        commentIv.setEnabled(true);
                    }
                } else {
                    if (commentIv != null) {
                        commentIv.setEnabled(false);
                    }
                }

                int flReserve = items.getFlReserve();
                /*
                 * New Changes.....
                 *
                 *
                 * Separate buttons for Status (Complaint, Resolved & Unresolved).
                 * If a status is selected, update the current flag value to DB. If
                 * a status is unselected (ie., Same status is again clicked),
                 * change the flag value as 0 in DB.
                 *
                 * And if the status is changed, change the color of the line strip
                 * in left of sub-category's layout. For Compliant - Orange,
                 * Resolved - Green, Unresolved - Red, if none is selected then
                 * change the color as White
                 */
                final LinearLayout linCompliant = (LinearLayout) view
                        .findViewById(R.id.linCompliantContainer);
                final LinearLayout linResolved = (LinearLayout) view
                        .findViewById(R.id.linResolvedContainer);
                final LinearLayout linUnresolved = (LinearLayout) view
                        .findViewById(R.id.linUnresolvedContainer);

                // set the boxframe for child
                LinearLayout parentLayout = (LinearLayout) view
                        .findViewById(R.id.ParentLayoutReport);

                if (flReserve == 1) {
                    linCompliant
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_compliant_status);
                } else if (flReserve == 2) {
                    linUnresolved
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_unresolved_status);
                } else if (flReserve == 3) {
                    linResolved
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_resolved_status);
                }
                final int i = flReserve;

                // ........................LISTENER...FOR...STATUS...BUTTONS...STARTS...HERE............................
                linCompliant.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (i != 1) {

                            String valItem = getValueFormat(items.getValeurNet(),
                                    items.getIdTypeItem());
                            String code;
                            if (valItem.equals(jobDetails
                                    .getString(R.string.textComplaintLable))) {

                                code = "1";
                            } else if (valItem.equals(jobDetails
                                    .getString(R.string.textNotComplaintLable))) {

                                code = "2";
                            } else {

                                code = "0";
                            }
                            dataAccessObject.updateValue(code, items.getComItem(),
                                    items.getIdItem(), idInterv, conf, 1, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);

                        } else {

                            String valItem = getValueFormat(items.getValeurNet(),
                                    items.getIdTypeItem());
                            String code;
                            if (valItem.equals(jobDetails
                                    .getString(R.string.textComplaintLable))) {

                                code = "1";
                            } else if (valItem.equals(jobDetails
                                    .getString(R.string.textNotComplaintLable))) {

                                code = "2";
                            } else {

                                code = "0";
                            }

                            dataAccessObject.updateValue(code, items.getComItem(),
                                    items.getIdItem(), idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }

                    }
                });
                linResolved.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (i != 3) {

                            String valItem = getValueFormat(items.getValeurNet(),
                                    items.getIdTypeItem());
                            String code;
                            if (valItem.equals(jobDetails
                                    .getString(R.string.textComplaintLable))) {

                                code = "1";
                            } else if (valItem.equals(jobDetails
                                    .getString(R.string.textNotComplaintLable))) {

                                code = "2";
                            } else {

                                code = "0";
                            }

                            dataAccessObject.updateValue(code, items.getComItem(),
                                    items.getIdItem(), idInterv, conf, 3, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        } else {

                            String valItem = getValueFormat(items.getValeurNet(),
                                    items.getIdTypeItem());
                            String code;
                            if (valItem.equals(jobDetails
                                    .getString(R.string.textComplaintLable))) {

                                code = "1";
                            } else if (valItem.equals(jobDetails
                                    .getString(R.string.textNotComplaintLable))) {

                                code = "2";
                            } else {

                                code = "0";
                            }

                            dataAccessObject.updateValue(code, items.getComItem(),
                                    items.getIdItem(), idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }
                    }
                });
                linUnresolved.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (i != 2) {

                            String valItem = getValueFormat(items.getValeurNet(),
                                    items.getIdTypeItem());
                            String code;
                            if (valItem.equals(jobDetails
                                    .getString(R.string.textComplaintLable))) {

                                code = "1";
                            } else if (valItem.equals(jobDetails
                                    .getString(R.string.textNotComplaintLable))) {

                                code = "2";
                            } else {

                                code = "0";
                            }

                            dataAccessObject.updateValue(code, items.getComItem(),
                                    items.getIdItem(), idInterv, conf, 2, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        } else {

                            String valItem = getValueFormat(items.getValeurNet(),
                                    items.getIdTypeItem());
                            String code;
                            if (valItem.equals(jobDetails
                                    .getString(R.string.textComplaintLable))) {

                                code = "1";
                            } else if (valItem.equals(jobDetails
                                    .getString(R.string.textNotComplaintLable))) {

                                code = "2";
                            } else {

                                code = "0";
                            }

                            dataAccessObject.updateValue(code, items.getComItem(),
                                    items.getIdItem(), idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }
                    }
                });

                // ........................LISTENER...FOR...STATUS...BUTTONS...ENDS...HERE............................

                if (jobDetails.getUpdatedValueOfStatus() != KEYS.JObDetail.JOB__STARTED) {
                    linCompliant.setEnabled(false);
                    linResolved.setEnabled(false);
                    linUnresolved.setEnabled(false);

                } else {
                    linCompliant.setEnabled(true);
                    linResolved.setEnabled(true);
                    linUnresolved.setEnabled(true);
                }

                // show the view as divider, if it is a last child
                if (isLastChild) {
                    View dividerLine = (View) view.findViewById(R.id.dividerView);
                    dividerLine.setVisibility(View.VISIBLE);
                }

            } else if (itemType == 2) {

                view = layoutInflater.inflate(R.layout.layout_datatpe_note_kitkat,
                        null);
//            view = layoutInflater.inflate(R.layout.layout_datatype_note_new,
//                    null);

                android.widget.TextView txtImageAvail = (android.widget.TextView) view
                        .findViewById(R.id.txtImageAvail);
                android.widget.TextView txtBarcodeIcon = (android.widget.TextView) view
                        .findViewById(R.id.txtBarcodeIconText);
                View lineImageAvail = (View) view.findViewById(R.id.viewImageAvail);

                txtImageAvail.setTypeface(fontAwesome);
                txtBarcodeIcon.setTypeface(fontAwesome);
                final byte[] photo = items.getImage();
                // ....................SWIPE...ACTION...STARTS..............................
                SwipeLayout swipeLayout = (SwipeLayout) view
                        .findViewById(R.id.swipeLayoutDataConform);

                // set show mode.
                swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

                // set drag edge.
                // swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
                if ((photo != null) && (photo.length != 0)) {
                    // swipeLayout.setBottomViewIds(R.id.bottom_wrapper_left,
                    // R.id.bottom_wrapper_right, 0, 0);
//				swipeLayout.setDragEdge(DragEdge.Left);
//				swipeLayout.setDragEdge(DragEdge.Right);

                    swipeLayout.addDrag(DragEdge.Left, swipeLayout.findViewById(R.id.bottom_wrapper_left));
                    swipeLayout.addDrag(DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_right));

                    txtImageAvail.setVisibility(View.VISIBLE);
                    lineImageAvail.setVisibility(View.VISIBLE);
                } else {
                    // swipeLayout
                    // .setBottomViewIds(0, R.id.bottom_wrapper_right, 0, 0);
//				swipeLayout.setDragEdge(DragEdge.Right);

                    swipeLayout.addDrag(DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_right));

                    txtImageAvail.setVisibility(View.GONE);
                    lineImageAvail.setVisibility(View.GONE);

                }

                // ***********************SWIPE...ACTION...ENDS******************************

                // ........................LISTENER...FOR...BARCODE...ICON...STARTS...........................
                txtBarcodeIcon.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        reportsJobDetailFragmentHelper
                                .setChildIndexSend(childIndex);
                        reportsJobDetailFragmentHelper
                                .setGroupIndexSend(groupIndex);
                        Intent it = new Intent(jobDetails, CodeScannerActivity.class);
                        reportsJobDetailFragment.startActivityForResult(it,
                                RequestCode.REQUEST_CODE_TEXT_BARCODE);
                    }
                });

                // ........................LISTENER...FOR...BARCODE...ICON...ENDS...........................

                final TextView reportsTextFiledItemName = (TextView) view
                        .findViewById(R.id.fieldName);

                final TextView noteData = (TextView) view
                        .findViewById(R.id.fieldDataKitKat);
//            final EditText noteData = (EditText) view
//                    .findViewById(R.id.fieldDataKitKat);

                noteData.setText(getValueFormat(items.getValeurNet(),
                        items.getIdTypeItem()));
                final int conf = 0;
                reportsTextFiledItemName.setText(items.getNomItem());

                final android.widget.TextView txtExpand = (android.widget.TextView) view
                        .findViewById(R.id.txtExpand);
                Typeface typeface = Typeface.createFromAsset(
                        jobDetails.getAssets(), jobDetails.getResources()
                                .getString(R.string.fontName_fontAwesome));

                // -------------------NEW CHANGES--------------

                txtExpand.setTypeface(typeface);

                Handler handler = new Handler();
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (reportsTextFiledItemName.getLineCount() > 1) {
                            txtExpand.setVisibility(View.VISIBLE);
                            reportsTextFiledItemName.setSingleLine(true);
                        } else {
                            txtExpand.setVisibility(View.GONE);
                        }
                    }
                });

                txtExpand.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (reportsTextFiledItemName.getLineCount() > 1) {
                            reportsTextFiledItemName.setSingleLine(true);
                            txtExpand.setText(jobDetails
                                    .getString(R.string.fa_expand));
                        } else {
                            reportsTextFiledItemName.setSingleLine(false);
                            txtExpand.setText(jobDetails
                                    .getString(R.string.fa_compress));
                        }
                    }
                });

                swipeLayout.getSurfaceView().setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        noteData.performClick();
                    }
                });

                // -------------------NEW CHANGES--------------

                // ** NEW CHANGES **
                /*
                 * If the text label has a pipe symbol(|), then set the visibility
                 * of the barcode icone to GONE. Because it contains two or more
                 * inputs.
                 */
                if (items.getNomItem().contains("|"))
                    txtBarcodeIcon.setVisibility(View.GONE);
                // ** NEW CHANGES **

                if (items.getOblig() != 0) {

                    if (!TextUtils.isEmpty(noteData.getText().toString())) {

                        reportsTextFiledItemName.setTextColor(jobDetails
                                .getResources().getColor(
                                        R.color.text_category_title_color));
                    } else {
                        reportsTextFiledItemName
                                .setTextColor(jobDetails
                                        .getResources()
                                        .getColor(
                                                R.color.textManadatoryFieldsReportsJobDetail));
                    }
                }

                if (jobDetails.getUpdatedValueOfStatus() != KEYS.JObDetail.JOB__STARTED) {
                    if (noteData != null) {
                        noteData.setEnabled(false);
                        txtBarcodeIcon.setEnabled(false);

                    }
                    view.setEnabled(false);
                } else {
                    if (noteData != null) {
                        noteData.setEnabled(true);
                        txtBarcodeIcon.setEnabled(true);

                    }
                    view.setEnabled(true);
                }

                noteData.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        String[] temp = items.getNomItem().split("\\|");
                        String categoryName = ((Families) getGroup(groupIndex))
                                .getNameFamily();
                        if (temp.length > 1) {

                            //New changes
//                        FragmentManager fm = reportsJobDetailFragment.getFragmentManager();
//                        EnterMultipleDataDialogNew dialogNew = EnterMultipleDataDialogNew.newInstance(items, conf, groupIndex, childIndex, categoryName);
//                        dialogNew.setTargetFragment(reportsJobDetailFragment, 100);
//                        dialogNew.show(fm, "frag_edit");

                            FragmentManager fm = reportsJobDetailFragment.getFragmentManager();
                            ReportMultipleTextiItemDialog dialog = ReportMultipleTextiItemDialog.newInstance(items, conf, groupIndex, childIndex, categoryName);
                            dialog.setTargetFragment(reportsJobDetailFragment, 100);
                            dialog.setCancelable(false);
                            assert fm != null;
                            FragmentTransaction transaction = fm.beginTransaction();
                            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            transaction.add(android.R.id.content, dialog, "frag_edit").addToBackStack(null).commit();

                            jobDetails.isTextDialogOpened = true;

                        } else {
//                        EnterDataDialog enterDataDialog = new EnterDataDialog(
//                                jobDetails, new EnterDialogInterface() {
//
//                            @Override
//                            public void doOnModifyClick(String data) {
//                                Logger.log("ExpandableListViewAdapter",
//                                        "Called From type 2");
//
//                                dataAccessObject.updateValue(data,
//                                        items.getComItem(),
//                                        items.getIdItem(), idInterv,
//                                        conf, items.getFlReserve(), items.getNomItem(), items.getIteration());
//
//                                reportsJobDetailFragmentHelper
//                                        .refreshItem(groupIndex, childIndex, 0);
//                            }
//
//                            @Override
//                            public void doOnCancelClick() {
//
//                            }
//                        }, reportsTextFiledItemName.getText()
//                                .toString(), noteData.getText()
//                                .toString());
//
//                        enterDataDialog.show();

                            //new changes
                            FragmentManager fm = reportsJobDetailFragment.getFragmentManager();
                            ReportTextItemDialog dialog = ReportTextItemDialog.getInstance(categoryName, reportsTextFiledItemName.getText()
                                    .toString(), noteData.getText()
                                    .toString(), new ReportTextItemDialog.TextItemListener() {
                                @Override
                                public void onSave(String data) {
                                    dataAccessObject.updateValue(data,
                                            items.getComItem(),
                                            items.getIdItem(), idInterv,
                                            conf, items.getFlReserve(), items.getNomItem(), items.getIteration());

                                    reportsJobDetailFragmentHelper
                                            .refreshItem(groupIndex, childIndex, 0);
                                }
                            });

                            dialog.setCancelable(false);
                            assert fm != null;
                            FragmentTransaction transaction = fm.beginTransaction();
                            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            transaction.add(android.R.id.content, dialog).addToBackStack(null).commit();
                            jobDetails.isTextDialogOpened = true;
                        }

                    }
                });

                ImageView imgph = (ImageView) view.findViewById(R.id.itemImage);
                if ((photo != null) && (photo.length != 0)) {

//                    final Bitmap btImag = BitmapFactory.decodeByteArray(photo, 0,
//                            photo.length, opt);
//
//                    // BitmapDrawable bmd = new BitmapDrawable(resiseBitmap(imgph,
//                    // btImag));
//                    imgph.setImageBitmap(btImag);

                    //new changes
                    Glide.with(jobDetails)
                            .load(photo)
                            .asBitmap()
                            .override(200, 200)
                            .fitCenter()
                            .placeholder(R.drawable.library_iicon)
                            .into(imgph);

                    imgph.setVisibility(View.VISIBLE);

                    imgph.setOnClickListener(new OnClickListener() {
                        public void onClick(final View v) {

                            DisplayMetrics displaymetrics = new DisplayMetrics();
                            jobDetails.getWindowManager().getDefaultDisplay()
                                    .getMetrics(displaymetrics);

                            // Bitmap bm2 = dataAccessObject.resizeBitmap(btImag ,
                            // displaymetrics.widthPixels ,
                            // displaymetrics.heightPixels);

//                            Bitmap resized = Bitmap.createScaledBitmap(btImag,
//                                    displaymetrics.widthPixels,
//                                    displaymetrics.heightPixels, true);
//
//                            showImg(v, btImag);

                            Glide.with(jobDetails)
                                    .load(photo)
                                    .asBitmap()
                                    .fitCenter()
                                    .placeholder(R.drawable.library_iicon)
                                    .into(new SimpleTarget<Bitmap>(displaymetrics.widthPixels, displaymetrics.heightPixels) {
                                        @Override
                                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                            showImg(v, resource);
                                        }
                                    });
                        }
                    });

                } else {
                    imgph.setVisibility(View.GONE);
                }

                android.widget.TextView commentIv = (android.widget.TextView) view
                        .findViewById(R.id.commentIv);
                commentIv.setTypeface(fontAwesome);
                if (gt != null && gt.getFlCommentsRapport() == 0) {
                    commentIv.setVisibility(View.GONE);

                }

                final boolean isWriteable = jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED;

                commentIv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        EnterCommentDialog enterCommentDialog = new EnterCommentDialog(
                                jobDetails,
                                new EnterCommentDialog.EnterDialogInterface() {

                                    @Override
                                    public void doOnModifyClick(String data) {
                                        dataAccessObject.updateValue(
                                                getValueFormat(
                                                        items.getValeurNet(),
                                                        items.getIdTypeItem()),
                                                data, items.getIdItem(), idInterv,
                                                conf, items.getFlReserve(), items.getNomItem(), items.getIteration());
                                        reportsJobDetailFragmentHelper
                                                .refreshItem(groupIndex, childIndex, 0);
                                    }

                                    @Override
                                    public void doOnCancelClick() {
                                        hideKeyboard();
                                    }
                                }, jobDetails.getString(R.string.enterComment).toUpperCase(),
                                items.getComItem(), isWriteable);

                        enterCommentDialog.show();

                    }
                });

                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED || jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__COMPLETE) {
                    if (commentIv != null) {
                        commentIv.setEnabled(true);
                    }
                } else {
                    if (commentIv != null) {
                        commentIv.setEnabled(false);
                    }
                }

                int flReserve = items.getFlReserve();
                /*
                 * New Changes.....
                 *
                 *
                 * Seperate buttons for Status (Complaint, Resolved & Unresolved).
                 * If a status is selected, update the current flag value to DB. If
                 * a status is unselected (ie., Same status is again clicked),
                 * change the flag value as 0 in DB.
                 *
                 * And if the status is changed, change the color of the line strip
                 * in left of sub-category's layout. For Compliant - Orange,
                 * Resolved - Green, Unresolved - Red, if none is selected then
                 * change the color as White
                 */
                final LinearLayout linCompliant = (LinearLayout) view
                        .findViewById(R.id.linCompliantContainer);
                final LinearLayout linResolved = (LinearLayout) view
                        .findViewById(R.id.linResolvedContainer);
                final LinearLayout linUnresolved = (LinearLayout) view
                        .findViewById(R.id.linUnresolvedContainer);

                // set the boxframe for child
                LinearLayout parentLayout = (LinearLayout) view
                        .findViewById(R.id.ParentLayoutReport);

                if (flReserve == 1) {
                    linCompliant
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_compliant_status);
                } else if (flReserve == 2) {
                    linUnresolved
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_unresolved_status);
                } else if (flReserve == 3) {
                    linResolved
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_resolved_status);
                }
                final int i = flReserve;

                // ........................LISTENER...FOR...STATUS...BUTTONS...STARTS...HERE............................
                linCompliant.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (i != 1) {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 1, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        } else {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }
                    }
                });
                linResolved.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (i != 3) {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 3, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        } else {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }
                    }
                });
                linUnresolved.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (i != 2) {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 2, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        } else {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }
                    }
                });

                // ........................LISTENER...FOR...STATUS...BUTTONS...ENDS...HERE............................

                if (jobDetails.getUpdatedValueOfStatus() != KEYS.JObDetail.JOB__STARTED) {
                    linCompliant.setEnabled(false);
                    linResolved.setEnabled(false);
                    linUnresolved.setEnabled(false);
                    swipeLayout.getSurfaceView().setEnabled(false);
                } else {
                    linCompliant.setEnabled(true);
                    linResolved.setEnabled(true);
                    linUnresolved.setEnabled(true);
                    swipeLayout.getSurfaceView().setEnabled(true);
                }

                // show the view as divider, if it is a last child
                if (isLastChild) {
                    View dividerLine = (View) view.findViewById(R.id.dividerView);
                    dividerLine.setVisibility(View.VISIBLE);
                }

            } else if (itemType == 1) {

                final ArrayList<String> listValues = dataAccessObject
                        .getItemValues(items.getIdItem() + "");
                listValues.add(0, jobDetails.getString(R.string.textNoneLable));
                boolean charMorethan15 = false;
                for (int i = 0; i < listValues.size(); i++) {
                    if (listValues.get(i).length() > 15) {
                        charMorethan15 = true;
                    }
                }

                if (listValues.size() > 3
                        || (listValues.size() <= 3 && charMorethan15)) {
                    view = layoutInflater.inflate(
                            R.layout.layout_datatype_listofvalues, null);
                    android.widget.TextView txtImageAvail = (android.widget.TextView) view
                            .findViewById(R.id.txtImageAvail);
                    View lineImageAvail = (View) view
                            .findViewById(R.id.viewImageAvail);

                    txtImageAvail.setTypeface(fontAwesome);
                    final byte[] photo = items.getImage();
                    // ....................SWIPE...ACTION...STARTS..............................
                    SwipeLayout swipeLayout = (SwipeLayout) view
                            .findViewById(R.id.swipeLayoutDataConform);

                    // set show mode.
                    swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

                    // set drag edge.
                    // swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
                    if ((photo != null) && (photo.length != 0)) {
                        // swipeLayout.setBottomViewIds(R.id.bottom_wrapper_left,
                        // R.id.bottom_wrapper_right, 0, 0);
//					swipeLayout.setDragEdge(DragEdge.Left);
//					swipeLayout.setDragEdge(DragEdge.Right);

                        swipeLayout.addDrag(DragEdge.Left, swipeLayout.findViewById(R.id.bottom_wrapper_left));
                        swipeLayout.addDrag(DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_right));

                        txtImageAvail.setVisibility(View.VISIBLE);
                        lineImageAvail.setVisibility(View.VISIBLE);
                    } else {
                        // swipeLayout.setBottomViewIds(0,
                        // R.id.bottom_wrapper_right,
                        // 0, 0);
//					swipeLayout.setDragEdge(DragEdge.Right);

                        swipeLayout.addDrag(DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_right));

                        txtImageAvail.setVisibility(View.GONE);
                        lineImageAvail.setVisibility(View.GONE);

                    }

                    // ***********************SWIPE...ACTION...ENDS******************************

                    final TextView reportListOfValuesItemName = (TextView) view
                            .findViewById(R.id.fieldName);
                    final Button listOfValues = (Button) view
                            .findViewById(R.id.dataListOfValues);
                    reportListOfValuesItemName.setText(items.getNomItem());

                    final android.widget.TextView txtExpand = (android.widget.TextView) view
                            .findViewById(R.id.txtExpand);
                    Typeface typeface = Typeface.createFromAsset(
                            jobDetails.getAssets(),
                            jobDetails.getResources().getString(
                                    R.string.fontName_fontAwesome));

                    // -------------------NEW CHANGES--------------

                    txtExpand.setTypeface(typeface);

                    Handler handler = new Handler();
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            if (reportListOfValuesItemName.getLineCount() > 1) {
                                txtExpand.setVisibility(View.VISIBLE);
                                reportListOfValuesItemName.setSingleLine(true);
                            } else {
                                txtExpand.setVisibility(View.GONE);
                            }
                        }
                    });

                    txtExpand.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            if (reportListOfValuesItemName.getLineCount() > 1) {
                                reportListOfValuesItemName.setSingleLine(true);
                                txtExpand.setText(jobDetails
                                        .getString(R.string.fa_expand));
                            } else {
                                reportListOfValuesItemName.setSingleLine(false);
                                txtExpand.setText(jobDetails
                                        .getString(R.string.fa_compress));
                            }
                        }
                    });

                    swipeLayout.getSurfaceView().setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            listOfValues.performClick();
                        }
                    });

                    // -------------------NEW CHANGES--------------

                    // .......NEW...CHANGES.......
                    // reportListOfValuesItemName
                    // .setOnClickListener(new OnClickListener() {
                    //
                    // @Override
                    // public void onClick(View v) {
                    // TextView ttt = (TextView) v;
                    // ttt.setSingleLine(false);
                    // ttt.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                    // }
                    // });

                    if (jobDetails.getUpdatedValueOfStatus() != KEYS.JObDetail.JOB__STARTED) {
                        if (listOfValues != null) {
                            listOfValues.setEnabled(false);

                        }
                        view.setEnabled(false);

                    } else {
                        if (listOfValues != null) {
                            listOfValues.setEnabled(true);

                        }
                        view.setEnabled(true);
                    }

                    final String[] tab = new String[listValues.size()];

                    final int conf = 0;
                    // ArrayAdapter adapters = new ArrayAdapter(jobDetails,
                    // android.R.layout.simple_spinner_item, tab);

                    listOfValues.setText(items.getValeurNet());

                    if (items.getOblig() != 0) {
                        if (!TextUtils.isEmpty(listOfValues.getText().toString())) {
                            reportListOfValuesItemName.setTextColor(jobDetails
                                    .getResources().getColor(
                                            R.color.text_category_title_color));
                        } else {
                            reportListOfValuesItemName
                                    .setTextColor(jobDetails
                                            .getResources()
                                            .getColor(
                                                    R.color.textManadatoryFieldsReportsJobDetail));

                        }
                    }

                    listOfValues.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            SelectValueFromListDialog selectValueFromListDialog = new SelectValueFromListDialog(
                                    jobDetails,
                                    listValues,
                                    new SelectValueFromListDialog.OnItemSelectInterface() {

                                        @Override
                                        public void doOnItemSelect(
                                                String itemSelected) {

                                            Logger.log("ExpandableListViewAdapter",
                                                    "Called From type 1");

                                            if (itemSelected.equals(jobDetails
                                                    .getString(R.string.textNoneLable))) {
                                                dataAccessObject.updateValue("",
                                                        items.getComItem(),
                                                        items.getIdItem(),
                                                        idInterv, conf,
                                                        items.getFlReserve(), items.getNomItem(), items.getIteration());
                                                items.setValeurNet(itemSelected);

                                            } else {

                                                dataAccessObject.updateValue(
                                                        itemSelected,
                                                        items.getComItem(),
                                                        items.getIdItem(),
                                                        idInterv, conf,
                                                        items.getFlReserve(), items.getNomItem(), items.getIteration());
                                                items.setValeurNet(itemSelected);

                                            }

                                            reportsJobDetailFragmentHelper
                                                    .refreshItem(groupIndex, childIndex, 0);

                                        }
                                    });

                            selectValueFromListDialog.show();

                        }
                    });

                    ImageView imgph = (ImageView) view.findViewById(R.id.itemImage);
                    if ((photo != null) && (photo.length != 0)) {

//                        final Bitmap btImag = BitmapFactory.decodeByteArray(photo,
//                                0, photo.length, opt);
//                        // BitmapDrawable bmd = new
//                        // BitmapDrawable(resiseBitmap(imgph,
//                        // btImag));
//                        imgph.setImageBitmap(btImag);

                        //new changes
                        Glide.with(jobDetails)
                                .load(photo)
                                .asBitmap()
                                .override(200, 200)
                                .fitCenter()
                                .placeholder(R.drawable.library_iicon)
                                .into(imgph);

                        imgph.setVisibility(View.VISIBLE);

                        imgph.setOnClickListener(new OnClickListener() {
                            public void onClick(final View v) {

                                DisplayMetrics displaymetrics = new DisplayMetrics();
                                jobDetails.getWindowManager().getDefaultDisplay()
                                        .getMetrics(displaymetrics);

                                // Bitmap bm2 = dataAccessObject.resizeBitmap(btImag
                                // ,
                                // displaymetrics.widthPixels ,
                                // displaymetrics.heightPixels);

//                                Bitmap resized = Bitmap.createScaledBitmap(btImag,
//                                        displaymetrics.widthPixels,
//                                        displaymetrics.heightPixels, true);
//                                showImg(v, btImag);

                                Glide.with(jobDetails)
                                        .load(photo)
                                        .asBitmap()
                                        .fitCenter()
                                        .placeholder(R.drawable.library_iicon)
                                        .into(new SimpleTarget<Bitmap>(displaymetrics.widthPixels, displaymetrics.heightPixels) {
                                            @Override
                                            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                                showImg(v, resource);
                                            }
                                        });
                            }
                        });

                    } else {
                        imgph.setVisibility(View.GONE);
                    }

                    android.widget.TextView commentIv = (android.widget.TextView) view
                            .findViewById(R.id.commentIv);
                    commentIv.setTypeface(fontAwesome);
                    if (gt != null && gt.getFlCommentsRapport() == 0) {
                        commentIv.setVisibility(View.GONE);

                    }

                    final boolean isWriteable = jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED;

                    commentIv.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            EnterCommentDialog enterCommentDialog = new EnterCommentDialog(
                                    jobDetails,
                                    new EnterCommentDialog.EnterDialogInterface() {

                                        @Override
                                        public void doOnModifyClick(String data) {
                                            dataAccessObject.updateValue(
                                                    getValueFormat(
                                                            items.getValeurNet(),
                                                            items.getIdTypeItem()),
                                                    data, items.getIdItem(),
                                                    idInterv, conf, items
                                                            .getFlReserve(), items.getNomItem(), items.getIteration());
                                            reportsJobDetailFragmentHelper
                                                    .refreshItem(groupIndex, childIndex, 0);
                                        }

                                        @Override
                                        public void doOnCancelClick() {
                                            hideKeyboard();
                                        }
                                    }, jobDetails.getString(R.string.enterComment).toUpperCase(),
                                    items.getComItem(), isWriteable);

                            enterCommentDialog.show();

                        }
                    });

                    if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED || jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__COMPLETE) {
                        if (commentIv != null) {
                            commentIv.setEnabled(true);
                        }
                    } else {
                        if (commentIv != null) {
                            commentIv.setEnabled(false);
                        }
                    }

                    int flReserve = items.getFlReserve();

                    /*
                     * New Changes.....
                     *
                     *
                     * Seperate buttons for Status (Complaint, Resolved &
                     * Unresolved). If a status is selected, update the current flag
                     * value to DB. If a status is unselected (ie., Same status is
                     * again clicked), change the flag value as 0 in DB.
                     *
                     * And if the status is changed, change the color of the line
                     * strip in left of sub-category's layout. For Compliant -
                     * Orange, Resolved - Green, Unresolved - Red, if none is
                     * selected then change the color as White
                     */
                    final LinearLayout linCompliant = (LinearLayout) view
                            .findViewById(R.id.linCompliantContainer);
                    final LinearLayout linResolved = (LinearLayout) view
                            .findViewById(R.id.linResolvedContainer);
                    final LinearLayout linUnresolved = (LinearLayout) view
                            .findViewById(R.id.linUnresolvedContainer);

                    // set the boxframe for child
                    LinearLayout parentLayout = (LinearLayout) view
                            .findViewById(R.id.ParentLayoutReport);

                    if (flReserve == 1) {
                        linCompliant
                                .setBackgroundResource(R.drawable.boxframe_report_status);
                        parentLayout
                                .setBackgroundResource(R.drawable.boxframe_compliant_status);
                    } else if (flReserve == 2) {
                        linUnresolved
                                .setBackgroundResource(R.drawable.boxframe_report_status);
                        parentLayout
                                .setBackgroundResource(R.drawable.boxframe_unresolved_status);
                    } else if (flReserve == 3) {
                        linResolved
                                .setBackgroundResource(R.drawable.boxframe_report_status);
                        parentLayout
                                .setBackgroundResource(R.drawable.boxframe_resolved_status);
                    }
                    final int i = flReserve;

                    // ........................LISTENER...FOR...STATUS...BUTTONS...STARTS...HERE............................
                    linCompliant.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            if (i != 1) {
                                dataAccessObject.updateValue(
                                        getValueFormat(items.getValeurNet(),
                                                items.getIdTypeItem()),
                                        items.getComItem(), items.getIdItem(),
                                        idInterv, conf, 1, items.getNomItem(), items.getIteration());
                                reportsJobDetailFragmentHelper
                                        .refreshItem(groupIndex, childIndex, 1);
                            } else {
                                dataAccessObject.updateValue(
                                        getValueFormat(items.getValeurNet(),
                                                items.getIdTypeItem()),
                                        items.getComItem(), items.getIdItem(),
                                        idInterv, conf, 0, items.getNomItem(), items.getIteration());
                                reportsJobDetailFragmentHelper
                                        .refreshItem(groupIndex, childIndex, 1);
                            }
                        }
                    });
                    linResolved.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (i != 3) {
                                dataAccessObject.updateValue(
                                        getValueFormat(items.getValeurNet(),
                                                items.getIdTypeItem()),
                                        items.getComItem(), items.getIdItem(),
                                        idInterv, conf, 3, items.getNomItem(), items.getIteration());
                                reportsJobDetailFragmentHelper
                                        .refreshItem(groupIndex, childIndex, 1);
                            } else {
                                dataAccessObject.updateValue(
                                        getValueFormat(items.getValeurNet(),
                                                items.getIdTypeItem()),
                                        items.getComItem(), items.getIdItem(),
                                        idInterv, conf, 0, items.getNomItem(), items.getIteration());
                                reportsJobDetailFragmentHelper
                                        .refreshItem(groupIndex, childIndex, 1);
                            }
                        }
                    });
                    linUnresolved.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (i != 2) {
                                dataAccessObject.updateValue(
                                        getValueFormat(items.getValeurNet(),
                                                items.getIdTypeItem()),
                                        items.getComItem(), items.getIdItem(),
                                        idInterv, conf, 2, items.getNomItem(), items.getIteration());
                                reportsJobDetailFragmentHelper
                                        .refreshItem(groupIndex, childIndex, 1);
                            } else {
                                dataAccessObject.updateValue(
                                        getValueFormat(items.getValeurNet(),
                                                items.getIdTypeItem()),
                                        items.getComItem(), items.getIdItem(),
                                        idInterv, conf, 0, items.getNomItem(), items.getIteration());
                                reportsJobDetailFragmentHelper
                                        .refreshItem(groupIndex, childIndex, 1);
                            }
                        }
                    });

                    // ........................LISTENER...FOR...STATUS...BUTTONS...ENDS...HERE............................

                    if (jobDetails.getUpdatedValueOfStatus() != KEYS.JObDetail.JOB__STARTED) {
                        linCompliant.setEnabled(false);
                        linResolved.setEnabled(false);
                        linUnresolved.setEnabled(false);
                        swipeLayout.getSurfaceView().setEnabled(false);
                    } else {
                        linCompliant.setEnabled(true);
                        linResolved.setEnabled(true);
                        linUnresolved.setEnabled(true);
                        swipeLayout.getSurfaceView().setEnabled(true);
                    }

                    // show the view as divider, if it is a last child
                    if (isLastChild) {
                        View dividerLine = (View) view
                                .findViewById(R.id.dividerView);
                        dividerLine.setVisibility(View.VISIBLE);
                    }
                } else {

                    view = layoutInflater.inflate(
                            R.layout.layout_datatype_listofvalues_radio_button,
                            null);
                    android.widget.TextView txtImageAvail = (android.widget.TextView) view
                            .findViewById(R.id.txtImageAvail);
                    View lineImageAvail = (View) view
                            .findViewById(R.id.viewImageAvail);

                    txtImageAvail.setTypeface(fontAwesome);
                    final byte[] photo = items.getImage();
                    // ....................SWIPE...ACTION...STARTS..............................
                    SwipeLayout swipeLayout = (SwipeLayout) view
                            .findViewById(R.id.swipeLayoutDataConform);

                    // set show mode.
                    swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

                    // set drag edge.
                    // swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
                    if ((photo != null) && (photo.length != 0)) {
                        // swipeLayout.setBottomViewIds(R.id.bottom_wrapper_left,
                        // R.id.bottom_wrapper_right, 0, 0);
//					swipeLayout.setDragEdge(DragEdge.Left);
//					swipeLayout.setDragEdge(DragEdge.Right);

                        swipeLayout.addDrag(DragEdge.Left, swipeLayout.findViewById(R.id.bottom_wrapper_left));
                        swipeLayout.addDrag(DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_right));

                        txtImageAvail.setVisibility(View.VISIBLE);
                        lineImageAvail.setVisibility(View.VISIBLE);
                    } else {
                        // swipeLayout.setBottomViewIds(0,
                        // R.id.bottom_wrapper_right,
                        // 0, 0);
//					swipeLayout.setDragEdge(DragEdge.Right);

                        swipeLayout.addDrag(DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_right));

                        txtImageAvail.setVisibility(View.GONE);
                        lineImageAvail.setVisibility(View.GONE);

                    }

                    // ***********************SWIPE...ACTION...ENDS******************************

                    final TextView reportListOfValuesItemName = (TextView) view
                            .findViewById(R.id.fieldName);
                    reportListOfValuesItemName.setText(items.getNomItem());

                    final android.widget.TextView txtExpand = (android.widget.TextView) view
                            .findViewById(R.id.txtExpand);
                    Typeface typeface = Typeface.createFromAsset(
                            jobDetails.getAssets(),
                            jobDetails.getResources().getString(
                                    R.string.fontName_fontAwesome));

                    // -------------------NEW CHANGES--------------

                    txtExpand.setTypeface(typeface);

                    Handler handler = new Handler();
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            if (reportListOfValuesItemName.getLineCount() > 1) {
                                txtExpand.setVisibility(View.VISIBLE);
                                reportListOfValuesItemName.setSingleLine(true);
                            } else {
                                txtExpand.setVisibility(View.GONE);
                            }
                        }
                    });

                    txtExpand.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            if (reportListOfValuesItemName.getLineCount() > 1) {
                                reportListOfValuesItemName.setSingleLine(true);
                                txtExpand.setText(jobDetails
                                        .getString(R.string.fa_expand));
                            } else {
                                reportListOfValuesItemName.setSingleLine(false);
                                txtExpand.setText(jobDetails
                                        .getString(R.string.fa_compress));
                            }
                        }
                    });

                    // -------------------NEW CHANGES--------------

                    // .......NEW...CHANGES.......
                    // reportListOfValuesItemName
                    // .setOnClickListener(new OnClickListener() {
                    //
                    // @Override
                    // public void onClick(View v) {
                    // TextView ttt = (TextView) v;
                    // if (ttt.getLineCount() > 1) {
                    // ttt.setSingleLine(true);
                    // } else {
                    // ttt.setSingleLine(false);
                    // }
                    //
                    // }
                    // });

                    // final Button listOfValues = (Button) view
                    // .findViewById(R.id.dataListOfValues);

                    final RadioGroup radioGroupListValues = (RadioGroup) view
                            .findViewById(R.id.dataListOfValues);
                    final RadioButton radioBtn1 = (RadioButton) view
                            .findViewById(R.id.radioBtnOne);
                    final RadioButton radioBtn2 = (RadioButton) view
                            .findViewById(R.id.radioBtnTwo);
                    final RadioButton radioBtn3 = (RadioButton) view
                            .findViewById(R.id.radioBtnThree);

                    radioBtn1.setText(listValues.get(0));

                    if (listValues.size() > 1) {
                        radioBtn2.setText(listValues.get(1));
                    } else {
                        radioBtn2.setVisibility(View.GONE);
                    }

                    if (listValues.size() > 2) {
                        radioBtn3.setText(listValues.get(2));
                    } else {
                        radioBtn3.setVisibility(View.GONE);
                    }

                    if (jobDetails.getUpdatedValueOfStatus() != KEYS.JObDetail.JOB__STARTED) {
                        if (radioGroupListValues != null) {
                            radioBtn1.setEnabled(false);
                            radioBtn2.setEnabled(false);
                            radioBtn3.setEnabled(false);
                        }

                    } else {
                        if (radioGroupListValues != null) {
                            radioBtn1.setEnabled(true);
                            radioBtn2.setEnabled(true);
                            radioBtn3.setEnabled(true);
                        }

                    }

                    final String[] tab = new String[listValues.size()];

                    final int conf = 0;

                    if (listValues.size() == 3) {
                        if (items.getValeurNet().equalsIgnoreCase("")) {
                            radioBtn1.setChecked(true);
                            radioBtn2.setChecked(false);
                            radioBtn3.setChecked(false);
                        } else if (items.getValeurNet().equalsIgnoreCase(
                                listValues.get(1))) {
                            radioBtn1.setChecked(false);
                            radioBtn2.setChecked(true);
                            radioBtn3.setChecked(false);
                        } else if (items.getValeurNet().equalsIgnoreCase(
                                listValues.get(2))) {
                            radioBtn1.setChecked(false);
                            radioBtn2.setChecked(false);
                            radioBtn3.setChecked(true);
                        }
                    } else if (listValues.size() == 2) {
                        if (items.getValeurNet().equalsIgnoreCase("")) {
                            radioBtn1.setChecked(true);
                            radioBtn2.setChecked(false);
                            radioBtn3.setChecked(false);
                        } else if (items.getValeurNet().equalsIgnoreCase(
                                listValues.get(1))) {
                            radioBtn1.setChecked(false);
                            radioBtn2.setChecked(true);
                            radioBtn3.setChecked(false);
                        }
                    } else {
                        if (items.getValeurNet().equalsIgnoreCase("")) {
                            radioBtn1.setChecked(true);
                            radioBtn2.setChecked(false);
                            radioBtn3.setChecked(false);
                        }
                    }

                    if (items.getOblig() != 0) {
                        if (radioGroupListValues.getCheckedRadioButtonId() != R.id.radioBtnOne) {
                            reportListOfValuesItemName.setTextColor(jobDetails
                                    .getResources().getColor(
                                            R.color.text_category_title_color));
                        } else {
                            reportListOfValuesItemName
                                    .setTextColor(jobDetails
                                            .getResources()
                                            .getColor(
                                                    R.color.textManadatoryFieldsReportsJobDetail));

                        }
                    }

                    radioGroupListValues
                            .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                                @Override
                                public void onCheckedChanged(RadioGroup group,
                                                             int checkedId) {
                                    String itemSelected = "";
                                    if (checkedId == R.id.radioBtnOne) {
                                        itemSelected = listValues.get(0);
                                        dataAccessObject.updateValue("",
                                                items.getComItem(),
                                                items.getIdItem(), idInterv, conf,
                                                items.getFlReserve(), items.getNomItem(), items.getIteration());
                                        items.setValeurNet(itemSelected);
                                    } else if (checkedId == R.id.radioBtnTwo) {
                                        itemSelected = listValues.get(1);
                                        dataAccessObject.updateValue(itemSelected,
                                                items.getComItem(),
                                                items.getIdItem(), idInterv, conf,
                                                items.getFlReserve(), items.getNomItem(), items.getIteration());
                                        items.setValeurNet(itemSelected);
                                    } else {
                                        itemSelected = listValues.get(2);
                                        dataAccessObject.updateValue(itemSelected,
                                                items.getComItem(),
                                                items.getIdItem(), idInterv, conf,
                                                items.getFlReserve(), items.getNomItem(), items.getIteration());
                                        items.setValeurNet(itemSelected);
                                    }
                                    reportsJobDetailFragmentHelper
                                            .refreshItem(groupIndex, childIndex, 0);
                                }

                            });

                    ImageView imgph = (ImageView) view.findViewById(R.id.itemImage);
                    if ((photo != null) && (photo.length != 0)) {

//                        final Bitmap btImag = BitmapFactory.decodeByteArray(photo,
//                                0, photo.length, opt);
//                        // BitmapDrawable bmd = new
//                        // BitmapDrawable(resiseBitmap(imgph,
//                        // btImag));
//                        imgph.setImageBitmap(btImag);

                        //new changes
                        Glide.with(jobDetails)
                                .load(photo)
                                .asBitmap()
                                .override(200, 200)
                                .fitCenter()
                                .placeholder(R.drawable.library_iicon)
                                .into(imgph);

                        imgph.setVisibility(View.VISIBLE);

                        imgph.setOnClickListener(new OnClickListener() {
                            public void onClick(final View v) {

                                DisplayMetrics displaymetrics = new DisplayMetrics();
                                jobDetails.getWindowManager().getDefaultDisplay()
                                        .getMetrics(displaymetrics);

                                // Bitmap bm2 = dataAccessObject.resizeBitmap(btImag
                                // ,
                                // displaymetrics.widthPixels ,
                                // displaymetrics.heightPixels);

//                                Bitmap resized = Bitmap.createScaledBitmap(btImag,
//                                        displaymetrics.widthPixels,
//                                        displaymetrics.heightPixels, true);
//
//                                showImg(v, btImag);

                                Glide.with(jobDetails)
                                        .load(photo)
                                        .asBitmap()
                                        .fitCenter()
                                        .placeholder(R.drawable.library_iicon)
                                        .into(new SimpleTarget<Bitmap>(displaymetrics.widthPixels, displaymetrics.heightPixels) {
                                            @Override
                                            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                                showImg(v, resource);
                                            }
                                        });
                            }
                        });

                    } else {
                        imgph.setVisibility(View.GONE);
                    }

                    android.widget.TextView commentIv = (android.widget.TextView) view
                            .findViewById(R.id.commentIv);
                    commentIv.setTypeface(fontAwesome);
                    if (gt != null && gt.getFlCommentsRapport() == 0) {
                        commentIv.setVisibility(View.GONE);

                    }

                    final boolean isWriteable = jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED;

                    commentIv.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            EnterCommentDialog enterCommentDialog = new EnterCommentDialog(
                                    jobDetails,
                                    new EnterCommentDialog.EnterDialogInterface() {

                                        @Override
                                        public void doOnModifyClick(String data) {
                                            dataAccessObject.updateValue(
                                                    getValueFormat(
                                                            items.getValeurNet(),
                                                            items.getIdTypeItem()),
                                                    data, items.getIdItem(),
                                                    idInterv, conf, items
                                                            .getFlReserve(), items.getNomItem(), items.getIteration());
                                            reportsJobDetailFragmentHelper
                                                    .refreshItem(groupIndex, childIndex, 0);
                                        }

                                        @Override
                                        public void doOnCancelClick() {
                                            hideKeyboard();
                                        }
                                    }, jobDetails.getString(R.string.enterComment).toUpperCase(),
                                    items.getComItem(), isWriteable);

                            enterCommentDialog.show();

                        }
                    });

                    if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED || jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__COMPLETE) {
                        if (commentIv != null) {
                            commentIv.setEnabled(true);
                        }
                    } else {
                        if (commentIv != null) {
                            commentIv.setEnabled(false);
                        }
                    }

                    int flReserve = items.getFlReserve();

                    /*
                     * New Changes.....
                     *
                     *
                     * Seperate buttons for Status (Complaint, Resolved &
                     * Unresolved). If a status is selected, update the current flag
                     * value to DB. If a status is unselected (ie., Same status is
                     * again clicked), change the flag value as 0 in DB.
                     *
                     * And if the status is changed, change the color of the line
                     * strip in left of sub-category's layout. For Compliant -
                     * Orange, Resolved - Green, Unresolved - Red, if none is
                     * selected then change the color as White
                     */
                    final LinearLayout linCompliant = (LinearLayout) view
                            .findViewById(R.id.linCompliantContainer);
                    final LinearLayout linResolved = (LinearLayout) view
                            .findViewById(R.id.linResolvedContainer);
                    final LinearLayout linUnresolved = (LinearLayout) view
                            .findViewById(R.id.linUnresolvedContainer);

                    // set the boxframe for child
                    LinearLayout parentLayout = (LinearLayout) view
                            .findViewById(R.id.ParentLayoutReport);

                    if (flReserve == 1) {
                        linCompliant
                                .setBackgroundResource(R.drawable.boxframe_report_status);
                        parentLayout
                                .setBackgroundResource(R.drawable.boxframe_compliant_status);
                    } else if (flReserve == 2) {
                        linUnresolved
                                .setBackgroundResource(R.drawable.boxframe_report_status);
                        parentLayout
                                .setBackgroundResource(R.drawable.boxframe_unresolved_status);
                    } else if (flReserve == 3) {
                        linResolved
                                .setBackgroundResource(R.drawable.boxframe_report_status);
                        parentLayout
                                .setBackgroundResource(R.drawable.boxframe_resolved_status);
                    }
                    final int i = flReserve;

                    // ........................LISTENER...FOR...STATUS...BUTTONS...STARTS...HERE............................
                    linCompliant.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            if (i != 1) {
                                dataAccessObject.updateValue(
                                        getValueFormat(items.getValeurNet(),
                                                items.getIdTypeItem()),
                                        items.getComItem(), items.getIdItem(),
                                        idInterv, conf, 1, items.getNomItem(), items.getIteration());
                                reportsJobDetailFragmentHelper
                                        .refreshItem(groupIndex, childIndex, 1);
                            } else {
                                dataAccessObject.updateValue(
                                        getValueFormat(items.getValeurNet(),
                                                items.getIdTypeItem()),
                                        items.getComItem(), items.getIdItem(),
                                        idInterv, conf, 0, items.getNomItem(), items.getIteration());
                                reportsJobDetailFragmentHelper
                                        .refreshItem(groupIndex, childIndex, 1);
                            }
                        }
                    });
                    linResolved.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (i != 3) {
                                dataAccessObject.updateValue(
                                        getValueFormat(items.getValeurNet(),
                                                items.getIdTypeItem()),
                                        items.getComItem(), items.getIdItem(),
                                        idInterv, conf, 3, items.getNomItem(), items.getIteration());
                                reportsJobDetailFragmentHelper
                                        .refreshItem(groupIndex, childIndex, 1);
                            } else {
                                dataAccessObject.updateValue(
                                        getValueFormat(items.getValeurNet(),
                                                items.getIdTypeItem()),
                                        items.getComItem(), items.getIdItem(),
                                        idInterv, conf, 0, items.getNomItem(), items.getIteration());
                                reportsJobDetailFragmentHelper
                                        .refreshItem(groupIndex, childIndex, 1);
                            }
                        }
                    });
                    linUnresolved.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (i != 2) {
                                dataAccessObject.updateValue(
                                        getValueFormat(items.getValeurNet(),
                                                items.getIdTypeItem()),
                                        items.getComItem(), items.getIdItem(),
                                        idInterv, conf, 2, items.getNomItem(), items.getIteration());
                                reportsJobDetailFragmentHelper
                                        .refreshItem(groupIndex, childIndex, 1);
                            } else {
                                dataAccessObject.updateValue(
                                        getValueFormat(items.getValeurNet(),
                                                items.getIdTypeItem()),
                                        items.getComItem(), items.getIdItem(),
                                        idInterv, conf, 0, items.getNomItem(), items.getIteration());
                                reportsJobDetailFragmentHelper
                                        .refreshItem(groupIndex, childIndex, 1);
                            }
                        }
                    });

                    // ........................LISTENER...FOR...STATUS...BUTTONS...ENDS...HERE............................

                    if (jobDetails.getUpdatedValueOfStatus() != KEYS.JObDetail.JOB__STARTED) {
                        linCompliant.setEnabled(false);
                        linResolved.setEnabled(false);
                        linUnresolved.setEnabled(false);

                    } else {
                        linCompliant.setEnabled(true);
                        linResolved.setEnabled(true);
                        linUnresolved.setEnabled(true);
                    }

                    // show the view as divider, if it is a last child
                    if (isLastChild) {
                        View dividerLine = (View) view
                                .findViewById(R.id.dividerView);
                        dividerLine.setVisibility(View.VISIBLE);
                    }

                }
            } else if (itemType == 3) {
                view = layoutInflater.inflate(R.layout.layout_datatype_date, null);

                android.widget.TextView txtImageAvail = (android.widget.TextView) view
                        .findViewById(R.id.txtImageAvail);
                View lineImageAvail = (View) view.findViewById(R.id.viewImageAvail);

                txtImageAvail.setTypeface(fontAwesome);
                final byte[] photo = items.getImage();
                // ....................SWIPE...ACTION...STARTS..............................
                SwipeLayout swipeLayout = (SwipeLayout) view
                        .findViewById(R.id.swipeLayoutDataConform);

                // set show mode.
                swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
                // set drag edge.
                // swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
                if ((photo != null) && (photo.length != 0)) {
                    // swipeLayout.setBottomViewIds(R.id.bottom_wrapper_left,
                    // R.id.bottom_wrapper_right, 0, 0);
//				swipeLayout.setDragEdge(DragEdge.Left);
//				swipeLayout.setDragEdge(DragEdge.Right);

                    swipeLayout.addDrag(DragEdge.Left, swipeLayout.findViewById(R.id.bottom_wrapper_left));
                    swipeLayout.addDrag(DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_right));

                    txtImageAvail.setVisibility(View.VISIBLE);
                    lineImageAvail.setVisibility(View.VISIBLE);
                } else {
                    // swipeLayout
                    // .setBottomViewIds(0, R.id.bottom_wrapper_right, 0, 0);
//				swipeLayout.setDragEdge(DragEdge.Right);

                    swipeLayout.addDrag(DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_right));

                    txtImageAvail.setVisibility(View.GONE);
                    lineImageAvail.setVisibility(View.GONE);

                }

                // ***********************SWIPE...ACTION...ENDS******************************

                final TextView reportsDateItemName = (TextView) view
                        .findViewById(R.id.fieldName);
                ImageView clearDateDataIv = (ImageView) view
                        .findViewById(R.id.clearDateDataIv);
                final Button dateBtn = (Button) view.findViewById(R.id.dataDate);

                reportsDateItemName.setText(items.getNomItem());

                final android.widget.TextView txtExpand = (android.widget.TextView) view
                        .findViewById(R.id.txtExpand);
                Typeface typeface = Typeface.createFromAsset(
                        jobDetails.getAssets(), jobDetails.getResources()
                                .getString(R.string.fontName_fontAwesome));

                // -------------------NEW CHANGES--------------

                txtExpand.setTypeface(typeface);

                Handler handler = new Handler();
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (reportsDateItemName.getLineCount() > 1) {
                            txtExpand.setVisibility(View.VISIBLE);
                            reportsDateItemName.setSingleLine(true);
                        } else {
                            txtExpand.setVisibility(View.GONE);
                        }
                    }
                });

                txtExpand.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (reportsDateItemName.getLineCount() > 1) {
                            reportsDateItemName.setSingleLine(true);
                            txtExpand.setText(jobDetails
                                    .getString(R.string.fa_expand));
                        } else {
                            reportsDateItemName.setSingleLine(false);
                            txtExpand.setText(jobDetails
                                    .getString(R.string.fa_compress));
                        }
                    }
                });

                // -------------------NEW CHANGES--------------

                clearDateDataIv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        int conf = 0;

                        dataAccessObject.updateValue("", "", items.getIdItem(),
                                idInterv, conf, items.getFlReserve(), items.getNomItem(), items.getIteration());
                        dateBtn.setText("");

                        items.setValeurNet("");
                        reportsJobDetailFragmentHelper.refreshItem(items
                                .getGroupPosition(), childIndex, 0);
                    }
                });

                if (jobDetails.getUpdatedValueOfStatus() != KEYS.JObDetail.JOB__STARTED) {
                    if (dateBtn != null) {
                        dateBtn.setEnabled(false);

                    }
                    view.setEnabled(false);
                } else {
                    if (dateBtn != null) {
                        dateBtn.setEnabled(true);

                    }
                    view.setEnabled(true);
                }
                Date date = null;
                String val = items.getValeurNet();
                try {
                    if (!TextUtils.isEmpty(val)) {
                        date = frenchDateFormat.parse(val);
                    }
                } catch (ParseException e) {
                    Logger.printException(e);
                }

                Format format = android.text.format.DateFormat
                        .getDateFormat(jobDetails);

                if (date != null) {
                    val = format.format(date);
                }

                dateBtn.setText(val);

                if (items.getOblig() != 0) {

                    if (!TextUtils.isEmpty(dateBtn.getText().toString())) {

                        reportsDateItemName.setTextColor(jobDetails.getResources()
                                .getColor(R.color.text_category_title_color));
                    } else {
                        reportsDateItemName
                                .setTextColor(jobDetails
                                        .getResources()
                                        .getColor(
                                                R.color.textManadatoryFieldsReportsJobDetail));
                    }
                }

                if (!TextUtils.isEmpty(dateBtn.getText())) {
                    if ((jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__COMPLETE)
                            || (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.DEDLINE_EXCEEDED)) {
                        clearDateDataIv.setVisibility(View.GONE);
                    } else {
                        clearDateDataIv.setVisibility(View.VISIBLE);
                    }
                } else {
                    clearDateDataIv.setVisibility(View.GONE);
                }

                dateBtn.setTag(items);
//			dateBtn.setOnClickListener(onClickListener);
                dateBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDate2((Item) v.getTag(), childIndex, v);
                    }
                });
                swipeLayout.getSurfaceView().setTag(items);
                swipeLayout.getSurfaceView().setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dateBtn.performClick();
                    }
                });

                ImageView imgph = (ImageView) view.findViewById(R.id.itemImage);
                if ((photo != null) && (photo.length != 0)) {

//                    final Bitmap btImag = BitmapFactory.decodeByteArray(photo, 0,
//                            photo.length, opt);
//                    // BitmapDrawable bmd = new BitmapDrawable(resiseBitmap(imgph,
//                    // btImag));
//                    imgph.setImageBitmap(btImag);

                    //new changes
                    Glide.with(jobDetails)
                            .load(photo)
                            .asBitmap()
                            .override(200, 200)
                            .fitCenter()
                            .placeholder(R.drawable.library_iicon)
                            .into(imgph);

                    imgph.setVisibility(View.VISIBLE);

                    imgph.setOnClickListener(new OnClickListener() {
                        public void onClick(final View v) {

                            DisplayMetrics displaymetrics = new DisplayMetrics();
                            jobDetails.getWindowManager().getDefaultDisplay()
                                    .getMetrics(displaymetrics);

                            // Bitmap bm2 = dataAccessObject.resizeBitmap(btImag ,
                            // displaymetrics.widthPixels ,
                            // displaymetrics.heightPixels);

//                            Bitmap resized = Bitmap.createScaledBitmap(btImag,
//                                    displaymetrics.widthPixels,
//                                    displaymetrics.heightPixels, true);
//                            showImg(v, btImag);

                            Glide.with(jobDetails)
                                    .load(photo)
                                    .asBitmap()
                                    .fitCenter()
                                    .placeholder(R.drawable.library_iicon)
                                    .into(new SimpleTarget<Bitmap>(displaymetrics.widthPixels, displaymetrics.heightPixels) {
                                        @Override
                                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                            showImg(v, resource);
                                        }
                                    });
                        }
                    });

                } else {
                    imgph.setVisibility(View.GONE);
                }

                android.widget.TextView commentIv = (android.widget.TextView) view
                        .findViewById(R.id.commentIv);
                commentIv.setTypeface(fontAwesome);
                if (gt != null && gt.getFlCommentsRapport() == 0) {
                    commentIv.setVisibility(View.GONE);

                }

                final boolean isWriteable = jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED;

                commentIv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        EnterCommentDialog enterCommentDialog = new EnterCommentDialog(
                                jobDetails,
                                new EnterCommentDialog.EnterDialogInterface() {

                                    @Override
                                    public void doOnModifyClick(String data) {
                                        int conf = 0;
                                        dataAccessObject.updateValue(
                                                getValueFormat(
                                                        items.getValeurNet(),
                                                        items.getIdTypeItem()),
                                                data, items.getIdItem(), idInterv,
                                                conf, items.getFlReserve(), items.getNomItem(), items.getIteration());
                                        reportsJobDetailFragmentHelper
                                                .refreshItem(groupIndex, childIndex, 0);
                                    }

                                    @Override
                                    public void doOnCancelClick() {
                                        hideKeyboard();
                                    }
                                }, jobDetails.getString(R.string.enterComment).toUpperCase(),
                                items.getComItem(), isWriteable);

                        enterCommentDialog.show();

                    }
                });

                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED || jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__COMPLETE) {
                    if (commentIv != null) {
                        commentIv.setEnabled(true);
                    }
                } else {
                    if (commentIv != null) {
                        commentIv.setEnabled(false);
                    }
                }

                int flReserve = items.getFlReserve();
                final int conf = 0;

                /*
                 * New Changes.....
                 *
                 *
                 * Seperate buttons for Status (Complaint, Resolved & Unresolved).
                 * If a status is selected, update the current flag value to DB. If
                 * a status is unselected (ie., Same status is again clicked),
                 * change the flag value as 0 in DB.
                 *
                 * And if the status is changed, change the color of the line strip
                 * in left of sub-category's layout. For Compliant - Orange,
                 * Resolved - Green, Unresolved - Red, if none is selected then
                 * change the color as White
                 */
                final LinearLayout linCompliant = (LinearLayout) view
                        .findViewById(R.id.linCompliantContainer);
                final LinearLayout linResolved = (LinearLayout) view
                        .findViewById(R.id.linResolvedContainer);
                final LinearLayout linUnresolved = (LinearLayout) view
                        .findViewById(R.id.linUnresolvedContainer);

                // set the boxframe for child
                LinearLayout parentLayout = (LinearLayout) view
                        .findViewById(R.id.ParentLayoutReport);

                if (flReserve == 1) {
                    linCompliant
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_compliant_status);
                } else if (flReserve == 2) {
                    linUnresolved
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_unresolved_status);
                } else if (flReserve == 3) {
                    linResolved
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_resolved_status);
                }
                final int i = flReserve;

                // ........................LISTENER...FOR...STATUS...BUTTONS...STARTS...HERE............................
                linCompliant.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (i != 1) {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 1, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        } else {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }
                    }
                });
                linResolved.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (i != 3) {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 3, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        } else {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }
                    }
                });
                linUnresolved.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (i != 2) {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 2, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        } else {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }
                    }
                });

                // ........................LISTENER...FOR...STATUS...BUTTONS...ENDS...HERE............................

                if (jobDetails.getUpdatedValueOfStatus() != KEYS.JObDetail.JOB__STARTED) {
                    linCompliant.setEnabled(false);
                    linResolved.setEnabled(false);
                    linUnresolved.setEnabled(false);
                    swipeLayout.getSurfaceView().setEnabled(false);
                } else {
                    linCompliant.setEnabled(true);
                    linResolved.setEnabled(true);
                    linUnresolved.setEnabled(true);
                    swipeLayout.getSurfaceView().setEnabled(true);
                }

            } else if (itemType == 4) {

                view = layoutInflater.inflate(R.layout.layout_datatype_hours, null);

                android.widget.TextView txtImageAvail = (android.widget.TextView) view
                        .findViewById(R.id.txtImageAvail);
                View lineImageAvail = (View) view.findViewById(R.id.viewImageAvail);

                txtImageAvail.setTypeface(fontAwesome);
                final byte[] photo = items.getImage();
                // ....................SWIPE...ACTION...STARTS..............................
                SwipeLayout swipeLayout = (SwipeLayout) view
                        .findViewById(R.id.swipeLayoutDataConform);

                // set show mode.
                swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

                // set drag edge.
                // swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
                if ((photo != null) && (photo.length != 0)) {
                    // swipeLayout.setBottomViewIds(R.id.bottom_wrapper_left,
                    // R.id.bottom_wrapper_right, 0, 0);
//				swipeLayout.setDragEdge(DragEdge.Left);
//				swipeLayout.setDragEdge(DragEdge.Right);

                    swipeLayout.addDrag(DragEdge.Left, swipeLayout.findViewById(R.id.bottom_wrapper_left));
                    swipeLayout.addDrag(DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_right));

                    txtImageAvail.setVisibility(View.VISIBLE);
                    lineImageAvail.setVisibility(View.VISIBLE);
                } else {
                    // swipeLayout
                    // .setBottomViewIds(0, R.id.bottom_wrapper_right, 0, 0);
//				swipeLayout.setDragEdge(DragEdge.Right);

                    swipeLayout.addDrag(DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_right));

                    txtImageAvail.setVisibility(View.GONE);
                    lineImageAvail.setVisibility(View.GONE);

                }

                // ***********************SWIPE...ACTION...ENDS******************************

                final TextView reportHoursItemName = (TextView) view
                        .findViewById(R.id.fieldName);
                ImageView clearHourDataIv = (ImageView) view
                        .findViewById(R.id.clearHourDataIv);
                reportHoursItemName.setText(items.getNomItem());

                final android.widget.TextView txtExpand = (android.widget.TextView) view
                        .findViewById(R.id.txtExpand);
                Typeface typeface = Typeface.createFromAsset(
                        jobDetails.getAssets(), jobDetails.getResources()
                                .getString(R.string.fontName_fontAwesome));

                // -------------------NEW CHANGES--------------

                txtExpand.setTypeface(typeface);

                Handler handler = new Handler();
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (reportHoursItemName.getLineCount() > 1) {
                            txtExpand.setVisibility(View.VISIBLE);
                            reportHoursItemName.setSingleLine(true);
                        } else {
                            txtExpand.setVisibility(View.GONE);
                        }
                    }
                });

                txtExpand.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (reportHoursItemName.getLineCount() > 1) {
                            reportHoursItemName.setSingleLine(true);
                            txtExpand.setText(jobDetails
                                    .getString(R.string.fa_expand));
                        } else {
                            reportHoursItemName.setSingleLine(false);
                            txtExpand.setText(jobDetails
                                    .getString(R.string.fa_compress));
                        }
                    }
                });

                // -------------------NEW CHANGES--------------

                final Button dateBtn = (Button) view.findViewById(R.id.dataHour);

                clearHourDataIv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        String comm = "";
                        final int conf = 0;
                        dataAccessObject.updateValue("", comm, items.getIdItem(),
                                idInterv, conf, items.getFlReserve(), items.getNomItem(), items.getIteration());
                        dateBtn.setText("");
                        items.setValeurNet("");
                        reportsJobDetailFragmentHelper.refreshItem(items
                                .getGroupPosition(), childIndex, 0);
                    }
                });

                if (jobDetails.getUpdatedValueOfStatus() != KEYS.JObDetail.JOB__STARTED) {
                    if (dateBtn != null) {
                        dateBtn.setEnabled(false);

                    }
                    view.setEnabled(false);
                } else {
                    if (dateBtn != null) {
                        dateBtn.setEnabled(true);

                    }
                    view.setEnabled(true);
                }

                String val = getValueFormat(items.getValeurNet(),
                        items.getIdTypeItem());

                Date time = null;
                try {
                    time = frenchTimeFormat.parse(val);
                } catch (ParseException e) {
                    Logger.printException(e);
                }

                Format format = android.text.format.DateFormat
                        .getTimeFormat(jobDetails);
                if (time != null) {
                    val = format.format(time);
                }
                dateBtn.setText(val);

                if (items.getOblig() != 0) {

                    if (!TextUtils.isEmpty(dateBtn.getText().toString())) {

                        reportHoursItemName.setTextColor(jobDetails.getResources()
                                .getColor(R.color.text_category_title_color));
                    } else {
                        reportHoursItemName
                                .setTextColor(jobDetails
                                        .getResources()
                                        .getColor(
                                                R.color.textManadatoryFieldsReportsJobDetail));
                    }
                }

                if (!TextUtils.isEmpty(dateBtn.getText())) {
                    if ((jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__COMPLETE)
                            || (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.DEDLINE_EXCEEDED)) {
                        clearHourDataIv.setVisibility(View.GONE);
                    } else {
                        clearHourDataIv.setVisibility(View.VISIBLE);
                    }
                } else {
                    clearHourDataIv.setVisibility(View.GONE);
                }
                dateBtn.setTag(items);

//			dateBtn.setOnClickListener(onClickListener);
                dateBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Item item = (Item) v.getTag();
                        showHours(
                                v,
                                item.getIdItem() + "",
                                getValueFormat(item.getValeurNet(),
                                        item.getIdTypeItem()), item.getNomItem(), item, childIndex);
                    }
                });

                swipeLayout.getSurfaceView().setTag(items);
                swipeLayout.getSurfaceView().setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        dateBtn.performClick();
                    }
                });

                ImageView imgph = (ImageView) view.findViewById(R.id.itemImage);
                if ((photo != null) && (photo.length != 0)) {

//                    final Bitmap btImag = BitmapFactory.decodeByteArray(photo, 0,
//                            photo.length, opt);
//                    // BitmapDrawable bmd = new BitmapDrawable(resiseBitmap(imgph,
//                    // btImag));
//                    imgph.setImageBitmap(btImag);

                    //new changes
                    Glide.with(jobDetails)
                            .load(photo)
                            .asBitmap()
                            .override(200, 200)
                            .fitCenter()
                            .placeholder(R.drawable.library_iicon)
                            .into(imgph);

                    imgph.setVisibility(View.VISIBLE);

                    imgph.setOnClickListener(new OnClickListener() {
                        public void onClick(final View v) {

                            DisplayMetrics displaymetrics = new DisplayMetrics();
                            jobDetails.getWindowManager().getDefaultDisplay()
                                    .getMetrics(displaymetrics);

                            // Bitmap bm2 = dataAccessObject.resizeBitmap(btImag ,
                            // displaymetrics.widthPixels ,
                            // displaymetrics.heightPixels);

//                            Bitmap resized = Bitmap.createScaledBitmap(btImag,
//                                    displaymetrics.widthPixels,
//                                    displaymetrics.heightPixels, true);
//                            showImg(v, btImag);

                            Glide.with(jobDetails)
                                    .load(photo)
                                    .asBitmap()
                                    .fitCenter()
                                    .placeholder(R.drawable.library_iicon)
                                    .into(new SimpleTarget<Bitmap>(displaymetrics.widthPixels, displaymetrics.heightPixels) {
                                        @Override
                                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                            showImg(v, resource);
                                        }
                                    });
                        }
                    });

                } else {
                    imgph.setVisibility(View.GONE);
                }

                android.widget.TextView commentIv = (android.widget.TextView) view
                        .findViewById(R.id.commentIv);
                commentIv.setTypeface(fontAwesome);
                if (gt != null && gt.getFlCommentsRapport() == 0) {
                    commentIv.setVisibility(View.GONE);

                }

                final boolean isWriteable = jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED;

                commentIv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        EnterCommentDialog enterCommentDialog = new EnterCommentDialog(
                                jobDetails,
                                new EnterCommentDialog.EnterDialogInterface() {

                                    @Override
                                    public void doOnModifyClick(String data) {
                                        int conf = 0;
                                        dataAccessObject.updateValue(
                                                getValueFormat(
                                                        items.getValeurNet(),
                                                        items.getIdTypeItem()),
                                                data, items.getIdItem(), idInterv,
                                                conf, items.getFlReserve(), items.getNomItem(), items.getIteration());
                                        reportsJobDetailFragmentHelper
                                                .refreshItem(groupIndex, childIndex, 0);
                                    }

                                    @Override
                                    public void doOnCancelClick() {
                                        hideKeyboard();
                                    }
                                }, jobDetails.getString(R.string.enterComment).toUpperCase(),
                                items.getComItem(), isWriteable);

                        enterCommentDialog.show();

                    }
                });

                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED || jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__COMPLETE) {
                    if (commentIv != null) {
                        commentIv.setEnabled(true);
                    }
                } else {
                    if (commentIv != null) {
                        commentIv.setEnabled(false);
                    }
                }

                int flReserve = items.getFlReserve();
                final int conf = 0;
                /*
                 * New Changes.....
                 *
                 *
                 * Seperate buttons for Status (Complaint, Resolved & Unresolved).
                 * If a status is selected, update the current flag value to DB. If
                 * a status is unselected (ie., Same status is again clicked),
                 * change the flag value as 0 in DB.
                 *
                 * And if the status is changed, change the color of the line strip
                 * in left of sub-category's layout. For Compliant - Orange,
                 * Resolved - Green, Unresolved - Red, if none is selected then
                 * change the color as White
                 */
                final LinearLayout linCompliant = (LinearLayout) view
                        .findViewById(R.id.linCompliantContainer);
                final LinearLayout linResolved = (LinearLayout) view
                        .findViewById(R.id.linResolvedContainer);
                final LinearLayout linUnresolved = (LinearLayout) view
                        .findViewById(R.id.linUnresolvedContainer);

                // set the boxframe for child
                LinearLayout parentLayout = (LinearLayout) view
                        .findViewById(R.id.ParentLayoutReport);

                if (flReserve == 1) {
                    linCompliant
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_compliant_status);
                } else if (flReserve == 2) {
                    linUnresolved
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_unresolved_status);
                } else if (flReserve == 3) {
                    linResolved
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_resolved_status);
                }
                final int i = flReserve;

                // ........................LISTENER...FOR...STATUS...BUTTONS...STARTS...HERE............................
                linCompliant.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (i != 1) {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 1, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        } else {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }
                    }
                });
                linResolved.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (i != 3) {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 3, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        } else {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }
                    }
                });
                linUnresolved.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (i != 2) {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 2, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        } else {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }
                    }
                });

                // ........................LISTENER...FOR...STATUS...BUTTONS...ENDS...HERE............................

                if (jobDetails.getUpdatedValueOfStatus() != KEYS.JObDetail.JOB__STARTED) {
                    linCompliant.setEnabled(false);
                    linResolved.setEnabled(false);
                    linUnresolved.setEnabled(false);
                    swipeLayout.getSurfaceView().setEnabled(false);
                } else {
                    linCompliant.setEnabled(true);
                    linResolved.setEnabled(true);
                    linUnresolved.setEnabled(true);
                    swipeLayout.getSurfaceView().setEnabled(true);
                }

            } else if (itemType == 5) {

                view = layoutInflater.inflate(
                        R.layout.layout_datatype_numberdecimealnote, null);

                android.widget.TextView txtImageAvail = (android.widget.TextView) view
                        .findViewById(R.id.txtImageAvail);
                android.widget.TextView txtBarcodeIcon = (android.widget.TextView) view
                        .findViewById(R.id.txtBarcodeIcon);
                View lineImageAvail = (View) view.findViewById(R.id.viewImageAvail);

                txtImageAvail.setTypeface(fontAwesome);
                txtBarcodeIcon.setTypeface(fontAwesome);
                final byte[] photo = items.getImage();
                // ....................SWIPE...ACTION...STARTS..............................
                SwipeLayout swipeLayout = (SwipeLayout) view
                        .findViewById(R.id.swipeLayoutDataConform);

                // set show mode.
                swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

                // set drag edge.
                // swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
                if ((photo != null) && (photo.length != 0)) {
                    // swipeLayout.setBottomViewIds(R.id.bottom_wrapper_left,
                    // R.id.bottom_wrapper_right, 0, 0);
//				swipeLayout.setDragEdge(DragEdge.Left);
//				swipeLayout.setDragEdge(DragEdge.Right);

                    swipeLayout.addDrag(DragEdge.Left, swipeLayout.findViewById(R.id.bottom_wrapper_left));
                    swipeLayout.addDrag(DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_right));

                    txtImageAvail.setVisibility(View.VISIBLE);
                    lineImageAvail.setVisibility(View.VISIBLE);
                } else {
                    // swipeLayout
                    // .setBottomViewIds(0, R.id.bottom_wrapper_right, 0, 0);
//				swipeLayout.setDragEdge(DragEdge.Right);

                    swipeLayout.addDrag(DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_right));

                    txtImageAvail.setVisibility(View.GONE);
                    lineImageAvail.setVisibility(View.GONE);

                }

                // ***********************SWIPE...ACTION...ENDS******************************

                final TextView reportsNumericTextfieldItemName = (TextView) view
                        .findViewById(R.id.fieldName);
                final TextView noteData = (TextView) view
                        .findViewById(R.id.fieldData);
                noteData.setText(getValueFormat(items.getValeurNet(),
                        items.getIdTypeItem()));

                final android.widget.TextView txtExpand = (android.widget.TextView) view
                        .findViewById(R.id.txtExpand);
                Typeface typeface = Typeface.createFromAsset(
                        jobDetails.getAssets(), jobDetails.getResources()
                                .getString(R.string.fontName_fontAwesome));

                // -------------------NEW CHANGES--------------

                txtExpand.setTypeface(typeface);

                Handler handler = new Handler();
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (reportsNumericTextfieldItemName.getLineCount() > 1) {
                            txtExpand.setVisibility(View.VISIBLE);
                            reportsNumericTextfieldItemName.setSingleLine(true);
                        } else {
                            txtExpand.setVisibility(View.GONE);
                        }
                    }
                });

                txtExpand.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (reportsNumericTextfieldItemName.getLineCount() > 1) {
                            reportsNumericTextfieldItemName.setSingleLine(true);
                            txtExpand.setText(jobDetails
                                    .getString(R.string.fa_expand));
                        } else {
                            reportsNumericTextfieldItemName.setSingleLine(false);
                            txtExpand.setText(jobDetails
                                    .getString(R.string.fa_compress));
                        }
                    }
                });

                swipeLayout.getSurfaceView().setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        noteData.performClick();
                    }
                });
                // -------------------NEW CHANGES--------------

                final int conf = 0;
                reportsNumericTextfieldItemName.setText(items.getNomItem());
                if (items.getOblig() != 0) {

                    if (!TextUtils.isEmpty(noteData.getText().toString())) {

                        reportsNumericTextfieldItemName.setTextColor(jobDetails
                                .getResources().getColor(
                                        R.color.text_category_title_color));
                    } else {

                        reportsNumericTextfieldItemName
                                .setTextColor(jobDetails
                                        .getResources()
                                        .getColor(
                                                R.color.textManadatoryFieldsReportsJobDetail));
                    }
                }

                if (jobDetails.getUpdatedValueOfStatus() != KEYS.JObDetail.JOB__STARTED) {
                    if (noteData != null) {
                        noteData.setEnabled(false);
                        txtBarcodeIcon.setEnabled(false);
                    }

                    view.setEnabled(false);
                } else {
                    if (noteData != null) {
                        noteData.setEnabled(true);
                        txtBarcodeIcon.setEnabled(true);
                    }
                    view.setEnabled(true);
                }

                // ........................LISTENER...FOR...BARCODE...ICON...STARTS...........................
                txtBarcodeIcon.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        reportsJobDetailFragmentHelper
                                .setChildIndexSend(childIndex);
                        reportsJobDetailFragmentHelper
                                .setGroupIndexSend(groupIndex);
                        Intent it = new Intent(jobDetails, CodeScannerActivity.class);
                        reportsJobDetailFragment.startActivityForResult(it,
                                RequestCode.REQUEST_CODE_NUMERIC_BARCODE);
                    }
                });

                // *************************LISTENER...FOR...BARCODE...ICON...ENDS*******************************
                noteData.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        enterDataDialog = new EnterNumericDataDialog(jobDetails,
                                new EnterNumericDataDialog.EnterDialogInterface() {

                                    @Override
                                    public void doOnModifyClick(String data,
                                                                EditText view) {

                                        Logger.log("ExpandableListViewAdapter",
                                                "Called From type 2");
                                        // if (!TextUtils.isEmpty(data)) {
                                        /*
                                         * data = arrondi(Double.parseDouble(data))
                                         * + "";
                                         */

                                        // }

                                        if (((data.trim()).equals("."))
                                                || ((data.trim()).equals("-"))) {

                                            /*
                                             * dataAccessObject.updateValue("" , "",
                                             * items.getIdItem(), idInterv, conf,
                                             * items.getFlReserve());
                                             *
                                             * items.setValeurNet("");
                                             * reportsJobDetailFragmentHelper
                                             * .refreshItem(groupIndex);
                                             *
                                             * DialogUtils.showInfoDialog(
                                             * jobDetails, jobDetails
                                             * .getString(R.string
                                             * .textPleaseEnterCorrectValue));
                                             */

                                            view.setError(jobDetails
                                                    .getString(R.string.textPleaseEnterCorrectValue));
                                            view.requestFocus();

                                        } else {
                                            dataAccessObject.updateValue(
                                                    data.trim(), "",
                                                    items.getIdItem(), idInterv,
                                                    conf, items.getFlReserve(), items.getNomItem(), items.getIteration());

                                            items.setValeurNet(data.trim());
                                            reportsJobDetailFragmentHelper
                                                    .refreshItem(groupIndex, childIndex, 0);

                                            enterDataDialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void doOnCancelClick() {
                                        hideKeyboard();
                                    }
                                }, reportsNumericTextfieldItemName.getText()
                                .toString(), noteData.getText().toString());

                        enterDataDialog.show();

                    }
                });

                ImageView imgph = (ImageView) view.findViewById(R.id.itemImage);
                if ((photo != null) && (photo.length != 0)) {

//                    final Bitmap btImag = BitmapFactory.decodeByteArray(photo, 0,
//                            photo.length, opt);
//                    // BitmapDrawable bmd = new BitmapDrawable(resiseBitmap(imgph,
//                    // btImag));
//                    imgph.setImageBitmap(btImag);

                    //new changes
                    Glide.with(jobDetails)
                            .load(photo)
                            .asBitmap()
                            .override(200, 200)
                            .fitCenter()
                            .placeholder(R.drawable.library_iicon)
                            .into(imgph);


                    imgph.setVisibility(View.VISIBLE);

                    imgph.setOnClickListener(new OnClickListener() {
                        public void onClick(final View v) {

                            DisplayMetrics displaymetrics = new DisplayMetrics();
                            jobDetails.getWindowManager().getDefaultDisplay()
                                    .getMetrics(displaymetrics);

                            // Bitmap bm2 = dataAccessObject.resizeBitmap(btImag ,
                            // displaymetrics.widthPixels ,
                            // displaymetrics.heightPixels);

//                            Bitmap resized = Bitmap.createScaledBitmap(btImag,
//                                    displaymetrics.widthPixels,
//                                    displaymetrics.heightPixels, true);
//                            showImg(v, btImag);

                            Glide.with(jobDetails)
                                    .load(photo)
                                    .asBitmap()
                                    .fitCenter()
                                    .placeholder(R.drawable.library_iicon)
                                    .into(new SimpleTarget<Bitmap>(displaymetrics.widthPixels, displaymetrics.heightPixels) {
                                        @Override
                                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                            showImg(v, resource);
                                        }
                                    });
                        }
                    });

                } else {
                    imgph.setVisibility(View.GONE);
                }

                android.widget.TextView commentIv = (android.widget.TextView) view
                        .findViewById(R.id.commentIv);
                commentIv.setTypeface(fontAwesome);
                if (gt != null && gt.getFlCommentsRapport() == 0) {
                    commentIv.setVisibility(View.GONE);

                }

                final boolean isWriteable = jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED;

                commentIv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        EnterCommentDialog enterCommentDialog = new EnterCommentDialog(
                                jobDetails,
                                new EnterCommentDialog.EnterDialogInterface() {

                                    @Override
                                    public void doOnModifyClick(String data) {
                                        dataAccessObject.updateValue(
                                                getValueFormat(
                                                        items.getValeurNet(),
                                                        items.getIdTypeItem()),
                                                data, items.getIdItem(), idInterv,
                                                conf, items.getFlReserve(), items.getNomItem(), items.getIteration());
                                        reportsJobDetailFragmentHelper
                                                .refreshItem(groupIndex, childIndex, 0);
                                    }

                                    @Override
                                    public void doOnCancelClick() {
                                        hideKeyboard();
                                    }
                                }, jobDetails.getString(R.string.enterComment).toUpperCase(),
                                items.getComItem(), isWriteable);

                        enterCommentDialog.show();

                    }
                });

                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED || jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__COMPLETE) {
                    if (commentIv != null) {
                        commentIv.setEnabled(true);
                    }
                } else {
                    if (commentIv != null) {
                        commentIv.setEnabled(false);
                    }
                }

                int flReserve = items.getFlReserve();
                /*
                 * New Changes.....
                 *
                 *
                 * Seperate buttons for Status (Complaint, Resolved & Unresolved).
                 * If a status is selected, update the current flag value to DB. If
                 * a status is unselected (ie., Same status is again clicked),
                 * change the flag value as 0 in DB.
                 *
                 * And if the status is changed, change the color of the line strip
                 * in left of sub-category's layout. For Compliant - Orange,
                 * Resolved - Green, Unresolved - Red, if none is selected then
                 * change the color as White
                 */
                final LinearLayout linCompliant = (LinearLayout) view
                        .findViewById(R.id.linCompliantContainer);
                final LinearLayout linResolved = (LinearLayout) view
                        .findViewById(R.id.linResolvedContainer);
                final LinearLayout linUnresolved = (LinearLayout) view
                        .findViewById(R.id.linUnresolvedContainer);

                // set the boxframe for child
                LinearLayout parentLayout = (LinearLayout) view
                        .findViewById(R.id.ParentLayoutReport);

                if (flReserve == 1) {
                    linCompliant
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_compliant_status);
                } else if (flReserve == 2) {
                    linUnresolved
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_unresolved_status);
                } else if (flReserve == 3) {
                    linResolved
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_resolved_status);
                }
                final int i = flReserve;

                // ........................LISTENER...FOR...STATUS...BUTTONS...STARTS...HERE............................
                linCompliant.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (i != 1) {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 1, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        } else {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }
                    }
                });
                linResolved.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (i != 3) {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 3, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        } else {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }
                    }
                });
                linUnresolved.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (i != 2) {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 2, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        } else {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }
                    }
                });

                // ........................LISTENER...FOR...STATUS...BUTTONS...ENDS...HERE............................

                if (jobDetails.getUpdatedValueOfStatus() != KEYS.JObDetail.JOB__STARTED) {
                    linCompliant.setEnabled(false);
                    linResolved.setEnabled(false);
                    linUnresolved.setEnabled(false);
                    swipeLayout.getSurfaceView().setEnabled(false);
                } else {
                    linCompliant.setEnabled(true);
                    linResolved.setEnabled(true);
                    linUnresolved.setEnabled(true);
                    swipeLayout.getSurfaceView().setEnabled(true);
                }

            } else if (itemType == 7) {

                view = layoutInflater.inflate(R.layout.layout_datatype_signature,
                        null);

                final TextView signatureItemName = (TextView) view
                        .findViewById(R.id.fieldName);

                signatureItemName.setText(items.getNomItem());

                final android.widget.TextView txtSignatureIcon = (android.widget.TextView) view
                        .findViewById(R.id.txtSignatureIcon);
                final android.widget.TextView txtExpand = (android.widget.TextView) view
                        .findViewById(R.id.txtExpand);
                Typeface typeface = Typeface.createFromAsset(
                        jobDetails.getAssets(), jobDetails.getResources()
                                .getString(R.string.fontName_fontAwesome));

                // -------------------NEW CHANGES--------------

                txtExpand.setTypeface(typeface);

                Handler handler = new Handler();
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (signatureItemName.getLineCount() > 1) {
                            txtExpand.setVisibility(View.VISIBLE);
                            signatureItemName.setSingleLine(true);
                        } else {
                            txtExpand.setVisibility(View.GONE);
                        }
                    }
                });

                txtExpand.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (signatureItemName.getLineCount() > 1) {
                            signatureItemName.setSingleLine(true);
                            txtExpand.setText(jobDetails
                                    .getString(R.string.fa_expand));
                        } else {
                            signatureItemName.setSingleLine(false);
                            txtExpand.setText(jobDetails
                                    .getString(R.string.fa_compress));
                        }
                    }
                });

                // -------------------NEW CHANGES--------------

                LinearLayout customaerSignatureContainer = (LinearLayout) view
                        .findViewById(R.id.customaerSignatureContainer);
                final ImageView signatureIndicator = (ImageView) view
                        .findViewById(R.id.fieldData);

                txtSignatureIcon.setTypeface(fontAwesome);

                if (jobDetails.getUpdatedValueOfStatus() != KEYS.JObDetail.JOB__STARTED) {
                    if (customaerSignatureContainer != null) {
                        customaerSignatureContainer.setEnabled(false);
                        txtSignatureIcon.setEnabled(false);
                    }
                    view.setEnabled(false);
                } else {
                    if (customaerSignatureContainer != null) {
                        customaerSignatureContainer.setEnabled(true);
                        txtSignatureIcon.setEnabled(true);
                    }
                    view.setEnabled(true);
                }

                byte[] retour = dataAccessObject.getPhotoById(idInterv, "SIGN_"
                        + items.getIdItem(), items.getIteration());
                if (retour != null) {
//                    Bitmap bitmap = BitmapFactory.decodeByteArray(retour, 0,
//                            retour.length);
//                    signatureIndicator.setImageBitmap(bitmap);

                    //new changes
                    Glide.with(jobDetails)
                            .load(retour)
                            .asBitmap()
                            .override(200, 200)
                            .fitCenter()
                            .placeholder(R.drawable.library_iicon)
                            .into(signatureIndicator);

                }

                if (items.getOblig() != 0) {

                    if ((retour != null) && (retour.length != 0)) {
                        signatureItemName.setTextColor(jobDetails.getResources()
                                .getColor(R.color.text_category_title_color));
                    } else {

                        signatureItemName
                                .setTextColor(jobDetails
                                        .getResources()
                                        .getColor(
                                                R.color.textManadatoryFieldsReportsJobDetail));
                    }
                }

                android.widget.TextView txtImageAvail = (android.widget.TextView) view
                        .findViewById(R.id.txtImageAvail);
                View lineImageAvail = (View) view.findViewById(R.id.viewImageAvail);

                txtImageAvail.setTypeface(fontAwesome);
                final byte[] photo = items.getImage();
                // ....................SWIPE...ACTION...STARTS..............................
                SwipeLayout swipeLayout = (SwipeLayout) view
                        .findViewById(R.id.swipeLayoutDataConform);

                // set show mode.
                swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

                // set drag edge.
                // swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
                if ((photo != null) && (photo.length != 0)) {
                    // swipeLayout.setBottomViewIds(R.id.bottom_wrapper_left,
                    // R.id.bottom_wrapper_right, 0, 0);
//				swipeLayout.setDragEdge(DragEdge.Left);
//				swipeLayout.setDragEdge(DragEdge.Right);

                    swipeLayout.addDrag(DragEdge.Left, swipeLayout.findViewById(R.id.bottom_wrapper_left));
                    swipeLayout.addDrag(DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_right));

                    txtImageAvail.setVisibility(View.VISIBLE);
                    lineImageAvail.setVisibility(View.VISIBLE);
                } else {
                    // swipeLayout
                    // .setBottomViewIds(0, R.id.bottom_wrapper_right, 0, 0);
//				swipeLayout.setDragEdge(DragEdge.Right);

                    swipeLayout.addDrag(DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_right));

                    txtImageAvail.setVisibility(View.GONE);
                    lineImageAvail.setVisibility(View.GONE);

                }

                // ***********************SWIPE...ACTION...ENDS******************************

                swipeLayout.getSurfaceView().setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        txtSignatureIcon.performClick();
                    }
                });

                txtSignatureIcon.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Bundle bundle = new Bundle();
                        bundle.putInt("id_item", items.getIdItem());
                        bundle.putString("id_interv", idInterv);
                        bundle.putInt("id_user", idUser);
                        bundle.putInt("cd_statut", cd_statut);
                        bundle.putInt("noCom", 0);
                        bundle.putInt("groupIndex", groupIndex);
                        bundle.putInt("childIndex", childIndex);
                        bundle.putInt(KEYS.SharedBlock.ITERATION, items.getIteration());
                        Intent i = new Intent(jobDetails,
                                SignatureFactureItem.class);
                        i.putExtras(bundle);
                        reportsJobDetailFragment.startActivityForResult(i,
                                RequestCode.REQUEST_CODE_SIGNATURE_DIALOG_ITEM);
                    }
                });

                ImageView imgph = (ImageView) view.findViewById(R.id.itemImage);
                if ((photo != null) && (photo.length != 0)) {

//                    final Bitmap btImag = BitmapFactory.decodeByteArray(photo, 0,
//                            photo.length, opt);
//                    // BitmapDrawable bmd = new BitmapDrawable(resiseBitmap(imgph,
//                    // btImag));
//                    imgph.setImageBitmap(btImag);

                    //new changes
                    Glide.with(jobDetails)
                            .load(photo)
                            .asBitmap()
                            .override(200, 200)
                            .fitCenter()
                            .placeholder(R.drawable.library_iicon)
                            .into(imgph);

                    imgph.setVisibility(View.VISIBLE);

                    imgph.setOnClickListener(new OnClickListener() {
                        public void onClick(final View v) {

                            DisplayMetrics displaymetrics = new DisplayMetrics();
                            jobDetails.getWindowManager().getDefaultDisplay()
                                    .getMetrics(displaymetrics);

                            // Bitmap bm2 = dataAccessObject.resizeBitmap(btImag ,
                            // displaymetrics.widthPixels ,
                            // displaymetrics.heightPixels);

//                            Bitmap resized = Bitmap.createScaledBitmap(btImag,
//                                    displaymetrics.widthPixels,
//                                    displaymetrics.heightPixels, true);
//                            showImg(v, btImag);

                            Glide.with(jobDetails)
                                    .load(photo)
                                    .asBitmap()
                                    .fitCenter()
                                    .placeholder(R.drawable.library_iicon)
                                    .into(new SimpleTarget<Bitmap>(displaymetrics.widthPixels, displaymetrics.heightPixels) {
                                        @Override
                                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                            showImg(v, resource);
                                        }
                                    });
                        }
                    });

                } else {
                    imgph.setVisibility(View.GONE);
                }

                android.widget.TextView commentIv = (android.widget.TextView) view
                        .findViewById(R.id.commentIv);
                commentIv.setTypeface(fontAwesome);
                if (gt != null && gt.getFlCommentsRapport() == 0) {
                    commentIv.setVisibility(View.GONE);

                }

                final int conf = 0;

                final boolean isWriteable = jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED;

                commentIv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        EnterCommentDialog enterCommentDialog = new EnterCommentDialog(
                                jobDetails,
                                new EnterCommentDialog.EnterDialogInterface() {

                                    @Override
                                    public void doOnModifyClick(String data) {

                                        dataAccessObject.updateValue(
                                                items.getValeurNet(), data,
                                                items.getIdItem(), idInterv, conf,
                                                items.getFlReserve(), items.getNomItem(), items.getIteration());
                                        reportsJobDetailFragmentHelper
                                                .refreshItem(groupIndex, childIndex, 0);
                                    }

                                    @Override
                                    public void doOnCancelClick() {
                                        hideKeyboard();
                                    }
                                }, jobDetails.getString(R.string.enterComment).toUpperCase(),
                                items.getComItem(), isWriteable);

                        enterCommentDialog.show();

                    }
                });

                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED || jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__COMPLETE) {
                    if (commentIv != null) {
                        commentIv.setEnabled(true);
                    }
                } else {
                    if (commentIv != null) {
                        commentIv.setEnabled(false);
                    }
                }

                int flReserve = items.getFlReserve();
                /*
                 * New Changes.....
                 *
                 *
                 * Seperate buttons for Status (Complaint, Resolved & Unresolved).
                 * If a status is selected, update the current flag value to DB. If
                 * a status is unselected (ie., Same status is again clicked),
                 * change the flag value as 0 in DB.
                 *
                 * And if the status is changed, change the color of the line strip
                 * in left of sub-category's layout. For Compliant - Orange,
                 * Resolved - Green, Unresolved - Red, if none is selected then
                 * change the color as White
                 */
                final LinearLayout linCompliant = (LinearLayout) view
                        .findViewById(R.id.linCompliantContainer);
                final LinearLayout linResolved = (LinearLayout) view
                        .findViewById(R.id.linResolvedContainer);
                final LinearLayout linUnresolved = (LinearLayout) view
                        .findViewById(R.id.linUnresolvedContainer);

                // set the boxframe for child
                LinearLayout parentLayout = (LinearLayout) view
                        .findViewById(R.id.ParentLayoutReport);

                if (flReserve == 1) {
                    linCompliant
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_compliant_status);
                } else if (flReserve == 2) {
                    linUnresolved
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_unresolved_status);
                } else if (flReserve == 3) {
                    linResolved
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_resolved_status);
                }
                final int i = flReserve;

                // ........................LISTENER...FOR...STATUS...BUTTONS...STARTS...HERE............................
                linCompliant.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (i != 1) {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 1, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        } else {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }
                    }
                });
                linResolved.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (i != 3) {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 3, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        } else {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }
                    }
                });
                linUnresolved.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (i != 2) {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 2, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        } else {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }
                    }
                });

                // ........................LISTENER...FOR...STATUS...BUTTONS...ENDS...HERE............................

                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {
                    linCompliant.setEnabled(true);
                    linResolved.setEnabled(true);
                    linUnresolved.setEnabled(true);
                    swipeLayout.getSurfaceView().setEnabled(true);
                } else {
                    linCompliant.setEnabled(false);
                    linResolved.setEnabled(false);
                    linUnresolved.setEnabled(false);
                    swipeLayout.getSurfaceView().setEnabled(false);
                }

            } else if (itemType == 8) {

                view = layoutInflater.inflate(R.layout.layout_photo_type, null);

                android.widget.TextView txtImageAvail = (android.widget.TextView) view
                        .findViewById(R.id.txtImageAvail);
                View lineImageAvail = (View) view.findViewById(R.id.viewImageAvail);

                txtImageAvail.setTypeface(fontAwesome);
                final byte[] photo = items.getImage();
                // ....................SWIPE...ACTION...STARTS..............................
                SwipeLayout swipeLayout = (SwipeLayout) view
                        .findViewById(R.id.swipeLayoutDataConform);

                // set show mode.
                swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

                // set drag edge.
                // swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
                if ((photo != null) && (photo.length != 0)) {
                    // swipeLayout.setBottomViewIds(R.id.bottom_wrapper_left,
                    // R.id.bottom_wrapper_right, 0, 0);
//				swipeLayout.setDragEdge(DragEdge.Left);
//				swipeLayout.setDragEdge(DragEdge.Right);

                    swipeLayout.addDrag(DragEdge.Left, swipeLayout.findViewById(R.id.bottom_wrapper_left));
                    swipeLayout.addDrag(DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_right));

                    txtImageAvail.setVisibility(View.VISIBLE);
                    lineImageAvail.setVisibility(View.VISIBLE);
                } else {
                    // swipeLayout
                    // .setBottomViewIds(0, R.id.bottom_wrapper_right, 0, 0);
//				swipeLayout.setDragEdge(DragEdge.Right);

                    swipeLayout.addDrag(DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_right));

                    txtImageAvail.setVisibility(View.GONE);
                    lineImageAvail.setVisibility(View.GONE);

                }

                // ***********************SWIPE...ACTION...ENDS******************************

                final TextView signatureItemName = (TextView) view
                        .findViewById(R.id.fieldName);

                ImageView removePhotoIconIv = (ImageView) view
                        .findViewById(R.id.removePhotoIconIv);

                signatureItemName.setText(items.getNomItem());

                final android.widget.TextView txtAddPhoto = (android.widget.TextView) view
                        .findViewById(R.id.txtAddPhotoIcon);
                final android.widget.TextView txtExpand = (android.widget.TextView) view
                        .findViewById(R.id.txtExpand);
                Typeface typeface = Typeface.createFromAsset(
                        jobDetails.getAssets(), jobDetails.getResources()
                                .getString(R.string.fontName_fontAwesome));

                // -------------------NEW CHANGES--------------

                txtExpand.setTypeface(typeface);

                Handler handler = new Handler();
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (signatureItemName.getLineCount() > 1) {
                            txtExpand.setVisibility(View.VISIBLE);
                            signatureItemName.setSingleLine(true);
                        } else {
                            txtExpand.setVisibility(View.GONE);
                        }
                    }
                });

                txtExpand.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (signatureItemName.getLineCount() > 1) {
                            signatureItemName.setSingleLine(true);
                            txtExpand.setText(jobDetails
                                    .getString(R.string.fa_expand));
                        } else {
                            signatureItemName.setSingleLine(false);
                            txtExpand.setText(jobDetails
                                    .getString(R.string.fa_compress));
                        }
                    }
                });

                swipeLayout.getSurfaceView().setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        txtAddPhoto.performClick();
                    }
                });

                // -------------------NEW CHANGES--------------

                final int conf = 0;

                ImageView viewAttachmentInBrowser = (ImageView) view
                        .findViewById(R.id.viewAttachmentInBrowser);

                txtAddPhoto.setTypeface(fontAwesome);

                txtAddPhoto.setTag("ChildView");
                final byte[] photoImg = items.getPhotoImg();

                if (items.getOblig() != 0) {

                    if ((photoImg != null) && (photoImg.length != 0)) {
                        signatureItemName.setTextColor(jobDetails.getResources()
                                .getColor(R.color.text_category_title_color));
                    } else {

                        signatureItemName
                                .setTextColor(jobDetails
                                        .getResources()
                                        .getColor(
                                                R.color.textManadatoryFieldsReportsJobDetail));
                    }
                }

                if ((photoImg != null) && (photoImg.length != 0)) {

//                    final Bitmap btImag = BitmapFactory.decodeByteArray(photoImg,
//                            0, photoImg.length, opt);
//                    viewAttachmentInBrowser.setImageBitmap(btImag);

                    //new changes
                    Glide.with(jobDetails)
                            .load(photoImg)
                            .asBitmap()
                            .override(200, 200)
                            .fitCenter()
                            .placeholder(R.drawable.library_iicon)
                            .into(viewAttachmentInBrowser);

                    removePhotoIconIv.setVisibility(View.VISIBLE);

                } else {
                    removePhotoIconIv.setVisibility(View.GONE);
                }

                removePhotoIconIv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        String cmtr = "PIC_" + items.getIdItem();
                        deletePhoto(cmtr, idInterv, items.getIdItem(), groupIndex, childIndex, items.getIteration());

                    }
                });

                if (jobDetails.getUpdatedValueOfStatus() != KEYS.JObDetail.JOB__STARTED) {
                    if (removePhotoIconIv != null) {
                        removePhotoIconIv.setEnabled(false);
                    }

                    view.setEnabled(false);
                } else {
                    if (removePhotoIconIv != null) {
                        removePhotoIconIv.setEnabled(true);
                    }
                    view.setEnabled(true);
                }

                txtAddPhoto.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {
                            if ((photoImg != null) && (photoImg.length != 0)) {

                            } else {

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE ){// Andsroid 14 changes
                                    if (ContextCompat.checkSelfPermission(jobDetails, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) != PackageManager.PERMISSION_GRANTED){
                                        reportsJobDetailFragment.callingPermissionCameraFromAdapter();
                                    }else {
                                        reportsJobDetailFragment.cameraPermission();
                                        AttachmentDialogNew attachmentDialog = new AttachmentDialogNew(
                                                jobDetails, reportsJobDetailFragment,
                                                reportsJobDetailFragmentHelper, v.getTag()
                                                .toString(), groupIndex, childIndex);
                                        attachmentDialog.show();
                                    }
                                }else

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                                    if (ContextCompat.checkSelfPermission(jobDetails, Manifest.permission.READ_MEDIA_IMAGES) !=
                                            PackageManager.PERMISSION_GRANTED) {
                                        reportsJobDetailFragment.callingPermissionCameraFromAdapter();
                                    }else {
                                        reportsJobDetailFragment.cameraPermission();
                                        AttachmentDialogNew attachmentDialog = new AttachmentDialogNew(
                                                jobDetails, reportsJobDetailFragment,
                                                reportsJobDetailFragmentHelper, v.getTag()
                                                .toString(), groupIndex, childIndex);
                                        attachmentDialog.show();                                    }
                                } else
                                if (ContextCompat.checkSelfPermission(jobDetails, Manifest.permission.READ_EXTERNAL_STORAGE)
                                        != PackageManager.PERMISSION_GRANTED ||
                                        ContextCompat.checkSelfPermission(jobDetails, Manifest.permission.CAMERA)
                                                != PackageManager.PERMISSION_GRANTED ||
                                        ContextCompat.checkSelfPermission(jobDetails, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                != PackageManager.PERMISSION_GRANTED) {
                                    reportsJobDetailFragment.callingPermissionCameraFromAdapter();

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                                        if (ContextCompat.checkSelfPermission(jobDetails, Manifest.permission.READ_EXTERNAL_STORAGE)
                                                != PackageManager.PERMISSION_GRANTED ||
                                                ContextCompat.checkSelfPermission(jobDetails, Manifest.permission.CAMERA)
                                                        != PackageManager.PERMISSION_GRANTED) {
                                            reportsJobDetailFragment.callingPermissionCameraFromAdapter();
                                        } else {
                                            AttachmentDialogNew attachmentDialog = new AttachmentDialogNew(
                                                    jobDetails, reportsJobDetailFragment,
                                                    reportsJobDetailFragmentHelper, v.getTag()
                                                    .toString(), groupIndex, childIndex);
                                            attachmentDialog.show();
                                        }

                                    } else {
                                        reportsJobDetailFragment.callingPermissionCameraFromAdapter();
                                    }

                                } else {
                                    AttachmentDialogNew attachmentDialog = new AttachmentDialogNew(
                                            jobDetails, reportsJobDetailFragment,
                                            reportsJobDetailFragmentHelper, v.getTag()
                                            .toString(), groupIndex, childIndex);
                                    attachmentDialog.show();
                                }

                            }
                        }

                    }
                });

                viewAttachmentInBrowser.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if ((photoImg != null) && (photoImg.length != 0)) {

//                        final Bitmap btImag = BitmapFactory.decodeByteArray(
//                                photoImg, 0, photoImg.length, opt);
//                        showImg(v, btImag);

                            String imgComment = "PIC_" + items.getIdItem();

                            Intent intent = new Intent(jobDetails, EditReportImage.class);
                            intent.putExtra(KEYS.CurrentJobs.ID, idInterv);
                            intent.putExtra(KEYS.Photos.KEY_COMMENT, imgComment);
                            intent.putExtra(KEYS.SharedBlock.ITERATION, items.getIteration());
                            intent.putExtra(KEYS.EditImage.KEY_ATTACHEMENT_IMAGE_ID, KEYS.EditImage.KEY_IMAGE_FROM_REPORTS_LIST_PHOTO_TYPE);

                            jobDetails.startActivity(intent);
                        }

                    }
                });

                ImageView imgph = (ImageView) view.findViewById(R.id.itemImage);
                if ((photo != null) && (photo.length != 0)) {

//                    final Bitmap btImag = BitmapFactory.decodeByteArray(photo, 0,
//                            photo.length, opt);
//                    // BitmapDrawable bmd = new BitmapDrawable(resiseBitmap(imgph,
//                    // btImag));
//                    imgph.setImageBitmap(btImag);

                    //new changes
                    Glide.with(jobDetails)
                            .load(photo)
                            .asBitmap()
                            .override(200, 200)
                            .fitCenter()
                            .placeholder(R.drawable.library_iicon)
                            .into(imgph);

                    imgph.setVisibility(View.VISIBLE);

                    imgph.setOnClickListener(new OnClickListener() {
                        public void onClick(final View v) {

                            DisplayMetrics displaymetrics = new DisplayMetrics();
                            jobDetails.getWindowManager().getDefaultDisplay()
                                    .getMetrics(displaymetrics);

                            // Bitmap bm2 = dataAccessObject.resizeBitmap(btImag ,
                            // displaymetrics.widthPixels ,
                            // displaymetrics.heightPixels);

//                            Bitmap resized = Bitmap.createScaledBitmap(btImag,
//                                    displaymetrics.widthPixels,
//                                    displaymetrics.heightPixels, true);
//                            showImg(v, btImag);

                            Glide.with(jobDetails)
                                    .load(photo)
                                    .asBitmap()
                                    .fitCenter()
                                    .placeholder(R.drawable.library_iicon)
                                    .into(new SimpleTarget<Bitmap>(displaymetrics.widthPixels, displaymetrics.heightPixels) {
                                        @Override
                                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                            showImg(v, resource);
                                        }
                                    });
                        }
                    });

                } else {
                    imgph.setVisibility(View.GONE);
                }

                android.widget.TextView commentIv = (android.widget.TextView) view
                        .findViewById(R.id.commentIv);
                commentIv.setTypeface(fontAwesome);
                if (gt != null && gt.getFlCommentsRapport() == 0) {
                    commentIv.setVisibility(View.GONE);
                }

                final boolean isWriteable = jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED;

                commentIv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        EnterCommentDialog enterCommentDialog = new EnterCommentDialog(
                                jobDetails,
                                new EnterCommentDialog.EnterDialogInterface() {

                                    @Override
                                    public void doOnModifyClick(String data) {
                                        dataAccessObject.updateValue(
                                                getValueFormat(
                                                        items.getValeurNet(),
                                                        items.getIdTypeItem()),
                                                data, items.getIdItem(), idInterv,
                                                conf, items.getFlReserve(), items.getNomItem(), items.getIteration());
                                        reportsJobDetailFragmentHelper
                                                .refreshItem(groupIndex, childIndex, 0);
                                    }

                                    @Override
                                    public void doOnCancelClick() {
                                        hideKeyboard();
                                    }
                                }, jobDetails.getString(R.string.enterComment).toUpperCase(),
                                items.getComItem(), isWriteable);

                        enterCommentDialog.show();

                    }
                });

                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED || jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__COMPLETE) {
                    if (commentIv != null) {
                        commentIv.setEnabled(true);
                    }
                } else {
                    if (commentIv != null) {
                        commentIv.setEnabled(false);
                    }
                }

                int flReserve = items.getFlReserve();
                /*
                 * New Changes.....
                 *
                 *
                 * Seperate buttons for Status (Complaint, Resolved & Unresolved).
                 * If a status is selected, update the current flag value to DB. If
                 * a status is unselected (ie., Same status is again clicked),
                 * change the flag value as 0 in DB.
                 *
                 * And if the status is changed, change the color of the line strip
                 * in left of sub-category's layout. For Compliant - Orange,
                 * Resolved - Green, Unresolved - Red, if none is selected then
                 * change the color as White
                 */
                final LinearLayout linCompliant = (LinearLayout) view
                        .findViewById(R.id.linCompliantContainer);
                final LinearLayout linResolved = (LinearLayout) view
                        .findViewById(R.id.linResolvedContainer);
                final LinearLayout linUnresolved = (LinearLayout) view
                        .findViewById(R.id.linUnresolvedContainer);

                // set the boxframe for child
                RelativeLayout parentLayout = (RelativeLayout) view
                        .findViewById(R.id.ParentLayoutReport);

                if (flReserve == 1) {
                    linCompliant
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_compliant_status);
                } else if (flReserve == 2) {
                    linUnresolved
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_unresolved_status);
                } else if (flReserve == 3) {
                    linResolved
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_resolved_status);
                }
                final int i = flReserve;

                // ........................LISTENER...FOR...STATUS...BUTTONS...STARTS...HERE............................
                linCompliant.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (i != 1) {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 1, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        } else {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }
                    }
                });
                linResolved.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (i != 3) {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 3, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        } else {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }
                    }
                });
                linUnresolved.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (i != 2) {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 2, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        } else {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }
                    }
                });

                // ........................LISTENER...FOR...STATUS...BUTTONS...ENDS...HERE............................

                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {
                    linCompliant.setEnabled(true);
                    linResolved.setEnabled(true);
                    linUnresolved.setEnabled(true);
                    swipeLayout.getSurfaceView().setEnabled(true);
                } else {
                    linCompliant.setEnabled(false);
                    linResolved.setEnabled(false);
                    linUnresolved.setEnabled(false);
                    swipeLayout.getSurfaceView().setEnabled(false);
                }
            }

            /*
             * New report item for getting latitude, longitude and address of the
             * user. (Type = 9 & Type name is "Localization")
             */
            else if (itemType == 9) {
                view = layoutInflater.inflate(R.layout.layout_datatype_localiztion,
                        null);

                android.widget.TextView txtImageAvail = (android.widget.TextView) view
                        .findViewById(R.id.txtImageAvail);
                final ImageView imgCaptureAddress = (ImageView) view
                        .findViewById(R.id.mapAddressIcon);
                View lineImageAvail = (View) view.findViewById(R.id.viewImageAvail);

                txtImageAvail.setTypeface(fontAwesome);
                final byte[] photo = items.getImage();
                // ....................SWIPE...ACTION...STARTS..............................
                SwipeLayout swipeLayout = (SwipeLayout) view
                        .findViewById(R.id.swipeLayoutDataLocalization);

                // set show mode.
                swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

                // set drag edge.
                // swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
                if ((photo != null) && (photo.length != 0)) {
                    // swipeLayout.setBottomViewIds(R.id.bottom_wrapper_left,
                    // R.id.bottom_wrapper_right, 0, 0);
//				swipeLayout.setDragEdge(DragEdge.Left);
//				swipeLayout.setDragEdge(DragEdge.Right);

                    swipeLayout.addDrag(DragEdge.Left, swipeLayout.findViewById(R.id.bottom_wrapper_left));
                    swipeLayout.addDrag(DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_right));

                    txtImageAvail.setVisibility(View.VISIBLE);
                    lineImageAvail.setVisibility(View.VISIBLE);
                } else {
                    // swipeLayout
                    // .setBottomViewIds(0, R.id.bottom_wrapper_right, 0, 0);
//				swipeLayout.setDragEdge(DragEdge.Right);

                    swipeLayout.addDrag(DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_right));

                    txtImageAvail.setVisibility(View.GONE);
                    lineImageAvail.setVisibility(View.GONE);

                }

                // ***********************SWIPE...ACTION...ENDS******************************

                final TextView reportsTextFiledItemName = (TextView) view
                        .findViewById(R.id.fieldName);
                final TextView noteData = (TextView) view
                        .findViewById(R.id.fieldLocalization);

                // ................SET...LAT...LONG..&..ADDRESS...TO...TEXTVIEW.........................................
                /*
                 * Get the whole address from DB. And seperate the lat, long &
                 * address from the string then. Then apply different colors for
                 * lat,long & address
                 */
                String localizedAddress = getValueFormat(items.getValeurNet(),
                        items.getIdTypeItem());

                /*
                 * Logic to find out the second pipe symbol in the address. To get
                 * the position of the end index of longitude from the address
                 * string. "pos" is the end index of the longitude.
                 */
                int counter = 0;
                int pos = 0;
                for (int i = 0; i < localizedAddress.length(); i++) {
                    if (localizedAddress.charAt(i) == '|') {
                        counter++;
                        if (counter == 2) {
                            pos = i;
                        }
                    }
                }

                if (localizedAddress != null && localizedAddress.length() > 0) {

                    try {
                        String strLat = localizedAddress.substring(0,
                                localizedAddress.indexOf("|"));
                        String strLong = localizedAddress.substring(
                                strLat.length() + 1, pos);
                        String strAddress = localizedAddress.substring(pos + 1);

                        /*
                         * html format to apply two set of colors to lat,log & address.
                         */
                        String localizedAddressWithColorSpan = "<font color=#9E9E9E>"
                                + strLat + "</font>" + " <font color=#7F7F7F>|</font>"
                                + " <font color=#9E9E9E>" + strLong + "</font>"
                                + " <font color=#7F7F7F>|</font>"
                                + " <font color=#7F7F7F><i>" + strAddress
                                + "</i></font>";

                        noteData.setText(Html.fromHtml(localizedAddressWithColorSpan));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                // ****************SET...LAT...LONG..&..ADDRESS...TO...TEXTVIEW**********************************
                final int conf = 0;
                reportsTextFiledItemName.setText(items.getNomItem());
                final android.widget.TextView txtExpand = (android.widget.TextView) view
                        .findViewById(R.id.txtExpand);
//                setFATypeFace(txtExpand);
//                Typeface typeface = Typeface.createFromAsset(
//                        jobDetails.getAssets(), jobDetails.getResources()
//                                .getString(R.string.fontName_fontAwesome));
                //                txtExpand.setTypeface(typeface);


                // -------------------NEW CHANGES--------------


                Handler handler = new Handler();
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (reportsTextFiledItemName.getLineCount() > 1) {
                            txtExpand.setVisibility(View.VISIBLE);
                            reportsTextFiledItemName.setSingleLine(true);
                        } else {
                            txtExpand.setVisibility(View.GONE);
                        }
                    }
                });

                txtExpand.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (reportsTextFiledItemName.getLineCount() > 1) {
                            reportsTextFiledItemName.setSingleLine(true);
                            txtExpand.setText(jobDetails
                                    .getString(R.string.fa_expand));
                        } else {
                            reportsTextFiledItemName.setSingleLine(false);
                            txtExpand.setText(jobDetails
                                    .getString(R.string.fa_compress));
                        }
                    }
                });

                swipeLayout.getSurfaceView().setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        imgCaptureAddress.performClick();
                    }
                });

                // -------------------NEW CHANGES--------------

                if (!TextUtils.isEmpty(noteData.getText().toString())) {
                    imgCaptureAddress.setImageResource(R.drawable.map_green);
                } else {
                    imgCaptureAddress
                            .setImageResource(R.drawable.map_org_icon_selected1);
                }
                final int grpPos = groupPosition;
                final int childPos = childPosition;
                grpIndex = groupIndex;
                chdIndex = childIndex;
                imgCaptureAddress.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        geocoder(v, grpPos, childPos, grpIndex);
                    }
                });

                if (items.getOblig() != 0) {

                    if (!TextUtils.isEmpty(noteData.getText().toString())) {

                        reportsTextFiledItemName.setTextColor(jobDetails
                                .getResources().getColor(
                                        R.color.text_category_title_color));
                    } else {
                        reportsTextFiledItemName
                                .setTextColor(jobDetails
                                        .getResources()
                                        .getColor(
                                                R.color.textManadatoryFieldsReportsJobDetail));
                    }
                }

                if (jobDetails.getUpdatedValueOfStatus() != KEYS.JObDetail.JOB__STARTED) {
                    if (noteData != null) {
                        imgCaptureAddress.setEnabled(false);
                        swipeLayout.getSurfaceView().setEnabled(false);
                    }

                    view.setEnabled(false);
                } else {
                    if (noteData != null) {
                        imgCaptureAddress.setEnabled(true);
                        swipeLayout.getSurfaceView().setEnabled(true);
                    }
                    view.setEnabled(true);
                }

                ImageView imgph = (ImageView) view.findViewById(R.id.itemImage);
                if ((photo != null) && (photo.length != 0)) {

//                    final Bitmap btImag = BitmapFactory.decodeByteArray(photo, 0,
//                            photo.length, opt);
//                    // BitmapDrawable bmd = new BitmapDrawable(resiseBitmap(imgph,
//                    // btImag));
//                    imgph.setImageBitmap(btImag);

                    //new changes
                    Glide.with(jobDetails)
                            .load(photo)
                            .asBitmap()
                            .override(200, 200)
                            .fitCenter()
                            .placeholder(R.drawable.library_iicon)
                            .into(imgph);

                    imgph.setVisibility(View.VISIBLE);

                    imgph.setOnClickListener(new OnClickListener() {
                        public void onClick(final View v) {

                            DisplayMetrics displaymetrics = new DisplayMetrics();
                            jobDetails.getWindowManager().getDefaultDisplay()
                                    .getMetrics(displaymetrics);

                            // Bitmap bm2 = dataAccessObject.resizeBitmap(btImag ,
                            // displaymetrics.widthPixels ,
                            // displaymetrics.heightPixels);

//                            Bitmap resized = Bitmap.createScaledBitmap(btImag,
//                                    displaymetrics.widthPixels,
//                                    displaymetrics.heightPixels, true);
//                            showImg(v, btImag);

                            Glide.with(jobDetails)
                                    .load(photo)
                                    .asBitmap()
                                    .fitCenter()
                                    .placeholder(R.drawable.library_iicon)
                                    .into(new SimpleTarget<Bitmap>(displaymetrics.widthPixels, displaymetrics.heightPixels) {
                                        @Override
                                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                            showImg(v, resource);
                                        }
                                    });
                        }
                    });

                } else {
                    imgph.setVisibility(View.GONE);
                }

                android.widget.TextView commentIv = (android.widget.TextView) view
                        .findViewById(R.id.commentIv);
                commentIv.setTypeface(fontAwesome);
                if (gt != null && gt.getFlCommentsRapport() == 0) {
                    commentIv.setVisibility(View.GONE);

                }

                final boolean isWriteable = jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED;

                commentIv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        EnterCommentDialog enterCommentDialog = new EnterCommentDialog(
                                jobDetails,
                                new EnterCommentDialog.EnterDialogInterface() {

                                    @Override
                                    public void doOnModifyClick(String data) {
                                        dataAccessObject.updateValue(
                                                getValueFormat(
                                                        items.getValeurNet(),
                                                        items.getIdTypeItem()),
                                                data, items.getIdItem(), idInterv,
                                                conf, items.getFlReserve(), items.getNomItem(), items.getIteration());
                                        reportsJobDetailFragmentHelper
                                                .refreshItem(groupIndex, childIndex, 0);
                                    }

                                    @Override
                                    public void doOnCancelClick() {
                                        hideKeyboard();
                                    }
                                }, jobDetails.getString(R.string.enterComment).toUpperCase(),
                                items.getComItem(), isWriteable);

                        enterCommentDialog.show();

                    }
                });

                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED || jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__COMPLETE) {
                    if (commentIv != null) {
                        commentIv.setEnabled(true);
                    }
                } else {
                    if (commentIv != null) {
                        commentIv.setEnabled(false);
                    }
                }

                int flReserve = items.getFlReserve();
                /*
                 * New Changes.....
                 *
                 *
                 * Seperate buttons for Status (Complaint, Resolved & Unresolved).
                 * If a status is selected, update the current flag value to DB. If
                 * a status is unselected (ie., Same status is again clicked),
                 * change the flag value as 0 in DB.
                 *
                 * And if the status is changed, change the color of the line strip
                 * in left of sub-category's layout. For Compliant - Orange,
                 * Resolved - Green, Unresolved - Red, if none is selected then
                 * change the color as White
                 */
                final LinearLayout linCompliant = (LinearLayout) view
                        .findViewById(R.id.linCompliantContainer);
                final LinearLayout linResolved = (LinearLayout) view
                        .findViewById(R.id.linResolvedContainer);
                final LinearLayout linUnresolved = (LinearLayout) view
                        .findViewById(R.id.linUnresolvedContainer);

                // set the boxframe for child
                LinearLayout parentLayout = (LinearLayout) view
                        .findViewById(R.id.ParentLayoutReport);

                if (flReserve == 1) {
                    linCompliant
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_compliant_status);
                } else if (flReserve == 2) {
                    linUnresolved
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_unresolved_status);
                } else if (flReserve == 3) {
                    linResolved
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_resolved_status);
                }
                final int i = flReserve;

                // ........................LISTENER...FOR...STATUS...BUTTONS...STARTS...HERE............................
                linCompliant.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (i != 1) {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 1, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        } else {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }
                    }
                });
                linResolved.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (i != 3) {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 3, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        } else {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }
                    }
                });
                linUnresolved.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (i != 2) {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 2, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        } else {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }
                    }
                });

                // ........................LISTENER...FOR...STATUS...BUTTONS...ENDS...HERE............................

                if (jobDetails.getUpdatedValueOfStatus() != KEYS.JObDetail.JOB__STARTED) {
                    linCompliant.setEnabled(false);
                    linResolved.setEnabled(false);
                    linUnresolved.setEnabled(false);

                } else {
                    linCompliant.setEnabled(true);
                    linResolved.setEnabled(true);
                    linUnresolved.setEnabled(true);
                }

                // show the view as divider, if it is a last child
                if (isLastChild) {
                    View dividerLine = (View) view.findViewById(R.id.dividerView);
                    dividerLine.setVisibility(View.VISIBLE);
                }

            } else if (itemType == 10) {

                final ArrayList<String> listValues = dataAccessObject
                        .getItemValues(items.getIdItem() + "");
//            listValues.add(0, jobDetails.getString(R.string.textNoneLable));
                boolean charMorethan15 = false;
                for (int i = 0; i < listValues.size(); i++) {
                    if (listValues.get(i).length() > 15) {
                        charMorethan15 = true;
                    }
                }

                if (listValues.size() > 3
                        || (listValues.size() <= 3 && charMorethan15)) {

                    view = layoutInflater.inflate(
                            R.layout.layout_datatype_multiselect, null);
                    android.widget.TextView txtImageAvail = (android.widget.TextView) view
                            .findViewById(R.id.txtImageAvail);
                    View lineImageAvail = (View) view
                            .findViewById(R.id.viewImageAvail);

                    txtImageAvail.setTypeface(fontAwesome);
                    final byte[] photo = items.getImage();
                    // ....................SWIPE...ACTION...STARTS..............................
                    SwipeLayout swipeLayout = (SwipeLayout) view
                            .findViewById(R.id.swipeLayoutDataConform);

                    // set show mode.
                    swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

                    // set drag edge.
                    // swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
                    if ((photo != null) && (photo.length != 0)) {
                        // swipeLayout.setBottomViewIds(R.id.bottom_wrapper_left,
                        // R.id.bottom_wrapper_right, 0, 0);
//					swipeLayout.setDragEdge(DragEdge.Left);
//					swipeLayout.setDragEdge(DragEdge.Right);

                        swipeLayout.addDrag(DragEdge.Left, swipeLayout.findViewById(R.id.bottom_wrapper_left));
                        swipeLayout.addDrag(DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_right));

                        txtImageAvail.setVisibility(View.VISIBLE);
                        lineImageAvail.setVisibility(View.VISIBLE);
                    } else {
                        // swipeLayout.setBottomViewIds(0,
                        // R.id.bottom_wrapper_right,
                        // 0, 0);
//					swipeLayout.setDragEdge(DragEdge.Right);

                        swipeLayout.addDrag(DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_right));

                        txtImageAvail.setVisibility(View.GONE);
                        lineImageAvail.setVisibility(View.GONE);

                    }

                    // ***********************SWIPE...ACTION...ENDS******************************

                    final TextView reportListOfValuesItemName = (TextView) view
                            .findViewById(R.id.fieldName);
                    final RelativeLayout listOfValues = (RelativeLayout) view
                            .findViewById(R.id.relSerialContainer);
                    reportListOfValuesItemName.setText(items.getNomItem());

                    final android.widget.TextView txtExpand = (android.widget.TextView) view
                            .findViewById(R.id.txtExpand);
                    Typeface typeface = Typeface.createFromAsset(
                            jobDetails.getAssets(),
                            jobDetails.getResources().getString(
                                    R.string.fontName_fontAwesome));

                    // -------------------NEW CHANGES--------------

                    txtExpand.setTypeface(typeface);

                    Handler handler = new Handler();
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            if (reportListOfValuesItemName.getLineCount() > 1) {
                                txtExpand.setVisibility(View.VISIBLE);
                                reportListOfValuesItemName.setSingleLine(true);
                            } else {
                                txtExpand.setVisibility(View.GONE);
                            }
                        }
                    });

                    txtExpand.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            if (reportListOfValuesItemName.getLineCount() > 1) {
                                reportListOfValuesItemName.setSingleLine(true);
                                txtExpand.setText(jobDetails
                                        .getString(R.string.fa_expand));
                            } else {
                                reportListOfValuesItemName.setSingleLine(false);
                                txtExpand.setText(jobDetails
                                        .getString(R.string.fa_compress));
                            }
                        }
                    });

                    swipeLayout.getSurfaceView().setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            listOfValues.performClick();
                        }
                    });

                    // -------------------NEW CHANGES--------------

                    // .......NEW...CHANGES.......
                    // reportListOfValuesItemName
                    // .setOnClickListener(new OnClickListener() {
                    //
                    // @Override
                    // public void onClick(View v) {
                    // TextView ttt = (TextView) v;
                    // ttt.setSingleLine(false);
                    // ttt.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                    // }
                    // });

                    if (jobDetails.getUpdatedValueOfStatus() != KEYS.JObDetail.JOB__STARTED) {
                        if (listOfValues != null) {
                            listOfValues.setEnabled(false);

                        }
                        view.setEnabled(false);

                    } else {
                        if (listOfValues != null) {
                            listOfValues.setEnabled(true);

                        }
                        view.setEnabled(true);
                    }

                    final String[] tab = new String[listValues.size()];

                    final int conf = 0;

                    String[] itemValeurNetSelected = new String[0];

                    if (items.getValeurNet().length() > 0) {
                        itemValeurNetSelected = items.getValeurNet().trim().split("@@@");
                    }

                    // ----------------------- updating views in layout ----------------------------

                    final ArrayList<String> listSel = new ArrayList<>();

                    for (int i = 0; i < itemValeurNetSelected.length; i++) {
                        listSel.add(itemValeurNetSelected[i]);
                    }

                    listOfValues.removeAllViews();


                    for (int i = 0; i < listSel.size(); i++) {

                        android.widget.TextView tv = new android.widget.TextView(jobDetails);
                        Typeface typeFaceAvenir = Typeface.createFromAsset(
                                jobDetails.getAssets(),
                                jobDetails.getResources().getString(
                                        R.string.fontName_avenir));
                        tv.setText(listSel.get(i));
                        tv.setBackgroundDrawable(jobDetails.getResources().getDrawable(
                                R.drawable.boxframe_serial_not_text));

                        tv.setId(i + 1);
                        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, jobDetails.getResources()
                                .getDimension(R.dimen.textSizeDaysTv));
                        tv.setSingleLine(true);
                        tv.setPadding(10, 5, 10, 5);
                        tv.setTypeface(typeFaceAvenir);
                        listOfValues.setVisibility(View.INVISIBLE);
                        listOfValues.addView(tv);
                    }

                    final android.widget.TextView tvTruncated = new android.widget.TextView(jobDetails);
                    tvTruncated.setText("...");
                    tvTruncated.setBackgroundDrawable(jobDetails.getResources().getDrawable(
                            R.drawable.boxframe_serial_not_text));

                    tvTruncated.setTextSize(TypedValue.COMPLEX_UNIT_PX, jobDetails.getResources()
                            .getDimension(R.dimen.textSizeDaysTv));
                    tvTruncated.setSingleLine(true);
                    tvTruncated.setPadding(10, 5, 10, 5);

                    listOfValues.post(new Runnable() {
                        @Override
                        public void run() {
                            int totalWidth = listOfValues.getWidth();
                            int startRemovePosition = 0;
                            boolean haveNextLine = false;

                            // loop through each text view, and set its layout
                            // params
                            for (int i = 0; i < listSel.size(); i++) {
                                View child = listOfValues.getChildAt(i);
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                                params.setMargins(10, 0, 0, 0);
                                // this text view can fit in the same row so
                                // lets place it relative to the previous one.
                                if (child.getWidth() < totalWidth) {

                                    if (i > 0) { // i == 0 is in correct
                                        // position
                                        params.addRule(RelativeLayout.RIGHT_OF,
                                                listOfValues.getChildAt(i - 1)
                                                        .getId());
                                        params.addRule(RelativeLayout.ALIGN_BOTTOM,
                                                listOfValues.getChildAt(i - 1)
                                                        .getId());

                                    }
                                } else {
                                    if (!haveNextLine) {
                                        startRemovePosition = i;
                                        haveNextLine = true;
                                        break;
                                    }
                                }
                                child.setLayoutParams(params);
                                totalWidth = totalWidth - child.getWidth() - 10;
                            }

                            if (haveNextLine) {
                                for (int i = listOfValues.getChildCount(); i > startRemovePosition; i--) {
                                    listOfValues.removeViewAt(i - 1);
                                }

                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                                        ViewGroup.LayoutParams.WRAP_CONTENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                                params.setMargins(10, 0, 0, 0);

                                //TODO check for null pointer
                                if (listOfValues.getChildAt(listOfValues.getChildCount() - 1) != null &&
                                        listOfValues.getChildAt(listOfValues.getChildCount() - 1)
                                                .getId() != 0) {
                                    params.addRule(RelativeLayout.RIGHT_OF,
                                            listOfValues.getChildAt(listOfValues.getChildCount() - 1)
                                                    .getId());
                                    params.addRule(RelativeLayout.ALIGN_BOTTOM,
                                            listOfValues.getChildAt(listOfValues.getChildCount() - 1)
                                                    .getId());

                                    tvTruncated.setLayoutParams(params);
                                }

                                if (tvTruncated != null)
                                    listOfValues.addView(tvTruncated);
                            }

                            listOfValues.setVisibility(View.VISIBLE);
                            listOfValues.requestLayout();

                        }
                    });


                    // ------------- list values updated -----------------//


                    if (items.getOblig() != 0) {

                        // listOfValues.getText().toString()

                        if (!TextUtils.isEmpty(items.getValeurNet().trim())) {
                            reportListOfValuesItemName.setTextColor(jobDetails
                                    .getResources().getColor(
                                            R.color.text_category_title_color));
                        } else {
                            reportListOfValuesItemName
                                    .setTextColor(jobDetails
                                            .getResources()
                                            .getColor(
                                                    R.color.textManadatoryFieldsReportsJobDetail));

                        }
                    }


                    final String[] finalItemValeurNetSelected = itemValeurNetSelected;
                    listOfValues.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            Log.e("Selected List", "" + finalItemValeurNetSelected.length);

                            ReportsMultiSelectDialogNew reportsMultiSelectDialog = new ReportsMultiSelectDialogNew(jobDetails, items.getNomItem(), listValues, finalItemValeurNetSelected, new ReportsMultiSelectDialogNew.SaveButtonClickListener() {
                                @Override
                                public void saveButtonClicked(ArrayList<String> arrayList) {

                                    Log.e("Selected List", "" + arrayList.size());

                                    if (arrayList == null || arrayList.size() == 0) {

                                        dataAccessObject.updateValue("",
                                                items.getComItem(),
                                                items.getIdItem(),
                                                idInterv, conf,
                                                items.getFlReserve(), items.getNomItem(), items.getIteration());
                                        items.setValeurNet("");

                                    } else {

                                        final StringBuilder itemsSelected = new StringBuilder();

                                        for (int i = 0; i < arrayList.size(); i++) {
                                            itemsSelected.append(arrayList.get(i));
                                            itemsSelected.append("@@@");
                                        }

                                        String itemSelected = itemsSelected.toString().trim();

                                        dataAccessObject.updateValue(
                                                itemSelected,
                                                items.getComItem(),
                                                items.getIdItem(),
                                                idInterv, conf,
                                                items.getFlReserve(), items.getNomItem(), items.getIteration());
                                        items.setValeurNet(itemSelected);
                                    }

                                    reportsJobDetailFragmentHelper
                                            .refreshItem(groupIndex, childIndex, 0);

                                }
                            });
                            reportsMultiSelectDialog.show(jobDetails.getSupportFragmentManager(), "");
                        }
                    });


                    ImageView imgph = (ImageView) view.findViewById(R.id.itemImage);
                    if ((photo != null) && (photo.length != 0)) {

//                        final Bitmap btImag = BitmapFactory.decodeByteArray(photo,
//                                0, photo.length, opt);
//                        // BitmapDrawable bmd = new
//                        // BitmapDrawable(resiseBitmap(imgph,
//                        // btImag));
//                        imgph.setImageBitmap(btImag);

                        //new changes
                        Glide.with(jobDetails)
                                .load(photo)
                                .asBitmap()
                                .override(200, 200)
                                .fitCenter()
                                .placeholder(R.drawable.library_iicon)
                                .into(imgph);

                        imgph.setVisibility(View.VISIBLE);

                        imgph.setOnClickListener(new OnClickListener() {
                            public void onClick(final View v) {

                                DisplayMetrics displaymetrics = new DisplayMetrics();
                                jobDetails.getWindowManager().getDefaultDisplay()
                                        .getMetrics(displaymetrics);

                                // Bitmap bm2 = dataAccessObject.resizeBitmap(btImag
                                // ,
                                // displaymetrics.widthPixels ,
                                // displaymetrics.heightPixels);

//                                Bitmap resized = Bitmap.createScaledBitmap(btImag,
//                                        displaymetrics.widthPixels,
//                                        displaymetrics.heightPixels, true);
//                                showImg(v, btImag);

                                Glide.with(jobDetails)
                                        .load(photo)
                                        .asBitmap()
                                        .fitCenter()
                                        .placeholder(R.drawable.library_iicon)
                                        .into(new SimpleTarget<Bitmap>(displaymetrics.widthPixels, displaymetrics.heightPixels) {
                                            @Override
                                            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                                showImg(v, resource);
                                            }
                                        });
                            }
                        });

                    } else {
                        imgph.setVisibility(View.GONE);
                    }

                    android.widget.TextView commentIv = (android.widget.TextView) view
                            .findViewById(R.id.commentIv);
                    commentIv.setTypeface(fontAwesome);
                    if (gt != null && gt.getFlCommentsRapport() == 0) {
                        commentIv.setVisibility(View.GONE);

                    }

                    final boolean isWriteable = jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED;

                    commentIv.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            EnterCommentDialog enterCommentDialog = new EnterCommentDialog(
                                    jobDetails,
                                    new EnterCommentDialog.EnterDialogInterface() {

                                        @Override
                                        public void doOnModifyClick(String data) {
                                            dataAccessObject.updateValue(
                                                    getValueFormat(
                                                            items.getValeurNet(),
                                                            items.getIdTypeItem()),
                                                    data, items.getIdItem(),
                                                    idInterv, conf, items
                                                            .getFlReserve(), items.getNomItem(), items.getIteration());
                                            reportsJobDetailFragmentHelper
                                                    .refreshItem(groupIndex, childIndex, 0);
                                        }

                                        @Override
                                        public void doOnCancelClick() {
                                            hideKeyboard();
                                        }
                                    }, jobDetails.getString(R.string.enterComment).toUpperCase(),
                                    items.getComItem(), isWriteable);

                            enterCommentDialog.show();

                        }
                    });

                    if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED || jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__COMPLETE) {
                        if (commentIv != null) {
                            commentIv.setEnabled(true);
                        }
                    } else {
                        if (commentIv != null) {
                            commentIv.setEnabled(false);
                        }
                    }

                    int flReserve = items.getFlReserve();

                    /*
                     * New Changes.....
                     *
                     *
                     * Seperate buttons for Status (Complaint, Resolved &
                     * Unresolved). If a status is selected, update the current flag
                     * value to DB. If a status is unselected (ie., Same status is
                     * again clicked), change the flag value as 0 in DB.
                     *
                     * And if the status is changed, change the color of the line
                     * strip in left of sub-category's layout. For Compliant -
                     * Orange, Resolved - Green, Unresolved - Red, if none is
                     * selected then change the color as White
                     */
                    final LinearLayout linCompliant = (LinearLayout) view
                            .findViewById(R.id.linCompliantContainer);
                    final LinearLayout linResolved = (LinearLayout) view
                            .findViewById(R.id.linResolvedContainer);
                    final LinearLayout linUnresolved = (LinearLayout) view
                            .findViewById(R.id.linUnresolvedContainer);

                    // set the boxframe for child
                    LinearLayout parentLayout = (LinearLayout) view
                            .findViewById(R.id.ParentLayoutReport);

                    if (flReserve == 1) {
                        linCompliant
                                .setBackgroundResource(R.drawable.boxframe_report_status);
                        parentLayout
                                .setBackgroundResource(R.drawable.boxframe_compliant_status);
                    } else if (flReserve == 2) {
                        linUnresolved
                                .setBackgroundResource(R.drawable.boxframe_report_status);
                        parentLayout
                                .setBackgroundResource(R.drawable.boxframe_unresolved_status);
                    } else if (flReserve == 3) {
                        linResolved
                                .setBackgroundResource(R.drawable.boxframe_report_status);
                        parentLayout
                                .setBackgroundResource(R.drawable.boxframe_resolved_status);
                    }
                    final int i = flReserve;

                    // ........................LISTENER...FOR...STATUS...BUTTONS...STARTS...HERE............................
                    linCompliant.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            if (i != 1) {
                                dataAccessObject.updateValue(
                                        getValueFormat(items.getValeurNet(),
                                                items.getIdTypeItem()),
                                        items.getComItem(), items.getIdItem(),
                                        idInterv, conf, 1, items.getNomItem(), items.getIteration());
                                reportsJobDetailFragmentHelper
                                        .refreshItem(groupIndex, childIndex, 1);
                            } else {
                                dataAccessObject.updateValue(
                                        getValueFormat(items.getValeurNet(),
                                                items.getIdTypeItem()),
                                        items.getComItem(), items.getIdItem(),
                                        idInterv, conf, 0, items.getNomItem(), items.getIteration());
                                reportsJobDetailFragmentHelper
                                        .refreshItem(groupIndex, childIndex, 1);
                            }
                        }
                    });
                    linResolved.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (i != 3) {
                                dataAccessObject.updateValue(
                                        getValueFormat(items.getValeurNet(),
                                                items.getIdTypeItem()),
                                        items.getComItem(), items.getIdItem(),
                                        idInterv, conf, 3, items.getNomItem(), items.getIteration());
                                reportsJobDetailFragmentHelper
                                        .refreshItem(groupIndex, childIndex, 1);
                            } else {
                                dataAccessObject.updateValue(
                                        getValueFormat(items.getValeurNet(),
                                                items.getIdTypeItem()),
                                        items.getComItem(), items.getIdItem(),
                                        idInterv, conf, 0, items.getNomItem(), items.getIteration());
                                reportsJobDetailFragmentHelper
                                        .refreshItem(groupIndex, childIndex, 1);
                            }
                        }
                    });
                    linUnresolved.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (i != 2) {
                                dataAccessObject.updateValue(
                                        getValueFormat(items.getValeurNet(),
                                                items.getIdTypeItem()),
                                        items.getComItem(), items.getIdItem(),
                                        idInterv, conf, 2, items.getNomItem(), items.getIteration());
                                reportsJobDetailFragmentHelper
                                        .refreshItem(groupIndex, childIndex, 1);
                            } else {
                                dataAccessObject.updateValue(
                                        getValueFormat(items.getValeurNet(),
                                                items.getIdTypeItem()),
                                        items.getComItem(), items.getIdItem(),
                                        idInterv, conf, 0, items.getNomItem(), items.getIteration());
                                reportsJobDetailFragmentHelper
                                        .refreshItem(groupIndex, childIndex, 1);
                            }
                        }
                    });

                    // ........................LISTENER...FOR...STATUS...BUTTONS...ENDS...HERE............................

                    if (jobDetails.getUpdatedValueOfStatus() != KEYS.JObDetail.JOB__STARTED) {
                        linCompliant.setEnabled(false);
                        linResolved.setEnabled(false);
                        linUnresolved.setEnabled(false);
                        swipeLayout.getSurfaceView().setEnabled(false);
                    } else {
                        linCompliant.setEnabled(true);
                        linResolved.setEnabled(true);
                        linUnresolved.setEnabled(true);
                        swipeLayout.getSurfaceView().setEnabled(true);
                    }

                    // show the view as divider, if it is a last child
                    if (isLastChild) {
                        View dividerLine = (View) view
                                .findViewById(R.id.dividerView);
                        dividerLine.setVisibility(View.VISIBLE);
                    }
                } else {

                    view = layoutInflater.inflate(
                            R.layout.layout_datatype_multiselect_checkbox,
                            null);
                    android.widget.TextView txtImageAvail = (android.widget.TextView) view
                            .findViewById(R.id.txtImageAvail);
                    View lineImageAvail = (View) view
                            .findViewById(R.id.viewImageAvail);

                    txtImageAvail.setTypeface(fontAwesome);
                    final byte[] photo = items.getImage();
                    // ....................SWIPE...ACTION...STARTS..............................
                    SwipeLayout swipeLayout = (SwipeLayout) view
                            .findViewById(R.id.swipeLayoutDataConform);

                    // set show mode.
                    swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

                    // set drag edge.
                    // swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
                    if ((photo != null) && (photo.length != 0)) {
                        // swipeLayout.setBottomViewIds(R.id.bottom_wrapper_left,
                        // R.id.bottom_wrapper_right, 0, 0);
//					swipeLayout.setDragEdge(DragEdge.Left);
//					swipeLayout.setDragEdge(DragEdge.Right);

                        swipeLayout.addDrag(DragEdge.Left, swipeLayout.findViewById(R.id.bottom_wrapper_left));
                        swipeLayout.addDrag(DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_right));

                        txtImageAvail.setVisibility(View.VISIBLE);
                        lineImageAvail.setVisibility(View.VISIBLE);
                    } else {
                        // swipeLayout.setBottomViewIds(0,
                        // R.id.bottom_wrapper_right,
                        // 0, 0);
//					swipeLayout.setDragEdge(DragEdge.Right);

                        swipeLayout.addDrag(DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_right));

                        txtImageAvail.setVisibility(View.GONE);
                        lineImageAvail.setVisibility(View.GONE);

                    }

                    // ***********************SWIPE...ACTION...ENDS******************************

                    final TextView reportListOfValuesItemName = (TextView) view
                            .findViewById(R.id.fieldName);
                    reportListOfValuesItemName.setText(items.getNomItem());

                    final android.widget.TextView txtExpand = (android.widget.TextView) view
                            .findViewById(R.id.txtExpand);
                    Typeface typeface = Typeface.createFromAsset(
                            jobDetails.getAssets(),
                            jobDetails.getResources().getString(
                                    R.string.fontName_fontAwesome));

                    // -------------------NEW CHANGES--------------

                    txtExpand.setTypeface(typeface);

                    Handler handler = new Handler();
                    handler.post(new Runnable() {

                        @Override
                        public void run() {
                            if (reportListOfValuesItemName.getLineCount() > 1) {
                                txtExpand.setVisibility(View.VISIBLE);
                                reportListOfValuesItemName.setSingleLine(true);
                            } else {
                                txtExpand.setVisibility(View.GONE);
                            }
                        }
                    });

                    txtExpand.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            if (reportListOfValuesItemName.getLineCount() > 1) {
                                reportListOfValuesItemName.setSingleLine(true);
                                txtExpand.setText(jobDetails
                                        .getString(R.string.fa_expand));
                            } else {
                                reportListOfValuesItemName.setSingleLine(false);
                                txtExpand.setText(jobDetails
                                        .getString(R.string.fa_compress));
                            }
                        }
                    });

                    // -------------------NEW CHANGES--------------

                    // .......NEW...CHANGES.......
                    // reportListOfValuesItemName
                    // .setOnClickListener(new OnClickListener() {
                    //
                    // @Override
                    // public void onClick(View v) {
                    // TextView ttt = (TextView) v;
                    // if (ttt.getLineCount() > 1) {
                    // ttt.setSingleLine(true);
                    // } else {
                    // ttt.setSingleLine(false);
                    // }
                    //
                    // }
                    // });

                    // final Button listOfValues = (Button) view
                    // .findViewById(R.id.dataListOfValues);


                    final CheckBox checkBox1 = (CheckBox) view
                            .findViewById(R.id.checkBoxOne);
                    final CheckBox checkBox2 = (CheckBox) view
                            .findViewById(R.id.checkBoxTwo);
                    final CheckBox checkBox3 = (CheckBox) view
                            .findViewById(R.id.checkBoxThree);

                    if (listValues != null && listValues.size() > 0) {
                        checkBox1.setText(listValues.get(0));
                    } else {
                        checkBox1.setVisibility(View.GONE);
                    }

                    if (listValues.size() > 1) {
                        checkBox2.setText(listValues.get(1));
                    } else {
                        checkBox2.setVisibility(View.GONE);
                    }

                    if (listValues.size() > 2) {
                        checkBox3.setText(listValues.get(2));
                    } else {
                        checkBox3.setVisibility(View.GONE);
                    }

                    if (jobDetails.getUpdatedValueOfStatus() != KEYS.JObDetail.JOB__STARTED) {

                        checkBox1.setEnabled(false);
                        checkBox2.setEnabled(false);
                        checkBox3.setEnabled(false);

                    } else {

                        checkBox1.setEnabled(true);
                        checkBox2.setEnabled(true);
                        checkBox3.setEnabled(true);

                    }

                    final String[] tab = new String[listValues.size()];

                    final int conf = 0;

                    String[] itemValeurNetArr = items.getValeurNet().split("@@@");

                    if (itemValeurNetArr.length > 0) {

                        if (listValues.size() == 3) {

                            for (String anItemValeurNetArr : itemValeurNetArr) {

                                if (anItemValeurNetArr.equalsIgnoreCase(listValues.get(0))) {
                                    checkBox1.setChecked(true);
                                }
                                if (anItemValeurNetArr.equalsIgnoreCase(listValues.get(1))) {
                                    checkBox2.setChecked(true);
                                }
                                if (anItemValeurNetArr.equalsIgnoreCase(listValues.get(2))) {
                                    checkBox3.setChecked(true);
                                }

                            }

                        } else if (listValues.size() == 2) {

                            for (String anItemValeurNetArr : itemValeurNetArr) {

                                if (anItemValeurNetArr.equalsIgnoreCase(listValues.get(0))) {
                                    checkBox1.setChecked(true);
                                }
                                if (anItemValeurNetArr.equalsIgnoreCase(listValues.get(1))) {
                                    checkBox2.setChecked(true);
                                }

                            }

                        } else if (listValues.size() == 1) {
                            for (String anItemValeurNetArr : itemValeurNetArr) {

                                if (anItemValeurNetArr.equalsIgnoreCase(listValues.get(0))) {
                                    checkBox1.setChecked(true);
                                }

                            }
                        } else {
                            checkBox1.setChecked(false);
                            checkBox2.setChecked(false);
                            checkBox3.setChecked(false);
                        }
                    } else {
                        checkBox1.setChecked(false);
                        checkBox2.setChecked(false);
                        checkBox3.setChecked(false);
                    }

                    if (items.getOblig() != 0) {
                        if (checkBox1.isChecked() || checkBox2.isChecked() || checkBox3.isChecked()) {
                            reportListOfValuesItemName.setTextColor(jobDetails
                                    .getResources().getColor(
                                            R.color.text_category_title_color));
                        } else {
                            reportListOfValuesItemName
                                    .setTextColor(jobDetails
                                            .getResources()
                                            .getColor(
                                                    R.color.textManadatoryFieldsReportsJobDetail));
                        }
                    }

                    checkBox1.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(android.widget.CompoundButton compoundButton, boolean boxChecked) {

                            final StringBuilder itemsSelected = new StringBuilder();
                            if (checkBox1.isChecked() && checkBox2.isChecked() && checkBox3.isChecked()) {
                                itemsSelected.append(listValues.get(0));
                                itemsSelected.append("@@@");
                                itemsSelected.append(listValues.get(1));
                                itemsSelected.append("@@@");
                                itemsSelected.append(listValues.get(2));
                            } else if (checkBox1.isChecked() && checkBox2.isChecked()) {
                                itemsSelected.append(listValues.get(0));
                                itemsSelected.append("@@@");
                                itemsSelected.append(listValues.get(1));
                            } else if (checkBox2.isChecked() && checkBox3.isChecked()) {
                                itemsSelected.append(listValues.get(1));
                                itemsSelected.append("@@@");
                                itemsSelected.append(listValues.get(2));
                            } else if (checkBox1.isChecked() && checkBox3.isChecked()) {
                                itemsSelected.append(listValues.get(0));
                                itemsSelected.append("@@@");
                                itemsSelected.append(listValues.get(2));
                            } else if (checkBox1.isChecked()) {
                                itemsSelected.append(listValues.get(0));
                            } else if (checkBox2.isChecked()) {
                                itemsSelected.append(listValues.get(1));
                            } else if (checkBox3.isChecked()) {
                                itemsSelected.append(listValues.get(2));
                            } else {
                                itemsSelected.append("");
                            }

                            dataAccessObject.updateValue(itemsSelected.toString(),
                                    items.getComItem(),
                                    items.getIdItem(), idInterv, conf,
                                    items.getFlReserve(), items.getNomItem(), items.getIteration());
                            items.setValeurNet(itemsSelected.toString());

                            reportsJobDetailFragmentHelper
                                    .refreshItem(groupIndex, childIndex, 0);

                        }
                    });

                    checkBox2.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(android.widget.CompoundButton compoundButton, boolean boxChecked) {

                            final StringBuilder itemsSelected = new StringBuilder();
                            if (checkBox1.isChecked() && checkBox2.isChecked() && checkBox3.isChecked()) {
                                itemsSelected.append(listValues.get(0));
                                itemsSelected.append("@@@");
                                itemsSelected.append(listValues.get(1));
                                itemsSelected.append("@@@");
                                itemsSelected.append(listValues.get(2));
                            } else if (checkBox1.isChecked() && checkBox2.isChecked()) {
                                itemsSelected.append(listValues.get(0));
                                itemsSelected.append("@@@");
                                itemsSelected.append(listValues.get(1));
                            } else if (checkBox2.isChecked() && checkBox3.isChecked()) {
                                itemsSelected.append(listValues.get(1));
                                itemsSelected.append("@@@");
                                itemsSelected.append(listValues.get(2));
                            } else if (checkBox1.isChecked() && checkBox3.isChecked()) {
                                itemsSelected.append(listValues.get(0));
                                itemsSelected.append("@@@");
                                itemsSelected.append(listValues.get(2));
                            } else if (checkBox1.isChecked()) {
                                itemsSelected.append(listValues.get(0));
                            } else if (checkBox2.isChecked()) {
                                itemsSelected.append(listValues.get(1));
                            } else if (checkBox3.isChecked()) {
                                itemsSelected.append(listValues.get(2));
                            } else {
                                itemsSelected.append("");
                            }

                            dataAccessObject.updateValue(itemsSelected.toString(),
                                    items.getComItem(),
                                    items.getIdItem(), idInterv, conf,
                                    items.getFlReserve(), items.getNomItem(), items.getIteration());
                            items.setValeurNet(itemsSelected.toString());

                            reportsJobDetailFragmentHelper
                                    .refreshItem(groupIndex, childIndex, 0);

                        }
                    });

                    checkBox3.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(android.widget.CompoundButton compoundButton, boolean boxChecked) {

                            final StringBuilder itemsSelected = new StringBuilder();
                            if (checkBox1.isChecked() && checkBox2.isChecked() && checkBox3.isChecked()) {
                                itemsSelected.append(listValues.get(0));
                                itemsSelected.append("@@@");
                                itemsSelected.append(listValues.get(1));
                                itemsSelected.append("@@@");
                                itemsSelected.append(listValues.get(2));
                            } else if (checkBox1.isChecked() && checkBox2.isChecked()) {
                                itemsSelected.append(listValues.get(0));
                                itemsSelected.append("@@@");
                                itemsSelected.append(listValues.get(1));
                            } else if (checkBox2.isChecked() && checkBox3.isChecked()) {
                                itemsSelected.append(listValues.get(1));
                                itemsSelected.append("@@@");
                                itemsSelected.append(listValues.get(2));
                            } else if (checkBox1.isChecked() && checkBox3.isChecked()) {
                                itemsSelected.append(listValues.get(0));
                                itemsSelected.append("@@@");
                                itemsSelected.append(listValues.get(2));
                            } else if (checkBox1.isChecked()) {
                                itemsSelected.append(listValues.get(0));
                            } else if (checkBox2.isChecked()) {
                                itemsSelected.append(listValues.get(1));
                            } else if (checkBox3.isChecked()) {
                                itemsSelected.append(listValues.get(2));
                            } else {
                                itemsSelected.append("");
                            }

                            dataAccessObject.updateValue(itemsSelected.toString(),
                                    items.getComItem(),
                                    items.getIdItem(), idInterv, conf,
                                    items.getFlReserve(), items.getNomItem(), items.getIteration());
                            items.setValeurNet(itemsSelected.toString());

                            reportsJobDetailFragmentHelper
                                    .refreshItem(groupIndex, childIndex, 0);

                        }
                    });


                    ImageView imgph = (ImageView) view.findViewById(R.id.itemImage);
                    if ((photo != null) && (photo.length != 0)) {

//                        final Bitmap btImag = BitmapFactory.decodeByteArray(photo,
//                                0, photo.length, opt);
//                        // BitmapDrawable bmd = new
//                        // BitmapDrawable(resiseBitmap(imgph,
//                        // btImag));
//                        imgph.setImageBitmap(btImag);

                        //new changes
                        Glide.with(jobDetails)
                                .load(photo)
                                .asBitmap()
                                .override(200, 200)
                                .fitCenter()
                                .placeholder(R.drawable.library_iicon)
                                .into(imgph);

                        imgph.setVisibility(View.VISIBLE);

                        imgph.setOnClickListener(new OnClickListener() {
                            public void onClick(final View v) {

                                DisplayMetrics displaymetrics = new DisplayMetrics();
                                jobDetails.getWindowManager().getDefaultDisplay()
                                        .getMetrics(displaymetrics);

                                // Bitmap bm2 = dataAccessObject.resizeBitmap(btImag
                                // ,
                                // displaymetrics.widthPixels ,
                                // displaymetrics.heightPixels);

//                                Bitmap resized = Bitmap.createScaledBitmap(btImag,
//                                        displaymetrics.widthPixels,
//                                        displaymetrics.heightPixels, true);
//                                showImg(v, btImag);

                                Glide.with(jobDetails)
                                        .load(photo)
                                        .asBitmap()
                                        .fitCenter()
                                        .placeholder(R.drawable.library_iicon)
                                        .into(new SimpleTarget<Bitmap>(displaymetrics.widthPixels, displaymetrics.heightPixels) {
                                            @Override
                                            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                                showImg(v, resource);
                                            }
                                        });
                            }
                        });

                    } else {
                        imgph.setVisibility(View.GONE);
                    }

                    android.widget.TextView commentIv = (android.widget.TextView) view
                            .findViewById(R.id.commentIv);
                    commentIv.setTypeface(fontAwesome);
                    if (gt != null && gt.getFlCommentsRapport() == 0) {
                        commentIv.setVisibility(View.GONE);

                    }

                    final boolean isWriteable = jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED;

                    commentIv.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            EnterCommentDialog enterCommentDialog = new EnterCommentDialog(
                                    jobDetails,
                                    new EnterCommentDialog.EnterDialogInterface() {

                                        @Override
                                        public void doOnModifyClick(String data) {
                                            dataAccessObject.updateValue(
                                                    getValueFormat(
                                                            items.getValeurNet(),
                                                            items.getIdTypeItem()),
                                                    data, items.getIdItem(),
                                                    idInterv, conf, items
                                                            .getFlReserve(), items.getNomItem(), items.getIteration());
                                            reportsJobDetailFragmentHelper
                                                    .refreshItem(groupIndex, childIndex, 0);
                                        }

                                        @Override
                                        public void doOnCancelClick() {
                                            hideKeyboard();
                                        }
                                    }, jobDetails.getString(R.string.enterComment).toUpperCase(),
                                    items.getComItem(), isWriteable);

                            enterCommentDialog.show();

                        }
                    });

                    if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED || jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__COMPLETE) {
                        if (commentIv != null) {
                            commentIv.setEnabled(true);
                        }
                    } else {
                        if (commentIv != null) {
                            commentIv.setEnabled(false);
                        }
                    }

                    int flReserve = items.getFlReserve();

                    /*
                     * New Changes.....
                     *
                     *
                     * Seperate buttons for Status (Complaint, Resolved &
                     * Unresolved). If a status is selected, update the current flag
                     * value to DB. If a status is unselected (ie., Same status is
                     * again clicked), change the flag value as 0 in DB.
                     *
                     * And if the status is changed, change the color of the line
                     * strip in left of sub-category's layout. For Compliant -
                     * Orange, Resolved - Green, Unresolved - Red, if none is
                     * selected then change the color as White
                     */
                    final LinearLayout linCompliant = (LinearLayout) view
                            .findViewById(R.id.linCompliantContainer);
                    final LinearLayout linResolved = (LinearLayout) view
                            .findViewById(R.id.linResolvedContainer);
                    final LinearLayout linUnresolved = (LinearLayout) view
                            .findViewById(R.id.linUnresolvedContainer);

                    // set the boxframe for child
                    LinearLayout parentLayout = (LinearLayout) view
                            .findViewById(R.id.ParentLayoutReport);

                    if (flReserve == 1) {
                        linCompliant
                                .setBackgroundResource(R.drawable.boxframe_report_status);
                        parentLayout
                                .setBackgroundResource(R.drawable.boxframe_compliant_status);
                    } else if (flReserve == 2) {
                        linUnresolved
                                .setBackgroundResource(R.drawable.boxframe_report_status);
                        parentLayout
                                .setBackgroundResource(R.drawable.boxframe_unresolved_status);
                    } else if (flReserve == 3) {
                        linResolved
                                .setBackgroundResource(R.drawable.boxframe_report_status);
                        parentLayout
                                .setBackgroundResource(R.drawable.boxframe_resolved_status);
                    }
                    final int i = flReserve;

                    // ........................LISTENER...FOR...STATUS...BUTTONS...STARTS...HERE............................
                    linCompliant.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            if (i != 1) {
                                dataAccessObject.updateValue(
                                        getValueFormat(items.getValeurNet(),
                                                items.getIdTypeItem()),
                                        items.getComItem(), items.getIdItem(),
                                        idInterv, conf, 1, items.getNomItem(), items.getIteration());
                                reportsJobDetailFragmentHelper
                                        .refreshItem(groupIndex, childIndex, 1);
                            } else {
                                dataAccessObject.updateValue(
                                        getValueFormat(items.getValeurNet(),
                                                items.getIdTypeItem()),
                                        items.getComItem(), items.getIdItem(),
                                        idInterv, conf, 0, items.getNomItem(), items.getIteration());
                                reportsJobDetailFragmentHelper
                                        .refreshItem(groupIndex, childIndex, 1);
                            }
                        }
                    });
                    linResolved.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (i != 3) {
                                dataAccessObject.updateValue(
                                        getValueFormat(items.getValeurNet(),
                                                items.getIdTypeItem()),
                                        items.getComItem(), items.getIdItem(),
                                        idInterv, conf, 3, items.getNomItem(), items.getIteration());
                                reportsJobDetailFragmentHelper
                                        .refreshItem(groupIndex, childIndex, 1);
                            } else {
                                dataAccessObject.updateValue(
                                        getValueFormat(items.getValeurNet(),
                                                items.getIdTypeItem()),
                                        items.getComItem(), items.getIdItem(),
                                        idInterv, conf, 0, items.getNomItem(), items.getIteration());
                                reportsJobDetailFragmentHelper
                                        .refreshItem(groupIndex, childIndex, 1);
                            }
                        }
                    });
                    linUnresolved.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            if (i != 2) {
                                dataAccessObject.updateValue(
                                        getValueFormat(items.getValeurNet(),
                                                items.getIdTypeItem()),
                                        items.getComItem(), items.getIdItem(),
                                        idInterv, conf, 2, items.getNomItem(), items.getIteration());
                                reportsJobDetailFragmentHelper
                                        .refreshItem(groupIndex, childIndex, 1);
                            } else {
                                dataAccessObject.updateValue(
                                        getValueFormat(items.getValeurNet(),
                                                items.getIdTypeItem()),
                                        items.getComItem(), items.getIdItem(),
                                        idInterv, conf, 0, items.getNomItem(), items.getIteration());
                                reportsJobDetailFragmentHelper
                                        .refreshItem(groupIndex, childIndex, 1);
                            }
                        }
                    });

                    // ........................LISTENER...FOR...STATUS...BUTTONS...ENDS...HERE............................

                    if (jobDetails.getUpdatedValueOfStatus() != KEYS.JObDetail.JOB__STARTED) {
                        linCompliant.setEnabled(false);
                        linResolved.setEnabled(false);
                        linUnresolved.setEnabled(false);

                    } else {
                        linCompliant.setEnabled(true);
                        linResolved.setEnabled(true);
                        linUnresolved.setEnabled(true);
                    }

                    // show the view as divider, if it is a last child
                    if (isLastChild) {
                        View dividerLine = (View) view
                                .findViewById(R.id.dividerView);
                        dividerLine.setVisibility(View.VISIBLE);
                    }

                }


                // TODO: 24/3/17


            } else if (itemType == 11) {

                view = layoutInflater.inflate(R.layout.layout_datatype_hours, null);

                android.widget.TextView txtImageAvail = (android.widget.TextView) view
                        .findViewById(R.id.txtImageAvail);
                View lineImageAvail = (View) view.findViewById(R.id.viewImageAvail);

                txtImageAvail.setTypeface(fontAwesome);
                final byte[] photo = items.getImage();
                // ....................SWIPE...ACTION...STARTS..............................
                SwipeLayout swipeLayout = (SwipeLayout) view
                        .findViewById(R.id.swipeLayoutDataConform);

                // set show mode.
                swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

                // set drag edge.
                // swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
                if ((photo != null) && (photo.length != 0)) {
                    swipeLayout.addDrag(DragEdge.Left, swipeLayout.findViewById(R.id.bottom_wrapper_left));
                    swipeLayout.addDrag(DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_right));

                    txtImageAvail.setVisibility(View.VISIBLE);
                    lineImageAvail.setVisibility(View.VISIBLE);
                } else {

                    swipeLayout.addDrag(DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_right));

                    txtImageAvail.setVisibility(View.GONE);
                    lineImageAvail.setVisibility(View.GONE);

                }

                // ***********************SWIPE...ACTION...ENDS******************************

                final TextView reportHoursItemName = (TextView) view
                        .findViewById(R.id.fieldName);
                ImageView clearHourDataIv = (ImageView) view
                        .findViewById(R.id.clearHourDataIv);
                reportHoursItemName.setText(items.getNomItem());

                final android.widget.TextView txtExpand = (android.widget.TextView) view
                        .findViewById(R.id.txtExpand);
                Typeface typeface = Typeface.createFromAsset(
                        jobDetails.getAssets(), jobDetails.getResources()
                                .getString(R.string.fontName_fontAwesome));

                // -------------------NEW CHANGES--------------

                txtExpand.setTypeface(typeface);

                Handler handler = new Handler();
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (reportHoursItemName.getLineCount() > 1) {
                            txtExpand.setVisibility(View.VISIBLE);
                            reportHoursItemName.setSingleLine(true);
                        } else {
                            txtExpand.setVisibility(View.GONE);
                        }
                    }
                });

                txtExpand.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (reportHoursItemName.getLineCount() > 1) {
                            reportHoursItemName.setSingleLine(true);
                            txtExpand.setText(jobDetails
                                    .getString(R.string.fa_expand));
                        } else {
                            reportHoursItemName.setSingleLine(false);
                            txtExpand.setText(jobDetails
                                    .getString(R.string.fa_compress));
                        }
                    }
                });

                // -------------------NEW CHANGES--------------

                final Button dateBtn = (Button) view.findViewById(R.id.dataHour);

                clearHourDataIv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        String comm = "";
                        final int conf = 0;
                        dataAccessObject.updateValue("", comm, items.getIdItem(),
                                idInterv, conf, items.getFlReserve(), items.getNomItem(), items.getIteration());
                        dateBtn.setText("");
                        dateBtn.setHint("HH:MM");
                        items.setValeurNet("");
                        reportsJobDetailFragmentHelper.refreshItem(items
                                .getGroupPosition(), childIndex, 0);
                    }
                });

                if (jobDetails.getUpdatedValueOfStatus() != KEYS.JObDetail.JOB__STARTED) {
                    if (dateBtn != null) {
                        dateBtn.setEnabled(false);
                    }
                    view.setEnabled(false);
                } else {
                    if (dateBtn != null) {
                        dateBtn.setEnabled(true);

                    }
                    view.setEnabled(true);
                }

                String val = getValueFormat(items.getValeurNet(),
                        items.getIdTypeItem());
//            Logger.log(TAG,"value duration hour is :"+items.getValeurNet());

//
//            Date time = null;
//            try {
//                time = frenchTimeFormat.parse(val);
//            } catch (ParseException e) {
//                Logger.printException(e);
//            }
//
//            Format format = android.text.format.DateFormat
//                    .getTimeFormat(jobDetails);
//            if (time != null) {
//                val = format.format(time);
//            }
//            dateBtn.setText(val);

                if (val.trim().length() > 0)
                    dateBtn.setText(val + " Hours");

                if (items.getOblig() != 0) {

                    if (!TextUtils.isEmpty(dateBtn.getText().toString())) {

                        reportHoursItemName.setTextColor(jobDetails.getResources()
                                .getColor(R.color.text_category_title_color));
                    } else {
                        reportHoursItemName
                                .setTextColor(jobDetails
                                        .getResources()
                                        .getColor(
                                                R.color.textManadatoryFieldsReportsJobDetail));
                    }
                }

                if (!TextUtils.isEmpty(dateBtn.getText())) {
                    if ((jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__COMPLETE)
                            || (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.DEDLINE_EXCEEDED)) {
                        clearHourDataIv.setVisibility(View.GONE);
                    } else {
                        clearHourDataIv.setVisibility(View.VISIBLE);
                    }
                } else {
                    clearHourDataIv.setVisibility(View.GONE);
                }
                dateBtn.setTag(items);

                final boolean isWriteable = jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED;

//			dateBtn.setOnClickListener(onClickListener);
                dateBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

//                    ReportItemDurationDialog durationDialog = new ReportItemDurationDialog
//                            (jobDetails, new ReportItemDurationDialog.ReportItemDurationInterface() {
//                                @Override
//                                public void doOnModifyClick(String data) {
//                                    int conf = 0;
//                                    dataAccessObject.updateValue(
//                                            data,
//                                            items.getComItem(), items.getIdItem(), idInterv,
//                                            conf, items.getFlReserve(), items.getNomItem(), items.getIteration());
//                                    reportsJobDetailFragmentHelper
//                                            .refreshItem(groupIndex, childIndex, 0);
//
//                                }
//
//                                @Override
//                                public void doOnCancelClick() {
//
//                                }
//                            }, items.getNomItem(),
//                                    getValueFormat(items.getValeurNet(), items.getIdTypeItem()),
//                                    isWriteable);
//
//                    durationDialog.show();
                        //custom view for selecting hour and minute

                        String previousHour = getValueFormat(items.getValeurNet(),
                                items.getIdTypeItem());

                        if (previousHour.trim().length() == 0) {
                            previousHour = "00:30";
                        }

                        HourMinSelectDialog pickerPopWin = new HourMinSelectDialog.Builder(jobDetails, new HourMinSelectDialog.OnTimePickedListener() {
                            @Override
                            public void onTimePickCompleted(int hour, int min, String hourLabel, String minLabel, String data) {
                                Logger.log(TAG, "selected date is --->" + data);

                                int conf = 0;
                                dataAccessObject.updateValue(
                                        data,
                                        items.getComItem(), items.getIdItem(), idInterv,
                                        conf, items.getFlReserve(), items.getNomItem(), items.getIteration());
                                reportsJobDetailFragmentHelper
                                        .refreshItem(groupIndex, childIndex, 0);


                            }

                        }).textConfirm(jobDetails.getResources().getString(R.string.txt_ack_msg))
                                .textCancel(jobDetails.getResources().getString(R.string.textCancelLable))
                                .btnTextSize(20) // button text size
                                .viewTextSize(19)
                                .timeChose(previousHour)
                                .labelTextSize(19)
                                .colorCancel(jobDetails.getResources()
                                        .getColor(R.color.actionbarColor))
                                .colorConfirm(jobDetails.getResources()
                                        .getColor(R.color.actionbarColor))
                                .build();

                        if (isWriteable)
                            pickerPopWin.showPopWin(jobDetails);

                    }
                });

                swipeLayout.getSurfaceView().setTag(items);
                swipeLayout.getSurfaceView().setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        dateBtn.performClick();
                    }
                });

                ImageView imgph = (ImageView) view.findViewById(R.id.itemImage);
                if ((photo != null) && (photo.length != 0)) {

//                    final Bitmap btImag = BitmapFactory.decodeByteArray(photo, 0,
//                            photo.length, opt);
//                    // BitmapDrawable bmd = new BitmapDrawable(resiseBitmap(imgph,
//                    // btImag));
//                    imgph.setImageBitmap(btImag);

                    //new changes
                    Glide.with(jobDetails)
                            .load(photo)
                            .asBitmap()
                            .override(200, 200)
                            .fitCenter()
                            .placeholder(R.drawable.library_iicon)
                            .into(imgph);

                    imgph.setVisibility(View.VISIBLE);

                    imgph.setOnClickListener(new OnClickListener() {
                        public void onClick(final View v) {

                            DisplayMetrics displaymetrics = new DisplayMetrics();
                            jobDetails.getWindowManager().getDefaultDisplay()
                                    .getMetrics(displaymetrics);

                            // Bitmap bm2 = dataAccessObject.resizeBitmap(btImag ,
                            // displaymetrics.widthPixels ,
                            // displaymetrics.heightPixels);

//                            Bitmap resized = Bitmap.createScaledBitmap(btImag,
//                                    displaymetrics.widthPixels,
//                                    displaymetrics.heightPixels, true);
//                            showImg(v, btImag);

                            Glide.with(jobDetails)
                                    .load(photo)
                                    .asBitmap()
                                    .fitCenter()
                                    .placeholder(R.drawable.library_iicon)
                                    .into(new SimpleTarget<Bitmap>(displaymetrics.widthPixels, displaymetrics.heightPixels) {
                                        @Override
                                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                            showImg(v, resource);
                                        }
                                    });
                        }
                    });

                } else {
                    imgph.setVisibility(View.GONE);
                }

                android.widget.TextView commentIv = (android.widget.TextView) view
                        .findViewById(R.id.commentIv);
                commentIv.setTypeface(fontAwesome);
                if (gt != null && gt.getFlCommentsRapport() == 0) {
                    commentIv.setVisibility(View.GONE);

                }


                commentIv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        EnterCommentDialog enterCommentDialog = new EnterCommentDialog(
                                jobDetails,
                                new EnterCommentDialog.EnterDialogInterface() {

                                    @Override
                                    public void doOnModifyClick(String data) {
                                        int conf = 0;
                                        dataAccessObject.updateValue(
                                                getValueFormat(
                                                        items.getValeurNet(),
                                                        items.getIdTypeItem()),
                                                data, items.getIdItem(), idInterv,
                                                conf, items.getFlReserve(), items.getNomItem(), items.getIteration());
                                        reportsJobDetailFragmentHelper
                                                .refreshItem(groupIndex, childIndex, 0);

                                    }

                                    @Override
                                    public void doOnCancelClick() {
                                        hideKeyboard();
                                    }
                                }, jobDetails.getString(R.string.enterComment).toUpperCase(),
                                items.getComItem(), isWriteable);

                        enterCommentDialog.show();

                    }
                });

                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED || jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__COMPLETE) {
                    if (commentIv != null) {
                        commentIv.setEnabled(true);
                    }
                } else {
                    if (commentIv != null) {
                        commentIv.setEnabled(false);
                    }
                }

                int flReserve = items.getFlReserve();
                final int conf = 0;
                /*
                 * New Changes.....
                 *
                 *
                 * Seperate buttons for Status (Complaint, Resolved & Unresolved).
                 * If a status is selected, update the current flag value to DB. If
                 * a status is unselected (ie., Same status is again clicked),
                 * change the flag value as 0 in DB.
                 *
                 * And if the status is changed, change the color of the line strip
                 * in left of sub-category's layout. For Compliant - Orange,
                 * Resolved - Green, Unresolved - Red, if none is selected then
                 * change the color as White
                 */
                final LinearLayout linCompliant = (LinearLayout) view
                        .findViewById(R.id.linCompliantContainer);
                final LinearLayout linResolved = (LinearLayout) view
                        .findViewById(R.id.linResolvedContainer);
                final LinearLayout linUnresolved = (LinearLayout) view
                        .findViewById(R.id.linUnresolvedContainer);

                // set the boxframe for child
                LinearLayout parentLayout = (LinearLayout) view
                        .findViewById(R.id.ParentLayoutReport);

                if (flReserve == 1) {
                    linCompliant
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_compliant_status);
                } else if (flReserve == 2) {
                    linUnresolved
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_unresolved_status);
                } else if (flReserve == 3) {
                    linResolved
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_resolved_status);
                }
                final int i = flReserve;

                // ........................LISTENER...FOR...STATUS...BUTTONS...STARTS...HERE............................
                linCompliant.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (i != 1) {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 1, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        } else {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }
                    }
                });
                linResolved.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (i != 3) {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 3, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        } else {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }
                    }
                });
                linUnresolved.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (i != 2) {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 2, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        } else {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }
                    }
                });

                // ........................LISTENER...FOR...STATUS...BUTTONS...ENDS...HERE............................

                if (jobDetails.getUpdatedValueOfStatus() != KEYS.JObDetail.JOB__STARTED) {
                    linCompliant.setEnabled(false);
                    linResolved.setEnabled(false);
                    linUnresolved.setEnabled(false);
                    swipeLayout.getSurfaceView().setEnabled(false);
                } else {
                    linCompliant.setEnabled(true);
                    linResolved.setEnabled(true);
                    linUnresolved.setEnabled(true);
                    swipeLayout.getSurfaceView().setEnabled(true);
                }

            } else {
//            view = new View(jobDetails.getApplicationContext());
//            view.setVisibility(View.GONE);

                // todo
                // if itemType is not available, put the row as textview(itemtype=5) instead


                view = layoutInflater.inflate(
                        R.layout.layout_datatype_numberdecimealnote, null);

                android.widget.TextView txtImageAvail = (android.widget.TextView) view
                        .findViewById(R.id.txtImageAvail);
                android.widget.TextView txtBarcodeIcon = (android.widget.TextView) view
                        .findViewById(R.id.txtBarcodeIcon);
                View lineImageAvail = (View) view.findViewById(R.id.viewImageAvail);

                txtImageAvail.setTypeface(fontAwesome);
                txtBarcodeIcon.setTypeface(fontAwesome);
                final byte[] photo = items.getImage();
                // ....................SWIPE...ACTION...STARTS..............................
                SwipeLayout swipeLayout = (SwipeLayout) view
                        .findViewById(R.id.swipeLayoutDataConform);

                // set show mode.
                swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

                // set drag edge.
                // swipeLayout.setDragEdge(SwipeLayout.DragEdge.Right);
                if ((photo != null) && (photo.length != 0)) {
                    // swipeLayout.setBottomViewIds(R.id.bottom_wrapper_left,
                    // R.id.bottom_wrapper_right, 0, 0);
//				swipeLayout.setDragEdge(DragEdge.Left);
//				swipeLayout.setDragEdge(DragEdge.Right);

                    swipeLayout.addDrag(DragEdge.Left, swipeLayout.findViewById(R.id.bottom_wrapper_left));
                    swipeLayout.addDrag(DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_right));

                    txtImageAvail.setVisibility(View.VISIBLE);
                    lineImageAvail.setVisibility(View.VISIBLE);
                } else {
                    // swipeLayout
                    // .setBottomViewIds(0, R.id.bottom_wrapper_right, 0, 0);
//				swipeLayout.setDragEdge(DragEdge.Right);

                    swipeLayout.addDrag(DragEdge.Right, swipeLayout.findViewById(R.id.bottom_wrapper_right));

                    txtImageAvail.setVisibility(View.GONE);
                    lineImageAvail.setVisibility(View.GONE);

                }

                // ***********************SWIPE...ACTION...ENDS******************************

                final TextView reportsNumericTextfieldItemName = (TextView) view
                        .findViewById(R.id.fieldName);
                final TextView noteData = (TextView) view
                        .findViewById(R.id.fieldData);
                noteData.setText(getValueFormat(items.getValeurNet(),
                        items.getIdTypeItem()));

                final android.widget.TextView txtExpand = (android.widget.TextView) view
                        .findViewById(R.id.txtExpand);
                Typeface typeface = Typeface.createFromAsset(
                        jobDetails.getAssets(), jobDetails.getResources()
                                .getString(R.string.fontName_fontAwesome));

                // -------------------NEW CHANGES--------------

                txtExpand.setTypeface(typeface);

                Handler handler = new Handler();
                handler.post(new Runnable() {

                    @Override
                    public void run() {
                        if (reportsNumericTextfieldItemName.getLineCount() > 1) {
                            txtExpand.setVisibility(View.VISIBLE);
                            reportsNumericTextfieldItemName.setSingleLine(true);
                        } else {
                            txtExpand.setVisibility(View.GONE);
                        }
                    }
                });

                txtExpand.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (reportsNumericTextfieldItemName.getLineCount() > 1) {
                            reportsNumericTextfieldItemName.setSingleLine(true);
                            txtExpand.setText(jobDetails
                                    .getString(R.string.fa_expand));
                        } else {
                            reportsNumericTextfieldItemName.setSingleLine(false);
                            txtExpand.setText(jobDetails
                                    .getString(R.string.fa_compress));
                        }
                    }
                });

                swipeLayout.getSurfaceView().setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        noteData.performClick();
                    }
                });
                // -------------------NEW CHANGES--------------

                final int conf = 0;
                reportsNumericTextfieldItemName.setText(items.getNomItem());
                if (items.getOblig() != 0) {

                    if (!TextUtils.isEmpty(noteData.getText().toString())) {

                        reportsNumericTextfieldItemName.setTextColor(jobDetails
                                .getResources().getColor(
                                        R.color.text_category_title_color));
                    } else {

                        reportsNumericTextfieldItemName
                                .setTextColor(jobDetails
                                        .getResources()
                                        .getColor(
                                                R.color.textManadatoryFieldsReportsJobDetail));
                    }
                }

                if (jobDetails.getUpdatedValueOfStatus() != KEYS.JObDetail.JOB__STARTED) {
                    if (noteData != null) {
                        noteData.setEnabled(false);
                        txtBarcodeIcon.setEnabled(false);
                    }

                    view.setEnabled(false);
                } else {
                    if (noteData != null) {
                        noteData.setEnabled(true);
                        txtBarcodeIcon.setEnabled(true);
                    }
                    view.setEnabled(true);
                }

                // ........................LISTENER...FOR...BARCODE...ICON...STARTS...........................
                txtBarcodeIcon.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        reportsJobDetailFragmentHelper
                                .setChildIndexSend(childIndex);
                        reportsJobDetailFragmentHelper
                                .setGroupIndexSend(groupIndex);
                        Intent it = new Intent(jobDetails, CodeScannerActivity.class);
                        reportsJobDetailFragment.startActivityForResult(it,
                                RequestCode.REQUEST_CODE_NUMERIC_BARCODE);
                    }
                });

                // *************************LISTENER...FOR...BARCODE...ICON...ENDS*******************************
                noteData.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        enterDataDialog = new EnterNumericDataDialog(jobDetails,
                                new EnterNumericDataDialog.EnterDialogInterface() {

                                    @Override
                                    public void doOnModifyClick(String data,
                                                                EditText view) {

                                        Logger.log("ExpandableListViewAdapter",
                                                "Called From type 2");
                                        // if (!TextUtils.isEmpty(data)) {
                                        /*
                                         * data = arrondi(Double.parseDouble(data))
                                         * + "";
                                         */

                                        // }

                                        if (((data.trim()).equals("."))
                                                || ((data.trim()).equals("-"))) {

                                            /*
                                             * dataAccessObject.updateValue("" , "",
                                             * items.getIdItem(), idInterv, conf,
                                             * items.getFlReserve());
                                             *
                                             * items.setValeurNet("");
                                             * reportsJobDetailFragmentHelper
                                             * .refreshItem(groupIndex);
                                             *
                                             * DialogUtils.showInfoDialog(
                                             * jobDetails, jobDetails
                                             * .getString(R.string
                                             * .textPleaseEnterCorrectValue));
                                             */

                                            view.setError(jobDetails
                                                    .getString(R.string.textPleaseEnterCorrectValue));
                                            view.requestFocus();

                                        } else {
                                            dataAccessObject.updateValue(
                                                    data.trim(), "",
                                                    items.getIdItem(), idInterv,
                                                    conf, items.getFlReserve(), items.getNomItem(), items.getIteration());

                                            items.setValeurNet(data.trim());
                                            reportsJobDetailFragmentHelper
                                                    .refreshItem(groupIndex, childIndex, 0);

                                            enterDataDialog.dismiss();
                                        }
                                    }

                                    @Override
                                    public void doOnCancelClick() {
                                        hideKeyboard();
                                    }
                                }, reportsNumericTextfieldItemName.getText()
                                .toString(), noteData.getText().toString());

                        enterDataDialog.show();

                    }
                });

                ImageView imgph = (ImageView) view.findViewById(R.id.itemImage);
                if ((photo != null) && (photo.length != 0)) {

//                    final Bitmap btImag = BitmapFactory.decodeByteArray(photo, 0,
//                            photo.length, opt);
//                    // BitmapDrawable bmd = new BitmapDrawable(resiseBitmap(imgph,
//                    // btImag));
//                    imgph.setImageBitmap(btImag);

                    //new changes
                    Glide.with(jobDetails)
                            .load(photo)
                            .asBitmap()
                            .override(200, 200)
                            .fitCenter()
                            .placeholder(R.drawable.library_iicon)
                            .into(imgph);

                    imgph.setVisibility(View.VISIBLE);

                    imgph.setOnClickListener(new OnClickListener() {
                        public void onClick(final View v) {

                            DisplayMetrics displaymetrics = new DisplayMetrics();
                            jobDetails.getWindowManager().getDefaultDisplay()
                                    .getMetrics(displaymetrics);

                            // Bitmap bm2 = dataAccessObject.resizeBitmap(btImag ,
                            // displaymetrics.widthPixels ,
                            // displaymetrics.heightPixels);

//                            Bitmap resized = Bitmap.createScaledBitmap(btImag,
//                                    displaymetrics.widthPixels,
//                                    displaymetrics.heightPixels, true);
//                            showImg(v, btImag);

                            Glide.with(jobDetails)
                                    .load(photo)
                                    .asBitmap()
                                    .fitCenter()
                                    .placeholder(R.drawable.library_iicon)
                                    .into(new SimpleTarget<Bitmap>(displaymetrics.widthPixels, displaymetrics.heightPixels) {
                                        @Override
                                        public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                            showImg(v, resource);
                                        }
                                    });
                        }
                    });

                } else {
                    imgph.setVisibility(View.GONE);
                }

                android.widget.TextView commentIv = (android.widget.TextView) view
                        .findViewById(R.id.commentIv);
                commentIv.setTypeface(fontAwesome);
                if (gt != null && gt.getFlCommentsRapport() == 0) {
                    commentIv.setVisibility(View.GONE);

                }

                final boolean isWriteable = jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED;

                commentIv.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        EnterCommentDialog enterCommentDialog = new EnterCommentDialog(
                                jobDetails,
                                new EnterCommentDialog.EnterDialogInterface() {

                                    @Override
                                    public void doOnModifyClick(String data) {
                                        dataAccessObject.updateValue(
                                                getValueFormat(
                                                        items.getValeurNet(),
                                                        items.getIdTypeItem()),
                                                data, items.getIdItem(), idInterv,
                                                conf, items.getFlReserve(), items.getNomItem(), items.getIteration());
                                        reportsJobDetailFragmentHelper
                                                .refreshItem(groupIndex, childIndex, 0);
                                    }

                                    @Override
                                    public void doOnCancelClick() {
                                        hideKeyboard();
                                    }
                                }, jobDetails.getString(R.string.enterComment).toUpperCase(),
                                items.getComItem(), isWriteable);

                        enterCommentDialog.show();

                    }
                });

                if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED || jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__COMPLETE) {
                    if (commentIv != null) {
                        commentIv.setEnabled(true);
                    }
                } else {
                    if (commentIv != null) {
                        commentIv.setEnabled(false);
                    }
                }

                int flReserve = items.getFlReserve();
                /*
                 * New Changes.....
                 *
                 *
                 * Seperate buttons for Status (Complaint, Resolved & Unresolved).
                 * If a status is selected, update the current flag value to DB. If
                 * a status is unselected (ie., Same status is again clicked),
                 * change the flag value as 0 in DB.
                 *
                 * And if the status is changed, change the color of the line strip
                 * in left of sub-category's layout. For Compliant - Orange,
                 * Resolved - Green, Unresolved - Red, if none is selected then
                 * change the color as White
                 */
                final LinearLayout linCompliant = (LinearLayout) view
                        .findViewById(R.id.linCompliantContainer);
                final LinearLayout linResolved = (LinearLayout) view
                        .findViewById(R.id.linResolvedContainer);
                final LinearLayout linUnresolved = (LinearLayout) view
                        .findViewById(R.id.linUnresolvedContainer);

                // set the boxframe for child
                LinearLayout parentLayout = (LinearLayout) view
                        .findViewById(R.id.ParentLayoutReport);

                if (flReserve == 1) {
                    linCompliant
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_compliant_status);
                } else if (flReserve == 2) {
                    linUnresolved
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_unresolved_status);
                } else if (flReserve == 3) {
                    linResolved
                            .setBackgroundResource(R.drawable.boxframe_report_status);
                    parentLayout
                            .setBackgroundResource(R.drawable.boxframe_resolved_status);
                }
                final int i = flReserve;

                // ........................LISTENER...FOR...STATUS...BUTTONS...STARTS...HERE............................
                linCompliant.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (i != 1) {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 1, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        } else {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }
                    }
                });
                linResolved.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (i != 3) {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 3, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        } else {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }
                    }
                });
                linUnresolved.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (i != 2) {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 2, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        } else {
                            dataAccessObject.updateValue(
                                    getValueFormat(items.getValeurNet(),
                                            items.getIdTypeItem()),
                                    items.getComItem(), items.getIdItem(),
                                    idInterv, conf, 0, items.getNomItem(), items.getIteration());
                            reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 1);
                        }
                    }
                });

                // ........................LISTENER...FOR...STATUS...BUTTONS...ENDS...HERE............................

                if (jobDetails.getUpdatedValueOfStatus() != KEYS.JObDetail.JOB__STARTED) {
                    linCompliant.setEnabled(false);
                    linResolved.setEnabled(false);
                    linUnresolved.setEnabled(false);
                    swipeLayout.getSurfaceView().setEnabled(false);
                } else {
                    linCompliant.setEnabled(true);
                    linResolved.setEnabled(true);
                    linUnresolved.setEnabled(true);
                    swipeLayout.getSurfaceView().setEnabled(true);
                }


            }

        }

        //new changes for hiding keyboard
//        if (jobDetails != null)
//            CommonUtils.hideKeyboardNew(jobDetails);

//        int count = index * baseCount;
//        if (count < familles.size()) {
//            count= count;
//        } else {
//            count=  familles.size();
//        }
//
//        if (groupPosition == count-1) {
//            DialogUtils.dismissProgressDialog();
//        }


        return view;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.ExpandableListAdapter#getChild(int, int)
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {

        Log.e("position", "familles size: " + groupPosition + "\n" + childPosition + "\n" + familles.size());

        if (familles != null && familles.size() > 0 && groupPosition < familles.size()) {
            if (childPosition < familles.get(groupPosition).getItems().size())
                return familles.get(groupPosition).getItems().get(childPosition);
            else
                return null;
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.ExpandableListAdapter#getChildId(int, int)
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {

        return childPosition;
    }

    /**
     * Sets font awesome typeface for textview.
     */
    private void setFATypeFace(android.widget.TextView txtView) {
        Typeface typeFace = Typeface.createFromAsset(jobDetails.getAssets(), jobDetails
                .getResources().getString(R.string.fontName_fontAwesome));
        txtView.setTypeface(typeFace);
    }

    protected void deletePhoto(final String cmtr, final String idInterv,
                               final int idItem, final int groupIndex, final int childIndex, final int iteration) {


        AlertDialog.Builder adbC = new AlertDialog.Builder(jobDetails);

        adbC.setMessage(R.string.txt_confirm);
        adbC.setPositiveButton(R.string.textYesLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Item item = dataAccessObject.getItem(idInterv, idItem, iteration);
                        dataAccessObject.updateValue("", item.getComItem(),
                                idItem, idInterv, 0, item.getFlReserve(), item.getNomItem(), item.getIteration());

                        dataAccessObject.deletePhotoImage(cmtr, idInterv, iteration);
                        reportsJobDetailFragmentHelper.refreshItem(groupIndex, childIndex, 0);

                    }
                });
        adbC.setNegativeButton(R.string.textNoLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        adbC.show();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
     */
    @Override
    public int getChildrenCount(int groupPosition) {

        return this.familles.get(groupPosition).getItems().size();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.ExpandableListAdapter#getGroup(int)
     */
    @Override
    public Object getGroup(int groupPosition) {
        return familles.get(groupPosition);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.ExpandableListAdapter#getGroupCount()
     */
    @Override
    public int getGroupCount() {

        int count = index * baseCount;

        if (count < familles.size()) {

            return count;
        } else {
            return familles.size();
        }

//        return familles.size();
    }

    /**
     * Sets the index position.
     *
     * @param index the new index position
     */
    public void setIndexPosition(int index) {

        this.index = index;

    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.ExpandableListAdapter#getGroupId(int)
     */
    @Override
    public long getGroupId(int groupPosition) {

        return groupPosition;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean,
     * android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        Logger.log("onGroupCalled", "onGroupCalle");
        LayoutInflater layoutInflater = (LayoutInflater) jobDetails
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final Families famille = (Families) getGroup(groupPosition);

        int obligator = infoItems(famille.getItems());

        int id = famille.getIdFamily();

        final int iteration = famille.getIteration();

        int originalCount = famille.getIterationCount();

        int isSharedBlock = famille.getIsSharedBlock();

        View view;
        if (isSharedBlock == 0) {
            view = layoutInflater.inflate(R.layout.layout_category, null);
        } else {

            view = layoutInflater.inflate(R.layout.layout_category_shared_block, null);
            TextView txtSBCount = (TextView) view.findViewById(R.id.txt_sb_count);
            ImageView imgAddSB = (ImageView) view.findViewById(R.id.iv_add_SB);
            android.widget.TextView txtCloseIcon = (android.widget.TextView) view.findViewById(R.id.txtDeleteItem);

            int max = famille.getMax();
            int min = famille.getMin();

            txtSBCount.setText(String.valueOf(originalCount));

            //if min & max are 1, then hide the count.
            if (min == 1 && max == 1) {
                txtSBCount.setVisibility(View.GONE);
            }

            //set visibility for add button according to the max value.
            //add button should be visible for the last family of a shared block, also the last shared block should not be the maximum value
            //if max == 0, then there is no limit, we can show the add button in the last family of a shared block

            int sbCount = dataAccessObject.getSharedBlockCount(famille.getIdFamily(), idInterv);
            if ((originalCount == sbCount) &&
                    ((max == 0) || (max != originalCount))) {
                imgAddSB.setVisibility(View.VISIBLE);
            } else {
                imgAddSB.setVisibility(View.GONE);
            }

            //set visibility for delete button according to the min value.
            if ((min != 0) && (originalCount <= min)) {
                txtCloseIcon.setVisibility(View.GONE);
            } else {
                txtCloseIcon.setVisibility(View.VISIBLE);
            }

            if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {
                imgAddSB.setImageResource(R.drawable.add_icon);
            } else {
                imgAddSB.setImageResource(R.drawable.add_icon_disable);
            }

            imgAddSB.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mStartBtnClickTime < 1500) {
                        return;
                    }
                    mStartBtnClickTime = SystemClock.elapsedRealtime();

                    if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {
                        boolean isSBAdded = dataAccessObject.addSharedBlock(idUser, idReportModel, famille.getIdFamily(), idInterv, famille.getIdEquip(),
                                famille.getIteration() + 1, famille.getNameFamily(), famille.getObligatoire(), famille.getPosition());
                        if (isSBAdded) {
                            Toast.makeText(jobDetails, jobDetails.getString(R.string.txt_ack_msg), Toast.LENGTH_SHORT).show();
//                            reportsJobDetailFragmentHelper.doOnSyncronize();
                            reportsJobDetailFragmentHelper.doOnSyncronizeNew(groupPosition);

                            EventBus.getDefault().post(new ReportViewUpdateEvent());
                        } else {
                            Toast.makeText(jobDetails, jobDetails.getString(R.string.msg_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            txtCloseIcon.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(jobDetails);
                    alertDialog.setTitle(jobDetails.getString(R.string.txt_shared_block));
                    alertDialog.setMessage(jobDetails.getString(R.string.txt_delete_shared_block));

                    alertDialog.setPositiveButton(jobDetails.getString(R.string.textYesLable), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int delete) {

                            if (SystemClock.elapsedRealtime() - mStartBtnClickTime < 1500) {
                                return;
                            }
                            mStartBtnClickTime = SystemClock.elapsedRealtime();

                            if (jobDetails.getUpdatedValueOfStatus() == KEYS.JObDetail.JOB__STARTED) {
                                //first, delete all the items in the shared block
                                boolean isItemsDeleted = dataAccessObject.deleteSBItems(famille.getIdFamily(), idInterv, iteration);
                                boolean isSBDeleted = false;

                                //after deleting all the items, delete the shared block
                                if (isItemsDeleted) {
                                    isSBDeleted = dataAccessObject.deleteSharedBlock(famille.getIdFamily(), idInterv, iteration);
                                }

                                //refresh the list after deleting the shared blocks
                                if (isSBDeleted) {
                                    Toast.makeText(jobDetails, jobDetails.getString(R.string.txt_ack_msg), Toast.LENGTH_SHORT).show();
//                                    reportsJobDetailFragmentHelper.doOnSyncronize();
                                    reportsJobDetailFragmentHelper.doOnSyncronizeNew(groupPosition - 1);

                                    EventBus.getDefault().post(new ReportViewUpdateEvent());
                                } else {
                                    Toast.makeText(jobDetails, jobDetails.getString(R.string.msg_error), Toast.LENGTH_SHORT).show();
                                }
                            }


                        }
                    });
                    alertDialog.setNegativeButton(jobDetails.getString(R.string.textNoLable), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();

                }
            });
        }

        TextView counterIssue = (TextView) view
                .findViewById(R.id.issuesCounterTv);
        TextView categoryName = (TextView) view
                .findViewById(R.id.titlecategoryTv);

        if (obligator == 3) {
            categoryName.setTextColor(jobDetails.getResources().getColor(
                    R.color.textColorReportsCategoryJobDetail));
        } else if (obligator == 2) {
            categoryName.setTextColor(jobDetails.getResources().getColor(
                    R.color.grayTextColorTitleTvDiscriptionJobDetails));
//            categoryName.setTextColor(jobDetails.getResources().getColor(
//                    R.color.textColorOrange));
        } else if (obligator == 1) {
            categoryName.setTextColor(jobDetails.getResources().getColor(
                    R.color.textColorGreen));

        }

        ImageView iconCollapseExpand = (ImageView) view
                .findViewById(R.id.expandCollapseIcon);

        if (isExpanded) {
            iconCollapseExpand.setImageResource(R.drawable.arrow_up);
        } else {
            iconCollapseExpand.setImageResource(R.drawable.arrow_down);
        }

        categoryName.setText(famille.getNameFamily());
        if ((id != -1) && (id != -2)) {
            counterIssue.setVisibility(View.VISIBLE);

            // resolved google doc item 138
            if (dataAccessObject.getCountResevation(idInterv, id, iteration).equals("0"))
                counterIssue.setVisibility(View.INVISIBLE);
            else
                counterIssue.setText(dataAccessObject.getCountResevation(
                        idInterv, id, iteration));

        } else {
            counterIssue.setVisibility(View.INVISIBLE);
        }

        view.setFilterTouchesWhenObscured(true);

        return view;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.ExpandableListAdapter#hasStableIds()
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.ExpandableListAdapter#isChildSelectable(int, int)
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /** The on click listener. */
//	OnClickListener onClickListener = new OnClickListener() {
//
//		@Override
//		public void onClick(View v) {
//			int id = v.getId();
//
//			if (id == R.id.dataDate) {
//
//				Logger.log("ExpandableListViewAdapter", "Called From type 3");
//				showDate2((Item) v.getTag(), v);
//			} else if (id == R.id.dataHour) {
//
//				Logger.log("ExpandableListViewAdapter", "Called From type 4");
//				Item item = (Item) v.getTag();
//				showHours(
//						v,
//						item.getIdItem() + "",
//						getValueFormat(item.getValeurNet(),
//								item.getIdTypeItem()), item.getNomItem(), item);
//
//			} else {
//			}
//		}
//	};

    /**
     * Show date2.
     *
     * @param Item        the item
     * @param viewPressed the view pressed
     */
    @SuppressLint("SimpleDateFormat")
    public void showDate2(final Item Item, final int childPosition, View viewPressed) {
        /**
         * For samsung note 3, we had a crash while selecting date from a date picker.
         * It's a bug with lollipop os devices in samsung, so we use the holo the for the samsung devices with lollipop os.
         */
//        Context context = jobDetails;
//        if (isBrokenSamsungDevice()) {
//            context = new ContextThemeWrapper(jobDetails, android.R.style.Theme_Holo_Light_Dialog);
//        }

        LayoutInflater factory = LayoutInflater.from(jobDetails);
        final View alertDialogView2 = factory.inflate(
                R.layout.show_date_picker, null);

        AlertDialog.Builder adb = new AlertDialog.Builder(jobDetails);
        adb.setView(alertDialogView2);
        adb.setTitle(R.string.textDateSmallLable);
        adb.setIcon(R.drawable.cal_icon);
        final int conf = 0;
        final DatePicker pickStartDate = (DatePicker) alertDialogView2
                .findViewById(R.id.start_date);
        final android.widget.TextView txtFullDate = (android.widget.TextView) alertDialogView2
                .findViewById(R.id.txt_full_date);

        //Hide the full date view in picker, if it is above or from lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            txtFullDate.setVisibility(View.GONE);
        }

        adb.setPositiveButton(jobDetails.getString(R.string.ok).toUpperCase(),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        int j, m;
                        String jj, mm, val;

                        j = pickStartDate.getDayOfMonth();

                        if (j < 10)
                            jj = "0" + String.valueOf(j);
                        else
                            jj = String.valueOf(j);

                        m = pickStartDate.getMonth();
                        m++;
                        if (m < 10)
                            mm = "0" + String.valueOf(m);
                        else
                            mm = String.valueOf(m);

                        val = jj + "/" + mm + "/" + pickStartDate.getYear();

                        dataAccessObject.updateValue(val, Item.getComItem(),
                                Item.getIdItem(), idInterv, conf,
                                Item.getFlReserve(), Item.getNomItem(), Item.getIteration());
                        // buttonPressed.setText(val);

                        Item.setValeurNet(val);
                        reportsJobDetailFragmentHelper.refreshItem(Item
                                .getGroupPosition(), childPosition, 0);
                        // dao.deleteSignature(idInterv,"SIGN_CLIENT");
                    }
                });

        adb.setNegativeButton(R.string.textCancelLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        // new changes......
        // To show the date changed in a textview abbove the date pickeer.

        Calendar thisDay = Calendar.getInstance();

        txtFullDate.setText(DateFormatUtils.getDateString(
                thisDay.get(Calendar.DAY_OF_MONTH),
                thisDay.get(Calendar.MONTH), thisDay.get(Calendar.YEAR)));

        pickStartDate.init(thisDay.get(Calendar.YEAR),
                thisDay.get(Calendar.MONTH),
                thisDay.get(Calendar.DAY_OF_MONTH),
                new OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {

                        txtFullDate.setText(DateFormatUtils.getDateString(
                                dayOfMonth, monthOfYear, year));
                    }
                });

        adb.show();

    }

    // /**
    // * Gets the day in string format.
    // *
    // * @param day
    // * @param month
    // * @param year
    // * @return
    // */
    // public String getDateString(int day, int month, int year) {
    //
    // final SimpleDateFormat fullDateFormat = new SimpleDateFormat(
    // "EEEE, dd MMMM yyyy");
    // final SimpleDateFormat rawDateFormat = new SimpleDateFormat(
    // "yyyy/MM/dd");
    //
    // String strDate = null;
    // String myDate = String.valueOf(day);
    // String myMonth = String.valueOf(month);
    //
    // if (myDate.length() == 1) {
    // myDate = "0" + day;
    // }
    // if (myMonth.length() == 1 && month < 9) {
    // myMonth = "0" + (month + 1);
    // } else {
    // myMonth = String.valueOf((month + 1));
    // }
    //
    // Date date = null;
    // String strDate1 = year + "/" + myMonth + "/" + myDate;
    // try {
    // date = rawDateFormat.parse(strDate1);
    // } catch (ParseException e) {
    // e.printStackTrace();
    // }
    //
    // strDate = fullDateFormat.format(date);
    // return strDate;
    //
    // }

    /**
     * Show hours.
     *
     * @param viewPressed the view pressed
     * @param idItem      the id item
     * @param valItem     the val item
     * @param nmItem      the nm item
     * @param item        the item
     */
    @SuppressWarnings("deprecation")
    public void showHours(View viewPressed, final String idItem,
                          String valItem, String nmItem, final Item item, final int childPosition) {

        LayoutInflater factory = LayoutInflater.from(jobDetails);
        final View alertDialogView2 = factory.inflate(
                R.layout.show_time_picker, null);
        final int conf = 0;

        TimePicker timePicker = (TimePicker) alertDialogView2
                .findViewById(R.id.StartTime);
        boolean isFormat24 = android.text.format.DateFormat
                .is24HourFormat(jobDetails);
        timePicker.setIs24HourView(isFormat24);
        timePicker.setCurrentHour(1);
        timePicker.setCurrentMinute(30);
        timePicker.setOnTimeChangedListener(mStartTimeChangedListener);
        if (!valItem.equals("")) {
            Date date = getDateFromStrHour(valItem);
            timePicker.setCurrentHour(date.getHours());
            timePicker.setCurrentMinute(date.getMinutes());
        }
        AlertDialog.Builder adb = new AlertDialog.Builder(jobDetails);
        adb.setView(alertDialogView2);

        adb.setTitle(nmItem);
        adb.setIcon(R.drawable.time_icon);

        adb.setPositiveButton(jobDetails.getString(R.string.ok).toUpperCase(),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        TimePicker pickStartTime = (TimePicker) alertDialogView2
                                .findViewById(R.id.StartTime);

                        int l = pickStartTime.getCurrentHour();
                        String str = String.valueOf(l);
                        String val;
                        if (l < 10)
                            str = "0" + String.valueOf(l);

                        int p = pickStartTime.getCurrentMinute();
                        String str2 = String.valueOf(p);
                        if (p < 10)
                            str2 = "0" + String.valueOf(p);

                        val = str + ":" + str2;

                        dataAccessObject.updateValue(val, item.getComItem(),
                                Integer.parseInt(idItem), idInterv, conf,
                                item.getFlReserve(), item.getNomItem(), item.getIteration());
                        // hoursButton.setText(val);

                        item.setValeurNet(val);
                        reportsJobDetailFragmentHelper.refreshItem(item
                                .getGroupPosition(), childPosition, 0);
                        // dao.deleteSignature(idInterv,"SIGN_CLIENT");

                    }
                });

        adb.setNegativeButton(R.string.textCancelLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        adb.show();
    }

    /**
     * The m start time changed listener.
     */
    private TimePicker.OnTimeChangedListener mStartTimeChangedListener = new TimePicker.OnTimeChangedListener() {

        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
            updateDisplay(view, hourOfDay, minute);
        }
    };

    /**
     * Update display.
     *
     * @param timePicker the time picker
     * @param hourOfDay  the hour of day
     * @param minute     the minute
     */
    private void updateDisplay(TimePicker timePicker, int hourOfDay, int minute) {
        int nextMinute = 0;
        timePicker.setOnTimeChangedListener(mNullTimeChangedListener);
        if (minute >= 55 && minute <= 59)
            nextMinute = 55;
        else if (minute >= 50)
            nextMinute = 50;
        else if (minute >= 45)
            nextMinute = 45;
        else if (minute >= 40)
            nextMinute = 40;
        else if (minute >= 35)
            nextMinute = 35;
        else if (minute >= 30)
            nextMinute = 30;
        else if (minute >= 25)
            nextMinute = 25;
        else if (minute >= 20)
            nextMinute = 20;
        else if (minute >= 15)
            nextMinute = 15;
        else if (minute >= 10)
            nextMinute = 10;
        else if (minute >= 5)
            nextMinute = 5;
        else if (minute >= 0)
            nextMinute = 0;
        else {
            nextMinute = 0;
        }
        if (minute - nextMinute == 1) {
            if (minute >= 55 && minute <= 59)
                nextMinute = 00;
            else if (minute >= 50)
                nextMinute = 55;
            else if (minute >= 45)
                nextMinute = 50;
            else if (minute >= 40)
                nextMinute = 45;
            else if (minute >= 35)
                nextMinute = 40;
            else if (minute >= 30)
                nextMinute = 35;
            else if (minute >= 25)
                nextMinute = 30;
            else if (minute >= 20)
                nextMinute = 25;
            else if (minute >= 15)
                nextMinute = 20;
            else if (minute >= 10)
                nextMinute = 15;
            else if (minute >= 5)
                nextMinute = 10;
            else if (minute > 0)
                nextMinute = 5;
            else {
                nextMinute = 5;
            }
        }

        timePicker.setCurrentMinute(nextMinute);
        timePicker.setOnTimeChangedListener(mStartTimeChangedListener);
    }

    /**
     * The m null time changed listener.
     */
    private TimePicker.OnTimeChangedListener mNullTimeChangedListener = new TimePicker.OnTimeChangedListener() {

        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

        }
    };

    /**
     * Gets the date from str hour.
     *
     * @param mDate the m date
     * @return the date from str hour
     */
    public Date getDateFromStrHour(String mDate) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        Date date;
        try {
            date = df.parse(mDate);
            return date;
        } catch (ParseException e) {
            Logger.printException(e);
            return new Date();
        }
    }

    /**
     * Gets the value format.
     *
     * @param value      the value
     * @param idTypeItem the id type item
     * @return the value format
     */
    private String getValueFormat(String value, int idTypeItem) {

        String locale = jobDetails.getResources().getConfiguration().locale
                .getDisplayName();
        String langue = locale.substring(0, locale.indexOf(" "));
        switch (idTypeItem) {
            case 0:
                if (!TextUtils.isEmpty(value)) {
                    int val = 0;
                    try {
                        val = Integer.parseInt(value.toString());
                    } catch (NumberFormatException ex) {
                        ex.printStackTrace();
                        ex.getMessage();
                    }

                    if (val == 1) {
                        value = jobDetails.getString(R.string.textComplaintLable);
                    } else if (val == 2) {
                        value = jobDetails
                                .getString(R.string.textNotComplaintLable);
                    }

                } else {
                    value = jobDetails.getString(R.string.non_renseigne);
                }
                break;

            case 6:
                if (!TextUtils.isEmpty(value) && langue.equalsIgnoreCase("english")) {

                    String[] tab = value.split(" ");
                    String[] tab2 = tab[0].split("/");
                    String jj, mm;

                    if (Integer.parseInt(tab2[0]) < 10 && tab2[0].length() == 1)
                        jj = "0" + tab2[0];
                    else
                        jj = tab2[0];

                    if (Integer.parseInt(tab2[1]) < 10 && tab2[1].length() == 1)
                        mm = "0" + tab2[1];
                    else
                        mm = tab2[1];
                    value = mm + "/" + jj + "/" + tab2[2] + " " + tab[1];

                }
                break;

            case 7:
                if (!TextUtils.isEmpty(value)) {
                    int val = 0;
                    try {
                        val = Integer.parseInt(value);
                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                    if (val == 1)
                        value = jobDetails.getString(R.string.exist);
                    else
                        value = jobDetails.getString(R.string.notExist);

                } else
                    value = jobDetails.getString(R.string.notExist);

        }

        return value;
    }

    /**
     * Info items.
     *
     * @param vect the vect
     * @return the int
     */
    private int infoItems(Vector<Item> vect) {
        // =cleanListItem(dao.getAllItem(idIntervention,idFamille));
        Enumeration<Item> en = vect.elements();
        int drp = 0;

        while (en.hasMoreElements()) {

            Item itm = en.nextElement();
            if (itm.getOblig() == 1 && itm.getValItem() == 0)
                return 3;
            if (itm.getOblig() == 0 && itm.getValItem() == 0)
                drp = 1;
        }
        if (drp == 1)
            return 2;
        else
            return 1;
    }

    /**
     * calulates round off.
     *
     * @param val the val
     * @return the double
     */
    public double arrondi(double val) {
        return (Math.floor(val * 100 + 0.5)) / 100;
    }

    /**
     * Show img.
     *
     * @param v      the v
     * @param btImag the bt imag
     */
    public void showImg(View v, Bitmap btImag) {

        final Dialog nagDialog = new Dialog(jobDetails,
                android.R.style.Theme_Translucent_NoTitleBar);
        nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        nagDialog.setCancelable(true);
        nagDialog.setContentView(R.layout.item_image);

        ImageView btnClose = (ImageView) nagDialog.findViewById(R.id.closeBtn);
        TouchImageView ivPreview = (TouchImageView) nagDialog
                .findViewById(R.id.imageItem);
        //

        // innerFrame.getLayoutParams().height = (deviceWidth * 60) / 100;
        // innerFrame.getLayoutParams().width = (deviceWidth * 60) / 100;
        //
        ivPreview.setImageBitmap(btImag);

        btnClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (nagDialog != null)
                    nagDialog.dismiss();
            }
        });
        nagDialog.show();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.BaseExpandableListAdapter#notifyDataSetChanged()
     */
    @Override
    public void notifyDataSetChanged() {

        gt = dataAccessObject.getAcces();

        super.notifyDataSetChanged();
    }

    // ........................FETCHING...LAT...LONG..&..ADDRESS...BLOCK...STARTS...HERE.....................................

    // ......................OVERRIDE...METHODS...FOR...GOOGLE.PLAY.SERVICE.LISTENER...STARTS...HERE................................
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (connectionResult.hasResolution()) {
            try {

                // Start an Activity that tries to resolve the error
                connectionResult
                        .startResolutionForResult(
                                jobDetails,
                                SynchroteamUitls.GOOGLE_PLAY_SERVICE_CONNECTION_REQUEST_CODE);

                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */

            } catch (IntentSender.SendIntentException e) {

                // Log the error
                Logger.printException(e);
            }
        } else {

            // If no resolution is available, display a dialog to the user with
            // the error.

        }

    }

    @Override
    public void onConnected(Bundle arg0) {

    }

    // ****************************OVERRIDE...METHODS...FOR...GOOGLE.PLAY.SERVICE.LISTENER...ENDS...HERE************************

    LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            stopUsingGPS();
            switch (ids) {
                case R.id.mapAddressIcon:
                    DialogUtils.dismissProgressDialog();
                    stopUsingGPS();
                    afficherAdresse(location.getLatitude(), location.getLongitude());
                    break;

            }
        }
    };

    /**
     * Stop using gps.
     */
    public void stopUsingGPS() {

        if ((locationClient != null) && (locationClient.isConnected())) {
            // locationClient.removeLocationUpdates(locationListener);
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    locationClient, locationListener);
        }
    }

    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(jobDetails);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status

            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode,
                    jobDetails, 0);
            dialog.show();

            return false;
        }
    }

    /**
     * Show settings alert.
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(jobDetails);
        alertDialog.setTitle(jobDetails.getString(R.string.textAlertLable) + "!");
        alertDialog.setMessage(jobDetails
                .getString(R.string.textEnableLocationService));
        alertDialog.setPositiveButton(R.string.textYesLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        jobDetails.startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton(R.string.textNoLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    /**
     * code of previous developer.
     *
     * @param v the v
     */
    public void geocoder(View v, int groupPosition, int childPosition,
                         int groupIndex) {
        items = (Item) getChild(groupPosition, childPosition);
        this.grpIndex = groupIndex;
        if (servicesConnected()) {

            boolean isGPSEnabled = false;
            boolean isNetworkEnabled = false;

            try {
                locationManager = (LocationManager) jobDetails
                        .getSystemService(JobDetails.LOCATION_SERVICE);

                isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                isNetworkEnabled = Build.FINGERPRINT.contains("generic") ? true : locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


                if (!isGPSEnabled && !isNetworkEnabled) {
                    showSettingsAlert();
                } else {


                    if (ActivityCompat.checkSelfPermission(jobDetails, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(jobDetails, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        reportsJobDetailFragment.callingPermissionLocation();
                    } else {
                        callingLocationFunctionalities();
                    }


                }

            } catch (Exception e) {
                Logger.printException(e);
                e.printStackTrace();
            }

        }

        ids = v.getId();

    }

    @SuppressLint("MissingPermission")
    private void callingLocationFunctionalities() {
        Toast.makeText(jobDetails,
                jobDetails.getString(R.string.gps_lancer),
                Toast.LENGTH_SHORT).show();

        // locationClient.requestLocationUpdates(mLocationRequest,
        // locationListener);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                locationClient, mLocationRequest, locationListener);

        final Toast tag = Toast.makeText(jobDetails,
                jobDetails.getString(R.string.gps_delai),
                Toast.LENGTH_SHORT);
        if (myTimer != null) {
            myTimer.cancel();
            myTimer = new Timer();
        } else
            myTimer = new Timer();

        myTimer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                DialogUtils.dismissProgressDialog();
                stopUsingGPS();
                tag.show();
            }
        }, 50000);

        DialogUtils
                .showProgressDialog(
                        jobDetails,
                        jobDetails
                                .getString(R.string.textPleaseWaitLable),
                        jobDetails
                                .getString(R.string.textFetchingLocation),
                        false);
    }

    /**
     * Code of Previous developer.
     *
     * @param latitude  the latitude
     * @param longitude the longitude
     */
    private void afficherAdresse(double latitude, double longitude) {

        Geocoder geo = new Geocoder(jobDetails);
        try {

            List<Address> adresses = geo
                    .getFromLocation(latitude, longitude, 1);

            if (adresses != null && adresses.size() == 1) {
                Address adresse = adresses.get(0);

                if (adresse.getAddressLine(0) != null) {
                    rue = adresse.getAddressLine(0);
                    ville = adresse.getLocality();
                    GpsX = String.valueOf(longitude);
                    GpsY = String.valueOf(latitude);
                    String localizationAddress = latitude + "|" + longitude
                            + "|" + adresse.getAddressLine(0) + ","
                            + adresse.getLocality();
                    dataAccessObject.updateValue(localizationAddress,
                            items.getComItem(), items.getIdItem(), idInterv, 0,
                            items.getFlReserve(), items.getNomItem(), items.getIteration());
                    reportsJobDetailFragmentHelper.refreshItem(grpIndex, chdIndex, 0);

                }

                Toast.makeText(jobDetails,
                        jobDetails.getString(R.string.gps_succes),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(jobDetails,
                        jobDetails.getString(R.string.gps_error),
                        Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Logger.printException(e);
            Toast.makeText(jobDetails,
                    jobDetails.getString(R.string.gps_error),
                    Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        // TODO Auto-generated method stub

    }

    // *************************FETCHING...LAT...LONG..&..ADDRESS...BLOCK...STARTS...HERE******************************

    private static boolean isBrokenSamsungDevice() {
        return (Build.MANUFACTURER.equalsIgnoreCase("samsung")
                && isBetweenAndroidVersions(
                Build.VERSION_CODES.LOLLIPOP,
                Build.VERSION_CODES.LOLLIPOP_MR1));
    }

    private static boolean isBetweenAndroidVersions(int min, int max) {
        return Build.VERSION.SDK_INT >= min && Build.VERSION.SDK_INT <= max;
    }

    private void hideKeyboard() {
        if (jobDetails != null) {
            jobDetails.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            jobDetails.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
    }

}