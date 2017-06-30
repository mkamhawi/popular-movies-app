package com.tutorial.nano.popularmovies.network.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.tutorial.nano.popularmovies.data.MovieReview;
import com.tutorial.nano.popularmovies.data.MovieReviewDao;
import com.tutorial.nano.popularmovies.data.MovieTrailer;
import com.tutorial.nano.popularmovies.data.MovieTrailerDao;
import com.tutorial.nano.popularmovies.data.dtos.MovieDetailsDto;
import com.tutorial.nano.popularmovies.data.dtos.ReviewDto;
import com.tutorial.nano.popularmovies.data.dtos.TrailerDto;
import com.tutorial.nano.popularmovies.network.NetworkJobManager;
import com.tutorial.nano.popularmovies.network.events.MovieDetailsRetrievedEvent;

import java.util.Vector;

public class SaveMovieDetailsJob extends Job {
    private NetworkJobManager mNetworkJobManager;
    private MovieDetailsDto mMovieDetails;
    private MovieTrailerDao mTrailerDao;
    private MovieReviewDao mReviewDao;

    public SaveMovieDetailsJob(
            NetworkJobManager networkJobManager,
            MovieDetailsDto movieDetails,
            MovieTrailerDao movieTrailerDao,
            MovieReviewDao movieReviewDao
    ) {
        super(new Params(1000));
        mNetworkJobManager = networkJobManager;
        mMovieDetails = movieDetails;
        mTrailerDao = movieTrailerDao;
        mReviewDao = movieReviewDao;
    }

    @Override
    public void onAdded() {}

    @Override
    public void onRun() throws Throwable {
        TrailerDto[] trailerDtos = mMovieDetails.getTrailerCollection().getYoutube();
        Vector<MovieTrailer> trailers = new Vector<>(trailerDtos.length);
        for (int i = 0; i < trailerDtos.length; i++) {
            TrailerDto currentTrailer = trailerDtos[i];
            MovieTrailer trailer = new MovieTrailer();

            trailer.setMovieId(mMovieDetails.getId());
            trailer.setName(currentTrailer.getName());
            trailer.setSize(currentTrailer.getSize());
            trailer.setSource(currentTrailer.getSource());
            trailer.setType(currentTrailer.getType());

            trailers.add(trailer);
        }


        ReviewDto[] reviewDtos = mMovieDetails.getReviewCollection().getReviews();
        Vector<MovieReview> reviews = new Vector<>(reviewDtos.length);
        for (int i = 0; i < reviewDtos.length; i++) {
            ReviewDto currentReview = reviewDtos[i];
            MovieReview review = new MovieReview();

            review.setMovieId(mMovieDetails.getId());
            review.setReviewId(currentReview.getId());
            review.setReviewURL(currentReview.getUrl());
            review.setAuthor(currentReview.getAuthor());
            review.setContent(currentReview.getContent());

            reviews.add(review);
        }

        mTrailerDao.insertOrReplaceInTx(trailers);
        mReviewDao.insertOrReplaceInTx(reviews);
        mNetworkJobManager.postEvent(new MovieDetailsRetrievedEvent());
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {}

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
