package com.synchroteam.HourMinuteCustomView;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.PopupWindow;

import com.synchroteam.synchroteam3.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * PopWindow for Date Pick
 */
public class HourMinSelectDialog extends PopupWindow implements OnClickListener {

    private static final int DEFAULT_MIN_HOUR = 0;
    private static final int DEFAULT_MIN_MIN = 0;

    private static final int DEFAULT_MAX_HOUR = 99;
    private static final int DEFAULT_MAX_MIN = 59;


    public Button cancelBtn;
    public Button confirmBtn;
    public LoopView hourLoopView;
    public LoopView minLoopView;
    public LoopView hourLabelView;
    public LoopView minLabelView;
    public View pickerContainerV;
    public View contentView;//root view

    private int maxHour; // max year
    private int maxMin; // max year
    private int hourPos = 0;
    private int minPos = 0;
    private int hourLabelPos = 0;
    private int timeMeridiemPos = 0;
    private Context mContext;
    private String textCancel;
    private String textConfirm;
    private int colorCancel;
    private int colorConfirm;
    private int btnTextsize;//text btnTextsize of cancel and confirm button
    private int viewTextSize;
    private int labelTextSize;
    Typeface typefaceCustom;


    List<String> hourList = new ArrayList();
    List<String> minList = new ArrayList();
    List<String> minuteLabelList = new ArrayList();
    List<String> hourLabelList = new ArrayList();

    public static class Builder {

        //Required
        private Context context;
        private OnTimePickedListener listener;

        public Builder(Context context, OnTimePickedListener listener) {
            this.context = context;
            this.listener = listener;

        }

        //Option
        private int minHour = DEFAULT_MIN_HOUR;
        private int maxHour = DEFAULT_MAX_HOUR;
        private int minMin = DEFAULT_MIN_MIN;
        private int maxMin = DEFAULT_MAX_MIN;


        private String textCancel = "Cancel";
        private String textConfirm = "Confirm";
        private String timeChose = getStrTime();
        private int colorCancel = Color.parseColor("#999999");
        private int colorConfirm = Color.parseColor("#303F9F");
        private int btnTextSize = 16;//text btnTextsize of cancel and confirm button
        private int viewTextSize = 18;
        private int labelTextSize = 18;

        public Builder textCancel(String textCancel) {
            this.textCancel = textCancel;
            return this;
        }

        public Builder textConfirm(String textConfirm) {
            this.textConfirm = textConfirm;
            return this;
        }

        public Builder timeChose(String timeChose) {
            this.timeChose = timeChose;
            return this;
        }

        public Builder colorCancel(int colorCancel) {
            this.colorCancel = colorCancel;
            return this;
        }

        public Builder colorConfirm(int colorConfirm) {
            this.colorConfirm = colorConfirm;
            return this;
        }

        /**
         * set btn text btnTextSize
         *
         * @param textSize dp
         */
        public Builder btnTextSize(int textSize) {
            this.btnTextSize = textSize;
            return this;
        }

        public Builder viewTextSize(int textSize) {
            this.viewTextSize = textSize;
            return this;
        }

        public Builder labelTextSize(int textSize) {
            this.labelTextSize = textSize;
            return this;
        }


        public HourMinSelectDialog build() {
            if (minHour > maxHour) {
                throw new IllegalArgumentException();
            }
            if (minMin > maxMin) {
                throw new IllegalArgumentException();
            }

            return new HourMinSelectDialog(this);
        }
    }

    public HourMinSelectDialog(Builder builder) {
        this.maxHour = builder.maxHour;
        this.maxMin = builder.maxMin;


        this.textCancel = builder.textCancel;
        this.textConfirm = builder.textConfirm;
        this.mContext = builder.context;
        this.mListener = builder.listener;
        this.colorCancel = builder.colorCancel;
        this.colorConfirm = builder.colorConfirm;
        this.btnTextsize = builder.btnTextSize;
        this.viewTextSize = builder.viewTextSize;
        this.labelTextSize = builder.labelTextSize;
        setSelectedDate(builder.timeChose);
        initView();
    }

