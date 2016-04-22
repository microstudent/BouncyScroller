package com.microstudent.app.bouncyfastscroller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SectionIndexer;

import com.microstudent.app.bouncyfastscroller.bouncyhandle.BouncyHandle;
import com.microstudent.app.bouncyfastscroller.calculation.ScrollProgressCalculator;
import com.microstudent.app.bouncyfastscroller.indexbar.IndexBar;

/**
 * Created by MicroStudent on 2016/4/19.
 */
public abstract class AbsRecyclerViewScroller extends FrameLayout implements RecyclerViewScroller, View.OnTouchListener {

    private static final String TAG = "AbsRecyclerViewScroller";

    private SCROLLER_TYPE mBehavior;

    protected IndexBar mBar;

    protected BouncyHandle mBouncyHandle;

    protected RecyclerView mRecyclerView;

    protected RecyclerView.OnScrollListener mOnScrollListener;

    protected boolean mIsTouching = false;

    public AbsRecyclerViewScroller(Context context) {
        this(context, null);
    }

    public AbsRecyclerViewScroller(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbsRecyclerViewScroller(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        int layoutResource = getLayoutResourceId();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(layoutResource, this, true);

        initView();

        if (mBar != null) {
            Log.d(TAG, "setting listener");
            mBar.setOnTouchListener(this);
        }
    }

    private void initView() {
        mBar = (IndexBar) findViewById(R.id.scroll_bar);
        mBouncyHandle = (BouncyHandle) findViewById(R.id.scroll_handle);
    }


    @Override
    public void setRecyclerView(RecyclerView recyclerView, SCROLLER_TYPE type) {
        this.mRecyclerView = recyclerView;
        this.mBehavior = type;
    }

    @Override
    public void scrollTo(float scrollProgress, boolean fromTouch) {
        int position = getPositionFromScrollProgress(scrollProgress);
        mRecyclerView.scrollToPosition(position);

        updateSectionIndicator(position);
    }

    private void updateSectionIndicator(int position) {
        if (mBouncyHandle != null) {
            if (mRecyclerView.getAdapter() instanceof SectionIndexer) {
                SectionIndexer indexer = ((SectionIndexer) mRecyclerView.getAdapter());
                int section = indexer.getSectionForPosition(position);
                Object[] sections = indexer.getSections();
                mBouncyHandle.setHintWord(sections[section].toString());
            }
        }
    }

    //TODO 改变功能
    private int getPositionFromScrollProgress(float scrollProgress) {
        return (int) (mRecyclerView.getAdapter().getItemCount() * scrollProgress);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (getScrollProgressCalculator() == null) {
            onCreateScrollProgressCalculator();
        }
        if (!mIsTouching) {
            // synchronize the handle position to the RecyclerView
            float scrollProgress = getScrollProgressCalculator().calculateScrollProgress(mRecyclerView);
            moveHandleToPosition(scrollProgress);
        }
    }

    protected abstract void onCreateScrollProgressCalculator();

    @Override
    public RecyclerView.OnScrollListener getOnScrollListener() {
        if (mOnScrollListener == null) {
            mOnScrollListener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    float scrollProgress = 0;
                    ScrollProgressCalculator scrollProgressCalculator = getScrollProgressCalculator();
                    if (scrollProgressCalculator != null && !mIsTouching) {
                        scrollProgress = scrollProgressCalculator.calculateScrollProgress(recyclerView);
                        moveHandleToPosition(scrollProgress);
                    }
                    Log.d(TAG, "rv is scrolling");
                }
            };
        }
        return mOnScrollListener;
    }

    protected abstract ScrollProgressCalculator getScrollProgressCalculator();

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        showOrHideIndicator(event);

        float scrollProgress = getScrollProgress(event);
        scrollTo(scrollProgress, true);

        moveHandleToPosition(scrollProgress);

        return true;
    }

    protected abstract void moveHandleToPosition(float scrollProgress);

    protected float getScrollProgress(MotionEvent event){
        ScrollProgressCalculator scrollProgressCalculator = getScrollProgressCalculator();
        if (scrollProgressCalculator != null) {
            return getScrollProgressCalculator().calculateScrollProgress(event);
        }
        return 0;
    }

    protected abstract int getLayoutResourceId();

    protected void showOrHideIndicator(MotionEvent event){
        if (mBouncyHandle == null) {
            return;
        }

        Log.d(TAG, "ON touch and the y is :" + String.valueOf(event.getY()));

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mIsTouching = true;
                  mBouncyHandle.showHandle();
                return;
            case MotionEvent.ACTION_UP:
                mIsTouching = false;
                mBouncyHandle.hideHandle();
                if (mBehavior == SCROLLER_TYPE.SHOW_INDEX_IN_NEED) {
                    mBar.hideIndexBar();
                }
        }
    }
}
