package com.synchroteam.TypefaceLibrary;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

// TODO: Auto-generated Javadoc
/**
 * The Class TextClock.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class TextClock extends android.widget.TextClock {
	
	/**
	 * Instantiates a new text clock.
	 *
	 * @param context the context
	 */
	public TextClock(Context context) {
		super(context);
	}

	/**
	 * Instantiates a new text clock.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public TextClock(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// return early for eclipse preview mode
		if (isInEditMode()) return;
		
		FontManager.getInstance().setFont(this, attrs);
	}
	
	/**
	 * Sets the font.
	 *
	 * @param fontPath the new font
	 */
	public void setFont(String fontPath) {
		FontManager.getInstance().setFont(this, fontPath);
	}
	
	/**
	 * Sets the font.
	 *
	 * @param resId the new font
	 */
	public void setFont(int resId) {
		String fontPath = getContext().getString(resId);
		setFont(fontPath);
	}
}
