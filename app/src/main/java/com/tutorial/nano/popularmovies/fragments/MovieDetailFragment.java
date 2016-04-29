package com.tutorial.nano.popularmovies.fragments;


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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tutorial.nano.popularmovies.activities.MovieReviewsActivity;
import com.tutorial.nano.popularmovies.R;
import com.tutorial.nano.popularmovies.adapters.MovieDetailsCursorAdapter;
import com.tutorial.nano.popularmovies.data.MoviesContract;
import com.tutorial.nano.popularmovies.tasks.FetchExtraMovieDetailsTask;

public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int MOVIE_DETAILS_LOADER = 0;
    private static final int TRAILERS_LOADER = 1;
    private boolean fetchedExtraMovieDetails;
    private long movieId;

    private MovieDetailsCursorAdapter mMovieDetailsCursorAdapter;

    private static final String[] MOVIE_PROJECTION = {
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry._ID,
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry.COLUMN_MOVIE_ID,
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry.COLUMN_TITLE,
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry.COLUMN_POSTER_URL,
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry.COLUMN_PLOT,
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
            MoviesContract.MovieEntry.TABLE_NAME + "." + MoviesContract.MovieEntry.COLUMN_VOTE_AVERAGE
    };

    static final int COL_ID_INDEX = 0;
    static final int COL_MOVIE_ID_INDEX = 1;
    static final int COL_TITLE_INDEX = 2;
    static final int COL_POSTER_URL_INDEX = 3;
    static final int COL_PLOT_INDEX = 4;
    static final int COL_RELEASE_DATE_INDEX = 5;
    static final int COL_VOTE_AVERAGE_INDEX = 6;

    private static final String[] TRAILERS_PROJECTION = {
            MoviesContract.TrailerEntry.TABLE_NAME + "." + MoviesContract.TrailerEntry._ID,
            MoviesContract.TrailerEntry.TABLE_NAME + "." + MoviesContract.TrailerEntry.COLUMN_NAME,
            MoviesContract.TrailerEntry.TABLE_NAME + "." + MoviesContract.TrailerEntry.COLUMN_SOURCE
    };

    static final int COL_TRAILER_ID_INDEX = 0;
    public static final int COL_TRAILER_NAME_INDEX = 1;
    static final int COL_TRAILER_SOURCE_INDEX = 2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fetchedExtraMovieDetails = false;
        movieId = getActivity().getIntent().getExtras().getLong("movieId");

        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        ListView trailersList = (ListView) rootView.findViewById(R.id.movie_trailers_list);
        ViewGroup header = (ViewGroup) inflater.inflate(R.layout.movie_details_header, container, false);
        trailersList.addHeaderView(header);

        View reviewsButton = inflater.inflate(R.layout.display_reviews_button, container, false);
        reviewsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reviews = new Intent(getContext(), MovieReviewsActivity.class)
                        .putExtra("movieId", movieId);
                startActivity(reviews);
            }
        });
        trailersList.addFooterView(reviewsButton);

        mMovieDetailsCursorAdapter = new MovieDetailsCursorAdapter(getContext(), null, 0);
        trailersList.setAdapter(mMovieDetailsCursorAdapter);
        trailersList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor item = (Cursor) parent.getItemAtPosition(position);
                if (item == null) {
                    return;
                }
                if (item.getColumnName(COL_TRAILER_SOURCE_INDEX).equals(MoviesContract.TrailerEntry.COLUMN_SOURCE)) {
                    Uri videoUri = Uri.parse(getString(R.string.youtube_video_base_url))
                            .buildUpon()
                            .appendQueryParameter("v", item.getString(COL_TRAILER_SOURCE_INDEX))
                            .build();
                    Intent trailerIntent = new Intent(Intent.ACTION_VIEW, videoUri);
                    startActivity(trailerIntent);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_DETAILS_LOADER, null, this);
        getLoaderManager().initLoader(TRAILERS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        if (id == MOVIE_DETAILS_LOADER) {
            Uri movieDetailsUri = Uri.parse(intent.getExtras().getString("movieEntryUri"));

            return new CursorLoader(
                    getContext(),
                    movieDetailsUri,
                    MOVIE_PROJECTION,
                    null,
                    null,
                    null
            );
        }

        if (id == TRAILERS_LOADER) {
            Uri movieTrailersUri = MoviesContract.TrailerEntry.buildAllMovieTrailersUri(Long.toString(movieId));
            String sortOrder = MoviesContract.TrailerEntry._ID + " ASC";
            return new CursorLoader(
                    getContext(),
                    movieTrailersUri,
                    TRAILERS_PROJECTION,
                    null,
                    null,
                    sortOrder
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int loaderId = loader.getId();

        if (loaderId == MOVIE_DETAILS_LOADER && data.moveToFirst()) {
            TextView title = (TextView) getView().findViewById(R.id.movie_detail_title);
            title.setText(data.getString(COL_TITLE_INDEX));

            String posterUrl = data.getString(COL_POSTER_URL_INDEX);
            ImageView poster = (ImageView) getView().findViewById(R.id.movie_detail_poster);
            Picasso.with(getContext()).load(posterUrl).into(poster);

            TextView plot = (TextView) getView().findViewById(R.id.movie_detail_plot);
            plot.setText(data.getString(COL_PLOT_INDEX));

            TextView releaseDate = (TextView) getView().findViewById(R.id.movie_detail_release_date);
            releaseDate.setText(data.getString(COL_RELEASE_DATE_INDEX));

            TextView voteAverage = (TextView) getView().findViewById(R.id.movie_detail_vote_average);
            voteAverage.setText(Double.toString(data.getDouble(COL_VOTE_AVERAGE_INDEX)) + "/10");
        }

        if (loaderId == TRAILERS_LOADER) {
            if(!data.moveToFirst() && !fetchedExtraMovieDetails) {
                updateDetailsList();
            }
            mMovieDetailsCursorAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieDetailsCursorAdapter.swapCursor(null);
    }

    private void updateDetailsList() {
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return;
        }
        fetchedExtraMovieDetails = true;
        FetchExtraMovieDetailsTask extraMovieDetailsTask = new FetchExtraMovieDetailsTask(getContext());
        extraMovieDetailsTask.execute(Long.toString(movieId));
    }
}
