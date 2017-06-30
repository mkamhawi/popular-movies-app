package com.tutorial.nano.popularmovies.ioc.module;

import android.app.Application;
import android.util.Log;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.birbit.android.jobqueue.log.CustomLogger;
import com.tutorial.nano.popularmovies.data.MovieDao;
import com.tutorial.nano.popularmovies.data.MovieReviewDao;
import com.tutorial.nano.popularmovies.data.MovieTrailerDao;
import com.tutorial.nano.popularmovies.network.NetworkJobManager;
import com.tutorial.nano.popularmovies.network.api.ApiMethods;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class JobManagerModule {
    @Provides
    @Singleton
    JobManager provideJobManager(Application application) {
        Configuration configuration = new Configuration.Builder(application)
                .customLogger(new CustomLogger() {
                    private final String TAG = "jobManager";
                    @Override
                    public boolean isDebugEnabled() {
                        return true;
                    }

                    @Override
                    public void d(String text, Object... args) {
                        Log.d(TAG, String.format(text, args));
                    }

                    @Override
                    public void e(Throwable t, String text, Object... args) {
                        Log.e(TAG, String.format(text, args), t);
                    }

                    @Override
                    public void e(String text, Object... args) {
                        Log.e(TAG, String.format(text, args));
                    }

                    @Override
                    public void v(String text, Object... args) {
                        Log.e(TAG, String.format(text, args));
                    }
                })
                .minConsumerCount(1)
                .maxConsumerCount(Runtime.getRuntime().availableProcessors())
                .loadFactor(1)
                .consumerKeepAlive(120)
                .id("network")
                .build();
        return new JobManager(configuration);
    }

    @Provides
    @Singleton
    NetworkJobManager provideNetworkJobManager(
            Application application,
            JobManager jobManager,
            EventBus eventBus,
            MovieDao movieDao,
            MovieTrailerDao movieTrailerDao,
            MovieReviewDao movieReviewDao,
            ApiMethods apiMethods
    ) {
        return new NetworkJobManager(
                application,
                jobManager,
                eventBus,
                movieDao,
                movieTrailerDao,
                movieReviewDao,
                apiMethods
        );
    }

}
