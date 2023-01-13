package com.example.networkbookreader.ui.home;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.networkbookreader.db.BookIntro;
import com.example.networkbookreader.util.HelpUtil;

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
        Log.d("myPage",String.valueOf(page));
        Log.d("MaxPage", String.valueOf(max_page));
        new Thread(() -> {
            try {
                String url  = "https://www.xxbiqu.com/" + (isEnd ? "quanben" :"sort") + "/" + type.get(typeString) +"/"+ page +"/";
                Document doc = Jsoup.connect(url).get();
                Log.d("url", url);
                String a  = doc.select(".pages .pagelink a").last().attr("href");
                max_page = new HelpUtil().getContainsNum(a);
                Log.d("getMaxPage", String.valueOf(max_page));
                isOnlyOnePage = (max_page == 0);

                List<BookIntro> myList = new ArrayList<>();

                Elements li = doc.select(".flex li");
                for (Element i: li) {
                    BookIntro bookIntro = new BookIntro(
                            i.selectFirst(".w100 a h2").text(),
                            i.selectFirst(".img_span span").text(),
                            i.selectFirst(".w100 .indent").text().trim(),
                            i.selectFirst(".w100 .li_bottom a").text(),
                            i.selectFirst(".img_span a img").attr("data-original"),
                            i.selectFirst(".img_span a").attr("href")
                            );
                    Log.d("book", String.valueOf(bookIntro));
                    myList.add(bookIntro);
                }
                result_list.postValue(myList);
            } catch (IOException e) {
                Log.d("title","请求失败");
                e.printStackTrace();
            }
        }).start();
    }
}