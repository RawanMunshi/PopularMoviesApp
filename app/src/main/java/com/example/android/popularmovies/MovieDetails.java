package com.example.android.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.android.popularmovies.adepter.ReviewsCardAdepter;
import com.example.android.popularmovies.adepter.VideoCardAdepter;
import com.example.android.popularmovies.database.AppExecutors;
import com.example.android.popularmovies.database.FavoritesDatabase;
import com.example.android.popularmovies.model.GetMovieViewModel;
import com.example.android.popularmovies.model.GetMovieViewModelFactory;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.ReviewCard;
import com.example.android.popularmovies.model.VideoCard;
import com.example.android.popularmovies.utilities.JsonUtils;
import com.example.android.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.List;

public class MovieDetails extends AppCompatActivity implements LoaderCallbacks<Movie> {

    public static final String IS_FAVORITE_EXTRA = "is favorite";
    public static final int MOVIE_LOADER_ID = 1;
    private static final String MOVIE_ID = "movie_id";
    private ImageView moviePosterIv;
    private TextView movieOriginalTitleTv;
    private TextView movieReleaseDateTv;
    private TextView movieGenreTv;
    private TextView movieDurationTv;
    private TextView movieUserRatingTv;
    private TextView movieOverviewTv;
    private TextView detailsErrorMessageTv;
    private ProgressBar detailsProgressBar;
    private LinearLayout movieInfoLL;
    private RelativeLayout movieInfoLL3;
    private RecyclerView videosRecyclerView;
    private VideoCardAdepter videosAdepter;
    private RecyclerView reviewsRecyclerView;
    private ReviewsCardAdepter reviewsAdepter;
    private TextView videosErrorMessageTv;
    private TextView reviewsErrorMessageTv;
    private Movie favoriteMovie;
    private Boolean isFavorite = false;
    private FavoritesDatabase DB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();

        // check if the movie is a favorite movie
        if (intent.hasExtra(IS_FAVORITE_EXTRA)) {
            isFavorite = intent.getBooleanExtra(IS_FAVORITE_EXTRA, false);
        }

        DB = FavoritesDatabase.getDatabase(getApplicationContext());


        // find views by ids
        movieInfoLL = findViewById(R.id.movie_info);
        movieInfoLL3 = findViewById(R.id.movie_info3);
        moviePosterIv = findViewById(R.id.movie_poster);
        movieOriginalTitleTv = findViewById(R.id.movie_original_title);
        movieReleaseDateTv = findViewById(R.id.movie_release_date);
        movieGenreTv = findViewById(R.id.movie_genres);
        movieDurationTv = findViewById(R.id.movie_duration);
        movieUserRatingTv = findViewById(R.id.movie_user_rating);
        movieOverviewTv = findViewById(R.id.movie_overview);
        detailsProgressBar = findViewById(R.id.details_loading);
        detailsErrorMessageTv = findViewById(R.id.details_error_message);
        videosRecyclerView = findViewById(R.id.videosRecyclerView);
        reviewsRecyclerView = findViewById(R.id.reviewsRecyclerView);
        videosErrorMessageTv = findViewById(R.id.videos_error_message);
        reviewsErrorMessageTv = findViewById(R.id.reviews_error_message);

        setUpReviewsRecyclerView();
        setUpVideosRecyclerView();

