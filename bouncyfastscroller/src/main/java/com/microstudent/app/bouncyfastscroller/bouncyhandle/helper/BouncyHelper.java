package com.microstudent.app.bouncyfastscroller.bouncyhandle.helper;

import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Path;
import android.graphics.Point;
import android.util.Log;

import com.microstudent.app.bouncyfastscroller.utils.DensityUtil;

/**
 *
 * Created by MicroStudent on 2016/4/14.
 */
public class BouncyHelper {
    public static final String TAG = "BouncyHelper";
    //默认的动画时长
    private static final long DEFAULT_ANIM_DURATION = 500;

    /**
     * 动画的方向，分别为上下左右
     */
    public static final int EDGE_SIDE_TOP = 1;
    public static final int EDGE_SIDE_RIGHT = 2;
    public static final int EDGE_SIDE_LEFT = 3;
    public static final int EDGE_SIDE_BOTTOM = 4;

    private final Callback mCallback;

    //动画的方向，默认为右方向
    private int mEdgeDirection;

    //起止点
    private Point mStartPoint, mEndPoint;
    //
    private Point mStartControlPoint, mEndControlPoint, mMidControlPointL, mMidControlPointR;
    //带动动画的主要控制点
    private Point mMidPoint;
    //中间的点的高度变化动画
    private ObjectAnimator mMidPointAnimator;

    //the width and height for the view that helper for.
    private int mWidth, mHeight;


    //动画最高能到达的高度,默认为宽/高
    private int mMaxHeight;

    private Path mPath;

    public BouncyHelper(int width, int height, int direction, Context context, Callback callback) {
        mCallback = callback;

        mWidth = width;
        mHeight = height;

        mEdgeDirection = direction;
        mMaxHeight = DensityUtil.dip2px(context, 40);

        mPath = new Path();

        initPoint();
        initAnimator();

    }

    private void initAnimator() {
        mMidPointAnimator = ObjectAnimator.ofObject(this, "MidPointHeight", new IntEvaluator(), 0, mMaxHeight);
        mMidPointAnimator.setDuration(DEFAULT_ANIM_DURATION);
    }

    private void initPoint() {
        switch (mEdgeDirection) {
            case EDGE_SIDE_BOTTOM:
                Log.d(TAG, "bottom");
                mStartPoint = new Point(0, mHeight);
                mStartControlPoint = new Point((int) (0.25 * mWidth), mHeight);
                mMidPoint = new Point(mWidth / 2, mHeight);
                mMidControlPointL = new Point((int) (0.25 * mWidth), mHeight);
                mMidControlPointR = new Point((int) (0.75 * mWidth), mHeight);
                mEndPoint = new Point(mWidth, mHeight);
                mEndControlPoint = new Point((int) (0.75 * mWidth), mHeight);
                break;
            case EDGE_SIDE_LEFT:
                Log.d(TAG, "left");
                mStartPoint = new Point(0, 0);
                mStartControlPoint = new Point(0, (int) (0.25 * mHeight));
                mMidPoint = new Point(0, mHeight / 2);
                mMidControlPointL = new Point(0, (int) (0.25 * mHeight));
                mMidControlPointR = new Point(0, (int) (0.75 * mHeight));
                mEndPoint = new Point(0, mHeight);
                mEndControlPoint = new Point(0, (int) (0.75 * mHeight));
                break;
            case EDGE_SIDE_TOP:
                Log.d(TAG, "top");
                mStartPoint = new Point(mWidth, 0);
                mStartControlPoint = new Point((int) (0.75 * mWidth), 0);
                mMidPoint = new Point(mWidth / 2, 0);
                mMidControlPointL = new Point((int) (0.75 * mWidth), 0);
                mMidControlPointR = new Point((int) (0.25 * mWidth), 0);
                mEndPoint = new Point(0, 0);
                mEndControlPoint = new Point((int) (0.25 * mWidth), 0);
                break;
            case EDGE_SIDE_RIGHT:
            default:
                Log.d(TAG, "right");
                mStartPoint = new Point(mWidth, mHeight);
                mStartControlPoint = new Point(mWidth, (int) (0.75 * mHeight));
                mMidPoint = new Point(mWidth, mHeight / 2);
                mMidControlPointL = new Point(mWidth, (int) (0.75 * mHeight));
                mMidControlPointR = new Point(mWidth, (int) (0.25 * mHeight));
                mEndPoint = new Point(mWidth, 0);
                mEndControlPoint = new Point(mWidth, (int) (0.25 * mHeight));
                break;
        }
    }

    private void setupDrawContent() {
        mPath.reset();

        mPath.moveTo(mStartPoint.x, mStartPoint.y);
        mPath.cubicTo(mStartControlPoint.x, mStartControlPoint.y, mMidControlPointL.x, mMidControlPointL.y,
                mMidPoint.x, mMidPoint.y);
        mPath.cubicTo(mMidControlPointR.x, mMidControlPointR.y, mEndControlPoint.x, mEndControlPoint.y,
                mEndPoint.x, mEndPoint.y);
        mPath.lineTo(mStartPoint.x,mStartPoint.y);
    }

    public Path getPath() {
        return mPath;
    }

    /**
     * 设置中间点的高度
     * @param height height
     */
    public void setMidPointHeight(int height) {
        if (mMidPoint != null) {
            switch (mEdgeDirection) {
                case EDGE_SIDE_BOTTOM:
                    mMidControlPointL.y = mMidControlPointR.y = mMidPoint.y = mHeight - height;
                    break;
                case EDGE_SIDE_LEFT:
                    mMidControlPointL.x = mMidControlPointR.x = mMidPoint.x = height;
                    break;
                case EDGE_SIDE_TOP:
                    mMidControlPointL.y = mMidControlPointR.y = mMidPoint.y = height;
                    break;
                case EDGE_SIDE_RIGHT:
                default:
                    mMidControlPointL.x = mMidControlPointR.x = mMidPoint.x = mWidth - height;
                    break;
            }
            setupDrawContent();
            mCallback.onAnimUpdate(mMidPointAnimator);
        }
    }

    /**
     * 获得中间点的高度
     */
    public int getMidPointHeight() {
        if (mMidPoint != null) {
            switch (mEdgeDirection) {
                case EDGE_SIDE_BOTTOM:
                case EDGE_SIDE_TOP:
                    return Math.abs(mMidPoint.y - mStartPoint.y);
                case EDGE_SIDE_RIGHT:
                case EDGE_SIDE_LEFT:
                default:
                    return Math.abs(mMidPoint.x - mStartPoint.x);
            }
        }
        return 0;
    }

    public void startAnimation() {
        if (mMidPointAnimator != null) {
            mMidPointAnimator.start();
        }
    }

    public void reverseAnimation() {
        if (mMidPointAnimator != null) {
            mMidPointAnimator.reverse();
        }
    }

    public void setDuration(long duration) {
        if (mMidPointAnimator != null) {
            mMidPointAnimator.setDuration(duration);
        }
    }

    public ObjectAnimator getMidPointAnimator() {
        return mMidPointAnimator;
    }
}
