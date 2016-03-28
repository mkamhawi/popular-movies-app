package com.tutorial.nano.popularmovies;

public class Movie {
    int id;
    String title;
    String posterPath;
    String plotSynopsis;
    String releaseDate;
    double voteAverage;

    public Movie(
            int id,
            String title,
            String posterPath,
            String plotSynopsis,
            String releaseDate,
            double voteAverage)
    {
        if(title == null) { throw new IllegalArgumentException("title can not be null."); }
        if(posterPath == null) { throw new IllegalArgumentException("posterPath can not be null."); }
        if(plotSynopsis == null) { throw new IllegalArgumentException("plotSynopsis can not be null."); }
        if(releaseDate == null) { throw new IllegalArgumentException("releaseDate can not be null."); }

        this.id = id;
        this.title = title;
        this.posterPath = posterPath;
        this.plotSynopsis = plotSynopsis;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
    }
}
