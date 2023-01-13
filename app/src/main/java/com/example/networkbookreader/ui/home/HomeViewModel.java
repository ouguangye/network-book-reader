package com.example.networkbookreader.ui.home;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.networkbookreader.db.BookIntro;
import com.example.networkbookreader.util.HelpUtil;
import com.example.networkbookreader.util.NetworkRequestUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private int max_page;
    private MutableLiveData<List<BookIntro>> result_list;
    private final HashMap<String,String> type;
    private boolean isOnlyOnePage = false;

    public HomeViewModel() {
        max_page = 0;
        result_list = new MutableLiveData<>();

        type = new HashMap<>();
        type.put("玄幻奇幻","xuanhuan");
        type.put("武侠修真","wuxia");
        type.put("都市言情","dushi");
        type.put("历史军事","lishi");
        type.put("网游竞技","wangyou");
        type.put("科幻灵异","kehuan");
        type.put("女频同人","tongren");
    }

    public void setMax_page(int max_page) {
        this.max_page = max_page;
    }

    public void setOnlyOnePage(boolean onlyOnePage) {
        isOnlyOnePage = onlyOnePage;
    }

    public boolean isOnlyOnePage() {
        return isOnlyOnePage;
    }

    public MutableLiveData<List<BookIntro>> getResult_list() {
        return result_list;
    }

    public HashMap<String, String> getType() {
        return type;
    }

    public void getBookList(String typeString, boolean isEnd) {
        int page = (int) (Math.random()*max_page) + 1;
        String url  = "https://www.xxbiqu.com/" + (isEnd ? "quanben" :"sort") + "/" + type.get(typeString) +"/"+ page +"/";
        NetworkRequestUtil networkRequestUtil = new NetworkRequestUtil().getInstance();
        networkRequestUtil.getResult(NetworkRequestUtil.request.LIBRARY, url);
        networkRequestUtil.setOnHttpUrlListener(new NetworkRequestUtil.OnHttpUrlListener() {
            @Override
            public void success(List<BookIntro> list, int i) {
                super.success(list, i);
                result_list.setValue(list);
                max_page = i;
                isOnlyOnePage = (max_page == 0);
            }
        });
    }
}