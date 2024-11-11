package com.synchroteam.utils;

import android.media.ExifInterface;

import java.io.IOException;

/**
 * Created by Trident on 9/15/2016.
 */
public class ExifData {

    public static void copyExif(String oldPath, String newPath) throws IOException
    {
        ExifInterface oldExif = new ExifInterface(oldPath);

        //commented date & gps attributes due to legal issues (12.01.2017).
        String[] attributes = new String[]
                {
                        ExifInterface.TAG_APERTURE,
                        ExifInterface.TAG_METERING_MODE,
//                        ExifInterface.TAG_DATETIME,
//                        ExifInterface.TAG_DATETIME_DIGITIZED,
                        ExifInterface.TAG_EXPOSURE_TIME,
                        ExifInterface.TAG_FLASH,
                        ExifInterface.TAG_FOCAL_LENGTH,
//                        ExifInterface.TAG_GPS_ALTITUDE,
//                        ExifInterface.TAG_GPS_ALTITUDE_REF,
//                        ExifInterface.TAG_GPS_DATESTAMP,
//                        ExifInterface.TAG_GPS_LATITUDE,
//                        ExifInterface.TAG_GPS_LATITUDE_REF,
//                        ExifInterface.TAG_GPS_LONGITUDE,
//                        ExifInterface.TAG_GPS_LONGITUDE_REF,
//                        ExifInterface.TAG_GPS_PROCESSING_METHOD,
//                        ExifInterface.TAG_GPS_TIMESTAMP,
                        ExifInterface.TAG_IMAGE_LENGTH,
                        ExifInterface.TAG_IMAGE_WIDTH,
                        ExifInterface.TAG_ISO,
                        ExifInterface.TAG_MAKE,
                        ExifInterface.TAG_MODEL,
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.TAG_SUBSEC_TIME,
                        ExifInterface.TAG_SUBSEC_TIME_DIG,
                        ExifInterface.TAG_SUBSEC_TIME_ORIG,
                        ExifInterface.TAG_WHITE_BALANCE,
                        ExifInterface.TAG_EXIF_VERSION
                };

        ExifInterface newExif = new ExifInterface(newPath);
        for (int i = 0; i < attributes.length; i++)
        {
            String value = oldExif.getAttribute(attributes[i]);
            if (value != null)
                newExif.setAttribute(attributes[i], value);
        }
        newExif.saveAttributes();
    }

}
