package com.synchroteam.customui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by Trident on 2/21/2017.
 */

public class DrawingImageView extends ImageView {
    // onTouch
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private Path mPath;

    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xFF660000;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;

    public DrawingImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        setupDrawing();
    }

    public DrawingImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setupDrawing();
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        Drawable d = getDrawable();
//
//        if(d!=null){
//            // ceil not round - avoid thin vertical gaps along the left/right edges
//            int width = MeasureSpec.getSize(widthMeasureSpec);
//            int height = (int) Math.ceil((float) width * (float) d.getIntrinsicHeight() / (float) d.getIntrinsicWidth());
//            setMeasuredDimension(width, height);
//        }else{
//            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        }
//    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
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
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.save();
        super.onDraw(canvas);

        drawCanvas.drawPath(mPath, drawPaint);
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.restore();
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

    public Bitmap getCanvasBitmap(){
        Bitmap bitmap = ((BitmapDrawable)getDrawable()).getBitmap();
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

    public void clearBitmap(){
        canvasBitmap.eraseColor(Color.TRANSPARENT);
        invalidate();
        System.gc();
    }

    public void setPaintColor(int color){
        drawPaint.setColor(color);
    }
}
