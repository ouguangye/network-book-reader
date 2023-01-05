package com.example.networkbookreader.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dyhdyh.widget.loadingbar.LoadingBar;
import com.example.networkbookreader.BookIntro;
import com.example.networkbookreader.CatalogueActivity;
import com.example.networkbookreader.R;
import com.example.networkbookreader.adapter.BookIntroAdapter;
import com.example.networkbookreader.adapter.PageAdapter;
import com.example.networkbookreader.databinding.FragmentHomeBinding;
import com.lwy.paginationlib.PaginationRecycleView;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private String type = "玄幻奇幻";
    private boolean type_change = true;
    private int page = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);

        // 设置下拉框
        binding.spinner.setAdapter(new ArrayAdapter<>(getContext(), R.layout.spinner_item, new String[]{
            "玄幻奇幻","武侠修真","都市言情","历史军事","网游竞技","科幻灵异","女频同人"
        }));

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String new_type = binding.spinner.getItemAtPosition(i).toString();
                if (!new_type.equals(type)) {
                    type = new_type;
                    homeViewModel.getBookList(type, page);
                    type_change = true;
                    LoadingBar.show(binding.bookList);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        homeViewModel.getResult_list().observe(getViewLifecycleOwner(), list->{
            BookIntroAdapter bookIntroAdapter = new BookIntroAdapter(getContext(), list);
            binding.bookList.setAdapter(bookIntroAdapter);
            LoadingBar.cancelAll();
            binding.paginationRcv.setState(PaginationRecycleView.SUCCESS);
        });

        homeViewModel.getMax_page().observe(getViewLifecycleOwner(), m->{
            if(!type_change) return;
            type_change = false;
            PageAdapter pageAdapter = new PageAdapter(getContext(), m);
            binding.paginationRcv.setAdapter(pageAdapter);
            binding.paginationRcv.setPerPageCountChoices(new int[]{30});
        });

        binding.bookList.setOnItemClickListener((adapterView, view, i, l) -> {
                BookIntro bookIntro = (BookIntro) adapterView.getAdapter().getItem(i);
                Intent intent = new Intent(getContext(), CatalogueActivity.class);
                intent.putExtra("book", bookIntro);
                startActivity(intent);
            }
        );

        // 分页
        binding.paginationRcv.setListener(new PaginationRecycleView.Listener() {
            @Override
            public void loadMore(int currentPagePosition, int nextPagePosition, int perPageCount, int dataTotalCount) {
                page = currentPagePosition + 1;
                if (page != 1) {
                    homeViewModel.getBookList(type, page);
                }
            }

            @Override
            public void onPerPageCountChanged(int perPageCount) {

            }
        });

        homeViewModel.getBookList(type, page);
        LoadingBar.show(binding.bookList);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}