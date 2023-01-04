package com.example.networkbookreader.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dyhdyh.widget.loading.bar.LoadingBar;
import com.example.networkbookreader.BookIntro;
import com.example.networkbookreader.BookIntroAdapter;
import com.example.networkbookreader.CatalogueActivity;
import com.example.networkbookreader.databinding.FragmentSearchBinding;

public class SearchFragment extends Fragment {

    private SearchViewModel searchViewModel;
    private FragmentSearchBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);

        binding = FragmentSearchBinding.inflate(inflater, container, false);

        searchViewModel.getSearch_list().observe(getViewLifecycleOwner(),list->{
            BookIntroAdapter bookIntroAdapter = new BookIntroAdapter(getContext(), list);
            binding.bookList.setAdapter(bookIntroAdapter);
            LoadingBar.cancel(binding.bookList);
        });

        SearchView searchView = binding.searchView;
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        searchViewModel.searchBook(s);
                        searchView.clearFocus();
                        LoadingBar.make(binding.bookList).show();
                        return true;
                    }
                    @Override
                    public boolean onQueryTextChange(String s) {
                        return false;
                    }
                }
        );

        binding.bookList.setOnItemClickListener((adapterView, view, i, l) -> {
                BookIntro bookIntro = (BookIntro) adapterView.getAdapter().getItem(i);
                Intent intent = new Intent(getContext(), CatalogueActivity.class);
                intent.putExtra("book", bookIntro);
                startActivity(intent);
            }
        );

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}