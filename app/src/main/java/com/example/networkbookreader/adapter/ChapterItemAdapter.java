package com.example.networkbookreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;

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

        // 手动适配 暗色模式
        if (mContext.getResources().getConfiguration().uiMode == 0x11) {
           viewHolder.name.setTextColor(mContext.getResources().getColor(R.color.black));
        } else {
            viewHolder.name.setTextColor(mContext.getResources().getColor(R.color.white));
        }

        return view;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    static class ViewHolder {
        TextView name;
    }
}
