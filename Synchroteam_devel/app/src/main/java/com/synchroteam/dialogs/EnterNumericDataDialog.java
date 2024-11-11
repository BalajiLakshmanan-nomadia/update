package com.synchroteam.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.CommonUtils;
import com.synchroteam.utils.DecimalDigitsInputFilter;
import com.synchroteam.utils.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: Auto-generated Javadoc

/**
 * The Class EnterNumericDataDialog.
 */
public class EnterNumericDataDialog extends Dialog {

    private boolean isButtonDisabled = true;

    /**
     * The Interface EnterDialogInterface.
     */
    public interface EnterDialogInterface {

        /**
         * Do on modify click.
         *
         * @param data the data
         */
        public void doOnModifyClick(String data, EditText view);

        /**
         * Do on cancel click.
         */
        public void doOnCancelClick();

    }

    /**
     * The enter dialog interface.
     */
    private EnterDialogInterface enterDialogInterface;

    /**
     * The data et.
     */
    private EditText dataEt;

    /**
     * The activity.
     */
    private Activity activity;
    private int disabledColorBtn, enabledColorbtn;

    private TextView cancelTv, modifytv;

    private static final String TAG = "EnterNumericDataDialog";

    /**
     * Instantiates a new enter numeric data dialog.
     *
     * @param activity             the activity
     * @param enterDialogInterface the enter dialog interface
     * @param titleDialog          the title dialog
     * @param data                 the data
     */
    public EnterNumericDataDialog(Activity activity,
                                  EnterDialogInterface enterDialogInterface, String titleDialog,
                                  String data) {
        super(activity, android.R.style.Theme_Translucent_NoTitleBar);
        setCancelable(false);
        setContentView(R.layout.layout_enter_data__numeric_reports_jobdetail);
        TextView dialogTitleTv = (TextView) findViewById(R.id.info);
        dialogTitleTv.setText(titleDialog);
        this.activity = activity;
        disabledColorBtn = activity.getResources().getColor(
                R.color.textCancelOkTvDataEnterDialog);
        enabledColorbtn = activity.getResources().getColor(R.color.black);
        cancelTv = (TextView) findViewById(R.id.cancelTv);
        modifytv = (TextView) findViewById(R.id.modifytv);
        cancelTv.setOnClickListener(onClickListener);
        modifytv.setOnClickListener(onClickListener);
        dataEt = (EditText) findViewById(R.id.commentaire);

        dataEt.setFilters(new InputFilter[]{new DecimalInputFilter()});

        dataEt.requestFocus();
        CommonUtils.showKeyboard(activity, dataEt);

        dataEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                if (s.toString().contains("-.")) {
                    String num = dataEt.getText().toString();
                    dataEt.setText(num.replace(".", ""));
                    dataEt.setSelection(dataEt.getText().toString().indexOf("-") + 1);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

                if (!TextUtils.isEmpty(s.toString())) {
                    if (isButtonDisabled) {
                        cancelTv.setTextColor(enabledColorbtn);
                        modifytv.setTextColor(enabledColorbtn);
                        isButtonDisabled = false;
                    }

                } else {
                    cancelTv.setTextColor(disabledColorBtn);
                    modifytv.setTextColor(disabledColorBtn);
                    isButtonDisabled = true;
                }

            }
        });

        dataEt.setText(data);
        dataEt.setSelection(data.length());
        this.enterDialogInterface = enterDialogInterface;

    }

    /**
     * The on click listener.
     */
    android.view.View.OnClickListener onClickListener = new android.view.View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int id = v.getId();

            switch (id) {
                case R.id.modifytv:
                    String entredText;
                    if ((!TextUtils.isEmpty(dataEt.getText().toString()))
                            && (dataEt.getText().length() > 1)
                            && (dataEt.getText().toString().endsWith("."))) {

                        entredText = dataEt.getText().toString().replace(".", "");
                    } else {
                        entredText = dataEt.getText().toString();
                    }

                    CommonUtils.hideKeyboard(activity, dataEt);
                    enterDialogInterface.doOnModifyClick(entredText, dataEt);

                    // EnterNumericDataDialog.this.dismiss();
                    break;

                case R.id.cancelTv:
                    CommonUtils.hideKeyboard(activity, dataEt);
                    enterDialogInterface.doOnCancelClick();
                    EnterNumericDataDialog.this.dismiss();
                    break;
            }

        }
    };

    public class DecimalInputFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            boolean allowMinus = true;
            boolean allowDot = true;
            if ((!TextUtils.isEmpty(dest))
                    && (dest.length() > 0)
                    && (dest.toString().startsWith("."))) {

                allowMinus = false;
                allowDot = false;

            } else if ((!TextUtils.isEmpty(dest))
                    && (dest.length() > 0)
                    && (dest.toString().startsWith("-"))) {
                allowMinus = false;

                if (dest.toString().contains("."))
                    allowDot = false;
                else
                    allowDot = true;

            } else if ((!TextUtils.isEmpty(dest))
                    && (dest.length() >= 1)) {
                allowMinus = false;
                if (dest.toString().contains("."))
                    allowDot = false;
                else
                    allowDot = true;
            }

            if ((!TextUtils.isEmpty(dest))
                    && (dest.length() > 0)) {
                if (source.toString().equals(".")) {
                    if (!allowDot) {
                        return "";
                    }
                } else if (source.toString().equals("-")) {
                    if (!allowMinus) {
                        return "";
                    }
                }
            }
            return null;
        }

    }

}
