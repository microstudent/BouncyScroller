package com.microstudent.app.bouncyfastscroller.bouncyhandle;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.microstudent.app.bouncyfastscroller.bouncyhandle.helper.BouncyHelper;
import com.microstudent.app.bouncyfastscroller.bouncyhandle.helper.Callback;
import com.microstudent.app.bouncyfastscroller.bouncyhandle.helper.DrawContent;
import com.microstudent.app.bouncyfastscroller.bouncyhandle.helper.IndicatorHelper;
import com.microstudent.app.bouncyfastscroller.calculation.VerticalScreenPositionCalculator;
import com.microstudent.app.bouncyfastscroller.calculation.VerticalScrollBoundsProvider;

/**
 * Created by MicroStudent on 2016/4/14.
 */
public class BouncyHandleImpl extends View implements BouncyHandle, Callback {

    private static final int DEFAULT_HINT_COLOR = 0xff669900;

    private static final int DEFAULT_TEXT_COLOR = Color.WHITE;

    private static final float DEFAULT_ALPHA = 0.6f;

    private static final int DEFAULT_DURATION = 250;

    private static final String TAG = "BouncyHandleImpl";

    //the paint
    private Paint mHintPaint, mTextPaint;

    private IndicatorHelper mIndicatorHelper;

    private BouncyHelper mBouncyHelper;

    private DrawContent mDrawContent;

    private boolean mIsIndicatorVisiable = true;

    public BouncyHandleImpl(Context context) {
        this(context, null);
    }

    public BouncyHandleImpl(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BouncyHandleImpl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        setupView();
    }

    private void setupView() {
        mHintPaint.setAntiAlias(true);
        mHintPaint.setColor(DEFAULT_HINT_COLOR);
        mHintPaint.setStyle(Paint.Style.FILL);

        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(DEFAULT_TEXT_COLOR);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setTextSize(100);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        setHintColor(DEFAULT_HINT_COLOR);
        setTextColor(DEFAULT_TEXT_COLOR);
        setAlpha(DEFAULT_ALPHA);
        setDuration(DEFAULT_DURATION);

        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    private void initView() {
        mHintPaint = new Paint();
        mTextPaint = new Paint();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (mIndicatorHelper == null) {
            mIndicatorHelper = new IndicatorHelper(getWidth(), getHeight(), getContext(), this);
            mBouncyHelper = new BouncyHelper(getWidth(), getHeight(), BouncyHelper.EDGE_SIDE_RIGHT, getContext(), this);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mDrawContent != null) {
            canvas.drawPath(mDrawContent.getPath(), mHintPaint);
            canvas.drawText(mDrawContent.getWord(), mDrawContent.getBaseX(), mDrawContent.getBaseY(), mTextPaint);
        }
    }


    @Override
    public void setHintWord(String s) {
        mIndicatorHelper.setHintWord(s, mTextPaint.getFontMetrics());
    }

    @Override
    public void showHandle() {
        if (mIndicatorHelper != null) {
            setHintWord("你他妈给我运行起来");

            if (mIsIndicatorVisiable) {
                mIndicatorHelper.startAnimation();
                mBouncyHelper.startAnimation();
            } else {
                mBouncyHelper.startAnimation();
            }
            Log.d(TAG, "start anim");
        }
    }


    @Override
    public void hideHandle() {
        if (mIndicatorHelper != null) {
            if (mIsIndicatorVisiable) {
                mIndicatorHelper.reverseAnimation();
                mBouncyHelper.reverseAnimation();
            } else {
                mBouncyHelper.reverseAnimation();
            }
        }
    }

    @Override
    public void setIndicatorVisibility(int visibility) {
        mIsIndicatorVisiable = visibility == VISIBLE;
    }

    @Override
    public void setHintColor(int color) {
        mHintPaint.setColor(color);
    }

    public void setTextColor(int color) {
        mTextPaint.setColor(color);
    }

    public void setAlpha(float alpha) {
        mHintPaint.setAlpha((int) (alpha * 255));
    }

    public void setDuration(long duration) {
        if (mBouncyHelper != null) {
            mBouncyHelper.setDuration(duration);
            mIndicatorHelper.setDuration(duration);
        }
    }

    @Override
    public void onAnimUpdate(Animator animation) {
        if (mBouncyHelper != null && animation == mBouncyHelper.getMidPointAnimator()) {
            if (mDrawContent != null) {
                mDrawContent.reset();
                mDrawContent.addPath(mBouncyHelper.getPath());
            }
        } else {
            mDrawContent = mIndicatorHelper.getDrawContent();
            mTextPaint.setAlpha((int) (mDrawContent.getAlpha() * 255));
        }
        invalidate();
    }
}
