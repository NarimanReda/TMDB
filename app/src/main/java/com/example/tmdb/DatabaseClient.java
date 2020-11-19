package com.example.tmdb;

import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {

    Context context;
    static DatabaseClient instance;

     AppDatabase appDatabase;

     DatabaseClient(Context context) {
        this.context = context;
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, "Movies").build();
    }

    public static synchronized DatabaseClient getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseClient(context);
        }
        return instance;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
