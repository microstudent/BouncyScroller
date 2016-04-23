package com.microstudent.app.surfaceviewapplication;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 *
 * Created by MicroStudent on 2016/4/21.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private LayoutInflater mInflater;

    private ArrayList<String> mData;

    public static final String alphas = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public MyAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mData = new ArrayList<>();
        initData();
    }

    private void initData() {
        mData.add("000");
        mData.add("123");
        mData.add("456");
        mData.add("♂");
        for(char c:alphas.toCharArray()){
            for(int i=0; i<10; i++){
                mData.add(c + String.valueOf(i));
            }
        }
    }

    public ArrayList<String> getData() {
        return mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_simple, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textView.setText(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv);
        }
    }
}

