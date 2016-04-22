package com.microstudent.app.bouncyfastscroller.indexbar;


import android.view.View;

/**
 *
 * Created by MicroStudent on 2016/4/19.
 */
public interface IndexBar {

    public void showIndexBar();

    public void hideIndexBar();

    public void setOnTouchListener(View.OnTouchListener listener);
}
