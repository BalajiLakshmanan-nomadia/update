package com.synchroteam.dialogs;


import android.app.Dialog;
import android.view.Gravity;
import android.view.View;

import com.synchroteam.synchroteam.Login_Syncroteam;
import com.synchroteam.synchroteam3.R;

// TODO: Auto-generated Javadoc
/**
 * The Class DomianInformationDialog shows domain information dialog.
 * @author ${Ideavate Solution}
 */
public class DomianInformationDialog  extends Dialog{
	
	/**
	 * **
	 * Creates an instance of DomianInformationDialog and perform necessary initialization.
	 *
	 * @param loginSyncroteamActivity the login syncroteam activity
	 */
		public DomianInformationDialog(Login_Syncroteam loginSyncroteamActivity) {
			super(loginSyncroteamActivity, android.R.style.Theme_Translucent_NoTitleBar);
			setCancelable(false);
			setContentView(R.layout.layout_login_domain_dialog);
			this.getWindow().setGravity(Gravity.CENTER);
			findViewById(R.id.okButton).setOnClickListener(onClickListener);
			
		}
		
		/** The on click listener. */
		android.view.View.OnClickListener onClickListener=new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
				
			}
		};
}
