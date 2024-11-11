/*
 * Copyright (C) 2011 The Android Open Source Project
 * Copyright (C) 2011 Jake Wharton
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.synchroteam.utils;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.synchroteam.synchroteam3.R;

// TODO: Auto-generated Javadoc

/**
 * This widget implements the dynamic action bar tab behavior that can change
 * across different configurations or circumstances.
 */
public class TabPageIndicator extends HorizontalScrollView implements
        PageIndicator {
    /**
     * Title text used when no title is provided by the adapter.
     */
    private static final CharSequence EMPTY_TITLE = "";

    /**
     * Interface for a callback when the selected tab has been reselected.
     *
     * @see OnTabReselectedEvent
     */
    public interface OnTabReselectedListener {
        /**
         * Callback when the selected tab has been reselected.
         *
         * @param position Position of the current center item.
         */
        void onTabReselected(int position);
    }

    /**
     * Interface for a callback when the tab has been selected
     *
     * @see OnTabReselectedEvent
     */
    public interface OnTabClickedListener {

        /**
         * Callback when the stab has been selected.
         *
         * @param View View of the current tab.
         */

        void onTabClicked(View view, int position);

    }

    private OnTabClickedListener mOnTabClickedListener;
    /**
     * The m tab selector.
     */
    private Runnable mTabSelector;

    /**
     * The m tab click listener.
     */
    private final OnClickListener mTabClickListener = new OnClickListener() {
        public void onClick(View view) {
            TabView tabView = (TabView) view;
            final int oldSelected = mViewPager.getCurrentItem();
            final int newSelected = tabView.getIndex();
            if (oldSelected == newSelected && mTabReselectedListener != null) {
                mTabReselectedListener.onTabReselected(newSelected);
            }

            if (mOnTabClickedListener != null)

                mOnTabClickedListener.onTabClicked(view, newSelected);
            mViewPager.setCurrentItem(newSelected);

        }
    };

    /**
     * The m tab layout.
     */
    private final IcsLinearLayout mTabLayout;

    /**
     * The m view pager.
     */
    private ViewPager mViewPager;

    /**
     * The m listener.
     */
    private ViewPager.OnPageChangeListener mListener;

    /**
     * The m max tab width.
     */
    private int mMaxTabWidth;

    /**
     * The m selected tab index.
     */
    private int mSelectedTabIndex;

    /**
     * The m tab reselected listener.
     */
    private OnTabReselectedListener mTabReselectedListener;

    /**
     * Instantiates a new tab page indicator.
     *
     * @param context the context
     */
    public TabPageIndicator(Context context) {
        this(context, null);
    }

    /**
     * Instantiates a new tab page indicator.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public TabPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHorizontalScrollBarEnabled(false);

        mTabLayout = new IcsLinearLayout(context,
                R.attr.vpiTabPageIndicatorStyle);
        addView(mTabLayout, new ViewGroup.LayoutParams(WRAP_CONTENT,
                MATCH_PARENT));
    }

    /**
     * Sets the on tab reselected listener.
     *
     * @param listener the new on tab reselected listener
     */
    public void setOnTabReselectedListener(OnTabReselectedListener listener) {
        mTabReselectedListener = listener;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.widget.HorizontalScrollView#onMeasure(int, int)
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final boolean lockedExpanded = widthMode == MeasureSpec.EXACTLY;
        setFillViewport(lockedExpanded);

        final int childCount = mTabLayout.getChildCount();
        if (childCount > 1
                && (widthMode == MeasureSpec.EXACTLY || widthMode == MeasureSpec.AT_MOST)) {
            if (childCount > 2) {
                mMaxTabWidth = (int) (MeasureSpec.getSize(widthMeasureSpec) * 0.4f);
            } else {
                mMaxTabWidth = MeasureSpec.getSize(widthMeasureSpec) / 2;
            }
        } else {
            mMaxTabWidth = -1;
        }

        final int oldWidth = getMeasuredWidth();
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int newWidth = getMeasuredWidth();

        if (lockedExpanded && oldWidth != newWidth) {
            // Recenter the tab display if we're at a new (scrollable) size.
            setCurrentItem(mSelectedTabIndex);
        }
    }

    /**
     * Animate to tab.
     *
     * @param position the position
     */
    private void animateToTab(final int position) {
        final View tabView = mTabLayout.getChildAt(position);
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
        mTabSelector = new Runnable() {
            public void run() {
                final int scrollPos = tabView.getLeft()
                        - (getWidth() - tabView.getWidth()) / 2;
                smoothScrollTo(scrollPos, 0);
                mTabSelector = null;
            }
        };
        post(mTabSelector);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.ViewGroup#onAttachedToWindow()
     */
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mTabSelector != null) {
            // Re-post the selector we saved
            post(mTabSelector);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see android.view.ViewGroup#onDetachedFromWindow()
     */
    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTabSelector != null) {
            removeCallbacks(mTabSelector);
        }
    }

    /**
     * Adds the tab.
     *
     * @param index     the index
     * @param text      the text
     * @param iconResId the icon res id
     */
    private void addTab(int index, CharSequence text, int iconResId) {
        final TabView tabView = new TabView(getContext());
        tabView.mIndex = index;
        tabView.setFocusable(true);
        tabView.setOnClickListener(mTabClickListener);
        tabView.setText(text);

        tabView.setTextColor(getResources().getColor(android.R.color.white));

        int density = getResources().getDisplayMetrics().densityDpi;

        switch (density) {

            case DisplayMetrics.DENSITY_HIGH:
                tabView.setTextSize(getResources().getDimensionPixelSize(
                        R.dimen.textSizeTabTitle));
                break;

        }

        if (iconResId != 0) {
            tabView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        mTabLayout.addView(tabView, new LinearLayout.LayoutParams(WRAP_CONTENT,
                MATCH_PARENT, 1));


    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.view.ViewPager.OnPageChangeListener#
     * onPageScrollStateChanged(int)
     */
    @Override
    public void onPageScrollStateChanged(int arg0) {
        if (mListener != null) {
            mListener.onPageScrollStateChanged(arg0);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v4.view.ViewPager.OnPageChangeListener#onPageScrolled
     * (int, float, int)
     */
    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        if (mListener != null) {
            mListener.onPageScrolled(arg0, arg1, arg2);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v4.view.ViewPager.OnPageChangeListener#onPageSelected
     * (int)
     */
    @Override
    public void onPageSelected(int arg0) {
        setCurrentItem(arg0);
        if (mListener != null) {
            mListener.onPageSelected(arg0);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.synchroteam.utils.PageIndicator#setViewPager(android.support.v4.view
     * .ViewPager)
     */
    @Override
    public void setViewPager(ViewPager view) {
        if (mViewPager == view) {
            return;
        }
        if (mViewPager != null) {
            mViewPager.setOnPageChangeListener(null);
        }
        final PagerAdapter adapter = view.getAdapter();
        if (adapter == null) {
            throw new IllegalStateException(
                    "ViewPager does not have adapter instance.");
        }
        mViewPager = view;
        view.setOnPageChangeListener(this);
        notifyDataSetChanged();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.synchroteam.utils.PageIndicator#notifyDataSetChanged()
     */
    public void notifyDataSetChanged() {
        mTabLayout.removeAllViews();
        PagerAdapter adapter = mViewPager.getAdapter();
        IconPagerAdapter iconAdapter = null;
        if (adapter instanceof IconPagerAdapter) {
            iconAdapter = (IconPagerAdapter) adapter;
        }
        final int count = adapter.getCount();
        // new changes done here to add only 3 tabs in the tab in navigation bar
        // (instead of "count", "count-1" is added)
        for (int i = 0; i < count; i++) {
            CharSequence title = adapter.getPageTitle(i);
            if (title == null) {
                title = EMPTY_TITLE;
            }
            int iconResId = 0;
            if (iconAdapter != null) {
                iconResId = iconAdapter.getIconResId(i);
            }
            addTab(i, title, iconResId);
        }
        if (mSelectedTabIndex > count) {
            mSelectedTabIndex = count - 1;
        }
        setCurrentItem(mSelectedTabIndex);
        requestLayout();
    }

    /**
     * unSelected all the Tab
     */
    public void unSelectedAllTab() {

        final int tabCount = mTabLayout.getChildCount();
        for (int i = 0; i < tabCount; i++) {
            final View child = mTabLayout.getChildAt(i);

            ((TextView) child).setTextColor(getResources().getColor(
                    android.R.color.white));

        }
    }

    /**
     * sets the sixe of the tab
     */
    public void setTabSize(int count) {
        final int tabCount = mTabLayout.getChildCount();
        for (int i = 0; i < tabCount; i++) {
            final View child = mTabLayout.getChildAt(i);

//            ((TextView) child).setTextSize(getResources().getDimension(
//                    R.dimen.txt_tab_size_label));

        }
//        notifyDataSetChanged();
    }

    /**
     * Select the Tab
     */
    public void selectedTab() {

        setCurrentItem(mSelectedTabIndex);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.synchroteam.utils.PageIndicator#setViewPager(android.support.v4.view
     * .ViewPager, int)
     */
    @Override
    public void setViewPager(ViewPager view, int initialPosition) {
        setViewPager(view);
        setCurrentItem(initialPosition);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.synchroteam.utils.PageIndicator#setCurrentItem(int)
     */
    @Override
    public void setCurrentItem(int item) {
        if (mViewPager == null) {
            throw new IllegalStateException("ViewPager has not been bound.");
        }
        mSelectedTabIndex = item;
        mViewPager.setCurrentItem(item);

        final int tabCount = mTabLayout.getChildCount();
        for (int i = 0; i < tabCount; i++) {
            final View child = mTabLayout.getChildAt(i);
            final boolean isSelected = (i == item);
            child.setSelected(isSelected);
            ((TextView) child).setTextColor(getResources().getColor(
                    android.R.color.white));
            if (isSelected) {
                animateToTab(item);

                ((TextView) child).setTextColor(getResources().getColor(
                        R.color.textAddItemCatalougeListItem));
            } else {
                ((TextView) child).setTextColor(getResources().getColor(
                        android.R.color.white));

            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.synchroteam.utils.PageIndicator#setOnPageChangeListener(android.support
     * .v4.view.ViewPager.OnPageChangeListener)
     */
    @Override
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        mListener = listener;
    }

    public void setOnTabClicked(OnTabClickedListener onTabClickedListener) {
        mOnTabClickedListener = onTabClickedListener;
    }

    /**
     * The Class TabView.
     */
    private class TabView extends TextView {

        /**
         * The m index.
         */
        private int mIndex;

        /**
         * Instantiates a new tab view.
         *
         * @param context the context
         */
        public TabView(Context context) {
            super(context, null, R.attr.vpiTabPageIndicatorStyle);
            Typeface custom_font = Typeface.createFromAsset(
                    context.getAssets(),
                    context.getResources().getString(
                            R.string.fontName_hevelicaLight));
            setTypeface(custom_font);

        }

        /*
         * (non-Javadoc)
         *
         * @see android.widget.TextView#onMeasure(int, int)
         */
        @Override
        public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            // Re-measure if we went beyond our maximum size.
            if (mMaxTabWidth > 0 && getMeasuredWidth() > mMaxTabWidth) {
                super.onMeasure(MeasureSpec.makeMeasureSpec(mMaxTabWidth,
                        MeasureSpec.EXACTLY), heightMeasureSpec);
            }
        }

        /**
         * Gets the index.
         *
         * @return the index
         */
        public int getIndex() {
            return mIndex;
        }
    }

    public void fullScrollRight() {
        new Handler().postAtTime(new Runnable() {
            @Override
            public void run() {
                fullScroll(ScrollView.FOCUS_RIGHT);
            }
        }, 20);
    }

    public void fullScrollLeft() {
        new Handler().postAtTime(new Runnable() {
            @Override
            public void run() {
                fullScroll(ScrollView.FOCUS_LEFT);
            }
        }, 20);
    }

}
