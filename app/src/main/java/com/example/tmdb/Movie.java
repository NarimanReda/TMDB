package com.example.tmdb;

import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "Movie")
public class Movie implements Serializable {
    @PrimaryKey
    private int uid;

    @ColumnInfo(name = "adult")
    private boolean adult;

    @ColumnInfo(name = "poster_path")
    @Nullable
    private String poster_path;

    @ColumnInfo(name = "overview")
    private String overview;

    @ColumnInfo(name = "release_date")
    private String release_date;

    @ColumnInfo(name = "genre_ids")
    private List<Integer> genre_ids;

    @ColumnInfo(name = "original_title")
    private String original_title;

    @ColumnInfo(name = "original_language")
    private String original_language;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "backdrop_path")
    @Nullable
    private String backdrop_path;

    @ColumnInfo(name = "popularity")
    private double popularity;

    @ColumnInfo(name = "vote_count")
    private int vote_count;

    @ColumnInfo(name = "vote_average")
    private double vote_average;

    @ColumnInfo(name = "video")
    private boolean video;


    /*public Movie(int uid, boolean adult, @Nullable String poster_path, String overview, String release_date, String original_title, String original_language, String title, @Nullable String backdrop_path, double popularity, int vote_count, double vote_average, boolean video) {
        this.uid = uid;
        this.adult = adult;
        this.poster_path = poster_path;
        this.overview = overview;
        this.release_date = release_date;
        this.original_title = original_title;
        this.original_language = original_language;
        this.title = title;
        this.backdrop_path = backdrop_path;
        this.popularity = popularity;
        this.vote_count = vote_count;
        this.vote_average = vote_average;
        this.video = video;
    }*/

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    @Nullable
    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(@Nullable String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public List<Integer> getGenre_ids() {
        return genre_ids;
    }

    public void setGenre_ids(List<Integer> genre_ids) {
        this.genre_ids = genre_ids;
    }

    public String getOriginal_title() {
        return original_title;
    }

    public void setOriginal_title(String original_title) {
        this.original_title = original_title;
    }

    public String getOriginal_language() {
        return original_language;
    }

    public void setOriginal_language(String original_language) {
        this.original_language = original_language;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Nullable
    public String getBackdrop_path() {
        return backdrop_path;
    }

    public void setBackdrop_path(@Nullable String backdrop_path) {
        this.backdrop_path = backdrop_path;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public int getVote_count() {
        return vote_count;
    }

    public void setVote_count(int vote_count) {
        this.vote_count = vote_count;
    }

    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(boolean video) {
        this.video = video;
    }
}

