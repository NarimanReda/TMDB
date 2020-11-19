package com.example.tmdb;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GenreDao {
    @Insert
    void insert(Genre genre);

    @Query("SELECT * FROM Genre WHERE uid=:uid ")
    Genre getGenreByID(int uid);

    @Query("SELECT * FROM Genre WHERE name=:name ")
    Genre getGenreByName(String name);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Genre> genres);

    @Query("SELECT * FROM Genre")
    List<Genre> getAll();
}
