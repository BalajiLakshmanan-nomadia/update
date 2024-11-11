package com.synchroteam.dialogs;


import android.app.Activity;
import android.app.Dialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.CommonUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class EnterDataDialog.
 */
public class EnterDataDialog extends Dialog {

	/**
	 * The Interface EnterDialogInterface.
	 */
	public interface EnterDialogInterface {

		/**
		 * Do on modify click.
		 *
		 * @param data the data
		 */
		public void doOnModifyClick(String data);

		/**
		 * Do on cancel click.
		 */
		public void doOnCancelClick();

	}

	/** The enter dialog interface. */
	private EnterDialogInterface enterDialogInterface;

	/** The data et. */
	private EditText dataEt;

/** The activity. */
private Activity activity;
	

private int disabledColorBtn,enabledColorbtn;

private TextView cancelTv,modifytv;
private boolean isButtonDisabled=true;

	/**
	 * Instantiates a new enter data dialog.
	 *
	 * @param activity the activity
	 * @param enterDialogInterface the enter dialog interface
	 * @param titleDialog the title dialog
	 * @param data the data
	 */
	public EnterDataDialog(Activity activity,
			EnterDialogInterface enterDialogInterface, String titleDialog,String data) {
		super(activity, android.R.style.Theme_Translucent_NoTitleBar);
		setCancelable(false);
		setContentView(R.layout.layout_enter_data_reports_jobdetail);
		TextView dialogTitleTv = (TextView) findViewById(R.id.info);
		dialogTitleTv.setText(titleDialog);
		this.activity=activity;
		dataEt = (EditText) findViewById(R.id.commentaire);
		dataEt.setText(data);
		disabledColorBtn=activity.getResources().getColor(R.color.textCancelOkTvDataEnterDialog);
		enabledColorbtn=activity.getResources().getColor(R.color.black);
		cancelTv=(TextView) findViewById(R.id.cancelTv);
	  	modifytv=(TextView) findViewById(R.id.modifytv);
	  	cancelTv.setOnClickListener(onClickListener);
	  	modifytv.setOnClickListener(onClickListener);
	  	
	  	InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(512);
		
		dataEt.setFilters(FilterArray);
		
dataEt.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
                   				   
					if(!TextUtils.isEmpty(s.toString())){
						if(isButtonDisabled){
						cancelTv.setTextColor(enabledColorbtn);
						modifytv.setTextColor(enabledColorbtn);
						isButtonDisabled=false;
						}
						
					}
					else{
						cancelTv.setTextColor(disabledColorBtn);
						modifytv.setTextColor(disabledColorBtn);
						isButtonDisabled=true;
					}
					
				
				
				
				
			}
		});


		this.enterDialogInterface = enterDialogInterface;

	}

	/** The on click listener. */
	android.view.View.OnClickListener onClickListener = new android.view.View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();

			switch (id) {
			case R.id.modifytv:
              CommonUtils.hideKeyboard(activity, dataEt);
				enterDialogInterface.doOnModifyClick(dataEt.getText()
						.toString());
			
				
				
				EnterDataDialog.this.dismiss();
				break;

			case R.id.cancelTv:
				CommonUtils.hideKeyboard(activity, dataEt);
				enterDialogInterface.doOnCancelClick();
				EnterDataDialog.this.dismiss();
				break;
			}

		}
	};

}
