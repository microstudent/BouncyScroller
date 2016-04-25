package com.microstudent.app.surfaceviewapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.microstudent.app.bouncyfastscroller.RecyclerViewScroller;
import com.microstudent.app.bouncyfastscroller.bouncyhandle.BouncyHandle;
import com.microstudent.app.bouncyfastscroller.bouncyhandle.BouncyHandleImpl;
import com.microstudent.app.bouncyfastscroller.indexbar.VerticalIndexBar;
import com.microstudent.app.bouncyfastscroller.utils.DensityUtil;
import com.microstudent.app.bouncyfastscroller.vertical.VerticalBouncyFastScroller;


public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private VerticalBouncyFastScroller verticalBouncyFastScroller;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView) findViewById(R.id.rv);
        MyAdapter adapter = new MyAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));


        verticalBouncyFastScroller = (VerticalBouncyFastScroller) findViewById(R.id.vbfs);
        assert verticalBouncyFastScroller != null;
        verticalBouncyFastScroller.setRecyclerView(recyclerView);
        verticalBouncyFastScroller.setData(adapter.getData());
    }
}
