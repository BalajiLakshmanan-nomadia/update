package com.synchroteam.utils;

/**
 * Copyright 2010 Lukasz Szmit <devmail@szmit.eu>
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.ViewSwitcher;

import com.synchroteam.synchroteam3.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimePicker extends RelativeLayout implements
        View.OnClickListener, OnDateChangedListener, OnTimeChangedListener {

    // DatePicker reference
    private DatePicker datePicker;
    // TimePicker reference
    private TimePicker timePicker;
    // ViewSwitcher reference
    private ViewSwitcher viewSwitcher;
    // Calendar reference
    private Calendar mCalendar;

    // textview or full date
    TextView txtFullDate;

    // Constructor start
    @SuppressLint("NewApi")
    public DateTimePicker(Context context) {
        this(context, null);
    }

    public DateTimePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DateTimePicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        // Get LayoutInflater instance
        final LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // Inflate myself
        inflater.inflate(R.layout.datetimepicker, this, true);

        // Inflate the date and time picker views
        final LinearLayout datePickerView = (LinearLayout) inflater.inflate(
                R.layout.datepicker, null);
        final LinearLayout timePickerView = (LinearLayout) inflater.inflate(
                R.layout.timepicker, null);

        // Grab a Calendar instance
        mCalendar = Calendar.getInstance();
        // Grab the ViewSwitcher so we can attach our picker views to it
        viewSwitcher = (ViewSwitcher) this.findViewById(R.id.DateTimePickerVS);

        // Init date picker
        datePicker = (DatePicker) datePickerView.findViewById(R.id.DatePicker);

        txtFullDate = (TextView) datePickerView
                .findViewById(R.id.txt_full_date);

        datePicker.init(mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH), this);

        txtFullDate.setText(DateFormatUtils.getDateString(
                mCalendar.get(Calendar.DAY_OF_MONTH),
                mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.YEAR)));

        //Hide the full date view in picker, if it is above or from lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            txtFullDate.setVisibility(GONE);
        }

        // Init time picker
        timePicker = (TimePicker) timePickerView.findViewById(R.id.TimePicker);
        timePicker.setOnTimeChangedListener(this);

        // Handle button clicks
        ((Button) findViewById(R.id.SwitchToTime)).setOnClickListener(this); // shows
        // the
        // time
        // picker
        ((Button) findViewById(R.id.SwitchToDate)).setOnClickListener(this); // shows
        // the
        // date
        // picker

        // Populate ViewSwitcher
        viewSwitcher.addView(datePickerView, 0);
        viewSwitcher.addView(timePickerView, 1);
    }

    // Constructor end

    // Called every time the user changes DatePicker values
    public void onDateChanged(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
        // Update the internal Calendar instance
        mCalendar.set(year, monthOfYear, dayOfMonth,
                mCalendar.get(Calendar.HOUR_OF_DAY),
                mCalendar.get(Calendar.MINUTE));

        txtFullDate.setText(DateFormatUtils.getDateString(dayOfMonth,
                monthOfYear, year));
    }

    // Called every time the user changes TimePicker values
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        // Update the internal Calendar instance
        mCalendar.set(mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
    }

    // Handle button clicks
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.SwitchToDate:
                v.setEnabled(false);
                findViewById(R.id.SwitchToTime).setEnabled(true);
                viewSwitcher.showPrevious();
                break;

            case R.id.SwitchToTime:
                v.setEnabled(false);
                findViewById(R.id.SwitchToDate).setEnabled(true);
                viewSwitcher.showNext();
                break;
        }
    }

    // Convenience wrapper for internal Calendar instance
    public int get(final int field) {
        return mCalendar.get(field);
    }

    // To get the name of the month
    public Date getCurrentDate() {
        return mCalendar.getTime();
    }

    // set minimum date as current date
    @SuppressLint("NewApi")
    public void setMinimumDate() {
        datePicker.setMinDate(System.currentTimeMillis() - 1000);
    }

    // Convenience wrapper for internal Calendar instance
    public String get() {
        NumberFormat f = new DecimalFormat("00");
        String hrs = f.format(mCalendar.MINUTE);
        return hrs;
    }

    // Reset DatePicker, TimePicker and internal Calendar instance
    public void reset() {
        final Calendar c = Calendar.getInstance();
        updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));
        updateTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE));
    }

    // Convenience wrapper for internal Calendar instance
    public long getDateTimeMillis() {
        return mCalendar.getTimeInMillis();
    }

    // Convenience wrapper for internal TimePicker instance
    public void setIs24HourView(boolean is24HourView) {
        timePicker.setIs24HourView(is24HourView);
    }

    // Convenience wrapper for internal TimePicker instance
    public boolean is24HourView() {
        return timePicker.is24HourView();
    }

    // Convenience wrapper for internal DatePicker instance
    public void updateDate(int year, int monthOfYear, int dayOfMonth) {
        datePicker.updateDate(year, monthOfYear, dayOfMonth);
    }

    // Convenience wrapper for internal TimePicker instance
    public void updateTime(int currentHour, int currentMinute) {
        timePicker.setCurrentHour(currentHour);
        timePicker.setCurrentMinute(currentMinute);
    }
}
