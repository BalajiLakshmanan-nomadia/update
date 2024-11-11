package com.synchroteam.synchroteam;

import static com.synchroteam.utils.ImageUtil.getSavedPhoto;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.provider.Settings.System;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TypefaceSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.synchroteam.beans.Client;
import com.synchroteam.beans.NotificationItem;
import com.synchroteam.beans.Site;
import com.synchroteam.dialogs.AttachmentDialogReport;
import com.synchroteam.retrofit.models.NotificationService.jsonInfo;
import com.synchroteam.retrofit.models.mobileAuth.AuthData;
import com.synchroteam.roomDB.RoomDBSingleTone;
import com.synchroteam.roomDB.entity.NotificationEventModels;
import com.synchroteam.technicalsupport.JobDetails;
import com.synchroteam.tracking.TrackingParameterService;
import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.Description;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.beans.ModeleRapport;
import com.synchroteam.beans.Photo_Pda;
import com.synchroteam.beans.TypeIntervention;
import com.synchroteam.beans.UpdateDataBaseEvent;
import com.synchroteam.beans.UpdateUiOnSync;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.AttachmentDialogNewJob;
import com.synchroteam.dialogs.AuthenticationErrorDialog;
import com.synchroteam.dialogs.ErrorDialog;
import com.synchroteam.dialogs.SynchronizationErrorDialog;
import com.synchroteam.retrofit.ApiClientNew;
import com.synchroteam.retrofit.ApiInterface;
import com.synchroteam.retrofit.models.NotificationService.EventNotiRequest;
import com.synchroteam.retrofit.models.NotificationService.EventNotiResult;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.CommonUtils;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DateChecker;
import com.synchroteam.utils.DateFormatUtils;
import com.synchroteam.utils.DialogResponseInterface;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.RequestCode;
import com.synchroteam.utils.SharedPref;
import com.synchroteam.utils.SynchroteamUitls;
import com.synchroteam.utils.TouchImageView;
import com.synchroteam.utils.imageutils.FileUtilsNew;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Timer;

import de.greenrobot.event.EventBus;
//v51.0.3
//import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// TODO: Auto-generated Javadoc

/**
 * This Activity is Responsible for Inflating And performing actions on this New
 * created for future purpose.
 *
 * @author Ideavate.solution Job Screen.
 */
@SuppressLint("SimpleDateFormat")
public class AddDuplicateNewJob extends AppCompatActivity implements
        CommonInterface, ConnectionCallbacks, OnConnectionFailedListener {

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    /**
     * The alert dialog view2.
     */
    private View alertDialogView, alertDialogView2;
    private DateFormat df;
    /**
     * The id type.
     */
    private int idModele, idType;

    /**
     * The builder.
     */
    private AlertDialog.Builder builder;

    /**
     * The progress d synch.
     */
    private ProgressDialog progressDSynch;

    /**
     * The dao.
     */
    private Dao dao;

    /**
     * The pi.
     */
    private PendingIntent pi, pi1, pITrackParams;

    /**
     * The m min m.
     */
    private int mJour, mMois, mAnnee, mEndJour, mEndMois, mEndAnnee, mH1, mH2, mMin1, mMin2, mAnneeM, mMoisM,
            mJourM, mHM, mMinM;

    /**
     * The is format24.
     */
    private static boolean isFormat24;

    /**
     * The id equipement.
     */
    private int idClient, idSite, idEquipement;

    /**
     * The cp.
     */
    private String cp = "";

    /**
     * The location.
     */
    Location location;

    /**
     * The location manager.
     */
    protected LocationManager locationManager;

    /**
     * The clear metting data iv.
     */
    private ImageView clearScheduleDataIv, clearMettingDataIv;
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
    private String pays = "";
    /**
     * The _millis.
     */
    private long _millis = 0;

    /**
     * The gt.
     */
    private GestionAcces gt;

    /**
     * The my timer.
     */
    private Timer myTimer;

    /**
     * The action bar.
     */
    private ActionBar actionBar;

    /**
     * The close activity.
     */
    private boolean closeActivity = false;

    /**
     * The cancel tv.
     */
    private TextView okTv, cancelTv;
    // DateFormat dateformat = DateFormat.getTimeInstance(DateFormat.SHORT);

    /**
     * The simple date format.
     */
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);

    /**
     * The duree.
     */
    private Date duree;

    /**
     * The syncro team application.
     */
    private SyncroTeamApplication syncroTeamApplication;

    /**
     * The m location request.
     */
    private LocationRequest mLocationRequest;

    /**
     * The location client.
     */
    private GoogleApiClient locationClient;

    /**
     * The add attachment icon iv.
     */
    private ImageView addAttachmentIconIv;

    /**
     * The photo_ pdas.
     */
    private ArrayList<Photo_Pda> photo_Pdas;

    /**
     * The bitmap.
     */
    private Bitmap bitmap;

    /**
     * The user.
     */
    private User user;

    /**
     * The attachment list view.
     */
    private LinearLayout attachmentLinearView;

    /**
     * The inflater.
     */
    private LayoutInflater inflater;

    /**
     * The arror btn.
     */
    private ImageView arrorBtn;

    /**
     * The device width.
     */
    private int deviceWidth;
    /**
     * The captured path.
     */
    private String capturedPath;
    /**
     * The extention.
     */
    private String extention;

    private int resisingWidth;

    private int resisingHeight;

    private String clientName;
    private String siteName;
    private String equipmentName;
    private TextView siteEt, equipmentEt, clientNameEt;

    private String address, additionalAddress, jobType, reportTemplate,
            description,add_province;

    private int idEquipment;

    /**
     * The data access object.
     */
    private Dao dataAccessObject;

    private String idInterv;

    /**
     * The description.
     */
    private Description mDescription;

    private ScrollView scrollView;
