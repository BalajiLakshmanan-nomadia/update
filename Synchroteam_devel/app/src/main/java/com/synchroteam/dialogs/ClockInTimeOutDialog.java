package com.synchroteam.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.synchroteam.synchroteam3.R;

/**
 * Created by Trident on 23/8/17.
 */

public class ClockInTimeOutDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = ClockInTimeOutDialog.class.getSimpleName();

    private TextView txtOk;
    private LinearLayout layClose;
    Context context;

    public static ClockInTimeOutDialog newInstance() {

        ClockInTimeOutDialog fragment = new ClockInTimeOutDialog();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_inactivity, container);
        context = getActivity();
        initializeUI(view);
        return view;
    }

    private void initializeUI(View view) {
        txtOk = (TextView) view.findViewById(R.id.txt_ok);
        layClose = (LinearLayout) view.findViewById(R.id.lay_close);
        txtOk.setOnClickListener(this);
        layClose.setOnClickListener(this);
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
        dialogHeight = (int) (screenHeight * Double.parseDouble(this.getResources().getString(R.string.dialog_job_details_height)));
        dialogWidth = (int) (screenWidth * Double.parseDouble(this.getResources().getString(R.string.dialog_job_pause_width)));
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_ok:
                dismiss();
                break;
            case R.id.lay_close:
                dismiss();
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}