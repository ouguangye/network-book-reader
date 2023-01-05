package com.example.networkbookreader.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.networkbookreader.ui.home.HomeFragment;
import com.lwy.paginationlib.PaginationRecycleView;
import com.lwy.paginationlib.ViewHolder;

import org.json.JSONObject;

public class PageAdapter extends PaginationRecycleView.Adapter<JSONObject, ViewHolder> {

    private Context mContext;

    public PageAdapter(Context context, int dataTotalCount) {
        super(dataTotalCount);
        mContext = context;
    }


    @Override
    public void bindViewHolder(ViewHolder viewholder, JSONObject data) {

    }


    @Override
    public ViewHolder createViewHolder(@NonNull ViewGroup parent, int viewTypea) {
        return null;
    }
}
