package com.tutorial.nano.popularmovies.network.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.tutorial.nano.popularmovies.data.dtos.MovieDetailsDto;
import com.tutorial.nano.popularmovies.network.NetworkJobManager;

import retrofit2.Call;

public class GetMovieDetailsJob extends NetworkJob {
    private final static String TAG = GetMovieDetailsJob.class.getName();
    private NetworkJobManager mNetworkJobManager;

    public GetMovieDetailsJob(
            NetworkJobManager networkJobManager,
            Call<MovieDetailsDto> call
    ) {
        super(call,  new Params(PRIORITY_MAX));
        this.mNetworkJobManager = networkJobManager;
    }

    @Override
    protected void handleSuccessfulResult(Object result) {
        mNetworkJobManager.saveMoviesDetails((MovieDetailsDto) result);
    }

    @Override
    protected void handleUnsuccessfulResult(int failureCode, String errorBodyString) {
        Log.d(TAG, errorBodyString);
    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {}

    @Override
    protected RetryConstraint shouldReRunOnThrowable(
            @NonNull Throwable throwable,
            int runCount,
            int maxRunCount
    ) {
        return null;
    }
}
