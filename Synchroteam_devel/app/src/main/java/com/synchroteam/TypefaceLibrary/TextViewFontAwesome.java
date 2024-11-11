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
public class TextViewFontAwesome extends AppCompatTextView {
	public TextViewFontAwesome(Context context) {
		super(context);
		applyCustomFont(context);
	}

	public TextViewFontAwesome(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
		applyCustomFont(context);
	}

	public TextViewFontAwesome(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		applyCustomFont(context);
	}

	private void applyCustomFont(Context context) {
		Typeface customFont = FontCache.getTypeface(context.getString(R.string.fontName_fontAwesome), context);
		setTypeface(customFont);
	}
}
