package com.example.networkbookreader.ui.bookshelf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.networkbookreader.CatalogueActivity;
import com.example.networkbookreader.adapter.BookShelfItemAdapter;
import com.example.networkbookreader.databinding.FragmentBookshelfBinding;
import com.example.networkbookreader.db.BookIntro;

public class BookshelfFragment extends Fragment {

    private FragmentBookshelfBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        BookshelfViewModel bookshelfViewModel = new ViewModelProvider(this).get(BookshelfViewModel.class);
        binding = FragmentBookshelfBinding.inflate(inflater, container, false);

        bookshelfViewModel.getBookIntroList().observe(getViewLifecycleOwner(), list->{
            BookShelfItemAdapter bookShelfItemAdapter = new BookShelfItemAdapter(getContext(), list);
            binding.bookshelfGridview.setAdapter(bookShelfItemAdapter);
        });

        binding.bookshelfGridview.setOnItemClickListener((adapterView, view, i, l)->{
            BookIntro bookIntro = (BookIntro) adapterView.getAdapter().getItem(i);
            Intent intent = new Intent(getContext(), CatalogueActivity.class);
            intent.putExtra("book", bookIntro);
            startActivity(intent);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}