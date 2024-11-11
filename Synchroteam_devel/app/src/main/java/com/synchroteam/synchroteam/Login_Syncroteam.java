package com.synchroteam.synchroteam;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.AsyncNotedAppOp;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.beans.User;
import com.synchroteam.customtoast.Crouton;
import com.synchroteam.customtoast.Style;
import com.synchroteam.dao.Dao;
import com.synchroteam.dialogs.AppUpdateDialog;
import com.synchroteam.dialogs.ChangePasswordDialog;
import com.synchroteam.dialogs.DomianInformationDialog;
import com.synchroteam.dialogs.ErrorDialog;
import com.synchroteam.dialogs.MultipleDeviceNotSupported;
import com.synchroteam.dialogs.SynchronizationErrorDialog;
import com.synchroteam.dialogs.WipeAllDetailDialog;
import com.synchroteam.retrofit.ApiInterface;
import com.synchroteam.retrofit.Apiclient;
import com.synchroteam.retrofit.models.mobileAuth.AuthData;
import com.synchroteam.retrofit.models.mobileAuth.AuthReq;
import com.synchroteam.retrofit.models.mobileAuth.MobileAuth;
import com.synchroteam.retrofit.models.syncservice.SyncService;
import com.synchroteam.roomDB.RoomDBSingleTone;
import com.synchroteam.roomDB.entity.NotificationEventModels;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.tracking.DaoTracking;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.DaoTrackingManager;
import com.synchroteam.utils.DateFormatUtils;
import com.synchroteam.utils.DialogUtils;
import com.synchroteam.utils.FourClickListener;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.SharedPref;
import com.synchroteam.utils.SynchroteamUitls;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
//v51.0.2
//import io.realm.Realm;
//import io.realm.RealmResults;
import androidx.core.app.NotificationCompat;
import io.reactivex.annotations.NonNull;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Class that Handles the validation of Login Data and Takes Care of the Login
 * Operation Of Application.
 * <p/>
 * created for future purpose
 *
 * @author Ideavate.solution
 */
public class Login_Syncroteam extends Activity {

    /**
     * The progress db.
     */
    protected ProgressDialog progressDB;

    /**
     * The domain.
     */
    private String userName, password, domain, strTokenTemp, strToken;
    String strExpiry, strExpiryTemp;

    /**
     * The data access operator.
     */
    private Dao dataAccessOperator;

    /**
     * The pass word et.
     */
    private EditText domainEt, userNameEt, passWordEt;

    /**
     * The is reset selected.
     */
    private boolean isResetSelected;

    /**
     * The dao tracking.
     */
    private DaoTracking daoTracking;

    /**
     * User object
     */
    private User user;
    private TextView tv_show_change_pwd_dialog;

    private NotificationManager mNotificationManager;

    /**
     * Logged in status.
     */
    // private boolean isLoggedInToday;

    private static final String TAG = "Login_Syncroteam";

    private boolean isDebugEnabled;

