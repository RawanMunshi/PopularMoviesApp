package com.example.android.popularmovies.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.android.popularmovies.database.FavoritesDatabase;

/**
 * View Model used to get a movie by the id from the database
 */
public class GetMovieViewModel extends ViewModel {

    private LiveData<Movie> movie;

    public GetMovieViewModel(FavoritesDatabase database, String movieId) {
        movie = database.movieDao().getFavoriteMovieById(movieId);
        Log.d("Life GetMovieViewModel", "Actively retrieving the tasks from the DataBase");
    }

    public LiveData<Movie> getMovie() {
        return movie;
    }
}
