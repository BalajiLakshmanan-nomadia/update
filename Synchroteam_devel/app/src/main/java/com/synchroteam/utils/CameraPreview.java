package com.synchroteam.utils;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

// TODO: Auto-generated Javadoc
/**
 *  A basic Camera preview class.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    
    /** The m holder. */
    private SurfaceHolder mHolder;
    
    /** The m camera. */
    private Camera mCamera;
    
    /** The preview callback. */
    private PreviewCallback previewCallback;
    
    /** The auto focus callback. */
    private AutoFocusCallback autoFocusCallback;

    /**
     * Instantiates a new camera preview.
     *
     * @param context the context
     * @param camera the camera
     * @param previewCb the preview cb
     * @param autoFocusCb the auto focus cb
     */
    @SuppressWarnings("deprecation")
	public CameraPreview(Context context, Camera camera,
                         PreviewCallback previewCb,
                         AutoFocusCallback autoFocusCb) {
        super(context);
        mCamera = camera;
        previewCallback = previewCb;
        autoFocusCallback = autoFocusCb;

        /* 
         * Set camera to continuous focus if supported, otherwise use
         * software auto-focus. Only works for API level >=9.
         */
        /*
        Camera.Parameters parameters = camera.getParameters();
        for (String f : parameters.getSupportedFocusModes()) {
            if (f == Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) {
                mCamera.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                autoFocusCallback = null;
                break;
            }
        }
        */

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);

        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    /* (non-Javadoc)
     * @see android.view.SurfaceHolder.Callback#surfaceCreated(android.view.SurfaceHolder)
     */
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
        	if(mCamera != null) {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            }
        } catch (IOException e) {
            Log.d("DBG", "Error setting camera preview: " + e.getMessage());
        }
    }

    /* (non-Javadoc)
     * @see android.view.SurfaceHolder.Callback#surfaceDestroyed(android.view.SurfaceHolder)
     */
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Camera preview released in activity
    }

    /* (non-Javadoc)
     * @see android.view.SurfaceHolder.Callback#surfaceChanged(android.view.SurfaceHolder, int, int, int)
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        /*
         * If your preview can change or rotate, take care of those events here.
         * Make sure to stop the preview before resizing or reformatting it.
         */
        if (mHolder.getSurface() == null){
          // preview surface does not exist
          return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
          // ignore: tried to stop a non-existent preview
        	Logger.printException(e);
        }

        try {
            // Hard code camera surface rotation 90 degs to match Activity view in portrait
            mCamera.setDisplayOrientation(90);

            mCamera.setPreviewDisplay(mHolder);
            mCamera.setPreviewCallback(previewCallback);
            mCamera.startPreview();
            mCamera.autoFocus(autoFocusCallback);
        } catch (Exception e){
            Log.d("DBG", "Error starting camera preview: " + e.getMessage());
        }
    }


}
