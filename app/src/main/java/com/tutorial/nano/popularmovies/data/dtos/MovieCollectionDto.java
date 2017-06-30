package com.tutorial.nano.popularmovies.data.dtos;

import com.google.gson.annotations.SerializedName;

public class MovieCollectionDto {
    private int page;
    @SerializedName("total_results")
    private int totalResults;
    @SerializedName("total_pages")
    private int totalPages;
    @SerializedName("results")
    private MovieDto[] movies;


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public MovieDto[] getMovies() {
        return movies;
    }

    public void setMovies(MovieDto[] movies) {
        this.movies = movies;
    }
}
