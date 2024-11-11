package com.synchroteam.dialogs;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.view.Gravity;
import android.view.View;

import com.synchroteam.fragment.ReportsJobDetailFragment;
import com.synchroteam.fragmenthelper.ReportsJobDetailFragmentHelper;
import com.synchroteam.synchroteam3.BuildConfig;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.scanner.CodeScannerActivity;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.RequestCode;

// TODO: Auto-generated Javadoc

/**
 * The Class AttachmentDialog class to show and manage all the actions in
 * attachment dialog.
 *
 * @author ${Ideavate Solution}
 */
@SuppressLint("SimpleDateFormat")
public class AttachmentDialog extends Dialog {

    /**
     * ** Creates an instance of AttachmentDialog and perform necessary
     * initialization.
     */

    private ReportsJobDetailFragment reportsJobDetailFragment;

    /**
     * The file uri.
     */
    private Uri fileUri;

    /**
     * The Constant MEDIA_TYPE_IMAGE.
     */
    public static final int MEDIA_TYPE_IMAGE = 1;

    /**
     * The captured path.
     */
    private String capturedPath;

    /**
     * The captured path new.
     */
    String mCurrentPhotoPath;
    /**
     * The job details.
     */
    private Activity jobDetails;

    /**
     * The reports job detail fragment helper.
     */
    private ReportsJobDetailFragmentHelper reportsJobDetailFragmentHelper;

    private String tag = "";

    private int groupIndex, childIndex;

    // private static final String TAG = "AttachmentDialog";

    static final int REQUEST_TAKE_PHOTO = 1;

    /**
     * Instantiates a new attachment dialog.
     *
     * @param jobDetails                     the job details
     * @param reportsJobDetailFragment       the reports job detail fragment
     * @param reportsJobDetailFragmentHelper the reports job detail fragment helper
     */
    public AttachmentDialog(Activity jobDetails,
                            ReportsJobDetailFragment reportsJobDetailFragment,
                            ReportsJobDetailFragmentHelper reportsJobDetailFragmentHelper) {
        super(jobDetails, android.R.style.Theme_Translucent_NoTitleBar);
        setCancelable(false);
        setContentView(R.layout.layout_add_attachment_dialog);
        this.getWindow().setGravity(Gravity.CENTER);
        findViewById(R.id.cancelBtn).setOnClickListener(onClickListener);
        findViewById(R.id.libraryIcon).setOnClickListener(onClickListener);
        findViewById(R.id.cameraIcon).setOnClickListener(onClickListener);
        findViewById(R.id.barcodeIcon).setOnClickListener(onClickListener);
        this.reportsJobDetailFragment = reportsJobDetailFragment;
        this.reportsJobDetailFragmentHelper = reportsJobDetailFragmentHelper;
        this.jobDetails = jobDetails;
        setCancelable(false);

    }

    public AttachmentDialog(Activity jobDetails,
                            ReportsJobDetailFragment reportsJobDetailFragment,
                            ReportsJobDetailFragmentHelper reportsJobDetailFragmentHelper,
                            String tag, int groupIndex, int childIndex) {
        super(jobDetails, android.R.style.Theme_Translucent_NoTitleBar);
        setCancelable(false);
        setContentView(R.layout.layout_add_attachment_dialog);
        Objects.requireNonNull(this.getWindow()).setGravity(Gravity.CENTER);
        findViewById(R.id.cancelBtn).setOnClickListener(onClickListener);
        findViewById(R.id.libraryIcon).setOnClickListener(onClickListener);
        findViewById(R.id.cameraIcon).setOnClickListener(onClickListener);
        //new changes enable scanner for picture type
        findViewById(R.id.barcodeIcon).setOnClickListener(onClickListener);
        this.reportsJobDetailFragment = reportsJobDetailFragment;
        this.reportsJobDetailFragmentHelper = reportsJobDetailFragmentHelper;
        this.jobDetails = jobDetails;
        this.tag = tag;
        this.groupIndex = groupIndex;
        this.childIndex = childIndex;
        setCancelable(false);


        //hide barcode icon for picture field
//        findViewById(R.id.barcodeIcon).setVisibility(View.GONE);

    }


