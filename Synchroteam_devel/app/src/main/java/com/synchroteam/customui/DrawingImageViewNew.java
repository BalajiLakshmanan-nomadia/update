package com.synchroteam.customui;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import com.synchroteam.events.TextEditImageEvent;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.RotationGestureDetector;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by Trident on 2/21/2017.
 */

public class DrawingImageViewNew extends androidx.appcompat.widget.AppCompatImageView implements ScaleGestureDetector.OnScaleGestureListener, RotationGestureDetector.OnRotationGestureListener {
    // onTouch
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private Path mPath;

    private Paint drawPaint, canvasPaint;

    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;

    public void setDrawEnabled(boolean drawEnabled) {
        isDrawEnabled = drawEnabled;
    }

    private boolean isDrawEnabled = true;

    public enum ClampMode {UNLIMITED, ORIGIN_INSIDE, TEXT_INSIDE}

    private ScaleGestureDetector scaleDetector;
    private RotationGestureDetector rotateDetector;

    // region Global parameters
    private float minSize;
    private float maxSize;
    private boolean panEnabled;
    private boolean scaleEnabled;
    private boolean rotationEnabled;
    private ClampMode clampTextMode;
    private int interline;
    private RectF imageRect;
    // endregion

    // region Other members
    private PointF focalPoint;
    private OnTextMovedListener onTextMovedListener;
    private float previousRotation = 0f;
    private ArrayList<TextProperties> texts;
    private int currentSize;
    private int currentColor;
    // endregion

    private int CLICK_ACTION_THRESHHOLD = 10;
    private float startX;
    private float startY;

    private static final String TAG = DrawingImageViewNew.class.getSimpleName();

    public interface OnTextMovedListener {
        void textMoved(PointF position);
    }

    protected static class TextProperties {
        private String text;

        public float scale;
        public float size;
        public String[] textLines;
        public Paint paint;
        public ArrayList<Rect> textRects;
        public PointF textPosition;
        public PointF rotationCenter;
        public float rotation;
        public float endX;
        public float endY;

        public TextProperties(String text, float size, int color) {
            this.scale = 1f;
            this.size = size;
            this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            this.textRects = new ArrayList<>();
            this.textPosition = new PointF(0f, 0f);
            this.rotationCenter = new PointF();
            this.rotation = 0f;
            this.endX = 0f;
            this.endY = 0f;

            paint.setColor(color);
            paint.setTextSize(size);
            setText(text);
        }

        public void setText(String text) {
            this.text = text;
            this.textLines = null;
            if (null != text) {
                this.textLines = text.split("\n");

                textRects.clear();
                for (int i = 0; i < textLines.length; i++) {
                    Rect r = new Rect();
                    paint.getTextBounds(textLines[i], 0, textLines[i].length(), r);
                    textRects.add(i, r);
                }
            }
        }

        public String getText() {
            return text;
        }
    }


    public DrawingImageViewNew(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init(context, attrs);
        setupDrawing();
    }

    public DrawingImageViewNew(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
        setupDrawing();
    }

    protected void init(Context context, AttributeSet attributeSet) {
        texts = new ArrayList<>();

        imageRect = new RectF();
        focalPoint = new PointF();

        Resources resources = context.getResources();

        if (null != attributeSet) {
            TypedArray attrs = context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.TextImageView, 0, 0);
            currentSize = attrs.getDimensionPixelSize(R.styleable.TextImageView_android_textSize, resources.getDimensionPixelSize(R.dimen.default_text_size));
            currentColor = attrs.getColor(R.styleable.TextImageView_android_textColor, Color.BLACK);
            panEnabled = attrs.getBoolean(R.styleable.TextImageView_tiv_panEnabled, false);
            scaleEnabled = attrs.getBoolean(R.styleable.TextImageView_tiv_scaleEnabled, false);
            rotationEnabled = attrs.getBoolean(R.styleable.TextImageView_tiv_rotationEnabled, false);
            interline = attrs.getDimensionPixelOffset(R.styleable.TextImageView_tiv_interline, 0);
            clampTextMode = ClampMode.values()[attrs.getInt(R.styleable.TextImageView_tiv_clampTextMode, 0)];
            setText(attrs.getString(R.styleable.TextImageView_android_text));

            minSize = attrs.getDimensionPixelSize(R.styleable.TextImageView_tiv_minTextSize, resources.getDimensionPixelSize(R.dimen.default_min_text_size));
            maxSize = attrs.getDimensionPixelSize(R.styleable.TextImageView_tiv_maxTextSize, resources.getDimensionPixelSize(R.dimen.default_max_text_size));
            attrs.recycle();
        }

