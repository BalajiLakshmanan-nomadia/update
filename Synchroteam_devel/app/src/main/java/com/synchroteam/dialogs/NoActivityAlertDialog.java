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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.synchroteam.dao.Dao;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DaoManager;

/**
 * Created by Trident.
 * This class is used for as a dialog that appears no actions are performed when certain time period,
 * after selecting clocked in mode and
 * it automatically goes to clocked out mode..
 */
public class NoActivityAlertDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = NoActivityAlertDialog.class.getSimpleName();
    private static final String ACTIVITY_NAME = "activity_name";

    private TextView txtOK;
    private LinearLayout layClose;

    Context context;
    private Dao dataAccessObject;

    public static NoActivityAlertDialog newInstance() {

        NoActivityAlertDialog fragment = new NoActivityAlertDialog();
        Bundle args = new Bundle();
        args.putString(ACTIVITY_NAME, "");
        fragment.setArguments(args);

        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_no_activity_alert_layout, container);

        context = getActivity();

        setCustomTypeface();

        initializeUI(view);

        return view;
    }

    private void initializeUI(View view) {

        dataAccessObject = DaoManager.getInstance();

        txtOK = (TextView) view.findViewById(R.id.txt_ok);
        layClose = (LinearLayout) view.findViewById(R.id.lay_close);

        txtOK.setOnClickListener(this);
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
        dialogHeight = (int) (screenHeight * Double.parseDouble(this.getResources().getString(R.string.driving_height)));
        dialogWidth = (int) (screenWidth * Double.parseDouble(this.getResources().getString(R.string.driving_width)));
        getDialog().getWindow().setLayout(dialogWidth, dialogHeight);
    }

    private void setCustomTypeface() {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return (keyCode == KeyEvent.KEYCODE_BACK);
            }
        });
        return dialog;
    }

    private void showToastMessage(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
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