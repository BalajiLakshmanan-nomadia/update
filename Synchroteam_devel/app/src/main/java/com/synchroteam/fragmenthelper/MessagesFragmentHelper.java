package com.synchroteam.fragmenthelper;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

import com.synchroteam.baseactivity.SyncroTeamBaseActivity;
import com.synchroteam.beans.EnableSynchronizationAddJobEvent;
import com.synchroteam.beans.Message_oper;
import com.synchroteam.dao.Dao;
import com.synchroteam.fragment.BaseFragment;
import com.synchroteam.listadapters.MessagesListAdapter;
import com.synchroteam.synchroteam3.R;
import com.synchroteam.utils.DaoManager;

import de.greenrobot.event.EventBus;
// TODO: Auto-generated Javadoc
/***
 * This class is responsible for functionality of Message Screen.
 * 
 * created for future purpose
 * 
 * @author Ideavate.solution
 *
 */
public class MessagesFragmentHelper implements HelperInterface {

	
/** The syncro team base activity. */
SyncroTeamBaseActivity syncroTeamBaseActivity;

/** The messages list view. */
ListView messagesListView;

/** The message_opers. */
private ArrayList<Message_oper> message_opers;

/** The messages list adapter. */
private MessagesListAdapter messagesListAdapter;

/** The data access object. */
private Dao dataAccessObject;

/** The builder. */
private AlertDialog.Builder builder;

/** The base fragment. */
private BaseFragment baseFragment;
	
	/**
	 * Instantiates a new messages fragment helper.
	 *
	 * @param syncroTeamBaseActivity the syncro team base activity
	 * @param baseFragment the base fragment
	 */
	public MessagesFragmentHelper(SyncroTeamBaseActivity syncroTeamBaseActivity,BaseFragment baseFragment) {
		// TODO Auto-generated constructor stub
	
	 this.syncroTeamBaseActivity=syncroTeamBaseActivity;
	 dataAccessObject=DaoManager.getInstance();
     builder = new AlertDialog.Builder(syncroTeamBaseActivity);
     this.baseFragment=baseFragment;
	}
	
	/* (non-Javadoc)
	 * @see com.synchroteam.fragmenthelper.HelperInterface#inflateLayout(android.view.LayoutInflater, android.view.ViewGroup)
	 */
	@Override
	public View inflateLayout(LayoutInflater inflater, ViewGroup container) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.layout_messages, container,
				false);
		initiateView(view);
		return view;
	}

	/* (non-Javadoc)
	 * @see com.synchroteam.fragmenthelper.HelperInterface#initiateView(android.view.View)
	 */
	@Override
	public void initiateView(View v) {
		// TODO Auto-generated method stub
		messagesListView=(ListView) v.findViewById(R.id.messageslv);
		messagesListView.setOnItemLongClickListener(itemLongClickListener);
		createAndInflateListView();
		
		
		
	}

	/**
	 * Creates the and inflate list view.
	 */
	private void createAndInflateListView() {
		// TODO Auto-generated method stub
		
		if(message_opers!=null){
			message_opers.clear();
		}
		else{
			message_opers=new ArrayList<Message_oper>();
		}
		
		
		
		message_opers.addAll(dataAccessObject.getAllMsg());
		
		
		
		
		
		
		
		
		if(messagesListAdapter==null){
			messagesListAdapter=new MessagesListAdapter(syncroTeamBaseActivity, message_opers,dataAccessObject,baseFragment);
			messagesListView.setAdapter(messagesListAdapter);

		}
		else{
			messagesListAdapter.notifyDataSetChanged();
		}
		

		EventBus.getDefault().post(new EnableSynchronizationAddJobEvent());
		
		
		
	}

	/* (non-Javadoc)
	 * @see com.synchroteam.fragmenthelper.HelperInterface#doOnSyncronize()
	 */
	@Override
	public void doOnSyncronize() {
		// TODO Auto-generated method stub
		createAndInflateListView();
		  
	}

	/* (non-Javadoc)
	 * @see com.synchroteam.fragmenthelper.HelperInterface#onReturnToActivity(int)
	 */
	@Override
	public void onReturnToActivity(int requestCode) {
		// TODO Auto-generated method stub
		
	}

/** The item long click listener. */
OnItemLongClickListener itemLongClickListener=new OnItemLongClickListener() {

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,  int arg2,
			long arg3) {
		final int position=arg2;
	        builder.setMessage(R.string.txt_confirm);
	        builder.setCancelable(false);       
	        builder.setPositiveButton(R.string.textYesLable, new DialogInterface.OnClickListener() {
	        	public void onClick(DialogInterface dialog, int idi) {  
	        		deleteMessage(message_opers.get(position).getId(),position);           
	        		}       
	        	});       
      	 builder.setNegativeButton(R.string.textNoLable, new DialogInterface.OnClickListener() {           
	        		public void onClick(DialogInterface dialog, int id) {                
	        			dialog.cancel();           
	        			}       
	        		});
	        AlertDialog alert = builder.create();
	        alert.show();
      		
		
		
		
		return false;
	}
	
};

///**
// * Supprimer msg by id.
// *
// * @param id the id
// * @param position the position
// */
//public void supprimerMsgById(int id,int position){
//    
//	dataAccessObject.deleteMsg(id);
//    message_opers.remove(position);
//    messagesListAdapter.notifyDataSetChanged();
//    
//    baseFragment.listUpdate();
//   
//}
/**
 * removes the message from particular position.
 *
 * @param id the id
 * @param position the position
 */
public void deleteMessage(int id,int position){
	 
	dataAccessObject.deleteMsg(id);
	 message_opers.remove(position);
   messagesListAdapter.notifyDataSetChanged();
   baseFragment.listUpdate();
	
}
}
