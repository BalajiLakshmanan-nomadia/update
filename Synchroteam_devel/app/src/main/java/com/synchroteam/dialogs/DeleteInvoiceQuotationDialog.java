package com.synchroteam.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.synchroteam3.R;

public class DeleteInvoiceQuotationDialog extends Dialog {

	/**
	 * ** Creates an instance of DeleteInvoiceQuotationInterface and perform
	 * necessary initialization.
	 *
	 */

	private DeleteInvoiceQuotationInterface deleteIQInterface;

	/**
	 * The Interface DeleteInvoiceQuotationInterface.
	 */
	public interface DeleteInvoiceQuotationInterface {

		/**
		 * * deletes the Invoice/Quotation form database and refresh the view.
		 */
		public void doOnYesClick();

		/***
		 * Dismiss the dialog and perform no change.
		 */
		public void doOnNoClick();
	}

	/**
	 * Instantiates a DeleteInvoiceQuotationInterface dialog.
	 *
	 * @param deleteIQInterface
	 *            the delete Invoice/Quotation interface
	 */
	public DeleteInvoiceQuotationDialog(Context context,boolean isInvoice,
										DeleteInvoiceQuotationInterface deleteIQInterface) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar);
		setCancelable(false);
		setContentView(R.layout.layout_delete_invoice_quotation_dialog);
		this.getWindow().setGravity(Gravity.CENTER);
		findViewById(R.id.yesBtn).setOnClickListener(onClickListener);
		findViewById(R.id.noBtn).setOnClickListener(onClickListener);
		TextView txtMsg = (TextView) findViewById(R.id.txt_msg_dialog);
			txtMsg.setText(context.getString(R.string.txt_confirm));
		this.deleteIQInterface = deleteIQInterface;

	}

	/** The on click listener. */
	View.OnClickListener onClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int id = v.getId();
			switch (id) {
			case R.id.yesBtn:
				dismiss();
				deleteIQInterface.doOnYesClick();
				break;

			case R.id.noBtn:
				dismiss();
				deleteIQInterface.doOnNoClick();
				break;
			}

		}
	};
}
