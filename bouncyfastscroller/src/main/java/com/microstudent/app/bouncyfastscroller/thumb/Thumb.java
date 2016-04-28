package com.microstudent.app.bouncyfastscroller.thumb;

import android.view.View;

import com.microstudent.app.bouncyfastscroller.AbsRecyclerViewScroller;

/**
 *
 * Created by MicroStudent on 2016/4/28.
 */
public interface Thumb {
    void setColor(int color);

    void setAlpha(float alpha);

    void show(long delay);

    void hide(long delay);

    void setY(float y);

    void setVisible(boolean visible);

    int getHeight();

    void setOnTouchListener(View.OnTouchListener listener);
}
