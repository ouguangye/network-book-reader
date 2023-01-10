package com.example.networkbookreader.db;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import java.util.List;

@Database(entities = {BookIntro.class}, version = 2,exportSchema = false)
public abstract class BookInfoDatabase extends RoomDatabase {
    public abstract BookIntroDao getBookIntroDao();

    @NonNull
    @Override
    protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
        return null;
    }

    @NonNull
    @Override
    protected InvalidationTracker createInvalidationTracker() {
        return null;
    }

    @Override
    public void clearAllTables() {

    }

    public boolean isDataExit(BookIntro bookIntro) {
        return getBookIntroDao().getDataFromName(bookIntro.getName()) != null;
    }

    public boolean isRead(BookIntro bookIntro) {
        BookIntro get_bookIntro = getBookIntroDao().getDataFromName(bookIntro.getName());
        return get_bookIntro != null && get_bookIntro.getReadProgress() != 0;
    }

    public void updateBookReadProgressFromName(String name, int readProgress) {
        Log.d("myName", name);
        BookIntro bookIntro = getBookIntroDao().getDataFromName(name);
        Log.d("myBook", String.valueOf(bookIntro));
        if(bookIntro == null) return;
        bookIntro.setReadProgress(readProgress);
        getBookIntroDao().updateBook(bookIntro);
    }

    private static BookInfoDatabase INSTANCE;
    private static final Object sLock = new Object();

    public static BookInfoDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE =
                        Room.databaseBuilder(context.getApplicationContext(), BookInfoDatabase.class, "bookInfo.db")
                                .allowMainThreadQueries().fallbackToDestructiveMigration().build();
            }
            return INSTANCE;
        }
    }
}
