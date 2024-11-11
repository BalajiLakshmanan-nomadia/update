package com.synchroteam.utils;

import android.animation.Animator;
import android.content.Context;
import android.os.Build;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

import com.synchroteam.listadapters.AllJobsDateAdapterRv;
import com.synchroteam.listadapters.AllJobsSortingAdapterRv;
import com.synchroteam.listadapters.CurrentJobsAdapterRv;
import com.synchroteam.synchroteam3.R;

/**
 * CoordinatorLayout Behavior for a quick return header
 * </p>
 * When the nested ScrollView is swiped down for first time (like swipe to refresh), the quick return view will appear.
 * When a nested ScrollView is scrolled down, the quick return view will disappear.
 * When the ScrollView is scrolled back up, the quick return view will reappear.
 * </p>
 * <p>
 * Created by Trident on 11/8/2016.
 */

//TODO Remove all suppress lint and write corrsponding animation for lower api versions

public class QuickReturnBehaviorForCurrentJobs extends CoordinatorLayout.Behavior<View> {

    private Context mContext;
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();

    private boolean mIsShowing;
    private boolean mIsHiding;
    public static int mDySinceDirectionChange;
    public static  boolean mIsFirstSwipeUp = true;

    public QuickReturnBehaviorForCurrentJobs(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
        Log.e("scroll y", "dy : " + dy);
        RecyclerView rv = (RecyclerView) target;
        LinearLayoutManager lm = (LinearLayoutManager) rv.getLayoutManager();
        assert lm != null;
        int visibleItemPosition = lm.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = lm.findLastVisibleItemPosition();

        if (mIsFirstSwipeUp) {
            if (dy < -10 && mDySinceDirectionChange == 0) {

                if (visibleItemPosition == 0) {
                    show(child);

                    RecyclerView.Adapter adapter = rv.getAdapter();
                    if (adapter instanceof AllJobsDateAdapterRv) {
                        AllJobsDateAdapterRv adapterNew = (AllJobsDateAdapterRv)adapter;
                        adapterNew.setHeaderShown(true);
                        adapterNew.setIncrementValue(1);
                        adapterNew.notifyDataSetChanged();
                    }else if (adapter instanceof AllJobsSortingAdapterRv){
                        AllJobsSortingAdapterRv adapterNew = (AllJobsSortingAdapterRv) adapter;
                        adapterNew.setHeaderShown(true);
                        adapterNew.setIncrementValue(1);
                        adapterNew.notifyDataSetChanged();
                    }else if (adapter instanceof CurrentJobsAdapterRv){
                        CurrentJobsAdapterRv adapterNew = (CurrentJobsAdapterRv) adapter;
                        adapterNew.setHeaderShown(true);
                        adapterNew.setIncrementValue(1);
                        adapterNew.notifyDataSetChanged();
                    }
                    mIsFirstSwipeUp = false;
                }
            }
        } else {
            if (dy > 0 && mDySinceDirectionChange < 0
                    || dy < 0 && mDySinceDirectionChange > 0) {
                // We detected a direction change- cancel existing animations and reset our cumulative delta Y
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    child.animate().cancel();
                }else {
                }

                mDySinceDirectionChange = 0;
            }

            mDySinceDirectionChange += dy;

            Log.e("scroll y", "dy : " + dy + " mDySinceDirectionChange " + mDySinceDirectionChange);

            if (mDySinceDirectionChange > child.getHeight()
                    && child.getVisibility() == View.VISIBLE
                    && !mIsHiding && lastVisibleItemPosition > 4) {
                hide(child);
            } else if (mDySinceDirectionChange < 0
                    && child.getVisibility() == View.INVISIBLE
                    && !mIsShowing) {
                show(child);
            }
//        }
        }
    }

    /**
     * Hide the quick return view.
     * <p>
     * Animates hiding the view, with the view sliding down and out of the screen.
     * After the view has disappeared, its visibility will change to GONE.
     *
     * @param view The quick return view
     */
    private void hide(final View view) {
        mIsHiding = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ViewPropertyAnimator animator = view.animate()
                .translationY(-view.getHeight())
                .setInterpolator(INTERPOLATOR)
                .setDuration(200);

        animator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                // Prevent drawing the View after it is gone
                mIsHiding = false;
                view.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                // Canceling a hide should show the view
                mIsHiding = false;
                if (!mIsShowing) {
                    show(view);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });

            animator.start();
        }else {
            Animation slideUp = AnimationUtils.loadAnimation(mContext, R.anim.slide_up);
            slideUp.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // Prevent drawing the View after it is gone
                    mIsHiding = false;
                    view.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(slideUp);
        }
    }

    /**
     * Show the quick return view.
     * <p>
     * Animates showing the view, with the view sliding up from the bottom of the screen.
     * After the view has reappeared, its visibility will change to VISIBLE.
     *
     * @param view The quick return view
     */
    private void show(final View view) {
        mIsShowing = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ViewPropertyAnimator animator = view.animate()
                    .translationY(0)
                    .setInterpolator(INTERPOLATOR)
                    .setDuration(200);

            animator.setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                    view.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    mIsShowing = false;
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                    // Canceling a show should hide the view
                    mIsShowing = false;
                    if (!mIsHiding) {
                        hide(view);
                    }
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });

            animator.start();
        }else {
            Animation slideDown = AnimationUtils.loadAnimation(mContext, R.anim.slide_down);
            slideDown.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    view.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    // Prevent drawing the View after it is gone
                    mIsHiding = false;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            view.startAnimation(slideDown);
        }
    }

}
