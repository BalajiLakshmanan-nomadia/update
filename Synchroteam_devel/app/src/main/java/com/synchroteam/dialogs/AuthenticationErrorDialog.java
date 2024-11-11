package com.synchroteam.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import androidx.core.content.IntentCompat;
import android.view.Gravity;
import android.view.View;

import com.synchroteam.dao.Dao;
import com.synchroteam.synchroteam.Login_Syncroteam;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DaoManager;

// TODO: Auto-generated Javadoc

/**
 * The Class AuthenticationErrorDialog is shown dialog alert for authentication
 * error while doing a sync after logged in.
 *
 * @author ${Ideavate Solution}
 */
public class AuthenticationErrorDialog extends Dialog {

    private Activity activity;
    private String userName;

    private Dao dao;
    /**
     * Instantiates a MultipleDeviceNotSupportedDialog
     *
     * @param activity the login syncroteam activity
     */
    public AuthenticationErrorDialog(Activity activity, String userName) {
        super(activity, android.R.style.Theme_Translucent_NoTitleBar);
        this.activity = activity;
        this.userName = userName;

        dao = DaoManager.getInstance();

        setCancelable(false);
        setContentView(R.layout.layout_auth_error_dialog);
        this.getWindow().setGravity(Gravity.CENTER);

        findViewById(R.id.okBtn).setOnClickListener(onClickListener);
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
                case R.id.okBtn:
                    //set pwd in T_USERS table to null...
              dao.clearPassword(userName);

                    //naviagate to login screen
                    Intent intent = new Intent(activity, Login_Syncroteam.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    //sdk 26 changes
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                    dismiss();
                    activity.startActivity(intent);
                    activity.finish();
                    break;
            }

        }
    };
}
