package com.example.android.popularmovies.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.android.popularmovies.model.Movie;

import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface MovieDao {
    @Query("SELECT * FROM favorite_movie")
    LiveData<List<Movie>> getFavoriteMoviesList();

    @Insert(onConflict = REPLACE)
    void addFavoriteMovies(Movie movie);

    @Query("SELECT * FROM favorite_movie WHERE id = :id")
    LiveData<Movie> getFavoriteMovieById(String id);

    @Delete
    void deleteFavoriteMovies(Movie movie);
}
