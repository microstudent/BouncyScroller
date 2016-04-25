package com.microstudent.app.bouncyfastscroller.indexbar;


import android.view.View;

/**
 *
 * Created by MicroStudent on 2016/4/19.
 */
public interface IndexBar {

    void showIndexBar();

    void hideIndexBar();

    void setVisitable(boolean visitable);

    void setOnTouchListener(View.OnTouchListener listener);

    int getSectionIndex(float y);

    void setAlpha(float alpha);
}
