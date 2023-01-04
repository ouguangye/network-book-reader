package com.example.networkbookreader;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dyhdyh.widget.loading.bar.LoadingBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private List<BookIntro> result_list;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // listView绑定点击事件
        listView = findViewById(R.id.book_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        BookIntro bookIntro = (BookIntro) adapterView.getAdapter().getItem(i);
                        Intent intent = new Intent(MainActivity.this, CatalogueActivity.class);
                        intent.putExtra("book", bookIntro);
                        startActivity(intent);
                    }
                }
        );

        // 搜索事件
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        searchBook(s);
                        searchView.clearFocus();
                        LoadingBar.make(listView).show();
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {
                        return false;
                    }
                }
        );
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                BookIntroAdapter bookIntroAdapter = new BookIntroAdapter(getApplicationContext(), result_list);
                listView.setAdapter(bookIntroAdapter);
                LoadingBar.cancel(listView);
            }
        }
    };

    private void searchBook(String bookname) {
        new Thread(() -> {
            try {
                String url  = "https://www.xxbiqu.com/search/" + bookname + "/?type=all";
                Document doc = Jsoup.connect(url).get();
                Elements search_element_list = doc.select("li");
                result_list = new ArrayList<>();
                for (Element i : search_element_list) {
                    String name = i.selectFirst("h3").text();
                    String detail = i.selectFirst(".searchresult_p").text().trim();
                    String type = i.selectFirst("span").text();
                    String bookUrl = i.selectFirst("a").attr("href");
                    String imgUrl = i.selectFirst("a img").attr("data-original");
                    String author = i.selectFirst("p").text().split(" ")[0];
                    BookIntro bookIntro = new BookIntro(name, type,detail, author,imgUrl,bookUrl);
                    result_list.add(bookIntro);
                }

                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            } catch (IOException e) {
                Log.d("title","请求失败");
                e.printStackTrace();
            }
        }).start();
    }
}