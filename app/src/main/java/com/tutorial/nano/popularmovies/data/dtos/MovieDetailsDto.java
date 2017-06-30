package com.tutorial.nano.popularmovies.data.dtos;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class MovieDetailsDto {

    private Long id;
    private String title;
    private String tagline;
    private String homepage;
    private String status;
    private Double budget;
    private Double revenue;
    private Double runtime;
    @SerializedName("spoken_languages")
    private LanguageDto[] spokenLanguages;
    @SerializedName("production_companies")
    private ProductionCompanyDto[] productionCompanies;
    @SerializedName("production_countries")
    private CountryDto[] productionCountries;
    private GenreDto[] genres;
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
    @SerializedName("reviews")
    private ReviewCollectionDto reviewCollection;
    @SerializedName("trailers")
    private TrailerCollectionDto trailerCollection;

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

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public Double getRevenue() {
        return revenue;
    }

    public void setRevenue(Double revenue) {
        this.revenue = revenue;
    }

    public Double getRuntime() {
        return runtime;
    }

    public void setRuntime(Double runtime) {
        this.runtime = runtime;
    }

    public LanguageDto[] getSpokenLanguages() {
        return spokenLanguages;
    }

    public void setSpokenLanguages(LanguageDto[] spokenLanguages) {
        this.spokenLanguages = spokenLanguages;
    }

    public ProductionCompanyDto[] getProductionCompanies() {
        return productionCompanies;
    }

    public void setProductionCompanies(ProductionCompanyDto[] productionCompanies) {
        this.productionCompanies = productionCompanies;
    }

    public CountryDto[] getProductionCountries() {
        return productionCountries;
    }

    public void setProductionCountries(CountryDto[] productionCountries) {
        this.productionCountries = productionCountries;
    }

    public GenreDto[] getGenres() {
        return genres;
    }

    public void setGenres(GenreDto[] genres) {
        this.genres = genres;
    }

    public ReviewCollectionDto getReviewCollection() {
        return reviewCollection;
    }

    public void setReviewCollection(ReviewCollectionDto reviewCollection) {
        this.reviewCollection = reviewCollection;
    }

    public TrailerCollectionDto getTrailerCollection() {
        return trailerCollection;
    }

    public void setTrailerCollection(TrailerCollectionDto trailerCollection) {
        this.trailerCollection = trailerCollection;
    }
}
