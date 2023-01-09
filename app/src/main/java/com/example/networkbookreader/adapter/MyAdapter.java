package com.example.networkbookreader.adapter;


import android.content.Context;
import android.widget.BaseAdapter;

import java.util.List;

public abstract class MyAdapter extends BaseAdapter {
    protected List list;
    protected Context mContext;

    public MyAdapter(Context mContext, List list) {
        this.list = list;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
}