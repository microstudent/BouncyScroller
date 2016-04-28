package com.microstudent.app.bouncyfastscroller.calculation;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by MicroStudent on 2016/4/19.
 */
public class VerticalScrollProgressCalculator implements ScrollProgressCalculator {
    private static final String TAG = "VSProgressCalculator";

    private final VerticalScrollBoundsProvider mScrollBoundsProvider;

    public VerticalScrollProgressCalculator(VerticalScrollBoundsProvider mScrollBoundsProvider) {
        this.mScrollBoundsProvider = mScrollBoundsProvider;
    }

    @Override
    public float calculateScrollProgress(RecyclerView recyclerView) {
        if (recyclerView == null) {
            return 0;
        }
        if (!(recyclerView.getLayoutManager() instanceof LinearLayoutManager)) {
            Log.e(TAG, "recyclerView's layout manager isn't a LinearLayoutManager, not Supported!");
            return 0;
        }

        float offset = recyclerView.computeVerticalScrollOffset();
        int range = recyclerView.computeVerticalScrollRange();
        int extent = recyclerView.computeVerticalScrollExtent();
        return offset / (range - extent);
    }

    @Override
    public float calculateScrollProgress(MotionEvent event) {
        float y = event.getY();

        if (y <= mScrollBoundsProvider.getMinimumScrollY()) {
            return 0;
        } else if (y >= mScrollBoundsProvider.getMaximumScrollY()) {
            return 1;
        } else {
            return y / mScrollBoundsProvider.getMaximumScrollY();
        }
    }
}
