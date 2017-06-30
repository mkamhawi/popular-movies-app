package com.tutorial.nano.popularmovies.ioc.module;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.tutorial.nano.popularmovies.data.DaoMaster;
import com.tutorial.nano.popularmovies.data.DaoMaster.DevOpenHelper;
import com.tutorial.nano.popularmovies.data.DaoSession;
import com.tutorial.nano.popularmovies.data.FavoriteMovieDao;
import com.tutorial.nano.popularmovies.data.MovieDao;
import com.tutorial.nano.popularmovies.data.MovieReviewDao;
import com.tutorial.nano.popularmovies.data.MovieTrailerDao;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.greendao.database.Database;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class StorageModule {
    @Provides
    @Singleton
    SharedPreferences provideSharedPreferences(Application application) {
        return PreferenceManager.getDefaultSharedPreferences(application);
    }

    @Provides
    @Singleton
    DaoSession provideDaoSession(Application application) {
        DevOpenHelper helper = new DevOpenHelper(application, "movies-db");
        Database db = helper.getWritableDb();
        return new DaoMaster(db).newSession();
    }

    @Provides
    @Singleton
    MovieDao provideMovieDao(DaoSession daoSession) {
        return daoSession.getMovieDao();
    }

    @Provides
    @Singleton
    FavoriteMovieDao provideFavoriteMovieDao(DaoSession daoSession) {
        return daoSession.getFavoriteMovieDao();
    }

    @Provides
    @Singleton
    MovieTrailerDao provideMovieTrailerDao(DaoSession daoSession) {
        return daoSession.getMovieTrailerDao();
    }

    @Provides
    @Singleton
    MovieReviewDao provideMovieReviewDao(DaoSession daoSession) {
        return daoSession.getMovieReviewDao();
    }

    @Provides
    @Singleton
    EventBus provideEventBus() {
        return EventBus.getDefault();
    }
}
