package com.synchroteam.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.synchroteam.beans.SharedBlocks;
import com.synchroteam.beans.UpdateJobDetailUi;
import com.synchroteam.beans.UpdateUiAfterSync;
import com.synchroteam.dialogs.ReportMultipleTextiItemDialog;
import com.synchroteam.dialogs.SharedBlockListDialog;
import com.synchroteam.fragmenthelper.JobDetailReportsFragmentHelper;
import com.synchroteam.fragmenthelper.ReportsJobDetailFragmentHelper;
import com.synchroteam.fragmenthelper.ReportsJobDetailFragmentHelperNew;
import com.synchroteam.synchroteam.AddNewJob;
import com.synchroteam.synchroteam.NewJob;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.technicalsupport.JobDetails;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.RequestCode;

import de.greenrobot.event.EventBus;

// TODO: Auto-generated Javadoc

/**
 * The Fragment Class to Reports Fragment in Job Details Fragments.
 *
 * @author Ideavate Solution
 */
public class ReportsJobDetailFragment extends Fragment implements SharedBlockListDialog.SelectionListener, ReportMultipleTextiItemDialog.MultipleDataSaveListener {

	/*
     * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */

    // Bundle bundle=new Bundle();
    // bundle.putInt("id_model",idModel);
    // bundle.putString("id_interv",idIntervention);
    // bundle.putInt("cd_statut",status);
    // bundle.putInt("id_user",idUser);
    // bundle.putInt("idClient",idClient);
    // bundle.putInt("idSite",idSite);
    // bundle.putInt("idEquipement",idEquipement);

