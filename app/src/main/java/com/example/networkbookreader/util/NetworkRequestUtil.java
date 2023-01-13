package com.example.networkbookreader.util;

import android.os.AsyncTask;

import com.example.networkbookreader.db.BookIntro;
import com.example.networkbookreader.vo.ChapterItem;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NetworkRequestUtil {

    // 对不同需求枚举
    public enum request {
        LIBRARY,  // 书库
        SEARCH,   // 搜索
        CATALOGUE, // 获取目录
        CHAPTER_CONTENT // 获取章节内容
    }

    /* 监听 接口*/
    private OnHttpUrlListener onHttpUrlListener;
    //  创建抽象类 接口
    public abstract static class OnHttpUrlListener {
        public void success(ArrayList<ChapterItem> chapter_list) {}
        public void success(List<BookIntro> list, int i) {}
        public void fail() {}
    }
    // 设置监听器
    public void setOnHttpUrlListener(OnHttpUrlListener onHttpUrlListener) {
        this.onHttpUrlListener = onHttpUrlListener;
    }

    // 单例模式
    private final static NetworkRequestUtil instance = new NetworkRequestUtil();
    public NetworkRequestUtil getInstance() {
        return instance;
    }


    private request type;
    public void getResult(request type, String url) {
        this.type = type;
        new BookAsyncTask().execute(url);
    }


    class BookAsyncTask extends AsyncTask<String,Void, Boolean> {
        private List<BookIntro> bookIntroList;
        private ArrayList<ChapterItem> chapter_list;
        private int max_page;

        @Override
        protected Boolean doInBackground(String... strings) {
            String url = strings[0];
            switch (type) {
                case LIBRARY:
                    try {
                        getLibraryBookList(url);
                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                case SEARCH:
                    break;
                case CATALOGUE:
                    try {
                        getCatalogue(url);
                        return true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                case CHAPTER_CONTENT:
                    break;
                default:
                    break;
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean isSuccess) {
            super.onPostExecute(isSuccess);
            if (isSuccess) {
                switch (type) {
                    case LIBRARY:
                        onHttpUrlListener.success(bookIntroList, max_page);
                        break;
                    case SEARCH:
                        break;
                    case CATALOGUE:
                        onHttpUrlListener.success(chapter_list);
                        break;
                    case CHAPTER_CONTENT:
                       break;
                    default:
                        break;
                }
            }
            else {
                onHttpUrlListener.fail();
            }
        }

        // 获取目录
        private void getCatalogue(String url) throws IOException {
            Document doc = Jsoup.connect(url).get();
            Elements result_list = doc.select("#ul_all_chapters li a");
            chapter_list = new ArrayList<>();
            for(Element i : result_list) {
                chapter_list.add(new ChapterItem(i.attr("href"), i.text().trim()));
            }
        }

        // 获取书库书籍
        private void getLibraryBookList(String url) throws IOException {
            Document doc = Jsoup.connect(url).get();
            String a  = doc.select(".pages .pagelink a").last().attr("href");
            max_page = new HelpUtil().getContainsNum(a);
            bookIntroList = new ArrayList<>();
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
                bookIntroList.add(bookIntro);
            }
        }
    }
}
