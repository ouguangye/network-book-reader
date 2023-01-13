package com.example.networkbookreader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.networkbookreader.adapter.ChapterItemAdapter;
import com.example.networkbookreader.component.SettingDialog;
import com.example.networkbookreader.db.BookInfoDatabase;
import com.example.networkbookreader.util.NetworkRequestUtil;
import com.example.networkbookreader.util.UnitTransformation;
import com.example.networkbookreader.vo.ChapterItem;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.Objects;

public class ReadActivity extends AppCompatActivity{
    // 相关属性
    private ArrayList<ChapterItem> chapter_list;
    private int chapter_num;
    private String book_name;

    private String preUrl;
    private String nextUrl;

    private SharedPreferences.Editor editor;

    // 相关ui组件
    private DrawerLayout drawerLayout;
    private BottomSheetDialog bottomSheetDialog;
    private SettingDialog settingDialog;
    private ScrollView scrollView;
    private TextView title_textview;
    private TextView contentTextView;

    // 监听 scrollView的滑动大小
    private int lastX = 0;
    private int lastY = 0;

    // 监听 滑动条拖动是否完成
    private int lastProgress = 0;

    // 判断是否进黑暗模式
    private boolean isNightMode = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        // 接收上一个页面传输的数据
        chapter_list = getIntent().getParcelableArrayListExtra("list");
        chapter_num = getIntent().getIntExtra("i",0);
        book_name = getIntent().getStringExtra("name");
        getContent(chapter_list.get(chapter_num).getHref());

