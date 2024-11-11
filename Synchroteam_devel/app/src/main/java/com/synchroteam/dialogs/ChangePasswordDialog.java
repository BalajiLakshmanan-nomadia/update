package com.synchroteam.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.synchroteam.beans.User;
import com.synchroteam.customtoast.Crouton;
import com.synchroteam.customtoast.Style;
import com.synchroteam.dao.Dao;
import com.synchroteam.retrofit.ApiInterface;
import com.synchroteam.retrofit.Apiclient;
import com.synchroteam.retrofit.models.mobileAuth.ChangePasswordModel;
import com.synchroteam.retrofit.models.mobileAuth.MobileAuth;
import com.synchroteam.retrofit.models.syncservice.SyncService;
import com.synchroteam.synchroteam.SyncoteamNavigationActivity;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DateFormatUtils;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.SharedPref;
import com.synchroteam.utils.SynchroteamUitls;

import org.jetbrains.annotations.NotNull;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class ChangePasswordDialog extends DialogFragment {

    private String strUserName, strUserDomain, strOldPwd, strNewPwd, strConformPwd, strToken, strExpiry;
    private EditText edtChpDomain, edtChpUser, edtChpNewPwd, edtChpConfirmPwd, edtChpCurrentPwd;
    private ProgressBar mPbChangePwd;
    private TextView btnChangePwd;
    private Dao dataAccessOperator;
    private LinearLayout mLinLayClose;
    SyncService syncService;
    User user;

    public ChangePasswordDialog instance(String domain, String user) {
        ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog();
        Bundle setChangePwdBundle = new Bundle();
        setChangePwdBundle.putString("domain", domain);
        setChangePwdBundle.putString("user", user);
        changePasswordDialog.setArguments(setChangePwdBundle);
        return changePasswordDialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(false);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View viewChangePWd = inflater.inflate(R.layout.dialog_change_password, container);
        setInitView(viewChangePWd);
        dataAccessOperator = DaoManager.getInstance();
        getDatachangePwdBundle();
        user = dataAccessOperator.getUser();
        syncService = new SyncService();
        setTextToView();
        setChangeButtonAction();
        return viewChangePWd;
    }

    private void setChangeButtonAction() {
        btnChangePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getViewValues();
            }
        });
        mLinLayClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
    }

    private void getViewValues() {
        mPbChangePwd.setVisibility(View.VISIBLE);
        strUserDomain = edtChpDomain.getText().toString().trim();
        strUserName = edtChpUser.getText().toString().trim();
        strOldPwd = edtChpCurrentPwd.getText().toString().trim();
        strNewPwd = edtChpNewPwd.getText().toString().trim();
        strConformPwd = edtChpConfirmPwd.getText().toString().trim();
        if (SynchroteamUitls.isNetworkAvailable(getActivity())) {
            if (TextUtils.isEmpty(strUserDomain) || TextUtils.isEmpty(strUserName) || TextUtils.isEmpty(strNewPwd) ||
                    TextUtils.isEmpty(strConformPwd) || TextUtils.isEmpty(strOldPwd)) {
                mPbChangePwd.setVisibility(View.GONE);
                Toast.makeText(getActivity(), R.string.text_enter_all_values, Toast.LENGTH_SHORT).show();

            } else {
                if (strConformPwd.equals(strNewPwd)) {
                    final ApiInterface apiService =
                            Apiclient.getClient().create(ApiInterface.class);
                    final ChangePasswordModel changePasswordModel = new ChangePasswordModel();
                    changePasswordModel.setDomain(strUserDomain);
                    changePasswordModel.setLogin(strUserName);
                    changePasswordModel.setOldPassword(strOldPwd);
                    changePasswordModel.setNewPassword(strNewPwd);
                    changePasswordModel.setConformPassword(strConformPwd);
                    retrofit2.Call<SyncService> call = apiService.synchronizeServer(strUserDomain);
                    call.enqueue(new Callback<SyncService>() {
                        @Override
                        public void onResponse(@NotNull retrofit2.Call<SyncService> call, @NotNull Response<SyncService> response) {
                            if (response.isSuccessful()) {
                                syncService = response.body();
                                assert syncService != null;
                                SharedPref.setUrlMobileAuth(syncService.getUrl().getUrlMobileAuth(), getActivity());
                                String auth = syncService.getUrl().getUrlMobileAuth() + "/Change";
                                Call<MobileAuth> changePasswordAuth = apiService.getChangePassword(auth, changePasswordModel);
                                changePasswordAuth.enqueue(new Callback<MobileAuth>() {
                                    @Override
                                    public void onResponse(@NotNull Call<MobileAuth> call, @NotNull Response<MobileAuth> response) {
                                        if (response.isSuccessful()) {
                                            MobileAuth mobileAuth = response.body();
                                            if (mobileAuth != null) {
                                                if (mobileAuth.getResult() == 1) {
                                                    strToken = mobileAuth.getData().getAuthToken();
                                                    strExpiry = DateFormatUtils.getFormattedDateFromAPIDate(mobileAuth.getData().getAuthExpiry());
                                                    dataAccessOperator = DaoManager.getInstance();
                                                    dataAccessOperator.setAuthToken1(strUserDomain, strToken, strExpiry,user.getId());
                                                    SharedPref.setSyncStdServer(syncService.getStd().getServer(), getActivity());
                                                    SharedPref.setSyncStdPort(syncService.getStd().getPort(), getActivity());
                                                    SharedPref.setSyncStdSsl(syncService.getStd().getSsl(), getActivity());

                                                    SharedPref.setSyncDebugServer(syncService.getDebug().getServer(), getActivity());
                                                    SharedPref.setSyncDebugPort(syncService.getDebug().getPort(), getActivity());
                                                    SharedPref.setSyncDebugSsl(syncService.getDebug().getSsl(), getActivity());

                                                    //version 50 changes
                                                    SharedPref.setUrlStripeServer(syncService.getUrl().getUrlStripe(), getActivity());
                                                    SharedPref.setNotiUrlServer(syncService.getUrl().getUrlEventListener(), getActivity());

                                                    //version 51 changes
                                                    SharedPref.setUrlBaseServer(syncService.getUrl().getUrl_base(), getActivity());
                                                    SharedPref.setJobPoolUrlServer(syncService.getUrl().getUrlPoolListener(), getActivity());

                                                    //version 52
                                                    SharedPref.setUrlMobileAuth(syncService.getUrl().getUrlMobileAuth(), getActivity());
                                                    if (strUserDomain != null) {
                                                        dataAccessOperator.setUserDomain(strUserDomain);
                                                    }
                                                    // Dao.storeSynchParams();
                                                    // Dao.getSynchParams();
                                                    SharedPref.setIsNotLoggedInToday(false, getActivity());
                                                    firstSynch(strUserName, strToken);


                                                } else {
                                                    mPbChangePwd.setVisibility(View.GONE);
                                                    Toast.makeText(getActivity(), mobileAuth.getMsg(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MobileAuth> call, Throwable t) {
                                        mPbChangePwd.setVisibility(View.GONE);
                                        Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(retrofit2.Call<SyncService> call, Throwable t) {
                            mPbChangePwd.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    mPbChangePwd.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), R.string.MsgPasswordMisMatch, Toast.LENGTH_SHORT).show();

                }
            }
        } else {
            mPbChangePwd.setVisibility(View.GONE);
            if (!getActivity().isFinishing()) {
                SynchroteamUitls.showToastMessage(getActivity());
            }
        }
    }

    private void setTextToView() {
        edtChpDomain.setText(strUserDomain);
        edtChpUser.setText(strUserName);
    }

    private void getDatachangePwdBundle() {
        Bundle getchangePwdBundle = getArguments();
        if (getchangePwdBundle != null) {
            strUserDomain = getchangePwdBundle.getString("domain");
            strUserName = getchangePwdBundle.getString("user");

            if (!TextUtils.isEmpty(strUserDomain)) {
                edtChpDomain.setClickable(false);
                edtChpDomain.setFocusableInTouchMode(false);
                edtChpDomain.setFocusable(false);
            } else {
                edtChpDomain.setClickable(true);
                edtChpDomain.setFocusableInTouchMode(true);
                edtChpDomain.setFocusable(true);
            }
            if (!TextUtils.isEmpty(strUserName)) {
                edtChpUser.setClickable(false);
                edtChpUser.setFocusableInTouchMode(false);
                edtChpUser.setFocusable(false);
            } else {
                edtChpDomain.setClickable(true);
                edtChpDomain.setFocusableInTouchMode(true);
                edtChpDomain.setFocusable(true);
            }
        } else {
            strUserDomain = "";
            strUserName = "";
        }
    }

    private void setInitView(View viewChangePWd) {
        btnChangePwd = viewChangePWd.findViewById(R.id.btn_change_pwd);
        edtChpDomain = viewChangePWd.findViewById(R.id.txt_change_pwd_domain);
        edtChpUser = viewChangePWd.findViewById(R.id.txt_change_pwd_user);
        edtChpNewPwd = viewChangePWd.findViewById(R.id.txt_change_pwd_new_pwd);
        edtChpConfirmPwd = viewChangePWd.findViewById(R.id.txt_change_pwd_confirm_pwd);
        edtChpCurrentPwd = viewChangePWd.findViewById(R.id.txt_change_pwd_current_pwd);
        mLinLayClose = viewChangePWd.findViewById(R.id.lay_close);
        mPbChangePwd = viewChangePWd.findViewById(R.id.pb_change_pwd);
    }

    private void saveCertificate() {

        try {
            InputStream is = getResources().openRawResource(R.raw.root_cert);
            FileOutputStream os = getActivity().openFileOutput("root_cert.pem", MODE_PRIVATE);
            byte[] buff = new byte[4096];
            int n;
            for (; ; ) {
                n = is.read(buff);
                if (n < 0)
                    break;
                os.write(buff, 0, n);
            }
        } catch (Exception e) {

            Logger.printException(e);
        }
    }

    private void firstSynch(final String userName, final String password) {
        saveCertificate();
        Thread syncDatabaseWithServer = new Thread((new Runnable() {
            @Override
            public void run() {

                Message myMessage = new Message();
                try {
                    dataAccessOperator.syncx(userName, password, true);
                    myMessage.obj = "ok";
                    loginSynchroteamHandler.sendMessage(myMessage);
                } catch (Exception ex) {
                    getActivity().runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mPbChangePwd.setVisibility(View.GONE);
                            // Stuff that updates the UI

                        }
                    });

                    ex.printStackTrace();
                    String exception = ex.getMessage();
                    Logger.printException(ex);
                    if (exception != null) {
                        if (exception.contains("4006"))
                            myMessage.obj = "4006";
                        else if (exception.contains("4101"))
                            myMessage.obj = "4101";
                        else if (exception.contains("4005"))
                            myMessage.obj = "4005";
                        else if (exception.contains("4000"))
                            myMessage.obj = "4000";
                        else if (exception.contains("4001"))
                            myMessage.obj = "4001";
                        else if (exception.contains("4003"))
                            myMessage.obj = "4003";
                        else
                            myMessage.obj = "Error";
                    } else
                        myMessage.obj = "Error";
                    loginSynchroteamHandler.sendMessage(myMessage);
                } finally {
                }
            }
        }));
        syncDatabaseWithServer.start();
    }


    @SuppressLint("HandlerLeak")
    private Handler loginSynchroteamHandler = new Handler() {
        public void handleMessage(Message msg) {
            String erreur = (String) msg.obj;
            if (erreur.equals("ok")) {
                mPbChangePwd.setVisibility(View.GONE);
                //dataAccessOperator.setAuthToken1(userName, strToken, strExpiry);
                Crouton.makeText(getActivity(),
                        getString(R.string.msg_synch_ok), Style.ALERT).show();
                dataAccessOperator.setDateLogin(strUserName, strToken,user.getId());
                String prevAutoClockOut = dataAccessOperator.getAutoClockOutTime();
                if (prevAutoClockOut != null)
                    SharedPref.setPreviousTimeOut(prevAutoClockOut, getActivity());
                Intent isyncroteamNavigationIntent;
                isyncroteamNavigationIntent = new Intent(getActivity(),
                        SyncoteamNavigationActivity.class);
                isyncroteamNavigationIntent
                        .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(isyncroteamNavigationIntent);
                getActivity().finish();
            } else {
                mPbChangePwd.setVisibility(View.GONE);
                if (erreur.equals("4006")) {
                    showErrMsgDialog(getString(R.string.msg_synch_error_4));
                } else if (erreur.equals("4101")) {
                    showMultipleDeviecDialog();
                } else if (erreur.equals("4005")) {
                    showAppUpdatetDialog();
                } else if (erreur.equals("4003")) {
                    showErrMsgDialog(getString(R.string.msg_sync_error_4003));
                } else if (erreur.equals("4000") || erreur.equals("4001")) {
                    showErrMsgDialog(getString(R.string.msg_error_auth));
                } else {
                    if (erreur.equals("Error")) {
                        showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));
                    } else {
                        showErrMsgDialog(erreur);
                    }
                }
            }
        }
    };

    protected void showMultipleDeviecDialog() {

        MultipleDeviceNotSupported multipleDeviceDialog = new MultipleDeviceNotSupported(
                getActivity(),
                new MultipleDeviceNotSupported.MultipleDeviceInterface() {

                    @Override
                    public void doOnOkClick() {
                    }

                    @Override
                    public void doOnLinkClick() {
                        if (Locale.getDefault().getLanguage()
                                .equalsIgnoreCase("fr")) {
                            openLinkInBrowser(getString(R.string.txtInfoFr));
                        } else if (Locale.getDefault().getLanguage()
                                .equalsIgnoreCase("es")) {
                            openLinkInBrowser(getString(R.string.txtInfoEs));
                        }else {
                            openLinkInBrowser(getString(R.string.txtInfoEn));
                        }
                    }
                });
        multipleDeviceDialog.show();
    }

    protected void openLinkInBrowser(String link) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(link));

        // Always use string resources for UI text. This says something like
        // "Share this photo with"
        String title = getString(R.string.titleBrowserSelection);
        // Create and start the chooser
        Intent chooser = Intent.createChooser(intent, title);
        startActivity(chooser);
    }

    protected void showAppUpdatetDialog() {

        AppUpdateDialog appUpdateDialog = new AppUpdateDialog(getActivity());

        appUpdateDialog.show();
    }

    protected void showErrMsgDialog(String errMsg) {

        ErrorDialog errDialog = new ErrorDialog(getActivity(), errMsg);

        errDialog.show();
    }

    protected void showSyncFailureMsgDialog(String syncFailureMsg) {

        SynchronizationErrorDialog synchronizationErrorDialog = new SynchronizationErrorDialog
                (getActivity(), syncFailureMsg, new SynchronizationErrorDialog.SynchronizationErrorInterface() {
                    @Override
                    public void doOnOkayClick() {
                        //perform any action
                    }
                });

        synchronizationErrorDialog.show();
    }
}
