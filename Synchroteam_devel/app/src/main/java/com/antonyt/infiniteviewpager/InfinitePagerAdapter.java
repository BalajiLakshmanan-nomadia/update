package com.antonyt.infiniteviewpager;

import android.os.Parcelable;
import androidx.viewpager.widget.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

// TODO: Auto-generated Javadoc
/**
 * A PagerAdapter that wraps around another PagerAdapter to handle paging
 * wrap-around.
 * 
 */
public class InfinitePagerAdapter extends PagerAdapter {
	
	/** The adapter. */
	private PagerAdapter adapter;

	/**
	 * Instantiates a new infinite pager adapter.
	 *
	 * @param adapter the adapter
	 */
	public InfinitePagerAdapter(PagerAdapter adapter) {
		this.adapter = adapter;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#getCount()
	 */
	@Override
	public int getCount() {
		// warning: scrolling to very high values (1,000,000+) results in
		// strange drawing behaviour
		return Integer.MAX_VALUE;
	}

	/**
	 * Gets the real count.
	 *
	 * @return the {@link #getCount()} result of the wrapped adapter
	 */
	public int getRealCount() {
		return adapter.getCount();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#instantiateItem(android.view.ViewGroup, int)
	 */
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		int virtualPosition = position % getRealCount();
		// only expose virtual position to the inner adapter
		return adapter.instantiateItem(container, virtualPosition);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#destroyItem(android.view.ViewGroup, int, java.lang.Object)
	 */
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		int virtualPosition = (position) % getRealCount();
		// only expose virtual position to the inner adapter
		adapter.destroyItem(container, virtualPosition, object);
	}

	/*
	 * Delegate rest of methods directly to the inner adapter.
	 */

	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#finishUpdate(android.view.ViewGroup)
	 */
	@Override
	public void finishUpdate(ViewGroup container) {
		adapter.finishUpdate(container);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#isViewFromObject(android.view.View, java.lang.Object)
	 */
	@Override
	public boolean isViewFromObject(View view, Object object) {
		return adapter.isViewFromObject(view, object);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#restoreState(android.os.Parcelable, java.lang.ClassLoader)
	 */
	@Override
	public void restoreState(Parcelable bundle, ClassLoader classLoader) {
		adapter.restoreState(bundle, classLoader);
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#saveState()
	 */
	@Override
	public Parcelable saveState() {
		return adapter.saveState();
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#startUpdate(android.view.ViewGroup)
	 */
	@Override
	public void startUpdate(ViewGroup container) {
		adapter.startUpdate(container);
	}
}
