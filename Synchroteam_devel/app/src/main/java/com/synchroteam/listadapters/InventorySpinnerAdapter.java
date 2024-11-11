package com.synchroteam.listadapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.synchroteam3.R;

/**
 * @author Trident
 *         <p>
 *         Adapter for customizing the spinner used in Inventory details screen.
 *         </p>
 */
public class InventorySpinnerAdapter extends ArrayAdapter<String> {

	private Context context;
	private String[] spinnerValues;

	public InventorySpinnerAdapter(Context context, int resource,
			String[] objects) {
		super(context, resource, objects);
		this.context = context;
		spinnerValues = objects;
	}
	
	@Override
	public int getCount() {
		return super.getCount()-1;
	}

	/*
	 * In order to set a hint for spinner, a string for first position is passed
	 * as hint and the visibility of the first position is set as GONE to hide
	 * it from drop down menu.
	 */
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(
					R.layout.layout_custom_spinner_inventory, parent, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.txtItem.setText(spinnerValues[position]);
		if (spinnerValues[position].equalsIgnoreCase("From")
				|| spinnerValues[position].equalsIgnoreCase("To")
				|| spinnerValues[position].equalsIgnoreCase("Action")
				|| spinnerValues[position].equalsIgnoreCase("Serial Numbers")) {

			convertView.getLayoutParams().height = 0;
			convertView.getLayoutParams().width = 0;
			convertView.invalidate();
			convertView.setVisibility(View.GONE);
		}
		return convertView;
	}

	/*
	 * view for single row.
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = mInflater.inflate(
					R.layout.layout_custom_spinner_inventory, parent, false);
			holder = new ViewHolder(convertView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.txtItem.setText(spinnerValues[position]);

		return convertView;
	}

	/**
	 * Holder class for the spinner
	 */
	class ViewHolder {
		TextView txtItem;
		android.widget.TextView txtSpinnerIcon;
		Typeface typeFace;
		RelativeLayout relContainer;

		public ViewHolder(View view) {
			relContainer = (RelativeLayout) view.findViewById(R.id.relDropDown);
			txtItem = (TextView) view.findViewById(R.id.txtSpinnerItem);
			txtSpinnerIcon = (android.widget.TextView) view
					.findViewById(R.id.txtSpinnerIcon);

			typeFace = Typeface.createFromAsset(context.getAssets(), context
					.getResources().getString(R.string.fontName_fontAwesome));

			txtSpinnerIcon.setTypeface(typeFace);
		}
	}

}
