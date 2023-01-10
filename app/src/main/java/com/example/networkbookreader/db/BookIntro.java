package com.example.networkbookreader.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "BookInfo")
public class BookIntro implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int book_id;

    private String name;
    private String type;
    private String detail;
    private String author;
    private String imgUrl;
    private String bookUrl;
    private int readProgress;

    public BookIntro(String name, String type, String detail, String author, String imgUrl, String bookUrl) {
        this.name = name;
        this.type = type;
        this.detail = detail;
        this.author = author;
        this.imgUrl = imgUrl;
        this.bookUrl = bookUrl;
        this.readProgress = 0;
    }

    public int getBook_id() {
        return book_id;
    }

    public void setBook_id(int book_id) {
        this.book_id = book_id;
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

    public void setType(String type) {
        this.type = type;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getBookUrl() {
        return bookUrl;
    }

    public void setBookUrl(String bookUrl) {
        this.bookUrl = bookUrl;
    }

    public int getReadProgress() {
        return readProgress;
    }

    public void setReadProgress(int readProgress) {
        this.readProgress = readProgress;
    }

    @NonNull
    @Override
    public String toString() {
        return "BookIntro{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", detail='" + detail + '\'' +
                ", author='" + author + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", bookUrl='" + bookUrl + '\'' +
                '}';
    }
}
