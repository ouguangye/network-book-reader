package com.example.networkbookreader.ui.bookshelf;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.networkbookreader.CatalogueActivity;
import com.example.networkbookreader.R;
import com.example.networkbookreader.ReadActivity;
import com.example.networkbookreader.adapter.BookShelfItemAdapter;
import com.example.networkbookreader.adapter.ChapterItemAdapter;
import com.example.networkbookreader.databinding.FragmentBookshelfBinding;
import com.example.networkbookreader.db.BookInfoDatabase;
import com.example.networkbookreader.db.BookIntro;
import com.example.networkbookreader.vo.ChapterItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class BookshelfFragment extends Fragment {

    private FragmentBookshelfBinding binding;
    private ArrayList<ChapterItem> chapter_list;
    private BookIntro clicked_book_item;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        BookshelfViewModel bookshelfViewModel = new ViewModelProvider(this).get(BookshelfViewModel.class);
        binding = FragmentBookshelfBinding.inflate(inflater, container, false);

        bookshelfViewModel.getBookIntroList().observe(getViewLifecycleOwner(), list->{
            BookShelfItemAdapter bookShelfItemAdapter = new BookShelfItemAdapter(getContext(), list);
            binding.bookshelfGridview.setAdapter(bookShelfItemAdapter);
        });

        binding.bookshelfGridview.setOnItemClickListener((adapterView, view, i, l)->{
            clicked_book_item = (BookIntro) adapterView.getAdapter().getItem(i);
            getCatalogue();
        });

        binding.bookshelfGridview.setOnItemLongClickListener((adapterView, view, i, l)->{
            BookIntro bookIntro = (BookIntro) adapterView.getAdapter().getItem(i);
            AlertDialog alertDialog2 = new AlertDialog.Builder(getContext())
                    .setTitle("提醒")
                    .setMessage("确定删除？")
                    .setPositiveButton("确定", (dialogInterface, i1) -> {
                        bookshelfViewModel.deleteBookIntro(bookIntro);
                        Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("取消", (dialogInterface, i12) -> {})
                    .create();
            alertDialog2.show();
            return true;
        });

        bookshelfViewModel.getBookIntroListFromDatabase(getContext());
        return binding.getRoot();
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Intent intent = new Intent(getContext(), ReadActivity.class);
                BookInfoDatabase bookInfoDatabase = BookInfoDatabase.getInstance(getContext());
                intent.putExtra("i",bookInfoDatabase.getBookIntroDao().getDataFromName(clicked_book_item.getName()).getReadProgress());
                intent.putExtra("name", clicked_book_item.getName());
                intent.putParcelableArrayListExtra("list", chapter_list);
                startActivity(intent);
            }
        }
    };

    private void getCatalogue() {
        new Thread(()->{
            try {
                Document doc = Jsoup.connect(clicked_book_item.getBookUrl()).get();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}