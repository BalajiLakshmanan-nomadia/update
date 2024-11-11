package com.synchroteam.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.synchroteam.beans.UpdateUiOnSync;
import com.synchroteam.fragmenthelper.CLientDetilsFragmentHelper;
import com.synchroteam.synchroteam.ClientDetail;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.KEYS;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc

/**
 * The Fragment Class to Show DiscrptionJobDetail Fragments.
 *
 * @author ${Ideavate Solution}
 */
public class ClientDetailFragment extends Fragment {

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */

    /**
     * The discription job detail fragment helper.
     */
    private CLientDetilsFragmentHelper cLientDetilsFragmentHelper;

    public static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 125;

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub

        Bundle bundle = getArguments();

        cLientDetilsFragmentHelper = new CLientDetilsFragmentHelper(
                (ClientDetail) getActivity(), bundle.getInt(KEYS.ClientDetial.ID_CLIENT), this);

        EventBus.getDefault().register(this);


        return cLientDetilsFragmentHelper.inflateLayout(inflater, container);

    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }


    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }


    /**
     * **
     * Function is called UpdateUiOnSync Event is fired through Event bus this is normally called when user is some other module and perform sync operation
     * than this event is fired by that module through Event bus to update the current client listing.
     *
     * @param updateUiOnSync the update ui on sync
     */

    public void onEvent(UpdateUiOnSync updateUiOnSync) {
        cLientDetilsFragmentHelper.doOnSyncronize();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE)
                            == PackageManager.PERMISSION_GRANTED)

                            cLientDetilsFragmentHelper.callingMethod();
                } else {

                }
                return;
            }

        }
    }


    public void callingPermissionPhone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CALL_PHONE)) {


            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle(getString(R.string.app_name));
            alertBuilder.setMessage(getString(R.string.str_phone_permission));
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CALL_PHONE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE);
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();

        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CALL_PHONE},
                    MY_PERMISSIONS_REQUEST_CALL_PHONE);
        }
    }

}
