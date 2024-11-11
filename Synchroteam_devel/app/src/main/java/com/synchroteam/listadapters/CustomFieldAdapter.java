package com.synchroteam.listadapters;

import java.util.ArrayList;

import android.app.Activity;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.CustomFieldBean;
import com.synchroteam.beans.CustomFieldsByVal;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.technicalsupport.CustomField;

// TODO: Auto-generated Javadoc
/**
 * Adapter Class to for Category List. created for future purpose
 * 
 * @author Ideavate.solution
 */
public class CustomFieldAdapter extends BaseExpandableListAdapter {

	/** The current jobs beans. */
	private ArrayList<CustomFieldBean> customFieldBeans;

	/** The layout inflater. */
	private LayoutInflater layoutInflater;

	/**
	 * Instantiates a new current jobs list adapter.
	 *
	 * @param activity
	 *            the activity
	 * @param customFieldBeans
	 *            the custom field beans
	 */
	public CustomFieldAdapter(Activity activity,
			ArrayList<CustomFieldBean> customFieldBeans) {
		// TODO Auto-generated constructor stub

		this.customFieldBeans = customFieldBeans;
		this.layoutInflater = activity.getLayoutInflater();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChild(int, int)
	 */
	@Override
	public Object getChild(int arg0, int arg1) {
		// TODO Auto-generated method stub

		return customFieldBeans.get(arg0).getCustomFieldBeans().get(arg1);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChildId(int, int)
	 */
	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChildView(int, int, boolean,
	 * android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		CustomFieldsByVal customFieldsByVal = (CustomFieldsByVal) getChild(
				groupPosition, childPosition);

		View view = layoutInflater.inflate(R.layout.list_customfield_val, parent,false);

		TextView nomcustom = (TextView) view.findViewById(R.id.nomcustom);
		nomcustom.setText(customFieldsByVal.getNomCustomsFields());
		TextView valcustom = (TextView) view.findViewById(R.id.valcustom);
		valcustom.setText(customFieldsByVal.getValCustomField());

		Linkify.addLinks(valcustom,Linkify.ALL);

		View dividerView = (View) view.findViewById(R.id.customDetailsDivider);
		LinearLayout linChildContainer = (LinearLayout) view
				.findViewById(R.id.linCustomFieldChildContainer);
		if (childPosition == 0 && !isLastChild) {
			linChildContainer
					.setBackgroundResource(R.drawable.boxframe_first_child_layout);
		} else if (childPosition == 0 && isLastChild) {
			dividerView.setVisibility(View.VISIBLE);
			linChildContainer
					.setBackgroundResource(R.drawable.boxframe_textview_layout);
		} else if (isLastChild && childPosition != 0) {
			dividerView.setVisibility(View.VISIBLE);
			linChildContainer
					.setBackgroundResource(R.drawable.boxframe_last_child_layout);
		} else {
			linChildContainer
					.setBackgroundResource(R.drawable.boxframe_middle_child_layout);
		}
		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getChildrenCount(int)
	 */
	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return customFieldBeans.get(groupPosition).getCustomFieldBeans().size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroup(int)
	 */
	@Override
	public Object getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return customFieldBeans.get(groupPosition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroupCount()
	 */
	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return customFieldBeans.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroupId(int)
	 */
	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean,
	 * android.view.View, android.view.ViewGroup)
	 */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		CustomFieldBean customFieldBean = (CustomFieldBean) getGroup(groupPosition);

		View view = layoutInflater.inflate(R.layout.layout_customfieldcategory,
				parent,false);
		TextView nomTable = (TextView) view.findViewById(R.id.nomtable);
		nomTable.setText(customFieldBean.getCustomFIeldCategory());

		ImageView expandCollapseIcon = (ImageView) view
				.findViewById(R.id.expandCollapseIcon);

		if (isExpanded) {
			expandCollapseIcon.setImageResource(R.drawable.arrow_up);
		} else {
			expandCollapseIcon.setImageResource(R.drawable.arrow_down);
		}

		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#hasStableIds()
	 */
	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.ExpandableListAdapter#isChildSelectable(int, int)
	 */
	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return false;
	}

}
