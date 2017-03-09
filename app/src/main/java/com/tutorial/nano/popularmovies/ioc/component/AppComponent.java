package com.tutorial.nano.popularmovies.ioc.component;

import com.tutorial.nano.popularmovies.activities.MainActivity;
import com.tutorial.nano.popularmovies.fragments.FragmentMain;
import com.tutorial.nano.popularmovies.fragments.MovieDetailFragment;
import com.tutorial.nano.popularmovies.fragments.MovieReviewsFragment;
import com.tutorial.nano.popularmovies.fragments.SingleReviewFragment;
import com.tutorial.nano.popularmovies.ioc.module.AppModule;
import com.tutorial.nano.popularmovies.ioc.module.StorageModule;
import com.tutorial.nano.popularmovies.tasks.FetchExtraMovieDetailsTask;
import com.tutorial.nano.popularmovies.tasks.FetchMoviesTask;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {
        AppModule.class,
        StorageModule.class
})
@Singleton
public interface AppComponent {
    void inject(MainActivity mainActivity);
    void inject(FragmentMain fragmentMain);
    void inject(MovieDetailFragment movieDetailFragment);
    void inject(MovieReviewsFragment movieReviewsFragment);
    void inject(SingleReviewFragment singleReviewFragment);
    void inject(FetchMoviesTask fetchMoviesTask);
    void inject(FetchExtraMovieDetailsTask fetchExtraMovieDetailsTask);
}
