package com.synchroteam.utils;

import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;

/**
 * This class used to catch the event when the views are finished drawn and the
 * layout process is completed.
 * 
 * @author Trident
 *
 */
public class ViewDrawnEventCatcher {

	/**
	 * Used to do the functions after the views have been finished drawing.
	 * 
	 * @param view
	 * @param runnable
	 */
	public static void runJustBeforeBeingDrawn(final View view,
			final Runnable runnable) {
		final ViewTreeObserver vto = view.getViewTreeObserver();
		final OnPreDrawListener preDrawListener = new OnPreDrawListener() {
			@Override
			public boolean onPreDraw() {
				runnable.run();
				final ViewTreeObserver vto = view.getViewTreeObserver();
				vto.removeOnPreDrawListener(this);
				return true;
			}
		};
		vto.addOnPreDrawListener(preDrawListener);
	}
}
