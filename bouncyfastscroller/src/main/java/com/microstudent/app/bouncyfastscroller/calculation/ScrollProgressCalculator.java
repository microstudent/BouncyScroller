package com.microstudent.app.bouncyfastscroller.calculation;

import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by MicroStudent on 2016/4/19.
 */
public interface ScrollProgressCalculator {
    /**
     * Calculates the scroll progress of a provided RecyclerView
     * @param recyclerView for which to calculate scroll progress
     * @return fraction from [0 to 1] representing the scroll progress
     */
    float calculateScrollProgress(RecyclerView recyclerView);

    /**
     * Calculates the scroll progress of a RecyclerView based on a motion event from a scroller
     * @param event for which to calculate scroll progress
     * @return fraction from [0 to 1] representing the scroll progress
     */
    float calculateScrollProgress(View view, MotionEvent event);
}
