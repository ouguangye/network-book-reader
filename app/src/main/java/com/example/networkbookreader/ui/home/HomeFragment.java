package com.example.networkbookreader.ui.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.networkbookreader.CatalogueActivity;
import com.example.networkbookreader.R;
import com.example.networkbookreader.adapter.BookIntroAdapter;
import com.example.networkbookreader.databinding.FragmentHomeBinding;
import com.example.networkbookreader.db.BookIntro;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private String type = "玄幻奇幻";
    private boolean isEnd = false;
    private HomeViewModel homeViewModel;
    private ProgressDialog dialog;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

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
                    homeViewModel.setMax_page(0);
                    homeViewModel.setOnlyOnePage(false);
                    homeViewModel.getBookList(type, isEnd);
                    dialog = ProgressDialog.show(getContext(), "", "加载中", true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        homeViewModel.getResult_list().observe(getViewLifecycleOwner(), list->{
            BookIntroAdapter bookIntroAdapter = new BookIntroAdapter(getContext(), list);
            binding.bookList.setAdapter(bookIntroAdapter);
            dialog.dismiss();
        });

        binding.bookList.setOnItemClickListener((adapterView, view, i, l) -> {
                BookIntro bookIntro = (BookIntro) adapterView.getAdapter().getItem(i);
                Intent intent = new Intent(getContext(), CatalogueActivity.class);
                intent.putExtra("book", bookIntro);
                startActivity(intent);
            }
        );

        // 选择全本
        binding.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            isEnd = b;
            homeViewModel.setMax_page(0);
            homeViewModel.setOnlyOnePage(false);
            homeViewModel.getBookList(type, isEnd);
            dialog = ProgressDialog.show(getContext(), "", "加载中", true);
        });

        // 刷新小说
        binding.refresh.setOnClickListener(view -> {
            if(homeViewModel.isOnlyOnePage()) {
                Toast.makeText(getContext(), "抱歉，资源有限",Toast.LENGTH_SHORT).show();
                return;
            }
            homeViewModel.getBookList(type,isEnd);
            dialog = ProgressDialog.show(getContext(), "", "加载中", true);
        });

        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        homeViewModel.getBookList(type, isEnd);
        dialog = ProgressDialog.show(getContext(), "", "加载中", true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}