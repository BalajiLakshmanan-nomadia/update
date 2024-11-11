package com.synchroteam.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.synchroteam.beans.TravelActivity;
import com.synchroteam.beans.UnavailabilityBeans;
import com.synchroteam.dao.Dao;
import com.synchroteam.listadapters.ActivityDialogListAdapter;
import com.synchroteam.listadapters.DrivingDialogListAdapter;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.Commons;
import com.synchroteam.utils.DaoManager;

import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;


public class DrivingOrActivityListDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = ClockJobTravelActDialog.class.getSimpleName();

    Context context;

    private LinearLayout layClose;
    private ImageView imgBack;
    private TextView txtActivity;
    private ListView listActivity;
    private ArrayList<UnavailabilityBeans> unavailabilitiesList;
    private ArrayList<TravelActivity> travelActivitiesist;
    private Dao dataAccessObject;
    protected LocationManager locationManager;
    private boolean isGPSEnabled = false;
    private boolean isNetworkEnabled = false;
    private int type;
    private static final String TYPE = "type";

    public static DrivingOrActivityListDialog newInstance(int type) {
        DrivingOrActivityListDialog fragment = new DrivingOrActivityListDialog();
        Bundle args = new Bundle();
        args.putInt(TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_activity_list, container);
        initializeUI(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = getActivity();
        checkNetworkAndGPS();
        type = getArguments().getInt(TYPE);
        switch (type) {
            case Commons.IS_DRIVING:
                setTravelActivityListAdapter();
                txtActivity.setText(getString(R.string.txt_travel));
                break;
            case Commons.IS_ACTIVITY:
                setActivityListAdapter();
                txtActivity.setText(getString(R.string.txt_activity));
                break;
        }
    }

    private void checkNetworkAndGPS() {
        locationManager = (LocationManager) getActivity()
                .getSystemService(LOCATION_SERVICE);

        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        isNetworkEnabled = Build.FINGERPRINT.contains("generic") ? true : locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void setActivityListAdapter() {
        unavailabilitiesList = new ArrayList<>();
        unavailabilitiesList = dataAccessObject.getActivities();
        listActivity.setAdapter(new ActivityDialogListAdapter(
                getActivity(), unavailabilitiesList, this, null));
    }

    private void setTravelActivityListAdapter() {
        travelActivitiesist = new ArrayList<>();
        travelActivitiesist = dataAccessObject.getTravelActivities();
        listActivity.setAdapter(new DrivingDialogListAdapter(
                getActivity(), travelActivitiesist, this));
    }


    private void initializeUI(View view) {
        dataAccessObject = DaoManager.getInstance();

        imgBack = (ImageView) view.findViewById(R.id.txt_back);
        txtActivity = (TextView) view.findViewById(R.id.txt_activity);
        listActivity = (ListView) view.findViewById(R.id.list_activity);
        layClose = (LinearLayout) view.findViewById(R.id.lay_close);

        imgBack.setOnClickListener(this);
        layClose.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * sets the height of the dialog dynamically based on orientation.
     */
    @Override
    public void onResume() {
        super.onResume();
//        settingDialogHeight();
    }

    private void settingDialogHeight() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenHeight = metrics.heightPixels;
        int screenWidth = metrics.widthPixels;
        int dialogHeight = 0, dialogWidth = 0;
        dialogHeight = (int) (screenHeight * Double.parseDouble(this.getResources().getString(R.string.clock_in_out_height)));
        dialogWidth = (int) (screenWidth * Double.parseDouble(this.getResources().getString(R.string.clock_in_out_width)));
        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        return dialog;
    }

    protected void showLocationSettingDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getString(R.string.textEnableLocationServiceTracking))
                .setMessage(getActivity().getString(R.string.textEnableLocationServiceText))
                .setPositiveButton(getActivity().getString(R.string.textOkLable).toUpperCase(),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // show system settings`
                                getActivity().startActivity(new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(getActivity().getString(R.string.textDontAllowTracking),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_back:
                ClockJobTravelActDialog.newInstance(false).show(getActivity().getSupportFragmentManager(), "");
                dismiss();
                break;
            case R.id.lay_close:
                ClockJobTravelActDialog.newInstance(false).show(getActivity().getSupportFragmentManager(), "");
                dismiss();
                break;
        }
    }
}