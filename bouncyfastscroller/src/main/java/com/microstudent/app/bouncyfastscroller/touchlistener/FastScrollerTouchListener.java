package com.microstudent.app.bouncyfastscroller.touchlistener;

import android.view.MotionEvent;
import android.view.View;

import com.microstudent.app.bouncyfastscroller.AbsRecyclerViewScroller;

/**
 * a listener for SIMPLE behavior.
 * Created by MicroStudent on 2016/4/28.
 */
public class FastScrollerTouchListener implements View.OnTouchListener {

    private final AbsRecyclerViewScroller mScroller;

    public FastScrollerTouchListener(AbsRecyclerViewScroller recyclerViewScroller) {
        mScroller = recyclerViewScroller;
    }

    /**
     * when the behavior is SIMPLE,than in onTouch method,

     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {

//        mIsTouching = event.getActionMasked() != MotionEvent.ACTION_UP;
//
//        float scrollProgress = getScrollProgress(event);
//
//        if (mBehavior == ALWAYS_SHOW_INDEX || mBehavior == SHOW_INDEX_IN_NEED) {
//            showOrHideHandle(event);
//            if (mIsTouching) {
//                mIndexBar.setVisitable(true);
//            }
//            int section = getTouchingSection(event.getY());
//            scrollTo(section, true);
//        } else {
//            scrollRecyclerViewTo(scrollProgress, true);
//        }
//        moveHandleToPosition(scrollProgress);
        return true;
    }
}
