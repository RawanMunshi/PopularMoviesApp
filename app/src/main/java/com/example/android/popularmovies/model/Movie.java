package com.example.android.popularmovies.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Model used to hold each movie details
 * also, it is used as an entry of the database
 */
@Entity(tableName = "favorite_movie")
public class Movie {

    // class members declaration

    @PrimaryKey
    @NonNull
    private String id;
    private String title;

    @ColumnInfo(name = "original_title")
    private String originalTitle;
    private String poster;
    private String overview;

    @ColumnInfo(name = "user_rating")
    private String userRating;

    @ColumnInfo(name = "release_date")
    private String releaseDate;

    private String genre;
    private String duration;

    @Ignore
    private List<VideoCard> videoCards;
    @Ignore
    private List<ReviewCard> reviewCards;

    // Constructor used by the database
    public Movie(String id, String title, String originalTitle, String poster, String overview, String userRating, String releaseDate, String genre, String duration) {
        this.id = id;
        this.title = title;
        this.originalTitle = originalTitle;
        this.poster = poster;
        this.overview = overview;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.genre = genre;
        this.duration = duration;

    }

    @Ignore
    public Movie(String id, String title, String originalTitle, String poster,
                 String overview, String userRating, String releaseDate, String genre,
                 String duration, List<VideoCard> videoCards, List<ReviewCard> reviewCards) {
        this.id = id;
        this.title = title;
        this.originalTitle = originalTitle;
        this.poster = poster;
        this.overview = overview;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.genre = genre;
        this.duration = duration;
        this.videoCards = videoCards;
        this.reviewCards = reviewCards;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getPoster() {
        return poster;
    }

    public String getOverview() {
        return overview;
    }

    public String getUserRating() {
        return userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getGenre() {
        return genre;
    }

    public String getDuration() {
        return duration;
    }

    public List<VideoCard> getVideoCards() {
        return videoCards;
    }

    public List<ReviewCard> getReviewCards() {
        return reviewCards;
    }
}