        // get movie id from the main activity
        String movieId = "";
        if (intent.hasExtra(Intent.EXTRA_TEXT)) {
            movieId = intent.getStringExtra(Intent.EXTRA_TEXT);
            boolean isConnect = NetworkUtils.checkConnection(this);

            // display the favorite movie details offline
            if (!isConnect && isFavorite) {
                showFavoriteMovie(movieId);
            } else {
                // fetch movie details by movie id
                Bundle bundle = new Bundle();
                bundle.putString(MOVIE_ID, movieId);
                getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, bundle, MovieDetails.this);
            }
        } else {
            showErrorMessage(R.string.error_message);
        }

    }

    /**
     * Function used the load and show the favorite movie list
     *
     * @param id of a favorite movie
     */
    private void showFavoriteMovie(final String id) {
        GetMovieViewModelFactory factory = new GetMovieViewModelFactory(DB, id);
        GetMovieViewModel viewModel =
                ViewModelProviders.of(this, factory).get(GetMovieViewModel.class);
        viewModel.getMovie().observe(this, new Observer<Movie>() {
            @Override
            public void onChanged(@Nullable Movie movie) {
                Log.v("MovieViewModel", "Receiving database update from LiveData in (detail class)");
                setMovieDetails(movie, false);
            }
        });
    }

    /**
     * Function used to setup the VideosRecyclerView
     */
    private void setUpVideosRecyclerView() {
        // set up the recycler view layout manager base on the screen orientation.
        videosRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        videosRecyclerView.setLayoutManager(layoutManager);

        // set up the recycler adepter and set on click listener
        videosAdepter = new VideoCardAdepter();
        videosRecyclerView.setAdapter(videosAdepter);
        videosAdepter.setOnItemClickListener(new VideoCardAdepter.OnItemClickListener() {
            @Override
            public void onListItemClick(int position) {
                // when the user clicked on a video card a youtube video will open
                String videoURL = videosAdepter.getVideoCards().get(position).getVideoURL();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoURL));
                startActivity(intent);
            }
        });
    }

    /**
     * Function used to setup the ReviewsRecyclerView
     */
    private void setUpReviewsRecyclerView() {
        // set up the recycler view layout manager base on the screen orientation.
        reviewsRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager;
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        reviewsRecyclerView.setLayoutManager(layoutManager);

        // set up the recycler adepter
        reviewsAdepter = new ReviewsCardAdepter();
        reviewsRecyclerView.setAdapter(reviewsAdepter);
    }

    /**
     * Functoion used to set the movie details
     *
     * @param movie
     * @param isConnect
     */
    private void setMovieDetails(Movie movie, boolean isConnect) {

        if (movie == null) {
            showErrorMessage(R.string.error_message);
            return;
        }

        // set the movie details
        hideErrorMessage();
        Picasso.get().load(movie.getPoster()).error(R.color.white).into(moviePosterIv);
        moviePosterIv.setContentDescription(movie.getTitle());
        movieOriginalTitleTv.setText(movie.getOriginalTitle());
        movieReleaseDateTv.setText(movie.getReleaseDate());
        movieDurationTv.setText(movie.getDuration());
        movieDurationTv.append(" ");
        movieDurationTv.append(getString(R.string.minute));
        movieUserRatingTv.setText(movie.getUserRating());
        movieOverviewTv.setText(movie.getOverview());
        movieGenreTv.setText(movie.getGenre());
        setTitle(movie.getTitle());
        favoriteMovie = movie;
        List<ReviewCard> reviewCard = movie.getReviewCards();
        List<VideoCard> videoCards = movie.getVideoCards();

        // show the videos and the reviews only if hte device is connected to the internet
        if (isConnect) {
            if (videoCards.isEmpty()) {
                showRecyclerViewErrorMessage(videosRecyclerView, videosErrorMessageTv, R.string.error_message_no_videos);
            } else {
                hideRecyclerViewErrorMessage(videosRecyclerView, videosErrorMessageTv);
                videosAdepter.setVideoCards(movie.getVideoCards());
            }
            if (reviewCard.isEmpty()) {
                showRecyclerViewErrorMessage(reviewsRecyclerView, reviewsErrorMessageTv, R.string.error_message_no_reviews);
            } else {
                hideRecyclerViewErrorMessage(reviewsRecyclerView, reviewsErrorMessageTv);
                reviewsAdepter.setReviewCards(movie.getReviewCards());
            }
        } else {
            showRecyclerViewErrorMessage(videosRecyclerView, videosErrorMessageTv, R.string.error_message_no_connection);
            showRecyclerViewErrorMessage(reviewsRecyclerView, reviewsErrorMessageTv, R.string.error_message_no_connection);
        }

    }

    /**
     * A function used to display the error message and hide the movie details
     *
     * @param errorMessage that will be shown
     */
    private void showErrorMessage(int errorMessage) {
        movieInfoLL.setVisibility(View.INVISIBLE);
        movieInfoLL3.setVisibility(View.INVISIBLE);
        detailsErrorMessageTv.setText(errorMessage);
        detailsErrorMessageTv.setVisibility(View.VISIBLE);
    }

    /**
     * A function used to hide the error message and display the movie details
     */
    private void hideErrorMessage() {
        detailsErrorMessageTv.setVisibility(View.INVISIBLE);
        movieInfoLL.setVisibility(View.VISIBLE);
        movieInfoLL3.setVisibility(View.VISIBLE);
    }

    /**
     * A function used to display the error message and hide the movie videos/reviews
     *
     * @param errorMessage that will be shown
     */
    private void showRecyclerViewErrorMessage(RecyclerView recyclerView, TextView errorTextView, int errorMessage) {
        recyclerView.setVisibility(View.INVISIBLE);
        errorTextView.setText(errorMessage);
        errorTextView.setVisibility(View.VISIBLE);
    }

    /**
     * A function used to hide the error message and display the movie videos/reviews
     */
    private void hideRecyclerViewErrorMessage(RecyclerView recyclerView, TextView errorTextView) {
        errorTextView.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * A function used to display the progress bar and hide the movie details
     */
    private void showProgressBar() {
        movieInfoLL.setVisibility(View.INVISIBLE);
        movieInfoLL3.setVisibility(View.INVISIBLE);
        detailsProgressBar.setVisibility(View.VISIBLE);
    }

    /**
     * A function used to hide the progress bar and display the movie details
     */
    private void hideProgressBar() {
        detailsProgressBar.setVisibility(View.INVISIBLE);
        movieInfoLL.setVisibility(View.VISIBLE);
        movieInfoLL3.setVisibility(View.VISIBLE);
    }

    /**
     * Used to fetch the movie details on the background thread
     */
    @NonNull
    @Override
    public Loader<Movie> onCreateLoader(int i, @Nullable final Bundle bundle) {
        return new AsyncTaskLoader<Movie>(this) {

            Movie movie = null;

            @Override
            protected void onStartLoading() {
                if (movie != null) {
                    deliverResult(movie);
                } else {
                    showProgressBar();
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public Movie loadInBackground() {
                String movieId = bundle.getString(MOVIE_ID);

                if (movieId.isEmpty()) {
                    return null;
                }

                try {
                    // get the json string and store it in movie instance
                    URL url = NetworkUtils.buildURL(movieId, true);
                    String jsonString = NetworkUtils.getHttpUrlResponse(url);

                    URL videosURL = NetworkUtils.buildMovieAdditionalInfoURL(movieId, "videos");
                    String videosJsonString = NetworkUtils.getHttpUrlResponse(videosURL);

                    URL reviewsURL = NetworkUtils.buildMovieAdditionalInfoURL(movieId, "reviews");
                    String reviewsJsonString = NetworkUtils.getHttpUrlResponse(reviewsURL);

                    return JsonUtils.jsonParseForMovieDetails(jsonString, videosJsonString, reviewsJsonString);

                } catch (Exception e) {
                    e.printStackTrace();
                    showErrorMessage(R.string.error_message);
                }
                return null;
            }

            @Override
            public void deliverResult(@Nullable Movie data) {
                movie = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Movie> loader, Movie movie) {
        hideProgressBar();
        if (movie == null) {
            showErrorMessage(R.string.error_message_no_connection);
        } else {
            // display the movie details
            setMovieDetails(movie, true);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Movie> loader) {
        /** To implement the LoaderCallbacks<List<MovieCard>> interface */
    }


    // Function use the inflater's inflate to inflate the menu layout.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.favorite_movie, menu);

        // set the favorite icon based on isFavorite variable
        int iconId;
        if (isFavorite) {
            iconId = R.drawable.ic_favorite_24px;
        } else {
            iconId = R.drawable.ic_heart_24px;
        }
        menu.getItem(0).setIcon(iconId);
        return true;
    }

    // Function use handel the menu item response
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.home) {
            onBackPressed();
        }

        if (itemId == R.id.favorite) {
            // allow adding and deleting only if the device is connected to the internet
            if (NetworkUtils.checkConnection(this)) {
                isFavorite = !isFavorite;
                final Movie finalFavoriteMovie = favoriteMovie;
                if (isFavorite) {
                    // add a new favorite movie
                    item.setIcon(R.drawable.ic_favorite_24px);
                    AppExecutors.getExecutorInstance().diskIOExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            DB.movieDao().addFavoriteMovies(finalFavoriteMovie);
                        }
                    });
                } else {
                    // delete a favorite movie
                    item.setIcon(R.drawable.ic_heart_24px);
                    AppExecutors.getExecutorInstance().diskIOExecutor().execute(new Runnable() {
                        @Override
                        public void run() {
                            DB.movieDao().deleteFavoriteMovies(finalFavoriteMovie);
                        }
                    });
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
