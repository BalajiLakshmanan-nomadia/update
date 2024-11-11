package com.synchroteam.fragment;

import androidx.fragment.app.Fragment;

import com.synchroteam.baseactivity.SyncroTeamBaseActivity;
// TODO: Auto-generated Javadoc

/**
 * *
 * Base Activity of all the Fragments used in Entire Application.
 *
 * @author Ambesh.Kukreja
 */
public abstract class BaseFragment extends Fragment implements FragmentInterface {

	/**
	 * Gets the Instance syncro team base activity.
	 *
	 * @return the syncro team base activity
	 */
	SyncroTeamBaseActivity getSyncroTeamBaseActivity(){
		return (SyncroTeamBaseActivity) getActivity();
	}
	
	
	/**
	 * The Interface ListUpdateListener .
	 */
	public interface ListUpdateListener {
		
		/**
		 * Method to be called when list is updated.
		 */
		public void  onListUpdate();
		
	}

	
	
	
	
}
