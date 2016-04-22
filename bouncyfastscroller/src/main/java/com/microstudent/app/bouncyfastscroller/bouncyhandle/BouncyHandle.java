package com.microstudent.app.bouncyfastscroller.bouncyhandle;

import android.view.View;

/**
 * a handle in the fast scroller.
 * Created by MicroStudent on 2016/4/14.
 */
public interface BouncyHandle {
    //set the word that will be shown in the indicator. and only the first char will be chose.
    public void setHintWord(String s);

    //show  Indicator
    public void showHandle();

    //hide Indicator
    public void hideHandle();

    public void setIndicatorVisibility(int visibility);

    //set hint color.
    public void setHintColor(int Color);
}
