package com.synchroteam.synchroteam;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.beans.UpdateUiAfterSync;
import com.synchroteam.customui.DrawingImageViewNew;
import com.synchroteam.customui.VerticalSlideColorPicker;
import com.synchroteam.dao.Dao;
import com.synchroteam.events.TextEditImageEvent;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.AsyncTaskCoroutine;
import com.synchroteam.utils.CommonUtils;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.KEYS;
import com.synchroteam.utils.RequestCode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import de.greenrobot.event.EventBus;

/**
 * This class used to draw over the image in Report screen.
 * Created by Trident on 2/21/2017.
 */

public class EditReportImage extends AppCompatActivity implements View.OnClickListener, TextView.OnEditorActionListener {
    private DrawingImageViewNew editImage;

    private TextView txtDraw, txtAddText, txtReset;
    private VerticalSlideColorPicker colorPicker;
    private LinearLayout linText;
    private EditText edText;

    private Dao dao;
    private String idInter;
    private boolean isAttachmentImg;
    private int attachmentID;
    private String idPhoto;
    private String capturedPath;
    private String capturedImgExtension;
    private String capturedImgComment;
    private String capturedImgID;
    private int iteration;
    private String imgComment;
    private byte[] bImage;
    private boolean isDrawEnabled = true;

    private boolean isTextEditEnabled = false;
    private int editTextPosition;

    private int resisingWidth;
    private int resisingHeight;

