package com.synchroteam.dialogs;


import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.synchroteam.synchroteam.Login_Syncroteam;
import com.synchroteam.synchroteam3.R;

// TODO: Auto-generated Javadoc
/**
 * The Class NearestClientNotAvailable is shown nearest client are not avaliable
 * 
 * @author ${Ideavate Solution}
 */
public class NearestClientNotAvailable extends Dialog {

	

	private NearestClientInterface nearestClientInterface;

	/**
	 * The Interface WipeAllDetailInterface.
	 */
	public interface NearestClientInterface {

		/**
		 * Passes the call to Activity to which this dailog is associated.
		 */
		public void doOnOkClick();

		
	}

	/**
	 * Instantiates a NearestCLientNotAvaliableDialog
	 * 
	 * @param loginSyncroteamActivity
	 *            the login syncroteam activity
	 * @param wipeAllDetailInterface
	 *            the wipe all detail interface
	 */
	public NearestClientNotAvailable(Activity activity,
			NearestClientInterface nearestClientInterface) {
		super(activity,
				android.R.style.Theme_Translucent_NoTitleBar);
		setCancelable(false);
		setContentView(R.layout.layout_nearest_client_not_avaliable);
		this.getWindow().setGravity(Gravity.CENTER);
		
		findViewById(R.id.okBtn).setOnClickListener(onClickListener);
		com.synchroteam.TypefaceLibrary.TextView txtDialogTitle = (com.synchroteam.TypefaceLibrary.TextView)findViewById(R.id.txt_dialog_title);
		txtDialogTitle.setText(activity.getString(R.string.sorryLable)+"!");
		this.nearestClientInterface = nearestClientInterface;

	}

	/** The on click listener. */
	android.view.View.OnClickListener onClickListener = new android.view.View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			switch (id) {
			case R.id.okBtn:
				nearestClientInterface.doOnOkClick();
				dismiss();
				
			break;	
				
			}

		}
	};
}
