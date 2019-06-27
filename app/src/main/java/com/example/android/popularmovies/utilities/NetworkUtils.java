package com.example.android.popularmovies.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.example.android.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Utility functions to :
 * 1. handel network connection
 * 2. build URL
 * 3. check the Internet connection
 */
public class NetworkUtils {

    // variables used to build URL for the movie poster
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p";
    private static final String IMAGE_SIZE = "w500";

    // variables used to build the movie request
    private static final String BASE_URL = "https://api.themoviedb.org/3/movie";
    private static final String API_KEY = BuildConfig.API_KEY;
    // variables used to specify the sort method for the movie request
    private static final String SORT_BY_POPULAR = "popular";
    private static final String SORT_BY_TOP_RATED = "top_rated";

    // variables used to build the thumbnail url
    private static final String THUMBNAIL_BASE_URL = "https://img.youtube.com/vi";
    private static final String THUMBNAIL_FORMAT = "0.jpg";

    // variables used to build the video url
    private static final String VIDEOS_BASE_URL = "https://www.youtube.com";
    private static final String VIDEO_WATCH = "watch";
    private static final String VIDEO_PARAMETER = "v";

    /**
     * Function used to build URL for the video's thumbnail
     *
     * @param key
     * @return URL for the video's thumbnail
     */
    public static URL buildThumbnailURL(String key) {
        Uri builtUri = null;
        if (!key.isEmpty()) {
            builtUri = Uri.parse(THUMBNAIL_BASE_URL).buildUpon()
                    .appendEncodedPath(key)
                    .appendEncodedPath(THUMBNAIL_FORMAT)
                    .build();
        }
        URL thumbnailUrl = null;
        try {
            thumbnailUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return thumbnailUrl;
    }

    /**
     * Function used to build URL for the movie's video
     *
     * @param key
     * @return URL for the movie's video
     */
    public static URL buildVideosURL(String key) {
        Uri builtUri = null;
        if (!key.isEmpty()) {
            builtUri = Uri.parse(VIDEOS_BASE_URL).buildUpon()
                    .appendEncodedPath(VIDEO_WATCH)
                    .appendQueryParameter(VIDEO_PARAMETER, key)
                    .build();
        }
        URL videoUrl = null;
        try {
            videoUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return videoUrl;
    }

    /**
     * Function used to build URL for the movie poster
     *
     * @param path of the poster
     * @return URL for the movie poster
     */
    public static URL buildImageURL(String path) {
        Uri builtUri = null;
        if (!path.isEmpty()) {
            builtUri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                    .appendEncodedPath(IMAGE_SIZE)
                    .appendEncodedPath(path)
                    .build();
        }
        URL imageUrl = null;
        try {
            imageUrl = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return imageUrl;
    }

    /**
     * @param param     used to specify the sort method for the movie request
     * @param isMovieId used to check if the param is a movie id then it will
     *                  build a request for movie details.
     * @return URL for movie request
     */
    public static URL buildURL(String param, boolean isMovieId) {

        Uri builtUri = null;

        // build URI for request most popular movies list
        if (param.equals("POPULAR") && !isMovieId) {
            builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendEncodedPath(SORT_BY_POPULAR)
                    .appendQueryParameter("api_key", API_KEY)
                    .build();
        } // build URI for request top rated movies list
        else if (param.equals("TOP_RATED") && !isMovieId) {
            builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendEncodedPath(SORT_BY_TOP_RATED)
                    .appendQueryParameter("api_key", API_KEY)
                    .build();
        } // build URI for movies poster
        else if (isMovieId && !(param.isEmpty())) {
            builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendEncodedPath(param)
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("language", "en-US")
                    .appendQueryParameter("external_source", "imdb_id")
                    .build();
        }

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Function used to build URL to get the movie's videos/movie's reviews
     *
     * @param movieId
     * @param type    videos or reviews
     * @return the URL
     */
    public static URL buildMovieAdditionalInfoURL(String movieId, String type) {

        Uri builtUri = null;

        if (!movieId.isEmpty()) {
            builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendEncodedPath(movieId)
                    .appendEncodedPath(type)
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("language", "en-US")
                    .build();
        }

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Function used to returns the result from the HTTP response.
     *
     * @param url used to fetch the response
     * @return the result of the response
     * @throws IOException if there a network problem
     */
    public static String getHttpUrlResponse(URL url) throws IOException {

        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        try {
            InputStream inputStream = httpURLConnection.getInputStream();

            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            if (scanner.hasNext()) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            httpURLConnection.disconnect();
        }

    }

    /**
     * Function used to check the connection state
     * sources: https://developer.android.com/training/monitoring-device-state/connectivity-monitoring
     *
     * @return true, if the connection success
     * false, if the connection fail
     */
    public static boolean checkConnection(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null &&
                networkInfo.isConnectedOrConnecting();
    }
}
