package com.synchroteam.listadapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.synchroteam.TypefaceLibrary.TextView;
import com.synchroteam.beans.Message_oper;
import com.synchroteam.dao.Dao;
import com.synchroteam.fragment.BaseFragment;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.KEYS;

import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * Adapter Class to for Deadline Jobs List. created for future purpose
 * 
 * @author Ideavate.solution
 */
public class MessagesListAdapter extends BaseAdapter {

	/** The activity. */
	private Activity activity;

	/** The messages beans. */
	private List<Message_oper> messagesBeans;

	/** The layout inflater. */
	private LayoutInflater layoutInflater;
    
    /** The data access object. */
    private Dao dataAccessObject;

	/** The base fragment. */
	private BaseFragment baseFragment;
	
	/**
	 * Instantiates a new messages list adapter.
	 *
	 * @param activity            the activity
	 * @param messagesBeans            the messages beans
	 * @param dataAccessObject the data access object
	 */
	public MessagesListAdapter(Activity activity,
							   List<Message_oper> messagesBeans, Dao dataAccessObject, BaseFragment baseFragment) {
		// TODO Auto-generated constructor stub
		this.activity = activity;
		this.messagesBeans = messagesBeans;
		this.dataAccessObject=dataAccessObject;
		this.baseFragment = baseFragment;
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
		return messagesBeans.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return messagesBeans.get(position);
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

	/**
	 * The Class ViewHolder.
	 */
	private class ViewHolder {

		/** The client name tv. */
		TextView messageDetailTv;
		
		/** The image button. */
		ImageView imageButton;
		
		/** The message tv. */
		TextView messageTv;

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

		final ViewHolder viewHolder;
		if (convertView == null) {

			viewHolder = new ViewHolder();

			convertView = layoutInflater.inflate(
					R.layout.layout_messages_list_item, parent,false);
			viewHolder.messageDetailTv = (TextView) convertView
					.findViewById(R.id.messageDetailsTv);
			viewHolder.imageButton = (ImageView) convertView
					.findViewById(R.id.arrowUpDownTv);
			
			viewHolder.messageTv=(TextView) convertView.findViewById(R.id.messageTv);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();

		}

		final Message_oper message_oper = (Message_oper) getItem(position);

		viewHolder.imageButton.setTag(Boolean.valueOf(false));
		viewHolder.imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Boolean isArrowUp = (Boolean) v.getTag();
				boolean a = isArrowUp.booleanValue();
				if (a) {
					((ImageView) v).setImageResource(R.drawable.arrow_up);
					viewHolder.messageDetailTv.setMaxLines(1);
					v.setTag(Boolean.valueOf(false));
				} else {

					((ImageView) v).setImageResource(R.drawable.arrow_down);
					if(message_oper.getEtat()==0){
						dataAccessObject.majMsgLu(message_oper.getId());
						baseFragment.listUpdate();
					}
					viewHolder.messageDetailTv.setMaxLines(Integer.MAX_VALUE);
					v.setTag(Boolean.valueOf(true));

				}

			}
		});

		viewHolder.messageDetailTv.setText(message_oper.getCorp());
		
		viewHolder.messageTv.setText(message_oper.getTitre());
		if(message_oper.getPriorite()==KEYS.Messages.PRIORITY_HIGH){
			viewHolder.messageTv.setTextColor(activity.getResources().getColor(R.color.textMessageHIghPriority));
			
		}
		else if(message_oper.getPriorite()==KEYS.Messages.PRIORITY_LOW){
			viewHolder.messageTv.setTextColor(activity.getResources().getColor(R.color.textMessageLowPriority));
			
		}
		else{
			viewHolder.messageTv.setTextColor(activity.getResources().getColor(R.color.textMessageAveragePriority));

		}
		

		convertView.setLongClickable(true);

		return convertView;
	}

}
