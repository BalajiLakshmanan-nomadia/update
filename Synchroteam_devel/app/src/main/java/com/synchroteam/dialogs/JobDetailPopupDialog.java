package com.synchroteam.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.Description;
import com.synchroteam.dao.Dao;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.KEYS;

public class JobDetailPopupDialog extends DialogFragment {

    private TextView clientName;
    private TextView siteName;
    private TextView equipmentName;
    private TextView address;
    private TextView additionalAddress;
    private TextView jobType;
    private TextView description;
    private TextView firstName;
    private TextView number;
    private LinearLayout closeBtn;
    private LinearLayout openBtn;
    private View site_border,equipment_border;

    Dao dao;

    static String _clientName = "_clientName";
    static String _siteName = "_siteName";
    static String _equipmentName = "_equipmentName";
    static  String _address = "_address";
    static String _additionalAddress = "_additionalAddress";
    static String _jobType = "_jobType";
    static String _description = "_description";
    static String _number = "_number";
    static String _clientId = "_clientId";

    static String _jobId = "_jobId";

    String clientFirstName;
    String clientLastName;
    static JobDetailsNavigationInterface jobDetailsNavigationInterface;

    private Description jobDescription;

    public interface JobDetailsNavigationInterface{
        void open();
        void close();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_job_details_popup, container);
        initialView(view);
        setData();
        closeBtn.setOnClickListener(v -> {
            dismiss();
//                jobDetailsNavigationInterface.close();
        });
        openBtn.setOnClickListener(v -> jobDetailsNavigationInterface.open());
        return view;
    }

    @SuppressLint("SetTextI18n")
    private void setData() {

        Bundle bundle = getArguments();

        if(bundle!=null){

            jobDescription = dao.getInterventionById( bundle.getString(_jobId));
            clientName.setText((CharSequence) bundle.get(_clientName));
            address.setText((CharSequence) bundle.get(_address));
            description.setText((CharSequence) bundle.get(_description));
            jobType.setText((CharSequence) bundle.get(_jobType));
            if( (bundle.getString(_additionalAddress) !=null && (bundle.getString(_additionalAddress).length()>1))){
                additionalAddress.setText(bundle.getString(_additionalAddress));
            }else {
                additionalAddress.setVisibility(View.GONE);
            }

            if( (bundle.getString(_siteName) !=null && (bundle.getString(_siteName).length()>1))){
                siteName.setText((CharSequence) bundle.get(_siteName));
            }else {
                siteName.setVisibility(View.GONE);
                site_border.setVisibility(View.GONE);
            }
            if( (bundle.getString(_equipmentName) !=null && (bundle.getString(_equipmentName).length()>1))){
                equipmentName.setText((CharSequence) bundle.get(_equipmentName));
            }else {
                equipmentName.setVisibility(View.GONE);
                equipment_border.setVisibility(View.GONE);
            }
            number.setText((CharSequence) bundle.get(_number));
            Linkify.addLinks(number, Linkify.ALL);

            boolean noContact = false;

            if (jobDescription.getNomContact() != null && jobDescription.getPreNomContact() != null && !jobDescription.getNomContact().equalsIgnoreCase("null null")) {
                if ((!TextUtils.isEmpty(jobDescription.getNomContact()) && !jobDescription
                        .getNomContact().matches("^\\s*$"))
                        || (!TextUtils.isEmpty(jobDescription.getPreNomContact()) && !jobDescription
                        .getPreNomContact().matches("^\\s*$"))) {
                    firstName.setText(jobDescription.getPreNomContact() + " "
                            + jobDescription.getNomContact().trim());
                } else {
                    noContact = true;
                }
            } else {
                noContact = true;
            }

            if (noContact) {
                clientFirstName = dao.getClientFirstName((Integer) bundle.get(_clientId));
                clientLastName = dao.getClientLastName((Integer) bundle.get(_clientId));
                if (clientFirstName ==null && clientLastName == null){
                    firstName.setText("");
                }else if (clientFirstName == null){
                    firstName.setText(clientLastName);}
                else if (clientLastName == null) {
                    firstName.setText(clientFirstName);
                }else {
                    firstName.setText(clientFirstName+" "+clientLastName);
                }
            }
        }
    }


    @SuppressLint("CutPasteId")
    private void initialView(View view) {

        dao = DaoManager.getInstance();
        clientName = view.findViewById(R.id.pop_up_customer_name);
        siteName = view.findViewById(R.id.pop_up_site_name);
        equipmentName = view.findViewById(R.id.pop_up_equipment_name);
        address = view.findViewById(R.id.pop_up_address);
         additionalAddress = view.findViewById(R.id.pop_up_additional_address);
        jobType = view.findViewById(R.id.pop_up_job_type);
        description = view.findViewById(R.id.pop_up_description);
        firstName = view.findViewById(R.id.pop_up_first_name);
        number = view.findViewById(R.id.pop_up_number);
        closeBtn = view.findViewById(R.id.close_btn);
        openBtn = view.findViewById(R.id.open_btn);
        site_border = view.findViewById(R.id.pop_up_site_name_border);
        equipment_border = view.findViewById(R.id.pop_up_equipment_name_border);

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

    public static JobDetailPopupDialog newInstance(Bundle bundle, JobDetailsNavigationInterface navigationInterface) {

        JobDetailPopupDialog fragment = new JobDetailPopupDialog();
        Bundle args = new Bundle() ;

        jobDetailsNavigationInterface = navigationInterface;

        args.putInt(_clientId, (bundle.getInt(KEYS.CurrentJobs.IDCLIENT)));
        args.putString(_number, String.valueOf(bundle.get(KEYS.CurrentJobs.MOBILE_CONTACT)));
        args.putString(_address, String.valueOf(bundle.get(KEYS.CurrentJobs.ADR_GLOBAL)));
        args.putString(_description, String.valueOf(bundle.get(KEYS.CurrentJobs.DESC)));
        args.putString(_siteName, String.valueOf(bundle.get(KEYS.CurrentJobs.NOMSITE)));
        args.putString(_equipmentName, String.valueOf(bundle.get(KEYS.CurrentJobs.NOMEQUIPMENT)));
        args.putString(_jobType, String.valueOf(bundle.get(KEYS.CurrentJobs.TYPE)));
        args.putString(_clientName, bundle.getString(KEYS.CurrentJobs.NOM_CLIENT_INTERV));
        args.putString(_additionalAddress, bundle.getString(KEYS.CurrentJobs.ADR_COMPLEMENT));
        args.putString(_jobId, (bundle.getString(KEYS.CurrentJobs.ID)));
        fragment.setArguments(args);

        return fragment;
    }

}
