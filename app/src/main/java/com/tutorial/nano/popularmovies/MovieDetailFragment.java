package com.tutorial.nano.popularmovies;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tutorial.nano.popularmovies.data.MoviesContract;

public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MOVIE_DETAILS_LOADER = 1;

    private static final String[] MOVIE_COLS = {
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
            MoviesContract.MovieEntry.COLUMN_TITLE,
            MoviesContract.MovieEntry.COLUMN_POSTER_URL,
            MoviesContract.MovieEntry.COLUMN_PLOT,
            MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE
    };

    static final int COL_ID_INDEX = 0;
    static final int COL_MOVIE_ID_INDEX = 1;
    static final int COL_TITLE_INDEX = 2;
    static final int COL_POSTER_URL_INDEX = 3;
    static final int COL_PLOT_INDEX = 4;
    static final int COL_RELEASE_DATE_INDEX = 5;
    static final int COL_VOTE_AVERAGE_INDEX = 6;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie_detail, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_DETAILS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        long movieEntryId = intent.getExtras().getLong("movieEntryId");

        Uri movieUri = MoviesContract.MovieEntry.buildMovieUri(movieEntryId);

        return new CursorLoader(
                getContext(),
                movieUri,
                MOVIE_COLS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor movie) {
        if (!movie.moveToFirst()) {
            return;
        }

        TextView title = (TextView) getView().findViewById(R.id.movie_detail_title);
        title.setText(movie.getString(COL_TITLE_INDEX));

        String posterUrl = movie.getString(COL_POSTER_URL_INDEX);
        ImageView poster = (ImageView) getView().findViewById(R.id.movie_detail_poster);
        Picasso.with(getContext()).load(posterUrl).into(poster);

        TextView plot = (TextView) getView().findViewById(R.id.movie_detail_plot);
        plot.setText(movie.getString(COL_PLOT_INDEX));

        TextView releaseDate = (TextView) getView().findViewById(R.id.movie_detail_release_date);
        releaseDate.setText(movie.getString(COL_RELEASE_DATE_INDEX));

        TextView voteAverage = (TextView) getView().findViewById(R.id.movie_detail_vote_average);
        voteAverage.setText(Double.toString(movie.getDouble(COL_VOTE_AVERAGE_INDEX)));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
