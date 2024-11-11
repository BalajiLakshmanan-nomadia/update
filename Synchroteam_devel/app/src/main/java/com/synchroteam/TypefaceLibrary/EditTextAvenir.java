package com.synchroteam.TypefaceLibrary;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.synchroteam.synchroteam3.R;


/**
 * Created by Rajesh on 6/29/2017.
 */

public class EditTextAvenir extends androidx.appcompat.widget.AppCompatEditText {

    public EditTextAvenir(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public EditTextAvenir(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public EditTextAvenir(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
        Typeface customFont = FontCache.getTypeface(context.getString(R.string.fontName_avenir), context);
        setTypeface(customFont);
    }
}
