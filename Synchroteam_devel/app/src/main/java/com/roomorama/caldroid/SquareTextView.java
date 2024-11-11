package com.roomorama.caldroid;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

// TODO: Auto-generated Javadoc
/**
 * The Class SquareTextView.
 */
public class SquareTextView extends TextView {

	/**
	 * Instantiates a new square text view.
	 *
	 * @param context the context
	 */
	public SquareTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new square text view.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 * @param defStyle the def style
	 */
	public SquareTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new square text view.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public SquareTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * @see android.widget.TextView#onMeasure(int, int)
	 */
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    super.onMeasure(widthMeasureSpec, widthMeasureSpec);
	}
	
	/* (non-Javadoc)
	 * @see android.view.View#onSizeChanged(int, int, int, int)
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	    super.onSizeChanged(w, w, oldw, oldh);
	}
}
