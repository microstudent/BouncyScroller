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
import android.view.ViewGroup;
import android.widget.AlphabetIndexer;
import android.widget.FrameLayout;
import android.widget.SectionIndexer;

import com.microstudent.app.bouncyfastscroller.bouncyhandle.BouncyHandle;
import com.microstudent.app.bouncyfastscroller.calculation.ScrollProgressCalculator;
import com.microstudent.app.bouncyfastscroller.indexbar.IndexBar;
import com.microstudent.app.bouncyfastscroller.indexer.IndexCursor;
import com.microstudent.app.bouncyfastscroller.thumb.Thumb;

import java.util.ArrayList;

/**
 * an abstract class that almost implement all feature in the scroller.
 * It can be decoupled further.
 * Created by MicroStudent on 2016/4/19.
 */
public abstract class AbsRecyclerViewScroller extends FrameLayout implements RecyclerViewScroller, View.OnTouchListener {

    private static final String TAG = "AbsRecyclerViewScroller";
    //The default animation's start delay.
    private static final long DEFAULT_ANIMATION_DELAY = 1000;
    //The default color.
    private static final int DEFAULT_COLOR = Color.GREEN;
    //The default alpha.
    private static final float DEFAULT_ALPHA = 0.6f;
    //the behavior of scroller.
    protected int mBehavior;
    //indexBar in INDEX's behavior.
    protected IndexBar mIndexBar;
    //a handle with a indicator which can show some key word.
    protected BouncyHandle mBouncyHandle;
    //the thumb of scroller for SIMPLE behavior.
    protected Thumb mThumb;
    //the recyclerView to scroll.
    protected RecyclerView mRecyclerView;

    protected RecyclerView.OnScrollListener mOnScrollListener;
    //the indexer for indexBar.
    private AlphabetIndexer mAlphabetIndexer;