    private OnTimePickedListener mListener;

    private void initView() {

        contentView = LayoutInflater.from(mContext).inflate(
                R.layout.layout_hour_minute_picker, null);
        cancelBtn = (Button) contentView.findViewById(R.id.btn_cancel);
        confirmBtn = (Button) contentView.findViewById(R.id.btn_confirm);
        hourLoopView = (LoopView) contentView.findViewById(R.id.picker_hour);
        minLoopView = (LoopView) contentView.findViewById(R.id.picker_min);
        hourLabelView = (LoopView) contentView.findViewById(R.id.picker_hour_label);
        minLabelView = (LoopView) contentView.findViewById(R.id.picker_minutes_label);
        pickerContainerV = contentView.findViewById(R.id.container_picker);
        typefaceCustom = Typeface.createFromAsset(this.mContext.getAssets(), this.mContext.getString(R.string.fontName_avenir_black));

        cancelBtn.setText(textCancel);
        confirmBtn.setText(textConfirm);
        cancelBtn.setTextColor(colorCancel);
        confirmBtn.setTextColor(colorConfirm);
        cancelBtn.setTextSize(btnTextsize);
        confirmBtn.setTextSize(btnTextsize);
        cancelBtn.setTypeface(typefaceCustom);
        confirmBtn.setTypeface(typefaceCustom);

        //do not loop,default can loop
        hourLoopView.setNotLoop();
        minLoopView.setNotLoop();
        hourLabelView.setNotLoop();
        minLabelView.setNotLoop();

        //set loopview text btnTextsize
        hourLoopView.setTextSize(viewTextSize);
        minLoopView.setTextSize(viewTextSize);
        hourLabelView.setTextSize(labelTextSize);
        minLabelView.setTextSize(labelTextSize);

        //set checked listen
        hourLoopView.setListener(new LoopListener() {
            @Override
            public void onItemSelect(int item) {
                hourPos = item;
                //initDayPickerView();
            }
        });
        minLoopView.setListener(new LoopListener() {
            @Override
            public void onItemSelect(int item) {
                minPos = item;
                //initDayPickerView();
            }
        });
        hourLabelView.setListener(new LoopListener() {
            @Override
            public void onItemSelect(int item) {
                hourLabelPos = item;
            }
        });

        minLabelView.setListener(new LoopListener() {
            @Override
            public void onItemSelect(int item) {
                timeMeridiemPos = item;
            }
        });

        initPickerViews(); // init year and month loop view
        //initDayPickerView(); //init day loop view

        cancelBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        contentView.setOnClickListener(this);

        setTouchable(true);
        setFocusable(true);
        // setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
        //setAnimationStyle(R.style.FadeInPopWin);
        setContentView(contentView);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
    }

    /**
     * Init year and month loop view,
     * Let the day loop view be handled separately
     */
    private void initPickerViews() {

        int hourCount = maxHour;
        int minCount = maxMin;


        for (int i = 0; i <= hourCount; i++) {
            hourList.add(format2LenStr(i));
        }

        for (int j = 0; j <= minCount; j++) {
            minList.add(format2LenStr(j));
        }

        hourLoopView.setArrayList((ArrayList) hourList);
        hourLoopView.setInitPosition(hourPos);

        minLoopView.setArrayList((ArrayList) minList);
        minLoopView.setInitPosition(minPos);


        hourLabelList.add(this.mContext.getString(R.string.hours_label));
        hourLabelView.setArrayList((ArrayList) hourLabelList);
        hourLabelView.setInitPosition(hourLabelPos);

        minuteLabelList.add(this.mContext.getString(R.string.minutes_label));
        minLabelView.setArrayList((ArrayList) minuteLabelList);
        minLabelView.setInitPosition(timeMeridiemPos);

    }


