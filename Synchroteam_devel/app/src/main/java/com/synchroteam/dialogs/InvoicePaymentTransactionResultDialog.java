package com.synchroteam.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.synchroteam.beans.UpdateDataBaseEvent;
import com.synchroteam.beans.UpdateUiAfterSync;
import com.synchroteam.beans.User;
import com.synchroteam.dao.Dao;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.SynchroteamUitls;

import de.greenrobot.event.EventBus;

/**
 * Created by user on 21/4/17.
 */
@SuppressLint("ValidFragment")
public class InvoicePaymentTransactionResultDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "payment";
    Activity activity;
    Dialog dialog;
    private static Typeface typeFace;

    private TextView textResult;
    private TextView signResult;
    private TextView txtMsg;
    private TextView txtpaidAmount;
    private TextView txtOK;

    private String successSymbol;
    private String failureSymbol;

    private String paidAmount;
    private String status;
    private String msg;

    private Dao dao;
    private ProgressDialog progressDSynch;

    public InvoicePaymentTransactionResultDialog(Activity activity, String status, String msg, String paidAmount) {
        this.activity = activity;
        this.paidAmount = paidAmount;
        this.status = status;
        this.msg = msg;
    }

//    public static InvoicePaymentTransactionResultDialog getInstance(String status, String msg, String paidAmount) {
//
//        InvoicePaymentTransactionResultDialog resultDialog = new InvoicePaymentTransactionResultDialog();
//
//        Bundle args = new Bundle();
//        args.putString("S", status);
//        args.putString("M", msg);
//        args.putString("P", paidAmount);
//        resultDialog.setArguments(args);
//
//        return resultDialog;
//    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        dialog = new Dialog(getActivity(), android.R.style.Theme_Light_NoTitleBar) {
            @Override
            public void onBackPressed() {
//                super.onBackPressed();
                synch();
            }
        };

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        dialog.setContentView(R.layout.layout_dialog_invoice_payment_success);

        typeFace = Typeface.createFromAsset(getActivity().getAssets(),
                getActivity().getString(R.string.fontName_fontAwesome));

        successSymbol = activity.getResources().getString(R.string.fa_thumbs_up);
        failureSymbol = activity.getResources().getString(R.string.fa_thumbs_down);

        dao = DaoManager.getInstance();

        initializeUI();

        dialog.setCancelable(true);

        dialog.show();

        return dialog;
    }

    private void getValues() {
        paidAmount = getArguments().getString("P");
        status = getArguments().getString("S");
        msg = getArguments().getString("M");
    }

    private void initializeUI() {
        textResult = (TextView) dialog.findViewById(R.id.txt_result);
        signResult = (TextView) dialog.findViewById(R.id.txt_sign_result);
        txtMsg = (TextView) dialog.findViewById(R.id.txt_you_have_paid);
        txtpaidAmount = (TextView) dialog.findViewById(R.id.txt_amount_paid);
        txtOK = (TextView) dialog.findViewById(R.id.txt_OK);

        txtpaidAmount.setVisibility(View.VISIBLE);

        if (status.equals("1")) {
            signResult.setText(successSymbol);
            textResult.setText(getString(R.string.txt_success) + " !");
            txtMsg.setText(R.string.txt_payment_confirmed);

            if (paidAmount != null) {
                txtpaidAmount.setText(paidAmount);
            }

        } else {
            signResult.setText(failureSymbol);
            textResult.setText(getString(R.string.txt_failure) + " !");
            txtpaidAmount.setVisibility(View.GONE);
            txtMsg.setText(msg);
        }

        txtOK.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_OK:

                synch();

                break;
            default:
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    /**
     * Synch.
     */
    private void synch() {

        if (SynchroteamUitls.isNetworkAvailable(getActivity())) {
            progressDSynch = ProgressDialog.show(getActivity(),
                    getString(R.string.textPleaseWaitLable),
                    getString(R.string.msg_synch), true, false);

            Thread syncDbToServer = new Thread((new Runnable() {

                @Override
                public void run() {

                    Message myMessage = new Message();
                    try {
                        User u = dao.getUser();
                        dao.sync(u.getLogin(), u.getPwd());
                        Logger.output(TAG, " finished sync");
                        Thread.sleep(3000);
                        myMessage.obj = "ok";

                        handler.sendMessage(myMessage);

                    } catch (Exception ex) {
                        String exception = ex.getMessage();
                        Logger.printException(ex);
                        if (exception != null) {
                            if (exception.indexOf("4001") != -1) {
                                myMessage.obj = "4001";
                            } else if (exception.indexOf("4000") != -1) {
                                myMessage.obj = "4000";
                            }else if (exception.indexOf("4006") != -1) {
                                myMessage.obj = "4006";
                            } else if (exception.indexOf("4101") != -1) {
                                myMessage.obj = "4101";
                            } else if (exception.indexOf("4005") != -1) {
                                myMessage.obj = "4005";
                            } else if (exception.indexOf("4003") != -1) {
                                myMessage.obj = "4003";
                            }else {
                                myMessage.obj = "Error";
                            }
                        } else {
                            myMessage.obj = "Error";
                        }

                        handler.sendMessage(myMessage);

                    } finally {
                        if (progressDSynch != null
                                && progressDSynch.isShowing())
                            progressDSynch.dismiss();

                    }

                }
            }));
            syncDbToServer.start();
        } else {

            EventBus.getDefault().post(new UpdateDataBaseEvent());
            if(!getActivity().isFinishing())
            {
                SynchroteamUitls.showToastMessage(getActivity());
            }        }

    }

    /**
     * The handler.
     */
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            String erreur = (String) msg.obj;
            EventBus.getDefault().post(new UpdateDataBaseEvent());

            if (erreur.equals("ok")) {
                Toast.makeText(getActivity(),
                        getString(R.string.msg_synch_ok), Toast.LENGTH_LONG)
                        .show();

                EventBus.getDefault().post(new UpdateUiAfterSync());

            } else if (erreur.equals("4001")||erreur.equals("4000")) {
                showAuthErrDialog();
            } else if (erreur.equals("4003")) {
                showErrMsgDialog(getString(R.string.msg_sync_error_4003));
            }else {
//                Toast.makeText(getActivity(),
//                        getString(R.string.msg_synch_error_new), Toast.LENGTH_LONG)
//                        .show();
                showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));
            }

            dialog.dismiss();

        }
    };

    /**
     * Show authentication error dialog
     */
    protected void showAuthErrDialog() {

        User user = dao.getUser();
        AuthenticationErrorDialog authenticationErrorDialog = new AuthenticationErrorDialog(
                getActivity(), user.getLogin());

        authenticationErrorDialog.show();
    }

    /**
     * Show error dialog
     */
    protected void showErrMsgDialog(String errMsg) {

        ErrorDialog errDialog = new ErrorDialog(getActivity(), errMsg);

        errDialog.show();
    }
    /**
     * For showing the synchronization failure messages
     */
    protected void showSyncFailureMsgDialog(String syncFailureMsg) {

        SynchronizationErrorDialog synchronizationErrorDialog = new SynchronizationErrorDialog
                (getActivity(), syncFailureMsg, new SynchronizationErrorDialog
                        .SynchronizationErrorInterface() {
                    @Override
                    public void doOnOkayClick() {
                        //perform any action
                    }
                });

        synchronizationErrorDialog.show();
    }

}