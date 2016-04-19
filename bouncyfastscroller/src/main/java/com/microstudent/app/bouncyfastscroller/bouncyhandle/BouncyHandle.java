package com.microstudent.app.bouncyfastscroller.bouncyhandle;

/**
 * a handle in the fast scroller.
 * Created by MicroStudent on 2016/4/14.
 */
public interface BouncyHandle {
    //set the word that will be shown in the indicator. and only the first char will be chose.
    public void setHintWord(String s);

    //show  Indicator
    public void showIndicator();

    //hide Indicator
    public void hideIndicator();

    //set hint color.
    public void setHintColor(int Color);
}
