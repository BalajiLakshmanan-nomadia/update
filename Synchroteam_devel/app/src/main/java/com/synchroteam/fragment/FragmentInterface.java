package com.synchroteam.fragment;
// TODO: Auto-generated Javadoc
/***
 * This Interface contains methods which are to be implemented by all fragments.
 * @author ${Ideavate Solution}
 *
 */
public interface FragmentInterface {
 
 /**
  * Do on back pressed.
  *
  * @return true, if successful
  */
 public boolean doOnBackPressed();
 
 /**
  * return fragment tag.
  *
  * @return the fragment tag
  */
 public String getFragmentTag();
 
 
 
 /**
  *  Called when syncronise operations are performed.
  */
 public void doOnSync();
 
 /**
  * List update.
  */
 public void listUpdate();

 
	
}
