package com.example.tmdb;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "Genre")
public class Genre implements Serializable {
    @PrimaryKey
    private int uid;

    public Genre(int uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    @ColumnInfo(name = "name")
    private String name;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
