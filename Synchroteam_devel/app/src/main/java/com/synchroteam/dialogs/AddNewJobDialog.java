package com.synchroteam.dialogs;


import static android.content.Context.LOCATION_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.synchroteam.TypefaceLibrary.EditTextAvenirMedium;
import com.synchroteam.beans.Client;
import com.synchroteam.beans.Families;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.beans.JobDetailsModel;
import com.synchroteam.beans.ModeleRapport;
import com.synchroteam.beans.NotificationItem;
import com.synchroteam.beans.Photo_Pda;
import com.synchroteam.beans.Site;
import com.synchroteam.beans.TypeIntervention;
import com.synchroteam.beans.UpdateDataBaseEvent;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.retrofit.ApiClientNew;
import com.synchroteam.retrofit.ApiInterface;
import com.synchroteam.retrofit.models.NotificationService.EventNotiRequest;
import com.synchroteam.retrofit.models.NotificationService.EventNotiResult;
import com.synchroteam.retrofit.models.NotificationService.jsonInfo;
import com.synchroteam.retrofit.models.mobileAuth.AuthData;
import com.synchroteam.roomDB.RoomDBSingleTone;
import com.synchroteam.roomDB.entity.NotificationEventModels;
import com.synchroteam.synchroteam.AddDuplicateNewJob;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.technicalsupport.JobDetails;
import com.synchroteam.tracking.DaoTracking;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DaoTrackingManager;
import com.synchroteam.utils.DateFormatUtils;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.SharedPref;
import com.synchroteam.utils.SynchroteamUitls;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

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
//v51.0.2
import de.greenrobot.event.EventBus;
//import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.synchroteam.synchroteam.NewJob.MY_PERMISSIONS_REQUEST_LOCATION;

/**
 * Created by Trident
 */

public class AddNewJobDialog extends DialogFragment implements View.OnClickListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = AddNewJobDialog.class.getSimpleName();

    Context context;
    private Dao dataAccessObject;
    private DaoTracking daoTracking;
    private User user;
    private Spinner spJobType;
    private EditTextAvenirMedium ajoutDescription;

    private com.synchroteam.TypefaceLibrary.TextView countCharacterLeftTv;
    private int chanracterCount, characterLeftCount;

    /**
     * The id equipement.
     */
    private int idClient, idSite, idEquipement;
    private String clientName;
    private String siteName;
    private String equipmentName;

    // new changes
    private String firstNameContact, lastNameContact, telContact, mobileContact, emailContact, add_province;


    /**
     * The rue.
     */
    private String rue = "";

    /**
     * The ville.
     */
    private String ville = "";

    /**
     * The cp.
     */
    private String cp = "";


    /**
     * The pays.
     */
    private String pays = "";

    /**
     * The Gps x.
     */
    private String GpsX = "";

    /**
     * The Gps y.
     */
    private String GpsY = "";


    /**
     * The adrGlobale.
     */
    private String adrGlobale = "";
    /**
     * The adrComplement.
     */
    private String adrComplement = "";

    /**
     * The photo_ pdas.
     */
    private ArrayList<Photo_Pda> photo_Pdas;

    private int mAnnee, mMois, mJour, mH2, mMin2, mH1, mMin1, mEndAnnee, mEndMois, mEndJour;

    /**
     * The gt.
     */
    private GestionAcces gt;

    /**
     * The id type.
     */
    private int idModele, idType;


    /**
     * The close activity.
     */
    private boolean closeActivity = false;

    /**
     * The simple date format.
     */
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.US);

    /**
     * The duree.
     */
    private Date duree;

    private static AddNewJobDialog.TakeBackActionListener takeBackActionListener;
    //v51.0.2
    // private Realm realm;
    ArrayList<NotificationItem> notiList;
    /**
     * The progress d synch.
     */
    private ProgressDialog progressDSynch;

    /**
     * The location request.
     */
    private LocationRequest locationRequest;

    private LocationManager locationManager;

    /**
     * The my timer.
     */
    private Timer myTimer;
    /**
     * The location client.
     */
    private GoogleApiClient locationClient;

    Double latitude;
    Double longitude;
    String gpsEvent;
    private Calendar cal;

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


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

    private String idJobCreated;

    public static AddNewJobDialog newInstance(int clientId, int siteId, int equipmentId,
                                              String clientName, String siteName, String equipmentName,
                                              TakeBackActionListener listener) {

        AddNewJobDialog fragment = new AddNewJobDialog();
        takeBackActionListener = listener;
        Bundle args = new Bundle();
        args.putInt(KEYS.NewJob.CLIENT_ID, clientId);
        args.putInt(KEYS.NewJob.SITE_ID, siteId);
        args.putInt(KEYS.NewJob.EQUIPMENTS_ID, equipmentId);
        args.putString(KEYS.NewJob.CLIENT_NAME, clientName);
        args.putString(KEYS.NewJob.SITE_NAME, siteName);
        args.putString(KEYS.NewJob.EQUIPMENTS_NAME,
                equipmentName);
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_start_job_new, container);

        dataAccessObject = DaoManager.getInstance();
        daoTracking = DaoTrackingManager.getInstance();

        user = dataAccessObject.getUser();
        gt = dataAccessObject.getAcces();

        photo_Pdas = new ArrayList<Photo_Pda>();

        try {
            duree = simpleDateFormat.parse("02:00");

        } catch (ParseException e) {
            Logger.printException(e);
        }
