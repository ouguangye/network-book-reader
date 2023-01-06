package com.example.networkbookreader.ui.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dyhdyh.widget.loadingbar.LoadingBar;
import com.example.networkbookreader.BookIntro;
import com.example.networkbookreader.CatalogueActivity;
import com.example.networkbookreader.R;
import com.example.networkbookreader.adapter.BookIntroAdapter;
import com.example.networkbookreader.databinding.FragmentSearchBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        binding.searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        if (s.equals("")) return false;
                        searchEvent(s);
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

        initAutoLL();
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void searchEvent(String s) {
        saveSearchHistory(s);
        initAutoLL();
        searchViewModel.searchBook(s);
        binding.searchView.clearFocus();
        LoadingBar.show(binding.bookList);
    }

    private void initAutoLL() {
        List<String> data = getSearchHistory();
        binding.llParent.removeAllViews();

        // 每一行的布局，初始化第一行布局
        LinearLayout rowLL = new LinearLayout(getContext());
        LinearLayout.LayoutParams rowLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        float rowMargin = dipToPx(10);
        rowLP.setMargins(0, (int) rowMargin, 0, 0);
        rowLL.setLayoutParams(rowLP);
        boolean isNewLayout = false;
        float maxWidth = getScreenWidth() - dipToPx(30);

        //  剩下的宽度
        float elseWidth = maxWidth;
        LinearLayout.LayoutParams textViewLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewLP.setMargins((int) dipToPx(8), 0, 0, 0);

        for (int i = 0; i < data.size(); i++) {
            //  若当前为新起的一行，先添加旧的那行
            //  然后重新创建布局对象，设置参数，将isNewLayout判断重置为false
            if (isNewLayout) {
                binding.llParent.addView(rowLL);
                rowLL = new LinearLayout(getContext());
                rowLL.setLayoutParams(rowLP);
                isNewLayout = false;
            }
            // 计算是否需要换行
            @SuppressLint("InflateParams") TextView textView = (TextView) getLayoutInflater().inflate(R.layout.history_text, null);
            textView.setText(data.get(i));
            textView.measure(0, 0);

            // 设置textView的监听事件
            int finalI = i;
            textView.setOnClickListener(view -> searchEvent(data.get(finalI)));

            // 若是一整行都放不下这个文本框，添加旧的那行，新起一行添加这个文本框
            if (maxWidth < textView.getMeasuredWidth()) {
                binding.llParent.addView(rowLL);
                rowLL = new LinearLayout(getContext());
                rowLL.setLayoutParams(rowLP);
                rowLL.addView(textView);
                isNewLayout = true;
                continue;
            }

            //  若是剩下的宽度小于文本框的宽度（放不下了）
            //  添加旧的那行，新起一行，但是i要-1，因为当前的文本框还未添加
            if (elseWidth < textView.getMeasuredWidth()) {
                isNewLayout = true;
                i--;
            //  重置剩余宽度
                elseWidth = maxWidth;
            } else {
            //  剩余宽度减去文本框的宽度+间隔=新的剩余宽度
                elseWidth -= textView.getMeasuredWidth() + dipToPx(8);
                if (rowLL.getChildCount() != 0) {
                    textView.setLayoutParams(textViewLP);
                }
                rowLL.addView(textView);
            }
        }

        // 添加最后一行，但要防止重复添加
        binding.llParent.addView(rowLL);
    }

    // dp转px
    private float dipToPx(int dipValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, this.getResources().getDisplayMetrics());
    }

    //  获得屏幕宽度
    private float getScreenWidth() {
        return this.getResources().getDisplayMetrics().widthPixels;
    }

    private final static String PREFERENCE_NAME = "search_history_sp";
    private final static String SEARCH_HISTORY="myHistory";
    // 保存搜索记录
    private void saveSearchHistory(String inputText) {
        SharedPreferences sp = requireContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        String longHistory = sp.getString(SEARCH_HISTORY, "");  //获取之前保存的历史记录
        String[] tmpHistory = longHistory.split(","); //逗号截取 保存在数组中
        List<String> historyList = new ArrayList<>(Arrays.asList(tmpHistory)); //将改数组转换成ArrayList
        SharedPreferences.Editor editor = sp.edit();
        if (historyList.size() > 0) {
            //1.移除之前重复添加的元素
            for (int i = 0; i < historyList.size(); i++) {
                if (inputText.equals(historyList.get(i))) {
                    historyList.remove(i);
                    break;
                }
            }
            historyList.add(0, inputText); //将新输入的文字添加集合的第0位也就是最前面(2.倒序)
            if (historyList.size() > 9) {
                historyList.remove(historyList.size() - 1); //3.最多保存8条搜索记录 删除最早搜索的那一项
            }
            //逗号拼接
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < historyList.size(); i++) {
                sb.append(historyList.get(i)).append(",");
            }
            //保存到sp
            editor.putString(SEARCH_HISTORY, sb.toString());
        } else {
            //之前未添加过
            editor.putString(SEARCH_HISTORY, inputText + ",");
        }
        editor.apply();
    }

    //获取搜索记录
    public List<String> getSearchHistory(){
        SharedPreferences sp = requireContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        String longHistory =sp.getString(SEARCH_HISTORY, "");
        String[] tmpHistory = longHistory.split(","); //split后长度为1有一个空串对象
        List<String> historyList = new ArrayList<>(Arrays.asList(tmpHistory));
        if (historyList.size() == 1 && historyList.get(0).equals("")) { //如果没有搜索记录，split之后第0位是个空串的情况下
            historyList.clear();
        }
        return historyList;
    }
}