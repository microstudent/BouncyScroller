package com.microstudent.app.bouncyfastscroller.vertical;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.microstudent.app.bouncyfastscroller.AbsRecyclerViewScroller;
import com.microstudent.app.bouncyfastscroller.R;
import com.microstudent.app.bouncyfastscroller.calculation.ScrollProgressCalculator;
import com.microstudent.app.bouncyfastscroller.calculation.VerticalScreenPositionCalculator;
import com.microstudent.app.bouncyfastscroller.calculation.VerticalScrollBoundsProvider;
import com.microstudent.app.bouncyfastscroller.calculation.VerticalScrollProgressCalculator;

/**
 *
 * Created by MicroStudent on 2016/4/19.
 */
public class VerticalBouncyFastScroller extends AbsRecyclerViewScroller {

    private static final String TAG = "VBFastScroller";

    private VerticalScrollProgressCalculator mScrollProgressCalculator;
    private VerticalScreenPositionCalculator mScreenPositionCalculator;

    public VerticalBouncyFastScroller(Context context) {
        this(context, null);
    }

    public VerticalBouncyFastScroller(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalBouncyFastScroller(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }


    @Override
    protected void onCreateScrollProgressCalculator() {
        View bar = (View) mIndexBar;
        VerticalScrollBoundsProvider boundsProvider;
        if (mBehavior == SIMPLE) {
            boundsProvider =
                    new VerticalScrollBoundsProvider(getPaddingTop(), getPaddingTop() + bar.getHeight() - mThumb.getHeight() - getPaddingBottom());
            Log.d(TAG, "thumb's height is " + String.valueOf(mThumb.getHeight()));
        } else {
            boundsProvider =
                    new VerticalScrollBoundsProvider(getPaddingTop(), getHeight() - getPaddingBottom());
        }

        mScrollProgressCalculator = new VerticalScrollProgressCalculator(boundsProvider);
        mScreenPositionCalculator = new VerticalScreenPositionCalculator(boundsProvider);
    }

    @Override
    protected ScrollProgressCalculator getScrollProgressCalculator() {
        return mScrollProgressCalculator;
    }

    @Override
    protected void moveHandleToPosition(float scrollProgress) {
        float y = mScreenPositionCalculator.getYPositionFromScrollProgress(scrollProgress);
        Log.d(TAG, "scrollProgress = " + String.valueOf(scrollProgress));
        if (mBehavior == SIMPLE) {
            mThumb.setY(y);
        } else if (mBehavior == SIMPLE_WITH_INDICATOR) {
            mThumb.setY(y);
            View handle = (View) mBouncyHandle;
            handle.setY(y - handle.getHeight() / 2);
        } else {
            View handle = (View) mBouncyHandle;
            handle.setY(y - handle.getHeight() / 2);
        }
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.vertical_bouncy_fast_scroller_layout;
    }
}
