package com.tutorial.nano.popularmovies.fragments;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.tutorial.nano.popularmovies.PopularMoviesApp;
import com.tutorial.nano.popularmovies.R;
import com.tutorial.nano.popularmovies.adapters.MoviesCursorAdapter;
import com.tutorial.nano.popularmovies.data.MoviesContract;
import com.tutorial.nano.popularmovies.interfaces.MasterActivityCallback;
import com.tutorial.nano.popularmovies.tasks.FetchMoviesTask;

import javax.inject.Inject;

public class FragmentMain extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private MoviesCursorAdapter mMoviesCursorAdapter;

    private static final int MOVIES_LOADER = 0;
    private static final int FAVORITES_LOADER = 1;

    private static final String[] MOVIE_PROJECTION = {
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
            MoviesContract.MovieEntry.COLUMN_POSTER_URL
    };

    static final int COL_ID = 0;
    static final int COL_MOVIE_ID = 1;
    public static final int COL_POSTER_URL_ID = 2;

    private static final String[] FAVORITE_MOVIE_PROJECTION = {
            MoviesContract.FavoriteEntry.TABLE_NAME + "." + MoviesContract.FavoriteEntry._ID,
            MoviesContract.FavoriteEntry.TABLE_NAME + "." + MoviesContract.FavoriteEntry.COLUMN_MOVIE_ID,
            MoviesContract.FavoriteEntry.COLUMN_POSTER_URL
    };

    static final int FAVORITE_COL_ID = 0;
    static final int FAVORITE_COL_MOVIE_ID = 1;
    public static final int FAVORITE_COL_POSTER_URL_ID = 2;
    @Inject protected SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((PopularMoviesApp) getActivity().getApplication()).getAppComponent().inject(this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mMoviesCursorAdapter = new MoviesCursorAdapter(getContext(), null, 0);
        GridView moviesGrid = (GridView) rootView.findViewById(R.id.movies_grid);
        moviesGrid.setAdapter(mMoviesCursorAdapter);
        moviesGrid.setEmptyView(rootView.findViewById(R.id.no_movies_message));
        moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor movie = (Cursor) parent.getItemAtPosition(position);
                ((MasterActivityCallback) getActivity()).onItemSelected(
                        movie.getLong(COL_ID),
                        movie.getLong(COL_MOVIE_ID),
                        FragmentMain.class.getSimpleName()
                );
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        updateMovieList();
        super.onActivityCreated(savedInstanceState);
    }

    public void updateMovieList() {
        String sortOrder = mSharedPreferences
                .getString(getString(R.string.pref_key_sort_order), getString(R.string.pref_default_value_sort_order));
        if(sortOrder.equals(getString(R.string.favorites_category_value))) {
            getLoaderManager().destroyLoader(MOVIES_LOADER);
            getLoaderManager().restartLoader(FAVORITES_LOADER, null, this);
        } else {
            getLoaderManager().destroyLoader(FAVORITES_LOADER);
            getLoaderManager().restartLoader(MOVIES_LOADER, null, this);
            FetchMoviesTask moviesTask = new FetchMoviesTask(getActivity());
            moviesTask.execute(sortOrder);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if(id == MOVIES_LOADER) {
            String sortOrder = MoviesContract.MovieEntry._ID + " ASC";

            return new CursorLoader(
                    getContext(),
                    MoviesContract.MovieEntry.CONTENT_URI,
                    MOVIE_PROJECTION,
                    null,
                    null,
                    sortOrder
            );
        }
        if(id == FAVORITES_LOADER) {
            String sortOrder = MoviesContract.FavoriteEntry._ID + " ASC";

            return new CursorLoader(
                    getContext(),
                    MoviesContract.FavoriteEntry.CONTENT_URI,
                    FAVORITE_MOVIE_PROJECTION,
                    null,
                    null,
                    sortOrder
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMoviesCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesCursorAdapter.swapCursor(null);
    }
}
