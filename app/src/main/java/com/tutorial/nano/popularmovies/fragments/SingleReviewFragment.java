package com.tutorial.nano.popularmovies.fragments;


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
import android.widget.TextView;

import com.tutorial.nano.popularmovies.R;
import com.tutorial.nano.popularmovies.data.MoviesContract;

public class SingleReviewFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int REVIEW_LOADER = 0;
    private long entryId;

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
        Bundle arguments = getArguments();
        if(arguments != null) {
            entryId = arguments.getLong("entryId");
        }

        return inflater.inflate(R.layout.fragment_single_review, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(REVIEW_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == REVIEW_LOADER) {
            Uri movieReviewsUri = MoviesContract.ReviewEntry.buildMovieReviewUri(entryId);
            return new CursorLoader(
                    getContext(),
                    movieReviewsUri,
                    REVIEWS_PROJECTION,
                    null,
                    null,
                    null
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int loaderId = loader.getId();

        if (loaderId == REVIEW_LOADER && data.moveToFirst()) {
            TextView authorName = (TextView) getView().findViewById(R.id.review_author_name);
            authorName.setText(data.getString(COL_REVIEW_AUTHOR_INDEX));
            TextView reviewContent = (TextView) getView().findViewById(R.id.review_content);
            reviewContent.setText(data.getString(COL_REVIEW_CONTENT_INDEX));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
