package com.synchroteam.fragmenthelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// TODO: Auto-generated Javadoc
/**
 * The Interface HelperInterface.
 */
public interface HelperInterface {
	
	/**
	 * Inflate layout.
	 *
	 * @param inflater the inflater
	 * @param container the container
	 * @return the view
	 */
	public View inflateLayout(LayoutInflater inflater,ViewGroup container);
	
	/**
	 * Instantiate view.
	 *
	 * @param v the v
	 */
	public void initiateView(View v);
	
	/**
	 * Called after syncronization to perform some action after syncronization is completed actual implementation of this varies from modules.
	 */
	public void doOnSyncronize();
	
	/**
	 * Called  form onActivityResult called to perform some action after returning to activity  of this varies from modules.
	 *
	 * @param requestCode the request code
	 */
	public void onReturnToActivity(int requestCode);
	
}
