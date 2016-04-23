package com.microstudent.app.bouncyfastscroller.calculation;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by MicroStudent on 2016/4/19.
 */
public class VerticalScrollProgressCalculator implements ScrollProgressCalculator {
    private final VerticalScrollBoundsProvider mScrollBoundsProvider;

    public VerticalScrollProgressCalculator(VerticalScrollBoundsProvider mScrollBoundsProvider) {
        this.mScrollBoundsProvider = mScrollBoundsProvider;
    }

    @Override
    public float calculateScrollProgress(RecyclerView recyclerView) {
        if (recyclerView == null) {
            return 0;
        }
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int lastFullyVisiblePosition = layoutManager.findLastCompletelyVisibleItemPosition();

        View visibleChild = recyclerView.getChildAt(0);
        if (visibleChild == null) {
            return 0;
        }
        RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(visibleChild);
        int itemHeight = holder.itemView.getHeight();
        int recyclerHeight = recyclerView.getHeight();
        int itemsInWindow = recyclerHeight / itemHeight;

        int numItemsInList = recyclerView.getAdapter().getItemCount();
        int numScrollableSectionsInList = numItemsInList - itemsInWindow;
        int indexOfLastFullyVisibleItemInFirstSection = numItemsInList - numScrollableSectionsInList - 1;

        int currentSection = lastFullyVisiblePosition - indexOfLastFullyVisibleItemInFirstSection;

        return (float) currentSection / numScrollableSectionsInList;
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

    public boolean shouldProgressIndicatorShown(RecyclerView recyclerView) {
        if (recyclerView == null) {
            return false;
        }

        View visibleChild = recyclerView.getChildAt(0);
        if (visibleChild == null) {
            return false;
        }
        RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(visibleChild);

        int itemHeight = holder.itemView.getHeight();
        int recyclerHeight = recyclerView.getHeight();

        int size = recyclerView.getAdapter().getItemCount();

        return size * itemHeight >= recyclerHeight;
    }
}
