package com.microstudent.app.bouncyfastscroller.vertical;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.microstudent.app.bouncyfastscroller.AbsRecyclerViewScroller;
import com.microstudent.app.bouncyfastscroller.R;
import com.microstudent.app.bouncyfastscroller.bouncyhandle.BouncyHandleImpl;
import com.microstudent.app.bouncyfastscroller.calculation.ScrollProgressCalculator;
import com.microstudent.app.bouncyfastscroller.calculation.VerticalScreenPositionCalculator;
import com.microstudent.app.bouncyfastscroller.calculation.VerticalScrollBoundsProvider;
import com.microstudent.app.bouncyfastscroller.calculation.VerticalScrollProgressCalculator;
import com.microstudent.app.bouncyfastscroller.indexbar.IndexBar;

/**
 * Created by MicroStudent on 2016/4/19.
 */
public class VerticalBouncyFastScroller extends AbsRecyclerViewScroller {

    private static final String TAG = "VerticalBouncyFastScroller";

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
    protected void onCreateScrollProgressCalculator() {
        View bar = (View) mBar;
        VerticalScrollBoundsProvider boundsProvider =
                new VerticalScrollBoundsProvider(0, bar.getHeight());
        mScrollProgressCalculator = new VerticalScrollProgressCalculator(boundsProvider);
        mScreenPositionCalculator = new VerticalScreenPositionCalculator(boundsProvider);

        mBouncyHandle.setIndicatorVisibility(GONE);
    }

    @Override
    protected ScrollProgressCalculator getScrollProgressCalculator() {
        return mScrollProgressCalculator;
    }

    @Override
    protected void moveHandleToPosition(float scrollProgress) {
        float y = mScreenPositionCalculator.getYPositionFromScrollProgress(scrollProgress);
        //show handle in its mid
        View handle = (View) mBouncyHandle;
        handle.setY(y - handle.getHeight() / 2);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.vertical_bouncy_fast_scroller_layout;
    }
}
