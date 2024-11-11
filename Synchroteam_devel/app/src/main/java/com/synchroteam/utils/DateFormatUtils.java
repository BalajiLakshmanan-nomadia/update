package com.synchroteam.utils;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


@SuppressLint("SimpleDateFormat")
public class DateFormatUtils {

    @SuppressLint("SimpleDateFormat")
    public static String getDateString(int day, int month, int year) {

        SimpleDateFormat fullDateFormat = new SimpleDateFormat(
                "EEEE, dd MMMM yyyy");
        SimpleDateFormat rawDateFormat = new SimpleDateFormat("yyyy/MM/dd");

        String strDate;
        String myDate = String.valueOf(day);
        String myMonth = String.valueOf(month);

        if (myDate.length() == 1) {
            myDate = "0" + day;
        }
        if (myMonth.length() == 1 && month < 9) {
            myMonth = "0" + (month + 1);
        } else {
            myMonth = String.valueOf((month + 1));
        }

        Date date = null;
        String strDate1 = year + "/" + myMonth + "/" + myDate;
        try {
            date = rawDateFormat.parse(strDate1);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        strDate = fullDateFormat.format(date);
        return strDate;

    }


    public static String addTwoDateTimeStamps(String date1, String date2) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date d1;
        Date d2;
        Date sumDate = null;

        try {
            d1 = format.parse(date1);
            d2 = format.parse(date2);

            long sum = d1.getTime() + d2.getTime();

            sumDate = new Date(sum);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return format.format(sumDate);
    }

    public static String subtractTwoDateTimeStamps(String date1, String date2) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date d1;
        Date d2;
        Date sumDate = null;

        try {
            d1 = format.parse(date1);
            d2 = format.parse(date2);

            long sum = d1.getTime() - d2.getTime();

            sumDate = new Date(sum);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return format.format(sumDate);
    }


    public static Boolean isFirstDateBeforeSecondDate(String date1, String date2) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date d1;
        Date d2;

        try {
            d1 = format.parse(date1);
            d2 = format.parse(date2);

            return d1.before(d2);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    public static String addHourToDateTime(String date, int hourToAdd) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Date d = null;

        try {
            d = format.parse(date);

            Calendar cal = Calendar.getInstance(); // creates calendar
            assert d != null;
            cal.setTime(d); // sets calendar time/date
            cal.add(Calendar.HOUR_OF_DAY, hourToAdd); // adding hour

            d = cal.getTime(); // returns new date object

        } catch (Exception e) {
            e.printStackTrace();
        }

        return format.format(d);
    }

    public static String getFormattedDateFromAPIDate(String apiTime) {
        String strTimeStamp = "";
        if (apiTime != null) {
            try {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                Date result = df.parse(apiTime);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                if (result != null) {
                    strTimeStamp = sdf.format(result);
                }
//            String[] authExpiry=apiTime.split("T");
//            String authTest=authExpiry[1].substring(0,8);
//            strTimeStamp=authExpiry[0]+" "+authTest;
//            Logger.log("TAG","CHECK DATE FORMAT====>"+authExpiry[0]+" "+authTest);
//            Logger.log("TAG","CHECK DATE FORMAT====>"+strTimeStamp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return strTimeStamp;
    }

    public static boolean getFormattedDateFromAPIDateBase(String apiTime) {
        String strTimeStamp = null;
        try {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            TimeZone utcZone = TimeZone.getTimeZone("UTC");
            simpleDateFormat.setTimeZone(utcZone);
            Date myDate = simpleDateFormat.parse(apiTime);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            assert myDate != null;
            strTimeStamp = sdf.format(myDate);
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            Date dateFinalResult = df.parse(strTimeStamp);
            assert dateFinalResult != null;
            long res = dateFinalResult.getTime();
            Calendar cal = Calendar.getInstance(Locale.getDefault());
            long call = cal.getTimeInMillis();
            if (res > call) {
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
//            e.printStackTrace();
            return false;
        }

    }
}