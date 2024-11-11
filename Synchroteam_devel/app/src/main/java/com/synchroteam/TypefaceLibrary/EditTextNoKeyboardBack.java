package com.synchroteam.TypefaceLibrary;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

// TODO: Auto-generated Javadoc

/**
 * The Class EditTextNoKeyboardBack.
 */
public class EditTextNoKeyboardBack extends EditText {


    /**
     * The Interface OnKeyBoardBackPressListner.
     */
    public interface OnKeyBoardBackPressListner {

        /**
         * On back key pressed.
         */
        public void onBackKeyPressed();

    }


    /**
     * Instantiates a new edits the text.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public EditTextNoKeyboardBack(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    /**
     * Sets the on key back pressed listner.
     *
     * @param onKeyBoardBackPressListner the new on key back pressed listner
     */
    public void setOnKeyBackPressedListner(OnKeyBoardBackPressListner onKeyBoardBackPressListner) {


    }


    /* (non-Javadoc)
     * @see android.widget.TextView#onKeyPreIme(int, android.view.KeyEvent)
     */
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getAction() == KeyEvent.ACTION_UP)) {

//			onKeyBoardBackPressListner.onBackKeyPressed();


            return false;
        } else {
            return super.onKeyPreIme(keyCode, event);
        }

    }
}
