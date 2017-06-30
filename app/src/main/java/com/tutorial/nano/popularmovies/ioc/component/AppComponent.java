package com.tutorial.nano.popularmovies.ioc.component;

import com.tutorial.nano.popularmovies.activities.MainActivity;
import com.tutorial.nano.popularmovies.fragments.FragmentMain;
import com.tutorial.nano.popularmovies.fragments.MovieDetailFragment;
import com.tutorial.nano.popularmovies.fragments.MovieReviewsFragment;
import com.tutorial.nano.popularmovies.fragments.SingleReviewFragment;
import com.tutorial.nano.popularmovies.ioc.module.AppModule;
import com.tutorial.nano.popularmovies.ioc.module.JobManagerModule;
import com.tutorial.nano.popularmovies.ioc.module.NetModule;
import com.tutorial.nano.popularmovies.ioc.module.StorageModule;

import javax.inject.Singleton;

import dagger.Component;

@Component(modules = {
        AppModule.class,
        JobManagerModule.class,
        NetModule.class,
        StorageModule.class
})
@Singleton
public interface AppComponent {
    void inject(MainActivity mainActivity);
    void inject(FragmentMain fragmentMain);
    void inject(MovieDetailFragment movieDetailFragment);
    void inject(MovieReviewsFragment movieReviewsFragment);
    void inject(SingleReviewFragment singleReviewFragment);
}
