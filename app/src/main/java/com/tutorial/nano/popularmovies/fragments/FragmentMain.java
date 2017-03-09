package com.tutorial.nano.popularmovies.fragments;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.tutorial.nano.popularmovies.PopularMoviesApp;
import com.tutorial.nano.popularmovies.R;
import com.tutorial.nano.popularmovies.adapters.MoviesAdapter;
import com.tutorial.nano.popularmovies.data.FavoriteMovie;
import com.tutorial.nano.popularmovies.data.FavoriteMovieDao;
import com.tutorial.nano.popularmovies.data.Movie;
import com.tutorial.nano.popularmovies.data.MovieDao;
import com.tutorial.nano.popularmovies.interfaces.AsyncResponseNotification;
import com.tutorial.nano.popularmovies.interfaces.MasterActivityCallback;
import com.tutorial.nano.popularmovies.tasks.FetchMoviesTask;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class FragmentMain extends Fragment implements AsyncResponseNotification {
    private MoviesAdapter mMoviesAdapter;
    private String mSortPreference;
    public List<Movie> mMovies;

    @Inject protected Application mApplication;
    @Inject protected SharedPreferences mSharedPreferences;

    @Inject protected MovieDao mMovieDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((PopularMoviesApp) getActivity().getApplication()).getAppComponent().inject(this);
        setHasOptionsMenu(true);
        mMovies = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mMoviesAdapter = new MoviesAdapter(getContext(), R.id.movies_grid, mMovies);
        GridView moviesGrid = (GridView) rootView.findViewById(R.id.movies_grid);
        moviesGrid.setAdapter(mMoviesAdapter);
        moviesGrid.setEmptyView(rootView.findViewById(R.id.no_movies_message));
        moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = (Movie) parent.getItemAtPosition(position);
                ((MasterActivityCallback) getActivity()).onItemSelected(
                        movie.getId(),
                        movie.getId(),
                        FragmentMain.class.getSimpleName()
                );
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override public void onResume() {
        super.onResume();
        updateMovieList();
    }

    public void updateMovieList() {
        mSortPreference = mSharedPreferences
                .getString(getString(R.string.pref_key_sort_order), getString(R.string.pref_default_value_sort_order));
        if(mSortPreference.equals(getString(R.string.favorites_category_value))) {
            new GetFavoriteMoviesFromDbTask().execute();
        } else {
            new FetchMoviesTask(mApplication, this).execute(mSortPreference);
        }
    }

    @Override
    public void notifyTaskCompleted() {
        new GetMoviesFromDbTask().execute();
    }

    public class GetMoviesFromDbTask extends AsyncTask<Void, Void, List<Movie>> {
        @Override
        protected List<Movie> doInBackground(Void... params) {
            return mMovieDao.queryBuilder()
                    .where(MovieDao.Properties.SortPreferences.eq(mSortPreference))
                    .list();
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            mMovies.clear();
            mMoviesAdapter.clear();
            mMovies.addAll(movies);
            mMoviesAdapter.notifyDataSetChanged();
        }
    }

    public class GetFavoriteMoviesFromDbTask extends AsyncTask<Void, Void, List<Movie>> {
        @Override
        protected List<Movie> doInBackground(Void... params) {
            QueryBuilder<Movie> qb = mMovieDao.queryBuilder();
            qb.join(FavoriteMovie.class, FavoriteMovieDao.Properties.MovieId);
            return qb.orderAsc(MovieDao.Properties.ReleaseDate).list();
        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            mMovies.clear();
            mMoviesAdapter.clear();
            mMovies.addAll(movies);
            mMoviesAdapter.notifyDataSetChanged();
        }
    }
}
