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
import android.widget.ProgressBar;

import com.tutorial.nano.popularmovies.PopularMoviesApp;
import com.tutorial.nano.popularmovies.R;
import com.tutorial.nano.popularmovies.adapters.MoviesAdapter;
import com.tutorial.nano.popularmovies.data.FavoriteMovie;
import com.tutorial.nano.popularmovies.data.FavoriteMovieDao;
import com.tutorial.nano.popularmovies.data.Movie;
import com.tutorial.nano.popularmovies.data.MovieDao;
import com.tutorial.nano.popularmovies.interfaces.MasterActivityCallback;
import com.tutorial.nano.popularmovies.network.NetworkJobManager;
import com.tutorial.nano.popularmovies.network.events.MoviesRetrievedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class FragmentMain extends Fragment {
    private MoviesAdapter mMoviesAdapter;
    private String mSortPreference;
    public List<Movie> mMovies;
    private ProgressBar mProgressBar;

    @Inject protected Application mApplication;
    @Inject protected SharedPreferences mSharedPreferences;

    @Inject protected MovieDao mMovieDao;
    @Inject protected EventBus mEventBus;
    @Inject protected NetworkJobManager mNetworkJobManager;

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
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
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

    @Override public void onStart() {
        super.onStart();
        mEventBus.register(this);
    }

    @Override public void onStop() {
        super.onStop();
        mEventBus.unregister(this);
    }

    public void updateMovieList() {
        mSortPreference = mSharedPreferences
                .getString(getString(R.string.pref_key_sort_order), getString(R.string.pref_default_value_sort_order));
        updateMovieList(mSortPreference);
    }

    public void updateMovieList(String sortOrder) {
        mSortPreference = sortOrder;
        if(sortOrder.equals(getString(R.string.favorites_category_value))) {
            new GetFavoriteMoviesFromDbTask().execute();
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            mNetworkJobManager.requestMovies(sortOrder);
        }
    }

    @Subscribe
    public void onMoviesRetrieved(MoviesRetrievedEvent moviesRetrievedEvent) {
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
            mProgressBar.setVisibility(View.GONE);
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
