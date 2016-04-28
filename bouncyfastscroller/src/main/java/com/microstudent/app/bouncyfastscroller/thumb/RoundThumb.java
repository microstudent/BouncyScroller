package com.microstudent.app.bouncyfastscroller.thumb;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.microstudent.app.bouncyfastscroller.R;
import com.microstudent.app.bouncyfastscroller.utils.DensityUtil;

/**
 *
 * Created by MicroStudent on 2016/4/28.
 */
public class RoundThumb extends View implements Thumb {
    private final int DEFAULT_HEIGHT = DensityUtil.dip2px(getContext(), 50);
    private final int DEFAULT_WIDTH = DensityUtil.dip2px(getContext(), 10);

    public RoundThumb(Context context) {
        this(context, null);
    }

    public RoundThumb(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundThumb(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        animate().setDuration(100);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int measuredWidth = DEFAULT_WIDTH, measuredHeight = DEFAULT_HEIGHT;
        if (widthMode == MeasureSpec.EXACTLY) {
            measuredWidth = widthSize;
        }
        if (heightMode == MeasureSpec.EXACTLY){
            measuredHeight = heightSize;
        }
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    public void setColor(int color) {
        if (getBackground() instanceof GradientDrawable) {
            GradientDrawable drawable = (GradientDrawable) getBackground();
            drawable.setColor(color);
        } else super.setBackgroundColor(color);
    }

    @Override
    public void show(long delay) {
        animate().setStartDelay(delay).scaleX(1).start();
    }

    public void show() {
        show(0);
    }

    public void hide(long delay) {
        animate().setStartDelay(delay).scaleX(0).start();
    }

    public void hide() {
        hide(0);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            setVisibility(VISIBLE);
        } else setVisibility(GONE);
    }
}
