package com.synchroteam.dialogs;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.synchroteam3.R;


public class SelectValueFromListDialog extends Dialog {

	/**
	 * The Interface WipeAllDetailInterface.
	 */
	public interface OnItemSelectInterface {

		/**
		 * * initialise the resetting of application when user call this
		 * function
		 */
		public void doOnItemSelect(String itemSelected);

	}

	
	private OnItemSelectInterface itemSelectInterface;
	
	public SelectValueFromListDialog(Activity activity, ArrayList<String> listValues,final OnItemSelectInterface itemSelectInterface) {
		super(activity, android.R.style.Theme_Translucent_NoTitleBar);
		setCancelable(false);
		setContentView(R.layout.layout_list_of_values_list);
		final ListView listView = (ListView) findViewById(R.id.listOfValuesList);

		
		
		CustomListOfValuesAdapter customListOfValuesAdapter = new CustomListOfValuesAdapter(
				activity, listValues);

		listView.setAdapter(customListOfValuesAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
                   
				
		             itemSelectInterface.doOnItemSelect(((TextView)arg1).getText().toString());		
				
				
				dismiss();
			}
		});

		
	}

	/**
	 * 
	 * Custom Adapter use by spinner to show the text view to show
	 * 
	 * @author Ideavate.solution
	 * 
	 */
	private class CustomListOfValuesAdapter extends ArrayAdapter<String> {
		LayoutInflater inflater;

		public CustomListOfValuesAdapter(Context context,
				ArrayList<String> objects) {
			super(context, R.layout.layout_list_of_values_spinner_list_item,
					objects);
			// TODO Auto-generated constructor stub
			inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView view = (TextView) inflater.inflate(
					R.layout.layout_list_of_values_spinner_list_item, parent,
					false);

			view.setText(getItem(position));
			return view;
		}

	}

}