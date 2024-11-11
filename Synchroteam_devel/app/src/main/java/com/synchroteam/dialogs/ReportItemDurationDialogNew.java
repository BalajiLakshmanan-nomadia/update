package com.synchroteam.dialogs;

import android.app.Activity;
import android.app.Dialog;
import androidx.annotation.NonNull;
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
public class ReportItemDurationDialogNew extends Dialog {

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
    private EditText dataEtHrs;

    /**
     * The data et.
     */
    private EditText dataEtMin;


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
    public ReportItemDurationDialogNew(Activity activity,
                                       ReportItemDurationInterface itemDurationDialogInterface, String
                                               titleDialog,
                                       String data, boolean isWriteable) {
        super(activity, android.R.style.Theme_Translucent_NoTitleBar);
        setCancelable(false);
        setContentView(R.layout.dialog_item_duration_reports_new);
        TextView dialogTitleTv = (TextView) findViewById(R.id.info);
        dialogTitleTv.setText(titleDialog);
        this.activity = activity;

        String hours = "03";
        String min = "00";
        if (data != null && data.contains(":")) {
            String[] hoursMin = data.split(":");
            hours = hoursMin[0];
            min = hoursMin[1];
        } else if (data != null && data.length() > 1) {
            hours = data;
            min = "00";
        } else {
            hours = "03";
            min = "00";
        }

        dataEtHrs = (EditText) findViewById(R.id.commentaire_hours);
        dataEtMin = (EditText) findViewById(R.id.commentaire_min);
        dataEtHrs.setText(hours);
        dataEtMin.setText(min);
        dataEtHrs.setSelection(hours.length());
        dataEtMin.setSelection(min.length());


        findViewById(R.id.cancelTv).setOnClickListener(onClickListener);
        findViewById(R.id.modifytv).setOnClickListener(onClickListener);

        dataEtHrs.setEnabled(isWriteable);
        dataEtHrs.setFocusable(isWriteable);

        dataEtMin.setEnabled(isWriteable);
        dataEtMin.setFocusable(isWriteable);

        findViewById(R.id.modifytv).setEnabled(isWriteable);

        dataEtHrs.addTextChangedListener(textWatcher);
        dataEtMin.addTextChangedListener(textWatcherMin);


        this.itemDurationDialogInterface = itemDurationDialogInterface;

    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

//            String str = dataEt.getText().toString();
//            if (str.isEmpty()) return;
//            String str2 = PerfectDecimal(str, 3, 2);
//
//            if (!str2.equals(str)) {
//                dataEt.setText(str2);
//                int pos = dataEt.getText().length();
//                dataEt.setSelection(pos);
//            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private TextWatcher textWatcherMin = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            String str = dataEtMin.getText().toString();
            if (str.isEmpty()) return;
            String str2 = minCal(str, 2);

            if (!str2.equals(str)) {
                dataEtMin.setText(str2);
                int pos = dataEtMin.getText().length();
                dataEtMin.setSelection(pos);
            }

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public String minCal(String str, int MAX_DECIMAL) {
        if (str.charAt(0) == ':') str = "00" + str;
        int max = str.length();

        String rFinal = "";
        boolean after = true;
        int i = 0, up = 0, decimal = 0;
        char t;
        while (i < max) {
            t = str.charAt(i);

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

            rFinal = rFinal + t;
            i++;
        }
        return rFinal;
    }

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
                    CommonUtils.hideKeyboard(activity, dataEtHrs);
                    String updatedString = null;

                    updatedString = getDurationValue();

                    itemDurationDialogInterface.doOnModifyClick(updatedString);

                    ReportItemDurationDialogNew.this.dismiss();
                    break;

                case R.id.cancelTv:
                    CommonUtils.hideKeyboard(activity, dataEtHrs);
                    itemDurationDialogInterface.doOnCancelClick();
                    ReportItemDurationDialogNew.this.dismiss();
                    break;
            }

        }
    };

    @NonNull
    private String getDurationValue() {
        String updatedString;
        if (!TextUtils.isEmpty(dataEtHrs.getText().toString()) &&
                dataEtHrs.getText().toString().length() > 0) {
            updatedString = dataEtHrs.getText().toString();

        } else {
            updatedString = "00";
        }

        if (!TextUtils.isEmpty(dataEtMin.getText().toString()) &&
                dataEtMin.getText().toString().length() > 0) {
            if (updatedString.trim().equals("00") && dataEtMin.getText().toString().trim().equals("00")) {
                updatedString = "03:00";
            } else {
                updatedString = updatedString + ":" + dataEtMin.getText().toString();
            }
        } else {
            updatedString = updatedString + ":00";
        }

        if (TextUtils.isEmpty(dataEtHrs.getText().toString()) &&
                dataEtHrs.getText().toString().trim().length() == 0 &&
                TextUtils.isEmpty(dataEtMin.getText().toString()) &&
                dataEtMin.getText().toString().trim().length() == 0) {
            updatedString = "03:00";
        }
        return updatedString;
    }

}
