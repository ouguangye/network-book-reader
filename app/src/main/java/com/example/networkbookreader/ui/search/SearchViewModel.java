package com.example.networkbookreader.ui.search;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.networkbookreader.db.BookIntro;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchViewModel extends ViewModel {

    private MutableLiveData<List<BookIntro>> search_list;

    public SearchViewModel() {
        search_list = new MutableLiveData<>();
    }

    public MutableLiveData<List<BookIntro>> getSearch_list() {
        return search_list;
    }

    public void searchBook(String bookname) {
        new Thread(() -> {
            try {
                String url  = "https://www.xxbiqu.com/search/" + bookname + "/?type=all";
                Document doc = Jsoup.connect(url).get();
                Elements search_element_list = doc.select("li");
                List<BookIntro> result_list = new ArrayList<>();
                for (Element i : search_element_list) {
                    String name = i.selectFirst("h3").text();
                    String detail = i.selectFirst(".searchresult_p").text().trim();
                    String type = i.selectFirst("span").text();
                    String bookUrl = i.selectFirst("a").attr("href");
                    String imgUrl = i.selectFirst("a img").attr("data-original");
                    String author = i.selectFirst("p").text().split(" ")[0];
                    BookIntro bookIntro = new BookIntro(name, type,detail, author,imgUrl,bookUrl);
                    result_list.add(bookIntro);
                }
                search_list.postValue(result_list);
            } catch (IOException e) {
                Log.d("title","请求失败");
                e.printStackTrace();
            }
        }).start();
    }
}