    private static final String TAG = EditReportImage.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_report_image);
        initiateToolBar();
        initiateViews();
        setFontAwesomeTF();
        getValues();
        setImageValues();
        setColorPicker();

        getDeviceWidth();

        EventBus.getDefault().registerSticky(this);
    }

    private void getDeviceWidth() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

        int deviceWidth = displaymetrics.widthPixels;
        resisingWidth = (deviceWidth * 80) / 100;
        resisingHeight = (displaymetrics.heightPixels * 80) / 100;
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_image, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        this);
                builder.setMessage(R.string.textCancelLable)
                        .setCancelable(false)
                        .setPositiveButton(R.string.textYesLable,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        finish();
                                    }
                                })
                        .setNegativeButton(R.string.textNoLable,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
                break;
            case R.id.action_save:
                new SaveEditedImage().execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onEvent(TextEditImageEvent event) {
        linText.setVisibility(View.VISIBLE);
        edText.setText(event.text);
        isTextEditEnabled = true;
        editTextPosition = event.position;
        edText.requestFocus();
        edText.setSelection(edText.getText().length());
        CommonUtils.showKeyboard(this, edText);
    }

    /**
     * Set image size and bitmap.
     */
    private void setImageValues() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
            if (bImage != null && bImage.length > 0) {
                final Bitmap bitmap = BitmapFactory.decodeByteArray(bImage, 0, bImage.length);
                editImage.setImageBitmap(bitmap);
            }
        } else {
            if (bImage != null && bImage.length > 0) {
                //new changes
                Glide.with(EditReportImage.this)
                        .load(bImage)
                        .asBitmap()
                        .fitCenter()
                        .override(1024, 1024)
                        .placeholder(R.drawable.library_iicon)
                        .into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                                editImage.setImageBitmap(resource);
                            }
                        });
            }
        }


    }

    /**
     * Get values from intent.
     */
    private void getValues() {
        dao = DaoManager.getInstance();
        Bundle bundle = getIntent().getExtras();
        attachmentID = bundle.getInt(KEYS.EditImage.KEY_ATTACHEMENT_IMAGE_ID);

        if (attachmentID == KEYS.EditImage.KEY_IMAGE_FROM_REPORTS_ATTACHMENT) {
            idInter = bundle.getString(KEYS.CurrentJobs.ID);
            idPhoto = bundle.getString(KEYS.Photos.KEY_ID_PHOTO);
        } else if (attachmentID == KEYS.EditImage.KEY_IMAGE_FROM_REPORTS_LIST_PHOTO_TYPE) {
            idInter = bundle.getString(KEYS.CurrentJobs.ID);
            imgComment = bundle.getString(KEYS.Photos.KEY_COMMENT);
            iteration = bundle.getInt(KEYS.SharedBlock.ITERATION);
        } else if (attachmentID == KEYS.EditImage.KEY_IMAGE_FROM_NEW_JOB_PHOTO_ATTACHMENT) {
            capturedPath = bundle.getString(KEYS.Photos.KEY_PHOTO_PATH);
            capturedImgExtension = bundle.getString(KEYS.Photos.KEY_PHOTO_EXTENSION);
            capturedImgComment = bundle.getString(KEYS.Photos.KEY_PHOTO_COMMENT);
            capturedImgID = bundle.getString(KEYS.Photos.KEY_ID_PHOTO);

//            bImage = bundle.getByteArray(KEYS.Photos.KEY_PHOTO_BYTE_ARRAY);

        }
        getImage();
    }

    private void getImage() {
        if (attachmentID == KEYS.EditImage.KEY_IMAGE_FROM_REPORTS_ATTACHMENT) {
            bImage = dao.getPhotoByIdPhoto(idInter, idPhoto);
        } else if (attachmentID == KEYS.EditImage.KEY_IMAGE_FROM_REPORTS_LIST_PHOTO_TYPE) {
            bImage = dao.getPhotoImage(imgComment, idInter, iteration);
        } else if (attachmentID == KEYS.EditImage.KEY_IMAGE_FROM_NEW_JOB_PHOTO_ATTACHMENT) {

            if (capturedPath == null) {
                Resources res = this.getResources();
                Drawable drawable = res.getDrawable(R.drawable.barcode);
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                bImage = stream.toByteArray();
            } else {
                File capturedFile = new File(capturedPath);

                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(capturedFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Bitmap bm2 = BitmapFactory.decodeStream(fis);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm2.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                bImage = stream.toByteArray();

            }
        }
    }

    private void initiateToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        }
    }

    /**
     * Initiates ui elements.
     */
    private void initiateViews() {
        editImage = (DrawingImageViewNew) findViewById(R.id.iv_edit_image);
        txtDraw = (TextView) findViewById(R.id.tv_draw);
        txtAddText = (TextView) findViewById(R.id.tv_add_text);
        txtReset = (TextView) findViewById(R.id.tv_reset);
        colorPicker = (VerticalSlideColorPicker) findViewById(R.id.color_picker);
        linText = (LinearLayout) findViewById(R.id.lin_text);
        edText = (EditText) findViewById(R.id.ed_text);

        Drawable mDrawable = setDrawableColor(colorPicker.getSelectedColor());

        txtDraw.setBackgroundDrawable(mDrawable);

        edText.setOnEditorActionListener(this);

        txtDraw.setOnClickListener(this);
        txtAddText.setOnClickListener(this);
        txtReset.setOnClickListener(this);
    }

    private void setFontAwesomeTF() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), getString(R.string.fontName_fontAwesome));

        txtDraw.setTypeface(typeface);
        txtAddText.setTypeface(typeface);
        txtReset.setTypeface(typeface);

        editImage.setTypeface(typeface);
    }

    private void setColorPicker() {
        colorPicker.setOnColorChangeListener(new VerticalSlideColorPicker.OnColorChangeListener() {
            @Override
            public void onColorChange(int selectedColor) {
                editImage.setPaintColor(selectedColor);
                editImage.setTextColor(selectedColor);
                Drawable mDrawable = setDrawableColor(selectedColor);

                if (isDrawEnabled) {
                    txtDraw.setBackgroundDrawable(mDrawable);
                } else {
                    txtAddText.setBackgroundDrawable(mDrawable);
                }
            }

        });
    }

    @NonNull
    private Drawable setDrawableColor(int selectedColor) {
        Drawable mDrawable = getResources().getDrawable(R.drawable.circleframe_orange);
        mDrawable.setColorFilter(new
                PorterDuffColorFilter(selectedColor, PorterDuff.Mode.SRC_IN));
        return mDrawable;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_draw:
                enableDrawFunction();
                break;
            case R.id.tv_add_text:
                Drawable mDrawable = setDrawableColor(colorPicker.getSelectedColor());

                txtAddText.setBackgroundDrawable(mDrawable);
                txtDraw.setBackgroundResource(0);

                isDrawEnabled = false;
                editImage.setDrawEnabled(isDrawEnabled);

                linText.setVisibility(View.VISIBLE);
                edText.requestFocus();
                CommonUtils.showKeyboard(this, edText);

                break;
            case R.id.tv_reset:
                editImage.clearBitmap();

                enableDrawFunction();
                break;
        }
    }

    private void enableDrawFunction() {
        isDrawEnabled = true;
        editImage.setDrawEnabled(isDrawEnabled);

        Drawable mDrawable = setDrawableColor(colorPicker.getSelectedColor());

        txtDraw.setBackgroundDrawable(mDrawable);
        txtAddText.setBackgroundResource(0);

        clearHideEdittext();

        txtDraw.setTag(true);
    }

    /**
     * clear texts in edittext and hide it.
     */
    private void clearHideEdittext() {
        edText.getText().clear();
        edText.clearFocus();
        linText.setVisibility(View.INVISIBLE);
    }

    /**
     * Listener for edit text.
     *
     * @param textView
     * @param i
     * @param keyEvent
     * @return
     */
    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_DONE) {
            String enteredText = edText.getText().toString();
            if (isTextEditEnabled) {
                editImage.modifyText(enteredText, editTextPosition);
                isTextEditEnabled = false;
            } else {
                editImage.setText(enteredText);
            }

            //clear request and hide edittext
            clearHideEdittext();
        }
        return false;
    }

    private class SaveEditedImage extends AsyncTaskCoroutine<Void, Void> {
        ProgressDialog pDialogLoading;
        ByteArrayOutputStream stream;
        Bitmap bitmap, bitmapNewJob;

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            pDialogLoading = ProgressDialog.show(EditReportImage.this, getString(R.string.textPleaseWaitLable), null);
            pDialogLoading.show();
            stream = new ByteArrayOutputStream();
            editImage.setDrawingCacheEnabled(true);
            bitmap = editImage.getDrawingCache();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            editImage.setDrawingCacheEnabled(false);
        }

        @Override
        public Void doInBackground(Void... voids) {
            byte[] img = stream.toByteArray();
            if (attachmentID == KEYS.EditImage.KEY_IMAGE_FROM_REPORTS_ATTACHMENT) {
                dao.updatePhoto(idPhoto, img);
            } else if (attachmentID == KEYS.EditImage.KEY_IMAGE_FROM_REPORTS_LIST_PHOTO_TYPE) {
                dao.updatePhotoByComment(idInter, imgComment, iteration, img);
            }
            return null;
        }

        @Override
        public void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (attachmentID == KEYS.EditImage.KEY_IMAGE_FROM_NEW_JOB_PHOTO_ATTACHMENT) {

                File file1 = getSavedImgFile(capturedImgID);
                try {
                    FileOutputStream fff = new FileOutputStream(file1);
                    editImage.setDrawingCacheEnabled(true);
                    bitmapNewJob = editImage.getDrawingCache();
                    bitmapNewJob.compress(Bitmap.CompressFormat.PNG, 100, fff);
                    editImage.setDrawingCacheEnabled(false);
                    fff.close();

                    Intent intent = new Intent();

                    intent.putExtra(KEYS.Photos.KEY_PHOTO_PATH, file1.getAbsolutePath());
                    intent.putExtra(KEYS.Photos.KEY_PHOTO_EXTENSION, capturedImgExtension);
                    intent.putExtra(KEYS.Photos.KEY_PHOTO_COMMENT, capturedImgComment);
                    intent.putExtra(KEYS.Photos.KEY_ID_PHOTO, capturedImgID);

              //      setResult(RequestCode.REQUEST_NEW_JOB_EDIT_IMAGE, intent);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            pDialogLoading.dismiss();

            EventBus.getDefault().post(new UpdateUiAfterSync());
            finish();
        }
    }

    private File getSavedImgFile(String uniNameID) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        //old code
//        File mediaStorageDir = new File(
//                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//                "MyCameraApp");

        //new changes
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "MyCameraApp");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }

        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "Img" + uniNameID + ".jpg");


        return mediaFile;
    }


}