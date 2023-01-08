package com.example.networkbookreader.ui.bookshelf;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.networkbookreader.db.BookInfoDatabase;
import com.example.networkbookreader.db.BookIntro;

import java.util.List;

public class BookshelfViewModel extends ViewModel {
    private MutableLiveData<List<BookIntro>> bookIntroList;
    private BookInfoDatabase bookInfoDatabase = null;

    public BookshelfViewModel() {
        bookIntroList = new MutableLiveData<>();
    }

    public MutableLiveData<List<BookIntro>> getBookIntroList() {
        return bookIntroList;
    }

    public void getBookIntroListFromDatabase(Context context) {
        getBookInfoDatabase(context);
        bookIntroList.setValue(bookInfoDatabase.getBookIntroDao().getAll());
    }

    public void deleteBookIntro(BookIntro bookIntro) {
        bookInfoDatabase.getBookIntroDao().delete(bookIntro);
        bookIntroList.setValue(bookInfoDatabase.getBookIntroDao().getAll());
    }

    private void getBookInfoDatabase(Context context) {
        if (bookInfoDatabase == null) {
            bookInfoDatabase = BookInfoDatabase.getInstance(context);
        }
    }
}