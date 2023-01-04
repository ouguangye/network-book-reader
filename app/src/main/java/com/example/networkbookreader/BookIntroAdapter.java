package com.example.networkbookreader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class BookIntroAdapter extends BaseAdapter {
    private final List<BookIntro> list;
    private final Context mContext;

    public BookIntroAdapter(Context mContext, List<BookIntro> list) {
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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.book_intro, viewGroup, false);
            viewHolder.author = view.findViewById(R.id.author);
            viewHolder.detail = view.findViewById(R.id.detail);
            viewHolder.name = view.findViewById(R.id.name);
            viewHolder.cover = view.findViewById(R.id.cover);
            viewHolder.type = view.findViewById(R.id.type);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.name.setText(list.get(i).getName());
        viewHolder.author.setText(list.get(i).getAuthor());
        viewHolder.detail.setText(list.get(i).getDetail());
        viewHolder.type.setText(list.get(i).getType());
        Glide.with(mContext).load(list.get(i).getImgUrl()).placeholder(R.drawable.no_cover)
                .diskCacheStrategy(DiskCacheStrategy.NONE).into(viewHolder.cover);

        return view;
    }

    static class ViewHolder {
        ImageView cover;
        TextView name;
        TextView author;
        TextView type;
        TextView detail;
    }
}
