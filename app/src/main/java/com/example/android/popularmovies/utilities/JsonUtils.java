package com.example.android.popularmovies.utilities;

import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.MovieCard;
import com.example.android.popularmovies.model.ReviewCard;
import com.example.android.popularmovies.model.VideoCard;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility functions to parse JSON data.
 */
@SuppressWarnings("unchecked")
public class JsonUtils {

    /**
     * Function used to parse JSON string to get movie card (id, title, poster)
     *
     * @param jsonString a JSON string that contain movies list
     * @return movie cards list
     * @throws JSONException if there is some problem when parsing the JSON string
     */
    public static List<MovieCard> jsonParseForMoviesList(String jsonString) throws JSONException {

        List<MovieCard> movieList = new ArrayList<>();
        JSONObject jsonObj = new JSONObject(jsonString);
        JSONArray movieArray = jsonObj.getJSONArray("results");

        for (int i = 0; i < movieArray.length(); i++) {

            String mId;
            String mTitle;
            String mPoster;

            JSONObject movieObj = movieArray.getJSONObject(i);

            mId = movieObj.getString("id");
            mTitle = movieObj.getString("title");

            // build the poster url
            URL posterURL = NetworkUtils.buildImageURL(movieObj.getString("poster_path"));
            mPoster = posterURL.toString();

            MovieCard movie = new MovieCard(mId, mTitle, mPoster);
            movieList.add(movie);
        }
        return movieList;
    }

    /**
     * Function used to parse JSON string to get movie details:
     * (title, originalTitle, poster, overview, userRating, releaseDate, genre, duration)
     * (videos, reviews)
     *
     * @param movieDetailsJsonString a JSON string that contain movies list
     * @return movie object that contain a movie details
     * @throws JSONException if there is some problem when parsing the JSON string
     */
    public static Movie jsonParseForMovieDetails(String movieDetailsJsonString, String videosJsonString, String reviewsJsonString)
            throws JSONException {


        JSONObject jsonObj = new JSONObject(movieDetailsJsonString);

        String mId;
        String mTitle;
        String mOriginalTitle;
        String mPoster;
        String mOverview;
        String mUserRating;
        String mReleaseDate;
        StringBuilder mGenre;
        String mDuration;
        List<VideoCard> videoCards = new ArrayList<>();
        List<ReviewCard> reviewCards = new ArrayList<>();

        mId = jsonObj.getString("id");
        mTitle = jsonObj.getString("title");
        mOriginalTitle = jsonObj.getString("original_title");

        // build the poster url
        URL posterURL = NetworkUtils.buildImageURL(jsonObj.getString("poster_path"));
        mPoster = posterURL.toString();

        mOverview = jsonObj.getString("overview");
        mUserRating = jsonObj.getString("vote_average");
        mReleaseDate = jsonObj.getString("release_date");
        JSONArray jsonArray = jsonObj.getJSONArray("genres");

        mGenre = new StringBuilder();

        for (int i = 0; i < jsonArray.length(); i++) {
            mGenre.append(jsonArray.getJSONObject(i).getString("name"));
            if (i < jsonArray.length() - 1) {
                mGenre.append(", ");
            }
        }
        mDuration = jsonObj.getString("runtime");

        JSONObject videosJsonObj = new JSONObject(videosJsonString);
        JSONArray videosJsonArray = videosJsonObj.getJSONArray("results");

        for (int i = 0; i < videosJsonArray.length(); i++) {
            String key = videosJsonArray.getJSONObject(i).getString("key");

            // build video URL and thumbnail URL
            String videoURL = NetworkUtils.buildVideosURL(key).toString();
            String thumbnailURL = NetworkUtils.buildThumbnailURL(key).toString();
            String title = videosJsonArray.getJSONObject(i).getString("type");
            videoCards.add(new VideoCard(videoURL, thumbnailURL, title));
        }

        JSONObject reviewsJsonObj = new JSONObject(reviewsJsonString);
        JSONArray reviewsJsonArray = reviewsJsonObj.getJSONArray("results");

        for (int i = 0; i < reviewsJsonArray.length(); i++) {
            String author = reviewsJsonArray.getJSONObject(i).getString("author");
            String content = reviewsJsonArray.getJSONObject(i).getString("content");
            reviewCards.add(new ReviewCard(author, content));
        }


        Movie movie = new Movie(mId, mTitle, mOriginalTitle,
                mPoster, mOverview, mUserRating, mReleaseDate,
                mGenre.toString(), mDuration, videoCards, reviewCards);

        return movie;
    }

}
