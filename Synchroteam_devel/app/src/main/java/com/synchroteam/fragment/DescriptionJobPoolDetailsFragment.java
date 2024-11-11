package com.synchroteam.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.Description;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.CallingOptionDialog;
import com.synchroteam.synchroteam.JobPoolDetails;
import com.synchroteam.synchroteam3.R;

import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.PackageInstallationUtil;
import com.synchroteam.utils.SynchroteamUitls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;
import io.michaelrocks.libphonenumber.android.Phonenumber;

public class DescriptionJobPoolDetailsFragment extends Fragment {

    /**
     * The job details.
     */
    private JobPoolDetails jobPoolDetails;

    /**
     * The id interv.
     */
    private String idInterv;

    /** The cd status. */
    // private String cdStatus;

    /**
     * The nom site.
     */
    private String nomSite;

    /**
     * The nom equipement.
     */
    private String nomEquipement;

    /**
     * The linear.
     */
    private View linear;

    /** The id site. */
    // private int idSite;

    /**
     * The data access object.
     */
    private Dao dataAccessObject;

    /**
     * Font awesome icons tc.
     */
    private android.widget.TextView cloudClientIconIv, locationSiteIv, locationEquipmentIv,
            cloudInterventionIconIv, locationIconIv;

    /**
     * The txt equipement.
     */
    private TextView txtClient, txtAdresse, txtType, txtModele, txtNumInterv,
            txtSite, txtEquipement, txtJobManager;

    /**
     * The technician tv.
     */
    private TextView phoneNumberTv, mobileNumberTv, technicianTv,
            additionalAddressDataTv;

    /**
     * The description.
     */
    private Description description;

    /**
     * The alert dialog view3.
     */
    private View alertDialogView3;

    /**
     * The adr_globale.
     */
    private String lat = "0", lon = "0", adr_globale;

    /**
     * The message icon iv.
     */
    private ImageView phoneIconIv, messageIconIv;

    private String callPhone = "";

    /**
     * The conatainer additional address.
     */
    private LinearLayout conatainerAdditionalAddress, containerClientEmail,
            containerCustomerName, containerCustomerSite, containerEquipmentSite, containerAddress,
            containerNumber, containerJobInformation,linManagerTechContainer,
            containerMobile,containerPhone;

    /**
     * The txtcontact.
     */
    private TextView txtcontact, clientEmailDataTv;

    /**
     * The txt description.
     */
    private TextView txtDescription;

    /**
     * The ma list view perso.
     */
    private ListView maListViewPerso;

    /**
     * The user.
     */
    private User user;

    /**
     * The id user.
     */
    private String idUser;

    private int idSite;

    private int idEquipment;

    /**
     * The view.
     */
    private View view;

    private ImageView emailClientIconIv;

    /**
     * The gestion acces.
     */
    private GestionAcces gestionAcces;

    /**
     * The mobile icon iv.
     */
    private ImageView mobileIconIv;

    private String publicLinkIntervention, publicLinkClient, publicLinkSite, publicLinkEquipment;

    private String modeleIntervention;

    // private static final String TAG = "DescriptionDetailsFragment";
    private PhoneNumberUtil phoneFormatUtil = null;

