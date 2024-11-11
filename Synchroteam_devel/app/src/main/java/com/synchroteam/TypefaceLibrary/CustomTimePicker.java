package com.synchroteam.TypefaceLibrary;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TimePicker;

// TODO: Auto-generated Javadoc
/**
 * The Class CustomTimePicker.
 */
public class CustomTimePicker extends TimePickerDialog{
 
            /** The Constant TIME_PICKER_INTERVAL. */
            public static final int TIME_PICKER_INTERVAL=15;
            
            /** The m ignore event. */
            private boolean mIgnoreEvent=false;
 
            /**
             * Instantiates a new custom time picker.
             *
             * @param context the context
             * @param callBack the call back
             * @param hourOfDay the hour of day
             * @param minute the minute
             * @param is24HourView the is24 hour view
             */
            public CustomTimePicker(Context context, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView) {
            super(context, callBack, hourOfDay, minute, is24HourView);
            }
     
            /* (non-Javadoc)
             * @see android.app.TimePickerDialog#onTimeChanged(android.widget.TimePicker, int, int)
             */
            @Override
            public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
                super.onTimeChanged(timePicker, hourOfDay, minute);
                if (!mIgnoreEvent){
                    minute = getRoundedMinute(minute);
                    mIgnoreEvent=true;
                    timePicker.setCurrentMinute(minute);
                    mIgnoreEvent=false;
                }
            }
 
            /**
             * Gets the rounded minute.
             *
             * @param minute the minute
             * @return the rounded minute
             */
            public static  int getRoundedMinute(int minute){
                 if(minute % TIME_PICKER_INTERVAL != 0){
                    int minuteFloor = minute - (minute % TIME_PICKER_INTERVAL);
                    minute = minuteFloor + (minute == minuteFloor + 1 ? TIME_PICKER_INTERVAL : 0);
                    if (minute == 60)  minute=0;
                 }
 
                return minute;
            }
        }
      