package com.example.networkbookreader.ui.bookshelf;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.networkbookreader.ReadActivity;
import com.example.networkbookreader.adapter.BookShelfItemAdapter;
import com.example.networkbookreader.databinding.FragmentBookshelfBinding;
import com.example.networkbookreader.db.BookInfoDatabase;
import com.example.networkbookreader.db.BookIntro;
import com.example.networkbookreader.util.NetworkRequestUtil;
import com.example.networkbookreader.vo.ChapterItem;

import java.util.ArrayList;

public class BookshelfFragment extends Fragment {

    private FragmentBookshelfBinding binding;
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
            NetworkRequestUtil networkRequestUtil = new NetworkRequestUtil().getInstance();
            networkRequestUtil.getResult(NetworkRequestUtil.request.CATALOGUE, clicked_book_item.getBookUrl());
            networkRequestUtil.setOnHttpUrlListener(new NetworkRequestUtil.OnHttpUrlListener() {
                @Override
                public void success(ArrayList<ChapterItem> chapter_list) {
                    Intent intent = new Intent(getContext(), ReadActivity.class);
                    BookInfoDatabase bookInfoDatabase = BookInfoDatabase.getInstance(getContext());
                    intent.putExtra("i",bookInfoDatabase.getBookIntroDao().getDataFromName(clicked_book_item.getName()).getReadProgress());
                    intent.putExtra("name", clicked_book_item.getName());
                    intent.putParcelableArrayListExtra("list", chapter_list);
                    startActivity(intent);
                }
            });
        });

        binding.bookshelfGridview.setEmptyView(binding.emptyView.getRoot());
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}