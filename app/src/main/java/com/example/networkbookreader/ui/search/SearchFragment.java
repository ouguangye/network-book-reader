package com.example.networkbookreader.ui.search;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.networkbookreader.CatalogueActivity;
import com.example.networkbookreader.MainViewModel;
import com.example.networkbookreader.R;
import com.example.networkbookreader.adapter.BookIntroAdapter;
import com.example.networkbookreader.db.BookIntro;
import com.example.networkbookreader.util.UnitTransformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchFragment extends Fragment {
    private MainViewModel mainViewModel;
    private SearchViewModel searchViewModel;
    private ProgressDialog dialog;
    private View root = null;

    private SearchView searchView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mainViewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        searchViewModel = mainViewModel.getSearchViewModel();
        if (searchViewModel == null) {
            searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
            mainViewModel.setSearchViewModel(searchViewModel);
        }

        root=mainViewModel.getSearchView().getValue();
        if(root==null){
            root = inflater.inflate(R.layout.fragment_search, container, false);
        }

        ListView bookList = root.findViewById(R.id.book_list);
        bookList.setOnItemClickListener((adapterView, view, i, l) -> {
                BookIntro bookIntro = (BookIntro) adapterView.getAdapter().getItem(i);
                Intent intent = new Intent(getContext(), CatalogueActivity.class);
                intent.putExtra("book", bookIntro);
                startActivity(intent);
            }
        );

        searchViewModel.getSearch_list().observe(getViewLifecycleOwner(),list->{
            BookIntroAdapter bookIntroAdapter = new BookIntroAdapter(getContext(), list);
            bookList.setAdapter(bookIntroAdapter);
            if (dialog != null) dialog.dismiss();
        });

        searchView = root.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(
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

        initAutoLL();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ViewGroup parent = (ViewGroup) root.getParent();
        if(parent!=null){
            parent.removeView(root);
        }
        mainViewModel.setSearchView(root);
    }

    private void searchEvent(String s) {
        saveSearchHistory(s);
        initAutoLL();
        searchViewModel.searchBook(s);
        searchView.clearFocus();
        dialog = ProgressDialog.show(getContext(), "", "?????????", true);
    }

    private void initAutoLL() {
        LinearLayout llParent = root.findViewById(R.id.ll_parent);
        List<String> data = getSearchHistory();
        llParent.removeAllViews();

        // ?????????????????????????????????????????????
        LinearLayout rowLL = new LinearLayout(getContext());
        LinearLayout.LayoutParams rowLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        float rowMargin = new UnitTransformation(getContext()).dip2px(10);
        rowLP.setMargins(0, (int) rowMargin, 0, 0);
        rowLL.setLayoutParams(rowLP);
        boolean isNewLayout = false;
        float maxWidth = getScreenWidth() - new UnitTransformation(getContext()).dip2px(30);

        //  ???????????????
        float elseWidth = maxWidth;
        LinearLayout.LayoutParams textViewLP = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewLP.setMargins(new UnitTransformation(getContext()).dip2px(8), 0, 0, 0);

        for (int i = 0; i < data.size(); i++) {
            //  ???????????????????????????????????????????????????
            //  ???????????????????????????????????????????????????isNewLayout???????????????false
            if (isNewLayout) {
                llParent.addView(rowLL);
                rowLL = new LinearLayout(getContext());
                rowLL.setLayoutParams(rowLP);
                isNewLayout = false;
            }
            // ????????????????????????
            @SuppressLint("InflateParams") TextView textView = (TextView) getLayoutInflater().inflate(R.layout.history_text, null);
            textView.setText(data.get(i));
            textView.measure(0, 0);

            // ??????textView???????????????
            int finalI = i;
            textView.setOnClickListener(view -> searchEvent(data.get(finalI)));

            // ???????????????????????????????????????????????????????????????????????????????????????????????????
            if (maxWidth < textView.getMeasuredWidth()) {
                llParent.addView(rowLL);
                rowLL = new LinearLayout(getContext());
                rowLL.setLayoutParams(rowLP);
                rowLL.addView(textView);
                isNewLayout = true;
                continue;
            }

            //  ???????????????????????????????????????????????????????????????
            //  ??????????????????????????????????????????i???-1???????????????????????????????????????
            if (elseWidth < textView.getMeasuredWidth()) {
                isNewLayout = true;
                i--;
            //  ??????????????????
                elseWidth = maxWidth;
            } else {
            //  ????????????????????????????????????+??????=??????????????????
                elseWidth -= textView.getMeasuredWidth() + new UnitTransformation(getContext()).dip2px(8);
                if (rowLL.getChildCount() != 0) {
                    textView.setLayoutParams(textViewLP);
                }
                rowLL.addView(textView);
            }
        }

        // ?????????????????????????????????????????????
        llParent.addView(rowLL);
    }

    //  ??????????????????
    private float getScreenWidth() {
        return this.getResources().getDisplayMetrics().widthPixels;
    }

    private final static String PREFERENCE_NAME = "search_history_sp";
    private final static String SEARCH_HISTORY="myHistory";
    // ??????????????????
    private void saveSearchHistory(String inputText) {
        SharedPreferences sp = requireContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        String longHistory = sp.getString(SEARCH_HISTORY, "");  //?????????????????????????????????
        String[] tmpHistory = longHistory.split(","); //???????????? ??????????????????
        List<String> historyList = new ArrayList<>(Arrays.asList(tmpHistory)); //?????????????????????ArrayList
        SharedPreferences.Editor editor = sp.edit();
        if (historyList.size() > 0) {
            //1.?????????????????????????????????
            for (int i = 0; i < historyList.size(); i++) {
                if (inputText.equals(historyList.get(i))) {
                    historyList.remove(i);
                    break;
                }
            }
            historyList.add(0, inputText); //???????????????????????????????????????0?????????????????????(2.??????)
            if (historyList.size() > 9) {
                historyList.remove(historyList.size() - 1); //3.????????????8??????????????? ??????????????????????????????
            }
            //????????????
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < historyList.size(); i++) {
                sb.append(historyList.get(i)).append(",");
            }
            //?????????sp
            editor.putString(SEARCH_HISTORY, sb.toString());
        } else {
            //??????????????????
            editor.putString(SEARCH_HISTORY, inputText + ",");
        }
        editor.apply();
    }

    //??????????????????
    public List<String> getSearchHistory(){
        SharedPreferences sp = requireContext().getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        String longHistory =sp.getString(SEARCH_HISTORY, "");
        String[] tmpHistory = longHistory.split(","); //split????????????1?????????????????????
        List<String> historyList = new ArrayList<>(Arrays.asList(tmpHistory));
        if (historyList.size() == 1 && historyList.get(0).equals("")) { //???????????????????????????split?????????0???????????????????????????
            historyList.clear();
        }
        return historyList;
    }
}