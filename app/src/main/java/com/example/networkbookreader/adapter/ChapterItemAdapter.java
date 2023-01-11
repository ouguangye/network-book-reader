package com.example.networkbookreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.networkbookreader.R;
import com.example.networkbookreader.vo.ChapterItem;

import java.util.List;

public class ChapterItemAdapter extends MyAdapter{
    private int textSize = 0;

    public ChapterItemAdapter(Context mContext, List<ChapterItem> list) {
        super(mContext, list);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.chapter_text_item, viewGroup, false);
            viewHolder.name = view.findViewById(R.id.chapterText);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }

        ChapterItem chapterItem = (ChapterItem) list.get(i);
        viewHolder.name.setText(chapterItem.getName());
        if (textSize != 0)  viewHolder.name.setTextSize(textSize);
        return view;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    static class ViewHolder {
        TextView name;
    }
}
