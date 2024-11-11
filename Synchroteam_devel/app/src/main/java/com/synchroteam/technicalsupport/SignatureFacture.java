package com.synchroteam.technicalsupport;

import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.beans.GestionAcces;
import com.synchroteam.dao.Dao;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DaoManager;
import com.synchroteam.utils.Logger;

// TODO: Auto-generated Javadoc

/**
 * The Class SignatureFacture .\
 *
 * @author Previous Developer
 */
public class SignatureFacture extends Activity {

    /**
     * The m bitmap.
     */
    private Bitmap mBitmap;

    /**
     * The id inter.
     */
    private String idInter;

    /**
     * The cd_statut.
     */
    @SuppressWarnings("unused")
    private int idUser, cd_statut;

    /**
     * The m cancel.
     */
    private Button mClear, mSave, mCancel;

    /**
     * The m content.
     */
    private LinearLayout mContent;

    /**
     * The m view.
     */
    private View mView;

    /**
     * The m signature.
     */
    private Signature mSignature;

    /**
     * The progress d synch.
     */
    protected ProgressDialog progressDSynch;

    /**
     * The dao.
     */
    private Dao dao;

    /**
     * The pi.
     */
    PendingIntent pi;

    /**
     * The sign.
     */
    private String sign;

    /**
     * The bitmap.
     */
    private Bitmap bitmap;

    private boolean isSignatureDone = false;
    private int disabledColorBtn, enabledColorbtn;

    private EditText editTextCustomerName;

