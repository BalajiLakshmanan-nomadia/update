package com.synchroteam.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.text.InputFilter;
import android.view.View;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.CommonUtils;

// TODO: Auto-generated Javadoc

/**
 * The Class EnterDataDialog.
 */
public class EnterCommentDialog extends Dialog {

    /**
     * The Interface EnterDialogInterface.
     */
    public interface EnterDialogInterface {

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
    private EnterDialogInterface enterDialogInterface;

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
     * @param activity             the activity
     * @param enterDialogInterface the enter dialog interface
     * @param titleDialog          the title dialog
     * @param data                 the data
     */
    public EnterCommentDialog(Activity activity,
                              EnterDialogInterface enterDialogInterface, String titleDialog,
                              String data, boolean isWriteable) {
        super(activity, android.R.style.Theme_Translucent_NoTitleBar);
        setCancelable(false);
        setContentView(R.layout.layout_enter_data_reports_jobdetail);
        TextView dialogTitleTv = (TextView) findViewById(R.id.info);
        dialogTitleTv.setText(titleDialog);
        this.activity = activity;
        dataEt = (EditText) findViewById(R.id.commentaire);
        dataEt.setText(data);
        if (data != null && dataEt.length() > 0)
            dataEt.setSelection(data.length());


        findViewById(R.id.cancelTv).setOnClickListener(onClickListener);
        findViewById(R.id.modifytv).setOnClickListener(onClickListener);

        dataEt.setEnabled(isWriteable);
        dataEt.setFocusable(isWriteable);
        findViewById(R.id.modifytv).setEnabled(isWriteable);

        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(12288);

        dataEt.setFilters(FilterArray);

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
                    CommonUtils.hideKeyboard(activity, dataEt);
                    enterDialogInterface.doOnModifyClick(dataEt.getText()
                            .toString());

                    EnterCommentDialog.this.dismiss();
                    break;

                case R.id.cancelTv:
                    CommonUtils.hideKeyboard(activity, dataEt);
                    enterDialogInterface.doOnCancelClick();
                    EnterCommentDialog.this.dismiss();
                    break;
            }

        }
    };

}
