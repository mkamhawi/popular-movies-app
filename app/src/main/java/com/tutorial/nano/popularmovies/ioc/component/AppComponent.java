package com.tutorial.nano.popularmovies.ioc.component;

import com.tutorial.nano.popularmovies.activities.MainActivity;
import com.tutorial.nano.popularmovies.fragments.FragmentMain;
import com.tutorial.nano.popularmovies.ioc.module.AppModule;
import com.tutorial.nano.popularmovies.ioc.module.StorageModule;

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
}
