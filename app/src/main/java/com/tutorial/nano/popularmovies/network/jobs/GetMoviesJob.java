package com.tutorial.nano.popularmovies.network.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.tutorial.nano.popularmovies.data.dtos.MovieCollectionDto;
import com.tutorial.nano.popularmovies.network.NetworkJobManager;

import retrofit2.Call;

public class GetMoviesJob extends NetworkJob {
    private final static String TAG = GetMoviesJob.class.getName();
    private NetworkJobManager mNetworkJobManager;
    private String mSortPreference;

    public GetMoviesJob(
            NetworkJobManager networkJobManager,
            String sortPreference,
            Call<MovieCollectionDto> call
    ) {
        super(call,  new Params(PRIORITY_MAX));
        this.mNetworkJobManager = networkJobManager;
        this.mSortPreference = sortPreference;
    }

    @Override
    protected void handleSuccessfulResult(Object result) {
        mNetworkJobManager.saveMovies((MovieCollectionDto) result, mSortPreference);
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
