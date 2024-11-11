package com.synchroteam.scanner;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.widget.FrameLayout;

import com.synchroteam.dialogs.AttachmentDialogNewJob;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.CameraPreview;
import com.synchroteam.utils.Logger;

import net.sourceforge.zbar.Config;
import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

// TODO: Auto-generated Javadoc

/**
 * The Class ScannerBarUpdated.
 */
public class ScannerBarUpdated extends Activity {
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private boolean isRequestRequired = true;

    /**
     * The m camera.
     */
    private Camera mCamera;

    /**
     * The m preview.
     */
    private CameraPreview mPreview;

    /**
     * The auto focus handler.
     */
    private Handler autoFocusHandler;

    /**
     * The scanner.
     */
    ImageScanner scanner;


    /**
     * The previewing.
     */
    private boolean previewing = true;
    private AttachmentDialogNewJob attachmentDialog;

    static {
        System.loadLibrary("iconv");
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.scanner_code_updated);

        callingCameraLogic();


    }

    private void callingCameraLogic() {

        if (ContextCompat.checkSelfPermission(ScannerBarUpdated.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(ScannerBarUpdated.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(ScannerBarUpdated.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

                if (ContextCompat.checkSelfPermission(ScannerBarUpdated.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(ScannerBarUpdated.this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                    callingPermissionCamera();
                } else {
                    getBasic();
                }

            } else {
                callingPermissionCamera();
            }
        } else {
            getBasic();
        }
    }
    
    private void callingPermissionCamera() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(ScannerBarUpdated.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) ||
                ActivityCompat.shouldShowRequestPermissionRationale(ScannerBarUpdated.this,
                        Manifest.permission.CAMERA) ||
                ActivityCompat.shouldShowRequestPermissionRationale(ScannerBarUpdated.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(ScannerBarUpdated.this,
                    Manifest.permission.CAMERA)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ScannerBarUpdated.this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.app_name));
                alertBuilder.setMessage(getString(R.string.str_camera_permission));
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(ScannerBarUpdated.this,
                                new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(ScannerBarUpdated.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ScannerBarUpdated.this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.app_name));
                alertBuilder.setMessage(getString(R.string.str_storage_permission));
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(ScannerBarUpdated.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
            } else {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(ScannerBarUpdated.this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle(getString(R.string.app_name));
                alertBuilder.setMessage(getString(R.string.str_storage_permission));
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(ScannerBarUpdated.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
            }
        } else {
            // No explanation needed; request the permission
            ActivityCompat.requestPermissions(ScannerBarUpdated.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    private boolean checkPermissionLocal() {
        int location = 0, read = 0, write = 0;

        location = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        read = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        write = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return location == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED &&
                write == PackageManager.PERMISSION_GRANTED;


    }


    @Override
    protected void onResume() {
        super.onResume();

        callingCameraLogic();

    }

    void getBasic() {
        autoFocusHandler = new Handler();
        scanner = new ImageScanner();
        mCamera = getCameraInstance();
        scanner.setConfig(0, Config.X_DENSITY, 3);
        scanner.setConfig(0, Config.Y_DENSITY, 3);
        // Only enable the codes your app requires
        scanner.setConfig(Symbol.QRCODE, Config.ENABLE, 1);
        mPreview = new CameraPreview(this, mCamera, previewCb, autoFocusCB);

        FrameLayout preview = (FrameLayout) findViewById(R.id.cameraPreview);
        preview.addView(mPreview);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NotNull String permissions[], @NotNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(ScannerBarUpdated.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(ScannerBarUpdated.this, Manifest.permission.CAMERA)
                                    == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(ScannerBarUpdated.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED)
                        getBasic();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    isRequestRequired = false;
                    final androidx.appcompat.app.AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
                    alertDialogBuilder.setTitle("Permissions Required")
                            .setMessage("You have forcefully denied some of the required permissions " +
                                    "for this application. Please open settings, go to permissions and allow them.")
                            .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.fromParts("package", getPackageName(), null));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    isRequestRequired = true;

                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    isRequestRequired = true;
                                    callingPermissionCamera();

                                }
                            })
                            .setCancelable(false)
                            .create()
                            .show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    public void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.stopPreview();
        }

        previewing = false;
    }

    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        releaseCamera();
        super.onDestroy();
    }
    
    /* (non-Javadoc)
     * @see android.app.Activity#onBackPressed()
     */
    public void onBackPressed() {

        setResult(RESULT_CANCELED);
        super.onBackPressed();

    }
    /**
     * A safe way to get an instance of the Camera object.
     *
     * @return the camera instance
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {

            c = Camera.open();
        } catch (Exception e) {
            Logger.printException(e);
        }
        return c;
    }

    /**
     * Release camera.
     */
    private void releaseCamera() {
        if (mCamera != null) {
            previewing = false;
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * The do auto focus.
     */
    private Runnable doAutoFocus = new Runnable() {
        public void run() {
            if (previewing)
                mCamera.autoFocus(autoFocusCB);
        }
    };

    /**
     * The auto focus cb.
     */
    Camera.AutoFocusCallback autoFocusCB = new Camera.AutoFocusCallback() {
        public void onAutoFocus(boolean success, Camera camera) {
            autoFocusHandler.postDelayed(doAutoFocus, 1000);
        }
    };


    /**
     * The preview cb.
     */
    Camera.PreviewCallback previewCb = new Camera.PreviewCallback() {
        public void onPreviewFrame(byte[] data, Camera camera) {
            Camera.Parameters parameters = camera.getParameters();
            Camera.Size size = parameters.getPreviewSize();
            Image barcode = new Image(size.width, size.height, "Y800");
            barcode.setData(data);


            int result = scanner.scanImage(barcode);

            if (result != 0) {
                previewing = false;
                mCamera.setDisplayOrientation(90);
                mCamera.setPreviewCallback(null);

                mCamera.stopPreview();

                SymbolSet syms = scanner.getResults();

                int width = parameters.getPreviewSize().width;
                int height = parameters.getPreviewSize().height;

                YuvImage yuv = new YuvImage(data, parameters.getPreviewFormat(), width, height, null);

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                yuv.compressToJpeg(new Rect(0, 0, width, height), 50, out);

                byte[] bytes = out.toByteArray();


                for (Symbol sym : syms) {

                    String symData = sym.getData();
                    if (!TextUtils.isEmpty(symData)) {
                        Intent dataIntent = new Intent();
                        dataIntent.putExtra("SCAN_RESULT_CODE", symData);
                        dataIntent.putExtra("RESULT_TYPE", 1);
                        //dataIntent.putExtra("SCAN_RESULT_DATA", data);
                        dataIntent.putExtra("sizewidth", size.width);
                        dataIntent.putExtra("sizeheight", size.height);
                        dataIntent.putExtra("imagebytearray", bytes);
//                        dataIntent.putExtra("rotationDegree", rotationDegree);
//                        deleteTempImage();
                        setResult(Activity.RESULT_OK, dataIntent);
                        finish();
                        break;
                    }
                }
            }
        }
    };
}