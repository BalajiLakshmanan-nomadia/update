package com.synchroteam.TypefaceLibrary;

import android.content.Context;
import android.graphics.Typeface;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.synchroteam.synchroteam3.R;

/**
 *
 */
public class TextViewAvenirBlack extends AppCompatTextView {
	public TextViewAvenirBlack(Context context) {
		super(context);
		applyCustomFont(context);
	}

	public TextViewAvenirBlack(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		applyCustomFont(context);
	}

	public TextViewAvenirBlack(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		applyCustomFont(context);
	}

	private void applyCustomFont(Context context) {
		Typeface customFont = FontCache.getTypeface(context.getString(R.string.fontName_avenir_black), context);
		setTypeface(customFont);
	}
}