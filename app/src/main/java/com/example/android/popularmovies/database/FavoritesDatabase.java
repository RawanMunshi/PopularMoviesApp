package com.example.android.popularmovies.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.util.Log;

import com.example.android.popularmovies.model.Movie;

@Database(entities = {Movie.class}, version = 1, exportSchema = false)
public abstract class FavoritesDatabase extends RoomDatabase {

    private static FavoritesDatabase databaseInstance;

    public static FavoritesDatabase getDatabase(Context context) {
        if (databaseInstance == null) {
            Log.v("database", "Create new database instance");
            databaseInstance =
                    Room.databaseBuilder(context.getApplicationContext(),
                            FavoritesDatabase.class, "favorite_movie_db")
                            .build();
        }
        Log.v("database", "Getting a database instance");
        return databaseInstance;
    }

    public abstract MovieDao movieDao();

}
