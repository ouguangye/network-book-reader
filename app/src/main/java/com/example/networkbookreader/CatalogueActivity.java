package com.example.networkbookreader;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.networkbookreader.adapter.ChapterItemAdapter;
import com.example.networkbookreader.db.BookInfoDatabase;
import com.example.networkbookreader.db.BookIntro;
import com.example.networkbookreader.vo.ChapterItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        getCatalogue();
        dialog = ProgressDialog.show(CatalogueActivity.this, "", "加载中", true);
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
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
        }
    };

    private void jumpToReadActivity(int i) {
        Intent intent = new Intent(getApplicationContext(), ReadActivity.class);
        intent.putExtra("i",i);
        intent.putExtra("name", bookIntro.getName());
        intent.putParcelableArrayListExtra("list", chapter_list);
        startActivity(intent);
    }


    private void getCatalogue() {
        new Thread(()->{
            try {
                Document doc = Jsoup.connect(bookIntro.getBookUrl()).get();
                Elements result_list = doc.select("#ul_all_chapters li a");
                chapter_list = new ArrayList<>();
                for(Element i : result_list) {
                    chapter_list.add(new ChapterItem(i.attr("href"), i.text().trim()));
                }
                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}