    private RelativeLayout relParentView;
    String authToken = "";
    // private static final String TAG = "Login_Syncroteam";
    private  int RES_IMAGE = 100;
    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setContentView(R.layout.layout_syncoteamlower_login);
        } else {
            setContentView(R.layout.layout_syncoteam_login);
        }

        dataAccessOperator = DaoManager.getInstance();
        daoTracking = DaoTrackingManager.getInstance();
        Logger.log("Login Syncroteam", "On Create");

        initialiseView();
        performActionBasedOnLastSync();
        String domainLocal = domainEt.getText().toString().trim();
        String userLocal = userNameEt.getText().toString().trim();
        if (TextUtils.isEmpty(domainLocal) && TextUtils.isEmpty(userLocal)) {
            tv_show_change_pwd_dialog.setVisibility(View.GONE);
        } else {
            tv_show_change_pwd_dialog.setVisibility(View.VISIBLE);
            tv_show_change_pwd_dialog.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog().instance(domainEt.getText().toString(),
                            userNameEt.getText().toString());
                    changePasswordDialog.show(getFragmentManager(), "ChangePasswordDialog");


                }
            });
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */

    /**
     * * Perform Ui Actions By checking Last Sync values
     */
    private void performActionBasedOnLastSync() {
        if (dataAccessOperator != null)
            if (!dataAccessOperator.checkSynch().equals("")) {

                      User   userBean = dataAccessOperator.getUser();
                        String domain = dataAccessOperator.getUserDomain();

                if (userBean != null && domain != null) {
                    userNameEt.setText(userBean.getLogin());
                    userNameEt.setEnabled(false);
                    userNameEt.setFocusable(false);
                    domainEt.setText(domain);
                    domainEt.setEnabled(false);
                    domainEt.setFocusable(false);
                }
                // SharedPref.setIsNotLoggedInToday(true, Login_Syncroteam.this);
                    }
            }


    /**
     * Initialise view.
     */
    private void initialiseView() {
        domainEt = (EditText) findViewById(R.id.domainLoginEt);
        userNameEt = (EditText) findViewById(R.id.userNameLoginEt);
        passWordEt = (EditText) findViewById(R.id.passwordLoginEt);
        relParentView = (RelativeLayout) findViewById(R.id.relParentView);
        tv_show_change_pwd_dialog = findViewById(R.id.txt_show_change_pwd_dialog);
        String domainLocal = domainEt.getText().toString().trim();
        String userLocal = userNameEt.getText().toString().trim();
        if (TextUtils.isEmpty(domainLocal) && TextUtils.isEmpty(userLocal)) {
            tv_show_change_pwd_dialog.setVisibility(View.GONE);
        } else {
            tv_show_change_pwd_dialog.setVisibility(View.VISIBLE);
        }
        tv_show_change_pwd_dialog.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog().instance(domainEt.getText().toString(),
                        userNameEt.getText().toString());
                changePasswordDialog.show(getFragmentManager(), "ChangePasswordDialog");


            }
        });
        /*
         * new changes.
         */
        relParentView.setOnClickListener(new FourClickListener() {

            @Override
            public void onFourthClick(View v) {

                savePreferences();

            }
        });

        findViewById(R.id.loginBt).setOnClickListener(onClickListener);
        findViewById(R.id.wipeAllDetailsButton).setOnClickListener(
                onClickListener);
        findViewById(R.id.domainInformationIcon).setOnClickListener(
                onClickListener);

        mNotificationManager = (NotificationManager) this
                .getSystemService(this.NOTIFICATION_SERVICE);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
    }


    /**
     * @param folder
     * @param fileList
     * @param fileType
     */
    private void readFilesfromExternalPath(File folder,
                                           ArrayList<String> fileList, final String fileType) {
        if (folder == null) {
            // folder = Environment.getExternalStorageDirectory();
            folder = new File(getApplicationInfo().dataDir);
        }

        try {
            if (folder.exists() && !folder.isHidden()) {

                File[] list = folder.listFiles();

                for (final File fileEntry : list) {
                    if (fileEntry.isDirectory() && !fileEntry.isHidden()) {
                        readFilesfromExternalPath(fileEntry, fileList, fileType);
                    } else
                        /*
                         * if (!fileEntry.isHidden() &&
                         * (fileEntry.getName().endsWith(fileType) ||
                         * fileEntry.getName().endsWith( fileType.toUpperCase()) ||
                         * fileEntry .getName().endsWith(fileType.toLowerCase())))
                         */ {
                        fileList.add(fileEntry.getAbsolutePath());

                        String filePath = fileEntry.getAbsolutePath();

                        String dbDaoFileName = Dao.dbName.split("\\.")[0];
                        String dbDaoTrackingFileName = DaoTracking.dbName
                                .split("\\.")[0];

                        if (filePath.contains(dbDaoFileName)) {
                            File file = new File(fileEntry.getAbsolutePath());
                            file.delete();
                            Log.e("Deleted file ",
                                    "" + fileEntry.getAbsolutePath());
                        } else if (filePath.contains(dbDaoTrackingFileName)) {
                            File file = new File(fileEntry.getAbsolutePath());
                            file.delete();
                            Log.e("Deleted file ",
                                    "" + fileEntry.getAbsolutePath());
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * The on click listener.
     */
    OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.loginBt) {

                // Validation check
                if ((domainEt.getText().toString().trim().length() == 0)
                        || (userNameEt.getText().toString().trim().length() == 0)
                        || (passWordEt.getText().toString().trim().length() == 0)) {
                    Crouton.makeText(Login_Syncroteam.this,
                            getString(R.string.text_enter_all_values),
                            Style.ALERT).show();
                } else {
                    login();
                }
            } else if (id == R.id.domainInformationIcon) {
                DomianInformationDialog domianInformationDialog = new DomianInformationDialog(
                        Login_Syncroteam.this);
                domianInformationDialog.show();
            } else if (id == R.id.wipeAllDetailsButton) {

                WipeAllDetailDialog wipeAllDetailDialog = new WipeAllDetailDialog(
                        Login_Syncroteam.this,
                        new WipeAllDetailDialog.WipeAllDetailInterface() {

                            @Override
                            public void doOnYesClick() {

                                // File daoDBFilePath =
                                // getDatabasePath(Dao.dbName);
                                // String dbPath =
                                // getApplicationInfo().dataDir;// +
                                // // "/databases";
                                // // File dbFile = new File(dbPath + "/" +
                                // // Dao.dbName);
                                // File dbFile = new File(dbPath);
                                //
                                // ArrayList<String> fileListPath = new
                                // ArrayList<String>();
                                // readFilesfromExternalPath(dbFile,
                                // fileListPath,
                                // "");
                                // Log.e("", "" + fileListPath.size());

                                isResetSelected = true;
                                dataAccessOperator.setUserScript("ForceDelete");

                                userNameEt.setText("");
                                domainEt.setText("");

                                try {
                                    WipeAllNotificationClearAlert();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Logger.printException(e);
                                }

                                Intent intent = new Intent(
                                        Login_Syncroteam.this,
                                        SpalshActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                                finish();

                            }

                            @Override
                            public void doOnNoClick() {

                            }
                        });
                wipeAllDetailDialog.show();

            }

        }
    };
//    public void WipeAllNotificationClearAlert() {
//        Realm realm = Realm.getDefaultInstance();
//        realm.executeTransactionAsync(new Realm.Transaction() {
//            @Override
//            public void execute(Realm realm) {
//                RealmResults<NotificationEventModels> results = realm.where(NotificationEventModels.class).findAll();
//                results.deleteAllFromRealm();
//            }
//        }, new Realm.Transaction.OnSuccess() {
//            @Override
//            public void onSuccess() {
//                Logger.log("TAG", "Cleared local notification DB");
//            }
//        });
//    }

    public void WipeAllNotificationClearAlert() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<NotificationEventModels> l = new ArrayList<>();
                l = RoomDBSingleTone.instance(Login_Syncroteam.this).roomDao().getAllNotificationEventModels();
                Log.e("lis", l.toString());
                RoomDBSingleTone.instance(Login_Syncroteam.this).roomDao().deteteAllNotificationEventModels();
            }
        }).start();
    }


    /**
     * Login.
     */
    protected void login() {

        userName = userNameEt.getText().toString().trim();

        password = passWordEt.getText().toString().trim();

        domain = domainEt.getText().toString().trim().toLowerCase();

        // isLoggedInToday = getIntent().getBooleanExtra(
        // KEYS.JObDetail.IS_LOGGED_IN_TODAY, true);

        domain = domain.replace("https:\\\\", "");
        domain = domain.replace("http:\\\\", "");
        domain = domain.replace(".synchroteam.com", "");

        SharedPref.setLoginUser(userName,Login_Syncroteam.this);
        final AuthData[] getAuthandExpriryToken = new AuthData[1];
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                user = dataAccessOperator.getUser();
                 getAuthandExpriryToken[0] = dataAccessOperator.getUserToken();

            }
        });


