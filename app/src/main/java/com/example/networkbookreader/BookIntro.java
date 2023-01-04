package com.example.networkbookreader;

import java.io.Serializable;

public class BookIntro implements Serializable {
    private String name;
    private final String type;
    private final String detail;
    private final String author;
    private final String imgUrl;
    private final String bookUrl;

    public BookIntro(String name, String type, String detail, String author, String imgUrl, String bookUrl) {
        this.name = name;
        this.type = type;
        this.detail = detail;
        this.author = author;
        this.imgUrl = imgUrl;
        this.bookUrl = bookUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getDetail() {
        return detail;
    }

    public String getAuthor() {
        return author;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getBookUrl() {
        return bookUrl;
    }
}