    /**
     * The on click listener.
     */
    android.view.View.OnClickListener onClickListener = new android.view.View.OnClickListener() {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.cancelBtn) {
                dismiss();
            } else if (id == R.id.cameraIcon) {
//                if (ActivityCompat.checkSelfPermission(jobDetails,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                    dispatchTakePictureIntent();
////                    prendrePhoto();
//                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    dispatchTakePictureIntent();
                } else if (ActivityCompat.checkSelfPermission(jobDetails,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
//                    prendrePhoto();
                }

            } else if (id == R.id.barcodeIcon) {
                Scan(v);
            } else if (id == R.id.libraryIcon) {
                ajouterPhoto(v);
            }

        }
    };


    /**
     * opens the list of avalable options to view the gallary .
     *
     * @param v the v
     */
    public void ajouterPhoto(View v) {
        // Intent intent = new Intent(Intent.ACTION_PICK,
        // android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        // intent.setType("image/*");
        reportsJobDetailFragmentHelper.removeFooterView();

        if (tag.equals("ChildView")) {

            reportsJobDetailFragmentHelper.setGroupIndexSend(groupIndex);
            reportsJobDetailFragmentHelper.setChildIndexSend(childIndex);
            // reportsJobDetailFragment.startActivityForResult(
            // Intent.createChooser(intent, "Choose a Viewer"),
            // RequestCode.REQUEST_CODE_CHILDVIEW_LIBRARY);

//            /*
//             * new changes
//             */
//            reportsJobDetailFragment.startActivityForResult(Intent
//                            .createChooser(new Intent(Intent.ACTION_GET_CONTENT)
//                                    .setType("image/*"), "Choose a Viewer"),
//                    RequestCode.REQUEST_CODE_CHILDVIEW_LIBRARY);

            reportsJobDetailFragment.startActivityForResult(getPickImageIntent(),
                    RequestCode.REQUEST_CODE_CHILDVIEW_LIBRARY);


        } else {
            /*
             * old code
             */
            // reportsJobDetailFragment.startActivityForResult(
            // Intent.createChooser(intent, "Choose a Viewer"),
            // RequestCode.REQUEST_CODE_ATTACHMENTS_LIBRARY);

            /*
             * new changes
             */
//            reportsJobDetailFragment.startActivityForResult(Intent
//                            .createChooser(new Intent(Intent.ACTION_GET_CONTENT)
//                                    .setType("image/*"), "Choose an image"),
//                    RequestCode.REQUEST_CODE_ATTACHMENTS_LIBRARY);

            reportsJobDetailFragment.startActivityForResult(getPickImageIntent(),
                    RequestCode.REQUEST_CODE_ATTACHMENTS_LIBRARY);
        }
        AttachmentDialog.this.dismiss();

    }

    /**
     * Create a File for saving an image or video.
     *
     * @return the output media file
     */
    private File createImageFile() throws IOException {
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";

        //old code
//        File storageDir = jobDetails.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );




        //new changes v51
        File mediaStorageDir = new File(jobDetails.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "MyCameraApp");


        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                mediaStorageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();


        return image;
    }


    /**
     * Opens The device camera.
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(jobDetails.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                fileUri = FileProvider.getUriForFile(jobDetails,
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                //new changes
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                savePreferences();
                reportsJobDetailFragmentHelper.removeFooterView();

                if (tag.equals("ChildView")) {
//
                    reportsJobDetailFragmentHelper.setGroupIndexSend(groupIndex);
                    reportsJobDetailFragmentHelper.setChildIndexSend(childIndex);
                    reportsJobDetailFragment.startActivityForResult(takePictureIntent,
                            RequestCode.REQUEST_CODE_CHILDVIEW_CAMERA);

                } else {
                    reportsJobDetailFragment.startActivityForResult(takePictureIntent,
                            RequestCode.REQUEST_CODE_ATTACHMENTS_CAMERA);

                }
                AttachmentDialog.this.dismiss();
            }
        }
    }


    /**
     * Save preferences.
     */
    private void savePreferences() {
        // We need an Editor object to make preference changes.
        SharedPreferences.Editor editor = jobDetails.getPreferences(
                Context.MODE_PRIVATE).edit();
        capturedPath = fileUri.getPath();
//        editor.putString("fileUriPath", capturedPath);

        editor.putString("fileUriPath", mCurrentPhotoPath);

        // Commit the edits!
        editor.commit();
    }

    /**
     * Opens The barcode Scanner class.
     *
     * @param v the v
     */
    public void Scan(View v) {

        Intent it = new Intent(jobDetails, CodeScannerActivity.class);
        reportsJobDetailFragmentHelper.removeFooterView();
        Logger.log(tag, "Tag : " + tag + "group index : " + groupIndex
                + "child index : " + childIndex);
        if (tag.equals("ChildView")) {

            reportsJobDetailFragmentHelper.setGroupIndexSend(groupIndex);
            reportsJobDetailFragmentHelper.setChildIndexSend(childIndex);
            reportsJobDetailFragment.startActivityForResult(it,
                    RequestCode.REQUEST_CODE_CHILDVIEW_BARCODE);

        } else
            reportsJobDetailFragment.startActivityForResult(it,
                    RequestCode.REQUEST_CODE_ATTACHMENTS_BARCODE);

        AttachmentDialog.this.dismiss();

    }


    private Intent getPickImageIntent() {
        Intent chooserIntent = null;
        ArrayList<Intent> intentList = new ArrayList<>();
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        intentList = addIntentsToList(jobDetails, intentList, pickIntent);

        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(
                    intentList.remove(intentList.size() - 1), "");

            if (chooserIntent != null) {
                chooserIntent.putExtra(
                        Intent.EXTRA_INITIAL_INTENTS,
                        intentList.toArray(new Parcelable[intentList.size()])
                );
            }
        }

        return chooserIntent;

    }


    private ArrayList<Intent> addIntentsToList(Context context,
                                               ArrayList<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }
        return list;
    }


}