    private ArrayList<String> mData;
    /**
     *  when is touching, the handle or thumb will NOT move the position
     *  which is calculated according to recyclerView.
     *  because position calculated by touchEvent doesn't fit to the position calculated by recyclerView.
     *  if we don't shield that, the handle or thumb will shown incorrect.
     */
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
        mThumb.setColor(color);
    }

    @Override
    public void setAlpha(float alpha) {
        mThumb.setAlpha(alpha);
        mBouncyHandle.setAlpha(alpha);
        mIndexBar.setAlpha(alpha);
    }

    private void initView() {
        mIndexBar = (IndexBar) findViewById(R.id.index_bar);
        mBouncyHandle = (BouncyHandle) findViewById(R.id.scroll_handle);
        mThumb = (Thumb) findViewById(R.id.thumb);
    }

    private void setupView() {
        if (mIndexBar != null) {
            mIndexBar.setOnTouchListener(this);

            if (mBehavior == SIMPLE || mBehavior == SIMPLE_WITH_INDICATOR) {
                mIndexBar.setVisitable(false);
                mThumb.setVisible(true);
            } else {
                mIndexBar.setVisitable(true);
                mThumb.setVisible(false);
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

    /**
     * setting the data from recyclerView, the data is responsible to sort by alphabet.
     * @param Data data from recyclerView
     */
    public void setData(ArrayList<String> Data) {
        this.mData = Data;
    }

    /**
     * connect recycler view to this scroller, with the default behavior SIMPLE.
     * @param recyclerView the recycler view to connect.
     */
    public void setRecyclerView(RecyclerView recyclerView) {
        setRecyclerView(recyclerView, mBehavior);
    }

    /**
     * connect recyclerView to this scroller, with specific behavior.
     * @param recyclerView the recyclerView that need a scroller.
     * @param behavior behavior, see the interface.
     */
    @Override
    public void setRecyclerView(RecyclerView recyclerView, int behavior) {
        this.mRecyclerView = recyclerView;
        this.mBehavior = behavior;
        setupView();
    }

    /**
     * while it is in INDEX_BAR behavior, we calculate the section index first, and than call this method,
     * to update handle's indicator.
     * @param section index of sections.
     */
    private void updateSectionIndicatorBySection(int section) {
        if (mBouncyHandle != null) {
            mBouncyHandle.setHintWord(String.valueOf(getIndexBarKeyword().charAt(section)));
        }
    }

    /**
     * while in SIMPLE_WITH_INDICATOR behavior, it has a indicator to update,
     * the recyclerView's adapter must implement #SectionIndexer# to support this behavior.
     * @param position the position in recyclerView.
     */
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

    /**
     * when we know the scrollProgress, we can calculate out the position in recyclerView.
     * @param scrollProgress [0,1]
     * @return the position in recyclerView.
     */
    private int getPositionFromScrollProgress(float scrollProgress) {
        if (scrollProgress >= 1) {
            return mRecyclerView.getAdapter().getItemCount() - 1;
        }
        return (int) (mRecyclerView.getAdapter().getItemCount() * scrollProgress);
    }

    /**
     *while layout, we create ScrollProgressCalculator, which can help subclass to provide some thing about calculation.
     * to support padding's feature, we must set clipChildren and clipToPadding to false, if not ,content will not draw outside.
     * also, when layout, if in SIMPLE behavior, and is NOT touching, we have to sync the position for thumb.
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (getScrollProgressCalculator() == null) {
            onCreateScrollProgressCalculator();
        }

        setClipChildren(false);
        setClipToPadding(false);
        ViewGroup parent = (ViewGroup) getParent();
        if (parent != null) {
            parent.setClipChildren(false);
            parent.setClipToPadding(false);
        }

        //it is meaningless to sync the handle when in SIMPLE mode. And doesn't have to sync when touching.
        if (!mIsTouching && mBehavior == SIMPLE || mBehavior == SIMPLE_WITH_INDICATOR) {
            // synchronize the handle position to the RecyclerView
            float scrollProgress = getScrollProgressCalculator().calculateScrollProgress(mRecyclerView);
            moveHandleToPosition(scrollProgress);
            Log.d(TAG, "onLayout!");
        }
    }

    /**
     * in this method, subclass must create a VerticalScrollBoundsProvider, a VerticalScrollProgressCalculator
     * and a VerticalScreenPositionCalculator according to its requirement.
     */
    protected abstract void onCreateScrollProgressCalculator();

    /**
     * create a OnScrollerListener for recyclerView, we have added Listener while setRecyclerView was called.
     * @return the OnScrollerListener for recyclerView.
     */
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
                //to support auto hide feature. we hide thumb when recyclerView is NOT scrolling, and show when scrolling.
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        //The RecyclerView is not scrolling.
                        if (mBehavior == SIMPLE) {
                            mThumb.hide(DEFAULT_ANIMATION_DELAY);
                        } else if (mBehavior == SHOW_INDEX_IN_NEED) {
                            mIndexBar.hideIndexBar(DEFAULT_ANIMATION_DELAY);
                        }
                    } else {
                        //scrolling
                        if (mBehavior == SIMPLE) {
                            mThumb.show(0);
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

    /**
     * onTouch from indexBar, NOT from this frameLayout itself!
     * @param v the indexBar.
     * @param event event from indexBar.
     * @return if true, we handle this event.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        mIsTouching = event.getActionMasked() != MotionEvent.ACTION_UP;

        float scrollProgress = getScrollProgress(v, event);

        if (mBehavior == ALWAYS_SHOW_INDEX || mBehavior == SHOW_INDEX_IN_NEED) {
            //INDEX
            showOrHideHandle(event);
            if (mIsTouching) {
                mIndexBar.setVisitable(true);
            }
            int section = getTouchingSection(event.getY());
            scrollRecyclerViewTo(section, true);
        } else {
            //SIMPLE
            showOrHideHandle(event);
            scrollRecyclerViewTo(scrollProgress, true);
        }
        moveHandleToPosition(scrollProgress);
        return true;
    }

    /**
     * in onTouch(), we must calculate out which section the user is touching, according to event.y.
     * @param y y from event.
     * @return user's touching section.
     */
    protected int getTouchingSection(float y) {
        return mIndexBar.getSectionIndex(y);
    }

    private int getPositionFromSection(int section) {
        if (mAlphabetIndexer != null) {
            return mAlphabetIndexer.getPositionForSection(section);
        } else {
            mAlphabetIndexer = new AlphabetIndexer(new IndexCursor(getData()), 0, getIndexBarKeyword());
            return mAlphabetIndexer.getPositionForSection(section);
        }
    }

    /**
     * get keyword from indexbar.
     * @return keyword
     */
    private CharSequence getIndexBarKeyword() {
        return mIndexBar.getKeyword();
    }

    private void scrollRecyclerViewTo(int section, boolean fromTouch) {
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

    /**
     * scroll recyclerView to position according to scrollProgress.
     * @param scrollProgress the progress of the scroll expressed as a fraction from [0, 1]
     * @param fromTouch true if this scroll request was triggered by a touch
     */
    @Override
    public void scrollRecyclerViewTo(float scrollProgress, boolean fromTouch) {
        int position = getPositionFromScrollProgress(scrollProgress);
        if (!(mRecyclerView.getLayoutManager() instanceof LinearLayoutManager)) {
            Log.e(TAG, "recyclerView's layout manager isn't a LinearLayoutManager, not Supported!");
            return;
        }

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        linearLayoutManager.scrollToPositionWithOffset(position, 0);

//            the method below doesn't scroll item to the top! so delete it, and using linearLayoutManager.scrollToPositionWithOffset instead.
//            mRecyclerView.scrollToPosition(position);
        if (fromTouch) {
            updateSectionIndicatorByPosition(position);
        }
    }

    /**
     * move handle to position according to scrollProgress
     */
    protected abstract void moveHandleToPosition(float scrollProgress);

    /**
     *
     * @param view
     * @param event
     * @return
     */
    protected float getScrollProgress(View view,MotionEvent event){
        ScrollProgressCalculator scrollProgressCalculator = getScrollProgressCalculator();
        if (scrollProgressCalculator != null) {
            return getScrollProgressCalculator().calculateScrollProgress(view, event);
        }
        return 0;
    }

    protected abstract int getLayoutResourceId();

    protected void showOrHideHandle(MotionEvent event){
        if (mBouncyHandle == null) {
            return;
        }
        Log.d(TAG, "ON touch and the y is :" + String.valueOf(event.getY()));

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (mBehavior != SIMPLE) {
                    mBouncyHandle.showHandle();
                    if (mBehavior == SIMPLE_WITH_INDICATOR) {
                        mThumb.hide(0);
                    }
                } else {
                    mThumb.show(0);
                }
                return;
            case MotionEvent.ACTION_UP:
                if (mBehavior != SIMPLE) {
                    mBouncyHandle.hideHandle();
                    if (mBehavior == SIMPLE_WITH_INDICATOR) {
                        mThumb.show(0);
                    }
                }
        }
    }
}
