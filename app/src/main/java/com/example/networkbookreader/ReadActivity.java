package com.example.networkbookreader;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.networkbookreader.vo.ChapterItem;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class ReadActivity extends AppCompatActivity{
    private String content;
    private String title;
    private String preUrl;
    private String nextUrl;

    private BottomSheetDialog bottomSheetDialog;

    // 监听 scrollView的滑动大小
    private int lastX = 0;
    private int lastY = 0;

    // 监听 滑动条拖动是否完成
    private int lastProgress = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        String url = getIntent().getStringExtra("href");
        getContent(url);

        ArrayList<ChapterItem> chapter_list = getIntent().getParcelableArrayListExtra("list");

        bottomSheetDialog = new BottomSheetDialog(ReadActivity.this);
        @SuppressLint("InflateParams") View dialogView = LayoutInflater.from(ReadActivity.this).inflate(R.layout.read_book_dialog, null);
        bottomSheetDialog.setContentView(dialogView);

        // 按钮点击事件
        Button preButton = bottomSheetDialog.findViewById(R.id.pre_button);
        Button nextButton = bottomSheetDialog.findViewById(R.id.next_button);
        Objects.requireNonNull(preButton).setOnClickListener(view -> {
            getContent(preUrl);
            bottomSheetDialog.cancel();
        });
        Objects.requireNonNull(nextButton).setOnClickListener(view -> {
            getContent(nextUrl);
            bottomSheetDialog.cancel();
        });

        ScrollView scrollView = findViewById(R.id.scrollView);
        scrollView.setOnTouchListener((v, event) -> {
            int y = (int) event.getY();
            int x = (int) event.getX();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 记录触摸点坐标
                    lastY = y;
                    lastX = x;
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    if (Math.abs(y - lastY) < 5 && Math.abs(x - lastX) <5) {
                        bottomSheetDialog.show();
                    }
                    break;
            }
            return false;
        });

        SeekBar seekBar = bottomSheetDialog.findViewById(R.id.progress);
        Objects.requireNonNull(seekBar).setMax(chapter_list.size());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                lastProgress = i;
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                getContent(chapter_list.get(lastProgress).getHref());
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                TextView title_textview = findViewById(R.id.title);
                title_textview.setText(title);

                TextView contentTextView = findViewById(R.id.content);
                contentTextView.setText(content);

                ScrollView scrollView = findViewById(R.id.scrollView);
                scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_UP));

                Log.d("content", content);
            }
        }
    };

    private void getContent(String url) {
        new Thread(()->{
            try {
                Document doc = Jsoup.connect(url).get();

                Element title_element = doc.selectFirst(".style_h1");
                title = title_element.text();

                Elements content_list = doc.select("article p");
                content = "";
                for(Element i : content_list) {
                    content = content.concat("  "+i.text() + '\n');
                }

                Elements urls = doc.select(".read_nav a");
                preUrl = urls.get(0).attr("href");
                nextUrl = urls.get(urls.size()-1).attr("href");

                Message msg = new Message();
                msg.what = 1;
                handler.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}