    public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 125;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jobPoolDetails = (JobPoolDetails) getActivity();
        idInterv = getArguments().getString("id_interv");
        nomSite = getArguments().getString("site_name");
        nomEquipement = getArguments().getString("equp_name");
        dataAccessObject = DaoManager.getInstance();
        gestionAcces = dataAccessObject.getAcces();
        lat = getArguments().getString("lat");
        lon = getArguments().getString("lon");
        idUser = getArguments().getString("id_user");
        idSite = getArguments().getInt("idSite");
        idEquipment = getArguments().getInt("idEquipement");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_fragment_details,
                container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            if (phoneFormatUtil == null) {
                phoneFormatUtil = PhoneNumberUtil.createInstance(jobPoolDetails);
            }
        initiateView(view);
        setDataToView(view);
        this.view = view;
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public void initiateView(View v) {
        phoneIconIv = (ImageView) v.findViewById(R.id.phoneIconIv);
        mobileIconIv = (ImageView) v.findViewById(R.id.mobileIconIv);
        messageIconIv = (ImageView) v.findViewById(R.id.messageIconIv);
        locationIconIv = (android.widget.TextView) v
                .findViewById(R.id.locationIconIv);

        conatainerAdditionalAddress = (LinearLayout) v
                .findViewById(R.id.conatainerAdditionalAddress);
        containerJobInformation = (LinearLayout) v
                .findViewById(R.id.containerJobInformation);
        cloudClientIconIv = (android.widget.TextView) v
                .findViewById(R.id.cloudClientIconIv);
        cloudInterventionIconIv = (android.widget.TextView) v
                .findViewById(R.id.cloudInterventionIconIv);
        locationSiteIv = (android.widget.TextView) v
                .findViewById(R.id.locationSiteIv);
        locationEquipmentIv = (android.widget.TextView) v
                .findViewById(R.id.locationEquipmentIv);

        linManagerTechContainer=(LinearLayout) v
                .findViewById(R.id.linManagerTechContainer);

        locationIconIv.setOnClickListener(onClickListener);
        phoneIconIv.setOnClickListener(onClickListener);
        mobileIconIv.setOnClickListener(onClickListener);
        messageIconIv.setOnClickListener(onClickListener);
        locationSiteIv.setOnClickListener(onClickListener);
        locationEquipmentIv.setOnClickListener(onClickListener);
        cloudClientIconIv.setOnClickListener(onClickListener);
        cloudInterventionIconIv.setOnClickListener(onClickListener);

        emailClientIconIv = (ImageView) v.findViewById(R.id.emailClientIconIv);
        emailClientIconIv.setOnClickListener(onClickListener);

        clientEmailDataTv = (TextView) v.findViewById(R.id.clientEmailDataTv);

        additionalAddressDataTv = (TextView) v
                .findViewById(R.id.additionalAddressDataTv);

        Typeface fontAwesome = Typeface.createFromAsset(jobPoolDetails.getAssets(),
                jobPoolDetails.getString(R.string.fontName_fontAwesome));
        // txtDateic.setTypeface(fontAwesome);
        // txtStartic.setTypeface(fontAwesome);
        // txtEndic.setTypeface(fontAwesome);
        // txtPlannedic.setTypeface(fontAwesome);
        cloudClientIconIv.setTypeface(fontAwesome);
        locationSiteIv.setTypeface(fontAwesome);
        locationEquipmentIv.setTypeface(fontAwesome);
        cloudInterventionIconIv.setTypeface(fontAwesome);
        locationIconIv.setTypeface(fontAwesome);

        containerCustomerName = (LinearLayout) v
                .findViewById(R.id.containerClientName);
        containerCustomerSite = (LinearLayout) v
                .findViewById(R.id.containerSite);
        containerEquipmentSite = (LinearLayout) v
                .findViewById(R.id.containerJobEquipments);
        containerAddress = (LinearLayout) v
                .findViewById(R.id.conatainerAddress);
        containerNumber = (LinearLayout) v.findViewById(R.id.containerNumber);

        containerClientEmail = (LinearLayout) v
                .findViewById(R.id.containerClientEmail);
        containerMobile = (LinearLayout) v.findViewById(R.id.containerNumber);
        containerPhone = (LinearLayout) v.findViewById(R.id.containerPhone);

        if (dataAccessObject.getAcces() != null
                && dataAccessObject.getAcces().getOptionHelpSurfing() == 0) {
            locationIconIv.setVisibility(View.GONE);
            containerAddress.setEnabled(false);
        }

        /*
         * New changes...
         *
         * If lat & long is null, then hide the arrow button and disable the
         * click for containerAddress.
         */
        // if (TextUtils.isEmpty(lat) && TextUtils.isEmpty(lon)) {
        // locationIconIv.setVisibility(View.GONE);
        // containerAddress.setEnabled(false);
        // } else {
        // locationIconIv.setVisibility(View.VISIBLE);
        // containerAddress.setEnabled(true);
        // }

        containerCustomerName.setOnClickListener(onClickListener);
        containerCustomerSite.setOnClickListener(onClickListener);
        containerAddress.setOnClickListener(onClickListener);
        containerNumber.setOnClickListener(onClickListener);
        containerJobInformation.setOnClickListener(onClickListener);
        containerMobile.setOnClickListener(onClickListener);
        containerClientEmail.setOnClickListener(onClickListener);
        containerPhone.setOnClickListener(onClickListener);

    }