    /**
     * The reports job detail fragment helper.
     */
    JobDetailReportsFragmentHelper reportsJobDetailFragmentHelper;

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_ADAPTER = 124;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 125;
    public static final int CAMERA_PERMISSION = 126;


    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    Toast.makeText(getActivity(),"Permission allowed",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(),"Permission denied",Toast.LENGTH_SHORT).show();
                }
            });
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

        reportsJobDetailFragmentHelper = new JobDetailReportsFragmentHelper (
                (JobDetails) getActivity(), bundle.getInt(KEYS.CurrentJobs.ID_MODEL),
                bundle.getString(KEYS.CurrentJobs.ID), bundle.getInt(KEYS.CurrentJobs.CD_STATUS),
                bundle.getInt(KEYS.CurrentJobs.ID_USER), bundle.getString(KEYS.JObDetail.DT_CREATED), this);
        View view = reportsJobDetailFragmentHelper.inflateLayout(inflater,
                container);
        EventBus.getDefault().register(this);
        return view;

    }

    /**
     * On event.
     *
     * @param updateJobDetailUi the update job detail ui
     */
    public void onEvent(UpdateJobDetailUi updateJobDetailUi) {
        reportsJobDetailFragmentHelper.doOnJobStartStop();
    }

    /**
     * On event.
     *
     * @param updateUiAfterSync the update ui after sync
     */
    public void onEvent(UpdateUiAfterSync updateUiAfterSync) {
        reportsJobDetailFragmentHelper.doOnSyncronize();
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onActivityResult(int, int,
     * android.content.Intent)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        if (resultCode == RequestCode.REQUEST_CODE_SIGNATURE_FACTURE) {
            if (resultCode != Activity.RESULT_CANCELED) {
                reportsJobDetailFragmentHelper.onReturnToFragment(requestCode,
                        data);
            }
        } else {
            if (resultCode == Activity.RESULT_OK) {
                reportsJobDetailFragmentHelper.onReturnToFragment(requestCode,
                        data);
            }
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onDestroy()
     */
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * this method will be invoke when the technician selects any one of the shared block to add.
     *
     * @param sharedBlocks
     */
    @Override
    public void onSelectSB(SharedBlocks sharedBlocks) {
        reportsJobDetailFragmentHelper.onSharedBlockSelected(sharedBlocks);
    }

    @Override
    public void onDataSaved(Bundle data) {
        reportsJobDetailFragmentHelper.saveMultipleData(data);
    }


//    public Boolean checkCameraPermission(Activity activity) {
//        return PermissionForAndroidM.checkForCameraPermission(activity);
//    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//        Boolean isGranted = PermissionForAndroidM.onRequestPermissionForFragment(new ReportsJobDetailFragment(), getContext(), requestCode, permissions, grantResults);
//
//        if (isGranted) {
//            reportsJobDetailFragmentHelper.openCameraAfterPermissionGranted();
//        }
//
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                                    == PackageManager.PERMISSION_GRANTED&&
                            ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED)
                    reportsJobDetailFragmentHelper.openCameraAfterPermissionGranted();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_ADAPTER: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                                    == PackageManager.PERMISSION_GRANTED&&
                            ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED)
                    reportsJobDetailFragmentHelper.openCameraAfterPermissionGranted();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            case CAMERA_PERMISSION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if ( ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED){
                        reportsJobDetailFragmentHelper.openCameraAfterPermissionGranted();                    }
                }

        }
    }

    public void callingPermissionLocation() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
            alertBuilder.setCancelable(true);
            alertBuilder.setTitle(getString(R.string.app_name));
            alertBuilder.setMessage(getString(R.string.str_location_permission));
            alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            MY_PERMISSIONS_REQUEST_LOCATION);
                }
            });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        }
    }

    public void callingPermissionCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_MEDIA_IMAGES) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) ==
                            PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(),"Permission allowed",Toast.LENGTH_SHORT).show();
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.app_name));
                alertBuilder.setMessage(getString(R.string.str_camera_permission));
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED, Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
                alertBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert = alertBuilder.create();
                alert.show();
            } else {
                // Directly ask for the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED, Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            }
        }else

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // android 13 changes
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_MEDIA_IMAGES) ==
                    PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(),"Permission allowed",Toast.LENGTH_SHORT).show();
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.app_name));
                alertBuilder.setMessage(getString(R.string.str_camera_permission));
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                    }
                });
                alertBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert = alertBuilder.create();
                alert.show();
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        }else

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) ||
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.CAMERA)||ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CAMERA)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.app_name));
                alertBuilder.setMessage(getString(R.string.str_camera_permission));
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
            } else if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.app_name));
                alertBuilder.setMessage(getString(R.string.str_storage_permission));
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
            }else {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.app_name));
                alertBuilder.setMessage(getString(R.string.str_storage_permission));
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
            }
        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    public void cameraPermission(){
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION);
    }
    public void callingPermissionCameraFromAdapter() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_MEDIA_IMAGES) ==
                    PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) ==
                            PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(),"Permission allowed",Toast.LENGTH_SHORT).show();
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.app_name));
                alertBuilder.setMessage(getString(R.string.str_camera_permission));
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED, Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
                alertBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert = alertBuilder.create();
                alert.show();
            } else {
                // Directly ask for the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED, Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

            }
        }else
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // android 13 changes
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_MEDIA_IMAGES) ==
                    PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(),"Permission allowed",Toast.LENGTH_SHORT).show();
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.READ_MEDIA_IMAGES)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.app_name));
                alertBuilder.setMessage(getString(R.string.str_camera_permission));
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
                    }
                });
                alertBuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alert = alertBuilder.create();
                alert.show();
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
            }
        }else
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) ||
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.CAMERA)||
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.CAMERA)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.app_name));
                alertBuilder.setMessage(getString(R.string.str_camera_permission));
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE))  {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.app_name));
                alertBuilder.setMessage(getString(R.string.str_storage_permission));
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_ADAPTER);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
            } else {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.app_name));
                alertBuilder.setMessage(getString(R.string.str_storage_permission));
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_ADAPTER);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
            }
        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE_ADAPTER);
        }
    }

}