    private LinearLayout linearCustomerName;

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dao = DaoManager.getInstance();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_newsignature_facture);
        // setTitle(R.string.titre_signature);
        setFinishOnTouchOutside(false);
        editTextCustomerName = (EditText) findViewById(R.id.editTextCustomerName);
        linearCustomerName = (LinearLayout) findViewById(R.id.linearCustomerName);
        orientationFixOreo();
        init();

    }

    private void orientationFixOreo() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    /**
     * Inits the.
     */
    @SuppressWarnings("deprecation")
    public void init() {
        Intent in = getIntent();
        idInter = in.getExtras().getString("id_interv");
        cd_statut = in.getExtras().getInt("cd_statut");
        idUser = in.getExtras().getInt("id_user");

        sign = in.getExtras().getString("sign");

        disabledColorBtn = getResources().getColor(R.color.textGrayOkBtn);
        enabledColorbtn = getResources().getColor(R.color.black);
        // Log.e("Info", idUser+" "+cd_statut+" "+idInter);

        mClear = (Button) findViewById(R.id.clearclt);
        mSave = (Button) findViewById(R.id.saveclt);

        mContent = (LinearLayout) findViewById(R.id.signclient);
        mCancel = (Button) findViewById(R.id.cancel);

        mView = mContent;
        /*
         * user=Dao.getUser(); if(user.getId()!=idUser || cd_statut != 3) {
         * mSave.setEnabled(false); mClear.setEnabled(false); }
         */
        byte[] retour = null;

        if (sign.equals("user")) {
GestionAcces gs=dao.getAcces();
            retour = dao.getPhotoById(idInter, "SIGN_USER");

            // retrieve cmt from T_INTERVENTIONS table
            String cmt = dao.getSignUser(idInter);

            linearCustomerName.setVisibility(View.VISIBLE);

            editTextCustomerName
                    .setHint(getString(R.string.textTechnicianNameLable));
            if ((cmt != null) && (!TextUtils.isEmpty(cmt)))
                editTextCustomerName.setText(cmt);

        } else if (sign.equals("client")) {

            retour = dao.getPhotoById(idInter, "SIGN_CLIENT");

            // retrieve cmt from T_INTERVENTIONS table
            String cmt = dao.getSignClient(idInter);

            linearCustomerName.setVisibility(View.VISIBLE);
            editTextCustomerName
                    .setHint(getString(R.string.textCustomerNameLable));
            if ((cmt != null) && (!TextUtils.isEmpty(cmt)))
                editTextCustomerName.setText(cmt);

        } else if (sign.equals("facture")) {

            retour = dao.getPhotoById(idInter, "SIGN_FACTURE");

        }

        if (retour != null) {
            bitmap = BitmapFactory.decodeByteArray(retour, 0, retour.length);

        }

        mSignature = new Signature(this, null);

        mContent.addView(mSignature, LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT);

        mClear.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mCancel.setTextColor(disabledColorBtn);
                mSave.setTextColor(disabledColorBtn);
                isSignatureDone = false;
                mSignature.clear();
            }
        });

        mSave.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // String type="SIGN_FACTURE";
                String type = null;

                if (sign.equals("user")) {
                    type = "SIGN_USER";
                    dao.setSignUser(idInter, editTextCustomerName.getText()
                            .toString().trim());
                } else if (sign.equals("client")) {
                    type = "SIGN_CLIENT";
                    dao.setSignClient(idInter, editTextCustomerName.getText()
                            .toString().trim());

                } else if (sign.equals("facture")) {
                    type = "SIGN_FACTURE";
                }

                mView.setDrawingCacheEnabled(true);
                mSignature.save(mView, type);
                setResult(RESULT_OK);
                finish();

            }
        });
        mCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                setResult(RESULT_CANCELED);
                finish();

            }
        });

    }

    /**
     * The Class Signature.
     */
    public class Signature extends View {

        /**
         * The Constant STROKE_WIDTH.
         */
        private static final float STROKE_WIDTH = 5f;

        /**
         * The Constant HALF_STROKE_WIDTH.
         */
        private static final float HALF_STROKE_WIDTH = STROKE_WIDTH / 2;

        /**
         * The paint.
         */
        private Paint paint = new Paint();

        /**
         * The path.
         */
        private Path path = new Path();

        /**
         * The last touch x.
         */
        private float lastTouchX;

        /**
         * The last touch y.
         */
        private float lastTouchY;

        /**
         * The dirty rect.
         */
        private final RectF dirtyRect = new RectF();

        /**
         * Instantiates a new signature.
         *
         * @param context the context
         * @param attrs   the attrs
         */
        public Signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            paint.setAntiAlias(true);
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(STROKE_WIDTH);
        }

        /**
         * Save.
         *
         * @param v    the v
         * @param type the type
         */
        public void save(View v, String type) {

            // float sizeW = 300 * scale;
            // float sizeH = 150 * scale;
            if (mBitmap == null) {
                mBitmap = Bitmap.createBitmap(mContent.getWidth(),
                        mContent.getHeight(), Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(mBitmap);
            String filename;
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            filename = sdf.format(date);

            try {

                FileOutputStream mFileOutStream;
                mFileOutStream = openFileOutput(filename, Context.MODE_PRIVATE);

                v.draw(canvas);
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100,
                        mFileOutStream);
                mFileOutStream.flush();
                mFileOutStream.close();
                savePhoto(getFilesDir() + "/" + filename, filename, type);

            } catch (Exception e) {
                Logger.log("log_tag", e.toString());
            }
        }

        /**
         * Clear.
         */
        public void clear() {

            if (bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }

            path.reset();
            invalidate();
        }

        /*
         * (non-Javadoc)
         *
         * @see android.view.View#onDraw(android.graphics.Canvas)
         */
        @Override
        protected void onDraw(Canvas canvas) {
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, 0, 0, null);

            }

            canvas.drawPath(path, paint);
        }

        /*
         * (non-Javadoc)
         *
         * @see android.view.View#onTouchEvent(android.view.MotionEvent)
         */
        @Override
        public boolean onTouchEvent(MotionEvent event) {

            float eventX = event.getX();
            float eventY = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(eventX, eventY);
                    lastTouchX = eventX;
                    lastTouchY = eventY;
                    return true;

                case MotionEvent.ACTION_MOVE:

                case MotionEvent.ACTION_UP:
                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        float historicalX = event.getHistoricalX(i);
                        float historicalY = event.getHistoricalY(i);
                        expandDirtyRect(historicalX, historicalY);
                        path.lineTo(historicalX, historicalY);
                        if (!isSignatureDone) {
                            mCancel.setTextColor(enabledColorbtn);
                            mSave.setTextColor(enabledColorbtn);
                            isSignatureDone = true;
                        }
                    }
                    path.lineTo(eventX, eventY);
                    break;

                default:
                    debug("Ignored touch event: " + event.toString());
                    return false;
            }

            invalidate((int) (dirtyRect.left - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.top - HALF_STROKE_WIDTH),
                    (int) (dirtyRect.right + HALF_STROKE_WIDTH),
                    (int) (dirtyRect.bottom + HALF_STROKE_WIDTH));

            lastTouchX = eventX;
            lastTouchY = eventY;

            return true;
        }

        /**
         * Debug.
         *
         * @param string the string
         */
        private void debug(String string) {
        }

        /**
         * Expand dirty rect.
         *
         * @param historicalX the historical x
         * @param historicalY the historical y
         */
        private void expandDirtyRect(float historicalX, float historicalY) {
            if (historicalX < dirtyRect.left) {
                dirtyRect.left = historicalX;
            } else if (historicalX > dirtyRect.right) {
                dirtyRect.right = historicalX;
            }

            if (historicalY < dirtyRect.top) {
                dirtyRect.top = historicalY;
            } else if (historicalY > dirtyRect.bottom) {
                dirtyRect.bottom = historicalY;
            }
        }

        /**
         * Reset dirty rect.
         *
         * @param eventX the event x
         * @param eventY the event y
         */
        private void resetDirtyRect(float eventX, float eventY) {
            dirtyRect.left = Math.min(lastTouchX, eventX);
            dirtyRect.right = Math.max(lastTouchX, eventX);
            dirtyRect.top = Math.min(lastTouchY, eventY);
            dirtyRect.bottom = Math.max(lastTouchY, eventY);
        }
    }

    /**
     * Save photo.
     *
     * @param file     the file
     * @param filename the filename
     * @param type     the type
     */
    public void savePhoto(String file, String filename, String type) {
        String da;
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

        try {
            da = sdf.format(date);
            dao.insertSignature(idUser + "_" + da, idInter, file, type, "jpeg", 0);
            deleteFile(filename);
        } catch (Exception e) {
            Logger.printException(e);
        }

    }

}
