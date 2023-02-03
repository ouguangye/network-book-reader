package com.example.networkbookreader.ui.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.networkbookreader.CatalogueActivity;
import com.example.networkbookreader.MainViewModel;
import com.example.networkbookreader.R;
import com.example.networkbookreader.adapter.BookIntroAdapter;
import com.example.networkbookreader.db.BookIntro;

public class HomeFragment extends Fragment {
    private String type = "玄幻奇幻";
    private boolean isEnd = false;
    private View root = null;
    private HomeViewModel homeViewModel;
    private MainViewModel mainViewModel;
    private ProgressDialog dialog = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        homeViewModel = mainViewModel.getHomeViewModel();
        if(homeViewModel == null) {
            homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
            mainViewModel.setHomeViewModel(homeViewModel);
        }

        root=mainViewModel.getHomeView().getValue();
        if(root==null){
            root = inflater.inflate(R.layout.fragment_home, container, false);
        }

        // 设置下拉框
        Spinner spinner = root.findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter<>(getContext(), R.layout.spinner_item, new String[]{
            "玄幻奇幻","武侠修真","都市言情","历史军事","网游竞技","科幻灵异","女频同人"
        }));

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String new_type = spinner.getItemAtPosition(i).toString();
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

        ListView bookList = root.findViewById(R.id.book_list);
        View emptyView = root.findViewById(R.id.empty_view);
        if(emptyView != null) {
            TextView tip =  emptyView.findViewById(R.id.tip);
            tip.setText("初次加载过程可能比较慢，请耐心等待");
        }
        bookList.setEmptyView(emptyView);
        bookList.setOnItemClickListener((adapterView, view, i, l) -> {
                    BookIntro bookIntro = (BookIntro) adapterView.getAdapter().getItem(i);
                    Intent intent = new Intent(getContext(), CatalogueActivity.class);
                    intent.putExtra("book", bookIntro);
                    startActivity(intent);
                }
        );

        homeViewModel.getResult_list().observe(getViewLifecycleOwner(), list->{
            BookIntroAdapter bookIntroAdapter = new BookIntroAdapter(getContext(), list);
            bookList.setAdapter(bookIntroAdapter);
            if (dialog != null) dialog.dismiss();
        });

        // 选择全本
        CheckBox checkBox = root.findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            isEnd = b;
            homeViewModel.setMax_page(0);
            homeViewModel.setOnlyOnePage(false);
            homeViewModel.getBookList(type, isEnd);
            dialog = ProgressDialog.show(getContext(), "", "加载中", true);
        });

        // 刷新小说
        LinearLayout refresh = root.findViewById(R.id.refresh);
        refresh.setOnClickListener(view -> {
            if(homeViewModel.isOnlyOnePage()) {
                Toast.makeText(getContext(), "抱歉，资源有限",Toast.LENGTH_SHORT).show();
                return;
            }
            homeViewModel.getBookList(type,isEnd);
            dialog = ProgressDialog.show(getContext(), "", "加载中", true);
        });

        if(homeViewModel.getResult_list().getValue() == null) homeViewModel.getBookList(type, isEnd);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ViewGroup parent = (ViewGroup) root.getParent();
        if(parent!=null){
            parent.removeView(root);
        }
        mainViewModel.setHomeView(root);
    }
}