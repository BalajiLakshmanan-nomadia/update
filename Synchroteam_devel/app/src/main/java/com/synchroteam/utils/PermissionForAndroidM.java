package com.synchroteam.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.widget.Toast;


import com.synchroteam.synchroteam3.R;

import static com.synchroteam.utils.RequestCode.CAMERA_PERMISSION_REQUEST_CODE;
import static com.synchroteam.utils.RequestCode.REQUEST_PERMISSION_SETTING;

/**
 * Created by user on 12/4/17.
 */

public class PermissionForAndroidM {


    public static Boolean onRequestPermission(final Context context, int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    return true;

                } else {
                    //permission denied
                    if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CAMERA)) {
                        //Show Information about why you need the permission
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(context.getString(R.string.need_permission));
                        builder.setMessage(context.getString(R.string.permission_camera));
                        builder.setPositiveButton(context.getString(R.string.grant), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);

                            }
                        });
                        builder.setNegativeButton(context.getString(R.string.textCancelLable), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    } else {
                        Toast.makeText(context, context.getString(R.string.unable_permission), Toast.LENGTH_LONG).show();
                    }

                }
                break;
        }

        return false;
    }


    public static Boolean onRequestPermissionForFragment(final Fragment fragment, final Context context, int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    return true;

                } else {
                    //permission denied
                    if (fragment.shouldShowRequestPermissionRationale((Manifest.permission.CAMERA))) {
                        //Show Information about why you need the permission
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(context.getString(R.string.need_permission));
                        builder.setMessage(context.getString(R.string.permission_camera));
                        builder.setPositiveButton(context.getString(R.string.grant), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                                fragment.requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);

                            }
                        });
                        builder.setNegativeButton(context.getString(R.string.textCancelLable), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    } else {
                        Toast.makeText(context, context.getString(R.string.unable_permission), Toast.LENGTH_LONG).show();
                    }

                }
                break;
        }

        return false;
    }


    public static Boolean onRequestPermissionForDialog(final DialogFragment fragment, final Context context, int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE:

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    return true;

                } else {
                    //permission denied
                    if (fragment.shouldShowRequestPermissionRationale((Manifest.permission.CAMERA))) {
                        //Show Information about why you need the permission
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(context.getString(R.string.need_permission));
                        builder.setMessage(context.getString(R.string.permission_camera));
                        builder.setPositiveButton(context.getString(R.string.grant), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();

                                fragment.requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);

                            }
                        });
                        builder.setNegativeButton(context.getString(R.string.textCancelLable), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    } else {
                        Toast.makeText(context, context.getString(R.string.unable_permission), Toast.LENGTH_LONG).show();
                    }

                }
                break;
        }

        return false;
    }


    public static Boolean checkForCameraPermission(final Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, android.Manifest.permission.CAMERA)) {
                //Show Information about why you need the permission
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
                builder.setTitle(activity.getString(R.string.need_permission));
                builder.setMessage(activity.getString(R.string.permission_camera));
                builder.setPositiveButton(activity.getString(R.string.grant), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
                    }
                });
                builder.setNegativeButton(activity.getString(R.string.textCancelLable), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else if (SharedPref.getPermissionCamera(activity)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(activity);
                builder.setTitle(activity.getString(R.string.need_permission));
                builder.setMessage(activity.getString(R.string.permission_camera));
                builder.setPositiveButton(activity.getString(R.string.grant), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                        intent.setData(uri);
                        activity.startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(activity.getBaseContext(), activity.getString(R.string.grant_camera), Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton(activity.getString(R.string.textCancelLable), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            }

            SharedPref.setPermissionCamera(true, activity);
        } else {
            return true;
        }

        return false;
    }


}