package com.synchroteam.listadapters;

import java.text.Format;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.Item;
import com.synchroteam.beans.ReportsCategoryItem;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.Logger;
import com.synchroteam.utils.ReportsItems;

// TODO: Auto-generated Javadoc
/**
 * Adapter Class to for Current Job List.
 * created for future purpose
 * 
 * @author Ideavate.solution
 */
public class ReportsDetailListAdapter extends BaseAdapter {

	/** The activity. */
	private Activity activity;

	/** The current jobs beans. */
	private List<ReportsItems> reportsItems;

	/** The layout inflater. */
	private LayoutInflater layoutInflater;

	
	
	/** The image resources. */
	private int imageResources[] = { 
			   R.drawable.nostatus,
			   R.drawable.resolved,
			   
			   R.drawable.complaint_noright,
			   R.drawable.complaint};
	
	
	
	
	/**
	 * Instantiates a new current jobs list adapter.
	 *
	 * @param activity            the activity
	 * @param reportsItems the reports items
	 */
	public ReportsDetailListAdapter(Activity activity,
			List<ReportsItems> reportsItems) {
		// TODO Auto-generated constructor stub
		this.activity = activity;
		this.reportsItems = reportsItems;
		layoutInflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		
		 

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return reportsItems.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public ReportsItems getItem(int position) {
		// TODO Auto-generated method stub
		return reportsItems.get(position);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/* (non-Javadoc)
	 * @see android.widget.BaseAdapter#getViewTypeCount()
	 */
	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
  
		ReportsItems reportsItem=getItem(position);
		
		View view=null;
		
		if(reportsItem.isHeader()){
			
			view=layoutInflater.inflate(R.layout.layout_reports_category_list_item, parent,false);
			
			TextView categoryDataTv=(TextView) view.findViewById(R.id.categoryDataTv);
			
			categoryDataTv.setText(((ReportsCategoryItem)reportsItem).getCategroy());
			
			
		}
		else{
			view=layoutInflater.inflate(R.layout.layout_report_detail_list_item, parent,false);
			
			Item item=(Item) reportsItem;
			TextView fieldLable=(TextView) view.findViewById(R.id.reportsFieldLableTv);
			TextView dataLable=(TextView) view.findViewById(R.id.reportsFieldDataTv);
			
			
			final ImageView imgReserv = (ImageView)view.findViewById(R.id.resovedStatusIv);
			int flReserve=item.getFlReserve();
			if(flReserve == 0){
				imgReserv.setImageResource(imageResources[0]);
			
			}
			else
				if(flReserve == 1){
				imgReserv.setImageResource(imageResources[1]);
			
				}
				else
					if(flReserve == 2){
					imgReserv.setImageResource(imageResources[2]);
			
					}
				else{
					imgReserv.setImageResource(imageResources[3]);
			
				}
			
			fieldLable.setText(item.getNomItem());
			dataLable.setText(getValueFormat(item.getValeurNet(), item.getIdTypeItem()));
			
			
			
			
			
		}
		
		
		
		
      


		return view;
	}
	
	
	
	
	
	
	/**
	 * Gets the value format.
	 *
	 * @param value the value
	 * @param idTypeItem the id type item
	 * @return the value format
	 */
	@SuppressWarnings("deprecation")
	private String getValueFormat(String value, int idTypeItem) {

		String locale = activity.getResources().getConfiguration().locale
				.getDisplayName();
		String langue = locale.substring(0, locale.indexOf(" "));
		switch (idTypeItem) {
		case 0:
			if (!TextUtils.isEmpty(value) && value != null) {
				int val = Integer.parseInt(value);

				if (val == 1)
					value = activity.getString(R.string.textComplaintLable);
				else if (val == 2)
					value = activity.getString(R.string.textNotComplaintLable);

			} else
				value = activity.getString(R.string.non_renseigne);

			break;

		case 4:
			if(!TextUtils.isEmpty(value)){
				String[] tab=value.split(":");
				Date date=new Date();
				date.setHours(Integer.parseInt(tab[0]));
				date.setMinutes(Integer.parseInt(tab[1]));
				Format format=android.text.format.DateFormat.getTimeFormat(activity);
				value=format.format(date);
				
			}
			break;
		case 3:
			if (!TextUtils.isEmpty(value)) {
				String[] tab = value.split("/");
				
				Logger.log("Reports Detail ", value);
				Date date=new Date(Integer.parseInt(tab[2])-1900,Integer.parseInt(tab[1]),Integer.parseInt(tab[0]));
				Format format=android.text.format.DateFormat.getDateFormat(activity);
				value=format.format(date);
			}
			break;
		case 6:
			if (!TextUtils.isEmpty(value) && value != null
					&& langue.equalsIgnoreCase("english")) {

				String[] tab = value.split(" ");
				String[] tab2 = tab[0].split("/");
				String jj, mm;

				if (Integer.parseInt(tab2[0]) < 10 && tab2[0].length() == 1)
					jj = "0" + tab2[0];
				else
					jj = tab2[0];

				if (Integer.parseInt(tab2[1]) < 10 && tab2[1].length() == 1)
					mm = "0" + tab2[1];
				else
					mm = tab2[1];
				value = mm + "/" + jj + "/" + tab2[2] + " " + tab[1];

			}
			break;
		}
		return value;
	}

	
	
	
}
