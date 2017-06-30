package com.tutorial.nano.popularmovies.network.jobs;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class NetworkJob<ResponseDto> extends Job {
    private static final String TAG = NetworkJob.class.getName();
    protected static final int PRIORITY_MEDIUM = 500;
    protected static final int PRIORITY_MAX = 1000;
    private Call<ResponseDto> mCall;


    protected NetworkJob(Call<ResponseDto> call, Params params) {
        super(params);
        mCall = call;
    }

    @Override
    public void onAdded() {}

    @Override
    public void onRun() {
        mCall.enqueue(new Callback<ResponseDto>() {
            @Override
            public void onResponse(Call<ResponseDto> call, Response<ResponseDto> response) {
                if(response == null || !response.isSuccessful())
                {
                    try {
                        handleUnsuccessfulResult(response.code(), response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    handleSuccessfulResult(response.body());
                }
            }

            @Override
            public void onFailure(Call<ResponseDto> call, Throwable t) {
                Log.e(TAG, t.getMessage());
                handleUnsuccessfulResult(0, t.getMessage());
            }
        });
    }

    protected abstract void handleSuccessfulResult(ResponseDto result);


    protected abstract void handleUnsuccessfulResult(int failureCode, String errorBodyString);
}
