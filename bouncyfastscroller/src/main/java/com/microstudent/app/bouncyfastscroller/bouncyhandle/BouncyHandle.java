package com.microstudent.app.bouncyfastscroller.bouncyhandle;

import android.view.View;

/**
 * a handle in the fast scroller.
 * Created by MicroStudent on 2016/4/14.
 */
public interface BouncyHandle {
    //set the word that will be shown in the indicator. and only the first char will be chose.
    void setHintWord(String s);

    //show  Indicator
    void showHandle();

    //hide Indicator
    void hideHandle();

    void setIndicatorVisibility(int visibility);

    //set hint color.
    void setHintColor(int Color);

    void setAlpha(float alpha);
}
