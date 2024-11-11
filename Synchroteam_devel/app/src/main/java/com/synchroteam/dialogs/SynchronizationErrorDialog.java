package com.synchroteam.dialogs;


import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.synchroteam.synchroteam3.R;

/**
 * This Class is used for displaying the various synchronization error throught the
 * app
 *
 * @Author Dharmalingam.R
 * @Company Trident Solution & Created on 04 Dec 2017
 */

public class SynchronizationErrorDialog extends Dialog {

    /**
     * ** Creates an instance of SynchronizationErrorInterface and perform
     * necessary initialization.
     */
    private final SynchronizationErrorDialog.SynchronizationErrorInterface syncFailureInterface;

    /**
     * The Interface DeleteInvoiceQuotationInterface.
     */
    public interface SynchronizationErrorInterface {

        /***
         * Dismiss the dialog and perform change if necessary using this interface.
         */
        void doOnOkayClick();

    }


    /**
     * Instantiates a SynchronizationErrorDialog
     *
     * @param activity             the Activity
     * @param errorMsg             the sync error message
     * @param syncFailureInterface interface
     */
    public SynchronizationErrorDialog(Activity activity, String errorMsg, SynchronizationErrorInterface syncFailureInterface) {
        super(activity, android.R.style.Theme_Translucent_NoTitleBar);
        setCancelable(false);
        setContentView(R.layout.dialog_synchronization_failure);
        this.getWindow().setGravity(Gravity.CENTER);

        findViewById(R.id.okBtn).setOnClickListener(onClickListener);
        ((TextView) findViewById(R.id.txtErrMsg)).setText(errorMsg);
        this.syncFailureInterface = syncFailureInterface;

    }

    /**
     * The on click listener.
     */
    android.view.View.OnClickListener onClickListener = new android.view.View.OnClickListener() {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int id = v.getId();
            if (id == R.id.okBtn) {
                dismiss();
                syncFailureInterface.doOnOkayClick();
            }

        }
    };

}
