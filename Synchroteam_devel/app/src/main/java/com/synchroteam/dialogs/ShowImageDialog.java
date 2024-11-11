package com.synchroteam.dialogs;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;

import com.synchroteam.fragment.ReportsJobDetailFragment;
import com.synchroteam.fragmenthelper.ReportsJobDetailFragmentHelper;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.technicalsupport.JobDetails;
import com.synchroteam.scanner.CodeScannerActivity;
import com.synchroteam.utils.RequestCode;

// TODO: Auto-generated Javadoc

/**
 * The Class AttachmentDialog class to show and manage all the actions in attachment dialog.
 * created for future use
 *
 * @author ${Ideavate Solution}
 */
public class ShowImageDialog extends Dialog {

    /**
     * **
     * Creates an instance of AttachmentDialog and perform necessary initialization.
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
     * The job details.
     */
    private JobDetails jobDetails;

    /**
     * The reports job detail fragment helper.
     */
    private ReportsJobDetailFragmentHelper reportsJobDetailFragmentHelper;

    /**
     * Instantiates a new attachment dialog.
     *
     * @param jobDetails                     the job details
     * @param id_interv                      the id_interv
     * @param cd_statut                      the cd_statut
     * @param id_user                        the id_user
     * @param reportsJobDetailFragment       the reports job detail fragment
     * @param reportsJobDetailFragmentHelper the reports job detail fragment helper
     */
    public ShowImageDialog(JobDetails jobDetails, String id_interv, String cd_statut, String id_user, ReportsJobDetailFragment reportsJobDetailFragment
            , ReportsJobDetailFragmentHelper reportsJobDetailFragmentHelper) {
        super(jobDetails, android.R.style.Theme_Translucent_NoTitleBar);
        setContentView(R.layout.item_image);
        this.getWindow().setGravity(Gravity.CENTER);


        setCancelable(false);

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
                prendrePhoto(v);
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
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        reportsJobDetailFragmentHelper.removeFooterView();
        reportsJobDetailFragment.startActivityForResult(Intent.createChooser(intent, "Choose a Viewer"), RequestCode.REQUEST_CODE_ATTACHMENTS_LIBRARY);
        ShowImageDialog.this.dismiss();

    }

    /**
     * Opens The device camera.
     *
     * @param v the v
     * @author ${Previous Developer}
     */
    public void prendrePhoto(View v) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
        savePreferences();
        reportsJobDetailFragmentHelper.removeFooterView();
        reportsJobDetailFragment.startActivityForResult(cameraIntent, RequestCode.REQUEST_CODE_ATTACHMENTS_CAMERA);

        ShowImageDialog.this.dismiss();

    }

    /**
     * Gets the output media file uri.
     *
     * @param type the type
     * @return the output media file uri
     */
    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * Create a File for saving an image or video.
     *
     * @param type the type
     * @return the output media file
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    /**
     * Save preferences.
     */
    private void savePreferences() {
        // We need an Editor object to make preference changes.
        SharedPreferences.Editor editor = jobDetails.getPreferences(Context.MODE_PRIVATE).edit();
        capturedPath = fileUri.getPath();
        editor.putString("fileUriPath", capturedPath);

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
        reportsJobDetailFragment.startActivityForResult(it, RequestCode.REQUEST_CODE_ATTACHMENTS_BARCODE);
        ShowImageDialog.this.dismiss();


    }

}
