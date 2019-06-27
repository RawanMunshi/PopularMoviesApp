package com.example.android.popularmovies.model;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.example.android.popularmovies.database.FavoritesDatabase;

/**
 * View Model Factory used to send the database instance and movie id to the GetMovieViewModel
 */
public class GetMovieViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final FavoritesDatabase database;
    private final String movieId;

    public GetMovieViewModelFactory(FavoritesDatabase database, String movieId) {
        this.database = database;
        this.movieId = movieId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new GetMovieViewModel(database, movieId);
    }
}