//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(new Date());
//        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
//        Date time = calendar.getTime();
//        @SuppressLint("SimpleDateFormat") SimpleDateFormat outputFmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSZ");
//        outputFmt.setTimeZone(TimeZone.getTimeZone("UTC"));
//
//        String fv = outputFmt.format(time);
        if (SynchroteamUitls.isNetworkAvailable(Login_Syncroteam.this)) {


            if (getAuthandExpriryToken[0] != null && user != null) {

                boolean isExpiry = DateFormatUtils.getFormattedDateFromAPIDateBase(getAuthandExpriryToken[0].getExpiry());
                if (isExpiry) {
                    if (SynchroteamUitls.isNetworkAvailable(Login_Syncroteam.this)) {
                        hitSyncServerForPreviousUser();
                    } else {
                        if (checkLinkValuesFromPref())
                            doActionForPreviousUser();
                        else
                            SynchroteamUitls.showToastMessage(Login_Syncroteam.this);
                    }
                } else {
                    hitSyncServerForFirstTime();
                }
            } else {
                hitSyncServerForFirstTime();
            }
        } else {
            SynchroteamUitls.showToastMessage(Login_Syncroteam.this);
        }
        // if (dataAccessOperator.checkSynch().equals("") || user.getPwd().equals("")) { v52.0.0 authoken
