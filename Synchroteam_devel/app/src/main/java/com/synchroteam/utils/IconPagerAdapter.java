package com.synchroteam.utils;

// TODO: Auto-generated Javadoc
/**
 * The Interface IconPagerAdapter.
 */
public interface IconPagerAdapter {
    
    /**
     * Get icon representing the page at {@code index} in the adapter.
     *
     * @param index the index
     * @return the icon res id
     */
    int getIconResId(int index);

    // From PagerAdapter
    /**
     * Gets the count.
     *
     * @return the count
     */
    int getCount();
}
