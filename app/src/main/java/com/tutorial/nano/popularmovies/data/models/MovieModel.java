package com.tutorial.nano.popularmovies.data.models;

import com.tutorial.nano.popularmovies.data.dtos.MovieDetailsDto;
import com.tutorial.nano.popularmovies.data.dtos.ReviewDto;
import com.tutorial.nano.popularmovies.data.dtos.TrailerDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MovieModel {

    private String title;

    private String posterPath;

    private String plot;

    private Date releaseDate;

    private Double voteAverage;

    private List<TrailerModel> trailers = new ArrayList<>();

    private List<ReviewModel> reviews = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public List<TrailerModel> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<TrailerModel> trailers) {
        trailers.clear();
        this.trailers.addAll(trailers);
    }

    public List<ReviewModel> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewModel> reviews) {
        reviews.clear();
        this.reviews.addAll(reviews);
    }

    public void fill(MovieDetailsDto movieDetailsDto) {
        title = movieDetailsDto.getTitle();
        posterPath = movieDetailsDto.getPosterPath();
        plot = movieDetailsDto.getPlot();
        releaseDate = movieDetailsDto.getReleaseDate();
        voteAverage = movieDetailsDto.getVoteAverage();

        for (TrailerDto trailerDto: movieDetailsDto.getTrailerCollection().getYoutube()) {
            TrailerModel trailerModel = new TrailerModel();
            trailerModel.fill(trailerDto);
            trailers.add(trailerModel);
        }

        for (ReviewDto reviewDto : movieDetailsDto.getReviewCollection().getReviews()) {
            ReviewModel reviewModel = new ReviewModel();
            reviewModel.fill(reviewDto);
            reviews.add(reviewModel);
        }
    }
}
