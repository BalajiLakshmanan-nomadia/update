package com.synchroteam.TypefaceLibrary;

import android.content.Context;
import android.util.AttributeSet;

// TODO: Auto-generated Javadoc
/**
 * The Class Button.
 */
public class Button extends android.widget.Button {
	
	/**
	 * Instantiates a new button.
	 *
	 * @param context the context
	 */
	public Button(Context context) {
		super(context);
	}

	/**
	 * Instantiates a new button.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public Button(Context context, AttributeSet attrs) {
		super(context, attrs);

		// return early for eclipse preview mode
		if (isInEditMode())
			return;

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