//v51.0.3
//    private Realm realm;

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 124;
    public static final int CAMERA_PERMISSION = 125;


    private Date mDefDate, mDateStart, mDateEnd, myStartDate, myEndDate, mDefultMinOneMin;
    private EditText edtEndDate, edtStartDate, edtStartTime, edtEndTime, edtMeetingDate, edtMeetingtime;
    private Date mFinalMettingStartDate, mFinalmettingEndDate, mFinalStartTimeDate, mFinalTimeEndDate;
    ArrayList<NotificationItem> notiList;
    List<NotificationEventModels> l;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(this,"Permission allowed",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this,"Permission denied",Toast.LENGTH_SHORT).show();
                }
            });

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub KEYS.JObDetail.ID
        super.onCreate(savedInstanceState);
        Logger.log("new job", "onCreate");
        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        mDefultMinOneMin = new Date();
        try {
            mDefultMinOneMin = simpleDateFormat.parse("00:01");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String title = this.getResources().getString(
                R.string.textDuplicateLableTv).toUpperCase();
        SpannableString titleSpannable = new SpannableString(title);
        titleSpannable.setSpan(
                new TypefaceSpan(this.getResources().getString(
                        R.string.fontName_hevelicaLight)), 0,
                titleSpannable.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // actionBar.setTitle(titleSpannable);
        actionBar.setTitle(isLGDevice() ? title : titleSpannable);


        dao = DaoManager.getInstance();
        user = dao.getUser();
        gt = dao.getAcces();
        isFormat24 = android.text.format.DateFormat.is24HourFormat(this);
        setContentView(R.layout.layout_duplicate_jobs);

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        df = DateFormat
                .getDateInstance(DateFormat.LONG, Locale.US);
        try {
            duree = simpleDateFormat.parse("02:00");
        } catch (ParseException e) {
            Logger.printException(e);
        }
        photo_Pdas = new ArrayList<Photo_Pda>();
//        pi = PendingIntent.getService(AddDuplicateNewJob.this, 0, new Intent(
//                        AddDuplicateNewJob.this, TrackingService.class),
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        pi1 = PendingIntent.getService(AddDuplicateNewJob.this, 0, new Intent(
//                        AddDuplicateNewJob.this, TrackingServiceFrequency.class),
//                PendingIntent.FLAG_UPDATE_CURRENT);
        pITrackParams = initializePendingIntent();

        builder = new AlertDialog.Builder(this);

        Intent intent = getIntent();

        /*
         * idClient=intent.getIntExtra(KEYS.ClientDetial.ID_CLIENT, 0);
         * idSite=intent.getIntExtra(KEYS.NewJob.SITE_ID, -1);
         * idEquipement=intent.getIntExtra(KEYS.NewJob.EQUIPMENTS_ID, -1);
         *
         *
         * clientName=intent.getStringExtra(KEYS.ClientDetial.CLIENT_NAME);
         * siteName=intent.getStringExtra(KEYS.ClientDetial.SITE_NAME);
         * equipmentName
         * =intent.getStringExtra(KEYS.ClientDetial.EQUIPMENTS_NAME);
         *
         * address=intent.getStringExtra(KEYS.ClientDetial.ADDRESS);
         * additionalAddress
         * =intent.getStringExtra(KEYS.ClientDetial.ADDITIONAL_ADDRESS);
         * jobType=intent.getStringExtra(KEYS.ClientDetial.JOB_TYPE);
         * reportTemplate
         * =intent.getStringExtra(KEYS.ClientDetial.REPORT_TEMPLATE);
         * description=intent.getStringExtra(KEYS.ClientDetial.DESCRIPTION);
         */

        // start

        idClient = 0;
        idSite = 0;
        idEquipement = 0;

        siteName = intent.getStringExtra(KEYS.ClientDetial.SITE_NAME);
        equipmentName = intent
                .getStringExtra(KEYS.ClientDetial.EQUIPMENTS_NAME);
        idInterv = intent.getStringExtra(KEYS.JObDetail.ID);

        dataAccessObject = DaoManager.getInstance();
        duree = dataAccessObject.getJobDuration(idInterv);
        if (duree.getHours() == 0 && duree.getMinutes() == 0) {
            try {

                duree = simpleDateFormat.parse("02:00");
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        mDescription = dataAccessObject.getInterventionById(idInterv);
        reportTemplate = dataAccessObject.getModelReportNameById(idInterv);
        if (mDescription != null) {
            clientName = mDescription.getNomClient();
            address = "";
            if (mDescription.getAdresseGlobale() != null && mDescription.getAdresseGlobale().length() > 0)
                address = mDescription.getAdresseGlobale().trim();
            additionalAddress = "";
            if (mDescription.getAdresseComplement() != null && mDescription.getAdresseComplement().length() > 0)
                additionalAddress = mDescription.getAdresseComplement();
            jobType = mDescription.getTypeIntervention();
            description = mDescription.getDescriptionIntervention();
        }

        EditText addressEt, additionalAddressEt, Ajoutdescription;

        addressEt = (EditText) findViewById(R.id.addressEt);
        additionalAddressEt = (EditText) findViewById(R.id.additionalAddressEt);
        Ajoutdescription = (EditText) findViewById(R.id.Ajoutdescription);

        addressEt.setEnabled(false);
        if (!TextUtils.isEmpty(address))
            addressEt.setText(address);
        additionalAddressEt.setEnabled(false);
        if (!TextUtils.isEmpty(additionalAddress))
            additionalAddressEt.setText(additionalAddress);
        Ajoutdescription.setEnabled(false);
        if (!TextUtils.isEmpty(description))
            Ajoutdescription.setText(description);

        TextView reportTemplateLabelTv, jobTypeLabelTv;

        reportTemplateLabelTv = (TextView) findViewById(R.id.reportTemplateLabelTv);
        jobTypeLabelTv = (TextView) findViewById(R.id.jobTypeLabelTv);

        reportTemplateLabelTv.setEnabled(false);
        if (reportTemplate != null && !TextUtils.isEmpty(reportTemplate))
            reportTemplateLabelTv.setText(reportTemplate);
        jobTypeLabelTv.setEnabled(false);
        if (!TextUtils.isEmpty(jobType))
            jobTypeLabelTv.setText(jobType);

        // end
        initAll();
        // initSpinnerOne();
        // initSpinnerTwo();
        builder = new AlertDialog.Builder(this);
        okTv = (TextView) findViewById(R.id.okTv);
        cancelTv = (TextView) findViewById(R.id.cancelTv);
        clearScheduleDataIv = (ImageView) findViewById(R.id.clearScheduleDataIv);
        addAttachmentIconIv = (ImageView) findViewById(R.id.addAttachmentIconIv);
        clearMettingDataIv = (ImageView) findViewById(R.id.clearMettingDataIv);

        arrorBtn = (ImageView) findViewById(R.id.arrowButtonAttachIv);

        arrorBtn.setTag(Boolean.valueOf(false));

        arrorBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Boolean isArrowUp = (Boolean) v.getTag();
                if (isArrowUp.booleanValue()) {
                    attachmentLinearView.setVisibility(View.GONE);
                    v.setTag(Boolean.valueOf(false));
                    ((ImageView) v).setImageResource(R.drawable.arrow_down);
                } else {

                    attachmentLinearView.setVisibility(View.VISIBLE);

                    v.setTag(Boolean.valueOf(true));
                    ((ImageView) v).setImageResource(R.drawable.arrow_up);
                }

            }
        });

        attachmentLinearView = (LinearLayout) findViewById(R.id.attachmentListView);

        clearMettingDataIv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                clearMeetingData();
            }
        });

        clearScheduleDataIv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                clearScheduleData();

            }
        });

        okTv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                v.setEnabled(false);
                addIntervention(v);
            }
        });
        cancelTv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                clearAll(arg0);
            }
        });
        addAttachmentIconIv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE ){// Andsroid 14 changes
                    if (ContextCompat.checkSelfPermission(AddDuplicateNewJob.this, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) != PackageManager.PERMISSION_GRANTED){
                        callingPermissionCamera();
                    }else {
                        callingAttachmentDialog();
                    }
                }else
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                    if (ContextCompat.checkSelfPermission(AddDuplicateNewJob.this, Manifest.permission.READ_MEDIA_IMAGES) !=
                            PackageManager.PERMISSION_GRANTED) {
                        callingPermissionCamera();
                    }else {
                        ActivityCompat.requestPermissions(AddDuplicateNewJob.this,
                                new String[]{Manifest.permission.CAMERA},
                                CAMERA_PERMISSION);
                        callingAttachmentDialog();
                    }
                }else
                if (ContextCompat.checkSelfPermission(AddDuplicateNewJob.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(AddDuplicateNewJob.this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(AddDuplicateNewJob.this, Manifest.permission.CAMERA)
                                == PackageManager.PERMISSION_GRANTED) {
//                    callingPermissionCamera();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                        if (ContextCompat.checkSelfPermission(AddDuplicateNewJob.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(AddDuplicateNewJob.this, Manifest.permission.CAMERA)
                                        != PackageManager.PERMISSION_GRANTED) {
                            callingPermissionCamera();
                        } else {
                            callingAttachmentDialog();
                        }

                    } else {
                        callingPermissionCamera();
                    }
                } else {
                    callingAttachmentDialog();
                }

            }
        });

        inflater = getLayoutInflater();

        syncroTeamApplication = (SyncroTeamApplication) getApplication();

        findViewById(R.id.mapTargetCorporationIcon).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        geocoder(v);
                    }
                });

        // locationClient = new LocationClient(this, this, this);
        locationClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        mLocationRequest = LocationRequest.create();

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

        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        deviceWidth = displaymetrics.widthPixels;
        resisingWidth = (deviceWidth * 80) / 100;
        resisingHeight = (displaymetrics.heightPixels * 80) / 100;

    }

    /**
     * Intializing the pending intent for the tracking service
     *
     * @return Pending intent
     */
    private PendingIntent initializePendingIntent() {
        Intent trackingParamsIntent = new Intent(AddDuplicateNewJob.this,
                TrackingParameterService.class);
        trackingParamsIntent.putExtra("from_pending_intent", true);

        PendingIntent pendingIntent;
        //Behaviour changes supporting android 12
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // Create a PendingIntent using FLAG_IMMUTABLE
            pendingIntent= PendingIntent.getService(AddDuplicateNewJob.this,
                    0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT|
                            PendingIntent.FLAG_IMMUTABLE);
        } else {

            pendingIntent= PendingIntent.getService(AddDuplicateNewJob.this,
                    0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        return pendingIntent;
//        return PendingIntent.getService(AddDuplicateNewJob.this,
//                0, trackingParamsIntent, PendingIntent.FLAG_UPDATE_CURRENT);

    }


    /**
     * For canceling the Tracking service when conditions are met
     */
    private void cancelTrackingAlarm() {
        stopService(new Intent(AddDuplicateNewJob.this, TrackingParameterService.class));
        AlarmManager am2 = (AlarmManager) AddDuplicateNewJob.this
                .getSystemService(Context.ALARM_SERVICE);
        am2.cancel(pITrackParams);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(AddDuplicateNewJob.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(AddDuplicateNewJob.this, Manifest.permission.CAMERA)
                                    == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(AddDuplicateNewJob.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED)
                        callingAttachmentDialog();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callingLocationFunctionalities();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case CAMERA_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if ( ContextCompat.checkSelfPermission(AddDuplicateNewJob.this, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED){
                        callingAttachmentDialog();
                    }
                }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    @SuppressLint("MissingPermission")
    private void callingLocationFunctionalities() {
        Toast.makeText(AddDuplicateNewJob.this, getString(R.string.gps_lancer),
                Toast.LENGTH_SHORT).show();

        LocationServices.FusedLocationApi.requestLocationUpdates(
                locationClient, mLocationRequest, locationListener);

        final Toast tag = Toast.makeText(AddDuplicateNewJob.this,
                getString(R.string.gps_delai), Toast.LENGTH_SHORT);
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
                        this,
                        AddDuplicateNewJob.this
                                .getString(R.string.textPleaseWaitLable),
                        AddDuplicateNewJob.this
                                .getString(R.string.textFetchingLocation),
                        false);
    }


    private void callingPermissionCamera() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) ==
                            PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"Permission allowed",Toast.LENGTH_SHORT).show();
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.app_name));
                alertBuilder.setMessage(getString(R.string.str_camera_permission));
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ActivityCompat.requestPermissions(AddDuplicateNewJob.this,
                                new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED, Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
                alertBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert = alertBuilder.create();
                alert.show();
            } else {
                // Directly ask for the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED, Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            }
        }else
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // android 13 changes
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) ==
                    PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,"Permission allowed",Toast.LENGTH_SHORT).show();
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.app_name));
                alertBuilder.setMessage(getString(R.string.str_camera_permission));
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                    }
                });
                alertBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert = alertBuilder.create();
                alert.show();
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        }else

        if (ActivityCompat.shouldShowRequestPermissionRationale(AddDuplicateNewJob.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) ||
                ActivityCompat.shouldShowRequestPermissionRationale(AddDuplicateNewJob.this,
                        Manifest.permission.CAMERA)) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(AddDuplicateNewJob.this,
                    Manifest.permission.CAMERA)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AddDuplicateNewJob.this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.app_name));
                alertBuilder.setMessage(getString(R.string.str_camera_permission));
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(AddDuplicateNewJob.this,
                                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(AddDuplicateNewJob.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AddDuplicateNewJob.this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.app_name));
                alertBuilder.setMessage("Write storage permission is necessary");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(AddDuplicateNewJob.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
            } else {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AddDuplicateNewJob.this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.app_name));
                alertBuilder.setMessage(getString(R.string.str_storage_permission));
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(AddDuplicateNewJob.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
            }
        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(AddDuplicateNewJob.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    private void callingPermissionLocation() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(AddDuplicateNewJob.this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AddDuplicateNewJob.this);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle(getString(R.string.app_name));
            alertBuilder.setMessage(getString(R.string.str_location_permission));
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(AddDuplicateNewJob.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(AddDuplicateNewJob.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    private void callingAttachmentDialog() {

        AttachmentDialogNewJob attachmentDialog = new AttachmentDialogNewJob(
                AddDuplicateNewJob.this);
        attachmentDialog.show();
    }

    @Override
    protected void onDestroy() {
        DialogUtils.dismissProgressDialog();
        super.onDestroy();
    }

    public boolean isLGDevice() {
        return (Build.MANUFACTURER.contains("LG") || Build.MODEL.contains("LG"));
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v7.app.AppCompatActivity#onStop()
     */
    @Override
    public void onStop() {

        // After disconnect() is called, the client is considered "dead".
        locationClient.disconnect();

        super.onStop();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onStart()
     */
    @Override
    public void onStart() {

        super.onStart();

        /*
         * Connect the client. Don't re-start any requests here; instead, wait
         * for onResume()
         */
        locationClient.connect();

    }

    /**
     * Clear meeting data.
     */
    protected void clearMeetingData() {
        // TODO Auto-generated method stub

        EditText dateMeeting = (EditText) findViewById(R.id.dateMeeting);
        dateMeeting.setText("");
        EditText heurMeeting = (EditText) findViewById(R.id.heurMeeting);
        heurMeeting.setText("");
        clearMettingDataIv.setVisibility(View.GONE);
    }

    /**
     * Clear schedule data.
     */
    protected void clearScheduleData() {
        // TODO Auto-generated method stub

        clearScheduleDataIv.setVisibility(View.GONE);
        edtEndDate.setText("");
        edtStartDate.setText("");
        edtEndTime.setText("");
        edtStartTime.setText("");

    }

    @SuppressWarnings("deprecation")
    public void showDateM(View v) {
/**
 * For samsung note 3, we had a crash while selecting date from a date picker.
 * It's a bug with lollipop os devices in samsung, so we use the holo the for the samsung devices with lollipop os.
 */
//		Context context = this;
//		if (isBrokenSamsungDevice()) {
//			context = new ContextThemeWrapper(this, android.R.style.Theme_Holo_Light_Dialog);
//		}

        LayoutInflater factory = LayoutInflater.from(this);
        alertDialogView2 = factory.inflate(R.layout.show_date_picker, null);
        DatePicker datePicker = (DatePicker) alertDialogView2
                .findViewById(R.id.start_date);
        android.widget.TextView txtFullDate = (android.widget.TextView) alertDialogView2
                .findViewById(R.id.txt_full_date);

        //Hide the full date view in picker, if it is above or from lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            txtFullDate.setVisibility(View.GONE);
        }
        datePicker.setMinDate(java.lang.System.currentTimeMillis() - 1000);
        if (!edtMeetingDate.getText().toString().equals("")) {

            Date date = getDateFromStrDate(edtMeetingDate.getText().toString());
            datePicker.updateDate(date.getYear() + 1900,
                    date.getMonth(),
                    date.getDate());
        }

        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setView(alertDialogView2);

        // restrictPreviousDateChoice(datePicker, txtFullDate);

        adb.setTitle(R.string.textDateSmallLable);

        adb.setIcon(R.drawable.cal_icon);
        adb.setPositiveButton(getString(R.string.ok).toUpperCase(),
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                        DatePicker datePicker = (DatePicker) alertDialogView2
                                .findViewById(R.id.start_date);
                        mJourM = datePicker.getDayOfMonth();
                        mMoisM = datePicker.getMonth();
                        mAnneeM = datePicker.getYear() - 1900;
                        DateFormat df = DateFormat
                                .getDateInstance(DateFormat.LONG, Locale.US);
                        Date date = new Date(mAnneeM, mMoisM, mJourM);
                        mFinalMettingStartDate = date;
                        EditText etM = (EditText) findViewById(R.id.dateMeeting);
                        etM.setText(df.format(date));

                        EditText et = (EditText) findViewById(R.id.dateDebut);
                        if (!TextUtils.isEmpty(edtMeetingtime.getText().toString())) {
                            Date DateMettingHM = getDateFromStrHour(edtMeetingtime.getText().toString());
                            Date MettingendDate = new Date();
                            date.setHours(DateMettingHM.getHours());
                            date.setMinutes(DateMettingHM.getMinutes());
                            MettingendDate.setTime(date.getTime());
                            MettingendDate.setHours(date.getHours() + duree.getHours());
                            MettingendDate.setMinutes(date.getMinutes() + duree.getMinutes());

                            mFinalMettingStartDate.setTime(date.getTime());
                            edtStartDate.setText(df.format(date));
                            edtStartTime.setText(updateTo24HresFormat(mH1, mMin1));
                            mFinalmettingEndDate.setTime(MettingendDate.getTime());
                            edtEndTime.setText(updateTo24HresFormat(mFinalmettingEndDate.getHours(),
                                    mFinalmettingEndDate.getMinutes()));
                        } else {
                            if (!et.getText().toString().equals("")) {
                                et.setText(df.format(date));
                                mJour = datePicker.getDayOfMonth();
                                mMois = datePicker.getMonth();
                                mAnnee = datePicker.getYear() - 1900;
                            }
                        }
                        edtEndTime.setText("");
                        edtStartTime.setText("");
                        edtMeetingtime.setText("");
                        edtEndDate.setText("");
                        if (!TextUtils.isEmpty(etM.getText())) {
                            clearMettingDataIv.setVisibility(View.VISIBLE);
                        }

                    }
                });

        adb.setNegativeButton(R.string.textCancelLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        adb.show();
    }

    @SuppressWarnings("deprecation")
    public void showTimeM(View v) {
        if (!("".equals(CommonUtils.convertEditTextToString(edtMeetingDate)))) {
            LayoutInflater factory = LayoutInflater.from(this);
            alertDialogView = factory.inflate(R.layout.show_time_picker, null);
            TimePicker timePicker = (TimePicker) alertDialogView
                    .findViewById(R.id.StartTime);
            timePicker.setIs24HourView(isFormat24);
            String hd = CommonUtils.convertEditTextToString(edtMeetingtime);
            if (!hd.equals("")) {
                Date date = getDateFromStrHour(hd);
                timePicker.setCurrentHour(date.getHours());
                timePicker.setCurrentMinute(date.getMinutes());
            } else {

                int min = CommonUtils.date().getMinutes();
                int hour = CommonUtils.date().getHours();
                timePicker.setCurrentMinute(min);
                timePicker.setCurrentHour(hour);
            }

            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setView(alertDialogView);
            adb.setTitle(R.string.timeMeeting);
            adb.setIcon(R.drawable.time_icon);
            adb.setPositiveButton(getString(R.string.ok).toUpperCase(),
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            TimePicker timePicker = (TimePicker) alertDialogView
                                    .findViewById(R.id.StartTime);
                            timePicker.setIs24HourView(isFormat24);
                            mHM = timePicker.getCurrentHour();
                            mMinM = timePicker.getCurrentMinute();
                            Date currentTime = CommonUtils.calendar().getTime();
                            Date stSelTime = CommonUtils.calendar().getTime();
                            Date SelectDte = getDateFromStrDate(edtMeetingDate.getText().toString());
                            stSelTime.setHours(mHM);
                            stSelTime.setMinutes(mHM);
                            if (currentTime.compareTo(SelectDte) > 0) {
                                if (currentTime.compareTo(stSelTime) > 0) {
                                    mHM = currentTime.getHours();
                                    mMinM = currentTime.getMinutes();
                                } else {
                                    mHM = timePicker.getCurrentHour();
                                    mMinM = timePicker.getCurrentMinute();
                                }
                            } else {
                                mHM = timePicker.getCurrentHour();
                                mMinM = timePicker.getCurrentMinute();
                            }
                            mFinalmettingEndDate = new Date(mAnneeM, mMoisM, mJourM, mHM
                                    + duree.getHours(), mMinM
                                    + duree.getMinutes());
                            mH1 = mHM;
                            mMin1 = mMinM;
                            mH2 = mFinalmettingEndDate.getHours();
                            mMin2 = mFinalmettingEndDate.getMinutes();
                            mFinalMettingStartDate.setHours(mH1);
                            mFinalMettingStartDate.setMinutes(mMin1);
                            if (!TextUtils.isEmpty(edtMeetingDate.getText())) {
                                clearScheduleDataIv.setVisibility(View.VISIBLE);
                                clearMettingDataIv.setVisibility(View.VISIBLE);
                            }
                            edtMeetingtime.setText(updateTo24HresFormat(mHM, mMinM));
                            if (!edtStartDate.getText().toString().equals("")) {
                                edtStartDate.setText(df.format(mFinalMettingStartDate));
                                edtStartTime.setText(updateTo24HresFormat(mFinalMettingStartDate.getHours(), mFinalMettingStartDate.getMinutes()));
                                edtEndTime.setText(updateTo24HresFormat(mFinalmettingEndDate.getHours(), mFinalmettingEndDate.getMinutes()));
                                edtEndDate.setText(df.format(mFinalmettingEndDate));
                            }

                            if (!edtStartDate.getText().toString().equals("") &&
                                    mFinalmettingEndDate != null && mFinalMettingStartDate != null) {
                                mEndJour = mFinalmettingEndDate.getDate();
                                mEndAnnee = mFinalmettingEndDate.getYear();
                                mEndMois = mFinalmettingEndDate.getMonth();


                                mJour = mFinalMettingStartDate.getDate();
                                mMois = mFinalMettingStartDate.getMonth();
                                mAnnee = mFinalMettingStartDate.getYear();

                                mH1 = mFinalMettingStartDate.getHours();
                                mMin1 = mFinalMettingStartDate.getMinutes();
                                mH2 = mFinalmettingEndDate.getHours();
                                mMin2 = mFinalmettingEndDate.getMinutes();
                            }

                        }
                    });

            adb.setNegativeButton(R.string.textCancelLable,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            adb.show();

        } else {

            Toast.makeText(this, R.string.msg_meeting_date, Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @SuppressWarnings("deprecation")
    public void showDate(View v) {


        if (!edtMeetingDate.getText().toString().equals("")) {

            edtStartDate.setText(edtMeetingDate.getText().toString());
            if (!edtMeetingtime.getText().toString().equals("")) {
                edtStartDate.setText(edtMeetingDate.getText().toString());
                edtStartTime.setText(updateTo24HresFormat(mFinalMettingStartDate.getHours(), mFinalMettingStartDate.getMinutes()));
                edtEndTime.setText(updateTo24HresFormat(mFinalmettingEndDate.getHours(), mFinalmettingEndDate.getMinutes()));
                edtEndDate.setText(df.format(mFinalmettingEndDate));
                mEndJour = mFinalmettingEndDate.getDate();
                mEndMois = mFinalmettingEndDate.getMonth();
                mEndAnnee = mFinalmettingEndDate.getYear();
                if (!TextUtils.isEmpty(edtMeetingDate.getText())) {
                    clearScheduleDataIv.setVisibility(View.VISIBLE);
                    clearMettingDataIv.setVisibility(View.VISIBLE);
                }
            }
            Date date = getDateFromStrDate(edtMeetingDate.getText().toString());
            Calendar calendar = CommonUtils.calendar();
            calendar.setTime(date);
            mJour = calendar.get(Calendar.DAY_OF_MONTH);
            mMois = date.getMonth();
            mAnnee = calendar.get(Calendar.YEAR) - 1900;

            //v52 Changes
            mFinalTimeEndDate = mFinalmettingEndDate;
            mFinalStartTimeDate = getDateFromStrDate(edtStartDate.getText().toString());
            mFinalStartTimeDate.setHours(mFinalMettingStartDate.getHours());
            mFinalStartTimeDate.setMinutes(mFinalMettingStartDate.getMinutes());
            mH1 = mFinalStartTimeDate.getHours();
            mMin1 = mFinalStartTimeDate.getMinutes();

            if (mFinalTimeEndDate != null) {
                mEndJour = mFinalTimeEndDate.getDate();
                mEndAnnee = mFinalTimeEndDate.getYear();
                mEndMois = mFinalTimeEndDate.getMonth();
                mH2 = mFinalTimeEndDate.getHours();
                mMin2 = mFinalTimeEndDate.getMinutes();
            }


        } else {

            LayoutInflater factory = LayoutInflater.from(this);
            alertDialogView2 = factory.inflate(R.layout.show_date_picker, null);
            final DatePicker datePicker = (DatePicker) alertDialogView2
                    .findViewById(R.id.start_date);
            android.widget.TextView txtFullDate = (android.widget.TextView) alertDialogView2
                    .findViewById(R.id.txt_full_date);

            //Hide the full date view in picker, if it is above or from lollipop
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                txtFullDate.setVisibility(View.GONE);
            }
            restrictPreviousDateChoice(datePicker, txtFullDate);
            if (!edtStartDate.getText().toString().equals("")) {
                Date date = getDateFromStrDate(edtStartDate.getText().toString());
                datePicker.updateDate(date.getYear() + 1900, date.getMonth(),
                        date.getDate());
            }
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setView(alertDialogView2);
            adb.setTitle(R.string.textDateSmallLable);

            adb.setIcon(R.drawable.cal_icon);
            adb.setPositiveButton(getString(R.string.ok).toUpperCase(),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            mJour = datePicker.getDayOfMonth();
                            mMois = datePicker.getMonth();
                            mAnnee = datePicker.getYear() - 1900;
                            DateFormat df = DateFormat
                                    .getDateInstance(DateFormat.LONG, Locale.US);
                            Date date = new Date(mAnnee, mMois, mJour);

                            edtStartDate.setText(df.format(date));
                            if (!edtStartDate.getText().toString().equals(""))
                                clearScheduleDataIv.setVisibility(View.VISIBLE);
                            if (!TextUtils.isEmpty(edtStartTime.getText().toString()) ||
                                    !TextUtils.isEmpty(edtEndTime.getText().toString()) ||
                                    !TextUtils.isEmpty(edtEndDate.getText().toString())) {
                                Calendar currentCalendar = CommonUtils.calendar();
                                Date mCurrentDate = CommonUtils.date();
                                int hour = currentCalendar.get(Calendar.HOUR_OF_DAY);
                                int minute = currentCalendar.get(Calendar.MINUTE);
                                Date mDateStart = new Date(datePicker.getYear() - 1900, mMois, mJour);
                                if (TextUtils.isEmpty(edtStartTime.getText().toString())) {
                                    mDateStart.setHours(hour);
                                    mDateStart.setMinutes(minute);

                                } else {
                                    Date stMin = getDateFromStrHour(edtStartTime.getText().toString());
                                    mDateStart.setHours(stMin.getHours());
                                    mDateStart.setMinutes(stMin.getMinutes());
                                    if (mCurrentDate.compareTo(mDateStart) > 0) {
                                        mDateStart.setHours(hour);
                                        mDateStart.setMinutes(minute);
                                    } else {
                                        mDateStart.setHours(stMin.getHours());
                                        mDateStart.setMinutes(stMin.getMinutes());
                                    }

                                }
                                Date stSelTime = CommonUtils.calendar().getTime();
                                stSelTime.setHours(mDateStart.getHours());
                                stSelTime.setMinutes(mDateStart.getMinutes());
                                mFinalTimeEndDate = new Date(mAnnee, mMois, mJour, mDateStart.getHours()
                                        + duree.getHours(), mDateStart.getMinutes()
                                        + duree.getMinutes());

                                mFinalStartTimeDate = getDateFromStrDate(edtStartDate.getText().toString());
                                mFinalStartTimeDate.setHours(mDateStart.getHours());
                                mFinalStartTimeDate.setMinutes(mDateStart.getMinutes());
                                mEndJour = mFinalTimeEndDate.getDate();
                                mEndAnnee = mFinalTimeEndDate.getYear();
                                mEndMois = mFinalTimeEndDate.getMonth();
                                mH2 = mFinalTimeEndDate.getHours();
                                mMin2 = mFinalTimeEndDate.getMinutes();
                                mH1 = mFinalStartTimeDate.getHours();
                                mMin1 = mFinalStartTimeDate.getMinutes();
                                edtEndTime.setText(updateTo24HresFormat(mFinalTimeEndDate.getHours(), mFinalTimeEndDate.getMinutes()));
                                edtEndDate.setText(df.format(mFinalTimeEndDate));
                                edtStartTime.setText(updateTo24HresFormat(mFinalStartTimeDate.getHours(), mFinalStartTimeDate.getMinutes()));
                            }

                        }
                    });

            adb.setNegativeButton(R.string.textCancelLable,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            adb.show();
        }
    }

    @SuppressWarnings("deprecation")
    public void showTime1(View v) {

        if (!edtStartDate.getText().toString().equals("")) {

            if (!edtMeetingtime.getText().toString().equals("")) {
                edtStartDate.setText(CommonUtils.convertEditTextToString(edtMeetingDate));
                edtStartTime.setText(updateTo24HresFormat(mFinalMettingStartDate.getHours(), mFinalMettingStartDate.getMinutes()));
                edtEndTime.setText(updateTo24HresFormat(mFinalmettingEndDate.getHours(), mFinalmettingEndDate.getMinutes()));
                edtEndDate.setText(df.format(mFinalmettingEndDate));
                clearScheduleDataIv.setVisibility(View.VISIBLE);

                //V52 new changes
                mFinalTimeEndDate = mFinalmettingEndDate;
                mFinalStartTimeDate = getDateFromStrDate(CommonUtils.convertEditTextToString(edtStartDate));
                mEndJour = mFinalTimeEndDate.getDate();
                mEndMois = mFinalTimeEndDate.getMonth();
                mEndAnnee = mFinalTimeEndDate.getYear();
                mH2 = mFinalTimeEndDate.getHours();
                mMin2 = mFinalTimeEndDate.getMinutes();

            } else {
                LayoutInflater factory = LayoutInflater.from(this);
                alertDialogView = factory.inflate(R.layout.show_time_picker,
                        null);
                TimePicker timePicker = (TimePicker) alertDialogView
                        .findViewById(R.id.StartTime);
                timePicker.setIs24HourView(isFormat24);
                if (TextUtils.isEmpty(edtStartTime.getText().toString())) {
                    mH1 = timePicker.getCurrentHour();
                    mMin1 = timePicker.getCurrentMinute();
                } else {
                    Date datez = getDateFromStrHour(edtStartTime.getText().toString());
                    mH1 = datez.getHours();
                    mMin1 = datez.getMinutes();
                }
                timePicker.setCurrentHour(mH1);
                timePicker.setCurrentMinute(mMin1);
                AlertDialog.Builder adb = new AlertDialog.Builder(this);
                adb.setView(alertDialogView);
                adb.setTitle(R.string.textStartTimeLable);
                adb.setIcon(R.drawable.time_icon);
                adb.setPositiveButton(getString(R.string.ok).toUpperCase(),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                TimePicker timePicker = (TimePicker) alertDialogView
                                        .findViewById(R.id.StartTime);
                                timePicker.setIs24HourView(isFormat24);

                                mH1 = timePicker.getCurrentHour();
                                mMin1 = timePicker.getCurrentMinute();
                                Date currentTime = Calendar.getInstance(Locale.getDefault()).getTime();
                                Date stSelTime = Calendar.getInstance(Locale.getDefault()).getTime();
                                stSelTime.setHours(mH1);
                                stSelTime.setMinutes(mMin1);
                                Date SelectDte = getDateFromStrDate(edtStartDate.getText().toString());
                                if (currentTime.compareTo(SelectDte) > 0) {
                                    if (currentTime.compareTo(stSelTime) > 0) {
                                        mH1 = currentTime.getHours();
                                        mMin1 = currentTime.getMinutes();
                                    } else {
                                        mH1 = timePicker.getCurrentHour();
                                        mMin1 = timePicker.getCurrentMinute();
                                    }
                                } else {
                                    mH1 = timePicker.getCurrentHour();
                                    mMin1 = timePicker.getCurrentMinute();
                                }
                                mFinalTimeEndDate = new Date(mAnnee, mMois, mJour, mH1
                                        + duree.getHours(), mMin1
                                        + duree.getMinutes());
                                mFinalStartTimeDate = getDateFromStrDate(CommonUtils.convertEditTextToString(edtStartDate));
                                mEndJour = mFinalTimeEndDate.getDate();
                                mEndMois = mFinalTimeEndDate.getMonth();
                                mEndAnnee = mFinalTimeEndDate.getYear();
                                mH2 = mFinalTimeEndDate.getHours();
                                mMin2 = mFinalTimeEndDate.getMinutes();
                                mFinalStartTimeDate.setHours(mH1);
                                mFinalStartTimeDate.setMinutes(mMin1);
                                if (!TextUtils.isEmpty(edtMeetingDate.getText())) {
                                    clearScheduleDataIv.setVisibility(View.VISIBLE);
                                    clearMettingDataIv.setVisibility(View.VISIBLE);
                                }
                                edtEndTime.setText(updateTo24HresFormat(mFinalTimeEndDate.getHours(), mFinalTimeEndDate.getMinutes()));
                                edtEndDate.setText(df.format(mFinalTimeEndDate));
                                edtStartTime.setText(updateTo24HresFormat(mFinalStartTimeDate.getHours(), mFinalStartTimeDate.getMinutes()));
                                if (!edtStartTime.getText().toString().equals(""))
                                    clearScheduleDataIv
                                            .setVisibility(View.VISIBLE);
                            }
                        });

                adb.setNegativeButton(R.string.textCancelLable,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                            }
                        });

                adb.show();
            }

        } else {
            Toast.makeText(this, R.string.msg_date, Toast.LENGTH_SHORT).show();
        }
    }

    public void showDateEnd(View v) {
        if (!TextUtils.isEmpty(edtStartTime.getText().toString())) {
            //v52 changes
//            if (!TextUtils.isEmpty(CommonUtils.convertEditTextToString(edtMeetingDate))) {
//                edtStartDate.setText(CommonUtils.convertEditTextToString(edtMeetingDate));
//                edtStartTime.setText(updateTo24HresFormat(mFinalMettingStartDate.getHours(), mFinalMettingStartDate.getMinutes()));
//                edtEndTime.setText(updateTo24HresFormat(mFinalmettingEndDate.getHours(), mFinalmettingEndDate.getMinutes()));
//                edtEndDate.setText(df.format(mFinalmettingEndDate));
//                if (!TextUtils.isEmpty(edtMeetingDate.getText())) {
//                    clearScheduleDataIv.setVisibility(View.VISIBLE);
//                    clearMettingDataIv.setVisibility(View.VISIBLE);
//                }
//            } else {

            myStartDate = getDateFromStrDate(CommonUtils.convertEditTextToString(edtStartDate));
            Date select = getDateFromStrHour(CommonUtils.convertEditTextToString(edtStartTime));
            myStartDate.setHours(select.getHours());
            myStartDate.setMinutes(select.getMinutes());


            final EditText edtStartDate = findViewById(R.id.heurDebut);
            if (!TextUtils.isEmpty(edtStartDate.getText().toString())) {
                if (!TextUtils.isEmpty(edtStartTime.getText().toString())) {


                    LayoutInflater factory = LayoutInflater.from(this);
                    alertDialogView2 = factory.inflate(R.layout.show_date_picker, null);
                    final DatePicker datePicker = (DatePicker) alertDialogView2
                            .findViewById(R.id.start_date);
                    android.widget.TextView txtFullDate = (android.widget.TextView) alertDialogView2
                            .findViewById(R.id.txt_full_date);
                    Calendar cal1 = Calendar.getInstance();
                    cal1.add(Calendar.DAY_OF_MONTH, 1);
                    Date date = getDateFromStrDate(edtEndDate.getText().toString());
                    datePicker.setMinDate(java.lang.System.currentTimeMillis() - 1000);
                    if (!edtEndDate.getText().toString().equals("")) {
                        datePicker.updateDate(date.getYear() + 1900,
                                date.getMonth(),
                                date.getDate());
                    }
                    AlertDialog.Builder adb = new AlertDialog.Builder(this);
                    adb.setView(alertDialogView2);
                    adb.setTitle(R.string.textDateSmallLable);
                    adb.setIcon(R.drawable.cal_icon);
                    adb.setPositiveButton(getString(R.string.ok).toUpperCase(),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    mEndJour = datePicker.getDayOfMonth();
                                    mEndMois = datePicker.getMonth();
                                    mEndAnnee = datePicker.getYear() - 1900;
                                    Date date = new Date(mEndAnnee, mEndMois, mEndJour);
                                    edtEndDate.setText(df.format(date));

                                    if (!TextUtils.isEmpty(edtEndDate.getText().toString())) {
                                        Date endDate = getDateFromStrDate(edtEndDate.getText().toString());
                                        datePicker.updateDate(endDate.getYear() + 1900, endDate.getMonth(),
                                                endDate.getDate());

                                        if (endDate.compareTo(mFinalTimeEndDate) > 0) {
                                            edtEndDate.setText(df.format(endDate));
                                            if (!TextUtils.isEmpty(CommonUtils.convertEditTextToString(edtEndTime))) {
                                                Date endHrsSEc = getDateFromStrHour(CommonUtils.convertEditTextToString(edtEndTime));
                                                mH2 = endHrsSEc.getHours();
                                                mMin2 = endHrsSEc.getMinutes();
                                            } else {
                                                mH2 = mFinalTimeEndDate.getHours();
                                                mMin2 = mFinalTimeEndDate.getMinutes();
                                            }
                                        } else {
                                            mFinalTimeEndDate = new Date(mAnnee, mMois, mJour, mH1
                                                    + mDefultMinOneMin.getHours(), mMin1
                                                    + mDefultMinOneMin.getMinutes());
                                            edtEndDate.setText(df.format(mFinalTimeEndDate));
                                            edtEndTime.setText("");
//                                                edtEndTime.setText(updateTo24HresFormat(mFinalTimeEndDate.getHours(),
//                                                        mFinalTimeEndDate.getMinutes()));
                                            mEndJour = mFinalTimeEndDate.getDate();
                                            mEndMois = mFinalTimeEndDate.getMonth();
                                            mEndAnnee = mFinalTimeEndDate.getYear();
                                            mH2 = mFinalTimeEndDate.getHours();
                                            mMin2 = mFinalTimeEndDate.getMinutes();
                                        }
                                    }

                                }
                            });
                    adb.setNegativeButton(R.string.textCancelLable,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                    adb.show();

                } else {

                    Toast.makeText(this, R.string.msg_endDate, Toast.LENGTH_SHORT)
                            .show();

                }
            } else {
                Toast.makeText(this, R.string.msg_date, Toast.LENGTH_SHORT).show();
            }
//            }
        } else {
            Toast.makeText(this, R.string.msg_date, Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("deprecation")
    public void showTime2(View v) {
        if (!edtStartDate.getText().toString().equals("")) {
            EditText heurDebutEdit = (EditText) findViewById(R.id.heurDebut);

            if (!heurDebutEdit.getText().toString().equals("")) {
                if (!TextUtils.isEmpty(edtEndDate.getText().toString())) {
                    // new func
                    EditText etM = (EditText) findViewById(R.id.heurMeeting);
                    //v52 changes
//                    if (!etM.getText().toString().equals("")) {
//                        if (!TextUtils.isEmpty(etM.getText())) {
//                            clearScheduleDataIv.setVisibility(View.VISIBLE);
//                            clearMettingDataIv.setVisibility(View.VISIBLE);
//                        }
//
//                        clearScheduleDataIv.setVisibility(View.VISIBLE);
//
//
//                    } else {

                    LayoutInflater factory = LayoutInflater.from(this);
                    alertDialogView = factory.inflate(R.layout.show_time_picker,
                            null);

                    TimePicker timePicker = (TimePicker) alertDialogView
                            .findViewById(R.id.StartTime);
                    timePicker.setIs24HourView(isFormat24);
                    Date datez = getDateFromStrHour(heurDebutEdit.getText()
                            .toString());
                    mH1 = datez.getHours();
                    mMin1 = datez.getMinutes();

                    if (TextUtils.isEmpty(CommonUtils.convertEditTextToString(edtEndTime))) {
                        Date getedtEndtime = new Date(mAnnee, mMois, mJour, mH1
                                + duree.getHours(), mMin1
                                + duree.getMinutes());
                        timePicker.setCurrentMinute(getedtEndtime.getMinutes());
                        timePicker.setCurrentHour(getedtEndtime.getHours());
                    } else {
                        Date getEndTime = getDateFromStrHour(CommonUtils.convertEditTextToString(edtEndTime));
                        timePicker.setCurrentMinute(getEndTime.getMinutes());
                        timePicker.setCurrentHour(getEndTime.getHours());
                    }
                    AlertDialog.Builder adb = new AlertDialog.Builder(this);
                    adb.setView(alertDialogView);
                    adb.setTitle(R.string.textEndTimeLable);
                    adb.setIcon(R.drawable.time_icon);
                    adb.setPositiveButton(getString(R.string.ok).toUpperCase(),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    TimePicker timePicker = (TimePicker) alertDialogView
                                            .findViewById(R.id.StartTime);
                                    timePicker.setIs24HourView(isFormat24);

                                    mH2 = timePicker.getCurrentHour();
                                    mMin2 = timePicker.getCurrentMinute();
                                    Date myEndDate = new Date();
                                    Date mDateEnd = new Date();
                                    myEndDate = getDateFromStrDate(CommonUtils.convertEditTextToString(edtEndDate));
                                    myEndDate.setMinutes(mMin2);
                                    myEndDate.setHours(mH2);
                                    Date currentint = getDateFromStrHour(CommonUtils.convertEditTextToString(edtStartTime));
                                    myEndDate = getDateFromStrDate(CommonUtils.convertEditTextToString(edtEndDate));
                                    myEndDate.setMinutes(mMin2);
                                    myEndDate.setHours(mH2);
                                    mDateEnd.setTime(mFinalStartTimeDate.getTime());
                                    mDateEnd.setHours(currentint.getHours() + mDefultMinOneMin.getHours());
                                    mDateEnd.setMinutes(currentint.getMinutes() + mDefultMinOneMin.getMinutes());
                                    int totalhour = mDateEnd.getHours();
                                    int totalhmin = mDateEnd.getMinutes();
                                    if (mDateEnd.compareTo(myEndDate) > 0) {
                                        mH2 = totalhour;
                                        mMin2 = totalhmin;
                                        edtEndTime.setText(updateTo24HresFormat(totalhour, totalhmin));
                                    } else {
                                        mH2 = timePicker.getCurrentHour();
                                        mMin2 = timePicker.getCurrentMinute();
                                        edtEndTime.setText(updateTo24HresFormat(mH2, mMin2));
                                    }


                                }
                            });

                    adb.setNegativeButton(R.string.textCancelLable,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {

                                }
                            });

                    adb.show();
//                    }

                } else {
                    Toast.makeText(this, R.string.msg_endDate, Toast.LENGTH_SHORT)
                            .show();
                }
            } else {

                Toast.makeText(this, R.string.msg_endtime, Toast.LENGTH_SHORT)
                        .show();
            }

        } else {

            Toast.makeText(this, R.string.msg_date, Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Performs neccesary initialization on new job screen.
     */
    public void initAll() {
        edtEndDate = findViewById(R.id.dateDebutfin);
        edtStartDate = findViewById(R.id.dateDebut);
        edtStartTime = findViewById(R.id.heurDebut);
        edtEndTime = findViewById(R.id.heurFin);
        edtMeetingDate = findViewById(R.id.dateMeeting);
        edtMeetingtime = findViewById(R.id.heurMeeting);
        clientNameEt = (TextView) findViewById(R.id.targetCorporationEt);
        clientNameEt.setEnabled(false);
        clientNameEt.setText(clientName);

        siteEt = (TextView) findViewById(R.id.siteEt);

        if (idSite == -1) {
            siteEt.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(AddDuplicateNewJob.this,
                            SiteList.class);
                    intent.putExtra(KEYS.NewJob.CLIENT_ID, idClient);
                    startActivityForResult(intent,
                            RequestCode.REQUEST_CODE_SITELISTFULL);

                }
            });

            siteEt.setEnabled(true);
        } else {

            siteEt.setText(siteName);

            siteEt.setEnabled(false);

        }

        if (gt != null && (gt.getFlPageSites() == 0)) {
            siteEt.setVisibility(TextView.GONE);

        }

        equipmentEt = (TextView) findViewById(R.id.equipmentsEt);
        if (idEquipement == -1) {

            equipmentEt.setEnabled(true);
            // textView.setText("");
            equipmentEt.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Intent intent = new Intent(AddDuplicateNewJob.this,
                            EquipmentList.class);
                    intent.putExtra(KEYS.NewJob.CLIENT_ID, idClient);
                    intent.putExtra(KEYS.NewJob.SITE_ID, idSite);
                    startActivityForResult(intent,
                            RequestCode.REQUEST_CODE_EQUPMENTLISTFULL);
                }
            });
        } else {
            equipmentEt.setText(equipmentName);

            equipmentEt.setEnabled(false);

        }

        if (gt != null && gt.getFlMandatoryDescription() == 0) {
            ((EditText) findViewById(R.id.Ajoutdescription))
                    .setHintTextColor(this.getResources().getColor(
                            R.color.textColorLableNonManadtoryMandatoryFiled));
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO Auto-generated method stub
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_job_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.action_refresh:

                synch();

                break;

            case android.R.id.home:
                DialogUtils.showConfirmationDialog(AddDuplicateNewJob.this,
                        this.getString(R.string.textAlertLable) + "!",
                        this.getString(R.string.textNewJobEndConfirmation), false,
                        0, this.getString(R.string.textYesLable),
                        this.getString(R.string.textNoLable),
                        new DialogResponseInterface() {

                            @Override
                            public void doOnPositiveBtnClick(Activity arg0) {
                                // TODO Auto-generated method stub

                                finish();

                            }

                            @Override
                            public void doOnNegativeBtnClick(Activity arg0) {
                                // TODO Auto-generated method stub

                            }
                        });

                return true;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.synchroteam.synchroteam.CommonInterface#getActivityInstance()
     */
    @Override
    public Activity getActivityInstance() {
        // TODO Auto-generated method stub
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onResume()
     */
    @SuppressWarnings("deprecation")
    protected void onResume() {
        Logger.log("onResume", "New Job");
        super.onResume();

        //New changes
        DateChecker.checkDateAndNavigate(this, dao);

        ((SyncroTeamApplication) getApplicationContext())
                .setSyncroteamAppLive(true);
        ((SyncroTeamApplication) getApplicationContext())
                .setSyncroteamActivityInstance(this);
        //
        // String deviceDateFormat=getDateFormat(this);
        // String deviceTimeFormat=getTimeFormat(this);
        //
        // if (!deviceDateFormat.equals(
        // SharedPref.getDateFormat(this))) {

        String dateFormatString = System.getString(this.getContentResolver(),
                System.DATE_FORMAT);
        String timeFormatString = System.getString(this.getContentResolver(),
                System.TIME_12_24);

        if ((!TextUtils.isEmpty(dateFormatString))
                && (!TextUtils.isEmpty(timeFormatString))) {
            if (!dateFormatString.equals(SharedPref.getDateFormat(this))) {
                SharedPref.setDateFormat(this);
                SharedPref.setTimeFormat(this);
                isFormat24 = android.text.format.DateFormat
                        .is24HourFormat(this);
                EditText startDate = (EditText) findViewById(R.id.dateDebut);
                EditText startTime = (EditText) findViewById(R.id.heurDebut);
                EditText endTime = (EditText) findViewById(R.id.heurFin);
                EditText mettingDate = (EditText) findViewById(R.id.dateMeeting);
                EditText meetingHour = (EditText) findViewById(R.id.heurMeeting);
                if (!TextUtils.isEmpty(startDate.getText())) {

                    Date date = new Date(mAnnee, mMois, mJour);

                    DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);
                    startDate.setText(df.format(date));

                }

                if (!TextUtils.isEmpty(startTime.getText())) {


                    startTime.setText(updateTo24HresFormat(mH1, mMin1));
                }

                if (!TextUtils.isEmpty(endTime.getText())) {

                    endTime.setText(updateTo24HresFormat(mH2, mMin2));
                }

                if (!TextUtils.isEmpty(mettingDate.getText())) {

                    Date date = new Date(mAnneeM, mMoisM, mJourM);

                    DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);
                    mettingDate.setText(df.format(date));

                }
                if (!TextUtils.isEmpty(meetingHour.getText())) {
                    Format format = android.text.format.DateFormat
                            .getTimeFormat(AddDuplicateNewJob.this);

                    Date date = new Date(mAnneeM, mMoisM, mJourM, mHM, mMinM);

//                    meetingHour.setText(format.format(date));
                    meetingHour.setText(updateTo24HresFormat(mHM, mHM));

                }

            } else if (!timeFormatString.equals(SharedPref.getTimeFormat(this))) {

                SharedPref.setDateFormat(this);
                SharedPref.setTimeFormat(this);
                isFormat24 = android.text.format.DateFormat
                        .is24HourFormat(this);

                EditText startDate = (EditText) findViewById(R.id.dateDebut);
                EditText startTime = (EditText) findViewById(R.id.heurDebut);
                EditText endTime = (EditText) findViewById(R.id.heurFin);
                EditText mettingDate = (EditText) findViewById(R.id.dateMeeting);
                EditText meetingHour = (EditText) findViewById(R.id.heurMeeting);
                if (!TextUtils.isEmpty(startDate.getText())) {

                    Date date = new Date(mAnnee, mMois, mJour);

                    DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);
                    startDate.setText(df.format(date));

                }

                if (!TextUtils.isEmpty(startTime.getText())) {

                    Format format = android.text.format.DateFormat
                            .getTimeFormat(AddDuplicateNewJob.this);
                    Date date = new Date(mAnnee, mMois, mJour, mH1, mMin1);

//                    startTime.setText(format.format(date));
                    startTime.setText(updateTo24HresFormat(mH1, mMin1));
                }

                if (!TextUtils.isEmpty(endTime.getText())) {

                    Format format = android.text.format.DateFormat
                            .getTimeFormat(AddDuplicateNewJob.this);

                    Date date = new Date(mAnnee, mMois, mJour, mH2, mMin2);

//                    endTime.setText(format.format(date));
                    endTime.setText(updateTo24HresFormat(mH2, mMin2));
                }

                if (!TextUtils.isEmpty(mettingDate.getText())) {

                    Date date = new Date(mAnneeM, mMoisM, mJourM);

                    DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);
                    mettingDate.setText(df.format(date));

                }
                if (!TextUtils.isEmpty(meetingHour.getText())) {
                    Format format = android.text.format.DateFormat
                            .getTimeFormat(AddDuplicateNewJob.this);

                    Date date = new Date(mAnneeM, mMoisM, mJourM, mHM, mMinM);

//                    meetingHour.setText(format.format(date));
                    meetingHour.setText(updateTo24HresFormat(mHM, mHM));
                }

            }
        }

        if (syncroTeamApplication.getIsLocaleChenged()) {

            SharedPref.setDateFormat(this);
            SharedPref.setTimeFormat(this);

            EditText startDate = (EditText) findViewById(R.id.dateDebut);
            EditText startTime = (EditText) findViewById(R.id.heurDebut);
            EditText endTime = (EditText) findViewById(R.id.heurFin);
            EditText mettingDate = (EditText) findViewById(R.id.dateMeeting);
            EditText meetingHour = (EditText) findViewById(R.id.heurMeeting);
            if (!TextUtils.isEmpty(startDate.getText())) {

                Date date = new Date(mAnnee, mMois, mJour);

                DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);
                startDate.setText(df.format(date));

            }

            if (!TextUtils.isEmpty(startTime.getText())) {

                Format format = android.text.format.DateFormat
                        .getTimeFormat(AddDuplicateNewJob.this);
                Date date = new Date(mAnnee, mMois, mJour, mH1, mMin1);

//                startTime.setText(format.format(date));
                startTime.setText(updateTo24HresFormat(mH1, mMin1));
            }

            if (!TextUtils.isEmpty(endTime.getText())) {

                Format format = android.text.format.DateFormat
                        .getTimeFormat(AddDuplicateNewJob.this);

                Date date = new Date(mAnnee, mMois, mJour, mH2, mMin2);

//                endTime.setText(format.format(date));
                endTime.setText(updateTo24HresFormat(mH2, mMin2));
            }

            if (!TextUtils.isEmpty(mettingDate.getText())) {

                Date date = new Date(mAnneeM, mMoisM, mJourM);

                DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);
                mettingDate.setText(df.format(date));

            }
            if (!TextUtils.isEmpty(meetingHour.getText())) {
                Format format = android.text.format.DateFormat
                        .getTimeFormat(AddDuplicateNewJob.this);

                Date date = new Date(mAnneeM, mMoisM, mJourM, mHM, mHM);

//                meetingHour.setText(format.format(date));
                meetingHour.setText(updateTo24HresFormat(mHM, mHM));
            }

            syncroTeamApplication.setIsLocaleChanged(false);

        }

    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        ((SyncroTeamApplication) getApplicationContext())
                .setSyncroteamAppLive(false);
        ((SyncroTeamApplication) getApplicationContext())
                .setSyncroteamActivityInstance(null);

    }

    /*
     * (non-Javadoc)
     *
     * @see com.synchroteam.synchroteam.CommonInterface#updateUi()
     */
    @Override
    public void updateUi() {
        // TODO Auto-generated method stub
        EventBus.getDefault().post(new UpdateUiOnSync());
    }

    @Override
    public void updateUiOnTrakingStatusChange(boolean isRunning) {

    }
    /**
     * Init list of sites.
     */
    public void initAutoCompleteSites() {

        // textView.setText("");
        siteEt.setEnabled(true);
        siteEt.setVisibility(View.VISIBLE);
        siteEt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(AddDuplicateNewJob.this,
                        SiteList.class);
                intent.putExtra(KEYS.NewJob.CLIENT_ID, idClient);
                startActivityForResult(intent,
                        RequestCode.REQUEST_CODE_SITELISTFULL);

            }
        });

    }

    /**
     * Inits the List of Equipments.
     */
    public void initAutoCompleteEquipements() {
        // ArrayList<Equipement> listEquipement = dao.getEquipementsForSite(
        // idClient, idSite);

        equipmentEt.setEnabled(true);
        // textView.setText("");
        equipmentEt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(AddDuplicateNewJob.this,
                        EquipmentList.class);
                intent.putExtra(KEYS.NewJob.CLIENT_ID, idClient);
                intent.putExtra(KEYS.NewJob.SITE_ID, idSite);
                startActivityForResult(intent,
                        RequestCode.REQUEST_CODE_EQUPMENTLISTFULL);
            }
        });

    }

    /**
     * Inits the spinner containing reports data.
     */
    public void initSpinnerOne() {
        ArrayList<ModeleRapport> al = dao.getModeleRapport();
        Spinner s = (Spinner) findViewById(R.id.reportsSpinner);
        ArrayAdapter<ModeleRapport> adapter = new ArrayAdapter<ModeleRapport>(
                this, android.R.layout.simple_spinner_item, al);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);

        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapter, View view,
                                       int pos, long id) {
                ModeleRapport mr = (ModeleRapport) adapter
                        .getItemAtPosition(pos);
                idModele = mr.getId();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Inits the spinner contaning Job Type Data.
     */
    public void initSpinnerTwo() {
        ArrayList<TypeIntervention> al = dao.getTypeIntervention();

        if (al.size() != 0) {

            TypeIntervention tr = al.get(0);
            try {
                duree = simpleDateFormat.parse(tr.getHrDureeIntervention());
                if (duree.getHours() == 0 && duree.getMinutes() == 0) {
                    duree = simpleDateFormat.parse("02:00");

                }
            } catch (ParseException e) {
                Logger.printException(e);

            }
        }

        Spinner s = (Spinner) findViewById(R.id.jobTypeSpinner);
        ArrayAdapter<TypeIntervention> adapter = new ArrayAdapter<TypeIntervention>(
                this, android.R.layout.simple_spinner_item, al);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);

        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapter, View view,
                                       int pos, long id) {
                TypeIntervention ti = (TypeIntervention) adapter
                        .getItemAtPosition(pos);
                idType = ti.getId();
                initDefaultModel(idType);

                try {
                    duree = simpleDateFormat.parse(ti.getHrDureeIntervention());
                    if (duree.getHours() == 0 && duree.getMinutes() == 0) {
                        duree = simpleDateFormat.parse("02:00");

                    }
                } catch (ParseException e) {
                    Logger.printException(e);

                }

            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Code of previous developer.
     *
     * @param idType the id type
     */
    public void initDefaultModel(int idType) {
        ModeleRapport mr = dao.getDefaultModeleRapport(idType);
        Spinner s = (Spinner) findViewById(R.id.reportsSpinner);
        int i = 0;

        ArrayList<ModeleRapport> al = dao.getModeleRapport();
        Iterator<ModeleRapport> iter = al.iterator();
        while (iter.hasNext()) {

            ModeleRapport aux = iter.next();
            if (mr != null) {
                idModele = mr.getId();

                if (aux.getId() == mr.getId()) {

                    s.setSelection(i);
                    if (gt != null && gt.getFlForceReportTemplate() == 1)
                        s.setEnabled(false);
                    break;
                }
            } else {
                if (gt != null && gt.getFlForceReportTemplate() == 1)
                    s.setEnabled(false);
                else
                    s.setEnabled(true);

                if (aux.getFlDefault() == 1) {
                    s.setSelection(i);
                    if (mr == null)
                        break;
                }
            }
            i++;
        }
    }


    private void showStartTimeLogicNew() {
        EditText scheduledDate = (EditText) findViewById(R.id.dateDebut);
        EditText edtMeetingTime = (EditText) findViewById(R.id.heurMeeting);
        EditText edtStartTime = (EditText) findViewById(R.id.heurDebut);
        EditText edtEndTime = (EditText) findViewById(R.id.heurFin);

        if (!scheduledDate.getText().toString().equals("")) {

            if (!edtMeetingTime.getText().toString().equals("")) {

                edtStartTime.setText(edtMeetingTime.getText().toString());

                Date dateM = getDateFromStrHour(edtMeetingTime.getText().toString());
                Calendar calendarM = Calendar.getInstance();
                calendarM.setTime(dateM);
            }
        } else {
            Toast.makeText(this, R.string.msg_date, Toast.LENGTH_SHORT).show();
        }
    }


    private String updateTo24HresFormat(int heur, int min) {
        String updatedTime = "";
        String dateFiMin = "";
        String dateFiHeur = "";
        if (heur < 10) {
            dateFiHeur = "0" + heur;
        } else {
            dateFiHeur = "" + heur;
        }
        if (min < 10) {
            dateFiMin = "0" + min;
        } else {
            dateFiMin = "" + min;
        }

        updatedTime = dateFiHeur + ":" + dateFiMin;
        return updatedTime;
    }

    /**
     * Validate start time and end time
     *
     * @param n
     * @return
     */
    public int validateDbDate(int n) {
        int i = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        String dbDate;

        Date dateStart = new Date(mAnnee, mMois, mJour, mH1, mMin1);
        dbDate = sdf.format(dateStart);

        Date dateEnd = new Date(mAnnee, mMois, mJour, mH2, mMin2);
        dbDate = sdf.format(dateEnd);

        return i;
    }


    public Date getDateFromStrHour(String mDate) {
        // DateFormat df=DateFormat.getTimeInstance(DateFormat.SHORT);
        Date date;
        try {
            date = simpleDateFormat.parse(mDate);
            return date;
        } catch (ParseException e) {
            Logger.printException(e);
            return new Date();
        }
    }

    /**
     * Gets the date from String literal.
     *
     * @param mDate the m date
     * @return the date from str date
     */
    public Date getDateFromStrDate(String mDate) {
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);
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
     * Perform Validation And addd the intervention.
     *
     * @param v the v
     */
    public void addIntervention(final View v) {

        EditText et3, et4, et5, et6, et7, et9;
        TextView et1, et2, et8;
        et1 = (TextView) findViewById(R.id.targetCorporationEt);

        et3 = (EditText) findViewById(R.id.addressEt);
        et4 = (EditText) findViewById(R.id.additionalAddressEt);
        et5 = (EditText) findViewById(R.id.heurDebut);
        et6 = (EditText) findViewById(R.id.heurFin);
        et7 = (EditText) findViewById(R.id.dateDebut);
        et9 = (EditText) findViewById(R.id.Ajoutdescription);

        final String nom_client = et1.getText().toString();
        final String nom_site = siteEt.getText().toString();
        final String adrGlobal = et3.getText().toString();
        final String adrCompl = et4.getText().toString();
        final String nom_equipement = equipmentEt.getText().toString();
        final String description = et9.getText().toString();
        String mdate = et7.getText().toString();
        String mheur1 = et5.getText().toString();
        String mEndDate = CommonUtils.convertEditTextToString(edtEndDate);
        String mheur2 = et6.getText().toString();
        Boolean drapo = true;

        if (description.equals("") && (gt != null)
                && gt.getFlMandatoryDescription() == 1)
            drapo = false;
        else
            drapo = true;
        //
        // if ((!et10.getText().toString().equals("") &&
        // et11.getText().toString()
        // .equals(""))
        // || (et10.getText().toString().equals("") && !et11.getText()
        // .toString().equals("")))
        // dp = false;
        // else
        // dp = true;


        // && dp
        if (!nom_client.equals("") && !adrGlobal.equals("") && drapo) {

            // Date dateDb=new Date(mAnnee,mMois,mJour,mH1,mMin1);
            // Date dateFn=new Date(mAnnee,mMois,mJour,mH2,mMin2);

            if (mdate.equals("") && mheur1.equals("") && mheur2.equals("") && mEndDate.equals("")) {
                builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setTitle(R.string.txt_confirm);
                builder.setMessage(R.string.msg_confirm_plannif);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.textYesLable,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int idi) {
                                String x = null;
                                String y = null;

                                /*
                                 * if(location != null){ y =
                                 * String.valueOf(location.getLatitude()); x =
                                 * String.valueOf(location.getLongitude()); }
                                 * else
                                 */
                                x = GpsX;
                                y = GpsY;

                                // drp = dao.ajoutIntervention(null, null,
                                // idModele, idType, nom_client, nom_site,
                                // adrGlobal, adrCompl.toString(), 0,
                                // idClient, idSite, idEquipement,
                                // nom_equipement, y, x, description, rue,
                                // ville, getDateM());

                                new SaveNewJobDataAsyncTask(false).execute();

                            }
                        });
                builder.setNegativeButton(R.string.textNoLable,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                v.setEnabled(true);

                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else if (mdate.equals("") || mheur1.equals("")
                    || mheur2.equals("")) {

                Toast.makeText(this, R.string.msg_date_manquante,
                        Toast.LENGTH_SHORT).show();
                v.setEnabled(true);
            }
            /*
             * else if( !dateDb.before(dateFn))
             * Toast.makeText(this,R.string.MsgCorrectHour
             * ,Toast.LENGTH_LONG).show();
             */
            else {
                String x = null;
                String y = null;

                /*
                 * if(location != null){ y =
                 * String.valueOf(location.getLatitude()); x =
                 * String.valueOf(location.getLongitude()); } else
                 */
                x = GpsX;
                y = GpsY;

                // drp = dao.ajoutIntervention(null, null,
                // idModele, idType, nom_client, nom_site,
                // adrGlobal, adrCompl.toString(), 0,
                // idClient, idSite, idEquipement,
                // nom_equipement, y, x, description, rue,
                // ville, getDateM());

                /*
                 * new SaveNewJobDataAsyncTask().execute(getDbDate(1),
                 * getDbDate(2), idModele + "", idType + "", nom_client,
                 * nom_site,adrGlobal, adrCompl.toString(), 1 + "", idClient +
                 * "", idSite + "", idEquipement +
                 * "",nom_equipement,y,x,description,rue,ville,getDateM());
                 */

                new SaveNewJobDataAsyncTask(true).execute();

                // if (!JobDetails.this.isFinishing())
                // EventBus.getDefault().post(new UpdateDataBaseEvent());

            }
        } else {
            Toast.makeText(this, R.string.msg_saisie_oblig, Toast.LENGTH_SHORT)
                    .show();
            v.setEnabled(true);
        }

    }

    /**
     * Gets the Start date.
     *
     * @param n the n
     * @return the db date
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("SimpleDateFormat")
    public String getDbDate(int n) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        String dbDate;
        if (n == 1) {
            Date date = new Date(mAnnee, mMois, mJour, mH1, mMin1);
            dbDate = sdf.format(date);
        } else {
            Date date = new Date(mEndAnnee, mEndMois, mEndJour, mH2, mMin2);
            dbDate = sdf.format(date);
        }
        return dbDate;
    }

    /**
     * Gets the Meeting Date.
     *
     * @return the date m
     */
    @SuppressWarnings("deprecation")
    public String getDateM() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        String dbDate;
        EditText et = (EditText) findViewById(R.id.dateMeeting);
        EditText et1 = (EditText) findViewById(R.id.heurMeeting);

        if (et.getText().toString().equals("")
                || et1.getText().toString().equals(""))
            return null;
        Date date = new Date(mAnneeM, mMoisM, mJourM, mHM, mMinM);
        dbDate = sdf.format(date);
        return dbDate;
    }

    /**
     * Clear all the fields.
     *
     * @param v the v
     */
    public void clearAll(View v) {
        EditText et5, et6, et7, et9, et10, et11;
        TextView et1, et2, et8;
        clearMettingDataIv.setVisibility(View.GONE);
        clearScheduleDataIv.setVisibility(View.GONE);

        // et3 = (EditText) findViewById(R.id.addressEt);
        // et4 = (EditText) findViewById(R.id.additionalAddressEt);
        et5 = (EditText) findViewById(R.id.heurDebut);
        et6 = (EditText) findViewById(R.id.heurFin);

        et7 = (EditText) findViewById(R.id.dateDebut);
        // et9 = (EditText) findViewById(R.id.Ajoutdescription);
        et10 = (EditText) findViewById(R.id.dateMeeting);

        et11 = (EditText) findViewById(R.id.heurMeeting);

        // if (clientNameEt.isEnabled()) {
        // clientNameEt.setText("");
        // idClient = -1;
        // }
        //
        // if (siteEt.isEnabled()) {
        // siteEt.setText("");
        // idSite = -1;
        // }
        //
        // if (equipmentEt.isEnabled()) {
        // equipmentEt.setText("");
        // idEquipement = -1;
        // }

        et5.setText("");

        et6.setText("");
        et7.setText("");
        et10.setText("");
        et11.setText("");
        // rue = "";
        // ville = "";
        // GpsX = "";
        // GpsY = "";

        photo_Pdas.clear();
        attachmentLinearView.removeAllViews();
        arrorBtn.setTag(Boolean.valueOf(false));
        arrorBtn.setImageResource(R.drawable.arrow_down);

    }

    /**
     * Synchronise with Db.
     */
    private void synch() {


        if (SynchroteamUitls.isNetworkAvailable(AddDuplicateNewJob.this)) {

            new OnSyncAsynTask().execute();



//            progressDSynch = ProgressDialog.show(AddDuplicateNewJob.this,
//                    getString(R.string.textPleaseWaitLable),
//                    getString(R.string.msg_synch), true, false);
//
//            Thread syncDatabaseWithServer = new Thread((new Runnable() {
//                @Override
//                public void run() {
//
//                    Message myMessage = new Message();
//                    try {
//                        User u = dao.getUser();
//                        dao.sync(u.getLogin(), u.getPwd());
//
//                        if (gt != null && gt.getOptionTaracking() == 0) {
////                            stopService(new Intent(AddDuplicateNewJob.this,
////                                    TrackingService.class));
////                            stopService(new Intent(AddDuplicateNewJob.this,
////                                    TrackingServiceFrequency.class));
////
////                            AlarmManager am = (AlarmManager) AddDuplicateNewJob.this
////                                    .getSystemService(Context.ALARM_SERVICE);
////                            am.cancel(pi);
////                            AlarmManager am1 = (AlarmManager) AddDuplicateNewJob.this
////                                    .getSystemService(Context.ALARM_SERVICE);
////                            am1.cancel(pi1);
//                            cancelTrackingAlarm();
//                        }
//                        //Logic for hitting notification after sync
//                        if (notiList != null && notiList.size() > 0)
//                            for (int i = 0; i < notiList.size(); i++)
//                                notificationEventList(notiList.get(i).getIdIntervention(),
//                                        notiList.get(i).getStatus());
//
//                        myMessage.obj = "ok";
//                        handler.sendMessage(myMessage);
//                    } catch (Exception ex) {
//                        String exception = ex.getMessage();
//                        if (exception != null) {
//                            if (exception.indexOf("4001") != -1) {
//                                myMessage.obj = "4001";
//                            } else if (exception.indexOf("4006") != -1) {
//                                myMessage.obj = "4006";
//                            } else if (exception.indexOf("4000") != -1) {
//                                myMessage.obj = "4000";
//                            } else if (exception.indexOf("4101") != -1) {
//                                myMessage.obj = "4101";
//                            } else if (exception.indexOf("4005") != -1) {
//                                myMessage.obj = "4005";
//                            } else if (exception.indexOf("4003") != -1) {
//                                myMessage.obj = "4003";
//                            } else {
//                                myMessage.obj = "Error";
//                            }
//                        } else {
//                            myMessage.obj = "Error";
//                        }
//                        handler.sendMessage(myMessage);
//                    } finally {
//                        if (progressDSynch != null
//                                && progressDSynch.isShowing())
//                            progressDSynch.dismiss();
//                    }
//                }
//            }));
//            syncDatabaseWithServer.start();
        } else {

            //save in database
            if (notiList != null && notiList.size() > 0)
                for (int i = 0; i < notiList.size(); i++)
                    notificationEventList(notiList.get(i).getIdIntervention(),
                            notiList.get(i).getStatus());

            EventBus.getDefault().post(new UpdateDataBaseEvent());

            if (closeActivity) {
                finish();
            } else {
                SynchroteamUitls.showToastMessage(AddDuplicateNewJob.this);
            }
        }

    }

    private void synchV52() {
        AuthData getAuthandExpriryToken = dao.getUserToken();
        if (getAuthandExpriryToken != null && user.getPwd() != null && !TextUtils.isEmpty(user.getPwd())) {
            boolean isExpiry = DateFormatUtils.getFormattedDateFromAPIDateBase(getAuthandExpriryToken.getExpiry());
            if (isExpiry) {
                synch();
            } else {
                SynchroteamUitls.ISTokonExpiryGotoLogin(AddDuplicateNewJob.this);
            }
        } else {
            SynchroteamUitls.ISTokonExpiryGotoLogin(AddDuplicateNewJob.this);
        }
    }

    /**
     * The handler.
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            gt = dao.getAcces();
            String erreur = (String) msg.obj;
            if (erreur.equals("ok")) {
                Toast.makeText(AddDuplicateNewJob.this,
                        getString(R.string.msg_synch_ok), Toast.LENGTH_SHORT)
                        .show();



                if (gt != null && gt.getFlMandatoryDescription() == 0) {
                    ((EditText) findViewById(R.id.Ajoutdescription))
                            .setHintTextColor(AddDuplicateNewJob.this
                                    .getResources()
                                    .getColor(
                                            R.color.textColorLableNonManadtoryMandatoryFiled));
                } else {
                    ((EditText) findViewById(R.id.Ajoutdescription))
                            .setHintTextColor(AddDuplicateNewJob.this
                                    .getResources()
                                    .getColor(
                                            R.color.textColorLableMandatoryFiled));
                }

                if (gt != null && gt.getFlForceReportTemplate() == 1) {

                    // initDefaultModel(idType);
                } else {
                    // findViewById(R.id.reportsSpinner).setEnabled(true);
                }

                Log.e("AddDuplicateNewJob","UpdateDataBaseEvent is called>>>>>>>>>>2");
                EventBus.getDefault().post(new UpdateDataBaseEvent());

                if (closeActivity) {
                    setResult(Activity.RESULT_OK);
                    finish();
                }

            } else if (erreur.equals("4001") || erreur.equals("4000")) {
                showAuthErrDialog();
            } else if (erreur.equals("4006")) {
                Toast.makeText(AddDuplicateNewJob.this,
                        getString(R.string.msg_synch_error_4),
                        Toast.LENGTH_SHORT).show();
                if (closeActivity) {
                    setResult(Activity.RESULT_OK);
                    finish();
                }

            } else if (erreur.equals("4101")) {
                Toast.makeText(AddDuplicateNewJob.this,
                        getString(R.string.msg_synch_error_2),
                        Toast.LENGTH_SHORT).show();
                if (closeActivity) {
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            } else if (erreur.equals("4005")) {
                Toast.makeText(AddDuplicateNewJob.this,
                        getString(R.string.msg_synch_error_3),
                        Toast.LENGTH_SHORT).show();
                if (closeActivity) {
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            } else if (erreur.equals("4003")) {
                showErrMsgDialog(getString(R.string.msg_sync_error_4003));
            } else {
//                Toast.makeText(AddDuplicateNewJob.this,
//                        getString(R.string.msg_synch_error_new), Toast.LENGTH_SHORT)
//                        .show();
//                if (closeActivity) {
//                    setResult(Activity.RESULT_OK);
//                    finish();
//                }
                showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));
            }
        }
    };


    /**
     * Show error dialog
     */
    protected void showErrMsgDialog(String errMsg) {

        ErrorDialog errDialog = new ErrorDialog(AddDuplicateNewJob.this, errMsg);

        errDialog.show();
    }

    /**
     * For showing the synchronization failure messages
     */
    protected void showSyncFailureMsgDialog(String syncFailureMsg) {

        SynchronizationErrorDialog synchronizationErrorDialog = new SynchronizationErrorDialog
                (AddDuplicateNewJob.this, syncFailureMsg, new SynchronizationErrorDialog
                        .SynchronizationErrorInterface() {
                    @Override
                    public void doOnOkayClick() {
                        //perform any action
                        if (closeActivity) {
                            setResult(Activity.RESULT_OK);
                            finish();
                        }
                    }
                });

        synchronizationErrorDialog.show();
    }

    /**
     * Show authentication error dialog
     */
    protected void showAuthErrDialog() {

        AuthenticationErrorDialog authenticationErrorDialog = new AuthenticationErrorDialog(
                AddDuplicateNewJob.this, user.getLogin());

        authenticationErrorDialog.show();
    }


    /**
     * The ids.
     */
    int ids = 0;

    /**
     * code of previous developer.
     *
     * @param v the v
     */
    public void geocoder(View v) {

        if (servicesConnected()) {

            boolean isGPSEnabled = false;
            boolean isNetworkEnabled = false;

            try {
                locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

                isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                isNetworkEnabled = Build.FINGERPRINT.contains("generic") ? true : locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


                if (!isGPSEnabled && !isNetworkEnabled) {
                    showSettingsAlert();
                } else {

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        callingPermissionLocation();
                    } else {
                        callingLocationFunctionalities();
                    }

                }

            } catch (Exception e) {
                Logger.printException(e);
            }

        }

        ids = v.getId();

    }

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
     * Show settings alert.
     */
    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getString(R.string.textAlertLable) + "!");
        alertDialog.setMessage(getString(R.string.textEnableLocationService));
        alertDialog.setPositiveButton(R.string.textYesLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
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

    /*
     * (non-Javadoc)
     *
     * @see
     * android.location.LocationListener#onLocationChanged(android.location.
     * Location)
     */

    /**
     * The location listener.
     */
    LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub

            myTimer.cancel();
            AddDuplicateNewJob.this.location = location;
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
     * Code of Previous developer.
     *
     * @param latitude  the latitude
     * @param longitude the longitude
     */
    private void afficherAdresse(double latitude, double longitude) {

        Geocoder geo = new Geocoder(this);
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
                    pays = adresse.getCountryName();
                    add_province=adresse.getAdminArea();
                    cp = adresse.getPostalCode();

                    ((EditText) findViewById(R.id.addressEt)).setText(adresse
                            .getAddressLine(0) + "," + adresse.getLocality());
                }

                Toast.makeText(AddDuplicateNewJob.this,
                        getString(R.string.gps_succes), Toast.LENGTH_SHORT)
                        .show();
            } else {
                Toast.makeText(AddDuplicateNewJob.this,
                        getString(R.string.gps_error), Toast.LENGTH_SHORT)
                        .show();
            }
        } catch (IOException e) {
            Logger.printException(e);
            Toast.makeText(AddDuplicateNewJob.this,
                    getString(R.string.gps_error), Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * One click.
     *
     * @return true, if successful
     */
    public boolean oneClick() {
        if (_millis == 0) {
            _millis = new Date().getTime();
            return true;
        } else {
            Date dt = new Date();
            if ((dt.getTime() - _millis) > 1000) {
                _millis = new Date().getTime();
                return true;
            } else
                return false;
        }
    }


    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        Logger.log("OnSavedInstanceState>>>>>", "cALLED");
        super.onSaveInstanceState(outState);
        outState.putInt("mAnnee", mAnnee);
        outState.putInt("mAnneeM", mAnneeM);
        outState.putInt("mJour", mJour);
        outState.putInt("mH1", mH1);
        outState.putInt("mH2", mH2);
        outState.putInt("mHM", mHM);
        outState.putInt("mJourM", mJourM);
        outState.putInt("mMin1", mMin1);
        outState.putInt("mMin2", mMin2);
        outState.putInt("mMinM", mMinM);
        outState.putInt("mMois", mMois);
        outState.putInt("mMoisM", mMoisM);

    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null) {
            Logger.log("onRestoreInstanceState>>>>>", "cALLED");
            mAnnee = savedInstanceState.getInt("mAnnee");
            mAnneeM = savedInstanceState.getInt("mAnneeM");
            mJour = savedInstanceState.getInt("mJour");
            mH1 = savedInstanceState.getInt("mH1");
            mH2 = savedInstanceState.getInt("mH2");
            mHM = savedInstanceState.getInt("mHM");
            mJourM = savedInstanceState.getInt("mJourM");
            mMin1 = savedInstanceState.getInt("mMin1");
            mMin2 = savedInstanceState.getInt("mMin2");
            mMinM = savedInstanceState.getInt("mMinM");
            mMois = savedInstanceState.getInt("mMois");
            mMoisM = savedInstanceState.getInt("mMoisM");

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 35)
            super.onBackPressed();
        else if (requestCode == RequestCode.REQUEST_CODE_CLIENTLISTFULL
                && (resultCode == RESULT_OK)) {

            Bundle bundle = data.getExtras();

            if (bundle.getBoolean(KEYS.NewJob.ISNEARESTCLIENTSELECTED)) {

                String nomSite = "";
                String ObjectType = data.getStringExtra("ObjectType");

                if (ObjectType.equals("Client")) {

                    TextView textView = (TextView) findViewById(R.id.targetCorporationEt);
                    textView.setText(data.getStringExtra("nom"));
                    idClient = Integer.parseInt(data.getStringExtra("id"));
                    idSite = -1;
                } else {
                    TextView textView = (TextView) findViewById(R.id.targetCorporationEt);
                    textView.setText(data.getStringExtra("nmClient"));
                    idClient = Integer
                            .parseInt(data.getStringExtra("IdClient"));
                    idSite = Integer.parseInt(data.getStringExtra("id"));
                    nomSite = data.getStringExtra("nom");

                    siteEt.setText(nomSite);

                }

                // TextView textst = (TextView) findViewById(R.id.siteEt);
                // textst.setText(nomSite);

                String adrGlobale = data.getStringExtra("AdrGlobale");
                String adrComplement = data.getStringExtra("AdrComplement");
                EditText et = (EditText) findViewById(R.id.addressEt);
                et.setText(adrGlobale);
                et = (EditText) findViewById(R.id.additionalAddressEt);
                et.setText(adrComplement);

                GpsX = data.getStringExtra("Longitude");
                GpsY = data.getStringExtra("Latitude");

                idEquipement = -1;
                initAutoCompleteSites();
                initAutoCompleteEquipements();

            } else {
                idClient = bundle.getInt(KEYS.NewJob.CLIENT_ID);
                EditText et = (EditText) findViewById(R.id.addressEt);
                if (bundle.getString(KEYS.NewJob.GLOBAL_ADDRESS).equals(""))
                    et.setText(" ");
                else
                    et.setText((bundle.getString(KEYS.NewJob.GLOBAL_ADDRESS)));

                et = (EditText) findViewById(R.id.additionalAddressEt);

                if (bundle.getString(KEYS.NewJob.COMPLY_ADDRESS).equals(""))
                    et.setText(" ");
                else
                    et.setText(bundle.getString(KEYS.NewJob.COMPLY_ADDRESS));

                TextView textView = (TextView) findViewById(R.id.targetCorporationEt);
                textView.setText(bundle.getString(KEYS.NewJob.CLIENT_NAME));

                Client clientDetails = null;
                try {
                    clientDetails = dao.getClientDetails(idClient);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(clientDetails!=null){
                    rue=clientDetails.getRueClient();
                    ville=clientDetails.getVilleClient();
                    pays=clientDetails.getAdd_client_pays();
                    add_province=clientDetails.getAdd_client_prov();
                    GpsX=clientDetails.getGpsX();
                    GpsY=clientDetails.getGpsY();
                    cp=clientDetails.getAdd_client_cp();
                }else{
                    rue = bundle.getString(KEYS.NewJob.RUE);
                    ville = bundle.getString(KEYS.NewJob.VILLE);
                    GpsX = bundle.getString(KEYS.NewJob.GPSX);
                    GpsY = bundle.getString(KEYS.NewJob.GPSY);
                    cp = bundle.getString(KEYS.NewJob.CP);
                    pays = bundle.getString(KEYS.NewJob.PAYS);
                }

                idSite = -1;
                idEquipement = -1;

                siteEt.setText("");

                equipmentEt.setText("");

                if (gt != null && gt.getFlPageSites() == 1)
                    initAutoCompleteSites();
                initAutoCompleteEquipements();
            }
            // Client clt=(Client)
            // bundle.getSerializable(KEYS.NewJob.CLIENT_DETAILS);

        } else if ((requestCode == RequestCode.REQUEST_CODE_SITELISTFULL)
                && (resultCode == RESULT_OK)) {
            Bundle bundle = data.getExtras();
            idSite = bundle.getInt(KEYS.NewJob.SITE_ID);
            initAutoCompleteEquipements();
            EditText et = (EditText) findViewById(R.id.addressEt);

            if (bundle.getString(KEYS.NewJob.ADDRESS).equals(""))
                et.setText(" ");
            else
                et.setText(bundle.getString(KEYS.NewJob.ADDRESS));

            et = (EditText) findViewById(R.id.additionalAddressEt);
            if (bundle.getString(KEYS.NewJob.COMPLY_ADDRESS).equals(""))
                et.setText(" ");
            else
                et.setText(bundle.getString(KEYS.NewJob.COMPLY_ADDRESS));

            rue = bundle.getString(KEYS.NewJob.RUE);
            ville = bundle.getString(KEYS.NewJob.VILLE);
            GpsX = bundle.getString(KEYS.NewJob.GPSX);
            GpsY = bundle.getString(KEYS.NewJob.GPSY);

            Site siteDetail = null;
            try {
                siteDetail = dao.getSiteDetail(idSite);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if(siteDetail!=null){
                rue=siteDetail.getRueSite();
                ville=siteDetail.getVilleSite();
                pays=siteDetail.getPaysSite();
                add_province=siteDetail.getAdd_site_province();
                GpsX=siteDetail.getGpsX();
                GpsY=siteDetail.getGpsY();
                cp=siteDetail.getCPSite();
            }else{
                rue = bundle.getString(KEYS.NewJob.RUE);
                ville = bundle.getString(KEYS.NewJob.VILLE);
                GpsX = bundle.getString(KEYS.NewJob.GPSX);
                GpsY = bundle.getString(KEYS.NewJob.GPSY);
                cp = bundle.getString(KEYS.NewJob.CP);
                pays = bundle.getString(KEYS.NewJob.PAYS);
            }


            siteEt.setText(bundle.getString(KEYS.NewJob.SITE_NAME));
            idEquipement = -1;

            equipmentEt.setText("");

        } else if ((requestCode == RequestCode.REQUEST_CODE_EQUPMENTLISTFULL)
                && (resultCode == RESULT_OK)) {
            Bundle bundle = data.getExtras();
            idEquipement = bundle.getInt(KEYS.NewJob.EQUIPMENTS_ID);
            int f = bundle.getInt(KEYS.NewJob.SITE_ID);
            idSite = bundle.getInt(KEYS.NewJob.SITE_ID);

            Site sidModel = dao.getSiteDetail(idSite);
            if (sidModel != null) {
                rue = sidModel.getRueSite();
                ville = sidModel.getVilleSite();
                GpsX = sidModel.getGpsX();
                GpsY = sidModel.getGpsY();
                add_province=sidModel.getAdd_site_province();

                TextView site = (TextView) findViewById(R.id.siteEt);
                if (!TextUtils.isEmpty(sidModel.getNmSite()))
                    site.setText(sidModel.getNmSite());

                EditText edt = (EditText) findViewById(R.id.addressEt);
                if (!TextUtils.isEmpty(sidModel.getAdresse()))
                    edt.setText(sidModel.getAdresse());
                EditText etExtraAddress = (EditText) findViewById(R.id.additionalAddressEt);
                if (!TextUtils.isEmpty(sidModel.getAdresseCompl()))
                    etExtraAddress.setText(sidModel.getAdresseCompl());
            } else {
                idSite = -1;
            }


            TextView textView = (TextView) findViewById(R.id.equipmentsEt);
            textView.setText(bundle.getString(KEYS.NewJob.EQUIPMENTS_NAME));
        } else if (requestCode == RequestCode.REQUEST_CODE_ATTACHMENTS_BARCODE) {

            if ((resultCode == RESULT_OK) && (data != null)) {
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
                    Resources res = this.getResources();
                    Drawable drawable = res.getDrawable(R.drawable.barcode);
                    bitmap = ((BitmapDrawable) drawable).getBitmap();
                }

                enregistrerScan(bitmap, code,capturedPath);

            }

        } else if (requestCode == RequestCode.REQUEST_CODE_ATTACHMENTS_CAMERA) {
            if (resultCode == RESULT_OK) {
                enregistrerPhotoCapturer();
            }

        } else if (requestCode == RequestCode.REQUEST_CODE_ATTACHMENTS_LIBRARY) {
            if ((resultCode == RESULT_OK) && (data != null)) {
//                Uri selectedImage = data.getData();
//                enregistrerPhoto(selectedImage);

                //v53 changes new
                handleImageRequest(data);
            }

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

    /**
     * Save the Images taken from gallary and save it to Db and update the
     * attachment list.
     *
     */
    private void handleImageRequest(final Intent data) {

        Uri selectedImage;
        String queryImageUrl = "";

        try {

            if (data != null && data.getData() != null) {

                selectedImage = data.getData();
                queryImageUrl = selectedImage.getPath();
                queryImageUrl = FileUtilsNew.compressImageFile(this, queryImageUrl,
                        false, selectedImage);

                Log.e("TAG", "IMAGE VALUES IS ===>" + queryImageUrl);
//                selectedImage = Uri.fromFile(new File(queryImageUrl));

                File capturedFile = new File(queryImageUrl);

//                rotateFileImageIfNecessary(capturedFile);


                String fileName = capturedFile.getName();
                extention = fileName.substring(fileName.lastIndexOf(".") + 1);

                String cmtr = "";

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
                String idPhoto1 = sdf.format(new Date());

                FileInputStream fis = new FileInputStream(capturedFile);

                if (resisingWidth > 1024 || resisingHeight > 1024) {
                    resisingWidth = 1024;
                    resisingHeight = 1024;
                }

                Bitmap bm2 = resizeBitmap(BitmapFactory.decodeStream(fis),
                        resisingWidth, resisingHeight);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm2.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                photo_Pdas.add(new Photo_Pda(idPhoto1, cmtr, queryImageUrl, byteArray,
                        extention));
                remplir();
                showAttachmentList();


            } else {

                Toast.makeText(this.getApplicationContext(),
                        R.string.msg_error_photo, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Logger.printException(e);
        }


    }

    /**
     * Save the Images taken from gallary and save it to Db and update the
     * attachment list.
     *
     * @param selectedImage the selected image
     */
    public void enregistrerPhoto(Uri selectedImage) {
        try {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = this.getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            if (cursor.getCount() == 0) {
                Toast.makeText(this.getApplicationContext(),
                        R.string.msg_error_photo, Toast.LENGTH_SHORT).show();
            } else {
                // byteArray = null;
                // cursor.moveToFirst();
                //
                // int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                // cursor.close();

                // String capturedPath1 = CommonUtils
                // .getRealPathOfGalleryPhotosFromURI(selectedImage, this);

                /*
                 * new change
                 */
                String capturedPath1 = CommonUtils.getPath(
                        this, selectedImage);

                File capturedFile = new File(capturedPath1);

                // CommonUtils.rotateFileImageIfNecessary(capturedFile);

                String fileName = capturedFile.getName();
                extention = fileName.substring(fileName.lastIndexOf(".") + 1);

                String cmtr = "";

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
                String idPhoto1 = sdf.format(new Date());

                FileInputStream fis = new FileInputStream(capturedFile);

                Bitmap bm2 = resizeBitmap(BitmapFactory.decodeStream(fis),
                        resisingWidth, resisingHeight);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm2.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                photo_Pdas.add(new Photo_Pda(idPhoto1, cmtr, capturedPath1, byteArray,
                        extention));
                remplir();
                showAttachmentList();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Logger.printException(e);
        }
    }

    /**
     * Save the image taken from camera to database and update attachment list.
     * Previous Developer Code
     */
    @SuppressLint("SimpleDateFormat")
    public void enregistrerPhotoCapturer() {
        try {
            restorePreferences();

            File capturedFile = new File(capturedPath);

            String fileName = capturedFile.getName();
            // CommonUtils.rotateFileImageIfNecessary(capturedFile);

            extention = fileName.substring(fileName.lastIndexOf(".") + 1);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
            String idPhoto1 = sdf.format(new Date());

            String cmtr = "";

            // dao.insertPhoto(idPhoto, idIntervention,
            // capturedPath, cmtr, extention);
            FileInputStream fis = new FileInputStream(capturedPath);
            // byte[] data = new byte[1024];

            Bitmap bm2 = resizeBitmap(BitmapFactory.decodeStream(fis),
                    resisingWidth, resisingHeight);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm2.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            photo_Pdas.add(new Photo_Pda(idPhoto1, cmtr, capturedPath, byteArray, extention));
            remplir();
            showAttachmentList();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Logger.printException(e);
        }
    }

    /**
     * Restore preferences. Previous Developer Code
     */
    private void restorePreferences() {
        SharedPreferences settings = this.getPreferences(Context.MODE_PRIVATE);
        capturedPath = settings.getString("fileUriPath", "");
    }

    /**
     * ** This method restrict date picker dialog so user can not choose any
     * future date.
     *
     * @param datePicker the date picker
     */
    public void restrictPreviousDateChoice(DatePicker datePicker,
                                           final android.widget.TextView txtFullDate) {
        try {
            // Field mDatePickerField = dpd.getClass().getDeclaredField(
            // "mDatePicker");
            // mDatePickerField.setAccessible(true);
            // DatePicker datePicker = (DatePicker) mDatePickerField.get(dpd);

            final Calendar calendar = Calendar.getInstance(Locale.getDefault());
            final int minYear = calendar.get(Calendar.YEAR);
            final int minMonth = calendar.get(Calendar.MONTH);
            final int minDay = calendar.get(Calendar.DAY_OF_MONTH);

            txtFullDate.setText(DateFormatUtils.getDateString(minDay, minMonth,
                    minYear));

            datePicker.init(minYear, minMonth, minDay,
                    new OnDateChangedListener() {
                        public void onDateChanged(DatePicker view, int year,
                                                  int month, int day) {
                            Calendar newDate = Calendar.getInstance(Locale
                                    .getDefault());
                            newDate.set(year, month, day);

                            if (calendar.after(newDate)) {

                                view.init(minYear, minMonth, minDay, this);
                            }

                            txtFullDate.setText(DateFormatUtils.getDateString(
                                    day, month, year));
                        }
                    });
        } catch (Exception e) {
            Logger.printException(e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v7.app.AppCompatActivity#onBackPressed()
     */
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

        DialogUtils.showConfirmationDialog(AddDuplicateNewJob.this,
                this.getString(R.string.textAlertLable) + "!",
                this.getString(R.string.textNewJobEndConfirmation), false, 0,
                this.getString(R.string.textYesLable),
                this.getString(R.string.textNoLable),
                new DialogResponseInterface() {

                    @Override
                    public void doOnPositiveBtnClick(Activity arg0) {
                        // TODO Auto-generated method stub
                        AddDuplicateNewJob.super.onBackPressed();

                    }

                    @Override
                    public void doOnNegativeBtnClick(Activity arg0) {
                        // TODO Auto-generated method stub

                    }
                });

    }

    /*
     * (non-Javadoc)
     *
     * @see com.google.android.gms.common.GooglePlayServicesClient.
     * OnConnectionFailedListener
     * #onConnectionFailed(com.google.android.gms.common.ConnectionResult)
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // TODO Auto-generated method stub

        if (connectionResult.hasResolution()) {
            try {

                // Start an Activity that tries to resolve the error
                connectionResult
                        .startResolutionForResult(
                                this,
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

    /*
     * (non-Javadoc)
     *
     * @see
     * com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks
     * #onConnected(android.os.Bundle)
     */
    @Override
    public void onConnected(Bundle arg0) {
        // TODO Auto-generated method stub

    }

    /**
     * Verify that Google Play services is available before making a request.
     *
     * @return true if Google Play services is available, otherwise false
     */
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status

            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode,
                    this, 0);
            dialog.show();

            return false;
        }
    }

    /**
     * insert the Barcode scanned to DB And update AttachmentList.
     *
     * @param bm   the bm
     * @param code the code
     */
    @SuppressLint("SimpleDateFormat")
    public void enregistrerScan(Bitmap bm, String code,String capturedPath) {

        try {

            bitmap = fixImageSize(bm);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
            String idPhoto1 = sdf.format(new Date());
            String idPhoto = user.getId() + "_" + idPhoto1;

            // dataAccessOperator
            // .insertPhoto(idPhoto, idIntervention,
            // jobDetails.getString(R.string.barcodeFilePath),
            // code, "jpg");
            deleteTempImage();

            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);

            byte[] byteArray = stream.toByteArray();

            Photo_Pda photo_Pda = new Photo_Pda(idPhoto, code, capturedPath, byteArray, "jpg");

            photo_Pdas.add(photo_Pda);
            remplir();

            showAttachmentList();

        } catch (Exception e) {
            Logger.printException(e);
        }
    }

    /**
     * Remplir.
     */
    public void remplir() {

        resetAttachmentListView();

        for (int j = 0; j < photo_Pdas.size(); j++) {
            attachmentLinearView.addView(getView(j));
        }

        attachmentLinearView.invalidate();
        scrollView.invalidate();

        if (photo_Pdas.size() >= 18) {
            addAttachmentIconIv.setEnabled(false);
        } else {
            addAttachmentIconIv.setEnabled(true);
        }

        if (photo_Pdas.size() == 0) {
            if (attachmentLinearView.getVisibility() == View.VISIBLE)
                attachmentLinearView.setVisibility(View.GONE);

            arrorBtn.setTag(Boolean.valueOf(false));
            arrorBtn.setImageResource(R.drawable.arrow_down);
        }

    }

    /**
     * Add view.
     *
     * @param position the position
     * @return the view
     */
    public View getView(int position) {

        View row = inflater.inflate(R.layout.layout_attachement_list_tem, null,
                false);

        RelativeLayout containerAttachment = (RelativeLayout) row
                .findViewById(R.id.containerAttachment);
        ImageView removeAttachmentIconIv = (ImageView) row
                .findViewById(R.id.removeAttachmentIconIv);

        final Photo_Pda photo_Pda = photo_Pdas.get(position);
        byte[] mPhoto = photo_Pda.getPhoto();

        ImageView img = (ImageView) row.findViewById(R.id.attachmentIv);

//        Bitmap bitmap;
//        BitmapFactory.Options opt = new BitmapFactory.Options();
//        opt.inDither = true;
//        opt.inPreferredConfig = Bitmap.Config.RGB_565;
//        bitmap = BitmapFactory.decodeByteArray(mPhoto, 0, mPhoto.length, opt);
//        // bmd = new BitmapDrawable(resizeBitmap(bitmap,newwidth,newheight));
//        img.setImageBitmap(bitmap);

        //new changes
        Glide.with(AddDuplicateNewJob.this)
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
                // TODO Auto-generated method stub
                showImg(v);

            }
        });

        // editImage.setImageDrawable(bmd);

        final TextView label = (TextView) row
                .findViewById(R.id.attachmentCommentEt);
        label.setText(photo_Pda.getCommentaire());
        // containerAttachment.setTag(photo_Pda);

        photo_Pda.setPosition(position);
        containerAttachment.setTag(position);
        removeAttachmentIconIv.setTag(position);

        containerAttachment.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                modifyComment(photo_Pda.getCommentaire(), ((RelativeLayout) v));

            }
        });
        containerAttachment.setLongClickable(true);

        removeAttachmentIconIv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                Logger.log("SetOnClickListner", "Called");
                int position = (Integer) v.getTag();
                deleteAttachment(position);

            }
        });

        return row;
    }

    /**
     * Show editImage.
     *
     * @param v the v
     */
    public void showImg(final View v) {

        final Dialog nagDialog = new Dialog(this,
                android.R.style.Theme_Translucent_NoTitleBar);
        nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        nagDialog.setCancelable(true);
        nagDialog.setContentView(R.layout.item_image);
        int position = (Integer) v.getTag();

        ImageView btnClose = (ImageView) nagDialog.findViewById(R.id.closeBtn);
        final TouchImageView ivPreview = (TouchImageView) nagDialog
                .findViewById(R.id.imageItem);
        byte[] bytes = photo_Pdas.get(position).getPhoto();

//        final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        Glide.with(AddDuplicateNewJob.this)
                .load(bytes)
                .asBitmap()
                .fitCenter()
                .placeholder(R.drawable.library_iicon)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        ivPreview.setImageBitmap(resource);
                    }
                });


        btnClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                nagDialog.dismiss();
            }
        });
        nagDialog.show();
    }

    /**
     * Delete attachment.
     *
     * @param pos the pos
     */
    protected void deleteAttachment(final int pos) {
        // TODO Auto-generated method stub
        AlertDialog.Builder adbC = new AlertDialog.Builder(this);

        adbC.setMessage(R.string.txt_confirm);
        adbC.setPositiveButton(R.string.textYesLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        photo_Pdas.remove(pos);
                        attachmentLinearView.removeAllViews();
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
     * Modify comment.
     *
     * @param cmtr the cmtr
     * @param view the view
     */
    protected void modifyComment(final String cmtr, final RelativeLayout view) {
        // TODO Auto-generated method stub

        AlertDialog.Builder adb = new AlertDialog.Builder(this);

        final TextView textView = (TextView) view.getChildAt(3);
        View alertDialogView = inflater.inflate(R.layout.dialogphoto, null);
        adb.setView(alertDialogView);
        final EditText commentEt = (EditText) alertDialogView
                .findViewById(R.id.commentaire);

        commentEt.setText(cmtr);

        adb.setNeutralButton(R.string.modifier,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        String cm = String.valueOf(commentEt.getText());

                        int position = (Integer) view.getTag();
                        photo_Pdas.get(position).setString(cm);

                        textView.setText(cm);
                        CommonUtils.hideKeyboard(AddDuplicateNewJob.this,
                                commentEt);

                        // remplir();

                    }
                });

        adb.setNegativeButton(R.string.textCancelLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // remplir();
                    }
                });

        adb.show();

    }

    /**
     * Reset Attachment ListView and clear all the data.
     */
    public void resetAttachmentListView() {

        attachmentLinearView.removeAllViews();

    }

    /**
     * Resize the bitmap and save it to Db. Previous Developer Code
     *
     * @param bitmap the bitmap
     * @return the bitmap
     */
    public Bitmap fixImageSize(Bitmap bitmap) {
        int origWidth = bitmap.getWidth();
        int origHeight = bitmap.getHeight();
        if (origWidth > 1024 || origHeight > 1024) {
            Bitmap bmap = resizeImageToDb(bitmap);
            return bmap;
        } else {
            return bitmap;
        }

    }

    /**
     * Save bitmap to Database. Previous Developer Code
     *
     * @param bitmap the bitmap
     */
    public void saveBitmap(Bitmap bitmap) {
        FileOutputStream fos = null;
        try {
            File f = new File(this.getString(R.string.barcodeFilePath));
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
     * Resize bitmap.
     *
     * @param bitmap    the bitmap
     * @param newHeight
     * @param newWidth
     * @return the bitmap
     */
    public Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        int width, height;
        float scaleWidth, scaleHeight;
        Matrix matrix;
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        Logger.log("Dao>>>>>> Width Height ", width + " " + height + " ");

        scaleWidth = ((float) newWidth) / width;
        scaleHeight = ((float) newHeight) / height;
        float scale = Math.min(scaleWidth, scaleHeight);
        scale = Math.min(scale, 1);
        matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                matrix, true);
        Logger.log("Dao>>>>>> Width Height New ", resizedBitmap.getWidth()
                + " " + resizedBitmap.getHeight());
        return (resizedBitmap);

    }

    /**
     * Delete temp image. Previous Developer Code
     */
    public void deleteTempImage() {
        File f = new File(this.getString(R.string.barcodeFilePath));
        if (f.exists())
            f.delete();
    }

    /**
     * Show attachment list.
     */
    private void showAttachmentList() {
        if (attachmentLinearView.getVisibility() == View.GONE)
            attachmentLinearView.setVisibility(View.VISIBLE);

        arrorBtn.setTag(Boolean.valueOf(true));
        arrorBtn.setImageResource(R.drawable.arrow_up);

    }

    /**
     * The Class SaveNewJobDataAsyncTask.
     */
    private class SaveNewJobDataAsyncTask extends AsyncTaskCoroutine<String, Boolean> {

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */

        private boolean isJobScheduled;

        private String idJobCreated = null;

        public SaveNewJobDataAsyncTask(boolean isJobschedule) {
            // TODO Auto-generated constructor stub
            this.isJobScheduled = isJobschedule;
        }

        @Override
        public void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            DialogUtils.showProgressDialog(AddDuplicateNewJob.this,
                    AddDuplicateNewJob.this.getString(R.string.textWaitLable),
                    AddDuplicateNewJob.this
                            .getString(R.string.textSavingAddJobData), false);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        public Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub

            boolean drp;
            idJobCreated = dao.getUniqueId();
            int statusCd = 0;
            if (isJobScheduled) {
                drp = dataAccessObject.duplication(idInterv, getDbDate(1),
                        getDbDate(2), getDateM(), photo_Pdas, idJobCreated);
                statusCd = 1;

            } else {
                drp = dataAccessObject.duplication(idInterv, null, null,
                        getDateM(), photo_Pdas, idJobCreated);
                statusCd = 0;
            }

            /*new Changes 50 When the job is added successfully ,
             Check if there is internet connection and generate notification
             service if not save in local DB*/
            if (drp) {
                //v51.0.3
                //   realm = Realm.getDefaultInstance();

                //V54 changes
                notiList = new ArrayList<>();
                NotificationItem item;
                if (statusCd == 1) {
//                    notificationEventList(idJobCreated, 0);
                    item = new NotificationItem(idJobCreated, 0);
                    notiList.add(item);
                }

                item = new NotificationItem(idJobCreated, statusCd);
                notiList.add(item);

//                notificationEventList(idJobCreated, statusCd);

            }

            return drp;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        public void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            boolean drp = result.booleanValue();
            if (drp) {
                Toast.makeText(AddDuplicateNewJob.this,
                        R.string.msg_succes_ajout, Toast.LENGTH_SHORT).show();

                closeActivity = true;
                synch();

            } else {
                Toast.makeText(AddDuplicateNewJob.this, R.string.msg_error,
                        Toast.LENGTH_SHORT).show();
            }
            DialogUtils.dismissProgressDialog();
        }

    }

    @Override
    public void onConnectionSuspended(int arg0) {

    }

    private void notificationEventList(String idJob, int jobStatus) {
        if (SynchroteamUitls.isNetworkAvailable(AddDuplicateNewJob.this)) {
            try {
                l = new ArrayList<>();
                l = RoomDBSingleTone.instance(AddDuplicateNewJob.this).roomDao().getAllNotificationEventModels();
                if (l != null && l.size()>0){
                    for (int i=0;i<l.size();i++){
                        JSONObject jsonObj = new JSONObject(l.get(i).value);

                        hitNotificationEventService(jsonObj.getString("IdJob"),jsonObj.getInt("IdCustomer"),jsonObj.getInt("JobStatus"),
                                l.get(i).id);
                    }
                }
            } catch (Exception e) {

            }
        } else {
            String notificationId = dao.getUniqueId();
            String values = getJsonRequestValues(idJob, dao.getIdCustomer(), jobStatus);
            com.synchroteam.roomDB.entity.NotificationEventModels eventModels = new com.synchroteam.roomDB.entity.NotificationEventModels();
            eventModels.setId(notificationId);
            eventModels.setUrl(SharedPref.getNotiUrlServer(AddDuplicateNewJob.this));
            eventModels.setValue(values);
            saveNotiAlertInLocalDB(eventModels);

        }
    }

    //50.0.0 changes
    private void hitNotificationEventService(String idJobCreated, int idCustomer, int jobStatus,String idLocalDb) throws Exception {
//        ApiInterface apiService =
//                Apiclient.getClient().create(ApiInterface.class);

        String authUserName = dataAccessObject.getUserDomain() + "_" + user.getLogin();
        String authPassword = user.getPwd();
        ApiInterface apiService = ApiClientNew.createService(ApiInterface.class, authUserName, authPassword);

        String url = SharedPref.getNotiUrlServer(AddDuplicateNewJob.this);


        String currentDateUtc = getCurrentUtcDate();

        Logger.log("TAG", "EventNotiResult currentDateUtc--->" + currentDateUtc);

        jsonInfo infoObject =new jsonInfo();
        int idJobType = dataAccessObject.getTypeIntervention(idJobCreated);
        ArrayList<String> list = dataAccessObject.getInterventionTimeDetails(idJobCreated);
        ArrayList<Integer> clientDetails = dataAccessObject.getInterventionClientDetails(idJobCreated);
        infoObject.setIdJobType(idJobType);
        infoObject.setIdTech(user.getId());

        if (clientDetails.get(0) != null && clientDetails.get(0) >= 1)
            infoObject.setIdClient(clientDetails.get(0));

        if (clientDetails.get(1) != null && clientDetails.get(1) >= 1)
            infoObject.setIdSite(clientDetails.get(1));

        if (clientDetails.get(2) != null && clientDetails.get(2) >= 1)
            infoObject.setIdEquipement(clientDetails.get(2));

        if (list.get(0) != null && list.get(0).length() > 0)
            infoObject.setStartedScheduledDateTime(list.get(0));

        if (list.get(1) != null && list.get(1).length() > 0)
            infoObject.setCompletedScheduledDateTime(list.get(1));

        if (list.get(2) != null && list.get(2).length() > 0)
            infoObject.setStartedRealisedDateTime(list.get(2));

        if (list.get(3) != null && list.get(3).length() > 0)
            infoObject.setCompletedRealisedDateTime(list.get(3));

        EventNotiRequest eventNotiRequest = new EventNotiRequest(idCustomer, idJobCreated,
                jobStatus, "tech", currentDateUtc,infoObject);
        Logger.log("TRIDENT", "EventNotiResult EventNotiRequest--->" + eventNotiRequest.toString());

        Call<EventNotiResult> call = apiService.notificationEventService(url, eventNotiRequest);
        call.enqueue(new Callback<EventNotiResult>() {
            @Override
            public void onResponse(Call<EventNotiResult> call, Response<EventNotiResult> response) {


                if (response.isSuccessful()) {
                    int status = response.body().getResult();
                    if (status == 1) {
                        Logger.log("TRIDENT", "EventNotiResult success :" + status);
                    } else {
                        Logger.log("TRIDENT", "EventNotiResult failure :" + status);
                    }
                } else {
                    Logger.log("TRIDENT", "EventNotiResult failure :" + response);
                }
            }

            @Override
            public void onFailure(Call<EventNotiResult> call, Throwable t) {
                Logger.log("TRIDENT", "EventNotiResult Exception :" + t);

            }

        });
    }

    private String getJsonRequestValues(String idJobCreated, int idCustomer, int JobStatus) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();

            jsonObject.put("IdCustomer", idCustomer);
            jsonObject.put("IdJob", idJobCreated);
            jsonObject.put("JobStatus", JobStatus);
            jsonObject.put("EventOrigin", "tech");
            jsonObject.put("timestamp", getCurrentUtcDate());
            jsonObject.put("jsonInfo", getJsonInfObject(idJobCreated));
            Logger.log("TAG","JSON NOTIFICATION VALUES===>"+jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private JSONObject getJsonInfObject(String idJobCreated) {
        JSONObject jsonInfo = new JSONObject();
        int idJobType = dataAccessObject.getTypeIntervention(idJobCreated);
        ArrayList<String> list = dataAccessObject.getInterventionTimeDetails(idJobCreated);
        ArrayList<Integer> clientDetails = dataAccessObject.getInterventionClientDetails(idJobCreated);

        try {
            if (clientDetails.get(0) != null && clientDetails.get(0) >= 1)
                jsonInfo.put("idClient", clientDetails.get(0));
            else
                jsonInfo.put("idClient", null);

            if (clientDetails.get(1) != null && clientDetails.get(1) >= 1)
                jsonInfo.put("idSite", clientDetails.get(1));
            else
                jsonInfo.put("idSite", null);
            if (clientDetails.get(2) != null && clientDetails.get(2) >= 1)
                jsonInfo.put("idEquipment", clientDetails.get(2));
            else
                jsonInfo.put("idEquipment", null);

            jsonInfo.put("idJobType", idJobType);
            jsonInfo.put("idTech", user.getId());

            if (list.get(0) != null && list.get(0).length() > 0)
                jsonInfo.put("startedScheduledDateTime", list.get(0));
            else
                jsonInfo.put("startedScheduledDateTime", null);
            if (list.get(1) != null && list.get(1).length() > 0)
                jsonInfo.put("completedScheduledDateTime", list.get(1));
            else
                jsonInfo.put("completedScheduledDateTime", null);
            if (list.get(2) != null && list.get(2).length() > 0)
                jsonInfo.put("startedRealisedDateTime", list.get(2));
            else
                jsonInfo.put("startedRealisedDateTime", null);
            if (list.get(3) != null && list.get(3).length() > 0)
                jsonInfo.put("completedRealisedDateTime", list.get(3));
            else
                jsonInfo.put("completedRealisedDateTime", null);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonInfo;
    }

    public String getCurrentUtcDate() {
        String currentDate;
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        currentDate = dateFormat.format(date);
        return currentDate;
    }

    public void saveNotiAlertInLocalDB(final com.synchroteam.roomDB.entity.NotificationEventModels notifModel) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    RoomDBSingleTone.instance(AddDuplicateNewJob.this).roomDao().insertAll(notifModel);
                    l = new ArrayList<>();
                    l = RoomDBSingleTone.instance(AddDuplicateNewJob.this).roomDao().getAllNotificationEventModels();
                    Log.e("taf", l.toString());
                }
            }).start();

        } catch (Exception e) {
            Logger.printException(e);
        }
    }

    public class OnSyncAsynTask extends AsyncTaskCoroutine<Void,Void>{

        @Override
        public void onPreExecute() {
            super.onPreExecute();

            if (SynchroteamUitls.isNetworkAvailable(AddDuplicateNewJob.this)) {
                progressDSynch = ProgressDialog.show(AddDuplicateNewJob.this,
                        getString(R.string.textPleaseWaitLable),
                        getString(R.string.msg_synch), true, false);

            }
        }

        @Override
        public Void doInBackground(Void... voids) {

            Message myMessage = new Message();
            try {
                User u = dao.getUser();
                dao.sync(u.getLogin(), u.getPwd());

                if (gt != null && gt.getOptionTaracking() == 0) {
//                            stopService(new Intent(AddDuplicateNewJob.this,
//                                    TrackingService.class));
//                            stopService(new Intent(AddDuplicateNewJob.this,
//                                    TrackingServiceFrequency.class));
//
//                            AlarmManager am = (AlarmManager) AddDuplicateNewJob.this
//                                    .getSystemService(Context.ALARM_SERVICE);
//                            am.cancel(pi);
//                            AlarmManager am1 = (AlarmManager) AddDuplicateNewJob.this
//                                    .getSystemService(Context.ALARM_SERVICE);
//                            am1.cancel(pi1);
                    cancelTrackingAlarm();
                }
                //Logic for hitting notification after sync
                if (notiList != null && notiList.size() > 0)
                    for (int i = 0; i < notiList.size(); i++)
                        notificationEventList(notiList.get(i).getIdIntervention(),
                                notiList.get(i).getStatus());

//                l = new ArrayList<>();
//                l = RoomDBSingleTone.instance(AddDuplicateNewJob.this).roomDao().getAllNotificationEventModels();
//                Log.e("taf", "the list is >>>>  "+ l.size());
//
//                if (l != null && l.size()>0){
//                    for (int i = 0; i < l.size(); i++){
//                        JSONObject jsonObj = new JSONObject(l.get(i).value);
//
//                        hitNotificationEventService(jsonObj.getString("IdJob"),jsonObj.getInt("IdCustomer"),jsonObj.getInt("JobStatus"),
//                                l.get(i).id);
//                        Log.e("JSON","THE JSON IS >>>>>" + jsonObj.getString("IdCustomer"));
//
//                    }
//
//                }

                myMessage.obj = "ok";
                handler.sendMessage(myMessage);
            } catch (Exception ex) {
                String exception = ex.getMessage();
                if (exception != null) {
                    if (exception.indexOf("4001") != -1) {
                        myMessage.obj = "4001";
                    } else if (exception.indexOf("4006") != -1) {
                        myMessage.obj = "4006";
                    } else if (exception.indexOf("4000") != -1) {
                        myMessage.obj = "4000";
                    } else if (exception.indexOf("4101") != -1) {
                        myMessage.obj = "4101";
                    } else if (exception.indexOf("4005") != -1) {
                        myMessage.obj = "4005";
                    } else if (exception.indexOf("4003") != -1) {
                        myMessage.obj = "4003";
                    } else {
                        myMessage.obj = "Error";
                    }
                } else {
                    myMessage.obj = "Error";
                }
                handler.sendMessage(myMessage);
            }

            return null;
        }

        @Override
        public void onPostExecute(Void unused) {
            super.onPostExecute(unused);

                if (progressDSynch != null
                        && progressDSynch.isShowing())
                    progressDSynch.dismiss();

        }
    }
}
