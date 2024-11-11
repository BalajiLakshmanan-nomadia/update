package com.synchroteam.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
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
public class AppUpdateDialog extends Dialog {

	private Activity activity;

	/**
	 * Instantiates app update dialog
	 *
	 * @param activity : context
     */
	public AppUpdateDialog(Activity activity) {
		super(activity, android.R.style.Theme_Translucent_NoTitleBar);
		this.activity = activity;
		setCancelable(false);
		setContentView(R.layout.layout_update_dialog);
		this.getWindow().setGravity(Gravity.CENTER);

		findViewById(R.id.okBtn).setOnClickListener(onClickListener);
	}

	/** The on click listener. */
	View.OnClickListener onClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			switch (id) {
			case R.id.okBtn:
				final String appPackageName = activity.getPackageName(); // getPackageName() from Context or Activity object
				try {
					activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
				} catch (android.content.ActivityNotFoundException anfe) {
					activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
				}
				dismiss();
				break;
			}

		}
	};
}