    /**
     * set selected date position value when initView.
     *
     * @param dateStr
     */
    public void setSelectedDate(String dateStr) {

        if (!TextUtils.isEmpty(dateStr)) {

            //parse the hour and minute earlier position
            String[] hourMinute = dateStr.split(":");
            int initHourPos = 0;
            int initMinPos = 0;
            if (hourMinute != null && hourMinute.length > 0) {
                initHourPos = Integer.parseInt(hourMinute[0]);
                initMinPos = Integer.parseInt(hourMinute[1]);
            }

            hourPos = initHourPos;
            minPos = initMinPos;

            hourLabelPos = 0;
            timeMeridiemPos = 0;

//            long milliseconds = getLongFromyyyyMMdd(dateStr);
//            Calendar calendar = Calendar.getInstance(Locale.getDefault());
//
//            if (milliseconds != -1) {
//
//                calendar.setTimeInMillis(milliseconds);
//                hourPos = initHourPos;
//                minPos = initMinPos;
//
//                hourLabelPos=0;
//                timeMeridiemPos = 0;
//
//            }
        }
    }

    /**
     * Show date picker popWindow
     *
     * @param activity
     */
    public void showPopWin(Activity activity) {

        if (null != activity) {

            TranslateAnimation trans = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                    0, Animation.RELATIVE_TO_SELF, 1,
                    Animation.RELATIVE_TO_SELF, 0);

            showAtLocation(activity.getWindow().getDecorView(), Gravity.BOTTOM,
                    0, 0);
            trans.setDuration(400);
            trans.setInterpolator(new AccelerateDecelerateInterpolator());

            pickerContainerV.startAnimation(trans);
        }
    }

    /**
     * Dismiss date picker popWindow
     */
    public void dismissPopWin() {

        TranslateAnimation trans = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1);

        trans.setDuration(400);
        trans.setInterpolator(new AccelerateInterpolator());
        trans.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                dismiss();
            }
        });

        pickerContainerV.startAnimation(trans);
    }

    @Override
    public void onClick(View v) {

        if (v == contentView || v == cancelBtn) {
            dismissPopWin();
        } else if (v == confirmBtn) {

            if (null != mListener) {

                int hour = hourPos;
                int min = minPos;
                int hourPos = hourLabelPos;
                int meredium = timeMeridiemPos;

                String hourLabel = "";
                if (hourPos == 0) {
                    hourLabel = "Hour(s)";
                }
                String minLabel = "";
                if (meredium == 0) {
                    minLabel = "Minute(s)";
                }

                StringBuffer sb = new StringBuffer();

                String hourStr = "";
                if (hour < 10) {
                    hourStr = "0" + hour;
                } else {
                    hourStr = "" + String.valueOf(hour);
                }
                sb.append(hourStr);
                sb.append(":");
                sb.append(format2LenStr(min));


                mListener.onTimePickCompleted(hour, min, hourLabel, minLabel, sb.toString());
            }

            dismissPopWin();
        }
    }

    /**
     * get long from hh:mm:ss
     *
     * @param time
     * @return
     */
    public static long getLongFromyyyyMMdd(String time) {
        SimpleDateFormat mFormat = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
        Date parse = null;
        try {
            parse = mFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (parse != null) {
            return parse.getTime();
        } else {
            return -1;
        }
    }

    public static String getStrTime() {
        SimpleDateFormat dd = new SimpleDateFormat("hh:mm:ss a", Locale.getDefault());
        return dd.format(new Date());
    }

    /**
     * Transform int to String with prefix "0" if less than 10
     *
     * @param num
     * @return
     */
    public static String format2LenStr(int num) {

        return (num < 10) ? "0" + num : String.valueOf(num);
    }

    public static int spToPx(Context context, int spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


    public interface OnTimePickedListener {

        /**
         * Listener when date has been checked
         *
         * @param hour
         * @param min
         * @param hourLabel
         * @param minLabel
         * @param timeDesc  HH:mm:ss
         */
        void onTimePickCompleted(int hour, int min, String hourLabel, String minLabel,
                                 String timeDesc);
    }
}