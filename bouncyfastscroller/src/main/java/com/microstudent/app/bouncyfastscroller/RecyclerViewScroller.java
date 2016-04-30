package com.microstudent.app.bouncyfastscroller;

import android.support.v7.widget.RecyclerView;

/**
 * a scroller for recyclerView, has more than one behavior.
 * Created by MicroStudent on 2016/4/19.
 */
public interface RecyclerViewScroller {
    /**
     * the behavior for indicator.
     * SIMPLE means that only show a thumb.
     * SIMPLE_WITH_INDICATOR show the indicator when user touch the thumb.
     * SHOW_INDEX_IN_NEED means that the indexBar would be hided automatically.
     * ALWAYS_SHOW_INDEX means that there will always be a indexBar shown.
     */
    int SIMPLE = 0;
    int SIMPLE_WITH_INDICATOR = 1;
    int SHOW_INDEX_IN_NEED = 2;
    int ALWAYS_SHOW_INDEX = 3;

    /**
     * connect receiverView to scroller.
     * @param recyclerView the recyclerView that need a scroller.
     * @param behavior the behavior for the scroller.
     */
    void setRecyclerView(RecyclerView recyclerView, int behavior);

    /**
     * Indicate to the scroller that it should scroll to a certain amount of scroll progress
     * @param scrollProgress the progress of the scroll expressed as a fraction from [0, 1]
     * @param fromTouch true if this scroll request was triggered by a touch
     */
    void scrollRecyclerViewTo(float scrollProgress, boolean fromTouch);

    /**
     * Since {@link RecyclerView.OnScrollListener} is not implemented as an interface, RecyclerViewScrollers cannot implement this
     * interface, and in most cases, it makes no sense for them to extend a scroll listener. For this reason, we must
     * provide a listener that is intended to be fetched and set as a listener on a {@link RecyclerView}.
     *
     * This assumes that a scroller should to know when RecyclerViews are scrolled independently of scroller actions.
     *
     * @return this scroller's listener for a RecyclerView's scrolling.
     */
    RecyclerView.OnScrollListener getOnScrollListener();
}
