package com.synchroteam.fragment;





import com.caldroidx.CaldroidFragment;
import com.caldroidx.CaldroidGridAdapter;
import com.synchroteam.listadapters.CustomCalanderDateAdapter;


// TODO: Auto-generated Javadoc
/**
 * The Class CustomCalanderFragment.
 */
public class CustomCalanderFragment extends CaldroidFragment {



	/* (non-Javadoc)
	 * @see com.roomorama.caldroid.CaldroidFragment#getNewDatesGridAdapter(int, int)
	 */
	@Override
	public CaldroidGridAdapter getNewDatesGridAdapter(int month, int year) {
		// TODO Auto-generated method stub

		return new CustomCalanderDateAdapter(getActivity(), month, year,
				getCaldroidData(), extraData);


	}




}
