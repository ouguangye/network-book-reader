package com.example.networkbookreader.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.networkbookreader.R;
import com.example.networkbookreader.db.BookIntro;

import java.util.List;

public class BookIntroAdapter extends MyAdapter {
    public BookIntroAdapter(Context mContext, List<BookIntro> list) {
        super(mContext, list);
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

        BookIntro bookIntro = (BookIntro) list.get(i);
        viewHolder.name.setText(bookIntro.getName());
        viewHolder.author.setText(bookIntro.getAuthor());
        viewHolder.detail.setText(bookIntro.getDetail());
        viewHolder.type.setText(bookIntro.getType());
        Glide.with(mContext).load(bookIntro.getImgUrl()).placeholder(R.drawable.no_cover)
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
