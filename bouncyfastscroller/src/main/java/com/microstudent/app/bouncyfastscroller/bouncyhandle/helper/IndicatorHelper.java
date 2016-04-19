package com.microstudent.app.bouncyfastscroller.bouncyhandle.helper;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.animation.DecelerateInterpolator;

import com.microstudent.app.bouncyfastscroller.utils.DensityUtil;


/**
 *
 * Created by MicroStudent on 2016/4/14.
 */
public class IndicatorHelper implements ValueAnimator.AnimatorUpdateListener{
    public static final String TAG = "IndicatorHelper";
    //默认的动画时长
    private static final long DEFAULT_ANIM_DURATION = 500;

    private final Callback mCallback;
    //the width and height for the view that helper for.
    private int mWidth, mHeight;

    //round's radius
    private int mRadius;
    //hint word
    private String mHintWord;

    private Paint.FontMetrics mFontMetrics;
    //center Point.
    private Point mCenterPoint;
    //Animation
    private ValueAnimator mMovingAnimator, mAlphaInAnimator;

    private DrawContent mDrawContent;


    public IndicatorHelper(int width, int height, Context context, Callback callback) {
        mCallback = callback;
        mWidth = width;
        mHeight = height;

        int defaultRadius = DensityUtil.dip2px(context, 60) / 2;
        mCenterPoint = new Point(defaultRadius, height / 2);
        mRadius = defaultRadius;

        mMovingAnimator = ValueAnimator.ofInt(width + defaultRadius, defaultRadius);
        mAlphaInAnimator = ValueAnimator.ofFloat(0f, 1f);

        mMovingAnimator.addUpdateListener(this);
        mAlphaInAnimator.addUpdateListener(this);

        mMovingAnimator.setInterpolator(new DecelerateInterpolator());
        mAlphaInAnimator.setInterpolator(new DecelerateInterpolator());

        mMovingAnimator.setDuration(DEFAULT_ANIM_DURATION);
        mAlphaInAnimator.setDuration(DEFAULT_ANIM_DURATION);

        mDrawContent = new DrawContent();
    }

    public void startAnimation() {
        if (mAlphaInAnimator != null && mMovingAnimator != null) {
            mMovingAnimator.start();
            mAlphaInAnimator.start();
        }
    }


    public void reverseAnimation() {
        if (mMovingAnimator != null) {
            mMovingAnimator.reverse();
            mAlphaInAnimator.reverse();
        }
    }

    public DrawContent getDrawContent() {
        return mDrawContent;
    }

    private void drawHintWord() {
        if (mHintWord != null && !mHintWord.isEmpty()) {
            float baseY = getBaseY(mFontMetrics);

            mDrawContent.setWord(mHintWord.substring(0, 1), mCenterPoint.x, baseY,
                    (Float) mAlphaInAnimator.getAnimatedValue());
        }
    }

    public void setHintWord(String text, Paint.FontMetrics fontMetrics) {
        mHintWord = text;
        mFontMetrics = fontMetrics;
        drawHintWord();

        mCallback.onAnimUpdate(mAlphaInAnimator);
    }

    private float getBaseY(Paint.FontMetrics fontMetrics) {
        //计算Y基准线
        return mCenterPoint.y + (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.bottom;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        if (animation == mAlphaInAnimator) {
            drawHintWord();
        } else if (animation == mMovingAnimator) {
            mCenterPoint.x = (int) mMovingAnimator.getAnimatedValue();
            mDrawContent.reset();
            mDrawContent.addCircle(mCenterPoint.x, mCenterPoint.y, mRadius);
        }
        mCallback.onAnimUpdate(animation);
    }

    public void setDuration(long duration) {
        if (mAlphaInAnimator != null) {
            mAlphaInAnimator.setDuration(duration);
            mMovingAnimator.setDuration(duration);
        }
    }

    public ValueAnimator getAlphaInAnimator() {
        return mAlphaInAnimator;
    }

    public ValueAnimator getMovingAnimator() {
        return mMovingAnimator;
    }
}
