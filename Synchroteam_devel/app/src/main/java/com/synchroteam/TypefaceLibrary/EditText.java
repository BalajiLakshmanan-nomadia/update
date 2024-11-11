package com.synchroteam.TypefaceLibrary;


import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.synchroteam.synchroteam3.R;


// TODO: Auto-generated Javadoc

/**
 * The Class EditText.
 */
public class EditText extends android.widget.EditText {


    private KeyImeChange keyImeChangeListener;


    /**
     * Instantiates a new edits the text.
     *
     * @param context the context
     */
    public EditText(Context context) {
        super(context);
        FontManager.getInstance().setFont(this,
                context.getString(R.string.fontName_hevelicaLight));
    }

    /**
     * Instantiates a new edits the text.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public EditText(Context context, AttributeSet attrs) {
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


    public void setKeyImeChangeListener(KeyImeChange listener) {
        keyImeChangeListener = listener;
    }

    public interface KeyImeChange {
        public void onKeyIme(int keyCode, KeyEvent event);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyImeChangeListener != null) {
            keyImeChangeListener.onKeyIme(keyCode, event);
        }
        return false;
    }

}