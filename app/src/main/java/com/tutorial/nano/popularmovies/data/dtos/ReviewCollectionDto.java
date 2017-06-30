package com.tutorial.nano.popularmovies.data.dtos;

import com.google.gson.annotations.SerializedName;

public class ReviewCollectionDto {
    private int page;
    @SerializedName("total_pages")
    private int totalPages;
    @SerializedName("total_results")
    private int totalResults;
    @SerializedName("results")
    private ReviewDto[] reviews;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public ReviewDto[] getReviews() {
        return reviews;
    }

    public void setReviews(ReviewDto[] reviews) {
        this.reviews = reviews;
    }
}
