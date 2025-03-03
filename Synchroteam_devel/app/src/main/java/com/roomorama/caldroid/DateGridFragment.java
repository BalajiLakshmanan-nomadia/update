package com.roomorama.caldroid;



import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;

import com.synchroteam.synchroteam3.R;

// TODO: Auto-generated Javadoc
/**
 * DateGridFragment contains only 1 gridview with 7 columns to display all the
 * dates within a month.
 * 
 * Client must supply gridAdapter and onItemClickListener before the fragment is
 * attached to avoid complex crash due to fragment life cycles.
 * 
 * @author thomasdao
 * 
 */
public class DateGridFragment extends Fragment {

	/** The grid view. */
	private GridView gridView;

	/** The grid adapter. */
	private CaldroidGridAdapter gridAdapter;

	/** The on item click listener. */
	private OnItemClickListener onItemClickListener;

	/** The on item long click listener. */
	private OnItemLongClickListener onItemLongClickListener;

	/**
	 * Gets the on item click listener.
	 *
	 * @return the on item click listener
	 */
	public OnItemClickListener getOnItemClickListener() {
		return onItemClickListener;
	}

	/**
	 * Sets the on item click listener.
	 *
	 * @param onItemClickListener the new on item click listener
	 */
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	/**
	 * Gets the on item long click listener.
	 *
	 * @return the on item long click listener
	 */
	public OnItemLongClickListener getOnItemLongClickListener() {
		return onItemLongClickListener;
	}

	/**
	 * Sets the on item long click listener.
	 *
	 * @param onItemLongClickListener the new on item long click listener
	 */
	public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
		this.onItemLongClickListener = onItemLongClickListener;
	}

	/**
	 * Gets the grid adapter.
	 *
	 * @return the grid adapter
	 */
	public CaldroidGridAdapter getGridAdapter() {
		return gridAdapter;
	}

	/**
	 * Sets the grid adapter.
	 *
	 * @param gridAdapter the new grid adapter
	 */
	public void setGridAdapter(CaldroidGridAdapter gridAdapter) {
		this.gridAdapter = gridAdapter;
	}

	/**
	 * Gets the grid view.
	 *
	 * @return the grid view
	 */
	public GridView getGridView() {
		return gridView;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		gridView = (GridView) inflater.inflate(R.layout.date_grid_fragment,
				container, false);
		// Client normally needs to provide the adapter and onItemClickListener
		// before the fragment is attached to avoid complex crash due to
		// fragment life cycles
		if (gridAdapter != null) {
			gridView.setAdapter(gridAdapter);
		}

		if (onItemClickListener != null) {
			gridView.setOnItemClickListener(onItemClickListener);
		}
		if(onItemLongClickListener != null) {
			gridView.setOnItemLongClickListener(onItemLongClickListener);
		}
		return gridView;
	}

}
