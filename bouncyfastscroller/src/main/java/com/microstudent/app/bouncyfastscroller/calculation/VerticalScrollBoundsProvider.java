package com.microstudent.app.bouncyfastscroller.calculation;

/**
 * Created by MicroStudent on 2016/4/19.
 */
public class VerticalScrollBoundsProvider {
    private final float mMinimumScrollY;
    private final float mMaximumScrollY;

    public VerticalScrollBoundsProvider(float minimumScrollY, float maximumScrollY) {
        mMinimumScrollY = minimumScrollY;
        mMaximumScrollY = maximumScrollY;
    }

    public float getMinimumScrollY() {
        return mMinimumScrollY;
    }

    public float getMaximumScrollY() {
        return mMaximumScrollY;
    }

    public float getLength() {
        return mMaximumScrollY - mMinimumScrollY;
    }
}
