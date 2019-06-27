package com.example.android.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.popularmovies.adepter.MovieCardAdepter;
import com.example.android.popularmovies.database.FavoritesDatabase;
import com.example.android.popularmovies.model.MainViewModel;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.MovieCard;
import com.example.android.popularmovies.utilities.JsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<MovieCard>> {

    public static final String SORTED_BY_EXTRA = "sorted by";
    public static final int MOVIE_CARDS_LOADER_ID = 0;
    public static final String IS_FAVORITE_EXTRA = "is favorite";
    private RecyclerView recyclerView;
    private MovieCardAdepter adepter;
    private ProgressBar progressBar;
    private TextView errorMessageTv;
    private FavoritesDatabase DB;
    private String sortedBy = "POPULAR";

    // a list used to hold the ids of the favorite movies
    // in order to display the appropriate icon (favorite icon or not favorite icon )
    // when the user click on the movie poster
    private List<String> favoriteMovieId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find views by ids
        progressBar = findViewById(R.id.list_loading);
        errorMessageTv = findViewById(R.id.error_message);
        recyclerView = findViewById(R.id.recyclerView);
        favoriteMovieId = new ArrayList<>();


        // set up the recycler view layout manager base on the screen orientation.
        recyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            layoutManager = new GridLayoutManager(this, 2);

        } else {
            layoutManager = new GridLayoutManager(this, 3);
        }

        recyclerView.setLayoutManager(layoutManager);


        // set up the recycler adepter and set on click listener
        adepter = new MovieCardAdepter();
        recyclerView.setAdapter(adepter);
        adepter.setOnItemClickListener(new MovieCardAdepter.OnItemClickListener() {
            @Override
            public void onListItemClick(int position) {
                SaveSortMethodPreferences();
                String movieId = adepter.getMovieCard().get(position).getMId();
                showFavoritesList(false);
                boolean isFavorite = false;
                //check if the movie is a favorite
                for (int i = 0; i < favoriteMovieId.size(); i++) {
                    if (favoriteMovieId.get(i).equals(movieId)) {
                        isFavorite = true;
                        break;
                    }
                }
                Context context = MainActivity.this;
                Intent intent = new Intent(context, MovieDetails.class);
                intent.putExtra(Intent.EXTRA_TEXT, movieId);
                intent.putExtra(IS_FAVORITE_EXTRA, isFavorite);
                startActivity(intent);
            }
        });

        DB = FavoritesDatabase.getDatabase(getApplicationContext());


        // load the movie list base on the sort method
        LoadSortMethodPreferences();
        setListHeader();
        if (sortedBy.equals("FAVORITE")) {
            showFavoritesList(true);
        } else {
            getSupportLoaderManager().initLoader(MOVIE_CARDS_LOADER_ID, null, MainActivity.this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // To make sure that the favorites list is updated when
        // the user come back to the activity
        setListHeader();
        if (sortedBy.equals("FAVORITE")) {
            showFavoritesList(true);
        }
    }

    private void setListHeader() {
        if (sortedBy.equals("POPULAR")) {
            setTitle(getString(R.string.most_popular_movies));
        } else if (sortedBy.equals("TOP_RATED")) {
            setTitle(getString(R.string.top_rated_movies));
        } else if (sortedBy.equals("FAVORITE")) {
            setTitle(getString(R.string.favorite_movies));
        }
    }

    /**
     * Function used to save the current sort method
     * as a SharedPreferences
     * this will help on rotation case
     * and when the user back to the activity
     */
    private void SaveSortMethodPreferences() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String saveSortMethod = sortedBy;
        editor.putString(SORTED_BY_EXTRA, saveSortMethod);
        editor.commit();
    }

    /**
     * Function used to load the sort method
     */
    private void LoadSortMethodPreferences() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        String sortMethod = sharedPreferences.getString(SORTED_BY_EXTRA, sortedBy);
        sortedBy = sortMethod;
    }

    // Function use the inflater's inflate to inflate the menu layout.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movies_sorting, menu);
        return true;
    }

    // Function use handel the menu item response
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        // Display movies sorted by popularity
        if (itemId == R.id.sorted_by_popular) {
            sortedBy = "POPULAR";
            setListHeader();
            SaveSortMethodPreferences();
            invalidateData();
            getSupportLoaderManager().restartLoader(MOVIE_CARDS_LOADER_ID, null, this);
            return true;
        }
        // Display movies sorted by top rating
        if (itemId == R.id.sorted_by_top_rating) {
            sortedBy = "TOP_RATED";
            setListHeader();
            SaveSortMethodPreferences();
            invalidateData();
            getSupportLoaderManager().restartLoader(MOVIE_CARDS_LOADER_ID, null, this);
            return true;
        }
        // Display favorite movies list
        if (itemId == R.id.open_favorites) {
            sortedBy = "FAVORITE";
            setListHeader();
            SaveSortMethodPreferences();
            invalidateData();
            showFavoritesList(true);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFavoritesList(final boolean show) {
        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        viewModel.getMoviesList().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(@Nullable List<Movie> FavoritesMovies) {
                Log.v("ViewModel", "Receiving database update from LiveData(Main)");

                // clear the list in order to put a updated list
                favoriteMovieId.clear();

                // Convert the Movie list to MovieCard list
                List<MovieCard> movieCards = new ArrayList<>();
                for (int i = 0; i < FavoritesMovies.size(); i++) {
                    Movie movie = FavoritesMovies.get(i);
                    movieCards.add(new MovieCard(movie.getId(), movie.getTitle(), movie.getPoster()));
                    favoriteMovieId.add(movie.getId());
                }
                // display the favorite movies list only if the sort method is FAVORITE
                if (show && sortedBy.equals("FAVORITE")) {
                    hideErrorMessage();
                    adepter.setMovieCards(movieCards);
                }
            }
        });
    }


    /**
     * A function used to display the error message and hide the recycler view
     *
     * @param errorMessage that will be shown
     */
    private void showErrorMessage(int errorMessage) {
        recyclerView.setVisibility(View.INVISIBLE);
        errorMessageTv.setText(errorMessage);
        errorMessageTv.setVisibility(View.VISIBLE);
    }

    /**
     * A function used to hide the error message and display the recycler view
     */
    private void hideErrorMessage() {
        errorMessageTv.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * A function used to display the progress bar and hide the recycler view
     */
    private void showProgressBar() {
        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * A function used to hide the progress bar and display the recycler view
     */
    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    @NonNull
    @Override
    public Loader<List<MovieCard>> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new AsyncTaskLoader<List<MovieCard>>(this) {

            List<MovieCard> MovieList = null;

            @Override
            protected void onStartLoading() {
                if (MovieList != null) {
                    deliverResult(MovieList);
                } else {
                    showProgressBar();
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public List<MovieCard> loadInBackground() {
                // check the Internet connection
                if (!NetworkUtils.checkConnection(getApplicationContext())) {
                    return null;
                }
                try {
                    // get the json string and store it in movieCard instance
                    URL url = NetworkUtils.buildURL(sortedBy, false);
                    String jsonString = NetworkUtils.getHttpUrlResponse(url);
                    return JsonUtils.jsonParseForMoviesList(jsonString);
                } catch (Exception e) {
                    showErrorMessage(R.string.error_message);
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public void deliverResult(@Nullable List<MovieCard> data) {
                MovieList = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<MovieCard>> loader, List<MovieCard> movieCards) {
        hideProgressBar();
        adepter.setMovieCards(movieCards);
        if (movieCards == null) {
            showErrorMessage(R.string.error_message_no_connection);
        } else {
            // display the movie card
            hideErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<MovieCard>> loader) {
        /** To implement the LoaderCallbacks<List<MovieCard>> interface */
    }

    private void invalidateData() {
        adepter.setMovieCards(null);
    }

}
