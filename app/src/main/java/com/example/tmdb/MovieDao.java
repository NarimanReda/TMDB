package com.example.tmdb;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.sqlite.db.SimpleSQLiteQuery;
import androidx.sqlite.db.SupportSQLiteQuery;

import java.util.List;

@Dao
public interface MovieDao {
    @Query("SELECT * FROM Movie")
    List<Movie> getAll();

    @Query("SELECT * FROM Movie WHERE uid IN (:movieIds)")
    List<Movie> loadAllByIds(int[] movieIds);

    @Query("SELECT * FROM Movie WHERE uid=:uid ")
    Movie getMovieByID(int uid);

    @RawQuery
    List<Movie> getMovieGenreQuery(SupportSQLiteQuery query);

    @Query("SELECT * FROM Movie WHERE vote_average >= :minVote")
    List<Movie> getMoviesWithMinVote(double minVote);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Movie> movies);

    @Delete
    void delete(Movie movie);

    @Insert
    void insert(Movie movie);

    @Query("DELETE FROM Movie")
    void deleteAll();

}

