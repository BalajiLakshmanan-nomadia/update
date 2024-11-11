package com.synchroteam.TypefaceLibrary;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.synchroteam.synchroteam3.R;



// TODO: Auto-generated Javadoc
/**
 * The Class FontManager.
 */
public class FontManager {
	
	/** The s instance. */
	private static FontManager sInstance;
	
	/** The m cache. */
	private Map<String, Typeface> mCache;

	/**
	 * Instantiates a new font manager.
	 */
	private FontManager() {
		mCache = new HashMap<String, Typeface>();
	}

	/**
	 * Gets the single instance of FontManager.
	 *
	 * @return single instance of FontManager
	 */
	public static FontManager getInstance() {
		if (sInstance == null) {
			sInstance = new FontManager();
		}

		return sInstance;
	}

	/**
	 * Sets the font.
	 *
	 * @param tv the tv
	 * @param attrs the attrs
	 */
	public void setFont(TextView tv, AttributeSet attrs) {
		String fontName = getFontName(tv.getContext(), attrs);
		setFont(tv, fontName);
	}
	
	
	/**
	 * Sets the font.
	 *
	 * @param tv the tv
	 * @param fontName the font name
	 */
	public void setFont(TextView tv, String fontName) {
		if (fontName == null) return;
		
		Typeface typeface = mCache.get(fontName);
		if (typeface == null) {
			typeface = Typeface.createFromAsset(tv.getContext().getAssets(), fontName);
			mCache.put(fontName, typeface);
		}
		
		tv.setTypeface(typeface);
	}
	
	/**
	 * Gets the font name.
	 *
	 * @param c the c
	 * @param attrs the attrs
	 * @return the font name
	 */
	public static String getFontName(Context c, AttributeSet attrs) {
		TypedArray arr = c.obtainStyledAttributes(attrs, R.styleable.Fontify);
		String fontName = arr.getString(R.styleable.Fontify_font_name);
		arr.recycle();
		return fontName;
	}
}
