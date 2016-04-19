package com.microstudent.app.bouncyfastscroller.bouncyhandle.helper;

import android.graphics.Path;

/**
 *
 * Created by MicroStudent on 2016/4/18.
 */
public final class DrawContent {
    private Path mPath;

    private String mWord = "";
    private float baseX = 0;
    private float baseY = 0;
    private float alpha = 0;

    public DrawContent() {
        mPath = new Path();
    }


    public void setWord(String word, float baseX, float baseY, float alpha) {
        if (word != null && !word.isEmpty()) {
            this.mWord = word;
            this.baseX = baseX;
            this.baseY = baseY;
            this.alpha = alpha;
        }
    }

    public void addPath(Path path) {
        if (path != null) {
            mPath.op(path, Path.Op.UNION);
        }
    }

    public void reset() {
        mPath.reset();
    }

    public void addCircle(int x,int y,float mRadius) {
        mPath.addCircle(x, y, mRadius, Path.Direction.CCW);
    }

    public Path getPath() {
        return mPath;
    }

    public String getWord() {
        return mWord;
    }

    public float getBaseX() {
        return baseX;
    }

    public float getBaseY() {
        return baseY;
    }

    public float getAlpha() {
        return alpha;
    }
}