    /**
     * Sets the data to view.
     *
     * @param view the new data to view
     */
    private void setDataToView(View view) {
        // TODO Auto-generated method stub

        description = dataAccessObject.getInterventionById(idInterv);
        modeleIntervention = dataAccessObject.getModelReportNameById(idInterv);
        user = dataAccessObject.getUser();

        // jobTypeTv.setText(description.getTypeIntervention());
        // jobInformationTv.setText(description.getDescriptionIntervention());
        // if(!TextUtils.isEmpty(nomEquipement)){
        // equipmentTv.setText(nomEquipement);
        // }
        // else{
        // containerEquipments.setVisibility(View.GONE);
        // }

        if (description != null) {
            Logger.log("PRe Nom", user.getPrenom());
            Logger.log("DIscriptionJobDetail>>>>", description.getNomClient());
        }

        // if(!dateMeeting.equals(null) && !dateMeeting.equals("")){
        //
        //
        // LinearLayout lnM=(LinearLayout)
        // view.findViewById(R.id.containerMeeting);
        //
        // TextView txtM=(TextView) view.findViewById(R.id.jobMeetingDataTv);
        // view.findViewById(R.id.imageViewMeeting).setVisibility(View.GONE);
        // lnM.setVisibility(0);
        //
        // txtM.setText(dateMeeting);
        // }

        if (description != null) {

            verifierClient(view);// client
            verifierEquipement(view);// equipment
            verifierContact(view);// contact
            verifierAdresse(view);// address
            verifierAdditionalAddress(view);
            verifierNumInterv(view);// jobnumber
            verifierType(view);// typejobtype
            verifierDescription(view);// job discription
            verifierPhone(view);// job phone
            verifierMobile(view);// job mobile
            verifierTechnician(view);// job mobile
            verifierSite(view);
            verifierJobManager(view);
            veriferPublicLinks();
            verifierEmail(view);
        }

        if (modeleIntervention != null && !TextUtils.isEmpty(modeleIntervention)) {
            verifierModele(view);// job template
        }

        linManagerTechContainer.setVisibility(View.GONE);

    }
    /**
     * To check if the technician is subcontractor
     *
     * @return result
     */
    private boolean isSubContractor() {
        boolean result = false;
        int idProfil = user.getIdProfil();
        int flSubContractor = user.getFlSubContractor();
        if (idProfil == 4 && flSubContractor == 1) {
            result = true;
        }
        return result;
    }
    private void veriferPublicLinks() {
        // TODO Auto-generated method stub
        boolean isSubContr = isSubContractor();
        if (!isSubContr) {
            publicLinkClient = description.getPublicLinkClient();
            publicLinkIntervention = description.getPublicLinkInterv();
            publicLinkSite = dataAccessObject.getSitePublicLink(idSite);
            publicLinkEquipment = dataAccessObject.getEquipmentPublicLink(idEquipment);

            if (!TextUtils.isEmpty(publicLinkClient)
                    && !publicLinkClient.matches("^\\s*$")) {
                cloudClientIconIv.setVisibility(View.VISIBLE);
            } else {
                cloudClientIconIv.setVisibility(View.INVISIBLE);
            }

            if (!TextUtils.isEmpty(publicLinkIntervention)) {
                cloudInterventionIconIv.setVisibility(View.VISIBLE);

            } else {
                cloudInterventionIconIv.setVisibility(View.GONE);
            }

            /*
             * if the public link is null, then hide the arrow button (>) in the
             * customer and disable the click listener for the container & arrow
             * icon
             */
            if (TextUtils.isEmpty(publicLinkClient)) {
                cloudClientIconIv.setVisibility(View.INVISIBLE);
                containerCustomerName.setOnClickListener(null);
                cloudClientIconIv.setOnClickListener(null);
            }

            if (TextUtils.isEmpty(publicLinkIntervention)) {
                containerNumber.setOnClickListener(null);
                cloudInterventionIconIv.setOnClickListener(null);
            }

            if (publicLinkSite != null && !TextUtils.isEmpty(publicLinkSite)) {
                locationSiteIv.setVisibility(View.VISIBLE);
                containerCustomerSite.setOnClickListener(onClickListener);
            } else {
                locationSiteIv.setVisibility(View.GONE);
                containerCustomerSite.setOnClickListener(null);
            }

            if (publicLinkEquipment != null && !TextUtils.isEmpty(publicLinkEquipment)) {
                locationEquipmentIv.setVisibility(View.VISIBLE);
                containerEquipmentSite.setOnClickListener(onClickListener);
            } else {
                locationEquipmentIv.setVisibility(View.GONE);
                containerEquipmentSite.setOnClickListener(null);
            }
        }else {
            cloudClientIconIv.setVisibility(View.INVISIBLE);
            cloudInterventionIconIv.setVisibility(View.GONE);
            locationSiteIv.setVisibility(View.GONE);
            locationEquipmentIv.setVisibility(View.GONE);
        }
    }

