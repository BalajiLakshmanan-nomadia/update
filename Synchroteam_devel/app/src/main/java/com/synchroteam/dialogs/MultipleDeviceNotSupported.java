package com.synchroteam.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;

import com.synchroteam.synchroteam3.R;

// TODO: Auto-generated Javadoc
/**
 * The Class MultipleDeviceNotSupported is shown dialog alert for multiple
 * device login.
 * 
 * @author ${Ideavate Solution}
 */
public class MultipleDeviceNotSupported extends Dialog {

	private MultipleDeviceInterface multipleDeviceInterface;

	/**
	 * The Interface MultipleDeviceInterface.
	 */
	public interface MultipleDeviceInterface {

		/**
		 * Passes the call to Activity to which this dialog is associated.
		 */
		public void doOnOkClick();

		/**
		 * Passes the call to activity when the link is clicked
		 */
		public void doOnLinkClick();

	}

	/**
	 * Instantiates a MultipleDeviceNotSupportedDialog
	 * 
	 * @param loginSyncroteamActivity
	 *            the login syncroteam activity
	 * @param multipleDeviceInterface
	 *            the multiple device interface
	 */
	public MultipleDeviceNotSupported(Activity activity,
			MultipleDeviceInterface multipleDeviceInterface) {
		super(activity, android.R.style.Theme_Translucent_NoTitleBar);
		setCancelable(false);
		setContentView(R.layout.layout_multiple_device_dialog);
		this.getWindow().setGravity(Gravity.CENTER);

		findViewById(R.id.okBtn).setOnClickListener(onClickListener);
		findViewById(R.id.txtMoreInfo).setOnClickListener(onClickListener);
		this.multipleDeviceInterface = multipleDeviceInterface;

	}

	/** The on click listener. */
	android.view.View.OnClickListener onClickListener = new android.view.View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			switch (id) {
			case R.id.okBtn:
				multipleDeviceInterface.doOnOkClick();
				dismiss();
				break;
			case R.id.txtMoreInfo:
				multipleDeviceInterface.doOnLinkClick();
				break;
			}

		}
	};
}
