package com.microstudent.app.bouncyfastscroller.indexbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.microstudent.app.bouncyfastscroller.R;
import com.microstudent.app.bouncyfastscroller.utils.DensityUtil;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Created by MicroStudent on 2016/4/19.
 */
public class VerticalIndexBar extends ImageView implements IndexBar{

    public final static char[] DEFAULT_INDEX_SET = {
            '#', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I',
            'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    private static final float DEFAULT_TEXT_SIZE = 40f;

    private static final int DEFAULT_BACKGROUND = R.drawable.bg_index_bar;

    private static final String TAG = "VerticalIndexBar";

    private final int DEFAULT_WIDTH = DensityUtil.dip2px(getContext(), 20);

    private char[] mIndexSet = DEFAULT_INDEX_SET;

    private TextPaint mTextPaint;

    public VerticalIndexBar(Context context) {
        this(context, null);
    }

    public VerticalIndexBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalIndexBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray array = context.obtainStyledAttributes(R.styleable.VerticalIndexBar);

        Drawable bg;

        try {
            bg = array.getDrawable(R.styleable.VerticalIndexBar_bg);
            if (bg != null) {
                setImageDrawable(bg);
            } else {
                setImageResource(DEFAULT_BACKGROUND);
            }
        }finally {
            array.recycle();
        }

        initView();

        setupView();
    }

    private void setupView() {
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(DEFAULT_TEXT_SIZE);
        mTextPaint.setColor(Color.DKGRAY);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    private void initView() {
        mTextPaint = new TextPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(Math.min(DEFAULT_WIDTH, widthSize), heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int offsetX = canvas.getHeight() / (mIndexSet.length + 1);
        float offsetY = mTextPaint.getFontMetrics().descent;
        for (int i = 0; i < mIndexSet.length; i++) {
            canvas.drawText(mIndexSet, i, 1, canvas.getWidth() / 2, (i + 1) * offsetX + offsetY, mTextPaint);
        }
    }

    @Override
    public void showIndexBar() {
        animate().alpha(1f).start();
        Log.d(TAG, "alpha in");
    }

    @Override
    public void hideIndexBar() {
        animate().alpha(0).start();
        Log.d(TAG, "alpha out");
    }

    @Override
    public void setVisitable(boolean visibility) {
        if (visibility) {
            setImageAlpha(255);
            mTextPaint.setAlpha(255);
        } else {
            setImageAlpha(0);
            mTextPaint.setAlpha(0);
        }
        invalidate();
    }

}