//        if (dataAccessOperator.checkSynch().equals("")) {
//            if (SynchroteamUitls.isNetworkAvailable(Login_Syncroteam.this)) {
//
//
//                // Code for Prod
//                hitSyncServerForFirstTime();
//
//                // code to work in devel
////                dataAccessOperator.setUserDomain(domain);
////                SharedPref.setIsNotLoggedInToday(false, Login_Syncroteam.this);
////                firstSynch(userName, password);
//
//            } else {
//                SynchroteamUitls.showToastMessage(Login_Syncroteam.this);
//            }
//        } else {
//            // if user login was a prvious user than he goes in this loop
//            if (user.getLogin().equals(userName) ) {
//              //  if (user.getLogin().equals(userName) && user.getPwd().equals(password)) {v52.0.0 authoken
//                if (SynchroteamUitls.isNetworkAvailable(Login_Syncroteam.this)) {
//
//                    hitSyncServerForPreviousUser();
//
//                } else {
//                    if (checkLinkValuesFromPref())
//                        doActionForPreviousUser();
//                    else
//                        SynchroteamUitls.showToastMessage(Login_Syncroteam.this);
//                }
//
//                // code to work in devel
////                doActionForPreviousUser();
//
//            }
//            else if (!user.getLogin().equals(userName)
//                    && !user.getPwd().equals(password)) {
////                Crouton.makeText(Login_Syncroteam.this,
////                                 getString(R.string.msg_error_auth), Style.ALERT).show();
//                showSyncFailureMsgDialog(getString(R.string.msg_error_auth));
//                userNameEt.setText("");
//                passWordEt.setText("");
//                userNameEt.requestFocus();
//            } else if (!user.getLogin().equals(userName)) {
////                Crouton.makeText(Login_Syncroteam.this,
////                                 getString(R.string.msg_error_login) + " !", Style.ALERT)
////                        .show();
//                showSyncFailureMsgDialog(getString(R.string.msg_error_login));
//                userNameEt.setText("");
//                userNameEt.requestFocus();
//            } else if (!user.getPwd().equals(password)) {
////                Crouton.makeText(Login_Syncroteam.this,
////                                 getString(R.string.msg_error_pwd) + "!", Style.ALERT).show();
//                showSyncFailureMsgDialog(getString(R.string.msg_error_pwd));
//                passWordEt.setText("");
//                passWordEt.requestFocus();
//            }
        // }
    }

    /**
     * hitting sync server service for first time login
     */
    private void hitSyncServerForFirstTime() {

        DialogUtils.showProgressDialog(Login_Syncroteam.this,
                getString(R.string.textPleaseWaitLable),
                getString(R.string.msg_synch), false, true);

        final ApiInterface apiService =
                Apiclient.getClient().create(ApiInterface.class);

        Call<SyncService> call = apiService.synchronizeServer(domain);

        call.enqueue(new Callback<SyncService>() {
            @Override
            public void onResponse(@NotNull Call<SyncService> call, @NotNull Response<SyncService> response) {

                try {
                    if (response.isSuccessful()) {
                        SyncService syncService = response.body();

                    }

                    // Saving Server, port, ssl from Response to Shared Preferences
                    assert response.body() != null;
                    SharedPref.setSyncStdServer(response.body().getStd().getServer(), Login_Syncroteam.this);
                    SharedPref.setSyncStdPort(response.body().getStd().getPort(), Login_Syncroteam.this);
                    SharedPref.setSyncStdSsl(response.body().getStd().getSsl(), Login_Syncroteam.this);

                    SharedPref.setSyncDebugServer(response.body().getDebug().getServer(), Login_Syncroteam.this);
                    SharedPref.setSyncDebugPort(response.body().getDebug().getPort(), Login_Syncroteam.this);
                    SharedPref.setSyncDebugSsl(response.body().getDebug().getSsl(), Login_Syncroteam.this);

                    //version 50 changes
                    SharedPref.setUrlStripeServer(response.body().getUrl().getUrlStripe(), Login_Syncroteam.this);
                    SharedPref.setNotiUrlServer(response.body().getUrl().getUrlEventListener(), Login_Syncroteam.this);

                    //version 51 changes
                    SharedPref.setUrlBaseServer(response.body().getUrl().getUrl_base(), Login_Syncroteam.this);
                    SharedPref.setJobPoolUrlServer(response.body().getUrl().getUrlPoolListener(), Login_Syncroteam.this);

                    //version 52
                    SharedPref.setUrlMobileAuth(response.body().getUrl().getUrlMobileAuth(), Login_Syncroteam.this);
                    AuthData getAuthandExpriryToken = dataAccessOperator.getUserToken();
                    if (getAuthandExpriryToken != null) {
                        boolean isExpiry = DateFormatUtils.getFormattedDateFromAPIDateBase(getAuthandExpriryToken.getExpiry());
                        if (isExpiry) {
                            User userBean = dataAccessOperator.getUser();
                            dataAccessOperator.setDateLogin(userName,
                                    getAuthandExpriryToken.getAuth(),userBean.getId());

                            Intent isyncroteamNavigationIntent;

                            isyncroteamNavigationIntent = new Intent(Login_Syncroteam.this,
                                    SyncoteamNavigationActivity.class);
                            isyncroteamNavigationIntent
                                    .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(isyncroteamNavigationIntent);
                            Login_Syncroteam.this.finish();
                        } else {
                            AuthReq mod = new AuthReq();
                            mod.setDomain(domainEt.getText().toString());
                            mod.setPassword(passWordEt.getText().toString());
                            mod.setUserId(userNameEt.getText().toString());
                            Call<MobileAuth> mobileAuthCall = apiService.getMobAuth(SharedPref.getUrlMobileAuth(Login_Syncroteam.this), mod);
                            mobileAuthCall.enqueue(new Callback<MobileAuth>() {
                                @Override
                                public void onResponse(@NotNull Call<MobileAuth> call, @NotNull Response<MobileAuth> response) {
                                    if (response.isSuccessful()) {
                                        MobileAuth mobileAuth = response.body();
                                        assert mobileAuth != null;
                                        strToken = mobileAuth.getData().getAuthToken();
                                        // strExpiry=mobileAuth.getData().getAuthExpiry();
                                        String authExpiry = mobileAuth.getData().getAuthExpiry();
                                        if (authExpiry != null) {
                                            strExpiry = DateFormatUtils.getFormattedDateFromAPIDate(authExpiry);
                                        }
                                        if (mobileAuth.getResult() == 1) {
                                            // doing first time sync
                                            if (domain != null) {
                                                dataAccessOperator.setUserDomain(domain);
                                            }                                            // Dao.storeSynchParams();
                                            // Dao.getSynchParams();
                                            SharedPref.setIsNotLoggedInToday(false, Login_Syncroteam.this);
                                            new Handler().post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    firstSynch(userName, strToken);
                                                }
                                            });

                                        } else {
                                            DialogUtils.dismissProgressDialog();

//                                            Toast.makeText(Login_Syncroteam.this, mobileAuth.getMsg(), Toast.LENGTH_SHORT).show();
                                            if (mobileAuth.getResult() == 0) {
                                                if (mobileAuth.getCode().equals("4000")) {
                                                    showErrMsgDialog(getString(R.string.msg_synch_error_4));
//                                                    Toast.makeText(Login_Syncroteam.this, mobileAuth.getCode(), Toast.LENGTH_SHORT).show();
                                                } else {
                                                    showErrMsgDialog(getString(R.string.msg_error_auth));
                                                }
                                            } else {
                                                showErrMsgDialog(getString(R.string.msg_error_auth));
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(@NotNull Call<MobileAuth> call, @NotNull Throwable t) {
                                    DialogUtils.dismissProgressDialog();
                                    showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));
                                }
                            });
                        }
                    } else {
                        AuthReq mod = new AuthReq();
                        mod.setDomain(domain);
                        mod.setPassword(password);
                        mod.setUserId(userName);
                        Call<MobileAuth> mobileAuthCall = apiService.getMobAuth(SharedPref.getUrlMobileAuth(Login_Syncroteam.this), mod);
                        mobileAuthCall.enqueue(new Callback<MobileAuth>() {
                            @Override
                            public void onResponse(@NotNull Call<MobileAuth> call, @NotNull Response<MobileAuth> response) {
                                if (response.isSuccessful()) {
                                    MobileAuth mobileAuth = response.body();
                                    assert mobileAuth != null;
                                    strToken = mobileAuth.getData().getAuthToken();
                                    // strExpiry=mobileAuth.getData().getAuthExpiry();
                                    String authExpiry = mobileAuth.getData().getAuthExpiry();
                                    if (authExpiry != null) {
                                        strExpiry = DateFormatUtils.getFormattedDateFromAPIDate(authExpiry);
                                    }
                                    if (mobileAuth.getResult() == 1) {
                                        DialogUtils.dismissProgressDialog();

                                        // doing first time sync
                                        if (domain != null) {
                                            dataAccessOperator.setUserDomain(domain);
                                        }
                                        // Dao.storeSynchParams();
                                        // Dao.getSynchParams();
                                        SharedPref.setIsNotLoggedInToday(false, Login_Syncroteam.this);
//
                                        new Handler().post(new Runnable() {
                                            @Override
                                            public void run() {
                                                firstSynch(userName, strToken);
                                            }
                                        });

                                    } else {
                                        DialogUtils.dismissProgressDialog();

                                        if (mobileAuth.getResult() == 0) {
                                            if (mobileAuth.getCode().equals("4000")) {
                                                showErrMsgDialog(getString(R.string.msg_synch_error_4));
//                                                Toast.makeText(Login_Syncroteam.this, mobileAuth.getCode(), Toast.LENGTH_SHORT).show();
                                            } else {
                                                showErrMsgDialog(getString(R.string.msg_error_auth));
                                            }
                                        } else {
                                            showErrMsgDialog(getString(R.string.msg_error_auth));
                                        }
//                                        Toast.makeText(Login_Syncroteam.this, mobileAuth.getMsg(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<MobileAuth> call, @NotNull Throwable t) {
                                DialogUtils.dismissProgressDialog();

                                showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));
                            }
                        });
                    }
                    // dismiss dialog


                } catch (Exception e) {
                    // dismiss dialog
                    DialogUtils.dismissProgressDialog();

                    showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));
                }

            }

            @Override
            public void onFailure(@NotNull Call<SyncService> call, @NotNull Throwable t) {
//                        Logger.log("SyncFirst", "Retrofit failure :" + t);

                // dismiss dialog
                DialogUtils.dismissProgressDialog();

                showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));
            }
        });
    }

    /**
     * if user login was a previous user then call this method
     * <p>
     * This is previous user logging in at the next day
     */
    private void hitSyncServerForPreviousUser() {

        DialogUtils.showProgressDialog(Login_Syncroteam.this,
                getString(R.string.textPleaseWaitLable),
                getString(R.string.msg_synch), false, true);

        ApiInterface apiService =
                Apiclient.getClient().create(ApiInterface.class);

        Call<SyncService> call = apiService.synchronizeServer(domain);
        call.enqueue(new Callback<SyncService>() {
            @Override
            public void onResponse(@NotNull Call<SyncService> call, @NotNull Response<SyncService> response) {

                try {

                    // Saving Server, port, ssl from Response to Shared Preferences
                    SharedPref.setSyncStdServer(response.body().getStd().getServer(), Login_Syncroteam.this);
                    SharedPref.setSyncStdPort(response.body().getStd().getPort(), Login_Syncroteam.this);
                    SharedPref.setSyncStdSsl(response.body().getStd().getSsl(), Login_Syncroteam.this);

                    SharedPref.setSyncDebugServer(response.body().getDebug().getServer(), Login_Syncroteam.this);
                    SharedPref.setSyncDebugPort(response.body().getDebug().getPort(), Login_Syncroteam.this);
                    SharedPref.setSyncDebugSsl(response.body().getDebug().getSsl(), Login_Syncroteam.this);

                    //version 50 changes
                    SharedPref.setUrlStripeServer(response.body().getUrl().getUrlStripe(), Login_Syncroteam.this);
                    SharedPref.setNotiUrlServer(response.body().getUrl().getUrlEventListener(), Login_Syncroteam.this);

                    //version 51 changes
                    SharedPref.setUrlBaseServer(response.body().getUrl().getUrl_base(), Login_Syncroteam.this);
                    SharedPref.setJobPoolUrlServer(response.body().getUrl().getUrlPoolListener(), Login_Syncroteam.this);

                    //version 52 changes
                    SharedPref.setUrlMobileAuth(response.body().getUrl().getUrlMobileAuth(), Login_Syncroteam.this);

                    // dismiss dialog
                    DialogUtils.dismissProgressDialog();


                    doActionForPreviousUser();


                } catch (Exception e) {
                    e.printStackTrace();

                    DialogUtils.dismissProgressDialog();

                    if (checkLinkValuesFromPref()) {

                        doActionForPreviousUser();

                    } else {
                        showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));
                    }

                }

            }

            @Override
            public void onFailure(@NotNull Call<SyncService> call, @NotNull Throwable t) {
//                        Logger.log("SyncPrev", "Retrofit failure :" + t);

                // dismiss dialog
                DialogUtils.dismissProgressDialog();

                if (checkLinkValuesFromPref()) {
                    doActionForPreviousUser();
                } else {
                    showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));
                }
            }
        });
    }

    private void doActionForPreviousUser() {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // doing previous user sync
                AuthData getAuthandExpriryToken = dataAccessOperator.getUserToken();
                User userBean = dataAccessOperator.getUser();
                dataAccessOperator.setDateLogin(userName, getAuthandExpriryToken.getAuth(),userBean.getId());
                // closeDb();
            }
        });

        Intent syncroteamNavigationActivityIntent;
        // if(domain.contains("dalkia"))
        // i = new Intent(Login.this, AccueilDalkia.class);
        // else

        // if (dataAccessOperator.checkDateLogin()) {
        // SharedPref.setIsNotLoggedInToday(false,
        // Login_Syncroteam.this);
        // } else {
        // SharedPref.setIsNotLoggedInToday(true,
        // Login_Syncroteam.this);
        // }

        syncroteamNavigationActivityIntent = new Intent(
                Login_Syncroteam.this,
                SyncoteamNavigationActivity.class);

        // syncroteamNavigationActivityIntent.putExtra(
        // KEYS.JObDetail.IS_LOGGED_IN_TODAY, isLoggedInToday);
        startActivity(syncroteamNavigationActivityIntent);
        finish();
    }


    /**
     * First synch.
     *
     * @param userName the user name
     * @param password the password
     */
    private void firstSynch(final String userName, final String password) {

        saveCertificate();
        DialogUtils.showProgressDialog(Login_Syncroteam.this,
                getString(R.string.textPleaseWaitLable),
                getString(R.string.msg_synch), false, true);

        Thread syncDatabaseWithServer = new Thread((new Runnable() {
            @Override
            public void run() {

                Message myMessage = new Message();
                try {
                    SharedPref.setLoginUser(userName,Login_Syncroteam.this);
                    dataAccessOperator.syncx(userName, password, true);
                    myMessage.obj = "ok";


                    loginSynchroteamHandler.sendMessage(myMessage);
                } catch (Exception ex) {
//                    ex.printStackTrace();
                    Logger.log("TAG",""+ex);
                    String exception = ex.getMessage();
                    Logger.printException(ex);
                    if (exception != null) {

                        if (!isDebugEnabled) {
                            if (exception.indexOf("4006") != -1)
                                myMessage.obj = "4006";
                            else if (exception.indexOf("4101") != -1)
                                myMessage.obj = "4101";
                            else if (exception.indexOf("4005") != -1)
                                myMessage.obj = "4005";
                            else if (exception.indexOf("4000") != -1)
                                myMessage.obj = "4000";
                            else if (exception.indexOf("4001") != -1)
                                myMessage.obj = "4001";
                            else if (exception.indexOf("4003") != -1)
                                myMessage.obj = "4003";
                            else
                                myMessage.obj = "Error";
                        } else {
                            myMessage.obj = exception;
                        }
                    } else
                        myMessage.obj = "Error";
                    loginSynchroteamHandler.sendMessage(myMessage);
                } finally {
                    DialogUtils.dismissProgressDialog();

                }

                isDebugEnabled = false;

            }

        }));
        syncDatabaseWithServer.start();
    }


    /**
     * Save certificate.
     */
    private void saveCertificate() {

        try {
            InputStream is = getResources().openRawResource(R.raw.root_cert);
            FileOutputStream os = openFileOutput("root_cert.pem", MODE_PRIVATE);
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

    /**
     * this handlers handleMessage is called when user first login into the
     * application or first login after wipe all details this handler checks the
     * atatus code returned after first sync if status code is ok it redirects
     * the user to dash board.or show appropriate message.
     */
    @SuppressLint("HandlerLeak")
    private Handler loginSynchroteamHandler = new Handler() {
        public void handleMessage(Message msg) {

            String erreur = (String) msg.obj;
            if (erreur.equals("ok")) {
                User userBean = dataAccessOperator.getUser();
                dataAccessOperator.setAuthToken1(userName, strToken, strExpiry,userBean.getId());
                Crouton.makeText(Login_Syncroteam.this,
                        getString(R.string.msg_synch_ok), Style.ALERT).show();
                dataAccessOperator.setDateLogin(userName, strToken,userBean.getId());

//                new changes
                String prevAutoClockOut = dataAccessOperator.getAutoClockOutTime();
                if (prevAutoClockOut != null)
                    SharedPref.setPreviousTimeOut(prevAutoClockOut, Login_Syncroteam.this);

                // closeDb();
                Intent isyncroteamNavigationIntent;
                // if(domain.contains("dalkia"))
                // i = new Intent(Login_Syncroteam.this, AccueilDalkia.class);
                // else
                isyncroteamNavigationIntent = new Intent(Login_Syncroteam.this,
                        SyncoteamNavigationActivity.class);
                isyncroteamNavigationIntent
                        .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(isyncroteamNavigationIntent);

                Login_Syncroteam.this.finish();
            } else {
                if (erreur.equals("4006")) {
                    // Crouton.makeText(Login_Syncroteam.this,
                    // getString(R.string.msg_synch_error_4), Style.ALERT)
                    // .show();
                    showErrMsgDialog(getString(R.string.msg_synch_error_4));
                } else if (erreur.equals("4101")) {
                    // Crouton.makeText(Login_Syncroteam.this,
                    // getString(R.string.msg_synch_error_2), Style.ALERT)
                    // .show();
                    showMultipleDeviecDialog();
                } else if (erreur.equals("4005")) {
                    // Crouton.makeText(Login_Syncroteam.this,
                    // getString(R.string.msg_synch_error_3), Style.ALERT)
                    // .show();

                    showAppUpdatetDialog();
                } else if (erreur.equals("4003")) {
                    showErrMsgDialog(getString(R.string.msg_sync_error_4003));
                } else if (erreur.equals("4000")) {  //new changes V53
                    // Crouton.makeText(Login_Syncroteam.this,
                    // getString(R.string.msg_error_auth), Style.ALERT)
                    // .show();
                    showErrMsgDialog(getString(R.string.msg_synch_error_4));
                } else if (erreur.equals("4001")) {
                    // Crouton.makeText(Login_Syncroteam.this,
                    // getString(R.string.msg_error_auth), Style.ALERT)
                    // .show();
                    showErrMsgDialog(getString(R.string.msg_error_auth));

                } else {
                    if (erreur.equals("Error")) {
                        // Crouton.makeText(Login_Syncroteam.this,
                        // getString(R.string.msg_synch_error_new),
                        // Style.ALERT).show();
                        showSyncFailureMsgDialog(getString(R.string.msg_synch_error_new));
                    } else {
                        showErrMsgDialog(erreur);
                        // Toast.makeText(Login_Syncroteam.this, erreur,
                        // Toast.LENGTH_LONG).show();
                    }
                }

//                passWordEt.setText("");
//
//                if (user == null) {
//                    userNameEt.setText("");
//                    domainEt.setText("");
//                }

                domainEt.requestFocus();
            }
        }
    };

    /**
     * Show multiple user dialog
     */
    protected void showMultipleDeviecDialog() {

        MultipleDeviceNotSupported multipleDeviceDialog = new MultipleDeviceNotSupported(
                Login_Syncroteam.this,
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

    /**
     * Show error dialog
     */
    protected void showErrMsgDialog(String errMsg) {

        ErrorDialog errDialog = new ErrorDialog(Login_Syncroteam.this, errMsg);

        errDialog.show();
    }

    /**
     * For showing the synchronization failure messages
     */
    protected void showSyncFailureMsgDialog(String syncFailureMsg) {

        SynchronizationErrorDialog synchronizationErrorDialog = new SynchronizationErrorDialog
                (Login_Syncroteam.this, syncFailureMsg, new SynchronizationErrorDialog.SynchronizationErrorInterface() {
                    @Override
                    public void doOnOkayClick() {
                        //perform any action
                    }
                });

        synchronizationErrorDialog.show();
    }

    /**
     * Show app update dialog
     */
    protected void showAppUpdatetDialog() {

        AppUpdateDialog appUpdateDialog = new AppUpdateDialog(Login_Syncroteam.this);

        appUpdateDialog.show();
    }

    /***
     * Create a chooser of browsers to open the link
     *
     * @param link
     */
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

    /*
     * (non-Javadoc)
     *
     * @see android.support.v7.app.AppCompatActivity#onStop()
     */
    @SuppressWarnings("static-access")
    protected void onStop() {

        Logger.log("LoginSyncroteam", "onStop Called");

        if (isResetSelected) {
            dataAccessOperator.closeDatabase();
            daoTracking.closeDatabase();
            DaoManager.resetDao();
            DaoTrackingManager.resetDaoTracking();
            dataAccessOperator = null;
            daoTracking = null;
            SharedPref.clearData(Login_Syncroteam.this);
            NotificationManager mNotificationManager = (NotificationManager) this
                    .getSystemService(this.NOTIFICATION_SERVICE);
            mNotificationManager.cancel(1986);

        }

        super.onStop();
    }

    ;

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onDestroy()
     */
    protected void onDestroy() {
        Logger.log("LoginSyncroteam", "onDestroy Called");

        Crouton.cancelAllCroutons();

        if (!isFinishing() && progressDB != null && progressDB.isShowing()) {
            progressDB.dismiss();
        }
        super.onDestroy();

    }

    ;

    /**
     * Save preferences.
     */
    private void savePreferences() {
        boolean isDebugEnabled = SharedPref.getDebugEnabled(this);

        if (!isDebugEnabled) {
            SharedPref.setDebugEnableSync(true, this);
            this.isDebugEnabled = true;
            Toast.makeText(this, this.getString(R.string.txt_debug_enabled),
                    Toast.LENGTH_SHORT).show();
        } else {
            SharedPref.setDebugEnableSync(false, this);
            this.isDebugEnabled = false;
            Toast.makeText(this, this.getString(R.string.txt_debug_disabled),
                    Toast.LENGTH_SHORT).show();
        }

    }

    private Boolean checkLinkValuesFromPref() {
        return SharedPref.getSyncStdServer(Login_Syncroteam.this) != null && SharedPref.getSyncStdPort(Login_Syncroteam.this) != 0;
    }

}