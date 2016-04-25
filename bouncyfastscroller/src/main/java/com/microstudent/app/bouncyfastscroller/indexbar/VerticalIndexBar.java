package com.microstudent.app.bouncyfastscroller.indexbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.microstudent.app.bouncyfastscroller.R;
import com.microstudent.app.bouncyfastscroller.utils.DensityUtil;

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

    private int mMinTouchingY, mMaxTouchingY, mDividesOffset;

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

        int divides = (DEFAULT_INDEX_SET.length + 1) * 2;
        mMinTouchingY = mDividesOffset = getHeight() / divides;
        mMaxTouchingY = getHeight() - mDividesOffset;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int offsetX = canvas.getHeight() / (mIndexSet.length + 1);
        float offsetY = mTextPaint.getFontMetrics().descent;
        for (int i = 0; i < mIndexSet.length; i++) {
            canvas.drawText(mIndexSet, i, 1, canvas.getWidth() / 2, (i + 1) * offsetX + offsetY, mTextPaint);
//            for debug only
//            canvas.drawLine(0, i * 2 * mDividesOffset+mDividesOffset, getWidth(), i * 2 * mDividesOffset+mDividesOffset, mTextPaint);
//            canvas.drawLine(0, mMinTouchingY, getWidth(), mMinTouchingY, mTextPaint);
//            canvas.drawLine(0, mMaxTouchingY, getWidth(), mMaxTouchingY, mTextPaint);
//            canvas.drawLine(0, 0, getWidth(), 0, mTextPaint);
        }
    }

    @Override
    public void showIndexBar() {
        animate().alpha(1f).setStartDelay(0).start();
        Log.d(TAG, "alpha in");
    }


    @Override
    public void setVisitable(boolean visibility) {
        if (visibility) {
            setAlpha(1f);
            mTextPaint.setAlpha(255);
        } else {
            setImageAlpha(0);
            mTextPaint.setAlpha(0);
        }
        invalidate();
    }

    @Override
    public int getSectionIndex(float y) {
        Log.d(TAG, "receive Y = " + String.valueOf(y) + "mMin = " + String.valueOf(mMinTouchingY) + "Offset = " + String.valueOf(mDividesOffset));
        if (y < mMinTouchingY) {
            return 0;
        } else if (y > mMaxTouchingY) {
            return DEFAULT_INDEX_SET.length - 1;
        }
        int result = (int) ((y - mDividesOffset) / (2 * mDividesOffset));
        if (result >= DEFAULT_INDEX_SET.length) {
            return DEFAULT_INDEX_SET.length - 1;
        } else {
            return result;
        }
    }

    @Override
    public void hideIndexBar(long AlphaOutDelay) {
        animate().alpha(0).setStartDelay(AlphaOutDelay).start();
        Log.d(TAG, "alpha out");
    }

    public void hideIndexBar() {
        hideIndexBar(0);
    }

}
