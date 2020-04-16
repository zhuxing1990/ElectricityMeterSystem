package com.vunke.electricity.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zhuxi on 2019/9/24.
 */

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.TestHolder> {

    TestAdapter(){

    }
    @Override
    public TestHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(TestHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
    class TestHolder extends RecyclerView.ViewHolder{

        public TestHolder(View itemView) {
            super(itemView);
        }
    }
}
