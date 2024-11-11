package com.synchroteam.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.CommonUtils;

// TODO: Auto-generated Javadoc

/**
 * The Class EnterDataDialog.
 */
public class ReportItemDurationDialog extends Dialog {

    /**
     * The Interface ReportItemDurationDialog.
     */
    public interface ReportItemDurationInterface {

        /**
         * Do on modify click.
         *
         * @param data the data
         */
        public void doOnModifyClick(String data);

        /**
         * Do on cancel click.
         */
        public void doOnCancelClick();

    }

    /**
     * The enter dialog interface.
     */
    private ReportItemDurationInterface itemDurationDialogInterface;

    /**
     * The data et.
     */
    private EditText dataEt;

    /**
     * The activity.
     */
    private Activity activity;

    /**
     * Instantiates a new enter data dialog.
     *
     * @param activity                    the activity
     * @param itemDurationDialogInterface the enter dialog interface
     * @param titleDialog                 the title dialog
     * @param data                        the data
     */
    public ReportItemDurationDialog(Activity activity,
                                    ReportItemDurationInterface itemDurationDialogInterface, String
                                            titleDialog,
                                    String data, boolean isWriteable) {
        super(activity, android.R.style.Theme_Translucent_NoTitleBar);
        setCancelable(false);
        setContentView(R.layout.dialog_item_duration_reports);
        TextView dialogTitleTv = (TextView) findViewById(R.id.info);
        dialogTitleTv.setText(titleDialog);
        this.activity = activity;
        dataEt = (EditText) findViewById(R.id.commentaire);
        if (data != null && data.length() > 0) {
            dataEt.setText(data);
            dataEt.setSelection(data.length());
        } else {
            String durationDefaultValue = activity.getString(R.string.def_duration_value);
            dataEt.setText(durationDefaultValue);
            dataEt.setSelection(durationDefaultValue.length());
        }

        findViewById(R.id.cancelTv).setOnClickListener(onClickListener);
        findViewById(R.id.modifytv).setOnClickListener(onClickListener);

        dataEt.setEnabled(isWriteable);
        dataEt.setFocusable(isWriteable);

        findViewById(R.id.modifytv).setEnabled(isWriteable);

//        InputFilter[] FilterArray = new InputFilter[1];
//        FilterArray[0] = new InputFilter.LengthFilter(12288);

//        dataEt.setFilters(FilterArray);

//        dataEt.setFilters(new InputFilter[]{new SemiColonDigitsInputFilter(4, 2, true)});

        dataEt.addTextChangedListener(textWatcher);


        this.itemDurationDialogInterface = itemDurationDialogInterface;

    }

    private TextWatcher textWatcher = new TextWatcher() {
        int prevLength;
        int currentLength;
        boolean backPressed;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (!TextUtils.isEmpty(dataEt.getText().toString()) && dataEt.getText().toString().length() > 0)
                prevLength = dataEt.getText().toString().length();
            else
                prevLength = 0;
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            if (!TextUtils.isEmpty(dataEt.getText().toString()) && dataEt.getText().toString().length() > 0)
                currentLength = dataEt.getText().toString().trim().length();

            if (currentLength < prevLength) {
                backPressed = true;
            } else {
                backPressed = false;
            }
            String str = dataEt.getText().toString();
            if (str.isEmpty()) {
                return;
            } else if (str.length() == 2 && !str.contains(":") && !backPressed) {
                String updatedDurationValue = str + ":";
                dataEt.setText(updatedDurationValue);
                dataEt.setSelection(updatedDurationValue.length());
                return;
            }

            String str2 = PerfectDecimal(str, 2, 2);

            if (!str2.equals(str)) {
                dataEt.setText(str2);
                int pos = dataEt.getText().length();
                dataEt.setSelection(pos);
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public String PerfectDecimal(String str, int MAX_BEFORE_POINT, int MAX_DECIMAL) {
        if (str.charAt(0) == ':') str = "00" + str;
        int max = str.length();

        String rFinal = "";
        boolean after = false;
        int i = 0, up = 0, decimal = 0;
        char t;
        while (i < max) {
            t = str.charAt(i);
            if (t != ':' && after == false) {
                up++;
                if (up > MAX_BEFORE_POINT) return rFinal;
            } else if (t == ':') {
                after = true;
            } else {
                if (decimal == 0) {
                    if (Integer.parseInt("" + t) <= 5) {
                        decimal++;
                        if (decimal > MAX_DECIMAL)
                            return rFinal;
                    } else {
                        return rFinal;
                    }
                } else {
                    decimal++;
                    if (decimal > MAX_DECIMAL)
                        return rFinal;
                }
            }
            rFinal = rFinal + t;
            i++;
        }
        return rFinal;
    }

    private String validateMinutes(String time) {
        String[] tokens = time.split(":");
        if (tokens.length > 1) {
            String first = tokens[0];
            String second = tokens[1];

            if (Integer.parseInt("" + second.charAt(0)) <= 5) {
                return time;
            } else {
                return first + ":";
            }
        } else {
            return time;
        }

    }

    /**
     * The on click listener.
     */
    View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int id = v.getId();

            switch (id) {
                case R.id.modifytv:
                    CommonUtils.hideKeyboard(activity, dataEt);
                    String updatedDurationText = dataEt.getText().toString();
                    if (!TextUtils.isEmpty(updatedDurationText) &&
                            updatedDurationText.length() > 0) {
                        if (updatedDurationText.contains(":")) {
                            String hour[] = dataEt.getText().toString().split(":");
                            if (hour.length == 1) {
                                updatedDurationText = updatedDurationText + "00";
                            }
                        }
                    } else {
                        //todo confirm and uncomment
//                        updatedDurationText = activity.getString(R.string.def_duration_value);
                    }

                    itemDurationDialogInterface.doOnModifyClick(updatedDurationText);

                    ReportItemDurationDialog.this.dismiss();
                    break;

                case R.id.cancelTv:
                    CommonUtils.hideKeyboard(activity, dataEt);
                    itemDurationDialogInterface.doOnCancelClick();
                    ReportItemDurationDialog.this.dismiss();
                    break;
            }

        }
    };

}
