package com.microstudent.app.bouncyfastscroller;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AlphabetIndexer;
import android.widget.FrameLayout;
import android.widget.SectionIndexer;

import com.microstudent.app.bouncyfastscroller.bouncyhandle.BouncyHandle;
import com.microstudent.app.bouncyfastscroller.calculation.ScrollProgressCalculator;
import com.microstudent.app.bouncyfastscroller.indexbar.IndexBar;
import com.microstudent.app.bouncyfastscroller.indexer.IndexCursor;

import java.util.ArrayList;

/**
 * Created by MicroStudent on 2016/4/19.
 */
public abstract class AbsRecyclerViewScroller extends FrameLayout implements RecyclerViewScroller, View.OnTouchListener {

    private static final String TAG = "AbsRecyclerViewScroller";

    private static final long DEFAULT_ALPHA_OUT_DELAY = 1000;

    private static final CharSequence DEFAULT_ALPHABET = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    protected int mBehavior;

    protected IndexBar mBar;

    protected BouncyHandle mBouncyHandle;

    protected View mProgressIndicator;

    protected RecyclerView mRecyclerView;

    protected RecyclerView.OnScrollListener mOnScrollListener;

    private AlphabetIndexer mAlphabetIndexer;

    private ArrayList<String> mData;

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

        TypedArray array = context.obtainStyledAttributes(R.styleable.VerticalBouncyFastScroller);
        try {
            mBehavior =  array.getInt(R.styleable.VerticalBouncyFastScroller_behavior, SIMPLE);
        }finally {
            array.recycle();
        }

        initView();

        setupView();
    }

    private void setupView() {
        if (mBar != null) {
            Log.d(TAG, "setting listener");
            mBar.setOnTouchListener(this);
            if (mBehavior == SIMPLE) {
                mBar.setVisitable(false);
            } else {
                mBar.setVisitable(true);
            }
        }
    }

    protected ArrayList<String> getData(){
        if (mData != null) {
            return mData;
        } else {
            throw new RuntimeException("the recyclerView's data is null! call setData() after setRecyclerView()");
        }
    }

    public void setData(ArrayList<String> Data) {
        this.mData = Data;
    }

    protected void initView() {
        mBar = (IndexBar) findViewById(R.id.scroll_bar);
        mBouncyHandle = (BouncyHandle) findViewById(R.id.scroll_handle);
        mProgressIndicator = findViewById(R.id.progress_indicator);
    }


    protected abstract void showOrHideProgressIndicator(RecyclerView mRecyclerView);

    @Override
    public void setRecyclerView(RecyclerView recyclerView, int type) {
        this.mRecyclerView = recyclerView;
        this.mBehavior = type;
        setupView();
    }

    private void updateSectionIndicatorBySection(int section) {
        if (mBouncyHandle != null) {
            mBouncyHandle.setHintWord(String.valueOf(DEFAULT_ALPHABET.charAt(section)));
        }
    }

    private void updateSectionIndicatorByPosition(int position) {
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
                    if (mBehavior == SIMPLE) {
                        if (mProgressIndicator.getAlpha() != 1) {
                            mProgressIndicator.setAlpha(1);
                        } else {
                            mProgressIndicator.animate().alpha(0).setStartDelay(DEFAULT_ALPHA_OUT_DELAY);
                        }
                    }
                }
            };
        }
        return mOnScrollListener;
    }

    protected abstract ScrollProgressCalculator getScrollProgressCalculator();

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        mIsTouching = event.getActionMasked() != MotionEvent.ACTION_UP;

        float scrollProgress = getScrollProgress(event);

        if (mBehavior == ALWAYS_SHOW_INDEX) {
            showOrHideIndicator(event);
            int section = getTouchingSection(scrollProgress);
            scrollTo(section, true);
        } else {
            scrollTo(scrollProgress, true);
        }
        moveHandleToPosition(scrollProgress);

        return true;
    }

    protected int getTouchingSection(float scrollProgress){
        if (scrollProgress <= 1 && scrollProgress >= 0) {
            int length = DEFAULT_ALPHABET.length() - 1;
            return (int) (length * scrollProgress);
        }
        return 0;
    }


    private void scrollTo(int section, boolean fromTouch) {
        int position = getPositionFromSection(section);
        if (position != -1) {
            mRecyclerView.scrollToPosition(position);
            if (fromTouch) {
                updateSectionIndicatorBySection(section);
            }
        }
    }

    private int getPositionFromSection(int section) {
        if (mAlphabetIndexer != null) {
            return mAlphabetIndexer.getPositionForSection(section);
        } else {
            mAlphabetIndexer = new AlphabetIndexer(new IndexCursor(getData()), 0, DEFAULT_ALPHABET);
            return mAlphabetIndexer.getPositionForSection(section);
        }
    }


    @Override
    public void scrollTo(float scrollProgress, boolean fromTouch) {
        int position = getPositionFromScrollProgress(scrollProgress);
        mRecyclerView.scrollToPosition(position);
        if (fromTouch) {
            updateSectionIndicatorByPosition(position);
        }
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
        if (mBouncyHandle == null || mBehavior == SIMPLE) {
            return;
        }

        Log.d(TAG, "ON touch and the y is :" + String.valueOf(event.getY()));

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mBouncyHandle.showHandle();
                return;
            case MotionEvent.ACTION_UP:
                mBouncyHandle.hideHandle();
                if (mBehavior == SHOW_INDEX_IN_NEED) {
                    mBar.hideIndexBar();
                }
        }
    }
}
