package com.tutorial.nano.popularmovies.network.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.tutorial.nano.popularmovies.data.Movie;
import com.tutorial.nano.popularmovies.data.MovieDao;
import com.tutorial.nano.popularmovies.data.dtos.MovieCollectionDto;
import com.tutorial.nano.popularmovies.data.dtos.MovieDto;
import com.tutorial.nano.popularmovies.network.NetworkJobManager;
import com.tutorial.nano.popularmovies.network.events.MoviesRetrievedEvent;

import java.util.Vector;

public class SaveMoviesJob extends Job {
    private NetworkJobManager mNetworkJobManager;
    private MovieCollectionDto mMovieCollection;
    private MovieDao mMovieDao;
    private String mPosterBaseUrl;
    private String mSortPreference;

    public SaveMoviesJob(
            NetworkJobManager networkJobManager,
            MovieCollectionDto movieCollection,
            MovieDao movieDao,
            String posterBaseUrl,
            String sortPreference
    ) {
        super(new Params(1000));
        mNetworkJobManager = networkJobManager;
        mMovieCollection = movieCollection;
        mMovieDao = movieDao;
        mPosterBaseUrl = posterBaseUrl;
        mSortPreference = sortPreference;
    }

    @Override
    public void onAdded() {}

    @Override
    public void onRun() throws Throwable {
        MovieDto[] moviesDto = mMovieCollection.getMovies();
        Vector<Movie> movies = new Vector<>(moviesDto.length);
        for (int i = 0; i < moviesDto.length; i++) {
            Movie movie = new Movie();
            MovieDto currentMovie = moviesDto[i];
            movie.setId(currentMovie.getId());
            movie.setTitle(currentMovie.getTitle());
            movie.setPosterUrl(mPosterBaseUrl + currentMovie.getPosterPath());
            movie.setPlot(currentMovie.getPlot());
            movie.setReleaseDate(currentMovie.getReleaseDate());
            movie.setVoteAverage(currentMovie.getVoteAverage());
            movie.setSortPreferences(mSortPreference);
            movies.add(movie);
        }

        mMovieDao.insertOrReplaceInTx(movies, false);
        mNetworkJobManager.postEvent(new MoviesRetrievedEvent());
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {}

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
