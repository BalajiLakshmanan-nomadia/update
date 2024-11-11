package com.synchroteam.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import android.view.View;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.synchroteam3.R;

/**
 * Created by user on 23/6/17.
 */

// Bottom sheet dialog for reschedule and decline jobs
public class RescheduleJobMenuDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    TextView txtRescheduleJob, txtDeclineJob, txtCancel;


    public static RescheduleJobMenuDialog getInstance(GestionAcces gestionAcces) {
        RescheduleJobMenuDialog rescheduleJobMenuDialog = new RescheduleJobMenuDialog();

        return rescheduleJobMenuDialog;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };


    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);

        View contentView = View.inflate(getContext(), R.layout.dialog_menu_reschedule_job, null);

        txtRescheduleJob = (TextView) contentView.findViewById(R.id.txtRescheduleJob);
        txtDeclineJob = (TextView) contentView.findViewById(R.id.txtDeclineJob);
        txtCancel = (TextView) contentView.findViewById(R.id.txtCancel);

        txtRescheduleJob.setVisibility(View.VISIBLE);
        txtDeclineJob.setVisibility(View.VISIBLE);

//        if (gestionAcces != null && gestionAcces.getOptionReplanif() != 1) {
//            txtRescheduleJob.setVisibility(View.GONE);
//        }
//        if (gestionAcces != null && gestionAcces.getOptionReject() != 1) {
//            txtDeclineJob.setVisibility(View.GONE);
//        }

//        txtRescheduleJob.setOnClickListener(this);
//        txtDeclineJob.setOnClickListener(this);
//        txtCancel.setOnClickListener(this);


        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.txtRescheduleJob:
//                replanifIntervention();
                break;
            case R.id.txtDeclineJob:
//                rejeterIntervention();
                break;
            case R.id.txtCancel:
                dismiss();
                break;
        }
    }

}