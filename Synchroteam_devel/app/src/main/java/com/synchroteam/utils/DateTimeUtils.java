package com.synchroteam.utils;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {
    public static synchronized String getDateWithRequiredPattern(String date,
                                                                 String datePattern, DateFormat requiredDatePattern)
    {

        String requiredDate = null;

        SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
        DateFormat formatterOut = requiredDatePattern;

        try {

            Date outputDate = formatter.parse(date);
            assert outputDate != null;
            requiredDate = formatterOut.format(outputDate);

        } catch (Exception e) {
            e.printStackTrace();
            requiredDate = date;
        }

        return requiredDate;
    }

    public static synchronized String validateDateWithRequiredPattern (String date, DateFormat datePattern, String
            requiredDatePattern){
        String requiredDate = null;

        SimpleDateFormat formatter = new SimpleDateFormat(requiredDatePattern);
        DateFormat formatterOut = datePattern;

        try {
            Date outputDate = formatterOut.parse(date);
            assert outputDate != null;
            requiredDate = formatter.format(outputDate);

        } catch (Exception e) {
           e.printStackTrace();
           requiredDate = date;
        }
        return requiredDate;
    }


    public static synchronized String getTimeWithRequiredFormatPattern(String date,
                                                                       String datePattern, Format requiredDatePattern)
    {

        String requiredDate = null;

        SimpleDateFormat formatter = new SimpleDateFormat(datePattern);
        Format formatterOut = requiredDatePattern;

        try {

            Date outputDate = formatter.parse(date);
            requiredDate = formatterOut.format(outputDate);


        } catch (Exception e) {
            e.printStackTrace();
            requiredDate = date;
        }

        return requiredDate;
    }

}