//v51.0.2
        //   realm = Realm.getDefaultInstance();

        context = this.getActivity();
        cal = Calendar.getInstance();
        initializingForLocation();
        getBunbleData();
        initializeUI(view);
        initAll(view);
        initSpinnerOne(view);
        initSpinnerTwo(view);


        return view;
    }

    private void getBunbleData() {

        Bundle bundle = getArguments();

        if (bundle != null) {
            idClient = bundle.getInt(KEYS.NewJob.CLIENT_ID, 0);
            idSite = bundle.getInt(KEYS.NewJob.SITE_ID, -1);
            idEquipement = bundle.getInt(KEYS.NewJob.EQUIPMENTS_ID, -1);
            clientName = bundle.getString(KEYS.NewJob.CLIENT_NAME);
            siteName = bundle.getString(KEYS.NewJob.SITE_NAME);
            equipmentName = bundle.getString(KEYS.NewJob.EQUIPMENTS_NAME);
        }

    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = this.getActivity();


    }


    private void initializeUI(View view) {

        TextView txtStartJob = (TextView) view.findViewById(R.id.txtStartJob);
//        edtSearchSerialNos = (EditText) view.findViewById(R.id.edtSearchSerialNos);
        LinearLayout lay_close = (LinearLayout) view.findViewById(R.id.lay_close);

        lay_close.setOnClickListener(this);
        txtStartJob.setOnClickListener(this);

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


    @Override
    public void onClick(View view) {
//        switch (view.getId()) {
//            case lay_close:
//                takeBackActionListener.doOnCancelClick();
//                dismiss();
//                break;
//            case R.id.txtStartJob:
//                addIntervention();
//                break;
//        }
        if (view.getId() == R.id.lay_close) {
            takeBackActionListener.doOnCancelClick();
            dismiss();
        }
        if (view.getId() == R.id.txtStartJob) {
            addIntervention();
            //Insert into GPS_STEPS_INTERVENTION
            if (daoTracking.getSessiondata("tracking").equals("on")) {
                gpsEvent = "START";
                geocoder();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    boolean isNotNullString(String text) {
        return (text != null);
    }

    /**
     * Performs neccesary initialization on new job screen.
     *
     * @param view
     */
    public void initAll(View view) {
        if (idClient != -1 && idSite != -1 && idSite > 0 && idClient > 0) {
            Site siteDetail = null;
            try {
                siteDetail = dataAccessObject.getSiteDetail(idSite);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (siteDetail != null) {
                if (siteDetail.getPreNomContactSite().length() > 0 && siteDetail.getNomContactSite().length() > 0) {
                    firstNameContact = siteDetail.getPreNomContactSite();
                    lastNameContact = siteDetail.getNomContactSite();
                } else if (siteDetail.getPreNomContactSite().length() > 0 && siteDetail.getNomContactSite().length() == 0) {
                    firstNameContact = siteDetail.getPreNomContactSite();
                } else if (siteDetail.getPreNomContactSite().length() == 0 && siteDetail.getNomContactSite().length() > 0) {
                    lastNameContact = siteDetail.getNomContactSite();
                } else {
                    firstNameContact = "";
                    lastNameContact = "";
                }
                telContact = siteDetail.getTelephoneContactSite();
                mobileContact = siteDetail.getMobileContactSite();
                emailContact = siteDetail.getEmailContactSite();
                adrGlobale = siteDetail.getAdresse();
                adrComplement = siteDetail.getAdresseCompl();


                GpsX = siteDetail.getGpsX();
                GpsY = siteDetail.getGpsY();

                //new changes
                rue = siteDetail.getRueSite();
                ville = siteDetail.getVilleSite();
                pays = siteDetail.getPaysSite();
                cp = siteDetail.getCPSite();
                add_province = siteDetail.getAdd_site_province();


//                if (rue.equals("") && ville.equals("") && pays.equals("")) {
//                    String addr = siteDetail.getNmSite(); //site text field data
//                    rue = siteDetail.getAdresse();
//                    ville = siteDetail.getAdresse();
//                    pays = siteDetail.getAdresse();
//                    Log.e("address", "address site start job: " + rue + "\n" + ville + "\n" + pays);
//                } else {
//                    rue = siteDetail.getRueSite();
//                    ville = siteDetail.getVilleSite();
//                    pays = siteDetail.getPaysSite();
//                }


            } else {
                Client clientDetails = null;
                try {
                    clientDetails = dataAccessObject.getClientDetails(idClient);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (clientDetails != null) {

                    if (clientDetails.getPreNomContactClient().length() > 0 && clientDetails.getNomContactClient().length() > 0) {
                        firstNameContact = clientDetails.getPreNomContactClient();
                        lastNameContact = clientDetails.getNomContactClient();
                    } else if (clientDetails.getPreNomContactClient().length() > 0 && clientDetails.getNomContactClient().length() == 0) {
                        firstNameContact = clientDetails.getPreNomContactClient();
                    } else if (clientDetails.getPreNomContactClient().length() == 0 && clientDetails.getNomContactClient().length() > 0) {
                        lastNameContact = clientDetails.getNomContactClient();
                    } else {
                        firstNameContact = "";
                        lastNameContact = "";
                    }
                    telContact = clientDetails.getTelephoeClient();
                    mobileContact = clientDetails.getMobileContactClient();
                    emailContact = clientDetails.getEmailContactClient();
                    adrGlobale = clientDetails.getAdresseGlobalClient();
                    adrComplement = clientDetails.getAdresseComplClient();
                    add_province = clientDetails.getAdd_client_prov();

                    GpsX = clientDetails.getGpsX();
                    GpsY = clientDetails.getGpsY();

                    //new changes
                    rue = clientDetails.getRueClient();
                    ville = clientDetails.getVilleClient();
                    pays = clientDetails.getAdd_client_pays();
                    add_province = clientDetails.getAdd_client_prov();
                    cp = clientDetails.getAdd_client_cp();


//                    if (rue.equals("") && ville.equals("")) {
//                        rue = clientDetails.getAdresseGlobalClient();
//                        ville = clientDetails.getAdresseGlobalClient();
//                        Log.e("address", "address customer start job: " + rue);
//                        Log.e("address", "address customer start job: " + ville);
//                    } else {
//                        rue = clientDetails.getRueClient();
//                        ville = clientDetails.getVilleClient();
//                    }

                }
            }
        } else if (idSite != -1 && idSite > 0) {
            Site siteDetail = null;
            try {
                siteDetail = dataAccessObject.getSiteDetail(idSite);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (siteDetail != null) {
                if (siteDetail.getPreNomContactSite().length() > 0 && siteDetail.getNomContactSite().length() > 0) {
                    firstNameContact = siteDetail.getPreNomContactSite();
                    lastNameContact = siteDetail.getNomContactSite();
                } else if (siteDetail.getPreNomContactSite().length() > 0 && siteDetail.getNomContactSite().length() == 0) {
                    firstNameContact = siteDetail.getPreNomContactSite();
                } else if (siteDetail.getPreNomContactSite().length() == 0 && siteDetail.getNomContactSite().length() > 0) {
                    lastNameContact = siteDetail.getNomContactSite();
                } else {
                    firstNameContact = "";
                    lastNameContact = "";
                }
                telContact = siteDetail.getTelephoneContactSite();
                mobileContact = siteDetail.getMobileContactSite();
                emailContact = siteDetail.getEmailContactSite();
                add_province = siteDetail.getAdd_site_province();

                adrGlobale = siteDetail.getAdresse();
                adrComplement = siteDetail.getAdresseCompl();
                GpsX = siteDetail.getGpsX();
                GpsY = siteDetail.getGpsY();
                rue = siteDetail.getRueSite();
                ville = siteDetail.getVilleSite();
                cp = siteDetail.getCPSite();
                pays = siteDetail.getPaysSite();


            }

        } else if (idClient != -1 && idClient > 0) {
            Client clientDetails = null;
            try {
                clientDetails = dataAccessObject.getClientDetails(idClient);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (clientDetails != null) {
                if (clientDetails.getPreNomContactClient().length() > 0 && clientDetails.getNomContactClient().length() > 0) {
                    firstNameContact = clientDetails.getPreNomContactClient();
                    lastNameContact = clientDetails.getNomContactClient();
                } else if (clientDetails.getPreNomContactClient().length() > 0 && clientDetails.getNomContactClient().length() == 0) {
                    firstNameContact = clientDetails.getPreNomContactClient();
                } else if (clientDetails.getPreNomContactClient().length() == 0 && clientDetails.getNomContactClient().length() > 0) {
                    lastNameContact = clientDetails.getNomContactClient();
                } else {
                    firstNameContact = "";
                    lastNameContact = "";
                }
                telContact = clientDetails.getTelephoeClient();
                mobileContact = clientDetails.getMobileContactClient();
                add_province = clientDetails.getAdd_client_prov();
                emailContact = clientDetails.getEmailContactClient();
                adrGlobale = clientDetails.getAdresseGlobalClient();
                adrComplement = clientDetails.getAdresseComplClient();

                //new changes
                rue = clientDetails.getRueClient();
                ville = clientDetails.getVilleClient();
                pays = clientDetails.getAdd_client_pays();
                add_province = clientDetails.getAdd_client_prov();
                cp = clientDetails.getAdd_client_cp();

                GpsX = clientDetails.getGpsX();
                GpsY = clientDetails.getGpsY();

//                // null case
//                if (rue.equals("") && ville.equals("")) {
//                    rue = clientDetails.getAdresseGlobalClient();
//                    ville = clientDetails.getAdresseGlobalClient();
//                    Log.e("address", "address customer start job: " + rue);
//                    Log.e("address", "address customer start job: " + ville);
//                } else {
//                    rue = clientDetails.getRueClient();
//                    ville = clientDetails.getVilleClient();
//                }
            }
        }
        //  end  new v51.0.2 21.0ct.19
//v52old
//        Client client = dataAccessObject.getCLientDetails(idClient);
//
//        if (client != null) {
//            adrGlobale = client.getAdresseGlobalClient();
//            adrComplement = client.getAdresseComplClient();
//            GpsX = client.getGpsX();
//            GpsY = client.getGpsY();
//
//        }
//
//        if (idEquipement == -1) {
//
//            //new changes
//            if (idSite == -1) {
//                Client clientDetails = null;
//                try {
//                    clientDetails = dataAccessObject.getClientDetails(idClient);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                if (clientDetails != null) {
//                    nameContact = clientDetails.getPreNomContactClient() + " "
//                            + clientDetails.getNomContactClient();
//                    telContact = clientDetails.getTelephoeClient();
//                    mobileContact = clientDetails.getMobileContactClient();
//                    emailContact = clientDetails.getEmailContactClient();
//                }
//
//            } else {
//                Site siteDetail = null;
//                try {
//                    siteDetail = dataAccessObject.getSiteDetail(idSite);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                if (siteDetail != null) {
//                    nameContact = siteDetail.getPreNomContactSite() + " "
//                            + siteDetail.getNomContactSite();
//                    telContact = siteDetail.getTelephoneContactSite();
//                    mobileContact = siteDetail.getMobileContactSite();
//                    emailContact = siteDetail.getEmailContactSite();
//
//                    adrGlobale = siteDetail.getAdresse();
//                    adrComplement = siteDetail.getAdresseCompl();
//
//                    GpsX = siteDetail.getGpsX();
//                    GpsY = siteDetail.getGpsY();
//
//
//                }
//
//            }
//        } else {
//            if (idSite == -1 || idSite == 0) {
//                Client clientDetails = null;
//                try {
//                    clientDetails = dataAccessObject.getClientDetails(idClient);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                if (clientDetails != null) {
//                    nameContact = clientDetails.getPreNomContactClient() + " "
//                            + clientDetails.getNomContactClient();
//                    telContact = clientDetails.getTelephoeClient();
//                    mobileContact = clientDetails.getMobileContactClient();
//                    emailContact = clientDetails.getEmailContactClient();
//                }
//
//            } else {
//                Site siteDetail = null;
//                try {
//                    siteDetail = dataAccessObject.getSiteDetail(idSite);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                if (siteDetail != null) {
//                    nameContact = siteDetail.getPreNomContactSite() + " "
//                            + siteDetail.getNomContactSite();
//                    telContact = siteDetail.getTelephoneContactSite();
//                    mobileContact = siteDetail.getMobileContactSite();
//                    emailContact = siteDetail.getEmailContactSite();
//                }
//
//            }
//        }

        countCharacterLeftTv = (com.synchroteam.TypefaceLibrary.TextView) view.findViewById(R.id.countCharacterLeftTv);

        view.findViewById(R.id.containerDescriptionCOunt)
                .setVisibility(View.VISIBLE);
        chanracterCount = this.getResources().getInteger(
                R.integer.maxCharacterJobDescription);

        ajoutDescription = view.findViewById(R.id.Ajoutdescription);

        ajoutDescription.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                characterLeftCount = chanracterCount - s.toString().length();

                countCharacterLeftTv.setText(characterLeftCount + "");

            }
        });

        if (gt != null && gt.getFlMandatoryDescription() == 0) {
            ajoutDescription
                    .setHintTextColor(this.getResources().getColor(
                            R.color.textColorLableNonManadtoryMandatoryFiled));
        }

    }

    /**
     * Inits the spinner containing reports data.
     */
    public void initSpinnerOne(View view) {
        ArrayList<ModeleRapport> al = dataAccessObject.getModeleRapport();
        Spinner s = (Spinner) view.findViewById(R.id.reportsSpinner);
        ArrayAdapter<ModeleRapport> adapter = new ArrayAdapter<ModeleRapport>(
                getActivity(), android.R.layout.simple_spinner_item, al);
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
    public void initSpinnerTwo(final View rootView) {
        ArrayList<TypeIntervention> al = dataAccessObject.getTypeIntervention();

        if (al.size() != 0) {

            TypeIntervention tr = al.get(0);
            try {
                duree = simpleDateFormat.parse(tr.getHrDureeIntervention());
                assert duree != null;
                if (duree.getHours() == 0 && duree.getMinutes() == 0) {
                    duree = simpleDateFormat.parse("02:00");
                }
            } catch (ParseException e) {
                Logger.printException(e);

            }
        }

        spJobType = (Spinner) rootView.findViewById(R.id.jobTypeSpinner);
        ArrayAdapter<TypeIntervention> adapter = new ArrayAdapter<TypeIntervention>(
                getActivity(), android.R.layout.simple_spinner_item, al);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spJobType.setAdapter(adapter);

        // to select the default job type initially.
        for (int i = 0; i < al.size(); i++) {
            if (al.get(i).getFlDefault() == 1) {
                spJobType.setSelection(i);
            }
        }
        // .......New changes............

        spJobType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapter,
                                       View view, int pos, long id) {
                TypeIntervention ti = (TypeIntervention) adapter
                        .getItemAtPosition(pos);
                idType = ti.getId();
                initDefaultModel(idType, rootView);

                try {
                    duree = simpleDateFormat.parse(ti
                            .getHrDureeIntervention());
                    assert duree != null;
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
     * @param idType   the id type
     * @param rootView
     */
    public void initDefaultModel(int idType, View rootView) {
        ModeleRapport mr = dataAccessObject.getDefaultModeleRapport(idType);
        Spinner s = (Spinner) rootView.findViewById(R.id.reportsSpinner);
        int i = 0;

        ArrayList<ModeleRapport> al = dataAccessObject.getModeleRapport();
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

    private void showToastMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    /**
     * Perform Validation And addd the intervention.
     */
    public void addIntervention() {

        String adrGlobal = adrGlobale;
        String adrCompl = adrComplement;
        String nom_client = clientName;
        String nom_site = siteName;
        String nom_equipement = equipmentName;
        String description = ajoutDescription.getText().toString();

        if (adrGlobal == null)
            adrGlobal = "";

        if (adrCompl == null)
            adrCompl = "";

        if (nom_client == null)
            nom_client = "";

        if (nom_site == null)
            nom_site = "";

        if (nom_equipement == null)
            nom_equipement = "";

        if (description == null)
            description = "";

        Boolean drapo = true;

        if (description.equals("") && gt != null
                && gt.getFlMandatoryDescription() == 1)
            drapo = false;
        else
            drapo = true;

        if (!nom_client.trim().equals("") && !adrGlobal.trim().equals("") && drapo) {

            String x = null;
            String y = null;

            x = GpsX;
            y = GpsY;

            getDateTimeJob();

            Logger.log(TAG, "ADD job Dialog start time is ===>" + getDbDate(1));
            Logger.log(TAG, "ADD job Dialog end time is ===>" + getDbDate(2));

//            new changes update cdStatus to 1

            new SaveNewJobDataAsyncTask(getActivity()).execute(getDbDate(1),
                    getDbDate(2), idModele + "", idType + "", nom_client,
                    nom_site, adrGlobal, adrCompl.toString(), 1 + "",
                    idClient + "", idSite + "", idEquipement + "",
                    nom_equipement, y, x, description, rue, ville,
                    getDateM(), firstNameContact, lastNameContact, telContact, mobileContact,
                    emailContact, cp, pays, add_province);


        } else {
            Toast.makeText(context, R.string.msg_saisie_oblig, Toast.LENGTH_SHORT)
                    .show();
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
        String dbDate = null;

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
     * Gets the Start date.
     *
     * @return the db date
     */
    @SuppressWarnings("deprecation")
    @SuppressLint("SimpleDateFormat")
    public void getDateTimeJob() {

        Calendar rightNow = Calendar.getInstance();

        mH1 = rightNow.get(Calendar.HOUR_OF_DAY);
        mMin1 = rightNow.get(Calendar.MINUTE);
        mAnnee = rightNow.get(Calendar.YEAR) - 1900;
        mMois = rightNow.get(Calendar.MONTH);
        mJour = rightNow.get(Calendar.DATE);
        Date mFinalTimeEndDate = new Date(mAnnee, mMois, mJour, mH1
                + duree.getHours(), mMin1
                + duree.getMinutes());

        mH2 = mFinalTimeEndDate.getHours();
        mMin2 = mFinalTimeEndDate.getMinutes();

        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.HOUR_OF_DAY, duree.getHours());
        endDate.add(Calendar.MINUTE, duree.getMinutes());


        mEndAnnee = endDate.get(Calendar.YEAR) - 1900;
        mEndMois = endDate.get(Calendar.MONTH);
        mEndJour = endDate.get(Calendar.DATE);
        mH2 = endDate.get(Calendar.HOUR_OF_DAY);
        mMin2 = endDate.get(Calendar.MINUTE);


//        if (mH1 + duree.getHours() > 23) {
//            mH2 = 23;
//            mMin2 = 55;
//        } else {
//            mH2 = mH1 + duree.getHours();
//            mMin2 = mMin1 + duree.getMinutes();
//        }


    }

    /**
     * Gets the Date from a String literal.
     *
     * @param mDate the m date
     * @return the date from str hour
     */
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
     * Gets the Meeting Date.
     *
     * @return the date m
     */
    @SuppressWarnings("deprecation")
    public String getDateM() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);
        return null;
    }

    /**
     * The Class SaveNewJobDataAsyncTask.
     */
    private class SaveNewJobDataAsyncTask extends
            AsyncTaskCoroutine<String, Boolean> {

        Context context;


        public SaveNewJobDataAsyncTask(Context context) {
            this.context = context;
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        public void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();

            DialogUtils.showProgressDialog(getActivity(),
                    context.getString(R.string.textWaitLable),
                    context.getString(R.string.textSavingAddJobData),
                    false);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.os.AsyncTask#doInBackground(Params[])sc
         */
        @Override
        public Boolean doInBackground(String... params) {

            idJobCreated = dataAccessObject.getUniqueId();

            boolean drp = dataAccessObject.ajoutIntervention(idJobCreated, params[0], params[1],
                    Integer.parseInt(params[2]), Integer.parseInt(params[3]),
                    params[4], params[5], params[6], params[7],
                    Integer.parseInt(params[8]), Integer.parseInt(params[9]),
                    Integer.parseInt(params[10]), Integer.parseInt(params[11]),
                    params[12], params[13], params[14], params[15], params[16],
                    params[17], params[18], params[19], params[20], params[21],
                    params[22], params[23], photo_Pdas, params[24], params[25], params[26]);
            /*new Changes 50 When the job is added successfully ,
             Check if there is internet connection and generate notification
             service if not save in local DB*/
            if (drp) {
                int statusCd = Integer.parseInt(params[8]);
                Logger.log("cdStatut", "job created status is:" + Integer.parseInt(params[8]));
                //  //v51.0.2
                //   realm = Realm.getDefaultInstance();
//                if (statusCd == 1)
//                    notificationEventList(idJobCreated, 0);
//
//                notificationEventList(idJobCreated, statusCd);

                //V54 changes
                notiList = new ArrayList<>();
                NotificationItem item;
                if (statusCd == 1) {
                    item = new NotificationItem(idJobCreated, 0);
                    notiList.add(item);
                }

                item = new NotificationItem(idJobCreated, statusCd);
                notiList.add(item);

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

            DialogUtils.dismissProgressDialog();

            boolean drp = result;
            if (drp) {
                Toast.makeText(getActivity(), R.string.msg_succes_ajout,
                        Toast.LENGTH_SHORT).show();
                closeActivity = true;
                Log.d("contact", "contact: " + firstNameContact);
                Log.d("contact_last", "contact_last: " + lastNameContact);
                synch();

            } else
                Toast.makeText(getActivity(), R.string.msg_error,
                        Toast.LENGTH_SHORT).show();
        }

    }

    private void notificationEventList(String idJob, int jobStatus) {
        if (SynchroteamUitls.isNetworkAvailable(context)) {
            try {
                hitNotificationEventService(idJob, dataAccessObject.getIdCustomer(), jobStatus);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String notificationId = dataAccessObject.getUniqueId();
            String values = getJsonRequestValues(idJob, dataAccessObject.getIdCustomer(), jobStatus);
            com.synchroteam.roomDB.entity.NotificationEventModels eventModels = new com.synchroteam.roomDB.entity.NotificationEventModels();
            eventModels.setId(notificationId);
            eventModels.setUrl(SharedPref.getNotiUrlServer(context));
            eventModels.setValue(values);
            saveNotiAlertInLocalDB(eventModels);
        }
    }

    //v50.0.0 changes
    private void hitNotificationEventService(String idJobCreated, int idCustomer, int jobStatus) throws Exception {

        String authUserName = dataAccessObject.getUserDomain() + "_" + user.getLogin();
        String authPassword = user.getPwd();
        ApiInterface apiService = ApiClientNew.createService(ApiInterface.class, authUserName, authPassword);

        String url = SharedPref.getNotiUrlServer(context);

        String currentDateUtc = getCurrentUtcDate();

        Logger.log("TAG", "EventNotiResult currentDateUtc--->" + currentDateUtc);

        jsonInfo infoObject = new jsonInfo();
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
                jobStatus, "tech", currentDateUtc, infoObject);
        Logger.log("TAG", "EventNotiResult EventNotiRequest--->" + eventNotiRequest.toString());

        Call<EventNotiResult> call = apiService.notificationEventService(url, eventNotiRequest);
        call.enqueue(new Callback<EventNotiResult>() {
            @Override
            public void onResponse(@NotNull Call<EventNotiResult> call, @NotNull Response<EventNotiResult> response) {


                if (response.isSuccessful()) {
                    assert response.body() != null;
                    int status = response.body().getResult();
                    if (status == 1) {
                        Logger.log("EventNotiResult", "EventNotiResult success :" + status);
                    } else {
                        Logger.log("EventNotiResult", "EventNotiResult failure :" + status);
                    }
                } else {
                    Logger.log("EventNotiResult", "EventNotiResult failure :" + response);
                }
            }

            @Override
            public void onFailure(@NotNull Call<EventNotiResult> call, @NotNull Throwable t) {
                Logger.log("EventNotiResult", "EventNotiResult Exception :" + t);

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
            Logger.log("TAG", "JSON NOTIFICATION VALUES===>" + jsonObject.toString());

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
                    RoomDBSingleTone.instance(getActivity()).roomDao().insertAll(notifModel);
                    List<NotificationEventModels> l = new ArrayList<>();
                    l = RoomDBSingleTone.instance(getActivity()).roomDao().getAllNotificationEventModels();
                    Log.e("taf", l.toString());
                }
            }).start();

        } catch (Exception e) {
            Logger.printException(e);
        }
    }


    /**
     * Synchronise with Db.
     */
    private void synchV52() {
        AuthData getAuthandExpriryToken = dataAccessObject.getUserToken();
        if (getAuthandExpriryToken != null && user.getPwd() != null && !TextUtils.isEmpty(user.getPwd())) {
            boolean isExpiry = DateFormatUtils.getFormattedDateFromAPIDateBase(getAuthandExpriryToken.getExpiry());
            if (isExpiry) {
                synch();
            } else {
                SynchroteamUitls.ISTokonExpiryGotoLogin(getActivity());
            }
        } else {
            SynchroteamUitls.ISTokonExpiryGotoLogin(getActivity());
        }
    }

    private void synch() {

        if (SynchroteamUitls.isNetworkAvailable(context)) {
            progressDSynch = ProgressDialog.show(getActivity(),
                    getString(R.string.textPleaseWaitLable),
                    getString(R.string.msg_synch), true, false);

            Thread syncDatabaseWithServer = new Thread((new Runnable() {
                @Override
                public void run() {

                    Message myMessage = new Message();
                    try {
                        User u = dataAccessObject.getUser();
                        dataAccessObject.sync(u.getLogin(), u.getPwd());

//                        if (gt != null && gt.getOptionTaracking() == 0) {
//                            cancelTrackingAlarm();
//                        }

                        //Logic for hitting notification after sync
                        if (notiList != null && notiList.size() > 0)
                            for (int i = 0; i < notiList.size(); i++)
                                notificationEventList(notiList.get(i).getIdIntervention(),
                                        notiList.get(i).getStatus());

                        myMessage.obj = "ok";
                        handler.sendMessage(myMessage);
                    } catch (Exception ex) {
                        String exception = ex.getMessage();
                        if (exception != null) {
                            if (exception.indexOf("4001") != -1) {
                                myMessage.obj = "4001";
                            } else if (exception.indexOf("4000") != -1) {
                                myMessage.obj = "4000";
                            } else if (exception.indexOf("4006") != -1) {
                                myMessage.obj = "4006";
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
                    } finally {
                        if (progressDSynch != null
                                && progressDSynch.isShowing())
                            progressDSynch.dismiss();
                    }
                }
            }));
            syncDatabaseWithServer.start();
        } else {

            //new changes
            //if internet is off, insert families in T_SAISIE_BLOC
            insertInTSaisieBloc();

            //save in database
            if (notiList != null && notiList.size() > 0)
                for (int i = 0; i < notiList.size(); i++)
                    notificationEventList(notiList.get(i).getIdIntervention(),
                            notiList.get(i).getStatus());

            EventBus.getDefault().post(new UpdateDataBaseEvent());

            if (closeActivity) {
                dismiss();
            } else {
                assert getActivity() != null;
                if (!getActivity().isFinishing()) {
                    SynchroteamUitls.showToastMessage(getActivity());
                }
            }
        }

    }

    /**
     * While internet is off, we can't able to sync, hence we add the lines in T_SAISIE_BLOC.
     */
    private void insertInTSaisieBloc() {
        ArrayList<Families> families = dataAccessObject.getAllFamilies(idModele);
        int idCustomer = dataAccessObject.getIdCustomer();

        for (Families family : families) {
            dataAccessObject.addSharedBlock(idCustomer, idModele, family.getIdFamily(), idJobCreated, 0, 0, family.getNameFamily(), 0, family.getPosition());
        }
    }

    /**
     * The handler.
     */
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NotNull Message msg) {
            String erreur = (String) msg.obj;
            if (erreur.equals("ok")) {
                Toast.makeText(context,
                        getString(R.string.msg_synch_ok), Toast.LENGTH_SHORT)
                        .show();

                EventBus.getDefault().post(new UpdateDataBaseEvent());

                if (closeActivity) {
                    JobDetailsModel model = dataAccessObject.getCurrentJob(idJobCreated);
                    if (model != null) {
                        navigateToJobDetailActivity(model);
                    }
                    dismiss();
                }
            } else if (erreur.equals("4001") || erreur.equals("4000")) {
                showAuthErrDialog();
            } else if (erreur.equals("4006")) {
                Toast.makeText(context,
                        getString(R.string.msg_synch_error_4),
                        Toast.LENGTH_SHORT).show();
                if (closeActivity) {
                    dismiss();
                }
            } else if (erreur.equals("4101")) {
                showMultipleDeviecDialog();
            } else if (erreur.equals("4005")) {
                showAppUpdatetDialog();
            } else if (erreur.equals("4003")) {
                showErrMsgDialog(getString(R.string.msg_sync_error_4003));
            } else {
                showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));
            }
            return true;
        }
    });
//    private final Handler handler = new Handler() {
//
//        public void handleMessage(Message msg) {
//            String erreur = (String) msg.obj;
//            if (erreur.equals("ok")) {
//                Toast.makeText(context,
//                        getString(R.string.msg_synch_ok), Toast.LENGTH_SHORT)
//                        .show();
//
//                EventBus.getDefault().post(new UpdateDataBaseEvent());
//
//                if (closeActivity) {
//
//                    JobDetailsModel model = dataAccessObject.getCurrentJob(idJobCreated);
//                    if (model != null) {
//                        navigateToJobDetailActivity(model);
//                    }
//                    dismiss();
//                }
//
//            } else if (erreur.equals("4001") || erreur.equals("4000")) {
//                showAuthErrDialog();
//            } else if (erreur.equals("4006")) {
//                Toast.makeText(context,
//                        getString(R.string.msg_synch_error_4),
//                        Toast.LENGTH_SHORT).show();
//                if (closeActivity) {
//                    dismiss();
//                }
//
//            } else if (erreur.equals("4101")) {
//                showMultipleDeviecDialog();
//            } else if (erreur.equals("4005")) {
//                showAppUpdatetDialog();
//            } else if (erreur.equals("4003")) {
//                showErrMsgDialog(getString(R.string.msg_sync_error_4003));
//            } else {
//                showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));
//            }
//        }
//    };

    /**
     * Show error dialog
     */
    protected void showErrMsgDialog(String errMsg) {

        ErrorDialog errDialog = new ErrorDialog(getActivity(), errMsg);

        errDialog.show();
    }

    /**
     * For showing the synchronization failure messages
     */
    protected void showSyncFailureMsgDialog(String syncFailureMsg) {

        SynchronizationErrorDialog synchronizationErrorDialog = new SynchronizationErrorDialog
                (getActivity(), syncFailureMsg, new SynchronizationErrorDialog
                        .SynchronizationErrorInterface() {
                    @Override
                    public void doOnOkayClick() {
                        //perform any action
                        if (closeActivity) {
                            dismiss();
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
                getActivity(), user.getLogin());

        authenticationErrorDialog.show();
    }

    /**
     * Show app update dialog
     */
    protected void showAppUpdatetDialog() {

        AppUpdateDialog appUpdateDialog = new AppUpdateDialog(getActivity());

        appUpdateDialog.show();
    }


    /**
     * Show multiple user dialog
     */
    protected void showMultipleDeviecDialog() {

        MultipleDeviceNotSupported multipleDeviceDialog = new MultipleDeviceNotSupported(
                getActivity(),
                new MultipleDeviceNotSupported.MultipleDeviceInterface() {

                    @Override
                    public void doOnOkClick() {
                    }

                    @Override
                    public void doOnLinkClick() {
                        if (Locale.getDefault().getLanguage()
                                .equalsIgnoreCase("fr")) {
                            openLinkInBrowser(getString(R.string.txtInfoFr));
                        } else if (Locale.getDefault().getLanguage()
                                .equalsIgnoreCase("es")) {
                            openLinkInBrowser(getString(R.string.txtInfoEs));
                        }else {
                            openLinkInBrowser(getString(R.string.txtInfoEn));
                        }
                    }
                });
        multipleDeviceDialog.show();
    }

    /***
     * Create a chooser of browsers to open the link
     *
     * @param link
     */
    protected void openLinkInBrowser(String link) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(link));

        // Always use string resources for UI text. This says something like
        // "Share this photo with"
        String title = getString(R.string.titleBrowserSelection);
        // Create and start the chooser
        Intent chooser = Intent.createChooser(intent, title);
        startActivity(chooser);
    }

    public void navigateToJobDetailActivity(JobDetailsModel model) {
        Bundle bundle = new Bundle();
        String[] numInterv = model.getJobType()
                .split("-");
        String nmInterv = "";
        if (numInterv.length == 2)
            nmInterv = numInterv[0].substring(1);
        else
            nmInterv = "0";

        String jobNo = null;

        if (!TextUtils.isEmpty(model.getRefCustomer()))
            jobNo = "#" + model.getRefCustomer()
                    + " - " + model.getJobType();
        else if (model.getJobNumber() != 0)
            jobNo = "#" + model.getJobNumber()
                    + " - " + model.getJobType();

        bundle.putString(KEYS.CurrentJobs.ID,
                model.getIdJob());

        bundle.putInt(KEYS.CurrentJobs.CD_STATUS, model.getJobStatus());
        bundle.putString(KEYS.CurrentJobs.ID_USER,
                String.valueOf(model.getJobUserId()));
        bundle.putString(KEYS.CurrentJobs.ID_MODEL,
                model.getIdModel());
        bundle.putString(KEYS.CurrentJobs.LAT,
                model.getLat());
        bundle.putString(KEYS.CurrentJobs.LON,
                model.getLon());
        bundle.putString("NumIntevType", nmInterv);
        bundle.putString(KEYS.CurrentJobs.NOMSITE,
                model.getSiteName());
        bundle.putString(KEYS.CurrentJobs.NOMEQUIPMENT,
                model.getEquipmentName());
        bundle.putInt(KEYS.CurrentJobs.IDSITE,
                model.getIdSite());
        bundle.putInt(KEYS.CurrentJobs.IDCLIENT, model.getIdEquipment());
        bundle.putInt(KEYS.CurrentJobs.IDEQUIPMENT, model.getIdEquipment());
        bundle.putString(KEYS.CurrentJobs.TYPE,
                jobNo);
        bundle.putBoolean(KEYS.CurrentJobs.IDSTARTJOB, true);
        Intent jobDetailIntent = new Intent(getActivity(),
                JobDetails.class);
        jobDetailIntent.putExtras(bundle);
        startActivity(jobDetailIntent);
    }


    private void geocoder() {

        if (servicesConnected()) {
            boolean isGPSEnabled = false;
            boolean isNetworkEnabled = false;

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
                }

                isGPSEnabled = locationManager
                        .isProviderEnabled(LocationManager.GPS_PROVIDER);

                isNetworkEnabled = Build.FINGERPRINT.contains("generic") ? true : locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


                if (!isGPSEnabled && !isNetworkEnabled) {
                    showSettingsAlert();
                } else {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        callingPermissionLocation();
                    } else {
                        callingLocationFunctionalities();
                    }
                }

            } catch (Exception e) {
                Logger.printException(e);
            }
        }
    }
    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(context);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status

            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            // Display an error dialog
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode,
                    (Activity) context, 0);
            dialog.show();

            return false;
        }
    }
    private void callingPermissionLocation() {

        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle(getString(R.string.app_name));
            alertBuilder.setMessage(getString(R.string.str_location_permission));
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions((Activity) context,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }
    private void callingLocationFunctionalities() {


        LocationServices.FusedLocationApi.requestLocationUpdates(
                locationClient, locationRequest, locationListener);

        final Toast tag = Toast.makeText(context,
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
                        (Activity) context,
                        this
                                .getString(R.string.textPleaseWaitLable),
                        this
                                .getString(R.string.textFetchingLocation),
                        false);
    }
    public void stopUsingGPS() {

        if ((locationClient != null) && (locationClient.isConnected())) {
            // locationClient.removeLocationUpdates(locationListener);
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    locationClient, locationListener);
        }
    }
    /**
     * The location listener.
     */
    LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {


            stopUsingGPS();
            latitude = location.getLatitude();
            longitude = location.getLongitude();

            insertIntoGpsStepsIntervention();

            myTimer.cancel();
            DialogUtils.dismissProgressDialog();
        }

    };

    private void insertIntoGpsStepsIntervention() {
        cal.add(Calendar.SECOND, -1);
        SimpleDateFormat dateFormat = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            dateFormat = new SimpleDateFormat("YYYYMMDD");
        }

        String currentDateStr = dateFormat.format(cal.getTime());

        int dateIndex = Integer.parseInt(currentDateStr);

        String lat = latitude+"";
        String lng = longitude+"";
        daoTracking.insertJobInGpsSteps(idJobCreated,lat,lng,gpsEvent,dateIndex);
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(getString(R.string.textAlertLable) + "!");
        alertDialog.setMessage(getString(R.string.textEnableLocationService));
        alertDialog.setPositiveButton(R.string.textYesLable,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                        callingLocationFunctionalities();
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
    private void initializingForLocation() {
        locationClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        locationClient.connect();

        locationRequest = LocationRequest.create();

        locationRequest
                .setInterval(SynchroteamUitls.UPDATE_INTERVAL_IN_MILLISECONDS);

        // Use high accuracy
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Set the interval ceiling to one minute
        locationRequest
                .setFastestInterval(SynchroteamUitls.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

    }
}