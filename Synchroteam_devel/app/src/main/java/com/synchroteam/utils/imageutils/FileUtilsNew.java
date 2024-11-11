package com.synchroteam.utils.imageutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.util.Pair;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FileUtilsNew {


    public static String compressImageFile(Context context,
                                           String path, boolean shouldOverride, Uri uri) {

        Bitmap scaledBitmap = null;

        try {
            Pair<Integer, Integer> imageHgtWdt = getImageHgtWdt(uri, context);
            int hgt, wdt;
            hgt = imageHgtWdt.first;
            wdt = imageHgtWdt.second;
            Log.d("tag", "Dynamic height$hgt width$wdt" + hgt + "===>" + wdt);

            try {
                Bitmap bm = getBitmapFromUri(uri, null, context);
                Log.d("tag", "original bitmap height width}===>" + bm.getHeight() + "===>" + bm.getWidth());

            } catch (Exception e) {
                e.printStackTrace();
            }
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilsKt.decodeFile(context, uri, wdt, hgt, ScalingLogic.FIT);
            if (unscaledBitmap != null) {
                if (!(unscaledBitmap.getWidth() <= 800 && unscaledBitmap.getHeight() <= 800)) {
                    // Part 2: Scale image
                    scaledBitmap = ScalingUtilsKt.createScaledBitmap(unscaledBitmap,
                            wdt, hgt, ScalingLogic.FIT);
                } else {
                    scaledBitmap = unscaledBitmap;
                }
            }


            // Store to tmp file
//            File mFolder = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),"Images");

            File mFolder = new File(context.getFilesDir()+"/Images");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }


            // Create a media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                    .format(new Date());
            String imageFileName = "IMG_" + timeStamp + "_";

            File tmpFile = new File(mFolder.getAbsolutePath(), imageFileName + ".png");

            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(tmpFile);
                scaledBitmap.compress(
                        Bitmap.CompressFormat.JPEG,
                        ScalingUtilsKt.getImageQualityPercent(tmpFile),
                        fos
                );
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();

            } catch (Exception e) {
                e.printStackTrace();
            }

            String compressedPath = "";
            if (tmpFile.exists() && tmpFile.length() > 0) {
                compressedPath = tmpFile.getAbsolutePath();
                if (shouldOverride) {
                    File srcFile = new File(path);
//                    File result = Files.copyTo(srcFile, true);
//                    FileUtils.copyFile(source, dest);
                    Log.d("tag", "Delete temp file ${tmpFile.delete()}" + tmpFile.getAbsolutePath());
                    Log.d("tag", "copied file ${srcFile.absolutePath}" + srcFile.getAbsolutePath());

                    File result = copyFile(tmpFile, srcFile);
                    Log.d("tag", "copied file ${result.absolutePath}" + result.getAbsolutePath());

                }
            }

            scaledBitmap.recycle();

            if (shouldOverride)
                return path;
            else
                return compressedPath;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static File copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
        return dest;
    }

    public static File copyFile(File sourceFile, File destFile) throws IOException {

        try {
            if (!destFile.exists()) {
//            destFile.createNewFile();
                destFile.mkdir();
            }

            FileChannel source = null;
            FileChannel destination = null;

            try {
                source = new FileInputStream(sourceFile).getChannel();
                destination = new FileOutputStream(destFile).getChannel();
                destination.transferFrom(source, 0, source.size());
            } finally {
                if (source != null) {
                    source.close();
                }
                if (destination != null) {
                    destination.close();
                }
            }
            return destFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private static Pair<Integer, Integer> getImageHgtWdt(Uri uri, Context context) {

        BitmapFactory.Options opt = new BitmapFactory.Options();

    /* by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded.
    If you try the use the bitmap here, you will get null.*/
        opt.inJustDecodeBounds = true;
        try {
            Bitmap bm = getBitmapFromUri(uri, opt, context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Float actualHgt = Float.valueOf((opt.outHeight));
        Float actualWdt = Float.valueOf((opt.outWidth));

    /*val maxHeight = 816.0f
    val maxWidth = 612.0f*/
        Float maxHeight = 720f;
        Float maxWidth = 1280f;
        Float imgRatio = Float.valueOf(actualWdt / actualHgt);
        Float maxRatio = maxWidth / maxHeight;

//    width and height values are set maintaining the aspect ratio of the image
        if (actualHgt > maxHeight || actualWdt > maxWidth) {

            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHgt;
                actualWdt = (imgRatio * actualWdt);
                actualHgt = maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWdt;
                actualHgt = (imgRatio * actualHgt);
                actualWdt = maxWidth;
            } else {
                actualHgt = maxHeight;
                actualWdt = maxWidth;
            }

        }
        return new Pair(actualHgt.intValue(), actualWdt.intValue());


    }

    private static Bitmap getBitmapFromUri(Uri uri, BitmapFactory.Options options, Context context)
            throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = context.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = null;
        if (options != null)
            image = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        else
            image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;

    }
}
