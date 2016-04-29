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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.tutorial.nano.popularmovies.data.MoviesContract;

public class MovieReviewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int REVIEWS_LOADER = 0;

    private MovieReviewsCursorAdapter mMovieReviewsCursorAdapter;

    private static final String[] REVIEWS_PROJECTION = {
            MoviesContract.ReviewEntry.TABLE_NAME + "." + MoviesContract.ReviewEntry._ID,
            MoviesContract.ReviewEntry.TABLE_NAME + "." + MoviesContract.ReviewEntry.COLUMN_AUTHOR,
            MoviesContract.ReviewEntry.TABLE_NAME + "." + MoviesContract.ReviewEntry.COLUMN_CONTENT
    };
    static final int COL_REVIEW_ID_INDEX = 0;
    static final int COL_REVIEW_AUTHOR_INDEX = 1;
    static final int COL_REVIEW_CONTENT_INDEX = 2;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_reviews, container, false);

        ListView reviewsList = (ListView) rootView.findViewById(R.id.movie_reviews_list);
        mMovieReviewsCursorAdapter = new MovieReviewsCursorAdapter(getContext(), null, 0);
        reviewsList.setAdapter(mMovieReviewsCursorAdapter);
        TextView emptyView = (TextView) rootView.findViewById(R.id.no_reviews_message);
        reviewsList.setEmptyView(emptyView);
        reviewsList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor item = (Cursor) parent.getItemAtPosition(position);
                if (item == null) {
                    return;
                }
                Intent intent = new Intent(getContext(), SingleReviewActivity.class)
                        .putExtra("reviewEntryId", item.getLong(COL_REVIEW_ID_INDEX));
                startActivity(intent);
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(REVIEWS_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        if (intent == null) {
            return null;
        }

        long movieId = intent.getExtras().getLong("movieId");
        if (id == REVIEWS_LOADER) {
            Uri movieReviewsUri = MoviesContract.ReviewEntry.buildAllMovieReviewsUri(Long.toString(movieId));
            String sortOrder = MoviesContract.ReviewEntry._ID + " ASC";
            return new CursorLoader(
                    getContext(),
                    movieReviewsUri,
                    REVIEWS_PROJECTION,
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

        if (loaderId == REVIEWS_LOADER) {
            mMovieReviewsCursorAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieReviewsCursorAdapter.swapCursor(null);
    }
}
