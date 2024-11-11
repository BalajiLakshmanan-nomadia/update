package com.synchroteam.utils;

import android.content.Context;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * View pager that allows only right swipe action.
 * Created by Trident on 1/27/2017.
 */

public class RightSwipeableViewPager extends ViewPager {

    private float initialXValue;

    public RightSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (this.isSwipedLeft(ev)) {
            return super.onTouchEvent(ev);
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (this.isSwipedLeft(ev)) {
            return super.onInterceptTouchEvent(ev);
        }
        return false;
    }


    private boolean isSwipedLeft(MotionEvent event) {

        if(event.getAction()==MotionEvent.ACTION_DOWN) {
            initialXValue = event.getX();
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            try {
                float diffX = event.getX() - initialXValue;
                //swipe from right to left
                if (diffX < 0) {
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
