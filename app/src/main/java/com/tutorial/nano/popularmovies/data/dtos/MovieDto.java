package com.tutorial.nano.popularmovies.data.dtos;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class MovieDto {

    private Long id;
    private String title;
    @SerializedName("poster_path")
    private String posterPath;
    @SerializedName("backdrop_path")
    private String backdropPath;
    @SerializedName("overview")
    private String plot;
    @SerializedName("release_date")
    private Date releaseDate;
    @SerializedName("vote_average")
    private Double voteAverage;
    @SerializedName("vote_count")
    private int voteCount;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() { return this.posterPath; }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() { return backdropPath; }

    public void setBackdropPath(String backdropPath) { this.backdropPath = backdropPath; }

    public String getPlot() {
        return this.plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public Date getReleaseDate() {
        return this.releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Double getVoteAverage() {
        return this.voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public int getVoteCount() { return voteCount; }

    public void setVoteCount(int voteCount) { this.voteCount = voteCount; }
}
