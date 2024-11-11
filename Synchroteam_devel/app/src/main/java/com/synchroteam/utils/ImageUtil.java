package com.synchroteam.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;
import com.synchroteam.synchroteam3.R;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

    public class ImageUtil {
        /**
         * Save bitmap to Database. Previous Developer Code
         *
         * @param bitmap the bitmap
         */
        public static void  saveBitmap(Bitmap bitmap, Context context) {
            FileOutputStream fos = null;
            try {
                File f = new File(context.getString(R.string.barcodeFilePath));
                String name = f.getAbsolutePath();
                fos = new FileOutputStream(name);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);


                fos.flush();
                fos.close();
            } catch (Exception e) {

            }
        }

        /**
         * Gets the image from the current path
         * @param capturedPath
         * @return
         */
        public static byte[] getSavedPhoto(String capturedPath,
                                           int resisingWidth,int resisingHeight) {
            byte[] byteArray=null;

            try {

                File capturedFile = new File(capturedPath);
                String fileName = capturedFile.getName();

                String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
                FileInputStream fis = new FileInputStream(capturedPath);

                if (resisingWidth > 1024 || resisingHeight > 1024) {
                    resisingWidth = 1024;
                    resisingHeight = 1024;
                }

                Bitmap bm2 = resizeBitmap(BitmapFactory.decodeStream(fis),
                        resisingWidth, resisingHeight);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm2.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byteArray = stream.toByteArray();


            } catch (Exception e) {
                byteArray=null;
            }

            return byteArray;
        }

        /**
         * Delete the image
         */
        public static void deleteTempImage(String filePath) {
            File f = new File(filePath);
            if (f.exists())
                f.delete();
        }

        public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth, int newHeight) {
            int width, height;
            float scaleWidth, scaleHeight;
            Matrix matrix;
            width = bitmap.getWidth();
            height = bitmap.getHeight();

            scaleWidth = ((float) newWidth) / width;
            scaleHeight = ((float) newHeight) / height;
            float scale = Math.min(scaleWidth, scaleHeight);
            scale = Math.min(scale, 1);
            matrix = new Matrix();
            matrix.postScale(scale, scale);
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
                    matrix, true);

            return (resizedBitmap);

        }


        public static String  savingImage(byte[] check,Context context) {
            String capturedPath="";
            FileOutputStream outStream = null;
            try {
                // Write to SD Card
                File file =createImageFile(context);

                Log.e("TAG","IMAGE SIZE IS===>"+file.getAbsolutePath());
                capturedPath=file.getAbsolutePath();

                outStream = new FileOutputStream(file);
                outStream.write(check);
                outStream.close();


            } catch (Exception e) {
                e.printStackTrace();
                capturedPath="";
            }  finally {

            }

            return capturedPath;
        }

        /**
         * Create a File for saving an image or video.
         *
         * @return the output media file
         */
        public static File createImageFile(Context context) throws IOException {
            // Create a media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                    .format(new Date());
            String imageFileName = "IMG_" + timeStamp + "_";

            File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "ScannerImage");

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

            return image;
        }
    }