        scaleDetector = new ScaleGestureDetector(context, this);
        rotateDetector = new RotationGestureDetector(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        scaleDetector.onTouchEvent(ev);
        rotateDetector.onTouchEvent(ev);
        super.onTouchEvent(ev);

        final int action = ev.getAction();
        if (isDrawEnabled) {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    touchStart(ev.getX(), ev.getY());
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touchMove(ev.getX(), ev.getY());
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touchUp();
                    invalidate();
                    break;
            }
            return true;
        } else {
            switch (action & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    startX = ev.getX();
                    startY = ev.getY();

                    recalculateFocalPoint(ev);
                    return true;
                case MotionEvent.ACTION_UP:
                    if (0 < texts.size()) {
                        float endX = ev.getX();
                        float endY = ev.getY();

                        if (isAClick(startX, endX, startY, endY)) {
                            getNearestText(ev.getX(), ev.getY());
                            TextProperties tp = texts.get(texts.size() - 1);
                            Logger.output(TAG, "x : " + ev.getX() + " y : " + ev.getY() + " text x : " + tp.textPosition.x + " text y : " + tp.textPosition.y);
                        }

                        recalculateFocalPoint(ev);
                    }
                    return true;
                case MotionEvent.ACTION_POINTER_UP:
                    recalculateFocalPoint(ev);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    Logger.output(TAG, "ACTION_MOVE");
                    if (0 < texts.size()) {
                        final float x = focalPoint.x;
                        final float y = focalPoint.y;
                        TextProperties tp = texts.get(texts.size() - 1);

                        recalculateFocalPoint(ev);

                        if (panEnabled) {
                            tp.textPosition.x += focalPoint.x - x;
                            tp.textPosition.y += focalPoint.y - y;

                            tp.rotationCenter.x += focalPoint.x - x;
                            tp.rotationCenter.y += focalPoint.y - y;

                            reclampText();

                            invalidate();
                        }
                    }
                    return true;
            }
            return false;
        }
    }

    /**
     * To detect click action in touch listener.
     *
     * @param startX
     * @param endX
     * @param startY
     * @param endY
     * @return
     */
    private boolean isAClick(float startX, float endX, float startY, float endY) {
        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);
        if (differenceX > CLICK_ACTION_THRESHHOLD/* =5 */ || differenceY > CLICK_ACTION_THRESHHOLD) {
            return false;
        }
        return true;
    }

    private void getNearestText(float touchX, float touchY) {
        for (int i = 0; i < texts.size(); i++) {
            Logger.output(TAG, "x : " + touchX);
            Logger.output(TAG, "start x : " + texts.get(i).textPosition.x);
            Logger.output(TAG, "end x : " + texts.get(i).endX);
            Logger.output(TAG, "y : " + touchY);
            Logger.output(TAG, "start y : " + texts.get(i).textPosition.y);
            Logger.output(TAG, "end y : " + texts.get(i).endY);

            float startX = texts.get(i).textPosition.x;
            float endX = texts.get(i).endX;
            float startY = texts.get(i).textPosition.y;
            float endY = texts.get(i).endY;

            if ((touchX > startX && touchX < endX) && (touchY > startY && touchY < endY)) {
                Logger.output(TAG, "text : " + texts.get(i).text);
                EventBus.getDefault().post(new TextEditImageEvent(texts.get(i).text, i));
                break;
            }
        }

    }

    protected void recalculateFocalPoint(MotionEvent event) {
        final int pointerCount = event.getPointerCount();
        if (pointerCount <= 0) {
            return;
        }

        focalPoint.x = 0f;
        focalPoint.y = 0f;
        for (int i = 0; i < pointerCount; i++) {
            focalPoint.x += event.getX(i);
            focalPoint.y += event.getY(i);
        }
        focalPoint.x /= pointerCount;
        focalPoint.y /= pointerCount;
    }

    @Override
    public void onDraw(Canvas canvas) {

        super.onDraw(canvas);

        canvas.save();

        if (drawCanvas != null && canvas != null) {
            drawCanvas.drawPath(mPath, drawPaint);
            canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
            canvas.restore();

//        if ( isInEditMode() && (0 == texts.size()) ) {
//                setText("sample text");
//            }
            // Get rectangle of the drawable


            imageRect.top = 0;
            imageRect.left = 0;

            Drawable drawable = getDrawable();
            if (null != drawable) {
                imageRect.right = drawable.getIntrinsicWidth();
                imageRect.bottom = drawable.getIntrinsicHeight();
            }
            // Translate and scale the rectangle
            getImageMatrix().mapRect(imageRect);

            for (TextProperties tp : texts) {
                canvas.save();
                if (rotationEnabled) {
                    canvas.rotate(-tp.rotation, tp.rotationCenter.x, tp.rotationCenter.y);
                }

                // Draw text
                float top = tp.textPosition.y + imageRect.top;
                for (int i = 0; i < tp.textLines.length; i++) {
                    int h = tp.textRects.get(i).height();
                    canvas.save();
                    canvas.translate(tp.textPosition.x + imageRect.left, top + h);
                    canvas.drawText(tp.textLines[i], 0, 0, tp.paint);
                    canvas.restore();
                    top += h + interline * tp.scale;
                }
                canvas.restore();
            }
        }
    }

    /**
     * Set text to be drawn over the image.
     *
     * @param text The text.
     */
    public void setText(String text) {
//        texts.clear();
        addText(text);
    }

    /**
     * Adds a text to be drawn over the image, above existing texts.
     *
     * @param text The text.
     */
    public void addText(String text) {
        if (null != text && !TextUtils.isEmpty(text)) {
            texts.add(new TextProperties(text, currentSize, currentColor));
            reclampText();
            invalidate();
        }
    }

    /**
     * Adds a text to be drawn over the image, above existing texts.
     *
     * @param text The text.
     */
    public void modifyText(String text, int position) {
        if (null != text && !TextUtils.isEmpty(text)) {
            TextProperties tp = texts.get(position);
            tp.setText(text);
//            texts.add(new TextProperties(text, currentSize, currentColor));
            reclampText();
            invalidate();
        }
    }

    public void setTextColor(int color) {
        if (!isDrawEnabled) {
            if (texts.size() > 0) {
                TextProperties tp = texts.get(texts.size() - 1);
                tp.paint.setColor(color);
                reclampText();
                invalidate();
            }
        }
    }

    protected void reclampText() {
        if (0 == texts.size()) {
            return;
        }

        TextProperties tp = texts.get(texts.size() - 1);
        switch (clampTextMode) {
            case UNLIMITED:
                break;
            case ORIGIN_INSIDE: {
                RectF enclosingRect = calculateEnclosingRect();
                enclosingRect.offset(-imageRect.left, -imageRect.top);
                tp.textPosition.x -= enclosingRect.left - between(enclosingRect.left, 0, imageRect.width());
                tp.textPosition.y -= enclosingRect.top - between(enclosingRect.top, 0, imageRect.height());

                invalidate();
                break;
            }
            case TEXT_INSIDE: {
                RectF enclosingRect = calculateEnclosingRect();
                enclosingRect.offset(-imageRect.left, -imageRect.top);
                tp.textPosition.x -= enclosingRect.left - between(enclosingRect.left, 0, imageRect.width() - enclosingRect.width());
                tp.textPosition.y -= enclosingRect.top - between(enclosingRect.top, 0, imageRect.height() - enclosingRect.height());

                tp.endX = tp.textPosition.x + enclosingRect.right;
                tp.endY = tp.textPosition.y + enclosingRect.bottom;

                Logger.output(TAG, " start x : " + tp.textPosition.x + " end x : " + tp.endX + " start y : " + tp.textPosition.y + " end Y : " + tp.endY);

                invalidate();
                break;
            }
        }

        if (null != onTextMovedListener) {
            PointF position = getTextPosition();
            if ((!Float.isNaN(position.x)) && (!Float.isNaN(position.y))) {
                onTextMovedListener.textMoved(position);
            }
        }
    }

    protected RectF calculateEnclosingRect() {
        if (0 == texts.size()) {
            return null;
        }

        TextProperties tp = texts.get(texts.size() - 1);

        Matrix mat = new Matrix();
        RectF globalRect = new RectF();
        float top = tp.textPosition.y;
        for (int i = 0; i < tp.textLines.length; i++) {
            int h = tp.textRects.get(i).height();

            RectF rect = new RectF(0, 0, tp.textRects.get(i).width(), h);
            rect.offset(imageRect.left, imageRect.top);

            Logger.output(TAG, " height : " + h + " w : " + tp.textRects.get(i).width());

            mat.reset();
            mat.preRotate(-tp.rotation, tp.rotationCenter.x, tp.rotationCenter.y);
            mat.preTranslate(tp.textPosition.x, top);

            mat.mapRect(rect);

            if (0 == i) {
                globalRect.set(rect);
            } else {
                globalRect.top = Math.min(globalRect.top, rect.top);
                globalRect.left = Math.min(globalRect.left, rect.left);
                globalRect.bottom = Math.max(globalRect.bottom, rect.bottom);
                globalRect.right = Math.max(globalRect.right, rect.right);
            }
            top += h + interline * tp.scale;
        }

        return globalRect;
    }

    protected static float between(float value, float min, float max) {
        return Math.max(Math.min(value, max), min);
    }

    /**
     * Return offset position between the text and the image. Considers both top left corners to the the calculation.
     *
     * @return Pointf containing x and y offsets, as a per-one value. Eg. (0,0)=top-left, (1,1)=bottom-right.
     */
    public PointF getTextPosition() {
        RectF enclosingRect = calculateEnclosingRect();
        enclosingRect.offset(-imageRect.left, -imageRect.top);
        return new PointF(enclosingRect.left / imageRect.width(), enclosingRect.top / imageRect.height());
    }

    private void setupDrawing() {
        mPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(Color.RED);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(10);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    private void touchStart(float x, float y) {
        // mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    public Bitmap getCanvasBitmap() {
        Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
        return bitmap;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touchUp() {
        mPath.lineTo(mX, mY);
        // commit the path to our offscreen
        drawCanvas.drawPath(mPath, drawPaint);
        // kill this so we don't double draw
        mPath.reset();
    }

    public void clearBitmap() {
        canvasBitmap.eraseColor(Color.TRANSPARENT);
        texts.clear();
        invalidate();
        System.gc();
    }

    public void setPaintColor(int color) {
        drawPaint.setColor(color);
        this.currentColor = color;
    }

    @Override
    public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
        if (scaleEnabled && (0 < texts.size())) {
            TextProperties tp = texts.get(texts.size() - 1);
            tp.scale *= scaleGestureDetector.getScaleFactor();
            tp.paint.setTextSize(Math.max(minSize, Math.min(tp.scale * tp.size, maxSize)));
            tp.scale = tp.paint.getTextSize() / tp.size;
            tp.setText(tp.text);
            reclampText();
            invalidate();
        }

        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {

    }

    @Override
    public void OnRotation(RotationGestureDetector rotationDetector) {
        if (rotationEnabled && (0 < texts.size())) {
            TextProperties tp = texts.get(texts.size() - 1);
            tp.rotation += rotationDetector.getAngle() - previousRotation;
            previousRotation = rotationDetector.getAngle();

            tp.rotationCenter.x = focalPoint.x;
            tp.rotationCenter.y = focalPoint.y;
            invalidate();
        }
    }

    /**
     * Set the typeface to use for the text.
     *
     * @param typeface The typeface to be used.
     */
    public void setTypeface(Typeface typeface) {
        if (texts.size() > 0) {
            TextProperties tp = texts.get(texts.size() - 1);
            tp.paint.setTypeface(typeface);
            reclampText();
            invalidate();
        }
    }

    /**
     * Set the listener to be fired when the text changes its location.
     *
     * @param listener the listener to be called, or null.
     */
    public void setOnTextMovedListener(OnTextMovedListener listener) {
        this.onTextMovedListener = listener;
    }

}
