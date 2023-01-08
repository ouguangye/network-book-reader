package com.example.networkbookreader.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BookIntroDao {
    @Query("select * from BookInfo")
    List<BookIntro> getAll();

    @Query("select * from BookInfo where book_id = :id")
    List<BookIntro> isDataExit(int id);

    @Insert
    void insertAll(BookIntro... bookIntros);

    @Delete
    void delete(BookIntro bookIntro);
}
