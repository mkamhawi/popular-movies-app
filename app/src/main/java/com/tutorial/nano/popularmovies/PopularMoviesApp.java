package com.tutorial.nano.popularmovies;

import android.app.Application;

import com.tutorial.nano.popularmovies.ioc.component.AppComponent;
import com.tutorial.nano.popularmovies.ioc.component.DaggerAppComponent;
import com.tutorial.nano.popularmovies.ioc.module.AppModule;
import com.tutorial.nano.popularmovies.ioc.module.StorageModule;

public class PopularMoviesApp extends Application {
    private AppComponent mAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .storageModule(new StorageModule())
                .build();
    }

    public AppComponent getAppComponent() { return mAppComponent; }
}
