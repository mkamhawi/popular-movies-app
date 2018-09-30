package com.tutorial.nano.popularmovies.data.models;

import com.tutorial.nano.popularmovies.R;
import com.tutorial.nano.popularmovies.data.MovieReview;
import com.tutorial.nano.popularmovies.data.dtos.ReviewDto;

import java.io.Serializable;

public class ReviewModel implements Serializable {

    private String author;

    private String content;

    private String url;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIcon() {
        return R.drawable.ic_comment;
    }

    public void fill(ReviewDto reviewDto) {
        author = reviewDto.getAuthor();
        content = reviewDto.getContent();
        url = reviewDto.getUrl();
    }
}
