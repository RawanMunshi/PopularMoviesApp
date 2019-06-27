package com.example.android.popularmovies.model;

/**
 * Model used to hold each movie card
 */
public class MovieCard {

    // class members declaration
    private final String mId;
    private final String mTitle;
    private final String mPoster;

    public MovieCard(String mId, String mTitle, String mPoster) {
        this.mId = mId;
        this.mTitle = mTitle;
        this.mPoster = mPoster;
    }

    public String getMId() {
        return mId;
    }

    public String getMTitle() {
        return mTitle;
    }

    public String getMPoster() {
        return mPoster;
    }

}
