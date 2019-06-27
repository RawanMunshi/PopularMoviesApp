package com.example.android.popularmovies.model;

/**
 * Model used to hold each movie's video details
 */
public class VideoCard {

    private String videoURL;
    private String thumbnailURL;
    private String title;

    public VideoCard(String videoURL, String thumbnailURL, String title) {
        this.videoURL = videoURL;
        this.thumbnailURL = thumbnailURL;
        this.title = title;
    }

    public String getVideoURL() {
        return videoURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public String getTitle() {
        return title;
    }

}
