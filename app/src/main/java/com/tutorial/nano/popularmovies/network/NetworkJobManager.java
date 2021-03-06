package com.tutorial.nano.popularmovies.network;

import android.app.Application;
import android.util.Log;

import com.birbit.android.jobqueue.JobManager;
import com.tutorial.nano.popularmovies.R;
import com.tutorial.nano.popularmovies.data.MovieDao;
import com.tutorial.nano.popularmovies.data.MovieReviewDao;
import com.tutorial.nano.popularmovies.data.MovieTrailerDao;
import com.tutorial.nano.popularmovies.data.dtos.MovieCollectionDto;
import com.tutorial.nano.popularmovies.data.dtos.MovieDetailsDto;
import com.tutorial.nano.popularmovies.network.api.ApiMethods;
import com.tutorial.nano.popularmovies.network.events.IEvent;
import com.tutorial.nano.popularmovies.network.jobs.GetMoviesJob;
import com.tutorial.nano.popularmovies.network.jobs.SaveMovieDetailsJob;
import com.tutorial.nano.popularmovies.network.jobs.SaveMoviesJob;

import org.greenrobot.eventbus.EventBus;

import java.util.function.Consumer;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkJobManager {
    private static final String TAG = NetworkJobManager.class.getName();

    private Application mApplication;
    private JobManager mJobManager;
    private EventBus mEventBus;
    private ApiMethods mApiMethods;
    private MovieDao mMovieDao;
    private MovieTrailerDao mMovieTrailerDao;
    private MovieReviewDao mMovieReviewDao;

    @Inject
    public NetworkJobManager(
            Application application,
            JobManager jobManager,
            EventBus eventBus,
            MovieDao movieDao,
            MovieTrailerDao movieTrailerDao,
            MovieReviewDao movieReviewDao,
            ApiMethods apiMethods
    ) {
        mApplication = application;
        mJobManager = jobManager;
        mEventBus = eventBus;
        mMovieDao = movieDao;
        mMovieTrailerDao = movieTrailerDao;
        mMovieReviewDao = movieReviewDao;
        mApiMethods = apiMethods;
    }

    public void postEvent(IEvent event) {
        mEventBus.post(event);
    }

    public void requestMovies(String sortPreference) {
        if (!sortPreference.isEmpty()) {
            Call<MovieCollectionDto> call = mApiMethods.getMovies(sortPreference);
            mJobManager.addJobInBackground(new GetMoviesJob(this, sortPreference, call));
        }
    }

    public void saveMovies(
            MovieCollectionDto movieCollection,
            String sortPreference
    ) {
        mJobManager.addJobInBackground(new SaveMoviesJob(
                this,
                movieCollection,
                mMovieDao,
                mApplication.getString(R.string.movies_poster_base_url),
                sortPreference
        ));
    }

    public void requestMovieDetails(String movieId, Consumer<MovieDetailsDto> consumer) {
        mApiMethods.getMovieDetails(movieId, "trailers,reviews")
                .enqueue(new Callback<MovieDetailsDto>() {
                    @Override
                    public void onResponse(Call<MovieDetailsDto> call, Response<MovieDetailsDto> response) {
                        if (response.isSuccessful()) {
                            saveMoviesDetails(response.body());
                            consumer.accept(response.body());
                        } else {
                            Log.d(TAG, response.errorBody().toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieDetailsDto> call, Throwable t) {
                        Log.d(TAG, t.getMessage());
                    }
                });
    }

    public void saveMoviesDetails(MovieDetailsDto movieDetails) {
        mJobManager.addJobInBackground(new SaveMovieDetailsJob(
                this,
                movieDetails,
                mMovieTrailerDao,
                mMovieReviewDao
        ));
    }
}
