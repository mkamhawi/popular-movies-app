package com.tutorial.nano.popularmovies.fragments;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.tutorial.nano.popularmovies.data.FavoriteMovie;
import com.tutorial.nano.popularmovies.data.FavoriteMovieDao;
import com.tutorial.nano.popularmovies.data.MovieDao;
import com.tutorial.nano.popularmovies.data.dtos.MovieDetailsDto;
import com.tutorial.nano.popularmovies.data.models.MovieModel;
import com.tutorial.nano.popularmovies.network.NetworkJobManager;

public class MovieDetailsViewModel extends ViewModel {

    MutableLiveData<MovieModel> movieDetails;
    MutableLiveData<Boolean> isFavorite;

    MovieDao mMovieDao;
    FavoriteMovieDao mFavoriteMovieDao;
    NetworkJobManager mNetworkJobManager;
    long mMovieId;

    public void init(
            long movieId,
            MovieDao movieDao,
            FavoriteMovieDao favoriteMovieDao,
            NetworkJobManager networkJobManager
    ) {
        mMovieId = movieId;
        mMovieDao = movieDao;
        mFavoriteMovieDao = favoriteMovieDao;
        mNetworkJobManager = networkJobManager;
    }

    public MutableLiveData<MovieModel> getMovieDetails() {
        if (movieDetails == null) {
            movieDetails = new MutableLiveData<>();
            mNetworkJobManager.requestMovieDetails(
                    Long.toString(mMovieId),
                    movieDetailsDto -> loadMovieDetails(movieDetailsDto)
            );
        }
        return movieDetails;
    }

    public MutableLiveData<Boolean> isFavorite() {
        if (isFavorite == null) {
            isFavorite = new MutableLiveData<>();
            checkIfFavorite();
        }
        return isFavorite;
    }

    public void setIsFavorite(boolean isFavorite) {
        this.isFavorite.setValue(isFavorite);
        saveIsFavorite(isFavorite);
    }

    private void loadMovieDetails(MovieDetailsDto movieDetailsDto) {

        MovieModel model = new MovieModel();
        model.fill(movieDetailsDto);
        movieDetails.setValue(model);
    }

    private void checkIfFavorite() {

        FavoriteMovie movieEntry = mFavoriteMovieDao.queryBuilder()
                .where(FavoriteMovieDao.Properties.MovieId.eq(mMovieId))
                .unique();

        isFavorite.setValue(movieEntry != null);
    }

    private void saveIsFavorite(boolean isFavorite) {

        if (isFavorite) {
            mFavoriteMovieDao.insertOrReplaceInTx(new FavoriteMovie(mMovieId, mMovieId));
        } else {
            mFavoriteMovieDao.queryBuilder()
                    .where(FavoriteMovieDao.Properties.MovieId.eq(mMovieId))
                    .unique().delete();
        }
    }
}
