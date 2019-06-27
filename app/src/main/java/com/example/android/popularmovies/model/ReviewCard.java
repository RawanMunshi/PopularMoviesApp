package com.example.android.popularmovies.model;

/**
 * Model used to hold each movie's review details
 */
public class ReviewCard {

    private String author;
    private String content;

    public ReviewCard(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
}
