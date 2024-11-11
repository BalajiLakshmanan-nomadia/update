package com.synchroteam.dialogs;


import android.app.Dialog;
import android.view.Gravity;
import android.view.View;

import com.synchroteam.synchroteam.Login_Syncroteam;
import com.synchroteam.synchroteam3.R;

// TODO: Auto-generated Javadoc
/**
 * The Class DomianInformationDialog shows domain information dialog.
 * 
 * @author ${Ideavate Solution}
 */
public class WipeAllDetailDialog extends Dialog {

	/**
	 * ** Creates an instance of WipeAllDetailDialog and perform necessary
	 * initialization.
	 * 
	 */

	private WipeAllDetailInterface wipeAllDetailInterface;

	/**
	 * The Interface WipeAllDetailInterface.
	 */
	public interface WipeAllDetailInterface {

		/**
		 * * initialise the resetting of application when user call this function
		 */
		public void doOnYesClick();

		/***
		 * Dis miss the dialog when no button is clicked.
		 */
		public void doOnNoClick();
	}

	/**
	 * Instantiates a new wipe all detail dialog.
	 * 
	 * @param loginSyncroteamActivity
	 *            the login syncroteam activity
	 * @param wipeAllDetailInterface
	 *            the wipe all detail interface
	 */
	public WipeAllDetailDialog(Login_Syncroteam loginSyncroteamActivity,
			WipeAllDetailInterface wipeAllDetailInterface) {
		super(loginSyncroteamActivity,
				android.R.style.Theme_Translucent_NoTitleBar);
		setCancelable(false);
		setContentView(R.layout.layout_wipealldetail_dialog);
		this.getWindow().setGravity(Gravity.CENTER);
		findViewById(R.id.yesBtn).setOnClickListener(onClickListener);
		findViewById(R.id.noBtn).setOnClickListener(onClickListener);
		this.wipeAllDetailInterface = wipeAllDetailInterface;

	}

	/** The on click listener. */
	android.view.View.OnClickListener onClickListener = new android.view.View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			switch (id) {
			case R.id.yesBtn:
				dismiss();
				wipeAllDetailInterface.doOnYesClick();
				break;

			case R.id.noBtn:
				dismiss();
				wipeAllDetailInterface.doOnNoClick();
				break;
			}

		}
	};
}
