package com.microstudent.app.bouncyfastscroller;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
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
 *
 * Created by MicroStudent on 2016/4/19.
 */
public abstract class AbsRecyclerViewScroller extends FrameLayout implements RecyclerViewScroller, View.OnTouchListener {

    private static final String TAG = "AbsRecyclerViewScroller";

    private static final long DEFAULT_ALPHA_OUT_DELAY = 1000;

    private static final CharSequence DEFAULT_ALPHABET = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final int DEFAULT_COLOR = Color.GREEN;

    private static final float DEFAULT_ALPHA = 0.6f;


    protected int mBehavior;

    protected IndexBar mIndexBar;

    protected BouncyHandle mBouncyHandle;

    protected View mThumb;

    protected RecyclerView mRecyclerView;

    protected RecyclerView.OnScrollListener mOnScrollListener;

    private AlphabetIndexer mAlphabetIndexer;

    private ArrayList<String> mData;

    protected boolean mIsTouching = false;

    private float mAlpha = 1f;

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

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AbsRecyclerViewScroller);
        try {
            mBehavior =  array.getInt(R.styleable.AbsRecyclerViewScroller_behavior, SIMPLE);
            int color = array.getColor(R.styleable.AbsRecyclerViewScroller_theme_color, DEFAULT_COLOR);
            setThemeColor(color);
            setAlpha(array.getFloat(R.styleable.AbsRecyclerViewScroller_alpha2, DEFAULT_ALPHA));
        }finally {
            array.recycle();
        }

        setupView();
    }

    public void setThemeColor(int color) {
        mBouncyHandle.setHintColor(color);
        mThumb.setBackgroundColor(color);
    }

    @Override
    public void setAlpha(float alpha) {
        mAlpha = alpha;
        mThumb.setAlpha(alpha);
        mBouncyHandle.setAlpha(alpha);
        mIndexBar.setAlpha(alpha);
    }

    private void setupView() {
        if (mIndexBar != null) {
            Log.d(TAG, "setting listener");
            mIndexBar.setOnTouchListener(this);
            if (mBehavior == SIMPLE) {
                mIndexBar.setVisitable(false);
                mThumb.setVisibility(VISIBLE);
            } else {
                mIndexBar.setVisitable(true);
                mThumb.setVisibility(INVISIBLE);
            }
            if (mRecyclerView != null) {
                mRecyclerView.addOnScrollListener(getOnScrollListener());
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
        mIndexBar = (IndexBar) findViewById(R.id.index_bar);
        mBouncyHandle = (BouncyHandle) findViewById(R.id.scroll_handle);
        mThumb = findViewById(R.id.thumb);
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        setRecyclerView(recyclerView, mBehavior);
    }

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

    private int getPositionFromScrollProgress(float scrollProgress) {
        return (int) (mRecyclerView.getAdapter().getItemCount() * scrollProgress);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (getScrollProgressCalculator() == null) {
            onCreateScrollProgressCalculator();
        }
        //it is meaningless to sync the handle when in SIMPLE mode. And doesn't have to sync when touching.
        if (!mIsTouching && mBehavior == SIMPLE) {
            // synchronize the handle position to the RecyclerView
            float scrollProgress = getScrollProgressCalculator().calculateScrollProgress(mRecyclerView);
            moveHandleToPosition(scrollProgress);
            Log.d(TAG, "onLayout!");
        }
    }

    protected abstract void onCreateScrollProgressCalculator();

    @Override
    public RecyclerView.OnScrollListener getOnScrollListener() {
        if (mOnScrollListener == null) {
            mOnScrollListener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    float scrollProgress;
                    ScrollProgressCalculator scrollProgressCalculator = getScrollProgressCalculator();
                    if (scrollProgressCalculator != null && !mIsTouching) {
                        scrollProgress = scrollProgressCalculator.calculateScrollProgress(recyclerView);
                        moveHandleToPosition(scrollProgress);
                    }
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        //The RecyclerView is not currently scrolling.
                        if (mBehavior == SIMPLE) {
                            mThumb.animate().alpha(0).setStartDelay(DEFAULT_ALPHA_OUT_DELAY);
                        } else if (mBehavior == SHOW_INDEX_IN_NEED) {
                            mIndexBar.hideIndexBar(DEFAULT_ALPHA_OUT_DELAY);
                        }
                    } else {
                        if (mBehavior == SIMPLE) {
                            mThumb.setAlpha(mAlpha);
                        } else if (mBehavior == SHOW_INDEX_IN_NEED) {
                            mIndexBar.showIndexBar();
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

        if (mBehavior == ALWAYS_SHOW_INDEX || mBehavior == SHOW_INDEX_IN_NEED) {
            showOrHideIndicator(event);
            if (mIsTouching) {
                mIndexBar.setVisitable(true);
            }
            int section = getTouchingSection(event.getY());
            scrollTo(section, true);
        } else {
            scrollTo(scrollProgress, true);
        }
        moveHandleToPosition(scrollProgress);

        return true;
    }

    protected int getTouchingSection(float y) {
        return mIndexBar.getSectionIndex(y);
    }


    private void scrollTo(int section, boolean fromTouch) {
        int position = getPositionFromSection(section);
        if (position != -1) {
            Log.d(TAG, "scroll to " + String.valueOf(section));
            Log.d(TAG, "position =  " + String.valueOf(position));


            if (!(mRecyclerView.getLayoutManager() instanceof LinearLayoutManager)) {
                Log.e(TAG, "recyclerView's layout manager isn't a LinearLayoutManager, not Supported!");
                return;
            }

            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            linearLayoutManager.scrollToPositionWithOffset(position, 0);

//            the method below doesn't scroll item to the top! so delete it, and using linearLayoutManager.scrollToPositionWithOffset
//            mRecyclerView.scrollToPosition(position);

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
        if (!(mRecyclerView.getLayoutManager() instanceof LinearLayoutManager)) {
            Log.e(TAG, "recyclerView's layout manager isn't a LinearLayoutManager, not Supported!");
            return;
        }

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        linearLayoutManager.scrollToPositionWithOffset(position, 0);

//            the method below doesn't scroll item to the top! so delete it, and using linearLayoutManager.scrollToPositionWithOffset
//            mRecyclerView.scrollToPosition(position);
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
        }
    }
}
