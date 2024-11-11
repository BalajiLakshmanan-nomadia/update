package com.synchroteam.dialogs;


import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.View;

import com.synchroteam.synchroteam3.R;

// TODO: Auto-generated Javadoc
/**
 * The Class DeletePartsDialog shows the confirmation dialog before deleting a part from PARTS LIST View.
 * 
 * @author ${Ideavate Solution}
 */
public class DeletePartsDialog extends Dialog {

	/**
	 * ** Creates an instance of DeletePartsInterface and perform necessary
	 * initialization.
	 * 
	 */

	private DeletePartsInterface deletePartsInterface;

	/**
	 * The Interface WipeAllDetailInterface.
	 */
	public interface DeletePartsInterface {

		/**
		 * * deletes the Parts form PartList database and refresh the view.
		 */
		public void doOnYesClick();

		/***
		 * Dismiss the dialog and peroform no change.
		 */
		public void doOnNoClick();
	}

	/**
	 * Instantiates a DeletePartsInterface dialog.
	 *
	 * @param activity the activity
	 * @param deletePartsInterface the delete parts interface
	 */
	public DeletePartsDialog(Activity activity,
			DeletePartsInterface deletePartsInterface) {
		super(activity,
				android.R.style.Theme_Translucent_NoTitleBar);
		setCancelable(false);
		setContentView(R.layout.layout_deleteparts_dialog);
		this.getWindow().setGravity(Gravity.CENTER);
		findViewById(R.id.yesBtn).setOnClickListener(onClickListener);
		findViewById(R.id.noBtn).setOnClickListener(onClickListener);
		this.deletePartsInterface = deletePartsInterface;

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
				deletePartsInterface.doOnYesClick();
				break;

			case R.id.noBtn:
				dismiss();
				deletePartsInterface.doOnNoClick();
				break;
			}

		}
	};
}