    /**
     * Verifier job manager.
     *
     * @param view the view
     */
    private void verifierJobManager(View view) {
        // TODO Auto-generated method stub

        txtJobManager = (TextView) view.findViewById(R.id.jobManagerDataTv);
        int userIdofUser = user.getId();

        if ((userIdofUser + "").equals(idUser)) {
            txtJobManager.setText(jobPoolDetails.getString(R.string.textYesLable));
        } else {
            txtJobManager.setText(jobPoolDetails.getString(R.string.textNoLable));
        }

    }

    /**
     * Verifier site.
     *
     * @param view the view
     */
    private void verifierSite(View view) {
        // TODO Auto-generated method stub
        if (!TextUtils.isEmpty(nomSite) && !nomSite.matches("^\\s*$")) {

            view.findViewById(R.id.containerSite).setVisibility(View.VISIBLE);
            txtSite = (TextView) view.findViewById(R.id.siteDataTv);
            txtSite.setText(nomSite);
            if (gestionAcces != null
                    && gestionAcces.getOptionHelpSurfing() == 0) {
                view.findViewById(R.id.locationSiteIv).setVisibility(View.GONE);
                containerCustomerSite.setEnabled(false);
            } else {
                view.findViewById(R.id.locationSiteIv).setVisibility(
                        View.VISIBLE);
                containerCustomerSite.setEnabled(true);
            }

        } else {
            view.findViewById(R.id.containerSite).setVisibility(View.GONE);
        }
    }

    /**
     * Verifier additional address.
     *
     * @param view the view
     */
    private void verifierAdditionalAddress(View view) {
        // TODO Auto-generated method stub
        if (!TextUtils.isEmpty(description.getAdresseComplement())
                && !description.getAdresseComplement().matches("^\\s*$")) {
            conatainerAdditionalAddress.setVisibility(View.VISIBLE);
            additionalAddressDataTv.setText(description.getAdresseComplement());
        } else {
            conatainerAdditionalAddress.setVisibility(View.GONE);
        }
    }

    /**
     * Verifier technician.
     *
     * @param view the view
     */
    private void verifierTechnician(View view) {
        if (!TextUtils.isEmpty(dataAccessObject.getUser().getNom())
                && !dataAccessObject.getUser().getNom().matches("^\\s*$")) {
            technicianTv = (TextView) view
                    .findViewById(R.id.jobTechnicianeDataTv);
            /*
             * technicianTv.setText(dataAccessObject.getUser().getNom() + " " +
             * dataAccessObject.getUser().getPrenom());
             */
            technicianTv.setText(dataAccessObject.getUser().getPrenom() + " "
                    + dataAccessObject.getUser().getNom());

        } else {
            linear = view.findViewById(R.id.containerTechnician);
            linear.setVisibility(8);
        }
    }

    /**
     * Verifier mobile.
     *
     * @param view the view
     */
    @SuppressLint("WrongConstant")
    private void verifierMobile(View view) {
        if (!TextUtils.isEmpty(description.getContactMobile())
                && !description.getContactMobile().matches("^\\s*$")) {
            mobileNumberTv = (TextView) view.findViewById(R.id.jobMobileDataTv);
            mobileNumberTv.setText(formatPhoneNumber(description.getContactMobile()));
        } else {
            linear = view.findViewById(R.id.containerMobile);
            // view.findViewById(R.id.dividerLineMobile).setVisibility(View.GONE);
            linear.setVisibility(8);
        }
    }

