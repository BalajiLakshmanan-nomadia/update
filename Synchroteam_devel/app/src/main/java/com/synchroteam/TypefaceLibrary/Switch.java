package com.synchroteam.TypefaceLibrary;


import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;


// TODO: Auto-generated Javadoc
/**
 * The Class Switch.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class Switch extends android.widget.Switch {
	
	/**
	 * Instantiates a new switch.
	 *
	 * @param context the context
	 */
	public Switch(Context context) {
		super(context);
	}

	/**
	 * Instantiates a new switch.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public Switch(Context context, AttributeSet attrs) {
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
