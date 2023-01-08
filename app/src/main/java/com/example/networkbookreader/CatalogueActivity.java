package com.example.networkbookreader;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dyhdyh.widget.loadingbar.LoadingBar;
import com.example.networkbookreader.db.BookInfoDatabase;
import com.example.networkbookreader.db.BookIntro;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CatalogueActivity extends AppCompatActivity {
    private BookIntro bookIntro;
    private ArrayList<HashMap<String,String>> chapter_list;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogue);

        bookIntro = (BookIntro) getIntent().getSerializableExtra("book");

        Glide.with(getApplicationContext()).load(bookIntro.getImgUrl()).placeholder(R.drawable.no_cover)
                .diskCacheStrategy(DiskCacheStrategy.NONE).into((ImageView) findViewById(R.id.cover));

        TextView title = findViewById(R.id.title);
        title.setText(bookIntro.getName());

        TextView author = findViewById(R.id.author);
        author.setText("作者：" + bookIntro.getAuthor());

        Button add_button = findViewById(R.id.add_button);
        add_button.setOnClickListener(view ->{
            BookInfoDatabase bookInfoDatabase = BookInfoDatabase.getInstance(getApplicationContext());
            if(bookInfoDatabase.isDataExit(bookIntro)){
                Toast.makeText(getApplicationContext(), "该书籍已被添加进书架", Toast.LENGTH_SHORT).show();
                return;
            }
            bookInfoDatabase.getBookIntroDao().insertAll(bookIntro);
            Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_SHORT).show();
        });

        getCatalogue();
        LoadingBar.show(findViewById(R.id.grid));
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                SimpleAdapter myAdapter=new SimpleAdapter(getApplicationContext(),chapter_list,R.layout.chaper_text_item,new String[]{"name"},new int[]{R.id.chapterText});
                GridView gridView = findViewById(R.id.grid);
                gridView.setAdapter(myAdapter);
                gridView.setOnItemClickListener((adapterView, view, i, l) -> jumpToReadActivity((HashMap<String, String>) adapterView.getAdapter().getItem(i)));
                Button read_button = findViewById(R.id.read_button);
                read_button.setOnClickListener(view -> jumpToReadActivity(chapter_list.get(0)));
                LoadingBar.cancel(findViewById(R.id.grid));
            }
        }
    };

    private void jumpToReadActivity(HashMap<String,String> hashMap) {
        Intent intent = new Intent(getApplicationContext(), ReadActivity.class);
        intent.putExtra("href", hashMap.get("href"));
        startActivity(intent);
    }

    private void getCatalogue() {
        new Thread(()->{
            try {
                Document doc = Jsoup.connect(bookIntro.getBookUrl()).get();
                Elements result_list = doc.select("#ul_all_chapters li a");
                chapter_list = new ArrayList<>();
                for(Element i : result_list) {
                    HashMap<String,String> hashMap = new HashMap<>();
                    hashMap.put("href",i.attr("href"));
                    hashMap.put("name",i.text().trim());
                    chapter_list.add(hashMap);
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