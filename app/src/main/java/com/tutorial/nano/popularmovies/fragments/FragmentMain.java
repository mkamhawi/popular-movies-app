package com.tutorial.nano.popularmovies.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.tutorial.nano.popularmovies.activities.MovieDetailActivity;
import com.tutorial.nano.popularmovies.R;
import com.tutorial.nano.popularmovies.adapters.MoviesCursorAdapter;
import com.tutorial.nano.popularmovies.data.MoviesContract;
import com.tutorial.nano.popularmovies.tasks.FetchMoviesTask;

public class FragmentMain extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private MoviesCursorAdapter mMoviesCursorAdapter;

    private static final int MOVIES_LOADER = 0;

    private static final String[] MOVIE_COLS = {
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
            MoviesContract.MovieEntry.COLUMN_POSTER_URL
    };

    static final int COL_ID = 0;
    static final int COL_MOVIE_ID = 1;
    public static final int COL_POSTER_URL_ID = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mMoviesCursorAdapter = new MoviesCursorAdapter(getContext(), null, 0);
        GridView moviesGrid = (GridView) rootView.findViewById(R.id.movies_grid);
        moviesGrid.setAdapter(mMoviesCursorAdapter);
        moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor movie = (Cursor) parent.getItemAtPosition(position);
                String movieEntryUri = MoviesContract.MovieEntry.buildMovieUri(movie.getLong(COL_ID)).toString();
                Intent details = new Intent(getActivity(), MovieDetailActivity.class)
                        .putExtra("movieEntryUri", movieEntryUri)
                        .putExtra("movieId", movie.getLong(COL_MOVIE_ID));
                startActivity(details);
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIES_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    public void updateMovieList() {
        String sortOrder = PreferenceManager
                .getDefaultSharedPreferences(getContext())
                .getString(getString(R.string.pref_key_sort_order), getString(R.string.pref_default_value_sort_order));
        FetchMoviesTask moviesTask = new FetchMoviesTask(getActivity());
        moviesTask.execute(sortOrder);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = MoviesContract.MovieEntry._ID + " ASC";

        return new CursorLoader(
                getContext(),
                MoviesContract.MovieEntry.CONTENT_URI,
                MOVIE_COLS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            updateMovieList();
        }
        mMoviesCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMoviesCursorAdapter.swapCursor(null);
    }
}
