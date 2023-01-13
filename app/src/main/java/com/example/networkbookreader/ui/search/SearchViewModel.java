package com.example.networkbookreader.ui.search;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.networkbookreader.db.BookIntro;
import com.example.networkbookreader.util.NetworkRequestUtil;

import java.util.List;

public class SearchViewModel extends ViewModel {

    private MutableLiveData<List<BookIntro>> search_list;

    public SearchViewModel() {
        search_list = new MutableLiveData<>();
    }

    public MutableLiveData<List<BookIntro>> getSearch_list() {
        return search_list;
    }

    public void searchBook(String book_name) {
        String url  = "https://www.xxbiqu.com/search/" + book_name + "/?type=all";
        NetworkRequestUtil networkRequestUtil = new NetworkRequestUtil().getInstance();
        networkRequestUtil.getResult(NetworkRequestUtil.request.SEARCH, url);
        networkRequestUtil.setOnHttpUrlListener(new NetworkRequestUtil.OnHttpUrlListener() {
            @Override
            public void success(List<BookIntro> list) {
                super.success(list);
                search_list.setValue(list);
            }
        });
    }
}