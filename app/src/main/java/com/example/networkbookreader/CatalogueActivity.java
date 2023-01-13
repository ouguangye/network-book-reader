package com.example.networkbookreader;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.networkbookreader.adapter.ChapterItemAdapter;
import com.example.networkbookreader.db.BookInfoDatabase;
import com.example.networkbookreader.db.BookIntro;
import com.example.networkbookreader.util.NetworkRequestUtil;
import com.example.networkbookreader.vo.ChapterItem;

import java.util.ArrayList;

public class CatalogueActivity extends AppCompatActivity {
    private BookIntro bookIntro;
    private ArrayList<ChapterItem> chapter_list;
    private ProgressDialog dialog;
    private Button read_button;
    private BookInfoDatabase bookInfoDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue);

        bookInfoDatabase = BookInfoDatabase.getInstance(getApplicationContext());

        bookIntro = (BookIntro) getIntent().getSerializableExtra("book");

        Glide.with(getApplicationContext()).load(bookIntro.getImgUrl()).placeholder(R.drawable.no_cover)
                .diskCacheStrategy(DiskCacheStrategy.NONE).into((ImageView) findViewById(R.id.cover));

        TextView title = findViewById(R.id.title);
        title.setText(bookIntro.getName());

        TextView author = findViewById(R.id.author);
        author.setText(String.format("作者：%s", bookIntro.getAuthor()));

        Button add_button = findViewById(R.id.add_button);
        add_button.setOnClickListener(view ->{
            if(bookInfoDatabase.isDataExit(bookIntro)){
                Toast.makeText(getApplicationContext(), "该书籍已被添加进书架", Toast.LENGTH_SHORT).show();
                return;
            }
            bookInfoDatabase.getBookIntroDao().insertAll(bookIntro);
            Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
        });

        read_button = findViewById(R.id.read_button);
        read_button.setText(bookInfoDatabase.isRead(bookIntro)? "继续阅读":"开始阅读");

        NetworkRequestUtil networkRequestUtil = new NetworkRequestUtil().getInstance();
        networkRequestUtil.getResult(NetworkRequestUtil.request.CATALOGUE, bookIntro.getBookUrl());
        networkRequestUtil.setOnHttpUrlListener(new NetworkRequestUtil.OnHttpUrlListener() {
            @Override
            public void success(ArrayList<ChapterItem> chapter_list) {
                CatalogueActivity.this.chapter_list = chapter_list;
                ChapterItemAdapter chapterItemAdapter = new ChapterItemAdapter(getApplicationContext(), chapter_list);
                GridView gridView = findViewById(R.id.grid);
                gridView.setAdapter(chapterItemAdapter);
                gridView.setOnItemClickListener((adapterView, view, i, l) -> jumpToReadActivity(i));
                read_button.setOnClickListener(view -> {
                    bookIntro = bookInfoDatabase.getBookIntroDao().getDataFromName(bookIntro.getName());
                    jumpToReadActivity(bookIntro.getReadProgress());
                });
                if (dialog != null) dialog.dismiss();
            }

            @Override
            public void fail() {
                if (dialog != null) dialog.dismiss();
                AlertDialog alertDialog = new AlertDialog.Builder(CatalogueActivity.this)
                        .setMessage("加载失败，请检查网络，然后重新进入该页面试试").create();
                alertDialog.show();
            }
        });
        dialog = ProgressDialog.show(CatalogueActivity.this, "", "加载中", true);
        dialog.show();
    }

    private void jumpToReadActivity(int i) {
        Intent intent = new Intent(getApplicationContext(), ReadActivity.class);
        intent.putExtra("i",i);
        intent.putExtra("name", bookIntro.getName());
        intent.putParcelableArrayListExtra("list", chapter_list);
        startActivity(intent);
    }
}