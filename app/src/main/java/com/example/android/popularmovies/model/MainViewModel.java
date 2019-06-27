package com.example.android.popularmovies.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.android.popularmovies.database.FavoritesDatabase;

import java.util.List;

/**
 * View Model used to load the favorite movies list from the database
 */
public class MainViewModel extends AndroidViewModel {
    private LiveData<List<Movie>> moviesList;

    public MainViewModel(@NonNull Application application) {
        super(application);
        FavoritesDatabase database = FavoritesDatabase.getDatabase(this.getApplication());
        moviesList = database.movieDao().getFavoriteMoviesList();
        Log.d("Life MainViewModel", "Actively retrieving the tasks from the DataBase");
    }

    public LiveData<List<Movie>> getMoviesList() {
        return moviesList;
    }

}
