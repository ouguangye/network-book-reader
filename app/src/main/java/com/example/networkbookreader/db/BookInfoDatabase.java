package com.example.networkbookreader.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

@Database(entities = {BookIntro.class}, version = 1,exportSchema = false)
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
        return getBookIntroDao().isDataExit(bookIntro.getBook_id()).size() != 0;
    }

    private static BookInfoDatabase INSTANCE;
    private static final Object sLock = new Object();

    public static BookInfoDatabase getInstance(Context context) {
        synchronized (sLock) {
            if (INSTANCE == null) {
                INSTANCE =
                        Room.databaseBuilder(context.getApplicationContext(), BookInfoDatabase.class, "bookInfo.db")
                                .allowMainThreadQueries().build();
            }
            return INSTANCE;
        }
    }
}