    public String formatPhoneNumber(String phoneNo) {
        String updatedPhoneNo = "";
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            updatedPhoneNo = phoneNo;
        } else {
            if (phoneNo.trim().startsWith("+") && !phoneNo.trim().contains(" ")) {
                if (phoneFormatUtil == null) {
                    phoneFormatUtil = PhoneNumberUtil.createInstance(jobPoolDetails);
                }
                try {
                    // phone must begin with '+'
                    Phonenumber.PhoneNumber numberProto = phoneFormatUtil.parse(phoneNo, "");
                    int countryCode = numberProto.getCountryCode();
                    long nationalNo = numberProto.getNationalNumber();


//                    updatedPhoneNo=numberProto.toString();
                    updatedPhoneNo = phoneFormatUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
                } catch (Exception e) {
                    updatedPhoneNo = phoneNo;
                }


            } else {
                updatedPhoneNo = phoneNo;
            }
        }
        return updatedPhoneNo;
    }

    /**
     * Verifier phone.
     *
     * @param view the view
     */
    private void verifierPhone(View view) {
        if (!TextUtils.isEmpty(description.getTelContact())
                && !description.getTelContact().matches("^\\s*$")) {
            phoneNumberTv = (TextView) view.findViewById(R.id.jobPhoneDataTv);
            phoneNumberTv.setText(formatPhoneNumber(description.getTelContact()));
        } else {
            linear = view.findViewById(R.id.containerPhone);
            // view.findViewById(R.id.dividerLinePhone).setVisibility(View.GONE);
            linear.setVisibility(8);
        }
    }

    /**
     * Verifier description.
     *
     * @param view the view
     */
    private void verifierDescription(View view) {
        if (!TextUtils.isEmpty(description.getDescriptionIntervention())
                && !description.getDescriptionIntervention().matches("^\\s*$")) {
            txtDescription = (TextView) view
                    .findViewById(R.id.dataJobInformationTv);
            txtDescription.setText(description.getDescriptionIntervention());
            txtDescription.setOnClickListener(onClickListener);

        } else {
            linear = view.findViewById(R.id.containerJobInformation);
            linear.setVisibility(8);
        }
    }

    /**
     * Verifier modele.
     *
     * @param view the view
     */
    private void verifierModele(View view) {
        if (!TextUtils.isEmpty(modeleIntervention)
                && !modeleIntervention.matches("^\\s*$")) {
            txtModele = (TextView) view
                    .findViewById(R.id.jobReportTemplateDataTv);
            txtModele.setText(modeleIntervention);
        } else {
            linear = view.findViewById(R.id.containerReportsTemplate);
            linear.setVisibility(8);
        }
    }

    /**
     * Verifier type.
     *
     * @param view the view
     */
    private void verifierType(View view) {
        if (!TextUtils.isEmpty(description.getTypeIntervention())
                && !description.getTypeIntervention().matches("^\\s*$")) {
            txtType = (TextView) view.findViewById(R.id.jobTypeDataTv);
            txtType.setText(description.getTypeIntervention());
        } else {
            linear = view.findViewById(R.id.containerJobType);
            linear.setVisibility(8);
        }

    }

    /**
     * Verifier adresse.
     *
     * @param view the view
     */
    private void verifierAdresse(View view) {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
        if (!TextUtils.isEmpty(description.getAdresseGlobale())
                && !description.getAdresseGlobale().matches("^\\s*$")) {
            txtAdresse = (TextView) view.findViewById(R.id.dataJobAddressTv);
            txtAdresse.setText(description.getAdresseGlobale().trim());
            if (gestionAcces != null
                    && gestionAcces.getOptionHelpSurfing() == 0) {
                view.findViewById(R.id.locationIconIv).setVisibility(View.GONE);
                containerAddress.setEnabled(false);
            } else {
                if (!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lon)) {
                    view.findViewById(R.id.locationIconIv).setVisibility(
                            View.VISIBLE);
                    containerAddress.setEnabled(true);
                } else {
                    view.findViewById(R.id.locationIconIv).setVisibility(
                            View.GONE);
                    containerAddress.setEnabled(false);
                }
            }

            /*
             * New changes...
             *
             * If lat & long is null, then hide the arrow button and disable the
             * click for containerAddress.
             */
            // if (TextUtils.isEmpty(lat) && TextUtils.isEmpty(lon)) {
            // locationIconIv.setVisibility(View.GONE);
            // containerAddress.setEnabled(false);
            // } else {
            // locationIconIv.setVisibility(View.VISIBLE);
            // containerAddress.setEnabled(true);
            // }

        } else {
            linear = view.findViewById(R.id.conatainerAddress);
            linear.setVisibility(View.GONE);
        }
    }

    /**
     * Verifier num interv.
     *
     * @param view the view
     */
    private void verifierNumInterv(View view) {
        txtNumInterv = (TextView) view.findViewById(R.id.jobNumberDataTv);
        if (!TextUtils.isEmpty(description.getRefCustomer())
                && !description.getRefCustomer().matches("^\\s*$"))
            txtNumInterv.setText("" + description.getRefCustomer());
        else if (description.getNoInterv() != 0)
            txtNumInterv.setText("#" + description.getNoInterv());
        else {
            linear = view.findViewById(R.id.containerNumber);
            linear.setVisibility(View.GONE);

        }
    }

    /**
     * Verifier contact.
     *
     * @param view the view
     */
    private void verifierContact(View view) {
        if ((!TextUtils.isEmpty(description.getNomContact()) && !description
                .getNomContact().matches("^\\s*$"))
                || (!TextUtils.isEmpty(description.getPreNomContact()) && !description
                .getPreNomContact().matches("^\\s*$"))) {
            txtcontact = (TextView) view.findViewById(R.id.jobContactDataTv);
            txtcontact.setText(description.getPreNomContact() + " "
                    + description.getNomContact().trim());
        } else {
            linear = view.findViewById(R.id.containerContact);
            linear.setVisibility(View.GONE);
        }
    }

    /**
     * Verifier equipement.
     *
     * @param view the view
     */
    private void verifierEquipement(View view) {
        // TODO Auto-generated method stub
        if (!TextUtils.isEmpty(nomEquipement)
                && !nomEquipement.matches("^\\s*$")) {
            txtEquipement = (TextView) view.findViewById(R.id.jobEquipmentsTv);
            txtEquipement.setText(nomEquipement);
        } else {
            linear = view.findViewById(R.id.containerJobEquipments);
            linear.setVisibility(View.GONE);
        }
    }

    /**
     * Verifier client.
     *
     * @param view the view
     */
    private void verifierClient(View view) {
        if (!TextUtils.isEmpty(description.getNomClient())
                && !description.getNomClient().matches("^\\s*$")) {
            txtClient = (TextView) view.findViewById(R.id.clientNameDataTv);
            txtClient.setText(description.getNomClient());
        } else {
            linear = view.findViewById(R.id.containerClientName);
            linear.setVisibility(View.GONE);
        }

    }

    private void verifierEmail(View view) {

        if (!TextUtils.isEmpty(description.getEmailContact())
                && !description.getEmailContact().matches("^\\s*$")) {
            // clientEmailDataTv = (TextView)
            // view.findViewById(R.id.clientNameDataTv);
            clientEmailDataTv.setText(description.getEmailContact());
        } else {
            containerClientEmail = (LinearLayout) view
                    .findViewById(R.id.containerClientEmail);
            containerClientEmail.setVisibility(View.GONE);
        }

    }

    /**
     * The on click listener.
     */
    OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.messageIconIv) {
                Intent email = new Intent(Intent.ACTION_SEND);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{""});
                email.putExtra(Intent.EXTRA_SUBJECT, "");
                email.putExtra(Intent.EXTRA_TEXT, "");
                email.setType("message/rfc822");
                jobPoolDetails.startActivity(Intent.createChooser(email,
                        "Complete Action Using"));

            } else if (id == R.id.emailClientIconIv||id == R.id.containerClientEmail) {
                if (!TextUtils.isEmpty(description.getEmailContact())) {
                    Intent email = new Intent(Intent.ACTION_SEND);
                    String emailAddress = description.getEmailContact().trim();
                    email.putExtra(Intent.EXTRA_EMAIL,
                            new String[]{emailAddress});
                    // email.putExtra(Intent.EXTRA_SUBJECT, "subject");
                    // email.putExtra(Intent.EXTRA_TEXT, "message");
                    email.setType("message/rfc822");
                    jobPoolDetails.startActivity(Intent.createChooser(email,
                            "Choose an Email client :"));
                }

            } else if (id == R.id.phoneIconIv ||id == R.id.containerPhone) {

                if (!TextUtils.isEmpty(description.getTelContact())) {
                    String phoneNo = description.getTelContact().trim();
                    callPhone = phoneNo;
                    boolean installed = PackageInstallationUtil.whatsAppInstalledOrNot("com.whatsapp",jobPoolDetails);
                    if(installed) {
                        CallingOptionDialog callingOptionDialog = new CallingOptionDialog(jobPoolDetails,phoneNo);
                        callingOptionDialog.show();
                    }else{
                        if (ContextCompat.checkSelfPermission(jobPoolDetails, Manifest.permission.CALL_PHONE)
                                != PackageManager.PERMISSION_GRANTED) {

                            callingPermissionPhone();
                        } else {
                            callingMethod(phoneNo);
                        }
                    }
                }

            } else if (id == R.id.mobileIconIv || id == R.id.containerMobile) {

                String phoneNo = description.getContactMobile().trim();
                callPhone = phoneNo;
//                Intent callIntent = new Intent(Intent.ACTION_CALL);
//                callIntent.setData(Uri.parse("tel:" + phoneNo));
//                jobPoolDetails.startActivity(callIntent);
                boolean installed = PackageInstallationUtil.whatsAppInstalledOrNot("com.whatsapp",jobPoolDetails);
                if(installed) {
                    CallingOptionDialog callingOptionDialog = new CallingOptionDialog(jobPoolDetails,phoneNo);
                    callingOptionDialog.show();
                }else{
                    if (ContextCompat.checkSelfPermission(jobPoolDetails, Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {

                        callingPermissionPhone();
                    } else {
                        callingMethod(phoneNo);
                    }
                }

            } else if (id == R.id.conatainerAddress
                    || id == R.id.locationIconIv) {
                openMaps();
            } else if (id == R.id.containerSite || id == R.id.locationSiteIv) {
                openLinkInBrowser(publicLinkSite);
            } else if (id == R.id.containerClientName
                    || id == R.id.cloudClientIconIv) {
                openLinkInBrowser(publicLinkClient);
            } else if (id == R.id.containerJobEquipments
                    || id == R.id.locationEquipmentIv) {
                openLinkInBrowser(publicLinkEquipment);
            } else if (id == R.id.containerNumber
                    || id == R.id.cloudInterventionIconIv) {

                openLinkInBrowser(publicLinkIntervention);

            } else if (id == R.id.containerJobInformation) {
                copyJobDescription();
            }

        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(jobPoolDetails, Manifest.permission.CALL_PHONE)
                            == PackageManager.PERMISSION_GRANTED)
                        if (callPhone.trim().length() > 0)
                            callingMethod(callPhone);
                } else {

                }
                return;
            }

        }
    }


    private void callingPermissionPhone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(jobPoolDetails,
                Manifest.permission.CALL_PHONE)) {


            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(jobPoolDetails);
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle(getString(R.string.app_name));
            alertBuilder.setMessage(getString(R.string.str_phone_permission));
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(jobPoolDetails,
                            new String[]{Manifest.permission.CALL_PHONE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE);
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();

        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(jobPoolDetails,
                    new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
        }
    }

    @SuppressLint("MissingPermission")
    private void callingMethod(String phoneNo) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phoneNo));
        jobPoolDetails.startActivity(callIntent);
    }

    /**
     * For copying the job description on click to the clipboard
     */
    private void copyJobDescription() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) jobPoolDetails.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(txtDescription.getText().toString());
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) jobPoolDetails.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", txtDescription.getText().toString());
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(jobPoolDetails, "Text Copied to clipboard",
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Checks if is intent available.
     *
     * @param context the context
     * @param action  the action
     * @return true, if is intent available
     */
    public static boolean isIntentAvailable(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    /***
     * Create a chooser of browsers to open the link
     *
     * @param link
     */
    protected void openLinkInBrowser(String link) {
        // TODO Auto-generated method stub
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(link));

        // Always use string resources for UI text. This says something like
        // "Share this photo with"
        String title = jobPoolDetails.getString(R.string.titleBrowserSelection);
        // Create and start the chooser
        Intent chooser = Intent.createChooser(intent, title);
        jobPoolDetails.startActivity(chooser);
    }

    /**
     * Open maps.
     */
    public void openMaps() {

        if (isIntentAvailable(jobPoolDetails,
                "android.intent.action.navigon.START_PUBLIC")) {
            AlertDialog alert = new myCustomAlertDialog(jobPoolDetails);
            alert.show();
        } else {
            if (!SynchroteamUitls.isGoogleMapInstalled(jobPoolDetails)) {
                SynchroteamUitls.showGoogleMapsDialog(jobPoolDetails);

                return;
            }

            try {
                if ((!TextUtils.isEmpty(lat)) && (!TextUtils.isEmpty(lon))) {
                    Intent myIntent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("geo:0,0?q=" + lat + "," + lon + "&z=20"));
                    jobPoolDetails.startActivity(myIntent);
                } else {
                    Intent myIntent = new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("geo:0,0?q=" + adr_globale + "&z=20"));
                    jobPoolDetails.startActivity(myIntent);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Logger.printException(e);
            }
        }

    }

    /**
     * The Class myCustomAlertDialog.
     */
    private class myCustomAlertDialog extends AlertDialog {

        /**
         * Instantiates a new my custom alert dialog.
         *
         * @param context the context
         */
        protected myCustomAlertDialog(Context context) {
            super(context);

            LayoutInflater factory = LayoutInflater.from(context);
            alertDialogView3 = factory.inflate(R.layout.nav_list, null);

            maListViewPerso = (ListView) alertDialogView3
                    .findViewById(R.id.list_view_navigation);
            maListViewPerso.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> a, View v, int position,
                                        long id) {

                    if (position == 0) {
                        try {
                            if ((!TextUtils.isEmpty(lat))
                                    && (!TextUtils.isEmpty(lon))) {
                                jobPoolDetails.startActivity(getMapsIntent(
                                        jobPoolDetails, "geo:0,0?q=" + lat + ","
                                                + lon + "&z=16"));
                            } else {
                                jobPoolDetails.startActivity(getMapsIntent(
                                        jobPoolDetails, "geo:0,0?q=" + adr_globale
                                                + "&z=16"));
                            }
                            // dismiss();
                        } catch (Exception e) {
                            Logger.printException(e);
                        } finally {
                            dismiss();
                        }

                    } else {
                        try {
                            if ((!TextUtils.isEmpty(lat))
                                    && (!TextUtils.isEmpty(lon))) {
                                String INTENT_EXTRA_KEY_LATITUDE = "latitude";
                                String INTENT_EXTRA_KEY_LONGITUDE = "longitude";
                                Intent myIntent = new Intent(
                                        "android.intent.action.navigon.START_PUBLIC");
                                myIntent.putExtra(INTENT_EXTRA_KEY_LATITUDE,
                                        Float.valueOf(lat));
                                myIntent.putExtra(INTENT_EXTRA_KEY_LONGITUDE,
                                        Float.valueOf(lon));
                                jobPoolDetails.startActivity(myIntent);
                            } else {
                                String INTENT_EXTRA_KEY_FREE_TEXT_ADDRESS = "free_text_address";
                                Intent myIntent = new Intent(
                                        "android.intent.action.navigon.START_PUBLIC");
                                myIntent.putExtra(
                                        INTENT_EXTRA_KEY_FREE_TEXT_ADDRESS,
                                        adr_globale);
                                jobPoolDetails.startActivity(myIntent);
                            }
                            // dismiss();
                        } catch (Exception e) {
                            Logger.printException(e);
                        } finally {
                            dismiss();
                        }

                    }
                }
            });
            fillNavList();
            setView(alertDialogView3);
            setTitle(R.string.naviguer_par);
            setIcon(android.R.drawable.ic_dialog_info);

        }

    }

    /**
     * Fill nav list.
     */
    public void fillNavList() {

        ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> map;

        map = new HashMap<String, String>();
        map.put("item", "Google Maps");
        map.put("img", String.valueOf(R.drawable.maps));
        listItem.add(map);

        map = new HashMap<String, String>();
        map.put("item", "Navigon");
        map.put("img", String.valueOf(R.drawable.navig));
        listItem.add(map);

        SimpleAdapter adapter = new SimpleAdapter(
                alertDialogView3.getContext(), listItem,
                R.layout.nav_list_item, new String[]{"img", "item"},
                new int[]{R.id.imgNav, R.id.textNav});

        maListViewPerso.setAdapter(adapter);
    }

    /**
     * Gets the maps intent.
     *
     * @param context the context
     * @param url     the url
     * @return the maps intent
     */
    public static Intent getMapsIntent(Context context, String url) {
        Intent videoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        final PackageManager pm = context.getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(videoIntent,
                0);
        for (int i = 0; i < activityList.size(); i++) {
            ResolveInfo app = activityList.get(i);
            if (app.activityInfo.name.contains("MapsActivity")) {
                videoIntent.setClassName(app.activityInfo.packageName,
                        app.activityInfo.name);
                return videoIntent;
            }
        }
        return videoIntent;
    }

    /**
     * Update ui.
     */
    public void updateUi() {

        setDataToView(view);

    }

    /**
     * Update ui.
     */
    public void updateUiAfterSync() {
        gestionAcces = dataAccessObject.getAcces();
        if (gestionAcces != null && gestionAcces.getOptionHelpSurfing() == 0) {
            view.findViewById(R.id.locationIconIv).setVisibility(View.GONE);
            view.findViewById(R.id.locationSiteIv).setVisibility(View.GONE);

            containerAddress.setEnabled(false);
            containerCustomerSite.setEnabled(false);
        } else {

            if (!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lon)) {
                view.findViewById(R.id.locationIconIv).setVisibility(
                        View.VISIBLE);
                containerAddress.setEnabled(true);
            } else {
                view.findViewById(R.id.locationIconIv).setVisibility(View.GONE);
                containerAddress.setEnabled(false);
            }

            view.findViewById(R.id.locationSiteIv).setVisibility(View.VISIBLE);

            containerCustomerSite.setEnabled(true);

        }
        /*
         * New changes...
         *
         * If lat & long is null, then hide the arrow button and disable the
         * click for containerAddress.
         */
        // if (TextUtils.isEmpty(lat) && TextUtils.isEmpty(lon)) {
        // locationIconIv.setVisibility(View.GONE);
        // containerAddress.setEnabled(false);
        // } else {
        // locationIconIv.setVisibility(View.VISIBLE);
        // containerAddress.setEnabled(true);
        // }

        Description descriptionPl = dataAccessObject
                .getPublicLinksById(idInterv);
        description.setPublicLinkIntervention(descriptionPl
                .getPublicLinkInterv());
        description.setPublicLinkClient(descriptionPl.getPublicLinkClient());
        veriferPublicLinks();

    }
}
