package com.synchroteam.listadapters;

import java.util.ArrayList;

import com.synchroteam.synchroteam3.R;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

/**
 * @author Trident
 *         <p>
 *         Custom adapter for spinner.
 *         </p>
 */
public class SpinnerAdapter extends ArrayAdapter<String> {
	Context context;
	ArrayList<String> values;
	Typeface font;

	public SpinnerAdapter(Context context, int resource, ArrayList<String> objects) {
		super(context, resource, objects);
		this.context = context;
		values = objects;
		font = Typeface.createFromAsset(context.getAssets(),
				context.getString(R.string.fontName_avenir));
	}

	@NotNull
	@Override
	public View getView(int position, View convertView, @NotNull ViewGroup parent) {
		android.widget.TextView view = (android.widget.TextView) super.getView(
				position, convertView, parent);
		view.setTypeface(font);
		if (position == 0) {
			view.setTextColor(context.getResources().getColor(
					R.color.invoice_hint_color));
		}
		return view;
	}

	@Override
	public View getDropDownView(int position, View convertView, @NotNull ViewGroup parent) {
		View v = null;
		// If this is the initial dummy entry, make it hidden
		if (position == 0) {
			android.widget.TextView tv = new android.widget.TextView(
					getContext());
			tv.setHeight(0);
			tv.setVisibility(View.GONE);
			v = tv;
		} else {
			// Pass convertView as null to prevent reuse of special case
			// views
			v = super.getDropDownView(position, null, parent);
			((android.widget.TextView) v).setTypeface(font);
		}

		// Hide scroll bar because it appears sometimes unnecessarily,
		// this does not prevent scrolling
		parent.setVerticalScrollBarEnabled(false);
		return v;
	}

}
