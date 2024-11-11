package com.synchroteam.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.synchroteam3.R;

// TODO: Auto-generated Javadoc
/**
 * The Class MultipleDeviceNotSupported is shown dialog alert for multiple
 * device login.
 * 
 * @author ${Ideavate Solution}
 */
public class ErrorDialog extends Dialog {

	/**
	 * Instantiates a MultipleDeviceNotSupportedDialog
	 * 
	 * @param activity
	 *            the activity
	 * @param errorMsg
	 *            the error message
	 */
	public ErrorDialog(Activity activity, String errorMsg) {
		super(activity, android.R.style.Theme_Translucent_NoTitleBar);
		setCancelable(false);
		setContentView(R.layout.layout_error_dialog);
		this.getWindow().setGravity(Gravity.CENTER);

		findViewById(R.id.okBtn).setOnClickListener(onClickListener);
		((TextView) findViewById(R.id.txtErrMsg)).setText(errorMsg);

	}

	/** The on click listener. */
	android.view.View.OnClickListener onClickListener = new android.view.View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			if (id == R.id.okBtn) {
				dismiss();
			}

		}
	};
}
