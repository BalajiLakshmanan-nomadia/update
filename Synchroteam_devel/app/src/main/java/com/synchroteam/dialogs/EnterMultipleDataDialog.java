package com.synchroteam.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.synchroteam.TypefaceLibrary.EditText;
import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.Item;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.ScannerBar;

// TODO: Auto-generated Javadoc
/**
 * The Class EnterMultipleDataDialog.
 */
public class EnterMultipleDataDialog extends Dialog {

	/**
	 * The Interface EnterDialogInterface.
	 */
	public interface EnterDialogInterface {

		/**
		 * Do on modify click.
		 * 
		 * @param data
		 *            the data
		 */
		public void doOnModifyClick(String data);

		/**
		 * Do on cancel click.
		 */
		public void doOnCancelClick();

	}

	/** The enter dialog interface. */
	private EnterDialogInterface enterDialogInterface;

	/** The activity. */
	private Activity activity;

	private LinearLayout containerView;
	private String[] itemName;
	private Item item;
	private int disabledColorBtn, enabledColorbtn;

	private TextView cancelTv, modifytv;
	private Typeface tfFontAwesome;
	private boolean isButtonDisabled = true;

	/**
	 * Instantiates a new enter data dialog to enter multiple data.
	 * 
	 * @param activity
	 *            the activity
	 * @param enterDialogInterface
	 *            the enter dialog interface
	 */
	public EnterMultipleDataDialog(Activity activity,
			EnterDialogInterface enterDialogInterface, Item item,
			String categoryName) {
		super(activity, android.R.style.Theme_Translucent_NoTitleBar);
		setCancelable(false);
		setContentView(R.layout.layout_multiple_data_dialog);
		TextView dialogTitleTv = (TextView) findViewById(R.id.categoryNameTv);
		dialogTitleTv.setText(categoryName);
		this.activity = activity;
		cancelTv = (TextView) findViewById(R.id.cancelTv);
		modifytv = (TextView) findViewById(R.id.modifytv);
		cancelTv.setOnClickListener(onClickListener);
		modifytv.setOnClickListener(onClickListener);
		this.item = item;
		disabledColorBtn = activity.getResources().getColor(
				R.color.textCancelOkTvDataEnterDialog);
		enabledColorbtn = activity.getResources().getColor(R.color.black);

		containerView = (LinearLayout) findViewById(R.id.containerViews);

		tfFontAwesome = Typeface.createFromAsset(activity.getAssets(), activity.getString(R.string.fontName_fontAwesome));

		inflateViews(item);

		this.enterDialogInterface = enterDialogInterface;

	}

	/**
	 * Adds the views at run time based on the number of table items.
	 * 
	 * @param item
	 */
	private void inflateViews(Item item) {
		// TODO Auto-generated method stub
		itemName = item.getNomItem().split("\\|");

		InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(512);

		String[] values = null;
		if (!TextUtils.isEmpty(item.getValeurNet())) {
			values = item.getValeurNet().split("\\|");

		}

		for (int i = 0; i < itemName.length; i++) {

			TextView textView = new TextView(activity);
			textView.setTextAppearance(activity,
					R.style.styleMultipleDataDialogLable);
			textView.setText(itemName[i]);
			containerView.addView(textView);

			//adding horizontal linear layout
//			LinearLayout linearLayout = new LinearLayout(activity);
//			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

//			linearLayout.setLayoutParams(lp);

			//adding edittext to the linear layou
			EditText editText = new EditText(activity);
			// editText.setBackgroundResource(R.color.white);
			editText.setId(item.getIdItem() + i);
			editText.setTextAppearance(activity,
					R.style.styleMultipleDataDialogData);
			editText.setBackgroundResource(R.drawable.background_edittext_multiple_list_item);
			editText.setMaxLines(2);
			editText.addTextChangedListener(textWatcher);
			editText.setMinLines(2);
			editText.setFilters(FilterArray);

			if (values != null) {
				if (i <= (values.length - 1)) {
					if (!TextUtils.isEmpty(values[i])) {
						editText.setText(values[i]);

					}
				}

			}
//			linearLayout.addView(editText);

			//adding barcode icon to the linear layout
//			android.widget.TextView txtBarcode = new TextView(activity);
//			txtBarcode.setTypeface(tfFontAwesome);
//			txtBarcode.setId(item.getIdItem() + i);
//
//			//setting click listener for barcode icon
//			txtBarcode.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View view) {
//					Intent intentScanner = new Intent(activity, ScannerBar.class);
//					activity.startActivity(intentScanner);
//				}
//			});
//			linearLayout.addView(txtBarcode);
			containerView.addView(editText);

		}

	}

	/** The on click listener. */
	android.view.View.OnClickListener onClickListener = new android.view.View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();

			switch (id) {
			case R.id.modifytv:
				StringBuilder values = new StringBuilder();
				for (int i = 0; i < itemName.length; i++) {

					EditText et1 = (EditText) findViewById(item.getIdItem() + i);
					if ((i + 1) == itemName.length)
						values.append(et1.getText().toString());
					else
						values.append(et1.getText().toString() + "|");

				}

				StringBuilder emptyString = new StringBuilder();
				for (int i = 0; i < itemName.length; i++) {
					if ((i + 1) == itemName.length)
						emptyString.append("");
					else
						emptyString.append("|");

				}

				if (values.toString().equals(emptyString.toString())) {
					enterDialogInterface.doOnModifyClick("");
				} else {
					enterDialogInterface.doOnModifyClick(values.toString());
				}

				EnterMultipleDataDialog.this.dismiss();
				break;

			case R.id.cancelTv:

				enterDialogInterface.doOnCancelClick();
				EnterMultipleDataDialog.this.dismiss();
				break;
			}

		}
	};

	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
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
			if (!TextUtils.isEmpty(s.toString())) {
				if (isButtonDisabled) {
					cancelTv.setTextColor(enabledColorbtn);
					modifytv.setTextColor(enabledColorbtn);
					isButtonDisabled = false;
				}

			} else {
				if (checkIfAllTextFieldsAreEmpty()) {
					cancelTv.setTextColor(disabledColorBtn);
					modifytv.setTextColor(disabledColorBtn);
					isButtonDisabled = true;
				}

			}

		}
	};

	private boolean checkIfAllTextFieldsAreEmpty() {

		StringBuilder values = new StringBuilder();
		for (int i = 0; i < itemName.length; i++) {

			EditText et1 = (EditText) findViewById(item.getIdItem() + i);
			values.append(et1.getText().toString());

		}

		if (TextUtils.isEmpty(values.toString())) {
			return true;
		} else {
			return false;
		}

	}

}
