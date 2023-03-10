package com.example.networkbookreader.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BookIntroDao {
    @Query("select * from BookInfo")
    List<BookIntro> getAll();

    @Query("select * from BookInfo where name = :name")
    BookIntro getDataFromName(String name);

    @Insert
    void insertAll(BookIntro... bookIntros);

    @Delete
    void delete(BookIntro bookIntro);

    @Update
    void updateBook(BookIntro bookIntro);
}
