package com.microstudent.app.surfaceviewapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.microstudent.app.bouncyfastscroller.bouncyhandle.BouncyHandle;
import com.microstudent.app.bouncyfastscroller.bouncyhandle.BouncyHandleImpl;
import com.microstudent.app.bouncyfastscroller.indexbar.VerticalIndexBar;
import com.microstudent.app.bouncyfastscroller.utils.DensityUtil;


public class MainActivity extends AppCompatActivity {
    private BouncyHandle bouncyHandle;
    private VerticalIndexBar indexBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bouncyHandle = (BouncyHandle) findViewById(R.id.bh);
        indexBar = (VerticalIndexBar) findViewById(R.id.indexBar);
    }

    public void startAnim(View view) {
        if (bouncyHandle != null) {
            bouncyHandle.showIndicator();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        BouncyHandleImpl bouncyHandle1 = (BouncyHandleImpl) bouncyHandle;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                bouncyHandle.showIndicator();
                bouncyHandle1.setY(event.getRawY() - DensityUtil.dip2px(getApplicationContext(), 170));
                break;
            case MotionEvent.ACTION_MOVE:
                bouncyHandle1.setY(event.getRawY() - DensityUtil.dip2px(getApplicationContext(), 170));
                break;
            case MotionEvent.ACTION_UP:
                bouncyHandle.hideIndicator();
                break;
        }
        return true;
    }

    public void changeWord(View view) {
        if (bouncyHandle != null) {
            bouncyHandle.setHintWord("zuile");
        }
    }

    public void reverse(View view) {
        bouncyHandle.hideIndicator();
    }
}