        // 对相关属性的储存
        SharedPreferences sharedPreferences = getSharedPreferences("data_storage", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        contentTextView = findViewById(R.id.content);
        drawerLayout = findViewById(R.id.drawer_layout);

        // 初始字体大小
        final int content_text_size = sharedPreferences.getInt("textSize",17);
        contentTextView.setTextSize(content_text_size);

        // 底部弹窗初始化
        bottomSheetDialog = new BottomSheetDialog(ReadActivity.this);
        @SuppressLint("InflateParams") View dialogView = LayoutInflater.from(ReadActivity.this).inflate(R.layout.read_book_dialog, null);
        bottomSheetDialog.setContentView(dialogView);

        // scrollView 点击触发 底部弹窗出现
        scrollView = findViewById(R.id.scrollView);
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

        // 侧边栏 目录渲染
        ListView listView = findViewById(R.id.catalogue_listview);
        ChapterItemAdapter chapterItemAdapter = new ChapterItemAdapter(getApplicationContext(), chapter_list);
        chapterItemAdapter.setTextSize(15);
        listView.setAdapter(chapterItemAdapter);
        listView.setOnItemClickListener((adapterView, view, i, l)->{
            chapter_num = i;
            getContent(chapter_list.get(i).getHref());
            drawerLayout.closeDrawers();
        });

        // 侧边栏适配 暗黑模式
        LinearLayout sidebar_linearLayout = findViewById(R.id.left_side);
        Log.d("mynightMode", String.valueOf(getApplicationContext().getResources().getConfiguration().uiMode));
        if (getApplicationContext().getResources().getConfiguration().uiMode == 0x11) {
            sidebar_linearLayout.setBackgroundColor(getResources().getColor(R.color.white));
        } else {
            sidebar_linearLayout.setBackgroundColor(getResources().getColor(R.color.black));
        }


        // 更多设置 弹窗
        settingDialog = new SettingDialog(ReadActivity.this);
        settingDialog.setConfirmClickListener(view1 -> {
            int text_size = settingDialog.getTextSize();
            contentTextView.setTextSize(text_size);
            editor.putInt("textSize", text_size);
            editor.commit();
            settingDialog.dismiss();
        });

        /* 底部弹窗点击事件 */
        // 底部弹窗 上一章、下一章按钮点击事件
        Button preButton = bottomSheetDialog.findViewById(R.id.pre_button);
        Button nextButton = bottomSheetDialog.findViewById(R.id.next_button);
        Objects.requireNonNull(preButton).setOnClickListener(view -> {
            if (chapter_num == 0) {
                Toast.makeText(getApplicationContext(), "这已经是第一章了", Toast.LENGTH_SHORT).show();
                return;
            }
            chapter_num -= 1;
            getContent(preUrl);
            bottomSheetDialog.cancel();
        });
        Objects.requireNonNull(nextButton).setOnClickListener(view -> {
            if (chapter_num == chapter_list.size()-1){
                Toast.makeText(getApplicationContext(), "这已经是最后一章了", Toast.LENGTH_SHORT).show();
                return;
            }
            chapter_num += 1;
            getContent(nextUrl);
            bottomSheetDialog.cancel();
        });

        // 底部弹窗 滑动条拖动事件
        SeekBar seekBar = bottomSheetDialog.findViewById(R.id.progress);
        TextView progressText = bottomSheetDialog.findViewById(R.id.seek_text);
        Objects.requireNonNull(seekBar).setMax(chapter_list.size()-1);
        seekBar.setProgress(chapter_num);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                lastProgress = i;
                //设置文本显示
                Objects.requireNonNull(progressText).setText(chapter_list.get(i).getName().split(" ")[0]);
                //获取文本宽度
                float textWidth = progressText.getWidth();
                //获取seekbar最左端的x位置
                float left = seekBar.getLeft();
                //进度条的刻度值
                float max =Math.abs(seekBar.getMax());
                //这不叫thumb的宽度,叫seekbar距左边宽度,实验了一下，seekbar 不是顶格的，两头都存在一定空间，所以xml 需要用paddingStart 和 paddingEnd 来确定具体空了多少值,我这里设置15dp;
                float thumb = new UnitTransformation(bottomSheetDialog.getContext()).dip2px(25);
                //每移动1个单位，text应该变化的距离 = (seekBar的宽度 - 两头空的空间) / 总的progress长度
                float average = (((float) seekBar.getWidth())-2*thumb)/max;
                //textview 应该所处的位置 = seekbar最左端 + seekbar左端空的空间 + 当前progress应该加的长度 - textview宽度的一半(保持居中作用)
                float pox = left - textWidth/2 +thumb + average * (float) i;
                progressText.setX(pox);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                chapter_num = lastProgress;
                getContent(chapter_list.get(lastProgress).getHref());
            }
        });

        // 底部弹窗 夜间模式点击事件
        LinearLayout night_linearLayout = bottomSheetDialog.findViewById(R.id.night_mode_linearLayout);
        Objects.requireNonNull(night_linearLayout).setOnClickListener(view -> {
            if(this.getApplicationContext().getResources().getConfiguration().uiMode != 0x11) {
                Toast.makeText(getApplicationContext(), "当前系统处于暗色模式下，该功能无法使用",Toast.LENGTH_SHORT).show();
                return;
            }
            isNightMode = !isNightMode;
            ImageView imageView = bottomSheetDialog.findViewById(R.id.night_icon);
            TextView textView = bottomSheetDialog.findViewById(R.id.night_text);
            if (isNightMode) {
                scrollView.setBackgroundColor(getResources().getColor(R.color.black));
                title_textview.setTextColor(getResources().getColor(R.color.white));
                contentTextView.setTextColor(getResources().getColor(R.color.white));

                Objects.requireNonNull(imageView).setImageResource(R.drawable.ic_day);
                Objects.requireNonNull(textView).setText("白天模式");
            }
            else {
                scrollView.setBackgroundColor(getResources().getColor(R.color.white));
                title_textview.setTextColor(getResources().getColor(R.color.black));
                contentTextView.setTextColor(getResources().getColor(R.color.black));

                Objects.requireNonNull(imageView).setImageResource(R.drawable.ic_night);
                Objects.requireNonNull(textView).setText("夜间模式");
            }
        });

        // 底部弹窗 目录
        LinearLayout catalogue_linearLayout = bottomSheetDialog.findViewById(R.id.catalogue_linearLayout);
        Objects.requireNonNull(catalogue_linearLayout).setOnClickListener(view->{
            drawerLayout.openDrawer(GravityCompat.START);
            bottomSheetDialog.dismiss();
        });

        // 底部弹窗 更多设置
        LinearLayout more_linearLayout = bottomSheetDialog.findViewById(R.id.more_setting_linearLayout);
        Objects.requireNonNull(more_linearLayout).setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            settingDialog.setTextSize(new UnitTransformation(getApplicationContext()).px2sp(contentTextView.getTextSize()));
            settingDialog.show();
        });
    }

    private void getContent(String url) {
        NetworkRequestUtil networkRequestUtil = new NetworkRequestUtil().getInstance();
        networkRequestUtil.getResult(NetworkRequestUtil.request.CHAPTER_CONTENT, url);
        networkRequestUtil.setOnHttpUrlListener(new NetworkRequestUtil.OnHttpUrlListener() {
            @Override
            public void success(String title, String content, String preUrl, String nextUrl) {
                super.success(title, content, preUrl, nextUrl);
                ReadActivity.this.preUrl = preUrl;
                ReadActivity.this.nextUrl = nextUrl;

                title_textview = findViewById(R.id.title);
                title_textview.setText(title);
                contentTextView.setText(content);
                ScrollView scrollView = findViewById(R.id.scrollView);
                scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_UP));
                BookInfoDatabase bookInfoDatabase = BookInfoDatabase.getInstance(getApplicationContext());
                bookInfoDatabase.updateBookReadProgressFromName(book_name, chapter_num);
            }
        });
    }
}