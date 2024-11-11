package com.synchroteam.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by Trident on 1/24/2017.
 */

public class MyFixedListView extends ListView {
    public MyFixedListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyFixedListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        try {
            super.dispatchDraw(canvas);
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }

    }
}
